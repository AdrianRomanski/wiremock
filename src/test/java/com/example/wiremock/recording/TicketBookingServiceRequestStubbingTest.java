package com.example.wiremock.recording;

import com.example.wiremock.payment.PaymentProcessorGateway;
import com.example.wiremock.ticket.TicketBookingService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TicketBookingServiceRequestStubbingTest {

    private TicketBookingService tbs;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        wireMockServer.startRecording("http://localhost:9090");

        PaymentProcessorGateway paymentProcessorGateway = new PaymentProcessorGateway();
        paymentProcessorGateway.initializeUrl("localhost", wireMockServer.port());

        tbs = new TicketBookingService(paymentProcessorGateway);
    }

    @Test
    void testCase1() {

        stubFor(get((urlEqualTo("localhost:9090/api/mytest"))).willReturn(ok()));
    }


    @AfterEach
    void tearDown() {
        wireMockServer.stopRecording();
        wireMockServer.stop();
    }

}
