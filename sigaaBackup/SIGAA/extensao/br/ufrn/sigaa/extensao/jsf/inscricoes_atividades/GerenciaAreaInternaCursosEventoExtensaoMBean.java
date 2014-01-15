/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 29/10/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;

/**
 *
 * <p>MBean para gerenciar as a��es da �rea interna de cursos e eventos de extens�o do usu�rio na parte p�blica do SIGAA </p>
 *
 * <p> <i> Gereneciar � realizar o fluxo dos casos de uso que usam a �rea interna. Controlar acesso, realizar forward p�gina autenticadas, etc... </i> </p>
 * 
 * @author jadson
 *
 */
@Component("gerenciaAreaInternaCursosEventoExtensaoMBean")
@Scope("session")
public class GerenciaAreaInternaCursosEventoExtensaoMBean extends SigaaAbstractController<CadastroParticipanteAtividadeExtensao>{

	/** P�gina da �rea interna  na �rea p�blica do sistema onde o usu�rio vai gerenciar suas inscri��es em cursos e eventos de extens�o. 
	 * 
	 * <strong>S� pode acessar esse p�gina se fizer o login na �rea p�blica.</strong>
	 * 
	 * */
	public final static String AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/areaInternaCursosEventosExtensao.jsp";
	
	
	/** P�gina inicia exibida por padr�o */
	public final static String PAGINA_INICIAL_AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/paginaInicialAreaInternaCursosEventosExtensao.jsp";
	
	/** A p�gina interna atual */
	private String paginaAreaInternaParticipante = PAGINA_INICIAL_AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO;
	
	
	/**
	 * Retorna a p�gina interna a ser mostrada.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/areaInternaCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String getPaginaAreaInternaParticipante(){
		if(StringUtils.isEmpty(this.paginaAreaInternaParticipante) ){
			this.paginaAreaInternaParticipante = PAGINA_INICIAL_AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO;
		}
		
		return this.paginaAreaInternaParticipante;
	}
	
	public void setPaginaAreaInternaParticipante(String paginaAreaInternaParticipante){
		this.paginaAreaInternaParticipante = paginaAreaInternaParticipante;
	}
	
	
	/**
	 *  Realiza um forward na �rea interna do participante de extens�o.
	 *  Um forward na �rea interna � setar a p�gina que ser� chamada pelo c:import na p�gina areaInternaCursosEventosExtensao.jsp
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/areaInternaCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @param paginaAreaInternaParticipante
	 * @return
	 */
	public String forwardPaginaInterna(String paginaAreaInternaParticipante){
		this.paginaAreaInternaParticipante = paginaAreaInternaParticipante;
		return forward(AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO);
	}
	
	/** Realiza um forward na �rea interna padr�o da �rea interna
	 *  Um forward na �rea interna � setar a p�gina que ser� chamada pelo c:import na p�gina areaInternaCursosEventosExtensao.jsp
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/areaInternaCursosEventosExtensao.jsp</li>
	 *   </ul>
	 */
	public String forwardPaginaInternaPadrao(){
		this.paginaAreaInternaParticipante = PAGINA_INICIAL_AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO;
		return forward(AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO);
	}
	
	/**
	 * 
	 * Verifica se o usu�rio est� na p�gina inicial da �rea interna.
	 *  
	 *   <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/extensao/areaInternaCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public boolean isPaginaInicialAreaInterna(){
		return PAGINA_INICIAL_AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO.equalsIgnoreCase(paginaAreaInternaParticipante);
	}
	
	/**
	 *   <p>Retorna o usu�rio logado na �rea interna de cursos e eventos.</p>
	 *   
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public CadastroParticipanteAtividadeExtensao getParticipanteLogadoAreaInterna(){
		return (CadastroParticipanteAtividadeExtensao) getCurrentSession().getAttribute("participanteCursosEventosExtensaoLogado");
	}
	
}
