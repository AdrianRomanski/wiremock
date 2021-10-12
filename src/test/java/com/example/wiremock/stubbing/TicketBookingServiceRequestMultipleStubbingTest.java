package com.example.wiremock.stubbing;

import com.example.wiremock.card.CardDetails;
import com.example.wiremock.payment.PaymentProcessorGateway;
import com.example.wiremock.ticket.TicketBookingPaymentRequest;
import com.example.wiremock.ticket.TicketBookingResponse;
import com.example.wiremock.ticket.TicketBookingService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.example.wiremock.ticket.TicketBookingResponse.BookingResponseStatus.SUCCESS;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TicketBookingServiceRequestMultipleStubbingTest {

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
        stubFor(post(urlPathEqualTo("/payments"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"cardNumber\": \"1111-1111-1111\",\n" +
                        "  \"cardExpiryDate\": \"2021-10-11\",\n" +
                        "  \"amount\": 200.0\n" +
                        "}"))
                .willReturn(okJson("{\n" +
                        "  \"paymentId\": \"3333\",\n" +
                        "  \"paymentResponseStatus\": \"SUCCESS\"\n" +
                        "}")));

        stubFor(get(urlPathEqualTo("/fraudCheck/1111-1111-1111"))
            .willReturn(okJson("  {\n" +
                    "    \"blacklisted\": false\n" +
                    "  }")));

        final var ticketBookingPaymentRequest =
                new TicketBookingPaymentRequest("1111", 200.0,
                        new CardDetails("1111-1111-1111",
                                LocalDate.of(2021, 10, 11)));
        ticketBookingPaymentRequest.setFraudAlert(true);

        final var paymentUpdateResponse =
                ticketBookingService.payForBooking(ticketBookingPaymentRequest);

        assertThat(paymentUpdateResponse).isEqualTo(
                new TicketBookingResponse("1111", "3333", SUCCESS));

        verify(getRequestedFor(urlPathEqualTo("/fraudCheck/1111-1111-1111")));

        verify(postRequestedFor(urlPathEqualTo("/payments"))
                .withRequestBody(equalToJson("{\n" +
                        "  \"cardNumber\": \"1111-1111-1111\",\n" +
                        "  \"cardExpiryDate\": \"2021-10-11\",\n" +
                        "  \"amount\": 200.0\n" +
                        "}")
                ));
    }
}
