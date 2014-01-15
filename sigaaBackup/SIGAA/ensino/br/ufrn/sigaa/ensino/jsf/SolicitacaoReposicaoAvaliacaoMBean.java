/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 13/05/2010
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoReposicaoAvaliacaoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.TamanhoArquivo;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoReposicaoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.StatusReposicaoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoReposicaoProva;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;

/**
 * MBean Respons�vel por gerenciar as opera��es no caso de uso de Solicita��o de Reposi��o de Prova.
 * @author Arlindo
 *
 */
@Component("solicitacaoReposicaoProva") @Scope("request")
public class SolicitacaoReposicaoAvaliacaoMBean extends SigaaAbstractController<SolicitacaoReposicaoAvaliacao> {

	/** Indica que s�o selecionadas todas as Solicita��es. */
	public static final int TODAS_SOLICITACOES = 0;
	/** Indica que s�o selecionadas apenas solicita��es com parecer do docente. */
	public static final int SUBMETIDO_HOMOLOGACAO = -1;
	
	/** Preenchido se o aluno anexar um arquivo. */
	private UploadedFile arquivo;
	
	/** Tamanho m�ximo do arquivo de anexo */
	private int tamanhoMaxArquivo;
	
	/** Turma Selecionada */
	private Turma turma;
	
	/** Lista de Avalia��es que o Aluno pode solicitar reposi��o */
	private Collection<DataAvaliacao> listaAvaliacoes;
	
	/** Lista de Solicita��es de Provas */
	private List<SolicitacaoReposicaoAvaliacao> listaSolicitacoes;
	
	/** Novo status definido na Homologa��o */
	private StatusReposicaoAvaliacao novoStatus;
	
	/** Data da Prova definida na Homologa��o */	
	private Date dataProva;
	
	/** Observa��o definida na Homologa��o */
	private String observacao;
	
	/** Comando que ser� executado */
	private Comando comando;
	
	/** Hora da Prova */
	private Date horaProva;
	
	/** Lista de Solicita��es Confirmados para Aprecia��o/Homologa��o */
	private List<SolicitacaoReposicaoAvaliacao> listaConfirmados;
	
	/** Indica se n�o foi dado parecer em alguma das solicita��es */
	private boolean parecerPendente;
	
	/** Lista de turmas abertas com avalia��es */
	private List<Map<String, Object>> listaTurmas = new ArrayList<Map<String,Object>>();
	
	/** Ano para o filtro de turmas */
	private Integer ano;
	
	/** Per�odo para o filtro de turmas */
	private Integer periodo;
	
	/** Dentro do prazo de 3 dias �teis */
	private boolean dentroPrazo;
	
	/** Foi dado o parecer do docente. */
	private boolean parecerDocente;
	
	/** Status das solicita��es de reposi��o */
	private Integer status;
	
	/** Status das solicita��es para o filtro de turmas - Trata o status submetido a chefia */
	private Integer statusFiltro;
	
	/** Se entrou pelo caso de uso de analisar solicita��es ou apreciar solicita��es. */
	private boolean analisarSolicitacoes;
		
	/** Construtor Padr�o da Classe */
	public SolicitacaoReposicaoAvaliacaoMBean() {
		initObj();
	}

	/** Inicializa os Objetos */
	private void initObj() {
		if (obj == null)
			obj = new SolicitacaoReposicaoAvaliacao();
		
		if (isEmpty(obj.getDiscente()))
			obj.setDiscente(new DiscenteGraduacao());
		
		if (isEmpty(obj.getDataAvaliacao()))
			obj.setDataAvaliacao(new DataAvaliacao());	
		
		novoStatus = new StatusReposicaoAvaliacao();
	}
	
	/**
	 * Inicia a Solicita��o de Reposi��o de Prova.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciar() throws HibernateException, DAOException{
		
		SolicitacaoReposicaoAvaliacaoDao dao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class);
		try {
			listaTurmas = dao.findTurmasAvaliacoes(getDiscenteUsuario().getId(), 
					getCalendarioVigente().getAno(), getCalendarioVigente().getPeriodo());
			
			if (ValidatorUtil.isEmpty(listaTurmas)){
				addMensagemErro("N�o existe nenhuma Avalia��o dispon�vel para Reposi��o, ou est� fora do prazo para solicitar reposi��o!");
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getFormTurmas());
	}
	
	/**
	 * Inicia a Solicita��o de Reposi��o de Prova.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/lista_turma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */	
	public String iniciarSolicitacao() throws ArqException{
		if (getCalendarioVigente().isPeriodoFerias()){
			addMensagemErro("N�o pode solicitar Reposi��o de Avalia��o no Per�odo de F�rias");
			return null;
		}
		
		int idAvaliacao = getParameterInt("idAvaliacao", 0);
		if (idAvaliacao == 0)
			return null;
				
		TurmaDao dao = getDAO(TurmaDao.class);
		SolicitacaoReposicaoAvaliacaoDao reposicaoDao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class);
		try{
			DataAvaliacao dataAvaliacao = dao.findByPrimaryKey(idAvaliacao, DataAvaliacao.class, 
					"id","data", "hora", "descricao", "turma.id");		
			
			// verifica se o discente j� possui alguma solicita��o ativa da avalia��o selecionada.
			if (!isEmpty(reposicaoDao.findByDiscente(getDiscenteUsuario(), dataAvaliacao, true))){
				addMensagemErro("J� existe uma solicita��o ativa para a avalia��o selecionada.");
				return null;
			}
			
			obj.setDataAvaliacao(dataAvaliacao);
			
			turma = getGenericDAO().refresh(dataAvaliacao.getTurma());			
		} finally {
			if (reposicaoDao != null)
				reposicaoDao.close();			
			if (dao != null)
				dao.close();
		}
		
		comando = SigaaListaComando.CADASTRAR_REPOSICAO_PROVA;
		prepareMovimento(comando);
		// Atribui o tamanho m�ximo do arquivo a ser anexado pelo aluno. 
		tamanhoMaxArquivo = ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_PADRAO_ARQUIVO_ALUNO);
		// se n�o existir valor definido atribui 5MB
		if (tamanhoMaxArquivo <= 0)
			tamanhoMaxArquivo = 5; 
		
		if (turma.isAgrupadora() && dao != null)
			turma = dao.findSubturmaByTurmaDiscente(obj.getDataAvaliacao().getTurma(), getDiscenteUsuario());
		
		obj.setTurma(turma);		
		obj.setDiscente((DiscenteGraduacao) getDiscenteUsuario());
		
		return forward(getFormPage());		
	}
	
	/**
	 * Lista as Solicita��o de Reposi��o de Provas.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String listarSolicitacoes(){
		return forward(getListPage());
	}
	
	/**
	 * Realiza as valida��es antes de cadastrar. 
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws IOException
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String beforeCadastrar() throws IOException, NegocioException, SegurancaException, ArqException{				
		SolicitacaoReposicaoAvaliacaoDao reposicaoDao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class);
		try {// verifica se o discente j� possui alguma solicita��o ativa da avalia��o selecionada.
			if (!isEmpty(reposicaoDao.findByDiscente(getDiscenteUsuario(), obj.getDataAvaliacao(), true))){
				addMensagemErro("J� existe uma solicita��o ativa para a avalia��o informada.");
				return null;
			}
		} finally {
			if (reposicaoDao != null)
				reposicaoDao.close();
		}
		
		if (arquivo != null){
			// verifica se o tamanha do arquivo � maior que o permitido.
			if (arquivo.getSize() > tamanhoMaxArquivo * TamanhoArquivo.MEGA_BYTE)
				throw new NegocioException ("O arquivo deve ter tamanho menor ou igual a " + tamanhoMaxArquivo + " MB.");
		}	
		
		novoStatus = new StatusReposicaoAvaliacao( StatusReposicaoAvaliacao.CADASTRADA );
		
		erros = new ListaMensagens();
		erros.addAll(obj.validate().getMensagens());
		
		if (hasErrors())
			return null;		
		
		return cadastrar();
	}
	
	/**
	 * Realiza o Cadastro da Solicita��o de Reposi��o de Prova.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		try {
			MovimentoReposicaoProva mov = new MovimentoReposicaoProva();
			mov.setSolicitacaoReposicaoProva(obj);
			mov.setNovoStatus(novoStatus.getId());
			mov.setArquivo(arquivo);
			if (comando.equals(SigaaListaComando.HOMOLOGAR_REPOSICAO_PROVA) || 
					comando.equals(SigaaListaComando.PARECER_REPOSICAO_PROVA)){				
				mov.setObservacao(observacao);
				mov.setSolicitacoes(listaConfirmados);
			}
			mov.setCodMovimento(comando);
			execute(mov);			
			
			listaSolicitacoes = null;
			if (getUltimoComando().equals(SigaaListaComando.CADASTRAR_REPOSICAO_PROVA)){
				addMensagemInformation("Sua solicita��o foi cadastrada com sucesso. Foi enviado um email para o(s) Docente(s) da disciplina informada, " +
						"notificando sua solicita��o para que seja analisada. Ap�s an�lise, " +
						"voc� receber� um email informando do parecer final. ATEN��O! Esta Solicita��o n�o garante a Reposi��o da Avalia��o, " +
				"� necess�rio sua aprova��o.");				
			} else if (getUltimoComando().equals(SigaaListaComando.PARECER_REPOSICAO_PROVA)){
				addMensagemInformation("Parecer Registrado com sucesso. ATEN��O! Esta Solicita��o n�o garante a Reposi��o da Avalia��o, " +
				"� necess�rio sua aprova��o da chefia do departamento.");				
			} else {
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);				
			}
			
			if (isPortalDiscente())
				return redirectJSF(getListPage());
			
			return cancelar();
		} catch (NegocioException e) {	
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
		
	/**
	 * Visualiza a solicita��o de reposi��o de prova.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String view() throws DAOException{
		if (isEmpty(obj))
			return null;
		populateObj();
		return forward(getViewPage());
	}
	
	/**
	 * Redireciona para a P�gina de Registro de Parecer.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String analisarSolicitacao() throws ArqException{
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = calendario.getAno();
		periodo = calendario.getPeriodo();
		
		parecerDocente = false;
		dentroPrazo = false;

		analisarSolicitacoes = true;
		
		comando = SigaaListaComando.PARECER_REPOSICAO_PROVA;
		prepareMovimento(comando);		
		return forward(getSolicitacoesTurmas());
	}
	
	/**
	 * Redireciona para a P�gina de Registro de Parecer.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
 	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/solicitacoes_turmas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String registrarParecer() throws ArqException{
		
		if (isEmpty( getListaSolicitacoes() )){
			addMensagemWarning("Nenhuma Solicita��o de Reposi��o de Avalia��o Cadastrada!");
			return null;
		}		
		
		int idTurma = getParameterInt("idTurma", 0);
		int idAvaliacao = getParameterInt("idAvaliacao", 0);
		
		SolicitacaoReposicaoAvaliacaoDao dao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class);		
		listaSolicitacoes = dao.findAllByDocente(null, getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), idTurma, idAvaliacao, ano,periodo,status,false,false);
		
		comando = SigaaListaComando.PARECER_REPOSICAO_PROVA;
		prepareMovimento(comando);
		return forward(getFormParecer());
	}
	
	/**
	 * Confirma o Registro de Parecer.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String confirmarParecer() throws SegurancaException, ArqException, NegocioException{
		if (ValidatorUtil.isEmpty(novoStatus))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Situa��o");
		
		if (novoStatus.isDeferido())
			dataProvaInvalida(dataProva);
		
		if (novoStatus.isDeferido() && isEmpty(obj.getLocalProva()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Local");
		
		if (novoStatus.isIndeferido() && ValidatorUtil.isEmpty(observacao))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observa��o");

		listaConfirmados = new ArrayList<SolicitacaoReposicaoAvaliacao>(0);
		for (SolicitacaoReposicaoAvaliacao s : listaSolicitacoes) {
			if (s.isSelecionado())
				listaConfirmados.add(s);
		}
		
		if (listaConfirmados.isEmpty()) 
			addMensagemErro("Deve ser escolhido no m�nimo um discente");
		
		confirmaSenha();
		
		if (hasErrors())
			return null;
		
		obj.setStatus(novoStatus);
		
		return cadastrar();		
	}
	
	/**
	 * Inicia a homologa��o de solicita��o de reposi��o de prova.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciarHomologacao() throws ArqException {
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = calendario.getAno();
		periodo = calendario.getPeriodo();
		
		parecerDocente = true;
		dentroPrazo = true;
		
		analisarSolicitacoes = false;
		
		listaSolicitacoes = new ArrayList<SolicitacaoReposicaoAvaliacao>();
		comando = SigaaListaComando.HOMOLOGAR_REPOSICAO_PROVA;
		prepareMovimento(comando);		
		getListaSolicitacoes();

		
		return forward(getSolicitacoesTurmas());	
	}
	
	/**
	 * Volta  para homologa��o de solicita��o de reposi��o de prova.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/docente/homologar_solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String voltarHomologacao() throws ArqException {
				
		listaSolicitacoes = new ArrayList<SolicitacaoReposicaoAvaliacao>();
		comando = SigaaListaComando.HOMOLOGAR_REPOSICAO_PROVA;
		prepareMovimento(comando);		
		getListaSolicitacoes();

		
		return forward(getSolicitacoesTurmas());	
	}
	
	/**
	 * Redireciona para a P�gina de Registro de Homologa��o de Reposi��o de Prova.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String registrarHomologacao() throws ArqException{

		if (isEmpty( getListaSolicitacoes() )){
			addMensagemWarning("Nenhuma Solicita��o de Reposi��o de Avalia��o Cadastrada!");
			return null;
		}		
		
		int idTurma = getParameterInt("idTurma", 0);
		int idAvaliacao = getParameterInt("idAvaliacao", 0);

		SolicitacaoReposicaoAvaliacaoDao dao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class);		
		listaSolicitacoes = dao.findAllByDocente(null, getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), idTurma, idAvaliacao, ano,periodo, status,parecerDocente,dentroPrazo);
		
		comando = SigaaListaComando.HOMOLOGAR_REPOSICAO_PROVA;
		prepareMovimento(comando);		
		
		return forward(getFormHomologacao());		
	}
	
	/**
	 * Cancela uma Solicita��o de Reposi��o de Prova.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String cancelarSolicitacao() throws ArqException, NegocioException{
		populateObj();
		comando = SigaaListaComando.CANCELAR_REPOSICAO_PROVA;
		prepareMovimento(comando);
		novoStatus = new StatusReposicaoAvaliacao( StatusReposicaoAvaliacao.CANCELADA );
		return cadastrar();		
	}	

	
	/**
	 * Checa as valida��es e passa para o pr�ximo passo para a Homologa��o de Reposi��o de Prova. 
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/solicitacao_reposicao_prova/homologar_solicitacoes.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String confirmarHomologacao() throws SegurancaException, ArqException, NegocioException{
		if (ValidatorUtil.isEmpty( novoStatus )){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Situa��o");
			return null;
		}
					
		if (novoStatus.isIndeferido() && ValidatorUtil.isEmpty(observacao)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observa��o");
			return null;
		}				

		parecerPendente = false;
		listaConfirmados = new ArrayList<SolicitacaoReposicaoAvaliacao>(0);
		for (SolicitacaoReposicaoAvaliacao s : listaSolicitacoes) {
			if (s.isSelecionado()){
				if (s.getStatusParecer() == null || (s.getStatusParecer() != null && 
						novoStatus.isDeferido() && s.getStatusParecer().isIndeferido()))
					parecerPendente = true;
				listaConfirmados.add(s);
			}
		}

		if (listaConfirmados.isEmpty()) {
			addMensagemErro("Deve ser escolhido no m�nimo um discente");
			return null;
		}			
		
		
		obj.setStatus(getGenericDAO().refresh(novoStatus));	
		
		return forward(getConfirmacaoHomologacao());
	}		
	
	/**
	 * Cadastra a Homologa��o de Reposi��o de Prova. 
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/graduacao/solicitacao_reposicao_prova/confirmacao_homologacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String cadastrarHomologacao() throws ArqException, NegocioException{
		if( !confirmaSenha() )
			return null;
		return cadastrar();
	}

	/**
	 * Verifica se a data da prova � v�lida.
	 * @param data
	 * @return
	 */
	private boolean dataProvaInvalida(Date data) {
		boolean erro = false;
		if (data == null || data.getTime() <= 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data da Avalia��o");
			erro = true;
		}
		
		if (horaProva == null || horaProva.getTime() <= 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora da Avalia��o");
			erro = true;
		}
		
		int dias = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QUANTIDADE_MIN_DIAS_REALIZACAO_REPOSICAO_PROVA);
		if (data != null && data.getTime() < CalendarUtils.adicionarDiasUteis(CalendarUtils.descartarHoras(new Date()), dias).getTime()){
			addMensagemErro("A data da avalia��o deve ser pelo menos "+dias+" dias �teis ap�s data do parecer.");
			erro = true;
		}
		
		if (!erro){			
			Calendar cHora = Calendar.getInstance();
			cHora.setTime(horaProva);		
			
			if (cHora.getTimeInMillis() <= 0){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora da Avalia��o");
				erro = true;
			}

			/* Monta a data com a hora */
			Calendar cData = Calendar.getInstance();
			cData.setTime(dataProva);
			cData.set(Calendar.HOUR_OF_DAY, cHora.get(Calendar.HOUR_OF_DAY));
			cData.set(Calendar.MINUTE, cHora.get(Calendar.MINUTE));
			cData.set(Calendar.SECOND, 0);
			cData.set(Calendar.MILLISECOND, 0);			

			obj.setDataProvaSugerida(cData.getTime());					
		}
		
		return erro;
	}
	
	/**
	 * Retorna todas as Avalia��es para o combo.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/form.jsp</li>
	 * </ul>
	 * @return 
	 */
	public Collection<SelectItem> getAllAvaliacoesCombo() throws DAOException{
		return toSelectItems(getListaAvaliacoes(), "id", "descricao");
	}	
	
	/**
	 * Retorna todas os Status de Homologa��o da Solicita��o de Reposi��o de Prova para o combo.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/homologar_solicitacoes.jsp</li>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/lista_parecer.jsp</li>
	 * </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<SelectItem> getStatusCombo() throws HibernateException, DAOException {
		SolicitacaoReposicaoAvaliacaoDao dao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class);
		try {
			return toSelectItems(dao.findAllStatusParecer(), "id", "descricao");
		} finally {
			if (dao != null)
				dao.close();
		}
	}		
		
	@Override
	public String getFormPage() {
		return "/graduacao/solicitacao_reposicao_prova/form.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/graduacao/solicitacao_reposicao_prova/lista.jsp";
	}
	
	public String getFormTurmas(){
		return "/graduacao/solicitacao_reposicao_prova/lista_turmas.jsp";
	}

	@Override
	public String getViewPage() {
		return "/graduacao/solicitacao_reposicao_prova/viewImpressao.jsp";
	}
	
	public String getFormParecer(){
		return "/graduacao/solicitacao_reposicao_prova/lista_parecer.jsp";
	}
	
	public String getFormHomologacao(){
		return "/graduacao/solicitacao_reposicao_prova/homologar_solicitacoes.jsp";
	}
		
	public String getSolicitacoesTurmas(){
		return "/graduacao/solicitacao_reposicao_prova/solicitacoes_turmas.jsp";
	}
	
	public String getConfirmacaoHomologacao(){
		return "/graduacao/solicitacao_reposicao_prova/confirmacao_homologacao.jsp";
	}		
	
	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public int getTamanhoMaxArquivo() {
		return tamanhoMaxArquivo;
	}

	public void setTamanhoMaxArquivo(int tamanhoMaxArquivo) {
		this.tamanhoMaxArquivo = tamanhoMaxArquivo;
	}

	public Collection<DataAvaliacao> getListaAvaliacoes() {
		return listaAvaliacoes;
	}

	public void setListaAvaliacoes(Collection<DataAvaliacao> listaAvaliacoes) {
		this.listaAvaliacoes = listaAvaliacoes;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/**
	 * Retorna a lista de solicita��es de reposi��o de prova.
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<SolicitacaoReposicaoAvaliacao> getListaSolicitacoes() throws DAOException {
		if (isEmpty(listaSolicitacoes)){
			SolicitacaoReposicaoAvaliacaoDao dao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class);
			try {
				if (isPortalDiscente())
					listaSolicitacoes = dao.findByDiscente(getDiscenteUsuario(), null, null);
				else
				if (isPortalDocente())
					if (getAcessoMenu().isChefeDepartamento() && getUltimoComando().equals(SigaaListaComando.HOMOLOGAR_REPOSICAO_PROVA)){						
						listaSolicitacoes = dao.findAllByDocente(null, getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), 0, 0, ano, periodo, status, parecerDocente, dentroPrazo);
						configurarAvaliacoes();
					} else if (getUltimoComando().equals(SigaaListaComando.PARECER_REPOSICAO_PROVA)) {
						listaSolicitacoes = dao.findAllByDocente(getUsuarioLogado().getServidor(), 0, 0, 0, ano, periodo, status, false, false);
						configurarAvaliacoes();
					}	
			} finally {
				if (dao != null)
					dao.close();
			}
		}		
		return listaSolicitacoes;
	}

	/**
	 * Configura as avalia��es das solicita��es que ser�o exibidas na listagem
	 * <br/><br/>
	 * M�todo n�o invocado por JSPs:
	 * @return
	 * @throws DAOException
	 */
	private void configurarAvaliacoes() {

		if (listaSolicitacoes != null && !listaSolicitacoes.isEmpty()){
		
			SolicitacaoReposicaoAvaliacao sol = listaSolicitacoes.get(0);
			sol.setAvaliacoes(new ArrayList<DataAvaliacao>());
			sol.getAvaliacoes().add(sol.getDataAvaliacao());
			
			for (SolicitacaoReposicaoAvaliacao s : listaSolicitacoes){
				if (sol.getTurma().getId() == s.getTurma().getId()){
					if ( !sol.getAvaliacoes().contains(s.getDataAvaliacao()) )
						sol.getAvaliacoes().add(s.getDataAvaliacao());
				}
				else {
					sol = s;
					sol.setAvaliacoes(new ArrayList<DataAvaliacao>());
					sol.getAvaliacoes().add(sol.getDataAvaliacao());
				}	
			}			
		} 		
	}

	/**
	 * Filtra as turmas que ser�o listadas para analisar as solicita��es
	 * <br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/solicitacoes_turmas.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String filtrar() throws DAOException {
		listaSolicitacoes = null;
		getListaSolicitacoes();		
		return forward(getSolicitacoesTurmas());	
	}
	
	/**
	 * Monta o combo com as situa��es das solicita��es de reposi��o.
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/solicitacoes_turmas.jsp</li>
	 * </ul>
	 * @return
	 */
	public ArrayList<SelectItem> getStatusFiltroCombo () {	
		ArrayList<SelectItem> statusCombo = new ArrayList<SelectItem>();
		statusCombo.add(new SelectItem(TODAS_SOLICITACOES,"-- TODOS AS SOLICITA��ES --"));
		statusCombo.add(new SelectItem(StatusReposicaoAvaliacao.CADASTRADA," CADASTRADA "));
		statusCombo.add(new SelectItem(SUBMETIDO_HOMOLOGACAO," SUBMETIDO HOMOLOGA��O "));
		statusCombo.add(new SelectItem(StatusReposicaoAvaliacao.DEFERIDO," DEFERIDO "));
		statusCombo.add(new SelectItem(StatusReposicaoAvaliacao.INDEFERIDO," INDEFERIDO "));
		statusCombo.add(new SelectItem(StatusReposicaoAvaliacao.CANCELADA," CANCELADA "));
		return statusCombo;
	}
	
	/**
	 * Trata o status falso, SUBMETIDO HOMOLOGA��O.<br/><br/>
	 * M�todo Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/solicitacoes_turmas.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws ArqException
	 */
	public void tratarStatusSubmetido ( ValueChangeEvent evt) throws ArqException {
		
		statusFiltro = (Integer) evt.getNewValue();
		if ( statusFiltro == SUBMETIDO_HOMOLOGACAO ){
			status = StatusReposicaoAvaliacao.CADASTRADA;
			parecerDocente = true;
		} else
			status = statusFiltro;
	}
	
	public void setListaSolicitacoes(
			List<SolicitacaoReposicaoAvaliacao> listaSolicitacoes) {
		this.listaSolicitacoes = listaSolicitacoes;
	}

	public StatusReposicaoAvaliacao getNovoStatus() {
		return novoStatus;
	}

	public void setNovoStatus(StatusReposicaoAvaliacao novoStatus) {
		this.novoStatus = novoStatus;
	}

	public Date getDataProva() {
		return dataProva;
	}

	public void setDataProva(Date dataProva) {
		this.dataProva = dataProva;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getHoraProva() {
		return horaProva;
	}

	public void setHoraProva(Date horaProva) {
		this.horaProva = horaProva;
	}

	public List<SolicitacaoReposicaoAvaliacao> getListaConfirmados() {
		return listaConfirmados;
	}

	public void setListaConfirmados(List<SolicitacaoReposicaoAvaliacao> listaConfirmados) {
		this.listaConfirmados = listaConfirmados;
	}

	public boolean isParecerPendente() {
		return parecerPendente;
	}

	public void setParecerPendente(boolean parecerPendente) {
		this.parecerPendente = parecerPendente;
	}

	public void setListaTurmas(List<Map<String, Object>> listaTurmas) {
		this.listaTurmas = listaTurmas;
	}

	public List<Map<String, Object>> getListaTurmas() {
		return listaTurmas;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getAno() {
		return ano;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setDentroPrazo(boolean dentroPrazo) {
		this.dentroPrazo = dentroPrazo;
	}

	public boolean isDentroPrazo() {
		return dentroPrazo;
	}

	public void setParecerDocente(boolean parecerDocente) {
		this.parecerDocente = parecerDocente;
	}

	public boolean isParecerDocente() {
		return parecerDocente;
	}

	public void setAnalisarSolicitacoes(boolean analisarSolicitacoes) {
		this.analisarSolicitacoes = analisarSolicitacoes;
	}

	public boolean isAnalisarSolicitacoes() {
		return analisarSolicitacoes;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}
	
	public Integer getStatusFiltro() {
		return statusFiltro;
	}

	public void setStatusFiltro(Integer statusFiltro) {
		this.statusFiltro = statusFiltro;
	}
}

