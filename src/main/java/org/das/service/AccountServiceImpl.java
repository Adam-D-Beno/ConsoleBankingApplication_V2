package org.das.service;

import org.das.dao.AccountDao;
import org.das.dao.UserDao;
import org.das.model.Account;
import org.das.model.User;
import org.das.utils.AccountProperties;
import org.das.validate.AccountValidation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountDao accountDao;
    private final AccountValidation accountValidation;
    private final AccountProperties accountProperties;
    private final UserDao userDao;
    private final SessionFactory sessionFactory;


    @Autowired
    public AccountServiceImpl(AccountDao accountDao,
                              AccountValidation accountValidation,
                              AccountProperties accountProperties,
                              UserDao userDao, SessionFactory sessionFactory
    ) {
        this.accountDao = accountDao;
        this.accountValidation = accountValidation;
        this.accountProperties = accountProperties;
        this.userDao = userDao;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Account create(Long userId) {
        User user = userDao.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No such user with id%s"
                        .formatted(userId)));
        Account newAccount = new Account(user);
        if (isFirstAccount(newAccount.getUser())) {
            newAccount.setMoneyAmount(BigDecimal.valueOf(accountProperties.getDefaultAmount()));
        }
        accountDao.save(newAccount);
        user.addAccount(newAccount);
        userDao.update(user);
        return newAccount;
    }

    @Override
    public Account close(Long accountId) {
        Account account = getAccount(accountId);
        if (isOnlyAccount(account.getUser())) {
            throw new IllegalArgumentException(("Account with id=%s cant delete, " +
                    "because user have only one account").formatted(accountId));
        }
        if (account.getMoneyAmount().doubleValue() >= 1) {
            var accountNextId = getAccountNextId(account);
            transfer(account.getAccountId(), accountNextId, account.getMoneyAmount());
        }
        accountDao.remove(accountId);
        account.removeUser(account.getUser()); // todo need i remove user from account?
        return account;
    }

    private Long getAccountNextId(Account account) {
        return account.getUser().getAccounts().stream()
                .map(Account::getAccountId)
                .filter(id -> !id.equals(account.getAccountId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such account id=%s to transfer for user id=%s"
                        .formatted(account.getAccountId(), account.getUser().getUserId())));
    }

    @Override
    public void deposit(Long accountId, BigDecimal amount) {
        accountValidation.negativeAmount(amount);
        Account account = getAccount(accountId);
        account.increaseAmount(amount);
        accountDao.update(account);
    }

    @Override
    public void withdraw(Long accountId, BigDecimal amount) {
        accountValidation.negativeAmount(amount);
        Account account = getAccount(accountId);
        accountValidation.negativeBalance(account, amount);
        account.decreaseAmount(amount);
        accountDao.update(account);
    }

    @Override
    public void transfer(Long accountFromId, Long accountToId, BigDecimal amount) {
        Transaction transaction = null;
        try(Session session = sessionFactory.getCurrentSession()) {
            transaction = session.getTransaction();
            transaction.begin();
                Account fromAccount = getAccount(accountFromId);
                accountValidation.negativeAmount(amount);
                Account toAccount = getAccount(accountToId);
                accountValidation.isSameAccount(fromAccount, toAccount);
                BigDecimal amountAfterCommission = calculateAmountAfterCommission(amount);
                if (isAccountOneUser(fromAccount, toAccount)) {
                    fromAccount.decreaseAmount(amount);
                    toAccount.increaseAmount(amount);
                } else {
                    fromAccount.decreaseAmount(amount);
                    toAccount.increaseAmount(amountAfterCommission);
                }
            transaction.commit();
        } catch (Exception e) {
            if (transaction!= null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    private boolean isAccountOneUser(Account fromAccount, Account toAccount) {
        return fromAccount.getUser().getUserId().equals(toAccount.getUser().getUserId());
    }

    private BigDecimal calculateAmountAfterCommission(BigDecimal amount) {
        BigDecimal commission = BigDecimal.valueOf(accountProperties.getTransferCommission());
        return amount.multiply(BigDecimal.ONE.subtract(commission)).setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isOnlyAccount(User user) {
        return user.getAccounts().size() == 0;
    }

    private boolean isFirstAccount(User user) {
        return isOnlyAccount(user);
    }

    private Account getAccount(Long accountId) {
        return accountDao.getAccount(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not exist id=%s".formatted(accountId)));
    }
}


