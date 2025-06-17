package com.example.vmeste;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Random;

public class NotificationsActivity extends BaseActivity {

    static final String PREFS_NAME = "NotificationPrefs";
    static final String MOTIVATION_KEY = "motivation_enabled";
    static final String DEADLINES_KEY = "deadlines_enabled";
    static final String RECOMMENDATIONS_KEY = "recommendations_enabled";
    static final int MOTIVATION_ID = 1;
    static final int DEADLINES_ID = 2;
    static final int RECOMMENDATIONS_ID = 3;
    private Switch motivationButton, DedlinesButton, recomendationsButton;
    private ImageButton pointerButton;
    private AlarmManager alarmManager;

    @Override
    protected int getLayoutResource() {
        return R.layout.notifications;
    }

    @Override
    protected void highlightCurrentButton() {
        tasksBtn.setBackgroundResource(R.drawable.rect);
        menuBtn.setImageResource(R.drawable.menucurr);
        homeBtn.setBackgroundResource(R.drawable.rect);
        menuBtn.setBackgroundResource(R.drawable.rectorange);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        motivationButton = findViewById(R.id.motivationButton);
        DedlinesButton = findViewById(R.id.DedlinesButton);
        pointerButton = findViewById(R.id.pointer);

        loadSwitchStates();
        setupSwitches();

        pointerButton.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationsActivity.this, OtherActivity.class);
            startActivity(intent);
        });
    }

    private void setupSwitches() {
        motivationButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(MOTIVATION_KEY, isChecked);
            if (isChecked) {
                scheduleMotivationalNotifications();
            } else {
                cancelNotification(MOTIVATION_ID);
            }
            showToast("Мотивационные уведомления " + (isChecked ? "включены" : "выключены"));
        });

        DedlinesButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(DEADLINES_KEY, isChecked);
            if (isChecked) {
                scheduleDeadlineNotifications();
            } else {
                cancelNotification(DEADLINES_ID);
            }
            showToast("Уведомления о дедлайнах " + (isChecked ? "включены" : "выключены"));
        });
    }

    private void scheduleMotivationalNotifications() {
        cancelNotification(MOTIVATION_ID);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("notification_id", MOTIVATION_ID);
        intent.putExtra("title", "Мотивация");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                MOTIVATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                60 * 1000,
                pendingIntent
        );
    }

    private void scheduleDeadlineNotifications() {
        cancelNotification(DEADLINES_ID);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("notification_id", DEADLINES_ID);
        intent.putExtra("title", "Дедлайны");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                DEADLINES_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private void scheduleNotification(int id, int hour, int minute, String title, String message) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("notification_id", id);
        intent.putExtra("title", title);
        intent.putExtra("message", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        );
    }

    private void cancelNotification(int id) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        alarmManager.cancel(pendingIntent);
    }

    private void loadSwitchStates() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        motivationButton.setChecked(prefs.getBoolean(MOTIVATION_KEY, false));
        DedlinesButton.setChecked(prefs.getBoolean(DEADLINES_KEY, false));
    }

    private void saveSwitchState(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}