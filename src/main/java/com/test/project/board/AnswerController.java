package com.test.project.board;

import java.security.Principal;
import java.util.Map;

import org.apache.catalina.User;
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

	private void setUserAttributes(Model model, @AuthenticationPrincipal UserDetails userDetails) {
		SiteUser user = userService.getUser(userDetails.getUsername());
		model.addAttribute("profileImage", user.getImageUrl());
		model.addAttribute("username", user.getUsername());
		model.addAttribute("userId", user.getId());
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{AnswerId}")
	public String createAnswer(Model model, @PathVariable("AnswerId") Long id, @Valid AnswerForm answerForm,
			BindingResult bindingResult, Principal principal, @AuthenticationPrincipal UserDetails userDetails) {
		// 현재 로그인 한 사용자의 정보를 알려면 스프링 시큐리티가 제공하는 Principal(본인) 객체를 사용해야 한다.Principal의
		// getName()를 이용하면 현재 로그인한 사용자ID를 알 수 있다.
		Board board = this.boardService.findById(id);
		SiteUser siteUser = this.userService.getUser(principal.getName());
		setUserAttributes(model, userDetails);

		if (bindingResult.hasErrors()) {
			model.addAttribute("board", board);
			return "board_detail";
		}
		Answer answer = this.answerService.create(board, answerForm.getContent(), siteUser);
		return String.format("redirect:/board_detail/%s#answer_%s", answer.getBoard().getBoardId(),
				answer.getAnswerId());
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
