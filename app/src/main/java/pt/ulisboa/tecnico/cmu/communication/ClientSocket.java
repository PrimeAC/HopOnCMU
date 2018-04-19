package pt.ulisboa.tecnico.cmu.communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pt.ulisboa.tecnico.cmu.activity.GeneralActivity;
import pt.ulisboa.tecnico.cmu.communication.command.Command;
import pt.ulisboa.tecnico.cmu.communication.response.Response;


public class ClientSocket extends AsyncTask<Void, Void, Response> {

    private GeneralActivity generalActivity;
    private Command command;

    public ClientSocket(GeneralActivity generalActivity, Command command) {
        this.generalActivity = generalActivity;
        this.command = command;
    }

    @Override
    protected Response doInBackground(Void[] params) {
        Socket server = null;
        Response reply = null;
        try {
            server = new Socket("10.0.2.2", 9090);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            oos.writeObject(command);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            reply = (Response) ois.readObject();

            oos.close();
            ois.close();
        }
        catch (Exception e) {
            Log.d("Socket", "Socket failed..." + e.getMessage());
            e.printStackTrace();
        } finally {
            if (server != null) {
                try { server.close(); }
                catch (Exception e) { }
            }
        }
        return reply;
    }

    @Override
    protected void onPostExecute(Response o) {
        if (o != null) {
            generalActivity.updateInterface(o);
        }
    }

}
