package com.assignment.resource;

import com.assignment.client.TestClient;
import com.assignment.model.Account;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/***
 * Test resource to perform basic operations
 * When /test endpoint called:
 *      First, two accounts created in the system
 *      Then each transaction type is performed (deposit, transaction, transfer)
 *      Lastly returned all accounts
 */
@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TestResource extends ResourceBase {

    private TestClient testClient;

    public TestResource(TestClient testClient) {
        this.testClient = testClient;
    }

    @GET
    public Response test() {

        log.info("Request to run test scenario");

        // Create two account
        Account account1 = testClient.saveAccount("acc_1", 10);
        Account account2 = testClient.saveAccount("acc_2", 20);

        // deposit 1 box to first account & withdraw 2 from second account
        testClient.deposit(account1, 1);
        testClient.withdraw(account2, 2);

        // transfer 3 from first account to second one
        testClient.transfer(account1, account2, 3);

        List<Account> allAccounts = testClient.getAllAccounts();

        // final result returned to the client must be something like this;
        // [{"id":"acc_2","balance":21},{"id":"acc_1","balance":8}]

        return ok(allAccounts);
    }
}