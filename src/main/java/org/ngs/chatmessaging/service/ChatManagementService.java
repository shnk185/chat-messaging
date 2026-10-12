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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatManagementService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Transactional
    public ChatDto createChat(ClientCreateChatDto clientCreateChatDto) {
        ChatEntity chatEntity = new ChatEntity(clientCreateChatDto.getChatName());
        chatRepository.save(chatEntity);

        List<UserDto> chatMembers = new ArrayList<>();
        for (Long userId: clientCreateChatDto.getUserIds()) {
            ChatMemberEntity chatMemberEntity = new ChatMemberEntity(userId, chatEntity.getId());
            chatMemberRepository.save(chatMemberEntity);
            chatMembers.add(new UserDto(null, userId));
        }
        return new ChatDto(chatEntity.getId(), chatEntity.getName(), chatMembers);
    }
}
