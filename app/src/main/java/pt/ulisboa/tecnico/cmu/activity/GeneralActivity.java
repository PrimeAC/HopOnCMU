package pt.ulisboa.tecnico.cmu.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.ulisboa.tecnico.cmu.R;
import pt.ulisboa.tecnico.cmu.communication.response.Response;

public abstract class GeneralActivity extends AppCompatActivity
        implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    public abstract void updateInterface(Response response);



}
