package pt.ulisboa.tecnico.cmu.activity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.communication.response.Response;


public class QuizActivity extends GeneralActivity {


    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //TODO Receive question ans answers from server and apply to view
    }

    private void incrementProgressBar(int amount){
        progressBar.incrementProgressBy(amount);
    }

    private void updateTextView(TextView textView, String content){
        textView.setText(content);
    }

    @Override
    public void updateInterface(Response response) {

    }
}