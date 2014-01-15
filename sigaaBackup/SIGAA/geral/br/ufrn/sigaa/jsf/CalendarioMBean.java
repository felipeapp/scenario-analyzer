/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '06/11/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.CalendarioAcademicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.EventoExtraSistema;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ConvenioAcademico;
import br.ufrn.sigaa.ensino.jsf.TrancamentoMatriculaUtil;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.negocio.CalendarioMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

/**
 * MBean responsável pelas operações com calendários acadêmicos
 * @author amdantas
 */
@Component("calendario")
@Scope("session")
public class CalendarioMBean extends SigaaAbstractController<CalendarioAcademico> {

	/** Representa o caminho para a página de definição dos parâmatros para o calendário */
	private static final String JSP_PARAMETROS = "/administracao/calendario_academico/parametros.jsp";
	/** Representa o caminho para a página dos calendário */
	private static final String JSP_CALENDARIOS = "/administracao/calendario_academico/calendarios.jsp";
	/** Representa o caminho para a página de consulta de calendário */
	private static final String JSP_CONSULTA_CALENDARIOS = "/administracao/calendario_academico/consulta.jsp";
	/** Representa o caminho para a página de view */
	private static final String JSP_VIEW = "/administracao/calendario_academico/view.jsp";	
	/** atributo de bean usado em jsp em um combobox */
	private List<SelectItem> comboCursos;
	
	/**
	 * Auxilia na visualização dos dados.
	 */
	private CalendarioAcademico calendario = new CalendarioAcademico();

	/**
	 * lista de calendários carregados de uma unidade para o usuário escolher
	 * qual manipular
	 */
	private Collection<CalendarioAcademico> calendarios = new HashSet<CalendarioAcademico>();

	/**
	 * objeto que é inserido e removido da lista de eventos extra do calendário
	 * manipulado
	 */
	private EventoExtraSistema extra = new EventoExtraSistema();

	/**
	 * lista de eventos extras removidos a serem tratados no processador
	 */
	private Collection<EventoExtraSistema> extrasRemovidos = new ArrayList<EventoExtraSistema>();
	/** boolean para verificar se é para aparecer uma aba */
	private boolean abaEventosAcademicos;
	/** usado para habilitar botao */
	private boolean habilitarVoltar = true;
	/** usado para verificar se usuario pode alterar o parâmetro unidade  */
	private boolean podeAlterarUnidade = false;
	/** usado para verificar se usuario pode alterar o parâmetro nível  */
	private boolean podeAlterarNivel = false;
	/** usado para verificar se usuario pode alterar o parâmetro modalidade  */
	private boolean podeAlterarModalidade = false;
	/** usado para verificar se usuario pode alterar o parâmetro convênio  */
	private boolean podeAlterarConvenio = false;
	/** usado para verificar se usuario pode alterar o parâmetro curso  */
	private boolean podeAlterarCurso = true;

	public boolean isHabilitarVoltar() {
		return habilitarVoltar;
	}

	public void setHabilitarVoltar(boolean habilitarVoltar) {
		this.habilitarVoltar = habilitarVoltar;
	}

	/**
	 * Construtor da classe.
	 */
	public CalendarioMBean() {
		initObj();
	}

	/**
	 * Inicializa os objetos.
	 */
	private void initObj() {
		obj = new CalendarioAcademico();
		obj.setConvenio(new ConvenioAcademico());
		obj.setModalidade(new ModalidadeEducacao());
		obj.setCurso(new Curso());
		comboCursos = new ArrayList<SelectItem>(0);
		abaEventosAcademicos = true;
		calendarios.clear();
	}

	public List<SelectItem> getComboCursos() {
		return comboCursos;
	}

	public void setComboCursos(List<SelectItem> comboCursos) {
		this.comboCursos = comboCursos;
	}

	public EventoExtraSistema getExtra() {
		return extra;
	}

	public void setExtra(EventoExtraSistema extra) {
		this.extra = extra;
	}

	public Collection<CalendarioAcademico> getCalendarios() {
		return calendarios;
	}

	public Collection<SelectItem> getCalendariosCombo() {
		return toSelectItems(getCalendarios(), "id", "anoPeriodoVigente");
	}

	public void setCalendarios(Collection<CalendarioAcademico> calendarios) {
		this.calendarios = calendarios;
	}

	public Collection<EventoExtraSistema> getExtrasRemovidos() {
		return extrasRemovidos;
	}

	public void setExtrasRemovidos(Collection<EventoExtraSistema> extrasRemovidos) {
		this.extrasRemovidos = extrasRemovidos;
	}


	/**
	 * entrada do caso de uso sem ter escolhido unidade ou nível
	 * Método chamado pelas seguintes JSPs: 
	 *        /administracao/menus/administracao.jsp
	 *        /administracao/menu_administracao.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String iniciar() throws SegurancaException, DAOException {
		try {			
			checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO.getId());
			initObj();
			
			permissoes(true,true,true,true,true);
			
		}		
		catch (RuntimeNegocioException e) {			
			obj.setUnidade(getGenericDAO().findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class));
			permissoes(true,true,true,true,true);
		}
			
		return telaParametros();
	}
	
	/**
	 * 
	 * Seta as permissões.
	 * 
	 * @param podeAlterarNivel
	 * @param podeAlterarConvenio
	 * @param podeAlterarModalidade
	 * @param podeAlterarUnidade
	 * @param abaEventosAcademicos
	 */
	private void permissoes(Boolean podeAlterarNivel,Boolean podeAlterarConvenio,Boolean podeAlterarModalidade,Boolean podeAlterarUnidade,Boolean abaEventosAcademicos) {
		this.podeAlterarNivel = podeAlterarNivel;
		this.podeAlterarConvenio = podeAlterarConvenio;
		this.podeAlterarModalidade = podeAlterarModalidade;
		this.podeAlterarUnidade = podeAlterarUnidade;
		this.abaEventosAcademicos = abaEventosAcademicos;
	}


	/**
	 * entrada do caso de uso com unidade e nível escolhido
	 * Método chamado pela seguinte JSPs: /graduacao/menus/administracao.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarDAE() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO.getId());
		abaEventosAcademicos = true;
		habilitarVoltar = true;
		initObj();
		GenericDAO dao = getGenericDAO();
		obj.setUnidade(dao.findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class));
		obj.setNivel(NivelEnsino.GRADUACAO);
		podeAlterarUnidade = false;
		podeAlterarNivel = false;
		podeAlterarModalidade = true;
		podeAlterarConvenio = true;
		return telaParametros();
	}

	/**
	 * entrada do caso de uso com unidade e nível escolhido
	 * Método chamado pela seguinte JSP: /stricto/menus/permissao.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarPPG() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.PPG);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO.getId());
		abaEventosAcademicos = false;
		habilitarVoltar = true;
		initObj();
		GenericDAO dao = getGenericDAO();
		obj.setUnidade(dao.findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class));
		obj.setNivel(NivelEnsino.STRICTO);
		podeAlterarUnidade = true;
		podeAlterarNivel = false;
		podeAlterarModalidade = false;
		podeAlterarConvenio = false;
		podeAlterarCurso = ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO);
		extrasRemovidos = new ArrayList<EventoExtraSistema>();
		obj.getModalidade().setDescricao("Nenhum");
		
		return telaParametros();
	}

	/**
	 * entrada do caso de uso com unidade e nível escolhido
	 * Método chamado pela seguinte JSP: /stricto/menus/menu_coordenador.jsp
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciarProgramasPos() throws SegurancaException, DAOException {		
		checkRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO.getId());
		calendarios = new ArrayList<CalendarioAcademico>();
		obj = new CalendarioAcademico();
		obj.setUnidade( getProgramaStricto() );
		obj.setNivel(NivelEnsino.STRICTO);
		obj.setAtivo(true);		
		obj.setCurso(new Curso());
		CursoDao daoCurso = getDAO(CursoDao.class);
		comboCursos = toSelectItems(daoCurso.findByUnidade(getProgramaStricto().getId(), obj.getNivel()),"id", "descricao");		
		abaEventosAcademicos = false;
		habilitarVoltar = true;
		podeAlterarUnidade = false;
		podeAlterarNivel = false;
		podeAlterarModalidade = false;
		podeAlterarConvenio = false;
		podeAlterarCurso = ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO);
		extrasRemovidos = null;		
		return telaParametros();		
	}

	/**
	 * entrada do caso de uso com unidade e nível escolhido
	 * Método chamado pela seguinte JSP: /WEB-INF/jsp/ensino/tecnico/menu/curso.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarTecnico() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_TECNICO);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO.getId());
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		
		Collection<CalendarioAcademico> calendariosBanco = dao.findByUnidade(getUnidadeGestora(), NivelEnsino.TECNICO);
		if(ValidatorUtil.isEmpty(calendariosBanco)) {
			calendarios = new ArrayList<CalendarioAcademico>();						
		} else {
			calendarios = calendariosBanco;
		}		
		abaEventosAcademicos = true;
		podeAlterarModalidade = false;
		CalendarioAcademico calendarioVigente = null;
		
		for(CalendarioAcademico c : calendariosBanco ) {
			if (c.isVigente()) {
				calendarioVigente = c;
				break;
			}
		}
		
		if(ValidatorUtil.isEmpty(calendarioVigente)) {
			obj = new CalendarioAcademico();
			obj.setNivel(NivelEnsino.TECNICO);
			obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
		} else {
			obj = calendarioVigente;
		}				
		if( obj!=null && obj.getEventosExtra() != null )
			obj.getEventosExtra().iterator();
		
		
		if( !ValidatorUtil.isEmpty(obj) && !calendarios.contains(obj) )		
			calendarios.add(obj);
		
		habilitarVoltar = false;
		
		if(obj.getId() != 0) {		
			setConfirmButton("Alterar Calendário");
		} else {
			setConfirmButton("Cadastrar Calendário");
		}
		
		return telaCalendarios();
	}

	/**
	 * Verfica se o usuário possui permissão e então popula as informações
	 * necessárias para iniciar a operação sobre os calendários de nível
	 * Formação Complementar. 
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>/sigaa.war/ensino/formacao_complementar/menus/curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarFormacaoComplementar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO.getId());
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);

		UnidadeGeral unidadePapel = getUsuarioLogado().getPermissao(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR).iterator().next().getUnidadePapel();
		calendarios = dao.findByUnidade(unidadePapel.getId(), NivelEnsino.FORMACAO_COMPLEMENTAR);
		abaEventosAcademicos = true;
		podeAlterarModalidade = false;
		
		CalendarioAcademico calendarioVigente = null;
		
		for(CalendarioAcademico c : calendarios) {
			if (c.isVigente()) {
				calendarioVigente = c;
				break;
			}
		}
		
		if(ValidatorUtil.isEmpty(calendarioVigente)) {
			obj = new CalendarioAcademico();
			obj.setNivel(NivelEnsino.FORMACAO_COMPLEMENTAR);
			obj.setUnidade(dao.findByPrimaryKey(unidadePapel.getId(), Unidade.class));
		} else {
			obj = calendarioVigente;
		}
		if( obj!=null && obj.getEventosExtra() != null )
			obj.getEventosExtra().iterator();
		if( !ValidatorUtil.isEmpty(obj) && !calendarios.contains(obj) )		
			calendarios.add(obj);
		habilitarVoltar = false;
		if(obj.getId() != 0) {		
			setConfirmButton("Alterar Calendário");
		} else {
			setConfirmButton("Cadastrar Calendário");
		}
		return telaCalendarios();
	}

	/**
	 * entrada do caso de uso com unidade e nível escolhido
	 * Método chamado pela seguinte JSP: /ead/menu.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarEAD() throws ArqException {
		checkRole(SigaaPapeis.SEDIS);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO.getId());
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);

		abaEventosAcademicos = false;
		habilitarVoltar = true;
		initObj();
		obj.setUnidade(dao.findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class));
		obj.setNivel(NivelEnsino.GRADUACAO);
		obj.setModalidade( dao.findByPrimaryKey( ModalidadeEducacao.A_DISTANCIA , ModalidadeEducacao.class) );
		podeAlterarUnidade = false;
		podeAlterarNivel = false;
		podeAlterarModalidade = false;
		podeAlterarConvenio = false;
		carregarCursosPorModalidade(null);
		return telaParametros();
	}

	/**
	 * Encaminha para a página principal dos calendários
	 * Este método não é chamado por JSPs. 
	 * @return
	 */
	public String telaCalendarios() {
		return forward(JSP_CALENDARIOS);
	}

	/**
	 * Encaminha para a página de parâmetros
	 * Este método não é chamado por JSPs.
	 * @return
	 */
	public String telaParametros() {
		return forward(JSP_PARAMETROS);
	}

	/**
	 * Carrega o calendário selecionado.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/calendarios.jsp
	 * @param evento
	 * @throws DAOException
	 */
	public void carregarCalendario(ValueChangeEvent evento) throws DAOException {
		GenericDAO dao = getGenericDAO();
		extrasRemovidos = new ArrayList<EventoExtraSistema>();
		int idEscolhido = Integer.parseInt(evento.getNewValue().toString());
		
		if (idEscolhido == 0) {
			setConfirmButton("Cadastrar Calendário");
			// parâmetros
			char nivel = obj.getNivel();
			ConvenioAcademico convenio = obj.getConvenio();
			ModalidadeEducacao modalidade = obj.getModalidade();
			Curso curso = obj.getCurso();
			Unidade unidade = obj.getUnidade();
			obj = new CalendarioAcademico();
			obj.setNivel(nivel);
			obj.setConvenio(convenio);
			obj.setModalidade(modalidade);
			obj.setCurso(curso);
			obj.setUnidade(unidade);
		} else {
			obj = dao.findAndFetch(idEscolhido, CalendarioAcademico.class,"curso");
			if (obj.getEventosExtra() == null || obj.getEventosExtra().isEmpty())
				obj.setEventosExtra(new HashSet<EventoExtraSistema>());
			setConfirmButton("Alterar Calendário");
		}

	}

	/**
	 * Adiciona evento extra no calendário
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/calendarios.jsp
	 * @param evento
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public void addExtra(ActionEvent evento) throws ArqException, NegocioException {
		
		extra.setCalendario(obj);
		
		erros = extra.validate(); 
		if(!erros.isEmpty()) {
			addMensagens(erros);
			return;
		}
				
		prepareMovimento(ArqListaComando.CADASTRAR);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(extra);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		execute(mov);
		obj.getEventosExtra().add(extra);
		erros = new ListaMensagens();
		addMessage("Evento incluído com sucesso", TipoMensagemUFRN.INFORMATION);
		extra = new EventoExtraSistema();
		abaEventosAcademicos = true;
	}

	/**
	 * remove evento extra do calendário
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/calendarios.jsp
	 * @param evento
	 */
	public void remExtra(ActionEvent evento) {
		extra = new EventoExtraSistema();
		
		extra.setId(getParameterInt("id"));
		extra.setDescricao(getParameter("desc"));
		
		obj.getEventosExtra().remove(extra);
		addMessage("Evento removido com sucesso", TipoMensagemUFRN.INFORMATION);
		if (extrasRemovidos == null)
			extrasRemovidos = new HashSet<EventoExtraSistema>();
		extrasRemovidos.add(extra);
		extra = new EventoExtraSistema();
		abaEventosAcademicos = true;
	}

	/**
	 * Confirma o cadastro.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/calendarios.jsp
	 * @return
	 * @throws ArqException 
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String confirmar() throws ArqException {
		
		if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO.getId(),SigaaListaComando.ALTERAR_CALENDARIO_ACADEMICO.getId())) {
			return cancelar();
		}		
		
		erros = new ListaMensagens();
		ListaMensagens lista = obj.validate();
			
		if (lista != null) 
			erros.addAll(lista.getMensagens());
		
		
		if (hasErrors())
			return null;

		/** se o usuário for COORD ou SECRETARIO de programa e o calendário carregado for de STRICTO GLOBAL
		 * deve criar outro calendário especifico do programa do usuário */
		if(isPortalCoordenadorStricto() && isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS ) && obj.getUnidade().getId() == Unidade.UNIDADE_DIREITO_GLOBAL ){
			obj.setUnidade( getProgramaStricto() );
			obj.setId(0);
		}

		Comando comando = null;
		String msg = "Calendário alterado com sucesso!";
		if (obj.getId() == 0) {
			comando = SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO;
			msg = "Calendário criado com sucesso!";
			if (isEmpty(calendarios))
				obj.setVigente(true);
			obj.setAtivo(true);
		} else {
			comando = SigaaListaComando.ALTERAR_CALENDARIO_ACADEMICO;
		}
		try {
			prepareMovimento(comando);
			CalendarioMov mov = new CalendarioMov();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(comando);
			mov.setExtrasRemovidos(extrasRemovidos); // só é usado no caso de  atualização
			setarParametrosIndefinidosParaNulo();
			obj = (CalendarioAcademico) executeWithoutClosingSession(mov, getCurrentRequest());
			CalendarioAcademico cal;
			if (obj.isVigente())
				cal = obj;
			else 
				// retorna o calendário do programa ativo caso for Stricto, senão retorna o calendário do
				// usuário logado.
				cal = getCalendarioVigenteProgramaAtivo();
			
			//Evita que um calendário acadêmico cujo curso esteja definido seja 
			//inserido na sessão sem o curso associado, evitando erros de Lazy.
			cal = getGenericDAO().findAndFetch(cal.getId(), CalendarioAcademico.class, "curso");
			getCurrentSession().setAttribute(SigaaAbstractController.CALENDARIO_SESSAO, cal);
			
			if(!isEmpty(cal) && NivelEnsino.isAlgumNivelStricto(cal.getNivel()) && isPortalCoordenadorStricto()) {
				
				if(isEmpty(cal.getCurso()))
					getCurrentSession().setAttribute(SigaaAbstractController.CURSO_ATUAL_COORDENADOR_STRICTO, null);
				else
					getCurrentSession().setAttribute(SigaaAbstractController.CURSO_ATUAL_COORDENADOR_STRICTO, cal.getCurso());
				
			}
			
			erros = new ListaMensagens();
			addMessage(msg, TipoMensagemUFRN.INFORMATION);

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}

		if (isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
			return iniciarDAE();
		
		return cancelar();
	}

	/**
	 * Carrega a página de calendários
	 * Não é chamado por JSP.
	 * @return
	 */
	public String carregar() {
		// identificar tipo de usuário
		return forward(JSP_PARAMETROS);
	}
	
	/**
	 * Redireciona para a tela de busca de calendários acadêmicos.
	 * Método chamado pelas seguintes JSPs:
	 * <ul> 
	 *    <li> /stricto/menu_coordenador.jsp </li>
	 *    <li> /portais/docente/menu_docente.jsp </li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 */
	public String iniciarBusca() throws DAOException{
		initObj();
		obj.setNivel(getUsuarioLogado().getNivelEnsino());
		if (isNivelStricto()){
			if (isPortalCoordenadorStricto())
				obj.setUnidade(getProgramaStricto());
			else
				obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());		
			return buscarCalendarios();
		}else if (isMedio(getUsuarioLogado().getNivelEnsino())) { 
			obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());	
			return buscarCalendarios();
		}else{
			GenericDAO dao = getGenericDAO();
			CalendarioAcademicoDao daoCal = getDAO(CalendarioAcademicoDao.class);
			try{
				obj.setUnidade(dao.findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class));				
				if (isPortalDiscente()){
					if (getUsuarioLogado().getDiscente().isRegular()){
						obj.setModalidade(getUsuarioLogado().getDiscente().getCurso().getModalidadeEducacao());
						obj.setConvenio(getUsuarioLogado().getDiscente().getCurso().getConvenio());
						if (daoCal.existeCalendarioEspecificoComConvenio(
								getUsuarioLogado().getDiscente().getCurso().getId(),
								getUsuarioLogado().getDiscente().getNivel() ))		
							obj.setCurso(getUsuarioLogado().getDiscente().getCurso());
					}
					return buscarCalendarios(); 
				} else {
					if(isPortalDocente()){
						podeAlterarUnidade = true;
						podeAlterarNivel = true;
					}
					return forward(JSP_CONSULTA_CALENDARIOS);
				}								
			}finally {
				if (dao != null)
					dao.close();
			}
			
		}
	}
	
	/**
	 * Buscar todos os calendários do programa ou curso, dependendo de onde acessado.
	 * Método chamado pela seguinte JSP:
	 * <ul> 
	 * 	  <li> /administracao/calendario_academico/consulta.jsp </li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String buscarCalendarios() throws DAOException{
		CalendarioAcademicoDao cDao = getDAO(CalendarioAcademicoDao.class);		
		
		try{
			CalendarioAcademico cal = new CalendarioAcademico();
			cal.setUnidade(obj.getUnidade());
			cal.setModalidade(obj.getModalidade());
			cal.setConvenio(obj.getConvenio());
			cal.setNivel(obj.getNivel());
			cal.setCurso(obj.getCurso());
			if (obj.getNivel() != NivelEnsino.TECNICO){
				if (obj.getCurso().getId() > 0) {
					if( obj.getModalidade().isADistancia() 	&& obj.getCurso() != null
							&& obj.getCurso().getId() != Curso.CURSO_ADMINISTRACAO_A_DISTANCIA ){
						cal.setConvenio(null);
						cal.setNivel(NivelEnsino.GRADUACAO);
						cal.setCurso( null );
					}else{
						cal.setModalidade(null);
						cal.setConvenio(null);
						cal.setCurso(cDao.findByPrimaryKey(obj.getCurso().getId(), Curso.class));
					}
				} else if (obj.getModalidade().getId() > 0 || obj.getConvenio().getId() > 0) {
					cal.setCurso(null);
					cal.setUnidade(cDao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
					if (obj.getModalidade().getId() > 0) {
						cal.setModalidade(cDao.findByPrimaryKey(obj.getModalidade().getId(), ModalidadeEducacao.class));
					} else {
						cal.setModalidade(null);
						cal.setConvenio(cDao.findByPrimaryKey(obj.getConvenio().getId(), ConvenioAcademico.class));
					}
				} else {
					cal.setModalidade(null);
					cal.setConvenio(null);
					cal.setCurso(null);
					cal.setUnidade(cDao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
				}
			}
			else {
				cal.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
			}
			if (isPortalDocente()){
				
				if(!isEmpty(cal.getCurso()) && !isEmpty(cal.getNivel()) && NivelEnsino.isAlgumNivelStricto(cal.getNivel())
						&& ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO) ) {
					cal.setNivel(cal.getCurso().getNivel());
				}
				
				calendarios = cDao.findCalendariosByParametrosDocente(cal.getUnidade(), cal.getNivel(), cal.getModalidade(),
		 			     cal.getConvenio(), cal.getCurso());
			}
			else {
				calendarios = cDao.findCalendariosByParametros(cal.getUnidade(), cal.getNivel(), cal.getModalidade(),
		 			     cal.getConvenio(), cal.getCurso());
			}

			if (calendarios.isEmpty()){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		} finally {
			if (cDao != null)
				cDao.close();
		}
		
		return forward(JSP_CONSULTA_CALENDARIOS);
	}
	
	/**
	 * Redireciona para a tela de visualização do calendário acadêmico.
	 * Método chamado pela seguinte JSP:
	 * <ul> 
	 * 	  <li> /administracao/calendario_academico/consulta.jsp </li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String view() throws DAOException{
		setId();
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		try{
			calendario = dao.findByPrimaryKey(obj.getId(), CalendarioAcademico.class);
		}finally{
			if (dao != null)
				dao.close();
		}
		return forward(JSP_VIEW);
	}
	

	/**
	 * Seleciona a unidade Gestora
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/parametros.jsp
	 * @param e
	 * @throws DAOException 
	 */
	public void selecionarGestora(ValueChangeEvent e) throws DAOException {
		if (e.getNewValue() == null) {
			addMensagemErro("Escolha uma Unidade Gestora Acadêmica");
			return;
		}
		obj.setUnidade(new Unidade((Integer) e.getNewValue()));
		if( (SigaaSubsistemas.STRICTO_SENSU.equals( getSubSistema() )&& isUserInRole(SigaaPapeis.PPG)) || (isPortalDocente() && podeAlterarUnidade) ){
			carregarCursosPorUnidadeNivel(e);
			obj.setModalidade(new ModalidadeEducacao());
			obj.getModalidade().setDescricao("Nenhum");
		}	
	}

	/**
	 * Carrega Cursos por Modalidade.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/parametros.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void carregarCursosPorModalidade(ValueChangeEvent e) throws DAOException {
		obj.setCurso(new Curso());
		obj.setConvenio(new ConvenioAcademico());
		comboCursos = new ArrayList<SelectItem>(0);
		Integer idModalidade = null;
		if( e != null )
			idModalidade = (Integer) e.getNewValue();
		else if ( !isEmpty( obj.getModalidade() ) ) {
			idModalidade = obj.getModalidade().getId();
		}else
			return;
		CursoDao dao = getDAO(CursoDao.class);
		
		ModalidadeEducacao modalidade = dao.findByPrimaryKey(idModalidade, ModalidadeEducacao.class);
		if(ValidatorUtil.isEmpty(modalidade)) {
			obj.setModalidade(new ModalidadeEducacao());
		} else {
			obj.setModalidade(modalidade);
		}
		comboCursos = toSelectItems(dao.findByModalidadeEducacao(idModalidade, obj.getNivel()),
					"id", "descricao");
	}

	/**
	 * Carrega Cursos por Convênio.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/parametros.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void carregarCursosPorConvenio(ValueChangeEvent e) throws DAOException {
		obj.setCurso(new Curso());
		obj.setModalidade(new ModalidadeEducacao());
		comboCursos = new ArrayList<SelectItem>(0);
		if (e.getNewValue() == null) {
			return;
		}
		obj.setConvenio(new ConvenioAcademico((Integer) e.getNewValue()));
		CursoDao dao = getDAO(CursoDao.class);
		comboCursos = toSelectItems(dao.findByConvenioAcademico((Integer) e.getNewValue(), obj.getNivel()),
				"id", "descricao");
	}
	
	/**
	 * Carrega Cursos por Unidade e Nível de Ensino.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/parametros.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void carregarCursosPorUnidadeNivel(ValueChangeEvent e) throws DAOException {
		obj.setCurso(new Curso());
		comboCursos = new ArrayList<SelectItem>(0);
		if (e.getNewValue() == null) {
			return;
		}
		CursoDao dao = getDAO(CursoDao.class);
		comboCursos = toSelectItems(dao.findByUnidade((Integer) e.getNewValue(), obj.getNivel()),
				"id", "descricao"); 
			
	}
	
	/**
	 * Carrega a modalidade por Curso.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/parametros.jsp
	 * @param e
	 * @throws DAOException
	 */
	public void carregarModalidadePorCurso(ValueChangeEvent e) throws DAOException {
		obj.setModalidade( new ModalidadeEducacao());
		
		Integer idCurso = null;
		if( e != null )
			idCurso = (Integer) e.getNewValue();
		else if ( !isEmpty( obj.getModalidade() ) ) {
			idCurso = obj.getCurso().getId();
		}else
			return;
		if( idCurso > 0){
			CursoDao dao = getDAO(CursoDao.class);
			obj.setCurso( dao.findByPrimaryKey(idCurso, Curso.class)  );
			if( !obj.getCurso().isStricto() )
				obj.setModalidade(obj.getCurso().getModalidadeEducacao());
		}
	}

	
	
	/**
	 * Seleciona Nível.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/parametros.jsp
	 * @param e
	 * @throws DAOException 
	 */
	public void selecionarNivel(ValueChangeEvent e) throws DAOException {
		if (e.getNewValue() == null)
			addMensagemErro("Escolha Nível de Ensinos");
		else
			obj.setNivel(e.getNewValue().toString().charAt(0));
		obj.setCurso(new Curso());
		obj.setModalidade(new ModalidadeEducacao());
		obj.setConvenio(new ConvenioAcademico());		
		if(isPortalDocente() && podeAlterarUnidade && podeAlterarNivel && !isEmpty(obj.getUnidade()) && !isEmpty(obj.getNivel())) {
			CursoDao dao = getDAO(CursoDao.class);
			comboCursos = toSelectItems(dao.findByUnidade(obj.getUnidade().getId(), obj.getNivel()),"id", "descricao"); 
		} else {
			comboCursos = new ArrayList<SelectItem>(0);
		}

	}

	/**
	 * Submete os Parâmetros alterados.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/parametros.jsp
	 * @return
	 * @throws DAOException
	 */
	public String submeterParametros() throws DAOException {
		
		if(!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_CALENDARIO_ACADEMICO.getId(),SigaaListaComando.ALTERAR_CALENDARIO_ACADEMICO.getId())) {
			return cancelar();
		}
		
		//Por padrão, caso não seja selecionada a unidade responsável pelo calendário, a unidade responsável será a GLOBAL.
		if(obj.getUnidade().getId() == 0) {
			obj.setUnidade(getGenericDAO().findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class));
		}
		
		if (obj.getUnidade().getId() == 0 && !NivelEnsino.isValido( obj.getNivel()) && obj.getModalidade().getId() == 0
				&& obj.getConvenio().getId() == 0 && obj.getCurso().getId() == 0)  {
			addMensagemErro("Preencha alguma das opções");
			return null;
		}
		
		if((NivelEnsino.isAlgumNivelStricto(obj.getNivel()))) {
			Character nivel = ValidatorUtil.isEmpty(obj.getCurso()) ?  NivelEnsino.STRICTO : obj.getCurso().getNivel();
			Curso curso = ValidatorUtil.isEmpty(obj.getCurso()) ? null : obj.getCurso();
			CalendarioAcademico calendarioBanco = CalendarioAcademicoHelper.getCalendarioExato(null, null, obj.getUnidade(), nivel, null, null, curso, null);
			if( ! ValidatorUtil.isEmpty(calendarioBanco)) {
				obj = calendarioBanco;
				if(ValidatorUtil.isEmpty(obj.getCurso())) {
					obj.setCurso(new Curso());
				} else {
					obj.setCurso(getGenericDAO().findByPrimaryKey(obj.getCurso().getId(), Curso.class));
				}
			}
		}

		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		
		Unidade unidadeForm = dao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class);
		
		ModalidadeEducacao modalidadeForm = obj.getModalidade();
		
		ConvenioAcademico convenioForm = obj.getConvenio();
		
		Curso cursoForm = obj.getCurso();
		
		if( obj.getId() > 0 ){
			CalendarioAcademico calTemp = dao.findByPrimaryKey(obj.getId(), CalendarioAcademico.class);			
			if( cursoForm != null && cursoForm.getId() > 0
					&& (calTemp.getCurso() == null || calTemp.getCurso().getId() != cursoForm.getId()) ){
				obj = CalendarioAcademicoHelper.getCalendario(cursoForm);
				if( obj == null || obj.getCurso() == null || ( obj.getCurso().getId() != cursoForm.getId() ) ){
					obj = new CalendarioAcademico();
				}
			}
			obj.setUnidade(unidadeForm);
			obj.setModalidade(modalidadeForm);
			obj.setConvenio(convenioForm);
			obj.setCurso(cursoForm);			
			dao.detach(calTemp);
		}
		
		
		
		if( obj.getId() > 0 )
			dao.lock(obj);
		if (obj.getCurso() != null && obj.getCurso().getId() > 0) {
			if( obj.getModalidade()!=null && obj.getModalidade().isADistancia() && obj.getCurso() != null && obj.getCurso().getId() != Curso.CURSO_ADMINISTRACAO_A_DISTANCIA ){
				obj.setConvenio(null);
				obj.setNivel(NivelEnsino.GRADUACAO);
				obj.setCurso( null );
			}else if (obj.getConvenio() != null && obj.getConvenio().getId() > 0 ){
				obj.setUnidade(dao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
				obj.setModalidade(null);
				obj.setConvenio(null);
				obj.setCurso(dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class));
			} else {
				
				obj.setUnidade(dao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
				if(obj.getModalidade()!=null)				
					obj.setModalidade(dao.findByPrimaryKey(obj.getModalidade().getId(), ModalidadeEducacao.class));
				obj.setConvenio(null);
				obj.setCurso(dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class));
				
				if( NivelEnsino.isAlgumNivelStricto(obj.getNivel()) && 
						ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.CALENDARIO_POR_CURSO) && 
						!ValidatorUtil.isEmpty(obj.getCurso())  ) {
					
					obj.setNivel(dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class).getNivel());
					
				}
				
				
			}
		} else if ( (obj.getModalidade() != null && obj.getModalidade().getId() > 0) 
				|| ( obj.getConvenio() != null && obj.getConvenio().getId() > 0 )) {
			obj.setCurso(null);
			obj.setUnidade(dao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
			if (obj.getModalidade().getId() > 0) {
				obj.setConvenio(null);
				obj.setModalidade(dao.findByPrimaryKey(obj.getModalidade().getId(), ModalidadeEducacao.class));
			} else {
				obj.setModalidade(null);
				obj.setConvenio(dao.findByPrimaryKey(obj.getConvenio().getId(), ConvenioAcademico.class));
			}
		} else {
			obj.setModalidade(null);
			obj.setConvenio(null);
			obj.setCurso(null);
			obj.setUnidade(dao.findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
		}

		calendarios = dao.findTodosByParametros(obj.getUnidade(), obj.getNivel(), obj.getModalidade(),
				obj.getConvenio(), obj.getCurso());

		abaEventosAcademicos = true;
		extrasRemovidos = new ArrayList<EventoExtraSistema>();
		
		if( obj.getId() > 0 )
			setConfirmButton("Alterar Calendário");
		else
			setConfirmButton("Cadastrar Calendário");
		

		return forward(JSP_CALENDARIOS);
	}

	/**
	 * Volta para a página de parâmetros.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/calendarios.jsp
	 * @return
	 * @throws ArqException
	 */
	public String voltarParametros() throws ArqException {
		obj = new CalendarioAcademico();

		if (SigaaSubsistemas.GRADUACAO.equals(getSubSistema()) && isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE)) {
			return iniciarDAE();
		} if( SigaaSubsistemas.SEDIS.equals( getSubSistema() )&& isUserInRole(SigaaPapeis.SEDIS) ){
			return iniciarEAD();
		} if( SigaaSubsistemas.STRICTO_SENSU.equals( getSubSistema() )&& isUserInRole(SigaaPapeis.PPG) ){			
			return telaParametros();
		}if( SigaaSubsistemas.PORTAL_COORDENADOR_STRICTO.equals( getSubSistema() )&& isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) ){
			return iniciarProgramasPos();
		}
		return forward(JSP_PARAMETROS);
	}
	
	
	
	/**
	 * Método usado para setar para nulo os objetos não definidos. Evita erro TransientObjectException ao persistir/atualizar.
	 */
	private void setarParametrosIndefinidosParaNulo(){
		if(ValidatorUtil.isEmpty(obj.getUnidade())) {
			obj.setUnidade(null);
		}
		if(ValidatorUtil.isEmpty(obj.getModalidade())) {
			obj.setModalidade(null);
		}
		if(ValidatorUtil.isEmpty(obj.getCurso())) {
			obj.setCurso(null);
		}
		if(ValidatorUtil.isEmpty(obj.getConvenio())) {
			obj.setConvenio(null);
		}
		if(ValidatorUtil.isEmpty(obj.getNivel())) {
			obj.setNivel(getNivelEnsino());
		}		
		
	}

	/**
	 * SelectItem com todos os níveis.
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/parametros.jsp
	 * @return
	 */
	public Collection<SelectItem> getComboNiveis() {
		SelectItem[] itens = new SelectItem[] { new SelectItem(NivelEnsino.INFANTIL + "", "INFANTIL"),
				new SelectItem(NivelEnsino.MEDIO + "", "MÉDIO"),
				new SelectItem(NivelEnsino.TECNICO + "", "TÉCNICO"),
				new SelectItem(NivelEnsino.GRADUACAO + "", "GRADUAÇÃO"),
				new SelectItem(NivelEnsino.LATO + "", "LATO-SENSU"),
				new SelectItem(NivelEnsino.STRICTO + "", "STRICTO"),
				new SelectItem(NivelEnsino.MESTRADO + "", "MESTRADO"),
				new SelectItem(NivelEnsino.DOUTORADO + "", "DOUTORADO") };
		return Arrays.asList(itens);
	}
	
	public ArrayList<EventoExtraSistema> getEventosExtraOrdenados () {
		ArrayList<EventoExtraSistema> eventosOrdenados = new ArrayList<EventoExtraSistema>();

		if (obj.getEventosExtra() != null ){
		
			Iterator<EventoExtraSistema> it = obj.getEventosExtra().iterator();
			while (it.hasNext()){
				eventosOrdenados.add(it.next());
			}
			
			if ( eventosOrdenados != null ) {
				Collections.sort(eventosOrdenados, new Comparator<EventoExtraSistema>(){
					public int compare(EventoExtraSistema e1, EventoExtraSistema e2) {
						
						if (e1.getInicio() == null || e1.getFim() == null || e2.getInicio() == null || e2.getFim() == null ) {
							return e1.getDescricao().compareToIgnoreCase(e2.getDescricao());
						}
						
						if (e1.getInicio().before(e2.getInicio()))
							return -1;
						else if (e1.getInicio().after(e2.getInicio()))
							return 1;
						else return e1.getFim().compareTo(e2.getFim());
					}
				});
			}
		}
		return eventosOrdenados;
	}
	
	/**
	 * Retorna o calendário vigente do programa que está ativo no momento.<br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>stricto/coordenacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public CalendarioAcademico getCalendarioVigenteProgramaAtivo() throws DAOException, NegocioException{
		CalendarioAcademicoDao dao = getDAO(CalendarioAcademicoDao.class);
		try {
			Unidade unidade = (Unidade) getCurrentSession().getAttribute(SigaaAbstractController.PROGRAMA_ATUAL);
			Curso cursoAtual = (Curso) getCurrentSession().getAttribute(SigaaAbstractController.CURSO_ATUAL_COORDENADOR_STRICTO);
			CalendarioAcademico cal;
			if (unidade != null && isPortalCoordenadorStricto())	{
				
				if( cursoAtual == null ){
					cal = CalendarioAcademicoHelper.getCalendario(unidade);
				}else{					
					cal = CalendarioAcademicoHelper.getCalendario(cursoAtual);
				}
				
			}else{
				cal = CalendarioAcademicoHelper.getCalendario(getUsuarioLogado());
				unidade = getUsuarioLogado().getUnidade();
			}
			
			//Evita setar um calendário nulo na sessão. Isso ocorre quando um programa não for gestora acadêmica dele mesmo.
			//No caso, o calendário da sessão vai permanescer o geral do nível de ensino, setado anteriormente ao Entrar na area Stricto(EntrarStrictoAction).
			if(cal != null){			
				getCurrentSession().setAttribute("calendarioAcademico", cal);
				cal.setInicioTrancamentoTurma(cal.getInicioPeriodoLetivo());
				cal.setFimTrancamentoTurma(TrancamentoMatriculaUtil.calcularPrazoLimiteTrancamentoTurma(unidade, cal));
			}
			
			return cal;
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Verificar se o nível do usuário logado é Stricto Sensu. 
	 * Método chamado pela seguinte JSP: /administracao/calendario_academico/consulta.jsp
	 * @return
	 */
	public boolean isNivelStricto(){
		return NivelEnsino.isAlgumNivelStricto(getUsuarioLogado().getNivelEnsino());
	}
	
	/**
	 * Verificar se o nível do usuário logado é de ensino médio. 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>NENHUMA JSP</li>
	 * </ul>
	 * @return
	 */
	public static boolean isMedio(Character nivel) {
		return nivel != null && nivel.charValue() == NivelEnsino.MEDIO;
	}

	public boolean isAbaEventosAcademicos() {
		return abaEventosAcademicos;
	}

	public void setAbaEventosAcademicos(boolean abaEventosAcademicos) {
		this.abaEventosAcademicos = abaEventosAcademicos;
	}

	public boolean isPodeAlterarUnidade() {
		return podeAlterarUnidade;
	}

	public void setPodeAlterarUnidade(boolean podeAlterarUnidade) {
		this.podeAlterarUnidade = podeAlterarUnidade;
	}

	public boolean isPodeAlterarNivel() {
		return podeAlterarNivel;
	}

	public void setPodeAlterarNivel(boolean podeAlterarNivel) {
		this.podeAlterarNivel = podeAlterarNivel;
	}

	public boolean isPodeAlterarModalidade() {
		return podeAlterarModalidade;
	}

	public void setPodeAlterarModalidade(boolean podeAlterarModalidade) {
		this.podeAlterarModalidade = podeAlterarModalidade;
	}

	public boolean isPodeAlterarConvenio() {
		return podeAlterarConvenio;
	}

	public void setPodeAlterarConvenio(boolean podeAlterarConvenio) {
		this.podeAlterarConvenio = podeAlterarConvenio;
	}

	public CalendarioAcademico getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	public boolean isPodeAlterarCurso() {
		return podeAlterarCurso;
	}

	public void setPodeAlterarCurso(boolean podeAlterarCurso) {
		this.podeAlterarCurso = podeAlterarCurso;
	}


}