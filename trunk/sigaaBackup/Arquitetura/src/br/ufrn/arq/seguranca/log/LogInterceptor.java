/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 31/03/2005
 */
package br.ufrn.arq.seguranca.log;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.CallbackException;
import org.hibernate.EntityMode;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe utilizada para ser um interceptor do Hibernate e logar as operações de
 * modificação no banco de dados
 *
 * @author Gleydson Lima
 *
 */
public class LogInterceptor implements Interceptor {

	/** Coleção de objetos inseridos no banco */
	private Set<Object> inserts = new HashSet<Object>();

	/** Coleção de objetos atualizados no banco */
	private Set<Object> updates = new HashSet<Object>();

	/** Coleção de objetos removidos do banco */
	private Set<Object> deletes = new HashSet<Object>();

	/** Quando true, desabilita o log */
	private boolean disabled;

	/** Informações para log, código do processador. */
	private int codMovimento;
	
	/** Sistema em que a alteração na base de dados está sendo realizada */
	private int sistema;

	/** Usuário que realiza a alteração na base de dados  */
	private UsuarioGeral usuario;

	public LogInterceptor() {
		
	}
	
	public LogInterceptor(int sistema) {
		this.sistema = sistema;
	}

	public boolean onLoad(Object arg0, Serializable arg1, Object[] arg2, String[] arg3, Type[] arg4) throws CallbackException {
		return false;
	}

	public synchronized String toStringLog(String[] property, Object[] informacoes, Type[] types) {

		try {
			StringBuffer modificacoes = new StringBuffer(200);
			for (int a = 0; a < property.length; a++) {
				if (types[a].isCollectionType()) {
					modificacoes.append(property[a] + "=");
					Collection<?> col = (Collection<?>) informacoes[a];
					if (col != null) {
						for (Object object : col) {
							modificacoes.append(ReflectionUtils.evalProperty(object, "id") + ",");
						}
						
						modificacoes.append("\n");
					}
				} else if (types[a].isAssociationType()) {
					modificacoes.append(property[a] + "=" + ReflectionUtils.evalProperty(informacoes[a], "id") + "\n");
				} else {
					modificacoes.append(property[a] + "=" + informacoes[a] + "\n");
				}
			}
			return modificacoes.toString();
		} catch (Exception e) {
			return null;
		}
	}

	
	/**
	 * Atualizando dado
	 */
	@SuppressWarnings("unchecked")
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
		if (!(entity instanceof LogDB || entity instanceof LogMovimento)) {
			@SuppressWarnings("rawtypes")
			ArrayList update = new ArrayList();
			update.add(entity);
			update.add(toStringLog(propertyNames, currentState, types));
			updates.add(update);
		}
		
		return false;
	}
	
	/**
	 * Criando dado
	 */
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
		if (!(entity instanceof LogDB || entity instanceof LogMovimento)) {
			inserts.add(entity);
		}
		
		return false;
	}
	
	/**
	 * Removendo dado
	 */
	@SuppressWarnings("unchecked")
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
		@SuppressWarnings("rawtypes")
		ArrayList delete = new ArrayList();
		delete.add(entity);
		delete.add(toStringLog(propertyNames, state, types));
		deletes.add(delete);
	}

	public void preFlush(@SuppressWarnings("rawtypes") Iterator it) throws CallbackException {
	}

	/**
	 * Persistência do log de alteração na base de dados
	 */
	public void postFlush(@SuppressWarnings("rawtypes") Iterator arg0) throws CallbackException {

		if (!disabled) { //Caso log esteja habilitado
			
			//Logs de INSERÇÕES
			Iterator<Object> it = inserts.iterator();
			while (it.hasNext()) {
				try {
					LogProcessorDelegate.getInstance().writeDatabaseLog((PersistDB) it.next(), 'I', codMovimento, usuario, sistema, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			inserts.clear();

			//Logs de ATUALIZAÇÕES
			it = updates.iterator();
			while (it.hasNext()) {
				try {
					ArrayList<?> update = (ArrayList<?>) it.next();
					LogProcessorDelegate.getInstance().writeDatabaseLog( (PersistDB) update.get(0), 'U', codMovimento, usuario, sistema, (String) update.get(1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			updates.clear();

			//Logs de REMOÇÕES
			it = deletes.iterator();
			while (it.hasNext()) {
				try {
					ArrayList<?> delete = (ArrayList<?>) it.next();
					LogProcessorDelegate.getInstance().writeDatabaseLog( (PersistDB) delete.get(0), 'D', codMovimento, usuario, sistema, (String) delete.get(1));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			deletes.clear();
		}
	}

	public Boolean isUnsaved(Object arg0) {
		return null;
	}

	public int[] findDirty(Object arg0, Serializable arg1, Object[] arg2, Object[] arg3, String[] arg4, Type[] arg5) {
		return null;
	}

	public Object instantiate(Class<?> arg0, Serializable arg1) throws CallbackException {
		return null;
	}

	public int getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(int codMovimento) {
		this.codMovimento = codMovimento;
	}

	public UsuarioGeral getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void onCollectionRecreate(Object arg0, Serializable arg1) throws CallbackException {

	}

	public void onCollectionRemove(Object arg0, Serializable arg1) throws CallbackException {

	}

	public void onCollectionUpdate(Object arg0, Serializable arg1) throws CallbackException {

	}

	public Boolean isTransient(Object arg0) {
		return null;
	}

	public Object instantiate(String arg0, EntityMode arg1, Serializable arg2) throws CallbackException {
		return null;
	}

	public String getEntityName(Object arg0) throws CallbackException {
		return null;
	}

	public Object getEntity(String arg0, Serializable arg1) throws CallbackException {
		return null;
	}

	public void afterTransactionBegin(Transaction arg0) {

	}

	public void beforeTransactionCompletion(Transaction arg0) {

	}

	public void afterTransactionCompletion(Transaction arg0) {

	}

	public String onPrepareStatement(String sql) {
		return sql;
	}

	public int getSistema() {
		return sistema;
	}

	public void setSistema(int sistema) {
		this.sistema = sistema;
	}

}