package org.das.service.oparations;

import org.das.model.Account;
import org.das.service.AccountService;
import org.das.service.UserService;
import org.das.utils.ConsoleOperationType;
import org.das.validate.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CloseAccountCommand implements OperationCommand {
    private final UserValidation userValidation;
    private final AccountService accountService;
    private final UserService userService;
    private final Scanner scanner;

    @Autowired
    public CloseAccountCommand(UserValidation userValidation,
                               AccountService accountService,
                               UserService userService,
                               Scanner scanner) {
        this.userValidation = userValidation;
        this.accountService = accountService;
        this.userService = userService;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        System.out.println("Enter account ID to close: ");
        Long accountId = scanner.nextLong();
        Account accountClose = accountService.close(accountId);
        System.out.println("Account with ID=%s fro user id=%s has been closed"
                .formatted(accountClose.getAccountId(), accountClose.getUser().getUserId()));
    }

    @Override
    public ConsoleOperationType getOperationType() {
        return ConsoleOperationType.ACCOUNT_CLOSE;
    }
}
