package org.ngs.chatmessaging.subscriber;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisSubscriptionManager {

    @Autowired
    private RedisMessageListenerContainer container;

    @Autowired
    private RedisMessageListener redisMessageListener;

    public void subscribe(String topic) {
        container.addMessageListener(redisMessageListener, new ChannelTopic(topic));
        log.info("subscribed to redis channel {}", topic);
    }

    public void unsubscribe(String topic) {
        container.removeMessageListener(redisMessageListener, new ChannelTopic(topic));
        log.info("unsubscribed from redis channel {}", topic);
    }
}
