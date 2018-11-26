package com.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/***
 * Enumeration for {@link Transaction} types
 */
public enum TransactionType {

    @JsonProperty("deposit") DEPOSIT,
    @JsonProperty("withdraw") WITHDRAW,
    @JsonProperty("transfer") TRANSFER;
}
