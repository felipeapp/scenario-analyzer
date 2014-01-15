/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 15/04/2010 
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.SubTipoDocumentoAutenticado;
import br.ufrn.arq.seguranca.autenticacao.TipoDocumentoAutenticado;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoTrancamentoProgramaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MotivoTrancamentoPrograma;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoPrograma;
import br.ufrn.sigaa.ensino.dominio.StatusSolicitacaoTrancamentoPrograma;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.MovimentacaoAlunoMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoMovimentacaoAluno;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed Bean para o trancamento de programa.
 *
 * @author Arlindo Rodrigues
 */
@Component("trancamentoPrograma") 
@Scope("request")
public class TrancamentoProgramaMBean extends SigaaAbstractController<SolicitacaoTrancamentoPrograma> implements OperadorDiscente, AutValidator {
	/** Caminho do Formul�rio de Solicita��o de Trancamento */
	private static final String FORM_SOLICITACAO = "/ensino/trancamento_programa/solicitacao.jsp";
	/** Comprovante de Solicita��o de Trancamento */
	private static final String COMPROVANTE_SOLICITACAO = "/ensino/trancamento_programa/comprovante_solicitacao.jsp";
	/** Lista de Trancamento */
	private static final String LISTA_TRANCAMENTOS = "/ensino/trancamento_programa/lista_trancamentos.jsp";
	/** Formul�rio de Submiss�o da Solicita��o de Trancamento de Programa */
	private static final String FORM_SUBMETER = "/ensino/trancamento_programa/form_submeter.jsp";	
	/** Formul�rio de Indeferimento da Solicita��o de Trancamento de Programa */
	private static final String FORM_OBSERVACAO = "/ensino/trancamento_programa/form_observacao.jsp";
	/** Tela explicativa sobre o trancamento de programa*/
	private static final String INTRUCOES_TRANCAMENTO = "/ensino/trancamento_programa/instrucoes.jsp";
	
	/** Lista de Solicita��es de Trancamento */
	private Collection<SolicitacaoTrancamentoPrograma> solicitacoes;
	
	/** Discente selecionado. */
	private DiscenteAdapter discente;
	
	/** Observa��o que sair� no hist�rico do aluno */
	private String observacao;
	/** Obriga o Discente marcar para prosseguir */
	private boolean estaCiente;	
	/** C�digo de seguran�a para valida��o do relat�rio */
	private String codigoSeguranca;
	
	/** Atributo que define o estado da gera��o da solicita��o de trancamento. */
	private boolean verificando;
	
	/** Comprovante da solicitacao */
	private EmissaoDocumentoAutenticado comprovante;

	/**
	 * Construtor da classe
	 */
	public TrancamentoProgramaMBean() {
		initObj();
	}
	
	/**
	 * Inicializa os objetos
	 */
	private void initObj(){
		if (obj == null)
			obj = new SolicitacaoTrancamentoPrograma();
	}

	/**
	 * Redireciona usu�rio para o formul�rio de trancamento.
	 * * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/instrucoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarSolicitacao() throws ArqException {
		
		
		if (!estaCiente) {
			addMensagemErro("� necess�rio confirmar que est� ciente das condi��es para prosseguir para a tela de confirma��o.");
			return null;
		}
		
		if (obj.isPosteriori()) {
			validateTrancamentoPosteriori();
			if (hasErrors())
				return null;
			prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
			setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		}		
		
		if (!obj.isPosteriori()) {
			EmprestimoDao emprestimoDao = getDAO(EmprestimoDao.class);
			Long qtdEmprestimosAtivoVinculo = 1l;
			
			qtdEmprestimosAtivoVinculo = emprestimoDao.countEmprestimosAtivosPorVinculoUsuario(discente.getPessoa().getId(),  VinculoUsuarioBiblioteca.ALUNO_GRADUACAO, discente.getId());
			
			if (qtdEmprestimosAtivoVinculo > 0) {
				addMensagemWarning("Foram identificados " + qtdEmprestimosAtivoVinculo + " empr�stimos ativos de materiais bibliogr�ficos. Eles dever�o ser devolvidos � biblioteca antes da efetiva��o do trancamento.");	
			}
			
		}
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		return forward(FORM_SOLICITACAO);
	}
	
	/**
	 * Inicia o caso de uso de solicita��o de trancamento. 
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarTrancamentoRegular() throws ArqException {
				
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		discente = getUsuarioLogado().getDiscente();
		if (discente.getTipo() == Discente.ESPECIAL) {
			addMensagemErro("Discentes do tipo Especial n�o est�o autorizados a efetuar Trancamento do Programa atrav�s dessa opera��o.");
			return null;
		}
		
		try {
			
			if (discente.isGraduacao())
				discente = discenteDao.findDiscenteAdapterById(discente.getId());
			
			obj.setDiscente(discente.getDiscente());	
		} finally {
			if (discenteDao != null)
				discenteDao.close();
		}
		
		obj.setPosteriori(false);
		obj.setAno(getCalendarioVigente().getAno());
		obj.setPeriodo(getCalendarioVigente().getPeriodo());
		
		validateTrancamento();
		
		if (hasErrors())
			return null;
		
		estaCiente = false;

		return forward(INTRUCOES_TRANCAMENTO);
	}
	
	
	/**
	 * Inicia o caso de uso de solicita��o de trancamento a posteriori. 
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarTrancamentoPosteriori() throws ArqException {
		
		discente = getUsuarioLogado().getDiscente();
		if (discente.getTipo() == Discente.ESPECIAL) {
			addMensagemErro("Discentes do tipo Especial n�o est�o autorizados a efetuar Trancamento do Programa atrav�s dessa opera��o.");
			return null;
		}
		
		CalendarioAcademico cAnterior = CalendarioAcademicoHelper.getCalendario(getCalendarioVigente().getAnoAnterior(), getCalendarioVigente().getPeriodoAnterior(), discente.getCurso().getUnidade(),
				 discente.getNivel(), null, null, null);
		
		DiscenteDao discenteDao = getDAO(DiscenteDao.class);
		
		try {
			
			if (discente.isGraduacao())
				discente = discenteDao.findDiscenteAdapterById(discente.getId());
			
			obj.setDiscente(discente.getDiscente());
		} finally {
			discenteDao.close();
		}
		
		obj.setPosteriori(true);
		obj.setAno(cAnterior.getAno());
		obj.setPeriodo(cAnterior.getPeriodo());
		
		validateTrancamento(); 
		
		if (hasErrors())
			return null;
		
		estaCiente = false;
		
		return forward(INTRUCOES_TRANCAMENTO);
	}
	
	
	/**
	 * Valida��o usada tanto no tracamento de programa regular como no trancamento a posteriori que valida
	 * as regras de neg�cio comum em ambos casos de uso. 
	 */
	private void validateTrancamento() throws DAOException {
		
		if( getUsuarioLogado() == null ){
			addMensagemErro("Problemas na identifica��o do usu�rio. Ser� necess�rio logar-se novamente e reiniciar a opera��o.");
			return;
		}
		
		if( !getUsuarioLogado().getDiscente().isGraduacao() /*&& !getUsuarioLogado().getDiscente().isStricto()*/){
			addMensagemErro("Discentes do n�vel " + getUsuarioLogado().getDiscente().getNivelDesc() + " n�o est�o autorizados a efetuar Trancamento do Programa atrav�s dessa opera��o.");
			return;
		}
		
		//Validar se o discente est� ativo
		if (!StatusDiscente.getStatusComVinculo().contains(getUsuarioLogado().getDiscente().getStatus())) {
			addMensagemErro("Somente discentes com v�nculo ativo podem realizar o trancamento do programa.");
			return;
		}
		
		if (!obj.isPosteriori()) {
			//Valida se est� dentro do per�odo para trancamento de programa.
			if (getCalendarioVigente().getInicioTrancamentoPrograma() == null 
					|| getCalendarioVigente().getFimTrancamentoPrograma() == null) {
				addMensagemErro("O per�odo de trancamento de programa n�o foi definido.");
				return ;
			} else if ((getCalendarioVigente().getInicioTrancamentoPrograma() != null 
					&& getCalendarioVigente().getFimTrancamentoPrograma() != null) 
					&& !getCalendarioVigente().isPeriodoTrancamentoPrograma()  ) {
				Formatador fmt = Formatador.getInstance();
				addMensagemErro(" O per�odo oficial para trancamento de programa estende-se de " +
				fmt.formatarData(getCalendarioVigente().getInicioTrancamentoPrograma()) + " a " + fmt.formatarData(getCalendarioVigente().getFimTrancamentoPrograma()) + ".");
				return;
			}
		} 
		else {	
			
			//Valida se est� dentro do per�odo para trancamento de programa a posteriori.
			if (getCalendarioVigente().getInicioTrancamentoProgramaPosteriori() == null 
					|| getCalendarioVigente().getFimTrancamentoProgramaPosteriori() == null) {
				addMensagemErro("O per�odo de trancamento de programa a posteriori n�o foi definido.");
				return ;
			}
			else if ((getCalendarioVigente().getInicioTrancamentoProgramaPosteriori()!=null && getCalendarioVigente().getFimTrancamentoProgramaPosteriori()!=null) && !getCalendarioVigente().isPeriodoTrancamentoProgramaPosteriori()  ) {
			Formatador fmt = Formatador.getInstance();
			addMensagemErro(" O per�odo oficial para trancamento de programa a posteriori estende-se de " +
			fmt.formatarData(getCalendarioVigente().getInicioTrancamentoProgramaPosteriori()) + " a " + fmt.formatarData(getCalendarioVigente().getFimTrancamentoProgramaPosteriori()) + ".");
			return;
			}
		}
		
		SolicitacaoTrancamentoProgramaDao dao = getDAO(SolicitacaoTrancamentoProgramaDao.class);
		MovimentacaoAlunoDao afasDao = getDAO(MovimentacaoAlunoDao.class);
		
		try {
			
			//Verifica se a quantidade de solicita��es foi excedida 
			ParametrosGestoraAcademica params = ParametrosGestoraAcademicaHelper.getParametros(getDiscenteUsuario());
			int quantAtual = afasDao.findTrancamentosByDiscente(discente.getId(), discente.getGestoraAcademica().getId(), discente.getNivel());
			int maxTrancamentos = params.getMaxTrancamentos().intValue(); 
			if (quantAtual >= maxTrancamentos){
				addMensagemErro("O limite m�ximo de trancamentos de " +  maxTrancamentos +" per�odos letivos regulares, consecutivos ou n�o, foi atingido. N�o ser� poss�vel fazer uma nova solicita��o de trancamento de programa.");
				return;				
			}
			
			/* Retorna o Hist�rico de Solicita��es */
			solicitacoes = dao.findSolicitacoesByDiscente(getUsuarioLogado().getDiscente());
			
			/*Verifica se o discente logado possui alguma solicita��o de trancamento no per�odo vigente */
			if (dao.quantSolicitacoesTrancamentoByDiscente(getDiscenteUsuario(), obj.getAno(), obj.getPeriodo()) > 0){
				addMensagemErro("J� existe uma solicita��o de trancamento do programa no per�odo vigente.");
				return;
			}
		} finally {
			if (dao != null)
				dao.close();
			if (afasDao != null)
				afasDao.close();
		}
		
		
		
	}
	
	
	/**
	 * Valida se o discente possuem os requisitos m�nimos para o solicita��o de trancamento a posteriori.
	 * Para fazer ese trancamento � necess�rio n�o ter aproveitado carga hor�ria, n�o ter sido reprovado por m�dia
	 * e falta em pelo menos um componente curricular
	 */
	private void validateTrancamentoPosteriori() throws DAOException {
		
		ParametrosGestoraAcademica param = getParametrosAcademicos();
		if (param == null) {
			addMensagem(MensagensTurmaVirtual.PARAMEROS_PARA_NIVEL_NAO_CONFIGURADOS, discente.getNivel());
			return;
		}
		
		CalendarioAcademico cAnterior = CalendarioAcademicoHelper.getCalendario(getCalendarioVigente().getAnoAnterior(), getCalendarioVigente().getPeriodoAnterior(), discente.getCurso().getUnidade(),
				 discente.getNivel(), null, null, null);
		
		
		MatriculaComponenteDao mcDao = getDAO(MatriculaComponenteDao.class);
		Collection<MatriculaComponente> matriculas = mcDao.findByDiscente(discente, cAnterior.getAno(), cAnterior.getPeriodo(), 
																		  SituacaoMatricula.MATRICULADO, SituacaoMatricula.CANCELADO, SituacaoMatricula.TRANCADO,
																		  SituacaoMatricula.APROVADO, SituacaoMatricula.APROVEITADO_DISPENSADO,
																		  SituacaoMatricula.REPROVADO ,SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO_MEDIA_FALTA); 
		
		if (isEmpty(matriculas)) {
			addMensagemErro("N�o foram encontradas matr�culas em componentes para o per�odo " + cAnterior.getAnoPeriodo());
			return;
		} else if (estaoTodosComponentesReprovadosFaltaOuMediaZero(matriculas)) {			
			addMensagemErro("N�o ser� poss�vel solicitar o trancamento a posteriori pois n�o foram identificadas matr�culas em atividades, ou reprova��es que n�o tenham sido por falta, com m�dia maior que 0 (zero), em ao menos um componente curricular no per�odo  " 
					+ getCalendarioVigente().getAnoAnterior() + "."+ getCalendarioVigente().getPeriodoAnterior() );
		}
		
	}

	private boolean estaoTodosComponentesReprovadosFaltaOuMediaZero(Collection<MatriculaComponente> matriculas) {
		boolean todosComponentesReprovadosOuFaltaMediaZero = true;
		for (MatriculaComponente mc: matriculas) {
			if (mc.getComponente().getBlocoSubUnidade() != null)
				continue;
			if (isAprovadoOuDispensado(mc)) {
				addMensagemErro("N�o ser� poss�vel solicitar o trancamento de programa pois foi identificada integraliza��o de carga hor�ria para o semestre.");
				return false;
			}
			else if (mc.isMatriculado() || (mc.isReprovado() && isAtividadeIndividual(mc)) || isReprovadoMediaMaiorQueZero(mc)) {
				todosComponentesReprovadosOuFaltaMediaZero = false;
			}
		}
		return todosComponentesReprovadosOuFaltaMediaZero;
	}

	private boolean isAprovadoOuDispensado(MatriculaComponente mc) {
		return mc.isAprovado() || SituacaoMatricula.APROVEITADO_DISPENSADO.equals(mc.getSituacaoMatricula());
	}

	private boolean isReprovadoMediaMaiorQueZero(MatriculaComponente mc) {
		return SituacaoMatricula.REPROVADO.equals(mc.getSituacaoMatricula()) && mc.getMediaFinal() > 0;
	}

	private boolean isAtividadeIndividual(MatriculaComponente mc) {
		return mc.getComponente().getFormaParticipacao() != null && !mc.getComponente().isPermiteCriarTurma();
	}			
	
		
	/**
	 * Confirma��o da Solicita��o
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String confirmarSolicitacao() throws ArqException, NegocioException{

		if (obj.getDiscente().isStricto()){
			ValidatorUtil.validateRequired(obj.getInicioTrancamento(), "In�cio do Trancamento", erros);
			
			ValidatorUtil.validateFuture(obj.getInicioTrancamento(), "In�cio do Trancamento", erros);
			
			ValidatorUtil.validaInt(obj.getNumeroMeses(), "N�mero de meses", erros);
		}
		
		confirmaSenha();
		
		if (hasErrors())
			return null;		
		
		obj.setDiscente(getGenericDAO().refresh(getDiscenteUsuario().getDiscente()));
		obj.setSituacao(StatusSolicitacaoTrancamentoPrograma.SOLICITADO);
		
		/* Cadastra a solicita��o */
		cadastrar();
		
		return  forward(LISTA_TRANCAMENTOS); 
	}
	
	
	/**
	 * Efetua o tancamento de programa a posteriori.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String confirmarSolicitacaoPosteriori() throws ArqException, NegocioException {
		
		confirmaSenha();
		
		if (hasErrors())
			return null;		
		
		obj.setDiscente(getGenericDAO().refresh(getDiscenteUsuario().getDiscente()));
				
		if (!checkOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId()))
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");		
									
		try {
			/* Cria Movimenta��o de Trancamento do Aluno Referente a Solicita��o */		
			MovimentacaoAluno movimentacao = new MovimentacaoAluno();
			movimentacao.setDiscente(discente);
			movimentacao.setAnoOcorrencia(obj.getAno());
			movimentacao.setPeriodoOcorrencia(obj.getPeriodo());
			movimentacao.setTrancamentoProgramaPosteriori(true);
			
			TipoMovimentacaoAluno tipo = getGenericDAO().findByPrimaryKey(TipoMovimentacaoAluno.TRANCAMENTO, TipoMovimentacaoAluno.class, "id", "descricao");			
			movimentacao.setTipoMovimentacaoAluno(tipo);
			
			movimentacao.setAnoReferencia(obj.getAno());
			movimentacao.setPeriodoReferencia(obj.getPeriodo());
			if (obj.getDiscente().isStricto()){
				movimentacao.setInicioAfastamento(obj.getInicioTrancamento());
				movimentacao.setValorMovimentacao(obj.getNumeroMeses());
			}
			movimentacao.setObservacao(observacao);
			
			MovimentoMovimentacaoAluno mov = new MovimentoMovimentacaoAluno();
			mov.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
			mov.setObjMovimentado(movimentacao);
			mov.setContext( WebApplicationContextUtils.getWebApplicationContext(getCurrentSession().getServletContext()) );
			
			/* Executa o Processador */ 
			execute(mov, getCurrentRequest());
			
			/* Confirma o Trancamento na Solicita��o */ 
			obj.setSituacao(StatusSolicitacaoTrancamentoPrograma.TRANCADO);
			cadastrar();
			
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		/* Cadastra a solicita��o */
		cadastrar();
		addMessage("Trancamento realizado com sucesso para o aluno " +
				obj.getDiscente().getMatriculaNome(), TipoMensagemUFRN.INFORMATION);
		
		return  forward(LISTA_TRANCAMENTOS);
	}
		
	/**
	 * Exibe o Comprovante de Solicita��o de Trancamento de Programa.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/solicitacao.jsp</li>
	 *   <li>/sigaa.war/ensino/trancamento_programa/lista_trancamentos.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String view() throws DAOException, NegocioException, ArqException{
		populateObj();
		obj.setDiscente(getGenericDAO().refresh(obj.getDiscente()));
		CalendarioAcademico cal = 
			(getCalendarioVigente().getAno() == obj.getAno() && getCalendarioVigente().getPeriodo() == obj.getPeriodo()) ? 
			getCalendarioVigente() :
			CalendarioAcademicoHelper.getCalendario(
				obj.getAno(), obj.getPeriodo(),  new Unidade( Unidade.UNIDADE_DIREITO_GLOBAL ), 
				obj.getDiscente().getNivel(), null, null, obj.getDiscente().getCurso());
		/* Verificando se o prazo de solicita��o est� ultrapassado, n�o possibilitando o aluno assinar a solicita��o.s*/
		if (cal.getInicioTrancamentoPrograma() != null && cal.getFimTrancamentoPrograma() != null 
			&& !CalendarUtils.isDentroPeriodo(cal.getInicioTrancamentoPrograma(), cal.getFimTrancamentoPrograma())){
			obj.setSituacao(StatusSolicitacaoTrancamentoPrograma.INDEFERIDO);
		}
		if (comprovante == null && !verificando) {
			comprovante = geraEmissao(TipoDocumentoAutenticado.DECLARACAO_COM_IDENTIFICADOR, discente.getMatricula().toString(), 
					gerarSemente(), String.valueOf(obj.getId()), SubTipoDocumentoAutenticado.SOLICITACAO_TRANCAMENTO_PROGRAMA ,false);
		}	
		
		codigoSeguranca = comprovante.getCodigoSeguranca();
		
		if (comprovante != null && verificando) {
			try {
				getCurrentSession().setAttribute("trancamentoPrograma", this);
				getCurrentSession().setAttribute(AutenticacaoUtil.TOKEN_REDIRECT, true);
				redirect("/ensino/trancamento_programa/comprovante_solicitacao.jsf");
			} catch (Exception e) {
				notifyError(e);
				e.printStackTrace();
			}
			return null;
		}
		
		return forward(COMPROVANTE_SOLICITACAO);		
	}
	
	/**
	 * Cadastra a Solicita��o de Trancamento.
	 * <br/><br/>
	 * O M�todo n�o � chamado por JSPs.
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,	NegocioException {			
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);		
		try {			
			Comando comando;
			if (obj.getId() == 0){
				comando = ArqListaComando.CADASTRAR;
			} else {
				comando = ArqListaComando.ALTERAR;
			}
			// Seta a opera��o como cadastrar
			mov.setCodMovimento(comando);
			// Tenta executar a opera��o
			execute(mov, getCurrentRequest());				
			if (!obj.isPosteriori())
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);							
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}						
		return exibirSolicitacoes(); 
	}
	
	/**
	 * Redireciona para selecionar um discente.<br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/graduacao/menus/programa.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarSubmeterTrancamento() throws SegurancaException{
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_CURSO_STRICTO, 
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG);
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");				
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.SOLICITACAO_TRANCAMENTO_PROGRAMA);
		return buscaDiscenteMBean.popular();
	}	
	
	/**
	 * Exibe as Solicita��es do Discente Logado.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/lista_trancamentos.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String exibirMinhasSolicitacoes() throws DAOException, ArqException{
		if( getDiscenteUsuario() == null ) {
			addMensagemErro("Est� opera��o s� pode ser realizada por discentes.");
			return null;
		}
		
		discente = getDiscenteUsuario();
		
		return exibirSolicitacoes();
	}
		
	/**
	 * Exibe as Solicita��es de Trancamento
	 * M�todo n�o chamado por JSP.
	 * @return
	 * @throws DAOException 
	 */
	public String exibirSolicitacoes() throws DAOException, ArqException{
		SolicitacaoTrancamentoProgramaDao dao = getDAO(SolicitacaoTrancamentoProgramaDao.class);
		try {
			if (discente == null && obj.getDiscente() != null)
				discente = obj.getDiscente();
			
			solicitacoes = dao.findSolicitacoesByDiscente(discente); 
			MovimentacaoAlunoMBean mAlunoMbean = getMBean("movimentacaoAluno");
			
			mAlunoMbean.getObj().setDiscente(discente);
			mAlunoMbean.selecionaDiscente();
			
			if (solicitacoes.isEmpty()){
				addMensagemErro("Nenhuma Solicita��o de Trancamento de Programa cadastrada para o Discente informado.");
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		return forward(LISTA_TRANCAMENTOS);
	}
	
	/**
	 * Retorna o MovimentacaoAlunoMBean.
	 * @return
	 */
	private MovimentacaoAlunoMBean getMovimentacaoAlunoMBean(){
		return getMBean("movimentacaoAluno");
	}
	
	/**
	 * Submete Trancamento de Programa.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/form_submeter.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String submeterTrancamento() throws SegurancaException, ArqException, NegocioException{	
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_CURSO_STRICTO, 
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG);
		
		if (obj.getDiscente().isStricto()){
			ValidatorUtil.validateRequired(obj.getInicioTrancamento(), "In�cio do Trancamento", erros);
			
			ValidatorUtil.validateFuture(obj.getInicioTrancamento(), "In�cio do Trancamento", erros);
			
			ValidatorUtil.validaInt(obj.getNumeroMeses(), "N�mero de meses", erros);
		}						
		
		if (observacao.trim().isEmpty())
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observa��o");
		
		if (!confirmaSenha() || hasErrors())
			return null;
		
		if (!checkOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId()))
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");		
									
		try {
			/* Cria Movimenta��o de Trancamento do Aluno Referente a Solicita��o */		
			MovimentacaoAluno movimentacao = new MovimentacaoAluno();
			movimentacao.setDiscente(discente);
			movimentacao.setAnoOcorrencia(obj.getAno());
			movimentacao.setPeriodoOcorrencia(obj.getPeriodo());
			
			TipoMovimentacaoAluno tipo = getGenericDAO().findByPrimaryKey(TipoMovimentacaoAluno.TRANCAMENTO, TipoMovimentacaoAluno.class, "id", "descricao");			
			movimentacao.setTipoMovimentacaoAluno(tipo);
			
			movimentacao.setAnoReferencia(obj.getAno());
			movimentacao.setPeriodoReferencia(obj.getPeriodo());
			if (obj.getDiscente().isStricto()){
				movimentacao.setInicioAfastamento(obj.getInicioTrancamento());
				movimentacao.setValorMovimentacao(obj.getNumeroMeses());
			}
			movimentacao.setObservacao(observacao);
			
			MovimentoMovimentacaoAluno mov = new MovimentoMovimentacaoAluno();
			mov.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
			mov.setObjMovimentado(movimentacao);
			mov.setContext( WebApplicationContextUtils.getWebApplicationContext(getCurrentSession().getServletContext()) );
			
			/* Executa o Processador */ 
			execute(mov, getCurrentRequest());
			
			/* Confirma o Trancamento na Solicita��o */ 
			obj.setSituacao(StatusSolicitacaoTrancamentoPrograma.TRANCADO);
			cadastrar();
			
			addMessage("Trancamento realizado com sucesso para o aluno " +
					obj.getDiscente().getMatriculaNome(), TipoMensagemUFRN.INFORMATION);
			
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		return exibirSolicitacoes();
	}
	
	/**
	 * Cancela uma solicita��o de trancamento ativa.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/lista_trancamentos.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String cancelarSolicitacao() throws SegurancaException, ArqException, NegocioException{
		populateObj();
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			discente = dao.findDiscenteAdapterById(obj.getDiscente().getId());			
			obj.setDiscente(discente.getDiscente());			
		} finally {
			if (dao != null)
				dao.close();
		}
		prepareMovimento(ArqListaComando.ALTERAR);
		obj.setSituacao(StatusSolicitacaoTrancamentoPrograma.CANCELADO);
		return cadastrar();
	}
	
	/**
	 * Indeferir a solicita��o de trancamento ativa.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/lista_trancamentos.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String indeferirSolicitacao() throws SegurancaException, ArqException, NegocioException{
		populateObj();
		if (obj.getSituacao() != StatusSolicitacaoTrancamentoPrograma.SOLICITADO) {
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");			
		}
		
		prepareMovimento(ArqListaComando.ALTERAR);
		return forward(FORM_OBSERVACAO);
	}	
	
	/**
	 * Submeter Indeferimento de trancamento ativa.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/form_observacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String submeterIndeferimento() throws SegurancaException, ArqException, NegocioException{
		if (isEmpty(obj.getObservacao())){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Motivo");
			return null;
		}
		obj.setSituacao(StatusSolicitacaoTrancamentoPrograma.INDEFERIDO);
		return cadastrar();
	}	
	
	/**
	 * Inicia o processo de confirma��o de trancamento de programa.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/lista_trancamentos.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String preSubmeter() throws ArqException{
		if (isEmpty(obj)){
			addMensagemErro("Nenhuma Solicita��o de Trancamento de Programa informada.");
			return null;
		}
		
		if (obj.isCancelado()){
			addMensagemErro("Solicita��o de Trancamento de Programa Cancelada.");
			return null;
		}
		
		populateObj();
		
		if (obj.getSituacao() != StatusSolicitacaoTrancamentoPrograma.SOLICITADO) {
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		}
		
		MovimentacaoAlunoMBean movMBean = getMovimentacaoAlunoMBean();
		movMBean.setOperacao(OperacaoDiscente.AFASTAMENTO_ALUNO);
		movMBean.setDiscente(discente);
		movMBean.selecionaDiscente();
		
		if (hasErrors()) {
			obj.setSituacao(0);
			return null;
		}
		
		prepareMovimento(ArqListaComando.ALTERAR);
		
		return forward(FORM_SUBMETER);
	}

	/**
	 * Retorna os motivos de trancamento de programa
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getAllMotivosCombo() throws DAOException {
		return toSelectItems(getGenericDAO().findAllAtivos(MotivoTrancamentoPrograma.class,"descricao"), "id", "descricao");
	}	
	
	/** 
	 * Chamado a partir do BuscaDiscenteMBean
	 * M�todo n�o invocado por JSP.
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	@Override
	public String selecionaDiscente() throws ArqException {		
		prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
		setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		
		return exibirSolicitacoes();
	}

	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = discente;		
	}
	
	public Collection<SolicitacaoTrancamentoPrograma> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(
			Collection<SolicitacaoTrancamentoPrograma> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}	

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public boolean isEstaCiente() {
		return estaCiente;
	}

	public void setEstaCiente(boolean estaCiente) {
		this.estaCiente = estaCiente;
	}
	
	/**
	 * Retorna a data atual no formato dd de m�s de yyyy.
	 */
	public String getDataAtual() {
		Locale locale = new Locale("pt", "BR");
		DateFormat d = new SimpleDateFormat("dd/MM/yyy", locale);
		return d.format(new Date());
	}

	/**
	 * Retorna a data atual no formato dd de m�s de yyyy.
	 */
	public String getDataSolicitacao() {
		Locale locale = new Locale("pt", "BR");
		DateFormat d = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", locale);
		return d.format(obj.getDataCadastro());
	}

	/**
	 * Retorna a data limite do trancamento do programa.
	 * @throws DAOException 
	 */
	public String getDataLimiteTrancamento() throws DAOException {
		
		CalendarioAcademico calAcademico = getCelendarioVigenteSolicitacao();
		
		Date prazo = null;
		if (!obj.isPosteriori())
			prazo = calAcademico.getFimTrancamentoPrograma();
		else
			prazo = calAcademico.getFimTrancamentoProgramaPosteriori();

		Locale locale = new Locale("pt", "BR");
		DateFormat d = new SimpleDateFormat("dd/MM/yyyy", locale);
		
		if (prazo != null)
			return d.format(prazo);
		
		return null;
	}
	
	/**
	 * Verifica se a data de visualiza��o � posterior a data limite de tracamento
	 * @throws DAOException 
	 */
	public boolean isPosteriorLimiteTrancamento() throws DAOException {
		
		CalendarioAcademico calAcademico = getCelendarioVigenteSolicitacao();
		
		Date prazo = null;
		if (!obj.isPosteriori())
			prazo = calAcademico.getFimTrancamentoPrograma();
		else
			prazo = calAcademico.getFimTrancamentoProgramaPosteriori();

		Date dataAtual = new Date();
		if (prazo != null && dataAtual.getTime() >= prazo.getTime())
			return true;
		return false;
	}

	/**
	 * Retorna o calend�rio vigente na data de solicita��o de trancamento
	 * @throws DAOException 
	 */
	private CalendarioAcademico getCelendarioVigenteSolicitacao()
			throws DAOException {
		
		CalendarioAcademico calAcademico = CalendarioAcademicoHelper.getCalendario(obj.getAno(),obj.getPeriodo(), discente.getCurso().getUnidade(),
				 discente.getNivel(), null, null, null);
		return calAcademico;
	}
	
	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}

	
	
	/**
	 * Gerar semente para valida��o do documento
	 * 
	 * @return
	 */
	private String gerarSemente() throws DAOException {
		StringBuilder builder = new StringBuilder();

		Discente discente = obj.getDiscente();
		
		// Utilizar curso, munic�pio do curso e status do discente
		builder.append( discente.getCurso().getId());
		
		if (!discente.isLato() && !discente.isResidencia())
			builder.append( discente.getCurso().getMunicipio().getId());
		
		if ( discente.isGraduacao() ) {
			DiscenteDao dao = getDAO(DiscenteDao.class);
			DiscenteGraduacao dg =  (DiscenteGraduacao) dao.findByPK(discente.getDiscente().getId());
			builder.append( dg.getMatrizCurricular().getDescricao());
		}
		
		builder.append( StatusDiscente.getStatusComVinculo().contains(discente.getStatus()) );

		return builder.toString();
	}

	/**
	 * Validada se o comprovante recebido � de um documento v�lido ou n�o.
	 * @param comprovante do documento passado na valida��o.
	 * * <br/>
	 * M�todo n�o invocado por JSP's. 
	 */
	@Override
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovante) {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			Collection<Discente> discentes = dao.findByExactField(Discente.class, "matricula", new Long(comprovante.getIdentificador()));
			for ( Discente discente : discentes ) {
				obj.setDiscente(discente);
				String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovante, gerarSemente());
				if (codigoVerificacao.equals(comprovante.getCodigoSeguranca()))
					return true;
			}
		} catch (Exception e ) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return false;
		
	}

	/**
	 * Respons�vel por exibir o documento da solicita��o ap�s a valida��o.
	 * <br/>
	 * M�todo n�o invocado por JSP's. 
	 */
	@Override
	public void exibir(EmissaoDocumentoAutenticado comprovante,
			HttpServletRequest req, HttpServletResponse res) {
		this.comprovante = comprovante;
		
		verificando = true;
		DiscenteDao dao = getDAO(DiscenteDao.class);

		try {
			Integer id = Integer.valueOf(comprovante.getDadosAuxiliares());
			if (id != null)
				obj = getGenericDAO().findByPrimaryKey(id, SolicitacaoTrancamentoPrograma.class);
			
			discente = 	dao.findByMatricula(Long.parseLong(comprovante.getIdentificador()));
			if (discente.isGraduacao())
				discente = dao.findDiscenteAdapterById(discente.getId());
			
			obj.setDiscente(discente.getDiscente());
					
			if (validaDigest(comprovante)) {
				view();
			}
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}

		
	}
	
}

