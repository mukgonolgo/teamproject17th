package com.test.project.eat_friends;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendDTO {
    private Long id; // 엔티티의 ID
    private String title; // 엔티티의 friendTitle과 매핑
    private Integer maxMembers; // 엔티티의 friendMaxMembers와 매핑
    private Integer totalMembers; // 엔티티의 friendTotalMembers와 매핑
    private String location; // 엔티티의 location 필드가 없으므로 필요 시 추가
    private String eventDate; // 엔티티의 friendEventDate와 매핑
    private String eventTime; // 엔티티의 friendEventTime과 매핑
    private String restaurant; // 엔티티의 restaurant 필드가 없으므로 필요 시 추가
    private String image; // 엔티티의 image 필드가 없으므로 필요 시 추가
    private String memberName; // 엔티티의 friendMemberName과 매핑
    private String roomID; // 엔티티의 roomID와 매핑
}
