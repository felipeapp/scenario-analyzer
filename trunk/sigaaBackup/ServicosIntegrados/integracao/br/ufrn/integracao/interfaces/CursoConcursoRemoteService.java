package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.jws.WebService;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.integracao.dto.CursoLatoDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Interface para comunica��o entre SIGAA e SIGPRH.
 * @author Diogo Souto
 */
@WebService
public interface CursoConcursoRemoteService extends Serializable {

	/**
	 * Realiza a cria��o do projeto de curso e concurso para o curso de
	 * Lato Sensu especificado, com as informa��es especificadas nos par�metros.
	 *
	 * @param cursoLatoDTO
	 * @param idUsuarioLogado
	 * @throws NegocioRemotoException 
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public void cadastrarProjetoCursoConcurso(CursoLatoDTO cursoLatoDTO, int idUsuarioLogado) throws NegocioRemotoException;
}
