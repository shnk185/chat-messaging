package org.ngs.chatmessaging.service.handler.socket;

import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.constant.WebsocketSessionKeys;
import org.ngs.chatmessaging.dto.ChatDto;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.dto.commands.client.ClientCreateChatDto;
import org.ngs.chatmessaging.dto.commands.server.ServerCreateChatResponseDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.ChatManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class ClientCreateChatHandler implements ICommandHandler {

    public final CommandType commandType = CommandType.CLIENT_CREATE_CHAT;

    @Autowired
    private ChatManagementService chatManagementService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions) {
        ClientCreateChatDto clientCreateChatDto = objectMapper.treeToValue(message.getCommand(), ClientCreateChatDto.class);
        log.info("{} parsed command dto: {}", message.getUuid(), clientCreateChatDto);
        ChatDto chatDto = chatManagementService.createChat(clientCreateChatDto);
        ServerCreateChatResponseDto response = new ServerCreateChatResponseDto();
        response.setChatDto(chatDto);
        SocketCommandDto<ServerCreateChatResponseDto> socketResponse = new SocketCommandDto<>();
        socketResponse.setUuid(message.getUuid());
        socketResponse.setCommandType(CommandType.SERVER_CREATE_CHAT_RESPONSE.name());
        socketResponse.setCommand(response);
        Set<Long> chatMembers = new HashSet<>(clientCreateChatDto.getUserIds());
        try {
            for (WebSocketSession userSession: sessions) {
                if (!userSession.getAttributes().containsKey(WebsocketSessionKeys.SESSION_USER_ID)) {
                    continue;
                }
                Long sessionUserId = (Long) userSession.getAttributes().get(WebsocketSessionKeys.SESSION_USER_ID);
                if (chatMembers.contains(sessionUserId) && userSession.isOpen()) {
                    userSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(socketResponse)));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
}
