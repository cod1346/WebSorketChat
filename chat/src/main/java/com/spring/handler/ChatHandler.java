package com.spring.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.domain.ChatRecordDTO;
import com.spring.service.ChatService;

@Component
public class ChatHandler extends TextWebSocketHandler{
	
	@Autowired
	private ChatService service;
	
	private List<Map<String, Object>> sessionList = new ArrayList<Map<String, Object>>();
	// 클라이언트가 서버로 메세지 전송 처리
	String fileName1 = "";
	String fileUser = "";
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		//소켓 연결
		super.afterConnectionEstablished(session);
		boolean flag = false;
		String url = session.getUri().toString();
		String roomNumber = url.split("/chat-ws/")[1];
		int idx = sessionList.size(); //방의 사이즈를 조사한다.
		if(idx > 0) {
			for(int i=0; i<idx; i++) {
				String roomNo = (String) sessionList.get(i).get("roomNo");
				if(roomNo.equals(roomNumber)) {
					flag = true;
					idx = i;
					break;
				}
			}
		}
		
		if(flag) { //존재하는 방이라면 세션만 추가한다.
			HashMap<String, Object> map = (HashMap<String, Object>) sessionList.get(idx);
			map.put(session.getId(), session);
		}else { //최초 생성하는 방이라면 방번호와 세션을 추가한다.
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roomNo", roomNumber);
			map.put(session.getId(), session);
			sessionList.add(map);
			System.out.println("방생성");
		}
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println("텍스트핸들러");
		super.handleTextMessage(session, message);
        
		// JSON --> Map으로 변환
		ObjectMapper objectMapper = new ObjectMapper();
		
		Map<String, String> mapReceive = objectMapper.readValue(message.getPayload(), Map.class);

		switch (mapReceive.get("type")) {
		
			case "enter":
				// 세션 리스트에 저장
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("roomNo", mapReceive.get("roomNo"));
				map.put("username", mapReceive.get("username"));
				map.put("session", session);
				sessionList.add(map);
				
				// 같은 채팅방에 입장 메세지 전송
				for (int i = 0; i < sessionList.size(); i++) {
					Map<String, Object> mapSessionList = sessionList.get(i);
					String roomNo = (String) mapSessionList.get("roomNo");
					System.out.println("1214시간");
					System.out.println(roomNo);
					WebSocketSession sess = (WebSocketSession) mapSessionList.get("session");
					if(sess==null) {
						System.out.println("세션은 null임");
					}
					if(sess!=null) {
						if(roomNo.equals(mapReceive.get("roomNo"))) {
							System.out.println("같음");
							System.out.println(mapReceive.get("username"));
							sess.sendMessage(new TextMessage(mapReceive.get("username")+"님이 입장하셨습니다."));
						}
					}
				}
			break;
			
			case "talk":
				System.out.println("2");
				// 같은 채팅방에 메세지 전송
				for (int i = 0; i < sessionList.size(); i++) {
					Map<String, Object> mapSessionList = sessionList.get(i);
					String roomNo = (String) mapSessionList.get("roomNo");
					WebSocketSession sess = (WebSocketSession) mapSessionList.get("session");
	
					if (roomNo.equals(mapReceive.get("roomNo"))&&sess!=null) {
						//db 작업------------------------------------
						ChatRecordDTO dto = new ChatRecordDTO();
						dto.setMessage(mapReceive.get("message"));
						dto.setRoomNo(mapReceive.get("roomNo"));
						dto.setUsername(mapReceive.get("username"));
						int messageNo=service.getNextMessageNo();
						dto.setMessageNo(messageNo);
						System.out.println(dto);
						System.out.println("dto");
						service.insertMessageRecord(dto);
						sess.sendMessage(new TextMessage( mapReceive.get("username") + " : " + mapReceive.get("message")));
					}
				}
			break;
		
			case "file": 
			System.out.println(mapReceive.get("fileName"));
			System.out.println("파일");
			Map<String, Object> fileData = new HashMap<>();
		    fileData.put("fileName", mapReceive.get("fileName")); // 파일 이름
		    fileData.put("roomNo", mapReceive.get("roomNo")); // 채팅방 번호 등 필요한 다른 정보
		    fileData.put("username", mapReceive.get("username"));
		    fileName1 =  mapReceive.get("fileName");
		    fileUser= mapReceive.get("username");
		    System.out.println("파일데이터 삽입전");
		    sessionList.add(fileData);
		    System.out.println("파일데이터 삽입후");
			break;
		
		}
		
	}
	//파일객체다루기
	@Override
	public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		System.out.println("바이너리핸들러");
 		ByteBuffer byteBuffer = message.getPayload();
		String roomNo = getRoomForSession(session);
		System.out.println("룸넘버 : "+roomNo);
		for (Map<String, Object> fileData : sessionList) {
		    String AllroomNo = (String) fileData.get("roomNo");
		    System.out.println("올룸넘버 : " + AllroomNo);
		    WebSocketSession sess = (WebSocketSession) fileData.get("session");
		    String fileName = "";
		    if((String) fileData.get("fileName")!=null) {
		    	fileName=(String) fileData.get("fileName");
		    }
		    System.out.println(fileData.get("roomNo"));
		    System.out.println(fileName);
		    if (fileData.get("roomNo").equals(AllroomNo) && sess != null) {
		        try {
		        	System.out.println("파일네임 : "+fileName);
		        	System.out.println(fileData.get("fileName"));
		        	System.out.println("파일네임1 : "+fileName1);
		            sess.sendMessage(new TextMessage("파일보냄+"+fileUser+"+" + fileName1));
		            sess.sendMessage(new BinaryMessage(byteBuffer));
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		}
		/*
		for (Map<String, Object> fileData : sessionList) {
		    String username = (String) fileData.get("username");
		   	String fileName = (String) fileData.get("fileName");
		       //String roomNo = (String) fileData.get("roomNo");
		       
		    System.out.println("---------------------------------------");
		    System.out.println(fileName);
			System.out.println(roomNo);
			System.out.println(username);
			System.out.println("---------------------------------------");
			WebSocketSession sess = (WebSocketSession) fileData.get("session");
	        byteBuffer.rewind(); // 버퍼를 읽을 수 있도록 위치를 처음으로 되돌림
	            try {
	            	sess.sendMessage(new BinaryMessage(byteBuffer));
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		*/		    
		    // 해당 채팅방의 세션들에게만 데이터 전송
		    /*
		    for (Map<String, Object> mapSessionList : sessionList) {
		        String currentRoomNo = (String) mapSessionList.get("roomNo");
		        WebSocketSession sess = (WebSocketSession) mapSessionList.get("session");

		        if (roomNo.equals(currentRoomNo)) {
		            try {
		                byteBuffer.rewind(); // 버퍼를 읽을 수 있도록 위치를 처음으로 되돌림
		                sess.sendMessage(new BinaryMessage(byteBuffer)); // 데이터를 전송
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
		    }
		    */
	}
	private String getRoomForSession(WebSocketSession session) {
	    for (Map<String, Object> map : sessionList) {
	        WebSocketSession sess = (WebSocketSession) map.get("session");
	        if (session.equals(sess)) {
	            return (String) map.get("roomNo");
	        }
	    }
	    return null;
	}
	
	/*
	private void saveToFile(byte[] data, String fileName) throws IOException {
        String uploadDirectory = "c:\\upload\\";

        String filePath = uploadDirectory + fileName;

        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(data);
        fos.close();

    }
	*/
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

		super.afterConnectionClosed(session, status);
        
		String nowRoomNo = "";
		String username = "";
		
		// 사용자 세션 제거
		for (int i = 0; i < sessionList.size(); i++) {
			Map<String, Object> map = sessionList.get(i);
			String roomNo = (String) map.get("roomNo");
			WebSocketSession sess = (WebSocketSession) map.get("session");
			
			if(session.equals(sess)) {
				System.out.println(map.get("username"));
				System.out.println(map.get("username"));
				username = (String) map.get("username");
				nowRoomNo = roomNo;
				sessionList.remove(map);
				break;
			}	
		}
		// 같은 채팅방에 퇴장 메세지 전송 
		for (int i = 0; i < sessionList.size(); i++) {
			Map<String, Object> mapSessionList = sessionList.get(i);
			String roomNo = (String) mapSessionList.get("roomNo");
			WebSocketSession sess = (WebSocketSession) mapSessionList.get("session");
			if (roomNo.equals(nowRoomNo)) {
				sess.sendMessage(new TextMessage(username+"님이 퇴장하셨습니다."));
			}
		}
		
	}
}