package com.statymanger.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingDTO(Long id, LocalDate checkInDate, LocalDate checkOutDate, int numOfChildren, int numOfAdults,
                         int totalNumOfGuest, String bookingConfirmationCode,UserDTO userDTO,RoomDTO roomDTO) {
}