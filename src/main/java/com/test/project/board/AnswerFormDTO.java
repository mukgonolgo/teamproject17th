package com.test.project.board;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AnswerFormDTO {
	@NotEmpty(message="답변내용을 입력하셔야 합니다.")
	private String content;
	
	
	private List<Answer> comment;
	
	  private Long parentId; // 부모 답변 ID 추가


	  
}