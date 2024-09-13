package com.test.project.store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

//import com.test.project.review.Review;
import com.test.project.user.SiteUser;

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
    private String storeAddress;    

    private double storeLatitude;
    private double storeLongitude;

    @Column(columnDefinition = "TEXT")
    private String storeContent;        

    private LocalDateTime createDate;  

    private boolean storeAdvertisement; //광고여부

    private String storeTag;    
    
    private String storeNumber;    

    @ManyToOne
    private SiteUser review;

    private LocalDateTime modifyDate; 
    
    @ManyToMany
    private Set<SiteUser> voter; 
}
