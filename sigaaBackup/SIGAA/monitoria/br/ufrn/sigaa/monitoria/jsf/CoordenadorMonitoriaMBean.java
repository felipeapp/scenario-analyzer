package br.ufrn.sigaa.monitoria.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Controlador usado para as operações do coordenador de monitoria
 *
 * @author Gleydson
 *
 */

@Component("coordMonitoria")
@Scope("request")
public class CoordenadorMonitoriaMBean extends SigaaAbstractController<Object> {

	public String cadastrarProjeto() {
		return forward("/monitoria/ProjetoMonitoria/form.jsp");
	}
	
	public String definiMonitores() {
		return forward("/monitoria/AssociarMonitorDocente/lista.jsp");
	}

	public String situacaoProjeto() {
		return forward("/monitoria/ProjetoMonitoria/consultar_projeto.jsp");
	}

	public String consultaMonitores() {
		return forward("/monitoria/ConsultarMonitor/lista.jsp");
	}

	public String resumoSID() {
		return forward("/monitoria/ResumoSid/lista.jsp");
	}
	
	
	public String cadastrarAvisoProjeto() {
		return forward("/monitoria/CadastrarAvisoProjeto/lista.jsp");
	}
	

	public String cadastrarOrientacao() {
		return forward("/monitoria/CadastrarOrientacao/lista.jsp");
	}

	/**
	 * Chamada pelo chefe de departamento
	 * 
	 * Este método é chamado na jsp sigaa.war/monitoria/AutorizacaoProjetoMonitoria/recibo_autorizacao.jsp
	 * Este método é chamado na jsp sigaa.war/portais/docente/menu_docente.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String autorizacaoChefe() throws SegurancaException {

		if ( (!getAcessoMenu().isChefeDepartamento()) && (!getAcessoMenu().isChefeUnidade()) )
			throw new SegurancaException("É necessário ser chefe de departamento para acessar essa operação");
		return forward("/monitoria/AutorizacaoProjetoMonitoria/lista.jsf");

	}

}