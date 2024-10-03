package com.test.project.board;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AnswerFormDTO {
	private List<Answer> comment;
    private String content; 
}