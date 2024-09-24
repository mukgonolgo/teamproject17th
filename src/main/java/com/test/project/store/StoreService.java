package com.test.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.test.project.DataNotFoundException;
import com.test.project.user.SiteUser;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreService {

    @Autowired
    private final StoreRepository storeRepository;

    // 가게 정보 조회
    public Store getStore(Integer storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new DataNotFoundException("Store not found"));
    }

    // 가게 저장 (SiteUser 추가)
    public Store saveStore(String storeName, String postcode, String basicAddress, String detailAddress,
                           double storeLatitude, double storeLongitude, String storeContent, String kategorieGroup,
                           String storeTagGroups, String storeNumber, String storeStarttime, String storeEndTime,
                           Boolean storeAdvertisement, SiteUser siteUser, boolean isPremium) {
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

    // 사업자 이름으로 가게 검색
    public Page<Store> searchStoresByOwnerUsername(String username, Pageable pageable) {
        return storeRepository.findBySiteUserUsernameContaining(username, pageable);
    }
    
}