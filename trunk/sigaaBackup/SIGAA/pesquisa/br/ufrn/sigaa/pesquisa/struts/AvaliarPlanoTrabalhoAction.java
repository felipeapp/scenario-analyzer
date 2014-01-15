/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho;
import br.ufrn.sigaa.pesquisa.form.PlanoTrabalhoForm;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;

/**
 * Action para cadastrar a avalia��o de um plano de trabalho por um consultor
 *
 * @author ricardo
 */

public class AvaliarPlanoTrabalhoAction extends AbstractCrudAction {

	/** Constante de visualiza��o da avalia��o. */
	private static final String FORM_AVALIACAO = "avaliacao";
	/** Conste de visualiza��o de retorno a avalia��o */
	private static final String VOLTA_AVALIACAO_PROJETO = "voltaProjeto";

	/**
	 * M�todo respons�vel pela lista listagem dos Planos de Trabalho 
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		// Identificar consultor
		int idConsultor = 0;

		if (req.getParameter("idConsultor") != null) {
			idConsultor = Integer.parseInt(req.getParameter("idConsultor"));
		} else if (idConsultor == 0) {
			idConsultor = (Integer) req.getAttribute("idConsultor");
		}

		req.setAttribute("lista", getGenericDAO(req).findByExactField(PlanoTrabalho.class, "consultor.id", idConsultor));

		return mapping.findForward(LISTAR);
	}

	/**
	 * M�todo respons�vel pela edi��o, altera��o de um Plano de Trabalho.
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;

		planoTrabalhoForm.setObj(getGenericDAO(req).findByPrimaryKey(planoTrabalhoForm.getObj().getId(), PlanoTrabalho.class));
		PlanoTrabalho plano = planoTrabalhoForm.getObj();
		
		ProjetoPesquisa projeto = getGenericDAO(req).refresh(plano.getProjetoPesquisa());
		ProjetoPesquisa projetoOriginal = null;
		if( projeto.getProjetoOriginal() != null ){
			projetoOriginal = getGenericDAO(req).refresh(projeto.getProjetoOriginal());
			Iterator<PlanoTrabalho> it = projetoOriginal.getPlanosTrabalho().iterator();
			while(it.hasNext()){
				it.next().getRelatorioBolsaParcial();
			}
		}

		// Inicializar tela do cronograma
		if (plano.getCota() != null) {
			TelaCronograma cronograma = new TelaCronograma(
					plano.getCota().getInicio(),
					plano.getCota().getFim(),
					plano.getCronogramas()
				);
			planoTrabalhoForm.setTelaCronograma(cronograma);
		}
		
		prepareMovimento(SigaaListaComando.AVALIAR_PLANO_TRABALHO_PESQUISA, req);
		
		req.setAttribute(mapping.getName(), planoTrabalhoForm);
		return mapping.findForward(FORM_AVALIACAO);
	}

	/** Aprovar relat�rio de projeto de pesquisa */
	public ActionForward aprovar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;

		if (planoTrabalhoForm.getObj().getParecerConsultor().equals("")) {
			addMensagemErro("Parecer: Campo Obrigat�rio n�o informado.", req);
			return edit(mapping, planoTrabalhoForm, req, res);
		}

		// Definir como aprovado
		planoTrabalhoForm.getObj().setStatus(TipoStatusPlanoTrabalho.APROVADO);
		return persistir(mapping, form, req, res);
	}
	
	/** Aprovar relat�rio de projeto de pesquisa mas com restri��es */
	public ActionForward aprovarComRestricao(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;

		if (planoTrabalhoForm.getObj().getParecerConsultor().equals("")) {
			addMensagemErro("Parecer: Campo Obrigat�rio n�o informado.", req);
			return edit(mapping, planoTrabalhoForm, req, res);
		}

		// Definir como aprovado com restri��es
		planoTrabalhoForm.getObj().setStatus(TipoStatusPlanoTrabalho.APROVADO_COM_RESTRICOES);
		return persistir(mapping, form, req, res);
	}

	/** Reprovar relat�rio de projeto de pesquisa */
	public ActionForward reprovar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;

		if (planoTrabalhoForm.getObj().getParecerConsultor().equals("")) {
			addMensagemErro("Parecer: Campo Obrigat�rio n�o informado.", req);
			return edit(mapping, planoTrabalhoForm, req, res);
		}
		
		// Definir como n�o aprovado
		planoTrabalhoForm.getObj().setStatus(TipoStatusPlanoTrabalho.NAO_APROVADO);
		return persistir(mapping, form, req, res);
	}

	/** Persistir avalia��o */
	private ActionForward persistir(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		PlanoTrabalhoForm planoTrabalhoForm = (PlanoTrabalhoForm) form;
		PlanoTrabalho planoTrabalho = planoTrabalhoForm.getObj();

		// TODO Validar dados
		planoTrabalho.setCodMovimento(SigaaListaComando.AVALIAR_PLANO_TRABALHO_PESQUISA);
		planoTrabalho = (PlanoTrabalho) execute(planoTrabalho, req);

		addInformation("Avalia��o do plano de trabalho realizada com sucesso!", req);

		if (getSubSistemaCorrente(req).equals(SigaaSubsistemas.PORTAL_CONSULTOR)) {
			if(getParameterInt(req, "fromAvaliacaoProjeto", 0) > 0)
				return mapping.findForward(VOLTA_AVALIACAO_PROJETO);
			return mapping.findForward(SigaaSubsistemas.PORTAL_CONSULTOR.getForward());
		} else {
			req.setAttribute("idConsultor", planoTrabalho.getConsultor().getId());
			return list(mapping, form, req, res);
		}
	}

	/**
	 * M�todo respons�vel pelo cancelamento de um Plano de Trabalho
	 */
	@Override
	public ActionForward cancelar(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if(getParameterInt(request, "fromAvaliacaoProjeto", 0) > 0)
			return mapping.findForward(VOLTA_AVALIACAO_PROJETO);
		return super.cancelar(mapping, form, request, response);
	}
}
