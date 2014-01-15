/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 28/02/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;

/**
 * MBean que trata de objetos ConvenioAcademico. Não está associado a caso de uso específico.
 * @author André
 *
 */
@Component("convenioAcademico")
@Scope("request")
public class ConvenioAcademicoMBean extends AbstractControllerCadastro<ConvenioAcademico>{

	public ConvenioAcademicoMBean() {
		obj = new ConvenioAcademico();
	}
	public Collection<SelectItem> getAllCombo() {
		return getAll(ConvenioAcademico.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new ConvenioAcademico();
	}
}
