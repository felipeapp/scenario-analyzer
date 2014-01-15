package br.ufrn.arq.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Classe utilitária para manipulação de Input Streams.
 * 
 * @author Raphael Medeiros
 *
 */
public class InputStreamUtils {

	/**
	 * Método que retorna duas cópias de um único InputStream em um array.
	 *   
	 * @param is
	 * @return
	 */
	public static InputStream[] get2Copies(InputStream is) {
		InputStream[] inputStreams = new InputStream[2];
		
		InputStream _is = is;
		ByteArrayOutputStream _copy1 = new ByteArrayOutputStream();
		ByteArrayOutputStream _copy2 = new ByteArrayOutputStream();
		
		try {
			int read = 0;
			int chunk = 0;
			byte[] data = new byte[256];
			
			while (-1 != (chunk = _is.read(data))) {
				read += data.length;
				_copy1.write(data, 0, chunk);
				_copy2.write(data, 0, chunk);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		inputStreams[0] = ((InputStream) new ByteArrayInputStream(_copy1.toByteArray()));
		inputStreams[1] = ((InputStream) new ByteArrayInputStream(_copy2.toByteArray()));
		
		return inputStreams;
	}
	
	/**
	 * Método que permite criar N cópias de um InputStream
	 * 
	 * @param is
	 * @param n
	 * @return
	 */
	public static InputStream[] getNCopies(InputStream is, int n) {
		InputStream[] inputStreams = new InputStream[n];
		
		InputStream _is = is;
		ByteArrayOutputStream[] _copy = new ByteArrayOutputStream[n];
		
		// inicializando array
		for (int i = 0; i < n; i++) {
			_copy[i] = new ByteArrayOutputStream();
		}
		
		try {
			int read = 0;
			int chunk = 0;
			byte[] data = new byte[256];
			
			while (-1 != (chunk = _is.read(data))) {
				read += data.length;
				
				for (int i = 0; i < n; i++) {
					_copy[i].write(data, 0, chunk);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < n; i++) {
			inputStreams[i] = ((InputStream) new ByteArrayInputStream(_copy[i].toByteArray()));
		}
		
		return inputStreams;
	}
}
