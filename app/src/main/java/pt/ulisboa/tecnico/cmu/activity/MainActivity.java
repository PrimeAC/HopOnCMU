package pt.ulisboa.tecnico.cmu.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.GroupInfoListener;
import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
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
import pt.ulisboa.tecnico.cmu.fragment.monuments.MonumentsListContent;
import pt.ulisboa.tecnico.cmu.fragment.monuments.MonumentsListContent.MonumentItem;
import pt.ulisboa.tecnico.cmu.fragment.RankingFragment;
import pt.ulisboa.tecnico.cmu.fragment.ranking.RankingListContent;
import pt.ulisboa.tecnico.cmu.fragment.ranking.RankingListContent.RankingItem;

import pt.ulisboa.tecnico.cmu.R;
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

    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private SimWifiP2pSocket mCliSocket = null;
    private TextView mTextInput;
    private TextView mTextOutput;
    private SimWifiP2pBroadcastReceiver mReceiver;
    private List<String> peersInRange = new ArrayList<>();

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

        // spawn the chat server background task
        new IncommingCommTask().executeOnExecutor(
                AsyncTask.THREAD_POOL_EXECUTOR);

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
            Log.i("##77777777##", "--------------------- " + sessionID);
        }

        new ClientSocket(this, new GetRankingCommand(sessionID, userID)).execute();

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
                new ClientSocket(this, new GetRankingCommand(sessionID, userID)).execute();
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
                new ClientSocket(this, new GetQuizCommand(item.content, sessionID, userID)).execute();
            }
            else{
                Toast.makeText(this, "Monument too far.", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            //TODO: implement the answered quiz to display to user
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

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mReceiver.setService(mManager, mChannel);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
        }
    };

    /*
	 * Listeners associated to Termite
	 */

    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        //StringBuilder peersStr = new StringBuilder();

        System.out.println("entrei no on peers available *************************************************");
        peersInRange = new ArrayList<>();
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            //String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            peersInRange.add(device.deviceName);
            //peersStr.append(devstr);
        }

        // display list of devices in range
        /*new AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Range")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();*/

    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {

        // compile list of network members
        StringBuilder peersStr = new StringBuilder();
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            String devstr = "" + deviceName + " (" +
                    ((device == null)?"??":device.getVirtIp()) + ")\n";
            peersStr.append(devstr);
        }

        // display list of network members
        new AlertDialog.Builder(this)
                .setTitle("Devices in WiFi Network")
                .setMessage(peersStr.toString())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
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
            mTextOutput.append(values[0] + "\n");
        }
    }

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            mTextOutput.setText("Connecting...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mCliSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt(getString(R.string.port)));
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                mTextOutput.setText(result);
            } else {
                mTextInput.setHint("");
                mTextInput.setText("");
                mTextOutput.setText("");
            }
        }
    }

    public class SendCommTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... msg) {
            try {
                mCliSocket.getOutputStream().write((msg[0] + "\n").getBytes());
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

        @Override
        protected void onPostExecute(Void result) {
            mTextInput.setText("");
        }
    }

}
