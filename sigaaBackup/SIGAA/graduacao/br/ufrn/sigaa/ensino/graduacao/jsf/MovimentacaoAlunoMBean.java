/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 21/05/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TipoMovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.HomologacaoTrabalhoFinalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.ConstantesTipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoAfastamentoAluno;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.MovimentacaoAlunoValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * MBean de cadastro de afastamento e retorno de alunos. Também responsável
 * pelo estorno de um cadastro de afastamento. Pode ser usado para o registro de
 * qualquer tipo de afastamento ou iniciar com algum tipo específico.
 * 
 * @author André
 * @author Victor Hugo 
 */
@SuppressWarnings("serial")
@Component("movimentacaoAluno") @Scope("session")
public class MovimentacaoAlunoMBean extends SigaaAbstractController<MovimentacaoAluno> implements OperadorDiscente {

	/** Endereço do formulário de afastamento. */
	private static final String JSP_FORM_AFASTAMENTO = "/ensino/movimentacao_aluno/form_afastamento.jsp";
	/** Endereço do formulário de retorno. */
	private static final String JSP_FORM_RETORNO = "/ensino/movimentacao_aluno/form_retorno.jsp";
	/** Endereço do formulário de confirmação de estorno. */
	private static final String JSP_CONFIRMACAO_ESTORNO = "/ensino/movimentacao_aluno/confirmacao_estorno.jsp";
	/** Endereço do formulário de trancamentos. */
	private static final String JSP_TRANCAMENTOS = "/ensino/movimentacao_aluno/trancamentos.jsp";
	/** Endereço do formulário de conclusão stricto. */
	private static final String JSP_CONCLUIR_STRICTO = "/ensino/movimentacao_aluno/concluir_stricto.jsp";

	/** Operação a ser realizada. */
	private int operacao;

	/** Título da operação. */
	private String tituloOperacao;

	/** Possíveis tipos de afastamentos. */
	private Collection<SelectItem> tiposAfastamentos;

	/** Status anterior. */
	private String statusAnterior;

	/** Coleção de movimentos do discente. */
	private Collection<MovimentacaoAluno> historicoMovimentacoes;

	/** Coleção de cancelamentos do discente. */
	private Collection<MovimentacaoAluno> historicoCancelamentos;
	
	/** Indica se foi chamado pelo relatório de alunos não matriculados on-line */
	private boolean discenteNaoMatriculadoOnline;

	/**
	 * Coleção de movimentações do discente selecionado utilizado na operação de
	 * estornar movimentação, onde o usuário seleciona a movimentação que deseja
	 * estornar.
	 */
	private Collection<MovimentacaoAluno> movimentacoesRetornadas;

	/**
	 * Status do discente que deve retornar após ser realizado operação de estorno
	 */
	private Integer statusRetorno;

	/** Coleção de trancamentos futuros. */
	private Collection<MovimentacaoAluno> trancamentosFuturos;

	/** Homologação do trabalho final. */
	private HomologacaoTrabalhoFinal homologacao;

	/** Tipos de retorno do discente. */
	private static Collection<SelectItem> tiposRetorno = new ArrayList<SelectItem>();
	
	/** Afastamento que será retornado. */
	private MovimentacaoAluno afastamento;

	static{
		SelectItem t1 = new SelectItem(MovimentacaoAluno.ADMINISTRATIVO, "Administrativo");
		SelectItem t2 = new SelectItem(MovimentacaoAluno.JUDICIAL, "Judicial");
		SelectItem t3 = new SelectItem(MovimentacaoAluno.CAMARA, "Decisão da Câmara");
		tiposRetorno.add( t1 );
		tiposRetorno.add( t2 );
		tiposRetorno.add( t3 );
	}
		
	/** Construtor padrão. */
	public MovimentacaoAlunoMBean() {
		initObj();
	}

	/** Inicializa os atributos do objeto do controller. */
	private void initObj() {
		obj = new MovimentacaoAluno();
		obj.setDiscente(new Discente());
		obj.setTipoMovimentacaoAluno(new TipoMovimentacaoAluno());
		this.afastamento = new MovimentacaoAluno();
		setDiscenteNaoMatriculadoOnline(false);
		setConfirmButton("Confirmar");
		setTituloOperacao(null);
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public String getStatusAnterior() {
		return statusAnterior;
	}

	public void setStatusAnterior(String statusAnterior) {
		this.statusAnterior = statusAnterior;
	}

	/** Método invocado pela arquitetura após o cadastramento.
	 * <br>
	 * Método não invocado por JSP´s.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterCadastrar()
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		if ( isTrancamento() ) {
			obj = new MovimentacaoAluno();
		} else {
			super.afterCadastrar();
			initObj();
		}
	}

	/**
	 * Operação final para registrar um afastamento.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/resumo.jsp</li>
	 * <li>sigaa.war/ensino/movimentacao_aluno/form_afastamento.jsp</li>
	 * <li>sigaa.war/ensino/movimentacao_aluno/form_retorno.js</li>
	 * <li>sigaa.war/ensino/movimentacao_aluno/concluir_stricto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarAfastamento() throws ArqException {
		
		if (obj.getDiscente() == null || isEmpty(obj.getDiscente())) {
			addMensagemErro("Por favor, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return cancelar();
		}
		
		verificaOperacaoAtiva();
		
		if (operacao == OperacaoDiscente.RETORNO_AFASTAMENTO_ALUNO)
			return cadastrarRetorno();

		if( !confirmaSenha() )
			return null;

		erros = obj.validateAfastamento();
		if (isConclusao() && obj.getDataColacaoGrau() == null) {
			addMensagemErro("Informe a data de colação de grau");
			return null;
		}

		if (hasErrors())
			return null;

		obj.setTipoMovimentacaoAluno(getGenericDAO().findByPrimaryKey(obj.getTipoMovimentacaoAluno().getId(), TipoMovimentacaoAluno.class));
		obj.setAnoOcorrencia(CalendarUtils.getAnoAtual());
		obj.setPeriodoOcorrencia(getPeriodoAtual());

		MovimentoMovimentacaoAluno mov = new MovimentoMovimentacaoAluno();
		mov.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
		mov.setObjMovimentado(obj);
		mov.setContext( WebApplicationContextUtils.getWebApplicationContext(getCurrentSession().getServletContext()) );
		
		try {
			execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		addMessage(tituloOperacao + " realizado com sucesso para o aluno " +
				obj.getDiscente().getMatriculaNome() + " no período "+obj.getAnoPeriodoReferencia(), TipoMensagemUFRN.INFORMATION);
		addMensagens(mov.getMensagens());
		afterCadastrar();
		if ( isTrancamento() )
			return iniciarTrancamentoPrograma();
		else
			return cancelar();
	}

	/**
	 * Registrar o retorno de um afastamento.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarRetorno() throws ArqException {
		verificaOperacaoAtiva(); 
		// verifica se o retorno é posterior ao ano/período atual
		String anoPeriodo = String.valueOf(afastamento.getAnoReferencia()*10 + afastamento.getPeriodoReferencia());
		CalendarioAcademico calendarioVigente = CalendarioAcademicoHelper.getCalendario(afastamento.getDiscente());
		String anoPeriodoAtual = String.valueOf(calendarioVigente.getAno()*10 + calendarioVigente.getPeriodo());
		if (afastamento.getDiscente().isTrancado() && anoPeriodo.compareTo(anoPeriodoAtual) > 0) {
			addMensagemErro("Não é possível retornar um trancamento posterior ao ano/período atual. Para tanto, utilize a opção Cancelar Trancamentos Futuros.");
			return null; 
		}
		if( obj.getDiscente().isStricto() || obj.getDiscente().isLato()){
			if( obj.getDataRetorno() == null ){
				addMensagemErro("Informe a data de retorno do aluno.");
			}
			if (obj.getDiscente().isLato() && isEmpty( obj.getTipoRetorno())){
				addMensagemErro("Tipo de Retorno: campo obrigatório não informado.");
			}
			int valor = CalendarUtils.calculoMeses(obj.getInicioAfastamento(), obj.getDataRetorno());
			obj.setValorMovimentacao( valor );
		}else{
			if( isEmpty( obj.getTipoRetorno() ) ){
				addMensagemErro("Tipo de Retorno: campo obrigatório não informado.");
			}
		}
		if (hasErrors())
			return null;
		afastamento.setDataRetorno(obj.getDataRetorno());
		afastamento.setValorMovimentacao(obj.getValorMovimentacao());
		afastamento.setTipoRetorno(obj.getTipoRetorno());
		afastamento.setObservacao(obj.getObservacao());
		afastamento.setAnoOcorrencia(CalendarUtils.getAnoAtual());
		afastamento.setPeriodoOcorrencia(getPeriodoAtual());
		
		MovimentoAfastamentoAluno mov = new MovimentoAfastamentoAluno();
		mov.setCodMovimento(SigaaListaComando.RETORNAR_ALUNO_AFASTADO);
		mov.setObjMovimentado(afastamento);
		try {
			execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		afterCadastrar();
		addMessage("Retorno cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);
		return cancelar();
	}
	
	/**
	 * Listener que atualiza o afastamento escolhido quando o usuário escolhe no
	 * formulário.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/form_retorno.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void anoPeriodoChange(ValueChangeEvent e) throws DAOException {
		afastamento = getGenericDAO().findByPrimaryKey((Integer) e.getNewValue(), MovimentacaoAluno.class);
	}

	/**
	 * Redireciona para a tela de confirmação de estorno.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String selecionarEstorno() throws DAOException{

		int id = getParameterInt("id");
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);
		obj = dao.findByPrimaryKey(id, MovimentacaoAluno.class);		  
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(obj.getDiscente());
		// Caso a operação que esteja sendo estornada seja de trancamento E o ano.periodo de ocorrência da movimentação
		// for superior ao ano.periodo atual os trancamentos futuros devem ser automaticamente estornados também
		if( obj.isTrancamento() &&
				(obj.getAnoReferencia() > cal.getAno() || ( obj.getAnoReferencia() == cal.getAno() && obj.getPeriodoReferencia() >= cal.getPeriodo() ) ) ){

			trancamentosFuturos = dao.findTrancamentosFuturosByDiscente(obj.getDiscente().getId(), obj.getAnoReferencia(), obj.getPeriodoReferencia());
			if( !isEmpty( trancamentosFuturos ) ){
				addMensagemWarning("ATENÇÃO! Existem trancamentos futuros lançados para este discente. Estes trancamentos também serão cancelados.");
			}

		}

		statusRetorno = DiscenteHelper.getUltimoStatus(obj.getDiscente().getDiscente());
		if( statusRetorno == null )
			statusRetorno = 0;

		return forward( JSP_CONFIRMACAO_ESTORNO );

	}

	/**
	 * Estorna um registro de afastamento.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String estornarAfastamento() throws ArqException {

		verificaOperacaoAtiva();
		
		if( !StatusDiscente.getStatusTodos().contains( new StatusDiscente(statusRetorno) )  ){
			addMensagemErro("É necessário selecionar o status que o discente deve possuir após o estorno!");
			return null;
		}

		MovimentoCadastro mov = new MovimentoCadastro();
		obj.setStatusRetorno( statusRetorno );
		mov.setCodMovimento(SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO);
		mov.setObjMovimentado(obj);
		try {
			execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		afterCadastrar();
		addMessage("Afastamento estornado com sucesso!", TipoMensagemUFRN.INFORMATION);
		return cancelar();
	}

	/** Verifica se o caso de uso foi iniciado corretamente.
	 * @throws ArqException
	 */
	private void verificaOperacaoAtiva() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.RETORNAR_ALUNO_AFASTADO.getId(),
				SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO.getId(),
				SigaaListaComando.CANCELAR_TRANCAMENTO_PROGRAMA.getId(),
				SigaaListaComando.AFASTAR_ALUNO.getId()))
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
	}

	/**
	 * Inicia o registro de retorno de um afastamento.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>sigaa.war/stricto/menus/matricula.jsp</li>
	 * <li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/menus/menu_lato_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarRetorno() throws ArqException  {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.SECRETARIA_POS , SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		prepareMovimento(SigaaListaComando.RETORNAR_ALUNO_AFASTADO);
		setOperacaoAtiva(SigaaListaComando.RETORNAR_ALUNO_AFASTADO.getId());
		operacao = OperacaoDiscente.RETORNO_AFASTAMENTO_ALUNO;
		tituloOperacao = "Retorno de Afastamento";
		setConfirmButton("Confirme o Retorno de Afastamento");
		return buscarDiscente();
	}

	/**
	 * Inicia o estorno de um registro de afastamento.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/menus/menu_lato_coordenador.jsp</li>
	 * <li>sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>sigaa.war/stricto/menus/matricula.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarEstorno() throws ArqException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.GESTOR_TECNICO, 
				SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.PPG, SigaaPapeis.DAE,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		prepareMovimento(SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO);
		setOperacaoAtiva(SigaaListaComando.ESTORNAR_AFASTAMENTO_ALUNO.getId());
		operacao = OperacaoDiscente.ESTORNO_AFASTAMENTO_ALUNO;
		tituloOperacao = "Estorno do Afastamento";
		setConfirmButton("Confirme o Estorno do Afastamento");
		return buscarDiscente();
	}

	/**
	 * Inicia o caso de uso de cancelamento de trancamento.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/trancamentos.jsp</li>
	 * <li>sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>sigaa.war/stricto/menus/matricula.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCancelamentoTrancamento() throws ArqException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA);
		prepareMovimento(SigaaListaComando.CANCELAR_TRANCAMENTO_PROGRAMA);
		setOperacaoAtiva(SigaaListaComando.CANCELAR_TRANCAMENTO_PROGRAMA.getId());
		operacao = OperacaoDiscente.CANCELAR_TRANCAMENTO;
		tituloOperacao = "Cancelar Trancamentos";
		setConfirmButton("Confirme o Cancelamento de Trancamentos");
		return buscarDiscente();
	}

	/**
	 * Inicia o trancamento de programa de discentes.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>sigaa.war/stricto/menus/matricula.jsp</li>
	 * <li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarTrancamentoPrograma() throws ArqException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.COORDENADOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA);
		prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
		setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		operacao = OperacaoDiscente.TRANCAR_PROGRAMA;
		tituloOperacao = "Trancamento do Programa";
		setConfirmButton("Confirme o Trancamento do Programa");
		carregarTipos(StatusDiscente.TRANCADO);
		obj.setAnoReferencia(getCalendarioVigente().getAno());
		obj.setPeriodoReferencia(getCalendarioVigente().getPeriodo());
		return buscarDiscente();
	}

	/**
	 * Inicia o caso de uso de cancelar o programa do discente utilizando a busca geral de discentes.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/menus/menu_lato_coordenador.jsp</li>
	 * <li>sigaa.war/graduacao/menus/cdp.jsp</li>
	 * <li>sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 * <li>sigaa.war/stricto/menus/matricula.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws ArqException
	 */
	public String iniciarCancelamentoPrograma() throws ArqException  {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO, 
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA);
		prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
		setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		initObj();
		operacao = OperacaoDiscente.CANCELAR_PROGRAMA;
		tituloOperacao = "Cancelamento do Programa";
		setConfirmButton("Confirme o Cancelamento do Programa");
		carregarTipos(StatusDiscente.CANCELADO);
		return buscarDiscente();
	}

	/**
	 * Inicia o caso de uso de cancelar o programa do discente
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/relatorios/discente/lista_alunos_nao_matriculados_online.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCancelamentoDiscentePrograma() throws ArqException  {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO
				, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG);
		
		int idDiscente = getParameterInt("idDiscente", 0);
		
		if (idDiscente > 0){
			prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
			setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
			initObj();
			
			operacao = OperacaoDiscente.CANCELAR_PROGRAMA;
			tituloOperacao = "Cancelamento do Programa";
			setConfirmButton("Confirme o Cancelamento do Programa");
			carregarTipos(StatusDiscente.CANCELADO);
			
			obj.setDiscente( getGenericDAO().findByPrimaryKey(idDiscente, Discente.class) );
			
			// Validar status válidos para cancelamento de programa
			List<Integer> statusValidos = Arrays.asList(StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.AFASTADO, StatusDiscente.FORMANDO, StatusDiscente.TRANCADO);
			if ( !statusValidos.contains( obj.getDiscente().getStatus() ) ) {
				addMensagemErro("O discente escolhido não pode ter seu programa cancelado pois não possui um vínculo ativo.");
				return null;
			}
			
			setDiscenteNaoMatriculadoOnline(true);
			
			return forward(JSP_FORM_AFASTAMENTO);						
		} else 
			return null;
	}

	/**
	 * Inicia a conclusão do discente
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/graduacao/menus/programa.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/menus/menu_lato_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String iniciarConclusaoPrograma() throws ArqException  {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO);
		prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
		setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		operacao = OperacaoDiscente.CONCLUIR_ALUNO;
		tituloOperacao = "Conclusão de Programa";
		setConfirmButton("Confirme a Conclusão de Programa");
		carregarTipos(StatusDiscente.CONCLUIDO);
		return buscarDiscente();
	}


	/** Indica se a operação é de conclusão.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/form_afastamento.jsp</li>
	 * <li>sigaa.war/ensino/movimentacao_aluno/resumo.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isConclusao() {
		return operacao == OperacaoDiscente.CONCLUIR_ALUNO;
	}

	/** Indica se a operação é de trancamento de programa.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/form_afastamento.jsp</li>
	 * <li>sigaa.war/ensino/movimentacao_aluno/resumo.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isTrancamento() {
		return operacao == OperacaoDiscente.TRANCAR_PROGRAMA;
	}

	/**
	 * Verifica se é operação de cancelamento do discente da instituição
	 *  
	 *  <br />
	 * Método não chamado de JSP(s):
	 *
	 */
	public boolean isCancelamento() {
		return operacao == OperacaoDiscente.CANCELAR_PROGRAMA;
	}
	
	/**
	 *  Verifica se é operação de afastamento do discente da instituição
	 *  
	 *  <br />
	 * Método não chamado de JSP(s):
	 *
	 */
	private boolean isAfastamento() {
		return operacao == OperacaoDiscente.AFASTAMENTO_ALUNO;
	}
	
	/**
	 *  Verifica se é operação de conclusão do discente de stricto da instituição
	 *  
	 *  <br />
	 * Método não chamado de JSP(s):
	 *
	 */
	public boolean isConclusaoAlunoStricto() {
		return operacao == OperacaoDiscente.CONCLUIR_ALUNO_STRICTO;
	}
	
	/**
	 * Verifica se é alguma operação de afastamento do discente da instituição
	 *  
	 *  <br />
	 * Método não chamado de JSP(s):
	 *
	 */
	public boolean isOperacaoAfastamento() {
		return isConclusao() ||  isTrancamento()  || isCancelamento() ||  isAfastamento() ||  isConclusaoAlunoStricto();
	}
	
	/**
	 * Inicia a conclusão de discente de técnico.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarConclusaoProgramaTecnico() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
		setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		operacao = OperacaoDiscente.AFASTAMENTO_ALUNO;
		tituloOperacao = "Conclusão de Programa";
		setConfirmButton("Confirme a Conclusão do Programa");
		carregarTipos(StatusDiscente.CONCLUIDO);
		return buscarDiscente();
	}

	/**
	 * Inicia a conclusão de discente de lato.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarConclusaoProgramaLato() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA);
		prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
		setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		operacao = OperacaoDiscente.AFASTAMENTO_ALUNO;
		tituloOperacao = "Conclusão de Programa";
		setConfirmButton("Confirme a Conclusão do Programa");
		carregarTipos(StatusDiscente.CONCLUIDO);
		return buscarDiscente();
	}

	/**
	 * Inicia a conclusão de discente de STRICTO.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/stricto/menus/matricula.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarConclusaoProgramaStricto() throws ArqException {
		checkRole(SigaaPapeis.PPG);
		prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
		setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		operacao = OperacaoDiscente.CONCLUIR_ALUNO_STRICTO;
		tituloOperacao = "Conclusão de Programa";
		setConfirmButton("Confirme a Conclusão do Programa");
		carregarTipos(StatusDiscente.CONCLUIDO);
		return buscarDiscente();
	}

	/**
	 * Inicia o cadastro para qualquer tipo de afastamento.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp</li>
	 * <li>sigaa.war/WEB-INF/jsp/menus/menu_lato_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAfastamento() throws ArqException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.SECRETARIA_TECNICO,
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
		setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		operacao = OperacaoDiscente.AFASTAMENTO_ALUNO;
		tituloOperacao = "Cadastro de Afastamento";
		setConfirmButton("Confirme o Cadastro de Afastamento");
		carregarTipos(StatusDiscente.AFASTADO);
		return buscarDiscente();
	}

	/** Carrega os tipos de afastamento.
	 * @param statusDiscente
	 * @throws DAOException
	 */
	private void carregarTipos(Integer statusDiscente) throws DAOException  {
		TipoMovimentacaoAlunoDao dao = getDAO(TipoMovimentacaoAlunoDao.class);
		Collection<TipoMovimentacaoAluno> tipos = null;

		if (statusDiscente != null && statusDiscente != StatusDiscente.AFASTADO)
			tipos = dao.findAtivosByStatusDiscente(statusDiscente, getNivelEnsino());
		else
			tipos = dao.findAtivos(getNivelEnsino());

		if (tipos != null && tipos.size() == 1) {
			tiposAfastamentos = toSelectItems(tipos, "id", "descricao");
			obj.setTipoMovimentacaoAluno(tipos.iterator().next());
		} else {
			tiposAfastamentos = toSelectItems(tipos, "id", "descricao");
		}
	}

	/** Indica se existe mais de um tipo de afastamento carregado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * @return 
	 */
	public boolean temVariosTiposMovimentacao() {
		return tiposAfastamentos != null && tiposAfastamentos.size() > 1;
	}

	/**
	 * Prepara e redireciona para a página de busca de discente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/form_afastamento.jsp</li>
	 * <li>sigaa.war/ensino/movimentacao_aluno/concluir_stricto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarDiscente() throws SegurancaException {
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(operacao);
		return buscaDiscenteMBean.popular();
	}

	/**
	 * Redireciona para o formulário específico de acordo com a operação.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/resumo.jsp</li>
	 * <li>sigaa.war/ensino/movimentacao_aluno/selecao_matriculas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public String telaDadosMovimentacao() throws DAOException {
		switch (operacao) {
		case OperacaoDiscente.RETORNO_AFASTAMENTO_ALUNO:
			return forward(JSP_FORM_RETORNO);

		case OperacaoDiscente.ESTORNO_AFASTAMENTO_ALUNO:
			return forward(JSP_CONFIRMACAO_ESTORNO);

		case OperacaoDiscente.CANCELAR_TRANCAMENTO:
			return forward(JSP_TRANCAMENTOS);

		case OperacaoDiscente.CONCLUIR_ALUNO_STRICTO:
			if (obj.getDiscente().getStatus() != StatusDiscente.EM_HOMOLOGACAO) {
				addMensagemErro("Não é possível concluir o aluno porque ele não está em processo de homologação.");
				return null;
			}

			homologacao = getDAO(HomologacaoTrabalhoFinalDao.class).findUltimoByDiscente(obj.getDiscente().getId());
			if (homologacao != null) // obrigatoriedade removida temporariamente
				homologacao.getBanca().getMembrosBanca().iterator();
			return forward(JSP_CONCLUIR_STRICTO);
		case OperacaoDiscente.CONCLUIR_ALUNO :
			return forward(JSP_FORM_AFASTAMENTO);
		default:
			return forward(JSP_FORM_AFASTAMENTO);
		}
	}

	/**
	 * Seleciona o discente da lista de resultados da busca por discente.
	 * Algumas validações são realizadas ao escolher um discente de acordo com o
	 * tipo de operação (afastamento, retorno ou estorno).
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 * @throws NumberFormatException
	 */
	public String selecionaDiscente() throws ArqException {
		// carregando histórico de movimentações
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);
		DiscenteDao ddDao = getDAO(DiscenteDao.class);
		try {
			carregarHistoricoMovimentacoes(dao);
			
			switch (operacao) {
			
			case OperacaoDiscente.RETORNO_AFASTAMENTO_ALUNO: {
				MovimentacaoAlunoValidator.validarRetornoAfastamentoAluno(obj,afastamento,getUsuarioLogado(),dao,erros);
				break;
			}
			case OperacaoDiscente.ESTORNO_AFASTAMENTO_ALUNO: {
				MovimentacaoAlunoValidator.validarEstornoAfastamentoAluno(obj,statusRetorno,statusAnterior,dao,erros);
				break;
			}
			case OperacaoDiscente.CANCELAR_TRANCAMENTO: {
				MovimentacaoAlunoValidator.validarCancelarTrancamento(historicoMovimentacoes,erros);
				break;
			}
			case OperacaoDiscente.CANCELAR_PROGRAMA: {
				MovimentacaoAlunoValidator.validarCancelarPrograma(obj,erros);
				break;
			}
			case OperacaoDiscente.CONCLUIR_ALUNO: {
				try {
					MovimentacaoAlunoValidator.validarConcluirAluno(obj,getUsuarioLogado(),erros);
				} catch (NegocioException e) {
					tratamentoErroPadrao(e);
				}
				break;
			}
			case OperacaoDiscente.AFASTAMENTO_ALUNO: {
				try {
					MovimentacaoAlunoValidator.validarAfastamentoAluno(obj,getUsuarioLogado(),erros);
				} catch (NegocioException e) {
					tratamentoErroPadrao(e);
				}
				break;
				
			}
			case OperacaoDiscente.CONCLUIR_ALUNO_STRICTO: {
				try {
					MovimentacaoAlunoValidator.validarConcluirAlunoStricto(obj,erros);
				} catch (NegocioException e) {
					tratamentoErroPadrao(e);
				}
				break;
			}
			case OperacaoDiscente.TRANCAR_PROGRAMA :
				MovimentacaoAlunoValidator.validarTrancarPrograma(obj,getUsuarioLogado(),erros);
				break;
		}
		if (hasErrors()) 
			return null;

		// Necessário fazer reload do discente poque a referencia que vem em MovimentacaoAluno é DiscenteAdapter
		obj.setDiscente(ddDao.findDiscenteAdapterById(obj.getDiscente().getId()));
		
		return telaDadosMovimentacao();			
			
		} catch (Exception e) {
			return tratamentoErroPadrao(e);
		}

		
	}
		
	/**
	 * <p>Verifica se o discente possui emprétimos abertos com o vínculo de discente, se possui não será 
	 * possível concluir ou trancar o discente enquanto não devolver os emprétimos.</p>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @param discente
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public void possuiPendenciasBiblioteca(DiscenteAdapter discente, ListaMensagens erros) throws DAOException, NegocioException {
		
		erros.addAll(VerificaSituacaoUsuarioBibliotecaUtil.verificaEmprestimoPendenteDiscente(discente.getDiscente()));
	}

	
	
	/** Carrega o histórico das movimentações.
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarHistoricoMovimentacoes(MovimentacaoAlunoDao dao ) throws DAOException {
		try {
			historicoMovimentacoes = dao.findByDiscente(obj.getDiscente().getId(),true);
			historicoCancelamentos = dao.findByDiscente(obj.getDiscente().getId(),false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Não foi possível carregar o histórico de movimentações do aluno");
		}

		if (operacao == OperacaoDiscente.CANCELAR_TRANCAMENTO) {
			int anoPeriodoAtual = 0;
			try {
				anoPeriodoAtual = new Integer (getCalendarioVigente().getAno() + "" + getCalendarioVigente().getPeriodo());
			} catch (Exception e) {
				notifyError(e);
				addMensagemErro("Erro ao carregar parâmetros acadêmicos");
				return;
			}

			// filtrar o histórico para os trancamentos de semestres futuros
			Collection<MovimentacaoAluno> novoHistorico = new ArrayList<MovimentacaoAluno>(0);
			MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class );
			for (MovimentacaoAluno mov : historicoMovimentacoes) {
				int anoPeriodoMov = mov.getAnoReferencia()*10+mov.getPeriodoReferencia();
				if (mov.isTrancamento() && anoPeriodoMov == anoPeriodoAtual) {
					Collection<MatriculaComponente> matriculadas = mcdao.findMatriculadasByDiscenteAnoPeriodo(obj.getDiscente(), getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), TipoComponenteCurricular.getNaoAtividades());
					if (isEmpty(matriculadas))
						novoHistorico.add(mov);
				}
				if (mov.isTrancamento() &&  anoPeriodoMov > anoPeriodoAtual) {
					novoHistorico.add(mov);
				}
			}
			historicoMovimentacoes = novoHistorico;
		}
	}

	/**
	 * Chama o processador para cancelar um trancamento.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/trancamentos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String cancelarTrancamento() throws ArqException {
		verificaOperacaoAtiva();
		MovimentacaoAluno tranc = new MovimentacaoAluno( getParameterInt("id"));
		MovimentoAfastamentoAluno mov = new MovimentoAfastamentoAluno();
		mov.setCodMovimento(SigaaListaComando.CANCELAR_TRANCAMENTO_PROGRAMA);
		mov.setObjMovimentado(tranc);
		try {
			execute(mov, getCurrentRequest());
			addMessage("Trancamento cancelado com sucesso.", TipoMensagemUFRN.INFORMATION);
			return iniciarCancelamentoTrancamento();
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Recebe os dados do afastamento e redireciona para a tela de matriculas do
	 * aluno.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/form_afastamento.jsp</li>
	 * <li>sigaa.war/ensino/movimentacao_aluno/concluir_stricto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String submeterDadosAfastamento() throws DAOException {
		
		if (obj.getDiscente() == null || isEmpty(obj.getDiscente())) {
			addMensagemErro("Por favor, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return cancelar();
		}
		
		erros = obj.validateAfastamento();


		/** 
		 * <p>Mensagem de alerta que deve ser mostrada para quem está cancelando o vínculo do discente na instituição. </p>
		 * 
		 * <p>Porém aqui o afastamento não será bloqueado. Apenas um alerta para o usuário. </p>
		 * 
		 */
		if ( isOperacaoAfastamento() ){
			 if( VerificaSituacaoUsuarioBibliotecaUtil.temPendencia(obj.getDiscente().getDiscente())   ){
				
				 addMensagemWarning(" Existe um relatório no módulo de bibliotecas que permite a verificação dos discentes afastados que possuem empréstimos abertos, a biblioteca ficará responsável por cobrar o material dos vínculos cancelados.");
				 
				 addMensagemWarning(" Aconselha-se não afastar o discente nos casos em que ele será beneficiado, por exemplo, na conclusão do curso. Caso o afastamento seja para cancelamento de vínculo, o afastamento deve ser realizado.");
				 
				 addMensagemWarning(" Esse discente possui empréstimos abertos na biblioteca, caso seja afastado ficará com um bem da instituição.");
			 }
		}
		
		if (DiscenteHelper.somaSemestres(obj.getAnoReferencia(),obj.getPeriodoReferencia(), 0) >
		    DiscenteHelper.somaSemestres(getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo(), 0)) {
			erros.addErro("O Ano-Período informado é superior ao atual.");
		}
			
		if (hasErrors())
			return null;
		return telaResumo();
	}

	/**
	 * Retorna uma coleção de MatriculaComponente em que o discente está
	 * matriculado, em espera, reprovado por falta, ou reprovado.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/selecao_matriculas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<MatriculaComponente> getMatriculasDiscente() {
		try {
			MatriculaComponenteDao matDao = getDAO(MatriculaComponenteDao.class);
			
			Collection<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>();
			situacoes.add(SituacaoMatricula.MATRICULADO);
			situacoes.add(SituacaoMatricula.EM_ESPERA);
			situacoes.add(SituacaoMatricula.REPROVADO_FALTA);
			situacoes.add(SituacaoMatricula.REPROVADO);
			situacoes.add(SituacaoMatricula.REPROVADO_MEDIA_FALTA);
			
			return matDao.findByDiscenteOtimizado(obj.getDiscente(), TipoComponenteCurricular.getAll(), situacoes);
		} catch (Exception e) {
			addMensagemErro("Erro ao carregar matrículas do aluno escolhido");
		}
		return null;
	}

	/**
	 * Trata as matriculas que serão trancadas ou canceladas.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/selecao_matriculas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String submeterSelecaoMatriculas() {
		try {
			String[] selecaoMatriculas = getCurrentRequest().getParameterValues("selecaoMatriculas");
			if (!isEmpty(selecaoMatriculas)) {
				GenericDAO dao = getGenericDAO();
				obj.setMatriculasAlteradas(new ArrayList<MatriculaComponente>(0));
				for (Integer id : ArrayUtils.toIntArray(selecaoMatriculas)) {
					if (id != null) {
						obj.getMatriculasAlteradas().add(dao.findByPrimaryKey(id, MatriculaComponente.class));
					}
				}
			}
		} catch (Exception e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		return telaResumo();
	}
	
	/** Retorna uma lista de movimentações a partir do ano-período atual.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/form_retorno.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getMovimentacoesCombo(){
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		if (getHistoricoMovimentacoes() != null) {
			for (MovimentacaoAluno mov : getHistoricoMovimentacoes()){
				if (mov.isCancelamento() || mov.getAnoReferencia() > CalendarUtils.getAnoAtual()
						|| mov.getAnoReferencia() == CalendarUtils.getAnoAtual() && mov.getPeriodoReferencia() >= getPeriodoAtual()) {
					SelectItem item = new SelectItem(mov.getId(), mov.getDescricao());
					combo.add(item);
				}
			}
		}
		return combo;
	}

	/**
	 * Retorna o formulário de resumo da movimentação do aluno.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaResumo() {
		return forward("/ensino/movimentacao_aluno/resumo.jsp");
	}

	/**
	 * Retorna o formulário para seleção de matrículas a cancelar/trancar<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaMatriculasAluno() {
		return forward("/ensino/movimentacao_aluno/selecao_matriculas.jsp");
	}

	/** 
	 * Seta o discente escolhido da lista de resultados da busca.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>Não Invocado por JSP.</li>
	 * </ul>
	 */
	public void setDiscente(DiscenteAdapter discente) {
		if (obj == null)
			initObj();
		obj.setDiscente(discente);
	}

	/** Retorna os possíveis tipos de afastamentos. 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/ensino/movimentacao_aluno/form_afastamento.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getTiposAfastamentos() throws DAOException {
		if (tiposAfastamentos == null) {
			carregarTipos(0);
		}
		return tiposAfastamentos;
	}

	public void setTiposAfastamentos(Collection<SelectItem> tiposAfastamentos) {
		this.tiposAfastamentos = tiposAfastamentos;
	}

	public String getTituloOperacao() {
		return tituloOperacao;
	}

	public void setTituloOperacao(String tituloOperacao) {
		this.tituloOperacao = tituloOperacao;
	}

	public Collection<MovimentacaoAluno> getHistoricoCancelamentos() {
		return historicoCancelamentos;
	}

	public void setHistoricoCancelamentos(
			Collection<MovimentacaoAluno> historicoCancelamentos) {
		this.historicoCancelamentos = historicoCancelamentos;
	}

	public boolean isStricto() {
		return obj.getDiscente().isStricto();
	}

	public HomologacaoTrabalhoFinal getHomologacao() {
		return homologacao;
	}

	public void setHomologacao(HomologacaoTrabalhoFinal homologacao) {
		this.homologacao = homologacao;
	}

	public Integer getStatusRetorno() {
		return statusRetorno;
	}

	public void setStatusRetorno(Integer statusRetorno) {
		this.statusRetorno = statusRetorno;
	}

	public Collection<MovimentacaoAluno> getMovimentacoesRetornadas() {
		return movimentacoesRetornadas;
	}

	public void setMovimentacoesRetornadas(
			Collection<MovimentacaoAluno> movimentacoesRetornadas) {
		this.movimentacoesRetornadas = movimentacoesRetornadas;
	}

	public Collection<MovimentacaoAluno> getTrancamentosFuturos() {
		return trancamentosFuturos;
	}

	public void setTrancamentosFuturos(Collection<MovimentacaoAluno> trancamentosFuturos) {
		this.trancamentosFuturos = trancamentosFuturos;
	}

	public Collection<MovimentacaoAluno> getHistoricoMovimentacoes() {
		return historicoMovimentacoes;
	}

	public void setHistoricoMovimentacoes(Collection<MovimentacaoAluno> historicoMovimentacoes) {
		this.historicoMovimentacoes = historicoMovimentacoes;
	}

	public Collection<SelectItem> getTiposRetorno() {
		return tiposRetorno;
	}

	public MovimentacaoAluno getAfastamento() {
		return afastamento;
	}

	public void setAfastamento(MovimentacaoAluno afastamento) {
		this.afastamento = afastamento;
	}

	public boolean isDiscenteNaoMatriculadoOnline() {
		return discenteNaoMatriculadoOnline;
	}

	public void setDiscenteNaoMatriculadoOnline(boolean discenteNaoMatriculadoOnline) {
		this.discenteNaoMatriculadoOnline = discenteNaoMatriculadoOnline;
	}

	
}
