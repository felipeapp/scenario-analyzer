/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/03/2010
 *
 */
package br.ufrn.sigaa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;


/**
 * Managed Bean responsável facilitar o acesso as opções do menu "Requisição/Projetos" do sistema SIPAC.
 * 
 * @author Arlindo Rodrigues
 */
@Component("acessoRequisicoesProjetos") @Scope("session")
public class AcessoRequisicoesProjetosMBean extends	AbstractControllerProdocente<AcessoRequisicoesProjetosMBean>  {

	/**
	 * Chama a lista de propostas de convênio do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarEncaminharProjeto(){
		getCurrentSession().setAttribute("acao", "alteraProjetoMBean.listarPreProjetosIntegracao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/proposta_convenio/lista_propostas_convenio.jsf&redirect=true");
		return redirect("/logonSIPAC");
	}
	
	/**
	 * Chama o cadastro pré-projetos do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarSubmeterPreProjetos(){
		getCurrentSession().setAttribute("acao", "cadastroPreProjetoMBean.iniciarCadastroPreProjetoIntegreacao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/pre_projeto/inicio_pre_projeto.jsf&redirect=true");
		return redirect("/logonSIPAC");				
	}
	
	/**
	 * Exibe a tela de submeter propostas do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarSubmeterPropostas(){
		getCurrentSession().setAttribute("acao", "projetoMBean.inicioIntegracao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/proposta_convenio/tela_explicativa.jsf&redirect=true");
		return redirect("/logonSIPAC");				
	}
	
	/**
	 * Exibe a tela de alterar propostas do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarAlterarPropostas(){
		getCurrentSession().setAttribute("acao", "alteraProjetoMBean.listarPropostasIntegracao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/proposta_convenio/lista_propostas_convenio.jsf&redirect=true");
		return redirect("/logonSIPAC");				
	}	
	
	/**
	 * Exibe a tela de acompanhamento de propostas de convênio do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarAcompanharTramitacaoOnLine(){
		getCurrentSession().setAttribute("acao", "alteraProjetoMBean.listarPropostasUsuarioIntegracao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/proposta_convenio/acompanhamento_propostas_convenios.jsf&redirect=true");
		return redirect("/logonSIPAC");				
	}	
	
	/**
	 * Exibe a tela de membros de plano de trabalho do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarMembrosEquipeTrabalho(){		
		getCurrentSession().setAttribute("acao", "participanteEquipeMBean.entrarConsultarPropostaIntegracao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/proposta_convenio/consulta_geral_jsf/consulta_proposta_convenio.jsf&redirect=true");		
		return redirect("/logonSIPAC");				
	}	
	
	/**
	 * Inicia o cadastro de plano de aplicação do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarCadastroPlanoAplicacao(){
		getCurrentSession().setAttribute("acao", "planilhaOrcamentariaMBean.iniciarIntegracao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/planilha_orcamentaria/consulta_planilha.jsf&redirect=true");
		return redirect("/logonSIPAC");				
	}		
	
	/**
	 * Exibe a tela de membros de plano de trabalho do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarSolicitarAditivo(){
		getCurrentSession().setAttribute("acao", "aditivaConvenioMBean.listarConveniosUnidadeIntegracao");		
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/proposta_convenio/lista_convenios.jsf&redirect=true");
		return redirect("/logonSIPAC");				
	}		
	
	/**
	 * Exibe a tela com a lista de solicitações de aditivo do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarListarSolicitacoesAditivo(){
		getCurrentSession().setAttribute("acao", "aditivaConvenioMBean.listarSolicitacoesAditivosIntegracao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/proposta_convenio/lista_sol_aditivos_convenios.jsf&redirect=true");
		return redirect("/logonSIPAC");				
	}		
	
	/**
	 * Inicia a autorização de tramitação de propostas do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarAutorizarTramitacao(){		
		getCurrentSession().setAttribute("acao", "alteraProjetoMBean.listarPropostasAutorizacaoIntegracao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/proposta_convenio/lista_propostas_convenio_autorizacao.jsf&redirect=true");
		return redirect("/logonSIPAC");				
	}		
	
	/**
	 * Inicia a tela para emitir o parecer de propostas do sistema SIPAC<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarEmitirParecer(){
		getCurrentSession().setAttribute("acao", "alteraProjetoMBean.listarPropostasParecerIntegracao");
		//getCurrentSession().setAttribute("url", "&url=/sipac/convenios/proposta_convenio/lista_propostas_convenio_parecer.jsf&redirect=true");
		return redirect("/logonSIPAC");				
	}			
}
