package lib;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Stack;

public class Main {
	public static void main(String args[]) {
		try {
			LoggerImpl log;
			Stack<String> stack = new Stack<String>();
			FTPClient ftp = new FTPClient("127.0.0.1", 21);
			ftp.setUsername("Raberr");
			ftp.setPassword("Mutand123");
			ftp.connect();
			ftp.ls();
			System.out.println(ftp.pwd());
			System.out.println(ftp.pwd());
			System.out.println(ftp.pwd());
			ftp.ls();
			log = ftp.getLogger();
			stack = log.loggerStack;
			for(String x : stack) {
			System.out.println(x);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
