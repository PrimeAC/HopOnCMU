package pt.ulisboa.tecnico.cmu.communication.sealed;

import pt.ulisboa.tecnico.cmu.communication.command.Command;
import pt.ulisboa.tecnico.cmu.communication.command.CommandHandler;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
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

public class SealedCommand implements Command{

	private String userID;
	private byte[] digest;
	private SealedObject sealedObject;
	private SealedObject randomAESKey;

	public SealedCommand(String userID, Cipher cipher, Serializable object) {
		try{
			this.userID = userID;
			this.sealedObject = new SealedObject(object, cipher);
			this.digest = SecurityManager.hashSHA256(SerializationUtils.serializeObject(sealedObject));

		}catch (IOException | IllegalBlockSizeException e){
			e.printStackTrace();
		}
	}

	@Override
	public Response handle(CommandHandler chi) {
		return chi.handle(this);
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

	public SealedObject getRandomAESKey() {
		return randomAESKey;
	}

	public void setRandomAESKey(Cipher cipher, Serializable key) {
		try{
			this.randomAESKey = new SealedObject(key, cipher);
		}catch(IOException | IllegalBlockSizeException e){
			e.printStackTrace();
		}
	}
}
