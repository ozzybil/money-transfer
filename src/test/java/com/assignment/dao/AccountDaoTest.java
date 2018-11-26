package com.assignment.dao;

import com.assignment.model.Account;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link AccountDao}
 */
public class AccountDaoTest {

    private AccountDao accountDao;

    private Account account1;
    private Account account2;

    @Before
    public void setUp() throws Exception {

        accountDao = new AccountDao();

        account1 = new Account("acc_1", 10);
        account2 = new Account("acc_2", 20);
    }

    @Test
    public void saveTest() throws Exception {

        // act
        boolean result = accountDao.save(account1);

        // assert
        assertTrue(result);
    }

    @Test
    public void getTest() throws Exception {

        // arrange
        accountDao.save(account1);

        // act
        Account actual = accountDao.get(account1.getId());

        // assert
        assertNotNull(actual);
        assertEquals(account1, actual);
    }

    @Test
    public void getAllTest() throws Exception {

        // arrange
        accountDao.save(account1);
        accountDao.save(account2);
        List<Account> expectedAccounts = Arrays.asList(account1, account2);
        List<Account> actualAccounts = new ArrayList<>();

        // act
        Collection<Account> result = accountDao.getAll();
        actualAccounts.addAll(result);

        Collections.sort(actualAccounts, (a1, a2) -> a1.getId().compareTo(a2.getId()));

        // assert
        assertEquals(expectedAccounts.size(), actualAccounts.size());

        for (int i = 0; i < expectedAccounts.size(); i++) {
            Account expectedAccount = expectedAccounts.get(i);
            Account actualAccount = actualAccounts.get(i);
            assertEquals(expectedAccount, actualAccount);
        }
    }
}