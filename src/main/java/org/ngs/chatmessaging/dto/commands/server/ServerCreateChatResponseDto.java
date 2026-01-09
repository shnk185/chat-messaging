package org.ngs.chatmessaging.dto.commands.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.chatmessaging.dto.ChatDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerCreateChatResponseDto {
    private ChatDto chatDto;
}
