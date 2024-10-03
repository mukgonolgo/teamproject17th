document.addEventListener("DOMContentLoaded", function () {
	// 삭제 버튼 처리
	const deleteElements = document.getElementsByClassName("delete");
	Array.from(deleteElements).forEach(function (element) {
		element.addEventListener("click", function () {
			if (confirm("정말로 삭제하시겠습니까?")) {
				location.href = this.dataset.uri;
			}
		});
	});
	const editTitleBtn = document.getElementById("editTitleBtn");
	if (editTitleBtn) {
	    const id = editTitleBtn.dataset.boardId;

	    editTitleBtn.addEventListener("click", function () {
	        const titleContent = document.querySelector('.question_title span:nth-child(2)').textContent; // 제목을 가져오기
	        document.getElementById("editTitleContent").value = titleContent; // 모달에 설정
	        $('#editTitleModal').modal('show'); // 모달 열기
	    });

	    // 제목 수정 폼 제출 이벤트
	    const editTitleForm = document.getElementById("editTitleForm");
	    editTitleForm.onsubmit = function (event) {
	        event.preventDefault();
	        const newContent = document.getElementById("editTitleContent").value;
			
	        fetch(`/question/Titlemodify/${id}`, {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json',
	                'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
	            },
	            body: JSON.stringify({title: newContent}) // 수정할 때 title로 전달
	        })
	        .then(response => {
	            if (response.ok) {
					alert("제목을 수정하였습니다")
	                document.querySelector('.question_title span:nth-child(2)').textContent = newContent; // 제목 업데이트
					
	                $('#editTitleModal').modal('hide'); // 모달 닫기
	            } else {
	                alert("제목 수정에 실패했습니다.");
	            }
	        });
	    };
	}
	// 답변 수정 버튼 처리
	const editButtons = document.querySelectorAll(".edit-btn");
	const editAnswerForm = document.getElementById("editAnswerForm");

	editButtons.forEach(button => {
		button.addEventListener("click", function () {
			const answerId = this.dataset.id;
			const answerContent = this.dataset.content;

			// 모달에 기존 답변 내용 설정
			document.getElementById("editAnswerContent").value = answerContent;
			$('#editAnswerModal').modal('show');

			// onsubmit 핸들러 설정
			editAnswerForm.onsubmit = function (event) {
				event.preventDefault();

				const content = document.getElementById("editAnswerContent").value;

				fetch(`/answer/modify/${answerId}`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
					},
					body: JSON.stringify({content: content})
				})
					.then(response => {
						if (response.ok) {
							const answerElement = document.querySelector(`#answer_${answerId}`);
							if (answerElement) {
								answerElement.querySelector("span").textContent = content; // 내용 업데이트
							}
							alert("답변을 수정하였습니다.");
							$('#editAnswerModal').modal('hide');
							location.reload();
						} else {
							alert("수정에 실패했습니다.");
						}
					});
			};
		});
	});

	// 질문 수정 버튼 클릭 이벤트
	const editQuestionBtn = document.getElementById("editQuestionBtn");
	if (editQuestionBtn) {
		const id = editQuestionBtn.dataset.boardId;

		editQuestionBtn.addEventListener("click", function () {
			const questionContent = document.querySelector('.question').textContent;
			document.getElementById("editQuestionContent").value = questionContent;
			$('#editQuestionModal').modal('show');
		});

		// 질문 수정 폼 제출 이벤트
		const editQuestionForm = document.getElementById("editQuestionForm");
		editQuestionForm.onsubmit = function (event) {
			event.preventDefault();
			const newContent = document.getElementById("editQuestionContent").value;

			fetch(`/question/modify/${id}`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
				},
				body: JSON.stringify({content: newContent})
			})
				.then(response => {
					if (response.ok) {
						alert("질문을 수정하였습니다ㅏ")
						document.querySelector('.question').textContent = newContent;
						$('#editQuestionModal').modal('hide');
					} else {
						alert("수정에 실패했습니다.");
					}
				});
		};
	}

	// 대댓글 버튼 클릭 이벤트
	const replyButtons = document.querySelectorAll("#reply-btn");
	replyButtons.forEach(button => {
		button.addEventListener("click", function () {
			const replyContainer = this.nextElementSibling;
			if (replyContainer) {
				replyContainer.classList.toggle("hidden");
				replyContainer.classList.toggle("shown");
			}
		});
	});

	// 대댓글 저장 버튼 클릭 이벤트
	const saveReplyButtons = document.querySelectorAll(".save-reply-btn");
	saveReplyButtons.forEach(button => {
		button.addEventListener("click", function () {
			const replyContainer = this.closest('.reply-container');
			const textarea = replyContainer.querySelector("textarea");
			const comment = textarea.value.trim();

			if (comment) {
				const answerId = this.closest('.answer_line').querySelector("[id^='answer_']").id.split('_')[1];

				fetch(`/answer/comment/${answerId}`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
					},
					body: JSON.stringify({comment: comment})
				})
					.then(response => {
						if (response.ok) {
							textarea.value = "";
							alert("대댓글이 저장되었습니다.");
							location.reload();
						} else {
							alert("대댓글 저장에 실패했습니다.");
						}
					});
			} else {
				alert("대댓글 내용을 입력해 주세요.");
			}
		});
	});

	// 답변 작성
	const answerTextArea = document.querySelector(".answerTextArea");
	const answerBtn = document.getElementById("answerText");

	answerBtn.addEventListener("click", function (event) {
		if (!answerTextArea.value.trim()) {
			alert("댓글 내용을 입력해 주세요.");
			event.preventDefault();
		}
	});

	// 대댓글 수정 버튼 클릭 이벤트
	const editCommentButtons = document.querySelectorAll(".edit-comment-btn");
	editCommentButtons.forEach(button => {
		button.addEventListener("click", function () {
			const answerId = this.dataset.id;
			const commentContent = this.dataset.content;

			// 모달에 기존 대댓글 내용 설정
			document.getElementById("editAnswerContent").value = commentContent;
			$('#editAnswerModal').modal('show');

			// 폼 제출 이벤트 핸들러 설정
			const editAnswerForm = document.getElementById("editAnswerForm");
			editAnswerForm.onsubmit = function (event) {
				event.preventDefault();

				const content = document.getElementById("editAnswerContent").value;

				fetch(`/answer/commentModify/${answerId}`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json', // JSON 형식으로 설정
						'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
					},
					body: JSON.stringify({content: content}) // JSON으로 변환
				})
					.then(response => {
						if (response.ok) {
							return response.text(); // 응답을 문자열로 변환
						} else {
							throw new Error("수정에 실패했습니다.");
						}
					})
					.then(data => {
						// data가 수정된 댓글 내용("22")일 것임
						const commentElement = document.querySelector(`.commentText [data-id='${answerId}']`).previousElementSibling;
						if (commentElement) {
							commentElement.textContent = data; // 수정된 내용을 사용
							console.log(data)
						}
						alert("대댓글을 수정하였습니다.");
						$('#editAnswerModal').modal('hide');
						location.reload(); // 페이지 새로 고침
					})
					.catch(error => {
						console.error("Error:", error);
						alert("수정 중 오류가 발생했습니다.");
					});
			};
		});
	});


	// 대댓글 삭제 버튼 처리
	const deleteCommentButtons = document.querySelectorAll(".delete-comment-btn");
	deleteCommentButtons.forEach(button => {
		button.addEventListener("click", function () {
			const deleteUri = this.dataset.uri; // 삭제 URI 가져오기
			if (confirm("정말로 삭제하시겠습니까?")) {
				fetch(deleteUri, {
					method: 'POST',
					headers: {
						'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
					}
				})
					.then(response => {
						if (response.ok) {
							// 삭제 성공 시, DOM에서 해당 대댓글 삭제
							this.closest('.commentText').remove(); // 대댓글 요소 삭제
							alert("대댓글이 삭제되었습니다.");
							location.reload();
						} else {
							alert("삭제에 실패했습니다.");
						}
					});
			}
		});
	});

});