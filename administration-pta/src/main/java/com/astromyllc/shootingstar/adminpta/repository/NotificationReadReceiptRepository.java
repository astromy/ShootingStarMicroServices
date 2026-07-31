package com.astromyllc.shootingstar.adminpta.repository;

import com.astromyllc.shootingstar.adminpta.model.NotificationReadReceipt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationReadReceiptRepository extends MongoRepository<NotificationReadReceipt, String> {
    // Fetched once per getNotifications call, then checked in-memory against
    // each item — cheaper than a query per notification.
    List<NotificationReadReceipt> findByRecipientContactAndInstitutionCode(String recipientContact, String institutionCode);

    Optional<NotificationReadReceipt> findByRecipientContactAndNotificationId(String recipientContact, String notificationId);
}