package com.example.wiremock.stubbing;

import com.example.paymoney.card.CardDetails;
import com.example.paymoney.payment.PaymentProcessorGateway;
import com.example.paymoney.ticket.TicketBookingPaymentRequest;
import com.example.paymoney.ticket.TicketBookingService;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Random;
import java.util.stream.IntStream;

import static com.example.paymoney.ticket.TicketBookingResponse.BookingResponseStatus.SUCCESS;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;

public class TicketBookingServiceRequestNondeterministicStubbingTest {

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
    void test() {
        stubFor(post(urlPathEqualTo("/payments"))
            .withRequestBody(
                    matchingJsonPath("cardNumber")
            )
            .withRequestBody(
                    matchingJsonPath("cardExpiryDate ")
            )
            .withRequestBody(
                    matchingJsonPath("cardNumber")
            )
            .willReturn(okJson("{\n" +
                    "  \"paymentId\": \"2222\",\n" +
                    "  \"paymentResponseStatus\": \"SUCCESS\"\n" +
                    "}")));

        stubFor(get(urlPathMatching("/fraudCheck/.*"))
                .willReturn(okJson("  {\n" +
                        "    \"blacklisted\": false\n" +
                        "  }")));

        final var batch = IntStream.range(0, 10)
                .mapToObj(this::generateBookingPayment)
                .collect(toList());

        final var ticketBookingResponses = ticketBookingService.batchPayment(batch);

        assertThat(ticketBookingResponses).hasSize(batch.size());

        ticketBookingResponses
                .forEach(response ->
                        assertThat(response.getBookingResponseStatus()).isEqualTo(SUCCESS));

        //verify
        verify(5,getRequestedFor(urlPathMatching("/fraudCheck/.*")));

        verify(10,postRequestedFor(urlPathEqualTo("/payments"))
                        .withRequestBody(
                                matchingJsonPath("cardNumber")
                        )
                        .withRequestBody(
                                matchingJsonPath("cardExpiryDate ")
                        )
                        .withRequestBody(
                                matchingJsonPath("cardNumber")
                        )
                );
    }

    private TicketBookingPaymentRequest generateBookingPayment(int i) {
        final CardDetails creditCard = new CardDetails(
                cardNumbers(),
                LocalDate.of(i, 1, 1)
        );
        TicketBookingPaymentRequest bookingPayment = new TicketBookingPaymentRequest(Integer.toString(i), Double.valueOf(i), creditCard);
        if (i % 2 == 0) {
            bookingPayment.setFraudAlert(true);
        }
        return bookingPayment;
    }

    private String cardNumbers() {
        Random random = new Random();
        return randomNumber(random) +
                "-" +
                randomNumber(random) +
                "-" +
                randomNumber(random) +
                "-" +
                randomNumber(random);
    }

    private String randomNumber(Random random) {
        return IntStream.range(0, 4).mapToObj(i -> Integer.toString(random.nextInt(9) + 1)).reduce((l, r) -> l + r).get();
    }
}
