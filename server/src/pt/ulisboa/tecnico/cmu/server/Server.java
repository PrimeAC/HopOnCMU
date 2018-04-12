package pt.ulisboa.tecnico.cmu.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;

import pt.ulisboa.tecnico.cmu.command.Command;
import pt.ulisboa.tecnico.cmu.response.Response;

public class Server {

	public enum Options {
		A, B, C, D;
	}

	//Monuments:
	//M1 - Torre de Belém
	//M2 - Mosteiro dos Jerónimos
	//M3 - Padrão dos Descobrimentos
	//M4 - Castelo de São Jorge

	private static Question m1_1;
	private static Question m1_2;
	private static Question m1_3;
	private static Question m2_1;
	private static Question m2_2;
	private static Question m2_3;
	private static Question m3_1;
	private static Question m3_2;
	private static Question m3_3;
	private static Question m4_1;
	private static Question m4_2;
	private static Question m4_3;

	private static final int PORT = 9090;

	//keeps a map with: userID:[[answers], [points]]
	private static Map<String, List<String[]>> users = new HashMap<String, List<String[]>>();


	//for test only
	private static Map<String, List<String>> userAnswers = new HashMap<String, List<String>>();
	private static List<String> aux = new ArrayList<String>();

	public static void main(String[] args) throws Exception {
		initializeM1();
		initializeM2();
		initializeM3();
		initializeM4();


		/*
		//for test only
		initializeAux();
		initTest();
		initUsers();
		print();
		compareAnswers(userAnswers);
		print();

		//end of test
		*/


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
		m1_1 = new Question("M1", "Em que ano começou a construção da Torre de Belém?",
				"1514", "1515", "1513", "1512", "1515");
		m1_2 = new Question("M1", "Em que ano foi inaugurada a Torre de Belém?",
				"1514", "1515", "1513", "1519", "1519");
		m1_3 = new Question("M1", "Que outro nome se dá à Torre de Belém?",
				"Torre de São Vicente", "Torre dos Jerónimos", "Torre Francisco de Arruda", "Torre Cultural de Belém", "Torre de São Vicente");
	}

	private static void initializeM2() {
		m2_1 = new Question("M2", "Em que ano começou a construção do Mosteiro dos Jerónimos?",
				"1510", "1505", "1512", "1502","1502");
		m2_2 = new Question("M2", "Em que ano foi inaugurado o Mosteiro dos Jerónimos?",
				"1514", "1515", "1513", "1519", "1513");
		m2_3 = new Question("M2", "Que outro nome se dá ao Mosteiro dos Jerónimos?",
				"Torre de São Vicente", "Torre dos Jerónimos", "Mosteiro de Santa Maria de Belém", "Mosteiro de Belém", "Mosteiro de Santa Maria de Belém");
	}

	private static void initializeM3() {
		m3_1 = new Question("M3", "Em que ano foi inaugurado o Padrão dos Descobrimentos original?",
				"1933", "1940", "1936", "1945", "1940");
		m3_2 = new Question("M3", "Em que ano foi inaugurado o Padrão dos Descobrimentos?",
				"1953", "1955", "1960", "1950", "1960");
		m3_3 = new Question("M3", "Que outro nome se dá ao Padrão dos Descobrimentos?",
				"Monumento aos Navegantes", "Torre dos Descobrimentos", "Padrão de Santa Maria de Belém", "Monumento de Belém", "Monumento aos Navegantes");
	}

	private static void initializeM4() {
		m4_1 = new Question("M4", "Em que século foi definido o nome do Castelo de São Jorge?",
				"XIV", "XV", "XVI", "XVII", "XIV");
		m4_2 = new Question("M4", "Qual o rei que definiu o nome do castelo?",
				"D.João I", "D.Duarte I", "D.João II", "D.Fernando I", "D.João I");
		m4_3 = new Question("M4", "Em que ano foi declarado Monumento Nacional?",
				"1900", "1905", "1910", "1915", "1910");
	}
























/*

	//for test only

	private static void initializeAux() {
		aux.add("1515");
		aux.add("1519");
		aux.add("Torre dos Jerónimos");
		aux.add("bruno");
	}

	private static void initTest(){
		userAnswers.put("M1", aux);
	}

	private static void initUsers(){
		List<String[]> b = new ArrayList<String[]>();
		b.add(new String[] {""});
		b.add(new String[] {"0"});
		users.put("bruno", b);
	}

	private static void print(){
		for (Map.Entry<String, List<String[]>> entry : users.entrySet()) {
			String key = entry.getKey();
			for (String[] value : entry.getValue()) {
				for (String aux : value) {
					System.out.println("VALUE: " + aux);
				}
			}
		}
	}

	*/

}

