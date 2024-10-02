package com.test.project.board;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

	private final UserService userService;
	private final BoardService boardService;
	private final AnswerService answerService;
	private final AnswerRepository answerRepository;
	private static final Logger logger = LoggerFactory.getLogger(AnswerService.class);
	private void setUserAttributes(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		SiteUser user = userService.getUser(userDetails.getUsername());
		model.addAttribute("profileImage", user.getImageUrl());
		model.addAttribute("username", user.getUsername());
		model.addAttribute("userId", user.getId());
	}

	//답변생성
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{boardId}")
	public String createAnswer(Model model,
	        @PathVariable("boardId") Long boardId,  // boardId로 변경
	        @Valid BoardDTO boardDTO,
	        @Valid AnswerFormDTO answerFormDTO,
	        BindingResult bindingResult,
	        Principal principal,
	        @AuthenticationPrincipal UserDetails userDetails) {

	    System.out.println("@@@@@@@@@createAnswer 메서드 시작@@@@@@@@@");

	    // 게시글 조회
	    Board board = this.boardService.findById(boardId);  // boardId 사용
	    SiteUser siteUser = this.userService.getUser(principal.getName());
	    setUserAttributes(model, userDetails);

	    // 유효성 검사
	    if (bindingResult.hasErrors()) {
	        System.out.println("유효성 검사 실패: " + bindingResult.getAllErrors());
	        model.addAttribute("board", board);
	        return "board_detail";
	    }

	    Answer parentAnswer = null; // 부모 답변 설정
	    if (boardDTO.getParentId() != null) {
	        parentAnswer = this.answerService.findById(boardDTO.getParentId());
	    }

	    // 답변 생성
	    if (boardDTO.getContent() != null && !boardDTO.getContent().isEmpty()) {
	        Answer answer = this.answerService.create(board, boardDTO.getContent(), siteUser, parentAnswer);

	        // 리다이렉트 URL 생성
	        return String.format("redirect:/board_detail/%s#answer_%s", answer.getBoard().getBoardId(), answer.getAnswerId());
	    } else {
	        model.addAttribute("board", board);
	        return "board_detail"; 
	    }
	}
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/comment/{AnswerId}")
	public ResponseEntity<String> createComment(
	        @PathVariable("AnswerId") Long id,
	        @RequestBody Map<String, String> body,
	        Principal principal) {
	    
	    // 부모 답변 조회
	    Answer parentAnswer = answerService.getAnswer(id);
	    if (parentAnswer == null) {
	        return ResponseEntity.notFound().build();
	    }
	    // 사용자 정보 조회
	    SiteUser siteUser = userService.getUser(principal.getName());
	    logger.info("answer id (b4 commentContent) "+ id);
	    logger.info("Request Body(af b4 commment: " + body);		  
	    // 댓글 내용 추출
	    String commentContent = body.get("comment");
	    logger.info("Request Body(af b4 commentContent: " + commentContent);		  
		  
	    if (commentContent == null || commentContent.isEmpty()) {
	        return ResponseEntity.badRequest().body("댓글 내용이 비어있습니다.");
	    }
	    logger.info("answer id (af commentContent) "+ id);
	    
	    // 대댓글 생성
	    Answer comment = new Answer();
	    comment.setAnswerContent(commentContent);
	    comment.setUser(siteUser);
	    comment.setParentAnswer(parentAnswer);
	    comment.setAnswerCreateDate(LocalDateTime.now());
	    logger.info("answer id (acc 114line) "+ id);
	    logger.info("commentContent (acc 115line) "+ commentContent);
		  
	    // 대댓글을 부모 답변의 댓글 목록에 추가
	    parentAnswer.getComment().add(comment);
	    
	    // 대댓글 저장
	    answerRepository.save(comment);
	    
	    return ResponseEntity.ok("대댓글이 성공적으로 추가되었습니다.");
	}


	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{AnswerId}")
	@ResponseBody
	public ResponseEntity<String> answerModify(
	        @PathVariable("AnswerId") Long id,
	        @RequestBody Map<String, String> payload, // JSON 요청을 받을 수 있도록 수정
	        Principal principal) {
	    String content = payload.get("content");
	    Answer answer = answerService.getAnswer(id);
	    if (!answer.getUser().getUsername().equals(principal.getName())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
	    }
	    answerService.modify(answer, content);
	    return ResponseEntity.ok("수정 완료"); // 성공 시 응답
	}


	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{AnswerId}")
	public String answerDelete(@PathVariable("AnswerId") Long id, Principal principal,
			@AuthenticationPrincipal UserDetails userDetails, Model model) {
		Answer answer = this.answerService.getAnswer(id);
		setUserAttributes(model, userDetails);
		if (!answer.getUser().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
		}
		this.answerService.delete(answer);
		return String.format("redirect:/board_detail/%s", answer.getBoard().getBoardId());
	}
	
	@PostMapping("/commentModify/{commentId}")
	public ResponseEntity<String> modifyComment(@PathVariable Long commentId, @RequestBody Map<String, String> body) {
	    String content = body.get("content"); // JSON에서 content를 가져옵니다.
	    answerService.modifyComment(commentId, content);
	    System.out.println(content+"@@@@@@@@@@@@@@@################");
	    return ResponseEntity.ok(content); // 수정된 내용을 문자열로 반환
	}
	
	//대댓글 삭제
    // 대댓글 삭제 API
    @PostMapping("/commentDelete/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        answerService.deleteComment(commentId);
        System.out.println(commentId+"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        return ResponseEntity.ok().build();
    }
	

	
	
}