package com.assignment;

import io.dropwizard.testing.ResourceHelpers;
import org.junit.Test;

/**
 * Dummy test for {@link MoneyTransferApplication}
 */
public class MoneyTransferApplicationTest {

    @Test
    public void main() throws Exception {
        MoneyTransferApplication.main(new String[] { "server", ResourceHelpers.resourceFilePath("test-config.yml")});
    }
}