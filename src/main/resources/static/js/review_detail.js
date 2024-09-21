document.addEventListener("DOMContentLoaded", function () {
    const reviewIdElement = document.querySelector("#reviewId");
    const commentContentElement = document.querySelector("#commentContent");

    // 로그인 여부 및 로그인한 사용자 ID 확인
    const isAuthenticated = document.getElementById("isAuthenticated").value === 'true';
    const loggedInUserId = document.getElementById("loggedInUserId").value;

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
            const displayDate = comment.updatedAt
                ? new Date(comment.updatedAt).toLocaleString()
                : new Date(comment.createDate).toLocaleString();

            const actionButtons = isAuthenticated && loggedInUserId == comment.userId ? `
                <span type="button" class="btn badge badge-primary edit-button" data-comment-id="${comment.commentId}">수정</span>
                <span type="button" class="btn badge badge-danger delete-button" data-comment-id="${comment.commentId}">삭제</span>
            ` : '';

            const commentHtml = `
                <hr />
                <div class="comment">
                    <img src="${comment.userImage || '/img/user/default-profile.png'}" alt="프로필 사진" class="rounded-circle mt-2" style="width: 50px;height: 50px;">
                    <div class="comment-content">
                        <span class="font-weight-bold mr-2">${comment.username}</span>
                        <p>${comment.content}</p>
                        <p class="card-text">
                            <small class="text-muted">${displayDate}</small>
                            <span type="button" class="btn badge badge-secondary reply-button" data-comment-id="${comment.commentId}">답글달기</span>
                            ${actionButtons}
                        </p>
                    </div>
                </div>
            `;
            commentsList.insertAdjacentHTML("beforeend", commentHtml);
        });
    }

    // 댓글 작성 이벤트 핸들러
    document.querySelector("#submitComment").addEventListener("click", function (e) {
        e.preventDefault();

        const commentContent = commentContentElement.value.trim();

        if (!commentContent) {
            alert("댓글을 입력하세요!");
            return;
        }

        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        fetch(`/comments/reviews/${reviewId}`, {
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
            .then(data => {
                commentContentElement.value = ""; // 입력 필드 초기화
                fetchComments(); // 댓글 목록 다시 불러오기
            })
            .catch(error => {
                alert(error.message);
                console.error("댓글 작성 중 오류가 발생했습니다:", error);
            });
    });

    // 댓글 수정 및 삭제 이벤트 핸들러
    document.querySelector(".comments-list").addEventListener("click", function (e) {
        const target = e.target;
        const commentId = target.getAttribute("data-comment-id");

        if (target.classList.contains("edit-button")) {
            // 수정 버튼 클릭 시
            const newContent = prompt("댓글을 수정하세요:");
            if (newContent && commentId) {
                updateComment(commentId, newContent);
            }
        } else if (target.classList.contains("delete-button")) {
            // 삭제 버튼 클릭 시
            if (confirm("정말로 이 댓글을 삭제하시겠습니까?")) {
                deleteComment(commentId);
            }
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
                fetchComments(); // 수정 후 댓글 목록 다시 불러오기
                alert("댓글이 수정되었습니다.");
            })
            .catch(error => console.error("Error:", error));
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
                fetchComments(); // 삭제 후 댓글 목록 다시 불러오기
                alert("댓글이 삭제되었습니다.");
            })
            .catch(error => console.error("Error:", error));
    }

    // 초기 댓글 목록 불러오기
    fetchComments();

    /* 공유 모달창 기능 */
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
