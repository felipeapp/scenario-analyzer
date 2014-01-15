/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/06/2008
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;

/**
 * Movimento utilizado para encapsular uma cole��o de avalia��es
 * que ser�o encerradas pelo processador.
 *  
 * @author Leonardo
 *
 */
@SuppressWarnings("serial")
public class MovimentoEncerrarAvaliacoesPendentes extends
		AbstractMovimentoAdapter {

	private Collection<AvaliacaoProjeto> avaliacoesPendentes;
	
	public MovimentoEncerrarAvaliacoesPendentes() {
	}

	public Collection<AvaliacaoProjeto> getAvaliacoesPendentes() {
		return avaliacoesPendentes;
	}

	public void setAvaliacoesPendentes(
			Collection<AvaliacaoProjeto> avaliacoesPendentes) {
		this.avaliacoesPendentes = avaliacoesPendentes;
	}
	
	
}
