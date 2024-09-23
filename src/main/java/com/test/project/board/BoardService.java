package com.test.project.board;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	
	
	public Page<Board> getBoard(Pageable pageable) {
	return boardRepository.findAll(pageable);
	
	}
	
	public List<Answer> getAnswer(Integer id){
		return answerRepository.findAll();
	}

	
	public void create(String tag,String image, String content, String title, SiteUser user, Principal principal, boolean is_private) {
		   if (user == null) {
		        System.out.println("User is null!");
		    } else {
		        System.out.println("User: " + user.getUsername());
		    }
		   
		   
		   SiteUser siteUser = this.userService.getUser(principal.getName());
		Board b = new Board();
		b.setBoardTag(tag);
		b.setBoardCreateDate(LocalDateTime.now());
		b.setBoardImage(image);		
		b.setBoardContent(content);
		b.setBoardTitle(title);
		b.setUser(user);
		b.setUsername(user.getUsername()); // 여기에 추가
		b.setPrivate(is_private);
		boardRepository.save(b);
		System.out.println("****************비밀글 확인(서비스)" + is_private +"************************");
		//id는 자동
	}
	
	 public Page<Board> getList(int page){
		 Pageable pageable = PageRequest.of(page, 5);
		 return this.boardRepository.findAll(pageable);
	 }

	public Board findById(Long id) {
		Optional<Board> question = this.boardRepository.findById(id);
		if(question.isPresent()) {
			return question.get();
		}else {
			throw new DataNotFoundException("question not found");
		}
	}

	public void modify(Board board, String content) {
		board.setBoardContent(content);
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
