package org.ngs.chatmessaging.service.handler.socket;

import lombok.extern.slf4j.Slf4j;
import org.ngs.chatmessaging.dto.SocketCommandDto;
import org.ngs.chatmessaging.dto.UserDto;
import org.ngs.chatmessaging.dto.commands.client.ClientFetchAllUsersDto;
import org.ngs.chatmessaging.dto.commands.server.ServerFetchAllUsersResponseDto;
import org.ngs.chatmessaging.entity.UserEntity;
import org.ngs.chatmessaging.enums.CommandType;
import org.ngs.chatmessaging.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class ClientFetchAllUsersHandler implements ICommandHandler {

    public final CommandType commandType = CommandType.CLIENT_FETCH_ALL_USERS;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(SocketCommandDto<JsonNode> message, WebSocketSession session, Set<WebSocketSession> sessions) {
        ClientFetchAllUsersDto clientFetchAllUsersDto = objectMapper.treeToValue(message.getCommand(), ClientFetchAllUsersDto.class);
        log.info("{} parsed command dto: {}", message.getUuid(), clientFetchAllUsersDto);
        List<UserEntity> userEntityList = userService.fetchAllUsers(clientFetchAllUsersDto.getUserId());
        ServerFetchAllUsersResponseDto response = new ServerFetchAllUsersResponseDto();
        List<UserDto> allUserDtos = new ArrayList<>();
        for (UserEntity userEntity: userEntityList) {
            UserDto userDto = new UserDto();
            userDto.setUserName(userEntity.getUserName());
            userDto.setUserId(userEntity.getId());
            allUserDtos.add(userDto);
        }
        response.setAllUsers(allUserDtos);
        SocketCommandDto<ServerFetchAllUsersResponseDto> socketResponse = new SocketCommandDto<>();
        socketResponse.setUuid(message.getUuid());
        socketResponse.setCommandType(CommandType.SERVER_FETCH_ALL_USERS_RESPONSE.name());
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
