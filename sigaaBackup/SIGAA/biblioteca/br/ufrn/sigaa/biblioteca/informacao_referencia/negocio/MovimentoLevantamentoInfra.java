/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/11/2010
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.LevantamentoInfraEstrutura;

/**
 * Movimento para acesso ao processador de a��es relacionadas ao Levantamento
 * de Infra-Estrutura.
 *
 * @author Br�ulio
 */
public class MovimentoLevantamentoInfra extends AbstractMovimento {

	/** Id do movimento */
	private int id;
	
	/** Levantamento que ser� cadastrado / alterado. */
	private LevantamentoInfraEstrutura levInfra;
	
	public MovimentoLevantamentoInfra( Comando comando,
			LevantamentoInfraEstrutura levantamentoInfra ) {
		super();
		this.setCodMovimento(comando);
		this.levInfra = levantamentoInfra;
	}
	
	/////// GETs e SETs ///////
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public LevantamentoInfraEstrutura getLevInfra() {
		return levInfra;
	}

	public void setLevInfra(LevantamentoInfraEstrutura levInfra) {
		this.levInfra = levInfra;
	}

}
