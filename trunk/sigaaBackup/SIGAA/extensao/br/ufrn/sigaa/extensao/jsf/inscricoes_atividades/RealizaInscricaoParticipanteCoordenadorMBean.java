/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Mbean que gerencia a parte em que o coordenador inscreve um participante em uma ação. </p>
 *
 * <p> <i> Para realizar a inscrição o participante tem que ter um cadastro no sistema, caso não 
 * tenha o coordenador pode cadastrá-lo primeiro </i> </p>
 * 
 * <p><i> O coordenador pode também cadastrar um participante sem ter realizado a inscrição dele, 
 * mas ai é outro caso de uso. Do mesmo jeito o participante precisa ter um cadastro no sistema. </i> </p>
 * 
 * @author jadson
 *
 */
@Component("realizaInscricaoParticipanteCoordenadorMBean")
@Scope("request")
public class RealizaInscricaoParticipanteCoordenadorMBean  extends SigaaAbstractController<InscricaoAtividadeParticipante> implements PesquisarParticipanteExtensao{

	
	/** Página para o coordenador realizar a inscrição de um participante em um curso ou evento de extensão.  */
	public static final String PAGINA_REALIZA_INSCRICAO_CURSO_EVENTO_EXTENSAO_COORDENADOR = "/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp";
	
	/** Página para o coordenador realizar a inscrição de um participante em um mini curso ou mini evento de extensão.  */
	public static final String PAGINA_REALIZA_INSCRICAO_SUB_ATIVIDADE_EXTENSAO_COORDENADOR = "/extensao/GerenciarInscricoes/buscaPadraoParticipantesExtensao.jsp";
	
	
	/** O cadastro do participante selecionado pelo coordenador para criar a inscrição para ele.*/
	private CadastroParticipanteAtividadeExtensao cadastroParticipante;
	
	
	/**
	 * Função não implementada por enquanto.
	 * 
	 * <p>Começa a realização de uma nova inscrição de uma  participante em uma atividade 
	 * de extenção pelo coordenador da ação.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
				"Caso o participante não possua um cadastro na nossa base, será necessário realizar um cadastro para esse participante.  </p>", true, false);
	}
	
	
	
	
	/** 
	 * Função não implementada por enquanto.
	 * 
	 * <p>Começa a realização de uma nova inscrição de uma  participante em uma mini atividade 
	 * de extenção pelo coordenador da ação.</p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
				"Caso o participante não possua um cadastro na nossa base, será necessário realizar um cadastro para esse participante.  </p>", true, false);
	}



	
	
	
	////////////////////////////// Métodos da Interface de Busca //////////////////////////////

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.PesquisarParticipanteExtensao#setParticipanteExtensao(br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao)
	 */
	@Override
	public void setParticipanteExtensao(CadastroParticipanteAtividadeExtensao participante) {
		this.cadastroParticipante = participante;
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.extensao.jsf.inscricoes_atividades.PesquisarParticipanteExtensao#selecionouParticipanteExtensao()
	 */
	@Override
	public String selecionouParticipanteExtensao() throws ArqException {
		return null;
	}
	

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 *  <p>Método não chamado por nenhuma página jsp.</p>
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
