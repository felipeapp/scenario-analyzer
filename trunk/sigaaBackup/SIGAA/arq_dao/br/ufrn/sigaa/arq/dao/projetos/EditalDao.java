/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Dao para acesso aos dados dos editais de projetos.
 *
 * @author André
 *
 */
public class EditalDao extends GenericSigaaDAO {

	/**
	 * Retorna o edital mais recentemente aberto do tipo passado.
	 *  Tipos:
	 *  ASSOCIADO 		= 'A';
	 *	MONITORIA 		= 'M';
	 *	PESQUISA 		= 'P';
	 *	EXTENSAO 		= 'E';
	 *	INOVACAO 		= 'I';
	 * 
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Edital findMaisRecente(Character tipo) throws DAOException {

		Criteria c = getSession().createCriteria(Edital.class);
		c.add(Restrictions.eq("tipo", tipo));
		c.add(Restrictions.lt("inicioSubmissao", new Date()));
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		c.addOrder(Order.desc("inicioSubmissao"));
		c.setMaxResults(1);

		List<?> l = c.list();
		if (l.size() > 0) {
			return (Edital) l.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Busca os editais lançados para o período de cotas informado
	 * 
	 * Uma cota armazena as informações referentes à vigência de uma determinada
	 * distribuição de bolsas.
	 * 
	 * @param cota
	 * @return
	 * @throws DAOException
	 */
	public EditalPesquisa findByCota(CotaBolsas cota) throws DAOException {

		Criteria c = getSession().createCriteria(EditalPesquisa.class);
		c.add(Restrictions.eq("cota", cota));
		c.add(Restrictions.eq("distribuicaoCotas", Boolean.TRUE));
		c.createCriteria("edital").add(Restrictions.eq("ativo", Boolean.TRUE));
		c.setMaxResults(1);

		@SuppressWarnings("unchecked")
		List<EditalPesquisa> l = c.list();
		if (l.size() > 0) {
			return l.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Retorna todos os editais de monitoria ativos
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalMonitoria> findAllAtivosMonitoria() throws DAOException {
		Criteria c = getSession().createCriteria(EditalMonitoria.class);
		c.createCriteria("edital").add(Restrictions.eq("ativo", Boolean.TRUE));
		return c.list();
	}
	
	/**
	 * Retorna todos os editais de extensão ativos
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalExtensao> findAllAtivosExtensao() throws DAOException {
		Criteria c = getSession().createCriteria(EditalExtensao.class);
		c.createCriteria("edital").add(Restrictions.eq("ativo", Boolean.TRUE)).addOrder(Order.desc("inicioSubmissao"));
		return c.list();
	}

	/**
	 * Retorna todos os editais de pesquisa ativos.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalPesquisa> findAllAtivosPesquisa() throws DAOException {
		Criteria c = getSession().createCriteria(EditalPesquisa.class);
		c.createCriteria("edital").add(Restrictions.eq("ativo", Boolean.TRUE));
		return c.list();
	}

	/**
	 * Retorna todos os editais de associados ativos
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Edital> findAllAtivosAssociados() throws DAOException {
		Criteria c = getSession().createCriteria(Edital.class);
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		c.add(Restrictions.eq("tipo", Edital.ASSOCIADO));
		return c.list();
	}
	
	/**
	 * Retorna todos os editais ativos do tipo especificado.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Edital> findAllAtivosByTipo(Character tipo) throws DAOException {
		Criteria c = getSession().createCriteria(Edital.class);
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		c.add(Restrictions.eq("tipo", tipo));
		return c.list();
	}

	
	/**
	 * Busca otimizada de editais, podendo a quantidade de registros ser 
	 * limitada pelo valor passado como parâmetro
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Edital> findAllOtimizado(int limit) throws DAOException {
		try {
			String hql = "SELECT new Edital(idArquivo, descricao, tipo) FROM Edital where ativo = trueValue()"
					+ "order by fimSubmissao desc";
			return getSession().createQuery(hql).setMaxResults(limit).list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca a quantidade total de editais registrados na base de dados
	 * @return
	 * @throws DAOException
	 */
	public int getTotal() throws DAOException {
		try {
			String hql = "SELECT count(*) FROM Edital where ativo = trueValue()";
			Long total = (Long) getSession().createQuery(hql).uniqueResult();
			return (int) total.longValue();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca todos os editais do tipo informado cujo período de submissão está aberto,
	 * podendo limitar sua quantidade.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Edital> findAbertos(Integer limit, Character... tipo) throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);

			StringBuffer hql =  new StringBuffer("SELECT e FROM Edital e " +
					"WHERE e.ativo = trueValue() AND e.inicioSubmissao <= :hoje AND e.fimSubmissao >= :hoje ");
			
			if (tipo != null) {
			    hql.append(" AND e.tipo IN (:tipos) ");
			}
			hql.append("order by e.inicioSubmissao");

			Query q = getSession().createQuery(hql.toString());
			q.setDate("hoje", hoje);
			
			if (tipo != null) {
			    q.setParameterList("tipos", tipo);
			}
			
			if (limit != null) {
			    q.setMaxResults(limit);
			}
			
			return q.list();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todos os editais do tipo informado cujo período de submissão está aberto,
	 * podendo limitar sua quantidade.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Edital> findAbertosAssociados() throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);

			Criteria c = getSession().createCriteria(Edital.class)
			.add(Restrictions.eq("tipo", Edital.ASSOCIADO))
			.add(Restrictions.ge("ativo", true))
			.add(Restrictions.le("inicioSubmissao", hoje))
			.add(Restrictions.ge("fimSubmissao", hoje))
			.addOrder(Order.asc("inicioSubmissao"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna editais abertos de monitoria.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<EditalMonitoria> findAbertosMonitoria() throws DAOException {
	    return findAbertosMonitoria(Edital.MONITORIA);
	}

	/**
	 * Retorna editais abertos de inovação.
	 * (Projeto de Apoio a Qualidade do Ensino de Graduação - PAMQEG)
	 * @return
	 * @throws DAOException
	 */
	public Collection<EditalMonitoria> findAbertosInovacao() throws DAOException {
	    return findAbertosMonitoria(Edital.INOVACAO);
	}

	/**
	 * Busca todos os editais de monitoria ou inovação que estão em aberto.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalMonitoria> findAbertosMonitoria(Character tipo) throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);			
			Criteria c = getSession().createCriteria(EditalMonitoria.class)
			.createCriteria("edital")
			.add(Expression.or(Expression.eq("tipo", tipo),
				 Expression.eq("tipo", Edital.MONITORIA_EOU_INOVACAO)))
			.add(Restrictions.le("inicioSubmissao", hoje))
			.add(Restrictions.ge("fimSubmissao", hoje))
			.add(Restrictions.eq("ativo", Boolean.TRUE));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todos os editais de extensão associados ao edital base informado.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalExtensao> findAbertosExtensao() throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);			
			Criteria c = getSession().createCriteria(EditalExtensao.class)
			.createCriteria("edital")
			.add(Restrictions.le("inicioSubmissao", hoje))
			.add(Restrictions.ge("fimSubmissao", hoje))
			.add(Restrictions.eq("ativo", Boolean.TRUE));			
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca todos os editais cujo período de submissão está aberto,
	 * sem limite de quantidade.
	 * @return
	 * @throws DAOException
	 */
	public Collection<Edital> findAbertos() throws DAOException {
		return findAbertos(null, (Character[]) null);
	}

	/**
	 * Busca editais em aberto do tipo informado sem limite de quantidade
	 * 
	 * @param tipo M - Monitoria, P - Pesquisa, E - Extensão, A - Associado
	 * @return
	 * @throws DAOException
	 */
	public Collection<Edital> findAbertos(Character... tipo) throws DAOException {
		return findAbertos(null, tipo);
	}

	
	/**
	 * Busca todos os editais ainda não finalizados, podendo limitar sua quantidade de acordo
	 * com o parâmetro informado.
	 * 
	 * @param limit
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Edital> findNaoFinalizados(Integer limit) throws DAOException {

		try {
			StringBuilder sql =  new StringBuilder("select e.id_edital, e.descricao, e.tipo, " +
					" e.id_arquivo, e.data_inicio_submissao, e.data_fim_submissao, " +
					" e.ano, ep.distribuicao_cotas, ep.distribuicao_cotas " +
					" from projetos.edital e " +
					" left join pesquisa.edital_pesquisa ep using ( id_edital ) " +
					" WHERE e.data_fim_submissao >= :hoje" +
					" and e.ativo = :ativo");
			
			if (limit != null) {
				sql.append(" limit " + limit);
			}
			
			SQLQuery q = getSession().createSQLQuery(sql.toString());
			
			q.setDate("hoje", new Date());
			q.setBoolean("ativo", Boolean.TRUE);
			
			Collection<Object[]> bulk = q.list();
			Collection<Edital> editais = new ArrayList<Edital>();
			
			if (bulk != null) {
				for (Object[] linha : bulk) {
					int k = 0;
					Edital edital = new Edital();
					edital.setId((Integer) linha[k++]);
					edital.setDescricao((String) linha[k++]);
					edital.setTipo((Character) linha[k++]);
					edital.setIdArquivo((Integer) linha[k++]);
					edital.setInicioSubmissao((Date) linha[k++]);
					edital.setFimSubmissao((Date) linha[k++]);
					
					Short ano = (Short) linha[k++];
					edital.setAno( ano.intValue() );

					Boolean editalDeConcesao = (Boolean) linha[k++];
					edital.setEditalConcessao( editalDeConcesao == null ? false : editalDeConcesao );
					
					editais.add(edital);
				}
			}
			
			return editais;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca todos os editais ainda não finalizados, sem limite de quantidade.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Edital> findNaoFinalizados() throws DAOException {
		return findNaoFinalizados(null);
	}
	
	/**
	 * Retorna a quantidade total de bolsas do tipo informado distribuídas pelos editais de 
	 * pesquisa para o período de cotas informado.
	 *  
	 * @param tipoBolsa
	 * @param idCota
	 * @return
	 * @throws DAOException
	 */
	public long getNumeroBolsasEditais(int tipoBolsa, int idCota)throws DAOException {
        try {
        	StringBuilder hql = new StringBuilder();
        	if(tipoBolsa == TipoBolsaPesquisa.PROPESQ)
        		hql.append(" select sum(e.totalBolsasPropesq)");
        	else if(tipoBolsa == TipoBolsaPesquisa.PIBIC)
        		hql.append(" select sum(e.totalBolsasPibic)");
        	hql.append(" from EditalPesquisa e");
        	hql.append(" where e.cota.id = :idCota");
        	
        	Query query = getSession().createQuery(hql.toString());
        	query.setInteger("idCota",idCota);
        	
            return (Long) query.uniqueResult();

        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	
	/**
	 * Realizar uma busca levando em consideração os parametros passados por parametro. 
	 */
	@SuppressWarnings("unchecked")
	public Collection<Edital> findEdital(Character tipo, Integer ano, Integer semestre, String descricao) throws DAOException{
		Criteria c = getSession().createCriteria(Edital.class);
		if (tipo != null) 
			c.add(Restrictions.eq("tipo", tipo));
		if(ano != null && semestre != null){
			try{
				SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
				if(semestre == 1)
					c.add(Restrictions.between("dataCadastro", fmt.parse("01/01/"+ano.toString()), fmt.parse("30/06/"+ano.toString())));
				else if(semestre == 2)
					c.add(Restrictions.between("dataCadastro", fmt.parse("01/07/"+ano.toString()), fmt.parse("31/12/"+ano.toString())));
			}catch(ParseException e){
				throw new DAOException(e);
			}
		}
		if (descricao != null) 
			c.add(Restrictions.eq("descricao", descricao));
		return c.list();
	}

	
	/**
	 * Busca todos os editais de extensão finalizados. 
	 * Disponibilizados nos casos de Registro de ações de extensão.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalExtensao> findFinalizadosExtensao() throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);			
			Criteria c = getSession().createCriteria(EditalExtensao.class)
			.createCriteria("edital")
			.add(Restrictions.lt("inicioSubmissao", hoje))
			.add(Restrictions.lt("fimSubmissao", hoje))
			.add(Restrictions.eq("ativo", Boolean.TRUE));			
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca todos os editais do tipo informado cujo período de submissão está aberto,
	 * podendo limitar sua quantidade.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalMonitoria> findEditalMonitoriaByTipos(Integer limit, Character... tipo) throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);

			StringBuffer hql =  new StringBuffer("SELECT e FROM EditalMonitoria e " +
					"WHERE 1=1 ");
			
			if (tipo != null) {
			    hql.append(" AND e.edital.tipo IN (:tipos) ");
			}
			hql.append("order by e.edital.inicioSubmissao");

			Query q = getSession().createQuery(hql.toString());
			
			if (tipo != null) {
			    q.setParameterList("tipos", tipo);
			}
			
			if (limit != null) {
			    q.setMaxResults(limit);
			}
			
			return q.list();
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todos os editais de configuração para projetos externos de monitoria ou inovação que estão em aberto.
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EditalMonitoria> findExternosAbertosMonitoria() throws DAOException {
		try {
			Date hoje = new Date();
			hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);			
			Criteria c = getSession().createCriteria(EditalMonitoria.class)
			.createCriteria("edital")
			.add(Expression.eq("tipo", Edital.MONITORIA_EXTERNO))
			.add(Restrictions.eq("ativo", Boolean.TRUE));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
}
