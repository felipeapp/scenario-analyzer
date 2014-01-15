/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/05/2010
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.jsf.ComponenteCurricularMBean;
import br.ufrn.sigaa.ensino.jsf.BuscaTurmaMBean;
import br.ufrn.sigaa.ensino.jsf.TurmaMBean;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.portal.jsf.PortalCoordenacaoStrictoMBean;

/** Controller responsável por operar turmas de stricto sensu.
 * @author Édipo Elder F. Melo
 *
 */
@Component("turmaStrictoSensuBean") @Scope("session")
public class TurmaStrictoSensuMBean extends TurmaMBean {
	
	/** Define o link para o formulário de dados gerais. */
	public static final String JSP_DADOS_GERAIS = "/stricto/turma/dados_gerais.jsp";
	/** Define o link para o resumo dos dados da turma. */
	public static final String JSP_RESUMO = "/stricto/turma/resumo.jsp";
	/** Define o link para o formulário de confirmação dos dados da turma. */
	public static final String JSP_CONFIRMA_REMOCAO = "/stricto/turma/confirma_remocao.jsp";
	/** Indica se a operação atual do controller é de remoção. */
	private boolean remover;

	/** verifica as permissões do usuário para operação de remoção da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#checkRoleAtualizarTurma()
	 */
	@Override
	public void checkRoleAtualizarTurma() throws ArqException {
		remover = false;
			if( !obj.isAberta() && !isUserInRole(SigaaPapeis.PPG ) ){
				addMensagemErro("Não é possível alterar turmas que não estão abertas.");
				obj = new Turma();
				return ;
			} else if( !obj.isAberta() && isUserInRole(SigaaPapeis.PPG ) ){
				addMensagemWarning("Atenção! Esta turma já foi consolidada.");
			}

			// Este boolean libera a adição de novas reservas de solicitações de turma pendentes a esta turma
			// é necessário que esteja no período de cadastramento de turma, regular ou de férias 
			if( !isUserInRole(SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO) && 
					obj.isTurmaRegular() && getCalendario() != null)
				setAdicionarOutrasReservas(getCalendario().isPeriodoCadastroTurmas() || getCalendario().isPeriodoAjustesTurmas());
			else
				setAdicionarOutrasReservas(false);

			if(isAdicionarOutrasReservas()){
				// Verificando se está no período de cadastramento de turma regular 
				if(  obj.getPeriodo() <= 2 &&
						 getCalendario() != null &&
						!getCalendario().isPeriodoCadastroTurmas()  && !getCalendario().isPeriodoAjustesTurmas() ){
					addMensagemErro("Não está no período de cadastro de turmas.");
					return;
				}

				// Arrumando as reservas que já foram atendidas para a exibição na JSP
				// as reservas atendidas não podem ser removidas da turma. 
				for (Object element : obj.getReservas()) {
					ReservaCurso rc = (ReservaCurso) element;

					rc.setPodeRemover(true);
				}
			}
			// Inicializar dados
			if (obj.getTurmaAgrupadora() != null)
				obj.getTurmaAgrupadora().getId();
			
			obj.getReservas().iterator();
			obj.getDocentesTurmas().iterator();
			obj.getHorarios().iterator();
			if (isTurmaEad()) {
				if (obj.getPolo() == null)
					obj.setPolo(new Polo());
			}
			if (obj.getCurso() == null)
				obj.setCurso(new Curso());
			setReserva(new ReservaCurso());
			if( obj.getDisciplina().isGraduacao() && obj.getCampus() == null )
				obj.setCampus(new CampusIes());

			// Buscar se já existem alunos matriculados para a turma selecionada
			MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
			obj.setQtdMatriculados( matriculaDao.findTotalMatriculasByTurmaAtivos(obj) );
	}
	
	/** Retorna uma coleção de SelectItem de disciplinas para a turma.
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getComboDisciplinas() throws ArqException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		Unidade unidade = null;
		if (isUserInRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO)) {
			unidade = getProgramaStricto();
		} else {
			unidade = new Unidade( getUnidadeGestora());
		}

		char nivel = getNivelEnsino();
		if( isPortalCoordenadorStricto() )
			nivel = NivelEnsino.STRICTO;

		Collection<ComponenteCurricular> disciplinas = dao.findOtimizado(nivel, unidade, TipoComponenteCurricular.DISCIPLINA, TipoComponenteCurricular.MODULO);
		return toSelectItems(disciplinas, "id", "descricao");
	}
	
	/**
	 * Cancela a operação e volta para a listagem das turmas, caso a operação seja de alteração ou remoção de uma turma.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/turma/confirma_remocao.jsp</li>
	 * <li>/sigaa.war/stricto/turma/resumo.jsp</li>
	 * <li>/sigaa.war/stricto/turma/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 */
	public String cancelar() {
		if(getConfirmButton().equals("Alterar") || getConfirmButton().equals("Remover")) {
			BuscaTurmaMBean mBean = getMBean("buscaTurmaBean");
			try {
				return mBean.buscarGeral();
			} catch (DAOException e) {
				return super.cancelar();
			}
		}
		return super.cancelar();
	}

	/**
	 * Cadastra, altera ou remove uma turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/turma/confirma_remocao.jsp</li>
	 * <li>/sigaa.war/stricto/turma/resumo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#cadastrar()
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
			if (obj.getCurso() != null && obj.getCurso().getId() == 0)
				obj.setCurso(null);
			
			mov.setCodMovimento(cmd);
			mov.setTurma(obj);
			mov.setSolicitacaoEnsinoIndividualOuFerias(null);
			mov.setSolicitacoes( new HashSet<SolicitacaoTurma>() );
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
			super.afterCadastrar();
			if (cmd.equals(SigaaListaComando.CADASTRAR_TURMA)) {
				if( obj.getDisciplina().isSubUnidade() )
					addMessage("ATENÇÃO! A turma criada foi de uma subunidade de um bloco e só estará disponível para matrícula após todas as subunidades do bloco terem sido criadas.", TipoMensagemUFRN.INFORMATION);
				addMessage("Turma " + obj.getDescricaoSemDocente() + " cadastrada com sucesso!", TipoMensagemUFRN.INFORMATION);
			} else {
				addMessage("Turma " + obj.getDescricaoSemDocente() + " alterada com sucesso!", TipoMensagemUFRN.INFORMATION);
			}
			clear();
			return ((BuscaTurmaMBean)getMBean("buscaTurmaBean")).popularBuscaGeral();
		}
	}

	/** Retorna o link do formulário de confirmação de dados da turma.<br/>Método não invocado por JSP´s.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#formConfirmacao()
	 */
	@Override
	public String formConfirmacao() {
		return forward(JSP_RESUMO);
	}

	/** Retorna o link do formulário de dados gerais da turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/turma/resumo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#formConfirmacao()
	 */
	@Override
	public String formDadosGerais() {
		if (hasErrors())
			return null;
		else
			return forward(JSP_DADOS_GERAIS);
	}

	/**
	 * Retorna o link para o formulário de seleção de componentes curriculares. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/turma/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#formSelecaoComponente()
	 */
	@Override
	public String formSelecaoComponente() {
		try {
			ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
			mBean.setSelecionaUnidade(false);
			Unidade unidade = null;
			if (isPortalCoordenadorStricto())
				unidade = getProgramaStricto();
			return mBean.buscarComponente(this, "Cadastro de Turmas", unidade, false,false, TipoComponenteCurricular.DISCIPLINA, TipoComponenteCurricular.MODULO, TipoComponenteCurricular.ATIVIDADE);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	/** Retorna o nível de ensino operado por este controller.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#getNivelEnsino()
	 */
	@Override
	public char getNivelEnsino() {
		return NivelEnsino.STRICTO;
	}

	/** Indica se deve-se informar os docentes da turma, ou não.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#isDefineDocentes()
	 */
	@Override
	public boolean isDefineDocentes() {
		return true;
	}

	/** Indica se deve-se informar os horários da turma, ou não.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#isDefineHorario()
	 */
	@Override
	public boolean isDefineHorario() {
		return true;
	}

	/** Indica se o usuário pode alterar o horário da turma.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#isPodeAlterarHorarios()
	 */
	@Override
	public boolean isPodeAlterarHorarios() {
		return obj.isAberta() 
			&& ( !isMatriculada()  || isUserInRole(SigaaPapeis.ADMINISTRADOR_STRICTO) )
			&& isUserInRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, SigaaPapeis.ADMINISTRADOR_STRICTO) 
			|| (obj.getId() == 0);
	}	

	/** Indica se a turma é de Ensino a Distância
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#isTurmaEad()
	 */
	@Override
	public boolean isTurmaEad() {
		return false;
	}

	/** Verifica as permissões do usuário para remover uma turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#checkRolePreRemover()
	 */
	@Override
	public void checkRolePreRemover() throws ArqException {
		if( !obj.isAberta() ){
			addMensagemErro("Não é possível remover a turma que não está aberta.");
		} else if (obj.getQtdMatriculados() > 0) {
			addMensagemErro("Não pode remover uma turma com discentes matriculados.");
		}
		remover = true;
	}

	/** Remove uma turma.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/stricto/turma/confirma_remocao.jsp</li>
	 * </ul>
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#remover()
	 */
	@Override
	public String remover() throws ArqException {
		TurmaMov mov = new TurmaMov();
		mov.setCodMovimento(SigaaListaComando.REMOVER_TURMA);
		mov.setTurma(obj);

		try {
			Turma removida = (Turma) executeWithoutClosingSession(mov, getCurrentRequest());
			if( removida.getPolo() != null )
				removida.setPolo( getGenericDAO().findByPrimaryKey( removida.getPolo().getId() , Polo.class) );
			addMessage("Turma " + removida.getDescricaoSemDocente() + " removida com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return getFormPage();
		} catch (SegurancaException e) {
			throw e;
		}
		
		if ( isPortalCoordenadorStricto() ) {
			PortalCoordenacaoStrictoMBean strictoBean = getMBean("portalCoordenacaoStrictoBean");
			strictoBean.setSolicitacoesMatricula(null);
		}

		clear();
		return cancelar();
	}

	/** Valida os dados gerais da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#validaDadosGerais(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public void validaDadosGerais(ListaMensagens erros) throws DAOException {
		if( obj.getDisciplina() == null || obj.getDisciplina().getId() <= 0 ){
			addMensagemErro("Selecione um componente curricular.");
		} else 	if (!obj.getDisciplina().isAtivo()) {
			addMensagemErro("O componente curricular " + obj.getDisciplina().getDescricaoResumida() + " não está ativo.");
		}
		
		if(hasErrors()) return;

		TurmaDao dao = getDAO(TurmaDao.class);

		TurmaValidator.validaDadosBasicos(obj, getUsuarioLogado(), erros);

		if (obj.getDataInicio() != null ) {
			// Atribuir ano-período com base nas datas informadas no formulário
			Calendar ini = Calendar.getInstance();
			ini.setTime(obj.getDataInicio());
			if( obj.getAno() <= 0 )
				obj.setAno(ini.get(Calendar.YEAR));
			
			if( obj.getPeriodo() <= 0 ){
				if (ini.get(Calendar.MONTH) <= Calendar.JUNE)
					obj.setPeriodo(1);
				else
					obj.setPeriodo(2);
			}
		}

		if (obj.getCurso() != null && obj.getCurso().getId() > 0)
			obj.setCurso(dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class));

		// tratando sub-turmas 
		if( obj.getDisciplina().isAceitaSubturma() ){
			int idTurmaAgrupadora = getParameterInt("turmaAgrupadoraSelecionada", 0);
			if( idTurmaAgrupadora > 0 ){
				obj.setTurmaAgrupadora( dao.findByPrimaryKey(idTurmaAgrupadora, Turma.class) );
			} else {
				// verificando o código da turma, quando se trata de alteração de dados
				if (obj.getId() != 0) {
					// caso possua letras no código da turma, remove
					obj.setCodigo(removeLetras(obj.getCodigo()));
					// verifica se o código informado é válido.
					if (obj.getTurmaAgrupadora() != null && obj.getCodigo().equals(obj.getTurmaAgrupadora().getCodigo())) {
						addMensagemErro("Informe um código diferente para a turma.");
						return;
					}
					// verifica se existe turma com o código informado.
					Collection<Turma> turmas = dao.findByDisciplinaAnoPeriodo(obj.getDisciplina(), obj.getAno(), obj.getPeriodo(), 0, (char) 0);
					for (Turma turma : turmas){
						if (turma.getCodigo().equals(obj.getCodigo())) {
							addMensagemErro("Existe uma turma com o código informado. Por favor, informe outro código para a turma.");
							return;
						}
					}
				obj.setTurmaAgrupadora(null);
				}
			}
		}
		
		// valida se a data informada / alterada pelo chefe de departamento está dentro do período de férias
		if (obj.isTurmaFerias() && getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO)){
			CalendarioAcademico calTurma = CalendarioAcademicoHelper.getCalendario(obj);
			validateMinValue(obj.getDataInicio(), calTurma.getInicioFerias(), "Início", erros);
			validateMaxValue(obj.getDataFim(), calTurma.getFimFerias(), "Fim", erros);
		}
		
		if (hasOnlyErrors())
			return;

	}
	
	/** Valida os docentes da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#validaDocentesTurma(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public void validaDocentesTurma(ListaMensagens erros) throws DAOException {
	}

	/** Verifica as permissões do usário para cadastrar turma sem solicitação.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#checkRoleCadastroTurmaSemSolicitacao(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public void checkRoleCadastroTurmaSemSolicitacao(ListaMensagens erros) throws SegurancaException {
		remover = false;
		checkRole(new int[] { SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS});
		try {
			if( isUserInRole( SigaaPapeis.PPG ) ){
				obj.setAno(CalendarUtils.getAnoAtual());
				obj.setPeriodo(getPeriodoAtual());
			}else{
					obj.setAno(getCalendario().getAno());
				obj.setPeriodo(getCalendario().getPeriodo());
			}
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
	}

	/** Retorna o calendário acadêmico a ser usado no cadastro de turmas.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#getCalendario()
	 */
	@Override
	protected CalendarioAcademico getCalendario() throws DAOException {
		CalendarioAcademico cal = getCalendarioVigente();
		if( isUserInRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS) 
				&& getCursoAtualCoordenacao() != null) {
			cal = CalendarioAcademicoHelper.getCalendario(getCursoAtualCoordenacao());
		}
		return cal;
	}

	/** Seta alguns atributos antes de selecionar o componente curricular da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#beforeSelecionarComponente()
	 */
	@Override
	public void beforeSelecionarComponente() throws DAOException {
		obj.setAno(getCalendario().getAno());
		obj.setPeriodo(getCalendario().getPeriodo());
		obj.setDataInicio( getCalendario().getInicioPeriodoLetivo() );
		obj.setDataFim( getCalendario().getFimPeriodoLetivo() );
		obj.setSituacaoTurma( new SituacaoTurma( SituacaoTurma.A_DEFINIR_DOCENTE ) );
		obj.setTipo( Turma.REGULAR );
	}

	/** Operações a serem executadas antes da confirmação do cadastro da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#beforeConfirmacao()
	 */
	@Override
	public void beforeConfirmacao() throws ArqException {
		
	}

	/** Operações a serem executadas antes da exibição do formulário de dados gerais.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#beforeDadosGerais()
	 */
	@Override
	public void beforeDadosGerais() throws ArqException {
		if (obj.getDisciplina().isAtividade() && !obj.getDisciplina().isAtividadeColetiva()){
			addMensagemErro("Não é possível criar turmas para Atividades com Forma de Participação diferente de Coletiva.");
			return;
		}
		
	}

	/** Operações a serem executadas antes do formulário de definição de docentes.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#beforeDefinirDocentesTurma()
	 */
	@Override
	public void beforeDefinirDocentesTurma() throws ArqException {
	}

	/** Validações adicionais aos horários da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#validaHorariosTurma(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	@Override
	public void validaHorariosTurma(ListaMensagens erros) throws DAOException {
	}

	/** Operações a serem executadas antes de atualizar a turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#beforeAtualizarTurma()
	 */
	@Override
	public void beforeAtualizarTurma() {
	}

	/** Retorna o link para o formulário de confirmação da remoção turma.<br/>Método não invocado por JSP´s.
	 * @see br.ufrn.sigaa.ensino.jsf.TurmaMBean#formConfirmacaoRemover()
	 */
	@Override
	public String formConfirmacaoRemover() {
		this.remover = true;
		return forward(JSP_CONFIRMA_REMOCAO);
	}

	/** Indica se o usuário tem permissão para remover a turma.
	 * @return
	 */
	public boolean isRemover() {
		return remover;
	}
	
	/** Indica se a operação corrente é de edição do código da turma.
	 * @return
	 */
	public boolean isEditarCodigoTurma() {
		return obj.getId() != 0;
	}

	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		ListaMensagens lista = new ListaMensagens();
		if (componente.isAtividade() && !componente.getDetalhes().isAtividadeAceitaTurma()) {
			lista.addErro("Não é possível criar uma turma do componente " + componente.getCodigo() + " porque esta atividade não aceita criação de turma. Entre em contato com a " + ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.SIGLA_NOME_PRO_REITORIA_POS) + " para mudar as características do componente.");
		}	
		return lista;
	}

	@Override
	public String retornarSelecaoComponente() {
		// TODO redirecionar o usuário para o formulário que invocou a seleção de componentes.
		return cancelar();
	}

	@Override
	public String definePeriodosTurma(Date inicio, Date fim)
			throws ArqException {
		// TODO Auto-generated method stub
		return null;
	}
}
