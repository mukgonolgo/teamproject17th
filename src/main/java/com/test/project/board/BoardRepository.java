package com.test.project.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.boardTitle = :boardTitle")
    Optional<Board> findByBoardTitle(@Param("boardTitle") String boardTitle);

    List<Board> findByBoardTitleLike(String boardTitle);

    Page<Board> findAll(Pageable pageable);
    
    List<Board> findByBoardTag(String boardTag);
    
    @Query("SELECT DISTINCT b "
    	     + "FROM Board b "
    	     + "LEFT JOIN b.user u1 "
    	     + "LEFT JOIN b.answerList a "
    	     + "LEFT JOIN a.user u2 "
    	     + "WHERE "
    	     + "   b.boardTitle LIKE %:kw% "
    	     + "   OR b.boardContent LIKE %:kw% "
    	     + "   OR u1.username LIKE %:kw% "
    	     + "   OR a.AnswerContent LIKE %:kw% " 
    	     + "   OR u2.username LIKE %:kw% "
    	     + "ORDER BY b.boardCreateDate DESC") // 여기서 수정
    	Page<Board> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

    Page<Board> findByBoardTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Board> findByBoardContentContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Board> findByUser_UsernameContainingIgnoreCase(String keyword, Pageable pageable); // user와 username의 관계

	

}