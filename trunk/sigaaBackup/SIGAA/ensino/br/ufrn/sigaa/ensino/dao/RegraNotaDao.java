/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 18/07/2011
 * Autor: Arlindo Rodrigues
 */
package br.ufrn.sigaa.ensino.dao;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.RegraNota;

/**
 * Dao responsável por realizar consultas de Regras de Notas para consolidação (nível médio).
 * 
 * @author Arlindo
 */
public class RegraNotaDao extends GenericSigaaDAO {
	

	/**
	 * Retorna as regras de consolidação do curso informado
	 * @param curso
	 * @param tipos
	 * @return
	 * @throws DAOException
	 */
	public List<RegraNota> findByCurso(Curso curso, Integer... tipos) throws DAOException {
		
		String projecao = " r.id, r.configuracao.id, r.configuracao.curso.id, r.titulo, r.ordem, r.tipo, " +
				" r.peso, r.refRec, r. classeEstrategiaRecuperacao ";
		
		StringBuffer hql = new StringBuffer("select "+projecao);
		hql.append(" from RegraNota r ");
		hql.append(" inner join r.configuracao ");
		hql.append(" left join r.configuracao.curso curso ");
		
		if (ValidatorUtil.isNotEmpty(curso))
			hql.append(" where curso.id = "+curso.getId());
		else
			hql.append(" where curso is null ");
		
		if (tipos != null && tipos.length > 0)
			hql.append(" and r.tipo in "+UFRNUtils.gerarStringIn(tipos));
		
		hql.append(" order by r.ordem ");
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<RegraNota> lista =  (List<RegraNota>) HibernateUtils.parseTo(q.list(), projecao, RegraNota.class, "r");

		// Caso não encontrar regra para o curso informado pega a regra geral
		if (ValidatorUtil.isEmpty(lista) && ValidatorUtil.isNotEmpty(curso))
			lista = findByCurso(null, tipos);
		
		return lista;
	}	

}
