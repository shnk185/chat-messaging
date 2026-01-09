package org.ngs.chatmessaging.repository;

import org.ngs.chatmessaging.entity.ChatMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMemberEntity, Long> {
}
