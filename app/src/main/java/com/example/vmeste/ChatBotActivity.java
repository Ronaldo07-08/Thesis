package com.example.vmeste;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ChatBotActivity extends AppCompatActivity {
    private ImageButton toggleOptionsButton;
    private boolean areOptionsVisible = false;
    private boolean isWelcomePhase = true;
    private RecyclerView chatRecyclerView;
    private FrameLayout answerOptionsContainer;
    private LinearLayout buttonsContainer;
    private ChatAdapter adapter;
    private List<ChatMessage> messages = new ArrayList<>();


    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;

    private final Question[] questions = {
            new Question(
                    "Как объявить массив чисел?",
                    new String[]{"int[]", "int array[]", "Array<int>", "new int()"},
                    0
            ),
            new Question(
                    "Как вывести текст в консоль?",
                    new String[]{"Console.WriteLine", "System.out.println", "print()", "Console.Print"},
                    0
            ),
            new Question(
                    "Какой цикл работает пока условие true?",
                    new String[]{"while", "for", "do...while", "loop"},
                    0
            ),
            new Question(
                    "Как создать список?",
                    new String[]{"List<T>", "ArrayList", "new List[]", "Array<T>"},
                    0
            ),
            new Question(
                    "Как проверить равенство чисел?",
                    new String[]{"==", ".equals()", "=", "==="},
                    0
            ),
            new Question(
                    "Как объявить константу?",
                    new String[]{"const", "final", "readonly", "static"},
                    0
            ),
            new Question(
                    "Как преобразовать строку в число?",
                    new String[]{"int.Parse", "Convert.ToInt", "ToInt()", "(int)string"},
                    0
            ),
            new Question(
                    "Какой метод возвращает длину строки?",
                    new String[]{"Length", "size()", "count()", "len()"},
                    0
            ),
            new Question(
                    "Как остановить цикл?",
                    new String[]{"break", "exit", "return", "stop"},
                    0
            ),
            new Question(
                    "Какой алгоритм сортировки самый медленный?",
                    new String[]{"пузырьком", "Быстрая сортировка", "Сортировка вставками", "Сортировка выбором"},
                    0
            )
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_bot);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }

        toggleOptionsButton = findViewById(R.id.toggleOptionsButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        answerOptionsContainer = findViewById(R.id.answerOptionsContainer);
        buttonsContainer = findViewById(R.id.buttonsContainer);
        toggleOptionsButton.setOnClickListener(v -> {
            areOptionsVisible = !areOptionsVisible;
            answerOptionsContainer.setVisibility(areOptionsVisible ? View.VISIBLE : View.GONE);
        });

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        answerOptionsContainer = findViewById(R.id.answerOptionsContainer);

        if (chatRecyclerView == null) throw new RuntimeException("RecyclerView not found");
        if (answerOptionsContainer == null) throw new RuntimeException("Options container not found");

        adapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapter);
        startWelcomeDialog();
    }

    private void startWelcomeDialog() {
        isWelcomePhase = true;
        addMessage("Добро пожаловать в Thesis!", true);

        new Handler().postDelayed(() -> {
            addMessage("Сейчас я задам тебе несколько вопросов.", true);

            new Handler().postDelayed(() -> {
                addMessage("Это необходимо для выявления уровня твоих знаний!", true);

                new Handler().postDelayed(() -> {
                    buttonsContainer.removeAllViews();
                    answerOptionsContainer.setVisibility(View.VISIBLE);

                    Button okButton = new Button(this, null, 0, R.style.TransparentButton);
                    okButton.setText("Хорошо!");
                    okButton.setPadding(32, 0, 32, 0);
                    okButton.setTextSize(15);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(32, 0, 32, 100);
                    okButton.setLayoutParams(params);

                    okButton.setOnClickListener(v -> {
                        addMessage("Хорошо!", false);
                        answerOptionsContainer.setVisibility(View.GONE);
                        isWelcomePhase = false;
                        showNextQuestion();
                    });

                    buttonsContainer.addView(okButton);
                }, 1000);
            }, 1000);
        }, 1000);
    }


    private void showNextQuestion() {
        if (currentQuestionIndex >= questions.length) {
            showFinalResults();
            return;
        }

        Question currentQuestion = questions[currentQuestionIndex];
        addMessage(currentQuestion.getText(), true);
        showAnswerOptions(currentQuestion);
    }


    private void showAnswerOptions(Question question) {
        buttonsContainer.removeAllViews();
        answerOptionsContainer.setVisibility(View.VISIBLE);

        String[] shuffledOptions = question.getShuffledOptions();
        for (int i = 0; i < shuffledOptions.length; i++) {
            Button button = new Button(this, null, 0, R.style.TransparentButton);

            button.setText(shuffledOptions[i]);
            button.setPadding(52, 20, 52, 0);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(40, -40, 40, 0);
            button.setLayoutParams(params);

            final int selectedIndex = i;
            button.setOnClickListener(v -> processAnswer(question, selectedIndex));

            buttonsContainer.addView(button);
        }
    }

    private void processAnswer(Question question, int selectedIndex) {
        String selectedAnswer = question.getShuffledOptions()[selectedIndex];
        addMessage(selectedAnswer, false);

        if (question.isCorrect(selectedIndex)) {
            correctAnswersCount++;
            addMessage("✓ Верно!", true);
        } else {
            addMessage(String.format("✗ Неверно. Правильный ответ: %s",
                    question.getCorrectAnswer()), true);
        }

        currentQuestionIndex++;
        answerOptionsContainer.setVisibility(View.GONE);

        if (currentQuestionIndex < questions.length) {
            new Handler().postDelayed(this::showNextQuestion, 1500);
        } else {
            showFinalResults();
        }
    }

    private void showFinalResults() {
        int correctAnswers = correctAnswersCount;
        int totalQuestions = questions.length;

        int skillLevel;
        if (correctAnswers >= 9) skillLevel = 5;
        else if (correctAnswers >= 7) skillLevel = 4;
        else if (correctAnswers >= 5) skillLevel = 3;
        else if (correctAnswers >= 3) skillLevel = 2;
        else skillLevel = 1;

        UserSkills.setSkillLevel(this, skillLevel);

        String result = String.format(Locale.getDefault(),
                "Тест завершен!\nПравильных ответов: %d из %d\n" +
                        "Ваш уровень навыков: %d",
                correctAnswers, totalQuestions, skillLevel);
        addMessage(result, true);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(ChatBotActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 2500);
    }

    private void addMessage(String text, boolean isBot) {
        runOnUiThread(() -> {
            messages.add(new ChatMessage(text, isBot));
            adapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.smoothScrollToPosition(messages.size() - 1);
        });
    }

    private static class Question {
        private final String text;
        private final String[] options;
        private final int correctIndex;
        private String[] shuffledOptions;

        public Question(String text, String[] options, int correctIndex) {
            this.text = text;
            this.options = options;
            this.correctIndex = correctIndex;
            shuffleOptions();
        }

        public String getText() {
            return text;
        }

        public String[] getShuffledOptions() {
            return shuffledOptions;
        }

        public String getCorrectAnswer() {
            return options[correctIndex];
        }

        public boolean isCorrect(int shuffledIndex) {
            return shuffledOptions[shuffledIndex].equals(options[correctIndex]);
        }

        private void shuffleOptions() {
            shuffledOptions = options.clone();
            List<String> optionsList = Arrays.asList(shuffledOptions);
            Collections.shuffle(optionsList);
            shuffledOptions = optionsList.toArray(new String[0]);
        }
    }
}
