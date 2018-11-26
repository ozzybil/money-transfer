package com.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;

/**
 * Holds data of an individual account
 */
public class Account extends Entity {

    @Min(0)
    private Integer balance;

    public Account() {
        super();
    }

    public Account(String id, Integer balance) {
        super(id);
        this.balance = balance;
    }

    @JsonProperty
    public Integer getBalance() {
        return balance;
    }

    public void increaseBalance(Integer amount) {
        balance += amount;
    }

    public void decreaseBalance(Integer amount) {
        balance -= amount;
    }

    /**
     * Checks if the account has sufficient balance to withdraw given amount of money
     *
     * @param amount to be withdrawn
     * @return true if account has sufficient balance, otherwise false
     */
    public boolean hasEnoughBalanceToWithdraw(Integer amount) {
        return amount <= balance;
    }

    /**
     * Checks if the account balance would not exceed the upper limit if deposit given amount of money
     *
     * @param amount to be deposit
     * @return true if balance does not exceed the upper limit
     */
    public boolean hasEnoughSpaceToDeposit(Integer amount) {
        return amount <= Integer.MAX_VALUE - balance;
    }

    @Override
    public String toString() {
        return "Account {" + super.toString() + " balance=" + balance + "}";
    }
}