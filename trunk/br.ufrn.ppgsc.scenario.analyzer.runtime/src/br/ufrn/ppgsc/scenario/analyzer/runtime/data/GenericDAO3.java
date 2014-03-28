//package br.ufrn.ppgsc.scenario.analyzer.runtime.data;
//
//import java.io.Serializable;
//import java.util.List;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
//
//// Esta classe usa libs do Hibernate 3
//public abstract class GenericDAO3<T extends Serializable> {
//
//	public abstract T persist(T instance);
//
//	public abstract T read(Class<T> clazz, long id);
//
//	public abstract List<T> readAll(Class<T> clazz);
//
//	private static Session s;
//
//	public static Session getSession() {
//		if (s == null) {
//			SessionFactory sf = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
//			s = sf.openSession();
//		}
//
//		return s;
//	}
//
//}
