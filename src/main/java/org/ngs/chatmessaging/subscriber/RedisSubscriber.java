package org.ngs.chatmessaging.subscriber;


import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisSubscriber implements MessageListener {

    @Autowired
    private RedisMessageListenerContainer container;

    public void subscribe(String topic) {
        container.addMessageListener(this, new ChannelTopic(topic));
    }

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        log.info("received on channel {} message {}", channel, message);
    }
}
