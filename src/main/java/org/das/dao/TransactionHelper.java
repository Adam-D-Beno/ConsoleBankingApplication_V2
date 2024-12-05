package org.das.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TransactionHelper {
    private final SessionFactory sessionFactory;

    @Autowired
    public TransactionHelper(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void executeInTransaction(Consumer<Session> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        if (transaction.getStatus().equals(TransactionStatus.ACTIVE)) {
             action.accept(session);
             return;
        }
        try {
            transaction.begin();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.getStatus().equals(TransactionStatus.MARKED_ROLLBACK)) {
                transaction.rollback();
            }
            throw e;
        }
    }

    public <R> R executeInTransaction(Function<Session, R> action) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();

        if (transaction.getStatus().equals(TransactionStatus.ACTIVE)) {
            return action.apply(session);
        }
        try {
            transaction.begin();
            R res = action.apply(session);
            transaction.commit();
            return res;
        } catch (Exception e) {
            if (transaction.getStatus().equals(TransactionStatus.MARKED_ROLLBACK)) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }
}
