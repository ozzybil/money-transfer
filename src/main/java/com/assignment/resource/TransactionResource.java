package com.assignment.resource;

import com.assignment.dao.AccountDao;
import com.assignment.model.Account;
import com.assignment.model.Transaction;
import com.assignment.service.TransactionException;
import com.assignment.service.TransactionService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Contains all endpoints for transaction based calls
 */
@Path("/transaction")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TransactionResource extends ResourceBase {

    private final TransactionService transactionService;
    private final AccountDao accountDao;

    public TransactionResource(TransactionService transactionService, AccountDao accountDao) {
        this.transactionService = transactionService;
        this.accountDao = accountDao;
    }

    @GET
    @Path("/all")
    public Response getAll() {

        log.info("Request for all transactions..");

        Collection<Transaction> result = transactionService.getAllTransactions();

        return done(result);
    }

    @POST
    @Path("/deposit")
    public Response deposit(@Valid Transaction transaction) {

        if (transaction == null) {
            return error(Response.Status.BAD_REQUEST, "Null transaction!");
        }

        String accountId = transaction.getToAccountId(); // deposit TO
        Integer amount = transaction.getAmount();

        log.info("Request for deposit " + amount + " to " + accountId);

        Account account = accountDao.get(accountId);

        if (account == null) {
            return error(Response.Status.NOT_FOUND, "No account found with id " + accountId);
        }

        try {

            transaction = transactionService.deposit(account, amount);

            return ok(transaction);

        } catch (TransactionException e) {
            return error(Response.Status.BAD_REQUEST, e.getMessage());
        }
    }

    @POST
    @Path("/withdraw")
    public Response withdraw(@Valid Transaction transaction) {

        if (transaction == null) {
            return error(Response.Status.BAD_REQUEST, "Null transaction!");
        }

        String accountId = transaction.getFromAccountId(); // Withdraw FROM
        Integer amount = transaction.getAmount();

        log.info("Request for withdraw " + amount + " of " + accountId);

        Account account = accountDao.get(accountId);

        if (account == null) {
            return error(Response.Status.NOT_FOUND, "No account found with id " + accountId);
        }

        try {

            transaction = transactionService.withdraw(account, amount);

            return ok(transaction);

        } catch (TransactionException e) {
            return error(Response.Status.BAD_REQUEST, e.getMessage());
        }
    }

    @POST
    @Path("/transfer")
    public Response transfer(@Valid Transaction transaction) {

        if (transaction == null) {
            return error(Response.Status.BAD_REQUEST, "Null transaction!");
        }

        String fromAccountId = transaction.getFromAccountId();
        String toAccountId = transaction.getToAccountId();
        Integer amount = transaction.getAmount();

        log.info("Request for transfer from " + fromAccountId + " to " + toAccountId + " amount of " + amount);

        Account from = accountDao.get(fromAccountId);

        if (from == null) {
            return error(Response.Status.NOT_FOUND, "No account found with id " + fromAccountId);
        }

        Account to = accountDao.get(toAccountId);

        if (to == null) {
            return error(Response.Status.NOT_FOUND, "No account found with id " + toAccountId);
        }

        try {

            transaction = transactionService.transfer(from, to, amount);

            return ok(transaction);

        } catch (TransactionException e) {
            return error(Response.Status.BAD_REQUEST, e.getMessage());
        }
    }
}