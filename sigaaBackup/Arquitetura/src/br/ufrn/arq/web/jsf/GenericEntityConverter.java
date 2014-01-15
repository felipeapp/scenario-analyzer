/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 15/10/2009
 */
package br.ufrn.arq.web.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.DAOException;

/**
 * Conversor genérico para entidades do sistema.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class GenericEntityConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Class<? extends PersistDB> classe = getTargetClass(context, component);
		return fromIdentifier(value, classe);
	}

	public String getAsString(FacesContext context, UIComponent component, Object object) {
		Class<? extends PersistDB> classe = getTargetClass(context, component);
		return toIdentifier(object, classe);
	}

	// Utilizado para descobrir o tipo do objeto que está sofrendo a conversão
	@SuppressWarnings("unchecked")
	private Class<? extends PersistDB> getTargetClass(final FacesContext context, final UIComponent component) {
		return (Class<? extends PersistDB>) component.getValueExpression("value").getType(context.getELContext());
	}
	
	private String toIdentifier(Object value, Class<? extends PersistDB> targetClass) {
		if (value == null) return "";
		if (!value.getClass().equals(targetClass)) return "";
		return String.valueOf(((PersistDB) value).getId());
	}

	private Object fromIdentifier(String value, Class<? extends PersistDB> classe) {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		int sistema = Integer.parseInt(String.valueOf(req.getAttribute("sistema")));
		
		GenericDAO dao = new GenericDAOImpl(sistema);

		if (isEmpty(value)) return null;
		
		try {
			return dao.findByPrimaryKey(Integer.parseInt(value), classe);
		} catch (DAOException e) {
			return null;
		} finally {
			dao.close();
		}
	}
	
}
