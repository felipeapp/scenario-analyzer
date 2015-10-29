package br.ufrn.ppgsc.scenario.analyzer.cdynamic.db;

import java.io.Serializable;
import java.util.List;

public interface GenericDAO<T extends Serializable> {

	public void clearSession();

	public T save(T instance);

	public T read(Class<T> clazz, long id);

	public List<T> readAll(Class<T> clazz);

}
