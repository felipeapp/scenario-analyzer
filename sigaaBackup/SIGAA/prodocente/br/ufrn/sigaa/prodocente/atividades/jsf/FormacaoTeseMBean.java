/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoTese;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * MBean responsável por carregar as informações das páginas que mostram as informações 
 * sobre afastamento.
 * 
 * @author Gleydson
 */
@Scope("request")
@Component("formacaoTese")
public class FormacaoTeseMBean extends 
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoTese> {
	
	public FormacaoTeseMBean() {
		obj = new FormacaoTese();
	}
	
	/**
	 * Retorna todas os registros de formação acadêmica da base de dados.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/atividades/FormacaoAcademica/form.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(FormacaoTese.class, "id", "descricao");
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}

	/**
	 * Método não é invocado por jsp
	 */
	@Override
	protected void afterCadastrar() {
		obj = new FormacaoTese();
	}
	
	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public String getUrlRedirecRemover() {
		return "/sigaa/prodocente/atividades/FormacaoTese/lista.jsf";
	}

}
