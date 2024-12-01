package org.das.dao;

import org.das.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AccountDao {
    private final TransactionHelper transactionHelper;
    private final Map<UUID, Account> accounts;

    @Autowired
    public AccountDao(TransactionHelper transactionHelper, Map<UUID, Account> accounts) {
        this.transactionHelper = transactionHelper;
        this.accounts = accounts;
    }

    public Account save(Account account) {
        return transactionHelper.executeInTransaction(session -> {
            session.persist(account);
            return account;
        });
    }

    public Collection<Account> getAllAccounts() {
       return accounts.values();
    }

    public List<Account> getAccounts() {
        return transactionHelper.executeInTransaction(session -> {
           return session.createQuery("SELECT ac FROM Account ac", Account.class)
                    .list();
        });
    }

    public Optional<Account> getAccount(UUID id) {
        return Optional.ofNullable(transactionHelper.executeInTransaction(session -> {
           return session.get(Account.class, id);
        }));
    }

    //todo if not find account
    public void remove(UUID id) {
        transactionHelper.executeInTransaction(session -> {
            Account account = session.get(Account.class, id);
            session.remove(account);
        });
    }
}
