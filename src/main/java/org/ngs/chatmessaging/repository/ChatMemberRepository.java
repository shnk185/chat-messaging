package org.ngs.chatmessaging.repository;

import org.ngs.chatmessaging.entity.ChatMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMemberEntity, Long> {
    List<ChatMemberEntity> findByChatId(Long chatId);
}
