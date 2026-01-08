package org.ngs.chatmessaging.service.command;

import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.command.ICommandHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;

import java.util.Set;

@Component
public class NoOpHandler implements ICommandHandler {
    public final CommandType commandType = CommandType.NO_OP;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions) {
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
}
