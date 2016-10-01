package lib;

import java.io.IOException;
import java.net.UnknownHostException;

public class Main {
	public static void main(String args[]) {
		try {
			FTPClient ftp = new FTPClient("127.0.0.1", 21);
			ftp.setUsername("Raberr");
			ftp.setPassword("Mutand123");
			ftp.connect();
			ftp.ls();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
