/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/04/2009'
 *
 */
package br.ufrn.sigaa.prodocente.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.prodocente.atividades.dominio.BolsaProdocente;

/**
 * 
 * @author Jean Guerethes
 *
 */
@Deprecated
@Component("bolsaProdocenteMBean")
@Scope("request")
public class BolsaProdocenteMBean extends SigaaAbstractController<BolsaProdocente> {

	public BolsaProdocenteMBean(){
		
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(BolsaProdocente.class, "id", "denominacao");
	}
	
}
