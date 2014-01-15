/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
	/** Caminho do Formulário de Solicitação de Trancamento */
	private static final String FORM_SOLICITACAO = "/ensino/trancamento_programa/solicitacao.jsp";
	/** Comprovante de Solicitação de Trancamento */
	private static final String COMPROVANTE_SOLICITACAO = "/ensino/trancamento_programa/comprovante_solicitacao.jsp";
	/** Lista de Trancamento */
	private static final String LISTA_TRANCAMENTOS = "/ensino/trancamento_programa/lista_trancamentos.jsp";
	/** Formulário de Submissão da Solicitação de Trancamento de Programa */
	private static final String FORM_SUBMETER = "/ensino/trancamento_programa/form_submeter.jsp";	
	/** Formulário de Indeferimento da Solicitação de Trancamento de Programa */
	private static final String FORM_OBSERVACAO = "/ensino/trancamento_programa/form_observacao.jsp";
	/** Tela explicativa sobre o trancamento de programa*/
	private static final String INTRUCOES_TRANCAMENTO = "/ensino/trancamento_programa/instrucoes.jsp";
	
	/** Lista de Solicitações de Trancamento */
	private Collection<SolicitacaoTrancamentoPrograma> solicitacoes;
	
	/** Discente selecionado. */
	private DiscenteAdapter discente;
	
	/** Observação que sairá no histórico do aluno */
	private String observacao;
	/** Obriga o Discente marcar para prosseguir */
	private boolean estaCiente;	
	/** Código de segurança para validação do relatório */
	private String codigoSeguranca;
	
	/** Atributo que define o estado da geração da solicitação de trancamento. */
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
	 * Redireciona usuário para o formulário de trancamento.
	 * * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/instrucoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarSolicitacao() throws ArqException {
		
		
		if (!estaCiente) {
			addMensagemErro("É necessário confirmar que está ciente das condições para prosseguir para a tela de confirmação.");
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
				addMensagemWarning("Foram identificados " + qtdEmprestimosAtivoVinculo + " empréstimos ativos de materiais bibliográficos. Eles deverão ser devolvidos à biblioteca antes da efetivação do trancamento.");	
			}
			
		}
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		return forward(FORM_SOLICITACAO);
	}
	
	/**
	 * Inicia o caso de uso de solicitação de trancamento. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
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
			addMensagemErro("Discentes do tipo Especial não estão autorizados a efetuar Trancamento do Programa através dessa operação.");
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
	 * Inicia o caso de uso de solicitação de trancamento a posteriori. 
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarTrancamentoPosteriori() throws ArqException {
		
		discente = getUsuarioLogado().getDiscente();
		if (discente.getTipo() == Discente.ESPECIAL) {
			addMensagemErro("Discentes do tipo Especial não estão autorizados a efetuar Trancamento do Programa através dessa operação.");
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
	 * Validação usada tanto no tracamento de programa regular como no trancamento a posteriori que valida
	 * as regras de negócio comum em ambos casos de uso. 
	 */
	private void validateTrancamento() throws DAOException {
		
		if( getUsuarioLogado() == null ){
			addMensagemErro("Problemas na identificação do usuário. Será necessário logar-se novamente e reiniciar a operação.");
			return;
		}
		
		if( !getUsuarioLogado().getDiscente().isGraduacao() /*&& !getUsuarioLogado().getDiscente().isStricto()*/){
			addMensagemErro("Discentes do nível " + getUsuarioLogado().getDiscente().getNivelDesc() + " não estão autorizados a efetuar Trancamento do Programa através dessa operação.");
			return;
		}
		
		//Validar se o discente está ativo
		if (!StatusDiscente.getStatusComVinculo().contains(getUsuarioLogado().getDiscente().getStatus())) {
			addMensagemErro("Somente discentes com vínculo ativo podem realizar o trancamento do programa.");
			return;
		}
		
		if (!obj.isPosteriori()) {
			//Valida se está dentro do período para trancamento de programa.
			if (getCalendarioVigente().getInicioTrancamentoPrograma() == null 
					|| getCalendarioVigente().getFimTrancamentoPrograma() == null) {
				addMensagemErro("O período de trancamento de programa não foi definido.");
				return ;
			} else if ((getCalendarioVigente().getInicioTrancamentoPrograma() != null 
					&& getCalendarioVigente().getFimTrancamentoPrograma() != null) 
					&& !getCalendarioVigente().isPeriodoTrancamentoPrograma()  ) {
				Formatador fmt = Formatador.getInstance();
				addMensagemErro(" O período oficial para trancamento de programa estende-se de " +
				fmt.formatarData(getCalendarioVigente().getInicioTrancamentoPrograma()) + " a " + fmt.formatarData(getCalendarioVigente().getFimTrancamentoPrograma()) + ".");
				return;
			}
		} 
		else {	
			
			//Valida se está dentro do período para trancamento de programa a posteriori.
			if (getCalendarioVigente().getInicioTrancamentoProgramaPosteriori() == null 
					|| getCalendarioVigente().getFimTrancamentoProgramaPosteriori() == null) {
				addMensagemErro("O período de trancamento de programa a posteriori não foi definido.");
				return ;
			}
			else if ((getCalendarioVigente().getInicioTrancamentoProgramaPosteriori()!=null && getCalendarioVigente().getFimTrancamentoProgramaPosteriori()!=null) && !getCalendarioVigente().isPeriodoTrancamentoProgramaPosteriori()  ) {
			Formatador fmt = Formatador.getInstance();
			addMensagemErro(" O período oficial para trancamento de programa a posteriori estende-se de " +
			fmt.formatarData(getCalendarioVigente().getInicioTrancamentoProgramaPosteriori()) + " a " + fmt.formatarData(getCalendarioVigente().getFimTrancamentoProgramaPosteriori()) + ".");
			return;
			}
		}
		
		SolicitacaoTrancamentoProgramaDao dao = getDAO(SolicitacaoTrancamentoProgramaDao.class);
		MovimentacaoAlunoDao afasDao = getDAO(MovimentacaoAlunoDao.class);
		
		try {
			
			//Verifica se a quantidade de solicitações foi excedida 
			ParametrosGestoraAcademica params = ParametrosGestoraAcademicaHelper.getParametros(getDiscenteUsuario());
			int quantAtual = afasDao.findTrancamentosByDiscente(discente.getId(), discente.getGestoraAcademica().getId(), discente.getNivel());
			int maxTrancamentos = params.getMaxTrancamentos().intValue(); 
			if (quantAtual >= maxTrancamentos){
				addMensagemErro("O limite máximo de trancamentos de " +  maxTrancamentos +" períodos letivos regulares, consecutivos ou não, foi atingido. Não será possível fazer uma nova solicitação de trancamento de programa.");
				return;				
			}
			
			/* Retorna o Histórico de Solicitações */
			solicitacoes = dao.findSolicitacoesByDiscente(getUsuarioLogado().getDiscente());
			
			/*Verifica se o discente logado possui alguma solicitação de trancamento no período vigente */
			if (dao.quantSolicitacoesTrancamentoByDiscente(getDiscenteUsuario(), obj.getAno(), obj.getPeriodo()) > 0){
				addMensagemErro("Já existe uma solicitação de trancamento do programa no período vigente.");
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
	 * Valida se o discente possuem os requisitos mínimos para o solicitação de trancamento a posteriori.
	 * Para fazer ese trancamento é necessário não ter aproveitado carga horária, não ter sido reprovado por média
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
			addMensagemErro("Não foram encontradas matrículas em componentes para o período " + cAnterior.getAnoPeriodo());
			return;
		} else if (estaoTodosComponentesReprovadosFaltaOuMediaZero(matriculas)) {			
			addMensagemErro("Não será possível solicitar o trancamento a posteriori pois não foram identificadas matrículas em atividades, ou reprovações que não tenham sido por falta, com média maior que 0 (zero), em ao menos um componente curricular no período  " 
					+ getCalendarioVigente().getAnoAnterior() + "."+ getCalendarioVigente().getPeriodoAnterior() );
		}
		
	}

	private boolean estaoTodosComponentesReprovadosFaltaOuMediaZero(Collection<MatriculaComponente> matriculas) {
		boolean todosComponentesReprovadosOuFaltaMediaZero = true;
		for (MatriculaComponente mc: matriculas) {
			if (mc.getComponente().getBlocoSubUnidade() != null)
				continue;
			if (isAprovadoOuDispensado(mc)) {
				addMensagemErro("Não será possível solicitar o trancamento de programa pois foi identificada integralização de carga horária para o semestre.");
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
	 * Confirmação da Solicitação
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/solicitacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String confirmarSolicitacao() throws ArqException, NegocioException{

		if (obj.getDiscente().isStricto()){
			ValidatorUtil.validateRequired(obj.getInicioTrancamento(), "Início do Trancamento", erros);
			
			ValidatorUtil.validateFuture(obj.getInicioTrancamento(), "Início do Trancamento", erros);
			
			ValidatorUtil.validaInt(obj.getNumeroMeses(), "Número de meses", erros);
		}
		
		confirmaSenha();
		
		if (hasErrors())
			return null;		
		
		obj.setDiscente(getGenericDAO().refresh(getDiscenteUsuario().getDiscente()));
		obj.setSituacao(StatusSolicitacaoTrancamentoPrograma.SOLICITADO);
		
		/* Cadastra a solicitação */
		cadastrar();
		
		return  forward(LISTA_TRANCAMENTOS); 
	}
	
	
	/**
	 * Efetua o tancamento de programa a posteriori.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
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
			/* Cria Movimentação de Trancamento do Aluno Referente a Solicitação */		
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
			
			/* Confirma o Trancamento na Solicitação */ 
			obj.setSituacao(StatusSolicitacaoTrancamentoPrograma.TRANCADO);
			cadastrar();
			
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		}
		
		/* Cadastra a solicitação */
		cadastrar();
		addMessage("Trancamento realizado com sucesso para o aluno " +
				obj.getDiscente().getMatriculaNome(), TipoMensagemUFRN.INFORMATION);
		
		return  forward(LISTA_TRANCAMENTOS);
	}
		
	/**
	 * Exibe o Comprovante de Solicitação de Trancamento de Programa.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
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
		/* Verificando se o prazo de solicitação está ultrapassado, não possibilitando o aluno assinar a solicitação.s*/
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
	 * Cadastra a Solicitação de Trancamento.
	 * <br/><br/>
	 * O Método não é chamado por JSPs.
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
			// Seta a operação como cadastrar
			mov.setCodMovimento(comando);
			// Tenta executar a operação
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
	 * Método chamado pela seguinte JSP:
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
	 * Exibe as Solicitações do Discente Logado.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/lista_trancamentos.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String exibirMinhasSolicitacoes() throws DAOException, ArqException{
		if( getDiscenteUsuario() == null ) {
			addMensagemErro("Está operação só pode ser realizada por discentes.");
			return null;
		}
		
		discente = getDiscenteUsuario();
		
		return exibirSolicitacoes();
	}
		
	/**
	 * Exibe as Solicitações de Trancamento
	 * Método não chamado por JSP.
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
				addMensagemErro("Nenhuma Solicitação de Trancamento de Programa cadastrada para o Discente informado.");
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
	 * Método chamado pela seguinte JSP:
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
			ValidatorUtil.validateRequired(obj.getInicioTrancamento(), "Início do Trancamento", erros);
			
			ValidatorUtil.validateFuture(obj.getInicioTrancamento(), "Início do Trancamento", erros);
			
			ValidatorUtil.validaInt(obj.getNumeroMeses(), "Número de meses", erros);
		}						
		
		if (observacao.trim().isEmpty())
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observação");
		
		if (!confirmaSenha() || hasErrors())
			return null;
		
		if (!checkOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId()))
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");		
									
		try {
			/* Cria Movimentação de Trancamento do Aluno Referente a Solicitação */		
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
			
			/* Confirma o Trancamento na Solicitação */ 
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
	 * Cancela uma solicitação de trancamento ativa.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
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
	 * Indeferir a solicitação de trancamento ativa.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
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
	 * Método chamado pela seguinte JSP:
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
	 * Inicia o processo de confirmação de trancamento de programa.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/trancamento_programa/lista_trancamentos.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String preSubmeter() throws ArqException{
		if (isEmpty(obj)){
			addMensagemErro("Nenhuma Solicitação de Trancamento de Programa informada.");
			return null;
		}
		
		if (obj.isCancelado()){
			addMensagemErro("Solicitação de Trancamento de Programa Cancelada.");
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
	 * Método chamado pela seguinte JSP:
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
	 * Método não invocado por JSP.
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
	 * Retorna a data atual no formato dd de mês de yyyy.
	 */
	public String getDataAtual() {
		Locale locale = new Locale("pt", "BR");
		DateFormat d = new SimpleDateFormat("dd/MM/yyy", locale);
		return d.format(new Date());
	}

	/**
	 * Retorna a data atual no formato dd de mês de yyyy.
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
	 * Verifica se a data de visualização é posterior a data limite de tracamento
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
	 * Retorna o calendário vigente na data de solicitação de trancamento
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
	 * Gerar semente para validação do documento
	 * 
	 * @return
	 */
	private String gerarSemente() throws DAOException {
		StringBuilder builder = new StringBuilder();

		Discente discente = obj.getDiscente();
		
		// Utilizar curso, município do curso e status do discente
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
	 * Validada se o comprovante recebido é de um documento válido ou não.
	 * @param comprovante do documento passado na validação.
	 * * <br/>
	 * Método não invocado por JSP's. 
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
	 * Responsável por exibir o documento da solicitação após a validação.
	 * <br/>
	 * Método não invocado por JSP's. 
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

