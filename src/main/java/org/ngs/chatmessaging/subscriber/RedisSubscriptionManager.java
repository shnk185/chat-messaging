package org.ngs.chatmessaging.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSubscriptionManager {

    private final RedisMessageListenerContainer container;
    private final RedisMessageListener redisMessageListener;

    public void subscribe(String topic) {
        container.addMessageListener(redisMessageListener, new ChannelTopic(topic));
        log.info("subscribed to redis channel {}", topic);
    }

    public void unsubscribe(String topic) {
        container.removeMessageListener(redisMessageListener, new ChannelTopic(topic));
        log.info("unsubscribed from redis channel {}", topic);
    }
}
