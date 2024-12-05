package org.das.service;

import org.das.model.Account;

import java.math.BigDecimal;

public interface AccountService {
    Account create(Long userId);
    Account close(Long accountId);
    void deposit(Long accountId, BigDecimal amount);
    void transfer(Long senderId, Long recipientId, BigDecimal amount);
    void withdraw(Long accountId, BigDecimal amount);
}
