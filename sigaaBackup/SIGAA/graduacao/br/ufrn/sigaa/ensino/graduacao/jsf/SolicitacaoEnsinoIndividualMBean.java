/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * 22/06/2007
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoEnsinoIndividualDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.negocio.SolicitacaoEnsinoIndividualValidator;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean para solicita��o de ensino individual de aluno
 * @author leonardo
 * @author Victor Hugo
 */
@Component("solicitacaoEnsinoIndividual") @Scope("session")
public class SolicitacaoEnsinoIndividualMBean extends SigaaAbstractController<SolicitacaoEnsinoIndividual> {

	/** JSP utilizada para o formul�rio de solicita��o de ensino individual. */
	public static final String JSP_FORM = "/graduacao/solicitacao_ensino_individual/form.jsp";
	/** JSP utilizada para o comprovante da solicita��o de ensino individual. */
	public static final String JSP_COMPROVANTE = "/graduacao/solicitacao_ensino_individual/comprovante.jsp";
	/** JSP utilizada para a opera��o de negar solicita��o de ensino individual. */
	public static final String JSP_NEGAR_SOLICITACAO = "/graduacao/solicitacao_ensino_individual/negar_solicitacao.jsp";

	/** Cole��o utilizada para armazenar componentes curriculares a serem utilizados na opera��o.*/
	private Collection<ComponenteCurricular> componentes =  new ArrayList<ComponenteCurricular>(0);

	/** Cole��o utilizada para armazenar componentes equivalentes */
	private Map<ComponenteCurricular, ComponenteCurricular> equivalentes =  new HashMap<ComponenteCurricular, ComponenteCurricular>();	
	
	/** atributos utilizados para emiss�o do comprovante de solicita��o de aproveitamento */
	private Collection<SolicitacaoEnsinoIndividual> solicitacoes = new ArrayList<SolicitacaoEnsinoIndividual>();

	/** Atributo do discente solicitante do ensino individualizado.*/
	private DiscenteAdapter discente;

	/**
	 * este atributo controla se esta sendo executado opera��o sobre turmas de f�rias ou ensino individualizado
	 */
	private boolean ferias = false;
	private Collection<ComponenteCurricular> ccObrigatorios;

	public SolicitacaoEnsinoIndividualMBean(){
		initObj();
	}

	/**
	 * inicializa o obj
	 */
	private void initObj(){
		obj = new SolicitacaoEnsinoIndividual();
		obj.setComponente(new ComponenteCurricular());
		componentes = new HashSet<ComponenteCurricular>();
		solicitacoes = new HashSet<SolicitacaoEnsinoIndividual>();
		resultadosBusca = new ArrayList<SolicitacaoEnsinoIndividual>();
		equivalentes = new HashMap<ComponenteCurricular, ComponenteCurricular>();
	}

	/**
	 * Inicia o caso de uso de solicita��o de ensino individualizado, realizado pelo discente.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarEnsinoIndividualizado() throws DAOException {
		if (!getCalendarioVigente().isPeriodoRequerimentoEnsinoIndiv()){
			addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("Solicita��o de Ensino Individualizado",
					getCalendarioVigente().getInicioRequerimentoEnsinoIndiv(), getCalendarioVigente().getFimRequerimentoEnsinoIndiv()));
			return null;
		}
		ferias = false;
		return iniciar();
	}

	/**
	 * Inicia requerimento de turma de f�rias, realizado pelo discente.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarFerias() throws DAOException {
		if (!getCalendarioVigente().isPeriodoRequerimentoTurmaFerias()){
			addMensagemErro(CalendarioAcademico.getDescricaoPeriodo("Solicita��o de turma de f�rias",
					getCalendarioVigente().getInicioRequerimentoTurmaFerias(), getCalendarioVigente().getFimRequerimentoTurmaFerias()));
			return null;
		}

		if( getCalendarioVigente().getAnoFeriasVigente() == null || getCalendarioVigente().getPeriodoFeriasVigente() == null ){
			addMensagem(MensagensGraduacao.ANO_PERIODO_FERIAS_NAO_DEFINIDO_CALENDARIO_ACADEMICO);
			return null;
		}
		ferias = true;
		return iniciar();
	}

	/**
	 * Inicia o caso de uso de solicita��o de ensino individual realizado pelo DISCENTE.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/solicitacoes_ensino_individual.jsp</li>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciar() throws DAOException {
		initObj();
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
  		discente = getGenericDAO().findByPrimaryKey(discente.getId(), Discente.class);
  		
		if( discente == null ){
			addMensagemErro("O usu�rio n�o possui discente associado.");
			return null;
		}

		if( !discente.isGraduacao() ){
			addMensagemErro("Apenas discentes de gradua��o podem realizar esta opera��o.");
			return null;
		}

		if( discente.getStatus() != StatusDiscente.ATIVO && discente.getStatus() != StatusDiscente.FORMANDO && discente.getStatus() != StatusDiscente.TRANCADO ){
			addMensagemErro("Apenas discentes de ATIVOS, FORMANDOS e TRANCADOS podem solicitar ensino individualizado.");
			return null;
		}
		
		
		if (!getUsuarioLogado().getDiscenteAtivo().isRegular()) {
			addMensagemErro("A solicita��o de ensino individual � permitida apenas para alunos regulares.");
			return null;
		}
		
		ccObrigatorios = getDAO(EstruturaCurricularDao.class).findComponentesObrigatoriosByCurriculo(discente.getCurriculo().getId());
		
		return formBusca();
	}
	
	/** Redirecionao o usu�rio para o formul�rio de busca por componente curricular.<br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_ensino_individual/confirma_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formBusca() {
		return forward(JSP_FORM);
	}

	/**
	 * Realiza a busca de componentes.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_ensino_individual/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String buscarComponente() throws ArqException {
		
		setComponentes(new ArrayList<ComponenteCurricular>());
		setEquivalentes(new HashMap<ComponenteCurricular, ComponenteCurricular>());
		String param = getParameter("paramBusca");
		
		if (param == null) {
			addMensagemErro("Selecione um tipo de busca e digite o par�metro de busca");
			return null;
		}

		setOperacaoAtiva(SigaaListaComando.SOLICITAR_ENSINO_INDIVIDUAL.getId());
		

  		consultarComponentes(param);

		
		if( !isFerias() && isEmpty( componentes ) && isEmpty(equivalentes) ){
			addMensagemErro("N�o foi encontrada disciplina obrigat�ria com esse c�digo no seu curr�culo.");
		}
		
		resultadosBusca = new ArrayList<SolicitacaoEnsinoIndividual>();
		
		for (ComponenteCurricular cc : componentes) {
			SolicitacaoEnsinoIndividual solicitacao = new SolicitacaoEnsinoIndividual();
			solicitacao.setComponente(cc);
			resultadosBusca.add(solicitacao);
		}

		for (Entry<ComponenteCurricular, ComponenteCurricular> entry : equivalentes.entrySet()) {
			SolicitacaoEnsinoIndividual solicitacao = new SolicitacaoEnsinoIndividual();
			solicitacao.setComponente(entry.getKey());
			solicitacao.setEquivalente(entry.getValue());
			resultadosBusca.add(solicitacao);
		}
		
		prepareMovimento(SigaaListaComando.SOLICITAR_ENSINO_INDIVIDUAL);

		return null;
	}

	private void consultarComponentes(String param) throws ArqException {
		if (ValidatorUtil.isEmpty(ccObrigatorios))
			ccObrigatorios = getDAO(EstruturaCurricularDao.class).findComponentesObrigatoriosByCurriculo(discente.getCurriculo().getId());
		
		consultarObrigatorias(param);
  		if (getComponentes().isEmpty())
  			consultarEquivalentes(param);
  		if (getComponentes().isEmpty())
  			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
	}

	private void consultarEquivalentes(String param) throws ArqException {
		String codigo = obj.getComponente().getCodigo();
		
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		ComponenteCurricular compEq = dao.findByCodigoNivel(codigo, NivelEnsino.GRADUACAO);
		
		if (compEq != null) {
			for (ComponenteCurricular cc : ccObrigatorios) {
				
				if (isNotEmpty(cc.getEquivalencia()) && ExpressaoUtil.eval(cc.getEquivalencia(), compEq))
					equivalentes.put(compEq, cc);
			}
		}
		
	}

	private void consultarObrigatorias(String param) throws DAOException {
  		ComponenteCurricularDao ccDao = getDAO(ComponenteCurricularDao.class);
  		
		if("codigo".equalsIgnoreCase(param)){
			String codigo = obj.getComponente().getCodigo();
			if(codigo == null || "".equals(codigo.trim())){
				addMensagemErro("Informe um c�digo para a busca.");
				return ;
			}
			if( isFerias() )
				setComponentes( ccDao.findByCodigo(codigo)  );
				//c = dao.findComponenteObrigatorioByCodigo(discente, codigo, false);
			else{
				
				for (ComponenteCurricular cc : ccObrigatorios) {
					if (cc.getCodigo().equals(codigo)) {
						getComponentes().add(cc);
						break;
					}
				}
			}
			
			
		} else if ("nome".equalsIgnoreCase(param)) {
			String nome = obj.getComponente().getDetalhes().getNome();
			if(nome == null || "".equals(nome.trim())){
				addMensagemErro("Informe um nome para a busca.");
				return ;
			} else if(nome.trim().length() < 3){
				addMensagemErro("O nome deve possuir no m�nimo 3 caracteres");
				return ;
			}
			if( isFerias() )
				setComponentes( ccDao.findByNome(nome, null)  );
			else {
				for (ComponenteCurricular cc : ccObrigatorios) {
					if (cc.getNome().indexOf(nome) != -1) {
						getComponentes().add(cc);
					}
				}				
			}
		} else
			setComponentes(null);
	}


	/**
	 * submete a solicita��o de ensino individual para o processador persistir
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_ensino_individual/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarComponenteSolicitacao() throws ArqException {

		CalendarioAcademico cal = getCalendarioVigente();
		if( !checkOperacaoAtiva(SigaaListaComando.SOLICITAR_ENSINO_INDIVIDUAL.getId()) ){
			return cancelar();
		}

		// recupera o componente selecionado
		int id = obj.getComponente().getId();
		for (SolicitacaoEnsinoIndividual sol : resultadosBusca) {
			if (sol.getComponente().getId() == id) {
				obj = sol;
				break;
			}
		}
		
		if( (obj.getComponente().isAtividade() && (obj.getComponente().getFormaParticipacao() != null && !obj.getComponente().getFormaParticipacao().isEspecialColetiva()) )|| obj.getComponente().isSubUnidade() ){
			addMensagemErro("N�o � poss�vel solicitar turmas de componentes do tipo atividade ou subunidade de componentes de bloco.");
			return null;
		}
		
		obj.setDiscente( (DiscenteGraduacao) getUsuarioLogado().getDiscenteAtivo() );
		obj.setDataSolicitacao(new Date());
		obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
		obj.setSituacao(SolicitacaoEnsinoIndividual.SOLICITADA);
		if( ferias ){
			if( cal.getAnoFeriasVigente() == null || cal.getPeriodoFeriasVigente() == null ){
				addMensagemErro(MensagensGraduacao.ANO_PERIODO_FERIAS_NAO_DEFINIDO_CALENDARIO_ACADEMICO);
				return null;
			}
			obj.setTipo( Turma.FERIAS );
			obj.setAno( cal.getAnoFeriasVigente() );
			obj.setPeriodo( cal.getPeriodoFeriasVigente() );
		} else{
			obj.setTipo( Turma.ENSINO_INDIVIDUAL );
			obj.setAno( cal.getAno() );
			obj.setPeriodo( cal.getPeriodo() );
		}

		ListaMensagens lista = new ListaMensagens();
		SolicitacaoEnsinoIndividualValidator.validarSolicitacao(obj, lista);
		validaExpressaoHorario(lista);
		if( !lista.isEmpty() ){
			addMensagens(lista);
			return null;
		}
		return forward("/graduacao/solicitacao_ensino_individual/confirma_solicitacao.jsp");
	}
	/**
	 * submete a solicita��o de ensino individual para o processador persistir
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_ensino_individual/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterSolicitacao() throws ArqException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.SOLICITAR_ENSINO_INDIVIDUAL);
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			removeOperacaoAtiva();
			addMessage("Solicita��o cadastrada com sucesso!", TipoMensagemUFRN.INFORMATION);
			return forward("/graduacao/solicitacao_ensino_individual/concluido.jsp");
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return null;
		}

	}
	
	/** valida a express�o de hor�rio sugerida.
	 * @param lista
	 * @throws ArqException
	 */
	private void validaExpressaoHorario(ListaMensagens lista) throws ArqException {
		if (isEmpty(obj.getSugestaoHorario())) return;
		String expressao = obj.getSugestaoHorario().toUpperCase();
		HorarioDao dao = getDAO(HorarioDao.class);
		List<HorarioTurma> horarios = null;
		CalendarioAcademico cal = getCalendarioVigente();
		int idUnidade = getUnidadeGestora();
		String[] horariosMarcados = null;
		List<Horario> horariosGrade = new ArrayList<Horario>();
		try {
			horarios = HorarioTurmaUtil.parseCodigoHorarios(expressao, idUnidade, obj.getComponente().getNivel(), dao);
			for (HorarioTurma ht : horarios) {
				ht.setDataInicio(cal.getInicioPeriodoLetivo());
				ht.setDataFim(cal.getFimPeriodoLetivo());
			}
			horariosMarcados = HorarioTurmaUtil.parseHorarios(horarios, horariosGrade);
		} finally {
			// verifica se os hor�rio foi totalmente convertido
			if (isEmpty(horariosMarcados) || isEmpty(horarios)) {
				lista.addErro("Express�o de hor�rio inv�lida");
				return;
			}
			Turma turmaTemp = new Turma();
			turmaTemp.setDisciplina(obj.getComponente());
			turmaTemp.setDataInicio(cal.getInicioPeriodoLetivo());
			turmaTemp.setDataFim(cal.getFimPeriodoLetivo());
			turmaTemp.setHorarios(new ArrayList<HorarioTurma>());
			List<HorarioTurma> listaHorariosMarcados = HorarioTurmaUtil.extrairHorariosEscolhidos(horariosMarcados, turmaTemp, horariosGrade, cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo());
			for (HorarioTurma ht : listaHorariosMarcados) {
				ht.setDataInicio(cal.getInicioPeriodoLetivo());
				ht.setDataFim(cal.getFimPeriodoLetivo());
				turmaTemp.getHorarios().add(ht);
			}
			String expressaoReversa = HorarioTurmaUtil.formatarCodigoHorarios(turmaTemp);
			if (!isEmpty(expressaoReversa)) {
				List<HorarioTurma> horariosReverso = HorarioTurmaUtil.parseCodigoHorarios(expressaoReversa, idUnidade, obj.getComponente().getNivel(), dao);
				if (horarios != null) Collections.sort(horarios);
				if (horariosReverso != null) Collections.sort(horariosReverso);
				if (horarios != null && horariosReverso != null && horarios.size() == horariosReverso.size()) {
					for (int i = 0; i < horarios.size(); i++ ) {
						if (horarios.get(i).equals(horariosReverso.get(i)))
							lista.addErro("O hor�rio informado ("
							+ expressao
							+ ") n�o foi totalmente convertido no hor�rio da grade. Por favor, verifique a express�o do hor�rio informada.");
					}
				}
			}
			// verifica os domingos
			boolean domingo = false;
			if (horarios != null) {
				Iterator<HorarioTurma> iterator = horarios.iterator();
				while (iterator.hasNext()) {
					HorarioTurma horario = iterator.next();
					if (!obj.getComponente().isLato() && Integer.parseInt(""+horario.getDia()) == Calendar.SUNDAY) {
						domingo = true;
						iterator.remove();
					}
				}
			}
			if (domingo) {
				horariosMarcados = HorarioTurmaUtil.parseHorarios(horarios, horariosGrade);
				lista.addErro("N�o � poss�vel definir o hor�rio para o domingo.");
			}
		}
	}

	/**
	 * Inicia o caso de uso realizado pela coordena��o do curso de engar solicita��o de turma
	 * Encaminha para o formul�rio onde a coordena��o ir� entrar com uma justificativa da nega��o.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/solicitacoes_ensino_individual.jsp</li>
	 * </ul> 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarNegaSolicitacao() throws ArqException{
		
		checkRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO );
		initObj();
		
		Integer id = getParameterInt("id_solicitacao");
		if( id == null || id == 0 ){
			addMensagemErroPadrao();
			return null;
		}
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class);
		obj = dao.findByPrimaryKey(id, SolicitacaoEnsinoIndividual.class);
		
		if( obj.isFerias() ){
			if (!getCalendarioVigente().isPeriodoSolicitacaoTurmaFerias()){
				addMensagemErro( "N�o � poss�vel negar esta solicita��o de turma. " + CalendarioAcademico.getDescricaoPeriodo( "Solicita��o de turma de f�rias",
						getCalendarioVigente().getInicioSolicitacaoTurmaFerias(), getCalendarioVigente().getFimSolicitacaoTurmaFerias()));
				return null;
			}
			ferias = true;
		}else{
			if (!getCalendarioVigente().isPeriodoSolicitacaoTurmaEnsinoIndiv()){
				addMensagemErro("N�o � poss�vel negar esta solicita��o de turma. " + CalendarioAcademico.getDescricaoPeriodo("Solicita��o de Ensino Individualizado",
						getCalendarioVigente().getInicioSolicitacaoTurmaEnsinoIndiv(), getCalendarioVigente().getFimSolicitacaoTurmaEnsinoIndiv()));
				return null;
			}
			ferias = false;
		}
		
		/** caso o par�metro todas seja true ent�o � porque o coordenador deseja negar todas as solicita��es de 
		 * f�rias ou ensino individual de um componente simultaneamente. Entrando com a mesma justificativa para todas.
		 * Neste caso todas as solicita��es de f�rias ou ensino individual com a situa��o pendentes ser�o negadas. */
		Boolean todas = getParameterBoolean("todas");
		if( todas != null && todas ){
			/** carregando outros discentes que tamb�m realizaram solicita��o de ensino individual da mesma disciplina */
			solicitacoes = dao.findByComponenteAnoPeriodoSituacao(getCursoAtualCoordenacao().getId(), obj.getComponente().getId(), obj.getAno(), obj.getPeriodo(), SolicitacaoEnsinoIndividual.SOLICITADA);
			Collections.sort((List<SolicitacaoEnsinoIndividual>) solicitacoes);
		}
		if (getCursoAtualCoordenacao() == null) {
			addMensagemErro("O curso atual coordenado n�o est� definido. Reinicie o procedimento utilizando os links do sistema.");
			return cancelar();
		}
		prepareMovimento(SigaaListaComando.CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL);
		return forward(JSP_NEGAR_SOLICITACAO);
	}
	
	/**
	 * Retorna o usu�rio � lista de solicita��es de turma individual/f�rias.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_ensino_individual/negar_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String formListaSolicitacoes() {
		return forward("/graduacao/solicitacao_turma/solicitacoes_ensino_individual.jsp");
	}
	
	/**
	 * Cancela a solicita��o de ensino individual.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_ensino_individual/lista.jsp</li>
	 * <li>/sigaa.war/graduacao/solicitacao_ensino_individual/negar_solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String cancelarSolicitacao() throws ArqException{

		GenericDAO dao = getGenericDAO();
		SolicitacaoEnsinoIndividual solicitacao = null;
		
		if( getDiscenteUsuario() == null ) {
			/** caso seja o coordenador est� vindo da p�gina de entrar com a justificativa */
			checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO );
			String justificativa = obj.getJustificativaNegacao();
			
			solicitacao = dao.refresh(obj);
			if( isEmpty( solicitacoes ) ){ /** negando apenas uma solicita��o de ensino individual ou f�rias */
				solicitacao.setJustificativaNegacao(justificativa);
			} else{
				/** caso a cole��o de solicita��es n�o seja vazia  � porque o coordenador deseja negar TODAS as solicita��es de 
				 * f�rias ou ensino individual de um componente simultaneamente. Entrando com a mesma justificativa para todas.
				 * Neste caso todas as solicita��es de f�rias ou ensino individual com a situa��o PENDENTE ser�o negadas. */
				for( SolicitacaoEnsinoIndividual sei : solicitacoes ){
					sei.setJustificativaNegacao( justificativa );
				}
			}
			
		} else {
			/** caso seja o discente que est� cancelando a sua solicita��o de ensino individual ou f�rias */
			Integer id = getParameterInt("id");
			if( id == null || id == 0 ){
				addMensagemErroPadrao();
				return null;
			}
			solicitacao = dao.findByPrimaryKey(id, SolicitacaoEnsinoIndividual.class);

			if( solicitacao.isFerias() ){
				if (!getCalendarioVigente().isPeriodoRequerimentoTurmaFerias()){
					addMensagemErro( "N�o � poss�vel cancelar esta solicita��o de turma." + CalendarioAcademico.getDescricaoPeriodo( "Solicita��o de turma de f�rias",
							getCalendarioVigente().getInicioRequerimentoTurmaFerias(), getCalendarioVigente().getFimRequerimentoTurmaFerias()));
					return null;
				}
			}else{
				if (!getCalendarioVigente().isPeriodoRequerimentoEnsinoIndiv()){
					addMensagemErro("N�o � poss�vel cancelar esta solicita��o de turma." + CalendarioAcademico.getDescricaoPeriodo("Solicita��o de Ensino Individualizado",
							getCalendarioVigente().getInicioRequerimentoEnsinoIndiv(), getCalendarioVigente().getFimRequerimentoEnsinoIndiv()));
					return null;
				}
			}
			
			prepareMovimento(SigaaListaComando.CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL);
		}

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento( SigaaListaComando.CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL );
		mov.setObjMovimentado( solicitacao );
		if( !isEmpty( solicitacoes ) )
			mov.setColObjMovimentado(solicitacoes);

		try {
			execute(mov, getCurrentRequest());
			addMessage("A solicita��o de turma de " + (solicitacao.isFerias() ? "f�rias" : "ensino individual") + " foi cancelada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
		}

		if( getDiscenteUsuario() == null ) {
			/**
			 * se for coordena��o de curso deve retornar para a lista de solicita��es
			 */
			SolicitacaoTurmaMBean solMBean = getMBean( "solicitacaoTurma" );
			if( solicitacao.isFerias() ){
				return solMBean.iniciarSolicitacaoFerias();
			}else {
				return solMBean.iniciarSolicitacaoEnsinoIndividual();
			}
		}else{
			if( solicitacao.isFerias() )
				return listarFerias();
			else
				return listar();
		}
	}

	/**
	 * Exibe para o discente o comprovante de suas solicita��es de acordo com o tipo informado.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String emitirComprovante() throws DAOException{
		DiscenteAdapter disc = getUsuarioLogado().getDiscenteAtivo();
		if( disc == null ){
			addMensagemErro("O usu�rio n�o possui discente associado.");
			return null;
		}

		if( !disc.isGraduacao() ){
			addMensagemErro("Apenas discentes de gradua��o podem realizar esta opera��o.");
			return null;
		}

		int tipoTurma = Turma.ENSINO_INDIVIDUAL;
		int ano = getCalendarioVigente().getAno();
		int periodo = getCalendarioVigente().getPeriodo();
		if( ferias ){
			tipoTurma = Turma.FERIAS;
			ano = getCalendarioVigente().getAnoFeriasVigente();
			periodo = getCalendarioVigente().getPeriodoFeriasVigente();
		}

		SolicitacaoEnsinoIndividualDao dao = getDAO( SolicitacaoEnsinoIndividualDao.class );
		solicitacoes = dao.findByDiscenteComponenteAnoPeriodoSituacao(disc.getId(), tipoTurma, null, ano, periodo ,null);
		if( isEmpty(solicitacoes) ){
			addMensagemErro("O Discente n�o possui nenhuma solicita��o de " + getTipoSolicitacao() + " pendente.");
			return null;
		}
		this.discente = disc;

		return forward(JSP_COMPROVANTE);
	}

	/**
	 * Exibe para o discente o comprovante de suas solicita��es de ensino individual.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String emitirComprovanteEnsinoIndividual() throws DAOException{
		ferias = false;
		return emitirComprovante();
	}
	
	/**
	 * Exibe para o discente o comprovante de suas solicita��es de f�rias
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String emitirComprovanteFerias() throws DAOException{
		ferias = true;
		return emitirComprovante();
	}

	/**
	 * Este m�todo � invocado pelo coordenador do curso para revogar uma solicita��o de turma 
	 * que foi negada por ele, caso ele decida atendar a solicita��o.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/solicitacoes_ensino_individual.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String retornarSolicitacao() throws ArqException{
		checkRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
		
		if (!isPortalCoordenadorGraduacao()) {
			addMensagemErro("Voc� utilizou o bot�o 'voltar' do navegador, o que n�o � recomendado. Por favor, reinicie os procedimentos!");
			return cancelar();
		}
		
		Integer id = getParameterInt("id_solicitacao");
		if( id == null || id == 0 ){
			addMensagemErroPadrao();
			return null;
		}
		GenericDAO dao = getGenericDAO();
		SolicitacaoEnsinoIndividual solicitacao = dao.findByPrimaryKey(id, SolicitacaoEnsinoIndividual.class);

		/**
		 * s� pode retornar solicita��es de ferias ou ensino individualizado se tiver no per�odo de 
		 * solicita��o de turmas de f�rias ou ensino individualizado respectivamente
		 */
		if( solicitacao.isFerias() ){
			if (!getCalendarioVigente().isPeriodoSolicitacaoTurmaFerias()){
				addMensagemErro( "N�o � poss�vel negar esta solicita��o de turma. " + CalendarioAcademico.getDescricaoPeriodo( "Solicita��o de turma de f�rias",
						getCalendarioVigente().getInicioSolicitacaoTurmaFerias(), getCalendarioVigente().getFimSolicitacaoTurmaFerias()));
				return null;
			}
		}else{
			if (!getCalendarioVigente().isPeriodoSolicitacaoTurmaEnsinoIndiv()){
				addMensagemErro("N�o � poss�vel negar esta solicita��o de turma. " + CalendarioAcademico.getDescricaoPeriodo("Solicita��o de Ensino Individualizado",
						getCalendarioVigente().getInicioSolicitacaoTurmaEnsinoIndiv(), getCalendarioVigente().getFimSolicitacaoTurmaEnsinoIndiv()));
				return null;
			}
		}
		
		prepareMovimento(SigaaListaComando.RETORNAR_SOLICITACAO_ENSINO_INDIVIDUAL);

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento( SigaaListaComando.RETORNAR_SOLICITACAO_ENSINO_INDIVIDUAL );
		mov.setObjMovimentado( solicitacao );

		try {
			execute(mov, getCurrentRequest());
			addMessage("A solicita��o de turma de " + (solicitacao.isFerias() ? "f�rias" : "ensino individual") + " foi retornada com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
		}

		/**
		 * se for coordena��o de curso deve retornar para a lista de solicita��es
		 */
		SolicitacaoTurmaMBean solMBean = getMBean( "solicitacaoTurma" );
		if( solicitacao.isFerias() ){
			return solMBean.iniciarSolicitacaoFerias();
		}else {
			return solMBean.iniciarSolicitacaoEnsinoIndividual();
		}
	}
	
	/**
	 * Exibe a lista de solicita��es de turmas de f�rias.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String listarFerias() throws ArqException {
		ferias = true;
		return listar();
	}
	
	/**
	 * Exibe a lista de solicita��es de ensino individual.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String listarEnsinoIndividual() throws ArqException {
		ferias = false;
		return listar();
	}

	@Override
	public String getDirBase() {
		return "/graduacao/solicitacao_ensino_individual";
	}

	/**
	 * Retorna as solicita��es de ensino individual realizadas pelo discente logado.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_ensino_individual/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoesEnviadas() throws DAOException{
		if( getDiscenteUsuario() == null )
			return null;
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class);
		if( ferias )
			return dao.findByDiscenteAnoPeriodo(getDiscenteUsuario().getId(), Turma.FERIAS, getCalendarioVigente().getAnoFeriasVigente(), getCalendarioVigente().getPeriodoFeriasVigente(), null);
		else
			return dao.findByDiscenteAnoPeriodo(getDiscenteUsuario().getId(), Turma.ENSINO_INDIVIDUAL, getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), null);
	}

	public Collection<ComponenteCurricular> getComponentes() {
		return componentes;
	}

	public void setComponentes(Collection<ComponenteCurricular> componentes) {
		this.componentes = componentes;
	}

	public boolean isPeriodoRegular(){
		return getCalendarioVigente().isPeriodoMatriculaRegular();
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Collection<SolicitacaoEnsinoIndividual> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	/** M�todo respons�vel por retornar a descri��o do tipo de solicita��o de turma.*/
	public String getTipoSolicitacao(){
		if(ferias)
			return "f�rias";
		else
			return "ensino individual";
	}

	public boolean isFerias() {
		return ferias;
	}

	public void setFerias(boolean ferias) {
		this.ferias = ferias;
	}

	public Map<ComponenteCurricular, ComponenteCurricular> getEquivalentes() {
		return equivalentes;
	}

	public void setEquivalentes(
			Map<ComponenteCurricular, ComponenteCurricular> equivalentes) {
		this.equivalentes = equivalentes;
	}

}
