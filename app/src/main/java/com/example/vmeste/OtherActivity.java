package com.example.vmeste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class OtherActivity extends BaseActivity {
    protected ImageButton notificationsBtn, doneTasksBtn, exportBtn, botBtn, pointerBtn;
    @Override
    protected int getLayoutResource() {
        return R.layout.other;
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

        notificationsBtn = findViewById(R.id.notificationsButton);
        doneTasksBtn = findViewById(R.id.tasksButton);
        exportBtn = findViewById(R.id.exportButton);
        botBtn = findViewById(R.id.botChatButton);

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherActivity.this, NotificationsActivity.class);
                startActivity(intent);
            }
        });

        doneTasksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherActivity.this, DoneTasksActivity.class);
                startActivity(intent);
            }
        });

        botBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtherActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
//
//        exportBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(OtherActivity.this, ExportActivity.class);
//                startActivity(intent);
//            }
//        });
//
    }

}
