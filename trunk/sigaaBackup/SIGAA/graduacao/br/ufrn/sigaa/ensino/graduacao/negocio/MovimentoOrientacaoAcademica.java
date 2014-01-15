/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jul 10, 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;

/**
 *
 * @author Victor Hugo
 *
 */
public class MovimentoOrientacaoAcademica extends AbstractMovimentoAdapter {

	/** usado para cadastrar muitas orienta��es de uma vez */
	private Collection<OrientacaoAcademica> orientacoes;

	/** usado para desativar uma orienta��o espec�fica */
	private OrientacaoAcademica orientacao;

	public OrientacaoAcademica getOrientacao() {
		return orientacao;
	}

	public void setOrientacao(OrientacaoAcademica orientacao) {
		this.orientacao = orientacao;
	}

	public Collection<OrientacaoAcademica> getOrientacoes() {
		return orientacoes;
	}

	public void setOrientacoes(Collection<OrientacaoAcademica> orientacoes) {
		this.orientacoes = orientacoes;
	}

}
