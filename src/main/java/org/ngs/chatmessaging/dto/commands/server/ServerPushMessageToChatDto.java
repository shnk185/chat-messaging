package org.ngs.chatmessaging.dto.commands.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.chatmessaging.dto.IncomingMessageDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerPushMessageToChatDto {
    private IncomingMessageDto incomingMessage;
}
