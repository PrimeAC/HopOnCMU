package pt.ulisboa.tecnico.cmu.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.communication.ClientSocket;
import pt.ulisboa.tecnico.cmu.communication.command.GetQuizCommand;
import pt.ulisboa.tecnico.cmu.communication.command.GetRankingCommand;
import pt.ulisboa.tecnico.cmu.communication.response.GetQuizResponse;
import pt.ulisboa.tecnico.cmu.communication.response.GetRankingResponse;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.data.Quiz;
import pt.ulisboa.tecnico.cmu.database.UserQuizDBHandler;
import pt.ulisboa.tecnico.cmu.database.UsersScoreDBHandler;
import pt.ulisboa.tecnico.cmu.fragment.MonumentsFragment;
import pt.ulisboa.tecnico.cmu.fragment.RankingFragment;
import pt.ulisboa.tecnico.cmu.fragment.monuments.MonumentsListContent;
import pt.ulisboa.tecnico.cmu.fragment.monuments.MonumentsListContent.MonumentItem;
import pt.ulisboa.tecnico.cmu.fragment.ranking.RankingListContent;
import pt.ulisboa.tecnico.cmu.fragment.ranking.RankingListContent.RankingItem;
import pt.ulisboa.tecnico.cmu.termite.SimWifiP2pBroadcastReceiver;


public class MainActivity extends GeneralActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MonumentsFragment.OnListFragmentInteractionListener,
        RankingFragment.OnListFragmentInteractionListener{

    private boolean rankingPressed = false;
    private String userID;
    private String sessionID;
    private int REQUEST_EXIT = 0;
    private UsersScoreDBHandler dbscore;
    private UserQuizDBHandler dbquiz;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(getApplicationContext());

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        mReceiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(mReceiver, filter);

        Intent intent = new Intent(getApplicationContext(), SimWifiP2pService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userID = extras.getString("userID");
            sessionID = extras.getString("sessionID");
            View header = navigationView.getHeaderView(0);
            ((TextView) header.findViewById(R.id.userID)).setText(userID);
        }

        new ClientSocket(this, new GetRankingCommand(sessionID, userID), userID, null).execute();

        //Initializing Monument fragment
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MonumentsFragment firstFragment = new MonumentsFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_monuments) {
            rankingPressed = false;
            startMonumentsFragment();
        } else if (id == R.id.nav_ranking) {
            if(!rankingPressed){
                new ClientSocket(this, new GetRankingCommand(sessionID, userID), userID, null).execute();

            }
            rankingPressed = true;
            startRankingFragment();
        } else if (id == R.id.nav_logout) {
            rankingPressed = false;
            MonumentsListContent.deleteMonuments();
            startValidateActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void updateInterface(Response response) {
        if (response instanceof GetRankingResponse) {
            RankingListContent.deleteRanking();
            GetRankingResponse getRankingResponse = (GetRankingResponse) response;
            if (getRankingResponse.getRanking() != null){
                RankingListContent.updateRanking(getRankingResponse.getRanking());
                dbscore = new UsersScoreDBHandler(this);
                dbscore.insertScore(userID, ((GetRankingResponse) response).getRanking().get(userID));
            }
            else {
                //means that session id expired
                startValidateActivity();
            }
        }
        if (response instanceof GetQuizResponse) {
            GetQuizResponse getQuizResponse = (GetQuizResponse) response;
            if(getQuizResponse.getQuiz() != null) {
                dbquiz = new UserQuizDBHandler(this);
                dbquiz.insertNameandMonumentName(userID, getQuizResponse.getQuiz().getMonumentName(),
                        getQuizResponse.getQuiz().getQuestions());
                startQuizActivity(getQuizResponse.getQuiz());
            }
            else {
                //means that session id expired
                MonumentsListContent.deleteMonuments();
                startValidateActivity();
            }
        }
    }

    @Override
    public void onListFragmentInteraction(MonumentItem item) {
        if(!item.answered){
            if (peersInRange.contains(item.monumentID)){
                new ClientSocket(this, new GetQuizCommand(item.content, sessionID, userID), userID, null).execute();
            }
            else{
                Toast.makeText(this, "Monument too far.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Intent intent = new Intent(this, HistoryActivity.class);
            intent.putExtra("userName", userID);
            intent.putExtra("monumentName", item.content);
            this.startActivity(intent);
        }
    }


    @Override
    public void onListFragmentInteraction(RankingItem item) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_EXIT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String quizName = data.getStringExtra("quizName");

                for (MonumentItem item : MonumentsListContent.getITEMS()) {
                    if(item.content.equals(quizName)){
                        item.answered = true;
                    }
                }
            }
        }
    }

    private void startValidateActivity() {
        Intent intent = new Intent(this, ValidateActivity.class);
        this.startActivity(intent);
        finish();
    }

    private void startQuizActivity(Quiz quiz) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("quiz", quiz);
        intent.putExtra("userID", userID);
        intent.putExtra("quizName", quiz.getMonumentName());
        intent.putExtra("sessionID", sessionID);
        this.startActivityForResult(intent, REQUEST_EXIT);
    }

    private void startRankingFragment() {
        RankingFragment newFragment = new RankingFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void startMonumentsFragment() {
        MonumentsFragment newFragment = new MonumentsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
