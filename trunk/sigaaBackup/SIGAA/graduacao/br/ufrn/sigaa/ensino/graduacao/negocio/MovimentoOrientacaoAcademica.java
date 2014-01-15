/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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

	/** usado para cadastrar muitas orientações de uma vez */
	private Collection<OrientacaoAcademica> orientacoes;

	/** usado para desativar uma orientação específica */
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
