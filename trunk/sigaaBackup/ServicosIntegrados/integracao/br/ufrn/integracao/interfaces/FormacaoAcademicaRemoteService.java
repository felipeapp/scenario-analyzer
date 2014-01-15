/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/05/2010
 *
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.integracao.dto.FormacaoAcademicaDTO;

/**
 * Interface para comunicação entre SIGAA e SIGPRH.
 * @author Alisson Nascimento
 */
@WebService
public interface FormacaoAcademicaRemoteService extends Serializable {

	/**
	 * Realiza a consulta de formação escolar do servidor passado.
	 * @param idServidor
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public List<FormacaoAcademicaDTO> consultarFormacaoAcademica (Integer idServidor, Integer areaConhecimento, Integer pais,
			String instituicao, String titulo, Date dataInicio, Date dataFim, Integer... idFormacao);
	
}
