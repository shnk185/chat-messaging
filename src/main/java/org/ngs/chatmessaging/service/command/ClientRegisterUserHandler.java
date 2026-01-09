package org.ngs.chatmessaging.service.command;

import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.dto.commands.UserDto;
import org.ngs.chatmessaging.dto.commands.client.ClientRegisterUserDto;
import org.ngs.chatmessaging.dto.commands.server.ServerRegisterUserResponseDto;
import org.ngs.chatmessaging.entity.UserEntity;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.CommandHandler;
import org.ngs.chatmessaging.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class ClientRegisterUserHandler implements ICommandHandler {

    public final CommandType commandType = CommandType.CLIENT_REGISTER_USER;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions) {
        ClientRegisterUserDto clientRegisterUserDto = objectMapper.treeToValue(message.getCommand(), ClientRegisterUserDto.class);
        log.info("{} parsed command dto: {}", message.getUuid(), clientRegisterUserDto);
        UserEntity userEntity = userService.createUser(clientRegisterUserDto.getUserName());
        ServerRegisterUserResponseDto response = new ServerRegisterUserResponseDto();
        response.setUser(new UserDto(userEntity.getUserName(), userEntity.getId()));
        SocketCommandDto<ServerRegisterUserResponseDto> socketResponse = new SocketCommandDto<>();
        socketResponse.setUuid(message.getUuid());
        socketResponse.setCommandType(CommandType.SERVER_REGISTER_USER_RESPONSE.name());
        socketResponse.setCommand(response);
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(socketResponse)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommandType getCommandType() {
        return commandType;
    }
}
