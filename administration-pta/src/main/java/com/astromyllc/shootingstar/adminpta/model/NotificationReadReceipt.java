package com.astromyllc.shootingstar.adminpta.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

// One receipt per (recipientContact, notificationId) pair. Deliberately a
// separate collection rather than a readByContacts set embedded on
// Announcement/VoiceMessage — keeps "who has read what" in one place for
// both notification kinds instead of duplicating the tracking mechanism
// per-type, and means getNotifications never needs to load full read-lists
// onto every list item, only whichever receipts belong to the one
// recipient asking.
@Document(value = "notification_read_receipt")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = "id")
public class NotificationReadReceipt {
    @Id
    private ObjectId id;

    @Indexed
    private String institutionCode;

    @Indexed
    private String recipientContact;

    // The id of the Announcement or VoiceMessage this receipt is for.
    @Indexed
    private String notificationId;

    private String kind; // "ANNOUNCEMENT" | "EMERGENCY" | "VOICE"
    private Instant readAt;
}