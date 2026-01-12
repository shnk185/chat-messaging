package org.ngs.chatmessaging.service.handler.internal;

import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;

import java.util.Set;

public interface IInternalCommandHandler {
    void handle(SocketCommandDto<JsonNode> message, Set<WebSocketSession> sessions);
    CommandType getCommandType();
}
