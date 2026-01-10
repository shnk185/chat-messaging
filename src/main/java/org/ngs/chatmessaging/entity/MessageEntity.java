package org.ngs.chatmessaging.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "message")
@AllArgsConstructor
@NoArgsConstructor
public class MessageEntity extends BaseEntity {
    private Long userId;
    private String content;
}
