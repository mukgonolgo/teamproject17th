package com.test.project.review;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;

@Controller
public class ReviewController {

	// 파일 업로드 디렉터리
	private final String uploadDirectory = "src/main/resources/static/img/upload";

	// ReviewService를 주입받기 위한 필드
	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ReviewRepository reviewRepository; // ReviewRepository 주입
	
	@Autowired
	private ReviewTagRepository reviewTagRepository;

	@GetMapping("/review")
	public String reviewPage(Model model) {
        List<Review> reviewPage = reviewService.getAllReviews();
        model.addAttribute("reviewPage", reviewPage);
		return "review/review_page";
	}

	@GetMapping("/review_detail/{id}")
	public String reviewDetail(@PathVariable("id") Long id, Model model) {
        Review review = reviewService.findById(id);
        model.addAttribute("review", review);
		return "review/review_detail";
	}

	// 리뷰 피드 페이지
	@GetMapping("/review_feed")
	public String review_feed() {
		return "review/review_feed";
	}

	@GetMapping("/review_create_form")
	public String review_create_form() {
		return "review/review_create_form";
	}
	
	
	@PostMapping("/review_create")
	@Transactional
	public String createReview(
	        @ModelAttribute Review review, 
	        @RequestParam("fileUpload") List<MultipartFile> imageFiles,
	        @RequestParam("tags_output") List<String> tags,
	        @RequestParam("rating") String rating) {

	    // 파일 처리
	    if (imageFiles != null && !imageFiles.isEmpty()) {
	        for (MultipartFile file : imageFiles) {
	            if (!file.isEmpty()) {
	                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	                Path path = Paths.get(uploadDirectory);

	                if (!Files.exists(path)) {
	                    try {
	                        Files.createDirectories(path);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    }
	                }

	                Path filePath = Paths.get(uploadDirectory, fileName);
	                System.out.println("Saving file to: " + filePath.toAbsolutePath());
	                try {
	                    Files.copy(file.getInputStream(), filePath);
	                    review.setImageUrl(fileName);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }

	    review.setRating(rating);
	    review.setCreateDate(LocalDateTime.now());

	    // 태그 처리
	    Set<ReviewTagMap> reviewTagMaps = new HashSet<>();
	    for (int i = 0; i < tags.size(); i++) {
	        String tagName = tags.get(i);
	        ReviewTag tag = reviewTagRepository.findByName(tagName);
	        if (tag == null) {
	            tag = new ReviewTag();
	            tag.setName(tagName);
	            reviewTagRepository.save(tag);
	        }

	        ReviewTagMap tagMap = new ReviewTagMap();
	        tagMap.setReviewTag(tag);
	        tagMap.setReview(review);
	        tagMap.setOrderIndex(i); // 태그의 순서 설정

	        reviewTagMaps.add(tagMap);
	    }

	    review.setTagMaps(reviewTagMaps);

	    // 데이터베이스에 리뷰 저장
	    reviewRepository.save(review);

	    return "redirect:/review_detail/" + review.getId();
	}


	@GetMapping("/review/{id}")
	public String getReviewPage(@PathVariable Long id, Model model) {
		Review review = reviewService.getReview(id);
		model.addAttribute("review", review);
		return "review/review_page"; // Thymeleaf 템플릿 이름
	}

	// 밥친구 페이지
	@GetMapping("/eat")
	public String eat() {
		return "eat_friends";
	}

	@GetMapping("/reservation_completed")
	public String reservation_completed() {
		return "/reservation_completed";
	}

	// 에러페이지
	@GetMapping("/error_404")
	public String error_404() {
		return "/error_404";
	}

	@GetMapping("/error_500")
	public String error_500() {
		return "/error_500";
	}

	@GetMapping("/error_403")
	public String error_403() {
		return "/error_403";
	}

	@GetMapping("/error_401")
	public String error_401() {
		return "/error_401";
	}

	@GetMapping("/footer")
	public String bord() {
		return "footer";
	}

}