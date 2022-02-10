package csumb.flashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import csumb.flashcards.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private List<Flashcard> flashcardList;

    private static final Random RANDOM = new Random();
    private int currentFlashcardId;
    private int cardsRemaining;
    private Flashcard currentFlashcard;
    private Toast currentAnswerToast;
    private Button markCorrectButton;
    private Button showAnswerButton;
    private Button randomCardButton;
    private Button resetCardsButton;

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        markCorrectButton = binding.markCorrectButton;
        markCorrectButton.setOnClickListener(v -> markComplete());
        showAnswerButton = binding.showAnswerButton;
        showAnswerButton.setOnClickListener(v -> showAnswer());
        randomCardButton = binding.randomCardButton;
        randomCardButton.setOnClickListener(v -> pickRandomFlashcard());
        resetCardsButton = binding.resetCardsButton;
        resetCardsButton.setOnClickListener(v -> resetCards());
        binding.addCardButton.setOnClickListener(v -> addCard(v));
        // ORDER BELOW CANNOT CHANGE
        resetCards();
        pickRandomFlashcard();
        // ORDER ABOVE CANNOT CHANGE
    }

    public void addCard(View v) {
        Intent intent = new Intent(this, NewCardActivity.class);
        startActivity(intent);
    }

    private void loadQuestions() {
        flashcardList = Arrays.asList(
                new Flashcard(this, R.string.question_compiler , R.string.answer_compiler ),
                new Flashcard(this, R.string.question_https , R.string.answer_https ),
                new Flashcard(this, R.string.question_os , R.string.answer_os ),
                new Flashcard(this, R.string.question_rdbms, R.string.answer_rdbms )
        );
    }

    @SuppressLint("SetTextI18n")
    private void resetCards() {
        if(flashcardList == null || flashcardList.size() == 0) {
            loadQuestions();
        } else {
            for (Flashcard f : flashcardList) {
                f.setComplete(false);
            }
            toggleButtons(true);
            pickRandomFlashcard();
        }
        cardsRemaining = flashcardList.size();
        updateRandomButton();
    }

    private void updateRandomButton() {
        randomCardButton.setText(getString(R.string.random_button_text, cardsRemaining));
    }

    private void toggleButtons(boolean enable) {
        markCorrectButton.setEnabled(enable);
        showAnswerButton.setEnabled(enable);
        randomCardButton.setEnabled(enable);
    }

    private void showAnswer() {
        if(currentAnswerToast != null) {
            currentAnswerToast.show();
        } else {
            Toast.makeText(getApplicationContext(), "WTF!?", Toast.LENGTH_LONG).show();
        }
    }
    private void markComplete() {
        if(cardsRemaining == 1 && currentFlashcard != null) {
            // turn off mark correct, show answer, and random card buttons
            toggleButtons(false);
            binding.questionTextView.setText(R.string.cards_finished_text);
        } else {
            currentFlashcard.setComplete(true);
            pickRandomFlashcard();
        }
        cardsRemaining--;
        updateRandomButton();
    }

    private void pickRandomFlashcard() {
        if(cardsRemaining == 1) {
            return;
        }

        int random;
        // can't look at the same card twice in a row unless it's the last card
        // and can't look at complete cards
        do {
            random = RANDOM.nextInt(flashcardList.size());
        } while (random == currentFlashcardId ||
                flashcardList.get(random).isComplete());
        currentFlashcardId = random;
        currentFlashcard = flashcardList.get(currentFlashcardId);
        binding.questionTextView.setText(currentFlashcard.getQuestion());
        currentAnswerToast = Toast.makeText(getApplicationContext(),
                currentFlashcard.getAnswer(), Toast.LENGTH_LONG);
    }
}