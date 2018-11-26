package com.assignment.resource;

import com.assignment.dao.AccountDao;
import com.assignment.model.Account;
import com.assignment.model.Transaction;
import com.assignment.model.TransactionType;
import com.assignment.service.TransactionException;
import com.assignment.service.TransactionService;
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
import static org.mockito.Mockito.*;

/**
 * Tests for {@link TransactionResource}
 */
public class TransactionResourceTest {

    private static TransactionService transactionService = mock(TransactionService.class);
    private static AccountDao accountDao = mock(AccountDao.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TransactionResource(transactionService, accountDao))
            .build();

    private String accountId1 = "acc_1";
    private String accountId2 = "acc_2";
    private String invalidAccountId = "invalidAcc";
    private Integer amount = 10;

    private Transaction depositTransaction = Transaction.ofDeposit(accountId2, amount);
    private Transaction withdrawTransaction = Transaction.ofWithdraw(accountId1, amount);
    private Transaction transferTransaction = Transaction.ofTransfer(accountId1, accountId2, amount);
    private Account account1 = new Account(accountId1, amount);
    private Account account2 = new Account(accountId1, amount);

    @Before
    public void setUp() throws Exception {
        when(accountDao.get(eq(accountId1))).thenReturn(account1);
        when(accountDao.get(eq(accountId2))).thenReturn(account2);
        when(accountDao.get(eq(invalidAccountId))).thenReturn(null);
        when(transactionService.deposit(account2, amount)).thenReturn(depositTransaction);
        when(transactionService.withdraw(account1, amount)).thenReturn(withdrawTransaction);
        when(transactionService.transfer(account1, account2, amount)).thenReturn(transferTransaction);
    }

    @After
    public void tearDown(){
        reset(accountDao);
        reset(transactionService);
    }

    @Test
    public void getAllTest() throws Exception {

        // arrange
        List<Transaction> actualTransactions = new ArrayList<>();
        List<Transaction> expectedTransactions = Arrays.asList(depositTransaction, withdrawTransaction, transferTransaction);
        when(transactionService.getAllTransactions()).thenReturn(expectedTransactions);

        // act
        Response response = resources.target("/transaction/all").request().buildGet().invoke();

        Collection<Transaction> result = response.readEntity(new GenericType<Collection<Transaction>>() {});
        actualTransactions.addAll(result);

        Collections.sort(actualTransactions, (t1, t2) -> t1.getId().compareTo(t2.getId()));

        // assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(expectedTransactions.size(), actualTransactions.size());

        for (int i = 0; i < expectedTransactions.size(); i++) {
            Transaction expectedTransaction = expectedTransactions.get(i);
            Transaction actualTransaction = actualTransactions.get(i);
            assertEquals(expectedTransaction.getTransactionType(), actualTransaction.getTransactionType());
            assertEquals(expectedTransaction.getFromAccountId(), actualTransaction.getFromAccountId());
            assertEquals(expectedTransaction.getToAccountId(), actualTransaction.getToAccountId());
            assertEquals(expectedTransaction.getAmount(), actualTransaction.getAmount());
        }
    }

    @Test
    public void depositTest() throws Exception {

        // arrange
        Transaction transaction = new Transaction(null, accountId2, amount);

        // act
        Response response = postDeposit(transaction);

        Transaction newTransaction = response.readEntity(Transaction.class);

        // assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(TransactionType.DEPOSIT, newTransaction.getTransactionType());
        assertEquals(transaction.getFromAccountId(), newTransaction.getFromAccountId());
        assertEquals(transaction.getToAccountId(), newTransaction.getToAccountId());
        assertEquals(transaction.getAmount(), newTransaction.getAmount());
    }

    @Test
    public void depositNullTransactionTest() throws Exception {

        // act
        Response response = postDeposit(null);

        // assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void depositInvalidAccountTest() throws Exception {

        // arrange
        Transaction transaction = new Transaction(null, invalidAccountId, amount);

        // act
        Response response = postDeposit(transaction);

        // assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void depositNotAvailableSpaceTest() throws Exception {

        // arrange
        Account richAccount = new Account("richAcc", amount);
        Transaction transaction = new Transaction(null, richAccount.getId(), amount);
        when(accountDao.get(eq(richAccount.getId()))).thenReturn(richAccount);
        when(transactionService.deposit(richAccount, amount)).thenThrow(new TransactionException(""));

        // act
        Response response = postDeposit(transaction);

        // assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void withdrawTest() throws Exception {

        // arrange
        Transaction transaction = new Transaction(accountId1, null, amount);

        // act
        Response response = postWithdraw(transaction);

        Transaction newTransaction = response.readEntity(Transaction.class);

        // assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(TransactionType.WITHDRAW, newTransaction.getTransactionType());
        assertEquals(transaction.getFromAccountId(), newTransaction.getFromAccountId());
        assertEquals(transaction.getToAccountId(), newTransaction.getToAccountId());
        assertEquals(transaction.getAmount(), newTransaction.getAmount());
    }

    @Test
    public void withdrawNullTransactionTest() throws Exception {

        // act
        Response response = postWithdraw(null);

        // assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void withdrawInvalidAccountTest() throws Exception {

        // arrange
        Transaction transaction = new Transaction(invalidAccountId, null, amount);

        // act
        Response response = postWithdraw(transaction);

        // assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void withdrawNotAvailableBalanceTest() throws Exception {

        // arrange
        Account poorAccount = new Account("poorAcc", amount);
        Transaction transaction = new Transaction(poorAccount.getId(), null, amount);
        when(accountDao.get(eq(poorAccount.getId()))).thenReturn(poorAccount);
        when(transactionService.withdraw(poorAccount, amount)).thenThrow(new TransactionException(""));

        // act
        Response response = postWithdraw(transaction);

        // assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transferTest() throws Exception {

        // arrange
        Transaction transaction = new Transaction(accountId1, accountId2, amount);

        // act
        Response response = postTransfer(transaction);

        Transaction newTransaction = response.readEntity(Transaction.class);

        // assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(TransactionType.TRANSFER, newTransaction.getTransactionType());
        assertEquals(transaction.getFromAccountId(), newTransaction.getFromAccountId());
        assertEquals(transaction.getToAccountId(), newTransaction.getToAccountId());
        assertEquals(transaction.getAmount(), newTransaction.getAmount());
    }

    @Test
    public void transferNullTransactionTest() throws Exception {

        // act
        Response response = postTransfer(null);

        // assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void transferInvalidAccountTest() throws Exception {

        // arrange
        Transaction transaction1 = new Transaction(invalidAccountId, accountId2, amount);
        Transaction transaction2 = new Transaction(accountId1, invalidAccountId, amount);

        // act
        Response response1 = postTransfer(transaction1);
        Response response2 = postTransfer(transaction2);

        // assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response1.getStatus());
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response2.getStatus());
    }

    @Test
    public void transferNegativeAmountTest() throws Exception {

        // arrange
        Transaction transaction = new Transaction(accountId1, accountId2, -1);

        // act
        Response response = postTransfer(transaction);

        // assert
        assertEquals(Response.Status.Family.CLIENT_ERROR, response.getStatusInfo().getFamily());
    }

    @Test
    public void transferSameAccountTest() throws Exception {

        // arrange
        when(transactionService.transfer(account1, account1, amount)).thenThrow(new TransactionException(""));
        Transaction transaction = new Transaction(accountId1, accountId1, amount);

        // act
        Response response = postTransfer(transaction);

        // assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    private Response postDeposit(Transaction transaction) {

        return resources.target("/transaction/deposit")
                .request()
                .buildPost(Entity.entity(transaction, MediaType.APPLICATION_JSON_TYPE))
                .invoke();
    }

    private Response postWithdraw(Transaction transaction) {

        return resources.target("/transaction/withdraw")
                .request()
                .buildPost(Entity.entity(transaction, MediaType.APPLICATION_JSON_TYPE))
                .invoke();
    }

    private Response postTransfer(Transaction transaction) {

        return resources.target("/transaction/transfer")
                .request()
                .buildPost(Entity.entity(transaction, MediaType.APPLICATION_JSON_TYPE))
                .invoke();
    }
}