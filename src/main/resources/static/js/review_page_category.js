function updateSubRegion() {
    var region1 = document.getElementById('region1').value;
    var region2 = document.getElementById('region2');
    region2.innerHTML = '';  // 기존 옵션 초기화

    var options = []; // 옵션 배열 초기화
       // 서울특별시
	   if (region1 === 'seoul') {
	          options = [
	              { name: '강남구', value: '강남구' },
	              { name: '강동구', value: '강동구' },
	              { name: '강북구', value: '강북구' },
	              { name: '강서구', value: '강서구' },
	              { name: '관악구', value: '관악구' },
	              { name: '광진구', value: '광진구' },
	              { name: '구로구', value: '구로구' },
	              { name: '금천구', value: '금천구' },
	              { name: '노원구', value: '노원구' },
	              { name: '도봉구', value: '도봉구' },
	              { name: '동대문구', value: '동대문구' },
	              { name: '동작구', value: '동작구' },
	              { name: '마포구', value: '마포구' },
	              { name: '서대문구', value: '서대문구' },
	              { name: '서초구', value: '서초구' },
	              { name: '성동구', value: '성동구' },
	              { name: '성북구', value: '성북구' },
	              { name: '송파구', value: '송파구' },
	              { name: '양천구', value: '양천구' },
	              { name: '영등포구', value: '영등포구' },
	              { name: '용산구', value: '용산구' },
	              { name: '은평구', value: '은평구' },
	              { name: '종로구', value: '종로구' },
	              { name: '중구', value: '중구' },
	              { name: '중랑구', value: '중랑구' }
	          ];
	      } else if (region1 === 'busan') {
	          options = [
	              { name: '강서구', value: '강서구' },
	              { name: '금정구', value: '금정구' },
	              { name: '기장군', value: '기장군' },
	              { name: '남구', value: '남구' },
	              { name: '동구', value: '동구' },
	              { name: '부산진구', value: '부산진구' },
	              { name: '북구', value: '북구' },
	              { name: '사하구', value: '사하구' },
	              { name: '서구', value: '서구' },
	              { name: '수영구', value: '수영구' },
	              { name: '영도구', value: '영도구' },
	              { name: '중구', value: '중구' },
	              { name: '해운대구', value: '해운대구' },
	              { name: '연제구', value: '연제구' },
	              { name: '동래구', value: '동래구' }
	          ];
	      } else if (region1 === 'gyeonggi') {
	          options = [
	              { name: '수원시', value: '수원시' },
	              { name: '성남시', value: '성남시' },
	              { name: '고양시', value: '고양시' },
	              { name: '용인시', value: '용인시' },
	              { name: '안양시', value: '안양시' },
	              { name: '부천시', value: '부천시' },
	              { name: '화성시', value: '화성시' },
	              { name: '남양주시', value: '남양주시' },
	              { name: '파주시', value: '파주시' },
	              { name: '평택시', value: '평택시' },
	              { name: '시흥시', value: '시흥시' },
	              { name: '광명시', value: '광명시' },
	              { name: '군포시', value: '군포시' },
	              { name: '이천시', value: '이천시' },
	              { name: '오산시', value: '오산시' }
	          ];
	      } else if (region1 === 'daegu') {
	          options = [
	              { name: '중구', value: '중구' },
	              { name: '서구', value: '서구' },
	              { name: '남구', value: '남구' },
	              { name: '동구', value: '동구' },
	              { name: '달서구', value: '달서구' },
	              { name: '북구', value: '북구' },
	              { name: '수성구', value: '수성구' },
	              { name: '달성군', value: '달성군' }
	          ];
	      } else if (region1 === 'incheon') {
	          options = [
	              { name: '중구', value: '중구' },
	              { name: '동구', value: '동구' },
	              { name: '남동구', value: '남동구' },
	              { name: '연수구', value: '연수구' },
	              { name: '부평구', value: '부평구' },
	              { name: '계양구', value: '계양구' },
	              { name: '서구', value: '서구' },
	              { name: '미추홀구', value: '미추홀구' }
	          ];
	      } else if (region1 === 'daejeon') {
	          options = [
	              { name: '동구', value: '동구' },
	              { name: '중구', value: '중구' },
	              { name: '서구', value: '서구' },
	              { name: '유성구', value: '유성구' },
	              { name: '대덕구', value: '대덕구' }
	          ];
	      } else if (region1 === 'gwangju') {
	          options = [
	              { name: '동구', value: '동구' },
	              { name: '서구', value: '서구' },
	              { name: '남구', value: '남구' },
	              { name: '북구', value: '북구' },
	              { name: '광산구', value: '광산구' }
	          ];
	      } else if (region1 === 'ulsan') {
	          options = [
	              { name: '중구', value: '중구' },
	              { name: '남구', value: '남구' },
	              { name: '동구', value: '동구' },
	              { name: '북구', value: '북구' },
	              { name: '울주군', value: '울주군' }
	          ];
	      } else if (region1 === 'sejong') {
	          options = [
	              { name: '조치원읍', value: '조치원읍' },
	              { name: '연서면', value: '연서면' },
	              { name: '부강면', value: '부강면' },
	              { name: '연동면', value: '연동면' },
	              { name: '소정면', value: '소정면' }
	          ];
	      } else if (region1 === 'gangwon') {
	          options = [
	              { name: '춘천시', value: '춘천시' },
	              { name: '원주시', value: '원주시' },
	              { name: '강릉시', value: '강릉시' },
	              { name: '동해시', value: '동해시' },
	              { name: '태백시', value: '태백시' },
	              { name: '속초시', value: '속초시' },
	              { name: '양양군', value: '양양군' },
	              { name: '홍천군', value: '홍천군' },
	              { name: '횡성군', value: '횡성군' }
	          ];
	      } else if (region1 === 'chungbuk') {
	          options = [
	              { name: '청주시', value: '청주시' },
	              { name: '충주시', value: '충주시' },
	              { name: '제천시', value: '제천시' },
	              { name: '보은군', value: '보은군' },
	              { name: '옥천군', value: '옥천군' },
	              { name: '음성군', value: '음성군' },
	              { name: '진천군', value: '진천군' },
	              { name: '괴산군', value: '괴산군' }
	          ];
	      } else if (region1 === 'chungnam') {
	          options = [
	              { name: '천안시', value: '천안시' },
	              { name: '공주시', value: '공주시' },
	              { name: '보령시', value: '보령시' },
	              { name: '아산시', value: '아산시' },
	              { name: '서산시', value: '서산시' },
	              { name: '당진시', value: '당진시' },
	              { name: '예산군', value: '예산군' },
	              { name: '홍성군', value: '홍성군' }
	          ];
	      } else if (region1 === 'jeonbuk') {
	          options = [
	              { name: '전주시', value: '전주시' },
	              { name: '군산시', value: '군산시' },
	              { name: '익산시', value: '익산시' },
	              { name: '정읍시', value: '정읍시' },
	              { name: '남원시', value: '남원시' },
	              { name: '김제시', value: '김제시' },
	              { name: '완주군', value: '완주군' },
	              { name: '진안군', value: '진안군' }
	          ];
	      } else if (region1 === 'jeonnam') {
	          options = [
	              { name: '목포시', value: '목포시' },
	              { name: '여수시', value: '여수시' },
	              { name: '순천시', value: '순천시' },
	              { name: '나주시', value: '나주시' },
	              { name: '광양시', value: '광양시' },
	              { name: '담양군', value: '담양군' },
	              { name: '곡성군', value: '곡성군' },
	              { name: '구례군', value: '구례군' }
	          ];
	      } else if (region1 === 'gyeongbuk') {
	          options = [
	              { name: '포항시', value: '포항시' },
	              { name: '경주시', value: '경주시' },
	              { name: '김천시', value: '김천시' },
	              { name: '안동시', value: '안동시' },
	              { name: '구미시', value: '구미시' },
	              { name: '영주시', value: '영주시' },
	              { name: '상주시', value: '상주시' },
	              { name: '문경시', value: '문경시' }
	          ];
	      } else if (region1 === 'gyeongnam') {
	          options = [
	              { name: '창원시', value: '창원시' },
	              { name: '진주시', value: '진주시' },
	              { name: '통영시', value: '통영시' },
	              { name: '사천시', value: '사천시' },
	              { name: '김해시', value: '김해시' },
	              { name: '밀양시', value: '밀양시' },
	              { name: '거제시', value: '거제시' },
	              { name: '양산시', value: '양산시' }
	          ];
	      } else if (region1 === 'jeju') {
	          options = [
	              { name: '제주시', value: '제주시' },
	              { name: '서귀포시', value: '서귀포시' },
	              { name: '성산읍', value: '성산읍' },
	              { name: '표선면', value: '표선면' },
	              { name: '한림읍', value: '한림읍' }
	          ];
	      }
	      // 새로운 옵션을 추가
	       options.forEach(function(option) {
	           var newOption = document.createElement('option');
	           newOption.value = option.value;
	           newOption.text = option.name;
	           region2.add(newOption);
	       });

	       // 하위 지역 선택 초기화
	       region2.value = '전체'; // 하위 지역 선택 초기화
	   }

	   function filterReviews() {
	       var selectedRegion = document.getElementById('region2').value;

	       if (selectedRegion === '') {
	           alert("하위 지역을 선택하세요."); // 하위 지역을 선택하지 않은 경우 알림
	       } else if (selectedRegion === '전체') {
	           window.location.href = '/review'; // 전체 리뷰로 이동
	       } else {
	           window.location.href = '/review?region=' + encodeURIComponent(selectedRegion); // 하위 지역을 포함하여 리뷰 페이지로 이동
	       }
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
