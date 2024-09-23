package com.test.project.review.comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    // 추가: 답글 리스트 필드
    private List<CommentResponse> replies;
    
 // 기본 생성자 추가
    public CommentResponse() {
        // 기본 생성자는 특별히 할 작업이 없을 경우 비워둘 수 있음
    }
    // 생성자: ReviewComment 객체로부터 CommentResponse 객체 생성
    public CommentResponse(ReviewComment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.createDate = comment.getCreateDate();
        this.updatedAt = comment.getUpdatedAt();
        this.userId = comment.getUser().getId();
        this.username = comment.getUser().getUsername();
        this.userImage = comment.getUser().getImageUrl();

        // 대댓글이 있을 경우 응답에 포함
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            this.replies = comment.getReplies().stream()
                .map(CommentResponse::new) // 대댓글도 같은 형식으로 변환
                .collect(Collectors.toList());
        }
    }
}