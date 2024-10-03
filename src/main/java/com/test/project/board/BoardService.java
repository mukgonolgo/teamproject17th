package com.test.project.board;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.test.project.DataNotFoundException;
import com.test.project.user.SiteUser;
import com.test.project.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardService {
   

   private final BoardRepository boardRepository;
   private final AnswerRepository answerRepository;
   private final UserService userService;
   
   private static final Logger logger = LoggerFactory.getLogger(AnswerService.class);
   
   public Page<Board> getBoard(Pageable pageable) {
   return boardRepository.findAll(pageable);
   
   }
   
   public List<Answer> getAnswer(Integer id){
      return answerRepository.findAll();
   }

   
   public Long create(String tag, String title,String content, SiteUser user, Principal principal, boolean isPrivate) {
          System.out.println("서비스에서 받은 isPrivate 값: " + isPrivate);
         if (user == null) {
              System.out.println("User is null!");
          } else {
              System.out.println("User: " + user.getUsername());
          }
         
         
         SiteUser siteUser = this.userService.getUser(principal.getName());
      Board b = new Board();
      b.setBoardTag(tag);
      b.setBoardCreateDate(LocalDateTime.now());            
      b.setBoardTitle(title);
      b.setBoardContent(content);
      b.setUser(user);
      b.setUsername(user.getUsername()); // 여기에 추가
      b.setPrivate(isPrivate);
      
      System.out.println("저장 전 Board 객체: " + b.toString()); // 객체 전체 출력 로그
      Board savedBoard = boardRepository.save(b);
      boardRepository.save(b);
      System.out.println("****************비밀글 확인(서비스)" + isPrivate +"************************");
      //id는 자동
      return savedBoard.getBoardId(); // 새로 생성된 게시글의 ID를 반환
      }
   
    public Page<Board> getList(int page){
       Pageable pageable = PageRequest.of(page, 5);
       return this.boardRepository.findAll(pageable);
    }

   public Board findById(Long id) {
      Optional<Board> board = this.boardRepository.findById(id);
      logger.info("findbyID(bsv 79line) 저장 전: {}", id);
		
      if(board.isPresent()) {
         return board.get();
      }else {
         throw new DataNotFoundException("boardid not found(boardService 84 line)");
      }
   }

   public void modify(Board board, String content) {
      board.setBoardContent(content);
      System.out.println("수정할 content 값: " + content); // 추가된 로깅
      
      this.boardRepository.save(board);
      
   }


   public void modifyTitle(Board board, String title) {
      board.setBoardTitle(title);
      System.out.println("수정할 title 값: " + title); // 추가된 로깅
      
      this.boardRepository.save(board);
      
   }
   public void delete(Board board) {
      this.boardRepository.delete(board);
      
   }

   public Page<Board> getList(int page, String kw) {
      //최신순으로 정렬
      List<Sort.Order> sorts= new ArrayList<>();
      sorts.add(Sort.Order.desc("boardCreateDate"));
      Pageable pageable = PageRequest.of(page, 5,Sort.by(sorts));
      return this.boardRepository.findAllByKeyword(kw, pageable);
   }
   

    public Page<Board> getBoardByKeyword(String kw, Pageable pageable) {
        return boardRepository.findAllByKeyword(kw, pageable);
    }


    public Page<Board> getBoardByKeywordInTitle(String keyword, Pageable pageable) {
        return boardRepository.findByBoardTitleContainingIgnoreCase(keyword, pageable);
    }

    public Page<Board> getBoardByKeywordInContent(String keyword, Pageable pageable) {
        return boardRepository.findByBoardContentContainingIgnoreCase(keyword, pageable);
    }

    public Page<Board> getBoardByKeywordInUsername(String keyword, Pageable pageable) {
        return boardRepository.findByUser_UsernameContainingIgnoreCase(keyword, pageable);
    }

   public List<Board> findByTag(String boardTag) {
      return boardRepository.findByBoardTag(boardTag);
   }
   
   public boolean isAdmin(Principal principal) {
      if(principal==null) {
         return false;
      }
      
      UserDetails userDetails = (UserDetails) ((Authentication) principal).getPrincipal();
      return userDetails.getAuthorities().stream()
            .anyMatch(GrantedAuthority -> GrantedAuthority.getAuthority().equals("ROLE_ADMIN"));
      
   }

   
}