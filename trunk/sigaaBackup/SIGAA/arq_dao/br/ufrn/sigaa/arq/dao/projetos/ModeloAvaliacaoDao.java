/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '27/10/2010'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.ModeloAvaliacao;

/**
 * Dao respons�vel pelo modelo de Avalia��o
 *
 */
public class ModeloAvaliacaoDao extends GenericSigaaDAO {
	
	/**
	 * Verifica se um grupo j� esta ligada a algum questionario de avalia��o.
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	public ModeloAvaliacao findDistribuicaoModeloExistente(ModeloAvaliacao modelo) throws DAOException {

		String hql = " select m from DistribuicaoAvaliacao da " +
		" inner join da.modeloAvaliacao m " +
		" where  da.ativo = trueValue() " +
		" and m.id = " + modelo.getId() + " ";

		Query query = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<ModeloAvaliacao> lista = query.list();

		for (ModeloAvaliacao modeloAvaliacao : lista) {
			if(modeloAvaliacao != null)
				return modeloAvaliacao;
		}

		return null;
	}

	/**
	 * Busca os modelos de avalia��o de acordo com tipo de edital informado.
	 * @param tipoEdital
	 * @return
	 * @throws DAOException
	 */
	public Collection<ModeloAvaliacao> findByTipoEdital(Character tipoEdital) throws DAOException {
		Query query = getSession().createQuery("from ModeloAvaliacao m where m.tipo = :tipo and m.ativo = trueValue()").setCharacter("tipo", tipoEdital);
		@SuppressWarnings("unchecked")
		List<ModeloAvaliacao> lista = query.list();
		return lista;
	}
}
