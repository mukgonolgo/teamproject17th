package com.test.project.board;



import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
 // 게시판
    @GetMapping("/board")
    public String board(
          @RequestParam(value = "page", defaultValue = "0") int page,
          @RequestParam(value = "size", defaultValue = "5") int size,
          @RequestParam(value = "kw", defaultValue = "") String kw,
          @RequestParam(value = "searchType", defaultValue = "") String searchType,
          Model model,
          Principal principal // Principal을 사용하여 인증된 사용자 정보에 접근
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boardPage = boardService.getBoard(pageable);

        // 검색 처리
        if (kw != null && !kw.isEmpty()) {
            if (searchType.equalsIgnoreCase("title")) {
                boardPage = boardService.getBoardByKeywordInTitle(kw, pageable);
            } else if (searchType.equalsIgnoreCase("content")) {
                boardPage = boardService.getBoardByKeywordInContent(kw, pageable);
            } else if (searchType.equalsIgnoreCase("username")) {
                boardPage = boardService.getBoardByKeywordInUsername(kw, pageable);
            } else {
                boardPage = boardService.getBoard(pageable);
            }
        }

        // 사용자 정보 설정 (UserDetails가 null일 수 있음)
        if (principal != null) {
            String username = principal.getName();
            model.addAttribute("username", username);
            // UserDetails를 사용하는 경우
            UserDetails userDetails = (UserDetails) ((Authentication) principal).getPrincipal();
            setUserAttributes(model, userDetails);
        }

        // 모델에 필요한 데이터 추가
        model.addAttribute("kw", kw);
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("board", boardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("totalPages", boardPage.getTotalPages());
        model.addAttribute("boardDTO", new BoardDTO());
        model.addAttribute("answerFormDTO", new AnswerFormDTO());

        return "board/board";
    }

   


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/boardwrite/{id}")
    public String boardwrite(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        // id가 null이거나 유효하지 않은 경우 로그인 페이지로 리다이렉트
        if (id == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        setUserAttributes(model, userDetails);
        return "board/boardwrite"; 
    }

    @PostMapping("/boardwrite/{id}")
    public String boardwrite(
            @Valid boardWriteDTO boardWriteDTO,
            BindingResult bindingResult,
            @PathVariable("id") Long id,
            Principal principal,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(value = "is_Private", required = false, defaultValue = "false") boolean isPrivate) {

        // id가 null이거나 유효하지 않은 경우 로그인 페이지로 리다이렉트
        if (id == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }

        // 입력값 검증
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.getDefaultMessage()); // 콘솔에 오류 메시지 출력
            });
            return "board/boardwrite"; // 오류가 있는 경우, 작성 페이지로 돌아감
        }

        // 사용자 정보 세팅
        setUserAttributes(model, userDetails);
        SiteUser siteuser = this.userService.getUser(principal.getName());

        // 게시글 생성
        Long newBoardId = this.boardService.create(
                boardWriteDTO.getTag(),
                boardWriteDTO.getTitle(),
                boardWriteDTO.getContent(),
                siteuser,
                principal, // Principal 인자를 추가
                isPrivate
        );

        // 로그 출력
        System.out.println("비밀글 설정확인(컨트롤러): " + isPrivate);
        System.out.println("DTO의 isPrivate 값: " + boardWriteDTO.isPrivate());

        // 게시글 상세 페이지로 리다이렉트
        return "redirect:/board_detail/" + newBoardId;
    }

   
   //상세페이지
   @GetMapping("/board_detail/{id}")
   public String detail(
           @PathVariable("id") Long id,
           @AuthenticationPrincipal UserDetails userDetails,
           BoardDTO boardDTO,
           AnswerFormDTO answerFormDTO,
           Model model           
   ) {   
	   
          if(userDetails != null) {
          setUserAttributes(model, userDetails);          
      }
          System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+id);
      Board board = this.boardService.findById(id);
      model.addAttribute("board",board);
      model.addAttribute("answerFormDTO", answerFormDTO);
       return "board/board_detail"; // board_detail.html로 이동
   }
   
//글 수정
    @PreAuthorize("isAuthenticated()")
       @PostMapping("/question/modify/{id}")
    @ResponseBody
       public String questionrModify(@RequestBody @Valid BoardDTO boardDTO, BindingResult bindingResult,
               @PathVariable("id") Long id, Principal principal, Model model) {
           if (bindingResult.hasErrors()) {
               return "";
           }
           Board board = this.boardService.findById(id);
           model.addAttribute("id", board.getBoardId());
           if (!board.getUser().getUsername().equals(principal.getName())) {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
           }
           this.boardService.modify(board, boardDTO.getContent());
           return String.format("redirect:/board_detail/%s", board.getBoardId());
       }
   
 // 제목 수정
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/Titlemodify/{id}")
    @ResponseBody
    public String questionModify(
            @RequestBody @Valid BoardDTO boardDTO, // AnswerFormDTO 대신 BoardDTO 사용
            BindingResult bindingResult,
            @PathVariable("id") Long id,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            return ""; // 오류가 있을 경우 빈 문자열 반환
        }

        Board board = this.boardService.findById(id); // 해당 게시물 찾기

        // 현재 사용자와 게시물 작성자 비교
        if (!board.getUser().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        // 제목 수정
        this.boardService.modifyTitle(board, boardDTO.getTitle()); // 제목으로 변경하는 메서드 호출
        return String.format("redirect:board/board_detail/%s", board.getBoardId()); // 수정 후 리다이렉트
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
      return "redirect:/board";
   }



   @GetMapping("/posts")
   @ResponseBody
   public ResponseEntity<List<Board>> getPostsByTag(
           @RequestParam(value="boardTag", defaultValue = "") String boardTag,
           @RequestParam(value ="page", defaultValue = "1") int page,
           @RequestParam(value = "size", defaultValue = "5") int size
           
   ) {
       System.out.println("Received boardTag: " + boardTag);
       Pageable pageable = PageRequest.of(page - 1, size);
       
       if (boardTag.trim().isEmpty()) {
           // boardTag가 빈 문자열일 경우 모든 게시글을 반환
           List<Board> allBoards = boardService.getBoard(pageable).getContent(); // Page를 List로 변환
           return ResponseEntity.ok(allBoards);
       } else {
           List<Board> boards = boardService.findByTag(boardTag);
           return ResponseEntity.ok(boards);
       }
   }
}
    