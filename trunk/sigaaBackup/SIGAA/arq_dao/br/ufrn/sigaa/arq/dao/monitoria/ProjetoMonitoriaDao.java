/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/09/2009
 *
 */
package br.ufrn.sigaa.arq.dao.monitoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.ParametroBuscaAgregadorBolsas;
import br.ufrn.sigaa.ensino.graduacao.jsf.AgregadorBolsasMBean.RestricoesBuscaAgregadorBolsas;
import br.ufrn.sigaa.monitoria.dominio.AtividadeMonitor;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.ProvaSelecao;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.SolicitacaoRenovacao;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoSituacaoProvaSelecao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Dao para as consultas sobre Projetos de Monitoria.
 * 
 * @author Victor Hugo
 * @author Ilueny
 * 
 */
public class ProjetoMonitoriaDao extends GenericSigaaDAO {

	/** Limite de resultados da consulta. */
	private static final long LIMITE_RESULTADOS = 1000;

	/**
	 * Retorna o total de projetos de ensino de acordo com o edital.
	 * 
	 * @param status
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 */
	public long getTotalByStatus(int status, int idEdital) throws DAOException {
		String sql = "select count(id) from ProjetoEnsino pm where pm.editalMonitoria.id = :edital";
		
		if (status != -1) {
			sql += " and  pm.projeto.situacaoProjeto.id = :status  ";
		}

		Query q = getSession().createQuery(sql);
		if (status != -1) {
			q.setInteger("status", status);
		}
		q.setInteger("edital", idEdital);
		return (Long) q.uniqueResult();
	}

	/**
	 * Retorna os projetos de monitoria abertos para edição, ou seja,
	 * com situação CADASTRO_EM_ANDAMENTO, gravados pelo usuário atual.
	 * 
	 * @param situacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProjetoEnsino> findGravadosByUsuario(Usuario usuario, Integer tipoProjetoEnsino)
			throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();
			hql
					.append("SELECT pe.id, pj.ano, pj.titulo, sit.id, sit.descricao, tip.id, tip.descricao "
							+ "from ProjetoEnsino pe "
							+ "INNER JOIN pe.projeto pj "
							+ "INNER JOIN pj.situacaoProjeto sit "
							+ "INNER JOIN pj.registroEntrada reg  "
							+ "INNER JOIN reg.usuario  "
							+ "INNER JOIN pe.tipoProjetoEnsino tip  "
							+ "WHERE reg.usuario.id = :idUsuario AND "
							+ " sit.id = :idSituacao and pj.ativo = trueValue() " +
									" AND pj.tipoProjeto.id = :idTipoProjeto ");
			
			
			if(tipoProjetoEnsino != null) {
				hql.append( " and pe.tipoProjetoEnsino.id = :tipoProjetoEnsino " );
			}

			Query query = getSession().createQuery(hql.toString());
			
			query.setInteger("idUsuario", usuario.getId());			
			query.setInteger("idTipoProjeto", TipoProjeto.ENSINO);
			query.setInteger("idSituacao",TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO);			
			
			if(tipoProjetoEnsino != null) {
				query.setInteger("tipoProjetoEnsino", tipoProjetoEnsino);
			}

			@SuppressWarnings("unchecked")
			List<Object> lista = query.list();

			ArrayList<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);

				ProjetoEnsino pe = new ProjetoEnsino();
				pe.setId((Integer) colunas[col++]);
				pe.setAno((Integer) colunas[col++]);
				pe.setTitulo((String) colunas[col++]);

				TipoSituacaoProjeto sit = new TipoSituacaoProjeto(
						(Integer) colunas[col++]);
				String desc = (String) colunas[col++];
				if (desc != null)
					sit.setDescricao(desc);

				pe.setSituacaoProjeto(sit);

				TipoProjetoEnsino tip = new TipoProjetoEnsino(
						(Integer) colunas[col++]);
				String des = (String) colunas[col++];
				if (des != null)
					tip.setDescricao(des);

				pe.setTipoProjetoEnsino(tip);

				result.add(pe);

			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	/**
	 * Busca os projeto nos quais um docente está com vínculo. Se
	 * anoProjetoInicio e anoProjetoFim forem informados são retornados somente
	 * os projetos desse período
	 * 
	 * Utilizado na exibição de meus projetos
	 * 
	 * @param docente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findMeusProjetos(
			int idServidor, Boolean coordenador, Integer anoProjetoInicio, Integer anoProjetoFim, Integer tipoProjetoEnsino) throws DAOException {

		Collection<ProjetoEnsino> projetos = new ArrayList<ProjetoEnsino>();

		try {
			String projecao = " pm.id, pm.ativo, pm.tipoProjetoEnsino.id, pm.projeto.ativo, pm.projeto.id, pm.projeto.dataInicio, " +
			"pm.projeto.ano, pm.projeto.dataFim, pm.projeto.titulo, pm.situacaoProjeto.id, pm.situacaoProjeto.descricao ";
			
			StringBuffer hql = new StringBuffer(" select ");
			hql.append(projecao);
			hql.append(" from EquipeDocente ed join ed.projetoEnsino pm ");
			hql.append(" where 1 = 1 ");
			hql.append(" and ed.servidor.id = :idServidor and pm.projeto.ativo = trueValue() and ed.ativo = trueValue() ");		 
			hql.append(" AND pm.projeto.tipoProjeto.id = :idTipoProjeto ");

			if (tipoProjetoEnsino!=null){
				hql.append(" and pm.tipoProjetoEnsino.id = :tipoProjeto ");
			}
			
			if ((coordenador != null) ) {
				hql.append(" and (ed.coordenador = :coordenador)");
			}

			if (anoProjetoInicio != null && anoProjetoFim != null) {
				hql.append(" and (pm.projeto.ano >= :anoProjetoInicio and pm.projeto.ano <= :anoProjetoFim)");
			}

			hql.append(" order by pm.projeto.ano desc, pm.id ");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idServidor", idServidor);
			q.setInteger("idTipoProjeto", TipoProjeto.ENSINO);
			
			if (tipoProjetoEnsino!=null){
				q.setInteger("tipoProjeto", tipoProjetoEnsino);
			}
			if (coordenador != null) {			    
				q.setBoolean("coordenador", coordenador);
			}

			if (anoProjetoInicio != null && anoProjetoFim != null) {
				q.setInteger("anoProjetoInicio", anoProjetoInicio);
				q.setInteger("anoProjetoFim", anoProjetoFim);
			}

			projetos = HibernateUtils.parseTo(q.list(), projecao, ProjetoEnsino.class, "pm");

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

		return projetos;
	}

	/**
	 * Busca os projeto nos quais um docente está com vínculo. Se
	 * anoProjetoInicio e anoProjetoFim forem informados são retornados somente
	 * os projetos desse período
	 * 
	 * Utilizado na exibição de meus projetos
	 * 
	 * @param docente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findMeusProjetosMonitoria(int idServidor, Integer anoProjetoInicio, 
			Integer anoProjetoFim, int idTipoRelatorio) throws DAOException {
			
			String sql = "select p.ano, p.titulo" +
					" from monitoria.equipe_docente ed" +
					" join monitoria.projeto_monitoria pm using ( id_projeto_monitoria )" +
					" join projetos.projeto p using (id_projeto)" +
					" where id_servidor = " + idServidor +
					" and ed.ativo = trueValue()" +
					" and ed.excluido = falseValue()" +
					" and p.ativo = trueValue()" +
					" and p.ano >= " + anoProjetoInicio +
					" and p.ano <= " + anoProjetoFim +
					" and ed.coordenador = trueValue()" +
					" and p.id_tipo_situacao_projeto in " + UFRNUtils.gerarStringIn(TipoSituacaoProjeto.MON_GRUPO_ATIVO) +
					" and p.id_tipo_projeto = " + TipoProjeto.ENSINO +
					
					" except" +
					
					" select p.ano, p.titulo" +
					" from monitoria.relatorio_projeto rp" +
					" join monitoria.equipe_docente ed on ( rp.id_projeto_monitoria = ed.id_projeto_monitoria )" +
					" join monitoria.projeto_monitoria pm on ( pm.id_projeto_monitoria = ed.id_projeto_monitoria)" +
					" join projetos.projeto p using (id_projeto)" +
					" where id_tipo_relatorio_monitoria = " + idTipoRelatorio +
					" and id_status_relatorio = " + StatusAvaliacao.AVALIADO +
					" and ed.id_servidor = " + idServidor +
					" and p.ano >= " + anoProjetoInicio +
					" and p.ano <= " + anoProjetoFim +
					" and ed.coordenador = trueValue() " +
					" and p.id_tipo_situacao_projeto in " + UFRNUtils.gerarStringIn(TipoSituacaoProjeto.MON_GRUPO_ATIVO) +
					" and p.id_tipo_projeto = " + TipoProjeto.ENSINO +
					" order by ano, titulo";

			List<ProjetoEnsino> lista = getJdbcTemplate().query(sql.toString(), new Object[] {}, new RowMapper() {
				public Object mapRow(ResultSet rs, int row) throws SQLException {
					ProjetoEnsino projMonitoria = new ProjetoEnsino();
					Projeto p = new Projeto();
				    p.setAno(rs.getInt("ano"));
				    p.setTitulo(rs.getString("titulo"));
				    projMonitoria.setProjeto(p);
					return projMonitoria;
				}
			});
			
			return lista;
	}

	
	/**
	 * Lista todos os projetos de melhoria da qualidade do ensino de graduação
	 * onde o docente atual faz parte da equipe.
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findMeusProjetosPAMQEG(int idServidor)	throws DAOException {

	    Collection<ProjetoEnsino> projetos = new ArrayList<ProjetoEnsino>();

	    try {
		StringBuffer hql = new StringBuffer(
			" select pe " +
			" from  ProjetoEnsino pe " +
			" inner join pe.projeto p "+
			" inner join p.coordenador coord ");

		hql.append(" where pe.tipoProjetoEnsino.id = :tipoProjeto ");
		hql.append(" and coord.servidor.id = :idServidor and p.ativo = trueValue() and coord.ativo = trueValue() ");
		hql.append(" AND p.tipoProjeto.id = :idTipoProjeto ");
		hql.append(" order by p.ano desc, pe.id");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idServidor", idServidor);
		q.setInteger("idTipoProjeto", TipoProjeto.ENSINO);
		q.setInteger("tipoProjeto", TipoProjetoEnsino.PROJETO_PAMQEG);

		projetos = q.list();

	    } catch (Exception e) {
		throw new DAOException(e.getMessage(), e);
	    }

	    return projetos;
	}

	
	
	/**
	 * Busca os projeto nos quais um docente está com vínculo ativo.
	 * 
	 * Utilizado na exibição de meus projetos e na validação dos
	 * docentes que enviaram resumos SID
	 * 
	 * @param docente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findValidosByServidor(int idServidor, Boolean coordenador) throws DAOException {

		Collection<ProjetoEnsino> projetos = new ArrayList<ProjetoEnsino>();

		try {
			StringBuffer hql = new StringBuffer("select pm " +
					"from ProjetoEnsino pm join pm.projeto pj join pm.equipeDocentes ed ");
			
			hql.append(" where ed.servidor.id = :idServidor and ed.dataEntradaProjeto is not null ");
			hql.append(" and pj.ativo = trueValue() and pm.ativo = trueValue() ");
			
			//Apenas projetos isolados são considerados.
			hql.append(" and pj.tipoProjeto.id = :idTipoProjeto ");

			if (coordenador != null) {
			    hql.append(" and ed.coordenador = :Coordenador");
			}

		    hql.append(" and pj.situacaoProjeto in " + 
		    		UFRNUtils.gerarStringIn(TipoSituacaoProjeto.MON_GRUPO_ATIVO));
		    
			hql.append(" order by pj.ano desc, pm.id");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idServidor", idServidor);
			q.setInteger("idTipoProjeto", TipoProjeto.ENSINO);
			
			if (coordenador != null) {
				q.setBoolean("Coordenador", coordenador);
			}
			
			projetos = q.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

		return projetos;
	}

	/**
	 * Retorna todos os projetos do servidor com a situação não recomendado
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public ProjetoEnsino findNaoRecomendadosByServidor(int idServidor)
			throws DAOException {
		try {

			Criteria c = getCriteria(ProjetoEnsino.class);
			c.createCriteria("projeto")
				.createCriteria("situacaoProjeto")
				.add(Expression.eq("id", TipoSituacaoProjeto.MON_NAO_RECOMENDADO));

			Criteria subC = c.createCriteria("equipeDocente");
			subC.add(Expression.eq("servidor.id", idServidor));

			return (ProjetoEnsino) subC.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os projetos em andamento do servidor sem resumo SID
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findSemResumoSidByServidor(int idServidor)
			throws DAOException {
		try {

			Criteria c = getCriteria(ProjetoEnsino.class);
			c.createCriteria("projeto")
				.createCriteria("situacaoProjeto")
				.add(Expression.eq("id", TipoSituacaoProjeto.MON_EM_EXECUCAO));
			c.add(Expression.isEmpty("resumosSid"));

			Criteria subC = c.createCriteria("equipeDocentes");
			subC.add(Expression.eq("servidor.id", idServidor));
			subC.add(Expression.eq("coordenador", true));

			return subC.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os projetos com uma determinada situação (igual ou
	 * diferente) onde o servidor informado participa como coordenador.
	 * 
	 * 
	 * @param situacaoProjeto
	 * @param projetosComSituacao
	 *            se TRUE retorna projetos com a situação informada, se FALSE
	 *            retorna projetos diferentes da situação
	 * @param servidor
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findBySituacaoProjetoServidor(
			int idSituacaoProjeto, boolean projetosComSituacao, int idServidor)
			throws DAOException {

		if (idServidor > 0) {

			Criteria c = getCriteria(ProjetoEnsino.class);
			Criteria subC = c.createCriteria("equipeDocentes");

			subC.createCriteria("servidor")
					.add(Expression.eq("id", idServidor));
			subC.add(Expression.eq("coordenador", true));

			if (idSituacaoProjeto > 0) {

				if (projetosComSituacao) {
					c.createCriteria("projeto").createCriteria("situacaoProjeto").add(
							Expression.eq("id", idSituacaoProjeto));
				} else {
					c.createCriteria("projeto").createCriteria("situacaoProjeto").add(
							Expression.ne("id", idSituacaoProjeto));
				}

			}

			return c.list();

		} else {
			return null;
		}

	}

	/**
	 * Retorna a quantidade de projetos ativos do Docente a partir do ano informados.
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public int countOutrosProjetosAtivos(EquipeDocente eqpDocente) throws DAOException {	    
	    if (eqpDocente != null) {		
		StringBuilder hql = new StringBuilder();
		hql.append(" select count(*) from ProjetoEnsino pe join pe.projeto pj join pe.equipeDocentes as e");
		hql.append(" where pj.ativo = trueValue() AND pe.ativo = trueValue() AND e.ativo = trueValue() " +
				" AND pe.id <> :idProjetoAtual " + //não considera o projeto atual
				" AND pj.ano >= :ano " +
				" AND (e.dataSaidaProjeto is null OR e.dataSaidaProjeto > :hoje) " +
				" AND pj.tipoProjeto.id = :idTipoProjeto " +
				" AND e.servidor.id = :idServidor " +
				" AND pj.situacaoProjeto.id not in " + UFRNUtils.gerarStringIn(new int[] {TipoSituacaoProjeto.MON_CONCLUIDO, 
							TipoSituacaoProjeto.MON_CANCELADO, TipoSituacaoProjeto.MON_REMOVIDO}) );

		Query query = getSession().createQuery(hql.toString());
		
		int idServidor = eqpDocente.getServidor().getId();
		int ano = eqpDocente.getProjetoEnsino().getAno() - 1;  //verifica projetos a partir de um ano anterior ao ano do projeto atual
		Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		int idProjetoAtual =  eqpDocente.getProjetoEnsino().getId();
		
		query.setInteger("idServidor", idServidor);
		query.setInteger("idTipoProjeto", TipoProjeto.ENSINO);
		query.setInteger("ano", ano);
		query.setDate("hoje", hoje);
		query.setInteger("idProjetoAtual", idProjetoAtual);

		return Integer.parseInt( query.uniqueResult().toString() );
	    }else {
		throw new DAOException("");
	    }

	}

	/**
	 * Retorna todas as provas nas quais o discente se inscreveu no processo de
	 * seleção
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProvaSelecao> findByInscricaoDiscente(int idDiscente)
			throws DAOException {

		String hql = null;

		hql = " SELECT prova FROM ProvaSelecao prova " 
				+ " INNER JOIN prova.discentesInscritos inscricao "
				+ " WHERE inscricao.discente.id = :idDiscente and prova.ativo = trueValue() and inscricao.ativo = trueValue() ";

		Query query = getSession().createQuery(hql);
		query.setInteger("idDiscente", idDiscente);

		return query.list();

	}

	/**
	 * Retorna lista de projetos de monitoria com provas seletivas
	 * cadastradas de um determinado centro ou de todos os centros
	 * 
	 * @return lista de projetos de monitoria
	 * @throws DAOException
	 * 
	 * @author Ilueny Santos
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByCadastroSelecao(Integer idUnidade, Integer ano, String titulo) throws DAOException {

		String projecao = " pm.id, pm.projeto.id, pm.projeto.titulo, pm.projeto.ano, " +
				" pm.projeto.coordenador.id, pm.projeto.coordenador.servidor.id, pm.projeto.coordenador.servidor.pessoa.id, " +
				" pm.projeto.coordenador.servidor.pessoa.nome, pm.projeto.unidade.id,  pm.projeto.unidade.sigla, pm.projeto.unidade.nome ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT ");
		hql.append(projecao);
		hql.append(" FROM ProjetoEnsino pm WHERE 1 = 1 "); 
		
		if (idUnidade != null) {
			hql.append(" AND pm.projeto.unidade.id = :idUnidade");
		}

		if (ano != null) {
			hql.append(" AND pm.projeto.ano = :ano");
		}

		if (titulo != null) {
			hql.append(" AND " + UFRNUtils.toAsciiUpperUTF8("pm.projeto.titulo") 
					+ " like " + UFRNUtils.toAsciiUpperUTF8(":titulo"));
		}
		hql.append(" ORDER BY pm.projeto.unidade.id, pm.projeto.ano");
		
		Query query = getSession().createQuery(hql.toString());
		if (idUnidade != null) {
			query.setInteger("idUnidade", idUnidade);
		}

		if (ano != null) {
			query.setInteger("ano", ano);
		}

		if (titulo != null) {
			query.setString("titulo", "%" + titulo.toUpperCase() + "%");
		}

		return HibernateUtils.parseTo(query.list(), projecao, ProjetoEnsino.class, "pm");
	}

	/**
	 * Retorna todos os projetos com componentes curriculares da unidade
	 * informada
	 * 
	 * @return Coleção de ProjetoEnsino do com componentes da unidade informada
	 * @throws DAOException
	 * 
	 * @author ilueny santos
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByUnidadeDoComponente(Unidade unidade)
			throws DAOException {

		Criteria c = getSession().createCriteria(ProjetoEnsino.class);
		Criteria subC = c.createCriteria("componentesCurriculares");
		subC.add(Expression.eq("disciplina.unidade.id", unidade.getId()));

		return subC.list();
	}

	/**
	 * Retorna todos os projetos com autorização do chefe da unidade de qualquer
	 * componente curricular pendente.
	 * 
	 * @return Colecao de ProjetoEnsino com dataAutorizacao pendente
	 * @throws DAOException
	 * 
	 * @author ilueny santos
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByAutorizacaoPendente()
			throws DAOException {

		Criteria c = getSession().createCriteria(ProjetoEnsino.class);
		Criteria subC = c.createCriteria("autorizacoesProjeto");
		subC.add(Expression.isNull("dataAutorizacao"));

		return subC.list();
	}

	/**
	 * Retorna todos os projetos pendentes de autorização de reconsideração por
	 * membros da PROGRAD
	 * 
	 * @return Colecao de ProjetoEnsino com dataAutorizacao pendente
	 * @throws DAOException
	 * 
	 * @author ilueny santos
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByAutorizacaoReconsideracaoPendente()
			throws DAOException {

		Criteria c = getSession().createCriteria(ProjetoEnsino.class);
		Criteria subC = c.createCriteria("autorizacoesReconsideracao");
		subC.add(Expression.isNull("dataAutorizacao"));

		return subC.list();
	}

	/**
	 * Retorna todos os projetos da unidade informada
	 * 
	 * @return Coleção de ProjetoEnsino do centro informado
	 * @throws DAOException
	 * 
	 * @author ilueny santos
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByUnidade(Unidade unidade)
			throws DAOException {

		Criteria c = getSession().createCriteria(ProjetoEnsino.class);
		c.add(Expression.eq("projeto.unidade.id", unidade.getId()));

		return c.list();
	}

	/**
	 * Retorna todos os projetos de monitoria onde o avaliador atuou ou atua
	 * 
	 * @param membro
	 *            membro da comissão
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByAvaliador(MembroComissao membro)
			throws DAOException {
		Criteria c = getSession().createCriteria(ProjetoEnsino.class);
		Criteria subC = c.createCriteria("avaliacoes");
		subC.add(Expression.eq("avaliador", membro));

		return subC.list();
	}

	/**
	 * Retorna projetos com o edital informado
	 * 
	 * @param edital
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ProjetoEnsino> findByEdital(EditalMonitoria edital)
			throws DAOException {
		Criteria c = getSession().createCriteria(ProjetoEnsino.class);
		c.add(Expression.eq("edital", edital));

		return c.list();
	}

	/**
	 * Projeto com uma determinada situação e que foi submetido pra o edital
	 * informado.
	 * 
	 * @param edital
	 * @param idTipoSituacaoProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ProjetoEnsino> findBySituacaoEdital(EditalMonitoria edital,	int idTipoSituacaoProjeto) throws DAOException {
	    Criteria c = getSession().createCriteria(ProjetoEnsino.class);
	    if (edital != null) {
		c.add(Expression.eq("editalMonitoria.id", edital.getId()));
	    }
	    c.createCriteria("projeto").add(Expression.eq("situacaoProjeto.id", idTipoSituacaoProjeto));
	    return c.list();
	}

	/**
	 * Utilizado no cálculo da nota final do projeto para classificação geral
	 * Contabiliza número de professores participantes do projeto
	 */
	public int findNumProfessoresByProjeto(ProjetoEnsino projeto)
			throws DAOException {
		Query q = getSession()
				.createQuery(
						"select count(distinct equipeDocente.servidor.id) from ProjetoEnsino pm "
								+ "left join pm.equipeDocentes equipeDocente where pm.id = :idProjeto");
		q.setInteger("idProjeto", projeto.getId());
		return ((Long) q.uniqueResult()).intValue();
	}

	/**
	 * Utilizado no cálculo da nota final do projeto para classificação geral
	 * Contabiliza número de departamentos do projeto
	 */
	public int findNumDepartamentosByProjeto(ProjetoEnsino projeto)
			throws DAOException {
		Query q = getSession()
				.createQuery(
						"select count(distinct comp.disciplina.unidade.id) from ProjetoEnsino pm "
								+ "left join pm.componentesCurriculares comp where pm.id = :idProjeto");
		q.setInteger("idProjeto", projeto.getId());
		return ((Long) q.uniqueResult()).intValue();
	}

	/**
	 * Utilizado no cálculo da nota final do projeto para classificação geral
	 * Contabiliza de componentes curriculares do projeto
	 */
	public int findNumComponentesCurriculares(ProjetoEnsino projeto)
			throws DAOException {
		Query q = getSession()
				.createQuery(
						"select count(distinct comp.id) from ProjetoEnsino pm "
								+ "left join pm.componentesCurriculares comp where pm.id = :idProjeto");
		q.setInteger("idProjeto", projeto.getId());
		return ((Long) q.uniqueResult()).intValue();
	}

	/**
	 * Utilizado no cálculo da nota final do projeto para classificação geral
	 * Contabiliza média das avaliações feitas pela comissão de monitoria
	 */
	public double findMediaAnaliseByProjeto(ProjetoEnsino projeto)
			throws DAOException {
		Query q = getSession()
				.createQuery(
						"select sum(aval.notaAvaliacao)/count(aval) from ProjetoEnsino pm "
								+ "left join pm.avaliacoes aval where pm.id = :idProjeto " +
										"and aval.statusAvaliacao.id = :idAvaliado " +
										"and aval.tipoAvaliacao.id = :idTipoProjeto");
		q.setInteger("idProjeto", projeto.getId());
		q.setInteger("idAvaliado", StatusAvaliacao.AVALIADO);
		q.setInteger("idTipoProjeto", TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO);
		Double result = (Double) q.uniqueResult();
		if (result == null)
			return 0.0;
		return result;
	}
	/**
	 * Faz a busca por um RT pelo projetos de monitoria.
	 * O RT é um campo ligado a um edital usado para fazer cálculos.   
	 * 
	 * @param projeto
	 * @return
	 * @throws DAOException
	 */
	public double findRtByProjeto(ProjetoEnsino projeto) throws DAOException {

		Query q = getSession()
				.createQuery(
						"select count(distinct t.id), count(md.id) "
								+ "from ProjetoEnsino pm, ComponenteCurricularMonitoria ccm, "
								+ "Turma t, MatriculaComponente md "
								+ "where pm.id = ccm.projetoEnsino.id "
								+ "and ccm.disciplina.id = t.disciplina.id and md.turma.id = t.id "
								+ "and pm.id = :idProjeto "
								+ "and ((t.ano = :ano1 and t.periodo = :periodo1) or (t.ano = :ano2 and t.periodo = :periodo2))");

		int anoLetivoAtual = projeto.getEditalMonitoria().getAno();
		int periodoLetivoAtual = projeto.getEditalMonitoria().getSemestre();

		int anoLetivoAnterior = anoLetivoAtual;
		int periodoLetivoAnterior = 1;

		if (periodoLetivoAtual == 1) {
			periodoLetivoAnterior = 2;
			anoLetivoAnterior = anoLetivoAtual - 1;
		}

		q.setInteger("idProjeto", projeto.getId());

		q.setInteger("ano1", anoLetivoAtual);
		q.setInteger("periodo1", periodoLetivoAtual);

		q.setInteger("ano2", anoLetivoAnterior);
		q.setInteger("periodo2", periodoLetivoAnterior);

		Object[] result = (Object[]) q.uniqueResult();
		if (result == null)
			return 0.0;

		double numAlunos = ((Long) result[1]).doubleValue();
		double numTurmas = ((Long) result[0]).doubleValue();

		if (numTurmas == 0.0)
			return 0.0;

		return (numAlunos / numTurmas) / 15.0;
	}
	/**
	 * Busca projetos para renovação pelo usuário ou ano.
	 * 
	 * @param usr
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	
	@SuppressWarnings("unchecked")
	public List<ProjetoEnsino> findProjetosRenovacao(Usuario usr, int ano)
			throws DAOException {
		StringBuffer hql = new StringBuffer(
				"from ProjetoEnsino pm where pm.projeto.ano = :ano ");

		if (usr != null) {
			hql.append(" and pm.equipeDocentes.servidor.id = :idServidor and "
					+ "pm.equipeDocentes.coordenador = trueValue()");
		}

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ano", ano - 1);
		if (usr != null)
			q.setInteger("idServidor", usr.getIdServidor());
		List<Object> lista = q.list();
		List<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();

		for (Iterator<Object> it = lista.iterator(); it.hasNext();) {
			ProjetoEnsino p = (ProjetoEnsino) it.next();
			Criteria c = getCriteria(SolicitacaoRenovacao.class);
			c.add(Expression.eq("projetoEnsino.id", p.getId()));
			List<Object> renovacoes = c.list();
			if (renovacoes == null || renovacoes.isEmpty())
				result.add(p);
		}

		return result;
	}

	/**
	 * Retorna todas as avaliações feitas pelo servidor informado
	 * 
	 * @param idServidor
	 * @param tipoAvaliacao
	 * @param statusAvaliacao
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AvaliacaoMonitoria> findAvaliacoesProjetoByUsuario(
			int idServidor, int tipoAvaliacao, Integer[] statusAvaliacao)
			throws DAOException {

		try {
			String hql = " select avaliacao.id, avaliacao.notaAvaliacao, avaliacao.dataAvaliacao, " +
					     "       projeto.id, projeto.ano, projeto.titulo, projetoEnsino.id, projetoEnsino.tipoProjetoEnsino " +
					     " from AvaliacaoMonitoria avaliacao " +
					     " inner join avaliacao.projetoEnsino projetoEnsino " +
					     " inner join projetoEnsino.projeto projeto " +
					     " where (avaliacao.avaliador.servidor.id = :idServidor) ";

			if (statusAvaliacao != null)
				hql += "and (avaliacao.statusAvaliacao.id in (:status)) ";

			if (tipoAvaliacao > 0)
				hql += "and (avaliacao.tipoAvaliacao = :tipoAvaliacao)";
			
			hql += "order by projeto.ano desc, avaliacao.statusAvaliacao.id";

			Query q = getSession().createQuery(hql);
			q.setInteger("idServidor", idServidor);

			if (statusAvaliacao != null)
				q.setParameterList("status", statusAvaliacao);

			if (tipoAvaliacao > 0)
				q.setInteger("tipoAvaliacao", tipoAvaliacao);

			List<Object> lista = q.list();

			ArrayList<AvaliacaoMonitoria> result = new ArrayList<AvaliacaoMonitoria>();
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				AvaliacaoMonitoria av = new AvaliacaoMonitoria();
				av.setId((Integer) colunas[col++]);
				av.setNotaAvaliacao((Double) colunas[col++]);
				av.setDataAvaliacao((Date) colunas[col++]);
				av.getProjetoEnsino().getProjeto().setId((Integer) colunas[col++]);
				av.getProjetoEnsino().setAno((Integer) colunas[col++]);
				av.getProjetoEnsino().setTitulo((String) colunas[col++]);
				av.getProjetoEnsino().setId((Integer) colunas[col++]);
				av.getProjetoEnsino().setTipoProjetoEnsino((TipoProjetoEnsino) colunas[col++]);

				result.add(av);
			}
			return result;	
			
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna a avaliação de um projeto feita por um usuário. 
	 * 
	 * @param projeto
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public AvaliacaoMonitoria findAvaliacaoByProjetoUsuario(
			ProjetoEnsino projeto, Usuario usuario) throws DAOException {
		try {
			Criteria c = getCriteria(AvaliacaoMonitoria.class);
			Criteria cc = c.createCriteria("avaliador");
			cc.add(Expression.eq("usuario.id", usuario.getId()));
			c.add(Expression.eq("projetoEnsino.id", projeto.getId()));

			return (AvaliacaoMonitoria) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todas as orientações do servidor informado
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Orientacao> findOrientacoesByServidor(int idServidor,
			boolean ativo) throws DAOException {
		try {
			String hql = "from Orientacao o where o.equipeDocente.servidor.id = :idServidor and o.equipeDocente.ativo = :ativo";
			Query q = getSession().createQuery(hql);
			q.setInteger("idServidor", idServidor);
			q.setBoolean("ativo", ativo);

			return q.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Todas as orientações do discente informado.
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Orientacao> findOrientacoesByDiscente(int idDiscente,
			boolean ativo) throws DAOException {
		try {

			String hql = "from Orientacao o where o.discenteMonitoria.discente.id = :idDiscente and o.discenteMonitoria.ativo = :ativo";
			Query q = getSession().createQuery(hql);
			q.setInteger("idDiscente", idDiscente);
			q.setBoolean("ativo", ativo);

			return q.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todas as atividades do mês informado
	 * 
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<AtividadeMonitor> findAtividadesMonitorByMesAno(int mes, int ano)
			throws DAOException {
		try {
			String hql = "from AtividadeMonitor a where a.ativo = trueValue() and a.mes = :mes and a.ano = :ano";

			if (ano > 0) {
				if (mes < 0)
					hql = "from AtividadeMonitor a where a.ativo = trueValue() and a.ano = :ano";

				Query q = getSession().createQuery(hql);
				q.setInteger("ano", ano);

				if (mes > 0)
					q.setInteger("mes", mes);

				return q.list();

			} else {
				throw new DAOException("Ano Obrigatório para a pesquisa");
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	/**
	 * Faz a busca por uma orientação dada a um Monitor. 
	 * 
	 * @param idMonitor
	 * @return
	 * @throws DAOException
	 */
	public Orientacao findOrientacaoByMonitor(int idMonitor)
			throws DAOException {
		try {
			Criteria c = getCriteria(Orientacao.class);
			c.add(Expression.eq("discenteMonitoria.id", idMonitor));
			return (Orientacao) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Método para buscar os projetos de monitoria de acordo com uma série de
	 * filtros opcionais
	 * 
	 * @param titulo
	 * @param anoProjeto
	 * @param idStatusProjeto
	 * @param idComponente
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> filter(String titulo, Integer anoProjeto, Integer idStatusProjeto, Integer idComponente, 
			Integer idEdital, Integer idServidor, Integer idPessoaCadastro, Integer idTipoProjeto, Integer idCentro, 
			Integer idTipoRelatorio, Boolean projetoAssociado, boolean considerarAvaliacoes) throws DAOException {

			StringBuilder hqlCount = new StringBuilder();
			hqlCount.append("SELECT  count(distinct pm.id) " 
					+ "FROM ProjetoEnsino pm "
					+ "INNER JOIN pm.projeto as pj "
					+ "INNER JOIN pj.unidade as unid "
					+ "INNER JOIN pj.situacaoProjeto as situacao "
					+ "LEFT JOIN pj.coordenador as coord "
					+ "LEFT JOIN coord.pessoa as pessoa "
					+ "LEFT JOIN pm.componentesCurriculares as comp "
					+ "LEFT JOIN pm.equipeDocentes as equipe "
					+ "LEFT JOIN equipe.servidor as servidor "
					+ "LEFT JOIN servidor.pessoa as pessoa "
					+ "LEFT JOIN pm.avaliacoes as ava ");
			hqlCount.append("WHERE 1 = 1 ");

			StringBuilder hqlConsulta = new StringBuilder();
			hqlConsulta.append("SELECT distinct pm.id, pm.tipoProjetoEnsino, pj.ano, pj.titulo, pm.ativo, " 
					+ "pj.id, pj.ativo, pj.ensino, pj.pesquisa, pj.extensao, pj.interno, pessoaCoord.nome, "
					+ "unid.id, unid.sigla, unid.nome, "
					+ "situacao.id, situacao.descricao, "
					+ "pj.idArquivo, pm.bolsasSolicitadas, pm.bolsasConcedidas, edital.tipo, equipe.id, pessoa.nome ");
			
					if ( considerarAvaliacoes )
						hqlConsulta.append(", ava.id, ava.tipoAvaliacao.id, ava.statusAvaliacao.id, ava.ativo ");
					
					hqlConsulta.append("FROM ProjetoEnsino pm "
					+ "INNER JOIN pm.projeto as pj "
					+ "INNER JOIN pj.unidade as unid "
					+ "INNER JOIN pj.situacaoProjeto as situacao "	
					+ "LEFT JOIN pj.edital as edital "
					+ "LEFT JOIN pj.coordenador as coord "
					+ "LEFT JOIN coord.pessoa as pessoaCoord "
					+ "LEFT JOIN pm.componentesCurriculares as comp "
					+ "LEFT JOIN pm.equipeDocentes as equipe "
					+ "LEFT JOIN equipe.servidor as servidor "
					+ "LEFT JOIN servidor.pessoa as pessoa "
					+ "LEFT JOIN pm.avaliacoes as ava ");
			
			hqlConsulta.append("WHERE pj.ativo = trueValue() ");

			StringBuilder hqlFiltros = new StringBuilder();
			// Filtros para a busca
			if (titulo != null) {
				hqlFiltros.append(" AND sem_acento(upper(pj.titulo)) like :titulo");
			}

			if (anoProjeto != null) {
				hqlFiltros.append(" AND pj.ano = :anoProjeto");
			}

			if (idCentro != null) {
				hqlFiltros.append(" AND unid.id = :idCentro");
			}

			if (projetoAssociado != null) {
				hqlFiltros.append(" AND pj.ensino = :projetoAssociado");
			}
			
			if (idStatusProjeto != null) {
				hqlFiltros.append(" AND situacao.id = :idSituacao ");
			}
			if (idComponente != null) {
				hqlFiltros.append(" AND comp.disciplina.id = :idComponente");
			}

			if (idEdital != null) {
				hqlFiltros.append(" AND pm.editalMonitoria.id = :idEdital");
			}

			if (idTipoProjeto != null) {
				hqlFiltros.append(" AND pm.tipoProjetoEnsino.id = :idTipoProjeto");
			}

			if (idServidor != null) {
				hqlFiltros.append(" AND servidor.id = :idServidor");
			}

			if (idPessoaCadastro != null) {
				hqlFiltros.append(" AND pj.registroEntrada.usuario.pessoa.id = :idPessoaCadastro ) ");
			}
			
			if (idTipoRelatorio != null) {
				hqlFiltros.append(" AND pm.id NOT IN (select pe.id FROM RelatorioProjetoMonitoria rpm JOIN rpm.projetoEnsino pe " +
						" WHERE rpm.tipoRelatorio.id IN (:idTipoRelatorio) AND rpm.ativo = trueValue()) ");
			}

			hqlCount.append(hqlFiltros.toString());
			hqlConsulta.append(hqlFiltros.toString());

			hqlConsulta.append(" ORDER BY unid.nome, pj.titulo, pessoa.nome asc ");

			// Criando consulta
			Query queryCount = getSession().createQuery(hqlCount.toString());
			Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

			// Populando os valores dos filtros
			if (titulo != null) {
				queryCount.setString("titulo", titulo.toUpperCase());
				queryConsulta.setString("titulo", "%" + StringUtils.toAscii(titulo.toUpperCase()) + "%");
			}
			
			if (anoProjeto != null) {
				queryCount.setInteger("anoProjeto", anoProjeto);
				queryConsulta.setInteger("anoProjeto", anoProjeto);
			}
			
			if (idStatusProjeto != null) {
				queryCount.setInteger("idSituacao", idStatusProjeto);
				queryConsulta.setInteger("idSituacao", idStatusProjeto);
			}
			
			if (idComponente != null) {
				queryCount.setInteger("idComponente", idComponente);
				queryConsulta.setInteger("idComponente", idComponente);
			}

			if (idEdital != null) {
				queryCount.setInteger("idEdital", idEdital);
				queryConsulta.setInteger("idEdital", idEdital);
			}

			if (idTipoProjeto != null) {
				queryCount.setInteger("idTipoProjeto", idTipoProjeto);
				queryConsulta.setInteger("idTipoProjeto", idTipoProjeto);
			}

			if (idCentro != null) {
				queryCount.setInteger("idCentro", idCentro);
				queryConsulta.setInteger("idCentro", idCentro);
			}
			
			if (projetoAssociado != null) {
				queryCount.setBoolean("projetoAssociado", projetoAssociado);
				queryConsulta.setBoolean("projetoAssociado", projetoAssociado);
			}
			
			if (idServidor != null) {
				queryCount.setInteger("idServidor", idServidor);
				queryConsulta.setInteger("idServidor", idServidor);
			}

			if (idPessoaCadastro != null) {
				queryCount.setInteger("idPessoaCadastro", idPessoaCadastro);
				queryConsulta.setInteger("idPessoaCadastro", idPessoaCadastro);
			}
			
			if (idTipoRelatorio != null) {
				queryCount.setInteger("idTipoRelatorio", idTipoRelatorio);
				queryConsulta.setInteger("idTipoRelatorio", idTipoRelatorio);
			}

			Long total = (Long) queryCount.uniqueResult();
			if (total > LIMITE_RESULTADOS)
				throw new LimiteResultadosException("A consulta retornou "
						+ total	+ "	resultados. Por favor, restrinja mais a busca.");

			List<Object[]> lista = queryConsulta.list();

			// pm.id, pm.ano, pm.titulo, pm.unidade.id, pm.unidade.nome,
			// pm.situacaoProjeto.id, pm.situacaoProjeto.descricao
			ArrayList<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = lista.get(a);

				ProjetoEnsino p = new ProjetoEnsino((Integer) (colunas[0]));

				// Verifica se é projeto novo
				if (!result.contains(p)) {

					ProjetoEnsino pm = new ProjetoEnsino();
					pm.setId((Integer) colunas[col++]);
					pm.setTipoProjetoEnsino((TipoProjetoEnsino) colunas[col++]);
					pm.setAno((Integer) colunas[col++]);
					pm.setTitulo((String) colunas[col++]);
					pm.setAtivo( (Boolean) colunas[col++] );
					
					pm.getProjeto().setId( (Integer) colunas[col++] );
					pm.getProjeto().setAtivo((Boolean) colunas[col++]);
					pm.getProjeto().setEnsino((Boolean) colunas[col++]);
					pm.getProjeto().setPesquisa((Boolean) colunas[col++]);
					pm.getProjeto().setExtensao((Boolean) colunas[col++]);
					pm.getProjeto().setInterno((Boolean) colunas[col++]);
					
					//Projetos com cadastro em andamento podem estar sem coordenador ativo
					//Evita erro no método getCoordenacao de ProjetoEnsino.
					if( ((String) colunas[col]) != null ) {
						pm.getProjeto().setCoordenador(new MembroProjeto());
						pm.getProjeto().getCoordenador().getServidor().getPessoa().setNome((String) colunas[col++]);
					}else {
						col++;
					}
					
					Unidade unidade = new Unidade();
					unidade.setId((Integer) colunas[col++]);
					unidade.setSigla((String) colunas[col++]);
					unidade.setNome((String) colunas[col++]);
					pm.setUnidade(unidade);

					TipoSituacaoProjeto situacao = new TipoSituacaoProjeto();
					situacao.setId((Integer) colunas[col++]);
					situacao.setDescricao((String) colunas[col++]);
					pm.setSituacaoProjeto(situacao);

					pm.setIdArquivo((Integer) colunas[col++]);

					pm.setBolsasSolicitadas((Integer) colunas[col++]);
					pm.setBolsasConcedidas((Integer) colunas[col++]);
										
					if (colunas[col] != null){
							Edital edital = new Edital();
							edital.setTipo((Character) colunas[col++]);
							pm.getProjeto().setEdital(edital);
					}
					
					result.add(pm);
				}

				//Um projeto com vários membros na equipe.
				col = 21;
				if (colunas[col] != null) {
					EquipeDocente eqp = new EquipeDocente();
					eqp.setId((Integer) colunas[col++]);
					eqp.setServidor(new Servidor());
					eqp.getServidor().setPessoa(new Pessoa());
					eqp.getServidor().getPessoa().setNome( (String) colunas[col++] );
					result.get(result.indexOf(p)).addEquipeDocente(eqp);
				}

				//Um projeto com várias avaliações.
				col = 22;
				if ( colunas[col] != null && considerarAvaliacoes ) {
					AvaliacaoMonitoria ava = new AvaliacaoMonitoria();
					ava.setId((Integer) colunas[col++]);
					TipoAvaliacaoMonitoria ta = new TipoAvaliacaoMonitoria((Integer) colunas[col++]);
					ava.setTipoAvaliacao(ta);
					StatusAvaliacao st = new StatusAvaliacao((Integer) colunas[col++]);
					ava.setStatusAvaliacao(st);
					ava.setAtivo((Boolean) colunas[col++]);
					
					if (!result.get(result.indexOf(p)).getAvaliacoes().contains(ava)) {
						result.get(result.indexOf(p)).getAvaliacoes().add(ava);
					}
				}

			}

			return result;

	}

	/**
	 * Retorna TODOS os projetos de monitoria que possuem qualquer tipo relatório de
	 * projeto cadastrado
	 */
	@SuppressWarnings("unchecked")
	public List<ProjetoEnsino> findProjetosComRelatorio() throws DAOException {

		try {
			String hql = "from ProjetoEnsino pm where pm.relatoriosProjetoMonitoria is not empty";
			Query q = getSession().createQuery(hql);

			return q.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna TRUE se o servidor for coordenador de algum projeto de monitoria
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public boolean isCoordenadorProjeto(Servidor servidor) throws DAOException {

		try {
			String hql = "select count(*) from EquipeDocente ed where ed.servidor.id = :servidor and coordenador = trueValue()";
			Query q = getSession().createQuery(hql);
			q.setInteger("servidor", servidor.getId());

			Long count = (Long) q.uniqueResult();
			return count > 0;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Retorna TRUE se o usuário for Autor de algum projeto de monitoria
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public boolean isAutorProjetoMonitoria(Usuario usuario) throws DAOException {

		try {
			String hql = "select count(*) from ProjetoEnsino pm inner join pm.projeto.registroEntrada reg where reg.usuario.id = :idUsuario";
			Query q = getSession().createQuery(hql);
			q.setInteger("idUsuario", usuario.getId());

			Long count = (Long) q.uniqueResult();
			return count > 0;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Faz uma busca por provas de Seleção para projetos de monitoria de acordo com o centro informado e que estão com o prazo de inscrição para
	 * seleção de monitoria com data maior ou igual à data informada.
	 *	 
	 * @param idCentro            
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ProvaSelecao> findByCentroAndPrazoInscricao(Integer idCentro, Date data)
			throws DAOException {

		try {

			Criteria c = getCriteria(ProvaSelecao.class);
			c.add(Expression.eq("ativo", true));

			if (data != null)
				c.add(Expression.ge("dataLimiteIncricao", DateUtils.truncate(
						data, Calendar.DAY_OF_MONTH)));

			c.addOrder(Order.asc("dataLimiteIncricao"));

			if (idCentro != null) {
				Criteria subC = c.createCriteria("projetoEnsino").createCriteria("projeto");
				subC.add(Expression.eq("unidade.id", idCentro));
			}

			return c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Realiza a busca por provas de seleção de monitoria
	 * por diversos critérios, onde um deles é a data. 
	 * 
	 * @param restricoes
	 * @param parametros
	 * @param data
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ProvaSelecao> findSelecao(
			RestricoesBuscaAgregadorBolsas restricoes,
			ParametroBuscaAgregadorBolsas parametros, Date data)
			throws HibernateException, DAOException {

		Criteria c = getCriteria(ProvaSelecao.class);
		c.add(Expression.eq("ativo", true));
		

		Criteria cProjetoEnsino = c.createCriteria("projetoEnsino");
		Criteria cProjeto = cProjetoEnsino.createCriteria("projeto");
		
		if (data != null) {
			c.add(Expression.ge("dataLimiteIncricao", DateUtils.truncate(data, Calendar.DAY_OF_MONTH)));
			c.add(Expression.eq("situacaoProva.id", TipoSituacaoProvaSelecao.AGUARDANDO_INSCRICAO));
		}

		c.addOrder(Order.asc("dataLimiteIncricao"));

		if (restricoes.isBuscaCentro()) {
			cProjeto.add(Restrictions.eq("unidade.id", parametros.getCentro()));
		}

		if (restricoes.isBuscaPalavraChave()) {
			cProjeto.add(Restrictions.ilike("titulo", parametros.getPalavraChave(), MatchMode.ANYWHERE));
		}

		if (restricoes.isBuscaServidor()) {
			cProjetoEnsino.createCriteria("equipeDocentes").add(
					Restrictions.eq("servidor.id", parametros.getServidor()
							.getId()));
		}

		if (restricoes.isBuscaDisciplina()) {
			Criteria subC = c.createCriteria("componentesObrigatorios");
			subC.createCriteria("componenteCurricularMonitoria").createCriteria("disciplina").add(Restrictions.eq("codigo", parametros.getDisciplina().getCodigo()));
		}

		return c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	}

	/**
	 * Utilizado para classificação geral de projetos
	 */
	public List<ProjetoEnsino> findProjetosClassificacaoByEdital(
			EditalMonitoria edital) throws DAOException {

		Connection con = null;
		List<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();

		try {
			con = Database.getInstance().getSigaaConnection();

			String sql = "select "
					+ "(select count(distinct id_servidor) from monitoria.equipe_docente where id_projeto_monitoria=p.id_projeto_monitoria) as qtd_professores, "

					+ "(select count(distinct id_disciplina) from monitoria.componente_curricular_monitoria where id_projeto_monitoria=p.id_projeto_monitoria) as qtd_componentes, "

					+ "(select count(distinct cc.id_unidade) from monitoria.componente_curricular_monitoria ccm, ensino.componente_curricular cc where ccm.id_disciplina = cc.id_disciplina "
					+ "and ccm.id_projeto_monitoria = p.id_projeto_monitoria) as qtd_departamentos, "

					+ "media_analise, "

					+ "(select case when count(distinct t.id_turma) > 0 then ((count(mc.id_matricula_componente)/count(distinct t.id_turma))/15.0) else 0.0 end from monitoria.componente_curricular_monitoria ccm, ensino.matricula_componente mc, ensino.turma t "
					+ "where ccm.id_disciplina = t.id_disciplina and t.id_turma = mc.id_turma and ccm.id_projeto_monitoria=p.id_projeto_monitoria "
					+ "and ((t.periodo = ? and t.ano = ?) or (t.periodo = ? and t.ano = ?))"
					+ ") as rt, "

					+ "p.id_projeto_monitoria, pr.titulo, pr.ano, pr.id_unidade, u.sigla, pr.id_tipo_situacao_projeto, s.descricao "

					+ "from monitoria.projeto_monitoria p, projetos.projeto pr, comum.unidade u, projetos.tipo_situacao_projeto s "
					+ "where pr.id_projeto = p.id_projeto "
					+ "and p.id_edital = ? "
					+ "and ((pr.id_tipo_situacao_projeto = ?) or (pr.id_tipo_situacao_projeto = ?))"
					+ "and pr.id_unidade = u.id_unidade "
					+ "and pr.id_tipo_situacao_projeto = s.id_tipo_situacao_projeto";

			// só pega as turmas do semestre atual e do anterior
			int anoLetivoAtual = edital.getAno();
			int periodoLetivoAtual = edital.getSemestre();

			int anoLetivoAnterior = anoLetivoAtual;
			int periodoLetivoAnterior = 1;

			if (periodoLetivoAtual == 1) {
				periodoLetivoAnterior = 2;
				anoLetivoAnterior = anoLetivoAtual - 1;
			}

			PreparedStatement st = con.prepareStatement(sql);
			st.setInt(1, periodoLetivoAtual);
			st.setInt(2, anoLetivoAtual);
			st.setInt(3, periodoLetivoAnterior);
			st.setInt(4, anoLetivoAnterior);
			st.setInt(5, edital.getId());
			st.setInt(6, TipoSituacaoProjeto.MON_RECOMENDADO);
			st.setInt(7, TipoSituacaoProjeto.MON_NAO_RECOMENDADO);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				ProjetoEnsino projeto = new ProjetoEnsino();
				projeto.setId(rs.getInt("id_projeto_monitoria"));
				projeto.setTitulo(rs.getString("titulo"));
				projeto.setAno(rs.getInt("ano"));

				projeto.setUnidade(new Unidade());
				projeto.getUnidade().setId(rs.getInt("id_unidade"));
				projeto.getUnidade().setSigla(rs.getString("sigla"));

				projeto.setSituacaoProjeto(new TipoSituacaoProjeto());
				projeto.getSituacaoProjeto().setId(
						rs.getInt("id_tipo_situacao_projeto"));
				projeto.getSituacaoProjeto().setDescricao(
						rs.getString("descricao"));

				projeto.setNumProfessores(rs.getInt("qtd_professores"));
				projeto.setNumDepartamentos(rs.getInt("qtd_departamentos"));
				projeto.setNumComponentesCurriculares(rs
						.getInt("qtd_componentes"));
				projeto.setMediaAnalise(rs.getDouble("media_analise"));
				projeto.setRt(rs.getDouble("rt"));

				result.add(projeto);
			}

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			Database.getInstance().close(con);
		}

		return result;
	}

	/**
	 * Retorna a quantidade de projetos de monitoria que o servidor passado por
	 * parâmetro faz parte.
	 * 
	 * @param servidor
	 *            o servidor o qual os projetos serão consultados
	 * @param ativo
	 *            se o membro está ativo ou não no projeto
	 * @return
	 * @throws DAOException
	 */
	public int findNumProjetosByServidor(Servidor servidor, Boolean ativo)
			throws DAOException {

		Query q;

		if (ativo == null) {
			q = getSession().createQuery(
					"select count(distinct equipe.id) from EquipeDocente equipe "
							+ "where equipe.servidor.id = :idServidor");
		} else {
			q = getSession()
					.createQuery(
							"select count(distinct equipe.id) from EquipeDocente equipe "
									+ "where equipe.servidor.id = :idServidor and equipe.ativo = :ativo");

			q.setBoolean("ativo", ativo);
		}

		q.setInteger("idServidor", servidor.getId());

		return ((Long) q.uniqueResult()).intValue();

	}

	/**
	 * Busca projetos de monitoria ativos que o título contém o trecho por
	 * parâmetro
	 * 
	 * @param titulo
	 *            o início do título do projeto
	 * @return
	 * @throws DAOException
	 * @author Victor Hugo
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByTrecho(String trecho)
			throws DAOException {

		try {
			Criteria c = getSession().createCriteria(ProjetoEnsino.class);
			c.add(Restrictions.like("projeto.titulo", trecho.toUpperCase(),
					MatchMode.ANYWHERE));
			c.add(Restrictions.isNull("dataFim"));

			c.addOrder(Order.asc("projeto.titulo"));

			return c.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Retorna os projetos com a quantidade de monitores setada
	 * 
	 * @param idCentro
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findQuantitativoMonitores(
			Integer idCentro, Integer idCurso, Integer ano,
			Integer idTipoMonitor) throws HibernateException, DAOException {

		StringBuffer hql = new StringBuffer();

		hql
				.append(" SELECT pm.id AS id, pm.projeto.titulo AS titulo, count(dm) AS totalMonitores, ");
		hql.append(" pm.projeto.ano AS ano, pm.projeto.unidade.sigla AS siglaUnidade ");
		hql
				.append(" FROM ProjetoEnsino pm LEFT OUTER JOIN pm.discentesMonitoria AS dm WHERE dm.ativo = trueValue()");

		// filtros
		if (idCentro != null) {
			hql.append(" AND pm.projeto.unidade.id = :idCentro ");
		}

		if (idCurso != null) {
			hql.append(" AND dm.discente.discente.curso.id = :idCurso ");
		}

		if (ano != null) {
			hql.append(" AND pm.projeto.ano = :ano ");
		}

		if (idTipoMonitor != null) {
			hql.append(" AND dm.tipoVinculo.id = :idTipoMonitor ");
		}

		hql.append(" GROUP BY pm.id, pm.projeto.titulo, pm.projeto.ano, pm.projeto.unidade.sigla ");
		hql.append(" ORDER BY pm.projeto.titulo ");

		Query query = getSession().createQuery(hql.toString());

		if (idCentro != null) {
			query.setInteger("idCentro", idCentro);
		}

		if (idCurso != null) {
			query.setInteger("idCurso", idCurso);
		}

		if (ano != null) {
			query.setInteger("ano", ano);
		}

		if (idTipoMonitor != null) {
			query.setInteger("idTipoMonitor", idTipoMonitor);
		}


		// return query.list();
		return query.setResultTransformer(
				new AliasToBeanResultTransformer(ProjetoEnsino.class)).list();

	}
	
	
	/**
	 * Retorna os projetos com a quantidade de monitores por departamento.
	 * @param idCentro
	 * @param ano
	 * @param idTipoMonitor
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findMonitoresPorCentro(Integer idCentro, Integer ano, Integer idTipoMonitor, Integer statusDiscenteMonitoria, Integer idSituacaoProj) 
			throws HibernateException, DAOException {

		StringBuffer hql = new StringBuffer();

		hql	.append(" SELECT pm.id AS id, pm.projeto.titulo AS titulo, count(dm) AS totalMonitores, ");
		hql.append(" pm.projeto.ano AS ano, pm.projeto.unidade.sigla AS siglaUnidade, pm.bolsasConcedidas AS bolsasConcedidas, pm.projeto.unidade.nome AS unidadeNome ");
		hql.append(" FROM ProjetoEnsino pm INNER JOIN pm.discentesMonitoria AS dm WHERE dm.ativo = trueValue() ");

		// filtros
		if (idCentro != null) {
			hql.append(" AND pm.projeto.unidade.id = :idCentro ");
		}

		if (ano != null) {
			hql.append(" AND pm.projeto.ano = :ano ");
		}

		if (idTipoMonitor != null) {
			hql.append(" AND dm.tipoVinculo.id = :idTipoMonitor ");
		}

		if (statusDiscenteMonitoria != null) {
			hql.append(" AND dm.situacaoDiscenteMonitoria.id = :idSituacaoDiscenteMoni ");
		}

		if (idSituacaoProj != null) {
			hql.append(" AND pm.situacaoProjeto.id = :idSituacaoProj ");
		}

		hql.append(" GROUP BY pm.id, pm.projeto.titulo, pm.projeto.ano, pm.projeto.unidade.sigla, pm.bolsasConcedidas, pm.projeto.unidade.nome  ");
		hql.append(" ORDER BY pm.projeto.unidade.sigla ,pm.projeto.titulo ");

		Query query = getSession().createQuery(hql.toString());
		

		if (idCentro != null) {
			query.setInteger("idCentro", idCentro);
		}

		if (ano != null) {
			query.setInteger("ano", ano);
		}

		if (idTipoMonitor != null) {
			query.setInteger("idTipoMonitor", idTipoMonitor);
		}

		if (statusDiscenteMonitoria != null) {
			query.setInteger("idSituacaoDiscenteMoni", statusDiscenteMonitoria);
		}

		if (idSituacaoProj != null) {
			query.setInteger("idSituacaoProj", idSituacaoProj);
		}
		
		return query.list();

	}

	/**
	 * Retorna os projetos e seus monitores
	 * 
	 * @param idCentro
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findMonitoresPorProjeto(Integer idCentro,
			Integer idCurso) throws HibernateException, DAOException {

		StringBuffer hql = new StringBuffer();

		hql.append(" SELECT pm.id AS id, pm.projeto.titulo AS titulo, pm.projeto.ano AS ano, pm.projeto.unidade.sigla AS unidade, dm.id AS idDiscente,  ");
		hql.append(" d.matricula AS matricula, d.pessoa.nome AS nome, dm.situacaoDiscenteMonitoria.id AS idSituacao, ");
		hql.append(" dm.situacaoDiscenteMonitoria.descricao AS descSituacao, tipoVinculo.id AS tipo, tipoVinculo.descricao, dm.ativo ");
		hql.append(" FROM ProjetoEnsino pm LEFT OUTER JOIN pm.discentesMonitoria AS dm " +
						" JOIN dm.tipoVinculo AS tipoVinculo " +
						" JOIN dm.discente dg " +
						" JOIN dg.discente d " +
						" WHERE 1=1 ");

		// filtros
		if (idCentro != null) {
			hql.append(" AND pm.projeto.unidade.id = :idCentro ");
		}

		if (idCurso != null) {
			hql.append(" AND d.curso.id = :idCurso ");
		}


		hql.append(" ORDER BY pm.projeto.titulo ");

		Query query = getSession().createQuery(hql.toString());

		if (idCentro != null) {
			query.setInteger("idCentro", idCentro);
		}

		if (idCurso != null) {
			query.setInteger("idCurso", idCurso);
		}

		ArrayList<ProjetoEnsino> retorno = new ArrayList<ProjetoEnsino>();

		List<Object> list = query.list();
		for (Iterator<Object> it = list.iterator(); it.hasNext();) {
			Object[] result = (Object[]) it.next();

			int col = 0;
			
			ProjetoEnsino pm = new ProjetoEnsino();
			pm.setId((Integer) result[col++]);
			pm.setTitulo((String) result[col++]);
			pm.setAno((Integer) result[col++]);
			pm.getUnidade().setSigla((String) result[col++]);
			// pm.setTotalMonitores( (Long) result[8] );

			DiscenteMonitoria discente = new DiscenteMonitoria();
			discente.setId((Integer) result[col++]);
			discente.getDiscente().setMatricula((Long) result[col++]);
			discente.getDiscente().getPessoa().setNome((String) result[col++]);
			discente.getSituacaoDiscenteMonitoria().setId((Integer) result[col++]);
			discente.getSituacaoDiscenteMonitoria().setDescricao(
					(String) result[col++]);
			discente.getTipoVinculo().setId((Integer) result[col++]);
			discente.getTipoVinculo().setDescricao((String) result[col++]);
			discente.setAtivo((Boolean) result[col++]);			
			
			if (!retorno.contains(pm)) { // ainda nao foi adicionado
				pm.getDiscentesMonitoria().add(discente);
				retorno.add(pm);
			} else {
				pm = retorno.get(retorno.indexOf(pm));
				pm.getDiscentesMonitoria().add(discente);
			}

		}

		return retorno;

	}

	/**
	 * Retorna todas informações de seleção cadastradas do projeto informado
	 * 
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProvaSelecao> findSelecoesByProjeto(Integer idProjeto,
			Boolean ativo) throws DAOException {

		Criteria c = getSession().createCriteria(ProvaSelecao.class);

		if (ativo != null)
			c.add(Expression.eq("ativo", ativo));

		c.createCriteria("projetoEnsino").add(Expression.eq("id", idProjeto));
		c.addOrder(Order.desc("dataCadastro"));

		return c.list();
	}

	/**
	 * Retorna o total de Bolsas Remuneradas já distribuídas nas provas de
	 * seleção excluindo a prova de seleção informada.
	 * 
	 */
	public int findTotalBolsasRemuneradasProvas(ProvaSelecao prova)
			throws DAOException {

		try {
			String hql = "select sum(s.vagasRemuneradas) from ProvaSelecao s "
					+ "where s.projetoEnsino.id = :idProjeto and s.ativo = trueValue() and s.id != :idProva";
			Query q = getSession().createQuery(hql);
			q.setInteger("idProjeto", prova.getProjetoEnsino().getId());
			q.setInteger("idProva", prova.getId());

			Number result = (Number) q.uniqueResult();

			if (result != null)
				return result.intValue();
			else
				return 0;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna o total de Bolsas NÃO Remuneradas já distribuídas nas provas de
	 * seleção excluindo a prova de seleção informada.
	 * 
	 */
	public int findTotalBolsasNaoRemuneradasProvas(ProvaSelecao prova)
			throws DAOException {

		try {
			String hql = "select sum(s.vagasNaoRemuneradas) from ProvaSelecao s "
					+ "where s.projetoEnsino.id = :idProjeto and s.ativo = trueValue()  and s.id != :idProva";
			Query q = getSession().createQuery(hql);
			q.setInteger("idProjeto", prova.getProjetoEnsino().getId());
			q.setInteger("idProva", prova.getId());

			Number result = (Number) q.uniqueResult();

			if (result != null)
				return result.intValue();
			else
				return 0;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	
	/**
	 * Retorna o total de discentes ativos do tipo informado na prova informada.
	 * 
	 * Utilizado no cadastro e alteração da prova seletiva usado para proibir
	 * que o docente reduza bolsas, que ainda estão ativas, da prova seletiva.
	 * 
	 * @param prova
	 * @param tipoMonitoria
	 * @return
	 * @throws DAOException
	 */
	public int findTotalDiscentesAtivosProva(ProvaSelecao prova,
			int tipoMonitoria) throws DAOException {

		try {
			String hql = "select count(d.id) from DiscenteMonitoria d "
					+ "where d.provaSelecao.id = :idProva and d.ativo = trueValue() and d.tipoVinculo.id = :tipoMonitoria " +
							"and d.situacaoDiscenteMonitoria.id = :idSituacaoAtiva";
			Query q = getSession().createQuery(hql);
			q.setInteger("idProva", prova.getId());
			q.setInteger("tipoMonitoria", tipoMonitoria);
			q.setInteger("idSituacaoAtiva", SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);

			Number result = (Number) q.uniqueResult();

			if (result != null)
				return result.intValue();
			else
				return 0;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Todas as autorizações do Projeto Monitoria ativas
	 * 
	 * @author Ilueny
	 */
	@SuppressWarnings("unchecked")
	public Collection<AutorizacaoProjetoMonitoria> findByProjetoEnsino(int idProjetoEnsino) throws HibernateException, DAOException {		
		String hql = "select ad from AutorizacaoProjetoMonitoria ad inner join ad.projetoEnsino a where (a.id = :idProjetoEnsino) and (ad.ativo = trueValue())";

		Query query = getSession().createQuery(hql);
			  query.setInteger("idProjetoEnsino", idProjetoEnsino);
		
		return query.list();
	}

	/**
	 * Retorna o calendário ativo (único) do ano informado.
	 * Dependendo do calendário os relatórios são liberados para envio 
	 * pelos coordenadores ou monitores.
	 * Dependendo do calendário os monitores podem entrar no projeto.
	 * 	 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public CalendarioMonitoria findCalendarioByAnoAtivo(int ano) throws DAOException {
		Criteria c = getSession().createCriteria(CalendarioMonitoria.class);
		c.add(Expression.eq("anoReferencia", ano));
		c.add(Expression.eq("ativo", true));
		c.setMaxResults(1);
		return (CalendarioMonitoria) c.uniqueResult();
	}

	/**
	 * Retorna o projeto de ensino associado ao projeto base com id informado.
	 * 
	 * @param idProjetoBase
	 * @param idTipoProjeto Projeto de monitoria ou projeto de melhoria da qualidade do ensino.
	 * @return
	 * @throws DAOException
	 */
	public ProjetoEnsino findByProjetoBaseAndTipo(int idProjetoBase, int idTipoProjeto) throws DAOException {
	    
	    Criteria c = getSession().createCriteria(ProjetoEnsino.class);
	    c.add(Expression.eq("projeto.id", idProjetoBase));
	    c.add(Expression.eq("tipoProjetoEnsino.id", idTipoProjeto));
	    c.createCriteria("projeto").add(Expression.eq("ativo", true));
	    c.setMaxResults(1);
	    return (ProjetoEnsino) c.uniqueResult();
	    
	}
	
	/**
	 * Retorna projetos de ensino associados ao projeto base com id informado.
	 * 
	 * @param idProjetoBase
	 * @param idTipoProjeto Projeto de monitoria ou projeto de melhoria da qualidade do ensino.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByProjetosBaseAndTipo(int idProjetoBase, int idTipoProjeto) throws DAOException {
	    
	    Criteria c = getSession().createCriteria(ProjetoEnsino.class);
	    c.add(Expression.eq("projeto.id", idProjetoBase));
	    c.add(Expression.eq("tipoProjetoEnsino.id", idTipoProjeto));
	    c.createCriteria("projeto").add(Expression.eq("ativo", true));
	    
	    return  c.list();
	    
	}
	
	
	
	/**
	 * Gera um relatório com os projetos ativos que possuam monitores.
	 * 
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> relatorioProjetosAtivosComMonitoresAtivosInativos(Integer ano)
		throws DAOException {

		try {
			StringBuilder hql = new StringBuilder();
			
			hql.append(" select projM.id, projM.bolsasConcedidas, proj.id, proj.ano, proj.titulo, sitProj.id, sitProj.descricao, " +
								" dm.id, dm.ativo, dm.dataInicio, dm.dataFim, tipoVinculo.id, tipoVinculo.descricao, sitDiscenteM.id, sitDiscenteM.descricao, " +
								" pessoa.nome " +
					   " from ProjetoEnsino as projM " +					   
					   " inner join projM.projeto as proj  " +
					   " inner join projM.discentesMonitoria as dm " +
					   " inner join dm.tipoVinculo tipoVinculo " +
					   " inner join proj.situacaoProjeto as sitProj " +
					   " inner join dm.discente as d " +
					   " inner join d.discente as discente " +
					   " inner join dm.situacaoDiscenteMonitoria sitDiscenteM" +
					   " inner join discente.pessoa as pessoa  " +
					   " where proj.ativo = trueValue() and proj.ano = :ano " +
					   " order by projM.id, dm.ativo desc, tipoVinculo.id asc, sitDiscenteM.id asc  ");
			
			Query query = getSession().createQuery(hql.toString());
	
			query.setInteger("ano", ano);			
			
			List<Object> lista = query.list();

			ArrayList<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();
			
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = (Object[]) lista.get(a);
				//Preenche os dados do projeto
				ProjetoEnsino pe = new ProjetoEnsino();								
				pe.setId((Integer) colunas[col++]);
				pe.setBolsasConcedidas((Integer) colunas[col++]);
				pe.getProjeto().setId((Integer) colunas[col++]);
				pe.getProjeto().setAno((Integer) colunas[col++]);
				pe.getProjeto().setTitulo((String) colunas[col++]);
				pe.getProjeto().getSituacaoProjeto().setId((Integer) colunas[col++]);
				pe.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[col++]);
					
				
				
				//Enquanto for o mesmo projeto de ensino, adicione os discentes.
				while(pe.getId() == (Integer) colunas[0])   {					
					
					col = 7;
					
					DiscenteMonitoria dm = new DiscenteMonitoria();
					
					dm.setId((Integer) colunas[col++]);
					dm.setAtivo((Boolean) colunas[col++]);
					dm.setDataInicio((Date) colunas[col++]);
					dm.setDataFim((Date) colunas[col++]);
					dm.getTipoVinculo().setId((Integer) colunas[col++]);
					dm.getTipoVinculo().setDescricao((String) colunas[col++]);
					dm.getSituacaoDiscenteMonitoria().setId((Integer) colunas[col++]);
					dm.getSituacaoDiscenteMonitoria().setDescricao((String) colunas[col++]);
					dm.getDiscente().getPessoa().setNome((String) colunas[col++]);
					
					pe.getDiscentesMonitoria().add(dm);
					
					a++;
					if( a < lista.size())
						colunas = (Object[]) lista.get(a);
					else
						break;
				}
				
				
				
				result.add(pe);
			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	
	/**
	 * Retorna todos os Projetos de Monitoria Ativos 
	 * relacionados a um determinado projeto base.
	 * Utilizado na gestão de projetos associados.
	 * 
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByProjetoBase(Integer idProjeto) throws DAOException {

		StringBuilder hql = new StringBuilder();		
		hql.append(" select pm.id, pj.id, pj.ano, pj.titulo, sit.id, sit.descricao, tip.id, " +
				   " tip.descricao, pm.bolsasSolicitadas, pm.bolsasConcedidas, pj.dataCadastro, pm.dataEnvio " +
				   " from ProjetoEnsino as pm " +					   
				   " inner join pm.projeto as pj " +
				   " inner join pm.situacaoProjeto as sit " +
				   " inner join pm.tipoProjetoEnsino as tip " +				   
				   " where pm.ativo = trueValue() and pj.ativo = trueValue() and pj.id = :idProjeto " +
				   " order by pj.titulo ");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idProjeto", idProjeto);	
		List<Object> lista = query.list();
		ArrayList<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();
		for (int a = 0; a < lista.size(); a++) {
       			int col = 0;
       			Object[] colunas = (Object[]) lista.get(a);
       			ProjetoEnsino pm = new ProjetoEnsino();								
       			pm.setId((Integer) colunas[col++]);
       			pm.getProjeto().setId((Integer) colunas[col++]);
       			pm.getProjeto().setAno((Integer) colunas[col++]);
       			pm.getProjeto().setTitulo((String) colunas[col++]);
       			pm.getSituacaoProjeto().setId((Integer) colunas[col++]);
       			pm.getSituacaoProjeto().setDescricao((String) colunas[col++]);       			
       			pm.getTipoProjetoEnsino().setId((Integer) colunas[col++]);
       			pm.getTipoProjetoEnsino().setDescricao((String) colunas[col++]);
       			pm.setBolsasSolicitadas((Integer) colunas[col++]);
       			pm.setBolsasConcedidas((Integer) colunas[col++]);
       			pm.setDataCadastro((Date) colunas[col++]);
       			pm.setDataEnvio((Date) colunas[col++]);
       			
       			result.add(pm);
		}
		return result;
	}	
	
	
	/**
	 * Retorna todos os Projetos de Monitoria Ativos 
	 * relacionados a um determinado projeto base.
	 * Utilizado na gestão de projetos associados.
	 * 
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public ProjetoEnsino findByProjeto(Integer idProjeto) throws DAOException {

		StringBuilder hql = new StringBuilder();	
		hql.append(" select " +
				   " pm.id, pm.ativo, pj.id, pj.ativo, pj.ano, pj.titulo, pj.renovacao, pj.numeroInstitucional, pj.situacaoProjeto.id, sit.id, sit.descricao, tip.id, " +
				   " tip.descricao, pm.bolsasSolicitadas, pm.bolsasConcedidas, pj.dataCadastro, pm.dataEnvio, " +
				   " pj.dataInicio, pj.dataFim, coor.id, serv.id, " +
				   " pes.id, pes.nome, pes.email, pj.unidade, pj.resumo, pj.justificativa, " +
				   " pj.objetivos, pj.resultados, pj.metodologia, pj.referencias, pm.avaliacao, pm.produto , pm.processoSeletivo, edital " +
				   " from ProjetoEnsino pm " +					   
				   " inner join pm.projeto as pj " +
				   " left join pj.coordenador as coor " +
				   " left join coor.servidor as serv " +
				   " left join serv.pessoa as pes " +
				   " inner join pm.situacaoProjeto as sit " +
				   " inner join pm.tipoProjetoEnsino as tip " +
				   " left join pm.editalMonitoria as edital " +
				   " where pm.ativo = trueValue() and pj.ativo = trueValue() and pm.id = :idProjeto " +
				   " order by pj.titulo ");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idProjeto", idProjeto);	
		List<Object> lista = query.list();
		ArrayList<ProjetoEnsino> result = new ArrayList<ProjetoEnsino>();
		for (int a = 0; a < lista.size(); a++) {
       			int col = 0;
       			Object[] colunas = (Object[]) lista.get(a);
       			ProjetoEnsino pm = new ProjetoEnsino();								
       			pm.setId((Integer) colunas[col++]);
       			pm.setAtivo((Boolean) colunas[col++]);
       			pm.getProjeto().setId((Integer) colunas[col++]);
       			pm.getProjeto().setAtivo((Boolean) colunas[col++]);
       			pm.getProjeto().setAno((Integer) colunas[col++]);
       			pm.getProjeto().setTitulo((String) colunas[col++]);
       			pm.getProjeto().setRenovacao((Boolean) colunas[col++]);
       			pm.getProjeto().setNumeroInstitucional((Integer) colunas[col++]);
       			pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto((Integer) colunas[col++]));       			
       			pm.getSituacaoProjeto().setId((Integer) colunas[col++]);
       			pm.getSituacaoProjeto().setDescricao((String) colunas[col++]);       			
       			pm.getTipoProjetoEnsino().setId((Integer) colunas[col++]);
       			pm.getTipoProjetoEnsino().setDescricao((String) colunas[col++]);
       			pm.setBolsasSolicitadas((Integer) colunas[col++]);
       			pm.setBolsasConcedidas((Integer) colunas[col++]);
       			pm.setDataCadastro((Date) colunas[col++]);
       			pm.setDataEnvio((Date) colunas[col++]);
       			pm.getProjeto().setDataInicio((Date) colunas[col++]);
       			pm.getProjeto().setDataFim((Date) colunas[col++]);
       			if(colunas[col++] != null){
       				pm.getProjeto().setCoordenador(new MembroProjeto((Integer) colunas[col]));
       				pm.getProjeto().getCoordenador().setServidor(new Servidor());
       				pm.getProjeto().getCoordenador().getServidor().setId((Integer) colunas[col++]);
       				pm.getProjeto().getCoordenador().getServidor().setPessoa(new Pessoa());
       				pm.getProjeto().getCoordenador().getServidor().getPessoa().setId((Integer) colunas[col++]);
       				pm.getProjeto().getCoordenador().getServidor().getPessoa().setNome((String) colunas[col++]);
       				pm.getProjeto().getCoordenador().getServidor().getPessoa().setEmail((String) colunas[col++]);
       				pm.getProjeto().getCoordenador().setPessoa(pm.getProjeto().getCoordenador().getServidor().getPessoa());
       			}else {
       				col = col + 4;
       			}
   				pm.getProjeto().setUnidade(new Unidade());
       			pm.setUnidade((Unidade) colunas[col++]);
       			pm.getProjeto().setResumo((String) colunas[col++]);
       			pm.getProjeto().setJustificativa((String) colunas[col++]);
       			pm.getProjeto().setObjetivos((String) colunas[col++]);
       			pm.getProjeto().setResultados((String) colunas[col++]);
       			pm.getProjeto().setMetodologia((String) colunas[col++]);
       			pm.getProjeto().setReferencias((String) colunas[col++]);
       			pm.setAvaliacao((String) colunas[col++]);
       			pm.setProduto((String) colunas[col++]);
       			pm.setProcessoSeletivo((String) colunas[col++]);
       			if(colunas[col] != null){
       				pm.setEditalMonitoria((EditalMonitoria) colunas[col]);
       			}
       			
       			result.add(pm);
		}
		
		if( ValidatorUtil.isAllEmpty(result) )
			return null;
			
		return result.get(0);
		
	}
	
	/**
	 * Retorna todos os projetos que podem solicitar reconsideração ou que já
	 * solicitaram reconsideração
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */	
	@SuppressWarnings("unchecked")
	public Collection<ProjetoEnsino> findByPassiveisReconsideracao(int idServidor) throws DAOException {
		if (idServidor > 0) {
					StringBuffer hql = new StringBuffer();
					hql.append("SELECT pm FROM ProjetoEnsino pm "
							+ "LEFT JOIN pm.editalMonitoria em "
							+ "LEFT JOIN em.edital edital "
							+ "JOIN pm.projeto proj "
							+ "JOIN proj.coordenador coord "
							+ "WHERE pm.ativo = trueValue() " 
							+ "AND proj.ativo = trueValue() "
							+ "AND proj.tipoProjeto = :tipoProjeto "
							+ "AND coord.servidor.id = :idServidor "
							+ "AND proj.situacaoProjeto.id IN (:SITUACOES_PARA_RECONSIDERACAO, :ANALISANDO_RECONSIDERACAO)");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idServidor", idServidor);
			query.setParameterList("SITUACOES_PARA_RECONSIDERACAO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_SOLICITAR_RECONSIDERACAO);
			query.setInteger("ANALISANDO_RECONSIDERACAO", TipoSituacaoProjeto.MON_ANALISANDO_SOLICITACAO_DE_RECONSIDERACAO);
			query.setInteger("tipoProjeto", TipoProjeto.ENSINO);
			return query.list();

		} else {
			return null;
		}

	}
	
}