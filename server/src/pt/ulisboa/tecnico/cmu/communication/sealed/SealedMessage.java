package pt.ulisboa.tecnico.cmu.communication.sealed;

import pt.ulisboa.tecnico.cmu.security.SecurityManager;
import pt.ulisboa.tecnico.cmu.util.SerializationUtils;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by afonsocaetano on 14/05/2018.
 */

public class SealedMessage {

	private byte[] digest;
	private SealedObject sealedObject;

	public SealedMessage(Cipher cipher, Serializable object) {
		try{
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
}
