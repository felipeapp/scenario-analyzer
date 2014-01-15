/*
 * Universidade Federal do Rio Grande do Norte

 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '14/04/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.forum.dominio.OrdemMensagensForum;

/**
 * Classe que controla na view os tipos ordena��o poss�veis para mensagens do F�runs. 
 * 
 * @author ilueny santos
 *
 */
@Component("ordemMensagensForumBean")
@Scope("request")
public class OrdemMensagensForumMBean extends SigaaAbstractController<OrdemMensagensForum> {

	public OrdemMensagensForumMBean() {
		setLabelCombo("descricao");
		obj = new OrdemMensagensForum();
	}
	
}