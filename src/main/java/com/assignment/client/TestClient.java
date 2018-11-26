package com.assignment.client;

import com.assignment.model.Account;
import com.assignment.model.Transaction;
import com.assignment.resource.TestResource;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Used by {@link TestResource}
 *
 * Calls various end-points via httpClient
 */
public class TestClient {

    private final Client httpClient;
    private final String endPoint;

    public TestClient(Client httpClient, String endPoint) {
        this.httpClient = httpClient;
        this.endPoint = endPoint;
    }

    public Account saveAccount(String id, Integer balance) {
        Account account = new Account(id, balance);
        return httpClient.target(endPoint)
                .path("account/save")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(account, MediaType.APPLICATION_JSON), Account.class);
    }

    public Transaction deposit(Account account, Integer amount) {
        Transaction transaction = new Transaction(null, account.getId(), amount);
        return httpClient.target(endPoint)
                .path("transaction/deposit")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(transaction, MediaType.APPLICATION_JSON), Transaction.class);
    }

    public Transaction withdraw(Account account, Integer amount) {
        Transaction transaction = new Transaction(account.getId(), null, amount);
        return httpClient.target(endPoint)
                .path("transaction/withdraw")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(transaction, MediaType.APPLICATION_JSON), Transaction.class);
    }

    public Transaction transfer(Account from, Account to, Integer amount) {
        Transaction transaction = new Transaction(from.getId(), to.getId(), amount);
        return httpClient.target(endPoint)
                .path("transaction/transfer")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(transaction, MediaType.APPLICATION_JSON), Transaction.class);
    }

    public List<Account> getAllAccounts() {
        return httpClient.target(endPoint)
                .path("account/all")
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Account>>() {
                });
    }
}