package com.mw.security.app.modelView;

import com.mw.security.app.model.Notification;
import com.mw.security.app.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsDTOList {
    Map<NotificationType,List<NotificationDTO>> notifications;
    List notificationType;
    @Builder(builderMethodName = "grouped")
    public static NotificationsDTOList fromNotifications(List<Notification> notifications){
        return NotificationsDTOList
                .builder()
                .notifications(
                        notifications
                                .stream()
                                .filter(not->not.isEnable()==true)
                                .map(not -> NotificationDTO.getFronMotification(not))
                                .collect(Collectors.groupingBy(
                                o -> ((NotificationDTO) o).getType())))
                .notificationType(Arrays.asList(NotificationType.values()))
                .build();
    }

    @Override
    public String toString() {
        return "NotificationsDTOList{" +
                "notifications=" + notifications +
                ", notificationType=" + notificationType +
                '}';
    }
}
