/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/02/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ava.forum.dominio.TipoForum;

/**
 * Classe que controla na view os tipos de Fóruns possíveis. 
 * 
 * @author UFRN
 *
 */
@Component("tipoForumBean")
@Scope("request")
public class TipoForumMBean extends SigaaAbstractController<TipoForum> {

	public TipoForumMBean() {
		setLabelCombo("descricao");
		obj = new TipoForum();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAllAtivo(TipoForum.class, "id", "descricao");
	}
	
}

