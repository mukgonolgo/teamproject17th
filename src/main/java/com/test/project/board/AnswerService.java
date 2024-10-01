package com.test.project.board;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.project.DataNotFoundException;
import com.test.project.user.SiteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class AnswerService {

	@Autowired
	private AnswerRepository answerRepository;

	private static final Logger logger = LoggerFactory.getLogger(AnswerService.class);


	public Answer create(Board board, String content, SiteUser siteUser,Answer parentAnswer) {
		Answer answer = new Answer();
		
		answer.setBoard(board);
		answer.setUser(siteUser);
		answer.setUsername(board.getUsername());
		answer.setAnswerContent(content);
		answer.setAnswerCreateDate(LocalDateTime.now());	
		answer.setParentAnswer(parentAnswer);
		
		  logger.info("content 저장 전: {}", content);
		  
		  return answerRepository.save(answer); // 저장 호출 추가
	}
	
	public Answer createComment(Answer parentAnswer, String content, SiteUser user) {
	    Answer comment = new Answer();
	    comment.setAnswerContent(content);
	    comment.setUser(user);
	    comment.setParentAnswer(parentAnswer);
	    comment.setAnswerCreateDate(LocalDateTime.now());
	    parentAnswer.getComment().add(comment); // 대댓글 목록에 추가
	    return answerRepository.save(comment); // 대댓글 저장
	}

	//답변 조회
		public Answer getAnswer(Long id) {
			Optional<Answer> answer = this.answerRepository.findById(id);
			
			if(answer.isPresent()) {
				return answer.get();
			}else {
				throw new DataNotFoundException("답변이 없습니다.");
			}
		}
		
		//답변 수정
		public void modify(Answer answer, String content) {
			answer.setAnswerContent(content);		
			this.answerRepository.save(answer);
		}
		
		//답변 삭제
		public void delete(Answer answer) {
			this.answerRepository.delete(answer);
		}

		public Answer findById(Long id) {
			Optional<Answer> answer = this.answerRepository.findById(id);
			if(answer.isPresent()) {
				return answer.get();
			}else {
				throw new DataNotFoundException("answer not found(asv 64 line)");
			}
		}
		

	

}