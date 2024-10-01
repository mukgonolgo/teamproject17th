// 메뉴 추가 스크립트
let isExpanded = false; // 초기 상태 설정

function updateMoreButtonVisibility() {
	const menuItems = document.querySelectorAll('.menu-item');
	const moreButton = document.getElementById('more-button');

	if (menuItems.length > 3) {
		moreButton.style.display = 'block';
	} else {
		moreButton.style.display = 'none';
	}
}

document.getElementById('add-button').onclick = function() {
	const menuName = document.getElementById('menu-name').value;
	const menuPrice = document.getElementById('menu-price').value;

	if (!menuName || !menuPrice) {
		alert("메뉴 이름과 가격을 입력해주세요.");
		return;
	}

	const newMenu = document.createElement('div');
	newMenu.className = 'menu-item d-flex align-items-center mb-2';
	newMenu.innerHTML = `
        <span class="menuName">${menuName}</span>
        <span class="dots mx-2"></span>
        <span class="menuPrice">${menuPrice} 원</span>
        <button class="btn btn-danger btn-sm ml-2 delete-button">삭제</button>
    `;
	document.getElementById('more-menu').appendChild(newMenu);

	// 삭제 버튼 클릭 이벤트 추가
	newMenu.querySelector('.delete-button').onclick = function() {
		newMenu.remove();
		updateMoreButtonVisibility(); // 메뉴 삭제 후 버튼 상태 업데이트
	};

	// 입력 필드 초기화
	document.getElementById('menu-name').value = '';
	document.getElementById('menu-price').value = '';

	updateMoreButtonVisibility(); // 메뉴 추가 후 버튼 상태 업데이트
};

document.getElementById('more-button').onclick = function() {
	const moreMenus = document.getElementById('more-menu');
	isExpanded = !isExpanded;

	if (isExpanded) {
		moreMenus.classList.add('show');
		this.textContent = '숨기기';
	} else {
		moreMenus.classList.remove('show');
		this.textContent = '더보기';
	}
};

// 초기 상태에서 버튼 가시성 업데이트
updateMoreButtonVisibility();

// 캘린더 선택
document.addEventListener("DOMContentLoaded", () => {
	createTimeButtons('startTime1', 'endTime1', 'timeButtons1', 'selectedTimeInput1', 'error1', 'timeButtons2');
	createTimeButtons('startTime2', 'endTime2', 'timeButtons2', 'selectedTimeInput2', 'error2', 'timeButtons1');

	// DB에서 가져온 오픈 시간과 마감 시간 설정
	const openTime = /*[[${store.storeStarttime}]]*/ ''; // DB에서 가져온 오픈 시간
	const closeTime = /*[[${store.storeEndTime}]]*/ ''; // DB에서 가져온 마감 시간

	document.getElementById('startTime1').value = openTime; // 시작 시간 입력 필드에 설정
	document.getElementById('endTime1').value = closeTime; // 마감 시간 입력 필드에 설정
});

function createTimeButtons(startTimeId, endTimeId, timeButtonsId, selectedTimeInputId, errorMessageId, otherTimeButtonsId) {
	const startTimeInput = document.getElementById(startTimeId);
	const endTimeInput = document.getElementById(endTimeId);
	const timeButtonsContainer = document.getElementById(timeButtonsId);
	const errorMessage = document.getElementById(errorMessageId);
	let selectedTime = '';

	function validateTimeFormat(time) {
		const regex = /^(0[0-9]|1[0-9]|2[0-3]|[0-9]):[0-5][0-9]$/;
		return regex.test(time);
	}

	function generateButtons() {
		console.log('timeButtonsContainer:', timeButtonsContainer); // 추가된 로그
		// null 체크 추가
		if (!timeButtonsContainer) {
			console.error("timeButtonsContainer가 null입니다!");
			return;
		}

		timeButtonsContainer.innerHTML = '';
		const startTime = startTimeInput.value;
		const endTime = endTimeInput.value;

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

		const startMinutes = Math.floor(start.getMinutes() / 30) * 30;
		start.setMinutes(startMinutes);

		for (let time = start; time < end; time.setMinutes(time.getMinutes() + 30)) {
			const button = document.createElement('div');
			button.className = 'time-button';
			button.textContent = time.toTimeString().slice(0, 5);

			button.addEventListener('click', () => {
				const buttons = document.querySelectorAll(`#${timeButtonsId} .time-button`);
				const otherButtons = document.querySelectorAll(`#${otherTimeButtonsId} .time-button`);

				buttons.forEach(btn => btn.classList.remove('selected'));
				button.classList.add('selected');
				selectedTime = button.textContent;

				otherButtons.forEach(btn => {
					btn.classList.remove('selected');
					if (btn.textContent === selectedTime) {
						btn.classList.add('selected');
					}
				});

				const selectedInputs = document.querySelectorAll('.selected-time');
				selectedInputs.forEach(input => {
					input.value = selectedTime;
				});
			});

			timeButtonsContainer.appendChild(button);
		}
	}

	startTimeInput.addEventListener('change', generateButtons);
	endTimeInput.addEventListener('change', generateButtons);
	generateButtons();
}

let count = 1;
const minCount = 1;
const maxCount = 10;

function increment() {
	if (count < maxCount) {
		count++;
		updateCount();
	}
}

function decrement() {
	if (count > minCount) {
		count--;
		updateCount();
	}
}

function updateCount() {
	const counts = document.querySelectorAll('.count');
	counts.forEach((element) => {
		element.innerText = count;
	});

	const selectedMembers = document.querySelectorAll('.selected-Memver');
	selectedMembers.forEach((element) => {
		element.value = count; // 입력란에 값 업데이트
	});

	updateButtons();
}

function updateButtons() {
	const decrementBtns = document.querySelectorAll('.decrementBtn');
	const incrementBtns = document.querySelectorAll('.incrementBtn');

	decrementBtns.forEach((btn) => {
		btn.disabled = (count <= minCount);
	});

	incrementBtns.forEach((btn) => {
		btn.disabled = (count >= maxCount);
	});
}

// 초기 버튼 상태 업데이트
updateButtons();

var disabledDays = [];

function updateCalendarDimensions() {
	$('.calendar').fullCalendar('option', 'contentHeight', $('.calendar').height());
	$('.calendar').fullCalendar('option', 'height', $('.calendar').height());
}

$('.calendar').fullCalendar({
	header: {
		left: 'prev',
		center: 'title',
		right: 'next'
	},
	monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
	monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
	titleFormat: 'YYYY년 M월',
	defaultDate: moment().format('YYYY-MM-DD'),
	navLinks: false,
	editable: false,
	selectable: true,
	eventLimit: true,
	dayRender: function(date, cell) {
		if (date.isBefore(moment(), 'day')) {
			cell.addClass('fc-past');
		} else if (disabledDays.includes(date.day())) {
			cell.addClass('disabled-day');
		} else {
			cell.css('cursor', 'pointer');
		}
	},
	select: function(start) {
		// 과거 날짜 체크
		if (start.isBefore(moment(), 'day')) {
			$('.selected-date-input').val('');
			$('.Day-error-message-container').text('과거 날짜는 선택할 수 없습니다.');
			$('.calendar').fullCalendar('unselect');
			return;
		}

		// 비활성화된 요일 체크
		if (disabledDays.includes(start.day())) {
			$('.selected-date-input').val('');
			$('.Day-error-message-container').text('이 요일은 비활성화되어 선택할 수 없습니다.');
			$('.calendar').fullCalendar('unselect');
			return;
		}

		$('.selected-date-input').val('');
		$('.calendar .selected-date').removeClass('selected-date');

		var dayCell = start.format('YYYY-MM-DD');
		$('td[data-date="' + dayCell + '"]').addClass('selected-date');

		$('.selected-date-input').val(dayCell);
		$('.additional-info-input').val('');
		$('.Day-error-message-container').text('');
	}
});

$('.day-disable').change(function() {
	disabledDays = [];
	$('.day-disable:checked').each(function() {
		disabledDays.push(parseInt($(this).val()));
	});
	$('.calendar').fullCalendar('rerenderEvents');

	$('.calendar .fc-day').each(function() {
		var dayOfWeek = moment($(this).data('date')).day();
		if (disabledDays.includes(dayOfWeek)) {
			$(this).addClass('disabled-day');
		} else {
			$(this).removeClass('disabled-day');
		}
	});

	$('.Day-error-message-container').text('');
});

updateCalendarDimensions(); // 초기 캘린더 크기 업데이트
$(window).resize(function() {
	updateCalendarDimensions(); // 창 크기 변경 시 캘린더 크기 업데이트
});


function checkOpenHours() {
	const startTime = document.getElementById('startTime2').value;
	const endTime = document.getElementById('endTime2').value;

	const now = new Date();
	const currentTime = now.getHours() + ":" + (now.getMinutes() < 10 ? '0' : '') + now.getMinutes();

	const statusElement = document.getElementById('business_time');

	if (startTime && endTime) {
		if (currentTime >= startTime && currentTime < endTime) {
			statusElement.textContent = "영업중";
		} else {
			statusElement.textContent = "영업 마감";
		}
	} else {
		statusElement.textContent = "가게 준비중 입니다";
	}
}

// 30분(1800000ms)마다 상태를 업데이트합니다.
setInterval(checkOpenHours, 1800000);

// 페이지 로드 시 초기 상태 확인
checkOpenHours();






