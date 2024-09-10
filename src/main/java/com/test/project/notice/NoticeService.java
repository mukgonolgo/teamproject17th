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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noRepository;

	public Notice getNotice(Long No_id) {
		Optional<Notice> notice = this.noRepository.findById(No_id);
		if(notice.isPresent()) {
			return notice.get();
		}else {
			throw new DataNotFoundException("question not found");
		}
	}
    
    public Notice saveNo(Notice notice) {
        return noRepository.save(notice);
    }
    public void delete(Notice nocice) {
    	this.noRepository.delete(nocice);
    }

    public List<Notice> getAllNotices() {
        return noRepository.findAll();
    }

    public void saveNotice( String No_title, String No_content) {
    	Notice notice = new Notice();
    	notice.setNo_title(No_title);
    	notice.setNo_content(No_content);
		this.noRepository.save(notice);  // 데이터 저장
    }
    
//    public Page<Notice> getList(int page, String kw){
//    	//최신순으로 정렬
//    	List<Sort.Order> sorts= new ArrayList<>();
//    	sorts.add(Sort.Order.desc("createDate"));
//    	Pageable pageable = PageRequest.of(page, 10,Sort.by(sorts));
//    	return this.noRepository.findAllByKeyword(kw, pageable);
//    }
}
