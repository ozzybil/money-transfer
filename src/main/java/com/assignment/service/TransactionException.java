package com.assignment.service;

/***
 * Checked Transaction exception to be thrown for invalid transaction requests
 */
public class TransactionException extends Exception {

    public TransactionException(String message) {
        super(message);
    }
}