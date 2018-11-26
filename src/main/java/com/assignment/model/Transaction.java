package com.assignment.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds data of a transaction
 */
public class Transaction extends Entity {

    // Transaction id must be incremented on every transaction creation
    // It's thread safe thanks to AtomicInteger
    private static AtomicInteger transactionId = new AtomicInteger(0);

    private TransactionType transactionType;
    private String fromAccountId;
    private String toAccountId;
    @Min(1)
    private Integer amount;

    /**
     * Private constructor
     * Used by static factory methods. Increments global transaction id
     *
     * @param transactionType transaction type
     * @param fromAccountId source account of transaction (can be null for deposit transaction)
     * @param toAccountId destination account of transaction (can be null for withdraw transaction)
     * @param amount to deposit, withdraw or transfer
     */
    private Transaction(TransactionType transactionType,
                       String fromAccountId,
                       String toAccountId,
                       Integer amount) {

        this(fromAccountId, toAccountId, amount);
        this.transactionType = transactionType;
        this.setId("trx_" + transactionId.incrementAndGet());
    }

    /**
     * Public constructor. Mostly used in test classes or test client for put actions
     *
     * @param fromAccountId source account of transaction (can be null for deposit transaction)
     * @param toAccountId destination account of transaction (can be null for withdraw transaction)
     * @param amount to deposit, withdraw or transfer
     */
    public Transaction(String fromAccountId,
                       String toAccountId,
                       Integer amount) {

        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public Transaction() {

    }

    /**
     * Factory method to create a deposit transaction
     * Given account corresponds to toAccountId of transaction object
     *
     * @param accountId account to deposit
     * @param amount to be deposit
     * @return newly created DEPOSIT transaction
     */
    public static Transaction ofDeposit(String accountId, Integer amount) {
        return new Transaction(TransactionType.DEPOSIT, null, accountId, amount);
    }

    /**
     * Factory method to create a withdraw transaction
     * Given account corresponds to fromAccountId of transaction object
     *
     * @param accountId account to withdraw
     * @param amount to be withdrawn
     * @return newly created WITHDRAW transaction
     */
    public static Transaction ofWithdraw(String accountId, Integer amount) {
        return new Transaction(TransactionType.WITHDRAW, accountId, null, amount);
    }

    /**
     * Factory method to create a transfer transaction
     *
     * @param fromAccountId source account of transfer
     * @param toAccountId destination account of transfer
     * @param amount to be transfer
     * @return newly created TRANSFER transaction
     */
    public static Transaction ofTransfer(String fromAccountId, String toAccountId, Integer amount) {
        return new Transaction(TransactionType.TRANSFER, fromAccountId, toAccountId, amount);
    }

    /**
     * Read-only getter for latest transaction id
     *
     * @return latest transaction id
     */
    public static int getLastTransactionId() {
        return transactionId.get();
    }

    @JsonGetter
    public TransactionType getTransactionType() {
        return transactionType;
    }

    @JsonProperty
    public String getFromAccountId() {
        return fromAccountId;
    }

    @JsonProperty
    public String getToAccountId() {
        return toAccountId;
    }

    @JsonProperty
    public Integer getAmount() {
        return amount;
    }
}
