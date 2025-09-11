package com.ahmedabasimel.myhotel.controller;

import com.ahmedabasimel.myhotel.exception.PhotoRetrievalException;
import com.ahmedabasimel.myhotel.exception.ResourceNotFoundException;
import com.ahmedabasimel.myhotel.models.BookedRoom;
import com.ahmedabasimel.myhotel.models.Room;
import com.ahmedabasimel.myhotel.models.response.BookingResponse;
import com.ahmedabasimel.myhotel.models.response.RoomResponse;
import com.ahmedabasimel.myhotel.service.impl.BookedRoomServiceImpl;
import com.ahmedabasimel.myhotel.service.impl.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomServiceImpl roomService;
    private final BookedRoomServiceImpl bookedRoomService;

    @PostMapping("/add/rooms")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> addRooms(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice")Double roomPrice ) throws SQLException, IOException {

        Room savedRoom = roomService.addRoom(photo,roomType, roomPrice);

        RoomResponse response = new RoomResponse(savedRoom.getId(),savedRoom.getRoomType(), savedRoom.getRoomPrice());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes(){
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        System.out.println("Rooms found: " + rooms.size());

        List<RoomResponse> roomResponses = rooms.stream()
                .map(this::getRoomResponse)
                .toList();

        return ResponseEntity.ok(roomResponses);
    }

    private RoomResponse getRoomResponse(Room room) {
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        List<BookingResponse> bookingInfo = bookings.stream()
                .map(booking -> new BookingResponse(
                        booking.getBookingId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getBookingConfirmationCode()))
                .toList();

        String base64Photo = roomService.getRoomPhotoBase64ByRoomId(room.getId());

        return new RoomResponse(
                room.getId(),
                room.getRoomType(),
                room.isBooked(),
                room.getRoomPrice(),
                base64Photo,
                bookingInfo);
    }
    private List<BookedRoom> getAllBookingsByRoomId(UUID roomId){
        return bookedRoomService.getAllBookingsByRoomId(roomId);
    }

    @DeleteMapping("/room/delete/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId) throws SQLException {
         roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")

    public ResponseEntity<RoomResponse> updateRoom(@PathVariable UUID roomId,
                                                   @RequestParam(required = false)  String roomType,
                                                   @RequestParam(required = false) Double roomPrice,
                                                   @RequestParam(required = false) MultipartFile photo) throws SQLException, IOException {
        byte[] photoBytes = photo != null && !photo.isEmpty() ?
                photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length >0 ? new SerialBlob(photoBytes): null;
        Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);
        theRoom.setPhoto(photoBlob);
        RoomResponse roomResponse = getRoomResponse(theRoom);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable UUID roomId){
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return  ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam("roomType") String roomType) {

        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();

        for (Room room : availableRooms) {
            try {
                byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
                RoomResponse roomResponse = getRoomResponse(room);
                if (photoBytes != null && photoBytes.length > 0) {
                    String photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
                    roomResponse.setPhoto(photoBase64);
                } else {
                    roomResponse.setPhoto(null); // or placeholder
                }
                roomResponses.add(roomResponse);
            } catch (PhotoRetrievalException ex) {
                throw new  PhotoRetrievalException("Failed to retrieve photo for room " + room.getId(), ex);

            }
        }

        if (roomResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(roomResponses);
        }
    }



}
