package org.ngs.chatmessaging.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.ngs.chatmessaging.service.handler.InternalCommandHandler;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessageListener implements MessageListener {

    private final InternalCommandHandler internalCommandHandler;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        log.info("received on channel {} message {}", channel, message);
        internalCommandHandler.handlePayload(body);
    }
}
