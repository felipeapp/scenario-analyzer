/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '01/11/2006'
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.GrupoItemAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.ItemAvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ResumoSid;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Dao utilizado nas buscas de avaliações de monitoria.
 * 
 * @author UFRN
 *
 */
public class AvaliacaoMonitoriaDao extends GenericSigaaDAO {

	/**
	 * Retorna a avaliação ativa do membro da comissão no projeto e/ou resumo informado
	 *
	 * @param projeto
	 * @param avaliador
	 * @param tipoAvaliacao
	 * @return
	 * @throws DAOException
	 */
	public AvaliacaoMonitoria findByProjetoResumoAvaliador(int idProjeto, int idResumo, int idMembroComissao, int idTipoAvaliacao) throws DAOException {
		if ( ((idProjeto > 0) || (idResumo > 0)) && (idMembroComissao > 0) ){

			Criteria c = getSession().createCriteria(AvaliacaoMonitoria.class);

			if (idProjeto > 0)
				c.add(Expression.eq("projetoEnsino.id", idProjeto));
			
			if (idResumo > 0)
				c.add(Expression.eq("resumoSid.id", idResumo));			
			
			c.add(Expression.eq("avaliador.id", idMembroComissao));
			c.add(Expression.eq("tipoAvaliacao.id", idTipoAvaliacao));
			c.add(Expression.isNull("dataRetiradaDistribuicao")); //avaliação ativa

			return (AvaliacaoMonitoria) c.uniqueResult();

		}else{
			return null;
		}

	}
	
	/**
	 * Retorna as avaliações do resumo sid de um avaliador.
	 * 
	 * @param idAvaliador
	 * @return
	 * @throws DAOException
	 */	
	public Collection<AvaliacaoMonitoria> findByAvaliadorAvaliacaoResumiSid(int idAvaliador) throws DAOException {
		
		Query q = getSession().createQuery(
			" select aval.id, status.descricao, status.id,aval.dataAvaliacao, sid.id, projM.id, proj.id, proj.ano, proj.titulo " +
			" from AvaliacaoMonitoria  aval " +
			" inner join aval.resumoSid sid  " +
			" inner join aval.statusAvaliacao status  " +
			" inner join aval.projetoEnsino projM " +
			" inner join aval.avaliador avaliador " +
			" inner join projM.projeto proj " +
			" where aval.tipoAvaliacao.id = :AVALIACAO_RESUMO_SID " +
			"       and avaliador.id = :idAvaliador and status.id != :idStatusCancelado ");		        
	
		q.setInteger("idAvaliador", idAvaliador);
		q.setInteger("AVALIACAO_RESUMO_SID", TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID);
		
		q.setInteger("idStatusCancelado", StatusAvaliacao.AVALIACAO_CANCELADA);
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();

		ArrayList<AvaliacaoMonitoria> result = new ArrayList<AvaliacaoMonitoria>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			AvaliacaoMonitoria avaliacaoMonitoria = new AvaliacaoMonitoria();
			
			avaliacaoMonitoria.setId((Integer) colunas[col++]);
			avaliacaoMonitoria.getStatusAvaliacao().setDescricao((String) colunas[col++]);
			avaliacaoMonitoria.getStatusAvaliacao().setId((Integer) colunas[col++]);
			avaliacaoMonitoria.setDataAvaliacao((Date) colunas[col++]);
			avaliacaoMonitoria.getResumoSid().setId((Integer) colunas[col++]);
			avaliacaoMonitoria.getProjetoEnsino().setId((Integer) colunas[col++]);
			avaliacaoMonitoria.getProjetoEnsino().getProjeto().setId((Integer) colunas[col++]);
			avaliacaoMonitoria.getProjetoEnsino().getProjeto().setAno((Integer) colunas[col++]);
			avaliacaoMonitoria.getProjetoEnsino().getProjeto().setTitulo((String) colunas[col++]);

			result.add(avaliacaoMonitoria);
		}
		return result;	
		
	}
	
	/**
	 * 
	 * Retorna os ítens de avaliação de um grupo.
	 * 
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")	
	public List<ItemAvaliacaoMonitoria> findItensByGrupo(GrupoItemAvaliacao grupo) throws DAOException {
		Criteria c = getCriteria(ItemAvaliacaoMonitoria.class);
		c.add(Expression.eq("grupo.id", grupo.getId()));
		c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return c.list();
	}

	/**
	 * 
	 * Retorna as avaliações de um projeto de monitoria.
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AvaliacaoMonitoria> findAvaliacoesByProjeto(int projeto) throws DAOException {
		Criteria c = getCriteria(AvaliacaoMonitoria.class);
		c.add(Expression.eq("projetoEnsino.id", projeto));
		return c.list();
	}

	/**
	 * Retorna todos os avaliadores do projeto que possuem avaliações NÃO canceladas
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<MembroComissao> findAvaliadoresByProjeto(int idProjeto) throws DAOException {
		Query q = getSession().createQuery(
				"select distinct avaliador from " +
				"ProjetoEnsino pm " +
				"inner join pm.avaliacoes avals " +							
				"inner join avals.avaliador avaliador " +				
				"where pm.id = :idProjeto " +
				"and avals.statusAvaliacao.id != :idStatus"
				);
		
		q.setInteger("idProjeto", idProjeto);
		q.setInteger("idStatus", StatusAvaliacao.AVALIACAO_CANCELADA);

		return q.list();
	}

	
	/**
	 * Retorna todos os grupos dos relatórios de monitoria, que são ativos, de determinado tipo
	 *
	 * @param tipo - tipo de grupos R - Relatório, P - Projeto 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GrupoItemAvaliacao> findByGruposAtivosDoTipo(Character tipo) throws DAOException {
		try {

			Criteria c = getCriteria(GrupoItemAvaliacao.class);
				c.add(Expression.eq("tipo", tipo));
				c.add(Expression.eq("ativo", true));
				
			return c.list();

		} catch(Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Retorna todas as avaliações de projetos ou de resumos com status igual ao status informado.
	 * 
	 * @param idProjeto
	 * @param idStatusAvaliacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoMonitoria> findByProjetosStatusAvaliacao(Integer idProjeto, Integer idResumo, Integer idStatusAvaliacao) throws DAOException {
		String projecao = "av.id, av.dataAvaliacao, av.parecer, av.notaAvaliacao, " +
		"av.tipoAvaliacao.id, av.avaliacaoPrograd, av.statusAvaliacao.id, av.dataDistribuicao, " +
		"av.projetoEnsino.id, av.projetoEnsino.projeto.id, av.avaliador.id, av.resumoSid.id ";

		String hql = "select " +  projecao + " from AvaliacaoMonitoria av where av.statusAvaliacao.id = :idStatus and av.ativo = trueValue() ";

		if (idProjeto != null) {
			hql += " and av.projetoEnsino.id = :idProjeto ";
		}

		if (idResumo != null) {
			hql += " and av.resumoSid.id = :idResumo ";
		}

		Query q = getSession().createQuery(hql);			
		q.setInteger("idStatus", StatusAvaliacao.AVALIACAO_CANCELADA);

		if (idResumo != null) {
			q.setInteger("idResumo", idResumo);
		}
		if (idProjeto != null) {
			q.setInteger("idProjeto", idProjeto);
		}

		return HibernateUtils.parseTo(q.list(), projecao, AvaliacaoMonitoria.class, "av");
	}


	/**
	 * Retorna todas as avaliações onde o avaliador atuou ou atua 
	 * 
	 * @param membro membro da comissão
	 * @return
	 * @throws DAOException
	 */
	public Collection<AvaliacaoMonitoria> findByAvaliador(int idServidor, int idTipoAvaliacao) throws DAOException {
		
	    	StringBuilder hql = new StringBuilder();
	    	hql.append("select avaliacao.id,  avaliacao.dataAvaliacao, status, " +
	    			"pe.id, " +
	    			"pe.projeto.id, " +
	    	    		"pe.projeto.ano, " +
	    	    		"pe.projeto.titulo ");
	    
	    	if (TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID == idTipoAvaliacao) {
	    	    hql.append(", resumo ");
	    	}
	    	
	    	hql.append(" from AvaliacaoMonitoria avaliacao ");
	    	
	    	if (TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID == idTipoAvaliacao) {
	    	    hql.append("join avaliacao.resumoSid resumo ");
	    	}
	    	
	    	hql.append("join avaliacao.avaliador avaliador " +							
			"join avaliador.servidor s " +
			"join avaliacao.tipoAvaliacao tipo " +
			"join avaliacao.statusAvaliacao status " +
			"join avaliacao.projetoEnsino pe ");
	    		    	
	    	hql.append("where s.id = :idServidor and tipo.id = :idTipo " +
			"and status.id != :idStatusCancelado");
	    
		Query q = getSession().createQuery(hql.toString());	
		q.setInteger("idServidor", idServidor);
		q.setInteger("idTipo", idTipoAvaliacao);
		q.setInteger("idStatusCancelado", StatusAvaliacao.AVALIACAO_CANCELADA);
		
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();

		ArrayList<AvaliacaoMonitoria> result = new ArrayList<AvaliacaoMonitoria>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			AvaliacaoMonitoria avaliacaoMonitoria = new AvaliacaoMonitoria();
			
			avaliacaoMonitoria.setId((Integer) colunas[col++]);
			avaliacaoMonitoria.setDataAvaliacao((Date) colunas[col++]);
			avaliacaoMonitoria.setStatusAvaliacao((StatusAvaliacao) colunas[col++]);
			avaliacaoMonitoria.getProjetoEnsino().setId((Integer) colunas[col++]);
			avaliacaoMonitoria.getProjetoEnsino().getProjeto().setId((Integer) colunas[col++]);
			avaliacaoMonitoria.getProjetoEnsino().getProjeto().setAno((Integer) colunas[col++]);
			avaliacaoMonitoria.getProjetoEnsino().getProjeto().setTitulo((String) colunas[col++]);
			
		    	if (TipoAvaliacaoMonitoria.AVALIACAO_RESUMO_SID == idTipoAvaliacao) {
		    	    avaliacaoMonitoria.setResumoSid((ResumoSid) colunas[col++]);
		    	}

			result.add(avaliacaoMonitoria);
		}
		return result;	
		
	}
	
	/**
	 * Retorna projetos de acordo com um 
	 * edital e uma situação informada. 
	 * 
	 * @param status
	 * @param idEdital
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ProjetoEnsino> findProjetosByStatusEdital(Integer status, Integer idEdital) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" Select distinct pe.id, pe.mediaAnalise, p.id, p.ano, p.titulo, p.situacaoProjeto, pe.editalMonitoria.mediaAprovacaoProjeto " +
				" FROM AvaliacaoMonitoria aval inner join aval.projetoEnsino pe" +
				" inner join pe.projeto p where p.situacaoProjeto.id = :idStatus and pe.editalMonitoria.id = :idEdital " +
				"order by pe.mediaAnalise desc");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idStatus", status);
		query.setInteger("idEdital", idEdital);
		
		List<Object> lista = query.list();

		ArrayList<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			ProjetoEnsino pje = new ProjetoEnsino();			
			pje.setId((Integer) colunas[col++]);
			pje.setMediaAnalise((Double) colunas[col++]);
			pje.setProjeto(new Projeto((Integer) colunas[col++]));
			pje.getProjeto().setAno((Integer) colunas[col++]);
			pje.getProjeto().setTitulo((String) colunas[col++]);
			pje.getProjeto().setSituacaoProjeto((TipoSituacaoProjeto) colunas[col++]);
			pje.getEditalMonitoria().setMediaAprovacaoProjeto((Double) colunas[col++]);
			
			result.add(pje);
		}
		return result;	

	}
	
	/**
	 * Retorna projetos com notas discrepantes após avaliações.
	 *  
	 * @param status
	 * @param idEdital
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ProjetoEnsino> findProjetosDiscrepantes(Integer idStatus, EditalMonitoria edital) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append(" Select distinct pe.id, pe.mediaAnalise, pe.notaPrimeiraAvaliacao, pe.notaSegundaAvaliacao, pe.editalMonitoria, " +
				" p.id, p.ano, p.titulo, p.situacaoProjeto FROM ProjetoEnsino pe join pe.projeto p " +
				" where pe.situacaoProjeto.id = :idStatus " +
				" and (pe.notaPrimeiraAvaliacao is not null) and (pe.notaSegundaAvaliacao is not null) " +
				" and pe.editalMonitoria.id = :idEdital " +
				" and ( ( abs(pe.notaPrimeiraAvaliacao - pe.notaSegundaAvaliacao) > :indiceAvaliacaoDiscrepante " +
				" 		  and (pe.notaPrimeiraAvaliacao < :mediaAprovacaoProjeto or pe.notaSegundaAvaliacao < :mediaAprovacaoProjeto) " +
				" 		  and (pe.notaAvaliacaoFinal is null) " +
				"       )" +
				"       or (pe.notaPrimeiraAvaliacao is null or pe.notaSegundaAvaliacao is null) " +
				"     )");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idEdital", edital.getId());
		query.setInteger("idStatus", idStatus);
		query.setDouble("indiceAvaliacaoDiscrepante", edital.getIndiceAvaliacaoDiscrepante());
		query.setDouble("mediaAprovacaoProjeto", edital.getMediaAprovacaoProjeto());
		List<Object> lista = query.list();

		ArrayList<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);
			
			ProjetoEnsino pje = new ProjetoEnsino();			
			pje.setId((Integer) colunas[col++]);
			pje.setMediaAnalise((Double) colunas[col++]);
			pje.setNotaPrimeiraAvaliacao((Double) colunas[col++]);
			pje.setNotaSegundaAvaliacao((Double) colunas[col++]);
			pje.setEditalMonitoria((EditalMonitoria) colunas[col++]);
			
			pje.setProjeto(new Projeto((Integer) colunas[col++]));
			pje.getProjeto().setAno((Integer) colunas[col++]);
			pje.getProjeto().setTitulo((String) colunas[col++]);
			pje.getProjeto().setSituacaoProjeto((TipoSituacaoProjeto) colunas[col++]);
			
			result.add(pje);
		}
		return result;	

	}

	/**
	 * Serve pra verificar se ainda há avaliação pendente.
	 */
	public boolean haAvaliadoresSemAvaliacao(int projeto, int avaliacao) throws DAOException {
		Criteria c = getCriteria(AvaliacaoMonitoria.class);
		c.setProjection(Projections.rowCount());
		c.add(Expression.eq("projetoEnsino.id", projeto));
		c.add(Expression.ne("id", avaliacao));
		c.add(Expression.eq("statusAvaliacao.id", StatusAvaliacao.AGUARDANDO_AVALIACAO));
		return (Integer) c.uniqueResult()>0; 
	}
	
	/** Retorna a quantidade de avaliadores ao qual o projeto de monitoria foi distribuido */
	public Integer totalAvaliadores(int projeto) throws DAOException {
		Criteria c = getCriteria(AvaliacaoMonitoria.class);
		c.setProjection(Projections.rowCount());
		c.add(Expression.eq("projetoEnsino.id", projeto));
		return (Integer) c.uniqueResult(); 
	}
	
}