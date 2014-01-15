/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/04/2010
 */
package br.ufrn.arq.arquivos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

import br.ufrn.arq.erros.ArqException;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Classe para criar assinaturas digitais em arquivos. 
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class AssinaturaDigitalService {

	/**
	 * Dado um arquivo Pdf como parâmetro, retorna o mesmo Pdf, mas
	 * com assinatura digital.
	 * @param origem
	 * @return
	 * @throws ArqException
	 */
	public static File assinarPdf(File origem) throws ArqException {
		try {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(new FileInputStream("/var/comum/keystore.ks"), "sinfoufrn".toCharArray());
			String alias = ks.aliases().nextElement();
			PrivateKey key = (PrivateKey)ks.getKey(alias, "password".toCharArray());
			
			Certificate[] chain = ks.getCertificateChain(alias);
			PdfReader reader = new PdfReader(origem.getAbsolutePath());
			
			File signed = File.createTempFile("signed", ".pdf");
			FileOutputStream fout = new FileOutputStream(signed);
			PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0');
			
			PdfSignatureAppearance sap = stp.getSignatureAppearance();
			sap.setCrypto(key, chain, null, PdfSignatureAppearance.WINCER_SIGNED);
			sap.setReason("I'm the author");
			sap.setLocation("Lisbon");
			
			// comment next line to have an invisible signature
			//sap.setVisibleSignature(new Rectangle(100, 100, 200, 200), 1, null);
			sap.setCertificationLevel(PdfSignatureAppearance.CERTIFIED_NO_CHANGES_ALLOWED);
			stp.close();
			
			return signed;
		} catch(Exception e) {
			throw new ArqException("Não foi possível criar a assinatura digital no documento.");
		}
	}
	
}
