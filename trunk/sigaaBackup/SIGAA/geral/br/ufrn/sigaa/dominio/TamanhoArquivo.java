/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 23/02/2011
 * 
 */
package br.ufrn.sigaa.dominio;

/**
 * Classe para armazenar constantes que definem o tamanho de um arquivo.
 * 
 * @author bernardo
 *
 */
public class TamanhoArquivo {
	
	/** Tamanho de um byte */
	public static final int BYTE = 1;
	/** Tamanho de um kilobyte */
	public static final int KILO_BYTE = BYTE * 1024;
	/** Tamanho de um megabyte, usado para calcular se o arquivo excede o tamanho máximo permitido */
	public static final int MEGA_BYTE = KILO_BYTE * 1024;

}
