/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/07/2006
 */
package br.ufrn.arq.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logador em arquivo. Esta classe é usada no caso de algum processo
 * precisar logar em um arquivo.
 *
 * @author Gleydson Lima
 *
 */
public class FileLogger {

	private boolean sysout;

	private static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private PrintWriter out;

	public FileLogger(String arquivo) throws IOException {
		out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(arquivo,true)));
	}

	public FileLogger(String arquivo, boolean sysout) throws IOException {
		out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(arquivo,true)));
		this.sysout = sysout;
	}

	public void log(String str) {
		out.println(df.format(new Date()) + ": " + str);
		out.flush();
	}

	public void exception(Throwable e) {
		e.printStackTrace(out);
	}

	public void info(String str) {
		if ( sysout ) {
			System.out.println(str);
		}
		log(str);
	}

}
