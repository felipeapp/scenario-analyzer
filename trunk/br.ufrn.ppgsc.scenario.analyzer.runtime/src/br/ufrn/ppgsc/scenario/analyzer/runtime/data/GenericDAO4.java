//package br.ufrn.ppgsc.scenario.analyzer.runtime.data;
//
//import java.io.Serializable;
//import java.util.List;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.boot.registry.StandardServiceRegistry;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.cfg.Configuration;
//
//public abstract class GenericDAO4<T extends Serializable> {
//
//	public abstract T persist(T instance);
//
//	public abstract T read(Class<T> clazz, long id);
//
//	public abstract List<T> readAll(Class<T> clazz);
//
//	private static Session s;
//	private static StandardServiceRegistry serviceRegistry;
//
//	public static Session getSession() {
//		if (s == null) {
//			Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
//
//			serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
//			SessionFactory sf = configuration.buildSessionFactory(serviceRegistry);
//
//			s = sf.openSession();
//		}
//
//		return s;
//	}
//
//	public static void destroy() {
//		StandardServiceRegistryBuilder.destroy(serviceRegistry);
//	}
//	
//}
