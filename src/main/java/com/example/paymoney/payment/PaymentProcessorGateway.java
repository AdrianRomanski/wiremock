package com.example.paymoney.payment;


import com.example.paymoney.fraud.FraudCheckResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PaymentProcessorGateway {

    private final PaymentProcessorRestTemplate restTemplate = new PaymentProcessorRestTemplate();
    private String baseUrl;

    public PaymentProcessorGateway() {}

    public void initializeUrl(final String host, final int port) {
        this.baseUrl = "http://" + host + ":" + port;
    }

    public PaymentProcessorResponse makePayment(String creditCardNumber, LocalDate creditCardExpiry, Double amount) {
        final PaymentProcessorResponseRequest request = new PaymentProcessorResponseRequest(creditCardNumber, creditCardExpiry, amount);
        return restTemplate.postForObject(baseUrl + "/payments", request, PaymentProcessorResponse.class);
    }

    public void updatePayment(String bookingId) {
        restTemplate.postForObject(baseUrl + "/update", bookingId, Void.class);
    }

    public FraudCheckResponse fraudCheck(String cardNumber) {
        return restTemplate.getForObject(baseUrl + "/fraudCheck/"+cardNumber, FraudCheckResponse.class);
    }
}
