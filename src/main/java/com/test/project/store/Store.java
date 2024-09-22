package com.test.project.store;

import java.time.LocalDateTime;
import java.util.Set;
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
    private String detailAddress;  // 오타 수정: detil -> detail

    private double storeLatitude;
    private double storeLongitude;

    @Column(columnDefinition = "TEXT")
    private String storeContent;

    private LocalDateTime createDate;
    private boolean storeAdvertisement;
    private String kategorieGroup;
    private String storeTagGroups;
    private String storeNumber;

    // 오픈 시간과 마감 시간 필드
    @Column(length = 5) // HH:MM 형식으로 제한
    private String storeStarttime;

    @Column(length = 5) // HH:MM 형식으로 제한
    private String storeEndTime;

    @ManyToOne
    private SiteUser review;

    private LocalDateTime modifyDate;

    @ManyToMany
    private Set<SiteUser> voter;
}
