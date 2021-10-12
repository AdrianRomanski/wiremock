package com.example.wiremock.fraud;

public class FraudCheck {
    private final boolean blacklisted;
    //more fraud flags to go here..

    public FraudCheck(final boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    @Override
    public String toString() {
        return "FraudCheck{" +
                "blacklisted=" + blacklisted +
                '}';
    }
}
