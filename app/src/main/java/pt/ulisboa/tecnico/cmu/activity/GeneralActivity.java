package pt.ulisboa.tecnico.cmu.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;

import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.termite.SimWifiP2pBroadcastReceiver;

public abstract class GeneralActivity extends AppCompatActivity
        implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener {


    protected static SimWifiP2pManager mManager = null;
    protected static SimWifiP2pManager.Channel mChannel = null;
    protected static Messenger mService = null;

    protected SimWifiP2pBroadcastReceiver mReceiver;
    protected static List<String> peersInRange = new ArrayList<>();
    protected static List<String> peersInGroup = new ArrayList<>();

    public abstract void updateInterface(Response response);

    protected ServiceConnection mConnection = new ServiceConnection() {
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
        peersInRange = new ArrayList<>();
        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            peersInRange.add(device.deviceName);
        }

    }

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList devices,
                                     SimWifiP2pInfo groupInfo) {

        peersInGroup = new ArrayList<>();
        // compile list of network members
        for (String deviceName : groupInfo.getDevicesInNetwork()) {
            SimWifiP2pDevice device = devices.getByName(deviceName);
            if (device != null){
                peersInGroup.add(device.getVirtIp());
            }
        }
    }


}
