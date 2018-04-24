package pt.ulisboa.tecnico.cmu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.data.Question;
import pt.ulisboa.tecnico.cmu.data.Quiz;


public class QuizActivity extends GeneralActivity {


    private ProgressBar progressBar;

    private String solution;
    private int questionNumber;
    //private int numberOfQuestions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        this.progressBar = (ProgressBar) findViewById(R.id.quizProgressBar);

        //TODO Receive question ans answers from server and apply to view

        questionNumber = 1;
        final Quiz quiz = (Quiz) getIntent().getSerializableExtra("quiz");
        final List<Question> questions = quiz.getQuestions();
        final int numberOfQuestions = questions.size();
        drawQuestion(questions, numberOfQuestions);

        Button buttonA = findViewById(R.id.button_A);
        buttonA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                drawQuestion(questions, numberOfQuestions);
                incrementProgressBar((double) 1/numberOfQuestions * 100);
            }
        });

        Button buttonB = findViewById(R.id.button_B);
        buttonB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                drawQuestion(questions, numberOfQuestions);
                incrementProgressBar((double) 1/numberOfQuestions * 100);
            }
        });

        Button buttonC = findViewById(R.id.button_C);
        buttonC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                drawQuestion(questions, numberOfQuestions);
                incrementProgressBar((double) 1/numberOfQuestions * 100);
            }
        });

        Button buttonD = findViewById(R.id.button_D);
        buttonD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                drawQuestion(questions, numberOfQuestions);
                incrementProgressBar((double) 1/numberOfQuestions * 100);
            }
        });

    }

    private void incrementProgressBar(double amount){
        progressBar.incrementProgressBy((int) amount);
    }

    private void updateTextView(TextView textView, String content){
        textView.setText(content);
    }

    private void drawQuestion(List<Question> questions, int numberOfQuestions) {
        if((questionNumber-1) < numberOfQuestions ){
            updateTextView((TextView) findViewById(R.id.text_question), questions.get(questionNumber-1).getQuestion());
            updateTextView((TextView) findViewById(R.id.text_answer_A), questions.get(questionNumber-1).getOption1());
            updateTextView((TextView) findViewById(R.id.text_answer_B), questions.get(questionNumber-1).getOption2());
            updateTextView((TextView) findViewById(R.id.text_answer_C), questions.get(questionNumber-1).getOption3());
            updateTextView((TextView) findViewById(R.id.text_answer_D), questions.get(questionNumber-1).getOption4());
            solution = questions.get(questionNumber-1).getSolution();
            questionNumber++;
        }
        else {
            updateTextView((TextView) findViewById(R.id.text_question), "Submited your answers to the server");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkAnswer(String answer) {
        if(answer.equals(solution)){
            // TODO: implement the check if a received answer is right and increment the bars in the bottom accordingly
        }
    }

    @Override
    public void updateInterface(Response response) {

    }
}