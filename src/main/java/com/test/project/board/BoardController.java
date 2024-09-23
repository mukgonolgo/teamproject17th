package com.test.project.board;



import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class BoardController {

	private final BoardService boardService;
	private final UserService userService;
	private final AnswerService answerService;


    private void setUserAttributes(Model model, UserDetails userDetails) {
        SiteUser user = userService.getUser(userDetails.getUsername());
        model.addAttribute("profileImage", user.getImageUrl());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userId", user.getId());
    }
    
    
    //게시판
	@GetMapping("/board/{id}")
	public String board(
			@PathVariable("id") Long id, 
			@AuthenticationPrincipal UserDetails userDetails,
			
			@RequestParam(value ="page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "5") int size,	       
	        @RequestParam(value="kw",defaultValue="") String kw,
	        @RequestParam(value="searchType", defaultValue="") String SearchType,
			Model model){
		
			Pageable pageable = PageRequest.of(page-1, size);
			Page<Board> boardPage = boardService.getBoard(pageable);
			
			if(kw != null && !kw.isEmpty()) {
				if(SearchType.equalsIgnoreCase("title")) {
					boardPage= boardService.getBoardByKeywordInTitle(kw, pageable);
				}else if(SearchType.equalsIgnoreCase("content")) {
					boardPage= boardService.getBoardByKeywordInUsername(kw, pageable);
				}else if(SearchType.equals("username")) {//유저명은 대소문자 구분으로?
					boardPage= boardService.getBoardByKeywordInUsername(kw, pageable);
				}else {
					boardPage=boardService.getBoard(pageable);
				}
			}else {
				boardPage= boardService.getBoard(pageable);
				
			}
			setUserAttributes(model, userDetails);
			model.addAttribute("kw", kw);
		    model.addAttribute("boardPage", boardPage);
			model.addAttribute("board", boardPage.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("searchType", SearchType);
			model.addAttribute("totalPages", boardPage.getTotalPages());
		return "board";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/boardwrite/{id}")
	public String boardwrite(
			@PathVariable("id") Long id, 
			@AuthenticationPrincipal UserDetails userDetails,
			Model model)
	{
		setUserAttributes(model, userDetails);
	    return "boardwrite"; 
	}
	
	//글 생성
	@PostMapping("/boardwrite/{id}")
	public String boardwrite(
			@Valid boardwriteDTO boardwriteDTO,
			BindingResult bindingResult,
			Principal principal,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model
			) {
		if(bindingResult.hasErrors()) {
			 bindingResult.getAllErrors().forEach(error -> {
		            System.out.println(error.getDefaultMessage()); // 콘솔에 오류 메시지 출력
		        });
			return "boardwrite";
		}
		setUserAttributes(model, userDetails);
		SiteUser siteuser = this.userService.getUser(principal.getName());
		this.boardService.create
		(
		boardwriteDTO.getTag(),
		boardwriteDTO.getImageFile(),	
		boardwriteDTO.getTitle(),
		boardwriteDTO.getContent(),
		siteuser, principal
		
		//SiteUser의 username만 가져오는중 다른코드와의 관계는 X
		);
	
		
		return "redirect:/board/{id}";
		
		
		
	}
	
	
	
	//상세페이지
	@GetMapping("/board_detail/{id}")
	public String detail(
	        @PathVariable("id") Long id,
	        @AuthenticationPrincipal UserDetails userDetails,
	        AnswerForm answerForm,
	        Model model	        
	) {	
	    	if(userDetails != null) {
		    setUserAttributes(model, userDetails);		    
		}	    		    
		Board board = this.boardService.findById(id);
		model.addAttribute("board",board);		
	    return "board_detail"; // board_detail.html로 이동
	}
	

	 @PreAuthorize("isAuthenticated()")
	    @PostMapping("/question/modify/{id}")
	 @ResponseBody
	    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
	            @PathVariable("id") Long id, Principal principal, Model model) {
	        if (bindingResult.hasErrors()) {
	            return "";
	        }
	        Board board = this.boardService.findById(id);
	        model.addAttribute("id", board.getBoardId());
	        if (!board.getUser().getUsername().equals(principal.getName())) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
	        }
	        this.boardService.modify(board, answerForm.getContent());
	        return String.format("redirect:/board_detail/%s", board.getBoardId());
	    }
	
	
	//글 삭제
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/question/delete/{id}")
	public String delete(
			@PathVariable("id") Long id,
			@AuthenticationPrincipal UserDetails userDetails,
			Model model,
			Principal principal
			) {
		Board board = this.boardService.findById(id);
		setUserAttributes(model, userDetails);
        if (!board.getUser().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
		
        this.boardService.delete(board);
        model.addAttribute("board",board);
		/*
		 * return String.format("redirect:/question/detail/%s", board.getAnswerList());
		 */
		return "redirect:/board/{id}";
	}



	@GetMapping("/posts")
	@ResponseBody
	public ResponseEntity<List<Board>> getPostsByTag(@RequestParam(value="boardTag") String boardTag) {
		 System.out.println("Received boardTag: " + boardTag);
	    if (boardTag == null || boardTag.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body(Collections.emptyList());
	    }
	    
	    List<Board> boards = boardService.findByTag(boardTag);
	    
	    if (boards.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
	    }
	    
	    return ResponseEntity.ok(boards);
	}
	
    }
 	


