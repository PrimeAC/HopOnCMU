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
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.communication.ClientSocket;
import pt.ulisboa.tecnico.cmu.communication.response.Response;

import pt.ulisboa.tecnico.cmu.communication.command.TicketCommand;
import pt.ulisboa.tecnico.cmu.communication.response.TicketResponse;

/**
 * A login screen that offers login via ticket code.
 */
public class ValidateActivity extends GeneralActivity {

    static final int REQUEST_EXIT = 0;

    // UI references.
    private AutoCompleteTextView mTicketCodeView;
    private View mProgressView;
    private View mLoginFormView;

    private String ticketCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);


        // Set up the login form.
        mTicketCodeView = (AutoCompleteTextView) findViewById(R.id.input_ticket_code);


        Button mSignInButton = (Button) findViewById(R.id.btn_validate);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mTicketCodeView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                attemptLogin();
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mTicketCodeView.setError(null);

        // Store values at the time of the login attempt.
        ticketCode = mTicketCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid ticket code.
        if (TextUtils.isEmpty(ticketCode)) {
            mTicketCodeView.setError(getString(R.string.error_field_required));
            focusView = mTicketCodeView;
            cancel = true;
        } else if (!isTicketCodeValid(ticketCode)) {
            mTicketCodeView.setError(getString(R.string.error_invalid_ticketCode));
            focusView = mTicketCodeView;
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
            new ClientSocket(this, new TicketCommand(ticketCode)).execute();
        }
    }

    private boolean isTicketCodeValid(String ticketCode) {
        //TODO: Replace this with your own logic
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void updateInterface(Response response) {
        showProgress(false);
        TicketResponse ticketResponse = (TicketResponse) response;
        if (ticketResponse.getMessage().get(0).equals("OK")){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userID", ticketResponse.getMessage().get(1));
            this.startActivity(intent);
            finish();
        }
        else if (ticketResponse.getMessage().get(0).equals("NU")){
            Intent intent = new Intent(this, SignUpActivity.class);
            intent.putExtra("ticketCode", ticketCode);
            this.startActivityForResult(intent, REQUEST_EXIT);
        }
        else {
            mTicketCodeView.setError(getString(R.string.error_incorrect_ticketCode));
            mTicketCodeView.requestFocus();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_EXIT)
        {
            finish();
        }
    }

}

