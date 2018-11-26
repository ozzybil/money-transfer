package com.assignment.service;

import com.assignment.dao.AccountDao;
import com.assignment.dao.TransactionDao;
import com.assignment.model.Account;
import com.assignment.model.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests for {@link TransactionService}
 */
public class TransactionServiceTest {

    private TransactionService transactionService;
    private AccountDao accountDao;

    private Account account1;
    private Account account2;

    private int transactionIdBeforeTest;

    @Before
    public void setUp() throws Exception {

        TransactionDao transactionDao = new TransactionDao();

        transactionService = new TransactionService(transactionDao);
        accountDao = new AccountDao();

        account1 = new Account("acc_1", 100_000);
        account2 = new Account("acc_2", 100_000);

        accountDao.save(account1);
        accountDao.save(account2);

        transactionIdBeforeTest = Transaction.getLastTransactionId();
    }

    @Test
    public void depositTest() throws Exception {

        // act
        Transaction transaction = transactionService.deposit(account1, 10);

        // assert
        Account actualAccount = accountDao.get(account1.getId());

        assertEquals(100_010, actualAccount.getBalance().intValue());
        assertTrue(transaction.getId().endsWith(String.valueOf(transactionIdBeforeTest + 1)));
    }

    @Test
    public void depositMaxiumumTest() throws Exception {

        // act
        Transaction transaction = transactionService.deposit(account1, Integer.MAX_VALUE - account1.getBalance());

        // assert
        Account actualAccount = accountDao.get(account1.getId());

        assertEquals(Integer.MAX_VALUE, actualAccount.getBalance().intValue());
        assertTrue(transaction.getId().endsWith(String.valueOf(transactionIdBeforeTest + 1)));
    }

    @Test(expected = TransactionException.class)
    public void depositMoreThanMaximumTest() throws Exception {

        // act
        transactionService.deposit(account1, Integer.MAX_VALUE - account2.getBalance() + 1);
    }

    @Test
    public void withdrawTest() throws Exception {

        // act
        Transaction transaction = transactionService.withdraw(account2, 10);

        // assert
        Account actualAccount = accountDao.get(account2.getId());

        assertEquals(99_990, actualAccount.getBalance().intValue());
        assertTrue(transaction.getId().endsWith(String.valueOf(transactionIdBeforeTest + 1)));
    }

    @Test
    public void withdrawMaxiumTest() throws Exception {

        // act
        Transaction transaction = transactionService.withdraw(account2, account2.getBalance());

        // assert
        Account actualAccount = accountDao.get(account2.getId());

        assertEquals(0, actualAccount.getBalance().intValue());
        assertTrue(transaction.getId().endsWith(String.valueOf(transactionIdBeforeTest + 1)));
    }

    @Test(expected = TransactionException.class)
    public void withdrawMoreThanAvailableTest() throws Exception {

        // act
        transactionService.withdraw(account1, account1.getBalance() + 1);
    }

    @Test
    public void transferTest() throws Exception {

        // act
        Transaction transaction = transactionService.transfer(account1, account2, 10);

        // assert
        Account actualAccount1 = accountDao.get(account1.getId());
        Account actualAccount2 = accountDao.get(account2.getId());

        assertEquals(99_990, actualAccount1.getBalance().intValue());
        assertEquals(100_010, actualAccount2.getBalance().intValue());
        assertTrue(transaction.getId().endsWith(String.valueOf(transactionIdBeforeTest + 1)));
    }

    @Test(expected = TransactionException.class)
    public void transferSameAccountTest() throws Exception {

        // act
        transactionService.transfer(account1, account1, 10);
    }

    @Test(expected = TransactionException.class)
    public void transferMoreThanAvailableTest() throws Exception {

        // act
        transactionService.transfer(account2, account1, account2.getBalance() + 1);
    }

    @Test(expected = TransactionException.class)
    public void transferMoreThanMaximumTest() throws Exception {

        // arrange
        int initialBalance = account1.getBalance();
        transactionService.deposit(account1, Integer.MAX_VALUE - initialBalance);

        // act
        transactionService.transfer(account1, account2, account1.getBalance() - initialBalance + 1);
    }

    @Test
    public void transferConcurrencyTest() throws Exception {

        // arrange
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        int nbrOfThreads = 20;
        int nbrOfTrials = 1_000;

        // act
        for (int i = 0; i < nbrOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    for (int j = 0; j < nbrOfTrials; j++) {
                        if (j % 2 == 0) {
                            transactionService.transfer(account1, account2, 1);
                        } else {
                            transactionService.transfer(account2, account1, 1);
                        }
                    }
                } catch (TransactionException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Account actualAccount1 = accountDao.get(account1.getId());
        Account actualAccount2 = accountDao.get(account2.getId());

        // assert
        assertEquals(100_000, actualAccount1.getBalance().intValue());
        assertEquals(100_000, actualAccount2.getBalance().intValue());
        assertEquals(nbrOfThreads * nbrOfTrials + transactionIdBeforeTest, Transaction.getLastTransactionId());
    }

    @Test
    public void getAllTransactionsTest() throws Exception {

        // arrange
        transactionService.deposit(account1, 10);
        transactionService.withdraw(account1, 5);
        transactionService.transfer(account1, account2, 3);

        // act
        Collection<Transaction> allTransactions = transactionService.getAllTransactions();

        // assert
        assertEquals(3, allTransactions.size());

        for (Transaction actualTransaction : allTransactions) {
            switch (actualTransaction.getTransactionType()) {
                case DEPOSIT:
                    assertNull(actualTransaction.getFromAccountId());
                    assertEquals(account1.getId(), actualTransaction.getToAccountId());
                    assertEquals(10, actualTransaction.getAmount().intValue());
                    break;
                case WITHDRAW:
                    assertEquals(account1.getId(), actualTransaction.getFromAccountId());
                    assertNull(actualTransaction.getToAccountId());
                    assertEquals(5, actualTransaction.getAmount().intValue());
                    break;
                case TRANSFER:
                    assertEquals(account1.getId(), actualTransaction.getFromAccountId());
                    assertEquals(account2.getId(), actualTransaction.getToAccountId());
                    assertEquals(3, actualTransaction.getAmount().intValue());
                    break;
            }
        }
    }
}