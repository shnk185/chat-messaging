package org.ngs.chatmessaging.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class UtilsConfig {

    @Bean
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
