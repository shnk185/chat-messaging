package org.ngs.chatmessaging.dto.commands.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.chatmessaging.dto.commands.UserDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerFetchAllUsersResponseDto {
    List<UserDto> allUsers;
}
