package com.example.vmeste;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Build;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TasksActivity extends BaseActivity {
    private TextView[] dayTextViews;
    private ImageButton[] dayButtons;
    private ImageButton addTaskBtn;
    private int currentDay;
    private int currentMonth;
    private int currentYear;

    private static final String PREFS_NAME = "TasksPrefs";
    private static final String KEY_DAY = "currentDay";
    private static final String KEY_MONTH = "currentMonth";
    private static final String KEY_YEAR = "currentYear";

    @Override
    protected int getLayoutResource() {
        return R.layout.task_overview;
    }

    @Override
    protected void highlightCurrentButton() {
        tasksBtn.setBackgroundResource(R.drawable.rectorange);
        tasksBtn.setImageResource(R.drawable.taskscurr);
        homeBtn.setBackgroundResource(R.drawable.rect);
        menuBtn.setBackgroundResource(R.drawable.rect);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //////
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        loadCurrentDate();

        dayTextViews = new TextView[] {
                findViewById(R.id.day1_text),
                findViewById(R.id.day2_text),
                findViewById(R.id.day3_text),
                findViewById(R.id.day4_text),
                findViewById(R.id.day5_text)
        };

        dayButtons = new ImageButton[] {
                findViewById(R.id.day1),
                findViewById(R.id.day2),
                findViewById(R.id.day3),
                findViewById(R.id.day4),
                findViewById(R.id.day5)
        };

        ImageButton calendarBtn = findViewById(R.id.calendar);
        ImageButton nextWeekBtn = findViewById(R.id.next_week);

        calendarBtn.setOnClickListener(v -> showCalendarPopup());
        nextWeekBtn.setOnClickListener(v -> updateDays(3));

        addTaskBtn = findViewById(R.id.addTaskButton);
        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TasksActivity.this, AddTaskActivity.class); // В главную активность добавляются лишние задачи тк AddTaskActivity привязана к ней
                startActivity(intent);
            }
        });

        for (int i = 0; i < dayButtons.length; i++) {
            final int dayIndex = i;
            dayButtons[i].setOnClickListener(v -> {

                Calendar tempCalendar = Calendar.getInstance();
                tempCalendar.set(currentYear, currentMonth, currentDay);
                tempCalendar.add(Calendar.DAY_OF_MONTH, dayIndex - 2);

                currentDay = tempCalendar.get(Calendar.DAY_OF_MONTH);
                currentMonth = tempCalendar.get(Calendar.MONTH);
                currentYear = tempCalendar.get(Calendar.YEAR);

                saveCurrentDate();
                updateDays(0);
            });
        }

        updateDays(0);
    }
    private void updateDays(int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth, currentDay);
        calendar.add(Calendar.DAY_OF_MONTH, daysToAdd);

        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        saveCurrentDate();

        for (int i = 0; i < 5; i++) {
            Calendar dayCalendar = (Calendar) calendar.clone();
            dayCalendar.add(Calendar.DAY_OF_MONTH, i - 2);

            int day = dayCalendar.get(Calendar.DAY_OF_MONTH);
            dayTextViews[i].setText(String.valueOf(day));

            if (i == 2) {
                dayButtons[i].setImageResource(R.drawable.calendar_ellipse_curr);
            } else {
                dayButtons[i].setImageResource(R.drawable.calendar_ellipse);
            }
        }
    }

    private void saveCurrentDate() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_DAY, currentDay);
        editor.putInt(KEY_MONTH, currentMonth);
        editor.putInt(KEY_YEAR, currentYear);
        editor.apply();
    }

    private void loadCurrentDate() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Calendar defaultCalendar = Calendar.getInstance();
        currentDay = preferences.getInt(KEY_DAY, defaultCalendar.get(Calendar.DAY_OF_MONTH));
        currentMonth = preferences.getInt(KEY_MONTH, defaultCalendar.get(Calendar.MONTH));
        currentYear = preferences.getInt(KEY_YEAR, defaultCalendar.get(Calendar.YEAR));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentDay", currentDay);
        outState.putInt("currentMonth", currentMonth);
        outState.putInt("currentYear", currentYear);
    }

    private void showCalendarPopup() {
        View popupView = getLayoutInflater().inflate(R.layout.popup_calendar, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );

        // Находим TableLayout внутри popupView, а не в основном layout
        TableLayout table = popupView.findViewById(R.id.calendarTable);
        TextView monthView = popupView.findViewById(R.id.popupMonth);

        // Инициализируем календарь
        updateCalendarTable(currentYear, currentMonth, table, monthView);

        // Обработчики кнопок
        ImageButton arrowRight = popupView.findViewById(R.id.arrowRight);
        ImageButton arrowLeft = popupView.findViewById(R.id.arrowLeft);
        ImageButton CloseBtn = popupView.findViewById(R.id.popupClose);

        arrowRight.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            updateCalendarTable(currentYear, currentMonth, table, monthView);
        });

        arrowLeft.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            updateCalendarTable(currentYear, currentMonth, table, monthView);
        });
        CloseBtn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View popupView) {
                    popupWindow.dismiss();
                }
            });
        popupWindow.showAtLocation(findViewById(R.id.tasks_overview), Gravity.TOP, 0, 380);
    }

    private void updateCalendarTable(int year, int month, TableLayout table, TextView monthView) {
        // Удаляем все строки кроме заголовка
        int childCount = table.getChildCount();
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }

        // Устанавливаем месяц в заголовок
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", new Locale("ru"));
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        monthView.setText(monthFormat.format(cal.getTime()));

        // Получаем количество дней в месяце
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Получаем день недели первого дня месяца (1-7, где 1=воскресенье)
        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        // Корректируем для нашего формата (пн=0, вс=6)
        int firstDayPosition = (firstDayOfWeek + 5) % 7;

        // Создаем строки календаря
        int dayCounter = 1;

        // Первая строка (может начинаться с пустых ячеек)
        TableRow currentRow = createNewTableRow();

        // Добавляем пустые ячейки до первого дня месяца
        for (int i = 0; i < firstDayPosition; i++) {
            currentRow.addView(createEmptyDayView());
        }

        // Добавляем дни первой недели
        int daysInFirstWeek = 7 - firstDayPosition;
        for (int i = 0; i < daysInFirstWeek && dayCounter <= daysInMonth; i++) {
            currentRow.addView(createDayView(dayCounter, year, month));
            dayCounter++;
        }
        table.addView(currentRow);

        // Добавляем полные недели
        while (dayCounter <= daysInMonth) {
            currentRow = createNewTableRow();

            // Определяем сколько дней добавлять в текущую строку
            int daysToAdd = Math.min(7, daysInMonth - dayCounter + 1);

            for (int i = 0; i < daysToAdd; i++) {
                currentRow.addView(createDayView(dayCounter, year, month));
                dayCounter++;
            }

            // Если это последняя строка и дней меньше 7
            if (daysToAdd < 7) {
                currentRow.setWeightSum(0); // Отключаем растягивание
            }

            table.addView(currentRow);
        }
    }

    private TableRow createNewTableRow() {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 8, 0, 8); // Добавляем отступы между строками
        row.setLayoutParams(params);
        row.setGravity(Gravity.CENTER);
        return row;
    }

    private TextView createEmptyDayView() {
        TextView view = new TextView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);
        view.setLayoutParams(params);
        view.setText("");
        return view;
    }

    private TextView createDayView(int day, int year, int month) {
        TextView dayView = new TextView(this);

        // Используем вес 1 для всех дней, кроме последней строки
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0, // 0 означает использование веса
                TableRow.LayoutParams.WRAP_CONTENT,
                1f); // Вес 1

        params.setMargins(4, 4, 4, 4);
        dayView.setLayoutParams(params);

        dayView.setGravity(Gravity.CENTER);
        dayView.setText(String.valueOf(day));
        dayView.setTextColor(Color.BLACK);
        dayView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        // Обработчик клика
        dayView.setOnClickListener(v -> {
            currentDay = day;
            currentMonth = month;
            currentYear = year;
            saveCurrentDate();
            updateDays(0);
            ((PopupWindow) ((View) v.getParent().getParent().getParent()).getParent()).dismiss();
        });

        return dayView;
    }
}

