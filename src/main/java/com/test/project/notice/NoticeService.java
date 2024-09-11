package com.test.project.notice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.test.project.DataNotFoundException;
import com.test.project.notice.Notice;
import com.test.project.notice.NoticeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noRepository;

   public Notice getNotice(Long noticeId) {
      Optional<Notice> notice = this.noRepository.findById(noticeId);
      if(notice.isPresent()) {
         return notice.get();
      }else {
         throw new DataNotFoundException("question not found");
      }
   }
    
    public Notice saveNo(Notice notice) {
		
        return noRepository.save(notice);
    }
    
    public void delete(Notice notice) {
       this.noRepository.delete(notice);
    }

    public List<Notice> getAllNotices() {
        return noRepository.findAll();
    }

    public void saveNotice( String noticeTitle, String noticeContent) {
       Notice notice = new Notice();
       notice.setNoticeTitle(noticeTitle);
       notice.setNoticeContent(noticeContent);
       notice.setCreateDate(LocalDateTime.now());
      this.noRepository.save(notice);  // 데이터 저장
    }
    
	public Page<Notice> getList(int page, String kw){
		//최신순으로 정렬
		List<Sort.Order> sorts= new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));
		Pageable pageable = PageRequest.of(page, 7,Sort.by(sorts));
		return this.noRepository.findAllByKeyword(kw, pageable);
	}

    public int getTotalCount() {
        return (int) noRepository.count(); // 전체 공지사항 수 반환
    }
}
