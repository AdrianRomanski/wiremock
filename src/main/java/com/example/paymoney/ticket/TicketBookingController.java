package com.example.paymoney.ticket;


import com.example.paymoney.payment.PaymentUpdateResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TicketBookingController {

    private final TicketBookingService ticketBookingService;

    public TicketBookingController(TicketBookingService ticketBookingService) {
        this.ticketBookingService = ticketBookingService;
    }

    @PostMapping("/bookTicket")
    TicketBookingResponse payForTicket(final TicketBookingPaymentRequest ticketBookingPaymentRequest){
        return ticketBookingService.payForBooking(ticketBookingPaymentRequest);
    }

    @PostMapping("/updatePayment")
    PaymentUpdateResponse updatePaymentDetails(final TicketBookingPaymentRequest ticketBookingPaymentRequest){
        return ticketBookingService.updatePaymentDetails(ticketBookingPaymentRequest);
    }

    @PostMapping("/batchPayment")
    List<TicketBookingResponse> batchPayment(List<TicketBookingPaymentRequest> bookingPayment){
        return ticketBookingService.batchPayment(bookingPayment);
    }
}
