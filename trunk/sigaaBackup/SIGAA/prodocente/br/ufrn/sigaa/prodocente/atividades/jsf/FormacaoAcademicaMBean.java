/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.integracao.dto.FormacaoAcademicaDTO;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * MBean responsável por carregar as informações das páginas que mostram as informações 
 * sobre formação acadêmica.
 * 
 * A Formação Acadêmica está sendo consultada no SIGRH através do Spring HTTP Invoker.
 * 
 * @author Gleydson
 */
@Scope("request")
@Component("formacaoAcademica")
public class FormacaoAcademicaMBean	extends AbstractControllerAtividades<FormacaoAcademicaDTO> {

	/** Coleção de Formação Acadêmica que será listada */
	private Collection<FormacaoAcademicaDTO> listaFormacao = new ArrayList<FormacaoAcademicaDTO>();
	
	/**
	 * Método não é invocado por jsp
	 */
	@Override
	@Deprecated
	public String getFormPage() { 
		return "/prodocente/atividades/FormacaoAcademica/form.jsp"; 
	}
	
	/**
	 * Método não é invocado por jsp
	 */
	@Override
	public String getListPage() { 
		return "/prodocente/atividades/FormacaoAcademica/lista.jsp"; 
	}
	
	/**
	 * Inicia o cadastro de formação acadêmica no SIGRH<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/prodocente/atividades/FormacaoAcademica/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarCadastro(){		
		getCurrentSession().setAttribute("acao", "formacaoAcademica.entrarSigaa");
		return redirect("/logonSIGRH");				
	}	

	/**
	 * Redireciona para a página de listagem.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() {
		FormacaoAcademicaRemoteService service = getMBean("formacaoAcademicaInvoker");		
		if (service != null){
			try {
				listaFormacao = service.consultarFormacaoAcademica(getServidorUsuario().getId(), null, null, null, null, null, null);
			} catch (RemoteAccessException e) {
				tratamentoErroPadrao(e);
			}					
		}
		return forward(getListPage());
	}

	/**
	 * Retorna os servidores existentes na base de dados.
	 * A Formação Acadêmica está sendo consultada no SIGRH através do Spring HTTP Invoker.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/atividades/FormacaoAcademica/lista.jsp</li>
	 * </ul>
	 */
	public Collection<FormacaoAcademicaDTO> getAllServidor() {
		return listaFormacao;
	}
}
