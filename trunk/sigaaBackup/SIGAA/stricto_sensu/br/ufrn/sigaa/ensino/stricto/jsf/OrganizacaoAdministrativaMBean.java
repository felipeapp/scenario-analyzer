/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 25, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.stricto.dominio.OrganizacaoAdministrativa;

/**
 *
 * @author Victor Hugo
 */
@Component("organizacaoAdministrativa") @Scope("request")
public class OrganizacaoAdministrativaMBean extends SigaaAbstractController<OrganizacaoAdministrativa> {

	public OrganizacaoAdministrativaMBean() {
		obj = new OrganizacaoAdministrativa();
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(OrganizacaoAdministrativa.class, "id", "denominacao");
	}

}
