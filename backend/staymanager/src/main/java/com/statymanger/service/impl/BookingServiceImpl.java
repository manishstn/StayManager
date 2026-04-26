package com.statymanger.service.impl;

import com.statymanger.dto.BookingDTO;
import com.statymanger.dto.Response;
import com.statymanger.entity.Booking;
import com.statymanger.entity.Room;
import com.statymanger.entity.User;
import com.statymanger.exception.ResourceNotFoundException;
import com.statymanger.repository.BookingRepository;
import com.statymanger.repository.RoomRepository;
import com.statymanger.repository.UserRepository;
import com.statymanger.service.BookingService;
import com.statymanger.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();
        bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        bookingRepository.deleteById(bookingId);

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Booking successfully cancelled");
        return response;
    }

    @Override
    public Response getByCode(String code) {
        Response response = new Response();
        Booking booking = bookingRepository.findByBookingConfirmationCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid booking confirmation code"));

        BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTO(booking);
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Success");
        response.setBooking(bookingDTO);
        return response;
    }

    @Override
    public Response getAllBooking() {
        Response response = new Response();
        List<Booking> bookings = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookings);

        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Successfully retrieved all bookings");
        response.setBookingList(bookingDTOList);
        return response;
    }

    @Override
    @Transactional
    public Response saveBooking(Long roomId, Long userId, Booking booking) {
        Response response = new Response();

        if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
            throw new IllegalArgumentException("Check-in Date must be before Check-out Date");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Room Id"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User Id"));

        List<Booking> existingBookingList = room.getBookings();

        if (!roomIsAvailable(booking, existingBookingList)) {
            throw new ResourceNotFoundException("Room not available for selected date range.");
        }

        booking.setRoom(room);
        booking.setUser(user);
        booking.setBookingConfirmationCode(Utils.generateRandomConfirmationCode(10));

        bookingRepository.save(booking);

        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Booking has been Successfully done.");
        response.setBookingConfirmationCode(booking.getBookingConfirmationCode());
        return response;
    }


    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream().noneMatch(existing ->
                bookingRequest.getCheckInDate().isBefore(existing.getCheckOutDate()) &&
                        bookingRequest.getCheckOutDate().isAfter(existing.getCheckInDate())
        );
    }
}