package com.example.vmeste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected ImageButton homeBtn, tasksBtn, menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        setupBottomMenu();
    }

    protected abstract int getLayoutResource();

    private void setupBottomMenu() {
        View menuView = findViewById(R.id.bottom_menu);
        homeBtn = menuView.findViewById(R.id.home);
        tasksBtn = menuView.findViewById(R.id.tasks);
        menuBtn = menuView.findViewById(R.id.menu);

        homeBtn.setOnClickListener(v -> navigateTo(MainActivity.class));
        tasksBtn.setOnClickListener(v -> navigateTo(TasksActivity.class));
        menuBtn.setOnClickListener(v -> navigateTo(OtherActivity.class));

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
