package br.com.ecommerce.arq.dao;

import java.util.Collection;

import org.hibernate.HibernateException;

import br.com.ecommerce.arq.dominio.CadastroDB;
import br.com.ecommerce.arq.dominio.PersistDB;
import br.com.ecommerce.arq.erros.DAOException;

public interface GenericDAO {

	/**
	 * Desassocia o objeto com a sess�o.
	 */
	public void detach(PersistDB p) throws DAOException;

	/**
	 * Busca um PersistDB pela sua chave prim�ria.
	 * @param <T> � o tipo do PersistDB.
	 * @param primaryKey � a chamve prim�ria.
	 * @param classe � a classe do PersistDB.
	 * 
	 * @return o PersistDB com a chave prim�ria informada.
	 * @throws DAOException
	 */
	public <T extends PersistDB> T findByPrimaryKey(int primaryKey, Class<T> classe) throws DAOException;

	/**
	 * Busca todos os PersistDBs associados � classe passada.
	 * @param <T> � o tipo do PersistDB
	 * @param classe � a classe que representa o tipo do PersistDB
	 * 
	 * @return Cole��o com os PersistDBs encontrados
	 * @throws DAOException
	 */
	public <T extends PersistDB> Collection<T> findAll(Class<T> classe) throws DAOException;
	
	/**
	 * Busca todos os PersistDBs associados � classe passada.
	 * @param <T> � o tipo do PersistDB
	 * @param classe � a classe que representa o tipo do PersistDB
	 * 
	 * @return Cole��o com os PersistDBs encontrados
	 * @throws DAOException
	 */
	public <T extends PersistDB> Collection<T> findAll(Class<T> classe, String order) throws DAOException;
	
	/**
	 * Busca todos os CadastroDBs ativos associados � classe passada.
	 * @param <T> � o tipo do PersistDB
	 * @param classe � a classe que representa o tipo do PersistDB
	 * 
	 * @return Cole��o com os CadastroDBs encontrados
	 * @throws DAOException
	 */
	public <T extends CadastroDB> Collection<T> findAllAtivos(Class<T> classe) throws DAOException;

	/**
	 * M�todo para efetuar a inser��o de um objeto mapedo no banco
	 * @param obj
	 * @throws DAOException
	 */
	public void save(PersistDB obj) throws DAOException;
	
	/**
	 * M�todo para efetuar a inser��o de um log de anota��o
	 * @param obj
	 * @throws DAOException
	 */
	public void saveAnnotation(PersistDB obj) throws DAOException;

	/**
	 * M�todo para efetuar a atualiz�o de um objeto mapedo no banco.
	 * @param obj
	 * @throws DAOException
	 */
	public void update(PersistDB obj) throws DAOException;

	/**
	 * M�todo para efetuar a remo��o de um objeto mapedo no banco
	 * @param obj
	 * @throws DAOException
	 */
	public void delete(PersistDB obj) throws DAOException;

	/**
	 * M�todo para se fechar a sess�o.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public void close() throws HibernateException, DAOException;
	
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
	public <T> Collection<T> findByExactField(Class<T> classe, String field, Object value) throws DAOException;
	

}
