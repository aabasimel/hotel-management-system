package com.ahmedabasimel.myhotel.service;

import com.ahmedabasimel.myhotel.models.Room;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomService {
    Room addRoom(MultipartFile photo, String roomType, Double roomPrice) throws IOException, SQLException;
    List<String> getAllRoomTypes();
    List<Room> getAllRooms();
    byte[] getRoomPhotoByRoomId(UUID roomId) throws SQLException;
    String getRoomPhotoBase64ByRoomId(UUID roomId);
    ResponseEntity<Void> deleteRoom(UUID roomId) throws SQLException;
    Room updateRoom(UUID roomId, String roomType, Double roomPrice, byte[] photoBytes);
    Optional<Room> getRoomById(UUID roomId);
    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate,String roomType);

}
