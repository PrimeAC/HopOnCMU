package pt.ulisboa.tecnico.cmu.activity;

import android.support.v7.app.AppCompatActivity;

import pt.ulisboa.tecnico.cmu.response.Response;

/**
 * Created by bruno on 17-04-2018.
 */

public abstract class GeneralActivity extends AppCompatActivity {

    public abstract void updateInterface(Response response);
}
