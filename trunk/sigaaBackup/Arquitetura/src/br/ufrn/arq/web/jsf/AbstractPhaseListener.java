/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 07/05/2009
 */
package br.ufrn.arq.web.jsf;

import java.util.ArrayList;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

import br.ufrn.arq.negocio.validacao.ListaMensagens;

/**
 * Classe que possui o comportamento que é comum a todos os PhaseListeners dos sistemas institucionais.
 * 
 * @author Diogo Souto
 * @author Gleydson Lima
 */
@SuppressWarnings("serial")
public class AbstractPhaseListener implements PhaseListener {

	public void beforePhase(PhaseEvent event) {
		FacesContext context = event.getFacesContext();

		Iterator<FacesMessage> erros = context.getMessages();

		Iterator<String> ui = context.getClientIdsWithMessages();

		ArrayList<String> components = new ArrayList<String>();

		while(ui.hasNext()){
			String id = ui.next();
			if(!components.contains(id))
				components.add(id);
		}

		ArrayList<String> listaErros = new ArrayList<String>();

		while (erros.hasNext()) {
			FacesMessage fm = erros.next();

			String summary = fm.getSummary();

			for(String idComponent : components){
				UIComponent c = context.getViewRoot().findComponent(idComponent);
				String title = FacesHelper.getTitle(c);

				summary = gerarMensagemAmigavel(summary, idComponent);

				if(title != null){
					summary = summary.replaceAll(c.getClientId(context), title);
					summary = summary.replaceAll(c.getId(), title);
				}
			}

			summary = summary.replaceAll("_", " ");
			summary = summary.replaceAll("'", "");

			listaErros.add(summary);
		}

		/**
		 * Adiciona os erros de validação do JSF na lista de mensagens de erros
		 */
		for(String erro : listaErros) {
			HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
			ListaMensagens listaMensagens = (ListaMensagens) session.getAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION);

			if (listaMensagens == null)
				listaMensagens = new ListaMensagens();

			listaMensagens.addErro(erro);
			session.setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, listaMensagens);
		}
	}

	/**
	 * Verifica se o erro foi oriundo de uma IllegalArgumentException e muda a mensagem a ser exibida ao usuário.
	 * @param erro
	 * @param idComponent
	 * @return
	 */
	private String gerarMensagemAmigavel(String erro, String idComponent){

		if(erro.contains(IllegalArgumentException.class.getName()))
			return idComponent += ": Campo obrigatório não informado ou valor informado para o campo é inválido.";

		return erro;
	}

	public void afterPhase(PhaseEvent event) {
	}

	// execute the logic before rendering the view
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

}
