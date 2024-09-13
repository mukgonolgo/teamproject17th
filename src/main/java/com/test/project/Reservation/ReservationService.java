package com.test.project.Reservation;

import com.test.project.chat.ChatRoom;
import com.test.project.chat.ChatRoomRepository;
import com.test.project.user.SiteUser;
import com.test.project.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    // 예약 생성
    public boolean reserveRoom(String roomId, String username) {
        SiteUser user = userService.findByUsername(username);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);

        if (user == null || chatRoom == null) {
            return false;
        }

        Optional<Reservation> existingReservation = reservationRepository.findByUserAndChatRoom(user, chatRoom);

        if (existingReservation.isPresent()) {
            Reservation reservation = existingReservation.get();
            if (reservation.isReserved()) {
                reservation.setReserved(false);
                reservationRepository.save(reservation);
                return true; // 예약 취소됨
            }
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setChatRoom(chatRoom);
        reservation.setReserved(true);

        reservationRepository.save(reservation);
        return true; // 예약 성공
    }

    // 예약 확인
    public boolean isRoomReserved(String roomId, Long userId) {
        SiteUser user = userService.getUserById(userId);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);

        if (user == null || chatRoom == null) {
            return false;
        }

        Optional<Reservation> reservation = reservationRepository.findByUserAndChatRoom(user, chatRoom);
        return reservation.map(Reservation::isReserved).orElse(false);
    }
}
