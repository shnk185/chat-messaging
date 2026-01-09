package org.ngs.chatmessaging.service.command;

import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;

import java.util.Set;

public interface ICommandHandler {
    void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions);
    CommandType getCommandType();
}
