package org.ngs.chatmessaging.dto.commands.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientCreateChatDto {
    private String chatName;
    private List<Long> userIds;
}
