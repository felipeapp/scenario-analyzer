/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * MBean respons�vel por carregar as informa��es das p�ginas que mostram as informa��es 
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
	 * Retorna todas os registros de forma��o acad�mica da base de dados.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/atividades/FormacaoAcademica/form.jsp</li>
	 * </ul>
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(FormacaoTese.class, "id", "descricao");
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}

	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	protected void afterCadastrar() {
		obj = new FormacaoTese();
	}
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public String getUrlRedirecRemover() {
		return "/sigaa/prodocente/atividades/FormacaoTese/lista.jsf";
	}

}
