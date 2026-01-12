package org.ngs.chatmessaging.utils;

public class KeyUtil {

    public static String getRedisUserTopicName(Long userId) {
        return "topic_user_" + userId;
    }
}
