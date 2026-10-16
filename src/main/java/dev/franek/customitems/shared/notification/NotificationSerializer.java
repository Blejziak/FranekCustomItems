package dev.franek.customitems.shared.notification;

import lombok.NonNull;
import eu.okaeri.configs.schema.GenericsDeclaration;
import eu.okaeri.configs.serdes.DeserializationData;
import eu.okaeri.configs.serdes.ObjectSerializer;
import eu.okaeri.configs.serdes.SerializationData;
import dev.franek.customitems.shared.notification.Notification;
import dev.franek.customitems.shared.notification.NotificationType;

public class NotificationSerializer
implements ObjectSerializer<Notification> {
    @Override
    public boolean supports(@NonNull Class<? super Notification> type) {
        if (type == null) {
            throw new NullPointerException("type is marked non-null but is null");
        }
        return Notification.class.isAssignableFrom(type);
    }

    @Override
    public void serialize(@NonNull Notification notification, @NonNull SerializationData data, @NonNull GenericsDeclaration generics) {
        if (notification == null) {
            throw new NullPointerException("notification is marked non-null but is null");
        }
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        data.add("notificationType", notification.getType(), NotificationType.class);
        data.add("notificationContent", notification.getContent(), String.class);
    }

    @Override
    public Notification deserialize(@NonNull DeserializationData data, @NonNull GenericsDeclaration generics) {
        if (data == null) {
            throw new NullPointerException("data is marked non-null but is null");
        }
        if (generics == null) {
            throw new NullPointerException("generics is marked non-null but is null");
        }
        NotificationType notificationType = data.get("notificationType", NotificationType.class);
        String notificationContent = data.get("notificationContent", String.class);
        return new Notification(notificationType, notificationContent);
    }
}

