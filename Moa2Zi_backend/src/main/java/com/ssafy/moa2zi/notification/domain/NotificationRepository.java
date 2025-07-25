package com.ssafy.moa2zi.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    List<Notification> findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(Long receiverId);
}
