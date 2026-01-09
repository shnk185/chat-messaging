package org.ngs.chatmessaging.dto.commands.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.chatmessaging.dto.commands.UserDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerRegisterUserResponseDto {
    private UserDto user;
}
