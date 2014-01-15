/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/05/2012
 * Autor:     Johnny Marçal
 *
 */
package br.ufrn.arq.mainframe;

import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

/**
 * @author johnnycms
 *
 */
public class SSLHelper {

	public static boolean ignorarCerificado(String urlCerificadoIgnorar) {

		try {
			SSLContext contextoSsl = SSLContext.getInstance("TLS");
			
			contextoSsl.init(new KeyManager[0],	new TrustManager[] { new PadraoTrustManager() }, new SecureRandom()); 
			SSLContext.setDefault(contextoSsl);

			URL url = new URL(urlCerificadoIgnorar);
			HttpsURLConnection conexao = (HttpsURLConnection) url.openConnection();
			
			conexao.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});

			conexao.disconnect();
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} 

	}
	
}
