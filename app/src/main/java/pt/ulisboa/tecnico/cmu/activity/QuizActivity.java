package pt.ulisboa.tecnico.cmu.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.communication.ClientSocket;
import pt.ulisboa.tecnico.cmu.communication.command.SubmitQuizCommand;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.communication.response.SubmitQuizResponse;
import pt.ulisboa.tecnico.cmu.data.Question;
import pt.ulisboa.tecnico.cmu.data.Quiz;
import pt.ulisboa.tecnico.cmu.database.UserQuizDBHandler;
import pt.ulisboa.tecnico.cmu.termite.SimWifiP2pBroadcastReceiver;


public class QuizActivity extends GeneralActivity {


    private ProgressBar progressBar;

    private String solution;
    private int questionNumber;
    private List<String> answers = new ArrayList<>();
    private UserQuizDBHandler db;
    private int numberOfRightAnswers;
    private int numberOfWrongAnswers;

    private int otherCliRightAnswers = 0;
    private int otherCliWrongAnswers = 0;

    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;

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

        // spawn the chat server background task
        new IncommingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);

        Button buttonA = findViewById(R.id.button_A);
        buttonA.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                String answer = ((TextView) findViewById(R.id.text_answer_A)).getText().toString();
                checkAnswer(answer);
                //sendAnswer(getIntent().getStringExtra("userID"), answer);
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
                //sendAnswer(getIntent().getStringExtra("userID"), answer);
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
                //sendAnswer(getIntent().getStringExtra("userID"), answer);
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
                //sendAnswer(getIntent().getStringExtra("userID"), answer);
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

    private void updateBars(String isRight) {
        ProgressBar rightProgressBar = (ProgressBar) findViewById(R.id.right_answer_progressbar);
        ProgressBar wrongProgressBar = (ProgressBar) findViewById(R.id.wrong_answer_progressbar);
        if (isRight.equals("T")) {
            otherCliRightAnswers++;
        }
        else {
            otherCliWrongAnswers++;
        }
        int total = otherCliRightAnswers + otherCliWrongAnswers;
        double right = (double) otherCliRightAnswers/total;
        double wrong = (double) otherCliWrongAnswers/total;
        setRightProgressBar(right*100, rightProgressBar);
        setWrongProgressBar(wrong*100, wrongProgressBar);
    }

    private void rightAnswer(String answer, String option) {
        int id = getResources().getIdentifier("button_" + option, "id", getPackageName());
        final Button button = findViewById(id);
        String isRigth;
        if (answer.equals(solution)) {
            button.setBackground(getResources().getDrawable(R.drawable.button_right));
            isRigth = "T";
        }
        else {
            button.setBackground(getResources().getDrawable(R.drawable.button_wrong));
            isRigth = "F";
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setBackground(getResources().getDrawable(R.drawable.button_disabled));
            }
        }, 700);
        sendAnswer(getIntent().getStringExtra("userID"), answer, isRigth);
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

    private void sendAnswer(String userID, String answer, String isRight) {
        //tenho de obter os que estao no meu grupo e meter o ip na task
        System.out.println("grupo --------- " + peersInGroup);
        for (String ip : peersInGroup) {
            new SendCommTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR,
                    new String[]{ip, userID + " answered " + answer, isRight});
        }
    }

    /*
	 * Asynctasks implementing message exchange
	 */

    public class IncommingCommTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            System.out.println( "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(
                        Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    try {
                        BufferedReader sockIn = new BufferedReader(
                                new InputStreamReader(sock.getInputStream()));
                        String st = sockIn.readLine();
                        publishProgress(st);
                        sock.getOutputStream().write(("\n").getBytes());
                    } catch (IOException e) {
                        Log.d("Error reading socket:", e.getMessage());
                    } finally {
                        sock.close();
                    }
                } catch (IOException e) {
                    Log.d("Error socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String value[] = values[0].split("\\|");
            String toDisplay = value[0];
            String isRight = value[1];
            Toast.makeText(getApplicationContext(), toDisplay, Toast.LENGTH_SHORT).show();
            updateBars(isRight);
        }
    }

    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                mCliSocket = new SimWifiP2pSocket(msg[0],
                        Integer.parseInt(getString(R.string.port)));
                mCliSocket.getOutputStream().write((msg[1] + "|" + msg[2] + "\n").getBytes());
                BufferedReader sockIn = new BufferedReader(
                        new InputStreamReader(mCliSocket.getInputStream()));
                sockIn.readLine();
                mCliSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCliSocket = null;
            return null;
        }
    }

}