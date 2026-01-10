package org.ngs.chatmessaging.repository;

import org.ngs.chatmessaging.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    @Modifying
    @Query("DELETE FROM MessageEntity m WHERE m.id = :id")
    int deleteIfExists(@Param("id") Long id);
}
