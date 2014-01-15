/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 13/09/2004
 */
package br.ufrn.arq.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.log.LogInterceptor;
import br.ufrn.arq.seguranca.log.SessionLogger;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.comum.dao.SistemaDao;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Fabrica de Objetos DAOs. Implementação do Factory Method.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 */
public class DAOFactory {

	protected static DAOFactory singleton = new DAOFactory();

	/** Mapa que contém informações dos session factories de todos os sistemas */
	protected static Map<Integer, SessionFactory> sessionFactories = new ConcurrentHashMap<Integer, SessionFactory>();
	
	public static DAOFactory getInstance() {
		return singleton;
	}
	
	private DAOFactory() {
		// Construtor privado para não permitir instanciação
	}
	
	/**
	 * Retorna o session factory  para o sistema passado como parâmetro.
	 * @param sistema
	 * @return
	 */
	public synchronized SessionFactory getSessionFactory(int sistema) {
		
		SessionFactory sf = sessionFactories.get(sistema);
		
		if (sf == null) {
			SistemaDao dao = new SistemaDao();

			try {
				sf = buildSf(Database.getInstance().getDataSource(sistema), dao.findConfigHibernateSistema(sistema));
				sessionFactories.put(sistema, sf);
			} finally {
				dao.close();
			}
		}
		
		return sf;
	}

	/*
	 * Constrói uma nova instância de um session factory com base no datasource 
	 * e no caminho para o arquivo de configuração passados como parâmetros. 
	 */
	private static synchronized SessionFactory buildSf(DataSource ds, String resource) {
		try {
			AnnotationSessionFactoryBean factory = new AnnotationSessionFactoryBean();
			factory.setDataSource(ds);
			factory.setConfigLocation(new ClassPathResource(resource));
			factory.afterPropertiesSet();
			return (SessionFactory) factory.getObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Retorna uma instância do DAO da classe passada como parâmetro, setando ainda o sistema do DAO.
	 * Seta a session do DAO para a session que estiver em request.
	 * @param <T>
	 * @param daoClass
	 * @param sistema
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends GenericDAO> T getDAO(Class<T> daoClass, int sistema, HttpServletRequest req) { 
		T dao = null;
		Component component = (Component) ReflectionUtils.getAnnotation(daoClass, Component.class);
			
		if (component == null ) {
			dao = ReflectionUtils.instantiateClass(daoClass);
		} else {
			WebApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(req.getSession().getServletContext());
			String beanName = component.value();
			if (isEmpty(beanName)) {
				beanName = StringUtils.uncapitalize(daoClass.getSimpleName());
			}

			if (ac.containsBean(beanName))
				dao = (T) ac.getBean(beanName);
			else
				dao = ReflectionUtils.instantiateClass(daoClass);
		}

		if (sistema >= 0) {
			dao.setSistema(sistema);
			dao.setSession(getSessionRequest(sistema, req));
		} else {
			//Associa sessão do Hibernate ao DAO
			dao.setSession(getSessionRequest(req));			
		}
		
		dao.setUsuario((UsuarioGeral) req.getSession().getAttribute("usuario"));
		return dao;
	}
	
	/**
	 * Retorna uma instância do DAO da classe passada como parâmetro. Seta a session do DAO para a session que estiver em request.
	 * @param <T>
	 * @param daoClass
	 * @param req
	 * @return
	 */
	public <T extends GenericDAO> T getDAO(Class<T> daoClass, HttpServletRequest req) {
		Integer sistema = (Integer) req.getAttribute("sistema");
		if (sistema == null) sistema = -1;
		return getDAO(daoClass, sistema, req);
	}

	/**
	 * Retorna uma instância do DAO da classe passada como parâmetro.
	 * @param <T>
	 * @param daoClass
	 * @return
	 */
	public <T extends GenericDAO> T getDAO(Class<T> daoClass) {
		T dao = ReflectionUtils.instantiateClass(daoClass);
		setThreadBoundSession(dao);
		return dao;
	}

	/**
	 * 
	 * @return
	 */
	public static synchronized void setThreadBoundSession(GenericDAO dao) {
		Map<Integer, Session> sessions = ThreadScopedResourceCache.sessionsCache.get();
		
		if (sessions == null) {
			sessions = new ConcurrentHashMap<Integer, Session>();
			ThreadScopedResourceCache.sessionsCache.set(sessions);
		} 
		
		Session session = null;
		
		int sistema = dao.getSistema();
		if (sessions.get(sistema) == null || !sessions.get(sistema).isOpen()) {
			session = DAOFactory.getInstance().getSessionFactory(sistema).openSession(new LogInterceptor(sistema));
			sessions.put(sistema, session);
			SessionLogger.log(session, 'O');
		} else {
			session = sessions.get(sistema);
		}

	}

	/**
	 * Retorna uma instância do DAO da classe passada como parâmetro, setando ainda o usuário
	 * e a session do Hibernate.
	 * @param <T>
	 * @param daoClass
	 * @param usuario
	 * @param session
	 * @return
	 * @throws DAOException
	 */
	public <T extends GenericDAO> T getDAO(Class<T> daoClass, UsuarioGeral usuario, Session session) throws DAOException {
		T dao = ReflectionUtils.instantiateClass(daoClass);
		dao.setUsuario(usuario);
		dao.setSession(session);
		return dao;
	}

	/**
	 * Retorna uma instância do DAO da classe passada como parâmetro, setando ainda o usuário que está
	 * usando o DAO.
	 * 
	 * @param <T>
	 * @param daoClass
	 * @param usuario
	 * @return
	 */
	public <T extends GenericDAO> T getDAO(Class<T> daoClass, UsuarioGeral usuario) {
		T dao = ReflectionUtils.instantiateClass(daoClass);
		dao.setUsuario(usuario);
		setThreadBoundSession(dao);
		return dao;
	}

	/**
	 * Retorna um GenericDAO para o sistema passado como parâmetro.
	 * @param sistema
	 * @return
	 */
	public static GenericDAO getGeneric(int sistema) {
		GenericDAO dao = new GenericDAOImpl(sistema);
		setThreadBoundSession(dao);
		return dao;
	}

	/**
	 * Retorna uma instância do DAO da classe passada como parâmetro. Utilizado em processadores.
	 * @param <T>
	 * @param daoClass
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public <T extends GenericDAO> T getDAOMov(Class<T> daoClass, Movimento mov) throws DAOException {
		return getDAOMov(daoClass, null, mov);
	}
	
	/**
	 * Retorna uma instância do DAO da classe passada como parâmetro, setando ainda o sistema do DAO.
	 * Utilizado em processadores.
	 * @param <T>
	 * @param daoClass
	 * @param sistema
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public <T extends GenericDAO> T getDAOMov(Class<T> daoClass, Integer sistema, Movimento mov) throws DAOException {
		T dao = null;
		Component component = (Component) ReflectionUtils.getAnnotation(daoClass, Component.class);
			
		if (component == null) {
			dao = ReflectionUtils.instantiateClass(daoClass);
			if (daoClass.getSimpleName().contains("GenericDAOImpl")) {
				dao = (T) new GenericDAOImpl(mov.getSistema());
			} else {
				dao = ReflectionUtils.instantiateClass(daoClass);
			}
		} else {
			ApplicationContext ac = mov.getApplicationContext();
			String beanName = component.value();
			if (isEmpty(beanName)) {
				beanName = StringUtils.uncapitalize(daoClass.getSimpleName());
			}

			if (ac.containsBean(beanName))
				dao = (T) ac.getBean(beanName);
			else
				dao = ReflectionUtils.instantiateClass(daoClass);
		}

		if (sistema != null) {
			dao.setSistema(sistema);
		}
		
		if (mov != null) {
			mov.addClosableResource(new SessionResource(dao.getSession()));
			dao.setUsuario(mov.getUsuarioLogado());
			if (mov.getCodMovimento() != null) {
				dao.setCodMovimento(mov.getCodMovimento().getId());
			}
		}
		
		return dao;
	}

	/**
	 * Retorna a sessao do hibernate existente em request. Implementação
	 * do padrão OpenSessionInView. 
	 * @param sistema 
	 */
	@SuppressWarnings("unchecked")
	public Session getSessionRequest(Integer sistema, HttpServletRequest req) {
		if (req == null)
			return null;
		
		//Atributo indicará se sessão está aberta
		Map<Integer, Session> sessions = (Map<Integer, Session>) req.getAttribute(Database.SESSION_ATRIBUTE);

		if (sessions == null) {
			sessions = new HashMap<Integer, Session>();
		}
		
		Session s = sessions.get(sistema);
		
		if (s == null || !s.isOpen()) {
			s = getSessionFactory(sistema).openSession(new LogInterceptor(sistema));
			SessionLogger.log(s, 'O');
			
			sessions.put(sistema, s);
		}
		
		req.setAttribute(Database.SESSION_ATRIBUTE, sessions);
		
		return s;
	}
	
	/**
	 * Retorna a sessao do hibernate existente em request. Implementação
	 * do padrão OpenSessionInView. 
	 * @param sistema 
	 */
	public Session getSessionRequest(HttpServletRequest req) {
		if (req == null)
			return null;
		return getSessionRequest((Integer) req.getAttribute("sistema"), req);
	}
	
	/**
	 * Destroi os session factorys para eliminar problemas de cache e memory leak
	 */
	public void closeSessionsFactory() {
		Collection<SessionFactory> factorys = sessionFactories.values();
		for ( SessionFactory f : factorys ) {
			f.close();
		}
		
		sessionFactories = new ConcurrentHashMap<Integer, SessionFactory>();
	}

}
