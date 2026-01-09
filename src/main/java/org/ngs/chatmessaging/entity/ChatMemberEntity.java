package org.ngs.chatmessaging.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "chat_member", uniqueConstraints = {@UniqueConstraint(name = "uk_chat_member_chat_user", columnNames = {"userId", "chatId"})})
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemberEntity extends BaseEntity {
    private Long userId;
    private Long chatId;
}
