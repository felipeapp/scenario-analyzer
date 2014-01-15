/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * MBean com as funcionalidades básicas para o envio de notificações via email ou mensagens pelo sistema.
 * Os controladores específicos de cada módulo deverão estendê-lo e definir os destinatários.
 * 
 * @author Arlindo Rodrigues
 */
@Component("notificacoes") @Scope("request")
public class NotificacoesMBean extends AbstractControllerNotificacoes {
	
	/** Lista com os destinatários que receberam a mensagem */
	private List<Destinatario> destinatarios = new ArrayList<Destinatario>();
	
	/** Remetente da mensagem */
	private String remetente = null;
	
	/** Caminho do form padrão de envio de mensagens */
	public final String FORM_MENSAGEM = "/geral/mensagem/enviar_mensagem.jsf";
	
	/** Descrição do form padrão de mensagem */
	private String descricao = null;
	
	/** Título do form padrão de mensagem*/
	private String titulo = null;
	
	/** Papéis que possui permissão para enviar mensagem */
	private int[] papeis;
	
	/**
	 * Caso for chamado a partir de uma jsp em struts passar os destinatários
	 * via sessão.
	 * <br>
	 * Método chamado pela seguinte JSP: <ul><li>/geral/mensagem/enviar_mensagem.jsp</li></ul>
	 * @throws ArqException 
	 */
	public String getIniciarFormulario() throws ArqException{
		// Verifica se os destinatários foram passados via sessão
		if (getCurrentSession().getAttribute("destinatarios") != null){
			
			// recupera os destinatários da sessão
			@SuppressWarnings("unchecked")
			List<Destinatario> dest = (List<Destinatario>) getCurrentSession().getAttribute("destinatarios");
					
			setDestinatarios(dest);
			// remove a sessão dos destinatários
			getCurrentSession().removeAttribute("destinatarios");
			
			// verifica se o remetente foi enviar via sessão
			if (getCurrentSession().getAttribute("remetente") != null){
				setRemetente((String) getCurrentSession().getAttribute("remetente") );
				getCurrentSession().removeAttribute("remetente");	
			}		
			// limpa as notificações 
			clear();
			// faz validações e prepara o movimento
			prepararFormulario();
		}
		return null;
	}
	
	/**
	 * Sobrescreve o método {@link AbstractControllerNotificacoes#popularDestinatarios()} da classe AbstractControllerNotificacoes
	 * setando os destinatários passados por na lista. 
	 * 
	 * <br>Não é chamado por nenhuma JSP.
	 */
	@Override
	public void popularDestinatarios() throws ArqException {	
		// Limpa os destinatários já adicionados
		obj.setDestinatariosEmail(new ArrayList<Destinatario>());
		obj.setDestinatariosMensagem(new ArrayList<Destinatario>());
		
		//Preenche os destinatários.
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
