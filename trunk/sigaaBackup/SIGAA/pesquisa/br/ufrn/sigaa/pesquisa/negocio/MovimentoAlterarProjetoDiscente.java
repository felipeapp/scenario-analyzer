/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/03/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;

/**
 * Entidade responsável pelo movimento de alteração de um projeto Discente. 
 * 
 * @author Leonardo
 */
public class MovimentoAlterarProjetoDiscente extends AbstractMovimentoAdapter {

	private MembroProjetoDiscente membroDiscente;
	
	/** Construtor Padrão */
	public MovimentoAlterarProjetoDiscente(){
		
	}

	public MembroProjetoDiscente getMembroDiscente() {
		return membroDiscente;
	}

	public void setMembroDiscente(MembroProjetoDiscente membroDiscente) {
		this.membroDiscente = membroDiscente;
	}
	
}
