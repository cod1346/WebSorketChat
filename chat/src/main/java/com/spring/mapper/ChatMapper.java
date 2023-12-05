package com.spring.mapper;

import java.util.List;

import com.spring.domain.ChatRecordDTO;
import com.spring.domain.ChatRoomList;
import com.spring.domain.ImoDTO;

public interface ChatMapper {
	public List<ChatRoomList> chatRoomList();
	public int createChatRoom(String roomName);
	public int getNextMessageNo();
	public int insertMessageRecord(ChatRecordDTO dto);
	public int insertSecurityMessageRecord(ChatRecordDTO dto);
	public List<ImoDTO> getImo();
}
