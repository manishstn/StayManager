package com.statymanger.controller;

import com.statymanger.dto.Response;
import com.statymanger.entity.Booking;
import com.statymanger.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking", description = "Endpoints for room bookings, history, and cancellations")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/book-room/{roomId}/{userId}")
    @Operation(summary = "Create a new booking", description = "Secure endpoint to book a room for a specific user.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Response> book(
            @Parameter(description = "ID of the room to book") @PathVariable Long roomId,
            @Parameter(description = "ID of the user making the booking") @PathVariable Long userId,
            @RequestBody Booking booking) {

        Response response = bookingService.saveBooking(roomId, userId, booking);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all bookings", description = "Administrative endpoint to view all system bookings.")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> getAllBooking() {
        Response response = bookingService.getAllBooking();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/get-by-confirmation-code/{code}") // Descriptive naming
    @Operation(summary = "Find booking by code", description = "Retrieve booking details using the unique confirmation code.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')") // Added security
    public ResponseEntity<Response> getAllBookingByCode(@PathVariable String code) {
        Response response = bookingService.getByCode(code);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/cancel/{bookingId}")
    @Operation(summary = "Cancel a booking", description = "Remove a booking from the system using its ID.")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<Response> cancelBooking(@PathVariable Long bookingId) {
        Response response = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}