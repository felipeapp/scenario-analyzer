/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * MBean respons�vel por carregar as informa��es das p�ginas que mostram as informa��es 
 * sobre forma��o acad�mica.
 * 
 * A Forma��o Acad�mica est� sendo consultada no SIGRH atrav�s do Spring HTTP Invoker.
 * 
 * @author Gleydson
 */
@Scope("request")
@Component("formacaoAcademica")
public class FormacaoAcademicaMBean	extends AbstractControllerAtividades<FormacaoAcademicaDTO> {

	/** Cole��o de Forma��o Acad�mica que ser� listada */
	private Collection<FormacaoAcademicaDTO> listaFormacao = new ArrayList<FormacaoAcademicaDTO>();
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	@Deprecated
	public String getFormPage() { 
		return "/prodocente/atividades/FormacaoAcademica/form.jsp"; 
	}
	
	/**
	 * M�todo n�o � invocado por jsp
	 */
	@Override
	public String getListPage() { 
		return "/prodocente/atividades/FormacaoAcademica/lista.jsp"; 
	}
	
	/**
	 * Inicia o cadastro de forma��o acad�mica no SIGRH<br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Redireciona para a p�gina de listagem.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * A Forma��o Acad�mica est� sendo consultada no SIGRH atrav�s do Spring HTTP Invoker.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/atividades/FormacaoAcademica/lista.jsp</li>
	 * </ul>
	 */
	public Collection<FormacaoAcademicaDTO> getAllServidor() {
		return listaFormacao;
	}
}
