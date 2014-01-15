/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.caixa_postal.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.web.struts.AbstractForm;
import br.ufrn.arq.web.struts.ConstantesActionGeral;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.caixa_postal.dominio.Mensagem;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * FormBean para mensagens no sistema
 *
 * @author Gleydson Lima
 *
 */
public class MensagemForm extends AbstractForm {

	private Mensagem mensagem = new Mensagem();

	/** Usado para parametrizar a JSP de envio de mensagem */
	private String view;

	private String destinatarios;

	public MensagemForm() {
		Mensagem resposta = new Mensagem();
		mensagem.setReplyFrom(resposta);
		mensagem.setUsuario(new Usuario());
	}

	public Mensagem getMensagem() {
		return mensagem;
	}

	public void setMensagem(Mensagem mensagem) {
		this.mensagem = mensagem;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest req) {
		ActionErrors erros = new ActionErrors();

		if (getAcao() == ConstantesActionGeral.INSERIR) {

			if (mensagem.getTitulo() != null
					&& mensagem.getTitulo().trim().length() == 0) {
				erros.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"caixa_postal.titulo"));
			}

			if (mensagem.getMensagem() == null
					|| mensagem.getMensagem().trim().length() == 0)
				erros.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
						"caixa_posta.mensagem"));

			UsuarioDao dao = null;
			try {
				dao = (UsuarioDao) DAOFactory.getInstance().getDAO(UsuarioDao.class,
						req);
				req.setAttribute("destinatario", dao.findByPrimaryKey(mensagem
						.getUsuario().getId(), Usuario.class));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} else if (getAcao() == ConstantesActionGeral.REMOVER_LOTE) {
			if (mensagem.getIdMensagem() == null
					|| mensagem.getIdMensagem().length == 0)
				addErro(erros, "caixa_postal.remover.selecionadas");

		}

		return erros;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	/**
	 * @return Returns the destinatarios.
	 */
	public String getDestinatarios() {
		return destinatarios;
	}

	/**
	 * @param destinatarios The destinatarios to set.
	 */
	public void setDestinatarios(String destinatarios) {
		this.destinatarios = destinatarios;
	}



}
