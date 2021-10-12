package com.example.wiremock.stubbing;

import com.example.wiremock.card.CardDetails;
import com.example.wiremock.payment.PaymentProcessorGateway;
import com.example.wiremock.ticket.TicketBookingPaymentRequest;
import com.example.wiremock.ticket.TicketBookingService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TicketBookingServiceGeneralStubbingTest {

    private TicketBookingService ticketBookingService;
    private WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer();
        configureFor("localhost", 8080);
        wireMockServer.start();

        var paymentProcessorGateway = new PaymentProcessorGateway();
        paymentProcessorGateway.initializeUrl("localhost", wireMockServer.port());
        ticketBookingService = new TicketBookingService(paymentProcessorGateway);
    }

    @AfterEach()
    void tearDown(){
        wireMockServer.stop();
    }

    @Test
    void testCase1() {
        stubFor(any((anyUrl())).willReturn(ok()));

        final var ticketBookingPaymentRequest =
                new TicketBookingPaymentRequest("1111", 200.0,
                        new CardDetails("1111-1111-1111", LocalDate.now()));

        final var paymentUpdateResponse =
                ticketBookingService.updatePaymentDetails(ticketBookingPaymentRequest);

        assertThat(paymentUpdateResponse.getStatus()).isEqualTo("SUCCESS");

        verify(1, postRequestedFor(urlEqualTo("/update")));
    }
}
