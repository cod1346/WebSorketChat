<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

</head>
<body>
	<input id="username" hidden="" value=${username }>
	<input id="roomNo" hidden="" value=${roomNo }>
	<div id="chat">
		<div id="chatarea" style="width: 300px; height: 500px; border: 1px solid black; overflow: scroll;" ></div>
		<input type="text" id="message" placeholder="메세지"/>
		<input type="button" id="send" value="보내기" />
		<input type="button" id="exit" value="나가기" />
	</div>

	<div>
		<input type="file" id="file">
		<button hidden="" id="sendFileBtn" type="button">파일전송</button>
	</div>
	
	<button id="dbImoBtn" type="button">db이모티콘</button>
	<button id="normalImoBtn" type="button">이모티콘</button>

	<div id= "dbImo" hidden="">
	</div>
	<div id= "normalImo" hidden="">
		<button id="sad"><img alt="" src="../resources/sad.jpg" width="30"></button>
		<button id="smile"><img alt="" src="../resources/smile.jpg" width="30"></button>
		<button id="angry"><img alt="" src="../resources/angry.jpg" width="30"></button>
	</div>
	
</body>
<script src="../resources/sockjs.js"></script>
<script type="text/javascript">
var username = document.querySelector("#username")
var sock;
connect()
function connect(){
		sock = new WebSocket("ws://localhost:8080/chat-ws/"+document.querySelector("#roomNo").value);
//	sock = new SockJS("/chat-ws");
	
	
		sock.onopen=onOpen;
		sock.onmessage = onMessage;
}

function onOpen(){
	var data = {
			roomNo : document.querySelector("#roomNo").value,
		    username: document.querySelector("#username").value,
		    message: "",
		    type:"enter"
		};
		sock.send(JSON.stringify(data));
}

document.getElementById("send").addEventListener("click", ()=> {
	send();
});

function send(){
	
	var msg = document.getElementById("message").value;
    var username = document.getElementById("username").value;
 // 현재 날짜를 가져오기
	// 메시지와 닉네임을 객체로 만들기
	var data = {
			roomNo : document.querySelector("#roomNo").value,
		    username: document.querySelector("#username").value,
		    message: msg,
		    type:"talk"
	};
	sock.send(JSON.stringify(data));

	document.getElementById("message").value = "";
}

var filename="";
var fileUser="";
function onMessage(evt) {
    var data = evt.data;
	console.log(data)
	
	if (data.toString().startsWith("파일보냄+")) {
	    console.log("'파일보냄+ '로 시작");
		const dataString = (typeof data === 'string') ? data : data.toString();
		const regex = /(?<=\+)([^+]+)\+(.+)$/;
	
		const matches = dataString.match(regex);
	
	    const firstPart = matches[1]; // "ㅁㅁㅇㄴ+ㅇㄻㄹ+ㅇㄹㅇㄴㄹ+ㅁㅇㄴㄹ+ㅁ"
		const secondPart = matches[2]; // "111.css"
	
		console.log(matches[1]); // "ㅁㅁㅇㄴ+ㅇㄻㄹ+ㅇㄹㅇㄴㄹ+ㅁㅇㄴㄹ+ㅁ"
		console.log(matches[2]); // "111.css"
		fileUser=matches[1];
		fileName = matches[2];
		console.log("파일유저 : "+fileUser)
	}
	if (data instanceof Blob) {
        var blobUrl = URL.createObjectURL(data);

        var link = document.createElement('a');
        link.href = blobUrl;
        link.download = fileName; 
        link.innerHTML = fileName;

        console.log("<br/>"+fileUser+" : ")
        console.log(fileUser)
        chatarea.innerHTML += "<br/>"+fileUser+" : "
        chatarea.appendChild(link);
    } else {
    	if (!data.toString().startsWith("파일보냄+")) {
	        chatarea.innerHTML += "<br/>" + data;
    	}
    }
	console.log("바깥 fileUSer : "+fileUser)
}

document.getElementById("exit").addEventListener("click", ()=> {
	sock.close();
	window.location.href = '/';
});
	
document.getElementById("normalImoBtn").addEventListener("click", ()=> {
	document.getElementById("normalImo").removeAttribute("hidden")
	document.getElementById("dbImo").setAttribute("hidden","")
});
document.getElementById("dbImoBtn").addEventListener("click", ()=> {
	 fetch("/dbImo")
     .then(response => {
         return response.json();
     })
     .then(data => {
    	 const dbImoDiv = document.getElementById("dbImo");
         dbImoDiv.innerHTML = ""; // 기존 내용 비우기
         
         data.forEach(item => {
             const button = document.createElement("button");
             const img = document.createElement("img");
             img.style.width = "40px";
             img.style.height = "40px";
             img.src = "/displayImo?fileName="+item.imoPath;
             button.appendChild(img);
             button.classList.add(item.imoPath);
             dbImoDiv.appendChild(button); 
             
             button.addEventListener("click", () => {
                 sendDBEmoji(item.imoPath); 
             });
         });
     })
	document.getElementById("dbImo").removeAttribute("hidden")
	document.getElementById("normalImo").setAttribute("hidden","")
});

function sendNormalImo(emojiName) {
    var roomNo = document.querySelector("#roomNo").value;
    var username = document.querySelector("#username").value;
    var message = "<img alt='' src='../resources/"+emojiName+".jpg' width='30'>";
	console.log(message)
    var data = {
        roomNo: roomNo,
        username: username,
        message: message,
        type: "talk"
    };

    sock.send(JSON.stringify(data));
}

document.getElementById("smile").addEventListener("click", () => sendNormalImo("smile"));
document.getElementById("sad").addEventListener("click", () => sendNormalImo("sad"));
document.getElementById("angry").addEventListener("click", () => sendNormalImo("angry"));

function sendDBEmoji(emojiName) {
	console.log(emojiName)
    var roomNo = document.querySelector("#roomNo").value;
    var username = document.querySelector("#username").value;
    var message = "<img alt='' src='/displayImo?fileName="+emojiName+"' width='30'>";
	console.log(message)
    var data = {
        roomNo: roomNo,
        username: username,
        message: message,
        type: "talk"
    };

    sock.send(JSON.stringify(data));
}

document.getElementById('file').addEventListener('change', ()=>{
    document.getElementById('sendFileBtn').removeAttribute('hidden');
    
});
document.getElementById('sendFileBtn').addEventListener('click', function() {
    var fileInput = document.getElementById('file');
    
    if (fileInput.files.length === 0) {
        alert('파일을 선택하세요.');
        return;
    }
    var fileSize = fileInput.files[0].size;
    var maxSize = 5 * 1024;

    if (fileSize > maxSize) {
        alert('5KB 이하의 파일만 전송할 수 있습니다.');
        return;
    }
    var file = fileInput.files[0];
    var reader = new FileReader();
    console.log(file.result)
    reader.onload = (e)=>{
        var data = {
            roomNo: document.getElementById("roomNo").value,
            username: document.getElementById("username").value,
            fileName: file.name, // 파일 이름
            type: "file"
        };
        sock.send(JSON.stringify(data));
        sock.send(e.target.result);
    };
    reader.readAsArrayBuffer(file);

});
</script>
</html>
