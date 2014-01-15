/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean Responsável por gerenciar as operações no caso de uso de Solicitação de Reposição de Prova.
 * @author Arlindo
 *
 */
@Component("solicitacaoReposicaoProva") @Scope("request")
public class SolicitacaoReposicaoAvaliacaoMBean extends SigaaAbstractController<SolicitacaoReposicaoAvaliacao> {

	/** Indica que são selecionadas todas as Solicitações. */
	public static final int TODAS_SOLICITACOES = 0;
	/** Indica que são selecionadas apenas solicitações com parecer do docente. */
	public static final int SUBMETIDO_HOMOLOGACAO = -1;
	
	/** Preenchido se o aluno anexar um arquivo. */
	private UploadedFile arquivo;
	
	/** Tamanho máximo do arquivo de anexo */
	private int tamanhoMaxArquivo;
	
	/** Turma Selecionada */
	private Turma turma;
	
	/** Lista de Avaliações que o Aluno pode solicitar reposição */
	private Collection<DataAvaliacao> listaAvaliacoes;
	
	/** Lista de Solicitações de Provas */
	private List<SolicitacaoReposicaoAvaliacao> listaSolicitacoes;
	
	/** Novo status definido na Homologação */
	private StatusReposicaoAvaliacao novoStatus;
	
	/** Data da Prova definida na Homologação */	
	private Date dataProva;
	
	/** Observação definida na Homologação */
	private String observacao;
	
	/** Comando que será executado */
	private Comando comando;
	
	/** Hora da Prova */
	private Date horaProva;
	
	/** Lista de Solicitações Confirmados para Apreciação/Homologação */
	private List<SolicitacaoReposicaoAvaliacao> listaConfirmados;
	
	/** Indica se não foi dado parecer em alguma das solicitações */
	private boolean parecerPendente;
	
	/** Lista de turmas abertas com avaliações */
	private List<Map<String, Object>> listaTurmas = new ArrayList<Map<String,Object>>();
	
	/** Ano para o filtro de turmas */
	private Integer ano;
	
	/** Período para o filtro de turmas */
	private Integer periodo;
	
	/** Dentro do prazo de 3 dias úteis */
	private boolean dentroPrazo;
	
	/** Foi dado o parecer do docente. */
	private boolean parecerDocente;
	
	/** Status das solicitações de reposição */
	private Integer status;
	
	/** Status das solicitações para o filtro de turmas - Trata o status submetido a chefia */
	private Integer statusFiltro;
	
	/** Se entrou pelo caso de uso de analisar solicitações ou apreciar solicitações. */
	private boolean analisarSolicitacoes;
		
	/** Construtor Padrão da Classe */
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
	 * Inicia a Solicitação de Reposição de Prova.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
				addMensagemErro("Não existe nenhuma Avaliação disponível para Reposição, ou está fora do prazo para solicitar reposição!");
				return null;
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getFormTurmas());
	}
	
	/**
	 * Inicia a Solicitação de Reposição de Prova.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/lista_turma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */	
	public String iniciarSolicitacao() throws ArqException{
		if (getCalendarioVigente().isPeriodoFerias()){
			addMensagemErro("Não pode solicitar Reposição de Avaliação no Período de Férias");
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
			
			// verifica se o discente já possui alguma solicitação ativa da avaliação selecionada.
			if (!isEmpty(reposicaoDao.findByDiscente(getDiscenteUsuario(), dataAvaliacao, true))){
				addMensagemErro("Já existe uma solicitação ativa para a avaliação selecionada.");
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
		// Atribui o tamanho máximo do arquivo a ser anexado pelo aluno. 
		tamanhoMaxArquivo = ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_PADRAO_ARQUIVO_ALUNO);
		// se não existir valor definido atribui 5MB
		if (tamanhoMaxArquivo <= 0)
			tamanhoMaxArquivo = 5; 
		
		if (turma.isAgrupadora() && dao != null)
			turma = dao.findSubturmaByTurmaDiscente(obj.getDataAvaliacao().getTurma(), getDiscenteUsuario());
		
		obj.setTurma(turma);		
		obj.setDiscente((DiscenteGraduacao) getDiscenteUsuario());
		
		return forward(getFormPage());		
	}
	
	/**
	 * Lista as Solicitação de Reposição de Provas.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * @return
	 */
	public String listarSolicitacoes(){
		return forward(getListPage());
	}
	
	/**
	 * Realiza as validações antes de cadastrar. 
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
		try {// verifica se o discente já possui alguma solicitação ativa da avaliação selecionada.
			if (!isEmpty(reposicaoDao.findByDiscente(getDiscenteUsuario(), obj.getDataAvaliacao(), true))){
				addMensagemErro("Já existe uma solicitação ativa para a avaliação informada.");
				return null;
			}
		} finally {
			if (reposicaoDao != null)
				reposicaoDao.close();
		}
		
		if (arquivo != null){
			// verifica se o tamanha do arquivo é maior que o permitido.
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
	 * Realiza o Cadastro da Solicitação de Reposição de Prova.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
				addMensagemInformation("Sua solicitação foi cadastrada com sucesso. Foi enviado um email para o(s) Docente(s) da disciplina informada, " +
						"notificando sua solicitação para que seja analisada. Após análise, " +
						"você receberá um email informando do parecer final. ATENÇÃO! Esta Solicitação não garante a Reposição da Avaliação, " +
				"é necessário sua aprovação.");				
			} else if (getUltimoComando().equals(SigaaListaComando.PARECER_REPOSICAO_PROVA)){
				addMensagemInformation("Parecer Registrado com sucesso. ATENÇÃO! Esta Solicitação não garante a Reposição da Avaliação, " +
				"é necessário sua aprovação da chefia do departamento.");				
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
	 * Visualiza a solicitação de reposição de prova.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Redireciona para a Página de Registro de Parecer.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Redireciona para a Página de Registro de Parecer.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
 	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/solicitacoes_turmas.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String registrarParecer() throws ArqException{
		
		if (isEmpty( getListaSolicitacoes() )){
			addMensagemWarning("Nenhuma Solicitação de Reposição de Avaliação Cadastrada!");
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
	 * Método Chamado pela seguinte JSP:
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
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Situação");
		
		if (novoStatus.isDeferido())
			dataProvaInvalida(dataProva);
		
		if (novoStatus.isDeferido() && isEmpty(obj.getLocalProva()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Local");
		
		if (novoStatus.isIndeferido() && ValidatorUtil.isEmpty(observacao))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observação");

		listaConfirmados = new ArrayList<SolicitacaoReposicaoAvaliacao>(0);
		for (SolicitacaoReposicaoAvaliacao s : listaSolicitacoes) {
			if (s.isSelecionado())
				listaConfirmados.add(s);
		}
		
		if (listaConfirmados.isEmpty()) 
			addMensagemErro("Deve ser escolhido no mínimo um discente");
		
		confirmaSenha();
		
		if (hasErrors())
			return null;
		
		obj.setStatus(novoStatus);
		
		return cadastrar();		
	}
	
	/**
	 * Inicia a homologação de solicitação de reposição de prova.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Volta  para homologação de solicitação de reposição de prova.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Redireciona para a Página de Registro de Homologação de Reposição de Prova.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String registrarHomologacao() throws ArqException{

		if (isEmpty( getListaSolicitacoes() )){
			addMensagemWarning("Nenhuma Solicitação de Reposição de Avaliação Cadastrada!");
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
	 * Cancela uma Solicitação de Reposição de Prova.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Checa as validações e passa para o próximo passo para a Homologação de Reposição de Prova. 
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Situação");
			return null;
		}
					
		if (novoStatus.isIndeferido() && ValidatorUtil.isEmpty(observacao)){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Observação");
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
			addMensagemErro("Deve ser escolhido no mínimo um discente");
			return null;
		}			
		
		
		obj.setStatus(getGenericDAO().refresh(novoStatus));	
		
		return forward(getConfirmacaoHomologacao());
	}		
	
	/**
	 * Cadastra a Homologação de Reposição de Prova. 
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Verifica se a data da prova é válida.
	 * @param data
	 * @return
	 */
	private boolean dataProvaInvalida(Date data) {
		boolean erro = false;
		if (data == null || data.getTime() <= 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data da Avaliação");
			erro = true;
		}
		
		if (horaProva == null || horaProva.getTime() <= 0){
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora da Avaliação");
			erro = true;
		}
		
		int dias = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QUANTIDADE_MIN_DIAS_REALIZACAO_REPOSICAO_PROVA);
		if (data != null && data.getTime() < CalendarUtils.adicionarDiasUteis(CalendarUtils.descartarHoras(new Date()), dias).getTime()){
			addMensagemErro("A data da avaliação deve ser pelo menos "+dias+" dias úteis após data do parecer.");
			erro = true;
		}
		
		if (!erro){			
			Calendar cHora = Calendar.getInstance();
			cHora.setTime(horaProva);		
			
			if (cHora.getTimeInMillis() <= 0){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Hora da Avaliação");
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
	 * Retorna todas as Avaliações para o combo.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/form.jsp</li>
	 * </ul>
	 * @return 
	 */
	public Collection<SelectItem> getAllAvaliacoesCombo() throws DAOException{
		return toSelectItems(getListaAvaliacoes(), "id", "descricao");
	}	
	
	/**
	 * Retorna todas os Status de Homologação da Solicitação de Reposição de Prova para o combo.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Retorna a lista de solicitações de reposição de prova.
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Configura as avaliações das solicitações que serão exibidas na listagem
	 * <br/><br/>
	 * Método não invocado por JSPs:
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
	 * Filtra as turmas que serão listadas para analisar as solicitações
	 * <br/><br/>
	 * Método Chamado pela seguinte JSP:
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
	 * Monta o combo com as situações das solicitações de reposição.
	 * Método Chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/ensino/solicitacao_reposicao_prova/solicitacoes_turmas.jsp</li>
	 * </ul>
	 * @return
	 */
	public ArrayList<SelectItem> getStatusFiltroCombo () {	
		ArrayList<SelectItem> statusCombo = new ArrayList<SelectItem>();
		statusCombo.add(new SelectItem(TODAS_SOLICITACOES,"-- TODOS AS SOLICITAÇÕES --"));
		statusCombo.add(new SelectItem(StatusReposicaoAvaliacao.CADASTRADA," CADASTRADA "));
		statusCombo.add(new SelectItem(SUBMETIDO_HOMOLOGACAO," SUBMETIDO HOMOLOGAÇÃO "));
		statusCombo.add(new SelectItem(StatusReposicaoAvaliacao.DEFERIDO," DEFERIDO "));
		statusCombo.add(new SelectItem(StatusReposicaoAvaliacao.INDEFERIDO," INDEFERIDO "));
		statusCombo.add(new SelectItem(StatusReposicaoAvaliacao.CANCELADA," CANCELADA "));
		return statusCombo;
	}
	
	/**
	 * Trata o status falso, SUBMETIDO HOMOLOGAÇÃO.<br/><br/>
	 * Método Chamado pela seguinte JSP:
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

