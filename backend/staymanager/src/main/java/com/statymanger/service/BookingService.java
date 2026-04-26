package com.statymanger.service;

import com.statymanger.dto.Response;
import com.statymanger.entity.Booking;

public interface BookingService {
    Response cancelBooking(Long bookingId);

    Response getByCode(String code);

    Response getAllBooking();

    Response saveBooking(Long roomId, Long userId, Booking booking);
}