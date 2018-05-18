package pt.ulisboa.tecnico.cmu.communication.response;

import java.security.PublicKey;

public class HelloResponse implements Response {

	private static final long serialVersionUID = 734457624276534179L;
	private PublicKey serverPubKey;

	public HelloResponse(PublicKey serverPubKey) {
		this.serverPubKey = serverPubKey;
	}

	public PublicKey getServerPubKey() {
		return this.serverPubKey;
	}
}
