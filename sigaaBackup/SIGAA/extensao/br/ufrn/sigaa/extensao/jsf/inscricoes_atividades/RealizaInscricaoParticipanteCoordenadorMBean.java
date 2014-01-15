/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 29/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.jsf.inscricoes_atividades;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;

/**
 *
 * <p> Mbean que gerencia a parte em que o coordenador inscreve um participante em uma a��o. </p>
 *
 * <p> <i> Para realizar a inscri��o o participante tem que ter um cadastro no sistema, caso n�o 
 * tenha o coordenador pode cadastr�-lo primeiro </i> </p>
 * 
 * <p><i> O coordenador pode tamb�m cadastrar um participante sem ter realizado a inscri��o dele, 
 * mas ai � outro caso de uso. Do mesmo jeito o participante precisa ter um cadastro no sistema. </i> </p>
 * 
 * @author jadson
 *
 */
@Component("realizaInscricaoParticipanteCoordenadorMBean")
@Scope("request")
public class RealizaInscricaoParticipanteCoordenadorMBean  extends SigaaAbstractController<InscricaoAtividadeParticipante> implements PesquisarParticipanteExtensao{

	
	/** P�gina para o coordenador realizar a inscri��o de um participante em um curso ou evento de extens�o.  */
	public static final String PAGINA_REALIZA_INSCRICAO_CURSO_EVENTO_EXTENSAO_COORDENADOR = "/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp";
	
	/** P�gina para o coordenador realizar a inscri��o de um participante em um mini curso ou mini evento de extens�o.  */
	public static final String PAGINA_REALIZA_INSCRICAO_SUB_ATIVIDADE_EXTENSAO_COORDENADOR = "/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp";
	
	
	/** O cadastro do participante selecionado pelo coordenador para criar a inscri��o para ele.*/
	private CadastroParticipanteAtividadeExtensao cadastroParticipante;
	
	
	/**
	 * Fun��o n�o implementada por enquanto.
	 * 
	 * <p>Come�a a realiza��o de uma nova inscri��o de uma  participante em uma atividade 
	 * de exten��o pelo coordenador da a��o.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaInscritosCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String inscreverParticipanteAtividade(){
		
		BuscaPadraoParticipanteExtensaoMBean mBean = getMBean("buscaPadraoParticipanteExtensaoMBean");
		return mBean.iniciaBuscaSelecaoParticipanteExtensao(this, 
				"Inscrever Novo Participante", 
				"<p>Caro coordenador, </p> " +
				"<p>Selecione um participante para ser inscrito na atividade. " +
				"Caso o participante n�o possua um cadastro na nossa base, ser� necess�rio realizar um cadastro para esse participante.  </p>", true, false);
	}
	
	
	
	
	/** 
	 * Fun��o n�o implementada por enquanto.
	 * 
	 * <p>Come�a a realiza��o de uma nova inscri��o de uma  participante em uma mini atividade 
	 * de exten��o pelo coordenador da a��o.</p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/GerenciarInscricoes/listaInscritosCursosEventosExtensao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String inscreverParticipanteSubAtividade(){
		
		
		BuscaPadraoParticipanteExtensaoMBean mBean = getMBean("buscaPadraoParticipanteExtensaoMBean");
		return mBean.iniciaBuscaSelecaoParticipanteExtensao(this, "Inscrever Novo Participante", 
				"<p>Caro coordenador, </p> " +
				"<p>Selecione um participante para ser inscrito na mini atividade. " +
				"Caso o participante n�o possua um cadastro na nossa base, ser� necess�rio realizar um cadastro para esse participante.  </p>", true, false);
	}



	
	
	
	////////////////////////////// M�todos da Interface de Busca //////////////////////////////

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.PesquisarParticipanteExtensao#setParticipanteExtensao(br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao)
	 */
	@Override
	public void setParticipanteExtensao(CadastroParticipanteAtividadeExtensao participante) {
		this.cadastroParticipante = participante;
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.PesquisarParticipanteExtensao#selecionouParticipanteExtensao()
	 */
	@Override
	public String selecionouParticipanteExtensao() throws ArqException {
		return null;
	}
	

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *  
	 * @see br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.PesquisarParticipanteExtensao#cancelarPesquiasParticipanteExtensao()
	 */
	@Override
	public String cancelarPesquiasParticipanteExtensao() {
		return super.cancelar();
	}
	
	////////////////////////////////////////////////////////////

	

	public CadastroParticipanteAtividadeExtensao getCadastroParticipante() {
		return cadastroParticipante;
	}

	public void setCadastroParticipante(CadastroParticipanteAtividadeExtensao cadastroParticipante) {
		this.cadastroParticipante = cadastroParticipante;
	}




	
}
