package org.das.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
                action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public <R> R executeInTransaction(Function<Session, R> action) {
        Transaction transaction = null;
        try(Session session = sessionFactory.openSession()) {
            transaction = session.getTransaction();
            transaction.begin();
                R res = action.apply(session);
            transaction.commit();
            return res;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }
}
