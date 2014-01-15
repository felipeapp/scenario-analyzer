package br.ufrn.sigaa.extensao.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.LocalRealizacao;

/**
 * DAO para efetuar consultas de Locais de Realização.
 * 
 * @author Jean Guerethes
 */
public class LocalRealizacaoDao extends GenericSigaaDAO {
	
	public List<LocalRealizacao> findLocaisRealizacao(AtividadeExtensao atividade) throws DAOException {
		Criteria c = getSession().createCriteria(LocalRealizacao.class);
		c.add(Restrictions.eq("atividade", atividade));
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		c.setFetchMode("municipio.unidadeFederativa", FetchMode.JOIN);
		return c.list();
	}

}