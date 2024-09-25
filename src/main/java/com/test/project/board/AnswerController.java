package com.test.project.board;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.catalina.User;
import org.hibernate.internal.build.AllowSysOut;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	private void setUserAttributes(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		SiteUser user = userService.getUser(userDetails.getUsername());
		model.addAttribute("profileImage", user.getImageUrl());
		model.addAttribute("username", user.getUsername());
		model.addAttribute("userId", user.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{AnswerId}")
	public String createAnswer(Model model,
	        @PathVariable("AnswerId") Long id,
	        @Valid AnswerFormDTO answerFormDTO,
	        BindingResult bindingResult,
	        Principal principal,
	        @AuthenticationPrincipal UserDetails userDetails) {

	    System.out.println("@@@@@@@@@createAnswer 메서드 시작@@@@@@@@@"); // 로그 추가
	    Answer answer = this.answerService.findById(id);
	    Board board = this.boardService.findById(id);
	    
	    SiteUser siteUser = this.userService.getUser(principal.getName());
	    setUserAttributes(model, userDetails);
	    System.out.println("content의 값" + board.getBoardContent());
	    System.out.println("answer 엔티티의 값"+  answerService.getAnswer(id));	  
	    System.out.println("answer 데이터 값"+  answer.getAnswerContent());
	    
	    
	    // 유효성 검사
	    if (bindingResult.hasErrors()) {
	        System.out.println("유효성 검사 실패: " + bindingResult.getAllErrors()); // 에러 로그 추가
	        model.addAttribute("board", board);
	        return "board_detail";
	    }

	    Answer parentAnswer = null; // 부모 답변 설정
	    if (answerFormDTO.getParentId() != null) {
	        parentAnswer = this.answerService.findById(answerFormDTO.getParentId());
	    }

	    // content가 있는 경우에만 처리
	    if (answerFormDTO.getContent() != null && !answerFormDTO.getContent().isEmpty()) {
	        // 댓글(comment)이 있는지 체크
	        List<Answer> comment = answerFormDTO.getComment();
	        if(comment == null) {
	        	comment = new ArrayList<>();
	        }
	        		
	         answer = this.answerService.create(board, answerFormDTO.getContent(), siteUser, parentAnswer, comment);
	        
	        // 리다이렉트 URL 생성
	        return String.format("redirect:/board_detail/%s#answer_%s", answer.getBoard().getBoardId(), answer.getAnswerId());
	    } else {
	        // content가 없는 경우 처리
	        model.addAttribute("board", board);
	        return "board_detail"; 
	    }
	}
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/comment/{AnswerId}")
	public ResponseEntity<String> createComment(
	        @PathVariable("AnswerId") Long id,
	        @RequestBody Map<String, String> body,
	        @Valid AnswerFormDTO answerFormDTO,
	        Principal principal) { // JSON 형식으로 받기
		Object commentJsom = body.get("comment");
		List<Answer> comment = new ArrayList<>(); // 빈 리스트로 초기화
		 ObjectMapper objectMapper = new ObjectMapper();

		    /*object로 html에서 받은 comment값을 String으로 변환시키고 LIst에 추가하거나
		     * List 타입일 경우 변환 없이 그대로 넣는 코드
		     *   */
	    if (commentJsom instanceof String) {
	        String commentJson = (String) commentJsom; // String으로 변환

	        // JSON 배열을 List<Answer>로 변환
	        if (commentJson != null && !commentJson.isEmpty()) {
	            try {
	                comment = objectMapper.readValue(commentJson, new TypeReference<List<Answer>>() {});
	            } catch (IOException e) {
	                e.printStackTrace(); // 에러 로그 추가
	                return ResponseEntity.badRequest().body("Invalid comment format");
	            }
	        }
	    } else if (commentJsom instanceof List) {
	        // comment가 이미 List 형태일 경우
	        comment = (List<Answer>) commentJsom;
	    }

	    
	    
	    
	    System.out.println("@@@@@@@@@@@@@@@@@@createComment 메서드 시작 @@@@@@@@@@@@"); // 로그 추가
	    Answer parentAnswer = answerService.findById(id);
	    
	    if (parentAnswer == null) {
	        return ResponseEntity.notFound().build();
	    }
	    Board board = this.boardService.findById(id);
	    SiteUser siteUser = this.userService.getUser(principal.getName());
	    Answer answer = new Answer();
	    String siteuser = answer.getUsername();
	    
	    System.out.println("answerFormDTO.getContent()  ==" + answerFormDTO.getContent());
	    answer.setAnswerContent(answerFormDTO.getContent());
	    answer = this.answerService.create(board, answerFormDTO.getContent(), siteUser, parentAnswer, comment);
	    
	    System.out.println("pa === " + parentAnswer);
	    System.out.println("****************************");
	    System.out.println("co === " + commentJsom);
	    
	    return ResponseEntity.ok().build();
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
	
	

	
	
}
