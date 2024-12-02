package org.das.service.oparations;

import org.das.service.AccountService;
import org.das.utils.ConsoleOperationType;
import org.das.validate.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class TransferCommand implements OperationCommand {
    private final UserValidation userValidation;
    private final AccountService accountService;
    private final Scanner scanner;

    @Autowired
    public TransferCommand(UserValidation userValidation, AccountService accountService, Scanner scanner) {
        this.userValidation = userValidation;
        this.accountService = accountService;
        this.scanner = scanner;
    }

    //todo use the try -catch block to handle exceptions
    @Override
    public void execute() {
        System.out.println("Enter source account ID: ");
        Long accountFromId = scanner.nextLong();;
        System.out.println("Enter target account ID: ");
        Long accountToId = scanner.nextLong();;
        System.out.println("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        accountService.transfer(accountFromId, accountToId, BigDecimal.valueOf(amount));
        System.out.println(" Amount " + amount + " transferred from account ID "
                + accountFromId + " to account ID " + accountToId);
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_TRANSFER;
    }
}
