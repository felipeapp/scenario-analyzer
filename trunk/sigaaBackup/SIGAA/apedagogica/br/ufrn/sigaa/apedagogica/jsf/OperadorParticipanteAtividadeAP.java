package br.ufrn.sigaa.apedagogica.jsf;

import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;

/**
 * Interface que define as operações comuns para os managed beans que utilizam a consulta
 * de particpantes.
 * @author Mário Rizzi
 *
 */
public interface OperadorParticipanteAtividadeAP {
	
	/**
	 * Define o objeto populado com o particpante nos MBeans que implementares essa interface
	 * @param pessoa
	 */
	public String selecionaParticipante();
	
	/**
	 * Define o participante para o uso nos demais métodos dessa interface quando implementados.
	 * @param participante
	 */
	public void setParticipante(ParticipanteAtividadeAtualizacaoPedagogica participante);

}
