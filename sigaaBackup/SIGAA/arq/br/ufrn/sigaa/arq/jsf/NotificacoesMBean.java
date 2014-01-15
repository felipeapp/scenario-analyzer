/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 17/11/2009
 *
 */
package br.ufrn.sigaa.arq.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.comum.dominio.notificacoes.Destinatario;

/**
 * MBean com as funcionalidades b�sicas para o envio de notifica��es via email ou mensagens pelo sistema.
 * Os controladores espec�ficos de cada m�dulo dever�o estend�-lo e definir os destinat�rios.
 * 
 * @author Arlindo Rodrigues
 */
@Component("notificacoes") @Scope("request")
public class NotificacoesMBean extends AbstractControllerNotificacoes {
	
	/** Lista com os destinat�rios que receberam a mensagem */
	private List<Destinatario> destinatarios = new ArrayList<Destinatario>();
	
	/** Remetente da mensagem */
	private String remetente = null;
	
	/** Caminho do form padr�o de envio de mensagens */
	public final String FORM_MENSAGEM = "/geral/mensagem/enviar_mensagem.jsf";
	
	/** Descri��o do form padr�o de mensagem */
	private String descricao = null;
	
	/** T�tulo do form padr�o de mensagem*/
	private String titulo = null;
	
	/** Pap�is que possui permiss�o para enviar mensagem */
	private int[] papeis;
	
	/**
	 * Caso for chamado a partir de uma jsp em struts passar os destinat�rios
	 * via sess�o.
	 * <br>
	 * M�todo chamado pela seguinte JSP: <ul><li>/geral/mensagem/enviar_mensagem.jsp</li></ul>
	 * @throws ArqException 
	 */
	public String getIniciarFormulario() throws ArqException{
		// Verifica se os destinat�rios foram passados via sess�o
		if (getCurrentSession().getAttribute("destinatarios") != null){
			
			// recupera os destinat�rios da sess�o
			@SuppressWarnings("unchecked")
			List<Destinatario> dest = (List<Destinatario>) getCurrentSession().getAttribute("destinatarios");
					
			setDestinatarios(dest);
			// remove a sess�o dos destinat�rios
			getCurrentSession().removeAttribute("destinatarios");
			
			// verifica se o remetente foi enviar via sess�o
			if (getCurrentSession().getAttribute("remetente") != null){
				setRemetente((String) getCurrentSession().getAttribute("remetente") );
				getCurrentSession().removeAttribute("remetente");	
			}		
			// limpa as notifica��es 
			clear();
			// faz valida��es e prepara o movimento
			prepararFormulario();
		}
		return null;
	}
	
	/**
	 * Sobrescreve o m�todo {@link AbstractControllerNotificacoes#popularDestinatarios()} da classe AbstractControllerNotificacoes
	 * setando os destinat�rios passados por na lista. 
	 * 
	 * <br>N�o � chamado por nenhuma JSP.
	 */
	@Override
	public void popularDestinatarios() throws ArqException {	
		// Limpa os destinat�rios j� adicionados
		obj.setDestinatariosEmail(new ArrayList<Destinatario>());
		obj.setDestinatariosMensagem(new ArrayList<Destinatario>());
		
		//Preenche os destinat�rios.
		obj.adicionarDestinatarios(destinatarios);
		
		//Preenche o Remetente
		obj.setNomeRemetente(getRemetente());
	}

	@Override
	public String getCaminhoFormulario() {
		return FORM_MENSAGEM;
	}

	@Override
	public int[] getPapeis() {
		return papeis;
	}

	public List<Destinatario> getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(List<Destinatario> destinatarios) {
		this.destinatarios = destinatarios;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setPapeis(int[] papeis) {
		this.papeis = papeis;
	}

	public String getRemetente() {
		return remetente;
	}

	public void setRemetente(String remetente) {
		this.remetente = remetente;
	}

	
}
