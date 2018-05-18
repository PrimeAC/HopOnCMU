package pt.ulisboa.tecnico.cmu.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.communication.ClientSocket;
import pt.ulisboa.tecnico.cmu.communication.command.SubmitQuizCommand;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.communication.response.SubmitQuizResponse;
import pt.ulisboa.tecnico.cmu.data.Question;
import pt.ulisboa.tecnico.cmu.data.Quiz;
import pt.ulisboa.tecnico.cmu.database.UserQuizDBHandler;

import static android.graphics.Color.GRAY;


public class QuizActivity extends GeneralActivity {


    private ProgressBar progressBar;

    private String solution;
    private int questionNumber;
    private List<String> answers = new ArrayList<>();
    private UserQuizDBHandler db;
    private int numberOfRightAnswers;
    private int numberOfWrongAnswers;

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
        db = new UserQuizDBHandler(this);

        Button buttonA = findViewById(R.id.button_A);
        buttonA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String answer = ((TextView) findViewById(R.id.text_answer_A)).getText().toString();
                checkAnswer(answer);
                String option = "A";
                answers.add(answer);
                rightAnswer(answer, option);
                db.insertAnswers(questionNumber-1, ((TextView) findViewById(R.id.button_A)).getText().toString());
                drawQuestion(questions, numberOfQuestions);
                incrementProgressBar((double) 1/numberOfQuestions * 100);
            }
        });

        Button buttonB = findViewById(R.id.button_B);
        buttonB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String answer = ((TextView) findViewById(R.id.text_answer_B)).getText().toString();
                checkAnswer(answer);
                String option = "B";
                answers.add(answer);
                rightAnswer(answer, option);
                db.insertAnswers(questionNumber-1, ((TextView) findViewById(R.id.button_B)).getText().toString());
                drawQuestion(questions, numberOfQuestions);
                incrementProgressBar((double) 1/numberOfQuestions * 100);
            }
        });

        Button buttonC = findViewById(R.id.button_C);
        buttonC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String answer = ((TextView) findViewById(R.id.text_answer_C)).getText().toString();
                checkAnswer(answer);
                String option = "C";
                answers.add(answer);
                rightAnswer(answer, option);
                db.insertAnswers(questionNumber-1, ((TextView) findViewById(R.id.button_C)).getText().toString());
                drawQuestion(questions, numberOfQuestions);
                incrementProgressBar((double) 1/numberOfQuestions * 100);
            }
        });

        Button buttonD = findViewById(R.id.button_D);
        buttonD.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String answer = ((TextView) findViewById(R.id.text_answer_D)).getText().toString();
                checkAnswer(answer);
                String option = "D";
                answers.add(answer);
                rightAnswer(answer, option);
                db.insertAnswers(questionNumber-1, ((TextView) findViewById(R.id.button_D)).getText().toString());
                drawQuestion(questions, numberOfQuestions);
                incrementProgressBar((double) 1/numberOfQuestions * 100);
            }
        });

    }

    private void incrementProgressBar(double amount){
        progressBar.incrementProgressBy((int) amount);
    }

    private void setRightProgressBar(double amount, ProgressBar rightProgressBar){
        rightProgressBar.setProgress((int) amount);
    }

    private void setWrongProgressBar(double amount, ProgressBar wrongProgressBar){
        wrongProgressBar.setProgress((int) amount);
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
            new ClientSocket(this, new SubmitQuizCommand(getIntent().getStringExtra("quizName"),
                    answers, getIntent().getStringExtra("userID"), getIntent().getStringExtra("sessionID"))).execute();
        }
    }

    private void checkAnswer(String answer) {
        if(answer.equals(solution)){
            // TODO: implement the check if a received answer is right and increment the bars in the bottom accordingly
            numberOfRightAnswers++;
        }
        else {
            numberOfWrongAnswers++;
        }
    }

    private void rightAnswer(String answer, String option) {
        int id = getResources().getIdentifier("button_" + option, "id", getPackageName());
        final Button button = findViewById(id);
        if (answer.equals(solution)) {
            button.setBackground(getResources().getDrawable(R.drawable.button_right));
        }
        else {
            button.setBackground(getResources().getDrawable(R.drawable.button_wrong));
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setBackground(getResources().getDrawable(R.drawable.button_disabled));
            }
        }, 700);
    }

    @Override
    public void updateInterface(Response response) {
        SubmitQuizResponse submitQuizResponse = (SubmitQuizResponse) response;
        if(submitQuizResponse.getStatus().equals("OK")){
            db = new UserQuizDBHandler(this);
            db.updateScore(numberOfRightAnswers);
            //Intent intent = new Intent(this, MainActivity.class);
            Intent intent = new Intent();
            //intent.putExtra("userID", getIntent().getExtras().getString("userID"));
            intent.putExtra("quizName", getIntent().getStringExtra("quizName"));
            setResult(RESULT_OK, intent);
            //startActivity(intent);
            finish();
        }
        else {
            updateTextView((TextView) findViewById(R.id.text_question), "Error sending quiz to the server");
        }
    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {

    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {

    }
}