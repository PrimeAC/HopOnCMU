package pt.ulisboa.tecnico.cmu.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.database.UserQuizDBHandler;

public class HistoryActivity extends AppCompatActivity {

    private UserQuizDBHandler db;
    private String userName;
    private String monumentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        userName = getIntent().getStringExtra("userName");
        monumentName = getIntent().getStringExtra("monumentName");

        db = new UserQuizDBHandler(this);

        //Populate viewtable
        updateTextView((TextView) findViewById(R.id.answer1),
                db.getQuestionAnswerByUserAndQuiz(userName,monumentName,1));
        updateTextView((TextView) findViewById(R.id.answer2),
                db.getQuestionAnswerByUserAndQuiz(userName,monumentName,2));
        updateTextView((TextView) findViewById(R.id.answer3),
                db.getQuestionAnswerByUserAndQuiz(userName,monumentName,3));

    }

    private void updateTextView(TextView textView, String content){
        textView.setText(content);
    }

}
