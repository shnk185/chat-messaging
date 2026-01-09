package org.ngs.chatmessaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {
    private Long chatId;
    private String chatName;
    private List<UserDto> chatMembers;
}
