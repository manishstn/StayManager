package com.statymanger.dto; // Fixed typo in package name

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(
        Long id,
        String email,
        String name,
        String phoneNumber,
        String role,
        List<BookingDTO> bookingList
) {
    // Compact constructor to ensure the list is never null
    public UserDTO {
        if (bookingList == null) {
            bookingList = List.of();
        }
    }
}