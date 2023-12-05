package com.spring.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.domain.ChatRoomList;
import com.spring.domain.ImoDTO;
import com.spring.service.ChatService;

import lombok.extern.slf4j.Slf4j;

@Controller @Slf4j @RequestMapping("/")
public class HomeController {
	
	@Autowired
	private ChatService service;
	
	@GetMapping("/")
	public String home(Model model) {
		List<ChatRoomList> list=service.chatRoomList();
		model.addAttribute("list",list);
		log.info("home요청");
		return "home";
	}
	
	@PostMapping("/createChatRoom")
	public String createChatRoom(String roomName){ 
		service.createChatRoom(roomName); 
		return "redirect:/"; 
	}
	
	
	@GetMapping("/chatRoom")
	public void enterChatRoomGet(String roomNo,String username,Model model) {
		model.addAttribute("username",username);
		model.addAttribute("roomNo",roomNo);
		System.out.println("채팅방ㅇ입장,"+roomNo+username);
	}
	@GetMapping("/dbImo")
	public ResponseEntity<List<ImoDTO>> getImo(){
		List<ImoDTO> list = service.getImo();
		System.out.println(list);
		return new ResponseEntity<>(list,HttpStatus.OK);
	}
	@GetMapping("/displayImo")
	public ResponseEntity<byte[]> getFile(String fileName) {
		log.info("이모티콘 요청 " + fileName);

		File file = new File("c:\\imo\\" + fileName);

		ResponseEntity<byte[]> result = null;
		try {
			result = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
