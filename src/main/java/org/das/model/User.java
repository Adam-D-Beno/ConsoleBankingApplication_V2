package org.das.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID userId;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @OneToMany(mappedBy = "userId")
    private List<Account> accounts;

    public User() {
    }

    public User(String login, List<Account> accounts) {
        this.login = login;
        this.accounts = accounts;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
    }

    public Optional<Account> getAccountById(UUID accountId) {
        return accounts.stream().filter(account -> account.getAccountId().equals(accountId)).findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return userId.equals(user.userId) && login.equals(user.login);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + login.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", login='" + login + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
