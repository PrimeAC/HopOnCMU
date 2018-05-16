package pt.ulisboa.tecnico.cmu.communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import pt.ulisboa.tecnico.cmu.activity.GeneralActivity;
import pt.ulisboa.tecnico.cmu.communication.command.Command;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.communication.sealed.SealedMessage;
import pt.ulisboa.tecnico.cmu.security.SecurityManager;


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
        Response response = null;
        try {
            server = new Socket("10.0.2.2", 9090);

            ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
            //Cipher command object
            SealedMessage sealedMessage = cipherCommand(command);
            oos.writeObject(sealedMessage);

            ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
            sealedMessage = (SealedMessage) ois.readObject();

            //Test integrity
            if(!SecurityManager.verifyHash(sealedMessage.getDigest(), sealedMessage.getSealedObject())){
               throw new SecurityException("Integrity violated.");
            }
            //Decipher response object
            response = decipherResponse(sealedMessage);

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

    private SealedMessage cipherCommand(Command command){

        if(SecurityManager.keyExists("SessionKey")){
            return new SealedMessage(SecurityManager.getCipher("SessionKey",
                    Cipher.ENCRYPT_MODE), command);
        }else{
            System.out.println("Working Directory = " +
                    System.getProperty("user.dir"));
            return new SealedMessage(SecurityManager.loadPublicKey(""),
                    command);
        }
    }

    private Response decipherResponse(SealedMessage message){
        try{

            if(SecurityManager.keyExists("SessionKey")){
                return (Response) message.getSealedObject().getObject(SecurityManager.getCipher("SessionKey", Cipher.DECRYPT_MODE));

            }else{
                return (Response) message.getSealedObject().getObject(SecurityManager.getCipher("TicketCode", Cipher.DECRYPT_MODE));

            }
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e) {
            throw new SecurityException("Error deciphering message");
        }
    }
}
