package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.Collection;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.dto.CursoLatoDTO;
import br.ufrn.integracao.dto.TurmaLatoDTO;

/**
 * Interface para comunicação entre SIGPRH e SIGAA.
 * @author Diogo Souto
 */
@WebService
public interface CursoLatoRemoteService extends Serializable {

	/**
	 * Retorna todos as turmas do curso lato informado.
	 * @param idCursoLato
	 * @return
	 * @throws DAOException
	 */
	public Collection<TurmaLatoDTO> findTurmasAbertasOuConsolidadasByCursoLato(int idCursoLato);

	/**
	 * Retorna todos os cursos lato coordenados pelo servidor especificado.
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<CursoLatoDTO> findCursosLatoCoordenadosPor(int idServidor);

}
