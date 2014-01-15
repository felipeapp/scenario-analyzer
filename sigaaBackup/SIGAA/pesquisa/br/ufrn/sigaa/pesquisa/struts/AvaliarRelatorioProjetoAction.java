/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioProjeto;
import br.ufrn.sigaa.pesquisa.form.MembroProjetoServidorForm;
import br.ufrn.sigaa.pesquisa.form.RelatorioProjetoForm;

/**
 * Action respons�vel pela avalia��o dos relat�rios finais de projetos de pesquisa
 * @author ricardo
 *
 */
public class AvaliarRelatorioProjetoAction extends AbstractCrudAction {

	private static final String FORM_AVALIACAO = "avaliacao";
	private static final String FORM_ALTERACAO = "alterar";

	/**
	 * M�todo respons�vel pela listagem dos relat�rios dos projetos
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		// Identificar consultor
		int idConsultor = 0;

		if (req.getParameter("idConsultor") != null) {
			idConsultor = Integer.parseInt(req.getParameter("idConsultor"));
		} else if (idConsultor == 0) {
			idConsultor = (Integer) req.getAttribute("idConsultor");
		}

		req.setAttribute("lista", getGenericDAO(req).findByExactField(RelatorioProjeto.class, "consultor.id", idConsultor));

		return mapping.findForward(LISTAR);
	}

	/**
	 * M�todo respons�vel pela edi��o ou altera��o dos relat�rios dos projetos
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;
		RelatorioProjeto relatorio = relatorioForm.getObj();

		relatorioForm.setObj(getGenericDAO(req).findByPrimaryKey(relatorio.getId(), RelatorioProjeto.class));

		prepareMovimento(SigaaListaComando.AVALIAR_RELATORIO_PROJETO_PESQUISA, req);

		if ( !isEmpty( relatorioForm.getObj().getParecerConsultor() ) ) {
			relatorioForm.setParecerAnterior( relatorioForm.getObj().getParecerConsultor() );
			relatorioForm.getObj().setParecerConsultor("");
		}
		
		req.setAttribute(mapping.getName(), relatorioForm);
		return mapping.findForward(FORM_AVALIACAO);
	}

	/** Carrega o Relat�rio de Projeto submetido para a edi��o. */
	public ActionForward carregarRelatorio(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;
		RelatorioProjeto relatorio = relatorioForm.getObj();

		relatorioForm.setObj(getGenericDAO(req).findByPrimaryKey(relatorio.getId(), RelatorioProjeto.class));

		prepareMovimento(SigaaListaComando.ENVIAR_RELATORIO_FINAL_PROJETO, req);

		req.setAttribute(mapping.getName(), relatorioForm);
		return mapping.findForward(FORM_ALTERACAO);
	}
	
	/** Aprovar relat�rio de projeto de pesquisa */
	public ActionForward aprovar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;

		if ( isEmpty( relatorioForm.getObj().getParecerConsultor() ) ) {
			addMensagemErro("Parecer: Campo Obrigat�rio n�o informado.", req);
			return edit(mapping, relatorioForm, req, res);
		}

		// Definir como aprovado
		relatorioForm.getObj().setAvaliacao(RelatorioProjeto.APROVADO);
		return persistir(mapping, form, req, res);
	}

	/** Reprovar relat�rio de projeto de pesquisa */
	public ActionForward reprovar(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;

		if ( isEmpty( relatorioForm.getObj().getParecerConsultor() ) ) {
			addMensagemErro("Parecer: Campo Obrigat�rio n�o informado.", req);
			return edit(mapping, relatorioForm, req, res);
		}
		
		// Definir como n�o aprovado
		relatorioForm.getObj().setAvaliacao(RelatorioProjeto.NECESSITA_CORRECAO);
		return persistir(mapping, form, req, res);
	}

	/** Persistir avalia��o */
	private ActionForward persistir(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;
		RelatorioProjeto relatorio = relatorioForm.getObj();

		// TODO Validar dados
		relatorio.setCodMovimento(SigaaListaComando.AVALIAR_RELATORIO_PROJETO_PESQUISA);
		relatorio = (RelatorioProjeto) execute(relatorio, req);

		addInformation("Avalia��o do relat�rio do projeto '" + relatorio.getProjetoPesquisa().getTitulo() + "' realizada com sucesso!", req);

		if (getSubSistemaCorrente(req).equals(SigaaSubsistemas.PORTAL_CONSULTOR)) {
			return mapping.findForward(SigaaSubsistemas.PORTAL_CONSULTOR.getForward());
		} else {
			req.setAttribute("popular", "true");
			return listRelatoriosFinais(mapping, form, req, res);
		}
	}

	/**
	 * Buscar as informa��es referentes � submiss�o de relat�rios finais
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward listRelatoriosFinais(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class, req);
		RelatorioProjetoDao relatorioDao = getDAO(RelatorioProjetoDao.class, req);

		RelatorioProjetoForm relatorioForm = (RelatorioProjetoForm) form;

		// Dados do formul�rio
		req.setAttribute("areasConhecimento", dao.findGrandeAreasConhecimentoCnpq());

		if (req.getParameter("popular") != null || req.getAttribute("popular") != null) {
			relatorioForm.getObj().getProjetoPesquisa().setAno(CalendarUtils.getAnoAtual()-1);
			req.setAttribute(mapping.getName(), relatorioForm);

			return mapping.findForward("lista_relatorios_finais");
		}

		Integer ano = null;
		Integer idCoordenador = null;
		Integer idArea = null;

		Collection<RelatorioProjeto> lista;
		ListaMensagens erros = new ListaMensagens();

		// Defini��o dos filtros e valida��es
		for(int filtro : relatorioForm.getFiltros() ){
			switch(filtro) {
			case MembroProjetoServidorForm.BUSCA_ANO:
				ano = relatorioForm.getObj().getProjetoPesquisa().getAno();
				ValidatorUtil.validaInt(ano, "Ano do Projeto", erros);
				break;
			case MembroProjetoServidorForm.BUSCA_COORDENADOR:
				idCoordenador = relatorioForm.getObj().getProjetoPesquisa().getCoordenador().getId();
				ValidatorUtil.validateRequiredId(idCoordenador, "Coordenador de Projeto", erros);
				break;
			case MembroProjetoServidorForm.BUSCA_AREA_CONHECIMENTO:
				idArea = relatorioForm.getObj().getProjetoPesquisa().getAreaConhecimentoCnpq().getId();
				ValidatorUtil.validateRequiredId(idArea, "�rea de Conhecimento", erros);
				break;
			}
		}

		// Verificar se foram detectados erros
		if (erros.isEmpty()) {
			lista = relatorioDao.filter(ano, idCoordenador, idArea);
			req.setAttribute("lista", lista);
			if (lista.isEmpty())
				addMensagemErro("Nenhum projeto encontrado para esse docente", req);
			
		} else {
			addMensagens(erros.getMensagens(), req);
		}

		return mapping.findForward("lista_relatorios_finais");
	}
	
}
