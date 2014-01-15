/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 15/06/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dominio.CurriculoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;

/**
 * Classe de Dao com consultas sobre as entidade de relação entre Turma e série de ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class TurmaSerieDao extends GenericSigaaDAO{

	/**
	 * Retornar a TurmaSerie por Curso, Série,tipo ou Ano de ensino médio informados.
	 * @param isMatriculaSerie 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TurmaSerie> findByCursoSerieAno(CursoMedio cursoMedio,
			Serie serie, int ano, boolean inativos, char nivelEnsino, boolean isMatriculaSerie) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(TurmaSerie.class);
			Criteria cSerie = c.createCriteria("serie");
			if ( cursoMedio != null && isNotEmpty(cursoMedio) ) 
				cSerie.add(Restrictions.eq("cursoMedio", cursoMedio));
			if ( serie != null && isNotEmpty(serie) )
				c.add(Restrictions.eq("serie", serie));
			if ( ano > 0 && isNotEmpty(ano) )
				c.add(Restrictions.eq("ano", ano));
			if (!inativos) {
				c.add(Restrictions.eq("ativo", true));
			}	
			if(isMatriculaSerie){
				c.add(Restrictions.eq("dependencia",false));
			}
			cSerie.addOrder(Order.asc("cursoMedio"));
			cSerie.addOrder(Order.asc("numero"));
			c.addOrder(Order.asc("nome"));
			c.addOrder(Order.asc("ano"));
			Collection<TurmaSerie> lista =  c.list();
			
			if (!lista.isEmpty()){
				Map<Integer, Long> mapAlunosTurma = findQtdeAlunosByTurmas((List<TurmaSerie>) lista);
				for (TurmaSerie turma : lista) {
					if ( mapAlunosTurma.get(turma.getId()) != null )
						turma.setQtdMatriculados(mapAlunosTurma.get(turma.getId()));
				}
				Map<Integer, Long> mapMatriculadosDisciplina = findQtdeAlunosMatriculadosByDisciplina((List<TurmaSerie>) lista);
				for (TurmaSerie turma : lista) {
					for (TurmaSerieAno tsa : turma.getDisciplinas()) {
						if ( mapMatriculadosDisciplina.get(tsa.getTurma().getId()) != null )
							tsa.getTurma().setQtdMatriculados(mapMatriculadosDisciplina.get(tsa.getTurma().getId()));
					}
				}
			}
			
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retornar a TurmaSerie por Curso, Série,tipo ou Ano de ensino médio informados.
	 * @param isMatriculaSerie 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */	
	public TurmaSerie findDependenciaByCursoSerieAnoCurriculo(CursoMedio cursoMedio,
			Serie serie, int ano, boolean inativos, CurriculoMedio curriculo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(TurmaSerie.class);
			Criteria cSerie = c.createCriteria("serie");
			if ( cursoMedio != null && isNotEmpty(cursoMedio) ) 
				cSerie.add(Restrictions.eq("cursoMedio", cursoMedio));
			if ( serie != null && isNotEmpty(serie) )
				c.add(Restrictions.eq("serie", serie));
			if ( ano > 0 && isNotEmpty(ano) )
				c.add(Restrictions.eq("ano", ano));
			if (!inativos) {
				c.add(Restrictions.eq("ativo", true));
			}				
			if (curriculo != null && isNotEmpty(curriculo)) {
				c.add(Restrictions.eq("curriculo", curriculo));
			}	
			c.add(Restrictions.eq("dependencia",true));
	
			cSerie.addOrder(Order.asc("cursoMedio"));
			c.addOrder(Order.asc("ano"));
			cSerie.addOrder(Order.asc("numero"));
			c.addOrder(Order.desc("nome"));
			TurmaSerie turmaDependencia =  (TurmaSerie) c.uniqueResult();
			
					
			return turmaDependencia;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retornar a coleção de TurmaSerie por discente, Série ou Ano de ensino médio informados.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TurmaSerie> findByDiscenteSerieAno(DiscenteMedio discenteMedio,
			Serie serie, int ano) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select new TurmaSerie(ts.id, ts.ano, ts.nome, ts.serie, " +
					"ts.capacidadeAluno, ts.ativo, ts.dataCadastro)" +
					" from MatriculaDiscenteSerie as mds" +
					" inner join mds.turmaSerie as ts" +
					" inner join mds.discenteMedio as d" +
					" inner join ts.serie as s" +
					" where ts.ativo = trueValue() " +
					" and ts.ano = :ano" +
					" and d.id = :idDiscente " +
					" and mds.situacaoMatriculaSerie.id = "+ SituacaoMatriculaSerie.MATRICULADO.getId());
			if ( serie != null )
				hql.append(" and s.id = :idSerie" );
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idDiscente", discenteMedio.getId());
			query.setInteger("ano", ano);
			if ( serie != null )
				query.setInteger("idSerie", serie.getId());
			
			return query.list();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Informa se o discente possui matrícula ativa em turmas séries diferente no mesmo ano,
	 * quando o aluno não for dependente.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public boolean existeMatriculaSerieDiferenteByAno(DiscenteMedio discenteMedio,
			Serie serie, int ano) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select new TurmaSerie(ts.id, ts.ano, ts.nome, ts.serie, " +
					"ts.capacidadeAluno, ts.ativo, ts.dataCadastro)" +
					" from MatriculaDiscenteSerie as mds" +
					" inner join mds.turmaSerie as ts" +
					" inner join mds.discenteMedio as d" +
					" inner join ts.serie as s" +
					" where ts.ativo = trueValue() " +
					" and s.id != :idSerie" +
					" and ts.ano = :ano" +
					" and d.id = :idDiscente" +
					" and mds.situacaoMatriculaSerie.id = :situacaoDiscente" +
					" and mds.dependencia = falseValue()");
					
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idDiscente", discenteMedio.getId());
			query.setInteger("idSerie", serie.getId());
			query.setInteger("ano", ano);
			query.setInteger("situacaoDiscente", SituacaoMatriculaSerie.MATRICULADO.getId());
			
			return query.list() != null && query.list().size() > 0;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retornar um coleção de TurmaSerie conforme os parâmetros informados
	 * 
	 * @param cursoMedio
	 * @param ano
	 * @param numeroSerie
	 * @param descricaoSerie
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<TurmaSerie> findBySerie(CursoMedio cursoMedio, Integer ano, Integer numeroSerie,
			String descricaoSerie, String turma) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		String projecao = " ts.id, ts.ano, ts.nome, ts.serie.numero, " +
				" ts.serie.descricao,ts.dependencia, ts.serie.cursoMedio.id, ts.serie.cursoMedio.nome," +
				" ts.serie.cursoMedio.codigo, ts.serie.cursoMedio.turno.sigla "; 
		
		hql.append(
				"select "+projecao+
				" from TurmaSerie as ts " +
				" inner join ts.serie as s " +
				" inner join ts.serie.cursoMedio as c " +
				" left join ts.serie.cursoMedio.turno as t " +
				" where ts.ativo = "+SQLDialect.TRUE);		
		
		if (isNotEmpty(cursoMedio))
			hql.append(" and c.id = "+cursoMedio.getId());
		
		if (ano != null && ano > 0)
			hql.append(" and ts.ano = "+ano);
		
		if (numeroSerie != null && numeroSerie > 0)
			hql.append(" and s.numero = "+numeroSerie);
		
		if (isNotEmpty(descricaoSerie))
			hql.append(" and s.descricao like '%"+descricaoSerie+"%'");
		
		if (isNotEmpty(turma))
			hql.append(" and ts.nome = '"+turma+"'");
		
		hql.append(" order by c.nome, ts.ano, s.numero, ts.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<TurmaSerie> lista = (List<TurmaSerie>) HibernateUtils.parseTo(q.list(), projecao, TurmaSerie.class, "ts");		
		return lista;		
	}	
	
	/**
	 * Retorna os participantes da turma informada
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<MatriculaDiscenteSerie> findParticipantesByTurma(int idTurma) throws DAOException {
		
		StringBuffer hql = new StringBuffer();
		String projecao = " mds.id, mds.discenteMedio.id, mds.discenteMedio.discente.matricula, " +
				" mds.discenteMedio.discente.pessoa.nome, mds.situacaoMatriculaSerie.id, mds.situacaoMatriculaSerie.descricao," +
				" mds.discenteMedio.discente.status "; 
		
		hql.append(
				"select "+projecao+
				" from MatriculaDiscenteSerie as mds " +
				" inner join mds.situacaoMatriculaSerie sm "+
				" inner join mds.discenteMedio dm " +
				" inner join mds.discenteMedio.discente d " +
				" inner join mds.discenteMedio.discente.pessoa as p " +
				" where d.status in "+UFRNUtils.gerarStringIn(new Integer[]{StatusDiscente.ATIVO, 
						StatusDiscente.ATIVO_DEPENDENCIA, StatusDiscente.TRANCADO, 
						StatusDiscente.CANCELADO, StatusDiscente.EXCLUIDO}));			
		
		if (idTurma > 0)
			hql.append(" and mds.turmaSerie.id = "+idTurma);
				
		hql.append(" order by p.nome ");
		
		Query q = getSession().createQuery(hql.toString());
		@SuppressWarnings("unchecked")
		List<MatriculaDiscenteSerie> lista = (List<MatriculaDiscenteSerie>) HibernateUtils.parseTo(q.list(), 
				projecao, MatriculaDiscenteSerie.class, "mds");		
		return lista;		
	}
	
	/**
	 * Retorna um mapa de docentes por disciplina, que compõe a turma Série.
	 * @param pendentesObrigatoriaCurriculo
	 * @param primeiraData
	 * @param ultimaData
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Set<DocenteTurma>> findDocentesByTurmaSerie(Collection<TurmaSerieAno> collection) throws DAOException {
		if (isEmpty(collection)) return null;
		
		Map<Integer, Set<DocenteTurma>> mapDocentes = new HashMap<Integer, Set<DocenteTurma>>();
		List<Integer> disciplinas = new ArrayList<Integer>();
		for (Iterator<TurmaSerieAno> it = collection.iterator(); it.hasNext(); ) {
			TurmaSerieAno tsa = it.next();
			disciplinas.add(tsa.getTurma().getId());
		}
	
		Set <DocenteTurma> docentes = CollectionUtils.toHashSet( getSession().createQuery(
				"select dt from DocenteTurma dt where dt.turma.id in "+UFRNUtils.gerarStringIn(disciplinas)).list() );
		
		for (DocenteTurma dt : docentes) {
			if (mapDocentes.get(dt.getTurma().getId()) == null)
				mapDocentes.put(dt.getTurma().getId(), (new HashSet<DocenteTurma>()) );
			mapDocentes.get(dt.getTurma().getId()).add( dt );
		}
		return mapDocentes;
	}
	
	/**
	 * Retorna a quantidade de alunos matriculados das turmas informadas
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Long findQtdeAlunosByTurma(TurmaSerie turma) throws DAOException {
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
		situacoes.addAll(SituacaoMatricula.getSituacoesAproveitadas());
		
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(	"select count(mds.id) as qtdMatriculados " +
						" from MatriculaDiscenteSerie as mds " +
						" where mds.situacaoMatriculaSerie.id in " + 
							gerarStringIn(SituacaoMatriculaSerie.getSituacoesMatriculadoOuConcluido()) +
						" and mds.turmaSerie.id = :turma "); 
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("turma", turma.getId());
			return (Long) q.uniqueResult();
		
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna a quantidade de alunos matriculados das turmas informadas
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Long> findQtdeAlunosByTurmas(List <TurmaSerie> turmas) throws DAOException {
		Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
		situacoes.addAll(SituacaoMatricula.getSituacoesAproveitadas());
		
		List<Integer> ids = new ArrayList<Integer>();
		for (Iterator<TurmaSerie> it = turmas.iterator(); it.hasNext(); ) {
			TurmaSerie t = it.next();
			ids.add(t.getId());
		}
		Map<Integer, Long> mapAlunosTurma = new HashMap<Integer, Long>();
		if( ids.size() > 0 ) {
			String projecao = "t.id, count(mds.id) as qtdMatriculados";
			StringBuffer hql = new StringBuffer();
			hql.append(
						"select "+projecao+
						" from MatriculaDiscenteSerie as mds " +
						" inner join mds.discenteMedio dm " +
						" inner join mds.discenteMedio.discente d " +
						" inner join mds.turmaSerie t " +
						" where mds.situacaoMatriculaSerie.id in " + 
						UFRNUtils.gerarStringIn(new Integer[]{SituacaoMatriculaSerie.MATRICULADO.getId(), SituacaoMatriculaSerie.REPROVADO.getId()})
						// +" and mds.dependencia is falseValue() "
						);
			
			hql.append(	" and t.id in " + UFRNUtils.gerarStringIn(ids));
			hql.append( " group by t.id");		
			Query q = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
			for (Object[] linha : resultado) {
				int a = 0;
				mapAlunosTurma.put((Integer) linha[a++], (Long) linha[a++]);
			}
		}	
		return mapAlunosTurma;		
	}
	
	/**
	 * Retorna a quantidade de alunos matriculados nas disciplinas das turmas informadas
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Long> findQtdeAlunosMatriculadosByDisciplina(List <TurmaSerie> turmas) throws DAOException {
		
		List<Integer> ids = new ArrayList<Integer>();
		for (Iterator<TurmaSerie> it = turmas.iterator(); it.hasNext(); ) {
			TurmaSerie t = it.next();
			for (TurmaSerieAno disciplina : t.getDisciplinas()) {
				ids.add(disciplina.getTurma().getId());
			}
		}
		Map<Integer, Long> mapAlunosTurma = new HashMap<Integer, Long>();
		if ( ids.size() > 0 ){
			String projecao = "t.id, count(mc.id) as qtdMatriculados";
			StringBuffer hql = new StringBuffer();
			hql.append(
						"select "+projecao+
						" from MatriculaComponente as mc " +
						" inner join mc.turma t " +
						" inner join mc.discente d " +
						" where mc.situacaoMatricula in " + 
						  gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) );
			hql.append(	" and t.id in " + UFRNUtils.gerarStringIn(ids));
			hql.append( " group by t.id");		
			Query q = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
			for (Object[] linha : resultado) {
				int a = 0;
				mapAlunosTurma.put((Integer) linha[a++], (Long) linha[a++]);
			}
		}
		return mapAlunosTurma;		
	}
	/**
	 * Retorna a quantidade de alunos matriculados nas disciplinas da turma informada
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Long> findQtdeAlunosMatriculadosByDisciplina(TurmaSerie turma) throws DAOException {
		
		List<Integer> ids = new ArrayList<Integer>();
		
		for (TurmaSerieAno disciplina : turma.getDisciplinas()) {
			ids.add(disciplina.getTurma().getId());
		}
		
		Map<Integer, Long> mapAlunosTurma = new HashMap<Integer, Long>();
		if ( ids.size() > 0 ){
			String projecao = "t.id, count(mc.id) as qtdMatriculados";
			StringBuffer hql = new StringBuffer();
			hql.append(
						"select "+projecao+
						" from MatriculaComponente as mc " +
						" inner join mc.turma t " +
						" inner join mc.discente d " +
						" where mc.situacaoMatricula in " + 
						  gerarStringIn(SituacaoMatricula.getSituacoesAtivas()) );
			hql.append(	" and t.id in " + UFRNUtils.gerarStringIn(ids));
			hql.append( " group by t.id");		
			Query q = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
			List<Object[]> resultado = q.list();
			for (Object[] linha : resultado) {
				int a = 0;
				mapAlunosTurma.put((Integer) linha[a++], (Long) linha[a++]);
			}
		}
		return mapAlunosTurma;		
	}
	/**
	 * Retornar a TurmaSerie pelo valor do identificador informados.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public TurmaSerie findAllDadosByID(int idTurmaSerie, boolean inativos) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(TurmaSerie.class);
			Criteria cSerie = c.createCriteria("serie");
			c.add(Restrictions.eq("id", idTurmaSerie));
			if (!inativos) {
				c.add(Restrictions.eq("ativo", true));
			}	
			cSerie.addOrder(Order.asc("cursoMedio"));
			cSerie.addOrder(Order.asc("numero"));
			c.addOrder(Order.asc("nome"));
			c.addOrder(Order.asc("ano"));
			TurmaSerie turmaSerie = (TurmaSerie) c.uniqueResult();
			
			if ( isNotEmpty(turmaSerie) ){
				turmaSerie.setQtdMatriculados(findQtdeAlunosByTurma(turmaSerie));
				List<TurmaSerie> lista = new ArrayList<TurmaSerie>(); lista.add(turmaSerie);
				Map<Integer, Long> mapMatriculadosDisciplina = findQtdeAlunosMatriculadosByDisciplina(lista);
				for (TurmaSerieAno tsa : turmaSerie.getDisciplinas()) {
					if ( mapMatriculadosDisciplina.get(tsa.getTurma().getId()) != null )
						tsa.getTurma().setQtdMatriculados(mapMatriculadosDisciplina.get(tsa.getTurma().getId()));
				}
				
			}
			
			return turmaSerie;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retornar a TurmaSerieAno da turma informada.
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public TurmaSerieAno findByTurma(Turma turma) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(TurmaSerieAno.class);
			c.add(Restrictions.eq("turma.id", turma.getId()));
			return (TurmaSerieAno) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	
	/**
	 * Verifica a existência de TurmaSerie ativa com mesmo nome para série e ano.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public boolean existeTurmaSerieMesmoNomeByAno(TurmaSerie turmaSerie) throws DAOException {
		try {
			
			Criteria c = getSession().createCriteria(TurmaSerie.class);
			c.add(Restrictions.ne("id", turmaSerie.getId()));
			c.add(Restrictions.eq("serie.id", turmaSerie.getSerie().getId()));
			c.add(Restrictions.eq("ativo", true));
			c.add(Restrictions.eq("ano", turmaSerie.getAno()));
			c.add(Restrictions.ilike("nome", turmaSerie.getNome()));
			c.setProjection(Projections.rowCount());
			
			return (Integer)c.uniqueResult() > 0;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}	
	
	/**
	 * Verifica a existência de TurmaDependencia ativa com mesmo nome para série e ano.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public boolean existeTurmaDependenciaMesmoCurriculoByAno(TurmaSerie turmaSerie) throws DAOException {
		try {
			
			Criteria c = getSession().createCriteria(TurmaSerie.class);
			c.add(Restrictions.ne("id", turmaSerie.getId()));
			c.add(Restrictions.eq("serie.id", turmaSerie.getSerie().getId()));
			c.add(Restrictions.eq("ativo", true));
			c.add(Restrictions.eq("ano", turmaSerie.getAno()));
			c.add(Restrictions.ilike("nome", turmaSerie.getNome()));
			c.add(Restrictions.eq("curriculo", turmaSerie.getCurriculo()));
			c.setProjection(Projections.rowCount());
			
			return (Integer)c.uniqueResult() > 0;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}	
	/**
	 * Retornar a lista de TurmaSerie por discente baseado na situação;
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<TurmaSerie> findByDiscente(DiscenteAdapter discente, boolean dependencia, Integer... situacaoMatriculaDiscente ) throws DAOException {
		try {
			String projecao = 	" ts.id, ts.ano, ts.nome, ts.serie, " +
								" ts.capacidadeAluno, ts.ativo, ts.dataCadastro ";
			
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select "+ projecao +
					" from MatriculaDiscenteSerie as mds" +
					" inner join mds.turmaSerie as ts" +
					" inner join mds.discenteMedio as d" +
					" inner join ts.serie as s" +
					" where ts.ativo = trueValue() " +
					" and d.id = " + discente.getId() +
					" and mds.dependencia = " + dependencia +
					" and mds.situacaoMatriculaSerie.id IN "+ UFRNUtils.gerarStringIn(situacaoMatriculaDiscente));
					
			Query query = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
		    List<TurmaSerie> lista = (List<TurmaSerie>) HibernateUtils.parseTo(query.list(), projecao, TurmaSerie.class, "ts");
	        return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retornar a lista de TurmaSerie por docente baseado na situação;
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<TurmaSerie> findByDocenteAno( Integer idDocente, Integer idDocenteExterno, int ano ) throws DAOException {
		try {
			String projecao = 	" ts.id, ts.ano, ts.nome, ts.serie, " +
								" ts.capacidadeAluno, ts.ativo, ts.dataCadastro ";
			
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select "+ projecao +
					" from TurmaSerieAno tsa, DocenteTurma dt " +
					" inner join tsa.turmaSerie ts " +
					" inner join tsa.turma t " +
					" where dt.turma.id = t.id " +
					" and dt.");
					if (idDocente != null)
						hql.append("docente.id = " + idDocente);
					else if (idDocenteExterno != null)
						hql.append("docenteExterno.id = " + idDocenteExterno);
					else
						return null;
					
			Query q = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
		    List<TurmaSerie> lista = (List<TurmaSerie>) HibernateUtils.parseTo(q.list(), projecao, TurmaSerie.class, "ts");
	        return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Verifica a existência de TurmaSerie ativa com mesmo nome para série e ano.
	 * 
	 * @param nome
	 * @param codigo
	 * @param curso
	 * @return
	 * @throws DAOException
	 */
	public boolean existeDisciplinaBySituacao(TurmaSerie turmaSerie, Integer situacoesTurma) throws DAOException {
		try {
			Criteria tsa = getSession().createCriteria(TurmaSerieAno.class);
			Criteria ts = tsa.createCriteria("turmaSerie");
			Criteria t = tsa.createCriteria("turma");
			ts.add(Restrictions.eq("id", turmaSerie.getId()));
			ts.add(Restrictions.eq("ativo", true));
			ts.add(Restrictions.eq("ano", turmaSerie.getAno()));
			t.add(Restrictions.eq("situacaoTurma.id", situacoesTurma));
			tsa.setProjection(Projections.rowCount());
			
			return (Integer)tsa.uniqueResult() > 0;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Método que retorna os ids dos alunos que são repetentes de uma determinada série.
	 * @param serie
	 * @return
	 */
	public Collection<Integer> findIdsRepetentesBySerie(TurmaSerie serie) throws DAOException {
		String sql =  "SELECT d.id_matricula_discente_serie FROM medio.matricula_discente_serie d " +
					  "JOIN medio.turma_serie ts using (id_turma_serie) " +
					  "WHERE id_turma_serie = :idTurmaSerie " +
					  "AND ano = :anoAtual " +
					  "AND EXISTS ( " +
							  "SELECT * FROM medio.matricula_discente_serie dreprv " +
							  "JOIN medio.turma_serie tsreprov using (id_turma_serie) " +
							  "WHERE tsreprov.id_turma_serie != ts.id_turma_serie " +
							  "AND d.id_discente = dreprv.id_discente " +
							  "AND tsreprov.id_serie = ts.id_serie " +
							  "AND ano = :anoAnterior ) ";
		
		Query q = getSession().createSQLQuery(sql);
		
		q.setInteger("idTurmaSerie", serie.getId());
		q.setInteger("anoAtual", serie.getAno());
		q.setInteger("anoAnterior", serie.getAno()-1);
		@SuppressWarnings("unchecked")
		List<Integer> resul = q.list(); 
		return resul;
	}
	
}
