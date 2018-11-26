package com.assignment.service;

import com.assignment.dao.TransactionDao;
import com.assignment.model.Account;
import com.assignment.model.Transaction;

import java.util.Collection;

/**
 * Service layer to perform all kinds of transactions
 */
public class TransactionService {

    private final TransactionDao transactionDao;

    public TransactionService(TransactionDao transactionDao) {
        this.transactionDao = transactionDao;
    }

    /**
     * First validates the request and then performs deposit operation on given account
     * Guards balance of the account from concurrent modifications via accounts implicit monitor
     *
     * @param account object to perform deposit
     * @param amount to be deposit
     * @return newly created transaction
     * @throws TransactionException deposit action is not valid
     */
    public Transaction deposit(Account account, Integer amount) throws TransactionException {

        synchronized (account) {

            // validation
            if (!account.hasEnoughSpaceToDeposit(amount)) {
                throw new TransactionException("Account balance cannot be greater than " + Integer.MAX_VALUE);
            }

            // perform deposit
            account.increaseBalance(amount);
        }

        // create a DEPOSIT transaction
        Transaction transaction = Transaction.ofDeposit(account.getId(), amount);

        // save the transaction
        transactionDao.save(transaction);

        return transaction;
    }

    /**
     * First validates the request then performs withdraw operation on given account
     * Guards balance of the account from concurrent modifications via accounts implicit monitor
     *
     * @param account object to perform withdrawal
     * @param amount to be withdrawn
     * @return newly created transaction
     * @throws TransactionException withdraw action is not valid
     */
    public Transaction withdraw(Account account, Integer amount) throws TransactionException {

        synchronized (account) {

            // validation
            if (!account.hasEnoughBalanceToWithdraw(amount)) {
                throw new TransactionException("Account balance cannot be less than zero");
            }

            // perform withdrawal
            account.decreaseBalance(amount);
        }

        // create a WITHDRAW transaction
        Transaction transaction = Transaction.ofWithdraw(account.getId(), amount);

        // save the transaction
        transactionDao.save(transaction);

        return transaction;
    }

    /**
     * First makes validations on both account then performs transfer operation
     * Guards balance of the accounts from concurrent modifications via their implicit monitors
     *
     * @param from object to perform deposit
     * @param to object to perform deposit
     * @param amount to be deposit
     * @return newly created transaction
     * @throws TransactionException deposit action is not valid
     */
    public Transaction transfer(Account from, Account to, Integer amount) throws TransactionException {

        // Two account id cannot be same since its checked during insertion to account store
        Object firstLock = from.getId().compareTo(to.getId()) < 0 ? from : to;
        Object secondLock = from.getId().compareTo(to.getId()) < 0 ? to : from;

        // Lock ordering must be deterministic to prevent deadlock situations
        synchronized (firstLock) {

            synchronized (secondLock) {

                // validations
                if (from.getId().equals(to.getId())) {
                    throw new TransactionException("Participants of a transfer must be different");
                }

                if (!from.hasEnoughBalanceToWithdraw(amount)) {
                    throw new TransactionException("Account balance cannot be less than zero");
                }

                if (!to.hasEnoughSpaceToDeposit(amount)) {
                    throw new TransactionException("Account balance cannot be greater than " + Integer.MAX_VALUE);
                }

                // perform transfer
                from.decreaseBalance(amount);
                to.increaseBalance(amount);
            }
        }

        // create a TRANSFER transaction
        Transaction transaction = Transaction.ofTransfer(from.getId(), to.getId(), amount);

        // save the transaction
        transactionDao.save(transaction);

        return transaction;
    }

    /**
     * Returns all transactions happened
     *
     * @return all transactions
     */
    public Collection<Transaction> getAllTransactions() {
        return transactionDao.getAll();
    }
}