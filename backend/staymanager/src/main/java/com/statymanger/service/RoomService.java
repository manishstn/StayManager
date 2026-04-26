package com.statymanger.service;

import com.statymanger.dto.Response;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) throws IOException;

    Response getAllRooms();

    Response getRoomById(Long roomId);

    Response deleteRoom(Long roomId);

    Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) throws IOException;

    Response getAvailableRoomsByDataAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    Response getAllAvailableRooms();

    List<String> getAllRoomTypes();
}