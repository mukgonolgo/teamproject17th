document.addEventListener('DOMContentLoaded', function () {
    // 변수 초기화
	const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

    var stompClient = null;
    var roomId = '';
	var username = document.querySelector('meta[name="username"]').getAttribute('content');
	 var userID = document.querySelector('meta[name="userID"]').getAttribute('content');

    // Fetch and initialize username and userID using AJAX
	fetch('/api/user/info')
	    .then(response => {
	        if (!response.ok) {
	            throw new Error('Network response was not ok');
	        }
	        return response.json();
	    })
	    .then(data => {

	        console.log('Username:', username);
	        console.log('User ID:', userID);
	        alert(username + ":" + userID);
	    })
	    .catch(error => console.error('Error fetching user info:', error));

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

    document.querySelectorAll('#chatButton').forEach(button => {
        button.addEventListener('click', function() {
            const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');	  
            const userId = document.querySelector('meta[name="user-id"]').getAttribute('content');
            
            console.log('@@@@@@@@@');
            console.log('User ID:', userId);
            console.log('Username:', username);
            console.log('CSRF Token:', csrfToken);

            fetch('/api/chat/saveUser', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({
                    userId: userId,
                    username: username
					
                })
            })
            .then(response => {
                if (response.ok) {
                    alert('User information saved successfully!');
                } else {
                    alert('Failed to save user information.');
                }
            })
            .catch(error => console.error('Error:', error));
        });
    });

    function openChatRoom(event) {
        const roomElement = event.target.closest('.S2_card');
        if (roomElement) {
            roomId = roomElement.getAttribute('data-room-id');
            if (roomId) {
                selectChatRoom(roomId);
                const modal = document.getElementById('chatModal');
                modal.classList.add('show-modal');
            } else {
                console.error('Room ID is missing');
            }

            fetch(`/room/${roomId}/exists`)
                .then(response => response.json())
                .then(data => {
                    if (data.exists) {
                        console.log('채팅방이 이미 존재합니다.');
                        alert(`채팅방 '${roomId}'이(가) 이미 존재합니다.`);
                        roomId = data.roomId;
                        selectChatRoom(roomId);
                    } else {
                        fetch('/create-room', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                                'X-CSRF-TOKEN': getCsrfToken()
                            },
                            body: JSON.stringify({ roomId: roomId })
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
    }

    function selectChatRoom(roomId) {
        console.log(`채팅방 데이터 가져오기: ${roomId}`);

        fetch(`/chat/api/${roomId}`)
            .then(response => response.json())
            .then(data => {
                if (data && data.chatList) {
                    loadChat(data.chatList);
                    initializeWebSocket();
                } else {
                    console.error('잘못된 데이터 형식:', data);
                }
            })
            .catch(error => console.error('채팅방 데이터 가져오기 오류:', error));

        $.getJSON('/room/' + roomId + '/info')
            .done(function(data) {
                console.log('받은 데이터:', data);

                $('#roomName').text(data.roomName);
                $('#currentMembers_' + roomId).text(data.currentMembers);
                $('#maxMembers_' + roomId).text(data.maxMembers);

                var membersList = $('#membersList_' + roomId);
                membersList.empty();

                data.members.forEach(function(member) {
                    console.log('멤버:', member);
                    var listItem = $('<li>').text(member.username);
                    membersList.append(listItem);
                });
            })
            .fail(function(jqXHR, textStatus, errorThrown) {
                console.error('채팅방 정보 로드 실패:', textStatus, errorThrown);
                alert('채팅방 정보를 로드하는 데 실패했습니다.');
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
            
            stompClient.subscribe('/topic/' + roomId, function (chatMessage) {
                console.log('Received message: ', chatMessage.body);
                showChat(JSON.parse(chatMessage.body));
            });

            stompClient.subscribe('/topic/' + roomId + '/info', function (roomInfo) {
                console.log('Received room info: ', roomInfo.body);
                updateRoomInfo(JSON.parse(roomInfo.body));
            });
            
            joinRoom();
        }, function (error) {
            console.error('Error: ' + error);
        });
    }

    function sendChat() {
        var message = $("#message").val();
        console.log("Sending message from User ID:", userID);
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
        $('#currentMembers_' + roomId).text(data.currentMembers);
        $('#maxMembers_' + roomId).text(data.maxMembers);
        
        var membersList = $('#membersList_' + roomId);
        membersList.empty();
        
        if (Array.isArray(data.members)) {
            data.members.forEach(function(member) {
                console.log('Member:', member);
                var listItem = $('<li>').text(member.username);
                membersList.append(listItem);
            });
        } else {
            console.error('Members data is not an array:', data.members);
        }
        
        alert('Room info updated');
    }

    function getCsrfToken() {
        return document.querySelector('meta[name="csrf-token"]').getAttribute('content');
    }

    function joinRoom() {
        if (stompClient && stompClient.ws && stompClient.ws.readyState === WebSocket.OPEN && roomId) {
            stompClient.send("/app/chat/join", {}, JSON.stringify({
                'roomId': roomId,
                'userId': userID
            }));
            console.log('Joined room:', roomId);
        }
    }
});
