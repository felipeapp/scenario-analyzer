/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/01/2007'
 *
 */
package br.ufrn.sigaa.caixa_postal.struts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.sigaa.caixa_postal.dominio.Mensagem;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;

/**
 *
 * Classe utilizada para ler mensagens
 *
 * @author Gleydson Lima
 *
 */
public class VisualizaMensagemAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		MensagemDAO dao = new MensagemDAO();
		ArrayList<br.ufrn.arq.caixa_postal.Mensagem> mensagens = new ArrayList<br.ufrn.arq.caixa_postal.Mensagem>();

		try {
			br.ufrn.arq.caixa_postal.Mensagem msg = (br.ufrn.arq.caixa_postal.Mensagem) dao.findByPrimaryKey(Integer.parseInt(req
					.getParameter("mensagem.id")), br.ufrn.arq.caixa_postal.Mensagem.class);

			Mensagem mensagem = MensagensHelper.decorateMensagem(msg, true);
			
			String folder = "inbox";
			int page = 1;

			if (req.getParameter("folder") != null)
				folder = req.getParameter("folder");
			if (req.getParameter("page") != null)
				page = Integer.parseInt(req.getParameter("page"));

			boolean incluirChamados = false;
			//if (getUsuarioLogado(req).isUserInRole(SigaaPapeis.ADMINISTRADOR_SIGAA)){
			//	incluirChamados = true;
			//}
			
			if ("inbox".equals(folder)) {
				mensagens.addAll(dao.findCaixaEntrada(getUsuarioLogado(req).getId(), page, incluirChamados));
			} else if ("sent".equals(folder)) {
				mensagens.addAll(dao.findCaixaSaida(getUsuarioLogado(req).getId(), page));
			} else {
				mensagens.addAll(dao.findLixeira(getUsuarioLogado(req).getId(), page, incluirChamados));
			}

			int i = mensagens.indexOf(msg);
			//caso a mensagem não seja encontrada redirecionar para o menu principal
			if (i < 0){
				return mapping.findForward("caixa_postal");
			}
			br.ufrn.arq.caixa_postal.Mensagem anterior = (i == 0 ? null : mensagens.get(i-1));
			br.ufrn.arq.caixa_postal.Mensagem proxima = (i == mensagens.size() - 1 ? null : mensagens.get(i+1));

			if (anterior != null)
				req.setAttribute("anterior", anterior.getId());
			if (proxima != null)
				req.setAttribute("proxima", proxima.getId());

			Collection<Mensagem> respostas = mensagem.getRespostas();
			if (respostas != null && respostas.size() > 0) {
				
				req.setAttribute("respostas",respostas);
			}

			if (mensagem.getUsuario().getId() != getUsuarioLogado(req).getId()
					&& mensagem.getRemetente().getId() != getUsuarioLogado(req)
							.getId()
					&& !getUsuarioLogado(req).isUserInRole(
							SigaaPapeis.ADMINISTRADOR_SIGAA)) {
				throw new SegurancaException("Usuário não autorizado");
			}

			if (mensagem.getUsuario().getId() == getUsuarioLogado(req).getId()
					|| (getUsuarioLogado(req).isUserInRole(SigaaPapeis.ADMINISTRADOR_SIGAA) && 
							(mensagem.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIGAA ||
							mensagem.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIPAC ||
							mensagem.getTipo() == br.ufrn.arq.caixa_postal.Mensagem.CHAMADO_SIGRH))) {

				prepareMovimento(ArqListaComando.MENSAGEM_LIDA, req);
				mensagem.setCodMovimento(ArqListaComando.MENSAGEM_LIDA);

				execute(MensagensHelper.msgSigaaToMsgArq(mensagem), req);
				
				if(!mensagem.isLida()){
					
					//Total de mensagens não lidas a partir de 2007, desconsiderando chamados
					Calendar c = Calendar.getInstance();
					c.set(Calendar.DAY_OF_MONTH, 31);
					c.set(Calendar.MONTH, 12);
					c.set(Calendar.YEAR, 2006);
					c.set(Calendar.HOUR_OF_DAY, 23);
					c.set(Calendar.MINUTE, 59);
					c.set(Calendar.SECOND, 59);
					
					Date data = c.getTime();
					int novas = dao.findTotalNaoLidaByUsuarioByData(getUsuarioLogado(req).getId(), data, false);
					req.getSession().setAttribute("qtdNaoLida", novas);
					//Total de mensagens não lidas
					req.getSession().setAttribute("qtdNovas", dao.findTotalNaoLidaByUsuario(getUsuarioLogado(req).getId()));
				
				}

				mensagem.setLida(true);
				msg.setLida(true);
			}

			req.setAttribute("mensagem", mensagem);

			return mapping.findForward("verMensagem");

		} finally {
			dao.close();
		}

	}

}
