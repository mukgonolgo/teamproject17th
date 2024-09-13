package com.test.project.Reservation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.project.chat.ChatRoom;
import com.test.project.user.SiteUser;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByUserAndChatRoom(SiteUser user, ChatRoom chatRoom);

    List<Reservation> findByUser(SiteUser user);

    List<Reservation> findByChatRoom(ChatRoom chatRoom);
}
