/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/02/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.dominio.AvaliadorAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAvaliacao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;
import br.ufrn.sigaa.monitoria.dominio.GrupoItemAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * Dao para as consultas sobre a entidade AvaliacaoAtividade.
 * Contém métodos que auxiliam no processo de avaliação de uma ação de extensão
 * pelos membros do comitê de extensão.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class AvaliacaoExtensaoDao extends GenericSigaaDAO {

	/** Constante que define o limite de linhas retornadas nas consultas dos métodos desta classe.*/
	private static final long LIMITE_RESULTADOS = 1000;

	
	/**
	 * Retorna todos os grupos do tipo especificado
	 * 
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
	 * 
	 * Retorna todas as avaliações de atividades com status igual/diferente do
	 * status informado avaliações canceladas não são retornadas.
	 * 
	 * @param idAtividade
	 * @param igualAoStatus
	 *            TRUE para igual ao status informado, false para diferente do
	 *            status informado
	 * @param idStatusAvaliacao
	 * @param idTipoAvaliacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AvaliacaoAtividade> findByAtividadeStatusAvaliacao(
			Integer idAtividade, Boolean igualAoStatus,
			Integer idStatusAvaliacao, Integer idTipoAvaliacao) throws DAOException {
		try {

			StringBuffer sql = new StringBuffer("select distinct ava "
					+ " from AvaliacaoAtividade ava " 
					+ " where ava.ativo = trueValue() " 
					+ " and ava.statusAvaliacao.id != :idStatusCancelada ");
			
			if ((idAtividade != null) && (idAtividade > 0)) {
				sql.append(" and ava.atividade.id = :idAtividade ");
			}

			if ((idStatusAvaliacao != null) && (idStatusAvaliacao > 0)) {
				if (igualAoStatus) {
					sql.append(" and ava.statusAvaliacao.id = :idStatusAvaliacao ");
				}else {
					sql.append(" and ava.statusAvaliacao.id != :idStatusAvaliacao ");
				}
			}
			if ((idTipoAvaliacao != null) && (idTipoAvaliacao > 0)) {
				sql.append(" and ava.tipoAvaliacao.id = :idTipoAvaliacao");
			}

			Query q = getSession().createQuery(sql.toString());			
			q.setInteger("idStatusCancelada", StatusAvaliacao.AVALIACAO_CANCELADA);
			
			if ((idAtividade != null) && (idAtividade > 0)) {
				q.setInteger("idAtividade", idAtividade);
			}
			if ((idStatusAvaliacao != null) && (idStatusAvaliacao > 0)) {
				q.setInteger("idStatusAvaliacao", idStatusAvaliacao);
			}
			if ((idTipoAvaliacao != null) && (idTipoAvaliacao > 0)) {
				q.setInteger("idTipoAvaliacao", idTipoAvaliacao);
			}

			return q.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todas as avaliações onde o avaliador atuou ou atua
	 * 
	 * @param membro
	 *            membro da comissao
	 * @return
	 * @throws DAOException
	 */	
	public Collection<AvaliacaoAtividade> findByAvaliadorAtividade(
			Integer idServidor, Integer idAtividade, Integer idTipoAvaliacao)
			throws DAOException {
				
		StringBuilder hqlConsulta = new StringBuilder();
		StringBuilder hqlConsulta2 = new StringBuilder();
		
		hqlConsulta
		.append(" select av.id, a.id, a.tipoAtividadeExtensao.descricao, av.dataAvaliacao, av.nota, " +
				" p.titulo, p.ano, p.media, a.sequencia, a.tipoAtividadeExtensao.id, av.statusAvaliacao.id, av.statusAvaliacao.descricao, " +
				" p.situacaoProjeto.id, tipoAv.id, tipoAv.descricao " +
				" from AvaliacaoAtividade av " +				
				" inner join av.avaliadorAtividadeExtensao aae " +
				" inner join av.tipoAvaliacao tipoAv" +
				" inner join aae.servidor  serv " +
				" inner join av.atividade a " +
				" inner join a.projeto p ");
		
		
		hqlConsulta2
		.append(" select av.id, a.id, a.tipoAtividadeExtensao.descricao, av.dataAvaliacao, av.nota, " +
				" p.titulo, p.ano, p.media, a.sequencia, a.tipoAtividadeExtensao.id, av.statusAvaliacao.id, av.statusAvaliacao.descricao, " +
				" p.situacaoProjeto.id, tipoAv.id, tipoAv.descricao " +
				" from AvaliacaoAtividade av " +				
				" inner join av.membroComissao mc " +
				" inner join av.tipoAvaliacao tipoAv " +
				" inner join mc.servidor serv " +
				" inner join av.atividade a " +
				" inner join a.projeto p " );
		
		
				
		
		StringBuilder hqlFiltros = new StringBuilder();
		hqlFiltros.append(" WHERE av.ativo = trueValue() AND av.statusAvaliacao.id != :idStatusCancelada ");
		
		if(idServidor != null) {
			hqlFiltros.append(" AND serv.id = :idServidor ");
		}
		
		if(idAtividade != null) {
			hqlFiltros.append(" AND av.atividade.id = :idAtividade ");
		}
		
		if(idTipoAvaliacao != null) {
			hqlFiltros.append(" AND av.tipoAvaliacao.id = :idTipoAvaliacao ");
		}
		
		hqlConsulta.append(hqlFiltros.toString());
		hqlConsulta2.append(hqlFiltros.toString());
		hqlConsulta.append(" ORDER BY p.ano DESC, av.statusAvaliacao.descricao ");
		hqlConsulta2.append(" ORDER BY p.ano DESC, av.statusAvaliacao.descricao ");
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
		Query queryConsulta2 = getSession().createQuery(hqlConsulta2.toString());

		// Populando valores nos filtros
		queryConsulta.setInteger("idStatusCancelada", StatusAvaliacao.AVALIACAO_CANCELADA);
		queryConsulta2.setInteger("idStatusCancelada", StatusAvaliacao.AVALIACAO_CANCELADA);
		
		if (idServidor != null) {
			queryConsulta.setInteger("idServidor", idServidor);
			queryConsulta2.setInteger("idServidor", idServidor);
		}
		if (idAtividade != null) {
			queryConsulta.setInteger("idAtividade", idAtividade);
			queryConsulta2.setInteger("idAtividade", idAtividade);
		}
		if (idTipoAvaliacao != null) {
			queryConsulta.setInteger("idTipoAvaliacao", idTipoAvaliacao);
			queryConsulta2.setInteger("idTipoAvaliacao", idTipoAvaliacao);
		}
		
		@SuppressWarnings("unchecked")
		List<Object> lista =  queryConsulta.list();
		@SuppressWarnings("unchecked")
		List<Object> lista2 = queryConsulta2.list();
		List<Object> listaTodos = lista;
		listaTodos.addAll(lista2);

		Collection<AvaliacaoAtividade> result = new ArrayList<AvaliacaoAtividade>();
		
		for (int a = 0; a < listaTodos.size(); a++) {
			int col = 0;

			Object[] colunas = (Object[]) listaTodos.get(a);
			AvaliacaoAtividade avalAtiv = new AvaliacaoAtividade();
			
			AtividadeExtensao ativ = new AtividadeExtensao();
			avalAtiv.setAtividade(ativ);
			
			avalAtiv.setId((Integer) colunas[col++]);
			avalAtiv.getAtividade().setId((Integer) colunas[col++]);
			avalAtiv.getAtividade().getTipoAtividadeExtensao().setDescricao((String)colunas[col++]);
			avalAtiv.setDataAvaliacao((Date)colunas[col++]);
			avalAtiv.setNota((Double)colunas[col++]);			
			avalAtiv.getAtividade().setTitulo((String)colunas[col++]);
			avalAtiv.getAtividade().getProjeto().setAno((Integer)colunas[col++]);
			avalAtiv.getAtividade().getProjeto().setMedia((Double)colunas[col++]);
			avalAtiv.getAtividade().setSequencia((Integer)colunas[col++]);
			avalAtiv.getAtividade().getTipoAtividadeExtensao().setId((Integer)colunas[col++]);
			avalAtiv.getStatusAvaliacao().setId((Integer)colunas[col++]);
			avalAtiv.getStatusAvaliacao().setDescricao((String)colunas[col++]);
			avalAtiv.getAtividade().getSituacaoProjeto().setId((Integer)colunas[col++]);
			avalAtiv.setTipoAvaliacao(new TipoAvaliacao());
			avalAtiv.getTipoAvaliacao().setId((Integer)colunas[col++]);
			avalAtiv.getTipoAvaliacao().setDescricao((String)colunas[col++]);
			
			result.add(avalAtiv);			
		}

		return result;
	}
	
	
	/**
	 * Retorna as avaliações de um avaliador Ad Hoc.
	 * 
	 * 
	 * @param idServidor
	 * @param idAtividade
	 * @param idTipoAvaliacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<AvaliacaoAtividade> findByAvaliadorAdHoc(
			Integer idServidor, Integer idAtividade, Integer idTipoAvaliacao)
			throws DAOException {
				
		StringBuilder hqlConsulta = new StringBuilder();
		
		
		hqlConsulta
		.append(" select av.id, av.nota, parecer.id, parecer.descricao, av.dataAvaliacao,  av.atividade.projeto.titulo, av.atividade.projeto.ano, av.atividade.sequencia, " +
				" av.atividade.projeto.dataInicio , av.atividade.projeto.dataFim, av.atividade.tipoAtividadeExtensao.id " +				
				" from AvaliacaoAtividade as av " +
				" inner join av.parecer as parecer " +
				" inner join av.avaliadorAtividadeExtensao aae " +
				" inner join aae.servidor as serv ");				
		
		StringBuilder hqlFiltros = new StringBuilder();
		hqlFiltros.append(" WHERE av.ativo = trueValue() ");
		
		if(idServidor != null) {
			hqlFiltros.append(" AND serv.id = :idServidor ");
		}
		
		if(idAtividade != null) {
			hqlFiltros.append(" AND av.atividade.id = :idAtividade ");
		}
		
		if(idTipoAvaliacao != null) {
			hqlFiltros.append(" AND av.tipoAvaliacao.id = :idTipoAvaliacao ");
		}
		
		hqlConsulta.append(hqlFiltros.toString());		
		hqlConsulta.append(" ORDER BY av.atividade.projeto.ano DESC ");
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());		

		// Populando valores nos filtros
		if (idServidor != null) {
			queryConsulta.setInteger("idServidor", idServidor);			
		}
		if (idAtividade != null) {
			queryConsulta.setInteger("idAtividade", idAtividade);		
		}
		if (idTipoAvaliacao != null) {
			queryConsulta.setInteger("idTipoAvaliacao", idTipoAvaliacao);		
		}
		
		List lista = queryConsulta.list();		

		Collection<AvaliacaoAtividade> result = new ArrayList<AvaliacaoAtividade>();
		
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;

			Object[] colunas = (Object[]) lista.get(a);
			AvaliacaoAtividade avalAtiv = new AvaliacaoAtividade();
			
			avalAtiv.setId( (Integer) colunas[col++] );
			avalAtiv.setNota((Double)colunas[col++]);
			
			avalAtiv.setParecer(new TipoParecerAvaliacaoExtensao());			
			avalAtiv.getParecer().setId( (Integer) colunas[col++] );
			avalAtiv.getParecer().setDescricao( (String) colunas[col++] );
			avalAtiv.setDataAvaliacao((Date) colunas[col++]);
			
			AtividadeExtensao ativ = new AtividadeExtensao();
			
			ativ.setTitulo( (String)colunas[col++] );			
			ativ.setAno( (Integer) colunas[col++] );			
			ativ.setSequencia((Integer) colunas[col++]);
			ativ.setDataInicio((Date) colunas[col++]);
			ativ.setDataFim((Date) colunas[col++]);
			ativ.getTipoAtividadeExtensao().setId((Integer) colunas[col++]);
			
			avalAtiv.setAtividade(ativ);
			
			result.add(avalAtiv);			
		}

		return result;
	}

	
	/**
	 * Retorna as avaliações de um membro da comissão.
	 * 
	 * 
	 * @param idServidor
	 * @param idAtividade
	 * @param idTipoAvaliacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<AvaliacaoAtividade> findByAvaliadorMembroComissao(
			Integer idServidor, Integer idAtividade, Integer idTipoAvaliacao)
			throws DAOException {
				
		StringBuilder hqlConsulta = new StringBuilder();
		
		
		hqlConsulta
		.append(" select av.id, av.nota, parecer.id, parecer.descricao, av.dataAvaliacao,  proj.titulo, proj.ano, atv.sequencia, " +
				" proj.dataInicio , proj.dataFim, atv.tipoAtividadeExtensao.id,  unidade.nome " +				
				" from AvaliacaoAtividade as av " +
				" inner join av.parecer as parecer " +
				" inner join av.atividade atv " +				
				" inner join atv.projeto proj " +
				" inner join proj.unidade as unidade " +
				" inner join av.membroComissao mc " +				
				" inner join mc.servidor as serv ");				
		
		StringBuilder hqlFiltros = new StringBuilder();
		hqlFiltros.append(" WHERE av.ativo = trueValue()  ");
		
		if(idServidor != null) {
			hqlFiltros.append(" AND serv.id = :idServidor ");
		}
		
		if(idAtividade != null) {
			hqlFiltros.append(" AND av.atividade.id = :idAtividade ");
		}
		
		if(idTipoAvaliacao != null) {
			hqlFiltros.append(" AND av.tipoAvaliacao.id = :idTipoAvaliacao ");
		}
		
		hqlConsulta.append(hqlFiltros.toString());		
		hqlConsulta.append(" ORDER BY proj.ano DESC ");
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());		

		// Populando valores nos filtros
		if (idServidor != null) {
			queryConsulta.setInteger("idServidor", idServidor);			
		}
		if (idAtividade != null) {
			queryConsulta.setInteger("idAtividade", idAtividade);		
		}
		if (idTipoAvaliacao != null) {
			queryConsulta.setInteger("idTipoAvaliacao", idTipoAvaliacao);		
		}
		
		List lista = queryConsulta.list();		

		Collection<AvaliacaoAtividade> result = new ArrayList<AvaliacaoAtividade>();
		
		for (int a = 0; a < lista.size(); a++) {
			int col = 0;

			Object[] colunas = (Object[]) lista.get(a);
			AvaliacaoAtividade avalAtiv = new AvaliacaoAtividade();
			avalAtiv.setParecer(new TipoParecerAvaliacaoExtensao());
			
			avalAtiv.setId( (Integer) colunas[col++] );
			avalAtiv.setNota((Double)colunas[col++]);
			avalAtiv.getParecer().setId( (Integer) colunas[col++] );
			avalAtiv.getParecer().setDescricao( (String) colunas[col++] );
			avalAtiv.setDataAvaliacao((Date) colunas[col++]);
			
			
			AtividadeExtensao ativ = new AtividadeExtensao();
			
			ativ.setTitulo( (String)colunas[col++] );			
			ativ.setAno( (Integer) colunas[col++] );			
			ativ.setSequencia((Integer) colunas[col++]);
			ativ.setDataInicio((Date) colunas[col++]);
			ativ.setDataFim((Date) colunas[col++]);
			ativ.getTipoAtividadeExtensao().setId((Integer) colunas[col++]);
			ativ.getUnidade().setNome((String) colunas[col++]);
			
			avalAtiv.setAtividade(ativ);
			
			result.add(avalAtiv);			
		}
			
		return result;
	}
	
	

	/**
	 * Retorna avaliação do presidente da comissão canceladas
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */	
	public AvaliacaoAtividade findAvaliacaoAcaoPresidenteByAtividade(
			int idAtividade) throws DAOException {
		Query q = getSession()
				.createQuery(
						"select ava from AvaliacaoAtividade ava "
								+ "where ava.tipoAvaliacao.id = :idTipo "
								+ "and ava.atividade.id = :idAtividade and ava.dataAvaliacao is null");

		q.setInteger("idAtividade", idAtividade);
		q.setInteger("idTipo", TipoAvaliacao.AVALIACAO_ACAO_PRESIDENTE_COMITE);

		return (AvaliacaoAtividade) q.uniqueResult();
	}
	

	/**
	 * Método utilizado na na busca geral de avalições pelos gestores de extensão.
	 * Utilizado para facilitar a gestão e acompanhar o andamento das avaliações
	 * realizadas.
	 * 
	 * @param tituloAtividade
	 * @param idTipoAtividade
	 * @param idUnidadeProponente
	 * @param idAreaCNPq
	 * @param idAreaTematicaPrincipal
	 * @param idServidorAtividade
	 * @param anoAtividade
	 * @param idTipoAvaliacao
	 * @param idServidorAvaliador
	 * @param idStatusAvaliacao
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<AvaliacaoAtividade> filter(String tituloAtividade,
			Integer idTipoAtividade, Integer idUnidadeProponente,
			Integer idAreaCNPq, Integer idAreaTematicaPrincipal,
			Integer idServidorAtividade, Integer anoAtividade,
			Integer idTipoAvaliacao, Integer idServidorAvaliador,
			Integer idStatusAvaliacao,
			Integer idEdital) throws DAOException, LimiteResultadosException {

	    
		StringBuilder hqlCount = new StringBuilder();
		hqlCount.append(" SELECT  count(distinct ava.id) " 
			+ " FROM AvaliacaoAtividade as ava "
			+ " LEFT JOIN ava.avaliadorAtividadeExtensao as a "
			+ " INNER JOIN ava.atividade as atv "
			+ " INNER JOIN atv.projeto as pj "
			+ " INNER JOIN pj.unidade as unidade "
			+ " INNER JOIN atv.tipoAtividadeExtensao as tae "
			+ " LEFT JOIN atv.editalExtensao edital "
			+ " LEFT JOIN pj.equipe as me "
			+ " LEFT JOIN a.servidor as s "
			+ " LEFT JOIN s.pessoa as p "
			+ " LEFT JOIN ava.membroComissao as mc "
			+ " LEFT JOIN mc.servidor as s1 "
			+ " LEFT JOIN s1.pessoa as p1 "
		);						
		hqlCount.append(" WHERE ava.ativo = trueValue() ");

		StringBuilder hqlConsulta = new StringBuilder();
		hqlConsulta.append(" SELECT distinct ava.id, parecer.id, parecer.descricao, ava.nota, ava.bolsasPropostas, "
						+ "ava.tipoAvaliacao.id, ava.tipoAvaliacao.descricao, "
						+ "ava.statusAvaliacao.id, ava.statusAvaliacao.descricao, "
						+ "atv.id, atv.sequencia, tae, pj.id, pj.ano, pj.titulo, "
						+ "pj.unidade.id, unidade.sigla, unidade.nome, "
						+ "a.id, s.id, p.id, p.nome, "
						+ "mc.id, s1.id, p1.id, p1.nome "
						+ " FROM AvaliacaoAtividade as ava "
						+ " LEFT JOIN ava.parecer as parecer "
						+ " LEFT JOIN ava.avaliadorAtividadeExtensao as a "
						+ " INNER JOIN ava.atividade as atv "
						+ " INNER JOIN atv.projeto as pj "
						+ " INNER JOIN pj.unidade as unidade "
						+ " INNER JOIN atv.tipoAtividadeExtensao as tae "
						+ " LEFT JOIN atv.editalExtensao edital "
						+ " LEFT JOIN pj.equipe as me "
						+ " LEFT JOIN a.servidor as s "
						+ " LEFT JOIN s.pessoa as p "
						+ " LEFT JOIN ava.membroComissao as mc "
						+ " LEFT JOIN mc.servidor as s1 "
						+ " LEFT JOIN s1.pessoa as p1 "
						);

		hqlConsulta.append(" WHERE ava.ativo = trueValue() ");

		StringBuilder hqlFiltros = new StringBuilder();

		// Filtros para a busca
		if (tituloAtividade != null) {
			hqlFiltros.append(" AND "
					+ UFRNUtils.toAsciiUpperUTF8("pj.titulo") + " like "
					+ UFRNUtils.toAsciiUpperUTF8(":tituloAtividade"));
		}
		if (idTipoAtividade != null) {
			hqlFiltros.append(" AND tae.id = :idTipoAtividade");
		}
		if (idEdital != null) {
			hqlFiltros.append(" AND edital.id = :idEdital");
		}
		if (idUnidadeProponente != null) {
			hqlFiltros.append(" AND unidade.id = :idUnidadeProponente");
		}
		if (idAreaCNPq != null) {
			hqlFiltros.append(" AND pj.areaConhecimentoCnpq.id = :idAreaCNPq");
		}
		if (idAreaTematicaPrincipal != null) {
			hqlFiltros.append(" AND atv.areaTematicaPrincipal.id = :idAreaTematicaPrincipal");
		}
		if (idServidorAtividade != null) {
			hqlFiltros.append(" AND me.servidor.id = :idServidorAtividade");
		}
		if (anoAtividade != null) {
			hqlFiltros.append(" AND pj.ano = :anoAtividade");
		}
		if (idTipoAvaliacao != null) {
			hqlFiltros.append(" AND ava.tipoAvaliacao.id = :idTipoAvaliacao");
		}
		if (idServidorAvaliador != null) {
			hqlFiltros.append(" AND (ava.membroComissao.servidor.id = :idServidorAvaliador "
							+ "or ava.avaliadorAtividadeExtensao.servidor.id = :idServidorAvaliador)");
		}

		if (idStatusAvaliacao != null) {
			hqlFiltros.append(" AND ava.statusAvaliacao.id = :idStatusAvaliacao");
		}

		hqlFiltros.append(" AND pj.situacaoProjeto.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO)");

		hqlCount.append(hqlFiltros.toString());
		hqlConsulta.append(hqlFiltros.toString());
		hqlConsulta.append(" ORDER BY pj.id, tae.id, ava.statusAvaliacao.id");

		// Criando consulta
		Query queryCount = getSession().createQuery(hqlCount.toString());
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

		Integer[] idsExtensaoGrupoInvalido = {TipoSituacaoProjeto.EXTENSAO_NAO_AUTORIZADO_DEPARTAMENTOS,TipoSituacaoProjeto.EXTENSAO_REMOVIDO, TipoSituacaoProjeto.EXTENSAO_REGISTRO_REPROVADO};
		Integer[] idsProjetoBaseGrupoInvalido = {TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO, TipoSituacaoProjeto.PROJETO_BASE_COORDENACAO_NEGOU_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_CANCELADO};
		// atividades não contabilizadas
		queryCount.setParameterList("EXTENSAO_GRUPO_INVALIDO", idsExtensaoGrupoInvalido);
		queryCount.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", idsProjetoBaseGrupoInvalido);
		
		queryConsulta.setParameterList("EXTENSAO_GRUPO_INVALIDO", idsExtensaoGrupoInvalido);
		queryConsulta.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", idsProjetoBaseGrupoInvalido);


		// Populando os valores dos filtros
		if (tituloAtividade != null) {
			queryCount.setString("tituloAtividade", "%" + tituloAtividade.toUpperCase() + "%");
			queryConsulta.setString("tituloAtividade", "%" + tituloAtividade.toUpperCase() + "%");
		}
		
		if (idTipoAtividade != null) {
			queryCount.setInteger("idTipoAtividade", idTipoAtividade);
			queryConsulta.setInteger("idTipoAtividade", idTipoAtividade);
		}

		if (idEdital != null) {
			queryCount.setInteger("idEdital", idEdital);
			queryConsulta.setInteger("idEdital", idEdital);
		}

		if (idUnidadeProponente != null) {
			queryCount.setInteger("idUnidadeProponente", idUnidadeProponente);
			queryConsulta.setInteger("idUnidadeProponente", idUnidadeProponente);
		}

		if (idAreaCNPq != null) {
			queryCount.setInteger("idAreaCNPq", idAreaCNPq);
			queryConsulta.setInteger("idAreaCNPq", idAreaCNPq);
		}

		if (idAreaTematicaPrincipal != null) {
			queryCount.setInteger("idAreaTematicaPrincipal", idAreaTematicaPrincipal);
			queryConsulta.setInteger("idAreaTematicaPrincipal", idAreaTematicaPrincipal);
		}

		if (idServidorAtividade != null) {
			queryCount.setInteger("idServidorAtividade", idServidorAtividade);
			queryConsulta.setInteger("idServidorAtividade", idServidorAtividade);
		}

		if (anoAtividade != null) {
			queryCount.setInteger("anoAtividade", anoAtividade);
			queryConsulta.setInteger("anoAtividade", anoAtividade);
		}

		if (idTipoAvaliacao != null) {
			queryCount.setInteger("idTipoAvaliacao", idTipoAvaliacao);
			queryConsulta.setInteger("idTipoAvaliacao", idTipoAvaliacao);
		}

		if (idServidorAvaliador != null) {
			queryCount.setInteger("idServidorAvaliador", idServidorAvaliador);
			queryConsulta.setInteger("idServidorAvaliador", idServidorAvaliador);
		}

		if (idStatusAvaliacao != null) {
			queryCount.setInteger("idStatusAvaliacao", idStatusAvaliacao);
			queryConsulta.setInteger("idStatusAvaliacao", idStatusAvaliacao);
		}

		Long total = (Long) queryCount.uniqueResult();

		if (total > LIMITE_RESULTADOS) {
			throw new LimiteResultadosException("A consulta retornou " + total + " resultados. Por favor, restrinja mais a busca.");
		}
		
		List lista = queryConsulta.list();

		// ava.id, ava.aprovado, ava.nota, ava.bolsasPropostas,
		// ava.tipoAvaliacao.id, ava.tipoAvaliacao.descricao,
		// ava.statusAvaliacao.id, ava.statusAvaliacao.descricao,
		// atv.id, atv.ano, atv.titulo, atv.unidade.id,
		// atv.unidade.sigla, atv.unidade.nome
		// a.id, s.id, p.id, p.nome " +
		// mc.id, s1.id, p1.id, p1.nome " +
		ArrayList<AvaliacaoAtividade> result = new ArrayList<AvaliacaoAtividade>();
		for (int a = 0; a < lista.size(); a++) {

			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);

			AvaliacaoAtividade ava = new AvaliacaoAtividade();
			ava.setId((Integer) colunas[col++]);
			
			if (colunas[1] != null) {
				ava.setParecer(new TipoParecerAvaliacaoExtensao());
				ava.getParecer().setId( (Integer) colunas[col++] );
				ava.getParecer().setDescricao( (String) colunas[col++] );
			}
			col = 3;
			ava.setNota((Double) colunas[col++]);
			ava.setBolsasPropostas((Integer) colunas[col++]);
			
			TipoAvaliacao tava = new TipoAvaliacao();
			tava.setId((Integer) colunas[col++]);
			tava.setDescricao((String) colunas[col++]);
			ava.setTipoAvaliacao(tava);

			StatusAvaliacao sava = new StatusAvaliacao();
			sava.setId((Integer) colunas[col++]);
			sava.setDescricao((String) colunas[col++]);
			ava.setStatusAvaliacao(sava);

			AtividadeExtensao at = new AtividadeExtensao();
			at.setId((Integer) colunas[col++]);
			at.setSequencia((Integer) colunas[col++]);
			at.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
			at.getProjeto().setId((Integer) colunas[col++]);
			at.setAno((Integer) colunas[col++]);
			at.setTitulo((String) colunas[col++]);

			Unidade unidade = new Unidade();
			unidade.setId((Integer) colunas[col++]);
			unidade.setSigla((String) colunas[col++]);
			unidade.setNome((String) colunas[col++]);
			at.setUnidade(unidade);

			ava.setAtividade(at);

			if (colunas[18] != null) {
				AvaliadorAtividadeExtensao avaliador = new AvaliadorAtividadeExtensao();
				avaliador.setId((Integer) colunas[col++]);
				Servidor s = new Servidor();
				s.setId((Integer) colunas[col++]);
				Pessoa p = new Pessoa();
				p.setId((Integer) colunas[col++]);
				p.setNome((String) colunas[col++]);
				s.setPessoa(p);
				avaliador.setServidor(s);
				ava.setAvaliadorAtividadeExtensao(avaliador);
			} else {
				ava.setAvaliadorAtividadeExtensao(null);
			}

			col = 22;
			if (colunas[22] != null) {
				MembroComissao mc = new MembroComissao();
				mc.setId((Integer) colunas[col++]);
				Servidor s1 = new Servidor();
				s1.setId((Integer) colunas[col++]);
				Pessoa p1 = new Pessoa();
				p1.setId((Integer) colunas[col++]);
				p1.setNome((String) colunas[col++]);
				s1.setPessoa(p1);
				mc.setServidor(s1);
				ava.setMembroComissao(mc);
			} else {
				ava.setMembroComissao(null);
			}

			result.add(ava);

		}

		return result;

	}
	
	/**
	 * Retorna avaliação do presidente da comissão
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */	
	public AvaliacaoAtividade findAvaliacaoPresidenteByAtividade(
			int idAtividade) throws DAOException {
		Query q = getSession()
				.createQuery("select ava from AvaliacaoAtividade ava "
								+ "where ava.tipoAvaliacao.id = :idTipo "
								+ "and ava.atividade.id = :idAtividade " +
										"and ava.dataAvaliacao is not null and ava.ativo = trueValue() " +
										"and ava.statusAvaliacao.id != :cancelada ");

		q.setInteger("idAtividade", idAtividade);
		q.setInteger("idTipo", TipoAvaliacao.AVALIACAO_ACAO_PRESIDENTE_COMITE);
		q.setInteger("cancelada", StatusAvaliacao.AVALIACAO_CANCELADA);
		return (AvaliacaoAtividade) q.uniqueResult();
	}
	
	
	/**
	 * Utilizado para classificação geral de ações de extensão.
	 * 
	 */
	public List<AtividadeExtensao> findClassificacaoByEdital(EditalExtensao edital) throws DAOException {
		Connection con = null;
		List<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();

		try {
			con = Database.getInstance().getSigaaConnection();

			String sql = "select "
			    
					+ "(select coalesce(sum(av.nota)/count(av.id_avaliacao_atividade),0) from extensao.avaliacao_atividade av " +
							"where " +
							"av.id_atividade_extensao=atv.id_atividade " +
							"and av.id_status_avaliacao = ? and av.id_tipo_avaliacao = ? and av.ativo = ? " +
							"and atv.ativo = trueValue()) as media_avaliacoes, " //<<<< considera só os avaliados AD HOC
							
					+ "array_to_string(array(select coalesce(av.nota, 0) from extensao.avaliacao_atividade av " +
							"where " +
							"av.id_atividade_extensao=atv.id_atividade " +
							"and av.id_status_avaliacao = ? and av.id_tipo_avaliacao = ? and av.ativo = ? " +
							"and atv.ativo = trueValue() ), ' / ') as notas_avaliacoes, " //<<<< considera só os avaliados AD HOC							
					
					+ "atv.total_discentes, atv.bolsas_solicitadas, "
					+ "atv.id_atividade, pr.id_projeto, pr.titulo, pr.ano, pr.id_unidade, u.sigla, pr.id_tipo_situacao_projeto, s.descricao, " 
					+ "pr.media, " 
					+ "area.id_area_tematica, area.descricao as descricao_area "
					
					+ "from extensao.atividade atv, extensao.area_tematica area, projetos.projeto pr, comum.unidade u, projetos.tipo_situacao_projeto s "
					+ "where pr.id_projeto = atv.id_projeto "
					+ "and atv.id_area_tematica_principal = area.id_area_tematica "
					+ "and atv.id_edital = ? "
					+ "and pr.id_tipo_situacao_projeto = ? "
					+ "and pr.id_unidade = u.id_unidade "
					+ "and pr.id_tipo_situacao_projeto = s.id_tipo_situacao_projeto "
					+ "and atv.ativo = trueValue() "
					
					+ "order by media_avaliacoes desc, atv.total_discentes desc, atv.bolsas_solicitadas desc ";  //<<<<< regra de desempate

			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, StatusAvaliacao.AVALIADO);
			st.setInt(2, TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA);
			st.setBoolean(3, Boolean.TRUE);
			st.setInt(4, StatusAvaliacao.AVALIADO);
			st.setInt(5, TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA);
			st.setBoolean(6, Boolean.TRUE);
			st.setInt(7, edital.getId());
			st.setInt(8, TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				AtividadeExtensao acao = new AtividadeExtensao();
				acao.setId(rs.getInt("id_atividade"));
				acao.setTitulo(rs.getString("titulo"));
				acao.setAno(rs.getInt("ano"));

				acao.setUnidade(new Unidade());
				acao.getUnidade().setId(rs.getInt("id_unidade"));
				acao.getUnidade().setSigla(rs.getString("sigla"));
				
				acao.getProjeto().setId(rs.getInt("id_projeto"));
				acao.getProjeto().setMedia(rs.getDouble("media_avaliacoes"));
				acao.setNotasAvaliacoes(rs.getString("notas_avaliacoes"));
				
				//verifica se a média de avaliação desta ação já foi calculada.
				Double media = rs.getDouble("media");
				if ((media == null) || (media == 0)) {
				    acao.getProjeto().setSelecionado(false);    
				}else {
				    acao.getProjeto().setSelecionado(true);
				}
				
				
				acao.setTotalDiscentes(rs.getInt("total_discentes"));
				acao.setBolsasSolicitadas(rs.getInt("bolsas_solicitadas"));
				
				
				acao.setAreaTematicaPrincipal(new AreaTematica(rs.getInt("id_area_tematica")));
				acao.getAreaTematicaPrincipal().setDescricao(rs.getString("descricao_area"));
				
				result.add(acao);
			}

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			Database.getInstance().close(con);
		}

		return result;
	}

}