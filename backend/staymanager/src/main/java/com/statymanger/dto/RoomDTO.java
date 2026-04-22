package com.statymanger.dto; // Consistent with your StayManager project

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoomDTO(
        Long id,
        String roomType,
        BigDecimal roomPrice,
        String roomDescription,
        String roomPhotoUrl,
        List<BookingDTO> bookings
) {
    // Compact constructor to prevent null list issues in the frontend
    public RoomDTO {
        if (bookings == null) {
            bookings = List.of();
        }
    }
}