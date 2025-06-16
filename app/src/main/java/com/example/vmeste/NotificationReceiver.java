package com.example.vmeste;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager == null) {
                Log.e("NOTIFY", "NotificationManager is null");
                return;
            }

            // Создаем канал для Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "VMESTE_NOTIFICATIONS",
                        "Уведомления Vmeste",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Уведомления приложения Vmeste");
                notificationManager.createNotificationChannel(channel);
            }

            int notificationId = (int) System.currentTimeMillis();
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");

            // Для мотивационных уведомлений используем случайное сообщение
            if (intent.getIntExtra("notification_id", 0) == NotificationsActivity.MOTIVATION_ID) {
                message = getRandomMotivationalMessage();
            }
            // Для уведомлений о дедлайнах используем специальный текст
            else if (intent.getIntExtra("notification_id", 0) == NotificationsActivity.DEADLINES_ID) {
                message = getDeadlineMessage();
            }

            Notification notification = new NotificationCompat.Builder(context, "VMESTE_NOTIFICATIONS")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(notificationId, notification);

        } catch (Exception e) {
            Log.e("NOTIFY", "Ошибка показа уведомления", e);
        }
    }

    private String getRandomMotivationalMessage() {
        String[] messages = {
                "Даже если кажется, что всё сложно, помни: каждый большой успех начинается с маленького шага. Продолжай!",
                "Ты уже так близко к цели! Осталось совсем немного — соберись, и всё получится!",
                "Не откладывай на завтра то, что можно сделать сегодня — и ты уже на пути к успеху!",
                "Работа важна, но не забывай отдыхать. Лучшие идеи приходят в моменты расслабления!",
                "Успех — это движение от неудачи к неудаче без потери энтузиазма. Продолжай идти!"
        };
        return messages[new Random().nextInt(messages.length)];
    }

    private String getDeadlineMessage() {
        String[] messages = {
                "У вас есть задания с приближающимися сроками! Проверьте список задач.",
                "Не забудьте проверить дедлайны на этой неделе!",
                "Внимание! Некоторые задания скоро должны быть сданы.",
                "Лучше начать сейчас: у вас есть задачи с близкими сроками.",
                "Пора проверить список дел - некоторые сроки уже на носу!"
        };
        return messages[new Random().nextInt(messages.length)];
    }
}