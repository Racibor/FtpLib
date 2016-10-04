package lib;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

public class FTPClient {
	private ControlSocket controlSocket;
	private DataSocket dataSocket;
	private Scanner dataReader;
	private FileWriter fileWriter;
	private FileReader fileReader;
	private InetAddress IP;
	private int port;
	private String username;
	private String password;
	private ArrayList<FTPFile> files;
	
	FTPClient(String ip, int port) throws UnknownHostException, IOException {
		this.IP = InetAddress.getByName(ip);
		this.port = port;
	}
	
	public void setPort(int port) { 
		this.port = port;
	}
	
	public void setIP(String ip) throws UnknownHostException {
		this.IP = InetAddress.getByName(ip);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void connect() throws IOException {
		controlSocket = new ControlSocket(new Socket(this.IP, this.port));
		login();
	}
	
	private void login() throws IOException {
		if(password==null) {
		controlSocket.login(username);
		} else {
			controlSocket.login(username, password);
		}
	}
	
	//Returning list of Files, and saving to and array of FTPFiles
	public ArrayList<FTPFile> ls() {
		String helper;
		files = new ArrayList<FTPFile>();
		try {
		controlSocket.setDataPort(this.IP, PortGenerator.generatePort());
		} catch(IOException e) {
			getLogger().log("error while reading from Socket buffer");
		}
		controlSocket.send("MLSD");
		try {
			dataSocket = controlSocket.createActiveDataSocket();
			dataReader = new Scanner(dataSocket.getInputStream());
			while(dataReader.hasNextLine()) {
				helper = dataReader.nextLine();
				if(helper.substring(5, 8).equals("dir")) {
				files.add(new FTPFile(helper.substring(helper.indexOf(" ", 18)+1) , true));
				getLogger().log("added: " + helper);
				} else {
					files.add(new FTPFile(helper.substring(helper.indexOf(" ", 18)+1) , false));
				getLogger().log("added: " + helper);
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
	
	//TODO make uploading
	public boolean upload(String path) {
		controlSocket.setDataPort(this.IP, PortGenerator.generatePort());
		controlSocket.send(msg);
	}
	
	public String pwd() throws IOException {
		return controlSocket.pwd();
	}
	
	public LoggerImpl getLogger() {
		return controlSocket.getLogger();
	}
	
}
