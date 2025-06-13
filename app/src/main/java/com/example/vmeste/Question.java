package com.example.vmeste;

import java.util.Random;

public class Question {
    private String text;
    private String[] options;
    private int correctOptionIndex;
    private String[] shuffledOptions;

    public Question(String text, String[] options, int correctOptionIndex) {
        this.text = text;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        shuffleOptions();
    }

    public String getText() {
        return text;
    }

    public String[] getOptions() {
        return shuffledOptions;
    }

    public boolean isCorrect(int selectedIndex) {
        return shuffledOptions[selectedIndex].equals(options[correctOptionIndex]);
    }

    public String getCorrectAnswer() {
        return options[correctOptionIndex];
    }

    private void shuffleOptions() {
        // Копируем и перемешиваем варианты
        shuffledOptions = options.clone();

        // Запоминаем правильный ответ
        String correctAnswer = shuffledOptions[correctOptionIndex];

        // Перемешиваем массив
        Random random = new Random();
        for (int i = shuffledOptions.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = shuffledOptions[index];
            shuffledOptions[index] = shuffledOptions[i];
            shuffledOptions[i] = temp;
        }

        // Обновляем индекс правильного ответа после перемешивания
        for (int i = 0; i < shuffledOptions.length; i++) {
            if (shuffledOptions[i].equals(correctAnswer)) {
                correctOptionIndex = i;

                break;
            }
        }
    }
}
