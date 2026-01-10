package org.ngs.chatmessaging.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "inbox")
@AllArgsConstructor
@NoArgsConstructor
public class InboxEntity extends BaseEntity {
    private Long receiverId;
    private Long messageId;
    private Long senderId;
    private Long chatId;
    private boolean delivered = false;
}
