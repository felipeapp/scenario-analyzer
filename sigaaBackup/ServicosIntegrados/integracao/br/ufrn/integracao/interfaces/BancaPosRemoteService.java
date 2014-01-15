/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/02/2010
 *
 */
package br.ufrn.integracao.interfaces;

import java.util.Date;

import javax.jws.WebService;

/**
 * Interface para comunicação entre SIPAC e SIGAA.
 * @author Arlindo Rodrigues
 */
@WebService
public interface BancaPosRemoteService {

	/**
	 * Verifica se o Membro passado por parâmetro está cadastrado em alguma banca no período informado.
	 */
	public boolean findBancabyMembro(final Long cpf, Date dataInicio, Date dataFim);	
}
