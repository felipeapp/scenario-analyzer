/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/07/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.jsf.OperacoesCoordenadorGeralEadMBean;
import br.ufrn.sigaa.ensino.dao.ExpressaoComponenteCurriculoDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricularPrograma;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.EquivalenciaEspecifica;
import br.ufrn.sigaa.ensino.dominio.FormaParticipacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoAtividadeComplementar;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.jsf.FormaParticipacaoAtividadeMBean;
import br.ufrn.sigaa.ensino.negocio.ComponenteCurricularTotaisHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.dominio.ComponenteCurricularMov;
import br.ufrn.sigaa.jsf.UnidadeMBean;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean para o cadastro de {@link ComponenteCurricular componente curricular}.
 * 
 * @author André
 * @author Victor Hugo
 */
@Component("componenteCurricular")
@Scope("session")
public class ComponenteCurricularMBean extends	SigaaAbstractController<ComponenteCurricular> {

	/** Número máximo de resultados por consulta. */
	private static final int MAX_RESULTADOS = 2000;
	
	/** Valor da tipo de disciplina Regular*/
	private static final int TIPO_DISCIPLINA_REGULAR = -1;
	
	/** Define o link para o formulário de Sub-unidades de Blocos. */
	private static final String JSP_BLOCO = "/graduacao/componente/bloco.jsp";
	/** Define o link para o formulário de dados gerais do componente curricular. */
	private static final String JSP_TIPO_COMPONENTE = "/graduacao/componente/tipo_componente.jsp";
	/** Define o link para o formulário de dados gerais do componente curricular. */
	private static final String JSP_ATIVAR_DESATIVAR_COMPONENTE_DETALHES = "/graduacao/componente/ativar_desativar_componente_detalhes.jsp";
	/** Define o link para o formulário de dados gerais do componente curricular. */
	private static final String JSP_GERAL = "/graduacao/componente/geral.jsp";
	/** Define o link para o formulário de resumo dos dados do componente curricular. */
	private static final String JSP_RESUMO = "/graduacao/componente/resumo.jsf";
	/** Define o link para a lista de componentes curriculares. */
	private static final String JSP_LISTA = "/graduacao/componente/lista.jsf";
	/** Define o link para o comprovante de solicitação de cadastro de componente curricular.*/
	private static final String JSP_COMPROVANTE = "/graduacao/componente/comprovante.jsp";
	/** Define o link para o formulário de confirmação de cadastro do componente curricular. */
	private static final String JSP_POSCADASTRO = "/sigaa/graduacao/componente/poscadastro.jsf";
	/** Define o link para a manutenção dos históricos de equivalências do componente curricular. */
	private static final String JSP_MANUTENCAO_EQUIVALENCIA = "/graduacao/componente/manutencao_historico_equivalencias.jsf";
	/** Define o link para a busca geral de componente curricular. */
	private static final String JSP_BUSCA_GERAL = "/geral/componente_curricular/busca_geral.jsf";
	/** Define o link para o formulário de definição de subunidades da turma. */
	public static final String JSP_SUBUNIDADES = "/graduacao/solicitacao_turma/subunidades.jsp";
	/** Define o link para o menu de graduação. */
	public static final String VIEW_MENU_GRAD = "menuGraduacao";
	/** Constante referente ao cargo emitido no comprovante de solicitação de cadastro de componente*/
	private static final String CARGO = "Chefe de Departamento";
	/** Lista de componentes curriculares encontrados na busca por componentes curriculares. */
	private Collection<ComponenteCurricular> componentes = new HashSet<ComponenteCurricular>();
	/** Lista de componentes curriculares encontrados na busca por componentes curriculares. */
	private Collection<ComponenteDetalhes> listaDetalhes = new ArrayList<ComponenteDetalhes>();
	/** Lista de currículos ao qual o componente curricular pode pertencer. */
	private Collection<CurriculoComponente> curriculos = new ArrayList<CurriculoComponente>();
	/** Lista de todos componentes curriculares dos cursos de graduação. */
	private Collection<ComponenteCurricular> todosGraduacao;
	/** Lista de todas as equivalências específicas do objeto. */
	private Collection<EquivalenciaEspecifica> equivalenciasEspecificas;
	/** Atributo utilizado para exibir mensagem no suggestion box*/
	private String textoSuggestionBox;		
	/** Componente Curricular Utilizado na busca */
	private ComponenteCurricular componenteBusca;
	/** Indica que os códigos das subunidades do bloco serão definidos automaticamente. */
	private boolean defineCodigoSubUnidadeAutomaticamente;
	
	/**
	 * Componente curricular cadastrado utilizado para exibir as informações no
	 * comprovante após o cadastro, pois objeto já foi limpado.
	 */
	private ComponenteCurricular componente;

	/** Id do componente criado. */
	private int idComponente;

	/** Expressão que define o pré-requisito do componente curricular. */
	private String preRequisitoForm;
	/** Expressão que define o co-requisito do componente curricular. */
	private String coRequisitoForm;
	/** Expressão que define a equivalência do componente curricular. */
	private String equivalenciaForm;

	/** Indica que o usuário está alterando a subUnidade do componente curricular. */
	private boolean alterandoSubUnidade;

	/** SubUnidade do componente curricular. */
	private ComponenteCurricular subUnidade;

	/** Posição da subUnidade na lista de sub-unidades para alteração. */
	private Integer posSubUnidadeAlterada;

	// Campos utilizados na consulta
	/** Indica que a busca por componente deve ser filtrada por nível de ensino. */
	private boolean filtroNivel;
	/** Indica que a busca por componente deve ser filtrada por código do componente curricular. */
	private boolean filtroCodigo;
	/** Indica que a busca por componente deve ser filtrada por nome do componente curricular.*/
	private boolean filtroNome;
	/** Indica que a busca por componente deve ser filtrada por pré-requisito. */
	private boolean filtroPreRequisito;
	/** Indica que a busca por componente deve ser filtrada por co-requisito. */
	private boolean filtroCoRequisito;
	/** Indica que a busca por componente deve ser filtrada por equivalência. */
	private boolean filtroEquivalencia;
	/** Indica que a busca por componente deve ser filtrada por equivalência utilizando o período de equivalência.*/
	private boolean filtroPeriodoEquivalencia;
	/** Indica que a busca por componente deve ser filtrada por tipo de componente. */
	private boolean filtroTipo;
	/** Indica que a busca por componente deve ser filtrada por m de componente. */
	private boolean filtroModalidade;
	/** Indica se o usuário poderá selecionar a unidade. */
	private boolean selecionaUnidade;
	/** Indica que a busca por componente deve ser filtrada por unidade ao qual o componente curricular pertence.*/
	private boolean filtroUnidade;
	/** Indica que a busca deve listar o resultado na forma de relatório. */
	private boolean filtroRelatorio;
	/** Indica que a busca por componente deve restringir os componentes curriculares do EAD.*/
	private boolean ead;
	/** Lista de SelectItem de possíveis unidades que o usuário pode selecionar. */
	private Collection<SelectItem> possiveisUnidades;
	/** Lista de SelecItem de cursos que o usuário pode selecionar. */
	private Collection<SelectItem> cursos;
	/** SubUnidade a ser alterada. */
	private ComponenteCurricular subUnidadeAlterar;
	
	// atributos utilizados para busca de componentes curriculares por outros controllers (modo operador)
	
	/** Managed Bean que está buscando por componente curricular */
	private SeletorComponenteCurricular mBean;
	
	/**Opções usadas na busca de componente*/
	private OpcoesBuscaComponente opcoesBusca;
	
	/** Nome da operação (Para utilização no título da página de busca do componente curricular) */
	private String tituloOperacao;

	/** {@link TipoComponenteCurricular Tipos de componentes} válidos para a busca de componentes curriculares. */
	private int[] tiposValidos;
	
	/** Indica que o controller está buscando componentes curriculares para outros controllers. */
	private boolean modoOperador;
	
	/** Indica se o usuário poderá selecionar o modo de ensino no formulário de busca por componente. */
	private boolean selecionaNivelEnsino;
	
	/** Indica a data de início utilizado para a busca de componentes por equivalência. */
	private Date dataInicioEquivalencia;
	
	/** Indica a data de fim utilizado para a busca de componentes por equivalência. */
	private Date dataFimEquivalencia;
	
	/** Indica se o componente será impresso ou não. */
	private boolean reportImpressao = false;

	/**Armazena a coleção de subunidades da turma.*/ 
	private Collection<ComponenteCurricular> subunidades = new HashSet<ComponenteCurricular>();

	/** Sugere um código para o componente curricular baseado na unidade escolhida pelo usuário. */
	private StringBuilder codigoSugerido;

	/** Indica se o usuário tem permissão para alterar o tipo de componente curricular. */
	private Boolean naoPermiteAlterarTipoComponente;
	
	/** Construtor padrão. 
	 * @throws NegocioException */
	public ComponenteCurricularMBean()  {
		initObj();
	}

	/**
	 * Inicializa os campos do objeto que representa um componente para ser
	 * manipulado durante as operações.
	 * @throws NegocioException 
	 * @throws NegocioException 
	 */
	private void initObj() {
		obj = new ComponenteCurricular();
		
		obj.setPrograma(null);
		obj.setComponentesCursoLato(null);
		obj.setCurso(new Curso());
		obj.setDetalhes(new ComponenteDetalhes());
		obj.setConteudoVariavel(false);
		initSubUnidade();
		initFiltros();

		preRequisitoForm = new String();
		coRequisitoForm = new String();
		equivalenciaForm = new String();
		
		cursos = null;
		possiveisUnidades = null;
		
		// atributos utilizados na busca de componentes por outros mbeans
		modoOperador = false;
		selecionaNivelEnsino = true;
		selecionaUnidade = true;
		tiposValidos = null;
		mBean = null;
		tituloOperacao = null;
		
		defineCodigoSubUnidadeAutomaticamente = true;
		naoPermiteAlterarTipoComponente = null;
	}

	/** Reseta os filtros de busca.
	 * 
	 */
	private void limparBusca() {
		initFiltros();
		selecionaUnidade = true;
	}
	
	/**
	 * Determina se o componente trabalhado é do nível de
	 * pós-graduação stricto sensu.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/view_painel.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isStricto() {
		return NivelEnsino.isAlgumNivelStricto(obj.getNivel());
	}
	
	/**
	 * Determina se o componente trabalhado é do nível de
	 * graduação.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/view_painel.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isGraduacao() {
		return obj.getNivel() == NivelEnsino.GRADUACAO;
	}
	
	/**
	 * Determina se o componente trabalhado é do nível técnico.
	 * <br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isTecnico(){
		return obj.getNivel() == NivelEnsino.TECNICO;
	}

	/**
	 * Inicializa os campos do objeto que representa uma sub-unidade para ser
	 * manipulado durante as operações.
	 */
	private void initSubUnidade() {
		subUnidade = new ComponenteCurricular();
		subUnidade.setUnidade(obj.getUnidade());
		subUnidade.setNivel(obj.getNivel());
		subUnidade.setPrograma(null);
		subUnidade.setTipoAtividade(null);
		subUnidade.setTipoAtividadeComplementar(null);
		subUnidade.setComponentesCursoLato(null);
		subUnidade.setDetalhes(new ComponenteDetalhes());
	}

	/**
	 * Inicializa todos os filtros utilizados em consultas de componentes
	 * curriculares.
	 */
	private void initFiltros() {
		componenteBusca = new ComponenteCurricular();
		filtroCodigo = false;
		filtroNivel = false;
		filtroNome = false;
		filtroRelatorio = false;
		filtroTipo = false;
		filtroModalidade = false;
		filtroUnidade = false;
	}

	/**
	 * Cria uma coleção de tipos de componentes para utilização em formulários.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/ensino/aproveitamento/busca_componente.jsp</li>
	 * <li>/sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/lista.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/tipo_componente.jsp</li>
	 * <li>/sigaa.war/graduacao/componente_programa/busca_componente.jsp</li>
	 * <li>/sigaa.war/graduacao/curiculo/busca_componente.jsp</li>
	 * <li>/sigaa.war/graduacao/relatorios/form_relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public List<SelectItem> getTiposComponentes() throws DAOException {
		List<SelectItem> tiposAdicionados = new ArrayList<SelectItem>();
		if (!isEmpty(tiposValidos)) {
			for (int idTipo : tiposValidos) {
				switch (idTipo) {
				case TipoComponenteCurricular.DISCIPLINA: tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.DISCIPLINA, "DISCIPLINA")); break;
				case TipoComponenteCurricular.ATIVIDADE: tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.ATIVIDADE, "ATIVIDADE"));break;
				case TipoComponenteCurricular.MODULO: tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.MODULO, "MÓDULO"));break;
				case TipoComponenteCurricular.BLOCO: tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.BLOCO, "BLOCO"));break;				
				}
			}
		} else {
			if(getNivelEnsino() == NivelEnsino.TECNICO
					|| !NivelEnsino.isValido(getNivelEnsino())){
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.DISCIPLINA, "DISCIPLINA"));
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.ATIVIDADE, "ATIVIDADE"));
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.MODULO, "MÓDULO"));
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.BLOCO, "BLOCO"));
			} else if(getNivelEnsino() == NivelEnsino.RESIDENCIA){
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.ATIVIDADE, "ATIVIDADE"));
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.DISCIPLINA, "DISCIPLINA"));
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.MODULO, "MÓDULO"));
			} else if (!NivelEnsino.isValido(getNivelEnsino())) {
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.ATIVIDADE, "ATIVIDADE"));
			} else {
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.DISCIPLINA, "DISCIPLINA"));
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.ATIVIDADE, "ATIVIDADE"));
				tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.MODULO, "MÓDULO"));
				if (getNivelEnsino() == NivelEnsino.GRADUACAO
						|| !NivelEnsino.isValido(getNivelEnsino())) {
					tiposAdicionados.add(new SelectItem(TipoComponenteCurricular.BLOCO, "BLOCO"));					
				}
			}
		}
		
		
		//Garantir que não seja mais possível o cadastro de ComponenteCurricular com TipoComponenteCurricular intativo.
		//Impede também o cadastro de turmas de componentes curriculares de tipos inativos.
		Collection<TipoComponenteCurricular> tiposAtivos = getGenericDAO().findAllAtivos(TipoComponenteCurricular.class, "id");
		List<SelectItem> tiposRetornaveis = new ArrayList<SelectItem>();
		for(SelectItem  t : tiposAdicionados) {
			TipoComponenteCurricular tipoComponente = new TipoComponenteCurricular((Integer)t.getValue());
			if(tiposAtivos.contains(tipoComponente)) {
				tiposRetornaveis.add(t);
			}
		}		
		
		return tiposRetornaveis;
	}
	
	/** 
	 * Retorna uma coleção de selectItem das possíveis formas de participação de acordo com o tipo do componente curricular.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/tipo_componente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public Collection<SelectItem> getFormaParticipacaoCombo() throws ArqException{
		FormaParticipacaoAtividadeMBean mBean = getMBean("formaParticipacaoAtividade");
		if (obj.getTipoAtividade() == null || obj.getTipoAtividade().getId() == 0) {
			return new ArrayList<SelectItem>();
		} else if (obj.isEstagio()) {
			return mBean.getFormasEstagioCombo();
		} if (obj.isTrabalhoConclusaoCurso()) {
			return mBean.getFormasTCCCombo();
		}
		else {
			return mBean.getAllCombo();
		}
	}
	
	/**
	 * Retorna uma lista com todos os tipos de componentes, para utilização em
	 * formulários.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/public/componentes/busca_componente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<SelectItem> getAllTiposComponentes() {
		List<SelectItem> tipos = new ArrayList<SelectItem>();
		tipos.add(new SelectItem(TipoComponenteCurricular.DISCIPLINA,
				"DISCIPLINA"));
		tipos.add(new SelectItem(TipoComponenteCurricular.ATIVIDADE,
				"ATIVIDADE"));
		tipos.add(new SelectItem(TipoComponenteCurricular.BLOCO, "BLOCO"));
		tipos.add(new SelectItem(TipoComponenteCurricular.MODULO, "MÓDULO"));		
		return tipos;
	}
	
	/**
	 * Retorna uma lista com todos as modalidades, para utilização em
	 * formulários.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li><li>/sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public List<SelectItem> getAllModalidades() {
		List<SelectItem> modalidades = new ArrayList<SelectItem>();
		modalidades.add(new SelectItem(ModalidadeEducacao.A_DISTANCIA,"DISTANCIAL"));
		modalidades.add(new SelectItem(ModalidadeEducacao.PRESENCIAL,"PRESENCIAL"));		
		return modalidades;
	}

	/**
	 * Monta combo com os níveis de ensino.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/geral/curso/busca_geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public SelectItem [] getNiveis() {
		return NivelEnsino.getNiveisCombo();
	}

	/**
	 * Retorna um grupo de componentes de acordo com a paginação.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
	 * <li>/sigaa.war/geral/componente_curricular/relatorio.jsp</li>
	 * <li>/sigaa.war/public/componentes/lista.jsp</li>
	 * <li>/sigaa.war/public/componentes/busca_componente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<ComponenteCurricular> getComponentes() {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		if (componentes == null) {
			try {
				componentes = dao.findAll(ComponenteCurricular.class,
						getPaginacao());
			} catch (DAOException e) {
				notifyError(e);
				addMensagemErroPadrao();
			}
		}
		return componentes;
	}

	/**
	 * Executa a busca por componentes curriculares.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/lista.jsp</li>
	 * 		<li>/sigaa.war/public/componentes/busca_componentes.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {
		return buscarComponente();
	}

	/**
	 * Inicia os casos de uso que começam buscando um
	 * componente curricular.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/ead/menu.jsp</li>
	 * 		<li>/sigaa.war/graduacao/menus/consultas.jsp</li>
	 * 		<li>/sigaa.war/graduacao/menus/coordenacao.jsp</li>
	 * 		<li>/sigaa.war/graduacao/departamento.jsp</li>
	 * 		<li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * 		<li>/sigaa.war/lato/menu_coordenador.jsp</li>
	 * 		<li>/sigaa.war/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * 		<li>/sigaa.war/portais/cpolo/menu_cpolo.jsp</li>
	 * 		<li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * 		<li>/sigaa.war/portais/rh_plan/abas/graduacao.jsp</li>
	 * 		<li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException 
	 */
	public String popularBuscaGeral() throws NegocioException {
		preparaBuscaGeral();
		return forward(JSP_BUSCA_GERAL);
	}
	
	/**
	 * Inicia a busca de componentes curriculares 
	 * no Portla do Discente, setando nível de ensino.  
	 * <br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 */
	public String popularBuscaDiscente() throws NegocioException {
		preparaBuscaGeral();
		componenteBusca.setNivel(getNivelEnsino());
		setFiltroNivel(true);
		return forward(JSP_BUSCA_GERAL);
	}
	
	/**
	 * Prepara os dados comuns a todos os níveis de ensino 
	 * para os casos de uso que utilizam a busca geral
	 * dos componentes curriculares
	 * <br/>
	 * Método não invocado por JSP's.
	 */
	private void preparaBuscaGeral(){
		initFiltros();
		initObj();
		componentes = new ArrayList<ComponenteCurricular>(0);
		selecionaNivelEnsino = true;
	}

	/**
	 * Localiza os detalhes de um componente curricular.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/geral/componente_curricular/include/resumo.jsp</li>
	 * </ul>
	 * 
	 * @see ComponenteDetalhes
	 * @return
	 * @throws DAOException
	 */
	public Collection<ComponenteDetalhes> getTodosDetalhes()
			throws DAOException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		return dao.findDetalhes(obj.getId());
	}

	/**
	 * Realiza buscas de componentes curriculares através dos parâmetros
	 * informados.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/lista.jsp</li>
	 * <li>/sigaa.war/public/componentes/busca_componentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String buscarComponente() throws ArqException {
		Character nivel = null;
		String codigo = null;
		String nome = null;
		String preRequisito = null, coRequisito = null, equivalencia = null;
		Date dataInicio = null, dataFim = null ;
		TipoComponenteCurricular tipo = null;
		ModalidadeEducacao modalidade = null;
		Unidade unidade = null;
		boolean filtroTotal = true;
		
		// se não permite o usuário selecionar o nível de ensino
		if (!isSelecionaNivelEnsino()) {
			filtroNivel = true;
			nivel = getNivelEnsino();
			componenteBusca.setNivel(getNivelEnsino());
		}
		
		if (filtroNivel) {
			validateRequired(componenteBusca.getNivel(), "Nível", erros);
			if( NivelEnsino.isAlgumNivelStricto( componenteBusca.getNivel() ) )
				componenteBusca.setNivel( NivelEnsino.STRICTO );
			nivel = componenteBusca.getNivel();
		}
		if (filtroCodigo) {
			validateRequired(componenteBusca.getCodigo(), "Código", erros);
			codigo = componenteBusca.getCodigo().toUpperCase().trim();
		}
		if (filtroNome) {
			validateRequired(componenteBusca.getNome(), "Nome da Disciplina", erros);
			nome = componenteBusca.getNome();
		}
		if (filtroPreRequisito){
			if ( !ValidatorUtil.isEmpty(componenteBusca.getDetalhes().getPreRequisito()) )
				preRequisito = componenteBusca.getDetalhes().getPreRequisito();
			else validateRequired(componenteBusca.getDetalhes().getPreRequisito(), "Pré-Requisito", erros);
		}	
		if (filtroCoRequisito){
			if ( !isEmpty(componenteBusca.getDetalhes().getCoRequisito()) )
				coRequisito = componenteBusca.getDetalhes().getCoRequisito();
			else validateRequired(componenteBusca.getDetalhes().getCoRequisito(), "Co-Requisito", erros);
		}	
		if (filtroEquivalencia){
			validateRequired(componenteBusca.getDetalhes().getEquivalencia(), "Equivalência", erros);
			if ( !isEmpty(componenteBusca.getDetalhes().getEquivalencia()) ){
				equivalencia = componenteBusca.getDetalhes().getEquivalencia();
			}	
		}else
			filtroPeriodoEquivalencia = false;
		
		if (filtroPeriodoEquivalencia){
			validateRequired(dataInicioEquivalencia, "Data Inicial do Período de Equivalência", erros);
			if (  !isEmpty(dataInicioEquivalencia) ){
				dataInicio = CalendarUtils.descartarHoras(dataInicioEquivalencia);
			}
			dataFim =  ValidatorUtil.isEmpty(dataFimEquivalencia) ? CalendarUtils.descartarHoras(new Date()) : CalendarUtils.descartarHoras(dataFimEquivalencia);
			if( !isEmpty(dataInicio) && !isEmpty(dataFim) ){
				ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Período de Equivalência", erros);
			}
		}	
		if (filtroTipo) {
			validateRequired(componenteBusca.getTipoComponente(), "Tipo de Componente", erros);
			tipo = componenteBusca.getTipoComponente();
		}
		if (filtroModalidade) {
			validateRequired(componenteBusca.getModalidadeEducacao(), "Modalidade", erros);
			modalidade = componenteBusca.getModalidadeEducacao();
		}
		if (!selecionaUnidade || filtroUnidade) {
			validateRequired(componenteBusca.getUnidade(), "Unidade Responsável", erros);
			unidade = componenteBusca.getUnidade();
		} else {
			obj.setUnidade(new Unidade());
		}
		
		if (nivel == null && codigo == null && nome == null && tipo == null && modalidade == null && unidade == null 
				&& preRequisito == null && coRequisito == null && equivalencia == null && dataInicio == null && dataFim == null) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			
		} 
		if (hasErrors())
			return null;
		if (NivelEnsino.isEnsinoBasico(getNivelEnsino()) && !getAcessoMenu().isPedagogico())
			unidade = new Unidade(getUnidadeGestora());
		try {
			if (nivel == null)
				nivel = ' ';
			
			boolean apenasAtivos = true;
			
			// somente os papéis de CDP e administrador DAE pode ter acesso aos componentes inativos
			if (!modoOperador && getUsuarioLogado() != null) {
				if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.PPG))
					apenasAtivos = false;
					filtroTotal = false;
			}
			DisciplinaDao dao = getDAO(DisciplinaDao.class);
			
			//Verificar se o subsistema foi setado no caso da área pública do SIGAA
			if (!isEmpty(getSubSistema()) && isPortalCoordenadorLato()) {
				componentes = dao.findByTipo((tipo == null ? 0 : tipo.getId()), (unidade == null ? 0 : unidade.getId()), getCursoAtualCoordenacao().getId(), NivelEnsino.LATO, codigo, null, (modalidade == null ? 0 : modalidade.getId()));
			} else if(!isEmpty(getSubSistema()) && isPortalComplexoHospitalar()){
				if(isUserInRole(SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA))
					componentes = dao.findByTipo((tipo == null ? 0 : tipo.getId()), (obj.getUnidade().getId() == 0 ? getProgramaResidencia().getId() : obj.getUnidade().getId()), 0, NivelEnsino.RESIDENCIA, codigo, null, (modalidade == null ? 0 : modalidade.getId()));
				else
					componentes = dao.findCompleto(codigo, nome, preRequisito, coRequisito, equivalencia, dataInicio, dataFim, modalidade, tipo, unidade, NivelEnsino.RESIDENCIA, filtroRelatorio, apenasAtivos, filtroTotal, tiposValidos);
			} else {
				componentes = dao.findCompleto(codigo, nome, preRequisito, coRequisito, equivalencia, dataInicio, dataFim, modalidade, tipo, unidade, nivel, filtroRelatorio, apenasAtivos, filtroTotal, tiposValidos);
			}
			if (componentes.size() == MAX_RESULTADOS) 
				addMensagem(MensagensArquitetura.BUSCA_MAXIMO_RESULTADOS, MAX_RESULTADOS);
			
		} catch (LimiteResultadosException e) {
			addMensagemErro(e.getMessage());
			componentes = new ArrayList<ComponenteCurricular>(0);
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			componentes = new ArrayList<ComponenteCurricular>(0);
			return null;
		}
		if (componentes.isEmpty())
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		if (! hasErrors() && filtroRelatorio)
			return relatorio();
		return null;
	}

	/**
	 * Encaminha para a página que exibe o resultado da consulta em formato
	 * relatório.<br/><br/>
	 * <ul>
	 * 		<li>Método chamado pelo método buscarComponente.</li>
	 * 		<li>Método não invocado por JSPs.</li>
	 * </ul>
	 * @return
	 */
	private String relatorio() {
		return forward("/geral/componente_curricular/relatorio.jsf");
	}

	/**
	 * Carrega e exibe os dados detalhados do componente curricular.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/lista.jsp</li>
	 * 		<li>/sigaa.war/public/componentes/busca_componentes.jsp</li>
	 * 		<li>/sigaa.war/public/curso/resumo_curriculo.jsp</li>
	 * 		<li>/sigaa.war/public/programa/grade.jsp</li>
	 * 		<li>/sigaa.war/public/programa/turma.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String detalharComponente() throws ArqException {
		obj = new ComponenteCurricular(); 
		populateObj(true);
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		curriculos = dao.findCurriculosComponente(obj);
		todosGraduacao = dao.findAllExpressoes(NivelEnsino.GRADUACAO);
		equivalenciasEspecificas = dao.findEquivalenciaEspecificaByComponente(obj);
		obj.preencherInversos(todosGraduacao);
		obj.preencherEquivalenciaEspecifica(equivalenciasEspecificas);
		obj.setInversosEquivalenciaEspecifica( dao.findEquivalenciaEspecificaInversosByComponente(obj) );
		
		ExpressaoComponenteCurriculoDao daoExp = getDAO(ExpressaoComponenteCurriculoDao.class);
		obj.setExpressoesEspecificaCurriculo( daoExp.findByComponenteCurriculo(null, obj.getId(), true) ); 
		
		reportImpressao = false;
		if (getParameter("publico") == null) {
			return forward("/geral/componente_curricular/resumo.jsp");
		} else {
			return forward("/public/componentes/resumo.jsp");
		}
	}

	/**
	 * Resumo do componente curricular para impressão.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/geral/componente_curricular/busca_geral.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String imprimirComponente() throws ArqException {
		obj = new ComponenteCurricular();
		populateObj(true);
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		curriculos = dao.findCurriculosComponente(obj);
		todosGraduacao = dao.findAllExpressoes(NivelEnsino.GRADUACAO);
		obj.preencherInversos(todosGraduacao);
		reportImpressao = true;
		return forward("/geral/componente_curricular/resumo_impressao.jsp");
	}
	
	

	/**
	 * Cancela a execução da operação, limpa os dados e retorna para a página
	 * principal do subsistema atual.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/geral/componente_curricular/busca_geral.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/bloco.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/comprovante.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/lista.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 */	
	@Override
	public String cancelar() {
		if (isModoOperador())
			return mBean.retornarSelecaoComponente();
		resetBean();
		if (!isPortalGraduacao() && isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO))
			return forward(AutorizacaoCadastroComponenteMBean.JSP_COMPONENTES_SOLICITADOS);
		return super.cancelar();
	}
	
	/**
	 * Limpar todos os dados populados na busca pública dos componentes e redireciona para
	 * página principal pública. 
	 * <ul>
	 * 		<li>/sigaa.war/public/componentes/busca_componentes.jsp</li>
	 * </ul>
	 * @return
	 */
	public String cancelarPublico() {
		resetBean();
		return redirectJSF("/public");
	}
	
	/**
	 * Retorna o endereço do formulário de resumo dos dados do componente curricular.
	 * <br />
	 * Método não chamado por JSPs.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return forward(JSP_RESUMO);
	}

	/**
	 * Retorna o link para a lista de componentes curriculares.
	 * <br />
	 * Método não chamado por JSPs.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return forward(JSP_LISTA);
	}
	
	/** Seta o estado da operação para cancelado, concluindo a operação corrente.<br/><br/>
	 * 
	 * Método não invocado por JSP. É public por causa da arquitetura.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return cancelar();
	}

	/**
	 * Popula os dados de um componente curricular a partir de seu Id e
	 * redireciona o usuário para a página de detalhes.<br/>
	 * Não foram encontradas referências para este método.<br/><br/>
	 * 
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String verDadosGerais() throws DAOException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		int id = getParameterInt("id");
		obj = dao.findByPrimaryKey(id, ComponenteCurricular.class);
		if (obj.isBloco() && obj.getSubUnidades() != null)
			obj.getSubUnidades().iterator();
		return forward(JSP_TIPO_COMPONENTE);
	}
	
	/**
	 * Atribui os dados submetidos pelo primeiro formulário ao componente
	 * curricular em sessão, e de acordo com o tipo do componente redireciona ou
	 * para a tela de resumo e confirmação dos dados ou para o formulário dos
	 * dados do Bloco.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/geral.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterTipoComponente() throws DAOException {
		operacaoIsAtiva();
		
		if (hasErrors()) {
			return cancelar();
		}

		ValidatorUtil.validateRequired(obj.getTipoComponente(), "Tipo do Componente Curricular", erros);
		if ( (obj.isAtividade() || obj.isPassivelTipoAtividade()) && obj.getTipoAtividade() != null && obj.getTipoAtividade().getId() > TIPO_DISCIPLINA_REGULAR ) {
			ValidatorUtil.validateRequired(obj.getTipoAtividade(), "Tipo da "+(obj.isAtividade() ? "Atividade" : "Disciplina"), erros);
			ValidatorUtil.validateRequired(obj.getFormaParticipacao(), "Forma de Participação", erros);
		} else {
			obj.setTipoAtividade(null);
			obj.setFormaParticipacao(null);
		}
		ValidatorUtil.validateRequired(obj.getModalidadeEducacao(), "Modalidade de Educação", erros);
		if (hasErrors())
			return null;
		GenericDAO dao = getGenericDAO();
		obj.setTipoComponente(dao.refresh(obj.getTipoComponente()));
		dao.initialize(obj.getModalidadeEducacao());
		if (obj.isAtividade() || obj.isPassivelTipoAtividade()) {
			obj.setTipoAtividade(dao.refresh(obj.getTipoAtividade()));
			obj.setFormaParticipacao(dao.refresh(obj.getFormaParticipacao()));
			obj.setAceitaSubturma(false);
		}
		if (obj.getCurso() == null) {
			obj.setCurso(new Curso());
		}
		
		obj.getDetalhes().calcularCHTotal();
		
		validarAlteracaoFormaParticipacao();
		
		if (!obj.isAtividade() && isPortalCoordenadorGraduacao() && isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)) {
			addMensagemErro("Caro Coordenador, sua permissão é só para cadastrar/alterar Atividades.");
			return null;
		}
		
		return forward(JSP_GERAL);		
	}

	/**
	 * Atribui os dados submetidos pelo primeiro formulário ao componente
	 * curricular em sessão, e de acordo com o tipo do componente redireciona ou
	 * para a tela de resumo e confirmação dos dados ou para o formulário dos
	 * dados do Bloco.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/tipo_componente.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String submeterDadosGerais() throws ArqException {
		operacaoIsAtiva();
		
		if (hasErrors()) {
			return cancelar();
		}
		obj.getDetalhes().calcularCHTotal();
		//Para algumas validações a frente é necessário a unidade para carregar os
		//parâmetros da gestora acadêmica, logo validando ela no início.
		if(obj.getUnidade() == null || obj.getUnidade().getId() == 0) {
			if (obj.isStricto())
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Programa");
			else
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade");
			return null;
		}
		if (!isEmpty(obj.getEmenta())) obj.setEmenta(obj.getEmenta().replace("\r", ""));
		if (!isEmpty(obj.getBibliografia())) obj.setBibliografia(obj.getBibliografia().replace("\r", ""));
		// pré-validação dos campos obrigatórios
		erros.addAll(obj.validate());
		if (hasErrors()) return null;
		
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		
		try {
	
			analisarInativacaoDependencias(dao);
			
			if (obj.isBloco()) {
				if (obj.getSubUnidades() == null)
					obj.setSubUnidades(new ArrayList<ComponenteCurricular>(0));
				initSubUnidade();
				alterandoSubUnidade = false;
			} else {
				obj.setSubUnidades(null);
				obj.isPassivelTipoAtividade();
			}
			
			obj.setUnidade(dao.refresh(obj.getUnidade()));
			
			if (!isEmpty(obj.getUnidade()) && !(obj.getUnidade().isUnidadeAcademica())) {
				addMensagemErro("Não é permitido criar componentes curriculares para unidades que não são acadêmicas.");
			}
			
			if (!hasErrors())
				ComponenteCurricularTotaisHelper.calcularTotais(obj);
			
			if (!ValidatorUtil.isEmpty(obj.getCurso().getId())){
				CursoDao cursoDao = getDAO(CursoDao.class);
				obj.setCurso(cursoDao.findByPrimaryKey(obj.getCurso().getId(), Curso.class));
			}
	
			//Se for uma disciplina de graduação ou um módulo
			//verifica se a quantidade de créditos de ead não ultrapassa o limite estabelecido no tipo do componente.
			if (obj.isDistancia()) {
				obj.getDetalhes().setCrAula(0);
				obj.getDetalhes().setChAula(0);
				validateRequired(obj.getDetalhes().getChEad(), "Carga Horária de EaD", erros);
			} else if (NivelEnsino.isGraduacao(obj.getNivel()) && obj.getDetalhes().getChEad() > obj.getDetalhes().getChTotal() * (((float)obj.getTipoComponente().getChEadMax()) / 100))
				addMensagem(MensagensGraduacao.CH_EAD_ACIMA_DO_LIMITE, obj.getTipoComponente().getChEadMax());
			
			// caso a forma de participação não permita carga horária do docente, zera.
			if (!isExibeChDedicadaDocente())
				obj.getDetalhes().setChDedicadaDocente(0);
			else if ( !obj.isTrabalhoConclusaoCurso() && obj.getDetalhes().getChTotal() < obj.getDetalhes().getChDedicadaDocente() )
				addMensagemErro("Carga Horária do Docente: Não pode ser superior a carga horária total.");
			else if (isGraduacao() || isTecnico()) {
				if ((obj.getDetalhes().getChDedicadaDocente() == 0) && (obj.isEstagio() || obj.isAtividadeColetiva())) {
					addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Carga Horária do Docente");
				}
			}
			
			// seta a unidade do componente curricular
	
			if (!(obj.getUnidade().isUnidadeAcademica())) {
				addMensagemErro("Não é permitido criar componentes curriculares para unidades que não são acadêmicas.");
			}
			
			// valida formulário
			erros = new ListaMensagens();
			erros.addAll(obj.validate().getMensagens());
			
			analisarFormacaoExpressao();
			
			analisarValidadeDasExpressoes(dao);
				
			if( (obj.getCurso() != null && obj.getCurso().getId() > 0) || ValidatorUtil.isEmpty(obj.getCursoNovo()) ){
				obj.setCursoNovo(null);
			}
			
			if(isValidaCodigo())
				verificaFormatoCodigo(erros);
			
			// Não pode existir outro currículo com o mesmo código
			verificaCodigo(erros);
	
			// Transformar expressões informadas para o formato de armazenamento
			if (!hasErrors()) {
				obj.setPreRequisito(preRequisitoForm);
				obj.setCoRequisito(coRequisitoForm);
				obj.setEquivalencia(equivalenciaForm);
				preencherExpressoes(erros);
			}
			if (hasErrors()) return null;
	
			// Se o componente pertencer a um currículo, não deixar ele ter como
			// equivalência outros
			// componentes do mesmo currículo
			validaEquivalenciaMesmoCurriculo(dao);
			// valida equivalência entre componentes curriculares de modalidade de educação diferentes
			validaEquivalenciaModalidadeEducacao(dao);
			
			// Verificando se há erros 
			if (hasErrors()) {
				obj.setPreRequisito(preRequisitoForm);
				obj.setCoRequisito(coRequisitoForm);
				obj.setEquivalencia(equivalenciaForm);
				return null;
			}
	
			if (isStricto()) {
				if (obj.isDisciplina())
					obj.setNecessitaMediaFinal(true);
				else if (obj.isAtividade())
					obj.setNecessitaMediaFinal(false);
			}
			else if (!isStricto() && !obj.isAtividade())
				obj.setNecessitaMediaFinal(true);
			
			if (obj.isAtividade() && !obj.getDetalhes().isAtividadeAceitaTurma())
				obj.setMatriculavel(false);
	
			if (!isExibeChDedicadaDocente())
				obj.getDetalhes().setChDedicadaDocente(0);
			
			obj.getDetalhes().setNome(obj.getDetalhes().getNome().toUpperCase());
			
			if(obj.getTipoComponente() == null)
				obj.setTipoComponente(dao.findByPrimaryKey(obj.getTipoComponente().getId(), TipoComponenteCurricular.class));
			if (obj.getTipoAtividade() != null && obj.getTipoAtividade().getId() > 0)
				obj.setTipoAtividade(dao.findByPrimaryKey(obj.getTipoAtividade().getId(), TipoAtividade.class));
			
			if (obj.getTipoAtividadeComplementar() != null && obj.getTipoAtividadeComplementar().getId() > 0)
				obj.setTipoAtividadeComplementar(dao.findByPrimaryKey(obj.getTipoAtividadeComplementar().getId(), TipoAtividadeComplementar.class));
	
			if (obj.isBloco()) {
				return forward(JSP_BLOCO);
			} else {
				return forward(JSP_RESUMO);
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Verifica de a operação está ativa
	 * 
	 * @return
	 */
	private void operacaoIsAtiva() {
		if(!isOperacaoAtiva(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR.getId(),
				SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR.getId(),
				SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR.getId())){
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
		}
	}

	/**
	 * Se o componente pertencer a um currículo, não deixar ele ter como equivalência outros componentes do mesmo currículo
	 * 
	 * @param dao
	 * @throws ArqException 
	 */
	@SuppressWarnings("unchecked")
	private void validaEquivalenciaMesmoCurriculo(ComponenteCurricularDao dao)
			throws ArqException {
		if (!isEmpty(obj.getEquivalencia()) && !hasErrors()) {
			Collection<?> curriculos = dao.findCurriculosComponente(obj);
			if (!isEmpty(curriculos)) {
				CollectionUtils.transform(curriculos, new Transformer() {
					@Override
					public Object transform(Object item) {
						CurriculoComponente currComp = (CurriculoComponente) item;
						return currComp.getCurriculo();
					}
				});

				Collection<ComponenteCurricular> componentes = ExpressaoUtil
						.expressaoToComponentes(obj.getEquivalencia(), null, true);

				for (ComponenteCurricular componente : componentes) {
					List<Integer> idCurriculos = dao.componentePertenceCurriculos(componente, (Collection<Curriculo>) curriculos);
					if (!isEmpty(idCurriculos)) {
						componente = dao.refresh(componente);
						StringBuilder lista = new StringBuilder();
						for (Curriculo curriculo : (Collection<Curriculo>) curriculos) {
							for (Integer idCurriculo : idCurriculos) {
								if (curriculo.getId() == idCurriculo) {
									if (!lista.toString().isEmpty())
										lista.append(", ");
									lista.append(curriculo.getDescricaoCursoCurriculo());
								}
							}
						}
						if (!lista.toString().isEmpty())
							addMensagemErro("O componente "
									+ componente.getCodigoNome()
									+ " não pode ser informado na equivalência porque ele faz parte do(s) curriculo(s): "
									+ lista.toString() + "; no(s) qual(ais) "
									+ obj.getCodigoNome() + " também faz parte");
						return ;
					}
				}
			}
		}
	}
	
	/**
	 * Valida se o componente curricular é equivalente a outro de modalidade de
	 * educação diferente. Em outras palavras, um componente de ensino à
	 * distância não pode ser equivalente a um componente de ensino presencial.
	 * 
	 * @param dao
	 * @throws ArqException 
	 */
	private void validaEquivalenciaModalidadeEducacao(ComponenteCurricularDao dao) throws ArqException {
		if (!isEmpty(obj.getEquivalencia())) {
			Collection<ComponenteCurricular> componentes = ExpressaoUtil.expressaoToComponentes(obj.getEquivalencia(), null, true);
			for (ComponenteCurricular componente : componentes) {
				dao.initialize(componente);
				if (obj.getModalidadeEducacao().getId() != componente.getModalidadeEducacao().getId()) {
					addMensagemErro("O componente não pode ser equivalente ao componente "
							+ componente.getCodigoNome()
							+ " cujo a modalidade de educacação é "
							+ componente.getModalidadeEducacao().getDescricao());
					return ;
				}
			}
		}
	}

	/**
	 * Verifica se algum componente informado na expressão de Pre/CO-requisito foi removido ou teve a solicitação negada
	 * 
	 * @param dao
	 * @throws DAOException
	 */
	private void analisarValidadeDasExpressoes(ComponenteCurricularDao dao) throws DAOException {
		Collection<ComponenteCurricular> componentesRefereciados = new ArrayList<ComponenteCurricular>();
		componentesRefereciados.addAll( ExpressaoUtil.expressaoCodigoToComponentes(preRequisitoForm) );
		componentesRefereciados.addAll( ExpressaoUtil.expressaoCodigoToComponentes(coRequisitoForm) );
		
		if (!isEmpty(componentesRefereciados)) {
			String[] codigos = new String[componentesRefereciados.size()];
			int index = 0;
			for (ComponenteCurricular cc : componentesRefereciados) {
				codigos[index++] = cc.getCodigo();
			}
			
			componentesRefereciados = dao.findByCodigos(codigos);
		}
		
		
		for (ComponenteCurricular componenteCurricular : componentesRefereciados) {
			if (!componenteCurricular.isAtivo() && (componenteCurricular.getStatusInativo() != null 
					&& (componenteCurricular.getStatusInativo() == ComponenteCurricular.DESATIVADO || 
						componenteCurricular.getStatusInativo() == ComponenteCurricular.CADASTRO_NEGADO)) ) {
				if (isGraduacao())
					erros.addWarning(componenteCurricular.getCodigo() + " pode ser usado como Co-Requisito ou Pré-Requisito, no entanto é um componente inativo e foi removido ou teve a solicitação negada pelo "+
						RepositorioDadosInstitucionais.get("siglaCDP")+".");
				else
					erros.addErro(componenteCurricular.getCodigo() + " não pode ser usado como Co-Requisito ou Pré-Requisito porque é um componente inativo e foi removido ou teve a solicitação negada pelo "+
							RepositorioDadosInstitucionais.get("siglaCDP")+"." );
			}	
		}
	}

	/**
	 * Analisa as expressões de Pre/Co-requisitos e equivalências
	 */
	private void analisarFormacaoExpressao() {
		try {
			ArvoreExpressao.fromExpressao(preRequisitoForm);
		} catch (Exception e) {
			addMensagem(MensagensGraduacao.EXPRESSAO_PREREQUISITO_MAL_FORMADA);
		}
		try {
			ArvoreExpressao.fromExpressao(coRequisitoForm);
		} catch (Exception e) {
			addMensagem(MensagensGraduacao.EXPRESSAO_COREQUISITO_MAL_FORMADA);
		}
		try {
			ArvoreExpressao.fromExpressao(equivalenciaForm);
		} catch (Exception e) {
			addMensagem(MensagensGraduacao.EXPRESSAO_EQUIVALENCIA_MAL_FORMADA);
		}
	}

	/**
	 * Verifica se outros componentes fazem referencias ao componente atual
	 * 
	 * @param dao
	 * @throws DAOException
	 */
	private void analisarInativacaoDependencias(ComponenteCurricularDao dao) throws DAOException {
		if ( !obj.isAtivo() ) {
			List<ComponenteCurricular> componentes = dao.findComponentesReferenciamExpressao(obj.getId());
			
			if (!componentes.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (ComponenteCurricular componenteCurricular : componentes) {
					sb.append(componenteCurricular.getCodigo() + ", ");
				}
				sb.deleteCharAt(sb.lastIndexOf(","));
				if (getUsuarioLogado().isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
					addMensagemWarning("Existem outros componentes ("+sb.toString()+") que possuem dependência à este componente.");
				else 
					addMensagemErro("Não é possível inativar o componente porque existem outros componentes ("+sb.toString()+") que possuem dependência à este.");
			}
		}
	}
	
	/**
	 * Verifica se os campos de créditos e carga horária são obrigatórios na tela de dados gerais.
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/geral.jsp
	 * 
	 * @return
	 */
	public boolean isCreditosObrigatorios(){
	    return (obj.isDisciplina() || (!obj.isBloco() && obj.isNecessitaCargaHoraria() && !obj.isResidencia()) ); 
	} 
	
	/**
	 * Verifica se o código esta no formato correto. Ex: DCA0800.<br/>
	 * Os 3 primeiros caracteres são letras e os 4 últimos são números.
	 * @param erros
	 * @throws DAOException 
	 */
	private void verificaFormatoCodigo(ListaMensagens erros) throws DAOException {
		
		int tamanhoCodigo = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.TAMANHO_CODIGO_COMPONENTE);
		int qtdLetrasCodigo = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.QTD_LETRAS_CODIGO_COMPONENTE);
		ParametrosGestoraAcademica parametros = getParametrosAcademicos();
		
		String codigoComponente = obj.getCodigo();
		
		if (obj.isSubUnidade() && isNotEmpty(codigoComponente)) {
			int finalTexto = obj.getCodigo().indexOf(".");
			codigoComponente = obj.getCodigo().substring(0, finalTexto);
		}
		
		if ( codigoComponente == null || codigoComponente.length() != tamanhoCodigo ) {
			erros.addErro("Código do componente deve conter " + tamanhoCodigo + " caracteres");
			return ;
		}
		
		if (obj.getUnidade().getSiglaAcademica() == null) {
			return;
		}
		
		String codUnidade = obj.getCodigo().substring(0, qtdLetrasCodigo);
		
		if (getUltimoComando() != null && getUltimoComando().equals(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR)) {
			String siglaAcademica = obj.getUnidade().getSiglaAcademica().trim();
			
			if (siglaAcademica.length() == qtdLetrasCodigo && parametros.isValidaQtdLetrasCodigo()) {
				boolean verificaCodigo = true;
				String [] codigos = ParametroHelper.getInstance().getParametroStringArray(ParametrosGraduacao.CODIGO_UNIDADES_NAO_VERIFICAR_SIGLA_NO_CADASTRO_DE_COMPONENTES);
				if (codigos != null) {
					for (String codigo : codigos) {
						try{
							if (Long.parseLong(codigo.trim()) == obj.getUnidade().getCodigo()) {
								verificaCodigo = false;
								break;
							}
						} catch (NumberFormatException e) {
							notifyError(e);
						}
					}
				} 
				if (verificaCodigo && !codUnidade.equals(siglaAcademica.toUpperCase())) {
					erros.addErro("O código do componente deve começar com a sigla: " + siglaAcademica);
				}
			} 
		}
		
		
		char[] arrayCodUnidade = codUnidade.toCharArray();
		
		for (int i = 0; i < arrayCodUnidade.length; i++) {
			char c = arrayCodUnidade[i];
			if (!Character.isLetter(c)) {
				erros.addErro("Os "+qtdLetrasCodigo+" primeiros dígitos do código devem ser letras.");
				break;
			}
		}
		
		String codNumero = obj.getCodigo().substring(qtdLetrasCodigo, tamanhoCodigo);
		char[] arrayCodNumero = codNumero.toCharArray();
		
		for (int i = 0; i < arrayCodNumero.length; i++) {
			char c = arrayCodNumero[i];
			if (!Character.isDigit(c)) {
				erros.addErro("Os "+(tamanhoCodigo-qtdLetrasCodigo)+" últimos caracteres do código devem ser números.");
				break;
			}
		}
		
	}

	/**
	 * Verifica se um código já está sendo utilizado por outro componente.
	 * 
	 * @param erros
	 * @throws DAOException
	 */
	private void verificaCodigo(ListaMensagens erros) throws DAOException {
		DisciplinaDao dao = getDAO(DisciplinaDao.class);
		ComponenteCurricular ccBD = dao.findByCodigo(obj.getCodigo(), 0, 0,
				getNivelEnsino(), null);
		if (ccBD != null) {
			if (ccBD.getId() != obj.getId())
				erros
						.addErro("Já existe um componente curricular com esse código: "
								+ obj.getCodigo());
		}
	}

	/**
	 * Preenche os campos de expressões de equivalência, pré-requisitos e
	 * co-requisitos com os códigos dos componentes das expressões.
	 * 
	 * @param erros
	 * @throws DAOException
	 */
	private void preencherExpressoes(ListaMensagens erros) throws DAOException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		int tmp = obj.getUnidade().getId();
		obj.setUnidade(new Unidade());
		try {
			boolean somenteAtivos = true;
			if (isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO,
					SigaaPapeis.SECRETARIA_DEPARTAMENTO,
					SigaaPapeis.COORDENADOR_CURSO,
					SigaaPapeis.SECRETARIA_COORDENACAO,
					SigaaPapeis.COORDENADOR_CURSO_STRICTO,
					SigaaPapeis.SECRETARIA_POS,
					SigaaPapeis.GESTOR_TECNICO,
					SigaaPapeis.CDP,
					SigaaPapeis.DAE,
					SigaaPapeis.ADMINISTRADOR_DAE) ) {
				somenteAtivos = false;
			}
			
			ExpressaoUtil.buildExpressaoToDB(getObj(), dao, somenteAtivos);
		} catch (Exception e) {
			notifyError(e);
			erros.addErro(e.getMessage());
		} finally {
			obj.setUnidade(dao.findByPrimaryKey(tmp, Unidade.class));
		}
	}

	/**
	 * Envia os dados de componentes de bloco e direciona o usuário para a tela
	 * de resumo do cadastro.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/bloco.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String submeterBloco() throws DAOException {
		operacaoIsAtiva();
		
		if (hasErrors()) {
			return cancelar();
		}
		
		erros = new ListaMensagens();
		erros.addAll(obj.validateBloco().getMensagens());
		
		if (hasErrors())
			return redirectMesmaPagina();
		
		ComponenteCurricularTotaisHelper.calcularTotais(obj);
		
		return forward(JSP_RESUMO);
	}

	/**
	 * Adiciona sub-unidade em blocos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/bloco.jsp
	 * 
	 * @param evento
	 * @throws DAOException
	 */
	public String adicionarSubUnidade() throws DAOException {
		operacaoIsAtiva();
		if (hasErrors()) {
			return cancelar();
		}
		
		preValidacaoComumSubUnidade();
		
		subUnidade.setBlocoSubUnidade(obj);
		subUnidade.setModalidadeEducacao(obj.getModalidadeEducacao());
		
		validacaoSubUnidade();
		if (hasErrors())
			return null;

		ComponenteCurricularTotaisHelper.calcularTotais(subUnidade);

		// Carregando tipo
		subUnidade.setTipoComponente(getGenericDAO().findByPrimaryKey(subUnidade.getTipoComponente().getId(),TipoComponenteCurricular.class));

		if (obj.getSubUnidades().contains(subUnidade)) {
			addMensagemErro("Já existe subunidade com este mesmo nome.");
			return null;
		}
		obj.getSubUnidades().add(subUnidade);
		initSubUnidade();
		alterandoSubUnidade = false;
		return redirectMesmaPagina();
	}

	/**
	 * Método utilizado para validar a subUnidade antes de ser adicionada ou
	 * alterada.
	 * @throws DAOException 
	 */
	private void validacaoSubUnidade() throws DAOException {
		ValidatorUtil.validateRequiredId(subUnidade.getTipoComponente().getId(), "Tipo da Subunidade", erros);
		erros.addAll(subUnidade.validateSubUnidade());
		for (ComponenteCurricular subCadastrado : obj.getSubUnidades()) {
			if (subCadastrado.getNome().equalsIgnoreCase(subUnidade.getNome()) 
					&& 
					(subCadastrado.getId() != subUnidade.getId() ||
					subUnidade.getId() == 0 && subUnidadeAlterar != subCadastrado)) {
				addMensagemErro("Já existe subunidade com este mesmo nome.");
			}
			if (subCadastrado.getCodigo() != null && subCadastrado.getCodigo().equalsIgnoreCase(subUnidade.getCodigo()) 
					&& 
					(subCadastrado.getId() != subUnidade.getId() ||
					subUnidade.getId() == 0 && subUnidadeAlterar != subCadastrado)) {
				addMensagemErro("Já existe subunidade com este mesmo código.");
			}
		}
	}

	/**
	 * Exibe as sub-unidades de um bloco selecionado.<br/><br/>
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/bloco.jsp
	 * @throws Exception 
	 * 
	 */
	public void verSubUnidade() throws Exception {
		String nome = getParameter("nome");
		if (nome != null) {
			Iterator<ComponenteCurricular> iterator = obj.getSubUnidades().iterator();
			while (iterator.hasNext()) {
				ComponenteCurricular next = iterator.next();
				if (next.getNome().equals(nome)) {
					subUnidade = UFRNUtils.deepCopy(next);
					subUnidadeAlterar = next;
					alterandoSubUnidade = true;
					if (subUnidade.isModuloOuAtividadeColetiva()) {
						subUnidade.getDetalhes().setCrAula(
								subUnidade.getDetalhes().getChAula());
					}
					if (subUnidade.getId() == 0 && !isEmpty(subUnidade.getCodigo()))
						defineCodigoSubUnidadeAutomaticamente = true;
					else 
						defineCodigoSubUnidadeAutomaticamente = false;
					forward("/graduacao/componente/bloco.jsp");
					return;
				}
			}
		}
		addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
	}

	/**
	 * Altera uma sub-unidade de um componente do tipo bloco.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/bloco.jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String alterarSubUnidade() throws DAOException {
		operacaoIsAtiva();
		if (hasErrors()) {
			return cancelar();
		}

		preValidacaoComumSubUnidade();
		
		validacaoSubUnidade();
		if (hasErrors())
			return null;
		subUnidade.setTipoComponente(getGenericDAO().refresh(subUnidade.getTipoComponente()));
		obj.getSubUnidades().remove(subUnidadeAlterar);
		obj.getSubUnidades().add(subUnidade);
		
		ComponenteCurricularTotaisHelper.calcularTotais(subUnidade);
		
		initSubUnidade();
		alterandoSubUnidade = false;
		return null;
	}

	/** Prévalidação comum de subunidades de blocos.
	 * @throws DAOException
	 */
	private void preValidacaoComumSubUnidade() throws DAOException {
		if (this.defineCodigoSubUnidadeAutomaticamente)
			subUnidade.setCodigo(null);
		else {
			validateRequired(subUnidade.getCodigo(), "Código", erros);
			if (!isEmpty(subUnidade.getCodigo()) && !subUnidade.getCodigo().contains("."))
				addMensagemErro("O código da subunidade " + subUnidade.getCodigo() + " não está no padrão (separado por ponto).");
		}
		
		if (subUnidade.isModulo()) {
			subUnidade.getDetalhes().setCrAula(0);
		} else {
			ParametrosGestoraAcademica p = getParametrosAcademicos();
			subUnidade.setChAula(subUnidade.getDetalhes().getCrAula() * p.getHorasCreditosAula());
		}
	}

	/**
	 * Remove sub-unidade de blocos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/bloco.jsp
	 * 
	 * @throws DAOException
	 */
	public String removerSubUnidade() throws DAOException {
		String nome = getParameter("nome");
		if (nome != null) {
			Iterator<ComponenteCurricular> iterator = obj.getSubUnidades().iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getNome().equals(nome)) {
					iterator.remove();
					return redirectMesmaPagina();
				}
			}
		}
		addMensagem(MensagensGraduacao.NAO_FOI_POSSIVEL_REMOVER_A_SUBUNIDADE);
		return redirectMesmaPagina();
	}
	
	/**
	 * Cadastra um componente curricular.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/resumo.jsp
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String cadastrarComponente() throws ArqException, NegocioException {

		operacaoIsAtiva();
		
		if (hasErrors()) {
			return cancelar();
		}
		
		if (!confirmaSenha())
			return null;

		if (obj.getTipoAtividadeComplementar() != null
				&& obj.getTipoAtividadeComplementar().getId() == 0)
			obj.setTipoAtividadeComplementar(null);


		if (ValidatorUtil.isEmpty(obj.getCurso()))
			obj.setCurso(null);
		
		Comando comando = SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR;
		if ("alterar".equalsIgnoreCase(getConfirmButton()))
			comando = SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR;
		else if ("remover".equalsIgnoreCase(getConfirmButton()))
			comando = SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR;

		if (comando.getId() != SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR.getId()) {
			erros = new ListaMensagens();
			erros.addAll(obj.validate().getMensagens());
			
			if (hasErrors())
				return null;
		}
		
		// Testando expressões de Pré-requisito, co-requisito e equivalência
		// Transformar expressões informadas para o formato de armazenamento
		if (!hasErrors()) {
			obj.setPreRequisito(preRequisitoForm);
			obj.setCoRequisito(coRequisitoForm);
			obj.setEquivalencia(equivalenciaForm);
			preencherExpressoes(erros);
		}
		
		ComponenteCurricularMov mov = new ComponenteCurricularMov();
		mov.setProcessarExpressoes(true);
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);
		if (isSolicitacaoCadastroComponente()) {
			mov.setAcao(ComponenteCurricularMov.ACAO_SOLICITAR_CADASTRO);
		}

		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			idComponente = obj.getId();
			

			String responsavelConfirmacaoComponente = isPortalCoordenadorStricto() ? 
					RepositorioDadosInstitucionais.get("siglaUnidadeGestoraPosGraduacao") : RepositorioDadosInstitucionais.get("siglaCDP");  
					; 
			
			
			if (comando.equals(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR)) {
				if (isSolicitacaoCadastroComponente()) {
					addMessage(
							"O cadastro de Componente Curricular foi solicitado com sucesso!<br>"
							+ "Aguarde a confirmação de cadastro da "+ responsavelConfirmacaoComponente +".",
							TipoMensagemUFRN.INFORMATION);
					redirectJSF(JSP_POSCADASTRO);
				} else {
					addMessage("Componente Curricular cadastrado com sucesso!",
							TipoMensagemUFRN.INFORMATION);
					cancelar();
				}
			} else if (comando.equals(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR)) {
				if (isSolicitacaoCadastroComponente())
					addMessage(
							"A alteração dos dados de Componente Curricular foi solicitado com sucesso!<br>"
							+ "Aguarde a confirmação de cadastro da "+ responsavelConfirmacaoComponente +".",
							TipoMensagemUFRN.INFORMATION);
				else
					addMessage("Componente Curricular alterado com sucesso!",
							TipoMensagemUFRN.INFORMATION);

				ComponenteCurricular cc = null;
				for(ComponenteCurricular c: componentes)
					if(c.getId() == obj.getId())
						cc = c;
				componentes.remove(cc);
				componentes.add(obj);
				
				if ( super.isPortalPpg() && obj.isAguardandoConfirmacao() ) {
					AutorizacaoCadastroComponenteMBean mBean = getMBean("autorizacaoComponente");
					return mBean.iniciar();
				}
				
				if ( super.isTecnico() || super.isFormacaoComplementar() || super.isPortalPpg() )
					returnBusca();
				return getListPage();
					
			} else if (comando.equals(SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR)) {
				addMessage("Componente Curricular removido com sucesso!",TipoMensagemUFRN.INFORMATION);
				getComponentes().remove(obj);
				initObj();
				setResultadosBusca(null);
				modoOperador = false;
				selecionaNivelEnsino = false;
				if ( super.isTecnico() || super.isFormacaoComplementar() )
					returnBusca();
				return getListPage();
			}
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			e.printStackTrace();
			return null;
		} finally {
			initObj();
			modoOperador = false;
			selecionaNivelEnsino = false;
			removeOperacaoAtiva();
		}
		
		removeOperacaoAtiva();
		return null;
	}

	/**
	 * Atualiza os dados do componente curricular.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/geral/componente_curricular/expressoes_invalidas.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/autorizacao/solicitados.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/lista.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	@Override
	public String atualizar() throws ArqException {
		//Evitar que após voltar para a lista, o usuário possa selecionar qualquer nível de ensino
		boolean restringirNivel = obj.isResidencia();
		
		initObj();
		
		if(restringirNivel)
			returnBusca();
		
		try {
			//Seta o nível de ensino.
			getPopularNivel();
		} catch (NegocioException e) {
			throw new ArqException(e);
		}

		checkRole(SigaaPapeis.CDP, SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO,
				SigaaPapeis.SECRETARIA_POS,
				SigaaPapeis.COORDENADOR_CURSO,
				SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG, SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.GESTOR_LATO,
				SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA,
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		naoPermiteAlterarTipoComponente = null;
		setOperacaoAtiva(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR.getId());
		prepareMovimento(SigaaListaComando.ALTERAR_COMPONENTE_CURRICULAR);
		GenericDAO dao = getGenericDAO();
		setId();
		this.obj = dao
				.findByPrimaryKey(obj.getId(), ComponenteCurricular.class);

		if (this.obj.isGraduacao()) {
			if (this.obj.getId() == 0
					|| (this.obj.getStatusInativo() != null && this.obj
							.getStatusInativo() == ComponenteCurricular.AGUARDANDO_CONFIRMACAO))
				setReadOnly(false);
			else
				setReadOnly(true);
		} else if (this.obj.getId() == 0)
			setReadOnly(false);
		else
			setReadOnly(true);

		obj.setUnidade((Unidade) HibernateUtils.getTarget(obj.getUnidade()));
		obj.setPrograma((ComponenteCurricularPrograma) HibernateUtils
				.getTarget(obj.getPrograma()));

		if (obj.isBloco())
			obj.getSubUnidades().iterator();
		else
			obj.setSubUnidades(null);
		formatarExpressoes();
		preRequisitoForm = obj.getPreRequisito() != null ? obj
				.getPreRequisito() : "";
		coRequisitoForm = obj.getCoRequisito() != null ? obj.getCoRequisito()
				: "";
		equivalenciaForm = obj.getEquivalencia() != null ? obj
				.getEquivalencia() : "";
		verificaNulos();

		if (erros.isErrorPresent())
			return null;

		if (obj.isAtividade() || obj.isModuloOuAtividadeColetiva()){
			obj.getDetalhes().setCrAula(obj.getChAula());
			obj.getDetalhes().setCrLaboratorio(obj.getChLaboratorio());
		}

		if (isPortalCoordenadorStricto() && getAcessoMenu().isProgramaStricto() && !isMostrarAlterarComponenteCurricular()) {
			addMensagem(MensagensGraduacao.OPERACAO_PERMITE_SOMENTE_ALTERAR_EMENTA_E_REFERENCIAS);
		}
		
		if (SigaaSubsistemas.STRICTO_SENSU.getId() == getSubSistema().getId() && getQtdTurmasComponente() > 0) {
			String mensagem = "Existem turmas cadastradas para este componente com discentes matriculados e/ou consolidados e a alteração do tipo do componente não é recomendável.";
			addMensagemWarning(mensagem);
		}

		if (obj.getCurso() != null)
			obj.getCurso().getNome();
		
		if (obj.isDisciplina() && obj.getDetalhes().getCrAula() == 0)
			obj.getDetalhes().setCrAula(obj.getDetalhes().getChAula() / getHorasCreditoTeorico());
		
		setConfirmButton("Alterar");
		return forward(JSP_TIPO_COMPONENTE);
	}
	
	/**
	 * Inicia o processo de ativação/desativação de equivalências do componente.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String atualizarDetalheComponente() throws ArqException {
		setId();
		listaDetalhes.clear();
		componente = getGenericDAO().findAndFetch(obj.getId(), ComponenteCurricular.class, "detalhes");
		Collection<ComponenteDetalhes> aux = getGenericDAO().findByExactField(ComponenteDetalhes.class, "componente", componente.getId(), "DESC", "data");		
		Boolean possuiEquivalencia = false;		
		ComponenteCurricularDao componenteDao = getDAO(ComponenteCurricularDao.class);
		for(ComponenteDetalhes det :  aux) {			
			if(det.getEquivalencia() != null && !det.getEquivalencia().trim().isEmpty()) {
				listaDetalhes.add(det);
				det.setEquivalencia(ExpressaoUtil.buildExpressaoFromDB(det.getEquivalencia(), componenteDao, false));
				possuiEquivalencia = true;				
			}
		}		
		
		if(!possuiEquivalencia) {
			addMensagemErro("Este Componente Curricular não possui Equivalência a ser Ativada ou Desativada.");
			return null;
		}
		
		return forward(JSP_ATIVAR_DESATIVAR_COMPONENTE_DETALHES);		
	}
	
	/**
	 * Habilita a equivalência selecionada para o componente.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/ativar_desativar_componente_detalhes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String habilitarEquivalencia() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);		
		MovimentoCadastro mov = new MovimentoCadastro();		
		ComponenteDetalhes det = getGenericDAO().findByPrimaryKey(getParameterInt("idDetalhe"), ComponenteDetalhes.class);
		det.setDesconsiderarEquivalencia(false);
		det.setEquivalenciaValidaAte(null);
		mov.setObjMovimentado(det);		
		mov.setCodMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);
		execute(mov);
		
		for(ComponenteDetalhes detalhe : listaDetalhes) {
			if(detalhe.getId() == det.getId()) {
				detalhe.setDesconsiderarEquivalencia(false);
				detalhe.setEquivalenciaValidaAte(null);
				break;
			}
		}
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return forward(JSP_ATIVAR_DESATIVAR_COMPONENTE_DETALHES);
	}
	
	/**
	 * Desabilita a equivalência selecionada para o componente.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/ativar_desativar_componente_detalhes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String desabilitarEquivalencia() throws ArqException, NegocioException {
		prepareMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);		
		MovimentoCadastro mov = new MovimentoCadastro();		
		ComponenteDetalhes det = getGenericDAO().findByPrimaryKey(getParameterInt("idDetalhe"), ComponenteDetalhes.class);
		Date hoje = new Date();
		det.setEquivalenciaValidaAte(hoje);
		mov.setObjMovimentado(det);		
		mov.setCodMovimento(SigaaListaComando.ATIVAR_DESATIVAR_COMPONENTE_DETALHE);
		execute(mov);
		
		for(ComponenteDetalhes detalhe : listaDetalhes) {
			if(detalhe.getId() == det.getId()) {
				detalhe.setEquivalenciaValidaAte(hoje);
				break;
			}
		}
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		return forward(JSP_ATIVAR_DESATIVAR_COMPONENTE_DETALHES);
	}

	/**
	 * Formata as expressões do componente para apresentação ao usuário
	 */
	private void formatarExpressoes() {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		if (!StringUtils.isEmpty(obj.getPreRequisito())) {
			try{
				String expP = ExpressaoUtil.buildExpressaoFromDB(obj.getPreRequisito(), dao, false);
				obj.setPreRequisito(expP);
			} catch (Exception e) {
				addMensagemWarning("Pré-requisito mal formado.");
			}
		}

		if (!StringUtils.isEmpty(obj.getCoRequisito())) {
			try{
				String expC = ExpressaoUtil.buildExpressaoFromDB(obj.getCoRequisito(), dao, false);
				obj.setCoRequisito(expC);
			} catch (Exception e) {
				addMensagemWarning("Co-requisito mal formado.");
			}
		}

		if (!StringUtils.isEmpty(obj.getEquivalencia())) {
			try{
				String expE = ExpressaoUtil.buildExpressaoFromDB(obj.getEquivalencia(), dao, false);
				obj.setEquivalencia(expE);
			} catch (Exception e) {
				addMensagemWarning("Equivalência mal formado.");
			}
		}			
	}

	/**
     * Inicia a operação de remoção do componente curricular.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/lista.jsp
	 */
	@Override
	public String preRemover() {
		try {
			checkRole(SigaaPapeis.CDP, SigaaPapeis.SECRETARIA_POS,
					SigaaPapeis.PPG, SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		} catch (SegurancaException e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return forward(getListPage());
		}
		GenericDAO dao = getGenericDAO();
		
		dao.setSistema(getSistema());
		dao.setSession(getSessionRequest());
		
		try {
			setOperacaoAtiva(SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR.getId());
			prepareMovimento(SigaaListaComando.REMOVER_COMPONENTE_CURRICULAR);
			setId();
			PersistDB obj = this.obj;
			this.obj = dao.findByPrimaryKey(obj.getId(),
					ComponenteCurricular.class);
			verificaNulos();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}finally{
			if ( dao != null )
			dao.close();
		}
		setReadOnly(true);
		setConfirmButton("Remover");
		return getFormPage();
	}

	/**
	 * Verifica se alguns dos atributos do objeto estão nulos, instânciando-os
	 * quando necessário.
	 */
	private void verificaNulos() {
		if (obj.getTipoAtividade() == null)
			obj.setTipoAtividade(new TipoAtividade());
		if (obj.getTipoAtividadeComplementar() == null)
			obj.setTipoAtividadeComplementar(new TipoAtividadeComplementar());
		if (obj.getFormaParticipacao() == null)
			obj.setFormaParticipacao(new FormaParticipacaoAtividade());
	}

	/**
     * Redireciona para o formulário de dados gerais do componente curricular.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/componente/bloco.jsp</li>
	 * 		<li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarDadosGerais() {
		operacaoIsAtiva();

		if (hasErrors()) {
			return cancelar();
		}
		
		if( obj.getTipoAtividade() == null )
			obj.setTipoAtividade( new TipoAtividade() );
		
		if( obj.getFormaParticipacao() == null )
			obj.setFormaParticipacao( new FormaParticipacaoAtividade() );
		
		return forward(JSP_TIPO_COMPONENTE);
	}

	/**
     * Redireciona para o formulário de Sub-unidades de Blocos.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/resumo.jsp
	 * 
	 * @return
	 */
	public String voltarBloco() {
		initSubUnidade();
		alterandoSubUnidade = false;
		return forward(JSP_BLOCO);
	}

	/**
	 * Este método não possui referência alguma para si, nem nas classes e nem
	 * nas JSPs.<br/><br/>
	 * 
	 * Método não invocado por JSPs.
	 */
	public String resumir() throws DAOException {
		if (getParameter("id") != null) {
			if (obj == null) {
				obj = new ComponenteCurricular();
			}
			setId();
			obj = getGenericDAO().findByPrimaryKey(obj.getId(), obj.getClass());
		}
		return forward(JSP_RESUMO);
	}

	/**
	 * Inicia a operação de cadastro de componente curriculares.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 * 		<li>/sigaa.war/graduacao/departamento.jsp</li>
	 * 		<li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * 		<li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * 		<li>/sigaa.war/stricto/menus/cadastro.jsp</li>
	 * </ul>
	 * @throws NegocioException 
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		Unidade unidade = new Unidade();
		initObj();
		if ((isUserInRole(SigaaPapeis.COORDENADOR_CURSO,
				SigaaPapeis.SECRETARIA_COORDENACAO) && 
				getSubSistema().equals( SigaaSubsistemas.PORTAL_COORDENADOR ))
			|| (getSubSistema().equals(SigaaSubsistemas.SEDIS) 
				 && isUserInRole(SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.SEDIS))) {
			validarUnidadeCoordenacao();
			if (hasErrors())
				return null;
			
			TipoComponenteCurricular tipoComponente = getGenericDAO().findByPrimaryKey(TipoComponenteCurricular.ATIVIDADE, TipoComponenteCurricular.class);
			obj.setTipoComponente(tipoComponente);
			unidade = getGenericDAO().refresh(getCursoAtualCoordenacao()).getUnidadeCoordenacao();
		}

		return iniciarCadastro(unidade);
	}

	/**
	 * Inicia a operação de cadastro de componente curriculares.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 
	 * </ul>
	 * @throws NegocioException 
	 */
	public String preCadastrarStricto() throws ArqException, NegocioException {
		initObj();
		obj.setTipoComponente(new TipoComponenteCurricular());
		return iniciarCadastro(getProgramaStricto());
	}
	
	private String iniciarCadastro(Unidade unidade) throws SegurancaException,
			NegocioException, ArqException, DAOException {		
		verificarPermissaoCadastro();
		//Seta o nível de ensino.
		getPopularNivel();
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_COMPONENTE_CURRICULAR);
		setReadOnly(false);
		setConfirmButton("Cadastrar");
//		  se o usuário for coordenador, o tipo de componente solicitado só pode
//		  ser ATIVIDADE
		if (isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO)
				&& (isPortalDocente() || isPortalGraduacao())) {
			unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			unidade = getGenericDAO().refresh(unidade);
			if (!(unidade.isUnidadeAcademica() || unidade.isUnidadeAcademicaEspecializada())) {
				addMensagemErro("Não é permitido criar componentes curriculares para unidades que não são acadêmicas.");
				return null;
			}
		}
		
		if( (isUserInRole(SigaaPapeis.GESTOR_TECNICO) && getSubSistema().equals(SigaaSubsistemas.TECNICO))
				|| (isUserInRole(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR) && getSubSistema().equals(SigaaSubsistemas.FORMACAO_COMPLEMENTAR))){
			unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			unidade = getGenericDAO().refresh(unidade);
		}
		
		obj.setUnidade(unidade);
		sugerirCodigo(unidade);

		return forward(JSP_TIPO_COMPONENTE);
	}
	
	/**
	 * 
	 * Utilizado pela view para decidir se mostra o botão de alterar componente curricular.
	 * 
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/componente/lista.jsp
	 * 
	 * @return
	 */
	public boolean isMostrarAlterarComponenteCurricular() {
		if(isPortalCoordenadorStricto() && isCoordenadorOpcaoAlterarComponenteCurricular()) {
			return true;
		}
		if(!getAcessoMenu().isSecretariaPosGraduacao() && !getAcessoMenu().isCoordenadorCursoStricto()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * Utilizado pela view para decidir se mostra opcao para alterar a ementa e as referencias do componente.
	 * 
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/componente/lista.jsp
	 * 
	 * @return
	 */
	public boolean isMostrarAlterarEmentaReferenciaDeComponente() {
		//Pois se pode alterar o componente ja pode alterar a ementa e referencias. Evita duplicidade de botão.
		if(isMostrarAlterarComponenteCurricular()) {
			return false;
		}
		if(getAcessoMenu().isSecretariaPosGraduacao() || getAcessoMenu().isCoordenadorCursoStricto()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Verifica se o usuário logado tem permissão para cadastrar um componente curricular.
	 * @throws SegurancaException
	 */
	private void verificarPermissaoCadastro() throws SegurancaException {
		
		if( isCoordenadorOpcaoCadastrarComponenteCurricular() ){		
			checkRole(SigaaPapeis.CDP, SigaaPapeis.CHEFE_DEPARTAMENTO,
					SigaaPapeis.SECRETARIA_DEPARTAMENTO,
					SigaaPapeis.COORDENADOR_GERAL_EAD,
					SigaaPapeis.COORDENADOR_CURSO,
					SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.PPG,
					SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
					SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.GESTOR_TECNICO, 
					SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,SigaaPapeis.COORDENADOR_CURSO_STRICTO);			
		} else {
			checkRole(SigaaPapeis.CDP, SigaaPapeis.CHEFE_DEPARTAMENTO,
					SigaaPapeis.SECRETARIA_DEPARTAMENTO,
					SigaaPapeis.COORDENADOR_GERAL_EAD,
					SigaaPapeis.COORDENADOR_CURSO,
					SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.PPG,
					SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
					SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.GESTOR_TECNICO, 
					SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		}
	}
	
	
	/**
	 * 
	 * Utilizado pela view para decidir se mostra o botão de alterar componente curricular para um coordenador.
	 * 
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/componente/lista.jsp
	 * 
	 * @return
	 */
	public boolean isCoordenadorOpcaoAlterarComponenteCurricular() {
		String param = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.PERMITE_COORDENACAO_CADASTRAR_ALTERAR_COMPONENTE_CURRICULAR);
		if(param.contains("A")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * Utilizado pela view para decidir se mostra o botão de cadastrar componente curricular para um coordenador. 
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/componente/lista.jsp
	 * 
	 * @return
	 */
	public boolean isCoordenadorOpcaoCadastrarComponenteCurricular() {
		String param = ParametroHelper.getInstance().getParametro(ParametrosStrictoSensu.PERMITE_COORDENACAO_CADASTRAR_ALTERAR_COMPONENTE_CURRICULAR);
		if(param.contains("C")) {
			return true;
		}
		return false;
	}
	

	/**
	 * Verifica se o usuário tem permissão de alterar a unidade de um componente
	 * curricular.
	 * <br />
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/complexo_hospitalar/Componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isPodeAlterarUnidade() {
		return getAcessoMenu().isCdp() && (obj.getId() == 00 || isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE)) ||
				getAcessoMenu().isPpg() ||
				obj.getId() == 0 && ( 
					getAcessoMenu().isComplexoHospitalar() ||
					SigaaSubsistemas.SEDIS.equals(getSubSistema()) ||
					(isCoordenadorOpcaoCadastrarComponenteCurricular() && isPortalCoordenadorStricto())
					);
	}

	/**
	 * Sugere o código de um componente curricular de acordo com a unidade
	 * selecionada.
	 * 
	 * @param unidade
	 * @throws DAOException
	 */
	private void sugerirCodigo(Unidade unidade) throws DAOException {
		
		int qtdLetrasCodigo = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.QTD_LETRAS_CODIGO_COMPONENTE);
		
		ParametrosGestoraAcademica parametros = getParametrosAcademicos();
		
		if (unidade != null) {
			if ( unidade.getSiglaAcademica() != null && 
				 (unidade.getSiglaAcademica().trim().length() == qtdLetrasCodigo || !parametros.isValidaQtdLetrasCodigo()) 
			   ) {
				
				ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
				codigoSugerido = new StringBuilder(unidade.getSiglaAcademica().trim().toUpperCase());
				Integer qtdLetrasCodigoSugerido = codigoSugerido.length() > qtdLetrasCodigo ? codigoSugerido.length() : null;
				if ( getNivelEnsino() == NivelEnsino.RESIDENCIA )
					codigoSugerido.append(dao.findCodigoDisponivel(unidade.getId(),NivelEnsino.RESIDENCIA, qtdLetrasCodigoSugerido));
				else
					codigoSugerido.append(dao.findCodigoDisponivel(unidade.getId(),obj.getNivel(), qtdLetrasCodigoSugerido));
				
				obj.setCodigo(codigoSugerido.toString());
			}
		}
	}

	/**
	 * Realiza as operações necessárias após a seleção de uma unidade para o
	 * componente.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/geral.jsp
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarUnidade(ValueChangeEvent e) throws DAOException {
		if (e.getNewValue() == null || e.getNewValue().toString().equals("0")) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Unidade");
			return;
		}
		cursos = null;
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		obj.setUnidade(dao.findByPrimaryKey((Integer) e.getNewValue(),
				Unidade.class));
		if (obj.getId() == 0) {
			sugerirCodigo(obj.getUnidade());
			getCurrentRequest().setAttribute("codigoSugerido", obj.getCodigo());
		}
	}

	/**
	 * Verifica se a coordenação do curso possui uma unidade associada.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 		<li>Método chamado pelo método verMinhasSolicitacoes na classe AutorizacaoCadastroComponenteMBean.</li>
	 * 		<li>Método não invocado por JSPs.</li>
	 * </ul>
	 */
	public void validarUnidadeCoordenacao() {
		if (getCursoAtualCoordenacao().getUnidadeCoordenacao() == null) {
			addMensagem(MensagensGraduacao.CURSO_SEM_UNIDADE_COORDENACAO, getCursoAtualCoordenacao().getDescricao());
		}
	}

	/**
	 * Monta combo com os componentes curriculares de graduação.<br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não foram encontradas referências a este método.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllGraduacao() throws DAOException {
		DisciplinaDao dao = getDAO(DisciplinaDao.class);
		Collection<ComponenteCurricular> col;
		try {
			col = dao.findByNome("", 0, NivelEnsino.GRADUACAO, null, false,false,null);
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro(e.getMessage());
			return null;
		}
		return toSelectItems(col, "id", "descricao");
	}

	/**
	 * Método para auto-completar combo de componentes curriculares por código
	 * ou nome do componente.<br/><br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/sigaa.war/stricto/edital_bolsas_reuni/form.jsp</li>
	 * 		<li>/sigaa.war/stricto/solicitacao_bolsas_reuni/form_plano_trabalho.jsp</li>
	 * </ul>
	 * 
	 * @param codigoNome
	 * @return
	 */
	public Collection<ComponenteCurricular> autocompleteGraduacao(
			Object codigoNome) {
		DisciplinaDao dao = getDAO(DisciplinaDao.class);
		try {
			Collection<ComponenteCurricular> listaBusca =  new ArrayList<ComponenteCurricular>();
			
			boolean apenasDepartamento = getParameterBoolean("apenasDepartamento");
			
			textoSuggestionBox = "";
			if (codigoNome != null && StringUtils.isAlpha(codigoNome.toString()) && codigoNome.toString().length() < 3)
				textoSuggestionBox = "Pesquisa pelo Nome ou Código, informe pelo menos 3 caracteres.";		
			
			if ((codigoNome != null && codigoNome.toString().length() >= 3))
				listaBusca = dao.findByNomeOuCodigo((String) codigoNome, (apenasDepartamento ? getUsuarioLogado().getVinculoAtivo().getUnidade().getId() : 0), NivelEnsino.GRADUACAO);
			
			if (ValidatorUtil.isEmpty(listaBusca) && ((codigoNome != null && codigoNome.toString().length() >= 3))){
				textoSuggestionBox = "Componente Curricular não encontrado.";
				listaBusca = new ArrayList<ComponenteCurricular>();				
			}
			
			return listaBusca;
		} catch (LimiteResultadosException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
			notifyError(e);
		}
		return null;
	}

	/**
	 * Método para auto-completar combo de componentes curriculares por código
	 * ou nome do componente do nivel e unidade informandos <br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/lato/proposta_curso/disciplinas.jsp</li>
	 * </ul>
	 * 
	 * @param codigoNome
	 * @return
	 */
	public Collection<ComponenteCurricular> autocompleteComponenteCurricular(Object codigoNome) {
		DisciplinaDao dao = getDAO(DisciplinaDao.class);
		try {
			Collection<ComponenteCurricular> listaBusca =  new ArrayList<ComponenteCurricular>();
			
			boolean apenasDepartamento = getParameterBoolean("apenasDepartamento");
			char nivel = getParameterChar("nivelPermitido"); 
			
			textoSuggestionBox = "";
			if (codigoNome != null && StringUtils.isAlpha(codigoNome.toString()) && codigoNome.toString().length() < 3)
				textoSuggestionBox = "Pesquisa pelo Nome ou Código, informe pelo menos 3 caracteres.";		
			
			if ((codigoNome != null && codigoNome.toString().length() >= 3))
				listaBusca = dao.findByNomeOuCodigo((String) codigoNome, (apenasDepartamento ? getUsuarioLogado().getVinculoAtivo().getUnidade().getId() : 0), nivel);
			
			if (ValidatorUtil.isEmpty(listaBusca) && ((codigoNome != null && codigoNome.toString().length() >= 3))){
				textoSuggestionBox = "Componente Curricular não encontrado.";
				listaBusca = new ArrayList<ComponenteCurricular>();				
			}
			
			return listaBusca;
		} catch (LimiteResultadosException e) {
			e.printStackTrace();
		} catch (DAOException e) {
			e.printStackTrace();
			notifyError(e);
		}
		return null;
	}

	/**
	 * Busca todos os componentes curriculares de graduação que podem ter turmas criadas e monta um combo.<br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não foram encontradas referências a este método.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllGraduacaoPassiveisTurma()
			throws DAOException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		Collection<ComponenteCurricular> col = dao
				.findAllGraduacaoPassiveisTurma(null);
		return toSelectItems(col, "id", "descricao");
	}

	/**
	 * Monta combo com os componentes curriculares do departamento.<br/>
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>Não foram encontradas referências a este método.</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllDepartamentoCombo() throws DAOException {
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);

		if (getUsuarioLogado().isUserInRole(SigaaPapeis.SEDIS)) {
			return toSelectItems(null, "id", "descricaoResumida");
		} else {
			return toSelectItems(dao.findByUnidadeOtimizado(getUsuarioLogado().getVinculoAtivo().getUnidade().getId(), getNivelEnsino(), null), "id",
					"descricaoResumida");
		}
	}

	/**
	 * Carrega os dados de um componente, incluindo suas expressões, para
	 * exibição dos detalhes ao usuário.
	 * <br />
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/comprovante.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/poscadastro.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/view_painel_resumido.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/view_painel.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String getCarregarComponente() throws ArqException {
		int idComponente = getParameterInt("id", 0);

		if (idComponente != 0) {
			ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
			obj = dao
					.findByPrimaryKey(idComponente, ComponenteCurricular.class);
			preRequisitoForm = obj.getPreRequisito() != null ? obj
					.getPreRequisito() : "";
			coRequisitoForm = obj.getCoRequisito() != null ? obj
					.getCoRequisito() : "";
			equivalenciaForm = obj.getEquivalencia() != null ? obj
					.getEquivalencia() : "";

			preRequisitoForm = ExpressaoUtil.buildExpressaoFromDBTag(
					preRequisitoForm, dao);
			coRequisitoForm = ExpressaoUtil.buildExpressaoFromDBTag(
					coRequisitoForm, dao);
			equivalenciaForm = ExpressaoUtil.buildExpressaoFromDBTag(
					equivalenciaForm, dao);
		}
		return "";
	}

	/**
	 * Retorna o total de horas necessárias para totalizar 1 crédito teórico, de acordo com os parâmetros da gestora
	 * acadêmica em questão.
	 * <br />
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public int getHorasCreditoTeorico() throws ArqException {
		ParametrosGestoraAcademica param = getParametrosAcademicosUnidadeDoComponente();
		if (param != null)
			return param.getHorasCreditosAula();
		else
			return 0;
	}
	
	/**
	 * Retorna o total de horas necessárias para totalizar 1 crédito de estágio, de acordo com os parâmetros da gestora
	 * acadêmica em questão.
	 * <br />
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public int getHorasCreditoEstagio() throws ArqException {
		ParametrosGestoraAcademica param = getParametrosAcademicosUnidadeDoComponente();
		if (param != null)
			return param.getHorasCreditosEstagio();
		else
			return 0;
	}
	
	/**
	 * Retorna o total de horas necessárias para totalizar 1 crédito prático, de acordo com os parâmetros da gestora
	 * acadêmica em questão.
	 * <br />
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public int getHorasCreditoPratico() throws ArqException {
		ParametrosGestoraAcademica param = getParametrosAcademicosUnidadeDoComponente();
		if (param != null)
			return param.getHorasCreditosLaboratorio();
		else
			return 0;
	}
	
	/**
	 * Retorna os parâmetros da unidade gestora acadêmica de acordo com o componente curricular.
	 * Vai do mais específico para o geral, ou seja, vai buscar na unidade gestora acadêmica da unidade responsável 
	 * pelo componente e se não encontrar retorna os parametros setados atualmente na sessão.  
	 * @return
	 * @throws DAOException
	 */
	private ParametrosGestoraAcademica getParametrosAcademicosUnidadeDoComponente() throws DAOException {		
		//Verificações para evitar NP em produção.
		//Se o componente curricular já estiver com a unidade responsável setada mas a unidade gestora 
		//acadêmica de tal unidade não estiver setada, vai buscar no banco.		
		if(obj!=null && (!ValidatorUtil.isEmpty(obj.getUnidade())  && ValidatorUtil.isEmpty(obj.getUnidade().getGestoraAcademica()) ) )  {
			//Como gestoraAcademica é EAGER, vai vir setado na unidade. 
			obj.setUnidade(getGenericDAO().findByPrimaryKey(obj.getUnidade().getId(), Unidade.class));
		}
		
		//Alguns métodos públicos que são acionados por jsp várias vezes, chamam este método.
		//Verifica se os parâmetros já foram definidos, evitando ir no banco novamente a cada get na jsp(através dos métodos públicos).
		if(obj!=null && !ValidatorUtil.isEmpty(obj.getUnidade()) && !ValidatorUtil.isEmpty(obj.getUnidade().getGestoraAcademica()) && !ValidatorUtil.isEmpty(obj.getParametros())
				&& obj.getUnidade().getGestoraAcademica().getId() == obj.getParametros().getUnidade().getId() && obj.getNivel() == obj.getParametros().getNivel()) {
			if(fatoresDeConversaoDefinidos(obj.getParametros()))			
				return obj.getParametros();
		} else if(obj!=null && !ValidatorUtil.isEmpty(obj.getUnidade()) && !ValidatorUtil.isEmpty(obj.getUnidade().getGestoraAcademica())){			
			ParametrosGestoraAcademica paramDoComponente = ParametrosGestoraAcademicaHelper.getParametros(obj);
			if(!ValidatorUtil.isEmpty(paramDoComponente) && fatoresDeConversaoDefinidos(paramDoComponente)) {				 
				obj.setParametros(paramDoComponente);					
				return obj.getParametros();
			}
		}
		//Se não retornou os parâmetros mais específicos, vai tentar pegar o atualmente na sessão. 
		return getParametrosAcademicos();
	}
	
	/**
	 * Verifica se os fatores de conversão de crédito em CH estão definidos nos parâmetros da gestora acadêmica  
	 * @param parametros
	 * @return
	 */
	private boolean fatoresDeConversaoDefinidos(ParametrosGestoraAcademica parametros) {
		if(!ValidatorUtil.isEmpty(parametros)) {
			if(!ValidatorUtil.isEmpty(parametros.getHorasCreditosAula()) && !ValidatorUtil.isEmpty(parametros.getHorasCreditosLaboratorio())
					&& !ValidatorUtil.isEmpty(parametros.getHorasCreditosEstagio())) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * Monta combo com os tipos de componente curricular.
	 * <br/>
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/bloco.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTiposSubUnidades() {
		ArrayList<SelectItem> sel = new ArrayList<SelectItem>(0);
		if (!isTecnico())
			sel.add(new SelectItem(TipoComponenteCurricular.DISCIPLINA,"Disciplina"));
		sel.add(new SelectItem(TipoComponenteCurricular.MODULO, "Módulo"));
		return sel;
	}

	/**
	 * Retorna o Id do componente criado. 
	 * @return
	 */
	public int getIdComponente() {
		return idComponente;
	}

	/**
	 * Seta o Id do componente criado. 
	 * @param idComponente
	 */
	public void setIdComponente(int idComponente) {
		this.idComponente = idComponente;
	}

	/**
	 * Exibe o comprovante da solicitação de cadastro de componente.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/poscadastro.jsp
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String imprimirComprovante()  {
		return forward(JSP_COMPROVANTE);
	}
	
	/**
	 * Retorna o Nome do chefe do departamento.
	 * <br/>
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/comprovante.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String getNomeChefeUnidade() throws ArqException {
		
		String chefeDepto = "";
		
		ServidorDao servidorDao = getDAO(ServidorDao.class);
		
		Collection<Servidor> colServidor = servidorDao.findChefesByDepartamento(getObj().getUnidade().getId());
		
		if(!isEmpty(colServidor)){
			for (Servidor servidor : colServidor) {
				if(servidor.getPessoa().getId() == getUsuarioLogado().getPessoa().getId()){
					// Caso exista mais de um chefe no departamento, retornar o nome do chefe logado no momento.
					return chefeDepto = servidor.getNome();
				}else{
					chefeDepto = servidor.getNome();
				} 
			}
		}	
		
		return chefeDepto;
		
	}
	
	/**
	 * Retorna o cargo do chefe da unidade para ser inserido no comprovante de 
	 * solicitação de cadastro de componente curricular.
	 * <br />
	 * Método chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/comprovante.jsp</li>
	 * </ul>
	 * @return
	 */
	public String getCargo(){
		return CARGO;
	}
	
	/**
	 * Re-exibe o comprovante da solicitação de cadastro de componente.<br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: 
	 * 		/sigaa.war/graduacao/componente/autorizacao/solicitados.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String reImprimirComprovante() throws DAOException {
		int id = getParameterInt("id", 0);
		this.obj = getGenericDAO().findByPrimaryKey(id,
				ComponenteCurricular.class);
		return imprimirComprovante();
	}

	/**
	 * Retorna os possíveis números máximos de avaliações que um componente pode ter.
	 * <br/>
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/bloco.jsp</li>
	 * <li>/sigaa.war/complexo_hospitalar/Componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getNumUnidadesPossiveis() throws DAOException {
		ArrayList<SelectItem> selects = new ArrayList<SelectItem>(0);
		ParametrosGestoraAcademica params = getParametrosAcademicos();
		for (int i = params.getQtdAvaliacoes(); i > 0; i--) {
			selects.add(new SelectItem(i + "", i + ""));
		}
		return selects;
	}

	/**
	 * Monta combo com os Programas ou unidades de graduação.
	 * <br/>
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/complexo_hospitalar/Componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getPossiveisUnidades() {
		if (this.possiveisUnidades == null) {
			UnidadeMBean mbean = new UnidadeMBean();
			if (getSubSistema().equals(SigaaSubsistemas.STRICTO_SENSU))
				this.possiveisUnidades = mbean.getAllProgramaPosCombo();
			else if (getSubSistema().equals(SigaaSubsistemas.COMPLEXO_HOSPITALAR))
				this.possiveisUnidades = mbean.getAllProgramaResidenciaCombo();
			else if(isPortalCoordenadorStricto())
				this.possiveisUnidades = toSelectItems(getAcessoMenu().getProgramas(), "id", "nome");
			else if ( isPortalLatoSensu() )
				this.possiveisUnidades = mbean.getAllDetentorasComponentesCombo();
			else
				this.possiveisUnidades = mbean.getAllDetentorasComponentesGraduacaoCombo();
		}
		return this.possiveisUnidades;
	}
	
	/**
	 * Método que retorna o nível do curso.
	 * <br/>
	 * Método chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public Collection<SelectItem> getCursoNivel() throws DAOException{
		if (this.cursos == null) {
			CursoDao dao = getDAO(CursoDao.class);
			if (obj.isGraduacao()) {
				obj.setUnidade(dao.refresh(obj.getUnidade()));
				cursos = toSelectItems(dao.findByNivel(obj.getNivel(), true, null), "id", "descricao");
			} else {
				cursos = toSelectItems(dao.findByUnidade(obj.getUnidade().getId(), obj.getNivel()), "id", "descricao");
			}
		}
		return this.cursos;
	}
	
	/**
	 * Serve para inicializar a busca dos componentes Curriculares.<br/><br/>
	 * 
	 * Método chamado pelas JSPs:<ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/cdp.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp</li>
	 *  </ul>
	 */
	@Override
	public String listar() throws ArqException {
		initObj();
		limparBusca();
		returnBusca();
		setComponenteBusca(obj);
		componentes = new HashSet<ComponenteCurricular>();
		return listaComponentes();
	}
	
	/**
	 * 
	 * Redireciona para a página de lista de componentes.
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/solicitacao_turma/dados_solicitacao.jsp
	 * @return
	 */
	public String listaComponentes() {
		return redirectJSF("graduacao/componente/lista.jsf");
	}
	
	/** Seta os filtros do retorno da busca.
	 * @throws ArqException
	 */
	private void returnBusca() throws ArqException{
		try {
			//Seta o nível de ensino.
			getPopularNivel();
			if((obj.getNivel() == NivelEnsino.TECNICO && !getAcessoMenu().isPedagogico()) || obj.getNivel() == NivelEnsino.FORMACAO_COMPLEMENTAR){
				setSelecionaUnidade(false);
				obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
			}
		} catch (NegocioException e) {
			throw new ArqException(e);
		}
		modoOperador = false;
		selecionaNivelEnsino = false;
	}

	/**
	 * Serve para inicializar a operação de manutenção do histórico das equivalências,
	 * onde poderá ativar ou desativar as equivalências a serem aceitas durantes
	 * as integralizações nas estruturas curriculares e nos currículos dos discentes.
	 * <br/><br/>
	 * 
	 * Método chamado pelas JSPs:<ul>
	 *  <li>/sigaa.war/graduacao/menus/cdp.jsp</li>
	 *  </ul>
	 */
	public String iniciarManutencaoHistoricoEquivalencias() throws ArqException {
		initObj();
		try {
			//Seta o nível de ensino.
			getPopularNivel();
			if(obj.getNivel() == NivelEnsino.TECNICO){
				setSelecionaUnidade(false);
				obj.setUnidade(getUsuarioLogado().getVinculoAtivo().getUnidade());
			}
		} catch (NegocioException e) {
			throw new ArqException(e);
		}
		componentes = new HashSet<ComponenteCurricular>();
		return forward(JSP_MANUTENCAO_EQUIVALENCIA);
	}
	
	/**
	 * Método que realiza a busca por um componente baseado nos parâmetros passados.
	 *  - Método não invocado por JSP´s.
	 * @param mBean 
	 * @param tituloOperacao 
	 * @param unidade
	 * @param selecionaNivelEnsino TODO
	 * @param tiposValidos
	 * @return
	 */
	public String buscarComponente(SeletorComponenteCurricular mBean, String tituloOperacao, Unidade unidade, boolean formaColetiva, boolean selecionaNivelEnsino, int... tiposValidos) throws ArqException {
		String retorno = listar();
		this.mBean = mBean;
		this.tituloOperacao = tituloOperacao;
		this.tiposValidos = tiposValidos;
		this.modoOperador = true;
		this.selecionaNivelEnsino = selecionaNivelEnsino;
		if( formaColetiva ){
			FormaParticipacaoAtividade formaParticipacao =	new FormaParticipacaoAtividade();
			formaParticipacao.setId( FormaParticipacaoAtividade.ESPECIAL_COLETIVA );
			componenteBusca.setFormaParticipacao( formaParticipacao );
		}
		selecionarUnidade(unidade);
		return retorno;
	}

	/**
	 * Método que realiza a busca por um componente baseado nos parâmetros passados.
	 * - Método não invocado por JSP´s.
	 * @param opcoes
	 * @return
	 * @throws ArqException
	 */
	public String buscarComponente(OpcoesBuscaComponente opcoes) throws ArqException {
		this.opcoesBusca = opcoes;
		
		String retorno = listar();
		this.mBean = opcoes.getMBean();
		this.tituloOperacao = opcoes.getTituloOperacao();
		this.tiposValidos = opcoes.getTiposValidos();
		this.modoOperador = true;
		this.selecionaNivelEnsino = false;
		if( opcoes.isFormaColetiva() ){
			FormaParticipacaoAtividade formaParticipacao =	new FormaParticipacaoAtividade();
			formaParticipacao.setId( FormaParticipacaoAtividade.ESPECIAL_COLETIVA );
			componenteBusca.setFormaParticipacao( formaParticipacao );
		}
		selecionarUnidade(opcoes.getUnidade());
		return retorno;
	}
	
	/**
	 * Seta a unidade dependendo da busca do nível. Buscando apenas para a unidade do 
	 * usuário ou em todas as unidades. 
	 * 
	 * @param unidade
	 */
	private void selecionarUnidade(Unidade unidade) {
		if ( unidade != null && getNivelEnsino() == NivelEnsino.LATO ) {
			this.selecionaUnidade = true;
			this.obj.setUnidade(new Unidade());
		} else if (unidade == null) {
				this.selecionaUnidade = true;
		} else {
			this.selecionaUnidade = false;
			this.obj.setUnidade(unidade);
		}
	}
	
	/**
	 * Método responsável pela seleção de um componente.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>sigaa.war\graduacao\componente_programa\busca_componente.jsp</li>
	 *   </ul>
	 * @throws ArqException 
	 *
	 * @throws SegurancaException
	 */
	public String selecionarComponente() throws ArqException {
		if (mBean != null) {
			int id = getParameterInt("id", 0);
			Integer idInt = new Integer(id);
			ComponenteCurricular selecionado = new ComponenteCurricular(idInt);
			selecionado = getGenericDAO().refresh(selecionado);
			if (selecionado == null) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
			
			ListaMensagens lista = mBean.validarSelecaoComponenteCurricular(selecionado);
			
			if (!isEmpty(lista)) {
				addMensagens(lista);
				// retorna caso haja algum erro.
				if (lista.isErrorPresent())
					return null;
			}
			
			if( selecionado.isBloco() && (opcoesBusca != null && opcoesBusca.isSelecionarSubUnidade()) ){
				subunidades = getGenericDAO().findByExactField(ComponenteCurricular.class, "blocoSubUnidade", selecionado.getId());
				return telaSubunidades();
			}
			
			return mBean.selecionaComponenteCurricular(selecionado);
		}
		return null;
	}
	
	/** Retorna o link para o formulário de definição de subunidades da turma.
	 * <br />
	 * Método não invocado por JSPs.
	 * 
	 * @return
	 */
	public String telaSubunidades(){
		return forward( JSP_SUBUNIDADES );
	}	
	
	/** Retorna a expressão que define o co-requisito do componente curricular.
	 * @return
	 */
	public String getCoRequisitoForm() {
		return coRequisitoForm;
	}

	/**
	 * Seta a expressão que define o co-requisito do componente curricular. 
	 * 
	 * @param coRequisitoForm
	 */
	public void setCoRequisitoForm(String coRequisitoForm) {
		this.coRequisitoForm = coRequisitoForm;
	}

	/**
	 * Retorna a expressão que define a equivalência do componente curricular. 
	 * @return
	 */
	public String getEquivalenciaForm() {
		return equivalenciaForm;
	}

	/**
	 * Seta a expressão que define a equivalência do componente curricular./**
	 * @param equivalenciaForm
	 */
	public void setEquivalenciaForm(String equivalenciaForm) {
		this.equivalenciaForm = equivalenciaForm;
	}

	/**
	 * Retorna a expressão que define o pré-requisito do componente curricular.
	 * @return
	 */
	public String getPreRequisitoForm() {
		return preRequisitoForm;
	}

	/**
	 * Seta a expressão que define o pré-requisito do componente curricular. 
	 * @param preRequisitoForm
	 */
	public void setPreRequisitoForm(String preRequisitoForm) {
		this.preRequisitoForm = preRequisitoForm;
	}

	/**
	 * Indica que o usuário está alterando a subUnidade do componente curricular.  
	 * @return
	 */
	public boolean isAlterandoSubUnidade() {
		return alterandoSubUnidade;
	}

	/**
	 * Seta que o usuário está alterando a subUnidade do componente curricular. 
	 * @param alterandoSubUnidade
	 */
	public void setAlterandoSubUnidade(boolean alterandoSubUnidade) {
		this.alterandoSubUnidade = alterandoSubUnidade;
	}

	/**
	 * Retorna a posição da subUnidade na lista de sub-unidades para alteração.
	 * @return
	 */
	public Integer getPosSubUnidadeAlterada() {
		return posSubUnidadeAlterada;
	}

	/**
	 * Seta a posição da subUnidade na lista de sub-unidades para alteração. 
	 * @param posSubUnidadeAlterada
	 */
	public void setPosSubUnidadeAlterada(Integer posSubUnidadeAlterada) {
		this.posSubUnidadeAlterada = posSubUnidadeAlterada;
	}

	/**
	 * Indica que a busca por componente deve ser filtrada por código do componente curricular. 
	 * @return
	 */
	public boolean isFiltroCodigo() {
		return filtroCodigo;
	}

	/**
	 * Seta que a busca por componente deve ser filtrada por código do componente curricular.  
	 * @param filtroCodigo
	 */
	public void setFiltroCodigo(boolean filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	/**
	 * Indica que a busca por componente deve ser filtrada por nível de ensino. 
	 * @return
	 */
	public boolean isFiltroNivel() {
		return filtroNivel;
	}

	/**
	 * Seta que a busca por componente deve ser filtrada por nível de ensino. 
	 * @param filtroNivel
	 */
	public void setFiltroNivel(boolean filtroNivel) {
		this.filtroNivel = filtroNivel;
	}

	/**
	 * Indica que a busca por componente deve ser filtrada por nome do componente curricular.
	 * @return
	 */
	public boolean isFiltroNome() {
		return filtroNome;
	}

	/**
	 * Seta que a busca por componente deve ser filtrada por nome do componente curricular.
	 * @param filtroNome
	 */
	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}

	public boolean isFiltroPreRequisito() {
		return filtroPreRequisito;
	}

	public void setFiltroPreRequisito(boolean filtroPreRequisito) {
		this.filtroPreRequisito = filtroPreRequisito;
	}

	public boolean isFiltroCoRequisito() {
		return filtroCoRequisito;
	}

	public void setFiltroCoRequisito(boolean filtroCoRequisito) {
		this.filtroCoRequisito = filtroCoRequisito;
	}

	public boolean isFiltroEquivalencia() {
		return filtroEquivalencia;
	}

	public void setFiltroEquivalencia(boolean filtroEquivalencia) {
		this.filtroEquivalencia = filtroEquivalencia;
	}

	public boolean isFiltroPeriodoEquivalencia() {
		return filtroPeriodoEquivalencia;
	}

	public void setFiltroPeriodoEquivalencia(boolean filtroPeriodoEquivalencia) {
		this.filtroPeriodoEquivalencia = filtroPeriodoEquivalencia;
	}

	/**
	 * Indica que a busca por componente deve ser filtrada por tipo do componente curricular.
	 * @return
	 */
	public boolean isFiltroTipo() {
		return filtroTipo;
	}

	/**
	 * Seta que a busca por componente deve ser filtrada por tipo do componente curricular.
	 * @param filtroTipo
	 */
	public void setFiltroTipo(boolean filtroTipo) {
		this.filtroTipo = filtroTipo;
	}

	/**
	 * Indica que a busca por componente deve ser filtrada por unidade ao qual o componente curricular pertence.
	 * @return
	 */
	public boolean isFiltroUnidade() {
		return filtroUnidade;
	}

	/**
	 * Seta que a busca por componente deve ser filtrada por unidade ao qual o componente curricular pertence.
	 * @param filtroUnidade
	 */
	public void setFiltroUnidade(boolean filtroUnidade) {
		this.filtroUnidade = filtroUnidade;
	}

	/**
	 * Retorna a lista de currículos ao qual o componente curricular pode pertencer.
	 * @return
	 */
	public Collection<CurriculoComponente> getCurriculos() {
		return curriculos;
	}

	/**
	 * Seta a lista de currículos ao qual o componente curricular pode pertencer. 
	 * @param curriculos
	 */
	public void setCurriculos(Collection<CurriculoComponente> curriculos) {
		this.curriculos = curriculos;
	}

	/**
	 * Retorna a lista de todos componentes curriculares dos cursos de graduação.
	 * @return
	 */
	public Collection<ComponenteCurricular> getTodosGraduacao() {
		return todosGraduacao;
	}

	/**
	 * Seta a lista de todos componentes curriculares dos cursos de graduação. 
	 * @param todosGraduacao
	 */
	public void setTodosGraduacao(Collection<ComponenteCurricular> todosGraduacao) {
		this.todosGraduacao = todosGraduacao;
	}

	public Collection<EquivalenciaEspecifica> getEquivalenciasEspecificas() {
		return equivalenciasEspecificas;
	}

	public void setEquivalenciasEspecificas(
			Collection<EquivalenciaEspecifica> equivalenciasEspecificas) {
		this.equivalenciasEspecificas = equivalenciasEspecificas;
	}

	/**
	 * Seta o nível de ensino do componente curricular.
	 * @param tipo
	 */
	public void setNivel(char tipo) {
		this.obj.setNivel(tipo);
		if( this.componenteBusca != null )
			this.componenteBusca.setNivel( this.obj.getNivel() );
	}

	/**
	 * Seta que a busca por componente deve restringir os componentes curriculares do EAD.
	 * @param ead
	 */
	public void setEad(boolean ead) {
		this.ead = ead;
	}

	/**
	 * Retorna a coordenação atual do curso.
	 * 
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente/lista.jsp</li>
	 * </ul>
	 */
	@Override
	public Curso getCursoAtualCoordenacao() {
		if (ead) {
			OperacoesCoordenadorGeralEadMBean bean = (OperacoesCoordenadorGeralEadMBean) getMBean("opCoordenadorGeralEad");
			return bean.getCurso();
		} else {
			return super.getCursoAtualCoordenacao();
		}
	}

	/**
	 * Retorna o nível de ensino do componente curricular.
	 * @return
	 * @throws NegocioException 
	 */
	public String getPopularNivel() throws NegocioException {
		
		if(getUsuarioLogado() == null) {

			String nivel = getParameter("nivel");
			if(nivel != null){
				setNivel(nivel.charAt(0));
			}
			
		}else{
			obj.setNivel(getNivelEnsino());
			filtroNivel = true;
			if (obj.getNivel() == ' ') {
				if (isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO)) {
					obj.setNivel(NivelEnsino.STRICTO);
					obj.setNumUnidades(1);
				} else if (isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.COORDENADOR_CURSO)){ 
					obj.setNivel(NivelEnsino.GRADUACAO);
				} else if (isUserInRole(SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA) && SigaaSubsistemas.COMPLEXO_HOSPITALAR.equals(getSubSistema()) ){
					obj.setNivel(NivelEnsino.RESIDENCIA);
				} else if (isUserInRole(SigaaPapeis.GESTOR_TECNICO) && SigaaSubsistemas.TECNICO.equals(getSubSistema())){
					obj.setNivel(NivelEnsino.TECNICO);
				} else if (isUserInRole(SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR) && SigaaSubsistemas.FORMACAO_COMPLEMENTAR.equals(getSubSistema())){
					obj.setNivel(NivelEnsino.FORMACAO_COMPLEMENTAR);
				}
				else
					throw new NegocioException("Não foi possível identificar o Nível de Ensino.");
			}
		}
		return "";
	}

	/**
	 * Parâmetro utilizado na consulta pública de componentes curriculares.
	 * @return
	 */
	public String getConsultaPublica() {
		return "public";
	}

	/**
	 * Retorna o componente curricular cadastrado utilizado para exibir as informações no comprovante após o cadastro, pois obj já foi limpado.
	 * @return
	 */
	public ComponenteCurricular getComponente() {
		return componente;
	}

	/**
	 * Seta o componente curricular cadastrado utilizado para exibir as informações no comprovante após o cadastro, pois obj já foi limpado.
	 * @param componente
	 */
	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	/**
	 * Seta a lista de componentes curriculares encontrados na busca por componentes curriculares. 
	 * @param componentes
	 */
	public void setComponentes(Collection<ComponenteCurricular> componentes) {
		this.componentes = componentes;
	}

	/**
	 * Retorna a eubUnidade do componente curricular.
	 * @return
	 */
	public ComponenteCurricular getSubUnidade() {
		return subUnidade;
	}

	/**
	 * Seta a eubUnidade do componente curricular. 
	 * @param subUnidade
	 */
	public void setSubUnidade(ComponenteCurricular subUnidade) {
		this.subUnidade = subUnidade;
	}

	/**
	 * Indica que a busca deve listar o resultado na forma de relatório. 
	 * @return
	 */
	public boolean isFiltroRelatorio() {
		return filtroRelatorio;
	}

	/**
	 * Seta que a busca deve listar o resultado na forma de relatório. 
	 * @param filtroRelatorio
	 */
	public void setFiltroRelatorio(boolean filtroRelatorio) {
		this.filtroRelatorio = filtroRelatorio;
	}

	/**
	 * Retornar a Data de início da Equivalência
	 * @return the dataInicioEquivalencia
	 */
	public Date getDataInicioEquivalencia() {
		return dataInicioEquivalencia;
	}

	/**
	 * Seta a Data de Início da Equivalência
	 * 
	 * @param dataInicioEquivalencia the dataInicioEquivalencia to set
	 */
	public void setDataInicioEquivalencia(Date dataInicioEquivalencia) {
		this.dataInicioEquivalencia = dataInicioEquivalencia;
	}

	/**
	 * Retornar a Data Fim da Equivalência
	 * 
	 * @return the dataFimEquivalencia
	 */
	public Date getDataFimEquivalencia() {
		return dataFimEquivalencia;
	}

	/**
	 * Seta a Data Fim da Equivalência
	 * 
	 * @param dataFimEquivalencia the dataFimEquivalencia to set
	 */
	public void setDataFimEquivalencia(Date dataFimEquivalencia) {
		this.dataFimEquivalencia = dataFimEquivalencia;
	}

	/**
	 * Indica se o relatório é para impressão ou não. 
	 * 
	 * @return the reportImpressao
	 */
	public boolean isReportImpressao() {
		return reportImpressao;
	}

	/**
	 * Seta o valor para impressão ou não. 
	 * 
	 * @param reportImpressao the reportImpressao to set
	 */
	public void setReportImpressao(boolean reportImpressao) {
		this.reportImpressao = reportImpressao;
	}

	/** 
	 * Indica se o usuário tem permissão para escolher o tipo do componente curricular.
	 * @return
	 */
	public boolean isEscolheTipoComponente() {

		if (isPortalCoordenadorGraduacao())
			return false;
		
		return true;
	}
	
	/**
	 * Retorna para a tela do formulário da busca do componente Curricular.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/ativar_desativar_componente_detalhes.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/tipo_componente.jsp</li>
	 * </ul>
	 */
	public String formBusca() throws DAOException {
		if ( super.isPortalPpg() && obj.isAguardandoConfirmacao() ) {
			AutorizacaoCadastroComponenteMBean mBean = getMBean("autorizacaoComponente");
			return mBean.iniciar();
		} else {
			return forward(JSP_LISTA);
		}
	}
	
	/** 
	 * Indica se o usuário pode alterar a carga horária (ou créditos) do componente curricular.
	 * 
	 * <ul>
	 * 		<li>/SIGAA/app/sigaa.ear/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 */
	public boolean isPermiteAlterarCargaHoraria() {
		
		if (obj.getId() == 0) {	//Criando		 
			return ( (obj.isResidencia() && getAcessoMenu().isComplexoHospitalar())
					|| (obj.isTecnico() && getAcessoMenu().isTecnico())
					|| (obj.isFormacaoComplementar() && getAcessoMenu().isFormacaoComplementar())
					|| isUserInRole(
							SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.CHEFE_DEPARTAMENTO, 
							SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
							SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE,
							SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO,
							SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR,
							SigaaPapeis.COORDENADOR_GERAL_EAD) );
					
		} else { // Alterando
			return ((obj.isStricto() && getAcessoMenu().isStricto()) 
					|| ( obj.isResidencia() && ( getAcessoMenu().isCoordenadorResidenciaMedica() || getAcessoMenu().isGestorResidenciaMedica() ) )
					|| (obj.isGraduacao() && (getAcessoMenu().isAdministradorDAE() || getAcessoMenu().isCdp()))
					|| (obj.isLato() || getAcessoMenu().isLato() || getAcessoMenu().isTecnico()));
		}
		
	}

	/**
	 * Indica se o usuário pode ativar/inativar o componente.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeAtivarComponente() {
		return isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP, SigaaPapeis.PPG,
				SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);
	}
	
	
	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular aceita subTurma.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeAtividadeAceitaTurma() {
		if (obj.getTipoComponente().getId() == 0) return false;
		
		if (obj.isStricto() || obj.isGraduacao())
			return obj.getFormaParticipacao() != null && obj.isPermiteCriarTurma();
		
		return false;
		
		
	}
	
	/**
	 * Indica se será dada, no formulário, para o usuário, a opção de informar a bibliografia do componente curricular.
	 * 
	 * Somente Stricto.
	 * 
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeBibliografia() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return isStricto();
	}
	
	
	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular permite carga horária compartilhada.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeChCompartilhada() {
		if (obj.isGraduacao()) return false;
		if (obj.getTipoComponente().getId() == 0) return false;
		return true;
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o docente terá horário flexível na turma criada para este componente curricular.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeHorarioDocenteFlexivel() {
		return true;
	}
	
	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular permite carga horária dedicada do docente.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeChDedicadaDocente() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isAtividadeComplementar()||(obj.isModulo()&&!isTecnico())||obj.isAtividadeColetiva()|| ( obj.getFormaParticipacao() != null && obj.getFormaParticipacao().isPermiteCHDocente());
	}
	
	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular terá créditos de EAD.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeCrEad() {
		if (obj.getTipoComponente().getId() == 0) return false;
		else return obj.isGraduacao() && obj.isDisciplina();
	}	

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular terá créditos de estágio.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeCrEstagio(){
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.getId() > 0 && obj.getDetalhes().getCrEstagio() > 0; 
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular terá créditos práticos.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeCrPratico() {
		if (obj.getTipoComponente().getId() == 0 || isTecnico()) return false;
		return obj.isDisciplina();
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular terá crédito teórico.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeCrTeorico() {
		if (obj.getTipoComponente().getId() == 0 || isTecnico()) return false;
		else return obj.isDisciplina() && !obj.isDistancia();
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se a ementa do componente curricular.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeEmenta() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isDisciplina() || obj.isModulo() || obj.isAtividade() || obj.isAtividadeColetiva();
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular permite flexibilidade do horário.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeFlexibilidadeHorario(){
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isGraduacao();
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular deverá ter horário de turma.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeHorarioTurma() {
		System.out.println(obj.getTipoComponente().getId());
		if (obj.getTipoComponente().getId() == 0) 
			return false;
		if (obj.isAtividade() && obj.getDetalhes().isAtividadeAceitaTurma())
			return true;
		
		return !obj.isAtividade();
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular é matriculável on-line.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeMatriculavelOnLine() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isPermiteCriarTurma();
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular necessita de orientador.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeNecessitaOrientador() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isAtividadeComplementar();
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular necessita de nota.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibePrecisaNota(){
		
		if (obj.getTipoComponente().getId() == 0)
			return false;
		
		if (isStricto())
			return false;
		else if (!isStricto() && !obj.isAtividade()) 
			return false;
		
		return true;
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se é permitido fazer aproveitamento do componente curricular. 
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeProibeAproveitamento(){
		if (obj.getTipoComponente().getId() == 0) return false;
		return isStricto() && getAcessoMenu().isPpg();
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar a quantidade de avaliações do componente curricular. 
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeQtdAvaliacoes() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return true;
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se o componente curricular permite subturmas.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeSubTurma() {
		if (!obj.isPermiteCriarTurma()) return false;
		if (obj.getTipoComponente().getId() == 0) return false;
		return isGraduacao() && (obj.isDisciplina() || obj.isBloco() || obj.isModuloOuAtividadeColetiva());
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar se pode criar turmas do componente curricular sem solicitação. 
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeTurmaSemSolicitacao() {
		if (obj.getTipoComponente().getId() == 0) return false;
		if (obj.isAtividade() && obj.getDetalhes().isAtividadeAceitaTurma()) return true;
		return obj.isDisciplina() || obj.isBloco();
	}
	
	/** Indica se será dada, no formulário, para o usuário, a opção de informar a carga horária teórica do componente curricular.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeChTeorico() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return (obj.isAtividade() || obj.isBloco() || obj.isModulo() || obj.isAtividadeColetiva() || isTecnico()) && !obj.isDistancia();
	}

	/** Indica se será dada, no formulário, para o usuário, a opção de informar a carga horária prática do componente curricular. 
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeChPratico() {
		if (obj.getTipoComponente().getId() == 0) return false;
		return obj.isAtividade() || obj.isBloco() || obj.isModulo() || obj.isAtividadeColetiva() || isTecnico();
	}
	
	/** Indica se será dada, no formulário, para o usuário, a opção de informar a carga horária de Ensino a Distância(EAD) do componente curricular. 
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeChEad() {
		if (obj.getTipoComponente().getId() == 0) return false;
		if (isExibeCrEad()) return false;
		return !isTecnico() && 
				(obj.isModulo() || (!isEmpty(obj.getFormaParticipacao()) && obj.getFormaParticipacao().isEspecialColetiva()));
	}
	
	/** Indica se será dada, no formulário, para o usuário, a opção de informar a carga horária de Ensino a Distância(EAD) do componente curricular. 
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeChNaoAula() {
		if (obj.getTipoComponente().getId() == 0) return false;
		if (isExibeCrEad()) return false;
		return isGraduacao() && obj.isAtividade();
	}
	
	/** Indica se será dada, no formulário, para o usuário, a opção de informar a carga horária ou número de créditos do componente curricular. 
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isExibeCargaHorariaTotal() {
		return !obj.isBloco();
	}

	/**
	 * Carrega as formas de participação relativas ao tipo de componente informado. <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/sigaa.war/graduacao/componente/tipo_componente.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws ArqException 
	 */
	public Collection<SelectItem> carregarFormaParticipacao(ValueChangeEvent evt) throws ArqException {
		obj.getTipoAtividade().setId((Integer) evt.getNewValue());
		obj.setFormaParticipacao(new FormaParticipacaoAtividade());
		return getFormaParticipacaoCombo();
	}
	
	public String getTituloOperacao() {
		return tituloOperacao;
	}

	public void setTituloOperacao(String tituloOperacao) {
		this.tituloOperacao = tituloOperacao;
	}

	public boolean isSelecionaUnidade() {
		return selecionaUnidade;
	}

	public void setSelecionaUnidade(boolean selecionaUnidade) {
		this.selecionaUnidade = selecionaUnidade;
	}

	public boolean isModoOperador() {
		return modoOperador;
	}

	public void setModoOperador(boolean modoOperador) {
		this.modoOperador = modoOperador;
	}

	/** Indica se o componente está sendo cadastrado ou está sendo solicitado o cadastro.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * <li>/sigaa.war/graduacao/componente/autorizacao/solicitados.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isSolicitacaoCadastroComponente() {
		return ( isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO) && isPortalDocente() )
			|| ( isUserInRole(SigaaPapeis.SECRETARIA_DEPARTAMENTO) && isPortalGraduacao() )
			|| ( isPortalCoordenadorGraduacao() && isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO) )
			|| ( isPortalCoordenadorStricto() && isUserInRole(SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_COORDENACAO) );
	}
	
	/**
	 * Método responsável por indicar se o código do componente curricular será validado, 
	 * com relação a sua estrutura literal. 
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/graduacao/componente/geral.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public boolean isValidaCodigo() throws DAOException{
		ParametrosGestoraAcademica parametros = getParametrosAcademicos();
		if (isStricto())
			return ( ParametroHelper.getInstance().getParametroBoolean(ParametrosStrictoSensu.VALIDAR_CODIGO_COMPONENTE_CURRICULAR) &&
					 parametros.isValidaQtdLetrasCodigo() );
		else if (isGraduacao())
			return ( ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.VALIDAR_CODIGO_COMPONENTE_CURRICULAR_ENSINO) &&
			 parametros.isValidaQtdLetrasCodigo() );
		else if (isTecnico()) {
			return (ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.VALIDAR_CODIGO_COMPONENTE_CURRICULAR_ENSINO) 
					&& obj.getId() == 0);
		} else if (isFormacaoComplementar())
			return ( ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.VALIDAR_CODIGO_COMPONENTE_CURRICULAR_ENSINO) &&
					 parametros.isValidaQtdLetrasCodigo() );
		else			
			return true;
	}
	
	
	/**
	 * 
	 * Indica se é para mostrar ou não opção de marcar um componente curricular 
	 * como conteúdo variável.
	 * 
	 * Chamado por:
	 * sigaa.war/graduacao/componente/geral.jsp
	 * 
	 * @return
	 */
	public boolean isMostrarConteudoVariavel() {
		return  NivelEnsino.isAlgumNivelStricto(getNivelEnsino());
	}
	
	
	/**
	 * Método responsável por indicar se o componente a ser cadastrado refere-se a um curso Novo.
	 * <br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/geral.jsp
	 * @return
	 */
	public boolean isCursoNovo(){
		return obj.getCursoNovo() != null;
	}
	
	/**
	 * Método responsável por verificar se o usuário tem permissão para alterar a forma 
	 * de participação de um componente, em um movimento de alteração.
	 * <br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/tipo_componente.jsp
	 * @return
	 */
	public boolean isNaoPermiteAlterarFormaParticipacao(){
		return obj.getId() != 0 && !isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && (isReadOnly() && !obj.isStricto() && !obj.isResidencia());
	}
	
	/**
	 * Método responsável por verificar se o usuário tem permissão para alterar a forma 
	 * de participação de um componente, em um movimento de alteração.
	 * <br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/tipo_componente.jsp
	 * @return
	 * @throws DAOException 
	 */
	public boolean isNaoPermiteAlterarTipoComponente() throws DAOException{
		if (naoPermiteAlterarTipoComponente == null) {
			if (obj.getId() != 0){
				int qtdeTurmasByComponente = getQtdTurmasComponente();
				if (qtdeTurmasByComponente > 0)  {
					if (isUserInRole(SigaaPapeis.ADMINISTRADOR_STRICTO)) {
						naoPermiteAlterarTipoComponente = false;
					} else {
						naoPermiteAlterarTipoComponente = true;
					}
				} else {
					naoPermiteAlterarTipoComponente = obj.getId() != 0 && !isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.ADMINISTRADOR_STRICTO) && (isReadOnly() && !obj.isStricto());
				}
				// Fora o trabalho final de curso, só é permitido componentes do tipo módulo em Lato.
				if (obj.isLato())
					naoPermiteAlterarTipoComponente = true;
			} else {
				naoPermiteAlterarTipoComponente = false;
			}
		}
		return naoPermiteAlterarTipoComponente;
	}

	/** Retorna a quantidade de turmas do componente curricular
	 * @return
	 * @throws DAOException
	 */
	private int getQtdTurmasComponente() throws DAOException {
		int qtdeTurmasByComponente;
		TurmaDao dao = getDAO(TurmaDao.class);
		int[] situacoes = new int[SituacaoTurma.getSituacoesValidas().size()];
		int i = 0; 
		for (SituacaoTurma st : SituacaoTurma.getSituacoesValidas()) {
			situacoes[i] = st.getId();
			i++;
		}
		qtdeTurmasByComponente = dao.countTurmasByComponente(obj, situacoes);
		return qtdeTurmasByComponente;
	}
	
	/**
	 * Método responsável por verificar se o usuário tem permissão para alterar a forma 
	 * de participação de um componente, em um movimento de alteração.
	 * <br/><br/>
	 * 
	 * Método chamado pela seguinte JSP: /sigaa.war/graduacao/componente/tipo_componente.jsp
	 * @return
	 */
	public boolean isNaoPermiteAlterarTipoAtividade(){
		return obj.getId() != 0 && !isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && (isReadOnly() && !obj.isStricto() && !obj.isResidencia());
	}
	
	
	public boolean isPermiteAlterarNome() {
		return ( obj.isGraduacao() && !isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE) && isReadOnly() ) || 
				 ( isPortalCoordenadorStricto() && !isUserInRole(SigaaPapeis.PPG) && !isCoordenadorOpcaoCadastrarComponenteCurricular()
						 && !isSolicitacaoCadastroComponente() );
	}	
	
	/**
	 * Método responsável por validar a alteração da forma de participação do componente, 
	 * informando ao usario se o componente possui alunos matriculados.
	 * */
	private void validarAlteracaoFormaParticipacao() throws DAOException{
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		if (obj.getId() > 0 && obj.getFormaParticipacao() != null){
			ComponenteCurricular componenteBD = getGenericDAO().findByPrimaryKey(obj.getId(), ComponenteCurricular.class);
			Long totalAlunosMatriculados = dao.findTotalJaMatriculadosByComponente(obj.getId());
			int idFormaParticipacaoBD = componenteBD.getFormaParticipacao() != null ? componenteBD.getFormaParticipacao().getId() : 0;
			if (idFormaParticipacaoBD != obj.getFormaParticipacao().getId() && totalAlunosMatriculados > 0){
				addMensagemWarning("Atenção ao alterar a forma de participação do componente curricular, pois existem alunos com matrículas no mesmo.");
			}
				
		}
	}
	
	/**
	 * Seta o Novo Curso.
	 * @param cursoNovo
	 */
	public void setCursoNovo(boolean cursoNovo) {
	}

	public String getTextoSuggestionBox() {
		return textoSuggestionBox;
	}

	public void setTextoSuggestionBox(String textoSuggestionBox) {
		this.textoSuggestionBox = textoSuggestionBox;
	}

	public Collection<ComponenteDetalhes> getListaDetalhes() {
		return listaDetalhes;
	}

	public void setListaDetalhes(Collection<ComponenteDetalhes> listaDetalhes) {
		this.listaDetalhes = listaDetalhes;
	}

	public boolean isSelecionaNivelEnsino() {
		return selecionaNivelEnsino;
	}
	
	public ComponenteCurricular getComponenteBusca() {
		return componenteBusca;
	}

	public void setComponenteBusca(ComponenteCurricular componenteBusca) {
		this.componenteBusca = componenteBusca;
	}

	/**
	 * Retorna uma lista com o histórico de equivalências de um componente curricular, informando as expressões
	 * que já foram usadas como equivalência, quais estão ativas e o período de vigência de cada uma.
	 *
 	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 *	 <li>/SIGAA/app/sigaa.ear/sigaa.war/geral/componente_curricular/include/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 */
	public List<Object[]> getEquivalenciasComponenteEscolhido() throws DAOException {
		return getDAO(ComponenteCurricularDao.class).buscarHistoricoEquivalenciasComponente(obj.getId());
	}

	/** Retorna a coleção de subunidades da turma. 
	 * @return
	 */
	public Collection<ComponenteCurricular> getSubunidades() {
		return subunidades;
	}

	/** Seta a coleção de subunidades da turma.
	 * @param subunidades
	 */
	public void setSubunidades(Collection<ComponenteCurricular> subunidades) {
		this.subunidades = subunidades;
	}

	public boolean isDefineCodigoSubUnidadeAutomaticamente() {
		return defineCodigoSubUnidadeAutomaticamente;
	}

	public void setDefineCodigoSubUnidadeAutomaticamente(
			boolean defineCodigoSubUnidadeAutomaticamente) {
		this.defineCodigoSubUnidadeAutomaticamente = defineCodigoSubUnidadeAutomaticamente;
	}

	public StringBuilder getCodigoSugerido() {
		return codigoSugerido;
	}

	public boolean isFiltroModalidade() {
		return filtroModalidade;
	}

	public void setFiltroModalidade(boolean filtroModalidade) {
		this.filtroModalidade = filtroModalidade;
	}

}