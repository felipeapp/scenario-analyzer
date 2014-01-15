package br.ufrn.sigaa.arq.dao.pesquisa;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;

/**
 * Dao para acesso aos dados dos Membros dos Grupo de Pesquisa.
 * 
 * @author Jean Guerethes
 */
public class MembroGrupoPesquisaDao extends GenericSigaaDAO {

	/**
	 * Realizar uma buscar por todos os servidores com os id´s Informados.
	 * 
	 * @param ids
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MembroGrupoPesquisa> findByIds(Collection<Integer> ids) {
		List<MembroGrupoPesquisa> result = getHibernateTemplate().find(
				" from MembroGrupoPesquisa mgp" +
				" left join fetch mgp.grupoPesquisa gp left join fetch mgp.servidor s"+
				" left join fetch s.unidade u left join fetch s.pessoa p" +
				" where s.id in " 
				+ UFRNUtils.gerarStringIn(ids) + " order by gp.nome, p.nome, u.nome");
		return result;
	}
	
	/**
	 * Realiza uma busca pelos id da pessoa verificando se o mesmo é coordenador 
	 * de algum grupo de pesquisa, que esteja em vigor.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public boolean isCoordenadorGrupoPesquisa(int idPessoa) throws DAOException {
        Criteria c = getSession().createCriteria(MembroGrupoPesquisa.class);
        c.setProjection(Projections.count("id"));
        c.add( Restrictions.eq("pessoa.id", idPessoa) );
        c.add( Restrictions.eq("classificacao", MembroGrupoPesquisa.COORDENADOR) );
        c.add( Restrictions.le("dataInicio", new Date()) );
        c.add( Restrictions.or(Restrictions.ge("dataFim", new Date()), Restrictions.isNull("dataFim")) );
        long total = (Integer)c.uniqueResult();
        return total>0;
	}

	/**
	 * Verifica se já existe a senha informada
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public boolean haSenha(String senha) throws DAOException {
        Criteria c = getSession().createCriteria(MembroGrupoPesquisa.class);
        c.add( Restrictions.eq("senhaConfirmacao", senha) );
        Long total = (Long)c.uniqueResult();
        return total != null && total > 0;
	}

	public MembroGrupoPesquisa findbyCodigo(String codigo) throws DAOException {
        Criteria c = getSession().createCriteria(MembroGrupoPesquisa.class);
        c.setFetchMode( "pessoa", FetchMode.JOIN);  
        c.add( Restrictions.eq("codigoAcesso", codigo) );
        return (MembroGrupoPesquisa) c.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<MembroGrupoPesquisa> findMembroByGrupoPesquisa(GrupoPesquisa grupoPesquisa) throws DAOException {
        Criteria c = getSession().createCriteria(MembroGrupoPesquisa.class);
        c.setFetchMode( "pessoa", FetchMode.JOIN);  
        c.add( Restrictions.eq("grupoPesquisa", grupoPesquisa) );
        return c.list();
	}

	/**
	 * Retorna o membro com as informações sobre o membro do Grupo de Pesquisa.
	 */
	public MembroGrupoPesquisa findByMembroGrupoPesq(MembroGrupoPesquisa membroGrupoPesquisa) throws DAOException {
        Criteria c = getSession().createCriteria(MembroGrupoPesquisa.class);
        c.setFetchMode("grupoPesquisa", FetchMode.JOIN);  
        c.setFetchMode("categoriaMembro", FetchMode.JOIN);  
        c.setFetchMode("tipoMembroGrupoPesquisa", FetchMode.JOIN);  
        c.setFetchMode("pessoa", FetchMode.JOIN);  
        c.setFetchMode("discente", FetchMode.JOIN);  
        c.setFetchMode("servidor", FetchMode.JOIN);  
        c.setFetchMode("docenteExterno", FetchMode.JOIN);  
        c.add( Restrictions.eq("id", membroGrupoPesquisa.getId()) );
        return (MembroGrupoPesquisa) c.uniqueResult();
	}

	public MembroGrupoPesquisa coordenadorGrupoPesquisa(int idPessoa, GrupoPesquisa grupoPesquisa ) throws DAOException {
        Criteria c = getSession().createCriteria(MembroGrupoPesquisa.class);
        c.setProjection(Projections.count("id"));
        c.add( Restrictions.eq("pessoa.id", idPessoa) );
        if (  !isEmpty( grupoPesquisa ) )
        	c.add( Restrictions.eq("grupoPesquisa.id", grupoPesquisa.getId()) );
        c.add( Restrictions.eq("classificacao", MembroGrupoPesquisa.COORDENADOR) );
        c.add( Restrictions.le("dataInicio", new Date()) );
        c.add( Restrictions.or(Restrictions.ge("dataFim", new Date()), Restrictions.isNull("dataFim")) );
        return (MembroGrupoPesquisa) c.uniqueResult();
	}
	
}