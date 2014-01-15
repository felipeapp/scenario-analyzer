/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
import br.ufrn.sigaa.extensao.dominio.FormaCompromisso;

/*******************************************************************************
 * Gerado pelo CrudBuilder
 * 
 * Classe utilizada no contexto de PrestacaoServicos. O modulo de prestação de
 * serviços ainda não está totalmente definido no SIGAA.
 * 
 ******************************************************************************/
@Component("formaCompromisso")
@Scope("request")
public class FormaCompromissoMBean extends AbstractControllerCadastro<FormaCompromisso> {
    
	public FormaCompromissoMBean() {
		obj = new FormaCompromisso();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(FormaCompromisso.class, "id", "descricao");
	}
	
	/**
	 * Inicia o cadastro de tipos de Formas de Compromisso.
	 * Chamado por:
	 * <ul><li>/sigaa.war/extensao/menu.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastro() throws ArqException {
	    prepareMovimento(ArqListaComando.CADASTRAR);
	    return forward("/extensao/FormaCompromisso/form.jsp");
	}
}
