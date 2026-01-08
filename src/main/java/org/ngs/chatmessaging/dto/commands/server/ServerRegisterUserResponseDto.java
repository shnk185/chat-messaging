package org.ngs.chatmessaging.dto.commands.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerRegisterUserResponseDto {
    private String userName;
    private Long userId;
}
