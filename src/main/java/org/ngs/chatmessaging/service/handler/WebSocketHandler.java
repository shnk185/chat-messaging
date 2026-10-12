package org.ngs.chatmessaging.service.handler;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.constant.WebsocketSessionKeys;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.SessionManager;
import org.ngs.chatmessaging.service.handler.socket.ICommandHandler;
import org.ngs.chatmessaging.subscriber.RedisSubscriptionManager;
import org.ngs.chatmessaging.utils.KeyUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final SessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final List<ICommandHandler> allHandlers;
    private final Map<CommandType, ICommandHandler> commandHandlerMap = new HashMap<>();
    private final RedisSubscriptionManager redisSubscriptionManager;

    @PostConstruct
    public void setUp() {
        for (ICommandHandler handler: allHandlers) {
            commandHandlerMap.put(handler.getCommandType(), handler);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionManager.addSession(session);
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
        handlePayload(session, payload);
    }

    public void handlePayload(WebSocketSession session, String payload) {
        SocketCommandDto<JsonNode> socketCommand = objectMapper.readValue(payload, new TypeReference<>() {});
        if (StringUtils.isBlank(socketCommand.getUuid())) {
            socketCommand.setUuid(UUID.randomUUID().toString());
        }
        CommandType commandType = CommandType.commandFromString(socketCommand.getCommandType());
        commandHandlerMap.get(commandType).handle(socketCommand, session, sessionManager.getAllSessions());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (session.getAttributes().containsKey(WebsocketSessionKeys.SESSION_USER_ID)) {
            Long sessionUserId = (Long) session.getAttributes().get(WebsocketSessionKeys.SESSION_USER_ID);
            String topic = KeyUtil.getRedisUserTopicName(sessionUserId);
            redisSubscriptionManager.unsubscribe(topic);
        }
        sessionManager.removeSession(session);
    }
}
