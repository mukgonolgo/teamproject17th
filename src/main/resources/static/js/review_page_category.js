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
       // 서울특별시
               if (region1 === 'seoul') {
                   var options = [
                       { name: '강남구', value: '37.517236,127.047325' },
                       { name: '서초구', value: '37.530125,126.975403' },
                       { name: '중구', value: '37.566535,126.977969' },
                       { name: '종로구', value: '37.579617,126.977041' },
                       { name: '은평구', value: '37.615560,126.922700' },
                       { name: '송파구', value: '37.512409,127.102719' }
                   ];
               }
               // 부산광역시
               else if (region1 === 'busan') {
                   var options = [
                       { name: '해운대구', value: '35.179554,129.075641' },
                       { name: '수영구', value: '35.127329,128.980051' },
                       { name: '부산진구', value: '35.162174,129.062239' },
                       { name: '사하구', value: '35.104603,128.966589' }
                   ];
               }
               // 경기도
               else if (region1 === 'gyeonggi') {
                   var options = [
                       { name: '수원시', value: '37.263573,127.028601' },
                       { name: '성남시', value: '37.420091,127.126569' },
                       { name: '고양시', value: '37.658359,126.832020' },
                       { name: '용인시', value: '37.241086,127.177553' },
                       { name: '안양시', value: '37.394252,126.956821' },
                       { name: '부천시', value: '37.503413,126.766030' }
                   ];
               }
               // 대구광역시
               else if (region1 === 'daegu') {
                   var options = [
                       { name: '중구', value: '35.872374,128.602317' },
                       { name: '서구', value: '35.879831,128.582255' },
                       { name: '남구', value: '35.869047,128.611247' },
                       { name: '동구', value: '35.880163,128.655159' },
                       { name: '달서구', value: '35.839431,128.557937' }
                   ];
               }
               // 인천광역시
               else if (region1 === 'incheon') {
                   var options = [
                       { name: '중구', value: '37.473620,126.621263' },
                       { name: '동구', value: '37.473759,126.648064' },
                       { name: '남동구', value: '37.447287,126.731619' },
                       { name: '연수구', value: '37.410298,126.678831' },
                       { name: '부평구', value: '37.507378,126.721366' }
                   ];
               }
               // 대전광역시
               else if (region1 === 'daejeon') {
                   var options = [
                       { name: '동구', value: '36.335154,127.423918' },
                       { name: '중구', value: '36.325734,127.423080' },
                       { name: '서구', value: '36.354727,127.384534' },
                       { name: '유성구', value: '36.362391,127.356633' },
                       { name: '대덕구', value: '36.346191,127.414214' }
                   ];
               }
               // 광주광역시
               else if (region1 === 'gwangju') {
                   var options = [
                       { name: '동구', value: '35.146637,126.923962' },
                       { name: '서구', value: '35.152709,126.890794' },
                       { name: '남구', value: '35.133160,126.902487' },
                       { name: '북구', value: '35.174079,126.915798' },
                       { name: '광산구', value: '35.139188,126.792605' }
                   ];
               }
               // 울산광역시
               else if (region1 === 'ulsan') {
                   var options = [
                       { name: '중구', value: '35.569621,129.332290' },
                       { name: '남구', value: '35.543962,129.317137' },
                       { name: '동구', value: '35.504022,129.419123' },
                       { name: '북구', value: '35.582685,129.361746' },
                       { name: '울주군', value: '35.562233,129.242067' }
                   ];
               }
               // 세종특별자치시
               else if (region1 === 'sejong') {
                   var options = [
                       { name: '조치원읍', value: '36.602894,127.297276' },
                       { name: '연서면', value: '36.637469,127.226716' },
                       { name: '부강면', value: '36.524288,127.380499' },
                       { name: '연동면', value: '36.561530,127.305331' },
                       { name: '소정면', value: '36.622017,127.160071' }
                   ];
               }
               // 강원도
               else if (region1 === 'gangwon') {
                   var options = [
                       { name: '춘천시', value: '37.881315,127.729970' },
                       { name: '원주시', value: '37.342218,127.920162' },
                       { name: '강릉시', value: '37.751853,128.876057' },
                       { name: '동해시', value: '37.524719,129.114324' },
                       { name: '태백시', value: '37.164428,128.985048' }
                   ];
               }
               // 충청북도
               else if (region1 === 'chungbuk') {
                   var options = [
                       { name: '청주시', value: '36.642434,127.489031' },
                       { name: '충주시', value: '36.991290,127.926999' },
                       { name: '제천시', value: '37.132570,128.190873' },
                       { name: '보은군', value: '36.489687,127.729491' },
                       { name: '옥천군', value: '36.306039,127.571646' }
                   ];
               }
               // 충청남도
               else if (region1 === 'chungnam') {
                   var options = [
                       { name: '천안시', value: '36.815105,127.113540' },
                       { name: '공주시', value: '36.446700,127.119052' },
                       { name: '보령시', value: '36.333417,126.612472' },
                       { name: '아산시', value: '36.789894,127.001849' },
                       { name: '서산시', value: '36.784517,126.450392' }
                   ];
               }
               // 전라북도
               else if (region1 === 'jeonbuk') {
                   var options = [
                       { name: '전주시', value: '35.824223,127.147953' },
                       { name: '군산시', value: '35.967510,126.736878' },
                       { name: '익산시', value: '35.950563,126.957580' },
                       { name: '정읍시', value: '35.569321,126.856084' },
                       { name: '남원시', value: '35.416426,127.390963' }
                   ];
               }
               // 전라남도
               else if (region1 === 'jeonnam') {
                   var options = [
                       { name: '목포시', value: '34.811835,126.392166' },
                       { name: '여수시', value: '34.760391,127.662222' },
                       { name: '순천시', value: '34.950641,127.487597' },
                       { name: '나주시', value: '35.016873,126.712446' },
                       { name: '광양시', value: '34.940696,127.695876' }
                   ];
               }
               // 경상북도
               else if (region1 === 'gyeongbuk') {
                   var options = [
                       { name: '포항시', value: '36.019033,129.343481' },
                       { name: '경주시', value: '35.856171,129.224748' },
                       { name: '김천시', value: '36.139316,128.113307' },
                       { name: '안동시', value: '36.568376,128.729357' },
                       { name: '구미시', value: '36.119484,128.344619' }
                   ];
               }
               // 경상남도
               else if (region1 === 'gyeongnam') {
                   var options = [
                       { name: '창원시', value: '35.227779,128.681119' },
                       { name: '진주시', value: '35.180195,128.107621' },
                       { name: '통영시', value: '34.854569,128.433586' },
                       { name: '사천시', value: '35.003815,128.064771' },
                       { name: '김해시', value: '35.228338,128.889538' }
                   ];
               }
               // 제주특별자치도
               else if (region1 === 'jeju') {
                   var options = [
                       { name: '제주시', value: '33.499621,126.531188' },
                       { name: '서귀포시', value: '33.253077,126.561080' },
                       { name: '성산읍', value: '33.386824,126.880978' },
                       { name: '표선면', value: '33.333826,126.789428' },
                       { name: '한림읍', value: '33.407676,126.271388' }
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
    // 지도를 띄우고 마커를 표시하는 함수
    // 지도를 띄우고 마커를 표시하는 함수
    function showMap() {
        var mapDiv = document.getElementById('map');
        var closeButton = document.getElementById('closeMap');
        var selectedRegion = document.getElementById('region2').value; // 사용자가 선택한 좌표 값
        var coords = selectedRegion.split(',');
        var lat = parseFloat(coords[0]);  // 선택된 위도 값
        var lng = parseFloat(coords[1]);  // 선택된 경도 값

        mapDiv.style.display = 'block';  // 지도를 보여줌
        closeButton.style.display = 'block';  // 닫기 버튼도 표시

        // 지도가 아직 생성되지 않은 경우에만 생성
        if (!map) {
            var container = mapDiv;
            var options = {
                center: new kakao.maps.LatLng(lat, lng),  // 선택된 지역의 좌표로 지도 중심을 설정
                level: 5  // 지도 줌 레벨
            };
            map = new kakao.maps.Map(container, options);  // 지도를 생성
        } else {
            // 선택한 지역으로 지도 중심을 이동
            var moveLatLon = new kakao.maps.LatLng(lat, lng);
            map.setCenter(moveLatLon);
        }

        // 기존 마커와 오버레이는 유지, 추가 마커를 생성
        var latElements = document.querySelectorAll('.store-lat');
        var lngElements = document.querySelectorAll('.store-lng');
        var nameElements = document.querySelectorAll('.store-name');  // 가게 이름 가져옴

        // 마커 생성 및 클릭 이벤트 추가 (클로저 사용)
        for (let i = 0; i < latElements.length; i++) {
            var storeLat = parseFloat(latElements[i].value);
            var storeLng = parseFloat(lngElements[i].value);
            var storeName = nameElements[i].value;  // 가게 이름

            // 위도와 경도 값이 유효한 경우에만 마커 생성
            if (!isNaN(storeLat) && !isNaN(storeLng)) {
                var markerPosition = new kakao.maps.LatLng(storeLat, storeLng);
                var marker = new kakao.maps.Marker({
                    position: markerPosition
                });

                marker.setMap(map);  // 마커를 지도에 표시
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
				
							