/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 08/11/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.GrupoHorarios;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.jsf.HorarioTurmaMBean;
import br.ufrn.sigaa.ensino.jsf.OperadorHorarioTurma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Controller respons�vel por opera��es de sugest�o de solicita��es de turma.
 * 
 * @author Rafael Gomes
 */
@Component("sugestaoSolicitacaoTurma") @Scope("session")
public class SugestaoSolicitacaoTurmaMBean extends SolicitacaoTurmaMBean implements OperadorHorarioTurma{
	
	/** Define o link para o formul�rio de solicita��o de dados da turma. */
	public static final String JSP_SUGESTAO_SOLICITACAO_DADOS = "/graduacao/solicitacao_turma/dados_sugestao_solicitacao.jsp";
	
	/** Define o link para o formul�rio de resumo dos dados da turma. */
	public static final String JSP_SUGESTAO_SOLICITACAO_RESUMO = "/graduacao/solicitacao_turma/resumo_sugestao.jsp";
	/**
	 * Construtor Padr�o 
	 */
	public SugestaoSolicitacaoTurmaMBean() {
		initObj();
	}
	
	/** Inicializa os atributos do controller. */
	private void initObj() {
		obj = new SolicitacaoTurma();
		obj.setUnidade(new Unidade());
		obj.setComponenteCurricular( new ComponenteCurricular() );
		obj.setDiscentes( new ArrayList<DiscentesSolicitacao>() );
		obj.setCurso( new Curso() );
		gerarGradeHorarios();
		setConfirmButton("Cadastrar");
		setAno( getCalendarioVigente().getAno() );
		setPeriodo( getCalendarioVigente().getPeriodo() );
		setModelGrupoHorarios( new ListDataModel() );
		grupoHorarios = new ArrayList<GrupoHorarios>();
		turma = null;
	}
	
	/**
	 * Inicia caso de uso de solicita��o de turma REGULAR.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarSugestaoTurma() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO);
		initObj();
		if( getCalendarioVigente() == null ){
			addMensagemErroPadrao();
			return null;
		}
		// para o chefe, h� um per�odo de sugest�o diferente do per�odo de solicita��o do coordenador
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			if (getCalendarioVigente().getInicioSugestaoTurmaChefe() == null || getCalendarioVigente().getFimSugestaoTurmaChefe() == null)
				addMensagemErro("O per�odo para sugest�o de turmas n�o foi definido no calend�rio atual." );
			else if (!getCalendarioVigente().isPeriodoSugestaoTurmaChefe())
				addMensagemErro("N�o � permitido realizar sugest�o de abertura de turma fora do per�odo " +
						"de solicita��o de turma determinado no calend�rio universit�rio." );
		} else if( !getCalendarioVigente().isPeriodoSolicitacaoTurma() && !getCalendarioVigente().isPeriodoCadastroTurmas() ){
			addMensagemErro("N�o � permitido realizar sugest�o de abertura de turma fora do per�odo " +
					"de solicita��o de turma determinado no calend�rio universit�rio." );
		}

		if( getSubSistema().equals(SigaaSubsistemas.GRADUACAO) &&  getCursoAtualCoordenacao() == null ){
			addMensagemErroPadrao();
		}
		
		if (hasErrors()) return null;
		
		// solicita��o de turma regular
		this.obj.setTipo( Turma.REGULAR );

		if( getCalendarioVigente().getAnoNovasTurmas() != null ){
			this.obj.setAno( getCalendarioVigente().getAnoNovasTurmas().shortValue() ) ;
		}else{
			this.obj.setAno( (short) getCalendarioVigente().getProximoAnoPeriodoRegular().getAno() );
		}

		if( getCalendarioVigente().getPeriodoNovasTurmas() != null ){
			this.obj.setPeriodo( getCalendarioVigente().getPeriodoNovasTurmas().byteValue() );
		} else{
			this.obj.setPeriodo( (byte) getCalendarioVigente().getProximoAnoPeriodoRegular().getPeriodo() );
		}

		if (getCursoAtualCoordenacao() != null) {
			this.obj.setCurso( getCursoAtualCoordenacao() );
		} 

		Turma turma = obj.toTurma();
		
		// Pega o calend�rio do mesmo ano e periodo da turma que est� sendo criado.
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
		
		setPeriodoInicio( cal.getInicioPeriodoLetivo() );
		obj.setCalendario(cal);
		
		setConfirmButton("Cadastrar Sugest�o de Turma");
		ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
		OpcoesBuscaComponente opcao = new OpcoesBuscaComponente();
		opcao.setMBean(this);
		opcao.setTituloOperacao("Sugest�o de Solicita��o de Turma");
		opcao.setSelecionarSubUnidade(true);
		return mBean.buscarComponente(opcao);
	}
	
	/** 
	 * M�todo respons�vel por verificar se o usu�rio � chefe de departamento e se encontra no portal do docente, menu chefia.
	 */
	public boolean isChefeDepartamento(){
		return getAcessoMenu().isChefeDepartamento() && isPortalDocente();
	}
	
	/**
	 * Submete os dados gerais da sugest�o de solicita��o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/dados_solicitacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	@Override
	public String submeterDados() throws ArqException, NegocioException{

		if( obj.getTipo() < 0 )
			addMensagemErro("Tipo da turma indefinido.");

		if( obj.getAno() <= 0 || obj.getAno() < getCalendarioVigente().getAno() )
			addMensagemErro("O ano informado n�o � v�lido. O ano n�o pode ser menor do que o ano atual.");

		if( obj.getPeriodo() <= 0  || obj.getPeriodo() >= 5 )
			addMensagemErro("O per�odo informado n�o � v�lido.");
		
		if( obj.getCurso().getId() <= 0 )
			addMensagemErro("O curso para sugest�o n�o foi informado.");

		if (hasOnlyErrors())
			return null;
		
		GenericDAO dao = getGenericDAO();
		
		obj.setHorarios(HorarioTurmaUtil.parseCodigoHorarios(obj.getHorario(), obj.getUnidade().getId(), getNivelEnsino(), getDAO(HorarioDao.class)));
		if ( obj.getCurso() != null && obj.getCurso().getId() > 0 )
			obj.setCurso(dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class, "id", "nome", "nivel"));
		
		obj.setComponenteCurricular(dao.findByPrimaryKey(obj.getComponenteCurricular().getId(), ComponenteCurricular.class));
		
		if( hasOnlyErrors() )
			return null;

		return formHorarios();

	}
	
	/** Invoca o controller de hor�rio de turma para definir o hor�rio desta solicita��o.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@Override
	public String formHorarios() throws ArqException, NegocioException {
		definePseudoTurma();
		HorarioTurmaMBean horarioBean = getMBean("horarioTurmaBean");
		return horarioBean.populaHorarioTurma(this, "Cadastro de Turma", turma);
	}
	
	/** Invoca o controller respons�vel por definir o hor�rio da turma.<br/>M�todo n�o invocado por JSP�s.
	 * @see br.ufrn.sigaa.ensino.jsf.OperadorHorarioTurma#defineHorariosTurma(java.util.Collection)
	 */
	@Override
	public String defineHorariosTurma(Collection<HorarioTurma> horarios) throws ArqException {
		turma.setHorarios((List<HorarioTurma>) horarios);
		obj.setHorario(HorarioTurmaUtil.formatarCodigoHorarios(turma));
		
		if (!obj.getComponenteCurricular().isPermiteHorarioFlexivel() && !isEmpty(horarios))
			obj.setUnidade(horarios.iterator().next().getHorario().getUnidade());
		
		obj.setHorarios(turma.getHorarios());
		return forward( JSP_SUGESTAO_SOLICITACAO_RESUMO);
	}
	
	/**
	 * Carrega os hor�rios com base na unidade gravada nos par�metros da gestora da acad�mica. 
	 */
	private void gerarGradeHorarios(){
		try {
			HorarioDao dao = getDAO(HorarioDao.class);
			if (getHorariosGrade() == null)
				setHorariosGrade( (List<Horario>) dao.findAtivoByUnidade(new Unidade(getUnidadeGestora()), NivelEnsino.GRADUACAO) );
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
	}
	
	/**
	 * Ap�s a sele��o do componente que ser� solicitado a turma, prepara a solicita��o e inicia o caso e uso,
	 * levando a tela inicial da solicita��o da turma.<br/>M�todo n�o invocado por JSP�s.
	 * @param comp
	 * @return
	 * @throws ArqException
	 */
	public String prepareSolicitacao( ComponenteCurricular comp ) throws ArqException{
		obj.setComponenteCurricular( comp );
		
		setOperacaoAtiva( SigaaListaComando.SUGERIR_SOLICITACAO_TURMA.getId() );
		prepareMovimento(SigaaListaComando.SUGERIR_SOLICITACAO_TURMA);
		SolicitacaoTurmaMBean mBean = getMBean("solicitacaoTurma");
		mBean.setObj(obj);
		return telaDadosGerais();
	}
	
	/** Retorna o link para o formul�rio de solicita��o de dados da turma.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @return
	 */
	@Override
	public String telaDadosGerais(){
		return forward( JSP_SUGESTAO_SOLICITACAO_DADOS );
	}
	
	/** Retorna o link para o formul�rio de resumo dos dados da turma.
	 * <br />
	 * M�todo n�o invocado por JSPs.
	 * 
	 * @return
	 */
	@Override
	public String telaResumo(){
		return forward( JSP_SUGESTAO_SOLICITACAO_RESUMO);
	}
	
	/**
	 * Cadastra a solicita��o de turma.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/resumo.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		String msgSucesso = null;
		try {
			
			// caso seja cadastro de nova solicita��o
			if( !checkOperacaoAtiva( SigaaListaComando.SUGERIR_SOLICITACAO_TURMA.getId() ) ){
				return cancelar();
			}
			
			obj.setCodMovimento(SigaaListaComando.SUGERIR_SOLICITACAO_TURMA);
			obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
			obj.setDataCadastro( new Date() );
			if( obj.isTurmaEnsinoIndividual() || obj.isTurmaFerias() )
				obj.setSolicitacoesEnsinoIndividualAtendidas(getSolicitacoesEnsinoIndividualAtendidas());

			// removendo matrizes com 0 (ZERO) vagas solicitadas 
			for (Iterator<ReservaCurso> it = obj.getReservas().iterator(); it.hasNext();) {
				ReservaCurso rc = it.next();
				if( rc.getVagasSolicitadas() == null || (rc.getVagasSolicitadas() != null && rc.getVagasSolicitadas() <= 0) )
					it.remove();
			}
			msgSucesso = "Sugest�o de turma cadastrada com sucesso!";
		
			
			if(obj.getUnidade() != null) {
				if(obj.getUnidade().getId() == 0) {
					obj.setUnidade(getGenericDAO().findByPrimaryKey(UnidadeGeral.UNIDADE_DIREITO_GLOBAL, Unidade.class));
				}
				else {
					obj.setUnidade(getGenericDAO().findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
				}
			}

			executeWithoutClosingSession(obj, getCurrentRequest());
			
		} catch(NegocioException e) {
			e.printStackTrace();
			addMensagemErro(e.getMessage());
			return null;
		}
		removeOperacaoAtiva();
		addMessage(msgSucesso, TipoMensagemUFRN.INFORMATION);
		return cancelar();

	}
	
	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente)
			throws ArqException {
		ListaMensagens lista = new ListaMensagens();
		
		if (componente.isAtividade()) {
			if (!componente.getDetalhes().isAtividadeAceitaTurma())
				lista.addErro("N�o � poss�vel criar uma turma do componente " + componente.getCodigo() + " porque esta atividade n�o aceita cria��o de turma. Entre em contato com " + ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO) + " para mudar as caracter�sticas do componente.");
				
		}
		return lista;
	}
	
	/**
	 * M�todo respons�vel por redirecionar o usu�rio para a tela de listagem de
	 * componentes curriculares. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente_programa/busca_componente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarComponente(){
		return redirectJSF("graduacao/componente/lista.jsf");
	}
}
