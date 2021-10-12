package com.example.paymoney.controller;
import com.example.paymoney.fraud.FraudCheck;
import com.example.paymoney.payment.PaymentProcessorResponse;
import com.example.paymoney.payment.PaymentProcessorResponseRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.example.paymoney.payment.PaymentProcessorResponse.PaymentResponseStatus.FAILED;
import static com.example.paymoney.payment.PaymentProcessorResponse.PaymentResponseStatus.SUCCESS;


@RestController
@RequestMapping("/api")
public class PayController {

    @PostMapping("/update")
    void updatePayment(@RequestBody String bookingId) {
        System.out.println("payment update for boookingid -  " + bookingId);
    }

    @PostMapping("/payments")
    PaymentProcessorResponse makePayment(@RequestBody PaymentProcessorResponseRequest request) {
        PaymentProcessorResponse response;
        if (request.getCardNumber().startsWith("1111")) {
            response = new PaymentProcessorResponse(UUID.randomUUID().toString(),
                    SUCCESS);
        } else {
            response = new PaymentProcessorResponse(UUID.randomUUID().toString(),
                    FAILED);
        }
        return response;
    }

    @GetMapping("/test")
    public FraudCheck test() {
        return new FraudCheck(true);
    }

    @GetMapping("/fraudCheck/{cardNumber}")
    public FraudCheck fraudCheck(@PathVariable String cardNumber) {
        FraudCheck fraudCheck;
        if (cardNumber.startsWith("1111")) {
            fraudCheck = new FraudCheck(false);
        } else {
            fraudCheck = new FraudCheck(true);
        }
        System.out.println("Fraud status - " + fraudCheck);
        return fraudCheck;
    }
}
