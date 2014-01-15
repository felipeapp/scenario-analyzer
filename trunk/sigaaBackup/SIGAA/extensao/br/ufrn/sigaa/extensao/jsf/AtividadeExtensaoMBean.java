/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/11/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteExternoDao;
import br.ufrn.sigaa.arq.dao.extensao.PlanoTrabalhoExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.AutorizacaoDepartamentoDao;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.dao.projetos.HistoricoSituacaoProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.OrcamentoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.extensao.dao.LocalRealizacaoDao;
import br.ufrn.sigaa.extensao.dao.ObjetivoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.AtividadeUnidade;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.extensao.dominio.ExecutorFinanceiro;
import br.ufrn.sigaa.extensao.dominio.LocalRealizacao;
import br.ufrn.sigaa.extensao.dominio.Objetivo;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.ParticipanteExterno;
import br.ufrn.sigaa.extensao.dominio.ProgramaEstrategicoExtensao;
import br.ufrn.sigaa.extensao.dominio.ResumoOrcamentoDetalhado;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.AtividadeExtensaoHelper;
import br.ufrn.sigaa.extensao.jsf.helper.ControleFluxoAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.extensao.negocio.AtividadeExtensaoValidator;
import br.ufrn.sigaa.extensao.negocio.CadastroExtensaoMov;
import br.ufrn.sigaa.mensagens.MensagensExtensao;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.pesquisa.negocio.ProjetoPesquisaValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.projetos.dominio.ArquivoProjeto;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.ClassificacaoFinanciadora;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.FotoProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.GrupoPublicoAlvo;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.TipoPublicoAlvo;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.negocio.MembroProjetoValidator;
import br.ufrn.sigaa.projetos.negocio.ProjetoBaseValidator;

/*******************************************************************************
 * MBean responsável pelo controle geral do cadastro de atividades de extensão.
 * Todos os fluxos de cadastro/alteração de atividades de extensão poderão
 * utilizar este MBean para os dados em comum, como as informações gerais,
 * membros da atividade, orçamento, etc.
 * 
 * @author Ilueny Santos
 * @author Ricardo Wendell
 * 
 ******************************************************************************/
@Scope("session")
@Component("atividadeExtensao")
public class AtividadeExtensaoMBean extends SigaaAbstractController<AtividadeExtensao> {

	/** Constante utilizada para redimensionar fotos incluídas na ação. */
	private static final int HEIGHT_FOTO = 100;
    /** Constante utilizada para redimensionar fotos incluídas na ação. */
    private static final int WIDTH_FOTO = 100;    
    
    /** Armazena um membro do projeto. **/
    private MembroProjeto membroEquipe = new MembroProjeto();

    /** Armazena os membros do projeto. **/
    private Collection<MembroProjeto> membrosEquipe = null;

    /** Armazena um orçamento detalhado. **/
    private OrcamentoDetalhado orcamento = new OrcamentoDetalhado();

    /** Armazena todos os públicos alvos possíveis cadastrados. **/
    private Collection<GrupoPublicoAlvo> grupos = new HashSet<GrupoPublicoAlvo>();

    /** Armazena a tabela orçamentária. **/
    private Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();

    /** Armazena atividades de extensão para manipulação. **/
    private Collection<AtividadeExtensao> atividadesLocalizadas;

    /** Armazena as ações gravadas que são ações com cadastro em andamento que 
     * ainda não foram submetidas para avaliação dos departamentos. **/
    private Collection<AtividadeExtensao> atividadesGravadas;    
    /** Locais de Realização */
    private DataModel locaisRealizacao = new ListDataModel();
    /** Atributo utilizado para a inserção de informação de Nome da Atividade em telas de buscas. **/
    private String buscaNomeAtividade;
    /** Atributo utilizado para a inserção de informação do edital em telas de buscas. **/
    private int buscaEdital;    
    /** Atributo utilizado para a inserção de informação do Tipo de atividade em telas de buscas. **/
    private Integer[] buscaTipoAtividade;    
    /** Atributo utilizado para a inserção de informação do Título em telas de buscas. **/
    private String buscaTitulo;
    /** Atributo utilizado para a inserção de informação da Área do CNPQ em telas de buscas. **/
    private int buscaAreaCNPq;
    /** Atributo utilizado para a inserção de informação da Unidade de atividade em telas de buscas. **/
    private int buscaUnidade;
    /** Atributo utilizado para a inserção de informação do Centro de atividade em telas de buscas. **/
    private int buscaCentro;
    /** Atributo utilizado para a inserção de informação da Área Temática Principal em telas de buscas. **/
    private int buscaAreaTematicaPrincipal;
    /** Atributo utilizado para a inserção de informação da Situação da Atividade em telas de buscas. **/
    private Integer[] buscaSituacaoAtividade;
    /** Atributo utilizado para a inserção de informação do Financiamento Interno em telas de buscas. **/
    private boolean buscaFinanciamentoInterno;
    /** Atributo utilizado para a inserção de informação do Financiamento Externo em telas de buscas. **/
    private boolean buscaFinanciamentoExterno;
    /** Atributo utilizado para a inserção de informação do Auto Financiamento em telas de buscas. **/
    private boolean buscaAutoFinanciamento;
    /** Atributo utilizado para a inserção de informação do Convennio FUNPEC em telas de buscas. **/
    private boolean buscaConvenioFunpec;
    /** Atributo utilizado para a inserção de informação do Financiamento Interno após aprovação do comitê em telas de busca */
    private boolean buscaRecebeuFinanciamentoInterno;
    /** Atributo utilizado para a inserção de informação do Ano em telas de buscas. **/
    private Integer buscaAno = CalendarUtils.getAnoAtual();
    /** Atributo utilizado para a inserção de informação da Data de início em telas de buscas. **/
    private Date buscaInicio;
    /** Atributo utilizado para a inserção de informação da Data de fim em telas de buscas. **/
    private Date buscaFim;
    /** Atributo utilizado para a inserção de informação da Data de início da conclusão em telas de buscas. **/
    private Date buscaInicioConclusao;
    /** Atributo utilizado para a inserção de informação da Data de fim da conclusão em telas de buscas. **/
    private Date buscaFimConclusao;
    /** Atributo utilizado para a inserção de informação da Data de início da execução em telas de buscas. **/
    private Date buscaInicioExecucao;
    /** Atributo utilizado para a inserção de informação da Data de fim da execução em telas de buscas. **/
    private Date buscaFimExecucao;
    /** Atributo utilizado para a inserção de informação do Registro Simplificado em telas de buscas. **/
    private int buscaRegistroSimplificado = 0;
    /** Atributo utilizado para a inserção de informação da Ação de Solicitação de renovação em telas de buscas. **/
    private int buscaAcaoSolicitacaoRenovacao = 0;
    /** Atributo utilizado para a inserção de informação do Projeto Associado em telas de buscas. **/
    private int buscaProjetoAssociado = 0;
    /** Atributo utilizado para a inserção de informação do Código em telas de buscas. **/
    private String buscaCodigo = null;

    /** Atributo utilizado para a seleção de opção de busca(filtro) do Título em telas de busca. **/
    private boolean checkBuscaTitulo;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Projeto Associado em telas de busca. **/
    private boolean checkBuscaProjetoAssociado;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Edital em telas de busca. **/
    private boolean checkBuscaEdital;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Código em telas de busca. **/
    private boolean checkBuscaCodigo;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Tipo de Atividade em telas de busca. **/
    private boolean checkBuscaTipoAtividade;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Situação da Atividade em telas de busca. **/
    private boolean checkBuscaSituacaoAtividade;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Unidade Proponente em telas de busca. **/
    private boolean checkBuscaUnidadeProponente;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Centro em telas de busca. **/
    private boolean checkBuscaCentro;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Área CNPQ em telas de busca. **/
    private boolean checkBuscaAreaCNPq;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Área Temática Principal em telas de busca. **/
    private boolean checkBuscaAreaTematicaPrincipal;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Servidor em telas de busca. **/
    private boolean checkBuscaServidor;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Ano em telas de busca. **/
    private boolean checkBuscaAno;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Financiamento do Convenio em telas de busca. **/
    private boolean checkBuscaFinanciamentoConvenio;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Registro Simplificado em telas de busca. **/
    private boolean checkBuscaRegistroSimplificado;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Período em telas de busca. **/
    private boolean checkBuscaPeriodo;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Período de conlcusão em telas de busca. **/
    private boolean checkBuscaPeriodoConclusao;
    /** Atributo utilizado para a seleção de opção de busca(filtro) do Período de início em telas de busca. **/
    private boolean checkBuscaPeriodoInicio;
    /** Atributo utilizado para a seleção de opção de busca(filtro) da Ação de solicitação de Renovação em telas de busca. **/
    private boolean checkBuscaAcaoSolicitacaoRenovacao;    
    
    /** Atributo utilizado para informar o nome do Edital */
    private String nomeEdital;
    /** Atributo utilizado para informar o nome do Tipo de Ação */
    private String nomeTipoAcao;
    /** Atributo utilizado para informar o nome da Área CNPQ */
    private String nomeAreaCnpq;
    /** Atributo utilizado para informar o nome da Unidade */
    private String nomeUnidade;
    /** Atributo utilizado para informar o nome do Centro */
    private String nomeCentro;
    /** Atributo utilizado para informar o nome da Área */
    private String nomeArea;
    /** Atributo utilizado para informar o nome da Situação */
    private String nomeSituacao;
    /** Atributo utilizado para informar o nome do Tipo de Registro */
    private String nomeTipoRegistro;

    /** Representa todas as possíveis situações o Projeto  **/
    private Collection<TipoSituacaoProjeto> situacoesProjeto = new HashSet<TipoSituacaoProjeto>();

    /** Representa um servidor que poderá se tornar um membro do projeto. **/
    private Servidor servidor = new Servidor();

    /** Representa um docente que poderá se tornar um membro do projeto. **/
    private Servidor docente = new Servidor();

    /** Representa um discente que poderá se tornar um membro do projeto. **/
    private Discente discente = new Discente();

    /** Representa uma pessoa externa que poderá se tornar um membro do projeto. **/
    private ParticipanteExterno participanteExterno = new ParticipanteExterno();
    /** Atributo utilizado para Upload de Arquivos */
    private UploadedFile file;
    /** Atributo utilizado para Upload de fotos */
    private UploadedFile foto;
    /** Atributo utilizado para descrição do Arquivo */
    private String descricaoArquivo;
    /** Atributo utilizado para descrição da Foto */
    private String descricaoFoto;

    /** Armazena as atividades de um membro do projeto. **/
    private Collection<AtividadeExtensao> atividadesMembroParticipa;
    /** Armazena as atividades de um membro do projeto. **/
    private Collection<AtividadeExtensao> atividadesMembroCoordena;
    /** Atributo utilizado para o controle de fluxo */
    private ControleFluxoAtividadeExtensao controleFluxo;
    /** Atributo utilizado para identificar outra unidade  */
    private Unidade outraUnidade = new Unidade();

    /** Armazena uma atividade a partir da view. **/
    private AtividadeExtensao atividadeSelecionada = new AtividadeExtensao();
    /** Atributo utilizado para informar os erros de ajax */
    private List<MensagemAviso> avisosAjax = new ArrayList<MensagemAviso>();
    /** Atributo utilizado para armazenar o CPF */
    private String cpf = new String("");
    /** Atributo utilizado para informar se haverá ou não o botão para relatório do contato do coordenador */
    private boolean botaoRelatorioContatoCoordenador;
    
    /** Mensagem exibida aos Coordenadores de ações de extensão que estão pendentes quanto ao envio de relatórios. **/
    private String msgCoordenadorPendenteRelatorio = new String();

    /** Data para reativação de uma ação de extensão. **/
    private Date dataNovaFinalizacao;
    
    /** Função de Membro para Docente */
    private FuncaoMembro funcaoDocente = new FuncaoMembro();
    /** Função de Membro para Discente */
    private FuncaoMembro funcaoDiscente = new FuncaoMembro();
    /** Função de Membro para Servidor */
    private FuncaoMembro funcaoServidor = new FuncaoMembro();
    /** Função de Membro para Externo */
    private FuncaoMembro funcaoExterno = new FuncaoMembro();
    /** Atributo utilizado para selecionar a Aba correta na tela de Membros da Equipe após a página ser recarregada. */
    
    /** Informa se o docente pode gerenciar particpantes. */
    private boolean docenteGerenciaParticipantes = false;
    
    private String tab = "";
    
    /**
     * Construtor padrão.
     * Inicializa o MBean removendo objetos antigos de sessão e
     * instânciando outros objetos importantes.
     * 
     */
    public AtividadeExtensaoMBean() {
    	clear();
    }

    /** 
     * Verifica compatilidade de pepeis do usuário.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>Não é chamado por JSPs.</li>
	 * </ul>
     */
    @Override
    public void checkChangeRole() throws SegurancaException {
		// Se o usuário não for docente ou docente externo e estiver tentando realizar esta operação.
		if (!getUsuarioLogado().getVinculoAtivo().isVinculoServidor() && !getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno()) {
			throw new SegurancaException("Apenas Docentes ou Técnicos Administrativos podem realizar esta operação.");
		}
    }
    
    /** 
     * Verifica compatilidade de pepeis do usuário.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/extensao/Atividade/lista_atividades_pendentes.jsp</li>
	 * </ul>
     * @param evt
     * @throws SegurancaException
     */
    public void checkChangeRole(PhaseEvent evt) throws SegurancaException{
    	checkChangeRole();
    }
    
    /**
     * Inicializa objetos importantes no cadastro de uma nova ação de extensão.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>Não é chamado por JSPs.</li>
	 * </ul>
     */
    public void clear() {
		this.obj = new AtividadeExtensao();
		membroEquipe.setFuncaoMembro(new FuncaoMembro());
		orcamento = new OrcamentoDetalhado();
		tabelaOrcamentaria.clear();
		getCurrentSession().setAttribute("idElementoDespesa", 0);
		this.obj.setAutoFinanciado(false);
		this.obj.setFinanciamentoInterno(false);
		this.obj.setFinanciamentoExterno(false);
		this.obj.setProgramaEstrategico(new ProgramaEstrategicoExtensao());
		this.obj.setExecutorFinanceiro(new ExecutorFinanceiro());
		membrosEquipe = null;
		locaisRealizacao = new ListDataModel();
		
		this.obj.setLocaisRealizacao(new ArrayList<LocalRealizacao>());
		this.obj.setLocalRealizacao(new LocalRealizacao());
		ProgramaMBean programaMBean = (ProgramaMBean) getMBean("programaExtensao");
		AtividadeExtensao atv = new AtividadeExtensao();
		atv.getProjeto().setCoordenador(new MembroProjeto());
		programaMBean.setObj(atv);
		
		
    }

	/**
	 * Inicia o cadastro de uma ação de extensão que já foi realizada.
	 * Solicitações de registro são realizadas quando uma ação já foi realizada,
	 * mas precisa ser formalizada junta a Pró-reitoria de Extensão.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/extensao/Atividade/lista_atividades_pendentes.jsp</li>
	 * </ul>
	 * @return Página para seleção de tipos de ações possíveis para cadastro.
	 * @throws SegurancaException 
	 */
	public String iniciarRegistro() throws SegurancaException {
		obj.setRegistro(true);
		return iniciar();
	}

    /**
     * Iniciar o cadastro de uma proposta de ação de extensão completa.
     * 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/lista_atividades_pendentes.jsp</li>
     * </ul>
     * @return Tela para seleção de tipos de ações possíveis para cadastro.
     * @throws SegurancaException 
     */
	public String iniciarPropostaCompleta() throws SegurancaException {
		obj.setRegistro(false);
		return iniciar();
	}

    /**
     * Inicia cadastro de atividades de extensão.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     *  <li>sigaa.war/extensao/menu.jsp</li>
     * </ul>
     * @return Tela para seleção de tipos de ações possíveis para cadastro.
     * @throws SegurancaException 
     */
    public String iniciar() throws SegurancaException {
    	checkChangeRole();
    	try {
    		boolean registro = obj.isRegistro();
    		clear();
    		obj.setRegistro(registro);
    		prepareMovimento(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO);
    		return forward(ConstantesNavegacao.SELECIONA_ATIVIDADE);    		
    	} catch (ArqException e) {
    		notifyError(e);
    		addMensagemErroPadrao();
    	}
    	return null;
    }

    /**
     * Método chamado pelos MBeans específicos das ações de extensão para
     * popular o objeto utilizado no fluxo.
     * <br />
     * <ul>
     * 		<li>Método não invocado por JSP´s</li>
     * </ul>
     * @param mBean Mbean do tipo de ação que será criada.
     * {@link ProjetoExtensaoMBean},
     * {@link CursoEventoMBean},
     * {@link ProdutoMBean},
     * {@link ProgramaMBean}
     * 
     * @throws DAOException Inicialização dos tipos de ações permitidas para cadastro. 
     */
    public void prepararFormulario(SigaaAbstractController<AtividadeExtensao> mBean) throws DAOException {

	if (obj == null) {
	    obj = new AtividadeExtensao();
	}
	
	AtividadeExtensao atividade = mBean.getObj();

	// Popular dados iniciais
	atividade.setUnidade(getServidorUsuario().getUnidade());
	atividade.setRegistro(this.obj.isRegistro());
	
	if (atividade.getClassificacaoFinanciadora() == null) {
	    atividade.setClassificacaoFinanciadora(new ClassificacaoFinanciadora());
	}
	
	this.obj = atividade;
	this.obj.getProjeto().setCoordenador(new MembroProjeto());

	getGenericDAO().initialize(obj.getTipoAtividadeExtensao());

	// Inicializar controle de fluxo do cadastro
	if (obj.isProjetoAssociado()) {
	    controleFluxo = new ControleFluxoAtividadeExtensao(obj.getTipoAtividadeExtensao().getId() + ControleFluxoAtividadeExtensao.CONST_ACAO_ASSOCIADA, obj.isRegistro());
	}else {
	    controleFluxo = new ControleFluxoAtividadeExtensao(obj.getTipoAtividadeExtensao().getId(), obj.isRegistro());
	}
    }

    /**
     * Método que retorna o próximo passo do fluxo em execução JSPs presentes no
     * cadastro de ações de extensão.
     * Grava os dados da tela atual e passa para próxima tela do fluxo.
     * <br /> 
     * <ul>
     * <li>Método não invocado por JSP´s</li>
     * </ul>
     * @see AtividadeExtensaoMBean#gravar();
     * @see ControleFluxoAtividadeExtensao;
     * 
     * @return retorna o próximo passo do fluxo 
     */
    public String proximoPasso() {
    	try {
    		if (isFluxoCadastroValido()) {
    			gravarTemporariamente();
    			AtividadeExtensaoHelper.instanciarAtributos(getObj());
    			if ( hasErrors() )
					return null;
    			return forward(controleFluxo.proximoPasso());
    		}
    	}catch (Exception e) {
    		tratamentoErroPadrao(e);
    		e.printStackTrace();
    	}
    	return null;
    }
    
    /**
     * Verifica se o fluxo do cadastro está configurado corretamente.
     * O fluxo determina qual será o próximo passo e o passo anterior durante o cadastro/edição da proposta.
     * 
     * Não é utilizado por JSP.
     * 
     */
    private boolean isFluxoCadastroValido() {
    	if (controleFluxo != null) {
    		return true;
    	}else if (ValidatorUtil.isNotEmpty(obj.getTipoAtividadeExtensao())) {
    		controleFluxo = new ControleFluxoAtividadeExtensao(obj.getTipoAtividadeExtensao().getId(), obj.isRegistro());
    		return true;
    	}else {
    		addMensagemErro("Não foi possível identificar o Próximo Passo ou o Passo Anterior. Por favor reinicie o cadastro ou edição da proposta.");
    		return false;
    	}
    }

    /**
     * Método que retorna o passo anterior do fluxo em execução.
     * JSPs presentes no cadastro de ações de extensão.
     * <br />
     * <ul>
     *  	<li>Método não invocado por JSP´s</li>
     * </ul>
     * @see ControleFluxoAtividadeExtensao; 
     * 
     * @return retorna o passo anterior do fluxo em execução
     */
    public String passoAnterior() {
    	try {
    		if (isFluxoCadastroValido()) {
    			return forward(controleFluxo.passoAnterior());
    		}
    	}catch (Exception e) {
    		tratamentoErroPadrao(e);
    	}
    	return null;
    }
    
    /**
     * Este método faz uma busca pelas ações de extensão do usuário logado atualmente.
     * Após a busca, redireciona para uma página de listagem com as ações encontradas.
     * <br />
     * Método chamado pela(s) JSP(s):
     * <ul>
     *  <ul>sigaa.war/portais/discente/menu_discente.jsp</li>
     * </ul>
     * @return
     * @throws DAOException
     */
    public String carregarAcoesDiscenteLogado() throws DAOException {    	
    	AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);    	
    	atividadesMembroParticipa = dao.findByDiscente(getDiscenteUsuario().getId());    	
    	return forward("/extensao/DiscenteExtensao/atividades_discente.jsp");
    }
    
    
    
    /**
     * Método usado para avançar o primeiro passo (dados gerais). Vai para a
     * tela de dados específicos de cada tipo de atividade.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/dados_gerais.jsp</li>
     * </ul>
     * @return página a ser carregada
     * @throws DAOException 
     */
    public String submeterDadosGerais() throws DAOException {

    		// Realizar validações
    		ListaMensagens mensagens = new ListaMensagens();
    		validateRequiredId(obj.getProjeto().getCoordenador().getServidor().getId(), "Coordenador", mensagens);
    		AtividadeExtensaoValidator.validaDadosGerais(obj, mensagens, isUserInRole(SigaaPapeis.GESTOR_EXTENSAO));

    		if (!mensagens.isEmpty()) {
    			addMensagens(mensagens);
    			return null;
    		}

    		// Inicializar atributos selecionados pelo usuário em listas
    		GenericDAO dao = getGenericDAO();
    		
    		if(obj.getAreaTematicaPrincipal() != null){
    			dao.initialize(obj.getAreaTematicaPrincipal());
    		}
    		
    		if(obj.getAreaConhecimentoCnpq() != null){
    			obj.setAreaConhecimentoCnpq(dao.findByPrimaryKey(obj.getAreaConhecimentoCnpq().getId(), AreaConhecimentoCnpq.class));
    		}
    		
    		if(obj.getSituacaoProjeto() != null){
    			obj.setSituacaoProjeto(dao.findByPrimaryKey(obj.getSituacaoProjeto().getId(), TipoSituacaoProjeto.class));
    		}
    		
    		if(obj.getProjeto().getUnidade() != null && (obj.getProjeto().getCoordenador() != null && obj.getProjeto().getCoordenador().getUnidade() != null)){
    			obj.getProjeto().setUnidade(dao.findByPrimaryKey(obj.getProjeto().getCoordenador().getUnidade().getId(), Unidade.class, "id", "nome", "sigla", "codigo"));
    		}

    		if (obj.getTipoRegiao() != null) {
    			dao.initialize(obj.getTipoRegiao());
    		}

    		if (obj.getProgramaEstrategico() != null && obj.getProgramaEstrategico().getId() == 0) {
    			obj.setProgramaEstrategico(null);
    		}

    		if ( obj.getExecutorFinanceiro() != null && obj.getExecutorFinanceiro().getId() == 0) {
    			obj.setExecutorFinanceiro(null);
    		}
    		
    		if (obj.getLocalRealizacao() != null) {
    			// Abrangência internacional
	    		if (ValidatorUtil.isNotEmpty(obj.getTipoRegiao()) && obj.getTipoRegiao().getId() == TipoRegiao.INTERNACIONAL) {
	    			obj.getLocalRealizacao().setMunicipio(null);    		
	    		
	    		// Abrangência nacional	
	    		}else {    		
		    		if (ValidatorUtil.isNotEmpty(obj.getLocalRealizacao().getMunicipio())) {		    			
		    			Municipio municipio = dao.findByPrimaryKey(obj.getLocalRealizacao().getMunicipio().getId(), Municipio.class, "id", "nome", "codigo", "unidadeFederativa.id", "unidadeFederativa.sigla");

		    			final String espaco = obj.getLocalRealizacao().getDescricao();
		    			dao.initialize(obj.getLocalRealizacao());
		    			// Alteração no espaço de realização da proposta
		    			if (!ValidatorUtil.isEmpty(espaco) && obj.getLocalRealizacao().getId() > 0) {
		    				obj.getLocalRealizacao().setDescricao(espaco);
		    			}
		    			obj.getLocalRealizacao().setMunicipio(municipio);
		    			obj.getLocalRealizacao().setMunicipioInternacional(null);
		    		}
	    		}
    		}

    		if (obj.getEditalExtensao() != null) {
    			dao.initialize(obj.getEditalExtensao());
    		}

    		if (obj.getClassificacaoFinanciadora() != null) {
    			dao.initialize(obj.getClassificacaoFinanciadora());
    		}

    		
    		setOperacaoAtiva(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO.getId());
    		membrosEquipe = null;
    		return proximoPasso();

    }

    
    /**
     * Indica se poderá ser alterado o ano do projeto. Caso o projeto já possua um número institucional associado
     * ao ano do projeto não é mais permitido altrar o ano.
     * 
     * Chapado por:
     * sigaa.war/extensao/Atividade/dados_gerais.jsp
     * @return
     */
    public boolean isPermiteAlterarAnoProjeto() {
    	if(obj!=null && obj.getProjeto()!=null &&  ValidatorUtil.isEmpty(obj.getProjeto().getNumeroInstitucional())) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * Retorna a lista completa com todos os públicos possíveis
     * cadastrados.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/dados_gerais.jsp</li>
     * </ul>
     * @return Coleção de {@link GrupoPublicoAlvo} públicos possíveis.
     * @throws DAOException Carrega lista de grupos de público do banco.
     */
    public Collection<GrupoPublicoAlvo> getGrupos() throws DAOException {
	grupos = getGenericDAO().findAllAtivos(GrupoPublicoAlvo.class, "descricao");
	// Removendo tipos de público inativos
	for (GrupoPublicoAlvo grupo : grupos) {
	    for (Iterator<TipoPublicoAlvo> iterator = grupo.getTipos()
		    .iterator(); iterator.hasNext();) {
		if (!iterator.next().isAtivo()) {
		    iterator.remove();
		}
	    }
	}
	return grupos;
    }

    /**
     * Adiciona um discente à lista de membros da atividade de extensão.
     * <br />
     * <ul>
     *  	<li>Método não invocado por JSP´s</li>
     * </ul>
     * @return Adiciona o discente retorna para a mesma tela.
     * @throws ArqException 
     */
    public String adicionaDiscente() throws ArqException {
    	checkChangeRole();

	ListaMensagens mensagens = new ListaMensagens();
	
	GenericDAO dao = getGenericDAO();
	
	try {
	    //setando a aba atual
	    getCurrentSession().setAttribute("aba", "membro-discente");

	    if ((discente == null) || (discente.getId() == 0)) {
		discente = new Discente();
		addMensagemErro("Selecione o Discente.");
		return null;
	    }

	    discente = dao.findByPrimaryKey(discente.getId(), Discente.class);
	    CategoriaMembro cat = dao.findByPrimaryKey(CategoriaMembro.DISCENTE, CategoriaMembro.class);
	    FuncaoMembro func = dao.findByPrimaryKey(funcaoDiscente.getId(), FuncaoMembro.class);

	    membroEquipe.setDiscente(discente);
	    membroEquipe.setCategoriaMembro(cat);	    
	    membroEquipe.setFuncaoMembro(func);
	    membroEquipe.setPessoa(discente.getPessoa());	    
	    membroEquipe.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
	    membroEquipe.setAtivo(true);	    
	    membroEquipe.setDataInicio(obj.getDataInicio());
	    membroEquipe.setDataFim(obj.getDataFim());

	    // Validação
	    MembroProjetoValidator.validaDiscente(obj.getMembrosEquipe(), membroEquipe, mensagens);
	    MembroProjetoValidator.validaChTotalMembroProjeto(obj.getMembrosEquipe(), membroEquipe, mensagens);

	    if (!mensagens.isEmpty()) {
		membroEquipe = new MembroProjeto();
		addMensagens(mensagens);
		return null;
	    }

	    //Verificando se o discente já foi adicionado ao membros da equipe
	    if ( verificaDuplicidadePessoa(membroEquipe) ) {

		// Evitando TransientObjectException
		membroEquipe.setServidor(null);
		membroEquipe.setDocenteExterno(null);
		membroEquipe.setParticipanteExterno(null);
		obj.addMembroEquipe(membroEquipe);
		gravarTemporariamente();

	    } else {
		addMensagemErro("Discente já inserido na atividade");
	    }

	} finally {
		dao.close();
	}

	membrosEquipe = null;
	// Limpa os dados
	popularDadosMembroProjeto();

	return null;
    }
    
    /**
     * Membros da Pro-Reitoria de Extensão podem cadastrar participantes externos como coordenadores de ações.
     * <br />
     * <ul>
     *  	<li>Método não invocado por JSP´s</li>
     * </ul>
     * @return
     */
    public boolean isPermitidoExternoCoordenar() {
	return getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_EXTENSAO);
    }

    /**
     * Adiciona um servidor à lista de membros da atividade de extensão.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):  
     * <ul>
     * <li>sigaa.war/extensao/Atividade/membros_equipe.jsp</li>
     * </ul>
     * @return retorna para mesma tela permitindo adição de novo membro a equipe.
     * @throws ArqException 
     */
	public String adicionarMembroEquipe() throws ArqException {
    	checkChangeRole();
		
		GenericDAO dao = getGenericDAO();
		ListaMensagens mensagens = new ListaMensagens();

		// Qual o tipo de membro será adicionado (default = docente)
		int categoriaMembro = getParameterInt("categoriaMembro",
				CategoriaMembro.DOCENTE);
		membroEquipe.setCategoriaMembro(dao.findByPrimaryKey(
				categoriaMembro, CategoriaMembro.class));
		membroEquipe.setRegistroEntrada(getUsuarioLogado()
				.getRegistroEntrada());
		membroEquipe.setProjeto(obj.getProjeto());
		membroEquipe.setChDedicada(1);
		// Redireciona para método que adiciona discente.
		if (CategoriaMembro.DISCENTE == categoriaMembro) {
			return adicionaDiscente();
		}

		if(categoriaMembro != CategoriaMembro.EXTERNO) {
			// Validação docente ou servidor.
			if (!ValidatorUtil.isEmpty(servidor)) {
			    membroEquipe.setServidor(servidor);
			    membroEquipe.setPessoa(servidor.getPessoa());
			    membroEquipe.setFuncaoMembro(getGenericDAO().findByPrimaryKey(funcaoServidor.getId(), FuncaoMembro.class));
			}else {
			    membroEquipe.setServidor(docente);
			    membroEquipe.setPessoa(docente.getPessoa());
			    membroEquipe.setFuncaoMembro(getGenericDAO().findByPrimaryKey(funcaoDocente.getId(), FuncaoMembro.class));
			}
			
			MembroProjetoValidator.validaServidor(obj.getMembrosEquipe(), membroEquipe, mensagens);
		}
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		if (CategoriaMembro.EXTERNO == categoriaMembro) {

			// Evitar nullpointer
			if ((participanteExterno == null)
					|| (participanteExterno.getPessoa() == null)) {
				participanteExterno = new ParticipanteExterno();
				cpf = "";
				return null;
			}

			membroEquipe.setFuncaoMembro(getGenericDAO().findByPrimaryKey(funcaoExterno.getId(), FuncaoMembro.class));
			
			// Verifica se o CPf foi digitado
			if ( participanteExterno.getPessoa().getCpf_cnpj() == null && !participanteExterno.getPessoa().isInternacional() ){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
				return null;
			}
			
			
			// Valida cpf
			if ((participanteExterno.getPessoa().isInternacional())
					|| (ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf))) {
				if ((membroEquipe != null) && (participanteExterno != null)) {

				    	membroEquipe.setParticipanteExterno(participanteExterno);
					MembroProjetoValidator.validaParticipanteExterno(obj.getMembrosEquipe(), 
						membroEquipe, mensagens, isPermitidoExternoCoordenar());

					if (!mensagens.isEmpty()) {
						addMensagens(mensagens);
						participanteExterno.getPessoa().setInternacional(false);
						return null;
					}

				} else {
					addMensagemErro("Dados obrigatórios para o cadastro do membro da equipe não foram informados");
					participanteExterno.getPessoa().setInternacional(false);
					return null;
				}

			} else {
				addMensagemErro("CPF digitado para o participante externo é ínválido!");
				participanteExterno.getPessoa().setInternacional(false);
				return null;
			}

			if (participanteExterno.getPessoa() != null) {
				participanteExterno.getPessoa().setNome(
						participanteExterno.getPessoa().getNome().trim()
								.toUpperCase());
				membroEquipe.setParticipanteExterno(participanteExterno);
				membroEquipe.setPessoa(participanteExterno.getPessoa());
				dao.initialize(membroEquipe.getFuncaoMembro());
			}

			getCurrentSession().setAttribute("aba", "membro-externo");

		} else { // é servidor ou docente

			dao.initialize(membroEquipe.getFuncaoMembro());

			if (CategoriaMembro.SERVIDOR == categoriaMembro) {
				membroEquipe.setServidor(getGenericDAO().findByPrimaryKey(
						servidor.getId(), Servidor.class));
				// Evitando erro de lazy na validação
				if (membroEquipe.getServidor().getEscolaridade() != null) {
					membroEquipe.getServidor().getEscolaridade().getId();
				}
				getCurrentSession().setAttribute("aba", "membro-servidor");
			}

			if (CategoriaMembro.DOCENTE == categoriaMembro) {
				
				
				
				if(membroEquipe.isCoordenador()) {
					//Para acidentalmente não gerar coordenador no banco com o atibuto false.
					//Todo coordenador irá gerenciar os participantes independentemente do atributo.
					membroEquipe.setGerenciaParticipantes(true);
				} else {
					membroEquipe.setGerenciaParticipantes(docenteGerenciaParticipantes);
				}
				
				membroEquipe.setServidor(getGenericDAO().findByPrimaryKey(
						docente.getId(), Servidor.class));					
				// evitando erro de lazy na validação
				if (membroEquipe.getServidor().getEscolaridade() != null) {
					membroEquipe.getServidor().getEscolaridade().getId();
				}
				getCurrentSession().setAttribute("aba", "membro-docente");
				
				
				ListaMensagens lista = new ListaMensagens();
				MembroProjetoValidator.verificaRelatorioPendenteDocente(membroEquipe, lista);
				if( ! lista.isEmpty()) {
					addMensagens(lista);
					return null;
				}	
			}

			if ((CategoriaMembro.SERVIDOR == categoriaMembro)
					|| (CategoriaMembro.DOCENTE == categoriaMembro)) {
				membroEquipe.setPessoa(membroEquipe.getServidor().getPessoa());
			}

			MembroProjetoValidator.validaServidor(obj.getMembrosEquipe(), membroEquipe, mensagens);
			MembroProjetoValidator.validaChTotalMembroProjeto(obj.getMembrosEquipe(), membroEquipe, mensagens);
		}

		// Validações finais
		MembroProjetoValidator.validaPessoaDupla(getMembrosEquipe(), membroEquipe, mensagens);
		MembroProjetoValidator.validaCooordenacaoDupla(obj.getProjeto(), membroEquipe, mensagens);

		// Permissão para prograd cadastrar membro externo como coordenador retirada.
		//if ((membroEquipe.isCoordenador()) && (!isPermitidoExternoCoordenar())) {			
		if (membroEquipe.isCoordenador()) {
		    if (obj.isFinanciamentoInterno() && (!ValidatorUtil.isEmpty(obj.getEditalExtensao()))) {
		    	//Evitando erro de lazy
		    	obj.setEditalExtensao(dao.findByPrimaryKey(obj.getEditalExtensao().getId(), EditalExtensao.class));
		    	obj.getEditalExtensao().getRegras().iterator();
		    	MembroProjetoValidator.validaCoordenacaoSimultaneaExtensao(membroEquipe, obj, mensagens);
		    }				
		    MembroProjetoValidator.validaCoordenacaoSimultaneaExtensao(obj.getTipoAtividadeExtensao(), membroEquipe, mensagens);
		    MembroProjetoValidator.validaCoordenacao(membroEquipe, mensagens);
		}

		// Informa qual tipo de membro de equipe será cadastrado
		getCurrentSession().setAttribute("categoriaAtual", categoriaMembro);
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		// Verifica se a pessoa externa já está na base de dados (pelo
		// cpf_cnpj) se não estiver, inclui...
		// se tiver, retorna a pessoa encontrada no banco se for extrangeiro
		// inclui sempre
		if (CategoriaMembro.EXTERNO == categoriaMembro) {
			Pessoa pessoa = adicionaPessoaExterna(membroEquipe
					.getParticipanteExterno().getPessoa());
			membroEquipe.setPessoa(pessoa);
			membroEquipe.getParticipanteExterno().setPessoa(pessoa);
		}

		if ( verificaDuplicidadePessoa(membroEquipe) ) {

			if ((CategoriaMembro.SERVIDOR == categoriaMembro)
					|| (CategoriaMembro.DOCENTE == categoriaMembro)) {
				membroEquipe.setParticipanteExterno(null);
			}
			if (CategoriaMembro.EXTERNO == categoriaMembro) {
				membroEquipe.setServidor(null);
			}

			// Em extensão utilizamos DiscenteExtensao e Participante externo
			membroEquipe.setDiscente(null);
			membroEquipe.setDocenteExterno(null);
			membroEquipe.setDataInicio(obj.getDataInicio());
			membroEquipe.setDataFim(obj.getDataFim());
			membroEquipe.setAtivo(true);
			obj.addMembroEquipe(membroEquipe);

			gravarTemporariamente();

		} else {
			addMensagemErro("Membro já inserido na atividade");
		}
		
		membrosEquipe = null;
		// Limpa os dados
		popularDadosMembroProjeto();
		return null;
	}

    /**
     * Prepara os dados para o cadastro de um novo Membro de um Projeto.
     * <br>
     * Método não invocado por JSP.
     * 
     */
	private void popularDadosMembroProjeto() {
		cpf = "";
		docente = new Servidor();
		discente = new Discente();
		servidor = new Servidor();
		participanteExterno = new ParticipanteExterno();

		membroEquipe = new MembroProjeto();
		membroEquipe.setProjeto(this.obj.getProjeto());
		membroEquipe.setFuncaoMembro(new FuncaoMembro());
		membroEquipe.setDocenteExterno(new DocenteExterno());
		membroEquipe.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
	}

    /**
     * Faz a busca de um participante externo de acordo com o CPF.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/membros_equipe.jsp</li>
     * </ul>
     * @throws NumberFormatException 
     * @throws DAOException 
     */
	public void buscarParticipanteExternoByCPF() throws DAOException, NumberFormatException {

		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		try {

			// Limpa os dados
			docente = new Servidor();
			servidor = new Servidor();
			membroEquipe = new MembroProjeto();
			membroEquipe.setProjeto(this.obj.getProjeto());
			membroEquipe.setFuncaoMembro(new FuncaoMembro());
			membroEquipe.setParticipanteExterno(new ParticipanteExterno());
			membroEquipe.setRegistroEntrada(getUsuarioLogado()
					.getRegistroEntrada());
			String nomeQuandoNaoEncontrado = participanteExterno.getPessoa().getNome(); 
			
			if (participanteExterno == null) {
				participanteExterno = new ParticipanteExterno();
			}

			// Pessoa internacional
			if (participanteExterno.getPessoa().isInternacional()) {
				Pessoa p1 = new Pessoa();
				p1.setNome(nomeQuandoNaoEncontrado);
				p1.setInternacional(true);
				participanteExterno.setPessoa(p1);
				membroEquipe.setParticipanteExterno(participanteExterno);
				// Permite a edição do nome da pessoa pelo usuário
				membroEquipe.setSelecionado(true);
				cpf = "";
			} else {
				// Permite a edição do nome da pessoa pelo usuário na view
				membroEquipe.setSelecionado(false);
				participanteExterno.getPessoa().setInternacional(false);
			}

			if ((cpf != null) && (!cpf.trim().equals(""))) {
				avisosAjax.clear();

				if (!ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf)) {
					getAvisosAjax().add(
							new MensagemAviso("CPF inválido!",
									TipoMensagemUFRN.ERROR));
				} else {
					// busca a pessoa informada pelo cpf
					String cpfFormatado = Formatador.getInstance()
							.parseStringCPFCNPJ(cpf);
					Pessoa p = pessoaDao
							.findByCpf(Long.parseLong(cpfFormatado));
					participanteExterno.setPessoa(p);

					if (participanteExterno.getPessoa() == null) {
						p = new Pessoa();
						p.setNome(nomeQuandoNaoEncontrado);
						p.setCpf_cnpjString(cpf);
						participanteExterno.setPessoa(p);
						membroEquipe
								.setParticipanteExterno(participanteExterno);
						// Permite a edição do nome da pessoa pelo usuário
						membroEquipe.setSelecionado(true);

					} else {
						// Não permite inclusão do nome da pessoa
						membroEquipe.setSelecionado(false);
					}
				}
			}
		} finally {
			pessoaDao.close();
		}
		getCurrentSession().setAttribute("aba", "membro-externo");
		getCurrentSession().setAttribute("categoriaAtual",
				CategoriaMembro.EXTERNO);
		redirectMesmaPagina();
	}

    /**
     * 
     * Adiciona uma pessoa externa à atividade de extensão.
     * 
     * 
     * @param pessoa {@link Pessoa} que será persistida
     * @return pessoa {@link Pessoa cadastrada} se cadastrou com sucesso e retorna Null se falhar.
     * @throws ArqException Gerada durante a chamada do processador.
     */
	private Pessoa adicionaPessoaExterna(Pessoa pessoa) throws ArqException {
    	checkChangeRole();

		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		// Busca pessoa por cpf, se não achar, entra para cadastrar...
		// estrangeiros não tem cpf, não tem como verificar se já está no banco.
		// se for estrangeiro, cadastra um novo
		if ((pessoa.isInternacional())
				|| ((pessoaDao.findByCpf(pessoa.getCpf_cnpj()) == null))) {
			pessoa.setTipo('F');
			try {
				ProjetoPesquisaValidator.limparDadosPessoa(pessoa);
				PessoaMov pessoaMov = new PessoaMov();
				pessoaMov.setPessoa(pessoa);
				pessoaMov.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
				pessoaMov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);
				prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA);
				pessoa = (Pessoa) execute(pessoaMov, getCurrentRequest());

			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				return null;
			} finally {
				// Repreparar o comando anterior
				prepareMovimento(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO);
				pessoaDao.close();
			}

		} else { // a pessoa já está no banco...
			Integer id = pessoaDao.findIdByCpf(pessoa.getCpf_cnpj());
			pessoa = pessoaDao.findByPrimaryKey(id, Pessoa.class);
		}

		return pessoa;

	}

    /**
     * Remove o docente/discente/participante externo da lista de membros da
     * ação de extensão.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/membros_equipe.jsp</li>
     * </ul>
     * @return Retorna para mesma página permitindo remover outro membro da equipe.
     * @throws SegurancaException 
     */
	public String removeMembroEquipe() throws SegurancaException {
    	checkChangeRole();

		int id = getParameterInt("idMembro", 0);
		MembroProjeto membro = new MembroProjeto(id);
		// já tá no banco
		if (id > 0) {
			try {
				membro = getGenericDAO().findByPrimaryKey(membro.getId(), MembroProjeto.class);
				if (membro == null) {
				    addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				    return null;
				}
				
				erros = obj.validateRemocaoMembroAtividade(getObjetivos(), membro);
				if ( hasOnlyErrors() ) {
					addMensagens(erros);
					return null;
				}
				
				membro.getProjeto().getId();
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(membro);
				mov.setUsuarioLogado(getUsuarioLogado());

				Comando ultimoComando = getUltimoComando();
				prepareMovimento(SigaaListaComando.REMOVER_MEMBRO_PROJETO_BASE);
				mov.setCodMovimento(SigaaListaComando.REMOVER_MEMBRO_PROJETO_BASE);
				execute(mov, getCurrentRequest());
				addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
				prepareMovimento(ultimoComando);
				membrosEquipe = null;
				
				//Removendo da view.
				obj.getMembrosEquipe().remove(membro);
				if (membro.isCoordenador()) {
				    obj.getProjeto().setCoordenador(null);
				}
				popularDadosMembroProjeto();
				

			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
			} catch (ArqException e) {
				tratamentoErroPadrao(e);
			}
		} else { // transient pessoa ainda não está no banco
			int idPessoa = getParameterInt("idPessoa", 0);
			membro.setPessoa(new Pessoa(idPessoa));
		}

		return null;
	}

    /**
     * Mantém o estado do botão que seleciona a despesa (botão permanece
     * selecionado) entre uma inclusão e outra de uma despesa.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/orcamento.jsp</li>
     * </ul>
     * @return Retorna para mesma página permitindo a inclusão de novo item de orçamento.
     */
	public String getIniciarOrcamento() {
		Integer idElementoDespesa = 0;
		idElementoDespesa = getParameterInt("idElementoDespesa");

		if ((idElementoDespesa != null) && (idElementoDespesa > 0)) {
			return null;
		}

		if ((orcamento != null) && (orcamento.getElementoDespesa() != null)) {
			getCurrentSession().setAttribute("idElementoDespesa",
					orcamento.getElementoDespesa().getId());
		} else {
			getCurrentSession().setAttribute("idElementoDespesa", 0);
		}

		recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());
		return null;
	}

    /**
     * Adiciona um orçamento à lista.
     * <br />
     *  Método chamado pela(s) seguinte(s) JSP(s):
     *  <ul>
     *   <li>sigaa.war/extensao/Atividade/orcamento.jsp</li>
     * </ul>
     * @return Retorna para mesma tela permitindo inclusão de novo elemento.
     * @throws ArqException 
     */
	public String adicionaOrcamento() throws ArqException {
    	checkChangeRole();
		try {
			ListaMensagens mensagens = new ListaMensagens();
			Integer idElementoDespesa = getParameterInt("idElementoDespesa", 0);
			orcamento.setElementoDespesa(getGenericDAO().findByPrimaryKey(idElementoDespesa, ElementoDespesa.class));
			orcamento.setAtivo(true);
			
			// Mantem o botão precionado
			getCurrentRequest().getSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
			
			if ((idElementoDespesa == null) || (idElementoDespesa == 0)) {
				addMensagemErro("Elemento de Despesa é Obrigatório: Selecione um elemento de despesa");
				return null;
			}
			
			if (orcamento.getValorUnitario() == null){
				addMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_ZERO, "Valor Unitário");
				return null;
			}

			AtividadeExtensaoValidator.validaAdicionaOrcamento(obj, orcamento, mensagens);
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
		}

		if (!obj.getOrcamentosDetalhados().contains(orcamento)) {
			obj.addOrcamentoDetalhado(orcamento);
		} else {
			addMensagemErro("Despesa já inserida na atividade");
		}

		// Prepara para novo item do orçamento
		getCurrentSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
		orcamento = new OrcamentoDetalhado();
		gravarTemporariamente();
		recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());

		return redirectMesmaPagina();
	}

    /**
     * Remove um orçamento da lista de orçamentos.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/orcamento.jsp</li>
     * </ul>
     * @return Retorna para mesma página permitindo remoção de novo elemento
     * @throws DAOException Gerado na busca de todos os itens do orçamento buscados no banco.
     * @throws SegurancaException 
     */
	public String removeOrcamento() throws DAOException, SegurancaException {
    	checkChangeRole();

		int id = getParameterInt("idOrcamentoDetalhado", 0);
		orcamento = getGenericDAO().findByPrimaryKey(id, OrcamentoDetalhado.class);

		// Orçamento não localizado no banco
		if (orcamento == null) {
			orcamento = new OrcamentoDetalhado();
		} else {

			remover(orcamento); // Remove do banco de dados

			// Verificar se ainda existem entradas do mesmo tipo para remover do
			// resumo...
			OrcamentoDao dao = getDAO(OrcamentoDao.class);
			Collection<OrcamentoDetalhado> o = dao.findByProjetoElementoDespesa(orcamento.getProjeto().getId(), orcamento.getElementoDespesa().getId());

			if ((o == null) || (o.size() == 0)) {
				for (Iterator<OrcamentoConsolidado> it = obj.getOrcamentosConsolidados().iterator(); it.hasNext();) {
					OrcamentoConsolidado oc = it.next();
					if (oc.getElementoDespesa().getId() == orcamento.getElementoDespesa().getId()) {
						remover(oc); // Remove do banco
						it.remove(); // Remove da collection
					}
				}
			}

			obj.getOrcamentosDetalhados().remove(orcamento);
			orcamento = new OrcamentoDetalhado();
			recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());
		}

		return redirectMesmaPagina();

	}

    /**
     * Método usado para avançar da tela de adição de servidores. Vai para a
     * tela de adição de discentes caso seja cadastro de projeto, e se for
     * cadastro de curso vai para a tela de adição de orçamento detalhado
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/membros_equipe.jsp</li>
     * </ul>
     * @return Próxima tela do fluxo do cadastro da ação em questão.
     * @throws DAOException
     */
	public String submeterServidores() throws DAOException {

		ListaMensagens mensagens = new ListaMensagens();
		
		
		/*
		 * Aqui não precisa validar se o coordenador está ativo (dentro do período), só precisa validar sem tem coordenador
		 */
		
		boolean coordenadorOK = false;
		
		if(obj.getMembrosEquipe() != null){
        		for (MembroProjeto membro : obj.getMembrosEquipe()) {
        		    if(membro.isCoordenador()){
        		    	membro.setDataInicio(obj.getProjeto().getDataInicio());
        		    	membro.setDataFim(obj.getProjeto().getDataFim());
        		    	coordenadorOK = true;
        		    }
        		}
		}
		
		if (!coordenadorOK) {
			mensagens.addMensagem(MensagensExtensao.COORDENADOR_ACAO);
		}
		
		
		//MembroProjetoValidator.validaCoordenacaoAtiva(obj.getMembrosEquipe(), mensagens);
		
		obj.setTotalDiscentes(AtividadeExtensaoHelper.getQtdeMembrosProjetoByCategoria(CategoriaMembro.DISCENTE));
		
		
		ValidatorUtil.validateEmptyCollection("É necessário informar pelo menos um membro.", obj.getMembrosEquipe(), mensagens);
		
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}

		return proximoPasso();
	}

    /**
     * Método usado para tela de adição de orçamento consolidado detalhado.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     * <li>sigaa.war/extensao/Atividade/orcamento.jsp</li>
     * </ul>
     * @return Próxima tela do fluxo do cadastro da ação em questão.
     * @throws DAOException 
     */
	public String submeterOrcamentoDetalhado() throws DAOException {
	    if (obj.getId() != 0) {
		    Collection<OrcamentoConsolidado> orcamentosNoBanco = getGenericDAO().findByExactField(OrcamentoConsolidado.class, "projeto.id", obj.getProjeto().getId()); 
		    obj.setOrcamentosConsolidados(orcamentosNoBanco);
	    }

	    // Percorre a tabela orçamentária e cadastra as rúbricas que ainda não
	    // foram consolidadas
	    for (Entry<ElementoDespesa, ResumoOrcamentoDetalhado> entrada : tabelaOrcamentaria.entrySet()) {
	    	boolean rubricaConsolidada = false;

	    	for (OrcamentoConsolidado consolidacao : obj.getOrcamentosConsolidados()) {
	    		if (entrada.getKey().getId() == consolidacao.getElementoDespesa().getId()) {
	    			rubricaConsolidada = true;
	    			consolidacao.setTotalOrcamento(entrada.getValue().getValorTotalRubrica());
	    		}
	    	}

	    	if (!rubricaConsolidada) {
	    		OrcamentoConsolidado consolidacao = new OrcamentoConsolidado(entrada.getKey(), obj.getProjeto());
	    		consolidacao.setTotalOrcamento(entrada.getValue().getValorTotalRubrica());

	    		// Adiciona uma nova consolidação de orçamento
	    		obj.getOrcamentosConsolidados().add(consolidacao);
	    	}
	    }

	    return proximoPasso();
	}

    /**
     * Persiste dados da tela de orçamento consolidado e segue para próxima tela do fluxo.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     * <li>sigaa.war/extensao/Atividade/orcamento_consolidado.jsp</li>
     * </ul>
     * @return Próxima tela do fluxo do cadastro da ação em questão.
     * @throws DAOException 
     */
	public String submeterOrcamentoConsolidado() throws DAOException {
		ListaMensagens mensagens = new ListaMensagens();
		AtividadeExtensaoValidator.validaOrcamento(obj, mensagens);
		if(obj.isFinanciamentoInterno()){
			AtividadeExtensaoValidator.validaLimiteMaxOrcamento(obj, mensagens);
		}
		
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		
		// Determina se possui convênio com a Funpec.
		for (OrcamentoConsolidado orcamentoConsolidado : obj.getOrcamentosConsolidados()) {
			if (orcamentoConsolidado.getFundacao() > 0){
				obj.setConvenioFunpec(true);
				break;
			}
			else {
				obj.setConvenioFunpec(false);
			}
		}
		
		return proximoPasso();
	}

    /**
     * Persiste dados da tela da arquivos submetidos e segue para próxima tela do fluxo.
     *  <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/anexar_arquivos.jsp</li>
     * </ul>
     * @return Próxima tela do fluxo do cadastro da ação em questão.
     */
	public String submeterArquivos() {
		return proximoPasso();
	}

    /**
     * Persiste dados da tela de fotos e segue para próxima tela do fluxo.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/anexar_fotos.jsp</li>
     * </ul>
     * @return Próxima tela do fluxo do cadastro da ação em questão.
     */
	public String submeterFotos() {
		return proximoPasso();	
	}

    /**
     * Envia a atividade para apreciação dos departamentos envolvidos. somente
     * após a aprovação de todos departamentos dos docentes envolvidos na
     * atividade é que ela será encaminhada à pró-reitoria de extensão para ser
     * avaliada pelo comitê de extensão.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/resumo.jsp</li>
     * </ul>
     * 
     * @return Tela do principal do sistema de extensão.
     * @throws ArqException Gerada na execução da chamada ao processador.
     * 
     */
	public String submeterAtividadeDepartamentosEnvolvidos() throws ArqException {
		
		//Verifica se a ação ja foi executada
		Integer op = (Integer) getCurrentSession().getAttribute("operacaoAtiva");
		if ( op != null && op.equals(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO.getId()) ) {
		
			obj.getProjeto().setEquipe( getMembrosEquipe() );
			prepareMovimento(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO);
			CadastroExtensaoMov mov = new CadastroExtensaoMov();
			mov.setCodMovimento(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO);
			mov.setAtividade(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setObjAuxiliar(controleFluxo);
	
			try {
				execute(mov, getCurrentRequest());
				limparMBeans();
				if (mov.getCodMovimento().equals(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO)) {
					Boolean chefeDiretorProponente = ProjetoHelper.isChefeUnidadeProponenteProjeto(mov.getUsuarioLogado(), obj.getProjeto());
					
					// Se for registro a atividade ou se o autor da atividade
					// for um diretor de centro ou chefe de departamento da unidade proponente da proposta, 
					// a mesma vai direto para proex...
					if (chefeDiretorProponente) {
						addMessage("Esta ação de extensão não foi submetida a aprovação dos departamentos porque "
										+ "seu autor é Chefe ou Diretor de sua Unidade Proponente.",
								TipoMensagemUFRN.INFORMATION);
						addMessage("Ação submetida diretamente para PROEx com sucesso.", TipoMensagemUFRN.INFORMATION);
					} else {
						addMessage("Ação submetida à aprovação dos departamentos envolvidos.", TipoMensagemUFRN.INFORMATION);
						if (obj.isFinanciamentoExterno())
							return forward(ConstantesNavegacao.CONTINUAR_PROPLAM);
					}
				}
	
			} catch (Exception e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
			}
			
			setOperacaoAtiva(null);
			return getSubSistema().getForward();
		}else {
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "Operação", "executada");
			return cancelar();
		}

	}

    /**
     * Cancela a operação de cadastro limpando os objetos em sessão.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     * <li>sigaa.war/extensao/AlterarAtividade/form_situacao_atividade.jsp</li>
     * <li>sigaa.war/extensao/Atividade/anexar_arquivos.jsp</li>
     * <li>sigaa.war/extensao/Atividade/anexar_fotos.jsp</li>
     * <li>sigaa.war/extensao/Atividade/atividades.jsp</li>
     * <li>sigaa.war/extensao/Atividade/curso_evento.jsp</li>
     * <li>sigaa.war/extensao/Atividade/dados_gerais.jsp</li>
     * <li>sigaa.war/extensao/Atividade/membros_equipe.jsp</li>
     * <li>sigaa.war/extensao/Atividade/objetivos_esperados.jsp</li>
     * <li>sigaa.war/extensao/Atividade/orcamento_consolidado.jsp</li>
     * <li>sigaa.war/extensao/Atividade/orcamento.jsp</li>
     * <li>sigaa.war/extensao/Atividade/produto.jsp</li>
     * <li>sigaa.war/extensao/Atividade/programa.jsp</li>
     * <li>sigaa.war/extensao/Atividade/projeto.jsp</li>
     * <li>sigaa.war/extensao/Atividade/remover.jsp</li>
     * <li>sigaa.war/extensao/Atividade/resumo.jsp</li>
     * <li>sigaa.war/extensao/PrestacaoServico/descricao.jsp</li>
     * <li>sigaa.war/extensao/form_busca_atividade.jsp</li>
     * <li>sigaa.war/extensao/lista_atividades_periodo_conclusao.jsp</li>
     * </ul>
     * @return Retorna para tela princial do sistema de extensão.
     */
    @Override
    public String cancelar() {
    	limparMBeans();	
    	return super.cancelar();
    }

    /**
     * Método chamado para iniciar a alteração de uma atividade de extensão. 
     * Vai para a primeira tela do cadastro (dados gerais).
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     * 	<li>sigaa.war/extensao/Atividade/lista_atividades_pendentes.jsp </li>
     * 	<li>sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp </li>
     * <ul>
     * @return Retorna a página principal do cadastro de ações de extensão.
     * @throws ArqException 
     */
	public String preAtualizar() throws ArqException {

		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		try {
		    	if (getCurrentRequest().getAttribute("existeAcao") == null) {
		    	    clear();
		    	    int id = getParameterInt("id", 0);
		    	    obj.setId(id);
		    	}
		    	
		    	this.obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
		    	getGenericDAO().refresh(obj.getProjeto().getUnidade());
		    	obj.getLocaisRealizacao().iterator();
		    	obj.getProjeto().getEquipe().iterator();
		    	locaisRealizacao = new ListDataModel();
		    	membrosEquipe = null;
		    	
			// evitar erro de lazy
			obj.getProjeto().getCronograma().iterator();
			
			if(getCurrentURL().contains("/sigaa/extensao/AlterarAtividade/lista"))
		    	    obj.setEdicaoGestor(true);
		    	else
		    	    obj.setEdicaoGestor(false);
		    	

			switch (obj.getTipoAtividadeExtensao().getId()) {
			case TipoAtividadeExtensao.PROGRAMA:
				ProgramaMBean programaExtensao = (ProgramaMBean) getMBean("programaExtensao");
				programaExtensao.setObj(obj);
				if (obj.getProjeto().getCoordenador() == null) {
				    obj.getProjeto().setCoordenador(new MembroProjeto());
				}
				break;

			case TipoAtividadeExtensao.PROJETO:
				((ProjetoExtensaoMBean) getMBean("projetoExtensao")).setObj(obj);
				break;

			case TipoAtividadeExtensao.PRODUTO:
				((ProdutoMBean) getMBean("produtoExtensao")).setObj(obj);
				break;

			case TipoAtividadeExtensao.CURSO:
			case TipoAtividadeExtensao.EVENTO:
				((CursoEventoMBean) getMBean("cursoEventoExtensao"))
						.setObj(obj);
				break;

			case TipoAtividadeExtensao.PRESTACAO_SERVICO:
				break;

			default:
				break;
			}

			prepareMovimento(SigaaListaComando.ALTERAR_ATIVIDADE_EXTENSAO);

			// Inicializar atributos lazy
			obj.getOrcamentosDetalhados().iterator();
			obj.getOrcamentosConsolidados().iterator();
			getIniciarOrcamento();

			AtividadeExtensaoHelper.instanciarAtributos(getObj());

			// Inicializar controle de fluxo do cadastro
			if (obj.isProjetoAssociado()) {
			    controleFluxo = new ControleFluxoAtividadeExtensao(obj.getTipoAtividadeExtensao().getId() + ControleFluxoAtividadeExtensao.CONST_ACAO_ASSOCIADA, obj.isRegistro());
			}else {
			    controleFluxo = new ControleFluxoAtividadeExtensao(obj.getTipoAtividadeExtensao().getId(), obj.isRegistro());
			}


		} finally {
			dao.close();
		}

		return forward(ConstantesNavegacao.DADOS_GERAIS);
	}

    /**
     * Limpa os outros MBeans (ProjetoMBean, ProdutoMBean, CursoEventoMBean,
     * ProgramMBean) utilizados durante o caso de uso.
     * 
     */
	private void limparMBeans() {

		switch (obj.getTipoAtividadeExtensao().getId()) {
		case TipoAtividadeExtensao.PROJETO:
			ProjetoExtensaoMBean projetoMBean = (ProjetoExtensaoMBean) getMBean("projetoExtensao");
			projetoMBean.setObj(new AtividadeExtensao());
			break;

		case TipoAtividadeExtensao.CURSO:
		case TipoAtividadeExtensao.EVENTO:
			CursoEventoMBean cursoEventoMBean = (CursoEventoMBean) getMBean("cursoEventoExtensao");
			cursoEventoMBean.setObj(new AtividadeExtensao());
			break;

		case TipoAtividadeExtensao.PRODUTO:
			ProdutoMBean produtoMBean = (ProdutoMBean) getMBean("produtoExtensao");
			produtoMBean.setObj(new AtividadeExtensao());
			break;

		case TipoAtividadeExtensao.PROGRAMA:
			ProgramaMBean programaMBean = (ProgramaMBean) getMBean("programaExtensao");
			programaMBean.setObj(new AtividadeExtensao());
			if (obj.getProjeto().getCoordenador() == null) {
			    obj.getProjeto().setCoordenador(new MembroProjeto());
			}
			
			break;
		default:
			break;
		}
	}

    /**
     * Método que carrega as configurações necessárias para a página de busca.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *   <li>sigaa.war/extensao/menu_ta.jsp</li>
     *  <li>sigaa.war/extensao/menu.jsp</li> 
     * </ul>
     * @return Tela de busca geral de ações de extensão
     */
	public String preLocalizar() {
		atividadesLocalizadas = new ArrayList<AtividadeExtensao>();
		buscaAno = CalendarUtils.getAnoAtual();
		botaoRelatorioContatoCoordenador = false;
		return forward(ConstantesNavegacao.LISTA);
	}
    
    /**
     * Método que carrega as configurações necessárias para a página de busca.
     * Utilizado quando for realizar a busca para exibir um relatório com os
     * dados do Coordenador de Projeto.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/menu.jsp</li> 
     * </ul>
     * @return Retorna formulário de busca de dados dos coordenadores de ações de extensão.
     */
	public String preLocalizarRelatorioCoordenador() {
		buscaAno = CalendarUtils.getAnoAtual();
		atividadesLocalizadas = new ArrayList<AtividadeExtensao>();
		botaoRelatorioContatoCoordenador = true;
		return forward(ConstantesNavegacao.LISTA);
	}
	
    /**
     * Verifica se um código passado por parâmetro é válido.
     * <br />
     * <ul>
     *  	<li>Método não invocado por JSP´s</li>
     * </ul>
     * @param codigo
     * @return
     */
    public Boolean isCodigoAcaoValido(String codigo) {
    	//Exemplo de código válido: CRxxx-2009
    	//Exemplo de código válido: CR021-2009
    	//Exemplo de código válido: CR015621-2009
    	//Exemplo de código válido: CR015...Infinitos números...621-2009
    	if(codigo == null || codigo.length() < 10)
    		return false;
    	
    	String prefixo = codigo.substring(0, 2);    	
    	if(!prefixo.equals("CR") && !prefixo.equals("EV") && !prefixo.equals("PD") &&
    	   !prefixo.equals("PG") && !prefixo.equals("PJ"))
    		return false;
    	
    	
    	int indiceInicioAno = codigo.length()-4;
    	String ano = codigo.substring(indiceInicioAno);
    	
    	    	
    	if(codigo.charAt(indiceInicioAno-1)!= '-')
    		return false;
    	
    	int indiceInicioMeio = 2; 
    	int indiceFimMeio = indiceInicioAno-2;
    	String meio = codigo.substring(indiceInicioMeio, indiceFimMeio+1);
    	
    	try {
    		Integer.parseInt(ano);    		
    	}
    	catch (NumberFormatException e) {
			return false;
		}
    	
    	try {    		
    		Integer.parseInt(meio);
    	}
    	catch (NumberFormatException e) {
			if( !meio.equalsIgnoreCase("xxx"))
				return false;					
		}
    	
    	return true;
    }
    
    /**
     * Retorna o tipo de uma atividade de extensão de acordo com o
     * código da ação.
     * <br />
     * <ul>
     *  	<li>Método não invocado por JSP´s</li>
     * </ul>
     * 
     * @param codigo
     * @return
     */
    public TipoAtividadeExtensao tipoAtividadeByCodigo(String codigo) {
    	
    	if( !isCodigoAcaoValido(codigo) )
    		return null;
    	
    	String prefixo = codigo.substring(0, 2);
    	
    	if(prefixo.equals("CR"))
    		return new TipoAtividadeExtensao(TipoAtividadeExtensao.CURSO);
    	else if (prefixo.equals("EV"))
    		return new TipoAtividadeExtensao(TipoAtividadeExtensao.EVENTO);
    	else if (prefixo.equals("PD"))
    		return new TipoAtividadeExtensao(TipoAtividadeExtensao.PRODUTO);
    	else if (prefixo.equals("PG"))
    		return new TipoAtividadeExtensao(TipoAtividadeExtensao.PROGRAMA);
    	else if (prefixo.equals("PJ"))
    		return new TipoAtividadeExtensao(TipoAtividadeExtensao.PROJETO);    	
    	
    	return null;
    }
    
    /**
     * Retorna o ano de uma atividade de extensão de acordo com 
     * o código da ação.
     * <br />
     * <ul>
     *  	<li>Método não invocado por JSP´s</li>
     * </ul>
     * @param codigo
     * @return
     */
    public Integer anoByCodigo(String codigo) {
    	
    	if( !isCodigoAcaoValido(codigo) )
    		return null;
    	
    	String ano = codigo.substring(codigo.length()-4);
    	return Integer.parseInt(ano);    	
    }
    
    /**
     * Retorna a sequência de uma atividade de extensão de acordo com 
     * o código da ação.
     * <br />
     * <ul>
     *  	<li>Método não invocado por JSP´s</li>
     * </ul>
     * @param codigo
     * @return
     */
    public String sequenciaByCodigo(String codigo) {
    	if( !isCodigoAcaoValido(codigo) )
        	return null;
    	
    	int indiceInicioAno = codigo.length()-4;
    	int indiceInicioMeio = 2; 
    	int indiceFimMeio = indiceInicioAno-2;
    	String meio = codigo.substring(indiceInicioMeio, indiceFimMeio+1);
    	return meio;
    	
    }   

    /***
     * Realiza a busca por ações de extensão 
     * que tem data de conclusão no período informado.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/extensao/Atividade/lista_atividades_periodo_conclusao.jsp</li>
     * </ul>
     * @return
     */
    public String buscarAtividadesPeriodoConclusao() {	
    	Date inicioConclusao = null;
    	Date fimConclusao = null;
    	
    	if (atividadesLocalizadas!= null) {
    		atividadesLocalizadas.clear();	
    	}

    	inicioConclusao = buscaInicioConclusao;
    	fimConclusao = buscaFimConclusao;

    	ListaMensagens msg = new ListaMensagens();
    	ValidatorUtil.validateRequired(inicioConclusao, "Data Início", msg);
    	ValidatorUtil.validateRequired(fimConclusao, "Data Fim", msg);
    	if ((inicioConclusao != null) && (fimConclusao != null)) {
    		ValidatorUtil.validaOrdemTemporalDatas(inicioConclusao, fimConclusao, true,"Data de Início", msg);
    	}

    	if(!msg.isEmpty()) {
    		addMensagens(msg);
    		return null;
    	}

    	try {
    		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
    		atividadesLocalizadas = dao.findByPeriodoConclusao(inicioConclusao, fimConclusao);	    
    	} catch (Exception e) {
    		notifyError(e);
    	}

    	return redirectMesmaPagina();    	
    }
    
    /**
     * Redireciona para uma página ao qual usuário pode notificar os
     * coordenadores pendentes quanto ao envio de relatórios finais. Na página
     * o usuário pode deixar a mensagem de notificação padrão ou alterar
     * a mensagem a ser enviada.
     *  <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/lista_atividades_periodo_conclusao.jsp</li>
     *  </ul>
     * @return
     */
    public String redirecionarPaginaNotificacaoCoordenador() {
    	try {
    		
    		if( atividadesLocalizadas == null ||  atividadesLocalizadas.isEmpty()) {
    			addMensagemWarning("Você utilizou o botão 'Voltar' do navegador, por favor realize uma nova busca.");
    			return  null;    			
    		}
    		
    		
    		boolean marcou = false;    		
        	for (AtividadeExtensao a : atividadesLocalizadas) {
        		if (a.isSelecionado()) {
        			marcou = true;
        			break;
        		}
        	}
        	if (marcou) {
        		
        		msgCoordenadorPendenteRelatorio = UFRNUtils.getMensagem(MensagensExtensao.COORDENADOR_PENDENTE_RELATORIO).getMensagem();        		
        		prepareMovimento(SigaaListaComando.ENVIAR_EMAIL_COORDENADOR_ACAO_SEM_RELATORIO);
        		return forward(ConstantesNavegacao.NOTIFICACAO_AUSENCIA_RELATORIO);
        	}        	
        	else {
        		addMensagemErro("Nenhuma Ação de Extensão foi selecionada.");
        		return null; 
        	}    		
    	}
    	catch (Exception e) {
    		notifyError(e);
		}    	    	
    	return null;
    } 
    
    /**
     * Usado para enviar e-mails para coordenadores que não enviou relatório final.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/RelatorioAcaoExtensao/notificacao_ausencia_rel.jsp</li>
     * </ul>
     * @throws NegocioException 
     * @throws ArqException 
     * @throws SegurancaException 
     * 
     */
    public String notificacaoFaltaDeRelatorioFinal() throws SegurancaException, ArqException, NegocioException {
    	
    		CadastroExtensaoMov mov = new CadastroExtensaoMov();		
    		mov.setAtividades(atividadesLocalizadas);
    		// Removendo comentários inseridos quando se copia e cola conteúdos de arquivos do Word.
    		mov.setMsgCoordenadorPendenteRelatorio(StringUtils.removerComentarios(msgCoordenadorPendenteRelatorio));
    		mov.setCodMovimento(SigaaListaComando.ENVIAR_EMAIL_COORDENADOR_ACAO_SEM_RELATORIO);    		
    		execute(mov);
    		
    		obj = new AtividadeExtensao();
    		buscaInicioConclusao = null;
    		buscaFimConclusao = null;
    		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
    		return forward("/extensao/menu.jsp"); 		
    	
    }
    
    
    /**
     * Localiza projeto na tela de situação do projeto.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/form_busca_atividade.jsp</li> 
     * </ul>
     * @return Mesma tela com lista de ações de extensão localizadas.
     * 
     */
	public String localizar() {

	    	if (atividadesLocalizadas != null) {
	    	    atividadesLocalizadas.clear();
		}

		/* Analisando filtros selecionados */
		Integer sequencia = null;
		String titulo = null;
		Integer[] idTipoAtividade = new Integer[1];
		Integer idEdital = null;
		Integer[] idSituacaoAtividade = new Integer[0];
		Integer idUnidadeProponente = null;
		Integer idCentro = null;
		Integer idAreaCNPq = null;
		Integer idAreaTematicaPrincipal = null;
		Integer idServidor = null;
		Integer ano = null;
		Integer anoCodigo = null;
		Boolean financiamentoInterno = null;
		Boolean financiamentoExterno = null;
		Boolean autoFinanciamento = null;
		Boolean convenioFunpec = null;
		Boolean recebeuFinanciamentoInterno = null;
		Boolean registroSimplificado = null;
		Date inicio = null;
		Date fim = null;
		Date inicioConclusao = null;
		Date fimConclusao = null;
		Date inicioExecucao = null;
		Date fimExecucao = null;
		Boolean solicitcaoRenovacao = null;
		Boolean associado = null;

		ListaMensagens listaMensagens = new ListaMensagens();
		
		if (checkBuscaCodigo) {
		    	ValidatorUtil.validateRequired(buscaCodigo, "Código", listaMensagens);
		    	if (!ValidatorUtil.isEmpty(buscaCodigo)) {
		    	    if (!isCodigoAcaoValido(getBuscaCodigo())) {
		    		listaMensagens.addWarning("O código da ação digitado esta em formato inválido.");
		    	    } else {
		    		idTipoAtividade[0] = (tipoAtividadeByCodigo(getBuscaCodigo())).getId();
		    		anoCodigo = anoByCodigo(getBuscaCodigo());
		    		String meio = sequenciaByCodigo(getBuscaCodigo());

		    		if (meio.equalsIgnoreCase("xxx")) {
		    		    sequencia = 0;
		    		}else {
		    		    sequencia = Integer.parseInt(meio);
		    		}
		    	    }
		    	}
		}

		// Definição dos filtros e validações
		if (checkBuscaEdital) {
			idEdital = buscaEdital;
			if (idEdital == 0) {
			    listaMensagens.addErro("Edital: Campo obrigatório não informado.");
			}
		}
		if (checkBuscaTitulo) {
			titulo = buscaNomeAtividade;
			ValidatorUtil.validateRequired(titulo, "Título da Ação", listaMensagens);
		}
		if (checkBuscaTipoAtividade) {
			idTipoAtividade = buscaTipoAtividade;
			if(idTipoAtividade.length == 0){
				listaMensagens.addErro("Tipo da Ação: Campo obrigatório não informado.");
			}
		}
		if (checkBuscaSituacaoAtividade) {
			idSituacaoAtividade = buscaSituacaoAtividade;
			if (buscaSituacaoAtividade.length == 0) {
			    listaMensagens.addErro("Situação da Ação: Campo obrigatório não informado.");
			}
		}
		if (checkBuscaUnidadeProponente) {
			idUnidadeProponente = buscaUnidade;
			ValidatorUtil.validateRequiredId(idUnidadeProponente, "Unidade Proponente", listaMensagens);
		}
		if (checkBuscaAreaCNPq) {
			idAreaCNPq = buscaAreaCNPq;
			ValidatorUtil.validateRequiredId(idAreaCNPq, "Área do CNPq", listaMensagens);
		}
		if (checkBuscaAreaTematicaPrincipal) {
			idAreaTematicaPrincipal = buscaAreaTematicaPrincipal;
			ValidatorUtil.validateRequiredId(idAreaTematicaPrincipal, "Área Temática", listaMensagens);
		}
		if (checkBuscaServidor) {
			idServidor = membroEquipe.getServidor().getId();
			ValidatorUtil.validateRequired(membroEquipe.getServidor(), "Servidor", listaMensagens);
		}
		if (checkBuscaAno) {
			ano = buscaAno;
			ValidatorUtil.validaInt(ano, "Ano", listaMensagens);
		}
		if (checkBuscaPeriodo) {
			inicio = buscaInicio;
			fim = buscaFim;
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(inicio), "Período de execução: Data Início", listaMensagens);
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(fim), "Período de execução: Data Fim", listaMensagens);
			if ((inicio != null) && (fim != null)) {
				ValidatorUtil.validaOrdemTemporalDatas(inicio, fim, true, "Data de início deve ser menor que a data fim", listaMensagens);
			}
		}

		if (checkBuscaPeriodoConclusao) {
			inicioConclusao = buscaInicioConclusao;
			fimConclusao = buscaFimConclusao;
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(inicioConclusao), "Período de conclusão: Data Início", listaMensagens);
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(fimConclusao), "Período de conclusão: Data Fim", listaMensagens);
			if ((inicioConclusao != null) && (fimConclusao != null)) {
				ValidatorUtil.validaOrdemTemporalDatas(inicioConclusao, fimConclusao, true,
						"Data de início deve ser menor que a data fim", listaMensagens);
			}
		}

		if (checkBuscaPeriodoInicio) {
			inicioExecucao = buscaInicioExecucao;
			fimExecucao = buscaFimExecucao;
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(inicioExecucao), "Período de início: Data Início", listaMensagens);
			ValidatorUtil.validaData(Formatador.getInstance().formatarData(fimExecucao), "Período de início: Data Fim", listaMensagens);
			if ((inicioExecucao != null) && (fimExecucao != null)) {
				ValidatorUtil.validaOrdemTemporalDatas(inicioExecucao, fimExecucao, true,
						"Data de início deve ser menor que a data fim", listaMensagens);
			}
		}

		
		if (checkBuscaFinanciamentoConvenio) {
			financiamentoInterno = buscaFinanciamentoInterno;
			financiamentoExterno = buscaFinanciamentoExterno;
			autoFinanciamento = buscaAutoFinanciamento;
			convenioFunpec = buscaConvenioFunpec;
			recebeuFinanciamentoInterno = buscaRecebeuFinanciamentoInterno;
			if (!buscaFinanciamentoInterno && !buscaFinanciamentoExterno
					&& !buscaAutoFinanciamento && !buscaConvenioFunpec && !buscaRecebeuFinanciamentoInterno) {
				addMensagemErro("Financiamentos & Convênios: Campo obrigatório não informado.");
			}
		}
		if (checkBuscaRegistroSimplificado) {
			if (buscaRegistroSimplificado == 0) {
			    addMensagemErro("Tipo de Registro: Campo obrigatório não informado");
			}else {
			    registroSimplificado = buscaRegistroSimplificado == 1;
			}
		}
		if (checkBuscaCentro) {
			idCentro = buscaCentro;
			ValidatorUtil.validateRequiredId(idCentro, "Centro", listaMensagens);
		}
		
		if (checkBuscaAcaoSolicitacaoRenovacao) {
			if (buscaAcaoSolicitacaoRenovacao == 0) {
			    listaMensagens.addErro("Solicitação de Renovação: campo obrigatório não informado.");
			} else {
			    solicitcaoRenovacao = buscaAcaoSolicitacaoRenovacao == 1;
			}
		}
		
		if (checkBuscaProjetoAssociado) {
			if (buscaProjetoAssociado == 0) {
			    listaMensagens.addErro("Dimensão Acadêmica: campo obrigatório não informado.");
			} else {
			    associado = buscaProjetoAssociado == 1;
			}
		}
		
		if (!checkBuscaEdital && !checkBuscaTitulo && !checkBuscaAreaCNPq
				&& !checkBuscaTipoAtividade && !checkBuscaSituacaoAtividade
				&& !checkBuscaUnidadeProponente
				&& !checkBuscaAreaTematicaPrincipal && !checkBuscaServidor
				&& !checkBuscaAno && !checkBuscaFinanciamentoConvenio
				&& !checkBuscaRegistroSimplificado && !checkBuscaCentro
				&& !checkBuscaPeriodo && !checkBuscaCodigo 
				&& !checkBuscaPeriodoConclusao && !checkBuscaPeriodoInicio
				&& !checkBuscaAcaoSolicitacaoRenovacao && !checkBuscaProjetoAssociado) {

		    listaMensagens.addErro("Selecione uma opção para efetuar a busca por ações de extensão.");
		    if(listaMensagens != null){
		    	addMensagens(listaMensagens);
		    }

		} else {

			try {
				if (listaMensagens.isEmpty()) {
					AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
					PagingInformation paginas = null;

					// Só mostra cadastros em andamento para membros da proex
					Boolean verCadastroEmAndamento = getUsuarioLogado()
							.isUserInRole(
									SigaaPapeis.GESTOR_EXTENSAO,
									SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO,
									SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
									SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
									SigaaPapeis.APOIO_TECNICO_COORDENACAO_EXTENSAO);
					
					if (!botaoRelatorioContatoCoordenador) {
						atividadesLocalizadas = dao.filter(idEdital, inicio,
								fim, inicioConclusao, fimConclusao, inicioExecucao, fimExecucao, 
								titulo, idTipoAtividade,
								idSituacaoAtividade, idUnidadeProponente,
								idCentro, idAreaCNPq, idAreaTematicaPrincipal,
								null, idServidor, ano, financiamentoInterno,
								financiamentoExterno, autoFinanciamento,
								convenioFunpec, recebeuFinanciamentoInterno, paginas,
								verCadastroEmAndamento, registroSimplificado,
								sequencia, anoCodigo, solicitcaoRenovacao, associado, isExtensao() );

						if (atividadesLocalizadas == null
								|| atividadesLocalizadas.isEmpty())
							addMensagemWarning("Nenhuma atividade localizada!");
					}
					if (botaoRelatorioContatoCoordenador) {
						atividadesLocalizadas = dao
								.gerarDadosContatoCoordenador(idEdital, inicio,
										fim, titulo, idTipoAtividade,
										idSituacaoAtividade,
										idUnidadeProponente, idCentro,
										idAreaCNPq, idAreaTematicaPrincipal,
										idServidor, ano, financiamentoInterno,
										financiamentoExterno,
										autoFinanciamento, convenioFunpec, recebeuFinanciamentoInterno,
										paginas, verCadastroEmAndamento,
										registroSimplificado);

						return forward("/extensao/Atividade/relatorio_contato_coord.jsp");
					}
				} else {
					addMensagens(listaMensagens);
				}
			} catch (LimiteResultadosException lre) {
				addMensagemErro(lre.getMessage());
			} catch (DAOException e) {
				notifyError(e);
				addMensagemErro(e.getMessage());
			}
		}
		
		return forward(ConstantesNavegacao.ALTERAR_ATIVIDADE_LISTA);
	}

    /**
     * Exibe a lista de ações de extensão onde o servidor faz parte da
     * lista de membros da equipe.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     * <li>sigaa.war/extensao/menu_ta.jsp</li> 
     * <li>sigaa.war/portais/docente/menu_docente.jsp </li>
     * </ul>
     * 
     * @return Página com a lista de ações encontradas
     * @throws SegurancaException 
     */
	public String listarMinhasAtividades() throws SegurancaException, DAOException {

		if (getUsuarioLogado().getServidor() == null && !getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno()) {
		    throw new SegurancaException();
		}
		
		atualizarCadastrosEmAndamento();
		atualizarAtividadesUsuarioParticipa();
		
		return forward(ConstantesNavegacao.LISTA_MINHAS_ATIVIDADES);
	}
	
	
	/**
	 * 
	 * Força a atualização das atividades caso o parâmetro de request _forcaAtualizacaoAtividades = true
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String getAtualizaMinhasAtividades() throws DAOException{
		atualizarCadastrosEmAndamento();
		atualizarAtividadesUsuarioParticipa();
		return "";
	}
	
	
	/**
	 * Possibilita a visualização externa de dados detalhados da ação de extensão selecionada ou do 
     * orçamento aprovado da ação. Utilizado para integração com o SIGED.
     * <br>
     * Método não chamado por JSPs.
	 * @return
	 */
	public String viewExterno() {

		TokenGenerator token = getMBean("tokenGenerator");

		if (token.isTokenValid(getParameterInt("idToken", 0), getParameter("key"))) {
			token.invalidateToken(getParameterInt("idToken", 0));
			
			Integer id = getParameterInt("id", 0);

			if (id > 0) {

				try {

					atividadeSelecionada.setId(id);

					//Utilizado na visualização de ações de extensão a partir da página de Ações Acadêmicas Associadas.
					if (getParameterBoolean("ACAO_ASSOCIADA")) {
						int idTipoAcao = getParameterInt("TIPO_ACAO");
						atividadeSelecionada = getDAO(AtividadeExtensaoDao.class).findAcaoByProjetoAndTipoAcao(atividadeSelecionada.getId(), idTipoAcao);
					}

					atividadeSelecionada = getGenericDAO().findByPrimaryKey(atividadeSelecionada.getId(), AtividadeExtensao.class);
					Collection<OrcamentoDetalhado> orcamento = getGenericDAO().findByExactField(OrcamentoDetalhado.class, "projeto.id", atividadeSelecionada.getProjeto().getId());
					if(orcamento != null){
						atividadeSelecionada.setOrcamentosDetalhados(orcamento);
					}

					recalculaTabelaOrcamentaria(atividadeSelecionada.getOrcamentosDetalhados());

					// listar os discentes envolvidos na atividade ordenados por nome, evitando erro de LazyInicialization
					atividadeSelecionada.setDiscentesSelecionados(getDAO(DiscenteExtensaoDao.class).findByAtividade(atividadeSelecionada.getId()));
					PlanoTrabalhoExtensaoDao planoTrabalhoDao = getDAO(PlanoTrabalhoExtensaoDao.class);
					for (DiscenteExtensao de : atividadeSelecionada.getDiscentesSelecionados()) {
						de.getDiscente().getMatriculaNome();
						de.setPlanoTrabalhoExtensao(planoTrabalhoDao.findByDiscente(de.getId()));
					}

					// Carregando todos os objetivos pra evitar erro de lazy
					if ((atividadeSelecionada.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) && (atividadeSelecionada.getProjetoExtensao() != null)) {
						atividadeSelecionada.setObjetivo(getGenericDAO().findByExactField(Objetivo.class,	"projetoExtensao.id", atividadeSelecionada.getProjetoExtensao().getId()));

						for (Objetivo objetivo : atividadeSelecionada.getObjetivo()) {
							objetivo.getAtividadesPrincipais().iterator();
						}
					}

					atividadeSelecionada.setMembrosEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", atividadeSelecionada.getProjeto().getId()));
					// evitar erro de lazy na busca do coordenador
					for (MembroProjeto mp : atividadeSelecionada.getMembrosEquipe()) {
						mp.getId();
					}

					HistoricoSituacaoProjetoDao dao = getDAO(HistoricoSituacaoProjetoDao.class);
					atividadeSelecionada.getProjeto().setHistoricoSituacao(dao.relatorioHistoricoByIdProjeto(atividadeSelecionada.getProjeto().getId()));

					atividadeSelecionada.getAtividades().iterator();
					atividadeSelecionada.getAtividadesPai().iterator();
					atividadeSelecionada.getProjeto().getOrcamentoConsolidado().iterator();
					atividadeSelecionada.getProjeto().getArquivos().iterator();
					atividadeSelecionada.getProjeto().getFotos().iterator();
					atividadeSelecionada.getAutorizacoesDepartamentos().iterator();
					atividadeSelecionada.getUnidadesProponentes().iterator();
					atividadeSelecionada.getUnidade().getNome();
					atividadeSelecionada.getAreaConhecimentoCnpq().getNome();
					if (atividadeSelecionada.getProjeto().getUnidadeOrcamentaria() != null)
						atividadeSelecionada.getProjeto().getUnidadeOrcamentaria().getNome();
					for (AtividadeUnidade au : atividadeSelecionada.getUnidadesProponentes()) {
						au.getUnidade().getNome();
					}

					if (atividadeSelecionada.getLocalRealizacao() != null) {
						atividadeSelecionada.getLocalRealizacao().getMunicipioString();
					}

					return redirect("/extensao/Atividade/print.jsf");

				} catch (Exception e) {
					notifyError(e);
					addMensagemErro(e.getMessage());
					return null;
				}
			} else {
				addMensagemErro("Ação de extensao não selecionada.");
			}
		}

		return null;

	}
	

    /**
     * Visualiza dados detalhados da ação de extensão selecionada ou do 
     * orçamento aprovado da ação.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/extensao/AlterarAtividade/lista.jsp </li>
     * 	<li>sigaa.war/extensao/AnalisarSolicitacaoReconsideracao/lista.jsp </li>
     * 	<li>sigaa.war/extensao/AnalisarSolicitacaoReconsideracao/form.jsp </li>
     * 	<li>sigaa.war/extensao/Atividade/include/dados_avaliar_atividades.jsp </li>
     * 	<li>sigaa.war/extensao/Atividade/atividades.jsp  </li>
     * 	<li>sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp</li> 
     * 	<li>sigaa.war/extensao/Atividade/lista.jsp </li>
     *  <li>sigaa.war/extensao/AutorizacaoDepartamento/form_busca_autorizar_acoes.jsp</li> 
     *  <li>sigaa.war/extensao/AutorizacaoDepartamento/form_busca_autorizar_relatorios.jsp</li> 
     *  <li>sigaa.war/extensao/AutorizacaoDepartamento/lista_relatorio.jsp </li>
     *  <li>sigaa.war/extensao/AutorizacaoDepartamento/lista.jsp </li>
     *  <li>sigaa.war/extensao/AvaliacaoAtividade/consultar_avaliacoes.jsp</li> 
     *  <li>sigaa.war/extensao/AvaliacaoAtividade/lista_parecerista.jsp </li>
     *  <li>sigaa.war/extensao/AvaliacaoAtividade/lista_presidente.jsp </li>
     *  <li>sigaa.war/extensao/AvaliacaoAtividade/lista.jsp  </li>
     *  <li>sigaa.war/extensao/DiscenteExtensao/atividades_visualizar_resultado.jsp</li>   
     *  <li>sigaa.war/extensao/DiscenteExtensao/busca_discente.jsp   </li>
     *  <li>sigaa.war/extensao/DiscenteExtensao/inscricao_discente.jsp </li>
     *  <li>sigaa.war/extensao/DistribuicaoComiteExtensao/lista_atividades_vinculadas.jsp</li>  
     *  <li>sigaa.war/extensao/DistribuicaoComiteExtensao/lista.jsp   </li>
     *  <li>sigaa.war/extensao/DistribuicaoPareceristaExtensao/lista_atividades_vinculadas.jsp</li> 
     *  <li>sigaa.war/extensao/DistribuicaoPareceristaExtensao/lista.jsp    </li>
     *  <li>sigaa.war/extensao/DocumentosAutenticados/form.jsp </li>
     *  <li>sigaa.war/extensao/MovimentacaoCota/form.jsp </li>
     *  <li>sigaa.war/extensao/MovimentacaoCota/lista.jsp </li>
     *  <li>sigaa.war/extensao/PlanoTrabalho/atividades_lista.jsp.jsp</li>   
     *  <li>sigaa.war/extensao/PlanoTrabalho/planos_discente.jsp   </li>
     *  <li>sigaa.war/extensao/SolicitacaoReconsideracao/lista.jsp </li>
     *  <li>sigaa.war/extensao/ValidacaoRelatorioProex/lista.jsp </li>
     * <ul>
     * 
     * @return Página com dados detalhados da ação de extensão selecionada.
     */
	public String view() throws DAOException {
		
		Integer id = getParameterInt("id", 0);

		/* Está usando <f:param>? Se não está usando <f:setPropertyActionListener>.
		 * Bug do JSF 1.2 que não renderiza commandLink com value=#{null} ou omitido
		 * */
	    if (id > 0) atividadeSelecionada.setId(id);

	    if (atividadeSelecionada.getId() > 0) {

		    //Utilizado na visualização de ações de extensão a partir da página de Ações Acadêmicas Associadas.
		    if (getParameterBoolean("ACAO_ASSOCIADA")) {
			int idTipoAcao = getParameterInt("TIPO_ACAO");
		    	atividadeSelecionada = getDAO(AtividadeExtensaoDao.class).findAcaoByProjetoAndTipoAcao(atividadeSelecionada.getId(), idTipoAcao);
		    }
		    
		    atividadeSelecionada = getGenericDAO().findByPrimaryKey(atividadeSelecionada.getId(), AtividadeExtensao.class);
		    Collection<OrcamentoDetalhado> orcamento = getGenericDAO().findByExactField(OrcamentoDetalhado.class, "projeto.id", atividadeSelecionada.getProjeto().getId());
		    if(orcamento != null){
		    	atividadeSelecionada.setOrcamentosDetalhados(orcamento);
		    }

		    recalculaTabelaOrcamentaria(atividadeSelecionada.getOrcamentosDetalhados());
		    
		    // listar os discentes envolvidos na atividade ordenados por nome, evitando erro de LazyInicialization
		    atividadeSelecionada.setDiscentesSelecionados(getDAO(DiscenteExtensaoDao.class).findByAtividade(atividadeSelecionada.getId()));
		    PlanoTrabalhoExtensaoDao planoTrabalhoDao = getDAO(PlanoTrabalhoExtensaoDao.class);
		    for (DiscenteExtensao de : atividadeSelecionada.getDiscentesSelecionados()) {
			de.getDiscente().getMatriculaNome();
			de.setPlanoTrabalhoExtensao(planoTrabalhoDao.findByDiscente(de.getId()));
		    }

		    // Carregando todos os objetivos pra evitar erro de lazy
		    if ((atividadeSelecionada.getTipoAtividadeExtensao().getId() == TipoAtividadeExtensao.PROJETO) && (atividadeSelecionada.getProjetoExtensao() != null)) {
				for (Objetivo objetivo : atividadeSelecionada.getObjetivo()) {
				    objetivo.getAtividadesPrincipais().iterator();
				}
		    }

		    atividadeSelecionada.setMembrosEquipe(getDAO(MembroProjetoDao.class).findAtivosByProjeto(atividadeSelecionada.getProjeto().getId(), CategoriaMembro.DISCENTE,
					CategoriaMembro.DOCENTE,CategoriaMembro.EXTERNO,CategoriaMembro.SERVIDOR));
		    // evitar erro de lazy na busca do coordenador
		    for (MembroProjeto mp : atividadeSelecionada.getMembrosEquipe()) {
			mp.getId();
		    }
		    
		    HistoricoSituacaoProjetoDao dao = getDAO(HistoricoSituacaoProjetoDao.class);
		    atividadeSelecionada.getProjeto().setHistoricoSituacao(dao.relatorioHistoricoByIdProjeto(atividadeSelecionada.getProjeto().getId()));

		    // Exibe o resultado da avaliação do comitê
		    Boolean orcamentoAprovado = getParameterBoolean("orcamentoAprovado");
		    if ((orcamentoAprovado != null) && (orcamentoAprovado)) {
			return forward(ConstantesNavegacao.ATIVIDADE_ORCAMENTO_APROVADO_VIEW);
		    }

		    // Exibe a versão para impressão dos dados da ação de extensão
		    Boolean print = getParameterBoolean("print");
		    
		    if ((print != null) && (print)) {
			return forward(ConstantesNavegacao.ATIVIDADE_PRINT);
		    } else {
			return forward(ConstantesNavegacao.ATIVIDADE_VIEW);
		    }

		
	    } else {
		addMensagemErro("Ação de extensao não selecionada.");
	    }

	    return null;

	}

    /**
     * Carrega atividade e prepara MBeans para visualização.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/atividades.jsp</li> 
     * </ul>
     * @param id da ação de extensão selecionada.
     * @return Página com detalhes da ação.
     */
    public String view(int id) throws DAOException {
	
    	getCurrentRequest().setAttribute("id", id);	    
    	return view();

    }

    /**
     * Adiciona um arquivo à ação de extensão.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/anexar_arquivos.jsp</li> 
     * <ul>
     * @return Retorma para mesma página permitindo a inclusão de novo arquivo.
     */
	public String anexarArquivo() {

		try {

			if ((descricaoArquivo == null)
					|| ("".equals(descricaoArquivo.trim()))) {
				addMensagemErro("Descrição: Campo obrigatório não informado.");
				return null;
			}

			if ((file == null) || (file.getBytes() == null)) {
				addMensagemErro("Arquivo: Campo obrigatório não informado.");
				return null;
			}

			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file
					.getContentType(), file.getName());
			ArquivoProjeto arquivo = new ArquivoProjeto();
			arquivo.setDescricao(descricaoArquivo);
			arquivo.setIdArquivo(idArquivo);
			arquivo.setAtivo(true);
			obj.addArquivo(arquivo);
			gravarTemporariamente();
			addMessage("Arquivo Anexado com Sucesso.", TipoMensagemUFRN.INFORMATION);
			descricaoArquivo = new String();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		return null;
	}

    /**
     * Remove o arquivo da lista de anexos da ação de extensão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/anexar_arquivos.jsp</li> 
     * </ul>
     * @return Retorna para mesma página permitindo a exclusão de novo arquivo.
     * @throws SegurancaException 
     */
	public String removeAnexo() throws SegurancaException {
    	checkChangeRole();
		ArquivoProjeto arquivo = new ArquivoProjeto();
		arquivo.setIdArquivo(getParameterInt("idArquivo", 0));
		arquivo.setId(getParameterInt("idArquivoExtensao", 0));
		// Remove do referencia ao arquivo do banco de extensão (sigaa)
		remover(arquivo);
		// Remove do banco de arquivos (cluster)
		EnvioArquivoHelper.removeArquivo(arquivo.getIdArquivo());
		// Remove da view
		if ((obj.getArquivos() != null) && (!obj.getArquivos().isEmpty())) {
			obj.getArquivos().remove(arquivo);
		}

		return null;
	}

    /**
     * Visualizar o arquivo anexo a atividade de extensão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/extensao/Atividade/include/dados_atividade.jsp</li>
     * <li>sigaa.war/extensao/Atividade/anexar_arquivos.jsp</li>
     * <li>sigaa.war/extensao/Atividade/print.jsp</li>
     * <li>sigaa.war/extensao/Atividade/view.jsp</li>
     * <li>sigaa.war/lato/relatorio_final/resumo.jsp</li>
     * </ul>
     */
	public String viewArquivo() {
		try {
			int idArquivo = getParameterInt("idArquivo");
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Arquivo não encontrado!");
			return null;
		}
		FacesContext.getCurrentInstance().responseComplete();
		return null;
	}

    /**
     * Adiciona uma foto à ação de extensão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/anexar_fotos.jsp</li> 
     * </ul>
     * @return Retorna para mesma página permitindo a inclusão de nova foto.
     * @throws SegurancaException 
     */
	public String anexarFoto() throws SegurancaException {
    	checkChangeRole();

		try {

			if ((descricaoFoto == null) || ("".equals(descricaoFoto.trim()))) {
				addMensagemErro("Descrição: Campo obrigatório não informado.");
				return null;
			}
			if ((foto == null) || (foto.getBytes() == null)) {
				addMensagemErro("Foto: Campo obrigatório não informado.");
				return null;
			}

			int idFotoOriginal = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idFotoOriginal, foto.getBytes(),
					foto.getContentType(), foto.getName());

			int idFotoMini = EnvioArquivoHelper.getNextIdArquivo();
			byte[] fotoMini = UFRNUtils.redimensionaJPG(foto.getBytes(),
					WIDTH_FOTO, HEIGHT_FOTO);

			EnvioArquivoHelper.inserirArquivo(idFotoMini, fotoMini, foto
					.getContentType(), foto.getName());
			FotoProjeto novaFoto = new FotoProjeto();
			novaFoto.setDescricao(descricaoFoto);
			novaFoto.setIdFotoOriginal(idFotoOriginal);
			novaFoto.setIdFotoMini(idFotoMini);
			novaFoto.setAtivo(true);
			obj.addFoto(novaFoto);
			gravarTemporariamente();
			addMessage("Foto Anexada com Sucesso!",
					TipoMensagemUFRN.INFORMATION);
			descricaoFoto = new String();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Tipo de arquivo não compatível.");
		}

		return null;
	}

    /**
     * Remove a foto da lista de anexos da atividade.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/anexar_fotos.jsp</li> 
     * </ul>
     * @return Retorma para mesma página permitindo que nova foto seja removida.
     * @throws ArqException Gerada por {@link EnvioArquivoHelper#removeArquivo(int)}
     */
    public String removeFoto() throws ArqException {
    	checkChangeRole();

    	FotoProjeto fotoRemovida = new FotoProjeto();
    	fotoRemovida.setIdFotoOriginal(Integer.parseInt(getParameter("idFotoOriginal")));
    	fotoRemovida.setIdFotoMini(Integer.parseInt(getParameter("idFotoMini")));
    	fotoRemovida.setId(Integer.parseInt(getParameter("idFotoProjeto")));
    	// Remove referencia ao arquivo de foto do banco de extensão (sigaa)
    	remover(fotoRemovida);
    	// Remove o arquivo do banco de arquivos (cluster)
    	EnvioArquivoHelper.removeArquivo(fotoRemovida.getIdFotoOriginal());
    	EnvioArquivoHelper.removeArquivo(fotoRemovida.getIdFotoMini());
    	// Remove da view
    	if ((obj.getFotos() != null) && (!obj.getFotos().isEmpty())) {
    		obj.getFotos().remove(fotoRemovida);
    	}

    	return null;
    }

    /**
     * Remove ação da lista de ações vinculadas a ação pai.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/atividades.jsp</li>
     *  </ul> 
     * 
     * @return Retorna para mesma página permitindo remoção de nova ação.
     * @throws ArqException 
     */
	public String removeAtividade() throws ArqException {
		checkChangeRole();
		int id = Integer.parseInt(getParameter("id"));
		try {
			AtividadeExtensao atividadeRemovida = getGenericDAO().findByPrimaryKey(id, AtividadeExtensao.class);
			obj.getAtividades().remove(atividadeRemovida);
			AtividadeExtensaoHelper.determinaVinculoProgramaExtensao(atividadeRemovida);	
			gravarTemporariamente();
		} catch (DAOException e) {
			addMensagemErro("Erro ao remover ação relacionada.");
			notifyError(e);
		}

		return null;
	}

    /**
     * Adiciona uma ação de extensão à lista de atividades vinculadas.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/atividades.jsp</li> 
     * </ul>
     * 
     * @return Retorna para mesma página permitindo a inclusão de outras ações.
     * @throws ArqException 
     */
	public String adicionaAtividade() throws ArqException {
    	checkChangeRole();
		ListaMensagens mensagens = new ListaMensagens();
		avisosAjax.clear();
		int idAtividade = getParameterInt("id", 0);
		AtividadeExtensao atividadeNova = null;

		try {
			atividadeNova = getGenericDAO().findByPrimaryKey(idAtividade, AtividadeExtensao.class);
		} catch (DAOException e) {
			mensagens.addErro("Falha ao carregar ação: " + e.getMessage());
			notifyError(e);
		}

		AtividadeExtensaoValidator.validaAdicionaAtividadeAtividade(obj,
				atividadeNova, mensagens);

		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			setAvisosAjax((List<MensagemAviso>) mensagens.getMensagens()); // Mensagens assíncronas de erro 
			return null;
		}

		if (!obj.getAtividades().contains(atividadeNova)) {
			obj.getAtividades().add(atividadeNova);
			AtividadeExtensaoHelper.determinaVinculoProgramaExtensao(atividadeNova);
			gravarTemporariamente();
		} else {
			addMensagemErro("Atividade já inserida no programa.");
		}

		return null;
	}

    /**
     * Facilita a exibição da tabela de orçamentos da ação que está sendo cadastrada/alterada.
     * 
     * @param orcamentos {@link Collection} de {@link OrcamentoDetalhado} com itens do orçamento da ação atual.
     */
	private void recalculaTabelaOrcamentaria(Collection<OrcamentoDetalhado> orcamentos) {
		tabelaOrcamentaria.clear();

		for (OrcamentoDetalhado orca : orcamentos) {
			ResumoOrcamentoDetalhado resumo = tabelaOrcamentaria.get(orca
					.getElementoDespesa());
			if (resumo == null) {
				resumo = new ResumoOrcamentoDetalhado();
			}
			resumo.getOrcamentos().add(orca);
			tabelaOrcamentaria.put(orca.getElementoDespesa(), resumo);
		}
	}

    /**
     * Visualização da tabela de orçamento cadastrado durante o cadastro da
     * atividade.
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/include/dados_atividade.jsp</li>
     *  <li>sigaa.war/extensao/Atividade/include/dados_avaliar_atividade.jsp</li>
     *  <li>sigaa.war/extensao/Atividade/orcamento.jsp</li>
     *  <li>sigaa.war/extensao/Atividade/print.jsp</li>
     *  <li>sigaa.war/extensao/Atividade/view.jsp</li>
     * </ul> 
     * 
     * @return Retorna a tabela orçamentária. Um {@link Map} de {@link ElementoDespesa} e {@link ResumoOrcamentoDetalhado}.
     */
    public Map<ElementoDespesa, ResumoOrcamentoDetalhado> getTabelaOrcamentaria() {
    	return tabelaOrcamentaria;
    }
    
    /**
     * Set da Tabela orçamentária visualizada na tela de orçamento do cadastro da ação.
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Método não invocado por JSP´s</li>
     * </ul>
     * 
     * @param tabela orçamentária. Um {@link Map} de {@link ElementoDespesa} e {@link ResumoOrcamentoDetalhado}.
     */
    public void setTabelaOrcamentaria(Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabela) {
    	this.tabelaOrcamentaria = tabela;
    }

    /**
     * Invoca o processador para salvar a ação de extensão sem passar pela
     * validação. Este é um cadastro com vários passos e deve ser possível
     * interromper o processo para continuar depois. 
     * É necessário o preenchimento de no mínimo a primeira tela 
     * (dados gerais de atividade de extensão) e a segunda 
     * (os dados gerais do tipo específico de ação a ser cadastrada).
     * 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Método não invocado por JSP´s</li>
     * </ul>
     * 
     * @return Retorma para mesma página permitindo seguir com o fluxo.
     * @throws ArqException 
     */
	public String gravarTemporariamente() throws ArqException {
    	checkChangeRole();
		Integer op = (Integer) getCurrentSession().getAttribute("operacaoAtiva");
		if ( op != null && op.equals(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO.getId()) ) {
			try {
				Comando ultimoComando = getUltimoComando();
	
				// Preparar movimento e chamar processador
				CadastroExtensaoMov mov = new CadastroExtensaoMov();
				mov.setAtividade(obj);
				mov.setObjAuxiliar(controleFluxo);
				mov.setCodMovimento(SigaaListaComando.GRAVAR_RASCUNHO_ATIVIDADE_EXTENSAO);
				prepareMovimento(SigaaListaComando.GRAVAR_RASCUNHO_ATIVIDADE_EXTENSAO);
				obj = (AtividadeExtensao) execute(mov, getCurrentRequest());
	
				// Repreparar a operação anterior
				prepareMovimento(ultimoComando);
	
			} catch (NegocioException e) {
			    addMensagemErro(e.getMessage());		    
			} finally {
				// Instanciar atributos que foram definidos com null pelo
				// processador
				AtividadeExtensaoHelper.instanciarAtributos(getObj());
			}
			return null;
		}else {
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "Operação", "executada");
			return redirect("/sigaa/extensao/menu.jsf");
		}
	}

    /**
     * Grava a ação de extensão temporariamente e sai do cadastro. 
     * Utilizado no botão de gravar na tela de resumo o cadastro da ação de extensão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/resumo.jsp</li> 
     * </ul>
     * @return Retorna para a página principal do subsistema de extensão.
     * @throws SegurancaException 
     */
	public String gravarFinalizar() throws SegurancaException {
    	checkChangeRole();
		//Verifica se a ação ja foi executada
		Integer op = (Integer) getCurrentSession().getAttribute("operacaoAtiva");
		if ( op != null && op.equals(SigaaListaComando.SUBMETER_ATIVIDADE_EXTENSAO.getId()) ) {
			
			try {
	
				CadastroExtensaoMov mov = new CadastroExtensaoMov();
				mov.setAtividade(obj);
				mov.setCodMovimento(SigaaListaComando.GRAVAR_RASCUNHO_ATIVIDADE_EXTENSAO);
				prepareMovimento(SigaaListaComando.GRAVAR_RASCUNHO_ATIVIDADE_EXTENSAO);
				obj = (AtividadeExtensao) execute(mov, getCurrentRequest());
				addMensagemInformation("Atividade gravada com sucesso.");
	
			} catch (Exception e) {
				notifyError(e);
				addMensagemErro("Não foi possível salvar a atividade."
						+ e.getMessage());
				e.printStackTrace();
			} finally {
				// Instanciar atributos que foram definidos com null pelo
				// processador
				AtividadeExtensaoHelper.instanciarAtributos(getObj());
			}
	
			limparMBeans();
			setOperacaoAtiva(null);
			return redirectJSF(getSubSistema().getLink());
		}else {
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "Operação", "executada");
			return redirect("/sigaa/extensao/menu.jsf");
		}
	}

    /**
     * Remove o objeto informado do banco de extensão.
     *
     * Método não invocado por JSP´s
     * 
     * @param persistDB Objeto que será removido do banco.
     * @return Retorna para mesma página permitindo que um novo objeto seja removido.       
     * @throws SegurancaException 
     */
	public String remover(PersistDB persistDB) throws SegurancaException {
    	checkChangeRole();

		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(persistDB);

		if (persistDB.getId() == 0) {
			addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
			return null;
		}

		try {
			Comando ultimoComando = getUltimoComando();
			prepareMovimento(ArqListaComando.REMOVER);
			mov.setCodMovimento(ArqListaComando.REMOVER);
			execute(mov, getCurrentRequest());
			addMessage("Remoção realizada com sucesso!",
					TipoMensagemUFRN.INFORMATION);
			prepareMovimento(ultimoComando);
		} catch (Exception e) {
		    tratamentoErroPadrao(e);
		}

		return null;
	}

    /**
     * Seleciona a ação e exibe página para confirmar a remoção.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/extensao/AlterarAtividade/lista.jsp</li>
     * 	<li>sigaa.war/extensao/Atividade/lista_atividades_pendentes.jsp </li>
     * 	<li>sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp</li> 
     * </ul>
     * 
     * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()     * 
     * @return Seleciona a ação e exibe página para confirmação da remoção da ação.
     */
    @Override
    public String preRemover() {
    	try {
    		prepareMovimento(SigaaListaComando.REMOVER_ATIVIDADE_EXTENSAO);
    		setId();
    		obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
    		obj.getMembrosEquipe().iterator();	    

    		if (!getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_EXTENSAO)) {
	    		ListaMensagens lista = new ListaMensagens();
	    			ProjetoBaseValidator.validaRemoverProjeto(obj.getProjeto(), getUsuarioLogado().getPessoa(), lista );
	    		if (!lista.isEmpty()) {
	    			addMensagens(lista);
	    			return null;
	    		}
    		}

    	} catch (ArqException e) {
    		notifyError(e);
    		addMensagemErroPadrao();
    	}	
    	return forward(ConstantesNavegacao.REMOVER);
    }

    /** 
     * Remove a ação de extensão do sistema para o usuário status da ação como
     * removida e campo ativo = false.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/remocao.jsp</li> 
     * <ul>
     * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#remover()
     * @return Retorna para tela com lista de ações permitindo remoção de outra ação de extensão.
     * @throws SegurancaException 
     */
    @Override
    public String remover() throws SegurancaException, ArqException {
    	checkChangeRole();
    	CadastroExtensaoMov mov = new CadastroExtensaoMov();
    	mov.setAtividade(obj);

    	if (obj.getId() == 0) {
    		addMensagem(MensagensArquitetura.NAO_HA_OBJETO_REMOCAO);
    		return null;
    	} else {
    		try {
    			mov.setCodMovimento(SigaaListaComando.REMOVER_ATIVIDADE_EXTENSAO);
    			execute(mov, getCurrentRequest());
    			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
    		} catch (NegocioException e) {
    			addMensagens(e.getListaMensagens());
    			hasErrors();
    			return null;
    		}

    		// Gestor de extensão removendo ação a partir da tela de busca...
    		if (getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_EXTENSAO) && isExtensao()) {		
    			localizar();
    			return redirectJSF(ConstantesNavegacao.ALTERAR_ATIVIDADE_LISTA);
    		} else {
    			// Coordenador da ação removendo...
    			listarMinhasAtividades();
    			return forward(ConstantesNavegacao.LISTA_MINHAS_ATIVIDADES);
    		}
    	}
    }

    /**
     * Atualiza as ações gravadas pelo usuário atual.
     * Ações gravadas são ações com cadastro em andamento que 
     * ainda não foram submetidas para avaliação dos departamentos.
     * 
     * Método chamado pelas seguintes JSP's
     * <ul>
     * 		<li>Método não invocado por JSP´s </li>
     * </ul>
     * @throws DAOException 
     *        
     */
	public void atualizarCadastrosEmAndamento() throws DAOException {
		AtividadeExtensaoDao dao = null;
		try {
			dao = getDAO(AtividadeExtensaoDao.class);
			atividadesGravadas = dao.findAtivasGravadosByUsuario(getUsuarioLogado());

		} finally{
			if(dao != null)  dao.close();
		}
	}
	
	
	/**
     * Atualiza Todas as ações que o usuário faz parte
     * 
     * Método chamado pelas seguintes JSP's
     * <ul>
     * 		<li>Método não invocado por JSP´s </li>
     * </ul>
	 * @throws DAOException 
     *        
     */
	public void atualizarAtividadesUsuarioParticipa() throws DAOException {
		Pessoa pessoa = (getDocenteExternoUsuario() != null ? getDocenteExternoUsuario().getPessoa() : getUsuarioLogado().getServidor().getPessoa());		
		
		AtividadeExtensaoDao dao = null;
		
		try{
			dao = getDAO(AtividadeExtensaoDao.class);
			
			//Procurar pela pessoa, pois se o usuário tiver um vínculo de Servidor que não pode ser selecionado 
			//na tela inicial de login(inativo), não poderá visualizar as ações de extensão que ele é coordenador.
		    Collection<AtividadeExtensao> atividades = dao.findAtivasByPessoa(pessoa);
		    popularDadosParaListagem(atividades);
		    
		}finally{
			if(dao != null) dao.close();
		}
	}
	
    private void popularDadosParaListagem(Collection<AtividadeExtensao> atividades) {
    	atividadesMembroCoordena = new ArrayList<AtividadeExtensao>();
    	atividadesMembroParticipa = new ArrayList<AtividadeExtensao>();
    	for (AtividadeExtensao atividadeExtensao : atividades) {
			if ( DesignacaoFuncaoProjetoHelper.isCoordenadorOrDesignacaoCoordenadorProjeto(atividadeExtensao.getProjeto().getId(), getUsuarioLogado().getPessoa().getId()) )
				atividadesMembroCoordena.add(atividadeExtensao);
			else 
				atividadesMembroParticipa.add(atividadeExtensao);
		}
	}

	/**
     * Lista todos os tipos de Região possíveis Utilizado na determinação da
     * Abrangência da ação de extensão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * </ul>
     *  <li>sigaa.war/extensao/Atividade/dados_gerais.jsp</li> 
     * </ul>
     * @return Retorna a lista de todas as regiões geográficas.
     */
    public Collection<SelectItem> getAllTipoRegiaoCombo() {
    	return getAll(TipoRegiao.class, "id", "descricao");
    }

    /**
     * Método chamado pela(s) seguinte(s) JSP(s):
     * </ul>
     *  <li>sigaa.war/extensao/Atividade/dados_gerais.jsp</li> 
     * </ul>
     * @return Retorna uma lista com os estados brasileiros (mais o Distrito Federal). 
     */
    public Collection<SelectItem> getAllEstadosCombo(){
    	// Remover o estado "Não Informado" da view
    	Collection<SelectItem> collection = getAll(UnidadeFederativa.class, "id", "descricao");
    	for (SelectItem selectItem : collection) {
    		if (selectItem.getValue().toString().equals("-1")){
    			collection.remove(selectItem);
    			break;
    		}
    	}
    	return collection;
    }
    
    public Collection<SelectItem> getAllMunicipio() throws DAOException {
    	if ( obj.getLocalRealizacao() != null && obj.getLocalRealizacao().getMunicipio().getUnidadeFederativa().getId() > 0  ) {
	    	MunicipioDao dao = getDAO(MunicipioDao.class);
	    	try {
	    		 return toSelectItems(dao.findByUF(obj.getLocalRealizacao().getMunicipio().getUnidadeFederativa().getId()), "id", "nome");
			} finally {
				dao.close();
			}
    	} else {
    		return new ArrayList<SelectItem>();
    	}
    }

    public Collection<SelectItem> getAllMembrosProjeto() throws DAOException {
		return toSelectItems(getMembrosEquipe(), "id", "pessoa.nome");
    }
    
    public Collection<Objetivo> getObjetivos() throws DAOException {
		if ( obj.getObjetivo() == null ) {
	    	ObjetivoDao dao = getDAO(ObjetivoDao.class);
	    	try {
	    		obj.setObjetivo( dao.findObjetivos(obj.getId(),	null) );
			} finally {
				dao.close();
			}
		}
		return obj.getObjetivo();
    }
    
	public void carregarServidor(ActionEvent e) throws DAOException{
		br.ufrn.rh.dominio.Servidor s = (br.ufrn.rh.dominio.Servidor) 
			e.getComponent().getAttributes().get("docenteAutoComplete");
		obj.getProjeto().getCoordenador().setServidor( 
				getGenericDAO().findByPrimaryKey(s.getId(), Servidor.class) );
	}
    
    /**
	 * Retorna os editais abertos para a ação de extensão 
	 * que está sendo cadastrada.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/extensao/Atividade/dados_gerais.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getEditaisCombo() throws DAOException {
		// Em caso de registro de ações somente editais finalizados são mostrados.
	    if ( (isExtensao() && isUserInRole(SigaaPapeis.GESTOR_EXTENSAO)) || obj.isRegistro() ) {
	    	return toSelectItems(getDAO(EditalDao.class).findAllAtivosExtensao(), "id", "descricao");
	    } else {
	    	return toSelectItems(getDAO(EditalDao.class).findAbertosExtensao(), "id", "descricao");
	    }
	}
    
    /**
     * Retorna todas as situações possíveis de atividades cadastradas no banco.
     * 
     * Método chamado pelas seguintes JSP's
     * <ul>
     * 		<li>Método não invocado por JSP´s </li>
     * </ul>
     * 
     * @return Lista de todas os tipos de ações de extensão.
     */
	public Collection<SelectItem> getTipoSituacaoAtividadeCombo() {

	    	Collection<SelectItem> lista = new ArrayList<SelectItem>();
		try {
			 lista.addAll(toSelectItems(getGenericDAO().findByExactField(
					TipoSituacaoProjeto.class, "contexto", "E", "asc",
					"descricao"), "id", "descricao"));
			 
			 //Incluindo situações que pertencem a todos os tipos de projetos.
			 lista.addAll(toSelectItems(getGenericDAO().findByExactField(
					TipoSituacaoProjeto.class, "contexto", "T", "asc",
					"descricao"), "id", "descricao"));			 
			 return lista;
			 
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}

	}

    /**
     * Carrega a ação do BD.
     *
     * Método não invocado por JSP´s
     * 
     * @param id id da ação.
     * @return retorna o objeto informado.
     */
	private Object loadObj(int id) {
		try {
			GenericDAO dao = getGenericDAO();
			return dao.findByPrimaryKey(id, AtividadeExtensao.class);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
	}
    
    /**
     * Inclui unidades proponentes na atividade.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/dados_gerais.jsp</li> 
     * <ul>
     */
	public void adicionarOutraUnidadeProponente() {

		if ((obj.getUnidadesProponentes() != null) && (outraUnidade != null)
				&& (outraUnidade.getId() > 0)) {
			try {
				AtividadeUnidade atividadeUnidade = new AtividadeUnidade();
				outraUnidade = getGenericDAO().findByPrimaryKey(
						outraUnidade.getId(), Unidade.class);
				atividadeUnidade.setUnidade(outraUnidade);
				atividadeUnidade.setAtividade(obj);
				obj.addUnidadeProponente(atividadeUnidade);
				outraUnidade = new Unidade();
			} catch (DAOException e) {
				notifyError(e);
			}
		}

	}

    /**
     * Remove o servidor da lista de membros da atividade.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/dados_gerais.jsp</li> 
     * </ul>
     * @return Retorna o usuário para mesma página permitindo a remaoção de outra unidade
     * @throws SegurancaException 
     */
	public String removeUnidadeProponente() throws SegurancaException {
    	checkChangeRole();
		int idUnidade = getParameterInt("idUnidade", 0);
		int idAtividadeUnidade = getParameterInt("idAtividadeUnidade", 0);

		try {
			AtividadeUnidade atividadeUnidade = new AtividadeUnidade();
			atividadeUnidade.setAtividade(obj);
			atividadeUnidade.setUnidade(new Unidade(idUnidade));
			obj.getUnidadesProponentes().remove(atividadeUnidade);

			// Se AtividadeUnidade já foi persistida, tem que remover também do
			// banco;
			if (idAtividadeUnidade > 0) {
				atividadeUnidade = getGenericDAO().findByPrimaryKey(
						idAtividadeUnidade, AtividadeUnidade.class);
				remover(atividadeUnidade);
			}

		} catch (Exception e) {
			addMensagemErro("Erro ao Remover Unidade Proponente da Atividade.");
			notifyError(e);
		}

		return null;
	}

    /**
     * Exibe a lista de atividades que foram gravadas pelo usuário logado
     * permitindo que ele continue o cadastro de onde parou.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/extensao/menu_ta.jsp</li> 
     * 	<li>sigaa.war/portais/docente/menu_docente.jsp</li> 
     * </ul>
     * 
     * @return Retorna página com lista de todas as ações pendentes de envio para os departamentos.
     * @throws DAOException 
     */
	public String listarCadastrosEmAndamento() throws DAOException {

		if ((getUsuarioLogado()).getServidor() == null) {
			addMensagemErro("Você não possui acesso a esta operação");
			return null;
		}

		atualizarCadastrosEmAndamento();
		return forward(ConstantesNavegacao.LISTA_ATIVIDADES_PENDENTES);
	}

    /**
     * Retorna a lista de todos os arquivos (documentos) anexados a ação de
     * extensão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/anexar_arquivo.jsp</li> 
     * </ul>
     * @return Retorna lista de arquivos da ação atual.
     * @throws DAOException Gerado por {@link GenericDAOImpl} utilizado na busca.
     */
	public Collection<ArquivoProjeto> getArquivosProjeto() throws DAOException {
		obj.setArquivos(getGenericDAO().findByExactField(ArquivoProjeto.class, "projeto.id", obj.getProjeto().getId()));
		return obj.getArquivos();
	}

    /**
     * Retorna todas as fotos anexadas a ação de extensão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/anexar_foto.jsp</li> 
     * </ul>
     * @return Retorna lista de arquivos da ação atual.
     * @throws DAOException Gerado por {@link GenericDAOImpl} utilizado na busca.
     */
	public Collection<FotoProjeto> getFotosProjeto() throws DAOException {
		obj.setFotos(getGenericDAO().findByExactField(FotoProjeto.class, "projeto.id", obj.getProjeto().getId()));
		return obj.getFotos();
	}

    /**
     * Devolver ação para reedição pelo coordenador cancela todas as solicitação
     * de validação para todos os chefes de departamentos para os quais a ação
     * foi enviada para solicitar autorização.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/AlterarAtividade/lista.jsp</li> 
     * </ul>
     * @return Retorna para mesma página permitindo que o usuário reenvie outra ação para reedição.
     * @throws ArqException Gerarao na chamada ao processador.
     */
	public String reeditarProposta() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
				SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO,
				SigaaPapeis.CHEFE_UNIDADE);

		try {

			int idAtividade = getParameterInt("id", 0);
			if (idAtividade > 0) {
				AutorizacaoDepartamentoDao dao = getDAO(AutorizacaoDepartamentoDao.class);
				AtividadeExtensao atv = dao.findByPrimaryKey(idAtividade,
						AtividadeExtensao.class);

				if (atv != null) {
					prepareMovimento(SigaaListaComando.REEDITAR_ATIVIDADE_EXTENSAO);
					// revogando a autorização ativa e
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setObjMovimentado(atv);
					mov
							.setCodMovimento(SigaaListaComando.REEDITAR_ATIVIDADE_EXTENSAO);
					execute(mov, getCurrentRequest());
					addMensagemInformation("Proposta Devolvida para Coordenador(a) com sucesso!");
				} else {
					addMensagemErro("Operação não realizada: Ação de Extensão não foi informada!");
				}
			}

		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		}

		// se for membro da proex
		if (isUserInRole(SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
				SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO)) {
			localizar();
			// Atualizar a tela da lista de ações
			return forward(ConstantesNavegacao.ALTERAR_ATIVIDADE_LISTA);

			// não é chefe de dpto.
		} else {
			return redirectMesmaPagina();
		}
	}

    /**
     * Muda o ação (obj) do Mbean permitindo visualizar os novos avaliadores da ação.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/AvaliacaoAtividade/consultar_avaliadores.jsp</li> 
     * </ul>
     * @param evt Valor recebido da jsp na geração do evento.
     */
	public void changeAtividadeExtensao(ValueChangeEvent evt) {
		Integer idAtividade = 0;
		try {
			idAtividade = new Integer(evt.getNewValue().toString());
			if (idAtividade != null && idAtividade != 0) {
				obj = (AtividadeExtensao) loadObj(idAtividade);
				obj.getAvaliacoes().iterator();
			}
		} catch (Exception e) {
			notifyError(e);
		}
	}

    /**
     * Lista de todos as atividadesLocalizadas no combo.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/AvaliacaoAtividade/consultar_avaliadores.jsp</li> 
     * </ul>
     * @return Coleção de SelectItem usado em selects html
     */
    public Collection<SelectItem> getAtividadesLocalizadasCombo() {
    	return toSelectItems(new ArrayList<AtividadeExtensao>(atividadesLocalizadas), "id", "titulo");
    }

    /**
     * Inicia o procedimento de alterar somente a situação da atividade.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/menu.jsp</li> 
     * </ul>
     * @return Retorna página com lista completa de ações.
     * @throws ArqException Gerado por {@link SigaaAbstractController#checkRole(int...)}
     */
    public String listaAlterarAtividade() throws ArqException {
    	checkRole(SigaaPapeis.GESTOR_EXTENSAO);
    	if(atividadesLocalizadas != null){
    			if(!atividadesLocalizadas.isEmpty()){
    					atividadesLocalizadas.clear();
    			}
    	}
    	
    	resetBean();
    	return forward(ConstantesNavegacao.ALTERAR_ATIVIDADE_LISTA);
    }

    /**
     * Inicia o procedimento de alterar somente a situação da atividade.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/AlterarAtividade/lista.jsp</li> 
     * </ul>
     * @return Retorna página com lista completa de ações.
     * @throws ArqException Gerado por {@link SigaaAbstractController#checkRole(int...)} 
     * 		e {@link SigaaAbstractController#prepareMovimento(Comando)} 
     */
    public String iniciarAlterarSituacaoAtividade() throws ArqException {
	checkRole(SigaaPapeis.GESTOR_EXTENSAO);
	obj = (AtividadeExtensao) loadObj(getParameterInt("id"));
	prepareMovimento(SigaaListaComando.ALTERAR_SITUACAO_ATIVIDADE_EXTENSAO);
	return forward(ConstantesNavegacao.ALTERAR_SITUACAO_ATIVIDADE_FORM);
    }
    
    /**
     * Inicia o procedimento de alterar somente a quantidade de bolsas consedidas da
     * ação de extensão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/AlterarAtividade/lista.jsp</li> 
     * </ul>
     * @return Retorna página com lista completa de ações.
     * @throws ArqException Gerado por {@link SigaaAbstractController#checkRole(int...)} 
     * 		e {@link SigaaAbstractController#prepareMovimento(Comando)} 
     */
    public String iniciarAlterarBolsasConcedidas() throws ArqException {
	checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
	obj = (AtividadeExtensao) loadObj(getParameterInt("id"));
	prepareMovimento(SigaaListaComando.ALTERAR_BOLSAS_ATIVIDADE_EXTENSAO);
	return forward(ConstantesNavegacao.ALTERAR_BOLSAS_ATIVIDADE_FORM);
    }

	/**
	 * Altera somente a situação da atividade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/AlterarAtividade/form_situacao_atividade.jsp</li> 
	 * 	<li>sigaa.war/extensao/AlterarAtividade/lista.jsp </li>
	 * </ul>
	 * @return Retorna página com lista completa de ações.
	 * @throws ArqException Gerada na chamada ao processador. 
	 */
	public String alterarSituacaoAtividade() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);

		if (obj != null && obj.getId() == 0) {
			addMensagemWarning("Você utilizou o botão 'voltar' do navegador, o que não é recomendado. Por favor, reinicie os procedimentos!");
			return forward("/extensao/menu.jsp");
		}
		
		TipoSituacaoProjeto novaSituacao = new TipoSituacaoProjeto(obj.getSituacaoProjeto().getId()); 

		//Evitar erro de lazy
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), AtividadeExtensao.class);
		obj.getMembrosEquipe().iterator();
		obj.getPlanosTrabalho().iterator();
		obj.getDiscentesSelecionados().iterator();
		obj.getAutorizacoesDepartamentos().iterator();

		try {
			CadastroExtensaoMov mov = new CadastroExtensaoMov();
			mov.setCodMovimento(SigaaListaComando.ALTERAR_SITUACAO_ATIVIDADE_EXTENSAO);
			obj.setSituacaoProjeto(novaSituacao);
			mov.setAtividade(obj);
			execute(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		}
		return cancelar();
	}


	/**
	 * Altera somente a quantidade de bolsas da ação de extensão.
	 * Esta ação pode ser realizada somente pelo presidênte do comitê de extensão.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>sigaa.war/extensao/AlterarAtividade/form_bolsas.jsp</li> 
	 * 	<li>sigaa.war/extensao/AlterarAtividade/lista.jsp </li>
	 * </ul>
	 * @return Retorna página com lista completa de ações.
	 * @throws ArqException Gerada na chamada ao processador. 
	 */
	public String alterarBolsasConcedidas() throws ArqException {
	    checkRole(SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO);
	    
	    if (obj != null && obj.getId() == 0) {
		addMensagemWarning("Você utilizou o botão 'voltar' do navegador, o que não é recomendado. Por favor, reinicie os procedimentos.");
		return forward("/extensao/menu.jsp");
	    }
	    CadastroExtensaoMov mov = new CadastroExtensaoMov();
	    mov.setCodMovimento(SigaaListaComando.ALTERAR_BOLSAS_ATIVIDADE_EXTENSAO);
	    mov.setAtividade(obj);
	    try {
		execute(mov, getCurrentRequest());
		addMensagemInformation("Alteração da ação realizada com sucesso.");
	    } catch (NegocioException e) {
		tratamentoErroPadrao(e);
	    }
	    return cancelar();
	}

	
    /**
     * Inicia o procedimento de visualizar avaliações da ação de extensão.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp</li> 
     *  </ul>
     * @return Retorna a lista com todas as avalições realizada da ação. 
     */
    public String iniciarVisualizarAvaliacaoAtividade() {
    	obj = (AtividadeExtensao) loadObj(getParameterInt("id"));
    	return forward(ConstantesNavegacao.AVALIACAO_LISTA_COORDENADOR);
    }
    
    
    /**
     * Busca atividades que se encontram no período de conclusão e 
     * redireciona para a página de listagem das atividades encontradas 
     *  <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/extensao/menu.jsp</li> 
     * </ul>
     * @return
     * @throws ArqException
     */
    public String iniciarBuscarAtividadesPeriodoConclusao() throws ArqException {	
	obj = new AtividadeExtensao();
    	atividadesLocalizadas = new ArrayList<AtividadeExtensao>();    	
    	return forward(ConstantesNavegacao.LISTA_ATIVIDADES_PERIODO_CONCLUSAO);
    }

        /**
        * Seleciona a ação e exibe página para confirmar a reativação.
        * <br>
        * Método chamado pela(s) seguinte(s) JSP(s):
        * <ul>
        * 	<li>sigaa.war/extensao/AlterarAtividade/lista.jsp</li> 
        * </ul>
        * 
        * @return Seleciona a ação e exibe página para confirmação da remoção da ação.
        */
	public String preReativarAcaoExtensao() {
		try {
		    setId();
		    prepareMovimento(SigaaListaComando.REATIVAR_ATIVIDADE_EXTENSAO);
		    obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
		    dataNovaFinalizacao = null;
		} catch (ArqException e) {
		    notifyError(e);
		    addMensagemErroPadrao();
		}	
		return forward(ConstantesNavegacao.REATIVAR);
	}

	
	/**
	 * Utilizado para reativar uma ação de extensão que tenha sido finalizada por engano.
	 * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li>sigaa.war/extensao/AlterarAtividade/form_reativar_acao.jsp</li> 
     * </ul>	
	 * @return
	 * @throws ArqException
	 */
	public String reativarAcaoExtensao() throws ArqException {
		if (obj.getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_CONCLUIDO) {
			//Reativando a ação de extensão			
			CadastroExtensaoMov mov = new CadastroExtensaoMov();				
			mov.setCodMovimento(SigaaListaComando.REATIVAR_ATIVIDADE_EXTENSAO);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setAtividade(obj);			
			mov.setNovaDataFinalizacaoAcao(dataNovaFinalizacao);
			try {
			    execute(mov, getCurrentRequest());
			    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			    return cancelar();
			 } catch (NegocioException e) {
			     addMensagens(e.getListaMensagens());
			 }
		}else {
		    addMensagemInformation("Operação disponível somente para ações com status 'CONCLUÍDO'");
		}
		return null;
	}
	
	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de docentes
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/Atividade/membros_equipe.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<Servidor> autoCompleteNomeDocente(Object event) throws DAOException{
		String nome = event.toString();
		Unidade uni = new Unidade(0);
		return (List<Servidor>) getDAO(ServidorDao.class).findByDocente(nome, uni.getId(), true, false);
	}
	
	/**
	 * Carrega o docente do autocomplete
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/Atividade/membros_equipe.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException 
	 */
	public void carregaDocente(ActionEvent e) throws DAOException{
		docente = (Servidor) e.getComponent().getAttributes().get("docenteAutoComplete");
		Pessoa p = new Pessoa();
		p = getGenericDAO().findByPrimaryKey(docente.getPessoa().getId(), Pessoa.class);
		docente.setPessoa(p);
		cpf = "";
		tab = "DOCENTE";
		getCurrentSession().setAttribute("categoriaAtual",CategoriaMembro.DOCENTE);
		redirectMesmaPagina();
	}
	
	/**
	 * <p>Retorna APENAS os servidores ATIVOS para incluir na ação de extensão. </p>
	 * 
	 * <p>IMPORTANTE: Servidores em outros status como CEDIDOS só pode ser incluídos como usuários externos na ação !!!! </p>
	 * 
	 * <p>Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Tecnicos Administrativos</p>
	 * 
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/Atividade/membros_equipe.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public List<Servidor> autoCompleteNomeServidorTecnico(Object e) throws DAOException{
		String nome = e.toString();
		Unidade uni = new Unidade(0);
		return (List<Servidor>) getDAO(ServidorDao.class).findByNome(nome, uni.getId(), true, false, Categoria.TECNICO_ADMINISTRATIVO, false);
	}
	
	/**
	 * Carrega o Servidor Técnico administrativo do autocomplete
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/Atividade/membros_equipe.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @throws DAOException 
	 */
	public void carregaServidorTecnico(ActionEvent event) throws DAOException{
		servidor = (Servidor) event.getComponent().getAttributes().get("servidorAutoComplete");
		Pessoa p = new Pessoa();
		p = getGenericDAO().findByPrimaryKey(servidor.getPessoa().getId(), Pessoa.class);
		servidor.setPessoa(p);
		cpf = "";
		tab = "SERVIDOR";
		getCurrentSession().setAttribute("categoriaAtual",CategoriaMembro.SERVIDOR);
		redirectMesmaPagina();
	}
	
	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Discentes
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa/extensao/Atividade/membros_equipe.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @return
	 * @throws DAOException
	 */
	public List<Discente> autoCompleteNomeDiscente(Object e) throws DAOException{
		String nome = e.toString();
		Unidade uni = new Unidade(0);
		char nivel = new Character(' ');
		return (List<Discente>) getDAO(DiscenteDao.class).findByNome(nome, uni.getId(), nivel, new PagingInformation());
	}
	
	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Discentes
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/MembroProjeto/cadastro_form.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 */
	public void carregaDiscente(ActionEvent event){
		discente = (Discente) event.getComponent().getAttributes().get("discenteAutoComplete");
		cpf = "";
		tab = "DISCENTE";
		getCurrentSession().setAttribute("categoriaAtual",CategoriaMembro.DISCENTE);
		redirectMesmaPagina();
	}
	
	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Discentes
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa/extensao/Atividade/membros_equipe.jsp</li>
	 * </ul>
	 * @param e
	 * @return
	 * @throws DAOException 
	 */
	public List<ParticipanteExterno> autoCompleteNomeParticipanteExterno(Object e) throws DAOException{
		String nome = e.toString();
		Servidor coordAtual = getServidorUsuario();
		return (List<ParticipanteExterno>) getDAO(ParticipanteExternoDao.class).findByNomeAndCoordenador(nome, coordAtual.getId());
	}
	
	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Discentes
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa/extensao/Atividade/membros_equipe.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @throws NumberFormatException 
	 * @throws DAOException 
	 */
	public void carregaParticipanteExterno(ActionEvent event) throws DAOException, NumberFormatException{
		participanteExterno = (ParticipanteExterno) event.getComponent().getAttributes().get("participanteExternoAutoComplete");
		cpf = participanteExterno.getPessoa().getCpf_cnpjString();
		buscarParticipanteExternoByCPF();
		tab = "EXTERNO";
		redirectMesmaPagina();
	}
	
	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}

	public Collection<AtividadeExtensao> getAtividadesLocalizadas() {
		return atividadesLocalizadas;
	}

	public void setAtividadesLocalizadas(
			Collection<AtividadeExtensao> atividades) {
		this.atividadesLocalizadas = atividades;
	}

	public String getBuscaNomeAtividade() {
		return buscaNomeAtividade;
	}

	public void setBuscaNomeAtividade(String buscaNome) {
		this.buscaNomeAtividade = buscaNome;
	}

	public Integer[] getBuscaTipoAtividade() {
		return buscaTipoAtividade;
	}

	public void setBuscaTipoAtividade(Integer[] buscaTipo) {
		this.buscaTipoAtividade = buscaTipo;
	}

	public Collection<TipoSituacaoProjeto> getSituacoesProjeto() {
		return situacoesProjeto;
	}

	public void setSituacoesProjeto(Collection<TipoSituacaoProjeto> situacoes) {
		this.situacoesProjeto = situacoes;
	}

	public Integer[] getBuscaSituacaoAtividade() {
		return buscaSituacaoAtividade;
	}

	public void setBuscaSituacaoAtividade(Integer[] buscaSituacao) {
		this.buscaSituacaoAtividade = buscaSituacao;
	}

	public Collection<AtividadeExtensao> getAtividadesGravadas() {
		return atividadesGravadas;
	}

	public void setAtividadesGravadas(Collection<AtividadeExtensao> atividades) {
		this.atividadesGravadas = atividades;
	}

	public void setGrupos(Collection<GrupoPublicoAlvo> gruposAlvo) {
		this.grupos = gruposAlvo;
	}

    /**
     * Utilizado no fluxo do cadastro da ação na tela de inclusão de membros na
     * ação de extensão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/membros_equipe.jsp</li> 
     * </ul>
     * @return Retorna um membro da equipe de extensão.
     */
	public MembroProjeto getMembroEquipe() {
		return membroEquipe;
	}

	public void setMembroEquipe(MembroProjeto membroEquipeAtividadeExtensao) {
		this.membroEquipe = membroEquipeAtividadeExtensao;
	}

	public Collection<MembroProjeto> getMembrosEquipe() throws DAOException {
		if (membrosEquipe == null) {
			MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
			try {
				membrosEquipe = dao.findAtivosByProjeto(obj.getProjeto().getId(),
						CategoriaMembro.DOCENTE, CategoriaMembro.DISCENTE, CategoriaMembro.SERVIDOR, CategoriaMembro.EXTERNO);
			} finally {
				dao.close();
			}
		}
		return membrosEquipe;
	}
	
	private boolean verificaDuplicidadePessoa(MembroProjeto membroProjeto) throws DAOException{
		for (MembroProjeto mp : getMembrosEquipe()) {
			if ( mp.getPessoa().getId() == membroProjeto.getPessoa().getId() )
				return false;
		}
		return true;
	}
	
    /**
     * Orcamento detalhado utilizado no formulário de definição do orçamento
     * detalhado de uma ação de extensão.
     * 
     * Método chamado pelas seguintes JSP's
     * <ul>
     * 		<li>sigaa.war/extesao/Atividade/orcamento.jsp</li>
     * </ul>
     * 
     * @return Orçamento detalhado da ação
     */
	public OrcamentoDetalhado getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(OrcamentoDetalhado orcamento) {
		this.orcamento = orcamento;
	}

	public int getBuscaAreaCNPq() {
		return buscaAreaCNPq;
	}

	public void setBuscaAreaCNPq(int buscaAreaCNPq) {
		this.buscaAreaCNPq = buscaAreaCNPq;
	}

	public int getBuscaUnidade() {
		return buscaUnidade;
	}

	public void setBuscaUnidade(int buscaUnidade) {
		this.buscaUnidade = buscaUnidade;
	}

	public int getBuscaAreaTematicaPrincipal() {
		return buscaAreaTematicaPrincipal;
	}

	public void setBuscaAreaTematicaPrincipal(int buscaAreaTematicaPrincipal) {
		this.buscaAreaTematicaPrincipal = buscaAreaTematicaPrincipal;
	}

	public Collection<AtividadeExtensao> getAtividadesMembroParticipa() {
		return atividadesMembroParticipa;
	}

	public void setAtividadesMembroParticipa(
			Collection<AtividadeExtensao> atividadesMembroParticipa) {
		this.atividadesMembroParticipa = atividadesMembroParticipa;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

    /**
     * Usado no form de cadastro de membros da equipe.
     * 
     * Método chamado pelas seguintes JSP's
     * <ul>
     * 		<li>sigaa.war/extensao/Atividade/membros_equipe.jsp</li>
     * 		<li>sigaa.war/extensao/AvaliacaoAtividade/confirmar_avaliacao.jsp</li>
     *  	<li>sigaa.war/extensao/AvaliacaoAtividade/form.jsp</li>
     *  	<li>sigaa.war/extensao/AvaliacaoAtividade/view.jsp</li>
     *  	<li>sigaa.war/extensao/DistribuicaoPereceristaExtensao/form.jsp</li>
     * </ul>
     * 
     * @return Retorna o servidor
     */
	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public ControleFluxoAtividadeExtensao getControleFluxo() {
		return this.controleFluxo;
	}

	public void setControleFluxo(ControleFluxoAtividadeExtensao controleFluxo) {
		this.controleFluxo = controleFluxo;
	}

	public boolean isCheckBuscaAreaCNPq() {
		return checkBuscaAreaCNPq;
	}

	public void setCheckBuscaAreaCNPq(boolean checkBuscaAreaCNPq) {
		this.checkBuscaAreaCNPq = checkBuscaAreaCNPq;
	}

	public boolean isCheckBuscaAreaTematicaPrincipal() {
		return checkBuscaAreaTematicaPrincipal;
	}

	public void setCheckBuscaAreaTematicaPrincipal(
			boolean checkBuscaAreaTematicaPrincipal) {
		this.checkBuscaAreaTematicaPrincipal = checkBuscaAreaTematicaPrincipal;
	}

	public boolean isCheckBuscaServidor() {
		return checkBuscaServidor;
	}

	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
		this.checkBuscaServidor = checkBuscaServidor;
	}

	public boolean isCheckBuscaSituacaoAtividade() {
		return checkBuscaSituacaoAtividade;
	}

	public void setCheckBuscaSituacaoAtividade(
			boolean checkBuscaSituacaoAtividade) {
		this.checkBuscaSituacaoAtividade = checkBuscaSituacaoAtividade;
	}

	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}

	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}

	public boolean isCheckBuscaUnidadeProponente() {
		return checkBuscaUnidadeProponente;
	}

	public void setCheckBuscaUnidadeProponente(
			boolean checkBuscaUnidadeProponente) {
		this.checkBuscaUnidadeProponente = checkBuscaUnidadeProponente;
	}

    /**
     * Utilizado na view como auxiliar para cadastrar outras unidades
     * proponentes da ação de extensão.
     * 
     * Método chamado pelas seguintes JSP's
     * 
     * <ul>
     * 		<li>sigaa.war/extensao/Atividade/dados_gerais.jsp</li>
     * </ul>
     * 
     * @return Unidade
     */
	public Unidade getOutraUnidade() {
		return outraUnidade;
	}

	public void setOutraUnidade(Unidade outraUnidade) {
		this.outraUnidade = outraUnidade;
	}

	public ParticipanteExterno getParticipanteExterno() {
		return participanteExterno;
	}

	public void setParticipanteExterno(ParticipanteExterno participanteExterno) {
		this.participanteExterno = participanteExterno;
	}

    /**
     * Utilizada durante o cadastro de uma ação de extensão quando se deseja
     * manipular outra ação. (Cadastro de programa, por exemplo).
     * 
     * Método chamado pelas seguintes JSP's
     * <ul>
     * 		<li>sigaa.war/extensao/Atividade/orcamento_aprovado.jsp</li>
     * 		<li>sigaa.war/extensao/Atividade/print.jsp</li>
     * 		<li>sigaa.war/extensao/Atividade/view.jsp</li>
     * 		<li>sigaa.war/extensao/Atividade/print.jsp</li>
     * 		<li>sigaa.war/public/extensao/view.jsp</li>
     * </ul>
     * 
     * @return Ação selecionada
     */
	public AtividadeExtensao getAtividadeSelecionada() {
		return atividadeSelecionada;
	}

	public void setAtividadeSelecionada(AtividadeExtensao atividadeSelecionada) {
		this.atividadeSelecionada = atividadeSelecionada;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public Integer getBuscaAno() {
		return buscaAno;
	}

	public void setBuscaAno(Integer buscaAno) {
		this.buscaAno = buscaAno;
	}

	public List<MensagemAviso> getAvisosAjax() {
		return avisosAjax;
	}

	public void setAvisosAjax(List<MensagemAviso> avisosAjax) {
		this.avisosAjax = avisosAjax;
	}

    /**
     * CPF utilizado na tela de cadastro dos servidores (participanteExterno).
     * 
     * Método chamado pelas seguintes JSP's
     * <ul>
     * 		<li>sigaa.war/extensao/Atividade/membros_equipe.jsp</li>
     * </ul>
     * 
     * @return Cpf
     */
	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public boolean isCheckBuscaRegistroSimplificado() {
		return checkBuscaRegistroSimplificado;
	}

	public void setCheckBuscaRegistroSimplificado(
			boolean checkBuscaRegistroSimplificado) {
		this.checkBuscaRegistroSimplificado = checkBuscaRegistroSimplificado;
	}

	public int getBuscaRegistroSimplificado() {
		return buscaRegistroSimplificado;
	}

	public void setBuscaRegistroSimplificado(int buscaRegistroSimplificado) {
		this.buscaRegistroSimplificado = buscaRegistroSimplificado;
	}

	public int getBuscaCentro() {
		return buscaCentro;
	}

	public void setBuscaCentro(int buscaCentro) {
		this.buscaCentro = buscaCentro;
	}

	public boolean isCheckBuscaCentro() {
		return checkBuscaCentro;
	}

	public void setCheckBuscaCentro(boolean checkBuscaCentro) {
		this.checkBuscaCentro = checkBuscaCentro;
	}

	public int getBuscaEdital() {
		return buscaEdital;
	}

	public void setBuscaEdital(int buscaEdital) {
		this.buscaEdital = buscaEdital;
	}

	public boolean isCheckBuscaEdital() {
		return checkBuscaEdital;
	}

	public void setCheckBuscaEdital(boolean checkBuscaEdital) {
		this.checkBuscaEdital = checkBuscaEdital;
	}

	public boolean isCheckBuscaFinanciamentoConvenio() {
		return checkBuscaFinanciamentoConvenio;
	}

	public void setCheckBuscaFinanciamentoConvenio(
			boolean checkBuscaFinanciamentoConvenio) {
		this.checkBuscaFinanciamentoConvenio = checkBuscaFinanciamentoConvenio;
	}

	public boolean isBuscaFinanciamentoInterno() {
		return buscaFinanciamentoInterno;
	}

	public void setBuscaFinanciamentoInterno(boolean buscaFinanciamentoInterno) {
		this.buscaFinanciamentoInterno = buscaFinanciamentoInterno;
	}

	public boolean isBuscaFinanciamentoExterno() {
		return buscaFinanciamentoExterno;
	}

	public void setBuscaFinanciamentoExterno(boolean buscaFinanciamentoExterno) {
		this.buscaFinanciamentoExterno = buscaFinanciamentoExterno;
	}

	public boolean isBuscaAutoFinanciamento() {
		return buscaAutoFinanciamento;
	}

	public void setBuscaAutoFinanciamento(boolean buscaAutoFinanciamento) {
		this.buscaAutoFinanciamento = buscaAutoFinanciamento;
	}

	public boolean isBuscaConvenioFunpec() {
		return buscaConvenioFunpec;
	}

	public void setBuscaConvenioFunpec(boolean buscaConvenioFunpec) {
		this.buscaConvenioFunpec = buscaConvenioFunpec;
	}

	public UploadedFile getFoto() {
		return foto;
	}

	public void setFoto(UploadedFile foto) {
		this.foto = foto;
	}

	public String getDescricaoFoto() {
		return descricaoFoto;
	}

	public void setDescricaoFoto(String descricaoFoto) {
		this.descricaoFoto = descricaoFoto;
	}

	public boolean isCheckBuscaPeriodo() {
		return checkBuscaPeriodo;
	}

	public void setCheckBuscaPeriodo(boolean checkBuscaPeriodo) {
		this.checkBuscaPeriodo = checkBuscaPeriodo;
	}

	public Date getBuscaInicio() {
		return buscaInicio;
	}

	public void setBuscaInicio(Date buscaInicio) {
		this.buscaInicio = buscaInicio;
	}

	public Date getBuscaFim() {
		return buscaFim;
	}

	public void setBuscaFim(Date buscaFim) {
		this.buscaFim = buscaFim;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public boolean isBotaoRelatorioContatoCoordenador() {
		return botaoRelatorioContatoCoordenador;
	}

	public void setBotaoRelatorioContatoCoordenador(
			boolean botaoRelatorioContatoCoordenador) {
		this.botaoRelatorioContatoCoordenador = botaoRelatorioContatoCoordenador;
	}

	public boolean isCheckBuscaTipoAtividade() {
		return checkBuscaTipoAtividade;
	}

	public void setCheckBuscaTipoAtividade(boolean checkBuscaTipoAtividade) {
		this.checkBuscaTipoAtividade = checkBuscaTipoAtividade;
	}

	public String getNomeEdital() {
		return nomeEdital;
	}

	public void setNomeEdital(String nomeEdital) {
		this.nomeEdital = nomeEdital;
	}

	public String getNomeTipoAcao() {
		return nomeTipoAcao;
	}

	public void setNomeTipoAcao(String nomeTipoAcao) {
		this.nomeTipoAcao = nomeTipoAcao;
	}

	public String getNomeAreaCnpq() {
		return nomeAreaCnpq;
	}

	public void setNomeAreaCnpq(String nomeAreaCnpq) {
		this.nomeAreaCnpq = nomeAreaCnpq;
	}

	public String getNomeUnidade() {
		return nomeUnidade;
	}

	public void setNomeUnidade(String nomeUnidade) {
		this.nomeUnidade = nomeUnidade;
	}

	public String getNomeCentro() {
		return nomeCentro;
	}

	public void setNomeCentro(String nomeCentro) {
		this.nomeCentro = nomeCentro;
	}

	public String getNomeArea() {
		return nomeArea;
	}

	public void setNomeArea(String nomeArea) {
		this.nomeArea = nomeArea;
	}

	public String getNomeSituacao() {
		return nomeSituacao;
	}

	public void setNomeSituacao(String nomeSituacao) {
		this.nomeSituacao = nomeSituacao;
	}

	public String getNomeTipoRegistro() {
		return nomeTipoRegistro;
	}

	public void setNomeTipoRegistro(String nomeTipoRegistro) {
		this.nomeTipoRegistro = nomeTipoRegistro;
	}

	public String getBuscaTitulo() {
		return buscaTitulo;
	}

	public void setBuscaTitulo(String buscaTitulo) {
		this.buscaTitulo = buscaTitulo;
	}

	public boolean isCheckBuscaCodigo() {
		return checkBuscaCodigo;
	}

	public void setCheckBuscaCodigo(boolean checkBuscaCodigo) {
		this.checkBuscaCodigo = checkBuscaCodigo;
	}

	public String getBuscaCodigo() {
		return buscaCodigo;
	}

	public void setBuscaCodigo(String codigo) {
		this.buscaCodigo = codigo;
	}

	/**
	 * Verifica se o edital da ação ainda está aberto.
	 * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>Não invocado por JSP's</li> 
     * </ul>
	 * @return
	 * @throws DAOException
	 */
	public boolean isEditalDestaAcaoAindaAberto() throws DAOException {
	    EditalDao dao = getDAO(EditalDao.class);
	    Collection<Edital> editais = dao.findAbertos(Edital.EXTENSAO, Edital.ASSOCIADO);
	    dao.initialize(obj.getEditalExtensao());
	    return obj.isPermitidoSelecionarEditaisEmAberto(editais);
	}

	public Date getBuscaInicioConclusao() {
	    return buscaInicioConclusao;
	}

	public void setBuscaInicioConclusao(Date buscaInicioConclusao) {
	    this.buscaInicioConclusao = buscaInicioConclusao;
	}

	public Date getBuscaFimConclusao() {
	    return buscaFimConclusao;
	}

	public void setBuscaFimConclusao(Date buscaFimConclusao) {
	    this.buscaFimConclusao = buscaFimConclusao;
	}

	public boolean isCheckBuscaPeriodoConclusao() {
	    return checkBuscaPeriodoConclusao;
	}

	public void setCheckBuscaPeriodoConclusao(boolean checkBuscaPeriodoConclusao) {
	    this.checkBuscaPeriodoConclusao = checkBuscaPeriodoConclusao;
	}

	public String getMsgCoordenadorPendenteRelatorio() {
		return msgCoordenadorPendenteRelatorio;
	}

	public void setMsgCoordenadorPendenteRelatorio(
			String msgCoordenadorPendenteRelatorio) {
		this.msgCoordenadorPendenteRelatorio = msgCoordenadorPendenteRelatorio;
	}

	public boolean isCheckBuscaAcaoSolicitacaoRenovacao() {
		return checkBuscaAcaoSolicitacaoRenovacao;
	}

	public void setCheckBuscaAcaoSolicitacaoRenovacao(
			boolean checkBuscaAcaoSolicitacaoRenovacao) {
		this.checkBuscaAcaoSolicitacaoRenovacao = checkBuscaAcaoSolicitacaoRenovacao;
	}

	public Date getDataNovaFinalizacao() {
	    return dataNovaFinalizacao;
	}

	public void setDataNovaFinalizacao(Date dataNovaFinalizacao) {
	    this.dataNovaFinalizacao = dataNovaFinalizacao;
	}

	public boolean isCheckBuscaProjetoAssociado() {
		return checkBuscaProjetoAssociado;
	}

	public void setCheckBuscaProjetoAssociado(boolean checkBuscaProjetoAssociado) {
		this.checkBuscaProjetoAssociado = checkBuscaProjetoAssociado;
	}
	
	/**
	 * Visualiza dados detalhados da ação de extensão para confirmar a execução da mesma.
	 * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/lista_minhas_atividades.jsp</li> 
     * </ul>
	 * 
	 * @return Página com dados do projeto.
	 */
	public String confirmarExecucao() {
	    setId();
	    if (obj.getId() > 0) {
		try {
		    
		    obj = getGenericDAO().findByPrimaryKey(obj.getId(), AtividadeExtensao.class);		    
		    obj.setOrcamentosDetalhados(getGenericDAO().findByExactField(OrcamentoDetalhado.class, "projeto.id", atividadeSelecionada.getProjeto().getId()));

		    // evitar erro de lazy na busca do coordenador
		    obj.setMembrosEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", obj.getProjeto().getId()));
		    
		    if(obj.getProjeto().getCoordenador().getPessoa().getId() != getUsuarioLogado().getPessoa().getId()){
		    	addMensagemWarning("Apenas o coordenador pode executar a Ação de Extensão.");
		    	return null;
		    }
		    
		    recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());
		    
		    // listar os discentes envolvidos na atividade ordenados por nome, evitando erro de LazyInicialization
		    obj.setDiscentesSelecionados(getDAO(DiscenteExtensaoDao.class).findByAtividade(obj.getId()));
		    for (DiscenteExtensao de : obj.getDiscentesSelecionados()) {
			de.getDiscente().getMatriculaNome();
		    }

		    HistoricoSituacaoProjetoDao dao = getDAO(HistoricoSituacaoProjetoDao.class);
		    obj.getProjeto().setHistoricoSituacao(dao.relatorioHistoricoByIdProjeto(obj.getProjeto().getId()));

		    prepareMovimento(SigaaListaComando.EXECUTAR_ATIVIDADE_EXTENSAO);
		    prepareMovimento(SigaaListaComando.NAO_EXECUTAR_ATIVIDADE_EXTENSAO);
		    return forward(ConstantesNavegacao.CONFIRMAR_EXECUCAO);

		} catch (Exception e) {
		    notifyError(e);
		    addMensagemErro(e.getMessage());
		    return null;
		}
	    } else {
		addMensagemErro("Ação não selecionada");
	    }

	    return null;
	}
	
	/**
	 * Projeto entra em execução após aprovação do orientador.
	 * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/confirmar_execucao.jsp</li> 
     * </ul>
     * 
	 * @return
	 * @throws ArqException
	 */
	public String executarAcaoExtensao() throws ArqException{
	    
	    CadastroExtensaoMov mov = new CadastroExtensaoMov();	
	    mov.setCodMovimento(SigaaListaComando.EXECUTAR_ATIVIDADE_EXTENSAO);
	    mov.setAtividade(obj);
	    try {
		execute(mov);
		addMensagem(OPERACAO_SUCESSO);
	    } catch (NegocioException e) {
		addMensagemErro(e.getMessage());
	    }
	    return listarMinhasAtividades();
	}

	/**
	 * Negar execução do projeto pelo orientador.
	 * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/extensao/Atividade/confirmar_execucao.jsp</li> 
     * </ul>
     * 
	 * @return
	 * @throws ArqException
	 */
	public String naoExecutarAcaoExtensao() throws ArqException{
	    
	    CadastroExtensaoMov mov = new CadastroExtensaoMov();	
	    mov.setCodMovimento(SigaaListaComando.NAO_EXECUTAR_ATIVIDADE_EXTENSAO);
	    mov.setAtividade(obj);
	    try {
		execute(mov);
		addMensagem(OPERACAO_SUCESSO);
	    } catch (NegocioException e) {
		addMensagemErro(e.getMessage());
	    }
	    return listarMinhasAtividades();
	}
	
	/** Atributo utilizado para armazenar a URL do botao voltar 
	 *	<br>
	 *	Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *			<li>/sigaa.war/extensao/Atividade/dados_gerais.jsp</li>
	 *	</ul> 
	 */
	public String voltar(){
		return redirect("/sigaa/extensao/Atividade/seleciona_atividade.jsf");
	}
	
	/**
	 * Visualiza participantes de uma ação de extensão
	 *	<br>
	 *	Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.war/extensao/Atividade/view.jsp</li>
	 *	</ul> 
	 *  
	 * */
	public String viewParticipantes() throws DAOException{
		clearMensagens();
		Integer idAcao = getParameterInt("id", 0);
		atividadeSelecionada.setParticipantes(getDAO(ParticipanteAcaoExtensaoDao.class).findByAcao(idAcao));
	    if(atividadeSelecionada.getParticipantesNaoOrdenados() == null || atividadeSelecionada.getParticipantesNaoOrdenados().size() <= 0){
	    	addMensagemWarning("Não há participantes cadastrados para esta ação de extensão.");
	    	return null;
	    }
		
		return forward(ConstantesNavegacao.VIEW_PARTICIPANTES);
	}
	
	public void adicionarLocalRealizacao() throws DAOException {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequiredId(obj.getLocalRealizacao().getMunicipio().getUnidadeFederativa().getId(), "Estado", lista);
		ValidatorUtil.validateRequiredId(obj.getLocalRealizacao().getMunicipio().getId(), "Município", lista);
		if ( !lista.isEmpty() ) {
			addMensagensAjax(lista);
			return;
		}

		GenericDAO dao = getGenericDAO(); 
		try {
			obj.getLocalRealizacao().setMunicipio( 
					dao.findByPrimaryKey( obj.getLocalRealizacao().getMunicipio().getId(), Municipio.class) );
			obj.getLocalRealizacao().getMunicipio().setUnidadeFederativa( 
					dao.findByPrimaryKey( obj.getLocalRealizacao().getMunicipio().getUnidadeFederativa().getId(), UnidadeFederativa.class) );
			if ( !obj.getLocaisRealizacao().contains(obj.getLocalRealizacao()) ) {
				obj.getLocalRealizacao().setAtivo(Boolean.TRUE);
				obj.getLocalRealizacao().setAtividade(obj);
				obj.getLocaisRealizacao().add( obj.getLocalRealizacao() );
				locaisRealizacao = new ListDataModel();
				obj.setLocalRealizacao(new LocalRealizacao());
			} else {
				addMensagemErroAjax("Local de Realização já cadastrado.");
				return;
			}
		} finally {
			dao.close();
		}
	}
	
	public void removeLocalRealizacao() throws DAOException {

		LocalRealizacaoDao dao = getDAO(LocalRealizacaoDao.class);

		obj.setLocalRealizacao( (LocalRealizacao) locaisRealizacao.getRowData() );
		try {
			if (obj.getLocaisRealizacao().contains((LocalRealizacao) locaisRealizacao.getRowData())) {
				dao.updateField(LocalRealizacao.class, obj.getLocalRealizacao().getId(), "ativo", Boolean.FALSE);
				obj.getLocaisRealizacao().remove( obj.getLocalRealizacao() );
				addMensagemInfoAjax("Local de Realização: Removido com sucesso.");
				obj.setLocalRealizacao(new LocalRealizacao());
			} 
		} finally {
			dao.close();
		}
	}
	
	/**
	 * 
	 * Redireciona para a página de notificação de relatórios pendentes.
	 * Não é chamado por JSP
	 * 
	 * @return
	 */
	public String listarAcoesPendentesRelatorios() {
		setSubSistemaAtual(SigaaSubsistemas.PORTAL_DOCENTE);
		return redirectJSF("/extensao/RelatorioAcaoExtensao/notificacaoRelatoriosPendentesLogin.jsp");
	}	

	public Date getBuscaInicioExecucao() {
	    return buscaInicioExecucao;
	}

	public void setBuscaInicioExecucao(Date buscaInicioExecucao) {
	    this.buscaInicioExecucao = buscaInicioExecucao;
	}

	public Date getBuscaFimExecucao() {
	    return buscaFimExecucao;
	}

	public void setBuscaFimExecucao(Date buscaFimExecucao) {
	    this.buscaFimExecucao = buscaFimExecucao;
	}

	public boolean isCheckBuscaPeriodoInicio() {
	    return checkBuscaPeriodoInicio;
	}

	public void setCheckBuscaPeriodoInicio(boolean checkBuscaPeriodoInicio) {
	    this.checkBuscaPeriodoInicio = checkBuscaPeriodoInicio;
	}

	public int getBuscaAcaoSolicitacaoRenovacao() {
	    return buscaAcaoSolicitacaoRenovacao;
	}

	public void setBuscaAcaoSolicitacaoRenovacao(int buscaAcaoSolicitacaoRenovacao) {
	    this.buscaAcaoSolicitacaoRenovacao = buscaAcaoSolicitacaoRenovacao;
	}

	public int getBuscaProjetoAssociado() {
	    return buscaProjetoAssociado;
	}

	public void setBuscaProjetoAssociado(int buscaProjetoAssociado) {
	    this.buscaProjetoAssociado = buscaProjetoAssociado;
	}

	public FuncaoMembro getFuncaoDocente() {
	    return funcaoDocente;
	}

	public void setFuncaoDocente(FuncaoMembro funcaoDocente) {
	    this.funcaoDocente = funcaoDocente;
	}

	public FuncaoMembro getFuncaoDiscente() {
	    return funcaoDiscente;
	}

	public void setFuncaoDiscente(FuncaoMembro funcaoDiscente) {
	    this.funcaoDiscente = funcaoDiscente;
	}

	public FuncaoMembro getFuncaoServidor() {
	    return funcaoServidor;
	}

	public void setFuncaoServidor(FuncaoMembro funcaoServidor) {
	    this.funcaoServidor = funcaoServidor;
	}

	public FuncaoMembro getFuncaoExterno() {
	    return funcaoExterno;
	}

	public void setFuncaoExterno(FuncaoMembro funcaoExterno) {
	    this.funcaoExterno = funcaoExterno;
	}

	public boolean isBuscaRecebeuFinanciamentoInterno() {
		return buscaRecebeuFinanciamentoInterno;
	}

	public void setBuscaRecebeuFinanciamentoInterno(
			boolean buscaRecebeuFinanciamentoInterno) {
		this.buscaRecebeuFinanciamentoInterno = buscaRecebeuFinanciamentoInterno;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public boolean isDocenteGerenciaParticipantes() {
		return docenteGerenciaParticipantes;
	}

	public void setDocenteGerenciaParticipantes(boolean docenteGerenciaParticipantes) {
		this.docenteGerenciaParticipantes = docenteGerenciaParticipantes;
	}

	public DataModel getLocaisRealizacao() {
		if (locaisRealizacao.getRowCount() == -1) {
			locaisRealizacao = new ListDataModel(obj.getLocaisRealizacao()); 
		}
		return locaisRealizacao;
	}

	public void setLocaisRealizacao(DataModel locaisRealizacao) {
		this.locaisRealizacao = locaisRealizacao;
	}

	public Collection<AtividadeExtensao> getAtividadesMembroCoordena() {
		return atividadesMembroCoordena;
	}

	public void setAtividadesMembroCoordena(
			Collection<AtividadeExtensao> atividadesMembroCoordena) {
		this.atividadesMembroCoordena = atividadesMembroCoordena;
	}
	
	/**
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.war/extensao/Atividade/include/dados_atividade.jsp</li>
	 *	</ul
	 * @return Denominação do executor financeiro.
	 */
	public String getExecutorFinanceiro() throws DAOException{
		ExecutorFinanceiro e = new ExecutorFinanceiro();
		if (obj.getExecutorFinanceiro() != null){
			 e = getGenericDAO().findByPrimaryKey(obj.getExecutorFinanceiro().getId(), ExecutorFinanceiro.class);
			 if ( e!= null){
				 return e.getDenominacao();
			 }
		}
		return "";
	}
	
	/**
	 *	Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>/sigaa.war/extensao/Atividade/include/dados_atividade.jsp</li>
	 *	</ul> 
	 * @return Descrição do programa estratégico.
	 */
	public String getProgramaEstrategico() throws DAOException{
		ProgramaEstrategicoExtensao p = new ProgramaEstrategicoExtensao();
		if (obj.getProgramaEstrategico() != null){
			 p = getGenericDAO().findByPrimaryKey(obj.getProgramaEstrategico().getId(), ProgramaEstrategicoExtensao.class);
		}
		return p.getDescricao();
	}
	
}