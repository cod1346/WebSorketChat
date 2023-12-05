package com.spring.service;

import java.util.List;

import com.spring.domain.ChatRecordDTO;
import com.spring.domain.ChatRoomList;
import com.spring.domain.ImoDTO;

public interface ChatService {
	public List<ChatRoomList> chatRoomList();
	public boolean createChatRoom(String roomName);
	public int getNextMessageNo();
	public boolean insertMessageRecord(ChatRecordDTO dto);
	public List<ImoDTO> getImo();
}
