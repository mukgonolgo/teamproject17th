package com.test.project.store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.test.project.reservation.Reservation;
import com.test.project.user.SiteUser;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer storeId;

    @Column(length = 50)
    private String storeName;

    @Column(columnDefinition = "TEXT")
    private String postcode;

    @Column(columnDefinition = "TEXT")
    private String basicAddress;

    @Column(columnDefinition = "TEXT")
    private String detailAddress;

    private double storeLatitude;
    private double storeLongitude;

    @Column(columnDefinition = "TEXT")
    private String storeContent;

    private LocalDateTime createDate;
    private boolean storeAdvertisement;
    private String kategorieGroup;
    private String storeTagGroups;
    private String storeNumber;

    @Column(length = 5) // HH:MM 형식으로 제한
    private String storeStarttime;

    @Column(length = 5) // HH:MM 형식으로 제한
    private String storeEndTime;
    
    @ManyToMany
    private Set<SiteUser> voter;
    
	@OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE) //질문이 삭제되면 관련 답변도 모두 삭제하겠다.
	private List<Reservation> reservationList;

    @ManyToOne
    @JoinColumn(name = "user_id")  // SiteUser와 연관 관계 설정
    private SiteUser siteUser;     // 글을 작성한 사업자

    private LocalDateTime modifyDate;

    // 승인 상태 필드 (1: 일반 광고 승인 대기 중, 2: 일반 광고 승인, 3: 일반 광고 보류, 4: 프리미엄 승인 대기 중, 5: 프리미엄 허용, 6: 프리미엄 보류)
    @Column(nullable = false)
    private Integer approvalStatus = 1; // 기본값: 일반 광고 승인 대기 중
    
    
}
