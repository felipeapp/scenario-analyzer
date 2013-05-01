package br.com.ecommerce.arq.util;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.ecommerce.arq.dao.GenericDAO;

/**
 * Utilit�rio para tratar as sess�es com o banco.
 * @author Rodrigo Dutra de Oliveira
 *
 */
public class SessionUtil {

	public static final String SESSIONS_ATRIBUTE = "_session";
	
	/**
	 * Implementa��o do padr�o de projeto singleton.
	 */
	private static SessionUtil singleton;
	
	private SessionUtil(){
		
	}
	
	/**
	 * Retorna a inst�ncia desta classe.
	 * 
	 * @return singleton
	 */
	public static SessionUtil getIntance(){
		if(singleton == null)
			singleton = new SessionUtil();
		return singleton;
	}
	
	/**
	 * M�todo respons�vel por possibilitar o acesso � request do usu�rio.
	 * 
	 * @return request associada
	 */
	private HttpServletRequest getCurrentUserRequest(){
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	/**
	 * Registra um dao na sess�o do usu�rio.
	 * Usado para uma implementa��o tempor�ria do Open Session in View
	 * 
	 * @param dao
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public void registerDAO(GenericDAO dao){
		List<GenericDAO> _sessions = (List<GenericDAO>) getCurrentUserRequest().getSession().getAttribute(SESSIONS_ATRIBUTE);
		
		if(_sessions == null)
			_sessions = new ArrayList<GenericDAO>();
		
		_sessions.add(dao);
		
		getCurrentUserRequest().getSession().setAttribute(SESSIONS_ATRIBUTE, _sessions);
	}
	
	/**
	 * Retorna os daos registrados e os remove.
	 * 
	 * @return
	 */
	@Deprecated
	public List<GenericDAO> getAndUnregisterDAOs(HttpServletRequest request){
		return getAndUnregisterDAOs(request.getSession());
	}

	/**
	 * Retorna os daos registrados e os remove.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	public  List<GenericDAO> getAndUnregisterDAOs(HttpSession session) {
		try{
			List<GenericDAO> listaDAOs = null;

			if(session.getAttribute(SESSIONS_ATRIBUTE) != null)
				listaDAOs = (List<GenericDAO>) session.getAttribute(SESSIONS_ATRIBUTE);
			
			if(listaDAOs == null)
				listaDAOs = new ArrayList<GenericDAO>();
			
			return listaDAOs;
		}finally{
			session.setAttribute(SESSIONS_ATRIBUTE, new ArrayList<GenericDAO>());
		}
	}
}
