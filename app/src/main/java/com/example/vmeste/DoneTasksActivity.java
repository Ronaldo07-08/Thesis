package com.example.vmeste;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class DoneTasksActivity extends BaseActivity {

    private ImageButton pointerButton;

    @Override
    protected int getLayoutResource() {
        return R.layout.done_tasks;
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

        pointerButton = findViewById(R.id.pointer);

        pointerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoneTasksActivity.this, OtherActivity.class);
                startActivity(intent);
            }
        });
    }
}
