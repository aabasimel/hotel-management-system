package com.ahmedabasimel.myhotel.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class RoomResponse {

    private UUID id;
    private String roomType;

    private Double roomPrice;

    private boolean isBooked;

    private String photo;
    private List<BookingResponse> bookings= new ArrayList<>();



    public RoomResponse(UUID id, String roomType, Double price) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = price;

    }

    public RoomResponse(UUID id, String roomType, boolean isBooked,
                        Double roomPrice, byte[] photoBytes,
                        List<BookingResponse> bookings) {
        this(id, roomType, isBooked, roomPrice,
                photoBytes != null ? Base64.getEncoder().encodeToString(photoBytes) : null,
                bookings);
    }

    // Constructor for Base64 String photo
    public RoomResponse(UUID id, String roomType, boolean isBooked,
                        Double roomPrice, String base64Photo,
                        List<BookingResponse> bookings) {
        this.id = id;
        this.roomType = roomType;
        this.isBooked = isBooked;
        this.roomPrice = roomPrice;
        this.photo = base64Photo;
        this.bookings = bookings;
    }
}