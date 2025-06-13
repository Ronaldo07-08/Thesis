package com.example.vmeste;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {
    private static final int MOTIVATION_NOTIFICATION_ID = 1;
    private static final String[] MOTIVATIONAL_MESSAGES = {
            "Даже если кажется, что всё сложно, помни: каждый большой успех начинается с маленького шага. Продолжай!",
            "Ты уже так близко к цели! Осталось совсем немного — соберись, и всё получится!",
            "Не откладывай на завтра то, что можно сделать сегодня — и ты уже на пути к успеху!",
            "Работа важна, но не забывай отдыхать. Лучшие идеи приходят в моменты расслабления!",
            "Успех — это движение от неудачи к неудаче без потери энтузиазма. Продолжай идти!",
            "Не говори, что у тебя мало времени. У тебя ровно столько же часов в сутках, сколько было у великих людей!",
            "Будущее зависит от того, что ты делаешь сегодня. Начни прямо сейчас!",
            "Сложные задачи — как пазлы: если разобрать на кусочки, они становятся проще.",
            "Прежде чем сдаться, вспомни, зачем ты начал(а). Эта причина всё ещё важна, правда?",
            "Рост не всегда виден сразу. Даже когда ты не замечаешь изменений — они есть. Продолжай."
    };

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
                channel.setDescription("Мотивационные уведомления");
                notificationManager.createNotificationChannel(channel);
            }

            // Генерируем уникальный ID для каждого уведомления
            int notificationId = (int) System.currentTimeMillis();

            Notification notification = new NotificationCompat.Builder(context, "VMESTE_NOTIFICATIONS")
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(intent.getStringExtra("title"))
                    .setContentText(getRandomMotivationalMessage())
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
                "Успех — это движение от неудачи к неудаче без потери энтузиазма. Продолжай идти!",
                "Не говори, что у тебя мало времени. У тебя ровно столько же часов в сутках, сколько было у великих людей!",
                "Будущее зависит от того, что ты делаешь сегодня. Начни прямо сейчас!",
                "Сложные задачи — как пазлы: если разобрать на кусочки, они становятся проще.",
                "Прежде чем сдаться, вспомни, зачем ты начал(а). Эта причина всё ещё важна, правда?",
                "Рост не всегда виден сразу. Даже когда ты не замечаешь изменений — они есть. Продолжай."
        };
        return messages[new Random().nextInt(messages.length)];
    }
}
