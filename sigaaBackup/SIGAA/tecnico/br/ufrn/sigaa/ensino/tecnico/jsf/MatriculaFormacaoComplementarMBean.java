/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/01/2012
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RestricaoDiscenteMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.AtestadoMatriculaMBean;
import br.ufrn.sigaa.ensino.negocio.RestricaoDiscenteMatriculaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.MatriculaMov;
import br.ufrn.sigaa.ensino.tecnico.dao.MatriculaFormacaoComplementarDao;

/**
 * Controlador responsável pela matrícula on-line de alunos de formação complementar.
 * 
 * @author Leonardo Campos
 *
 */
@Component("matriculaFormacaoComplementarMBean") @Scope("session")
public class MatriculaFormacaoComplementarMBean extends SigaaAbstractController<MatriculaComponente> {

	/** Calendário Acadêmico para fazer verificações ao realizar a matrícula */
	private CalendarioAcademico calendarioParaMatricula;
	
	/** Discente cuja matrícula será efetuada */
	private DiscenteAdapter discente;
	
	/** Turmas para sugestão de matrícula */
	private Collection<SugestaoMatricula> turmasCurriculo;
	
	/** Turmas selecionadas na matrícula */
	private Collection<Turma> turmas;
	
	/** Instruções específicas para a matrícula. */
	private String instrucoes;
	
	/** Situação que a matrícula deve ficar após realizada. */
	private SituacaoMatricula situacaoMatricula = SituacaoMatricula.EM_ESPERA;
	
	/**
	 * Efetua as validações e preenche as informações necessárias para iniciar o caso de uso.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {
		DiscenteDao ddao = getDAO(DiscenteDao.class);
		setCalendarioParaMatricula(getCalendarioVigente());
		setDiscente(ddao.findByPK(getDiscenteUsuario().getId()));
		setTurmas( new ArrayList<Turma>() );
		
		if(!getCalendarioParaMatricula().isPeriodoMatriculaRegular()){
			addMensagemErro("Não está no período oficial de matrículas on-line.");
			return null;
		}
		MatriculaFormacaoComplementarDao dao = getDAO(MatriculaFormacaoComplementarDao.class);
		
		if(!dao.isAlunoVeterano(getDiscente())){
			addMensagemErro("Apenas alunos veteranos podem realizar sua matrícula on-line. " +
					"Procure a secretaria do seu curso para efetuar sua matrícula.");
			return null;
		}
		
		if ( !StatusDiscente.getStatusMatriculavelGraduacao().contains(discente.getStatus()) ) {
			addMensagemErro("Apenas alunos ativos podem realizar matrículas.");
			return null;
		}
		
		Collection<RestricaoDiscenteMatricula> restricoes = dao.findRestricoesDiscenteMatricula(getDiscente().getGestoraAcademica(), getDiscente().getNivel());
		for(RestricaoDiscenteMatricula r: restricoes) {
			if(r.isPeriodoVigente()){
				erros = new ListaMensagens();
				if(ValidatorUtil.isNotEmpty(r.getClasse())) {
					RestricaoDiscenteMatriculaValidator validator = ReflectionUtils.newInstance(r.getClasse());
					validator.validate(getDiscente(), erros);
				}
				if(ValidatorUtil.isNotEmpty(r.getSituacaoMatricula()))
					setSituacaoMatricula(r.getSituacaoMatricula());
				if(ValidatorUtil.isNotEmpty(r.getInstrucoes()))
					setInstrucoes(r.getInstrucoes());
			}
		}
		
		if(hasOnlyErrors())
			return null;
		
		setTurmasCurriculo(dao.findSugestoesMatricula(getDiscente(),
				getCalendarioParaMatricula().getAno(),
				getCalendarioParaMatricula().getPeriodo()));
		
		return telaTurmasCurriculo();
	}

	/**
	 * Carrega turmas por expressão.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/formacao_complementar/matricula/painel_turmas_abertas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String getCarregarTurmasPorExpressao() throws ArqException {
		if (getDiscente() == null) {
			return null;
		}

		TurmaDao dao = getDAO(TurmaDao.class);

		try {

			String expressao = getParameter("expressao");
			if (isEmpty(expressao)) {
				getCurrentRequest().setAttribute("erroExpressao", "Não foi possível carregar expressão");
				return null;
			}
			Collection<ComponenteCurricular> componentes = ExpressaoUtil.expressaoToComponentes(expressao, discente.getId());
			if (isEmpty(componentes)) {
				getCurrentRequest().setAttribute("erroExpressao", "Não foi possível carregar expressão");
				return null;
			}
			Collection<Integer> tiposTurma = new ArrayList<Integer>(3);
			tiposTurma.add(Turma.REGULAR);

			Collection<Turma> resultadoTurmasBuscadas;
			resultadoTurmasBuscadas = dao.findAbertasByExpressao(componentes, null, null,
					getCalendarioParaMatricula().getAno(), getCalendarioParaMatricula().getPeriodo(), true, getNivelEnsino(), getDiscente().isDiscenteEad(), tiposTurma.toArray(new Integer[]{}));
			getCurrentRequest().setAttribute("resultadoTurmasBuscadas", resultadoTurmasBuscadas);

			ComponenteCurricularDao ccdao = getDAO(ComponenteCurricularDao.class);
			String expressaoFormatada = ExpressaoUtil.buildExpressaoFromDB(expressao, ccdao);
			getCurrentRequest().setAttribute("expressaoFormatada", expressaoFormatada);
			return "";
		} catch (LimiteResultadosException lre) {
			getCurrentRequest().setAttribute("erroExpressao", "O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
			return null;
		}
	}
	
	/**
	 * Encaminha para a tela de turmas do currículo.
	 * @return
	 */
	private String telaTurmasCurriculo() {
		return forward(ConstantesNavegacaoFormacaoComplementar.MATRICULA_TURMAS);
	}
	
	/**
	 * Encaminha para a tela de turmas do currículo.
	 * @return
	 */
	private String telaConfirmacaoMatricula() {
		return forward(ConstantesNavegacaoFormacaoComplementar.MATRICULA_CONFIRMACAO);
	}
	
	/**
	 * Invoca o processador para tentar efetuar a matrícula na
	 * turma selecionada. <br/>
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li>
	 * /sigaa.war/ensino/formacao_complementar/matricula/turmas_curriculo.jsp</li>
	 * </ul>
	 * @return
	 */
	public String selecionarTurma() throws ArqException {
		
		DiscenteDao dao = getDAO(DiscenteDao.class);
		Integer idTurma = getParameterInt("idTurma");
		if(idTurma == null){
			addMensagemErro("Ocorreu um problema ao carregar a turma selecionada. Por favor, reinicie a operação.");
			return cancelar();
		}
		
		Turma turma = dao.findByPrimaryKey(idTurma, Turma.class);
		turma.setMatricular(true);
		turma.getHorarios().iterator();
		turma.getDocentesNomes();
		turma.getLocal();
		getTurmas().add(turma);
		
		Collection<DiscenteAdapter> discentes = new ArrayList<DiscenteAdapter>();
		discentes.add(getDiscente());
		
		MatriculaMov movimento = new MatriculaMov();
		movimento.setTurmas(getTurmas());
		movimento.setDiscentes(discentes);
		movimento.setSituacao(getSituacaoMatricula());
		movimento.setCodMovimento(SigaaListaComando.MATRICULAR_FORMACAO_COMPLEMENTAR);
		
		try {
			execute(movimento);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			getTurmas().clear();
			return null;
		}
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		
		return telaConfirmacaoMatricula();
	}

	/**
	 * Exibe o atestado de matrícula.
	 * <br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/ensino/formacao_complementar/matricula/confirmacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String exibirAtestadoMatricula() throws SegurancaException, DAOException {
		getCurrentSession().setAttribute("atestadoLiberado", discente.getId());
		AtestadoMatriculaMBean atestado = (AtestadoMatriculaMBean) getMBean("atestadoMatricula");
		atestado.setDiscente(discente);
		return atestado.selecionaDiscente();
	}
	
	public CalendarioAcademico getCalendarioParaMatricula() {
		return calendarioParaMatricula == null ? getCalendarioVigente() : calendarioParaMatricula;
	}

	public void setCalendarioParaMatricula(CalendarioAcademico calendarioParaMatricula) {
		this.calendarioParaMatricula = calendarioParaMatricula;
	}
	
	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public Collection<SugestaoMatricula> getTurmasCurriculo() {
		return turmasCurriculo;
	}

	public void setTurmasCurriculo(Collection<SugestaoMatricula> turmasCurriculo) {
		this.turmasCurriculo = turmasCurriculo;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public String getInstrucoes() {
		return instrucoes;
	}

	public void setInstrucoes(String instrucoes) {
		this.instrucoes = instrucoes;
	}

	public SituacaoMatricula getSituacaoMatricula() {
		return situacaoMatricula;
	}

	public void setSituacaoMatricula(SituacaoMatricula situacaoMatricula) {
		this.situacaoMatricula = situacaoMatricula;
	}
}
