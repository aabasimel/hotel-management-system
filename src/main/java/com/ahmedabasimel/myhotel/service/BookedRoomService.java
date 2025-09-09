package com.ahmedabasimel.myhotel.service;

import com.ahmedabasimel.myhotel.models.BookedRoom;

import java.util.List;
import java.util.UUID;

public interface BookedRoomService {
     List<BookedRoom> getAllBookingsByRoomId(UUID roomId);
    BookedRoom findByBookingConfirmationCode(String confirmationCode);
    String saveBooking(UUID roomId, BookedRoom bookingRequest);
    void cancelBooking(UUID bookingId);

}
