package com.mw.security.app.modelView;

import com.mw.security.app.model.Member;
import com.mw.security.app.model.Notification;
import com.mw.security.app.model.NotificationType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationDTO {
    Long id;
    NotificationType type;
    Long typeId;
    String message;
    Long from;
    Long to;
    public LocalDateTime dateTime;
    public boolean enable;

    @Builder(builderMethodName = "fromNotification")
    public static NotificationDTO getFronMotification(Notification notification){
        return NotificationDTO.builder()
                .to(notification.getTo().getId())
                .from(notification.getFrom().getId())
                .dateTime(notification.getDateTime())
                .type(notification.getType())
                .typeId(notification.getTypeId())
                .id(notification.getId())
                .message(notification.getMessage())
                .enable(notification.isEnable())
                .build();
    }
}
