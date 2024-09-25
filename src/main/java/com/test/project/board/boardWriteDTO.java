package com.test.project.board;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class boardWriteDTO  {

	private String tag = "일반";
	
	private String imageFile;
	
	@NotEmpty(message="제목을 입력해주세요")
	@Size(max=50)
	private String title;
	
	@NotEmpty(message="본문을 입력해주세요")
	private String content;
	
    private boolean isPrivate;
    
}
