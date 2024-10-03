<<<<<<< HEAD
document.querySelectorAll('.S2_hearticon').forEach(icon => {
  const postId = icon.id.match(/\d+/)[0]; // 정규식으로 게시글 ID 추출
  const heartFilled = document.getElementById(`heartFilled${postId}`);
  const heartLine = document.getElementById(`heartLine${postId}`);

  if (heartFilled && heartLine) {
    // 기본 상태 설정: 하트가 채워진 상태는 숨기고, 빈 상태는 보이도록 설정
    heartFilled.style.display = 'none'; // 기본적으로 하트가 채워진 상태는 숨김
    heartLine.style.display = 'inline';  // 기본적으로 빈 하트는 보이도록 설정

    // 하트 상태 업데이트
    const updateHeartStatus = (newHeartStatus) => {
      // 서버로 데이터 전송
      fetch('/updateHeart', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: postId, heart: newHeartStatus })
      })
      .then(response => response.text())
      .then(result => {
        if (result === 'success') {
          console.log('하트 상태가 성공적으로 업데이트되었습니다.');
        } else {
          console.error('하트 상태 업데이트에 실패했습니다.');
        }
      })
      .catch(error => {
        console.error('서버와의 통신 중 오류가 발생했습니다:', error);
      });
    };

    heartLine.addEventListener('click', () => {
      // 빈 하트를 클릭하면 하트가 채워진 상태로 변경
      heartLine.classList.add('icon-active'); // 확대 애니메이션 추가
      heartFilled.classList.remove('icon-active'); // 기본 크기로 돌아가기
      setTimeout(() => {
        heartLine.style.display = 'none'; // 빈 하트 숨기기
        heartFilled.style.display = 'inline'; // 채워진 하트 보이기
        updateHeartStatus(true); // 하트 상태 활성화
      }, 300); // 애니메이션 시간이 끝난 후 display 변경
    });

    heartFilled.addEventListener('click', () => {
      // 채워진 하트를 클릭하면 빈 하트 상태로 변경
      heartFilled.classList.add('icon-active'); // 확대 애니메이션 추가
      heartLine.classList.remove('icon-active'); // 기본 크기로 돌아가기
      setTimeout(() => {
        heartFilled.style.display = 'none'; // 채워진 하트 숨기기
        heartLine.style.display = 'inline'; // 빈 하트 보이기
        updateHeartStatus(false); // 하트 상태 비활성화
      }, 300); // 애니메이션 시간이 끝난 후 display 변경
    });
  }
=======
document.querySelectorAll('.S2_hearticon').forEach(icon => {
  const postId = icon.id.match(/\d+/)[0]; // 정규식으로 게시글 ID 추출
  const heartFilled = document.getElementById(`heartFilled${postId}`);
  const heartLine = document.getElementById(`heartLine${postId}`);

  if (heartFilled && heartLine) {
    // 기본 상태 설정: 하트가 채워진 상태는 숨기고, 빈 상태는 보이도록 설정
    heartFilled.style.display = 'none'; // 기본적으로 하트가 채워진 상태는 숨김
    heartLine.style.display = 'inline';  // 기본적으로 빈 하트는 보이도록 설정

    // 하트 상태 업데이트
    const updateHeartStatus = (newHeartStatus) => {
      // 서버로 데이터 전송
      fetch('/updateHeart', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: postId, heart: newHeartStatus })
      })
      .then(response => response.text())
      .then(result => {
        if (result === 'success') {
          console.log('하트 상태가 성공적으로 업데이트되었습니다.');
        } else {
          console.error('하트 상태 업데이트에 실패했습니다.');
        }
      })
      .catch(error => {
        console.error('서버와의 통신 중 오류가 발생했습니다:', error);
      });
    };

    heartLine.addEventListener('click', () => {
      // 빈 하트를 클릭하면 하트가 채워진 상태로 변경
      heartLine.classList.add('icon-active'); // 확대 애니메이션 추가
      heartFilled.classList.remove('icon-active'); // 기본 크기로 돌아가기
      setTimeout(() => {
        heartLine.style.display = 'none'; // 빈 하트 숨기기
        heartFilled.style.display = 'inline'; // 채워진 하트 보이기
        updateHeartStatus(true); // 하트 상태 활성화
      }, 300); // 애니메이션 시간이 끝난 후 display 변경
    });

    heartFilled.addEventListener('click', () => {
      // 채워진 하트를 클릭하면 빈 하트 상태로 변경
      heartFilled.classList.add('icon-active'); // 확대 애니메이션 추가
      heartLine.classList.remove('icon-active'); // 기본 크기로 돌아가기
      setTimeout(() => {
        heartFilled.style.display = 'none'; // 채워진 하트 숨기기
        heartLine.style.display = 'inline'; // 빈 하트 보이기
        updateHeartStatus(false); // 하트 상태 비활성화
      }, 300); // 애니메이션 시간이 끝난 후 display 변경
    });
  }
>>>>>>> refs/remotes/origin/develop
});console.log("좋아요 버튼 클릭 데이터 전송 완료")