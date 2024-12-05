package org.das.dao;

import org.das.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserDao {

    private final TransactionHelper transactionHelper;

    public UserDao(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    public Optional<User> getUserById(Long id) {
        return transactionHelper.executeInTransaction(session -> {
            return Optional.ofNullable(session.get(User.class, id));
        });
    }

    public Optional<User> getUserByLogin(String login) {
        return transactionHelper.executeInTransaction(session -> {
            return Optional.ofNullable(session.createQuery("SELECT u FROM User u WHERE u.login =:login", User.class)
                    .setParameter("login", login)
                    .getSingleResultOrNull()
          );
        });
    }


    public User saveUser(User user) {
        return transactionHelper.executeInTransaction(session -> {
            session.persist(user);
            return user;
        });
    }

    public Collection<User> getUsers() {
        return findAllUsers();
    }

    public List<User> findAllUsers() {
       return transactionHelper.executeInTransaction(session -> {
            return session.createQuery("SELECT u FROM User u", User.class)
                    .list();
        });
    }

    public boolean userExist(String login) {
        return getUserByLogin(login).isPresent();
    }

    public void update(User user) {
        transactionHelper.executeInTransaction(session -> {
            session.merge(user);
        });
    }
}