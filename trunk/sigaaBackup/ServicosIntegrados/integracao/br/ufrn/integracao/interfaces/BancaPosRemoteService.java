/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 18/02/2010
 *
 */
package br.ufrn.integracao.interfaces;

import java.util.Date;

import javax.jws.WebService;

/**
 * Interface para comunica��o entre SIPAC e SIGAA.
 * @author Arlindo Rodrigues
 */
@WebService
public interface BancaPosRemoteService {

	/**
	 * Verifica se o Membro passado por par�metro est� cadastrado em alguma banca no per�odo informado.
	 */
	public boolean findBancabyMembro(final Long cpf, Date dataInicio, Date dataFim);	
}
