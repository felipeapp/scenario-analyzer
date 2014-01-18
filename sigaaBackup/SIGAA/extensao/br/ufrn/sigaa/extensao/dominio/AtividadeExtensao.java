/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/10/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioAcaoExtensao;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;
import br.ufrn.sigaa.projetos.dominio.ArquivoProjeto;
import br.ufrn.sigaa.projetos.dominio.AutorizacaoDepartamento;
import br.ufrn.sigaa.projetos.dominio.ClassificacaoFinanciadora;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.FotoProjeto;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.SolicitacaoReconsideracao;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/*******************************************************************************
 * <p>
 * Representa uma a��o de extens�o. <br>
 * 
 * Uma a��o de extens�o pode ser considerada como (a) uma atividade formadora,
 * vinculada ao processo pedag�gico do aluno; (b) coordenada por docente e (c)
 * com contato direto com a sociedade. <br>
 * As A��es de Extens�o s�o aquelas que envolvem professores, alunos e
 * servidores t�cnicos-administrativos e que se enquadrem nas modalidades:
 * Programas, Projetos, Cursos, Eventos, Produtos e Presta��o de servi�os. <br>
 * Toda proposta de a��o de extens�o deve ter obrigatoriamente um Coordenador,
 * que dever� ser professor do quadro permanente da UFRN, lotado em Departamento
 * Acad�mico ou Unidade Acad�mica Especializada da UFRN, nos termos do Estatuto
 * e do Regimento Geral da UFRN (Art. 3 da Res. 0702004 - CONSEPE). <br>
 * Cada professor s� poder� coordenar, simultaneamente, duas a��es de extens�o
 * da mesma modalidade (Art 4o da Res. 070/2004 - CONSEPE). <br>
 * As propostas devem conter o registro da equipe respons�vel pela realiza��o da
 * a��o, com explicita��o das fun��es de cada participante, bem como da carga
 * hor�ria a ser cumprida pelos membros (Art 5o da Res. 070/2004 - CONSEPE).
 * <br>
 * No caso da equipe respons�vel pela realiza��o da a��o contar com servidores
 * lotados em Unidade(s) da UFRN distinta(s) daquela em que esta lotado o
 * Coordenador ou em �rg�os externos da universidade, dever� constar do processo
 * a concord�ncia expressa do(s) dirigente(s) da(s) outra(s) Unidade(s)
 * envolvida(s) (Art 6o da Res. 070/2004 - CONSEPE). <br>
 * Uma a��o de extens�o herda caracter�sticas de public.Projeto
 * </p>
 * 
 * @author Gleydson
 * @author Victor Hugo
 * @author Ilueny Santos
 * 
 ******************************************************************************/

@Entity
@Table(schema = "extensao", name = "atividade")
public class AtividadeExtensao implements Validatable {

//	/** Atributo utilizado para identificar o tipo de a��o. */
//    public static final int PROGRAMA = 1;
//    /** Atributo utilizado para identificar o tipo de a��o. */
//    public static final int PROJETO = 2;
//    /** Atributo utilizado para identificar o tipo de a��o. */
//    public static final int CURSO = 3;
//    /** Atributo utilizado para identificar o tipo de a��o. */
//    public static final int EVENTO = 4;
//    /** Atributo utilizado para identificar o tipo de a��o. */
//    public static final int PRESTACAO_DE_SERVICO = 5;
//    /** Atributo utilizado para identificar o tipo de a��o. */
//    public static final int PRODUTO = 6;

    /** intervalo de dias para envio de email pelos coordenadores  */
    public static final  int INTERVALO_DIAS_PERMITIR_EMAIL = 10;
    
    /** identificador da a��o */
    @Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="extensao.atividade_extensao_sequence") })
	@Column(name = "id_atividade", unique = true, nullable = false)
    private int id;

    /** Projeto associado � atividade. Cont�m os dados b�sicos da A��o de Extens�o. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto")
    private Projeto projeto = new Projeto(new TipoProjeto(TipoProjeto.EXTENSAO));

    /** Data de envio da Atividade para as unidades envolvidas.  */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_envio")
    private Date dataEnvio;

    /** Indica se a a��o esta vinculada a um programa de extens�o. */
    @Column(name = "vinculo_programa_extensao")
    private boolean vinculoProgramaExtensao = false;

    /** P�blico estimado da A��o de Extens�o */
    @Column(name = "publico_interno")
    private Integer publicoInterno;

    /** P�blico Externo estimado da A��o de Extens�o */
    @Column(name = "publico_externo")
    private Integer publicoExterno;
    
    /** N�mero de bolsas concedidas para a A��o de Extens�o. */
    @Column(name = "bolsas_concedidas")
    private Integer bolsasConcedidas = 0;

    /** N�mero de bolsas solicitadas pelo coordenador da A��o de Extens�o; */
    @Column(name = "bolsas_solicitadas")
    private Integer bolsasSolicitadas = 0;

    /** Total estimado de discentes envolvidos na execu��o da atividade. */
    @Column(name = "total_discentes")
    private Integer totalDiscentes = 0;

    /** Total de pessoas atendidas com a execu��o da a��o de extens�o. */
    @Column(name = "publico_atendido")
    private Integer publicoAtendido;

    /** Referente aos tipos de p�blico alvo atingido pela a��o de extens�o.*/
    @Column(name = "publico_alvo")
    private String publicoAlvo;
    
    /** Referente aos tipos de p�blico alvo Externo atingido pela a��o de extens�o.*/
    @Column(name = "publico_alvo_externo")
    private String publicoAlvoExterno;
    
    /** Caso o evento/curso seja detalhado em SubAtividadeExtensao permite informar o n�mero m�ximo de 
     * atividades permitidas. Se for NULO deixar se inscrever em quantos quiser. */
    @Column(name = "max_sub_atividades_permitidas_inscricao")
    private Integer maxSubAtividadesPermitidasInscricao = null;

    /**
     * True se a a��o for do tipo permanente. A��es permanente s�o a��es j�
     * consolidadas junto a comunidade e que sempre s�o submetidas para renova��o
     * todos os anos. (Grupo Permanente de Arte e Cultura)
     */
    @Column(name = "permanente", nullable = false)
    private Boolean permanente = false;

    /** Informa se a a��o de extens�o possui algum tipo de conv�nio com a FUNPEC. */
    @Column(name = "convenio_funpec", nullable = false)
    private Boolean convenioFunpec = false;

    /** 
     * Informa se a a��o de extens�o possui algum financiamento externo nos termos publicados no edital.
     * A��es deste tipo, normalmente, possuem recursos de valores muito elevados e s�o tratadas de forma
     * diferenciada pela equipe de avaliadores da proposta. 
     */
    @Column(name = "financ_externo_especial", nullable = false)
    private boolean financExternoEspecial = false;

    /** 
     * Permite que o docente informe se esta a��o recebeu bolsa de algum financiamento externo.
     * Utilizado pela comiss�o de avalia��o para determinar a concess�o de novas bolsas.
     */
    @Column(name = "bolsa_financ_externo_especial")
    private Boolean bolsaFinancExternoEspecial = false;
    
    
    
    /** Notificacao de cobranca de relatorio s� poder� ser enviada
       apos X dias da ultima data de cobranca do relatorio final. */
    @Column(name = "data_cobranca_relatorio_final")
    private Date dataEmailCobrancaRelatorioFinal;
	
    
    /**Campo utilizado na gera��o do c�digo da a��o de extens�o.
       A contagem � feita por ano e por tipo de a��o. */
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    		parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "sequencia", unique = true, nullable = false)
    private int sequencia;

    /** Principal �rea de atua��o da a��o de extens�o. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_area_tematica_principal", referencedColumnName = "id_area_tematica")
    private AreaTematica areaTematicaPrincipal = new AreaTematica();

    /** Em caso de renova��o, relaciona esta com a a��o renovada. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atividade_renovada")
    private AtividadeExtensao atividadeRenovada;

    /** Local de realiza��o da a��o de extens�o.
        Ex. Audit�rio da Reitoria da UFRN. Ver tabela extensao.local_realizacao */
    @Transient
    private LocalRealizacao localRealizacao = new LocalRealizacao();

    /**Lista de locais da realiza��o da a��o de extens�o*/
    @OneToMany(mappedBy = "atividade")
	private List<LocalRealizacao> locaisRealizacao = new ArrayList<LocalRealizacao>();
	
    /** Representa o tipo de A��o de Extens�o. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_atividade_extensao")
    private TipoAtividadeExtensao tipoAtividadeExtensao = new TipoAtividadeExtensao();

    /** Edital ao qual a atividade est� associada. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_edital")
    private EditalExtensao editalExtensao = new EditalExtensao();

    /** Linha de Atua��o ao qual a atividade est� associada. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_linha_atuacao")
    private EditalExtensaoLinhaAtuacao linhaAtuacao = new EditalExtensaoLinhaAtuacao();
    
    /** Uma a��o de extens�o � um projeto. Para obter dados mais gerais
       da a��o de extens�o veja a tabela projetos.projeto */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto_extensao")
    private ProjetoExtensao projetoExtensao;

    /** Curso. Se a a��o for do tipo curso esta coluna informa o id do curso. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_curso_evento")
    private CursoEventoExtensao cursoEventoExtensao;

    /** Se a a��o for do tipo programa esta coluna informa o id do programa.     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_programa")
    private ProgramaExtensao programaExtensao;

    /** Se a a��o for do tipo produto esta coluna informa o id do produto.   */ 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_produto")
    private ProdutoExtensao produtoExtensao;
    
    /** 
     * Determina qual a situa��o desta a��o de extens�o.
     * Verificar que esta situa��o deve estar sincronizada com a situa��o de
     * projeto. As situa��es podem ser diferentes somente nos casos de A��es 
     * Acad�micas Associadas.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_situacao_projeto")
    private TipoSituacaoProjeto situacaoProjeto = new TipoSituacaoProjeto();


    /** Outras unidades envolvidas na execu��o da A��o. */
    @OneToMany(mappedBy = "atividade")
    private Collection<AtividadeUnidade> unidadesProponentes = new ArrayList<AtividadeUnidade>();

    /** Representa os grupo de pesquisa ao qual a a��o de extens�o est� vinculada.
     Propostas de a��es de tipo projeto normalmente tem v�nculo com algum grupo de
     pesquisa j� existente. */
    @OneToMany(mappedBy = "atividade")
    private Collection<AtividadeGrupoPesquisa> atividadeGruposPesquisa;

    /** Relaciona uma A��o de Extens�o com todas as suas entidades financiadoras. */
    @OneToMany(mappedBy = "atividade")
    private Collection<AtividadeEntidadeFinanciadora> atividadeEntidadesFinanciadoras;

    /** epresenta a autoriza��o dada pelo chefe de departamento que
    	participa da a��o de extens�o atrav�s de seus docentes/t�cnicos
    	administrativos. */ 
    @OneToMany(mappedBy = "atividade")
    private Collection<AutorizacaoDepartamento> autorizacoesDepartamentos = new ArrayList<AutorizacaoDepartamento>();
    
    /** SubAtividades da AtividadeExtencao. Pode ser preenchida quando a atividade for um curso ou evento  */
    @OneToMany(mappedBy = "atividade")
    private Collection<SubAtividadeExtensao> subAtividadesExtensao = new ArrayList<SubAtividadeExtensao>();

    /** Representa um plano de trabalho de um Discente de extens�o. */
    @OneToMany(mappedBy = "atividade")
    @OrderBy("dataCadastro ASC")
    private Collection<PlanoTrabalhoExtensao> planosTrabalho = new ArrayList<PlanoTrabalhoExtensao>();

    /** Avalia��es da A��o de Extens�o pelos membros da PROEx ou por docentes nomeados pela PROEx.*/
    @OneToMany(mappedBy = "atividade")
    private Collection<AvaliacaoAtividade> avaliacoes = new ArrayList<AvaliacaoAtividade>();

    /** Representa uma inscri��o de um discente para a sele��o de uma a��o de extens�o. */
    @OneToMany(mappedBy = "atividade")
    private Collection<InscricaoSelecaoExtensao> inscricoesSelecao = new ArrayList<InscricaoSelecaoExtensao>();

    
    /** Discentes participantes da A��o de Extens�o. */
    @OneToMany(mappedBy = "atividade")
    private Collection<DiscenteExtensao> discentesSelecionados = new ArrayList<DiscenteExtensao>();


    /** Relat�rios da A��o de Extens�o(Final ou Parcial).*/
    @OneToMany(mappedBy = "atividade")
    @OrderBy(value = "tipoRelatorio")
    private Collection<RelatorioAcaoExtensao> relatorios = new ArrayList<RelatorioAcaoExtensao>();

    /** Os participantes s�o, por exemplo, os alunos de um curso de extens�o, ou os
        inscritos de um evento. P�blico alvo atendido.
    	Estes participantes s�o origin�rios de inscri��es on-line e de cadastros realizados pelo pr�prio coordenador. */
    @OneToMany(mappedBy = "atividadeExtensao")
    private Collection<ParticipanteAcaoExtensao> participantes = new ArrayList<ParticipanteAcaoExtensao>();

    /** Atividades vinculadas a atividade
    	Ex. todos os projetos, cursos e eventos que formam o programa... */
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AtividadeExtensao.class) 
    @JoinTable(name = "extensao.atividade_atividade", joinColumns = { @JoinColumn(name = "id_atividade_pai") }, inverseJoinColumns = { @JoinColumn(name = "id_atividade_filha") })
    private Collection<AtividadeExtensao> atividades = new ArrayList<AtividadeExtensao>();

    /** Lista de atividades que vinculam a atividade como filha,
     	geralmente programas
     	Ex. todos os programas dos quais o projeto faz parte */
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AtividadeExtensao.class)
    @JoinTable(name = "extensao.atividade_atividade", joinColumns = { @JoinColumn(name = "id_atividade_filha") }, inverseJoinColumns = { @JoinColumn(name = "id_atividade_pai") })
    private Collection<AtividadeExtensao> atividadesPai = new ArrayList<AtividadeExtensao>();

    /** Utilizado para verificar se o usu�rio est� apenas registrando uma
   		atividade antiga (atualizando a base de dados).
     	Nestes casos, as valida��es para o simples registro de atividades s�o
     	diferentes. */
    @Column(name = "registro")
    private boolean registro = false;
    
	/** Arquivo do documento da atividade de extens�o no SIGED */
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
	/** Token de seguran�a gerado para possibilitar a manipula��o do documento no SIGED */
	@Column(name="security_token")
	private String securityToken;

	/** Armazena o programa Estrat�gico de Extens�o */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_programa_estrategico")
    private ProgramaEstrategicoExtensao programaEstrategico = new ProgramaEstrategicoExtensao();

	/** Fundamenta��o Te�rica */
	@Column(name = "fundamentacao_teorica")
	private String fundamentacaoTeorica;
    
    /** 
     * Determina se esta a��o de extens�o est� ativa.
     * Este campo � utilizado no controle de exclus�o l�gica do sistema.
     * Verificar que este campo deve ser sincronizado com o campo ativo de projeto.
     * Somente diverg�ncias poss�veis somente nos casos de A��es Acad�micas Associadas.
     */
    private boolean ativo = true;
    
    

    /** Informa de a a��o � v�nculada a um Programa Estrat�gico */
    @Column(name = "vinculado_programa_estrategico", nullable = false)
    private boolean vinculadoProgramaEstrategico;

    /** Informa se a a��o � continua��o  */
    @Column(name = "projeto_formacao_continuada", nullable = false)
    private boolean projetoVinculadoFormacaoContinuada;
    
    /**
     *  � estranho mas, onde tem objetivos leia-se atividade.
     *  S�o as atividades a serem realizadas pelos membros para essa a��o.
     *  
     *  Por exemplo, um membro tem que, supervisionar algu�m, depois escrever o relat�rio, depois entregar o relat�rio, etc...
     */
	@OneToMany(mappedBy = "atividadeExtensao", fetch = FetchType.LAZY)
	private Collection<Objetivo> objetivo = new HashSet<Objetivo>();

    /** Informa se a a��o � financiada pela unidade PROEX */
    @Column(name = "financ_proex", nullable = false)
    private boolean financProex = false;

    /** Se o edital tem financiamento externo.
     * 
     * Nesse caso n�o precisa indicar a fonte do financiamento.
     */
    @Column(name = "financ_externo_edital", nullable = false)
    private boolean financExternoEdital = false;

    /** Informa se a a��o � financiada pela unidade proponente */
    @Column(name = "financ_unidade_proponente", nullable = false)
    private boolean financUnidProponente = false;

    /** Guarda a unidade executara da atividade quando n�o � uma unidade da institui��o. */
    @Column(name = "unidade_executora_externa")
    private String unidadeExecutoraExterna;
    
    /** N�o sei o que significa isso.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_executor_financeiro")
	private ExecutorFinanceiro executorFinanceiro = new ExecutorFinanceiro();
    
	
	 /** Utilizado na view para sele��o de a��es vinculadas a programas e a��es associadas.*/
    @Transient
    private boolean selecionado = false;


    /** Representa o total vagaras que fora abertas. 
	 *  Ou seja O somat�rio do n�mero de vagas de todas as inscri��es abertas.
	 * 
	 *  Esse n�mero � calculado "on line"
	 *  
	 */
	@Transient
	private int numeroVagasAbertas;
	
	
	/** Representa o total de inscritos e APROVADOS pela coordena��o do curso/evento.
	 * 
	 *  Esse n�mero � calculado "on line"
	 */
	@Transient
	private int numeroInscritos;
	
	
	
	/** Representa o total de participantes para essa atividade.
	 * 
	 *  Esse n�mero � calculado "on line"
	 */
	@Transient
	private int numeroParticipantes;
	
	
    
    /** Disponibiliza uma String com as notas de todas as avalia��es realizadas.*/
    @Transient
    private String notasAvaliacoes;
    
    /** Utilizado para verificar se esta a��o est� sendo editada por um gestor de extens�o. */
    @Transient
    private boolean edicaoGestor = false; 
    
    
    /** Utilizado para auxiliar apresenta��o de vagas em mini curso e mini eventos */
    @Transient
    private SubAtividadeExtensao subAtividade;
	
	
	
	
	
    public AtividadeExtensao() {
    }

    public AtividadeExtensao(int id) {
    	setId(id);
    }

    public AtividadeExtensao(int id, String titulo, int ano) {
    	setId(id);
    	getProjeto().setTitulo(titulo);
    	getProjeto().setAno(ano);
    }

    public AtividadeExtensao(Integer id, String titulo, Unidade unidade, Collection<MembroProjeto> membrosEquipe) {
    	setId(id);
    	getProjeto().setTitulo(titulo);
    	getProjeto().setUnidade(unidade);
    	setMembrosEquipe(membrosEquipe);
    }

    
    
    @Override
	public String toString() {
		return "AtividadeExtensao [" +  (projeto != null ? projeto.getTitulo(): " ") + "]";
	}

	/** Utilizado na compara��o de 2 a��es de extens�o.*/
    @Override
    public boolean equals(Object obj) {
    	return EqualsUtil.testEquals(this, obj, "id");
    }
    
    /** Determina a identidade de uma A��o de Extens�o. Utilizado na compra��o de objetos.*/
    @Override
    public int hashCode() {
    	return HashCodeUtil.hashAll(getId());
    }

    /**
     * Adiciona um MembroProjeto a lista de membros do projeto
     * 
     * @param membro
     * @return
     */
    public boolean addMembroEquipe(MembroProjeto membro) {
	membro.setProjeto(this.getProjeto());
	if (membro.isCoordenador()) {
	    getProjeto().setCoordenador(membro);
	}	    
	return getProjeto().getEquipe().add(membro);
    }

    /**
     * Adiciona um OrcamentoDetalhado a lista de or�amentos da atividade
     * 
     * @param orcamento
     * @return
     */
    public boolean addOrcamentoDetalhado(OrcamentoDetalhado orcamentoDetalhado) {
	orcamentoDetalhado.setProjeto(getProjeto());
	return getProjeto().getOrcamento().add(orcamentoDetalhado);
    }

    

    /**
     * Adiciona uma unidade proponente a atividade sem permitir a inclus�o de
     * unidades repetidas
     * 
     * @param atividadeUnidade
     * @return
     */
    public boolean addUnidadeProponente(AtividadeUnidade atividadeUnidade) {
	atividadeUnidade.setAtividade(this);
	if (!unidadesProponentes.contains(atividadeUnidade))
	    return unidadesProponentes.add(atividadeUnidade);
	else
	    return false;
    }

    /**
     * Adiciona uma sub atividade � atividade de extensao
     *
     * @param sub
     */
    public void addSubAtividade(SubAtividadeExtensao sub) {
    	if(this.subAtividadesExtensao == null)
    		this.subAtividadesExtensao  = new ArrayList<SubAtividadeExtensao>();
    	this.subAtividadesExtensao.add(sub);
	}
    

    public AreaTematica getAreaTematicaPrincipal() {
	return areaTematicaPrincipal;
    }

    public void setAreaTematicaPrincipal(AreaTematica areaTematicaPrincipal) {
	this.areaTematicaPrincipal = areaTematicaPrincipal;
    }

    public java.util.Collection<br.ufrn.sigaa.extensao.dominio.AtividadeEntidadeFinanciadora> getAtividadeEntidadesFinanciadoras() {
	return atividadeEntidadesFinanciadoras;
    }

    public void setAtividadeEntidadesFinanciadoras(
	    java.util.Collection<br.ufrn.sigaa.extensao.dominio.AtividadeEntidadeFinanciadora> atividadeEntidadesFinanciadoras) {
	this.atividadeEntidadesFinanciadoras = atividadeEntidadesFinanciadoras;
    }

    public java.util.Collection<br.ufrn.sigaa.extensao.dominio.AtividadeGrupoPesquisa> getAtividadeGruposPesquisa() {
	return atividadeGruposPesquisa;
    }

    public void setAtividadeGruposPesquisa(
	    java.util.Collection<br.ufrn.sigaa.extensao.dominio.AtividadeGrupoPesquisa> atividadeGruposPesquisa) {
	this.atividadeGruposPesquisa = atividadeGruposPesquisa;
    }

    public CursoEventoExtensao getCursoEventoExtensao() {
	return cursoEventoExtensao;
    }

    public void setCursoEventoExtensao(CursoEventoExtensao cursoEventoExtensao) {
	this.cursoEventoExtensao = cursoEventoExtensao;
    }

    public LocalRealizacao getLocalRealizacao() {
	return localRealizacao;
    }

    public void setLocalRealizacao(LocalRealizacao localRealizacao) {
	this.localRealizacao = localRealizacao;
    }


    public Collection<OrcamentoDetalhado> getOrcamentosDetalhados() {
	return getProjeto().getOrcamento();
    }

    /**
     * M�todo utilizado para setar os or�amentos detalhados
     * @param orcamentosDetalhados
     */
    public void setOrcamentosDetalhados(Collection<OrcamentoDetalhado> orcamentosDetalhados) {
	this.getProjeto().setOrcamento(orcamentosDetalhados);
    }

    public ProdutoExtensao getProdutoExtensao() {
	return produtoExtensao;
    }

    public void setProdutoExtensao(ProdutoExtensao produtoExtensao) {
	this.produtoExtensao = produtoExtensao;
    }

    public ProgramaExtensao getProgramaExtensao() {
	return programaExtensao;
    }

    public void setProgramaExtensao(ProgramaExtensao programaExtensao) {
	this.programaExtensao = programaExtensao;
    }

    public ProjetoExtensao getProjetoExtensao() {
	return projetoExtensao;
    }

    public void setProjetoExtensao(ProjetoExtensao projetoExtensao) {
	this.projetoExtensao = projetoExtensao;
    }

    public Integer getPublicoAtendido() {
	return publicoAtendido;
    }

    public void setPublicoAtendido(Integer publicoAtendido) {
	this.publicoAtendido = publicoAtendido;
    }

    public Integer getPublicoEstimado() {
	return publicoInterno;
    }

    public void setPublicoEstimado(Integer publicoEstimado) {
	this.publicoInterno = publicoEstimado;
    }

    public TipoAtividadeExtensao getTipoAtividadeExtensao() {
	return tipoAtividadeExtensao;
    }

    public void setTipoAtividadeExtensao(
	    TipoAtividadeExtensao tipoAtividadeExtensao) {
	this.tipoAtividadeExtensao = tipoAtividadeExtensao;
    }

    
    /***
     * Retorna de uma meneira muito errada as atividades ativas vinculada � essa atividade.
     *
     * @return
     */
    public Collection<AtividadeExtensao> getAtividades() {
		// removendo os inativos da lista..
		if (atividades!= null) {
		    for (Iterator<AtividadeExtensao> it = atividades.iterator(); it.hasNext();) {
			if (!it.next().isAtivo()) {
				it.remove();
			}
		    }
		}
	    return atividades;
    }

    public void setAtividades(Collection<AtividadeExtensao> atividades) {
	this.atividades = atividades;
    }

    public EditalExtensao getEditalExtensao() {
	return editalExtensao;
    }

    public void setEditalExtensao(EditalExtensao editalExtensao) {
	this.editalExtensao = editalExtensao;
    }
    
    /**
     * Informa quais s�o as regras do edital as quais esta a��o deve estar subordinada. 
     * 
     * @return
     */
    public EditalExtensaoRegra getRegrasEdital() {
	if (!ValidatorUtil.isEmpty(editalExtensao)) {
	    return editalExtensao.getRegraByTipo(tipoAtividadeExtensao);	    
	}
	return null;
    }

    /**
     * Utilizado pelo grupo permanente. Grupo permanente � um tipo de a��o de
     * extens�o bastante regular que ocorre j� a muito tempo. � um tipo de a��o
     * j� consolidada.
     * 
     * @return
     */
    public Boolean getPermanente() {
	return this.permanente;
    }

    public void setPermanente(Boolean permanente) {
	this.permanente = permanente;
    }

    /**
     * Retorna o nome da fonte de financiamento da a��o de extens�o
     * 
     * @return
     */
    public String getFonteFinanciamentoString() {

    	StringBuffer result = new StringBuffer();

    	if (this.isFinanciamentoInterno()) {
    		result.append("FINANCIAMENTO INTERNO ");
    		if (editalExtensao != null && editalExtensao.getDescricao() != null)
    			result.append("(" + editalExtensao.getDescricao() + ")");
    	}

    	if (this.isFinanciamentoExterno()) {
    		if (result.length() > 0)
    			result.append(", ");
	    		result.append("FINANCIAMENTO EXTERNO");
    	}

    	if (this.isAutoFinanciado()) {
    		if (result.length() > 0)
    			result.append(", ");
	    		result.append("A��O AUTO-FINANCIADA ");
    	}

    	if (this.isSemFinanciamento() && 
    			!this.isAutoFinanciado() && 
    			!this.isFinanciamentoExterno() &&
    			!this.isFinanciamentoInterno())
    		result.append("SEM FINANCIAMENTO");

    	if (!this.isSemFinanciamento() && 
    			!this.isAutoFinanciado() && 
    			!this.isFinanciamentoExterno() &&
    			!this.isFinanciamentoInterno())
    		result.append("N�O DEFINIDO");

    	return result.toString();
    }

    public Collection<OrcamentoConsolidado> getOrcamentosConsolidados() {
	return getProjeto().getOrcamentoConsolidado();
    }

    /**
     * M�todo utilizado para setar os Or�amentos Consolidados
     * @param orcamentosConsolidados
     */
    public void setOrcamentosConsolidados(Collection<OrcamentoConsolidado> orcamentosConsolidados) {
	getProjeto().setOrcamentoConsolidado(orcamentosConsolidados);
    }

    public boolean isFinanciamentoExterno() {
	return getProjeto().isFinanciamentoExterno();
    }

    /**
     * M�todo utilizado para setar o Financiamento Externo
     * @param financiamentoExterno
     */
    public void setFinanciamentoExterno(boolean financiamentoExterno) {
	getProjeto().setFinanciamentoExterno(financiamentoExterno);
    }

    public ClassificacaoFinanciadora getClassificacaoFinanciadora() {
	return getProjeto().getClassificacaoFinanciadora();
    }

    /**
     * M�todo utilizado para setar a Classifica��o Financiadora
     * @param classificacaoFinanciadora
     */
    public void setClassificacaoFinanciadora(ClassificacaoFinanciadora classificacaoFinanciadora) {
	getProjeto().setClassificacaoFinanciadora(classificacaoFinanciadora);
    }

    public String getEditalExterno() {
	return getProjeto().getDescricaoFinanciadorExterno();
    }

    /**
     * M�todo utilizado para setar o Edital Externo
     * @param descricaoFinanciadorExterno
     */
    public void setEditalExterno(String descricaoFinanciadorExterno) {
	getProjeto().setDescricaoFinanciadorExterno(descricaoFinanciadorExterno);
    }

    public boolean isFinanciamentoInterno() {
	return getProjeto().isFinanciamentoInterno();
    }

    /**
     * M�todo utilizado para setar o Financiamento Interno
     * @param financiamentoInterno
     */
    public void setFinanciamentoInterno(boolean financiamentoInterno) {
    	getProjeto().setFinanciamentoInterno(financiamentoInterno);
    }

    public AtividadeExtensao getAtividadeRenovada() {
	return atividadeRenovada;
    }

    public void setAtividadeRenovada(AtividadeExtensao atividadeRenovada) {
	this.atividadeRenovada = atividadeRenovada;
    }

    /**
     * A��es de extens�o executadas anteriormente solicitam registro na proex
     * para terem certificado reconhecido.
     * 
     * @return
     */
    public boolean isRegistro() {
	return registro;
    }

    public void setRegistro(boolean registro) {
	this.registro = registro;
    }

    /**
     * Retorna o coordenador da a��o de extens�o
     * 
     * @return
     */
    @Transient
    public MembroProjeto getCoordenacao() {
    	if (getProjeto() != null && getProjeto().getCoordenador() != null) {
    		return getProjeto().getCoordenador();    
    	}
    	//Evitar erro de nullPointer
    	return new MembroProjeto();
    }

    /**
     * M�todo utilizado para das informa��es sober o membro.
     * @return
     */
    @Transient
    public MembroProjeto getMembroInfo() {
	for (MembroProjeto ep : getMembrosEquipe()) {
	    return ep;
	}
	return null;
    }

    public Collection<AutorizacaoDepartamento> getAutorizacoesDepartamentos() {
	return autorizacoesDepartamentos;
    }

    public void setAutorizacoesDepartamentos(
	    Collection<AutorizacaoDepartamento> autorizacoes) {
	this.autorizacoesDepartamentos = autorizacoes;
    }

    public Integer getBolsasConcedidas() {
	return bolsasConcedidas;
    }

    public void setBolsasConcedidas(Integer bolsasConcedidas) {
	this.bolsasConcedidas = bolsasConcedidas;
    }

    /**
     * M�todo utilizado para informar os planos de Trabalho
     * @return
     */
    public Collection<PlanoTrabalhoExtensao> getPlanosTrabalho() {
	// Removendo os exclu�dos da lista..
	if ((planosTrabalho != null) && (!planosTrabalho.isEmpty())) {
	    for (Iterator<PlanoTrabalhoExtensao> it = planosTrabalho.iterator(); it.hasNext();) {
		PlanoTrabalhoExtensao pt = it.next();
		if (!pt.isAtivo())
		    it.remove();
	    }
	}
	return planosTrabalho;
    }

    public void setPlanosTrabalho(Collection<PlanoTrabalhoExtensao> planosTrabalho) {
	this.planosTrabalho = planosTrabalho;
    }

    public Integer getBolsasSolicitadas() {
	return bolsasSolicitadas;
    }

    public void setBolsasSolicitadas(Integer bolsasSolicitadas) {
	this.bolsasSolicitadas = bolsasSolicitadas;
    }

    /**
     * Atividades (Cursos/Eventos/Projetos/Programas) que possuem financiamento
     * pr�prio
     * 
     * @return
     */
    public boolean isAutoFinanciado() {
	return getProjeto().isAutoFinanciado();
    }

    /**
     * M�todo utilizado para setar o autoFinanciamento
     * @param autoFinanciado
     */
    public void setAutoFinanciado(boolean autoFinanciado) {
	getProjeto().setAutoFinanciado(autoFinanciado);
    }

    public Collection<AtividadeUnidade> getUnidadesProponentes() {
	return this.unidadesProponentes;
    }

    public void setUnidadesProponentes(
	    Collection<AtividadeUnidade> unidadesProponentes) {
	this.unidadesProponentes = unidadesProponentes;
    }

    /**
     * Total estimado de discentes envolvidos na atividade de extens�o.
     * Informado durante o cadastro da atividade e utilizado na an�lise da
     * proposta.
     * 
     * @return
     */
    public Integer getTotalDiscentes() {
	return totalDiscentes;
    }

    public void setTotalDiscentes(Integer totalDiscentes) {
	this.totalDiscentes = totalDiscentes;
    }

    /**
     * Lista de todas as avalia��es ativas.
     * 
     * @return {@link Collection} de {@link AvaliacaoAtividade} com lista de avalia��es.
     */
    public Collection<AvaliacaoAtividade> getAvaliacoes() {
	for (Iterator<AvaliacaoAtividade> iterator = avaliacoes.iterator(); iterator.hasNext();) {
	    AvaliacaoAtividade ava = iterator.next();
	    if (ava.getStatusAvaliacao().getId() == StatusAvaliacao.AVALIACAO_CANCELADA || !ava.isAtivo()) {		    
		iterator.remove();
	    }		
	}
	return avaliacoes;
    }

    public void setAvaliacoes(Collection<AvaliacaoAtividade> avaliacoes) {
	this.avaliacoes = avaliacoes;
    }

    /**
     * M�todo utilizado para informar as inscri��es da sele��o
     * @return
     */
    public Collection<InscricaoSelecaoExtensao> getInscricoesSelecao() {
	//Ordenando pelo compareTo de InscricaoSelecaoExtensao
	Collections.sort((List<InscricaoSelecaoExtensao>) inscricoesSelecao);

	return inscricoesSelecao;
    }

    public void setInscricoesSelecao(Collection<InscricaoSelecaoExtensao> inscricoes) {
	this.inscricoesSelecao = inscricoes;
    }

    /**
     * M�todo utilizado para informar os discentes selecionados
     * @return
     */
    public Collection<DiscenteExtensao> getDiscentesSelecionados() {
	// removendo os excluidos da lista..
	if ((discentesSelecionados != null) && (!discentesSelecionados.isEmpty())) {
	    for (Iterator<DiscenteExtensao> it = discentesSelecionados.iterator(); it.hasNext();) {
		DiscenteExtensao pt = it.next();
		if (!pt.isAtivo())
		    it.remove();
	    }
	}
	return discentesSelecionados;
    }

    public void setDiscentesSelecionados(
	    Collection<DiscenteExtensao> discentesSelecionados) {
	this.discentesSelecionados = discentesSelecionados;
    }

    /**
     * Retorna total de membros do comit� avaliando a a��o (com avalia��es
     * ativas)
     * 
     * @return
     */
    public int getTotalComiteAvaliando() {
    	int result = 0;
    	for (AvaliacaoAtividade a : getAvaliacoes()) {
    		if (a.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_COMITE && a.isAtivo()) {
    			result++;
    		}
    	}
    	return result;
    }

    /**
     * Retorna total de pareceristas avaliando a a��o
     * 
     * @return
     */
    public int getTotalPareceristaAvaliando() {
    	int result = 0;
    	for (AvaliacaoAtividade a : getAvaliacoes()) {
    		if ((a.getTipoAvaliacao().getId() == TipoAvaliacao.AVALIACAO_ACAO_PARECERISTA) && a.isAtivo()) {
    			result++;
    		}
    	}
    	return result;
    }

    /**
     *  Retorna de uma maneira muito errada as atividades pai vinculadas a essa atividade.
     *
     * @return
     */
    public Collection<AtividadeExtensao> getAtividadesPai() {
		// removendo os inativos da lista..
		if (atividadesPai != null) {
		    for (Iterator<AtividadeExtensao> it = atividadesPai.iterator(); it.hasNext();) {
			if (!it.next().isAtivo()) {
				it.remove();
			}
		    }
		}
	    return atividadesPai;
    }

    public void setAtividadesPai(Collection<AtividadeExtensao> atividadesPai) {
	this.atividadesPai = atividadesPai;
    }

    public Collection<SolicitacaoReconsideracao> getSolicitacoesReconsideracao() {
	return getProjeto().getSolicitacoesReconsideracao();
    }

    /**
     * M�todo utilizado para setar as solicita��es de Reconsidera��o
     * @param solicitacoesReconsideracao
     */
    public void setSolicitacoesReconsideracao(
	    Collection<SolicitacaoReconsideracao> solicitacoesReconsideracao) {
	this.getProjeto().setSolicitacoesReconsideracao(solicitacoesReconsideracao);
    }

    public TipoRegiao getTipoRegiao() {
	return getProjeto().getAbrangencia();
    }

    /**
     * M�todo utilizado para setar o tipo de Regi�o
     * @param tipoRegiao
     */
    public void setTipoRegiao(TipoRegiao tipoRegiao) {
	this.getProjeto().setAbrangencia(tipoRegiao);
    }

    public int getSequencia() {
	return sequencia;
    }

    public void setSequencia(int sequencia) {
	this.sequencia = sequencia;
    }

    /**
     * Retorna o c�digo da a��o de extens�o. A��es com o xx no c�digo n�o foram
     * aprovadas pelos membros do comit� ainda.
     * 
     * @return
     */
    public String getCodigo() {
	String prefixo = "";

	if (tipoAtividadeExtensao != null) {
	    switch (tipoAtividadeExtensao.getId()) {
	    case TipoAtividadeExtensao.CURSO:
		prefixo = "CR";
		break;
	    case TipoAtividadeExtensao.EVENTO:
		prefixo = "EV";
		break;
	    case TipoAtividadeExtensao.PRODUTO:
		prefixo = "PD";
		break;
	    case TipoAtividadeExtensao.PROGRAMA:
		prefixo = "PG";
		break;
	    case TipoAtividadeExtensao.PROJETO:
		prefixo = "PJ";
		break;
	    }
	}else {
	    prefixo = "--";
	}

	NumberFormat format = new DecimalFormat("000");

	if (sequencia == 0)
	    return prefixo + "xxx" + "-" + projeto.getAno();

	return prefixo + format.format(sequencia) + "-" + projeto.getAno();
    }

    public Boolean getConvenioFunpec() {
	return convenioFunpec;
    }

    public void setConvenioFunpec(Boolean convenioFunpec) {
	this.convenioFunpec = convenioFunpec;
    }

    /**
     * Retorna true se a a��o est� aprovada ou em execu��o
     */
    public boolean isAprovadoEmExecucao() {
	return ((getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO) || 
		(getProjeto().getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO));
    }

    /**
     * Retorna true se a a��o est� aprovada ou em execu��o
     */
    public boolean isValida() {
	Integer situacaoAtual = getProjeto().getSituacaoProjeto().getId();
	return	Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO).contains(situacaoAtual)
		|| Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO).contains(situacaoAtual) ;
    }


    /**
     * Retorna true se a a��o pode ser editada
     */
    public boolean isPassivelEdicao() {
	return	((getProjeto().getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO)
		|| (getProjeto().getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_PROPOSTA_DEVOLVIDA_PARA_COORDENADOR)) ;
    }



    public String getCodigoTitulo() {
	return getCodigo() + " - " + getProjeto().getTitulo();
    }
    
    
    public boolean isSemFinanciamento() {
	return getProjeto().isSemFinanciamento();
    }

    /**
     * M�todo utilizado para setar o atributo semFinanciamento do projeto.
     * @param semFinanciamento
     */
    public void setSemFinanciamento(boolean semFinanciamento) {
	getProjeto().setSemFinanciamento(semFinanciamento);
    }

    /**
     * Retorna somente relat�rios ativos da a��o atual
     * 
     * @return
     */
    public Collection<RelatorioAcaoExtensao> getRelatorios() {
    	
	if(relatorios != null) {
	    for (Iterator<RelatorioAcaoExtensao> ir = relatorios.iterator(); ir.hasNext();) {
		if (!ir.next().isAtivo())
		    ir.remove();
	    } 
	}
				
	return relatorios;
    }

    public void setRelatorios(Collection<RelatorioAcaoExtensao> relatorios) {
	this.relatorios = relatorios;
    }

    /**
     * Retorna lista de participantes 'ativos' da a��o de extens�o 'ordenados' por nome.
     * (Publico alvo atendido)
     * 
     * @return
     */
    public Collection<ParticipanteAcaoExtensao> getParticipantesOrdenados() {
    	Collections.sort(getParticipantesNaoOrdenados());
    	return participantes;
    }
    
    /**
     * Retorna lista de participantes 'ativos' da a��o de extens�o sem ordena��o.
     * Melhora o desempenho na manipula��o de listas de participantes. 
     * 
     * @return
     */
    public List<ParticipanteAcaoExtensao> getParticipantesNaoOrdenados() {
    	// Removendo os exclu�dos da lista..
    	if ((participantes != null) && (!participantes.isEmpty())) {
    		for (Iterator<ParticipanteAcaoExtensao> it = participantes.iterator(); it.hasNext();) {
    			if (!it.next().isAtivo())
    				it.remove();
    		}
    	}

    	return (List<ParticipanteAcaoExtensao>) participantes;
    }

    public Collection<ParticipanteAcaoExtensao> getParticipantes() {
    	return participantes;
    }
    
    public void setParticipantes(Collection<ParticipanteAcaoExtensao> participantes) {
	this.participantes = participantes;
    }
    
    
    /**
     * Informa se esta a��o de extens�o est� apta para receber inscri��es de
     * interessados em participar da equipe
     * 
     * @return
     */
    public boolean isAbertoParaSelecao() {
	return isAprovadoEmExecucao() && getBolsasSolicitadas() > 0;
    }

    /**
     * Informa se a a��o de extens�o foi analisada e aprovada pela proex
     * 
     * @return
     */
    public boolean isAprovadaProex() {
	return sequencia > 0;
    }
    
    /**
     * Informa se a proposta foi enviada pelo coordenador.
     * 
     * @return
     */
    public boolean isPropostaEnviada() {
	return getDataEnvio() != null 
		&& (getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_SUBMETIDO 
			|| getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AUTORIZACAO_DEPARTAMENTOS);
			
    }

    public boolean isTipoProjeto() {
	return getTipoAtividadeExtensao().isProjeto();
    }

    public boolean isTipoProduto() {
	return getTipoAtividadeExtensao().isProduto();
    }

    public boolean isTipoPrograma() {
	return getTipoAtividadeExtensao().isPrograma();
    }

    public boolean isTipoCurso() {
	return getTipoAtividadeExtensao().isCurso();
    }

    public boolean isTipoEvento() {
	return getTipoAtividadeExtensao().isEvento();
    }

    /**
     * Retorna a quantidade de participantes da a��o de extens�o
     * 
     * @return
     */
    public Integer getTotalParticipantes(){
	return participantes.isEmpty() ? 0 : participantes.size();
    }

    public Projeto getProjeto() {
	return projeto;
    }

    public void setProjeto(Projeto projeto) {
	this.projeto = projeto;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    /**
     * M�todo utilizado para setar a unidade do projeto da Atividade de Exetns�o.
     * @param unidade
     */
    public void setUnidade(Unidade unidade) {
	getProjeto().setUnidade(unidade);	    
    }

    public Unidade getUnidade() {
	return getProjeto().getUnidade();
    }
    
    /** Retorna a sigla da unidade em quest�o de forma invertida. 
     *  Utilizada na listagem das atividades de extens�o.  
     */
    public String getSiglaUnidadeInsertida() {
    	String[] sigla = getProjeto().getUnidade().getSigla().split("/");
    	if ( sigla.length == 1 )
    		return getProjeto().getUnidade().getSigla();
    	else
    		return sigla[1] + "/" + sigla[0];
	}

    /**
     * M�todo utilizado para setar o ano do projeto da Atividade de Extens�o.
     * @param anoAtual
     */
    public void setAno(Integer anoAtual) {
	getProjeto().setAno(anoAtual);	    
    }

    public Integer getAno() {
	return getProjeto().getAno();	    
    }

    public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
	return getProjeto().getAreaConhecimentoCnpq();
    }

    /**
     * M�todo utilizado para setar a �rea de conhecimento CNPQ do projeto da Ativadade de Exetns�o.
     * @param areaCNPQ
     */
    public void setAreaConhecimentoCnpq(AreaConhecimentoCnpq areaCNPQ) {
	getProjeto().setAreaConhecimentoCnpq(areaCNPQ);
    }

    /**
     * M�todo utilizado para setar o tipo de situa��o do projeto da Ativiadde de Extens�o.
     * @param tipoSituacaoProjeto
     */
    public void setSituacaoProjeto(TipoSituacaoProjeto tipoSituacaoProjeto) {
    	situacaoProjeto = tipoSituacaoProjeto;
    	getProjeto().setSituacaoProjeto(situacaoProjeto);
    }
    
    /** 
     * Situa��o na qual o projeto se encontra (Ex.: Conclu�do, Cancelado, Aguardando Avalia��o) 
     */
    public TipoSituacaoProjeto getSituacaoProjeto() {
	return situacaoProjeto;	    
    }

    public Collection<ArquivoProjeto> getArquivos() {
	return getProjeto().getArquivos();
    }

    /**
     * M�todo utilizado para setar o Collection de arquivos do projeto da Atividade de Extens�o
     * @param arquivos
     */
    public void setArquivos(Collection<ArquivoProjeto> arquivos) {
	getProjeto().setArquivos(arquivos);
    }

    /**
     * M�todo utilizado para adicionar um arquivo ao projeto da Atividade de Extens�o
     * @param arquivo
     */
    public void addArquivo(ArquivoProjeto arquivo) {
	getProjeto().addArquivo(arquivo);	    
    }

    /**
     * M�todo utilizado para adicionar uma nova foto ao projeto da Atividade de Extens�o.
     * @param novaFoto
     */
    public void addFoto(FotoProjeto novaFoto) {
	getProjeto().addFoto(novaFoto);	    
    }

    public Collection<FotoProjeto> getFotos(){
	return getProjeto().getFotos();
    }

    /**
     * M�todo utilizado para setar o Collection de fotos do projeto da Atividade de Extens�o.
     * @param fotos
     */
    public void setFotos(Collection<FotoProjeto> fotos){
    	getProjeto().setFotos(fotos);
    }

    public String getTitulo() {
    	return getProjeto().getTitulo();
    }

    /**
     * M�todo utilizado para setar o t�tulo do projeto da Atividade de Extens�o
     * @param titulo
     */
    public void setTitulo(String titulo) {
    	getProjeto().setTitulo(titulo);
    }

    /**
     * M�todo utilizado para setar a data de in�cio do projeto da Atividade de Extens�o.
     * @param data
     */
    public void setDataInicio(Date data) {
	getProjeto().setDataInicio(data);
    }

    public Date getDataInicio() {
	return getProjeto().getDataInicio();
    }

    /**
     * M�todo utilizado para setar a data fim do projeto da Atividade de Extens�o
     * @param data
     */
    public void setDataFim(Date data) {
	getProjeto().setDataFim(data);
    }

    public Date getDataFim() {
	return getProjeto().getDataFim();
    }

    public String getMetodologia() {
	return getProjeto().getMetodologia();
    }

    /**
     * M�todo utilizado para setar a metodologia
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param metodologia
     */
    public void setMetodologia(String metodologia) {
	getProjeto().setMetodologia(metodologia);
    }

    public String getJustificativa() {
	return getProjeto().getJustificativa();
    }

    /**
     * M�todo utilizado para setar a justificativa
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param justificativa
     */
    public void setJustificativa(String justificativa) {
	getProjeto().setJustificativa(justificativa);
    }

    public void setAtivo(boolean a) {
	ativo = a; 
    }

    public boolean isAtivo() {
	return ativo;
    }

    /**
     * M�todo utilizado para setar o hist�rico
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param historico
     */
    public void setHistoricoSituacao(Collection<HistoricoSituacaoProjeto> historico) {
	getProjeto().setHistoricoSituacao(historico);	    	    
    }

    public Collection<HistoricoSituacaoProjeto> getHistoricoSituacao(){
	return getProjeto().getHistoricoSituacao();
    }

    public RegistroEntrada getRegistroEntrada() {
	return getProjeto().getRegistroEntrada();
    }

    /**
     * M�todo utilizado apra setar o registro de entrada.
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param reg
     */
    public void setRegistroEntrada(RegistroEntrada reg) {
	getProjeto().setRegistroEntrada(reg);
    }

    /**
     * M�todo utilizado apra setar a data de cadastro
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param date
     */
    public void setDataCadastro(Date date) {
	getProjeto().setDataCadastro(date);	    
    }
    
    public Date getDataCadastro() {
    	return getProjeto().getDataCadastro();	    
    }

    /**
     * M�todo utilizado apra setar o n�mero institucional
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param numero
     */
    public void setNumeroInstitucional(Integer numero) {
	getProjeto().setNumeroInstitucional(numero); 
    }

    public Collection<MembroProjeto> getMembrosEquipe() {
	return getProjeto().getEquipe();
    }

    /**
     * M�todo utilizado apra setar os membros da equipe
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param equipe
     */
    public void setMembrosEquipe(Collection<MembroProjeto> equipe) {
	getProjeto().setEquipe(equipe);
    }

    public String getAnoTitulo(){
	return getProjeto().getAnoTitulo();
    }

    public String getPublicoAlvo() {
	return publicoAlvo;
    }

    public void setPublicoAlvo(String publicoAlvo) {
	this.publicoAlvo = publicoAlvo;
    }

    /**
     * M�todo utilizado para realizar valida��es na entidade AtividadeExtens�o
     */
    public ListaMensagens validate() {
	return null;
    }

    /**
     * Verifica se o membro possui atividade cadastrada, se tiver se deve deixar remover depois 
     * de remover a atividade do membro.
     * @param objetivos
     * @param mp
     * @return
     */
    public ListaMensagens validateRemocaoMembroAtividade(Collection<Objetivo> objetivos, MembroProjeto mp) {
    	ListaMensagens lista = new ListaMensagens();
    	achou : for (Objetivo objetivo : objetivos) {
			for (ObjetivoAtividades objAtividade : objetivo.getAtividadesPrincipais()) {
				for (MembroAtividade membroAtividade : objAtividade.getMembrosAtividade()) {
					if ( membroAtividade.getMembroProjeto().getId() == mp.getId() ) {
						lista.addErro("N�o � poss�vel remover esse membro pois h� atividade(s) v�nculadas para o mesmo.");
						break achou;
					}
				}
			}
		}
    	return lista;
    }
    
    /**
     * Informa se este projeto faz parte de uma 
     * A��o Acad�mica Associada. 
     * 
     * @return
     */
    public boolean isProjetoAssociado() {
	return getProjeto().isProjetoAssociado();
    }

    /**
     * Informa se � um projeto isolado.
     * 
     * @return
     */
    public boolean isProjetoIsolado() {
	return getProjeto().isProjetoIsolado();
    }


    /**
     * Informa se a a��o de extens�o est� sendo renovada.
     * 
     * @return
     */
    public Boolean getRenovacao() {
        return getProjeto().getRenovacao();
    }

    /**
     * M�todo utilizado apra setar se h� ou n�o renova��o
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param renovacao
     */
    public void setRenovacao(Boolean renovacao) {
        getProjeto().setRenovacao(renovacao);
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
    	this.dataEnvio = dataEnvio;
    }

    public String getResumo() {
    	return getProjeto().getResumo();
    }

    /**
     * M�todo utilizado apra setar o resumo
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param resumo
     */
    public void setResumo(String resumo) {
    	getProjeto().setResumo(resumo);
    }

    public String getReferencias() {
    	return getProjeto().getReferencias();
    }

    /**
     * M�todo utilizado apra setar as refenr�ncias
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param referencias
     */
    public void setReferencias(String referencias) {
	this.getProjeto().setReferencias(referencias);
    }

    /**
     * M�todo utilizado apra setar os Objetivos
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param referencias
     */
    public void setObjetivos(String objetivos) {
    	this.getProjeto().setObjetivos(objetivos);
    }
    
    public String getObjetivos() {
    	return getProjeto().getObjetivos();
    }
    
    /**
     * M�todo utilizado apra setar os Resultados
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param referencias
     */
    public void setResultados(String resultados) {
    	this.getProjeto().setResultados(resultados);
    }
    
    public String getResultados() {
    	return getProjeto().getResultados();
    }
    
    /**
     * M�todo utilizado apra setar os Objetivos Especificos
     * <br>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao � chamdo por JSP(s)</li>
     * </ul>
     * @param referencias
     */
    public void setObjetivosEspecificos(String objetivosEspecificos) {
    	this.getProjeto().setObjetivosEspecificos(objetivosEspecificos);
    }
    
    public String getObjetivosEspecificos() {
    	return getProjeto().getObjetivosEspecificos();
    }
    
    /**
     * Indica se a a��o foi finalizada.
     * 
     * @return
     */
    public boolean isFinalizada() {
    	return getProjeto().isFinalizado();
    }

    /**
     * Verifica se o edital desta a��o de extens�o ainda est� em aberto.
     * Utilizado para habilitar o campo de preenchimento de edital no formul�rio
     * de cadastro da a��o.
     * 
     * @param editaisAbertosAtualmente Lista de editais abertos atualmente.
     * @return
     */
    public boolean isPermitidoSelecionarEditaisEmAberto(Collection<Edital> editaisAbertosAtualmente) {
	if ((getEditalExtensao() != null) && (getEditalExtensao().getId() > 0)) {
	    for (Edital ee : editaisAbertosAtualmente) {
		if (getEditalExtensao().getEdital().getId() == ee.getId()) {
		    return true;
		}
	    }	    
	    return false; //edital desta a��o n�o est� mais aberto.
	}
	return true; //a��o ainda n�o selecionou um edital.
    }
    
    /** verifica se � permito enviar email de notifica��o na falta do relat�rio final */
    public boolean isPermitidoEnviarEmailNotificacaoFaltaDeRelatorioFinal() {
    	
    	if (isEnviouRelatorioFinal()) {
    		return false;
    	}
    	
    	//S� poder� enviar outra notifica��o X dias ap�s o envio da �ltima notifica��o.
    	//Irei substituir pelo m�todo da arquitetura que soma datas, caso exista.
    	if(dataEmailCobrancaRelatorioFinal != null) {
    		
    		Calendar calendar = Calendar.getInstance();  
    		calendar.setTime( dataEmailCobrancaRelatorioFinal );  
    		calendar.add( Calendar.DAY_OF_MONTH , INTERVALO_DIAS_PERMITIR_EMAIL );	
    		
    		if( (new Date()).before(calendar.getTime()) ) {
    			return false;
    		}    	
    	}
    	
    	//S� poder� enviar notifica��o se coordenador possuir email cadastrado
    	if(getCoordenacao().getPessoa().getEmail() == null ){
    		return false;
    	}
    	
    	return true;    	
    }

    
    /** Verifica se o relat�rio final desta a��o j� foi enviado. */
    public boolean isEnviouRelatorioFinal() {
    	for(RelatorioAcaoExtensao rel : relatorios) {
    		if(rel.isRelatorioFinal() && rel.getDataEnvio() != null) {
    			return true;
    		}
    	}
    	return false;
    }
	

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Date getDataEmailCobrancaRelatorioFinal() {
		return dataEmailCobrancaRelatorioFinal;
	}

	public void setDataEmailCobrancaRelatorioFinal(
			Date dataEmailCobrancaRelatorioFinal) {
		this.dataEmailCobrancaRelatorioFinal = dataEmailCobrancaRelatorioFinal;
	}
    
	/**
	 * Retorna a quantidade de sub atividades.
	 *
	 * @return
	 */
	public int getQtdSubAtividadesExtensao() {
		if(subAtividadesExtensao == null)
			return 0;
		else
			return subAtividadesExtensao.size();
	}
	
	/**
	 * Retorna o total de dias restantes para a conclus�o da
	 * a��o de extens�o.
	 * 
	 * @return valores negativos indicam que a a��o j� passou do prazo de conclus�o.
	 */
	public long getTotalDiasConclusao() {		
		return CalendarUtils.calculoDias(
				CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0),
				CalendarUtils.configuraTempoDaData(getDataFim(), 0, 0, 0, 0) );			
	}
	
	/**
	 * Verifica se a data para a conclus�o da a��o � posterior ou anterior � atual.
	 * 
	 * @return
	 */	
	public boolean isPrazoExpiradoParaConclusao() {
		return CalendarUtils.estorouPrazo(getDataFim(), new Date());
	}
	
	/**
	 * Usado para mostrar a data formatada em um atributo title. 
	 */
	public String getDataCobrancaRelFormatada() {
		Formatador f = Formatador.getInstance();
		return f.formatarDataHora(dataEmailCobrancaRelatorioFinal);		
	}
	
	/**
	 * Verifica se a a��o pode ser distribu�da para iniciar a avalia��o.
	 * 
	 * @return
	 */
	public boolean isPermitidoIniciarAvaliacao() {
	    return  isProjetoIsolado() 
	    			&& (getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO 
	    			|| getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_SUBMETIDO); 
	}
	
	/** retorna total de vagas remuneradas dispon�veis   */
	public Integer getVagasRemuneradasDisponiveis() {
		
		if( bolsasConcedidas == 0)
			return bolsasConcedidas;
		
		int totalAtivos = 0;
		
		for(DiscenteExtensao d : discentesSelecionados) {
			if(d.isAtivo() && d.getTipoVinculo().getId() == TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO
					&& d.getSituacaoDiscenteExtensao().getId() == TipoSituacaoDiscenteExtensao.ATIVO) {
				totalAtivos++;
			}
		}
		
		return bolsasConcedidas - totalAtivos;		
	}

	/** P�blico total da a��o de extens�o */
	public int getPublicoTotal() {
		int total = publicoExterno != null ? publicoExterno : 0;
		total += publicoInterno != null ? publicoInterno : 0;
		return total; 
	}
	
	public boolean isFinancExternoEspecial() {
		return financExternoEspecial;
	}

	public void setFinancExternoEspecial(boolean financExternoEspecial) {
		this.financExternoEspecial = financExternoEspecial;
	}

	public Boolean getBolsaFinancExternoEspecial() {
	    return bolsaFinancExternoEspecial;
	}

	public void setBolsaFinancExternoEspecial(Boolean bolsaFinancExternoEspecial) {
	    this.bolsaFinancExternoEspecial = bolsaFinancExternoEspecial;
	}

	public Boolean getVinculoProgramaExtensao() {
	    return vinculoProgramaExtensao;
	}

	public void setVinculoProgramaExtensao(Boolean vinculoProgramaExtensao) {
	    this.vinculoProgramaExtensao = vinculoProgramaExtensao;
	}
	
	/**
	 * Determina se um avaliador Ad Hoc pode avaliar esta a��o de extens�o.
	 * O avaliador Ad Hoc � respons�vel por avaliar o m�rito acad�mico da a��o de extens�o
	 * definindo uma nota para a proposta baseado no formul�rio de avalia��o disponibilizado
	 * pela Pr�-Reitoria de Extens�o.
	 * 
	 * @return
	 */
	public boolean isPermitidoAdHocAvaliar() {
	    return  (getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO); //se a a��o ainda est� aguardando avalia��o 
	}

	/**
	 * Retorna uma String de notas de todas a avalia��es realizadas para esta a��o. 
	 */	
	public String getNotasAvaliacoes() {
	    if (avaliacoes != null) {
		for (AvaliacaoAtividade ava : avaliacoes) {
		    notasAvaliacoes += ava.getNota() + " / "; 
		}
	    }	    
	    return notasAvaliacoes;
	}

	public void setNotasAvaliacoes(String notasAvaliacoes) {
	    this.notasAvaliacoes = notasAvaliacoes;
	}

	public boolean isEdicaoGestor() {
	    return edicaoGestor;
	}

	public void setEdicaoGestor(boolean edicaoGestor) {
	    this.edicaoGestor = edicaoGestor;
	}
	
	
	/***
	 * Determina se esta a��o de extens�o pode ser Reativada.
	 * 
	 * @return
	 */
	public boolean isPossivelReativar() {
		return isFinalizada() && situacaoProjeto.getId() == TipoSituacaoProjeto.EXTENSAO_CONCLUIDO;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public String getSecurityToken() {
		return securityToken;
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

	public Collection<SubAtividadeExtensao> getSubAtividadesExtensao() {
		return subAtividadesExtensao;
	}

	public void setSubAtividadesExtensao(
			Collection<SubAtividadeExtensao> subAtividadesExtensao) {
		this.subAtividadesExtensao = subAtividadesExtensao;
	}

	public Integer getMaxSubAtividadesPermitidasInscricao() {
		return maxSubAtividadesPermitidasInscricao;
	}

	public void setMaxSubAtividadesPermitidasInscricao(
			Integer maxSubAtividadesPermitidasInscricao) {
		this.maxSubAtividadesPermitidasInscricao = maxSubAtividadesPermitidasInscricao;
	}

	public SubAtividadeExtensao getSubAtividade() {
		return subAtividade;
	}

	public void setSubAtividade(SubAtividadeExtensao subAtividade) {
		this.subAtividade = subAtividade;
	}

	public ProgramaEstrategicoExtensao getProgramaEstrategico() {
		return programaEstrategico;
	}

	public void setProgramaEstrategico(
			ProgramaEstrategicoExtensao programaEstrategico) {
		this.programaEstrategico = programaEstrategico;
	}

	public String getFundamentacaoTeorica() {
		return fundamentacaoTeorica;
	}

	public void setFundamentacaoTeorica(String fundamentacaoTeorica) {
		this.fundamentacaoTeorica = fundamentacaoTeorica;
	}

	public boolean isVinculadoProgramaEstrategico() {
		return vinculadoProgramaEstrategico;
	}

	public void setVinculadoProgramaEstrategico(boolean vinculadoProgramaEstrategico) {
		this.vinculadoProgramaEstrategico = vinculadoProgramaEstrategico;
	}

	public boolean isProjetoVinculadoFormacaoContinuada() {
		return projetoVinculadoFormacaoContinuada;
	}

	public void setProjetoVinculadoFormacaoContinuada(
			boolean projetoVinculadoFormacaoContinuada) {
		this.projetoVinculadoFormacaoContinuada = projetoVinculadoFormacaoContinuada;
	}

	public Integer getPublicoInterno() {
		return publicoInterno;
	}

	public void setPublicoInterno(Integer publicoInterno) {
		this.publicoInterno = publicoInterno;
	}

	public Integer getPublicoExterno() {
		return publicoExterno;
	}

	public void setPublicoExterno(Integer publicoExterno) {
		this.publicoExterno = publicoExterno;
	}

	public String getPublicoAlvoExterno() {
		return publicoAlvoExterno;
	}

	public void setPublicoAlvoExterno(String publicoAlvoExterno) {
		this.publicoAlvoExterno = publicoAlvoExterno;
	}

	/**Retorna apenas os objetivos da a��o que est�o ativos.*/
	public Collection<Objetivo> getObjetivo() {
		if (objetivo != null){
			// Removendo os inativos
			for (Iterator<Objetivo> it = objetivo.iterator(); it.hasNext() ; )	{
				if (!it.next().isAtivo()){
					it.remove();
				}
			}
		}
		return objetivo;
	}

	public void setObjetivo(Collection<Objetivo> objetivo) {
		this.objetivo = objetivo;
	}

	public boolean isFinancProex() {
		return financProex;
	}

	public void setFinancProex(boolean financProex) {
		this.financProex = financProex;
	}

	/**retorna apenas os locais de realiza��o da a��o, que est�o ativos.*/
	public List<LocalRealizacao> getLocaisRealizacao() {
		if (locaisRealizacao != null){
			// Removendo os inativos
			for (Iterator<LocalRealizacao> it = locaisRealizacao.iterator(); it.hasNext() ; )	{
				if (!it.next().isAtivo())
					it.remove();
			}
		}
		return locaisRealizacao;
	}

	public void setLocaisRealizacao(List<LocalRealizacao> locaisRealizacao) {
		this.locaisRealizacao = locaisRealizacao;
	}

	public boolean isFinancExternoEdital() {
		return financExternoEdital;
	}

	public void setFinancExternoEdital(boolean financExternoEdital) {
		this.financExternoEdital = financExternoEdital;
	}

	public boolean isFinancUnidProponente() {
		return financUnidProponente;
	}

	public void setFinancUnidProponente(boolean financUnidProponente) {
		this.financUnidProponente = financUnidProponente;
	}

	public String getUnidadeExecutoraExterna() {
		return unidadeExecutoraExterna;
	}

	public void setUnidadeExecutoraExterna(String unidadeExecutoraExterna) {
		this.unidadeExecutoraExterna = unidadeExecutoraExterna;
	}

	public ExecutorFinanceiro getExecutorFinanceiro() {
		return executorFinanceiro;
	}

	public void setExecutorFinanceiro(ExecutorFinanceiro executorFinanceiro) {
		this.executorFinanceiro = executorFinanceiro;
	}
	
	public int getNumeroInscritos() {
		return numeroInscritos;
	}

	public void setNumeroInscritos(int numeroInscritos) {
		this.numeroInscritos = numeroInscritos;
	}

	public int getNumeroVagasAbertas() {
		return numeroVagasAbertas;
	}

	public void setNumeroVagasAbertas(int numeroVagasAbertas) {
		this.numeroVagasAbertas = numeroVagasAbertas;
	}

	public int getNumeroParticipantes() {
		return numeroParticipantes;
	}

	public void setNumeroParticipantes(int numeroParticipantes) {
		this.numeroParticipantes = numeroParticipantes;
	}

	public Boolean getVinculoProgramaEstrategico(){
		return vinculadoProgramaEstrategico;
	}

	public EditalExtensaoLinhaAtuacao getLinhaAtuacao() {
		return linhaAtuacao;
	}

	public void setLinhaAtuacao(EditalExtensaoLinhaAtuacao linhaAtuacao) {
		this.linhaAtuacao = linhaAtuacao;
	}
}