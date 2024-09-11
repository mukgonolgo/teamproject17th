package com.test.project.notice;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    @GetMapping("/notices")
    public String getNotices(Model model, @RequestParam(value="page",defaultValue="1") int page, @RequestParam(value="kw",defaultValue="") String kw) {
        model.addAttribute("notices", noticeService.getAllNotices());
        Page<Notice> paging = this.noticeService.getList(page, kw);
		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);
        return "notice/notices";
    }  
//    @GetMapping("/notices")
//    public String getNotices(Model model) {
//        model.addAttribute("notices", noticeService.getAllNotices());
//        return "notice/notices";
//    }   
    @GetMapping("/add-notice")
    public String showAddNoticeForm() {
        return "notice/notice_form";
    }

    @PostMapping("/notices")
    public String addnotice(@RequestParam(value="noticeTitle") String noticeTitle, @RequestParam(value="noticeContent") String noticeContent) {
    	this.noticeService.saveNotice(noticeTitle, noticeContent);
        return "redirect:/notices";
    }
    
    @PostMapping("/notices/{noticeId}")
    public String deleteNotice(Principal principal, @PathVariable("noticeId") Long noticeId) {
    	Notice notice = this.noticeService.getNotice(noticeId);
        noticeService.delete(notice);
        return "redirect:/notices"; 
    }
}