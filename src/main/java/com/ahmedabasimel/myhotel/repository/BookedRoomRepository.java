package com.ahmedabasimel.myhotel.repository;

import com.ahmedabasimel.myhotel.models.BookedRoom;
import com.ahmedabasimel.myhotel.models.response.BookingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookedRoomRepository extends JpaRepository<BookedRoom, UUID> {
    List<BookedRoom> getAllBookingsByRoomId(UUID roomId);
    Optional<BookedRoom> findByBookingConfirmationCode(String confirmationCode);

}
