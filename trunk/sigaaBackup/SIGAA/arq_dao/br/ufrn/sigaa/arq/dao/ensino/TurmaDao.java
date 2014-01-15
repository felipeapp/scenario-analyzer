/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/09/2006
 *
 */


package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static org.hibernate.criterion.Restrictions.eq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.relatorios.dominio.RelatorioQuantitativoTurmas;
import br.ufrn.sigaa.ensino.jsf.BuscaTurmaMBean;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoUtil;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.nee.dominio.StatusAtendimento;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Docente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Dao responsável por consultas específicas às Turmas.
 * @author Gleydson Lima
 */
public class TurmaDao extends GenericSigaaDAO {

	/** Define o limite de resultados da busca por turmas. */
	private static final int LIMITE_RESULTADOS_BUSCA_TURMA = 500;

	/** Construtor padrão. */
	public TurmaDao() {
	}
	

	/**
	 * Busca turmas abertas ou consolidadas de uma disciplina/ano/período/unidade/nível informados
	 *
	 * @param disciplina Componente Curricular
	 * @param ano obrigatório
	 * @param periodo  obrigatório
	 * @param unid 0 (zero) para todos
	 * @param nivel 0 (zero) para todos
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDisciplinaAnoPeriodo(ComponenteCurricular disciplina, int ano, int periodo, int unid,
			char nivel) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("FROM Turma WHERE ");
			if (unid > 0)
				hql.append("disciplina.unidade.id = " + unid + " and ");
			if (nivel != 0)
				hql.append("disciplina.nivel = '" + nivel + "' and ");

			hql.append("((situacaoTurma.id = " + SituacaoTurma.ABERTA + ") or ");
			hql.append("(situacaoTurma.id = " + SituacaoTurma.CONSOLIDADA +")) and ");
			hql.append("disciplina.id = " + disciplina.getId() + " and ");
			hql.append("ano = " + ano + " and ");
			hql.append("periodo = " + periodo);
			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			Collection<Turma> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca turmas abertas ou consolidadas de uma disciplina/ano/período/unidade/nível e curso informados
	 *
	 * @param disciplina Componente Curricular
	 * @param ano obrigatório
	 * @param periodo  obrigatório
	 * @param unid 0 (zero) para todos
	 * @param nivel 0 (zero) para todos
	 * @param idCurso 0 (zero) para todos
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDisciplinaAnoPeriodoCurso(ComponenteCurricular disciplina, int ano, int periodo, int unid,
			char nivel, int idCurso) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
   		    hql.append("select t.id, t.codigo, t.disciplina.codigo, t.disciplina.detalhes.nome, t.disciplina.detalhes.chTotal, "+
   		    		" t.descricaoHorario, t.disciplina.unidade.id, t.ano, t.periodo, t.situacaoTurma.id, t.distancia, dt  ");
   		    
			hql.append("FROM Turma t left join t.docentesTurmas as dt ");						
			if (idCurso > 0){				
				hql.append(" WHERE t.id in (select r.turma.id " +
						                   "from ReservaCurso r " +
						                   "where (r.matrizCurricular.id in" +
											"        (" +
											"	SELECT id" +
											"	FROM MatrizCurricular m" +
											"	WHERE m.curso.id = " +idCurso+
											"        )" +
											"    or r.curso.id = " +idCurso+
									"    ))");
				hql.append("AND ");
			} else {
				hql.append("WHERE ");
			}
			if (unid > 0)
				hql.append("t.disciplina.unidade.id = " + unid + " and ");
			if (nivel != 0)
				hql.append("t.disciplina.nivel = '" + nivel + "' and ");

			hql.append("((t.situacaoTurma.id = " + SituacaoTurma.ABERTA + ") or ");
			hql.append("(t.situacaoTurma.id = " + SituacaoTurma.CONSOLIDADA +")) and ");
			hql.append("t.disciplina.id = " + disciplina.getId() + " and ");
			hql.append("t.ano = " + ano + " and ");
			hql.append("t.periodo = " + periodo);
			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
			List<Turma> turmas = new ArrayList<Turma>();

			for (Object[] linha : resultado) {
				Turma t = new Turma();
				ComponenteCurricular componente = new ComponenteCurricular();
				ComponenteDetalhes detalhes = new ComponenteDetalhes();
				Unidade unidade = new Unidade();

				int a = 0;
				componente.setDetalhes(detalhes);
				componente.setUnidade(unidade);
				t.setDisciplina( componente );

				t.setId( (Integer) linha[a++] );
				t.setCodigo( (String) linha[a++] );
				componente.setCodigo( (String) linha[a++] );
				detalhes.setNome( (String) linha[a++] );				
				detalhes.setChTotal( (Integer) linha[a++] );
				t.setDescricaoHorario( (String) linha[a++] );
				unidade.setId((Integer) linha[a++]);
				t.setAno((Integer) linha[a++]);
				t.setPeriodo((Integer) linha[a++]);
				
				SituacaoTurma situacao = new SituacaoTurma();
				situacao.setId((Integer) linha[a++]);
				t.setSituacaoTurma(situacao);
				
				t.setDistancia((Boolean) linha[a++]);
				
				int linhaDocente = a++;
				if (!turmas.contains(t)) {
					if(linha[linhaDocente] != null)
						t.setDocentesTurmas(new HashSet<DocenteTurma>());
					turmas.add(t);
				} else {
					t = turmas.get(turmas.indexOf(t));
				}
				if(linha[linhaDocente] != null)
					t.addDocenteTurma((DocenteTurma) linha[linhaDocente]);				
			}
			return turmas;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}	
	

	/**
	 * Retorna as turmas de um componente, ano, período, situação indicados
	 *
	 * @param disciplina
	 * @param ano
	 * @param periodo
	 * @param situacoesTurma
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDisciplinaAnoPeriodoSituacao(ComponenteCurricular disciplina, int ano, int periodo, SituacaoTurma... situacoesTurma) throws DAOException {
		return findByDisciplinaAnoPeriodoNivelSituacao(disciplina, ano, periodo, null, situacoesTurma);
	}

	/**
	 * Busca as turmas pela disciplina, ano, período, nível e situação
	 *
	 * @param disciplina
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @param situacoesTurma
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDisciplinaAnoPeriodoNivelSituacao(ComponenteCurricular disciplina, int ano, int periodo, Character nivel, SituacaoTurma... situacoesTurma) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("FROM Turma ");

			hql.append(" WHERE disciplina.id = " + disciplina.getId());
			if (ano > 0)
				hql.append(" AND ano = " + ano);
			if (periodo > 0)
				hql.append(" AND periodo = " + periodo);

			if (nivel != null)
				hql.append(" AND disciplina.nivel = '" + nivel.toString() + "' ");

			if( situacoesTurma != null )
				hql.append(" AND situacaoTurma.id in " + gerarStringIn(situacoesTurma) );
			
			hql.append(" order by disciplina.codigo, ano, periodo, codigo");
			Query q = getSession().createQuery(hql.toString());

			@SuppressWarnings("unchecked")
			Collection<Turma> lista =  q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Busca as turmas pelo ano, período, situação, unidade e nível informados
	 *
	 * @param ano
	 * @param periodo
	 * @param idSituacao
	 * @param unid
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByAnoPeriodo(int ano, int periodo,Integer idSituacao, Integer unidade, char nivel,
			PagingInformation paging) throws DAOException {
			if (isEmpty(idSituacao))
				return findByAnoPeriodo(ano,periodo,null, unidade, null, null, nivel,paging);
			else 
				return findByAnoPeriodo(ano,periodo,new Integer[]{idSituacao}, unidade, null, null, nivel,paging);
	}

	/**
	 * Busca as turmas pelo ano, período, situação,
	 * departamento, centro e nível informados
	 *
	 * @param ano
	 * @param periodo
	 * @param idSituacao
	 * @param unid
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	private Collection<Turma> findByAnoPeriodo(Integer ano, Integer periodo, Integer[] idSituacao, Integer depto,
			Integer centro, String palavraChave, Character nivel,
			PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM Turma turma " +
					" inner join fetch turma.disciplina disciplina" +
					" inner join fetch disciplina.detalhes detalhes" +
					" left join fetch turma.usuarioConsolidacao usuario" +
					" left join fetch usuario.pessoa" +
					" WHERE turma.situacaoTurma.id != " + SituacaoTurma.EXCLUIDA + " ");

			if (!isEmpty(depto))
				hql.append(" AND disciplina.unidade.id = " + depto);
			if (!isEmpty(centro))
				hql.append(" AND disciplina.unidade.unidadeResponsavel.id = " + centro );
			if (!isEmpty(nivel) && NivelEnsino.isValido(nivel))
				hql.append(" AND disciplina.nivel = '" + nivel + "'");

			if (!isEmpty(idSituacao))
				hql.append(" AND turma.situacaoTurma.id IN " + gerarStringIn(idSituacao) + "");

			if(!isEmpty(palavraChave)){
				hql.append(" AND ( UPPER(turma.observacao) LIKE '%" + StringUtils.toAsciiAndUpperCase(palavraChave) + "%'");
				hql.append(" OR UPPER(disciplina.detalhes.nome_ascii) LIKE '%" + StringUtils.toAsciiAndUpperCase(palavraChave) + "%'");
				hql.append(" ) ");
			}

			if (!isEmpty(ano))
				hql.append(" AND turma.ano = " + ano);
			if (!isEmpty(periodo))
				hql.append(" AND turma.periodo = " + periodo);

			hql.append(" order by turma.ano desc, turma.periodo desc, disciplina.unidade.nome, disciplina.detalhes.nome, turma.codigo");
			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			@SuppressWarnings("unchecked")
			Collection<Turma> lista =  q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Busca as turmas abertas marcadas como pública pelo docente sem limitar o resultado
	 * @param idUnidade
	 * @param idUnidadeGestora
	 * @param palavraChave
	 * @param qtdCaracter
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findVisiveisByUnidade(Integer idUnidade, Integer idUnidadeGestora, String palavraChave, int qtdCaracter) throws DAOException {
		return findVisiveisByUnidade(idUnidade, idUnidadeGestora, palavraChave, null, qtdCaracter);
	}
	/**
	 * Busca as turmas abertas marcadas como pública pelo docente
	 *
	 * @param idUnidade
	 * @param palavraChave (qualquer palavra que faça parte nome do componente,
	 *  descrição e conteúdo do tópico de aula)
	 * @param qtdCaracter (quantidade mínima de caracter das palavras chaves para consulta)
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Turma> findVisiveisByUnidade(Integer idUnidade, Integer idUnidadeGestora, String palavraChave, Integer limite, int qtdCaracter) throws DAOException {
		
		try {

			StringBuffer hql = new StringBuffer();

			hql.append(" SELECT DISTINCT ON(campoOrdem) t.id_turma, t.ano, t.periodo, t.descricao_horario, disc.codigo ,d.ch_total, d.nome, ta.conteudo, ta.descricao, ");
			hql.append(" agrup.id_turma_agrupadora, d.nome , disc.id_disciplina,(ca.data_visualizacao_externa || '' || t.id_turma) as campoOrdem,  t.data_cadastro, t.distancia ");
			hql.append(" FROM ava.topico_aula ta RIGHT JOIN ensino.turma t USING( id_turma ) ");
			hql.append(" 	LEFT JOIN ensino.turma agrup ON agrup.id_turma_agrupadora = t.id_turma ");
			hql.append(" 	INNER JOIN ensino.situacao_turma st ON st.id_situacao_turma = t.id_situacao_turma ");
			hql.append("	INNER JOIN ensino.componente_curricular disc ON disc.id_disciplina = t.id_disciplina ");
			hql.append("	INNER JOIN comum.unidade u ON u.id_unidade = disc.id_unidade ");
			hql.append("	INNER JOIN ensino.componente_curricular_detalhes d ON d.id_componente_detalhes = disc.id_detalhe, ava.configuracoes_ava ca " );
			hql.append(" WHERE st.id_situacao_turma = " + SituacaoTurma.ABERTA + " AND ta.ativo = trueValue() ");
			hql.append("	AND ca.id_turma = t.id_turma AND ca.permite_visualizacao_externa = trueValue() ");

			if(!isEmpty(idUnidade))
				hql.append(" AND u.id_unidade = " + idUnidade);
			if(!isEmpty(idUnidadeGestora))
				hql.append(" AND u.id_gestora = " + idUnidadeGestora);

			// Somente quando o valor da palavra chave é passada
			if(!isEmpty(palavraChave)){

				palavraChave = StringUtils.toAsciiAndUpperCase(palavraChave);
				String[] palavras = palavraChave.split(" ");
				hql.append(" AND ( ");

				//Condição se existe cada palavra em nome do componente
				for (int i = 0; i < palavras.length; i++) {
					if(palavras[i].length()>= qtdCaracter){
						if(i > 0)
							hql.append(" AND ");
						hql.append("  sem_acento( upper(d.nome) )  LIKE '%" +  palavras[i] + "%' ");
					}
				}
					hql.append(" OR (");

				//Condição se existe cada palavra em descrição ou em conteúdo do tópico
				for (int i = 0; i < palavras.length; i++) {
					if(palavras[i].length()>= qtdCaracter ){
						if(i > 0)
							hql.append(" AND ");

						hql.append(" ( sem_acento( upper(ta.descricao) ) LIKE '%" + palavras[i] + "%' OR sem_acento( upper(ta.conteudo) ) LIKE '%" + palavras[i] + "%' ) ");
					}
				}
					hql.append(" ) OR ( ");

				//Condição se existe para cada palavra em um do componente ou descrição ou conteúdo do tópico de aula
				for (int i = 0; i < palavras.length; i++) {
					if(palavras[i].length()>= qtdCaracter ) {
						if(i > 0)
							hql.append(" AND ");

						hql.append(" ( sem_acento( upper(d.nome) ) LIKE '%"+ palavras[i]+ "%' OR  sem_acento( upper(ta.descricao) ) LIKE '%" + palavras[i] + "%'" );
						hql.append("  OR sem_acento( upper(ta.conteudo) ) LIKE '%" + palavras[i] + "%' ) ");
					}
				}
				hql.append(" ) OR disc.codigo = '"+palavraChave+"') ");

			}
			
			hql.append(" GROUP BY  t.id_turma , ca.data_visualizacao_externa , t.data_cadastro ,t.ano, t.periodo, t.descricao_horario, disc.codigo ");
			hql.append(" , d.ch_total, d.nome, ta.conteudo, ta.descricao, agrup.id_turma_agrupadora, d.nome , disc.id_disciplina, t.distancia ");
			
			//Caso não tenha efetuado a busca a ordenação será pela data que a turma disponibilizada para comunidade externa
			if( isEmpty(idUnidade) && isEmpty(palavraChave) && isEmpty(idUnidadeGestora) )
				hql.append(" ORDER BY campoOrdem DESC, t.data_cadastro DESC ");
			else
				hql.append(" ORDER BY campoOrdem DESC, t.id_turma DESC  ");

			Query q = getSession().createSQLQuery(hql.toString());
			List<Object[]> lista;			
			
			// Caso não tenha efetuado a busca, disponibiliza os últmas turmas disponibilizada para comunidade externa limitadas pela constante MAX_TURMAS_PUBLICAS 
			if( !isEmpty(limite) && isEmpty(idUnidade) && isEmpty(palavraChave) && isEmpty(idUnidadeGestora) )
				lista = q.setMaxResults(limite).list(); 
			else	
				lista = q.list();
			
			List<Turma> result = new ArrayList<Turma>();
			Turma t = new Turma();

			Integer codAnt = 0;
			String obs = "";

			for (int a = 0; a < lista.size(); a++) {

				Object[] colunas = lista.get(a);

				t.setId((Integer) colunas[0]);

				t.setAno((Integer) colunas[1]);
				t.setPeriodo((Integer) colunas[2]);
				t.setDescricaoHorario((String) colunas[3]);

				t.setDisciplina(new ComponenteCurricular());
				t.getDisciplina().setCodigo((String) colunas[4]);
				t.getDisciplina().setDetalhes(new ComponenteDetalhes());
				t.getDisciplina().getDetalhes().setChTotal((Short) colunas[5]);
				t.getDisciplina().getDetalhes().setNome((String) colunas[6]);
				t.getDisciplina().setId((Integer) colunas[11]);
				/*
				 *  Todos títulos descrição dos tópicos relacionados a
				 * palavra chave passada.
				*/

				if(colunas[7] != null)
					obs += " " + colunas[7];
				if(colunas[8] != null)
					obs += " " + colunas[8];
				if(colunas[10] != null)
					obs += " " + colunas[10];

				Integer idTurmaAgrupadora = (Integer) colunas[9];
				if( idTurmaAgrupadora != null ){
					t.setTurmaAgrupadora(new Turma());
					t.getTurmaAgrupadora().setId((Integer) colunas[9]);
				}
				
				t.setDistancia((Boolean) colunas[14]);

				if(!codAnt.equals(t.getId())){
					t.setObservacao(obs);
					result.add(t);
					t = new Turma();
					obs = "";
				}
				codAnt = (Integer) colunas[0];

			}
			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		
	}


	/**
	 *  Busca as turmas pelo ano, período, situação, unidade e nível informados
	 *
	 * @param ano
	 * @param periodo
	 * @param situacao
	 * @param nivel
	 * @param unid
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByAnoPeriodo(int ano, int periodo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Turma.class);
			c.add(Restrictions.eq("ano", ano));
			if (periodo > 0)
				c.add(Restrictions.eq("periodo", periodo));
			@SuppressWarnings("unchecked")
			Collection<Turma> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}



	/**
	 * Busca as turmas do docente externo informado com a situação informada, utiliza projeção para otimizar o desempenho
	 *
	 * @param docExt
	 * @param situacao
	 * @param fetchQtdAlunos TRUE caso deva trazer também os alunos da turma na mesma consulta
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocenteExternoOtimizado(DocenteExterno docExt, int situacao, boolean fetchQtdAlunos)
			throws DAOException {
		return findByDocenteExterno(docExt, null, null, fetchQtdAlunos, true, situacao);
	}


	/**
	 * Busca as turmas do servidor, situação e nível informados
	 *
	 * @param docente
	 * @param situacao
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocente(Servidor docente, int situacao,Character nivel) throws DAOException {
		return findByDocente(docente, situacao, nivel, null);
	}

	/**
	 * Consulta com projeção para otimizar o desempenho
	 *
	 * @param docente
	 * @param situacao
	 * @param nivel
	 * @param fetchQtdAlunos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocenteOtimizado(Servidor docente, Integer situacao,Character nivel, boolean fetchQtdAlunos)
			throws DAOException {
		return findByDocente(docente, nivel, situacao, null, fetchQtdAlunos, true);
	}

	/**
	 * Busca as turmas do servidor, situação, nível, ano, período informados
	 *
	 * @param docente
	 * @param situacao
	 * @param nivel
	 * @param ano
	 * @param periodo
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocente(Servidor docente, Character nivel, Integer ano, Integer periodo, PagingInformation paging, Integer... situacoes) throws DAOException {
		return findByDocente(docente, nivel, ano, periodo, paging, false, false, situacoes);
	}

	/**
	 * Busca as turmas do servidor, situação e nível informados
	 *
	 * @param docente
	 * @param situacao
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	private Collection<Turma> findByDocente(Servidor docente, int situacao,Character nivel, PagingInformation paging)
			throws DAOException {
		return findByDocente(docente, nivel, situacao, paging, false, true);
	}

	/**
	 *  Busca as turmas do docente externo, situação e nível informados
	 *
	 * @param docente
	 * @param situacao
	 * @param nivel
	 * @param paging
	 * @param fetchQtdAlunos TRUE caso deva trazer também os alunos da turma na mesma consulta
	 * @param withProjection TRUE caso deva utilizar projeção para otimizar o desempenho
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocenteExterno(DocenteExterno docente, Character nivel, PagingInformation paging,
			boolean fetchQtdAlunos, boolean withProjection, Integer... situacoes) throws DAOException{
		return findByDocenteExterno( docente, nivel, null, null, paging, fetchQtdAlunos, withProjection, situacoes );
	}

	/**
	 * Busca as turmas do docente externo, situação, nível, ano, período informados
	 *
	 * @param docente
	 * @param situacao
	 * @param nivel
	 * @param ano
	 * @param periodo
	 * @param paging
	 * @param fetchQtdAlunos TRUE caso deva trazer também os alunos da turma na mesma consulta
	 * @param withProjection TRUE caso deva utilizar projeção para otimizar o desempenho
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocenteExterno(DocenteExterno docente, Character nivel, Integer ano, Integer periodo, PagingInformation paging,
			boolean fetchQtdAlunos, boolean withProjection, Integer... situacoes) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			String projecao = "dt.turma.id," +
					" dt.turma.disciplina.codigo," +
					" dt.turma.disciplina.detalhes.codigo," +
					" dt.turma.disciplina.detalhes.id," +
					" dt.turma.disciplina.detalhes.codigo," +
					" dt.turma.disciplina.detalhes.nome, " +
					" dt.turma.disciplina.detalhes.chTotal," +
					" dt.turma.disciplina.detalhes.crAula," +
					" dt.turma.disciplina.detalhes.crLaboratorio," +
					" dt.turma.disciplina.detalhes.crEstagio," +
					" dt.turma.disciplina.nivel," +
					" dt.turma.disciplina.unidade.id," +
					" dt.turma.disciplina.unidade.gestoraAcademica.id," +
					" dt.turma.ano," +
					" dt.turma.periodo," +
					" dt.turma.descricaoHorario," +
					" dt.turma.local," +
					" dt.turma.codigo," +
					" dt.turma.situacaoTurma.id," +
					" dt.turma.situacaoTurma.descricao," +
					" dt.turma.distancia,"+
					" dt.chDedicadaPeriodo," +
					" p.id as dt.turma.polo.id," +
					" c.id as dt.turma.polo.cidade.id," +
					" c.nome as dt.turma.polo.cidade.nome," +
					" uf.id as dt.turma.polo.cidade.unidadeFederativa.id," +
					" uf.sigla as dt.turma.polo.cidade.unidadeFederativa.sigla," +
					" t.capacidadeAluno as dt.turma.capacidadeAluno," +
					" ta.id as dt.turma.turmaAgrupadora.id," +
					" ta.codigo as dt.turma.turmaAgrupadora.codigo," +
					" disc.id as dt.turma.turmaAgrupadora.disciplina.id," +
					" disc.codigo as dt.turma.turmaAgrupadora.disciplina.codigo," +
					" gestAgr.id as dt.turma.turmaAgrupadora.disciplina.unidade.gestoraAcademica.id";
			if (!withProjection)
				hql.append("select distinct dt.turma ");
			else
				hql.append("select " + HibernateUtils.removeAliasFromProjecao(projecao));
			hql.append(" from DocenteTurma dt " +
					" left join dt.turma t" +
					" left join dt.turma.turmaAgrupadora ta" +
					" left join t.polo p" +
					" left join p.cidade c" +
					" left join c.unidadeFederativa uf" +
					" left join t.turmaAgrupadora ta" +
					" left join ta.disciplina disc" +
					" left join disc.unidade unidAgr" +
					" left join unidAgr.gestoraAcademica gestAgr" +
					" where ");
			hql.append("dt.docenteExterno.id = :docenteId  ");

			if ( !isEmpty(situacoes) ) {
				hql.append(" and dt.turma.situacaoTurma.id in " + gerarStringIn(situacoes));
			} else {
				hql.append(" and dt.turma.situacaoTurma.id in "
						+ UFRNUtils.gerarStringIn(new int[] {SituacaoTurma.ABERTA, SituacaoTurma.CONSOLIDADA}));
			}

			if( nivel != null ){
				hql.append(" and dt.turma.disciplina.nivel='"+nivel+"' ");
			}

			if( ano != null )
				hql.append(" and dt.turma.ano = " + ano);
			if( periodo != null )
				hql.append(" and dt.turma.periodo = " + periodo);

			hql.append(" order by dt.turma.periodo desc");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("docenteId", docente.getId());

			preparePaging(paging, q);
			Collection<Turma> turmas = new ArrayList<Turma>();
			if (withProjection) {
				@SuppressWarnings("unchecked")
				List<Object[]> lista = q.list();
				Collection<DocenteTurma> docenteTurmas = HibernateUtils.parseTo(lista, projecao, DocenteTurma.class, "dt");
				for (DocenteTurma dt : docenteTurmas) {
					Turma turma = dt.getTurma();
					turma.setDocentesTurmas(new TreeSet<DocenteTurma>());
					turma.addDocenteTurma(dt);
					turmas.add(turma);
				}
			} else {
				@SuppressWarnings("unchecked")
				List<Turma> lista = q.list();
				turmas = lista;
			}
			if (fetchQtdAlunos) {
				if (turmas != null) {
					for (Turma turma : turmas) {
						Query qtd = getSession().createQuery(
								"select count(*) from "
										+ "MatriculaComponente where turma.id = :idTurma and "
										+ "situacaoMatricula.id = :situacao");
						qtd.setInteger("idTurma", turma.getId());
						qtd.setInteger("situacao", SituacaoMatricula.MATRICULADO.getId());
						turma.setQtdMatriculados(((Number) qtd.uniqueResult()).intValue());
					}
				}
			}
			return turmas;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna as turmas do docente sem considerar o ano/período
	 *
	 * @param docente
	 * @param nivel
	 * @param situacao
	 * @param paging
	 * @param fetchQtdAlunos
	 * @param withProjection
	 * @return
	 * @throws DAOException
	 */
	
	public Collection<Turma> findByDocente(Servidor docente, Character nivel, Integer situacao, PagingInformation paging,
			boolean fetchQtdAlunos, boolean withProjection) throws DAOException {

		if (situacao == null)
			return findByDocente(docente, nivel, null, null, paging, fetchQtdAlunos, withProjection, new Integer[0]);
		else
			return findByDocente(docente, nivel, null, null, paging, fetchQtdAlunos, withProjection, situacao);
	}
	
	/**
	 * Retorna todas as turmas de acordo com os parâmetros definidos.
	 * @param docente
	 * @param nivel
	 * @param ano
	 * @param periodo
	 * @param paging
	 * @param fetchQtdAlunos
	 * @param withProjection
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocente(Servidor docente, Character nivel, Integer ano, Integer periodo, PagingInformation paging,
			boolean fetchQtdAlunos, boolean withProjection, Integer... situacoes) throws DAOException {
		
		List<Character> niveis = new ArrayList<Character>();
		
		if(nivel != null)
			niveis.add(nivel);

		return findByDocente(docente, ano, periodo, niveis, paging,	fetchQtdAlunos, withProjection, situacoes);
		
	}


	/**
	 * Retorna as turmas do docente considerando o ano/período
	 *
	 * @param docente
	 * @param nivel
	 * @param situacao
	 * @param paging
	 * @param fetchQtdAlunos
	 * @param withProjection
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocente(Servidor docente, Integer ano, Integer periodo,  List<Character> niveis, PagingInformation paging,
			boolean fetchQtdAlunos, boolean withProjection, Integer... situacoes) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from DocenteTurma dt" +
					" left join dt.turma t" +
					" left join dt.turma.turmaAgrupadora ta" +
					" left join t.polo p" +
					" left join p.cidade c" +
					" left join c.unidadeFederativa uf" +
					" left join t.turmaAgrupadora ta" +
					" left join ta.situacaoTurma sit" +
					" left join ta.disciplina disc" +
					" left join disc.unidade unidAgr" +
					" left join unidAgr.gestoraAcademica gestAgr" +
					" where 1=1 ");

			if(docente != null)
				hql.append(" and dt.docente.id = "+docente.getId());

			if ( !isEmpty( situacoes ) ) {
				hql.append(" and dt.turma.situacaoTurma.id in " + gerarStringIn(situacoes));
			} else {
				hql.append(" and dt.turma.situacaoTurma.id in "
						+ UFRNUtils.gerarStringIn(new int[] {SituacaoTurma.ABERTA, SituacaoTurma.CONSOLIDADA}));
			}

			if( ano != null )
				hql.append(" and dt.turma.ano = " + ano);
			if( periodo != null )
				hql.append(" and dt.turma.periodo = " + periodo);

			if (!ValidatorUtil.isEmpty(niveis))
				hql.append(" and dt.turma.disciplina.nivel IN "+UFRNUtils.gerarStringIn(niveis)+" ");
			
			hql.append(" order by dt.turma.ano desc, dt.turma.periodo desc, dt.turma.disciplina.nivel,dt.turma.disciplina.detalhes.nome, dt.turma.codigo asc");

			String projecao;
			if (!withProjection) {
				hql.insert(0, "select dt.turma ");
				return findByDocenteSemProjecao(hql);
			} else
				projecao = "distinct dt.turma.id as turma.id," +
						" dt.turma.disciplina.id as turma.disciplina.id," +
						" dt.turma.disciplina.codigo as turma.disciplina.codigo," +
						" dt.turma.disciplina.detalhes.nome as turma.disciplina.detalhes.nome," +
						" dt.turma.disciplina.detalhes.chTotal as turma.disciplina.detalhes.chTotal," +
						" dt.turma.disciplina.detalhes.crAula as turma.disciplina.detalhes.crAula," +
						" dt.turma.disciplina.detalhes.crLaboratorio as turma.disciplina.detalhes.crLaboratorio," +
						" dt.turma.disciplina.detalhes.crEstagio as turma.disciplina.detalhes.crEstagio," +
						" dt.turma.disciplina.nivel as turma.disciplina.nivel," +
						" dt.turma.disciplina.unidade.id as turma.disciplina.unidade.id," +
						" dt.turma.disciplina.unidade.gestoraAcademica.id as turma.disciplina.unidade.gestoraAcademica.id," +
						" dt.turma.ano as turma.ano," +
						" dt.turma.periodo as turma.periodo, " +
						" dt.turma.distancia as turma.distancia, " +
						" dt.turma.descricaoHorario as turma.descricaoHorario," +
						" dt.turma.local as turma.local," +
						" dt.turma.codigo as turma.codigo," +
						" dt.turma.situacaoTurma.id as turma.situacaoTurma.id," +
						" dt.turma.situacaoTurma.descricao as turma.situacaoTurma.descricao," +
						" p.id as dt.turma.polo.id," +
						" c.nome as dt.turma.polo.cidade.nome," +
						" uf.id as dt.turma.polo.cidade.unidadeFederativa.id," +
						" uf.sigla as dt.turma.polo.cidade.unidadeFederativa.sigla," +
						" dt.chDedicadaPeriodo," +
						" t.capacidadeAluno as dt.turma.capacidadeAluno," +
						" ta.id as dt.turma.turmaAgrupadora.id," +
						" ta.codigo as dt.turma.turmaAgrupadora.codigo," +
						" ta.ano as dt.turma.turmaAgrupadora.ano," +
						" ta.periodo as dt.turma.turmaAgrupadora.periodo," +
						" sit.id as dt.turma.turmaAgrupadora.situacaoTurma.id," +
						" sit.descricao as dt.turma.turmaAgrupadora.situacaoTurma.descricao," +
						" disc as dt.turma.turmaAgrupadora.disciplina," +
						" unidAgr.id as dt.turma.turmaAgrupadora.disciplina.unidade.id,"+
						" gestAgr.id as dt.turma.turmaAgrupadora.disciplina.unidade.gestoraAcademica.id";			
						
			hql.insert(0, " ").insert(0, HibernateUtils.removeAliasFromProjecao(projecao)).insert(0, "select ");
			
			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			
			@SuppressWarnings("unchecked")
			Collection<DocenteTurma> lista = HibernateUtils.parseTo(q.list(), projecao, DocenteTurma.class, "dt");
			Collection<Turma> turmas = new ArrayList<Turma>();
			if (!isEmpty(lista)) {
				for (DocenteTurma dt : lista) {
					dt.getTurma().addDocenteTurma(dt);
					turmas.add(dt.getTurma());
				}
			}
			
			if (fetchQtdAlunos) {
				if (turmas != null) {
					for (Turma turma : turmas) {
						Query qtd = getSession().createQuery(
								"select count(*) from "
										+ "MatriculaComponente where turma.id = ? and "
										+ "situacaoMatricula.id in (?, ?, ?, ?, ?)");
						qtd.setInteger(0, turma.getId());
						qtd.setInteger(1, SituacaoMatricula.MATRICULADO.getId());
						qtd.setInteger(2, SituacaoMatricula.APROVADO.getId());
						qtd.setInteger(3, SituacaoMatricula.REPROVADO.getId());
						qtd.setInteger(4, SituacaoMatricula.REPROVADO_FALTA.getId());
						qtd.setInteger(5, SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId());
						//TODO: refazer a consulta dividindo os status. Do jeito que está abaixo, está ok porque é exibido o total. Mas caso precise, não será exibido os totais por status.
						turma.setQtdMatriculados(((Number) qtd.uniqueResult()).intValue());
					}
				}
			}
			return turmas;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna as turmas do docente sem utilizar projeção (utilizado nas
	 * operações da turma virtual).
	 * 
	 * @param hql
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<Turma> findByDocenteSemProjecao(StringBuffer hql)
			throws HibernateException, DAOException {
		Query q = getSession().createQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<Turma> turmas = q.list();
		return turmas;
	}

	/**
	 * Retorna todas as turmas que possuem o discente, situações de matrícula e situações da turma informados
	 *
	 * @param discente
	 * @param situacoesMatricula
	 * @param situacoesTurma
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findAllByDiscente(Discente discente, SituacaoMatricula[] situacoesMatricula, SituacaoTurma[] situacoesTurma) throws DAOException {
		try {
			String projecao = "md.turma.id as turma.id," +
					" md.turma.disciplina.detalhes.nome as turma.disciplina.detalhes.nome," +
					" md.turma.disciplina.codigo as turma.disciplina.codigo," +
					" md.turma.disciplina.detalhes.chTotal as turma.disciplina.detalhes.chTotal," +
					" md.turma.disciplina.detalhes.crAula as turma.disciplina.detalhes.crAula," +
					" md.turma.disciplina.detalhes.crLaboratorio as turma.disciplina.detalhes.crLaboratorio," +
					" md.turma.disciplina.detalhes.crEstagio as turma.disciplina.detalhes.crEstagio," +
					" md.turma.disciplina.nivel as turma.disciplina.nivel," +
					" md.turma.disciplina.unidade.id as turma.disciplina.unidade.id," +
					" md.turma.descricaoHorario as turma.descricaoHorario," +
					" md.turma.distancia as turma.distancia," +
					" md.turma.ano as turma.ano," +
					" md.turma.periodo as turma.periodo," +
					" md.turma.codigo as turma.codigo," +
					" md.turma.local as turma.local," +
					" md.turma.turmaAgrupadora.id as turma.turmaAgrupadora.id";
			StringBuffer hql = new StringBuffer();
			hql.append("select ");
			hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
			hql.append(" from MatriculaComponente md where ");
			if (situacoesMatricula != null)
				hql.append("md.situacaoMatricula.id in " + gerarStringIn(situacoesMatricula) + " and ");
			hql.append("md.discente.id = :discenteId  and md.turma.situacaoTurma.id in " + gerarStringIn(situacoesTurma)
					+ " order by md.turma.ano, md.turma.periodo, md.turma.disciplina.detalhes.nome asc");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("discenteId", discente.getId());

			@SuppressWarnings("unchecked")
			List<Turma> turmas = (List<Turma>) HibernateUtils.parseTo(q.list(), projecao, Turma.class, "turma");
			String idsTurmas = "";

			for (Turma t : turmas) {
				idsTurmas += (idsTurmas.equals("") ? "" : ",") + t.getId();
			}
			
			if (!isEmpty(turmas)) {
				// Descobre a quantidade de alunos matriculados nas turmas do discente.
				Query qtd = getSession().createSQLQuery(
						"select id_turma, count(id_matricula_componente) from ensino.matricula_componente " +
						"where id_turma in ("+idsTurmas+") and " +
						"id_situacao_matricula = " + SituacaoMatricula.MATRICULADO.getId() +
				" group by id_turma");
				
				@SuppressWarnings("unchecked")
				List <Object []> linhas = qtd.list();
				
				Map <Integer, Integer> mapaQuantidades = new HashMap <Integer, Integer> ();
				
				for (Object [] linha : linhas)
					mapaQuantidades.put(Integer.parseInt(""+linha[0]), Integer.parseInt(""+linha[1]));
				
				for (Turma turma : turmas){
					Integer mats = mapaQuantidades.get(turma.getId());
					turma.setQtdMatriculados(mats == null ? 0 : mats);
				}
			}

			return turmas;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todas as turmas do curso de lato informado, independente de ano ou período
	 *
	 * @param idCursoLato
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByCursoLato(int idCursoLato) throws DAOException{
		return findByCursoLato(idCursoLato, null, null);
	}

	/**
	 * Retorna todas as turmas do curso de lato informado
	 *
	 * @param idCursoLato
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByCursoLato(int idCursoLato, Integer ano, Integer periodo) throws DAOException {
		int turmasExcluidas[] = {SituacaoTurma.ABERTA, SituacaoTurma.CONSOLIDADA, SituacaoTurma.A_DEFINIR_DOCENTE};
		try {
			String hql = " FROM Turma t WHERE t.disciplina.id IN (";
			hql += " SELECT ccl.disciplina.id FROM ComponenteCursoLato ccl" +
					" WHERE ccl.cursoLato.id = :idCursoLato " +
					" and t.situacaoTurma.id in " +
					UFRNUtils.gerarStringIn(turmasExcluidas);

			if(!isEmpty(ano))
				hql += " AND t.ano = :ano ";

			if(!isEmpty(periodo))
				hql += " AND t.periodo = :periodo ";

			hql += " )";

			Query query = getSession().createQuery(hql);
			query.setInteger("idCursoLato", idCursoLato);

			if(!isEmpty(ano))
				query.setInteger("ano", ano);

			if(!isEmpty(periodo))
				query.setInteger("periodo", periodo);

			@SuppressWarnings("unchecked")
			Collection<Turma> lista = query.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

		/**
	 * Retorna todas as turmas com a situação informada que pertencem ao curso lato informado
	 *
	 * @param idCursoLato
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByCursoLatoSituacao(int idCursoLato, int... situacoes) throws DAOException {
		try {
			Query query = getSession().createQuery(
					"from Turma t where t.disciplina.id in " + "(select ccl.disciplina.id from ComponenteCursoLato ccl "
							+ "where ccl.cursoLato.id = :idCursoLato) and t.situacaoTurma.id in " + UFRNUtils.gerarStringIn(situacoes) );
			query.setInteger("idCursoLato", idCursoLato);
			@SuppressWarnings("unchecked")
			Collection<Turma> lista = query.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca as turmas pelo nome da disciplina, situação, unidade e nível informados
	 *
	 * @param nomeDisciplina
	 * @param situacaoTurma
	 * @param unidadeId
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByNomeDisciplina(String nomeDisciplina, int unidadeId, char nivel, int... situacaoTurma)
			throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select t, doc FROM Turma t left join t.disciplina d left join d.detalhes dt, Turma t2 left join t2.docentesTurmas as doc WHERE t.id = t2.id and ");
			if (unidadeId > 0)
				hql.append("d.unidade.id = " + unidadeId + " and ");
			if (nivel != 0)
				hql.append(" d.nivel = '" + nivel + "' and ");
			if (situacaoTurma != null)
				hql.append(" t.situacaoTurma.id in " + UFRNUtils.gerarStringIn(situacaoTurma) + " and ");
			hql.append( UFRNUtils.toAsciiUpperUTF8("dt.nome") +
					" LIKE " + UFRNUtils.toAsciiUpperUTF8("'%" + nomeDisciplina
					+ "%'") );
			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			List<Object[]> linhas = q.list();
			List<Turma> result = new ArrayList<Turma>();
			for (Object[] linha : linhas) {
				Turma t = (Turma) linha[0];

				if (!result.contains(t)) {
					if(linha[1] != null)
						t.setDocentesTurmas(new HashSet<DocenteTurma>());
					result.add(t);
				} else {
					t = result.get(result.indexOf(t));
				}
				if(linha[1] != null)
					t.addDocenteTurma((DocenteTurma) linha[1]);

			}

			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca as turmas pelo nome da disciplina, situação, unidade, nível, ano, período informados
	 *
	 * @param nomeDisciplina
	 * @param situacaoTurma
	 * @param unidadeId
	 * @param nivel
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByNomeDisciplinaAnoPeriodo(String nomeDisciplina, int unidadeId, int ano, int periodo, char nivel, int... situacaoTurma)
		throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from Turma t where ");
			if (unidadeId > 0)
				hql.append("t.disciplina.unidade.id = " + unidadeId + " and ");
			if (nivel != 0)
				hql.append("t.disciplina.nivel = '" + nivel + "' and ");
			if (situacaoTurma != null)
				hql.append("t.situacaoTurma.id in " + UFRNUtils.gerarStringIn(situacaoTurma) + " and ");

			hql.append( UFRNUtils.toAsciiUpperUTF8("t.disciplina.detalhes.nome") +
					" like " + UFRNUtils.toAsciiUpperUTF8("'%" + nomeDisciplina + "%'") );
			hql.append(" and t.ano = " + ano + " and t.periodo = " + periodo + " order by t.disciplina.detalhes.nome ");

			Query q = getSession().createQuery(hql.toString());

			@SuppressWarnings("unchecked")
			Collection<Turma> resultado = q.list();
			return resultado;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Retorna todas as matrículas da turma informada que possuem as situações também informadas
	 *
	 * @param idTurma
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculasByTurma(int idTurma, Collection<SituacaoMatricula> situacoes) throws DAOException {
		return findMatriculasByTurma(idTurma, situacoes.toArray(new SituacaoMatricula[situacoes.size()])) ;
	}

	/**
	 * Retorna todas as matrículas da turma informada que possuem as situações também informadas
	 *
	 * @param idTurma
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculasByTurma(int idTurma, SituacaoMatricula... situacoes) throws DAOException {

		try {
			// verifica o nível de ensino da turma
			String sqlNivel = "select turma.disciplina.nivel from Turma turma where turma.id = " + idTurma;
			Character nivel = (Character) getSession().createQuery(sqlNivel).uniqueResult();
			// projeção da consulta por discentes da turma
			StringBuilder sb = new StringBuilder("select mc.id, d.id, d.matricula, d.pessoa.nome,"
					+ " mc.turma.disciplina.id, mc.turma.disciplina.codigo, mc.turma.disciplina.detalhes.nome," +
					  " mc.turma.disciplina.numUnidades, mc.mediaFinal, mc.numeroFaltas,"
					+ " mc.situacaoMatricula.id, mc.situacaoMatricula.descricao, d.nivel, m");
			
			// realiza os joins de acordo com o nível de ensino.
			switch (nivel) {
				case 'F' :
				case 'T' : sb.append(", e, d.pessoa.cpf_cnpj, cr.nome from MatriculaComponente mc left join mc.discente d, Discente d1 left join d1.curriculo c left join c.matriz m,"
						+ " DiscenteTecnico dt join dt.discente d2 left join dt.turmaEntradaTecnico t left join t.especializacao e left join d.curso cr" 
						+ " where mc.turma.id = ? and d.id = d1.id and d.id = d2.id ");break;
				case 'I' :
				case 'L' : sb.append(", c.nome from MatriculaComponente mc left join mc.discente d left join d.curriculo c left join c.matriz m"
						+ " left join d.curso c where mc.turma.id = ?"); break;
				case 'S' : 
				case 'R' : 
				case 'G' : sb.append(" from MatriculaComponente mc left join mc.discente d left join d.curriculo c left join c.matriz m"
						+ " where mc.turma.id = ?"); break;
			}

			if (situacoes != null && situacoes.length > 0) {
				sb.append(" and mc.situacaoMatricula.id in ");
				sb.append(UFRNUtils.gerarStringIn(situacoes));
			}

			sb.append(" order by d.pessoa.nome ");

			Query q = getSession().createQuery(sb.toString());
			q.setInteger(0, idTurma);

			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();

			if (result != null && !result.isEmpty()) {

				List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();

				for (Object[] linha : result) {
					MatrizCurricular matriz = (MatrizCurricular) linha[13];
					EspecializacaoTurmaEntrada especializacao = null;
					Long cpf = null;
					nivel = (Character) linha[12];
					if (nivel == 'T' || nivel == 'F') {
						especializacao = (EspecializacaoTurmaEntrada) linha[14];
						cpf = (Long) linha[15];
					}

					MatriculaComponente mc = new MatriculaComponente();
					mc.setId((Integer) linha[0]);

					if (nivel == NivelEnsino.TECNICO || nivel == NivelEnsino.FORMACAO_COMPLEMENTAR) {
						mc.setDiscente(new DiscenteTecnico());
						mc.getDiscente().getCurso().setNome((String) linha[16]);
					} else
						mc.setDiscente(new Discente());

					mc.getDiscente().setId((Integer) linha[1]);
					mc.getDiscente().setMatricula((Long) linha[2]);
					mc.getDiscente().setPessoa(new Pessoa());
					mc.getDiscente().getPessoa().setNome((String) linha[3]);

					mc.setTurma(new Turma());
					mc.getTurma().setDisciplina(new ComponenteCurricular());
					mc.getTurma().getDisciplina().setId((Integer) linha[4]);
					mc.getTurma().getDisciplina().setCodigo((String) linha[5]);
					mc.getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes());
					mc.getTurma().getDisciplina().getDetalhes().setNome((String) linha[6]);
					mc.getTurma().getDisciplina().setNumUnidades((Integer) linha[7]);
					mc.setMediaFinal((Double) linha[8]);
					mc.setNumeroFaltas((Integer) linha[9]);
					mc.setSituacaoMatricula(new SituacaoMatricula());
					mc.getSituacaoMatricula().setId((Integer) linha[10]);
					mc.getSituacaoMatricula().setDescricao((String) linha[11]);
					mc.getDiscente().setNivel(nivel);

					if (matriz != null) {
						mc.getDiscente().setCurriculo(new Curriculo());
						mc.getDiscente().getCurriculo().setMatriz(matriz);
					}

					if (especializacao != null) {
						DiscenteTecnico discente = (DiscenteTecnico) mc.getDiscente();
						discente.setTurmaEntradaTecnico(new TurmaEntradaTecnico());
						discente.getTurmaEntradaTecnico().setEspecializacao(especializacao);
					}
					if(cpf != null)
						mc.getDiscente().getPessoa().setCpf_cnpj(cpf);

					if (nivel == NivelEnsino.LATO) {
						mc.getDiscente().getCurso().setNome((String) linha[14]);
					}
					
					matriculas.add(mc);

				}

				return matriculas;
			}

			return null;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todas as matrículas da turma informada que possuem as situações também informadas.
	 * Traz também os dados pessoais dos discentes.
	 *
	 * @param idTurma
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatriculaComponente> findMatriculasDadosPessoaisByTurma(int idTurma, Collection<SituacaoMatricula> situacoes) throws DAOException {

		try {
			StringBuilder sb = new StringBuilder("select mc.discente.id, mc.discente.matricula, mc.discente.pessoa.nome, mc.discente.pessoa.sexo, "
					+ " mc.discente.pessoa.nomePai, mc.discente.pessoa.nomeMae, mc.discente.pessoa.pais.nome, mc.discente.pessoa.municipio.nome, "
					+ " mc.discente.pessoa.cpf_cnpj, mc.discente.pessoa.identidade.numero, mc.discente.pessoa.identidade.orgaoExpedicao, "
					+ " uf.sigla, mc.discente.pessoa.dataNascimento, "
					+ " l.descricao, e.logradouro, "
					+ " e.numero, e.bairro, e.cep, "
					+ " cidade.nome, ue.sigla, "
					+ " mc.situacaoMatricula.id, mc.situacaoMatricula.descricao, mc.discente.pessoa.telefone, mc.discente.pessoa.celular, "
					+ " cb.numero, cb.agencia, b.denominacao, mc.id, mc.discente.periodoAtual "
					+ " from MatriculaComponente mc " +
							"left join mc.discente.pessoa.identidade.unidadeFederativa as uf " +
							"left join mc.discente.pessoa.enderecoContato as e " +
							"left join mc.discente.pessoa.contaBancaria as cb " +
							"left join mc.discente.pessoa.contaBancaria.banco as b " +
							"left join e.municipio as cidade " +
							"left join e.unidadeFederativa ue " +
							"left join e.tipoLogradouro l"

					+ " where mc.turma.id = ? "		);

			if (situacoes != null && !situacoes.isEmpty()) {
				sb.append("and mc.situacaoMatricula.id in ");
				sb.append(UFRNUtils.gerarStringIn(situacoes));
			}

			sb.append(" order by mc.discente.pessoa.nome ");

			Query q = getSession().createQuery(sb.toString());
			q.setInteger(0, idTurma);
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();

			if (result != null && !result.isEmpty()) {

				List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();

				int col;
				for (Object[] linha : result) {
					col = 0;
					MatriculaComponente mc = new MatriculaComponente();

					mc.setDiscente(new Discente());
					mc.getDiscente().setId((Integer) linha[col++]);
					mc.getDiscente().setMatricula((Long) linha[col++]);
					mc.getDiscente().setPessoa(new Pessoa());
					mc.getDiscente().getPessoa().setNome((String) linha[col++]);
					mc.getDiscente().getPessoa().setSexo((Character)linha[col++]);
					mc.getDiscente().getPessoa().setNomePai((String)linha[col++]);
					mc.getDiscente().getPessoa().setNomeMae((String)linha[col++]);
					mc.getDiscente().getPessoa().getPais().setNome((String)linha[col++]);
					mc.getDiscente().getPessoa().getMunicipio().setNome((String)linha[col++]);
					mc.getDiscente().getPessoa().setCpf_cnpj((Long) linha[col++]);
					mc.getDiscente().getPessoa().setIdentidade(new Identidade());
					mc.getDiscente().getPessoa().getIdentidade().setNumero((String) linha[col++]);
					mc.getDiscente().getPessoa().getIdentidade().setOrgaoExpedicao((String) linha[col++]);
					mc.getDiscente().getPessoa().getIdentidade().setUnidadeFederativa(new UnidadeFederativa());
					mc.getDiscente().getPessoa().getIdentidade().getUnidadeFederativa().setSigla((String) linha[col++]);
					mc.getDiscente().getPessoa().setDataNascimento((Date) linha[col++]);
					mc.getDiscente().getPessoa().setEnderecoContato(new Endereco());
					mc.getDiscente().getPessoa().getEnderecoContato().getTipoLogradouro().setDescricao((String) linha[col++]);
					mc.getDiscente().getPessoa().getEnderecoContato().setLogradouro((String) linha[col++]);
					mc.getDiscente().getPessoa().getEnderecoContato().setNumero((String) linha[col++]);
					mc.getDiscente().getPessoa().getEnderecoContato().setBairro((String) linha[col++]);
					mc.getDiscente().getPessoa().getEnderecoContato().setCep((String) linha[col++]);
					mc.getDiscente().getPessoa().getEnderecoContato().getMunicipio().setNome((String) linha[col++]);
					mc.getDiscente().getPessoa().getEnderecoContato().getUnidadeFederativa().setSigla((String) linha[col++]);
					mc.setSituacaoMatricula(new SituacaoMatricula());
					mc.getSituacaoMatricula().setId((Integer) linha[col++]);
					mc.getSituacaoMatricula().setDescricao((String) linha[col++]);
					mc.getDiscente().getPessoa().setTelefone((String) linha[col++]);
					mc.getDiscente().getPessoa().setCelular((String) linha[col++]);
					mc.getDiscente().getPessoa().setContaBancaria(new ContaBancaria());
					mc.getDiscente().getPessoa().getContaBancaria().setNumero((String) linha[col++]);
					mc.getDiscente().getPessoa().getContaBancaria().setAgencia((String) linha[col++]);
					mc.getDiscente().getPessoa().getContaBancaria().getBanco().setDenominacao((String) linha[col++]);
					mc.setId((Integer) linha[col++]);
					mc.getDiscente().setPeriodoAtual((Integer) linha[col++]);

					matriculas.add(mc);

				}

				return matriculas;
			}

			return null;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna todas as matrículas da turma informada .
	 *
	 * @param idTurma
	 * @return
	 */
	public Collection<MatriculaComponente> findMatriculasAConsolidar(Turma turma) {
		
		return findParticipantesTurma(turma.getId(), null, false, turma.isAgrupadora(), ConsolidacaoUtil.getSituacaoesAConsolidar());
	}
	
	/**
	 * Retorna uma lista contendo todas as presenças de uma turma. 
	 * @param idTurma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List <Object []> findPresencaPlanilhaByTurma (int idTurma) throws DAOException{
		String auxSituacoes = "";
		
		for (SituacaoMatricula s : SituacaoMatricula.getSituacoesFrequencia())
			auxSituacoes = auxSituacoes.equals("") ? "" + s.getId() : auxSituacoes + "," + s.getId(); 

			String sql = "select m.id_matricula_componente, d.matricula, p.nome, cast (extract ('day' from data) as integer) as dia, " +
					"cast(extract ('month' from data) as integer) as mes, frequencia, id_frequencia, " +
					"false as alterado, 0 as maxFaltas, d.id_discente, data, case when m.id_situacao_matricula = "+SituacaoMatricula.TRANCADO.getId()+" then true else false end " +
					"from discente d " +
					"join ensino.matricula_componente m on m.id_discente = d.id_discente and m.id_situacao_matricula in ("+auxSituacoes+") " +
					"join comum.pessoa p using (id_pessoa) " +
					"left join ensino.frequencia_aluno f on f.id_discente = d.id_discente and f.id_turma = " + idTurma + " " +
					"where m.id_turma = " + idTurma + " and f.ativo = trueValue() " +
					"order by p.nome, d.id_discente, f.data";
		
		
		Query q = getSession().createSQLQuery(sql);
		
		@SuppressWarnings("unchecked")
		List <Object []> rs = q.list();
		return rs;
	}


	/**
	 * Retorna a MatriculaComponente da turma do aluno, indicando se é para buscar nas subturmas ou não.
	 * 
	 * @param idTurma
	 * @param idDiscente
	 * @param buscarNasSubturmas
	 * @return
	 */
	public Collection<MatriculaComponente> findMatriculasAConsolidar(int idTurma, Integer idDiscente, boolean buscarNasSubturmas) {
		return findParticipantesTurma(idTurma, idDiscente, false, buscarNasSubturmas, ConsolidacaoUtil.getSituacaoesAConsolidar());
	}

	/**
	 * Retorna todos os discentes que participam ou participaram da turma informada, ou seja, os discentes que possuem a situação de matrícula:
	 * MATRICULADO, APROVADO, REPROVADO, REPROVADO POR FALTA, REPROVADO POR MEDIA E FALTA, EM ESPERA
	 *
	 * @param idTurma
	 * @return
	 */
	public Collection<MatriculaComponente> findParticipantesTurma(int idTurma){
		return findParticipantesTurma(idTurma, null, true, false, ConsolidacaoUtil.getSituacaoesAConsolidar());
	}
	
	/**
	 * Retorna todas as matrículas de acordo com as turmas setadas no parâmetro.
	 * @param idTurmas
	 * @return
	 */
	public Collection<MatriculaComponente> findEmailsParticipantesTurmas(List <Integer> idTurmas){
		return findEmailsParticipantesTurmas(idTurmas, SituacaoMatricula.MATRICULADO);
	}

	/**
	 * Retorna todos os discentes que participam ou participaram da turma, discente e situações informadas
	 *
	 * @param idTurma
	 * @param idDiscente
	 * @param pegarUsuario diz se é pra trazer o usuário dos alunos retornados
	 * @param situacoes
	 * @return
	 */
	public Collection<MatriculaComponente> findParticipantesTurma(int idTurma, Integer idDiscente, boolean pegarUsuario, boolean buscarNasSubturmas, SituacaoMatricula... situacoes) {
		return findParticipantesTurma(idTurma, idDiscente, pegarUsuario, buscarNasSubturmas, Arrays.asList(situacoes));
	}
	
	/**
	 * Retorna todos os discentes que participam ou participaram da turma, discente e situações informadas
	 *
	 * @param idTurma
	 * @param situacoes
	 * @return
	 */
	public Collection<MatriculaComponente> findParticipantesTurma(int idTurma,List<SituacaoMatricula> situacoes) {
		return findParticipantesTurma(idTurma, null, true, false, situacoes);
	}
	
	/**
	 * Retorna todos os discentes que participam ou participaram da turma, discente e situações informadas
	 *
	 * @param idTurma
	 * @param idDiscente
	 * @param pegarUsuario diz se é pra trazer o usuário dos alunos retornados
	 * @param situacoes
	 * @return
	 */
	private Collection<MatriculaComponente> findParticipantesTurma(int idTurma, Integer idDiscente, boolean pegarUsuario, boolean buscarNasSubturmas, List<SituacaoMatricula> situacoes) {
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet result = null;
		
		try {
			String idTurmas = "" + idTurma;
			
			// Se for para buscar nas subturmas, procura as ids das subturmas.
			if (buscarNasSubturmas){
				@SuppressWarnings("unchecked")
				List <Integer> ids = getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora = " + idTurma).list();
				
				for (Integer id : ids)
					idTurmas += "," + id;
			}
			
			
			idTurmas = "("+idTurmas+")";
			
			// Ano e período utilizados na busca da metodologia de educação EAD
			String anoPeriodo = getAnoPeriodoTurma(idTurma);
						
			int[] statusPossiveis = new int[] { StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.CONCLUIDO,
					StatusDiscente.CANCELADO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.TRANCADO, StatusDiscente.DEFENDIDO, StatusDiscente.ATIVO_DEPENDENCIA};
	
			String sql =
					"select mc.id_matricula_componente, d.id_discente, d.matricula, d.nivel, p.id_pessoa, p.nome, p.email as emailPessoa, p.cpf_cnpj, u.id_usuario, u.login, u.email as emailUsuario, t.id_turma, t.codigo as codigo_turma, t.ano, t.periodo, " +
					"cc.id_disciplina, cc.codigo, cc.num_unidades, cd.id_componente_detalhes, cd.nome as nome_disciplina, cd.ch_total, mc.media_final, mc.numero_faltas, mc.recuperacao, mc.ano as mat_ano, mc.periodo as mat_periodo, " +
					"mc.apto, sm.id_situacao_matricula, sm.descricao as descricao_sm, un.id_gestora_academica, c.id_curso, c.nome as nome_curso, d.id_foto, po.id_polo, ma.metodo_avaliacao, " +
					"ma.id, ma.porcentagem_tutor, ma.porcentagem_professor, ma.numero_aulas, cc.id_bloco_subunidade, nu.id_nota_unidade, nu.unidade, nu.nota, nu.faltas, " +
					"nee.id_solicitacao_apoio_nee, nee.justificativa_solicitacao, nee.data_cadastro, nee.parecer_comissao, nee.ativo, nee.id_status_atendimento, id_tipo_atividade," +
					"mod.id_modalidade_educacao, unid.sigla, s.id_serie, s.id_curso as id_curso_medio " +
				
					"from ensino.matricula_componente mc left join ensino.nota_unidade nu using (id_matricula_componente) join ensino.turma t on mc.id_turma = t.id_turma "+
					"join ensino.componente_curricular cc on t.id_disciplina = cc.id_disciplina left join ensino.componente_curricular_detalhes cd using (id_componente_detalhes) "+
					"left join comum.unidade un on cc.id_unidade = un.id_unidade join discente d using (id_discente) join comum.pessoa p using (id_pessoa) "+
					"join ensino.situacao_matricula sm using (id_situacao_matricula)" +
					"left join (" +
						"curso c left join ead.metodologia_avaliacao ma on c.id_curso = ma.id_curso and ma.ativa = true " +
						" and coalesce((ma.ano_inicio * 10) + ma.periodo_inicio, 0)  <= "+anoPeriodo+" "+
						" and coalesce((ma.ano_fim    * 10) + ma.periodo_fim, "+anoPeriodo+")  >= "+anoPeriodo+" "+
						"inner join comum.modalidade_educacao mod on (c.id_modalidade_educacao = mod.id_modalidade_educacao) " +
						"inner join comum.unidade unid on (c.id_unidade = unid.id_unidade)" +
					") on d.id_curso = c.id_curso " +
					"left join (graduacao.discente_graduacao dg join ead.polo po using (id_polo)) on dg.id_discente_graduacao = d.id_discente " +
					"left join comum.usuario u on u.id_pessoa = p.id_pessoa "+
					"left join nee.solicitacao_apoio_nee nee on nee.id_discente = d.id_discente and nee.ativo = trueValue() and nee.id_status_atendimento = "+StatusAtendimento.EM_ATENDIMENTO + " " +
					"left join medio.serie s using (id_serie)" +
					"where ( nu.id_nota_unidade is null or nu.ativo = trueValue() ) and mc.id_turma in " + idTurmas + " and d.status in " + gerarStringIn(statusPossiveis) + " " +
					(idDiscente  != null ?  "and mc.id_discente = " + idDiscente + " " : "" ) +
					"and mc.id_situacao_matricula in " + gerarStringIn(situacoes) + " order by translate(p.nome_ascii, ' ', '0') asc, nu.unidade asc";
			
			
			
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql.toString());
			result = st.executeQuery();

			if (result != null) 
				return populaMatriculasComponentes(pegarUsuario, result);
				
		} catch (Exception e) {
			throw new TurmaVirtualException(e);
		} finally {
			closeResultSet(result);
			closeStatement(st);
			Database.getInstance().close(con);
		} 

		return null;		
	}
	
	/**
	 * Retorna a string com o ano e período da turma utilizado na busca de sua metodologia de avaliação.
	 *
	 * @param idTurma
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String getAnoPeriodoTurma(int idTurma) throws HibernateException, DAOException {
		
		Query  q = getSession().createSQLQuery("select t.ano , t.periodo from ensino.turma t where t.id_turma = "+idTurma);	
		List<Object[]> result = q.list();
		
		Integer ano = 0;
		Integer periodo = 0;
		
		if ( result != null )
		{	
			for (Object[] linha : result) {
				Integer i = 0;
				ano = (Integer)linha[i++];
				periodo = ((Number)linha[i++]).intValue();
			}
		}
		
		if (periodo == 3)
			periodo = 1;
		else if (periodo == 4)
			periodo = 2;
		
		String res = ano.toString()+periodo.toString();		
		return res;
	}

	
	/**
	 * Retorna os dados necessários para enviar notificações aos discentes ativos das turmas passadas.
	 * 
	 * @param idsTurmas
	 * @param situacoes
	 * @return
	 */
	private Collection<MatriculaComponente> findEmailsParticipantesTurmas(List <Integer> idsTurmas, SituacaoMatricula ... situacoes) {

		int[] statusPossiveis = new int[] { StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.CONCLUIDO,
				StatusDiscente.CANCELADO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.TRANCADO, StatusDiscente.DEFENDIDO};

		String ids = UFRNUtils.gerarStringIn(idsTurmas);
		
		String sql = "(select mc.id_matricula_componente, mc.id_situacao_matricula, u.email " +
				"from ensino.matricula_componente mc " +
				"join discente d using (id_discente) " +
				"join comum.usuario u using (id_pessoa) " +
				"join ensino.turma t using (id_turma) " +
				"left join ensino.turma ta on t.id_turma_agrupadora = ta.id_turma " +
				"where d.status in " + gerarStringIn(statusPossiveis) + " " +
				"and (mc.id_turma in " + ids + ") " +
				"and mc.id_situacao_matricula in " + gerarStringIn(situacoes)+") " +
				"union " +
				"(select mc.id_matricula_componente, mc.id_situacao_matricula, u.email " +
				"from ensino.matricula_componente mc " +
				"join discente d using (id_discente) " +
				"join comum.usuario u using (id_pessoa) " +
				"join ensino.turma t using (id_turma) " +
				"left join ensino.turma ta on t.id_turma_agrupadora = ta.id_turma " +
				"where d.status in " + gerarStringIn(statusPossiveis) + " " +
				"and (ta.id_turma in " + ids + ") " +
				"and mc.id_situacao_matricula in " + gerarStringIn(situacoes)+")";
		
		try {
			Query q = getSession().createSQLQuery(sql);
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();

			if (result != null && !result.isEmpty()){
				
				List <MatriculaComponente> matriculas = new ArrayList <MatriculaComponente> ();
				
				for (Object [] l : result){
					MatriculaComponente m = new MatriculaComponente ();
					m.setId((Integer) l[0]);
					
					m.setSituacaoMatricula(new SituacaoMatricula((Integer) l[1]));
					
					Usuario u = new Usuario ();
					u.setEmail((String) l[2]);
					m.setDiscente(new Discente());
					m.getDiscente().setUsuario(u);
					
					matriculas.add(m);
				}
				
				return matriculas;
			}
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}

		return null;
	}

	/**
	 * Converte o vetor de objetos passado em uma lista de MatriculaComponente.
	 * 
	 * @param pegarUsuario
	 * @param result
	 * @return
	 * @throws SQLException 
	 * @throws DataAccessException 
	 */
	private List<MatriculaComponente> populaMatriculasComponentes(boolean pegarUsuario, ResultSet rs) throws DataAccessException, SQLException {
		List<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
		
		try {
			while (rs.next()) {
				MatriculaComponente mc = new MatriculaComponente();
				mc.setId(rs.getInt("id_matricula_componente"));
				if (matriculas.contains(mc)) {
					mc = matriculas.get(matriculas.indexOf(mc));
				} else {
					mc.setDiscente(new Discente());
					mc.getDiscente().setId(rs.getInt("id_discente"));
					
					if (rs.getObject("matricula") != null)
						mc.getDiscente().setMatricula(rs.getLong("matricula") );
						
					mc.getDiscente().setNivel(rs.getString("nivel").charAt(0));
						
					mc.getDiscente().setPessoa(new Pessoa());
					mc.getDiscente().getPessoa().setId(rs.getInt("id_pessoa"));
					mc.getDiscente().getPessoa().setNome(rs.getString("nome"));
					mc.getDiscente().getPessoa().setEmail(rs.getString("emailPessoa"));
						
					if (rs.getObject("cpf_cnpj") != null)
						mc.getDiscente().getPessoa().setCpf_cnpj(rs.getLong("cpf_cnpj"));
	
					if (pegarUsuario) {
						try {
							@SuppressWarnings("unchecked")
							Map<String, Object> map = getJdbcTemplate().queryForMap("select id_usuario, login, email, id_foto from comum.usuario where id_pessoa=? and inativo=falseValue() " + BDUtils.limit(1), new Object[] { mc.getDiscente().getPessoa().getId() });
							if (map != null) {
								mc.getDiscente().setUsuario(new Usuario());
								mc.getDiscente().getUsuario().setId((Integer) map.get("id_usuario"));
								mc.getDiscente().getUsuario().setLogin((String) map.get("login"));
								mc.getDiscente().getUsuario().setEmail((String) map.get("email"));
								mc.getDiscente().getUsuario().setIdFoto((Integer) map.get("id_foto"));
							}
						} catch(EmptyResultDataAccessException e) {
						// Não existe usuário, não fazer nada.
						}
					} else {					
						if (rs.getObject("id_usuario") != null) {
							mc.getDiscente().setUsuario(new Usuario());
							mc.getDiscente().getUsuario().setId(rs.getInt("id_usuario"));
							mc.getDiscente().getUsuario().setLogin(rs.getString("login"));
							mc.getDiscente().getUsuario().setEmail(rs.getString("emailUsuario"));
						}
					}
	
					mc.setTurma(new Turma(rs.getInt("id_turma")));
					mc.getTurma().setCodigo(rs.getString("codigo_turma"));
					mc.getTurma().setAno(rs.getInt("ano"));
					mc.getTurma().setPeriodo(rs.getInt("periodo"));
						
					mc.getTurma().setDisciplina(new ComponenteCurricular(rs.getInt("id_disciplina")));
					mc.getTurma().getDisciplina().setCodigo(rs.getString("codigo"));
					if( rs.getObject("id_tipo_atividade") != null ){
						mc.getTurma().getDisciplina().setTipoAtividade(new TipoAtividade(rs.getInt("id_tipo_atividade")));
					}
						
						
					if (rs.getObject("num_unidades") != null)
						mc.getTurma().getDisciplina().setNumUnidades(rs.getInt("num_unidades"));
	
					mc.getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes(rs.getInt("id_componente_detalhes")));
					mc.getTurma().getDisciplina().getDetalhes().setNome(rs.getString("nome_disciplina"));
										
					if (rs.getObject("ch_total") != null)
						mc.getTurma().getDisciplina().getDetalhes().setChTotal(rs.getInt("ch_total"));
						
					if (rs.getObject("media_final") != null)
						mc.setMediaFinal(rs.getDouble("media_final"));
						
					mc.setNumeroFaltas(rs.getInt("numero_faltas"));
						
					if (rs.getObject("recuperacao") != null)
						mc.setRecuperacao(rs.getDouble("recuperacao"));
						
					mc.setAno(rs.getShort("mat_ano"));
					mc.setPeriodo(rs.getByte("mat_periodo"));
					
					mc.setApto(rs.getBoolean("apto"));
						
					mc.setSituacaoMatricula(new SituacaoMatricula(rs.getInt("id_situacao_matricula")));
					mc.getSituacaoMatricula().setDescricao(rs.getString("descricao_sm"));
						
					if(rs.getObject("id_gestora_academica") != null){
						mc.getTurma().getDisciplina().setUnidade(new Unidade());
						mc.getTurma().getDisciplina().getUnidade().setGestoraAcademica(new Unidade(rs.getInt("id_gestora_academica")));
					}
						
					mc.getTurma().setId(rs.getInt("id_turma"));
					mc.getTurma().getDisciplina().setId(rs.getInt("id_disciplina"));
						
					mc.getDiscente().setCurso(new Curso());
					mc.getDiscente().getCurso().setModalidadeEducacao(new ModalidadeEducacao(rs.getInt("id_modalidade_educacao")));
					mc.getDiscente().getCurso().setUnidade(new Unidade());
					mc.getDiscente().getCurso().getUnidade().setSigla(rs.getString("sigla"));
						
					if (rs.getObject("id_curso") != null)
						mc.getDiscente().getCurso().setId(rs.getInt("id_curso"));
					mc.getDiscente().getCurso().setNome(rs.getString("nome_curso"));
	
					if (mc.getDiscente().getUsuario() != null)
						mc.getDiscente().setIdFoto(mc.getDiscente().getUsuario().getIdFoto());
					else
						mc.getDiscente().setIdFoto(rs.getInt("id_foto"));
	
					// Turmas de Ensino a Distância tem alguns parâmetros diferentes...
						
					if (rs.getObject("id_polo") != null && !mc.getTurma().isLato() && !mc.getTurma().getDisciplina().isEstagio()) {
						Integer metodologiaAvaliacao = rs.getInt("metodo_avaliacao");
						if (metodologiaAvaliacao == null)
							throw new TurmaVirtualException("O curso " + mc.getDiscente().getCurso().getDescricao() + " não tem metodologia de avaliação definida. Por favor, entre em contato com a SEDIS.");
	
						mc.getTurma().setPolo(new Polo(rs.getInt("id_polo")));
					}
	
					mc.setComponente(mc.getTurma().getDisciplina());
						
					if (rs.getObject("id_bloco_subunidade") != null)
						mc.getComponente().setBlocoSubUnidade(new ComponenteCurricular(rs.getInt("id_bloco_subunidade")));
						
					mc.setNotas(new ArrayList<NotaUnidade>());
					
					if (rs.getObject("id_serie") != null){
						Serie s = new Serie(rs.getInt("id_serie"));
						s.setCursoMedio(new CursoMedio(rs.getInt("id_curso_medio")));
						mc.setSerie(s);
					}
	
					matriculas.add(mc);
				}
	
				// Se tiver nota_unidade e for do mesmo usuário
				boolean mesmoUsuario = true;
					
				if (rs.getObject("id_usuario") != null && mc.getDiscente().getUsuario() != null)
					mesmoUsuario = (   rs.getInt("id_usuario")  ==   mc.getDiscente().getUsuario().getId()  );
					
				if (rs.getObject("id_nota_unidade") != null && mesmoUsuario){
						
					NotaUnidade nota = new NotaUnidade ();
						
					if (rs.getObject("id_nota_unidade") != null)
						nota.setId(rs.getInt("id_nota_unidade"));
						
					if (rs.getObject("unidade") != null)
						nota.setUnidade(rs.getByte("unidade"));
						
					if (rs.getObject("nota") != null)
						nota.setNota(rs.getDouble("nota"));
						
					if (rs.getObject("faltas") != null)
						nota.setFaltas(rs.getShort("faltas"));
						
					nota.setMatricula(mc);
					
					mc.getNotas().add(nota);
				}
					
				if (rs.getObject("id_solicitacao_apoio_nee") != null && mesmoUsuario ){
						
					SolicitacaoApoioNee solNee = new SolicitacaoApoioNee();
						
					if (rs.getObject("id_solicitacao_apoio_nee") != null)
						solNee.setId(rs.getInt("id_solicitacao_apoio_nee"));
						
					if (rs.getObject("justificativa_solicitacao") != null)
						solNee.setJustificativaSolicitacao(rs.getString("justificativa_solicitacao"));
						
					if (rs.getObject("data_cadastro") != null)
						solNee.setDataCadastro(rs.getDate("data_cadastro"));
						
					if (rs.getObject("parecer_comissao") != null)
						solNee.setParecerComissao(rs.getString("parecer_comissao"));
						
					if (rs.getObject("id_status_atendimento") != null)
						solNee.setStatusAtendimento(new StatusAtendimento( rs.getInt("id_status_atendimento") ) );
						
					boolean addSolicitacaoNee = true;
					for (SolicitacaoApoioNee sol : mc.getDiscente().getSolicitacoesApoioNee()) {
						if ( sol.getId() == solNee.getId() )
							addSolicitacaoNee = false;
					}
					if (addSolicitacaoNee) mc.getDiscente().getSolicitacoesApoioNee().add(solNee);
						
				}
			}
		} finally {
			closeResultSet(rs);			
		}
		
		return matriculas;
	}


	/**
	 * Retorna o total de alunos da turma informada que estão com as situações de matrícula também informado
	 * @param idTurma
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public long findQtdAlunosPorTurma(int idTurma, SituacaoMatricula... situacoes) throws DAOException {
		try {
			Query q = getSession().createQuery(
					"select count(m.turma.id) from MatriculaComponente m where m.turma.id = :idTurma " +
					"and m.situacaoMatricula.id in " + gerarStringIn(situacoes));
			q.setInteger("idTurma", idTurma);
			return (Long) q.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		}

	/**
	 * Retorna o total de alunos da turma e situação turma informada
	 *
	 * @param idTurma
	 * @param idSituacaoMatricula
	 * @return
	 * @throws DAOException
	 */
	public long findQtdAlunosPorTurma(int idTurma, int idSituacaoMatricula) throws DAOException {

		try {
			Query q = getSession().createQuery(
					"select count(m.turma.id) from MatriculaComponente m where m.turma.id = :idTurma and m.situacaoMatricula.id = :idSituacaoMatricula");
			q.setInteger("idTurma", idTurma);
			q.setInteger("idSituacaoMatricula", idSituacaoMatricula);
			return (Long) q.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todas as notas dos alunos da turma, unidade informada
	 *
	 * @param idTurma
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<NotaUnidade> findNotasByTurmaUnidade(int idTurma, int unidade) throws DAOException {

		try {
			Criteria c = getCriteria(NotaUnidade.class);
			c.createCriteria("matricula").add(eq("turma.id", idTurma));
			c.add(eq("unidade", (byte) unidade));
			c.add(eq("ativo", true));
			@SuppressWarnings("unchecked")
			Collection<NotaUnidade> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna todas as notas dos alunos da turma agrupadora, unidade informada
	 *
	 * @param idTurma
	 * @param unidade
	 * @return
	 * @throws DAOException
	 */
	
	@SuppressWarnings("unchecked")
	public Collection<NotaUnidade> findNotasByTurmaAgrupadoraUnidade(int idTurma, int unidade) throws DAOException {

		try {
			
			Query q = getSession().createSQLQuery("select n.id_nota_unidade , n.faltas , n.unidade , n.nota , n.id_matricula_componente , n.recuperacao from ensino.nota_unidade as n " +
					"inner join ensino.matricula_componente as mc using ( id_matricula_componente ) " +
					"inner join ensino.turma as t using ( id_turma ) " +
					"where n.ativo = trueValue() and t.id_turma_agrupadora = " + idTurma + " and n.unidade = " + (byte) unidade);  
			
			List<Object[]> result = q.list();
			
			if ( result != null )
			{	
				ArrayList<NotaUnidade> notas = new ArrayList<NotaUnidade>();
				
				for ( Object[] linha : result )
				{
					NotaUnidade nota = new NotaUnidade();
					nota.setId(( (Number) linha[0]).intValue() );
					nota.setFaltas(((Number) linha[1]).shortValue());
					nota.setUnidade( ((Number) linha[2]).byteValue());
					nota.setNota( linha[3] == null ? null : ((Number) linha[3]).doubleValue());
					nota.setMatricula( new MatriculaComponente() );
					nota.getMatricula().setId( ( Integer ) linha[4] );
					notas.add(nota);
				}
				return notas;
			}	
			return null;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca as frequências do aluno em uma turma, em uma determinada data
	 *
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public List<FrequenciaAluno> findFrequenciasByTurma(Turma turma, Date data) {
		@SuppressWarnings("unchecked")
		List<FrequenciaAluno> lista = getHibernateTemplate().find("select new FrequenciaAluno(f.id, f.faltas, f.data, f.turma.id, d.matricula, p.id, p.nome, f.tipoCaptcaoFrequencia) "
				+ "from FrequenciaAluno f left outer join f.discente d left outer join d.pessoa p "
				+ "where f.turma.id = ? and f.data = to_date(?,'dd/MM/yyyy') and f.ativo = trueValue() order by p.nome asc",
				new Object[] { turma.getId(), Formatador.getInstance().formatarData(data) });
		return lista;
	}
	
	/**
	 * Retorna os dados necessários para enviar emails aos docentes das turmas passadas por parâmetro.
	 * 
	 * @param turmas
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> findEmailsDocentesByTurmas(List <Integer> turmas) throws DAOException {
		
		String idsTurmas = UFRNUtils.gerarStringIn(turmas);
		
		@SuppressWarnings("unchecked")
		List <Integer> sts = getSession().createSQLQuery("select id_turma from ensino.turma where id_turma_agrupadora in " + idsTurmas).list();
		
		for (Integer t : sts)
			turmas.add(t);
		
		idsTurmas = UFRNUtils.gerarStringIn(turmas);
		
		String sql = "select distinct u.email "
			+ "from ensino.docente_turma dt "
			+ "left join rh.servidor s on dt.id_docente = s.id_servidor "
			+ "left join ensino.docente_externo de using (id_docente_externo) "
			+ "join comum.usuario u on u.id_pessoa = coalesce (s.id_pessoa, de.id_pessoa) "
			+ "where u.inativo = falseValue() and dt.id_turma in " + idsTurmas;

		List<DocenteTurma> lista = new ArrayList <DocenteTurma> ();
		
		@SuppressWarnings("unchecked")
		List <String> rs = getSession().createSQLQuery(sql).list();
		
		for (String email : rs){
			DocenteTurma dt = new DocenteTurma();
			Servidor s = new Servidor();
			Usuario u = new Usuario();
			
			u.setEmail(email);
			s.setPrimeiroUsuario(u);
			dt.setDocente(s);
			
			lista.add(dt);
		}
		
		return lista;
	}
	
	/**
	 * Retorna os dados necessários para enviar emails aos docentes das turmas passadas por parâmetro.
	 * 
	 * @param turmas
	 * @return
	 * @throws DAOException
	 */
	public List<Usuario> findEmailsUsuariosAutorizadosByTurmas(List <Integer> turmas) throws DAOException {
		
		String idsTurmas = UFRNUtils.gerarStringIn(turmas);
		
		String sql = "select distinct coalesce (u.email, p.email) as email "
			+ "from ava.permissao_ava pa "
			+ "join comum.pessoa p using (id_pessoa) "
			+ "left join comum.usuario u using (id_pessoa) "
			+ "where u.inativo = falseValue() and pa.id_turma in " + idsTurmas;

		List<Usuario> lista = new ArrayList <Usuario> ();
		
		@SuppressWarnings("unchecked")
		List <String> rs = getSession().createSQLQuery(sql).list();
		
		for (String email : rs){
			Usuario u = new Usuario();
			u.setEmail(email);
			lista.add(u);
		}
		
		return lista;
	}


	/**
	 * Retorna todas as turmas oferecidas ao curso informado no ano e período informados
	 *
	 * @param ano
	 * @param periodo
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasOferecidasAoCurso(int ano, int periodo, int idCurso) throws DAOException {
		try {
			Collection<Turma> resultados = new HashSet<Turma>();

			Criteria cReserva = getSession().createCriteria(ReservaCurso.class);
			Criteria cMatriz = cReserva.createCriteria("matrizCurricular");
			cMatriz.add( Restrictions.eq("curso.id", idCurso) );

			@SuppressWarnings("unchecked")
			Collection<ReservaCurso> reservas = cReserva.list();
			for(ReservaCurso rc: reservas){
				Turma t = rc.getTurma();
				if( t != null )
					if( t.getAno() == ano && t.getPeriodo() == periodo)
						resultados.add(t);
			}

			return resultados;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todas as turmas de graduação com seus docentes e a n° de vagas.
	 *
	 * @param ano
	 * @param periodo
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Turma> findTurmasGraduacao(int ano, int periodo, int idCurso) throws DAOException {
		try {
			List<Turma> resultados = new ArrayList<Turma>();
				
			StringBuffer projecaoHql = new StringBuffer();
			projecaoHql.append("  t.id, t.ano, t.periodo, t.distancia, t.codigo,t.descricaoHorario, cc.id,cc.codigo,ccd.id,ccd.nome,dt.id, " +
								" p2.id as idPessoaServidor, p2.nome, s.siape,p.id as idPessoaDocente,  " +
								" p.nome as docenteExterno, st.id, st.descricao, r.id as idReserva," +
								" r.vagasReservadas, r.vagasReservadas ");
			
			StringBuffer hql = new StringBuffer();
			hql.append(
					" SELECT " + projecaoHql.toString() + " FROM ReservaCurso r RIGHT JOIN r.turma t " +
			  		" INNER JOIN t.disciplina cc INNER JOIN cc.detalhes ccd LEFT JOIN t.situacaoTurma st " +
			  		" LEFT JOIN t.horarios ht " +
			  		" LEFT JOIN ht.horario h LEFT JOIN r.matrizCurricular mc INNER JOIN" +
			  		" mc.curso c LEFT JOIN t.docentesTurmas dt LEFT JOIN dt.docenteExterno de LEFT JOIN " +
			  		" de.pessoa p LEFT JOIN dt.docente s LEFT JOIN s.pessoa p2 WHERE 1=1 "
			  		);
			
			if(idCurso > 0)
				hql.append( " AND (c.id = :idCurso OR r.curso.id = :idCurso )");
			if(ano > 0)
				hql.append( " AND t.ano = :ano ");
			if(periodo > 0)
				hql.append( " AND t.periodo = :periodo ");
			
			hql.append( " ORDER BY   ccd.nome asc, t.codigo asc");
			
			Query q = getSession().createQuery(hql.toString());
			
			if(idCurso > 0)
				q.setInteger("idCurso", idCurso);
			if(ano > 0)
				q.setInteger("ano", ano);
			if(periodo > 0)
				q.setInteger("periodo", periodo);
			
			
			List<Object[]> lista = q.list();

			int idAtual = 0;
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = lista.get(a);
				
				idAtual = (Integer) colunas[col++];
				
				Turma t = new Turma();
				t.setId(idAtual);
				t.setAno((Integer) colunas[col++]);
				t.setPeriodo((Integer) colunas[col++]);
				t.setDistancia((Boolean) colunas[col++]);
				t.setCodigo((String) colunas[col++]);
				t.setDescricaoHorario((String) colunas[col++]);
				t.setDisciplina(new ComponenteCurricular());
				t.getDisciplina().setId((Integer) colunas[col++]);
				t.getDisciplina().setCodigo((String) colunas[col++]);
				t.getDisciplina().setDetalhes(new ComponenteDetalhes());
				t.getDisciplina().getDetalhes().setId((Integer) colunas[col++]);
				t.getDisciplina().getDetalhes().setNome((String) colunas[col++]);
			
				DocenteTurma dt = new DocenteTurma();
				Integer idDT = (Integer) colunas[col++];
				Integer idDocente = (Integer) colunas[col++];
				String nomeDocente = (String) colunas[col++];
				Integer siape = (Integer) colunas[col++];
				Integer idDocenteExterno = (Integer) colunas[col++];
				String nomeDocenteExterno = (String) colunas[col++];
				
				if(idDT != null){
					dt.setId(idDT);
				
					if(nomeDocente != null){
						dt.setDocente(new Servidor());
						dt.getDocente().setPessoa(new Pessoa(idDocente, nomeDocente));
						dt.getDocente().setSiape(siape);
	
					}else{	
						dt.setDocenteExterno(new DocenteExterno());
						dt.getDocenteExterno().setPessoa(new Pessoa(idDocenteExterno, nomeDocenteExterno));
					}
					
					dt.setTurma(new Turma(idAtual));
				}
				t.setReservas(new ArrayList<ReservaCurso>());

				
				t.setSituacaoTurma(new SituacaoTurma((Integer) colunas[col++],(String) colunas[col++]));
				
				ReservaCurso r = new ReservaCurso();
				r.setId((Integer)colunas[col++]);
				r.setVagasReservadas((Short) colunas[col++]);
				r.setTurma(new Turma());
				r.getTurma().setId(idAtual);

				 if(resultados.contains(t)){
			
					Integer index = resultados.indexOf(t);
					Set<DocenteTurma> listaDT =  resultados.get(index).getDocentesTurmas();
					if(!listaDT.contains(dt))
						resultados.get(index).getDocentesTurmas().add(dt);
					
					Collection<ReservaCurso> listaRS = resultados.get(index).getReservas();
					if(!listaRS.contains(r))
						 resultados.get(index).getReservas().add(r);
					
			 	}else{
			
					t.getReservas().add(r);
					t.getDocentesTurmas().add(dt);
					resultados.add(t);
			
				 }
				
				
			}	

			return resultados;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca os horários de uma turma
	 *
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<HorarioTurma> findHorariosByTurma(Turma turma) throws DAOException {
		Collection<Turma> turmas = new ArrayList<Turma>(1);
		turmas.add(turma);
		return findHorariosByTurmas(turmas);
	}


	/**
	 * Busca otimizada de horários por turma
	 *
	 * @param turmas
	 * @return
	 * @throws DAOException
	 */
	public List<HorarioTurma> findHorariosByTurmas(Collection<Turma> turmas) throws DAOException {
		if (turmas == null || turmas.size() == 0)
			return null;
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT ht.dia, ht.id_horario, cc.codigo, det.nome, ht.data_inicio, ht.data_fim, h.inicio, h.fim, h.ordem, h.tipo, h.ativo ");
			sql.append("FROM  ensino.horario_turma ht, ensino.horario h, ensino.turma t, ensino.componente_curricular cc " +
					" join ensino.componente_curricular_detalhes det on cc.id_detalhe=det.id_componente_detalhes ");
			sql.append("WHERE ht.id_turma=t.id_turma and t.id_disciplina=cc.id_disciplina and ht.id_horario = h.id_horario and" +
					" ht.id_turma in " + gerarStringIn(turmas));

			st = con.prepareStatement(sql.toString());
			rs = st.executeQuery();
			ArrayList<HorarioTurma> horarios = new ArrayList<HorarioTurma>(0);
			while (rs.next()) {
				Horario horario = new Horario(rs.getInt("ID_HORARIO"));
				horario.setInicio(rs.getTime("INICIO"));
				horario.setFim(rs.getTime("FIM"));
				horario.setOrdem(rs.getShort("ORDEM"));
				horario.setTipo(rs.getShort("TIPO"));
				horario.setAtivo(rs.getBoolean("ATIVO"));
				HorarioTurma h = new HorarioTurma(horario, rs.getString("DIA").charAt(0));
				h.setTurma(new Turma());
				h.getTurma().setDisciplina(new ComponenteCurricular());
				h.getTurma().getDisciplina().setCodigo(rs.getString("CODIGO"));
				h.getTurma().getDisciplina().setNome(rs.getString("NOME"));
				h.setDataInicio(rs.getDate("DATA_INICIO"));
				h.setDataFim(rs.getDate("DATA_FIM"));
				horarios.add(h);
			}
			return horarios;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Busca por turmas abertas. Filtra a partir de diversos atributos dos
	 * componentes dessas turmas.
	 * Só procurar por turmas de componentes matriculáveis
	 * BUSCA USADA PARA MATRÍCULA DE GRADUAÇÃO.
	 *
	 * @param componentes
	 * @param curso
	 * @param polo
	 * @param ano
	 * @param periodo
	 * @param matriculaveis
	 * @param nivel
	 * @param aDistancia
	 * @param tiposTurma
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<Turma> findAbertasByExpressao(Collection<ComponenteCurricular> componentes,Curso curso, Polo polo, Integer ano, Integer periodo, Boolean matriculaveis, char nivel, boolean aDistancia, Integer... tiposTurma) throws DAOException, LimiteResultadosException {
		return findAbertasByComponenteCurricular(componentes, null, null, null, null, null, null, curso, polo, ano, periodo, matriculaveis, nivel, aDistancia, tiposTurma);
	}

	/**
	 * Busca por turmas abertas. Filtra a partir de diversos atributos dos
	 * componentes dessas turmas.
	 * Só procurar por turmas de componentes matriculáveis
	 * BUSCA USADA PARA MATRÍCULA DE GRADUAÇÃO.
	 *
	 * @param nomeComponente
	 * @param codigo
	 * @param depto
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findAbertasByComponenteCurricular(Collection<ComponenteCurricular> componentes, String nomeComponente, String codigo, Integer depto,
			Integer tipo, String nomeDocente, String horario, Curso curso, Polo polo,
			Integer ano, Integer periodo, Boolean matriculaveis, char nivel, boolean aDistancia, Integer... tiposTurma) throws DAOException, LimiteResultadosException {
		return findAbertasByComponenteCurricular(null, componentes, nomeComponente, codigo, depto, tipo, nomeDocente, horario, curso, polo, ano, periodo, matriculaveis, nivel, aDistancia, null, tiposTurma);
	}
	
	/**
	 * Busca por turmas abertas. Filtra a partir de diversos atributos dos
	 * componentes dessas turmas.
	 * Só procurar por turmas de componentes matriculáveis.
	 * Sobrecarga que utiliza o discente de graduação para retornar também as reservas
	 * de vagas nas turmas para para a matriz curricular do aluno. 
	 * BUSCA USADA PARA MATRÍCULA DE GRADUAÇÃO.
	 *
	 * @param nomeComponente
	 * @param codigo
	 * @param depto
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findAbertasByComponenteCurricular(DiscenteGraduacao discenteGrad, Collection<ComponenteCurricular> componentes, String nomeComponente, String codigo, Integer depto,
			Integer tipo, String nomeDocente, String horario, Curso curso, Polo polo,
			Integer ano, Integer periodo, Boolean matriculaveis, char nivel, boolean aDistancia, Boolean reservasCurso, Integer... tiposTurma) throws DAOException, LimiteResultadosException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			StringBuffer restoSQL = new StringBuffer();

			restoSQL.append(" FROM ensino.componente_curricular cc " +
				" join ensino.componente_curricular_detalhes ccd on ( cc.id_detalhe = ccd.id_componente_detalhes ) " +
				" join comum.unidade as und on cc.id_unidade = und.id_unidade " +				
				" join ensino.turma t on ( cc.id_disciplina = t.id_disciplina ) " +
				" left join ensino.docente_turma dt on ( t.id_turma = dt.id_turma ) " +
				" left join rh.servidor s on (dt.id_docente = s.id_servidor) " +
				" left join comum.pessoa p on ( p.id_pessoa = s.id_pessoa ) " +
				" left join ead.polo polo on ( polo.id_polo = t.id_polo ) " +
				" left join comum.municipio municipio on (municipio.id_municipio = polo.id_cidade)");
			if(discenteGrad != null){
				restoSQL.append(" left join graduacao.reserva_curso rc on (rc.id_turma=t.id_turma and rc.id_matriz_curricular = "+ discenteGrad.getMatrizCurricular().getId() +")");
			}
			if( NivelEnsino.isValido(nivel) && NivelEnsino.isAlgumNivelStricto(nivel) ){
				restoSQL.append( " join comum.unidade unidade on ( cc.id_unidade = unidade.id_unidade ) " );
			}

			restoSQL.append(" WHERE t.id_turma_bloco is null AND t.agrupadora = falseValue()" + 
					" AND t.id_situacao_turma in " + gerarStringIn(
							new int[] {SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.ABERTA}));

			if (!isEmpty(componentes))
				restoSQL.append(" and t.id_disciplina in " + gerarStringIn(componentes));
			if (NivelEnsino.isValido(nivel)){
				if( NivelEnsino.isAlgumNivelStricto(nivel) ){
					restoSQL.append(" and cc.nivel in " + gerarStringIn( NivelEnsino.getNiveisStricto() ));
				}else
					restoSQL.append(" and cc.nivel= '" + nivel+"'");
			}
			if (matriculaveis != null)
				restoSQL.append(" and cc.matriculavel= " + matriculaveis);

			// Turmas probásica
			if (curso != null && curso.getId() > 0)
				restoSQL.append(" and t.id_curso = ?");
			else {
				restoSQL.append(" and t.id_curso is null");
			}
			if (polo != null && polo.getId() > 0)
				restoSQL.append(" and t.id_polo = ?");
			if (depto!= null && depto > 0)
				restoSQL.append(" and cc.id_unidade = ?");
			if (tipo != null && tipo > 0)
				restoSQL.append(" and cc.id_tipo_componente = ?");
			if (!isEmpty(horario))
				restoSQL.append(" and t.descricao_horario like '"+horario+"%' ");
			if (codigo != null && codigo.length() > 0)
				restoSQL.append(" and ccd.codigo = ?");
			if (nomeDocente != null && nomeDocente.length() > 0)
				restoSQL.append(" AND p.nome_ascii ilike"+
						" translate('%"+UFRNUtils.trataAspasSimples(nomeDocente)+"%','áàâãäéèêëíìïóòôõöúùûüÁÀÂÃÄÉÈÊËÍÌÏÓÒÔÕÖÚÙÛÜçÇñÑ','aaaaaeeeeiiiooooouuuuAAAAAEEEEIIIOOOOOUUUUcCnN')");
			if (nomeComponente != null && nomeComponente.length() > 0)
				restoSQL.append(" AND ccd.nome_ascii ilike"+
						" translate('%"+UFRNUtils.trataAspasSimples(nomeComponente)+"%','áàâãäéèêëíìïóòôõöúùûüÁÀÂÃÄÉÈÊËÍÌÏÓÒÔÕÖÚÙÛÜçÇñÑ','aaaaaeeeeiiiooooouuuuAAAAAEEEEIIIOOOOOUUUUcCnN')");

			// Turmas de ensino a distância
			if (aDistancia) {
				restoSQL.append(" AND t.distancia = trueValue()");
			} else {
				restoSQL.append(" AND (t.distancia is null or t.distancia = falseValue())");
			}
			if(discenteGrad != null && aDistancia && (reservasCurso != null && reservasCurso.booleanValue())){
				restoSQL.append(" AND (rc.id_turma = t.id_turma and rc.id_matriz_curricular = "+ discenteGrad.getMatrizCurricular().getId() +")");
			}

			if( ano != null )
				restoSQL.append(" AND t.ano = " + ano);
			if( periodo != null )
				restoSQL.append(" AND t.periodo = " + periodo);
			if( tiposTurma != null )
				restoSQL.append(" AND t.tipo in " + UFRNUtils.gerarStringIn(tiposTurma) );

			st = con.prepareStatement("SELECT count(t.id_turma) as total " + restoSQL);
			setParametrosTurma(nomeComponente, codigo, tipo, depto, curso, polo, st, ano, periodo);
			rs = st.executeQuery();
			long total;
			if (rs.next()) {
				total = rs.getLong("TOTAL");
				if (total > LIMITE_RESULTADOS_BUSCA_TURMA)
					throw new LimiteResultadosException();
			}

			String countMatriculas = " , (select count(mc.id_matricula_componente) from ensino.matricula_componente as mc where mc.id_turma = t.id_turma " +
			"and mc.id_situacao_matricula in " + gerarStringIn( SituacaoMatricula.getSituacoesAtivas() ) + " ) as TOTAL_MATRICULADOS ";

			String projecao = "SELECT t.ano as ANO, t.periodo as PERIODO, t.distancia, t.id_turma, t.codigo as TURMA_CODIGO, t.local, t.id_situacao_turma, t.descricao_horario, cc.id_disciplina,  " +
			"cc.codigo as CC_CODIGO, cc.id_unidade as CC_ID_UNIDADE, und.id_gestora_academica as GESTORA_ACADEMICA, ccd.nome as CC_NOME, dt.id_docente, p.nome as DOCENTE_NOME, t.capacidade_aluno AS CAPACIDADE, t.tipo AS TIPO, " +
			"polo.id_polo, polo.id_cidade, municipio.nome as nomeCidade";
			if(discenteGrad != null)
				projecao += ", rc.vagas_atendidas";

			String orderBy = " ORDER BY ccd.nome, t.id_disciplina, t.codigo asc";

			if( NivelEnsino.isValido(nivel) && NivelEnsino.isAlgumNivelStricto(nivel) ){
				projecao += " , unidade.nome as UNIDADE_NOME ";
				orderBy = " ORDER BY cc.id_unidade, ccd.nome, t.id_disciplina, t.codigo asc";
			}

			System.out.println( projecao +
					countMatriculas +
					restoSQL +
					orderBy );

			st = con.prepareStatement( projecao +
					countMatriculas +
					restoSQL +
					orderBy);

			setParametrosTurma(nomeComponente, codigo, tipo, depto, curso, polo, st, ano, periodo);
			rs = st.executeQuery();

			ArrayList<Turma> turmas = new ArrayList<Turma>(0);
			int idAtual = 0;
			while (rs.next()) {
				Turma t = new Turma();
				t.setId(rs.getInt("ID_TURMA"));
				if (idAtual == t.getId() && idAtual > 0)
					t = turmas.get(turmas.size()-1);
				DocenteTurma dt = new DocenteTurma();
				dt.getDocente().setId(rs.getInt("ID_DOCENTE"));
				dt.getDocente().getPessoa().setNome(rs.getString("DOCENTE_NOME"));
				t.getDocentesTurmas().add(dt);
				if (idAtual == t.getId() && idAtual > 0)
					continue;
				t.setCodigo(rs.getString("TURMA_CODIGO"));
				t.setLocal(rs.getString("LOCAL"));
				if (discenteGrad != null && rs.getInt("vagas_atendidas") > 0) {
					t.setReservas(new ArrayList<ReservaCurso>());
					ReservaCurso rc = new ReservaCurso();
					rc.setVagasReservadas((short) rs.getInt("vagas_atendidas"));
					t.getReservas().add(rc);
				}
				t.setSituacaoTurma(new SituacaoTurma(rs.getInt("ID_SITUACAO_TURMA")));
				t.setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				t.setDisciplina(new ComponenteCurricular(rs.getInt("ID_DISCIPLINA")));
				t.getDisciplina().setCodigo(rs.getString("CC_CODIGO"));
				t.getDisciplina().setUnidade(new Unidade());
				t.getDisciplina().getUnidade().setId( rs.getInt("CC_ID_UNIDADE") );

				if( NivelEnsino.isValido(nivel) && NivelEnsino.isAlgumNivelStricto(nivel) ){
					t.getDisciplina().getUnidade().setNome( rs.getString("UNIDADE_NOME") );
				}
				t.getDisciplina().getUnidade().setGestoraAcademica(new Unidade());
				t.getDisciplina().getUnidade().getGestoraAcademica().setId(rs.getInt("GESTORA_ACADEMICA"));

				t.getDisciplina().setNome(rs.getString("CC_NOME"));

				t.setCapacidadeAluno( rs.getInt("CAPACIDADE") );
				t.setTipo( rs.getInt("TIPO") );
				t.setQtdMatriculados( rs.getInt("TOTAL_MATRICULADOS") );
				t.setAno( Integer.parseInt( rs.getString("ANO") ) );
				t.setPeriodo( Integer.parseInt( rs.getString("PERIODO") ) );
				t.setDistancia(rs.getBoolean("distancia"));
				Integer idPolo = (Integer) rs.getObject("id_polo");

				if (idPolo != null) {
					t.setPolo(new Polo());
					t.getPolo().setId( idPolo );
					t.getPolo().setCidade(new Municipio());
					t.getPolo().getCidade().setId( Integer.parseInt( rs.getString("id_cidade") ));
					t.getPolo().getCidade().setNome( rs.getString("nomeCidade") );
				}

				turmas.add(t);
				idAtual = t.getId();
			}
			return turmas;
		} catch (LimiteResultadosException e) {
			throw new LimiteResultadosException (e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Seta os parâmetros informados no PreparedStatement passado
	 *
	 * @param nome
	 * @param codigo
	 * @param tipo
	 * @param depto
	 * @param curso
	 * @param polo
	 * @param st
	 * @param ano
	 * @param periodo
	 * @throws SQLException
	 */
	private void setParametrosTurma(String nome, String codigo, Integer tipo, Integer depto, Curso curso, Polo polo, PreparedStatement st, Integer ano, Integer periodo) throws SQLException {
		int i = 1;
		if (curso != null && curso.getId() > 0)
			st.setInt(i++, curso.getId());
		if (polo != null && polo.getId() > 0)
			st.setInt(i++, polo.getId());
		if (depto!= null && depto > 0)
			st.setInt(i++, depto);
		if (tipo!= null && tipo > 0)
			st.setInt(i++, tipo);
		if (codigo != null && codigo.length() > 0)
			st.setString(i++, codigo);
	}

	
	/**
	 * Busca pelas turmas em que houveram solicitações de matrícula para um discente
	 *
	 * @param discente
	 * @param situacoes
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findByDiscenteMatSolicitadas(Discente discente, Integer status) throws DAOException {
		return findByDiscenteMatSolicitadas(discente, status, false);
	}

	/**
	 * Busca as turmas pelo discente e status informado
	 *
	 * @param discente
	 * @param status
	 * @param otimizada diz se deve utilizar projeção para otimizar a consulta
	 * @return
	 * @throws DAOException
	 */
	private Collection<Turma> findByDiscenteMatSolicitadas(Discente discente, Integer status, Boolean otimizada) throws DAOException {
		try {
            if (otimizada != null && !otimizada) {
				Criteria c = getSession().createCriteria(SolicitacaoMatricula.class);
	            c.add( Restrictions.eq("discente", discente) );
	            if (status != null && status > 0)
	            	c.add(Restrictions.eq("status", status));
	        	c.setProjection(Projections.property("turma"));
	        	@SuppressWarnings("unchecked")
				Collection<Turma> lista = c.list();
	        	return lista;
            } else if (otimizada != null && !otimizada) {
            	@SuppressWarnings("unchecked")
    			Collection<Turma> lista = getSession().createQuery(
						"select turma.id from SolicitacaoMatricula where discente.id=" + discente.getId()
								+ " and  anulado=falseValue() and status="+status).list();
            	return lista;
            } else
            	return null;
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Retorna o total de solicitações de matrícula realizadas para a turma informada
	 *
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public int findTotalSolicitacoesMatricula(int idTurma) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(SolicitacaoMatricula.class);
			c.add(Restrictions.eq("turma", new Turma(idTurma)));
			c.add(Restrictions.eq("anulado", false));
			c.add(Restrictions.eq("status", SolicitacaoMatricula.SUBMETIDA));
			c.setProjection(Projections.count("id"));
			Integer r = (Integer) c.uniqueResult();
			return ((r == null)?0:r);
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna o total de solicitações de matrícula realizadas para a turma informada 
	 * durante o período de rematrícula.
	 *
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public int findTotalSolicitacoesReMatricula(int idTurma) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(SolicitacaoMatricula.class);
			c.add(Restrictions.eq("turma", new Turma(idTurma)));
			c.add(Restrictions.eq("anulado", false));
			c.add(Restrictions.eq("rematricula", true));
			c.add(Restrictions.ne("status", SolicitacaoMatricula.EXCLUIDA));
			c.setProjection(Projections.count("id"));
			Integer r = (Integer) c.uniqueResult();
			return ((r == null)?0:r);
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Retorna o total de discentes matriculados na turma informada
	 *
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public int findTotalMatriculados(int idTurma) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(MatriculaComponente.class);
			c.add(Restrictions.eq("turma", new Turma(idTurma)));
			c.add(Restrictions.or(Restrictions.eq("situacaoMatricula", SituacaoMatricula.EM_ESPERA),
					Restrictions.eq("situacaoMatricula", SituacaoMatricula.MATRICULADO)));
			c.setProjection(Projections.count("id"));
			return (Integer) c.uniqueResult();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}

	/**
	 * Busca as turmas de acordo com os parâmetros informados
	 *
	 * @param idDisciplina
	 * @param idSituacao
	 * @param ano
	 * @param periodo
	 * @param idDocente
	 * @param unidade
	 * @param nivel
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findCompleto(int idDisciplina, int idSituacao, int ano, int periodo, int idDocente, int unidade, char nivel,
			PagingInformation paging) throws DAOException {
		try {

			StringBuffer hql = new StringBuffer();
			hql.append("select t ");
			hql.append(" from Turma t left join t.docentesTurmas as dt");
			hql.append(" where ");
			if (idSituacao > 0) hql.append("t.situacaoTurma.id = " + idSituacao);
			if (idDisciplina > 0) hql.append(" and t.disciplina.id = " + idDisciplina );
			if (ano > 0) hql.append(" and t.ano = " + ano );
			if (periodo > 0) hql.append(" and t.periodo = " + periodo );
			if (idDocente > 0) hql.append(" and dt.docente.id = " + idDocente );
			if (unidade > 0) hql.append(" and t.disciplina.unidade.id = " + unidade );
			if (NivelEnsino.isValido(nivel)) hql.append(" and t.disciplina.nivel = '" + nivel+"' ");
			hql.append(" order by t.ano desc, t.periodo desc, t.disciplina.detalhes.nome asc, t.codigo asc");
			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			q.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			@SuppressWarnings("unchecked")
			List<Turma> turmas = q.list();
			if (turmas != null) {
				for (Turma turma : turmas) {
					Query qtd = getSession().createQuery(
							"select count(*) from "
									+ "MatriculaComponente where turma.id = :idTurma and "
									+ "situacaoMatricula.id in (:matriculado,:espera,:aprovado,:reprovado,:falta, :mediaFalta)");
					qtd.setInteger("idTurma", turma.getId());
					qtd.setInteger("matriculado", SituacaoMatricula.MATRICULADO.getId());
					qtd.setInteger("espera", SituacaoMatricula.EM_ESPERA.getId());
					qtd.setInteger("aprovado", SituacaoMatricula.APROVADO.getId());
					qtd.setInteger("reprovado", SituacaoMatricula.REPROVADO.getId());
					qtd.setInteger("falta", SituacaoMatricula.REPROVADO_FALTA.getId());
					qtd.setInteger("mediaFalta", SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId());
					////TODO: refazer a consulta dividindo os status. Do jeito que está abaixo, está ok porque é exibido o total. Mas caso precise, não será exibido os totais por status.
					turma.setQtdMatriculados(((Number) qtd.uniqueResult()).intValue());
				}
			}
			return turmas;
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}



	/**
	 * Busca as turmas de acordo com os parâmetros informados
	 *
	 * @param nivel
	 * @param unidade
	 * @param codigoComp
	 * @param codigoTurma
	 * @param nome
	 * @param nomeDocente
	 * @param situacao
	 * @param ano
	 * @param periodo
	 * @param polo
	 * @param cursos
	 * @param turmasEAD
	 * @param curso
	 * @param idComponente
	 * @param local
	 * @param tipoTurma
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<Turma> findGeral(Character nivel, Unidade unidade, String codigoComp, String codigoTurma,
			String nome, String nomeDocente, Integer[] situacao, Integer ano, Integer periodo, Polo polo, Collection<Curso> cursos, 
			ModalidadeEducacao modalidade, Curso curso, Integer idComponente, String local, Integer tipoTurma, String horario, Integer ordenarPor, List<SituacaoMatricula> situacoesMatricula) throws DAOException, LimiteResultadosException {


		ArrayList<Turma> result = new ArrayList<Turma>();
		result.addAll(findGeral(nivel, unidade, codigoComp, codigoTurma, nome, nomeDocente, situacao, ano, periodo, polo, cursos, modalidade, curso, idComponente, false, local, tipoTurma, false, null,horario,ordenarPor,situacoesMatricula));

		// exclui as repetidas pois as turmas com docente externo vem na primeira consulta também com a coleção de docentes vazia
		Collection<Turma> comDocentesExternos = findGeral(nivel, unidade, codigoComp, codigoTurma, nome, nomeDocente, situacao, ano, periodo, polo, cursos, modalidade, curso, idComponente, true, local, tipoTurma, false, null, horario,ordenarPor,situacoesMatricula);
		for ( Turma t : comDocentesExternos ) {
			int existe = result.indexOf(t);
			if ( existe != -1 ) {
				Turma outra = result.get( result.indexOf(t) );
				outra.getDocentesTurmas().addAll(t.getDocentesTurmas());
			} else {
				result.add(t);
			}
		}
		return result;
	}

	/**
	 * Consulta as turmas de um determinado curso, ano e período
	 *
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */

	public Collection<Turma> findGeral(Curso curso, Character nivel, Integer ano, Integer periodo) throws LimiteResultadosException, DAOException{
			return findGeral(nivel, curso.getUnidade(), null, null, null, null, null, ano, periodo, null, null, new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL),
					null, null, false, null, null, false, null,null,null,null);
	}


	/**
	 * Consulta geral de turmas filtrando por diversos parâmetros.
	 * @param nivel
	 * @param unidade
	 * @param codigoComp
	 * @param codigoTurma
	 * @param nome
	 * @param nomeDocente
	 * @param situacao
	 * @param ano
	 * @param periodo
	 * @param polo
	 * @param cursos
	 * @param turmasEAD
	 * @param curso
	 * @param idComponente
	 * @param externos
	 * @param local
	 * @param tipoTurma
	 * @param publico - busca somente as turmas públicas para o discente.
	 * @param palavraChave - busca em nome do componente e em topicos de aula.
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<Turma> findGeral(Character nivel, Unidade unidade, String codigoComp, String codigoTurma,
			String nome, String nomeDocente, Integer[] situacao, Integer ano, Integer periodo, Polo polo, Collection<Curso> cursos,
			ModalidadeEducacao modalidade,Curso curso, Integer idComponente, boolean externos, String local, Integer tipoTurma,
			boolean publico,String palavraChave, String horario, Integer ordenarPor, List<SituacaoMatricula> situacoesMatricula) throws DAOException, LimiteResultadosException {
		try {
			Collection<SituacaoMatricula> situacoes = situacoesMatricula != null ? situacoesMatricula : SituacaoMatricula.getSituacoesAtivas();
			StringBuilder hql = new StringBuilder();
			StringBuilder projecao = new StringBuilder(" t.id, d.id, p.nome, dt.chDedicadaPeriodo, t.ano, t.periodo, t.distancia, " +
					" t.codigo, t.local, t.descricaoHorario, t.capacidadeAluno, " +
					" t.disciplina.id, t.disciplina.codigo, t.disciplina.nivel, t.disciplina.matriculavel," +
					"t.disciplina.detalhes.nome,t.disciplina.detalhes.chTotal," +
					" t.disciplina.unidade.id, t.disciplina.unidade.nome, t.disciplina.unidade.sigla, t.situacaoTurma.id, t.situacaoTurma.descricao, t.idPolo, e.descricao, t.tipo, t.curso.id, t.processada, t.processadaRematricula, tAgrup.id ");

			// Contabilização de matrículas ativas
			String countMatriculas = "select count(mc.id) from MatriculaComponente as mc where mc.turma.id = t.id " +
				"and mc.situacaoMatricula.id in " + gerarStringIn(situacoes);
			projecao.append(", ("+countMatriculas+") as total_matriculados ");

			// Contabilização de solicitações pendentes
			String countEspera = "select count(sol.id) from SolicitacaoMatricula as sol where sol.turma.id = t.id " +
				" and sol.anulado = falseValue() " +
				" and sol.idMatriculaGerada is null " +
				" and sol.status in " + gerarStringIn(SolicitacaoMatricula.getStatusSolicitacoesPendentes());
			projecao.append(", ("+countEspera+") as total_espera ");

			String count = " count(distinct t.id) ";

			String cursoString = "";
			if(curso != null)
				cursoString = " left join t.reservas r "; 
			
			hql.append(" from Turma t ");
			hql.append(" left join t.turmaAgrupadora tAgrup ");
			
			if ( !externos )
			    hql.append(" left join t.docentesTurmas as dt " +
						   " left join dt.docente as d ");
			else
			    hql.append(" join t.docentesTurmas as dt " +
			    		   " join dt.docenteExterno as d ");

			hql.append("	left join d.pessoa as p " +
					   "	left join t.especializacao as e "+cursoString+" " +
					   "	inner join t.disciplina disciplina " +
					   "	inner join disciplina.detalhes detalhes ");
			
			// Somente as turmas que tem a permissão para visualização para aluno (pública)
			if( publico )
				hql.append(", ConfiguracoesAva ca ");


			hql.append(" WHERE ");
			hql.append( " t.agrupadora = falseValue() " );
			hql.append(" AND t.situacaoTurma.id not in "+ gerarStringIn(SituacaoTurma.getSituacoesInvalidas()));
			if (cursos != null && !cursos.isEmpty()) hql.append(" and t.curso in " + gerarStringIn(cursos));
			if (situacao != null) hql.append(" AND t.situacaoTurma.id in " + UFRNUtils.gerarStringIn(situacao));
			if (!isEmpty(unidade) && unidade.getId()>0)	hql.append(" AND t.disciplina.unidade.id = '" + unidade.getId() + "' ");
			if (!isEmpty(codigoTurma)) hql.append(" AND t.codigo = :codigoTurma ");
			if (codigoComp != null) hql.append(" AND t.disciplina.codigo = :codigoComp ");
			if (idComponente != null) hql.append(" AND t.disciplina.id = :idComponente ");

			// Somente as turmas que tem a permissão para visualização para aluno (pública)
			if(publico)
				hql.append(" AND ca.turma.id = t.id AND ca.permiteVisualizacaoExterna = trueValue() ");

			// Somente quando o valor da palavra chave é passada
			if(palavraChave != null){
				hql.append(" AND (t.disciplina.detalhes.nome_ascii LIKE :palavraChave");
				hql.append(" OR t.id = ANY(SELECT DISTINCT(ta.turma.id) FROM TopicoAula ta WHERE ");
				hql.append(" ta.visivel = trueValue() AND ta.ativo = trueValue() AND ");
				hql.append(" (upper(ta.descricao) LIKE :palavraChave ");
				hql.append(" OR upper(ta.conteudo) LIKE :palavraChave)))");
			}else if (nome != null) hql.append(" AND t.disciplina.detalhes.nome_ascii LIKE :nomeComponente" );

			if (ano != null) hql.append(" AND t.ano = :ano ");
			if (periodo != null) hql.append(" AND t.periodo = :periodo ");
			// turmas à distância
			if (modalidade != null && modalidade.isADistancia())
				hql.append(" AND t.distancia = true");
			else if (modalidade != null && modalidade.isPresencial())
				hql.append(" AND t.distancia = false");
			if (nomeDocente != null){
				hql.append(" AND (" + UFRNUtils.toAsciiUpperUTF8("p.nomeAscii") + " like :nomeDocente)");
			}
			if (nivel != null && NivelEnsino.isValido(nivel)) hql.append(" AND t.disciplina.nivel = :nivel ");
			if (horario != null) hql.append(" AND t.descricaoHorario like :horario ");
			if (polo != null) hql.append(" AND t.polo.id = " + polo.getId()+" ");
			if (curso != null){
				hql.append(" AND (" +
						"			t.curso.id = " +curso.getId()+
					    " 			OR (" +
					    "					r.matrizCurricular.id in" +
						"        			(" +
						"						SELECT id" +
						"							FROM MatrizCurricular m" +
						"							WHERE m.curso.id = " +curso.getId()+
						"        			)" +
						"    				or r.curso.id = " +curso.getId()+
						"    			)"+
						"		 ) "		
					);
			}


			if (local != null) hql.append(" AND sem_acento(upper(t.local)) like :local");
			if (tipoTurma != null) hql.append(" AND t.tipo = :tipoTurma ");
			String groupBy = " group by t.distancia, t.id, t.codigo, t.local, t.descricaoHorario, t.situacaoTurma.id, t.situacaoTurma.descricao, " +
			" t.disciplina.id, t.disciplina.codigo, t.disciplina.nivel, t.disciplina.matriculavel," +
			" t.disciplina.detalhes.nome,t.disciplina.detalhes.chTotal, " +
			" t.disciplina.unidade.id, t.disciplina.unidade.nome, t.disciplina.unidade.sigla, " +
			" d.id, p.nome, dt.chDedicadaPeriodo, t.ano, t.periodo, t.capacidadeAluno, t.idPolo, t.especializacao.descricao, t.tipo, t.curso.id, t.processada, t.processadaRematricula, tAgrup.id ";
			
			String orderBy = new String();			
			
			if(ordenarPor == null || ordenarPor == BuscaTurmaMBean.ORDENAR_POR_COMPONENTE_CURRICULAR) {
				orderBy = " order by t.ano desc, t.periodo desc, t.disciplina.detalhes.nome asc, t.disciplina.codigo asc, t.codigo asc";				
			}
			else if(ordenarPor == BuscaTurmaMBean.ORDENAR_POR_DIAS_SEMANA_RESPECTIVOS_HORARIOS) {
				orderBy = " order by t.ano desc, t.periodo desc,t.descricaoHorario , t.disciplina.detalhes.nome asc, t.disciplina.codigo asc, t.codigo asc";				
			}
			else if(ordenarPor == BuscaTurmaMBean.ORDENAR_POR_DOCENTE_DISCIPLINA_HORARIOS) {
				orderBy = " order by p.nome, t.ano desc, t.periodo desc,  t.disciplina.detalhes.nome,t.disciplina.codigo asc,t.codigo asc, t.descricaoHorario";	
			}
			else if(ordenarPor == BuscaTurmaMBean.ORDENAR_POR_LOCAL) {
				orderBy = " order by t.ano desc, t.periodo desc, t.disciplina.detalhes.nome asc, t.disciplina.codigo asc, t.local";	
			}
			
			
			HashMap<Integer, Integer> idTurmaPosicaoLista = new HashMap<Integer, Integer>();

			Query q = getSession().createQuery("select " + count + hql);
			if( nome != null )
				q.setString("nomeComponente", "%" + StringUtils.toAscii(nome.toUpperCase()) + "%");
			if (codigoComp != null)
				q.setString("codigoComp", codigoComp);

			if (!isEmpty(codigoTurma))  q.setString("codigoTurma", StringUtils.toAscii(codigoTurma.toUpperCase())); 
			if (idComponente != null) q.setInteger("idComponente", idComponente);
			if (palavraChave != null) q.setString("palavraChave", "%" + StringUtils.toAscii(palavraChave.toUpperCase()) + "%");
			if (ano != null) q.setInteger("ano", ano);
			if (periodo != null) q.setInteger("periodo",periodo);
			if (nomeDocente != null) q.setString("nomeDocente", "%" + StringUtils.toAscii(nomeDocente.toUpperCase()) + "%");
			if (local != null) q.setString("local", "%" + StringUtils.toAscii(local.toUpperCase()) + "%");
			if (tipoTurma != null) q.setInteger("tipoTurma", tipoTurma);
			if (nivel != null && NivelEnsino.isValido(nivel)) q.setCharacter("nivel", nivel);
			if (horario != null) q.setString("horario", "%" + StringUtils.toAscii(horario.toUpperCase()) + "%");
			
			Long qtd = (Long) q.uniqueResult();
			if (qtd > 300)
				throw new LimiteResultadosException();

			q = getSession().createQuery("select " + projecao.toString() + hql + groupBy + orderBy);
			if( nome != null )
				q.setString("nomeComponente", "%" + StringUtils.toAscii(nome.toUpperCase()) + "%");
			if (codigoComp != null)
				q.setString("codigoComp", codigoComp);
			if (!isEmpty(codigoTurma))   q.setString("codigoTurma", StringUtils.toAscii(codigoTurma.toUpperCase())); 
			if (idComponente != null) q.setInteger("idComponente", idComponente);
			if (palavraChave != null) q.setString("palavraChave", "%" + StringUtils.toAscii(palavraChave.toUpperCase()) + "%");
			if (ano != null) q.setInteger("ano", ano);
			if (periodo != null) q.setInteger("periodo",periodo);
			if (nomeDocente != null) q.setString("nomeDocente", "%" + StringUtils.toAscii(nomeDocente.toUpperCase()) + "%");
			if (local != null) q.setString("local", "%" + StringUtils.toAscii(local.toUpperCase()) + "%");
			if (tipoTurma != null) q.setInteger("tipoTurma", tipoTurma);
			if (nivel != null && NivelEnsino.isValido(nivel)) q.setCharacter("nivel", nivel);
			if (horario != null) q.setString("horario", "%" + StringUtils.toAscii(horario.toUpperCase()) + "%");

			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			ArrayList<Turma> result = new ArrayList<Turma>();
			List<Integer> idTurmas = new ArrayList<Integer>();
			//  Instancia as turmas do resultado da busca.
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = lista.get(a);

				Turma t = new Turma();
				t.setId((Integer) colunas[col++]);	
				idTurmas.add(t.getId());
				if( !idTurmaPosicaoLista.isEmpty() && idTurmaPosicaoLista.get(t.getId()) != null)				
					t = result.get(idTurmaPosicaoLista.get(t.getId()));
				DocenteTurma dt = new DocenteTurma();
				if ( !externos  ) {
					Integer idDocente = (Integer) colunas[col++];
					String nDocente = (String) colunas[col++];
					Integer ch = (Integer) colunas[col++];
					if(idDocente != null){
						dt.getDocente().setId(idDocente);
						dt.setChDedicadaPeriodo(ch);
						if (dt.getDocente().getPessoa() != null)
							dt.getDocente().getPessoa().setNome(nDocente);
						t.getDocentesTurmas().add(dt);
					}
				} else {
					Integer idDocenteExt = (Integer) colunas[col++];
					String nDocenteExt = (String) colunas[col++];
					Integer ch = (Integer) colunas[col++];
					if(idDocenteExt != null){
						dt.setDocenteExterno(new DocenteExterno(idDocenteExt));
						dt.setChDedicadaPeriodo(ch);
						if(dt.getDocenteExterno().getPessoa() != null)
							dt.getDocenteExterno().getPessoa().setNome(nDocenteExt);
						t.getDocentesTurmas().add(dt);
					}
				}
				if (idTurmaPosicaoLista.containsKey(t.getId()))
					continue;
				t.setAno((Integer) colunas[col++]);
				t.setPeriodo((Integer) colunas[col++]);
				t.setDistancia((Boolean) colunas[col++]);
				t.setCodigo((String) colunas[col++]);
				t.setLocal((String) colunas[col++]);
				t.setDescricaoHorario((String) colunas[col++]);
				t.setCapacidadeAluno((Integer) colunas[col++]);
				t.setDisciplina(new ComponenteCurricular((Integer) colunas[col++]));
				t.getDisciplina().setCodigo((String) colunas[col++]);
				t.getDisciplina().setNivel((Character) colunas[col++]);
				t.getDisciplina().setMatriculavel((Boolean) colunas[col++]);
				t.getDisciplina().setNome((String) colunas[col++]);
				t.getDisciplina().setChTotal((Integer) colunas[col++]);
				Integer idUnidade = (Integer) colunas[col++];
				if (idUnidade != null){
					t.getDisciplina().getUnidade().setId(idUnidade);
					t.getDisciplina().getUnidade().setNome((String)colunas[col++]);
					t.getDisciplina().getUnidade().setSigla((String)colunas[col++]);
				}
				t.setSituacaoTurma(new SituacaoTurma());
				t.getSituacaoTurma().setId((Integer) colunas[col++]);
				t.getSituacaoTurma().setDescricao((String) colunas[col++]);


				Integer idPolo = (Integer) colunas[col++];
				if (idPolo != null) {
					t.setPolo(new Polo());
					t.getPolo().setId(idPolo);
				}
				String descricaoEspecializacao = (String) colunas[col++];
				if(descricaoEspecializacao != null){
					t.setEspecializacao(new EspecializacaoTurmaEntrada());
					t.getEspecializacao().setDescricao(descricaoEspecializacao);
				}

				t.setTipo( (Integer) colunas[ col++ ] );

				Integer idCurso = (Integer) colunas[ col++ ];
				if( idCurso != null ){
					t.setCurso( new Curso() );
					t.getCurso().setId(idCurso);
				}

				t.setProcessada((Boolean) colunas[col++]);
				t.setProcessadaRematricula((Boolean) colunas[col++]);
				Integer idAgrup = (Integer) colunas[col++];
				
				if (isNotEmpty(idAgrup)) {
					t.setTurmaAgrupadora(new Turma());
					t.getTurmaAgrupadora().setId(idAgrup);
				}
				
				Long totalMatriculados = (Long) colunas[col++];
				t.setQtdMatriculados(totalMatriculados);

				t.setQtdEspera( (Long) colunas[col++] );
				result.add(t);
				idTurmaPosicaoLista.put(t.getId(), result.size()-1);				
			}
			// recupera as informações sobre reservas de curso.
			if (!isEmpty(idTurmas)) {
				// consulta os IDs das reservas por turmas
				String projecaoReserva = 
						"reserva.id," +
						" curso.id as reserva.curso.id," +
						" matrizCurricular.id as reserva.matrizCurricular.id," +
						" matrizCurricular.curso.id as reserva.matrizCurricular.curso.id," +
						" reserva.turma.id";
				String sql = "select " +HibernateUtils.removeAliasFromProjecao(projecaoReserva) +
						" from ReservaCurso reserva" +
						" left join reserva.curso curso" +
						" left join reserva.matrizCurricular matrizCurricular" +
						" where reserva.turma.id in " + UFRNUtils.gerarStringIn(idTurmas);
				Query qReservaCurso = getSession().createQuery(sql);
				@SuppressWarnings("unchecked")
				Collection<ReservaCurso> reservas = HibernateUtils.parseTo(qReservaCurso.list(), projecaoReserva, ReservaCurso.class, "reserva");
				if (reservas != null) {
					for (ReservaCurso reserva : reservas) {
						for (Turma turma : result)
							if (reserva.getTurma().getId() == turma.getId())
								turma.addReservaCurso(reserva);
					}
				}
			}
			return result;
		} catch (LimiteResultadosException lre) {
			throw new LimiteResultadosException(lre);
		}
	}


	/**
	 * Relatório de turmas filtrado por alguns parâmetros.
	 *
	 * @param codigo
	 * @param nome
	 * @param nomeDocente
	 * @param ano
	 * @param unidadeGestora
	 * @param nivelEnsino
	 * @return
	 */
	public Collection<Turma> gerarRelatorio(int situacao, String codigo, String nomeComponente,
			String nomeDocente, Integer ano, Integer periodo, int unidadeGestora, char nivelEnsino)  throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			StringBuffer sql = new StringBuffer();
			StringBuffer inSituacoes = new StringBuffer("(");
			inSituacoes.append(SituacaoMatricula.EM_ESPERA.getId()+",");
			inSituacoes.append(SituacaoMatricula.MATRICULADO.getId()+",");
			inSituacoes.append(SituacaoMatricula.REPROVADO.getId()+",");
			inSituacoes.append(SituacaoMatricula.REPROVADO_FALTA.getId()+",");
			inSituacoes.append(SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId()+",");
			inSituacoes.append(SituacaoMatricula.APROVADO.getId()+")");

			sql.append("SELECT t.id_turma, t.distancia, t.codigo as TURMA_CODIGO, t.local, t.descricao_horario, " +
					"cc.id_disciplina, cc.codigo as CC_CODIGO, cc.qtd_max_matriculas, ccd.id_componente_detalhes, ccd.nome as CC_NOME, st.descricao as SITUACAO_TURMA, " +
					"dt.id_docente, p.nome as DOCENTE_NOME, t.ano, t.periodo, t.capacidade_aluno, t.id_especializacao_turma_entrada as ID_ESPECIALIZACAO, ete.descricao as ESPECIALIZACAO, " +
					"count(mc.id_matricula_componente) as TOTAL_MATRICULADOS ");
			sql.append("FROM  ensino.turma t join ensino.componente_curricular cc on (cc.id_disciplina = t.id_disciplina) " +
					"join ensino.componente_curricular_detalhes ccd on (cc.id_detalhe = ccd.id_componente_detalhes ) " +
					"left join ensino.matricula_componente mc  on (t.id_turma = mc.id_turma and mc.id_situacao_matricula in (1,2,6,7,9,4))" +
					"left join ensino.docente_turma dt on (t.id_turma=dt.id_turma) " +
					"left join tecnico.especializacao_turma_entrada ete on (t.id_especializacao_turma_entrada=ete.id_especializacao_turma_entrada) " +
					"left join ensino.situacao_turma st on (t.id_situacao_turma=st.id_situacao_turma)  " +
					"left join rh.servidor s on (dt.id_docente=s.id_servidor) " +
					"left join comum.pessoa p on (s.id_pessoa=p.id_pessoa) ");
			sql.append(" WHERE 1=1 ");
			if (situacao > 0)
				sql.append(" and t.id_situacao_turma=? ");
			if(nivelEnsino != ' ')
				sql.append(" and cc.nivel='"+nivelEnsino+"'");
			if (ano != null && ano > 0)
				sql.append(" and t.ano = ?");
			if (periodo != null && periodo > 0)
				sql.append(" and t.periodo = ?");
			if (unidadeGestora > 0 && unidadeGestora != 605)
				sql.append(" and cc.id_unidade = ?");
			if (codigo != null && codigo.length() > 0)
				sql.append(" and ccd.codigo = ?");
			if (nomeDocente != null && nomeDocente.length() > 0)
				sql.append(" AND "+ UFRNUtils.toAsciiUpperUTF8("p.nome")+" like "+UFRNUtils.toAsciiUpperUTF8("'"+nomeDocente+"%'"));
			if (nomeComponente != null && nomeComponente.length() > 0)
				sql.append(" AND "+ UFRNUtils.toAsciiUpperUTF8("ccd.nome")+" like "+UFRNUtils.toAsciiUpperUTF8("'"+nomeComponente+"%'"));
			sql.append(" GROUP BY t.id_turma, TURMA_CODIGO, t.local, t.descricao_horario, st.descricao, " +
					"cc.id_disciplina, CC_CODIGO, cc.qtd_max_matriculas, ccd.id_componente_detalhes, CC_NOME, " +
					"dt.id_docente, DOCENTE_NOME, t.ano, t.periodo, t.id_especializacao_turma_entrada, ete.descricao, t.capacidade_aluno " +
					" ORDER BY ccd.nome asc");

			st = con.prepareStatement(sql.toString());
			int i = 1;
			if (situacao > 0)
				st.setInt(i++, situacao);
			if (ano != null && ano > 0)
				st.setInt(i++, ano);
			if (periodo != null && periodo > 0)
				st.setInt(i++, periodo);
			if (unidadeGestora > 0 && unidadeGestora != 605)
				st.setInt(i++, unidadeGestora);
			if (codigo != null && codigo.length() > 0)
				st.setString(i++, codigo);
			rs = st.executeQuery();

			ArrayList<Turma> turmas = new ArrayList<Turma>(0);
			int idAtual = 0;
			while (rs.next()) {
				Turma t = new Turma();
				t.setId(rs.getInt("ID_TURMA"));
				t.setDistancia(rs.getBoolean("DISTANCIA"));
				if (idAtual == t.getId() && idAtual > 0)
					t = turmas.get(turmas.size()-1);
				DocenteTurma dt = new DocenteTurma();
				dt.getDocente().setId(rs.getInt("ID_DOCENTE"));
				if (dt.getDocente().getPessoa() != null)
					dt.getDocente().getPessoa().setNome(rs.getString("DOCENTE_NOME"));
				t.getDocentesTurmas().add(dt);
				if (idAtual == t.getId() && idAtual > 0)
					continue;
				t.setAno(rs.getInt("ANO"));
				t.setPeriodo(rs.getInt("PERIODO"));
				t.setCapacidadeAluno(rs.getInt("CAPACIDADE_ALUNO"));
				t.setCodigo(rs.getString("TURMA_CODIGO"));
				t.setLocal(rs.getString("LOCAL"));
				t.setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				if (rs.getInt("ID_ESPECIALIZACAO") > 0) {
					t.setEspecializacao(new EspecializacaoTurmaEntrada());
					t.getEspecializacao().setId(rs.getInt("ID_ESPECIALIZACAO"));
					t.getEspecializacao().setDescricao(rs.getString("ESPECIALIZACAO"));
				}
				t.setDisciplina(new ComponenteCurricular(rs.getInt("ID_DISCIPLINA")));
				t.getDisciplina().setCodigo(rs.getString("CC_CODIGO"));
				t.getDisciplina().setQtdMaximaMatriculas(rs.getInt("QTD_MAX_MATRICULAS"));
				t.getDisciplina().getDetalhes().setId(rs.getInt("ID_COMPONENTE_DETALHES"));
				t.getDisciplina().setNome(rs.getString("CC_NOME"));
				t.setQtdMatriculados(rs.getInt("total_matriculados"));
				t.setSituacaoTurma(new SituacaoTurma());
				t.getSituacaoTurma().setDescricao(rs.getString("SITUACAO_TURMA"));
				turmas.add(t);
				idAtual = t.getId();
			}
			return turmas;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Equivalente a consulta #gerarRelatorio, porém especifica para docentes externos
	 *
	 * @param situacao
	 * @param codigo
	 * @param nomeComponente
	 * @param nomeDocente
	 * @param ano
	 * @param periodo
	 * @param unidadeGestora
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> gerarRelatorioDocenteExterno(int situacao, String codigo, String nomeComponente,
			String nomeDocente, Integer ano, Integer periodo, int unidadeGestora, char nivelEnsino)  throws DAOException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			StringBuffer sql = new StringBuffer();
			StringBuffer inSituacoes = new StringBuffer("(");
			inSituacoes.append(SituacaoMatricula.EM_ESPERA.getId()+",");
			inSituacoes.append(SituacaoMatricula.MATRICULADO.getId()+",");
			inSituacoes.append(SituacaoMatricula.REPROVADO.getId()+",");
			inSituacoes.append(SituacaoMatricula.REPROVADO_FALTA.getId()+",");
			inSituacoes.append(SituacaoMatricula.REPROVADO_MEDIA_FALTA.getId()+",");
			inSituacoes.append(SituacaoMatricula.APROVADO.getId()+")");

			sql.append("SELECT t.id_turma, t.distancia, t.codigo as TURMA_CODIGO, t.local, t.descricao_horario, " +
					"cc.id_disciplina, cc.codigo as CC_CODIGO, ccd.nome as CC_NOME, " +
					"dt.id_docente_externo, p.nome as DOCENTE_NOME, t.ano, t.periodo, " +
					"count(mc.id_matricula_componente) as TOTAL_MATRICULADOS ");
			sql.append("FROM ensino.componente_curricular cc, ensino.componente_curricular_detalhes ccd, " +
					"ensino.docente_externo de, comum.pessoa p, " +
					"ensino.turma t left join ensino.matricula_componente mc on (t.id_turma = mc.id_turma " +
					"and mc.id_situacao_matricula in "+inSituacoes+")" +
					"left join ensino.docente_turma dt on (t.id_turma=dt.id_turma) ");
			sql.append("WHERE dt.id_docente_externo=de.id_docente_externo and de.id_pessoa=p.id_pessoa and " +
					"cc.id_disciplina = t.id_disciplina and cc.id_detalhe = ccd.id_componente_detalhes ");
			if (situacao > 0)
				sql.append(" and t.id_situacao_turma=? ");
			if(nivelEnsino != ' ')
				sql.append(" and cc.nivel='"+nivelEnsino+"'");
			if (ano != null && ano > 0)
				sql.append(" and t.ano = ?");
			if (periodo != null && periodo > 0)
				sql.append(" and t.periodo = ?");
			if (unidadeGestora > 0 && unidadeGestora != 605)
				sql.append(" and cc.id_unidade = ?");
			if (codigo != null && codigo.length() > 0)
				sql.append(" and ccd.codigo = ?");
			if (nomeDocente != null && nomeDocente.length() > 0)
				sql.append(" AND "+ UFRNUtils.toAsciiUpperUTF8("p.nome")+" like "+UFRNUtils.toAsciiUpperUTF8("'"+nomeDocente+"%'"));
			if (nomeComponente != null && nomeComponente.length() > 0)
				sql.append(" AND "+ UFRNUtils.toAsciiUpperUTF8("ccd.nome")+" like "+UFRNUtils.toAsciiUpperUTF8("'"+nomeComponente+"%'"));
			sql.append(" GROUP BY t.id_turma, TURMA_CODIGO, t.local, t.descricao_horario, " +
					"cc.id_disciplina, CC_CODIGO, CC_NOME, " +
					"dt.id_docente_externo, DOCENTE_NOME, t.ano, t.periodo " +
					" ORDER BY ccd.nome asc");

			st = con.prepareStatement(sql.toString());
			int i = 1;
			if (situacao > 0)
				st.setInt(i++, situacao);
			if (ano != null && ano > 0)
				st.setInt(i++, ano);
			if (periodo != null && periodo > 0)
				st.setInt(i++, periodo);
			if (unidadeGestora > 0 && unidadeGestora != 605)
				st.setInt(i++, unidadeGestora);
			if (codigo != null && codigo.length() > 0)
				st.setString(i++, codigo);
			rs = st.executeQuery();

			ArrayList<Turma> turmas = new ArrayList<Turma>(0);
			int idAtual = 0;
			while (rs.next()) {
				Turma t = new Turma();
				t.setId(rs.getInt("ID_TURMA"));
				t.setDistancia(rs.getBoolean("DISTANCIA"));
				if (idAtual == t.getId() && idAtual > 0)
					t = turmas.get(turmas.size()-1);
				DocenteTurma dt = new DocenteTurma();
				dt.setDocenteExterno(new DocenteExterno());
				dt.getDocenteExterno().setId(rs.getInt("ID_DOCENTE_EXTERNO"));
				dt.getDocenteExterno().getPessoa().setNome(rs.getString("DOCENTE_NOME"));
				t.getDocentesTurmas().add(dt);
				if (idAtual == t.getId() && idAtual > 0)
					continue;
				t.setAno(rs.getInt("ANO"));
				t.setPeriodo(rs.getInt("PERIODO"));
				t.setCodigo(rs.getString("TURMA_CODIGO"));
				t.setLocal(rs.getString("LOCAL"));
				t.setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				t.setDisciplina(new ComponenteCurricular(rs.getInt("ID_DISCIPLINA")));
				t.getDisciplina().setCodigo(rs.getString("CC_CODIGO"));
				t.getDisciplina().setNome(rs.getString("CC_NOME"));
				t.setQtdMatriculados(rs.getInt("total_matriculados"));
				turmas.add(t);
				idAtual = t.getId();
			}
			return turmas;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Retorna as turmas criadas a partir da solicitação informada.
	 *
	 * @param idSolicitacao
	 * @return
	 * @throws DAOException

	 */
	public Collection<Turma> findBySolicitacao( int idSolicitacao ) throws DAOException{

		StringBuffer hql = new StringBuffer();

		hql.append(" SELECT turma FROM TurmaSolicitacaoTurma tst, Turma turma ");
		hql.append(" WHERE tst.turma.id = turma.id ");
		hql.append(" AND tst.solicitacao.id = :idSolicitacao ");

		Query q = getSession().createQuery( hql.toString() );

		q.setInteger("idSolicitacao", idSolicitacao);

		@SuppressWarnings("unchecked")
		Collection<Turma> lista = q.list();
		return lista;
	}

	/**
	 * Busca todos os tópicos de aulas das turmas
	 *
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<TopicoAula> findConteudosByTurma(Turma turma) throws DAOException {
		try {
			Criteria c = getCriteria(TopicoAula.class);
			c.add(Restrictions.eq("turma", turma));
			c.add(Restrictions.eq("ativo", true));
			c.addOrder(Order.asc("id"));
			@SuppressWarnings("unchecked")
			List<TopicoAula> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}

	}

	/**
	 * Busca as turmas de subunidade da turma informada
	 *
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findTurmasSubUnidadesByBloco(Turma turma) throws DAOException {
		try {
			String hql = "FROM Turma WHERE turmaAgrupadora.id = " + turma.getId();
			@SuppressWarnings("unchecked")
			Collection<Turma> lista = getSession().createQuery(hql).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna as avaliações de todos os alunos de uma turma para uma mesma unidade cujo tipo for igual
	 * ao tipo da avaliação inicial passada como parâmetro.
	 *
	 * @param avaliacao
	 * @return
	 * @throws DAOException
	 */
	public List<Integer> findAvaliacoesByAvaliacaoInicial(Avaliacao avaliacao) throws DAOException {
		try {

			String idTurmas = "";
			Turma t = avaliacao.getUnidade().getMatricula().getTurma(); 
			if (t.isAgrupadora()){
				@SuppressWarnings("unchecked")
				List <Integer> ids = getSession().createSQLQuery ("select id_turma from ensino.turma where id_turma_agrupadora = " + t.getId()).list();
				for (Integer id : ids)
					idTurmas += (idTurmas.equals("") ? "" : ",") + id;
			}
			
			if (idTurmas.equals(""))
				idTurmas = ""+t.getId();
			
			idTurmas = "(" + idTurmas + ")";

			// Busca as avaliações dos outros discentes de acordo com o número de ordem encontrado acima
			SQLQuery q = getSession().createSQLQuery("select id_avaliacao from ensino.avaliacao_unidade as a " +
													 "inner join ensino.nota_unidade as n on a.id_unidade = n.id_nota_unidade " +
													 "inner join ensino.matricula_componente as m on m.id_matricula_componente = n.id_matricula_componente " +
													 "where n.ativo = trueValue() and m.id_turma in "+idTurmas+" and n.unidade = "+avaliacao.getUnidade().getUnidade()+" and a.denominacao = '"+avaliacao.getDenominacao().replace("'", "''")+"' and a.abreviacao = '"+avaliacao.getAbreviacao().replace("'", "''")+"' ");

			@SuppressWarnings("unchecked")
			List<Integer> avaliacoes = q.list();

			return avaliacoes;
		} catch(Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Busca os vínculos DocenteTurma do servidor, ano, período, nível informados
	 *
	 * @param idServidor
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<DocenteTurma> findByDocente(int idServidor, int ano, int periodo, Character nivel) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" select dt.turma.disciplina.codigo, dt.turma.disciplina.detalhes.nome, dt.turma.codigo, dt.chDedicadaPeriodo," +
					" dt.turma.ano, dt.turma.periodo, dt.turma.distancia ");
			hql.append(" from DocenteTurma dt where ");
			hql.append(" dt.docente.id = "+idServidor);
			hql.append(" and dt.turma.ano = "+ano);
			hql.append(" and dt.turma.periodo = "+periodo);
			if(nivel != null)
				hql.append(" and dt.turma.disciplina.nivel = '"+nivel+"'");
			hql.append(" order by dt.turma.ano desc, dt.turma.periodo desc, dt.turma.disciplina.detalhes.nome, dt.turma.codigo asc");
			Query q = getSession().createQuery(hql.toString());
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			ArrayList<DocenteTurma> result = new ArrayList<DocenteTurma>();

			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = lista.get(a);

				DocenteTurma dt = new DocenteTurma();
				Turma t = new Turma();
				ComponenteCurricular c = new ComponenteCurricular();
				c.setCodigo((String) colunas[col++]);
				c.setNome((String) colunas[col++]);
				t.setDisciplina(c);
				t.setCodigo((String) colunas[col++]);
				dt.setChDedicadaPeriodo((Integer) colunas[col++]);
				t.setAno((Integer) colunas[col++]);
				t.setPeriodo((Integer) colunas[col++]);
				t.setDistancia((Boolean) colunas[col++]);
				dt.setTurma(t);
				result.add(dt);
			}
			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca uma matrícula de uma turma aleatoriamente, excluindo a matrícula informada
	 *
	 * @param turma
	 * @param excluindoEssa
	 * @return
	 * @throws DAOException
	 */
	public MatriculaComponente findMatriculaAleatoriaByTurma(Turma turma, MatriculaComponente excluindoEssa) throws DAOException {
		Query q = getSession().createQuery("from MatriculaComponente mc where mc.turma.id = ? and mc.id != ? and mc.id in (select nu.matricula.id from NotaUnidade nu where nu.ativo = true) order by random()");
		q.setInteger(0, turma.getId());
		q.setInteger(1, excluindoEssa.getId());
		q.setMaxResults(1);
		return (MatriculaComponente) q.uniqueResult();
	}


	/**
	 * Retorna a quantidade de turmas existentes para o componente, ano, período informados
	 *
	 * @param idComponente
	 * @param ano
	 * @param periodo
	 * @return A quantidade de turmas existentes
	 * @throws DAOException
	 */
	public int countTurmasByComponenteAnoPeriodo(int idComponente, int ano, int periodo) throws DAOException{

		try {
			StringBuilder hql = new StringBuilder();
			hql.append( " SELECT COUNT(*) FROM Turma t " );
			hql.append( " WHERE t.disciplina.id = :idComponente " );
			hql.append( " AND t.turmaAgrupadora.id is null " );
			hql.append( " AND t.ano = :ano " );
			hql.append( " AND t.periodo = :periodo " );

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idComponente", idComponente );
			q.setInteger("ano", ano );
			q.setInteger("periodo", periodo );

			return ((Long) q.uniqueResult()).intValue();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/**
	 * Retorna a quantidade de turmas EAD existentes para o componente em um polo, ano e período informados.
	 *
	 * @param idComponente
	 * @param ano
	 * @param periodo
	 * @return A quantidade de turmas existentes
	 * @throws DAOException
	 */
	public Collection<Turma> turmasEadByComponentePoloAnoPeriodo(int idComponente, int ano, int periodo, int idPolo) throws DAOException{

		try {
			StringBuilder hql = new StringBuilder();
			hql.append( " SELECT t FROM Turma t " );
			hql.append( " WHERE t.disciplina.id = :idComponente " );
			hql.append( " AND t.turmaAgrupadora.id is null " );
			hql.append( " AND t.ano = :ano " );
			hql.append( " AND t.periodo = :periodo " );
			hql.append( " AND t.polo.id = :idPolo " );
			hql.append( " AND t.situacaoTurma.id != " + SituacaoTurma.EXCLUIDA);

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idComponente", idComponente );
			q.setInteger("ano", ano );
			q.setInteger("periodo", periodo );
			q.setInteger("idPolo", idPolo );

			@SuppressWarnings("unchecked")
			Collection <Turma> rs = q.list();
			
			return rs;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Busca uma turma pela chave primária de forma otimizada, utilizando uma projeção
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Turma findByPrimaryKeyOtimizado(int id) throws DAOException {
		String projecao = "t.id," +
				" cc.id as t.disciplina.id," +
				" ccd.nome as t.disciplina.detalhes.nome," +
				" cc.codigo as t.disciplina.codigo," +
				" ccd.chTotal as t.disciplina.detalhes.chTotal, " + 
				" ccd.crAula  as t.disciplina.detalhes.crAula," +
				" ccd.crLaboratorio as t.disciplina.detalhes.crLaboratorio," +
				" ccd.crEstagio as t.disciplina.detalhes.crEstagio," +
				" cc.nivel as t.disciplina.nivel," +
				" cc.unidade.id as t.disciplina.unidade.id," +
				" t.ano," +
				" t.periodo," +
				" t.distancia," +
				" t.descricaoHorario," +
				" t.local," +
				" t.codigo, " +
				" t.situacaoTurma.id," +
				" t.situacaoTurma.descricao," +
				" p.id as t.polo.id," +
				" c.nome as t.polo.cidade.nome," +
				" uf.sigla as t.polo.cidade.unidadeFederativa.sigla," +
				" t.dataInicio," +
				" t.dataFim," +
				" t.agrupadora," +
				" t.turmaAgrupadora.id";
		StringBuilder hql = new StringBuilder("select ");
		hql.append(HibernateUtils.removeAliasFromProjecao(projecao));
		hql.append(" from Turma t" +
				" left join t.disciplina cc" +
				" left join cc.detalhes ccd," +
				" Turma t2" +
				" left join t2.polo p" +
				" left join p.cidade c" +
				" left join c.unidadeFederativa uf" +
				" where t.id = t2.id and t.id=?");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger(0, id);
		@SuppressWarnings("unchecked")
		Collection<Turma> lista = HibernateUtils.parseTo(q.list(), projecao, Turma.class, "t");
		if (lista != null)
			return lista.iterator().next();
		else
			return null;
	}
	
	/**
	 * Busca uma turma pela chave primária de forma otimizada, utilizando uma projeção
	 *
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public List <Turma> findByPrimaryKeyOtimizado(List <Integer> ids) throws DAOException {
		String projecao = "t.id," +
				" cc.id as t.disciplina.id," +
				" ccd.nome as t.disciplina.detalhes.nome," +
				" cc.codigo as t.disciplina.codigo," +
				" ccd.chTotal as t.disciplina.detalhes.chTotal," +
				" ccd.crAula as t.disciplina.detalhes.crAula," +
				" ccd.crLaboratorio as t.disciplina.detalhes.crLaboratorio," +
				" ccd.crEstagio as t.disciplina.detalhes.crEstagio," +
				" cc.nivel as t.disciplina.nivel," +
				" cc.unidade.id as t.disciplina.unidade.id," +
				" t.ano," +
				" t.periodo," +
				" t.distancia," +
				" t.descricaoHorario," +
				" t.local," +
				" t.codigo," +
				" t.situacaoTurma.id," +
				" t.situacaoTurma.descricao," +
				" p.id as t.polo.id," +
				" c.nome as t.polo.cidade.nome," +
				" uf.sigla as t.polo.cidade.unidadeFederativa.sigla," +
				" t.dataInicio," +
				" t.dataFim," +
				" t.agrupadora," +
				" t.turmaAgrupadora.id";
		Query q = getSession().createQuery("select " + HibernateUtils.removeAliasFromProjecao(projecao)
				+ " from Turma t left join t.disciplina cc left join cc.detalhes ccd, Turma t2 left join t2.polo p left join p.cidade c left join c.unidadeFederativa uf "
				+ "where t.id = t2.id and t.id in " + UFRNUtils.gerarStringIn(ids));
		
		@SuppressWarnings("unchecked")
		List <Turma> rs = (List<Turma>) HibernateUtils.parseTo(q.list(), projecao, Turma.class, "t");
		return rs;
	}

	/**
	 * Retorna as turmas com as seguintes informações
	 *
	 * @param idComponente
	 * @param horario
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findByComponenteHorarioAnoPeriodo(int idComponente, String horario, int ano, int periodo) throws DAOException {
		Criteria c = getSession().createCriteria(Turma.class);
		c.add( Restrictions.eq("disciplina.id", idComponente) );
		c.add( Restrictions.eq("descricaoHorario", horario) );
		c.add( Restrictions.eq("ano", ano) );
		c.add( Restrictions.eq("periodo", periodo) );

		Collection<Integer> situacoes = new ArrayList<Integer>();
		situacoes.add( SituacaoTurma.ABERTA );
		situacoes.add( SituacaoTurma.A_DEFINIR_DOCENTE );
		c.add( Restrictions.in("situacaoTurma.id", situacoes) );

		@SuppressWarnings("unchecked")
		List<Turma> lista = c.list();
		return lista;
	}

	/**
	 * Busca as turmas passíveis de transferência de alunos
	 *
	 * @param ano
	 * @param periodo
	 * @param idComponente
	 * @return
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	public Collection<Turma> findOrigemTransferencia(Integer ano, Integer periodo, int idComponente) throws DAOException, LimiteResultadosException {
		return findGeral(null, null, null, null, null, null,
				new Integer[] { SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE }, ano, periodo,
				null, null, null, null, idComponente, null, null, null,null,null);
	}

	/**
	 * Busca as turmas candidatas como destino de transferências de alunos de uma turma
	 *
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findDestinosTransferencia(Turma turma, boolean todosPeriodos, ModalidadeEducacao modalidade) throws DAOException {
		try {
			Collection<Turma> turmas =  new ArrayList<Turma>();
			turmas = findGeral(null, null, null, null, null, null,
					new Integer[] { SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE },
					todosPeriodos ? null : turma.getAno(), todosPeriodos ? null : turma.getPeriodo(),
					null, null, modalidade, null,
					turma.getDisciplina().getId(), null, null, null,null,null);

			for (Turma t : turmas) {
				if (t.getId() == turma.getId()) {
					turmas.remove(t);
					break;
				}
			}

			return turmas;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca as turmas do servidor informado no ano.período informado
	 *
	 * @param servidor
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> findTurmasSemestreByDocente(Servidor servidor, int ano, int periodo, Boolean distancia) throws DAOException {
		StringBuilder hql = new StringBuilder("select dt from DocenteTurma dt left join  dt.turma t "
				+ "where dt.docente.id = ? and t.situacaoTurma.id in ")
				.append(UFRNUtils.gerarStringIn(SituacaoTurma.getSituacoesValidas()))
				.append(" and t.ano = ? and t.periodo = ? and t.disciplina.nivel = 'G'");
		if (distancia != null) {
			if (distancia)
				hql.append(" and (t.distancia = true or t.polo is not null)");
			else
				hql.append(" and (t.distancia = false or t.polo is null)");
		}
		@SuppressWarnings("unchecked")
		List<DocenteTurma> lista = getSession()
			.createQuery(hql.toString())
				.setInteger(0, servidor.getId())
				.setInteger(1, ano)
				.setInteger(2, periodo)
				.list();
		return lista;
	}

	

	/**
	 * Busca os vínculos DocenteTurma das turmas que possuem o docente externo informado no ano, período informado
	 *
	 * @param docenteExterno
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> findTurmasSemestreByDocenteExterno(DocenteExterno docenteExterno, int ano, int periodo, Boolean distancia) throws DAOException {
		StringBuilder hql = new StringBuilder("select dt from DocenteTurma dt left join  dt.turma t "
							+ "where dt.docenteExterno.id = ? and t.situacaoTurma.id in "
							+ UFRNUtils.gerarStringIn(SituacaoTurma.getSituacoesValidas())
							+ " and t.ano = ? and t.periodo = ? and t.disciplina.nivel = 'G'");
		if (distancia != null) {
			if (distancia)
				hql.append(" and (t.distancia = true or t.polo is not null)");
			else
				hql.append(" and (t.distancia = false or t.polo is null)");
		}
		@SuppressWarnings("unchecked")
		List<DocenteTurma> lista = getSession()
			.createQuery(hql.toString())
					.setInteger(0, docenteExterno.getId())
					.setInteger(1, ano)
					.setInteger(2, periodo)
					.list();
		return lista;
	}

	/**
	 * Indica se o discente estava matriculado em alguma turma no ano.período informado
	 *
	 * @param discente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public boolean discenteEstavaMatriculadoNoPeriodo(Discente discente, int ano, int periodo) {
		return getJdbcTemplate().queryForInt("select count(mc.id_matricula_componente) from ensino.matricula_componente mc "
				+ "where id_turma is not null and mc.id_situacao_matricula in "
				+ UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesMatriculadoOuConcluido())
				+ " and mc.ano = ? and mc.periodo = ? and mc.id_discente = ?"
				+ " and (select count(*) from ensino.docente_turma where id_turma = mc.id_turma) > 0", new Object[] { ano, periodo, discente.getId() }) > 0;
	}

	/**
	 * Retorna várias informações úteis sobre uma turma
	 * para que o aluno possar visualizar no celular
	 *
	 * @param codigoDisciplia
	 * @param ano
	 * @param semestre
	 * @return
	 * @throws DAOException
	 */
	public List<DocenteTurma> findTurmasAtivasByCodigo(String codigoDisciplina, int ano, int semestre){

		String sql = " select distinct(t.codigo), cc.codigo, cd.codigo, t.descricao_horario, p.nome, cd.nome_ascii, cd.ch_aula, t.local " +
					 " from ensino.turma t " +
					 " left join ensino.docente_turma dt on dt.id_turma = t.id_turma " +
					 " left join ensino.componente_curricular cc on cc.id_disciplina = t.id_disciplina " +
					 " left join ensino.componente_curricular_detalhes cd on cd.id_componente = cc.id_disciplina " +
					 " left join rh.servidor s on s.id_servidor = dt.id_docente " +
					 " left join comum.pessoa p on p.id_pessoa = s.id_pessoa " +
					 " where t.ano = " + ano + " and t.periodo = " + semestre + " and id_situacao_turma = 1 and cc.codigo = " + "'" + codigoDisciplina + "'";

		@SuppressWarnings("unchecked")
		List<DocenteTurma> lista = getJdbcTemplate().query(sql, new RowMapper() {

			public DocenteTurma mapRow(ResultSet rs, int arg1) throws SQLException {

				Pessoa p = new Pessoa();
					p.setNome(rs.getString(5));

				Servidor s = new Servidor();
					s.setPessoa(p);

				DocenteTurma dt = new DocenteTurma();
					dt.setDocente(s);

				Turma turma = new Turma();
					turma.setDescricaoHorario(rs.getString("descricao_horario"));
					turma.setLocal(rs.getString("local"));

				ComponenteDetalhes ccdet = new ComponenteDetalhes();
					ccdet.setChAula(rs.getInt("ch_aula"));
					ccdet.setCodigo(rs.getString(1));
					ccdet.setNome_ascii(rs.getString("nome_ascii"));

				ComponenteCurricular cc = new ComponenteCurricular();
					cc.setDetalhes(ccdet);

					turma.setDisciplina(cc);

					dt.setTurma(turma);

					return dt;
			}
		});
		return lista;
	}

	/**
	 * Verifica se existe algum discente do curso informado matriculado em uma turma específica
	 *
	 * @param turma
	 * @param curso
	 * @return
	 */
	public boolean existsAlunosCurso(Turma turma, Curso curso) {
		String sql = " select count(*) from discente d " +
			" where d.id_discente in (select id_discente "+
				" from ensino.matricula_componente m join discente using(id_discente) "+
				" where discente.id_curso = ? and m.id_turma  = ?)";

		return getJdbcTemplate().queryForLong(sql, new Object[] {curso.getId(), turma.getId()}) > 0;
	}

	/**
	 * Consulta gera o relatório que exibe o quantitativo de solicitações,
	 * turmas, matrículas solicitadas, matrículas efetivas e
	 * matrículas indeferidas por componente curricular por semestre
	 *
	 * @param ano
	 * @param periodo
	 * @return Coleção de RelatorioQuantitativoTurmas
	 * @throws DAOException
	 */
	public ArrayList<RelatorioQuantitativoTurmas> gerarRelatorioQuantitativoGeralTurmas( int ano, int periodo ) throws DAOException{

		String sql = " select cc.id_disciplina AS ID_COMPONENTE, cc.codigo AS COD_COMPONENTE, max(ccd.nome) AS NOME_COMPONENTE, " +
			" sum(1) AS TOTAL, " +
			" sum(case when st.situacao = 6 then 1 else 0 end) AS TURMAS_NEGADAS, " +
			" sum(case when st.situacao in (1,2,3) then 1 else 0 end) as TURMAS_NAO_ATENDIDAS, " +
			" sum(case when st.situacao = 4 then 1 else 0 end) AS TURMAS_ATENDIDAS_PARCIALMENTE, " +
			" sum(case when st.situacao = 5 then 1 else 0 end) AS TURMAS_ATENDIDAS, " +
			" " +
			" ( select count(mc.id_matricula_componente) from ensino.matricula_componente mc " +
			" where mc.ano = " + ano + " and mc.periodo = " + periodo +
			" and mc.id_situacao_matricula <> 11 and mc.id_componente_curricular = cc.id_disciplina) AS MATRICULADOS, " +
			" " +
			" ( select count(mc.id_matricula_componente) from ensino.matricula_componente mc " +
			" where mc.ano = " + ano + " and mc.periodo = " + periodo +
			" and mc.id_situacao_matricula = 11 and mc.id_componente_curricular = cc.id_disciplina) AS INDEFERIDOS, " +
			" " +
			" ( select sum(sol.vagas) from graduacao.solicitacao_turma sol " +
			" where sol.ano = " + ano + " and sol.periodo = " + periodo + " and sol.situacao <> 7 " +
			" and sol.id_componente_curricular = cc.id_disciplina) AS VAGAS_SOLICITADAS, " +
			" " +
			" ( select sum(t1.capacidade_aluno) from ensino.turma t1 " +
			" join graduacao.turma_solicitacao_turma tst on tst.id_turma = t1.id_turma " +
			" join graduacao.solicitacao_turma sol on sol.id_solicitacao_turma = tst.id_solicitacao " +
			" where t1.ano = " + ano + " and t1.periodo = " + periodo +
			" and t1.id_disciplina = cc.id_disciplina) AS VAGAS_ATENDIDAS " +
			" " +
			" from graduacao.solicitacao_turma st " +
			" join ensino.componente_curricular cc on cc.id_disciplina = st.id_componente_curricular " +
			" join ensino.componente_curricular_detalhes ccd on cc.id_disciplina = ccd.id_componente " +
			" where st.ano = " + ano + " and st.periodo = " + periodo +
			" and cc.nivel = 'G' " +
			" group by cc.id_disciplina, cc.codigo " +
			" having sum(case when st.situacao in (1,2,3,6) then 1 else 0 end) > 0 " +
			" order by cc.codigo ";


		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql.toString());
			rs = st.executeQuery();

			ArrayList<RelatorioQuantitativoTurmas> resultado = new ArrayList<RelatorioQuantitativoTurmas>(0);
			while (rs.next()) {
				RelatorioQuantitativoTurmas r = new RelatorioQuantitativoTurmas();

				r.setIdDisciplina( rs.getInt( "ID_COMPONENTE" ) );
				r.setCodigoDisciplina( rs.getString( "COD_COMPONENTE" ) );
				r.setNomeDisciplina( rs.getString( "NOME_COMPONENTE" ) );
				r.setTotalSolicitacoes( rs.getInt( "TOTAL" ) );
				r.setTurmasNegadas( rs.getInt( "TURMAS_NEGADAS" ) )  ;
				r.setTurmasNaoAtendidas( rs.getInt( "TURMAS_NAO_ATENDIDAS" ) );
				r.setTurmasAtendidasParcialmente( rs.getInt( "TURMAS_ATENDIDAS_PARCIALMENTE" ) );
				r.setTurmasAtendidas( rs.getInt( "TURMAS_ATENDIDAS" ) );
				r.setVagasSolicitadas( rs.getInt( "VAGAS_SOLICITADAS" ) );
				r.setVagasAtendidas( rs.getInt( "VAGAS_ATENDIDAS" ) );

				resultado.add( r );
			}

			return resultado;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	/**
	 * Retorna os ids das turmas que o discente está matriculado no ano período informado
	 * @param idDiscente
	 * @param ano
	 * @param periodo
	 * @return lista dos ids das turmas
	 * @throws DAOException
	 */
	public List<Integer> findIdsTurmasMatriculadasByDiscenteAnoPeriodo( int idDiscente, int ano, int periodo ) throws DAOException{

		StringBuilder hql = new StringBuilder( " SELECT mc.turma.id FROM MatriculaComponente mc " );
		hql.append( " where mc.ano = :ano AND mc.periodo = :periodo " );
		hql.append( " AND mc.discente.id = :idDiscente " );
		hql.append( " AND mc.situacaoMatricula.id = " + SituacaoMatricula.MATRICULADO.getId() );

		Query q = getSession().createQuery(hql.toString());
		q.setInteger( "ano" , ano);
		q.setInteger( "periodo" , periodo);
		q.setInteger( "idDiscente" , idDiscente);

		@SuppressWarnings("unchecked")
		List<Integer> lista = q.list();
		return lista;
	}

	/** Retorna a quantidade de turmas abertas ou consolidadas que este componente tem.
	 * @param componente
	 * @return
	 * 		Caso o componente seja nulo, retorna -1. Caso o componente nunca teve matrícula (componente novo), retorna 0 (zero). Caso contrário, retorna o número de matrículas.
	 * @throws DAOException
	 */

	public int countTurmasByComponente(ComponenteCurricular componente, int situacoes[]) throws DAOException {
		if (componente == null) return -1;
		try {
			String hql = " SELECT COUNT(*) FROM Turma t " +
					" WHERE t.disciplina.id = :idComponente " +
            		" AND situacaoTurma.id in " + gerarStringIn(situacoes);
            Query q = getSession().createQuery(hql);
            q.setInteger("idComponente", componente.getId());
            return ((Long) q.uniqueResult()).intValue();
        } catch (Exception e) {
            throw new DAOException(e.getMessage(), e);
        }
	}
	/**
	 * Retorna o numero de turma abertas passando uma turma agrupadora.
	 * Caso ocorra algum erro retorna -1;
	 * @param turma
	 * @return
	 */
	public int countSubTurmasAbertas(Turma turma) {
		
		int idAgrupadora = turma.getId();
		
		String hql = "SELECT COUNT(*) from Turma t " +
				" WHERE t.turmaAgrupadora.id = :idTurma AND t.situacaoTurma.id = :situacaoAberta ";
		try {
			Query q = getSession().createQuery(hql);
			q.setInteger("idTurma", idAgrupadora);
			q.setInteger("situacaoAberta", SituacaoTurma.ABERTA);
			return ((Long) q.uniqueResult()).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Retorna as possíveis subturmas existentes da turma informada
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findSubturmasByTurmaFetchDocentes( Turma turma ) throws DAOException{
		if( turma == null || turma.getId() == 0 )
			return new ArrayList<Turma>();

		String hql = " SELECT DISTINCT t FROM Turma t LEFT JOIN FETCH t.docentesTurmas where t.turmaAgrupadora.id = :idTurma AND t.situacaoTurma.id not in "
			+ gerarStringIn( new int[] {SituacaoTurma.EXCLUIDA} ) + " ORDER BY t.codigo ";
		@SuppressWarnings("unchecked")
		List<Turma> lista = getSession().createQuery(hql).setInteger("idTurma", turma.getId()).list();
		return lista;
	}
	
	/**
	 * Retorna a subturma na qual o aluno está matriculado para a turma agrupadora passada.
	 * 
	 * @param turmaAgrupadora
	 * @param discente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Turma findSubturmaByTurmaDiscente (Turma turmaAgrupadora, DiscenteAdapter discente) throws HibernateException, DAOException{
		
		List <Integer> situacoes = new ArrayList <Integer> ();
		
		for (SituacaoMatricula sm : SituacaoMatricula.getSituacoesAtivas())
			situacoes.add(sm.getId());
		
		String hql = "select t from Turma t where turmaAgrupadora.id = :idAgrupadora and t.id in (select m.turma.id from MatriculaComponente m where m.discente.id = :idDiscente and m.situacaoMatricula.id in "+UFRNUtils.gerarStringIn(situacoes)+")";
		Query q = getSession().createQuery (hql);
		q.setInteger("idAgrupadora", turmaAgrupadora.getId());
		q.setInteger("idDiscente", discente.getId());
		
		return (Turma) q.uniqueResult();
	}

	/**
	 * Turmas que não foram consolidadas até o ano atual ou que estão sem professor. Não considera as turmas anteriores a 2002.
	 *
	 * @param nivel
	 * @param periodoFim
	 * @param anoFim
	 * @param periodoInicio
	 * @param anoInicio
	 * @param periodoAnterior
	 * @param anoAnterior
	 * @param fim
	 * @param inicio
	 * @return
	 */
	public List<Turma> findTurmasPendentes(final char nivel, Integer anoInicio, Integer anoFim, String anoPeriodo) {

		StringBuilder projecao = new StringBuilder();
		projecao.append("select t.ano, t.periodo, t.distancia, t.codigo as turma_cod, cc.codigo as disc_cod, ccd.nome as disc_nome, ccd.ch_total, st.id_situacao_turma, st.descricao , u.nome as unidade_nome");

		List<Object> lista = new ArrayList<Object>();
		lista.add(String.valueOf(nivel));

		if (NivelEnsino.LATO == nivel)
			projecao.append(", c.nome as curso_nome");

		StringBuilder query = new StringBuilder();

		query.append(" from ensino.turma t " +
				" 	join ensino.situacao_turma st on t.id_situacao_turma = st.id_situacao_turma " +
				" 	join ensino.componente_curricular cc on t.id_disciplina = cc.id_disciplina " +
				" 	join ensino.componente_curricular_Detalhes ccd on cc.id_disciplina = ccd.id_componente " +
				" 	join comum.unidade u on cc.id_unidade = u.id_unidade ");

		if (NivelEnsino.LATO == nivel)
			query.append(" 	join curso c on c.id_curso = t.id_curso ");

		query.append(" where cc.nivel = ? and " +
				" 	( t.id_situacao_turma = " + SituacaoTurma.A_DEFINIR_DOCENTE + " or ( t.id_situacao_turma = " + SituacaoTurma.ABERTA + " and ((ano *10) + periodo) < " + anoPeriodo  + "  ) or" +
				" 	( ( select count(id_docente_turma) from ensino.docente_turma where id_turma = t.id_turma) = 0 )) ");

		if (!isEmpty(anoInicio)) {
			query.append(" 	and ano >= ? ");
			lista.add(anoInicio);
		}
		if (!isEmpty(anoFim)) {
			query.append(" 	and ano <= ? ");
			lista.add(anoFim);
		}
		query.append("	and t.id_situacao_turma not in " + gerarStringIn(new Object[] {SituacaoTurma.EXCLUIDA}) +
				" order by u.nome asc, ano asc, periodo asc ");

		@SuppressWarnings("unchecked")
		List<Turma> listaTurmas = getJdbcTemplate().query(projecao.append(query).toString(), lista.toArray(), new RowMapper(){
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Turma turma = new Turma();
				turma.setAno( rs.getInt("ano") );
				turma.setPeriodo( rs.getInt("periodo") );
				turma.setDistancia( rs.getBoolean("distancia") );
				turma.setCodigo( rs.getString("turma_cod") );
				turma.setDisciplina(new ComponenteCurricular());
				turma.getDisciplina().setCodigo( rs.getString("disc_cod") );
				turma.getDisciplina().setDetalhes(new ComponenteDetalhes());
				turma.getDisciplina().getDetalhes().setNome( rs.getString("disc_nome") );
				turma.getDisciplina().getDetalhes().setChTotal( rs.getInt("ch_total") );
				turma.getDisciplina().setUnidade(new Unidade());
				turma.getDisciplina().getUnidade().setNome( rs.getString("unidade_nome") );
				if (NivelEnsino.LATO == nivel) {
					turma.setCurso(new Curso());
					turma.getCurso().setNome( rs.getString("curso_nome") );
				}
				turma.setSituacaoTurma(new SituacaoTurma());
				turma.getSituacaoTurma().setId( rs.getInt("id_situacao_turma") );
				turma.getSituacaoTurma().setDescricao( rs.getString("descricao") );

				return turma;
			}
		});
		return listaTurmas;
	}

	/**
	 * Select otimizado só pra trazer o ID da Turma e o ID da Situação da Turma
	 *
	 * @param ids
	 * @return
	 */
	public List<Turma> findSituacaoesByIds(Object[] ids) {

		String sql = "select t.id_turma, t.id_situacao_turma from ensino.turma t" +
				"			inner join ensino.componente_curricular cc on (t.id_disciplina = cc.id_disciplina) " +
				"			inner join ensino.componente_curricular_detalhes ccd on (ccd.id_componente_detalhes = cc.id_detalhe) " +
				" where id_turma in " + gerarStringIn(ids) +
				" order by t.ano desc, t.periodo desc, ccd.nome";

		@SuppressWarnings("unchecked")
		List<Turma> lista = getJdbcTemplate().query(sql, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Turma turma = new Turma();
				turma.setId(rs.getInt("id_turma"));
				turma.setSituacaoTurma(new SituacaoTurma());
				turma.getSituacaoTurma().setId(rs.getInt("id_situacao_turma"));
				return turma;
			}

		});
		return lista;
	}
	
	/**
	 * Select otimizado só pra trazer o ano, período e nome da disciplina
	 *
	 * @param ids
	 * @return
	 */
	public List<Turma> findTurmasByIds(Object[] ids) {

		String sql = "	select t.id_turma, t.id_situacao_turma, t.ano, t.periodo, ccd.nome, "+
				" ccd.ch_aula, ccd.ch_laboratorio, ccd.ch_estagio, cc.nivel, cc.codigo from ensino.turma t " +
				"			inner join ensino.componente_curricular cc on (t.id_disciplina = cc.id_disciplina) " +
				"			inner join ensino.componente_curricular_detalhes ccd on (ccd.id_componente_detalhes = cc.id_detalhe) " +
				" 		where id_turma in " + gerarStringIn(ids) +
				"		order by t.ano asc, t.periodo asc, ccd.nome, ccd.ch_aula, t.codigo";
		
		@SuppressWarnings("unchecked")
		List<Turma> lista = getJdbcTemplate().query(sql, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Turma turma = new Turma();
				turma.setId(rs.getInt("id_turma"));
				turma.setSituacaoTurma(new SituacaoTurma());
				turma.getSituacaoTurma().setId(rs.getInt("id_situacao_turma"));				
				turma.setAno(rs.getInt("ano"));
				turma.setPeriodo(rs.getInt("periodo"));
				turma.setDisciplina(new ComponenteCurricular());
				turma.getDisciplina().setNivel(rs.getString("nivel").charAt(0));
				turma.getDisciplina().setCodigo(rs.getString("codigo"));
				turma.getDisciplina().setDetalhes(new ComponenteDetalhes());
				turma.getDisciplina().getDetalhes().setNome(rs.getString("nome"));
				turma.getDisciplina().getDetalhes().setChAula(rs.getInt("ch_aula"));
				turma.getDisciplina().getDetalhes().setChLaboratorio(rs.getInt("ch_laboratorio"));
				turma.getDisciplina().getDetalhes().setChEstagio(rs.getInt("ch_estagio"));
				return turma;
			}

		});
		return lista;
	}

	/**
	 * Retorna o id da situação da turma passada no parâmetro
	 * @param id
	 * @return o id da situação da turma
	 */
	public int findSituacaoTurmaByIdTurma(int id){
		return getJdbcTemplate().queryForInt("select id_situacao_turma from ensino.turma where id_turma = ? ", new Object[] { id });
	}
	
	/**
	 * Retorna uma lista das turma tendo com base o ano, o período e a unidade da(s) turma(s) que se enquadram 
	 * nos parâmetros informados.  
	 * 
	 * @param unidade
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Turma> findByDepartamento(Unidade unidade, Integer ano, Integer periodo) throws DAOException {

		String sql = "select t.id_turma, ccd.nome_ascii"+
			" from ensino.turma t inner join ensino.componente_curricular cc using (id_disciplina)"+
			" inner join ensino.componente_curricular_detalhes ccd on (ccd.id_componente_detalhes = cc.id_detalhe)"+
			" where cc.id_unidade = "+unidade.getId()+" and t.ano = "+ano+" and t.periodo = "+periodo+ 
			" order by ccd.nome_ascii";
		
		List<Turma> lista = getJdbcTemplate().query(sql, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Turma turma = new Turma();
				turma.setDisciplina(new ComponenteCurricular());
				turma.setId(rs.getInt("id_turma"));
				turma.getDisciplina().getDetalhes().setNome_ascii(rs.getString("nome_ascii"));
				return turma;
			}
		});
		return lista;
	}
	
	/**
	 * Retorna o quantitativo de matriculados, aprovados, reprovados e trancados de uma turma
	 * passada por parâmetro.
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public Map<String,Object> findQuantitativoSituacaoTurma(int idTurma) throws DAOException{
		String sql =
		"	select coalesce(sum(case when (id_situacao_matricula = 2) then 1 else 0 end),0) as matriculados, "+
		"	   coalesce(sum(case when (id_situacao_matricula = 4) then 1 else 0 end),0) as aprovados, "+
		"	   coalesce(sum(case when (id_situacao_matricula = 6) then 1 else 0 end),0) as reprovado_nota, "+
		"	   coalesce(sum(case when (id_situacao_matricula = 7) then 1 else 0 end),0) as reprovado_falta, "+
		"	   coalesce(sum(case when (id_situacao_matricula = 5) then 1 else 0 end),0) as trancados "+ 
		"	from ensino.matricula_componente  "+
		"	where id_turma = "+idTurma;		
		
		@SuppressWarnings("unchecked")
		Map<String, Object> mapa = getJdbcTemplate().queryForMap(sql);
		return mapa; 
	}

	/**
	 * Retorna uma lista contendo todas as subturmas e a agrupadora de acordo com a turma passada.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Turma> findTurmasByTurma(Turma turma) throws DAOException {
		
	
		List <Turma> rs = new ArrayList <Turma> ();
		
		int idAgrupadora = 0;
		
		if (turma.isAgrupadora()){
			idAgrupadora = turma.getId();
		} else if (turma.getTurmaAgrupadora() != null){
			idAgrupadora = turma.getTurmaAgrupadora().getId();
		}
		
		
		if (idAgrupadora > 0){
			@SuppressWarnings("unchecked")
			List <Turma> turmas = getSession().createSQLQuery (
					"select * from ensino.turma " +
					" where (id_turma_agrupadora = " + idAgrupadora + " or id_turma = " + idAgrupadora + ") "+
					" and id_situacao_turma != "+SituacaoTurma.EXCLUIDA
					).addEntity(Turma.class).list();
			
			if (turmas != null && !turmas.isEmpty())
				rs.addAll(turmas);
		} else
			rs.add((Turma) getSession().createSQLQuery ("select * from ensino.turma where id_turma = " + turma.getId()).addEntity(Turma.class).uniqueResult());

		return rs;
	}
	
	/**
	 * Retorna uma lista de Strings que representa os anos e períodos que o docente teve turmas.
	 * 
	 * @param docente
	 * @return
	 * @throws DAOException 
	 */
	public List<String> findAnoPeriodoOfTurmasByDocente ( Docente docente ) throws DAOException
	{
		List<String> result = new ArrayList<String>();  
		
		String sql = " select distinct t.ano , t.periodo from ensino.docente_turma as dt "+
		               "inner join ensino.turma as t using ( id_turma ) " +
		               "where id_docente = " + docente.getId() + "order by ano desc , periodo desc";
		
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		
		try {
			con = Database.getInstance().getSigaaConnection();
			st = con.prepareStatement(sql);
			rs = st.executeQuery();
			
			while(rs.next()){
				String ano = Integer.toString(rs.getInt("ano"));
				String periodo = Integer.toString(rs.getInt("periodo"));
				String anoPeriodo = ano + "." + periodo;
				result.add(anoPeriodo);
			}	
			
		   }catch(Exception e) {
				throw new DAOException(e);
			} finally {
				closeResultSet(rs);
				closeStatement(st);
				Database.getInstance().close(con);
			}
			return result;
	}
	
}