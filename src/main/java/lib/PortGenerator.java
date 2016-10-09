package lib;

import java.util.Random;

public class PortGenerator {
		static Random generator = new Random();
		public static int generatePort() {
			return generator.nextInt(170);
		}
}
