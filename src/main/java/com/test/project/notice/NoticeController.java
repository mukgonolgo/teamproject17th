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
    public String getNotices(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        return "notice/notices";
    }
//    @GetMapping("/notices")
//    public String getNotices(Model model, @RequestParam(defaultValue = "0") int page,
//                              @RequestParam(required = false) String kw) {
//        Page<Notice> noticesPage = noticeService.getList(page, kw);
//        model.addAttribute("notices", noticesPage.getContent());
//        model.addAttribute("page", noticesPage);
//        model.addAttribute("notice", new Notice());
//        model.addAttribute("kw", kw); // 키워드 추가
//        return "notices";
//    }
    
    @GetMapping("/add-notice")
    public String showAddNoticeForm() {
        return "notice/notice_form";
    }

    @PostMapping("/notices")
    public String addnotice(@RequestParam(value="No_title") String No_title, @RequestParam(value="No_content") String No_content) {
    	this.noticeService.saveNotice(No_title, No_content);
        return "redirect:/notices";
    }
    
    @PostMapping("/notices/{No_id}")
    public String deleteNotice(Principal principal, @PathVariable("No_id") Long No_id) {
    	Notice notice = this.noticeService.getNotice(No_id);
        noticeService.delete(notice);
        return "redirect:/notices"; 
    }
}