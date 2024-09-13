package com.test.project.eat_friends;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
	 Friend findByFriendTitle(String friendTitle); // 게시글 제목으로 Friend 엔티티 조회
}
