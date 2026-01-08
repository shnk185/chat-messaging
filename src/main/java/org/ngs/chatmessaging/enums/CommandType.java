package org.ngs.chatmessaging.enums;


public enum CommandType {
    NO_OP,
    CLIENT_REGISTER_USER,
    SERVER_REGISTER_USER_RESPONSE,
    CLIENT_FETCH_ALL_USERS,
    SERVER_FETCH_ALL_USERS_RESPONSE,
    CLIENT_CREATE_CHAT,
    SERVER_CREATE_CHAT_RESPONSE,
    CLIENT_FETCH_CHATS,
    SERVER_FETCH_CHATS_RESPONSE,
    SERVER_PUSH_ALL_PENDING_MESSAGES,
    CLIENT_SEND_MESSAGE_TO_CHAT,
    SERVER_SEND_MESSAGE_TO_CHAT_RESPONSE,
    SERVER_PUSH_MESSAGE_TO_CHAT;

    public static CommandType commandFromString(String input) {
        for (CommandType command : CommandType.values()) {
            if (command.name().equals(input)) {
                return command;
            }
        }
        return CommandType.NO_OP;
    }

}
