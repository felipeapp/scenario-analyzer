/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static org.hibernate.criterion.Order.desc;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.in;
import static org.hibernate.criterion.Restrictions.isNotNull;
import static org.hibernate.criterion.Restrictions.ne;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.assistencia.cadunico.dominio.FormularioCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.dominio.ParametroDiscenteCadastroUnico;
import br.ufrn.sigaa.assistencia.dominio.RestricaoDiscenteCadastroUnico;
import br.ufrn.sigaa.complexohospitalar.dominio.DiscenteResidenciaMedica;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.dominio.DadosCalculosDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.InteressadoBolsa;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoInteressadoBolsa;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.IntegralizacoesHelper;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.RelatoriosPlanejamentoMBean;
import br.ufrn.sigaa.ensino.infantil.dominio.DiscenteInfantil;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.TipoCursoStricto;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.util.RepositorioInformacoesCalculoDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.prodocente.atividades.dominio.TrabalhoFimCurso;

/** 
 * Dao responsável pelas consultas que são inerentes apenas aos discentes.
 * 
 * @author André M Dantas
 *
 */

public class DiscenteDao extends GenericSigaaDAO {

	/** Informa a classe parente de Discente */
	private final Class<?> discenteSubClasse = Discente.class;
	
	/**
	 * Busca um discente ativo e que não esteja CANCELADO, CONCLUÍDO, JUBILADO ou EXCLUIDO 
	 * e que possua a matrícula igual a passada como parâmetro.
	 * 
	 * @param matricula
	 */
	public Discente findAtivosByMatricula(long matricula) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(discenteSubClasse);
			c.add(Restrictions.eq("matricula", matricula));
			c.add(Restrictions.ne("status", StatusDiscente.CANCELADO));
			c.add(Restrictions.ne("status", StatusDiscente.CONCLUIDO));
			c.add(Restrictions.ne("status", StatusDiscente.JUBILADO));
			c.add(Restrictions.ne("status", StatusDiscente.EXCLUIDO));

			return (Discente) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Busca um discente ativo pela matrícula e nível de ensino.
	 * O Discente também não pode estar (CANCELADO,CONCLUIDO,JUBILADO,EXCLUIDO)
	 * 
	 * @param matricula
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Discente findAtivosByMatricula(long matricula, char... nivel) throws DAOException {
		return findAtivosByMatriculaPrograma(matricula, null, nivel);
	}

	/**
	 * Busca um discente ativo pela matrícula, nível de ensino e programa especificado.
	 * O discente também não pode estar (CANCELADO,CONCLUIDO,JUBILADO,EXCLUIDO) 
	 * 
	 * @param matricula
	 * @param nivel
	 * @param idPrograma
	 * @return
	 * @throws DAOException
	 */
	public Discente findAtivosByMatriculaPrograma(long matricula, Integer idPrograma, char... nivel) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(discenteSubClasse);
			c.add(Restrictions.eq("matricula", matricula));
			c.add(Restrictions.ne("status", StatusDiscente.CANCELADO));
			c.add(Restrictions.ne("status", StatusDiscente.CONCLUIDO));
			c.add(Restrictions.ne("status", StatusDiscente.JUBILADO));
			c.add(Restrictions.ne("status", StatusDiscente.EXCLUIDO));

			if (nivel != null) {
				Character niveis[] = new Character[nivel.length];
				for (int i = 0; i < nivel.length; i++) {
					niveis[i] = new Character(nivel[i]);
				}
				c.add(Restrictions.in("nivel", niveis));
			}
			if (idPrograma != null) {
				c.add(Restrictions.eq("gestoraAcademica.id", idPrograma));
			}
			return (Discente) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca discente pela matrícula
	 * 
	 * @param matricula
	 * @return
	 * @throws DAOException
	 */
	public Discente findByMatricula(long matricula) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(discenteSubClasse);
			c.add(Restrictions.eq("matricula", matricula));
			c.setMaxResults(1);
			return (Discente) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca discente pela matrícula e nível de ensino informado
	 * 
	 * @param matricula
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public DiscenteAdapter findByMatricula(long matricula, char nivel) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(discenteSubClasse);
			c.add(Restrictions.eq("matricula", matricula));
			c.add(Restrictions.eq("nivel", nivel));
			c.addOrder(Order.desc("anoIngresso"));
			c.addOrder(Order.desc("periodoIngresso"));
			c.setMaxResults(1);
			return (Discente) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca discente pela matrícula e níveis de ensino informados, utilizando o método {@link #DiscenteDao()#findByMatricula(long, char[], Integer)}.
	 * @param matricula
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Discente findByMatricula(long matricula, char nivel[]) throws DAOException {
		return findByMatricula(matricula, nivel, (Integer) null);
	}
	
	/**
	 * Busca discente pela matrícula e níveis de ensino informados
	 * 
	 * @param matricula
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Discente findByMatricula(long matricula, char nivel[], Integer... status) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(discenteSubClasse);
			c.add(Restrictions.eq("matricula", matricula));
			if (nivel != null && nivel.length > 0) {
				Character niveis[] = new Character[nivel.length];
				for (int i = 0; i < nivel.length; i++) {
					niveis[i] = new Character(nivel[i]);
				}
				c.add(Restrictions.in("nivel", niveis));
			}
			if (status != null)
				c.add(Restrictions.in("status", status));
			c.addOrder(Order.desc("anoIngresso"));
			c.addOrder(Order.desc("periodoIngresso"));
			c.setMaxResults(1);
			return (Discente) c.uniqueResult();
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca discente pela matrícula, unidade do curso e nível de ensino informado
	 * 
	 * @param matricula
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Discente findAtivosByMatriculaUnidadeNivel(Long matricula, int unidade, char nivel) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT new Discente(id, matricula, pessoa.nome, status, nivel) FROM Discente WHERE  ");
			hql.append("matricula = :matricula and curso.unidade.id = :unidade and nivel = :nivel and status = :status");
			hql.append(" order by pessoa.nome");
			
			Query q = getSession().createQuery(hql.toString());
			q.setLong("matricula", matricula);
			q.setInteger("unidade", unidade);
			q.setCharacter("nivel", nivel);
			q.setInteger("status", StatusDiscente.ATIVO);

			return (Discente) q.uniqueResult();

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna os discentes vinculados à pessoa informada com o nível e status
	 * informado
	 *
	 * @param idPessoa
	 * @param nivel
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findByPessoaSituacao(int idPessoa, Integer... status) throws DAOException {
		
		String projecao = "id, discente.curso.id, matrizCurricular.grauAcademico.id, matrizCurricular.habilitacao.id ";
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select " + projecao + 
					" from DiscenteGraduacao where discente.pessoa.id = " + idPessoa);

			if (status != null) {
				hql.append(" and discente.status in " + gerarStringIn(status));
			}

			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			Collection<DiscenteGraduacao> lista = HibernateUtils.parseTo(q.list(), projecao, DiscenteGraduacao.class );
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca os discentes ativos da pessoa, da unidade gestora acadêmica 
	 * e do nível passado por parâmetro
	 * 
	 * @param idPessoa
	 * @param idUnidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Discente findAtivoByPessoaUnidadeNivel(Integer idPessoa, Integer idUnidade, Character nivel)	throws DAOException {
		
		Criteria c = getSession().createCriteria(Discente.class);
		c.add(Restrictions.eq("pessoa.id", idPessoa));
		c.add(Restrictions.eq("gestoraAcademica.id", idUnidade));
		c.add(Restrictions.eq("nivel", nivel));

		c.add(Restrictions.ne("status", StatusDiscente.CANCELADO));
		c.add(Restrictions.ne("status", StatusDiscente.CONCLUIDO));
		c.add(Restrictions.ne("status", StatusDiscente.JUBILADO));
		c.add(Restrictions.ne("status", StatusDiscente.AFASTADO));
		c.add(Restrictions.ne("status", StatusDiscente.EXCLUIDO));

		return (Discente) c.uniqueResult();
	}

	/**
	 * Busca todos os discente ATIVOS que começam com o nome informado, nível de ensino e unidade 
	 * (NAO USA PROJEÇÃO)
	 * 
	 * @param nome
	 * @param idUnidade
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByNome(String nome, int idUnidade, char nivel, PagingInformation paging) throws DAOException {
		return findByNome(nome, idUnidade, new char[] { nivel }, null, false, true,	paging);
	}


	/**
	 * Busca de forma otimizada todos os alunos pelo nome informado, unidade, nível de ensino, curso, 
	 * status (ativo ou inativo) e somente os alunos concluintes (sim ou não).
	 * @param nome
	 * @param idUnidade
	 * @param nivel
	 * @param curso
	 * @param somenteAtivos
	 * @param somenteConcluintes
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByNomeOtimizado(String nome, int idUnidade, char[] nivel,
			Curso curso, boolean somenteAtivos, boolean somenteConcluintes)	throws DAOException {
		return findByNome(nome, idUnidade, nivel, curso, true, somenteAtivos, null, somenteConcluintes);
	}

	/**
	 * Busca de forma otimizada ou não todos os alunos pelo nome informado, unidade, nível de ensino, curso e
	 * status (ativo ou inativo).
	 * @param nome
	 * @param unid
	 * @param nivel
	 * @param curso
	 * @param otimizado
	 * @param somenteAtivos
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByNome(String nome, int unid, char[] nivel,	Curso curso, boolean otimizado, 
			boolean somenteAtivos, PagingInformation paging) throws DAOException {
		return findByNome(nome, unid, nivel, curso, otimizado, somenteAtivos, paging, false);
	}
	

	/**
	 * Busca todos os discentes que começam pelo nome especificado, gestora acadêmica, nível e curso
	 * 
	 * @param nome
	 * @param unid
	 * @param nivel
	 * @param curso
	 * @param otimizado
	 * @param somenteAtivos
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	private Collection<Discente> findByNome(String nome, int unid, char[] nivel,	Curso curso, boolean otimizado, 
			boolean somenteAtivos, PagingInformation paging, boolean somenteConcluintes) throws DAOException {
		
		try {
			StringBuffer hql = new StringBuffer();
			String projecao = "id, matricula, pessoa.nome, status, nivel";
			
			if (otimizado) {
				hql.append("select " + projecao);
			}
			hql.append(" from " + discenteSubClasse.getName() + " WHERE ");
			String nomeBusca = Normalizer.normalize(nome.trim(), Normalizer.Form.NFD);
			nomeBusca = nomeBusca.replaceAll("[^\\p{ASCII}]", "");
			hql.append(UFRNUtils.convertUtf8UpperLatin9("pessoa.nomeAscii") + " like "
					+ UFRNUtils.toAsciiUpperUTF8("'" + nomeBusca + "%'"));

			if (unid > 0) {
				hql.append(" and gestoraAcademica.id = " + unid);
			}
			if (!ArrayUtils.contains(nivel, '0')
					&& !ArrayUtils.contains(nivel, 'S')
					&& !ArrayUtils.contains(nivel, ' ')) {
				hql.append(" and nivel in " + UFRNUtils.gerarStringIn(nivel));
			} else if (ArrayUtils.contains(nivel, 'S')) {
				String niveis = new String(nivel);
				niveis += "ED";
				hql.append(" and nivel in " + UFRNUtils.gerarStringIn(niveis.toCharArray()));
			}
			if (curso != null) {
				hql.append(" and curso.id = " + curso.getId());
			}
			if (somenteAtivos) {
				hql.append(" and status <> " + StatusDiscente.CANCELADO
						+ " and status <> " + StatusDiscente.CONCLUIDO
						+ " and status <> " + StatusDiscente.EXCLUIDO
						+ " and status <> " + StatusDiscente.JUBILADO);
			}else if (somenteConcluintes) 
				hql.append(" and status in " + UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinte()));
			
			hql.append(" order by pessoa.nome  asc");
			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			
			if (otimizado) {
				@SuppressWarnings("unchecked")
				List<Object[]> lista = q.list();
				return HibernateUtils.parseTo(lista, projecao, Discente.class);
			} else {
				@SuppressWarnings("unchecked")
				Collection<Discente> lista = q.list();
				return lista;
			}
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os discentes pelo ano de entrada. 
	 * Filtra também por unidade e nível de ensino. 
	 * 
	 * @param ano
	 * @param unid
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByAnoIngresso(int ano, int unid, char nivel, PagingInformation paging) throws DAOException {
		
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from " + discenteSubClasse.getName() + " WHERE ");
			if (unid > 0) {
				hql.append("gestoraAcademica.id = " + unid + " and ");
			}
			if (nivel != '0') {
				hql.append("nivel = '" + nivel + "' and ");
			}
			hql.append(" anoIngresso = " + ano);
			hql.append(" and status <> " + StatusDiscente.EXCLUIDO);
			hql.append(" order by curso.nome, pessoa.nome asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);

			@SuppressWarnings("unchecked")
			Collection<Discente> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todas as turmas de um discente com matrículas com status
	 * MATRICULADA e EM_ESPERA. (( SEM OTIMIZAÇÃO ))
	 *
	 * @param idDiscente
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasMatriculadas(int idDiscente) throws DAOException {
		return findTurmasMatriculadas(idDiscente, false);
	}

	/**
	 * Busca todas as turmas de um discente com matrículas com status
	 * MATRICULADA e EM_ESPERA. (( OTIMIZAÇÃO PARAMETRIZADA ))
	 *
	 * @param idDiscente
	 * @param otimizado
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasMatriculadas(int idDiscente,	boolean otimizado) throws DAOException {
		
		try {
			StringBuffer hql = new StringBuffer();
			if (otimizado) {
				hql.append("select distinct m.turma.id, m.componente.id, m.componente.detalhes.nome, m.componente.codigo ");
			} else {
				hql.append("select distinct m.turma ");
			}
			hql.append("from MatriculaComponente m ");
			if (!otimizado) {
				hql.append(" left join fetch m.turma.horarios ");
			}
			hql.append("where m.discente.id = :idDiscente and "
					+ "m.turma.id > 0 and m.situacaoMatricula.id "
					+ "in (:matriculado, :espera)");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idDiscente", idDiscente);
			q.setInteger("matriculado", SituacaoMatricula.MATRICULADO.getId());
			q.setInteger("espera", SituacaoMatricula.EM_ESPERA.getId());
			if (!otimizado) {
				@SuppressWarnings("unchecked")
				Collection<Turma> lista = q.list();
				return lista;
			} else {
				ArrayList<Turma> turmas = new ArrayList<Turma>(0);
				@SuppressWarnings("unchecked")
				List<Object[]> res = q.list();
				if (res != null) {
					for (Object[] id : res) {
						Turma t = new Turma((Integer) id[0]);
						t.setDisciplina(new ComponenteCurricular(
								(Integer) id[1]));
						t.getDisciplina().setDetalhes(new ComponenteDetalhes());
						t.getDisciplina().getDetalhes().setNome( (String) id[2]);
						t.getDisciplina().setCodigo( (String) id[3]);
						turmas.add(t);
					}
				}
				return turmas;
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna o total de períodos cursados pelo discente
	 * 
	 * @param idDiscente
	 * @param calendarioAcademico
	 * @return
	 * @throws DAOException
	 */
	public long findQtdPeriodosCursados(int idDiscente, CalendarioAcademico calendarioAcademico) throws DAOException {
		
		try {
			String sql = "select count(distinct (m.ano || '' || m.periodo)) from ensino.matricula_componente m "
					+ " where m.id_discente =  " + idDiscente
					+ " and (m.ano != :ano or (m.ano = :ano and m.periodo != :periodo))"
					+ " and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas());
			Query q = getSession().createSQLQuery(sql);
			q.setInteger("ano", calendarioAcademico.getAno());
			q.setInteger("periodo", calendarioAcademico.getPeriodo());
			return ((BigInteger) q.uniqueResult()).longValue();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Busca todos os alunos com o status informado de um curso
	 * 
	 * @param idCurso
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByCurso(int idCurso, Integer... status)	throws DAOException {
		
		try {
			String projecao = "id, pessoa.nome,pessoa.email, matricula, curso.id, status, nivel";
			Query q = getSession().createQuery(
					"SELECT " + projecao + " FROM Discente WHERE "
							+ "curso.id=:curso and status in "
							+ gerarStringIn(status) + " ORDER BY pessoa.nome");
			q.setInteger("curso", idCurso);
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			return HibernateUtils.parseTo(lista, projecao, Discente.class);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Verifica se a pessoa com CPF informado possui vínculo de discente no mesmo nível de ensino.
	 * 
	 * @param idCurso
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public boolean possuiVinculo(Character nivel, Long cpf)	throws DAOException {
		return possuiVinculo(nivel, cpf, null);
	}
	
	/**
	 * Verifica se a pessoa com CPF informado possui vínculo de discente no mesmo nível de ensino e unidade gestora acadêmica.
	 * 
	 * @param idCurso
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public boolean possuiVinculo(Character nivel, Long cpf, Integer idUnidade)	throws DAOException {
		
		return getJdbcTemplate().queryForInt( " SELECT COUNT(*) FROM discente d INNER JOIN comum.pessoa p USING(id_pessoa) WHERE d.status in "+
				UFRNUtils.gerarStringIn( StatusDiscente.getStatusComVinculo() ) +" AND d.nivel = '" + nivel + "' AND p.cpf_cnpj = "  +cpf
				+ (idUnidade != null ? " AND d.id_gestora_academica = " + idUnidade : "") ) > 0;
				
	}

	/**
	 * Busca todos os alunos com o vínculo ativo do curso informado
	 * 
	 * @param idCurso
	 * @param status
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findDiscentesComVinculoByCurso(int idCurso)	throws DAOException {
		
		try {
			
			Collection<Integer> col = StatusDiscente.getStatusComVinculo();
			Object[] array = col.toArray();
			
			Integer[] status = new Integer[col.size()]; 
			for (int i = 0; i < array.length; i++) {
				status[i] = new Integer(array[i].toString());
			}			
			
			String projecao = "id, pessoa.nome, pessoa.email, matricula, curso.id, anoIngresso, periodoIngresso, status, nivel";
			Query q = getSession().createQuery(
					"SELECT " + projecao + " FROM Discente WHERE "
							+ "curso.id=:curso and status in "
							+ gerarStringIn(status) + " ORDER BY anoIngresso, periodoIngresso, pessoa.nome");
			q.setInteger("curso", idCurso);
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			return HibernateUtils.parseTo(lista, projecao, Discente.class);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	

	/**
	 * Busca todos os alunos do curso que possui o nome especificado
	 * 
	 * @param idCurso
	 * @param nome
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByCursoNome(int idCurso, String nome, PagingInformation paging) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(Discente.class);
			c.add(Restrictions.eq("curso", new Curso(idCurso)));
			c.add(Restrictions.ne("status", StatusDiscente.CANCELADO));
			c.add(Restrictions.ne("status", StatusDiscente.CONCLUIDO));
			c.add(Restrictions.ne("status", StatusDiscente.JUBILADO));
			Criteria cPessoa = c.createCriteria("pessoa");
			cPessoa.add(Restrictions.ilike("nome", nome, MatchMode.ANYWHERE));
			preparePaging(paging, c);
			@SuppressWarnings("unchecked")
			Collection<Discente> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca todas as matrículas que um aluno fez no ano-período informado
	 * 
	 * @param discente
	 * @param periodo
	 * @param ano
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findMatriculasByDiscente(Discente discente, int periodo, int ano) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MatriculaComponente.class);
			c.add(Restrictions.eq("periodo", (byte) periodo));
			c.add(Restrictions.eq("ano", (short) ano));
			c.add(Restrictions.eq("discente.id", discente.getId()));

			@SuppressWarnings("unchecked")
			List<MatriculaComponente> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna a lista de disciplinas concluídas e matriculadas de um discente
	 *
	 * @param idDiscente
	 * @param trazerCancelados
	 * @return
	 */
	public List<MatriculaComponente> findDisciplinasConcluidasMatriculadas(
			int idDiscente, boolean trazerCancelados) throws DAOException {
		return findDisciplinasConcluidasMatriculadas(idDiscente, trazerCancelados, true);
	}
	
	/**
	 * Retorna a lista de disciplinas concluídas e matriculadas de um discente
	 *
	 * @param idDiscente
	 * @param trazerCancelados
	 * @return
	 */
	public List<MatriculaComponente> findDisciplinasConcluidasMatriculadas(
			int idDiscente, boolean trazerCancelados, boolean esconderSubUnidades) throws DAOException {

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "select mc.id_matricula_componente, mc.data_cadastro, mc.ano, mc.periodo, t.id_turma as id_turma, t.codigo as codigo_turma, t.data_inicio, t.data_fim, mc.media_final, mc.numero_faltas, mc.porcentagem_frequencia , mc.tipo_integralizacao, ccd.id_componente_detalhes as id_detalhe, ccd.nome, ccd.codigo, "			
					+ "ccd.ch_total, cr_aula, cr_laboratorio, cr_estagio, sm.id_situacao_matricula, sm.descricao, cc.id_tipo_componente, cc.id_tipo_atividade, esp.descricao as especializacao, t.observacao, cc.id_disciplina, "
					+ "cc.necessitamediafinal, cc.nivel, cc.id_unidade as unidadeComponente, und.id_gestora_academica as gestoraAcademica, mc.mes, mc.ano_inicio, mc.ano_fim, mc.mes_fim, cc.id_bloco_subunidade, array_to_string(array(" +
							" select case when sin.id_formacao=25 or dex.id_formacao=25 then 'Esp.' " +
							"			  when sin.id_formacao=26 or dex.id_formacao=26 then 'MSc.' " +
							"			  when sin.id_formacao in (27,28) or dex.id_formacao in (27,28) then 'Dr.'" +
							"			  else '' end||' '||pessoa.nome||' ('||ch_dedicada_periodo||'h)'" +
							" from ensino.turma" +
							" inner join ensino.docente_turma using (id_turma)" +
							" left join rh.servidor sin on (id_docente = id_servidor)" +
							" left join ensino.docente_externo dex using (id_docente_externo)" +
							" left join comum.pessoa on (sin.id_pessoa = pessoa.id_pessoa or dex.id_pessoa = pessoa.id_pessoa)" +
							" where id_turma = t.id_turma), ', ') as docentes_turma "
					+ "from ensino.matricula_componente mc left outer join ensino.turma t on (mc.id_turma = t.id_turma) left outer join tecnico.especializacao_turma_entrada esp "
					+ "on (t.id_especializacao_turma_entrada = esp.id_especializacao_turma_entrada), "
					+ "ensino.componente_curricular cc, discente d, "
					+ "ensino.componente_curricular_detalhes ccd, "
					+ "ensino.situacao_matricula sm, " 
					+ "comum.unidade und "
					+ "where cc.id_unidade = und.id_unidade "
					+ " and mc.id_componente_curricular = cc.id_disciplina " 
					+ " and mc.id_discente = d.id_discente "
					+ " and mc.id_situacao_matricula = sm.id_situacao_matricula "
					+ " and ccd.id_componente_detalhes = cc.id_detalhe "
					+ " and d.id_discente = ? "
					+ " and sm.id_situacao_matricula in (?,?,?,?,?,?,?,?,?,?,? "
					+ (trazerCancelados ? ",?" : "")
					+ ") order by mc.ano, mc.periodo, "+ (esconderSubUnidades ? "" : " cc.id_bloco_subunidade desc, ") + "cc.codigo";

			con = Database.getInstance().getSigaaConnection();

			st = con.prepareStatement(sql);
			st.setInt(1, idDiscente);
			st.setInt(2, SituacaoMatricula.APROVADO.getId());
			st.setInt(3, SituacaoMatricula.EM_ESPERA.getId());
			st.setInt(4, SituacaoMatricula.MATRICULADO.getId());
			st.setInt(5, SituacaoMatricula.REPROVADO.getId());
			st.setInt(6, SituacaoMatricula.REPROVADO_FALTA.getId());
			st.setInt(7, SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId());
			st.setInt(8, SituacaoMatricula.TRANCADO.getId());
			st.setInt(9, SituacaoMatricula.APROVEITADO_CUMPRIU.getId());
			st.setInt(10, SituacaoMatricula.APROVEITADO_DISPENSADO.getId());
			st.setInt(11, SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId());
			st.setInt(12, SituacaoMatricula.NAO_CONCLUIDO.getId());
			
			if (trazerCancelados) {
				st.setInt(13, SituacaoMatricula.CANCELADO.getId());
			}
			rs = st.executeQuery();

			List<MatriculaComponente> result = new ArrayList<MatriculaComponente>();
			Map<Integer, Boolean> subUnidadesDevemSerEscondidas = new HashMap<Integer, Boolean>();

			while (rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				mc.setDataCadastro(rs.getDate("data_cadastro"));
				
				mc.setTurma(new Turma());
				mc.getTurma().setId(rs.getInt("id_turma"));
				mc.getTurma().setAno(rs.getInt("ano"));
				mc.getTurma().setPeriodo(rs.getInt("periodo"));
				mc.getTurma().setCodigo(rs.getString("codigo_turma"));
				mc.getTurma().setDataInicio(rs.getDate("data_inicio"));
				mc.getTurma().setDataFim(rs.getDate("data_fim"));				
				String especializacao = rs.getString("especializacao");
				
				if (especializacao != null && !"".equals(especializacao.trim())) {
					mc.getTurma().setEspecializacao(new EspecializacaoTurmaEntrada());
					mc.getTurma().getEspecializacao().setDescricao(especializacao);
				}
				mc.getTurma().setObservacao(rs.getString("observacao"));
				mc.getTurma().setNomesDocentes(rs.getString("docentes_turma"));

				mc.getTurma().setDisciplina(new ComponenteCurricular());
				mc.getTurma().getDisciplina().setNivel(rs.getString("nivel").charAt(0));
				mc.getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes(rs.getInt("id_detalhe")));
				mc.getTurma().getDisciplina().getDetalhes().setNome(rs.getString("nome"));
				mc.getTurma().getDisciplina().setCodigo(rs.getString("codigo"));
				mc.getTurma().getDisciplina().getDetalhes().setCodigo(mc.getTurma().getDisciplina().getCodigo());
				mc.getTurma().getDisciplina().getDetalhes().setChTotal(rs.getInt("ch_total"));
				mc.getTurma().getDisciplina().getDetalhes().setCrAula(rs.getInt("cr_aula"));
				mc.getTurma().getDisciplina().getDetalhes().setCrLaboratorio(rs.getInt("cr_laboratorio"));
				mc.getTurma().getDisciplina().getDetalhes().setCrEstagio(rs.getInt("cr_estagio"));
				mc.setDetalhesComponente(mc.getTurma().getDisciplina().getDetalhes());
				mc.setComponente(mc.getTurma().getDisciplina());

				mc.setMediaFinal(rs.getDouble("media_final"));
				mc.setNumeroFaltas(rs.getInt("numero_faltas"));

				mc.setSituacaoMatricula(new SituacaoMatricula());
				mc.getSituacaoMatricula().setId(rs.getInt("id_situacao_matricula"));
				mc.getSituacaoMatricula().setDescricao(rs.getString("descricao"));

				mc.setTipoIntegralizacao(rs.getString("tipo_integralizacao"));
				mc.getComponente().setTipoAtividade(new TipoAtividade());
				
				
				mc.getComponente().getUnidade().setId(rs.getInt("unidadeComponente"));
				mc.getComponente().getUnidade().setGestoraAcademica(new Unidade());
				mc.getComponente().getUnidade().getGestoraAcademica().setId(rs.getInt("gestoraAcademica"));
				
				
				
				mc.getComponente().getTipoAtividade().setId(rs.getInt("id_tipo_atividade"));
				mc.getComponente().setTipoComponente(new TipoComponenteCurricular());
				mc.getComponente().getTipoComponente().setId(rs.getInt("id_tipo_componente"));
				mc.getComponente().setId(rs.getInt("id_disciplina"));
				mc.getComponente().setNecessitaMediaFinal(rs.getBoolean("necessitamediafinal"));

				int idBlocoSubUnidade = rs.getInt("id_bloco_subunidade");
				
				if (idBlocoSubUnidade > 0) {
					mc.getComponente().setBlocoSubUnidade(new ComponenteCurricular(idBlocoSubUnidade));
				}
				mc.setAno(rs.getShort("ano"));
				mc.setPeriodo(rs.getByte("periodo"));
				
				int mes = rs.getInt("mes");
				mc.setMes(mes > 0 ? mes : null);
				
				int anoInicio = rs.getInt("ano_inicio");
				mc.setAnoInicio(anoInicio > 0 ? anoInicio : null);

				int mesFim = rs.getInt("mes_fim");
				mc.setMesFim(mesFim > 0 ? mesFim : null);

				int anoFim = rs.getInt("ano_fim");
				mc.setAnoFim(anoFim > 0 ? anoFim : null);

				ComponenteCurricular bloco = mc.getComponente().getBlocoSubUnidade();
				
				if (bloco != null) {
					int idBloco = bloco.getId();

					if (subUnidadesDevemSerEscondidas.get(idBloco) == null) {
						subUnidadesDevemSerEscondidas.put(idBloco, true);
					}
					subUnidadesDevemSerEscondidas.put(idBloco, subUnidadesDevemSerEscondidas.get(idBloco) && isSubUnidadeConcluida(mc));
				}
				result.add(mc);
			}
			if (esconderSubUnidades) {
				for (Iterator<MatriculaComponente> it = result.iterator(); it.hasNext(); ) {
					MatriculaComponente mc = it.next();
					if (mc.getComponente().getBlocoSubUnidade() != null) {
						if (subUnidadesDevemSerEscondidas.get(mc.getComponente().getBlocoSubUnidade().getId())) {
							it.remove();
						}else if ( !subUnidadesDevemSerEscondidas.get(mc.getComponente().getBlocoSubUnidade().getId()) 
								&& isSubUnidadeConcluida(mc))
							it.remove();
					}
				}
			} 
			return result;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/** Indica se a matriculaComponente é uma subunidade e se está consolidada
	 * @param mc
	 * @return
	 */
	private boolean isSubUnidadeConcluida(MatriculaComponente mc) {
		return mc.isConsolidada() && mc.getComponente().isSubUnidade();
	}

	/**
	 * Método para ser usado todas as vezes que quiser atualizar apenas um campo
	 * da entidade Discente.
	 */
	public void updateDiscente(Class<? extends PersistDB> classe, Integer id, String campo,	Object valor) throws DAOException {
		try {
			String query = HibernateUtils.createUpdateQuery(getSF(), classe, id, campo, valor);
			update(query);
		} catch (Exception e) {
			throw new DAOException(e);
		} 
	}

	/** Atualiza um campo de um Discente.
	 * @param id
	 * @param campo
	 * @param valor
	 * @throws DAOException
	 */
	public void updateDiscente(Integer id, String campo, Object valor) throws DAOException {
		try {
			updateDiscente(Discente.class, id, campo, valor);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna todos os componentes de turmas aprovadas (ou aproveitadas) de um
	 * discente
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesCurricularesConcluidos(DiscenteAdapter discente) throws DAOException {
		return findComponentesCurriculares(discente, SituacaoMatricula.getSituacoesPagas(), null);
	}

	/**
	 * Retorna todos os componentes de turmas matriculadas, aprovadas (ou
	 * aproveitadas) de um discente
	 *
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesMatriculadosConcluidos(Discente discente) throws DAOException {
		return findComponentesCurriculares(discente, SituacaoMatricula.getSituacoesPagasEMatriculadas(), null);
	}

	/**
	 * Busca todos os componentes curriculares do discente que esta com situação de matrícula e tipo de integralização definidos.
	 * 
	 * @param discente
	 * @param situacoesMatricula
	 * @param tipoIntegralizacao
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findComponentesCurriculares(DiscenteAdapter discente, 
			Collection<SituacaoMatricula> situacoesMatricula, String[] tipoIntegralizacao) throws DAOException {
		try {
			String hql = "select mc.componente.id, mc.componente.detalhes.equivalencia, mc.componente.detalhes.nome" 
					+ " from MatriculaComponente mc "
					+ "where mc.situacaoMatricula.id in  "
					+ gerarStringIn(situacoesMatricula)
					+ "and mc.discente.id = ? ";
			if (tipoIntegralizacao != null && tipoIntegralizacao.length > 0) {
				hql += " and tipo_integralizacao in " + UFRNUtils.gerarStringIn(tipoIntegralizacao);
			}
			hql += " order by mc.ano, mc.periodo, mc.componente.codigo";
			Query q = getSession().createQuery(hql);
			q.setInteger(0, discente.getId());
			@SuppressWarnings("unchecked")
			Collection<Object[]> res = q.list();
			List<ComponenteCurricular> ccs = new ArrayList<ComponenteCurricular>(0);
			if (res != null) {
				for (Object[] reg : res) {
					ComponenteCurricular cmp = new ComponenteCurricular(
							(Integer) reg[0]);
					cmp.setEquivalencia((String) reg[1]);
					cmp.setNome((String) reg[2]);
					ccs.add(cmp);
				}
			}
			return ccs;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Este método foi criado pela necessidade de retornar os tipos de
	 * integralizações no cálculo das equivalentes. Uma vez que para considerar
	 * como equivalente é necessário ter pelo menos uma matrícula marcada como
	 * equivalente na expressão
	 *
	 * @param discente
	 * @param situacoesMatricula
	 * @param tipoIntegralizacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculasComponentesCurriculares(DiscenteAdapter discente, 
			Collection<SituacaoMatricula> situacoesMatricula, String[] tipoIntegralizacao) throws DAOException {
		try {
			String hql = "select mc.id, mc.ano, mc.periodo, mc.componente.id, mc.componente.detalhes.equivalencia, mc.tipoIntegralizacao, "
					+ " mc.componente.codigo, mc.componente.detalhes.nome, mc.situacaoMatricula.id "
					+ "  from MatriculaComponente mc "
					+ "where mc.situacaoMatricula.id in  "
					+ gerarStringIn(situacoesMatricula)
					+ "and mc.discente.id = ? ";
			if (tipoIntegralizacao != null && tipoIntegralizacao.length > 0) {
				hql += " and tipo_integralizacao in "
						+ UFRNUtils.gerarStringIn(tipoIntegralizacao);
			}
			hql += " order by mc.ano, mc.periodo, mc.componente.codigo";
			Query q = getSession().createQuery(hql);
			q.setInteger(0, discente.getId());
			@SuppressWarnings("unchecked")
			Collection<Object[]> res = q.list();
			Collection<MatriculaComponente> ccs = new ArrayList<MatriculaComponente>(
					0);
			if (res != null) {
				for (Object[] reg : res) {
					MatriculaComponente mc = new MatriculaComponente();
					mc.setId((Integer) reg[0]);
					mc.setAno((Short) reg[1]);
					mc.setPeriodo((Byte) reg[2]);
					ComponenteCurricular cmp = new ComponenteCurricular((Integer) reg[3]);
					cmp.setEquivalencia((String) reg[4]);
					mc.setComponente(cmp);
					mc.setTipoIntegralizacao((String) reg[5]);
					cmp.setCodigo((String) reg[6]);
					cmp.setNome((String) reg[7]);
					mc.setSituacaoMatricula(new SituacaoMatricula((Integer) reg[8]));
					ccs.add(mc);
				}
			}
			return ccs;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca todos os componentes curriculares do discente que está com situação de matrícula definida
	 * 
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesCurriculares(DiscenteAdapter discente, SituacaoMatricula... situacoes)
			throws DAOException {
		try {
			StringBuffer hql = new StringBuffer(
					"select mc.componente.id, mc.componente.detalhes.equivalencia, mc.componente.tipoComponente.id, " +
					"mc.componente.detalhes.nome from MatriculaComponente mc "
							+ "where mc.discente.id = :discente and mc.situacaoMatricula.id in "
							+ gerarStringIn(situacoes));
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("discente", discente.getId());
			@SuppressWarnings("unchecked")
			Collection<Object[]> res = q.list();
			Collection<ComponenteCurricular> ccs = new ArrayList<ComponenteCurricular>(0);
			if (res != null) {
				for (Object[] reg : res) {
					ComponenteCurricular cmp = new ComponenteCurricular((Integer) reg[0]);
					cmp.setEquivalencia((String) reg[1]);
					cmp.getTipoComponente().setId((Integer) reg[2]);
					cmp.setNome((String) reg[3]);
					ccs.add(cmp);
				}
			}
			return ccs;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/** Limite de resultados da busca
	 * 
	 * @see #findOtimizado(Long, Long, String, String, Collection, int[], int[], int, char, boolean)
	 * 
	 */
	private static final long LIMITE_RESULTADOS = 100;
	
	/** Limite de resultados máximo da busca @see #findOtimizado(Long, Long, String, String, Collection, int[], int[], int, char, boolean)
	 * 
	 */
	private static final long LIMITE_RESULTADOS_MAXIMO = 5000;

	/** Realiza uma busca otimizada por discente
	 * @param cpf
	 * @param matricula
	 * @param nome
	 * @param nomeCurso
	 * @param cursos
	 * @param statusValidos
	 * @param tiposValidos
	 * @param unidade
	 * @param nivel
	 * @param limitarResultados
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<? extends DiscenteAdapter> findOtimizado(Long cpf, Long matricula, String nome, 
			String nomeCurso, Integer tipoNecessidadEspecial, Collection<Curso> cursos, 
			int[] statusValidos, int[] tiposValidos, int unidade, char nivel, boolean limitarResultados) 
			throws DAOException, LimiteResultadosException {

		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			con = Database.getInstance().getSigaaConnection();

			StringBuilder consulta = new StringBuilder();
			consulta.append(" SELECT d.id_discente, d.matricula, d.status, d.nivel, d.tipo, p.nome as nome_discente, "
							+ "d.ano_ingresso, d.periodo_ingresso, d.id_curso, c.nome as nome_curso, mun_curso.nome as nome_mun_curso, "
							+ "mod.id_modalidade_educacao, conv.id_convenio_academico, conv.descricao as descricao_convenio ");
			if (nivel == NivelEnsino.GRADUACAO) {
				consulta.append(" , dg.id_matriz_curricular, "
								+ "dg.id_polo, municipio_polo.nome as nome_municipio_polo, "
								+ "hab.id_habilitacao, hab.nome as nome_habilitacao, "
								+ "tur.id_turno, tur.sigla as sigla_turno, "
								+ "grau.id_grau_academico, grau.descricao as descricao_grau_academico");
			}
			if (nivel == NivelEnsino.STRICTO) {
				consulta.append(" , tc.descricao as nome_tipo_stricto, u.nome as gestora_academica ");
			}
			StringBuilder sql = new StringBuilder();
			sql.append(" FROM comum.pessoa p , discente d left join curso c on (d.id_curso = c.id_curso) "
							+ " left join comum.municipio as mun_curso on (mun_curso.id_municipio = c.id_municipio)" 
							+ " left join comum.modalidade_educacao mod on (c.id_modalidade_educacao = mod.id_modalidade_educacao)"
							+ " left join ensino.convenio_academico conv on (c.id_convenio = conv.id_convenio_academico) ");
			if (nivel == NivelEnsino.GRADUACAO) {
				sql.append(" left join graduacao.discente_graduacao dg on (d.id_discente = dg.id_discente_graduacao) "
								+ "left join graduacao.matriz_curricular matriz on (matriz.id_matriz_curricular = dg.id_matriz_curricular) "
								+ "left join graduacao.habilitacao hab on (hab.id_habilitacao = matriz.id_habilitacao) "
								+ "left join ensino.turno tur on (tur.id_turno = matriz.id_turno) "
								+ "left join ensino.grau_academico as grau on (grau.id_grau_academico = matriz.id_grau_academico) "
								+ "left join ead.polo pol on (pol.id_polo = dg.id_polo) "
								+ "left join comum.municipio municipio_polo on (municipio_polo.id_municipio = pol.id_cidade) ");
			}
			if (nivel == NivelEnsino.STRICTO) {
				sql.append(" left join stricto_sensu.tipo_curso_stricto tc on (c.id_tipo_curso_stricto = tc.id_tipo_curso_stricto) ");
			}
			sql.append(", discente d2 left join comum.unidade u on (d2.id_gestora_academica = u.id_unidade) " +
					"WHERE d.id_discente = d2.id_discente and d.id_pessoa = p.id_pessoa ");

			if (nivel != '0' && nivel != 'S' && nivel != ' ') {
				sql.append(" AND d.nivel = '" + nivel + "'");
			} else if (nivel == 'S') {
				sql.append(" AND d.nivel in ('S', 'E', 'D') ");
			}
			if (unidade > 0) {
				sql.append(" AND d.id_gestora_academica = " + unidade);
			}
			if (matricula != null) {
				sql.append(" AND d.matricula = ?");
			}
			if (nome != null) {
				sql.append(" AND " + UFRNUtils.convertUtf8UpperLatin9("p.nome_ascii")
						+ " like '" + UFRNUtils.trataAspasSimples(StringUtils.toAscii(nome.toUpperCase()))
						+ "%'");
			}
			if (nomeCurso != null) {
				sql.append(" AND " + UFRNUtils.convertUtf8UpperLatin9("c.nome_ascii") + "like '" 
						+ UFRNUtils.trataAspasSimples(StringUtils.toAscii(nomeCurso.toUpperCase())) + "%'");
			}
			if (cpf != null) {
				sql.append(" AND p.cpf_cnpj = ?");
			}
			if (tipoNecessidadEspecial != null) {
				
				if (tipoNecessidadEspecial.intValue() == 0) {
					sql.append(" AND p.id_tipo_necessidade_especial in (SELECT tne.id_tipo_necessidade_especial " +
							"FROM comum.tipo_necessidade_especial tne) ");
					
				} else {
					sql.append(" and p.id_tipo_necessidade_especial = " + tipoNecessidadEspecial.intValue());
				}
			}
			if (cursos != null && !cursos.isEmpty()) {
				sql.append(" AND c.id_curso IN " + gerarStringIn(cursos));
			}
			if (statusValidos != null && statusValidos.length != 0) {
				sql.append(" AND d.status IN " + gerarStringIn(statusValidos));
			}
			if (tiposValidos != null && tiposValidos.length != 0) {
				sql.append(" AND d.tipo IN " + gerarStringIn(tiposValidos));
			}

			// Contar resultados
			st = con.prepareStatement(" SELECT COUNT(d.id_discente) as total " + sql);
			setarParametrosDiscente(st, matricula, nome, nomeCurso, cpf);
			rs = st.executeQuery();

			long total;
			if (rs.next()) {
				total = rs.getLong("TOTAL");
				long limiteResultados = limitarResultados ? LIMITE_RESULTADOS : LIMITE_RESULTADOS_MAXIMO;
				if (total > limiteResultados) {
					throw new LimiteResultadosException(
							"O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
				}
			}
			sql.append(" ORDER BY ");
			if (nivel == NivelEnsino.GRADUACAO) {
				sql.append(" d.id_curso, dg.id_matriz_curricular, municipio_polo.nome, dg.id_polo, p.nome_ascii ");
			} else {
				sql.append(" d.id_curso, c.nome, p.nome_ascii");
			}

			// Contar resultados
			st = con.prepareStatement(consulta.toString() + sql.toString());
			setarParametrosDiscente(st, matricula, nome, nomeCurso, cpf);
			
			rs = st.executeQuery();

			Collection<DiscenteAdapter> discentes = new ArrayList<DiscenteAdapter>();
			while (rs.next()) {
				DiscenteAdapter discente = null;
				if (nivel == NivelEnsino.GRADUACAO) {
					discente = new DiscenteGraduacao();
				} else {
					discente = new Discente();
				}

				discente.setId(rs.getInt("ID_DISCENTE"));
				discente.setMatricula(rs.getLong("MATRICULA"));
				discente.setStatus(rs.getInt("STATUS"));
				discente.setAnoIngresso(rs.getInt("ANO_INGRESSO"));
				discente.setPeriodoIngresso(rs.getInt("PERIODO_INGRESSO"));
				discente.setStatus(rs.getInt("STATUS"));
				discente.setNivel(rs.getString("NIVEL").charAt(0));
				discente.setTipo(rs.getInt("TIPO"));
				discente.getPessoa().setNome(rs.getString("NOME_DISCENTE"));

				discente.setCurso(new Curso());
				discente.getCurso().setNome(rs.getString("NOME_CURSO"));
				discente.getCurso().setId(rs.getInt("ID_CURSO"));
				discente.getCurso().setMunicipio(new Municipio());
				discente.getCurso().getMunicipio().setNome(rs.getString("NOME_MUN_CURSO"));
				discente.getCurso().setModalidadeEducacao(new ModalidadeEducacao(rs.getInt("ID_MODALIDADE_EDUCACAO")));
				if (rs.getString("ID_CONVENIO_ACADEMICO") != null){
					discente.getCurso().setConvenio(new ConvenioAcademico(rs.getInt("ID_CONVENIO_ACADEMICO")));
					discente.getCurso().getConvenio().setDescricao(rs.getString("DESCRICAO_CONVENIO"));
				}	

				if (nivel == NivelEnsino.GRADUACAO) {
					DiscenteGraduacao dg = (DiscenteGraduacao) discente;
					dg.getDiscente().setId(dg.getId());
					if (rs.getString("ID_POLO") != null) {
						dg.setPolo(new Polo(rs.getInt("ID_POLO")));
						dg.getPolo().setCidade(new Municipio());
						dg.getPolo().getCidade().setNome(rs.getString("NOME_MUNICIPIO_POLO"));
					}
					MatrizCurricular matriz = new MatrizCurricular(rs.getInt("ID_MATRIZ_CURRICULAR"));
					// nome_habilitacao, sigla_turno, descricao_grau_academico
					if (rs.getString("ID_HABILITACAO") != null) {
						matriz.setHabilitacao(new Habilitacao(rs.getInt("ID_HABILITACAO")));
						matriz.getHabilitacao().setNome(rs.getString("NOME_HABILITACAO"));
					}
					if (rs.getString("ID_TURNO") != null) {
						matriz.setTurno(new Turno(rs.getInt("ID_TURNO")));
						matriz.getTurno().setSigla(rs.getString("SIGLA_TURNO"));
					}
					if (rs.getString("ID_GRAU_ACADEMICO") != null) {
						matriz.setGrauAcademico(new GrauAcademico(rs.getInt("ID_GRAU_ACADEMICO")));
						matriz.getGrauAcademico().setDescricao(rs.getString("DESCRICAO_GRAU_ACADEMICO"));
					}
					dg.setMatrizCurricular(matriz);
				}
				if (nivel == NivelEnsino.STRICTO) {
					discente.getCurso().setTipoCursoStricto(new TipoCursoStricto());
					discente.getCurso().getTipoCursoStricto().setDescricao(rs.getString("NOME_TIPO_STRICTO"));
					discente.setGestoraAcademica(new Unidade());
					discente.getGestoraAcademica().setNome(rs.getString("GESTORA_ACADEMICA"));
				}
				discentes.add(discente);
			}
			return discentes;

		} catch (LimiteResultadosException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/** Seta parâmetros utilizados no método  {@link #findOtimizado(Long, Long, String, String, 
	 * Collection, int[], int[], int, char, boolean)}
	 * @param st
	 * @param matricula
	 * @param nome
	 * @param nomeCurso
	 * @param cpf
	 * @throws SQLException
	 */
	private void setarParametrosDiscente(PreparedStatement st, Long matricula, String nome, 
			String nomeCurso, Long cpf) throws SQLException {
		int i = 1;
		if (matricula != null) {
			st.setLong(i++, matricula);
		}
		if (cpf != null) {
			st.setLong(i++, cpf);
		}
	}

	/**
	 * Retorna a lista de disciplinas pendentes. Passa a lista de disciplinas 
	 * (concluídas e matriculadas), equivalência e nível de ensino
	 * 
	 * @param idDiscente
	 * @param disciplinas
	 * @param equivalenciasDiscente 
	 * @param equivalenciasDiscente
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByDisciplinasCurricularesPendentes(int idDiscente, List<MatriculaComponente> disciplinas, List<TipoGenerico> equivalenciasDiscente) throws DAOException {
		Collection<ComponenteCurricular> componentesPendentes = findComponentesDaGradeQueDiscenteNaoPagou(idDiscente, disciplinas, true);
		IntegralizacoesHelper.analisarEquivalenciasPorExtenso(idDiscente, disciplinas, equivalenciasDiscente, componentesPendentes);
		return componentesPendentes;
	}

	/**
	 * Retorna todos os compoenntes que da grade do aluno que ele não pagou
	 * 
	 * @param idDiscente
	 * @param disciplinas
	 * @param somenteObrigatorias
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesDaGradeQueDiscenteNaoPagou(int idDiscente, List<MatriculaComponente> disciplinas, boolean somenteObrigatorias) throws DAOException {
		ComponenteCurricularDao ccdao = new ComponenteCurricularDao();
		try {
			
			// conjunto de componentes que serão usados na busca por equivalencia
			// conjunto de componentes que se encontrão com status de matriculado
			List<Integer> idsMatriculadas = new ArrayList<Integer>();
			for (MatriculaComponente mc : disciplinas) {
				if (mc.getSituacaoMatricula().equals(SituacaoMatricula.MATRICULADO)) 
					idsMatriculadas.add(mc.getComponente().getId());
				
			}
			
			Query q = getSession()
					.createQuery("select cc.id, det.codigo, det.nome, det.chTotal, det.equivalencia, ac.id "
									+ "from Discente d, CurriculoComponente cu left join cu.areaConcentracao ac, ComponenteCurricular cc left join cc.detalhes det "
									+ "where d.curriculo.id = cu.curriculo.id "
									+ "and cu.componente.id = cc.id and d.id = :idDiscente and cu.obrigatoria = :obrig and cc.id not in "
									+ "(select mc.componente.id from MatriculaComponente mc where "
									+ "mc.discente.id = :idDiscente and mc.situacaoMatricula.id in (:aprovado,:cumpriu,:dispensado,:transferido)) "									
									+ "order by cu.semestreOferta asc");
			q.setInteger("idDiscente", idDiscente);
			q.setInteger("aprovado", SituacaoMatricula.APROVADO.getId());
			q.setInteger("cumpriu", SituacaoMatricula.APROVEITADO_CUMPRIU.getId());
			q.setInteger("dispensado", SituacaoMatricula.APROVEITADO_DISPENSADO.getId());
			q.setInteger("transferido", SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId());
			q.setBoolean("obrig", somenteObrigatorias);
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();

			// Verificar o nível de ensino do discente para validação da área de concentração dos componentes. Apenas discentes de stricto sensu passam pela validação. 
			char nivel = getJdbcTemplate().queryForChar("select nivel from discente where id_discente = ?", idDiscente);
			if (NivelEnsino.isAlgumNivelStricto(nivel)) {
				int idAreaDiscente = getJdbcTemplate().queryForInt("select coalesce(id_area_concentracao, 0) from stricto_sensu.discente_stricto where id_discente = ?", idDiscente);
				
				if (!isEmpty(idAreaDiscente)) {
					
					// Iterar na lista de componentes pendentes e comparar a área de concentração do componente com a do discente
					for (Iterator<Object[]> it = result.iterator(); it.hasNext(); ) {
						Object[] linha = it.next();
						if (linha[5] != null) {
							Integer idAreaComponente = (Integer) linha[5];
							// Remover as obrigatórias que não são da área de concentração do discente.
							if (idAreaComponente != idAreaDiscente) {
								it.remove();
							}
						}
					}
					
				}
			}
			
			String sql = "select id_disciplina, id_bloco_subunidade from ensino.componente_curricular where id_bloco_subunidade in ("
				+ "select id_disciplina from graduacao.curriculo_componente cu, ensino.componente_curricular cc, discente d "
				+ "where cu.id_componente_curricular = cc.id_disciplina and cu.id_curriculo = d.id_curriculo "
				+ "and cu.obrigatoria = trueValue() and cc.id_tipo_componente = 4 and d.id_discente = ?)";
			@SuppressWarnings("unchecked")
			Map<Integer, Integer> subUnidades = (Map<Integer, Integer>) getJdbcTemplate().query(
					sql, new Object[] { idDiscente }, MAP_EXTRACTOR);

			ccdao.setSession(getSession());
			
			List<ComponenteCurricular> pendentes = new ArrayList<ComponenteCurricular>();
			for (Object[] linha : result) {
				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setDetalhes(new ComponenteDetalhes());
				cc.setId((Integer) linha[0]);
				cc.setCodigo((String) linha[1]);
				cc.getDetalhes().setCodigo(cc.getCodigo());
				cc.getDetalhes().setNome((String) linha[2]);
				cc.getDetalhes().setChTotal((Integer) linha[3]);
				cc.getDetalhes().setEquivalencia((String) linha[4]);

				if (idsMatriculadas.contains(cc.getId())) {
					cc.setMatriculado(true);
				}

				// Verificar se está matriculado em uma sub-unidade para marcar o bloco como "matriculado"
				if (subUnidades.containsValue(cc.getId())) {
					for (Entry<Integer, Integer> entry : subUnidades.entrySet()) {
						if (entry.getValue() == cc.getId()) {
							if (idsMatriculadas.contains(entry.getKey())) {
								cc.setMatriculado(true);
								break;
							}
						}
					}
				}
				
				pendentes.add(cc);
			}
			
			return pendentes;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		} finally {
			if( ccdao != null ) ccdao.close();
		}
	}
	
	/**
	 * Busca o componente curricular pelo Id.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricular findComponenteNomeChById(int id) throws DAOException {
		Query q = getSession().createQuery("select cc.id, cc.detalhes.codigo, cc.detalhes.nome, " +
				"cc.detalhes.chTotal from ComponenteCurricular cc where cc.id=?");
		q.setInteger(0, id);
		Object[] result = (Object[]) q.uniqueResult();

		if (result != null) {
			ComponenteCurricular componente = new ComponenteCurricular();
			componente.setId((Integer) result[0]);
			componente.setCodigo((String) result[1]);
			componente.setDetalhes(new ComponenteDetalhes());
			componente.getDetalhes().setCodigo(componente.getCodigo());
			componente.getDetalhes().setNome((String) result[2]);
			componente.getDetalhes().setChTotal((Integer) result[3]);
			return componente;
		}
		return null;
	}


	/**
	 * Retorna os discente do curso ou programa informado que possuem solicitação de
	 * trancamento de matrícula pendentes.
	 *
	 * Curso ou unidade devem ser nulos! Apenas um parâmetro destes dois podem ser não nulos.
	 * @param curso 
	 * @param programa
	 * @param portal - informa se a consulta é destinada ao portal do coordenador
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findBySolicitacoesTrancamentoPendentes(Curso curso, Unidade programa, int ano, int periodo, boolean portal) throws DAOException {

		StringBuffer hql = new StringBuffer();

		hql	.append(" SELECT DISTINCT new Discente(matriculaComponente.discente.id,matriculaComponente.discente.matricula,"
			+ " matriculaComponente.discente.pessoa.nome, matriculaComponente.discente.status, matriculaComponente.discente.nivel) "
			+ " FROM SolicitacaoTrancamentoMatricula stm ");
		hql.append(" WHERE stm.situacao = :situacao ");
		
		if( ano > 0 ) {
			hql.append(" AND stm.matriculaComponente.ano = :ano ");
		}
		if( periodo > 0 ) {
			hql.append(" AND stm.matriculaComponente.periodo = :periodo ");
		}
		
		if( curso != null ){
			hql.append(" AND stm.matriculaComponente.discente.curso.id = :idCurso ");
		} else if( programa != null ){
			hql.append(" AND stm.matriculaComponente.discente.gestoraAcademica.id = :idUnidade ");
		}
		
		hql.append(" ORDER BY matriculaComponente.discente.pessoa.nome ");
		

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("situacao", SolicitacaoTrancamentoMatricula.SOLICITADO);
		
		if( curso != null ){
			q.setInteger("idCurso", curso.getId());
		} else if( programa != null ){
			q.setInteger("idUnidade", programa.getId());
		} else
			throw new IllegalArgumentException();
		
		if( ano > 0 ) {
			q.setInteger("ano", ano);
		}
		if( periodo > 0 ) {
			q.setInteger("periodo", periodo);
		}
		// Se a consulta é destinada ao portal do coordenador, restringe o
		// número de resultados
		if (portal) {
			q.setMaxResults(5);
		}

		@SuppressWarnings("unchecked")
		Collection<Discente> lista = q.list();
		return lista;

	}

	/**
	 * Retorna as solicitações de trancamento pendentes realizada por alunos
	 * especiais (que não tem curso).
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findBySolicitacoesTrancamentoPendentesEspecial(
			Character... nivel) throws DAOException {
		StringBuffer hql = new StringBuffer();

		hql
				.append(" SELECT DISTINCT new Discente(matriculaComponente.discente.id,matriculaComponente.discente.matricula,"
						+ " matriculaComponente.discente.pessoa.nome, matriculaComponente.discente.status) "
						+ " FROM SolicitacaoTrancamentoMatricula stm ");
		hql.append(" WHERE stm.situacao = :situacao ");
		hql.append(" AND stm.matriculaComponente.discente.tipo = :tipo ");
		if (!isEmpty(nivel)) {
			hql.append(" AND stm.matriculaComponente.discente.nivel in "
					+ gerarStringIn(nivel));
		}
		hql.append(" ORDER BY matriculaComponente.discente.pessoa.nome ");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("situacao", SolicitacaoTrancamentoMatricula.SOLICITADO);
		q.setInteger("tipo", Discente.ESPECIAL);

		@SuppressWarnings("unchecked")
		Collection<Discente> lista = q.list();
		return lista;
	}


	/**
	 * Retorna os dados de reconhecimento sobre o curso que o discente faz.
	 * 
	 * @param id - ID do discente
	 * @return
	 * @throws DAOException
	 */
	public Object[] findDadosReconhecimentoCurso(int id) throws DAOException {
		String sql = "select re.portaria_decreto, re.data_decreto, re.data_publicacao,"
				+ " mc.id_enfase, mc.id_habilitacao, mc.id_curso, mc.id_grau_academico, mc.id_turno "
				+ "from graduacao.matriz_curricular mc, graduacao.reconhecimento re, graduacao.discente_graduacao dg "
				+ "where dg.id_matriz_curricular = mc.id_matriz_curricular and re.id_matriz = mc.id_matriz_curricular "
				+ "and dg.id_discente_graduacao=? order by re.data_decreto desc " + BDUtils.limit(1);

		SQLQuery q = getSession().createSQLQuery(sql);
		q.setInteger(0, id);
		
		Object[] result = (Object[]) q.uniqueResult();
		
		// se a matriz possui ênfase, buscar a equivalente sem ênfase
		if (result != null && result[3] != null) {
			String sqlEquivalente = "select re.portaria_decreto, re.data_decreto, re.data_publicacao"
					+ " from graduacao.matriz_curricular mc, graduacao.reconhecimento re"
					+ " where re.id_matriz = mc.id_matriz_curricular "
					+ " and mc.id_curso = :idCurso"
					+ " and mc.id_grau_academico = :idGrauAcademico"
					+ " and mc.id_turno = :idTurno"
					+ (result[4] != null ? " and mc.id_habilitacao = :idHabilitacao" : "")
					+ " and mc.id_enfase is null"
					+ " order by re.data_decreto desc " + BDUtils.limit(1);
				Query qEquivalente = getSession().createSQLQuery(sqlEquivalente);
				qEquivalente.setInteger("idCurso", (Integer) result[5]);
				qEquivalente.setInteger("idGrauAcademico", (Integer) result[6]);
				qEquivalente.setShort("idTurno", (Short) result[7]);
				if (result[4] != null)
					qEquivalente.setInteger("idHabilitacao", (Integer) result[4]);
				Object[] equivalente = (Object[]) qEquivalente.uniqueResult();
				result = equivalente;
		}
		return result;
	}

	/**
	 * Retorna os dados sobre a autorização do curso que o discente faz.
	 * 
	 * @param id - ID do discente
	 * @return
	 * @throws DAOException
	 */
	public Object[] findDadosAutorizacaoCurso(int id) throws DAOException {
		String sql = "select mc.autorizacao_ato_normativo, mc.autorizacao_ato_data, mc.autorizacao_publicacao "
				+ "from graduacao.discente_graduacao dg, graduacao.matriz_curricular mc "
				+ "where dg.id_matriz_curricular = mc.id_matriz_curricular and dg.id_discente_graduacao = ? "
				+ "order by mc.autorizacao_ato_data desc " + BDUtils.limit(1);

		SQLQuery q = getSession().createSQLQuery(sql);
		q.setInteger(0, id);

		return (Object[]) q.uniqueResult();
	}


	/**
	 * Retorna a lista de matrículas de um discente.
	 *
	 * @param discente
	 *            Discente
	 * @param completo
	 *            Se a listagem vai ser de todos os anos-períodos ou apenas de
	 *            um
	 * @param ano
	 *            O ano selecionado
	 * @param periodo
	 *            O Período selecionado
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaComponente> findRelatorioNotasAluno(DiscenteAdapter discente,
			boolean completo, int ano, int periodo) throws DAOException {

		try {

			Collection<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>(0);
			situacoes.add(SituacaoMatricula.APROVADO);
			situacoes.add(SituacaoMatricula.REPROVADO);
			situacoes.add(SituacaoMatricula.REPROVADO_FALTA);
			situacoes.add(SituacaoMatricula.REPROVADO_MEDIA_FALTA);

			situacoes.add(SituacaoMatricula.MATRICULADO);
			List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();

			StringBuilder sb = new StringBuilder(
					"select mc.id, mc.componente.id as idComponente, mc.componente.codigo, mc.componente.detalhes.nome, mc.mediaFinal, mc.numeroFaltas, "
							+ "n, mc.situacaoMatricula.descricao, mc.componente.unidade.gestoraAcademica.id, mc.componente.nivel, mc.ano, mc.periodo, mc.situacaoMatricula.id, mc.recuperacao, "
							+ "mc.discente.id, mc.componente.id, mc.ano, mc.periodo, t.idPolo, mc.componente.detalhes.chTotal, mc.turma.id "
							+ "from MatriculaComponente mc left outer join mc.notas n left join fetch n.avaliacoes aval left outer join mc.turma t  "
							+ "where (n.id is null or n.ativo = trueValue()) and mc.discente.id = ? and mc.situacaoMatricula.id in "
							+ gerarStringIn(situacoes));

			if (completo) {
				sb.append(" and mc.ano = ? and mc.periodo = ? ");
			}

			sb.append("order by mc.ano desc, mc.periodo desc, mc.componente.detalhes.nome, n.unidade asc");

			Query q = getSession().createQuery(sb.toString());
			q.setInteger(0, discente.getId());

			if (completo) {
				q.setInteger(1, ano);
				q.setInteger(2, periodo);
			}

			List<?> result = q.list();
			for (Object name : result) {
				Object[] linha = (Object[]) name;
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId((Integer) linha[0]);

				if (!matriculas.contains(mc)) {
					mc.setComponente(new ComponenteCurricular());
					mc.getComponente().setDetalhes(new ComponenteDetalhes());

					mc.getComponente().setId((Integer) linha[1]);
					mc.getComponente().setCodigo((String) linha[2]);
					mc.getComponente().getDetalhes().setNome((String) linha[3]);
					mc.setMediaFinal((Double) linha[4]);
					mc.setNumeroFaltas((Integer) linha[5]);
					mc.setSituacaoMatricula(new SituacaoMatricula());
					mc.getSituacaoMatricula().setId((Integer) linha[12]);
					mc.getSituacaoMatricula().setDescricao((String) linha[7]);
					mc.setNotas(new ArrayList<NotaUnidade>());
					mc.getComponente().setUnidade(new Unidade());
					mc.getComponente().setNivel((Character) linha[9]);
					mc.setAno((Short) linha[10]);
					mc.setPeriodo((Byte) linha[11]);
					mc.setRecuperacao((Double) linha[13]);
					mc.setDiscente(new Discente((Integer) linha[14]));
					mc.setTurma(new Turma());
					mc.getTurma().setDisciplina(
							new ComponenteCurricular((Integer) linha[15]));
					mc.getTurma().setId(linha[20] != null ? (Integer) linha[20] : 0);
					mc.getTurma().setAno(linha[16] != null ? (Short) linha[16] : 0);
					mc.getTurma().setPeriodo(linha[17] != null ? (Byte) linha[17] : 0);

					if (linha[18] != null) {
						mc.getTurma().setPolo(new Polo((Integer) linha[18]));
					}

					mc.getComponente().getDetalhes().setChTotal((Integer) linha[19]);
					
					matriculas.add(mc);
				} else {
					mc = matriculas.get(matriculas.indexOf(mc));
				}

				if (linha[6] != null) {
					NotaUnidade nu = (NotaUnidade) linha[6];
					nu.setMatricula(mc);
					
					if (!mc.getNotas().contains(nu))
						mc.getNotas().add(nu);
				}
			}

			return matriculas;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna os componentes pendentes do discente.
	 * 
	 * @param discente
	 * @param somenteDoCurriculo
	 * @param somenteObrigatorias
	 * @return
	 * @throws DAOException
	 */
	public Collection<CurriculoComponente> findComponentesPendentesByDiscente(DiscenteAdapter discente, boolean somenteDoCurriculo, boolean somenteObrigatorias) throws DAOException {
		// se o discente for especial, não há disciplinas pendentes;
		if (!discente.isRegular())
			return new ArrayList<CurriculoComponente>();
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT cc.obrigatoria, cur.id_curriculo, cur.id_curso, comp.id_disciplina, comp.id_tipo_componente, "
					+ "det.equivalencia, det.ch_total, (det.cr_aula + det.cr_laboratorio + det.cr_estagio + cr_ead) as cr_total, det.codigo, det.nome "
					+ " FROM graduacao.curriculo_componente cc join graduacao.curriculo cur on (cc.id_curriculo = cur.id_curriculo) "
					+ "join ensino.componente_curricular comp on (cc.id_componente_curricular = comp.id_disciplina),"
					+ "ensino.componente_curricular_detalhes det "
					+ " WHERE comp.id_detalhe = det.id_componente_detalhes AND cur.id_curso = ? "
					+ " AND comp.id_disciplina not in "
					+ "(SELECT mc.id_componente_curricular "
					+ " FROM ensino.matricula_componente mc "
					+ " WHERE mc.id_discente = ? "
					+ "and mc.id_situacao_matricula in "
					+ gerarStringIn(SituacaoMatricula.getSituacoesPagas())
					+ ") "
					+ (somenteDoCurriculo ? " and cc.id_curriculo = "
							+ discente.getCurriculo().getId() : "")
					+ (somenteObrigatorias ? " and cc.obrigatoria = trueValue()" : "");

			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			st.setInt(1, discente.getCurso().getId());
			st.setInt(2, discente.getId());
			rs = st.executeQuery();

			List<CurriculoComponente> curriculosComponentes = new ArrayList<CurriculoComponente>(
					0);
			while (rs.next()) {
				CurriculoComponente cc = new CurriculoComponente();
				cc.setObrigatoria(rs.getBoolean("obrigatoria"));

				Curriculo c = new Curriculo();
				c.setId(rs.getInt("id_curriculo"));
				c.setCurso(new Curso(rs.getInt("id_curso")));
				cc.setCurriculo(c);

				ComponenteCurricular comp = new ComponenteCurricular(rs.getInt("id_disciplina"));
				comp.setTipoComponente(new TipoComponenteCurricular(rs.getInt("id_tipo_componente")));
				comp.setEquivalencia(rs.getString("equivalencia"));
				comp.setChTotal(rs.getInt("ch_total"));
				comp.getDetalhes().setCrTotal(rs.getInt("cr_total"));
				comp.setNome(rs.getString("nome"));
				comp.setCodigo(rs.getString("codigo"));
				cc.setComponente(comp);
				curriculosComponentes.add(cc);
			}

			return curriculosComponentes;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Retorna as movimentações do tipo especificado que o aluno possui.
	 * 
	 * @see TipoMovimentacaoAluno
	 * @param discente
	 * @param tipos
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findProrrogacoesByDiscente(
			DiscenteAdapter discente, int... tipos) throws DAOException {
		try {
			Query q = getSession()
					.createQuery(
							"SELECT id as id, valorMovimentacao as valorMovimentacao FROM MovimentacaoAluno ma where ma.discente.id=? and "
									+ " ma.ativo=trueValue() and ma.tipoMovimentacaoAluno.id in "
									+ gerarStringIn(tipos));
			q.setInteger(0, discente.getId());
			@SuppressWarnings("unchecked")
			Collection<MovimentacaoAluno> lista = q.setResultTransformer(
					Transformers.aliasToBean(MovimentacaoAluno.class)).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os trancamentos do discente.
	 * 
	 * @param discente
	 * @param otimizado
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findTrancamentosByDiscente(
			DiscenteAdapter discente, Boolean otimizado) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" FROM MovimentacaoAluno ma where ma.discente.id=? and ma.tipoMovimentacaoAluno.id=? and ma.ativo=trueValue() order by ma.anoReferencia, ma.periodoReferencia");
			if (otimizado != null && otimizado) {
				hql.insert(0, "SELECT id as id ");
			}

			Query q = getSession().createQuery(hql.toString());
			q.setInteger(0, discente.getId());
			q.setInteger(1, TipoMovimentacaoAluno.TRANCAMENTO);
			if (otimizado != null && otimizado) {
				@SuppressWarnings("unchecked")
				Collection<MovimentacaoAluno> lista = q.setResultTransformer(
						Transformers.aliasToBean(MovimentacaoAluno.class)).list();
				return lista;
			} else {
				@SuppressWarnings("unchecked")
				Collection<MovimentacaoAluno> lista = q.list();
				return lista;
			}
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todos os trancamentos do discente.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<MovimentacaoAluno> findTrancamentosByDiscente(
			DiscenteAdapter discente) throws DAOException {
		return findTrancamentosByDiscente(discente, false);
	}


	/**
	 * Retorna as observações do discente.
	 * 
	 * @see ObservacaoDiscente
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public List<ObservacaoDiscente> findObservacoesDiscente(DiscenteAdapter discente)
			throws DAOException {
		try {
			Query q = getSession()
					.createQuery(
							" from ObservacaoDiscente observacao left join fetch observacao.movimentacao mov" +
							" where observacao.discente.id = ? and observacao.ativo = trueValue() and (mov is null or mov.ativo = trueValue()) " +
							" order by observacao.data asc");
			q.setInteger(0, discente.getId());
			@SuppressWarnings("unchecked")
			List<ObservacaoDiscente> result = q.list();
			for (ObservacaoDiscente obs : result) {
				obs.setObservacao(StringUtils.divideEmLinhas(obs
						.getObservacao().replaceAll("\n", "").replaceAll("\r",
								""), 150));
			}
			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca Discentes que tenham o mesmo nível acadêmico e dados pessoais.
	 * 
	 * @see #findByDadosPessoaisMesmoNivel(Discente)
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteAdapter> findByDadosPessoaisMesmoNivel(DiscenteAdapter discente)
			throws DAOException {
		return findByDadosPessoais(discente, discente.getNivel());
	}

	/** 
	 * Encontra um Discente pela chave primária.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public DiscenteAdapter findByPK(int id) throws DAOException {
		return findDiscenteAdapterById(id);
	}

	/** 
	 * Busca uma Discentes, que não o especificado, pelos seguintes critérios: mesma referência à Pessoa
	 * ou (nome do discente, nome da mãe, e data de Nascimento iguais) ou mesmo CPF.
	 * 
	 * @param discente
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteAdapter> findByDadosPessoais(DiscenteAdapter discente,
			char nivel) throws DAOException {
		try {
			boolean subClasse = false;
			String tipoDiscente = "Discente";
			if (nivel == NivelEnsino.GRADUACAO) {
				tipoDiscente += "Graduacao";
				subClasse = true;
			} else if (nivel == NivelEnsino.TECNICO) {
				tipoDiscente += "Tecnico";
				subClasse = true;
			} else if (nivel == NivelEnsino.LATO) {
				tipoDiscente += "Lato";
				subClasse = true;
			} else if (NivelEnsino.isAlgumNivelStricto(nivel)) {
				tipoDiscente += "";
			} else if (NivelEnsino.isResidenciaMedica(nivel)){
				tipoDiscente += "ResidenciaMedica";
				subClasse = true;
			}

			String niveis = "('" + nivel + "')";
			if (NivelEnsino.isAlgumNivelStricto(nivel)) {
				niveis = "('" + NivelEnsino.MESTRADO + "', '"
						+ NivelEnsino.DOUTORADO + "')";
			}

			StringBuilder hql = new StringBuilder();
			hql.append("FROM " + tipoDiscente + " "
					+ "WHERE id <> :discente and "+ (subClasse ? "discente." : "") +"nivel in " + niveis
					+ " and (("+ (subClasse ? "discente." : "") +"pessoa.id = :pessoa and "
					+(subClasse ? "discente." : "") +"pessoa.id <> 0) or ("
					+ UFRNUtils.convertUtf8UpperLatin9((subClasse ? "discente." : "") +"pessoa.nome")
					+ " = :nomePessoa and "
					+ UFRNUtils.convertUtf8UpperLatin9((subClasse ? "discente." : "") +"pessoa.nomeMae")
					+ " = :nomeMae and "
					+ (subClasse ? "discente." : "") +"pessoa.dataNascimento = :nascimento)");
			if ( !isEmpty(discente.getPessoa().getCpf_cnpj()) ) {
				hql.append("or ("+ (subClasse ? "discente." : "") +"pessoa.cpf_cnpj > 0 and "+ (subClasse ? "discente." : "") +"pessoa.cpf_cnpj = :cpf)");
			}else if (  discente.getPessoa().isInternacional() && !isEmpty(discente.getPessoa().getPassaporte()) ) {
				hql.append("or ("+ (subClasse ? "discente." : "") +"pessoa.passaporte is not null and "+ (subClasse ? "discente." : "") +"pessoa.passaporte = :passaporte)");
			}
			hql.append(")");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("discente", discente.getId());
			q.setInteger("pessoa", discente.getPessoa().getId());
			q.setString("nomePessoa", UFRNUtils.convertUtf8UpperLatin9("'"
					+ discente.getNome() + "'"));
			q.setString("nomeMae", UFRNUtils.convertUtf8UpperLatin9("'"
					+ discente.getPessoa().getNomeMae() + "'"));
			if ( !isEmpty(discente.getPessoa().getCpf_cnpj()) ) {
				q.setLong("cpf", discente.getPessoa().getCpf_cnpj());
			}else if ( discente.getPessoa().isInternacional() && !isEmpty(discente.getPessoa().getPassaporte()) ) {
				q.setString("passaporte", discente.getPessoa().getPassaporte());
			}
			q.setDate("nascimento", discente.getPessoa().getDataNascimento());
			@SuppressWarnings("unchecked")
			Collection<DiscenteAdapter> lista = q.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	
	/**
	 * Busca os quantitativos dos diversos tipos de alunos matriculados.
	 * Utilizado no relatório do portal do planejamento.
	 *
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public TreeMap<String, Map<String, Integer>> findQuantitativoAlunosMatriculadosPlanejamento(
			int ano, int periodo) throws DAOException {
		TreeMap<String, Map<String, Integer>> result = new TreeMap<String, Map<String, Integer>>();
		TreeMap<String, Integer> grad = new TreeMap<String, Integer>();
		TreeMap<String, Integer> posgrad = new TreeMap<String, Integer>();
		TreeMap<String, Integer> tec = new TreeMap<String, Integer>();

		JdbcTemplate tmp = getJdbcTemplate();
		Object[] parametros = new Object[] { ano, periodo };

		tec.put("Alunos matriculados no ensino técnico",
						tmp.queryForInt("select count(distinct m.id_discente) "
												+ "from ensino.matricula_componente m, discente d, curso c "
												+ "where m.id_discente = d.id_discente and d.id_curso = c.id_curso "
												+ "and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " "
												+ "and m.ano = ? and m.periodo = ? and d.nivel = '" + NivelEnsino.TECNICO + "'",
												parametros));

		grad.put("Alunos matriculados na graduação presencial",
						tmp.queryForInt("select count(distinct m.id_discente) "
												+ "from ensino.matricula_componente m, discente d, curso c "
												+ "where m.id_discente = d.id_discente and d.id_curso = c.id_curso "
												+ "and c.id_modalidade_educacao =  " + ModalidadeEducacao.PRESENCIAL + " "
												+ "and c.id_convenio is null "
												+ "and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " "
												+ "and m.ano = ? and m.periodo = ? and d.nivel = '" + NivelEnsino.GRADUACAO + "'",
												parametros));

		grad.put("Alunos matriculados na educação a distância",
						tmp.queryForInt("select count(distinct m.id_discente) "
												+ "from ensino.matricula_componente m, discente d, curso c "
												+ "where m.id_discente = d.id_discente and d.id_curso = c.id_curso "
												+ "and c.id_modalidade_educacao = " + ModalidadeEducacao.A_DISTANCIA + " "
												+ "and c.id_convenio is null "
												+ "and m.ano = ? and m.periodo = ? "
												+ "and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " "
												+ "and d.nivel = '" + NivelEnsino.GRADUACAO + "'",
												parametros));

		grad.put("Alunos matriculados na probásica",
						tmp.queryForInt("select count(distinct m.id_discente) "
												+ "from ensino.matricula_componente m, discente d, curso c "
												+ "where m.id_discente = d.id_discente and d.id_curso = c.id_curso "
												+ "and c.id_convenio = 1 "
												+ "and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " "
												+ "and m.ano = ? and m.periodo = ? "
												+ " and d.nivel = '" + NivelEnsino.GRADUACAO + "'",
										parametros));

		grad.put("Alunos Especiais",
						tmp.queryForInt("select count(distinct m.id_discente) "
												+ "from ensino.matricula_componente m, discente d "
												+ "where m.id_discente = d.id_discente "
												+ "and d.tipo = " + Discente.ESPECIAL + " "
												+ "and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " "
												+ "and m.ano = ? and m.periodo = ? "
												+ " and d.nivel = '" + NivelEnsino.GRADUACAO + "'",
										parametros));

		posgrad.put("Alunos matriculados nos cursos de especialização",
						tmp.queryForInt("select count(distinct m.id_discente) "
										+ "from ensino.matricula_componente m, discente d, curso c "
										+ "where m.id_discente = d.id_discente and d.id_curso = c.id_curso "
										+ "and m.ano = ? and m.periodo = ? "
										+ "and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " "
										+ "and c.id_convenio is null "
										+ "and d.nivel = '" + NivelEnsino.LATO + "'", parametros));

		posgrad.put("Alunos matriculados nos cursos de mestrado",
						tmp.queryForInt("select count(distinct m.id_discente) "
												+ "from ensino.matricula_componente m, discente d, curso c "
												+ "where m.id_discente = d.id_discente and d.id_curso = c.id_curso "
												+ "and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " "
												+ "and c.id_modalidade_educacao = " + ModalidadeEducacao.PRESENCIAL + " "
												+ "and c.id_convenio is null "
												+ "and m.ano = ? and m.periodo = ? and d.nivel = '" + NivelEnsino.MESTRADO + "'",
										parametros));

		posgrad.put("Alunos matriculados nos cursos de doutorado",
						tmp.queryForInt("select count(distinct m.id_discente) "
												+ "from ensino.matricula_componente m, discente d, curso c "
												+ "where m.id_discente = d.id_discente and d.id_curso = c.id_curso "
												+ "and c.id_modalidade_educacao = " +  ModalidadeEducacao.PRESENCIAL + " "
												+ "and c.id_convenio is null "
												+ "and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " "
												+ "and m.ano = ? and m.periodo = ? and d.nivel = '" + NivelEnsino.DOUTORADO + "'",
										parametros));

		posgrad.put("Alunos Especiais de Pós-Graduação",
						tmp.queryForInt("select count(distinct m.id_discente) "
												+ "from ensino.matricula_componente m, discente d "
												+ "where m.id_discente = d.id_discente "
												+ "and d.tipo = " + Discente.ESPECIAL + " "
												+ "and m.id_situacao_matricula in " + gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) + " "
												+ "and m.ano = ? and m.periodo = ? and d.nivel in " + UFRNUtils.gerarStringIn(NivelEnsino.getNiveisStricto()),
										parametros));

		result.put("Graduação", grad);
		result.put("Pós-Graduação", posgrad);
		result.put("Técnico", tec);

		return result;
	}

	/** 
	 * Retorna dados para o relatório Quantitativo de Alunos Matriculados.
	 * 
	 * @see RelatoriosPlanejamentoMBean#gerarRelatorioQuantitativoAlunosMatriculados()
	 * @return
	 * @throws DAOException
	 */
	public TreeMap<String, Map<String, Integer>> findQuantitativoAlunosAtivosPlanejamento()
			throws DAOException {
		TreeMap<String, Map<String, Integer>> result = new TreeMap<String, Map<String, Integer>>();
		TreeMap<String, Integer> grad = new TreeMap<String, Integer>();
		TreeMap<String, Integer> posgrad = new TreeMap<String, Integer>();
		TreeMap<String, Integer> tec = new TreeMap<String, Integer>();

		JdbcTemplate tmp = getJdbcTemplate();

		tec.put("Alunos ativos no ensino técnico", tmp
				.queryForInt("select count(distinct d.id_discente) "
						+ "from discente d, curso c "
						+ "where d.id_curso = c.id_curso "
						+ "and d.status = " + StatusDiscente.ATIVO + " and d.nivel = '" + NivelEnsino.TECNICO + "'"));

		grad.put("Alunos ativos na graduação presencial", tmp
				.queryForInt("select count(distinct d.id_discente) "
						+ "from discente d, curso c "
						+ "where d.id_curso = c.id_curso "
						+ "and c.id_modalidade_educacao = " + ModalidadeEducacao.PRESENCIAL + " "
						+ "and c.id_convenio is null "
						+ "and d.status in " + gerarStringIn(StatusDiscente.getAtivos()) +"  and d.nivel = '" + NivelEnsino.GRADUACAO + "'"));

		grad.put("Alunos ativos na educação a distância",
						tmp.queryForInt("select count(distinct d.id_discente) "
										+ "from discente d, curso c "
										+ "where d.id_curso = c.id_curso "
										+ "and c.id_modalidade_educacao = " + ModalidadeEducacao.A_DISTANCIA + " "
										+ "and c.id_convenio is null "
										+ "and d.status in " + gerarStringIn(StatusDiscente.getAtivos()) + " "
										+ "and d.nivel = '" + NivelEnsino.GRADUACAO + "'"));

		grad.put("Alunos ativos na probásica",
						tmp.queryForInt("select count(distinct d.id_discente) "
										+ "from discente d, curso c "
										+ "where d.id_curso = c.id_curso "
										+ "and c.id_convenio = 1 "
										+ "and d.status in " + gerarStringIn(StatusDiscente.getAtivos()) + " "
										+ "and d.nivel = '" + NivelEnsino.GRADUACAO + "'"));

		grad.put("Alunos Especiais ativos",
						tmp.queryForInt("select count(distinct d.id_discente) "
										+ "from discente d "
										+ "where d.tipo = " + Discente.ESPECIAL + " "
										+ "and d.status in " + gerarStringIn(StatusDiscente.getAtivos()) + " "
										+ "and d.nivel = '" + NivelEnsino.GRADUACAO + "'"));

		posgrad.put("Alunos ativos nos cursos de especialização", tmp
				.queryForInt("select count(distinct d.id_discente) "
						+ "from discente d, curso c "
						+ "where d.id_curso = c.id_curso "
						+ "and c.id_convenio is null "
						+ "and d.status = " + StatusDiscente.ATIVO + " and d.nivel = '" + NivelEnsino.LATO + "'"));

		posgrad.put("Alunos ativos nos cursos de mestrado", tmp
				.queryForInt("select count(distinct d.id_discente) "
						+ "from discente d, curso c "
						+ "where d.id_curso = c.id_curso "
						+ "and c.id_modalidade_educacao = " + ModalidadeEducacao.PRESENCIAL + " "
						+ "and c.id_convenio is null "
						+ "and d.status = " + StatusDiscente.ATIVO + " and d.nivel = '" + NivelEnsino.MESTRADO + "'"));

		posgrad.put("Alunos ativos nos cursos de doutorado", tmp
				.queryForInt("select count(distinct d.id_discente) "
						+ "from discente d, curso c "
						+ "where d.id_curso = c.id_curso "
						+ "and c.id_modalidade_educacao = " + ModalidadeEducacao.PRESENCIAL
						+ "and c.id_convenio is null "
						+ "and d.status = " + StatusDiscente.ATIVO + " and d.nivel = '" + NivelEnsino.DOUTORADO + "'"));

		posgrad.put("Alunos Especiais de Pós-Graduação ativos", tmp
				.queryForInt("select count(distinct d.id_discente) "
						+ "from discente d, curso c "
						+ "where d.id_curso = c.id_curso "
						+ "and c.id_modalidade_educacao = " + ModalidadeEducacao.PRESENCIAL
						+ "and c.id_convenio is null "
						+ "and d.status = " + StatusDiscente.ATIVO + " and d.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto())));

		result.put("Graduação", grad);
		result.put("Pós-Graduação", posgrad);
		result.put("Técnico", tec);

		return result;
	}

	/**
	 * Busca todos os discentes.
	 * 
	 * @param polo - Polo do Aluno
	 * @param curso - Curso do Aluno
	 * @param ano - ano ingresso
	 * @param periodo - período ingresso
	 * @param matriculados - TRUE se desejar trazer somente MATRICULADOS ou EM_ESPERA
	 * @param agrupar - TRUE se desejar agrupar pelo polo, curso e nome
	 * @return
	 * @throws DAOException
	 */
	public List<DiscenteGraduacao> findDiscentesByPoloCurso(Polo polo,
			Curso curso, Integer ano, Integer periodo, boolean matriculados,
			boolean agrupar, boolean analitico) throws DAOException {
		StringBuilder sb = new StringBuilder(
				"select p.cpf_cnpj, p.data_nascimento, d.matricula, d.ano_ingresso, p.nome as nome_pessoa, c.id_curso, c.nome as nome_curso," +
				" mod.id_modalidade_educacao, unid.sigla, m.nome nome_cidade,uf.sigla as unidade_federativa "
						+ "from graduacao.discente_graduacao dg, discente d, comum.pessoa p, curso c, comum.modalidade_educacao mod, comum.unidade unid, ead.polo po, comum.municipio m, comum.unidade_federativa uf "
						+ "where d.id_pessoa = p.id_pessoa and dg.id_discente_graduacao = d.id_discente and d.id_curso = c.id_curso "
						+ "and c.id_modalidade_educacao = mod.id_modalidade_educacao and c.id_unidade = unid.id_unidade "
						+ "and po.id_polo = dg.id_polo and po.id_cidade = m.id_municipio and uf.id_unidade_federativa = m.id_unidade_federativa "
						+ "and d.status in "
						+ gerarStringIn(new int[] { StatusDiscente.ATIVO,
								StatusDiscente.FORMANDO,
								StatusDiscente.GRADUANDO,
								StatusDiscente.TRANCADO }));
		
		if (polo != null && polo.getId() > 0)
			sb.append(" and dg.id_polo = " + polo.getId());
		
		if (ano != null && ano > 0 ) {
			sb.append(" and d.ano_ingresso = " + ano);
		}
		if (periodo != null && periodo > 0) {
			sb.append(" and d.periodo_ingresso = " + periodo);
		}
		if (curso.getId() > 0) {
			sb.append(" and c.id_curso = " + curso.getId());
		}
		if (matriculados) {
			sb.append(" and exists (select id_discente from ensino.matricula_componente where id_discente=d.id_discente "
							+ "and id_situacao_matricula in "
							+ gerarStringIn(SituacaoMatricula
									.getSituacoesMatriculadas()) + ")");
		}
		sb.append(" order by");
		if (!analitico) 
			sb.append(" d.ano_ingresso,");
		if (agrupar) 
			sb.append(" m.nome asc, c.nome asc,");
		
		sb.append(" p.nome asc");
		@SuppressWarnings("unchecked")
		List<DiscenteGraduacao> result = getJdbcTemplate().query(sb.toString(),
				new RowMapper() {
					public Object mapRow(ResultSet rs, int line)
							throws SQLException {
						DiscenteGraduacao dg = new DiscenteGraduacao();
						dg.setPessoa(new Pessoa());
						dg.setCurso(new Curso());
						dg.setPolo(new Polo());
						dg.setDiscente(new Discente());
						dg.getDiscente().setAnoIngresso(new Integer(rs.getInt("ano_ingresso")));
						dg.getPolo().setCidade(new Municipio());
						dg.getPolo().getCidade().setUnidadeFederativa(new UnidadeFederativa());
						dg.getPessoa().setCpf_cnpj(rs.getLong("cpf_cnpj"));
						dg.getPessoa().setDataNascimento(rs.getDate("data_nascimento"));
						dg.setMatricula(rs.getLong("matricula"));
						dg.getPessoa().setNome(rs.getString("nome_pessoa"));
						dg.getCurso().setId(rs.getInt("id_curso"));
						dg.getCurso().setNome(rs.getString("nome_curso"));
						dg.getCurso().setModalidadeEducacao(new ModalidadeEducacao(rs.getInt("id_modalidade_educacao")));
						dg.getCurso().setUnidade(new Unidade());
						dg.getCurso().getUnidade().setSigla(rs.getString("sigla"));
						dg.getPolo().getCidade().setNome(rs.getString("nome_cidade"));
						dg.getPolo().getCidade().getUnidadeFederativa().setSigla(rs.getString("unidade_federativa"));
						return dg;
					}
				});

		return result;
	}

	/**
	 * Busca todos os alunos EAD com status (ATIVO, FORMANDO, GRADUANDO) e matrícula componente MATRICULADO.
	 * 
	 * @param polo
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<DiscenteGraduacao> findDadosExportacaoAlunosEad(Polo polo,
			Curso curso, int ano, int periodo) throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;

		StringBuilder sb = new StringBuilder(
				"select d.id_discente, d.matricula, p.nome, p.email, m.nome as nome_cidade, mc.id_matricula_componente, ccd.codigo "
						+ "from discente d, graduacao.discente_graduacao dg, ensino.matricula_componente mc, ensino.componente_curricular_detalhes ccd, "
						+ "comum.pessoa p, curso c, ead.polo po, comum.municipio m where dg.id_polo = po.id_polo and po.id_cidade = m.id_municipio and d.id_curso = c.id_curso "
						+ "and d.id_discente = dg.id_discente_graduacao and dg.id_polo is not null and d.id_pessoa = p.id_pessoa and mc.id_componente_curricular = ccd.id_componente "
						+ "and mc.id_discente = d.id_discente and mc.ano = " + ano + " and mc.periodo = " + periodo	+ " and mc.id_situacao_matricula = 2 "
						+ "and d.status in (1,8,9)");
		if (polo != null && polo.getId() > 0) {
			sb.append(" and dg.id_polo = " + polo.getId());
		}
		if (curso != null && curso.getId() > 0) {
			sb.append(" and c.id_curso = " + curso.getId());
		}
		sb.append(" order by p.nome, d.matricula");

		List<DiscenteGraduacao> result = new ArrayList<DiscenteGraduacao>();

		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sb.toString());

			rs = st.executeQuery();
			while (rs.next()) {
				DiscenteGraduacao dg = new DiscenteGraduacao();
				dg.setId(rs.getInt("id_discente"));
				dg.setMatricula(rs.getLong("matricula"));

				if (result.contains(dg)) {
					dg = result.get(result.indexOf(dg));
				} else {
					dg.setPessoa(new Pessoa());
					dg.getPessoa().setNome(rs.getString("nome"));
					dg.getPessoa().setEmail(rs.getString("email"));
					dg.setMatriculasDisciplina(new HashSet<MatriculaComponente>());
					dg.setPolo(new Polo());
					dg.getPolo().setCidade(new Municipio());
					dg.getPolo().getCidade().setNome(rs.getString("nome_cidade"));
					result.add(dg);
				}

				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				mc.getComponente().setCodigo(rs.getString("codigo"));
				dg.getMatriculasDisciplina().add(mc);
			}

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}

		return result;
	}

	/**
	 * Retorna os discente do curso/ano/período informados.
	 *
	 * @param anoIngresso
	 * @param periodoIngresso
	 * @param cursos
	 * @param somenteAtivos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByCursoAnoPeriodo(int anoIngresso,
			int periodoIngresso, Collection<Curso> cursos, boolean somenteAtivos)
			throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT new Discente(id, matricula, pessoa.nome, status, nivel) FROM Discente WHERE  ");
			hql.append("curso.id in " + gerarStringIn(cursos)
					+ " and anoIngresso=:ano and periodoIngresso =:periodo ");
			if (somenteAtivos) {
				hql.append(" and status in "
						+ gerarStringIn(StatusDiscente.getStatusComVinculo()));
			}
			hql.append(" order by pessoa.nome");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("ano", anoIngresso);
			q.setInteger("periodo", periodoIngresso);
			@SuppressWarnings("unchecked")
			Collection<Discente> lista = q.list();
			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	

	/**
	 * Retorna um lista de id's da pessoa informada.
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public List<Integer> findIdsDiscenteByPessoa(Pessoa pessoa) throws DAOException {
		Criteria c = getCriteria(Discente.class)
		.setProjection( Projections.property("id") )
		.add( eq("pessoa.id", pessoa.getId()) )
		.add( ne("status", StatusDiscente.EXCLUIDO) )
		.add( isNotNull("matricula") )
		.addOrder( desc("anoIngresso") )
		.addOrder( desc("periodoIngresso") );

		@SuppressWarnings("unchecked")
		List<Integer> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna um objeto que implementa a interface DiscenteAdapter 
	 * cujo tipo depende do nível de ensino do discente passado como parâmetro.
	 * @param idDiscente
	 * @return
	 */
	public DiscenteAdapter findDiscenteAdapterById(int idDiscente) throws DAOException {
		char nivel = getJdbcTemplate().queryForChar("select nivel from discente where id_discente = ?", new Object[] { idDiscente });
		DiscenteAdapter discente = findDetalhesByDiscente(idDiscente, nivel);
		return discente;
	}
	
	
	/**
	 * Busca o objeto do tipo mais específico do discente adapter passado como parâmetro dependendo do
	 * seu nível de ensino.
	 * 
	 * @param idDiscente, nivel
	 */
	public DiscenteAdapter findDetalhesByDiscente(DiscenteAdapter discente) throws DAOException {
		if( isEmpty(discente.getNivel()) ) 
			throw new DAOException("Necessário popular o nível do discente");
		
		return findDetalhesByDiscente(discente.getId(), discente.getNivel());
	}
	
	/**
	 * Busca o objeto do tipo mais específico do discente passado como parâmetro dependendo do
	 * seu nível de ensino.
	 * 
	 * @param idDiscente, nivel
	 */
	private DiscenteAdapter findDetalhesByDiscente(int idDiscente, char nivel) throws DAOException {
		DiscenteAdapter discente = null;
		
		if (NivelEnsino.isAlgumNivelStricto(nivel)) {
			discente = (DiscenteAdapter) getCriteria(DiscenteStricto.class).add( eq("discente.id", idDiscente) ).uniqueResult();
		} else if (nivel == NivelEnsino.GRADUACAO) {
			discente = (DiscenteAdapter) getCriteria(DiscenteGraduacao.class).add( eq("discente.id", idDiscente) ).uniqueResult();				
		} else if (NivelEnsino.isResidenciaMedica(nivel)) {
			discente = (DiscenteAdapter) getCriteria(DiscenteResidenciaMedica.class).add( eq("discente.id", idDiscente) ).uniqueResult();
		} else if (nivel == NivelEnsino.INFANTIL) {
			discente = (DiscenteAdapter) getCriteria(DiscenteInfantil.class).add( eq("discente.id", idDiscente) ).uniqueResult();
		} else if (nivel == NivelEnsino.LATO) {
			discente = (DiscenteAdapter) getCriteria(DiscenteLato.class).add( eq("discente.id", idDiscente) ).uniqueResult();
		} else if (nivel == NivelEnsino.TECNICO || nivel == NivelEnsino.FORMACAO_COMPLEMENTAR) {
			discente = (DiscenteAdapter) getCriteria(DiscenteTecnico.class).add( eq("discente.id", idDiscente) ).uniqueResult();
		} else if (nivel == NivelEnsino.MEDIO) {
			discente = (DiscenteAdapter) getCriteria(DiscenteMedio.class).add( eq("discente.id", idDiscente) ).uniqueResult();
		}
		
		return discente;
	}	

	/**
	 * Busca os objetos dos tipos mais específicos dos discentes passados como parâmetro dependendo do
	 * seu nível de ensino.
	 * 
	 * @param idsDiscente, nivel
	 */
	public List<DiscenteAdapter> findDetalhesByDiscente(List<Integer> idsDiscente, char nivel) throws DAOException {
		List<DiscenteAdapter> discente = null;
		
		if (NivelEnsino.isAlgumNivelStricto(nivel)) {
			discente = getCriteria(DiscenteStricto.class).add( in("discente.id", idsDiscente) ).list();
		} else if (nivel == NivelEnsino.GRADUACAO) {
			discente = getCriteria(DiscenteGraduacao.class).add( in("discente.id", idsDiscente) ).list();				
		} else if (NivelEnsino.isResidenciaMedica(nivel)) {
			discente = getCriteria(DiscenteResidenciaMedica.class).add( in("discente.id", idsDiscente) ).list();
		} else if (nivel == NivelEnsino.INFANTIL) {
			discente = getCriteria(DiscenteInfantil.class).add( in("discente.id", idsDiscente) ).list();
		} else if (nivel == NivelEnsino.LATO) {
			discente = getCriteria(DiscenteLato.class).add( in("discente.id", idsDiscente) ).list();
		} else if (nivel == NivelEnsino.TECNICO || nivel == NivelEnsino.FORMACAO_COMPLEMENTAR) {
			discente = getCriteria(DiscenteTecnico.class).add( in("discente.id", idsDiscente) ).list();
		} else if (nivel == NivelEnsino.MEDIO) {
			discente = getCriteria(DiscenteMedio.class).add( in("discente.id", idsDiscente) ).list();
		}
		
		return discente;
	}		
	
	/**
	 * Traz o trabalho de fim de curso de um aluno.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public TrabalhoFimCurso findTrabalhoConclusaoCurso(Discente discente)
			throws DAOException {
		Criteria c = getSession().createCriteria(TrabalhoFimCurso.class);
		c.add(Restrictions.eq("orientando", discente));
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		c.addOrder(Order.desc("dataDefesa"));
		c.setMaxResults(1);
		return (TrabalhoFimCurso) c.uniqueResult();
	}

	/** 
	 * Determina se o aluno tem o status CONCLUIDO e créditos pendentes.
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public boolean isAlunoConcluidoCreditoPendente(DiscenteAdapter discente)
			throws DAOException {
		StringBuilder sqlconsulta = new StringBuilder(" select d.id_discente"
				+ " from discente d, graduacao.discente_graduacao dg, comum.pessoa p"
				+ " where status = 3"
				+ " and dg.id_discente_graduacao = d.id_discente"
				+ " and d.id_pessoa = p.id_pessoa"
				+ " and dg.cr_total_pendentes > 0" + " and d.id_discente = "
				+ discente.getId() + " order by ano_ingresso, periodo_ingresso");
		Query q = getSession().createSQLQuery(sqlconsulta.toString());
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		return (result != null && !result.isEmpty());
	}

	/**
	 * Retorna os discentes de uma turma.
	 *
	 * @param idTurma
	 * @param somenteMatriculados
	 *            retorna somente os matriculados
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByTurma(int idTurma,
			boolean somenteMatriculados) throws DAOException {

		StringBuilder hql = new StringBuilder();
		hql.append(" SELECT new Discente( mc.discente.id, mc.discente.matricula, mc.discente.pessoa.nome, mc.discente.status, mc.discente.pessoa.cpf_cnpj ) ");
		hql.append(" FROM MatriculaComponente mc ");
		hql.append(" WHERE mc.turma.id = :idTurma ");
		if (somenteMatriculados) {
			hql.append(" AND situacaoMatricula in "
					+ gerarStringIn(SituacaoMatricula
							.getSituacoesMatriculadas()));
		}

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idTurma", idTurma);

		@SuppressWarnings("unchecked")
		Collection<Discente> lista = q.list();
		return lista;
	}

	/**
	 * Buscar campos de discente utilizados para a geração de etiquetas de
	 * identificação de arquivos físicos.
	 * 
	 * @param nivel
	 *
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @param collection
	 * @param formaIngresso
	 * @return
	 */
	public List<Map<String, Object>> findParaEtiquetas(char nivel, Curso curso,
			Integer ano, Integer periodo, FormaIngresso formaIngresso,
			Collection<Long> matriculas) {

		String sql = "SELECT d.matricula, p.nome, fi.descricao as formaIngresso, c.nome as curso, m.nome as municipio" +
				" FROM discente as d " +
					" INNER JOIN comum.pessoa as p ON d.id_pessoa = p.id_pessoa" +
					" LEFT JOIN ensino.forma_ingresso as fi ON d.id_forma_ingresso = fi.id_forma_ingresso" +
					" INNER JOIN curso as c ON d.id_curso = c.id_curso" +
					" INNER JOIN comum.municipio as m ON c.id_municipio = m.id_municipio" +
					" WHERE d.nivel = '"+nivel+"'" +
							" AND status in " + UFRNUtils.gerarStringIn(StatusDiscente.getStatusComVinculo());

		List<Object> args = new ArrayList<Object>();

		if (curso != null) {
			sql += " AND c.id_curso = ? ";
			args.add(curso.getId());
		}

		if (ano != null && periodo != null) {
			sql += " AND d.ano_ingresso = ? AND d.periodo_ingresso = ? ";
			args.add(ano);
			args.add(periodo);
		}

		if (formaIngresso != null) {
			sql += " AND d.id_forma_ingresso = ? ";
			args.add(formaIngresso.getId());
		}

		if (matriculas != null) {
			sql += " AND d.matricula in " + UFRNUtils.gerarStringIn(matriculas);
		}

		sql += " ORDER BY d.matricula ";

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> lista = getJdbcTemplate().queryForList(sql, args.toArray());
		return lista;
	}

	/**
	 * Com base no ano-período atual e perfil inicial, calcula o atual período do discente.
	 * 
	 * @param d
	 * @param anoAtual
	 * @param periodoAtual
	 * @return
	 */
	public int calculaPeriodoAtualDiscente(DiscenteGraduacao d, int anoAtual,
			int periodoAtual) {
		return getJdbcTemplate().queryForInt(
						"select greatest((? - d.ano_ingresso) * 2 +"
						        + "      (? - d.periodo_ingresso + 1) -"
								+ "      (select count(*)"
								+ "       from ensino.movimentacao_aluno "
								+ "       where ativo = trueValue()"
								+ "       and id_tipo_movimentacao_aluno = " + TipoMovimentacaoAluno.TRANCAMENTO
								+ "       and id_discente = d.id_discente"
								+ "       and (ano_referencia < ? or"
								+ "            (ano_referencia = ? and periodo_referencia <= ?)"
								+ "           )"
								+ "      ) + ( greatest(dg.perfil_inicial,0) + greatest(dg.perfil_inicial_alterado,0) ), 0) as periodo_atual "
								+ " from graduacao.discente_graduacao dg, discente d"
								+ " where d.id_discente = dg.id_discente_graduacao "
								+ " and d.id_discente = ?",
						new Object[] { anoAtual, periodoAtual, anoAtual, anoAtual, periodoAtual, d.getId() });
	}

	/**
	 * Retorna todos os discente que possuem a EstruturaCurricularTecnica especificada.
	 * 
	 * @see EstruturaCurricularTecnica
	 * @param ect
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteTecnico> findByEstruturaCurricularTecnica(
			EstruturaCurricularTecnica ect) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT d ");
		hql.append("FROM DiscenteTecnico d join d.discente dd ");
		hql.append("WHERE dd.status = :ativo ");
		hql.append("and d.turmaEntradaTecnico.estruturaCurricularTecnica.id = :idEstrutura");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("ativo", StatusDiscente.ATIVO);
		q.setInteger("idEstrutura", ect.getId());
		@SuppressWarnings("unchecked")
		Collection<DiscenteTecnico> lista = q.list();
		return lista;
	}

	/**
	 * Traz os componentes pendentes de um aluno do Técnico.
	 * 
	 * @param id
	 * @param disciplinas - Disciplinas que ele já pagou
	 * @param equivalenciasDiscente
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ComponenteCurricular> findComponentesPendentesTecnico(int id, List<MatriculaComponente> disciplinas) {
		String sql = "select cc.id_disciplina, cc.qtd_max_matriculas, ccd.codigo, ccd.nome, ccd.ch_total, ccd.equivalencia from tecnico.estrutura_curricular_tecnica  ect, tecnico.modulo_curricular mc, "
				+ "tecnico.modulo_disciplina md, ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd, discente d, tecnico.discente_tecnico dt "
				+ "where d.id_discente=dt.id_discente and dt.id_estrutura_curricular=ect.id_estrutura_curricular and ect.id_estrutura_curricular=mc.id_estrutura_curricular "
				+ "and mc.id_modulo = md.id_modulo and ect.ativa = trueValue() and md.id_disciplina = cc.id_disciplina "
				+ "and cc.id_detalhe = ccd.id_componente_detalhes and d.id_discente=?";

		final List<Integer> idsDisciplinas = new ArrayList<Integer>();
		final List<Integer> idsMatriculadas = new ArrayList<Integer>();
		for (MatriculaComponente mc : disciplinas) {
			if (mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVADO)
					|| mc.getSituacaoMatricula().equals(
							SituacaoMatricula.APROVEITADO_TRANSFERIDO)
					|| mc.getSituacaoMatricula().equals(
							SituacaoMatricula.APROVEITADO_CUMPRIU)
					|| mc.getSituacaoMatricula().equals(
							SituacaoMatricula.APROVEITADO_DISPENSADO)) {
				idsDisciplinas.add(mc.getComponente().getId());
			} else if (mc.getSituacaoMatricula().equals(
					SituacaoMatricula.MATRICULADO)
					|| mc.getSituacaoMatricula().equals(
							SituacaoMatricula.EM_ESPERA)) {
				idsMatriculadas.add(mc.getComponente().getId());
			}
		}

		final List<ComponenteCurricular> pendentes = new ArrayList<ComponenteCurricular>();
		getJdbcTemplate().query(sql, new Object[] { id }, new RowMapper() {			/** Mapeia o resultado da busca para um objeto ComponenteCurricular
			 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
			 */
			public Object mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setId(rs.getInt("id_disciplina"));
				cc.setQtdMaximaMatriculas(rs.getInt("qtd_max_matriculas"));
				cc.setCodigo(rs.getString("codigo"));
				cc.setDetalhes(new ComponenteDetalhes());
				cc.getDetalhes().setCodigo(cc.getCodigo());
				cc.getDetalhes().setNome(rs.getString("nome"));
				cc.getDetalhes().setChTotal(rs.getInt("ch_total"));
				cc.getDetalhes().setEquivalencia(rs.getString("equivalencia"));

				// Se a disciplina pendente esta na lista de matricula, então marca como matriculado
				if (idsMatriculadas.contains(cc.getId())) {
					cc.setMatriculado(true);
				// se não esta na lista de matriculados, então verifica se o aluno esta pagando alguma equivalente ao componente pedente
				} else if (isNotEmpty(cc.getDetalhes().getEquivalencia())) {
					ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(cc.getDetalhes().getEquivalencia());
					
					if (arvore != null && arvore.eval(idsMatriculadas)) {
						cc.setMatriculadoEmEquivalente(true);
					}
				}
				
				boolean pagouEquivalente = false;
				ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(cc.getDetalhes().getEquivalencia());
				
				if (arvore != null && arvore.eval(idsDisciplinas))
					pagouEquivalente = true;
				
				
				if (!pagouEquivalente) {
					if (!idsDisciplinas.contains(cc.getId()))
						pendentes.add(cc);
					else if (Collections.frequency(idsDisciplinas, cc.getId()) < cc.getQtdMaximaMatriculas()) 
						pendentes.add(cc);
				} 
				
				return cc;
			}
		});
		
		return pendentes;
	}

	/**
	 * Busca os discentes que possuem os id's especificados.
	 * 
	 * @param ids
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findByIds( Collection<Integer> ids ) throws DAOException{

		String hql = "SELECT new Discente(id, matricula, pessoa.nome, status, nivel) FROM Discente " +
			" WHERE id in " + gerarStringIn(ids);

		Query q = getSession().createQuery( hql );
		@SuppressWarnings("unchecked")
		Collection<Discente> lista = q.list();
		return lista;
	}


	/**
	 * Faz uma projeção e retorna o Discente com id especificado.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Discente findByPrimaryKeyOtimizado(int id) throws DAOException{
		String hql = "SELECT new Discente(id, matricula, status, nivel) FROM Discente  WHERE id = " + id;
		Query q = getSession().createQuery( hql );
		return (Discente) q.uniqueResult();
	}
	

	/**
	 * Verifica se um aluno possui matrículas com status matriculado.
	 */
	public boolean existemMatriculasNaoConsolidadas(int idDiscente) {
		return getJdbcTemplate().queryForInt("select count(*) from ensino.matricula_componente where id_discente = ? and id_situacao_matricula = ?", new Object[] { idDiscente, SituacaoMatricula.MATRICULADO.getId() }) > 0;
	}
	
	
	/**
	 * Todos os discentes interessados em uma oportunidade.
	 * 
	 * @param tipo
	 * @param idOportunidade
	 * @return
	 * @throws DAOException
	 */
	public List<Discente> findAllInteressadoBolsa(TipoInteressadoBolsa tipo, int idOportunidade) throws DAOException {
		Criteria c = getSession().createCriteria(InteressadoBolsa.class).setProjection( Projections.property("discente") );
		c.add(Restrictions.eq("idEstagio", idOportunidade));
		c.add(Restrictions.eq("tipoBolsa.id", tipo.getId()));
		@SuppressWarnings("unchecked")
		List<Discente> lista = c.list();
		return lista;
	}
	
	/**
	 * Retorna a lista de disciplinas pendentes do discente informado. 
	 * 
	 * @param idDiscente id do discentes
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesCurricularesPendentesByDiscente( int idDiscente ) throws DAOException{
		List<MatriculaComponente> disciplinas = findDisciplinasConcluidasMatriculadas(idDiscente, false);
		return findByDisciplinasCurricularesPendentes(idDiscente, disciplinas, new ArrayList<TipoGenerico>());
	}
	
	/**
	 * Retorna a situação dos discentes no cadastro único que possuem a bolsa de residência.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public List<Object> findAllResidentesNoCadastroUnico(int ano, int periodo) {

		String sql = "select distinct p.nome, " +
				"CASE WHEN adesao.id_adesao is null THEN 'NÃO ADERIU AO CADASTRO ÚNICO' " +
				"ELSE 'ADERIU AO CADASTRO ÚNICO' " +
				"END as status " +
				"" +
				"from sae.bolsa_auxilio_periodo bap " +
				"inner join sae.bolsa_auxilio bolsa on bap.id_bolsa_auxilio = bolsa.id_bolsa_auxilio " +
				"left join sae.adesao_cadastro_unico adesao on bolsa.id_discente = adesao.id_discente " +
				"left join sae.cadastro_unico_bolsa cub on cub.id_cadastro_unico = adesao.id_cadastro_unico " +
				"left join discente d on d.id_discente = bolsa.id_discente " +
				"left join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
				"" +
				"where bolsa.id_tipo_bolsa_auxilio in (1,2) and " +
				"bap.ano = ? and " +
				"bap.periodo = ? and " +
				"(cub.status = 1 or cub.status is null) " +
				"" +
				"order by status,p.nome";
		
		@SuppressWarnings("unchecked")
		List<Object> lista = getJdbcTemplate().query(sql, new Object[] {ano, periodo}, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				Object[] obj = new Object[]{rs.getString(1), rs.getString(2)};
			
				return obj;
			}
		});
		return lista;
		
	}
	/**
	 * Busca discente no cadastro único de acordo com as restrições
	 * 
	 * @param restricao
	 * @param parametro
	 * @return
	 * @throws ArqException 
	 */
	public List<Discente> findDiscentes(RestricaoDiscenteCadastroUnico restricao, ParametroDiscenteCadastroUnico parametro) throws ArqException {
		
		StringBuilder hql = new StringBuilder();
		hql.append("select adesao.discente.id, adesao.discente.matricula, adesao.discente.pessoa.nome, ");
		hql.append("adesao.discente.curso.id from AdesaoCadastroUnicoBolsa adesao ");
		hql.append("where adesao.cadastroUnico.status = " + FormularioCadastroUnicoBolsa.EM_VIGOR);
		hql.append(" and adesao.discente.curso.unidade.id = :centro");
		
		if (restricao.isBuscaCurso())
			hql.append(" and adesao.discente.curso.id = :curso");

		hql.append(" group by adesao.discente.curso.id, adesao.discente.pessoa.nome," +
				" adesao.discente.id, adesao.discente.matricula");
		hql.append(" order by adesao.discente.curso.id, adesao.discente.pessoa.nome," +
				" adesao.discente.id, adesao.discente.matricula");
		
		Query query = getSession().createQuery(hql.toString());
		
		query.setInteger("centro", parametro.getCentro().getId());
		
		if (restricao.isBuscaCurso())
			query.setInteger("curso", parametro.getCurso().getId());
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		Iterator<Object[]> it = lista.iterator();

		List<Discente> resultado = null;
		
		while(it.hasNext()) {
			
			if (resultado == null)
				resultado = new ArrayList<Discente>();
			
			int coluna = 0;
			Discente discente = new Discente();
			Object[] obj = it.next();
			
			discente.setId((Integer) obj[coluna++]);
			discente.setMatricula((Long) obj[coluna++]);
			discente.setPessoa(new Pessoa());
			discente.getPessoa().setNome((String) obj[coluna++]);
			
			resultado.add(discente);
		}
		
		if (restricao.isBuscaAlunosSemBolsa() && !isEmpty(resultado)) {
			removerAlunosComBolsa(resultado);
		}
		
		if (restricao.isBuscaAreaInteresse() && !isEmpty(resultado)) {
			resultado = localizarDiscentePorArea(parametro, resultado);
		}
		
		return resultado;
		
	}

	/**
	 * Localiza os discente por área de interesses
	 * 
	 * @param parametro
	 * @param resultado
	 * @return
	 */
	private List<Discente> localizarDiscentePorArea(ParametroDiscenteCadastroUnico parametro,
			List<Discente> resultado) {
		List<Integer> ids = new ArrayList<Integer>();
		for (Discente discente : resultado) 
			ids.add(discente.getId());
		
		List<PerfilPessoa> perfilPessoas = PerfilPessoaDAO.getDao().findByAreas(parametro.getArea(), ids);

		List<Discente> comPerfil = new ArrayList<Discente>();
		for (PerfilPessoa pp : perfilPessoas) {
			Discente d = new Discente();
			d.setId(pp.getIdDiscente());
			comPerfil.add(d);
		}

		List<Discente> temp = new ArrayList<Discente>();
		for (Discente dRes : resultado) {
			for (Discente dPer : comPerfil) {
				if (dRes.getId() == dPer.getId()) {
					temp.add(dRes);
				}
			}
		}
		return temp;
	}

	/**
	 * Remove do resultado os alunos com bolsas
	 * 
	 * @param resultado
	 * @throws ArqException
	 */
	private void removerAlunosComBolsa(List<Discente> resultado) throws ArqException {
		/**
		 * Lista com as matrículas dos alunos encontrados na busca
		 */
		List<Long> matriculas = new ArrayList<Long>();
		for (Discente discente : resultado) 
			matriculas.add(discente.getMatricula());

		/**
		 * Alunos com bolsas
		 */
		Collection<Long> bolsistas = IntegracaoBolsas.verificarCadastroBolsaSIPAC(matriculas);

		if(bolsistas != null){
			/**
			 * Removendo do resultado os alunos com bolsas
			 */
			for (Long mat : bolsistas) {
				Discente d = new Discente();
				d.setMatricula(mat);
				
				if (resultado.contains(d))
					resultado.remove(d);
			}
		}
	}
	
	
	/** Retorna uma coleção de discentes ativos que não possuem orientação acadêmica ativa no momento da consulta.
	 * @param idCentro
	 * @param idCurso
	 * @param nivelEnsino
	 * @param idModalidadeEducacao
	 * @return
	 * @throws DAOException
	 */
	public Collection<Discente> findDiscentesAtivosSemOrientacaoAcademica(
			Integer idCentro, Integer idCurso, Character nivelEnsino,
			Integer idModalidadeEducacao) throws DAOException {
		// data atual
		Date hoje = CalendarUtils.descartarHoras(new Date());
		// Consulta para discentes que possuem orientação ativa na data atual
		StringBuilder hql = new StringBuilder("select discente" +
				" from Discente discente" +
				" inner join fetch discente.pessoa pessoa" +
				" inner join fetch discente.curriculo" +
				" inner join fetch discente.curso curso" +
				" inner join fetch curso.unidade unidade" +
				" left join fetch curso.modalidadeEducacao modalidade" +
				" where discente.status = :status");
		StringBuilder subHql = new StringBuilder("select distinct orientacao.discente.id" +
				"  from OrientacaoAcademica orientacao" +
				"  where cancelado = :cancelado" +
				"  and inicio <= :hoje" +
				"  and (fim is null or fim >= :hoje)");
		if (idCentro != null && idCentro != 0) {
			hql.append(" and unidade.id = :idCentro");
		}
		if (idCurso != null && idCurso != 0) {
			hql.append(" and curso.id = :idCurso");
		}
		if (nivelEnsino != null) {
			hql.append(" and curso.nivel = :nivelEnsino");
		}
		if (idModalidadeEducacao != null && idModalidadeEducacao != 0) {
			hql.append(" and modalidade.id = :idModalidadeEducacao");
		}
		hql.append(" and discente.id not in ("+subHql+")");
		hql.append(" order by curso.nome, pessoa.nomeAscii");
		Query q = getSession().createQuery(hql.toString());
		q.setDate("hoje", hoje);
		q.setInteger("status", StatusDiscente.ATIVO);
		q.setBoolean("cancelado", false);
		if (idCentro != null && idCentro != 0) {
			q.setInteger("idCentro", idCentro);
		}
		if (idCurso != null && idCurso != 0) {
			q.setInteger("idCurso", idCurso);
		}
		if (nivelEnsino != null) {
			q.setCharacter("nivelEnsino", nivelEnsino);
		}
		if (idModalidadeEducacao != null && idModalidadeEducacao != 0) {
			q.setInteger("idModalidadeEducacao", idModalidadeEducacao);
		}
		@SuppressWarnings("unchecked")
		Collection<Discente> discentes  = q.list();
		return discentes;
	}	
		
	/**
	 * Retorna todos os usuários do programa na qual foi cadastrada a nova notícia. 
	 * 
	 * @param programa
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findByNoticiaPublicadaProgramaEmail(Integer programa) throws DAOException {

		String consultaSql= "SELECT p.nome, p.email FROM discente as d, comum.pessoa as p " +
					" WHERE d.id_gestora_academica = "+programa+" and p.id_pessoa = d.id_pessoa";

		@SuppressWarnings("unchecked")
		List<Map<String, Object>>lista = getJdbcTemplate().queryForList(consultaSql);
		
		return lista;
	}
	
	/**
	 * Verifica se o aluno marcou a opção de concordância com o novo regulamento dos cursos de graduação.
	 * @param idDiscente
	 * @return true se o aluno ainda não marcou a opção.
	 * @throws DAOException
	 */
	public boolean verificaConcordanciaRegulamento(int idDiscente) throws DAOException {
		return getSession().createSQLQuery("SELECT id_discente FROM graduacao.concordancia_regulamento where id_discente="+idDiscente).list().isEmpty();	
	}
	
	/**
	 * Retorna o quantitativos de situações dos alunos por Curso.
	 * @param ano
	 * @return
	 */
	public List<Map<String, Object>> findQuantitativoSituacoesInsucesso(int ano, int intervalo){		
		String sqlconsulta = 
		"	SELECT "	 
			+"			U.SIGLA, U.ID_UNIDADE, C.ID_CURSO, C.NOME, "
			//+"			-- INGRESSANTES\n "
			+"			(select count(id_discente) as total "
			+"			from discente d " 
			+"          inner join ensino.forma_ingresso fi on (fi.id_forma_ingresso = d.id_forma_ingresso) "
			+"			where d.nivel = '"+NivelEnsino.GRADUACAO+"'"
			+"			  and ano_ingresso = "+ano
			+"            and fi.contagem_taxa_conclusao = trueValue() "
			+"			and id_curso = c.id_curso) as ingressantes, "
			//+"			-- CONCLUINTES\n "
			+"			  (select count(id_discente) as total "
			+"			  from discente d " 
			+"			  where nivel = '"+NivelEnsino.GRADUACAO+"'"
			+"			  and d.status in "+ UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinte())
			+"			  and id_curso = c.id_curso "
			+"			  and ano_ingresso = "+ano
			+"			  and ((d.status = "+StatusDiscente.CONCLUIDO+" and d.id_discente in " 
			+"						   ( select id_discente from ensino.movimentacao_aluno " 
			+"						     where  ANO_referencia >= "+(ano+intervalo)
			+"						     and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ativo = trueValue())) " 	                     
			+"			  or  (d.status = "+StatusDiscente.GRADUANDO+"))) as concluintes, "
			//+"			-- CONCLUINTES ANTES DO TEMPO\n "
			+"			  (select count(id_discente) as total "
			+"			  from discente d " 
			+"			  where nivel = '"+NivelEnsino.GRADUACAO+"'"
			+"			  and d.status = "+ StatusDiscente.CONCLUIDO
			+"			  and id_curso = c.id_curso "
			+"			  and ano_ingresso = "+ano
			+"            and (d.id_discente in " 
			+"						   ( select id_discente from ensino.movimentacao_aluno " 
			+"						     where  ANO_referencia < "+(ano+intervalo)
			+"						     and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ativo = trueValue())))"
					+" as concluintes_antes, "			
			//+"			-- DESISTENTES\n "
			+"			  (select count(id_discente) as total "
			+"			  from discente d " 
			+"			  where nivel = '"+NivelEnsino.GRADUACAO+"'"
			+"			  and ano_ingresso = "+ano
			+"			  and (d.status in ("+StatusDiscente.CANCELADO+","+StatusDiscente.EXCLUIDO+"))"
			+"			  and id_curso = c.id_curso) as desistencias, "
			//+"			--MATRICULADOS\n "
			+"			(select count(id_discente) " 
			+"			from public.discente d "
			+"			where (d.status in ("+StatusDiscente.ATIVO+","+StatusDiscente.FORMANDO+"))"
			+"			and d.nivel = '"+NivelEnsino.GRADUACAO+"'"
			+"			  and ano_ingresso = "+ano
			+"			and id_curso = c.id_curso) as matriculados, "
			//+"			--OUTROS\n "
			+"			(select count(id_discente) " 
			+"			from public.discente d "
			+"			where d.nivel = '"+NivelEnsino.GRADUACAO+"'"
			+"			  and ano_ingresso = "+ano
			+"			and d.status not in "+ UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())
			+"			and d.status not in ("+StatusDiscente.CONCLUIDO+","+StatusDiscente.CANCELADO+","+StatusDiscente.EXCLUIDO+")"			
			+"			and id_curso = c.id_curso) as outros "						
			+"		from public.curso c "
			+"		inner join comum.modalidade_educacao me ON me.id_modalidade_educacao = c.id_modalidade_educacao "
			+"		inner join comum.unidade u  ON u.id_unidade = c.id_unidade "
			+"		left join comum.municipio m on (m.id_municipio = c.id_municipio) " 
			+"		where c.nivel = '"+NivelEnsino.GRADUACAO+"' "
			+"      and c.id_modalidade_educacao =  "+ModalidadeEducacao.PRESENCIAL
			+"      and c.id_convenio is null "
			+"		order by sigla, nome ";
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}
	
	/**
	 * Retorna a lista de discentes com situação determinada no atributo tipo.
	 * Tipo = (1-Entrada, 2-Desistência, 3-Matriculados, 4-Outros)
	 * Usado no Relatório de Insucesso.
	 * @param ano
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String, Object>> findListaDiscentesInsucesso(int ano, int idCurso, int tipo, int idUnidade, int intervalo) throws DAOException {	
		String sqlconsulta = 		
		" SELECT DISTINCT U.SIGLA,C.ID_CURSO, "
		+"			       CASE "
		+"		            WHEN STRPOS(C.NOME,'PROBASICA')>0 THEN ' PROBASICA' "
		+"		            ELSE ME.DESCRICAO "
		+"		       END AS ME_MODALIDADE, "
		+"		       C.NOME, D.ID_DISCENTE,D.MATRICULA,P.NOME AS DISCENTE_NOME,P.SEXO,S.DESCRICAO AS STATUS, "
		+"			CASE "
		+"				WHEN (D.STATUS="+StatusDiscente.CONCLUIDO+" AND (SELECT COUNT(*) " 
		+"		                             FROM ENSINO.MOVIMENTACAO_ALUNO MA " 
		+"		                            WHERE MA.ID_DISCENTE=D.ID_DISCENTE "
		+"		                              AND MA.ano_referencia="+ano+intervalo
		+"					      AND MA.periodo_referencia=1 " 
		+"		                              AND MA.ID_TIPO_MOVIMENTACAO_ALUNO=1 " 
		+"		                              AND ((MA.ATIVO) OR (MA.ATIVO IS NULL)) "
		+"		                           )>0 "
		+"		           		) THEN 1 "
		+"				WHEN (D.STATUS="+StatusDiscente.CONCLUIDO+" AND (SELECT COUNT(*) " 
		+"		                             FROM ENSINO.MOVIMENTACAO_ALUNO MA " 
		+"		                            WHERE MA.ID_DISCENTE=D.ID_DISCENTE "
		+"		                              AND MA.ano_referencia="+ano+intervalo
		+"					      AND MA.periodo_referencia=2 " 
		+"		                              AND MA.ID_TIPO_MOVIMENTACAO_ALUNO="+TipoMovimentacaoAluno.CONCLUSAO
		+"		                              AND ((MA.ATIVO) OR (MA.ATIVO IS NULL)) "
		+"		                           )>0 "
		+"		           		) THEN 2 "
		+"				ELSE (SELECT PERIODO " 
		+"					FROM ENSINO.MATRICULA_COMPONENTE " 
		+"					WHERE ID_DISCENTE=D.ID_DISCENTE " 
		+"					AND ID_SITUACAO_MATRICULA IN ( "+SituacaoMatricula.APROVADO.getId()+" ) "
		+"					ORDER BY ANO DESC, PERIODO DESC "
		+"					LIMIT 1) "
		+"			END AS SEMESTRE_SAIDA, P.DATA_NASCIMENTO "
		+"		 FROM PUBLIC.DISCENTE D " 
		+"		 LEFT  JOIN PUBLIC.STATUS_DISCENTE S		ON S.STATUS=D.STATUS "
		+"		 INNER JOIN COMUM.PESSOA P 			    ON P.ID_PESSOA=D.ID_PESSOA "
		+"		 INNER JOIN PUBLIC.CURSO C 				    ON C.ID_CURSO=D.ID_CURSO "
		+"		 INNER JOIN COMUM.UNIDADE  U 				ON U.ID_UNIDADE=C.ID_UNIDADE "
		+"		 INNER JOIN COMUM.MODALIDADE_EDUCACAO ME   ON ME.ID_MODALIDADE_EDUCACAO=C.ID_MODALIDADE_EDUCACAO "
		+"		where c.nivel = '"+NivelEnsino.GRADUACAO+"' "
		+"      and c.id_modalidade_educacao =  "+ModalidadeEducacao.PRESENCIAL
		+"      and c.id_convenio is null ";
		
		sqlconsulta += (idUnidade > 0 ? "  and c.id_unidade = "+idUnidade : "");
		
		switch (tipo) {
		case 1: // Entrada
			sqlconsulta +=
				" and  d.ano_ingresso = "+ano+(idCurso > 0 ? "  and d.id_curso = "+idCurso : "");
			break;
		case 2: // Concluintes
			sqlconsulta +=
			"			  and d.status in "+ UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinte())
	        +"			  and ano_ingresso = "+ano
			+"			  and ((d.status = "+StatusDiscente.CONCLUIDO+" and d.id_discente in " 
			+"						   ( select id_discente from ensino.movimentacao_aluno " 
			+"						     where  ANO_referencia >= "+(ano+intervalo)
			+"						     and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ativo = trueValue())) "			
			+"			  or  (d.status = "+StatusDiscente.GRADUANDO+"))"+
			(idCurso > 0 ? "  and d.id_curso = "+idCurso : "");						
			break;			
		case 3: // Desistência
			sqlconsulta +=
			"	  and ano_ingresso = "+ano
			+"			and (d.status in ("+StatusDiscente.CANCELADO+","+StatusDiscente.EXCLUIDO+"))"
			+(idCurso > 0 ? "  and d.id_curso = "+idCurso : "");		
			break;
		case 4: // Matriculados
			sqlconsulta +=
				"			and (d.status in ("+StatusDiscente.ATIVO+","+StatusDiscente.FORMANDO+"))"
				+"			  and ano_ingresso = "+ano
				+(idCurso > 0 ? "  and d.id_curso = "+idCurso : "");				
			break;	
		case 5: // Outros
			sqlconsulta +=
				" and d.nivel = '"+NivelEnsino.GRADUACAO+"'"				
				+"			  and ano_ingresso = "+ano
				+"			and d.status not in "+ UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())
				+"			and d.status not in ("+StatusDiscente.CONCLUIDO+","+StatusDiscente.CANCELADO+","+StatusDiscente.EXCLUIDO+")"					
				+(idCurso > 0 ? "  and d.id_curso = "+idCurso : "");				
			break;
		case 6: // Concluintes Antes do Previsto
			sqlconsulta +=
				"			  and d.status = "+ StatusDiscente.CONCLUIDO
				+"			  and ano_ingresso = "+ano
				+"            and ((d.id_discente in " 
				+"						   ( select id_discente from ensino.movimentacao_aluno " 
				+"						     where  ANO_referencia < "+(ano+intervalo)
				+"						     and id_tipo_movimentacao_aluno = "+TipoMovimentacaoAluno.CONCLUSAO+" and ativo = trueValue())))"				
				+(idCurso > 0 ? "  and d.id_curso = "+idCurso : "");					
			break;		
		case 7: // Concluintes Sucesso
			sqlconsulta +=
				"			  and d.status in "+ UFRNUtils.gerarStringIn(StatusDiscente.getStatusConcluinte())
		        +"			  and ano_ingresso = "+ano		
				+(idCurso > 0 ? "  and d.id_curso = "+idCurso : "");					
			break;				
		}
		
		sqlconsulta += " order by U.SIGLA, C.NOME, DISCENTE_NOME ";
		
		@SuppressWarnings("unchecked")
		List <Map<String, Object>> rs = getJdbcTemplate().queryForList(sqlconsulta.toString());
		return rs;
	}			
		
	/**
	 * Retorna o Usuário correspondente à Pessoa de acordo com o id passado como parâmetro
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByUsuario(int idPessoa) throws DAOException{
		
		Query query = getSession().createQuery("from Usuario usuario where usuario.pessoa.id = :idPessoa");
		query.setInteger("idPessoa", idPessoa);
		
		return (Usuario) query.uniqueResult();
		
	}	
	
	/**
	 * Método responsável em buscar os possíveis discentes que 
	 * que serão cadastrados como orientados 
	 * @param nome
	 * @param status
	 * @param nivel
	 * @param idUnidade
	 * @param idCurso
	 * @param ano
	 * @param periodo
	 * @param semOrientacao
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<Discente> findPossiveisDiscentesOrientandos(String nome, Character nivel,
			Collection<Integer> status, Integer idUnidade,
			Integer idCurso, Integer ano, Integer periodo, boolean semOrientacao)
				throws DAOException{
		
		StringBuilder corpoHQL = new StringBuilder();
		StringBuilder condicaoHQL = new StringBuilder();
		
		corpoHQL.append(" SELECT discente ");
		corpoHQL.append(" FROM Discente discente INNER JOIN FETCH discente.pessoa pessoa " );
		corpoHQL.append(" LEFT JOIN FETCH discente.curriculo LEFT JOIN FETCH discente.curso curso ");
		corpoHQL.append(" LEFT JOIN FETCH discente.curriculo.matriz matriz ");
		
		condicaoHQL.append( corpoHQL ); 
		
		condicaoHQL.append(" WHERE 1 = 1 ");
		
		if( !isEmpty(nome) ){
			condicaoHQL.append(" AND " + UFRNUtils.convertUtf8UpperLatin9("pessoa.nome") + " LIKE " );
			condicaoHQL.append( UFRNUtils.toAsciiUpperUTF8("'" + nome.trim() + "%'"));
		}
		
		if( !isEmpty(nivel) )
			condicaoHQL.append(" AND discente.nivel = :nivel ");
		
		if( !isEmpty(ano) && !isEmpty(periodo) )
			condicaoHQL.append(" AND discente.anoIngresso= :ano AND discente.periodoIngresso = :periodo ");
		
		if( !isEmpty(idUnidade) && !isEmpty(nivel) ){
			if( nivel.equals(NivelEnsino.STRICTO) )
				condicaoHQL.append(" AND gestoraAcademica.id = :idUnidade ");
			else 
				condicaoHQL.append(" AND curso.unidade.id = :idUnidade ");
		}
		
		if( !isEmpty(idCurso) )
			condicaoHQL.append(" AND curso.id = :idCurso ");
		
		if( semOrientacao ){
		
			StringBuilder subHQL  = new StringBuilder();
			subHQL.append(" SELECT  DISTINCT orientacao.discente.id FROM OrientacaoAcademica orientacao ");
			subHQL.append(" WHERE cancelado = :cancelado AND inicio <= :hoje AND (fim IS NULL OR fim >= :hoje) ");
			
			condicaoHQL.append(" AND discente.status IN " + UFRNUtils.gerarStringIn(StatusDiscente.getAtivos()) );
			condicaoHQL.append(" AND discente.id not IN (" + subHQL + ")");
			
		}else if( !isEmpty(status) )
			condicaoHQL.append(" AND discente.status IN " + gerarStringIn(status) );
		
		condicaoHQL.append(" ORDER BY pessoa.nome ");
		
		Query q = getSession().createQuery(condicaoHQL.toString());
		
		if( !isEmpty(nivel) )
			q.setCharacter("nivel", nivel);
		
		if( !isEmpty(idCurso) )
			q.setInteger("idCurso", idCurso);
		
		if( !isEmpty(idUnidade) )
			q.setInteger("idUnidade", idUnidade);
		
		if( !isEmpty(ano) && !isEmpty(periodo) ){
			q.setInteger("ano", ano);
			q.setInteger("periodo", periodo);
		}
		
		if( semOrientacao ){
			q.setDate("hoje", CalendarUtils.descartarHoras(new Date()));
			q.setBoolean("cancelado", false);
		}
		
		return q.list();
		
	}

	/**
	 * Método para buscar informações importantes dos discentes para a realização
	 * dos seus cálculos de integralizações. Utilizado {@link RepositorioInformacoesCalculoDiscente}.
	 * @param idDiscente
	 * @return
	 */
	public DadosCalculosDiscente findDadosCalculosByDiscente(int idDiscente) {
		Date primeiraData = new Date();
		
		int numeroMatriculas = getJdbcTemplate().queryForInt("select count(id_matricula_componente) from ensino.matricula_componente where id_discente = ? and id_situacao_matricula in (1,2,3,4,5,6,7,9,10,11,12,21,22,23)", new Object[] { idDiscente });
		if ( numeroMatriculas > 0 ){
			try {
				primeiraData = (Date) getJdbcTemplate().queryForObject("select data_cadastro from ensino.matricula_componente where id_discente = ? order by data_cadastro asc limit 1", new Object[] { idDiscente }, Date.class);
			} catch(EmptyResultDataAccessException e) { }
		}	
		
		Date ultimaData = null;
		try {
			ultimaData = (Date) getJdbcTemplate().queryForObject("select data_ocorrencia from ensino.movimentacao_aluno where id_discente = ? and ativo = trueValue() and id_tipo_movimentacao_aluno = 1 limit 1", new Object[] { idDiscente }, Date.class);
		} catch(EmptyResultDataAccessException e) { } 
		
		if (ultimaData == null) {
			ultimaData = new Date();
		}
			
		final int idCurriculo = getJdbcTemplate().queryForInt("select id_curriculo from discente where id_discente = ?", new Object[] { idDiscente });
		Curriculo curriculo = new Curriculo();
		if ( idCurriculo > 0 ){
			curriculo = (Curriculo) getJdbcTemplate().queryForObject("select * from graduacao.curriculo where id_curriculo = ?", new Object[] { idCurriculo }, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					Curriculo c = new Curriculo();
					c.setId(idCurriculo);
					c.setCodigo(rs.getString("codigo"));
					c.setCrMaximoSemestre(rs.getInt("crmaximosemestre"));
					c.setCrMinimoSemestre(rs.getInt("crminimosemestre"));
					c.setCrIdealSemestre(rs.getInt("cridealsemestre"));
					c.setChMinimaSemestre(rs.getInt("ch_minima_semestre"));
					c.setChMaximaSemestre(rs.getInt("ch_maxima_semestre"));
					c.setAnoEntradaVigor(rs.getInt("anoentradavigor"));
					c.setPeriodoEntradaVigor(rs.getInt("periodoentradavigor"));
					c.setSemestreConclusaoMinimo(rs.getInt("semestre_conclusao_minimo"));
					c.setSemestreConclusaoIdeal(rs.getInt("semestre_conclusao_ideal"));
					c.setSemestreConclusaoMaximo(rs.getInt("semestre_conclusao_maximo"));
					c.setMesesConclusaoMinimo(rs.getInt("meses_conclusao_minimo"));
					c.setMesesConclusaoIdeal(rs.getInt("meses_conclusao_ideal"));
					c.setMesesConclusaoMaximo(rs.getInt("meses_conclusao_maximo"));
					c.setChOptativasMinima(rs.getInt("ch_optativas_minima"));
					c.setChTotalMinima(rs.getInt("ch_total_minima"));
					c.setCrTotalMinimo(rs.getInt("cr_total_minimo"));
					c.setChAtividadeObrigatoria(rs.getInt("ch_atividade_obrigatoria"));
					c.setChNaoAtividadeObrigatoria(rs.getInt("ch_nao_atividade_obrigatoria"));
					c.setCrNaoAtividadeObrigatorio(rs.getInt("cr_nao_atividade_obrigatorio"));
					c.setMaxEletivos(rs.getInt("max_eletivos"));
					c.setSituacao(rs.getInt("situacao"));
					c.setAtivo(rs.getBoolean("ativo"));
					return c;
				}
			});
		}
		// Cache das datas de entrada e conclusão do discente
		DadosCalculosDiscente dados = new DadosCalculosDiscente();
		dados.setIdDiscente(idDiscente);
		dados.setCurriculo(curriculo);
		dados.setDataInicio(primeiraData);
		dados.setDataFim(ultimaData);
		return dados;
	}
	
}
