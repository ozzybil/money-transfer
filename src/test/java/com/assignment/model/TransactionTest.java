package com.assignment.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Tests for {@link Transaction}
 */
public class TransactionTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    private String accountId1;
    private String accountId2;
    private Integer amount;
    private int transactionIdBeforeTest;

    @Before
    public void setUp() throws Exception {

        accountId1 = "acc_1";
        accountId2 = "acc_2";
        amount = 1;
        transactionIdBeforeTest = Transaction.getLastTransactionId();
    }

    @Test
    public void serializeToJsonTest() throws Exception {

        // arrange
        Transaction transaction = new Transaction("acc_1", "acc_2", 10);
        String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture("fixtures/transaction.json"), Transaction.class));

        // act
        String actual = MAPPER.writeValueAsString(transaction);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void deserializeFromJsonTest() throws Exception {

        // arrange
        Transaction expected = new Transaction("acc_1", "acc_2", 10);

        // act
        Transaction actual = MAPPER.readValue(fixture("fixtures/transaction.json"), Transaction.class);

        // assert
        assertThat(actual.getFromAccountId()).isEqualTo(expected.getFromAccountId());
        assertThat(actual.getToAccountId()).isEqualTo(expected.getToAccountId());
        assertThat(actual.getAmount()).isEqualTo(expected.getAmount());
    }

    @Test
    public void ofDepositTest() throws Exception {

        // act
        Transaction result = Transaction.ofDeposit(accountId1, amount);

        // assert
        assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
        assertTrue(result.getId().contains(String.valueOf(transactionIdBeforeTest + 1)));
        assertNull(result.getFromAccountId());
        assertEquals(accountId1, result.getToAccountId());
        assertEquals(amount, result.getAmount());
    }

    @Test
    public void ofWithdrawTest() throws Exception {

        // act
        Transaction result = Transaction.ofWithdraw(accountId1, amount);

        // assert
        assertEquals(TransactionType.WITHDRAW, result.getTransactionType());
        assertTrue(result.getId().contains(String.valueOf(transactionIdBeforeTest + 1)));
        assertEquals(accountId1, result.getFromAccountId());
        assertNull(result.getToAccountId());
        assertEquals(amount, result.getAmount());
    }

    @Test
    public void ofTransferTest() throws Exception {

        // act
        Transaction result = Transaction.ofTransfer(accountId1, accountId2, amount);

        // assert
        assertEquals(TransactionType.TRANSFER, result.getTransactionType());
        assertTrue(result.getId().contains(String.valueOf(transactionIdBeforeTest + 1)));
        assertEquals(accountId1, result.getFromAccountId());
        assertEquals(accountId2, result.getToAccountId());
        assertEquals(amount, result.getAmount());
    }

    @Test
    public void getLastTransactionIdTest() throws Exception {

        // arrange
        Transaction.ofTransfer(accountId1, accountId2, amount);
        Transaction.ofTransfer(accountId2, accountId1, amount);

        // act
        int result = Transaction.getLastTransactionId();

        // assert
        assertEquals(transactionIdBeforeTest + 2, result);
    }

    @Test
    public void getLastTransactionIdWithPublicConstructorTest() throws Exception {

        // arrange
        new Transaction(accountId1, accountId2, amount);

        // act
        int result = Transaction.getLastTransactionId();

        // assert
        assertEquals(transactionIdBeforeTest, result);
    }
}