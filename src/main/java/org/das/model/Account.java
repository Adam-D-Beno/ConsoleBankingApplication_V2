package org.das.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID Id;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "money", nullable = false)
    private BigDecimal moneyAmount;

    public Account() {

    }

    public Account(User user) {
        this.user = user;
    }

    public void setMoneyAmount(BigDecimal moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public UUID getId() {
        return Id;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getMoneyAmount() {
        return moneyAmount;
    }

    public void increaseAmount(BigDecimal amount) {
        this.moneyAmount = moneyAmount.add(amount);
    }
    public void decreaseAmount(BigDecimal amount) {
        this.moneyAmount = moneyAmount.subtract(amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;
        return Id.equals(account.Id) && user.equals(account.user);
    }

    @Override
    public int hashCode() {
        int result = Id.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountId=" + Id +
                ", userId=" + user +
                ", moneyAmount=" + moneyAmount +
                '}';
    }
}
