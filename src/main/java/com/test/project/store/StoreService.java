package com.test.project.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.project.DataNotFoundException;
import com.test.project.notice.Notice;
import com.test.project.notice.NoticeRepository;
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

   public Store getStore(Integer StoreId) {
      Optional<Store> store = this.storeRepository.findById(StoreId);
      if(store.isPresent()) {
         return store.get();
      }else {
         throw new DataNotFoundException("store not found");
      }
   }
	public void create(String storeName, String storeContent) {
		Store store = new Store();
		store.setStoreName(storeName);
		store.setStoreContent(storeContent);
		store.setCreateDate(LocalDateTime.now());
		this.storeRepository.save(store);
	}
	public void delete(Store store) {
		this.storeRepository.delete(store);
	}
	
	public void vote(Store store, SiteUser siteUser) {
		store.getVoter().add(siteUser);
		this.storeRepository.save(store);
	}
    public List<Store> getAllStore() {
        return storeRepository.findAll();
    }
	
    public int getTotalCount() {
        return (int) storeRepository.count(); // 전체 공지사항 수 반환
    }
    public void saveStore(String storeName,//식당이름
    		              String postcode,
    		              String basicAddress,//
    		              String detilAddress,
    					  double storeLatitude, 
    					  double storeLongitude,
    					  String storeContent,
    					  String kategorieGroup,
    					  String storeTagGroups,
    					  String storeNumber,
    					  String StoreStarttime,
    					  String StoreEndTime) {
        Store store = new Store();
        store.setStoreName(storeName);
        store.setPostcode(postcode);
        store.setBasicAddress(basicAddress);
        store.setDetilAddress(detilAddress);
        store.setStoreLatitude(storeLatitude);
        store.setStoreLongitude(storeLatitude);
        store.setStoreContent(storeContent);
        store.setKategorieGroup(kategorieGroup);
        store.setStoreTagGroups(storeTagGroups);
        store.setStoreNumber(storeNumber);
        store.setStoreStarttime(StoreStarttime);
        store.setStoreEndTime(StoreEndTime);
        store.setCreateDate(LocalDateTime.now()); // 현재 시간으로 설정

        storeRepository.save(store); // 데이터 저장
    }
}