/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 12/02/2010
 *
 */
package br.ufrn.arq.tasks;

import java.io.IOException;
import java.util.Properties;

/**
 * Classe para timer de leitura de arquivos.
 * 
 * @author Marcus Gonçalves
 * @author Gleydson Lima
 *
 */
public abstract class TimerLeituraArquivo extends TarefaTimer {

	/**
	 * Retorna a localização dos arquivos a serem importados.
	 * @return
	 */
	public String getLocation() {

		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getResourceAsStream("/br/ufrn/sipac/siafi/siafi.properties"));

			return prop.getProperty("location");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retorna a localização onde os arquivos importados serão arquivados.
	 * @return
	 */
	public String getArchive() {

		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getResourceAsStream("/br/ufrn/sipac/siafi/siafi.properties"));

			return prop.getProperty("archive");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}