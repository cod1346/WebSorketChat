package com.spring.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.domain.ChatDTO;
import com.spring.util.Encrypt;

@Component
public class ChatHandler4 extends TextWebSocketHandler{
		
	 	private Map<String, WebSocketSession> users = new ConcurrentHashMap<>();
	 	
		private Encrypt encrypt;
		
		ObjectMapper objectMapper = new ObjectMapper();
		@Override	
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			System.out.println("사용자 접속 요청");
			users.put(session.getId(), session);
		}
		
		@Override
		protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
			String payload = message.getPayload();
			System.out.println("_---------------------------");
			System.out.println(payload);
			System.out.println("_---------------------------");
			ChatDTO dto = objectMapper.readValue(payload, ChatDTO.class);
			System.out.println(dto);
			  for (WebSocketSession s : users.values()) { //<-- .values() 로 session들만 가져옴
		            
		            // 여기서 모든 세션들에게 보내지게 된다
		            // 1회전당 현재 회전에 잡힌 session에게 메세지 보낸다
		            s.sendMessage(message);

		        }
		}
		
		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
			System.out.println("사용자 접속 해제 요청");
			 users.remove(session.getId());
		}

	
}
