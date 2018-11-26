package com.assignment.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * Tests for {@link Account}
 */
public class AccountTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializeToJsonTest() throws Exception {

        // arrange
        Account account = new Account("acc_1", 10);
        String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture("fixtures/account.json"), Account.class));

        // act
        String actual = MAPPER.writeValueAsString(account);

        // assert
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void deserializeFromJsonTest() throws Exception {

        // arrange
        Account expected = new Account("acc_1", 10);

        // act
        Account actual = MAPPER.readValue(fixture("fixtures/account.json"), Account.class);

        // assert
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getBalance()).isEqualTo(expected.getBalance());
    }

    @Test
    public void increaseBalanceTest() throws Exception {

        // arrange
        Account account = new Account("acc_1", 0);

        // act
        account.increaseBalance(125);

        // assert
        assertEquals(125, account.getBalance().intValue());
    }

    @Test
    public void decreaseBalanceTest() throws Exception {

        // arrange
        Account account = new Account("acc_1", 125);

        // act
        account.decreaseBalance(124);

        // assert
        assertEquals(1, account.getBalance().intValue());
    }

    @Test
    public void hasEnoughBalanceToWithdrawTest() throws Exception {

        // arrange
        Account account = new Account("acc_1", 0);

        // act
        boolean result = account.hasEnoughBalanceToWithdraw(1);

        // assert
        assertFalse(result);
    }

    @Test
    public void hasEnoughSpaceToDepositTest() throws Exception {

        // arrange
        Account account = new Account("acc_1", Integer.MAX_VALUE);

        // act
        boolean result = account.hasEnoughSpaceToDeposit(1);

        // assert
        assertFalse(result);
    }

    @Test
    public void toStringTest() throws Exception {

        // arrange
        Account account = new Account("acc_1", 125);

        // act
        String result = account.toString();

        // assert
        assertTrue(result.contains(account.getId()));
        assertTrue(result.contains(String.valueOf(account.getBalance())));
    }
}