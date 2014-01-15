/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/01/2010
 *
 */	
package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.TipoCicloFormacao;

/**
 * MBean usado para a geração de Comboboxes de objetos da classe TipoCicloFormacao
 * 
 * @author Rômulo Augusto
 *
 */
@Component @Scope("request")
public class TipoCicloFormacaoMBean extends SigaaAbstractController<TipoCicloFormacao> {

	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		
		Collection<SelectItem> items = getAll(TipoCicloFormacao.class, "id", "descricao");
		
		//Reordena a coleção para seguir a ordem dos "values", não dos "labels".
		Collections.sort((List<SelectItem>) items, new Comparator<SelectItem>() {

			public int compare(SelectItem o1, SelectItem o2) {
				
				Integer value1 = new Integer(o1.getValue().toString());
				Integer value2 = new Integer(o2.getValue().toString());
				
				return value1.compareTo(value2);
			}
			
		});
		
		return items;
	}
}
