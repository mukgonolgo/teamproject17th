document.addEventListener("DOMContentLoaded", function () {
    const reviewIdElement = document.querySelector("#reviewId");
    const commentContentElement = document.querySelector("#commentContent");
    const commentForm = document.querySelector(".comment-input");
    const submitButton = document.querySelector("#submitComment");

    let isEditMode = false; // 수정 모드 여부
    let editCommentId = null; // 수정할 댓글 ID 저장
    let replyToCommentId = null; // 대댓글 작성 대상 댓글 ID

    // 로그인 여부 및 로그인한 사용자 ID 확인
    const isAuthenticated = document.getElementById("isAuthenticated").value === 'true';
    const loggedInUserId = document.getElementById("loggedInUserId").value;
    const authorId = document.getElementById("authorId").value; // 리뷰 작성자 ID 가져오기

    if (!reviewIdElement || !commentContentElement) {
        console.error("필수 요소가 누락되었습니다.");
        return;
    }

    const reviewId = reviewIdElement.value;

    // 댓글 목록 가져오기
    function fetchComments() {
        fetch(`/comments/reviews/${reviewId}`)
            .then(response => response.json())
            .then(comments => {
                renderComments(comments);
            })
            .catch(error => {
                console.error("댓글을 불러오는 중 오류가 발생했습니다.", error);
            });
    }

    // 댓글 렌더링 함수
    function renderComments(comments) {
        const commentsList = document.querySelector(".comments-list");
        commentsList.innerHTML = ''; // 기존 댓글 초기화

        comments.forEach(comment => {
            renderComment(comment, commentsList);
        });
    }

    // 댓글 렌더링 함수
    function renderComment(comment, parentElement) {
        const displayDate = comment.updatedAt
            ? new Date(comment.updatedAt).toLocaleString()
            : new Date(comment.createDate).toLocaleString();

        const isAuthor = comment.userId == authorId ? '<span class="badge text-danger">작성자</span>' : '';

        const actionButtons = isAuthenticated && loggedInUserId == comment.userId ? `
            <span type="button" class="btn badge badge-primary edit-button" data-comment-id="${comment.commentId}" data-content="${comment.content}">수정</span>
            <span type="button" class="btn badge badge-danger delete-button" data-comment-id="${comment.commentId}">삭제</span>
        ` : '';
	
        const commentHtml = `
            <hr />
            <div class="comment" data-comment-id="${comment.commentId}">
                <img src="${comment.userImage || '/img/user/default-profile.png'}" alt="프로필 사진" class="rounded-circle mt-2" style="width: 50px;height: 50px;">
                <div class="comment-content">
                    <span class="font-weight-bold">${comment.username}</span> ${isAuthor}
                    <p>${comment.content}</p>
                    <p class="card-text">
                        <small class="text-muted">${displayDate}</small>
                        <span type="button" class="btn badge badge-secondary reply-button" data-comment-id="${comment.commentId}" data-username="${comment.username}">답글달기</span>
                        ${actionButtons}
                    </p>
                </div>
            </div>
        `;
        parentElement.insertAdjacentHTML("beforeend", commentHtml);

        // 대댓글이 있는 경우, 부모 댓글 아래에 대댓글만 표시
        if (comment.replies && comment.replies.length > 0) {
            comment.replies.forEach(reply => {
                renderReply(reply, parentElement);
            });
        }
    }

    // 대댓글 렌더링 함수
    function renderReply(reply, parentElement) {
        const replyDisplayDate = reply.updatedAt
            ? new Date(reply.updatedAt).toLocaleString()
            : new Date(reply.createDate).toLocaleString();

        const isReplyAuthor = reply.userId == authorId ? '<span class="badge text-danger">작성자</span>' : '';

        const replyActionButtons = isAuthenticated && loggedInUserId == reply.userId ? `
            <span type="button" class="btn badge badge-primary edit-button" data-comment-id="${reply.commentId}" data-content="${reply.content}">수정</span>
            <span type="button" class="btn badge badge-danger delete-button" data-comment-id="${reply.commentId}">삭제</span>
        ` : '';

        const replyHtml = `
            <div class="reply ml-4" data-reply-id="${reply.commentId}">
                <img src="${reply.userImage || '/img/user/default-profile.png'}" alt="프로필 사진" class="rounded-circle mt-2" style="width: 50px;height: 50px;">
                <div class="reply-content">
                    <span class="font-weight-bold">${reply.username}</span> ${isReplyAuthor}
                    <p>${reply.content}</p>
                    <p class="card-text">
                        <small class="text-muted">${replyDisplayDate}</small>
                        ${replyActionButtons}
                    </p>
                </div>
            </div>
        `;
        parentElement.insertAdjacentHTML("beforeend", replyHtml);
    }

    // 댓글 작성 및 수정 이벤트 핸들러
    submitButton.addEventListener("click", function (e) {
        e.preventDefault();

        const commentContent = commentContentElement.value.trim();

        if (!commentContent) {
            alert("댓글을 입력하세요!");
            return;
        }

        if (isEditMode) {
            updateComment(editCommentId, commentContent);
        } else {
            const csrfToken = document.querySelector('meta[name="_csrf"]').content;
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

            const url = replyToCommentId
                ? `/comments/reviews/${reviewId}/comments/${replyToCommentId}/reply`
                : `/comments/reviews/${reviewId}`;

            fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    [csrfHeader]: csrfToken
                },
                body: `content=${encodeURIComponent(commentContent)}`
            })
                .then(response => {
                    if (!response.ok) throw new Error("댓글 작성에 실패했습니다.");
                    return response.json();
                })
                .then(() => {
                    commentContentElement.value = "";
                    replyToCommentId = null;
                    fetchComments();
                })
                .catch(error => {
                    alert(error.message);
                    console.error("댓글 작성 중 오류가 발생했습니다:", error);
                });
        }
    });

    // 댓글 수정 및 삭제, 답글 작성 이벤트 핸들러
    document.querySelector(".comments-list").addEventListener("click", function (e) {
        const target = e.target;
        const commentId = target.getAttribute("data-comment-id");

        if (target.classList.contains("edit-button")) {
            const content = target.getAttribute("data-content");
            commentContentElement.value = content;
            editCommentId = commentId;
            isEditMode = true;
            submitButton.textContent = "수정 완료";
            commentContentElement.focus();
        } else if (target.classList.contains("delete-button")) {
            if (confirm("정말로 이 댓글을 삭제하시겠습니까?")) {
                deleteComment(commentId);
            }
        } else if (target.classList.contains("reply-button")) {
            replyToCommentId = commentId;
            const username = target.getAttribute("data-username");
            commentContentElement.value = `@${username} `;
            commentContentElement.focus();
        }
    });

    // 댓글 수정 함수
    function updateComment(commentId, newContent) {
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        fetch(`/comments/${commentId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                [csrfHeader]: csrfToken
            },
            body: `content=${encodeURIComponent(newContent)}`
        })
            .then(response => {
                if (!response.ok) throw new Error("댓글 수정에 실패했습니다.");
                return response.json();
            })
            .then(() => {
                isEditMode = false;
                editCommentId = null;
                submitButton.textContent = "댓글 작성";
                commentContentElement.value = "";
                fetchComments();
                alert("댓글이 수정되었습니다.");
            })
            .catch(error => console.error("댓글 수정 중 오류가 발생했습니다:", error));
    }

    // 댓글 삭제 함수
    function deleteComment(commentId) {
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        fetch(`/comments/${commentId}`, {
            method: "DELETE",
            headers: {
                [csrfHeader]: csrfToken
            }
        })
            .then(response => {
                if (!response.ok) throw new Error("댓글 삭제에 실패했습니다.");
                fetchComments();
                alert("댓글이 삭제되었습니다.");
            })
            .catch(error => console.error("댓글 삭제 중 오류가 발생했습니다:", error));
    }

    // 초기 댓글 목록 불러오기
    fetchComments();

    // 공유 모달창 기능
    document.getElementById('shareBtn').addEventListener('click', function () {
        const modal = document.getElementById('shareModal');
        const shareUrl = window.location.href;
        document.getElementById('shareUrl').value = shareUrl;

        document.getElementById('facebookShare').href = `https://www.facebook.com/sharer/sharer.php?u=${shareUrl}`;
        document.getElementById('twitterShare').href = `https://twitter.com/intent/tweet?url=${shareUrl}&text=Check this out!`;

        modal.style.display = 'block';
    });

    document.querySelector('.closeBtn').addEventListener('click', function () {
        document.getElementById('shareModal').style.display = 'none';
    });

    document.getElementById('copyBtn').addEventListener('click', function () {
        const copyText = document.getElementById('shareUrl');
        copyText.select();
        document.execCommand('copy');
        alert('주소가 복사되었습니다!');
    });
});
