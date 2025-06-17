package com.example.vmeste;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String savedNickname = prefs.getString("nickname", "Nickname");
        TextView nicknameTextView = findViewById(R.id.textView6);
        nicknameTextView.setText(savedNickname);

        notificationsBtn = findViewById(R.id.notificationsButton);
        doneTasksBtn = findViewById(R.id.tasksButton);
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
                Intent intent = new Intent(OtherActivity.this, ChatBotActivity.class);
                startActivity(intent);
            }
        });

        nicknameTextView = findViewById(R.id.textView6);
        nicknameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNicknameDialog();
            }
        });
    }

    private void showNicknameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите ваш никнейм");

        // Создаем поле ввода
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Устанавливаем кнопки
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNickname = input.getText().toString().trim(); // trim() убирает пробелы в начале и конце
                if (!newNickname.isEmpty()) {
                    // Обновляем TextView
                    TextView nicknameTextView = findViewById(R.id.textView6);
                    nicknameTextView.setText(newNickname);

                    // Сохраняем в SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("nickname", newNickname);
                    editor.apply(); // или editor.commit() для немедленного сохранения
                }
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
