/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/01/2011
 *
 */

package br.ufrn.sigaa.ava.questionarios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.jsf.CadastroTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.ConfiguracoesAvaMBean;
import br.ufrn.sigaa.ava.jsf.ControllerTurmaVirtual;
import br.ufrn.sigaa.ava.jsf.PermissaoAvaMBean;
import br.ufrn.sigaa.ava.jsf.TopicoAulaMBean;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ava.negocio.AvaliacaoHelper;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.ava.negocio.RegistroAtividadeAvaHelper;
import br.ufrn.sigaa.ava.questionario.dao.PerguntaQuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionario.dao.QuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.AlternativaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.CategoriaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.ConjuntoRespostasQuestionarioAluno;
import br.ufrn.sigaa.ava.questionarios.dominio.EnvioRespostasQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.FeedbackQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.RespostaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.negocio.MovimentoAdicionarPerguntasDoBanco;
import br.ufrn.sigaa.ava.questionarios.negocio.MovimentoCorrigirQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.negocio.MovimentoPublicarNotasQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.negocio.MovimentoSalvarPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.util.LatexUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.jsf.ConsolidarTurmaMBean;

/**
 * MBean que gerencia os questionários da turma virtual.
 * @author Fred_Castro
 *
 */

@Component("questionarioTurma")
@Scope("request")
public class QuestionarioTurmaMBean extends CadastroTurmaVirtual<QuestionarioTurma> {

	/** String que indica ao managed bean que deve direcionar o docente à página de configuração da turma. */
	public static final String CONFIGURAR_TURMA = "configurar turma";
	
	/** Lista com os questionários da turma */
	private List <QuestionarioTurma> questionarios;
	
	/** O questionário que está sendo utilizado no momento*/
	private QuestionarioTurma questionario;
	
	/** A pergunta que está sendo gerenciada no momento */
	private PerguntaQuestionarioTurma pergunta;
	
	/** A alternativa que está sendo gerenciada no nomento */
	private AlternativaPerguntaQuestionarioTurma alternativa = new AlternativaPerguntaQuestionarioTurma();
	
	/** Lista com todas as alternativa a serem removidas */
	private List <AlternativaPerguntaQuestionarioTurma> alternativasRemover;
	
	/** DataModel para as alternativas */
	private DataModel modelAlternativas;
	
	/** DataModel para as perguntas */
	private DataModel modelPerguntas;
	
	/** A resposta que está sendo utilizada no momento */
	private EnvioRespostasQuestionarioTurma resposta;
	
	/** O conjunto que está sendo utilizada no momento */
	private ConjuntoRespostasQuestionarioAluno conjunto;
	
	/** DataModel para as respostas */
	private DataModel respostasModel;
	
	/** Variável que indica se o aluno pode enviar as respostas novamente */
	private boolean permiteNovaTentativa;
	
	/** Variável que indica a quantidade de tentativas que o aluno já fez */
	private int tentativas;
	
	/** Indica qual a alternativa correta quando a pergunta é de única escolha */
	private Integer gabaritoUnicaEscolha;
	
	/** Indica se há avaliações associadas ao questionário */
	private boolean possuiAvaliacoes;
	
	/** Indica se a questão está sendo cadastrada em uma categoria */
	private boolean adicionarEmCategoria = false;
	
	/** Indica se é para não salvar o questionário ao mudar de páginas */
	private boolean naoSalvar = false;
	
	/** Se deve ser enviado um e-mail para os alunos ao cadastrar um novo questionário. */
	private boolean notificarAlunos = true;
	
	/**
	 * atributo utilizado para controlar se a ação é alterar ou cadastrar nova pergunta
	 * necessário pois se a pergunta ainda não tiver sido persistida ela terá id = 0 mesmo que seja alteração
	 */
	private boolean acaoAlterarPergunta = false;
	
	/**
	 * Indica se é para voltar à edição de perguntas do questionário, ao cancelar a edição de uma pergunta. Se for false, deve voltar para o banco de questões.
	 */
	private boolean voltarAoQuestionario = false;
	
	/**
	 * Indica se é para não validar as respostas na submissão do aluno. Usado para o método que mantém a sessão do aluno ativa.
	 */
	private boolean naoValidarRespostas = false;
	
	/** Indica o mínimo para o próximo feedback a ser criado. */
	private int minFeedback = 0;
	
	/** Indica o máximo para o próximo feedback a ser criado. */
	private int maxFeedback = 100;
	
	/** Indica o texto para o próximo feedback a ser criado. */
	private String txtFeedback;
	
	/** DataModel para os feedbacks */
	private DataModel modelFeedbacks;
	
	/** Uma lista de SelectItem que contêm opções de zero a cem */
	private List <SelectItem> zeroACem;
	
	/** Mensagem a ser exibida para o cadastro de feedbacks */
	private String mensagemFeedback;
	
	/** Lista com todos os feedbacks a serem removidas */
	private List <FeedbackQuestionarioTurma> feedbacksRemover;
	
	/** Atributo que auxilia no cadastro de uma nova categoria à partir do formulário de cadastrar pergunta. No caso, faz a nova pergunta vir com a nova categoria já selecionada. */
	private int idCategoria;
	
	/** Atributo que indica se o questionário está sendo finalizado. */
	boolean finalizarQuestionario = false;

	/** Indica se ao inicializar o questionario os dados deverão ser mantidos */
	private boolean manterDados = false;
	
	/** Lista com todos os conjuntos de respostas dos discentes */
	private List<ConjuntoRespostasQuestionarioAluno> conjuntoRespostas;

	/** Mensagens de erro da tela de perguntas. */
	private String mensagemErroPergunta;
	
	/**
	 * Exibe a lista de questionários para o docente.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>Não invocado por JSPs. É acessado pela classe MenuTurmaMBean</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarQuestionariosDocente () throws DAOException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		PermissaoAvaMBean perm = getMBean("permissaoAva");
		
		if (tBean.isDocente() || perm.getPermissaoUsuario().isDocente()){
			QuestionarioTurmaDao dao = null;
			
			try {
				dao = getDAO(QuestionarioTurmaDao.class);
				questionarios = dao.findQuestionariosAtivosParaListagem(turma().getId(),true);
			} finally {
				if (dao != null)
					dao.close();
			}
			
			return forward("/ava/QuestionarioTurma/listar.jsp");
		}
		
		return null;
	}
	
	/**
	 * Exibe a lista de questionários para o discente.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>Não invocado por JSPs. É acessado pela classe MenuTurmaMBean</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarQuestionariosDiscente () {
		
		if (questionarios == null){
			QuestionarioTurmaDao dao = null;
			
			try {
				dao = getDAO(QuestionarioTurmaDao.class);
				questionarios = dao.findQuestionariosAtivosParaListagem(turma().getId(),false);
								
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return forward("/ava/QuestionarioTurma/listarDiscente.jsp");
	}
	
	/**
	 * Inicia o cadastro de um questionário em um tópico de aula.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>Não invocado por JSPs. É acessado pela classe TopicoAulaMBean</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String novoQuestionarioParaTopico (int idTopicoSelecionado) throws ArqException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			novoQuestionario();
			questionario.getAula().setId(idTopicoSelecionado);
		}
		return null;
	}
	
	
	/**
	 * Inicializa os objetos das respostas.
	 * 
	 * @throws DAOException
	 */
	private void popularRespostas() throws DAOException {
		
		// Se o usuário já iniciou o questionário, utiliza as mesmas perguntas.
		if (resposta.getId() != 0){
			
			questionario.setPerguntas(new ArrayList<PerguntaQuestionarioTurma>());
			
			for (RespostaPerguntaQuestionarioTurma r : resposta.getRespostas())
				questionario.getPerguntas().add(r.getPergunta());
			
		// Se o usuário está iniciando agora, monta o questionário
		} else {
			PerguntaQuestionarioTurmaDao pDAO = null;
			QuestionarioTurmaDao qtDAO = null;
			
			try {
				List <RespostaPerguntaQuestionarioTurma> respostas = new ArrayList<RespostaPerguntaQuestionarioTurma>();
				
				pDAO = getDAO(PerguntaQuestionarioTurmaDao.class);
				qtDAO = getDAO(QuestionarioTurmaDao.class);
				
				questionario.setPerguntas(pDAO.findAllPerguntasQuestionario(questionario));
				
				boolean dependeUltima = false;
				// Ultima Tentativa
				if ( questionario.getDependeUltima() != null && questionario.getDependeUltima() ) {
					List<EnvioRespostasQuestionarioTurma> tentativas = qtDAO.findRespostasByQuestionarioAluno(questionario,getUsuarioLogado(),true,true);
					
					if ( !isEmpty(tentativas) ){
						EnvioRespostasQuestionarioTurma ultimaTentativa = tentativas.get(0);
						dependeUltima = true;
						for (RespostaPerguntaQuestionarioTurma r : ultimaTentativa.getRespostas()){
							RespostaPerguntaQuestionarioTurma novaResposta = new RespostaPerguntaQuestionarioTurma(resposta, r.getPergunta());
							respostas.add(novaResposta);
						}
					}
				}
				
				if ( !dependeUltima ){
					// Se for para mostrar somente um subconjunto das tarefas, remove para ficarem quantas tarefas o docente especificou.
					if (questionario.isExibirSubConjunto() && questionario.getPerguntas().size() > questionario.getTamanhoSubConjunto())
						while (questionario.getPerguntas().size() > questionario.getTamanhoSubConjunto())
							questionario.getPerguntas().remove((int)(Math.random()*100000) % questionario.getPerguntas().size() );
					
					for (PerguntaQuestionarioTurma pergunta : questionario.getPerguntas() ) {
						RespostaPerguntaQuestionarioTurma r = new RespostaPerguntaQuestionarioTurma(resposta, pergunta);
						respostas.add(r);
					}
				}
				resposta.setRespostas(respostas);
				
				resposta.setUsuarioEnvio(getUsuarioLogado());
				resposta.setQuestionario(questionario);
			} finally {
				if (pDAO != null)
					pDAO.close();
				if (qtDAO != null)
					qtDAO.close();
			}
		}
		
		// Se for para misturar as perguntas,
		if (questionario.isMisturarPerguntas())
			Collections.shuffle(resposta.getRespostas());
		
		int c = 1;
		for (RespostaPerguntaQuestionarioTurma r : resposta.getRespostas()){
			// Corrige a ordem das tarefas.
			r.getPergunta().setOrdem(c++);
			
			// Se for para misturar as alternativas,
			if (r.getPergunta().getAlternativas() != null && questionario.isMisturarAlternativas())
				Collections.shuffle(r.getPergunta().getAlternativas());
		}
		
		respostasModel = new ListDataModel(resposta.getRespostas());
	}
	
	/**
	 * Inicia o cadastro de um novo questionário.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String novoQuestionario () throws ArqException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			
			if ( !manterDados ){
			
				questionario = new QuestionarioTurma();
				questionario.setTurma(turma());
				
				questionario.setInicio(new Date());
				
				Date fim = new Date ();
				Calendar cal = Calendar.getInstance();
				cal.setTime(fim);
				cal.add(Calendar.DAY_OF_MONTH, 7);
				questionario.setFim(cal.getTime());
				
				Date fimVisualizacao = new Date ();
				cal.setTime(fimVisualizacao);
				cal.add(Calendar.DAY_OF_MONTH, 30);
				questionario.setFimVisualizacao(cal.getTime());
				
				modelFeedbacks = new ListDataModel( questionario.getFeedbacks());
			} else
				manterDados = false;
			
			prepareMovimento(SigaaListaComando.SALVAR_QUESTIONARIO_TURMA);
			
			naoSalvar = false;
			
			return formularioQuestionario();
		}
		
		return null;
	}
	
	/**
	 * Inicia o cadastro de um novo questionário.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String alterarDadosDoQuestionario () throws ArqException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		naoSalvar = getParameterBoolean("naoSalvar");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			int idQuestionario = getParameterInt("id", 0);
			
			if (idQuestionario > 0){
				GenericDAO dao = null;
				
				try {
					dao = getGenericDAO();
					questionario = dao.findByPrimaryKey(idQuestionario, QuestionarioTurma.class);
					questionario.getPerguntas();
				} finally {
					if (dao != null)
						dao.close();
				}
			}
			
			prepareMovimento(SigaaListaComando.SALVAR_QUESTIONARIO_TURMA);
			
			if (questionario.getAula() == null)
				questionario.setAula(new TopicoAula());
			else
				questionario.setAula(getGenericDAO().refresh(questionario.getAula()));
			
			if ( questionario.getMaterial() != null )
				questionario.setMaterial(getGenericDAO().refresh(questionario.getMaterial()));
			
			if (questionario.isPossuiNota() && StringUtils.isEmpty(questionario.getAbreviacao()))
				questionario.setPossuiNota(false);

			
			modelFeedbacks = new ListDataModel( questionario.getFeedbacks());
			
			return formularioQuestionario();
		}
		
		return null;
	}
	
	/**
	 * Exibe o formulário de gerenciar suas perguntas sem salvar os dados do questionário.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerenciarPerguntasDoQuestionarioSemSalvar () throws ArqException {
		naoSalvar = true;
		
		return gerenciarPerguntasDoQuestionario();
	}
	
	/**
	 * Salva os dados do questionário e exibe o formulário de gerenciar suas perguntas.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/formDadosQuestionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerenciarPerguntasDoQuestionario () throws ArqException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			GenericDAO dao = null;
			try {
				
				if (!getParameterBoolean("naoSalvar") && naoSalvar == false){
					prepareMovimento(ArqListaComando.ALTERAR);
					
					String resultado = salvarQuestionario();
					
					if (resultado != null && resultado.equals(CONFIGURAR_TURMA))
						return null;
					
					if (!hasOnlyErrors())
						naoSalvar = true;
				}
	
				dao = getGenericDAO();
	
				int idQuestionario = getParameterInt("id", 0);
				
				if (idQuestionario > 0)
					questionario = dao.findByPrimaryKey(idQuestionario, QuestionarioTurma.class);

				questionario.getAula().getId();
				questionario.getFeedbacks().iterator();
				
				if ( questionario.getMaterial() != null )
					questionario.setMaterial(getGenericDAO().refresh(questionario.getMaterial()));
				
				questionario.setPerguntas((List<PerguntaQuestionarioTurma>) dao.findByExactField(PerguntaQuestionarioTurma.class, new String [] {"questionarioTurma.id", "ativo"}, new Object [] {questionario.getId(), true}));
				modelPerguntas = new ListDataModel( questionario.getPerguntas() );
			} finally {
				if (dao != null)
					dao.close();
			}
			
			if (!hasOnlyErrors())
				return formularioPerguntasDoQuestionario ();
		}
		
		return null;
	}
	
	/**
	 * Salva os dados do questionário sem exibir a tela de gerenciar perguntas.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/formDadosQuestionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String salvarQuestionario () throws ArqException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		// Registra a tentativa de cadastrar um questionário.
		boolean alterando = false;
		if (questionario.getId() == 0)
			registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.INICIAR_INSERCAO);
		else {
			alterando = true;
			registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.INICIAR_ALTERACAO, questionario.getId());
		}
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
		
			boolean erro = false;
			boolean voltarPerguntas = false;
			
			if (StringUtils.isEmpty(questionario.getTitulo())){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título");
				erro = true;
			}
				
			boolean inteirosOk = true;
			
			if (isEmpty(questionario.getTentativas())){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tentativas");
				erro = true;
				inteirosOk = false;
			}
			
			if (isEmpty(questionario.getDuracao())){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Duração");
				erro = true;
				inteirosOk = false;
			}
			
			boolean datasOk = true;
			
			if (questionario.getInicio() == null){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de início");
				erro = true;
				datasOk = false;
			}
			
			if (questionario.getFim() == null){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de fim");
				erro = true;
				datasOk = false;
			}
			
			if (questionario.getFimVisualizacao() == null){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data limite para visualização");
				erro = true;
				datasOk = false;
			}
			
			if (datasOk){
				if (questionario.getInicio().after(questionario.getFim())){
					addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Início");
					erro = true;
				}

				if (questionario.getInicio().equals(questionario.getFim())){
					if ( questionario.getHoraInicio() > questionario.getHoraFim() ){
						addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Início");
						erro = true;
					} else if ( questionario.getHoraInicio() == questionario.getHoraFim() ){
						if ( questionario.getMinutoInicio() >= questionario.getMinutoFim() ) {
							addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM, "Início");
							erro = true;
						}	
					}	
				}	
				
				if (questionario.getFim().after(questionario.getFimVisualizacao())){
					addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Fim da visualização", "Fim");
					erro = true;
				}
				
				if (questionario.getFim().equals(questionario.getFimVisualizacao())){
					if ( questionario.getHoraFim() > questionario.getHoraFimVisualizacao() ){
						addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Fim da visualização", "Fim");
						erro = true;
					} else if ( questionario.getHoraFim() == questionario.getHoraFimVisualizacao() ){
						if ( questionario.getMinutoFim() > questionario.getMinutoFimVisualizacao() ) {
							addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Fim da visualização", "Fim");
							erro = true;
						}
					}	
				}
			}
			
			if (questionario.getAula() == null || questionario.getAula().getId() <= 0){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tópico de aula");
				erro = true;
			}
			
			if (inteirosOk){
				if (questionario.getTentativas() <= 0){
					addMensagemErro("A quantidade tentativas de respostas deve ser maior que zero");
					erro = true;
				}
				
				if (questionario.getDuracao() <= 0){
					addMensagemErro("A duração da tentativa deve ter um valor razoável, em minutos, e deve ser suficiente para os discentes responderem o questionário na íntegra");
					erro = true;
				}
			}

			if (questionario.isExibirSubConjunto() && questionario.getDependeUltima() == null){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Depende da última tentativa");
				erro = true;
				voltarPerguntas = true;
			}
			
			if (questionario.isExibirSubConjunto() && (questionario.getTamanhoSubConjunto() == null || questionario.getTamanhoSubConjunto() <= 0)){
				addMensagemErro("Informe a quantidade de perguntas a serem exibidas no sub-conjunto. Este número deve ser maior que zero.");
				voltarPerguntas = true;
			}
			
			if (questionario.isExibirSubConjunto() && questionario.getPerguntas() != null && !questionario.getPerguntas().isEmpty() && questionario.getTamanhoSubConjunto() > questionario.getPerguntas().size()){
				addMensagemErro("A quantidade de perguntas a serem exibidas no sub-conjunto não deve ser maior que o número de perguntas.");
				voltarPerguntas = true;
			}
			
			
			if (voltarPerguntas)
				return formularioPerguntasDoQuestionario();
			
			if (!erro){
				GenericDAO dao = null;
				
				try {
					dao = getGenericDAO();
				
					if (questionario.getAula() != null && questionario.getAula().getId() == -1)
						questionario.setAula(null);
					
					int i = 1;
					if (questionario.getPerguntas() != null)
						for (PerguntaQuestionarioTurma p : questionario.getPerguntas())
							p.setOrdem(i++);
					
					if (questionario.isPossuiNota()){
								
						if(isEmpty(questionario.getUnidade()) )
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade");
						if(isEmpty(questionario.getAbreviacao())) 
							addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Abreviação");
						
						if (questionario.getId() == 0)
							AvaliacaoHelper.validarAvaliacao(questionario, turma(), erros, true);
						else
							AvaliacaoHelper.validarAvaliacao(questionario, turma(), erros, false);
						
						if (hasErrors())
							return null;
						
						ConfiguracoesAva config = getConfig();
						
						if (config == null || (config != null && config.getTipoMediaAvaliacoes(questionario.getUnidade()) == null) ){
							addMensagemWarning("Não é possível cadastrar uma avaliação porque o tipo de cálculo da média da avaliação não foi definido. "
									+ "Para defini-lo, realize a configuração da turma no formulário abaixo.");
							ConfiguracoesAvaMBean bean = getMBean("configuracoesAva");
							bean.setCadastroQuestionario(true);
							
							bean.configurar(turma());
							return CONFIGURAR_TURMA;
						} else {
							// Garante que há notas onde inserir as avaliações para este questionário.
							Turma t = turma();
							t.setDisciplina(getGenericDAO().refresh(t.getDisciplina()));
							
							ConsolidarTurmaMBean cBean = getMBean ("consolidarTurma");
							cBean.prepararTurma(t);
						}
					} else
						questionario.setNotaMaxima(null);
					
					MovimentoCadastroAva mov = new MovimentoCadastroAva ();
					
					mov.setCodMovimento(SigaaListaComando.SALVAR_QUESTIONARIO_TURMA);
					mov.setObjMovimentado(questionario);
					mov.setMensagem(questionario.getMensagemAtividade());
					
					execute(mov);
					
					registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.QUESTIONARIO, alterando ? AcaoAva.ALTERAR : AcaoAva.INSERIR, questionario.getId());
					
					modelPerguntas = new ListDataModel( questionario.getPerguntas() );
					
					// Caso o questionário estiver sendo finalizado a mensagem de sucesso será exibida depois.
					if (!finalizarQuestionario)
						addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
					else {
						tBean.getUltimasAtividades();
						
						// Registra atividade para a turma
						RegistroAtividadeAvaHelper.getInstance().registrarAtividade(questionario.getTurma(), questionario.getMensagemAtividade());
					}
						
					finalizarQuestionario = false;
					
					questionarios = null;
					
					return listarQuestionariosDocente();
				} catch (NegocioException e){
					addMensagens(e.getListaMensagens());
				} finally {
					if (dao != null)
						dao.close();
				}
			}
			
			finalizarQuestionario = false;
			
			if (questionario.getId() == 0) {
				manterDados = true;
				return novoQuestionario();
			}	
			return formularioQuestionario();
		}
				
		
		return null;
	}
	
	/**
	 * Retorna as configurações da turma.
	 * 
	 * @return
	 */
	public ConfiguracoesAva getConfig (){
		return getDAO(TurmaVirtualDao.class).findConfiguracoes(turma());
	}
	
	/**
	 * Remove um questionário.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String remover () {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
			
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			boolean voltarParaATurma = getParameterBoolean("voltar_para_a_turma");
			
			GenericDAO dao = null;

			try {
				dao = getGenericDAO();
				Turma turma = dao.findByPrimaryKey(tBean.getTurma().getId(), Turma.class);
				
				prepareMovimento(SigaaListaComando.REMOVER_QUESTIONARIO_TURMA);
			
				questionario = dao.findByPrimaryKey(getParameterInt("id"), QuestionarioTurma.class);
				registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.INICIAR_REMOCAO, questionario.getId());

				questionario.setMaterial(dao.findByExactField(MaterialTurma.class, "idMaterial", getParameterInt("id"), true));
				questionario.setAtivo(false);
				questionario.setTurma(turma);
				
				MovimentoCadastroAva mov = new MovimentoCadastroAva ();
				mov.setCodMovimento(SigaaListaComando.REMOVER_QUESTIONARIO_TURMA);
				mov.setObjMovimentado(questionario);
				
				boolean possuiAvaliacoes = isPossuiAvaliacoes();
				
				execute(mov);
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Questionário");
				aposRemocao();
				registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.REMOVER, questionario.getId());

				turma = tBean.getTurma();
				if (possuiAvaliacoes && !turma.isMedio()){
					turma.setDisciplina(getGenericDAO().findAndFetch(turma.getDisciplina().getId(),ComponenteCurricular.class,"programa"));
					ConsolidarTurmaMBean bean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
					bean.setTurma(turma);
					bean.prepararTurma(turma);
					bean.recarregarMatriculas();
					bean.salvarNotas(true);
				}
				questionarios = null;
				
				if (voltarParaATurma)
					return tBean.entrar();
				
				return listarQuestionariosDocente();
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			} catch (ArqException e) {
				tratamentoErroPadrao(e);
			} finally {
				if ( dao != null )
					dao.close();
			}
		}
		
		return null;
	}
	
	/**
	 * Remove um envio de respostas.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/visualizarRespostas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String removerRespostas () throws ArqException {
		int idRespostas = getParameterInt("idRespostas");
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			
			EnvioRespostasQuestionarioTurma respostas = dao.findByPrimaryKey(idRespostas, EnvioRespostasQuestionarioTurma.class);
			respostas.setAtivo(false);
			
			prepareMovimento(SigaaListaComando.REMOVER_RESPOSTAS_QUESTIONARIO_TURMA);
			
			MovimentoCadastro mov = new MovimentoCadastro ();
			mov.setObjMovimentado(respostas);
			mov.setCodMovimento(SigaaListaComando.REMOVER_RESPOSTAS_QUESTIONARIO_TURMA);
			
			execute(mov);
			registrarAcao(respostas.getQuestionario().getTitulo() + " - " + respostas.getUsuarioEnvio().getPessoa().getNome(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.REMOVER_RESPOSTA, respostas.getQuestionario().getId(), respostas.getId());
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Resposta do discente");
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return visualizarRespostas();
	}
	
	/**
	 * Finaliza e publica o questionário na turma virtual.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String finalizarQuestionario () throws ArqException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		//Caso o questionario esteja sendo finalizado pela primeira vez permitir queno email seja enviado.  
		boolean enviarMail = true;
		if (questionario.isFinalizado())
			enviarMail = false;
			
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			
			if (isEmpty(questionario.getPerguntas()))
				addMensagemErro("Prezado(a) docente, o questionário deve ter pelo menos uma questão para poder ser finalizado.");
			
			if (!hasErrors())
				try {
					questionario.setFinalizado(true);
					
					prepareMovimento(SigaaListaComando.SALVAR_QUESTIONARIO_TURMA);
					finalizarQuestionario = true;
					if (CONFIGURAR_TURMA.equals(salvarQuestionario()))
						return null;
					
					if (!hasErrors()){
					
						prepareMovimento(SigaaListaComando.SALVAR_ORDEM_DAS_QUESTOES);
						MovimentoCadastro mov = new MovimentoCadastro();
						mov.setCodMovimento(SigaaListaComando.SALVAR_ORDEM_DAS_QUESTOES);
						mov.setObjMovimentado(questionario);
						
						execute(mov);
						addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
						
						// Se o docente optou por avisar os discentes sobre a finalização do questionário e seja a primeira vez que o questionario e finalizado.
						if (notificarAlunos && enviarMail ){
							String turma = turma().getDescricaoSemDocente();
							String conteudo = "O questionário \"" + questionario.getTitulo() + "\" foi cadastrado na turma <b>" + turma + "</b> do SIGAA"; 
							String assunto = "Novo Questionário Cadastrado na turma " + turma;
							
							List <String> stringIdTurma = new ArrayList <String> ();
							stringIdTurma.add("" + turma().getId());
							
							notificarTurmas(stringIdTurma, assunto , conteudo, ControllerTurmaVirtual.DISCENTE, ControllerTurmaVirtual.DOCENTE, ControllerTurmaVirtual.AUTORIZADO, ControllerTurmaVirtual.DOCENCIA_ASSISTIDA );
						}
						
						return paginaListagem();
					}
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
				}
		}
		
		return null;
	}
	
	/**
	 * Salva o questionário fazendo com que o mesmo não fique visível aos discentes.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String salvarQuestionarioSemFinalizar () throws ArqException {
		questionario.setFinalizado(false);
		
		prepareMovimento(SigaaListaComando.SALVAR_QUESTIONARIO_TURMA);
		
		return salvarQuestionario();
	}
	
	/**
	 * Exibe a tela de adição de pergunta já marcando a categoria passada por parâmetro<br/>
	 * Método não invocado por JSPs. É public porque é chamado pelo método cadastrar da classe CategoriaPerguntaQuestionarioTurmaMBean.
	 * 
	 * @param idCategoria
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarAdicionarPergunta (int idCategoria) throws DAOException{
		this.idCategoria = idCategoria;
		
		if (this.idCategoria == 0)
			this.idCategoria = -1;
		
		return iniciarAdicionarPergunta();
	}
	
	/**
	 * Vai para a tela de adição de pergunta.<br/><br/>
 	 * Método chamado pela(s) seguinte(s) JSP(s):<br/>
	 * <ul>
	 * 		<li>sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarAdicionarPergunta() throws DAOException{
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			
			if (idCategoria == 0)
				adicionarEmCategoria = getParameterBoolean("adicionar_em_categoria");
			
			voltarAoQuestionario = !adicionarEmCategoria;
			
			if (idCategoria == 0){
				pergunta = new PerguntaQuestionarioTurma();
				pergunta.setCategoria(new CategoriaPerguntaQuestionarioTurma());
				pergunta.setAlternativas( new ArrayList<AlternativaPerguntaQuestionarioTurma>() );
				
				// Se só existe uma categoria, adiciona à pergunta.
				CategoriaPerguntaQuestionarioTurmaMBean cMBean = getMBean ("categoriaPerguntaQuestionarioTurma");
				Collection<CategoriaPerguntaQuestionarioTurma> cs = cMBean.getAll();
				if (cs.size() == 1)
					for (CategoriaPerguntaQuestionarioTurma c : cs)
						pergunta.getCategoria().setId(c.getId());
			}
			
			if (idCategoria > 0){
				
				if (!voltarAoQuestionario)
					adicionarEmCategoria = true;
				
				pergunta.setCategoria(new CategoriaPerguntaQuestionarioTurma(idCategoria));
				
				idCategoria = 0;
			}
			
			modelAlternativas = new ListDataModel( pergunta.getAlternativas() );
			return formularioPergunta();
		}
		
		return null;
	}
	
	/**
	 * Adiciona todas as perguntas selecionadas no banco de questões.<br/><br/>
 	 * Método chamado pela(s) seguinte(s) JSP(s):<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarCategorias.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String adicionarPerguntasDoBanco () throws ArqException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			prepareMovimento (SigaaListaComando.ADICIONAR_PERGUNTAS_DO_BANCO);
			
			List <PerguntaQuestionarioTurma> perguntas = new ArrayList <PerguntaQuestionarioTurma> ();
			
			CategoriaPerguntaQuestionarioTurmaMBean cBean = getMBean("categoriaPerguntaQuestionarioTurma");
			
			List <CategoriaPerguntaQuestionarioTurma> categorias = new ArrayList<CategoriaPerguntaQuestionarioTurma>();
			categorias.addAll(cBean.getAll());
			categorias.addAll(cBean.getCategoriasCompartilhadas());
			
			for (CategoriaPerguntaQuestionarioTurma c : categorias)
				if (c.getPerguntas() != null)
					for (PerguntaQuestionarioTurma p : c.getPerguntas())
						if (p.isSelecionada())
							perguntas.add(p);
			
			if (perguntas.isEmpty()){
				addMensagemErro("Selecione pelo menos uma pergunta");
				return null;
			}
				
			GenericDAO dao = null;
			
			try {
				dao = getGenericDAO();
				
				questionario.setPerguntas((List<PerguntaQuestionarioTurma>) dao.findByExactField(PerguntaQuestionarioTurma.class, new String [] {"questionarioTurma.id", "ativo"}, new Object [] {questionario.getId(), true}));
				
				MovimentoAdicionarPerguntasDoBanco mov = new MovimentoAdicionarPerguntasDoBanco (questionario, perguntas);
				
				try {
					execute (mov);
					
					prepareMovimento(SigaaListaComando.SALVAR_QUESTIONARIO_TURMA);
					
					// Indica que o próximo método não deve tentar atualizar o questionário
					naoSalvar = true;
					return gerenciarPerguntasDoQuestionario();
				} catch (NegocioException e){
					addMensagens (e.getListaMensagens());
				}
				
				
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return null;
	}
	
	/**
	 * Adiciona/altera uma pergunta ao/do questionário.<br/><br/>
 	 * Método chamado pela(s) seguinte(s) JSP(s):<br/>
	 * <ul>
	 * 		<li> sigaa.war/geral/questionario/pergunta.jsp,</li>
	 * 		<li>/sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws IOException 
	 * @throws  
	 */
	public String adicionarPergunta() throws ArqException, IOException{
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			
			if (adicionarEmCategoria)
				if (pergunta.getCategoria().getId() <= 0)
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria");
			
			ListaMensagens erros = pergunta.validate();
			if( !erros.isEmpty() ){
				addMensagens(erros);
				return null;
			}
							
			if (pergunta.getCategoria().getId() > 0)
				pergunta.setDono(getUsuarioLogado());
	
			// Se for de múltipla ou única escolha deve setar o gabarito
			if( pergunta.isMultiplaOuUnicaEscolha() ){
				if( (pergunta.isUnicaEscolha()) && !isEmpty(gabaritoUnicaEscolha) )
					for( AlternativaPerguntaQuestionarioTurma a : pergunta.getAlternativas() )
						if( gabaritoUnicaEscolha.equals( a.getOrdem() ) )
								a.setGabarito(true);
						else
							a.setGabarito(false);
			}
	
			if( !pergunta.hasGabarito() && !pergunta.isDissertativa() ){
				if (pergunta.isMultiplaOuUnicaEscolha())
					addMensagemErro("Informe o gabarito da pergunta selecionando uma ou mais alternativas.");
				else
					addMensagemErro("Informe o gabarito da pergunta.");
				return null;
			}
	
			if ( pergunta.isDissertativa() && (pergunta.isLimitarCaracteres() || pergunta.islimitarPalavras() )  ) {
				if ( pergunta.getValorLimitadorDissertativa() == null ) {
					addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Número máximo de caracteres");
					return null;
				}
				if ( pergunta.getValorLimitadorDissertativa() != null && pergunta.getValorLimitadorDissertativa() <= 0 ) {
					addMensagem(MensagensArquitetura.VALOR_MAIOR_ZERO, "Número máximo de caracteres");
					return null;
				}

				if ( pergunta.getGabaritoDissertativa() != null && pergunta.getValorLimitadorDissertativa() != null && pergunta.isLimitarCaracteres() && (pergunta.getValorLimitadorDissertativa() < pergunta.getGabaritoDissertativa().length())) {
					addMensagemErro("Número máximo de caracteres permitidos na resposta é inferior ao número de caracteres do gabarito.");
					return null;
				}
				
				if (pergunta.getGabaritoDissertativa() != null && pergunta.getValorLimitadorDissertativa() != null && pergunta.islimitarPalavras()) {
					// Remove espaços duplicados
					pergunta.setGabaritoDissertativa(pergunta.getGabaritoDissertativa().trim().replaceAll("  ", " "));

					String gabarito = pergunta.getGabaritoDissertativa().trim().replaceAll(",", " ");
					gabarito = gabarito.trim().replaceAll("  ", " ");
					String palavras[] = gabarito.split(" |\n|, |: |; |\n|,|:|;");
					if (palavras.length > pergunta.getValorLimitadorDissertativa()) {
						addMensagemErro("Número máximo de palavras permitidas na resposta é inferior ao número de palavras do gabarito.");
					}
				}

			}
					
			pergunta.atualizarOrdemAlternativas();
			
			if (!adicionarEmCategoria){
				if( acaoAlterarPergunta ){ // se esta alterando a pergunta
					questionario.getPerguntas().remove( pergunta.getOrdem() - 1 );
					questionario.getPerguntas().add(pergunta.getOrdem() - 1, pergunta);
				} else {
					pergunta.setQuestionario(questionario);
					questionario.getPerguntas().add( pergunta );
				}
				questionario.atualizarOrdemPerguntas();
			}
					
			adicionarLatex();

			prepare(SigaaListaComando.SALVAR_PERGUNTA_QUESTIONARIO_TURMA);
			MovimentoSalvarPerguntaQuestionarioTurma mov = new MovimentoSalvarPerguntaQuestionarioTurma(pergunta, alternativasRemover, adicionarEmCategoria);
					
			if (!hasErrors())
				try {
				
					execute(mov);
					
					voltarAoQuestionario = false;
					
					int tipoAtual = pergunta.getTipo();
					pergunta = new PerguntaQuestionarioTurma();
					pergunta.setTipo(tipoAtual);
					alternativa = new AlternativaPerguntaQuestionarioTurma();
					pergunta.setAlternativas( new ArrayList<AlternativaPerguntaQuestionarioTurma>() );
					alternativasRemover = null;
		
					acaoAlterarPergunta = false;
					
					addMensagemInformation("Pergunta salva com sucesso!");
					
					if (adicionarEmCategoria){
						CategoriaPerguntaQuestionarioTurmaMBean cBean = getMBean ("categoriaPerguntaQuestionarioTurma");
						cBean.setAll(null);
						cBean.setCategoriasCompartilhadas(null);
						return iniciarAdicionarPergunta(pergunta.getCategoria().getId());
					}
	
					modelAlternativas = new ListDataModel( pergunta.getAlternativas() ) ;
					modelPerguntas = new ListDataModel( questionario.getPerguntas() );
					
					return formularioPerguntasDoQuestionario();
					
				} catch (NegocioException e) {
					addMensagens(e.getListaMensagens());
				}
		}
		
		return null;
	}

	/**
	 * Adiciona fórmula latex a pergunta
	 * Método não invocado por JSP's 
	 * @return
	 * @throws IOException 
	 */
	private void adicionarLatex() throws IOException {
		if ( LatexUtil.possuiFormulaLatex(pergunta.getPergunta())){
			pergunta.setPerguntaFormula(pergunta.getPergunta());
			pergunta.setPergunta(LatexUtil.createStringImqLatex(pergunta.getPergunta(),erros));
		}
		
		if ( pergunta.getPerguntaFormula() != null || LatexUtil.possuiFormulaLatex(pergunta.getPerguntaFormula())){
			pergunta.setPergunta(LatexUtil.createStringImqLatex(pergunta.getPerguntaFormula(),erros));
		}
	}
	
	/**
	 * Remove uma pergunta do questionário<br/><br/>
 	 * Método chamado pela(s) seguinte(s) JSP(s):<br/>
	 * <ul>
	 * 		<li>/sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public void removerPergunta (ActionEvent evt) throws ArqException{
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			
			if ( questionario.getPerguntas().size() == 1 && questionario.isFinalizado()){
				mensagemErroPergunta ="É nescessário informar pelo menos uma pergunta para este questionário.";
				return;
			}
				
			
			//índice da alternativa que sera removida
			int indice = modelPerguntas.getRowIndex();
			PerguntaQuestionarioTurma perguntaRemovida = questionario.getPerguntas().remove(indice);
			modelPerguntas = new ListDataModel( questionario.getPerguntas() );
			
			GenericDAO dao = null;
			
			try {
				dao = getGenericDAO();
				dao.detach(perguntaRemovida);
			
				prepareMovimento(SigaaListaComando.REMOVER_PERGUNTA_QUESTIONARIO_TURMA);
				MovimentoCadastro mov = new MovimentoCadastro ();
				mov.setCodMovimento(SigaaListaComando.REMOVER_PERGUNTA_QUESTIONARIO_TURMA);
				mov.setObjMovimentado(perguntaRemovida);
			
				execute(mov);
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}

	/**
	 * Altera uma pergunta do questionário
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String alterarPergunta() throws DAOException{
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			adicionarEmCategoria = getParameterBoolean("adicionar_em_categoria");
			voltarAoQuestionario = !adicionarEmCategoria;
			alternativasRemover = null;
			
			int idPergunta = getParameterInt("id", 0);
			
			if (idPergunta > 0) {
				GenericDAO dao = null;
				
				try {
					dao = getGenericDAO();
					pergunta = dao.findByPrimaryKey(idPergunta, PerguntaQuestionarioTurma.class);
				} finally {
					if (dao != null)
						dao.close();
				}
				
			} else
				pergunta = (PerguntaQuestionarioTurma) modelPerguntas.getRowData();
			
			if (pergunta.getCategoria() == null)
				pergunta.setCategoria(new CategoriaPerguntaQuestionarioTurma());
			
			modelAlternativas = new ListDataModel( pergunta.getAlternativas() );
			
			if (pergunta.isUnicaEscolha())
				for (AlternativaPerguntaQuestionarioTurma a : pergunta.getAlternativas())
					if (a.isGabarito())
						gabaritoUnicaEscolha = a.getOrdem();
			
			if (pergunta.getValorLimitadorDissertativa() == null)
				pergunta.setValorLimitadorDissertativa(new Integer(0));
			
			acaoAlterarPergunta = true;
			return editarPergunta();
		}
		
		return null;
	}
	
	/**
	 * Incrementa a ordem de uma pergunta, o atributo ordem define a ordem de exibição das perguntas do formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public void movePerguntaCima(ActionEvent evt) {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			//índice da alternativa que sera movida
			int indice = modelPerguntas.getRowIndex();
	
			if( indice != 0 ){
				PerguntaQuestionarioTurma perg = questionario.getPerguntas().get(indice);
				questionario.getPerguntas().remove(indice);
				questionario.getPerguntas().add(indice - 1, perg);
				modelPerguntas = new ListDataModel( questionario.getPerguntas() );
			}
		}
	}

	/**
	 * Decrementa a ordem de uma pergunta, o atributo ordem define a ordem de exibição das perguntas do formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/questionario.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public void movePerguntaBaixo(ActionEvent evt) {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			//índice da alternativa que sera movida
			int indice = modelPerguntas.getRowIndex();
	
			if( indice < questionario.getPerguntas().size() -1 ){
				PerguntaQuestionarioTurma perg = questionario.getPerguntas().get(indice);
				questionario.getPerguntas().remove(indice);
				questionario.getPerguntas().add(indice + 1, perg);
				modelPerguntas = new ListDataModel( questionario.getPerguntas() );
			}
		}
	}
	
	/**
	 * Adiciona uma alternativa da pergunta ao questionário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/pergunta.jsp</li>
	 * <ul>
	 * 
	 * @return
	 */
	public void adicionarAlternativa(ActionEvent evt){
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			if (isEmpty(alternativa.getAlternativa())){
				getCurrentRequest().setAttribute("errosQuestionario", "Atenção: é obrigatório informar o texto da alternativa.");
				return;
			}
			
			// Evitando alternativas repetidas na pergunta.
			for (AlternativaPerguntaQuestionarioTurma alternativaJaCadastrada : pergunta.getAlternativas()) {
				if (alternativaJaCadastrada.getAlternativa() != null && 
						alternativa.getAlternativa().trim().equalsIgnoreCase(alternativaJaCadastrada.getAlternativa().trim())) {
					getCurrentRequest().setAttribute("errosQuestionario", "Atenção: esta alternativa já foi cadastrada para esta pergunta.");
					return;
				}	
			}
			
			alternativa.setPergunta(pergunta);
			pergunta.getAlternativas().add(alternativa);
			pergunta.atualizarOrdemAlternativas();
			alternativa = new AlternativaPerguntaQuestionarioTurma();
		}
	}
	
	/**
	 * Remove uma alternativa da pergunta do formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public void removerAlternativa(ActionEvent evt){
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			//índice da alternativa que sera removida
			int indice = modelAlternativas.getRowIndex();
			AlternativaPerguntaQuestionarioTurma alternativaRemovida = pergunta.getAlternativas().remove(indice);
			if ( alternativaRemovida.getId() > 0 ){
				if( alternativasRemover == null )
					alternativasRemover = new ArrayList<AlternativaPerguntaQuestionarioTurma> ();
				alternativasRemover.add(alternativaRemovida);
			}	
		}
	}

	/**
	 * Move a alternativa para cima na ordem de exibição no formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s): 
	 * <ul>
	 * <li>JSP: /sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public void moveAlternativaCima(ActionEvent evt) {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			//índice da alternativa que sera movida
			int indice = modelAlternativas.getRowIndex();
	
			if( indice != 0 ){
				AlternativaPerguntaQuestionarioTurma alter = pergunta.getAlternativas().get(indice);
				pergunta.getAlternativas().remove(indice);
				pergunta.getAlternativas().add(indice - 1, alter);
				pergunta.atualizarOrdemAlternativas();
				modelAlternativas = new ListDataModel( pergunta.getAlternativas() );
			}
		}
	}

	/**
	 * Move a alternativa para baixo na ordem de exibição no formulário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li> JSP: /sigaa.ear/sigaa.war/geral/questionario/pergunta.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 */
	public void moveAlternativaBaixo(ActionEvent evt) {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			//índice da alternativa que será movida
			int indice = modelAlternativas.getRowIndex();
	
			if( indice < pergunta.getAlternativas().size() - 1 ){
				AlternativaPerguntaQuestionarioTurma alter = pergunta.getAlternativas().get(indice);
				pergunta.getAlternativas().remove(indice);
				pergunta.getAlternativas().add(indice + 1, alter);
				pergunta.atualizarOrdemAlternativas();
				modelAlternativas = new ListDataModel( pergunta.getAlternativas() );
			}
		}
	}
	
	/**
	 * Analisa se o discente pode responder o questionário e exibe a página para começar a responder.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listar.jsp</li>
	 * 		<li>/ava/topico_aula.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciarResponderQuestionario () throws DAOException {
		
		Integer id = getParameterInt("id");
		
		QuestionarioTurmaDao qtDAO = null;

		try {
			qtDAO = getDAO(QuestionarioTurmaDao.class);
			
			questionario = qtDAO.findByPrimaryKey(id, QuestionarioTurma.class);
			questionario.getFeedbacks().iterator();
			
			Usuario u = getUsuarioLogado();
			
			List <EnvioRespostasQuestionarioTurma> respostas = qtDAO.findRespostasByQuestionarioAluno(questionario, u, false, null);
			
			resposta = null;
			for (EnvioRespostasQuestionarioTurma r : respostas)
				// Se ainda há tempo para continuar a responder com esta tentativa, permite.
				if (r.getDataCadastro().getTime() + questionario.getDuracao() * 60 * 1000 > new Date().getTime())
					resposta = r;
			
			// Se o usuário ainda não enviou respostas para este questionário, inicia uma nova resposta.
			if (resposta == null)
				resposta = new EnvioRespostasQuestionarioTurma();
			else {
				// Se sim, inicializa as respostas.
				resposta.getRespostas().iterator();
				
				for (RespostaPerguntaQuestionarioTurma r : resposta.getRespostas())
					r.getAlternativasEscolhidas().iterator();
			}
			
			permiteNovaTentativa = qtDAO.permiteNovaTentativa (u, questionario);
			
			tentativas = qtDAO.findNumeroTentativasUsuario(questionario, u);
			
			if (!permiteNovaTentativa) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm");
				
				Calendar calInicio = Calendar.getInstance();
				calInicio.setTime(questionario.getInicio());
				calInicio.add(Calendar.HOUR, questionario.getHoraInicio());
				calInicio.add(Calendar.MINUTE, questionario.getMinutoInicio());
				
				Calendar calFim = Calendar.getInstance();
				calFim.setTime(questionario.getFim());
				calFim.add(Calendar.HOUR, questionario.getHoraFim());
				calFim.add(Calendar.MINUTE, questionario.getMinutoFim());
				
				// Se não permite uma nova tentativa para o usuário, exibe a causa.
				if (new Date().before(calInicio.getTime())){
					addMensagemWarning("O período para responder este questionário inicia no dia " + sdf.format(calInicio.getTime()));
				} else if (new Date().after(calFim.getTime())){
					addMensagemWarning("O período para responder este questionário acabou no dia " + sdf.format(calFim.getTime()));
				} else
					addMensagemWarning("Você já enviou o limite de tentativas para este questionário.");
			}
	
			return forward("/ava/QuestionarioTurma/visualizarQuestionario.jsp");
		} finally {
			if (qtDAO != null)
				qtDAO.close();
		}
	}
	
	/**
	 * Remove um conjunto de envios de respostas.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/visualizarRespostas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String removerConjuntoRespostas () throws ArqException {
		int idConjunto = getParameterInt("idConjunto");

		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			
			ConjuntoRespostasQuestionarioAluno conjunto = dao.findAndFetch(idConjunto, ConjuntoRespostasQuestionarioAluno.class, "respostas");
			conjunto.setAtivo(false);
			
			for ( EnvioRespostasQuestionarioTurma e : conjunto.getRespostas() ) 
				e.setAtivo(false);
			
			prepareMovimento(SigaaListaComando.REMOVER_CONJUNTO_RESPOSTAS_QUESTIONARIO_TURMA);
			
			MovimentoCadastro mov = new MovimentoCadastro ();
			mov.setObjMovimentado(conjunto);
			mov.setCodMovimento(SigaaListaComando.REMOVER_CONJUNTO_RESPOSTAS_QUESTIONARIO_TURMA);
			
			execute(mov);
			
			registrarAcao(conjunto.getQuestionario().getTitulo() + " - " + conjunto.getUsuarioEnvio().getPessoa().getNome(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.REMOVER_RESPOSTA, conjunto.getQuestionario().getId(), conjunto.getId());
			addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Respostas do discente");
			
			return visualizarRespostas();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}
	
	/**
	 * Exibe o questionário a ser respondido.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/topico_aula.jsp</li>
	 * 		<li>/ava/QuestionarioTurma/listarDiscente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String responderQuestionario () throws ArqException {
		
		QuestionarioTurmaDao qtDAO = null;
		PerguntaQuestionarioTurmaDao pDAO = null;
	
		if ( questionario == null ){
			addMensagemErro("Por favor, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return redirectJSF(getSubSistema().getLink());
		}
		
		if ( getDiscenteUsuario() != null )
			registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.ACESSAR, getDiscenteUsuario().getId());
		else
			registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.ACESSAR, getUsuarioLogado().getId());
		
		try {
						
			qtDAO = getDAO(QuestionarioTurmaDao.class);
			pDAO = getDAO(PerguntaQuestionarioTurmaDao.class);
			
			List <EnvioRespostasQuestionarioTurma> respostas = qtDAO.findRespostasByQuestionarioAluno(questionario, getUsuarioLogado(), false, null);
			
			resposta = null;
			for (EnvioRespostasQuestionarioTurma r : respostas)
				// Se ainda há tempo para continuar a responder com esta tentativa, permite.
				if (r.getDataCadastro().getTime() + questionario.getDuracao() * 60 * 1000 > new Date().getTime())
					resposta = r;
			
			// Se o usuário ainda não enviou respostas para este questionário, inicia uma nova resposta.
			if (resposta == null)
				resposta = new EnvioRespostasQuestionarioTurma();
			else {
				// Se sim, inicializa as respostas.
				resposta.getRespostas().iterator();
				
				for (RespostaPerguntaQuestionarioTurma r : resposta.getRespostas())
					r.getAlternativasEscolhidas().iterator();
			}
			
			if (!questionario.isDentroPeriodoEntrega()){
				addMensagemErro("Este questionario só estará disponível no período de "+CalendarUtils.format(questionario.getInicio(), getPadraoData())+" às "+questionario.getHoraInicio()+"h"+questionario.getMinutoInicio()+" até "+CalendarUtils.format(questionario.getFim(), getPadraoData())+" às "+questionario.getHoraFim()+"h"+questionario.getMinutoFim()+".");
				return null;
			}
			
			// Se não permite novo envio,
			permiteNovaTentativa = qtDAO.permiteNovaTentativa (getUsuarioLogado(), questionario);
			if (!permiteNovaTentativa){
				addMensagemErro("Você já enviou o limite de tentativas para este questionário.");
				return null;
			}
			
			cadastrarRespostas();
			
			setOperacaoAtiva(SigaaListaComando.ALTERAR_RESPOSTAS_QUESTIONARIO_TURMA.getId());
			prepareMovimento(SigaaListaComando.ALTERAR_RESPOSTAS_QUESTIONARIO_TURMA);
						
			return forward("/ava/QuestionarioTurma/formResponderQuestionario.jsp");
			
		} finally {
			if (qtDAO != null)
				qtDAO.close();
			
			if (pDAO != null)
				pDAO.close();
		}
	}

	/**
	 * Cadastra as resposta de um questionário.<br/><br/>
	 * Método não invocados por JSPs:<br/>
	 * 
	 * @return
	 * @throws DAOException
	 */
	private void cadastrarRespostas()throws DAOException, ArqException {
		QuestionarioTurmaDao qtDAO = null;
		
		try {
			
			qtDAO = getDAO(QuestionarioTurmaDao.class);
			popularRespostas();
			
			conjunto = qtDAO.findConjuntoRespostasByQuestionarioUsuario(questionario,getUsuarioLogado());
			// Se o usuário ainda não enviou uma resposta para o questionário persiste um novo conjunto de respostas.
			if ( conjunto == null ) {
				
				conjunto = new ConjuntoRespostasQuestionarioAluno();
				conjunto.setQuestionario(questionario);
				conjunto.setUsuarioEnvio(getUsuarioLogado());
				conjunto.setRespostas(new ArrayList<EnvioRespostasQuestionarioTurma>());
				
				// Zera as alternativas das respostas para permitir salvar
				for (RespostaPerguntaQuestionarioTurma r : resposta.getRespostas()){
					r.setAlternativaEscolhida(null);
					r.setAlternativasEscolhidas(null);
				}
				
				prepareMovimento(ArqListaComando.CADASTRAR);
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setCodMovimento(ArqListaComando.CADASTRAR);
				
				conjunto.getRespostas().add(resposta);
				resposta.setConjuntoRespostas(conjunto);
				resposta.setRegistroCadastro(getUsuarioLogado().getRegistroEntrada());
				resposta.setDataCadastro(new Date());

				mov.setObjMovimentado(conjunto);
			
				execute(mov);
			} // Se for uma nova tentativa, salva no banco para permitir continuar mais tarde.
			else if (resposta.getId() == 0){
	
					resposta.setConjuntoRespostas(conjunto);
					
					prepareMovimento(ArqListaComando.CADASTRAR);
					
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setObjMovimentado(resposta);
					mov.setCodMovimento(ArqListaComando.CADASTRAR);
					
					// Zera as alternativas das respostas para permitir salvar
					for (RespostaPerguntaQuestionarioTurma r : resposta.getRespostas()){
						r.setAlternativaEscolhida(null);
						r.setAlternativasEscolhidas(null);
					}
						
					execute(mov);
					
			}
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		} finally {
			if ( qtDAO != null )
				qtDAO.close();
		}
	}
	
	
	
	// Métodos referentes a operação de responder o questionário pelo discente.
	
	
	
	/**
	 * Método salvar relativo ao a4j:poll da gerencia de plano de cursos. Chama o método salvar ignorando as validações.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ava/PlanoCurso/form.jsp</li>
	 * </ul>
	 * 
	 * @see #salvar()
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public String submeterRespostasPoll () throws SegurancaException, ArqException {
		prepareMovimento(SigaaListaComando.ALTERAR_RESPOSTAS_QUESTIONARIO_TURMA);
		naoValidarRespostas = true;
		return submeterRespostas();
	}
		
	
	/**
	 * Inicia o cadastro de um novo questionário.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/visualizarQuestionario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterRespostas () throws ArqException {
		
		if ( questionario == null ){
			addMensagemErro("Por favor, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return redirectJSF(getSubSistema().getLink());
		}
		
		// Se estiver salvando com o poll
		if (naoValidarRespostas) {
			// Verifica se o questionário já não foi finalizado para não ser sobrescrito;
			EnvioRespostasQuestionarioTurma respostaAux = getGenericDAO().findByPrimaryKey(resposta.getId(),EnvioRespostasQuestionarioTurma.class);
			if (respostaAux.isFinalizado()){
				addMensagemErro("A resposta para este questionário já foi enviada.");
				return redirectJSF(getSubSistema().getLink());
			}
			getGenericDAO().detach(respostaAux);
		}		
		
		if(!naoValidarRespostas && !checkOperacaoAtiva(SigaaListaComando.ALTERAR_RESPOSTAS_QUESTIONARIO_TURMA.getId()))
			return cancelar();	
		
		if (!naoValidarRespostas)
			registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.INICIAR_RESPOSTA, questionario.getId(), resposta.getId());

		// Se nao estiver salvando com o poll
		if (!naoValidarRespostas){
			// Verifica se está dentro do prazo de entrega.
			if (!questionario.isDentroPeriodoEntrega()){
				addMensagemErro("Este questionario só estará disponível no período de "+CalendarUtils.format(questionario.getInicio(), getPadraoData())+" às "+questionario.getHoraInicio()+"h"+questionario.getMinutoInicio()+" até "+CalendarUtils.format(questionario.getFim(), getPadraoData())+" às "+questionario.getHoraFim()+"h"+questionario.getMinutoFim()+".");
				return null;
			}
			
			// Verifica se o tempo para o envio da resposta não etá expirado.
			if (resposta.getDataCadastro().getTime() + questionario.getDuracao() * 60 * 1000 < new Date().getTime())
				addMensagemErro("O tempo para enviar esta resposta está expirado.");
			
			// Verifica se todas as perguntas foram respondidas.
			ListaMensagens lm = resposta.validate();
			if (lm != null && !lm.isEmpty()) { 
				addMensagens(lm);
				return null;
			}
		}
		
		if (!hasErrors()){
			try {
				MovimentoCadastro mov = new MovimentoCadastro ();
				
				if (!naoValidarRespostas)
					resposta.setFinalizado(true);
				mov.setObjMovimentado(resposta);
				mov.setCodMovimento(SigaaListaComando.ALTERAR_RESPOSTAS_QUESTIONARIO_TURMA);
				
				// Calcula a nota do aluno
				resposta.calcularNota();

				if (!naoValidarRespostas)
					resposta.setDataFinalizacao(new Date());
				
				execute(mov);
				
				if (!naoValidarRespostas) {
					
					registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.QUESTIONARIO, AcaoAva.RESPONDER, questionario.getId(), resposta.getId());
					removeOperacaoAtiva();
					addMensagemInformation("Suas respostas foram enviadas com sucesso!");
					
					return visualizarResultado();
				}
				
				return null;
				
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			} finally {
				naoValidarRespostas = false;
			}
		}
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		return tBean.entrar();
	}
	
	/**
	 * Exibe as respostas enviadas para o questionário selecionado.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String visualizarRespostas () throws DAOException {
		
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
		
			Integer idQuestionario = getParameterInt("id", 0);
			QuestionarioTurmaDao qtDAO = null;
			
			try {
				qtDAO = getDAO(QuestionarioTurmaDao.class);
				
				if (idQuestionario > 0)
					questionario = qtDAO.findByPrimaryKey(idQuestionario, QuestionarioTurma.class);
				
				conjuntoRespostas = qtDAO.findConjuntoRespostasByQuestionario(questionario);
				List<ConjuntoRespostasQuestionarioAluno> conjuntosARemover = new ArrayList<ConjuntoRespostasQuestionarioAluno>();
				
				for ( ConjuntoRespostasQuestionarioAluno c : conjuntoRespostas ) {
					
					List <EnvioRespostasQuestionarioTurma> todas = c.getRespostas();
					c.setRespostas(new ArrayList <EnvioRespostasQuestionarioTurma> ());
	
					boolean possuiFinalizadas = false;
					// Adiciona as respostas finalizadas.
					for (EnvioRespostasQuestionarioTurma e : todas){
						if ( e.isFinalizado()  ){
								c.getRespostas().add(e);
								possuiFinalizadas = true;
						}		
					}
					// Adiciona a última resposta não finalizada de cada usuário.
					// Deve ser a última, para não pegar respostas salvas devido o pool.
					if ( !possuiFinalizadas ){
						EnvioRespostasQuestionarioTurma ultima = null;
						
						for (EnvioRespostasQuestionarioTurma e : todas){
							// Verifica se o questionário ainda está sendo respondido					
							if ( !e.isFinalizado() && new Date().getTime() > e.getDataCadastro().getTime() + questionario.getDuracao() * 60 * 1000 ) {
								// Caso a duração do questionário tenha acabado, pega o último questionário
								if (ultima == null || (e.getDataCadastro().after(ultima.getDataCadastro())))
										ultima = e;
							}
						}
						
						if ( ultima != null )
							c.getRespostas().add(ultima);					
					}
					
					if (c.getRespostas().isEmpty())
						conjuntosARemover.add(c);
				}
				
				if ( !conjuntosARemover.isEmpty() )
					for ( ConjuntoRespostasQuestionarioAluno r : conjuntosARemover )
						conjuntoRespostas.remove(r);
						
				Collections.sort(conjuntoRespostas, new Comparator<ConjuntoRespostasQuestionarioAluno>(){
					public int compare(ConjuntoRespostasQuestionarioAluno c1, ConjuntoRespostasQuestionarioAluno c2) {
						return c1.getUsuarioEnvio().getNome().compareToIgnoreCase(c2.getUsuarioEnvio().getNome());
					}
				});
				
				questionario.setConjuntos(conjuntoRespostas);
			} finally {
				if (qtDAO != null)
					qtDAO.close();
			}
			
			return forward("/ava/QuestionarioTurma/visualizarRespostas.jsp");
		}
		
		return null;
	}
	
	/**
	 * Exibe o resultado da resposta do discente.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarDiscente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String visualizarResultado () throws DAOException {
		Integer id = getParameterInt("id", 0);
		QuestionarioTurmaDao qtDAO = null;

		try {
			
			if (questionario.isFimExibicao()){
				addMensagemErro("Acabou o prazo para visualizar as respostas.");
				return null;
			}
			
			qtDAO = getDAO(QuestionarioTurmaDao.class);
			
			conjunto = qtDAO.refresh(conjunto);
			if ( conjunto != null && conjunto.getRespostas() != null && !conjunto.getRespostas().isEmpty() ) {
			
				if (id > 0){
					for ( EnvioRespostasQuestionarioTurma r : conjunto.getRespostas() )
						if ( r.getId() == id ){
							resposta = r;
							break;
						}	
				} 			
	
				if (resposta != null){
					resposta = qtDAO.refresh(resposta);
					respostasModel = new ListDataModel(resposta.getRespostas());
					
					for (RespostaPerguntaQuestionarioTurma r : resposta.getRespostas()){
						for (AlternativaPerguntaQuestionarioTurma a : r.getPergunta().getAlternativasValidas())
							for (AlternativaPerguntaQuestionarioTurma ae : r.getAlternativasEscolhidas())
								if (ae.getId() == a.getId()){
									a.setSelecionado(true);
									break;
								}
					}
				
					return forward ("/ava/QuestionarioTurma/visualizarResultado.jsp");
				}
			}
			
			addMensagemErro("Você ainda não enviou respostas para este questionário");
			
			return null;
			
		} finally {
			if (qtDAO != null)
				qtDAO.close();
		}
	}
	
	/**
	 * Exibe as tentativas de resposta do discente.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listarDiscente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String visualizarTentativas () throws DAOException {
		Integer id = getParameterInt("id", 0);
		QuestionarioTurmaDao qtDAO = null;
		
		try {
			qtDAO = getDAO(QuestionarioTurmaDao.class);
			
			if (id > 0){
				questionario = qtDAO.findByPrimaryKey(id, QuestionarioTurma.class);
				questionario.getFeedbacks().iterator();
			}
			
			registrarAcao(questionario.getTitulo(), EntidadeRegistroAva.RESPOSTA_QUESTIONARIO, AcaoAva.ACESSAR, questionario.getId() );

			conjunto = qtDAO.findConjuntoRespostasByQuestionarioUsuario(questionario, getUsuarioLogado());
			
			if ( conjunto == null ){
				addMensagemErro("Você ainda não enviou respostas para este questionário");
				return null;
			}
			
			List <EnvioRespostasQuestionarioTurma> respostas = new ArrayList<EnvioRespostasQuestionarioTurma> ();
			
			// Procurada se existe respostas finalizadas
			boolean finalizadas = false;
			for (EnvioRespostasQuestionarioTurma e : conjunto.getRespostas()){
				if ( e.isFinalizado() )
					finalizadas = true;
			}	
			// Adicioana apenas as finalizadas
			if ( finalizadas ) {
				for (EnvioRespostasQuestionarioTurma e : conjunto.getRespostas()){
					if ( e.isFinalizado() )
							respostas.add(e);
				}		
			} // Adiciona a última resposta não finalizada cujo o questionário não esteja sendo respondido.
			else {
				for (EnvioRespostasQuestionarioTurma e : conjunto.getRespostas()){
					if ( new Date().getTime() > e.getDataCadastro().getTime() + questionario.getDuracao() * 60 * 1000 )
							respostas.add(e);
				}		
			}
			
			// Ainda não enviou tentativas.
			if ( respostas == null || respostas.isEmpty() ){
				addMensagemErro("Você ainda não enviou respostas para este questionário");
				return null;

			} // O questionário possui apenas uma tentativa e o sistema irá redirecionar automaticamente para a página desta tentativa. 
			else if (questionario.getTentativas() == 1) {
				resposta = conjunto.getUltimaRespostaFinalizada();
				return visualizarResultado();
			} // O questionário possui várias tentativas e o sistema irá redirecionar para a listagem de respostas do discente. 
			else {
				conjunto.setRespostas(respostas);
				return forward ("/ava/QuestionarioTurma/visualizarTentativasDiscentes.jsp");
			}
			
		} finally {
			if (qtDAO != null)
				qtDAO.close();
		}
	}
	
	/**
	 * Retorna os tipos de pergunta para o cadastro.<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/pergunta.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getTiposPergunta(){
		Collection<SelectItem> tipos = new ArrayList<SelectItem>();
		tipos.add( new SelectItem( PerguntaQuestionarioTurma.UNICA_ESCOLHA, "ÚNICA ESCOLHA" ) );
		tipos.add( new SelectItem( PerguntaQuestionarioTurma.MULTIPLA_ESCOLHA, "MÚLTIPLA ESCOLHA" ) );
		tipos.add( new SelectItem( PerguntaQuestionarioTurma.DISSERTATIVA, "DISSERTATIVA" ) );
		tipos.add( new SelectItem( PerguntaQuestionarioTurma.NUMERICA, "NÚMERICA" ) );
		tipos.add( new SelectItem( PerguntaQuestionarioTurma.VF, "VERDADEIRO OU FALSO" ) );
		return tipos;
	}
	
	/**
	 * Exibe o feedback geral.<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/visualizarResultado.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getFeedbackGeral () {
		if (questionario == null || resposta == null)
			return "";
		
		FeedbackQuestionarioTurma feedback = null;
		
		for (FeedbackQuestionarioTurma f : questionario.getFeedbacks())
			if (f.getPorcentagemMaxima() > resposta.getPorcentagem() * 100 || f.getPorcentagemMaxima() == 100 && resposta.getPorcentagem() * 100 == 100)
				feedback = f;
			else
				break;
		
		if (feedback != null)
			return feedback.getTexto();
		
		return "";
	}
	
	/**
	 * Inicia o caso de uso de corrigir a resposta para o questionário.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String preCorrigir () throws ArqException {
		Integer id = getParameterInt("id");
		QuestionarioTurmaDao qtDAO = null;

		try {
			qtDAO = getDAO(QuestionarioTurmaDao.class);	
			resposta = qtDAO.findByExactField(EnvioRespostasQuestionarioTurma.class, new String [] {"id","ativo"}, new Object [] {id, true}, true);
			
			if (resposta == null){
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
			
			if (!resposta.isFinalizado() && !resposta.getQuestionario().isFimRespostas()) {
				
				if (resposta.getDataCadastro().getTime() + resposta.getQuestionario().getDuracao() * 60 * 1000 > new Date().getTime()){
					addMensagemErro("Caro usuário, o discente está respondendo este questionário no momento.");
					return null;
				}
			}
			
			for ( ConjuntoRespostasQuestionarioAluno c : conjuntoRespostas )
				if ( c.getRespostas().contains(resposta) ) {
					conjunto = c;
					break;
				}	
			
			questionario = resposta.getQuestionario();
			
			questionario.getFeedbacks().iterator();
			
			respostasModel = new ListDataModel(resposta.getRespostas());
			
			prepareMovimento (SigaaListaComando.CORRIGIR_QUESTIONARIO_TURMA);
			
			return forward ("/ava/QuestionarioTurma/corrigirResposta.jsp");
		} finally {
			if (qtDAO != null)
				qtDAO.close();
		}
	}
	
	/**
	 * Retorna a lista de notas possíveis para uma tarefa em uma turma.<br/><br/>
	 * JSP: /ava/QuestionarioTurma/corrigirResposta.jsp
	 * @return
	 */
	public List<SelectItem> getNotas() {
		List<SelectItem> notas = new ArrayList<SelectItem>();
		notas.add(new SelectItem("-1", "Sem Nota"));
		for (int i = 100; i >= 0; --i){
			String porcentagem = i + "%";
			notas.add(new SelectItem(String.valueOf(i), porcentagem));
		}
		return notas;
	}
	
	/**
	 * Exibe o envio do discente, com todas as repostas e permite corrigir as perguntas dissertativas.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/visualizarRespostas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String corrigirDissertativas () throws ArqException {
		
		QuestionarioTurmaDao qtDAO = null;

		try {
			qtDAO = getDAO(QuestionarioTurmaDao.class);	

			resposta.calcularNota();
			
			conjunto = getDAO(QuestionarioTurmaDao.class).findConjuntoRespostas(conjunto.getId());
			// Atualiza a resposta
			conjunto.getRespostas().remove(resposta);
			conjunto.getRespostas().add(resposta);
			conjunto.calcularNotas();
			
			resposta.setConjuntoRespostas(conjunto);
			
			execute( new MovimentoCorrigirQuestionarioTurma (resposta));
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			
			if (resposta.isDissertativasPendentes())
				addMensagemWarning("Prezado(a) docente, para que o envio do discente seja marcado como 'corrigido',  é necessário assinalar todas as respostas dissertativas como (in)correta e informar um texto para a correção.");
				
			return visualizarRespostas();

		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		} finally {
			if ( qtDAO != null )
				qtDAO.close();
		}
		
		return null;
	}
	
	/**
	 * Envia as notas dos alunos para a planilha de notas.<br/><br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/listar.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String publicarNotas () throws ArqException {
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			int idQuestionario = getParameterInt("idQuestionario", 0);
			
			QuestionarioTurmaDao dao = null;
			
			try {
				dao = getDAO(QuestionarioTurmaDao.class);
				
				QuestionarioTurma q = dao.findByPrimaryKey(idQuestionario, QuestionarioTurma.class);
				
				Calendar c = Calendar.getInstance();
				c.setTime(q.getFim());
				c.set(Calendar.HOUR, q.getHoraFim());
				c.set(Calendar.MINUTE, q.getMinutoFim());
				
				if (new Date().before(c.getTime())){					
					addMensagemErro("Só é possível lançar as notas após o término do questionário. " +
							"O questionário está configurado para terminar às "+ Formatador.getInstance().formatarDataHora(c.getTime()) +".");
					return null;
				}
				
				prepareMovimento (SigaaListaComando.PUBLICAR_NOTAS_QUESTIONARIO_TURMA);
				MovimentoPublicarNotasQuestionarioTurma pMov = new MovimentoPublicarNotasQuestionarioTurma(q);
				pMov.getQuestionario().setTurma(turma());
				
				execute(pMov);
				
				Turma t = turma();
				t.setDisciplina(getGenericDAO().refresh(t.getDisciplina()));
				
				// Processa as alterações nas notas
				if (!t.isMedio()){
					ConsolidarTurmaMBean cBean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
					cBean.setTurma(t);
					cBean.prepararTurma(t);
					cBean.recarregarMatriculas();
					cBean.salvarNotas(true);
				}
				
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				
				questionarios = null;
				
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			} finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return listarQuestionariosDocente();
	}
	
	/**
	 * Adiciona uma mensagem a ser exibida em na faixa de porcentagem especificada.<br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/formDadosQuestionario.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 */
	public void adicionarFeedback (ActionEvent e){
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			
			if (StringUtils.isEmpty(txtFeedback))
				mensagemFeedback = "<span style='color:#FF0000;'>Digite uma mensagem a ser exibida.</span>";
			else {
				boolean ok = true;
			
				int indice = questionario.getFeedbacks().size();
				
				if (minFeedback > maxFeedback){
					ok = false;
					mensagemFeedback = "<span style='color:#FF0000;'>Por favor, a porcentagem à esquerda deve ser inferior ou igual à porcentagem à direita.</span>";
				} else
					for (int i = 0; i < questionario.getFeedbacks().size(); i++){
						FeedbackQuestionarioTurma f = questionario.getFeedbacks().get(i);
						
						if (f.getPorcentagemMinima() == minFeedback || f.getPorcentagemMaxima() == maxFeedback){
							ok = false;
							mensagemFeedback = "<span style='color:#FF0000;'>Já existe um feedback cadastrado com as porcentagens informadas.</span>";
							break;
						}
						
						if (f.getPorcentagemMaxima() < maxFeedback){
							
							// Se há um feedback anterior, verifica se não vai ficar com um valor intermediário.
							if (i > 0 && questionario.getFeedbacks().get(i-1).getPorcentagemMinima() < maxFeedback){
								ok = false;
								mensagemFeedback = "<span style='color:#FF0000;'>O feedback não pode ser inserido porque já há um feedback entre as porcentagens informadas.</span>";
								break;
							}
							
							// Verifica se o novo feedback não vai conter um valor dentro dos valores do próximo feedback;
							if (f.getPorcentagemMaxima() > minFeedback){
								ok = false;
								mensagemFeedback = "<span style='color:#FF0000;'>O feedback não pode ser inserido porque já há um feedback entre as porcentagens informadas.</span>";
								break;
							}
							
							indice = i;
							
							break;
						} else if (f.getPorcentagemMinima() < maxFeedback){
							ok = false;
							mensagemFeedback = "<span style='color:#FF0000;'>O feedback não pode ser inserido porque já há um feedback entre as porcentagens informadas.</span>";
							break;
						}
					}
				
				// Se está tudo ok, insere o feedback;
				if (ok){
					
					FeedbackQuestionarioTurma feedback = new FeedbackQuestionarioTurma();
					feedback.setTexto(txtFeedback);
					feedback.setPorcentagemMaxima(maxFeedback);
					feedback.setPorcentagemMinima(minFeedback);
					feedback.setQuestionario(questionario);
					
					questionario.getFeedbacks().add(indice, feedback);
					
					txtFeedback = "";
					maxFeedback = minFeedback;
					minFeedback = 0;
					
					mensagemFeedback = "<span style='color:#00CC00;'>Feedback inserido com sucesso.</span>";
				}
			}
		}
	}
	
	/**
	 * Remove a mensagem de feedback selecionada.<br/>
	 * Método chamado pelas seguintes JSPs:<br/>
	 * <ul>
	 * 		<li>/ava/QuestionarioTurma/formDadosQuestionario.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 */
	public void removerFeedback (ActionEvent e){
		TurmaVirtualMBean tBean = getMBean("turmaVirtual");
		
		if (tBean.isDocente() || tBean.isPermissaoDocente()){
			//índice da alternativa que sera removida
			int indice = modelFeedbacks.getRowIndex();
			FeedbackQuestionarioTurma feedbackRemovido = questionario.getFeedbacks().remove(indice);
			if ( feedbackRemovido.getId() > 0 ){
				if( feedbacksRemover == null )
					feedbacksRemover = new ArrayList<FeedbackQuestionarioTurma> ();
				feedbacksRemover.add(feedbackRemovido);
			}
		}
	}
	
	/**
	 * Verifica se o questionário possui avaliações.
	 * Usado para exibir uma mensagem ao tentar excluir um questionário com avaliações.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/QuestionarioTurma/formDadosQuestionario.jsp</li>
	 * </ul>
	 */
	public boolean isPossuiAvaliacoes() throws DAOException {
		possuiAvaliacoes = false;
		if ( questionario != null )
			possuiAvaliacoes = getGenericDAO().findByExactField(Avaliacao.class, "atividadeQueGerou.id", questionario.getId()) != null;

		return possuiAvaliacoes;
	}
	
	/**
	 * Indica se o prazo para responder o questionário está expirado. 
	 * @return
	 */
	public boolean isPrazoFinalizado () {
		if (questionario != null)
			return new Date().after(questionario.getFim());
		
		return false;
	}
	
	/**
	 * Exibe a jsp do formulário do questionário.
	 * @return
	 */
	private String formularioQuestionario () {
		return forward("/ava/QuestionarioTurma/formDadosQuestionario.jsf");
	}
	
	/**
	 * Exibe a jsp do formulário da pergunta.
	 * @return
	 */
	private String formularioPergunta () {
		return forward("/ava/QuestionarioTurma/novaPergunta.jsf");
	}
	
	/**
	 * Exibe a jsp do formulário da pergunta.
	 * @return
	 */
	private String editarPergunta () {
		return forward("/ava/QuestionarioTurma/editarPergunta.jsf");
	}
	
	/**
	 * Exibe a listagem das perguntas do questionário, para que o docente as gerencie.
	 * @return
	 */
	private String formularioPerguntasDoQuestionario () {
		return forward("/ava/QuestionarioTurma/questionario.jsf");
	}
	
	/**
	 * Exibe a lista de questionários.
	 * @return
	 */
	private String paginaListagem() {
		return forward("/ava/QuestionarioTurma/listar.jsf");
	}
	
	/**
	 * Exibe as respostas de um conjunto.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/QuestionarioTurma/visualizarRespostas.jsp</li>
	 * </ul>
	 * @return
	 */
	public  List <EnvioRespostasQuestionarioTurma> getRespostasConjunto () {
		
		List <EnvioRespostasQuestionarioTurma> result = new ArrayList<EnvioRespostasQuestionarioTurma>();
		
		if (questionario == null){
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
			return result;
		}
		
		if ( questionario.getConjuntos() != null && questionario.getTentativas() == 1 )
			for ( ConjuntoRespostasQuestionarioAluno c : questionario.getConjuntos() ) {
				if ( c.getRespostas() != null ) {
					int ultimaResposta = c.getRespostas().size()-1;
					result.add(c.getRespostas().get(ultimaResposta));
				}
			}
		return result;
	}
	
	/**
	 * Verifica se existe questionário finalizado.
	 * <br/><br/>Método chamado pela(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/ava/QuestionarioTurma/listarDiscente.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isPossuiQuestionarios(){
		if (questionarios != null && !questionarios.isEmpty()) {
			for ( QuestionarioTurma q : questionarios ) {
				if (q.isFinalizado()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<QuestionarioTurma> getQuestionarios() {
		return questionarios;
	}

	public void setQuestionarios(List<QuestionarioTurma> questionarios) {
		this.questionarios = questionarios;
	}

	public QuestionarioTurma getQuestionario() {
		return questionario;
	}

	public void setQuestionario(QuestionarioTurma questionario) {
		this.questionario = questionario;
	}

	public PerguntaQuestionarioTurma getPergunta() {
		return pergunta;
	}

	public void setPergunta(PerguntaQuestionarioTurma pergunta) {
		this.pergunta = pergunta;
	}

	public AlternativaPerguntaQuestionarioTurma getAlternativa() {
		return alternativa;
	}

	public void setAlternativa(AlternativaPerguntaQuestionarioTurma alternativa) {
		this.alternativa = alternativa;
	}

	public DataModel getModelAlternativas() {
		return modelAlternativas;
	}

	public void setModelAlternativas(DataModel modelAlternativas) {
		this.modelAlternativas = modelAlternativas;
	}

	public DataModel getModelPerguntas() {
		return modelPerguntas;
	}

	public void setModelPerguntas(DataModel modelPerguntas) {
		this.modelPerguntas = modelPerguntas;
	}

	public List<AlternativaPerguntaQuestionarioTurma> getAlternativasRemover() {
		return alternativasRemover;
	}

	public void setAlternativasRemover(
			List<AlternativaPerguntaQuestionarioTurma> alternativasRemover) {
		this.alternativasRemover = alternativasRemover;
	}

	public Integer getGabaritoUnicaEscolha() {
		return gabaritoUnicaEscolha;
	}

	public void setGabaritoUnicaEscolha(Integer gabaritoUnicaEscolha) {
		this.gabaritoUnicaEscolha = gabaritoUnicaEscolha;
	}

	public boolean isAcaoAlterarPergunta() {
		return acaoAlterarPergunta;
	}

	public void setAcaoAlterarPergunta(boolean acaoAlterarPergunta) {
		this.acaoAlterarPergunta = acaoAlterarPergunta;
	}

	public boolean isAdicionarEmCategoria() {
		return adicionarEmCategoria;
	}

	public void setAdicionarEmCategoria(boolean adicionarEmCategoria) {
		this.adicionarEmCategoria = adicionarEmCategoria;
	}

	public void setPossuiAvaliacoes(boolean possuiAvaliacoes) {
		this.possuiAvaliacoes = possuiAvaliacoes;
	}

	public boolean isNotificarAlunos() {
		return notificarAlunos;
	}

	public void setNotificarAlunos(boolean notificarAlunos) {
		this.notificarAlunos = notificarAlunos;
	}

	public EnvioRespostasQuestionarioTurma getResposta() {
		return resposta;
	}

	public void setResposta(EnvioRespostasQuestionarioTurma resposta) {
		this.resposta = resposta;
	}

	public DataModel getRespostasModel() {
		return respostasModel;
	}

	public void setRespostasModel(DataModel respostasModel) {
		this.respostasModel = respostasModel;
	}

	public boolean isPermiteNovaTentativa() {
		return permiteNovaTentativa;
	}

	public void setPermiteNovaTentativa(boolean permiteNovaTentativa) {
		this.permiteNovaTentativa = permiteNovaTentativa;
	}

	public int getTentativas() {
		return tentativas;
	}

	public void setTentativas(int tentativas) {
		this.tentativas = tentativas;
	}

	@Override
	public List<QuestionarioTurma> lista() {
		return null;
	}

	public boolean isNaoSalvar() {
		return naoSalvar;
	}

	public void setNaoSalvar(boolean naoSalvar) {
		this.naoSalvar = naoSalvar;
	}

	public boolean isVoltarAoQuestionario() {
		return voltarAoQuestionario;
	}

	public void setVoltarAoQuestionario(boolean voltarAoQuestionario) {
		this.voltarAoQuestionario = voltarAoQuestionario;
	}

	public boolean isNaoValidarRespostas() {
		return naoValidarRespostas;
	}

	public void setNaoValidarRespostas(boolean naoValidarRespostas) {
		this.naoValidarRespostas = naoValidarRespostas;
	}

	public int getMinFeedback() {
		return minFeedback;
	}

	public void setMinFeedback(int minFeedback) {
		this.minFeedback = minFeedback;
	}

	public int getMaxFeedback() {
		return maxFeedback;
	}

	public void setMaxFeedback(int maxFeedback) {
		this.maxFeedback = maxFeedback;
	}

	public String getTxtFeedback() {
		return txtFeedback;
	}

	public void setTxtFeedback(String txtFeedback) {
		this.txtFeedback = txtFeedback;
	}

	public DataModel getModelFeedbacks() {
		return modelFeedbacks;
	}

	public void setModelFeedbacks(DataModel modelFeedbacks) {
		this.modelFeedbacks = modelFeedbacks;
	}
	
	/**
	 * Retorna uma lista de opções para um combo contendo valores de zero a cem.
	 * 
	 * @return
	 */
	public List <SelectItem> getZeroACem () {
		if (zeroACem == null){
			zeroACem = new ArrayList <SelectItem> ();
			
			for (int i = 0; i <= 100; i++)
				zeroACem.add(new SelectItem(i, ""+i));
		}
		
		return zeroACem;
	}

	public void setZeroACem(List<SelectItem> zeroACem) {
		this.zeroACem = zeroACem;
	}

	/**
	 * Retorna a mensagem para o feedback e a reinicializa para que só seja exibida uma vez.
	 * @return
	 */
	public String getMensagemFeedback() {
		String retorno = mensagemFeedback;
		mensagemFeedback = "";
		
		return retorno;
	}

	public void setMensagemFeedback(String mensagemFeedback) {
		this.mensagemFeedback = mensagemFeedback;
	}

	public List<FeedbackQuestionarioTurma> getFeedbacksRemover() {
		return feedbacksRemover;
	}

	public void setFeedbacksRemover(List<FeedbackQuestionarioTurma> feedbacksRemover) {
		this.feedbacksRemover = feedbacksRemover;
	}
	
	
	public boolean isFinalizarQuestionario() {
		return finalizarQuestionario;
	}

	public void setFinalizarQuestionario(boolean finalizarQuestionario) {
		this.finalizarQuestionario = finalizarQuestionario;
	}
	
	/** Chamado no método cadastrar, antes de salvar o objeto. 
	 * <ul>
	 * 		<li>Não invocado por JSPs.</li>
	 * </ul>
	 * */
	@Override
	public void antesPersistir() {
		questionario.setTipoAtividade(2);
	}
	
	/** Chamado no método remover, apos de remover o objeto. 
	 * <ul>
	 * 		<li>Não invocado por JSPs</li>
	 * </ul>
	 * */
	@Override
	public void aposRemocao() {
		TopicoAulaMBean tBean = getMBean("topicoAula");
		tBean.setTopicosAulas(null);
	}
	
	/**
	 * Retorna os possíveis tipos de visualizações de feedback.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> sigaa.war/ava/QuestionarioTurma/formDadosQuestionario.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getTiposVisualizacoesFeedback() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(Character.valueOf('A'), "Após responder o questionário"));
		result.add(new SelectItem(Character.valueOf('F'), "Depois que finalizar o questionário"));
		result.add(new SelectItem(Character.valueOf('N'), "Não exibir"));
		return result;
	}

	/**
	 * Retorna as possíveis formas de lançar notas para um questionário que foi respondido mais de uma vez.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> sigaa.war/ava/QuestionarioTurma/formDadosQuestionario.jsp</li>
	 * </ul>
	 * @return
	 */
	public List<SelectItem> getTiposMedia() {
		List<SelectItem> result = new ArrayList<SelectItem>();
		result.add(new SelectItem(Character.valueOf('U'), "Última tentativa"));
		result.add(new SelectItem(Character.valueOf('M'), "Média das tentativas"));
		result.add(new SelectItem(Character.valueOf('A'), "Melhor tentativa"));
		return result;
	}
	
	public void setManterDados(boolean manterDados) {
		this.manterDados = manterDados;
	}

	public boolean isManterDados() {
		return manterDados;
	}

	public void setConjuntoRespostas(List<ConjuntoRespostasQuestionarioAluno> conjuntoRespostas) {
		this.conjuntoRespostas = conjuntoRespostas;
	}

	public List<ConjuntoRespostasQuestionarioAluno> getConjuntoRespostas() {
		return conjuntoRespostas;
	}

	public void setConjunto(ConjuntoRespostasQuestionarioAluno conjunto) {
		this.conjunto = conjunto;
	}

	public ConjuntoRespostasQuestionarioAluno getConjunto() {
		return conjunto;
	}
	
	/**
	 * pega as mensagens de erro da tela de perguntas.
	 * @return
	 */
	public String getMensagemErroPergunta() {
		String m = mensagemErroPergunta;
		mensagemErroPergunta = null;
		return m;
	}

	public void setMensagemErroPergunta(String mensagemErroPergunta) {
		this.mensagemErroPergunta = mensagemErroPergunta;
	}
	
	/**
	 * Verifica se a unidade possui nota cadastrada.
	 * Método não invocado por JSP(s)
	 * @return
	 */
	public boolean isUnidadePossuiNota() throws DAOException {
		AvaliacaoDao aDao = null;
		try {
			aDao = getDAO(AvaliacaoDao.class);	
			Integer unidade = questionario.getUnidade();
			boolean possuiNotas = aDao.possuiNotasCadastradasNaUnidade(turma(), unidade);
			boolean possuiAvaliacoes = aDao.possuiAvaliacoesNaUnidade(turma(), unidade);
			return possuiNotas && !possuiAvaliacoes;
		} finally {
			if (aDao != null)
				aDao.close();
		}
	}
}