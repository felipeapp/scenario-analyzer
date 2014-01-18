/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 13/09/2004
 */
package br.ufrn.arq.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Order.desc;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ilike;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.seguranca.log.LogDB;
import br.ufrn.arq.seguranca.log.LogInterceptor;
import br.ufrn.arq.seguranca.log.LogJdbcUpdate;
import br.ufrn.arq.seguranca.log.LogProcessorDelegate;
import br.ufrn.arq.seguranca.log.SessionLogger;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Esta classe é a mãe de todas as implementações dos DAOs e já
 * implementa um conjunto de operações básicas de mapeamento que estão
 * na interface GenericDAO.
 *
 * @author Gleydson Lima
 * @author David Pereira
 */
public class GenericDAOImpl implements GenericDAO {

	protected static final int INSERIR = 1;
	protected static final int ATUALIZAR = 2;
	protected static final int REMOVER = 3;

	private Transaction tx;

	private Session session;

	/** Informa se o DAO vai ser paginado */
	private boolean paginable;

	/** Tamanho da página, padrão é 50 */
	private int pageSize = 50;

	/** Número da página * */
	private int pageNum = 1;

	/** Total de Registros * */
	private int count;

	/** Ordenação * */
	protected ArrayList<String> orderBy;
	protected ArrayList<String> ascDesc;

	// Informações usadas para fins de Log
	private UsuarioGeral usuario;

	private int codMovimento;

	/** Intercepta alterações de dados via hibernate para registro de logs */
	protected LogInterceptor interceptor;

	@Deprecated
	protected String daoName;

	/** Indica se está usando o padrão open session in view ou não */
	private boolean openSessionInView;

	/** Sistema pelo o qual o DAO foi criado */
	private int sistema;

	/** Auxilia consultas com JDBC puro */
	private Map<DataSource, JdbcTemplate> templates = new HashMap<DataSource, JdbcTemplate>();

	/** Auxilia consultas com Hibernate */
	private HibernateTemplate hibernateTemplate;

	private SimpleJdbcTemplate simpleJdbcTemplate;
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	protected static final ResultSetExtractor MAP_EXTRACTOR = new ResultSetExtractor() {
		public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
			@SuppressWarnings("unchecked")
			Map<Object, Object> result = new ListOrderedMap();
			while(rs.next()) {
				result.put(rs.getObject(1), rs.getObject(2));
			}
			return result;
		}
	};

	public GenericDAOImpl() {

	}

	public GenericDAOImpl(int sistema) {
		orderBy = new ArrayList<String>();
		ascDesc = new ArrayList<String>();
		interceptor = new LogInterceptor();
		interceptor.setSistema(sistema);
		this.sistema = sistema;
	}

	public GenericDAOImpl(int sistema, Session session) {
		orderBy = new ArrayList<String>();
		ascDesc = new ArrayList<String>();
		interceptor = new LogInterceptor();
		interceptor.setSistema(sistema);
		this.sistema = sistema;
		this.session = session;
		openSessionInView = true;
	}

	/**
	 * Retorna o SessionFactory do mapeamento de acordo com o sistema associado ao DAO
	 * @return
	 */
	public SessionFactory getSF() {
		return getSF(sistema);
	}

	/**
	 * Retorna o SessionFactory do mapeamento de acordo com o sistema passado
	 * @return
	 */
	public SessionFactory getSF(int sistema) {
		return DAOFactory.getInstance().getSessionFactory(sistema);
	}

	/**
	 *  Retorna uma conexão com o banco de acordo com o sistema
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return Database.getInstance().getConnection(new Sistema(sistema));
	}

	/**
	 * Método para realização de persistências. Commit a cada invocação
	 * @param obj
	 * @param operacao
	 * @throws DAOException
	 */
	protected void changeOperation(PersistDB obj, int operacao) throws DAOException {
		
		if (sistema <= 0 || interceptor == null || interceptor.getSistema() <= 0)
			throw new DAOException("Não foi possível realizar a operação. O DAO não tem o sistema definido.");
		
		try {

			Transaction tx = getSession().beginTransaction();

			switch (operacao) {
			case INSERIR:
				creationData(obj);
				getSession().save(obj);
				break;
			case ATUALIZAR:
				updateData(obj);
				getSession().update(obj);
				break;
			case REMOVER:
				getSession().delete(obj);
				break;
			}
			tx.commit();

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Método para realização de persistências. Commit a cargo do JTA.
	 * @param obj
	 * @param operacao
	 * @throws DAOException
	 */
	protected void changeOperationNoFlush(PersistDB obj, int operacao) throws DAOException {

		if (sistema <= 0 || interceptor == null || interceptor.getSistema() <= 0)
			throw new DAOException("Não foi possível realizar a operação. O DAO não tem o sistema definido.");
		
		try {
			if (tx == null)
				tx = getSession().beginTransaction();

			switch (operacao) {
			case INSERIR:
				creationData(obj);
				getSession().save(obj);
				break;
			case ATUALIZAR:
				updateData(obj);
				getSession().update(obj);
				break;
			case REMOVER:
				getSession().delete(obj);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Persiste dado neste instante
	 */
	public void create(PersistDB obj) throws DAOException {
		changeOperation(obj, INSERIR);
	}
	/**
	 * Prepara persistência do dado via JTA
	 */
	public void createNoFlush(PersistDB obj) throws DAOException {
		changeOperationNoFlush(obj, INSERIR);
	}
	/**
	 * Remove dado
	 */
	public void remove(PersistDB obj) throws DAOException {
		changeOperation(obj, REMOVER);
	}
	/**
	 * Prepara atualização do dado via JTA
	 */
	public void updateNoFlush(PersistDB obj) throws DAOException {
		changeOperationNoFlush(obj, ATUALIZAR);
	}
	/**
	 * Atualiza dado
	 */
	public void update(PersistDB obj) throws DAOException {
		changeOperation(obj, ATUALIZAR);
	}

	/**
	 * Cria ou atualiza dado.
	 */
	public void createOrUpdate(PersistDB obj) throws DAOException {
		if (obj.getId() == 0)
			changeOperation(obj, INSERIR);
		else
			changeOperation(obj, ATUALIZAR);
	}

	/**
	 * Busca dado através do valor de sua chave primária
	 */
	@SuppressWarnings("unchecked")
	public <T extends PersistDB> T findByPrimaryKey(int id, Class<T> classe) throws DAOException {
		try {
			T obj = (T) getSession().get(classe, new Integer(id));
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}

	/**
	 * Busca dado através do valor de sua chave primária, bloqueando o dado
	 */
	@SuppressWarnings("unchecked")
	public <T extends PersistDB> T findByPrimaryKeyLock(Serializable id, Class<?> classe) throws DAOException {
		// É necessário limpar o cache senão o hibernate não da o lock na linha, simplesmente retorna o objeto que ja está no cache.
		clearSession();
		return (T) getSession().get(classe, id, LockMode.UPGRADE);
	}

	/**
	 * Busca todos os dados da tabela mapeada pela entidade passada, de acordo com a ordanenação
	 * e carregando atributos lazy
	 */
	public Collection<?> findAllIterate(String entidade, String orderBy) throws DAOException {
		return getSession().createQuery("from " + entidade + " " + orderBy).list();
	}

	/**
	 * Busca todos os dados da tabela mapeada pela entidade passada
	 *
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAll(Class<T> classe) throws DAOException {
		Criteria c = getCriteria(classe);
		c.setCacheable(true);
		return c.list();
	}

	/**
	 * Busca todos os dados da tabela mapeada pela entidade passada, que
	 * possui uma coluna ativo mapeada e assume valor = TRUE.
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAllAtivos(Class<T> classe, String... orderColumns) throws DAOException {
		Criteria c = getCriteria(classe);
		c.add(Expression.eq("ativo", Boolean.TRUE));
		
		if(orderColumns != null && orderColumns.length > 0){
			for (String coluna : orderColumns) {
				c.addOrder(Order.asc(coluna));
			}
		}

		return c.list();
	}

	/**
	 *  Busca todos os dados da tabela mapeada pela entidade passada, utilizando paginação
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAll(Class<T> classe, PagingInformation paging) throws DAOException {
		Long total = (Long) getSession().createQuery("select count(*) from " + classe.getName()).uniqueResult();

		if (paging != null && total != null) {
			paging.setTotalRegistros((int) total.longValue());
		}

		Criteria c = getCriteria(classe);
		c.setCacheable(true);
		if (paging != null) {
			c.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			c.setMaxResults(paging.getTamanhoPagina());
		}
		return c.list();
	}


	/**
	 * Busca todos os dados da tabela mapeada pela entidade passada, que
	 * possui uma coluna ativo mapeada e assume valor = TRUE, utilizando paginação
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAllAtivos(Class<T> classe, String orderColumn, PagingInformation paging) throws DAOException {
		Long total = (Long) getSession().createQuery("select count(*) from " + classe.getName() + " where ativo = trueValue()").uniqueResult();

		if (paging != null) {
			paging.setTotalRegistros((int) total.longValue());
		}

		Criteria c = getCriteria(classe);
		c.add(Expression.eq("ativo", Boolean.TRUE));

		if(orderColumn != null && !orderColumn.trim().equals(""))
			c.addOrder(Order.asc(orderColumn));

		c.setCacheable(true);
		if (paging != null) {
			c.setFirstResult(paging.getPaginaAtual()
					* paging.getTamanhoPagina());
			c.setMaxResults(paging.getTamanhoPagina());
		}
		return c.list();
	}


	/**
	 * Busca todos os dados da tabela mapeada pela entidade passada, utilizando ordenação e paginação
	 */
	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAll(Class<T> classe, PagingInformation paging, String orderField, String orderType) throws DAOException {
		Long total = (Long) getSession().createQuery("select count(*) from " + classe.getName()).uniqueResult();

		if (paging != null) {
			paging.setTotalRegistros((int) total.longValue());
		}

		Criteria c = getCriteria(classe);
		c.setCacheable(true);
		if (paging != null) {
			c.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			c.setMaxResults(paging.getTamanhoPagina());
		}
		if (orderType.equals("asc")) {
			c.addOrder(Order.asc(orderField));
		} else {
			c.addOrder(Order.desc(orderField));
		}
		return c.list();
	}

	public <T> Collection<T> findAll(Class<T> classe, String orderField, String ascDesc) throws DAOException {
		return findAll(classe, new String[] { orderField }, new String[] { ascDesc });
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAll(Class<T> classe, String orderFields[], String ascDesc[]) throws DAOException {
		String hql = "FROM " + classe.getName();

		if(!isEmpty(orderFields)){
			hql += " ORDER BY ";

			for(int i = 0; i < orderFields.length; i++){

				if(i > 0)	hql += ", ";

				hql += " " + orderFields[i];

				/**
				 * Se ascDesc não for especificado,
				 * é utilizada sempre ordenação ASC
				 */
				if(!isEmpty(ascDesc))
					hql += " " + ascDesc[i];

			}
		}

		Query q = getSession().createQuery(hql);

		// return result;
		return q.list();
	}

	/**
	 * Fechando a conexão
	 */
	public void close() {

		if (tx != null)
			tx.commit(); //Comita a transação em caso de != nula

		if (session != null && !openSessionInView && session.isOpen()) {

			//Se sessão aberta e não utilizar o padrão openSessionInView
			try {
				session.disconnect();
				session.close();

				SessionLogger.log(session, 'C');
				
				session = null;
				/*
				 * synchronized (sessionsClosed) { sessionsClosed.put(daoId,
				 * System.currentTimeMillis()); }
				 */
			} catch (HibernateException e) { //
				e.printStackTrace();
			}
		}
	}

	/**
	 * Desassocia o objeto com a sessão
	 */
	public void detach(PersistDB p) throws DAOException {

		try {
			getSession().evict(p);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Reassocia um objeto transient com a sessão
	 */
	public void lock(PersistDB p) throws DAOException {

		try {
			getSession().lock(p, LockMode.READ);
		} catch (HibernateException e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Centraliza os pedidos de sessão. Assim, pode-se abrir sessão somente
	 * quando for necessário
	 *
	 * @return
	 * @throws DAOException
	 */
	public synchronized Session getSession() throws DAOException {

		if (session == null || !session.isOpen()) {
			try {
				session = getSF().openSession(interceptor);
				SessionLogger.log(session, 'O');
			} catch (HibernateException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				throw new DAOException("Nao foi possivel abrir sessao: " + e.getMessage(), e);
			}
		}
		
		if (ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.REGISTRAR_METODO_INVOCADOR_SESSAO)) {
			SessionLogger.registerCaller(sistema, session);
		}
		
		return session;
	}

	/**
	 * Retorna um Criteria para a classe especificada.
	 * @param classe
	 * @return
	 * @throws DAOException
	 */
	protected Criteria getCriteria(Class<?> classe) throws DAOException {

		Criteria c = getSession().createCriteria(classe);
		if (paginable) {
			c.setFirstResult(pageSize * (pageNum - 1));
			c.setMaxResults(pageSize);
		}
		// Adiciona ordenaï¿½ï¿½es
		if (orderBy != null) {
			for (int a = 0; a < orderBy.size(); a++) {
				String ordem = ascDesc.get(a);
				if (ordem.equals("desc")) {
					c.addOrder(Order.desc(orderBy.get(a)));
				} else {
					c.addOrder(Order.asc(orderBy.get(a)));
				}
			}
		}

		return c;

	}

	@Override
	public void finalize() {

		if (session != null) {
			if (session.isOpen() && !openSessionInView) {
				try {
					session.close();
					SessionLogger.log(session, 'C');
					
					session = null;
				} catch (Exception e) {
					// Silenciada
				}
			}
		}
	}

	public int getTotalPaginas() {
		return (int) Math.ceil((double) count / pageSize);
	}

	public boolean isPaginable() {
		return paginable;
	}

	public void setPaginable(boolean paginable) {
		this.paginable = paginable;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void addOrder(String order, String asc) {
		if(orderBy == null) orderBy = new ArrayList<String>();
		orderBy.add(order);
	}

	public void clearOrder() {
		if(orderBy != null){
			orderBy.clear();
		}
		ascDesc.clear();
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
		if ( interceptor != null )
			interceptor.setUsuario(usuario);
	}

	public int getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(int codMovimento) {
		this.codMovimento = codMovimento;
		interceptor.setCodMovimento(codMovimento);
	}

	public void disableLog() {
		interceptor.setDisabled(true);
	}

	/**
	 * Fecha um statement silenciando as exceções
	 *
	 * @param st
	 */
	public void closeStatement(Statement st) {
		Database.getInstance().close(st);
	}

	/**
	 * Fecha uma conexão silenciando as exceções
	 * @param con
	 */
	public void closeConnection(Connection con) {
		Database.getInstance().close(con);
	}

	/**
	 * Fecha um resultSet silenciando as exceções
	 *
	 * @param rs
	 */
	public void closeResultSet(ResultSet rs) {
		Database.getInstance().close(rs);
	}

	/**
	 * Retorna o objeto do Último registro de uma Classe (ordenado
	 * decrescentemente pelo ID)
	 *
	 * @param classe
	 * @return o objeto do Último registro
	 * @throws DAOException
	 * @author Andre M Dantas
	 */
	public PersistDB findLast(Class<?> classe) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(classe);
			c.addOrder(Order.desc("id"));
			c.setMaxResults(1);
			return (PersistDB) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Inicializa as propriedades de um objeto com base no seu ID.
	 *
	 * @param obj
	 * @throws DAOException
	 * @author Andre M Dantas
	 */
	public void initialize(PersistDB obj) throws DAOException {
		try {
			if (obj == null || obj.getId() == 0)
				return;

			PersistDB loadedObj = findByPrimaryKey(obj.getId(), obj.getClass());
			
			if (loadedObj == null) return;
			
			BeanUtils.copyProperties(obj, loadedObj);
			detach(loadedObj);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Inicializa as propriedades de um objeto com base no seu ID.
	 *
	 * @param obj
	 * @throws DAOException
	 * @author Andre M Dantas
	 */
	@SuppressWarnings("unchecked")
	public <T> T initializeClone(Object proxy) throws DAOException {
		if (proxy == null) return null;
		try {
			if ((proxy instanceof HibernateProxy)) {
				proxy = ((HibernateProxy) proxy).getHibernateLazyInitializer().getImplementation();
			}

			T obj = (T) proxy.getClass().newInstance();
			BeanUtils.copyProperties(obj, proxy);
			return obj;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see br.ufrn.arq.dao.GenericDAO#refresh(br.ufrn.arq.dominio.PersistDB)
	 */
	@SuppressWarnings("unchecked")
	public <T extends PersistDB> T refresh(T obj) throws DAOException {
		if (obj == null) {
			return null;
		}
		if (obj.getId() == 0)
			return obj;
		try {
			T entidade = (T) HibernateUtils.getTarget(obj);
			entidade = (T) findByPrimaryKey(obj.getId(), entidade.getClass() );
			return (T) HibernateUtils.getTarget(entidade);
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Delegate para busca genérica com query.
	 * @param <T>
	 * @param classe
	 * @param field
	 * @param value
	 * @param exact
	 * @param init
	 * @param paging
	 * @param orderType
	 * @param orderFields
	 * @return
	 * @throws DAOException
	 */
	private <T> Collection<T> findWithQuery(Class<T> classe, String field, Object value, boolean exact, boolean init, PagingInformation paging, String orderType, String... orderFields) throws DAOException {
		return findWithQuery(classe, field, value, exact, init, paging, orderType, null, null, orderFields);
	}
	
	/**
	 * Busca genérica com Query.
	 *
	 * @param classe
	 * @param field
	 * @param value
	 * @param exact
	 * @param orderType
	 * @param orderFields
	 * @return
	 * @throws DAOException
	 * @author Andre M Dantas
	 */
	@SuppressWarnings("unchecked")
	private <T> Collection<T> findWithQuery(Class<T> classe, String field, Object value, boolean exact, boolean init, PagingInformation paging, String orderType, String[] fields, Object[] values, String... orderFields) throws DAOException {
		String query = "from " + classe.getSimpleName() + " obj ";
		String orderQuery = "";
		if (exact) {

			if(value != null){
				if (value instanceof String || value instanceof Character) {
					value = "'" + UFRNUtils.trataAspasSimples(value.toString()) + "'";
				}
				query += " where obj." + field + "=" + value;
			}

			if(fields != null && fields.length > 0){
				int indice = 0;

				//Não tem clausula where ainda
				if(value == null){
					query += " where 1=1 ";
				}

				for(String f : fields){
					query += " and obj." + f + "=" + ("'" + UFRNUtils.trataAspasSimples(values[indice].toString()) + "'");
					indice++;
				}
			}

		} else if (!init){
			query += " where to_ascii(upper(obj." + field + "), 'LATIN9') like to_ascii(upper('%" + UFRNUtils.trataAspasSimples(value.toString()) + "%'),'LATIN9')";
		} else{
			query += " where to_ascii(upper(obj." + field + "), 'LATIN9') like to_ascii(upper('" + UFRNUtils.trataAspasSimples(value.toString()) + "%'),'LATIN9')";
		}
		if (orderType != null & orderFields.length > 0) {
			orderQuery += " order by ";
			for (String f : orderFields) {
				orderQuery += f + ",";
			}
			orderQuery = orderQuery.substring(0, orderQuery
					.lastIndexOf(','));
			orderQuery += " " + orderType;

		}

		Query q = getSession().createQuery(query + orderQuery);
		if (paging != null) {
			Query qTotal = getSession().createQuery("select count(*)" + query);
			paging.setTotalRegistros((int) ((Long) qTotal.uniqueResult()).longValue());
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}

		return q.list();
	}

	/**
	 * Busca todos os registros de uma classe EXATAMENTE com o valor do campo
	 * passado por parametro
	 *
	 * @param classe
	 * @param field
	 * @param value
	 * @return lista de registros
	 * @throws DAOException
	 * @author Andre M Dantas
	 */
	public <T> Collection<T> findByExactField(Class<T> classe, String field, Object value, PagingInformation paging) throws DAOException {
		return findWithQuery(classe, field, value, true, false, paging, null, new String[0]);
	}

	public <T> Collection<T> findByExactField(Class<T> classe, String field, Object value) throws DAOException {
		return findWithQuery(classe, field, value, true, false, null, null, new String[0]);
	}
	/**
	 * Método usado para se buscar por match de vários campos.
	 */
	public <T> Collection<T> findByExactField(Class<T> classe, String[] fields, Object[] values) throws DAOException{
		return findWithQuery(classe, null, null, true, false, null, null, fields, values, new String[0]);
	}

	public <T> T findByExactField(Class<T> classe, String field, Object value, boolean limit) throws DAOException {
		Collection<T> retorno = findByExactField(classe, field, value);
		if( !isEmpty(retorno) && limit )
			return retorno.iterator().next();

		return null;
	}
	
	public <T> T findByExactField(Class<T> classe, String[] fields, Object[] values, boolean limit) throws DAOException {
		Collection<T> retorno = findWithQuery(classe, null, null, true, false, null, null, fields, values, new String[0]);
		if( !isEmpty(retorno) && limit )
			return retorno.iterator().next();

		return null;
	}

	/**
	 * Busca todos os registros de uma classe a partir do valor em STRING do
	 * campo passado por parametro (usando like '%COLUNA%')
	 *
	 * @param classe
	 * @param field
	 * @param value
	 * @return
	 * @throws DAOException
	 * @author Andre M Dantas
	 */
	public <T> Collection<T> findByLikeField(Class<T> classe, String field, Object value,
			PagingInformation paging) throws DAOException {
		return findWithQuery(classe, field, value, false, false, paging, null, new String[0]);
	}

	public <T> Collection<T> findByLikeField(Class<T> classe, String field, Object value) throws DAOException {
		return findWithQuery(classe, field, value, false, false, null, null, new String[0]);
	}

	public <T> Collection<T> findByLikeInitField(Class<T> classe, String field, Object value, PagingInformation paging) throws DAOException {
		return findWithQuery(classe, field, value, false, true, paging, null, new String[0]);
	}

	/**
	 * Igual ao findByExactField(Class, Strin, Object), só que possibilita
	 * ordenação por campos.
	 *
	 * @param classe
	 * @param field
	 * @param value
	 * @param orderType
	 * @param orderFields
	 * @return
	 * @throws DAOException
	 * @author Andre M Dantas
	 */
	public <T> Collection<T> findByExactField(Class<T> classe, String field, Object value, PagingInformation paging, String orderType, String... orderFields) throws DAOException {
		return findWithQuery(classe, field, value, true, false, paging, orderType, orderFields);
	}

	public <T> Collection<T> findByExactField(Class<T> classe, String field, Object value, String orderType, String... orderFields) throws DAOException {
		return findByExactField(classe, field, value, null, orderType, orderFields);
	}

	public <T> Collection<T> findAtivosByExactField(Class<T> classe, String field, Object value) throws DAOException {
		return findAtivosByExactField(classe, field, value, (String[]) null, (String[]) null);
	}
	
	public <T> Collection<T> findAtivosByExactField(Class<T> classe, String field, Object value, String orderBy, String ascDesc) throws DAOException {
		return findAtivosByExactField(classe, field, value, new String[] { orderBy }, new String[] { ascDesc });
	}
	
	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAtivosByExactField(Class<T> classe, String field, Object value, String[] orderBy, String[] ascDesc) throws DAOException {
		Criteria c = getSession().createCriteria(classe);
		c.add(eq("ativo", true));
		
		if (value instanceof String)
			c.add(ilike(field, value));
		else
			c.add(eq(field, value));
		
		if (orderBy != null) {
			for (int i = 0; i < orderBy.length; i++) {
				if (ascDesc.length > i && "asc".equals(ascDesc[i]))
					c.addOrder(asc(orderBy[i]));
				else
					c.addOrder(desc(orderBy[i]));
				
				i++;
			}
		}
		return c.list();
	}
	
	/**
	 * Igual ao findByLikeField(Class, Strin, Object), só que possibilita
	 * ordenação por campos.
	 *
	 * @param classe
	 * @param field
	 * @param value
	 * @param orderType
	 * @param orderFields
	 * @return
	 * @throws DAOException
	 * @author Andre M Dantas
	 */
	public <T> Collection<T> findByLikeField(Class<T> classe, String field, Object value, PagingInformation paging, String orderType, String... orderFields) 	throws DAOException {
		return findWithQuery(classe, field, value, false, false, paging, orderType, orderFields);
	}

	public <T> Collection<T> findByLikeField(Class<T> classe, String field, Object value, String orderType, String... orderFields) throws DAOException {
		return findByLikeField(classe, field, value, null, orderType, orderFields);
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAllProjection(Class<T> classe,
			String... fields) throws DAOException {
		Criteria c = getCriteria(classe);

		ProjectionList projections = Projections.projectionList();
		for (String field : fields) {
			projections.add(Projections.property(field), field);
		}
		c.setProjection(projections);
		c.setResultTransformer(new AliasToBeanResultTransformer(classe));
		return c.list();
	}


	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAllProjection(Class<T> classe, PagingInformation paging, String... fields) throws DAOException {
		Criteria c = getCriteria(classe);

		ProjectionList projections = Projections.projectionList();
		for (String field : fields) {
			projections.add(Projections.property(field), field);
		}
		c.setProjection(projections);
		c.setResultTransformer(new AliasToBeanResultTransformer(classe));
		try {
			preparePaging(paging, c);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
		return c.list();
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAllProjection(Class<T> classe, String orderBy, String ascDesc, String... fields) throws DAOException {
		Criteria c = getCriteria(classe);

		if (fields.length > 0) {
			ProjectionList projections = Projections.projectionList();
			for (String field : fields) {
				projections.add(Projections.property(field), field);
			}
			c.setProjection(projections);
			c.setResultTransformer(new AliasToBeanResultTransformer(classe));
		}

		if ("desc".equals(ascDesc)) {
			c.addOrder(Order.desc(orderBy));
		} else {
			c.addOrder(Order.asc(orderBy));
		}

		return c.list();
	}

	/**
	 * Caso a sessão seja passada após ou durante a criação do DAO, assume-se
	 * o padrão open session in view.
	 */
	public void setSession(Session sessao) {
		if ( sessao != null ) {
			openSessionInView = true;
			this.session = sessao;
		}
	}

	/**
	 * Prepara a query e 'configura' o Paging Information para a a paginação de
	 * uma determinada consulta
	 *
	 * @param paging
	 * @param q
	 * @throws Exception
	 */
	public void preparePaging(PagingInformation paging, Query q)
			throws Exception {
		if (paging != null) {
			paging.setTotalRegistros(count(q));
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
	}

	/**
	 * Elimina a clausula order by e os campos definidos no "FROM" e executa a
	 * consulta com um SELECT COUNT(*) e retorna a quantidade de resultados
	 * encontrados.
	 *
	 * @param q
	 * @return
	 * @throws DAOException
	 */
	public int count(Query q) throws DAOException {
		String query = q.getQueryString();
		int posOrder = (query.indexOf("order by") > 0) ? query.indexOf("order by") : query.length();
		int posSelect = (query.indexOf("select") >= 0) ? query.indexOf("from") : 0;
		Query qTotal = getSession().createQuery("select count(*) " + query.substring(posSelect, posOrder));
		return (int) ((Long) qTotal.uniqueResult()).longValue();
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dao.GenericDAO#count(java.lang.String)
	 */
	public int count(String sqlQuery) {
		int posOrder = (sqlQuery.indexOf("order by") > 0) ? sqlQuery.indexOf("order by") : sqlQuery.length();
		int posSelect = (sqlQuery.indexOf("select") >= 0) ? sqlQuery.indexOf("from") : 0;
		return getJdbcTemplate().queryForInt("select count(*) " + sqlQuery.substring(posSelect, posOrder));
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dao.GenericDAO#count(java.lang.String)
	 */
	public int count(String sqlQuery, Sistema sistema) {
		int posOrder = (sqlQuery.indexOf("order by") > 0) ? sqlQuery.indexOf("order by") : sqlQuery.length();
		int posSelect = (sqlQuery.indexOf("select") >= 0) ? sqlQuery.indexOf("from") : 0;
		return getJdbcTemplate(sistema).queryForInt("select count(*) " + sqlQuery.substring(posSelect, posOrder));
	}

	public void preparePaging(PagingInformation paging, Criteria c) throws Exception {
		Collection<?> resul = c.list();
		if (paging != null && resul != null && !resul.isEmpty()) {
			paging.setTotalRegistros(resul.size());
			c.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			c.setMaxResults(paging.getTamanhoPagina());
		}
	}

	public boolean isOpenSessionInView() {
		return openSessionInView;
	}

	public void setOpenSessionInView(boolean openSessionInView) {
		this.openSessionInView = openSessionInView;
	}

	public int getSistema() {
		return sistema;
	}

	public final void setSistema(int sistema) {
		this.sistema = sistema;
		if (interceptor == null) {
			interceptor = new LogInterceptor(); 
			interceptor.setSistema(sistema);
		}
	}

	public void clearSession() {
		try {
			getSession().clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Atualiza somente um atributo de uma entidade
	 *
	 * @param classe
	 * @param id
	 * @param campo
	 * @param valor
	 * @throws Exception
	 */
	public void updateField(Class<?> classe, Integer id, String campo, Object valor) throws DAOException {
		Statement st = null;
		Connection con = null;
		try {
			Date data = new Date();

			Field atualizadoEm = ReflectionUtils.getFieldByNameOrAnnotation(classe, "atualizadoEm", AtualizadoEm.class);
			Field atualizadoPor = ReflectionUtils.getFieldByNameOrAnnotation(classe, "atualizadoPor", AtualizadoPor.class);
			Class<?> classeUpdateFields = null;
			if (atualizadoEm != null){
				ReflectionUtils.setFieldValue(classe.newInstance(), atualizadoEm, data);
				classeUpdateFields = atualizadoEm.getDeclaringClass();
			}
			if (atualizadoPor != null){
				ReflectionUtils.setFieldValue(classe.newInstance(), atualizadoPor, getCriador(atualizadoPor));
				classeUpdateFields = atualizadoPor.getDeclaringClass();
			}

			con = getConnection();
			st = con.createStatement();

			String query = null;
			if( classeUpdateFields != null && !classeUpdateFields.equals( classe ) ){
				query = HibernateUtils.createUpdateQuery(getSF(), classe, id, campo, valor);
				if (atualizadoEm != null)
					st.addBatch( HibernateUtils.createUpdateQuery(getSF(), classeUpdateFields, id,  atualizadoEm.getName(), data) );
				if (atualizadoPor != null)
					st.addBatch( HibernateUtils.createUpdateQuery(getSF(), classeUpdateFields, id, atualizadoPor.getName(), getCriador(atualizadoPor)) );
			}else
				query = HibernateUtils.createUpdateQuery(getSF(), classe, id, campo, valor, atualizadoPor, getCriador(atualizadoPor), atualizadoEm, data);

			st.addBatch(query);

			st.executeBatch();

			logUpdateField(classe, id, new String[] { campo } , new Object[] { valor } );

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}


	/**
	 * Registra o log do updateField
	 * @param classe
	 * @param id
	 * @param campos
	 * @param valores
	 */
	private void logUpdateField(Class<?> classe, Integer id, String[] campos, Object[] valores ) {

		// Log de alteração
		LogDB log = new LogDB();
		log.setIdElemento(id);
		log.setData(new Date());
		if ( getUsuario() != null && getUsuario().getRegistroEntrada() != null){
			log.setIdRegistroEntrada(getUsuario().getRegistroEntrada().getId());
		}
		log.setOperacao('U');
		log.setSistema(getSistema());
		log.setTabela(classe.getName());

		StringBuffer alteracoes = new StringBuffer(300);
		for (int i = 0; i < valores.length; i++) {
			alteracoes.append(campos[i] + ": " + valores[i]  );
			alteracoes.append("\n");
		}
		log.setAlteracao(alteracoes.toString());

		// Coloca o log na fila
		LogProcessorDelegate.getInstance().writeDatabaseLog(log);

	}


	/**
	 * Atualiza alguns atributos de uma entidade
	 *
	 * @param classe
	 * @param id
	 * @param campo
	 * @param valor
	 * @throws Exception
	 */
	public void updateFields(Class<?> classe, Integer id, String[] campos, Object[] valores) throws DAOException {
		Statement st = null;
		Connection con = null;
		try {
			Date data = new Date();

			Field atualizadoEm = ReflectionUtils.getFieldByNameOrAnnotation(classe, "atualizadoEm", AtualizadoEm.class);
			Field atualizadoPor = ReflectionUtils.getFieldByNameOrAnnotation(classe, "atualizadoPor", AtualizadoPor.class);

			String query = HibernateUtils.createUpdateQuery(getSF(), classe, id, campos, valores, atualizadoPor, getCriador(atualizadoPor), atualizadoEm, data);

			con = getConnection();
			st = con.createStatement();
			st.executeUpdate(query);

			logUpdateField(classe, id, campos, valores);

		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<T> findAllCombo(Class<T> classe, String orderField, String ascDesc, String id, String label ) throws DAOException {
		String sql = " select o." + id + ", o." + label + " from " + classe.getName() + " o ";
		String order = "order by o." + orderField + " " + ascDesc;

		Query q = getSession().createQuery(sql + order);

		return q.list();
	}

	public DataSource getDataSource(int sistema) {
		return Database.getInstance().getDataSource(sistema);
	}

	public DataSource getDataSource() {
		return getDataSource(this.sistema);
	}

	public JdbcTemplate getJdbcTemplate() {
		return getJdbcTemplate(getDataSource());
	}

	public JdbcTemplate getJdbcTemplate(DataSource ds) {
		if (templates.get(ds) == null)
			templates.put(ds, new JdbcTemplate(ds));
		return templates.get(ds);
	}


	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(DataSource ds) {
		if (namedParameterJdbcTemplate == null)
			namedParameterJdbcTemplate =  new NamedParameterJdbcTemplate(ds);
		return namedParameterJdbcTemplate;
	}
	
	public JdbcTemplate getJdbcTemplate(Sistema sistema) {
		return getJdbcTemplate(getDataSource(sistema.getId()));
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(Sistema sistema) {
		return getNamedParameterJdbcTemplate(getDataSource(sistema.getId()));
	}

	public JdbcTemplate getJdbcTemplate(int sistema) {
		return getJdbcTemplate(getDataSource(sistema));
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(int sistema) {
		return getNamedParameterJdbcTemplate(getDataSource(sistema));
	}

	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return getSimpleJdbcTemplate(getDataSource());
	}

	public SimpleJdbcTemplate getSimpleJdbcTemplate(DataSource ds) {
		if (simpleJdbcTemplate == null)
			simpleJdbcTemplate = new SimpleJdbcTemplate(ds);
		return simpleJdbcTemplate;
	}

	/**
	 * Seta informações de criação do objeto
	 */
	private void creationData(PersistDB obj) {
		Field[] criadoEm = ReflectionUtils.getFieldsByNameOrAnnotation(obj, "criadoEm", CriadoEm.class);
		Field[] criadoPor = ReflectionUtils.getFieldsByNameOrAnnotation(obj, "criadoPor", CriadoPor.class);
		Field[] ativo = ReflectionUtils.getFieldsByNameOrAnnotation(obj, "ativo", CampoAtivo.class);
		
		if (criadoEm != null && criadoEm.length > 0) {
			for (Field field : criadoEm) {
				ReflectionUtils.setFieldValue(obj, field, new Date());				
			}
		}
		
		if (criadoPor != null && criadoPor.length > 0) {
			for (Field field : criadoPor) {
				ReflectionUtils.setFieldValue(obj, field, getCriador(field));
			}
		}
		
		if (ativo != null && ativo.length > 0) {
			for (Field field : ativo) {
				CampoAtivo campoAtivo = (CampoAtivo) ReflectionUtils.getAnnotation(field, CampoAtivo.class);
				boolean valorAtivo = true;
				if (campoAtivo != null)
					valorAtivo = campoAtivo.value();
				ReflectionUtils.setFieldValue(obj, field, valorAtivo);
			}
		}
	}

	/**
	 * Seta informações de atualização do objeto
	 *
	 * @throws DAOException
	 */
	private void updateData(PersistDB obj) throws DAOException {
		retrieveCreationData(obj);
		Field[] atualizadoEm = ReflectionUtils.getFieldsByNameOrAnnotation(obj, "atualizadoEm", AtualizadoEm.class);
		Field[] atualizadoPor = ReflectionUtils.getFieldsByNameOrAnnotation(obj, "atualizadoPor", AtualizadoPor.class);
		
		if (atualizadoEm != null && atualizadoEm.length > 0) {
			for (Field field : atualizadoEm) {
				ReflectionUtils.setFieldValue(obj, field, new Date());				
			}
		}
		
		if (atualizadoPor != null && atualizadoPor.length > 0) {
			for (Field field : atualizadoPor) {
				ReflectionUtils.setFieldValue(obj, field, getCriador(field));
			}
		}
	}
	/**
	 * Retorna o objeto que representa o criador/atualizador da entidade.
	 * Dependendo tipo do atribuito, pode ser um Usuario ou um Registro de Entrada
	 *
	 * @param fieldCriador
	 * @return
	 */
	private PersistDB getCriador(Field fieldCriador) {
		if (fieldCriador == null)
			return null;

		if (fieldCriador.getType().equals(RegistroEntrada.class)) {
			if (getUsuario() != null)
				return getUsuario().getRegistroEntrada();
			else
				return null;
		} else {
			return getUsuario();
		}
	}
	/**
	 * Pega as informações de cadastro para que uma atualização não as sobrescreva.
	 *
	 * @param obj
	 * @throws DAOException
	 */
	private void retrieveCreationData(PersistDB obj) throws DAOException {
		Field[] criadoEm = ReflectionUtils.getFieldsByNameOrAnnotation(obj, "criadoEm", CriadoEm.class);
		Field[] criadoPor = ReflectionUtils.getFieldsByNameOrAnnotation(obj, "criadoPor", CriadoPor.class);
		
		if (criadoPor != null && criadoPor.length > 0) {
			Class<?> classe = obj.getClass();
			
			for (Field field : criadoPor) {
				String nomeCriadoPor = field.getName();
				List<Object> dados = findDadosAuditoriaByIdObj(classe, obj.getId(), nomeCriadoPor, null);
				Integer idCriador = null;

				if( dados != null && (dados.size() > 0) && dados.get(0) != null){
					idCriador = (Integer) dados.get(0);
				}
				
				if( field != null && idCriador != null ){
					PersistDB u;
					try {
						u = (PersistDB) field.getType().newInstance();
					} catch (Exception e) {
						e.printStackTrace();
						throw new DAOException( "Não foi possível recuperar os dados de auditoria do registro.", e);
					}
					u.setId(idCriador);
					ReflectionUtils.setFieldValue(obj, field, u);
				}
			}
		}
		
		if (criadoEm != null && criadoEm.length > 0) {
			Class<?> classe = obj.getClass();
			
			for (Field field : criadoEm) {
				String nomeCriadoEm = field.getName();
				List<Object> dados = findDadosAuditoriaByIdObj(classe, obj.getId(), null, nomeCriadoEm);
				Date dataCriacao = null;
				
				if( dados != null && (dados.size() > 0) && dados.get(1) != null){
					dataCriacao = (Date) dados.get(1);
				}
				
				if( field != null && dataCriacao != null ){
					ReflectionUtils.setFieldValue(obj, field, dataCriacao);
				}
			}

		}
	}

	/**
	 * Mesma funcionalidade normal do findByPrimaryKey, mas podem ser passados atributos
	 * para otimizar a consulta através da projeção desses atributos.
	 */
	@SuppressWarnings("unchecked")
	public <T extends PersistDB> T findByPrimaryKey(int id, Class<T> classe, String... atributos) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append(" SELECT ");
			
			String projecao = StringUtils.join(atributos, ", c.");
			hql.append("c."+projecao);
			
			hql.append(" FROM " + classe.getName() + " as c WHERE c.id = :id");
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("id", id);
			
			Collection<T> result = HibernateUtils.parseTo(q.list(), projecao, classe, "c");
			if (!result.isEmpty()) {
				return result.iterator().next();
			}
			
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public int incrNumField(Class<? extends PersistDB> classe, Integer id, String campo) throws DAOException {
		try {
			PersistDB o = findByPrimaryKey(id, classe, campo);
			Integer i = (Integer) PropertyUtils.getProperty(o, campo);
			if (i == null)
				i = 1;
			else
				i++;
			updateField(classe, id, campo, i);
			return i;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	public int decrNumField(Class<? extends PersistDB> classe, Integer id, String campo) throws DAOException {
		try {
			PersistDB o = findByPrimaryKey(id, classe, campo);
			Integer i = (Integer) PropertyUtils.getProperty(o, campo);
			if (i == null  || i  == 0)
				i = 0;
			else
				i--;
			updateField(classe, id, campo, i);
			return i;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public HibernateTemplate getHibernateTemplate() {
		if (hibernateTemplate == null)
			hibernateTemplate = new HibernateTemplate(getSF());
		return hibernateTemplate;
	}

	public HibernateTemplate getHibernateTemplate(SessionFactory sf) {
		if (hibernateTemplate == null)
			hibernateTemplate = new HibernateTemplate(sf);
		return hibernateTemplate;
	}

	public HibernateTemplate getHibernateTemplate(Sistema sistema) {
		if (hibernateTemplate == null)
			hibernateTemplate = new HibernateTemplate(getSF(sistema.getId()));
		return hibernateTemplate;
	}

	public HibernateTemplate getHibernateTemplate(int sistema) {
		if (hibernateTemplate == null)
			hibernateTemplate = new HibernateTemplate(getSF(sistema));
		return hibernateTemplate;
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.dao.GenericDAO#findAndFetch(int, java.lang.Class, java.lang.String[])
	 */
	@SuppressWarnings("unchecked")
	public <T extends PersistDB> T findAndFetch(int id, Class<T> classe, String... atributos) throws DAOException {
		Criteria c = getCriteria(classe);
		c.add( Restrictions.idEq(id));

		if (atributos.length > 0) {
			for (String atributo : atributos) {
				c.setFetchMode(atributo, FetchMode.JOIN);
			}
		}
		return (T) c.uniqueResult();
	}

	public void addJdbcTemplate(DataSource ds, JdbcTemplate jt) {
		templates.put(ds, jt);
	}

	/**
	 * Cria uma sequência na base de dados dado uma conexão com o banco de dados, usando JDBC e SQL
	 *
	 * @param con
	 * @param seqName
	 */
	public void criaSequencia(String esquema, String seqName) {
		String criaSequencia =  SQLDialect.criarSequencia(esquema, seqName);

		getJdbcTemplate().update(criaSequencia);
	}

	/**
	 * Recupera o próximo valor de uma sequência de esquema e nome passados como argumento.
	 * Se a sequência não existir, cria.
	 *
	 * @param esquema - esquema em que a sequencia foi criada
	 * @param nomeSequencia - nome da sequencia
	 * @return
	 */
	public int getNextSeq(String esquema, String nomeSequencia) {
		String sequencia = esquema == null ? nomeSequencia : esquema + "." + nomeSequencia; 
		if (!isSequenciaExistente(esquema, nomeSequencia)){
			//Cria sequência. Ela ainda não existe
			criaSequencia( esquema, nomeSequencia );
		}

		//Sequência já existe na base de dados. Retorna próximo valor
		return getJdbcTemplate().queryForInt("select " + SQLDialect.nextVal(sequencia) );
	}

	/**
	 * Recupera o valor atual de uma sequência de esquema e nome passados como argumento.
	 * Se a sequência não existir, retorna 0 (Zero).
	 *
	 * @param esquema - esquema em que a sequência foi criada
	 * @param nomeSequencia - nome da sequência
	 * @return
	 */
	public int getCurrentSeq(String esquema, String nomeSequencia) {
		if (!isSequenciaExistente(esquema, nomeSequencia))
			return 0;

		//Sequencia já existe na base de dados. Retorna valor corrente.
		return getJdbcTemplate().queryForInt(SQLDialect.getCurrentSeqValue(esquema, nomeSequencia));
	}

	/**
	 * Verifica se a sequência definida pelos parâmetros informados existe ou não na base de dados.
	 * @param esquema
	 * @param nomeSequencia
	 * @return
	 */
	private boolean isSequenciaExistente(String esquema, String nomeSequencia){
		
		String sqlVerificaSequencia = SQLDialect.verificarExistenciaSequencia(esquema, nomeSequencia);

		int total = getJdbcTemplate().queryForInt(sqlVerificaSequencia);

		return total > 0;
	}

	/**
	 * Retorna uma lista com dois objetos sendo o primeiro o id do usuário/registro entrada criado
	 * e o segundo a data de criação
	 * @param classe
	 * @param id
	 * @param nomeAtributoCriadoPor
	 * @param nomeAtributoCriadoEm
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Object> findDadosAuditoriaByIdObj(Class<?> classe, int id, String nomeAtributoCriadoPor, String nomeAtributoCriadoEm) throws DAOException {

		if( classe == null  || id <= 0 || ( nomeAtributoCriadoEm == null && nomeAtributoCriadoPor == null ) )
			return null;

		String hql = "SELECT obj.id, " ;
		if( nomeAtributoCriadoPor != null )
			hql += "obj." + nomeAtributoCriadoPor + ".id";
		if( nomeAtributoCriadoEm != null && nomeAtributoCriadoPor != null )
			hql += ", ";
		if( nomeAtributoCriadoEm != null )
			hql += "obj." + nomeAtributoCriadoEm;

		hql += " FROM " + classe.getName() + " obj "
			+ " WHERE obj.id = " + id;

		Session s = getSF().openSession();
		
		try {
			Query q = s.createQuery(hql);
			List<Object[]> rs = q.list();
	
			List<Object> resultado = new ArrayList<Object>();
			for( Object[] linha : rs ){
	
				int i = 1;
	
				if( nomeAtributoCriadoPor != null )
					resultado.add( linha[i++] );
				else
					resultado.add( null );
	
				if( nomeAtributoCriadoEm != null )
					resultado.add( linha[i++] );
				else
					resultado.add( null );
	
			}
			return resultado;
		} finally {
			s.close();
		}
	}

	public void update(String sql, Object... params) {
		getJdbcTemplate().update(sql, params);

		Integer idUsuario = usuario == null ? null : usuario.getId();
		Integer idRegistro = (usuario == null || usuario.getRegistroEntrada() == null) ? null : usuario.getRegistroEntrada().getId();
		
		LogJdbcUpdate log = new LogJdbcUpdate();
		log.setCodMovimento(codMovimento);
		log.setData(new Date());
		log.setSql(sql);
		log.setParams(params);
		log.setSistema(sistema);
		log.setIdUsuario(idUsuario);
		log.setIdRegistroEntrada(idRegistro);

		LogProcessorDelegate.getInstance().writeJdbcUpdateLog(log);
	}
}
