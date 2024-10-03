document.addEventListener("DOMContentLoaded", function () {
	// 검색 버튼 클릭 이벤트
	const btn_search = document.getElementById("btn_search");
	btn_search.addEventListener("click", function (e) {
		e.preventDefault(); // 기본 폼 제출 방지
		document.getElementById("page").value = 0; // 첫 페이지로 초기화
		document.getElementById("searchForm").submit();
	});

	// 태그 클릭 이벤트 설정
	document.querySelectorAll('.badge').forEach(badge => {
		badge.addEventListener('click', function (e) {
			e.preventDefault(); // 기본 링크 클릭 동작 방지
			const tag = this.getAttribute('data-tag');
			console.log("Fetching posts for tag:", tag);

			// 활성화된 태그 표시
			document.querySelectorAll('.badge').forEach(b => b.classList.remove('active'));
			this.classList.add('active');

			if (tag === "") { // 전체 태그의 data-tag는 빈 문자열로 설정
				// 검색창 비우기
				document.getElementById("search_kw").value = "";
				// 검색 버튼 클릭 이벤트 트리거
				btn_search.click(); // 검색 버튼 클릭
			} else {
				// 초기 페이지 요청
				fetchPostsForPage(tag, 1); // 첫 번째 페이지 요청
			}
		});
	});
});

function fetchPostsForPage(tag, page) {
	const boardTag = tag === "" ? "" : tag; // 전체 태그 클릭 시 빈 문자열 사용

	fetch(`/posts?boardTag=${tag}&page=${page}`)
		.then(response => {
			if (!response.ok) {
				throw new Error('Network response was not ok');
			}
			return response.json();
		})
		.then(data => {
			const filteredData = data.filter(board =>
				board.boardTitle &&
				board.username &&
				board.boardCreateDate
			);
			const postsTable = document.getElementById('posts-table');
			postsTable.innerHTML = ''; // 기존 게시글 지우기
			// thead 부분을 추가
			postsTable.innerHTML = `
            <thead class="table-warning">
                <tr class="board_header board_text">
                    <th class="board_num">번호</th>
                    <th class="board_title">제목</th>
                    <th>글쓴이</th>
                    <th>작성일시</th>
					<th>댓글</th>
                </tr>
            </thead>
            <tbody></tbody>
        `;

			const startIndex = (page - 1) * 5;
			const paginatedData = filteredData.slice(startIndex, startIndex + 5);

			paginatedData.forEach((board, index) => {
				const row = document.createElement('tr');
				row.className=('board_text');
				row.innerHTML = `
                <td>${startIndex + index + 1}</td>
                <td>
                    <a href="/board_detail/${board.boardId}" class="text-dark boardLink">${board.boardTitle}</a>
                    
                </td>
                <td>${board.username}</td>
                <td>${new Date(board.boardCreateDate).toLocaleDateString()}</td>
				<td>${board.answerList ? board.answerList.length : 0}</td>
            `;
				postsTable.appendChild(row);
			});

			updatePagination(filteredData.length);
		})
		.catch(error => {
			console.error('Error:', error);
			const postsTable = document.getElementById('posts-table');
			postsTable.innerHTML = '<tr><td colspan="4">Error loading posts. Please try again.</td></tr>';
			updatePagination(0);
		});
}

// 페이지네이션 업데이트 함수
function updatePagination(totalPosts) {
	const paginationContainer = document.querySelector('.pagination');
	
	var boardPage = {
		number: '[[${boardPage.number}]]',
		hasPrevious: '[[${boardPage.hasPrevious}]]'
	};
	paginationContainer.innerHTML = ''; // 기존 페이지네이션 초기화

	const totalPages = Math.ceil(totalPosts / 5);
	for (let i = 1; i <= totalPages; i++) {
		const li = document.createElement('li');
		li.className = 'page-item';
		li.innerHTML = `<a class="page-link" href="#">${i}</a>`;

		li.querySelector('.page-link').addEventListener('click', function (e) {
			e.preventDefault();
			const tag = document.querySelector('.badge.active').getAttribute('data-tag');
			fetchPostsForPage(tag, i); // 선택한 페이지의 게시글 요청
		});

		paginationContainer.appendChild(li);
	}
}