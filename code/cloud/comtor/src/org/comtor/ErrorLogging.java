package org.comtor;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ErrorLogging {

	private static Logger l = Logger.getLogger("");

	public static void main(String args[]) throws Exception {
		FileHandler handler = new FileHandler("errorlog.txt");
		l.addHandler(handler);

		l.setLevel(Level.ALL);

		l.info("Error logs");
		try {

		} catch (Error ex) {
			l.log(Level.INFO, "", ex);
		}
		l.fine("");
	}
}
