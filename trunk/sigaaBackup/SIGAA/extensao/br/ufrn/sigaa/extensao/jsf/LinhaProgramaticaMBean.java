/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/10/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.extensao.dominio.LinhaProgramatica;

/*******************************************************************************
 * Gerado pelo CrudBuilder
 * 
 * Controla opera��es com Linha program�tica.
 * 
 * 
 ******************************************************************************/
@Component("linhaProgramatica")
@Scope("request")
public class LinhaProgramaticaMBean extends AbstractControllerCadastro<LinhaProgramatica> {
    
	public LinhaProgramaticaMBean() {
		obj = new LinhaProgramatica();
	}

	/**
	 * Lista todas as linhas program�ticas cadastradas no banco de modo a
	 * possibilitar que a lista seja exibida em um componente de listagem no
	 * navegador
	 * 
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(LinhaProgramatica.class, "id", "descricao");
	}
}
