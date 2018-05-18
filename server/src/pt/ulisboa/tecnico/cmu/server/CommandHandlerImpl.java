package pt.ulisboa.tecnico.cmu.server;


import pt.ulisboa.tecnico.cmu.communication.command.*;
import pt.ulisboa.tecnico.cmu.communication.response.*;
import pt.ulisboa.tecnico.cmu.communication.sealed.SealedCommand;
import pt.ulisboa.tecnico.cmu.communication.sealed.SealedResponse;
import pt.ulisboa.tecnico.cmu.data.Question;
import pt.ulisboa.tecnico.cmu.data.Quiz;
import pt.ulisboa.tecnico.cmu.data.SessionID;
import pt.ulisboa.tecnico.cmu.data.User;
import pt.ulisboa.tecnico.cmu.security.SecurityManager;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Logger;


public class CommandHandlerImpl implements CommandHandler {

    private Logger LOGGER = Logger.getLogger(CommandHandlerImpl.class.getName());

    @Override
    public Response handle(HelloCommand hc) {
        LOGGER.info("Received HELLO command");
        return new HelloResponse(Server.getServerPublicKey());
    }

    @Override
    public Response handle(TicketCommand tc) {
        LOGGER.info("Bilhete recebido " + tc.getTicketCode());
        TicketResponse ticketResponse;
        if (Server.validTicket(tc.getTicketCode())) {
            for (User user : Server.getUsers()) {
                if (user.getTicketCode().equals(tc.getTicketCode())) {
                    //ticket code already used so can login
                    String sessionID;
                    while (true){
                        sessionID = generateSessionID(user.getUserID());

                        if (sessionID != null){
	                        //Set sessionKey
                            break;
                        }
                    }
                    ticketResponse = new TicketResponse("OK",
                            user.getUserID(), getMonuments(user.getUserID()), sessionID);;
                    return new SealedResponse(null, SecurityManager.getCipher(tc.getRandomAESKey(),
                            Cipher.ENCRYPT_MODE, "AES"), ticketResponse);
                }
            }
            //ticket never used (NU), so need to create an account
            ticketResponse = new TicketResponse("NU", null,
                    null,  null);
            return new SealedResponse(null, SecurityManager.getCipher(tc.getRandomAESKey(),
                    Cipher.ENCRYPT_MODE, "AES"), ticketResponse);
        }
        ticketResponse = new TicketResponse("NOK", null,
                null, null);

        return new SealedResponse(null, SecurityManager.getCipher(tc.getRandomAESKey(),
                Cipher.ENCRYPT_MODE, "AES"), ticketResponse);
    }

    @Override
    public Response handle(SignUpCommand suc) {
        LOGGER.info("Bilhete recebido " + suc.getTicketCode() + " user: " + suc.getUserID());
        SignUpResponse signUpResponse;
        for (User user : Server.getUsers()) {
            if (user.getUserID().equals(suc.getUserID())) {
                //ticket userID already used
                signUpResponse = new SignUpResponse("NOK", suc.getUserID(), null, null);
                return new SealedResponse(null, SecurityManager.getCipher(suc.getRandomAESKey(),
                        Cipher.ENCRYPT_MODE, "AES"), signUpResponse);
            }
        }
        User user = new User(suc.getUserID(), suc.getTicketCode(), 0);
        Server.getUsers().add(user);
        String sessionID;
        while (true){
            sessionID = generateSessionID(user.getUserID());
            if (sessionID != null){
	            //Set sessionKey
                break;
            }
        }
        signUpResponse = new SignUpResponse("OK", user.getUserID(), getMonuments(user.getUserID()), sessionID);
        return new SealedResponse(suc.getUserID(), SecurityManager.getCipher(suc.getRandomAESKey(),
                Cipher.ENCRYPT_MODE, "AES"), signUpResponse);
    }

    @Override
    public Response handle(GetQuizCommand gqc) {
        LOGGER.info("Quiz " + gqc.getMonumentName());
        GetQuizResponse getQuizResponse;
        if (validateSessionID(gqc.getSessionID(), gqc.getUserID())) {
            for (Quiz quiz : Server.getQuizzes()) {
                if (quiz.getMonumentName().equals(gqc.getMonumentName())) {
                    //put the current time on the associated client
                    initializeClientTimer(quiz, gqc);
	                getQuizResponse = new GetQuizResponse(quiz);
	                return new SealedResponse(gqc.getUserID(), SecurityManager.getCipher(SecurityManager
			                .getKeyFromString(Server.getSessionID().get(gqc.getUserID()).getSessionID()),
			                Cipher.ENCRYPT_MODE,"AES"),getQuizResponse);
                }
            }
        }
	    getQuizResponse = new GetQuizResponse(null);
	    return new SealedResponse(gqc.getUserID(), SecurityManager.getCipher(SecurityManager
					    .getKeyFromString(Server.getSessionID().get(gqc.getUserID()).getSessionID()),
			    Cipher.ENCRYPT_MODE,"AES"),getQuizResponse);
    }

    @Override
    public Response handle(GetRankingCommand grc) {
        LOGGER.info("recebi um get ranking " + grc.getSessionID() + " //// " + grc.getUserID());
	    GetRankingResponse getRankingResponse;
        if (validateSessionID(grc.getSessionID(), grc.getUserID())) {
            Map<String, Integer> unsortRanking = new HashMap<>();
            for (User user : Server.getUsers()) {
                unsortRanking.put(user.getUserID(), Math.round(user.getScore()));
            }
	        getRankingResponse = new GetRankingResponse(Server.sortByScore(unsortRanking));
	        return new SealedResponse(grc.getUserID(), SecurityManager.getCipher(SecurityManager
					        .getKeyFromString(Server.getSessionID().get(grc.getUserID()).getSessionID()),
			        Cipher.ENCRYPT_MODE,"AES"),getRankingResponse);
        }
	    getRankingResponse = new GetRankingResponse(null);
	    return new SealedResponse(grc.getUserID(), SecurityManager.getCipher(SecurityManager
					    .getKeyFromString(Server.getSessionID().get(grc.getUserID()).getSessionID()),
			    Cipher.ENCRYPT_MODE,"AES"),getRankingResponse);
    }

    @Override
    public Response handle(SubmitQuizCommand sqc) {
        LOGGER.info("Recebi as respostas ao quiz " + sqc.getAnswers().get(0) + sqc.getAnswers().get(1) + sqc.getAnswers().get(2));
        SubmitQuizResponse submitQuizResponse;
        if (validateSessionID(sqc.getSessionID(), sqc.getUserID())) {
            Quiz quiz = Server.getQuiz(sqc.getQuizName());
            List<Question> questions = quiz.getQuestions();
            int cnt = 0;
            int score = 0;
            if(questions != null){
                for (Question question: questions) {
                    if(sqc.getAnswers().get(cnt).equals(question.getSolution())){
                        //need to increase the user score by one
                        score++;
                    }
                    cnt++;
                }
                //compare the get quiz time and the current time and update the score accordingly
                float diff = calculateQuizTime(quiz, sqc);
                Server.updateUserScore(sqc.getUserID(), Math.round(score*1000/diff), sqc.getQuizName());
                submitQuizResponse = new SubmitQuizResponse("OK");
	            return new SealedResponse(sqc.getUserID(), SecurityManager.getCipher(SecurityManager
					            .getKeyFromString(Server.getSessionID().get(sqc.getUserID()).getSessionID()),
			            Cipher.ENCRYPT_MODE,"AES"),submitQuizResponse);
            }
        }
        submitQuizResponse = new SubmitQuizResponse("NOK");
	    return new SealedResponse(sqc.getUserID(), SecurityManager.getCipher(SecurityManager
					    .getKeyFromString(Server.getSessionID().get(sqc.getUserID()).getSessionID()),
			    Cipher.ENCRYPT_MODE,"AES"),submitQuizResponse);
    }

    @Override
    public Response handle(SealedCommand sm) {
        CommandHandlerImpl cmi = new CommandHandlerImpl();
        Command cmd;
        //Test integrity
        if(!SecurityManager.verifyHash(sm.getDigest(), sm.getSealedObject())){
            throw new SecurityException("Integrity violated.");
        }
        try {
	        if (sm.getUserID() == null) {
	        	//Get RandomAESKey
		        SecretKey randomAESKey = (SecretKey) sm.getRandomAESKey().getObject(SecurityManager.getCipher(Server.getServerPrivateKey(),
				        Cipher.DECRYPT_MODE, "RSA/ECB/PKCS1Padding"));
		        //Use Server private key
		        cmd = (Command) sm.getSealedObject().getObject(SecurityManager.getCipher(randomAESKey,
				        Cipher.DECRYPT_MODE, "AES"));
	        } else {
		        //Use Client session key
		        SecretKey sessionKey = SecurityManager.getKeyFromString(Server.getSessionID()
				        .get(sm.getUserID()).getSessionID());
		        cmd = (Command) sm.getSealedObject().getObject(SecurityManager.getCipher(sessionKey,
				        Cipher.DECRYPT_MODE, "AES"));
	        }
        }catch(IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException e){
        	e.printStackTrace();
        	throw new SecurityException("Error unciphering message");
        }
        return cmd.handle(cmi);
    }

    private List<String> getMonuments(String userID) {
        List<String> monumentsNames = new ArrayList<>();
        for (Quiz quiz : Server.getQuizzes()) {
            if(quiz.getUserScore().containsKey(userID)){
                monumentsNames.add(quiz.getMonumentName() + "|T");
            }
            else {
                monumentsNames.add(quiz.getMonumentName() + "|F");
            }
        }
        return monumentsNames;
    }

    private String generateSessionID(String userID) {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //generate a random number
            String sessionID = Integer.toString(secureRandom.nextInt());
            if (notUsedSessionID(sessionID, userID)){
                Server.updateSessionID(userID, new SessionID(sessionID, new Date()));
                return sessionID;
            }
        } catch (NoSuchAlgorithmException nae){
            nae.getMessage();
        }
        return null;
    }

    private boolean notUsedSessionID(String sessionID, String userID) {
        if (Server.getSessionID().containsKey(userID)){
            SessionID session = Server.getSessionID().get(userID);
            return !session.getSessionID().equals(sessionID);
        }
        return true;
    }

    private boolean validateSessionID(String sessionID, String userID) {
        if (Server.getSessionID().containsKey(userID)){
            SessionID session = Server.getSessionID().get(userID);
            if (session.getSessionID().equals(sessionID)){
                long diff = subtractTimes(session.getGeneratedTime(), new Date());
                System.out.println(diff);
                // 300000 miliseconds equals 5 minutes of a session
                if (diff < 300000) {
                    session.setGeneratedTime(new Date());
                    return true;
                }
                else {
                    Server.removeSession(userID);
                    return false;
                }
            }
        }
        return false;
    }

    private void initializeClientTimer(Quiz quiz, GetQuizCommand gqc) {
        Map<String, Date> quizTime = quiz.getUserTime();
        quizTime.put(gqc.getUserID(), new Date());
        quiz.setUserTime(quizTime);
    }

    private long subtractTimes(Date start, Date end) {
        return Math.abs(end.getTime() - start.getTime());
    }

    private float calculateQuizTime(Quiz quiz, SubmitQuizCommand sqc) {
        Map<String, Date> userTime = quiz.getUserTime();
        long diff = subtractTimes(userTime.get(sqc.getUserID()), new Date());
        return convertToSeconds(diff);
    }

    private float convertToSeconds(long time) {
        return time/1000;
    }
}
