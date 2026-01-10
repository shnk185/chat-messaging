package org.ngs.chatmessaging.dto.commands.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientAcknowledgeMessageReceivedDto {
    private Long messageId;
    private Long chatId;
}
