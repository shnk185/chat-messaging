package org.ngs.chatmessaging.service.handler;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.SessionManager;
import org.ngs.chatmessaging.service.handler.internal.IInternalCommandHandler;
import org.ngs.chatmessaging.service.handler.socket.ICommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class InternalCommandHandler {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private List<IInternalCommandHandler> allHandlers;

    private Map<CommandType, IInternalCommandHandler> commandHandlerMap;

    @PostConstruct
    public void setUp() {
        commandHandlerMap = new HashMap<>();
        for (IInternalCommandHandler handler: allHandlers) {
            commandHandlerMap.put(handler.getCommandType(), handler);
        }
    }

    public void handlePayload(String payload) {
        SocketCommandDto<JsonNode> socketCommand = objectMapper.readValue(payload, new TypeReference<>() {});
        if (StringUtils.isBlank(socketCommand.getUuid())) {
            socketCommand.setUuid(UUID.randomUUID().toString());
        }
        CommandType commandType = CommandType.commandFromString(socketCommand.getCommandType());
        commandHandlerMap.get(commandType).handle(socketCommand, sessionManager.getAllSessions());
    }
}
