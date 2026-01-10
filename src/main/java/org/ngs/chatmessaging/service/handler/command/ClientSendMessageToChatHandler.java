package org.ngs.chatmessaging.service.handler.command;

import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.constant.WebsocketSessionKeys;
import org.ngs.chatmessaging.dto.IncomingMessageDto;
import org.ngs.chatmessaging.dto.MessageDto;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.dto.commands.client.ClientSendMessageToChatDto;
import org.ngs.chatmessaging.dto.commands.server.ServerPushMessageToChatDto;
import org.ngs.chatmessaging.dto.commands.server.ServerSendMessageToChatResponseDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class ClientSendMessageToChatHandler implements ICommandHandler {

    public final CommandType commandType = CommandType.CLIENT_SEND_MESSAGE_TO_CHAT;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatService chatService;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions) {
        ClientSendMessageToChatDto clientSendMessageToChatDto = objectMapper.treeToValue(message.getCommand(), ClientSendMessageToChatDto.class);
        log.info("{} parsed command dto {}", message.getUuid(), clientSendMessageToChatDto);
        Long senderUserId = (Long) session.getAttributes().get(WebsocketSessionKeys.SESSION_USER_ID);
        MessageDto messageDto = chatService.persistChatMessage(senderUserId, clientSendMessageToChatDto);
        sendMessageToReceivers(message, sessions, messageDto, senderUserId);
        ServerSendMessageToChatResponseDto response = new ServerSendMessageToChatResponseDto();
        response.setMessage(messageDto);
        SocketCommandDto<ServerSendMessageToChatResponseDto> socketResponse = new SocketCommandDto<>();
        socketResponse.setUuid(message.getUuid());
        socketResponse.setCommandType(CommandType.SERVER_SEND_MESSAGE_TO_CHAT_RESPONSE.name());
        socketResponse.setCommand(response);
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(socketResponse)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageToReceivers(SocketCommandDto<JsonNode> message, Set<WebSocketSession> sessions, MessageDto messageDto, Long senderUserId) {
        for (WebSocketSession userSession: sessions) {
            if (userSession.getAttributes().get(WebsocketSessionKeys.SESSION_USER_ID) == null) continue;
            Long sessionUserId = (Long) userSession.getAttributes().get(WebsocketSessionKeys.SESSION_USER_ID);
            if (messageDto.getReceiverIds().contains(sessionUserId)) {
                IncomingMessageDto incomingMessageDto = new IncomingMessageDto();
                incomingMessageDto.setMessageId(messageDto.getMessageId());
                incomingMessageDto.setContent(messageDto.getContent());
                incomingMessageDto.setChatId(messageDto.getChatId());
                incomingMessageDto.setReceiverId(sessionUserId);
                incomingMessageDto.setSenderId(senderUserId);
                ServerPushMessageToChatDto response = new ServerPushMessageToChatDto();
                response.setIncomingMessage(incomingMessageDto);
                SocketCommandDto<ServerPushMessageToChatDto> socketResponse = new SocketCommandDto<>();
                socketResponse.setUuid(message.getUuid());
                socketResponse.setCommandType(CommandType.SERVER_PUSH_MESSAGE_TO_CHAT.name());
                socketResponse.setCommand(response);
                try {
                    userSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(socketResponse)));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
}
