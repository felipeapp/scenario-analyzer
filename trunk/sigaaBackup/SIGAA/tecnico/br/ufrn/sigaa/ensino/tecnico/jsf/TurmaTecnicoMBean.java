/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/05/2010
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.HorarioDocente;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.jsf.ComponenteCurricularMBean;
import br.ufrn.sigaa.ensino.jsf.TurmaMBean;
import br.ufrn.sigaa.ensino.latosensu.dominio.RegistroAlteracaoLato;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;

/** Controller respons�vel por opera��es com turmas de cursos t�cnicos.
 * @author �dipo Elder F. Melo
 *
 */
@Component("turmaTecnicoBean") @Scope("session")
public class TurmaTecnicoMBean extends TurmaMBean {
	/** Define o link para o formul�rio de dados gerais. */
	public static final String JSP_DADOS_GERAIS = "/ensino/tecnico/turma/dados_gerais.jsp";
	/** Define o link para o resumo dos dados da turma. */
	public static final String JSP_RESUMO = "/ensino/tecnico/turma/resumo.jsp";
	/** Define o link de cadastro efetuado com sucesso. */
	public static final String JSP_CADASTRO_SUCESSO = "/ensino/tecnico/turma/cadastro_sucesso.jsp";
	/** Define o link de confirma��o de remo��o. */
	public static final String JSP_CONFIRMA_REMOCAO = "/ensino/tecnico/turma/confirma_remocao.jsp";

	/** Registro de altera��es efetuadas na turma em rela��o �s pr�-aprovadas na proposta do curso de lato sensu. */
	private RegistroAlteracaoLato registroAlteracao;
	/** Indica que a opera��o � de remover turma. * */
	private boolean remover;
	/** Componente curricular cadastrado, permitindo o cadastro de outra turma para o mesmo componente. */ 
	private ComponenteCurricular componenteAnterior;
	
	/**
	 * Checa a regra para atualiza��o da turma
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public void checkRoleAtualizarTurma() throws ArqException {
		this.remover = false;
		if( !obj.isAberta()){
			addMensagemErro("N�o � poss�vel alterar turmas que n�o est�o abertas.");
			obj = new Turma();
			return;
		}
	}

	/**
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/turma/resumo.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cadastrar() throws ArqException {
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {
			
			Comando cmd = SigaaListaComando.CADASTRAR_TURMA;
			TurmaMov mov = new TurmaMov();
			if (getConfirmButton().equalsIgnoreCase("alterar")) {
				cmd = SigaaListaComando.ALTERAR_TURMA;
				mov.setAlteracaoTurma(getAlteracaoTurma());
			}

			obj.setCurso(null);
			mov.setCodMovimento(cmd);
			mov.setTurma(obj);
			mov.setSolicitacaoEnsinoIndividualOuFerias(null);
			mov.setSolicitacoes( new HashSet<SolicitacaoTurma>() );
			mov.setRegistroAlteracaoLato(registroAlteracao);

			componenteAnterior = obj.getDisciplina();
			for( ReservaCurso r : obj.getReservas() ){
				mov.getSolicitacoes().add( r.getSolicitacao() );
			}
			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return null;
			}
			if (cmd.equals(SigaaListaComando.CADASTRAR_TURMA)) {
				if( obj.getDisciplina().isSubUnidade() )
					addMessage("ATEN��O! A turma criada foi de uma subunidade de um bloco e s� estar� dispon�vel para matr�cula ap�s todas as subunidades do bloco terem sido criadas.", TipoMensagemUFRN.INFORMATION);
				addMessage("Turma " + obj.getDescricaoSemDocente() + " cadastrada com sucesso!", TipoMensagemUFRN.INFORMATION);
			} else {
				addMessage("Turma " + obj.getDescricaoSemDocente() + " alterada com sucesso!", TipoMensagemUFRN.INFORMATION);
			}
			clear();
			return formCadastroSucesso();
		}
	}
	
	/** Inicia o cadastro de uma turma sem solicita��o para o mesmo componente cadastrado anteriormente.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/tecnico/turma/cadastro_sucesso.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	public String preCadastrarMesmoComponente() throws ArqException, NegocioException {
		if (componenteAnterior == null)
			return super.preCadastrar();
		return iniciarTurmaSemSolicitacao();
	}

	/**
	 * Verifica se a turma est� pass�vel de edi��o
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/ensino/tecnico/turma/dados_gerais.jsp</li>
	 * </ul>
	 */
	@Override
	public boolean isPassivelEdicao() throws DAOException {
		if ( isReadOnly() ) {
			return false;
		}
		return obj.isAberta() ;
	}
	/**
	 * Inicia o cadastro de uma turma sem solicita��o para um componente
	 * curricular. <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/tecnico/turma/cadastro_sucesso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String preCadastrarOutroComponente() throws ArqException, NegocioException {
		componenteAnterior = null;
		return super.preCadastrar();
	}

	/** Redireciona o usu�rio para a tela de resumo do cadastro.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String formConfirmacao() {
		return forward(JSP_RESUMO);
	}
	
	/** Redireciona o usu�rio para a tela de cadastro com sucesso.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	public String formCadastroSucesso() {
		return forward(JSP_CADASTRO_SUCESSO);
	}

	/**
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/turma/resumo.jsp</li>
	 *	</ul>
	 */
	@Override
	public String formDadosGerais() {
		return forward(JSP_DADOS_GERAIS);
	}

	/**
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/turma/dados_gerais.jsp</li>
	 *	</ul>
	 */
	@Override
	public String formSelecaoComponente() {
		try {
			if (componenteAnterior != null) { 
				String retorno = selecionaComponenteCurricular(componenteAnterior);
				componenteAnterior = null;
				return retorno;
			}
			ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
			mBean.setSelecionaUnidade(false);
			Unidade unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			return mBean.buscarComponente(this, "Cadastro de Turmas", unidade, false, false, TipoComponenteCurricular.DISCIPLINA, TipoComponenteCurricular.MODULO, TipoComponenteCurricular.ATIVIDADE);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	@Override
	public char getNivelEnsino() {
		return 'T';
	}

	@Override
	public boolean isDefineDocentes() {
		return true;
	}

	@Override
	public boolean isDefineHorario() {
		return true;
	}

	@Override
	public boolean isPodeAlterarHorarios() {
		return obj.isAberta() || (obj.getId() == 0 || !isMatriculada());
	}

	@Override
	public boolean isTurmaEad() {
		return false;
	}

	/**
	 * Checa as regras para a remo��o.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public void checkRolePreRemover() throws ArqException {
		if( !obj.isAberta() ){
			addMensagemErro("N�o � poss�vel remover a turma que n�o est� aberta.");
		} else if (obj.getQtdMatriculados() > 0) {
			addMensagemErro("N�o pode remover uma turma com discentes matriculados.");
		}
		remover = true;
	}

	/** Reimplementa a verifica��o de altera��o de hor�rio do docente, quando o hor�rio da turma � alterado. A verifica��o ser� realizada em outro passo.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#verificaAlteracaoHorarioDocente()
	 */
	@Override
	protected void verificaAlteracaoHorarioDocente() throws DAOException {
	}

	/**
	 * Realiza a valida��o nos dados gerais
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public void validaDadosGerais(ListaMensagens erros) throws DAOException {
		if( obj.getDisciplina() == null || obj.getDisciplina().getId() <= 0 ){
			addMensagemErro("Selecione um componente curricular.");
		} else 	if (!obj.getDisciplina().isAtivo()) {
			addMensagemErro("O componente curricular " + obj.getDisciplina().getDescricaoResumida() + " n�o est� ativo.");
		}
		
		if(hasErrors()) return;

		TurmaDao dao = getDAO(TurmaDao.class);
		obj.setDisciplina( dao.findByPrimaryKey(obj.getDisciplina().getId(), ComponenteCurricular.class) );

		TurmaValidator.validaDadosBasicos(obj, getUsuarioLogado(), erros);

		String anoPeriodoInformado = obj.getAnoPeriodo();
		if (obj.getDataInicio() != null) {
			// Atribuir ano-per�odo com base nas datas informadas no formul�rio
			Calendar ini = Calendar.getInstance();
			ini.setTime(obj.getDataInicio());
			obj.setAno(ini.get(Calendar.YEAR));
			if (ini.get(Calendar.MONTH) <= Calendar.JUNE)
				obj.setPeriodo(1);
			else
				obj.setPeriodo(2);
		}

		// informa ao usu�rio que o ano-per�odo foi ajustado
		if (anoPeriodoInformado != null && !anoPeriodoInformado.equals(obj.getAnoPeriodo())) {
			addMensagemWarning("O ano-per�odo da turma foi ajustado conforme a data de in�cio da turma.");
		}

		obj.setCurso(null);

		// tratando sub-turmas 
		if( obj.getDisciplina().isAceitaSubturma() ){
			int idTurmaAgrupadora = getParameterInt("turmaAgrupadoraSelecionada", 0);
			if( idTurmaAgrupadora > 0 ){
				obj.setTurmaAgrupadora( dao.findByPrimaryKey(idTurmaAgrupadora, Turma.class) );
			} else {
				// verificando o c�digo da turma, quando se trata de altera��o de dados
				if (obj.getId() != 0) {
					// caso possua letras no c�digo da turma, remove
					obj.setCodigo(removeLetras(obj.getCodigo()));
					// verifica se o c�digo informado � v�lido.
					if (obj.getTurmaAgrupadora() != null && obj.getCodigo().equals(obj.getTurmaAgrupadora().getCodigo())) {
						addMensagemErro("Informe um c�digo diferente para a turma.");
						return;
					}
					// verifica se existe turma com o c�digo informado.
					Collection<Turma> turmas = dao.findByDisciplinaAnoPeriodo(obj.getDisciplina(), obj.getAno(), obj.getPeriodo(), 0, (char) 0);
					for (Turma turma : turmas){
						if (turma.getCodigo().equals(obj.getCodigo())) {
							addMensagemErro("Existe uma turma com o c�digo informado. Por favor, informe outro c�digo para a turma.");
							return;
						}
					}
				obj.setTurmaAgrupadora(null);
				}
			}
		}
		
		// valida se a data informada / alterada pelo chefe de departamento est� dentro do per�odo de f�rias
		if (obj.isTurmaFerias() && getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO)){
			CalendarioAcademico calTurma = CalendarioAcademicoHelper.getCalendario(obj);
			validateMinValue(obj.getDataInicio(), calTurma.getInicioFerias(), "In�cio", erros);
			validateMaxValue(obj.getDataFim(), calTurma.getFimFerias(), "Fim", erros);
		}
		
		if (hasOnlyErrors())
			return;

	}
	
	/**
	 * M�todo de valida��o dos docentes da turma<br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public void validaDocentesTurma(ListaMensagens erros) throws DAOException {
		
	}

	/**
	 * M�todo de checagem das regras de cadastro das turmas sem solicita��o<br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public void checkRoleCadastroTurmaSemSolicitacao(ListaMensagens erros) throws SegurancaException {
		this.remover = false;
		checkRole(new int[] { SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.COORDENADOR_TECNICO,});
		try {
			obj.setAno(getCalendario().getAno());
			obj.setPeriodo(getCalendario().getPeriodo());
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
	}

	/**
	 * Retorna o calend�rio acad�mico do curso 
	 */
	@Override
	protected CalendarioAcademico getCalendario() throws DAOException {
		CalendarioAcademico cal = getCalendarioVigente();
		if( isUserInRole( SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO) 
				&& getCursoAtualCoordenacao() != null) {
			cal = CalendarioAcademicoHelper.getCalendario(getCursoAtualCoordenacao());
		}
		return cal;
	}

	/**
	 * M�todo executado antes da sele��o do componente<br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public void beforeSelecionarComponente() throws DAOException {
		obj.setAno(getCalendario().getAno());
		obj.setPeriodo(getCalendario().getPeriodo());
		obj.setDataInicio( getCalendario().getInicioPeriodoLetivo() );
		obj.setDataFim( getCalendario().getFimPeriodoLetivo() );
		obj.setSituacaoTurma( new SituacaoTurma( SituacaoTurma.A_DEFINIR_DOCENTE ) );
		obj.setTipo( Turma.REGULAR );
		obj.setCurso(null);
		obj.setEspecializacao(new EspecializacaoTurmaEntrada());
		registroAlteracao = null;
	}
	
	/**
	 * M�todo executado antes da defini��o dos docentes da turma<br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public void beforeDefinirDocentesTurma() throws DAOException {
		GenericDAO dao = getGenericDAO();
		if (getNivelEnsino() == NivelEnsino.TECNICO 
				&& obj.getEspecializacao() != null
				&& obj.getEspecializacao().getId() > 0) {
			obj.setEspecializacao(dao.findByPrimaryKey(obj.getEspecializacao().getId(), EspecializacaoTurmaEntrada.class));
		}
		// reajusta o hor�rio dos docentes na turma para o mesmo hor�rio da turma
		if (!obj.getDisciplina().isPermiteHorarioDocenteFlexivel() && !ValidatorUtil.isEmpty(obj.getDocentesTurmas())) {
			for (DocenteTurma dt : obj.getDocentesTurmas()) {
				dt.getHorarios().clear();
				for (HorarioTurma ht : obj.getHorarios()) {
					HorarioDocente hd = new HorarioDocente();
					hd.setDataFim(obj.getDataFim());
					hd.setDataInicio(obj.getDataInicio());
					hd.setDia(ht.getDia());
					hd.setHorario(ht.getHorario());
					hd.setTipo(ht.getTipo());
					dt.getHorarios().add(hd);
				}
			}
		}
	}

	/**
	 * M�todo executado antes da confirma��o<br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public void beforeConfirmacao() throws ArqException {
	}

	/**
	 * M�todo executado antes dos dados gerais <br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public void beforeDadosGerais() throws ArqException {
		if (obj.getEspecializacao() == null)
			obj.setEspecializacao(new EspecializacaoTurmaEntrada());
	}

	/**
	 * M�todo respons�vel pela valida��o dos hor�rios das turmas<br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public void validaHorariosTurma(ListaMensagens erros) throws DAOException {
	}

	/**
	 * M�todo executado antes da atualiza��o da turma<br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public void beforeAtualizarTurma() {
		if (obj.getEspecializacao() == null)
			obj.setEspecializacao(new EspecializacaoTurmaEntrada());
	}

	/**
	 * Direciona para uma tela com o resumo para a confirma��o da remo��o<br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public String formConfirmacaoRemover() {
		this.remover = true;
		return forward(JSP_RESUMO);
	}

	public boolean isRemover() {
		return remover;
	}
	
	/** 
	 * Indica se a opera��o corrente � de edi��o do c�digo da turma.
	 */
	public boolean isEditarCodigoTurma() {
		return obj.getId() != 0;
	}

	/**
	 * M�todo respons�vel pela valida��o da sele��o dos componentes Curriculares<br />
	 * M�todo n�o invocado por JSPs.
	 */
	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		return new ListaMensagens();
	}

	@Override
	public String retornarSelecaoComponente() {
		// TODO redirecionar o usu�rio para o formul�rio que invocou a sele��o de componentes.
		return cancelar();
	}

	@Override
	public String definePeriodosTurma(Date inicio, Date fim)
			throws ArqException {
		// TODO Auto-generated method stub
		return null;
	}
}