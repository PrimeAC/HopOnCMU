package pt.ulisboa.tecnico.cmu.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.communication.clientSocket;
import pt.ulisboa.tecnico.cmu.response.Response;

import static android.Manifest.permission.READ_CONTACTS;

import pt.ulisboa.tecnico.cmu.command.TicketCommand;
import pt.ulisboa.tecnico.cmu.response.TicketResponse;

/**
 * A login screen that offers login via ticket code.
 */
public class ValidateActivity extends GeneralActivity {

    /**
     * A dummy authentication store containing known ticket codes.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "ABC123", "DEF456"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

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
        if (mAuthTask != null) {
            return;
        }

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
            new clientSocket(this, new TicketCommand(ticketCode)).execute();
            mAuthTask = new UserLoginTask(ticketCode, this);
            mAuthTask.execute((Void) null);
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
        TicketResponse ticketResponse = (TicketResponse) response;
        if (ticketResponse.getMessage().equals("OK")){
            //TODO: implement the login activity
            Log.i("1", "OK-----------------------------------------");
        }
        else if (ticketResponse.getMessage().equals("NU")){
            // TODO: implement the jump to sign up activity
            Log.i("2", "NU-----------------------------------------");
        }
        else {
            //TODO: print an error message to the user
            Log.i("3", "NOK-----------------------------------------");
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mTicketCode;
        private final Activity mActivity;

        UserLoginTask(String ticketCode, Activity activity) {
            mTicketCode = ticketCode;
            mActivity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                if (credential.equals(mTicketCode)) {
                    // Ticket code exists, return true.
                    return true;
                }
            }

            // TODO: ticket code does not exist
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //finish();
            } else {
                Intent intent = new Intent(mActivity, SignUpActivity.class);
                mActivity.startActivity(intent);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

