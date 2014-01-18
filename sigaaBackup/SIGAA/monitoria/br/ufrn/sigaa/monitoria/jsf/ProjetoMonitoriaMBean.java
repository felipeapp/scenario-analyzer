/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/10/2009
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
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
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.dao.monitoria.EquipeDocenteDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.EditalDao;
import br.ufrn.sigaa.arq.dao.projetos.HistoricoSituacaoProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.extensao.dominio.ResumoOrcamentoDetalhado;
import br.ufrn.sigaa.mensagens.MensagensMonitoria;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ComponenteCurricularMonitoria;
import br.ufrn.sigaa.monitoria.dominio.DiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocente;
import br.ufrn.sigaa.monitoria.dominio.EquipeDocenteComponente;
import br.ufrn.sigaa.monitoria.dominio.Orientacao;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaMov;
import br.ufrn.sigaa.monitoria.negocio.ProjetoMonitoriaValidator;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.ArquivoProjeto;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.FotoProjeto;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.jsf.ControleFluxo;

/** 
 * Controller responsável pelas operações de projeto de monitoria.
 * 
 * @author Édipo Elder F. Melo
 * @author Ilueny Santos
 *
 */
@Scope("session")
@Component("projetoMonitoria") 
public class ProjetoMonitoriaMBean extends SigaaAbstractController<ProjetoEnsino> {	
	
    /** Controla o fluxo nos diversos passos do cadastro de projetos de ação integrada */
	private ControleFluxo controleFluxo;
    
	/** Mensagem para o envio do projeto a PROGRAD(Pró-Reitoria de Graduação). */
	public static final String ENVIAR_PROJETO = "Enviar Proposta de Projeto para PROGRAD";

	/** Mensagem ao gravar o projeto. */
	public static final String GRAVAR_PROJETO = "Gravar Proposta de Projeto";

	/** Mensagem ao gravar um novo docente. */
	public static final String GRAVAR_NOVO_DOCENTE = "Confirmar Inclusão de Novo Docente";

	/** Anexo ao projeto de monitoria. */
	private UploadedFile file;

	/** Componente curricular da monitoria. */
	private ComponenteCurricularMonitoria componente = new ComponenteCurricularMonitoria();

	/** Equipe de docente da monitoria. */
	private EquipeDocente equipeDocente = new EquipeDocente();

	/** Lista de componentes curriculares selecionados. */
	private ArrayList<ComponenteCurricularMonitoria> componentesSelecionados = new ArrayList<ComponenteCurricularMonitoria>();

	/** Lista de componentes curriculares a remover. */
	private ArrayList<ComponenteCurricularMonitoria> componentesRemovidos = new ArrayList<ComponenteCurricularMonitoria>();

	/** Lista de docentes a remover dos componentes curriculares. */
	private ArrayList<EquipeDocenteComponente> docentesComponentesRemovidos = new ArrayList<EquipeDocenteComponente>();

	/** Indica se a Busca é filtrada por título. */
	private boolean checkBuscaTitulo;

	/** Indica se a busca é filtrada por ano. */
	private boolean checkBuscaAno;

	/** Indica se a Busca é filtrada por situação. */
	private boolean checkBuscaSituacao;
	
	/** Indica se a Busca é filtrada por Projetos Sem Relatório. */
	private boolean checkBuscaSemRelatorio;

	/** Indica se a Busca é filtrada por componente. */
	private boolean checkBuscaComponente;

	/** Indica se a Busca é filtrada por Edital. */
	private boolean checkBuscaEdital;

	/** Indica se a Busca é filtrada por servidor. */
	private boolean checkBuscaServidor;

	/** Indica se a Busca é filtrada por usuário. */
	private boolean checkBuscaUsuario;

	/** Indica se a Busca é filtrada por tipo de projeto. */
	private boolean checkBuscaTipoProjeto;

	/** Indica se a Busca é filtrada por centro. */
	private boolean checkBuscaCentro;

	/** Indica se o projeto é uma ação acadêmica associada. */
	private boolean checkBuscaProjetoAssociado;
	
	/** Nome do projeto a ser filtrado na busca. */
	private String buscaNomeProjeto;
	
	/** Ano a ser filtrado na busca. */
	private Integer buscaAnoProjeto;

	/** Tipo de projeto utilizado na busca. */
	private TipoProjetoEnsino buscaTipoProjeto = new TipoProjetoEnsino();

	/** Tipos de situações de projeto, utilizados na busca. */
	private TipoSituacaoProjeto buscaSituacaoProjeto = new TipoSituacaoProjeto();

	/** Centro utilizado na busca. */
	private Unidade buscaCentro = new Unidade();
	
	/** Utilizado na busca geral de projetos de monitoria associados. */
	private Boolean buscaProjetoAssociado;

	/** Edital utilizado na busca. */
	private EditalMonitoria edital = new EditalMonitoria();

	/** Servidor utilizado na busca. */
	private Servidor servidor = new Servidor();
	
	/** Usuário utilizado na busca. */
	private Servidor usuarioBusca = new Servidor();

	/** Coleção de projetos encontrados. */
	private Collection<ProjetoEnsino> projetosLocalizados;

	/** ID do novo coordenador do projeto. */
	private int idNovoCoordenador;

	/** Indica se há seleção de coordenador. */
	private boolean selecaoCoordenador = false;

	/** Indica se mostra os docentes. */
	private boolean mostarDocentes;

	/** Indica se o docente é novo. */
	private boolean novoDocente = false;

	/** Indica se deve gerar um relatório. */
	private boolean checkGerarRelatorio;

	/** Tipos de projetos de ensino. */
	private TipoProjetoEnsino tipoProjetoEnsino = new TipoProjetoEnsino();

	/** Permite o envio de fotos a partir do cadastro do projeto de monitoria */
	/** Descrição da foto*/
	private String descricaoFoto;
	
	/** Indica se é possível enviar propostar durante o cadastro de porjeto de ensino */
	private boolean podeEnviarProjeto = false;
	
	/** coordenador projeto ensino */
	private MembroProjeto coordenadorPro = new MembroProjeto();
	
	/**
	 * Tipo de relatório
	 */
	private Integer tipoRelatorio;
	
	/** Arquivo que representa a foto no mbean */
	private UploadedFile foto;
	
    /** Atributo utilizado para Upload de Arquivos */
    private UploadedFile fileArquivo;
    /** Atributo utilizado para descrição do Arquivo */
    private String descricaoArquivo;
	
    /** Armazena um orçamento detalhado. **/
    private OrcamentoDetalhado orcamento = new OrcamentoDetalhado();
    
    /** Armazena a tabela orçamentária. **/
    private Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
	
	/** Constante que define o tamanho da foto miniaturizada para exibição nos formulários */
	private static final int HEIGHT_FOTO = 100;
	/** Constante que define o tamanho da foto miniaturizada para exibição nos formulários */
	private static final int WIDTH_FOTO = 100;
	/** Usado pra identificar a posição do OrcamentoDetalhe na lista. */
	private int posicaoOrcamento = 0;
	/** Usado pra identificar o tipo do projeto. */
	boolean checkPAMQEG = false;
	/** Usado pra identificar o tipo do projeto. */
	boolean checkMonitoria = false;
	
	/**
	 * Construtor padrão. Inicializa o MBean removendo objetos antigos de sessão
	 * e instânciando outros objetos importantes.
	 * 
	 */
	public ProjetoMonitoriaMBean() {
		clear();
	}

	/**
	 * Inicializa objetos importantes no cadastro de um novo projeto.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>Não invocado por JSP(s)</li>
	 * </ul>
	 */
	public void clear() {
		obj = new ProjetoEnsino();
		obj.getProjeto().setRenovacao(false);
		obj.getAvaliacoes().iterator();
		obj.setComponentesCurriculares(new HashSet<ComponenteCurricularMonitoria>());
		servidor.setPessoa(new Pessoa());
		usuarioBusca.setPessoa(new Pessoa());
		buscaAnoProjeto = CalendarUtils.getAnoAtual();
		orcamento = new OrcamentoDetalhado();
		tabelaOrcamentaria.clear();
		componentesRemovidos.clear();
		docentesComponentesRemovidos.clear();
		podeEnviarProjeto = false;
		posicaoOrcamento = 0;
		checkPAMQEG = false;
		checkMonitoria = false;
	}

	/**
	 * Inicia o cadastro de projetos de monitoria.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/ProjetoMonitoria/meus_projetos.jsp</li>
	 * <li>sigaa.war/portais/docente/docente.jsp</li>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarProjetoMonitoria() throws SegurancaException {
		checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);

		try {
			obj.getTipoProjetoEnsino().setId(
					TipoProjetoEnsino.PROJETO_DE_MONITORIA);

			ListaMensagens mensagens = new ListaMensagens();
					
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}

			setOperacaoAtiva(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA.getId());
			prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
			controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_MONITORIA);
			return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_DIMENSAO_PROJETO);

		} catch (ArqException e) {
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}
	}

	public String iniciarProjetoMonitoriaInterno() throws SegurancaException {
		clear();
		obj.getProjeto().setInterno(Boolean.TRUE);
		return iniciarProjetoMonitoria();
	}
	
	public String iniciarProjetoMonitoriaExterno() throws SegurancaException {
		clear();
		obj.getProjeto().setInterno(Boolean.FALSE);
		return iniciarProjetoMonitoria();
	}

	/**
	 * Método que retorna o próximo passo do fluxo em execução. Grava os dados
	 * da tela atual e passa para próxima tela do fluxo.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @return retorna o próximo passo do fluxo
	 */
	public String proximoPasso() {
		try {
    		if (isFluxoCadastroValido()) {
    			return forward(controleFluxo.proximoPasso());
    		} else {
    			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
    			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
    			redirectJSF(getSubSistema().getLink());
    			return cancelar();
    		}
    	}catch (Exception e) {
    		tratamentoErroPadrao(e);
    	}
    	return null;
	}

	/**
	 * Método que retorna o passo anterior do fluxo em execução.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/anexar_fotos.jsp</li>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/componente_curricular.jsp</li>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/resumo.jsp</li>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/selecao_docente.jsp</li>
	 *  <li>sigaa.war/monitoria/SelecaoCoordenador/selecao_coordenador.jsp</li>
	 * </ul>
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
	 * Inicia o cadastro de Projetos de Apoio a Melhoria da Qualidade do Ensino de Graduação
	 * Somente diretores de centro ou docente com uma permissão especial tem acesso a esta operação.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String novoProjetoPAMQEG() throws SegurancaException {
	    checkRole(SigaaPapeis.DOCENTE);

	    try {
		obj = new ProjetoEnsino();
		obj.getTipoProjetoEnsino().setId(TipoProjetoEnsino.PROJETO_PAMQEG);			
		
		Projeto pj = new Projeto();
		obj.setProjeto(pj);
		
		MembroProjeto coordenador = new MembroProjeto();
		coordenador.setCategoriaMembro(new CategoriaMembro(CategoriaMembro.DOCENTE));
		coordenador.setFuncaoMembro(new FuncaoMembro(FuncaoMembro.COORDENADOR));
		coordenador.setAtivo(true);		
		coordenador.setDiscente(null);
		coordenador.setDocenteExterno(null);
		pj.setCoordenador(coordenador);


		ListaMensagens mensagens = new ListaMensagens();
		ProjetoMonitoriaValidator.validaEditaisAbertos(obj, mensagens);

		if (!mensagens.isEmpty()) {
		    addMensagens(mensagens);
		    return null;
		}

		prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
		controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_PAMQEG);
		setConfirmButton("Cadastrar");
		return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_FORM);

	    } catch (ArqException e) {
		notifyError(e);
		addMensagemErroPadrao();
		return null;
	    }
	}

	/**
	 * Valida a dimensão do projeto de monitoria e segue com o fluxo 
	 * para formulário principal.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/dimensao_projeto.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String submeterDimensao() throws SegurancaException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);
	    
		if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA.getId(), SigaaListaComando.REEDITAR_PROJETO_MONITORIA.getId())){
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
			return cancelar();
		}	
		
		if (!checkPAMQEG && !checkMonitoria)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Dimensão do Projeto");
		
		if (checkPAMQEG && !checkMonitoria){
			obj.getTipoProjetoEnsino().setId(TipoProjetoEnsino.PROJETO_PAMQEG);
		} else if (!checkPAMQEG && checkMonitoria) {
			obj.getTipoProjetoEnsino().setId(TipoProjetoEnsino.PROJETO_DE_MONITORIA);
		} else			
			obj.getTipoProjetoEnsino().setId(TipoProjetoEnsino.AMBOS_MONITORIA_PAMQEG);
					
		if (obj.getEditalMonitoria() != null && (obj.getEditalMonitoria().getTipo() != Edital.MONITORIA_EOU_INOVACAO && obj.getEditalMonitoria().getTipo()!=obj.getTipoEdital()) 
				&& obj.getEditalMonitoria().getTipo() != Edital.MONITORIA_EXTERNO )
			obj.setEditalMonitoria(new EditalMonitoria());
			
		// inicializa campos para projeto PAMQEG
		if (obj.getTipoProjetoEnsino().getId() == TipoProjetoEnsino.PROJETO_PAMQEG || obj.getTipoProjetoEnsino().getId() == TipoProjetoEnsino.AMBOS_MONITORIA_PAMQEG){
			if ( obj.getProjeto().isInterno() )
				controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_COMPLETO_PAMQEG);
			else
				controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_COMPLETO_PAMQEG_EXTERNO);
		} else
			if ( obj.getProjeto().isInterno() )
				controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_MONITORIA);
			else
				controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_MONITORIA_EXTERNO);
		
		if (obj.getEditalMonitoria() == null)
			obj.setEditalMonitoria(new EditalMonitoria());
		
		if (ValidatorUtil.isEmpty(obj.getProjeto().getUnidade()))
			obj.getProjeto().setUnidade(null);	
		
		if (hasErrors()) {
		    return null;
		}
		
		return proximoPasso();
	}
	
	/**
	 * Valida dados gerais de projetos de monitoria e segue com o fluxo 
	 * para seleção de componentes curriculares.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/selecao_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException 
	 */
	public String submeterDadosGerais() throws SegurancaException, DAOException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);

		// validando dados gerais do projeto
		ListaMensagens mensagens = new ListaMensagens();
		ProjetoMonitoriaValidator.validaDadosProjeto(obj, mensagens);

		if (!mensagens.isEmpty()) {
		    addMensagens(mensagens);
		    return null;
		}

		// possibilita a visualização dos detalhes dos objetos na tela de resumo			
		if ( obj.getProjeto().isInterno() && obj.getEditalMonitoria().getId() != 0) {
		    getGenericDAO().initialize(obj.getEditalMonitoria());
		}

		return proximoPasso();
	}
	
	/**
	 * Tela de informar os docentes e associá-los aos componentes curriculares
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/resumo.jsp</li>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/componente_curricular.jsp</li>
	 *  <li>sigaa.war/monitoria/SelecaoCoordenador/selecao_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String submeterComponentesCurriculares() throws ArqException {
		checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);

		try {
			ListaMensagens mensagens = new ListaMensagens();
			//usado para pegar o ano e o período letivo atual para validar os componentes curriculares...
			//projetos de monitoria usa sempre o calendário de graduação
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
			if (!isEmpty(cal)) {
				ProjetoMonitoriaValidator.validaComponentesCurriculares(obj, obj.getComponentesCurriculares(), 0, 0, mensagens);
				//Membros da PROGRAD(Pró-Reitoria de Graduação) podem cadastrar componentes no projeto sem restrição alguma (suporte aos usuários)
				if (!getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_MONITORIA)) {
					ProjetoMonitoriaValidator.validaComponentesCurriculares( obj, obj.getComponentesCurriculares(), cal.getAno(), cal.getPeriodo(), mensagens);
				}
			} else {
				mensagens.addErro("Erro ao buscar parâmetros da Unidade Gestôra Acadêmica.");
			}

			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				if (!mensagens.getErrorMessages().isEmpty()) {
					return null;
				}
			}

			return proximoPasso();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}
	}
	
	
	
	/**
	 * Verifica se existem os docentes de todos os componentes(disciplinas) do projeto e vai
	 * para tela de seleção do(a) coordenador(a)
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/selecao_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String submeterDocentes() throws SegurancaException {
		checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);
		try {

			ListaMensagens mensagens = new ListaMensagens();
			ProjetoMonitoriaValidator.validaComponentesCurricularesSemDocentes(obj, mensagens);
			ProjetoMonitoriaValidator.validaRelacaoDiscenteOrientadores(obj, mensagens);

			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}

			obj.getProjeto().setEquipe(getGenericDAO().findByExactField(MembroProjeto.class, "projeto.id", obj.getProjeto().getId()));
			return proximoPasso();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}
	}
	
	
	/**
	 * Utilizado na alteração do coordenador pela prograd(Pró-Reitoria de Graduação) e 
	 * na indicação de coordenador durante o cadastro do projeto.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/SelecaoCoordenador/selecao_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public String submeterSelecaoCoordenador() {
	    try {
		
			int idServidorCoordenador = getParameterInt("id", 0);
			if (idServidorCoordenador == 0) {
			    addMensagemErro("Escolha um docente.");
			    return null;
			}
	
			//Indica se vem da tela que só permite alteração de coordenador ou do fluxo normal de cadastro de projetos.
			if (selecaoCoordenador) {
			    idNovoCoordenador = getParameterInt("id");
			} else {
	
			    //remove o coordenador atual (todos para coordenador = false)
			    for (ComponenteCurricularMonitoria ccm : obj.getComponentesCurriculares()) {
			    	for (EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
			    		edc.getEquipeDocente().setCoordenador(false);
			    		edc.getEquipeDocente().setDataInicioCoordenador(null);
			    	}
			    }		    
			}
			
			definirCoordenadorProjeto(obj.getProjeto(), new Servidor(idServidorCoordenador));
			
			ListaMensagens mensagens = new ListaMensagens();
			ProjetoMonitoriaValidator.validaComponentesCurricularesSemDocentes(obj, mensagens);
			//ProjetoMonitoriaValidator.validaCoordenador(obj, new Servidor(idServidorCoordenador), mensagens);
	
			if (!mensagens.isEmpty()) {
			    addMensagens(mensagens);
			    return null;
			}
			
	
			if (!selecaoCoordenador) {
			    //seta novo coordenador
			    for (ComponenteCurricularMonitoria ccm : obj.getComponentesCurriculares()) {
			    	for (EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
			    		if (edc.getEquipeDocente().getServidor().getId() == idServidorCoordenador) {
			    			edc.getEquipeDocente().setCoordenador(true);
			    			edc.getEquipeDocente().setDataInicioCoordenador(new Date());
			    			edc.getEquipeDocente().setDataFimCoordenador(edc.getEquipeDocente().getProjetoEnsino().getProjeto().getDataFim());
			    			break;
			    		}
			    	}
			    }
			    setPodeEnviarProjeto(true);
			    
			    return proximoPasso();
				
			//alteração de coordenador feita pela PROGRAD(Pró-Reitoria de Graduação)
			} else { 
			    return alterarCoordenador();
			}
	    } catch (DAOException e) {
	    	notifyError(e);
	    	return null;
	    }
	}

	
	/**
	 * Este método seta o coordenador em um projeto, ambos passados por parâmetro.
	 * 
	 * @param p
	 * @param s
	 * @throws DAOException
	 */
	private void definirCoordenadorProjeto(Projeto p, Servidor s) throws DAOException {
		s = getGenericDAO().findByPrimaryKey(s.getId(), Servidor.class);		
		s.setEscolaridade(null);
		s.setFormacao(null);
		
		MembroProjeto coordenadorProjeto = new MembroProjeto();
		coordenadorProjeto.setServidor(s);	    
		coordenadorProjeto.setPessoa(s.getPessoa());
		coordenadorProjeto.setDocenteExterno(null);
		coordenadorProjeto.setDiscente(null);
		coordenadorProjeto.setParticipanteExterno(null);
		coordenadorProjeto.setFuncaoMembro(new FuncaoMembro(FuncaoMembro.COORDENADOR));
		coordenadorProjeto.setCategoriaMembro(new CategoriaMembro(CategoriaMembro.DOCENTE));
		coordenadorProjeto.setDataInicio(p.getDataInicio());
		coordenadorProjeto.setDataFim(p.getDataFim());
		coordenadorProjeto.setAtivo(true);
		coordenadorProjeto.setProjeto(p);
		setCoordenadorPro(coordenadorProjeto);
		p.setCoordenador(null);
		// Unidade do projeto será igual a unidade de lotação do coordenador
		p.setUnidade(s.getUnidade());
		for(MembroProjeto m : p.getEquipe()){
			if (m.isCoordenador()) {
				m.setAtivo(false);
			}
		}
		p.getEquipe().add(coordenadorProjeto);
	}
	
	
	/**
	 * Método usado pra redirecionar para a tela de anexar fotos.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/selecao_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String submeterFotos() throws SegurancaException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);
	    return proximoPasso();
	}
	
	
	/**
	 * Carrega todos os docentes do projeto para seleção
	 * de um possível coordenador.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/SelecaoCoordenador/consultar_projeto.jsp</li>
	 * </ul>
	 * @return
	 */
	public String listarCoordenadores() {
	    try {
		selecaoCoordenador = true;
		prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);

		obj = new ProjetoEnsino();
		setId();
		this.obj =  getGenericDAO().findByPrimaryKey(obj.getId(), ProjetoEnsino.class);

		//evitar erro de lazy
		obj.getEquipeDocentes().iterator();
		for (EquipeDocente ed : obj.getEquipeDocentes()) {
		    ed.getOrientacoes().iterator();
		}
		for (ComponenteCurricularMonitoria ccm : obj.getComponentesCurriculares()) {
		    ccm.getDocentesComponentes().iterator();
		}
		obj.getProjeto().getEquipe().iterator();

	    } catch (Exception e) {
		addMensagemErro(e.getMessage());
		notifyError(e);
	    }
	    
	    if(!erros.isEmpty()) {
	    	addMensagens(erros);
	    	return null;
	    }
	    
	    return forward(ConstantesNavegacaoMonitoria.ALTERAR_COORDENADOR_PROJETO);
	}

	
	/**
	 * Permite que o coordenador do projeto seja alterado pela prograd(Pró-Reitoria de Graduação) ou pelo coordenador atual
	 * do projeto.
	 * 
	 *  Não é chamado por JSP
	 *  
	 * @return
	 */
	private String alterarCoordenador() {
	    // chamar processador
	    ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
	    mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
	    mov.setAcao(ProjetoMonitoriaMov.ACAO_ALTERAR_COORDENADOR);
	    mov.setObjMovimentado(obj);
	    mov.setIdNovoCoordenador(idNovoCoordenador);
	    
	    try {
		
		Projeto proj = getGenericDAO().findByPrimaryKey(obj.getProjeto().getId(), Projeto.class);
		if((proj.isProjetoAssociado()) && (proj.getCoordenador().getServidor().getId() != idNovoCoordenador)) {
			erros.addMensagem(MensagensMonitoria.ALTERAR_COORDENADOR_PROJETO_ENSINO_ASSOCIADO);
		}		
	    
		execute(mov, getCurrentRequest());
		addMensagemInformation("Alteração de coordenador do projeto foi realizada com sucesso");

	    } catch (NegocioException e) {
		addMensagens(e.getListaMensagens());
		return null;
	    } catch (Exception e) {
		notifyError(e);
	    }
	    return cancelar();
	}

	/** Retorna uma lista de Equipe de Docentes
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/SelecaoCoordenador/selecao_coordenador.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<EquipeDocente> getEquipeDocentes() {
	    if (selecaoCoordenador)
		return obj.getEquipeDocentes();
	    else {
		ArrayList<EquipeDocente> lista = new ArrayList<EquipeDocente>(0);
		for (ComponenteCurricularMonitoria ccm : obj.getComponentesCurriculares()) {
		    for (EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
			if (!lista.contains(edc.getEquipeDocente())) {
			    lista.add(edc.getEquipeDocente());
			}
		    }
		}
		return lista;
	    }
	}
	
	
	/**
	 * Lista de todos os componentes curriculares do projeto
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @return Coleção de SelectItem usado em selects html
	 */
	public Collection<SelectItem> getComponentesCombo() {
	    List<ComponenteCurricularMonitoria> lista = new ArrayList<ComponenteCurricularMonitoria>(obj.getComponentesCurriculares());
	    return toSelectItems(lista, "disciplina.id", "disciplina.nome");
	}

	
	/**
	 * Lista de todos os orientadores do projeto
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @return Coleção de SelectItem usado em selects html
	 */
	public Collection<SelectItem> getOrientadoresCombo() {
	    List<Orientacao> lista = new ArrayList<Orientacao>();
	    for (EquipeDocente equipeDocente : obj.getEquipeDocentes()) {
		lista.addAll(equipeDocente.getOrientacoes());
	    }

	    return toSelectItems(lista, "equipeDocente.servidor.id", "equipeDocente.servidor.pessoa.nome");
	}

	/**
	 * Retorna todas as situações possíveis de projetos cadastradas no banco
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/AlterarSituacaoProjeto/form.jsp</li>
	 * <li>sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * <li>sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * <li>sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTipoSituacaoProjetoCombo() {
	    try {
		Collection<TipoSituacaoProjeto> situacoes = getGenericDAO().findByExactField(TipoSituacaoProjeto.class, "contexto", "M", "asc",
		"descricao");
		
		for (TipoSituacaoProjeto tipoSit : situacoes) {
			if(tipoSit.getId() == TipoSituacaoProjeto.MON_RENOVADO_COM_REDUCAO_BOLSAS){
				situacoes.remove(tipoSit);
				break;
			}
		}
		
		return toSelectItems(situacoes, "id", "descricao");
	    } catch (Exception e) {
		notifyError(e);
		addMensagemErro(e.getMessage());
		return null;
	    }
	}

	/**
	 * Retorna todos os tipos de projetos de ensino
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getAllTipoProjetoEnsinoCombo() {
	    return getAll(TipoProjetoEnsino.class, "id", "descricao");
	}

	/**
	 * Lista todos os projetos onde usuário logado fez o cadastro do mesmo (é o autor)
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/ProjetoMonitoria/meus_projetos.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 * 
	 */
	public Collection<ProjetoEnsino> getProjetosGravados() throws SegurancaException {
	    checkDocenteRole();

	    try {
		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		return dao.findGravadosByUsuario(getUsuarioLogado(), null);
	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro("Erro ao Listar projeto(s) do usuário atual.");
		return null;
	    }
	}
	
	/**
	 * Retorna os projetos de apoio à qualidade do ensino do usuário logado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/meus_projetos_pamqeg.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public Collection<ProjetoEnsino> getProjetosGravadosApoioMelhoriaQualidadeEnsino() throws SegurancaException {
	    checkDocenteRole();

	    try {
		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		return dao.findGravadosByUsuario(getUsuarioLogado(), TipoProjetoEnsino.PROJETO_PAMQEG);
	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro("Erro ao Listar projeto(s) do usuário atual.");
		return null;
	    }
	}
	
	/**
	 * Lista todos os projetos ativos coordenados pelo usuário logado 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/AssociarMonitorDocente/lista.jsp</li>
	 *  <li>/monitoria/CadastrarAvisoProjeto/lista_projetos.jsp</li>
	 *  <li>/monitoria/CadastrarProvaSelecao/projetos.jsp</li>
	 *  <li>/monitoria/RelatorioProjetoMonitoria/lista.jsp</li>
	 *  <li>/monitoria/VisualizarAvaliacoes/lista_projetos.jsp</li>
	 *  <li>/monitoria/ResumoSid/lista.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 * 
	 */
	public Collection<ProjetoEnsino> getProjetosAtivosCoordenadosUsuarioLogado() throws SegurancaException {
	    checkDocenteRole();

	    try {	
		//somente servidores ativos tem projetos de monitoria
		if(!isEmpty(getServidorUsuario())){
		    ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		    return dao.findValidosByServidor(getServidorUsuario().getId(), true);
		}else
		    return null;	
	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro("Erro ao Listar projeto(s) do usuário atual.");
		return null;
	    }
	}

	/**
	 * Lista todos os projetos do usuário logado onde ele é participante ATIVO do
	 * projeto
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @throws SegurancaException 
	 * 
	 */
	public Collection<ProjetoEnsino> getProjetosUsuarioLogado() throws SegurancaException {
	    checkDocenteRole();

	    try {	
		//somente servidores ativos tem projetos de monitoria
		if(!isEmpty(getServidorUsuario())){
		    ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		    return dao.findValidosByServidor(getServidorUsuario().getId(), null);
		}else {
		    return null;
		}
	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro("Erro ao Listar projeto(s) do usuário atual");
		return null;
	    }
	}	
	
	/**
	 * Retorna projetos do usuário atual cadastrados no banco pra exibição no
	 * select
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public Collection<SelectItem> getProjetosUsuarioLogadoCombo() throws SegurancaException {		
	    return toSelectItems(getProjetosUsuarioLogado(), "id", "titulo");		
	}

	/**
	 * Lista todos os projetos do usuário logado onde ele é participante
	 * ATIVO ou INATIVO
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>sigaa.war/monitoria/ProjetoMonitoria/meus_projetos.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 * 
	 */
	public Collection<ProjetoEnsino> getMeusProjetos() throws SegurancaException {
	    checkDocenteRole();

	    try {	
		//somente servidores ativos tem projetos de monitoria
		if(!isEmpty(getServidorUsuario())){
		    ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		    return dao.findMeusProjetos(getServidorUsuario().getId(), null, null, null, null);
		}else {
		    return null;
		}
	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro("Erro ao Listar projeto(s) do usuário atual");
		return null;
	    }
	}
	

	/**
	 * Retorna projetos PAMQEG do usuário logado.
	 * Projetos PAMQEG são projeto de apoio à melhoria da qualidade de ensino. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/meus_projetos_pamqeg.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public Collection<ProjetoEnsino> getMeusProjetosApoioMelhoriaQualidadeEnsino() throws SegurancaException {
	    checkDocenteRole();

	    try {	
		//somente servidores ativos tem projetos de melhoria da qualidade
		if(!isEmpty(getServidorUsuario())){
		    ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		    return dao.findMeusProjetosPAMQEG(getServidorUsuario().getId());
		}else {
		    return null;
		}
	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro("Erro ao Listar projeto(s) do usuário atual");
		return null;
	    }
	}
	
	/**
	 * Lista todas as orientações do servidor
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não invocado por JSP´s</li>
	 * </ul>
	 * 
	 * @throws SegurancaException 
	 * 
	 */
	public Collection<Orientacao> getOrientacoesUsuarioLogado() throws SegurancaException {
	    checkDocenteRole();

	    try {
		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
		// Orientador Ativo = true
		return dao.findOrientacoesByServidor(getUsuarioLogado().getServidor().getId(), true);
	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro("Erro ao Listar orientações do usuário atual");
		return null;
	    }
	}

	/**
	 * Retorna os editais abertos para o projeto de ensino 
	 * que está sendo cadastrado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 * <ul>
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getEditaisCombo() throws DAOException {
		return ((EditalMonitoriaMBean) getMBean("editalMonitoria")).getEditaisAbertosByTipoCombo(obj.getTipoEdital());
	}
	
	/**
	 * Retorna os editais de configuração abertos para o projeto externo de ensino 
	 * que está sendo cadastrado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 * <ul>
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getConfiguracaoProjetoExternoCombo() throws DAOException {
		return ((EditalMonitoriaMBean) getMBean("editalMonitoria")).getEditalConfiguracaoProjetoExternoAbertos();
	}
	
	/**
	 * Adiciona uma componente curricular informado na visualização
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String adicionaDisciplina() throws SegurancaException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);
	    DisciplinaDao dao =  getDAO(DisciplinaDao.class);
	    try {
	    	ComponenteCurricular disciplina = null;
		    Integer id = getParameterInt("id", 0);
		    disciplina =  dao.findByPrimaryKey(id, ComponenteCurricular.class);
			ListaMensagens mensagens = new ListaMensagens();
			ProjetoMonitoriaValidator.validaNovoComponenteCurricular(disciplina, obj, mensagens);
			if (!mensagens.isEmpty()) {
			    addMensagens(mensagens);
			    return null;
			}
			componente.setDisciplina(disciplina);
			obj.addComponenteCurricular(componente);
			componente = new ComponenteCurricularMonitoria();
	    } catch (DAOException e) {
			tratamentoErroPadrao(e, e.getMessage());
	    }
	    return null;
	}

	/**
	 * Remove o componente curricular selecionado e com ele todas relações 
	 * entre os docentes que seriam orientadores dos alunos neste componente.
	 * 
	 * Se componente já tiver sido cadastrado no banco, adiciona-o a uma lista de
	 * objetos que será utilizada posteriormente no processador para remoção no banco.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/componente_curricular.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String removeDisciplina() throws SegurancaException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);

	    DisciplinaDao dao = getDAO(DisciplinaDao.class);
	    try {

		Integer id = getParameterInt("idDisciplina", 0);
		ComponenteCurricular d = dao.findByPrimaryKey(id, ComponenteCurricular.class);

		for (ComponenteCurricularMonitoria ccm : obj.getComponentesCurriculares()) {
		    if (ccm.getDisciplina().equals(d)) {
			// remove da view
			obj.getComponentesCurriculares().remove(ccm);					
			//só inclui na lista para remover se já estiver no banco...
			if (ccm.getId() != 0){
			    ComponenteCurricularMonitoria compDoBanco = dao.findByPrimaryKey(ccm.getId(), ComponenteCurricularMonitoria.class);
			    if (!isEmpty(compDoBanco)) {
				componentesRemovidos.add(ccm);
			    }
			}
			break;
		    }
		}
		//Retirar da sessão do hibernate
		dao.detach(d);

	    } catch (DAOException e) {
		notifyError(e);
		addMensagemErro(e.getMessage());
	    }
	    return null;
	}

	/**
	 * Remove um DocenteComponente do projeto.
	 * Um DocenteComponente relaciona um docente a uma disciplina
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/selecao_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String removeDocenteComponente() throws SegurancaException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);
	    
	    Integer idDis = getParameterInt("idDisciplina", 0);
	    Integer idDoc = getParameterInt("idDocente", 0);

	    for (ComponenteCurricularMonitoria ccm : obj.getComponentesCurriculares()) {
	    	if (ccm.getDisciplina().getId() == idDis) {
	    		for (EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
	    			if (edc.getEquipeDocente().getServidor().getId() == idDoc) {
	    				ccm.removeDocenteComponente(edc);
	    				if (edc.getId() != 0) {
	    					docentesComponentesRemovidos.add(edc);
	    				}
	    				break;
	    			}
	    		}
	    	}
	    }
	    return null;
	}

	/**
	 * Adiciona o docente e relaciona-o com os componentes curriculares
	 * selecionados criando a estrutura: projeto -> componente curricular ->
	 * equipe componente curricular -> equipe docente
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/selecao_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String adicionaDocente() throws SegurancaException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);

	    ListaMensagens mensagens = new ListaMensagens();
	    EquipeDocenteDao equipeDao = getDAO(EquipeDocenteDao.class);

	    try {
		
	    	//Carrega dados do servidor do banco para evitar null pointer na validação.
	    	if (!isEmpty(equipeDocente.getServidor())) {
	    		equipeDocente.setServidor( equipeDao.findByPrimaryKey(equipeDocente.getServidor().getId(), Servidor.class));

        		//Componentes selecionados na view
        		componentesSelecionados = new ArrayList<ComponenteCurricularMonitoria>();			
        		for (ComponenteCurricularMonitoria ccm : obj.getComponentesCurriculares()) { 
        		    if (ccm.isSelecionado()){					
        		    	componentesSelecionados.add(ccm);	
        		    	ccm.setSelecionado(false);
        		    }
        		}
        
        		// validando
        		equipeDocente.setProjetoEnsino(obj);
        		ProjetoMonitoriaValidator.validaAdicionaDocente(obj, componentesSelecionados, equipeDocente, mensagens);
        
        		if (!mensagens.isEmpty()) {
        		    addMensagens(mensagens);
        		    return null;
        		}
        
        		// Verifica que já tem esse servidor na equipeDocente do projeto
        		EquipeDocente docenteNoProjeto = equipeDao.findByServidorProjeto(equipeDocente.getServidor().getId(), obj.getId());
        
        		// se ainda não tem, monta um novo equipeDocente
        		if (isEmpty(docenteNoProjeto)) {
        		    
        		    boolean servidorJaIncluido = false;
        		    // Aproveita o orientador já incluído no projeto para não cadastrá-lo 2 vezes.
        		    // Os docentes que serão testados aqui, ainda não foram para o banco de dados
        		    // por isso procuro dentro dos componentes curriculares.
        				for (ComponenteCurricularMonitoria cc : obj.getComponentesCurriculares()) {
        					for (EquipeDocenteComponente edc : cc.getDocentesComponentes()) {
        						if ((edc.getEquipeDocente().getServidor().getId() == equipeDocente.getServidor().getId())) {
        							// faz o novo docente ser o que já estava no projeto
        							// impedindo duplicação
        							equipeDocente = edc.getEquipeDocente();
        							servidorJaIncluido = true;
        						}
        					}
        				}
        		    
        		    // Verifica se o servidor já está no projeto
        		    if (!servidorJaIncluido) {
        		    	// carrega todos os dados do servidor direto do banco
        		    	equipeDocente.setServidor( equipeDao.findByPrimaryKey(equipeDocente.getServidor().getId(), Servidor.class));
        		    }
        		    // inicializando propriedades do novo docente do projeto
        		    equipeDocente.setDataEntradaProjeto(obj.getProjeto().getDataInicio());
        		    equipeDocente.setDataSaidaProjeto(null);
        		    equipeDocente.setAtivo(true);
        		    equipeDocente.setProjetoEnsino(obj);
        
        		    // Se já está na equipe do projeto, só atualiza.
        		} else {
        		    equipeDocente = docenteNoProjeto;
        		}
        
        		// criando e associando os componentes curriculares ao equipeDocente
        		// Incluído agora
        		for (ComponenteCurricularMonitoria compSelecionado : componentesSelecionados) {
        		    compSelecionado.getDocentesComponentes().iterator();
        		    EquipeDocenteComponente docenteComponente = new EquipeDocenteComponente();
        		    docenteComponente.setEquipeDocente(equipeDocente);
        		    docenteComponente.setDataVinculacao(new Date());
        		    docenteComponente.setAtivo(true);
        		    compSelecionado.addDocenteComponente(docenteComponente);
        		}
	    	}else
	    		addMensagemErro("Docente: Campo obrigatório não informado");
	    		
		
	    } catch (Exception e) {
		tratamentoErroPadrao(e);
	    }

	    // limpa os dados
	    componentesSelecionados = new ArrayList<ComponenteCurricularMonitoria>();
	    equipeDocente = new EquipeDocente();
	    return null;
	}
	
	/**
	 * Inativa um projeto de monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	@Override
	public String remover() throws SegurancaException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);
	    try {	    
	    	//super.inativar();
	    	ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
	    	mov.setAcao(ProjetoMonitoriaMov.ACAO_DESATIVAR_PROJETO);
	    	mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
	    	mov.setObjMovimentado(obj);
	    	execute(mov);
	    	addMensagemInformation("Operação realizada com sucesso.");
	    	setResultadosBusca(null);
	    	
	    	if(obj.getTipoProjetoEnsino().getId() == TipoProjetoEnsino.PROJETO_DE_MONITORIA){
	    		resetBean();
	    		return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_MEUS_PROJETOS);	    		
	    	}
	    	else {
	    		resetBean();
	    		return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_MEUS_PROJETOS_PAMQEG);
	    		
	    	}
	    		
	    	
	    	
	    } catch (ArqException e) {
	    	notifyError(e);
	    } catch (NegocioException e) {
	    	addMensagemErro(e.getMessage());
	    }
	    return null;
	}
	
	/**
	 * Envia o projeto para os departamentos em caso de o cadastro ter sido realizado por um docente
	 * ou envia para prograd(Pró-Reitoria de Graduação), caso o cadastro tenha sido realizado por um gestor de monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/monitoria/ProjetoMonitoria/aviso_envio.jsp</li>
	 * 		<li>/monitoria/ProjetoMonitoria/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String enviarProjetoPrograd() throws ArqException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);
	    setConfirmButton(ENVIAR_PROJETO);
	    prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
	    return cadastrar();
	}

	/**
	 * Valida a tela de dados do projeto PAMQEG(Programa de Apoio à Melhoria da Qualidade do Ensino de Graduação).
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/siga.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 */
	public String cadastrarProjetoPAMQEG() throws SegurancaException {
	    checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);

	    try {
		ListaMensagens mensagens = new ListaMensagens();
				
		//MELHORAR: permitir alteração do arquivo de projetos de inovação		
		if ((file == null) || ("".equals(file.getName()))){
		    mensagens.addErro("Arquivo Anexo: campo obrigatório não informado.");
		}else {
		    int idArquivo = EnvioArquivoHelper.getNextIdArquivo();	
		    EnvioArquivoHelper.inserirArquivo(idArquivo, file.getBytes(), file.getContentType(), file.getName());
		    obj.setIdArquivo(idArquivo);
		}
		
		//adicionar coordenador
		if (obj.getProjeto().getCoordenador().getServidor().getId() > 0) {
		    adicionarCoordenadorPAMQEG();
		}
		
		ProjetoMonitoriaValidator.validaDadosProjeto(obj, mensagens);

		if (!mensagens.isEmpty()) {
		    addMensagens(mensagens);
		    return null;
		}	
		
		setConfirmButton(ENVIAR_PROJETO);
		return cadastrar();

	    } catch (Exception e) {
	    	notifyError(e);
	    	addMensagemErro(e.getMessage());
			return null;
	    }
	}
	
	
	/**
	 * Método responsável por cadastrar coordenador de projetos de
	 * Melhoria da Qualidade do Ensino de Graduação.
	 * 
	 */
	private void adicionarCoordenadorPAMQEG() {
	    GenericDAO dao = getGenericDAO();
	    try {
	    	if (!obj.isProjetoAssociado()) {
		    //Carrega o servidor selecionado pelo usuário para criar o novo membro da equipe do projeto.
		    dao.initialize(obj.getProjeto().getCoordenador().getServidor());
	    	    obj.getProjeto().getCoordenador().setPessoa(obj.getProjeto().getCoordenador().getServidor().getPessoa());
	    	    obj.getProjeto().getCoordenador().setDiscente(null);
	    	    obj.getProjeto().getCoordenador().setDocenteExterno(null);
	    	    obj.getProjeto().getCoordenador().setDataInicio(obj.getProjeto().getDataInicio());
	    	    obj.getProjeto().getCoordenador().setDataFim(obj.getProjeto().getDataFim());
	    	    obj.getProjeto().getCoordenador().setFuncaoMembro(new FuncaoMembro(FuncaoMembro.COORDENADOR));
	    	    obj.getProjeto().getCoordenador().setCategoriaMembro(new CategoriaMembro(CategoriaMembro.DOCENTE));
       			
	    	    // Definindo o coordenador do projeto.
	    	    obj.getProjeto().getCoordenador().setProjeto(obj.getProjeto());	    	    	
	    	    obj.getProjeto().getEquipe().add(obj.getProjeto().getCoordenador());		
       
	    	}
	    } catch (DAOException e) {
	    	tratamentoErroPadrao(e);
	    }

	}

	
	/**
	 * Cadastra projeto PAMQEG(Programa de Apoio à Melhoria da Qualidade do Ensino de Graduação)
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String cadastrarParcialProjetoPAMQEG() throws SegurancaException {
		checkRole(SigaaPapeis.DOCENTE, SigaaPapeis.GESTOR_MONITORIA);
		try {
		    
		    	// adicionar coordenador
			if (!isEmpty(obj.getProjeto().getCoordenador().getServidor())) {
				adicionarCoordenadorPAMQEG();
			}else {
			    addMensagemErro("Coordenador(a): campo obrigatório não informado.");
			    return null;
			}
			
			ListaMensagens mensagens = new ListaMensagens();
			ValidatorUtil.validateRequired(obj.getAno(), "Ano referência", mensagens);
			ValidatorUtil.validateRequired(obj.getUnidade(), "Centro", mensagens);
			ValidatorUtil.validateRequired(obj.getEditalMonitoria(), "Edital",	mensagens);

			if (!mensagens.isEmpty()) {
			    addMensagens(mensagens);
			    return null;
			}	
			
			setConfirmButton(GRAVAR_PROJETO);
			return cadastrar();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
	}

	/**
	 * Visualiza o arquivo do edital.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_projeto.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 */
	public void viewArquivo() {		
		try {
		    	int idArquivo = getParameterInt("idArquivo", 0);
			EnvioArquivoHelper.recuperaArquivo(getCurrentResponse(), idArquivo, false);			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Arquivo não encontrado.");
		}
		FacesContext.getCurrentInstance().responseComplete();
	}

	
	/**
	 * Valida a tela de dados gerais e grava parcialmente o projeto de ensino.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String cadastrarParcialDadosGerais() throws ArqException {
		
		if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA.getId())){
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
			return cancelar();
		}	
		ListaMensagens mensagens = new ListaMensagens();
		ProjetoMonitoriaValidator.validaDadosProjeto(obj, mensagens);
		if (!mensagens.isEmpty()) {
		    addMensagens(mensagens);
		    return null;
		}
		setConfirmButton(GRAVAR_PROJETO);
		return cadastrar();
	}

	/**
	 * Valida a tela da componentes e grava parcialmente o projeto de ensino
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/monitoria/ProjetoMonitoria/componente_curricular.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String cadastrarParcialComponentesCurriculares() throws ArqException {
		if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA.getId())){
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
			return cancelar();
		}	
		
		try {
			ListaMensagens mensagens = new ListaMensagens();
			CalendarioAcademico cal = getCalendarioVigente();
			if (!isEmpty(cal)) {
				ProjetoMonitoriaValidator.validaComponentesCurriculares(obj,
						obj.getComponentesCurriculares(), cal.getAno(), cal.getPeriodo(), mensagens);
			} else {
				mensagens.addErro("Erro ao buscar parâmetros da Unidade Gestôra Acadêmica.");
			}

			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				if (!mensagens.getErrorMessages().isEmpty()) {
					return null;
				}
			}
			setConfirmButton(GRAVAR_PROJETO);
			return cadastrar();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}
	}

	/**
	 * Valida a tela da equipe e grava parcialmente o projeto de ensino
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/selecao_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String cadastrarParcialEquipeDocente() throws ArqException {
		if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA.getId())){
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
			return cancelar();
		}	
		
		ListaMensagens mensagens = new ListaMensagens();
		ProjetoMonitoriaValidator.validaComponentesCurricularesSemDocentes(obj, mensagens);
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		setConfirmButton(GRAVAR_PROJETO);
		return cadastrar();
	}
	
	/**
	 * Valida a tela do coordenador e grava parcialmente o projeto de ensino.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/selecao_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String cadastrarParcialCoordenador() throws ArqException{
		if (!isOperacaoAtiva(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA.getId())){
			addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
			" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			redirectJSF(getSubSistema().getLink());
			return cancelar();
		}	
		
		try {
			int idServidorCoordenador = getParameterInt("id", 0);
			if (idServidorCoordenador == 0) {
			    addMensagemErro("Escolha um docente.");
			    return null;
			}
			
			//remove o coordenador atual (todos para coordenador = false)
			for (ComponenteCurricularMonitoria ccm : obj.getComponentesCurriculares()) {
				for (EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
					edc.getEquipeDocente().setCoordenador(false);
					edc.getEquipeDocente().setDataInicioCoordenador(null);
				}
			}			    

			definirCoordenadorProjeto(obj.getProjeto(), new Servidor(idServidorCoordenador));

			ListaMensagens mensagens = new ListaMensagens();
			ProjetoMonitoriaValidator.validaComponentesCurricularesSemDocentes(obj, mensagens);

			if (!mensagens.isEmpty()) {
			    addMensagens(mensagens);
			    return null;
			}
			
			//seta o novo coordenador
			for (ComponenteCurricularMonitoria ccm : obj.getComponentesCurriculares()) {
				for (EquipeDocenteComponente edc : ccm.getDocentesComponentes()) {
					if (edc.getEquipeDocente().getServidor().getId() == idServidorCoordenador) {
						edc.getEquipeDocente().setCoordenador(true);
						edc.getEquipeDocente().setDataInicioCoordenador(new Date());
						edc.getEquipeDocente().setDataFimCoordenador(edc.getEquipeDocente().getProjetoEnsino().getProjeto().getDataFim());
						break;
					}
				}
			}
			
			return cadastrar();
			
		} catch (DAOException e) {
			notifyError(e);
			return null;
		}
	}
	
	/**
	 * Valida a tela de anexar arquivo e grava parcialmente o projeto de ensino.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/orcamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String cadastrarParcialOrcamento() throws ArqException {
		
		ListaMensagens mensagens = new ListaMensagens();
		ProjetoMonitoriaValidator.validaOrcamentos(obj, mensagens);
		if (!mensagens.isEmpty()) {
		    addMensagens(mensagens);
		    return null;
		}
		setConfirmButton(GRAVAR_PROJETO);
		return cadastrar();
	}
	
	/**
	 * Cadastra um componente curricular em um projeto
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/monitoria/ProjetoMonitoria/resumo.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */	
	public String cadastrar() throws ArqException {

		try {
			// Detecta se o usuário é um Gestor de Monitoria e não é docente.
			// Somente Técnicos Administrativos com papel 'Gestor Monitoria' 
		    	// podem alterar propostas de projetos a qualquer momento.
			if (obj.getId() != 0 && isUserInRole(SigaaPapeis.GESTOR_MONITORIA) && !isUserInRole(SigaaPapeis.DOCENTE)) {

				ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
				mov.setObjMovimentado(obj);
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);

				mov.setComponentesCurricularesRemovidos(componentesRemovidos);
				mov.setEquipesDocenteComponenteRemovidos(docentesComponentesRemovidos);
				mov.setAcao(ProjetoMonitoriaMov.ACAO_GRAVAR_TEMPORARIAMENTE);
				mov.setIdNovoCoordenador(idNovoCoordenador);

				// Evita que o projeto seja enviado para os departamentos
				// novamente
				// alterações feitas pela prograd não devem alterar o ciclo de
				// vida do projeto
				mov.setSolicitacaoPrograd(true);
				obj = (ProjetoEnsino) execute(mov, getCurrentRequest());

				// Limpa componentes
				componentesRemovidos.clear();
				docentesComponentesRemovidos.clear();
				addMensagemInformation("Alteração realizada com sucesso.");
				resetBean();
				return forward(ConstantesNavegacaoMonitoria.ALTERARSITUACAOPROJETO_LISTA);

			} else {

				if (getConfirmButton().equalsIgnoreCase("remover"))
					return remover();
				else {

					ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
					mov.setObjMovimentado(obj);
					mov.setComponentesCurricularesRemovidos(componentesRemovidos);
					mov.setEquipesDocenteComponenteRemovidos(docentesComponentesRemovidos);
					mov.setIdNovoCoordenador(idNovoCoordenador);

					if (getConfirmButton().equalsIgnoreCase(ENVIAR_PROJETO)) {
						mov.setAcao(ProjetoMonitoriaMov.ACAO_ENVIAR_PROPOSTA_AOS_DEPARTAMENTOS);
						for (MembroProjeto m : obj.getProjeto().getEquipe()) {
							if(m.getFuncaoMembro().equals(FuncaoMembro.COORDENADOR)){
								setCoordenadorPro(m);
							}
						}
						obj.getProjeto().setCoordenador(coordenadorPro);
						
					} else {
						mov.setAcao(ProjetoMonitoriaMov.ACAO_GRAVAR_TEMPORARIAMENTE);
					}

					obj = (ProjetoEnsino) execute(mov, getCurrentRequest());
					componentesRemovidos.clear();
					docentesComponentesRemovidos.clear();
					return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_AVISO_ENVIO);

				}
			}
		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return forward(getFormPage());
		}
	}

	/**
	 * Devolver ação para reedição pelo coordenador cancela todas as solicitação
	 * de validação para todos os chefes de departamentos para os quais a ação
	 * foi enviada para solicitar autorização.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/AlterarSituacaoProjeto/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String reeditarProposta() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);

		try {
			int id = getParameterInt("id", 0);
			if (id > 0) {
				ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
				ProjetoEnsino projeto = dao.findByPrimaryKey(id, ProjetoEnsino.class);

				if (!isEmpty(projeto)) {
					prepareMovimento(SigaaListaComando.REEDITAR_PROJETO_MONITORIA);
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setObjMovimentado(projeto);
					mov.setCodMovimento(SigaaListaComando.REEDITAR_PROJETO_MONITORIA);
					execute(mov, getCurrentRequest());
					addMensagemInformation("Proposta devolvida para Coordenador(a) com sucesso.");
				} else {
					addMensagemErro("Operação não realizada: Projeto de monitoria não foi informado.");
				}
			}
		} catch (Exception e) {
			addMensagemErro(e.getMessage());
			notifyError(e);
		}

		// Atualizar a tela/extensao/AlterarAtividade/lista.jsp
		localizar();
		return forward("/monitoria/AlterarSituacaoProjeto/lista.jsp");
	}

	/**
	 * Chama método para localizar projeto na tela de situação do projeto e na tela de consultar avaliadores
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 *  	<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public void iniciarLocalizacao() throws DAOException {
		localizar();
	}

	/**
	 * Localiza projeto na tela de situação do projeto
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/DistribuicaoRelatorioProjeto/lista.jsp</li>
	 *  	<li>/sigaa.war/monitoria/RelatorioProjetoMonitoria/busca.jsp</li>
	 *  	<li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public void localizar() throws DAOException {

		erros.getMensagens().clear();
		projetosLocalizados = new ArrayList<ProjetoEnsino>();

		/* Analisando filtros selecionados */
		String titulo = null;
		Integer ano = null;
		Integer idSituacaoProjeto = null;
		Integer idComponenteCurricular = null;
		Integer idEdital = null;
		Integer idServidor = null;
		Integer idPessoaCadastro = null;
		Integer idTipoProjeto = null;
		Integer idCentro = null;
		Integer idTipoRelatorio = null;
		Boolean associado = null;

		ListaMensagens erros = new ListaMensagens();

		// Definição dos filtros e validações
		if (checkBuscaTitulo) {
			titulo = buscaNomeProjeto;
		}
		if (checkBuscaAno) {
			ano = buscaAnoProjeto;
			ValidatorUtil.validaInt(ano, "Ano", erros);
		}
		if (checkBuscaSituacao) {
			idSituacaoProjeto = buscaSituacaoProjeto.getId();
			ValidatorUtil.validateRequiredId(idSituacaoProjeto, "Situação do Projeto", erros);
		}
		
		if (checkBuscaSemRelatorio) {
			idTipoRelatorio = this.tipoRelatorio;
		}
		
		if (checkBuscaEdital) {
			idEdital = edital.getId();
			ValidatorUtil.validateRequiredId(idEdital, "Edital", erros);
		}
		if (checkBuscaServidor) {
			idServidor = servidor.getId();
			ValidatorUtil.validateRequiredId(idServidor, "Servidor", erros);
		}
		if (checkBuscaUsuario) {
			getGenericDAO().initialize(usuarioBusca);
			idPessoaCadastro = usuarioBusca.getPessoa().getId();
		}
		if (checkBuscaTipoProjeto) {
			idTipoProjeto = buscaTipoProjeto.getId();
			ValidatorUtil.validateRequiredId(idTipoProjeto, "Tipo de Projeto", erros);
		}
		if (checkBuscaCentro) {
			idCentro = buscaCentro.getId();
			ValidatorUtil.validateRequiredId(idCentro, "Centro do Projeto:", erros);
		}
		if (checkBuscaProjetoAssociado) {
			associado = buscaProjetoAssociado;			
		}

		
		if (checkBuscaComponente) {
			DisciplinaDao daoDisciplina = getDAO(DisciplinaDao.class);
			ComponenteCurricular disc = null;

			if (componente.getDisciplina().getCodigo().equals("")) {
				if ((getParameterInt("id") != null) && (getParameterInt("id") != 0)) {
					Integer idInt = getParameterInt("id");
					disc = daoDisciplina.findByPrimaryKey(idInt, ComponenteCurricular.class);
				}
			} else {
				disc = daoDisciplina.findByCodigo(componente.getDisciplina().getCodigo(), -1, 'G');
			}

			if (disc == null) {
				addMensagemErro("Disciplina não encontrada");
				return;
			} else {
				idComponenteCurricular = disc.getId();
			}
		}
		
		if( !checkBuscaTitulo && !checkBuscaTipoProjeto && !checkBuscaServidor && !checkBuscaEdital &&
				!checkBuscaAno && !checkBuscaSituacao && !checkBuscaCentro && !checkBuscaComponente && 
				!checkBuscaUsuario  && !checkBuscaProjetoAssociado && !checkBuscaSemRelatorio && !checkGerarRelatorio) {
			erros.addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}
		
		try {
			if (erros.isEmpty()) {
				ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
				projetosLocalizados = dao.filter(titulo, ano, idSituacaoProjeto,
						idComponenteCurricular, idEdital, idServidor,
						idPessoaCadastro, idTipoProjeto, idCentro, idTipoRelatorio, associado, false);
	
			} else {
				addMensagens(erros);
			}
		} catch (LimiteResultadosException e){
			addMensagemErro(e.getMessage());
		}
	}

	/** 
	 * Inicia a operação de atualização de dados cadastrais.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/monitoria/ProjetoMonitoria/lista.jsp</li>
	 *  <li>/monitoria/ProjetoMonitoria/meus_projetos.jsp</li> 
	 * 
	 *  
	 */
	@Override
	public String atualizar() {
		try {

			prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
			selecaoCoordenador = false;
			obj = new ProjetoEnsino();
			
			//controle de botão na tela de aviso_envio.jsp
			setPodeEnviarProjeto(false);
			
			setId();
			setReadOnly(false);
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), ProjetoEnsino.class);			

			if (obj.isProjetoMonitoria()) {
				controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_MONITORIA);
				checkPAMQEG = false;
				checkMonitoria = true;
			} else if (obj.isProjetoPAMQEG()) {
				controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_COMPLETO_PAMQEG);
				checkPAMQEG = true;
				checkMonitoria = false;
			} else {
				controleFluxo = new ControleFluxo(ControleFluxo.PROJETO_COMPLETO_PAMQEG);
				checkPAMQEG = true;
				checkMonitoria = true;
			}

			// Caso o projeto seja antigo e na época do cadastro ainda não haviam sido criados certos campos obrigatórios
			if (obj.getProcessoSeletivo() == null || obj.getProduto() == null)
				obj.setValidaNovosCampos(false);
				
			// carrega histórico
			if (obj.getHistoricoSituacao() != null) {
				obj.getHistoricoSituacao().iterator();
			}
			// carrega o componentes
			if (obj.getComponentesCurriculares() != null) {
				obj.getComponentesCurriculares().iterator();
			}
			if (obj.getEquipeDocentes() != null) {
				obj.getEquipeDocentes().iterator();
			}
			//evitar erro de lazy na troca do coordenador
			if (obj.getProjeto().getEquipe() != null) {
			    obj.getProjeto().getEquipe().iterator();
			}
			//evitar erro de lazy na atualização do projeto
			if (obj.getProjeto().getCronograma() != null) {
			    obj.getProjeto().getCronograma().iterator();
			}
			//evitar erro de lazy na atualização do projeto
			if(obj.getProjeto().getFotos() != null){
				obj.getProjeto().getFotos().iterator();
			}
			//evitar erro de lazy na atualização do projeto
			if(obj.getProjeto().getArquivos() != null){
				obj.getProjeto().getArquivos().iterator();
			}
			//evitar erro de lazy na atualização do projeto
			if(obj.getProjeto().getOrcamento() != null){
				obj.getProjeto().getOrcamento().iterator();
				recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());
			}
			//evitar erro de lazy na atualização do projeto
			for (ComponenteCurricularMonitoria comp : obj.getComponentesCurriculares()) {
				comp.getDocentesComponentesValidos().iterator();
			}
			//evitar erro de lazy na atualização do projeto
			if(obj.getProjeto().getCoordenador() == null)
				obj.getProjeto().setCoordenador(new MembroProjeto());
			
			setOperacaoAtiva(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA.getId());
			setConfirmButton("Alterar");

		} catch (ArqException e) {
			notifyError(e);
		}
		return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_DIMENSAO_PROJETO);
	}
	
	/** 
	 * Inicia a operação de remoção de dados cadastrais.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/lista.jsp</li>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/meus_projetos.jsp</li> 
	 * </ul>
	 */
	@Override
	public String preRemover() {
		try {
			setId();
			this.obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
			// evitar erro de lazy
			obj.getHistoricoSituacao().iterator();
			obj.getComponentesCurriculares().iterator();
			for (ComponenteCurricularMonitoria comp : obj.getComponentesCurriculares()) {
				comp.getDocentesComponentes().iterator();
			}
			setReadOnly(true);
			setConfirmButton("Remover");
			prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);			
			return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_RESUMO);
		} catch (Exception e) {
			notifyError(e);
			return null;
		}
	}

	/**
	 * Muda o projeto do Mbean
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void changeProjetoMonitoria(ValueChangeEvent evt) throws DAOException {
			Integer idProjeto = 0;
		try {
			idProjeto = new Integer(evt.getNewValue().toString());
			if ((idProjeto != null) && (idProjeto != 0)) {
				ProjetoMonitoriaDao daoPM = getDAO(ProjetoMonitoriaDao.class);
				obj = daoPM.findByPrimaryKey(idProjeto, ProjetoEnsino.class);
				obj.getAvaliacoes().iterator();
			}
		} catch (Exception e) {
			notifyError(e);
		}
	}

	/**
	 * Lista de todos os projetosLocalizados no combo
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * </ul>
	 * 
	 * @return Coleção de SelectItem usado em selects html
	 */
	public Collection<SelectItem> getProjetosLocalizadosCombo() {
		List<ProjetoEnsino> lista = new ArrayList<ProjetoEnsino>();
		if (projetosLocalizados != null) {
			lista = new ArrayList<ProjetoEnsino>(projetosLocalizados);
		}
		return toSelectItems(lista, "id", "titulo");
	}

	/**
	 * Carrega o projeto do BD
	 * 
	 * @param id
	 * @throws DAOException 
	 */
	private void loadObj(int id) throws DAOException {
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
		EquipeDocenteDao daoEquipe = getDAO(EquipeDocenteDao.class);
		clear();
		try {
			if (isEmpty(id)) {
				addMensagemErro("Projeto de não localizado.");
			}
			
			obj = dao.findByProjeto(id);
			
			if (obj==null)
				obj = dao.findByExactField(ProjetoEnsino.class, "projeto.id", id, true);
			
			Collection<ComponenteCurricularMonitoria> componentes = dao.findAtivosByExactField(ComponenteCurricularMonitoria.class, "projetoEnsino.id", id);
			Collection<AutorizacaoProjetoMonitoria> autorizacoes = dao.findAtivosByExactField(AutorizacaoProjetoMonitoria.class, "projetoEnsino.id", id);

			//Preenchendo componentes do projeto
			if ( obj.getComponentesCurriculares() != null )
				obj.getComponentesCurriculares().addAll(componentes);
			else if ( obj.getComponentesCurriculares() == null && componentes != null ) {
				obj.setComponentesCurriculares( new HashSet<ComponenteCurricularMonitoria>() );
				obj.getComponentesCurriculares().addAll(componentes);
			}
			//Preenchendo autorizações do projeto
			obj.setAutorizacoesProjetoColecao(autorizacoes);
			//Preenchendo equipe docente
			obj.setEquipeDocentes(daoEquipe.findByEquipeDocente(obj.getId()));
			//Discente envolvidos no projeto
			String [] parametros = {"projetoEnsino.id", "situacaoDiscenteMonitoria"};
			Object [] valoresAssumiu = { obj.getId(), SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA };
			obj.setDiscentesMonitoria(new ArrayList<DiscenteMonitoria>());
			obj.getDiscentesMonitoria().addAll(dao.findByExactField(DiscenteMonitoria.class, parametros, valoresAssumiu));
			Object [] valoresFinalizada = { obj.getId(), SituacaoDiscenteMonitoria.MONITORIA_FINALIZADA };
			obj.getDiscentesMonitoria().addAll(dao.findByExactField(DiscenteMonitoria.class, parametros, valoresFinalizada));
			
		}finally{
			dao.close();
			daoEquipe.close();
		}
		
	}

	
	/**
	 * Carrega todas as avaliações do projeto de monitoria atual
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * 		<li>/sigaa.war/monitoria/VisualizarAvaliacoes/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public Collection<AvaliacaoMonitoria> getAvaliacoesProjeto() throws DAOException {
		if ((obj != null) && (obj.getId() > 0)) {
			loadObj(obj.getId());
			obj.getAvaliacoes().iterator();
			return obj.getAvaliacoes();
		} else {
			return new ArrayList<AvaliacaoMonitoria>();
		}
	}

	/**
	 * Permite visualização da avaliação do projeto de monitoria selecionado
	 * pelos membro da equipe
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/VisualizarAvaliacoes/lista_projetos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String viewAvaliacoes() {
	    try {
	    	int id = getParameterInt("id",0);
	    	obj = getGenericDAO().findByPrimaryKey(id, ProjetoEnsino.class);
	    	obj.getAvaliacoes().iterator();	    	
			return forward(ConstantesNavegacaoMonitoria.VISUALIZARAVALIACOES_LISTA);
		} catch (DAOException e) {
			notifyError(e);
			addMensagem(e.getMessage());
		}
	    return null;
	}

	/**
	 * Visualizar o resumo do projeto de monitoria
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/DetalhesSelecaoExtensao/lista_atividades.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AlterarComponentesObrigatorios/lista_projetos.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AlterarSituacaoProjeto/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AutorizacaoDepartamento/form_busca_autorizacoes.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AutorizacaoDepartamento/form_busca_autorizacoes.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AutorizacaoReconsideracao/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AvaliacaoMonitoria/discrepancia_lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AvaliacaoMonitoria/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AvaliacaoMonitoria/projetos.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AvaliacaoRelatorioProjeto/form.jsp</li>
	 * 		<li>/sigaa.war/monitoria/CadastrarAvisoProjeto/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/CadastrarEquipeDocente/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/CadastrarMonitor/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/DetalhesSelecaoMonitoria/lista_projetos.jsp</li>
	 * 		<li>/sigaa.war/monitoria/DiscenteMonitoria/inscricao_discente.jsp</li>
	 * 		<li>/sigaa.war/monitoria/DiscenteMonitoria/lista_projetos.jsp</li>
	 * 		<li>/sigaa.war/monitoria/DiscenteMonitoria/meus_projetos.jsp</li>
	 * 		<li>/sigaa.war/monitoria/DistribuicaoProjeto/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/DistribuicaoRelatorioProjeto/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/aviso_envio.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_projeto.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/detalhamento_quantitativo.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/meus_projetos.jsp</li>
	 * 		<li>/sigaa.war/monitoria/RelatorioProjetoMonitoria/busca.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ResumoSid/busca.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ResumoSid/form_avaliar.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ResumoSid/lista_avaliar.jsp</li>
	 * 		<li>/sigaa.war/monitoria/SelecaoCoordenador/consultar_projeto.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ValidaSelecaoMonitor/provas.jsp</li>
	 * 		<li>/sigaa.war/public/departamento/monitoria.jsp</li>
	 * 		<li>/sigaa.war/public/docente/monitoria.jsp</li>
	 * <ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {
	    loadObj(getParameterInt("id", 0));
	    return viewObj();
	}

	/**
	 * Exibe página com dados do projeto atual (obj).
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private String viewObj() throws HibernateException, DAOException {
		if (!isEmpty(obj)) {
			// evitar erro de lazy
			obj.getEquipeDocentes().iterator();
			for (ComponenteCurricularMonitoria comp : obj.getComponentesCurriculares()) {
				comp.getDocentesComponentesValidos().iterator();
			}
			HistoricoSituacaoProjetoDao dao = getDAO(HistoricoSituacaoProjetoDao.class);
		    obj.getProjeto().setHistoricoSituacao(dao.relatorioHistoricoByIdProjeto(obj.getProjeto().getId()));
			
			Collection<ArquivoProjeto> arquivos = dao.findAtivosByExactField(ArquivoProjeto.class, "projeto.id", obj.getProjeto().getId());
			Collection<OrcamentoDetalhado> orcamentos = dao.findAtivosByExactField(OrcamentoDetalhado.class, "projeto.id", obj.getProjeto().getId());
			obj.setArquivos(arquivos);
			obj.setOrcamentosDetalhados(orcamentos);
			recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());

			setMostarDocentes(isExibirDocentes());
			return forward(ConstantesNavegacaoMonitoria.CADASTROPROJETO_VISUALIZAR);
		} else {
			addMensagemErro("Projeto não localizado.");
			return null;
		}
	}

	/**
	 * Responsável por exibir dados do projeto de monitoria associado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/ProjetoBase/resumo_integrado.jsp</li> 
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String viewMonitoriaAssociado() throws DAOException {
		int idProjetoBase = getParameterInt("id", 0);

		if (idProjetoBase == 0) {
			addMensagemErro("Projeto de não localizado.");
			return null;
		}

		try {
			ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
			obj = dao.findByProjetoBaseAndTipo(idProjetoBase,
					TipoProjetoEnsino.PROJETO_DE_MONITORIA);
			return viewObj();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao Carregar projeto.");
			return null;
		}
	}

	/**
	 * Responsável por exibir dados do projeto de melhoria da qualidade do ensino associado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/projetos/ProjetoBase/resumo_integrado.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String viewMelhoriaAssociado() throws DAOException {
		int idProjetoBase = getParameterInt("id", 0);

		if (idProjetoBase == 0) {
			addMensagemErro("Projeto de não localizado.");
			return null;
		}

		try {
			ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
			obj = dao.findByProjetoBaseAndTipo(idProjetoBase,
					TipoProjetoEnsino.PROJETO_PAMQEG);
			return viewObj();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro ao Carregar projeto.");
			return null;
		}
	}
	
	/**
	 * Verifica se o usuário atual pode visualizar os docentes que fazem parte do
	 * projeto de monitoria.
	 * Membros da comissão de avaliação não podem visualizar os nomes do professores
	 * que fazem parte do projeto.
	 * 
	 */
	private boolean isExibirDocentes() {
		// por padrão mostra os docentes
		boolean result = true;

		// se for da comissão esconde os docentes, mas se for da prograd(Pró-Reitoria de Graduação) também
		// então pode ver
		if ((getAcessoMenu() != null)
				&& ((getAcessoMenu().isComissaoMonitoria()) || (getAcessoMenu()
						.isComissaoCientificaMonitoria()))
				&& (!getAcessoMenu().isMonitoria())) { // membro da prograd
			result = false;
		}

		// verifica se o usuário atual participa do projeto
		for (EquipeDocente eq : obj.getEquipeDocentes()) {
			if ((eq.getServidor() != null) && (eq.getServidor().getPessoa() != null)
					&& (getUsuarioLogado() != null)
					&& (eq.getServidor().getPessoa().getId() == getUsuarioLogado()
							.getPessoa().getId())) {
				result = true;
				break;
			}
		}
		return result;
	}
	
	/**
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/monitoria/form_busca_projeto.jsp<li>
	 *  <li>/sigaa.war/monitoria/AlterarSituacaoProjeto/form.jsp</li>
	 *  <li>/sigaa.war/monitoria/AlterarComponentesObrigatorios/form.jsp</li>
	 *  <li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 *  <li>/sigaa.war/monitoria/SelecaoCoordenador/selecao_coordenador.jsp</li>
	 *  <li>/sigaa.war/monitoria/ProjetoMonitoria/anexar_fotos.jsp</li>
	 *  <li>/sigaa.war/monitoria/ProjetoMonitoria/aviso_envio.jsp</li>
	 *  <li>/sigaa.war/monitoria/ProjetoMonitoria/componente_curricular.jsp</li>
	 *  <li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 *  <li>/sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 *  <li>/sigaa.war/monitoria/ProjetoMonitoria/resumo.jsp</li>
	 *  <li>/sigaa.war/monitoria/ProjetoMonitoria/selecao_docente.jsp</li>
	 * </ul>
	 */
	public String cancelar() {
		// Removendo fotos adicionadas no servidor de arquivos
		// para o caso de projetos cancelados e transients
		if (obj.getId() == 0) {
			for (FotoProjeto foto : obj.getFotos()) {
				EnvioArquivoHelper.removeArquivo(foto.getIdFotoOriginal());
				EnvioArquivoHelper.removeArquivo(foto.getIdFotoMini());
			}
			for (ArquivoProjeto arq : obj.getArquivos()) {
				EnvioArquivoHelper.removeArquivo(arq.getIdArquivo());
			}
		}

		setOperacaoAtiva(null);
		return super.cancelar();
	}
	
	/**
	 * Inicia o procedimento de alterar componentes obrigatórios do projeto pela
	 * prograd(Pró-Reitoria de Graduação).
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/AlterarComponentesObrigatorios/lista_projetos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlterarComponentesObrigatorios() throws ArqException {
	    checkRole(SigaaPapeis.GESTOR_MONITORIA);

	    loadObj(getParameterInt("id",0));
	    setConfirmButton("Confirmar");
	    prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
	    return forward(ConstantesNavegacaoMonitoria.ALTERARCOMPONENTESOBRIGATORIOS_FORM);
	}

	/**
	 * Chama o processador pra realizar a alteração efetiva dos componentes
	 * obrigatórios do projeto pela prograd(Pró-Reitoria de Graduação).
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/AlterarComponentesObrigatorios/form.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AlterarComponentesObrigatorios/lista_projetos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String alterarComponentesObrigatorios() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		try {
			// chamar processador
			ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
			mov.setAcao(ProjetoMonitoriaMov.ACAO_ALTERAR_COMPONENTES_OBRIGATORIOS);
			mov.setObjMovimentado(obj);

			obj = (ProjetoEnsino) execute(mov, getCurrentRequest());
			addMensagemInformation("Alteração de componentes obrigatórios do projeto foi realizada com sucesso.");

		} catch (NegocioException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return forward(getFormPage());
		}
		return cancelar();
	}

	/**
	 * Inicia o procedimento de alterar somente a situação do projeto pela
	 * prograd(Pró-Reitoria de Graduação).
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/AlterarSituacaoProjeto/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlterarSituacaoProjeto() throws ArqException {
	    checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_INTEGRADO);

	    prepareMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
	    loadObj(getParameterInt("id",0));
	    setConfirmButton("Confirmar");
	    return forward(ConstantesNavegacaoMonitoria.ALTERARSITUACAOPROJETO_FORM);
	}

	/**
	 * Chama o processador pra realizar a alteração efetiva dos componentes
	 * obrigatórios do projeto pela prograd(Pró-Reitoria de Graduação).
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/AlterarSituacaoProjeto/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String alterarSituacaoProjeto() throws SegurancaException {
	    checkRole(SigaaPapeis.GESTOR_MONITORIA, SigaaPapeis.MEMBRO_COMITE_INTEGRADO);

		// chamar processador
		ProjetoMonitoriaMov mov = new ProjetoMonitoriaMov();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_PROJETO_MONITORIA);
		mov.setAcao(ProjetoMonitoriaMov.ACAO_ALTERAR_SITUACAO_PROJETO);
		mov.setObjMovimentado(obj);
		try {
			obj = (ProjetoEnsino) execute(mov, getCurrentRequest());
			addMensagemInformation("Alteração da situação do projeto foi realizada com sucesso.");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}

		return cancelar();
	}

	/**
	 * Relatório sintético dos projetos de monitoria
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarLocalizacaoRelatorioGeral() {
	    resetBean();
	    if (projetosLocalizados != null) {
		projetosLocalizados.clear();
	    }
	    return forward(ConstantesNavegacaoMonitoria.RELATORIO_QUADRO_GERAL_PROJETOSA_FORM);
	}
	
	/**
	 * Inicia o relatório com discentes ativos ou inativos pertencentes a um projeto de monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>/sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String iniciarRelatorioProjetosAtivosComMonitoresAtivosInativos() throws DAOException {	
		return forward(ConstantesNavegacaoMonitoria.RELATORIO_PROJETOS_ATIVOS_COM_MONITORES_ATIVOS_INATIVOS);	
	}
	
	/**
	 * Relatório com discentes ativos ou inativos pertencentes a um projeto de monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  	<li>sigaa.war/monitoria/Relatorios/projetos_monitores_ativos_inativos_rel.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String relatorioProjetosAtivosComMonitoresAtivosInativos() throws DAOException {		
		
		
		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
		try{
			if(buscaAnoProjeto == null){
				addMensagemErro("Ano do Projeto: Campo Obrigatório não informado.");
				return null;
			}
			
			projetosLocalizados = dao.relatorioProjetosAtivosComMonitoresAtivosInativos(buscaAnoProjeto);
			
			if(projetosLocalizados.isEmpty()) {
				addMensagemWarning("Não há projetos ativos com monitores ativos ou inativos para o critério de busca informado.");			
			}
			return null;
		}finally{
			dao.close();
		}
	}

	
	
	/**
	 * Busca projetos de monitoria de acordo com parâmetro de jsp específica
	 * Exibe o resultado em forma de relatório para impressão
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String localizarRelatorioGeral() throws DAOException {
		localizar();

		if (isCheckGerarRelatorio()) {
			return forward(ConstantesNavegacaoMonitoria.RELATORIO_QUADRO_GERAL_PROJETOSA_REL);
		} else {
			return null;
		}
	}
	
	/**
	 * Remove a foto da lista de anexos do projeto. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/anexar_fotos.jsp</li>
	 * </ul>
	 * 
	 * @return Retorna para mesma página permitindo que nova foto seja removida.
	 * @throws ArqException Gerada por {@link EnvioArquivoHelper#removeArquivo(int)}
	 */
	public String removeFoto() throws ArqException {

		FotoProjeto fotoRemovida = new FotoProjeto();
		fotoRemovida.setIdFotoOriginal(Integer.parseInt(getParameter("idFotoOriginal")));
		fotoRemovida.setIdFotoMini(Integer.parseInt(getParameter("idFotoMini")));
		fotoRemovida.setId(Integer.parseInt(getParameter("idFotoProjeto")));

		// Remove o arquivo do banco de arquivos (cluster)
		EnvioArquivoHelper.removeArquivo(fotoRemovida.getIdFotoOriginal());
		EnvioArquivoHelper.removeArquivo(fotoRemovida.getIdFotoMini());

		// Remove da view
		if ((obj.getFotos() != null) && (!obj.getFotos().isEmpty())) {
			for (FotoProjeto foto : obj.getFotos()) {
				if (foto.equals(fotoRemovida)) {
					foto.setAtivo(false);
				}
			}
			obj.getFotos().remove(fotoRemovida);
		}

		if ((getParameter("idFotoProjeto") != null)
				&& (getParameterInt("idFotoProjeto") != 0)) {
			prepareMovimento(ArqListaComando.DESATIVAR);
			MovimentoCadastro mov = new MovimentoCadastro(obj,
					ArqListaComando.DESATIVAR);
			mov.setObjMovimentado(fotoRemovida);
			try {
				execute(mov, getCurrentRequest());
			} catch (NegocioException e) {
				notifyError(e);
				return null;
			}
		}

		addMensagemInformation("Foto Removida com Sucesso!");
		return null;
	}

	
	/**
	 * Adiciona uma foto ao Projeto sem gravar no banco
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/anexar_fotos.jsp</li>
	 * </ul>
	 * 
	 * @return Retorna para mesma página permitindo a inclusão de nova foto.
	 */
	public String anexarFoto() {

		try {

			if ((descricaoFoto == null) || ("".equals(descricaoFoto.trim()))) {
				addMensagemErro("Descrição: Campo obrigatório não informado.");
				return null;
			}
			if ((foto == null) || (foto.getBytes() == null)) {
				addMensagemErro("Informe o nome completo do arquivo de foto.");
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
			novaFoto.setProjeto(obj.getProjeto());
			novaFoto.setAtivo(true);
			
			obj.getProjeto().getId();
			
			obj.getProjeto().addFoto(novaFoto);
			
			addMensagemInformation("Foto Anexada com Sucesso!");
			descricaoFoto = new String();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Tipo de arquivo não compatível.");
		}

		return null;
	}
	
	/**
	 * Retorna lista de docentes da equipe.
	 * Exibe a lista de possíveis orientadores de monitoria.
	 * Somente docentes que fazem parte da equipe podem ser orientadores.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/ProjetoMonitoria/selecao_docente.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<SelectItem> getSelectItemsPossiveisOrientadores() {
		
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		for (MembroProjeto mp : obj.getProjeto().getEquipe()) {
			// Somente docentes que fazem parte da equipe podem ser
			// orientadores.
			if (mp.isCategoriaDocente()) {
				String nomeDocente = mp.getServidor().getNome();
				if (mp.isCoordenador()) {
					nomeDocente += " (COORDENADOR(A))";
				}
				SelectItem item = new SelectItem(mp.getServidor().getId(), nomeDocente);
				lista.add(item);

				// definindo um docente padrão (radio marcado na view)
				if (equipeDocente.getServidor().getId() == 0) {
					equipeDocente.getServidor().setId(mp.getServidor().getId());
				}
			}
		}
		return lista;
	}

	/**
	 * Método utilizado para recuperar o diretório padrão da página.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Método não invocado por JSP´s</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getDirBase() 
	 */
	@Override
	public String getDirBase() {
		return "/monitoria/ProjetoMonitoria";
	}

	/** 
	 * Retorna a equipe de docente da monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/selecao_coordenador.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/selecao_docente.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoCoordenador/selecao_coordenador.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public EquipeDocente getEquipeDocente() {
		return equipeDocente;
	}

	/** 
	 * Seta a equipe de docente da monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param equipeDocente
	 */
	public void setEquipeDocente(EquipeDocente equipeDocente) {
		this.equipeDocente = equipeDocente;
	}

	/** 
	 * Retorna o componente curricular da monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul> 
	 * 
	 * @return
	 */
	public ComponenteCurricularMonitoria getComponente() {
		return componente;
	}

	/** 
	 * Seta o componente curricular da monitoria.
	 * <br>
	 * Metodo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param componente
	 */
	public void setComponente(ComponenteCurricularMonitoria componente) {
		this.componente = componente;
	}

	/** 
	 * Retorna a lista de componentes curriculares selecionados. 
	 * <br>
	 * Método chamado pela(s) seguintes(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 */
	public ArrayList<ComponenteCurricularMonitoria> getComponentesSelecionados() {
		return componentesSelecionados;
	}

	/** 
	 * Seta a lista de componentes curriculares selecionados.
	 * <br>
	 * Método chamado pela(s) seguintes(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param componentesSelecionados
	 */
	public void setComponentesSelecionados(ArrayList<ComponenteCurricularMonitoria> componentesSelecionados) {
		this.componentesSelecionados = componentesSelecionados;
	}

	/** 
	 * Retorna a equipe de docente da monitoria. 
	 * <br>
	 * Método chamado pela(s) seguintes(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * @return
	 */
	public EquipeDocente getEquipeDocenteSelecionado() {
		return equipeDocente;
	}

	/**
	 * Retorna o nome do projeto as ser filtrado na busca. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String getBuscaNomeProjeto() {
		return buscaNomeProjeto;
	}

	/** 
	 * Seta o nome do projeto as ser filtrado na busca.
	 * <br>
	 * Método chamado pela(s) seguintes(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * @param buscaNomeProjeto
	 */
	public void setBuscaNomeProjeto(String buscaNomeProjeto) {
		this.buscaNomeProjeto = buscaNomeProjeto;
	}

	/** 
	 * Retorna a coleção de projetos encontrados. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/AlterarComponentesObrigatorios/lista_projetos.jsp</li>
	 * 		<li>/sigaa.war/monitoria/AlterarSituacaoProjeto/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/CadastrarEquipeDocente/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/CadastrarMonitor/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/DistribuicaoProjeto/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/MovimentacaoCota/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_projeto.jsp</li>
	 * 		<li>/sigaa.war/monitoria/Relatorios/projetos_monitores_ativos_rel.jsp</li>
	 * 		<li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * 		<li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_rel.jsp</li>
	 * 		<li>/sigaa.war/monitoria/SelecaoCoordenador/consultar_projeto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<ProjetoEnsino> getProjetosLocalizados() {
		return projetosLocalizados;
	}

	/** Seta a coleção de projetos encontrados.
	 * @param projetosLocalizados
	 */
	public void setProjetosLocalizados(Collection<ProjetoEnsino> projetosLocalizados) {
		this.projetosLocalizados = projetosLocalizados;
	}

	/** 
	 * Retorna o ano a ser filtrado na busca. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * 		<li>/sigaa.war/monitoria/DistribuicaoRelatorioProjeto/lista.jsp</li>
	 * 		<li>/sigaa.war/monitoria/RelatorioProjetoMonitoria/busca.jsp</li>
	 * 		<li>/sigaa.war/monitoria/Relatorios/projetos_monitores_ativos_inativos_rel.jsp</li>
	 * 		<li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Integer getBuscaAnoProjeto() {
		return buscaAnoProjeto;
	}

	/** 
	 * Seta o ano a ser filtrado na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param buscaAnoProjeto
	 */
	public void setBuscaAnoProjeto(Integer buscaAnoProjeto) {
		this.buscaAnoProjeto = buscaAnoProjeto;
	}

	/** 
	 * Retorna os tipos de situações de projeto, utilizados na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>		
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * 		<li>/sigaa.war/monitoria/Relatorios/quadro_busca_projetos_form.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public TipoSituacaoProjeto getBuscaSituacaoProjeto() {
		return buscaSituacaoProjeto;
	}

	/** 
	 * Seta os tipos de situações de projeto, utilizados na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param buscaSituacaoProjeto
	 */
	public void setBuscaSituacaoProjeto(TipoSituacaoProjeto buscaSituacaoProjeto) {
		this.buscaSituacaoProjeto = buscaSituacaoProjeto;
	}

	/** 
	 * Indica se a Busca é filtrada por título. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isCheckBuscaTitulo() {
		return checkBuscaTitulo;
	}

	/** 
	 * Seta  se a Busca é filtrada por título. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param checkBuscaTitulo
	 */
	public void setCheckBuscaTitulo(boolean checkBuscaTitulo) {
		this.checkBuscaTitulo = checkBuscaTitulo;
	}

	/** 
	 * Indica se a busca é filtrada por ano. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * 		<li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	/** 
	 * Seta se a busca é filtrada por ano.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param checkBuscaAno
	 */
	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	/** 
	 * Indica se a Busca é filtrada por componente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 *  
	 * @return
	 */
	public boolean isCheckBuscaComponente() {
		return checkBuscaComponente;
	}

	/** 
	 * Seta se a Busca é filtrada por componente. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param checkBuscaComponente
	 */
	public void setCheckBuscaComponente(boolean checkBuscaComponente) {
		this.checkBuscaComponente = checkBuscaComponente;
	}

	/** 
	 * Indica se a Busca é filtrada por situação. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * 		<li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * </ul>
	 * @return 
	 */
	public boolean isCheckBuscaSituacao() {
		return checkBuscaSituacao;
	}

	/** 
	 * Seta se a Busca é filtrada por situação.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param checkBuscaSituacao
	 */
	public void setCheckBuscaSituacao(boolean checkBuscaSituacao) {
		this.checkBuscaSituacao = checkBuscaSituacao;
	}

	public void setCheckBuscaSemRelatorio(boolean checkBuscaSemRelatorio) {
		this.checkBuscaSemRelatorio = checkBuscaSemRelatorio;
	}

	public boolean isCheckBuscaSemRelatorio() {
		return checkBuscaSemRelatorio;
	}

	/** 
	 * Retorna a lista de componentes curriculares a remover.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 */
	public ArrayList<ComponenteCurricularMonitoria> getComponentesRemovidos() {
		return componentesRemovidos;
	}

	/** 
	 * Seta a lista de componentes curriculares a remover.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param componentesRemovidos
	 */
	public void setComponentesRemovidos(ArrayList<ComponenteCurricularMonitoria> componentesRemovidos) {
		this.componentesRemovidos = componentesRemovidos;
	}

	/** 
	 * Retorna a lista de docentes a remover dos componentes curriculares. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 */
	public ArrayList<EquipeDocenteComponente> getDocentesComponentesRemovidos() {
		return docentesComponentesRemovidos;
	}

	/** 
	 * Seta a lista de docentes a remover dos componentes curriculares.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param docentesComponentesRemovidos
	 */
	public void setDocentesComponentesRemovidos(ArrayList<EquipeDocenteComponente> docentesComponentesRemovidos) {
		this.docentesComponentesRemovidos = docentesComponentesRemovidos;
	}

	/** 
	 * Indica se mostra os docentes.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/view.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isMostarDocentes() {
		return mostarDocentes;
	}

	/** 
	 * Seta se mostra os docentes. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param mostarDocentes
	 */
	public void setMostarDocentes(boolean mostarDocentes) {
		this.mostarDocentes = mostarDocentes;
	}

	/** 
	 * Retorna o Edital utilizado na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * 		<li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public EditalMonitoria getEdital() {
		return edital;
	}

	/** 
	 * Seta o Edital utilizado na busca. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param edital
	 */
	public void setEdital(EditalMonitoria edital) {
		this.edital = edital;
	}

	/** 
	 * Retorna o servidor utilizado na busca. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Servidor getServidor() {
		return servidor;
	}

	/** 
	 * Seta o servidor utilizado na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param servidor
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	/** 
	 * Indica se a Busca é filtrada por Edital.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitora/form_busca_projeto.jsp</li>
	 * 		<li>/sigaa.war/monitora/ProjetoMonitoria/consultar_avaliadores.jsp</li>
	 * 		<li>/sigaa.war/monitora/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * </ul> 
	 * @return
	 */
	public boolean isCheckBuscaEdital() {
		return checkBuscaEdital;
	}

	/** 
	 * Seta se a Busca é filtrada por Edital.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param checkBuscaEdital
	 */
	public void setCheckBuscaEdital(boolean checkBuscaEdital) {
		this.checkBuscaEdital = checkBuscaEdital;
	}

	/** 
	 * Indica se a Busca é filtrada por servidor.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * <ul>
	 * @return
	 */
	public boolean isCheckBuscaServidor() {
		return checkBuscaServidor;
	}

	/** 
	 * Seta se a Busca é filtrada por servidor.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * @param checkBuscaServidor
	 */
	public void setCheckBuscaServidor(boolean checkBuscaServidor) {
		this.checkBuscaServidor = checkBuscaServidor;
	}

	/** 
	 * Indica se a Busca é filtrada por usuário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul>
	 * @return
	 */
	public boolean isCheckBuscaUsuario() {
		return checkBuscaUsuario;
	}

	/** 
	 * Seta se a Busca é filtrada por usuário.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * @param checkBuscaUsuario
	 */
	public void setCheckBuscaUsuario(boolean checkBuscaUsuario) {
		this.checkBuscaUsuario = checkBuscaUsuario;
	}

	/** 
	 * Retorna o usuário utilizado na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * <ul>
	 * 
	 * @return
	 */
	public Servidor getUsuarioBusca() {
		return usuarioBusca;
	}

	/** 
	 * Seta o usuário utilizado na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param usuarioBusca
	 */
	public void setUsuarioBusca(Servidor usuarioBusca) {
		this.usuarioBusca = usuarioBusca;
	}

	/** 
	 * Retorna o ID do novo coordenador do projeto.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return
	 */
	public int getIdNovoCoordenador() {
		return idNovoCoordenador;
	}

	/** 
	 * Seta o ID do novo coordenador do projeto. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param idNovoCoordenador
	 */
	public void setIdNovoCoordenador(int idNovoCoordenador) {
		this.idNovoCoordenador = idNovoCoordenador;
	}

	/** Indica se há seleção de coordenador.
	 * @return
	 */
	public boolean isSelecaoCoordenador() {
		return selecaoCoordenador;
	}


	/** 
	 * Seta se há seleção de coordenador.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 *  
	 * @param selecaoCoordenador
	 */
	public void setSelecaoCoordenador(boolean selecaoCoordenador) {
		this.selecaoCoordenador = selecaoCoordenador;
	}

	/** 
	 * Indica se o docente é novo. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @return the novoDocente
	 */
	public boolean isNovoDocente() {
		return novoDocente;
	}

	/** 
	 * Seta se o docente é novo. 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param novoDocente
	 */
	public void setNovoDocente(boolean novoDocente) {
		this.novoDocente = novoDocente;
	}


	/** 
	 * Indica se deve gerar um relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/Relatorios/quadro_geral_projetos_form.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public boolean isCheckGerarRelatorio() {
		return checkGerarRelatorio;
	}

	/** 
	 * Seta se deve gerar um relatório.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 *  
	 * @param checkGerarRelatorio
	 */
	public void setCheckGerarRelatorio(boolean checkGerarRelatorio) {
		this.checkGerarRelatorio = checkGerarRelatorio;
	}

	/** 
	 * Retorna os tipos de projetos de ensino.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 *   
	 * @return
	 */
	public TipoProjetoEnsino getTipoProjetoEnsino() {
		return tipoProjetoEnsino;
	}

	/** 
	 * Seta os tipos de projetos de ensino.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 *  
	 * @param tipoProjetoEnsino
	 */
	public void setTipoProjetoEnsino(TipoProjetoEnsino tipoProjetoEnsino) {
		this.tipoProjetoEnsino = tipoProjetoEnsino;
	}

	/** 
	 * Retorna o tipo de projeto utilizado na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul>
	 *   
	 * @return
	 */
	public TipoProjetoEnsino getBuscaTipoProjeto() {
		return buscaTipoProjeto;
	}

	/** 
	 * Seta o tipo de projeto utilizado na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 *  
	 * @param buscaTipoProjeto
	 */
	public void setBuscaTipoProjeto(TipoProjetoEnsino buscaTipoProjeto) {
		this.buscaTipoProjeto = buscaTipoProjeto;
	}

	/** 
	 * Indica se a Busca é filtrada por tipo de projeto.
	 *  <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public boolean isCheckBuscaTipoProjeto() {
		return checkBuscaTipoProjeto;
	}

	/** 
	 * Seta se a Busca é filtrada por tipo de projeto.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 *   
	 * @param checkBuscaTipoProjeto
	 */
	public void setCheckBuscaTipoProjeto(boolean checkBuscaTipoProjeto) {
		this.checkBuscaTipoProjeto = checkBuscaTipoProjeto;
	}

	/** 
	 * Retorna o centro utilizado na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/form_busca_projeto.jsp</li>
	 * </ul>
	 *    
	 * @return
	 */
	public Unidade getBuscaCentro() {
		return buscaCentro;
	}

	/** 
	 * Seta o centro utilizado na busca.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 *   
	 * @param buscaCentro
	 */
	public void setBuscaCentro(Unidade buscaCentro) {
		this.buscaCentro = buscaCentro;
	}

	/** 
	 * Indica se a Busca é filtrada por centro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitora/form_busca_projeto.jsp</li>
	 * </ul>
	 *    
	 * @return
	 */
	public boolean isCheckBuscaCentro() {
		return checkBuscaCentro;
	}

	/** 
	 * Seta se a Busca é filtrada por centro.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 *   
	 * @param checkBuscaCentro
	 */
	public void setCheckBuscaCentro(boolean checkBuscaCentro) {
		this.checkBuscaCentro = checkBuscaCentro;
	}
	
	/**
	 * Método utilizado para informar uma Collection de fotos do projeto
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/anexar_fotos.jsp</li>
	 * 		<li>/sigaa.war/monitoria/resumo.jsp</li>
	 * 		<li>/sigaa.war/monitoria/view.jsp</li>
	 * </ul>
	 *   
	 * @return
	 * @throws DAOException
	 */
	public Collection<FotoProjeto> getFotosProjeto() throws DAOException {			
	    return obj.getFotos();
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

	public ControleFluxo getControleFluxo() {
	    return controleFluxo;
	}

	public void setControleFluxo(ControleFluxo controleFluxo) {
	    this.controleFluxo = controleFluxo;
	}
	
	/** 
	 * Retorna o anexo ao projeto de monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 *  
	 * @return
	 */
	public UploadedFile getFile() {
		return file;
	}

	/** 
	 * Seta o anexo ao projeto de monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>Não é chamado por JSP(s)</li>
	 * </ul>
	 * 
	 * @param file
	 */
	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public void setTipoRelatorio(Integer tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Integer getTipoRelatorio() {
		return tipoRelatorio;
	}

	/**
	 * Verifica se o edital de um projeto ainda esta aberto.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isEditalDesteProjetoAindaAberto() throws DAOException {
	    EditalDao dao = getDAO(EditalDao.class);
	    Collection<Edital> editais = dao.findAbertos(Edital.MONITORIA, Edital.INOVACAO, Edital.ASSOCIADO, Edital.MONITORIA_EOU_INOVACAO);
	    dao.initialize(obj.getEditalMonitoria());
	    return obj.isPermitidoSelecionarEditaisEmAberto(editais);
	}

	public boolean isCheckBuscaProjetoAssociado() {
	    return checkBuscaProjetoAssociado;
	}

	public void setCheckBuscaProjetoAssociado(boolean checkBuscaProjetoAssociado) {
	    this.checkBuscaProjetoAssociado = checkBuscaProjetoAssociado;
	}

	public Boolean getBuscaProjetoAssociado() {
	    return buscaProjetoAssociado;
	}

	public void setBuscaProjetoAssociado(Boolean buscaProjetoAssociado) {
	    this.buscaProjetoAssociado = buscaProjetoAssociado;
	}

    /**
     * Adiciona um orçamento à lista.
     * <br />
     *  Método chamado pela(s) seguinte(s) JSP(s):
     *  <ul>
     *   <li>sigaa.war/monitoria/ProjetoMonitoria/orcamento.jsp</li>
     * </ul>
     * @return Retorna para mesma tela permitindo inclusão de novo elemento.
     * @throws SegurancaException 
     */
	public String adicionaOrcamento() throws SegurancaException {
    	checkChangeRole();
		try {
			ListaMensagens mensagens = new ListaMensagens();
			Integer idElementoDespesa = getParameterInt("idElementoDespesa", 0);
			orcamento.setElementoDespesa(getGenericDAO().findByPrimaryKey(idElementoDespesa, ElementoDespesa.class));
			orcamento.setAtivo(true);
			posicaoOrcamento++;
			orcamento.setPosicao(posicaoOrcamento);

			if ((idElementoDespesa == null) || (idElementoDespesa == 0)) {
				addMensagemErro("Elemento de Despesa é Obrigatório: Selecione um elemento de despesa");
				return null;
			}
			
			// Mantem o botão precionado
			getCurrentRequest().getSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
			
			if (orcamento.getValorUnitario() == null){
				addMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_ZERO, "Valor Unitário");
				return null;
			}

			ProjetoMonitoriaValidator.validaAdicionaOrcamento(obj, orcamento, mensagens);
			if (!mensagens.isEmpty()) {
				addMensagens(mensagens);
				return null;
			}

		} catch (Exception e) {
			addMensagemErro(e.getMessage());
		}

		obj.addOrcamentoDetalhado(orcamento);
		
		// Prepara para novo item do orçamento
		getCurrentSession().setAttribute("idElementoDespesa", orcamento.getElementoDespesa().getId());
		orcamento = new OrcamentoDetalhado();
		recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());

		return redirectMesmaPagina();
	}

    /**
     * Remove um orçamento da lista de orçamentos.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/monitoria/ProjetoMonitoria/orcamento.jsp</li>
     * </ul>
     * @return Retorna para mesma página permitindo remoção de novo elemento
     * @throws DAOException Gerado na busca de todos os itens do orçamento buscados no banco.
     * @throws SegurancaException 
     */
	public String removeOrcamento() throws DAOException, SegurancaException {
    	checkChangeRole();

		int id = getParameterInt("idOrcamentoDetalhado", 0);
		int posicao = getParameterInt("posicao", -1);

		orcamento = getGenericDAO().findByPrimaryKey(id, OrcamentoDetalhado.class);

		// Orçamento não localizado no banco
		if (orcamento == null) {
			OrcamentoDetalhado orcRem = null;
			Iterator<OrcamentoDetalhado> itrOrc = obj.getOrcamentosDetalhados().iterator();
			while (itrOrc.hasNext()){
				orcRem = itrOrc.next();
				if (orcRem.getPosicao() == posicao)
					itrOrc.remove();
			}
			recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());
			orcamento = new OrcamentoDetalhado();
		} else {

			remover(orcamento); // Remove do banco de dados
			obj.getOrcamentosDetalhados().remove(orcamento);
			orcamento = new OrcamentoDetalhado();
			recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());
		}

		return redirectMesmaPagina();

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
	    return proximoPasso();
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
     * Remove o objeto informado.
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
     * Adiciona um arquivo ao projeto.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/ProjetoMonitoria/anexar_arquivos.jsp</li> 
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

			if ((fileArquivo == null) || (fileArquivo.getBytes() == null)) {
				addMensagemErro("Arquivo: Campo obrigatório não informado.");
				return null;
			}

			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, fileArquivo.getBytes(), fileArquivo
					.getContentType(), fileArquivo.getName());
			ArquivoProjeto arquivo = new ArquivoProjeto();
			arquivo.setDescricao(descricaoArquivo);
			arquivo.setIdArquivo(idArquivo);
			arquivo.setAtivo(true);
			arquivo.setProjeto(obj.getProjeto());
			obj.getProjeto().getId();
			obj.addArquivo(arquivo);
			addMessage("Arquivo Anexado com Sucesso.", TipoMensagemUFRN.INFORMATION);
			descricaoArquivo = new String();

		} catch (Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
		}

		return null;
	}
	
    /**
     * Retorna a lista de todos os arquivos (documentos) anexados ao projeto
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/ProjetoMonitoria/anexar_arquivo.jsp</li> 
     * </ul>
     * @return Retorna lista de arquivos da ação atual.
     * @throws DAOException Gerado por {@link GenericDAOImpl} utilizado na busca.
     */
	public Collection<ArquivoProjeto> getArquivosProjeto() throws DAOException {
		return obj.getArquivos();
	}
	
    /**
     * Remove o arquivo da lista de anexos do projeto de monitoria.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     *  <li>sigaa.war/monitoria/ProjetoMonitoria/anexar_arquivos.jsp</li> 
     * </ul>
     * @return Retorna para mesma página permitindo a exclusão de novo arquivo.
     * @throws SegurancaException 
     */
	public String removeAnexo() throws SegurancaException {
    	checkChangeRole();
		ArquivoProjeto arquivo = new ArquivoProjeto();
		arquivo.setIdArquivo(getParameterInt("idArquivo", 0));
		arquivo.setId(getParameterInt("idArquivoProjeto", 0));
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
     * Segue para próxima tela do fluxo.
     *  <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul> 
     *  <li>sigaa.war/monitoria/ProjetoMonitoria/anexar_arquivos.jsp</li>
     * </ul>
     * @return Próxima tela do fluxo do cadastro da ação em questão.
     */
	public String submeterArquivos() {
		return proximoPasso();
	}
	
	/**
	 * Carrega as informações do edital selecionado na página
	 * <br>Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li></ul>
	 * @throws DAOException
	 */
	public void changeEdital() throws DAOException{
	    if(obj.getEditalMonitoria() != null && obj.getEditalMonitoria().getId() > 0){
		obj.setEditalMonitoria(getGenericDAO().findByPrimaryKey(obj.getEditalMonitoria().getId(), EditalMonitoria.class));
		obj.setAno(obj.getEditalMonitoria().getEdital().getAno());
		obj.setDataInicio(obj.getEditalMonitoria().getEdital().getInicioRealizacao());
		obj.setDataFim(obj.getEditalMonitoria().getEdital().getFimRealizacao());
	    }else{
		obj.setEditalMonitoria(new EditalMonitoria());
		obj.setAno(null);
		obj.setDataInicio(null);
		obj.setDataFim(null);
	    }
	}

	/**
	 * Método utilizado para informar os possíveis coordenadores do projeto de monitoria.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/monitoria/ProjetoMonitoria/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getPossiveisCoordenadores() throws ArqException{
		return toSelectItems(getEquipeDocentes(), "servidor.id", "servidor.nome");
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
    	}else if (ValidatorUtil.isNotEmpty(obj.getTipoEdital())) {
    		controleFluxo = new ControleFluxo(controleFluxo.PROJETO_MONITORIA);
    		return true;
    	}else {
    		addMensagemErro("Não foi possível identificar o Próximo Passo ou o Passo Anterior.");
    		return false;
    	}
    }

	public boolean isPodeEnviarProjeto() {
		return podeEnviarProjeto;
	}

	public void setPodeEnviarProjeto(boolean podeEnviarProjeto) {
		this.podeEnviarProjeto = podeEnviarProjeto;
	}

	public MembroProjeto getCoordenadorPro() {
		return coordenadorPro;
	}

	public void setCoordenadorPro(MembroProjeto coordenadorPro) {
		this.coordenadorPro = coordenadorPro;
	}

	public void setOrcamento(OrcamentoDetalhado orcamento) {
		this.orcamento = orcamento;
	}

	public OrcamentoDetalhado getOrcamento() {
		return orcamento;
	}

	public void setTabelaOrcamentaria(Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria) {
		this.tabelaOrcamentaria = tabelaOrcamentaria;
	}

	public Map<ElementoDespesa, ResumoOrcamentoDetalhado> getTabelaOrcamentaria() {
		return tabelaOrcamentaria;
	}

	public void setFileArquivo(UploadedFile fileArquivo) {
		this.fileArquivo = fileArquivo;
	}

	public UploadedFile getFileArquivo() {
		return fileArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}

	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public boolean isCheckPAMQEG() {
		return checkPAMQEG;
	}

	public void setCheckPAMQEG(boolean checkPAMQEG) {
		this.checkPAMQEG = checkPAMQEG;
	}

	public boolean isCheckMonitoria() {
		return checkMonitoria;
	}

	public void setCheckMonitoria(boolean checkMonitoria) {
		this.checkMonitoria = checkMonitoria;
	}

}
