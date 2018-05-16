package pt.ulisboa.tecnico.cmu.util;

import java.io.*;

/**
 * Created by afonsocaetano on 15/05/2018.
 */
public class SerializationUtils {

	public static byte[] serializeObject(Serializable object) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(object);
			out.flush();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return null;
	}

	public static Object desserializeObject(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			return in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return null;
	}
}
