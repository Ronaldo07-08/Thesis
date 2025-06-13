package com.example.vmeste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

// BaseActivity.java
public abstract class BaseActivity extends AppCompatActivity {
    protected ImageButton homeBtn, tasksBtn, menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        // Включаем нижнее меню
        setupBottomMenu();
    }

    protected abstract int getLayoutResource();

    private void setupBottomMenu() {
        // Инфлейтим меню
        View menuView = findViewById(R.id.bottom_menu);
        // Находим кнопки
        homeBtn = menuView.findViewById(R.id.home);
        tasksBtn = menuView.findViewById(R.id.tasks);
        menuBtn = menuView.findViewById(R.id.menu);

        // Устанавливаем обработчики
        homeBtn.setOnClickListener(v -> navigateTo(MainActivity.class));
        tasksBtn.setOnClickListener(v -> navigateTo(TasksActivity.class));
        menuBtn.setOnClickListener(v -> navigateTo(OtherActivity.class));

        // Подсвечиваем текущую кнопку
        highlightCurrentButton();
    }

    private void navigateTo(Class<?> activityClass) {
        if (this.getClass() != activityClass) {
            startActivity(new Intent(this, activityClass));
            finish();
        }
    }

    protected abstract void highlightCurrentButton();
}
