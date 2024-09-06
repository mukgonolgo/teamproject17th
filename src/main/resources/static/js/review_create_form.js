let imageCount = 0;
       const maxImages = 5;

       function addImage(event) {
           if (imageCount >= maxImages) {
               alert("이미지는 최대 5개까지 추가할 수 있습니다.");
               return;
           }

           const reader = new FileReader();
           reader.onload = function () {
               const imgContainer = document.createElement('div');
               imgContainer.classList.add('img-container');

               const img = document.createElement('img');
               img.src = reader.result;
               img.classList.add('uploaded-image');

               // 이미지 삭제 시 커서를 포인터로 변경
               img.style.cursor = 'pointer';

               // 이미지 클릭 시 삭제되는 이벤트 추가
               img.addEventListener('click', function () {
                   imgContainer.remove();
                   imageCount--;

                   // 이미지가 삭제되면 + 버튼 다시 표시
                   if (imageCount < maxImages) {
                       document.getElementById('add-icon').style.display = 'flex';
                   }
               });

               imgContainer.appendChild(img);

               const iconContainer = document.querySelector('.icon-container');
               iconContainer.appendChild(imgContainer); // 이미지가 오른쪽에 추가되도록 위치 조정

               imageCount++;

               if (imageCount >= maxImages) {
                   document.getElementById('add-icon').style.display = 'none';
               }
           };
           reader.readAsDataURL(event.target.files[0]);
       }

       let S3_tags_input = $("#S3_tags_input");
       let tagGroup = $("#tagGroup");
       let tags_str = '';
       let S3_tagsOutput = $("#S3_tagsOutput");

       S3_tags_input.keyup(function (key) {
           if (key.keyCode == 13 || key.keyCode == 32) {
               let tag = $(this).val();
               tag = tag.replace(" ", "");
               tag = tag.replace(/[^\w\s가-힣ㄱ-ㅎㅏ-ㅣ]/g, '');
               if (tag == "") {
                   alert("입력된 값이 없습니다.(특수문자는 태그에 포함할 수 없습니다.)");
                   return;
               }
               tagGroup.append('<span class="badge badge-info badge-pill d-flex align-items-center p-2 mr-2">#' + tag + '<svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" class="bi bi-x-circle-fill ml-1" viewBox="0 0 16 16"><path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0M5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293z"/></svg></span>');
               tags_str += "#" + tag;
               S3_tagsOutput.val(tags_str);

               $(this).val('');
               console.log(tags_str);

               let tagNum = $("#tagGroup .badge").length;
               if (tagNum >= 5) {
                   $(this).css('visibility', 'hidden'); // 태그가 5개일 때 입력 필드를 숨기되, 공간은 유지
               }
           }
       });

       $(document).on("click", "span.badge", function (event) {
           let removedtag = $(this).text();
           tags_str = tags_str.replace(removedtag, '');
           S3_tagsOutput.val(tags_str);

           $(this).remove();

           let tagNum = $("#tagGroup .badge").length;

           if (tagNum < 5) {
               S3_tags_input.css('visibility', 'visible'); // 태그가 5개 미만일 때 입력 필드를 다시 표시
           }
       });

       new Vue({
           el: '#app',
           data: {
               selectedRating: 0, // 사용자 선택한 별점
           },
           computed: {
               ratingToPercent() {
                   return this.selectedRating * 20;
               }
           },
           methods: {
               setRating(n) {
                   if (this.selectedRating === n) {
                       // 동일한 별점을 두 번 클릭한 경우 초기화
                       this.selectedRating = 0;
                   } else {
                       this.selectedRating = n;
                   }

                   // star-ratings-fill의 넓이를 조정
                   const fillElement = document.querySelector('.star-ratings-fill');
                   fillElement.style.width = this.ratingToPercent + '%';
               }
           }
       });