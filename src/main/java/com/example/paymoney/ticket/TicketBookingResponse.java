package com.example.paymoney.ticket;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TicketBookingResponse {
    private final String bookingId;
    private final String paymentId;
    private final BookingResponseStatus bookingResponseStatus;

    public TicketBookingResponse(String bookingId, String paymentId, BookingResponseStatus bookingResponseStatus) {
        this.bookingId = bookingId;
        this.paymentId = paymentId;
        this.bookingResponseStatus = bookingResponseStatus;
    }

    public enum BookingResponseStatus {
        SUCCESS, REJECTED
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public BookingResponseStatus getBookingResponseStatus() {
        return bookingResponseStatus;
    }

    @Override
    public boolean equals(Object obj){
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode(){
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this);
    }

}
