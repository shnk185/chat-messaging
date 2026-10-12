package org.ngs.chatmessaging.service.handler.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.constant.WebsocketSessionKeys;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.dto.commands.client.ClientAcknowledgeMessageReceivedDto;
import org.ngs.chatmessaging.dto.commands.server.ServerAcknowledgeMessageReceivedResponseDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientAcknowledgeMessageReceivedHandler implements ICommandHandler {

    public final CommandType commandType = CommandType.CLIENT_ACKNOWLEDGE_MESSAGE_RECEIVED;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions) {
        ClientAcknowledgeMessageReceivedDto clientAcknowledgeMessageReceivedDto = objectMapper.treeToValue(message.getCommand(), ClientAcknowledgeMessageReceivedDto.class);
        log.info("{} parser command dto {}", message.getUuid(), clientAcknowledgeMessageReceivedDto);
        Long receiverId = (Long) session.getAttributes().get(WebsocketSessionKeys.SESSION_USER_ID);
        chatService.processMessageAcknowledge(clientAcknowledgeMessageReceivedDto, receiverId);
        ServerAcknowledgeMessageReceivedResponseDto response = new ServerAcknowledgeMessageReceivedResponseDto(clientAcknowledgeMessageReceivedDto.getMessageId(),
                clientAcknowledgeMessageReceivedDto.getChatId(), receiverId);
        SocketCommandDto<ServerAcknowledgeMessageReceivedResponseDto> socketResponse = new SocketCommandDto<>(message.getUuid(),
                CommandType.SERVER_ACKNOWLEDGE_MESSAGE_RECEIVED_RESPONSE.name(), response);
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(socketResponse)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
}
