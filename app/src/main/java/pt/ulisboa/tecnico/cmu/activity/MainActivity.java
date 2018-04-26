package pt.ulisboa.tecnico.cmu.activity;

import android.content.Intent;
import android.os.Bundle;
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

import pt.ulisboa.tecnico.cmu.communication.ClientSocket;
import pt.ulisboa.tecnico.cmu.communication.command.GetQuizCommand;
import pt.ulisboa.tecnico.cmu.communication.command.GetRankingCommand;
import pt.ulisboa.tecnico.cmu.communication.response.GetQuizResponse;
import pt.ulisboa.tecnico.cmu.communication.response.GetRankingResponse;
import pt.ulisboa.tecnico.cmu.communication.response.Response;

import pt.ulisboa.tecnico.cmu.fragment.MonumentsFragment;
import pt.ulisboa.tecnico.cmu.fragment.monuments.MonumentsListContent;
import pt.ulisboa.tecnico.cmu.fragment.monuments.MonumentsListContent.MonumentItem;
import pt.ulisboa.tecnico.cmu.fragment.RankingFragment;
import pt.ulisboa.tecnico.cmu.fragment.ranking.RankingListContent;
import pt.ulisboa.tecnico.cmu.fragment.ranking.RankingListContent.RankingItem;

import pt.ulisboa.tecnico.cmu.R;


public class MainActivity extends GeneralActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MonumentsFragment.OnListFragmentInteractionListener,
        RankingFragment.OnListFragmentInteractionListener{

    private boolean rankingPressed = false;
    private String userID;
    private int REQUEST_EXIT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new ClientSocket(this, new GetRankingCommand()).execute();

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
            String data = extras.getString("userID");
            View header = navigationView.getHeaderView(0);
            ((TextView) header.findViewById(R.id.userID)).setText(data);
            userID = ((TextView) header.findViewById(R.id.userID)).getText().toString();
        }


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
            MonumentsFragment newFragment = new MonumentsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_ranking) {
            if(!rankingPressed){
                new ClientSocket(this, new GetRankingCommand()).execute();
            }
            rankingPressed = true;
            RankingFragment newFragment = new RankingFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_logout) {
            rankingPressed = false;
            MonumentsListContent.deleteMonuments();
            Intent intent = new Intent(this, ValidateActivity.class);
            startActivity(intent);
            finish();
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
            RankingListContent.updateRanking(getRankingResponse.getRanking());
        }
        if (response instanceof GetQuizResponse) {
            GetQuizResponse getQuizResponse = (GetQuizResponse) response;
            if(getQuizResponse.getQuiz() != null) {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("quiz", getQuizResponse.getQuiz());
                intent.putExtra("userID", userID);
                intent.putExtra("quizName", getQuizResponse.getQuiz().getMonumentName());
                this.startActivityForResult(intent, REQUEST_EXIT);
            }
        }
    }

    @Override
    public void onListFragmentInteraction(MonumentItem item) {
        if(!item.answered){
            //item.answered = true;
            new ClientSocket(this, new GetQuizCommand(item.content)).execute();
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
            else {
                Log.i("123", "-----------------------------------*********************");
            }
        }
    }

}
