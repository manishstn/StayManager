package com.statymanger.utils; // Updated package

import com.statymanger.dto.BookingDTO;
import com.statymanger.dto.RoomDTO;
import com.statymanger.dto.UserDTO;
import com.statymanger.entity.Booking;
import com.statymanger.entity.Room;
import com.statymanger.entity.User;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateRandomConfirmationCode(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC_STRING.length());
            stringBuilder.append(ALPHANUMERIC_STRING.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    // --- USER MAPPING ---

    public static UserDTO mapUserEntityToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }

    public static UserDTO mapUserEntityToUserDTOPlusBookings(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .bookingList(user.getBookings().stream()
                        .map(booking -> mapBookingEntityToBookingDTOPlusRooms(booking, false))
                        .collect(Collectors.toList()))
                .build();
    }

    // --- ROOM MAPPING ---

    public static RoomDTO mapRoomEntityToRoomDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .roomType(room.getRoomType())
                .roomPrice(room.getRoomPrice())
                .roomPhotoUrl(room.getRoomPhotoUrl())
                .roomDescription(room.getRoomDescription())
                .build();
    }

    public static RoomDTO mapRoomEntityToRoomDTOPlusBookings(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .roomType(room.getRoomType())
                .roomPrice(room.getRoomPrice())
                .roomPhotoUrl(room.getRoomPhotoUrl())
                .roomDescription(room.getRoomDescription())
                .bookings(room.getBookings() != null ?
                        room.getBookings().stream()
                                .map(Utils::mapBookingEntityToBookingDTO)
                                .collect(Collectors.toList()) : null)
                .build();
    }

    // --- BOOKING MAPPING ---

    public static BookingDTO mapBookingEntityToBookingDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numOfAdults(booking.getNumOfAdults())
                .numOfChildren(booking.getNumOfChildren())
                .totalNumOfGuest(booking.getTotalNumOfGuest())
                .bookingConfirmationCode(booking.getBookingConfirmationCode())
                .build();
    }

    public static BookingDTO mapBookingEntityToBookingDTOPlusRooms(Booking booking, boolean mapUser) {
        return BookingDTO.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .numOfAdults(booking.getNumOfAdults())
                .numOfChildren(booking.getNumOfChildren())
                .totalNumOfGuest(booking.getTotalNumOfGuest())
                .bookingConfirmationCode(booking.getBookingConfirmationCode())
                .roomDTO(booking.getRoom() != null ? mapRoomEntityToRoomDTO(booking.getRoom()) : null)
                .userDTO(mapUser ? mapUserEntityToUserDTO(booking.getUser()) : null)
                .build();
    }

    // --- LIST MAPPINGS ---

    public static List<UserDTO> mapUserListEntityToUserListDTO(List<User> userList) {
        return userList.stream().map(Utils::mapUserEntityToUserDTO).collect(Collectors.toList());
    }

    public static List<RoomDTO> mapRoomListEntityToRoomListDTO(List<Room> roomList) {
        return roomList.stream().map(Utils::mapRoomEntityToRoomDTO).collect(Collectors.toList());
    }

    public static List<BookingDTO> mapBookingListEntityToBookingListDTO(List<Booking> bookingList) {
        return bookingList.stream().map(Utils::mapBookingEntityToBookingDTO).collect(Collectors.toList());
    }
}