package br.ufrn.sigaa.ead.negocio;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dao.MetodologiacaAvaliacaoEadDao;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;

/**
 * Classe com métodos auxiliares para metodologia de avaliação
 * 
 * @author Rayron Victor
 *
 */
public class MetodologiaAvaliacaoHelper {

	/**
	 * Retorna a metodologia de avaliação utilizada com
	 * base no curso, ano e período
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public static MetodologiaAvaliacao getMetodologia(Curso curso, int ano, int periodo) throws DAOException {
		
		MetodologiacaAvaliacaoEadDao dao = DAOFactory.getInstance().getDAO(MetodologiacaAvaliacaoEadDao.class);
		
		try {
			return dao.findByCursoAnoPeriodo(curso, ano, periodo);
		} finally {
			dao.close();
		}
	}
	
}
