package br.ufrn.sigaa.extensao.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AtividadeUnidade;

/**
 * DAO para efetuar consultas das atividades da Unidade.
 * 
 * @author guerethes
 */
public class AtividadeUnidadeDao extends GenericSigaaDAO {
	
	public List<AtividadeUnidade> findAtividades(AtividadeExtensao atividade) throws DAOException {
		Criteria c = getSession().createCriteria(AtividadeUnidade.class);
		c.add(Restrictions.eq("atividade", atividade));
		c.setFetchMode("unidade", FetchMode.JOIN);
		return c.list();
	}
}