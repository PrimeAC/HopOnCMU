package pt.ulisboa.tecnico.cmu.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.crypto.SecretKey;

import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.communication.ClientSocket;
import pt.ulisboa.tecnico.cmu.communication.command.SignUpCommand;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.communication.response.SignUpResponse;
import pt.ulisboa.tecnico.cmu.database.UsersScoreDBHandler;
import pt.ulisboa.tecnico.cmu.fragment.monuments.MonumentsListContent;
import pt.ulisboa.tecnico.cmu.security.SecurityManager;


public class SignUpActivity extends GeneralActivity {

    //UI references
    private EditText mUsernameView;
    private View mProgressView;
    private View mSignUpFormView;
    private UsersScoreDBHandler dbscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsernameView = findViewById(R.id.input_new_username);
        mSignUpFormView = findViewById(R.id.signup_form);
        mProgressView = findViewById(R.id.signup_progress);


        Button mSignInButton = (Button) findViewById(R.id.btn_signUp);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        mUsernameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                attemptSignUp();
                return false;
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp() {

        //Reset errors
        mUsernameView.setError(null);

        // Store values at the time of the sign up attempt.
        String username = mUsernameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        //Check for a valid username.
        if(TextUtils.isEmpty(username)){
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if(!isUsernameValid(username)){
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                String data = extras.getString("ticketCode");
                SecretKey randomAESKey = SecurityManager.generateRandomAESKey();
                new ClientSocket(this, new SignUpCommand(data, username,randomAESKey),
                        null, randomAESKey).execute();
            }
        }


    }

    private boolean isUsernameValid(String username){
        //TODO: Implement your username format logic
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSignUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSignUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void updateInterface(Response response) {
        showProgress(false);
        SignUpResponse signUpResponse = (SignUpResponse) response;
        switch (signUpResponse.getStatus()) {
            case "OK":
                //Add sessionkey
                SecurityManager.sessionKey = SecurityManager.getKeyFromString(signUpResponse.getSessionID());
                dbscore = new UsersScoreDBHandler(this);
                dbscore.insertName(signUpResponse.getUserID());
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("userID", signUpResponse.getUserID());
                intent.putExtra("sessionID", signUpResponse.getSessionID());
                MonumentsListContent.addMonuments(signUpResponse.getMonumentsNames());
                Intent previousIntent = new Intent();
                setResult(RESULT_OK, previousIntent);
                this.startActivity(intent);
                finish();
                break;
            case "NOK":
                mUsernameView.setError(getString(R.string.error_occupied_username));
                mUsernameView.requestFocus();
                break;
        }

    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {

    }

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList) {

    }
}
