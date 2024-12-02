package org.das.dao;

import org.das.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountDao {
    private final TransactionHelper transactionHelper;

    @Autowired
    public AccountDao(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    public Account save(Account account) {
        return transactionHelper.executeInTransaction(session -> {
            session.persist(account);
            return account;
        });
    }

    public Account update(Account account) {
        return transactionHelper.executeInTransaction(session -> {
            session.merge(account);
            return account;
        });
    }

    public List<Account> findAllAccountsByUserId(Long userId) {
        return transactionHelper.executeInTransaction(session -> {
            return session.createQuery(
                    "SELECT ac FROM Account ac WHERE ac.user.id = :userId", Account.class)
                    .setParameter("userId", userId)
                    .list();
        });
    }

    public Optional<Account> getAccount(Long id) {
        return Optional.ofNullable(transactionHelper.executeInTransaction(session -> {
           return session.get(Account.class, id);
        }));
    }

    //todo if not find account
    public void remove(Long id) {
        transactionHelper.executeInTransaction(session -> {
            Account account = session.get(Account.class, id);
            session.remove(account);
        });
    }
}
