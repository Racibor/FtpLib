package lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

public class ControlSocket {
	private int port;
	private InetAddress IP;
	private LoggerImpl logger;
	private ArrayList<FTPFile> files;
	private Socket controlSocket;
	private DataSocket dataSocket;
	private int dataPort;
	private PrintWriter pw;
	private BufferedReader sc;
	private Scanner dataReader;
	
	ControlSocket(Socket socket) {
		logger = new LoggerImpl();
		controlSocket = socket;
		IP = socket.getInetAddress();
		port = socket.getPort();
		try {
		createStreams();
		String welcome;
			while((welcome = sc.readLine()).contains("-")) {
				logger.log(welcome);
			}
		logger.log(welcome);
		welcome = null;
		} catch(IOException e) {
			logger.log("couldn't create streams");
		}
	}
	
	private void createStreams() throws IOException {
		pw = new PrintWriter(new OutputStreamWriter(controlSocket.getOutputStream()), true);
		sc = new BufferedReader(new InputStreamReader(controlSocket.getInputStream()));
	}
	
	public boolean login(String username) throws IOException {
		send("USER " + username);
		logger.log(sc.readLine());
		return true;
	}
	
	public void login(String username, String password) throws IOException {
		send("USER " + username);
		logger.log(sc.readLine());
		send("PASS " + password);
		logger.log(sc.readLine());
	}
	
	public DataSocket createActiveDataSocket() {
		return new DataActiveSocket(this.IP, (14*256)+dataPort);
	}
	
	public void setDataPort(InetAddress ip, int dataPort) throws IOException {
		this.dataPort = dataPort;
		byte[] hostBytes = controlSocket.getInetAddress().getAddress();
		send("PORT " + hostBytes[0] + "," + hostBytes[1] + "," + hostBytes[2] + "," + hostBytes[3] + ",14," + dataPort);
		logger.log(sc.readLine());
	}
	
	public String pwd() throws IOException {
		send("PWD");
		return sc.readLine();
	}
	
	public ArrayList<FTPFile> ls() {
		String helper;
		files = new ArrayList<FTPFile>();
		try {
		setDataPort(this.IP, PortGenerator.generatePort());
		} catch(IOException e) {
			logger.log("error while reading from Socket buffer");
		}
		send("MLSD");
		try {
			dataSocket = createActiveDataSocket();
			dataReader = new Scanner(dataSocket.getInputStream());
			while(dataReader.hasNextLine()) {
				helper = dataReader.nextLine();
				if(helper.substring(5, 8).equals("dir")) {
				files.add(new FTPFile(helper.substring(helper.indexOf(" ", 18)+1) , true));
				logger.log("added: " + helper);
				} else {
					files.add(new FTPFile(helper.substring(helper.indexOf(" ", 18)+1) , false));
				logger.log("added: " + helper);
				}
			}
			dataReader.close();
			dataSocket.close();
			dataReader = null;
			dataSocket = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return files;
	}
	
	public void send(String msg) {
		pw.println(msg);
	}
	
	public void close() throws IOException {
		if (controlSocket !=null) {
			pw.close();
			sc.close();
			controlSocket.close();
		}
	}
	
	public LoggerImpl getLogger() {
		return logger;
	}
}
