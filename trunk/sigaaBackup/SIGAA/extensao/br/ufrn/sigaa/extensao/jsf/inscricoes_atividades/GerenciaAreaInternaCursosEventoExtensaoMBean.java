/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean para gerenciar as ações da área interna de cursos e eventos de extensão do usuário na parte pública do SIGAA </p>
 *
 * <p> <i> Gereneciar é realizar o fluxo dos casos de uso que usam a área interna. Controlar acesso, realizar forward página autenticadas, etc... </i> </p>
 * 
 * @author jadson
 *
 */
@Component("gerenciaAreaInternaCursosEventoExtensaoMBean")
@Scope("session")
public class GerenciaAreaInternaCursosEventoExtensaoMBean extends SigaaAbstractController<CadastroParticipanteAtividadeExtensao>{

	/** Página da área interna  na área pública do sistema onde o usuário vai gerenciar suas inscrições em cursos e eventos de extensão. 
	 * 
	 * <strong>Só pode acessar esse página se fizer o login na área pública.</strong>
	 * 
	 * */
	public final static String AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/areaInternaCursosEventosExtensao.jsp";
	
	
	/** Página inicia exibida por padrão */
	public final static String PAGINA_INICIAL_AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO = "/public/extensao/paginaInicialAreaInternaCursosEventosExtensao.jsp";
	
	/** A página interna atual */
	private String paginaAreaInternaParticipante = PAGINA_INICIAL_AREA_INTERNA_CURSOS_EVENTOS_EXTENSAO;
	
	
	/**
	 * Retorna a página interna a ser mostrada.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *  Realiza um forward na área interna do participante de extensão.
	 *  Um forward na área interna é setar a página que será chamada pelo c:import na página areaInternaCursosEventosExtensao.jsp
	 *
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Realiza um forward na área interna padrão da área interna
	 *  Um forward na área interna é setar a página que será chamada pelo c:import na página areaInternaCursosEventosExtensao.jsp
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Verifica se o usuário está na página inicial da área interna.
	 *  
	 *   <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *   <p>Retorna o usuário logado na área interna de cursos e eventos.</p>
	 *   
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @return
	 */
	public CadastroParticipanteAtividadeExtensao getParticipanteLogadoAreaInterna(){
		return (CadastroParticipanteAtividadeExtensao) getCurrentSession().getAttribute("participanteCursosEventosExtensaoLogado");
	}
	
}
