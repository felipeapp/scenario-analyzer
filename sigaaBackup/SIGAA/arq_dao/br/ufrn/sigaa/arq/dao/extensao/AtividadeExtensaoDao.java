/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '21/12/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;   

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.ParametroBuscaAgregadorBolsas;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.RestricoesBuscaAgregadorBolsas;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AvaliacaoAtividade;
import br.ufrn.sigaa.extensao.dominio.AvaliadorAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.CursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAvaliacao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSubAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.extensao.relatorio.dominio.TipoRelatorioExtensao;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.pessoa.dominio.Docente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/*******************************************************************************
 * Dao para as consultas sobre a entidade AtividadeExtensao
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class AtividadeExtensaoDao extends GenericSigaaDAO {

	/** Limite de resultados da busca **/
	private static final long LIMITE_RESULTADOS = 1000;

	public AtividadeExtensaoDao() {
	}

	
	/**
	 * Retorna todas as informações da atividade de extensão para serem alteraddas
	 *
	 * @param idAtividade
	 * @return
	 * @throws DAOException
	 */
	public AtividadeExtensao findInformacoesAlteracaoAtividadeExtensao(int idAtividade) throws DAOException{
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT atividade.id as idAtividade, atividade.sequencia, atividade.tipoAtividadeExtensao, projeto.id, projeto.titulo, projeto.ano, ");
		hql.append(" projeto.dataInicio, projeto.dataFim, coordenador.id, pessoa.id, pessoa.nome ");
		
		hql.append(" FROM AtividadeExtensao atividade " +
				" JOIN atividade.projeto projeto " +
				" JOIN projeto.coordenador coordenador " +
				" JOIN coordenador.pessoa pessoa " +
				" JOIN coordenador.funcaoMembro funcao ");
		
		hql.append(" WHERE atividade.id = :idAtividade"); 
		
		// APENAS PERÍODOS DE INSCRIÇÃO DE ATIVIDADES E PROJETOS QUE CONTINUEM ATIVOS  //
		hql.append(" AND atividade.ativo = trueValue() AND projeto.ativo = trueValue()");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idAtividade", idAtividade);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		
		AtividadeExtensao atividade = null;
		
		for (Object[] colunas : lista) {
		    
			atividade = new AtividadeExtensao((Integer) colunas[0]);
		    
			atividade.setSequencia( (Integer) colunas[1]);
		    atividade.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[2]);
		    atividade.setAtivo(true);
		    atividade.setProjeto(new Projeto((Integer) colunas[3]));
		    atividade.getProjeto().setTitulo((String) colunas[4]);
		    atividade.getProjeto().setAno((Integer) colunas[5]);
		    atividade.getProjeto().setDataInicio( (Date) colunas[6] );
		    atividade.getProjeto().setDataFim( (Date) colunas[7] );
		    atividade.getProjeto().setAtivo(true);
		    
		    atividade.getProjeto().setCoordenador(new MembroProjeto((Integer) colunas[8]));
		    atividade.getProjeto().getCoordenador().setPessoa(new Pessoa((Integer) colunas[9]));
		    atividade.getProjeto().getCoordenador().getPessoa().setNome(((String) colunas[10]));
		    
		    
		}
		
		return atividade;
	}
	

	/**
	 * Retorna uma coleção de objetos não vazia se o servidor (idServidor) é coordenador de uma ação de extensão
	 * e não enviou o relatório final após uma quantidade máxima de dias permitidos.
	 * 
	 * @param idServidor
	 * @param diasPermitidosAtrasoRelatorio
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Object> projetosCoordenadorNaoEnviouRelatorio(Integer idServidor, Integer diasPermitidosAtrasoRelatorio, Integer... idsTiposAcoesComRelatoriosDefinidos)
		throws HibernateException, DAOException {
		
		String hql = null;
		
		hql = (" Select distinct p.titulo " +
				   " From Projeto p " +				   
				   " where p.id not in " +
				                       " ( " +
				                           "Select distinct p.id " +				                       
				                           " From RelatorioAcaoExtensao r " +
				                           " inner join r.atividade a " +
				                           " inner join a.projeto p " +
				                           " where r.dataEnvio is not null and r.tipoParecerDepartamento.id = :APROVADO and r.tipoParecerProex.id = :APROVADO " +
				                           "      and r.tipoRelatorio.id = :tipoRelatorio and r.ativo = trueValue() " +
				                       " ) " +
				           " and p.coordenador.servidor.id = :idServidor " +
				           " and p.dataFim < now() and ((p.dataFim + :diasPermitidosAtrasoRelatorio) < now()) " +
				           " and p.ativo = trueValue() " +
				           " and p.situacaoProjeto.id = :idSituacaoProjeto " +
				           " and p.id in (select a.projeto.id from AtividadeExtensao a " +
				           "              where a.tipoAtividadeExtensao.id in (:idsTiposAcoesComRelatoriosDefinidos)" +
				           "			  AND (a.projeto.ensino = falseValue() AND a.projeto.pesquisa = falseValue() AND a.projeto.extensao = falseValue())	) ");
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idServidor", idServidor);
		query.setInteger("APROVADO", TipoParecerAvaliacaoExtensao.APROVADO);
		query.setInteger("diasPermitidosAtrasoRelatorio", diasPermitidosAtrasoRelatorio);
		query.setInteger("tipoRelatorio", TipoRelatorioExtensao.RELATORIO_FINAL);
		query.setParameterList("idsTiposAcoesComRelatoriosDefinidos", idsTiposAcoesComRelatoriosDefinidos);
		query.setInteger("idSituacaoProjeto", TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO);		
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();
		
		return lista;
	}
	
	/**
	 * Retorna as atividades de um discente.
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */	
	public Collection<AtividadeExtensao> findByDiscente(Integer idDiscente)
	throws DAOException {
		
		try {
			
			String hql = null;
			
			hql = " SELECT distinct a.id, p.ano, p.titulo, p.dataCadastro ,t.id, t.descricao, s.descricao, s.id, a.sequencia  "
				+ " FROM AtividadeExtensao a  "
				+ " INNER JOIN a.projeto as p " 
				+ " INNER JOIN p.equipe m "
				+ " INNER JOIN a.tipoAtividadeExtensao t  "
				+ " INNER JOIN p.situacaoProjeto s  "
				+ " WHERE m.discente.id = :idDiscente "
				+ " AND s.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +
				  " AND (p.tipoProjeto.id = :idTipoProjeto) ";
				
			
			Query query = getSession().createQuery(hql);
			query.setInteger("idDiscente", idDiscente);
			query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
			query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);

			@SuppressWarnings("unchecked")
			List<Object> lista = query.list();
			
			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
			
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				AtividadeExtensao at = new AtividadeExtensao();
				at.setId((Integer) colunas[col++]);
				at.setAno((Integer) colunas[col++]);
				at.setTitulo((String) colunas[col++]);
				at.setDataCadastro((Date) colunas[col++]);
				
				TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();
				int idTipo = (Integer) colunas[col++];
				String descricao = (String) colunas[col++];
				
				if (descricao != null) {
					tipo.setId(idTipo);
					tipo.setDescricao(descricao);
				}
				
				at.setTipoAtividadeExtensao(tipo);
				TipoSituacaoProjeto sit = new TipoSituacaoProjeto();
				String desc = (String) colunas[col++];
				
				if (desc != null) {					
					sit.setDescricao(desc);
					sit.setId((Integer) colunas[col++]);
				}
				
				at.setSequencia((Integer) colunas[col++]);
				at.setSituacaoProjeto(sit);
				result.add(at);
			}
			
			return result;
		}
		catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}		
	}

	/**
	 * Retorna as ações de extensão ATIVAS nas quais a pessoa faz parte.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */	
	public Collection<AtividadeExtensao> findAtivasByPessoa(Pessoa pessoa) throws DAOException {

		try {

			StringBuilder hqlConsulta = new StringBuilder();
			StringBuilder hqlFiltros = new StringBuilder();

			hqlConsulta.append("SELECT distinct a.id, projeto.id, projeto.ano, projeto.titulo, coord.id, pess.id, pess.nome, t.id, t.descricao, s.descricao, s.id, a.sequencia  "
					+" , subAtividade.id, subAtividade.titulo, tipoSubAtividade.descricao, subAtividade.ativo "
					+ "FROM AtividadeExtensao a  "
					+ "JOIN a.projeto projeto " 
					+ "LEFT JOIN projeto.coordenador coord "					
					+ "LEFT JOIN coord.pessoa pess "
					+ "JOIN projeto.equipe m "
					+ "JOIN a.tipoAtividadeExtensao t  "
					+ "JOIN projeto.situacaoProjeto s  "
					+ "LEFT JOIN a.subAtividadesExtensao subAtividade "
					+ "LEFT JOIN subAtividade.tipoSubAtividadeExtensao tipoSubAtividade "
					+ "WHERE a.ativo = trueValue() " + // É preciso verifica porque as vezes uma atividade de extensão é desabilitada diretamente no banco, 
					                                   // mas o projeto continua ativo. SEMPRE aplique o maior número de restrições possíveis para evitar erros no sistema.
					" AND projeto.ativo = trueValue() AND m.ativo = trueValue() " +
					" AND s.id not in (:EXTENSAO_REMOVIDO, :PROJETO_BASE_REMOVIDO) " +
					" AND (projeto.tipoProjeto.id = :idTipoProjeto) ");
			
			if(pessoa != null) {
				hqlFiltros.append(" and m.pessoa.id = :idPessoa ");
			}
			hqlFiltros.append(" ORDER BY projeto.ano desc, s.id");
			
			hqlConsulta.append(hqlFiltros);
			Query query = getSession().createQuery(hqlConsulta.toString());
			query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
			
			if(pessoa != null) {
				query.setInteger("idPessoa", pessoa.getId());
			}
			
			query.setInteger("EXTENSAO_REMOVIDO", TipoSituacaoProjeto.EXTENSAO_REMOVIDO);
			query.setInteger("PROJETO_BASE_REMOVIDO", TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO);

			@SuppressWarnings("unchecked")
			List<Object> lista = query.list();

			ArrayList<AtividadeExtensao> atividadesExtensaoRetorno = new ArrayList<AtividadeExtensao>();
			
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				
				AtividadeExtensao at = new AtividadeExtensao();
				at.setId((Integer) colunas[col++]);
				
				if( atividadesExtensaoRetorno.contains(at)){
					at = atividadesExtensaoRetorno.get(atividadesExtensaoRetorno.indexOf(at));
				}
				
				at.getProjeto().setId((Integer) colunas[col++]);
				at.setAno((Integer) colunas[col++]);
				at.setTitulo((String) colunas[col++]);
				
				Integer idCoord = (Integer)colunas[col++];
				if (idCoord != null) {
					at.getProjeto().setCoordenador(new MembroProjeto(idCoord));
					at.getProjeto().getCoordenador().setPessoa(new Pessoa((Integer)colunas[col++]));
					at.getProjeto().getCoordenador().getPessoa().setNome((String)colunas[col++]);
				}

				TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();
				col = 7;
				tipo.setId((Integer) colunas[col++]);
				tipo.setDescricao((String) colunas[col++]);

				at.setTipoAtividadeExtensao(tipo);
				TipoSituacaoProjeto sit = new TipoSituacaoProjeto();
				sit.setDescricao((String) colunas[col++]);
				sit.setId((Integer) colunas[col++]);
				
				at.setSituacaoProjeto(sit);
				at.getProjeto().setSituacaoProjeto(sit);
				at.setSequencia((Integer) colunas[col++]);
				
				if((Integer) colunas[col] != null && (Boolean) colunas[col+3] == true){ // se a atividade tem sub atividade e ela está ativa
					
					SubAtividadeExtensao subAtividade = new SubAtividadeExtensao();
					subAtividade.setId((Integer) colunas[col++]);
					subAtividade.setTitulo((String) colunas[col++]);
					subAtividade.setTipoSubAtividadeExtensao( new TipoSubAtividadeExtensao((String) colunas[col++]));
					at.addSubAtividade(subAtividade);
				}
				
				if( ! atividadesExtensaoRetorno.contains(at)){
					atividadesExtensaoRetorno.add(at);
				}
				
			}
			return atividadesExtensaoRetorno;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	
	/**
	 * Busca todas as ações de extensão do servidor por função exceto cursos de extensão
	 * @param servidor
	 * @param funcao
	 * @param anoInicio
	 * @param anoFim
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> findByServidorFuncaoPeriodoExcetoCurso(
			Servidor servidor, Integer funcao, Integer anoInicio, Integer anoFim, Integer periodoInicio, Integer periodoFim)
			throws DAOException {
		return findByServidorFuncaoPeriodoExcetoCurso(servidor, new Integer[] {funcao}, anoInicio, anoFim, periodoInicio, periodoFim);
	}

	/**
	 * Busca todas as ações de extensão do servidor por função exceto cursos de extensão
	 * 
	 * @param servidor
	 *            Servidor que faz parte da Ação
	 * @param funcoes
	 *            Função do servidor
	 * @param anoInicio
	 *            ano de início da ação de extensão
	 * @param anoFim
	 *            ano do fim da ação de extensão
	 * @return
	 * @throws DAOException
	 */	
	@SuppressWarnings("unchecked")
	public Collection<AtividadeExtensao> findByServidorFuncaoPeriodoExcetoCurso(Servidor servidor, Integer[] funcoes,
			Integer anoInicio, Integer anoFim, Integer periodoInicio, Integer periodoFim) throws DAOException {

		try {
			String hql = "select distinct atividade from AtividadeExtensao atividade"
						+ " inner join atividade.projeto.equipe membro inner join membro.funcaoMembro funcao"
						+ " where membro.servidor.id = :idServidor and atividade.tipoAtividadeExtensao.id != " 
						+ TipoAtividadeExtensao.CURSO;
					
					if ( periodoInicio != null && periodoFim != null ) 
						hql += String.format(" and (%1$s = %3$s or (%1$s > %3$s and %1$s <= %4$s) or (%1$s < %3$s and (%3$s <= %2$s or %2$s is null)))", 
								" atividade.projeto.dataInicio", "atividade.projeto.dataFim", ":dataInicial", ":dataFinal"); 
					else
						hql += " and atividade.projeto.dataInicio >= " + anoInicio + " and atividade.projeto.ano <= " + anoFim;
					
					hql += " and atividade.projeto.situacaoProjeto.id in (:SITUACOES_VALIDAS_RID)" 
						+ " and atividade.projeto.ativo = trueValue()" 
						+ "  and funcao.id in (:FUNCOES)";
			
			Query query = getSession().createQuery(hql);
			query.setInteger("idServidor", servidor.getId());
			if ( periodoInicio != null && periodoFim != null ) {
				query.setDate("dataInicial", CalendarUtils.createDate(periodoInicio == 1 ? 1 : 30 , periodoInicio == 1 ? 0 : 5, anoInicio));
				query.setDate("dataFinal", CalendarUtils.createDate(periodoFim == 1 ? 30 : 31 , periodoFim == 1 ? 5 : 11, anoFim));
			}
			query.setParameterList("SITUACOES_VALIDAS_RID", TipoSituacaoProjeto.SITUACOES_VALIDAS_RID);
			query.setParameterList("FUNCOES", funcoes);

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * <p>
	 * Retorna as atividades de extensão ativas da qual o servidor faz parte
	 * como coordenador.
	 * </p>
	 * <p>
	 * Utilizado na verificação do servidor que não pode participar de mais de duas
	 * atividades ativas de extensão como coordenador no mesmo ano e na área
	 * pública de extensão
	 * </p>
	 * <p>
	 * Exclui da consulta as ações Reprovadas, Com registro reprovado, Não
	 * autorizadas pelo departamento, Removidas, etc
	 * </p>
	 * 
	 * @param servidor
	 * @param funcao
	 * @return
	 * @throws DAOException
	 */	
	@SuppressWarnings("unchecked")
	public Collection<AtividadeExtensao> findAtivasByServidor(Servidor servidor, Integer ano, Integer idFuncao)
			throws DAOException {

		try {

			StringBuffer hql = new StringBuffer();

			hql
					.append(" select atividade from AtividadeExtensao atividade "
							+ "inner join atividade.projeto.equipe membro "
							+ "inner join membro.funcaoMembro funcao "
							+ "where membro.servidor.id = :idServidor and membro.ativo = trueValue() "
							+ " and (atividade.projeto.situacaoProjeto.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO))" 
							+ " AND (atividade.projeto.tipoProjeto.id = :idTipoProjeto) ");

			if (ano != null) {
				hql.append(" AND (extract(year from atividade.projeto.dataInicio) <= :ano and extract(year from atividade.projeto.dataFim) >= :ano) ");
			}

			if (idFuncao != null) {
				hql.append(" and funcao.id = :idFuncao ");
			}

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idServidor", servidor.getId());
			query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);

			if (idFuncao != null) {
				query.setInteger("idFuncao", idFuncao);
			}

			query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

			if (ano != null) {
				query.setInteger("ano", ano);
			}

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}


	/**
	 * Retorna as atividades de extensão com uma situação específica
	 * da qual o servidor participa como coordenador.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> findByCoordenadorAtivo(Docente servidor, Integer[] idsSituacoesAcao) throws DAOException {
		StringBuilder hql = new StringBuilder();

		hql.append(" select distinct a.id, a.ativo, projeto.ano, projeto.titulo, projeto.ativo, " +
				"projeto.dataInicio, projeto.dataFim, a.registro, " +
				"tip.id, tip.descricao, sitpro.id, sitpro.descricao, sitat.id, " +
				"sitat.descricao, a.sequencia, coordenador " +
			" FROM AtividadeExtensao a " +
			" JOIN a.projeto projeto " +
			" JOIN projeto.coordenador coordenador" +
			" JOIN projeto.situacaoProjeto sitpro " +
			" JOIN a.situacaoProjeto sitat " +
			" JOIN a.tipoAtividadeExtensao tip  " +
			" WHERE (( coordenador.servidor.id = :idServidor AND coordenador.ativo = trueValue() ) or projeto.id in ( :idProjetos ) ) " +
			" AND projeto.situacaoProjeto.id NOT IN (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +
			" AND (projeto.tipoProjeto.id = :idTipoProjeto) "
		);
		
		if (idsSituacoesAcao != null) {
		    hql.append("and projeto.situacaoProjeto.id in (:idsSituacoesAcao) ");
		}

		hql.append("order by projeto.ano desc, a.id");			
		Query query = getSession().createQuery(hql.toString());

		query.setInteger("idServidor", servidor.getId());
		
		if (DesignacaoFuncaoProjetoHelper.projetosByCoordenadoresOrDesignacaoCoordenador(servidor.getId()).size() > 0){
			query.setParameterList("idProjetos", DesignacaoFuncaoProjetoHelper.projetosByCoordenadoresOrDesignacaoCoordenador(servidor.getId()));
		}
		// Caso o servidor não participe de nenhuma ação de extensão como coordenador
		else {
			Collection<Integer> lista0 = new ArrayList<Integer>();
			lista0.add(new Integer(0));
			query.setParameterList("idProjetos", lista0);
		}
		
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

		if (idsSituacoesAcao != null) {
		    query.setParameterList("idsSituacoesAcao", idsSituacoesAcao);
		}
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();

		ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
		for (int a = 0; a < lista.size(); a++) {

			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);

			AtividadeExtensao at = new AtividadeExtensao();
			at.setId((Integer) colunas[col++]);
			at.setAtivo((Boolean) colunas[col++]);
			at.getProjeto().setAno((Integer) colunas[col++]);
			at.getProjeto().setTitulo((String) colunas[col++]);
			at.getProjeto().setAtivo((Boolean) colunas[col++]);
			at.getProjeto().setDataInicio((Date) colunas[col++]);
			at.getProjeto().setDataFim((Date) colunas[col++]);
			at.setRegistro((Boolean) colunas[col++]);

			TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();

			int idTipo = (Integer) colunas[col++];
			String descricao = (String) colunas[col++];
			if (descricao != null) {
				tipo.setId(idTipo);
				tipo.setDescricao(descricao);
			}
			at.setTipoAtividadeExtensao(tipo);

			TipoSituacaoProjeto sitpro = new TipoSituacaoProjeto();
			sitpro.setId((Integer) colunas[col++]);
			sitpro.setDescricao((String) colunas[col++]);
			
			at.getProjeto().setSituacaoProjeto(sitpro);
			TipoSituacaoProjeto sitat = new TipoSituacaoProjeto();
			sitat.setId((Integer) colunas[col++]);
			sitat.setDescricao((String) colunas[col++]);
			at.setSituacaoProjeto(sitat);

			at.setSequencia((Integer) colunas[col++]);
			at.getProjeto().setCoordenador((MembroProjeto) colunas[col++]);

			result.add(at);
		}
		return result;
	}

	/**
	 * Retorna as ações de extensão com cadastro em andamento gravadas pelo
	 * usuário informado.
	 * 
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */	
	public Collection<AtividadeExtensao> findAtivasGravadosByUsuario(Usuario usuario)
			throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();
			hql
					.append("SELECT a.id, a.projeto.ano, a.projeto.titulo, a.registro, tip.id, tip.descricao, sitpro.id, sitpro.descricao, sitat.id, sitat.descricao, a.sequencia "
							+" , subAtividade.id, subAtividade.titulo, tipoSubAtividade.descricao, subAtividade.ativo "
							+ " from AtividadeExtensao a "
							+ " INNER JOIN a.projeto.registroEntrada reg  "
							+ " INNER JOIN a.projeto.situacaoProjeto sitpro "
							+ " INNER JOIN a.situacaoProjeto sitat "
							+ " INNER JOIN a.tipoAtividadeExtensao tip  " 
							+ " LEFT JOIN a.subAtividadesExtensao subAtividade "
							+ " LEFT JOIN subAtividade.tipoSubAtividadeExtensao tipoSubAtividade "
							+ " WHERE a.ativo = trueValue() AND a.projeto.ativo = trueValue() " // SEMPRE aplique o maior número de restrições possíveis para evitar erros no sistema.
							+ " AND reg.usuario.id = :idUsuario AND sitat.id = :idSituacao" 
							+ " AND (a.projeto.tipoProjeto.id = :idTipoProjeto) ");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idUsuario", usuario.getId());
			query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
			query.setInteger("idSituacao", TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO);

			@SuppressWarnings("unchecked")
			List<Object> lista = query.list();

			ArrayList<AtividadeExtensao> atividadesExtensaoRetorno = new ArrayList<AtividadeExtensao>();
			
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				AtividadeExtensao at = new AtividadeExtensao();
				at.setId((Integer) colunas[col++]);
				
				if( atividadesExtensaoRetorno.contains(at)){
					at = atividadesExtensaoRetorno.get(atividadesExtensaoRetorno.indexOf(at));
				}
				
				at.setAno((Integer) colunas[col++]);
				at.setTitulo((String) colunas[col++]);
				at.setRegistro((Boolean) colunas[col++]);

				TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();

				int idTipo = (Integer) colunas[col++];
				String descricao = (String) colunas[col++];
				if (descricao != null) {
					tipo.setId(idTipo);
					tipo.setDescricao(descricao);
				}

				at.setTipoAtividadeExtensao(tipo);

				TipoSituacaoProjeto sitpro = new TipoSituacaoProjeto();
				sitpro.setId((Integer) colunas[col++]);
				sitpro.setDescricao((String) colunas[col++]);
				
				at.getProjeto().setSituacaoProjeto(sitpro);
				TipoSituacaoProjeto sitat = new TipoSituacaoProjeto();
				sitat.setId((Integer) colunas[col++]);
				sitat.setDescricao((String) colunas[col++]);
				at.setSituacaoProjeto(sitat);

				at.setSequencia((Integer) colunas[col++]);

				if((Integer) colunas[col] != null && (Boolean) colunas[col+3] == true){ // se a atividade tem sub atividade e ela está ativa
					
					SubAtividadeExtensao subAtividade = new SubAtividadeExtensao();
					subAtividade.setId((Integer) colunas[col++]);
					subAtividade.setTitulo((String) colunas[col++]);
					subAtividade.setTipoSubAtividadeExtensao( new TipoSubAtividadeExtensao((String) colunas[col++]));
					at.addSubAtividade(subAtividade);
				}
				
				if( ! atividadesExtensaoRetorno.contains(at)){
					atividadesExtensaoRetorno.add(at);
				}
			}
			
			return atividadesExtensaoRetorno;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	
	/**
	 * Método usado para exibição de informações dos relatórios(parcial e final)
	 * de ações de extensão de acordo com o período de conclusão. 
	 * 
	 * @param inicioConclusao
	 * @param fimConclusao
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> findByPeriodoConclusao(Date inicioConclusao, Date fimConclusao) throws HibernateException, DAOException {
		
		
		StringBuilder hql = new StringBuilder();
		
		hql.append(" select distinct atv.id, atv.dataEmailCobrancaRelatorioFinal, atv.sequencia, proj.id, tipoAtiv.id,tipoAtiv.descricao, proj.ano, proj.titulo, situacaoProj.id, " +
				   "        situacaoProj.descricao, proj.extensao, proj.dataInicio, proj.dataFim, pessoa.nome, pessoa.email, " +
				   "        rel.id, rel.dataEnvio, rel.dataValidacaoDepartamento, rel.dataValidacaoProex, tipoRel.descricao, tipoRel.id, rel.ativo " +
				   " from AtividadeExtensao atv " +
				   " inner join atv.projeto proj " +
				   " inner join atv.tipoAtividadeExtensao tipoAtiv " +
				   " inner join proj.situacaoProjeto situacaoProj  " +
				   " inner join proj.coordenador as coord " +
				   " inner join coord.pessoa as pessoa " +
				   " left  join atv.relatorios rel " +
				   " left  join rel.tipoRelatorio tipoRel " +
				   " where (proj.dataFim >= :inicioConclusao AND proj.dataFim <= :fimConclusao) and situacaoProj.id IN (:SITUACOES_PROJETO) " +
				   " and (proj.tipoProjeto.id = :idTipoProjeto) " + 
				   " order by  proj.ano DESC, proj.id, situacaoProj.id ");		
		
		Query query = getSession().createQuery(hql.toString());		
		query.setParameterList("SITUACOES_PROJETO", new Integer[] {TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, TipoSituacaoProjeto.EXTENSAO_CONCLUIDO});
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		
		if ((inicioConclusao != null) && (fimConclusao != null)) {
			query.setDate("inicioConclusao", inicioConclusao);		
			query.setDate("fimConclusao", fimConclusao);
		}			
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();

		ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
		
		for (int cont = 0; cont < lista.size(); cont++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(cont);
			
			AtividadeExtensao atv = new AtividadeExtensao();
			
			atv.setId((Integer) colunas[col++]);
			atv.setDataEmailCobrancaRelatorioFinal((Date) colunas[col++]);
			atv.setSequencia((Integer) colunas[col++]);
			atv.getProjeto().setId((Integer) colunas[col++]);
			atv.getTipoAtividadeExtensao().setId((Integer) colunas[col++]);
			atv.getTipoAtividadeExtensao().setDescricao((String) colunas[col++]);
			atv.getProjeto().setAno((Integer) colunas[col++]);
			atv.getProjeto().setTitulo((String) colunas[col++]);
			atv.getProjeto().getSituacaoProjeto().setId((Integer) colunas[col++]);
			atv.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[col++]);
			atv.getProjeto().setExtensao((Boolean) colunas[col++]);
			atv.getProjeto().setDataInicio((Date) colunas[col++]);
			atv.getProjeto().setDataFim((Date) colunas[col++]);
			atv.getProjeto().setCoordenador(new MembroProjeto());
			atv.getProjeto().getCoordenador().getPessoa().setNome((String) colunas[col++]);
			atv.getProjeto().getCoordenador().getPessoa().setEmail((String) colunas[col++]);
			
			//Enquanto for  a mesma ação, adicione os relatórios.
			while(atv.getId() == (Integer) colunas[0])   {	
				
				col = 15;
				if(  colunas[15] != null) {
					RelatorioAcaoExtensao rel = new RelatorioAcaoExtensao();

					rel.setId((Integer) colunas[col++]);
					rel.setDataEnvio((Date) colunas[col++]);
					rel.setDataValidacaoDepartamento((Date) colunas[col++]);
					rel.setDataValidacaoProex((Date) colunas[col++]);
					rel.getTipoRelatorio().setDescricao((String) colunas[col++]);
					rel.getTipoRelatorio().setId((Integer) colunas[col++]);
					rel.setAtivo((Boolean) colunas[col++]);

					atv.getRelatorios().add(rel);
				}
				
				cont++;
				if( cont < lista.size())
					colunas = (Object[]) lista.get(cont);
				else
					break;
			}			
			result.add(atv);
		}
		return result;		
	}

	/**
	 * Método usado para acessar os projetos que possuem Coordenador associado,
	 * trazendo seus dados para contato como e-mail, telefone, etc.
	 * 
	 * @param idEdital
	 * @param inicio
	 * @param fim
	 * @param titulo
	 * @param idTipoAtividade
	 * @param idSituacaoAtividade
	 * @param idUnidadeProponente
	 * @param idCentro
	 * @param idAreaCNPq
	 * @param idAreaTematicaPrincipal
	 * @param idServidor
	 * @param ano
	 * @param financiamentoInterno
	 * @param financiamentoExterno
	 * @param autoFinanciamento
	 * @param convenioFunpec
	 * @param paginacao
	 * @param verCadastrosEmAndamento
	 * @param registro
	 * @return
	 * @throws DAOException
	 */	
	public Collection<AtividadeExtensao> gerarDadosContatoCoordenador(
			Integer idEdital,
			Date inicio,
			Date fim,
			String titulo,
			Integer[] idTipoAtividade,
			Integer[] idSituacaoAtividade,
			Integer idUnidadeProponente, Integer idCentro, Integer idAreaCNPq,
			Integer idAreaTematicaPrincipal, Integer idServidor, Integer ano,
			Boolean financiamentoInterno, Boolean financiamentoExterno,
			Boolean autoFinanciamento, Boolean convenioFunpec, Boolean recebeuFinanciamentoInterno,
			PagingInformation paginacao, Boolean verCadastrosEmAndamento,
			Boolean registro) throws DAOException {

		try {

			StringBuilder hqlCount = new StringBuilder();
			hqlCount.append(" SELECT  count(distinct atv.id) "
					+ " FROM AtividadeExtensao atv "
					+ " LEFT JOIN atv.editalExtensao edital "
					+ " INNER JOIN atv.projeto pj " 
					+ " LEFT JOIN pj.equipe as me "
					);
			hqlCount.append(" WHERE 1 = 1 ");

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
					.append(" SELECT distinct atv.id, pj.dataInicio, pj.dataFim, atv.publicoEstimado, "
							+ " pj.financiamentoInterno, pj.financiamentoExterno, pj.autoFinanciado, "
							+ " atv.convenioFunpec, atv.editalExtensao, pj.ano, pj.titulo, pj.unidade.id, "
							+ " pj.unidade.sigla, "
							+ " pj.unidade.nome, pj.situacaoProjeto.id, pj.situacaoProjeto.descricao, "
							+ " atv.tipoAtividadeExtensao.id, atv.tipoAtividadeExtensao.descricao,"
							+ " atv.areaTematicaPrincipal.id, atv.areaTematicaPrincipal.descricao, atv.sequencia, me.pessoa "
							+ " FROM AtividadeExtensao atv "
							+ " LEFT JOIN atv.editalExtensao edital "
							+ " INNER JOIN atv.projeto pj " 
							+ " LEFT JOIN pj.equipe as me "
							);

			hqlConsulta.append(" WHERE 1 = 1 and me.funcaoMembro.id = 1 ");

			StringBuilder hqlFiltros = new StringBuilder();
			// Filtros para a busca
			if (titulo != null) {
				hqlFiltros.append(" AND "
						+ UFRNUtils.toAsciiUpperUTF8("pj.titulo") + " like "
						+ UFRNUtils.toAsciiUTF8(":titulo"));
			}

			if (idEdital != null) {
				hqlFiltros.append(" AND edital.id = :idEditalExtensao ");
			}

			if ((inicio != null) && (fim != null)) {
				hqlFiltros.append(" AND " + HibernateUtils.generateDateIntersection("pj.dataInicio", "pj.dataFim", ":inicio", ":fim"));
						//.append(" AND  ( (atv.dataInicio >= :inicio) and (atv.dataFim <= :fim) )");
			}

			if (idTipoAtividade[0] != null) {
				hqlFiltros
						.append(" AND atv.tipoAtividadeExtensao.id IN (:idTipoAtividade) ");
			}

			// A busca pode ser feita por mais de uma SituacaoAtividade ao mesmo
			// tempo, exemplo: 103, 105, 110
			if (idSituacaoAtividade.length > 0) {
				// Colocando os IDSituacao entre vírgula para usar na cláusula
				// SQL IN()
				String situacoes = "";
				for (Integer idSituacao : idSituacaoAtividade)
					situacoes += idSituacao + ",";
				// Retirando a última vírgula
				situacoes = situacoes.substring(0, situacoes.length() - 1);
				hqlFiltros.append(" AND pj.situacaoProjeto.id IN ("
						+ situacoes + ") ");
			}
			// ---//

			if (idUnidadeProponente != null) {
				hqlFiltros.append(" AND pj.unidade.id = :idUnidadeProponente ");
			}

			if (idCentro != null) {
				hqlFiltros.append(" AND pj.unidade.gestora.id = :idCentro ");
			}

			if (idAreaCNPq != null) {
				hqlFiltros.append(" AND pj.areaConhecimentoCnpq.id = :idAreaCNPq ");
			}

			if (idAreaTematicaPrincipal != null) {
				hqlFiltros.append(" AND atv.areaTematicaPrincipal.id = :idAreaTematicaPrincipal ");
			}

			if (idServidor != null) {
				hqlFiltros.append(" AND me.servidor.id = :idServidor ");
			}

			if (ano != null) {
				hqlFiltros.append(" AND (extract(year from pj.dataInicio) <= :ano and extract(year from pj.dataFim) >= :ano) ");
			}

			if (financiamentoInterno != null) {
				hqlFiltros.append(" AND pj.financiamentoInterno = :financiamentoInterno ");
			}

			if (financiamentoExterno != null) {
				hqlFiltros.append(" AND pj.financiamentoExterno = :financiamentoExterno ");
			}

			if (autoFinanciamento != null) {
				hqlFiltros.append(" AND pj.autoFinanciado = :autoFinanciamento ");
			}

			if (convenioFunpec != null) {
				hqlFiltros.append(" AND atv.convenioFunpec = :convenioFunpec ");
			}

			if (recebeuFinanciamentoInterno != null){
				hqlFiltros.append(" AND pj.recebeuFinanciamentoInterno = :recebeuFinanciamentoInterno ");
			}
			
			// localizar registros de atividades anteriores
			if (registro != null) {
				hqlFiltros.append(" AND atv.registro = :registro ");
			}

			hqlFiltros.append(" AND pj.situacaoProjeto.id <> :idSituacaoAtividadeRemovida ");

			// Cadastros em andamento não devem ser mostrados para os docentes
			// que não forem seus donos
			// cadastros em andamento devem ser excluídos das estatísticas
			if ((verCadastrosEmAndamento != null) && (!verCadastrosEmAndamento)) {
				hqlFiltros.append(" AND pj.situacaoProjeto.id <> :idSituacaoAtividadeCadastroAndamento ");
			}

			hqlCount.append(hqlFiltros.toString());

			hqlConsulta.append(hqlFiltros.toString());
			hqlConsulta.append(" ORDER BY me.pessoa.nome ");

			// Criando consulta
			Query queryCount = getSession().createQuery(hqlCount.toString());
			Query queryConsulta = getSession().createQuery(
					hqlConsulta.toString());

			// atividades não contabilizadas
			queryCount.setInteger("idSituacaoAtividadeRemovida", TipoSituacaoProjeto.EXTENSAO_REMOVIDO);
			queryConsulta.setInteger("idSituacaoAtividadeRemovida",	TipoSituacaoProjeto.EXTENSAO_REMOVIDO);

			//
			if ((verCadastrosEmAndamento != null) && (!verCadastrosEmAndamento)) {
				queryCount.setInteger("idSituacaoAtividadeCadastroAndamento",
						TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO);
				queryConsulta.setInteger(
						"idSituacaoAtividadeCadastroAndamento",
						TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO);
			}

			// Populando os valores dos filtros
			if (titulo != null) {
				queryCount
						.setString("titulo", "%" + titulo.toUpperCase() + "%");
				queryConsulta.setString("titulo", "%" + titulo.toUpperCase()
						+ "%");
			}

			if (idEdital != null) {
				queryCount.setInteger("idEditalExtensao", idEdital);
				queryConsulta.setInteger("idEditalExtensao", idEdital);
			}

			if (idEdital != null) {
				queryCount.setInteger("idEditalExtensao", idEdital);
				queryConsulta.setInteger("idEditalExtensao", idEdital);
			}

			if ((inicio != null) && (fim != null)) {
				queryConsulta.setDate("inicio", inicio);
				queryConsulta.setDate("fim", fim);

				queryCount.setDate("inicio", inicio);
				queryCount.setDate("fim", fim);
			}

			if (idTipoAtividade[0] != null) {
				queryCount.setParameterList("idTipoAtividade", idTipoAtividade);
				queryConsulta.setParameterList("idTipoAtividade", idTipoAtividade);
			}

			if (idUnidadeProponente != null) {
				queryCount.setInteger("idUnidadeProponente",
						idUnidadeProponente);
				queryConsulta.setInteger("idUnidadeProponente",
						idUnidadeProponente);
			}

			if (idCentro != null) {
				queryCount.setInteger("idCentro", idCentro);
				queryConsulta.setInteger("idCentro", idCentro);
			}

			if (idAreaCNPq != null) {
				queryCount.setInteger("idAreaCNPq", idAreaCNPq);
				queryConsulta.setInteger("idAreaCNPq", idAreaCNPq);
			}

			if (idAreaTematicaPrincipal != null) {
				queryCount.setInteger("idAreaTematicaPrincipal",
						idAreaTematicaPrincipal);
				queryConsulta.setInteger("idAreaTematicaPrincipal",
						idAreaTematicaPrincipal);
			}

			if (idServidor != null) {
				queryCount.setInteger("idServidor", idServidor);
				queryConsulta.setInteger("idServidor", idServidor);
			}

			if (ano != null) {
				queryCount.setInteger("ano", ano);
				queryConsulta.setInteger("ano", ano);
			}

			if (financiamentoInterno != null) {
				queryCount.setBoolean("financiamentoInterno",
						financiamentoInterno);
				queryConsulta.setBoolean("financiamentoInterno",
						financiamentoInterno);
			}

			if (financiamentoExterno != null) {
				queryCount.setBoolean("financiamentoExterno",
						financiamentoExterno);
				queryConsulta.setBoolean("financiamentoExterno",
						financiamentoExterno);
			}

			if (autoFinanciamento != null) {
				queryCount.setBoolean("autoFinanciamento", autoFinanciamento);
				queryConsulta.setBoolean("autoFinanciamento", autoFinanciamento);
			}

			if (convenioFunpec != null) {
				queryCount.setBoolean("convenioFunpec", convenioFunpec);
				queryConsulta.setBoolean("convenioFunpec", convenioFunpec);
			}
			
			if (recebeuFinanciamentoInterno != null) {
				queryCount.setBoolean("recebeuFinanciamentoInterno", recebeuFinanciamentoInterno);
				queryConsulta.setBoolean("recebeuFinanciamentoInterno", recebeuFinanciamentoInterno);
			}

			if (registro != null) {
				queryCount.setBoolean("registro", registro);
				queryConsulta.setBoolean("registro", registro);
			}

			Long total = (Long) queryCount.uniqueResult();
			if (paginacao != null) {
				paginacao.setTotalRegistros(total.intValue());
				queryConsulta.setFirstResult((paginacao.getPaginaAtual() - 1)
						* paginacao.getTamanhoPagina());
				queryConsulta.setMaxResults(paginacao.getTamanhoPagina());
			}

			if (total > LIMITE_RESULTADOS)
				throw new LimiteResultadosException("A consulta retornou "
						+ total
						+ " resultados. Por favor, restrinja mais a busca.");
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();

			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				AtividadeExtensao at = new AtividadeExtensao();
				at.setId((Integer) colunas[col++]);
				at.setDataInicio((Date) colunas[col++]);
				at.setDataFim((Date) colunas[col++]);
				at.setPublicoEstimado((Integer) colunas[col++]);
				at.setFinanciamentoInterno((Boolean) colunas[col++]);
				at.setFinanciamentoExterno((Boolean) colunas[col++]);
				at.setAutoFinanciado((Boolean) colunas[col++]);
				at.setConvenioFunpec((Boolean) colunas[col++]);
				at.setEditalExtensao((EditalExtensao) colunas[col++]);
				at.setAno((Integer) colunas[col++]);

				at.setTitulo((String) colunas[col++]);

				Unidade unidade = new Unidade();
				unidade.setId((Integer) colunas[col++]);
				unidade.setSigla((String) colunas[col++]);
				unidade.setNome((String) colunas[col++]);
				at.setUnidade(unidade);

				TipoSituacaoProjeto situacao = new TipoSituacaoProjeto();
				situacao.setId((Integer) colunas[col++]);
				situacao.setDescricao((String) colunas[col++]);
				at.setSituacaoProjeto(situacao);

				TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();
				tipo.setId((Integer) colunas[col++]);
				tipo.setDescricao((String) colunas[col++]);
				at.setTipoAtividadeExtensao(tipo);

				AreaTematica area = new AreaTematica();
				area.setId((Integer) colunas[col++]);
				area.setDescricao((String) colunas[col++]);
				at.setAreaTematicaPrincipal(area);

				at.setSequencia((Integer) colunas[col++]);

				Pessoa p = (Pessoa) colunas[col++];
				if (p != null) {
					MembroProjeto mp = new MembroProjeto();

					mp.setPessoa(p);

					// evitar exibir nomes duplicados
					// pois um coordenador pode ter vários projetos
					boolean repetido = false;
					for (AtividadeExtensao obj : result) {
						for (MembroProjeto it : obj.getMembrosEquipe()) {
							if (it.getPessoa() == p)
								repetido = true;
						}
					}

					if (!repetido)
						at.getMembrosEquipe().add(mp);
				}

				result.add(at);

			}

			return result;

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * Retorna as atividades sem planos de trabalho cadastrados ou com planos cadastrados mas sem discentes associados ao plano.
	 *  
	 * @param titulo
	 * @param ano
	 * @return
	 */	
	public Collection<AtividadeExtensao> atividadesSemPlano(String titulo, Integer ano) {
		
		StringBuilder hqlConsulta = new StringBuilder();
		hqlConsulta
				.append(" select distinct atv.id, p.titulo, p.dataInicio, p.dataFim, p.ano, atv.bolsasSolicitadas, " +
						" atv.bolsasConcedidas , atv.sequencia , atv.tipoAtividadeExtensao " +
						" from AtividadeExtensao as atv " +
						" inner join atv.projeto as p " +
						" where atv.id not in ( " +
						"				select distinct ativ.id " +
						"				from AtividadeExtensao as ativ " +										
						"				inner join ativ.planosTrabalho as plano " +
						"				inner join plano.discenteExtensao as de " +
						"				where ativ.bolsasSolicitadas > 0 and de.situacaoDiscenteExtensao = 4 " +
						"                     and plano.dataInicio <= now() and plano.dataFim >= now() " +
						"					  ) " +
						"       and p.situacaoProjeto.id = :em_execucao and atv.bolsasSolicitadas > 0 " +
						"       and (p.tipoProjeto.id = :idTipoProjeto) ");
		try {
			
			StringBuilder hqlFiltros = new StringBuilder();
			
			if (titulo != null) {
				hqlFiltros.append(" AND "
						+ UFRNUtils.toAsciiUpperUTF8("p.titulo") + " like "
						+ UFRNUtils.toAsciiUTF8(":titulo"));
			}
			
			if (ano != null) {
				// Na busca serão trazidas todas as ações que estejam em execução durante ano informado.
				hqlFiltros.append(" AND (extract(year from p.dataInicio) <= :ano and extract(year from p.dataFim) >= :ano) ");
			}
			
			hqlConsulta.append(hqlFiltros.toString());
			hqlConsulta.append(" ORDER BY p.ano DESC ");
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
			queryConsulta.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
			
			
			// Populando valores nos filtros
			if (titulo != null) {				
				queryConsulta.setString("titulo", "%" + titulo.toUpperCase()
						+ "%");
			}
			if (ano != null) {				
				queryConsulta.setInteger("ano", ano);
			}
			queryConsulta.setInteger("em_execucao", TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO);

			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();
			
			Collection<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();

			
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				
				Object[] colunas = (Object[]) lista.get(a);
				AtividadeExtensao ativ = new AtividadeExtensao();
				
				ativ.setId( (Integer) colunas[col++] );
				ativ.setTitulo( (String)colunas[col++] );
				ativ.setDataInicio( (Date) colunas[col++] );
				ativ.setDataFim( (Date) colunas[col++] );
				ativ.setAno( (Integer) colunas[col++] );
				ativ.setBolsasSolicitadas((Integer) colunas[col++]);
				ativ.setBolsasConcedidas((Integer) colunas[col++]);
				ativ.setSequencia((Integer) colunas[col++]);
				ativ.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
				
				result.add(ativ);
			}
			
			return result;
		}
		catch (HibernateException e) {			
			e.printStackTrace();
		}
		catch (DAOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Filtro utilizado nas principais buscas de ações de extensão.
	 * 
	 * OBS: Caso idSituacaoAtividade não esteja setada, a busca não esta trazendo ações com situação inserida nos seguintes grupos: 
	 * EXTENSAO_GRUPO_INVALIDO
	 * PROJETO_BASE_GRUPO_INVALIDO
	 * 
	 * @param titulo
	 * @param idTipoAtividade
	 * @param idSituacaoAtividade
	 * @param idUnidadeProponente
	 * @param idAreaCNPq
	 * @param idAreaTematicaPrincipal
	 * @param idFonteFinanciamento
	 * @param idServidor
	 * @param paginacao
	 * @return
	 * @throws DAOException
	 */	
	public Collection<AtividadeExtensao> filter ( 
			Integer idEdital,
			Date inicio,
			Date fim,
			Date inicioConclusao,
			Date fimConclusao,
			Date inicioExecucao,
			Date fimExecucao,			
			String titulo,
			Integer[] idTipoAtividade,
			Integer[] idSituacaoAtividade,
			Integer idUnidadeProponente, Integer idCentro, Integer idAreaCNPq,
			Integer idAreaTematicaPrincipal, Integer idCoordenador, Integer idServidor, Integer ano,
			Boolean financiamentoInterno, Boolean financiamentoExterno,
			Boolean autoFinanciamento, Boolean convenioFunpec, Boolean recebeuFinanciamentoInterno,
			PagingInformation paginacao, Boolean verCadastrosEmAndamento,
			Boolean registro, Integer sequencia, Integer anoCodigo, Boolean solicitacaoRenovacao, Boolean projetoAssociado, Boolean moduloExtensao) throws DAOException,LimiteResultadosException {			

		try {

			StringBuilder hqlCount = new StringBuilder();
			hqlCount.append(" SELECT  count(distinct atv.id) "
					+ "FROM AtividadeExtensao atv "
					+ "LEFT JOIN atv.editalExtensao editalExtensao "
					+ "INNER JOIN atv.projeto as p "
					+ "LEFT JOIN p.coordenador as coord "
					+ "LEFT JOIN p.equipe as me "
					+ "LEFT JOIN p.unidadeOrcamentaria as uo "
					+ "LEFT JOIN me.pessoa as pessoa "
					);
			hqlCount.append(" WHERE 1 = 1 ");

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
					.append(" SELECT distinct atv.id, p.id, p.ativo, p.ensino, p.pesquisa, p.extensao, p.dataInicio, p.dataFim, atv.publicoInterno, p.financiamentoInterno, "
							+ "p.financiamentoExterno, p.autoFinanciado, atv.convenioFunpec, editalExtensao, p.ano, p.titulo, "
							+ "p.unidade.id, p.unidade.sigla, p.unidade.nome, " 
							+ "uo.id, uo.sigla, uo.nome, "
							+ "atv.dataEnvio, atv.situacaoProjeto.id, atv.situacaoProjeto.descricao, "
							+ "atv.tipoAtividadeExtensao.id, atv.tipoAtividadeExtensao.descricao, "
							+ "atv.areaTematicaPrincipal.id, atv.areaTematicaPrincipal.descricao, atv.sequencia, " 
							+ "atv.bolsasSolicitadas, atv.bolsasConcedidas, "
							+ "coord.id, pessoaCoord.id, pessoaCoord.nome, pessoaCoord.email, coord.funcaoMembro.id, coord.ativo, coord.dataInicio, coord.dataFim, " 
							+ "servCoordUnidade.id, servCoordUnidade.nome, servCoordUnidade.sigla, "
							+ "docenCoordUnidade.id, docenCoordUnidade.nome, docenCoordUnidade.sigla, "
							+ "me.id, pessoa.id, pessoa.nome, me.funcaoMembro.id, me.ativo, me.dataInicio, me.dataFim  "
							+ "FROM AtividadeExtensao atv "
							+ "LEFT JOIN atv.editalExtensao editalExtensao "
							+ "INNER JOIN atv.projeto as p "
							+ "LEFT JOIN p.coordenador as coord "
							+ "LEFT JOIN coord.pessoa as pessoaCoord "
							+ "LEFT JOIN coord.servidor.unidade as servCoordUnidade "
							+ "LEFT JOIN coord.docenteExterno.unidade as docenCoordUnidade "
							+ "LEFT JOIN p.equipe as me "
							+ "LEFT JOIN p.unidadeOrcamentaria uo "
							+ "LEFT JOIN me.pessoa as pessoa "
							);

			hqlConsulta.append(" WHERE 1=1 ");

			StringBuilder hqlFiltros = new StringBuilder();
			// Filtros para a busca
			//buscando por código
			if(sequencia != null && anoCodigo != null) {
				hqlFiltros.append(" AND atv.sequencia = :sequencia AND p.ano = :ano "); 
			}	
			
			if (titulo != null) {
				hqlFiltros.append(" AND "
						+ UFRNUtils.toAsciiUpperUTF8("p.titulo") + " like "
						+ UFRNUtils.toAsciiUpperUTF8(":titulo"));
			}
			
			if (projetoAssociado != null) {
				hqlFiltros.append(" AND p.extensao = :projetoAssociado");
			}

			if (idEdital != null) {
				hqlFiltros.append(" AND editalExtensao.id = :idEditalExtensao ");
			}

			if ((inicio != null) && (fim != null)) {
				hqlFiltros.append(" AND " + HibernateUtils.generateDateIntersection("p.dataInicio", "p.dataFim", ":inicio", ":fim"));
			}

			if ((inicioConclusao != null) && (fimConclusao != null)) {
				hqlFiltros.append(" AND (p.dataFim >= :inicioConclusao AND p.dataFim <= :fimConclusao)");
			}
			
			if ((inicioExecucao != null) && (fimExecucao != null)) {
				hqlFiltros.append(" AND (p.dataInicio >= :inicioExecucao AND p.dataInicio <= :fimExecucao)");
			}

			
			if (idTipoAtividade != null && idTipoAtividade[0] != null) {
				hqlFiltros.append(" AND atv.tipoAtividadeExtensao.id IN (:idTipoAtividade)");
			}

			// A busca pode ser feita por mais de uma SituacaoAtividade ao mesmo
			// tempo, exemplo: 103, 105, 110
			if (idSituacaoAtividade.length > 0) {
				// Colocando os IDSituacao entre vírgula para usar na cláusula
				// SQL IN()
				String situacoes = "";
				for (Integer idSituacao : idSituacaoAtividade)
					situacoes += idSituacao + ",";
				// Retirando a última vírgula
				situacoes = situacoes.substring(0, situacoes.length() - 1);
				hqlFiltros.append(" AND atv.situacaoProjeto.id IN ("
						+ situacoes + ") ");
			}
			// ---//

			if (idUnidadeProponente != null) {
				hqlFiltros.append(" AND p.unidade.id = :idUnidadeProponente");
			}

			if (idCentro != null) {
			    	// adaptação para unidades especializadas
				hqlFiltros.append(" AND (p.unidade.gestora.id = :idCentro or p.unidade.id = :idCentro)");
			}

			if (idAreaCNPq != null) {
				hqlFiltros.append(" AND p.areaConhecimentoCnpq.id = :idAreaCNPq");
			}

			if (idAreaTematicaPrincipal != null) {
				hqlFiltros.append(" AND atv.areaTematicaPrincipal.id = :idAreaTematicaPrincipal");
			}

			if (idServidor != null) {
				hqlFiltros.append(" AND me.servidor.id = :idServidor");
			}

			if (idCoordenador != null) {
				hqlFiltros.append(" AND coord.servidor.id = :idCoordenador ");
			}
			
			if (ano != null) {
				hqlFiltros.append(" AND p.ano = :ano ");
			}
			
			if (financiamentoInterno != null && financiamentoInterno) {
				hqlFiltros.append(" AND p.financiamentoInterno = :financiamentoInterno");
			}

			if (financiamentoExterno != null && financiamentoExterno) {
				hqlFiltros.append(" AND p.financiamentoExterno = :financiamentoExterno");
			}

			if (autoFinanciamento != null && autoFinanciamento) {
				hqlFiltros.append(" AND p.autoFinanciado = :autoFinanciamento");
			}

			if (convenioFunpec != null && convenioFunpec) {
				hqlFiltros.append(" AND atv.convenioFunpec = :convenioFunpec");
			}
			
			if (recebeuFinanciamentoInterno != null && recebeuFinanciamentoInterno) {
					hqlFiltros.append(" AND p.recebeuFinanciamentoInterno = :recebeuFinanciamentoInterno");
			}
			
			// localizar registros de atividades anteriores
			if (registro != null) {
				hqlFiltros.append(" AND atv.registro = :registro");
			}

			//Evitando que deixe de trazer ações quando idSituacaoAtividade esteja nos grupos EXTENSAO_GRUPO_INVALIDO ou PROJETO_BASE_GRUPO_INVALIDO.
			//Se a busca esta com idSituacaoAtividade preenchido, então não devemos excluir nenhuma situação, pois a própria idSituacaoAtividade diz
			//quais situações são validas.
			if( (idSituacaoAtividade==null || idSituacaoAtividade.length == 0) && !moduloExtensao) {
				hqlFiltros.append(" AND p.ativo = trueValue() AND atv.ativo = trueValue()");
				hqlFiltros.append(" AND p.situacaoProjeto.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO)");
			}
			// cadastros em andamento não devem ser mostrados para os docentes
			// que não forem seus donos
			// cadastros em andamento devem ser excluídos das estatísticas
			if ((verCadastrosEmAndamento != null) && (!verCadastrosEmAndamento))
				hqlFiltros.append(" AND p.situacaoProjeto.id <> :idSituacaoAtividadeCadastroAndamento");
			
			if (solicitacaoRenovacao != null) {
			    hqlFiltros.append(" AND p.renovacao = :solicitacaoRenovacao");
			}

			hqlCount.append(hqlFiltros.toString());

			hqlConsulta.append(hqlFiltros.toString());
			hqlConsulta.append(" ORDER BY p.ano DESC, atv.tipoAtividadeExtensao.id DESC, atv.sequencia DESC ");

			// Criando consulta
			Query queryCount = getSession().createQuery(hqlCount.toString());
			Query queryConsulta = getSession().createQuery(
					hqlConsulta.toString());


			//Evitando que deixe de trazer ações quando idSituacaoAtividade esteja nos grupos EXTENSAO_GRUPO_INVALIDO ou PROJETO_BASE_GRUPO_INVALIDO.
			//Se a busca esta com idSituacaoAtividade preenchido, então não devemos excluir nenhuma situação, pois a própria idSituacaoAtividade diz
			//quais situações são validas.
			if( (idSituacaoAtividade==null || idSituacaoAtividade.length == 0) && !moduloExtensao) {
				// atividades não contabilizadas
				queryCount.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
				queryCount.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
				
				queryConsulta.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);			
				queryConsulta.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
			}
			//
			if ((verCadastrosEmAndamento != null) && (!verCadastrosEmAndamento)) {
				queryCount.setInteger("idSituacaoAtividadeCadastroAndamento",
						TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO);
				queryConsulta.setInteger(
						"idSituacaoAtividadeCadastroAndamento",
						TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO);
			}

			// Populando os valores dos filtros
			if (titulo != null) {
				queryCount.setString("titulo", "%" + titulo.toUpperCase() + "%");
				queryConsulta.setString("titulo", "%" + titulo.toUpperCase() + "%");
			}
			
			if( sequencia != null ) {
				queryCount.setInteger("sequencia", sequencia);
				queryConsulta.setInteger("sequencia", sequencia);
			}

			if (idEdital != null) {
				queryCount.setInteger("idEditalExtensao", idEdital);
				queryConsulta.setInteger("idEditalExtensao", idEdital);
			}

			if ((inicio != null) && (fim != null)) {
				queryConsulta.setDate("inicio", inicio);
				queryConsulta.setDate("fim", fim);

				queryCount.setDate("inicio", inicio);
				queryCount.setDate("fim", fim);
			}

			//identifica o período de conclusão da ação
			if ((inicioConclusao != null) && (fimConclusao != null)) {
				queryConsulta.setDate("inicioConclusao", inicioConclusao);
				queryConsulta.setDate("fimConclusao", fimConclusao);

				queryCount.setDate("inicioConclusao", inicioConclusao);
				queryCount.setDate("fimConclusao", fimConclusao);
			}

			//identifica o período de início da execução ação
			if ((inicioExecucao != null) && (fimExecucao != null)) {
				queryConsulta.setDate("inicioExecucao", inicioExecucao);
				queryConsulta.setDate("fimExecucao", fimExecucao);

				queryCount.setDate("inicioExecucao", inicioExecucao);
				queryCount.setDate("fimExecucao", fimExecucao);
			}
			
			
			if (idTipoAtividade != null && idTipoAtividade[0] != null) {
				queryCount.setParameterList("idTipoAtividade", idTipoAtividade);
				queryConsulta.setParameterList("idTipoAtividade", idTipoAtividade);
			}
			
			if (projetoAssociado != null) {
				queryCount.setBoolean("projetoAssociado", projetoAssociado);
				queryConsulta.setBoolean("projetoAssociado", projetoAssociado);
			}

			if (idUnidadeProponente != null) {
				queryCount.setInteger("idUnidadeProponente",
						idUnidadeProponente);
				queryConsulta.setInteger("idUnidadeProponente",
						idUnidadeProponente);
			}

			if (idCentro != null) {
				queryCount.setInteger("idCentro", idCentro);
				queryConsulta.setInteger("idCentro", idCentro);
			}

			if (idAreaCNPq != null) {
				queryCount.setInteger("idAreaCNPq", idAreaCNPq);
				queryConsulta.setInteger("idAreaCNPq", idAreaCNPq);
			}

			if (idAreaTematicaPrincipal != null) {
				queryCount.setInteger("idAreaTematicaPrincipal",
						idAreaTematicaPrincipal);
				queryConsulta.setInteger("idAreaTematicaPrincipal",
						idAreaTematicaPrincipal);
			}

			if (idServidor != null) {
				queryCount.setInteger("idServidor", idServidor);
				queryConsulta.setInteger("idServidor", idServidor);
			}

			if (idCoordenador != null) {
				queryCount.setInteger("idCoordenador", idCoordenador);
				queryConsulta.setInteger("idCoordenador", idCoordenador);
			}
			
			if (ano != null) {
				queryCount.setInteger("ano", ano);
				queryConsulta.setInteger("ano", ano);
			}
			
			if (anoCodigo != null) {
				queryCount.setInteger("ano", anoCodigo);
				queryConsulta.setInteger("ano", anoCodigo);
			}

			if (financiamentoInterno != null && financiamentoInterno) {
				queryCount.setBoolean("financiamentoInterno", financiamentoInterno);
				queryConsulta.setBoolean("financiamentoInterno", financiamentoInterno);
			}

			if (financiamentoExterno != null && financiamentoExterno) {
				queryCount.setBoolean("financiamentoExterno", financiamentoExterno);
				queryConsulta.setBoolean("financiamentoExterno", financiamentoExterno);
			}

			if (autoFinanciamento != null && autoFinanciamento) {
				queryCount.setBoolean("autoFinanciamento", autoFinanciamento);
				queryConsulta.setBoolean("autoFinanciamento", autoFinanciamento);
			}

			if (convenioFunpec != null && convenioFunpec) {
				queryCount.setBoolean("convenioFunpec", convenioFunpec);
				queryConsulta.setBoolean("convenioFunpec", convenioFunpec);
			}
			
			if (recebeuFinanciamentoInterno != null && recebeuFinanciamentoInterno) {
					queryCount.setBoolean("recebeuFinanciamentoInterno", recebeuFinanciamentoInterno);
					queryConsulta.setBoolean("recebeuFinanciamentoInterno", recebeuFinanciamentoInterno);
			}

			if (registro != null) {
				queryCount.setBoolean("registro", registro);
				queryConsulta.setBoolean("registro", registro);
			}
			
			if (solicitacaoRenovacao != null) {
			    queryCount.setBoolean("solicitacaoRenovacao", solicitacaoRenovacao);
			    queryConsulta.setBoolean("solicitacaoRenovacao", solicitacaoRenovacao);
			}

			Long total = (Long) queryCount.uniqueResult();
			if (paginacao != null) {
				paginacao.setTotalRegistros(total.intValue());
				queryConsulta.setFirstResult((paginacao.getPaginaAtual() - 1) * paginacao.getTamanhoPagina());
				queryConsulta.setMaxResults(paginacao.getTamanhoPagina());
			}

			if (total > LIMITE_RESULTADOS) {
				throw new LimiteResultadosException("A consulta retornou "
						+ total
						+ " resultados. Por favor, restrinja mais a busca.");
			}
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();

			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();

			int idOld = 0;
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				AtividadeExtensao at = new AtividadeExtensao();

				int idNew = (Integer) colunas[col++];
				at.setId(idNew);

				if (idOld != idNew) {

					idOld = idNew;
					
					at.getProjeto().setId((Integer) colunas[col++]);
					at.getProjeto().setAtivo((Boolean) colunas[col++]);
					at.getProjeto().setEnsino((Boolean) colunas[col++]);
					at.getProjeto().setPesquisa((Boolean) colunas[col++]);
					at.getProjeto().setExtensao((Boolean) colunas[col++]);
					at.setDataInicio((Date) colunas[col++]);
					at.setDataFim((Date) colunas[col++]);
					at.setPublicoEstimado((Integer) colunas[col++]);
					at.setFinanciamentoInterno((Boolean) colunas[col++]);
					at.setFinanciamentoExterno((Boolean) colunas[col++]);
					at.setAutoFinanciado((Boolean) colunas[col++]);
					at.setConvenioFunpec((Boolean) colunas[col++]);
					at.setEditalExtensao((EditalExtensao) colunas[col++]);
					at.setAno((Integer) colunas[col++]);

					at.setTitulo((String) colunas[col++]);

					Unidade unidade = new Unidade();
					unidade.setId((Integer) colunas[col++]);
					unidade.setSigla((String) colunas[col++]);
					unidade.setNome((String) colunas[col++]);
					at.setUnidade(unidade);

					if (colunas[19] != null) {
					    Unidade uo = new Unidade();
					    uo.setId((Integer) colunas[col++]);
					    uo.setSigla((String) colunas[col++]);
					    uo.setNome((String) colunas[col++]);
					    at.getProjeto().setUnidadeOrcamentaria(uo);
					}
					col = 22;
					
					at.setDataEnvio((Date) colunas[col++]);
					
					TipoSituacaoProjeto situacao = new TipoSituacaoProjeto();
					situacao.setId((Integer) colunas[col++]);
					situacao.setDescricao((String) colunas[col++]);
					at.setSituacaoProjeto(situacao);

					TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();
					tipo.setId((Integer) colunas[col++]);
					tipo.setDescricao((String) colunas[col++]);
					at.setTipoAtividadeExtensao(tipo);

					AreaTematica area = new AreaTematica();
					area.setId((Integer) colunas[col++]);
					area.setDescricao((String) colunas[col++]);
					at.setAreaTematicaPrincipal(area);

					at.setSequencia((Integer) colunas[col++]);
					at.setBolsasSolicitadas((Integer) colunas[col++]);
					at.setBolsasConcedidas((Integer) colunas[col++]);
					
					// Adicionando Coordenador
					if(colunas[33] != null){
						MembroProjeto m = new MembroProjeto();
						m.setId((Integer) colunas[col++]);
						Pessoa p = new Pessoa();
						p.setId((Integer) colunas[col++]);
						p.setNome((String) colunas[col++]);
						p.setEmail((String) colunas[col++]);
						m.setPessoa(p);
						m.setFuncaoMembro(new FuncaoMembro((Integer) colunas[col++]));
						m.setAtivo((Boolean) colunas[col++]);
						m.setDataInicio((Date) colunas[col++]);
						m.setDataFim((Date) colunas[col++]);
						
						// setando a unidade do coordenador seja ele servidor ou docente externo
						Unidade u = new Unidade();
						if(colunas[41] != null){
							u.setId((Integer) colunas[col++]);
							u.setNome((String) colunas[col++]);
							u.setSigla((String) colunas[col++]);
							m.getServidor().setUnidade(u);
						}else if(colunas[44] != null){
							u.setId((Integer) colunas[col++]);
							u.setNome((String) colunas[col++]);
							u.setSigla((String) colunas[col++]);
							m.getDocenteExterno().setUnidade(u);
						}
						
						at.getProjeto().setCoordenador(m);
					}
					result.add(at);
				}

				/**
				// Adicionando o coordenador na ação encontrada...
				if ((colunas[35] != null)
						&& ((Integer) colunas[35] == FuncaoMembro.COORDENADOR)
						&& ((Boolean) colunas[36])) {
					MembroProjeto m = new MembroProjeto();
					m.setId((Integer) colunas[32]);
					Pessoa p = new Pessoa();
					p.setId((Integer) colunas[33]);
					p.setNome((String) colunas[34]);
					m.setPessoa(p);
					m.setFuncaoMembro(new FuncaoMembro((Integer) colunas[35]));
					m.setAtivo((Boolean) colunas[36]);
					m.setDataInicio((Date) colunas[37]);
					m.setDataFim((Date) colunas[38]);

					result.get(result.indexOf(at)).getMembrosEquipe().add(m);
					
					at.getProjeto().setCoordenador(m);
				} */
			}

			return result;

		} catch (LimiteResultadosException ex) {
			throw new LimiteResultadosException(ex.getMessage(), ex);
		}		
		catch (Exception ex) {
				throw new DAOException(ex.getMessage(), ex);
		}
	}

	
	/**
	 * 
	 * Lista todas as atividades pendentes de avaliação ou abertas inicia a
	 * distribuição para avaliação pelos membros do comitê de extensão ou ad-hoc
	 * 
	 * atividades aguardando avaliação ou submetidas
	 * 
	 * @return
	 * @throws DAOException
	 */	
	public Collection<AtividadeExtensao> findPendenteDistribuicao(
			String titulo, Integer ano, Integer idEdital, Boolean financiamentoInterno,
			Boolean financiamentoExterno, Boolean autoFinanciamento,
			Boolean convenioFunpec, Integer idAreaTematica) throws DAOException {

		try {

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
					.append(" SELECT distinct atv.id, " 
							+ " p.ensino, p.pesquisa, p.extensao, p.financiamentoInterno, p.financiamentoExterno, "
							+ " p.autoFinanciado, atv.convenioFunpec, "
							+ " p.id, p.coordenador.id, p.coordenador.servidor.id, p.coordenador.servidor.pessoa.id, p.coordenador.servidor.pessoa.nome, "
							+ " atv.sequencia, p.ano, p.titulo, p.unidade.id, p.unidade.sigla, p.unidade.nome, " 
							+ " atv.situacaoProjeto.id, atv.situacaoProjeto.descricao, "
							+ " atv.tipoAtividadeExtensao.id, atv.tipoAtividadeExtensao.descricao, "
							+ " atv.areaTematicaPrincipal.id, atv.areaTematicaPrincipal.descricao, "
							+ " ava.id, ava.tipoAvaliacao.id, ava.statusAvaliacao.id, ava.ativo, avld.id, s1.id, ps1.id, mc.id, s2.id, ps2.id "
							+ "FROM AtividadeExtensao atv " 
							+ " LEFT JOIN atv.avaliacoes as ava "
							+ " LEFT JOIN ava.avaliadorAtividadeExtensao as avld "
							+ " LEFT JOIN avld.servidor as s1 "
							+ " LEFT JOIN s1.pessoa ps1 "
							+ " LEFT JOIN ava.membroComissao as mc "
							+ " LEFT JOIN mc.servidor as s2 "
							+ " LEFT JOIN s2.pessoa ps2 "
							+ " LEFT JOIN atv.editalExtensao as edital "
							+ " INNER JOIN atv.projeto p ");

			hqlConsulta.append(" WHERE 1 = 1 ");

			hqlConsulta.append(" AND ((atv.situacaoProjeto.id = :idSituacaoSubmetida) "
							+ "OR (atv.situacaoProjeto.id = :idSituacaoAguardandoAvaliacao))");
			
			if (titulo != null) {
				hqlConsulta.append(" AND "
						+ UFRNUtils.toAsciiUpperUTF8("p.titulo") + " like "
						+ UFRNUtils.toAsciiUTF8(":titulo"));
			}

			if ((ano != null) && (ano != 0)) {
				hqlConsulta.append(" AND p.ano = :ano");
			}

			
			if ((idEdital != null) && (idEdital != 0)) {
				hqlConsulta.append(" AND edital.id = :idEdital");
			}

			if ((idAreaTematica != null) && (idAreaTematica != 0)) {
				hqlConsulta.append(" AND atv.areaTematicaPrincipal.id = :idAreaTematica");
			}

			
			if (financiamentoInterno != null) {
				hqlConsulta.append(" AND p.financiamentoInterno = :financiamentoInterno");
			}

			if (financiamentoExterno != null) {
				hqlConsulta.append(" AND p.financiamentoExterno = :financiamentoExterno");
			}

			if (autoFinanciamento != null) {
				hqlConsulta.append(" AND p.autoFinanciado = :autoFinanciamento");
			}

			if (convenioFunpec != null) {
				hqlConsulta.append(" AND atv.convenioFunpec = :convenioFunpec");
			}

			hqlConsulta.append(" ORDER BY p.ano DESC, atv.tipoAtividadeExtensao.id, p.titulo ");

			// Criando consulta
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

			// Populando os valores dos filtros			
			queryConsulta.setInteger("idSituacaoSubmetida",	TipoSituacaoProjeto.EXTENSAO_SUBMETIDO);
			queryConsulta.setInteger("idSituacaoAguardandoAvaliacao", TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO);
			
			
			if (titulo != null) {
				queryConsulta.setString("titulo", "%" + titulo.toUpperCase() + "%");
			}
			
			if (ano != null) {
				queryConsulta.setInteger("ano", ano);
			}

			if (idEdital != null) {
				queryConsulta.setInteger("idEdital", idEdital);
			}

			if (idAreaTematica != null) {
				queryConsulta.setInteger("idAreaTematica", idAreaTematica);
			}

			if (financiamentoInterno != null) {
				queryConsulta.setBoolean("financiamentoInterno", financiamentoInterno);
			}

			if (financiamentoExterno != null) {
				queryConsulta.setBoolean("financiamentoExterno", financiamentoExterno);
			}

			if (autoFinanciamento != null) {
				queryConsulta.setBoolean("autoFinanciamento", autoFinanciamento);
			}

			if (convenioFunpec != null) {
				queryConsulta.setBoolean("convenioFunpec", convenioFunpec);
			}
			
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();

			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				AtividadeExtensao a1 = new AtividadeExtensao(
						(Integer) (colunas[0]));

				// colunas repetidas se já tiver o projeto pula todas e vai pra
				// o que é diferente...
				if (!result.contains(a1)) {

					AtividadeExtensao at = new AtividadeExtensao();

					at.setId((Integer) colunas[col++]);
					
					at.getProjeto().setEnsino((Boolean) colunas[col++]);					
					at.getProjeto().setPesquisa((Boolean) colunas[col++]);
					at.getProjeto().setExtensao((Boolean) colunas[col++]);					

					at.setFinanciamentoInterno((Boolean) colunas[col++]);
					at.setFinanciamentoExterno((Boolean) colunas[col++]);
					at.setAutoFinanciado((Boolean) colunas[col++]);
					at.setConvenioFunpec((Boolean) colunas[col++]);
					
					at.getProjeto().setId((Integer) colunas[col++]);
					at.getProjeto().setCoordenador(new MembroProjeto((Integer) colunas[col++]));
					at.getProjeto().getCoordenador().setServidor(new Servidor((Integer) colunas[col++]));
					at.getProjeto().getCoordenador().getServidor().setPessoa(new Pessoa((Integer) colunas[col++]));
					at.getProjeto().getCoordenador().getServidor().getPessoa().setNome((String) colunas[col++]);

					at.setSequencia((Integer) colunas[col++]);
					at.setAno((Integer) colunas[col++]);
					at.setTitulo((String) colunas[col++]);

					Unidade unidade = new Unidade();
					unidade.setId((Integer) colunas[col++]);
					unidade.setSigla((String) colunas[col++]);
					unidade.setNome((String) colunas[col++]);
					at.setUnidade(unidade);

					TipoSituacaoProjeto situacao = new TipoSituacaoProjeto();
					situacao.setId((Integer) colunas[col++]);
					situacao.setDescricao((String) colunas[col++]);
					at.setSituacaoProjeto(situacao);

					TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();
					tipo.setId((Integer) colunas[col++]);
					tipo.setDescricao((String) colunas[col++]);
					at.setTipoAtividadeExtensao(tipo);

					AreaTematica area = new AreaTematica();
					area.setId((Integer) colunas[col++]);
					area.setDescricao((String) colunas[col++]);
					at.setAreaTematicaPrincipal(area);

					result.add(at);

				}

				// avaliação não repete nas linhas retornadas, várias avaliações
				// para a mesma ação
				col = 25;
				if (colunas[col] != null) {
					AvaliacaoAtividade ava = new AvaliacaoAtividade();
					ava.setId((Integer) colunas[col++]);
					TipoAvaliacao ta = new TipoAvaliacao(
							(Integer) colunas[col++]);
					ava.setTipoAvaliacao(ta);
					StatusAvaliacao st = new StatusAvaliacao(
							(Integer) colunas[col++]);
					ava.setStatusAvaliacao(st);
					ava.setAtivo((Boolean) colunas[col++]);

					col = 29;
					if (colunas[col] != null){
						ava.setAvaliadorAtividadeExtensao(new AvaliadorAtividadeExtensao((Integer) colunas[col++]));
						ava.getAvaliadorAtividadeExtensao().setServidor(new Servidor((Integer) colunas[col++]));
						ava.getAvaliadorAtividadeExtensao().getServidor().setPessoa(new Pessoa((Integer) colunas[col++]));
					}
					
					col = 32;
					if (colunas[col] != null){
						ava.setMembroComissao(new MembroComissao((Integer) colunas[col++]));
						ava.getMembroComissao().setServidor(new Servidor((Integer) colunas[col++]));
						ava.getMembroComissao().getServidor().setPessoa(new Pessoa((Integer) colunas[col++]));
					}

					result.get(result.indexOf(a1)).getAvaliacoes().add(ava);
				}
			}
			return result;

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}

	/**
	 * Método utilizado para exportar os dados de ações de extensão com situação
	 * 'aguardando avaliação' para serem analisados no excel.
	 * 
	 * @param discente
	 * @return retorna String com formato .CSV
	 * @throws ArqException
	 */
	@Deprecated
	public String findListaAcoesAvaliacaoFinal(int ano) throws ArqException {

		Connection con = null;

		try {
			con = Database.getInstance().getSigaaConnection();

			String sql = "SELECT DISTINCT "
					+ "u.sigla as SIGLA_DEP, u.nome as NOME_DEP, "

					+ "(SELECT p.nome FROM projetos.membro_projeto mp "
					+ "INNER JOIN comum.pessoa p ON mp.id_pessoa = p.id_pessoa "
					+ "WHERE mp.id_funcao_membro = 1 and mp.id_atividade = atv.id_atividade) as COORDENADOR,"
					+

					"p.id_projeto as CODIGO_ACAO,"
					+ "UPPER(SUBSTR(p.titulo,1, 70)) as TITULO_ACAO,	"
					+ "tatv.descricao as TIPO_ACAO,"
					+ "area.descricao as AREA_TEMATICA,"
					+ "atv.publico_estimado as PUBLICO_ESTIMADO, "
					+ "ur.sigla as CENTRO, "
					+ "atv.bolsas_solicitadas as BOLSAS_SOLICITADAS, '' as BOLSAS_APROVADAS, "
					+

					"(SELECT CASE WHEN sum(orc.valor_unitario * orc.quantidade) is null THEN 0 ELSE sum(orc.valor_unitario * orc.quantidade) END "
					+ "FROM extensao.orcamento_detalhado orc	"
					+ "	WHERE id_elemento_despesa = 29 and orc.id_atividade = atv.id_atividade)  as PESSOA_JURICA, "
					+ "'' as PESSOA_JURICA_APROVADO,"
					+

					"(SELECT CASE WHEN sum(orc.valor_unitario * orc.quantidade) is null THEN 0 ELSE sum(orc.valor_unitario * orc.quantidade) END"
					+ " FROM extensao.orcamento_detalhado orc"
					+ "	WHERE id_elemento_despesa = 31 and orc.id_atividade = atv.id_atividade) as PESSOA_FISICA, "
					+ "'' as PESSOA_FISICA_APROVADO,"
					+

					"(SELECT CASE WHEN sum(orc.valor_unitario * orc.quantidade) is null THEN 0 ELSE sum(orc.valor_unitario * orc.quantidade) END"
					+ " FROM extensao.orcamento_detalhado orc"
					+ "	WHERE id_elemento_despesa = 35 and orc.id_atividade = atv.id_atividade) as PASSAGENS, "
					+ "'' as PASSAGENS_APROVADO,"
					+

					"(SELECT CASE WHEN sum(orc.valor_unitario * orc.quantidade) is null THEN 0 ELSE sum(orc.valor_unitario * orc.quantidade) END"
					+ " FROM extensao.orcamento_detalhado orc"
					+ "	WHERE id_elemento_despesa = 33 and orc.id_atividade = atv.id_atividade) as MATERIAL_CONSUMO, "
					+ "'' as MATERIAL_CONSUMO_APROVADO,"
					+

					"(SELECT CASE WHEN sum(orc.valor_unitario * orc.quantidade) is null THEN 0 ELSE sum(orc.valor_unitario * orc.quantidade) END"
					+ " FROM extensao.orcamento_detalhado orc"
					+ "	WHERE id_elemento_despesa = 34 and orc.id_atividade = atv.id_atividade) as DIARIAS, "
					+ "'' as DIARIAS_APROVADO,"
					+

					"(SELECT CASE WHEN sum(orc.valor_unitario * orc.quantidade) is null THEN 0 ELSE sum(orc.valor_unitario * orc.quantidade) END"
					+ " FROM extensao.orcamento_detalhado orc"
					+ "	WHERE orc.id_atividade = atv.id_atividade) as TOTAL_SOLICITADO"
					+

					" FROM extensao.atividade atv "
					+ " INNER JOIN projetos.projeto p ON atv.id_projeto = p.id_projeto "
					+ " INNER JOIN extensao.tipo_atividade_extensao tatv ON atv.id_tipo_atividade_extensao = tatv.id_tipo_atividade_extensao"
					+ " INNER JOIN extensao.area_tematica area	ON atv.id_area_tematica_principal = area.id_area_tematica"
					+ " INNER JOIN comum.unidade u ON u.id_unidade = p.id_unidade"
					+ " INNER JOIN comum.unidade ur ON ur.id_unidade = u.unidade_responsavel"
					+

					" WHERE	(p.id_tipo_situacao_projeto = ? or p.id_tipo_situacao_projeto = ?) and atv.financiamento_interno = ? " +
					"AND (extract(year from p.dataInicio) <= :ano and extract(year from p.dataFim) >= :ano) ";

			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO);
			st.setInt(2, TipoSituacaoProjeto.EXTENSAO_SUBMETIDO);
			st.setBoolean(3, true);
			st.setInt(4, ano);

			ResultSet rs = st.executeQuery();

			return UFRNUtils.resultSetToCSV(rs);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException(e);
		} finally {
			Database.getInstance().close(con);
		}

	}


	/**
	 * Retorna TRUE se o servidor for coordenador de alguma ação de extensão
	 * ativa.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public boolean isCoordenadorAtividade(Pessoa pessoa) throws DAOException {

		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" select count(*) from AtividadeExtensao atividade " 
							+ "inner join atividade.projeto projeto "
							+ "inner join projeto.coordenador coordenador "
							+ "where atividade.ativo = trueValue() and projeto.ativo = trueValue() and " +
									"coordenador.pessoa.id = :idPessoa and coordenador.ativo = trueValue() and "
							+ "(projeto.situacaoProjeto.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO)) ");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idPessoa", pessoa.getId());

			query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

			Long count = (Long) query.uniqueResult();
			return count > 0;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Retorna a sequência atual para cadastro da ação de extensão aprovada pelo
	 * presidente do comitê de extensão fazendo lock na tabela
	 * extensao.codigo_acao (concorrência!)...
	 * 
	 * @param ano
	 * @param idTipoAtividade
	 * @return
	 */
	public int findNextSequencia(int ano, int idTipoAtividade) {
		Integer result = null;
		try {

			result = getJdbcTemplate()
					.queryForInt(
							"select sequencia from extensao.codigo_acao where ano=? and id_tipo_acao=? for update",
							new Object[] { ano, idTipoAtividade });

		} catch (EmptyResultDataAccessException e) {
			return result = 0;
		}

		return result;
	}

	/**
	 * Atualiza a sequência de geração dos códigos das atividades de extensão
	 * 
	 * @param ano
	 * @param idTipoAtividade
	 * @param next
	 */
	public void updateNextSequencia(int ano, int idTipoAtividade, int next) {
				update(
						"update extensao.codigo_acao set sequencia = ? where ano = ? and id_tipo_acao = ?",
						new Object[] { next, ano, idTipoAtividade });
	}

	/**
	 * Insere nova sequência
	 * 
	 * @param ano
	 * @param idTipoAtividade
	 */
	public void novaSequencia(int ano, int idTipoAtividade) {
		update(
				"insert into extensao.codigo_acao values(?, ?, ?)",
				new Object[] { ano, idTipoAtividade, 1 });
	}

	/**
	 * Retorna todos as ações que podem solicitar reconsideração ou que já
	 * solicitaram reconsideração
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */	
	@SuppressWarnings("unchecked")
	public Collection<AtividadeExtensao> findByPassiveisReconsideracao(int idServidor) throws DAOException {
		if (idServidor > 0) {
					StringBuffer hql = new StringBuffer();
					hql.append("SELECT atv FROM AtividadeExtensao atv "
							+ "JOIN atv.projeto proj "
							+ "JOIN proj.coordenador coord "
							+ "LEFT JOIN proj.edital edital "
							+ "WHERE atv.ativo = trueValue() " 
							+ "AND proj.ativo = trueValue() "
							+ "AND proj.tipoProjeto = :tipoProjeto "
							+ "AND coord.servidor.id = :idServidor "
							+ "AND proj.situacaoProjeto.id IN (:SITUACOES_PARA_RECONSIDERACAO, :ANALISANDO_RECONSIDERACAO)");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idServidor", idServidor);
			query.setParameterList("SITUACOES_PARA_RECONSIDERACAO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_SOLICITAR_RECONSIDERACAO);
			query.setInteger("ANALISANDO_RECONSIDERACAO", TipoSituacaoProjeto.EXTENSAO_ANALISANDO_SOLICITACAO_RECONSIDERACAO);
			query.setInteger("tipoProjeto", TipoProjeto.EXTENSAO);
			return query.list();

		} else {
			return null;
		}

	}

	/**
	 * Retorna o total de participantes (público alvo) das ações de extensão
	 * dependendo dos parâmetros 
	 * 
	 * @param ano
	 * @param tipoAtividade
	 * @param tipoParticipante
	 * @param unidade	 
	 * @param dataFim 
	 * @param dataInicio 
	 * @return
	 * @throws DAOException
	 */	
	public Collection<Object[]> totaisParticipantes(Integer ano, Integer tipoAtividade, Integer tipoParticipante, Integer unidade, Date dataInicio, Date dataFim)
			throws DAOException {

		try {

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
					.append(" SELECT tae.descricao, pae.tipoParticipacao.descricao, count(pae.id), unid.nome, ativ.ano "
							+ "FROM   ParticipanteAcaoExtensao pae "
							+ "inner join pae.acaoExtensao ativ "
							+ "inner join ativ.tipoAtividadeExtensao tae "
							+ "inner join ativ.projeto.unidade unid "
							+ "GROUP BY pae.tipoParticipacao.descricao, tae.descricao, unid.nome, " +
									"ativ.projeto.ano, ativ.projeto.dataInicio, ativ.projeto.dataFim, ativ.tipoAtividadeExtensao.id, pae.tipoParticipacao.id, unid.id ");
			
			hqlConsulta.append(" HAVING 1 = 1 ");

			StringBuilder hqlFiltros = new StringBuilder();

			if (ano != null) {
				hqlFiltros.append(" AND (extract(year from ativ.projeto.dataInicio) <= :ano and extract(year from ativ.projeto.dataFim) >= :ano)");
			}
			if (tipoAtividade != null) {
				hqlFiltros.append(" AND ativ.tipoAtividadeExtensao.id = :tipoAtividade");
			}
			if (tipoParticipante != null) {
				hqlFiltros.append(" AND pae.tipoParticipacao.id = :tipoParticipante");
			}
			if (unidade != null) {
				hqlFiltros.append(" AND unid.id = :unidade");
			}
			
			if ((dataInicio != null) && (dataFim != null)) {
				hqlFiltros.append(" AND " + HibernateUtils.generateDateIntersection("ativ.projeto.dataInicio", "ativ.projeto.dataFim", ":dataInicio", ":dataFim"));
			}
			
			hqlFiltros.append(" ORDER BY ativ.projeto.ano, tae.descricao asc");

			hqlConsulta.append(hqlFiltros.toString());

			// Criando consulta
			Query queryConsulta = getSession().createQuery(
					hqlConsulta.toString());
			

			// Populando os valores dos filtros
			if (ano != null)
				queryConsulta.setInteger("ano", ano);
			
			if (tipoAtividade != null)
				queryConsulta.setInteger("tipoAtividade", tipoAtividade);
			
			if (tipoParticipante != null)
				queryConsulta.setInteger("tipoParticipante", tipoParticipante);
			
			if (unidade != null)
				queryConsulta.setInteger("unidade", unidade);

			if ((dataInicio != null) && (dataFim != null)) {
				queryConsulta.setDate("dataInicio", dataInicio);
				queryConsulta.setDate("dataFim", dataFim);
			}
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();

			ArrayList<Object[]> result = new ArrayList<Object[]>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				Object[] obj = new Object[colunas.length];
				obj[0] = colunas[col++];
				obj[1] = colunas[col++];
				obj[2] = colunas[col++];
				obj[3] = colunas[col++];
				obj[4] = colunas[col++];
				
				result.add(obj);
			}

			return result;

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}
	
	
	/**
	 * Retorna totais de público estimado e atendido
	 * de acordo com os parâmetros de busca.
	 * Os dados de público atingido tem como fonte
	 * os relatórios finais e parciais enviados pelos 
	 * coordenadores de ações.
	 *   
	 * @param idsSituacaoProjeto
	 * @param dataFim 
	 * @param dataInicio 
	 * @return
	 * @throws DAOException
	 */	
	public Collection<Object[]> totaisPublicoEstimadoAtendido(
			Integer[] idsSituacaoProjeto, Date dataInicio, Date dataFim, 
			Date dataInicioConclusao, Date dataFimConclusao)
			throws DAOException {

		try {
		    
		    StringBuilder hqlConsulta = new StringBuilder();
		    hqlConsulta.append(" SELECT " 
			    + " ta.descricao, pj.ano, month(pj.dataFim), "
			    + " sit.descricao, count(distinct atv.id), "

			    + " sum(atv.publicoExterno + atv.publicoInterno), sum(atv.publicoAtendido) "

			    + " FROM AtividadeExtensao atv "
			    + " INNER JOIN atv.tipoAtividadeExtensao ta "
			    + " INNER JOIN atv.projeto pj "
			    + " INNER JOIN pj.situacaoProjeto sit ");


		    hqlConsulta.append(" WHERE 1 = 1 ");

			// Filtros para a busca

			if (idsSituacaoProjeto != null && idsSituacaoProjeto.length > 0) {
			    hqlConsulta.append(" AND sit.id IN (:idsSituacaoProjeto) ");
			}

			hqlConsulta.append(" AND (sit.id not in (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO)) ");

			
			// Data de início da ação deve está dentro do período informado.
			if ((dataInicio != null) && (dataFim != null)) {
			    hqlConsulta.append(" AND (pj.dataInicio >= :dataInicio AND pj.dataInicio <= :dataFim) ");
			}

			// Data de finalização da ação deve está dentro do período informado.
			if ((dataInicioConclusao != null) && (dataFimConclusao != null)) {
			    hqlConsulta.append(" AND (pj.dataFim >= :dataInicioConclusao AND pj.dataFim <= :dataFimConclusao) ");
			}
			
			hqlConsulta.append(" GROUP BY " +
				    "ta.descricao, " +
				    "sit.id, " +
				    "sit.descricao, " +
				    "pj.ano, " +
			    "month(pj.dataFim) ");

			
			hqlConsulta.append(" ORDER BY " +
					"pj.ano, " +
					"month(pj.dataFim), " +
					"ta.descricao, " +
					"month(pj.dataFim)");

			// Criando consulta
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
			// atividades não contabilizadas
			queryConsulta.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
			queryConsulta.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);


			if (idsSituacaoProjeto != null && idsSituacaoProjeto.length > 0) {
				queryConsulta.setParameterList("idsSituacaoProjeto", idsSituacaoProjeto);
			}

			if ((dataInicio != null) && (dataFim != null)) {
				queryConsulta.setDate("dataInicio", dataInicio);
				queryConsulta.setDate("dataFim", dataFim);
			}
			
			if ((dataInicioConclusao != null) && (dataFimConclusao != null)) {
				queryConsulta.setDate("dataInicioConclusao", dataInicioConclusao);
				queryConsulta.setDate("dataFimConclusao", dataFimConclusao);
			}

			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();

			ArrayList<Object[]> result = new ArrayList<Object[]>();
			for (int cont = 0; cont < lista.size(); cont++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(cont);

				Object[] obj = new Object[colunas.length];
				obj[0] = colunas[col++]; // descrição
				obj[1] = colunas[col++]; // ano

				Integer mes = (Integer) colunas[col++];
				if (mes != null)
					obj[2] = CalendarUtils.getMesAbreviado(mes); // mês inicio

				obj[3] = colunas[col++]; // situação
				obj[4] = colunas[col++]; // total ações

				Long pEstimado = (Long) colunas[col++];
				obj[5] = pEstimado == null ? 0 : pEstimado; // publico estimado

				Long pAtendido = (Long) colunas[col++];
				obj[6] = pAtendido == null ? 0 : pAtendido; // publico atendido

				result.add(obj);
			}

			return result;

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}
	/**
	 * 
	 * Retorna todas as bolsas disponíveis de acordo com as opções e
	 * restrições solicitadas e agregada nos parâmetros do método	 	  
	 * 
	 * @param restricoes
	 * @param parametros	  
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */	
	public List<AtividadeExtensao> findBolsasDisponiveis(RestricoesBuscaAgregadorBolsas restricoes, ParametroBuscaAgregadorBolsas parametros)
			throws HibernateException, DAOException {

		StringBuilder hql = new StringBuilder();

		hql.append(
					" select atv.id, atv.bolsasConcedidas, proj.titulo, und.id, und.nome, und.sigla, undResponsavel.id, undResponsavel.nome, " +
					"  me.id, funcao.id, funcao.descricao, serv.id, pessoa.id, pessoa.nome, de.id, de.ativo, tipoVinculo.id, " +
				    " tipoVinculo.descricao, sitDiscenteExt.id, sitDiscenteExt.descricao " +
				    " FROM AtividadeExtensao atv " +
				    " inner join atv.tipoAtividadeExtensao as tipoAtv " +
				    " inner join atv.projeto as proj  " +
				    " inner join proj.situacaoProjeto as sitProj " +
				    " inner join proj.unidade as und " +
				    " inner join und.unidadeResponsavel as undResponsavel " +
				    " left  join atv.discentesSelecionados as de " +
				    " left  join de.tipoVinculo as tipoVinculo " +
				    " left  join de.situacaoDiscenteExtensao as sitDiscenteExt " +
				    " inner join proj.equipe as me " +
				    " inner join me.funcaoMembro as funcao " +
				    " inner join me.servidor as serv " +
				    " inner join serv.pessoa as pessoa "
				  );		
		
		if (restricoes.isBuscaServidor())
			hql.append(" where me.servidor.id = :idDocente");
		else
			hql.append(" where 1 = 1 ");		
		
		hql.append(	" and sitProj.id in (:situacaoAtivos) " );
		 
		if (restricoes.isBuscaDepartamento())
			hql.append(" and und.id = :departamento");
		if (restricoes.isBuscaCentro())
			hql.append(" and undResponsavel.id = :centro or und.id = :centro");
		if (restricoes.isBuscaPalavraChave())
			hql.append(" and (lower(proj.resumo) like lower(:palavraChave) or lower(proj.titulo) like lower(:palavraChave))");
		if (restricoes.isBuscaTipoAtividade())
			hql.append(" and tipoAtv.id = :idTipoAtividade");
		if (restricoes.isBuscaAno())
			hql.append(" and proj.ano = :ano");
		
		hql.append(" order by atv.id, atv.bolsasConcedidas desc ");
		
		
		Query q = getSession().createQuery(hql.toString());

		q.setParameterList("situacaoAtivos", new Integer[] {TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO});
		
		if (restricoes.isBuscaServidor())
			q.setInteger("idDocente", parametros.getServidor().getId());
		if (restricoes.isBuscaDepartamento())
			q.setInteger("departamento", parametros.getDepartamento());
		if (restricoes.isBuscaCentro())
			q.setInteger("centro", parametros.getCentro());
		if (restricoes.isBuscaPalavraChave())
			q.setString("palavraChave", "%" + parametros.getPalavraChave() + "%");
		if (restricoes.isBuscaTipoAtividade())
			q.setInteger("idTipoAtividade", parametros.getTipoAtividade());
		if (restricoes.isBuscaAno())
			q.setInteger("ano", parametros.getAno());
		

		
		
		List<AtividadeExtensao> resultado = new ArrayList<AtividadeExtensao>();
		@SuppressWarnings("unchecked")
		List<Object> lista = q.list();
		
		AtividadeExtensao atvAtual = new AtividadeExtensao();
		for (int i = 0; i < lista.size(); i++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(i);

			Integer idNew = (Integer) colunas[col++]; 
			
			//nova extensão
			if (atvAtual.getId() != idNew) {
				atvAtual = new AtividadeExtensao();
				
				atvAtual.setId(idNew);
				atvAtual.setBolsasConcedidas((Integer) colunas[col++]);				
				atvAtual.setTitulo((String) colunas[col++]);
				atvAtual.getUnidade().setId((Integer) colunas[col++]);
				atvAtual.getUnidade().setNome((String) colunas[col++]);
				atvAtual.getUnidade().setSigla((String) colunas[col++]);
				atvAtual.getUnidade().setUnidadeResponsavel(new Unidade());
				atvAtual.getUnidade().getUnidadeResponsavel().setId((Integer) colunas[col++]);
				atvAtual.getUnidade().getUnidadeResponsavel().setNome((String) colunas[col++]);
				
				
				resultado.add(atvAtual);
			}
			
			col = 8;
			
			//adiciona os membros da equipe
			MembroProjeto mp = new MembroProjeto();
			
			mp.setId((Integer) colunas[col++]);
			mp.setFuncaoMembro(new FuncaoMembro());
			mp.getFuncaoMembro().setId((Integer) colunas[col++]);
			mp.getFuncaoMembro().setDescricao((String) colunas[col++]);			
			mp.getServidor().setId((Integer) colunas[col++]);
			
			Pessoa p = new Pessoa();
			
			p.setId((Integer) colunas[col++]);
			p.setNome((String) colunas[col++]);
			mp.getServidor().setPessoa(p);
			
			
			if(colunas[col] != null) {			
				
				DiscenteExtensao de = new DiscenteExtensao();
				de.setId((Integer) colunas[col++]);
				de.setAtivo((Boolean) colunas[col++]);
				de.getTipoVinculo().setId((Integer) colunas[col++]);
				de.getTipoVinculo().setDescricao((String) colunas[col++]);
				de.getSituacaoDiscenteExtensao().setId((Integer) colunas[col++]);
				de.getSituacaoDiscenteExtensao().setDescricao((String) colunas[col++]);
				
				if(!resultado.get(resultado.indexOf(atvAtual)).getDiscentesSelecionados().contains(de))			
					resultado.get(resultado.indexOf(atvAtual)).getDiscentesSelecionados().add(de);				
				
			}
			
			
			resultado.get(resultado.indexOf(atvAtual)).getMembrosEquipe().add(mp);
			
			
		}
		
		return resultado;

	}
	
	
	/**
	 * Retorna todas as atividade com seus orçamentos consolidados aprovados 
	 * pelos membros do comitê de extensão.
	 * 
	 * Utilizado no relatório que auxilia a descentralização dos orçamentos aprovados 
	 * para os centros acadêmicos.
	 * 
	 * @param ano
	 * @param tipoAtividade
	 * @param tipoParticipante
	 * @param unidade	 
	 * @return
	 * @throws DAOException
	 */	
	public Collection<AtividadeExtensao> findAcoesOrcamentosConsolidadosByCentro(Integer idEdital, Unidade unidadeResponsavel, Integer idAreaTematica, Boolean associada)
			throws DAOException {

		try {

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta
					.append(" SELECT atv.id, atv.projeto.ano, atv.sequencia, tae, atv.projeto.titulo, area,  " +
							" unidade.codigo, unidade.id, unidade.nome, unidade.sigla, " 
							+ "gestora.nome, gestora.sigla, " 
							+ "coord.id, coord.pessoa, coord.funcaoMembro, atv.bolsasConcedidas, orcamento "
							+ "FROM AtividadeExtensao atv " 
							+ "JOIN atv.editalExtensao edital "
							+ "JOIN atv.areaTematicaPrincipal area "
							+ "JOIN atv.projeto.coordenador coord "
							+ "JOIN atv.tipoAtividadeExtensao tae "
							+ "JOIN atv.projeto.unidade unidade " 
							+ "JOIN unidade.gestora gestora "
							+ "LEFT JOIN atv.projeto.orcamentoConsolidado orcamento "
							+ "WHERE atv.projeto.situacaoProjeto.id IN (:idSituacaoEmExecucao) "); 

			StringBuilder hqlFiltros = new StringBuilder();

			
			if (associada != null) {
				hqlFiltros.append(" AND (atv.projeto.tipoProjeto.id = :idTipoProjeto)");
			}
			
			if (idEdital != null) {
				hqlFiltros.append(" AND edital.id = :idEdital");
			}
			
			if (unidadeResponsavel != null) {
				hqlFiltros.append(" AND unidade.hierarquia like :idUnidadeResponsavel ");
			}

			if (idAreaTematica != null) {
				hqlFiltros.append(" AND area.id = :idAreaTematica");
			}
			
			hqlFiltros.append(" ORDER BY unidade.id, tae.id, atv.id, atv.projeto.ano, atv.sequencia, orcamento.elementoDespesa.id");

			hqlConsulta.append(hqlFiltros.toString());

			// Criando consulta
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
			

			// Populando os valores dos filtros
			// Somente ações em execução
			queryConsulta.setParameterList("idSituacaoEmExecucao", new Integer[] {TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO});
			
			if (idEdital != null) {
				queryConsulta.setInteger("idEdital", idEdital);
			}
			
			if (unidadeResponsavel != null) {
				queryConsulta.setString("idUnidadeResponsavel", "%."+unidadeResponsavel.getId()+".%");
			}

			if (idAreaTematica != null) {
				queryConsulta.setInteger("idAreaTematica", idAreaTematica);
			}
			
			if (associada != null) {
				int idTipoProjeto = associada ? TipoProjeto.ASSOCIADO : TipoProjeto.EXTENSAO;
				queryConsulta.setInteger("idTipoProjeto", idTipoProjeto);
			}

			
			List<AtividadeExtensao> resultado = new ArrayList<AtividadeExtensao>();
			
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();
			
			AtividadeExtensao atvAtual = new AtividadeExtensao();
			for (int i = 0; i < lista.size(); i++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(i);

				Integer idNew = (Integer) colunas[col++]; 
				
				//nova ação
				if (atvAtual.getId() != idNew) {
					atvAtual = new AtividadeExtensao();
					
					atvAtual.setId(idNew);
					atvAtual.setAno((Integer) colunas[col++]);
					atvAtual.setSequencia((Integer) colunas[col++]);
					atvAtual.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
					atvAtual.setTitulo((String) colunas[col++]);					
					atvAtual.setAreaTematicaPrincipal((AreaTematica) colunas[col++]);
					
					Unidade u = new Unidade();
					u.setCodigo((Long) colunas[col++]);
					u.setId((Integer) colunas[col++]);
					u.setNome((String) colunas[col++]);
					u.setSigla((String) colunas[col++]);
					atvAtual.setUnidade(u);

					Unidade gestora = new Unidade();
					gestora.setNome((String) colunas[col++]);
					gestora.setSigla((String) colunas[col++]);					
					atvAtual.getUnidade().setUnidadeResponsavel(gestora);
					
					MembroProjeto coordenador = new MembroProjeto();
					coordenador.setId((Integer) colunas[col++]);
					coordenador.setPessoa((Pessoa) colunas[col++]);
					coordenador.setFuncaoMembro((FuncaoMembro) colunas[col++]);
					atvAtual.getProjeto().setCoordenador(coordenador);
					
					atvAtual.setBolsasConcedidas((Integer) colunas[col++]);					
					resultado.add(atvAtual);
				}
				
				//adiciona orçamento a ação de extensão atual
				OrcamentoConsolidado orcamento = new OrcamentoConsolidado();
					orcamento = (OrcamentoConsolidado) colunas[16];
				
				resultado.get(resultado.indexOf(atvAtual)).getOrcamentosConsolidados().add(orcamento);
			}
			
			return resultado;
			

		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}
	

	/**
	 * Retorna a lista de todas as unidades (responsáveis ou proponentes) que submeteram 
	 * propostas a um determinado edital.
	 * 
	 * Se unidadeResponsavel = true retorna todas as unidades 
	 * responsáveis da unidade proponente da ação
	 * Se unidadeResponsavel = false retorna lista de todas as unidades
	 * proponentes que submeteram propostas.
	 * 
	 * 
	 * @param idEdital
	 * @param unidadeResponsavel
	 * @return
	 * @throws DAOException
	 */
	private Collection<Unidade> getUnidadesByEditalExtensao(Integer idEdital, boolean unidadeResponsavel)throws DAOException {

		if (idEdital == null)
			throw new IllegalArgumentException("Parâmetro idEdital é obrigatório.");
		
		StringBuilder hqlConsulta = new StringBuilder();
		
		if (unidadeResponsavel) {
			hqlConsulta.append(" SELECT distinct u.id, u.nome, u.sigla " 
						+ "FROM  AtividadeExtensao atv " 
						+ "inner join atv.editalExtensao edital "
						+ "inner join atv.projeto.unidade unidade, Unidade u " 
						+ "where atv.ativo = trueValue() " + //somente unidades de ações ativas
								" and edital.id = :idEdital " +
								" and ((u.id = unidade.gestora.id) " +
								" or (u.id = unidade.id and u.tipoAcademica in " + gerarStringIn(new int[] {TipoUnidadeAcademica.ESCOLA, TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA})  + " )) " +
						" order by u.nome "); 
		}else {
			
			hqlConsulta.append(" SELECT unidade.id, unidade.nome, unidade.sigla " 
					+ "FROM  AtividadeExtensao atv " 
					+ "inner join atv.editalExtensao edital "
					+ "inner join atv.projeto.unidade unidade "						
					+ "where edital.id = :idEdital "
					+ "group by unidade.id, unidade.nome, unidade.sigla "); 
		}
		
		// Criando consulta
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());		
			queryConsulta.setInteger("idEdital", idEdital);
		
		List<Unidade> resultado = new ArrayList<Unidade>();
		@SuppressWarnings("unchecked")
		List<Object> lista = queryConsulta.list();
		
		for (int i = 0; i < lista.size(); i++) {
			int col = 0;
			Object[] colunas = (Object[]) lista.get(i);
			
			Unidade unid = new Unidade();
			unid.setId((Integer) colunas[col++]);
			unid.setNome((String) colunas[col++]);
			unid.setSigla((String) colunas[col++]);					
			
			resultado.add(unid);
		}
		
		return resultado;		

	}

	
	/**
	 * Retorna lista de unidades responsáveis das unidades proponentes que submeteram propostas ao edital informado.
	 * 
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> getUnidadesResponsaveisByEditalExtensao(Integer idEdital)throws DAOException {
		return getUnidadesByEditalExtensao(idEdital, true);
	}
	
	/**
	 * Retorna lista com a quantidade de ações, público e membros da equipe relacionados por área temática em um determinado ano ou período.
	 * 
	 * @param tipoAcao, tipoSituacao, ano, inicio, fim
	 * @return List<HashMap<String, Object>>
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> quantidadeAcoesPublicoMembroPorArea(Integer tipoAcao, Integer tipoSituacao, 
			Integer ano, Date inicio, Date fim) throws DAOException {
				
		StringBuilder consultaSql = new StringBuilder();		
		consultaSql.append("select descricao, totais.acoes, publico.estimado, publico.atingido, totais.docentes, ");
		consultaSql.append("totais.bolsistas, totais.nao_bolsistas, totais.alunos_pos, totais.tecnicos, totais.externos ");
		consultaSql.append("from extensao.area_tematica area left join (select at.id_area_tematica, count(distinct a.id_atividade) as acoes, ");
		consultaSql.append("count(distinct docentes.id_servidor) as docentes, count(distinct bolsistas.id_discente_extensao) as bolsistas, ");
		consultaSql.append("count(distinct voluntarios.id_discente_extensao) as nao_bolsistas, count(distinct discentes_pos.id_discente) as alunos_pos, ");
		consultaSql.append("count(distinct tecnicos.id_servidor) as tecnicos, count(distinct mp.id_docente_externo) as externos ");
		consultaSql.append("from extensao.area_tematica at join extensao.atividade a on(a.id_area_tematica_principal = at.id_area_tematica ");
		
		if (tipoAcao != null && tipoAcao != 0) {
			consultaSql.append("and a.id_tipo_atividade_extensao = " + tipoAcao);
		}
		consultaSql.append(") join projetos.projeto proj on(proj.id_projeto = a.id_atividade ");
		
		if (tipoSituacao != null && tipoSituacao != 0) {
			consultaSql.append(" and proj.id_tipo_situacao_projeto in (" + tipoSituacao + ") ");
		}
		consultaSql.append("and proj.id_tipo_situacao_projeto <> 108 ");
		
		if (ano != null){
			consultaSql.append("and (extract(year from proj.data_inicio) <= " + ano + " and extract(year from proj.data_fim) >= " + ano + ")  ");
		}else if (inicio != null && fim != null){
			consultaSql.append("and " + HibernateUtils.generateDateIntersection("proj.data_inicio", "proj.data_fim", "?", "?"));
		}
			
		consultaSql.append(") left join projetos.membro_projeto mp on(mp.id_atividade = a.id_atividade and mp.ativo = trueValue()) ");
		consultaSql.append("left join rh.servidor docentes on(mp.id_servidor = docentes.id_servidor and docentes.id_categoria = 1) ");
		consultaSql.append("left join rh.servidor tecnicos on(mp.id_servidor = tecnicos.id_servidor and tecnicos.id_categoria = 2) ");
		consultaSql.append("left join discente discentes_pos on(mp.id_discente = discentes_pos.id_discente and discentes_pos.nivel in ('L','S','E','D')) ");
		consultaSql.append("left join extensao.discente_extensao bolsistas on((bolsistas.id_atividade = a.id_atividade) and bolsistas.id_tipo_vinculo_discente in(2,4)) ");
		consultaSql.append("left join extensao.discente_extensao voluntarios on((voluntarios.id_atividade = a.id_atividade) and voluntarios.id_tipo_vinculo_discente in(1,3)) ");
		consultaSql.append("group by at.id_area_tematica) as totais on (area.id_area_tematica = totais.id_area_tematica) left join (select at.id_area_tematica, ");
		consultaSql.append("coalesce(sum(a.publico_estimado),0) as estimado, coalesce(sum(a.publico_atendido),0) as atingido from extensao.area_tematica at ");
		consultaSql.append("join extensao.atividade a on(a.id_area_tematica_principal = at.id_area_tematica ");
		
		if (tipoAcao != null && tipoAcao != 0) {
			consultaSql.append("and a.id_tipo_atividade_extensao = " + tipoAcao);
		}
		consultaSql.append(") join projetos.projeto proj on(proj.id_projeto = a.id_projeto");
		
		if (tipoSituacao != null && tipoSituacao != 0) {
			consultaSql.append("and proj.id_tipo_situacao_projeto in (" + tipoSituacao + ") ");
		}
		consultaSql.append("and proj.id_tipo_situacao_projeto <> 108 ");
		if (ano != null) {
			consultaSql.append("and (extract(year from proj.data_inicio) <= " + ano + " and extract(year from proj.data_fim) >= " + ano + ")  ");
		}
		else if (inicio != null && fim != null){
			consultaSql.append("and " + HibernateUtils.generateDateIntersection("proj.data_inicio", "proj.data_fim", "?", "?"));
		}
		
		consultaSql.append(") group by at.id_area_tematica) as publico on (area.id_area_tematica = publico.id_area_tematica) order by area.descricao");
		
		if (inicio != null && fim != null) {
			return getJdbcTemplate().queryForList(consultaSql.toString(), new Object[] { inicio.getTime(), inicio.getTime(), fim.getTime(), inicio.getTime(), inicio.getTime(), inicio.getTime(), inicio.getTime(), fim.getTime(), inicio.getTime(), inicio.getTime() });
		} else {
			return getJdbcTemplate().queryForList(consultaSql.toString());
		}
	}


	/**
	 * Retorna uma ação de extensão a partir do id do projeto
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public AtividadeExtensao findAcaoByProjeto(int idProjeto)throws DAOException {
	    return findAcaoByProjetoAndTipoAcao(idProjeto, null);
	}

	/**
	 * Retorna uma ação de extensão a partir de um projeto e o tipo de ação informado.
	 * 
	 * @param idProjeto
	 * @param idTipoAcao se for igual a null retorna só a ação com id do projeto informado.
	 * @return
	 * @throws DAOException
	 */
	public AtividadeExtensao findAcaoByProjetoAndTipoAcao(Integer idProjeto, Integer idTipoAcao)throws DAOException {
		StringBuilder hqlConsulta = new StringBuilder();
		hqlConsulta.append(" SELECT atv.id, atv.tipoAtividadeExtensao, cursoEvento.id, cursoEvento.cargaHoraria,  " 
					+ " p.id, p.dataInicio, p.dataFim, p.ativo, p.ano, p.titulo, p.ensino, p.pesquisa, p.extensao, ta  " 
					+ " FROM  AtividadeExtensao atv " 
					+ " INNER JOIN atv.tipoAtividadeExtensao ta " 
					+ " INNER JOIN atv.projeto p "
					+ " LEFT JOIN  atv.cursoEventoExtensao cursoEvento "
					+ " WHERE p.id = :idProjeto ");
		
		if (idTipoAcao != null) {
		    hqlConsulta.append(" AND ta.id = :idTipoAcao ");
		}
		
		// Criando consulta
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());		
		queryConsulta.setInteger("idProjeto", idProjeto);
		
		if (idTipoAcao != null) {
			queryConsulta.setInteger("idTipoAcao", idTipoAcao);		    
		}
		
		queryConsulta.setMaxResults(1);		
		Object[] colunas = (Object[]) queryConsulta.uniqueResult();

		if (colunas != null) {
		    int col = 0;
		    AtividadeExtensao atv = new AtividadeExtensao();
		    atv.setId((Integer) colunas[col++]);	
		    atv.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
		    
		    if (colunas[2] != null) {
			CursoEventoExtensao ce = new CursoEventoExtensao();
			ce.setId((Integer) colunas[col++]);
			ce.setCargaHoraria((Integer) colunas[col++]);
			atv.setCursoEventoExtensao(ce);
		    }else {
			col++;
			col++;
		    }
		    
		    Projeto p = new Projeto();
		    p.setId((Integer) colunas[col++]);
		    p.setDataInicio((Date) colunas[col++]);
		    p.setDataFim((Date) colunas[col++]);
		    p.setAtivo((Boolean) colunas[col++]);
		    p.setAno((Integer) colunas[col++]);
		    p.setTitulo((String) colunas[col++]);
		    p.setEnsino((Boolean) colunas[col++]);
		    p.setPesquisa((Boolean) colunas[col++]);
		    p.setExtensao((Boolean) colunas[col++]);
		    atv.setProjeto(p);
		    atv.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
		    return atv;		
		}else {
		    return null;
		}
	}

	
	/**
	 * Retorna todas as atividades de extensão do tipo Cursos e Eventos nas quais o docente seja coordenador.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeExtensao> findCursoEventoAtivosEmExecucaoByCoordenador(Servidor servidor) throws DAOException {
		
		try {
			
			String projecao = 
					" atividadeExtensao.id, atividadeExtensao.sequencia, " +
					" atividadeExtensao.tipoAtividadeExtensao," +
					" atividadeExtensao.projeto.id, atividadeExtensao.projeto.ano, atividadeExtensao.projeto.titulo, " +
					" atividadeExtensao.cursoEventoExtensao.id, atividadeExtensao.cursoEventoExtensao.numeroVagas, ";
			
			// Conta a quantidade de inscritos e APROVADOS para essa atividade  //
			projecao += "(SELECT count(inscricao.id) " +
					" FROM InscricaoAtividadeParticipante inscricao 	" +
					" JOIN inscricao.inscricaoAtividade as inscAtv " +
					" JOIN inscAtv.atividade as atividadeExtensaoInterna	" +
					" WHERE atividadeExtensaoInterna.id = atividadeExtensao.id " +
					" AND inscricao.statusInscricao.id = :idStatus) as numeroInscritos ";
			
			StringBuilder hql = new StringBuilder();
			hql.append(" select " + projecao + " from AtividadeExtensao atividadeExtensao ");
			hql.append(" inner join atividadeExtensao.projeto.coordenador coordenador ");
			hql.append(" where coordenador.servidor.id = :idServidor and coordenador.ativo = trueValue() ");
			hql.append(" and atividadeExtensao.ativo = :true AND atividadeExtensao.projeto.ativo = :true "); // Sempre usar a consulta mais restritiva possível para evitar erros
			hql.append(" and atividadeExtensao.projeto.tipoProjeto.id = :idTipoProjeto ");
			hql.append(" and atividadeExtensao.situacaoProjeto.id in (:idSituacaoExecucao) ");
			hql.append(" and atividadeExtensao.cursoEventoExtensao is not null order by atividadeExtensao.id ");			
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idServidor", servidor.getId());
			query.setBoolean("true", true);
			query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
			query.setParameterList("idSituacaoExecucao", TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
			query.setInteger("idStatus", StatusInscricaoParticipante.APROVADO);
			
			List<Object> lista = query.list();
			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				AtividadeExtensao at = new AtividadeExtensao();
				at.setId((Integer) colunas[col++]);
				at.setSequencia((Integer) colunas[col++]);
				at.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
				at.setProjeto(new Projeto((Integer) colunas[col++]));
				at.setAno((Integer) colunas[col++]);
				at.setTitulo((String) colunas[col++]);
				at.setCursoEventoExtensao(new CursoEventoExtensao((Integer) colunas[col++]));
				at.getCursoEventoExtensao().setNumeroVagas((Integer) colunas[col++]);
				//at.getCursoEventoExtensao().setNumeroInscritos (((Long) colunas[col++]).intValue());
				result.add(at);
			}
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * Retorna todas as SubAtividades de extensão do tipo Cursos e Eventos nas quais o docente seja coordenador.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeExtensao> findSubAtividadeCursoEventoByCoordenador(Servidor servidor) throws DAOException {
		
		try {
			
			String projecao = " atv.id, atv.sequencia, atv.tipoAtividadeExtensao," +
							  " atv.projeto.id, atv.projeto.ano, atv.projeto.titulo, " +
							  " subAtv2, atv.cursoEventoExtensao.id,  " +
					
					// Conta a quantidade de inscritos para essa atividade com uma sub consulta //
					" (SELECT count(inscricao.id) " +
					" FROM InscricaoAtividadeParticipante inscricao 	" +
					" JOIN inscricao.inscricaoAtividade as inscAtv " +
					" JOIN inscAtv.subAtividade as subAtv	" +
					" JOIN subAtv.atividade as atv2 " +
					" WHERE atv2.id = atv.id AND inscricao.ativo = trueValue() and subAtv.id = subAtv2.id " +
					" AND inscricao.statusInscricao.id = :idStatus ) as numeroInscritos ";
			
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT " + projecao + "" +
					   " FROM SubAtividadeExtensao subAtv2 " +
					   " INNER join subAtv2.atividade atv ");
			hql.append(" INNER join atv.projeto.coordenador coordenador ");
			hql.append(" WHERE subAtv2.ativo = trueValue() AND atv.ativo = trueValue() ");
			hql.append(" AND (( coordenador.servidor.id = :idServidor AND coordenador.ativo = trueValue() ) or atv.id in ( :idProjetos ) ) ");
			hql.append(" AND atv.projeto.tipoProjeto.id = :idTipoProjeto ");
			hql.append(" AND atv.situacaoProjeto.id in (:idSituacaoExecucao) ");
			hql.append(" AND atv.cursoEventoExtensao is not null" +
					   " ORDER BY atv.id ");			
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idServidor", servidor.getId());
			query.setParameterList("idProjetos", DesignacaoFuncaoProjetoHelper.atividadesByCoordenadoresOrDesignacaoCoordenador(servidor.getId()));
			query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
			query.setParameterList("idSituacaoExecucao", TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO);
			query.setInteger("idStatus", StatusInscricaoParticipante.APROVADO);
			
			List<Object> lista = query.list();
			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				AtividadeExtensao at = new AtividadeExtensao();
				at.setId((Integer) colunas[col++]);
				at.setSequencia((Integer) colunas[col++]);
				at.setTipoAtividadeExtensao((TipoAtividadeExtensao) colunas[col++]);
				at.setProjeto(new Projeto((Integer) colunas[col++]));
				at.setAno((Integer) colunas[col++]);
				at.setTitulo((String) colunas[col++]);
				at.setSubAtividade((SubAtividadeExtensao) colunas[col++]);				
				at.setCursoEventoExtensao(new CursoEventoExtensao((Integer) colunas[col++]));
				at.getSubAtividade().setNumeroInscritos(((Long) colunas[col++]).intValue());			
				
				
				result.add(at);
			}
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	
	
	
	/**
	 * Retornar os tipos de atividade Curso e Evento.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TipoAtividadeExtensao> findTipoCursoEvento() throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select tipo from TipoAtividadeExtensao tipo ");
			hql.append("where tipo.ativo = trueValue() and tipo.id in(:idCurso, :idEvento) ");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idCurso", TipoAtividadeExtensao.CURSO);
			query.setInteger("idEvento", TipoAtividadeExtensao.EVENTO);
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Retorna as atividades preenchidas com seus respectivos membros (de acordo com os parâmetros de busca)
	 * 
	 * 
	 * @param tipoAtividade
	 * @param situacaoAtividade
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public Collection<AtividadeExtensao> relatorioEquipePorModalidade(Integer tipoAtividade, Integer situacaoAtividade, Date dataInicio, Date dataFim) {
		
		StringBuilder hqlConsulta = new StringBuilder();
		StringBuilder hqlFiltros = new StringBuilder();
		
		hqlConsulta.append(	" select distinct ativ.id, ativ.sequencia, tipoAtiv.id, tipoAtiv.descricao, proj.dataInicio, proj.dataFim, proj.id, proj.ano, proj.titulo," +
							" mp.id, funcaoMembro.id, funcaoMembro.descricao, categMembro.id, categMembro.descricao, pessoa.id, pessoa.nome " +
							" from AtividadeExtensao ativ " +
							" inner join ativ.tipoAtividadeExtensao tipoAtiv " +
							" inner join ativ.projeto proj " +
							" inner join proj.situacaoProjeto sitProj " +
							" inner join proj.equipe mp " +
							" inner join mp.funcaoMembro funcaoMembro " +
							" inner join mp.pessoa pessoa" +
							" inner join mp.categoriaMembro categMembro " +
							" where mp.ativo = trueValue() ");
		
		
		if( tipoAtividade != null ) {
			hqlFiltros.append(" and tipoAtiv.id = :tipoAtividade ");
		}
			
		if( situacaoAtividade != null ) {
			hqlFiltros.append(" and sitProj = :situacaoAtividade ");
		}
		
		if( dataInicio != null && dataFim != null) {
			hqlFiltros.append(" and " + HibernateUtils.generateDateIntersection("proj.dataInicio", "proj.dataFim", ":dataInicio", ":dataFim"));
		}
		
		try {
			
			hqlConsulta.append(hqlFiltros);
			hqlConsulta.append(" order by ativ.id, categMembro.id, funcaoMembro.id ");
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
		
			if( tipoAtividade != null ) {
				queryConsulta.setInteger("tipoAtividade", tipoAtividade);
			}
			
			if( situacaoAtividade != null ) {
				queryConsulta.setInteger("situacaoAtividade", situacaoAtividade);
			}
		
			if( dataInicio != null && dataFim != null) {
				queryConsulta.setDate("dataInicio", dataInicio);
				queryConsulta.setDate("dataFim", dataFim);
			}
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();			
			
			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();			
			
			int idOld = 0;
			AtividadeExtensao at = new AtividadeExtensao();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);				

				int idNew = (Integer) colunas[col++];
				

				if (idOld != idNew) {

					idOld = idNew;
					at = new AtividadeExtensao();
					at.setId(idNew);
					at.setSequencia((Integer) colunas[col++]);
					at.getTipoAtividadeExtensao().setId((Integer) colunas[col++]);
					at.getTipoAtividadeExtensao().setDescricao((String) colunas[col++]);
					at.setDataInicio((Date) colunas[col++]);
					at.setDataFim((Date) colunas[col++]);
					at.getProjeto().setId(((Integer) colunas[col++]));					
					at.getProjeto().setAno((Integer) colunas[col++]);
					at.getProjeto().setTitulo((String) colunas[col++]);				

					result.add(at);
				}

				col = 9;
				if (colunas[col] != null) {
					MembroProjeto mp = new MembroProjeto();
					mp.setId((Integer) colunas[col++]);
					mp.setFuncaoMembro(new FuncaoMembro());
					mp.getFuncaoMembro().setId((Integer) colunas[col++]);
					mp.getFuncaoMembro().setDescricao((String) colunas[col++]);
					mp.setCategoriaMembro(new CategoriaMembro());
					mp.getCategoriaMembro().setId((Integer) colunas[col++]);
					mp.getCategoriaMembro().setDescricao((String) colunas[col++]);
					mp.getPessoa().setId((Integer) colunas[col++]);
					mp.getPessoa().setNome((String) colunas[col++]);
					
					at.getMembrosEquipe().add(mp);					
				}
			}

			
			return result;
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	

	/**
	 * Retorna as atividades preenchidas com seus respectivos discentes (de acordo com os parâmetros de busca)
	 * 
	 * @param tipoAtividade
	 * @param situacaoAtividade
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public Collection<AtividadeExtensao> relatorioDiscentePorModalidade(Integer tipoAtividade, Integer situacaoAtividade, Date dataInicio, Date dataFim) {
		
		StringBuilder hqlConsulta = new StringBuilder();
		StringBuilder hqlFiltros = new StringBuilder();
		
		hqlConsulta.append(	" select distinct ativ.id, ativ.sequencia, tipoAtiv.id, tipoAtiv.descricao, proj.dataInicio, proj.dataFim, proj.id, proj.ano, proj.titulo," +
							" de.id, tipoVinculo.id, tipoVinculo.descricao, pessoa.id, pessoa.nome " +
							" from AtividadeExtensao ativ " +
							" inner join ativ.tipoAtividadeExtensao tipoAtiv " +
							" inner join ativ.projeto proj " +
							" inner join proj.situacaoProjeto sitProj " +
							" inner join ativ.discentesSelecionados de " +
							" inner join de.discente discente " +
							" inner join discente.pessoa pessoa" +
							" inner join de.tipoVinculo tipoVinculo " +
							" inner join de.planoTrabalhoExtensao plano " +
							" where de.ativo = trueValue() ");
		
		
		if( tipoAtividade != null ) {
			hqlFiltros.append(" and tipoAtiv.id = :tipoAtividade ");
		}
			
		if( situacaoAtividade != null ) {
			hqlFiltros.append(" and sitProj = :situacaoAtividade ");
		}
		
		if( dataInicio != null && dataFim != null) {
			hqlFiltros.append(" and " + HibernateUtils.generateDateIntersection("proj.dataInicio", "proj.dataFim", ":dataInicio", ":dataFim"));
		}
		
		try {
			
			hqlConsulta.append(hqlFiltros);
			hqlConsulta.append(" order by ativ.id, tipoVinculo.id ");
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
		
			if( tipoAtividade != null ) {
				queryConsulta.setInteger("tipoAtividade", tipoAtividade);
			}
			
			if( situacaoAtividade != null ) {
				queryConsulta.setInteger("situacaoAtividade", situacaoAtividade);
			}
		
			if( dataInicio != null && dataFim != null) {
				queryConsulta.setDate("dataInicio", dataInicio);
				queryConsulta.setDate("dataFim", dataFim);
			}
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();			
			
			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();			
			
			int idOld = 0;
			AtividadeExtensao at = new AtividadeExtensao();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);				

				int idNew = (Integer) colunas[col++];
				

				if (idOld != idNew) {

					idOld = idNew;
					at = new AtividadeExtensao();
					at.setId(idNew);
					at.setSequencia((Integer) colunas[col++]);
					at.getTipoAtividadeExtensao().setId((Integer) colunas[col++]);
					at.getTipoAtividadeExtensao().setDescricao((String) colunas[col++]);
					at.setDataInicio((Date) colunas[col++]);
					at.setDataFim((Date) colunas[col++]);
					at.getProjeto().setId(((Integer) colunas[col++]));					
					at.getProjeto().setAno((Integer) colunas[col++]);
					at.getProjeto().setTitulo((String) colunas[col++]);				

					result.add(at);
				}

				col = 9;
				if (colunas[col] != null) {
					DiscenteExtensao de = new DiscenteExtensao();
					de.setId((Integer) colunas[col++]);					
					de.getTipoVinculo().setId((Integer) colunas[col++]);
					de.getTipoVinculo().setDescricao((String) colunas[col++]);
					de.getDiscente().getPessoa().setId((Integer) colunas[col++]);
					de.getDiscente().getPessoa().setNome((String) colunas[col++]);
					
					at.getDiscentesSelecionados().add(de);					
				}
			}

			
			return result;
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Retorna as atividades preenchidas com seus respectivos discentes (de acordo com os parâmetros de busca)
	 * 
	 * @param tipoAtividade
	 * @param situacaoAtividade
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public Collection<AtividadeExtensao> relatorioFinanciamentoInternoExterno(Integer tipoAtividade, Integer situacaoAtividade, Date dataInicio, Date dataFim, 
			Integer idEdital, Integer idAreaTematica) {
		
		StringBuilder hqlConsulta = new StringBuilder();
		StringBuilder hqlFiltros = new StringBuilder();
		
		hqlConsulta.append(	" select distinct ativ.id, ativ.sequencia, tipoAtiv.id, tipoAtiv.descricao," +
							"        proj.dataInicio, proj.dataFim, proj.id, proj.ano, proj.titulo, " +
							"        proj.financiamentoInterno, proj.financiamentoExterno " +							
							" from   AtividadeExtensao ativ " +
							" inner  join ativ.tipoAtividadeExtensao tipoAtiv " +
							" inner  join ativ.projeto proj " +
							" left   join ativ.editalExtensao edital " +
							" inner  join ativ.areaTematicaPrincipal areaTematica " +						
							" inner  join proj.situacaoProjeto sitProj " +														
							" where  ( proj.financiamentoInterno = trueValue() or proj.financiamentoExterno = trueValue() ) ");
		
		
		if( tipoAtividade != null ) {
			hqlFiltros.append(" and tipoAtiv.id = :tipoAtividade ");
		}
			
		if( situacaoAtividade != null ) {
			hqlFiltros.append(" and sitProj = :situacaoAtividade ");
		}
		
		if( dataInicio != null && dataFim != null) {
			hqlFiltros.append(" and " + HibernateUtils.generateDateIntersection("proj.dataInicio", "proj.dataFim", ":dataInicio", ":dataFim"));
		}
		
		
		if( idEdital != null && idEdital != 0 ) {
			hqlFiltros.append(" and edital.id = :idEdital ");
		}
			
		if( idAreaTematica != null && idAreaTematica != 0) {
			hqlFiltros.append(" and areaTematica.id = :idAreaTematica ");
		}
		
		
		
		try {
			
			hqlConsulta.append(hqlFiltros);
			hqlConsulta.append(" order by proj.ano, proj.financiamentoInterno, proj.financiamentoExterno ");
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
			
			
		
			if( tipoAtividade != null ) {
				queryConsulta.setInteger("tipoAtividade", tipoAtividade);
			}
			
			if( situacaoAtividade != null ) {
				queryConsulta.setInteger("situacaoAtividade", situacaoAtividade);
			}
		
			if( dataInicio != null && dataFim != null) {
				queryConsulta.setDate("dataInicio", dataInicio);
				queryConsulta.setDate("dataFim", dataFim);
			}
			
			if( idEdital != null && idEdital != 0 ) {
				queryConsulta.setInteger("idEdital", idEdital);
			}
				
			if( idAreaTematica != null && idAreaTematica != 0) {
				queryConsulta.setInteger("idAreaTematica", idAreaTematica);				
			}			
			
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();			
			
			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();			
			
			
			AtividadeExtensao at = new AtividadeExtensao();
			for (int cont = 0; cont < lista.size(); cont++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(cont);				

									
				at = new AtividadeExtensao();
				at.setId((Integer) colunas[col++]);
				at.setSequencia((Integer) colunas[col++]);
				at.getTipoAtividadeExtensao().setId((Integer) colunas[col++]);
				at.getTipoAtividadeExtensao().setDescricao((String) colunas[col++]);
				at.setDataInicio((Date) colunas[col++]);
				at.setDataFim((Date) colunas[col++]);
				at.getProjeto().setId(((Integer) colunas[col++]));					
				at.getProjeto().setAno((Integer) colunas[col++]);
				at.getProjeto().setTitulo((String) colunas[col++]);				
				at.getProjeto().setFinanciamentoInterno((Boolean) colunas[col++]);
				at.getProjeto().setFinanciamentoExterno((Boolean) colunas[col++]);

				result.add(at);
				}
			
			return result;			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	/**
	 * Retorna o total de Atividades por Localidade. 
	 * 
	 * @param tipoAtividade
	 * @param situacaoAtividade
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public Collection< Map<String,Object> > relatorioAcoesPorLocalRealizacao(Integer tipoAtividade, Integer situacaoAtividade, Date dataInicio, Date dataFim) throws DAOException {
		
		StringBuilder hqlConsulta = new StringBuilder();
		StringBuilder hqlFiltros = new StringBuilder();		
		
		hqlConsulta.append(	" select municipio.nome as cidade, count(municipio.nome) as total" +
				" from AtividadeExtensao as atv " +
				" inner join atv.tipoAtividadeExtensao as tipoAtiv " +
				" inner join atv.projeto as proj " +
				" inner join proj.situacaoProjeto sitProj " +
				" inner join atv.locaisRealizacao local " +
				" inner join local.municipio municipio " +
				" where 1 = 1 " );
		
		if( tipoAtividade != null && tipoAtividade > 0 ) {
			hqlFiltros.append(" and tipoAtiv.id = :tipoAtividade ");
		}
			
		if( situacaoAtividade != null ) {
			hqlFiltros.append(" and sitProj = :situacaoAtividade ");
		}
		
		if( dataInicio != null && dataFim != null) {
			hqlFiltros.append(" and " + HibernateUtils.generateDateIntersection("proj.dataInicio", "proj.dataFim", ":dataInicio", ":dataFim"));
		}	
		
		
		try {
			
			hqlConsulta.append(hqlFiltros);
			hqlConsulta.append(" group by municipio.nome ");
			hqlConsulta.append(" order by municipio.nome ");
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
			
			
		
			if( tipoAtividade != null && tipoAtividade > 0 ) {
				queryConsulta.setInteger("tipoAtividade", tipoAtividade);
			}
			
			if( situacaoAtividade != null ) {
				queryConsulta.setInteger("situacaoAtividade", situacaoAtividade);
			}
		
			if( dataInicio != null && dataFim != null) {
				queryConsulta.setDate("dataInicio", dataInicio);
				queryConsulta.setDate("dataFim", dataFim);
			}				
			
			@SuppressWarnings("unchecked")
			Collection< Map<String,Object> > lista = queryConsulta.list();
			return lista;
			
		}
		catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}
	
	
	/**
	 * Retorna o total de Atividades por Localidade. 
	 * 
	 * @param tipoAtividade
	 * @param situacaoAtividade
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */	
	public Collection< AtividadeExtensao > relatorioNominalAcoesPorLocalRealizacao(Integer tipoAtividade, Integer situacaoAtividade, Date dataInicio, Date dataFim, Integer idMunicipio) {
		
		StringBuilder hqlConsulta = new StringBuilder();
		StringBuilder hqlFiltros = new StringBuilder();		
		
		hqlConsulta.append(	" select  distinct atv.id, atv.sequencia, tipoAtiv.id, tipoAtiv.descricao, sitProj.id, sitProj.descricao, " +
									" proj.ano, proj.titulo, pessoa.nome, pessoa.email, pessoa.telefone, municipio.nome " +
				" from AtividadeExtensao as atv " +
				" inner join atv.tipoAtividadeExtensao as tipoAtiv " +
				" inner join atv.projeto as proj " +
				" inner join proj.situacaoProjeto sitProj " +
				" left  join proj.coordenador coord " +
				" left  join coord.pessoa pessoa " +
				" inner join atv.locaisRealizacao local " +
				" inner join local.municipio municipio " +
				" where 1 = 1 " );
		
		if( tipoAtividade != null && tipoAtividade > 0 ) {
			hqlFiltros.append(" and tipoAtiv.id = :tipoAtividade ");
		}
			
		if( situacaoAtividade != null ) {
			hqlFiltros.append(" and sitProj.id = :situacaoAtividade ");
		}		
		
		if( dataInicio != null && dataFim != null) {
			hqlFiltros.append(" and " + HibernateUtils.generateDateIntersection("proj.dataInicio", "proj.dataFim", ":dataInicio", ":dataFim"));
		}	
		
		if( idMunicipio != null ) {
			hqlFiltros.append(" and municipio.id = :idMunicipio ");
		}
		
		
		try {
			
			hqlConsulta.append(hqlFiltros);			
			hqlConsulta.append(" order by proj.ano ");
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());
			
			
		
			if( tipoAtividade != null && tipoAtividade > 0 ) {
				queryConsulta.setInteger("tipoAtividade", tipoAtividade);
			}
			
			if( situacaoAtividade != null ) {
				queryConsulta.setInteger("situacaoAtividade", situacaoAtividade);
			}
		
			if( dataInicio != null && dataFim != null) {
				queryConsulta.setDate("dataInicio", dataInicio);
				queryConsulta.setDate("dataFim", dataFim);
			}				
			
			if( idMunicipio != null ) {
				queryConsulta.setInteger("idMunicipio", idMunicipio);
			}
			
			@SuppressWarnings("unchecked")
			List<Object> lista = queryConsulta.list();

			ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
			for (int cont = 0; cont < lista.size(); cont++) {
			    int col = 0;
			    Object[] colunas = (Object[]) lista.get(cont);

			    AtividadeExtensao at = new AtividadeExtensao();
			    at.setId((Integer) colunas[col++]);
			    at.setSequencia((Integer) colunas[col++]);
			    at.getTipoAtividadeExtensao().setId((Integer) colunas[col++]);
			    at.getTipoAtividadeExtensao().setDescricao((String) colunas[col++]);
			    at.getProjeto().getSituacaoProjeto().setId((Integer) colunas[col++]);
			    at.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[col++]);
			    at.getProjeto().setAno((Integer) colunas[col++]);
			    at.getProjeto().setTitulo((String) colunas[col++]);
			    at.getProjeto().setCoordenador(new MembroProjeto());
			    at.getProjeto().getCoordenador().getPessoa().setNome((String) colunas[col++]);
			    at.getProjeto().getCoordenador().getPessoa().setEmail((String) colunas[col++]);
			    at.getProjeto().getCoordenador().getPessoa().setTelefone((String) colunas[col++]);
			    at.getLocalRealizacao().getMunicipio().setNome((String) colunas[col++]);
			    
			    
			    result.add(at);
			}
			return result;
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	
	/**
	 * Retorna todas as ações coordenadas pelo servidor informado
	 * que possuem relatórios cadastrados ou não.
	 * 
	 * @param servidor
	 * @param idFuncao
	 * @param idSituacaoAcao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeExtensao> findAcoesComRelatorioByCoordenador(Servidor servidor, Integer[] idSituacaoAcao) throws DAOException {

    		StringBuilder hql = new StringBuilder();
    		hql.append(" SELECT atv.id, atv.sequencia, atv.ativo, tae.id, tae.descricao, " +
    					" pj.id, pj.ativo, pj.ano, pj.titulo, pj.dataInicio, pj.dataFim, " +
    					" rel.id, tr.id, tr.descricao, rel.ativo, rel.dataEnvio, " +
    					" tipoParecerDepartamento.id, tipoParecerDepartamento.descricao, rel.dataValidacaoDepartamento, rel.parecerDepartamento, " +
    					" tipoParecerProex.id, tipoParecerProex.descricao, rel.dataValidacaoProex, rel.parecerProex " +
    				   	" FROM AtividadeExtensao atv " +
    				   	" JOIN atv.projeto pj " +
    				   	" JOIN atv.tipoAtividadeExtensao tae " +
    					" JOIN atv.projeto.coordenador coord " +
    					" LEFT JOIN atv.relatorios rel " +
    					" LEFT JOIN rel.tipoRelatorio tr " +        					
    					" LEFT JOIN rel.tipoParecerDepartamento tipoParecerDepartamento " +
    					" LEFT JOIN rel.tipoParecerProex tipoParecerProex " +
    					" WHERE (( coord.servidor.id = :idServidor AND coord.ativo = trueValue() ) or pj.id in ( :idProjetos ) ) " +
    				   	" AND atv.projeto.situacaoProjeto.id NOT IN (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +
    				   	" AND atv.ativo = trueValue() " +
    				   	" AND atv.projeto.tipoProjeto.id = :idTipoProjeto ");
    		
    		if (idSituacaoAcao != null) {
    			hql.append("AND pj.situacaoProjeto.id IN (:idSituacaoAcao) ");
    		}
    		
    		hql.append("ORDER BY pj.ano desc, atv.id");
    		Query query = getSession().createQuery(hql.toString());
    		query.setInteger("idServidor", servidor.getId());
    		query.setParameterList("idProjetos", DesignacaoFuncaoProjetoHelper.projetosByCoordenadoresOrDesignacaoCoordenador(servidor.getId()));
    		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
    		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
    		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
    
    		if (idSituacaoAcao != null) {
    		    query.setParameterList("idSituacaoAcao", idSituacaoAcao);
    		}
    
    		List<Object> lista = query.list();

    		ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
    		for (int a = 0; a < lista.size(); a++) {

    			int col = 0;
    			Object[] colunas = (Object[]) lista.get(a);

    			AtividadeExtensao a1 = new AtividadeExtensao((Integer) (colunas[0]));
    			if (!result.contains(a1)) {

    				AtividadeExtensao at = new AtividadeExtensao();
    				at.setId((Integer) colunas[col++]);
    				at.setSequencia((Integer) colunas[col++]);
    				at.setAtivo((Boolean) colunas[col++]);
    				at.setTipoAtividadeExtensao(new TipoAtividadeExtensao((Integer) colunas[col++]));
    				at.getTipoAtividadeExtensao().setDescricao((String) colunas[col++]);

    				at.setProjeto(new Projeto((Integer) colunas[col++]));					
    				at.getProjeto().setAtivo((Boolean) colunas[col++]);
    				at.getProjeto().setAno((Integer) colunas[col++]);					
    				at.getProjeto().setTitulo((String) colunas[col++]);
    				at.getProjeto().setDataInicio((Date) colunas[col++]);
    				at.getProjeto().setDataFim((Date) colunas[col++]);

    				result.add(at);
    			}

    			col = 11;
    			if (colunas[col] != null) {
    				RelatorioAcaoExtensao rel = new RelatorioAcaoExtensao();
    				rel.setId((Integer) colunas[col++]);	
    				rel.setTipoRelatorio(new TipoRelatorioExtensao((Integer) colunas[col++]));
    				rel.getTipoRelatorio().setDescricao((String) colunas[col++]);
    				rel.setAtivo((Boolean) colunas[col++]);
    				rel.setDataEnvio((Date) colunas[col++]);
    				
    				if (colunas[16] != null) {
    					rel.setTipoParecerDepartamento(new TipoParecerAvaliacaoExtensao((Integer) colunas[col++]));
    					rel.getTipoParecerDepartamento().setDescricao((String) colunas[col++]);
    					rel.setDataValidacaoDepartamento((Date) colunas[col++]);
    					rel.setParecerDepartamento((String) colunas[col++]);
    				}
    				
    				if (colunas[20] != null) {
    					rel.setTipoParecerProex(new TipoParecerAvaliacaoExtensao((Integer) colunas[col++]));
    					rel.getTipoParecerProex().setDescricao((String) colunas[col++]);
    					rel.setDataValidacaoProex((Date) colunas[col++]);
    					rel.setParecerProex((String) colunas[col++]);
    				}

    				result.get(result.indexOf(a1)).getRelatorios().add(rel);
    			}
    		}
    		return result;
        
	}
	
	
	/**
	 * Retorna todas as Ações de Extensão Ativas 
	 * relacionadas a um determinado projeto base.
	 * Utilizado na gestão de projetos associados.
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> findByProjetoBase(int idProjeto) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append("SELECT a.id, a.projeto.ano, pj.titulo, tip.id, tip.descricao, sit.id, sit.descricao, a.sequencia, " 
					+ " a.bolsasSolicitadas, a.bolsasConcedidas, pj.dataCadastro, a.dataEnvio "
					+ " from AtividadeExtensao a "
					+ " INNER JOIN a.projeto pj "
					+ " INNER JOIN a.situacaoProjeto sit "
					+ " INNER JOIN a.tipoAtividadeExtensao tip  " 
					+ " WHERE a.ativo = trueValue() and pj.ativo = trueValue() and pj.id = :idProjeto" 
					+ " ORDER BY pj.titulo ");

		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idProjeto", idProjeto);
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();

		ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
		for (int cont = 0; cont < lista.size(); cont++) {
		    int col = 0;
		    Object[] colunas = (Object[]) lista.get(cont);

		    AtividadeExtensao at = new AtividadeExtensao();
		    at.setId((Integer) colunas[col++]);
		    at.setAno((Integer) colunas[col++]);
		    at.setTitulo((String) colunas[col++]);
		    
		    TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();		    
		    tipo.setId((Integer) colunas[col++]);
		    tipo.setDescricao((String) colunas[col++]);
		    at.setTipoAtividadeExtensao(tipo);		    
		    
		    TipoSituacaoProjeto sit = new TipoSituacaoProjeto();
		    sit.setId((Integer) colunas[col++]);
		    sit.setDescricao((String) colunas[col++]);		    
		    at.setSituacaoProjeto(sit);
		    
		    at.setSequencia((Integer) colunas[col++]);		    
		    at.setBolsasSolicitadas((Integer) colunas[col++]);
		    at.setBolsasConcedidas((Integer) colunas[col++]);
		    at.setDataCadastro((Date) colunas[col++]);
		    at.setDataEnvio((Date) colunas[col++]);
		    
		    result.add(at);
		}
		return result;
	}
	
	
	
	
	/**
	 * Lista os projetos pendentes de avaliação final (parecer do presidente do comitê de extensão)
	 * 
	 * @param titulo
	 * @param ano
	 * @param idEdital
	 * @param financiamentoInterno
	 * @param financiamentoExterno
	 * @param autoFinanciamento
	 * @param convenioFunpec
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> findAguardandoAvaliacaoFinal(String titulo, Integer ano, Integer idEdital, Boolean financiamentoInterno,
		Boolean financiamentoExterno, Boolean autoFinanciamento, Boolean convenioFunpec, Integer idArea) throws DAOException {

	    try {

		StringBuilder hqlConsulta = new StringBuilder();
		hqlConsulta.append(" SELECT distinct atv.id, p.ensino, p.pesquisa, p.extensao, p.financiamentoInterno, p.financiamentoExterno, "
			+ " p.autoFinanciado, atv.convenioFunpec, "
			+ " atv.sequencia, p.ano, p.titulo, p.unidade.id, p.unidade.sigla, "
			+ " p.unidade.nome, atv.situacaoProjeto.id, atv.situacaoProjeto.descricao, "
			+ " atv.tipoAtividadeExtensao.id, atv.tipoAtividadeExtensao.descricao, "
			+ " atv.areaTematicaPrincipal.id, atv.areaTematicaPrincipal.descricao, "
			+ " ava.id, ava.tipoAvaliacao.id, ava.statusAvaliacao.id, ava.ativo "
			+ " FROM AtividadeExtensao atv " 
			+ "  LEFT JOIN atv.editalExtensao as edital "
			+ "  JOIN atv.avaliacoes as ava "
			+ "  JOIN atv.projeto p ");

		hqlConsulta.append(" WHERE atv.ativo = trueValue() AND (atv.situacaoProjeto.id = :idSituacaoAguardandoAvaliacao)");
		hqlConsulta.append(" AND (ava.statusAvaliacao.id = :idAvaliacaoRealizada)");

		if (titulo != null) {
		    hqlConsulta.append(" AND "
			    + UFRNUtils.toAsciiUpperUTF8("p.titulo") + " like "
			    + UFRNUtils.toAsciiUTF8(":titulo"));
		}

		if ((ano != null) && (ano != 0)) {
		    hqlConsulta.append(" AND p.ano = :ano");
		}


		if ((idEdital != null) && (idEdital != 0)) {
		    hqlConsulta.append(" AND edital.id = :idEdital");
		}

		if (idArea != null) {
		    hqlConsulta.append(" AND atv.areaTematicaPrincipal.id = :idArea");
		}
		
		if (financiamentoInterno != null) {
		    hqlConsulta.append(" AND p.financiamentoInterno = :financiamentoInterno");
		}

		if (financiamentoExterno != null) {
		    hqlConsulta.append(" AND p.financiamentoExterno = :financiamentoExterno");
		}

		if (autoFinanciamento != null) {
		    hqlConsulta.append(" AND p.autoFinanciado = :autoFinanciamento");
		}

		if (convenioFunpec != null) {
		    hqlConsulta.append(" AND atv.convenioFunpec = :convenioFunpec");
		}

		hqlConsulta.append(" ORDER BY p.ano DESC, p.titulo ");

		// Criando consulta
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

		// Populando os valores dos filtros			
		queryConsulta.setInteger("idAvaliacaoRealizada", StatusAvaliacao.AVALIADO);
		queryConsulta.setInteger("idSituacaoAguardandoAvaliacao", TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO);


		if (titulo != null) {
		    queryConsulta.setString("titulo", "%" + titulo.toUpperCase() + "%");
		}

		if (ano != null) {
		    queryConsulta.setInteger("ano", ano);
		}

		if (idEdital != null) {
		    queryConsulta.setInteger("idEdital", idEdital);
		}
		
		if (idArea != null) {
			queryConsulta.setInteger("idArea", idArea);
		}

		if (financiamentoInterno != null) {
		    queryConsulta.setBoolean("financiamentoInterno", financiamentoInterno);
		}

		if (financiamentoExterno != null) {
		    queryConsulta.setBoolean("financiamentoExterno", financiamentoExterno);
		}

		if (autoFinanciamento != null) {
		    queryConsulta.setBoolean("autoFinanciamento", autoFinanciamento);
		}

		if (convenioFunpec != null) {
		    queryConsulta.setBoolean("convenioFunpec", convenioFunpec);
		}

		@SuppressWarnings("unchecked")
		List<Object> lista = queryConsulta.list();

		ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
		for (int a = 0; a < lista.size(); a++) {

		    int col = 0;
		    Object[] colunas = (Object[]) lista.get(a);

		    AtividadeExtensao a1 = new AtividadeExtensao((Integer) (colunas[0]));

		    // colunas repetidas se já tiver o projeto pula todas e vai pra
		    // o que é diferente...
		    if (!result.contains(a1)) {

			AtividadeExtensao at = new AtividadeExtensao();
			at.setId((Integer) colunas[col++]);

			at.getProjeto().setEnsino((Boolean) colunas[col++]);					
			at.getProjeto().setPesquisa((Boolean) colunas[col++]);
			at.getProjeto().setExtensao((Boolean) colunas[col++]);					

			at.setFinanciamentoInterno((Boolean) colunas[col++]);
			at.setFinanciamentoExterno((Boolean) colunas[col++]);
			at.setAutoFinanciado((Boolean) colunas[col++]);
			at.setConvenioFunpec((Boolean) colunas[col++]);

			at.setSequencia((Integer) colunas[col++]);
			at.setAno((Integer) colunas[col++]);
			at.setTitulo((String) colunas[col++]);

			Unidade unidade = new Unidade();
			unidade.setId((Integer) colunas[col++]);
			unidade.setSigla((String) colunas[col++]);
			unidade.setNome((String) colunas[col++]);
			at.setUnidade(unidade);

			TipoSituacaoProjeto situacao = new TipoSituacaoProjeto();
			situacao.setId((Integer) colunas[col++]);
			situacao.setDescricao((String) colunas[col++]);
			at.setSituacaoProjeto(situacao);

			TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();
			tipo.setId((Integer) colunas[col++]);
			tipo.setDescricao((String) colunas[col++]);
			at.setTipoAtividadeExtensao(tipo);

			AreaTematica area = new AreaTematica();
			area.setId((Integer) colunas[col++]);
			area.setDescricao((String) colunas[col++]);
			at.setAreaTematicaPrincipal(area);

			result.add(at);

		    }

		    // avaliação não repete nas linhas retornadas, várias avaliações
		    // para a mesma ação
		    col = 20;
		    if (colunas[col] != null) {
			AvaliacaoAtividade ava = new AvaliacaoAtividade();
			ava.setId((Integer) colunas[col++]);
			TipoAvaliacao ta = new TipoAvaliacao((Integer) colunas[col++]);
			ava.setTipoAvaliacao(ta);
			StatusAvaliacao st = new StatusAvaliacao((Integer) colunas[col++]);
			ava.setStatusAvaliacao(st);
			ava.setAtivo((Boolean) colunas[col++]);

			result.get(result.indexOf(a1)).getAvaliacoes().add(ava);
		    }
		}
		return result;

	    } catch (Exception ex) {
		throw new DAOException(ex.getMessage(), ex);
	    }
	}
	
	
	/**
	 * Retorna o total de ações de extensão coordenadas pela pessoa informada
	 * no edital informado.  
	 * 
	 * @param editalExtensao
	 * @return
	 * @throws DAOException
	 */
	public Long countAtividadesCoordenador(Pessoa pessoa, AtividadeExtensao atividadeExtensao) throws DAOException {
	    try {
		StringBuilder hql = new StringBuilder();
		hql.append(" select count(a.id) FROM AtividadeExtensao a " +
			" JOIN a.projeto projeto " +
			" JOIN projeto.coordenador coordenador " +
			" JOIN projeto.situacaoProjeto sitPro " +
			" WHERE coordenador.servidor.pessoa.id = :idPessoa " +
			" AND a.ativo = trueValue() " +
			" AND coordenador.ativo = trueValue() " +
			" AND a.id <> :idAtividadeExtensao " +
			" AND a.editalExtensao.id = :idEditalExtensao " +
			" AND sitPro.id NOT IN (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +
			" AND projeto.tipoProjeto.id = :idTipoProjeto ");
		Query query = getSession().createQuery(hql.toString());
		
		query.setInteger("idPessoa", pessoa.getId());
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		query.setInteger("idAtividadeExtensao", atividadeExtensao.getId());
		query.setInteger("idEditalExtensao", atividadeExtensao.getEditalExtensao().getId());
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);
		return (Long) query.uniqueResult();
		
	    } catch (Exception e) {
		throw new DAOException(e.getMessage(), e);
	    }
	}

	/***
	 * Retorna todas as ações de extensão com cadastro em andamento a uma certa quantidade de dias.
	 * Normalmente é utilizado para expirar os cadastros antigos.
	 * 
	 * @param dias
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<AtividadeExtensao> findByCadastroEmAndamentoOcioso(int dias) throws HibernateException, DAOException {		
		String projecao = "atv.id, atv.projeto.id, atv.projeto.ano, atv.projeto.titulo, atv.projeto.dataCadastro, atv.projeto.dataInicio, atv.projeto.dataFim, " +
				"atv.projeto.situacaoProjeto.id, atv.projeto.situacaoProjeto.descricao, atv.tipoAtividadeExtensao.id, atv.tipoAtividadeExtensao.descricao, " +
				"atv.situacaoProjeto.id, atv.situacaoProjeto.descricao ";
		StringBuilder hql = new StringBuilder("SELECT ");
		hql.append(projecao);
		hql.append(" FROM AtividadeExtensao as atv WHERE day(current_date - atv.projeto.dataCadastro) > :dias " +
				"and atv.projeto.ativo = trueValue() and atv.ativo = trueValue() and atv.projeto.situacaoProjeto.id = :cadastroEmAndamento ");
		hql.append("order by atv.projeto.ano DESC ");
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("dias", dias);
		query.setInteger("cadastroEmAndamento", TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO);
		
		return HibernateUtils.parseTo(query.list(), projecao, AtividadeExtensao.class, "atv");		
	}
	
	/**
	 * Método que retorna a quantidade de avaliadores do comitê de extensão da atividade passada por parâmetro.
	 * 
	 * @param idAtividade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Long countAvaliadoresComiteExtensaoAtividade(int idAtividade) throws HibernateException, DAOException{
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT count(ava.id) FROM  AtividadeExtensao atv ");
		hql.append("INNER JOIN atv.avaliacoes ava ");
		hql.append("WHERE atv.id = :idAtividade AND ava.ativo = trueValue() AND ava.tipoAvaliacao.id = :tipoComite ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idAtividade", idAtividade);
		q.setInteger("tipoComite", TipoAvaliacao.AVALIACAO_ACAO_COMITE);
		return (Long) q.uniqueResult();
	}

	/**
	 * Método para buscar os projetos de pesquisa que ainda não estão
	 * inseridos no SIGED.
	 */
	@SuppressWarnings("unchecked")
	public List<AtividadeExtensao> buscarAtividadesExtensaoNaoExistentesNoSiged() throws DAOException {
		return getSession().createQuery("select a from AtividadeExtensao a where a.situacaoProjeto.id not in (" + TipoSituacaoProjeto.EXTENSAO_REMOVIDO + ") and a.idArquivo is null and a.securityToken is null")
				.setMaxResults(10).list();
	}
	
	
	/**
	 * Retorna as sub atividades de extensão com uma situação específica
	 * da qual o servidor participa como coordenador.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> findSubAtividadesByCoordenadorAtivo(Servidor servidor, Integer[] idsSituacoesAcao) throws DAOException {
		StringBuilder hql = new StringBuilder();

		hql.append(" select a.id, a.ativo, projeto.ano, projeto.titulo, projeto.ativo, " +
				"projeto.dataInicio, projeto.dataFim, a.registro, " +
				"tip.id, tip.descricao, sitpro.id, sitpro.descricao, sitat.id,  " +
				"sitat.descricao, a.sequencia, coordenador, subAtv.id, subAtv.titulo " +
			" FROM SubAtividadeExtensao subAtv " +
			" JOIN subAtv.atividade a " +
			" JOIN a.projeto projeto " +
			" JOIN projeto.coordenador coordenador" +
			" JOIN projeto.situacaoProjeto sitpro " +
			" JOIN a.situacaoProjeto sitat " +
			" JOIN a.tipoAtividadeExtensao tip  " +
			" WHERE (( coordenador.servidor.id = :idServidor AND coordenador.ativo = trueValue() ) or projeto.id in ( :idProjetos ) )  " +
			" AND subAtv.ativo = trueValue() AND a.ativo = trueValue() AND projeto.ativo = trueValue() "+
			" AND projeto.situacaoProjeto.id NOT IN (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +
			" AND (projeto.tipoProjeto.id = :idTipoProjeto) "
		);

		if (idsSituacoesAcao != null) {
		    hql.append("and projeto.situacaoProjeto.id in (:idsSituacoesAcao) ");
		}

		hql.append("order by projeto.ano desc, a.id");			
		Query query = getSession().createQuery(hql.toString());

		query.setInteger("idServidor", servidor.getId());
		query.setParameterList("idProjetos", DesignacaoFuncaoProjetoHelper.projetosByCoordenadoresOrDesignacaoCoordenador(servidor.getId()));
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

		if (idsSituacoesAcao != null) {
		    query.setParameterList("idsSituacoesAcao", idsSituacoesAcao);
		}
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();

		ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
		for (int a = 0; a < lista.size(); a++) {

			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);

			AtividadeExtensao at = new AtividadeExtensao();
			at.setId((Integer) colunas[col++]);
			at.setAtivo((Boolean) colunas[col++]);
			at.getProjeto().setAno((Integer) colunas[col++]);
			at.getProjeto().setTitulo((String) colunas[col++]);
			at.getProjeto().setAtivo((Boolean) colunas[col++]);
			at.getProjeto().setDataInicio((Date) colunas[col++]);
			at.getProjeto().setDataFim((Date) colunas[col++]);
			at.setRegistro((Boolean) colunas[col++]);

			TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();

			int idTipo = (Integer) colunas[col++];
			String descricao = (String) colunas[col++];
			if (descricao != null) {
				tipo.setId(idTipo);
				tipo.setDescricao(descricao);
			}
			at.setTipoAtividadeExtensao(tipo);

			TipoSituacaoProjeto sitpro = new TipoSituacaoProjeto();
			sitpro.setId((Integer) colunas[col++]);
			sitpro.setDescricao((String) colunas[col++]);
			
			at.getProjeto().setSituacaoProjeto(sitpro);
			TipoSituacaoProjeto sitat = new TipoSituacaoProjeto();
			sitat.setId((Integer) colunas[col++]);
			sitat.setDescricao((String) colunas[col++]);
			at.setSituacaoProjeto(sitat);

			at.setSequencia((Integer) colunas[col++]);
			at.getProjeto().setCoordenador((MembroProjeto) colunas[col++]);
			at.setSubAtividade(new SubAtividadeExtensao());
			at.getSubAtividade().setId((Integer) colunas[col++]);
			at.getSubAtividade().setTitulo((String) colunas[col++]);

			result.add(at);
		}
		return result;
	}
	
	/**
	 * Retorna as atividades de extensão com uma situação específica
	 * da qual o servidor participa como gerenciador de participantes.
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Collection<AtividadeExtensao> findByGerenciadorAtivo(Servidor servidor, Integer[] idsSituacoesAcao) throws DAOException {
		StringBuilder hql = new StringBuilder();

		hql.append(" select distinct a.id, a.ativo, projeto.ano, projeto.titulo, projeto.ativo, " +
				"projeto.dataInicio, projeto.dataFim, a.registro, " +
				"tip.id, tip.descricao, sitpro.id, sitpro.descricao, sitat.id, " +
				"sitat.descricao, a.sequencia " +
			" FROM AtividadeExtensao a " +
			" JOIN a.projeto projeto " +
			" JOIN projeto.coordenador coordenador" +
			" JOIN projeto.situacaoProjeto sitpro " +
			" JOIN projeto.equipe mp " +
			" JOIN a.situacaoProjeto sitat " +
			" JOIN a.tipoAtividadeExtensao tip  " +
			" WHERE (( coordenador.servidor.id = :idServidor AND coordenador.ativo = trueValue() ) or projeto.id in ( :idProjetos ) )  " +
			" AND ( mp.gerenciaParticipantes = trueValue() AND mp.pessoa = :idPessoa ) " +
			" AND projeto.situacaoProjeto.id NOT IN (:EXTENSAO_GRUPO_INVALIDO, :PROJETO_BASE_GRUPO_INVALIDO) " +
			" AND (projeto.tipoProjeto.id = :idTipoProjeto) "
		);

		if (idsSituacoesAcao != null) {
		    hql.append("and projeto.situacaoProjeto.id in (:idsSituacoesAcao) ");
		}

		hql.append("order by projeto.ano desc, a.id");			
		Query query = getSession().createQuery(hql.toString());

		query.setInteger("idServidor", servidor.getId());
		query.setParameterList("idProjetos", DesignacaoFuncaoProjetoHelper.projetosByCoordenadoresOrDesignacaoCoordenador(servidor.getId()));
		query.setInteger("idPessoa", servidor.getPessoa().getId());
		query.setInteger("idTipoProjeto", TipoProjeto.EXTENSAO);
		query.setParameterList("EXTENSAO_GRUPO_INVALIDO", TipoSituacaoProjeto.EXTENSAO_GRUPO_INVALIDO);
		query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);

		if (idsSituacoesAcao != null) {
		    query.setParameterList("idsSituacoesAcao", idsSituacoesAcao);
		}
		
		@SuppressWarnings("unchecked")
		List<Object> lista = query.list();

		ArrayList<AtividadeExtensao> result = new ArrayList<AtividadeExtensao>();
		for (int a = 0; a < lista.size(); a++) {

			int col = 0;
			Object[] colunas = (Object[]) lista.get(a);

			AtividadeExtensao at = new AtividadeExtensao();
			at.setId((Integer) colunas[col++]);
			at.setAtivo((Boolean) colunas[col++]);
			at.getProjeto().setAno((Integer) colunas[col++]);
			at.getProjeto().setTitulo((String) colunas[col++]);
			at.getProjeto().setAtivo((Boolean) colunas[col++]);
			at.getProjeto().setDataInicio((Date) colunas[col++]);
			at.getProjeto().setDataFim((Date) colunas[col++]);
			at.setRegistro((Boolean) colunas[col++]);

			TipoAtividadeExtensao tipo = new TipoAtividadeExtensao();

			int idTipo = (Integer) colunas[col++];
			String descricao = (String) colunas[col++];
			if (descricao != null) {
				tipo.setId(idTipo);
				tipo.setDescricao(descricao);
			}
			at.setTipoAtividadeExtensao(tipo);

			TipoSituacaoProjeto sitpro = new TipoSituacaoProjeto();
			sitpro.setId((Integer) colunas[col++]);
			sitpro.setDescricao((String) colunas[col++]);
			
			at.getProjeto().setSituacaoProjeto(sitpro);
			TipoSituacaoProjeto sitat = new TipoSituacaoProjeto();
			sitat.setId((Integer) colunas[col++]);
			sitat.setDescricao((String) colunas[col++]);
			at.setSituacaoProjeto(sitat);

			at.setSequencia((Integer) colunas[col++]);
			

			result.add(at);
		}
		return result;
		
	}
	
}

