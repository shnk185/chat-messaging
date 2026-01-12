package org.ngs.chatmessaging.dto.commands.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalSendMessageToChatDto {
    private Long messageId;
    private String content;
    private Long chatId;
    private Long receiverId;
    private Long senderId;
}
