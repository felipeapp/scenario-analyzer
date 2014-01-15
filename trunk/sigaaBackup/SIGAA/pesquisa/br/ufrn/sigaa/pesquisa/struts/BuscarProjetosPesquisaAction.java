/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/11/2006
 *
 */
package br.ufrn.sigaa.pesquisa.struts;
/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 06/07/2007
 * Autor: Raphaela
 * 
 */
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.arq.struts.AbstractCrudAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.CategoriaProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.CodigoProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.FinanciamentoProjetoPesq;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.SiglaUnidadePesquisa;
import br.ufrn.sigaa.pesquisa.form.ProjetoPesquisaForm;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.projetos.dominio.ClassificacaoFinanciadora;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Action responsável pelas diferentes buscas de projetos de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
public class BuscarProjetosPesquisaAction extends AbstractCrudAction {

	/** Forwards */
	private final static String RELATORIO = "relatorio";
	private final static String RELATORIO_FINANCIAMENTOS = "relatorio_financiamentos";
	private final static String RELATORIO_FINANCIAMENTOS_IMPRESSAO = "relatorio_financiamentos_impressao";

	/**
	 * Consulta de Projetos de Pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/projetos.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/relatorio.jsp</li>
	 *		<li>sigaa.war/WEB-INF/menu_pesquisa.jsp</li>
	 *	</ul>
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward consulta(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		projetoForm.setConsulta(Boolean.parseBoolean(req.getParameter("consulta")));
		if (projetoForm.isConsulta()) 
			projetoForm.setFinalidadeBusca(FinalidadeBuscaProjeto.CONSULTA);
		else
			projetoForm.setFinalidadeBusca(FinalidadeBuscaProjeto.GERENCIAR);
		req.setAttribute("popular", req.getParameter("popular") == null ? req.getAttribute("popular") : null);
		return list(mapping, form, req, res);
	}

	/**
	 * Busca de projetos para cadastro de planos de trabalho.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp</li>
	 *	</ul>
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward cadastroPlanoTrabalho(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		projetoForm.setFinalidadeBusca(FinalidadeBuscaProjeto.CADASTRO_PLANO_TRABALHO);
		req.setAttribute("popular", req.getParameter("popular"));
		return list(mapping, form, req, res);
	}

	/**
	 * Popula a tela de busca e/ou consulta projetos pelos filtros selecionados.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war//WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {

		// Popular coleções para selects
		AreaConhecimentoCnpqDao daoArea = getDAO( AreaConhecimentoCnpqDao.class, req );
		req.setAttribute("areasConhecimento", daoArea.findAllProjection(AreaConhecimentoCnpq.class, "nome", "asc", new String[]{"id", "nome"}));
		req.setAttribute("situacoesProjeto", daoArea.findByExactField(TipoSituacaoProjeto.class, "contexto", "P", "asc", "id" ));
		req.setAttribute("centros", daoArea.findAll(SiglaUnidadePesquisa.class, "unidade.nome", "asc") );
		req.setAttribute("gruposPesquisa", daoArea.findAllProjection(GrupoPesquisa.class, "nome", "asc", new String[]{"id", "nome"}));
		req.setAttribute("entidadesFinanciadoras", daoArea.findAllProjection(EntidadeFinanciadora.class, "nome", "asc", new String[]{"id", "nome"}));
		req.setAttribute("categorias", daoArea.findAllAtivos(CategoriaProjetoPesquisa.class, "ordem"));
		req.setAttribute("editais", daoArea.findAll(EditalPesquisa.class, "edital.dataCadastro", "desc"));
		
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		ProjetoPesquisa projeto = projetoForm.getObj();

		if (req.getParameter("popular") != null || req.getAttribute("popular") != null) {
			int finalidade = projetoForm.getFinalidadeBusca();
			projetoForm = new ProjetoPesquisaForm();
			projetoForm.setFinalidadeBusca(finalidade);
			projetoForm.getObj().getCodigo().setAno(CalendarUtils.getAnoAtual());
			req.getSession().setAttribute(mapping.getName(), projetoForm);

			if (req.getParameter("relatorio") != null) {
				projetoForm.setFiltros( new int[] {TipoFiltroProjetoPesquisa.FORMATO_RELATORIO});
			}

			return mapping.findForward(LISTAR);
		}

		ProjetoPesquisaDao projetoDao = getDAO( ProjetoPesquisaDao.class, req );
		Collection<ProjetoPesquisa> lista = null;

		/* Analisando filtros selecionados */
		Boolean tipo = null;
		CodigoProjetoPesquisa codigoProjeto = null;
		Integer ano = null;
		String palavraChave = null;
		Integer idPesquisador = null;
		Integer idCentro = null;
		Integer idUnidade = null;
		String titulo = null;
		String objetivos = null;
		String linhaPesquisa = null;
		Integer idSubarea = null;
		Integer idGrupoPesquisa = null;
		Integer idAgenciaFinanciadora = null;
		Integer idEdital = null;
		Integer idStatusProjeto = null;
		Integer categoriaProjeto = null;
		Boolean relatorioEnviado = null;
		boolean formatoRelatorio = false;

		ListaMensagens erros = new ListaMensagens();


		// Definição dos filtros e validações
		for(int filtro : projetoForm.getFiltros() ){
			switch(filtro) {
				case TipoFiltroProjetoPesquisa.TIPO_PROJETO:
					tipo = projeto.isInterno();
					break;
				case TipoFiltroProjetoPesquisa.CODIGO:
					codigoProjeto = prepararCodigoProjeto(projetoForm.getCodigo(), req);
					break;
				case TipoFiltroProjetoPesquisa.ANO:
					ano = projeto.getCodigo().getAno();
					ValidatorUtil.validaInt(ano, "Ano do Projeto", erros);
					break;
				case TipoFiltroProjetoPesquisa.PESQUISADOR:
					idPesquisador = projetoForm.getMembroProjeto().getServidor().getId();
					ValidatorUtil.validateRequiredId(idPesquisador, "Pesquisador", erros);
					break;
				case TipoFiltroProjetoPesquisa.CENTRO:
					idCentro = projetoForm.getCentro().getId();
					ValidatorUtil.validateRequiredId(idCentro, "Centro", erros);
					if(idCentro > 0)
						projetoForm.setCentro(projetoDao.findByPrimaryKey(idCentro, Unidade.class));
					break;
				case TipoFiltroProjetoPesquisa.UNIDADE:
					idUnidade = projetoForm.getUnidade().getId();
					ValidatorUtil.validateRequiredId(idUnidade, "Unidade", erros);
					break;
				case TipoFiltroProjetoPesquisa.TITULO:
					titulo = projetoForm.getTitulo();
					ValidatorUtil.validateRequired(titulo, "Título", erros);
					break;
				case TipoFiltroProjetoPesquisa.OBJETIVOS:
					objetivos = projetoForm.getObjetivos();
					ValidatorUtil.validateRequired(objetivos, "Objetivos", erros);
					break;
				case TipoFiltroProjetoPesquisa.LINHA_PESQUISA:
					linhaPesquisa = projeto.getLinhaPesquisa().getNome();
					ValidatorUtil.validateRequired(linhaPesquisa, "Linha de Pesquisa", erros);
					break;
				case TipoFiltroProjetoPesquisa.SUBAREA:
					idSubarea = projetoForm.getSubarea().getId();
					ValidatorUtil.validateRequiredId(idSubarea, "Área de Conhecimento", erros);
					if(idSubarea > 0)
						projetoForm.setSubarea(projetoDao.findByPrimaryKey(idSubarea, AreaConhecimentoCnpq.class));
					break;
				case TipoFiltroProjetoPesquisa.GRUPO_PESQUISA:
					idGrupoPesquisa = projeto.getLinhaPesquisa().getGrupoPesquisa().getId();
					ValidatorUtil.validateRequiredId(idGrupoPesquisa, "Grupo de Pesquisa", erros);
					if(idGrupoPesquisa > 0)
						projeto.getLinhaPesquisa().setGrupoPesquisa(projetoDao.findByPrimaryKey(idGrupoPesquisa, GrupoPesquisa.class));
					break;
				case TipoFiltroProjetoPesquisa.AGENCIA_FINANCIADORA:
					idAgenciaFinanciadora = projetoForm.getFinanciamentoProjetoPesq().getEntidadeFinanciadora().getId();
					ValidatorUtil.validateRequiredId(idAgenciaFinanciadora, "Agência Financiadora", erros);
					if(idAgenciaFinanciadora > 0)
						projetoForm.getFinanciamentoProjetoPesq().setEntidadeFinanciadora(projetoDao.findByPrimaryKey(idAgenciaFinanciadora, EntidadeFinanciadora.class));
					break;
				case TipoFiltroProjetoPesquisa.EDITAL_PESQUISA:
					idEdital = projeto.getEdital().getId();
					ValidatorUtil.validateRequired(idEdital, "Edital", erros);
					break;
				case TipoFiltroProjetoPesquisa.STATUS_PROJETO:
					idStatusProjeto = projeto.getSituacaoProjeto().getId();
					ValidatorUtil.validateRequiredId(idStatusProjeto, "Situação do Projeto", erros);
					if(idStatusProjeto > 0)
						projeto.setSituacaoProjeto(projetoDao.findByPrimaryKey(idStatusProjeto, TipoSituacaoProjeto.class));
					break;
				case TipoFiltroProjetoPesquisa.CATEGORIA_PROJETO:
					categoriaProjeto = projeto.getCategoria().getId();
					ValidatorUtil.validateRequired(categoriaProjeto, "Categoria do Projeto", erros);
					break;
				case TipoFiltroProjetoPesquisa.STATUS_RELATORIO:
					relatorioEnviado = projetoForm.isRelatorioSubmetido();
					break;
				case TipoFiltroProjetoPesquisa.FORMATO_RELATORIO:
					formatoRelatorio = true;
					break;
			}
		}

		try {

			if (erros.isEmpty()) {
				lista = projetoDao.filter(
						tipo,
						codigoProjeto,
						ano,
						null,
						idPesquisador,
						palavraChave,
						idCentro,
						idUnidade,
						titulo,
						objetivos,
						linhaPesquisa,
						idSubarea,
						idGrupoPesquisa,
						idAgenciaFinanciadora,
						idEdital,
						idStatusProjeto,
						categoriaProjeto,
						relatorioEnviado, formatoRelatorio);
				req.setAttribute("lista", lista);

				if (!lista.isEmpty() && formatoRelatorio) {
					req.setAttribute("relatorio", true);
				}

			} else {
				addMensagens(erros.getMensagens(), req);
			}

		} catch (LimiteResultadosException e) {
			addMensagemErro(e.getMessage(), req);
		}


		if (lista != null && !lista.isEmpty() && formatoRelatorio)
			return mapping.findForward(RELATORIO);
		else
			return mapping.findForward(LISTAR);

	}

	/**
	 * Prepara os dados do código do projeto de pesquisa para o formato utilizado na busca.
	 * Método não invocado por JSP.
	 * @param cod
	 * @param req
	 * @return
	 */
	public static CodigoProjetoPesquisa prepararCodigoProjeto(String cod, HttpServletRequest req) {
		
		CodigoProjetoPesquisa codigo = null;
		try {
			codigo = new CodigoProjetoPesquisa(cod);
		} catch ( IllegalArgumentException e ) {
			addMensagemErro(e.getMessage(), req);
			return null;
		}

		if (codigo.getPrefixo() == null || codigo.getNumero() == 0 || codigo.getAno() == 0) {
			codigo = null;
		}
		return codigo;
	}

	/**
	 * Popula/gera o relatório de projetos financiados.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/financiamentos.jsp</li>
	 *	</ul>
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward financiamentos(ActionMapping mapping, ActionForm form,
				
			HttpServletRequest req, HttpServletResponse res)
			throws Exception {

        ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		ProjetoPesquisaDao projetoDao = getDAO( ProjetoPesquisaDao.class, req );

		Collection<ProjetoPesquisa> projetos = new ArrayList<ProjetoPesquisa>();

		// Preparar consulta
		req.setAttribute("classificacoes", projetoDao.findAll(ClassificacaoFinanciadora.class));
		req.setAttribute("entidades", projetoDao.findAll(EntidadeFinanciadora.class));
		if (req.getParameter("popular") != null) {
			projetoForm.getObj().getCodigo().setAno(CalendarUtils.getAnoAtual());
			projetoForm.getFinanciamentoProjetoPesq().getEntidadeFinanciadora().
				setClassificacaoFinanciadora(new ClassificacaoFinanciadora());
			req.setAttribute(mapping.getName(), projetoForm);
			return mapping.findForward(RELATORIO_FINANCIAMENTOS);
		}

		// Validar dados do formulário
		ListaMensagens lista = new ListaMensagens();
		validarBuscaFinanciamentos(projetoForm, req, lista);
		if (flushErros(req)) {
			projetoForm.getObj().getLinhaPesquisa().setGrupoPesquisa( new GrupoPesquisa() );
			return mapping.findForward(RELATORIO_FINANCIAMENTOS);
		}

		// Buscar projetos
		int idClassificacaoFinanciadora = projetoForm.getFinanciamentoProjetoPesq().getEntidadeFinanciadora() 
		.getClassificacaoFinanciadora().getId();
		int idEntidade = projetoForm.getFinanciamentoProjetoPesq().getEntidadeFinanciadora().getId();
		projetos = projetoDao.findByClassificacaoFinanciamento(idClassificacaoFinanciadora,	projetoForm.getObj().getCodigo().getAno(),	idEntidade);

		projetoForm.getFinanciamentoProjetoPesq().setEntidadeFinanciadora(
				projetoDao.findByPrimaryKey(idEntidade, EntidadeFinanciadora.class));
		if(idClassificacaoFinanciadora > 0){
			projetoForm.getFinanciamentoProjetoPesq().getEntidadeFinanciadora().setClassificacaoFinanciadora(
				projetoDao.findByPrimaryKey(idClassificacaoFinanciadora, ClassificacaoFinanciadora.class));
		}
		
		
		
		if (isEmpty(projetoForm.getFinanciamentoProjetoPesq().getEntidadeFinanciadora())) {
			
			projetoForm.getFinanciamentoProjetoPesq().setEntidadeFinanciadora(new EntidadeFinanciadora());
			
			addMensagemErro("Selecione uma Entidade Financiadora . ", req);
			return mapping.findForward(RELATORIO_FINANCIAMENTOS);
		}
		
	
		
		if(projetos.size() == 0){
			addMensagemErro("Nenhum registro encontrado de acordo com os critérios de busca informados.", req);
			projetoForm.getFinanciamentoProjetoPesq().getEntidadeFinanciadora().
				setClassificacaoFinanciadora(new ClassificacaoFinanciadora());
			return mapping.findForward(RELATORIO_FINANCIAMENTOS);
		}
		req.setAttribute("lista", projetos);
		req.setAttribute(mapping.getName(), projetoForm);
		return mapping.findForward(RELATORIO_FINANCIAMENTOS_IMPRESSAO);
	}

	private void validarBuscaFinanciamentos(ProjetoPesquisaForm projetoForm, HttpServletRequest req, ListaMensagens erros) {
		ValidatorUtil.validateRequiredId(projetoForm.getFinanciamentoProjetoPesq().getEntidadeFinanciadora().getId(),
				"Entidade Financiadora", erros);
//		ValidatorUtil.validateRequiredId(projetoForm.getFinanciamentoProjetoPesq().getEntidadeFinanciadora().getClassificacaoFinanciadora().getId(),
//				"Informe a classificação do financiamento dos projetos de pesquisa", erros);
		int ano = projetoForm.getObj().getCodigo().getAno();
		
		
		ValidatorUtil.validaInt(ano, "Informe o ano dos projetos", erros);
		
		if (ano > CalendarUtils.getAnoAtual()) {
			addMensagemErro("Informe um ano válido", req);
		}
	}

	/**
	 * Busca de projetos para alteração de situação e/ou tipo do projeto.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/menu/projetos.jsp</li>
	 *	</ul>
	 *
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward alterarSituacao(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		projetoForm.setFinalidadeBusca(FinalidadeBuscaProjeto.ALTERAR_SITUACAO);
		req.setAttribute("popular", req.getParameter("popular"));
		return list(mapping, form, req, res);
	}
	
	/**
	 * Monta e exibe a tela de visualização de um projeto de pesquisa.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/comprovante.jsp</li>
	 *		<li>sigaa.war/WEB-INF/jsp/pesquisa/ProjetoPesquisa/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest req, HttpServletResponse res) throws Exception {
		super.view(mapping, form, req, res);

		ProjetoPesquisaForm projetoForm = (ProjetoPesquisaForm) form;
		projetoForm.setObj((ProjetoPesquisa) req.getAttribute(ConstantesCadastro.ATRIBUTO_VISUALIZACAO));

		// Inicializar tela do cronograma
		TelaCronograma cronograma = new TelaCronograma(
				projetoForm.getObj().getDataInicio(),
				projetoForm.getObj().getDataFim(),
				projetoForm.getObj().getCronogramas()
			);
		projetoForm.setTelaCronograma(cronograma);

		req.setAttribute(mapping.getName(), projetoForm);
		return mapping.findForward(VIEW);
	}

}
