/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.CargoAcademico;
import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.CategoriaUnidade;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.rh.dominio.Servidor;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.relatorios.jsf.RelatorioReprovacoesDisciplinas;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/** DAO responsável por consultas específicas às Unidades
 * @author André M Dantas
 *
 */
public class UnidadeDao extends GenericSigaaDAO {

	/**
	 * <p>Encontra todas as unidades ativas pelo nome.</p>
	 * <p>Faz uma projeção apenas no id da unidade, codigo e nome </p>
	 * 
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Unidade> findAtivasByNome(String nomeUnidade) throws DAOException {
		
		List<Unidade> retorno = new ArrayList<Unidade>();
		
		try {
			StringBuilder sql = new StringBuilder("select id_unidade, codigo_unidade, nome, hierarquia, hierarquia_organizacional from comum.unidade where  ");
			sql.append(" nome_ascii like :nomeUnidade " );
			sql.append(" AND ativo = trueValue() order by nome " );

			
			Query q = getSession().createSQLQuery(sql.toString());
			q.setString("nomeUnidade", "%"+StringUtils.toAsciiAndUpperCase(nomeUnidade)+"%");
			
			@SuppressWarnings("unchecked")
			List<Object> lista = q.list();
		
			
			for (Object object : lista) {
				Object[] temp = (Object[]) object;
				Unidade u = new Unidade((Integer) temp[0], ((BigInteger) temp[1]).longValue() , (String) temp[2], null);
				u.setHierarquia((String) temp[3]);
				u.setHierarquiaOrganizacional((String) temp[4]);
				retorno.add(u);
			}
			
			return retorno;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/**
	 * Retorna todas as subunidades da unidade pai informada e do tipoAcademico informado
	 * @param pai
	 * @param tipoAcademico
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findBySubUnidades(UnidadeGeral pai, int... tipoAcademico)
			throws DAOException {
		try {
			String hql = "select new Unidade(u.id, u.codigo, u.nome, u.sigla,"
					+ " u.nomeCapa, u.hierarquia) from Unidade u where u.unidadeResponsavel"
					+ " = :ID_UNIDADE_PAI and u.tipoAcademica IN "+gerarStringIn(tipoAcademico)
					+ " ORDER BY u.nomeCapa";

			Query q = getSession().createQuery(hql);
			q.setInteger("ID_UNIDADE_PAI", pai.getId());
			
			@SuppressWarnings("unchecked")
			Collection<UnidadeGeral> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/** Retorna uma coleção de Unidades de acordo com nome.
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findByNome(String nome) throws DAOException {
		return findByNome(nome, -1, -1);
	}

	/** Retorna uma coleção de unidades por tipo.
	 * @param nome
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findByNomeTipo(String nome, int tipo)
			throws DAOException {
		return findByNome(nome, -1, tipo);
	}
	
	/**
	 * Retorna uma coleção de Unidades de acordo com nome.
	 * 
	 * @param nome
	 * @param hierarquia
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findByNome(String nome, int hierarquia,
			int tipo) throws DAOException {
		try {
			nome = '%' + nome.trim() + '%';

			String where = " where " + UFRNUtils.toAsciiUpperUTF8("u.nome")
					+ " like " + UFRNUtils.toAsciiUpperUTF8(":NOME");

			if (hierarquia > 0)
				where += "AND hierarquia like '%." + hierarquia + ".%'";

			if (tipo > 0) {
				where += " AND tipo = " + tipo;
			}
			Query query = getSession().createQuery(
					"select u from Unidade u" + where + "order by u.nome");
			query.setString("NOME", nome);

			if (isPaginable()) {
				query.setMaxResults(getPageSize());
				query.setFirstResult((getPageNum() - 1) * getPageSize());

				if (getCount() == 0) {
					Query queryCount = getSession().createQuery(
							"select count(*) from Unidade u" + where);
					queryCount.setString("NOME", nome);
					Integer count = (Integer) queryCount.uniqueResult();

					setCount(count.intValue());
				}
			}

			@SuppressWarnings("unchecked")
			Collection<UnidadeGeral> lista = query.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna todas as unidades gestoras acadêmicas(Somente se elas são de fato gestora de alguma outra unidade)
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findAllGestorasAcademicas() throws DAOException {
		try {
			Query q = getSession().createQuery(
					"select distinct u.gestoraAcademica from Unidade u");

			@SuppressWarnings("unchecked")
			Collection<Unidade> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna todas as unidades gestoras acadêmicas de acordo com o nivel passadopor parametro
	 * 
	 * @return
	 * @throws DAOException
	 * @param Nivel
	 */
	public Collection<Unidade> findAllGestorasAcademicas(Character Nivel) throws DAOException {
		try {
			Query q = getSession().createQuery(
					"select pga.unidade from ParametrosGestoraAcademica pga "+
					"where pga.nivel = '"+ Nivel + "'"+ "and pga.unidade.tipoAcademica in ("+
					TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA +","+
					TipoUnidadeAcademica.COORDENACAO_CURSO +")");

			@SuppressWarnings("unchecked")
			Collection<Unidade> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna uma coleção de todas as Unidades que definem horários
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findAllGestorasAcademicasDefinemHorarios() throws DAOException {
		try {			
			
			Query q = getSession().createQuery(
					" select u " +
					" from Unidade u " +
					" where ( u.unidadeAcademica = trueValue() and tipoAcademica in (:PROGRAMA,:CENTRO,:ESCOLA)) or u.id = :GLOBAL " +
					" order by tipoAcademica ");			
			
			q.setInteger("PROGRAMA", UnidadeGeral.PROGRAMA);
			q.setInteger("CENTRO", UnidadeGeral.CENTRO);
			q.setInteger("ESCOLA", UnidadeGeral.ESCOLA);
			q.setInteger("GLOBAL", UnidadeGeral.UNIDADE_DIREITO_GLOBAL);

			@SuppressWarnings("unchecked")
			Collection<Unidade> lista = q.list();
			return lista;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna uma Unidade de acordo com 
	 * o parâmetro código 
	 * 
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public UnidadeGeral findByCodigo(long codigo) throws DAOException {
		try {
			Criteria criteria = getCriteria(Unidade.class);
			criteria.add(Restrictions.eq("codigo", codigo));
			return (UnidadeGeral) criteria.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e);

		}
	}

	/**
	 * Retorna uma Unidade de acordo com 
	 * o parâmetro código 
	 * 
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public Unidade findById(Integer id) throws DAOException {
		try {
			Criteria criteria = getCriteria(Unidade.class);
			criteria.add(Restrictions.eq("id", id));
			return  (Unidade)criteria.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e);

		}
	}

	/**
	 * Sobrecarga paginada de findAll
	 *
	 */
	@Override
	public <T> Collection<T> findAll(Class<T> classe, String orderFields[], String ascDesc[]) throws DAOException {
		try {
			Criteria c = getCriteria(classe);
			for (int a = 0; a < orderFields.length; a++) {
				if (ascDesc[a].equals("asc")) {
					c.addOrder(Order.asc(orderFields[a]));
				} else {
					c.addOrder(Order.desc(orderFields[a]));
				}
			}

			if (isPaginable()) {
				c.setMaxResults(getPageSize());
				c.setFirstResult((getPageNum() - 1) * getPageSize());

				if (getCount() == 0) {
					Object ret = getSession().createQuery(
							"select count(*) from Unidade u").uniqueResult();
					setCount(((Integer) ret).intValue());
				}
			}

			@SuppressWarnings("unchecked")
			Collection<T> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	

	/**
	 * Retorna todos os centros com seus departamentos.
	 * 
	 * @param tipos
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Unidade> findCentroDepto() throws DAOException {

		StringBuilder hql = new StringBuilder(" SELECT ");
		hql.append(" d.id,  d.nome,  d.sigla, d.tipoAcademica, c.id, c.nome, c.sigla ");
		hql.append(" FROM Unidade d LEFT JOIN d.unidadeResponsavel c  WHERE c.tipoAcademica IN (");
		hql.append( TipoUnidadeAcademica.CENTRO + "," + TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA + ")");
		hql.append(" AND (d.tipoAcademica = " + TipoUnidadeAcademica.DEPARTAMENTO  + " OR d.tipoAcademica IS NULL) ");
		hql.append(" ORDER BY c.nome, d.nome ");
			
		Query query = getSession().createQuery(hql.toString());
		
		Iterator<Object[]> it = query.list().iterator();
		List<Unidade> result = new ArrayList<Unidade>();
		Integer idTipo = 0;
		while(it.hasNext()){
			
			Object[] colunas = it.next();
			idTipo = (Integer) colunas[3];
			
			Unidade centro = new Unidade((Integer) colunas[4], null,(String) colunas[5], (String) colunas[6]);
			
			if (!result.contains(centro)){
				centro.setNome((String)colunas[5]);
				centro.setSigla((String)colunas[6]);				
				centro.setColectionGeral(new ArrayList<Unidade>());
				result.add(centro);
			} else
				centro = result.get(result.indexOf(centro));
			
			if( idTipo != null && idTipo.equals(TipoUnidadeAcademica.DEPARTAMENTO) ){
				Unidade depart = new Unidade((Integer) colunas[0], null,(String) colunas[1], (String) colunas[2]);
				centro.getColectionGeral().add(depart);
			}
		}
		
		return result;
		
	}
	
	
	
	/**
	 * Retorna uma coleção de Unidades de acordo
	 * com o parâmetro tipoUnidadeAcademica(Departamento, Escola, Programa Pós, Centro, 
	 * Coordenação Curso, Órgão Suplementar, Coordenação Lato Senso )
	 * 
	 * @param tipos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findByTipoUnidadeAcademica(int... tipos) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select new Unidade( u.id, u.codigo, u.nome, u.sigla, u.nomeCapa, u.hierarquia, m.nome, u.siglaAcademica, u.tipoAcademica) from Unidade u left join u.municipio m where ");
		
		if (!isEmpty(tipos)) {
			hql.append("u.tipoAcademica in " + gerarStringIn(tipos));
		}
		
		hql.append(" order by u.nome, u.codigo asc");
		Query q = getSession().createQuery(hql.toString());

		@SuppressWarnings("unchecked")
		Collection<Unidade> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna uma coleção de unidades que possuem componentes curriculares de acordo
	 * com o parâmetro tipoUnidadeAcademica(Departamento, Escola, Programa Pós, Centro, 
	 * Coordenação Curso, Órgão Suplementar, Coordenação Lato Senso )
	 * 
	 * @param tipos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findDetentoraComponentesByTipoUnidadeAcademica(Integer... tipos) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select new Unidade( u.id, u.codigo, u.nome, u.sigla, u.nomeCapa, u.hierarquia, m.nome, u.siglaAcademica, u.tipoAcademica) " +
				"from ComponenteCurricular cc " +
				"inner join cc.unidade u " +
				"left join u.municipio m " +
				"where ");
		
		if (!isEmpty(tipos)) {
			hql.append("u.tipoAcademica in " + gerarStringIn(tipos));
		}
		
		hql.append(" group by u.nome, u.id, u.codigo, u.sigla, u.nomeCapa, u.hierarquia, m.nome, u.siglaAcademica, u.tipoAcademica " +
				" order by u.nome, u.codigo asc");
		Query q = getSession().createQuery(hql.toString());

		@SuppressWarnings("unchecked")
		Collection<Unidade> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna uma coleção de Unidades de acordo
	 * com o parâmetro niveis e tipoUnidadeAcademica(Departamento, Escola, Programa Pós, Centro, 
	 * Coordenação Curso, Órgão Suplementar, Coordenação Lato Senso )
	 * 
	 * @param tipos
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findByTipoUnidadeAcademicaNivel( Collection<Integer> tipos, Collection<Character> niveis )
			throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql
					.append(" select distinct new Unidade( u.id, u.codigo, u.nome, u.sigla, u.nomeCapa, u.hierarquia, u.municipio.nome, u.siglaAcademica, u.tipoAcademica ) "
							+ " from Curso c  " 
							+ " inner join c.unidade u" 
							+ " where 1 = 1");
			
			if (tipos != null && tipos.size() > 0) {
				hql.append(" and u.tipoAcademica in " + gerarStringIn(tipos));
			}
			if (niveis != null && niveis.size() > 0) {
				hql.append(" and c.nivel in " + gerarStringIn(niveis));
			}
			
			hql.append(" order by u.nome asc, u.tipoAcademica");
			Query q = getSession().createQuery(hql.toString());

			@SuppressWarnings("unchecked")
			Collection<Unidade> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca todas as unidade com base no tipo acadêmica e gestora acadêmica.
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Unidade> findByTipoUnidadeAndGestoraAcademica(int tipoAcademica, int idUnidadeAcademica) throws DAOException{
		
		String sql = "select u.id_unidade, u.nome from comum.unidade u where u.tipo_academica = ? and u.id_gestora_academica = ?";
		
		return getJdbcTemplate().query(sql, new Object[]{tipoAcademica, idUnidadeAcademica}, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int i) throws SQLException {
				Unidade unidade = new Unidade();
				
				unidade.setId(rs.getInt("id_unidade"));
				unidade.setNome(rs.getString("nome"));
				
				return unidade;
			}
		});
	}


	/**
	 * Retorna uma coleção de Unidades Orçamentárias  que são visualizadas
	 * no SCO
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findAllUnidadeOrcamentaria()throws DAOException {
	   return findAllUnidadeOrcamentariaByCategoria(null);
	}

	/**
	 * Retorna uma coleção de unidades orçamentárias de uma categoria específica.
	 * Se a categoria não for informada retorna todas as unidades orçamentárias.
	 * Utilizado na vinculação de projetos a unidades orçamentárias.
	 * 
	 * @param categoria 
	 * @see {@link CategoriaUnidade}
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findAllUnidadeOrcamentariaByCategoria(Integer... categoria)
			throws DAOException {
		try {
			Criteria criteria = getCriteria(Unidade.class);
			criteria.add(Restrictions.eq("unidadeOrcamentaria", Boolean.TRUE));			
			if (categoria != null) {
			    criteria.add(Restrictions.in("categoria", categoria));
			}			
			criteria.addOrder(Order.asc("nomeCapa"));

			@SuppressWarnings("unchecked")
			Collection<UnidadeGeral> lista = criteria.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}


	/**
	 * Retorna uma coleção de Unidade Acadêmica de acordo com
	 * nome da unidade e/ou tipo da unidade acadêmica  
	 * 
	 * @param nome
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findUnidadeAcademicaByNome(String nome, Integer tipo)throws DAOException {
		return findUnidadeAcademicaByNome(nome, tipo, null);
	}
	
	/**
	 * Retorna uma coleção de Unidade Acadêmica de acordo com
	 * nome da unidade, tipo da unidade acadêmica, ativa ou não 
	 * 
	 * @param nome
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<UnidadeGeral> findUnidadeAcademicaByNome(String nome, Integer tipo, Boolean ativo)
			throws DAOException {
		try {
			nome = '%' + nome.trim() + '%';

			String where = " where " + UFRNUtils.toAsciiUpperUTF8("u.nome")
					+ " like " + UFRNUtils.toAsciiUpperUTF8(":NOME");
			if( !isEmpty(tipo) )
				where += " and tipoAcademica = :tipo ";
			if( !isEmpty(ativo) )
				where += " and ativo = :ativo ";
			
			where += " AND u.unidadeAcademica = trueValue() ";

			Query query = getSession().createQuery(
					"select u from Unidade u" + where + "order by u.nome");
			query.setString("NOME", nome);
			if( !isEmpty(tipo) )
				query.setInteger("tipo", tipo);
			if( !isEmpty(ativo) )
				query.setBoolean("ativo", ativo);

			if (isPaginable()) {
				query.setMaxResults(getPageSize());
				query.setFirstResult((getPageNum() - 1) * getPageSize());

				if (getCount() == 0) {
					Query queryCount = getSession().createQuery(
							"select count(*) from Unidade u" + where);
					queryCount.setString("NOME", nome);
					Integer count = (Integer) queryCount.uniqueResult();

					setCount(count.intValue());
				}
			}

			@SuppressWarnings("unchecked")
			Collection<UnidadeGeral> lista = query.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna uma lista das Áreas de Concentração de acordo
	 * com o parâmetro id (Unidade que representa um Programa de Pós)
	 * 
	 * @param id
	 * @return
	 */
	public ArrayList<AreaConcentracao> buscarAreas(int id) {
		try {
			@SuppressWarnings("unchecked")
			List<AreaConcentracao> lista = getSession().createQuery("from AreaConcentracao area where area.programa.id = ?").setInteger(0, id).list();
			return (ArrayList<AreaConcentracao>) lista;
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Faz uma busca por linhas de pesquisa de acordo com o id de um programa. 
	 * 
	 * @param id
	 * @return
	 */
	public ArrayList<LinhaPesquisaStricto> buscarLinhasPesquisa(int id) {
		try {
			@SuppressWarnings("unchecked")
			List<LinhaPesquisaStricto> lista = getSession().createQuery("from LinhaPesquisaStricto lp where lp.programa.id = ?").setInteger(0, id).list();
			return (ArrayList<LinhaPesquisaStricto>) lista;
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retorna uma Lista de Equipes do Programa (Unidade Acadêmica) de acordo
	 * com o parâmetro id , identificador da Unidade.
	 * 
	 * @param id
	 * @return
	 */
	public ArrayList<EquipePrograma> buscarEquipePrograma(int id) {
		
		try {
			@SuppressWarnings("unchecked")
			List<EquipePrograma> lista = getSession().createQuery("select ep from EquipePrograma as ep " +
																  "left join ep.docenteExterno as de " +
																  "where ep.programa.id = ? ").setInteger(0, id).list();
			return (ArrayList<EquipePrograma>) lista;
		} catch (HibernateException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * Retorna uma coleção contendo as reprovações de acordo com os
	 * os parâmetros centro, ano, período  
	 * 	
	 * @param departamento
	 * @param ano
	 * @param periodo
	 * @param qtd
	 * @return
	 * @throws DAOException
	 */
	public Collection<RelatorioReprovacoesDisciplinas> findReprovacoesByDepartamento(Unidade centro, int anoInicio, int anoFim) throws DAOException {
		
		String sitAtivos = "";
		for (SituacaoMatricula s : SituacaoMatricula.getSituacoesAtivas()){		
			if (ValidatorUtil.isNotEmpty(sitAtivos)) sitAtivos += " + ";
			sitAtivos += " case (mc.id_situacao_matricula) when "+s.getId()+" then 1 else 0 end ";
		}
		
		String sitPagas = "";
		for (SituacaoMatricula s : SituacaoMatricula.getSituacoesPagas()){
			if (ValidatorUtil.isNotEmpty(sitPagas)) sitPagas += " + ";	
			sitPagas += " case (mc.id_situacao_matricula) when "+s.getId()+" then 1 else 0 end ";
		}
		
		String sitRep = "";
		for (SituacaoMatricula s : SituacaoMatricula.getSituacoesReprovadas()){	
			if (ValidatorUtil.isNotEmpty(sitRep)) sitRep += " + ";	
			sitRep += " case (mc.id_situacao_matricula) when "+s.getId()+" then 1 else 0 end ";
		}
		
		Query q = getSession().createSQLQuery(
				" select id_centro, centro, id_unidade, unidade, id_disciplina, codcomponente, disciplina,  "+ 
						"	id_turma, id_docente_turma, nome, ano, periodo, codigo, descricao, "+
						"   ativos, aprovados, reprovados, trancados "+
			" from ( "+
			"		select distinct centro.id_unidade as id_centro, centro.nome as centro, u.id_unidade, u.nome as unidade, cc.id_disciplina, " +
			"         cc.codigo as codcomponente, ccd.nome as disciplina," +
			"        t.id_turma, dt.id_docente_turma, p.nome, t.ano, t.periodo, "+
			"		t.codigo, st.descricao,"+
			"	      sum("+ sitAtivos+") as ativos,"+
			"	      sum("+ sitPagas+") as aprovados,"+   
			"	      sum("+ sitRep+ ") as reprovados,"+       
			"	      sum( case (mc.id_situacao_matricula) when "+SituacaoMatricula.TRANCADO.getId()+" then 1 else 0 end ) as trancados " +	
			"	from ensino.turma t "+
			"	inner join ensino.matricula_componente mc on mc.id_turma = t.id_turma"+ 
			"	left join ensino.docente_turma dt on t.id_turma = dt.id_turma"+
			"   left join rh.servidor s on dt.id_docente = s.id_servidor "+
			"   left join ensino.docente_externo de on dt.id_docente_externo = de.id_docente_externo "+
			"   left join comum.pessoa p on p.id_pessoa = coalesce (s.id_pessoa, de.id_pessoa) "+			
			"	inner join ensino.componente_curricular cc on t.id_disciplina = cc.id_disciplina"+ 
			"	inner join ensino.componente_curricular_detalhes ccd on cc.id_detalhe = ccd.id_componente_detalhes"+  
			"	inner join ensino.situacao_turma st on st.id_situacao_turma = t.id_situacao_turma"+
			"	inner join comum.unidade u on u.id_unidade = cc.id_unidade"+
			"	inner join comum.unidade centro on centro.id_unidade = u.unidade_responsavel"+
			"	where t.id_situacao_turma = "+SituacaoTurma.CONSOLIDADA+ 
			"	 and t.ano between :anoInicio and :anoFim "+
			"	 and cc.nivel = '"+NivelEnsino.GRADUACAO+"'"+
			(ValidatorUtil.isNotEmpty(centro) ? " and centro.id_unidade = "+centro.getId() : 
					" and centro.tipo_academica in ("+TipoUnidadeAcademica.CENTRO+","+ TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA+")")+
			"	 and u.tipo_academica = "+TipoUnidadeAcademica.DEPARTAMENTO+
			"	group by t.id_turma, dt.id_docente_turma, centro.id_unidade, centro.nome, p.nome, t.ano,"+ 
			"		t.periodo, u.id_unidade, u.nome, t.codigo, cc.id_disciplina, cc.codigo, "+
			"		ccd.nome, st.descricao "+
			"	) a "+
			"	where reprovados > 0 "+
			"	order by centro, unidade, disciplina, reprovados desc, codigo "			
		);
		
		q.setInteger("anoInicio", anoInicio);
		q.setInteger("anoFim", anoFim);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		Iterator<Object[]> it = lista.iterator();
		List<RelatorioReprovacoesDisciplinas> result = new ArrayList<RelatorioReprovacoesDisciplinas>();
		while(it.hasNext()){
			Object[] colunas = it.next();
			
			ComponenteCurricular cc = new ComponenteCurricular();
			cc.setId((Integer) colunas[4]);
			
			RelatorioReprovacoesDisciplinas linha = new RelatorioReprovacoesDisciplinas();
			linha.setDisciplina(cc);
		
			if (!result.contains(linha)){
				
				Unidade uc = new Unidade();
				uc.setId((Integer) colunas[0]);
				uc.setNome((String) colunas[1]);			
				
				Unidade ud = new Unidade();
				ud.setId((Integer) colunas[2]);
				ud.setNome((String) colunas[3]);				
				
				cc.setDetalhes(new ComponenteDetalhes());
				cc.setCodigo((String) colunas[5]);
				cc.setNome((String) colunas[6]);	
				
				linha.setDisciplina(cc);
				linha.setCentro(uc);
				linha.setDepartamento(ud);
				linha.setTurmas(new ArrayList<Turma>());
				result.add(linha);
				
			} else {
				
				linha = result.get(result.indexOf(linha));
				
			}
			
			Turma t = new Turma();
			t.setId((Integer) colunas[7]);
			
			if (!linha.getTurmas().contains(t)) {
				t.setAno((Integer) colunas[10]);
				t.setPeriodo((Integer) colunas[11]);
				t.setCodigo((String) colunas[12]);
				t.setSituacaoTurma(new SituacaoTurma(0, (String) colunas[13]));				
				t.setDocentesTurmas(new HashSet<DocenteTurma>());
				linha.getTurmas().add(t);
			} else {
				t = linha.getTurmas().get(linha.getTurmas().indexOf(t));
			}
			
			BigInteger val = (BigInteger) colunas[14];
			t.setQtdMatriculados(t.getQtdMatriculados() + val.longValue());
			
			val = (BigInteger) colunas[15];
			t.setQtdAprovados(t.getQtdAprovados() + val.longValue());
			
			val = (BigInteger) colunas[16];
			t.setQtdReprovados(t.getQtdReprovados() + val.longValue());
			
			val = (BigInteger) colunas[17];
			t.setQtdTrancados(t.getQtdTrancados() + val.longValue());
			
			linha.setTotal(linha.getTotal() + t.getQtdMatriculados());
			linha.setAprovados(linha.getAprovados() + t.getQtdAprovados());
			linha.setReprovados(linha.getReprovados() + t.getQtdReprovados());
			linha.setTrancados(linha.getTrancados() + t.getQtdTrancados());	
			linha.setPercentual( 100 * ((1.0f*linha.getReprovados())/(1.0f*linha.getTotal())) );
			
			if ((Integer) colunas[8] != null){
				DocenteTurma dt = new DocenteTurma();			
				dt.setIdTurma(t.getId());
				dt.setDocente(new br.ufrn.sigaa.pessoa.dominio.Servidor((Integer) colunas[8]));
				dt.getDocente().setPessoa(new Pessoa());
				dt.getDocente().getPessoa().setNome((String) colunas[9]);				
				t.addDocenteTurma(dt);								
			}
			
			
		}

		return result;
	}	
	
	
	
	/**
	 * Retorna as unidades que podem propor projetos.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<Unidade> findUnidadesProponentesProjetos()
	throws DAOException {

		try {
		
			String hql = "select distinct uni.id, uni.codigo, uni.nome, uni.hierarquiaOrganizacional " +
					"from Unidade uni where uni.id in ( select distinct s.unidade.id from Servidor s) or uni.unidadeAcademica = trueValue() " +
							" order by uni.nome";
		
			Query q = getSession().createQuery(hql);
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			ArrayList<Unidade> unidades = new ArrayList<Unidade>();
			for (Object[] element : result) {
				Unidade u = new Unidade();
				u.setId((Integer)element[0]);
				u.setCodigo((Long)element[1]);
				u.setNome((String) element[2]);
				u.setHierarquiaOrganizacional((String) element[3]);
				unidades.add(u);
			}
			return unidades;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {

		}
	
	}
	

		
	/**
	 * Método responsável por realizar a busca na unidade da coordenação para um servidor
	 * 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public UnidadeGeral findUnidadeCoordenacaoServidor(int idServidor) throws DAOException{
		
		String sql = "select distinct u.* from ensino.coordenacao_curso cc, rh.servidor s, comum.pessoa p,comum.unidade u " +
				"where s.id_servidor = ? and cc.id_servidor = s.id_servidor and s.id_pessoa = p.id_pessoa " +
				"and cc.ativo = true and cc.data_fim_mandato > now() and id_cargo_academico in "+UFRNUtils.gerarStringIn(new int[]{CargoAcademico.COORDENACAO,CargoAcademico.VICE_COORDENACAO});
			sql += " and ( u.id_unidade = cc.id_unidade  ";
			sql += " or u.id_unidade in ( select id_unidade_coordenacao from curso where id_curso = cc.id_curso)) ";
		
		@SuppressWarnings("unchecked")
		List<UnidadeGeral> unidades = getJdbcTemplate().query(sql, new Object[]{idServidor}, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int i) throws SQLException {
				
				UnidadeGeral unidade = new UnidadeGeral();
				
				unidade.setId(rs.getInt("id_unidade"));
				unidade.setCodigo(rs.getLong("codigo_unidade"));
				unidade.setNome(rs.getString("nome"));
				unidade.setSigla(rs.getString("sigla"));
				
				return unidade;
			}
			
		});
		
		if(unidades != null && !unidades.isEmpty())
			return unidades.get(0);
		
		return null;
	}
	
	/**
	 * Método responsável por realizar a busca na unidade da coordenação para um curso.
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public UnidadeGeral findUnidadeCoordenacaoByCursoAluno(int idCurso,char nivelEnsino) throws DAOException{
		
		String sql = "select distinct u.* from comum.unidade u, curso c " +
				"where c.id_curso = ? ";
		if(NivelEnsino.isAlgumNivelStricto(nivelEnsino))
			sql +=" and u.id_unidade = c.id_unidade ";	
		else 
			sql +=" and u.id_unidade = c.id_unidade_coordenacao ";
			
		
		@SuppressWarnings("unchecked")
		List<UnidadeGeral> unidades = getJdbcTemplate().query(sql, new Object[]{idCurso}, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int i) throws SQLException {
				
				UnidadeGeral unidade = new UnidadeGeral();
				
				unidade.setId(rs.getInt("id_unidade"));
				unidade.setCodigo(rs.getLong("codigo_unidade"));
				unidade.setNome(rs.getString("nome"));
				unidade.setSigla(rs.getString("sigla"));
				
				return unidade;
			}
			
		});
		
		if(unidades != null && !unidades.isEmpty())
			return unidades.get(0);
		
		return null;
	}
	
	/**
	 * Busca todos os responsáveis das unidades informadas.
	 * 
	 * <br/>
	 * @param idsUndades 
	 * @param nivel
	 * @return todos os Responsáveis pelas unidades informadas
	 */
	@SuppressWarnings("unchecked")
	public List<Responsavel> findResponsaveisByUnidades(Set<Integer> idsUndades, char[] niveis) throws DAOException {
	    try {
		
		String sql = "select resp.*, s.id_servidor, p.id_pessoa, p.nome, p.email from comum.responsavel_unidade as resp " +
				" inner join comum.unidade u on resp.id_unidade = u.id_unidade " +
				" inner join rh.servidor s on resp.id_servidor = s.id_servidor " +
				" inner join comum.pessoa p on s.id_pessoa = p.id_pessoa " +
				" where u.id_unidade in " + UFRNUtils.gerarStringIn(idsUndades) +
				" and resp.nivel_responsabilidade in " + UFRNUtils.gerarStringIn(niveis) +
				" and resp.data_inicio <= now() and (resp.data_fim is null or resp.data_fim >= now())";

		List<Responsavel> responsaveis = getJdbcTemplate().query(sql, new RowMapper(){
		    
		    @Override
		    public Object mapRow(ResultSet rs, int i) throws SQLException {
			Responsavel resp = new Responsavel();
			resp.setId(rs.getInt("id"));
			resp.setServidor(new Servidor(rs.getInt("id_servidor")));
				Pessoa p = new Pessoa();
				p.setId(rs.getInt("id_pessoa"));
				p.setNome(rs.getString("nome"));
				p.setEmail(rs.getString("email"));
				resp.getServidor().setPessoa(p);
			resp.setUnidade(new Unidade (rs.getInt("id_unidade")));
			resp.setInicio(rs.getDate("data_inicio"));
			resp.setFim(rs.getDate("data_fim"));
			resp.setIdDesignacao(rs.getInt("id_designacao"));
			resp.setNomeServidor(rs.getString("nome"));
			return resp;
		    	}
		    
		});

		return responsaveis;
		
	    } catch (Exception e) {
		throw new DAOException(e);
	    }
	}
	
	/**
	 * Busca da unidade gestora de uma outra unidade
	 * @param un
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Unidade findGestoraByUnidade(Unidade un ) throws HibernateException, DAOException {
		
		Query q = getSession().createQuery("select u.gestora.id, u.gestora.nome, u.gestora.municipio.id, u.gestora.municipio.nome from Unidade u where u.id = " + un.getId());
		Object[] result = (Object[]) q.uniqueResult();
		if ( result != null ) {
			Unidade u = new Unidade();
			u.setId((Integer)result[0]);
			u.setNome((String) result[1]);
			
			u.setMunicipio(new Municipio((Integer) result[2]));
			u.getMunicipio().setNome( (String) result[3] );
			return u;
		}
		return null;
	}
	
	/**
	 * Busca todas as unidade com base no tipo Acadêmica e da unidade Academica
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Unidade> findUnidadeProgramaResidenciaMedica(int tipoAcademica, int idUnidadeAcademica) throws DAOException{
		
		String sql = "select u.id_unidade, u.nome from comum.unidade u where u.tipo_academica = ? and u.id_gestora_academica = ?";
		
		return getJdbcTemplate().query(sql, new Object[]{tipoAcademica, idUnidadeAcademica}, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int i) throws SQLException {
				Unidade unidade = new Unidade();
				
				unidade.setId(rs.getInt("id_unidade"));
				unidade.setNome(rs.getString("nome"));
				
				return unidade;
			}
		});
	}

}