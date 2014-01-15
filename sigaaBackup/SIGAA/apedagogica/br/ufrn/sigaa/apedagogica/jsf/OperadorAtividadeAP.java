package br.ufrn.sigaa.apedagogica.jsf;

import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;

/**
 * Interface que define as opera��es comuns para os managed beans que utilizam a consulta
 * de atividades.
 * @author M�rio Rizzi
 *
 */
public interface OperadorAtividadeAP {
	
	/**
	 * Define o objeto populado com a atividade nos MBeans que implementares essa interface
	 * @param pessoa
	 */
	public String selecionaAtividade();
	
	/**
	 * Define a atividade para o uso nos demais m�todos dessa interface quando implementados.
	 * @param participante
	 */
	public void setAtividade(AtividadeAtualizacaoPedagogica atividade);
}
