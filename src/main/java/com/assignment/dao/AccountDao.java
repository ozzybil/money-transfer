package com.assignment.dao;

import com.assignment.db.InMemoryDb;
import com.assignment.model.Account;

import java.util.Collection;

/**
 * Data access object for {@link Account} objects
 */
public class AccountDao {

    private final InMemoryDb<Account> accounts;

    public AccountDao() {
        accounts = new InMemoryDb<>();
    }

    /**
     * Saves given account
     *
     * @param account to be saved
     * @return true if the account saved successfully
     */
    public boolean save(Account account) {
        return accounts.add(account);
    }

    /**
     * Returns the account with specified id
     *
     * @param id to be search
     * @return the account with specified id
     */
    public Account get(String id) {
        return accounts.get(id);
    }

    /**
     * Returns all accounts saved so far
     *
     * @return all accounts
     */
    public Collection<Account> getAll() {
        return accounts.getAll();
    }
}