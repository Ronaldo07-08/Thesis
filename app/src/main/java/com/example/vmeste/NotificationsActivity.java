package com.example.vmeste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

public class NotificationsActivity extends BaseActivity {

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

    private Switch motivationSwitch;
    private Switch deadlinesSwitch;
    private Switch recommendationsSwitch;
    private ImageButton pointerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация переключателей
        motivationSwitch = findViewById(R.id.motivationButton);
        deadlinesSwitch = findViewById(R.id.DedlinesButton);
        recommendationsSwitch = findViewById(R.id.recomendationsButton);

        // Инициализация кнопок навигации
        pointerButton = findViewById(R.id.pointer);

        pointerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotificationsActivity.this, OtherActivity.class);
                startActivity(intent);
            }
        });
        // Обработчики для Switch
        setupSwitches();

    }

    private void setupSwitches() {
        // Обработчик для мотивационных уведомлений
        motivationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String status = isChecked ? "включены" : "выключены";
                Toast.makeText(NotificationsActivity.this,
                        "Мотивационные уведомления " + status, Toast.LENGTH_SHORT).show();

                // Здесь можно сохранить состояние в SharedPreferences
                // или отправить на сервер
            }
        });

        // Обработчик для уведомлений о дедлайнах
        deadlinesSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "включены" : "выключены";
            Toast.makeText(this,
                    "Уведомления о дедлайнах " + status, Toast.LENGTH_SHORT).show();
        });

        // Обработчик для рекомендаций
        recommendationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "включены" : "выключены";
            Toast.makeText(this,
                    "Рекомендации " + status, Toast.LENGTH_SHORT).show();
        });
    }

    // Дополнительно: загрузка сохраненных состояний переключателей
    @Override
    protected void onResume() {
        super.onResume();
        // Пример загрузки состояний из SharedPreferences
        /*
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        motivationSwitch.setChecked(prefs.getBoolean("motivation_notifications", true));
        deadlinesSwitch.setChecked(prefs.getBoolean("deadline_notifications", true));
        recommendationsSwitch.setChecked(prefs.getBoolean("recommendation_notifications", true));
        */
    }

    // Сохранение состояний при выходе
    @Override
    protected void onPause() {
        super.onPause();
        // Пример сохранения состояний
        /*
        SharedPreferences.Editor editor = getSharedPreferences("AppPrefs", MODE_PRIVATE).edit();
        editor.putBoolean("motivation_notifications", motivationSwitch.isChecked());
        editor.putBoolean("deadline_notifications", deadlinesSwitch.isChecked());
        editor.putBoolean("recommendation_notifications", recommendationsSwitch.isChecked());
        editor.apply();
        */
    }
}