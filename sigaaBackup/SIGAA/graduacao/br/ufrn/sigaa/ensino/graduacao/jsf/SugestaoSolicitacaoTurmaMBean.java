/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Controller responsável por operações de sugestão de solicitações de turma.
 * 
 * @author Rafael Gomes
 */
@Component("sugestaoSolicitacaoTurma") @Scope("session")
public class SugestaoSolicitacaoTurmaMBean extends SolicitacaoTurmaMBean implements OperadorHorarioTurma{
	
	/** Define o link para o formulário de solicitação de dados da turma. */
	public static final String JSP_SUGESTAO_SOLICITACAO_DADOS = "/graduacao/solicitacao_turma/dados_sugestao_solicitacao.jsp";
	
	/** Define o link para o formulário de resumo dos dados da turma. */
	public static final String JSP_SUGESTAO_SOLICITACAO_RESUMO = "/graduacao/solicitacao_turma/resumo_sugestao.jsp";
	/**
	 * Construtor Padrão 
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
	 * Inicia caso de uso de solicitação de turma REGULAR.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		// para o chefe, há um período de sugestão diferente do período de solicitação do coordenador
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			if (getCalendarioVigente().getInicioSugestaoTurmaChefe() == null || getCalendarioVigente().getFimSugestaoTurmaChefe() == null)
				addMensagemErro("O período para sugestão de turmas não foi definido no calendário atual." );
			else if (!getCalendarioVigente().isPeriodoSugestaoTurmaChefe())
				addMensagemErro("Não é permitido realizar sugestão de abertura de turma fora do período " +
						"de solicitação de turma determinado no calendário universitário." );
		} else if( !getCalendarioVigente().isPeriodoSolicitacaoTurma() && !getCalendarioVigente().isPeriodoCadastroTurmas() ){
			addMensagemErro("Não é permitido realizar sugestão de abertura de turma fora do período " +
					"de solicitação de turma determinado no calendário universitário." );
		}

		if( getSubSistema().equals(SigaaSubsistemas.GRADUACAO) &&  getCursoAtualCoordenacao() == null ){
			addMensagemErroPadrao();
		}
		
		if (hasErrors()) return null;
		
		// solicitação de turma regular
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
		
		// Pega o calendário do mesmo ano e periodo da turma que está sendo criado.
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
		
		setPeriodoInicio( cal.getInicioPeriodoLetivo() );
		obj.setCalendario(cal);
		
		setConfirmButton("Cadastrar Sugestão de Turma");
		ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
		OpcoesBuscaComponente opcao = new OpcoesBuscaComponente();
		opcao.setMBean(this);
		opcao.setTituloOperacao("Sugestão de Solicitação de Turma");
		opcao.setSelecionarSubUnidade(true);
		return mBean.buscarComponente(opcao);
	}
	
	/** 
	 * Método responsável por verificar se o usuário é chefe de departamento e se encontra no portal do docente, menu chefia.
	 */
	public boolean isChefeDepartamento(){
		return getAcessoMenu().isChefeDepartamento() && isPortalDocente();
	}
	
	/**
	 * Submete os dados gerais da sugestão de solicitação.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("O ano informado não é válido. O ano não pode ser menor do que o ano atual.");

		if( obj.getPeriodo() <= 0  || obj.getPeriodo() >= 5 )
			addMensagemErro("O período informado não é válido.");
		
		if( obj.getCurso().getId() <= 0 )
			addMensagemErro("O curso para sugestão não foi informado.");

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
	
	/** Invoca o controller de horário de turma para definir o horário desta solicitação.
	 * <br />
	 * Método não invocado por JSPs.
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
	
	/** Invoca o controller responsável por definir o horário da turma.<br/>Método não invocado por JSP´s.
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
	 * Carrega os horários com base na unidade gravada nos parâmetros da gestora da acadêmica. 
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
	 * Após a seleção do componente que será solicitado a turma, prepara a solicitação e inicia o caso e uso,
	 * levando a tela inicial da solicitação da turma.<br/>Método não invocado por JSP´s.
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
	
	/** Retorna o link para o formulário de solicitação de dados da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 */
	@Override
	public String telaDadosGerais(){
		return forward( JSP_SUGESTAO_SOLICITACAO_DADOS );
	}
	
	/** Retorna o link para o formulário de resumo dos dados da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 */
	@Override
	public String telaResumo(){
		return forward( JSP_SUGESTAO_SOLICITACAO_RESUMO);
	}
	
	/**
	 * Cadastra a solicitação de turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/solicitacao_turma/resumo.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		String msgSucesso = null;
		try {
			
			// caso seja cadastro de nova solicitação
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
			msgSucesso = "Sugestão de turma cadastrada com sucesso!";
		
			
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
				lista.addErro("Não é possível criar uma turma do componente " + componente.getCodigo() + " porque esta atividade não aceita criação de turma. Entre em contato com " + ParametroHelper.getInstance().getParametro(ParametrosGraduacao.SIGLA_NOME_GESTAO_GRADUACAO) + " para mudar as características do componente.");
				
		}
		return lista;
	}
	
	/**
	 * Método responsável por redirecionar o usuário para a tela de listagem de
	 * componentes curriculares. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
