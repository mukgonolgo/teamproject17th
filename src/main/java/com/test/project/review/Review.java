package com.test.project.review;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 100) //글자의 갯수를 100개로 지정
	private String subject;
	
	@Column(columnDefinition = "TEXT") //글자의 갯수를 무한대
	private String content;
	
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column
    private String tag;
    
    @Column
    private String rating;

	
	private LocalDateTime createDate;//DB에서는 crate_date로 만들어짐

}
