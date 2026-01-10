package org.ngs.chatmessaging.dto.commands.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerAcknowledgeMessageReceivedResponseDto {
    private Long messageId;
    private Long chatId;
    private Long receiverId;
}
