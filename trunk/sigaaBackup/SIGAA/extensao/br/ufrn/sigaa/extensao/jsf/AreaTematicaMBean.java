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

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;

/*******************************************************************************
 * Gerado pelo CrudBuilder
 * 
 * Utilizado na pagina de dados gerais do cadastro das a��es de extens�o.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Component("areaTematica")
@Scope("request")
public class AreaTematicaMBean extends AbstractControllerCadastro<AreaTematica> {

	public AreaTematicaMBean() {
		obj = new AreaTematica();
	}

	/**
	 * Permite Lista de todas as areas tem�ticas em um componente de sele��o
	 * combobox, listbox, etc.
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(AreaTematica.class, "id", "descricao");
	}
	
	/**
	 * Inicia o cadastro de tipos de �rea Tem�tica.
	 * Chamado por:
	 * <ul><li>/sigaa.war/extensao/menu.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastro() throws ArqException {
	    prepareMovimento(ArqListaComando.CADASTRAR);
	    return forward("/extensao/AreaTematica/form.jsp");
	}

	
}
