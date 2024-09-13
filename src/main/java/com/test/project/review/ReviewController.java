package com.test.project.review;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.img.ReviewImageRepository;
import com.test.project.review.tag.ReviewTag;
import com.test.project.review.tag.ReviewTagMap;
import com.test.project.review.tag.ReviewTagRepository;

import jakarta.transaction.Transactional;

@Controller
public class ReviewController {

    private final String uploadDirectory = "src/main/resources/static/img/upload";

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewTagRepository reviewTagRepository;

    @Autowired
    private ReviewImageRepository reviewImageRepository;

    @GetMapping("/review")
    public String reviewPage(Model model) {
        List<Review> reviewPage = reviewService.getAllReviews();
        model.addAttribute("reviewPage", reviewPage);
        return "review/review_page";
    }

    @GetMapping("/review_detail/{id}")
    public String reviewDetail(@PathVariable("id") Long id, Model model) {
        Review review = reviewService.findReviewById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        model.addAttribute("review", review);
        return "review/review_detail";
    }

    @GetMapping("/review_feed")
    public String reviewFeed() {
        return "review/review_feed";
    }

    @GetMapping("/review_create_form")
    public String reviewCreateForm() {
        return "review/review_create_form";
    }


    @GetMapping("/review/{id}")
    public String getReviewPage(@PathVariable Long id, Model model) {
        Review review = reviewService.getReview(id);
        model.addAttribute("review", review);
        return "review/review_page";
    }

    @GetMapping("/eat")
    public String eat() {
        return "eat_friends";
    }

    @GetMapping("/reservation_completed")
    public String reservationCompleted() {
        return "reservation_completed";
    }

    @GetMapping("/error_404")
    public String error404() {
        return "error_404";
    }

    @GetMapping("/error_500")
    public String error500() {
        return "error_500";
    }

    @GetMapping("/error_403")
    public String error403() {
        return "error_403";
    }

    @GetMapping("/error_401")
    public String error401() {
        return "error_401";
    }

    @GetMapping("/footer")
    public String footer() {
        return "footer";
    }

    @PostMapping("/review_create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createReview(
            @ModelAttribute Review review,
            @RequestParam(name = "fileUpload") List<MultipartFile> fileUpload,
            @RequestParam("tags_output") List<String> tags,
            @RequestParam("rating") String rating) throws IOException {

        // 파일 개수 로그 출력
        System.out.println("파일 개수: " + fileUpload.size());

        // 리뷰 정보 설정
        review.setCreateDate(LocalDateTime.now());
        review.setRating(rating);

        // 태그 처리
        reviewService.processTags(tags, review);

        // 이미지 처리
        List<ReviewImageMap> reviewImageMaps = reviewService.processImages(fileUpload, review);

        reviewRepository.save(review);

        // 응답으로 reviewId와 이미지 URL들을 포함한 JSON 반환
        Map<String, Object> response = new HashMap<>();
        response.put("reviewId", review.getId());
        response.put("imageUrls", reviewImageMaps.stream().map(map -> map.getReviewImage().getFilepath()).collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }
}