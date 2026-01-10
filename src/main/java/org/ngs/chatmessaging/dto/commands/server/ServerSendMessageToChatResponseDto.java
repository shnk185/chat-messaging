package org.ngs.chatmessaging.dto.commands.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.chatmessaging.dto.MessageDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerSendMessageToChatResponseDto {
    private MessageDto message;
}
