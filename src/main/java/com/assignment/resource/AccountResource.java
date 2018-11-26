package com.assignment.resource;

import com.assignment.dao.AccountDao;
import com.assignment.model.Account;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountResource extends ResourceBase {

    private final AccountDao accountDao;

    public AccountResource(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @GET
    @Path("/all")
    public Response getAll() {

        log.info("Request for all accounts..");

        Collection<Account> result = accountDao.getAll();

        return done(result);
    }

    @GET
    @Path("/id/{id}")
    public Response get(final @PathParam("id") String id) {

        log.info("Request for account with id: " + id);

        Account result = accountDao.get(id);

        return result != null ? ok(result) : error(Response.Status.NOT_FOUND, "No account found with id " + id);
    }

    @POST
    @Path("/save")
    public Response save(@Valid Account account) {

        log.info("Request for save an " + account);

        if (account == null) {
            return error(Response.Status.BAD_REQUEST, "Could not store null account");
        }

        boolean saved = accountDao.save(account);

        if (!saved) {
            return error(Response.Status.BAD_REQUEST, "You cannot save multiple accounts with the same id: " + account.getId());
        }

        return ok(account);
    }
}