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
import br.ufrn.sigaa.extensao.dominio.FatorGerador;

/*******************************************************************************
 * Gerado pelo CrudBuilder
 * 
 * Classe utilizada no contexto de PrestacaoServicos. O modulo de prestação de
 * serviços ainda não está totalmente definido no SIGAA.
 * 
 ******************************************************************************/
@Component("fatorGerador")
@Scope("request")
public class FatorGeradorMBean extends AbstractControllerCadastro<FatorGerador> {
    
	public FatorGeradorMBean() {
		obj = new FatorGerador();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(FatorGerador.class, "id", "descricao");
	}
	
	/**
	 * Inicia o cadastro de tipos de Fator Gerador.
	 * Chamado por:
	 * <ul><li>/sigaa.war/extensao/menu.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastro() throws ArqException {
	    prepareMovimento(ArqListaComando.CADASTRAR);
	    return forward("/extensao/FatorGerador/form.jsp");
	}
}
