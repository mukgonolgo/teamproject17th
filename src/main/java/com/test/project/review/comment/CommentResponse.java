package com.test.project.review.comment;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {
    private Long commentId;
    private String content;
    private LocalDateTime createDate;
    private LocalDateTime updatedAt;
    private Long userId;
    private String username;
    private String userImage;

}
