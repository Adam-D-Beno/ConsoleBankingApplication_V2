package org.das.service;

import org.das.model.Account;
import org.das.model.User;

import java.math.BigDecimal;

public interface AccountService {
    Account create(User user);
    Account close(Long accountId);
    void deposit(Long accountId, BigDecimal amount);
    void transfer(Long senderId, Long recipientId, BigDecimal amount);
    void withdraw(Long accountId, BigDecimal amount);
}
