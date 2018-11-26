package com.assignment.resource;

import com.assignment.dao.AccountDao;
import com.assignment.model.Account;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link AccountResource}
 */
public class AccountResourceTest {

    private static AccountDao accountDao = mock(AccountDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountResource(accountDao))
            .build();

    private String accountId1 = "acc_1";
    private String accountId2 = "acc_2";
    private String invalidAccountId = "invalidAcc";
    private Integer balance = 10;

    private Account account1 = new Account(accountId1, balance);
    private Account account2 = new Account(accountId2, balance);

    @Before
    public void setUp() throws Exception {
        when(accountDao.get(eq(accountId1))).thenReturn(account1);
        when(accountDao.get(eq(invalidAccountId))).thenReturn(null);
    }

    @After
    public void tearDown(){
        reset(accountDao);
    }

    @Test
    public void getAllTest() throws Exception {

        // arrange
        List<Account> actualAccounts = new ArrayList<>();
        List<Account> expectedAccounts = Arrays.asList(account1, account2);
        when(accountDao.getAll()).thenReturn(expectedAccounts);

        // act
        Response response = resources.target("/account/all").request().buildGet().invoke();

        Collection<Account> result = response.readEntity(new GenericType<Collection<Account>>() {});
        actualAccounts.addAll(result);

        Collections.sort(actualAccounts, (a1, a2) -> a1.getId().compareTo(a2.getId()));

        // assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expectedAccounts.size(), actualAccounts.size());

        for (int i = 0; i < expectedAccounts.size(); i++) {
            Account expectedAccount = expectedAccounts.get(i);
            Account actualAccount = actualAccounts.get(i);
            assertEquals(expectedAccount.getId(), actualAccount.getId());
            assertEquals(expectedAccount.getBalance(), actualAccount.getBalance());
        }
    }

    @Test
    public void getTest() throws Exception {

        // act
        Response response = resources.target("/account/id/" + accountId1).request().buildGet().invoke();

        Account account = response.readEntity(Account.class);

        // assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(account1.getId(), account.getId());
        assertEquals(account1.getBalance(), account.getBalance());
    }

    @Test
    public void getInvalidAccountTest() throws Exception {

        // act
        Response response = resources.target("/account/id/" + invalidAccountId).request().buildGet().invoke();

        // assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void saveTest() throws Exception {

        // arrange
        when(accountDao.save(any(Account.class))).thenReturn(true);

        // act
        Response response = postAccount(account1);

        Account newAccount = response.readEntity(Account.class);

        // assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(account1.getId(), newAccount.getId());
        assertEquals(account1.getBalance(), newAccount.getBalance());
    }

    @Test
    public void saveNullAccountTest() throws Exception {

        // act
        Response response = postAccount(null);

        // assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void saveSameAccountIdTest() throws Exception {

        // arrange
        when(accountDao.save(any(Account.class))).thenReturn(false);

        // act
        Response response = postAccount(account1);

        // assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    private Response postAccount(Account account) {

        return resources.target("/account/save")
                .request()
                .buildPost(Entity.entity(account, MediaType.APPLICATION_JSON_TYPE))
                .invoke();
    }
}