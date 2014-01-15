package br.ufrn.sigaa.monitoria.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;

/**
 * MBean Responsável pelo status de avaliação.
 */
@Component("statusAvaliacao")
@Scope("request")
public class StatusAvaliacaoMBean extends AbstractControllerCadastro<StatusAvaliacao> {
    
	public StatusAvaliacaoMBean() {
		obj = new StatusAvaliacao();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(StatusAvaliacao.class, "id", "descricao");
	}
}
