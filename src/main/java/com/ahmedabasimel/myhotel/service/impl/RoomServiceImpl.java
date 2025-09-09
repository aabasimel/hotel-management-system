package com.ahmedabasimel.myhotel.service.impl;

import com.ahmedabasimel.myhotel.exception.InternalServerException;
import com.ahmedabasimel.myhotel.exception.PhotoRetrievalException;
import com.ahmedabasimel.myhotel.exception.ResourceNotFoundException;
import com.ahmedabasimel.myhotel.models.Room;
import com.ahmedabasimel.myhotel.repository.RoomRepository;
import com.ahmedabasimel.myhotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    public Room addRoom(MultipartFile file, String roomType, Double roomPrice) throws IOException, SQLException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if (!file.isEmpty()){
            byte[] photoByte = file.getBytes();
            Blob photoBlob = new SerialBlob(photoByte);
            room.setPhoto(photoBlob);
        }

        return roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }



    @Override
    @Transactional(readOnly = true)
    public byte[] getRoomPhotoByRoomId(UUID roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        Blob photoBlob = room.getPhoto();
        if (photoBlob == null) {
            return null;
        }

        try (InputStream inputStream = photoBlob.getBinaryStream()) {
            // Read the photo in chunks to handle large files
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        } catch (SQLException e) {
            throw new PhotoRetrievalException("Database error while retrieving photo for room ID: " + roomId, e);
        } catch (IOException e) {
            throw new PhotoRetrievalException("IO error while reading photo for room ID: " + roomId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getRoomPhotoBase64ByRoomId(UUID roomId) {
        byte[] photoBytes = getRoomPhotoByRoomId(roomId);
        return photoBytes != null ? Base64.getEncoder().encodeToString(photoBytes) : null;

    }

    @Override
    public ResponseEntity<Void> deleteRoom(UUID roomId) throws SQLException {
        Optional<Room> room = roomRepository.findById(roomId);
        if(room.isPresent()){
            roomRepository.deleteById(roomId);
        }

        return null;
    }

    @Override
    public Room updateRoom(UUID roomId, String roomType, Double roomPrice, byte[] photoBytes){
        Room room = roomRepository.findById(roomId).get();
        if(roomType!=null) room.setRoomType(roomType);
        if(roomPrice!=null) room.setRoomPrice(roomPrice);
        if(photoBytes!=null && photoBytes.length>0){
            try{
                room.setPhoto(new SerialBlob(photoBytes));
            }catch(SQLException e){
                throw new InternalServerException("Fails to update room");
            }
        }
        return roomRepository.save(room);

    }

    @Override
    public Optional<Room> getRoomById(UUID roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<Room> getAvailableRooms(LocalDate checkinDate, LocalDate checkoutDate, String roomType) {
        return roomRepository.findAvailableRoomsBydateAndType(checkinDate, checkoutDate, roomType);
    }

}


