package pt.ulisboa.tecnico.cmu.server;

import pt.ulisboa.tecnico.cmu.communication.command.Command;
import pt.ulisboa.tecnico.cmu.communication.response.Response;
import pt.ulisboa.tecnico.cmu.data.Question;
import pt.ulisboa.tecnico.cmu.data.Quiz;
import pt.ulisboa.tecnico.cmu.data.SessionID;
import pt.ulisboa.tecnico.cmu.data.User;
import pt.ulisboa.tecnico.cmu.security.SecurityManager;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public class Server {

	//Monuments:
	//M1 - Belem Tower
	//M2 - Jeronimos Monastery
	//M3 - Monument of the Discoveries
	//M4 - Sao Jorge Castle

	private static final int PORT = 9090;

	//keeps a list of users
	private static List<User> users = new ArrayList<>();

	//keeps a list of quizzes
	private static List<Quiz> quizzes = new ArrayList<>();

	//keeps a list of valid codes
	private static List<String> tickets = new ArrayList<>();

	//keeps a map of userID: sessionID
	private static Map<String, SessionID> sessionIDs = new HashMap<>();

	//Server keypair
	private static KeyPair keyPair;


	public static void main(String[] args) throws Exception {
		initializeM1();
		initializeM2();
		initializeM3();
		initializeM4();
		initializeTickets();
		keyPair = SecurityManager.generateKeyPair();


		CommandHandlerImpl chi = new CommandHandlerImpl();
		ServerSocket socket = new ServerSocket(PORT);
		Socket client = null;

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Server now closed.");
				try { socket.close(); }
				catch (Exception e) { }
			}
		});

		System.out.println("Server is accepting connections at " + PORT);

		while (true) {
			try {
				client = socket.accept();

				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
				Command cmd = (Command) ois.readObject();
				System.out.println("Received message!");
				Response rsp = cmd.handle(chi);

				ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
				oos.writeObject(rsp);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (client != null) {
					try { client.close(); }
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static void initializeM1() {
		Question m1_1 = new Question("In what year did the construction of the Belem Tower began?",
				"1514", "1515", "1513", "1512", "1515");
		Question m1_2 = new Question("In what year was the Belem Tower inaugurated?",
				"1514", "1515", "1513", "1519", "1519");
		Question m1_3 = new Question("What other name is given to the Belem Tower?",
				"São Vicente Tower", "Jeronimos Tower", "Francisco de Arruda Tower", "Cultural Belem Tower", "São Vicente Tower");
		List<Question> questions = new ArrayList<>();
		questions.add(m1_1);
		questions.add(m1_2);
		questions.add(m1_3);
		Quiz quiz1 = new Quiz("M1", "Belem Tower", questions);
		quizzes.add(quiz1);
	}

	private static void initializeM2() {
		Question m2_1 = new Question("In what year did the construction of the Jeronimos Monastery began?",
				"1510", "1505", "1512", "1502","1502");
		Question m2_2 = new Question("In what year was the Jeronimos Monastery inaugurated?",
				"1514", "1515", "1513", "1519", "1513");
		Question m2_3 = new Question("What other name is given to the Jeronimos Monastery?",
				"São Vicente Tower", "Jeronimos Tower", "Santa Maria de Belem Monastery", "Belem Monastery", "Santa Maria de Belem Monastery");
		List<Question> questions = new ArrayList<>();
		questions.add(m2_1);
		questions.add(m2_2);
		questions.add(m2_3);
		Quiz quiz2 = new Quiz("M2", "Jeronimos Monastery", questions);
		quizzes.add(quiz2);
	}

	private static void initializeM3() {
		Question m3_1 = new Question("In what year was the original Monument to the Discoveries inaugurated?",
				"1933", "1940", "1936", "1945", "1940");
		Question m3_2 = new Question("In what year was the Monument to the Discoveries inaugurated?",
				"1953", "1955", "1960", "1950", "1960");
		Question m3_3 = new Question("What other name is given to the Monument to the Discoveries?",
				"Monument to the Navigators", "Discoveries Tower", "Standard of Santa Maria de Belem", "Belem Monument", "Monument to the Navigators");
		List<Question> questions = new ArrayList<>();
		questions.add(m3_1);
		questions.add(m3_2);
		questions.add(m3_3);
		Quiz quiz3 = new Quiz("M3", "Monument to the Discoveries", questions);
		quizzes.add(quiz3);
	}

	private static void initializeM4() {
		Question m4_1 = new Question("In what century was the name of the Sao Jorge Castle defined?",
				"XIV", "XV", "XVI", "XVII", "XIV");
		Question m4_2 = new Question("What king defined the name of the castle?",
				"D.João I", "D.Duarte I", "D.João II", "D.Fernando I", "D.João I");
		Question m4_3 = new Question("In what year was it declared a National Monument?",
				"1900", "1905", "1910", "1915", "1910");
		List<Question> questions = new ArrayList<>();
		questions.add(m4_1);
		questions.add(m4_2);
		questions.add(m4_3);
		Quiz quiz4 = new Quiz("M4", "Sao Jorge Castle", questions);
		quizzes.add(quiz4);
	}

	private static void initializeTickets(){
		tickets.add("123456");
		tickets.add("654321");
		tickets.add("123123");
		tickets.add("321321");
		tickets.add("111111");
		tickets.add("222222");
		tickets.add("333333");
		tickets.add("444444");
		tickets.add("555555");
		tickets.add("666666");
	}

	public static boolean validTicket(String ticketCode) {
		return tickets.contains(ticketCode);
	}

	public static List<User> getUsers(){
		return users;
	}

	public static List<Quiz> getQuizzes(){
		return quizzes;
	}

	public static Map<String, Integer> sortByScore(Map<String, Integer> unsortedRanking){
		Map<String, Integer> sortedRanking = unsortedRanking.entrySet().stream().sorted(Collections
				.reverseOrder(Map.Entry.comparingByValue())).collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		return sortedRanking;
	}

	public static Quiz getQuiz(String quizName) {
		for (Quiz quiz: quizzes) {
			if(quizName.equals(quiz.getMonumentName())){
				return quiz;
			}
		}
		return null;
	}

	public static void updateUserScore(String userID, int scoreToAdd, String quizName) {
		for (User user: users) {
			if(user.getUserID().equals(userID)){
				int score = Math.round(user.getScore());
				user.setScore(score + scoreToAdd);
				for (Quiz quiz: quizzes) {
					if (quiz.getMonumentName().equals(quizName)){
						Map<String, Integer> userScore = quiz.getUserScore();
                        userScore.put(userID, scoreToAdd);
                        quiz.setUserScore(userScore);
					}
				}
			}
		}
	}


	public static Map<String, SessionID> getSessionID() {
		return sessionIDs;
	}

	public static void updateSessionID(String userID, SessionID sessionID) {
		sessionIDs.put(userID, sessionID);
		System.out.println(sessionIDs.get(userID).getGeneratedTime());
	}

	public static void removeSession(String userID) {
		sessionIDs.remove(userID);
	}

	public static PublicKey getServerPublicKey(){
		return keyPair.getPublic();
	}

	public static PrivateKey getServerPrivateKey(){
		return keyPair.getPrivate();
	}

}

