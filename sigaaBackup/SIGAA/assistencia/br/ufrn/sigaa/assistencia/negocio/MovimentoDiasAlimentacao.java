/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/06/2008
 *
 */	
package br.ufrn.sigaa.assistencia.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.assistencia.restaurante.dominio.DiasAlimentacao;

/**
 * Movimento para Dias de Alimentação 
 * 
 * @author agostinho
 *
 */

public class MovimentoDiasAlimentacao extends AbstractMovimentoAdapter {

	private List<DiasAlimentacao> diasAlimentacao;

	public void setDiasAlimentacao(List<DiasAlimentacao> diasAlimentacao) {
		this.diasAlimentacao = diasAlimentacao;
	}

	public List<DiasAlimentacao> getDiasAlimentacao() {
		return diasAlimentacao;
	}
}
