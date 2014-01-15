/**
 * 
 */
package br.ufrn.comum.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.comum.dao.SistemaDao;
import br.ufrn.comum.dominio.Sistema;

/**
 * MBean utilizado para realizar a busca sobre os subsistemas  de um sistema.
 * 
 * @author Mário Melo
 *
 */
@Component 
@Scope("request")
public class SubSistemaMBean extends ComumAbstractController<Sistema> {
	
	
	public Collection<SelectItem> getAllCombo() {
		return toSelectItems(getDAO(SistemaDao.class).findSubsistemasBySistema(getSistema()), "id", "nome");
	}
	
			
}
