// 시작 시간과 종료 시간 관련 변수 설정
const startTimeInput = document.getElementById('StoreStarttime');
const endTimeInput = document.getElementById('StoreEndTime');
const errorMessage = document.getElementById('error1');

// 시간 형식 검증 함수
function validateTimeFormat(time) {
	const regex = /^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/; // HH:MM 형식 검증
	return regex.test(time);
}

// 시간 검증 및 버튼 생성 함수
function generateButtons() {
	const startTime = startTimeInput.value;
	const endTime = endTimeInput.value;

	if (!startTime && !endTime) {
		errorMessage.style.display = 'none'; // 아무것도 입력되지 않았을 때 에러 메시지 숨김
		return;
	}

	if (!validateTimeFormat(startTime) || !validateTimeFormat(endTime)) {
		errorMessage.style.display = 'block';
		errorMessage.textContent = '*시간 형식이 잘못되었습니다. HH:MM 형식으로 입력하세요.';
		return;
	}

	const start = new Date(`2024-01-01T${startTime}:00`);
	const end = new Date(`2024-01-01T${endTime}:00`);

	if (start >= end) {
		errorMessage.style.display = 'block';
		errorMessage.textContent = '*시작 시간이 끝나는 시간보다 빨라야 합니다.';
		return;
	} else {
		errorMessage.style.display = 'none';
	}
}

// 태그 토글 함수
	function toggleTag(button, tag, inputId) {
	    // 선택된 버튼에 'selected' 클래스를 추가하거나 제거
	    button.classList.toggle('selected');
	    
	    // 해당 input 필드를 찾음
	    const input = document.getElementById(inputId);
	    const currentTags = input.value ? input.value.split(', ') : [];

	    // 선택된 태그가 있다면 추가하고, 해제된 태그는 제거
	    if (button.classList.contains('selected')) {
	        if (!currentTags.includes(tag)) {
	            currentTags.push(tag);
	        }
	    } else {
	        const index = currentTags.indexOf(tag);
	        if (index > -1) {
	            currentTags.splice(index, 1);
	        }
	    }

	    // 선택된 태그들을 input 필드에 반영
	    input.value = currentTags.join(', ');
	}

// 주소 검색 및 좌표 가져오는 함수
let timeout = null;

document.getElementById('basicAddress').addEventListener('input', function() {
	clearTimeout(timeout);
	const basicAddress = this.value;
	const detailAddress = document.getElementById('detailAddress').value;

	if (basicAddress) {
		timeout = setTimeout(() => {
			getCoordinates(basicAddress, detailAddress);
		}, 500); // 500ms 후에 API 호출
	} else {
		document.getElementById('result').innerText = '';
	}
});

document.getElementById('detailAddress').addEventListener('input', function() {
	clearTimeout(timeout);
	const basicAddress = document.getElementById('basicAddress').value;

	if (basicAddress) {
		timeout = setTimeout(() => {
			getCoordinates(basicAddress, this.value); // 기본 주소와 상세 주소로 호출
		}, 500); // 500ms 후에 API 호출
	} else {
		document.getElementById('result').innerText = '';
	}
});

async function getCoordinates(basicAddress, detailAddress) {
	const apiKey = 'AIzaSyDNDLO6UZO7gnvqpdjk5CALPXeWx08sOnM'; // API 키를 여기에 입력하세요
	const fullAddress = detailAddress ? `${basicAddress} ${detailAddress}` : basicAddress;
	const response = await fetch(`https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(fullAddress)}&key=${apiKey}`);
	const data = await response.json();
	
	if (data.status === 'OK') {
		const location = data.results[0].geometry.location;
		document.getElementById('storeLatitude').value = location.lat; // 위도 입력
		document.getElementById('storeLongitude').value = location.lng; // 경도 입력
		document.getElementById('result').innerText = '';
	} else {
		document.getElementById('result').innerText = '주소를 찾을 수 없습니다.';
		document.getElementById('storeLatitude').value = '';
		document.getElementById('storeLongitude').value = '';
	}
}

// Daum Postcode API를 이용한 주소 검색
function sample6_execDaumPostcode() {
	new daum.Postcode({
		oncomplete: function(data) {
			document.getElementById('postcode').value = data.zonecode; // 우편번호
			document.getElementById('basicAddress').value = data.roadAddress || data.jibunAddress; // 기본 주소
			getCoordinates(data.roadAddress || data.jibunAddress); // 주소 선택 후 바로 위도, 경도 찾기
			document.getElementById('detailAddress').focus(); // 상세 주소 입력 필드로 포커스
		}
	}).open();
}