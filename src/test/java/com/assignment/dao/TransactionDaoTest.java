package com.assignment.dao;

import com.assignment.model.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link TransactionDao}
 */
public class TransactionDaoTest {

    private TransactionDao transactionDao;

    private Transaction transaction1;
    private Transaction transaction2;

    @Before
    public void setUp() throws Exception {

        transactionDao = new TransactionDao();

        transaction1 = Transaction.ofTransfer("acc_1", "acc_2", 10);
        transaction2 = Transaction.ofTransfer("acc_2", "acc_1", 10);
    }

    @Test
    public void saveTest() throws Exception {

        // act
        boolean result = transactionDao.save(transaction1);

        // assert
        assertTrue(result);
    }

    @Test
    public void getAllTest() throws Exception {

        // arrange
        transactionDao.save(transaction1);
        transactionDao.save(transaction2);
        List<Transaction> expectedTransactions = Arrays.asList(transaction1, transaction2);
        List<Transaction> actualTransactions = new ArrayList<>();

        // act
        Collection<Transaction> result = transactionDao.getAll();
        actualTransactions.addAll(result);

        Collections.sort(actualTransactions, (t1, t2) -> t1.getId().compareTo(t2.getId()));

        // assert
        assertEquals(expectedTransactions.size(), actualTransactions.size());

        for (int i = 0; i < expectedTransactions.size(); i++) {
            Transaction expectedTransaction = expectedTransactions.get(i);
            Transaction actualTransaction = actualTransactions.get(i);
            assertEquals(expectedTransaction, actualTransaction);
        }
    }
}