/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/11/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.nee.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.nee.dominio.StatusAtendimento;

/**
 * @author Rafael Gomes
 *
 */
@Component("statusAtendimento") 
public class StatusAtendimentoMBean extends SigaaAbstractController<StatusAtendimento>{

	
	@Override
	public Collection<SelectItem> getAllCombo(){
		return  getAllAtivo(StatusAtendimento.class, "id", "denominacao");
	}
}
