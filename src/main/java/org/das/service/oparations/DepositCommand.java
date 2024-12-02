package org.das.service.oparations;

import org.das.service.AccountService;
import org.das.utils.ConsoleOperationType;
import org.das.validate.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class DepositCommand implements OperationCommand {
    private final UserValidation userValidation;
    private final AccountService accountService;
    private final Scanner scanner;

    @Autowired
    public DepositCommand(UserValidation userValidation, AccountService accountService, Scanner scanner) {
        this.userValidation = userValidation;
        this.accountService = accountService;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("Enter account ID: ");
        Long accountId = scanner.nextLong();
        System.out.println("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        accountService.deposit(accountId, BigDecimal.valueOf(amount));
        System.out.println("Amount " + amount + " deposited to account ID: " + accountId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_DEPOSIT;
    }
}
