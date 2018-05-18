package pt.ulisboa.tecnico.cmu.communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.crypto.Cipher;

import pt.ulisboa.tecnico.cmu.activity.GeneralActivity;
import pt.ulisboa.tecnico.cmu.communication.command.Command;
import pt.ulisboa.tecnico.cmu.communication.command.GetQuizCommand;
import pt.ulisboa.tecnico.cmu.communication.command.GetRankingCommand;
import pt.ulisboa.tecnico.cmu.communication.command.SignUpCommand;
import pt.ulisboa.tecnico.cmu.communication.command.SubmitQuizCommand;
import pt.ulisboa.tecnico.cmu.communication.command.TicketCommand;
import pt.ulisboa.tecnico.cmu.communication.response.HelloResponse;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.communication.sealed.SealedCommand;
import pt.ulisboa.tecnico.cmu.communication.sealed.SealedResponse;
import pt.ulisboa.tecnico.cmu.security.SecurityManager;


public class ClientSocket extends AsyncTask<Void, Void, Response> {

    private GeneralActivity generalActivity;
    private Command command;
    private String userID;

    public ClientSocket(GeneralActivity generalActivity, Command command, String userID) {
        this.generalActivity = generalActivity;
        this.command = command;
        this.userID = userID;
    }

    @Override
    protected Response doInBackground(Void[] params) {
        Socket server = null;
        Response response = null;
        try {
            server = new Socket("10.0.2.2", 9090);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());

            if(command instanceof TicketCommand || command instanceof SignUpCommand){
                //Send ciphered command
                SealedCommand sealedCommand = new SealedCommand(userID, SecurityManager
                        .getServerPublicKeyCipher(Cipher.ENCRYPT_MODE),command);
                oos.writeObject(sealedCommand);

                //Receive server response
                SealedResponse sealedResponse = (SealedResponse) ois.readObject();
                //Test integrity
                if(!SecurityManager.verifyHash(sealedResponse.getDigest(), sealedResponse.getSealedObject())){
                    throw new SecurityException("Integrity violated.");
                }
                response = (Response) sealedResponse.getSealedObject()
                        .getObject(SecurityManager.getPrivateKeyCipher("KeyPair", Cipher.DECRYPT_MODE));
            }
            else if(command instanceof GetQuizCommand ||
                    command instanceof GetRankingCommand ||
                    command instanceof SubmitQuizCommand){
                SealedCommand sealedCommand = new SealedCommand(userID, SecurityManager
                        .getSecretKeyCipher("SessionKey", Cipher.ENCRYPT_MODE), command);
                oos.writeObject(sealedCommand);

                SealedResponse sealedResponse = (SealedResponse) ois.readObject();
                //Test integrity
                if(!SecurityManager.verifyHash(sealedResponse.getDigest(), sealedResponse.getSealedObject())){
                    throw new SecurityException("Integrity violated.");
                }
                response = (Response) sealedResponse.getSealedObject()
                        .getObject(SecurityManager.getSecretKeyCipher("SessionKey", Cipher.DECRYPT_MODE));
            }
            else{
                oos.writeObject(command);
                HelloResponse helloResponse = (HelloResponse) ois.readObject();
                SecurityManager.serverPubKey = helloResponse.getServerPubKey();
            }

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
        return response;
    }

    @Override
    protected void onPostExecute(Response o) {
        if (o != null) {
            generalActivity.updateInterface(o);
        }
    }
}
