package org.ngs.chatmessaging.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ngs.chatmessaging.dto.IncomingMessageDto;
import org.ngs.chatmessaging.dto.MessageDto;
import org.ngs.chatmessaging.dto.commands.client.ClientAcknowledgeMessageReceivedDto;
import org.ngs.chatmessaging.dto.commands.client.ClientSendMessageToChatDto;
import org.ngs.chatmessaging.entity.ChatMemberEntity;
import org.ngs.chatmessaging.entity.InboxEntity;
import org.ngs.chatmessaging.entity.MessageEntity;
import org.ngs.chatmessaging.repository.ChatMemberRepository;
import org.ngs.chatmessaging.repository.InboxRepository;
import org.ngs.chatmessaging.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final InboxRepository inboxRepository;
    private final ChatMemberRepository chatMemberRepository;

    @Transactional
    public MessageDto persistChatMessage(Long senderUserId, ClientSendMessageToChatDto clientSendMessageToChatDto) {
        MessageEntity messageEntity = new MessageEntity(senderUserId, clientSendMessageToChatDto.getContent());
        messageRepository.save(messageEntity);

        Set<Long> receiverIds = new HashSet<>();
        List<ChatMemberEntity> chatMemberEntityList = chatMemberRepository.findByChatId(clientSendMessageToChatDto.getChatId());
        if (chatMemberEntityList != null) {
            for (ChatMemberEntity member: chatMemberEntityList) {
                if (member.getUserId().equals(senderUserId)) continue;
                InboxEntity inboxEntity = InboxEntity.builder().messageId(messageEntity.getId())
                        .delivered(false).receiverId(member.getUserId()).senderId(senderUserId)
                        .chatId(clientSendMessageToChatDto.getChatId()).build();
                inboxRepository.save(inboxEntity);
                receiverIds.add(member.getUserId());
            }
        }

        return MessageDto.builder().content(messageEntity.getContent()).messageId(messageEntity.getId())
                .userId(messageEntity.getUserId()).chatId(clientSendMessageToChatDto.getChatId()).receiverIds(receiverIds)
                .build();
    }

    @Transactional
    public void processMessageAcknowledge(ClientAcknowledgeMessageReceivedDto acknowledgeDto, Long receiverId) {
        InboxEntity inboxEntity = inboxRepository.findByChatIdAndReceiverIdAndMessageId(acknowledgeDto.getChatId(),
                receiverId, acknowledgeDto.getMessageId());
        inboxEntity.setDelivered(true);
        inboxRepository.save(inboxEntity);
        if (!inboxRepository.existsByMessageIdAndDeliveredFalse(acknowledgeDto.getMessageId())) {
            messageRepository.deleteIfExists(acknowledgeDto.getMessageId());
        }
    }

    public Map<Long, List<IncomingMessageDto>> fetchPendingMessagesForUser(Long receiverUserId) {
        Map<Long, List<IncomingMessageDto>> pendingMessages = new HashMap<>();
        List<InboxEntity> inboxEntities = inboxRepository.findByReceiverIdAndDeliveredFalse(receiverUserId);
        for (InboxEntity inboxEntity: inboxEntities) {
            MessageEntity messageEntity = messageRepository.findById(inboxEntity.getMessageId()).get();
            IncomingMessageDto incomingMessageDto = IncomingMessageDto.builder().senderId(inboxEntity.getSenderId())
                    .messageId(inboxEntity.getMessageId()).content(messageEntity.getContent())
                    .chatId(inboxEntity.getChatId()).receiverId(receiverUserId).build();
            pendingMessages.computeIfAbsent(inboxEntity.getChatId(), x -> new ArrayList<>()).add(incomingMessageDto);
        }
        return pendingMessages;
    }
}
