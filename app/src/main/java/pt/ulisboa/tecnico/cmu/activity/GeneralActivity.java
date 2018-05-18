package pt.ulisboa.tecnico.cmu.activity;

import android.support.v7.app.AppCompatActivity;

import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.ulisboa.tecnico.cmu.communication.response.Response;

public abstract class GeneralActivity extends AppCompatActivity
        implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {

    public abstract void updateInterface(Response response);
}
