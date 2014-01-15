/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/11/2006'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/**
 * Classe Responsável pelo acesso ao dados do membro da comissão 
 * de monitoria, pesquisa e extensão no banco de dados.
 *
 * @author David Ricardo
 *
 */
public class MembroComissaoDao extends GenericSigaaDAO {

	/**
	 * Busca um membro da comissão ativo através do usuário e do papel (tipo de comissão) informado
	 * 
	 * @param usr
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	public MembroComissao findByUsuario(Usuario usr, Integer papel) throws DAOException {
		Criteria c = getCriteria(MembroComissao.class);
		c.add(Expression.eq("servidor", usr.getServidor()));
		c.add(Expression.eq("papel", papel));
		c.add(Expression.le("dataInicioMandato", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
		c.add(Expression.isNull("dataDesligamento"));

		return (MembroComissao) c.uniqueResult();
	}
	
	/**
	 * Verifica se o servidor informado é membro ativo da comissão de pesquisa
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public boolean isMembroComissaoPesquisa(Servidor servidor) throws DAOException {
		Criteria c = getCriteria(MembroComissao.class);
		c.add(Expression.eq("servidor.id", servidor.getId()));
		c.add(Expression.eq("papel", MembroComissao.MEMBRO_COMISSAO_PESQUISA) );
		c.add(Expression.le("dataInicioMandato", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
		c.add(Expression.isNull("dataDesligamento"));
		c.setProjection(Projections.property("id"));
		return c.uniqueResult() != null;
	}

	/**
	 * Verifica se o servidor informado é membro ativo da comissão de extensão
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public boolean isMembroComissaoExtensao(Servidor servidor) throws DAOException {
		Criteria c = getCriteria(MembroComissao.class);
		c.add(Expression.eq("servidor.id", servidor.getId()));
		c.add(Expression.eq("papel", MembroComissao.MEMBRO_COMISSAO_EXTENSAO) );
		c.add(Expression.le("dataInicioMandato", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
		c.add(Expression.isNull("dataDesligamento"));
		c.setProjection(Projections.property("id"));
		return c.uniqueResult() != null;
	}

	/**
	 * Verifica se o servidor informado é membro ativo da comissão de monitoria
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public boolean isMembroComissaoMonitoria(Servidor servidor) throws DAOException {
		Criteria c = getCriteria(MembroComissao.class);
		c.add(Expression.eq("servidor.id", servidor.getId()));
		c.add(Expression.eq("papel", MembroComissao.MEMBRO_COMISSAO_MONITORIA) );
		c.add(Expression.le("dataInicioMandato", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
		c.add(Expression.isNull("dataDesligamento"));
		c.setProjection(Projections.property("id"));
		return c.uniqueResult() != null;
	}

	/**
	 * Verifica se o servidor informado é membro ativo da comissão científica de monitoria.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public boolean isMembroComissaoCientificaMonitoria(Servidor servidor) throws DAOException {
		Criteria c = getCriteria(MembroComissao.class);
		c.add(Expression.eq("servidor.id", servidor.getId()));
		c.add(Expression.eq("papel", MembroComissao.MEMBRO_COMISSAO_CIENTIFICA) );
		c.add(Expression.le("dataInicioMandato", DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
		c.add(Expression.isNull("dataDesligamento"));
		c.setProjection(Projections.property("id"));
		return c.uniqueResult() != null;
	}
	
	
	/**
	 * Informa se o servidor já está cadastrado como membro da comissão informada.
	 * Utilizado no cadastro e alteração dos membros das comissões.
	 *
	 * @param idServidor
	 * @param comissao
	 * @return
	 * @throws DAOException
	 */
	public boolean isMembroCadastrado(int idServidor, int comissao) throws DAOException {
		Criteria c = getCriteria(MembroComissao.class);
		c.add(Expression.eq("servidor.id", idServidor));
		c.add(Expression.eq("papel", comissao) );
		c.add(Expression.isNull("dataDesligamento"));
		c.setProjection(Projections.property("id"));		
		return c.uniqueResult() != null;
	}


	/**
	 * Retorna todos os membros ativos da comissão passada 
	 * 
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroComissao> findByComissao(Integer papel) throws DAOException {

		String projecao = "mc.id, mc.servidor.id, mc.servidor.siape, mc.servidor.pessoa.id, mc.servidor.pessoa.nome, mc.servidor.unidade.id, mc.servidor.unidade.nome, mc.papel, mc.dataInicioMandato, mc.dataFimMandato ";
		String hqlQuery = "select " + projecao + " from MembroComissao mc " +
				"where mc.dataDesligamento is null and mc.ativo = trueValue() ";
		
		if (papel != null && papel != 0) {
			hqlQuery += " and mc.papel = :papel ";
		}
		hqlQuery += " order by mc.servidor.pessoa.nome ";

		Query query = getSession().createQuery(hqlQuery);
		if (papel != null && papel != 0) {
			query.setInteger("papel", papel);
		}	
		return HibernateUtils.parseTo(query.list(), projecao, MembroComissao.class, "mc");
	}

	
	/**
	 * Retorna o membro ativo da comissão passada
	 * através do id do servidor
	 * 
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	public MembroComissao findByServidorPapel(Integer idServidor, Integer papel)
			throws DAOException {

		if (papel != null && idServidor != null && papel != 0 && idServidor != 0) {

			Criteria c = getSession().createCriteria(MembroComissao.class);
				c.add(Expression.isNull("dataDesligamento")); //só membros ativos		
				c.add(Expression.eq("papel", papel));
				c.add(Expression.eq("servidor.id", idServidor));
				
				return (MembroComissao) c.uniqueResult();
		}		

		return null;

	}

	
	
	
	/**
	 * Retorna todos os Membros de determinada comissão de determinado projeto
	 * que fizeram determinado tipo de avaliação.
	 * 
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroComissao> findByProjeto(Papel papel,
			TipoAvaliacaoMonitoria tipoAvaliacao, ProjetoEnsino projeto)
			throws DAOException {

		Map<Object, Object> parameters = new HashMap<Object, Object>();
		boolean first = true;
		StringBuffer queryBuf = new StringBuffer(
				"select aval.avaliador from ProjetoEnsino pm ");
		queryBuf
				.append("inner join pm.avaliacoes aval inner join aval.avaliador.papel p ");

		if (projeto != null && projeto.getId() != 0) {
			queryBuf.append(first ? " where " : " and ");
			queryBuf.append(" pm.id = :id_projeto ");
			parameters.put("id_projeto", projeto.getId());
			first = false;
		}
		
		//se o papel for null retorna os membros das duas comissões
		if (papel != null && papel.getId() != 0) {
			queryBuf.append(first ? " where " : " and ");
			queryBuf.append(" p.id = :id_papel ");
			parameters.put("id_papel", papel.getId());
			first = false;
		}
		if (tipoAvaliacao != null && tipoAvaliacao.getId() != 0) {
			queryBuf.append(first ? " where " : " and ");
			queryBuf.append(" aval.tipoAvaliacao.id = :id_tipo_avaliacao ");
			parameters.put("id_tipo_avaliacao", tipoAvaliacao.getId());
		}

		String hqlQuery = queryBuf.toString();
		Query query = getSession().createQuery(hqlQuery);

		//
		// Set valores de parâmetros da query
		//
		Iterator<Object> iter = parameters.keySet().iterator();
		while (iter.hasNext()) {
			String nome = (String) iter.next();
			Object valor = parameters.get(nome);
			query.setParameter(nome, valor);
		}

		
		//
		// Execute the query
		//
		return query.list();

	}
	
	
	/**
	 * Retorna todos os membros ativos da comissão passada. 
	 * 
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	public List<Usuario> findUsuariosByComissao(Integer papel) throws DAOException {
	    String hql = new String(
		    "select id, pessoa.id, pessoa.nome, email " +
		    " from Usuario where " +
	    " pessoa.id in (select servidor.pessoa.id from MembroComissao where ativo = trueValue() and papel = :papel) ");
	    Query query = getSession().createQuery(hql);
	    query.setInteger("papel", papel);

	    @SuppressWarnings("unchecked")
	    List<Object> lista = query.list();
	    ArrayList<Usuario> result = new ArrayList<Usuario>();
	    for (int a = 0; a < lista.size(); a++) {
		int col = 0;
		Object[] colunas = (Object[]) lista.get(a);
		Usuario user = new Usuario();
		user.setId((Integer) colunas[col++]);
		user.setPessoa(new Pessoa((Integer)colunas[col++]));
		user.getPessoa().setNome((String) colunas[col++]);
		user.setEmail((String) colunas[col++]);
		
		Servidor s = new Servidor();
		s.setPrimeiroUsuario(user);
		s.setPessoa(user.getPessoa());	
		user.setServidor(s);
		
		result.add(user);
	    }
	    return result;
	}

	/**
	 * Busca tipos de comissão por servidor
	 * @param servidor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findTiposComissaoPorServidor(Servidor servidor) throws DAOException {
		return getSession().createQuery("select distinct m.papel from MembroComissao m " +
				" where m.servidor.id = ? and m.dataInicioMandato <= ? " +
				" and (m.dataFimMandato is null or m.dataFimMandato >= ?) " +
				" and m.ativo = ? ")
			.setInteger(0, servidor.getId())
			.setDate(1, DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
			.setDate(2, DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH))
			.setBoolean(3, Boolean.TRUE)
			.list();
	}
	
}



