/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2008
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoApresentacaoResumo;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoResumo;

/**
 * Movimento que encapsula uma coleção de AvaliacaoResumo ou AvaliacaoApresentacaoResumo
 * para cadastro no processador.
 * 
 * @author Leonardo Campos
 *
 */
public class MovimentoAvaliacaoResumo extends MovimentoCadastro {

	private Collection<AvaliacaoResumo> avaliacoes = new ArrayList<AvaliacaoResumo>();
	
	private Collection<AvaliacaoApresentacaoResumo> avaliacoesApresentacao = new ArrayList<AvaliacaoApresentacaoResumo>();
	
	private int numeroAvaliacoes;

	public Collection<AvaliacaoApresentacaoResumo> getAvaliacoesApresentacao() {
		return avaliacoesApresentacao;
	}

	public void setAvaliacoesApresentacao(
			Collection<AvaliacaoApresentacaoResumo> avaliacoesApresentacao) {
		this.avaliacoesApresentacao = avaliacoesApresentacao;
	}

	public Collection<AvaliacaoResumo> getAvaliacoes() {
		return avaliacoes;
	}

	public void setAvaliacoes(Collection<AvaliacaoResumo> avaliacoes) {
		this.avaliacoes = avaliacoes;
	}

	public int getNumeroAvaliacoes() {
		return numeroAvaliacoes;
	}

	public void setNumeroAvaliacoes(int numeroAvaliacoes) {
		this.numeroAvaliacoes = numeroAvaliacoes;
	}
	
}
