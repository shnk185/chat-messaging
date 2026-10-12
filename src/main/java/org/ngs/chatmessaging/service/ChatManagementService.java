package org.ngs.chatmessaging.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ngs.chatmessaging.dto.ChatDto;
import org.ngs.chatmessaging.dto.UserDto;
import org.ngs.chatmessaging.dto.commands.client.ClientCreateChatDto;
import org.ngs.chatmessaging.entity.ChatEntity;
import org.ngs.chatmessaging.entity.ChatMemberEntity;
import org.ngs.chatmessaging.repository.ChatMemberRepository;
import org.ngs.chatmessaging.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ChatManagementService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Transactional
    public ChatDto createChat(ClientCreateChatDto clientCreateChatDto) {
        ChatEntity chatEntity = new ChatEntity(clientCreateChatDto.getChatName());
        chatRepository.save(chatEntity);

        ChatDto chatDto = new ChatDto();
        chatDto.setChatName(chatEntity.getName());
        chatDto.setChatId(chatEntity.getId());
        chatDto.setChatMembers(new ArrayList<>());


        for (Long userId: clientCreateChatDto.getUserIds()) {
            ChatMemberEntity chatMemberEntity = new ChatMemberEntity();
            chatMemberEntity.setChatId(chatEntity.getId());
            chatMemberEntity.setUserId(userId);
            chatMemberRepository.save(chatMemberEntity);
            UserDto userDto = new UserDto();
            userDto.setUserId(userId);
            chatDto.getChatMembers().add(userDto);
        }
        return chatDto;
    }
}
