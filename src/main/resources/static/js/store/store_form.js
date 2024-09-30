let imageCount = 0;
const maxImages = 5;
const imageFiles = []; // 파일들을 저장할 배열
function addImage(event) {
    const files = Array.from(event.target.files);

    if (imageCount + files.length > maxImages) {
        alert("이미지는 최대 5개까지 추가할 수 있습니다.");
        return;
    }

    files.forEach(file => {
        if (imageCount >= maxImages) {
            alert("이미지는 최대 5개까지 추가할 수 있습니다.");
            return;
        }

        const reader = new FileReader();
        reader.onload = function (e) {
            const imgContainer = document.createElement('div');
            imgContainer.classList.add('img-container');

            const img = document.createElement('img');
            img.src = e.target.result;
            img.classList.add('uploaded-image');
            img.style.cursor = 'pointer';

            img.addEventListener('click', function () {
                imgContainer.remove();
                imageCount--;
                const index = imageFiles.findIndex(f => f.name === file.name && f.size === file.size);
                if (index > -1) {
                    imageFiles.splice(index, 1); // 배열에서 파일 제거
                }

                if (imageCount < maxImages) {
                    document.getElementById('add-icon').style.display = 'flex';
                }
            });

            imgContainer.appendChild(img);

            const iconContainer = document.querySelector('.icon-container');
            iconContainer.appendChild(imgContainer);

            imageCount++;
            imageFiles.push(file); // 배열에 파일 추가

            if (imageCount >= maxImages) {
                document.getElementById('add-icon').style.display = 'none';
            }

            console.log("현재 이미지 파일 배열:", imageFiles.map(file => file.name));
        };

        reader.readAsDataURL(file);
    });
}



// 폼 제출 이벤트 처리
	document.getElementById('storeForm').addEventListener('submit', function(event) {
	    const input = document.createElement('input');
	    input.type = 'hidden';
	    input.name = 'imageFiles'; // 서버에서 받을 파라미터 이름
	    input.value = JSON.stringify(imageFiles.map(file => file.name)); // 파일 이름을 JSON 형식으로 변환

	    this.appendChild(input);
	});
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
