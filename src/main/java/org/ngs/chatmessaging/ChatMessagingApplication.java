package org.ngs.chatmessaging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ChatMessagingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatMessagingApplication.class, args);
    }

}
