package org.ngs.chatmessaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Long messageId;
    private Long userId;
    private Long chatId;
    private String content;
    private Set<Long> receiverIds;
}
