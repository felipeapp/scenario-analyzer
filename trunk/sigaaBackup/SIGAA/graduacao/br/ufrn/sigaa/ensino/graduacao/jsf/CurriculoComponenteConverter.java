/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 04/06/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;

/**
 * Conversor para transformar id de componente curricular em objetos
 * ComponenteCurricular. Usado no cadastro de grupos de optativas.
 * 
 * @see GrupoOptativasMBean
 * @author David Pereira
 *
 */
@Component
public class CurriculoComponenteConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		int id = Integer.valueOf(value);
		GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
		
		try {
			CurriculoComponente cc = dao.findByPrimaryKey(id, CurriculoComponente.class);
			return cc;

		} catch (DAOException e) {
			return null;
		} finally {
			dao.close();
		}
		
	}

	public String getAsString(FacesContext context, UIComponent component, Object obj) {
		CurriculoComponente cc = (CurriculoComponente) obj;
		return String.valueOf(cc.getId());
	}

}
