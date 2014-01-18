/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '05/02/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Restrictions.eq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.OptativaCurriculoSemestre;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 * Dao para realizar consultas de estruturas curriculares de graduação
 * @author Leonardo
 */
public class EstruturaCurricularDao extends GenericSigaaDAO {

	/**
	 * Traz o currículo podendo informar diversos parâmetros como filtro
	 * 
	 * @param idCurso
	 * @param idMatriz
	 * @param idUnidade
	 * @param codigo
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<Curriculo> findCompleto(Integer idCurso, Integer idMatriz, Integer idUnidade, String codigo) throws DAOException, LimiteResultadosException {
		try {
			StringBuilder hql = new StringBuilder();
			String projecao = " select c.id, c.codigo, c.chOptativasMinima, c.chNaoAtividadeObrigatoria, " +
				"c.chAtividadeObrigatoria, c.anoEntradaVigor, c.periodoEntradaVigor, c.ativo , " +
				"curso.nome, curso.modalidadeEducacao.descricao, curso.nivel, habilitacao.nome, turno.sigla, grau.descricao ";
			String count = " select count(c.id) ";
			hql.append( " from Curriculo c " +
						" join c.curso as curso " +
						" join curso.modalidadeEducacao as modalidadeEducacao " +
						" join curso.unidade as unidade " +
						" left join c.matriz as matriz " +
						" left join matriz.habilitacao as habilitacao " +
						" left join matriz.turno as turno " +
						" left join matriz.grauAcademico as grau " +
						" left join matriz.enfase as enfase");
			hql.append(" where 1=1 ");
			if(codigo != null)
				hql.append(" and c.codigo = '"+ codigo +"'");
			if(idCurso != null)
				hql.append(" and c.curso.id = "+ idCurso );
			if(idMatriz != null)
				hql.append(" and c.matriz.id = "+ idMatriz );
			if (idUnidade != null)
				hql.append(" and c.curso.unidade.id = "+ idUnidade );
			String orderBy = " order by c.anoEntradaVigor desc, c.periodoEntradaVigor, c.codigo desc ";

			Query q = getSession().createQuery(count + hql);
			Long qtd = (Long) q.uniqueResult();
			if (qtd > 50)
				throw new LimiteResultadosException();

			q = getSession().createQuery(projecao + hql + orderBy);
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			ArrayList<Curriculo> result = new ArrayList<Curriculo>();

			for(int i = 0; i < lista.size(); i++){
				int col = 0;
				Object[] colunas = lista.get(i);

				Curriculo curriculo = new Curriculo();
				curriculo.setId((Integer) colunas[col++]);
				curriculo.setCodigo((String) colunas[col++]);
				curriculo.setChOptativasMinima((Integer) colunas[col++] );
				curriculo.setChNaoAtividadeObrigatoria((Integer) colunas[col++] );
				curriculo.setChAtividadeObrigatoria((Integer) colunas[col++] );
				curriculo.setAnoEntradaVigor((Integer) colunas[col++]);
				curriculo.setPeriodoEntradaVigor((Integer) colunas[col++]);
				curriculo.setAtivo((Boolean) colunas[col++]);

				MatrizCurricular matriz = new MatrizCurricular();

				Curso curso = new Curso();
				curso.setNome((String) colunas[col++]);
				curso.setModalidadeEducacao(new ModalidadeEducacao());
				curso.getModalidadeEducacao().setDescricao((String) colunas[col++]);
				curso.setNivel((Character) colunas[col++]);
				curriculo.setCurso(curso);
				matriz.setCurso(curso);

				Habilitacao habilitacao = new Habilitacao();
				habilitacao.setNome((String) colunas[col++]);
				matriz.setHabilitacao(habilitacao);

				Turno turno = new Turno();
				turno.setSigla((String) colunas[col++]);
				matriz.setTurno(turno);

				GrauAcademico modalidade = new GrauAcademico();
				modalidade.setDescricao((String) colunas[col++]);
				matriz.setGrauAcademico(modalidade);

				curriculo.setMatriz(matriz);

				result.add(curriculo);
			}
			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	
	

	/**
	 * Traz o currículo podendo informar diversos parâmetros como filtro
	 * 
	 * @param idCurso
	 * @param idMatriz
	 * @param idUnidade
	 * @param codigo
	 * @param somenteAtivo 
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<Curriculo> findCompletoAtivo(Integer idCurso, Integer idMatriz, Integer idUnidade, String codigo, boolean somenteAtivo) throws DAOException, LimiteResultadosException {
		try {
			StringBuilder hql = new StringBuilder();
			String projecao = " select c.id, c.codigo, c.chOptativasMinima, c.chNaoAtividadeObrigatoria, " +
					"c.chAtividadeObrigatoria, c.anoEntradaVigor, c.periodoEntradaVigor , c.ativo , " +
					"curso.nome, curso.modalidadeEducacao.descricao, curso.nivel, unidade.nome, habilitacao.nome, turno.sigla, grau.descricao, enfase.id, enfase.nome ";
			String count = " select count(c.id) ";
			hql.append( " from Curriculo c " +
						" join c.curso as curso " +
						" join curso.unidade as unidade " +
						" left join curso.modalidadeEducacao as modalidadeEducacao " +
						" left join c.matriz as matriz " +
						" left join matriz.habilitacao as habilitacao " +
						" left join matriz.turno as turno " +
						" left join matriz.grauAcademico as grau " +
						" left join matriz.enfase as enfase");
			hql.append(" where 1=1 ");
			if(codigo != null)
				hql.append(" and c.codigo = '"+ codigo +"'");
			if(idCurso != null)
				hql.append(" and c.curso.id = "+ idCurso );
			if(idMatriz != null)
				hql.append(" and c.matriz.id = "+ idMatriz );
			if (idUnidade != null)
				hql.append(" and c.curso.unidade.id = "+ idUnidade );
			if (somenteAtivo == true)
				hql.append(" and (c.ativo is null or c.ativo = trueValue())");
			String orderBy = " order by curso.nivel, c.anoEntradaVigor desc, c.periodoEntradaVigor, c.codigo desc ";

			Query q = getSession().createQuery(count + hql);
			Long qtd = (Long) q.uniqueResult();
			if (qtd > 50)
				throw new LimiteResultadosException();

			q = getSession().createQuery(projecao + hql + orderBy);
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			ArrayList<Curriculo> result = new ArrayList<Curriculo>();

			for(int i = 0; i < lista.size(); i++){
				int col = 0;
				Object[] colunas = lista.get(i);

				Curriculo curriculo = new Curriculo();
				curriculo.setId((Integer) colunas[col++]);
				curriculo.setCodigo((String) colunas[col++]);
				curriculo.setChOptativasMinima((Integer) colunas[col++] );
				curriculo.setChNaoAtividadeObrigatoria((Integer) colunas[col++] );
				curriculo.setChAtividadeObrigatoria((Integer) colunas[col++] );
				curriculo.setAnoEntradaVigor((Integer) colunas[col++]);
				curriculo.setPeriodoEntradaVigor((Integer) colunas[col++]);
				curriculo.setAtivo((Boolean) colunas[col++]);

				MatrizCurricular matriz = new MatrizCurricular();

				Curso curso = new Curso();
				curso.setNome((String) colunas[col++]);
				curso.setModalidadeEducacao(new ModalidadeEducacao());
				curso.getModalidadeEducacao().setDescricao((String) colunas[col++]);
				curso.setNivel((Character) colunas[col++]);
				
				Unidade unidade = new Unidade();
				unidade.setNome((String) colunas[col++]);
				curso.setUnidade(unidade);
				
				curriculo.setCurso(curso);
				matriz.setCurso(curso);

				Habilitacao habilitacao = new Habilitacao();
				habilitacao.setNome((String) colunas[col++]);
				matriz.setHabilitacao(habilitacao);

				Turno turno = new Turno();
				turno.setSigla((String) colunas[col++]);
				matriz.setTurno(turno);

				GrauAcademico modalidade = new GrauAcademico();
				modalidade.setDescricao((String) colunas[col++]);
				matriz.setGrauAcademico(modalidade);
				
				curriculo.setMatriz(matriz);
				
				Integer idEnfase = (Integer) colunas[col++];
				if (idEnfase != null) {
					Enfase enfase = new Enfase();
					enfase.setId(idEnfase);
					enfase.setNome((String) colunas[col++]);
					matriz.setEnfase(enfase);
				}
				
				result.add(curriculo);
			}
			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}	
	
	
	
	/**
	 * Retorna uma lista currículos associados a um curso.
	 * 
	 * @param idCurso 
	 * @param nivel
	 * @param somenteAtivos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curriculo> findByCurso(int idCurso, char nivel, Boolean somenteAtivos) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Curriculo.class);
			c.addOrder(Order.desc("matriz.id"));
			c.addOrder(Order.desc("anoEntradaVigor"));
			c.addOrder(Order.desc("periodoEntradaVigor"));
			Criteria subC = null;
			if (nivel == NivelEnsino.GRADUACAO) {
				subC = c.createCriteria("matriz");
				subC.add( Restrictions.eq("ativo", Boolean.TRUE) );
				subC.add( Restrictions.eq("curso", new Curso(idCurso)) );
			} else {
				if (somenteAtivos != null && somenteAtivos) {
					// se for true ou null (traz somente currículos ativos)
					Criterion isTrue = Restrictions.eq("ativo", Boolean.TRUE);
					Criterion isNull = Restrictions.isNull("ativo");				
					LogicalExpression orExp = Restrictions.or(isTrue,isNull);
					
					c.add(orExp);
				}
				subC = c.createCriteria("curso");
				subC.add( Restrictions.eq("id", idCurso) );
				subC.add( Restrictions.eq("ativo", Boolean.TRUE) );
			}

			@SuppressWarnings("unchecked")
			Collection<Curriculo> lista = subC.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 *  Retorna os currículos de um curso com base no nível de ensino
	 *  
	 * @param idCurso
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curriculo> findByCurso(int idCurso, char nivel) throws DAOException {
		return findByCurso(idCurso, nivel, null);
	}

	/**
	 *  Retorna os currículos de uma matriz
	 * 
	 * @param idMatriz
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curriculo> findByMatriz(int idMatriz) throws DAOException {
		return findByMatriz(idMatriz, true);
	}
	
	/**
	 *  Retorna os currículos de uma matriz
	 * 
	 * @param idMatriz
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curriculo> findByMatriz(int idMatriz, Boolean ativo) throws DAOException {

		try {
			Criteria c = getSession().createCriteria(Curriculo.class);
			c.add(Restrictions.eq("matriz", new MatrizCurricular(idMatriz)));
			Criteria cMatriz = c.createCriteria("matriz");
			if (ativo != null)
				cMatriz.add( Restrictions.eq("ativo", ativo) );

			@SuppressWarnings("unchecked")
			Collection<Curriculo> lista =  c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna o currículo mais recente da matriz indicada
	 * @param idMatriz
	 * @return
	 * @throws DAOException
	 */
	public Curriculo findMaisRecenteByMatriz(int idMatriz) throws DAOException {

		try {
			Criteria c = buscaMaisRecente(idMatriz);
			return (Curriculo) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna o currículo mais recente da matriz indicada, apenas com dados básicos
	 * @param idMatriz
	 * @return
	 * @throws DAOException
	 */
	public Curriculo findMaisRecenteByMatrizLeve(int idMatriz) throws DAOException {

		try {
			Criteria c = buscaMaisRecente(idMatriz);
			c.setProjection( Projections.projectionList()
	        .add( Projections.property("id") )
	        .add( Projections.property("semestreConclusaoMaximo") ));
			
			Object[] result = (Object[]) c.uniqueResult();
			
			Curriculo curriculo = null;
			if (!isEmpty(result)) {
				curriculo = new Curriculo((Integer) result[0]);
				curriculo.setSemestreConclusaoMaximo( (Integer) result[1] );
			}
			
			return curriculo;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna o currículo mais recente da matriz indicada, apenas com dados básicos
	 * @param idMatriz
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Curriculo> findMaisRecentesByMatrizesLeve(Collection<Integer> idMatrizes) throws DAOException {
		Map<Integer, Curriculo> mapa = new TreeMap<Integer, Curriculo>();
		if(idMatrizes != null && idMatrizes.size() > 0){
			Criteria c = getSession().createCriteria(Curriculo.class);
			c.createCriteria("matriz").add(Restrictions.in("id", idMatrizes))
			.add( Restrictions.eq("ativo", Boolean.TRUE) );
			
			c.addOrder( Order.desc("anoEntradaVigor") );
			c.addOrder( Order.desc("periodoEntradaVigor") );
			c.addOrder( Order.desc("codigo") );
			
			c.setProjection( Projections.projectionList()
					.add( Projections.property("id") )
					.add( Projections.property("semestreConclusaoMaximo") )
					.add (Projections.property("matriz.id")));
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = c.list();
			
			Curriculo curriculo = null;
			if (!isEmpty(result)) {
				for (Object[] obj : result) {
					if (!mapa.containsKey(obj[2])) {
						curriculo = new Curriculo((Integer) obj[0]);
						curriculo.setSemestreConclusaoMaximo( (Integer) obj[1] );
						mapa.put((Integer) obj[2], curriculo); 
					}
				}
			}
		}
		return mapa;
	}


	/** Cria um {@link Criteria} que retorna a matriz curricular mais recente. 
	 * @param idMatriz
	 * @return
	 * @throws DAOException
	 */
	private Criteria buscaMaisRecente(int idMatriz) throws DAOException {
		Criteria c = getSession().createCriteria(Curriculo.class);
		c.add(Restrictions.eq("matriz", new MatrizCurricular(idMatriz)));
		Criteria cMatriz = c.createCriteria("matriz");
		cMatriz.add( Restrictions.eq("ativo", Boolean.TRUE) );

		c.addOrder( Order.desc("anoEntradaVigor") );
		c.addOrder( Order.desc("periodoEntradaVigor") );
		c.addOrder( Order.desc("codigo") );

		c.setMaxResults(1);
		return c;
	}	

	/**
	 * Retorna o currículo mais recente do curso indicado
	 * @param idMatriz
	 * @return
	 * @throws DAOException
	 */
	public Curriculo findMaisRecenteByCurso(int idCurso) throws DAOException {

		try {
			Criteria c = getSession().createCriteria(Curriculo.class);
			c.add(Restrictions.eq("curso", new Curso(idCurso)));
			Criteria cCurso = c.createCriteria("curso");
			cCurso.add( Restrictions.eq("ativo", Boolean.TRUE) );

			c.addOrder( Order.desc("anoEntradaVigor") );
			c.addOrder( Order.desc("periodoEntradaVigor") );
			c.addOrder( Order.desc("codigo") );

			c.setMaxResults(1);

			return (Curriculo) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 *  Retorna os currículos procurando pelo código e nível
	 *  
	 * @param codigo
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curriculo> findByCodigo(String codigo, char nivel) throws DAOException {

		try {
			Criteria c = getSession().createCriteria(Curriculo.class);
			c.add(Restrictions.eq("codigo", codigo));
			if (nivel == NivelEnsino.GRADUACAO) {
				c.add(Restrictions.isNull("curso"));
				c.add(Restrictions.isNotNull("matriz"));
				Criteria cMatriz = c.createCriteria("matriz");
				cMatriz.add( Restrictions.eq("ativo", Boolean.TRUE) );
			} else if (nivel == NivelEnsino.STRICTO) {
				c.add(Restrictions.isNull("matriz"));
				c.add(Restrictions.isNotNull("curso"));
				Criteria subC = c.createCriteria("curso");
				subC.add( Restrictions.eq("ativo", Boolean.TRUE) );
			}

			@SuppressWarnings("unchecked")
			Collection<Curriculo> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todos os currículos ativos de um nível de ensino
	 * 
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curriculo> findAll(char nivel) throws DAOException {

		try {
			Criteria c = getSession().createCriteria(Curriculo.class);
			c.add(Restrictions.eq("ativo", Boolean.TRUE));
			
			if (nivel == NivelEnsino.GRADUACAO) {
				c.add(Restrictions.isNotNull("curso"));
				c.add(Restrictions.isNotNull("matriz"));
				Criteria cMatriz = c.createCriteria("matriz");
				cMatriz.add( Restrictions.eq("ativo", Boolean.TRUE) );
			} else if (nivel == NivelEnsino.STRICTO) {
				c.add(Restrictions.isNull("matriz"));
				c.add(Restrictions.isNotNull("curso"));
				Criteria subC = c.createCriteria("curso");
				subC.add( Restrictions.eq("ativo", Boolean.TRUE) );
			}

			c.addOrder(Order.asc("anoEntradaVigor"));
			c.addOrder(Order.asc("periodoEntradaVigor"));
			
			@SuppressWarnings("unchecked")
			Collection<Curriculo> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Verifica se já existe algum currículo cadastrado usando o código
	 * 
	 * @param curriculo
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public boolean existeCodigo(Curriculo curriculo, char nivel) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Curriculo.class).setProjection(Projections.rowCount());
			c.add(Restrictions.ne("id", curriculo.getId()));
			c.add(Restrictions.eq("codigo", curriculo.getCodigo()));
			c.add(Restrictions.eq("curso.id", curriculo.getCurso().getId()));
			if (nivel == NivelEnsino.GRADUACAO) {
				c.add(Restrictions.eq("matriz", curriculo.getMatriz()));
				Criteria cMatriz = c.createCriteria("matriz");
				cMatriz.add( Restrictions.eq("ativo", Boolean.TRUE) );
			} else if (nivel == NivelEnsino.STRICTO || nivel == NivelEnsino.RESIDENCIA) {
				c.add(Restrictions.eq("curso", curriculo.getCurso()));
				Criteria subC = c.createCriteria("curso");
				subC.add( Restrictions.eq("ativo", Boolean.TRUE) );
			}
			Integer res = (Integer) c.uniqueResult();
			return res != null && res > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna os componentes curriculares de um currículo.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesByCurriculo(int id) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select new ComponenteCurricular(componente.id, componente.codigo, componente.detalhes.nome, componente.detalhes.chTotal, componente.nivel) " +
					"from CurriculoComponente where curriculo.id=:c");
			hql.append(" order by componente.detalhes.nome asc");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("c", id);
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna os currículos componentes de um currículo.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<CurriculoComponente> findCurriculoComponentesByCurriculo(int id) throws DAOException {
		try {
			Query q = getSession().createQuery(
					"SELECT cc "
					+ " FROM CurriculoComponente cc "
					+ " LEFT JOIN cc.areaConcentracao areaConcentracao "
					+ " INNER JOIN FETCH cc.componente disciplina "
					+ " WHERE cc.curriculo.id = " + id
					+ " ORDER BY areaConcentracao.denominacao, cc.semestreOferta, cc.obrigatoria, disciplina.codigo");
			@SuppressWarnings("unchecked")
			Collection<CurriculoComponente> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna todos os currículos componentes obrigatórios de um currículo
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<CurriculoComponente> findCurriculoComponentesObrigatoriosByCurriculo(int id) throws DAOException {
		try {
			Query q = getSession().createQuery(
					"SELECT new CurriculoComponente( cc.id, cc.semestreOferta , cc.obrigatoria,"
					+ " cc.curriculo.id, cc.componente.id, cc.componente.codigo, cc.componente.detalhes.nome, "
					+ " cc.componente.detalhes.chTotal, area.id, area.denominacao, cc.componente.detalhes.equivalencia ) "
					+ " FROM CurriculoComponente cc LEFT JOIN cc.areaConcentracao area "
					+ " WHERE cc.obrigatoria = trueValue() and cc.curriculo.id = " + id
					+ " ORDER BY cc.semestreOferta, cc.obrigatoria, cc.componente.codigo");
			@SuppressWarnings("unchecked")
			Collection<CurriculoComponente> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Verifica se um curso tem um componente curricular
	 * 
	 * @param curso
	 * @param componente
	 * @return
	 * @throws DAOException
	 */
	public boolean containsComponenteByCurso(int curso, int componente) throws DAOException {
		try {
			Query q = getSession().createQuery("select id from CurriculoComponente where " +
					"curriculo.curso.id=:curso and componente.id=:componente and curriculo.ativo is true");
			q.setInteger("curso", curso);
			q.setInteger("componente", componente);
			@SuppressWarnings("unchecked")
			List<Object> l = q.list();
			return l != null && !l.isEmpty();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}

	/**
	 * Verifica se existe alguma atividade no currículo pertencente a um
	 * determinado tipo de atividade
	 *
	 * @param curriculo
	 * @param tipoAtividade
	 * @return
	 * @throws DAOException
	 */
	public boolean containsAtividade(Curriculo curriculo, TipoAtividade tipoAtividade) throws DAOException {
		try {
			Query q = getSession().createQuery("select id from CurriculoComponente " +
					" where curriculo.id = :curriculo " +
					" and componente.tipoAtividade.id = :tipoAtividade");
			q.setInteger("curriculo", curriculo.getId());
			q.setInteger("tipoAtividade", tipoAtividade.getId() );
			@SuppressWarnings("unchecked")
			List<Object> l = q.list();
			return l != null && !l.isEmpty();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna a quantidade de alunos de um currículo
	 * @param idCurriculo
	 * @return
	 * @throws DAOException
	 * @throws
	 */
	public int countDiscentesByCurriculo(int idCurriculo) throws DAOException{
			StringBuilder hql = new StringBuilder();
			hql.append( " SELECT COUNT(*) FROM Discente d " );
			hql.append( " WHERE d.curriculo.id = :idCurriculo " );

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idCurriculo", idCurriculo );

			return ((Long) q.uniqueResult()).intValue();
	}

	/**
	 * Retorna um currículo com os atributos crObrigatoriosTeoricos, crObrigatoriosPraticos, chObrigatorios populados
	 * ou seja, a carga horária obrigatória prática e teórica e os créditos obrigatórios práticos e teóricos
	 * @param idCurriculo
	 * @return
	 * @throws DAOException
	 */
	public Curriculo countTotalCrChCurriculo(int idCurriculo) throws DAOException{

		String hql = "SELECT SUM(cc.componente.detalhes.chAula) as ch_teorico, "
				+ " SUM(cc.componente.detalhes.chLaboratorio + cc.componente.detalhes.chEstagio + cc.componente.detalhes.chEad) as ch_pratica, "
				+ " SUM(cc.componente.detalhes.crAula) as cr_teorico, "
				+ " SUM(cc.componente.detalhes.crLaboratorio + cc.componente.detalhes.crEstagio + cc.componente.detalhes.crEad) as cr_pratico "
				+ " FROM CurriculoComponente cc "
				+ " WHERE cc.curriculo.id = :idCurriculo "
				+ " AND cc.obrigatoria = trueValue() ";

		Query q = getSession().createQuery( hql );
		q.setInteger("idCurriculo", idCurriculo);

		Object[] array = (Object[]) q.uniqueResult();
		Curriculo curriculo = null;
		if( array != null ){
			Long chTeoricos = (Long) array[0];
			Long chPraticos = (Long) array[1];
			Long crTeoricos = (Long) array[2];
			Long crPraticos = (Long) array[3];
			curriculo = new Curriculo();

			curriculo.setChTeoricos( chTeoricos == null ? 0 : chTeoricos.intValue() );
			curriculo.setChPraticos( chPraticos == null ? 0 : chPraticos.intValue() );
			curriculo.setCrTeoricos( crTeoricos == null ? 0 : crTeoricos.intValue() );
			curriculo.setCrPraticos( crPraticos == null ? 0 : crPraticos.intValue() );
		}

		// Contabilizando ch de atividades acadêmicas específicas obrigatórias
		String hqlAAE = "SELECT SUM(cc.componente.detalhes.chAula + cc.componente.detalhes.chLaboratorio + cc.componente.detalhes.chEstagio) as ch_aae "
			+ " FROM CurriculoComponente cc where cc.obrigatoria = trueValue()"
			+ " AND cc.curriculo.id = :idCurriculo "
			+ " AND cc.componente.tipoComponente.id in " + gerarStringIn(TipoComponenteCurricular.getAtividades());

		Query qAAE = getSession().createQuery( hqlAAE.toString() );
		qAAE.setInteger("idCurriculo", idCurriculo);

		Long chAAE = (Long) qAAE.uniqueResult();
		if( curriculo != null && chAAE != null )
			curriculo.setChAAE( chAAE.intValue() );

		return curriculo;

	}

	/**
	 * Retorna as optativas de um currículo
	 * 
	 * @param obj
	 * @return
	 * @throws DAOException
	 */
	public List<OptativaCurriculoSemestre> findOptativasCurriculoSemestreByCurriculo(Curriculo obj) throws DAOException {
		Criteria c = getSession().createCriteria(OptativaCurriculoSemestre.class);
		c.add(eq("curriculo", obj)).addOrder(asc("semestre"));
		@SuppressWarnings("unchecked")
		List<OptativaCurriculoSemestre> lista = c.list();
		return lista;
	}

	/**
	 * Retorna todos os ids dos currículos ativos que contém o componente informado
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<Integer> findAtivosByComponente(int id) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT DISTINCT c.id FROM CurriculoComponente cc JOIN cc.curriculo c WHERE cc.componente.id = :idComponente ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idComponente", id);
		@SuppressWarnings("unchecked")
		Collection<Integer> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna a quantidade de discente no currículo.
	 * 
	 * @param idCurriculo
	 * @return
	 * @throws DAOException
	 */
	public long findQuantidadeDiscentesByCurriculo(int idCurriculo) throws DAOException {
		
		String hql = "select count(d.id) from Discente d where d.curriculo = " + idCurriculo + " and d.status in " + gerarStringIn(StatusDiscente.getAtivos());
		
		return (Long) getSession().createQuery(hql).uniqueResult();
		
	}
	
	/**
	 * Retorna a quantidade de semestres que possuem componentes cadastrados
	 * 
	 * @param c
	 * @return
	 * @throws DAOException
	 */
	public int findMaxSemestreOferta(Curriculo c) throws DAOException {
		String hql = "select max(cc.semestreOferta) from CurriculoComponente cc where cc.curriculo.id = :curriculo";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("curriculo", c.getId());
		
		return (q.uniqueResult() == null ? 0 : (Integer) q.uniqueResult());		
	}
	
	/**
	 * Retorna as informações do curriculo especificado.
	 * <br>
	 * Usa projeção.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Curriculo findDetalhesByCurriculo(int id) throws DAOException {
		
		String projecao = " c.id, c.maxEletivos, cc.id, cc.componente.id, cc.componente.detalhes.nome, c.curso.id, c.curso.nome, c.curso.nivel ";
		String hql = " select " + projecao + " from Curriculo c " +
					 " left join c.curriculoComponentes as cc where c.id = :id";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("id", id);
		
		List<Object> lista = q.list();
		
		List<Curriculo> resultado = new ArrayList<Curriculo>();
		
		for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
			
			int indice = 0;
			Object[] colunas = (Object[]) iterator.next();

			Curriculo curriculo = new Curriculo();
			curriculo.setCurriculoComponentes(new ArrayList<CurriculoComponente>());
			
			curriculo.setId((Integer) colunas[indice++]);
			curriculo.setMaxEletivos((Integer) colunas[indice++]);
			
			if (resultado.contains(curriculo))
				curriculo = resultado.get(resultado.indexOf(curriculo));
			else
				resultado.add(curriculo);
			
			Integer idCC = (Integer) colunas[indice++];
			
			if (!isEmpty(idCC)) {
				CurriculoComponente cc = new CurriculoComponente();
				cc.setId(idCC);
				
				cc.setComponente(new ComponenteCurricular());
				cc.getComponente().setId((Integer) colunas[indice++]);
				cc.getComponente().setDetalhes(new ComponenteDetalhes());
				cc.getComponente().getDetalhes().setNome((String) colunas[indice++]);
				
				curriculo.getCurriculoComponentes().add(cc);
			}
			
			Integer idCurso = (Integer) colunas[indice++];
			
			if (!isEmpty(idCurso)) {
				Curso curso = new Curso(idCurso);
				curso.setNome((String) colunas[indice++]);
				curso.setNivel((Character) colunas[indice++]);
				
				curriculo.setCurso(curso);
			}
			
		}
		
		if (isEmpty(resultado))
			return null;
		
		return resultado.iterator().next();
		
	}

	/**
	 * Retorna os curriculos que possuem o componente passado no parametro
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curriculo> findByComponente(int id) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT DISTINCT c FROM CurriculoComponente cc JOIN cc.curriculo c WHERE cc.componente.id = :idComponente ");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idComponente", id);
		return q.list();
	}
	
	/**
	 * Retorna todos os currículos componentes obrigatórios de um currículo
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<CurriculoComponente> findCurriculoComponentesObrigatoriosAreaConcentracaoByDiscente(DiscenteStricto discente) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			
			hql.append("SELECT new CurriculoComponente( cc.id, cc.semestreOferta , cc.obrigatoria,"
					+ " cc.curriculo.id, cc.componente.id, cc.componente.codigo, cc.componente.detalhes.nome, "
					+ " cc.componente.detalhes.chTotal, area.id, area.denominacao, cc.componente.detalhes.equivalencia )");
			hql.append(" FROM CurriculoComponente cc " 
					+ " LEFT JOIN cc.areaConcentracao area "
					+ " WHERE cc.obrigatoria = trueValue() " 
					+ " AND cc.curriculo.id = :curriculo ");
			if (ValidatorUtil.isNotEmpty(discente.getArea()))
				hql.append(" AND (cc.areaConcentracao = null OR cc.areaConcentracao.id = :areaConcentracao)");
			else 
				hql.append(" AND cc.areaConcentracao = null ");
			hql.append(" ORDER BY cc.semestreOferta, cc.obrigatoria, cc.componente.codigo");
		
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("curriculo", discente.getCurriculo().getId());
			if (ValidatorUtil.isNotEmpty(discente.getArea()))
				q.setInteger("areaConcentracao", discente.getArea().getId());
			
			@SuppressWarnings("unchecked")
			Collection<CurriculoComponente> lista = q.list();
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	public Collection<ComponenteCurricular> findComponentesObrigatoriosByCurriculo(int idCurriculo) throws DAOException {
		String hql = "SELECT c FROM CurriculoComponente cc JOIN cc.componente c WHERE cc.curriculo.id = " + idCurriculo;
		return getSession().createQuery(hql).list();
	}
	
}
