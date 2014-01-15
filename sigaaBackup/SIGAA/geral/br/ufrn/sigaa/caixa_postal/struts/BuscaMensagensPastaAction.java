/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 * 
 * Created on 24/07/2006
 *
 */
package br.ufrn.sigaa.caixa_postal.struts;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.struts.SigaaAbstractAction;
import br.ufrn.sigaa.caixa_postal.dominio.Mensagem;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;

/**
 * Action para busca de mensagens em uma pasta
 *
 * @author David Ricardo
 *
 */
public class BuscaMensagensPastaAction extends SigaaAbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		MensagemDAO msgDAO = new MensagemDAO();
		MensagemForm menForm = (MensagemForm) form;
		Collection<br.ufrn.arq.caixa_postal.Mensagem> msgs = null;
		
		try {
			if (menForm.getAcao() == ConstantesMensagem.REMOVER_LOTE) {
				Mensagem msg = menForm.getMensagem();
				msg.setCodMovimento(ArqListaComando.MENSAGEM_EXCLUIR_LOTE);
				execute(MensagensHelper.msgSigaaToMsgArq(msg), req);
			}
			
			String folder = "inbox";
			int page = 1;
			
			if (req.getParameter("folder") != null)
				folder = req.getParameter("folder");
			if (req.getParameter("page") != null)	
				page = Integer.parseInt(req.getParameter("page"));
				
			boolean incluirChamado = false;
			if (getUsuarioLogado(req).isUserInRole(SigaaPapeis.ADMINISTRADOR_SIGAA)){
//				incluirChamado = true;
			}
			
			//Busca mensagens
			if ("inbox".equals(folder)) {
				msgs = msgDAO.findCaixaEntrada(getUsuarioLogado(req).getId(), page, incluirChamado);
				
			} else if ("sent".equals(folder)) {
				msgs = msgDAO.findCaixaSaida(getUsuarioLogado(req).getId(), page);
			} else {
				msgs = msgDAO.findLixeira(getUsuarioLogado(req).getId(), page, incluirChamado);
			}
			
			Collection<Mensagem> mensagens = MensagensHelper.decorateMensagem(msgs, false);
			
			// Cálculos para paginação
			int total = msgDAO.findTotalMensagensPasta(folder, getUsuarioLogado(req).getId(), incluirChamado);
			int ultimaMsg = ((page - 1) * (incluirChamado ? Mensagem.TAM_PAGINA_ADM : Mensagem.TAM_PAGINA)) + (incluirChamado ? Mensagem.TAM_PAGINA_ADM : Mensagem.TAM_PAGINA);
			
			if (ultimaMsg > total) ultimaMsg = total;
			
			req.setAttribute("primeiraMsgPagina", (page - 1) * (incluirChamado ? Mensagem.TAM_PAGINA_ADM : Mensagem.TAM_PAGINA) + 1);
			req.setAttribute("ultimaMsgPagina", ultimaMsg);
			req.setAttribute("totalMsgPasta", total);
			req.setAttribute("mensagens", mensagens);
			req.setAttribute("totalPaginas", (int) Math.ceil(((double) total) / (incluirChamado ? Mensagem.TAM_PAGINA_ADM : Mensagem.TAM_PAGINA)));
			
			
			prepareMovimento(ArqListaComando.MENSAGEM_EXCLUIR_LOTE.getId(), req);
			
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, 31);
			c.set(Calendar.MONTH, 12);
			c.set(Calendar.YEAR, 2006);
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			
			Date data = c.getTime();
			//Quantidade de mensagens não lidas a partir de 2007. Não deixa entrar no sistema se tiver mensagem a ser lida
			//Deixa passar mensagens que são geradas automaticamente pelo sistema
			int novas = msgDAO.findTotalNaoLidaByUsuarioByData(getUsuarioLogado(req).getId(), data, false);
			req.getSession().setAttribute("qtdNaoLida", novas);
			
			//Quantidade de mensagens ainda não lidas
			req.getSession().setAttribute("qtdNovas", msgDAO.findNaoLidasByUsuario(getUsuarioLogado(req).getId()));

			
		} finally {
			msgDAO.close();
		}
		
		return mapping.findForward("caixa_postal");
	}

}
