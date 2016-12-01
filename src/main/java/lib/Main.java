package lib;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;

public class Main {
	public static void main(String args[]) {
		try {
			ArrayList<FTPFile> files = new ArrayList<FTPFile>();
			Stack<String> stack = new Stack<String>();
			FTPClient ftp = new FTPClient("127.0.0.1", 21);
			ftp.setUsername("Raberr");
			ftp.setPassword("Mutand123");
			ftp.connect();
			ftp.passiveMode();
			ftp.ls();
			// files = ftp.ls();
			// ftp.setBinaryTransferMode();
			// File file = new File("E:/aab.txt");
			// ftp.download("aab.txt", file);
			// System.out.println("-----------------------");
			// for(FTPFile file : files) {
			// System.out.println(file.getName());
			// System.out.println(file.isDir());
			// }

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
