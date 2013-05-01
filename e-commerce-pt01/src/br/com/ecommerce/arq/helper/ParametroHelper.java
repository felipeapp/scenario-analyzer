package br.com.ecommerce.arq.helper;

import org.hibernate.HibernateException;

import br.com.ecommerce.arq.dao.DAOFactory;
import br.com.ecommerce.arq.dao.ParametroDAO;
import br.com.ecommerce.arq.erros.DAOException;

/**
 * Classe helper para tratar com par�metros no sistema.
 * @author Rodrigo Dutra de Oliveira
 *
 */
public class ParametroHelper {
	
	/**
	 * Singleton desta classe.
	 */
	private static ParametroHelper singleton;

	private ParametroHelper(){
		
	}
	
	/**
	 * @return a inst�ncia desta classe.
	 */
	public static ParametroHelper getInstance(){
		if(singleton == null)
			singleton = new ParametroHelper();
		return singleton;
	}
	
	/**
	 * M�todo usado para se obter um parametro passando seu c�digo.
	 * 
	 * @return o valor do par�metro.
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String getParamametro(String codigo) throws DAOException{
		return DAOFactory.getInstance().getDAO(ParametroDAO.class, null).getParametro(codigo);
	}
	
	/**
	 * M�todo usado para se obter um valor inteiro de um par�metro.
	 *  
	 * @param codigo
	 * @return
	 * @throws NumberFormatException
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int getIntParametro(String codigo) throws NumberFormatException, DAOException{
		return Integer.valueOf(getParamametro(codigo)); 
	}

	/**
	 * Busca por um par�metro booleano.
	 * 
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public boolean getBooleanParametro(String codigo) throws DAOException{
		return Boolean.valueOf(getParamametro(codigo));
	}
}
