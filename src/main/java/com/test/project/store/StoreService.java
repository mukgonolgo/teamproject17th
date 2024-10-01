package com.test.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.test.project.DataNotFoundException;
import com.test.project.review.Review;
import com.test.project.review.ReviewRepository;
import com.test.project.user.SiteUser;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class StoreService {

    @Autowired
    private final StoreRepository storeRepository;

	private static final String UPLOAD_DIR = "src/main/resources/static/img/store/";
    private final ReviewRepository reviewRepository;
    // 가게 정보 조회
    public Store getStore(Integer storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new DataNotFoundException("Store not found"));
    }
    // 특정 사용자가 좋아요를 누른 가게 리스트를 반환하는 메서드
    public List<Store> getStoresLikedByUser(SiteUser user) {
        return storeRepository.findByVoterContaining(user);
    }
    // 가게 저장 (SiteUser 추가)
    public Store saveStore(String storeName, String postcode, String basicAddress, String detailAddress,
                           double storeLatitude, double storeLongitude, String storeContent, String kategorieGroup,
                           String storeTagGroups, String storeNumber, String storeStarttime, String storeEndTime,
                           Boolean storeAdvertisement, SiteUser siteUser, boolean isPremium,MultipartFile imageFile) throws IOException {
        Store store = new Store();
        store.setStoreName(storeName);
        store.setPostcode(postcode);
        store.setBasicAddress(basicAddress);
        store.setDetailAddress(detailAddress);
        store.setStoreLatitude(storeLatitude);
        store.setStoreLongitude(storeLongitude);
        store.setStoreContent(storeContent);
        store.setKategorieGroup(kategorieGroup);
        store.setStoreTagGroups(storeTagGroups);
        store.setStoreNumber(storeNumber);
        store.setStoreStarttime(storeStarttime);
        store.setStoreEndTime(storeEndTime);
        store.setStoreAdvertisement(storeAdvertisement != null ? storeAdvertisement : false);
        store.setCreateDate(LocalDateTime.now());
        store.setSiteUser(siteUser);  // 사업자 정보 저장
        // 프리미엄 광고 여부에 따라 승인 상태 설정
        if (isPremium) {
            store.setApprovalStatus(4); // 프리미엄 승인 대기중
        } else {
            store.setApprovalStatus(1); // 일반 광고 승인 대기중
        }		
		if(!imageFile.isEmpty()) {
			//고유한 이미지 이름 생성
			String fileName = UUID.randomUUID().toString()+"_"+imageFile.getOriginalFilename();
			//파일 저장 경로
			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			//디렉토리가 없으면 생성
			Files.createDirectories(filePath.getParent());
			//파일 저장
			Files.write(filePath,imageFile.getBytes());
			//사용자 엔티티에 이미지 경로 설정
			store.setImageUrl("/img/store/"+fileName);
		}
        
        return storeRepository.save(store);
    
    }

    // 모든 가게 조회
    public List<Store> getAllStore() {
        return storeRepository.findAll();
    }

    public Store save(Store store) {
        return storeRepository.save(store);
    }
 // 가게 삭제 로직
    public void deleteStore(Integer storeId) {
        storeRepository.deleteById(storeId);  // 가게 ID를 기준으로 가게 삭제
    }
 // 모든 가게 조회 (페이지네이션 추가)
    public Page<Store> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable);
    }
    //좋아요 기능
    public void vote(Store store, SiteUser siteUser) {
        if (store.getVoter().contains(siteUser)) {
            // 이미 좋아요를 눌렀다면 삭제
            store.getVoter().remove(siteUser);
        } else {
            // 좋아요를 누르지 않았다면 추가
            store.getVoter().add(siteUser);
        }
        this.storeRepository.save(store);
    }



    public Page<Store> searchStoresByStoreName(String storeName, Pageable pageable) {
        return storeRepository.findByStoreNameContainingIgnoreCase(storeName, pageable);
    }

    public Page<Store> searchStoresByOwnerUsername(String username, Pageable pageable) {
        return storeRepository.findBySiteUser_UsernameContainingIgnoreCase(username, pageable);
    }
    public List<Store> searchStoresByStoreName(String storeName) {
        return storeRepository.findByStoreNameContainingIgnoreCase(storeName);
    }

    public List<Store> searchStoresByBasicAddress(String basicAddress) {
        return storeRepository.findByBasicAddressContainingIgnoreCase(basicAddress);
    }
    
    public List<Store> getTopStores(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createDate").descending());
        Page<Store> topStores = storeRepository.findAll(pageable);
        return topStores.getContent();
    }
    // StoreId로 스토어를 찾는 메서드 추가
    public Optional<Store> findById(Integer storeId) {
        return storeRepository.findById(storeId); // StoreRepository의 findById 메서드 호출
    }
    
//	별점 계산
    public double getStoreForstar(Integer storeId) {
        // 스토어에 해당하는 리뷰 조회
        List<Review> reviews = reviewRepository.findAllByStore_StoreId(storeId);
        
        // 리뷰의 평점 평균 계산
        return reviews.stream()
            .mapToDouble(review -> {
                try {
                    return Double.parseDouble(review.getRating()); // 문자열을 double로 변환
                } catch (NumberFormatException e) {
                    return 0.0; // 변환 실패 시 0.0 반환
                }
            })
            .average() // 평균 계산
            .orElse(0.0); // 리뷰가 없을 경우 0.0 반환
    }
    public long getReviewCountForstore(Integer storeId) {
	    // 스토어 객체를 먼저 조회
	    Store store = storeRepository.findById(storeId)
	        .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다. ID: " + storeId));

	    // 리뷰 객체로 댓글 수 계산
	    return reviewRepository.countByStore(store);
	}

	public Store getStoreByUser(SiteUser siteUser) {
		// TODO Auto-generated method stub
		return null;
	}

	

	public Store getStoreByOwner(SiteUser siteUser) {
		// TODO Auto-generated method stub
		return null;
	}

	 // 로그인한 사용자의 가게 리스트 반환
    public List<Store> getStoresByOwner(SiteUser siteUser) {
        return storeRepository.findBySiteUser(siteUser);
    }
    
}
