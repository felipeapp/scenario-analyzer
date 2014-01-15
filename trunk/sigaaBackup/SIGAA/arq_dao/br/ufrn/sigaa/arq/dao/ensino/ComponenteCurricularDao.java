/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/01/2007
 *
 */
package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Restrictions.eq;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AlteracaoAtivacaoComponente;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricularPrograma;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.DadosCalculosDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.EquivalenciaEspecifica;
import br.ufrn.sigaa.ensino.dominio.FormaParticipacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoAtividadeComplementar;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.IntegralizacoesHelper;
import br.ufrn.sigaa.ensino.latosensu.dominio.ComponenteCursoLato;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.util.RepositorioInformacoesCalculoDiscente;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * DAO para efetuar consultas de Componentes Curriculares no banco.
 *
 * @author Leonardo
 *
 */
public class ComponenteCurricularDao extends GenericSigaaDAO {

	/** Valor limite de resultados a serem utilizados nas consultas de Componentes Curriculares.*/
	public static final int LIMITE_RESULTADOS = 50;
	/** HashTable utilizado para armazenar o código e nome de componentes curricular. */
	private static Hashtable<Integer, String> cacheCodigoNome = new Hashtable<Integer,String>();

	/** HashTable utilizado para armazenar os códigos das equivalências na busca de componentes por equivalência. */
	private static Hashtable<Integer, String> equivalenciasCache = new Hashtable<Integer,String>();
	
	/**
	 * Busca por nome, filtrando por unidade e nível de ensino do componente.
	 * Para buscar apenas por nível de ensino, basta passar o ID da unidade menor
	 * ou igual a Zero.
	 *
	 * @param nome
	 * @param unid
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByNome(String nome, PagingInformation paging) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select cc from ComponenteCurricular cc " +
					" inner join cc.detalhes detalhes " +
					" where upper(detalhes.nome_ascii) like :nomeComponente"+
					" and ativo = trueValue()" +
					" order by detalhes.nome asc");
			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			q.setString("nomeComponente", StringUtils.toAscii(nome.toUpperCase().trim()) + "%");
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = q.list();
			return lista;
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna os componentes curriculares da unidade passada como parâmetro e que possuem o nível
	 * de ensino passado como parâmetro.
	 * 
	 * @param unid
	 * @param nivelEnsino
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByUnidadeOtimizado(int unid, char nivelEnsino, PagingInformation paging) throws DAOException {
		return findByUnidade(unid, nivelEnsino, paging, true);
	}

	/**
	 * Busca os componentes de uma unidade, em um nível de ensino e que foram
	 * oferecidos em um determinado ano-período.
	 * @param unid
	 * @param nivelEnsino
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByUnidadeTurmaAnoSemestre(int unid,
			char nivelEnsino, int ano, int periodo) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select new ComponenteCurricular(c.id, c.codigo, c.detalhes.nome, c.detalhes.chTotal, c.nivel) ");
			hql.append("from ComponenteCurricular c where 1 = 1");
			if (unid > 0)
				hql.append(" and c.unidade.id = " + unid);
			if (nivelEnsino != ' ') {
				hql.append("and c.nivel = '" + nivelEnsino + "'");
			}
			hql.append( " and c.ativo = trueValue() " );
			hql.append(" and c.id in ( select t.disciplina.id from Turma t where t.ano = " + ano + " and t.periodo = " + periodo + ") " );
			hql.append(" order by detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());

			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	

	/**
	 * Retorna os componentes curriculares da unidade passada como parâmetro e que possuem o nível
	 * de ensino passado como parâmetro.
	 * 
	 * @param unid
	 * @param nivelEnsino
	 * @param paging
	 * @param otimizado
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByUnidade(int unid,
			char nivelEnsino, PagingInformation paging, boolean otimizado) throws DAOException {

		try {
			StringBuffer hql = new StringBuffer();
			if (otimizado)
				hql.append("select new ComponenteCurricular(c.id, c.codigo, c.detalhes.nome, c.detalhes.chTotal, c.nivel) ");
			hql.append("from ComponenteCurricular c where ");
			if (unid > 0)
				hql.append("c.unidade.id = " + unid + " and ");
			if (nivelEnsino != ' ') {
				hql.append("c.nivel = '" + nivelEnsino + "' ");
			}
			hql.append( " and c.ativo = trueValue() " );
			hql.append("order by detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());
			preparePaging(paging, q);
			
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca os componentes curriculares de um determinado nível de ensino
	 * contendo todas as expressões (de pré-requisito, co-requisito e equivalência)
	 * populadas.
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findAllExpressoes(char nivel) throws DAOException {

		try {
			StringBuffer hql = new StringBuffer();
				hql.append("select new ComponenteCurricular(c.id, c.detalhes.codigo, c.detalhes.nome, " +
						"c.detalhes.equivalencia, c.detalhes.coRequisito, c.detalhes.preRequisito ) " +
						" from ComponenteCurricular c  " +
						" where (c.detalhes.equivalencia is not null and c.detalhes.equivalencia <> '')" +
						" or (c.detalhes.coRequisito is not null and c.detalhes.coRequisito <> '')" +
						" or (c.detalhes.preRequisito is not null and c.detalhes.preRequisito <> '')");
			hql.append( " and c.ativo = trueValue() " );
			hql.append( " and c.nivel = :nivel");
			hql.append(" order by c.id");

			Query q = getSession().createQuery(hql.toString());
			q.setCharacter("nivel", nivel);

			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = q.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Busca atividades por nível, currículo ao qual pertencem, unidade, nome ou tipo de atividade.
	 * 
	 * @param nivel
	 * @param curriculo
	 * @param unidade
	 * @param nome
	 * @param tipoAtividade
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findAtividades(char nivel, Curriculo curriculo, Unidade unidade, String nome, Integer tipoAtividade) throws DAOException{
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" select distinct c.id as id, c.codigo as codigo," +
					" c.tipoAtividade as tipoAtividade, c.unidade as unidade," +
					" c.detalhes as detalhes, c.tipoComponente as tipoComponente, c.nivel as nivel ");
			hql.append(" from ComponenteCurricular c where ");
			hql.append(" c.tipoComponente.id = " + TipoComponenteCurricular.ATIVIDADE);
			hql.append(" and c.ativo = trueValue() ");
			hql.append(" and c.nivel = '" + nivel + "' ");

			if (curriculo != null) {
				hql.append(" and c.id in " +
						" (select cc.componente.id from CurriculoComponente cc" +
						" where cc.curriculo.id = " + curriculo.getId() + ")");
			}
			if (unidade != null) {
				hql.append(" and c.unidade.id = " + unidade.getId());
			}
			
			if (nome != null && !"".equals(nome.trim().toUpperCase())) {
				hql.append(" and ("
					+ UFRNUtils.toAsciiUpperUTF8("c.detalhes.nome")
					+ " like "
					+ UFRNUtils.toAsciiUpperUTF8(":nomeAtividade")
					+ " or "
					+ UFRNUtils.toAsciiUpperUTF8("c.codigo")
					+ " like "
					+ UFRNUtils.toAsciiUpperUTF8(":nomeAtividade")
					+ " ) " );
			}
			if (tipoAtividade != null) {
				hql.append(" and c.tipoAtividade.id = " + tipoAtividade);
			}
			hql.append(" order by c.detalhes.nome asc, c.detalhes.codigo");

			Query q = getSession().createQuery(hql.toString());
			if (nome != null && !"".equals(nome.trim().toUpperCase())) {
				q.setString("nomeAtividade", "%"+nome.trim().toUpperCase()+"%");
			}
			
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = q.setResultTransformer(Transformers.aliasToBean(ComponenteCurricular.class)).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Pega todas as atividades de diferentes currículos, desde que sejam do mesmo curso e possuam o mesmo grau acadêmico.
	 * 
	 * @param nivel
	 * @param curriculo
	 * @param unidade
	 * @param nome
	 * @param tipoAtividade
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findAtividadesMesmoCursoAndGrau(char nivel, Curriculo curriculo, Unidade unidade, String nome, Integer tipoAtividade) throws DAOException{
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" select distinct c.id as id, c.codigo as codigo," +
					" c.tipoAtividade as tipoAtividade, c.unidade as unidade," +
					" c.detalhes as detalhes, c.tipoComponente as tipoComponente, c.nivel as nivel ");
			hql.append(" from ComponenteCurricular c where ");
			hql.append(" c.tipoComponente.id = " + TipoComponenteCurricular.ATIVIDADE);
			hql.append(" and c.formaParticipacao.id <> " + FormaParticipacaoAtividade.ESPECIAL_COLETIVA);
			hql.append(" and c.ativo = trueValue() ");
			hql.append(" and c.nivel = '" + nivel + "' ");

			if (curriculo != null) {
				hql.append(" and c.id in " +
						" (select cc.componente.id from CurriculoComponente cc" +
						" where cc.curriculo.curso.id = " + curriculo.getCurso().getId() + " and cc.curriculo.matriz.grauAcademico.id = " + curriculo.getMatriz().getGrauAcademico().getId() + ")");
			}
			if (unidade != null) {
				hql.append(" and c.unidade.id = " + unidade.getId());
			}
			
			if (nome != null && !"".equals(nome.trim().toUpperCase())) {
				hql.append(" and ("
					+ UFRNUtils.toAsciiUpperUTF8("c.detalhes.nome")
					+ " like "
					+ UFRNUtils.toAsciiUpperUTF8(":nomeAtividade")
					+ " or "
					+ UFRNUtils.toAsciiUpperUTF8("c.codigo")
					+ " like "
					+ UFRNUtils.toAsciiUpperUTF8(":nomeAtividade")
					+ " ) " );
			}
			if (tipoAtividade != null) {
				hql.append(" and c.tipoAtividade.id = " + tipoAtividade);
			}
			hql.append(" order by c.detalhes.nome asc, c.detalhes.codigo");

			Query q = getSession().createQuery(hql.toString());
			if (nome != null && !"".equals(nome.trim().toUpperCase())) {
				q.setString("nomeAtividade", "%"+nome.trim().toUpperCase()+"%");
			}
			
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = q.setResultTransformer(Transformers.aliasToBean(ComponenteCurricular.class)).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}	

	/**
	 * Busca componentes curriculares de acordo com o nível de ensino, unidade
	 * ou tipo de componente curricular. É feita uma projeção, de modo que a busca
	 * é otimizada.
	 * @param nivel
	 * @param unidade
	 * @param tipoComponente
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findOtimizado(char nivel, Unidade unidade, Integer... tipoComponente) throws DAOException{
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" select distinct c.id as id, c.codigo as codigo," +
					"  c.unidade as unidade," +
					" c.detalhes as detalhes, c.tipoComponente as tipoComponente, c.nivel as nivel ");
			hql.append(" from ComponenteCurricular c where ");
			hql.append(" c.tipoComponente.id in " + gerarStringIn(tipoComponente) );
			hql.append(" and c.nivel = '" + nivel + "' ");
			if (unidade != null) {
				hql.append(" and c.unidade.id = " + unidade.getId());
			}
			hql.append(" and c.ativo = trueValue() ");
			hql.append(" order by c.detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());
			
			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = q.setResultTransformer(Transformers.aliasToBean(ComponenteCurricular.class)).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca o último código utilizado no cadastro de componentes curriculares
	 * para a unidade e nível passados como parâmetro.
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricular findUltimoCodigo(int unidade, char nivel) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ComponenteCurricular.class);
			c.add(Restrictions.eq("nivel", nivel));
			c.add(Restrictions.eq("unidade", new Unidade(unidade)));
			c.addOrder(Order.desc("dataCadastro"));
			c.setMaxResults(1);
			return (ComponenteCurricular) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca os componentes curriculares com o código passado como parâmetro.
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findByCodigo(String codigo, boolean somenteAtivos) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ComponenteCurricular.class);
			c.add(Restrictions.eq("codigo", codigo));
			if (somenteAtivos)
				c.add(Restrictions.eq("ativo", somenteAtivos));
			
			c.addOrder(Order.desc("codigo"));
			
			@SuppressWarnings("unchecked")
			List<ComponenteCurricular> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	
	/**
	 * Retorna o componente curricular por código e nível de ensino.
	 * @param codigo
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricular findByCodigoNivel(String codigo, char nivelEnsino) throws DAOException {
		String hql = "select cc from ComponenteCurricular cc where codigo = '" + codigo + "' and nivel = '" + nivelEnsino + "'";
		
		return (ComponenteCurricular) getSession().createQuery(hql).uniqueResult();
	}
	
	/**
	 * Busca os componentes curriculares com o código passado como parâmetro. <br>
	 * 
	 * SOMENTE COMPONENTES ATIVOS
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findByCodigo(String codigo) throws DAOException {
		return findByCodigo(codigo, true);
	}

	/**
	 * Retorna o id de um componente curricular de acordo com o código,
	 * unidade e nível de ensino.
	 * @param codigo
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Integer findIdByCodigo(String codigo, int unidade, char nivel) throws DAOException {
		return findIdByCodigo(codigo, unidade, nivel, Boolean.TRUE);
	}

	/**
	 * Sobrecarregando método para contemplar o ensino técnico, onde disciplinas inativas
	 * podem fazer parte de expressões de equivalências e requisitos.
	 * 
	 * @param codigo
	 * @param unidade
	 * @param nivel
	 * @param ativos
	 * @return
	 * @throws DAOException
	 */
	public Integer findIdByCodigo(String codigo, int unidade, char nivel, Boolean ativos) throws DAOException {
		
		try {
			Criteria c = getSession().createCriteria(ComponenteCurricular.class);
			c.setProjection(Projections.property("id"));
			c.add(Restrictions.eq("nivel", nivel));
			if(ativos != null && ativos)
				c.add(Restrictions.eq("ativo", true));
			// Somente para graduação não deve considerar a unidade do componente
			if (unidade > 0 && NivelEnsino.GRADUACAO != nivel) {
				c.add(Restrictions.eq("unidade", new Unidade(unidade)));
			}
			c.createCriteria("detalhes").add(Restrictions.eq("codigo", codigo));
			return (Integer) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	
	/** Cria uma tabela com o par <idComponenteCurricular, Código> a ser usado como cache em consultas.
	 * @param listaIdComponente
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Map<Integer, String> criaCacheComponente(List<Integer> listaIdComponente, boolean somenteAtivos) throws HibernateException, DAOException {
		// consulta todos componentes curriculares
		// é mais rápido trazer todos e tratar no método, que usar a expressão "IN" do SQL.
		StringBuilder hql = new StringBuilder("select id, codigo" +
				" from ComponenteCurricular");
		if (somenteAtivos)
			hql.append(" where ativo = trueValue()");
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createQuery(hql.toString()).list();
		if (!isEmpty(lista)) {
			Map<Integer, String> mapa = new HashMap<Integer, String>();
			for (Object[] res : lista) {
				// inclui no mapa somente os componentes que foram especificados na lista.
				if (listaIdComponente.contains(res[0]))
					mapa.put((Integer) res[0], (String) res[1]);
			}
			return mapa;
		}
		return null;
	}

	/**
	 * Retorna um mapa contendo pares (ID do componente, código) a partir da
	 * lista de ID dos componentes curriculares passados por parâmetro.
	 * 
	 */
	public Map<Integer, String> findCodigoNomeById(Collection<Integer> listaIDComponentes, boolean somenteAtivos, Map<Integer, String> cacheComponente) throws DAOException {
		Map<Integer, String> mapa = new Hashtable<Integer, String>();
		List<Integer> consultar = new ArrayList<Integer>();
		Iterator<Integer> iterator = listaIDComponentes.iterator();
		// verifica se o código já foi consultado anteriormente e está em cache
		if (cacheComponente != null) {
			while (iterator.hasNext()) {
				Integer id = iterator.next();
				String codigoCache = cacheComponente.get(id);
				// caso em cache, recupera o código e remove o ID da lista a consultar.
				if (codigoCache != null) {
					mapa.put(id, codigoCache);
				} else {
					consultar.add(id);
				}
			}
		} else {
			consultar.addAll(listaIDComponentes);
		}
		// consulta os IDs não recuperados no cache.
		if (!isEmpty(consultar)) {
			StringBuilder hql = new StringBuilder();
			hql.append("select id, codigo, detalhes.nome from ComponenteCurricular where id in " + UFRNUtils.gerarStringIn(consultar));
			if (somenteAtivos)
				hql.append(" and ativo = trueValue()");
			@SuppressWarnings("unchecked")
			List<Object[]> lista = getSession().createQuery(hql.toString()).list();
			if (!isEmpty(lista)) {
				for (Object[] res : lista) {
					mapa.put((Integer) res[0], (String) res[1]);
					// remove da lista o ID encontrado
					consultar.remove(res[0]);
				}
			}
			// se houver id que não foi encontrado, dispara uma exceção
			if (!consultar.isEmpty())
				throw new DAOException("ID(s) de Componente(s) Curricular(es) "+(somenteAtivos?"ativo":"")+" não encontrado(s): " + StringUtils.transformaEmLista(consultar));
		}

		return mapa;
	}

	/**
	 * Retorna um componente curricular populado com o código
	 * e o nome do componente cujo id foi passado como parâmetro.
	 * @param id
	 * @param somenteAtivos
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findByIds(Collection<ComponenteCurricular> componentes, boolean somenteAtivos) throws DAOException {
		String hql = "select cc from ComponenteCurricular cc where ativo = " + somenteAtivos + " and id in " + gerarStringIn(componentes);
		return getSession().createQuery(hql).list();
	}
	
	/**
	 * Retorna um componente curricular populado com o código
	 * e o nome do componente cujo id foi passado como parâmetro.
	 * @param id
	 * @param somenteAtivos
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricular findCodigoNomeById(int id, boolean somenteAtivos) throws DAOException {
		try {
			String codigoNome = cacheCodigoNome.get(id);
			if ( codigoNome == null ) {
				StringBuilder hql = new StringBuilder();
				hql.append("select codigo, detalhes.nome from ComponenteCurricular where id=" + id);
				if (somenteAtivos)
					hql.append(" and ativo = trueValue()");
				
				Object[] res = (Object[]) getSession().createQuery(hql.toString()).uniqueResult();
				if (!isEmpty(res)) {
					codigoNome = res[0]+","+res[1];
					cacheCodigoNome.put(id,codigoNome);
				}
			}
			ComponenteCurricular cc = new ComponenteCurricular();
			if (codigoNome != null) {
				StringTokenizer st = new StringTokenizer(codigoNome, ",");				
				cc.setCodigo(st.nextToken());
				// Foi modificado para pegar o nome do componente completo, quando tem vírgula.
				String nome = "";
				while (st.hasMoreElements())
					nome += (nome.equals("") ? "" : ",")+ st.nextToken();
				cc.setNome(nome);
			}

			return cc;

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Identifica se já existe um componente com o código informado na unidade
	 * e nível passados como parâmetro e que tenha id diferente do id passado como parâmetro. 
	 * @param id
	 * @param codigo
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public boolean jaExisteCodigo(int id, String codigo, int unidade, char nivel) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(ComponenteCurricular.class);
			c.setProjection(Projections.property("id"));
			c.add(Restrictions.ne("id", id));
			c.add(Restrictions.eq("nivel", nivel));
			c.createCriteria("detalhes").add(Restrictions.eq("codigo", codigo));
			return c.list() != null && c.list().size() > 0;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Lista os componentes curriculares oferecidos a um curso de pós-graduação
	 * lato sensu.
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByCursoLato(int idCurso) throws DAOException {
		Collection<ComponenteCurricular> disciplinas = new HashSet<ComponenteCurricular>();
		try {
			Criteria c = getSession().createCriteria(ComponenteCursoLato.class);
			c.add( Restrictions.eq("cursoLato.id", idCurso));
			c.createCriteria("disciplina").add(Restrictions.eq("ativo", Boolean.TRUE));
			c.addOrder(Order.asc("disciplina"));

			@SuppressWarnings("unchecked")
			Collection<ComponenteCursoLato> lista = c.list();
			
			for(ComponenteCursoLato ccl: lista){
				disciplinas.add( ccl.getDisciplina() );
			}
			return disciplinas;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Busca as alterações no status de ativo e o motivo de inativação de um componente curricular.
	 * @param cc
	 * @return
	 * @throws DAOException
	 */
	public Collection<AlteracaoAtivacaoComponente> findAlteracoesAtivacaoByComponente(ComponenteCurricular cc) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(AlteracaoAtivacaoComponente.class);
			c.add( Restrictions.eq("componenteCurricular", cc));
			c.addOrder(Order.desc("data"));
			
			@SuppressWarnings("unchecked")
			Collection<AlteracaoAtivacaoComponente> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca componentes curriculares de acordo com seu nome, código, nível de ensino,
	 * unidade, se está ativo ou não ou seu status de inativação (se for inativo).
	 * @param nome
	 * @param codigo
	 * @param nivelEnsino
	 * @param unidade
	 * @param ativo
	 * @param statusInativo
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ComponenteCurricular> findByStatus(String nome, String codigo, char nivelEnsino, int unidade, List<Integer> statusInativo) throws DAOException, LimiteResultadosException {
			StringBuffer hql = new StringBuffer();
			String projecao = "select new ComponenteCurricular(id, codigo, detalhes.nome, ativo, statusInativo)";
			hql.append(" from ComponenteCurricular where 1=1");
			if (unidade > 0)
				hql.append(" and unidade.id = " + unidade );
			if (nivelEnsino != ' ')
				hql.append(" and nivel = '" + nivelEnsino + "' ");
			if (nome != null )
				hql.append(" and " + UFRNUtils.toAsciiUpperUTF8("detalhes.nome") + "" +
						" like " + UFRNUtils.toAsciiUpperUTF8("'%" + nome.toUpperCase().trim()
						+ "%'") );
			if (codigo != null)
				hql.append(" and detalhes.codigo = '" + codigo + "' ");
			if (statusInativo != null) {
				StringBuffer sb = new StringBuffer();
				for (Integer s : statusInativo) {
					sb.append(" statusInativo = " + s + " or ");
				}
				if (sb.length() > 0) {
					sb.delete(sb.lastIndexOf(" or "), sb.length());
					hql.append(" and ("+sb+")");
				}
			}
			String orderBy = " order by detalhes.nome asc";

			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = getSession().createQuery(projecao +  hql + orderBy).list();
			return lista;

	}

	/**
	 * Busca atividades de acordo com seu nome, código, nível de ensino,
	 * unidade, se está ativo ou não ou seu status de inativação (se for inativo).
	 * 
	 * @param nome
	 * @param codigo
	 * @param nivelEnsino
	 * @param unidade
	 * @param ativo
	 * @param statusInativo
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public Collection<ComponenteCurricular> findAtividadesByStatus(String nome, String codigo, char nivelEnsino, Unidade unidade, List<Integer> statusInativo) throws DAOException, LimiteResultadosException {
			StringBuffer hql = new StringBuffer();
			String projecao = "select distinct new ComponenteCurricular(c.id, c.codigo, c.detalhes.nome, c.ativo, c.statusInativo)";
			String count = "select count(distinct c.id)";
			hql.append(" from ComponenteCurricular as c ");
			hql.append(" where c.tipoComponente.id in " + gerarStringIn(new int[] {TipoComponenteCurricular.ATIVIDADE}));
			if (unidade != null)
				hql.append(" and c.unidade.id = " + unidade.getId() );
			if (nivelEnsino != ' ')
				hql.append(" and c.nivel = '" + nivelEnsino + "' ");
			if (nome != null )
				hql.append(" and " + UFRNUtils.toAsciiUpperUTF8("c.detalhes.nome") + "" +
						" like " + UFRNUtils.toAsciiUpperUTF8("'%" + nome.toUpperCase().trim()
						+ "%'") );
			if (codigo != null)
				hql.append(" and c.codigo = '" + codigo + "' ");
			if ( !isEmpty( statusInativo ) ) {
				int[] arr = new int[statusInativo.size()];
				for (int i = 0; i  <arr.length; i++) {
					arr[i] = statusInativo.get(i);
				}
				hql.append(" and c.statusInativo in " + gerarStringIn(arr));
			}
			String orderBy = " order by c.detalhes.nome asc";

			Query q = getSession().createQuery(count +  hql);
			Long qtd = (Long) q.uniqueResult();
			if (qtd > 100)
				throw new LimiteResultadosException();

			@SuppressWarnings("unchecked")
			Collection<ComponenteCurricular> lista = getSession().createQuery(projecao +  hql + orderBy).list();
			return lista;
	}

	/**
	 * Busca os componentes curriculares oferecidos a cursos de ensino a distância.
	 * É possível filtrar por curso.
	 * @return lista de componentes curriculares de cursos a distância
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findComponentesEnsinoADistancia(Curso curso) throws DAOException {
			
		StringBuilder hql = new StringBuilder("SELECT DISTINCT cc ");
		hql.append( " FROM Curso c, MatrizCurricular mc, Curriculo cu, CurriculoComponente cuc, ComponenteCurricular cc " );
		hql.append( " WHERE mc.curso.id = c.id and cu.matriz.id = mc.id and cuc.curriculo.id = cu.id and cuc.componente.id = cc.id " );
		hql.append( " AND c.modalidadeEducacao.id =  " + ModalidadeEducacao.A_DISTANCIA );
		if( curso != null )
			hql.append( " AND c.id = :idCurso " );
		hql.append( " AND cc.ativo = trueValue() " );
		
		Query q = getSession().createQuery(hql.toString());
		if( curso != null )
			q.setInteger("idCurso", curso.getId());
		
		@SuppressWarnings("unchecked")
		List<ComponenteCurricular> lista = q.list();
		return lista;
	}


	/**
	 * Busca a associação do componente curricular passado como parâmetro com um
	 * currículo. 
	 * @param componente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<CurriculoComponente> findCurriculosComponente(ComponenteCurricular componente) throws DAOException {

		StringBuilder hqlProjecao = new StringBuilder(" cc.id, cc.obrigatoria, cc.semestreOferta,  ");
		hqlProjecao.append("  area.id, area.denominacao, cur.id, ");
		hqlProjecao.append("  cur.codigo, cur.anoEntradaVigor, cur.periodoEntradaVigor, ma.id, c.id, c.nome, ");
		hqlProjecao.append("  h.id, h.nome, e.id, e.nome, ga.id, ga.descricao, me.id, me.descricao, t.id, t.sigla, t.descricao, c.nome, mu.nome ");
			
		StringBuilder hql = new StringBuilder("SELECT");
		hql.append( hqlProjecao );
		hql.append(" FROM CurriculoComponente cc LEFT JOIN cc.areaConcentracao area");
		hql.append(" INNER JOIN cc.componente co INNER JOIN cc.curriculo cur ");
		hql.append(" INNER JOIN cur.matriz ma INNER JOIN ma.curso c ");
		hql.append(" LEFT JOIN c.municipio mu LEFT JOIN c.modalidadeEducacao me ");
		hql.append(" LEFT JOIN ma.habilitacao h LEFT JOIN ma.enfase e ");
		hql.append(" LEFT JOIN ma.turno t LEFT JOIN ma.grauAcademico ga ");
		hql.append(" WHERE co.id = ? and c.ativo = trueValue() ");
		
		
		Query q = getSession().createQuery( hql.toString() );
		q.setInteger(0, componente.getId());
		List<Object[]> lista = q.list();
		
		List<CurriculoComponente> curriculos = new ArrayList<CurriculoComponente>();
		
		for (Object[] object : lista) {
			
			int i = 0;
			CurriculoComponente c = new CurriculoComponente();
			
			c.setId((Integer) object[i++]);
			c.setObrigatoria((Boolean) object[i++]);
			c.setSemestreOferta((Integer) object[i++]);
			
			Integer idArea = (Integer) object[i++];
			String descricaoArea = (String) object[i++];
			
			if( !isEmpty(idArea) ){
				c.setAreaConcentracao( new AreaConcentracao() );
				c.getAreaConcentracao().setId( idArea );
				c.getAreaConcentracao().setDenominacao( descricaoArea );
			}
			
			c.setCurriculo( new Curriculo( (Integer) object[i++] ) );
			c.getCurriculo().setCodigo( (String) object[i++] );
			c.getCurriculo().setAnoEntradaVigor( (Integer) object[i++] );
			c.getCurriculo().setPeriodoEntradaVigor( (Integer) object[i++] );
			
			c.getCurriculo().setMatriz( new MatrizCurricular( (Integer) object[i++] ) );
			c.getCurriculo().getMatriz().setCurso(new Curso( (Integer) object[i++] ));
			c.getCurriculo().getMatriz().getCurso().setNome( (String) object[i++] );
			
			Integer idModalidade = (Integer) object[i++];
			String descricaoModalidade = (String) object[i++];
			if( !isEmpty(idModalidade) ){
				c.getCurriculo().getMatriz().getCurso().setModalidadeEducacao(
					new ModalidadeEducacao(idModalidade, descricaoModalidade) );
			}
			
			Integer idHabilitacao = (Integer) object[i++];
			String descricaoHabilitacao = (String) object[i++];
			if( !isEmpty(idHabilitacao) ){
				c.getCurriculo().getMatriz().setHabilitacao( new Habilitacao( idHabilitacao , descricaoHabilitacao ) );
			}
			
			Integer idEnfase = (Integer) object[i++];
			String descricaoEnfase= (String) object[i++];
			if( !isEmpty(idEnfase) ){
				c.getCurriculo().getMatriz().setEnfase(new Enfase());
				c.getCurriculo().getMatriz().getEnfase().setId( idEnfase );
				c.getCurriculo().getMatriz().getEnfase().setNome( descricaoEnfase );
			}
			
			Integer idGrau = (Integer) object[i++];
			String descricaoGrau = (String) object[i++];
			if( !isEmpty(idGrau) ){
				c.getCurriculo().getMatriz().setGrauAcademico( new GrauAcademico(idGrau ,  descricaoGrau ) );
			}
			
			Integer idTurno = (Integer) object[i++];
			String siglaTurno = (String) object[i++];
			String descricaoTurno = (String) object[i++];
			if( !isEmpty(idTurno) ){
				c.getCurriculo().getMatriz().setTurno( new Turno(idTurno ,  descricaoTurno ) );
				c.getCurriculo().getMatriz().getTurno().setSigla( siglaTurno );
			}
						
			c.getCurriculo().setCurso(new Curso());
			c.getCurriculo().getCurso().setNome((String) object[i++]);
			c.getCurriculo().getCurso().setMunicipio(new Municipio());
			c.getCurriculo().getCurso().getMunicipio().setNome((String) object[i++]);
			
			curriculos.add(c);
		}
		
		return curriculos;

	}


	/**
	 * Busca os componentes curriculares que estão sendo oferecidos a um curso
	 * em um determinado ano-período.
	 * @param curso
	 * @param ano
	 * @param periodo
	 * @return
	 */
	public List<ComponenteCurricular> findComponentesPorCursoGraduacao(Curso curso, int ano, int periodo, boolean ead) {

		StringBuilder sql = new StringBuilder(
			" select distinct cc.id_disciplina, ccd.codigo, ccd.nome, ccd.ch_total, ccd.ementa" +
			" from graduacao.curriculo_componente cuc, ensino.componente_curricular cc," +
			" ensino.componente_curricular_detalhes ccd, ensino.turma t, graduacao.curriculo cu " +
			" inner join curso c using (id_curso) " +
			" where cc.ativo = trueValue() and cu.id_curso=? ");	

			if (ead) 
					sql.append(" and c.id_modalidade_educacao in " + gerarStringIn(new int[]{ModalidadeEducacao.PRESENCIAL, ModalidadeEducacao.A_DISTANCIA}));

			sql.append(
			" and cu.id_curriculo = cuc.id_curriculo and cuc.id_componente_curricular = cc.id_disciplina " +
			" and cc.id_detalhe = ccd.id_componente_detalhes and cc.id_disciplina = t.id_disciplina " +
			" and t.ano = ? and t.periodo = ? and ccd.ementa is not null");
			
		@SuppressWarnings("unchecked")
		List<ComponenteCurricular> lista = getJdbcTemplate().query(sql.toString(), new Object[] { curso.getId(), ano, periodo }, new RowMapper() {

			public Object mapRow(ResultSet rs, int row) throws SQLException {
				int id = rs.getInt("id_disciplina");
				String nome = rs.getString("nome");
				String codigo = rs.getString("codigo");
				int chTotal = rs.getInt("ch_total");
				
				ComponenteDetalhes compDetalhes = new ComponenteDetalhes();
				compDetalhes.setEmenta(rs.getString("ementa"));
				compDetalhes.setNome(rs.getString("nome"));
				
			    ComponenteCurricular cc = new ComponenteCurricular(id, codigo, nome, chTotal, NivelEnsino.GRADUACAO);
			    cc.setDetalhes(compDetalhes);
			    
				return cc;
			}

		});
		return lista;
	}


	/**
	 * Busca todos os componentes curriculares de graduação que podem ter turmas criadas.
	 * 
	 * @param parametro
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findAllGraduacaoPassiveisTurma(String parametro) throws DAOException {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append( "select cc.id, cc.detalhes.codigo, cc.detalhes.nome, cc.detalhes.chTotal, cc.nivel, cc.blocoSubUnidade.id" );
			hql.append(" from ComponenteCurricular cc ");
			hql.append(" where cc.nivel = '" + NivelEnsino.GRADUACAO + "'");
			hql.append(" and cc.tipoComponente.id != " + TipoComponenteCurricular.ATIVIDADE);
			hql.append(" and cc.ativo = trueValue()");

			if ( parametro != null ) {
				hql.append(" and (");
				hql.append("upper(cc.detalhes.nome_ascii)" + " like "+"'%"+StringUtils.toAscii(parametro.toUpperCase().trim())+ "%'");
				hql.append(" or cc.codigo = "+"'"+StringUtils.toAscii(parametro.toUpperCase().trim())+ "'");
				hql.append(" )");
			}

			hql.append(" order by detalhes.nome asc");

			Query q = getSession().createQuery(hql.toString());

			Collection<ComponenteCurricular> retorno = new ArrayList<ComponenteCurricular>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			
			for (Object[] object : lista) {
				Integer id = (Integer) object[0];
				String name = (String) object[1];
				String cod = (String) object[2];
				Integer ch = (Integer) object[3];
				char nivel =  (Character) object[4];
				Integer sub = (Integer) object[5];
				ComponenteCurricular cc;
				if( sub == null )
					cc = new ComponenteCurricular( id, name, cod, ch, nivel);
				else
					cc = new ComponenteCurricular( id, name, cod, ch, nivel, sub );
				retorno.add(cc);
			}

			return retorno;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Retorna o próximo código disponível para o cadastro
	 * de componentes curriculares de acordo com a unidade responsável
	 * pelo componente e o nível de ensino.
	 * @param unidade
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public String findCodigoDisponivel(int unidade , char nivel, Integer tamLetrasCodigo) throws DAOException {
		StringBuffer hql = new StringBuffer();
		
		int qtdLetrasCodigo = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.QTD_LETRAS_CODIGO_COMPONENTE);
		int tamanhoCodigo = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.TAMANHO_CODIGO_COMPONENTE);
		
		int qtdLetrasCodigoFiltro = (tamLetrasCodigo != null ? tamLetrasCodigo : qtdLetrasCodigo) + 1;
		int tamanhoCodigoFiltro = tamanhoCodigo - 1;
		
		hql.append("SELECT substring(codigo, " + qtdLetrasCodigoFiltro + ", " + tamanhoCodigoFiltro + ") as COD FROM ComponenteCurricular  " +
				"WHERE nivel=:nivel and unidade.id=:unidade ORDER BY substring(codigo, " + qtdLetrasCodigoFiltro + ", " + tamanhoCodigoFiltro + ")");
		Query q = getSession().createQuery(hql.toString());
		q.setCharacter("nivel", nivel);
		q.setInteger("unidade", unidade);
		
		@SuppressWarnings("unchecked")
		List<String> res = q.list();
		
		ArrayList<Integer> codigos = new ArrayList<Integer>(0);
		for (String c : res) {
			try {
				codigos.add(new Integer(c));
			} catch (Exception e) {
				// silenciado
			}
		}
		Collections.sort(codigos);
		Integer numDisponivel = 1;
		if (codigos != null) {
			for (int i = 0; i <  codigos.size(); i++) {
				int num = codigos.get(i);
				// verifica se o próximo número não é exatamente o atual incrementado
				if (i == codigos.size() -1) {
					numDisponivel = num + 1;
				} else if (num + 1 != codigos.get(i + 1) && num != codigos.get(i + 1)) {
					numDisponivel = num + 1;
					break;
				}
			}
		}

		return UFRNUtils.completaZeros(numDisponivel, tamanhoCodigo - qtdLetrasCodigo);
	}
	
	/**
	 * Retorna a expressão de equivalência do componente curricular 
	 * cujo id foi passado como parâmetro. 
	 * @param id
	 * @return
	 * @throws ArqException 
	 */
	public ComponenteCurricular findEquivalenciaPorDiscente(Integer idComponente, Integer idDiscente) throws ArqException {
		DiscenteDao ddao = new DiscenteDao();
		MatriculaComponenteDao mcdao = new MatriculaComponenteDao();
		try{
		ddao.setSession(getSession());
		
		mcdao.setSession(getSession());
		
		DadosCalculosDiscente dados = RepositorioInformacoesCalculoDiscente.INSTANCE.buscarInformacoes(idDiscente);
		DiscenteAdapter discente = new Discente(idDiscente);
		
		List<ComponenteCurricular> ccs = new ArrayList<ComponenteCurricular>();	
		ComponenteCurricular componenteCurricular = new ComponenteCurricular(idComponente);
		ccs.add(componenteCurricular);
		
		Map<Integer, List<Object[]>> mapaEquivalencias = findEquivalenciasComponentesByIntervaloDatas(ccs, dados.getCurriculo().getId(), dados.getDataInicio(), dados.getDataFim());
		List<Object[]> infoEquivalencias = mapaEquivalencias.get(idComponente);
		Collection<ComponenteCurricular> componentesPagosEMatriculados = ddao.findComponentesCurriculares(discente,SituacaoMatricula.getSituacoesPagasEMatriculadas(),null);
		Collection<MatriculaComponente> matriculas = mcdao.findPagaseMatriculadasByDiscente(discente);
		
		
		List<String> equivalencias = analisaEquivalencias(ddao, componentesPagosEMatriculados, matriculas, componenteCurricular, infoEquivalencias);
			
		
		for (int i=0; i<equivalencias.size(); i++) {  
		   String eq = equivalencias.get(i);  
		   if (eq != null && eq.contains(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL)) {  
			   ComponenteCurricular componente = findByPrimaryKey(idComponente, ComponenteCurricular.class);
			   if( componente.getEquivalencia() != null ){
				   equivalencias.set(i, eq.replaceAll(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL, componente.getEquivalencia()));
			   }
		   }  
		}  
		
		for (Iterator<String> it = equivalencias.iterator(); it.hasNext();) {
			String str = it.next();
			if (str == null)
				it.remove();
		}
		
		String equivalencia = ExpressaoUtil.juntarExpressoes(equivalencias.toArray(new String[equivalencias.size()]));

		ComponenteCurricular cc = componenteCurricular;
		cc.setEquivalencia(equivalencia);
		return cc;
		} finally {
			if (ddao != null) ddao.close();
			if (mcdao != null) mcdao.close();
			
		}
	}

	/**
	 * Analisa as equivalências do componente, retornando somente aquelas que são válidas para o discente, ou seja,
	 * as que o discente se matriculou quando a expressão de equivalência ainda era válida.
	 * @param ddao
	 * @param componentesPagosEMatriculados
	 * @param matriculas
	 * @param ccPendente
	 * @param equivalencias
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	private List<String> analisaEquivalencias(DiscenteDao ddao, Collection<ComponenteCurricular> componentesPagosEMatriculados, Collection<MatriculaComponente> matriculas, ComponenteCurricular ccPendente, List<Object[]> equivalencias) throws DAOException, ArqException {
		List<String> equivalenciasValidasNaDataMatricula = new ArrayList<String>();
		if (!isEmpty(equivalencias)) {
			for (Object[] infoEquivalencia : equivalencias) {
				String equivalencia = (String) infoEquivalencia[0];
				Integer idEspecifica = (Integer) infoEquivalencia[1];
				Date fimVigenciaEquivalencia = (Date) infoEquivalencia[3];
				EquivalenciaEspecifica especifica = null;
				if (!isEmpty(idEspecifica))
					especifica = ddao.findByPrimaryKey(idEspecifica, EquivalenciaEspecifica.class);
				
				if (equivalencia != null && ExpressaoUtil.eval(equivalencia, ccPendente, componentesPagosEMatriculados)) {
					Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, ccPendente, componentesPagosEMatriculados);
					if (equivalentes != null) {
						int qtdeMatEquivalenciasCadastradas = 0;
						for (ComponenteCurricular componente : equivalentes) {
							MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, componente);
							boolean componenteEquivalente = !isEmpty(mat);
							// Existem casos onde matricula pode ser null. 
							// Por exemplo, se estiver validando apenas um conjunto de matrículas (matriculas do semestre por exemplo)
							// o componente equivalente pago pode não estar nesse pequeno conjunto de matrículas. 
							// Mas se estiver analisando todas as matrículas do discente, ai sim não deveria ser null.
							if (mat != null) {
								// Verifica se a equivalência que foi encontrada é do tipo específica e estava valendo na data da matrícula

								if (((componenteEquivalente && IntegralizacoesHelper.isEquivalenciaValendoNaDataMatricula(mat, null, fimVigenciaEquivalencia)) && especifica == null) 
										|| (especifica != null && especifica.isEquivalenciaValendoNaData(mat.getDataCadastro()))) {
									
									++qtdeMatEquivalenciasCadastradas;
								}
							}
						}
						if ( equivalentes.size() == qtdeMatEquivalenciasCadastradas ){
							equivalenciasValidasNaDataMatricula.add(equivalencia);
							break;
						}
					}									
				}
			}
		}
		return equivalenciasValidasNaDataMatricula;
	}
	
	/**
	 * Retorna a expressão de equivalência do componente curricular 
	 * cujo id foi passado como parâmetro. 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public ComponenteCurricular findEquivalencia(Integer id) throws DAOException {
		String equivalencia = null;
		if ( equivalenciasCache.get(id) == null ) {
			equivalencia = (String) getSession().createQuery("select ccd.equivalencia from ComponenteCurricular cc left join cc.detalhes ccd where cc.ativo = trueValue() and cc.id = " + id).uniqueResult();
			if ( equivalencia != null )
				equivalenciasCache.put(id,equivalencia);
			else
				equivalenciasCache.put(id,"");
		}  else {
			equivalencia = equivalenciasCache.get(id);
		}
		ComponenteCurricular cc = new ComponenteCurricular(id);
		cc.setEquivalencia(equivalencia);
		return cc;
	}

	/**
	 * Busca os detalhes de um componente curricular de acordo com o id
	 * do componente;
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteDetalhes> findDetalhes(int id) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT id, data, registroEntrada.usuario.pessoa.nome, registroEntrada.usuario.login" +
				" FROM ComponenteDetalhes " +
				" WHERE componente = :comp");
		hql.append(" order by data desc");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("comp", id);
		Collection<ComponenteDetalhes> detalhes = new ArrayList<ComponenteDetalhes>(0);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		for (Object[] object : lista) {
			int i = 0;
			ComponenteDetalhes det = new ComponenteDetalhes();
			det.setId((Integer) object[i++]);
			det.setData((Date) object[i++]);
			det.setRegistroEntrada(new RegistroEntrada());
			det.getRegistroEntrada().setUsuario(new Usuario());
			det.getRegistroEntrada().getUsuario().setPessoa(new PessoaGeral());
			det.getRegistroEntrada().getUsuario().getPessoa().setNome((String) object[i++]);
			det.getRegistroEntrada().getUsuario().setLogin((String) object[i++]);
			detalhes.add(det);
		}

		return detalhes;
	}

	/**
	 * Retorna todos os componentes curriculares que estão com o boolean turmasSemSolicitacao
	 * marcados como true da unidade informada no parâmetro.
	 * 
	 * @param idUnidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findComponentesSemSolicitacao( int idUnidade ) throws DAOException{

		StringBuffer hql = new StringBuffer();
		hql.append("SELECT new ComponenteCurricular( cc.id, cc.detalhes.codigo, cc.detalhes.nome, " +
				" cc.detalhes.chTotal, cc.detalhes.crAula + cc.detalhes.crEstagio + cc.detalhes.crLaboratorio, " +
				" cc.nivel, cc.tipoComponente.descricao )" +
				" FROM ComponenteCurricular cc " +
				" WHERE cc.unidade.id = :idUnidade" +
				" AND cc.ativo = trueValue() " +
				" AND cc.turmasSemSolicitacao = trueValue() ");

		hql.append(" order by cc.detalhes.nome");

		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idUnidade", idUnidade);

		@SuppressWarnings("unchecked")
		Collection<ComponenteCurricular> lista = q.list();
		return lista;
	}
	
	/**
	 * Busca os componentes curriculares de acordo com a unidade
	 * do componente curricular
	 * @param departamento
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<ComponenteCurricular> findResumoComponentes(Unidade unidade) throws DAOException {
		Collection<ComponenteCurricular> componentes =  new ArrayList<ComponenteCurricular>();

		try {
			StringBuffer hql = new StringBuffer();
			String projecao = "cc.id, cc.nivel, cc.codigo, cc.detalhes.nome, cc.tipoComponente.descricao," +
				" cc.detalhes.chTotal, ta.descricao, tac.descricao";
			hql.append("SELECT " + projecao +
					" FROM ComponenteCurricular cc left join cc.tipoAtividade ta left join cc.tipoAtividadeComplementar tac  " +
					" WHERE cc.ativo = trueValue() and cc.unidade.id = :idUnidade " +
					" ORDER BY cc.tipoComponente.descricao,cc.detalhes.nome ");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idUnidade", unidade.getId() );

			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			
			for (Object[] object : result) {
				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setId( (Integer) object[0] );
				cc.setNivel((Character) object[1]);
				cc.setCodigo((String) object[2]);
				
				ComponenteDetalhes det = new ComponenteDetalhes();
				det.setNome((String) object[3]);
				cc.setDetalhes(det);
				cc.setChTotal((Integer) object[5]);

				TipoComponenteCurricular tipo = new TipoComponenteCurricular();
				tipo.setDescricao((String) object[4]);
				cc.setTipoComponente(tipo);
				cc.setTipoAtividade(new TipoAtividade());
				cc.getTipoAtividade().setDescricao((String) object[6]);
				cc.setTipoAtividadeComplementar(new TipoAtividadeComplementar());
				cc.getTipoAtividadeComplementar().setDescricao((String) object[7]);

				componentes.add(cc);
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

		return componentes;
	}

	/**
	 * Busca os programas de componentes curriculares de acordo com a unidade
	 * do componente curricular e o ano-período do programa.
	 * @param departamento
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public Collection<ComponenteCurricular> findResumoComponentesPrograma(
			Unidade unidade, Integer ano, Integer periodo) throws DAOException {
		Collection<ComponenteCurricular> componentes =  new ArrayList<ComponenteCurricular>();

		try {
			StringBuffer hql = new StringBuffer();
			String projecao = "cc.id, cc.nivel, cc.codigo, cc.detalhes.nome, " +
				" cc.tipoComponente.descricao, programa.id, programa.ano, programa.periodo," +
				" cc.detalhes.chTotal, ta.descricao, tac.descricao";
			hql.append("SELECT " + projecao +
					" FROM ComponenteCurricular cc left join cc.programa as programa " +
					" left join cc.tipoAtividade ta left join cc.tipoAtividadeComplementar tac  " +
					" WHERE cc.ativo = trueValue() and cc.unidade.id = :idUnidade " +
					" AND programa.ano = " + ano + " AND programa.periodo = " + periodo +
					" ORDER BY cc.tipoComponente.descricao, cc.programa.ano, cc.programa.periodo, cc.detalhes.nome ");

			Query q = getSession().createQuery(hql.toString());
			q.setInteger("idUnidade", unidade.getId() );

			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			
			for (Object[] object : result) {
				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setId( (Integer) object[0] );
				cc.setNivel((Character) object[1]);
				cc.setCodigo((String) object[2]);
				
				ComponenteDetalhes det = new ComponenteDetalhes();
				det.setNome((String) object[3]);
				cc.setDetalhes(det);
				
				cc.setChTotal((Integer) object[8]);
					
				

				TipoComponenteCurricular tipo = new TipoComponenteCurricular();
				tipo.setDescricao((String) object[4]);
				cc.setTipoComponente(tipo);

				Integer idPrograma = (Integer) object[5];
				if (idPrograma != null) {
					ComponenteCurricularPrograma programa = new ComponenteCurricularPrograma();
					programa.setId(idPrograma);
					programa.setAno((Integer) object[6]);
					programa.setPeriodo((Integer) object[7]);
					cc.setPrograma(programa);
					cc.setTipoAtividade(new TipoAtividade());
					cc.getTipoAtividade().setDescricao((String) object[9]);
					cc.setTipoAtividadeComplementar(new TipoAtividadeComplementar());
					cc.getTipoAtividadeComplementar().setDescricao((String) object[10]);
				}

				componentes.add(cc);
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

		return componentes;
	}

	/**
	 * Busca componentes curriculares optativas de um currículo.
	 */
	public List<CurriculoComponente> findOptativasByCurriculo(Curriculo curriculo) throws DAOException {
		Criteria c = getSession().createCriteria(CurriculoComponente.class);
		c.add(eq("curriculo", curriculo)).add(eq("obrigatoria", false));
		c.createCriteria("componente").add(eq("ativo", true)).createCriteria("detalhes").addOrder(asc("nome"));
		
		@SuppressWarnings("unchecked")
		List<CurriculoComponente> lista = c.list();
		return lista;
	}


	/**
	 * Busca os componentes que contém o id passado por parâmetro em alguma das 
	 * expressões (equivalência, pré-requisito ou co-requisito).
	 * 
	 * @param idComponente id do componente referenciado
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findComponentesReferenciamExpressao(int idComponente) throws DAOException {
		StringBuffer hql = new StringBuffer( " SELECT new ComponenteCurricular(c.id, c.codigo, d.nome) " );
		hql.append( " FROM ComponenteCurricular c JOIN c.detalhes d " );
		hql.append( " WHERE c.ativo = trueValue() " );
		hql.append( " and (d.equivalencia like :idComponente or d.preRequisito like :idComponente or d.coRequisito like :idComponente) " );
		
		Query q = getSession().createQuery(hql.toString());
		q.setString("idComponente", "% " + idComponente + " %");
		
		@SuppressWarnings("unchecked")
		List<ComponenteCurricular> lista = q.list();
		return lista;
	}
	
	/**
	 * Busca os componentes que contém o id passado por parâmetro na 
	 * expressão de co-requisito.
	 * 
	 * @param idComponente id do componente referenciado
	 * @return
	 * @throws DAOException
	 */
	public List<ComponenteCurricular> findComponentesReferenciamExpressaoCoRequisito(List<Integer> idsComponentes) throws DAOException {
		StringBuffer hql = new StringBuffer( " SELECT new ComponenteCurricular(c.id, c.codigo, d.nome) " );
		hql.append( " FROM ComponenteCurricular c JOIN c.detalhes d " );
		hql.append( " WHERE c.ativo = trueValue() " );
		hql.append( " and c.id not in " + UFRNUtils.gerarStringIn(idsComponentes) );
		hql.append( " and ( ");
		for(Integer id: idsComponentes)
			hql.append( " d.coRequisito like '% "+ id + " %' or");
		hql.delete(hql.length() - 2, hql.length());
		hql.append(")");
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<ComponenteCurricular> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna o crédito total da disciplina informada no parâmetro.
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Integer findCrTotalByIdComponente(int id) throws DAOException{

		StringBuilder hql = new StringBuilder( " SELECT ccd.crAula + ccd.crLaboratorio + ccd.crEstagio " );
		hql.append( " FROM ComponenteCurricular cc JOIN cc.detalhes ccd " );
		hql.append( " WHERE cc.ativo = trueValue() and cc.id = :idComponente" );
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idComponente", id);
		
		return (Integer) q.uniqueResult();
		
	}

	/**
	 * Identifica se o componente pertence a algum dos currículos passados como parâmetro.
	 * @param componente
	 * @param curriculos
	 * @return lista de ID do(s) currículo(s) ao qual o componente curricular pertence
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<Integer> componentePertenceCurriculos(ComponenteCurricular componente, Collection<Curriculo> curriculos) throws HibernateException, DAOException {
		String sql = "select id_curriculo" +
				" from graduacao.curriculo_componente" +
				" where id_componente_curricular = " + componente.getId() +
				" and id_curriculo in " + UFRNUtils.gerarStringIn(curriculos);
		Query q = getSession().createSQLQuery(sql);
		@SuppressWarnings("unchecked")
		List<Integer> lista = q.list();
		return lista;
	}

	/**
	 * Localiza as disciplinas pelo código
	 * 
	 * @param codigos
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findByCodigos(String[] codigos) throws DAOException {
		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createQuery("select cc.id, cc.codigo, cc.ativo, cc.statusInativo from ComponenteCurricular cc where cc.codigo in " + gerarStringIn(codigos)).list();
		
		Collection<ComponenteCurricular> resultado = new ArrayList<ComponenteCurricular>();
		for (int i = 0; i < list.size(); i++) {
			int col  = 0;
			Object[] colunas = list.get(i);

			ComponenteCurricular cc = new ComponenteCurricular();
			cc.setId( (Integer) colunas[col++]);
			cc.setCodigo( (String) colunas[col++] );
			cc.setAtivo( (Boolean) colunas[col++] );
			cc.setStatusInativo((Integer) colunas[col++] );
			
			resultado.add(cc);
		}
		return resultado;
	}
	

	/**
	 * Retorna a lista de equivalências que um conjunto de componentes curriculares teve de acordo
	 * com o discente passado como parâmetro. Inclui equivalências específicas.
	 * @param pendentesObrigatoriaCurriculo
	 * @param primeiraData
	 * @param ultimaData
	 * @return
	 */
	public Map<Integer, List<Object[]>> findEquivalenciasComponentesByDiscente(Collection<ComponenteCurricular> componentes, Discente discente) throws DAOException {
		DadosCalculosDiscente dados = RepositorioInformacoesCalculoDiscente.INSTANCE.buscarInformacoes(discente.getId());
		Collection<CurriculoComponente> ccs = new ArrayList<CurriculoComponente>();
		
		for (ComponenteCurricular comp : componentes) {
			ccs.add(new CurriculoComponente(comp));
		}
		
		return findEquivalenciasComponentesByIntervaloDatas(ccs, dados.getCurriculo().getId(), dados.getDataInicio(), dados.getDataFim());
	}
	
	/**
	 * Retorna a lista de equivalências que um componente curricular teve em um intervalo de datas,
	 * incluindo as equivalências específicas.
	 * @param pendentesObrigatoriaCurriculo
	 * @param primeiraData
	 * @param ultimaData
	 * @return
	 */
	public Map<Integer, List<Object[]>> findEquivalenciasComponentesByIntervaloDatas(Collection<CurriculoComponente> ccs, int idCurriculo, Date primeiraData, Date ultimaData) {
		if (isEmpty(ccs)) return null;
		List<ComponenteCurricular> componentes = new ArrayList<ComponenteCurricular>();
		for (CurriculoComponente curriculoComponente : ccs) {
			componentes.add(curriculoComponente.getComponente());
		}
		
		return findEquivalenciasComponentesByIntervaloDatas(componentes, idCurriculo, primeiraData, ultimaData);
	}
	
	/**
	 * Retorna a lista de equivalências que um componente curricular teve em um intervalo de datas,
	 * incluindo as equivalências específicas.
	 * @param pendentesObrigatoriaCurriculo
	 * @param primeiraData
	 * @param ultimaData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, List<Object[]>> findEquivalenciasComponentesByIntervaloDatas(List<ComponenteCurricular> ccs, int idCurriculo, Date primeiraData, Date ultimaData) {
		if (isEmpty(ccs)) return null;
		
		String sql = "select distinct id_componente, equivalencia, especifica, data , fim_vigencia from " 
			+ "((select id_componente, equivalencia, null as especifica, ccd.data as data , equivalencia_valida_ate as fim_vigencia from ensino.componente_curricular_detalhes ccd where desconsiderar_equivalencia = false and id_componente_detalhes in " 
			+ "((select id_componente_detalhes from ensino.componente_curricular_detalhes where id_componente = ccd.id_componente and ( date_trunc('day', data) >= ? and date_trunc('day', data) <= ?)) " 
			+ "union (select id_componente_detalhes from ensino.componente_curricular_detalhes where id_componente = ccd.id_componente and date_trunc('day', data) <= ? order by data desc limit 1) "
			+ "union (select id_componente_detalhes from ensino.componente_curricular_detalhes where id_componente = ccd.id_componente and date_trunc('day', data) is null and not exists " 
			+ "(select id_componente_detalhes from ensino.componente_curricular_detalhes where id_componente = ccd.id_componente and date_trunc('day', data) < ? order by data desc limit 1))) "
			+ "and ccd.id_componente in " + gerarStringIn(ccs) + ") union (select id_componente_curricular, expressao, id as especifica, inicio_vigencia as data, fim_vigencia from ensino.equivalencia_especifica where ativo = true and id_curriculo = ? and id_componente_curricular in " + gerarStringIn(ccs) +" )) as eq "
			+ "order by id_componente asc, data desc"; 
			
		primeiraData = CalendarUtils.descartarHoras(primeiraData);
		ultimaData = CalendarUtils.descartarHoras(ultimaData);
		
		return (Map<Integer, List<Object[]>>) getJdbcTemplate().query(sql, new Object[] { primeiraData, ultimaData, primeiraData, primeiraData, idCurriculo }, new ResultSetExtractor() {
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
				Map<Integer, List<Object[]>> result = new HashMap<Integer, List<Object[]>>();
				
				while(rs.next()) {
					Integer idComponente = (Integer) rs.getObject(1);
					String equivalencia = (String) rs.getObject(2);
					Integer especifica = (Integer) rs.getObject(3);
					Date dataInicioEquivalencia = (Date) rs.getObject(4);
					Date dataFimEquivalencia = (Date) rs.getObject(5);
					
					if (result.get(idComponente) == null)
						result.put(idComponente, new ArrayList<Object[]>());
					result.get(idComponente).add(new Object[] { equivalencia, especifica, dataInicioEquivalencia, dataFimEquivalencia });
				}
				
				return result;
			}
		});
	}	
	
	/**
	 * Retorna os componentes Curriculares cujo os programas apresentam dados incompletos.
	 * 
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteCurricular> findProgramasIncompletosByNivel(
			Unidade unidade, Character nivel, Integer ano, Integer periodo) throws DAOException {
		Collection<ComponenteCurricular> componentes =  new ArrayList<ComponenteCurricular>();
		Collection<ComponenteCurricular> componentesProgramasIncompletos =  new ArrayList<ComponenteCurricular>();

		try {
			StringBuffer hql = new StringBuffer();
			String projecao = " cc.id, cc.nivel, cc.codigo, cc.detalhes.nome," +
				" cc.tipoComponente.descricao, programa.id, programa.ano, programa.periodo," +
				" programa.objetivos, programa.conteudo, programa.competenciasHabilidades," +
				" programa. metodologia, programa.procedimentosAvaliacao, programa.referencias," +
				" cc.unidade.id, cc.unidade.nome ";
			hql.append("SELECT " + projecao +
					" FROM ComponenteCurricular cc inner join cc.programa as programa " +
					" WHERE cc.ativo = trueValue() " +
					" AND cc.nivel = :nivel ");
			if(unidade != null && unidade.getId() > 0){ 
				hql.append(" AND cc.unidade.id = "+ unidade.getId());
			}
			hql.append(" ORDER BY cc.unidade.nome, cc.codigo, cc.detalhes.nome, programa.ano, cc.programa.periodo ");

			Query q = getSession().createQuery(hql.toString());
			q.setCharacter("nivel", nivel );

			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			
			for (Object[] object : result) {
				ComponenteCurricular cc = new ComponenteCurricular();
				cc.setId( (Integer) object[0] );
				cc.setNivel((Character) object[1]);
				cc.setCodigo((String) object[2]);
				
				ComponenteDetalhes det = new ComponenteDetalhes();
				det.setNome((String) object[3]);
				cc.setDetalhes(det);
				
				
				TipoComponenteCurricular tipo = new TipoComponenteCurricular();
				tipo.setDescricao((String) object[4]);
				cc.setTipoComponente(tipo);

				Integer idPrograma = (Integer) object[5];
				if (idPrograma != null) {
					ComponenteCurricularPrograma programa = new ComponenteCurricularPrograma();
					programa.setId(idPrograma);
					programa.setAno((Integer) object[6]);
					programa.setPeriodo((Integer) object[7]);
					programa.setObjetivos((String) object[8]);
					programa.setConteudo((String) object[9]);
					programa.setCompetenciasHabilidades((String) object[10]);
					programa.setMetodologia((String) object[11]);
					programa.setProcedimentosAvaliacao((String) object[12]);
					programa.setReferencias((String) object[13]);
					cc.setPrograma(programa);
					
				}
				
				Unidade u = new Unidade();
				u.setId((Integer) object[14]);
				u.setNome((String) object[15]);
				cc.setUnidade(u);

				componentes.add(cc);
				
			}

			String str = null;
			boolean erro = false;
			
			for (ComponenteCurricular c : componentes) {
				
				if(c.getPrograma().getObjetivos() != null) 
					str = StringUtils.containsLeroLero(c.getPrograma().getObjetivos(), null, null, null);
				else
					erro = true;
				if(c.getPrograma().getConteudo() != null) 
					str = StringUtils.containsLeroLero(c.getPrograma().getConteudo(), null, null, null);
				else
					erro = true;
				if(c.getPrograma().getCompetenciasHabilidades() != null) 
					str = StringUtils.containsLeroLero(c.getPrograma().getCompetenciasHabilidades(), null, null, null);
				else
					erro = true;
				
				if( str != null || erro){
					componentesProgramasIncompletos.add(c);
				}
				erro = false;
			}

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

		return componentesProgramasIncompletos;
	}

	/**
	 *  Método responsável por retornar Equivalências Específicas por componentes Curricular.
	 *  
	 * @param componente
	 * @return
	 * @throws DAOException
	 */
	public List<EquivalenciaEspecifica> findEquivalenciaEspecificaByComponente(ComponenteCurricular componente) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append( " FROM EquivalenciaEspecifica eq " );
		hql.append( " WHERE eq.ativo = trueValue() and eq.componente.id = :idComponente ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idComponente", componente.getId());
		
		@SuppressWarnings("unchecked")
		List<EquivalenciaEspecifica> lista = q.list();
		
		for (EquivalenciaEspecifica eq : lista) {
			eq.setExpressao(eq.getExpressao().replaceAll(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL, componente.getEquivalencia()));
		}
		
		return lista;
	}

	/**
	 *  Método responsável por retornar Equivalências Específicas inversas por componentes Curricular.
	 *  
	 * @param componente
	 * @return
	 * @throws DAOException
	 */
	public List<EquivalenciaEspecifica> findEquivalenciaEspecificaInversosByComponente(ComponenteCurricular componente) throws DAOException {
		StringBuffer hql = new StringBuffer();
		hql.append( " FROM EquivalenciaEspecifica eq " );
		hql.append( " WHERE eq.ativo = trueValue() and eq.expressao like :idComponente ");
		
		hql.append(	" AND eq.inicioVigencia <= :hoje " );
		hql.append(	" AND eq.fimVigencia >= :hoje " );
		
		Query q = getSession().createQuery(hql.toString());
		q.setString("idComponente", "%" + componente.getId() + "%");
		q.setDate("hoje", new Date());
		
		@SuppressWarnings("unchecked")
		List<EquivalenciaEspecifica> lista = q.list();
		
		for (EquivalenciaEspecifica eq : lista) {
			eq.setExpressao(eq.getExpressao().replaceAll(EquivalenciaEspecifica.EQUIVALENCIA_GLOBAL, eq.getComponente().getEquivalencia()));
		}
		
		return lista;
	}
	
	/**
	 * Busca lista de componentes curriculares em currículos ativos e que foram
	 * oferecidos no ano-período passados como parâmetro.
	 * @param ano
	 * @param periodo
	 * @param nivel
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> buscaComponentesOferecidosNoSemestre(int ano, int periodo, char nivel) {
		return getJdbcTemplate().queryForList("select distinct cc.id_disciplina from ensino.componente_curricular cc, graduacao.curriculo_componente cuco, graduacao.curriculo cu "
				+ "where cc.id_disciplina = cuco.id_componente_curricular and cuco.id_curriculo = cu.id_curriculo "
				+ "and cu.ativo = true and cuco.ativo = true and cc.ativo = true and cc.nivel = ? "
				+ "and cc.id_disciplina in (select distinct id_disciplina from ensino.turma where ano = ? and periodo = ? and id_situacao_turma in (1,2))",
				new Object[] { String.valueOf(nivel), ano, periodo }, Integer.class);
	}

	/**
	 * Busca a lista do histórico de equivalências de um componente curricular, informando as expressões
	 * que já foram usadas como equivalência, quais estão ativas e o período de vigência de cada uma.
	 * 
	 * @param idComponente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> buscarHistoricoEquivalenciasComponente(int idComponente) throws DAOException {
		Query q = getSession().createQuery("select ccd.equivalencia, ccd.id, cc.detalhes.id, ccd.data, ccd.equivalenciaValidaAte from ComponenteDetalhes ccd, ComponenteCurricular cc where cc.id = ccd.componente and ccd.componente = ? order by ccd.id desc");
		q.setInteger(0, idComponente);
		List<Object[]> result = q.list();
		
		List<Object[]> equivalencias = new ArrayList<Object[]>(result.size());
		
		for (Object[] linha : result) {
			String expressao = (String) linha[0];
			int id1 = (Integer) linha[1];
			int id2 = (Integer) linha[2];
			Date inicio = (Date) linha[3];
			Date fim = (Date) linha[4];
			                
			equivalencias.add(new Object[] { expressao, id1 == id2, inicio, fim  });
		}
		
		return equivalencias;
	}
	
}