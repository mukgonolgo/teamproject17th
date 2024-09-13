package com.test.project.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByRoom_RoomId(String roomId);
}
