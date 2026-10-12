package org.ngs.chatmessaging.service.handler.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.dto.UserDto;
import org.ngs.chatmessaging.dto.commands.client.ClientRegisterUserDto;
import org.ngs.chatmessaging.dto.commands.server.ServerRegisterUserResponseDto;
import org.ngs.chatmessaging.entity.UserEntity;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientRegisterUserHandler implements ICommandHandler {

    public final CommandType commandType = CommandType.CLIENT_REGISTER_USER;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions) {
        ClientRegisterUserDto clientRegisterUserDto = objectMapper.treeToValue(message.getCommand(), ClientRegisterUserDto.class);
        log.info("{} parsed command dto: {}", message.getUuid(), clientRegisterUserDto);
        UserEntity userEntity = userService.createUser(clientRegisterUserDto.getUserName());
        ServerRegisterUserResponseDto response = new ServerRegisterUserResponseDto(new UserDto(userEntity.getUserName(), userEntity.getId()));
        SocketCommandDto<ServerRegisterUserResponseDto> socketResponse = new SocketCommandDto<>(message.getUuid(),
                CommandType.SERVER_REGISTER_USER_RESPONSE.name(), response);
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
