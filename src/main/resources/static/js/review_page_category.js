var map;
     var markers = [];
     var overlays = [];
     var currentOverlay = null;  // 현재 표시 중인 오버레이 저장

     // 서브 지역을 업데이트하는 함수
     function updateSubRegion() {
         var region1 = document.getElementById('region1').value;
         var region2 = document.getElementById('region2');
         region2.innerHTML = '';  // 기존 옵션 초기화

         // 선택된 지역에 따라 하위 지역을 동적으로 추가
         if (region1 === 'seoul') {
             var options = [
                 {name: '강남구', value: '37.517236,127.047325'},
                 {name: '서초구', value: '37.530125,126.975403'}
             ];
         } else if (region1 === 'busan') {
             var options = [
                 {name: '해운대구', value: '35.179554,129.075641'},
                 {name: '수영구', value: '35.127329,128.980051'}
             ];
         } else if (region1 === 'gyeonggi') {
             var options = [
                 {name: '권선구', value: '37.2414248,127.0161908'},
                 {name: '수영구', value: '35.127329,128.980051'}
             ];
         }

         // 새로운 옵션을 추가
         for (var i = 0; i < options.length; i++) {
             var option = document.createElement('option');
             option.value = options[i].value;
             option.text = options[i].name;
             region2.add(option);
         }
     }

     // 지도를 띄우고 마커를 표시하는 함수
     function showMap() {
         var mapDiv = document.getElementById('map');
         var closeButton = document.getElementById('closeMap');
         mapDiv.style.display = 'block';  // 지도를 보여줌
         closeButton.style.display = 'block';  // 닫기 버튼도 표시

         // 지도가 아직 생성되지 않은 경우에만 생성
         if (!map) {
             var container = mapDiv;
             var options = {
                 center: new kakao.maps.LatLng(37.517236, 127.047325),  // 기본 좌표 (서울)
                 level: 5
             };
             map = new kakao.maps.Map(container, options);  // 지도를 생성
         }

         // 기존 마커와 오버레이를 모두 삭제
         markers.forEach(marker => marker.setMap(null));
         overlays.forEach(overlay => overlay.setMap(null));  // 모든 오버레이 닫기
         markers = [];
         overlays = [];
         currentOverlay = null;  // 현재 오버레이 초기화

         // 페이지에 있는 가게 정보 가져오기
         var latElements = document.querySelectorAll('.store-lat');
         var lngElements = document.querySelectorAll('.store-lng');
         var nameElements = document.querySelectorAll('.store-name');  // 가게 이름 가져옴

         // 마커 생성 및 클릭 이벤트 추가 (클로저 사용)
         for (let i = 0; i < latElements.length; i++) {
             var lat = parseFloat(latElements[i].value);
             var lng = parseFloat(lngElements[i].value);
             var storeName = nameElements[i].value;  // 가게 이름

             // 위도와 경도 값이 유효한 경우에만 마커 생성
             if (!isNaN(lat) && !isNaN(lng)) {
                 var markerPosition = new kakao.maps.LatLng(lat, lng);
                 var marker = new kakao.maps.Marker({
                     position: markerPosition
                 });

                 marker.setMap(map);
                 markers.push(marker);

                 // 오버레이 생성 및 이벤트 핸들러 추가 (클로저로 묶음)
                 (function(marker, storeName, markerPosition) {
                     var content = `
                         <div class="m_card card" style="width: 15rem;">
                           <img src="https://via.placeholder.com/150" class="m_card-img-top" alt="가게 이미지">
                           <div class="m_card-body card-body">
                             <h5 class="m_card-title card-title">` + storeName + `</h5>
                           </div>
                         </div>
                     `;

                     // 오버레이 생성
                     var overlay = new kakao.maps.CustomOverlay({
                         content: content,
                         position: markerPosition,
                         yAnchor: 1.12  // 오버레이가 마커 위에 나타나도록 위치 조정
                     });

                     // 마커 클릭 이벤트 (토글 방식으로 오버레이 표시/닫기)
                     kakao.maps.event.addListener(marker, 'click', function() {
                         if (currentOverlay === overlay) {
                             currentOverlay.setMap(null);  // 클릭 시 현재 오버레이 닫기
                             currentOverlay = null;  // 오버레이 초기화
                         } else {
                             if (currentOverlay) {
                                 currentOverlay.setMap(null);  // 기존 오버레이 닫기
                             }
                             overlay.setMap(map);  // 새로운 오버레이 표시
                             currentOverlay = overlay;  // 현재 오버레이로 설정
                         }
                     });

                     overlays.push(overlay);  // 오버레이를 배열에 추가하여 추적
                 })(marker, storeName, markerPosition);
             }
         }

         // 마지막 마커로 지도의 중심을 이동
         if (markers.length > 0) {
             var lastMarkerPosition = markers[markers.length - 1].getPosition();
             map.setCenter(lastMarkerPosition);
         }
     }

     // 지도를 닫는 함수
     function closeMap() {
         var mapDiv = document.getElementById('map');
         var closeButton = document.getElementById('closeMap');
         mapDiv.style.display = 'none';  // 지도를 숨김
         closeButton.style.display = 'none';  // 닫기 버튼 숨김

         // 지도 닫을 때 오버레이도 모두 제거
         overlays.forEach(overlay => overlay.setMap(null));
         currentOverlay = null;  // 현재 오버레이 초기화
         overlays = [];  // 오버레이 배열 초기화
     }

//인원수, 달력, 메뉴 제이쿼리
$(document).ready(function() {
    const $rangeInput = $('#customRange2');
    const $rangeValueLabel = $('#rangeValue');
    const $P_C_btn = $('#toggleButton');
    const $ca_btn = $('.ca_button');
    const $rangeBox = $('#rangeBox');
    const $calendarInput = $('#calendar-input');
    const $calendarBox = $('.daterangepicker');
    const $menuBtn = $('.menu_btn');
    const $foodBox = $('.food_box');

    function updateRange() {
        const value = $rangeInput.val();
        $rangeValueLabel.text(`${value}`); // 박스 안의 텍스트 업데이트
        $P_C_btn.html(`인원수 <i class="fa-solid fa-user ml-1"></i> ${value}명`); // 버튼 텍스트 업데이트
        const percentage = (value / $rangeInput.attr('max')) * 100;
        $rangeInput.css('--value', `${percentage}%`);
    }

    // 범위 입력 값 변경 시 업데이트
    $rangeInput.on('input change', updateRange);

    // 초기 업데이트
    updateRange();

    // ca_Button 클릭 시 박스 숨김
    $ca_btn.on('click', function() {
        $rangeBox.removeClass('show').hide();
    });

    // 인원수 버튼 클릭 시 박스 토글
    $P_C_btn.on('click', function(event) {
        $rangeBox.toggle(); 
        event.stopPropagation(); // 이벤트 버블링 방지
    });

    // 달력 초기화
    $calendarInput.daterangepicker({
        singleDatePicker: true,
        alwaysShowCalendars: true,
        startDate: moment().startOf('day'),
        locale: {
            format: 'YYYY-MM-DD',
            daysOfWeek: ['일', '월', '화', '수', '목', '금', '토'],
            monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
            firstDay: 0,
            applyLabel: '적용',
            cancelLabel: '취소'
        }
    });

    // 달력 버튼 클릭 시 달력 표시
    $('#calendar-btn').on('click', function(event) {
        $calendarInput.trigger('click');
        event.stopPropagation(); // 이벤트 버블링 방지
    });

    // 메뉴 버튼 클릭 시 food_box 보이게
    $menuBtn.on('click', function(event) {
        $foodBox.toggle();
        event.stopPropagation(); // 이벤트 버블링 방지
    });

    // 문서 클릭 시 박스 숨기기
    $(document).on('click', function() {
        $rangeBox.hide();
        $calendarBox.hide();
        $foodBox.hide();
    });

    // 특정 박스를 클릭할 때는 이벤트가 닫히지 않도록 설정
    $rangeBox.on('click', function(event) {
        event.stopPropagation();
    });

    $calendarBox.on('click', function(event) {
        event.stopPropagation();
    });

    $foodBox.on('click', function(event) {
        event.stopPropagation();
    });
});

$(document).ready(function() {
    // 드롭다운 항목 클릭 시 버튼 텍스트 업데이트
    $('.dropdown-menu .dropdown-item').on('click', function(event) {
        // 기본 링크 동작 방지
        event.preventDefault();
        
        // 클릭한 항목의 텍스트를 가져옵니다
        const selectedText = $(this).text();
        // 버튼의 텍스트를 업데이트합니다
        $(this).closest('.dropdown').find('.dropdown-toggle').text(selectedText);
    });
});


document.addEventListener('DOMContentLoaded',
				function() {
					const carouselItems = document
							.querySelectorAll('.carousel-item');
					const prevButton = document
							.querySelector('.carousel-control-prev');
					const nextButton = document
							.querySelector('.carousel-control-next');

					if (carouselItems.length <= 1) {
						if (prevButton)
							prevButton.style.display = 'none';
						if (nextButton)
							nextButton.style.display = 'none';
					}
				});
				
							