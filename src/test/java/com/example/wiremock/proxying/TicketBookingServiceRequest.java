package com.example.wiremock.proxying;


import com.example.paymoney.card.CardDetails;
import com.example.paymoney.payment.PaymentProcessorGateway;
import com.example.paymoney.ticket.TicketBookingPaymentRequest;
import com.example.paymoney.ticket.TicketBookingResponse;
import com.example.paymoney.ticket.TicketBookingService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;


import static com.example.paymoney.ticket.TicketBookingResponse.BookingResponseStatus.SUCCESS;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TicketBookingServiceRequest {
    private TicketBookingService tbs;

    private WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer();
        configureFor("localhost", 8080);
        wireMockServer.start();

        PaymentProcessorGateway paymentProcessorGateway = new PaymentProcessorGateway();
        paymentProcessorGateway.initializeUrl("localhost", wireMockServer.port());

        tbs = new TicketBookingService(paymentProcessorGateway);

        stubFor(any(anyUrl()).willReturn(aResponse().proxiedFrom("http://localhost:8082/api")));
    }

    @Test
    void testCase1() {

        //when
        TicketBookingPaymentRequest tbpr = new TicketBookingPaymentRequest("1111",
                200.0,
                new CardDetails("1111-1111-1111-1111", LocalDate.of(2020, 8, 3)));

        TicketBookingResponse response = tbs.payForBooking(tbpr);

        //then
        assertThat(response.getBookingResponseStatus()).isEqualTo(SUCCESS);

        //verify
        verify(postRequestedFor(urlPathEqualTo("/payments")).withRequestBody(
                equalToJson("{\n" +
                        "\"cardNumber\" : \"1111-1111-1111-1111\",\n" +
                        "\"cardExpiryDate\" : \"2020-08-03\",\n" +
                        "\"amount\" : 200.0\n" +
                        "}")
        ));


    }

    @Test
    void testCase2() {

        stubFor(get(urlPathEqualTo("/fraudCheck/1111-1111-1111-1111"))
                .willReturn(okJson("{" +
                        "  \"blacklisted\": \"false\"" +
                        "}")));
        //when
        TicketBookingPaymentRequest tbpr = new TicketBookingPaymentRequest("1111",
                200.0,
                new CardDetails("1111-1111-1111-1111", LocalDate.of(2020, 8, 3)));
        tbpr.setFraudAlert(true);
        TicketBookingResponse response = tbs.payForBooking(tbpr);

        //then
        assertThat(response.getBookingResponseStatus()).isEqualTo(SUCCESS);

        //verify
        verify(postRequestedFor(urlPathEqualTo("/payments")).withRequestBody(
                equalToJson("{\n" +
                        "\"cardNumber\" : \"1111-1111-1111-1111\",\n" +
                        "\"cardExpiryDate\" : \"2020-08-03\",\n" +
                        "\"amount\" : 200.0\n" +
                        "}")
        ));


    }
    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }
}

