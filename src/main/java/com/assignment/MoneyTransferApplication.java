package com.assignment;

import com.assignment.client.TestClient;
import com.assignment.dao.AccountDao;
import com.assignment.dao.TransactionDao;
import com.assignment.resource.AccountResource;
import com.assignment.resource.TestResource;
import com.assignment.resource.TransactionResource;
import com.assignment.service.TransactionService;
import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;

/**
 * Main class to run
 */
public class MoneyTransferApplication extends Application<MoneyTransferConfiguration> {

    /**
     * Main point of execution
     *
     * @param args contain "server" and config file
     * @throws Exception if something goes wrong
     */
    public static void main(final String[] args) throws Exception {
        new MoneyTransferApplication().run(args);
    }

    @Override
    public String getName() {
        return "MoneyTransfer";
    }

    @Override
    public void initialize(final Bootstrap<MoneyTransferConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final MoneyTransferConfiguration config,
                    final Environment environment) {

        // dao
        AccountDao accountDao = new AccountDao();
        TransactionDao transactionDao = new TransactionDao();

        // service
        TransactionService transactionService = new TransactionService(transactionDao);

        // resource
        AccountResource accountResource = new AccountResource(accountDao);
        TransactionResource transactionResource = new TransactionResource(transactionService, accountDao);

        // test client
        Client client = new JerseyClientBuilder(environment).using(config.getJerseyClientConfiguration()).build(getName());
        TestClient testClient = new TestClient(client, config.getTestEndpoint());
        TestResource testResource = new TestResource(testClient);

        // register
        environment.jersey().register(accountResource);
        environment.jersey().register(transactionResource);
        environment.jersey().register(testResource);
        environment.healthChecks().register("template", new HealthCheck() { // Dummy health check
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
    }
}