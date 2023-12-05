package com.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ChatDTO {
	
	private String type;
	private String roomNo;
	private String username;
	private String message;
}


















