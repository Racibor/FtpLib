package lib;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class FTPClient {
	private ControlSocket controlSocket;
	private DataSocket dataSocket;
	private FileWriter fileWriter;
	private FileReader fileReader;
	private InetAddress ip;
	private int port;
	private String username;
	private String password;
	
	FTPClient(String ip, int port) throws UnknownHostException, IOException {
		this.ip = InetAddress.getByName(ip);
		this.port = port;
	}
	
	public void setPort(int port) { 
		this.port = port;
	}
	
	public void setIP(String ip) throws UnknownHostException {
		this.ip = InetAddress.getByName(ip);
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void connect() throws IOException {
		controlSocket = new ControlSocket(new Socket(this.ip, this.port));
		login();
	}
	
	private void login() {
		if(password==null) {
		controlSocket.login(username);
		} else {
			controlSocket.login(username, password);
		}
	}
	
	public FTPFile[] ls() {
		return controlSocket.ls();
	}
	public String pwd() {
		return controlSocket.pwd();
	}
}
