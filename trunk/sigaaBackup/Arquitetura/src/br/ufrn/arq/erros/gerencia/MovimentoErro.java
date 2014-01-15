/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/12/2008
 */
package br.ufrn.arq.erros.gerencia;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.arq.negocio.ArqListaComando;

/**
 * Representa os dados que são passados para o processador de erros.
 * Ele é construído e chamado pelo ErrorUtils.
 *
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class MovimentoErro extends AbstractMovimentoAdapter {

	public MovimentoErro() {
		setCodMovimento(ArqListaComando.PROCESSAR_ERRO);
	}
	
	private Throwable erro;

	private String details;

	public Throwable getErro() {
		return erro;
	}

	public void setErro(Throwable erro) {
		this.erro = erro;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
