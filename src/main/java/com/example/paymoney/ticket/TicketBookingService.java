package com.example.paymoney.ticket;


import com.example.paymoney.card.CardDetails;
import com.example.paymoney.fraud.FraudCheckResponse;
import com.example.paymoney.payment.PaymentProcessorGateway;
import com.example.paymoney.payment.PaymentProcessorResponse;
import com.example.paymoney.payment.PaymentUpdateResponse;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class TicketBookingService {

    private final PaymentProcessorGateway paymentProcessorGateway;

    public TicketBookingService(PaymentProcessorGateway paymentProcessorGateway) {
        this.paymentProcessorGateway = paymentProcessorGateway;
    }

    public TicketBookingResponse payForBooking(final TicketBookingPaymentRequest bookingPayment) {


        final CardDetails cardDetails = bookingPayment.getCardDetails();

        if (bookingPayment.isFraudAlert()) {
            final FraudCheckResponse fraudCheckResponse = paymentProcessorGateway.fraudCheck(cardDetails.getNumber());
            if (fraudCheckResponse.isBlacklisted()) {
                return new TicketBookingResponse(bookingPayment.getBookingId(), null, TicketBookingResponse.BookingResponseStatus.REJECTED);
            }
        }
        final PaymentProcessorResponse paymentProcessorResponse = paymentProcessorGateway.makePayment(cardDetails.getNumber(), cardDetails.getExpiry(), bookingPayment.getAmount());

        if (paymentProcessorResponse.getPaymentResponseStatus() == PaymentProcessorResponse.PaymentResponseStatus.SUCCESS) {
            return new TicketBookingResponse(bookingPayment.getBookingId(), paymentProcessorResponse.getPaymentId(), TicketBookingResponse.BookingResponseStatus.SUCCESS);
        } else {
            return new TicketBookingResponse(bookingPayment.getBookingId(), paymentProcessorResponse.getPaymentId(), TicketBookingResponse.BookingResponseStatus.REJECTED);
        }
    }

    public PaymentUpdateResponse updatePaymentDetails(final TicketBookingPaymentRequest bookingPayment) {

        paymentProcessorGateway.updatePayment(bookingPayment.getBookingId());

        PaymentUpdateResponse paymentUpdateResponse = new PaymentUpdateResponse();
        paymentUpdateResponse.setStatus("SUCCESS");

        return paymentUpdateResponse;
    }

    public List<TicketBookingResponse> batchPayment(final List<TicketBookingPaymentRequest> bookingPayment) {
        return bookingPayment
                .stream()
                .map(this::payForBooking)
                .collect(toList());
    }
}
