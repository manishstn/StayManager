package com.statymanger.dto; // Keep it in your staymanager package

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    private int statusCode;
    private String message;

    // Auth details
    private String token;
    private String role;
    private String expirationTime;

    // Logic details
    private String bookingConfirmationCode;

    // Single Entities
    private UserDTO user;
    private RoomDTO room;
    private BookingDTO booking;

    // Lists (For Search/Admin views)
    private List<UserDTO> userList;
    private List<RoomDTO> roomList;
    private List<BookingDTO> bookingList;
}