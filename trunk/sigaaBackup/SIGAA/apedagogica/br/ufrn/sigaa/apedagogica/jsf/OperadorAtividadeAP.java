package br.ufrn.sigaa.apedagogica.jsf;

import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;

/**
 * Interface que define as operações comuns para os managed beans que utilizam a consulta
 * de atividades.
 * @author Mário Rizzi
 *
 */
public interface OperadorAtividadeAP {
	
	/**
	 * Define o objeto populado com a atividade nos MBeans que implementares essa interface
	 * @param pessoa
	 */
	public String selecionaAtividade();
	
	/**
	 * Define a atividade para o uso nos demais métodos dessa interface quando implementados.
	 * @param participante
	 */
	public void setAtividade(AtividadeAtualizacaoPedagogica atividade);
}
