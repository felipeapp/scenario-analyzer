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
import br.ufrn.sigaa.extensao.dominio.LocalRealizacao;

/*******************************************************************************
 * Gerado pelo CrudBuilder
 * 
 * Controla opera��es realizadas com o local de realiza��o da a��o de extens�o.
 * 
 ******************************************************************************/
@Component("localRealizacao")
@Scope("request")
public class LocalRealizacaoMBean extends AbstractControllerCadastro<LocalRealizacao> {
    
	public LocalRealizacaoMBean() {
		obj = new LocalRealizacao();
	}

	/**
	 * Lista todos os locais de realizac�o cadastradas no banco de modo a
	 * possibilitar que a lista seja exibida em um componente de listagem no
	 * navegador 
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(LocalRealizacao.class, "id", "descricao");
	}
}
