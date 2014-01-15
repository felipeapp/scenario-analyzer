/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;

/**
 * Movimento usado na identifica��o/substitui��o de secret�rios
 * 
 * @author leonardo
 *
 */
public class SecretariaUnidadeMov extends AbstractMovimentoAdapter {

	private SecretariaUnidade secretario;
	
	private SecretariaUnidade secretarioAntigo;
	
	public SecretariaUnidadeMov(){
		
	}

	public SecretariaUnidade getSecretario() {
		return secretario;
	}

	public void setSecretario(SecretariaUnidade secretario) {
		this.secretario = secretario;
	}

	public SecretariaUnidade getSecretarioAntigo() {
		return secretarioAntigo;
	}

	public void setSecretarioAntigo(SecretariaUnidade secretarioAntigo) {
		this.secretarioAntigo = secretarioAntigo;
	}
	 
	
}
