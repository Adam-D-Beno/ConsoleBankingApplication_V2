package org.das.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Account> accounts;

    public User() {
    }

    public User(String login, List<Account> accounts) {
        this.login = login;
        this.accounts = accounts;
    }

    public Long getUserId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        this.accounts.add(account);
        account.setUser(this);
    }

    public Optional<Account> getAccountById(Long accountId) {
        return accounts.stream().filter(account -> account.getAccountId().equals(accountId)).findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return id.equals(user.id) && login.equals(user.login);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + login.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + id +
                ", login='" + login + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
