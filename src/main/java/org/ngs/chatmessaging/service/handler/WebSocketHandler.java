package org.ngs.chatmessaging.service.handler;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.handler.command.ICommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        try {
            session.sendMessage(new TextMessage("Connected to server"));
        } catch (IOException e) {
            log.error("failed to send message to session {} due to exception", session.getId(), e);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        log.info("received command {}", payload);
        SocketCommandDto<JsonNode> socketCommand = objectMapper.readValue(payload, new TypeReference<>() {});
        if (StringUtils.isBlank(socketCommand.getUuid())) {
            socketCommand.setUuid(UUID.randomUUID().toString());
        }
        CommandType commandType = CommandType.commandFromString(socketCommand.getCommandType());
        commandHandlerMap.get(commandType).handle(socketCommand, session, sessions);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}
