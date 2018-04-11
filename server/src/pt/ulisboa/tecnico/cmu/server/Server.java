package pt.ulisboa.tecnico.cmu.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmu.command.Command;
import pt.ulisboa.tecnico.cmu.response.Response;

public class Server {

    //Monuments:
	//M1 - Torre de Belém
    //M2 - Mosteiro dos Jerónimos
    //M3 - Padrão dos Descobrimentos
    //M4 - Castelo de São Jorge

	private static final int PORT = 9090;
    private static Map<String, List> quizzes = new HashMap<String, List>();
    private static Map<String, String[]> quizzesAnswers = new HashMap<String, String[]>();
    private static List<String[]> m1Quiz = new ArrayList<String[]>();
    private static List<String[]> m2Quiz = new ArrayList<String[]>();
    private static List<String[]> m3Quiz = new ArrayList<String[]>();
    private static List<String[]> m4Quiz = new ArrayList<String[]>();

	public static void main(String[] args) throws Exception {
	    initializeM1();
	    initializeM2();
	    initializeM3();
	    initializeM4();
	    initializeAnswers();
	    initializeQuiz();

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
			Command cmd =  (Command) ois.readObject();
			Response rsp = cmd.handle(chi);
			
			ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			oos.writeObject(rsp);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (client != null) {
					try { client.close(); }
					catch (Exception e) {}
				}
			}
		}
	}

	private static void initializeM1() {
        m1Quiz.add(new String[] {"Em que ano começou a construção da Torre de Belém?", "1514", "1515", "1513", "1512"});  //1514
        m1Quiz.add(new String[] {"Em que ano foi inaugurada a Torre de Belém?", "1514", "1515", "1513", "1519"});  //1519
        m1Quiz.add(new String[] {"Que outro nome se dá à Torre de Belém?", "Torre de São Vicente", "Torre dos Jerónimos", "Torre Francisco de Arruda", "Torre Cultural de Belém"});  //Torre de sao vicente
    }

    private static void initializeM2() {
        m2Quiz.add(new String[] {"Em que ano começou a construção do Mosteiro dos Jerónimos?", "1510", "1505", "1512", "1502"});  //1502
        m2Quiz.add(new String[] {"Em que ano foi inaugurado o Mosteiro dos Jerónimos?", "1514", "1515", "1513", "1519"});  //1513
        m2Quiz.add(new String[] {"Que outro nome se dá ao Mosteiro dos Jerónimos?", "Torre de São Vicente", "Torre dos Jerónimos", "Mosteiro de Santa Maria de Belém", "Mosteiro de Belém"});  //mosteiro de santa maria
    }

    private static void initializeM3() {
        m3Quiz.add(new String[] {"Em que ano foi inaugurado o Padrão dos Descobrimentos original?", "1933", "1940", "1936", "1945"});  //1940
        m3Quiz.add(new String[] {"Em que ano foi inaugurado o Padrão dos Descobrimentos?", "1953", "1955", "1960", "1950"});  //1960
        m3Quiz.add(new String[] {"Que outro nome se dá ao Padrão dos Descobrimentos?", "Monumento aos Navegantes", "Torre dos Descobrimentos", "Padrão de Santa Maria de Belém", "Monumento de Belém"});  //Monumento aos Navegantes
    }

    private static void initializeM4() {
        m4Quiz.add(new String[] {"Em que século foi definido o nome do Castelo de São Jorge?", "XIV", "XV", "XVI", "XVII"});  //XIV
        m4Quiz.add(new String[] {"Qual o rei que definiu o nome do castelo?", "D.João I", "D.Duarte I", "D.João II", "D.Fernando I"});  //D.João I
        m4Quiz.add(new String[] {"Em que ano foi declarado Monumento Nacional?", "1900", "1905", "1910", "1915"});  //1910
    }


    private static void initializeQuiz() {
        quizzes.put("M1", m1Quiz);
        quizzes.put("M2", m2Quiz);
        quizzes.put("M3", m3Quiz);
        quizzes.put("M4", m4Quiz);
    }

    private static void initializeAnswers(){
	    quizzesAnswers.put("M1", new String[] {"1514", "1519", "Torre de São Vicente"});
        quizzesAnswers.put("M2", new String[] {"1502", "1513", "Mosteiro de Santa Maria de Belém"});
        quizzesAnswers.put("M3", new String[] {"XIV", "D.João I", "1910"});
    }
    
}
