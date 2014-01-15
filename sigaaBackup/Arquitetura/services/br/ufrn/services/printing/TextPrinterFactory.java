/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/06/2008
 */
package br.ufrn.services.printing;

import java.io.IOException;

import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;

/**
 * Factory para criar um TextPrinter.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class TextPrinterFactory {

	private static TextPrinter printer;
	
	private TextPrinterFactory() {
		
	}

	/**
	 * Retorna uma instancia de TextPrinter.
	 */
	public static TextPrinter getPrinter() throws NoSuchPortException, PortInUseException, IOException {
		if (printer == null)
			printer = new TextPrinter();
		return printer;
	}

	/**
	 * Muda a instância padrão da fábrica. Método útil 
	 * para a realização de testes.
	 */
	public static void setPrinter(TextPrinter _printer) {
		printer = _printer;
	}
	
}
