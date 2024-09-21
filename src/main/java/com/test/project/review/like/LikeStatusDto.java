package com.test.project.review.like;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeStatusDto {
    private boolean liked;
    private int likeCount;

    public LikeStatusDto(boolean liked, int likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }
}