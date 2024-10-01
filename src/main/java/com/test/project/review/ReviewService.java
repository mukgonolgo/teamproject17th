package com.test.project.review;

import java.io.File;
import java.io.IOException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashSet;
import java.util.List;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.DataNotFoundException;
import com.test.project.review.comment.ReviewCommentRepository;

import com.test.project.review.img.ReviewImage;
import com.test.project.review.img.ReviewImageMap;
import com.test.project.review.img.ReviewImageMapRepository;
import com.test.project.review.img.ReviewImageRepository;
import com.test.project.review.like.ReviewLikeService;
import com.test.project.review.tag.ReviewTag;
import com.test.project.review.tag.ReviewTagMap;
import com.test.project.review.tag.ReviewTagRepository;
import com.test.project.user.SiteUser;
import com.test.project.user.UserRepository;

@Service
public class ReviewService {

	private final String uploadDirectory = "src/main/resources/static/img/upload";

	private final ReviewRepository reviewRepository;
	private final ReviewImageRepository reviewImageRepository;
	private final ReviewImageMapRepository reviewImageMapRepository;
	private final ReviewTagRepository reviewTagRepository;
	private final ReviewLikeService reviewLikeService;
	private final ReviewCommentRepository reviewCommentRepository;
	private final UserRepository userRepository;

	@Autowired
	public ReviewService(ReviewRepository reviewRepository, ReviewImageRepository reviewImageRepository,
			ReviewTagRepository reviewTagRepository, UserRepository userRepository, ReviewLikeService reviewLikeService,
			ReviewImageMapRepository reviewImageMapRepository, ReviewCommentRepository reviewCommentRepository) {
		this.reviewRepository = reviewRepository;
		this.reviewImageRepository = reviewImageRepository;
		this.reviewImageMapRepository = reviewImageMapRepository;
		this.reviewTagRepository = reviewTagRepository;
		this.userRepository = userRepository;
		this.reviewLikeService = reviewLikeService;
		this.reviewCommentRepository = reviewCommentRepository;
	}

	// ID로 리뷰 조회
	public Optional<Review> findReviewById(Long id) {
		return reviewRepository.findById(id); // 리뷰 ID로 특정 리뷰를 조회
	}

	// 특정 ID의 리뷰를 조회하고, 리뷰가 없으면 예외 발생
	public Review getReview(Long id) {
		return reviewRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Review not found"));
	}

	// 모든 리뷰를 조회하는 메서드
	public List<Review> getAllReviews() {
		return reviewRepository.findAll(); // 모든 리뷰를 리스트로 반환
	}

	// 현재 로그인한 사용자의 ID를 가져오는 메서드
	public Long getCurrentUserId() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) principal;
			SiteUser user = userRepository.findByUsername(userDetails.getUsername())
					.orElseThrow(() -> new DataNotFoundException("해당 사용자를 찾을 수 없습니다."));
			return user.getId(); // 로그인한 사용자의 ID 반환
		}
		return null; // 로그인되지 않은 경우 null 반환
	}

	// 사용자 ID로 리뷰 조회
	public List<Review> getReviewsByUserId(Long userId) {
		return reviewRepository.findByUserId(userId); // 특정 사용자 ID에 해당하는 리뷰 조회
	}

	// 리뷰 ID로 사용자 ID를 가져오는 메서드
	public Long getUserIdByReviewId(Long reviewId) {
		return reviewRepository.findById(reviewId)
				.map(review -> review.getUser() != null ? review.getUser().getId() : null).orElse(null); // 리뷰에 연결된 사용자
																											// ID를 반환
	}

	// 리뷰 ID로 특정 리뷰 조회, 없으면 예외 발생
	public Review findById(Long id) {
		return reviewRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + id));
	}

	// 모든 리뷰와 해당 리뷰의 좋아요 수 및 사용자의 좋아요 여부를 반환하는 메서드
	public List<ReviewDto> getAllReviewsWithLikes(Long currentUserId) {
		List<Review> reviews = reviewRepository.findAll(); // 모든 리뷰를 가져옴
		List<ReviewDto> reviewDtos = new ArrayList<>();

		for (Review review : reviews) {
			Long likeCount = reviewLikeService.countLikes(review.getId()); // 각 리뷰의 좋아요 수 조회
			boolean likedByUser = reviewLikeService.isLikedByUser(review.getId(), currentUserId); // 사용자가 좋아요를 눌렀는지 확인
			ReviewDto reviewDto = new ReviewDto(review); // Review 객체를 ReviewDto로 변환
			reviewDto.setLikeCount(likeCount); // 좋아요 수 설정
			reviewDto.setLikedByUser(likedByUser); // 사용자의 좋아요 여부 설정
			reviewDtos.add(reviewDto); // DTO 리스트에 추가
		}

		return reviewDtos; // 변환된 리뷰 목록 반환
	}

	// 리뷰 삭제
	public void deleteReview(Long id) {
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Review not found")); // ID로 리뷰 조회, 없으면 예외 발생
		reviewRepository.delete(review); // 리뷰 삭제
	}

	// 리뷰 업데이트 처리 메서드
	@Transactional
	public void updateReview(Long id, Review updatedReview, List<MultipartFile> fileUpload, String existingImages,
			List<String> tags, String rating) throws IOException {
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + id)); // ID로 리뷰 조회

		review.setTitle(updatedReview.getTitle()); // 리뷰 제목 업데이트
		review.setContent(updatedReview.getContent()); // 리뷰 내용 업데이트
		review.setRating(rating); // 리뷰 평점 업데이트

		processTags(tags, review); // 태그 처리

		if (fileUpload != null && !fileUpload.isEmpty()) {
			processImages(fileUpload, review, existingImages); // 이미지 처리
		}

		review.setUpdatedAt(LocalDateTime.now()); // 리뷰 수정 시간 설정
		reviewRepository.save(review); // 리뷰 저장
	}

	// 리뷰에 업로드된 이미지를 처리하는 메서드
	@Transactional
	public List<ReviewImageMap> processImages(List<MultipartFile> imageFiles, Review review, String existingImages)
			throws IOException {
		List<ReviewImageMap> reviewImageMaps = new ArrayList<>();
		String imagePath = System.getProperty("user.dir") + "/src/main/resources/static/img/upload/";

		for (MultipartFile file : imageFiles) {
			if (file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
				continue; // 파일이 비어있으면 건너뜀
			}

			// UUID로 중복 방지
			String filenameWithUUID = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
			File dest = new File(imagePath + filenameWithUUID);

			try {
				file.transferTo(dest); // 파일 저장
			} catch (IOException e) {
				continue; // 파일 저장 실패 시 건너뜀
			}

			// ReviewImage 객체 생성 및 저장
			ReviewImage image = new ReviewImage();
			image.setFilename(filenameWithUUID);
			image.setFilepath("/img/upload/" + filenameWithUUID);
			reviewImageRepository.save(image); // 이미지 DB에 저장

			// 리뷰와 이미지 매핑 생성
			ReviewImageMap imageMap = new ReviewImageMap();
			imageMap.setReview(review);
			imageMap.setReviewImage(image);
			reviewImageMaps.add(imageMap);
		}

		// 기존 이미지와 새 이미지 처리
		Set<String> existingImagesSet = new HashSet<>();
		if (existingImages != null && !existingImages.isEmpty()) {
			existingImagesSet = new HashSet<>(Arrays.asList(existingImages.split(",")));
		}

		List<ReviewImageMap> currentImageMaps = review.getReviewImageMap();
		List<ReviewImageMap> imagesToRemove = new ArrayList<>();

		for (ReviewImageMap imageMap : currentImageMaps) {
			String fullPath = imageMap.getReviewImage().getFilepath();
			if (!existingImagesSet.contains(fullPath)) {
				File fileToDelete = new File(System.getProperty("user.dir") + "/src/main/resources/static" + fullPath);
				if (fileToDelete.exists()) {
					fileToDelete.delete(); // 이미지 파일 삭제
				}
				imagesToRemove.add(imageMap); // 삭제할 이미지 매핑 추가
			}
		}

		currentImageMaps.removeAll(imagesToRemove); // 기존 이미지에서 삭제
		currentImageMaps.addAll(reviewImageMaps); // 새로운 이미지 추가
		reviewRepository.save(review); // 리뷰 저장

		return reviewImageMaps;
	}

	
	
	
	// 리뷰에 태그를 처리하는 메서드
	@Transactional
	public void processTags(List<String> tags, Review review) {
		Set<ReviewTagMap> existingTagMaps = review.getTagMaps(); // 기존 태그 맵
		Set<String> existingTags = existingTagMaps.stream().map(tagMap -> tagMap.getReviewTag().getName())
				.collect(Collectors.toSet()); // 기존 태그 이름 목록

		Set<String> newTags = new HashSet<>(tags); // 새로운 태그 목록

		// 삭제할 태그 찾기
		Set<ReviewTagMap> tagsToRemove = existingTagMaps.stream()
				.filter(tagMap -> !newTags.contains(tagMap.getReviewTag().getName())).collect(Collectors.toSet());

		// 태그 삭제
		for (ReviewTagMap tagMap : tagsToRemove) {
			review.getTagMaps().remove(tagMap); // 리뷰와의 연결을 끊음
			reviewTagRepository.delete(tagMap.getReviewTag()); // 태그 삭제
		}

		// 새로 추가할 태그 찾기
		Set<String> tagsToAdd = newTags.stream().filter(tag -> !existingTags.contains(tag)).collect(Collectors.toSet());

		// 새 태그 추가
		int orderIndex = existingTagMaps.size();
		for (String newTag : tagsToAdd) {
			ReviewTag tag = reviewTagRepository.findByName(newTag);
			if (tag == null) {
				tag = new ReviewTag();
				tag.setName(newTag);
				reviewTagRepository.save(tag);
			}

			ReviewTagMap tagMap = new ReviewTagMap();
			tagMap.setReviewTag(tag);
			tagMap.setReview(review);
			tagMap.setOrderIndex(orderIndex++);
			review.getTagMaps().add(tagMap);
		}

		reviewRepository.save(review); // 리뷰 저장
	}

	// 리뷰 ID로 댓글 수 조회
	public long getCommentCountForReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + reviewId)); // 리뷰 조회

		return reviewCommentRepository.countByReview(review); // 리뷰에 달린 댓글 수 반환
	}

	// 제목이나 내용에 검색어가 포함된 리뷰를 검색
	public List<Review> searchReviews(String query) {
		return reviewRepository.findByTitleContainingOrContentContaining(query, query); // 제목 또는 내용에서 검색어로 리뷰 검색
	}
	
	public List<Review> getRecentReviews(int limit) {
	    Pageable pageable = PageRequest.of(0, limit, Sort.by("createDate").descending());
	    return reviewRepository.findAll(pageable).getContent();
	}

	
	   public List<Review> getReviewsByRegion(String region) {
	        // 지역 정보가 포함된 리뷰를 검색
	        return reviewRepository.findByStoreBasicAddressContaining(region);
	    }


}
