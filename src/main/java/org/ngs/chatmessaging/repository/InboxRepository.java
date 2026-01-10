package org.ngs.chatmessaging.repository;

import org.ngs.chatmessaging.entity.InboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InboxRepository extends JpaRepository<InboxEntity, Long> {

    InboxEntity findByChatIdAndReceiverIdAndMessageId(Long chatId, Long receiverId, Long messageId);

    boolean existsByMessageIdAndDeliveredFalse(Long messageId);

    List<InboxEntity> findByReceiverIdAndDeliveredFalse(Long receiverUserId);
}
