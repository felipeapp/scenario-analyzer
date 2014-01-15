/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.caixa_postal.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.web.struts.ConstantesActionGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.caixa_postal.dominio.Mensagem;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;
import br.ufrn.sigaa.dominio.Usuario;

/**
 *
 * Classe utilizada para ler mensagens
 *
 * @author Gleydson Lima
 *
 */
public class EnviaMensagemAction extends SigaaAbstractAction {

	/** Persiste a mensagem a ser enviada.
	 * @see org.apache.struts.actions.DispatchAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		MensagemForm mensagemForm = (MensagemForm) form;
		Mensagem mensagem = mensagemForm.getMensagem();
		UsuarioDao dao = getDAO(UsuarioDao.class, req);
		MensagemDAO mensagemDAO = new MensagemDAO();
		try {

			switch (getAcao(req)) {
			case ConstantesActionGeral.PRE_INSERIR: {
				UsuarioGeral user = null;
				Mensagem msg = null;
				br.ufrn.arq.caixa_postal.Mensagem msgArq = null;
				Mensagem replyFrom = mensagemForm.getMensagem().getReplyFrom();

				// Se for diferente de chamado, busca o usuário destino
				if (mensagem.getTipo() != br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIPAC&
					mensagem.getTipo() != br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIPAC) {
					int idMensagem = -1;
					int idUsuario = -1;

					if (!isEmpty(req.getParameter("idUsuario")))
						idUsuario = Integer.parseInt(req.getParameter("idUsuario"));

					if (req.getParameter("idMensagemEncaminhar") != null)
						idMensagem = Integer.parseInt(req
								.getParameter("idMensagemEncaminhar"));

					if (idUsuario != -1)
						user = dao.findByPrimaryKey(idUsuario,
								Usuario.class);

					if (idMensagem != -1) {
						msgArq = mensagemDAO
								.findByPrimaryKey(idMensagem,
										br.ufrn.arq.caixa_postal.Mensagem.class);
						msg = MensagensHelper.decorateMensagem(msgArq, false);
					}
					if (replyFrom.getId() > 0) {

						msgArq = mensagemDAO
								.findByPrimaryKey(replyFrom.getId(),
										br.ufrn.arq.caixa_postal.Mensagem.class);
						if (msgArq != null) {
							replyFrom = MensagensHelper
									.decorateMensagem(msgArq, false);
						}

						if (replyFrom != null) {
							StringBuilder msgOriginal = new StringBuilder();
							msgOriginal
									.append("\n\n\n------- Mensagem Original -------\n");
							msgOriginal.append(replyFrom.getMensagem());

							req
									.setAttribute("original", msgOriginal
											.toString());

							req.setAttribute("tituloResposta", "RES: "
									+ replyFrom.getTitulo());
						}
					}
				}
				//populando tipos de chamados.
				List<Object[]> tiposChamados = mensagemDAO.findAllTiposChamados();
				req.setAttribute("tiposChamados", tiposChamados);

				if (user != null)
					req.setAttribute("user", user);

				if (msg != null)
					req.setAttribute("encaminhar", msg);

				prepareMovimento(ArqListaComando.MENSAGEM_ENVIAR, req);

				if (mensagemForm.getView() == null) {

					if (req.getParameter("outroSistema") == null) {
						// Se vier de outro sistema, tirar o cabeçalho pois será
						// incluído por AJAX
						return mapping.findForward("envia_mensagem");
					} else {
						return mapping.findForward("envia_mensagem_body");
					}
				} else {
					return mapping.findForward(mensagemForm.getView());
				}

			}
			case ConstantesActionGeral.INSERIR: {
				MensagemForm msgForm = (MensagemForm) form;
				ArrayList<UsuarioGeral> destinatarios = new ArrayList<UsuarioGeral>();

				ActionErrors erros = validarEnvio(msgForm.getDestinatarios(),
						destinatarios, msgForm.getMensagem(), dao, req);
				if (!erros.isEmpty()) {
					saveMessages(req, erros);
					res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return null;
				}

				prepareMovimento(ArqListaComando.MENSAGEM_ENVIAR, req);

				Mensagem msg = msgForm.getMensagem();

				// Destinatário de chamada é sempre usuário sipac
				if (msg.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIGAA ||
						msg.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIPAC) {
					UsuarioGeral usuario = new UsuarioGeral(Integer.parseInt(RepositorioDadosInstitucionais.get("idUsuarioChamado")));
					destinatarios.add(usuario);
					msg.setStatusChamadoDesc(-1); //Chamado Pendente
				}

				msg.setDestinatarios(destinatarios);

				try {
					
					msg.setTitulo(URLDecoder.decode(msg.getTitulo().replaceAll("\\+", "&#43;"), "ISO_8859-1"));
					msg.setMensagem(URLDecoder.decode(msg.getMensagem().replaceAll("\\+", "&#43;"), "ISO_8859-1"));
				} catch (IllegalArgumentException e) {
					msg.setTitulo(new String(msg.getTitulo().getBytes(), "ISO_8859-1"));
					msg.setMensagem(new String(msg.getMensagem().getBytes(), "ISO_8859-1"));
				}

				msg.setCodMovimento(ArqListaComando.MENSAGEM_ENVIAR);
				execute(MensagensHelper.msgSigaaToMsgArq(msg), req);
				
				//enviando email para caixa postal
				if(msg.getTipo() != Mensagem.CHAMADO_SIPAC && msg.getTipo() != Mensagem.CHAMADO_SIGAA && 
					msg.getTipo() != Mensagem.CHAMADO_SIGRH && msg.getTipo() != Mensagem.CHAMADO_SIGAA) { 	
				
					for (UsuarioGeral user : destinatarios){
						String assunto = "[SIGAAMail] " + msg.getTitulo();
	
						String corpoMsg = "Caro(a) "
								+ user.getNome() + ","
								+ "<br/><br/>"
								+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;O usuário " + getUsuarioLogado(req).getPessoa().getNome().trim()
								+ " enviou uma mensagem para sua caixa postal dos Sistemas da " + RepositorioDadosInstitucionais.getSiglaInstituicao() + ".<br/><br/>"
								+ "<b>Título:</b> " + msg.getTitulo() + "<br/>" 
								+ "<b>Mensagem:</b> " + msg.getMensagem() + "<br/>";
	
						Mail.sendMessageWithReply(user.getNome(), user.getEmail(),
								assunto, corpoMsg, getUsuarioLogado(req).getEmail());
					}
				}

				res.setContentType("text");
				res.getWriter().print("{sucesso: true, erros: []}");
				res.flushBuffer();
				return null;
			}

			case ConstantesActionGeral.REMOVER: {

				MensagemForm msgForm = (MensagemForm) form;

				prepareMovimento(ArqListaComando.MENSAGEM_EXCLUIR, req);

				Mensagem msg = msgForm.getMensagem();
				msg.setCodMovimento(ArqListaComando.MENSAGEM_EXCLUIR);
				execute(MensagensHelper.msgSigaaToMsgArq(msg), req);

				return mapping.findForward("entrada");

			}

			case ConstantesActionGeral.REMOVER_LOTE: {
				MensagemForm msgForm = (MensagemForm) form;

				prepareMovimento(ArqListaComando.MENSAGEM_EXCLUIR_LOTE, req);

				Mensagem msg = msgForm.getMensagem();
				msg.setCodMovimento(ArqListaComando.MENSAGEM_EXCLUIR_LOTE);
				execute(MensagensHelper.msgSigaaToMsgArq(msg), req);

				res.sendRedirect(req.getContextPath()
						+ mapping.findForward("caixa_postal").getPath()
						+ "?mensagem=Mensagens removidas com sucesso!");

				return null;
			}

			default:
				// Caso senha administrador e não tenha escolhido nenhuma ação
//				if (getUsuarioLogado(req).isUserInRole(
//						SipacPapeis.ADMINISTRADOR_SIPAC)
//						|| getUsuarioLogado(req).isUserInRole(
//								SipacPapeis.ENVIADOR_MENSAGEM)) {
//					prepareMovimento(ArqListaComando.MENSAGEM_ENVIAR, req);
//					Collection<Papel> papeis = new ArrayList<Papel>();
//					Papel papel = new Papel(-1);
//					papel.setDescricao("TODOS OS USUÁRIOS");
//					papeis.add(papel);
//					papeis.addAll(dao.findAll(Papel.class, "descricao", "asc"));
//					req.setAttribute("papeis", papeis);
//					req.setAttribute("msgAdministrador", Boolean.TRUE);
//					return mapping.findForward("envia_mensagem");
//				}

			}

		} finally {
			mensagemDAO.close();
		}
		return mapping.findForward("caixa_postal");
	}

	/** Valida os dados para o envio da mensagem.
	 * @param lista
	 * @param destinatarios
	 * @param msg
	 * @param dao
	 * @param req
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	private ActionErrors validarEnvio(String lista,
			ArrayList<UsuarioGeral> destinatarios, Mensagem msg,
			UsuarioDao dao, HttpServletRequest req) throws SegurancaException,
			ArqException {
		ArrayList<String> destinatariosInvalidos = new ArrayList<String>();
		ActionErrors erros = new ActionErrors();
		boolean admin = false;

		if (lista != null && lista.trim().length() > 0) {
			for (String login : lista.split(",")) {
				if (!login.trim().equals("")) {
					Usuario usr = (Usuario) dao.findByLogin(login.trim());
					if (usr == null || (usr.isInativo()&& !getUsuarioLogado(req).isUserInRole(SigaaPapeis.ADMINISTRADOR_SIGAA))) {
						destinatariosInvalidos.add(login);
					} else {
						destinatarios.add(usr);
					}
				}
			}
		}

		if (msg.getUsuario().getId() == 0
				&& (lista == null || lista.trim().equals(""))
				&& !msg.isChamado()) {
			checkRole(new int[] { SigaaPapeis.ADMINISTRADOR_SIGAA }, req);
			msg.setUsuario(null);
			admin = true;
		}

		if (!destinatariosInvalidos.isEmpty() && !msg.isChamado()) {
			StringBuffer logins = new StringBuffer();
			for (Iterator<String> it = destinatariosInvalidos.iterator(); it
					.hasNext();) {
				String login = it.next();
				logins.append(login);
				if (it.hasNext())
					logins.append(", ");
			}
			erros.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"caixa_postal.destinatarioInvalido", logins.toString()));
		}

		if (!admin && destinatarios.isEmpty() && !msg.isChamado()) {
			erros.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(
					"caixa_postal.semDestinatario"));
		}

		return erros;

	}

}
