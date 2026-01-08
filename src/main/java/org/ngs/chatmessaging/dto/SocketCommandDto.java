package org.ngs.chatmessaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ngs.chatmessaging.enums.CommandType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketCommandDto<T> {
    private String uuid;
    private String commandType;
    private T command;
}
