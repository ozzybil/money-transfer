package com.assignment.dao;

import com.assignment.db.InMemoryDb;
import com.assignment.model.Transaction;

import java.util.Collection;

/**
 * Data access object for {@link Transaction} objects
 */
public class TransactionDao {

    private final InMemoryDb<Transaction> transactions;

    public TransactionDao() {
        transactions = new InMemoryDb<>();
    }

    /**
     * Saves given transaction
     *
     * @param transaction to be saved
     * @return true if transaction saved successfully
     */
    public boolean save(Transaction transaction) {
        return transactions.add(transaction);
    }

    /**
     * Returns all transactions saved so far
     *
     * @return all transactions
     */
    public Collection<Transaction> getAll() {
        return transactions.getAll();
    }
}
