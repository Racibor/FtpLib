package lib;

import java.util.Stack;

public class LoggerImpl implements Logger {
	Stack<String> loggerStack = new Stack<String>();
	public void log(String msg) {
		loggerStack.add(msg);
	}

}
