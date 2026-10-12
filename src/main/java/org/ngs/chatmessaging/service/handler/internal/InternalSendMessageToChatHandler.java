package org.ngs.chatmessaging.service.handler.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.constant.WebsocketSessionKeys;
import org.ngs.chatmessaging.dto.IncomingMessageDto;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.dto.commands.internal.InternalSendMessageToChatDto;
import org.ngs.chatmessaging.dto.commands.server.ServerPushMessageToChatDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalSendMessageToChatHandler implements IInternalCommandHandler {

    public final CommandType commandType = CommandType.INTERNAL_SEND_MESSAGE_TO_CHAT;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, Set<WebSocketSession> sessions) {
        InternalSendMessageToChatDto internalSendMessageToChatDto = objectMapper.treeToValue(message.getCommand(), InternalSendMessageToChatDto.class);
        log.info("{} parsed internal command dto {}", message.getUuid(), internalSendMessageToChatDto);
        IncomingMessageDto incomingMessageDto = new IncomingMessageDto();
        incomingMessageDto.setMessageId(internalSendMessageToChatDto.getMessageId());
        incomingMessageDto.setContent(internalSendMessageToChatDto.getContent());
        incomingMessageDto.setChatId(internalSendMessageToChatDto.getChatId());
        incomingMessageDto.setReceiverId(internalSendMessageToChatDto.getReceiverId());
        incomingMessageDto.setSenderId(internalSendMessageToChatDto.getSenderId());
        ServerPushMessageToChatDto response = new ServerPushMessageToChatDto();
        response.setIncomingMessage(incomingMessageDto);
        SocketCommandDto<ServerPushMessageToChatDto> socketResponse = new SocketCommandDto<>();
        socketResponse.setUuid(message.getUuid());
        socketResponse.setCommandType(CommandType.SERVER_PUSH_MESSAGE_TO_CHAT.name());
        socketResponse.setCommand(response);
        for (WebSocketSession userSession: sessions) {
            if (userSession.getAttributes().get(WebsocketSessionKeys.SESSION_USER_ID) == null) continue;
            Long sessionUserId = (Long) userSession.getAttributes().get(WebsocketSessionKeys.SESSION_USER_ID);
            if (internalSendMessageToChatDto.getReceiverId().equals(sessionUserId)) {
                try {
                    userSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(socketResponse)));
                } catch (Exception e) {
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
