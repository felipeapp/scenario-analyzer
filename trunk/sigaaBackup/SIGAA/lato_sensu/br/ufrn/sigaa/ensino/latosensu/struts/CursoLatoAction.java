/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on '27/08/2008'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.struts; 

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.EquipeLatoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractWizardAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoTrabalhoConclusao;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.latosensu.dominio.ComponenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteDisciplinaLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaAvaliacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaAvaliacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaSelecao;
import br.ufrn.sigaa.ensino.latosensu.dominio.FormaSelecaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.OutrosDocumentos;
import br.ufrn.sigaa.ensino.latosensu.dominio.ParceriaLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.PublicoAlvoCurso;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoPeriodicidadeAula;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.ensino.latosensu.form.CursoLatoForm;
import br.ufrn.sigaa.ensino.latosensu.negocio.CursoLatoMov;
import br.ufrn.sigaa.ensino.latosensu.negocio.CursoLatoValidator;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pessoa.dominio.TipoDocenteExterno;
import br.ufrn.sigaa.projetos.dominio.TipoPublicoAlvo;

/**
 * Action com as operações de gerenciamento dos cursos de pós-graduação lato
 * sensu
 * 
 * @author Leonardo Campos
 * 
 */
public class CursoLatoAction extends AbstractWizardAction {

	/** Constante da tela dos dados gerais */
	public static final String DADOS_GERAIS = "dadosGerais";
	/** Constante da tela da proposta */
	public static final String PROPOSTA = "proposta";
	/** Constante da tela da seleção */
	public static final String SELECAO = "selecao";
	/** Constante da tela de recursos */
	public static final String RECURSOS = "recursos";
	/** Constante da tela de docentes */
	public static final String DOCENTES = "docentes";
	/** Constante da tela da turma de entrada */
	public static final String TURMAS_ENTRADA = "turmasEntrada";
	/** Constante da tela das disciplinas */
	public static final String DISCIPLINAS = "disciplinas";
	/** Constante da tela da coordenação do curso */
	public static final String COORDENACAO_CURSO = "coordenacaoCurso";
	/** Constante da tela do resumo */
	public static final String RESUMO = "resumo";
	/** Constante da tela do resumo para impressão */
	public static final String RESUMO_IMPRESSAO = "resumo_impressao";
	/** Constante da tela para continuação */
	public static final String CONTINUAR = "continuar";

	/** Constante de atualização do curso Lato */
	public static final String ATUALIZAR = "Atualizar";
	/** Constante de cadastro do curso Lato */
	public static final String CADASTRAR = "Cadastrar";
	/** Constante de remoção do curso Lato */
	public static final String REMOVER = "Remover";
	/** Constante de renovação do curso Lato */
	public static final String RENOVAR = "Renovar";
	/** Constante de visualização do curso Lato */
	public static final String VISUALIZAR = "Visualizar";
	/** Constante das operações utilizadas no curso Lato */
	public static final String OPERACAO = "operacao";
	/** Constante de passo atual no cadastro do curso Lato */
	public static final String PASSO_ATUAL = "passo_atual";

	/** Constantes das operações */
	private static final String REMOVER_PROPOSTA = "1";

	/** Auxiliar */
	private int globalId;

	/**
	 * Lista as propostas cadastradas pelo usuário. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/portais/docente/menu_docente.jsp
	 * 
	 */
	public ActionForward minhasPropostas(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Collection<CursoLato> cursos = getGenericDAO(request).findByExactField(
				CursoLato.class, "propostaCurso.usuario.id",
				getUsuarioLogado(request).getId());
		if (cursos != null && !cursos.isEmpty()) {
			request.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, cursos);
			return mapping.findForward(CONTINUAR);
		} else {
			addMensagemErro("Você não possui propostas submetidas", request);
			return mapping.findForward(getSubSistemaCorrente(request)
					.getForward());
		}
	}

	/**
	 * Popula os atributos de sessão para serem usados em todo o caso de uso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/portais/docente/menu_docente.jsp
	 * 
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		CursoLatoForm cForm = (CursoLatoForm) form;
		if (request.getParameter("id") == null
				&& request.getParameter("renovar") == null) {
			cForm = new CursoLatoForm();
		}
		Usuario user = (Usuario) getUsuarioLogado(request);
		UnidadeDao dao = getDAO(UnidadeDao.class, request);

		AreaConhecimentoCnpqDao daoAreaConhecimento = getDAO(
				AreaConhecimentoCnpqDao.class, request);
		try {
			addSession("grandeAreasCnpq", daoAreaConhecimento
					.findGrandeAreasConhecimentoCnpq(), request);
			addSession("programasDeptos", dao.findByTipoUnidadeAcademica(
					TipoUnidadeAcademica.DEPARTAMENTO,
					TipoUnidadeAcademica.ESCOLA,
					TipoUnidadeAcademica.PROGRAMA_POS,
					TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA,
					TipoUnidadeAcademica.ORGAO_SUPLEMENTAR), request);
			addSession("tipoCursos", dao.findAll(TipoCursoLato.class), request);
			addSession("modalidadesEducacoes", dao
					.findAll(ModalidadeEducacao.class), request);
			addSession("tiposTrabalhoConclusao", dao
					.findAll(TipoTrabalhoConclusao.class), request);
			addSession("areasConhecimentoCnpq", dao.findAll(
					AreaConhecimentoCnpq.class, "nome", "asc"), request);
			addSession("tiposPublicoAlvoCurso", dao
					.findAll(TipoPublicoAlvo.class), request);
			addSession("turnos", dao.findAll(Turno.class), request);
			addSession("tiposPeriodicidadeAula", dao
					.findAll(TipoPeriodicidadeAula.class), request);
			addSession("municipios", dao.findByExactField(Municipio.class,
					"unidadeFederativa.sigla", "RN"), request);
			addSession("campi", dao.findAll(CampusIes.class), request);
			addSession("formasSelecao", dao.findAll(FormaSelecao.class),
					request);
			addSession("formasAvaliacao", dao.findAll(FormaAvaliacao.class),
					request);
			addSession("formacoes", dao.findAll(Formacao.class), request);
			addSession("instituicoes", dao.findAll(InstituicoesEnsino.class,
					"nome", "asc"), request);
		} finally {
			if (dao != null) dao.close();
		}
		prepareMovimento(SigaaListaComando.PERSISTIR_CURSO_LATO, request);

		// limpa os passos armazenados em sessão
		clearSteps(request);

		// Adiciona passos do caso de uso
		addStep(request, "Dados Básicos", "/ensino/latosensu/criarCurso",
				DADOS_GERAIS);
		addStep(request, "Proposta", "/ensino/latosensu/criarCurso", PROPOSTA);
		addStep(request, "Seleção", "/ensino/latosensu/criarCurso", SELECAO);
		addStep(request, "Recursos", "/ensino/latosensu/criarCurso", RECURSOS);
		addStep(request, "Docentes", "/ensino/latosensu/criarCurso", DOCENTES);
		addStep(request, "Turmas de Entrada", "/ensino/latosensu/criarCurso",
				TURMAS_ENTRADA);
		addStep(request, "Disciplinas", "/ensino/latosensu/criarCurso",
				DISCIPLINAS);
		addStep(request, "Coordenação do Curso",
				"/ensino/latosensu/criarCurso", COORDENACAO_CURSO);
		addStep(request, "Resumo", "/ensino/latosensu/criarCurso", RESUMO);

		addSession(OPERACAO, CADASTRAR, request);
		globalId = 1;

		// se o usuário for um docente, adiciona-o no corpo docente do curso
		if (user.getServidor() != null) {
			CorpoDocenteCursoLato cs = new CorpoDocenteCursoLato();
			cs.setCursoLato(cForm.getObj());
			cs.setServidor(user.getServidor());
			cs.setDocenteExterno(null);
			cForm.getObj().getCursosServidores().add(cs);
		}

		if (cForm.getObj().getId() == 0) {
			cForm.getObj().setSetorResponsavelCertificado(
					"Pró-Reitoria de Pós-Graduação");
			cForm.getProposta().setPercentualCoordenador(new Integer(50));
			cForm.getProposta().setPercentualInstituicao(new Integer(50));
		}
		cForm.setProposta(cForm.getObj().getPropostaCurso());
		// informa o passo atual
		setStep(request, 1);
		addSession(PASSO_ATUAL, DADOS_GERAIS, request);
		request.getSession().setAttribute(mapping.getName(), cForm);
		return mapping.findForward(DADOS_GERAIS);
	}

	/**
	 * Carrega todos os objetos necessários para alteração ou remoção. <br>
	 * <br>
	 * Método não invocado por JSP´s
	 * 
	 */
	public CursoLatoForm carregaObject(int id, HttpServletRequest req)
			throws ArqException {

		GenericDAO dao = getGenericDAO(req);
		CursoLatoForm cForm = new CursoLatoForm();
		cForm.setObj(dao.findByPrimaryKey(id, CursoLato.class));
		if (cForm.getObj() != null) {

			cForm.getObj().getCursosServidores().iterator();
			cForm.getObj().getComponentesCursoLato().iterator();
			cForm.getObj().getTurmasEntrada().iterator();
			cForm.getObj().getPublicosAlvoCurso().iterator();
			cForm.getObj().getCoordenacoesCursos().iterator();
			cForm.getObj().getPropostaCurso().getFormasSelecaoProposta()
					.iterator();
			cForm.getObj().getPropostaCurso().getFormasAvaliacaoProposta()
					.iterator();
			cForm.getObj().getPropostaCurso().getEquipesLato().iterator();

			// setando as áreas nos selects
			AreaConhecimentoCnpq area = null;
			if (cForm.getObj().getAreaConhecimentoCnpq() != null) {
				area = dao.findByPrimaryKey(Integer.parseInt(cForm.getObj()
						.getAreaConhecimentoCnpq().getCodigo()),
						AreaConhecimentoCnpq.class);
				cForm.setGrandeArea(cForm.getObj().getAreaConhecimentoCnpq()
						.getGrandeArea());
			}

			if (area != null) {
				cForm.setArea(area);
				if (cForm.getObj().getAreaConhecimentoCnpq().getSubarea() != null) {
					cForm.setSubarea(cForm.getObj().getAreaConhecimentoCnpq()
							.getSubarea());
				}
				if (cForm.getObj().getAreaConhecimentoCnpq().getEspecialidade() != null) {
					cForm.setEspecialidade(cForm.getObj()
							.getAreaConhecimentoCnpq().getEspecialidade());
				}
			}

			addSession("cursoLatoForm", cForm, req);
		}
		return cForm;

	}

	/**
	 * Carregar a lista das propostas cadastradas pelo docente logado. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/lista.jsp
	 * 
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		CursoLatoDao dao = getDAO(CursoLatoDao.class, req);
		CursoLatoForm cForm = (CursoLatoForm) form;

		getForm(form).checkRole(req);
		getForm(form).referenceData(req);
		populateRequest(req, cForm.getReferenceData());

		Collection<? extends Curso> lista = null;

		/* Popular formulário de Busca */
		Map<String, String> searchMap = cForm.getSearchMap(req);

		/* Retirar dados da busca de sessão */
		if (getForm(form).isUnregister()) {
			searchMap = null;
			getForm(form).unregisterSearchData(req);
		}

		if (searchMap != null) {
			Set<Entry<String, String>> entrySet = searchMap.entrySet();
			for (Entry<String, String> entry : entrySet)
				BeanUtils.setProperty(cForm, entry.getKey(), entry.getValue());
			req.setAttribute(mapping.getName(), cForm);
		}

		if (getPaging(req) == null) {
			req.setAttribute("pagingInformation", new PagingInformation(0));
		}

		/* Realiza buscas */
		if (isUserInRole(req, SigaaPapeis.COORDENADOR_LATO)) {
			Usuario coordenador = (Usuario) getUsuarioLogado(req);
			if (req.getParameter("renovar") == null) {
				lista = dao.findAllCoordenadoPor(coordenador.getServidor()
						.getId());
			} else {
				// TODO: chamar o método DAO trazendo somente os cursos que
				// podem ser renovados
			}
		} else if (isUserInRole(req, SigaaPapeis.GESTOR_LATO)) {
			if (req.getParameter("renovar") == null) {
				if (getForm(form).isBuscar())
					lista = cForm.customSearch(req);
			} else {
				// TODO: chamar o método DAO trazendo somente os cursos que
				// podem ser renovados
			}
		}

		if (lista != null && lista.size() == 0) {
			addMensagemErro("A busca não encontrou nenhum resultado.", req);
		}
		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, lista);
		
		cForm.clear();
		return mapping.findForward(LISTAR);
	}

	/**
	 * Continua o preenchimento da proposta (incompleta) do curso Lato Sensu. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/continuar.jsp
	 * 
	 */
	@Override
	public ActionForward edit(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		popular(mapping, form, req, res);

		Integer id = new Integer(req.getParameter("id"));
		CursoLatoForm cForm = carregaObject(id, req);

		if (cForm.getObj() == null) {
			addMensagem(new MensagemAviso(
					"Esta proposta foi excluída, reinicie a operação.",
					TipoMensagemUFRN.ERROR), req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}

		if (req.getParameter("renovar") != null) {
			zerarIds(cForm.getObj());
			addSession(OPERACAO, RENOVAR, req);
		} else
			addSession(OPERACAO, ATUALIZAR, req);

		cForm.setDataInicio(Formatador.getInstance().formatarData(
				cForm.getObj().getDataInicio()));
		cForm.setDataFim(Formatador.getInstance().formatarData(
				cForm.getObj().getDataFim()));
		cForm.setProposta(cForm.getObj().getPropostaCurso());

		for (CoordenacaoCurso cc : cForm.getObj().getCoordenacoesCursos())
			switch (cc.getCargoAcademico().getId()) {
			case CargoAcademico.COORDENACAO:
				cForm.setCoordenador(cc);
				break;
			case CargoAcademico.VICE_COORDENACAO:
				cForm.setViceCoordenador(cc);
				break;
			case CargoAcademico.SECRETARIA:
				cForm.setSecretario(cc);
				break;
			}

		prepareMovimento(SigaaListaComando.PERSISTIR_CURSO_LATO, req);
		return mapping.findForward(DADOS_GERAIS);
	}

	/**
	 * Zera os ids do curso e de suas entidades.
	 * 
	 * @param curso
	 */
	private void zerarIds(CursoLato curso) {
		curso.setId(0);

		for (CoordenacaoCurso cc : curso.getCoordenacoesCursos()) {
			cc.setId(0);
			cc.setCurso(curso);
		}
		for (PublicoAlvoCurso pac : curso.getPublicosAlvoCurso()) {
			pac.setId(0);
			pac.setCursoLato(curso);
		}
		for (CorpoDocenteCursoLato cs : curso.getCursosServidores()) {
			cs.setId(0);
			cs.setCursoLato(curso);
		}
		for (TurmaEntradaLato tel : curso.getTurmasEntrada()) {
			tel.setId(0);
			tel.setCursoLato(curso);
			tel.setDiscentesLato(new HashSet<DiscenteLato>(0));
		}
		for (ComponenteCurricular d : curso.getDisciplinas()) {
			d.setId(0);
		}
		curso.setOutrosDocumentos(new HashSet<OutrosDocumentos>(0));
		curso.setParcerias(new HashSet<ParceriaLato>(0));

		PropostaCursoLato proposta = curso.getPropostaCurso();
		proposta.setId(0);

		for (FormaSelecaoProposta fsp : proposta.getFormasSelecaoProposta()) {
			fsp.setId(0);
			fsp.setProposta(proposta);
		}
		for (FormaAvaliacaoProposta fap : proposta.getFormasAvaliacaoProposta()) {
			fap.setId(0);
			fap.setProposta(proposta);
		}
		for (CorpoDocenteDisciplinaLato el : proposta.getEquipesLato()) {
			el.setId(0);
			el.setProposta(proposta);
		}
	}

	/**
	 * Visualiza o resumo da proposta do curso Lato Sensu. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/continuar.jsp
	 * 
	 */
	@Override
	public ActionForward view(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		String param = req.getParameter("id");
		Integer id = 0;
		if (param != null) {
			id = new Integer(param);
		} else if (req.getSession().getAttribute("cursoAtual") != null) {
			id = ((Curso) req.getSession().getAttribute("cursoAtual")).getId();
		}

		CursoLatoForm cForm = carregaObject(id, req);
		if (cForm.getObj() == null) {
			addMensagem(new MensagemAviso(
					"Esta proposta foi excluída, reinicie a operação.",
					TipoMensagemUFRN.ERROR), req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		cForm.setDataInicio(Formatador.getInstance().formatarData(
				cForm.getObj().getDataInicio()));
		cForm.setDataFim(Formatador.getInstance().formatarData(
				cForm.getObj().getDataFim()));
		cForm.setProposta(cForm.getObj().getPropostaCurso());

		for (CoordenacaoCurso cc : cForm.getObj().getCoordenacoesCursos())
			switch (cc.getCargoAcademico().getId()) {
			case CargoAcademico.COORDENACAO:
				cForm.setCoordenador(cc);
				break;
			case CargoAcademico.VICE_COORDENACAO:
				cForm.setViceCoordenador(cc);
				break;
			case CargoAcademico.SECRETARIA:
				cForm.setSecretario(cc);
				break;
			}

		addSession(OPERACAO, VISUALIZAR, req);

		return mapping.findForward(RESUMO);
	}

	/**
	 * Realizar a exclusão da proposta (incompleta) do curso Lato Sensu. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/continuar.jsp
	 * 
	 */
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		Integer id = new Integer(req.getParameter("id"));
		setOperacaoAtiva(req, REMOVER_PROPOSTA);
		if (carregaObject(id, req).getObj() == null) {
			addMensagem(new MensagemAviso(
					"Esta proposta foi excluída, reinicie a operação.",
					TipoMensagemUFRN.ERROR), req);
			return mapping.findForward(getSubSistemaCorrente(req).getForward());
		}
		addMessage(req, "Confirma Remoção da Proposta?",
				TipoMensagemUFRN.WARNING);
		prepareMovimento(SigaaListaComando.REMOVER_CURSO_LATO, req);
		addSession(OPERACAO, REMOVER, req);
		return mapping.findForward(RESUMO);
	}

	/**
	 * Método que contém operações a serem realizadas logo após o passo
	 * DADOS_GERAIS, incluindo a validação
	 */
	private ActionForward afterDadosGerais(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GenericDAO dao = getGenericDAO(request);
		CursoLatoForm cForm = (CursoLatoForm) form;

		// setando a última area selecionada no form
		if (cForm.getEspecialidade().getId() != 0) {
			cForm.getObj().setAreaConhecimentoCnpq(cForm.getEspecialidade());
		} else if (cForm.getSubarea().getId() != 0) {
			cForm.getObj().setAreaConhecimentoCnpq(cForm.getSubarea());
		} else {
			cForm.getObj().setAreaConhecimentoCnpq(cForm.getArea());
		}
		dao.initialize(cForm.getObj().getAreaConhecimentoCnpq());

		if (cForm.getTiposPublicoAlvo() != null) {
			Collection<String> marcar = new HashSet<String>();
			boolean flag = false;
			Collection<PublicoAlvoCurso> publicos1 = new ArrayList<PublicoAlvoCurso>(
					cForm.getObj().getPublicosAlvoCurso());
			Collection<PublicoAlvoCurso> publicos2 = new ArrayList<PublicoAlvoCurso>(
					cForm.getObj().getPublicosAlvoCurso());

			for (String id : cForm.getTiposPublicoAlvo()) {
				for (PublicoAlvoCurso pac : publicos1) {
					if (pac.getTipoPublicoAlvo().getId() == Integer
							.parseInt(id))
						flag = true;
				}
				if (!flag) // se a flag não for ativada deve-se acrescentar o
							// público-alvo
					marcar.add(id);
				flag = false;
			}
			for (PublicoAlvoCurso pac : publicos2) {
				for (String id : cForm.getTiposPublicoAlvo()) {
					if (pac.getTipoPublicoAlvo().getId() == Integer
							.parseInt(id))
						flag = true;
				}
				if (!flag) // se a flag não for ativada deve-se remover esse
							// público-alvo
					cForm.getObj().getPublicosAlvoCurso().remove(pac);
				flag = false;
			}
			for (String id : marcar) {
				PublicoAlvoCurso pub = createPublicoAlvo(id, cForm.getObj());
				cForm.getObj().getPublicosAlvoCurso().add(pub);
			}
		}

//		CursoLatoValidator.validaDadosBasicos(cForm.getObj(), cForm,
//				newListaMensagens(request));
		if (flushErros(request))
			return mapping.findForward(DADOS_GERAIS);

		if (!cForm.getObj().isCertificado())
			cForm.getObj().setSetorResponsavelCertificado("");

		cForm.getObj().setNivel('L');

		try {
			dao.initialize(cForm.getObj().getTipoCursoLato());
		}
		finally {
				if (dao != null) dao.close();
		}

		// antecipando o preenchimento de dados de outros formulários adiante
		cForm.getTurmaEntrada().setVagas(cForm.getObj().getNumeroVagas());
		cForm.setDataInicioTurmaEntrada(cForm.getDataInicio());
		cForm.setDataFimTurmaEntrada(cForm.getDataFim());
		cForm.setDataInicioMandatoCoordenador(cForm.getDataInicio());
		cForm.setDataFimMandatoCoordenador(cForm.getDataFim());
		cForm.setDataInicioMandatoViceCoordenador(cForm.getDataInicio());
		cForm.setDataFimMandatoViceCoordenador(cForm.getDataFim());

		return null;
	}

	/**
	 * Método auxiliar que retorna uma instância de PublicoAlvoCurso a partir do
	 * id e curso passados
	 */
	private PublicoAlvoCurso createPublicoAlvo(String id, CursoLato curso) {
		PublicoAlvoCurso pub = new PublicoAlvoCurso();
		pub.setCursoLato(curso);
		pub.setTipoPublicoAlvo(new TipoPublicoAlvo(Integer.parseInt(id)));
		return pub;
	}

	/**
	 * Segue para o segundo passo do caso de uso Adicionar dados da proposta <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/dados_gerais.jsp
	 * 
	 * 
	 */
	public ActionForward proposta(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward retorno = afterDadosGerais(mapping, form, request,
				response);
		if (retorno != null)
			return retorno;

		addSession(PASSO_ATUAL, PROPOSTA, request);
		setStep(request, 2);
		return mapping.findForward(PROPOSTA);
	}

	/**
	 * Configura as informações após a tela de Proposta;
	 */
	private ActionForward afterProposta(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;

//		CursoLatoValidator.validaProposta(cForm.getProposta(), cForm,
//				newListaMensagens(request));
		if (flushErros(request))
			return mapping.findForward(PROPOSTA);

		return null;
	}

	/**
	 * Segue para o terceiro passo do caso de uso Adicionar dados do processo
	 * seletivo <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/proposta.jsp
	 * 
	 */
	public ActionForward selecao(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward retorno = afterProposta(mapping, form, request, response);
		if (retorno != null)
			return retorno;

		addSession(PASSO_ATUAL, SELECAO, request);
		setStep(request, 3);
		return mapping.findForward(SELECAO);
	}

	/**
	 * Cria a entidade responsável pelas informações do Processo Seletivo do
	 * curso
	 * 
	 * @param id
	 * @param propostaCurso
	 * @return
	 */
	private FormaSelecaoProposta createFormaSelecao(String id,
			PropostaCursoLato propostaCurso) {
		FormaSelecaoProposta fsp = new FormaSelecaoProposta();
		fsp.setProposta(propostaCurso);
		fsp.setFormaSelecao(new FormaSelecao(Integer.parseInt(id)));
		return fsp;
	}

	/**
	 * Cria a entidade responsável pelas informações do Processo de Avaliação do
	 * curso
	 * 
	 * @param id
	 * @param propostaCurso
	 * @return
	 */
	private FormaAvaliacaoProposta createFormaAvaliacao(String id,
			PropostaCursoLato propostaCurso) {
		FormaAvaliacaoProposta fap = new FormaAvaliacaoProposta();
		fap.setProposta(propostaCurso);
		fap.setFormaAvaliacao(new FormaAvaliacao(Integer.parseInt(id)));
		return fap;
	}

	/**
	 * Configura as informações após a tela de seleção.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private ActionForward afterSelecao(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;

		String operacao = (String) getFromSession(OPERACAO, request);

		if (cForm.getFormasSelecaoProposta() != null) {
			Collection<String> marcar = new HashSet<String>();
			boolean flag = false;
			for (String id : cForm.getFormasSelecaoProposta()) {
				for (FormaSelecaoProposta fsp : cForm.getObj()
						.getPropostaCurso().getFormasSelecaoProposta()) {
					if (fsp.getFormaSelecao().getId() == Integer.parseInt(id))
						flag = true;
				}
				if (!flag) // se a flag não for ativada deve-se acrescentar a
					// forma de seleção
					if (CADASTRAR.equals(operacao))
						marcar.add(id);
					else if (ATUALIZAR.equals(operacao)) {
						FormaSelecaoProposta fsp = createFormaSelecao(id, cForm
								.getObj().getPropostaCurso());
						if (cForm.getRemoveFormasSelecao().contains(fsp))
							cForm.getRemoveFormasSelecao().remove(fsp);
						marcar.add(id);
					}

				flag = false;
			}
			for (FormaSelecaoProposta fsp : cForm.getObj().getPropostaCurso()
					.getFormasSelecaoProposta()) {
				for (String id : cForm.getFormasSelecaoProposta()) {
					if (fsp.getFormaSelecao().getId() == Integer.parseInt(id))
						flag = true;
				}
				if (!flag) // se a flag não for ativada deve-se remover essa
					// forma de seleção
					if (CADASTRAR.equals(operacao))
						cForm.getObj().getPropostaCurso()
								.getFormasSelecaoProposta().remove(fsp);
					else if (ATUALIZAR.equals(operacao)) {
						cForm.getObj().getPropostaCurso()
								.getFormasSelecaoProposta().remove(fsp);
						cForm.getRemoveFormasSelecao().add(fsp);
					}
				flag = false;
			}
			for (String id : marcar) {
				FormaSelecaoProposta fsp = createFormaSelecao(id, cForm
						.getObj().getPropostaCurso());
				cForm.getObj().getPropostaCurso().getFormasSelecaoProposta()
						.add(fsp);
			}
		}

		if (cForm.getFormasAvaliacaoProposta() != null) {
			Collection<String> marcar = new HashSet<String>();
			boolean flag = false;
			for (String id : cForm.getFormasAvaliacaoProposta()) {
				for (FormaAvaliacaoProposta fap : cForm.getObj()
						.getPropostaCurso().getFormasAvaliacaoProposta()) {
					if (fap.getFormaAvaliacao().getId() == Integer.parseInt(id))
						flag = true;
				}
				if (!flag) // se a flag não for ativada deve-se acrescentar a
					// forma de seleção
					if (CADASTRAR.equals(operacao))
						marcar.add(id);
					else if (ATUALIZAR.equals(operacao)) {
						FormaAvaliacaoProposta fap = createFormaAvaliacao(id,
								cForm.getObj().getPropostaCurso());
						if (cForm.getRemoveFormasAvaliacao().contains(fap))
							cForm.getRemoveFormasAvaliacao().remove(fap);
						marcar.add(id);
					}

				flag = false;
			}
			for (FormaAvaliacaoProposta fap : cForm.getObj().getPropostaCurso()
					.getFormasAvaliacaoProposta()) {
				for (String id : cForm.getFormasAvaliacaoProposta()) {
					if (fap.getFormaAvaliacao().getId() == Integer.parseInt(id))
						flag = true;
				}
				if (!flag) // se a flag não for ativada deve-se remover essa
					// forma de seleção
					if (CADASTRAR.equals(operacao))
						cForm.getObj().getPropostaCurso()
								.getFormasAvaliacaoProposta().remove(fap);
					else if (ATUALIZAR.equals(operacao)) {
						cForm.getObj().getPropostaCurso()
								.getFormasAvaliacaoProposta().remove(fap);
						cForm.getRemoveFormasAvaliacao().add(fap);
					}
				flag = false;
			}
			for (String id : marcar) {
				FormaAvaliacaoProposta fap = createFormaAvaliacao(id, cForm
						.getObj().getPropostaCurso());
				cForm.getObj().getPropostaCurso().getFormasAvaliacaoProposta()
						.add(fap);
			}
		}

//		CursoLatoValidator.validaSelecao(cForm.getObj(), cForm.getProposta(),
//				cForm, newListaMensagens(request));
		if (flushErros(request))
			return mapping.findForward(SELECAO);

		return null;
	}

	/**
	 * Segue para o quarto passo do caso de uso, adicionar recursos para o
	 * funcionamento do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/selecao.jsp
	 * 
	 */
	public ActionForward recursos(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward retorno = afterSelecao(mapping, form, request, response);
		if (retorno != null)
			return retorno;

		addSession(PASSO_ATUAL, RECURSOS, request);
		setStep(request, 4);
		return mapping.findForward(RECURSOS);
	}

	/**
	 * Configura as informações após a tela de recursos.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private ActionForward afterRecursos(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		cForm.getObj().setPropostaCurso(cForm.getProposta());

		return null;
	}

	/**
	 * Segue para o quinto passo do caso de uso, adicionar docentes ao corpo
	 * docente do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/recursos.jsp
	 * 
	 */
	public ActionForward docentes(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		afterRecursos(mapping, form, request, response);
		cForm.setDocenteExterno(new DocenteExterno());

		for (CorpoDocenteCursoLato cs : cForm.getObj().getCursosServidores())
			if (cs.getId() == 0)
				cs.setId(globalId++);

		addSession(PASSO_ATUAL, DOCENTES, request);
		setStep(request, 5);
		return mapping.findForward(DOCENTES);
	}

	/**
	 * Configura as informações após a tela de docentes.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private ActionForward afterDocentes(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		CursoLatoForm cForm = (CursoLatoForm) form;
		Collection<CorpoDocenteCursoLato> cursoServidores = cForm.getObj()
				.getCursosServidores();
//		CursoLatoValidator.validaDocentes(cursoServidores,
//				newListaMensagens(req));
		if (flushErros(req))
			return mapping.findForward(DOCENTES);

		Collection<CorpoDocenteCursoLato> docentesInternos = new ArrayList<CorpoDocenteCursoLato>(
				cursoServidores.size());
		Collection<CorpoDocenteCursoLato> docentesExternos = new ArrayList<CorpoDocenteCursoLato>(
				cursoServidores.size());
		CorpoDocenteCursoLato docente;
		for (CorpoDocenteCursoLato cs : cursoServidores) {
			if (!cs.getExterno() && cs.getServidor() != null) {
				cs.setCursoServidorId(cs.getServidor().getId());

				docente = new CorpoDocenteCursoLato();
				docente.setServidor(cs.getServidor());
				docente.setDocenteExterno(null);
				if (cs.getId() < 100)
					docente.setId(0);
				else
					docente.setId(cs.getId());
				docente.setCursoServidorId(cs.getCursoServidorId());
				docente.setCursoLato(cs.getCursoLato());
				docentesInternos.add(docente);
			} else if (cs.getDocenteExterno() != null) {
				cs.setCursoServidorId(cs.getDocenteExterno().getId());

				docente = new CorpoDocenteCursoLato();
				docente.setServidor(null);
				docente.setDocenteExterno(cs.getDocenteExterno());
				if (cs.getId() < 100)
					docente.setId(0);
				else
					docente.setId(cs.getId());
				docente.setCursoServidorId(cs.getCursoServidorId());
				docente.setCursoLato(cs.getCursoLato());
				docentesExternos.add(docente);
			}
		}

		addSession("docentesInternos", docentesInternos, req);
		addSession("docentesExternos", docentesExternos, req);

		return null;
	}

	/**
	 * Segue para o sexto passo do caso de uso Adicionar turmas de entrada. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/docentes.jsp
	 * 
	 */
	public ActionForward turmasEntrada(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward retorno = afterDocentes(mapping, form, request, response);
		if (retorno != null)
			return retorno;

		addSession(PASSO_ATUAL, TURMAS_ENTRADA, request);
		setStep(request, 6);
		return mapping.findForward(TURMAS_ENTRADA);
	}

	/**
	 * Configura as informações após a tela de turma de entrada.
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private ActionForward afterTurmasEntrada(ActionMapping mapping,
			ActionForm form, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		CursoLatoForm cForm = (CursoLatoForm) form;

//		CursoLatoValidator.validaTurmasEntrada(cForm.getObj()
//				.getTurmasEntrada(), newListaMensagens(req));
		if (flushErros(req))
			return mapping.findForward(TURMAS_ENTRADA);

		cForm.setDocentesDisciplina(getDAO(EquipeLatoDao.class, req)
				.findByCurso(cForm.getObj().getId()));
		return null;
	}

	/**
	 * Segue para o sétimo passo do caso de uso - Adicionar disciplinas ao
	 * curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/turmas_entrada.jsp
	 * 
	 */
	public ActionForward disciplinas(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward retorno = afterTurmasEntrada(mapping, form, request,
				response);
		if (retorno != null)
			return retorno;

		addSession(PASSO_ATUAL, DISCIPLINAS, request);
		setStep(request, 7);
		return mapping.findForward(DISCIPLINAS);
	}

	/**
	 * Configura as informações após a tela de disciplinas. <br>
	 * <br>
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private ActionForward afterDisciplinas(ActionMapping mapping,
			ActionForm form, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		CursoLatoForm cForm = (CursoLatoForm) form;

		cForm.setDocenteExterno(new DocenteExterno());

//		CursoLatoValidator.validaDisciplinas(cForm.getObj().getDisciplinas(),
//				newListaMensagens(req));
		if (flushErros(req))
			return mapping.findForward(DISCIPLINAS);

		return null;
	}

	/**
	 * Segue para o oitavo passo do caso de uso - Definir coordenação do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/disciplinas.jsp
	 * 
	 */
	@SuppressWarnings("unchecked")
	// - SuppressWarnings("unchecked") permitido pela especificação da SINFO.
	public ActionForward coordenacaoCurso(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ActionForward retorno = afterDisciplinas(mapping, form, request,
				response);
		if (retorno != null)
			return retorno;

		// Docentes que podem assumir a vice-coordenação do curso
		Collection<CorpoDocenteCursoLato> docentesViceCoordenacao = new HashSet<CorpoDocenteCursoLato>();

		Collection<CorpoDocenteCursoLato> docentesInternos = (Collection<CorpoDocenteCursoLato>) getFromSession(
				"docentesInternos", request);
		Collection<CorpoDocenteCursoLato> docentesExternos = (Collection<CorpoDocenteCursoLato>) getFromSession(
				"docentesExternos", request);

		docentesViceCoordenacao.addAll(docentesInternos);
		for (Object o : docentesExternos) {
			CorpoDocenteCursoLato cs = (CorpoDocenteCursoLato) o;
			if (cs.getDocenteExterno().getInstituicao().getId() == 96
					&& cs.getDocenteExterno().getServidor() != null){
				cs.setServidor(cs.getDocenteExterno().getServidor());
				docentesViceCoordenacao.add(cs);
			}
		}

		addSession("docentesViceCoordenacao", docentesViceCoordenacao, request);

		addSession(PASSO_ATUAL, COORDENACAO_CURSO, request);
		setStep(request, 8);
		return mapping.findForward(COORDENACAO_CURSO);
	}

	/**
	 * Configura as informações após a tela de coordenação do curso
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	private ActionForward afterCoordenacaoCurso(ActionMapping mapping,
			ActionForm form, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		CursoLatoForm cForm = (CursoLatoForm) form;
		GenericDAO dao = getGenericDAO(req);

//		CursoLatoValidator
//				.validaCoordenacaoCurso(cForm, newListaMensagens(req));
		if (flushErros(req))
			return mapping.findForward(COORDENACAO_CURSO);

		// preenche dados restantes do coordenador e adiciona ao curso
		cForm.getCoordenador().setAtivo(true);
		cForm.getCoordenador().setServidor(
				dao.findByPrimaryKey(cForm.getCoordenador().getServidor()
						.getId(), Servidor.class));
		cForm.getCoordenador().setCurso(cForm.getObj());
		cForm.getCoordenador().setCargoAcademico(
				dao.findByPrimaryKey(CargoAcademico.COORDENACAO,
						CargoAcademico.class));
		cForm.getObj().getCoordenacoesCursos().add(cForm.getCoordenador());

		// preenche dados restantes do vice-coordenador e adiciona ao curso
		cForm.getViceCoordenador().setAtivo(true);
		cForm.getViceCoordenador().setServidor(
				dao.findByPrimaryKey(cForm.getViceCoordenador().getServidor()
						.getId(), Servidor.class));
		cForm.getViceCoordenador().setCurso(cForm.getObj());
		cForm.getViceCoordenador().setCargoAcademico(
				dao.findByPrimaryKey(CargoAcademico.VICE_COORDENACAO,
						CargoAcademico.class));
		cForm.getObj().getCoordenacoesCursos().add(cForm.getViceCoordenador());

		// preenche dados restantes do secretário e adiciona ao curso
		if (cForm.getSecretario().getServidor().getId() > 0) {
			cForm.getSecretario().setCurso(cForm.getObj());
			cForm.getSecretario().setCargoAcademico(
					dao.findByPrimaryKey(CargoAcademico.SECRETARIA,
							CargoAcademico.class));
			cForm.getSecretario().setDataInicioMandato(
					cForm.getObj().getDataInicio());
			cForm.getSecretario()
					.setDataFimMandato(cForm.getObj().getDataFim());
			cForm.getObj().getCoordenacoesCursos().add(cForm.getSecretario());
		}

		return null;
	}

	/**
	 * Carrega o resumo das informações da proposta do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/coordenacao_curso.jsp
	 * 
	 */
	public ActionForward resumo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionForward retorno = afterCoordenacaoCurso(mapping, form, request,
				response);
		if (retorno != null)
			return retorno;

		addMensagem(
				new MensagemAviso(
						"Confira os dados da proposta antes de submetê-la. "
								+ "Após a submissão, não será mais permitido fazer alterações, apenas mediante "
								+ "autorização da Pró-Reitoria de Pós-Graduação.",
						TipoMensagemUFRN.WARNING), request);
		addSession(PASSO_ATUAL, RESUMO, request);
		setStep(request, 9);
		return mapping.findForward(RESUMO);

	}

	/**
	 * Adiciona um docente ao corpo docente do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/docentes.jsp
	 * 
	 */
	public ActionForward adicionarCursoServidor(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
//		CursoLatoValidator.validaDocenteInserido(cForm.getCursoServidor(),
//				newListaMensagens(request));
		if (!flushErros(request)) {
			GenericDAO dao = getGenericDAO(request);
			CorpoDocenteCursoLato cServidor = cForm.getCursoServidor();
			cServidor.setCursoLato(cForm.getObj());
			try {
				if ("externo".equalsIgnoreCase(request
						.getParameter("tipoAjaxDocente"))) {
					cServidor.setDocenteExterno(dao.findByPrimaryKey(cServidor
							.getServidor().getId(), DocenteExterno.class));
					if (cServidor.getDocenteExterno() == null) {
						addMensagemErro(
								"O docente externo não existe no sistema. Verifique se é um docente da Instituição"
										+ " sendo adicionado como externo.",
								request);
						return mapping.findForward(DOCENTES);
					}
					cServidor.setServidor(null);
				} else {
					cServidor.setServidor(dao.findByPrimaryKey(cServidor
							.getServidor().getId(), Servidor.class));
					cServidor.setDocenteExterno(null);
				}
			} finally {
				if (dao != null) dao.close();
			}

			if (!cForm.getObj().getCursosServidores().add(cServidor)) {
				addMensagemErro("Docente não pode ser adicionado.<br>"
						+ "Ele provavelmente já foi adicionado.", request);
				return mapping.findForward(DOCENTES);
			}
			addMensagem(new MensagemAviso("Docente adicionado com sucesso.",
					TipoMensagemUFRN.INFORMATION), request);

			cForm.setCursoServidor(new CorpoDocenteCursoLato());
		}

		return mapping.findForward(DOCENTES);
	}

	/**
	 * Remove um docente do corpo docente do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/docentes.jsp
	 * 
	 */
	public ActionForward removerCursoServidor(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		CorpoDocenteCursoLato cServidor = cForm.getCursoServidor();
		Servidor serv = new Servidor(RequestUtils.getIntParameter(request,"docenteId"));
		cServidor.setServidor(serv);
		cServidor.setId( RequestUtils.getIntParameter(request,"id") );
		cServidor.setDocenteExterno(null);		

		CursoLatoDao dao = getDAO(CursoLatoDao.class, request);
		boolean docenteAssociado = 
			dao.lecionaDisciplina( cServidor.getServidor().getId(),cForm.getProposta().getId() );
		if (docenteAssociado) {
			addMensagem(new MensagemAviso("Docente associado a uma disciplina.", TipoMensagemUFRN.ERROR), request);
			return mapping.findForward(DOCENTES);
		}
		
		if( cServidor.getId() != 0 )
			cForm.getRemoveCursosServidores().add( cServidor );

		if (cForm.getObj().removeCursoServidor( cServidor ))
			addMensagem(new MensagemAviso("Docente removido com sucesso.", TipoMensagemUFRN.INFORMATION), request);

		return mapping.findForward(DOCENTES);
	}

	/**
	 * Adiciona um docente externo ao corpo docente do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/docentes.jsp
	 * 
	 */
	public ActionForward adicionarCursoServidorExterno(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		ServidorDao dao = getDAO(ServidorDao.class, request);

		if (cForm.isTecnico())
			cForm.getDocenteExterno().setInstituicao(
					dao.findByPrimaryKey(InstituicoesEnsino.UFRN,
							InstituicoesEnsino.class));

//		CursoLatoValidator.validaDocenteExternoInserido(cForm
//				.getDocenteExterno(), cForm, newListaMensagens(request));
		if (!flushErros(request)) {

			if (!cForm.isTecnico())
				dao.initialize(cForm.getDocenteExterno().getInstituicao());
			else {
				Servidor servidor = dao.findByCpf(UFRNUtils.parseCpfCnpj(cForm
						.getCpf()));
				if (servidor == null) {
					addMensagemErro(
							"O CPF informado não é de um Servidor Técnico da Instituição.",
							request);
					return mapping.findForward(DOCENTES);
				} else {
					cForm.getDocenteExterno().setServidor(servidor);
				}
			}
			// verificando e carregando se já existe pessoa cadastrada no banco
			// com o cpf/passaporte informado
			PessoaDao daoPessoa = getDAO(PessoaDao.class, request);
			if (!cForm.isEstrangeiro()) {
				int idPessoa = daoPessoa.findIdByCpf(cForm.getDocenteExterno()
						.getPessoa().getCpf_cnpj());
				cForm.getDocenteExterno().getPessoa().setId(idPessoa);
			} else {
				Pessoa p = daoPessoa.findByPassaporte(cForm.getDocenteExterno()
						.getPessoa().getPassaporte());
				if (p != null && p.getId() > 0)
					cForm.getDocenteExterno().getPessoa().setId(p.getId());
			}

			// definindo o tipo de docente externo (utilizado na geração da
			// matrícula do docente)
			cForm.getDocenteExterno().setTipoDocenteExterno(
					new TipoDocenteExterno(
							TipoDocenteExterno.DOCENTE_EXTERNO_LATO_SENSU));

			// definido a unidade do docente externo como a mesma do curso lato
			cForm.getDocenteExterno().setUnidade(cForm.getObj().getUnidade());

			// estipulando o prazo de validade do acesso do docente externo para
			// 2 meses após o fim do curso
			Calendar c = Calendar.getInstance();
			c.setTime(cForm.getObj().getDataFim());
			c.add(Calendar.MONTH, 2);
			cForm.getDocenteExterno().setPrazoValidade(c.getTime());
			dao.initialize(cForm.getDocenteExterno().getFormacao());

			if (cForm.getDocenteExterno().getPessoa().getId() == 0) {
				MovimentoCadastro movDocExt = new MovimentoCadastro();
				movDocExt.setObjMovimentado(cForm.getDocenteExterno());
				movDocExt.setUsuarioLogado(getUsuarioLogado(request));
				movDocExt.setSistema(getSistema(request));
				movDocExt
						.setCodMovimento(SigaaListaComando.CADASTRAR_DOCENTE_EXTERNO);
				execute(movDocExt, request);
			} else
				dao.create(cForm.getDocenteExterno());

			cForm.getCursoServidor().setDocenteExterno(
					cForm.getDocenteExterno());
			cForm.getCursoServidor().setCursoLato(cForm.getObj());
			cForm.getCursoServidor().setId(globalId++);
			cForm.getCursoServidor().setServidor(null);
			cForm.getObj().getCursosServidores().add(cForm.getCursoServidor());

			cForm.setCursoServidor(new CorpoDocenteCursoLato());
			cForm.setDocenteExterno(new DocenteExterno());
			addMensagem(new MensagemAviso(
					"Docente Externo adicionado com sucesso.",
					TipoMensagemUFRN.INFORMATION), request);
		}

		return mapping.findForward(DOCENTES);
	}

	/**
	 * Remove um docente externo do corpo docente do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/docentes.jsp
	 * 
	 */
	public ActionForward removerCursoServidorExterno(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		CorpoDocenteCursoLato cServidor = cForm.getCursoServidor();
		cServidor.setServidor(null);
		cServidor.setId(RequestUtils.getIntParameter(request, "id"));
		cServidor.setDocenteExterno(new DocenteExterno(RequestUtils
				.getIntParameter(request, "docenteExternoId")));
		
		if (cServidor.getId() != 0 && cServidor.getId() > 1000)
			cForm.getRemoveCursosServidores().add(cServidor);

		if (cForm.getObj().removeCursoServidor(cServidor))
			addMensagem(new MensagemAviso(
					"Docente Externo removido com sucesso.",
					TipoMensagemUFRN.INFORMATION), request);

		cForm.setCursoServidor(new CorpoDocenteCursoLato());
		return mapping.findForward(DOCENTES);
	}

	/**
	 * Adiciona uma turma de entrada ao curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/turmas_entrada.jsp
	 * 
	 */
	public ActionForward adicionarTurmaEntrada(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
//		CursoLatoValidator.validaTurmaEntradaInserida(cForm.getTurmaEntrada(),
//				cForm, newListaMensagens(request));
		if (!flushErros(request)) {
			cForm.getTurmaEntrada().setCursoLato(cForm.getObj());
			GenericDAO dao = getGenericDAO(request);
			try{
				cForm.getTurmaEntrada().setMunicipio(
						dao.findByPrimaryKey(cForm.getTurmaEntrada().getMunicipio()
								.getId(), Municipio.class));
			} finally {
				if (dao != null) dao.close();
			}

			if (!cForm.getObj().getTurmasEntrada().add(cForm.getTurmaEntrada())) {
				addMensagemErro(
						"Turma Entrada não pode ser adicionada.<br>"
								+ "Provavelmente você já adicionou uma Turma Entrada com esse código no mesmo município.",
						request);
				return mapping.findForward(TURMAS_ENTRADA);
			}
			addMensagem(new MensagemAviso(
					"Turma Entrada adicionada com sucesso.",
					TipoMensagemUFRN.INFORMATION), request);

			cForm.setTurmaEntrada(new TurmaEntradaLato());
			cForm.setDataInicioTurmaEntrada("");
			cForm.setDataFimTurmaEntrada("");
		}

		return mapping.findForward(TURMAS_ENTRADA);
	}

	/**
	 * Remove uma turma de entrada do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/turmas_entrada.jsp
	 * 
	 */
	public ActionForward removerTurmaEntrada(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		TurmaEntradaLato tEntrada = cForm.getTurmaEntrada();
		tEntrada.setId(RequestUtils.getIntParameter(request, "id"));
		tEntrada.setCodigo(request.getParameter("codigo"));
		tEntrada.getMunicipio().setId(
				RequestUtils.getIntParameter(request, "municipioId"));

		cForm.getObj().removeTurmaEntrada(tEntrada);
		addMensagem(new MensagemAviso("Turma Entrada removida com sucesso.",
				TipoMensagemUFRN.INFORMATION), request);

		return mapping.findForward(TURMAS_ENTRADA);
	}

	/**
	 * Adiciona uma disciplina ao curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/disciplinas.jsp
	 * 
	 */
	public ActionForward adicionarDisciplina(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		GenericDAO dao = getGenericDAO(request);
		boolean existente;

		if (cForm.getDisciplinaAjax().getId() != 0) { // disciplina já existente

			existente = true;
			// carrega disciplina do banco
			cForm.setDisciplinaAjax(dao.findByPrimaryKey(cForm
					.getDisciplinaAjax().getId(), ComponenteCurricular.class));
			// valida a disciplina a ser inserida
//			CursoLatoValidator.validaDisciplinaInserida(cForm
//					.getDisciplinaAjax(), cForm, newListaMensagens(request));

		} else { // nova disciplina

			existente = false;
			// preenche dados restantes da disciplina
			cForm.getDisciplina().setUnidade(cForm.getObj().getUnidade());
			cForm.getDisciplina().setNumUnidades(1);
			cForm.getDisciplina().getTipoComponente().setId(
					TipoComponenteCurricular.DISCIPLINA);
			cForm.getDisciplina().setPrograma(null);
			cForm.getDisciplina().setTipoAtividade(null);
			cForm.getDisciplina().setFormaParticipacao(null);
			cForm.getDisciplina().setTipoAtividadeComplementar(null);
			cForm.getDisciplina().getDetalhes().setComponente(
					cForm.getDisciplina().getId());
			cForm.getDisciplina().getDetalhes().setChTotal(
					cForm.getDisciplina().getDetalhes().getChAula()
							+ cForm.getDisciplina().getDetalhes()
									.getChEstagio()
							+ cForm.getDisciplina().getDetalhes()
									.getChLaboratorio());
			cForm.getDisciplina().getDetalhes().setTipoComponente(
					TipoComponenteCurricular.DISCIPLINA);
			cForm.getDisciplina().setNivel('L');
			// valida a disciplina a ser inserida
//			CursoLatoValidator.validaDisciplinaInserida(cForm.getDisciplina(),
//					cForm, newListaMensagens(request));
		}

		if (!flushErros(request)) {
			ComponenteCursoLato ccl = new ComponenteCursoLato();
			ccl.setId(globalId++);
			if (!existente) {

				ccl.setDisciplina(cForm.getDisciplina());

			} else {

				ccl.setDisciplina(cForm.getDisciplinaAjax());

			}

			for (CorpoDocenteDisciplinaLato el : cForm.getEquipesLato())
				ccl.addDocentesNome(((el.getDocente() != null && el
						.getDocente().getId() != 0) ? el.getDocente()
						.getPessoa().getNome() : el.getDocenteExterno()
						.getPessoa().getNome())
						+ "</td><td>Carga Horária:</td><td>"
						+ el.getCargaHoraria() + "h</td>");

			if (cForm.getObj().addComponenteCursoLato(ccl))
				addMensagem(new MensagemAviso(
						"Disciplina adicionada com sucesso.",
						TipoMensagemUFRN.INFORMATION), request);
			else
				addMensagem(new MensagemAviso(
						"Disciplina não pode ser adicionada.",
						TipoMensagemUFRN.WARNING), request);

			// zerando os ids dos equipes lato do form
			for (CorpoDocenteDisciplinaLato el : cForm.getEquipesLato())
				el.setId(0);
			// adicionando equipes lato na proposta
			cForm.getObj().getPropostaCurso().getEquipesLato().addAll(
					cForm.getEquipesLato());

			cForm.setEquipesLato(new HashSet<CorpoDocenteDisciplinaLato>());
			cForm.setDisciplina(new ComponenteCurricular());
			cForm.setDisciplinaAjax(new ComponenteCurricular());
		}
		return mapping.findForward(DISCIPLINAS);
	}

	/**
	 * Remove uma disciplina do curso. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/disciplinas.jsp
	 * 
	 */
	public ActionForward removerDisciplina(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;

		int id = RequestUtils.getIntParameter(request, "id");
		ComponenteCursoLato ccl = new ComponenteCursoLato(id);

		if (id > 1000)
			cForm.getRemoveComponentesCursoLato().add(ccl);

		if (cForm.getObj().removeComponenteCursoLato(ccl))
			addMensagem(new MensagemAviso("Disciplina removida com sucesso.",
					TipoMensagemUFRN.INFORMATION), request);

		return mapping.findForward(DISCIPLINAS);
	}

	/**
	 * Adiciona um docente a uma disciplina. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/disciplinas.jsp
	 * 
	 */
	public ActionForward adicionarDocenteDisciplina(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		GenericDAO dao = getGenericDAO(request);
		CursoLatoForm cForm = (CursoLatoForm) form;

		if (cForm.getDisciplinaAjax() != null
				&& cForm.getDisciplinaAjax().getId() > 0) {
			cForm.setDisciplina(dao.findByPrimaryKey(cForm.getDisciplinaAjax()
					.getId(), ComponenteCurricular.class));
		}

		cForm.getDisciplina().getDetalhes().calcularCHTotal();
		cForm.getEquipeLato().setDisciplina(cForm.getDisciplina());
		cForm.getEquipeLato().setProposta(cForm.getProposta());

//		CursoLatoValidator.validaDocenteDisciplinaInserido(cForm, cForm
//				.getEquipeLato(), newListaMensagens(request));
		if (!flushErros(request)) {
			try {
				if (cForm.isInterno()) {
					cForm.getEquipeLato().setDocenteExterno(null);
					dao.initialize(cForm.getEquipeLato().getDocente());
				} else {
					cForm.getEquipeLato().setDocente(null);
					cForm.getEquipeLato().setDocenteExterno(
							dao.findByPrimaryKey(cForm.getEquipeLato()
									.getDocenteExterno().getId(),
									DocenteExterno.class));
				}
			} finally {
				if (dao != null) dao.close();
			}
			cForm.getEquipeLato().setId(globalId++);
			cForm.getEquipesLato().add(cForm.getEquipeLato());

			cForm.setEquipeLato(new CorpoDocenteDisciplinaLato());
			addMensagem(new MensagemAviso(
					"Docente adicionado a disciplina com Sucesso!",
					TipoMensagemUFRN.INFORMATION), request);
		}
		return mapping.findForward(DISCIPLINAS);
	}

	/**
	 * Remove um docente da disciplina. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/disciplinas.jsp
	 * 
	 */
	public ActionForward removerDocenteDisciplina(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		int id = RequestUtils.getIntParameter(request, "id");
		cForm.getEquipesLato().remove(new CorpoDocenteDisciplinaLato(id));
		addMensagem(new MensagemAviso(
				"Docente removido da disciplina com Sucesso!",
				TipoMensagemUFRN.INFORMATION), request);
		return mapping.findForward(DISCIPLINAS);
	}

	/**
	 * Invoca o processador para realizar o cadastro da proposta. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/resumo.jsp
	 * 
	 */
	public ActionForward chamaModelo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		boolean andamento = false;
		if (getFromSession("andamento", request) != null) {
//			CursoLatoValidator.validaResumo(cForm.getObj().getPropostaCurso(),
//					newListaMensagens(request));
			if (flushErros(request))
				return mapping.findForward(RESUMO);
			andamento = true;
		}

		String operacao = (String) getFromSession(OPERACAO, request);
		if (operacao == null) {
			addMensagem(
					new MensagemAviso(
							"A operação não está mais ativa. Por favor reinicie a operação.",
							TipoMensagemUFRN.ERROR), request);
			return mapping.findForward(getSubSistemaCorrente(request)
					.getForward());
		}
		for (CorpoDocenteCursoLato cs : cForm.getObj().getCursosServidores()) {
			if (cs.getId() < 100)
				cs.setId(0);
		}

		// zerando os ids das novas disciplinas adicionadas
		for (ComponenteCursoLato ccl : cForm.getObj().getComponentesCursoLato()) {
			if (ccl.getId() < 500)
				ccl.setId(0);
		}

		CursoLatoMov mov = new CursoLatoMov();
		mov.setObjMovimentado(cForm.getObj());
		mov.setCompleta(true);
		mov.setAndamento(andamento);

		GenericDAO dao = getGenericDAO(request);
		if (CADASTRAR.equals(operacao) || ATUALIZAR.equals(operacao)) {
			cForm.getObj().getPropostaCurso().setSituacaoProposta(
					dao.findByPrimaryKey(SituacaoProposta.SUBMETIDA,
							SituacaoProposta.class));
			mov.setCodMovimento(SigaaListaComando.PERSISTIR_CURSO_LATO);

		} else if (REMOVER.equals(operacao)) {
			if (!checkOperacaoAtiva(request, response, REMOVER_PROPOSTA)) {
				if (flushErros(request))
					return mapping.findForward(getSubSistemaCorrente(request)
							.getForward());
			}
			mov.setCodMovimento(SigaaListaComando.REMOVER_CURSO_LATO);
		}
		mov.setRemovePublicosAlvo(cForm.getRemovePublicosAlvo());
		mov.setRemoveFormasAvaliacao(cForm.getRemoveFormasAvaliacao());
		mov.setRemoveFormasSelecao(cForm.getRemoveFormasSelecao());
		mov.setRemoveCursosServidores(cForm.getRemoveCursosServidores());
		mov
				.setRemoveComponentesCursoLato(cForm
						.getRemoveComponentesCursoLato());

		dao.detach(cForm.getObj());
		try {
			execute(mov, request);
		} catch (NegocioException e) {
			addMensagem(new MensagemAviso(e.getMessage(),
					TipoMensagemUFRN.ERROR), request);
			return mapping.findForward(getSubSistemaCorrente(request)
					.getForward());
		}

		if (!REMOVER.equals(operacao))
			prepareMovimento(mov.getCodMovimento(), request);

		if (CADASTRAR.equals(operacao) || ATUALIZAR.equals(operacao)) {
			addInformation(
					"Proposta de Criação de Curso Submetida com Sucesso!",
					request);
		} else if (REMOVER.equals(operacao)) {
			addInformation(
					"Proposta de Criação de Curso Removida com Sucesso!",
					request);
			removeOperacaoAtiva(request);
		}

		return mapping.findForward(getSubSistemaCorrente(request).getForward());
	}

	/**
	 * Chama o processador e grava a proposta (incompleta). <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/dados_gerais.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/proposta.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/selecao.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/recursos.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/docentes.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/turmas_entrada.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/disciplinas.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/coordenacao_curso.jsp</li>
	 * </ul>
	 * 
	 */
	public ActionForward gravar(ActionMapping mapping, ActionForm form,
			HttpServletRequest req, HttpServletResponse res) throws Exception {

		CursoLatoForm cForm = (CursoLatoForm) form;
		String passoAtual = (String) getFromSession(PASSO_ATUAL, req);

		ActionForward retorno = null;
		switch (getCurrentStep(req)) {
		case 1:
			retorno = afterDadosGerais(mapping, form, req, res);
			break;
		case 2:
			retorno = afterProposta(mapping, form, req, res);
			break;
		case 3:
			retorno = afterSelecao(mapping, form, req, res);
			break;
		case 5:
			retorno = afterDocentes(mapping, form, req, res);
			break;
		case 6:
			retorno = afterTurmasEntrada(mapping, form, req, res);
			break;
		case 7:
			retorno = afterDisciplinas(mapping, form, req, res);
			break;
		case 8:
			retorno = afterCoordenacaoCurso(mapping, form, req, res);
			break;
		}
		if (retorno != null)
			return retorno;

		cForm.getObj().getPropostaCurso().getSituacaoProposta().setId(
				SituacaoProposta.INCOMPLETA);
		for (CorpoDocenteCursoLato cs : cForm.getObj().getCursosServidores()) {
			if (cs.getId() < 100)
				cs.setId(0);
		}
		// zerando os ids das novas disciplinas adicionadas
		for (ComponenteCursoLato ccl : cForm.getObj().getComponentesCursoLato()) {
			if (ccl.getId() < 500)
				ccl.setId(0);
		}
		try {
			CursoLatoMov mov = new CursoLatoMov(cForm.getObj(),
					SigaaListaComando.PERSISTIR_CURSO_LATO);
			mov.setCompleta(false);
			mov.setRemovePublicosAlvo(cForm.getRemovePublicosAlvo());
			mov.setRemoveFormasAvaliacao(cForm.getRemoveFormasAvaliacao());
			mov.setRemoveFormasSelecao(cForm.getRemoveFormasSelecao());
			mov.setRemoveCursosServidores(cForm.getRemoveCursosServidores());
			mov.setRemoveComponentesCursoLato(cForm
					.getRemoveComponentesCursoLato());

			execute(mov, req);
			addInformation("Proposta de Criação de Curso Gravada com Sucesso",
					req);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), req);
		} catch (DAOException e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();

			// Tratar problema de sessão suja
			if (e.getCause() != null
					&& stacktrace.indexOf("org.hibernate.StaleStateException") != -1) {
				addMensagemErro(
						"Atenção! Você já salvou esta proposta de criação de curso. "
								+ "Caso deseje editá-la, reinicie o processo.",
						req);
				return cancelar(mapping, form, req, res);
			} else {
				throw e;
			}
		}
		prepareMovimento(SigaaListaComando.PERSISTIR_CURSO_LATO, req);
		forceCloseConnection(req);
		return mapping.findForward(passoAtual);
	}

	/**
	 * Cancela a ação e sai do caso de uso <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/dados_gerais.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/proposta.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/selecao.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/recursos.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/docentes.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/turmas_entrada.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/disciplinas.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/coordenacao_curso.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/resumo.jsp</li>
	 * </ul>
	 * 
	 */
	public ActionForward cancelar(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String operacao = (String) getFromSession(OPERACAO, request);
		clearSession(request);
		removeSession("cursoLatoForm", request);
		clearSteps(request);
		if (REMOVER.equals(operacao) || VISUALIZAR.equals(operacao))
			removeOperacaoAtiva(request);
		return mapping.findForward(getSubSistemaCorrente(request).getForward());

	}

	/**
	 * Mostra o resumo da proposta para impressão. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * sigaa.war/WEB-INF/jsp/ensino/latosensu/CursoLato/resumo.jsp
	 * 
	 * @param mapping
	 * @param form
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public ActionForward resumoImpressao(ActionMapping mapping,
			ActionForm form, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		view(mapping, form, req, res);
		return mapping.findForward(RESUMO_IMPRESSAO);
	}

}
