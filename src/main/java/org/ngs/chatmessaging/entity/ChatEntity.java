package org.ngs.chatmessaging.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "chat")
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity extends BaseEntity {
    private String name;
}
