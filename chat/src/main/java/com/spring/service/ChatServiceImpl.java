package com.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.domain.ChatRecordDTO;
import com.spring.domain.ChatRoomList;
import com.spring.domain.ImoDTO;
import com.spring.mapper.ChatMapper;
import com.spring.util.Encrypt;

@Service
public class ChatServiceImpl implements ChatService {

	@Autowired
	private ChatMapper mapper;

	private Encrypt encrypt; 
	
	@Override
	public List<ChatRoomList> chatRoomList() {
		return mapper.chatRoomList();
	}

	@Override
	public boolean createChatRoom(String roomName) {
		return mapper.createChatRoom(roomName)==1?true:false;
	}

	@Override
	public int getNextMessageNo() {
		return mapper.getNextMessageNo();
	}

	@Override
	@Transactional
	public boolean insertMessageRecord(ChatRecordDTO dto) {
		mapper.insertMessageRecord(dto);
		System.out.println(dto);
		ChatRecordDTO securityDto = new ChatRecordDTO();
		securityDto=dto;
		try {
			securityDto.setMessage(encrypt.encrypt(dto.getMessage()));
			mapper.insertSecurityMessageRecord(securityDto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(securityDto);
		System.out.println("dto : "+dto);
		return true;
	}

	@Override
	public List<ImoDTO> getImo() {
		return mapper.getImo();
	}

	

}
