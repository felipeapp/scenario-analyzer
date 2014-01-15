/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jun 21, 2007
 *
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;

/**
 * Classe que representa uma movimenta��o sobre objetos da classe MatriculaComponente
 * 
 * @author Victor Hugo
 *
 */
public class AproveitamentoMov extends AbstractMovimentoAdapter {

	private Collection<MatriculaComponente> aproveitamentos;

	public Collection<MatriculaComponente> getAproveitamentos() {
		return aproveitamentos;
	}

	public void setAproveitamentos(Collection<MatriculaComponente> aproveitamentos) {
		this.aproveitamentos = aproveitamentos;
	}
	
	
}
