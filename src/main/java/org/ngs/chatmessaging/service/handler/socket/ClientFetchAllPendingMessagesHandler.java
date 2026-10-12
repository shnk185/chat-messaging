package org.ngs.chatmessaging.service.handler.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.constant.WebsocketSessionKeys;
import org.ngs.chatmessaging.dto.IncomingMessageDto;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.dto.commands.client.ClientFetchAllPendingMessagesDto;
import org.ngs.chatmessaging.dto.commands.server.ServerFetchAllPendingMessagesResponseDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.ChatService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientFetchAllPendingMessagesHandler implements ICommandHandler {

    public final CommandType commandType = CommandType.CLIENT_FETCH_ALL_PENDING_MESSAGES;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions) {
        ClientFetchAllPendingMessagesDto clientFetchAllPendingMessagesDto = objectMapper.treeToValue(message.getCommand(), ClientFetchAllPendingMessagesDto.class);
        log.info("{} parsed command dto {}", message.getUuid(), clientFetchAllPendingMessagesDto);
        Long receiverUserId = (Long) session.getAttributes().get(WebsocketSessionKeys.SESSION_USER_ID);
        Map<Long, List<IncomingMessageDto>> pendingMessages = chatService.fetchPendingMessagesForUser(receiverUserId);
        ServerFetchAllPendingMessagesResponseDto response = new ServerFetchAllPendingMessagesResponseDto(pendingMessages);
        SocketCommandDto<ServerFetchAllPendingMessagesResponseDto> socketResponse = new SocketCommandDto<>(message.getUuid(),
                CommandType.SERVER_FETCH_ALL_PENDING_MESSAGES_RESPONSE.name(), response);
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
