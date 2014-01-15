package br.ufrn.sigaa.arq.dao.projetos;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;

/*******************************************************************************
 * Dao para realizar consultas sobre as funções dos membros dos projetos
 * 
 * @author Jean Guerethes
 * 
 ******************************************************************************/
public class FuncaoMembroDao extends GenericSigaaDAO {

	/**
	 * Retorna as funções cujo parâmetro foi informado.
	 * 
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	public Collection<FuncaoMembro> findByFuncoes(Integer... funcao) throws DAOException {

		String hql = "FROM FuncaoMembro fm WHERE fm.id in " + UFRNUtils.gerarStringIn(funcao);

		Query query = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<FuncaoMembro> lista = query.list();

		return lista;
	}
	
	public Collection<FuncaoMembro> findByFuncoesPesquisa( int escopo ) throws DAOException {
		return findByFuncoesProjeto(escopo, true, false, false, false);	
	}
	
	public Collection<FuncaoMembro> findByFuncoesProjeto( int escopo, boolean pesquisa, boolean extensao, boolean integrados, boolean ensino ) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(FuncaoMembro.class);
			c.add(Restrictions.eq("escopo", escopo));
			if (pesquisa)
				c.add(Restrictions.eq("pesquisa", pesquisa));
			if (extensao)
				c.add(Restrictions.eq("extensao", extensao));
			if (integrados)
				c.add(Restrictions.eq("integrados", integrados));
			if (ensino)
				c.add(Restrictions.eq("ensino", ensino));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}