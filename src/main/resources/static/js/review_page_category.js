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
            { name: '강남구', value: '37.517236,127.047325' },
            // 서울시의 서브 지역 정보가 추가됨
        ];
    }
    // 여러 지역에 대한 추가 설정
    // 각 지역마다 하위 지역의 이름과 좌표(위도, 경도)가 설정됨
    // 사용자가 첫 번째 지역을 선택하면, 두 번째 드롭다운에 맞는 하위 지역이 표시됨
    // 경기도, 부산, 대구 등 다른 지역도 유사한 방식으로 처리됨

    // 새로운 옵션을 추가
    for (var i = 0; i < options.length; i++) {
        var option = document.createElement('option');
        option.value = options[i].value;
        option.text = options[i].name;
        region2.add(option);  // region2 드롭다운에 옵션 추가
    }
}

// 지도를 띄우고 마커를 표시하는 함수
function showMap() {
    var mapDiv = document.getElementById('map');
    var closeButton = document.getElementById('closeMap');
    var selectedRegion = document.getElementById('region2').value;  // 사용자가 선택한 좌표 값
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

document.addEventListener('DOMContentLoaded', function() {
    const carouselItems = document.querySelectorAll('.carousel-item');
    const prevButton = document.querySelector('.carousel-control-prev');
    const nextButton = document.querySelector('.carousel-control-next');

    // 캐러셀 아이템이 1개 이하일 때, 이전/다음 버튼 숨김
    if (carouselItems.length <= 1) {
        if (prevButton) prevButton.style.display = 'none';
        if (nextButton) nextButton.style.display = 'none';
    }
});
