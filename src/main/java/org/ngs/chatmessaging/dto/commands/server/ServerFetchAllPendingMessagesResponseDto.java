package org.ngs.chatmessaging.dto.commands.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.chatmessaging.dto.IncomingMessageDto;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerFetchAllPendingMessagesResponseDto {
    Map<Long, List<IncomingMessageDto>> pendingMessages;
}
