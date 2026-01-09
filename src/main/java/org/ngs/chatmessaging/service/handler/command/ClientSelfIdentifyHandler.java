package org.ngs.chatmessaging.service.handler.command;

import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.constant.WebsocketSessionKeys;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.dto.commands.client.ClientSelfIdentifyDto;
import org.ngs.chatmessaging.dto.commands.server.ServerClientSelfIdentifyResponseDto;
import org.ngs.chatmessaging.enums.CommandType;
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
public class ClientSelfIdentifyHandler implements ICommandHandler {

    public final CommandType commandType = CommandType.CLIENT_SELF_IDENTIFY;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions) {
        ClientSelfIdentifyDto clientSelfIdentifyDto = objectMapper.treeToValue(message.getCommand(), ClientSelfIdentifyDto.class);
        session.getAttributes().put(WebsocketSessionKeys.SESSION_USER_ID, clientSelfIdentifyDto.getUserId());
        ServerClientSelfIdentifyResponseDto response = new ServerClientSelfIdentifyResponseDto();
        response.setUserId(clientSelfIdentifyDto.getUserId());
        SocketCommandDto<ServerClientSelfIdentifyResponseDto> socketResponse = new SocketCommandDto<>();
        socketResponse.setUuid(message.getUuid());
        socketResponse.setCommandType(CommandType.SERVER_REGISTER_USER_RESPONSE.name());
        socketResponse.setCommand(response);
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
