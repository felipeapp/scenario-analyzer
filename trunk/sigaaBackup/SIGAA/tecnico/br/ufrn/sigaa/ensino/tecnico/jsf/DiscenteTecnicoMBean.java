/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 03/02/2009
 * 
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.star.text.SetVariableType;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeAluno;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.DiscenteValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.DiscenteMov;
import br.ufrn.sigaa.ensino.tecnico.dao.EstruturaCurricularTecDao;
import br.ufrn.sigaa.ensino.tecnico.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.EstruturaCurricularTecnica;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.jsf.DadosPessoaisMBean;
import br.ufrn.sigaa.jsf.OperacaoDadosPessoais;
import br.ufrn.sigaa.jsf.OperadorDadosPessoais;
import br.ufrn.sigaa.mensagens.MensagensPortalCoordenadorStricto;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed Bean de Discente de Técnico, que realiza
 * as seguintes operações cadastro, atualização, remoção de um discente.
 *   
 * @author Mário Rizzi
 *
 */
@Component("discenteTecnico")  @Scope("session")
public class DiscenteTecnicoMBean extends SigaaAbstractController<DiscenteTecnico> 
	implements OperadorDadosPessoais, OperadorDiscente {


	/** Coleção de SelectItem das formas de ingresso possíveis para o discente. */
	private List<SelectItem> formasIngressoCombo = new ArrayList<SelectItem>(0);

	/** Lista dos possíveis cursos para o discente .*/
	private List<SelectItem> cursosCombo = new ArrayList<SelectItem>(0);

	/** Lista dos possíveis status discente. */
	private List<SelectItem> statusCombo = new ArrayList<SelectItem>(0);

	/** Lista dos possíveis currículos do discente. */
	private List<SelectItem> curriculosCombo = new ArrayList<SelectItem>(0);
	
	/** Lista das possíveis turmas do discente. */
	private List<SelectItem> turmasCombo = new ArrayList<SelectItem>(0);
	
	/** Lista dos possíveis regimes do discente. */
	private List<SelectItem> regimesAlunosCombo = new ArrayList<SelectItem>(0);
	
	/** Lista dos possíveis processos seletivos do discente. */
	private List<SelectItem> processosSeletivosCombo = new ArrayList<SelectItem>(0);
	
	/** Indica se é discente antigo. */
	private boolean discenteAntigo;

	/** Construtor padrão. */
	public DiscenteTecnicoMBean() {
		initObj();
	}

	/** Inicializa os atributos do controller.
	 * 
	 */
	private void initObj() {
		
		obj = new DiscenteTecnico();
		obj.setDiscente(new Discente());
		obj.setAnoIngresso( CalendarUtils.getAnoAtual() );
		obj.setTurmaEntradaTecnico(new TurmaEntradaTecnico());
		obj.setFormaIngresso(new FormaIngresso());

	}

	/**
	 * Inicia o cadastro de discentes novos.<br>
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/tecnico/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteNovo() throws ArqException {
		
		checkRole(SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, 
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		setConfirmButton("Cadastrar");
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
		initObj();
		setDiscenteAntigo(false);
		obj.setStatus(StatusDiscente.ATIVO);
		return popular();
		
	}

	/**
	 * Inicia o cadastro de discentes antigos.<br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/tecnico/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastroDiscenteAntigo() throws ArqException {
		
		checkRole(SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, 
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		setConfirmButton("Cadastrar");
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_DISCENTE);
		initObj();
		setDiscenteAntigo(true);
		obj.setStatus(StatusDiscente.CONCLUIDO);
		return popular();
		
	}

	/**
	 * Chama o operador de dados pessoais para realizar a verificação do CPF
	 * e redireciona para o cadastro do discente de pós-graduação.
	 * @return
	 * @throws ArqException
	 */
	private String popular() throws ArqException{

		
		if(obj.getId() == 0){
			GenericDAO dao = getGenericDAO();
			obj.setTipoRegimeAluno(new TipoRegimeAluno(TipoRegimeAluno.EXTERNO));
			obj.setFormaIngresso(new FormaIngresso(37350));
			obj.setAnoIngresso(Calendar.getInstance().get(Calendar.YEAR));
		
			obj.setGestoraAcademica(dao.findByPrimaryKey(getUnidadeGestora(), Unidade.class));
			obj.setNivel(getNivelEnsino());
		
		
			if(obj.getProcessoSeletivo() != null)
				obj.getProcessoSeletivo().getEditalProcessoSeletivo();
			
			//Prepara as combos utilizados no formulário
			popularCombos();
		}
		
		DadosPessoaisMBean dadosPessoaisMBean = (DadosPessoaisMBean) getMBean("dadosPessoais");
		dadosPessoaisMBean.initObj();
		dadosPessoaisMBean.setObj(obj.getPessoa());
		dadosPessoaisMBean.carregarMunicipios();
		dadosPessoaisMBean.setCodigoOperacao(OperacaoDadosPessoais.DISCENTE_TECNICO);
	
		return dadosPessoaisMBean.popular();
		
	}
	
	/**
	 * Popula selects a serem usados nas JSPs. O método é usado tanto para
	 * visualização do formulário para cadastro como para atualização ->>> Cada
	 * tipo de discente deve ter seus SELECTS específicos populados aqui.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 */
	public void popularCombos() throws ArqException {
		
		CursoDao dao = getDAO(CursoDao.class);
		
		//carrega o combo-box contendo todas as formas de ingresso disponíveis.
		setFormasIngressoCombo( toSelectItems( dao.findAll(FormaIngresso.class,
				"descricao", "asc"),"id","descricao") );
		
		if ( isEmpty(obj.getFormaIngresso()) )
			obj.setFormaIngresso(new FormaIngresso(37350));
		
		if(obj.isHasVariosCursos()){
			//Carrega o combo-box contendo todos os cursos de nível técnico.
			setCursosCombo( toSelectItems( dao.findAll(getUnidadeGestora(), getNivelEnsino(),
					CursoTecnico.class, null),"id","nome") );
		} else {
			SelectItem item = new SelectItem(obj.getCurso().getId(), obj.getCurso().getNome());
			List<SelectItem> list = new ArrayList<SelectItem>();
			list.add(item);
			setCursosCombo(list);
		}
		
		setTurmasCombo(new ArrayList<SelectItem>(0));
		setCurriculosCombo(new ArrayList<SelectItem>(0));
		
		carregarCurriculosTurmasEntrada(null);
		if (obj.getId() > 0)
			obj.setProcessoSeletivo( getGenericDAO().findAndFetch(obj.getId(), DiscenteTecnico.class, 
				"processoSeletivo").getProcessoSeletivo() );
			
		setRegimesAlunosCombo( toSelectItems( getGenericDAO().findAll(TipoRegimeAluno.class), "id", "descricao"));
		if(discenteAntigo)
			setStatusCombo( toSelectItems( StatusDiscente.getStatusTodos() , "id", "descricao") );
		
	}

	/**
	 * Responsável por realizar a validação antes do cadastro.  
	 * 
	 * JSP: Não invocado por jsp.
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {

		if( obj.getPessoa().getTipoRaca().getId() == 0 )
			obj.getPessoa().setTipoRaca(null);
		
		super.beforeCadastrarAndValidate();
	}

	/**
	 * Método responsável pelo cadastro do discente Técnico. 
	 * 
	 * Chamado por /ensino/discente/resumo.jsp
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/discente.jsp
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/stricto/menu_coordenador.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException {

		checkRole(SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, 
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		
		clearMensagens();
		
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId(),SigaaListaComando.ALTERAR_DISCENTE_TECNICO.getId()) )
			return cancelar();
		
		
		ListaMensagens mensagens = new ListaMensagens();

		 
		if( obj.getId() > 0){
			DiscenteTecnico discOriginal = getGenericDAO().findByPrimaryKey( obj.getId() , DiscenteTecnico.class);
			if( !obj.getTipo().equals( discOriginal.getTipo() ) ){
				addMensagem(MensagensPortalCoordenadorStricto.NAO_E_POSSIVEL_ALTERAR_TIPO_DO_DISCENTE);
				return null;
			}
		}

		DiscenteValidator.validarDadosDiscenteTecnico(obj, null, discenteAntigo, mensagens);
		
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		// removendo atributos transientes
		if( obj.getPessoa().getEstadoCivil() != null &&
				obj.getPessoa().getEstadoCivil().getId() == 0 )
			obj.getPessoa().setEstadoCivil(null);

		if( obj.getPessoa().getTipoRaca()!= null &&
				obj.getPessoa().getTipoRaca().getId() == 0 )
			obj.getPessoa().setTipoRaca(null);

		Comando comando = SigaaListaComando.CADASTRAR_DISCENTE;
		if( obj.getId() > 0 )
			comando = SigaaListaComando.ALTERAR_DISCENTE_TECNICO;
		
		DiscenteMov mov = new DiscenteMov(comando, obj);
		mov.setDiscenteAntigo(isDiscenteAntigo());
	
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			if( comando.equals(SigaaListaComando.CADASTRAR_DISCENTE) ){
				addMessage( "Discente "+obj.getNome()+" cadastrado com sucesso, e associado com o " +
						"número de  matrícula " + obj.getMatricula(), TipoMensagemUFRN.INFORMATION);
			}else{
				addMessage( "O discente "+obj.toString()+" foi atualizado com sucesso.", TipoMensagemUFRN.INFORMATION);
			}
			removeOperacaoAtiva();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		if (getCurrentSession().getAttribute("discenteProcessoSeletivo") != null) {
			resetBean();
			getCurrentSession().removeAttribute("discenteProcessoSeletivo");
			return redirect("/administracao/cadastro/ProcessoSeletivo/lista_inscritos.jsf");
		} else {
			return cancelar();
		}
	}

	/**
	 * Carrega os possíveis currículos e as turmas de entrada do aluno de acordo com o curso selecionado.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/discente/form.jsp</li>
	 * </ul>
	 * @param e
	 * @throws DAOException
	 */
	public String carregarCurriculosTurmasEntrada(ValueChangeEvent e) throws DAOException  {
		EstruturaCurricularTecDao dao = getDAO(EstruturaCurricularTecDao.class);
		TurmaEntradaTecnicoDao dao2 = getDAO(TurmaEntradaTecnicoDao.class);
		Integer id = null;
		if( e != null )
			id = (Integer) e.getNewValue();
		else if( !isEmpty( obj.getEstruturaCurricularTecnica().getCursoTecnico().getId() ) ){
			id =  obj.getEstruturaCurricularTecnica().getCursoTecnico().getId();
		}
		if( id == null )
			return null;
		curriculosCombo = toSelectItems(dao.findByCursoTecnico(id), "id", "descricao");
		turmasCombo = toSelectItems(dao2.findByCursoTecnico(id,0,0,0), "id", "descricao");
		setFormasIngressoCombo( toSelectItems( dao.findAll(FormaIngresso.class,
				"descricao", "asc"),"id","descricao") );
		return null;
	}
	
	/**
	 * Seta os dados pessoais
	 */
	public void setDadosPessoais(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}

	/**
	 * Submete os dados pessoais
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>

	 */
	public String submeterDadosPessoais() {
		try {
			return telaDadosDiscente();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Submete os dados pessoais
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public String submeterDadosDiscente() throws DAOException {
		
		ListaMensagens mensagens = new ListaMensagens();
		DiscenteValidator.validarDadosDiscenteTecnico(obj, null, discenteAntigo, mensagens);
		erros.addAll(mensagens);
		
		if(hasErrors())
			return null;
		
		GenericDAO dao = getGenericDAO();

		obj.setFormaIngresso( dao.findByPrimaryKey(
				obj.getFormaIngresso().getId(), FormaIngresso.class) );
		obj.setTipoRegimeAluno( dao.findByPrimaryKey( 
				obj.getTipoRegimeAluno().getId(), TipoRegimeAluno.class) );
		obj.setEstruturaCurricularTecnica(dao.findByPrimaryKey(
				obj.getEstruturaCurricularTecnica().getId(), EstruturaCurricularTecnica.class));
		obj.setTurmaEntradaTecnico( dao.findByPrimaryKey( 
				obj.getTurmaEntradaTecnico().getId(), TurmaEntradaTecnico.class) );
				
		if( obj.getId() > 0 )
			obj.setPessoa( dao.findAndFetch(obj.getPessoa().getId(), Pessoa.class, "estadoCivil", "tipoRedeEnsino", 
				"tipoRaca", "unidadeFederativa", "pais", "contaBancaria") );

		obj.setCurso( obj.getTurmaEntradaTecnico().getCursoTecnico() );
		
		return telaResumo();
	}

	/** Redireciona para a busca de discentes, a fim de realizar a operação de atualização de dados.<br>
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/tecnico/menus/discente.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, 
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		prepareMovimento( SigaaListaComando.ALTERAR_DISCENTE_TECNICO);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_DISCENTE_TECNICO.getId());
		initObj();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_TECNICO);
		return buscaDiscenteMBean.popular();
	}
	
	
	/** Redireciona para a busca de discentes, a fim de realizar somente consulta aos dados do discente.<br>
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/tecnico/menus/consultas.jsp</li>
	 * </ul>
	 */
	@Override
	public String listar() throws ArqException {
		checkRole(SigaaPapeis.PEDAGOGICO_TECNICO);
		initObj();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DISCENTE_TECNICO);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Seleciona o discente e redireciona para o formulário de alteração de discente.
	 * Chamado por {@link BuscaDiscenteMBean#redirecionarDiscente(Discente)}
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 * 
	 * JSP: Não invocado por jsp.
	 */
	public String selecionaDiscente() throws ArqException {
		popular();
		obj.setHasVariosCursos(false);
		if (getAcessoMenu().isPedagogico()) {
			return submeterDadosDiscente();
		} else {
			carregarCurriculosTurmasEntrada(null);
			popularCombos();
			setOperacaoAtiva(SigaaListaComando.ALTERAR_DISCENTE_TECNICO.getId());
			return telaDadosDiscente();
		}
	}

	/**
	 * Seta o discente
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj = (DiscenteTecnico) discente;
	}

	/** Retorna o formulário de dados do discente.
 	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/discente/form.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 */
	private String telaDadosDiscente() throws DAOException {
		EstruturaCurricularTecDao dao = getDAO(EstruturaCurricularTecDao.class);
		try {
			clearMensagens();
			if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId(),
					SigaaListaComando.ALTERAR_DISCENTE_TECNICO.getId()) )
				return cancelar();
			
		} finally {
			dao.close();
		}
		return forward( "/ensino/tecnico/discente/form.jsp");
	}
	
	/** Retorna o formulário de dados do discente.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/lato/discente/form.jsp</li>
	 * </ul>
	 */
	public String telaDadosPessoais() {
		
		clearMensagens();
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_DISCENTE.getId(),
				SigaaListaComando.ALTERAR_DISCENTE_TECNICO.getId()) )
			return cancelar();
		
		return forward( "/geral/pessoa/dados_pessoais.jsp");
	}
	
	/** Retorna o formulário de dados do discente.
	 * @return /ensino/discente/form.jsp
	 */
	private String telaResumo() {
		return forward( "/ensino/tecnico/discente/resumo.jsp");
	}
	
	
	/**
	 * Direciona o usuário para a tela de busca de discentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/sigaa.war/ensino/tecnico/discente/resumo.jsp.</li>
	 * </ul>
	 */
	public String telaBuscaDiscentes() {
		return forward("/graduacao/busca_discente.jsp");
	}

	/** Retorna a coleção de SelectItem de um curso para o discente . 
	 * @return
	 */
	public List<SelectItem> getCursosCombo() {
		return cursosCombo;
	}

	/** Retorna a lista dos possíveis currículos do discente. 
	 * @return
	 */
	public List<SelectItem> getCurriculosCombo() {
		return curriculosCombo;
	}

	
	/** Seta a coleção de SelectItem de um curso para o discente.
	 * @param possiveisCursos
	 */
	public void setCursosCombo(List<SelectItem> cursosCombo) {
		this.cursosCombo = cursosCombo;
	}

	/** Retorna a coleção de SelectItem das formas de ingresso possíveis para o discente. 
	 * @return
	 */
	public Collection<SelectItem> getFormasIngressoCombo() {
		return formasIngressoCombo;
	}

	/** Seta a coleção de SelectItem das formas de ingresso possíveis para o discente.  
	 * @param formasIngressoCombo
	 */
	public void setFormasIngressoCombo(List<SelectItem> formasIngressoCombo) {
		this.formasIngressoCombo = formasIngressoCombo;
	}

	/** Indica se é discente antigo. 
	 * @return
	 */
	public boolean isDiscenteAntigo() {
		return discenteAntigo;
	}

	/** Seta se é discente antigo. 
	 * @param discenteAntigo
	 */
	public void setDiscenteAntigo(boolean discenteAntigo) {
		this.discenteAntigo = discenteAntigo;
	}

	/** Retorna a lista dos possíveis status discente. 
	 * @return
	 */
	public List<SelectItem> getStatusCombo() {
		return statusCombo;
	}

	/** Seta a lista dos possíveis status discente. 
	 * @param possiveisStatus
	 */
	public void setStatusCombo(List<SelectItem> statusCombo) {
		this.statusCombo = statusCombo;
	}

	public void setCurriculosCombo(List<SelectItem> curriculosCombo) {
		this.curriculosCombo = curriculosCombo;
	}

	public List<SelectItem> getTurmasCombo() {
		return turmasCombo;
	}

	public void setTurmasCombo(List<SelectItem> turmasCombo) {
		this.turmasCombo = turmasCombo;
	}

	public List<SelectItem> getRegimesAlunosCombo() {
		return regimesAlunosCombo;
	}

	public void setRegimesAlunosCombo(List<SelectItem> regimesAlunosCombo) {
		this.regimesAlunosCombo = regimesAlunosCombo;
	}

	public List<SelectItem> getProcessosSeletivosCombo() {
		return processosSeletivosCombo;
	}

	public void setProcessosSeletivosCombo(List<SelectItem> processosSeletivosCombo) {
		this.processosSeletivosCombo = processosSeletivosCombo;
	}

}