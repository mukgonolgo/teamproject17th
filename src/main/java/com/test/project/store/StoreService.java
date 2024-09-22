package com.test.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.test.project.DataNotFoundException;
import com.test.project.user.SiteUser;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StoreService {
    @Autowired
    private StoreRepository storeRepository;

    // 가게 정보 조회
    public Store getStore(Integer storeId) {
        Optional<Store> store = storeRepository.findById(storeId);
        if (store.isPresent()) {
            return store.get();
        } else {
            throw new DataNotFoundException("Store not found");
        }
    }

    // 가게 저장
    public Store saveStore(String storeName, String postcode, String basicAddress, String detailAddress,
                           double storeLatitude, double storeLongitude, String storeContent, String kategorieGroup,
                           String storeTagGroups, String storeNumber, String storeStarttime, String storeEndTime,
                           Boolean storeAdvertisement) {
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
        return storeRepository.save(store);
    }

    // 모든 가게 조회
    public List<Store> getAllStore() {
        return storeRepository.findAll();
    }
}
