// Ajax 요청 시 CSRF 토큰을 헤더에 추가하는 예제
const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

fetch('/your-action', {
    method: 'POST',
    headers: {
        [header]: token,  // CSRF 토큰을 헤더에 추가
        'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
});
