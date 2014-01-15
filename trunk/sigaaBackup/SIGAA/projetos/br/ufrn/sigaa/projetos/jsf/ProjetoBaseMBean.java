/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */
package br.ufrn.sigaa.projetos.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.PagingInformation;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.HistoricoSituacaoProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.dao.projetos.OrcamentoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoAssociadoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.extensao.dominio.ParticipanteExterno;
import br.ufrn.sigaa.extensao.dominio.ResumoOrcamentoDetalhado;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.jsf.AtividadeExtensaoMBean;
import br.ufrn.sigaa.extensao.jsf.CursoEventoMBean;
import br.ufrn.sigaa.extensao.jsf.ProgramaMBean;
import br.ufrn.sigaa.extensao.jsf.ProjetoExtensaoMBean;
import br.ufrn.sigaa.monitoria.dominio.ComponenteCurricularMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino;
import br.ufrn.sigaa.monitoria.jsf.ConstantesNavegacaoMonitoria;
import br.ufrn.sigaa.monitoria.jsf.ProjetoMonitoriaMBean;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaValidator;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.pesquisa.form.TelaCronograma;
import br.ufrn.sigaa.pesquisa.negocio.ProjetoPesquisaValidator;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.projetos.dominio.ArquivoProjeto;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.ClassificacaoFinanciadora;
import br.ufrn.sigaa.projetos.dominio.CronogramaProjeto;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.FotoProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.negocio.MembroProjetoHelper;
import br.ufrn.sigaa.projetos.negocio.MembroProjetoValidator;
import br.ufrn.sigaa.projetos.negocio.ProjetoBaseValidator;

/*******************************************************************************
 * MBean responsável pelo controle geral do cadastro de proposta de ação
 * acadêmica integrada.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Scope("session")
@Component("projetoBase")
public class ProjetoBaseMBean extends SigaaAbstractController<Projeto> {

	/** controla o fluxo das tela no cadastro de projetos */
    private ControleFluxo controleFluxo;

    /** Constante utilizada na redimencionalização das fotos incluídas no projeto. */
    private static final int HEIGHT_FOTO = 100;
    /** Constante utilizada na redimencionalização das fotos incluídas no projeto. */
    private static final int WIDTH_FOTO = 100;    

    /** Dados gerais*/
    private Collection<TipoSituacaoProjeto> situacoesProjeto = new HashSet<TipoSituacaoProjeto>();

    /** Cronograma */
    private TelaCronograma telaCronograma = new TelaCronograma();

    /** Orçamento */
    private OrcamentoDetalhado orcamento = new OrcamentoDetalhado();
    /** Atributo utilizado para representar a tabela orçamentária do projeto */
    private Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();

    /** Atributo utilizado para representar o membroEquipe */
    private MembroProjeto membroEquipe = new MembroProjeto();
    /** Atributo utilizado para representar o Servidor */
    private Servidor servidor = new Servidor();
    /** Atributo utilizado para representar o Docente */
    private Servidor docente = new Servidor();
    /** Atributo utilizado para representar o Discente */
    private Discente discente = new Discente();
    /** Atributo utilizado para representar o Participante Externo */
    private ParticipanteExterno participanteExterno = new ParticipanteExterno();    
    /** Atributo utilizado para representar o CPF em forma de String */
    private String cpf = new String("");

    /** Atributo utilizado para representar o arquivo a ser Upado */
    private UploadedFile file;
    /** Atributo utilizado para representar a foto a ser Upada */
    private UploadedFile foto;
    /** Atributo utilizado para representa a descrição do arquivo */
    private String descricaoArquivo;
    /** Atributo utilizado para representar a descrição da foto */
    private String descricaoFoto;

    /** Utilizado como auxiliar*/
    private Projeto projeto = new Projeto();

    /** Utilizado na lista de projetos pendentes de conclusão*/
    private Collection<Projeto> projetosGravados = new ArrayList<Projeto>();
    /** Atributo utilizado para representar a lista de projetos pendentes de conclusão */
    private Collection<Projeto> meusProjetos = new ArrayList<Projeto>();
    /** Função de Membro para Docente */
    private FuncaoMembro funcaoDocente = new FuncaoMembro();
    /** Função de Membro para Discente */
    private FuncaoMembro funcaoDiscente = new FuncaoMembro();
    /** Função de Membro para Servidor */
    private FuncaoMembro funcaoServidor = new FuncaoMembro();
    /** Função de Membro para Externo */
    private FuncaoMembro funcaoExterno = new FuncaoMembro();
    
    /**
     * Construtor padrão. 
     * Inicializa o MBean removendo objetos antigos de sessão e
     * instânciando outros objetos importantes.
     * 
     */
    public ProjetoBaseMBean() {
    	clear();
    }
    
    /** Verifica compatilidade de pepeis do usuário.     
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>Não invocado por jsp</li>
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
	 *  	<li>/sigaa.war/projetos/ProjetoBase/projetos_pendentes.jsp</li>
	 * </ul>
     * @param evt
     * @throws SegurancaException
     */
    public void checkChangeRole(PhaseEvent evt) throws SegurancaException{
    	checkChangeRole();
    }

    /**
     * Inicializa objetos importantes no cadastro de um novo projeto.
     * 
     */
    private void clear() {
		obj = new Projeto(new TipoProjeto(TipoProjeto.ASSOCIADO));
		
		obj.setRenovacao(false); //TODO: Retirar e exibir no formulário após a submissão dos projetos de 2011.
		
		obj.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO));
		membroEquipe.setFuncaoMembro(new FuncaoMembro());
		orcamento = new OrcamentoDetalhado();
		tabelaOrcamentaria.clear();
		getCurrentSession().setAttribute("idElementoDespesa", 0);
    }

    /** Inicializa objetos importantes no cadastro de um novo projeto e faz um redirecionamento
     * para a página de seleção de projetos que compõe o projetos associado.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/portais/docente/docente.jsp</li>
     * 		<li>/sigaa.war/portais/docente/menu_docente.jsp</li>
     * 		<li>/sigaa.war/projetos/Edital/buscar.jsp</li>
     * 		<li>/sigaa.war/projetos/ProjetoBase/projetos_pendentes.jsp</li>
     * 		<li>/sigaa.war/projetos/ProjetoBase/resumo_integrado.jsp</li>
     * 		<li>/sigaa.war/projetos/ProjetoBase/selecionar_projeto.jsp</li>
     * </li>
     * @throws SegurancaException 
     * 
     */
    public String iniciar() throws SegurancaException {
    	checkChangeRole();
    	clear();
    	return forward(ConstantesNavegacaoProjetos.SELECIONAR_PROJETO);
    }

    /**
     * Verifica se o cadastro está com o fluxo configurado e ativo.
     * 
     * @return
     */
    private boolean isFluxoAtivo() {
		if (controleFluxo != null) {
		    return true;
		}else {
		    addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
		    return false;
		}
    }
    
    /**
     * Inicia cadastro de um projeto integrado (Projeto Interno).
     * A escolha desta opção permite o cadastro de projeto acadêmico 
     * que solicite apoio financeiro por meio de algum Edital Interno.
     * 
     *  <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
     * </ul>
     * 
     * @return inicial do cadastro.
     * @throws SegurancaException 
     */
    public String iniciarProjetoInternoComFinanciamentoInterno() throws SegurancaException {
    	checkChangeRole();

    	try {
    		clear();
    		obj.setInterno(true);
    		obj.setFinanciamentoInterno(true);

    		// Realizar validações
    		ListaMensagens mensagens = new ListaMensagens();
    		ProjetoBaseValidator.validaEditaisAbertos(obj, mensagens);

    		if (!mensagens.isEmpty()) {
    			addMensagens(mensagens);
    			return null;
    		}

    		prepareMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
    		prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_BASE);
    		controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_BASE);
    		return forward(ConstantesNavegacaoProjetos.DIMENSAO_ACADEMICA);
    	} catch (ArqException e) {
    		notifyError(e);
    		addMensagemErroPadrao();
    		return null;
    	}
    }

    /**
     * Inicia cadastro de um projeto integrado (Projeto Interno).
     * A escolha desta opção permite o cadastro de projeto acadêmico 
     * sem apoio financeiro de editais da instituição.
     * 
     *  <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
     * </ul>
     * 
     * @return inicial do cadastro.
     * @throws SegurancaException 
     */
    public String iniciarProjetoInternoSemFinanciamentoInterno() throws SegurancaException {
    	checkChangeRole();

    	try {
    		clear();
    		obj.setInterno(true);
    		obj.setFinanciamentoInterno(false);

    		prepareMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
    		prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_BASE);
    		controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_BASE);
    		return forward(ConstantesNavegacaoProjetos.DIMENSAO_ACADEMICA);
    	} catch (ArqException e) {
    		notifyError(e);
    		addMensagemErroPadrao();
    		return null;
    	}
    }

    
    /**
     * Inicia cadastro de um projeto externo.
     *  <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
     * </ul>
     * 
     * 
     * @return inicial do cadastro.
     * @throws SegurancaException 
     */
    public String selecionarTipoProjetoIntegrado() throws SegurancaException {    	
	    checkChangeRole();
	    return forward(ConstantesNavegacaoProjetos.SELECIONAR_TIPO_PROJETO_INTEGRADO);
    }

    
    
    /**
     * CADASTRAR PROJETO ACADÊMICO PARA TRAMITAÇÃO DE INSTRUMENTO JURÍDICO NA UFRN
     * 
     * Escolha esta opção caso deseje cadastrar um projeto acadêmico que será base para um convênio, contrato, 
     * termo de cooperação ou outro instrumento.  Nestes casos, o projeto deverá seguir os seguintes passos:
     * 1. Cadastrar Projeto Acadêmico (esta operação em questão)
     * 2. Encaminhar projeto para a PROPLAN através do Portal Docente -> Convênios -> Projeto/Plano de Trabalho.
     * 
     * Nestes casos, o projeto será cadastrado como REGISTRADO e só entrará em execução após a 
     * finalização dos instrumentos legais.
     * 
     * Inicia cadastro de um projeto externo para Convênio.
     *  <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
     * </ul>
     * 
     * @return inicial do cadastro.
     * @throws SegurancaException 
     */
    public String iniciarProjetoExternoConvenio() throws SegurancaException {
    	checkChangeRole();
    	try {
    		clear();
    		obj.setExterno(true);
    		obj.setConvenio(true);
    		obj.setFinanciamentoInterno(false);
    		prepareMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
    		prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_BASE);
    		controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_BASE);
    		return forward(ConstantesNavegacaoProjetos.DIMENSAO_ACADEMICA);
    	} catch (ArqException e) {
    		notifyError(e);
    		addMensagemErroPadrao();
    		return null;
    	}
    }
    
    /**
     * CADASTRAR PROJETO JÁ APROVADO EM EDITAIS DE PESQUISADOR INDIVIDUAL
     * 
     * Escolha esta opção caso deseje cadastrar projetos que foram aprovados junto ao CNPQ, 
     * FAPERN, etc a relação é entre o órgão financiador e o pesquisador. 
     * Não há recebimento de recursos nem execução na UFRN.
     * 
     * Nestes casos, o seu projeto já será considerado EM EXECUÇÂO.
     * 
     * Inicia cadastro de um projeto externo de Pesquisador Individual.
     *  <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
     * </ul>
     * 
     * @return inicial do cadastro.
     * @throws SegurancaException 
     */
    public String iniciarProjetoExternoPesquisadorIndividual() throws SegurancaException {
    	checkChangeRole();
    	try {
    		clear();
    		obj.setExterno(true);
    		obj.setFinanciamentoInterno(false);
    		obj.setConvenio(false);
    		prepareMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
    		prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_BASE);
    		controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_BASE);
    		return forward(ConstantesNavegacaoProjetos.DIMENSAO_ACADEMICA);
    	} catch (ArqException e) {
    		notifyError(e);
    		addMensagemErroPadrao();
    		return null;
    	}
    }


    
    
    /**
     * Redireciona para a ação que inicia o cadastro de um projeto de Ensino
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/projetos/ProjetoBase/selecionar_projeto.jsp</li>
     * </ul>
     * @return
     * @throws SegurancaException
     */
    public String iniciarEnsino() throws SegurancaException {	
    	checkChangeRole();
		return ((ProjetoMonitoriaMBean)getMBean("projetoMonitoria")).iniciarProjetoMonitoria();
    }

    /**
     * Redireciona para a ação que inicia o cadastro de um projeto de pesquisa
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/selecionar_projeto.jsp</li></ul>
     * 
     * @return
     * @throws SegurancaException
     */
    public String iniciarPesquisa() throws SegurancaException {
    	checkChangeRole();
		return redirect(getParameter("linkPesquisa"));
    }

    /**
     * Redireciona para a ação que inicia o cadastro de um projeto de Extensão
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/projetos/ProjetoBase/selecionar_projeto.jsp</li>
     * </ul>
     * @return
     * @throws SegurancaException 
     */
    public String iniciarExtensao() throws SegurancaException {	
    	checkChangeRole();
    	return ((AtividadeExtensaoMBean)getMBean("atividadeExtensao")).iniciarPropostaCompleta();	
    }

    /**
     * Método que retorna o próximo passo do fluxo em execução.
     * Grava os dados da tela atual e passa para próxima tela do fluxo.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war\projetos\ProjetoBase\passos_projeto.jsp</li>
     * </ul>
     * @return retorna o próximo passo do fluxo 
     */
    public String proximoPasso() {
	if (isFluxoAtivo()) {
	    try {
		gravarTemporariamente();
		iniciarCombosDadosGerais();
		return forward(controleFluxo.proximoPasso());
	    } catch (NegocioException e) {
		addMensagens(e.getListaMensagens());
	    } catch (ArqException e) {
		tratamentoErroPadrao(e);
	    }
	}else {
	    return cancelar();
	}
	return null;    	
    }

    /**
     * Método que retorna o passo anterior do fluxo em execução.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/projetos/ProjetoBase/passos_projeto.jsp</li>
     * </ul>
     * @return retorna o passo anterior do fluxo em execução
     */
    public String passoAnterior() {
	if (isFluxoAtivo()) {
	    return forward(controleFluxo.passoAnterior());
	}else {
	    return cancelar();
	}
    }

    /**
     * Carrega as informações do edital selecionado na página
     * <br>Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/dados_gerais.jsp</li></ul>
     * @throws DAOException
     */
    public void changeEdital() throws DAOException{
	if(obj.getEdital() != null && obj.getEdital().getId() > 0){
	    obj.setEdital(getGenericDAO().findByPrimaryKey(obj.getEdital().getId(), Edital.class));
	    obj.setAno(obj.getEdital().getAno());
	    obj.setDataInicio(obj.getEdital().getInicioRealizacao());
	    obj.setDataFim(obj.getEdital().getFimRealizacao());
	}else{
	    obj.setEdital(new Edital());
	    obj.setAno(null);
	    obj.setDataInicio(null);
	    obj.setDataFim(null);
	}
    }
    
    /**
     * Método usado para iniciar o primeiro passo (dados gerais). Vai para a
     * tela de dados específicos.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul><li> sigaa.war/projetos/ProjetoBase/dados_gerais.jsp</li></ul>
     * 
     * @return página a ser carregada
     */
    public String submeterDadosGerais() {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}
	
	try {
		
		// Realizar validações
	    ListaMensagens mensagens = new ListaMensagens();
	    
	    ProjetoBaseValidator.validaDadosGerais(obj, mensagens);

	    if (!mensagens.isEmpty()) {
		addMensagens(mensagens);
		return null;
	    }

	    // Inicia atributos selecionados pelo usuário em listas
	    GenericDAO dao = getGenericDAO();
	    dao.refresh(obj.getAreaConhecimentoCnpq());
	    dao.initialize(obj.getUnidade());
	    dao.initialize(obj.getSituacaoProjeto());
	    dao.initialize(obj.getAbrangencia());
	    if (obj.isFinanciamentoExterno()) {
		dao.initialize(obj.getClassificacaoFinanciadora());
	    }	    

	} catch (DAOException e) {
	    tratamentoErroPadrao(e);
	    return null;
	}
	
	iniciarCronograma();
	return proximoPasso();
    }



    /**
     * Método usado para avançar submetendo os dados da dimensão
     * acadêmica da proposta do projeto base. 
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/dimensao_academica.jsp</li></ul>
     * 
     * @return página a ser carregada
     */
    public String submeterDimensaoAcademica() {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

	try {
	    ListaMensagens mensagens = new ListaMensagens();
	    ProjetoBaseValidator.validaDimensaoAcademica(obj, mensagens);
	    if (!mensagens.isEmpty()) {
		addMensagens(mensagens);
		return null;
	    }
	    return forward(controleFluxo.proximoPasso());
	} catch (DAOException e) {
	    tratamentoErroPadrao(e);
	    return null;
	}
    }



    /**
     * Inicia a tela do cronograma.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul><li> sigaa.war/projetos/ProjetoBase/cronograma.jsp</li></ul>
     * 
     */
    public void iniciarCronograma() {
	TelaCronograma cronograma = new TelaCronograma(obj.getDataInicio(), obj.getDataFim(), obj.getCronograma());
	setTelaCronograma(cronograma);
    }

    /**
     * Submete o cronograma a validação dos dados digitados e em seguida 
     * redireciona o usuário para próxima tela do fluxo.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/cronograma.jsp</li></ul>
     * 
     * @return Próxima tela do fluxo. 
     * @throws DAOException Gerado pela busca dos dados para validaçao.
     */
    public String submeterCronograma() {
    	if (!isFluxoAtivo()) {
    		return cancelar();
    	}else {
    		if (isCronogramaValidado()) {
    			return proximoPasso();
    		}
    	}
    	return null;
    }
    
    /**
     * Realiza a validação do cronograma que será submetido.
     * 
     * Não é chamado por JSP.
     * 
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    private boolean isCronogramaValidado() {
    	try {
	    	// Obter objetos cronogramas a partir dos dados do formulário
	    	String[] atividadesDesenvolvidas = getCurrentRequest().getParameterValues("telaCronograma.atividade");
	    	getTelaCronograma().setAtividade(atividadesDesenvolvidas);
	
	    	// Valida se todas as atividades adicionadas foram preenchidas
	    	if (ValidatorUtil.isEmpty(atividadesDesenvolvidas)) {
	    		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Atividade");
	    		return false;
	    	}else{
	    		for (int i=0;i<atividadesDesenvolvidas.length; i++){
	    			if(ValidatorUtil.isEmpty(atividadesDesenvolvidas[i])){
	    				//addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Atividade");
	    				addMensagemErro("Caro usuário, você deve preencher todas as descrições de Atividades Envolvidas");
	    				return false;
	    			}
	    		}
	    	}
	
	    	String[] calendario = getCurrentRequest().getParameterValues("telaCronograma.calendario");
	    	getTelaCronograma().setCalendario(calendario);
	
	    	// Valida se há um cronograma para cada atividade adicionada.
	    	if (ValidatorUtil.isEmpty(calendario)) {
	    		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Cronograma do Projeto");
	    		return false;
	    	}
	
	    	// Obtendo descrição das atividades desenvolvidas do cronograma a partir da view.
	    	getTelaCronograma().definirCronograma(getCurrentRequest());
	    	List<CronogramaProjeto> cronogramas = getTelaCronograma().getCronogramas();
	    	for (CronogramaProjeto cronograma : cronogramas) {
	    		cronograma.setProjeto(obj);
	    	}
	    	obj.setCronograma(cronogramas);
	
	    	// Validar dados gerais do plano de trabalho
	    	ListaMensagens mensagens = new ListaMensagens();
	    	ProjetoBaseValidator.validarCronograma(obj, mensagens);
	    	if (!mensagens.isEmpty()) {
	    		addMensagens(mensagens);
	    		return false;
	    	}
	    	
	    	return true;
    	}catch (Exception e) {
    		addMensagemErro("Erro ao recuperar dados do informado.");
    		return false;
		}
    }
    

    /**
     * Inicia o procedimento de alteração do cronograma da ação Acadêmica.
     * 
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/meus_projetos.jsp</li>
     * </ul>
     * 
     * @return Página com dados do projeto.
     */
    public String iniciarAtualizarCronograma() throws DAOException {
    	int id = getParameterInt("id", 0);
    	obj = getGenericDAO().findByPrimaryKey(id, Projeto.class);
    	
    	//Evitar org.hibernate.LazyInitializationException
    	if (obj.getCoordenador() != null)
    		getGenericDAO().refresh(obj.getCoordenador());
    	
    	iniciarCronograma();
    	setConfirmButton("Salvar");
    	controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_BASE);    	
    	return forward(controleFluxo.goPassoByURL(ConstantesNavegacaoProjetos.CRONOGRAMA));
    }


    /**
     * Altera o cronograma da ação Acadêmica.
     * 
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/cronograma.jsp</li>
     * </ul>
     * 
     * @return Página com dados do projeto.
     * @throws ArqException 
     */
    public String atualizarCronograma() throws ArqException {
    	if (isCronogramaValidado()) {
	    	try {
		    	MovimentoCadastro mov = new MovimentoCadastro();
			    mov.setObjMovimentado(obj);
			    mov.setObjAuxiliar(controleFluxo);
			    mov.setCodMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
			    prepareMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
			    obj = (Projeto) execute(mov, getCurrentRequest());
			    setConfirmButton(null);
			    return listarMeusProjetos();
	    	}catch (NegocioException e) {
	    		addMensagens(e.getListaMensagens());
			}
    	}
    	return null;
    }
    
    
    /**
     * Método utilizado para salvar um membro da equipe do projeto.
     * 
     * @param membro
     * @throws ArqException
     * @throws NegocioException
     */
    private MembroProjeto salvarMembroEquipe(MembroProjeto membro) throws ArqException, NegocioException {
		Comando ultimoComando = getUltimoComando();	    
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(membro);
		mov.setCodMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
		prepareMovimento(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE);
		membro = execute(mov, getCurrentRequest());
		prepareMovimento(ultimoComando);
		obj.getEquipe().add(membro);
		return membro;
    }
    
    
    /**
     * Adiciona um docente a lista de membros da equipe do projeto.
     * 
     * @return
     */
    private String adicionarDocente() {
		ListaMensagens mensagens = new ListaMensagens();
		try {
		    //setando a aba atual
		    getCurrentSession().setAttribute("aba", "membro-docente");	    
		    getCurrentSession().setAttribute("categoriaAtual",CategoriaMembro.DOCENTE);
		    
		    membroEquipe.setServidor(getGenericDAO().findByPrimaryKey(docente.getId(), Servidor.class));
		    membroEquipe.setFuncaoMembro(funcaoDocente);
		    
		    boolean validado = MembroProjetoHelper.adicionarDocente(getGenericDAO(), membroEquipe, obj, mensagens);	    
		    if (validado) {
			salvarMembroEquipe(membroEquipe);
			popularDadosMembroProjeto();
		    }else {
			addMensagens(mensagens);
		    }
		    
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());	    
		} catch (ArqException e) {
		    tratamentoErroPadrao(e);
		}
	
		return null;
    }


    /**
     * Adiciona um técnico-administrativo a lista de membros da equipe do projeto.
     * 
     * @return
     */
    private String adicionarServidor() {
		ListaMensagens mensagens = new ListaMensagens();
		try {
		    //setando a aba atual
		    getCurrentSession().setAttribute("aba", "membro-servidor");	    
		    getCurrentSession().setAttribute("categoriaAtual",CategoriaMembro.SERVIDOR);
		    
		    membroEquipe.setServidor(getGenericDAO().findByPrimaryKey(servidor.getId(), Servidor.class));
		    membroEquipe.setFuncaoMembro(funcaoServidor);
		    
		    boolean validado = MembroProjetoHelper.adicionarServidor(getGenericDAO(), membroEquipe, obj, mensagens);	    
		    if (validado) {
			salvarMembroEquipe(membroEquipe);
			popularDadosMembroProjeto();
		    }else {
			addMensagens(mensagens);
		    }	    
		} catch (NegocioException e) {	    
		    addMensagens(e.getListaMensagens());	    
		} catch (ArqException e) {
		    tratamentoErroPadrao(e);
		}
	
		return null;
    }


    /**
     * Adiciona um discente à equipe do projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul><li> sigaa.war/projetos/ProjetoBase/equipe.jsp</li></ul>
     * 
     * @return Adiciona o discente retorna para a mesma tela.
     */
    private String adicionarDiscente() {
		ListaMensagens mensagens = new ListaMensagens();
		try {
		    //setando a aba atual
		    getCurrentSession().setAttribute("aba", "membro-discente");	    
		    getCurrentSession().setAttribute("categoriaAtual",CategoriaMembro.DISCENTE);
		    
		    membroEquipe.setDiscente(getGenericDAO().findByPrimaryKey(discente.getId(), Discente.class));
		    membroEquipe.setFuncaoMembro(funcaoDiscente);
		    boolean validado = MembroProjetoHelper.adicionarDiscente(getGenericDAO(), membroEquipe, obj, mensagens);
		    
		    if (validado) {
			salvarMembroEquipe(membroEquipe);
			popularDadosMembroProjeto();
		    }else {
			addMensagens(mensagens);
		    }
		    
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());	    
		} catch (ArqException e) {
		    tratamentoErroPadrao(e);
		}
	
		return null;
    }

    /**
     * Adiciona um técnico-administrativo a lista de membros da equipe do projeto.
     * 
     * @return
     */
    private String adicionarParticipanteExterno() {
		ListaMensagens mensagens = new ListaMensagens();
		try {
		    //setando a aba atual
		    getCurrentSession().setAttribute("aba", "membro-externo");	    
		    getCurrentSession().setAttribute("categoriaAtual",CategoriaMembro.EXTERNO);
		    
		    if ((participanteExterno == null) || (participanteExterno.getPessoa() == null) 
		    		|| (participanteExterno.getPessoa().getNome() == null)
					|| ("".equalsIgnoreCase(participanteExterno.getPessoa().getNome().trim()))) {
				participanteExterno = new ParticipanteExterno();
				cpf = "";
				addMensagemErro("Selecione um Parcipante Externo.");
				return null;
			}
		    
		    participanteExterno.setPessoa(adicionarPessoaExterna(participanteExterno.getPessoa()));
		    membroEquipe.setParticipanteExterno(participanteExterno);
		    membroEquipe.setFuncaoMembro(funcaoExterno);
		    boolean validado = MembroProjetoHelper.adicionarParticipanteExterno(getGenericDAO(), membroEquipe, obj, mensagens);
		    
		    if (validado) {
			salvarMembroEquipe(membroEquipe);
			popularDadosMembroProjeto();
		    }else {
			addMensagens(mensagens);
		    }	    
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());	    
		} catch (ArqException e) {
		    tratamentoErroPadrao(e);
		}
	
		return null;
    }

    
    /**
     * Prepara os dados para o cadastro de um novo {@link MembroProjeto}.
     * 
     */
    private void popularDadosMembroProjeto() {
		cpf = "";
		docente = new Servidor();
		discente = new Discente();
		servidor = new Servidor();
		participanteExterno = new ParticipanteExterno();
	
		funcaoDocente = new FuncaoMembro();
		funcaoServidor = new FuncaoMembro();
		funcaoDiscente = new FuncaoMembro();
		funcaoExterno = new FuncaoMembro();
	
		membroEquipe = new MembroProjeto();
		membroEquipe.setProjeto(obj);
		membroEquipe.setFuncaoMembro(new FuncaoMembro());
    }


    /**
     * Adiciona um servidor à lista de membros do projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/equipe.jsp</li></ul>
     * 
     * @return retorna para mesma tela permitindo adição de novo membro a equipe.
     */
    public String adicionarMembroEquipe() {
		if (!isFluxoAtivo()) {
		    return cancelar();
		}
	
		// Qual o tipo de membro será adicionado (default = docente)	    
		int categoriaMembro = getParameterInt("categoriaMembro",  CategoriaMembro.DOCENTE);
		// Informa qual tipo de membro de equipe será cadastrado
		getCurrentSession().setAttribute("categoriaAtual", categoriaMembro);
	
		if (CategoriaMembro.DISCENTE == categoriaMembro) {			
		    return adicionarDiscente();
		}
		if (CategoriaMembro.DOCENTE == categoriaMembro) {
		    return adicionarDocente();
		}
		if (CategoriaMembro.SERVIDOR == categoriaMembro) {
		    return adicionarServidor();
		}
		if (CategoriaMembro.EXTERNO == categoriaMembro) {
		    return adicionarParticipanteExterno();
		}	    
	
		return null;
    }


    /**
     * Faz a busca de um participante externo de acordo com o CPF.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/equipe.jsp</li></ul> 
     * 
     */
    public void buscarParticipanteExternoByCPF() {

		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		try {
	
		    // Limpa os dados
		    docente = new Servidor();
		    servidor = new Servidor();
		    membroEquipe = new MembroProjeto();
		    membroEquipe.setProjeto(this.obj);
		    membroEquipe.setFuncaoMembro(new FuncaoMembro());
		    membroEquipe.setParticipanteExterno(new ParticipanteExterno());
		    membroEquipe.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
	
		    if (participanteExterno == null) {
			participanteExterno = new ParticipanteExterno();
		    }
	
		    // Pessoa internacional
		    if (participanteExterno.getPessoa().isInternacional()) {
			participanteExterno.setPessoa(new Pessoa());
			membroEquipe.setParticipanteExterno(participanteExterno);
			// Permite a edição do nome da pessoa pelo usuário
			membroEquipe.setSelecionado(true);
			cpf = "";
		    } else {
			// Permite a edição do nome da pessoa pelo usuário na view
			membroEquipe.setSelecionado(false);
		    }
	
		    if ((cpf != null) && (!cpf.trim().equals(""))) {
	
			if (!ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(cpf)) {
			    addMensagemErroAjax("CPF inválido.");		    
			} else {
			    //busca a pessoa informada pelo cpf
			    String cpfFormatado = Formatador.getInstance().parseStringCPFCNPJ(cpf);
			    Pessoa p = pessoaDao.findByCpf(Long.parseLong(cpfFormatado));
			    participanteExterno.setPessoa(p);
	
			    if (participanteExterno.getPessoa() == null) {
				p = new Pessoa();
				p.setCpf_cnpjString(cpf);
				participanteExterno.setPessoa(p);
				membroEquipe.setParticipanteExterno(participanteExterno);
				// Permite a edição do nome da pessoa pelo usuário
				membroEquipe.setSelecionado(true);
	
			    } else {			
				// Não permite inclusão do nome da pessoa
				membroEquipe.setSelecionado(false);			
			    }
			}
		    }
		} catch (Exception e) {
		    notifyError(e);
		}
		
		getCurrentSession().setAttribute("aba", "membro-externo");
		getCurrentSession().setAttribute("categoriaAtual", CategoriaMembro.EXTERNO);

    }

    /**
     * Adiciona uma pessoa externa a equipe do projeto.
     * 
     * @param pessoa {@link Pessoa} que será persistida
     * @return pessoa {@link Pessoa cadastrada} se cadastrou com sucesso e retorna Null se falhar.
     */
    private Pessoa adicionarPessoaExterna(Pessoa pessoa) {

		PessoaDao pessoaDao = getDAO(PessoaDao.class);
		try {
		    // Busca pessoa por cpf, se não achar, entra para cadastrar...
		    // estrangeiros não tem cpf, não tem como verificar se já está no banco.
		    // se for estrangeiro, cadastra um novo
		    // TODO: melhorar lógica para os casos de estrangeiros 
		    if ((pessoa.isInternacional()) || ((pessoaDao.findByCpf(pessoa.getCpf_cnpj()) == null))) {
			pessoa.setTipo('F');
			ProjetoPesquisaValidator.limparDadosPessoa(pessoa);
			PessoaMov pessoaMov = new PessoaMov();
			pessoaMov.setPessoa(pessoa);
			pessoaMov.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
			pessoaMov.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);
			prepareMovimento(SigaaListaComando.CADASTRAR_PESSOA);
			
			pessoa = (Pessoa) execute(pessoaMov, getCurrentRequest());
	
		    } else { // a pessoa já está no banco...
			Integer id = pessoaDao.findIdByCpf(pessoa.getCpf_cnpj());
			pessoa = pessoaDao.findByPrimaryKey(id, Pessoa.class);
		    }
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());
		    return null;
		} catch (ArqException e) {
		    tratamentoErroPadrao(e);		
		}
	
		return pessoa;

    }

    /**
     * Remove o docente/discente/participante externo da lista de membros do projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/equipe.jsp</li></ul> 
     * 
     * @return Retorna para mesma página permitindo remover outro membro da equipe.
     */
    public String removeMembroEquipe() {

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
			membro.getProjeto().getId();			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(membro);
			mov.setUsuarioLogado(getUsuarioLogado());
	
			Comando ultimoComando = getUltimoComando();
			prepareMovimento(SigaaListaComando.REMOVER_MEMBRO_PROJETO_BASE);
			mov.setCodMovimento(SigaaListaComando.REMOVER_MEMBRO_PROJETO_BASE);
			execute(mov, getCurrentRequest());
			addMensagemInformation("Remoção realizada com sucesso.");
			prepareMovimento(ultimoComando);
	
			//Removendo da view.
		    for (Iterator<MembroProjeto> it = obj.getEquipe().iterator(); it.hasNext();) {
		    	MembroProjeto mp = it.next(); 
		    	if ( membro.getId() == mp.getId() && membro.getPessoa().getId() == mp.getPessoa().getId() ) {
		    		it.remove();
		    	}
		    }
			
			if (membro.isCoordenador()) {
			    obj.setCoordenador(null);
			}
			popularDadosMembroProjeto();
		
	    } catch (Exception e) {
			addMensagemErro("Erro ao Remover Membro do Projeto.");
			notifyError(e);
	    }
	} else { // transient pessoa ainda não está no banco
	    int idPessoa = getParameterInt("idPessoa", 0);
	    membro.setPessoa(new Pessoa(idPessoa));
	}

	return null;
    }

    /**
     * Submete os dados da tela de cadastro da equipe do projeto. 
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/equipe.jsp</li></ul> 
     * 
     * @return Próxima tela do fluxo do cadastro.
     * @throws DAOException
     */
    public String submeterEquipe() throws DAOException {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

	ListaMensagens mensagens = new ListaMensagens();
	MembroProjetoValidator.validaCoordenacaoAtiva(obj.getEquipe(), mensagens);
	if (!mensagens.isEmpty()) {
	    addMensagens(mensagens);
	    return null;
	}

	return proximoPasso();
    }


    /**
     * Mantém o estado do botão que seleciona a despesa 
     * (botão permanece selecionado) entre uma inclusão e 
     * outra de uma despesa.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/orcamento.jsp</li></ul> 
     * 
     * @return Retorna para mesma página permitindo a inclusão de novo item de orçamento.
     */
    public String getIniciarOrcamento() {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

	Integer idElementoDespesa = 0;
	idElementoDespesa = getParameterInt("idElementoDespesa");

	if ((idElementoDespesa != null) && (idElementoDespesa > 0)) {
	    return null;
	}

	if ((orcamento != null) && (orcamento.getElementoDespesa() != null)) {
	    getCurrentSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
	} else {
	    getCurrentSession().setAttribute("idElementoDespesa", 0);
	}

	recalculaTabelaOrcamentaria(obj.getOrcamento());
	return null;
    }

    /**
     * Adiciona um orçamento à lista.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/orcamento.jsp</li></ul> 
     * 
     * @return Retorna para mesma tela permitindo inclusão de novo elemento.
     */
    public String adicionarOrcamento() {
		if (!isFluxoAtivo()) {
		    return cancelar();
		}
	
		try {
		    Integer idElementoDespesa = getParameterInt("idElementoDespesa", 0);
		    orcamento.setElementoDespesa(getGenericDAO().findByPrimaryKey(idElementoDespesa, ElementoDespesa.class));
		    orcamento.setProjeto(obj);
	
		    //mantém o botão pressionado
		    getCurrentSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
		    
		    if (orcamento.getValorUnitario() == null){
				addMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_ZERO, "Valor Unitário");
				return null;
			}
		    
		    ListaMensagens mensagens = new ListaMensagens();	    
		    ProjetoBaseValidator.validaAdicionaOrcamento(orcamento, mensagens);
		    if (!mensagens.isEmpty()) {
		    	addMensagens(mensagens);
		    	return null;
		    }
		    
		    
		    if (obj.getOrcamento().contains(orcamento)) {
			addMensagemErro("Despesa já adicionada ao projeto.");
			return null;
		    } else {
			obj.getOrcamento().add(orcamento);
		    }
	
		    gravarTemporariamente();
		    
		    // Prepara para novo item do orçamento
		    orcamento = new OrcamentoDetalhado();
		    recalculaTabelaOrcamentaria(obj.getOrcamento());
		    
		} catch (NegocioException e) {
		    addMensagens(e.getListaMensagens());	    
		} catch (ArqException e) {
		    tratamentoErroPadrao(e);
		}
	
		return redirectMesmaPagina();
    }

    /**
     * Remove um orçamento da lista de orçamentos.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/orcamento.jsp</li></ul> 
     * 
     * @return Retorna para mesma página permitindo remoção de novo elemento
     * 
     */
    public String removeOrcamento() {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

	try {
	    int id = getParameterInt("idOrcamentoDetalhado", 0);
	    orcamento = getGenericDAO().findByPrimaryKey(id, OrcamentoDetalhado.class);

	    //Orçamento não localizado no banco
	    if (orcamento == null) {
	    	orcamento = new OrcamentoDetalhado();
	    } else {
			remover(orcamento);			
			
			// Verificar se ainda existem entradas do mesmo tipo para remover do
			// resumo...
			OrcamentoDao dao = getDAO(OrcamentoDao.class);
			Collection<OrcamentoDetalhado> o = dao.findByProjetoElementoDespesa(orcamento.getProjeto().getId(), orcamento.getElementoDespesa().getId());

			if ((o == null) || (o.size() == 0)) {
				for (Iterator<OrcamentoConsolidado> it = obj.getOrcamentoConsolidado().iterator(); it.hasNext();) {
					OrcamentoConsolidado oc = it.next();
					if (oc.getElementoDespesa().equals(orcamento.getElementoDespesa())) {
						remover(oc); // Remove do banco
						it.remove(); // Remove da collection
					}
				}
			}
			
			//remove da view
			obj.getOrcamento().remove(orcamento);
			orcamento = new OrcamentoDetalhado();        
			recalculaTabelaOrcamentaria(obj.getOrcamento());
			
	    }
	    
	    return redirectMesmaPagina();
	} catch (Exception e) {
	    tratamentoErroPadrao(e);
	    return null;
	}
    }

    /**
     * Atualiza rubricas do orçamento consolidado a partir do orçamento detalhado informado. 
     * @throws NegocioException 
     * @throws ArqException 
     */
    private void atualizarOrcamentoConsolidado() throws ArqException, NegocioException {
	
	    if (obj.getId() != 0) {
		Collection<OrcamentoConsolidado> orcamentosNoBanco = getGenericDAO().findByExactField(OrcamentoConsolidado.class, "projeto.id", obj.getId()); 
		obj.setOrcamentoConsolidado(orcamentosNoBanco);
	    }

	    // Percorre a tabela orçamentária e cadastra as rúbricas que ainda não foram consolidadas
	    for (Entry<ElementoDespesa, ResumoOrcamentoDetalhado> entrada : tabelaOrcamentaria.entrySet()) {
		boolean rubricaConsolidada = false;

		for (OrcamentoConsolidado consolidacao : obj.getOrcamentoConsolidado()) {
		    if (consolidacao.getElementoDespesa().equals(entrada.getKey())) {
			rubricaConsolidada = true;
			//@negocio: Atualizando o total da consolidação devido a inclusão de novos detalhes.
			//@negocio: Inicialmente todo orçamento deve ser solicitado ao Fundo(edital selecionado) 
			//          uma vez que não existe uma tela para consolidação dos valores pelo usuário, como em extensão.
			consolidacao.setFundo(entrada.getValue().getValorTotalRubrica());
			break;
		    }
		}
		//Nova rubrica para consolidar.
		if (!rubricaConsolidada) {
		    OrcamentoConsolidado consolidacao = new OrcamentoConsolidado(entrada.getKey(), obj);
		    consolidacao.setFundo(entrada.getValue().getValorTotalRubrica());
		    

		    // Adiciona uma nova consolidação de orçamento
		    obj.getOrcamentoConsolidado().add(consolidacao);
		}
	    }
	    

	    // Percorre orcamentos consolidados verificando se existe algum sem rubricas detalhadas
	    for (Iterator<OrcamentoConsolidado> it = obj.getOrcamentoConsolidado().iterator(); it.hasNext();) {
		OrcamentoConsolidado consolidacao = it.next();
		boolean consolidacaoSemDetalhe = true;
		for (OrcamentoDetalhado orcamentoDetalhe : obj.getOrcamento()) {
		    if (orcamentoDetalhe.getElementoDespesa().equals(consolidacao.getElementoDespesa())) {
			consolidacaoSemDetalhe = false;
			break;
		    }			
		}
		if (consolidacaoSemDetalhe) {
		    remover(consolidacao);
		    it.remove();
		}
	    }
    }
    

    /**
     * Submete os dados da tela de orçamento.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul><li> sigaa.war/projetos/ProjetoBase/orcamento.jsp</li></ul> 
     * 
     * @return Próxima tela do fluxo.
     */
    public String submeterOrcamento() {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

	try {
	    ListaMensagens mensagens = new ListaMensagens();
	    ProjetoBaseValidator.validaLimiteOrcamentoEdital(obj, mensagens);

	    if (!mensagens.isEmpty()) {
		addMensagens(mensagens);
		return null;
	    }

	    atualizarOrcamentoConsolidado();
	} catch (Exception e) {
	    tratamentoErroPadrao(e);
	}
	return proximoPasso();
    }

    /**
     * Submete dados da tela de orçamento consolidado e segue para próxima tela do fluxo.
     * <br>
     * Utilizado por: 
     * <ul><li> sigaa.war/projetos/ProjetoBase/orcamento.jsp</li></ul> 
     * 
     * @return Próxima tela do fluxo.
     */
    public String submeterOrcamentoConsolidado() {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

	ListaMensagens mensagens = new ListaMensagens();
	ProjetoBaseValidator.validaLimiteOrcamentoEdital(obj, mensagens);

	if (!mensagens.isEmpty()) {
	    addMensagens(mensagens);
	    return null;
	}

	return proximoPasso();
    }

    /**
     * Persiste dados da tela de arquivos submetidos e segue para próxima tela do fluxo.
     * <br> 
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/arquivos.jsp</li></ul> 
     * 
     * @return Próxima tela do fluxo.
     */
    public String submeterArquivos() {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

	ListaMensagens mensagens = new ListaMensagens();
	ProjetoBaseValidator.validaSubmissaoArquivos(obj, mensagens);

	if (!mensagens.isEmpty()) {
	    addMensagens(mensagens);
	    return null;
	}

	return proximoPasso();
    }

    /**
     * Persiste dados da tela de fotos e segue para próxima tela do fluxo.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/fotos.jsp</li></ul> 
     * 
     * @return Próxima tela do fluxo.
     */
    public String submeterFotos() {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

    	return proximoPasso();
    }

    /**
     * Verifica se os projetos anexos ao projeto base foram concluídos.
     * @throws DAOException 
     */
    @SuppressWarnings("unused")
    private void verificarPendenciasProjetoBase() throws DAOException {
	ProjetoDao dao = getDAO(ProjetoDao.class);
	ProjetoHelper.atualizarPendenciasProjetoBase(projeto, dao);
    }

    /**
     * Grava dados do projeto e submete 
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul><li> sigaa.war/projetos/ProjetoBase/resumo_integrado.jsp</li></ul> 
     * @throws ArqException 
     * 
     */
    public String submeterProjeto() throws ArqException {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

	ListaMensagens mensagens = new ListaMensagens();
	try {
	    //verificarPendenciasProjetoBase();
	    ProjetoBaseValidator.validaPendenciasProjetoBase(obj, mensagens);

	    if (!mensagens.isEmpty()) {
		addMensagens(mensagens);
		return null;
	    }

	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setObjMovimentado(obj);	
	    mov.setObjAuxiliar(controleFluxo);
	    mov.setCodMovimento(SigaaListaComando.ENVIAR_PROJETO_BASE);
	    obj = execute(mov, getCurrentRequest());

	    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	    if (obj.isConvenio() && Sistema.isSipacAtivo()) {
	    	clear();
	    	return forward(ConstantesNavegacaoProjetos.CONTINUAR_PROPLAM);
	    }
	    
	    clear();
    	return listarMeusProjetos();
	    
	} catch (NegocioException e) {
	    addMensagens(mensagens);
	} catch (DAOException e) {
	    tratamentoErroPadrao(e);
	}
	return null;

    }

    
    /**
     * Permite que o coordenador verifique pendências do projeto.
     * (conclusão de cadastro de projetos associados).
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <br>
     * <ul>
     *  <li>sigaa.war\projetos\ProjetoBase\meus_projetos.jsp</li>
     *  </ul>
     * @return
     * @throws DAOException 
     */
    public String verificarPendencias() throws DAOException {
	try {
	    clear();
	    Integer id = getParameterInt("id");
	    if(id == null) id = (Integer) getCurrentRequest().getAttribute("id");
	    this.obj = (Projeto) loadObj(id);
	    prepareMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
	    prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_BASE);

	    // evitar erro de lazy
	    obj.getOrcamento().iterator();
	    recalculaTabelaOrcamentaria(obj.getOrcamento());
	    obj.getCronograma().iterator();
	    obj.getEquipe().iterator();
	    obj.getArquivos().iterator();
	    obj.getFotos().iterator();
	    
	    iniciarCombosDadosGerais();

	    // Inicializar controle de fluxo do cadastro
	    controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_BASE);
	    int ultimoPasso = controleFluxo.getFluxo().length;
	    controleFluxo.setPassoAtual(ultimoPasso - 1);

	} catch (Exception e) {
	    notifyError(e);
	}
	projeto = obj;
	//verificarPendenciasProjetoBase();
	return forward(ConstantesNavegacaoProjetos.RESUMO);
    }

    
    /**
     * Inicializa campos do formulário de dos gerais
     */
    private void iniciarCombosDadosGerais() {	
    	//Evitar erro de Target Unreachable
    	if (obj.getAreaConhecimentoCnpq() == null) {
    		obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
    	}
    	if (obj.getAbrangencia() == null) {
    		obj.setAbrangencia(new TipoRegiao());
    	}
    	if (obj.getClassificacaoFinanciadora() == null) {
    		obj.setClassificacaoFinanciadora(new ClassificacaoFinanciadora());
    	}
    	if(obj.getEdital() == null){
    		obj.setEdital(new Edital());
    	}
    }

    
    
    
    /**
     * Cancela a operação de cadastro limpando os objetos em sessão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/dados_gerais.jsp	</li>
     * 	<li> sigaa.war/projetos/ProjetoBase/cronograma.jsp	</li> 
     * 	<li> sigaa.war/projetos/ProjetoBase/equipe.jsp		</li>
     * 	<li> sigaa.war/projetos/ProjetoBase/orcamento.jsp		</li>
     * 	<li> sigaa.war/projetos/ProjetoBase/arquivos.jsp		</li>   
     *  <li> sigaa.war/projetos/ProjetoBase/fotos.jsp		</li>
     * </ul>
     *  
     * @return Retorna para tela principal do sistema.
     */
    @Override
    public String cancelar() {
		clear();
		//Caso o projeto esteja sendo alterado por um membro do comitê
		getCurrentRequest().getSession(true).removeAttribute("CIEPE_ALTERANDO_CADASTRO");
		return super.cancelar();
    }

    /**
     * Método chamado para iniciar a alteração de um projeto. 
     * Primeira tela do cadastro (dados gerais).
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/projetos_pendentes.jsp</li>
     * 	<li> sigaa.war/projetos/ProjetoBase/meus_projetos.jsp</li>
     * </ul>
     * 
     * @return Retorna a página principal do cadastro.
     */
    public String preAtualizar() {
	try {
	    clear();
	    int id = getParameterInt("id");
	    this.obj = (Projeto) loadObj(id);
	    prepareMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
	    prepareMovimento(SigaaListaComando.ENVIAR_PROJETO_BASE);

	    // Inicializar controle de fluxo do cadastro
	    controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_BASE);
	    
	    // evitar erro de lazy
	    getGenericDAO().refresh(obj.getAreaConhecimentoCnpq());
	    obj.getOrcamento().iterator();
	    obj.getCronograma().iterator();
	    obj.getEquipe().iterator();
	    getIniciarOrcamento();
	    iniciarCombosDadosGerais();

	} catch (Exception e) {
	    return tratamentoErroPadrao(e);
	}

	return forward(ConstantesNavegacaoProjetos.DIMENSAO_ACADEMICA);
    }
    
    /**
     * Projeto entra em execução após aprovação do orientador.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/projetos/ProjetoBase/executarProjeto.jsp</li>
     * </ul>
     * @return
     * @throws ArqException
     */
    public String executarProjeto() {
	try {	    
	    MovimentoCadastro mov = new MovimentoCadastro();	
	    mov.setCodMovimento(SigaaListaComando.EXECUTAR_PROJETO_BASE);
	    mov.setObjMovimentado(obj);
	    execute(mov);
	    addMensagem(OPERACAO_SUCESSO);	    
	    return listarMeusProjetos();
	} catch (NegocioException e) {
	    addMensagens(e.getListaMensagens());
	} catch (ArqException e) {
	    tratamentoErroPadrao(e);
	}
	return null;
    }
    
    /**
     * Negar execução do projeto pelo orientador.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/projetos/ProjetoBase/executarProjeto.jsp</li>
     * </ul>
     * 
     * @return
     * @throws ArqException
     */
    public String naoExecutarProjeto() {
	try {
	    MovimentoCadastro mov = new MovimentoCadastro();	
	    mov.setCodMovimento(SigaaListaComando.NAO_EXECUTAR_PROJETO_BASE);
	    mov.setObjMovimentado(obj);
	    execute(mov);
	    addMensagem(OPERACAO_SUCESSO);
	    return listarMeusProjetos();
	} catch (NegocioException e) {
	    addMensagens(e.getListaMensagens());
	} catch (ArqException e) {
	    tratamentoErroPadrao(e);
	}
	return null;
    }


    /**
     * Visualiza dados detalhados do projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/projetos_pendentes.jsp</li>
     * 	<li> sigaa.war/projetos/ProjetoBase/meus_projetos.jsp</li>
     * </ul>
     * 
     * @return Página com dados do projeto.
     * @throws DAOException 
     * @throws HibernateException 
     */
    public String view() throws HibernateException, DAOException {
    	Integer id = getParameterInt("id", 0);
    	if ( id > 0 ) {
    		carregarDadosView(id);
    		return forward(ConstantesNavegacaoProjetos.VIEW);
		} else {
			return null;
		}
    }
    
    /**
     * Visualiza dados detalhados do projeto no formato de impressão.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> /SIGAA/app/sigaa.ear/sigaa.war/projetos/ProjetoBase/lista.jsp</li>
     * </ul>
     */
    public String viewImpressao() throws HibernateException, DAOException {
    	Integer id = getParameterInt("id", 0);
    	if ( id > 0 ) {
    		carregarDadosView(id);
    		return forward(ConstantesNavegacaoProjetos.VIEW_IMPRESSAO);
		} else {
			return null;
		}
    }
    
    /**
     * Esse método serve para carregar as informações que vão ser exibidas na view.
     * 
     * @param id
     * @throws HibernateException
     * @throws DAOException
     */
    private void carregarDadosView(int id) throws HibernateException, DAOException {
		ProjetoDao dao = getDAO(ProjetoDao.class);
		try {
			if (id > 0) {
		    	projeto = dao.findByPrimaryKey(id, Projeto.class);
		    	ProjetoHelper.atualizarPendenciasProjetoBase(projeto, dao);
		    	iniciarDetalhesParaView(projeto);
			} else {
				addMensagemErro("Ação não selecionada");
			}
		} finally {
			dao.close();
		}
    }

    /**
     * Inicializa todos os campos usados na visualização do projeto.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamado por JSP(s)</li>
     * </ul>
     * 
     * @throws HibernateException
     * @throws DAOException
     */
    public void iniciarDetalhesParaView(Projeto projeto) throws HibernateException, DAOException {	
		//Orcamento
		recalculaTabelaOrcamentaria(projeto.getOrcamento());		
	
		//Tela do cronograma
		TelaCronograma cronograma = new TelaCronograma(projeto.getDataInicio(), projeto.getDataFim(), projeto.getCronograma());
		setTelaCronograma(cronograma);
	
		//Histórico de situações do projeto
		HistoricoSituacaoProjetoDao dao = getDAO(HistoricoSituacaoProjetoDao.class);
		projeto.setHistoricoSituacao(dao.relatorioHistoricoByIdProjeto(projeto.getId()));
		
		//iniciando membros da equipe do projeto;
		projeto.getEquipe().iterator();
		projeto.getDiscentes().iterator();
    }
    
    /**
     * Visualiza dados detalhados do projeto para execução.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/projetos_pendentes.jsp</li>
     * 	<li> sigaa.war/projetos/ProjetoBase/meus_projetos.jsp</li>
     * </ul>
     * 
     * @return Página com dados do projeto.
     * @throws ArqException 
     */
    public String viewExecutar() {
    	Integer id = getParameterInt("id", 0);
    	if (id > 0) {
    		try {
    			ProjetoDao dao = getDAO(ProjetoDao.class);
    			projeto = dao.findByPrimaryKey(id, Projeto.class);
    			iniciarDetalhesParaView(projeto);
    			obj = projeto;

    			if(obj.getCoordenador().getPessoa().getId() != getUsuarioLogado().getPessoa().getId()){
    				addMensagemWarning("Operação restrita à coordenação atual do projeto.");
    				return null;
    			}

    			prepareMovimento(SigaaListaComando.EXECUTAR_PROJETO_BASE);
    			prepareMovimento(SigaaListaComando.NAO_EXECUTAR_PROJETO_BASE);                        
    			return forward(ConstantesNavegacaoProjetos.EXECUTAR_PROJETO);

    		} catch (ArqException e) {
    			notifyError(e);
    			addMensagemErro(e.getMessage());
    		}
    	} else {
    		addMensagemErro("Ação não selecionada");
    	}
    	return null;
    }

    
    /**
     * Visualiza dados do orçamento aprovado do projeto.
     * 
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/meus_projetos.jsp</li>
     * </ul>
     * 
     * @return Página com dados do projeto.
     */
    public String viewOrcamento() {

	Integer id = getParameterInt("id", 0);
	if (id > 0) {
	    try {
		ProjetoDao dao = getDAO(ProjetoDao.class);
		projeto = dao.findByPrimaryKey(id, Projeto.class);
		recalculaTabelaOrcamentaria(projeto.getOrcamento());		
		ProjetoHelper.atualizarPendenciasProjetoBase(projeto, dao);		
		return forward(ConstantesNavegacaoProjetos.VIEW_ORCAMENTO);		
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
     * Adiciona um arquivo ao projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul><li> sigaa.war/projetos/ProjetoBase/arquivos.jsp</li></ul>
     * 
     * @return Retorma para mesma página permitindo a inclusão de novo arquivo.
     */
    public String anexarArquivo() {

	try {

	    if ((descricaoArquivo == null) || ("".equals(descricaoArquivo.trim()))) {
	    	addMensagemErro("Descrição do arquivo é obrigatória!");
		return null;
	    }

	    if ((file == null) || (file.getBytes() == null)) {
	    	addMensagemErro("Informe o nome completo do arquivo.");
		return null;
	    }

	    int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
	    EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file.getContentType(), file.getName());
	    ArquivoProjeto arquivo = new ArquivoProjeto();
	    arquivo.setDescricao(descricaoArquivo);
	    arquivo.setIdArquivo(idArquivo);
	    arquivo.setAtivo(true);
	    obj.getArquivos().add(arquivo);
	    arquivo.setProjeto(obj);
	    gravarTemporariamente();
	    addMensagemInformation("Arquivo Anexado com Sucesso!");
	    descricaoArquivo = new String();

	} catch (Exception e) {
	    notifyError(e);
	    addMensagemErro(e.getMessage());
	}

	return null;
    }

    /**
     * Remove o arquivo da lista de anexos do projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/arquivos.jsp</li></ul>
     * 
     * @return Retorna para mesma página permitindo a exclusão de novo arquivo.
     */
    public String removeArquivo() {

	try {
	    ArquivoProjeto arquivo = new ArquivoProjeto();
	    arquivo.setIdArquivo(Integer.parseInt(getParameter("idArquivo")));
	    arquivo.setId(Integer.parseInt(getParameter("idArquivoProjeto")));

	    // Remove do referência ao arquivo do banco de projeto (sigaa)
	    remover(arquivo);

	    // Remove do banco de arquivos (cluster)
	    EnvioArquivoHelper.removeArquivo(arquivo.getIdArquivo());

	    // Remove da view
	    if (!ValidatorUtil.isEmpty(obj.getArquivos())) {
		obj.getArquivos().remove(arquivo);
	    }
	} catch (Exception e) {
	    tratamentoErroPadrao(e);
	    return null;
	}

	return null;
    }

    /**
     * Visualizar o arquivo anexo ao projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/arquivos.jsp</li></ul>
     * 
     */
    public void viewArquivo() {
	try {
	    int idArquivo = getParameterInt("idArquivo");
	    EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);
	} catch (Exception e) {
	    notifyError(e);
	    addMensagemErro("Arquivo não encontrado!");
	    return;
	}
	FacesContext.getCurrentInstance().responseComplete();
    }

    /**
     * Adiciona uma foto ao projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/fotos.jsp</li></ul>
     * 
     * @return Retorna para mesma página permitindo a inclusão de nova foto.
     */
    public String anexarFoto() {

	try {
	    if ((descricaoFoto == null) || ("".equals(descricaoFoto.trim()))) {
		addMensagemErro("Descrição da foto é obrigatória!");
		return null;
	    }
	    if ((foto == null) || (foto.getBytes() == null)) {
		addMensagemErro("Informe o nome completo do arquivo de foto.");
		return null;
	    }

	    int idFotoOriginal = EnvioArquivoHelper.getNextIdArquivo();
	    EnvioArquivoHelper.inserirArquivo(idFotoOriginal, foto.getBytes(), foto.getContentType(), foto.getName());

	    int idFotoMini = EnvioArquivoHelper.getNextIdArquivo();
	    byte[] fotoMini = UFRNUtils.redimensionaJPG(foto.getBytes(), WIDTH_FOTO,  HEIGHT_FOTO);

	    EnvioArquivoHelper.inserirArquivo(idFotoMini, fotoMini, foto.getContentType(), foto.getName());
	    FotoProjeto novaFoto = new FotoProjeto();
	    novaFoto.setDescricao(descricaoFoto);
	    novaFoto.setIdFotoOriginal(idFotoOriginal);
	    novaFoto.setIdFotoMini(idFotoMini);
	    novaFoto.setAtivo(true);
	    obj.addFoto(novaFoto);
	    gravarTemporariamente();
	    addMensagemInformation("Foto Anexada com Sucesso!");
	    descricaoFoto = new String();

	} catch (Exception e) {
	    notifyError(e);
	    addMensagemErro("Tipo de arquivo não compatível.");
	}

	return null;
    }

    /**
     * Remove a foto da lista de anexos do projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul><li> sigaa.war/projetos/ProjetoBase/fotos.jsp</li></ul>
     * 
     * @return Retorma para mesma página permitindo que nova foto seja removida.
     * @throws ArqException Gerada por {@link EnvioArquivoHelper#removeArquivo(int)}
     */
    public String removeFoto() throws ArqException {
	try {	    	    
	    FotoProjeto fotoRemovida = new FotoProjeto();
	    fotoRemovida.setIdFotoOriginal(Integer.parseInt(getParameter("idFotoOriginal")));
	    fotoRemovida.setIdFotoMini(Integer.parseInt(getParameter("idFotoMini")));
	    fotoRemovida.setId(Integer.parseInt(getParameter("idFotoProjeto")));
	    getGenericDAO().refresh(fotoRemovida);
	    // Remove referência ao arquivo de foto do banco de projeto (sigaa)
	    remover(fotoRemovida);
	    // Remove o arquivo do banco de arquivos (cluster)
	    EnvioArquivoHelper.removeArquivo(fotoRemovida.getIdFotoOriginal());
	    EnvioArquivoHelper.removeArquivo(fotoRemovida.getIdFotoMini());
	    // Remove da view
	    if (!ValidatorUtil.isEmpty(obj.getFotos())) {
		obj.getFotos().remove(fotoRemovida);
	    }

	} catch (Exception e) {
	    tratamentoErroPadrao(e);
	}
	return null;
    }


    /**
     * Facilita a exibição da tabela de orçamentos do projeto que está sendo cadastrada/alterada.
     * 
     * @param orcamentos {@link Collection} de {@link OrcamentoDetalhado} com itens do orçamento do projeto atual.
     */
    private void recalculaTabelaOrcamentaria(Collection<OrcamentoDetalhado> orcamentos) {
	tabelaOrcamentaria.clear();

	for (OrcamentoDetalhado orca : orcamentos) {
	    ResumoOrcamentoDetalhado resumo = tabelaOrcamentaria.get(orca.getElementoDespesa());
	    if (resumo == null) {
		resumo = new ResumoOrcamentoDetalhado();
	    }
	    resumo.getOrcamentos().add(orca);
	    tabelaOrcamentaria.put(orca.getElementoDespesa(), resumo);
	}
    }


    /**
     * Visualização da tabela de orçamento cadastrado durante o cadastro do projeto.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/projetos/AvaliacaoProjeto/view_orcamento.jsp</li>
     * 		<li>/sigaa.war/projetos/ProjetoBase/dados_projeto.jsp</li>
     * 		<li>/sigaa.war/projetos/ProjetoBase/executarProjeto.jsp</li>
     * 		<li>/sigaa.war/projetos/ProjetoBase/orcamento.jsp</li>
     * 		<li>/sigaa.war/projetos/ProjetoBase/view.jsp</li>
     * </ul>
     * 
     * @return Retorna a tabela orçamentária. Um {@link Map} de {@link ElementoDespesa} e {@link ResumoOrcamentoDetalhado}.
     */
    public Map<ElementoDespesa, ResumoOrcamentoDetalhado> getTabelaOrcamentaria() {
	return tabelaOrcamentaria;
    }

    /**
     * Set da Tabela orçamentária visualizada na tela de orçamento do cadastro da ação.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamado por JSP(s)</li>
     * </ul>
     * 
     * @param tabela orçamentária. Um {@link Map} de {@link ElementoDespesa} e {@link ResumoOrcamentoDetalhado}.
     */
    public void setTabelaOrcamentaria(Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabela) {
	this.tabelaOrcamentaria = tabela;
    }

    /**
     * Invoca o processador para salvar um projeto sem passar por todas as
     * validações. Este é um cadastro com vários passos e deve ser possível
     * interromper o processo para continuar depois.
     *  
     * É necessário o preenchimento mínimo da primeira tela 
     * (dados gerais do projeto) 
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamado por JSPs.</li>
     * </ul>
     * 
     * @return Retorna para mesma página permitindo seguir com o fluxo.
     * @throws ArqException 
     * @throws NegocioException 
     */
    public String gravarTemporariamente() throws ArqException, NegocioException {
    	checkChangeRole();

	    //salva a projeto
	    Comando ultimoComando = getUltimoComando();	    
	    MovimentoCadastro mov = new MovimentoCadastro();

	    //REGRA: Membro do CIEPE alterando um cadastro tem um processamento diferenciado na gravação temporária do projeto.
	    Boolean ciepeAlterando = ((Boolean) getCurrentRequest().getSession(true).getAttribute("CIEPE_ALTERANDO_CADASTRO") == null) ? false : true;
	    if (ciepeAlterando) {
		mov.setAcao(SigaaListaComando.ALTERAR_CADASTRO_PROJETO_BASE.getId());
	    }
	    
	    mov.setObjMovimentado(obj);
	    mov.setObjAuxiliar(controleFluxo);
	    mov.setCodMovimento(SigaaListaComando.GRAVAR_RASCUNHO_PROJETO_BASE);
	    prepareMovimento(SigaaListaComando.GRAVAR_RASCUNHO_PROJETO_BASE);
	    obj = (Projeto) execute(mov, getCurrentRequest());

	    // Repreparar a operação anterior
	    prepareMovimento(ultimoComando);

	return null;
    }

    /**
     * Grava o projeto temporariamente e sai do cadastro utilizado no botão de
     * gravar na tela de resumo o cadastro do projeto.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul><li> sigaa.war/projetos/ProjetoBase/resumo.jsp</li></ul>
     * 
     * @return Retorna para a página principal.
     * @throws ArqException 
     */
    public String gravarFinalizar() throws ArqException {
	if (!isFluxoAtivo()) {
	    return cancelar();
	}

	try {	    
	    MovimentoCadastro mov = new MovimentoCadastro();
	    mov.setObjMovimentado(obj);
	    mov.setObjAuxiliar(controleFluxo);
	    mov.setCodMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);
	    prepareMovimento(SigaaListaComando.SALVAR_PROJETO_BASE);	    
	    obj = (Projeto) execute(mov, getCurrentRequest());	    
	    //Caso o projeto esteja sendo alterado por um membro do comitê
	    getCurrentRequest().getSession(true).removeAttribute("CIEPE_ALTERANDO_CADASTRO");
	    cancelar();
	    addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
	    return forward(ConstantesNavegacaoProjetos.SELECIONAR_PROJETO);
	} catch (NegocioException e) {
	    addMensagens(e.getListaMensagens());
	    return null;
	}

    }

    /**
     * Remove o objeto informado do banco.
     * Utilizando na remoção de membros da equipe durante o cadastro do projeto.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamado por JSPs.</li>
     * </ul>
     * 
     * @param persistDB Objeto que será removido do banco.
     * @return Retorna para mesma página permitindo que um novo objeto seja removido.       
     * @throws ArqException 
     * @throws NegocioException 
     */
    public String remover(PersistDB persistDB) throws ArqException, NegocioException {
    	checkChangeRole();

    	MovimentoCadastro mov = new MovimentoCadastro();
    	mov.setObjMovimentado(persistDB);

    	if (persistDB.getId() == 0) {
    		addMensagemErro("Não há objeto informado para remoção");
    		return null;
    	}

    	Comando ultimoComando = getUltimoComando();
    	prepareMovimento(ArqListaComando.REMOVER);
    	mov.setCodMovimento(ArqListaComando.REMOVER);
    	execute(mov, getCurrentRequest());
    	addMensagemInformation("Remoção realizada com sucesso.");
    	prepareMovimento(ultimoComando);

    	return null;
    }

    /**
     * Desativa o projeto base.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/projetos/ProjetoBase/remover.jsp</li>
     * </ul>
     * 
     * @param persistDB
     * @return
     * @throws SegurancaException 
     */
    public String removerProjetoBase() throws SegurancaException {
    	checkChangeRole();
    	MovimentoCadastro mov = new MovimentoCadastro();
    	mov.setObjMovimentado(obj);

    	if (obj.getId() == 0) {
    		addMensagemErro("Não há objeto informado para remoção");
    		return null;
    	}

    	try {
    		Comando ultimoComando = getUltimoComando();
    		mov.setCodMovimento(SigaaListaComando.REMOVER_PROJETO_BASE);
    		execute(mov, getCurrentRequest());
    		addMensagemInformation("Remoção realizada com sucesso.");
    		prepareMovimento(ultimoComando);
    	} catch (NegocioException e) {
    		addMensagem(e.getMessage());
    	} catch (Exception e) {
    		notifyError(e);
    		addMensagemErroPadrao();
    	}

    	return listarMeusProjetos();
    }
    
    /**
     * Desativa o projeto base.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/AlteracaoProjeto/remover.jsp</li>
     * </ul>
     * @param persistDB
     * @return
     * @throws SegurancaException 
     */
    public String removerCiepe() throws SegurancaException {
    	checkChangeRole();
    	MovimentoCadastro mov = new MovimentoCadastro();
    	mov.setObjMovimentado(obj);

    	if (obj.getId() == 0) {
    		addMensagemErro("Não há objeto informado para remoção");
    		return null;
    	}

    	try {
    		Comando ultimoComando = getUltimoComando();
    		mov.setCodMovimento(SigaaListaComando.REMOVER_PROJETO_BASE);
    		execute(mov, getCurrentRequest());
    		addMensagemInformation("Remoção realizada com sucesso.");
    		prepareMovimento(ultimoComando);
    	} catch (NegocioException e) {
    		addMensagens(e.getListaMensagens());
    	} catch (Exception e) {
    		notifyError(e);
    		addMensagemErroPadrao();
    	}

    	return forward("/projetos/AlteracaoProjeto/lista.jsp");
    }

    
    
    
    /**
     * Seleciona um projeto e exibe página para confirmar a remoção.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/projetos_pendentes.jsp</li>
     * 	<li> sigaa.war/projetos/ProjetoBase/meus_projetos.jsp</li>
     * </ul>
     * 
     * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()     * 
     * @return página para confirmação da remoção.
     */
    @Override
    public String preRemover() {
	try {
	    prepareMovimento(SigaaListaComando.REMOVER_PROJETO_BASE);
	    setId();
	    obj = (Projeto) loadObj(obj.getId());	    
    	ListaMensagens lista = new ListaMensagens();
		ProjetoBaseValidator.validaRemoverProjeto(obj, getUsuarioLogado().getPessoa(), lista );			
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return null;
		}
	} catch (ArqException e) {
	    notifyError(e);
	    addMensagemErroPadrao();
	}	
	return forward(ConstantesNavegacaoProjetos.REMOVER);
    }
    
    /**
     * Seleciona um projeto e exibe página para confirmar a remoção.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/AlteracaoProjetos/lista.jsp</li>
     * </ul>
     * 
     * @return página para confirmação da remoção.
     */
    public String preRemoverCiepe() {
	try {
	    prepareMovimento(SigaaListaComando.REMOVER_PROJETO_BASE);
	    setId();
	    obj = (Projeto) loadObj(obj.getId());
	} catch (ArqException e) {
	    notifyError(e);
	    addMensagemErroPadrao();
	}	
	return forward("/projetos/AlteracaoProjeto/remover.jsp");
    }


    /**
     * Retorna todas as situações de projetos possíveis.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * 
     * @return Lista de todas os tipos de situação
     */
    public Collection<SelectItem> getTipoSituacaoProjetoCombo() {

	try {
	    return toSelectItems(getGenericDAO().findAll(TipoSituacaoProjeto.class, "descricao", "asc"), "id", "descricao");
	} catch (Exception e) {
	    notifyError(e);
	    addMensagemErro(e.getMessage());
	    return null;
	}

    }

    /**
     * Carrega objeto do banco.
     * 
     * @param id.
     * @return retorna o objeto com id informado.
     * @throws DAOException 
     */
    private Object loadObj(int id) throws DAOException {
    	MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
    	try {
		    Projeto pj = dao.findByPrimaryKey(id, Projeto.class);
		    pj.setCoordenador( dao.findCoordenadorAtualProjeto(pj.getId()) );
		    return pj;
    	} finally {
    		dao.close();
    	}
    }

    /**
     * Exibe a lista de projeto que foram gravados pelo usuário logado
     * permitindo que ele continue o cadastro de onde parou.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/portais/docente/menu_docente.jsp </li>
     * </ul>
     * 
     * @return Retorna página com lista de todos os projetos pendentes de envio.
     * @throws SegurancaException 
     */
    public String listarCadastrosEmAndamento() throws SegurancaException {
	checkChangeRole();
	atualizarCadastrosEmAndamento();
	return forward(ConstantesNavegacaoProjetos.PROJETOS_PENDENTES);
    }

    /**
     * Atualiza todos os projetos gravados pelo usuário atual.
     * Projetos gravados são projetos com cadastro em andamento que 
     * ainda não foram concluídas pelo coordenador da propostas.
     * 
     * Utilizado para listar todos os projetos do usuário
     * na página de meus projetos permitindo a continuação do 
     * cadastro.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Não é chamado por JSPs.</li>
     * </ul>
     *        
     */
    public void atualizarCadastrosEmAndamento() {
    	try {
    		ProjetoDao dao = getDAO(ProjetoDao.class);
    		projetosGravados = dao.findGravadosByUsuario(getUsuarioLogado(), TipoProjeto.ASSOCIADO);
    	} catch (DAOException e) {
    		notifyError(e);
    	}
    }


    /**
     * Exibe a lista de projetos onde o servidor faz parte da
     * lista de membros da equipe.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/portais/docente/menu_docente.jsp </li>
     * </ul>
     * 
     * @return Página com a lista de ações encontradas
     * @throws SegurancaException 
     */
    public String listarMeusProjetos() throws SegurancaException {

    if (getUsuarioLogado().getServidor() == null && !getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno()) {
	    throw new SegurancaException();
	}
    Pessoa pessoa = (getDocenteExternoUsuario() != null ? getDocenteExternoUsuario().getPessoa() : getUsuarioLogado().getServidor().getPessoa());
	ProjetoAssociadoDao dao = getDAO(ProjetoAssociadoDao.class);
	try {
	    meusProjetos = dao.findByPessoa(pessoa);
	} catch (DAOException e) {
	    notifyError(e);
	    return null;
	}

	atualizarCadastrosEmAndamento();
	return forward(ConstantesNavegacaoProjetos.MEUS_PROJETOS);
    }


    /**
     * Inicia o cadastro de projeto de ensino anexo a ação acadêmica integrada.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/resumo_integrado.jsp </li>
     * </ul>
     * 
     * @return
     * @throws ArqException 
     */
    public String iniciarProjetoMonitoriaAssociado() throws ArqException {
    	checkChangeRole();
    	try {	    
    		int id = getParameterInt("id", 0);
    		ProjetoMonitoriaMBean bean = getMBean("projetoMonitoria");
    		bean.clear();

    		//Verificando se o cadastro do projeto já foi iniciado anteriormente.
    		//Recuperando cadastro do projeto de ensino do banco.
    		ProjetoEnsino pm = getDAO(ProjetoMonitoriaDao.class).findByProjetoBaseAndTipo(id, TipoProjetoEnsino.PROJETO_DE_MONITORIA);

    		if ((pm != null) && (pm.isProjetoMonitoria()) && pm.isAtivo()) {
    			for (ComponenteCurricularMonitoria ccm : pm.getComponentesCurriculares()) {
    				ccm.getDocentesComponentes().iterator();			    
    			}
    			pm.getFotos().iterator();
    			pm.getEquipeDocentes().iterator();
    			pm.getProjeto().getEquipe().iterator();
    			bean.setObj(pm);
    		}else {	    
    			projeto = getGenericDAO().findByPrimaryKey(id, Projeto.class);
    			projeto.getEquipe().iterator();
    			bean.getObj().setProjeto(projeto);
    			bean.getObj().getTipoProjetoEnsino().setId(TipoProjetoEnsino.PROJETO_DE_MONITORIA);

    			//Verificando se há edital aberto
    			ListaMensagens mensagens = new ListaMensagens();
    			ProjetoMonitoriaValidator.validaEditaisAbertos(bean.getObj(), mensagens);

    			if (!mensagens.isEmpty()) {
    				addMensagens(mensagens);
    				return null;
    			}


    		}

    		prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
    		bean.setControleFluxo(new ControleFluxo(ControleFluxo.PROJETO_MONITORIA_ASSOCIADO));
    		return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_FORM);

    	} catch (DAOException e) {
    		notifyError(e);
    		return null;
    	}
    }

    
    /**
     * Inicia o cadastro de projeto de melhoria da qualidade do ensino anexo a ação acadêmica integrada.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 	<li> sigaa.war/projetos/ProjetoBase/resumo_integrado.jsp </li>
     * </ul>
     * 
     * @return
     * @throws ArqException 
     */
    public String iniciarProjetoMelhoriaQualidadeEnsinoAssociado() throws ArqException {
    	checkChangeRole();
		try {	    
			int id = getParameterInt("id", 0);
			ProjetoMonitoriaMBean bean = getMBean("projetoMonitoria");
			bean.clear();

			//Verificando se o cadastro do projeto já foi iniciado anteriormente.
			//Recuperando cadastro do projeto de ensino do banco.
			ProjetoEnsino pm = getDAO(ProjetoMonitoriaDao.class).findByProjetoBaseAndTipo(id, TipoProjetoEnsino.PROJETO_PAMQEG);
			if ((pm != null) && (pm.isProjetoPAMQEG()) && pm.isAtivo()) {
				bean.setObj(pm);
			}else {	    
				projeto = getGenericDAO().findByPrimaryKey(id, Projeto.class);        	
				bean.getObj().setProjeto(projeto);
				bean.getObj().getTipoProjetoEnsino().setId(TipoProjetoEnsino.PROJETO_PAMQEG);

				//Verificando se há edital aberto
				ListaMensagens mensagens = new ListaMensagens();
				ProjetoMonitoriaValidator.validaEditaisAbertos(bean.getObj(), mensagens);

				if (!mensagens.isEmpty()) {
					addMensagens(mensagens);
					return null;
				}
			}

			prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
			bean.setControleFluxo(new ControleFluxo(ControleFluxo.PROJETO_PAMQEG));
			return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_FORM);

		} catch (DAOException e) {
			notifyError(e);
			return null;
		}

    }

    /**
     * Inicia o cadastro de uma ação de extensão do tipo programa a partir da
     * tela de resumo no cadastro da ação acadêmica integrada.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war\projetos\ProjetoBase\resumo_integrado.jsp</li>
     * </ul>
     * @return
     * @throws ArqException
     */
    public String iniciarProgramaExtensaoAssociado() throws ArqException {
    	checkChangeRole();
    	try {	    
    		int id = getParameterInt("id", 0);
    		if (id == 0) {
    			addMensagemErro("Projeto não informado.");
    			return null;
    		}

    		//Verificando se o cadastro do projeto já foi iniciado anteriormente.
    		//Recuperando cadastro do projeto de ensino do banco.
    		AtividadeExtensao acao = getDAO(AtividadeExtensaoDao.class).findAcaoByProjetoAndTipoAcao(id, TipoAtividadeExtensao.PROGRAMA);	    
    		if (!ValidatorUtil.isEmpty(acao) && acao.isAtivo()) {
    			AtividadeExtensaoMBean bean = getMBean("atividadeExtensao");
    			bean.clear();
    			bean.setObj(acao);
    			getCurrentRequest().setAttribute("existeAcao", Boolean.TRUE);
    			return bean.preAtualizar();
    		}else {	 
    			ProgramaMBean bean = getMBean("programaExtensao");	    
    			projeto = getGenericDAO().findByPrimaryKey(id, Projeto.class);
    			bean.clear();
    			bean.getObj().setProjeto(projeto);
    			return bean.iniciar();
    		}
    	} catch (DAOException e) {
    		notifyError(e);
    		return null;
    	}
    }

    
    /**
     * Inicia o cadastro de um projeto de extensão a partir da
     * tela de resumo no cadastro da ação acadêmica integrada.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war\projetos\ProjetoBase\resumo_integrado.jsp</li>
     * </ul>
     * @return
     * @throws ArqException
     */
    public String iniciarProjetoExtensaoAssociado() throws ArqException {
    	checkChangeRole();
    	try {	    
    		int id = getParameterInt("id", 0);
    		if (id == 0) {
    			addMensagemErro("Projeto não informado.");
    			return null;
    		}

    		//Verificando se o cadastro do projeto já foi iniciado anteriormente.
    		//Recuperando cadastro do projeto de ensino do banco.
    		AtividadeExtensao acao = getDAO(AtividadeExtensaoDao.class).findAcaoByProjetoAndTipoAcao(id, TipoAtividadeExtensao.PROJETO);
    		if (!ValidatorUtil.isEmpty(acao) && acao.isAtivo()) {
    			AtividadeExtensaoMBean bean = getMBean("atividadeExtensao");
    			bean.clear();
    			bean.setObj(acao);
    			getCurrentRequest().setAttribute("existeAcao", Boolean.TRUE);
    			return bean.preAtualizar();
    		}else {	 
    			ProjetoExtensaoMBean bean = getMBean("projetoExtensao");	    
    			projeto = getGenericDAO().findByPrimaryKey(id, Projeto.class);
    			bean.clear();
    			bean.getObj().setProjeto(projeto);
    			return bean.iniciar();
    		}
    	} catch (DAOException e) {
    		notifyError(e);
    		return null;
    	}
    }


    /**
     * Inicia o cadastro de um curso de extensão a partir da
     * tela de resumo no cadastro da ação acadêmica integrada.
     * <br>
     * <ul>
     * <li>sigaa.war\projetos\ProjetoBase\resumo_integrado.jsp</li>
     * </ul>
     * @return
     * @throws ArqException
     */
    public String iniciarCursoExtensaoAssociado() throws ArqException {
    	checkChangeRole();
		try {	    
		    int id = getParameterInt("id", 0);
		    if (id == 0) {
			addMensagemErro("Projeto não informado.");
			return null;
		    }
		    
		    //Verificando se o cadastro do projeto já foi iniciado anteriormente.
		    //Recuperando cadastro do projeto de ensino do banco.
		    AtividadeExtensao acao = getDAO(AtividadeExtensaoDao.class).findAcaoByProjetoAndTipoAcao(id, TipoAtividadeExtensao.CURSO);	    
		    if (!ValidatorUtil.isEmpty(acao) && acao.isAtivo()) {
			AtividadeExtensaoMBean bean = getMBean("atividadeExtensao");
			bean.clear();
			bean.setObj(acao);
			getCurrentRequest().setAttribute("existeAcao", Boolean.TRUE);
			return bean.preAtualizar();
		    }else {	 
			CursoEventoMBean bean = getMBean("cursoEventoExtensao");	    
	        	projeto = getGenericDAO().findByPrimaryKey(id, Projeto.class);
	        	bean.clear();
	        	bean.getObj().setProjeto(projeto);
	        	return bean.iniciarCurso();
		    }
		} catch (DAOException e) {
		    notifyError(e);
		    return null;
		}
    }


    /**
     * Inicia o cadastro de um evento de extensão a partir da
     * tela de resumo no cadastro da ação acadêmica integrada.
     * <br>
     * <ul>
     * <li>sigaa.war\projetos\ProjetoBase\resumo_integrado.jsp</li>
     * </ul>
     * @return
     * @throws ArqException
     */
    public String iniciarEventoExtensaoAssociado() throws ArqException {
    	checkChangeRole();
		try {	    
		    int id = getParameterInt("id", 0);
		    if (id == 0) {
			addMensagemErro("Projeto não informado.");
			return null;
		    }
		    
		    //Verificando se o cadastro do projeto já foi iniciado anteriormente.
		    //Recuperando cadastro do projeto de ensino do banco.
		    AtividadeExtensao acao = getDAO(AtividadeExtensaoDao.class).findAcaoByProjetoAndTipoAcao(id, TipoAtividadeExtensao.EVENTO);
		    if (!ValidatorUtil.isEmpty(acao) && acao.isAtivo()) {
			AtividadeExtensaoMBean bean = getMBean("atividadeExtensao");
			bean.clear();
			bean.setObj(acao);
			getCurrentRequest().setAttribute("existeAcao", Boolean.TRUE);
			return bean.preAtualizar();
		    }else {	 
			CursoEventoMBean bean = getMBean("cursoEventoExtensao");	    
	        	projeto = getGenericDAO().findByPrimaryKey(id, Projeto.class);
	        	bean.clear();
	        	bean.getObj().setProjeto(projeto);
	        	return bean.iniciarEvento();
		    }
		} catch (DAOException e) {
		    notifyError(e);
		    return null;
		}
    }

    /**
     * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de docentes
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/ProjetoBase/equipe.jsp</li>
	 * </ul>
	 * 
     * @param event
     * @return
     * @throws DAOException
     */
    public List<Servidor> autoCompleteNomeDocente(Object event) throws DAOException{
    	String nome = event.toString();
    	return (List<Servidor>) getDAO(ServidorDao.class).findByDocente(nome, 0, true, false);
    }
    
    /**
     * Carrega o docente do autocomplete
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/ProjetoBase/equipe.jsp</li>
	 * </ul>
	 * 
     * @param e
     */
    public void carregaDocente(ActionEvent e){
    	docente = (Servidor) e.getComponent().getAttributes().get("docenteAutoComplete");
    }
    
    /**
     * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Servidores Técnicos
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/ProjetoBase/equipe.jsp</li>
	 * </ul>
	 * 
     * @param event
     * @return
     * @throws DAOException
     */
    public List<Servidor> autoCompleteNomeServidorTecnico(Object event) throws DAOException{
    	String nome = event.toString();
    	return (List<Servidor>) getDAO(ServidorDao.class).findByNome(nome, 0, true, false, Categoria.TECNICO_ADMINISTRATIVO, false);
    }
    
    /**
     * Carrega o Servidor Técnico do autocomplete
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/ProjetoBase/equipe.jsp</li>
	 * </ul>
	 * 
     * @param e
     */
    public void carregaServidorTecnico(ActionEvent e){
    	servidor = (Servidor) e.getComponent().getAttributes().get("servidorAutoComplete");
    }
    
    /**
     * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de Discentes Ativos de Pós-Graduação
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/ProjetoBase/equipe.jsp</li>
	 * </ul>
	 * 
     * @param event
     * @return
     * @throws DAOException
     */
    public List<Discente> autoCompleteNomeDiscente(Object event) throws DAOException{
    	String nome = event.toString();
    	return (List<Discente>) getDAO(DiscenteDao.class).findByNome(nome, 0, new char[] {'L','S','R','G'}, null, false, true, new PagingInformation());
    }
    
    /**
     * Carrega o Discente Ativo de Pós-Graduação do autocomplete
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa/projetos/ProjetoBase/equipe.jsp</li>
	 * </ul>
	 * 
     * @param e
     */
    public void carregaDiscente(ActionEvent e){
    	discente = (Discente) e.getComponent().getAttributes().get("discenteAutoComplete");
    }

    public String getDescricaoArquivo() {
	return descricaoArquivo;
    }

    public void setDescricaoArquivo(String descricaoArquivo) {
	this.descricaoArquivo = descricaoArquivo;
    }

    public Collection<TipoSituacaoProjeto> getSituacoesProjeto() {
	return situacoesProjeto;
    }

    public void setSituacoesProjeto(Collection<TipoSituacaoProjeto> situacoes) {
	this.situacoesProjeto = situacoes;
    }

    /**
     * Utilizado no fluxo do cadastro da ação na tela de inclusão de membros do projeto
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s): 
     * <ul><li> /sigaa.war/projetos/projetoBase/equipe.jsp </li></ul>
     * 
     * @return Retorna um membro da equipe do projeto.
     */
    public MembroProjeto getMembroEquipe() {
	return membroEquipe;
    }

    public void setMembroEquipe(MembroProjeto membroEquipeAtividadeExtensao) {
	this.membroEquipe = membroEquipeAtividadeExtensao;
    }

    /**
     * Orçamento detalhado utilizado no formulário de definição do orçamento
     * detalhado de um projeto
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/extensao/Atividade/orcamento.jsp</li>
     * 		<li>/sigaa.war/extensao/projetos/ProjetoBase/orcamento.jsp</li>
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

    public Servidor getDocente() {
	return docente;
    }

    public void setDocente(Servidor docente) {
	this.docente = docente;
    }

    /**
     * Usado no form de cadastro de membros da equipe.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/projetos/ProjetoBase/equipe.jsp</li>
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

    public ParticipanteExterno getParticipanteExterno() {
	return participanteExterno;
    }

    public void setParticipanteExterno(ParticipanteExterno participanteExterno) {
	this.participanteExterno = participanteExterno;
    }

    /**
     * CPF utilizado na tela de cadastro da equipe (participanteExterno).
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/projetos/ProjetoBase/equipe.jsp</li>
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


    public Discente getDiscente() {
	return discente;
    }

    public void setDiscente(Discente discente) {
	this.discente = discente;
    }

    public Projeto getProjeto() {
	return projeto;
    }

    public void setProjeto(Projeto projeto) {
	this.projeto = projeto;
    }

    public ControleFluxo getControleFluxo() {
	return controleFluxo;
    }

    public void setControleFluxo(ControleFluxo controleFluxo) {
	this.controleFluxo = controleFluxo;
    }

    public TelaCronograma getTelaCronograma() {
	return telaCronograma;
    }

    public void setTelaCronograma(TelaCronograma telaCronograma) {
	this.telaCronograma = telaCronograma;
    }

    public Collection<Projeto> getProjetosGravados() {
	return projetosGravados;
    }

    public void setProjetosGravados(Collection<Projeto> projetosGravados) {
	this.projetosGravados = projetosGravados;
    }

    public Collection<Projeto> getMeusProjetos() {
	return meusProjetos;
    }

    public void setMeusProjetos(Collection<Projeto> meusProjetos) {
	this.meusProjetos = meusProjetos;
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
    
    /**
     * Informa se o objetos deste MBean está sendo alterado por um membro
     * do comitê integrado de ensino, pesquisa e extensão (CIEPE).
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/resumo.jsp</li>
     * 		<li>/sigaa.war/projetos/ProjetoBase/resumo.jsp</li>
     * 		<li>/sigaa.war/projetos/ProjetoBase/resumo_integrado.jsp</li>
     * </ul>
     * 
     * @return
     */
    public boolean isMembroComiteAlterandoCadastro() {
	Boolean alterando = (Boolean) getCurrentRequest().getSession(true).getAttribute("CIEPE_ALTERANDO_CADASTRO");	
	return (alterando == null) ? false : true;
    }

}
