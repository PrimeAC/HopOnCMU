package pt.ulisboa.tecnico.cmu.communication.sealed;

import java.io.IOException;
import java.io.Serializable;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

import pt.ulisboa.tecnico.cmu.security.SecurityManager;
import pt.ulisboa.tecnico.cmu.util.SerializationUtils;

/**
 * Created by afonsocaetano on 14/05/2018.
 */

public class SealedMessage {

    private byte[] digest;
    private SealedObject sealedObject;

    public SealedMessage(Cipher cipher, Serializable object) {
        try{
            this.sealedObject = new SealedObject(object, cipher);
            this.digest = SecurityManager.sign("PrivateKey",
                    SerializationUtils.serializeObject(object));

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
}
