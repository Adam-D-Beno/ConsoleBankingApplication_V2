package org.das.service.oparations;

import org.das.model.Account;
import org.das.service.AccountService;
import org.das.utils.ConsoleOperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CreateAccountCommand implements OperationCommand {
    private final AccountService accountService;
    private final Scanner scanner;

    @Autowired
    public CreateAccountCommand(AccountService accountService,

                                Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }


    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CREATE;
    }

    @Override
    public void execute() {
        System.out.println("Enter the user id for which to create an account: ");
        Long userId = scanner.nextLong();
        Account account = accountService.create(userId);
        System.out.println("New account created with ID =%s for user with id=%s"
                .formatted(account.getAccountId(), account.getUser().getUserId()));
    }
}
