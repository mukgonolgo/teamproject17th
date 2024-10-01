package com.test.project.board;



import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
   @GetMapping("/board/{id}")
   public String board(
         @PathVariable("id") Long id, 
         @AuthenticationPrincipal UserDetails userDetails,
         
         @RequestParam(value ="page", defaultValue = "0") int page,
         @RequestParam(value = "size", defaultValue = "5") int size,          
           @RequestParam(value="kw",defaultValue="") String kw,
           @RequestParam(value="searchType", defaultValue="") String SearchType,
         Model model,
         Principal principal){
         Pageable pageable = PageRequest.of(page, size);
         Page<Board> boardPage = boardService.getBoard(pageable);
         
//         List<Board> visiblePosts = boardPage.getContent().stream()
//                  .filter(post -> !post.isPrivate() || 
//                                  post.getUsername().equals(principal.getName()) || 
//                                  boardService.isAdmin(principal))
//                  .collect(Collectors.toList());
         
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
//         model.addAttribute("posts",visiblePosts);
         model.addAttribute("kw", kw);
         model.addAttribute("boardPage", boardPage);
         model.addAttribute("board", boardPage.getContent());
         model.addAttribute("currentPage", page);
         model.addAttribute("searchType", SearchType);
         model.addAttribute("totalPages", boardPage.getTotalPages());
         model.addAttribute("username", userDetails.getUsername());
         model.addAttribute("answerForm", new AnswerFormDTO());
         
         
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
         @Valid boardWriteDTO boardWriteDTO,
         BindingResult bindingResult,
         Principal principal,
         @AuthenticationPrincipal UserDetails userDetails,
         Model model,
         @RequestParam (value="is_Private",required = false, defaultValue = "false") boolean isPrivate
         ) {
      if(bindingResult.hasErrors()) {
          bindingResult.getAllErrors().forEach(error -> {
                  System.out.println(error.getDefaultMessage()); // 콘솔에 오류 메시지 출력
              });
         return "boardwrite";
      }
      setUserAttributes(model, userDetails);
      SiteUser siteuser = this.userService.getUser(principal.getName());
        boolean isPrivateResult = boardWriteDTO.isPrivate(); 
        System.out.println("DTO비밀글 설정확인"+ isPrivateResult);
        System.out.println("******************************************************************************");
      this.boardService.create
      (
            boardWriteDTO.getTag(),           
            boardWriteDTO.getTitle(),
            boardWriteDTO.getContent(),
      siteuser,
      principal,
      isPrivate
      //boardWriteDTO.isPrivate() DTO의 boolean은 항상 false기 떄문에 엔티티에서 직접 가져옴
      
      
      //SiteUser의 username만 가져오는중 다른코드와의 관계는 X
      );
       System.out.println("비밀글 설정확인(컨트로럴2)"+ isPrivate);
        System.out.println("******************************************************************************");
      
        System.out.println("DTO의 isPrivate 값: " + boardWriteDTO.isPrivate());
      return "redirect:/board/{id}";
      
      
      
   }
   
   
   
   //상세페이지
   @GetMapping("/board_detail/{id}")
   public String detail(
           @PathVariable("id") Long id,
           @AuthenticationPrincipal UserDetails userDetails,
           AnswerFormDTO answerFormDTO,
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
       public String questionrModify(@RequestBody @Valid AnswerFormDTO answerFormDTO, BindingResult bindingResult,
               @PathVariable("id") Long id, Principal principal, Model model) {
           if (bindingResult.hasErrors()) {
               return "";
           }
           Board board = this.boardService.findById(id);
           model.addAttribute("id", board.getBoardId());
           if (!board.getUser().getUsername().equals(principal.getName())) {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
           }
           this.boardService.modify(board, answerFormDTO.getContent());
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
    