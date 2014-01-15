/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/06/2007
 *
 */
package br.ufrn.sigaa.ensino.struts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.GenericTipo;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.TurmaEntradaLatoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.arq.struts.AbstractWizardAction;
import br.ufrn.sigaa.arq.struts.ConstantesCadastro;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeAluno;
import br.ufrn.sigaa.ensino.form.DiscenteForm;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.ensino.negocio.DiscenteValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.ensino.tecnico.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.form.PessoaForm;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoProcedenciaAluno;

/**
 *
 * Ação generalizada para os discentes. Para cada tipo de discente deve: 
 * - mapear a sua classe ao subsistema - carregar as listas dos discentes para SELECTS 
 * - setar atributos default do formulário para cada tipo de discente 
 * - implementar a submissão de dados específicos de cada tipo de discente
 *
 * @author Andre M Dantas
 *
 */
public class DiscenteAction extends AbstractWizardAction {

	/** Constante referente aos dados pessoais, 1 passo. */
	public static final String DADOS_PESSOAIS = "dadosPessoais"; 

	/** Constante referente aos dados do discente, 2 passo. */
	public static final String DADOS_DISCENTE = "dadosDiscente";
	
	/** Constante referente ao resumo das informações, 3 passo. */
	public static final String RESUMO = "resumo"; // passo 3
	
	/** São constantes que redireciona os dados do dicente Lato para um contexto JSF  */
	public static final String DADOS_DISCENTE_LATO = "dadosDiscenteLato";
	/** São constantes que redireciona os dados do dicente Tecnico para um contexto JSF  */
	public static final String DADOS_DISCENTE_TECNICO = "dadosDiscenteTecnico";

	/** Realizar a operação de atualizar */
	public static final String ATUALIZAR = "Atualizar";
	/** Realizar a operação de cadastrar */
	public static final String CADASTRAR = "Cadastrar";
	/** Realizar a operação de remoção */
	public static final String REMOVER = "Remover";
	/** Guarda a operação a ser realizada */
	public static final String OPERACAO = "operacao";

	/** Mapeamento que representa as classes de domínio dos discentes de nível lato, médio, infantil e técnico. */
	//Suppress necessário, por que não há como se corrigir esse warning
	@SuppressWarnings("unchecked")
	public static Map<SubSistema, Class> subClasses = new HashMap<SubSistema, Class>();

	/**
	 * Cada tipo de discente deve ter sua classe e seus comandos mapeados aqui.
	 */
	static {
		// mapeando sub-classes de ensino
		subClasses.put(SigaaSubsistemas.TECNICO, DiscenteTecnico.class);
		subClasses.put(SigaaSubsistemas.FORMACAO_COMPLEMENTAR, DiscenteTecnico.class);
		subClasses.put(SigaaSubsistemas.MEDIO, DiscenteTecnico.class);
		subClasses.put(SigaaSubsistemas.INFANTIL, DiscenteTecnico.class);
		subClasses.put(SigaaSubsistemas.PORTAL_COORDENADOR_LATO, DiscenteLato.class);
		subClasses.put(SigaaSubsistemas.LATO_SENSU, DiscenteLato.class);
	}

	/**
	 * Popula selects a serem usados nas JSPs. O método é usado tanto para
	 * visualização do formulário para cadastro como para atualização ->>> Cada
	 * tipo de discente deve ter seus SELECTS específicos populados aqui.
	 */
	private void popularSelects(HttpServletRequest request) throws ArqException {
		SubSistema subSistema = getSubSistemaCorrente(request);
		CursoDao dao = getDAO(CursoDao.class, request);
		TurmaEntradaLatoDao daoTurmaEntrada = getDAO(TurmaEntradaLatoDao.class, request);
		try {
			/** carregando selects de dados_discente */
			if (subSistema.equals(SigaaSubsistemas.TECNICO) 
				|| subSistema.equals(SigaaSubsistemas.MEDIO)
				|| subSistema.equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)) {
				addSession("formasIngresso", dao.findAll(FormaIngresso.class, "descricao", "asc"), request);
				addSession("tiposRegimeAluno", dao.findAll(TipoRegimeAluno.class), request);
				addSession("curriculos", new ArrayList<Object>(0), request);
				addSession("turmasEntrada", new ArrayList<Object>(0), request);
				addSession("cursosTecnicos", dao.findAll(getUnidadeGestora(request), getNivelEnsino(request),
						CursoTecnico.class, null), request);
				if(request.getSession().getAttribute("discenteAntigo") != null)
					addSession("statusDiscente", StatusDiscente.getStatusTodos(), request);
			} else if (subSistema.equals(SigaaSubsistemas.LATO_SENSU) || subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) {
				Usuario user = (Usuario) getUsuarioLogado(request);
				if (user.isCoordenadorLato()) {
					addSession("turmasEntrada", daoTurmaEntrada.findByCursoLato(user.getCursoLato().getId(), true), request);
				} else {
					addSession("cursosLato", dao.findAll(CursoLato.class, "nome", "asc"), request);
					addSession("turmasEntrada", new ArrayList<Object>(0), request);
				}
				addSession("tiposProcedenciaAluno", dao.findAll(TipoProcedenciaAluno.class), request);
				addSession("formasIngresso", dao.findAll(FormaIngresso.class), request);
			} else {
			}
		} finally {
			dao.close();
			daoTurmaEntrada.close();
		}
	}
	
	/**
	 * Cria uma instância de discente de acordo com id passado na busca e redireciona para o formulário
	 * de dados pessoais, no contexto JSF.
	 *<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 *<ul>
	 *	<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/discente/lista.jsp</li>
	 *</ul> 
	 *  
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward redirectDadosPessoais(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		SubSistema subSistema = getSubSistemaCorrente(request);
		BuscaDiscenteMBean buscaDiscenteMBean = 
			getMBean("buscaDiscenteGraduacao", request, response);
		
		addSession(OPERACAO, ATUALIZAR, request);
		
		/** Seta a operação discente  */
		if (subSistema.equals(SigaaSubsistemas.TECNICO) 
			|| subSistema.equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)) {
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_TECNICO);
			prepareMovimento(SigaaListaComando.ALTERAR_DISCENTE_TECNICO, request);
		} else if (subSistema.equals(SigaaSubsistemas.LATO_SENSU) 
				|| subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) {
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_LATO);
			prepareMovimento(SigaaListaComando.ALTERAR_DISCENTE_LATO, request);
		}

		/* Segundo a operação discente setada acima indica 
		qual managed bean que irá gerenciar a view que será redirecionada */
		//buscaDiscenteMBean.setObj(discente);
		buscaDiscenteMBean.escolheDiscente(Integer.valueOf(request.getParameter("proximoId")));
		
		return mapping.findForward(DADOS_PESSOAIS);
		
	}
	
	/**
	 * Cria uma instância de discente de acordo com id passado na busca e redireciona para 
	 * formulário de dados acadêmicos do discente no contexto JSF.
	 *<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 *<ul>
	 *	<li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/discente/lista.jsp</li>
	 *</ul> 
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward redirectDadosDiscente(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String forward = "";
		
		SubSistema subSistema = getSubSistemaCorrente(request);
		BuscaDiscenteMBean buscaDiscenteMBean = 
			getMBean("buscaDiscenteGraduacao", request, response);
		
		addSession(OPERACAO, ATUALIZAR, request);
		
		/** Seta a operação discente e o path para redirecionamento para o contexto JSF */
		if (subSistema.equals(SigaaSubsistemas.TECNICO) || subSistema.equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR)){ 
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_TECNICO);
			forward = DADOS_DISCENTE_TECNICO;
			prepareMovimento(SigaaListaComando.ALTERAR_DISCENTE_TECNICO, request);
		}else if (subSistema.equals(SigaaSubsistemas.LATO_SENSU) || 
			subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)){
			buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_LATO);
			forward = DADOS_DISCENTE_LATO;
			prepareMovimento(SigaaListaComando.ALTERAR_DISCENTE_LATO, request);
		}	
		
		/* Segundo a operação discente setada acima indica 
		qual managed bean que irá gerenciar a view que será redirecionada */
		//buscaDiscenteMBean.setObj(discente);
		buscaDiscenteMBean.escolheDiscente(Integer.valueOf(request.getParameter("proximoId")));
		
		return mapping.findForward(forward);
		
	}

	/**
	 * Setar as propriedades padrões a serem exibidas no formulário dos dados de
	 * discente. ->>> Cada tipo de discente deve ter as propriedades específicas
	 * de seus forms.
	 *
	 * @throws ArqException
	 */
	private void setDefaultProperties(ActionForm form, HttpServletRequest request) throws ArqException {
		DiscenteForm dForm = (DiscenteForm) form;
		SubSistema subSistema = getSubSistemaCorrente(request);
		if (subSistema.equals(SigaaSubsistemas.TECNICO) || subSistema.equals(SigaaSubsistemas.MEDIO)) {
			dForm.getDiscenteTecnico().setTipoRegimeAluno(new TipoRegimeAluno(TipoRegimeAluno.EXTERNO));
			dForm.getDiscenteTecnico().setFormaIngresso(new FormaIngresso(37350));


		} else if (subSistema.equals(SigaaSubsistemas.LATO_SENSU) || subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) {
			dForm.getDiscenteLato().setFormaIngresso(new FormaIngresso(37350));
			dForm.getDiscenteLato().setTipoProcedenciaAluno(new TipoProcedenciaAluno(GenericTipo.NAO_INFORMADO));
			if (getFromSession("cursoId", request) != null)
				dForm.getDiscenteLato().getTurmaEntrada().setCursoLato(new CursoLato(Integer.parseInt((String) getFromSession("cursoId", request))));
			if( (getAcessoMenu(request).isCoordenadorCursoLato() || getAcessoMenu(request).isSecretarioLato()) && subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO) )
				dForm.getDiscenteLato().getTurmaEntrada().setCursoLato( (CursoLato) request.getSession().getAttribute("cursoAtual") );

		}
		if (!subSistema.equals(SigaaSubsistemas.TECNICO)) {
			dForm.getDiscente().setAnoIngresso(Calendar.getInstance().get(Calendar.YEAR));
		}
		dForm.getDiscente().setNivel(getNivelEnsino(request));

		// setando a gestora acadêmica do discente
		GenericDAO dao = getGenericDAO(request);
		try {
			Unidade gestora;
			//	a gestora acadêmica do discente lato sempre é a Instituição
			if(!subSistema.equals(SigaaSubsistemas.LATO_SENSU) && !subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO))
				gestora = dao.findByPrimaryKey(getUnidadeGestora(request), Unidade.class);
			else
				gestora = dao.findByPrimaryKey(Unidade.UNIDADE_DIREITO_GLOBAL, Unidade.class);
			dForm.getDiscente().setGestoraAcademica(gestora);
		} finally {
			dao.close();
		}
	}

	/**
	 * Ação chamada pelo segundo form. Responsável pela validação dos dados
	 * submetidos específicos de um aluno. Também inicializa alguns campos dos
	 * objetos selecionados nos SELECT, já que só possuem o ID ao serem
	 * submetidos. ->>> Cada tipo de discente deve ter suas validações e objetos
	 * inicializados.
	 */
	public ActionForward submeterDadosDiscente(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		SubSistema subSistema = getSubSistemaCorrente(request);
		DiscenteForm discenteForm = (DiscenteForm) form;
		
		if(request.getSession().getAttribute("discenteAntigo") != null)
			discenteForm.setDiscenteAntigo(true);

		/* Validar o form submetido */
		if (subSistema.equals(SigaaSubsistemas.TECNICO) || subSistema.equals(SigaaSubsistemas.MEDIO)) {
			DiscenteValidator.validarDadosDiscenteTecnico(discenteForm.getDiscenteTecnico(), discenteForm, newListaMensagens(request));
		} else if (subSistema.equals(SigaaSubsistemas.LATO_SENSU) || subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) {
			DiscenteValidator.validarDadosDiscenteLato(discenteForm.getDiscenteLato(), discenteForm, newListaMensagens(request));
		}

		if (flushErros(request)) {
			return mapping.findForward(DADOS_DISCENTE);
		}

		/*
		 * Inicializando objs selecionados apenas pelo ID (objs dos SELECTS das JSPs)
		 */
		GenericDAO dao = getGenericDAO(request);
		try {
			if (subSistema.equals(SigaaSubsistemas.TECNICO) || subSistema.equals(SigaaSubsistemas.MEDIO)) {
				DiscenteTecnico tecnico = discenteForm.getDiscenteTecnico();
				tecnico.setFormaIngresso(dao.findByPrimaryKey(tecnico.getFormaIngresso().getId(), FormaIngresso.class));
				tecnico.setTipoRegimeAluno(dao.findByPrimaryKey(tecnico.getTipoRegimeAluno().getId(), TipoRegimeAluno.class));
				tecnico.setTurmaEntradaTecnico(dao.findByPrimaryKey(tecnico.getTurmaEntradaTecnico().getId(), TurmaEntradaTecnico.class));
				tecnico.setCurso(tecnico.getTurmaEntradaTecnico().getCursoTecnico());
			} else if (subSistema.equals(SigaaSubsistemas.LATO_SENSU)) {
				DiscenteLato lato = discenteForm.getDiscenteLato();
				lato.setFormaIngresso(dao.findByPrimaryKey(lato.getFormaIngresso().getId(),	FormaIngresso.class));
				lato.setTurmaEntrada( dao.findByPrimaryKey(lato.getTurmaEntrada().getId(), TurmaEntradaLato.class) );
				lato.setTipoProcedenciaAluno( dao.findByPrimaryKey(lato.getTipoProcedenciaAluno().getId(), TipoProcedenciaAluno.class) );
			}
		} finally {
			dao.close();
		}
		setStep(request, 3);
		// e redireciona para a visualização do resumo
		return mapping.findForward(RESUMO);
	}

	/***************************************************************************
	 * Fim dos métodos customizados por sub-sistema
	 * *************************************************** -- Abaixo
	 * encontram-se os métodos generalizados
	 */

	/**
	 * Retorna objeto das subclasses de Discente de acordo com o subsistema
	 * corrente.
	 */
	@SuppressWarnings("unchecked")
	private Class getClasseDiscente(HttpServletRequest req) {
		return subClasses.get(getSubSistemaCorrente(req));
	}

	/**
	 * Inicializa os passos a serem exibidos durante o caso de uso de cadastro e
	 * atualização. O método é usado tanto para visualização do formulário para
	 * cadastro como para atualização
	 */
	private void initSteps(ActionMapping mapping, ActionForm form, HttpServletRequest request) {
		clearSteps(request);
		// Adiciona passos do caso de uso
		addStep(request, "Dados Pessoais", "/pessoa/wizard", DADOS_PESSOAIS);
		addStep(request, "Dados Acadêmicos", "/ensino/discente/wizard", DADOS_DISCENTE);
		addStep(request, "Resumo", "/ensino/wizard", RESUMO);

		setStep(request, 2);
	}

	/**
	 * Ação chamada pelo primeiro formulário do cadastro, onde contém os dados
	 * pessoais. Depois que PessoaAction valida e popula PessoaForm, ele
	 * redireciona para esse método popular o form de discente, e os selects
	 * específicos dos dados dos tipos de discentes.
	 *
	 * Não é chamado por JSP.
	 * @param mapping
	 * @param form
	 * @param req
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward popular(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse response) throws Exception {
		DiscenteForm discenteForm = (DiscenteForm) form;
		Boolean passou = (Boolean) getFromSession("passou", req);
		if (passou == null) {
			passou = false;
			addSession("passou", Boolean.TRUE, req);
		}

		if (!passou) {
			discenteForm.initDiscente(getClasseDiscente(req));
			PessoaForm pForm = (PessoaForm) req.getSession().getAttribute("pessoaForm");
			discenteForm.getDiscente().setPessoa(pForm.getPessoa());

			popularSelects(req);
			initSteps(mapping, form, req);
		}

		String operacao = null;
		Comando comando = null;
		// se não for passado o id do discente, é cadastro
		if (getFromSession("proximoId", req) == null) {
			operacao = CADASTRAR;
			comando = SigaaListaComando.CADASTRAR_DISCENTE;
			if (!passou)
				setDefaultProperties(discenteForm, req);
		} else { // senão é alteração
			operacao = ATUALIZAR;
			comando = SigaaListaComando.ALTERAR_DISCENTE;
			if (!passou) {
				int discenteId = new Integer(getFromSession("proximoId", req).toString());
				GenericDAO dao = getGenericDAO(req);
				try {
					@SuppressWarnings("unchecked")
					DiscenteAdapter disc = (DiscenteAdapter) dao.findByPrimaryKey(discenteId, getClasseDiscente(req));
					disc.setPessoa(discenteForm.getDiscente().getPessoa());
					discenteForm.setDiscente(disc);
					discenteForm.init(mapping, req);
					SubSistema subSistema = getSubSistemaCorrente(req);
					if (subSistema.equals(SigaaSubsistemas.TECNICO) || subSistema.equals(SigaaSubsistemas.MEDIO)) {
						DiscenteTecnico tec = discenteForm.getDiscenteTecnico();
						if (tec.getTipoRegimeAluno() == null || tec.getTipoRegimeAluno().getId() <= 0)
							tec.setTipoRegimeAluno(new TipoRegimeAluno(TipoRegimeAluno.EXTERNO));
						if (tec.getFormaIngresso() == null || tec.getFormaIngresso().getId() <= 0)
							tec.setFormaIngresso(new FormaIngresso(37350));
					}
				} finally {
					dao.close();
				}
			}
		}

		addSession(OPERACAO, operacao, req);
		prepareMovimento(comando, req);
		if (getFromSession("resumirDados", req) != null)
			return mapping.findForward(RESUMO);
		return mapping.findForward(DADOS_DISCENTE);
	}

	/**
	 * Carrega objeto no form para a visualização dos seus detalhes, e então confirma a remoção
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *<br/> 
	 *<ul>
	 *	<li>/sigaa.war/WEB-INF/jsp/ensino/discente/lista.jsp</li>
	 *</ul> 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		DiscenteForm discenteForm = (DiscenteForm) form;
		discenteForm.initDiscente(getClasseDiscente(req));
		GenericDAO dao = getGenericDAO(req);
		try {
			int id = RequestUtils.getIntParameter(req, "discenteId");
			discenteForm.setDiscente((DiscenteAdapter) dao.findByPrimaryKey(id, getClasseDiscente(req)));
		} finally {
			dao.close();
		}

		prepareMovimento(SigaaListaComando.REMOVER_DISCENTE, req);
		addSession(OPERACAO, REMOVER, req);

		return mapping.findForward(RESUMO);
	}

	/**
	 * Verifica se a operação ainda se encontra ativa.
	 * Retorna verdadeiro caso a operação não esteja mais ativa. 
	 * Utilizado para evitar problemas quando o usuário volta pelo navegador.
	 * 
	 * @param req
	 * @param projetoForm
	 * @return
	 */
	private boolean isNotOperacaoAtiva(HttpServletRequest req) {
		return (req.getSession().getAttribute("steps") == null || ((ArrayList<?>) req.getSession().getAttribute("steps")).size() < 3 )&& 
				(getFromSession(OPERACAO, req) == null || !getFromSession(OPERACAO, req).equals(REMOVER));
	}
	
	/**
	 * Esse método será chamado para encaminhar o objeto de domínio ao processo
	 * de persistência pelos processadores EJB.
	 * 
	 * Não é chamado por JSP.
	 * 
	 */
	public ActionForward chamaModelo(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		DiscenteForm discenteForm = (DiscenteForm) form;
	
		if( isNotOperacaoAtiva(request) ){
			addMensagemErro("A operação não está mais ativa. Por favor reinicie a operação.", request);
			if(flushErros(request))
				return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		DiscenteAdapter discente = discenteForm.getDiscente();

		/** criando movimento */
		DiscenteMov mov = new DiscenteMov(getUltimoComando(request), discente);
		mov.setDiscenteAntigo(discenteForm.isDiscenteAntigo());
		if(discenteForm.getArquivoHistorico() != null)
			mov.setFile(discenteForm.getArquivoHistorico());

		// Decide o tipo de aluno baseado no subsistema
		discente.setNivel(getNivelEnsino(request));
		DiscenteAdapter discenteRetornado = null;
		try {
			discenteRetornado = (DiscenteAdapter) execute(mov, request);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens().getMensagens(), request);
			return mapping.findForward(RESUMO);
		}

		if(discente.getPessoa() == null)
			discente.setPessoa(new Pessoa());
		String genero = null;
		if (discente.getPessoa().getSexo() == 'F')
			genero = "a";
		else
			genero = "o";

		String operacao = (String) getFromSession(OPERACAO, request);
		removeSession("pessoaForm", request);
		removeSession(mapping.getName(), request); 
		// não remove os dados do
		// formulário dos dados do discente
		clearSession(request);
		if (CADASTRAR.equals(operacao)) {
			addInformation("Discente " +  discenteRetornado.getMatriculaNome() +  
					", cadastrad" + genero + " com sucesso!", request);
			clearSteps(request);
			
			// Verificar se o fluxo do cadastro iniciou-se com um processo seletivo 
			if ( request.getSession().getAttribute("discenteProcessoSeletivo") != null ) {
				request.getSession().removeAttribute("discenteProcessoSeletivo");
				response.sendRedirect(request.getContextPath() +  "/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsf");
				return null;
			} else {
				return mapping.findForward("cad_sucesso");
			}
			
		} else if (ATUALIZAR.equals(operacao)) {
			addInformation("Discente Atualizad" + genero + " com Sucesso", request);
			clearSteps(request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		} else if (REMOVER.equals(operacao)) {
			addInformation("Discente Removid" + genero + " com Sucesso", request);
			return mapping.findForward(getSubSistemaCorrente(request).getForward());
		}
		
		return null;
		
	}

	/**
	 * Lista os discentes a partir do tipo discente especificado pela subclasse.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 * 	<li>/sigaa.war/WEB-INF/jsp/menus/menu_lato_coordenador.jsp</li>
	 * </ul>
	 */
	@Override
	public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		DiscenteForm dForm = (DiscenteForm) form;
		dForm.initDiscente(getClasseDiscente(req));
		dForm.setObj(dForm.getDiscente());

		getForm(form).checkRole(req);

		Collection<?> lista = null;

		/* Popular formulário de Busca */
		SigaaForm<?> sForm = getForm(form);
		Map<String, String> searchMap = sForm.getSearchMap(req);

		/* Retirar dados da busca de sessão */
		if (getForm(form).isUnregister()) {
			searchMap = null;
			getForm(form).unregisterSearchData(req);
		}

		if (searchMap != null) {
			Set<Entry<String, String>> entrySet = searchMap.entrySet();
			for (Entry<String, String> entry : entrySet)
				BeanUtils.setProperty(sForm, entry.getKey(), entry.getValue());
			req.setAttribute(mapping.getName(), sForm);
		}

		if (getPaging(req) == null || req.getParameter("tipoBusca") != null) {
			req.setAttribute("pagingInformation", new PagingInformation(0));
		}
		/* Realiza buscas */
		if (getForm(form).isBuscar())
			lista = getForm(form).customSearch(req);

		DiscenteDao dao = null;
		try {
			dao = getDAO(DiscenteDao.class, req);
			if( getNivelEnsino(req) != NivelEnsino.LATO ){
				TurmaEntradaTecnicoDao tetDao = getDAO(TurmaEntradaTecnicoDao.class, req);
				try {
					if (getFromSession("turmasEntrada", req) == null) {
						addSession("turmasEntrada",
								tetDao.findAll(getUnidadeGestora(req), getNivelEnsino(req), null), req);
					}
				} finally {
					tetDao.close();
				}
			} else if(isUserInRole(SigaaPapeis.GESTOR_LATO, req)){
				if(getFromSession("cursosLato", req) == null){
					addSession("cursosLato", dao.findAll(CursoLato.class, "nome", "asc"), req);
				}
			}
		} finally {
		    if(dao != null){
		    	dao.close();
		    }
			req.getSession().removeAttribute("cancelandoDadosPessoais");
		}
		req.setAttribute(ConstantesCadastro.ATRIBUTO_LISTAGEM, lista);

		if ("null".equalsIgnoreCase(req.getParameter("forward"))) {
			req.getSession().removeAttribute("forward");
		}

		return mapping.findForward("listar");
	}

	/**
	 * Cancela a busca de discente, reinicializando os objetos envolvidos na operação.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <br>
	 * <ul>
	 * 	<li>/sigaa.war/WEB-INF/jsp/ensino/discente/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.struts.AbstractAction#cancelar(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse))
	 * 
	 */
	@Override
	public ActionForward cancelar(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// teste que determina o redirecionamento de um cancelamento
		String operacao = (String) getFromSession(OPERACAO, request);
		// remover form de pessoa de sessão
		request.getSession().removeAttribute("pessoaForm");
		clearSession(request);
		clearSteps(request);
		if (ATUALIZAR.equals(operacao) || REMOVER.equals(operacao)) {
			request.getSession().setAttribute("cancelandoDadosPessoais", true);
			DiscenteForm dForm = (DiscenteForm) form;
			dForm.setDiscente((DiscenteAdapter) getClasseDiscente(request).newInstance());
			return list(mapping, form, request, response);
		}
		return getMappingSubSistema(request, mapping);
	}

	/**
	 * Método que popula os dados para a exibição do relatório do caso de uso
	 * 
	 * Método não invocado por jsp.
	 * 
	 * Usar: RelatoriosTecnicoMBean.listagemAlunosCadastrados()
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public ActionForward gerarRelatorio(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DiscenteDao dao = getDAO(DiscenteDao.class, request);
		Collection<Discente> discentes = dao.findByNome("", getUnidadeGestora(request), new char[]{getNivelEnsino(request)}, null, true, true, null);
		request.setAttribute("discentes", discentes	);
		return mapping.findForward("relatorio");
	}

}