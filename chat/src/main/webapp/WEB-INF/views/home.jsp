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
	<form method="post" action="/createChatRoom">
		<input type="text" name="roomName" placeholder="채팅방 만들기">
		<button type="submit">만들기</button>
	</form>
	<div>
		<form action="/chatRoom">
			<select id="roomList" name="roomNo">
				<option value="">채팅방목록
				<c:forEach items="${list}" var="dto">
					<option value=${dto.roomNo }>${dto.roomName }
				</c:forEach>
			</select>
			<input type="text" name="username" id="username" value="" placeholder="닉네임">
			<button type="submit" id="enter">입 장</button>
		</form>
	</div>
</body>
<script>
	var username = document.querySelector("#username")
	document.getElementById("enter").addEventListener("click",(e)=> {
	    if(username.value==""){
	    	e.preventDefault();
	        alert("닉네임을 입력해주세요")
	    }else if(document.querySelector("#roomList").value==""){
	    	e.preventDefault();
	        alert("채팅방을 확인해주세요")
	    }
	});
</script>
</html>
