package com.test.project.board;

import java.time.LocalDateTime;
import java.util.Optional;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.project.DataNotFoundException;
import com.test.project.user.SiteUser;

@Service
public class AnswerService {

	@Autowired
	private AnswerRepository answerRepository;

	public Answer create(Board board, String content, SiteUser siteUser) {
		Answer answer = new Answer();
		answer.setBoard(board);
		answer.setUser(siteUser);
		answer.setUsername(board.getUsername());
		answer.setAnswerContent(content);
		answer.setAnswerCreateDate(LocalDateTime.now());		
		this.answerRepository.save(answer);
		
		return answer;
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
				throw new DataNotFoundException("question not found");
			}
		}

		
	

}
