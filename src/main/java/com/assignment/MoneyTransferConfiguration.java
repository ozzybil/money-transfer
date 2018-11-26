package com.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.jetty.ConnectorFactory;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import io.dropwizard.server.ServerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class MoneyTransferConfiguration extends Configuration {

    @Valid
    @NotNull
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty("jerseyClient")
    public void setJerseyClientConfiguration(JerseyClientConfiguration jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    @JsonIgnore
    public String getTestEndpoint() {
        int port = 8080;
        ServerFactory serverFactory = this.getServerFactory();
        if (serverFactory instanceof DefaultServerFactory) {
            DefaultServerFactory defaultServerFactory = (DefaultServerFactory) serverFactory;
            List<ConnectorFactory> applicationConnectors = defaultServerFactory.getApplicationConnectors();
            ConnectorFactory connectorFactory = applicationConnectors.get(0);
            if (connectorFactory instanceof HttpConnectorFactory) {
                HttpConnectorFactory httpConnectorFactory = (HttpConnectorFactory) connectorFactory;
                port = httpConnectorFactory.getPort();
            }
        }
        return "http://localhost:" + port;
    }
}
