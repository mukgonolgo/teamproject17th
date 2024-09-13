document.addEventListener('DOMContentLoaded', function () {
    // 변수 초기화
    var stompClient = null;
    var roomId = ''; // 빈 문자열로 초기화
    var username = ''; // 문자열
    var userID = ''; // 사용자 ID, 사용자 로그인 시 설정해야 함

    // Fetch and initialize username and userID
    username = document.querySelector('meta[name="username"]').getAttribute('content');
    userID = document.querySelector('meta[name="user-id"]').getAttribute('content');
    console.log('Username:', username);
    console.log('User ID:', userID);

    // Handle modals
    document.querySelectorAll('.trigger').forEach(trigger => {
        trigger.addEventListener('click', openChatRoom);
    });

    const closeButton = document.getElementById('closeModal');
    if (closeButton) {
        closeButton.addEventListener('click', function () {
            document.querySelectorAll('.modal').forEach(modal => {
                modal.classList.remove('show-modal');
            });
        });
    }

    // Form submit event handler to prevent default action
    $("#sendMessageForm").on('submit', function (e) {
        e.preventDefault();
    });

    $("#send").click(function () {
        sendChat();
    });

/*채팅방 만드는 함수인데 사용안해서 주석처리
    // Create room button event listener
    document.getElementById('createRoomButton').addEventListener('click', function() {
        const roomName = document.getElementById('createRoomName').value;
        if (roomName) {
            const newRoomId = uuid.v4();
            checkAndCreateChatRoom(newRoomId, roomName);
        } else {
            alert('채팅방 이름을 입력하세요.');
        }
    });*/

    function openChatRoom(event) {
        const roomElement = event.target.closest('.S2_card');
        if (roomElement) {
            roomId = roomElement.getAttribute('data-room-id');
            if (roomId) {
                selectChatRoom(roomId);
                document.querySelectorAll('.modal').forEach(modal => {
                    modal.classList.add('show-modal');
                });
            } else {
                console.error('Room ID is missing');
            }
        }
    }

    function checkAndCreateChatRoom(roomId, roomName) {
        fetch(`/room/${roomId}/exists`)
            .then(response => response.json())
            .then(data => {
                if (data.exists) {
                    console.log('채팅방이 이미 존재합니다.');
                    alert(`채팅방 '${roomId}'이(가) 이미 존재합니다.`);
                    // 방에 참여하도록 설정
                    roomId = data.roomId;
                    selectChatRoom(roomId);
                } else {
                    fetch('/create-room', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'X-CSRF-TOKEN': getCsrfToken()
                        },
                        body: JSON.stringify({ roomId: roomId, name: roomName })
                    })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            console.log('채팅방 생성 성공:', data.roomId);
                            alert(`채팅방 '${data.roomId}'이(가) 성공적으로 생성되었습니다.`);
                            roomId = data.roomId;
                            selectChatRoom(roomId);
                        } else {
                            console.error('채팅방 생성 실패:', data.message);
                            alert('채팅방 생성에 실패했습니다: ' + data.message);
                        }
                    })
                    .catch(error => console.error('채팅방 생성 중 오류 발생:', error));
                }
            })
            .catch(error => console.error('채팅방 존재 확인 중 오류 발생:', error));
    }

    function selectChatRoom(roomId) {
        console.log(`Fetching chat data for roomId: ${roomId}`);

        // Fetch chat data
        fetch(`/chat/api/${roomId}`)
            .then(response => response.json())
            .then(data => {
                if (data && data.chatList) {
                    loadChat(data.chatList);
                    initializeWebSocket();
                } else {
                    console.error('Invalid data format:', data);
                }
            })
            .catch(error => console.error('Error fetching chat room data:', error));

        // Fetch and initialize room info
        $.getJSON('/room/' + roomId + '/info')
            .done(function(data) {
                console.log('Received data:', data); // 데이터 확인

                $('#roomName').text(data.roomName); // 채팅방 이름
                $('#currentMembers_' + roomId).text(data.currentMembers); // 현재 멤버 수
                $('#maxMembers_' + roomId).text(data.maxMembers); // 최대 멤버 수
                
                var membersList = $('#membersList_' + roomId);
                membersList.empty(); // 기존 목록을 비움

                data.members.forEach(function(member) {
                    console.log('Member:', member); // 멤버 데이터 확인
                    var listItem = $('<li>').text(member.username); // 사용자 이름으로 목록 아이템 생성
                    membersList.append(listItem);
                });
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.error('Failed to load chat room information:', textStatus, errorThrown);
                alert('Failed to load chat room information.');
            });
    }

    function initializeWebSocket() {
        if (!roomId) {
            console.error('Room ID is not set');
            return;
        }
        var socket = new SockJS('/ws/chat');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            
            // 채팅 메시지 수신
            stompClient.subscribe('/topic/' + roomId, function (chatMessage) {
                console.log('Received message: ', chatMessage.body);
                showChat(JSON.parse(chatMessage.body));
            });

            // 채팅방 정보 수신
            stompClient.subscribe('/topic/' + roomId + '/info', function (roomInfo) {
                console.log('Received room info: ', roomInfo.body);
                updateRoomInfo(JSON.parse(roomInfo.body));
            });
            
            // 방에 참여
            joinRoom();
        }, function (error) {
            console.error('Error: ' + error);
        });
    }

    function sendChat() {
        var message = $("#message").val();
        if (message !== "" && stompClient && stompClient.ws && stompClient.ws.readyState === WebSocket.OPEN && roomId) {
            stompClient.send("/app/chat/" + roomId, {}, JSON.stringify({
                'roomId': roomId,
                'sender': username,
                'message': message,
                'sendDate': new Date().toISOString()
            }));

            $("#message").val('');
            console.log("Message sent");
        } else {
            console.log("WebSocket connection is not established, message is empty, or roomId is missing.");
        }
    }

    function loadChat(chatList) {
        if (!Array.isArray(chatList)) {
            console.error('Chat list is not an array or is undefined:', chatList);
            return;
        }

        const chatContainer = document.getElementById('chatting');
        chatContainer.innerHTML = '';

        chatList.forEach(chat => {
            var chatMessage = document.createElement('tr');
            var chatContent = document.createElement('td');
            chatContent.textContent = chat.senderId === userID ? chat.message : `[${chat.sender}] ${chat.message}`;
            chatMessage.appendChild(chatContent);
            chatContainer.appendChild(chatMessage);
        });
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }

    function showChat(chatMessage) {
        var chatMessageElement = document.createElement('tr');
        var chatContentElement = document.createElement('td');
        chatContentElement.textContent = chatMessage.senderId === userID ? chatMessage.message : `[${chatMessage.sender}] ${chatMessage.message}`;
        chatMessageElement.appendChild(chatContentElement);
        $("#chatting").append(chatMessageElement);
        const chatContainer = document.getElementById('chatting');
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }

	function updateRoomInfo(data) {
	    $('#currentMembers_' + roomId).text(data.currentMembers); // 현재 멤버 수
	    $('#maxMembers_' + roomId).text(data.maxMembers); // 최대 멤버 수
	    
	    var membersList = $('#membersList_' + roomId);
	    membersList.empty(); // 기존 목록을 비움
	    
	    // 데이터가 배열인지 확인
	    if (Array.isArray(data.members)) {
	        data.members.forEach(function(member) {
	            console.log('Member:', member); // 멤버 데이터 확인
	            var listItem = $('<li>').text(member.username); // 사용자 이름으로 목록 아이템 생성
	            membersList.append(listItem);
	        });
	    } else {
	        console.error('Members data is not an array:', data.members);
	    }
	    
	    alert('test'); // 확인용 alert
	}


    function getCsrfToken() {
        return document.querySelector('meta[name="csrf-token"]').getAttribute('content');
    }

    function joinRoom() {
        if (stompClient && stompClient.ws && stompClient.ws.readyState === WebSocket.OPEN && roomId) {
            stompClient.send("/app/chat/" + roomId + "/join", {}, JSON.stringify({
                'roomId': roomId,
                'username': username
            }));
        }
    }
	$("#leaveRoomButton").click(function() {
	    if (roomId && username) {
	        fetch(`/room/${roomId}/leave`, {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/json',
	                'X-CSRF-TOKEN': getCsrfToken()
	            },
	            body: JSON.stringify({ username: username })
	        })
	        .then(response => response.json())
	        .then(data => {
	            if (data.success) {
	                console.log(data.message);
	                alert('채팅방에서 나가셨습니다.');
	                document.querySelectorAll('.modal').forEach(modal => {
	                    modal.classList.remove('show-modal');
	                });
	            } else {
	                console.error(data.message);
	                alert('채팅방 나가기에 실패했습니다.');
	            }
	        })
	        .catch(error => console.error('Error:', error));
	    } else {
	        console.error('roomId or username is missing');
	    }
});

function updateChatRoomStatus() {
    fetch('/chat-room-status')
        .then(response => response.json())
        .then(data => {
            // 각 채팅방에 대한 현재 인원 수 업데이트
            data.forEach(roomStatus => {
                const currentMembersElement = document.getElementById(`currentMembers_${roomStatus.roomID}`);
                if (currentMembersElement) {
                    currentMembersElement.textContent = roomStatus.currentMembers;
                } else {
                    console.error(`Element with ID "currentMembers_${roomStatus.roomID}" not found.`);
                }
            });
        })
        .catch(error => console.error('Error fetching chat room status:', error));
}

document.addEventListener('DOMContentLoaded', function() {
    setInterval(updateChatRoomStatus, 1000);
});
});
