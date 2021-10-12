package com.example.paymoney.fraud;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FraudCheckResponse {
    private final boolean blacklisted;

    @JsonCreator
    public FraudCheckResponse(@JsonProperty("blacklisted") final boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }
}
