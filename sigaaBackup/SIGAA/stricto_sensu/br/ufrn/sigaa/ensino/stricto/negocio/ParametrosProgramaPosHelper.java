/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
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
 * Classe auxiliar para carregar os par�metros de um programa de p�s-gradua��o
 *
 * @author Ricardo Wendell
 *
 */
public class ParametrosProgramaPosHelper {

	/**
	 * Recupera os par�metros de um determinado programa de p�s-gradua��o.
	 * Caso n�o existam par�metros definidos para o programa ent�o �
	 * retornada uma inst�ncias com valores padr�o.
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
	 * Recupera os par�metros associados a um discente (na verdade, a sua gestora acad�mica)
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
