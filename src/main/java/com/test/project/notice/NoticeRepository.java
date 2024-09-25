package com.test.project.notice;

import java.util.List;

//import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 제목으로 공지를 검색하는 메소드
    List<Notice> findByNoticeTitleContaining(String title);
    Page<Notice> findAll(Pageable pageable);
    // 키워드로 공지를 검색하는 메소드
    @Query("SELECT n FROM Notice n WHERE n.noticeTitle LIKE %:kw%")
	Page<Notice> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
      
      
   }
