package com.test.project.board;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoardDTO {
	
	private String content;
	
	private String title;
	
	  private Long parentId; // 부모 답변 ID 추가



	  
}