/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 30/01/2007
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dao.ensino.FormaIngressoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed-Bean das opera��es e casos de uso envolvendo os Discentes de Gradua��o
 *
 * @author Leonardo
 * @author Victor Hugo
 *
 */
@Component("discenteGraduacao")
@Scope("session")
public class DiscenteGraduacaoMBean extends SigaaAbstractController<DiscenteGraduacao> implements OperadorDadosPessoais, OperadorDiscente{
	
	/** Indica o redirecionamento para ao menu do m�dulo gradua��o.*/
	public static final String JSP_MENU_DAE = "menuGraduacao";
	/** Indica o redirecionamento para ao menu do m�dulo da coordena��o �nica.*/
	public static final String JSP_MENU_COORDENACAO_UNICA = "menuCoordenacaoUnica";
	/** Indica o redirecionamento para o formul�rio de discente.*/
	public static final String JSP_FORM = "/graduacao/discente/form.jsp";
	/** Indica o redirecionamento para o relat�rio de alunos.*/
	public static final String JSP_REL_ALUNOS = "/graduacao/relatorios/alunos.jsp";
	/** Indica o redirecionamento para a altera��o de perfil inicial.*/
	public static final String JSP_PERFIL = "/graduacao/discente/perfil_inicial.jsp";

	/** Bloqueia a edi��o do campo ano/semestre no formul�rio.
	 * Usado em /graduacao/discente/form.jsp 
	 */
	public boolean blockAnoSemestre;

	/** Bloqueia a edi��o do campo status no formul�rio.
	 * Usado em /graduacao/discente/form.jsp 
	 */
	public boolean blockStatus;

	/** select a ser populado com as formas de ingresso poss�veis para o aluno */
	private Collection<SelectItem> formasIngressoCombo = new ArrayList<SelectItem>(0);
	
	/** Indica se o discente � antigo */
	private boolean discenteAntigo;
	
	/** Arquivo digitalizado do hist�rico do discente antigo */
	private UploadedFile historico;
	
	/** COntrola se o formul�rio abrir� para importa��o do hist�rico digitalizado ou n�o. */
	private boolean importaHistorico;
	
	/** Construtor padr�o. Invoca o m�todo {@link #initObj()}.
	 * @throws DAOException
	 */
	public DiscenteGraduacaoMBean() throws DAOException{
		initObj();
	}

	/** Inicializa os dados do controller.
	 * @throws DAOException
	 */
	private void initObj() throws DAOException {
		CalendarioAcademico cal = getCalendarioVigente();
		if(cal == null){
			cal = new CalendarioAcademico();
			cal.setAno( CalendarUtils.getAnoAtual() );
			cal.setPeriodo( getPeriodoAtual() );
		}
		obj = new DiscenteGraduacao();
		obj.setAnoIngresso( cal.getAno() );
		obj.setPeriodoIngresso( cal.getPeriodo() );
		obj.setMatrizCurricular( new MatrizCurricular() );
		obj.setCurso(new Curso());
		obj.setNivel('G');
	}

	/** Inicializa os dados do controller e retorna o formul�rio de relat�rios de alunos.<br /><br />
	 * 	Chamado por /SIGAA/app/sigaa.ear/sigaa.war/portais/rh_plan/abas/graduacao.jsp
	 * @return /graduacao/relatorios/alunos.jsp
	 * @throws DAOException
	 */
	public String preBuscar() throws DAOException{
		initObj();
		setResultadosBusca(null);
		return forward(JSP_REL_ALUNOS);
	}

	/** Verifica se o usu�rio pode informar o hist�rico digitalizado do discente ou n�o.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/discente/form.jsp
	 * @param evt
	 * @throws DAOException
	 */
	public void numeroMatriculaListener(ActionEvent evt) throws DAOException{
		DiscenteDao dao = getDAO(DiscenteDao.class);
		Discente d = dao.findByMatricula(obj.getMatricula());
		if (d != null) {
			MatriculaComponenteDao mDao = getDAO(MatriculaComponenteDao.class);
			if (mDao.countMatriculasByDiscente(d, (SituacaoMatricula[]) null) == 0)
				importaHistorico = true;
			else 
				importaHistorico = false;
		} else {
			importaHistorico = true;
		}
	}

	/** Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/alunos.jsp
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/busca_discente.jsp
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#buscar()
	 */
	@Override
	public String buscar() throws DAOException {
		Collection<Curso> cursos = new ArrayList<Curso>(0);
		// no caso de coordena��es do centro
		if (isUserInRole(SigaaPapeis.SECRETARIA_CENTRO)) {
			CursoDao dao = getDAO(CursoDao.class);
			cursos =  dao.findByCentro(getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
			// verifica se foi escolhido um curso em especifico
			if (obj.getCurso().getId() > 0) {
				// verifica se o curso � do centro do usu�rio
				 if (!cursos.contains(obj.getCurso())) {
					 addMensagemErro("O usu�rio s� pode buscar por discentes de cursos do seu Centro");
					 return null;
				 } else {
					 cursos = new ArrayList<Curso>(0);
					 cursos.add(obj.getCurso());
				 }
			}

		} else {
			cursos.add(obj.getCurso());
		}
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		setResultadosBusca(dao.findByCursoAnoPeriodo(obj.getAnoIngresso(), obj.getPeriodoIngresso(), cursos));

		obj = new DiscenteGraduacao();

		return null;
	}


	/**
	 * Chama o operador de dados pessoais para realizar a verifica��o do CPF e redireciona para o cadastro do discente de gradua��o. <br /><br />
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/cdp.jsp
	 * @return
	 * @throws ArqException
	 */
	public String popular() throws ArqException{
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.CDP});

		//init();
		setConfirmButton("Cadastrar");
		prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
		getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);

		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setCodigoOperacao( OperacaoDadosPessoais.DISCENTE_GRADUACAO );
		if ( (obj.getId() == 0 && obj.getPessoa().getId() == 0) || isDiscenteAntigo() )
			blockAnoSemestre = false;
		else
			blockAnoSemestre = true;
		return dadosPessoaisMBean.popular();
	}

	/**
	 * Inicia o cadastro de discentes novos.<br /><br />
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/aluno.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteNovo() throws ArqException {
		setDiscenteAntigo(false);
		obj = new DiscenteGraduacao();
		initObj();
		obj.setStatus(StatusDiscente.CADASTRADO);
		obj.setAnoIngresso(CalendarUtils.getAnoAtual());
		obj.setPeriodoIngresso(getPeriodoAtual());
		importaHistorico = false;
		return popular();
	}

	/**
	 * Inicia o cadastro de discentes antigos. <br /><br />
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/aluno.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteAntigo() throws ArqException {
		setDiscenteAntigo(true);
		obj = new DiscenteGraduacao();
		initObj();
		obj.setStatus(StatusDiscente.CONCLUIDO);
		importaHistorico = false;
		return popular();
	}


	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {

		if( obj.getPessoa().getTipoRaca().getId() == 0 )
			obj.getPessoa().setTipoRaca(null);

		super.beforeCadastrarAndValidate();
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		ListaMensagens mensagens = new ListaMensagens();
		
		obj.setMatrizCurricular( getGenericDAO().refresh(obj.getMatrizCurricular()) );
		DiscenteValidator.validarDadosDiscenteGraduacao(obj, discenteAntigo, mensagens);

		
		
		if ( !discenteAntigo ) {
			CalendarioAcademico cal = getCalendarioVigente();
			int semDiscente = new Integer(obj.getAnoIngresso()+""+obj.getPeriodoIngresso());
			int semAnterior = DiscenteHelper.somaSemestres(cal.getAno(), cal.getPeriodo(), -1);
			int semSeguinte = DiscenteHelper.somaSemestres(cal.getAno(), cal.getPeriodo(), 1);
			
			if (semDiscente < semAnterior || semDiscente > semSeguinte)
				mensagens.addMensagem(new MensagemAviso("Ano-Per�odo de Ingresso Inv�lido", TipoMensagemUFRN.ERROR));
		} else {
			if (this.importaHistorico && this.historico == null && this.obj.getStatus() == StatusDiscente.CONCLUIDO) 
				mensagens.addMensagem(new MensagemAviso("Para discentes antigos conclu�dos, � necess�rio informar o arquivo digitalizado do hist�rico.",TipoMensagemUFRN.ERROR));
			if (!this.importaHistorico)
				this.historico = null;
		}

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		// removendo atributos transientes
		DiscenteMov mov = new DiscenteMov(SigaaListaComando.CADASTRAR_DISCENTE, obj);
		mov.setDiscenteAntigo(isDiscenteAntigo());
		if (isDiscenteAntigo())
			mov.setArquivo(historico);

		try {
			executeWithoutClosingSession(mov, (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
			addMessage( "Discente cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}

		getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		
		// Redirecionar para a tela de comprovante
		HistoricoDiscenteMBean historicoDiscenteMBean = new HistoricoDiscenteMBean();
		historicoDiscenteMBean.setDiscente( obj );
		getCurrentRequest().setAttribute( OperacaoDiscente.getOperacao(OperacaoDiscente.HISTORICO_COMPLETO_DISCENTE).getMBean(), historicoDiscenteMBean);
		getCurrentRequest().setAttribute("comprovante", true );

		resetBean();
		DadosPessoaisMBean dMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dMBean.resetBean();
		initObj();

		return historicoDiscenteMBean.selecionaDiscente();
	}

	/** Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/discente/form.jsp
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/discente/view_all.jsp
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/relatorios/alunos.jsp
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/busca_discente.jsp
	 * 
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar(){
		getCurrentSession().removeAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		resetBean();
		DadosPessoaisMBean dMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dMBean.resetBean();
		if(getUsuarioLogado().isUserInRole(SigaaPapeis.DAE))
			return JSP_MENU_DAE;
		else if(getUsuarioLogado().isUserInRole(SigaaPapeis.SECRETARIA_CENTRO))
			return JSP_MENU_COORDENACAO_UNICA;
		else
			return getSubSistema().getForward();
	}

	/**
	 * Define o objeto populado com os dados pessoais nos MBeans que implementares essa interface
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#setDadosPessoais(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/**
	 * M�todo invocado pelos MBeans ap�s a submiss�o dos dados
	 * @see br.ufrn.sigaa.jsf.OperadorDadosPessoais#submeterDadosPessoais()
	 * Chamado por: M�todo n�o invocado por JSPs.
	 */
	public String submeterDadosPessoais() {

		DiscenteGraduacaoDao dao = DAOFactory.getInstance().getDAO( DiscenteGraduacaoDao.class );
		try {
			if( obj.getPessoa().getId() != 0 ){
				Collection<DiscenteGraduacao> discentes = dao.findAtivoByPessoa( obj.getPessoa().getId() );
				if (discentes != null) {
					for (DiscenteGraduacao d : discentes) {
						if( d != null && d.getId() != 0 ) {
							
							if (d.getStatus() == StatusDiscente.GRADUANDO) {
								obj.setStatus( StatusDiscente.CADASTRADO );
								blockStatus = true;
								addMessage("Aten��o! J� existe um discente " + d.getStatusString()
										+ " associado a esta pessoa (mat. " + d.getMatricula() + ").", TipoMensagemUFRN.WARNING);
								// participa��o no ENADE
								if (d.getParticipacaoEnadeIngressante() == null || d.getParticipacaoEnadeIngressante().isParticipacaoPendente())
									addMensagemErro("O discente (mat. " + d.getMatricula() + ") tem pend�ncia no ENADE INGRESSANTE.");
								if (d.getParticipacaoEnadeConcluinte() == null || d.getParticipacaoEnadeConcluinte().isParticipacaoPendente())
									addMensagemErro("O discente (mat. " + d.getMatricula() + ") tem pend�ncia no ENADE CONCLU�NTE.");
							} else {
								addMensagemInformation("Aten��o! J� existe um discente " + d.getTipoString() + " e " + d.getStatusString() + "  associado a esta pessoa (mat. " + d.getMatricula() + ")");
							}
							
						}
					}
				}
				

			}
			if(checkOperacaoDadosPessoaisAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId()+"")){
				addMensagem(MensagensGerais.JA_EXISTE_OUTRA_OPERACAO_DADOS_PESSOAIS_ATIVA);
				redirectJSF(getSubSistema().getLink());
				return null;
			}
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		} finally {
			dao.close();
		}
		
		if (hasOnlyErrors())
			return null;
		return forward( JSP_FORM );
	}

	/**
	 * Verifica se a opera��o de dados pessoais (cadastro ou atualiza��o) continua ativa
	 * @param operacoes
	 * @return
	 */
	private boolean checkOperacaoDadosPessoaisAtiva(String ...operacoes) {
		String operacaoAtiva = (String) getCurrentSession().getAttribute(OperadorDadosPessoais.OPERACAO_ATIVA_SESSION);
		if (operacaoAtiva != null) {
			for (String operacao : operacoes) {
				if (operacaoAtiva.equals(operacao)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/** Indica se o formul�rio dever� estar bloqueado para edi��o de ano/semestre
	 * @return
	 */
	public boolean isBlockAnoSemestre() {
		return blockAnoSemestre;
	}

	/** Configura o formul�rio para o bloqueio da edi��o do ano/semestre
	 * @param blockAnoSemestre
	 */
	public void setBlockAnoSemestre(boolean blockAnoSemestre) {
		this.blockAnoSemestre = blockAnoSemestre;
	}

	/**
	 * Listener respons�vel por carregar as formas de entrada relativas ao 
	 * tipo de discente informado.<br /><br />
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/discente/form.jsp
	 * @param evt
	 * @throws DAOException
	 */
	public void filtraFormaEntrada(ValueChangeEvent evt) throws DAOException {
		FormaIngressoDao dao = getDAO(FormaIngressoDao.class);
		char tipo = 'R';
		if(((Integer) evt.getNewValue()) == 2)
			tipo = 'E';
		formasIngressoCombo = toSelectItems(dao.findAllFormasEntradaHabilitadasByNivelTipo(getNivelEnsino(),tipo), "id", "descricao");
	}

	/** Listener respons�vel por buscar o curso selecionado e atualizar o polo correspondente. <br /><br />
	 * chamado por /graduacao/discente/form.jsp
	 * @param evt
	 * @throws DAOException
	 */
	public void selecionaCurso(ValueChangeEvent evt) throws DAOException {

		// Atualizar Matrizes
		EstruturaCurricularMBean curriculoMBean = (EstruturaCurricularMBean) getMBean("curriculo");
		curriculoMBean.carregarMatrizes(evt);

		Curso curso = getGenericDAO().findByPrimaryKey((Integer) evt.getNewValue(), Curso.class);

		if (curso != null) {
			obj.setCurso(curso);

			if (curso.isADistancia()) {
				obj.setPolo(new Polo());
			} else {
				obj.setPolo(null);
			}
		}
		redirectMesmaPagina();
	}

	/** Cria e retorna uma lista de SelectItem de P�los do Curso.
	 * @see PoloDao#findByCurso(int)
	 * chamado por /graduacao/discente/form.jsp
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getPolosCursoCombo() throws ArqException {
		return toSelectItems( getDAO(PoloDao.class).findByCurso(obj.getCurso().getId() ), "id",  "cidade");
	}

	/** Retorna uma cole��o de SElectItem das formas de ingresso. 
	 * @return
	 */
	public Collection<SelectItem> getFormasIngressoCombo() {
		return formasIngressoCombo;
	}

	/** Determina se a edi��o do campo status no formul�rio est� bloqueado para edi��o.
	 * @return valor do bloqueio
	 */
	public boolean isBlockStatus() {
		return blockStatus;
	}

	/** Seta se a edi��o do campo status no formul�rio ser� bloqueado para edi��o.
	 * 
	 * @param blockStatus novo valor para o bloqueio
	 */
	public void setBlockStatus(boolean blockStatus) {
		this.blockStatus = blockStatus;
	}

	/** Informa se o cadastro � de discente antigo.
	 * @return 
	 */
	public boolean isDiscenteAntigo() {
		return this.discenteAntigo;
	}

	/** Seta se o cadastro � de discente antigo.
	 * @param discenteAntigo
	 */
	public void setDiscenteAntigo(boolean discenteAntigo) {
		this.discenteAntigo = discenteAntigo;
	}

	/** Seta o arquivo do hist�rico digitalizado.
	 * @param historico
	 */
	public void setHistorico(UploadedFile historico) {
		this.historico = historico;
	}

	/** Arquivo do hist�rico digitalizado.
	 * @return
	 */
	public UploadedFile getHistorico() {
		return historico;
	}

	/**
	 * Redireciona o usu�rio para a lista de discentes.
	 * <br /><br />
	 * Chamado por /sigaa.war/graduacao/estudante/perfil_inicial.jsp
	 * @return
	 * @throws DAOException
	 */
	public String buscaDiscente(){
		return forward("/graduacao/busca_discente.jsp");
	}
	
	/**
	 * M�todo respons�vel por iniciar a opera��o de acr�scimo no valor do perfil inicial.
	 * <br /><br />
	 * Chamado por /sigaa.war/graduacao/menus/administracao.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAcrescimoPerfilInicial() throws ArqException{
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE);
		
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ACRESCIMO_PERFIL_INICIAL);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * M�todo respons�vel por realizar a altera��o do perfil inicial do discente de gradua��o.
	 * <br /><br />
	 * Chamado por /sigaa.war/graduacao/menus/administracao.jsp
	 * @return
	 * @throws DAOException
	 */
	 public String confirmarAlteracaoPerfilInicial() throws DAOException{
		if( !isOperacaoAtiva(SigaaListaComando.ACRESCIMO_PERFIL_INICIAL.getId())  ){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return buscaDiscente();
		}
		if (obj.getPerfilInicialAlterado() == null) 
			erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Valor a ser adicionado no Perfil Inicial sem acr�scimo");
		
		if (obj.getPerfilInicialAlterado() != null && obj.getPerfilInicialAlterado() < 0) 
			erros.addErro("N�o � permitido diminuir o perfil inicial original, apenas aument�-lo");
		
		if (hasErrors()) return null;
		 
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.ACRESCIMO_PERFIL_INICIAL);
		mov.setObjMovimentado(obj);
		
		try {
			execute(mov);
			addMessage( "Perfil Inicial alterado com sucesso!", TipoMensagemUFRN.INFORMATION);
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		
		initObj();

		return buscaDiscente();
	}
	
	@Override
	public String selecionaDiscente() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.DAE);
		prepareMovimento(SigaaListaComando.ACRESCIMO_PERFIL_INICIAL);
		setOperacaoAtiva(SigaaListaComando.ACRESCIMO_PERFIL_INICIAL.getId());
		
		return forward( JSP_PERFIL );
	}			

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj = (DiscenteGraduacao) discente;
	}

	public boolean isImportaHistorico() {
		return importaHistorico;
	}

	public void setImportaHistorico(boolean importaHistorico) {
		this.importaHistorico = importaHistorico;
	}
	
}
