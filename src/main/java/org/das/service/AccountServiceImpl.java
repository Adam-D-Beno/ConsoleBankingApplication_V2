package org.das.service;

import org.das.dao.AccountDao;
import org.das.model.Account;
import org.das.model.User;
import org.das.utils.AccountProperties;
import org.das.validate.AccountValidation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    private final AccountDao accountDao;
    private final AccountValidation accountValidation;
    private final AccountProperties accountProperties;
    private final SessionFactory sessionFactory;

    @Autowired
    public AccountServiceImpl(AccountDao accountDao,
                              AccountValidation accountValidation,
                              AccountProperties accountProperties,
                              SessionFactory sessionFactory) {
        this.accountDao = accountDao;
        this.accountValidation = accountValidation;
        this.accountProperties = accountProperties;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Account create(User user) {
        Account newAccount = new Account(user);

        if (isFirstAccount(newAccount.getUser())) {
            newAccount.setMoneyAmount(BigDecimal.valueOf(accountProperties.getDefaultAmount()));
        }
        try(Session session = sessionFactory.openSession()) {
            accountDao.save(newAccount, session);
            newAccount.addUser(user);
        }
        return  newAccount;
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
        account.removeUser(account.getUser());
        accountDao.remove(accountId);
        return account;
    }

    private Long getAccountNextId(Account account) {
         return getAllUserAccounts(account.getUser()).stream()
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
        accountValidation.negativeAmount(amount);
        Account fromAccount = getAccount(accountFromId);
        Account toAccount = getAccount(accountToId);
        accountValidation.isSameAccount(fromAccount, toAccount);
        if (isAccountOneUser(fromAccount, toAccount)) {
            withdraw(fromAccount.getAccountId(), amount);
            deposit(toAccount.getAccountId(), amount);
            return;
        }
        BigDecimal amountAfterCommission = calculateAmountAfterCommission(amount);
        withdraw(fromAccount.getAccountId(), amount);
        deposit(toAccount.getAccountId(), amountAfterCommission);
    }

    private boolean isAccountOneUser(Account fromAccount, Account toAccount) {
        return fromAccount.getUser().getUserId().equals(toAccount.getUser().getUserId());
    }

    private BigDecimal calculateAmountAfterCommission(BigDecimal amount) {
        BigDecimal commission = BigDecimal.valueOf(accountProperties.getTransferCommission());
        return amount.multiply(BigDecimal.ONE.subtract(commission)).setScale(2, RoundingMode.HALF_UP);
    }

    private boolean isOnlyAccount(User user) {
        return getAllUserAccounts(user).size() == 0;
    }

    private boolean isFirstAccount(User user) {
        return isOnlyAccount(user);
    }

    private Account getAccount(Long accountId) {
        return accountDao.getAccount(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not exist id=%s".formatted(accountId)));
    }

    public List<Account> getAllUserAccounts(User user) {
        List<Account> accountsByUser = accountDao.findAllAccountsByUserId(user.getUserId())
                .stream()
                .filter(account -> account.getUser().getUserId().equals(user.getUserId()))
                .toList();;
          return accountsByUser;
    }
}


