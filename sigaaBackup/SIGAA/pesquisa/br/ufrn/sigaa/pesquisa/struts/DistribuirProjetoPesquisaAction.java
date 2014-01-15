/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.pesquisa.AvaliacaoProjetoDao;
import br.ufrn.sigaa.arq.dao.pesquisa.EditalPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.form.DistribuicaoProjetosForm;
import br.ufrn.sigaa.pesquisa.negocio.DistribuicaoProjetos;
import br.ufrn.sigaa.pesquisa.negocio.DistribuicaoProjetosValidator;
import br.ufrn.sigaa.pesquisa.negocio.MovimentoDistribuicaoPesquisa;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Action responsável pelas operações de distribuição de projetos
 * de pesquisa para avaliação pelos consultores
 *
 * @author ilueny santos
 * @author Victor Hugo
 * @author Ricardo Wendell
 *
 */
public class DistribuirProjetoPesquisaAction extends AbstractCrudAction {

	/** FORM FORWARDS */
	public static final String FORM_AUTOMATICA 		= "formAutomatica";
	public static final String FORM_ESPECIAIS 		= "formEspeciais";
	public static final String FORM_CENTROS			= "formCentros";
	public static final String FORM_MANUAL 			= "formManual";
	public static final String COMPROVANTE		 	= "comprovante";
	public static final String FORM_DETALHES		= "formDetalhes";
	public static final String DETALHES				= "detalhes";

	/**
	 * Popula o formulário para a realização da distribuição automática de projetos
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularAutomatica(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		prepareMovimento(SigaaListaComando.DISTRIBUIR_PROJETO_PESQUISA,req);
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class, req);
		EditalPesquisaDao editalDao = getDAO(EditalPesquisaDao.class, req);

		DistribuicaoProjetosForm dForm = (DistribuicaoProjetosForm) form;
		
		// Buscar resumo da distribuição de projetos, areas e consultores disponíveis
		Collection<AreaConhecimentoCnpq> areas = dao.findByResumoGrandeArea( new int[]{TipoSituacaoProjeto.SUBMETIDO , 
				TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE, TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE}, dForm.getIdEdital() );
		req.setAttribute("areas", areas);
		req.setAttribute("editais", editalDao.findAllAtivos());

		// Redirecionar para o formulário
		return mapping.findForward(FORM_AUTOMATICA);
	}

	/**
	 * Popula o formulário para a realização da distribuição automática de projetos
	 * para consultores especiais
	 */
	public ActionForward popularAutomaticaEspeciais(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		prepareMovimento(SigaaListaComando.DISTRIBUIR_PROJETO_PESQUISA,req);
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class, req);
		EditalPesquisaDao editalDao = getDAO(EditalPesquisaDao.class, req);
		
		DistribuicaoProjetosForm dForm = (DistribuicaoProjetosForm) form;

		// Buscar resumo da distribuição de projetos, áreas e consultores disponíveis
		Collection<AreaConhecimentoCnpq> areas = dao.findByResumoGrandeArea( new int[]{TipoSituacaoProjeto.SUBMETIDO, 
			TipoSituacaoProjeto.DISTRIBUIDO_AUTOMATICAMENTE, TipoSituacaoProjeto.DISTRIBUIDO_MANUALMENTE}, dForm.getIdEdital(), true );
		req.setAttribute("areas", areas);

		Collection<EditalPesquisa> editais = editalDao.findAllAtivos();
		req.setAttribute("editais", editais);
		

		// Redirecionar para o formulário
		return mapping.findForward(FORM_ESPECIAIS);
	}
	
	/**
	 * Realiza a distribuição automática de projetos
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward automaticamente(ActionMapping mapping,
			ActionForm form, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		DistribuicaoProjetosForm dForm = (DistribuicaoProjetosForm) form;
		
		// Validar valor do número de consultores por projeto
		if( req.getParameter("consultoresPorProjeto") == null || req.getParameter("consultoresPorProjeto").trim().length() == 0 )
			addMensagemErro("Informe a quantidade de Consultores", req);
		int consultoresPorItem = Integer.parseInt(req.getParameter("consultoresPorProjeto"));

		if (consultoresPorItem <= 0){
			addMensagemErro("É necessário informar o número de consultores por projeto", req);
		}

		if (flushErros(req)) {
			return popularAutomatica(mapping, form, req, res);
		}

		// Preparar movimento de distribuição
		MovimentoDistribuicaoPesquisa mov = new MovimentoDistribuicaoPesquisa();
		mov.setConsultoresPorItem(consultoresPorItem);
		mov.setIdEdital(dForm.getIdEdital());
		mov.setAcao(MovimentoDistribuicaoPesquisa.DISTRIBUIR_AUTOMATICAMENTE);
		mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_PROJETO_PESQUISA);

		try {
			// Efetuar distribuição
			Map< AreaConhecimentoCnpq, Collection<DistribuicaoProjetos> > dist = (Map<AreaConhecimentoCnpq, Collection<DistribuicaoProjetos>>) execute(mov, req);

			// Resultado da distribuição
			req.setAttribute("distribuidos", dist);
			req.setAttribute("totalDistribuidos", dist.values().size() );
			addInformation("Projetos distribuídos aos consultores com sucesso!", req);
		} catch (Exception e) {
//			addMensagemErro("Erro na distribuição dos projetos", req);
//			e.printStackTrace();
			throw new ArqException(e);
//			return popularAutomatica(mapping, form, req, res);
		}

		return mapping.findForward(COMPROVANTE);
	}

	/**
	 * Realiza a distribuição automática de projetos para consultores especiais
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward automaticamenteEspeciais(ActionMapping mapping,
			ActionForm form, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		DistribuicaoProjetosForm dForm = (DistribuicaoProjetosForm) form;
		
		// Validar valor do número de consultores por projeto
		if( req.getParameter("consultoresPorProjeto") == null || req.getParameter("consultoresPorProjeto").trim().length() == 0 )
			addMensagemErro("Informe a quantidade de Consultores", req);
		int consultoresPorItem = Integer.parseInt(req.getParameter("consultoresPorProjeto"));

		if (consultoresPorItem <= 0){
			addMensagemErro("É necessário informar o número de consultores por projeto", req);
		}

		if (flushErros(req)) {
			return popularAutomaticaEspeciais(mapping, form, req, res);
		}

		// Preparar movimento de distribuição
		MovimentoDistribuicaoPesquisa mov = new MovimentoDistribuicaoPesquisa();
		mov.setConsultoresPorItem(consultoresPorItem);
		mov.setIdEdital(dForm.getIdEdital());
		mov.setAcao(MovimentoDistribuicaoPesquisa.DISTRIBUIR_AUTOMATICAMENTE_ESPECIAIS);
		mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_PROJETO_PESQUISA);

		try {
			// Efetuar distribuição
			Map< AreaConhecimentoCnpq, Collection<DistribuicaoProjetos> > dist = (Map<AreaConhecimentoCnpq, Collection<DistribuicaoProjetos>>) execute(mov, req);

			// Resultado da distribuição
			req.setAttribute("distribuidos", dist);
			req.setAttribute("totalDistribuidos", dist.values().size() );
			addInformation("Projetos distribuídos aos consultores especiais com sucesso!", req);
		} catch (Exception e) {
			addMensagemErro("Erro na distribuição dos projetos", req);
			e.printStackTrace();
			return popularAutomaticaEspeciais(mapping, form, req, res);
		}

		return mapping.findForward(COMPROVANTE);
	}
	
	/**
	 * Popula o formulário de distribuição de projetos por centro
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularCentros(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class, req);

		// Lista o resumo dos projetos agrupados por area de conhecimento
		Collection<AreaConhecimentoCnpq> areasConhecimento = dao.findByResumoGrandeArea( new int[]{TipoSituacaoProjeto.AVALIACAO_INSUFICIENTE}, null );

		// Lista todos os consultores internos
		Collection<Consultor> consultoresInternos = dao.findByExactField(Consultor.class, "interno", false, "asc", "nome");

		req.setAttribute("consultoresInternos", consultoresInternos);
		req.setAttribute("areasConhecimento", areasConhecimento);

		return mapping.findForward(FORM_CENTROS);
	}


	/**
	 * Realiza a distribuição de projetos por centro
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward centros(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		prepareMovimento(SigaaListaComando.DISTRIBUIR_PROJETO_PESQUISA,req);

		GenericDAO dao = getGenericDAO(req);
		Map<Consultor, AreaConhecimentoCnpq> mapaDistribuicaoManual = new HashMap<Consultor, AreaConhecimentoCnpq>();

		int totalAreas = Integer.parseInt( req.getParameter("totalAreas"));

		for (int i = 1; i <= totalAreas; i++) {

			int idAreaConhecimentoCnpq = Integer.parseInt(req.getParameter("idGrandeArea"+i));
			AreaConhecimentoCnpq area = dao.findByPrimaryKey(idAreaConhecimentoCnpq, AreaConhecimentoCnpq.class);

			int idConcultor = Integer.parseInt(req.getParameter("consultor_"+idAreaConhecimentoCnpq));
			if (idConcultor > 0){  //escolheu um consultor
				Consultor consultor = dao.findByPrimaryKey(idConcultor, Consultor.class);
				mapaDistribuicaoManual.put(consultor, area);
			}
		}

		MovimentoDistribuicaoPesquisa mov = new MovimentoDistribuicaoPesquisa();
		mov.setMapaDistribuicaoManual(mapaDistribuicaoManual);
		mov.setAcao(MovimentoDistribuicaoPesquisa.DISTRIBUIR_MANUALMENTE);
		mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_PROJETO_PESQUISA);

		Collection<ProjetoPesquisa> dist = (Collection) execute(mov, req);
		req.setAttribute("distribuidos", dist);
		addInformation("Projetos distribuídos com sucesso!", req);

		return mapping.findForward(COMPROVANTE);
	}

	/**
	 * Popula o formulário de distribuição manual de projetos
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward popularManual(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		// Popular código dos projetos com o ano atual
		DistribuicaoProjetosForm distribuicaoForm = (DistribuicaoProjetosForm) form;
		distribuicaoForm.getCodigo().setAno( CalendarUtils.getAnoAtual() );

		prepareMovimento(SigaaListaComando.DISTRIBUIR_PROJETO_PESQUISA,req);
		return mapping.findForward(FORM_MANUAL);
	}

	/**
	 * Adicionar um projeto à distribuição manual
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward adicionarProjeto(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		ProjetoPesquisaDao dao = getDAO(ProjetoPesquisaDao.class, req);

		DistribuicaoProjetosForm distribuicaoForm = (DistribuicaoProjetosForm) form;

		// Adicionar projeto
		if (!flushErros(req)) {

			// Buscar projeto
			ProjetoPesquisa projeto = dao.findByCodigo(distribuicaoForm.getCodigo());

			// Verificar existência do projeto
			if ( projeto != null ) {

				if ( distribuicaoForm.getProjetos().contains(projeto) ) {
					addMensagemErro(" O projeto informado já consta na lista para distribuição  ", req);
					return mapping.findForward(FORM_MANUAL);
				}

				// Validar possibilidade de distribuição do projeto
				ListaMensagens erros = new ListaMensagens();
				DistribuicaoProjetosValidator.validaProjeto(projeto, erros);
				if ( !erros.isEmpty() ) {
					addMensagens(erros.getMensagens(), req);
					return mapping.findForward(FORM_MANUAL);
				}

				distribuicaoForm.getProjetos().add(projeto);
				distribuicaoForm.getCodigo().setNumero(0);

			} else {
				addMensagemErro(" Projeto de pesquisa não foi encontrado. Verifique o código informado", req);
			}
		}

		return mapping.findForward(FORM_MANUAL);
	}

	/**
	 * Remover um projeto da distribuição manual
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward removerProjeto(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
		DistribuicaoProjetosForm distribuicaoForm = (DistribuicaoProjetosForm) form;

		removePorPosicao( distribuicaoForm.getProjetos() , distribuicaoForm.getAcao());

		return mapping.findForward(FORM_MANUAL);
	}

	/**
	 *
	 * Realizar distribuição manual
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward manual(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);

		DistribuicaoProjetosForm distribuicaoForm = (DistribuicaoProjetosForm) form;

		// Validar dados da distribuição
		ListaMensagens erros = new ListaMensagens();
		DistribuicaoProjetosValidator.validaDistribuicaoManual(
				distribuicaoForm.getConsultor(),
				distribuicaoForm.getProjetos(),
				erros);
		if ( !erros.isEmpty() ) {
			addMensagens(erros.getMensagens(), req);
			return mapping.findForward(FORM_MANUAL);
		}

		// Preparar movimento de distribuição
		MovimentoDistribuicaoPesquisa mov = new MovimentoDistribuicaoPesquisa();
		mov.setConsultor( distribuicaoForm.getConsultor() );
		mov.setProjetos( distribuicaoForm.getProjetos() );
		mov.setAcao(MovimentoDistribuicaoPesquisa.DISTRIBUIR_MANUALMENTE);
		mov.setCodMovimento(SigaaListaComando.DISTRIBUIR_PROJETO_PESQUISA);

		try {
			// Efetuar distribuição
			Map< AreaConhecimentoCnpq, Collection<DistribuicaoProjetos> > dist = (Map<AreaConhecimentoCnpq, Collection<DistribuicaoProjetos>>) execute(mov, req);

			// Resultado da distribuição
			req.setAttribute("distribuidos", dist);
			req.setAttribute("totalDistribuidos", distribuicaoForm.getProjetos().size() );
			addInformation("Projetos distribuídos com sucesso!", req);

			removeSession(mapping.getName(), req);

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
			return mapping.findForward(FORM_MANUAL);
		}

		return mapping.findForward(COMPROVANTE);
	}

	/**
	 * Responsável pela consulta dos resultados da distribuição dos projetos de pesquisa
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws ArqException
	 */
	public ActionForward consultaResultadoDistribuicao(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws ArqException{
		AvaliacaoProjetoDao dao = getDAO(AvaliacaoProjetoDao.class, req);

		req.setAttribute("centros", dao.findAll(SiglaUnidadePesquisa.class, "unidade.nome", "asc"));
		req.setAttribute("editaisPesquisa", dao.findAll(EditalPesquisa.class, "edital.dataCadastro", "desc"));

		Map<Integer, String> tipos = new HashMap<Integer, String>();
		tipos.put(AvaliacaoProjeto.TIPO_DISTRIBUICAO_AUTOMATICA, "DISTRIBUIÇÃO AUTOMÁTICA");
		tipos.put(AvaliacaoProjeto.TIPO_DISTRIBUICAO_MANUAL, "DISTRIBUIÇÃO MANUAL");
		req.setAttribute( "tipos" , tipos);

		return mapping.findForward(FORM_DETALHES);
	}
	/**
	 * Exibir os detalhes da distribuição
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws ArqException
	 */
	public ActionForward resultadoDistribuicao(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws ArqException{
		DistribuicaoProjetosForm distribuicaoForm = (DistribuicaoProjetosForm) form;
		AvaliacaoProjetoDao dao = getDAO(AvaliacaoProjetoDao.class, req);

		// Analisando filtros selecionados
		Consultor consultor = null;
		Unidade centro = null;
		Integer tipoDistribuicao = null;
		Integer idEdital = null;
		EditalPesquisa edital = null;

		for(int filtro : distribuicaoForm.getFiltros() ){
			switch(filtro) {
				case DistribuicaoProjetosForm.CONSULTOR:
					consultor = distribuicaoForm.getConsultor();
					break;
				case DistribuicaoProjetosForm.CENTRO:
					centro = distribuicaoForm.getCentro();
					break;
				case DistribuicaoProjetosForm.TIPO_DISTRIBUICAO:
					tipoDistribuicao = distribuicaoForm.getTipoDistribuicao();
					break;
				case DistribuicaoProjetosForm.EDITAL:
					idEdital = distribuicaoForm.getIdEdital();
					edital = new EditalPesquisa();
					edital.setId(idEdital);
					break;
			}
		}
		Collection<AvaliacaoProjeto> avaliacoes = dao.filter(null,
				AvaliacaoProjeto.AGUARDANDO_AVALIACAO,
				consultor,
				centro,
				tipoDistribuicao, edital);
		req.setAttribute("avaliacoes", avaliacoes);

		return mapping.findForward(DETALHES);
	}

}
