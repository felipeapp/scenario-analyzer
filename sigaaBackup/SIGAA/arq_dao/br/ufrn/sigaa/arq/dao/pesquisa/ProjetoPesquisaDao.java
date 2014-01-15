/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '09/12/2005'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.SeqAno;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CotaBolsas;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioProjeto;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Dao para consultas de projetos de pesquisa. 
 * 
 * @author Victor Hugo
 * @author Ilueny Santos
 * @author Ricardo Wendell
 *
 */
public class ProjetoPesquisaDao extends GenericSigaaDAO {

	/**
	 * Atributo que limita o resultado da consulta dos projetos de pesquisa
	 */
	public static final int LIMITE_RESULTADOS_CONSULTA = 500;


	/**
	 * Retorna o próximo número na sequência de códigos de projetos de pesquisa.
	 * @return
	 * @throws DAOException
	 */
	public int findNextNumero() throws DAOException {
		return getNextSeq(SeqAno.SEQUENCIA_CODIGO_PROJETO_PESQUISA, 0);
	}

	/**
	 * Retorna todos os centros e unidades acadêmicas especializadas utilizadas na pesquisa.
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findCentrosPesquisa() throws DAOException {
		try {
			@SuppressWarnings("unchecked")
			Collection<Unidade> lista = getSession().createQuery("select s.unidade from SiglaUnidadePesquisa s order by s.unidade.nome").list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna todos os centros e unidades acadêmicas especializadas utilizadas na pesquisa.
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public Collection<SiglaUnidadePesquisa> findCentrosUnidadesPesquisa() throws DAOException {
		try {
			@SuppressWarnings("unchecked")
			Collection<SiglaUnidadePesquisa> lista = getSession().createQuery("select s from SiglaUnidadePesquisa s where s.sigla is not null order by s.unidade.nome").list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca um projeto de pesquisa trazendo apenas alguns dados principais.
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public ProjetoPesquisa findLeve(int idProjeto) throws DAOException {
		try {
			return (ProjetoPesquisa) getSession()
				.createQuery("select p.id as id, p.codigo as codigo, p.projeto.titulo as titulo " +
						" from ProjetoPesquisa p where id = " + idProjeto)
				.setResultTransformer( Transformers.aliasToBean(ProjetoPesquisa.class) )
				.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna uma coleção de projetos onde o servidor informado é
	 * coordenador.
	 *
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public boolean isCoordenadorPesquisa(Servidor servidor)
			throws DAOException {
		try {

			Query q = getSession().createQuery("select p.id from ProjetoPesquisa p where " +
					" p.coordenador.id = " + servidor.getId() + " order by p.id");
			q.setMaxResults(1);
			return q.list().size()>0;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna uma coleção de projetos onde o servidor informado é
	 * coordenador.
	 *
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findByCoordenador(Servidor servidor)
			throws DAOException {
		try {

			Criteria c = getSession().createCriteria(ProjetoPesquisa.class);
			c.add(Expression.eq("coordenador.id", servidor.getId()));
			c.addOrder(Order.desc("codigo.ano"));
			c.addOrder(Order.asc("codigo.numero"));
			return c.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca um projeto a partir de seu código.
	 *
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public ProjetoPesquisa findByCodigo(CodigoProjetoPesquisa codigo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ProjetoPesquisa.class);
			if (codigo.getPrefixo() != null) {
				c.add(Expression.eq("codigo.prefixo", codigo.getPrefixo()));
			}
			c.add(Expression.eq("codigo.numero", codigo.getNumero()));
			c.add(Expression.eq("codigo.ano", codigo.getAno()));
			c.setMaxResults(1);
			return (ProjetoPesquisa) c.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os projetos do docente que estão com situação apta a receberem planos de trabalho.
	 * @param servidor
	 * @param solicitacaoCota
	 * @return
	 * @throws DAOException
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findPassiveisCadastroPlanoTrabalho(
			Servidor servidor, Collection<DocenteExterno> docenteExterno, boolean solicitacaoCota) throws DAOException {
		try {
			
			// Status de projetos passíveis
			List<Integer> status = new ArrayList<Integer>();
			status.add(TipoSituacaoProjeto.EM_ANDAMENTO);
			if(solicitacaoCota) {
				status.add(TipoSituacaoProjeto.SUBMETIDO);
			}
			
			Collection<Integer> idsDocenteExterno = new ArrayList<Integer>();
			if(!ValidatorUtil.isEmpty(docenteExterno)) {
				for (DocenteExterno docExterno : docenteExterno) {
					idsDocenteExterno.add(docExterno.getId());
				}
			}
			
			Criteria c = getSession().createCriteria(ProjetoPesquisa.class);
			if (servidor != null) 
				c.add(Restrictions.eq("coordenador.id", servidor.getId()));
			else
				c.add(Restrictions.in("coordenadorExterno.id", idsDocenteExterno));
			c.createCriteria("projeto").add(Restrictions.in("situacaoProjeto.id", status));
			c.add(Restrictions.ge("codigo.ano", CalendarUtils.getAnoAtual()));
			c.addOrder(Order.desc("codigo.ano"));
			c.addOrder(Order.asc("codigo.numero"));
			return c.list();
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca os projetos do docente que estão com situação apta a receber planos de trabalho para solicitação de cota.
	 * @param servidor
	 * @param solicitacaoCota
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findParaSolicitacaoCota(
			Servidor servidor, Collection<DocenteExterno> docenteExterno, EditalPesquisa edital) throws DAOException {
		try {
			
			// Status de projetos passíveis
			List<Integer> status = new ArrayList<Integer>();
			status.add(TipoSituacaoProjeto.EM_ANDAMENTO);
			status.add(TipoSituacaoProjeto.SUBMETIDO);
			status.add(TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE);
			status.add(TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE);
			status.add(TipoSituacaoProjeto.RENOVADO);
			
			Collection<Integer> idsDocenteExterno = new ArrayList<Integer>();
			if(!ValidatorUtil.isEmpty(docenteExterno)) {
				for (DocenteExterno docExterno : docenteExterno) {
					idsDocenteExterno.add(docExterno.getId());
				}
			}
			
			Criteria c = getSession().createCriteria(ProjetoPesquisa.class);
			if (servidor != null) 
				c.add(Restrictions.eq("coordenador.id", servidor.getId()));
			else
				c.add(Restrictions.in("coordenadorExterno.id", idsDocenteExterno));
			c.createCriteria("projeto")
			.add(Restrictions.in("situacaoProjeto.id", status))
			.add(Restrictions.le("dataInicio", edital.getCota().getInicio()))
			.add(Restrictions.ge("dataFim", edital.getCota().getFim()));
			c.addOrder(Order.desc("codigo.ano"));
			c.addOrder(Order.asc("codigo.numero"));
			return c.list();
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca os projetos do docente que estão com situação apta a receber planos de trabalho para voluntários.
	 * @param servidor
	 * @param solicitacaoCota
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findParaVoluntario(
			Servidor servidor, Collection<DocenteExterno> docenteExterno, CotaBolsas cota) throws DAOException {
		try {
			
			// Status de projetos passíveis
			List<Integer> status = new ArrayList<Integer>();
			status.add(TipoSituacaoProjeto.EM_ANDAMENTO);
			status.add(TipoSituacaoProjeto.SUBMETIDO);
			status.add(TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE);
			status.add(TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE);
			status.add(TipoSituacaoProjeto.RENOVADO);
			
			Collection<Integer> idsDocenteExterno = new ArrayList<Integer>();
			if(!ValidatorUtil.isEmpty(docenteExterno)) {
				for (DocenteExterno docExterno : docenteExterno) {
					idsDocenteExterno.add(docExterno.getId());
				}
			}
			
			Criteria c = getSession().createCriteria(ProjetoPesquisa.class);
			if (servidor != null) 
				c.add(Restrictions.eq("coordenador.id", servidor.getId()));
			else
				c.add(Restrictions.in("coordenadorExterno.id", idsDocenteExterno));
			c.createCriteria("projeto")
			.add(Restrictions.in("situacaoProjeto.id", status))
			.add(Restrictions.le("dataInicio", cota.getInicio()))
			.add(Restrictions.ge("dataFim", cota.getFim()));
			c.addOrder(Order.desc("codigo.ano"));
			c.addOrder(Order.asc("codigo.numero"));
			return c.list();
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca os projetos do docente que estão com situação apta a receber planos de trabalho 
	 * sem vínculo com cota de bolsas da instituição.
	 * @param servidor
	 * @param solicitacaoCota
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findParaSemCota(
			Servidor servidor, Collection<DocenteExterno> docenteExterno) throws DAOException {
		try {

			// Status de projetos passíveis
			List<Integer> status = new ArrayList<Integer>();
			status.add(TipoSituacaoProjeto.EM_ANDAMENTO);
			status.add(TipoSituacaoProjeto.RENOVADO);
			
			Collection<Integer> idsDocenteExterno = new ArrayList<Integer>();
			if(!ValidatorUtil.isEmpty(docenteExterno)) {
				for (DocenteExterno docExterno : docenteExterno) {
					idsDocenteExterno.add(docExterno.getId());
				}
			}
			
			Date hoje = new Date();
			
			Criteria c = getSession().createCriteria(ProjetoPesquisa.class);
			if (servidor != null) 
				c.add(Restrictions.eq("coordenador.id", servidor.getId()));
			else
				c.add(Restrictions.in("coordenadorExterno.id", idsDocenteExterno));
			c.createCriteria("projeto")
				.add(Restrictions.in("situacaoProjeto.id", status))
				.add(Restrictions.le("dataInicio", hoje))
				.add(Restrictions.ge("dataFim", hoje));
			c.addOrder(Order.desc("codigo.ano"));
			c.addOrder(Order.asc("codigo.numero"));
			return c.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os projetos de pesquisa passíveis de renovação de um determinado docente.
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findParaRenovacao(Servidor servidor)
			throws DAOException {
		try {

			Query query = getSession().createQuery(
					"select distinct pp from ProjetoPesquisa pp inner join pp.projeto p inner join p.equipe mem "
							+ " where mem.servidor.id = :idServidor "
							+ " and mem.funcaoMembro.id = :funcao "
							+ " and pp.projeto.dataFim > now() "
							+ " and pp.projeto.situacaoProjeto.id in "
							+ UFRNUtils.gerarStringIn(new int[]{TipoSituacaoProjeto.EM_ANDAMENTO}) );

			query.setInteger("idServidor", servidor.getId());
			query.setInteger("funcao", FuncaoMembro.COORDENADOR);
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna uma coleção de projetos onde o servidor informado é
	 * coordenador e não tem nenhum relatório de projeto.
	 *
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findByCoordenadorSemRelatorioProjeto(
			Servidor servidor) throws DAOException {
		try {

			Query query = getSession().createQuery(
					"select pp from ProjetoPesquisa pp inner join pp.membrosProjeto mem "
							+ " where pp.relatorioProjeto = null "
							+ " and mem.servidor.id=:idservidor "
							+ " and mem.tipoParticipacao =:classificacao ");

			query.setInteger("idservidor", servidor.getId());
			query.setInteger("classificacao", FuncaoMembro.COORDENADOR);
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Método para buscar os projetos de pesquisa de acordo com uma série de
	 * filtros opcionais.
	 *
	 * @param interno
	 * @param codigoProjeto
	 * @param anoProjeto
	 * @param idCoordenador
	 * @param idPesquisador
	 * @param palavraChaveTitulo
	 * @param idUnidade
	 * @param idSubarea
	 * @param idGrupoPesquisa
	 * @param idStatusProjeto
	 * @param relatorioEnviado
	 * @param formatoRelatorio
	 * @param idGestoraAcademica
	 * @param agenciaFinanciadora
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */	
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> filter(Boolean interno,
			CodigoProjetoPesquisa codigoProjeto, Integer anoProjeto, Integer idCoordenador,
			Integer idPesquisador, String palavraChaveTitulo, Integer idGestora,
			Integer idUnidade, String titulo, String objetivos, String linhaPesquisa,
			Integer idSubarea, Integer idGrupoPesquisa,
			Integer idAgenciaFinanciadora, Integer idEdital, Integer idStatusProjeto, Integer categoriaProjeto,
			Boolean relatorioEnviado, boolean formatoRelatorio,
			String nomePesquisador, String nomeUnidade,
			boolean somenteAprovados) throws DAOException,
			LimiteResultadosException {

		try {

			StringBuilder hqlCount = new StringBuilder();
			StringBuilder hqlCorpo = new StringBuilder();
			StringBuilder hqlConsulta = new StringBuilder();

			String projecao = "id, codigo, titulo, objetivos, coordenador, coordenador.pessoa.nome, coordenadorExterno, coordenadorExterno.pessoa.nome, situacaoProjeto, centro.nome, centro.sigla, interno, descricao, dataCadastro";

			hqlCount.append(" SELECT count(distinct pp.id) ");
			hqlConsulta
					.append(" SELECT distinct pp.id as id, pp.codigo as codigo, projeto.titulo as titulo, projeto.objetivos, "
							+ " c as coordenador, p.nome, ce as coordenadorExterno, pe.nome, projeto.situacaoProjeto as situacaoProjeto, pp.centro.nome, pp.centro.sigla, " +
									" projeto.interno as interno, projeto.descricao as descricao, projeto.dataCadastro as dataCadastro");
			hqlCorpo.append(" FROM ProjetoPesquisa pp ");
			hqlCorpo.append(" INNER JOIN pp.projeto as projeto");
			hqlCorpo.append(" LEFT OUTER JOIN projeto.equipe as membros");
			hqlCorpo.append(" LEFT OUTER JOIN pp.coordenador as c");
			hqlCorpo.append(" LEFT OUTER JOIN pp.coordenador.pessoa as p");
			hqlCorpo.append(" LEFT OUTER JOIN pp.coordenadorExterno as ce");
			hqlCorpo.append(" LEFT OUTER JOIN pp.coordenadorExterno.pessoa as pe");
			hqlCorpo
					.append(" LEFT OUTER JOIN pp.financiamentosProjetoPesq as financiamentos");
			hqlCorpo.append(" WHERE 1 = 1 ");

			// Filtros para a busca
			if (interno != null) {
				hqlCorpo.append(" AND projeto.interno = :interno");
			}
			if (codigoProjeto != null) {
				hqlCorpo.append(" AND pp.codigo.prefixo = :codigoPrefixo");
				hqlCorpo.append(" AND pp.codigo.numero = " + codigoProjeto.getNumero());
				hqlCorpo.append(" AND pp.codigo.ano = " + codigoProjeto.getAno());
			}
			if (anoProjeto != null) {
				hqlCorpo.append(" AND pp.codigo.ano = :anoProjeto");
			}
			if (idCoordenador != null) {
				hqlCorpo.append(" AND pp.coordenador.id = :idCoordenador");
			}
			if (idPesquisador != null) {
				hqlCorpo.append(" AND membros.servidor.id = :idPesquisador");
			}
			if (palavraChaveTitulo != null) {
				hqlCorpo.append(" AND (pp.nome like :palavraChaveTitulo OR pp.palavrasChave like :palavraChaveTitulo)");
			}
			if (idGestora != null) {
				hqlCorpo.append(" AND pp.centro.id = :idGestora");
			}
			if (idUnidade != null) {
				hqlCorpo.append(" AND projeto.unidade.id = :idUnidade");
			}
			if (titulo != null) {
				hqlCorpo.append(" AND upper(projeto.titulo) like upper(:titulo)");
			}
			if (objetivos != null) {
				hqlCorpo.append(" AND upper(projeto.objetivos) like upper(:objetivos)");
			}
			if (linhaPesquisa != null) {
				hqlCorpo.append(" AND upper(pp.linhaPesquisa.nome) like upper(:linhaPesquisa)");
			}
			if (idSubarea != null) {
				hqlCorpo.append(" AND pp.areaConhecimentoCnpq.id = :idSubarea");
			}
			if (idGrupoPesquisa != null) {
				hqlCorpo.append(" AND pp.linhaPesquisa.grupoPesquisa.id = :idGrupoPesquisa");
			}
			if (idAgenciaFinanciadora != null) {
				hqlCorpo.append(" AND financiamentos.entidadeFinanciadora.id = :idAgenciaFinanciadora");
			}
			if (idEdital != null) {
				hqlCorpo.append(" AND pp.edital.id = :idEdital");
			}
			if (idStatusProjeto != null) {
				hqlCorpo.append(" AND projeto.situacaoProjeto.id = :idSituacao");
			}
			if (categoriaProjeto != null) {
				hqlCorpo.append(" AND pp.categoria = :categoriaProjeto");
			}
			if (relatorioEnviado != null) {
				hqlCorpo.append(" AND size(pp.relatoriosProjeto)"+ (relatorioEnviado?" > ":" = ") +"0");
			}
			if (nomePesquisador != null) {
				hqlCorpo.append(" AND upper(membros.servidor.pessoa.nome) like upper(:nomePesquisador)");
			}
			if (nomeUnidade != null) {
				hqlCorpo.append(" AND upper(projeto.unidade.nome) like upper(:nomeUnidade)");
			}
			if (somenteAprovados) {
				hqlCorpo.append(" AND projeto.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(new int[] {
						TipoSituacaoProjeto.EM_ANDAMENTO, TipoSituacaoProjeto.FINALIZADO, 
						TipoSituacaoProjeto.RENOVADO, TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO
				}) );
			}

			// Contagem
			hqlCount.append(hqlCorpo.toString());
			Query queryCount = getSession().createQuery(hqlCount.toString());

			// Consulta dos projetos
			hqlCorpo.append(" ORDER BY pp.codigo.ano desc, pp.centro.nome, p.nome, pp.codigo.numero");
			hqlConsulta.append(hqlCorpo.toString());
			Query queryConsulta = getSession().createQuery(
					hqlConsulta.toString());

			// Populando os valores dos filtros
			if (interno != null) {
				queryCount.setBoolean("interno", interno);
				queryConsulta.setBoolean("interno", interno);
			}
			if (codigoProjeto != null) {
				queryCount.setString("codigoPrefixo", codigoProjeto.getPrefixo());
				queryConsulta.setString("codigoPrefixo", codigoProjeto.getPrefixo());
			}
			if (anoProjeto != null) {
				queryCount.setInteger("anoProjeto", anoProjeto);
				queryConsulta.setInteger("anoProjeto", anoProjeto);
			}
			if (idCoordenador != null) {
				queryCount.setInteger("idCoordenador", idCoordenador);
				queryConsulta.setInteger("idCoordenador", idCoordenador);
			}
			if (idPesquisador != null) {
				queryCount.setInteger("idPesquisador", idPesquisador);
				queryConsulta.setInteger("idPesquisador", idPesquisador);
			}
			if (palavraChaveTitulo != null) {
				queryCount.setString("palavraChaveTitulo", "%"
						+ palavraChaveTitulo.trim().toUpperCase() + "%");
				queryConsulta.setString("palavraChaveTitulo", "%"
						+ palavraChaveTitulo.trim().toUpperCase() + "%");
			}
			if (idGestora != null) {
				queryCount.setInteger("idGestora", idGestora);
				queryConsulta.setInteger("idGestora", idGestora);
			}
			if (idUnidade != null) {
				queryCount.setInteger("idUnidade", idUnidade);
				queryConsulta.setInteger("idUnidade", idUnidade);
			}
			if (titulo != null) {
				queryCount.setString("titulo", "%" + titulo + "%");
				queryConsulta.setString("titulo", "%" + titulo + "%");
			}
			if (objetivos != null) {
				queryCount.setString("objetivos", "%" + objetivos + "%");
				queryConsulta.setString("objetivos", "%" + objetivos + "%");
			}
			if (linhaPesquisa != null) {
				queryCount.setString("linhaPesquisa", "%" + linhaPesquisa + "%");
				queryConsulta.setString("linhaPesquisa", "%" + linhaPesquisa + "%");
			}
			if (idSubarea != null) {
				queryCount.setInteger("idSubarea", idSubarea);
				queryConsulta.setInteger("idSubarea", idSubarea);
			}
			if (idGrupoPesquisa != null) {
				queryCount.setInteger("idGrupoPesquisa", idGrupoPesquisa);
				queryConsulta.setInteger("idGrupoPesquisa", idGrupoPesquisa);
			}
			if (idAgenciaFinanciadora != null) {
				queryCount.setInteger("idAgenciaFinanciadora",
						idAgenciaFinanciadora);
				queryConsulta.setInteger("idAgenciaFinanciadora",
						idAgenciaFinanciadora);
			}
			if (idEdital != null) {
				queryCount.setInteger("idEdital", idEdital);
				queryConsulta.setInteger("idEdital", idEdital);
			}
			if (idStatusProjeto != null) {
				queryCount.setInteger("idSituacao", idStatusProjeto);
				queryConsulta.setInteger("idSituacao", idStatusProjeto);
			}
			if (categoriaProjeto != null) {
				queryCount.setInteger("categoriaProjeto", categoriaProjeto);
				queryConsulta.setInteger("categoriaProjeto", categoriaProjeto);
			}
			if (nomePesquisador != null) {
				queryCount.setString("nomePesquisador", "%" + nomePesquisador.trim().toUpperCase() + "%");
				queryConsulta.setString("nomePesquisador","%" +  nomePesquisador.trim().toUpperCase() + "%");
			}
			if (nomeUnidade != null) {
				queryCount.setString("nomeUnidade", "%" + nomeUnidade.trim().toUpperCase() + "%");
				queryConsulta.setString("nomeUnidade", "%" + nomeUnidade.trim().toUpperCase() + "%");
			}

			if (!formatoRelatorio) {
				queryCount.setMaxResults(LIMITE_RESULTADOS_CONSULTA);
				queryConsulta.setMaxResults(LIMITE_RESULTADOS_CONSULTA);
			}

			if (!formatoRelatorio) {
				Long total = (Long) queryCount.uniqueResult();
				if (total > LIMITE_RESULTADOS_CONSULTA) {
					throw new LimiteResultadosException("A consulta retornou "
							+ total
							+ " resultados. Por favor, restrinja mais a busca.");
				}
			}
			return HibernateUtils.parseTo(queryConsulta.list(), projecao, ProjetoPesquisa.class);

		} catch (LimiteResultadosException e) {
			throw e;
		} catch (Exception ex) {
			throw new DAOException(ex.getMessage(), ex);
		}
	}

	/**
	 * Retorna uma coleção de projetos de pesquisa que já foram distribuídos
	 * para consultores internos segundo o tipo de distribuição informado.
	 *
	 * Constates em AvaliacaoProjeto int DISTRIBUICAO_AUTOMATICA = 1; int
	 * DISTRIBUICAO_MANUAL = 2;
	 *
	 * @param tipoDistribuicao
	 * @return coleção de ProjetosPesquisa
	 * @throws DAOException
	 *
	 * @see {@link AvaliacaoProjeto} AvaliacaoProjeto;
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findByTipoDistribuicao(
			int tipoDistribuicao) throws DAOException {
		try {
			Query query = getSession()
					.createQuery(
							"select pp from ProjetoPesquisa pp inner join pp.avaliacoesProjeto ap where ap.tipoDistribuicao=:tipoDistribuicao and  pp.projeto.interno=trueValue()");
			query.setInteger("tipoDistribuicao", tipoDistribuicao);
			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Seleciona todos os projetos de pesquisa que ainda não foram distribuídos.
	 * 
	 * @param idEdital 
	 * @return lista de projetos de pesquisa
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findByNaoDistribuidos(Integer idEdital)
			throws DAOException {
		try {

			StringBuffer hql = new StringBuffer();

			hql.append( " SELECT DISTINCT pp.id AS id, pp.projeto.titulo AS titulo, pp.areaConhecimentoCnpq AS areaConhecimentoCnpq");
			hql.append( " FROM ProjetoPesquisa pp " );
			hql.append( " WHERE ( pp.projeto.interno = trueValue() ) " );
			hql.append( " AND ( pp.projeto.situacaoProjeto.id = " + TipoSituacaoProjeto.SUBMETIDO + " ) " );
			if(idEdital != null)
				hql.append( " AND ( pp.edital.id = " + idEdital + " ) " );

			Query query = getSession().createQuery( hql.toString() );

			return query.setResultTransformer( Transformers.aliasToBean(ProjetoPesquisa.class) ).list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca projetos ainda não distribuídos OU com número de avaliações insuficiente.
	 *
	 * @param numeroAvaliacoes - O número de avaliações considerado insuficiente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findByNaoDistribuidosOuAvaliacaoInsuficiente(Integer idEdital) throws DAOException {

		try {

			StringBuilder hql = new StringBuilder();
			hql.append("SELECT p.id AS id, p.projeto.id AS idProjeto, p.codigo AS codigo, centro.sigla as siglaCentro, p.areaConhecimentoCnpq.codigo as codigoArea, ");
			hql.append(" count(avaliacoes) AS qtdAvaliacoesSubmetidas,  ");

			hql.append(" sum(CASE WHEN avaliacoes.dataAvaliacao IS NOT null AND avaliacoes.justificativa IS null ");
			hql.append(" THEN 1 else 0 END) AS qtdAvaliacoesRealizadas, ");

			hql.append(" sum(CASE WHEN avaliacoes.dataAvaliacao IS null OR avaliacoes.justificativa IS NOT null ");
			hql.append(" THEN 1 else 0 END) AS qtdAvaliacoesNegadas, ");

			hql.append(" sum( avaliacoes.media )/coalesce(sum(CASE WHEN avaliacoes.dataAvaliacao IS NOT null AND avaliacoes.justificativa IS null ");
			hql.append(" THEN 1 else null END), 1) as media");

			hql.append(" FROM ProjetoPesquisa p JOIN p.centro AS centro");
			hql.append(" LEFT JOIN p.avaliacoesProjeto as avaliacoes ");
			hql.append(" LEFT JOIN avaliacoes.notasItens as notas" );
			hql.append(" WHERE p.projeto.interno = trueValue()");
			if(idEdital != null)
				hql.append( " AND ( p.edital.id = " + idEdital + " ) " );
			hql.append(" AND (p.projeto.situacaoProjeto = :distAutomatica");
			hql.append(" OR p.projeto.situacaoProjeto.id = :submetido ");
			hql.append(" OR p.projeto.situacaoProjeto.id = :distManual) ");


			hql.append(" GROUP BY p.id, p.codigo, centro.sigla, p.areaConhecimentoCnpq.codigo, avaliacoes.media, notas.nota, p.projeto.id ");


			hql.append(" HAVING sum(CASE WHEN avaliacoes.situacao = "+ AvaliacaoProjeto.REALIZADA + "THEN 1 else 0 END) = 0");
			hql.append(" OR ( sum(CASE WHEN avaliacoes.situacao = "+ AvaliacaoProjeto.REALIZADA + "THEN 1 else 0 END) = 1");
			hql.append(" AND avaliacoes.media < 5 )");
			hql.append(" OR notas.nota < 3 ");


			hql.append(" ORDER BY p.centro.sigla, p.codigo.numero");

			Query query = getSession().createQuery(hql.toString());

			query.setInteger("distAutomatica",
					TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE);
			query.setInteger("submetido",
					TipoSituacaoProjeto.SUBMETIDO);
			query.setInteger("distManual",
					TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE);
			return query.setResultTransformer(
					Transformers.aliasToBean(ProjetoPesquisa.class)).list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna a coleção de projetos que foram avaliados e estão com a
	 * situação DISTRIBUIDO_AUTOMATICAMENTE ou DISTRIBUIDO_MANUALMENTE
	 * @param minimoAvaliacoes
	 * @param unidadeAcademica
	 *
	 * @return
	 * @throws DAOException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findParaAnalise(Unidade unidadeAcademica, Integer minimoAvaliacoes, EditalPesquisa edital) throws DAOException {

		try {
			Query query = builderQueryParaAnalise(null, unidadeAcademica, minimoAvaliacoes, edital);
			return query.setResultTransformer(
					Transformers.aliasToBean(ProjetoPesquisa.class)).list();
		} catch (DAOException e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna a coleção de projetos que foram avaliados e estão com a
	 * situação DISTRIBUIDO_AUTOMATICAMENTE ou DISTRIBUIDO_MANUALMENTE.
	 *
	 * @return
	 * @throws DAOException
	 */
	public ProjetoPesquisa findParaAnaliseById(Integer idProjeto)
			throws DAOException {

		try {
			Query query = builderQueryParaAnalise(idProjeto, null, null, null);

			return (ProjetoPesquisa) query.setResultTransformer(
					Transformers.aliasToBean(ProjetoPesquisa.class))
					.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Monta a query para buscar os projetos para análise.
	 *
	 * @param idProjeto
	 * @param minimoAvaliacoes
	 * @param unidadeAcademica
	 * @return
	 * @throws DAOException
	 */
	private Query builderQueryParaAnalise(Integer idProjeto, Unidade unidadeAcademica, Integer minimoAvaliacoes, EditalPesquisa edital)
			throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append("SELECT p.id AS id, p.codigo AS codigo, p.centro.sigla as siglaCentro, ");
		hql.append(" count(avaliacoes) AS qtdAvaliacoesSubmetidas,  ");

		hql.append(" sum(CASE WHEN avaliacoes.dataAvaliacao IS NOT null AND avaliacoes.justificativa IS null ");
		hql.append(" THEN 1 else 0 END) AS qtdAvaliacoesRealizadas, ");

		hql.append(" sum(CASE WHEN avaliacoes.dataAvaliacao IS null OR avaliacoes.justificativa IS NOT null ");
		hql.append(" THEN 1 else 0 END) AS qtdAvaliacoesNegadas, ");

		hql.append(" sum( avaliacoes.media )/coalesce(sum(CASE WHEN avaliacoes.dataAvaliacao IS NOT null AND avaliacoes.justificativa IS null ");
		hql.append(" THEN 1 else null END), 1) as media");

		hql.append(" FROM ProjetoPesquisa p ");
		hql.append(" JOIN p.avaliacoesProjeto as avaliacoes ");
		hql.append(" WHERE ");
		hql.append(" (p.projeto.situacaoProjeto = :distAutomatica");
		hql.append(" OR p.projeto.situacaoProjeto.id = :distManual) ");

		if (idProjeto != null) {
			hql.append(" AND p.id = " + idProjeto);
		}
		if (unidadeAcademica != null) {
			hql.append(" AND p.centro.id = " + unidadeAcademica.getId());
		}
		if (edital != null) {
			hql.append(" AND p.edital.id = " + edital.getId());
		}

		hql.append(" GROUP BY p.id, p.codigo, p.centro.sigla ");

		if (minimoAvaliacoes != null) {
			hql.append(" HAVING sum(CASE WHEN avaliacoes.dataAvaliacao IS NOT null AND avaliacoes.justificativa IS null ");
			hql.append(" THEN 1 else 0 END) >= "  + minimoAvaliacoes);
		}

		hql.append(" ORDER BY p.centro.sigla, p.codigo.numero");

		Query query = getSession().createQuery(hql.toString());

		query.setInteger("distAutomatica",
				TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE);
		query.setInteger("distManual",
				TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE);

		return query;
	}


	/**
	 * Sobrecarga do método sem considerar os consultores especiais.
	 * 
	 * @param situacaoProjeto
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 */
	public Collection<AreaConhecimentoCnpq> findByResumoGrandeArea(int[] situacaoProjeto, Integer idEdital) throws DAOException {
		return findByResumoGrandeArea(situacaoProjeto, idEdital, false);
	}

	/**
	 * Lista todas as áreas de conhecimento do CNPq que tem projetos de
	 * pesquisa com seus respectivos totais de projetos e totais de consultores dado uma situação de projeto por parâmetro.
	 * A situação SUBMETIDO @see TipoSituacaoProjeto.SUBMETIDO deve ser utilizada para distribuição de projetos automática.
	 * A situação AVALIACAO_INSUFICIENTE @see TipoSituacaoProjeto.AVALIACAO_INSUFICIENTE deve ser usado na distribuição de projetos manual.
	 * @param situacaoProjeto A situação do projeto a ser consultada.
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 */
	public Collection<AreaConhecimentoCnpq> findByResumoGrandeArea(int[] situacaoProjeto, Integer idEdital, boolean consultoresEspeciais) throws DAOException {
		try {

			StringBuffer hql = new StringBuffer();
			hql.append( " SELECT distinct area.id AS id, area.codigo AS codigo, area.nome AS nome, " +
					" area.grandeArea.nome AS nomeGrandeArea, " +
					" count(distinct pp.id) AS qtdProjetos, " );

			if(!consultoresEspeciais) {
				hql.append( " (select count(distinct id_consultor) from Consultor c where c.areaConhecimentoCnpq.codigo = area.codigo ) AS qtdConsultores ");
			} else {
				// Se a distribuição for pra consultores especiais
				hql.append( " (select count(distinct c.id) from Consultor c, ConsultoriaEspecial ce" +
							"  where c.areaConhecimentoCnpq.codigo = area.codigo" +
							"  and c.id = ce.consultor.id" +
							"  and ce.dataInicio <= :hoje" +
							"  and ce.dataFim >= :hoje ) AS qtdConsultores ");
			}
			hql.append(", sum( avaliacoes.media )/coalesce(sum(CASE WHEN avaliacoes.dataAvaliacao IS NOT null AND avaliacoes.justificativa IS null ");
			hql.append(" THEN 1 else null END), 1) as media, ");
			hql.append(" notas.nota ");


			hql.append( " FROM ProjetoPesquisa pp" );
			hql.append( " LEFT JOIN pp.avaliacoesProjeto as avaliacoes" );
			hql.append( " LEFT JOIN avaliacoes.notasItens as notas" );
			hql.append( " , AreaConhecimentoCnpq area" );
			hql.append( " WHERE area.id = pp.areaConhecimentoCnpq.codigo " );
			hql.append( " AND pp.projeto.interno = trueValue() " );
			if(idEdital != null)
				hql.append( " AND pp.edital.id = " + idEdital );
			hql.append( " AND pp.projeto.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(situacaoProjeto) );
			hql.append( " GROUP BY area.id, area.nome, area.codigo, area.grandeArea.nome, area.grandeArea.nome " );
			hql.append( ", avaliacoes.media, notas.nota ");
			hql.append(" HAVING sum(CASE WHEN avaliacoes.situacao = "+ AvaliacaoProjeto.REALIZADA + "THEN 1 else 0 END) = 0");
			hql.append(" OR ( sum(CASE WHEN avaliacoes.situacao = "+ AvaliacaoProjeto.REALIZADA + "THEN 1 else 0 END) = 1");
			hql.append(" AND avaliacoes.media < 5 )");
			hql.append(" OR notas.nota < 3");
			hql.append( " ORDER BY area.grandeArea.nome, area.nome  " );

			Query query = getSession().createQuery( hql.toString() );
			if(consultoresEspeciais)
				query.setDate("hoje", new Date());

			Collection<AreaConhecimentoCnpq> areas = new ArrayList<AreaConhecimentoCnpq>();
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> resultado = query.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP ).list();
			for ( Map<String, Object> mapa : resultado ) {
				AreaConhecimentoCnpq area = new AreaConhecimentoCnpq();
				area.setId( (Integer) mapa.get("id") );
				area.setNome( (String) mapa.get("nome") );
				area.setCodigo((String) mapa.get("codigo"));
				area.setQtdProjetos( (Long) mapa.get("qtdProjetos") );
				area.setQtdConsultores( (Long) mapa.get("qtdConsultores") );

				AreaConhecimentoCnpq grandeArea = new AreaConhecimentoCnpq();
				grandeArea.setNome( (String) mapa.get("nomeGrandeArea") );
				area.setGrandeArea(grandeArea);

				areas.add(area);
			}

			return areas;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Retorna todos os projetos de pesquisa externos que não foram avaliados.
	 * (situação = SUBMETIDO)
	 *
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findExternosSubmetidos()
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ProjetoPesquisa.class);
			c.add(Expression.eq("projeto.interno", false));
			c.add(Expression.eq("projeto.situacaoProjeto", new TipoSituacaoProjeto(
					TipoSituacaoProjeto.SUBMETIDO)));
			c.addOrder(Order.asc("codigo"));
			c.setMaxResults(LIMITE_RESULTADOS_CONSULTA);
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os projetos a partir do centro acadêmico ao qual estão vinculados.
	 * 
	 * @param centro
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findByCentroAcademico(Unidade centro)
			throws DAOException {
		try {
			String hql = "from ProjetoPesquisa p where p.projeto.unidade.gestoraAcademica.id = :idGestora order by p.codigo.prefixo, p.codigo.numero ";
			Query query = getSession().createQuery(hql);
			query.setInteger("idGestora", centro.getId());

			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os projetos pertencentes a uma unidade ou de acordo com o tipo de unidade.
	 * 
	 * @return Coleção de ProjetoEnsino do centro informado
	 * @throws DAOException
	 * 
	 */
	public Collection<ProjetoPesquisa> findByUnidadeProjecEquipe(Unidade unidade, Integer tipoUnidadeAcademica)	throws DAOException {
		try {
		
			StringBuilder hql = new StringBuilder();
			
			hql.append(" select p.codigo.ano, p.codigo.numero, p.projeto.unidade.id, p.centro.id, pe2.nome, p.projeto.titulo, p.id ");
			hql.append(" from ProjetoPesquisa p  inner join p.projeto.unidade u left join p.coordenador c join c.pessoa pe");
			hql.append(" join p.coordenador co join co.pessoa pe2 ");
			hql.append(" where u.id = :idUnidade");
			hql.append(" and p.projeto.situacaoProjeto.id in " + 
					UFRNUtils.gerarStringIn(new int[] {
					TipoSituacaoProjeto.EM_ANDAMENTO, 
					TipoSituacaoProjeto.FINALIZADO,
					TipoSituacaoProjeto.RENOVADO
			}) );
			if(tipoUnidadeAcademica != null)
				hql.append("  AND u.tipoAcademica = :unidadeAcademica");
			hql.append(" order by pe.nome,p.codigo.ano desc,p.codigo.numero ");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idUnidade", unidade.getId());
			
			if(tipoUnidadeAcademica != null)
				query.setInteger("unidadeAcademica", tipoUnidadeAcademica);
			@SuppressWarnings("unchecked")
			List<Object> lista = query.list();
			Iterator<Object> it = lista.iterator();
			Set<ProjetoPesquisa> result = new LinkedHashSet<ProjetoPesquisa>();
			
			Integer codNumeroAnterior = 0;
			while(it.hasNext()){
				
				Object[] colunas = (Object[]) it.next();
				ProjetoPesquisa pp = new ProjetoPesquisa();
				
				if(!codNumeroAnterior.equals(colunas[1])){
					pp.setId((Integer) colunas[6]);
					pp.setCodigo(new CodigoProjetoPesquisa());
	
					pp.getCodigo().setAno((Integer) colunas[0]);
					pp.getCodigo().setNumero((Integer) colunas[1]);
					
					pp.setUnidade(new Unidade());
					pp.getUnidade().setId((Integer) colunas[2]);
					
					pp.setCentro(new Unidade());
					pp.getCentro().setId((Integer) colunas[3]);
					
					pp.setCoordenador(new Servidor());
					pp.getCoordenador().setPessoa(new Pessoa());
					pp.getCoordenador().getPessoa().setNome((String) colunas[4]);
					pp.setTitulo((String) colunas[5]);
							
					result.add(pp);
				}	
				
				codNumeroAnterior = (Integer) colunas[1];
				
			}
			return result;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os projetos pela unidade à qual estão vinculados.
	 * 
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findByUnidade(Unidade unidade)
			throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ProjetoPesquisa.class);
			c.add(Expression.eq("projeto.unidade", unidade));
			c.addOrder(Order.asc("codigo"));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca os projetos de acordo com a classificação da entidade financiadora do mesmo.
	 * 
	 * @param idClassificacaoFinanciadora
	 * @param ano
	 * @param idEntidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findByClassificacaoFinanciamento(
			int idClassificacaoFinanciadora, Integer ano, int idEntidade) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql
					.append(" from ProjetoPesquisa p join fetch p.financiamentosProjetoPesq as financiamentos");
			hql
					.append(" where financiamentos.entidadeFinanciadora.id = :idEntidade ");
			if(idClassificacaoFinanciadora > 0){
				hql.append(" and financiamentos.entidadeFinanciadora.classificacaoFinanciadora.id = :idClassificacao");
			}

			if (ano != null) {
				hql.append(" and p.codigo.ano = :anoProjeto");
			}
			hql.append(" order by p.centro.nome, p.coordenador.pessoa.nome, p.codigo.prefixo, p.codigo.numero");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idEntidade", idEntidade);
			if(idClassificacaoFinanciadora > 0)
				query.setInteger("idClassificacao", idClassificacaoFinanciadora);

			if (ano != null) {
				query.setInteger("anoProjeto", ano);
			}

			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os projetos de pesquisa que uma pessoa participa como membro.
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	
	public Collection<ProjetoPesquisa> findByMembro(Pessoa pessoa) throws DAOException {
		try {
			String hql = "select distinct p.id, p.codigo.prefixo, p.codigo.numero, p.codigo.ano, p.projeto.id, " +
					" pessoaCoordenadorInterno.id, pessoaCoordenadorInterno.nome, pessoaCoordenadorExterno.id, pessoaCoordenadorExterno.nome, proj.titulo, proj.interno, situacaoProjeto.id, situacaoProjeto.descricao, p.areaConhecimentoCnpq.nome " +
					" from ProjetoPesquisa p join p.projeto proj left join proj.equipe as membro left join proj.situacaoProjeto as situacaoProjeto " +
					" left join p.coordenador as coord left join coord.pessoa as pessoaCoordenadorInterno " +
					" left join p.coordenadorExterno as coordExt left join coordExt.pessoa as pessoaCoordenadorExterno " +
					" where membro.pessoa.id = :idPessoa and proj.situacaoProjeto.id not in " + 
					UFRNUtils.gerarStringIn(new int[]{TipoSituacaoProjeto.EXCLUIDO, TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO}) +
					" order by p.codigo.ano desc, p.codigo.numero asc";

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idPessoa", pessoa.getId());
			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();
			Collection<ProjetoPesquisa> result = new ArrayList<ProjetoPesquisa>();
			for(Object[] colunas: lista){
				int col = 0;
				ProjetoPesquisa p = new ProjetoPesquisa((Integer) colunas[col++]);
				p.setCodigo(new CodigoProjetoPesquisa());
				p.getCodigo().setPrefixo((String) colunas[col++]);
				p.getCodigo().setNumero((Integer) colunas[col++]);
				p.getCodigo().setAno((Integer) colunas[col++]);
				
				p.getProjeto().setId((Integer) colunas[col++]);
				Integer idPessoaCoordenadorInterno = (Integer) colunas[col++];
				String nomeCoordenadorInterno = (String) colunas[col++];
				Integer idPessoaCoordenadorExterno = (Integer) colunas[col++];
				String nomeCoordenadorExterno = (String) colunas[col++];
				if(nomeCoordenadorInterno != null) {
					Servidor coordenador = new Servidor();
					coordenador.getPessoa().setId(idPessoaCoordenadorInterno);
					coordenador.getPessoa().setNome(nomeCoordenadorInterno);
					p.setCoordenador(coordenador);
				} else if(nomeCoordenadorExterno != null) {
					DocenteExterno coordenadorExterno = new DocenteExterno();
					coordenadorExterno.getPessoa().setId(idPessoaCoordenadorExterno);
					coordenadorExterno.getPessoa().setNome(nomeCoordenadorExterno);
					p.setCoordenadorExterno(coordenadorExterno);
				}
				
				p.getProjeto().setTitulo((String) colunas[col++]);
				p.getProjeto().setInterno((Boolean) colunas[col++]);
				p.getProjeto().getSituacaoProjeto().setId((Integer) colunas[col++]);
				p.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[col++]);
				p.getProjeto().getAreaConhecimentoCnpq().setNome((String) colunas[col++]);
				result.add(p);
			}
			
			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca apenas os projetos ativos que uma pessoa participa como membro.
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findAtivosByMembro(Pessoa pessoa)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select distinct p from ProjetoPesquisa p join fetch p.membrosProjeto as membro");
			hql.append(" where membro.pessoa.id = :idPessoa ");
			hql.append(" and p.projeto.situacaoProjeto.id = " + TipoSituacaoProjeto.EM_ANDAMENTO );
			hql.append(" order by p.codigo.ano desc, p.codigo.numero asc");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idPessoa", pessoa.getId());

			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os projetos que foram gravados por um usuário no sistema mas ainda não foram submetidos.
	 * 
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findGravadosByUsuario(Pessoa pessoa)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" select distinct p from ProjetoPesquisa p ");
			hql.append(" join p.coordenador as coord ");
			hql.append(" join coord.pessoa as pes ");
			hql.append(" where pes.id = :idPessoa ");
			hql.append(" and p.projeto.situacaoProjeto = " + TipoSituacaoProjeto.GRAVADO);
			hql.append(" order by p.codigo.ano, p.codigo.numero asc");

			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idPessoa", pessoa.getId());

			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna a quantidade de projetos com um determinado status submetidos para um
	 * determinado edital.
	 * 
	 * @param status
	 * @param idEdital
	 * @param interno
	 * @return
	 * @throws DAOException
	 */
	public long getTotalByStatus(int status, int idEdital, Boolean interno)
			throws DAOException {

		String sql = "select count(id) from ProjetoPesquisa pp"
				+ " where pp.edital.id = :edital ";

		if (status != -1) {
			sql += " and  pp.projeto.situacaoProjeto.id = :status  ";
		}

		if (interno != null) {
			sql += " and pp.projeto.interno = " + interno;
		}

		Query q = getSession().createQuery(sql);
		if (status != -1) {
			q.setInteger("status", status);
		}
		q.setInteger("edital", idEdital);

		return (Long) q.uniqueResult();

	}

	/**
	 * Retorna um relatório sintético do status do projeto de pesquisa.
	 *
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	public ArrayList<HashMap<String, Comparable<?>>> findBySumarioEdital(int idEdital) throws DAOException,
			SQLException {

		Connection con = Database.getInstance().getSigaaConnection();
		Statement st = con.createStatement();
		ResultSet rs = st
				.executeQuery("(select count(*) as total, p.interno, tsp.descricao from " +
							" pesquisa.projeto_pesquisa pp, projetos.projeto p, projetos.tipo_situacao_projeto tsp " +
							" where id_edital_pesquisa = " + idEdital +
							" and pp.id_projeto = p.id_projeto and p.id_tipo_situacao_projeto = tsp.id_tipo_situacao_projeto "  +
							" group by tsp.descricao, p.interno order by p.interno, tsp.descricao) " +
							" union " +
							" (select count(*) as total, p.interno, tsp.descricao from " +
							" pesquisa.projeto_pesquisa pp, projetos.projeto p, projetos.tipo_situacao_projeto tsp " +
							" where p.interno = falseValue() and cod_ano =  " + CalendarUtils.getAnoAtual() +
							" and pp.id_projeto = p.id_projeto and p.id_tipo_situacao_projeto = tsp.id_tipo_situacao_projeto " +
							" group by tsp.descricao, p.interno order by p.interno, tsp.descricao) " +
							" order by interno, descricao");

		ArrayList<HashMap<String, Comparable<?>>> result = new ArrayList<HashMap<String, Comparable<?>>>();
		while (rs.next()) {

			HashMap<String, Comparable<?>> linha = new HashMap<String, Comparable<?>>();
			linha.put("total", rs.getInt("total"));
			linha
					.put("tipo", rs.getBoolean("interno") ? "Interno"
							: "Externo ");
			linha.put("situacao", rs.getString("descricao"));

			result.add(linha);
		}

		return result;

	}

	/**
	 * Retorna o sumário de solicitação de cotas por edital.
	 * @param agruparPorDepartamento
	 * @param unidade
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findBySumarioCotaEdital(int idCota, Unidade centro, boolean agruparPorDepartamento) throws DAOException {

		return getJdbcTemplate().queryForList("select p.titulo, un.sigla, un.id_unidade, serv.siape, pes.nome, " +
							" pp.cod_prefixo as prefixo, pp.cod_numero as numero, pp.cod_ano as ano, " +
							" dep.nome as departamento, dep.sigla as siglaDepartamento, " +
							" count(pt.id_plano_trabalho) as cotas " +
							" from pesquisa.plano_trabalho pt, pesquisa.projeto_pesquisa pp, rh.servidor serv, " +
							" comum.pessoa pes, projetos.projeto p, comum.unidade un, comum.unidade dep " +
							" where "  +
						    " pp.id_projeto = p.id_projeto " +
							" and pt.id_projeto_pesquisa = pp.id_projeto_pesquisa "  +
							" and pt.id_orientador = serv.id_servidor and serv.id_pessoa = pes.id_pessoa " +
							" and dep.id_unidade = serv.id_unidade " +
							" and dep.id_gestora = un.id_unidade " +
							( centro.getId() > 0 ? "and pp.id_centro = " + centro.getId() : " ") +
							" and pt.id_cota_bolsas = " + idCota +
							" and (pt.tipo_bolsa in " +
							"		( select c.id_tipo_bolsa_pesquisa from pesquisa.cotas c" +
							"			inner join pesquisa.edital_pesquisa e on c.id_edital_pesquisa=e.id_edital" +
							"			inner join pesquisa.cota_bolsas cb on e.id_cota=cb.id_cota_bolsas" +
							"		  where cb.id_cota_bolsas="+ idCota +")"+
							"   or pt.tipo_bolsa = " + TipoBolsaPesquisa.PROPESQ +
							"   or pt.tipo_bolsa = " + TipoBolsaPesquisa.PIBIC +
							"   or pt.tipo_bolsa = " + TipoBolsaPesquisa.A_DEFINIR + ")" +
							" group by p.id_projeto, id_orientador, serv.siape, pes.nome, p.titulo, un.sigla, un.id_unidade, " +
							" pp.cod_prefixo, pp.cod_numero, pp.cod_ano, dep.nome, dep.sigla " +
							" order by un.sigla, " +
							(agruparPorDepartamento ? "dep.nome," : "") +
							" pes.nome, p.titulo");
	}

	/**
	 * Retorna a quantidade de projetos com status submetido (ainda não distribuídos)
	 * @param idEdital 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public long findQtdProjetosNaoDistribuidos(Integer idEdital) throws HibernateException, DAOException{

		StringBuffer hql = new StringBuffer();

		hql.append( " SELECT count (DISTINCT pp.id) ");
		hql.append( " FROM ProjetoPesquisa pp " );
		hql.append( " WHERE pp.projeto.interno = trueValue() " );
		hql.append( " AND pp.projeto.situacaoProjeto.id = " + TipoSituacaoProjeto.SUBMETIDO );
		if(idEdital != null)
			hql.append( " AND pp.edital.id = " + idEdital );


		Query query = getSession().createQuery( hql.toString() );

		return (Long) query.uniqueResult();

	}

	/**
	 * Retorna a quantidade total de projetos a serem distribuídos para a consultoria especial.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public long findQtdProjetosDistribuicaoEspecial() throws HibernateException, DAOException{

		StringBuffer hql = new StringBuffer();

		hql.append( " SELECT count (DISTINCT pp.id) ");
		hql.append( " FROM ProjetoPesquisa pp " );
		hql.append( " LEFT JOIN pp.avaliacoesProjeto as avaliacoes ");
		hql.append( " WHERE pp.projeto.interno = trueValue() " );
		hql.append( " AND pp.projeto.situacaoProjeto.id in "+ UFRNUtils.gerarStringIn(new int[]{TipoSituacaoProjeto.SUBMETIDO,
				TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE, TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE}) );
		hql.append(" GROUP BY avaliacoes.media " );
		hql.append(" HAVING sum(CASE WHEN avaliacoes.situacao = "+ AvaliacaoProjeto.REALIZADA + "THEN 1 else 0 END) = 0");
		hql.append(" OR ( sum(CASE WHEN avaliacoes.situacao = "+ AvaliacaoProjeto.REALIZADA + "THEN 1 else 0 END) = 1");
		hql.append(" AND avaliacoes.media < 5 )");
		hql.append(" OR avaliacoes.media < 3 ");

		Query query = getSession().createQuery( hql.toString() );

		return (Long) query.uniqueResult();

	}

	/**
	 * Busca as participações de um docente como membro de projetos cujo relatório foi aprovado
	 * @param servidor
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<MembroProjeto> findByRelatorioFinalizado(Servidor servidor, int ano ) throws DAOException {

		StringBuilder hql = new StringBuilder("select distinct mp from ProjetoPesquisa pp " +
				" join pp.relatoriosProjeto relatorio join pp.projeto p join p.equipe mp "
				+ " where pp.codigo.ano = " + ano
				+ " and relatorio.avaliacao = " +RelatorioProjeto.APROVADO);
		if(servidor != null){
			hql.append(" and mp.servidor.id = :idServidor");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idServidor", servidor.getId());
			return q.list();
		}
		return null;

	}

	/**
	 * Busca os projetos de pesquisa de um docente que ainda não tiveram seu relatório final enviado.
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findPendentesRelatorioFinal( Servidor servidor ) throws DAOException {
        try {
        	
        	Query q = getSession().createQuery("select pp from ProjetoPesquisa pp " +
        			"left join pp.relatoriosProjeto as relatorio " +
        			"where pp.coordenador.id = :coordenador " +
        			"and current_date() - pp.projeto.dataInicio >= :diasPermiteEnvioRelatorioAnual "+
        			"and pp.projeto.situacaoProjeto.id in "+ UFRNUtils.gerarStringIn(new Integer[]{TipoSituacaoProjeto.EM_ANDAMENTO, TipoSituacaoProjeto.RENOVADO}));
        	q.setInteger("coordenador", servidor.getId());
        	q.setInteger("diasPermiteEnvioRelatorioAnual", ParametroHelper.getInstance().getParametroInt(ParametrosPesquisa.DIAS_PARA_ENVIO_RELATORIO_ANUAL_PROJETO_PESQUISA));
        	return q.list();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca os projetos pendentes de avaliação de uma determinada área de conhecimento.
	 * @param areaConhecimentoCnpq
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findPendentesAvaliacao(AreaConhecimentoCnpq areaConhecimentoCnpq) throws DAOException {
		try {
			
			String projecao = "pp.id, pp.codigo, pp.projeto.titulo, pp.projeto.descricao";
			
			String hql = "SELECT " + projecao +
			 " FROM ProjetoPesquisa pp" +
			 " WHERE pp.areaConhecimentoCnpq.grandeArea.id = :grande" +
			 " and pp.projeto.situacaoProjeto.id in" + 
			 UFRNUtils.gerarStringIn(new Integer[] {TipoSituacaoProjeto.SUBMETIDO, TipoSituacaoProjeto.AVALIACAO_INSUFICIENTE, 
					 TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE, TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE} ) +
			 " and pp.projeto.interno = :interno" +
			 " and pp.projeto.ano = :ano" +
			 " and pp.edital.avaliacaoVigente = :vigente" +
			 " and pp.id not in (select a.projetoPesquisa.id from AvaliacaoProjeto a where a.situacao = "+ AvaliacaoProjeto.REALIZADA +")" +
			 " order by pp.codigo";
	
			Query q = getSession().createQuery( hql );
			
			q.setInteger("grande", areaConhecimentoCnpq.getGrandeArea().getId() );
			q.setBoolean("interno", Boolean.TRUE);
			q.setInteger("ano", CalendarUtils.getAnoAtual() );
			q.setBoolean("vigente", Boolean.TRUE);
			
			List<Object[]> lista = q.list();
			
			Collection<ProjetoPesquisa> projetosPesquisa = HibernateUtils.parseTo(lista, projecao, ProjetoPesquisa.class, "pp");
			
			return projetosPesquisa;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os projetos de pesquisa externos para o relatório de produção intelectual
	 *
	 * @param servidor
	 * @param coordenador
	 * @param ano
	 * @return
	 * @throws DAOException
	 */	
	public Collection<ProjetoPesquisa> findExternosByMembro( Servidor servidor, boolean coordenador, int ano )  throws DAOException {
		Collection<ProjetoPesquisa> projetos = new ArrayList<ProjetoPesquisa>();

		try {
			StringBuilder hql =  new StringBuilder();
			hql.append("select new map(pp.projeto.titulo as titulo, pp.codigo.prefixo as prefixo, pp.codigo.numero as numero, max(pp.codigo.ano) as ano, pp.projeto.dataInicio as dataInicio, pp.projeto.dataFim as dataFim)");
			hql.append(" from ProjetoPesquisa pp join pp.projeto.equipe as mp");
			hql.append(" where pp.projeto.interno = falseValue() ");
			hql.append(" and pp.codigo.ano = " + ano);
			hql.append(" and mp.servidor.id = " + servidor.getId());

			if ( coordenador ) {
				hql.append(" and mp.tipoParticipacao = " + FuncaoMembro.COORDENADOR);
			} else {
				hql.append(" and mp.tipoParticipacao != " + FuncaoMembro.COORDENADOR);
			}

			hql.append(" group by pp.projeto.titulo, pp.codigo.prefixo, pp.codigo.numero, pp.projeto.dataInicio, pp.projeto.dataFim ");
			hql.append(" order by pp.projeto.dataInicio, pp.projeto.titulo ");

			Query q = getSession().createQuery(hql.toString());
		
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> resultado = q.list();
			for (Map<String, Object> mapa : resultado) {
				ProjetoPesquisa projeto = new ProjetoPesquisa();
				projeto.setTitulo((String) mapa.get("titulo"));
				projeto.setCodigo( new CodigoProjetoPesquisa((String) mapa.get("prefixo"),
						(Integer) mapa.get("numero"),
						(Integer) mapa.get("ano")));
				projeto.setDataInicio((Date) mapa.get("dataInicio"));
				projeto.setDataFim((Date) mapa.get("dataFim"));
				projetos.add(projeto);
			}

			return projetos;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os projetos de pesquisa para o relatório individual do docente.
	 *
	 */	
	public Collection<ProjetoPesquisa> findTodosByMembro( Servidor servidor, boolean coordenador, int ano )  throws DAOException {
		Collection<ProjetoPesquisa> projetos = new ArrayList<ProjetoPesquisa>();

		try {
			StringBuilder hql =  new StringBuilder();
			hql.append("select new map(pp.id, pp.projeto.titulo as titulo, pp.codigo.prefixo as prefixo, pp.codigo.numero as numero, " +
					" max(pp.codigo.ano) as ano, pp.projeto.dataInicio as dataInicio, pp.projeto.dataFim as dataFim, mp.funcaoMembro.descricao as descricao )");
			hql.append(" from ProjetoPesquisa pp join pp.projeto.equipe as mp");
			hql.append(" where year(pp.projeto.dataInicio) <= " + ano + " and year(pp.projeto.dataFim) >= " + ano);
			hql.append(" and mp.servidor.id = " + servidor.getId());
			hql.append(" and pp.projeto.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(new int[] {TipoSituacaoProjeto.EM_ANDAMENTO,TipoSituacaoProjeto.RENOVADO,
					TipoSituacaoProjeto.FINALIZADO,TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO,TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO}));

			if ( coordenador ) {
				hql.append(" and mp.funcaoMembro = " + FuncaoMembro.COORDENADOR);
			} else {
				hql.append(" and mp.funcaoMembro != " + FuncaoMembro.COORDENADOR);
			}

			hql.append(" group by pp.id, pp.projeto.titulo, pp.codigo.prefixo, pp.codigo.numero, pp.projeto.dataInicio, pp.projeto.dataFim, mp.funcaoMembro.descricao ");
			hql.append(" order by pp.projeto.dataInicio, pp.projeto.titulo ");

			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> resultado = q.list();
			for (Map<String, Object> mapa : resultado) {
				ProjetoPesquisa projeto = new ProjetoPesquisa();
				projeto.setTitulo((String) mapa.get("titulo"));
				projeto.setCodigo( new CodigoProjetoPesquisa((String) mapa.get("prefixo"),
						(Integer) mapa.get("numero"),
						(Integer) mapa.get("ano")));
				projeto.setDataInicio((Date) mapa.get("dataInicio"));
				projeto.setDataFim((Date) mapa.get("dataFim"));
				projeto.setId((Integer) mapa.get("0"));
				projeto.getProjeto().setCoordenador( new MembroProjeto() );
				projeto.getProjeto().getCoordenador().setFuncaoMembro(new FuncaoMembro());
				projeto.getProjeto().getCoordenador().getFuncaoMembro().setDescricao((String) mapa.get("descricao"));
				projetos.add(projeto);
			}

			return projetos;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna um mapa com os docentes e as respectivas médias das avaliações de seus projetos.
	 * 
	 * @param edital
	 * @return
	 * @throws DAOException
	 */	
	public Map<Integer, Double> findMediaProjetosByEdital(EditalPesquisa edital) throws DAOException {
		Map<Integer, Double> medias = new HashMap<Integer, Double>();

		try {
			int[] ids = new int[] {TipoSituacaoProjeto.EM_ANDAMENTO};

			StringBuilder hql =  new StringBuilder();
			hql.append("SELECT mp.servidor.id as idDocente, " +
				 " sum( avaliacoes.media )/coalesce(sum(CASE WHEN avaliacoes.dataAvaliacao IS NOT null AND avaliacoes.justificativa IS null " +
				 " THEN 1 else null END), 1) as media");
			hql.append(" FROM ProjetoPesquisa pp ");
			hql.append(" JOIN pp.membrosProjeto as mp");
			hql.append(" JOIN pp.avaliacoesProjeto as avaliacoes ");
			hql.append(" where pp.interno = trueValue() ");
			hql.append(" and pp.edital.id =  " + edital.getId() );
			hql.append(" and pp.projeto.situacaoProjeto.id IN " + UFRNUtils.gerarStringIn(ids));

			hql.append(" GROUP BY mp.servidor.id ");
			@SuppressWarnings("unchecked")
			List<Object[]> lista = getSession().createQuery(hql.toString()).list();
			for (Object[] object : lista) {
				medias.put((Integer) object[0], (Double) object[1] != null ? (Double) object[1] : 0);
			}

			return medias;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Novo cálculo da nota dos projetos (NP) de cada professor usado na distribuição de cotas
	 * Retorna o máximo entre as notas dos projetos que ele participa (somente projetos com o mesmo ano do edital)
	 * Projetos externos possuem nota 10.0
	 * @return
	 * @throws DAOException
	 */	
	public Map<Integer, Double> findNotaProjetosByAnoEdital(EditalPesquisa edital) throws DAOException {
		Map<Integer, Double> medias = new HashMap<Integer, Double>();

		try {
			int[] ids = new int[] {TipoSituacaoProjeto.EM_ANDAMENTO, TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, TipoSituacaoProjeto.APROVADO};

			StringBuilder hql =  new StringBuilder();
			hql.append("SELECT mp.servidor.id as idDocente, " +
				 		" max( case when proj.media > 10.0 then 10.0 else proj.media end ) as media ");
			hql.append(" FROM ProjetoPesquisa pp ");
			hql.append(" JOIN pp.projeto as proj");
			hql.append(" JOIN proj.equipe as mp");
			hql.append(" WHERE pp.projeto.situacaoProjeto.id IN " + UFRNUtils.gerarStringIn(ids));
			hql.append(" AND proj.dataFim > current_date() " );

			hql.append(" GROUP BY mp.servidor.id ");

			@SuppressWarnings("unchecked")
			List<Object[]> lista = getSession().createQuery(hql.toString()).list();
			for (Object[] object : lista) {
				medias.put((Integer) object[0], (Double) object[1] != null ? (Double) object[1] : 0);
			}

			return medias;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Sobrecarga do método de busca de projetos com algumas diferenças nos parâmetros.
	 * @param interno
	 * @param codigoProjeto
	 * @param anoProjeto
	 * @param idCoordenador
	 * @param nomePesquisador
	 * @param palavraChaveTitulo
	 * @param idGestora
	 * @param idUnidade
	 * @param titulo
	 * @param objetivos
	 * @param linhaPesquisa
	 * @param idSubarea
	 * @param idGrupoPesquisa
	 * @param idAgenciaFinanciadora
	 * @param relatorioEnviado
	 * @param formatoRelatorio
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ProjetoPesquisa> filter(Boolean interno,
			CodigoProjetoPesquisa codigoProjeto, Integer anoProjeto, Integer idCoordenador,
			String nomePesquisador, String palavraChaveTitulo, Integer idGestora,
			Integer idUnidade, String titulo, String objetivos, String linhaPesquisa,
			Integer idSubarea, Integer idGrupoPesquisa,
			Integer idAgenciaFinanciadora, Boolean relatorioEnviado, boolean formatoRelatorio)
			throws DAOException, LimiteResultadosException {
		return filter(interno, codigoProjeto, anoProjeto,
				idCoordenador, null, palavraChaveTitulo,
				idGestora, idUnidade, titulo, objetivos, linhaPesquisa,
				idSubarea, idGrupoPesquisa, idAgenciaFinanciadora, null, null, null,
				relatorioEnviado, formatoRelatorio, nomePesquisador, null, true);
	}

	/**
	 * Sobrecarga do método de busca de projetos com algumas diferenças nos parâmetros.
	 * @param interno
	 * @param codigoProjeto
	 * @param anoProjeto
	 * @param idCoordenador
	 * @param idPesquisador
	 * @param palavraChaveTitulo
	 * @param idGestora
	 * @param idUnidade
	 * @param titulo
	 * @param objetivos
	 * @param linhaPesquisa
	 * @param idSubarea
	 * @param idGrupoPesquisa
	 * @param idAgenciaFinanciadora
	 * @param idEdital
	 * @param idStatusProjeto
	 * @param categoriaProjeto
	 * @param relatorioEnviado
	 * @param formatoRelatorio
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ProjetoPesquisa> filter(Boolean interno,
			CodigoProjetoPesquisa codigoProjeto, Integer anoProjeto, Integer idCoordenador,
			Integer idPesquisador, String palavraChaveTitulo, Integer idGestora,
			Integer idUnidade, String titulo, String objetivos, String linhaPesquisa,
			Integer idSubarea, Integer idGrupoPesquisa,
			Integer idAgenciaFinanciadora, Integer idEdital, Integer idStatusProjeto, Integer categoriaProjeto,
			Boolean relatorioEnviado, boolean formatoRelatorio) throws DAOException, LimiteResultadosException {
		return filter(interno, codigoProjeto, anoProjeto,
				idCoordenador, idPesquisador, palavraChaveTitulo,
				idGestora, idUnidade, titulo, objetivos, linhaPesquisa,
				idSubarea, idGrupoPesquisa, idAgenciaFinanciadora, idEdital, idStatusProjeto, categoriaProjeto,
				relatorioEnviado, formatoRelatorio, null, null, false);
	}

	/**
	 * Retorna um quantitativo de projetos internos e externos cadastrados por mês num determinado ano. 
	 * @param ano
	 * @return
	 */	
	public List<Map<String,Object>> findEstatisticasCadastroProjetos(int ano) {

		String sql = "select (extract(month from data_cadastro)) as mes, " +
				" sum(case when p.interno then 1 else 0 end) as internos, " +
				" sum(case when not p.interno then 1 else 0 end) as externos " +
			" from projetos.projeto p, pesquisa.projeto_pesquisa pp " +
			" where p.id_projeto = pp.id_projeto " +
				" and extract(year from data_cadastro) = ?" +
				" and p.id_tipo_situacao_projeto <> ? " +
				" group by extract(month from data_cadastro) " +
				" order by mes";

		List<Map<String,Object>> estatisticas = getJdbcTemplate().queryForList(sql, new Object[] {ano, TipoSituacaoProjeto.CADASTRADO});

		if (!estatisticas.isEmpty()) {
			Formatador f = Formatador.getInstance();
			for(Map<String,Object> linha : estatisticas ) {
				linha.put( "mes" , f.formatarMes( ((Double) linha.get("mes")).intValue() - 1 ) );
			}
		}

		return estatisticas;
	}

	/**
	 * Retorna todos os projetos cujo os membros fazem parte do programa de pós graduação informado
	 * Considera tanto docente interno (servidor) quanto externo
	 * @param idPrograma
	 * @return
	 * @throws DAOException
	 */
	public List<ProjetoPesquisa> findByMembrosPrograma(int idPrograma, Integer ano, 
			CodigoProjetoPesquisa codigo, String titulo, String membro, Integer idSituacao) throws DAOException{
		
		StringBuffer hql = new StringBuffer();
		hql.append( " SELECT p.id, pp.codigo.prefixo, pp.codigo.ano, pp.codigo.numero, " +
					" p.titulo, sp.id, s.id, p1.nome, de.id, p2.nome,sp.descricao, p.ativo, " +
					" pp.id, p.descricao FROM ProjetoPesquisa pp " +
					" JOIN pp.projeto p JOIN p.equipe membro JOIN p.situacaoProjeto sp " +
					" LEFT JOIN membro.servidor s " +
					" LEFT JOIN s.pessoa p1 " +
					" LEFT JOIN membro.docenteExterno de" +
					" LEFT JOIN de.pessoa p2, " +
					" EquipePrograma equipe" +
					" WHERE equipe.programa.id = :idPrograma " +
					" AND ( equipe.servidor.id = s.id " +
					" OR equipe.docenteExterno.id = de.id ) " );
		
		if( ano != null )
			hql.append( " AND pp.codigo.ano = :ano " );
		if( codigo != null ){
			hql.append( " AND pp.codigo.prefixo = :codPrefixo " );
			hql.append( " AND pp.codigo.numero = :codNumero " );
			hql.append( " AND pp.codigo.ano = :codAno " );
			
		}
		if( titulo != null )
			hql.append( " AND upper(p.titulo) like :titulo " );
		if( idSituacao != null ){
			hql.append( " AND p.situacaoProjeto.id = :situacao " );
		} else{
			hql.append( " AND p.situacaoProjeto.id in "  + gerarStringIn(new Integer[]{TipoSituacaoProjeto.EM_ANDAMENTO,TipoSituacaoProjeto.RENOVADO, 
					TipoSituacaoProjeto.FINALIZADO, TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO, TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO}) );
		}
		
		if( membro != null ){
			hql.append( " AND pp.id IN ( " );
			hql.append( " SELECT projPesq.id FROM ProjetoPesquisa projPesq " );
			hql.append( " JOIN projPesq.projeto p2 JOIN p2.equipe mem " );
			hql.append( " LEFT JOIN mem.servidor serv LEFT JOIN serv.pessoa p1 " );
			hql.append( " LEFT JOIN mem.docenteExterno docExt LEFT JOIN docExt.pessoa p2 " );
			hql.append( " WHERE 1 = 1 " );
			hql.append( " AND ( p1.nome like :membro OR p2.nome like :membro ) " );
			hql.append( " )  " );
		}
		hql.append( " ORDER BY p.id " );
		
		Query q = getSession().createQuery( hql.toString() );
		q.setInteger( "idPrograma" , idPrograma);
		
		if( ano != null )
			q.setInteger("ano", ano);
		if( codigo != null ){
			q.setString("codPrefixo", codigo.getPrefixo());
			q.setInteger("codNumero", codigo.getNumero());
			q.setInteger("codAno", codigo.getAno() );
		}
		if( titulo != null )
			q.setString("titulo", titulo.toUpperCase()+"%");
		if( membro != null )
			q.setString("membro", "%" + membro.toUpperCase() + "%");
		if( idSituacao != null )
			q.setInteger("situacao", idSituacao);
		
		List<?> lista = q.list();
		List<ProjetoPesquisa> result = new ArrayList<ProjetoPesquisa>();
		ProjetoPesquisa pp = new ProjetoPesquisa();
		
		for (int a = 0; a < lista.size(); a++) {
			
			Object[] colunas = (Object[]) lista.get(a);
			
			pp.setId((Integer) colunas[12]);
			pp.setProjeto(new Projeto());
			pp.getProjeto().setId((Integer) colunas[0]);
			pp.getProjeto().setAtivo((Boolean) colunas[11]);
			pp.getProjeto().setDescricao((String) colunas[13]);
			
			pp.setCodigo(new CodigoProjetoPesquisa());
			pp.getCodigo().setPrefixo((String) colunas[1]);
			pp.getCodigo().setAno((Integer) colunas[2]);
			pp.getCodigo().setNumero((Integer) colunas[3]);
			
			pp.getProjeto().setTitulo((String) colunas[4]);
			pp.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto((Integer) colunas[5],(String) colunas[10]));
			
			//Popula o docente externo ou servidor como membro do projeto de pesquisa
			MembroProjeto membroProjeto = new MembroProjeto();
			if((Integer) colunas[6] != null){
				membroProjeto.setServidor(new Servidor());
				membroProjeto.getServidor().setPessoa(new Pessoa());
				membroProjeto.getServidor().setId((Integer) colunas[6]);
				membroProjeto.getServidor().getPessoa().setNome((String) colunas[7]);
			}else{
				membroProjeto.setDocenteExterno(new DocenteExterno());
				membroProjeto.getDocenteExterno().setPessoa(new Pessoa());
				membroProjeto.getDocenteExterno().setId((Integer) colunas[8]);
				membroProjeto.getDocenteExterno().getPessoa().setNome((String) colunas[9]);
			}
		
			//Se o projeto existir, localiza-o e adiciona mais um membro.
			if( result.contains(pp) ){
				int index  =  result.indexOf(pp);
				result.get( index ).getProjeto().addMembroEquipe( membroProjeto );
			}else{
				pp.getProjeto().addMembroEquipe(membroProjeto);
				result.add(pp);
				pp = new ProjetoPesquisa();
			}
			
		}
		return result;
		
	}

	/**
	 * Retornar um quantitativo dos projetos cadastrados no referido mês e ano.
	 * 
	 * @param ano
	 * @param mes
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findQuantitativoProjetos(int ano, int mes) throws DAOException {
		String sqlconsulta = new String("select "+ 
				"	sum(case when p.interno is true then 1 else 0 end) as Internos, "+
				"	sum(case when pesquisa is true then 1 else 0 end) as Associados, "+
				"	sum(case when pesquisa is false then 1 else 0 end) as Isolados, "+
				"	sum(case when p.interno is false then 1 else 0 end) as Externos "+
				"	from projetos.projeto p "+
				"	inner join pesquisa.projeto_pesquisa using (id_projeto) "+
				"	where date_part('year', data_cadastro) =  "+ano+
				"	and date_part('months', data_cadastro) = "+mes);

	
			SQLQuery q;
		try {
			q = getSession().createSQLQuery(sqlconsulta.toString());
			q.setResultTransformer( Transformers.ALIAS_TO_ENTITY_MAP );
		} catch (Exception e) {
			throw new DAOException(e);
		}

		return q.list();
	}

	/**
	 * Método para buscar os projetos de pesquisa que ainda não estão
	 * inseridos no SIGED.
	 */
	@SuppressWarnings("unchecked")
	public List<ProjetoPesquisa> buscarProjetosPesquisaNaoExistentesNoSiged() throws DAOException {
		return getSession().createQuery("select a from ProjetoPesquisa a where a.idArquivo is null and a.securityToken is null")
				.setMaxResults(10).list();
	}

	/**
	 * Obtém a lista de projetos de pesquisa para serem exibidos no portal do consultor.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<ProjetoPesquisa> findPortalConsultor() throws DAOException {
		
		StringBuilder hqlCorpo = new StringBuilder();
		StringBuilder hqlConsulta = new StringBuilder();

		String projecao = "id, codigo, titulo, objetivos, coordenador, coordenador.pessoa.nome, coordenadorExterno, coordenadorExterno.pessoa.nome, situacaoProjeto, centro.nome, centro.sigla, interno, descricao, dataCadastro";

		hqlConsulta
				.append(" SELECT distinct pp.id as id, pp.codigo as codigo, projeto.titulo as titulo, projeto.objetivos, "
						+ " c as coordenador, p.nome, ce as coordenadorExterno, pe.nome, projeto.situacaoProjeto as situacaoProjeto, pp.centro.nome, pp.centro.sigla, " +
								" projeto.interno as interno, projeto.descricao as descricao, projeto.dataCadastro as dataCadastro");
		hqlCorpo.append(" FROM ProjetoPesquisa pp ");
		hqlCorpo.append(" INNER JOIN pp.projeto as projeto");
		hqlCorpo.append(" LEFT OUTER JOIN projeto.equipe as membros");
		hqlCorpo.append(" LEFT OUTER JOIN pp.coordenador as c");
		hqlCorpo.append(" LEFT OUTER JOIN pp.coordenador.pessoa as p");
		hqlCorpo.append(" LEFT OUTER JOIN pp.coordenadorExterno as ce");
		hqlCorpo.append(" LEFT OUTER JOIN pp.coordenadorExterno.pessoa as pe");
		hqlCorpo.append(" LEFT OUTER JOIN pp.financiamentosProjetoPesq as financiamentos");
		hqlCorpo.append(" WHERE 1 = 1 ");
		hqlCorpo.append(" AND (" + HibernateUtils.generateDateIntersection("projeto.dataInicio", "projeto.dataFim", "now()", "now()") + " OR year(projeto.dataFim) = " + CalendarUtils.getAnoAtual() +")");
		
		hqlCorpo.append(" ORDER BY pp.codigo.ano desc, pp.centro.nome, p.nome, pp.codigo.numero");
		hqlConsulta.append(hqlCorpo.toString());
		Query queryConsulta = getSession().createQuery(hqlConsulta.toString());

		return HibernateUtils.parseTo(queryConsulta.list(), projecao, ProjetoPesquisa.class);
	}

	/**
	 * Retorna um projeto de pesquisa a partir do identificador do projeto base.
	 * @param idProjeto
	 * @return
	 * @throws DAOException
	 */
	public ProjetoPesquisa findByIdProjetoBase(int idProjeto) throws DAOException {
		return (ProjetoPesquisa) getSession().createQuery("select pp from ProjetoPesquisa pp join pp.projeto p where p.id = " + idProjeto).setMaxResults(1).uniqueResult();
	}
}