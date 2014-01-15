/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.stricto.ParametrosProgramaPosDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.stricto.dominio.ParametrosProgramaPos;

/**
 * Classe auxiliar para carregar os parâmetros de um programa de pós-graduação
 *
 * @author Ricardo Wendell
 *
 */
public class ParametrosProgramaPosHelper {

	/**
	 * Recupera os parâmetros de um determinado programa de pós-graduação.
	 * Caso não existam parâmetros definidos para o programa então é
	 * retornada uma instâncias com valores padrão.
	 *
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosProgramaPos getParametros(Unidade programa) throws DAOException {
		ParametrosProgramaPosDao parametrosDao = getDAO(ParametrosProgramaPosDao.class);
		try {
			ParametrosProgramaPos parametros = parametrosDao.findByPrograma(programa);
			if (parametros == null) {
				parametros = new ParametrosProgramaPos(programa);
			}
			return parametros;
		} finally {
			parametrosDao.close();
		}
	}

	/**
	 * Recupera os parâmetros associados a um discente (na verdade, a sua gestora acadêmica)
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public static ParametrosProgramaPos getParametros(DiscenteAdapter discente) throws DAOException {
		return getParametros(discente.getGestoraAcademica());
	}

	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

}
