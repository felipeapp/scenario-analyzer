 /*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Aug 22, 2007
 *
 */
package br.ufrn.sigaa.arq.seguranca;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Entidade que configura o padr�o de URL e os pap�is
 * @author Gleydson
 *
 */
public class URLAcesso {

	private String padrao;

	private int[] papeis;


	public URLAcesso(String padrao, int[] papeis) {
		this.padrao = padrao;
		this.papeis = papeis;
	}


	/**
	 * Verifica se o usu�rio tem permiss�o de acesso a URL
	 * @param requestedURL
	 * @param user
	 * @throws SegurancaException
	 */
	public void verificaPermissao(String requestedURL, Usuario user ) throws SegurancaException {

		if ( requestedURL.startsWith(padrao) ) {
			if ( !user.isUserInRole(papeis) ) {
				throw new SegurancaException("Acesso Negado para esta URL");
			}
		}

	}

}
