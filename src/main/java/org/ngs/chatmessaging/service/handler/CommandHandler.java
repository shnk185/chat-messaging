package org.ngs.chatmessaging.service.handler;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.handler.command.ICommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Slf4j
@Service
public class CommandHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private List<ICommandHandler> allHandlers;

    private Map<CommandType, ICommandHandler> commandHandlerMap;

    @PostConstruct
    public void setUp() {
        commandHandlerMap = new HashMap<>();
        for (ICommandHandler handler: allHandlers) {
            commandHandlerMap.put(handler.getCommandType(), handler);
        }
    }

    public void handle(TextMessage message, WebSocketSession session, Set<WebSocketSession> sessions) {
        String payload = message.getPayload();
        log.info("received command {}", payload);
        SocketCommandDto<JsonNode> socketCommand = objectMapper.readValue(payload, new TypeReference<>() {});
        if (StringUtils.isBlank(socketCommand.getUuid())) {
            socketCommand.setUuid(UUID.randomUUID().toString());
        }
        CommandType commandType = CommandType.commandFromString(socketCommand.getCommandType());
        commandHandlerMap.get(commandType).handle(socketCommand, session, sessions);
    }
}
