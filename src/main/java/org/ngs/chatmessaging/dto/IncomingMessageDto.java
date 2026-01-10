package org.ngs.chatmessaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncomingMessageDto {
    private Long messageId;
    private Long chatId;
    private String content;
    private Long receiverId;
    private Long senderId;
}
