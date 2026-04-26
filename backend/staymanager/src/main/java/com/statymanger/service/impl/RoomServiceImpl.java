package com.statymanger.service.impl;

import com.statymanger.dto.Response;
import com.statymanger.dto.RoomDTO;
import com.statymanger.entity.Room;
import com.statymanger.exception.ResourceNotFoundException;
import com.statymanger.repository.RoomRepository;
import com.statymanger.service.CloudinaryService;
import com.statymanger.service.RoomService;
import com.statymanger.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
        Map<String, String> uploadResult = cloudinaryService.uploadFile(photo);

        Room room = new Room();
        room.setRoomPhotoUrl(uploadResult.get("url"));
        room.setRoomPhotoId(uploadResult.get("publicId"));
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        room.setRoomDescription(description);

        Room savedRoom = roomRepository.save(room);
        RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);

        return Response.builder()
                .statusCode(200)
                .message("Room added successfully")
                .room(roomDTO)
                .build();
    }

    @Override
    public Response updateRoom(Long roomId, String description, String roomType, BigDecimal roomPrice, MultipartFile photo) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        if (photo != null && !photo.isEmpty()) {
            // Delete old photo from Cloudinary first
            if (room.getRoomPhotoId() != null) {
                cloudinaryService.deleteFile(room.getRoomPhotoId());
            }
            Map<String, String> uploadResult = cloudinaryService.uploadFile(photo);
            room.setRoomPhotoUrl(uploadResult.get("url"));
            room.setRoomPhotoId(uploadResult.get("publicId"));
        }

        if (roomType != null) room.setRoomType(roomType);
        if (roomPrice != null) room.setRoomPrice(roomPrice);
        if (description != null) room.setRoomDescription(description);

        Room updatedRoom = roomRepository.save(room);
        RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);

        return Response.builder()
                .statusCode(200)
                .message("Room updated successfully")
                .room(roomDTO)
                .build();
    }

    @Override
    public Response deleteRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        // Clean up Cloudinary storage
        if (room.getRoomPhotoId() != null) {
            cloudinaryService.deleteFile(room.getRoomPhotoId());
        }

        roomRepository.deleteById(roomId);
        return Response.builder()
                .statusCode(200)
                .message("Room and image deleted successfully")
                .build();
    }

    @Override
    public Response getAllRooms() {
        List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return Response.builder()
                .statusCode(200)
                .message("Rooms fetched")
                .roomList(Utils.mapRoomListEntityToRoomListDTO(roomList))
                .build();
    }

    @Override
    public Response getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        return Response.builder()
                .statusCode(200)
                .message("Room fetched")
                .room(Utils.mapRoomEntityToRoomDTOPlusBookings(room))
                .build();
    }

    @Override
    public Response getAvailableRoomsByDataAndType(LocalDate checkIn, LocalDate checkOut, String type) {
        List<Room> rooms = roomRepository.findAvailableRoomsByDatesAndTypes(checkIn, checkOut, type);
        return Response.builder()
                .statusCode(200)
                .roomList(Utils.mapRoomListEntityToRoomListDTO(rooms))
                .build();
    }

    @Override
    public Response getAllAvailableRooms() {
        List<Room> rooms = roomRepository.getAllAvailableRooms();
        return Response.builder()
                .statusCode(200)
                .roomList(Utils.mapRoomListEntityToRoomListDTO(rooms))
                .build();
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }
}