/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2010'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.GrupoAvaliacao;

/**
 * Dao responsável por consultas relacionadas ao grupos de avaliações de projetos
 * @author geyson
 *
 */
public class GrupoAvaliacaoDao extends GenericSigaaDAO {

	/**
	 * Verifica se um grupo já esta ligada a algum questionario de avaliação.
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	public GrupoAvaliacao findGrupoExistenteQuestionario(GrupoAvaliacao grupo) throws DAOException {

		String hql = " select g from ItemAvaliacaoProjeto it " +
		" inner join it.grupo g " +
		" inner join it.questionario quest" +
		" where  it.ativo = trueValue() " +
		" and g.id = " + grupo.getId() + " ";

		Query query = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<GrupoAvaliacao> lista = query.list();

		for (GrupoAvaliacao grupoAvaliacao : lista) {
			if(grupoAvaliacao != null)
				return grupoAvaliacao;
		}

		return null;
	}


}
