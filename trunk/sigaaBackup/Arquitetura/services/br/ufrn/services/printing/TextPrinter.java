/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 19/06/2008
 */
package br.ufrn.services.printing;

import java.io.IOException;
import java.io.OutputStream;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;

/**
 * Classe utilitária para enviar dados para serem impressos
 * em uma impressora de 40 colunas. Para usar um TextPrinter
 * deve-se utilizar o TextPrinterFactory.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public final class TextPrinter {

	private CommPortIdentifier portId = null;
	
	private ParallelPort parallel = null;

	private static OutputStream output = null;
	
	protected TextPrinter() throws NoSuchPortException, PortInUseException, IOException {
		portId = CommPortIdentifier.getPortIdentifier("LPT1");

		if (portId.getPortType() != CommPortIdentifier.PORT_PARALLEL) {
			System.out.println("Erro: A porta especificada não é uma porta paralela.");
		}
			
		parallel = (ParallelPort) portId.open("PrintTest", 0);
		parallel.setOutputBufferSize(0);
		
		output = parallel.getOutputStream();
	}
	
	/**
	 * Imprime uma mensagem e dá uma quebra de linha.
	 */
	public void printLine(String message) throws IOException {
		printString(message);
		sendLineBreak();
	}
	
	/**
	 * Imprime uma Spring na impressora
	 */
	public void printString(String message) throws IOException {
		output.write(message.getBytes());
	}

	/**
	 * Envia um byte para a impressora
	 */
	public void sendByte(Byte b) throws IOException {
		output.write(b);
	}

	/**
	 * Envia uma quebra de linha (\n) para a impressora
	 */
	public void sendLineBreak() throws IOException {
		output.write(Byte.valueOf("10"));
		output.write(Byte.valueOf("13"));
	}

	/**
	 * Envia várias quebras de linha. 
	 * @param n Número de quebras de linha a serem enviadas
	 */
	public void sendLineBreak(int n) throws IOException {
		for (int i = n; i > 0; i--) {
			sendLineBreak();
		}
	}
	
	/**
	 * Limpa o buffer da porta paralela.
	 */
	public void cleanBuffer() {
		parallel.restart();
	}
}
