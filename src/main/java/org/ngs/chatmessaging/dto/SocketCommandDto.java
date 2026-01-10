package org.ngs.chatmessaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocketCommandDto<T> {
    private String uuid;
    private String commandType;
    private T command;
}
