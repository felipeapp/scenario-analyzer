/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 15/09/2004
 */
package br.ufrn.arq.dao;

import java.io.Serializable;
import java.util.Collection;

import org.hibernate.Session;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Esta interface é implementada por todos os outros DAOs e fornece as
 * funcionalidades para mapeamento básico.
 *
 * @author Gleydson Lima
 * @author David Pereira
 */
public interface GenericDAO {

	public void create(PersistDB obj) throws DAOException;

	public void createNoFlush(PersistDB obj) throws DAOException;

	public void remove(PersistDB obj) throws DAOException;

	public void updateNoFlush(PersistDB obj) throws DAOException;

	public void update(PersistDB obj) throws DAOException;

	public void update(String sql, Object... params);

	public void createOrUpdate(PersistDB obj) throws DAOException;

	public <T extends PersistDB> T findByPrimaryKey(int id, Class<T> classe) throws DAOException;

	public <T extends PersistDB> T findByPrimaryKeyLock(Serializable id, Class<?> classe) throws DAOException;

	public <T> Collection<T> findAll(Class<T> classe) throws DAOException;

	public <T> Collection<T> findAllAtivos(Class<T> classe, String... orderColumn)
			throws DAOException;

	public <T> Collection<T> findAll(Class<T> classe, String orderBy[],
			String ascDesc[]) throws DAOException;

	public <T> Collection<T> findAll(Class<T> classe, PagingInformation paging)
			throws DAOException;

	public <T> Collection<T> findAll(Class<T> classe, PagingInformation paging,
			String orderField, String orderType) throws DAOException;

	/**
	 * Retorna coleção com todos os elementos da classe no banco ordenados
	 * de acordo com os parâmetros.
	 *
	 * @param classe
	 * @param orderBy atributo que ordenará a lista
	 * @param ascDesc asc ou desc
	 * @return
	 * @throws DAOException
	 */
	public <T> Collection<T> findAll(Class<T> classe, String orderBy,
			String ascDesc) throws DAOException;

	public void close();

	public void detach(PersistDB obj) throws DAOException;

	public void lock(PersistDB obj) throws DAOException;

	/**
	 * Adiciona ordenação no DAO.
	 *
	 * @param order atributo a ser ordenado
	 * @param ascDesc (asc ou desc)
	 */
	public void addOrder(String order, String ascDesc);

	/**
	 * Remove as ordenações inseridas para este DAO
	 *
	 */
	public void clearOrder();

	/**
	 * Disabilita o log no DAO
	 */
	public void disableLog();

	/** Busca todos pelo Iterate */

	public Collection<?> findAllIterate(String entidade, String orderBy) throws DAOException;

	/**
	 * Método inicializa um objeto com seus dados no BD com base no seu ID
	 *
	 * @param obj
	 * @throws DAOException
	 */
	public void initialize(PersistDB obj) throws DAOException;

	/**
	 * Método que repopula os dados de um objeto persistível
	 *
	 * @param <T>
	 * @param obj
	 * @return
	 * @throws DAOException
	 */
	public <T extends PersistDB> T refresh(T obj) throws DAOException;

	/**
	 * Método retorna o objeto do último registro de uma classe
	 *
	 * @param classe
	 * @return o objeto do Último registro
	 * @throws DAOException
	 */
	public PersistDB findLast(Class<?> classe) throws DAOException;

	/**
	 * Busca todos os registros de uma classe EXATAMENTE com o valor do campo
	 * passado por parametro
	 *
	 * @param classe
	 * @param field
	 * @param value
	 * @return lista de registros
	 * @throws DAOException
	 */
	public <T> Collection<T> findByExactField(Class<T> classe, String field, Object value, PagingInformation paging) throws DAOException;

	public <T> Collection<T> findByExactField(Class<T> classe, String field, Object value) throws DAOException;
	
	/**
	 * Permite a busca por mais de um dos compos.
	 * 
	 * @param <T>
	 * @param classe
	 * @param fields
	 * @param values
	 * @return
	 * @throws DAOException
	 */
	public <T> Collection<T> findByExactField(Class<T> classe, String[] fields, Object[] values) throws DAOException;
	
	public <T> T findByExactField(Class<T> classe, String[] fields, Object[] values, boolean limit) throws DAOException;

	public <T> Collection<T> findByExactField(Class<T> classe, String field, Object value, String orderType, String... orderFields) throws DAOException;

	/**
	 * busca UMA Entidade por um parametro especifico
	 * Observe que se a consulta findByExactField retornar mais de um registro só será retornado o primeiro
	 * @param <T>
	 * @param classe
	 * @param field
	 * @param value
	 * @param limit limita a um unico resultado
	 * @return
	 * @throws DAOException
	 */
	public <T> T findByExactField(Class<T> classe, String field, Object value, boolean limit) throws DAOException;

	/**
	 * Busca todos os registros ativos de uma classe EXATAMENTE com o valor do campo
	 * passado por parametro
	 * @param <T>
	 * @param classe
	 * @param field
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	public <T> Collection<T> findAtivosByExactField(Class<T> classe, String field, Object value) throws DAOException;
	
	public <T> Collection<T> findAtivosByExactField(Class<T> classe, String field, Object value, String orderBy, String ascDesc) throws DAOException;
	
	public <T> Collection<T> findAtivosByExactField(Class<T> classe, String field, Object value, String[] orderBy, String[] ascDesc) throws DAOException;

	/**
	 * Busca todos os registros de uma classe a partir do valor em STRING do
	 * campo passado por parametro (usando like '%COLUNA%')
	 *
	 * @param classe
	 * @param field
	 * @param value
	 * @return
	 * @throws DAOException
	 */
	public <T> Collection<T> findByLikeField(Class<T> classe, String field, Object value, PagingInformation paging) throws DAOException;

	/**
	 * Busca todos os registros de uma classe a partir do valor em STRING do
	 * campo passado por parametro (usando like 'COLUNA%')
	 *
	 * @param classe
	 * @param field
	 * @param value
	 * @param paging
	 * @return
	 * @throws DAOException
	 */
	public <T> Collection<T> findByLikeInitField(Class<T> classe, String field, Object value, PagingInformation paging) throws DAOException;

	public <T> Collection<T> findByLikeField(Class<T> classe, String field, Object value) throws DAOException;

	public <T> Collection<T> findByLikeField(Class<T> classe, String field, Object value, PagingInformation paging, String orderType, String... orderFields) throws DAOException;

	public void setUsuario(UsuarioGeral usuario);

	public void setCodMovimento(int codigo);

	public <T> Collection<T> findAllProjection(Class<T> classe, String... fields) throws DAOException;

	public <T> Collection<T> findAllProjection(Class<T> classe, String orderBy, String ascDesc, String... fields) throws DAOException;

	public <T> Collection<T> findAllProjection(Class<T> classe, PagingInformation paging, String... fields) throws DAOException;

	public Session getSession() throws DAOException;

	public void setSession(Session session);

	public void setSistema(int sistema);

	public void clearSession();

	public void updateField(Class<?> classe, Integer id, String campo, Object valor) throws DAOException;

	public void updateFields(Class<?> classe, Integer id, String[] campos, Object[] valores) throws DAOException;

	public <T extends PersistDB> T findByPrimaryKey(int id, Class<T> classe, String... atributos) throws DAOException;

	/**
	 * Busca uma entidade por sua chave primária e inicializa os atributos
	 * informados
	 *
	 * @param <T>
	 * @param id
	 * @param classe
	 * @param atributos
	 * @return
	 * @throws DAOException
	 */
	public <T extends PersistDB> T findAndFetch(int id, Class<T> classe, String... atributos) throws DAOException;

	public int incrNumField(Class<? extends PersistDB> classe, Integer id, String campo) throws DAOException;

	public int decrNumField(Class<? extends PersistDB> classe, Integer id, String campo) throws DAOException;

	public int count(String sqlQuery);

	public int count(String sqlQuery, Sistema sistema);

	public int getNextSeq(String esquema, String nomeSequencia);

	/**
	 * Recupera o valor atual de uma sequencia de esquema e nome passados como argumento.
	 * Se a sequencia nao existir, retorna 0 (Zero).
	 * @param esquema
	 * @param nomeSequencia
	 * @return
	 */
	public int getCurrentSeq(String esquema, String nomeSequencia);

	/**
	 * 
	 * @param openSessionInView
	 */
	public void setOpenSessionInView(boolean openSessionInView);

	/**
	 * 
	 * @return
	 */
	public int getSistema();

}