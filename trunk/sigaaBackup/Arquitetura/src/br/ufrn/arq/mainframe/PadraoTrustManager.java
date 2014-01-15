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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * @author johnnycms
 *
 */
public class PadraoTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {		
	}
	
	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
			throws CertificateException {
	}
	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}
