package pt.ulisboa.tecnico.cmu.communication.command;

import pt.ulisboa.tecnico.cmu.communication.response.Response;

import javax.crypto.SecretKey;

public class SignUpCommand implements Command {

    private static final long serialVersionUID = -8807331723807741905L;
    private String ticketCode;
    private String userID;
    private SecretKey randomAESKey;

    public SignUpCommand(String ticketCode, String userID, SecretKey randomAESKey) {
        this.ticketCode = ticketCode;
        this.userID = userID;
        this.randomAESKey = randomAESKey;
    }

    @Override
    public Response handle(CommandHandler chi) {
        return chi.handle(this);
    }

    public String getTicketCode() {
        return this.ticketCode;
    }

    public String getUserID() {
        return this.userID;
    }

    public SecretKey getRandomAESKey() {
        return randomAESKey;
    }
}
