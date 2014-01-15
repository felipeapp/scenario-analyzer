package br.ufrn.comum.wireless;

import java.io.PrintStream;
import java.util.Date;

/**
 * Log do serviço Wireless
 * 
 * @author gleydson
 *
 */
public class WASLog {

	static PrintStream out = System.out;
	
	public static void debug(String log) {
		out.println(new Date() + ": " + log);
	}
	
	public static void error(String log) {
		out.println(new Date() + ": " + log);
	}
	
}
