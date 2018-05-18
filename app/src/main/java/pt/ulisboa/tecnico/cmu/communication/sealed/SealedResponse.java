package pt.ulisboa.tecnico.cmu.communication.sealed;

import java.io.IOException;
import java.io.Serializable;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.security.SecurityManager;
import pt.ulisboa.tecnico.cmu.util.SerializationUtils;

/**
 * Created by afonsocaetano on 18/05/2018.
 */

public class SealedResponse implements Response {
    private String userID;
    private byte[] digest;
    private SealedObject sealedObject;

    public SealedResponse(String userID, Cipher cipher, Serializable object) {
        try{
            this.userID = userID;
            this.sealedObject = new SealedObject(object, cipher);
            this.digest = SecurityManager.hashSHA256(SerializationUtils.serializeObject(object));

        }catch (IOException | IllegalBlockSizeException e){
            e.printStackTrace();
        }
    }

    public byte[] getDigest() {
        return digest;
    }

    public SealedObject getSealedObject() {
        return sealedObject;
    }

    public String getUserID() {
        return userID;
    }
}
