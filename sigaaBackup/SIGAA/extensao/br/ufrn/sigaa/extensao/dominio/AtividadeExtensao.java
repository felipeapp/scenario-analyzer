/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Representa uma ação de extensão. <br>
 * 
 * Uma ação de extensão pode ser considerada como (a) uma atividade formadora,
 * vinculada ao processo pedagógico do aluno; (b) coordenada por docente e (c)
 * com contato direto com a sociedade. <br>
 * As Ações de Extensão são aquelas que envolvem professores, alunos e
 * servidores técnicos-administrativos e que se enquadrem nas modalidades:
 * Programas, Projetos, Cursos, Eventos, Produtos e Prestação de serviços. <br>
 * Toda proposta de ação de extensão deve ter obrigatoriamente um Coordenador,
 * que deverá ser professor do quadro permanente da UFRN, lotado em Departamento
 * Acadêmico ou Unidade Acadêmica Especializada da UFRN, nos termos do Estatuto
 * e do Regimento Geral da UFRN (Art. 3 da Res. 0702004 - CONSEPE). <br>
 * Cada professor só poderá coordenar, simultaneamente, duas ações de extensão
 * da mesma modalidade (Art 4o da Res. 070/2004 - CONSEPE). <br>
 * As propostas devem conter o registro da equipe responsável pela realização da
 * ação, com explicitação das funções de cada participante, bem como da carga
 * horária a ser cumprida pelos membros (Art 5o da Res. 070/2004 - CONSEPE).
 * <br>
 * No caso da equipe responsável pela realização da ação contar com servidores
 * lotados em Unidade(s) da UFRN distinta(s) daquela em que esta lotado o
 * Coordenador ou em órgãos externos da universidade, deverá constar do processo
 * a concordância expressa do(s) dirigente(s) da(s) outra(s) Unidade(s)
 * envolvida(s) (Art 6o da Res. 070/2004 - CONSEPE). <br>
 * Uma ação de extensão herda características de public.Projeto
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

//	/** Atributo utilizado para identificar o tipo de ação. */
//    public static final int PROGRAMA = 1;
//    /** Atributo utilizado para identificar o tipo de ação. */
//    public static final int PROJETO = 2;
//    /** Atributo utilizado para identificar o tipo de ação. */
//    public static final int CURSO = 3;
//    /** Atributo utilizado para identificar o tipo de ação. */
//    public static final int EVENTO = 4;
//    /** Atributo utilizado para identificar o tipo de ação. */
//    public static final int PRESTACAO_DE_SERVICO = 5;
//    /** Atributo utilizado para identificar o tipo de ação. */
//    public static final int PRODUTO = 6;

    /** intervalo de dias para envio de email pelos coordenadores  */
    public static final  int INTERVALO_DIAS_PERMITIR_EMAIL = 10;
    
    /** identificador da ação */
    @Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="extensao.atividade_extensao_sequence") })
	@Column(name = "id_atividade", unique = true, nullable = false)
    private int id;

    /** Projeto associado à atividade. Contém os dados básicos da Ação de Extensão. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto")
    private Projeto projeto = new Projeto(new TipoProjeto(TipoProjeto.EXTENSAO));

    /** Data de envio da Atividade para as unidades envolvidas.  */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_envio")
    private Date dataEnvio;

    /** Indica se a ação esta vinculada a um programa de extensão. */
    @Column(name = "vinculo_programa_extensao")
    private boolean vinculoProgramaExtensao = false;

    /** Público estimado da Ação de Extensão */
    @Column(name = "publico_interno")
    private Integer publicoInterno;

    /** Público Externo estimado da Ação de Extensão */
    @Column(name = "publico_externo")
    private Integer publicoExterno;
    
    /** Número de bolsas concedidas para a Ação de Extensão. */
    @Column(name = "bolsas_concedidas")
    private Integer bolsasConcedidas = 0;

    /** Número de bolsas solicitadas pelo coordenador da Ação de Extensão; */
    @Column(name = "bolsas_solicitadas")
    private Integer bolsasSolicitadas = 0;

    /** Total estimado de discentes envolvidos na execução da atividade. */
    @Column(name = "total_discentes")
    private Integer totalDiscentes = 0;

    /** Total de pessoas atendidas com a execução da ação de extensão. */
    @Column(name = "publico_atendido")
    private Integer publicoAtendido;

    /** Referente aos tipos de público alvo atingido pela ação de extensão.*/
    @Column(name = "publico_alvo")
    private String publicoAlvo;
    
    /** Referente aos tipos de público alvo Externo atingido pela ação de extensão.*/
    @Column(name = "publico_alvo_externo")
    private String publicoAlvoExterno;
    
    /** Caso o evento/curso seja detalhado em SubAtividadeExtensao permite informar o número máximo de 
     * atividades permitidas. Se for NULO deixar se inscrever em quantos quiser. */
    @Column(name = "max_sub_atividades_permitidas_inscricao")
    private Integer maxSubAtividadesPermitidasInscricao = null;

    /**
     * True se a ação for do tipo permanente. Ações permanente são ações já
     * consolidadas junto a comunidade e que sempre são submetidas para renovação
     * todos os anos. (Grupo Permanente de Arte e Cultura)
     */
    @Column(name = "permanente", nullable = false)
    private Boolean permanente = false;

    /** Informa se a ação de extensão possui algum tipo de convênio com a FUNPEC. */
    @Column(name = "convenio_funpec", nullable = false)
    private Boolean convenioFunpec = false;

    /** 
     * Informa se a ação de extensão possui algum financiamento externo nos termos publicados no edital.
     * Ações deste tipo, normalmente, possuem recursos de valores muito elevados e são tratadas de forma
     * diferenciada pela equipe de avaliadores da proposta. 
     */
    @Column(name = "financ_externo_especial", nullable = false)
    private boolean financExternoEspecial = false;

    /** 
     * Permite que o docente informe se esta ação recebeu bolsa de algum financiamento externo.
     * Utilizado pela comissão de avaliação para determinar a concessão de novas bolsas.
     */
    @Column(name = "bolsa_financ_externo_especial")
    private Boolean bolsaFinancExternoEspecial = false;
    
    
    
    /** Notificacao de cobranca de relatorio só poderá ser enviada
       apos X dias da ultima data de cobranca do relatorio final. */
    @Column(name = "data_cobranca_relatorio_final")
    private Date dataEmailCobrancaRelatorioFinal;
	
    
    /**Campo utilizado na geração do código da ação de extensão.
       A contagem é feita por ano e por tipo de ação. */
    @GeneratedValue(generator="seqGenerator")
    @GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    		parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
    @Column(name = "sequencia", unique = true, nullable = false)
    private int sequencia;

    /** Principal área de atuação da ação de extensão. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_area_tematica_principal", referencedColumnName = "id_area_tematica")
    private AreaTematica areaTematicaPrincipal = new AreaTematica();

    /** Em caso de renovação, relaciona esta com a ação renovada. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_atividade_renovada")
    private AtividadeExtensao atividadeRenovada;

    /** Local de realização da ação de extensão.
        Ex. Auditório da Reitoria da UFRN. Ver tabela extensao.local_realizacao */
    @Transient
    private LocalRealizacao localRealizacao = new LocalRealizacao();

    /**Lista de locais da realização da ação de extensão*/
    @OneToMany(mappedBy = "atividade")
	private List<LocalRealizacao> locaisRealizacao = new ArrayList<LocalRealizacao>();
	
    /** Representa o tipo de Ação de Extensão. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_atividade_extensao")
    private TipoAtividadeExtensao tipoAtividadeExtensao = new TipoAtividadeExtensao();

    /** Edital ao qual a atividade está associada. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_edital")
    private EditalExtensao editalExtensao = new EditalExtensao();

    /** Linha de Atuação ao qual a atividade está associada. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_linha_atuacao")
    private EditalExtensaoLinhaAtuacao linhaAtuacao = new EditalExtensaoLinhaAtuacao();
    
    /** Uma ação de extensão é um projeto. Para obter dados mais gerais
       da ação de extensão veja a tabela projetos.projeto */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_projeto_extensao")
    private ProjetoExtensao projetoExtensao;

    /** Curso. Se a ação for do tipo curso esta coluna informa o id do curso. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_curso_evento")
    private CursoEventoExtensao cursoEventoExtensao;

    /** Se a ação for do tipo programa esta coluna informa o id do programa.     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_programa")
    private ProgramaExtensao programaExtensao;

    /** Se a ação for do tipo produto esta coluna informa o id do produto.   */ 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_produto")
    private ProdutoExtensao produtoExtensao;
    
    /** 
     * Determina qual a situação desta ação de extensão.
     * Verificar que esta situação deve estar sincronizada com a situação de
     * projeto. As situações podem ser diferentes somente nos casos de Ações 
     * Acadêmicas Associadas.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_situacao_projeto")
    private TipoSituacaoProjeto situacaoProjeto = new TipoSituacaoProjeto();


    /** Outras unidades envolvidas na execução da Ação. */
    @OneToMany(mappedBy = "atividade")
    private Collection<AtividadeUnidade> unidadesProponentes = new ArrayList<AtividadeUnidade>();

    /** Representa os grupo de pesquisa ao qual a ação de extensão está vinculada.
     Propostas de ações de tipo projeto normalmente tem vínculo com algum grupo de
     pesquisa já existente. */
    @OneToMany(mappedBy = "atividade")
    private Collection<AtividadeGrupoPesquisa> atividadeGruposPesquisa;

    /** Relaciona uma Ação de Extensão com todas as suas entidades financiadoras. */
    @OneToMany(mappedBy = "atividade")
    private Collection<AtividadeEntidadeFinanciadora> atividadeEntidadesFinanciadoras;

    /** epresenta a autorização dada pelo chefe de departamento que
    	participa da ação de extensão através de seus docentes/técnicos
    	administrativos. */ 
    @OneToMany(mappedBy = "atividade")
    private Collection<AutorizacaoDepartamento> autorizacoesDepartamentos = new ArrayList<AutorizacaoDepartamento>();
    
    /** SubAtividades da AtividadeExtencao. Pode ser preenchida quando a atividade for um curso ou evento  */
    @OneToMany(mappedBy = "atividade")
    private Collection<SubAtividadeExtensao> subAtividadesExtensao = new ArrayList<SubAtividadeExtensao>();

    /** Representa um plano de trabalho de um Discente de extensão. */
    @OneToMany(mappedBy = "atividade")
    @OrderBy("dataCadastro ASC")
    private Collection<PlanoTrabalhoExtensao> planosTrabalho = new ArrayList<PlanoTrabalhoExtensao>();

    /** Avaliações da Ação de Extensão pelos membros da PROEx ou por docentes nomeados pela PROEx.*/
    @OneToMany(mappedBy = "atividade")
    private Collection<AvaliacaoAtividade> avaliacoes = new ArrayList<AvaliacaoAtividade>();

    /** Representa uma inscrição de um discente para a seleção de uma ação de extensão. */
    @OneToMany(mappedBy = "atividade")
    private Collection<InscricaoSelecaoExtensao> inscricoesSelecao = new ArrayList<InscricaoSelecaoExtensao>();

    
    /** Discentes participantes da Ação de Extensão. */
    @OneToMany(mappedBy = "atividade")
    private Collection<DiscenteExtensao> discentesSelecionados = new ArrayList<DiscenteExtensao>();


    /** Relatórios da Ação de Extensão(Final ou Parcial).*/
    @OneToMany(mappedBy = "atividade")
    @OrderBy(value = "tipoRelatorio")
    private Collection<RelatorioAcaoExtensao> relatorios = new ArrayList<RelatorioAcaoExtensao>();

    /** Os participantes são, por exemplo, os alunos de um curso de extensão, ou os
        inscritos de um evento. Público alvo atendido.
    	Estes participantes são originários de inscrições on-line e de cadastros realizados pelo próprio coordenador. */
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

    /** Utilizado para verificar se o usuário está apenas registrando uma
   		atividade antiga (atualizando a base de dados).
     	Nestes casos, as validações para o simples registro de atividades são
     	diferentes. */
    @Column(name = "registro")
    private boolean registro = false;
    
	/** Arquivo do documento da atividade de extensão no SIGED */
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
	/** Token de segurança gerado para possibilitar a manipulação do documento no SIGED */
	@Column(name="security_token")
	private String securityToken;

	/** Armazena o programa Estratégico de Extensão */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_programa_estrategico")
    private ProgramaEstrategicoExtensao programaEstrategico = new ProgramaEstrategicoExtensao();

	/** Fundamentação Teórica */
	@Column(name = "fundamentacao_teorica")
	private String fundamentacaoTeorica;
    
    /** 
     * Determina se esta ação de extensão está ativa.
     * Este campo é utilizado no controle de exclusão lógica do sistema.
     * Verificar que este campo deve ser sincronizado com o campo ativo de projeto.
     * Somente divergências possíveis somente nos casos de Ações Acadêmicas Associadas.
     */
    private boolean ativo = true;
    
    

    /** Informa de a ação é vínculada a um Programa Estratégico */
    @Column(name = "vinculado_programa_estrategico", nullable = false)
    private boolean vinculadoProgramaEstrategico;

    /** Informa se a ação é continuação  */
    @Column(name = "projeto_formacao_continuada", nullable = false)
    private boolean projetoVinculadoFormacaoContinuada;
    
    /**
     *  É estranho mas, onde tem objetivos leia-se atividade.
     *  São as atividades a serem realizadas pelos membros para essa ação.
     *  
     *  Por exemplo, um membro tem que, supervisionar alguém, depois escrever o relatório, depois entregar o relatório, etc...
     */
	@OneToMany(mappedBy = "atividadeExtensao", fetch = FetchType.LAZY)
	private Collection<Objetivo> objetivo = new HashSet<Objetivo>();

    /** Informa se a ação é financiada pela unidade PROEX */
    @Column(name = "financ_proex", nullable = false)
    private boolean financProex = false;

    /** Se o edital tem financiamento externo.
     * 
     * Nesse caso não precisa indicar a fonte do financiamento.
     */
    @Column(name = "financ_externo_edital", nullable = false)
    private boolean financExternoEdital = false;

    /** Informa se a ação é financiada pela unidade proponente */
    @Column(name = "financ_unidade_proponente", nullable = false)
    private boolean financUnidProponente = false;

    /** Guarda a unidade executara da atividade quando não é uma unidade da instituição. */
    @Column(name = "unidade_executora_externa")
    private String unidadeExecutoraExterna;
    
    /** Não sei o que significa isso.*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_executor_financeiro")
	private ExecutorFinanceiro executorFinanceiro = new ExecutorFinanceiro();
    
	
	 /** Utilizado na view para seleção de ações vinculadas a programas e ações associadas.*/
    @Transient
    private boolean selecionado = false;


    /** Representa o total vagaras que fora abertas. 
	 *  Ou seja O somatório do número de vagas de todas as inscrições abertas.
	 * 
	 *  Esse número é calculado "on line"
	 *  
	 */
	@Transient
	private int numeroVagasAbertas;
	
	
	/** Representa o total de inscritos e APROVADOS pela coordenação do curso/evento.
	 * 
	 *  Esse número é calculado "on line"
	 */
	@Transient
	private int numeroInscritos;
	
	
	
	/** Representa o total de participantes para essa atividade.
	 * 
	 *  Esse número é calculado "on line"
	 */
	@Transient
	private int numeroParticipantes;
	
	
    
    /** Disponibiliza uma String com as notas de todas as avaliações realizadas.*/
    @Transient
    private String notasAvaliacoes;
    
    /** Utilizado para verificar se esta ação está sendo editada por um gestor de extensão. */
    @Transient
    private boolean edicaoGestor = false; 
    
    
    /** Utilizado para auxiliar apresentação de vagas em mini curso e mini eventos */
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

	/** Utilizado na comparação de 2 ações de extensão.*/
    @Override
    public boolean equals(Object obj) {
    	return EqualsUtil.testEquals(this, obj, "id");
    }
    
    /** Determina a identidade de uma Ação de Extensão. Utilizado na compração de objetos.*/
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
     * Adiciona um OrcamentoDetalhado a lista de orçamentos da atividade
     * 
     * @param orcamento
     * @return
     */
    public boolean addOrcamentoDetalhado(OrcamentoDetalhado orcamentoDetalhado) {
	orcamentoDetalhado.setProjeto(getProjeto());
	return getProjeto().getOrcamento().add(orcamentoDetalhado);
    }

    

    /**
     * Adiciona uma unidade proponente a atividade sem permitir a inclusão de
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
     * Adiciona uma sub atividade à atividade de extensao
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
     * Método utilizado para setar os orçamentos detalhados
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
     * Retorna de uma meneira muito errada as atividades ativas vinculada à essa atividade.
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
     * Informa quais são as regras do edital as quais esta ação deve estar subordinada. 
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
     * Utilizado pelo grupo permanente. Grupo permanente é um tipo de ação de
     * extensão bastante regular que ocorre já a muito tempo. É um tipo de ação
     * já consolidada.
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
     * Retorna o nome da fonte de financiamento da ação de extensão
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
	    		result.append("AÇÃO AUTO-FINANCIADA ");
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
    		result.append("NÃO DEFINIDO");

    	return result.toString();
    }

    public Collection<OrcamentoConsolidado> getOrcamentosConsolidados() {
	return getProjeto().getOrcamentoConsolidado();
    }

    /**
     * Método utilizado para setar os Orçamentos Consolidados
     * @param orcamentosConsolidados
     */
    public void setOrcamentosConsolidados(Collection<OrcamentoConsolidado> orcamentosConsolidados) {
	getProjeto().setOrcamentoConsolidado(orcamentosConsolidados);
    }

    public boolean isFinanciamentoExterno() {
	return getProjeto().isFinanciamentoExterno();
    }

    /**
     * Método utilizado para setar o Financiamento Externo
     * @param financiamentoExterno
     */
    public void setFinanciamentoExterno(boolean financiamentoExterno) {
	getProjeto().setFinanciamentoExterno(financiamentoExterno);
    }

    public ClassificacaoFinanciadora getClassificacaoFinanciadora() {
	return getProjeto().getClassificacaoFinanciadora();
    }

    /**
     * Método utilizado para setar a Classificação Financiadora
     * @param classificacaoFinanciadora
     */
    public void setClassificacaoFinanciadora(ClassificacaoFinanciadora classificacaoFinanciadora) {
	getProjeto().setClassificacaoFinanciadora(classificacaoFinanciadora);
    }

    public String getEditalExterno() {
	return getProjeto().getDescricaoFinanciadorExterno();
    }

    /**
     * Método utilizado para setar o Edital Externo
     * @param descricaoFinanciadorExterno
     */
    public void setEditalExterno(String descricaoFinanciadorExterno) {
	getProjeto().setDescricaoFinanciadorExterno(descricaoFinanciadorExterno);
    }

    public boolean isFinanciamentoInterno() {
	return getProjeto().isFinanciamentoInterno();
    }

    /**
     * Método utilizado para setar o Financiamento Interno
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
     * Ações de extensão executadas anteriormente solicitam registro na proex
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
     * Retorna o coordenador da ação de extensão
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
     * Método utilizado para das informações sober o membro.
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
     * Método utilizado para informar os planos de Trabalho
     * @return
     */
    public Collection<PlanoTrabalhoExtensao> getPlanosTrabalho() {
	// Removendo os excluídos da lista..
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
     * próprio
     * 
     * @return
     */
    public boolean isAutoFinanciado() {
	return getProjeto().isAutoFinanciado();
    }

    /**
     * Método utilizado para setar o autoFinanciamento
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
     * Total estimado de discentes envolvidos na atividade de extensão.
     * Informado durante o cadastro da atividade e utilizado na análise da
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
     * Lista de todas as avaliações ativas.
     * 
     * @return {@link Collection} de {@link AvaliacaoAtividade} com lista de avaliações.
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
     * Método utilizado para informar as inscrições da seleção
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
     * Método utilizado para informar os discentes selecionados
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
     * Retorna total de membros do comitê avaliando a ação (com avaliações
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
     * Retorna total de pareceristas avaliando a ação
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
     * Método utilizado para setar as solicitações de Reconsideração
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
     * Método utilizado para setar o tipo de Região
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
     * Retorna o código da ação de extensão. Ações com o xx no código não foram
     * aprovadas pelos membros do comitê ainda.
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
     * Retorna true se a ação está aprovada ou em execução
     */
    public boolean isAprovadoEmExecucao() {
	return ((getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_EM_EXECUCAO) || 
		(getProjeto().getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_EM_EXECUCAO));
    }

    /**
     * Retorna true se a ação está aprovada ou em execução
     */
    public boolean isValida() {
	Integer situacaoAtual = getProjeto().getSituacaoProjeto().getId();
	return	Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO).contains(situacaoAtual)
		|| Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_EM_EXECUCAO).contains(situacaoAtual) ;
    }


    /**
     * Retorna true se a ação pode ser editada
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
     * Método utilizado para setar o atributo semFinanciamento do projeto.
     * @param semFinanciamento
     */
    public void setSemFinanciamento(boolean semFinanciamento) {
	getProjeto().setSemFinanciamento(semFinanciamento);
    }

    /**
     * Retorna somente relatórios ativos da ação atual
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
     * Retorna lista de participantes 'ativos' da ação de extensão 'ordenados' por nome.
     * (Publico alvo atendido)
     * 
     * @return
     */
    public Collection<ParticipanteAcaoExtensao> getParticipantesOrdenados() {
    	Collections.sort(getParticipantesNaoOrdenados());
    	return participantes;
    }
    
    /**
     * Retorna lista de participantes 'ativos' da ação de extensão sem ordenação.
     * Melhora o desempenho na manipulação de listas de participantes. 
     * 
     * @return
     */
    public List<ParticipanteAcaoExtensao> getParticipantesNaoOrdenados() {
    	// Removendo os excluídos da lista..
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
     * Informa se esta ação de extensão está apta para receber inscrições de
     * interessados em participar da equipe
     * 
     * @return
     */
    public boolean isAbertoParaSelecao() {
	return isAprovadoEmExecucao() && getBolsasSolicitadas() > 0;
    }

    /**
     * Informa se a ação de extensão foi analisada e aprovada pela proex
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
     * Retorna a quantidade de participantes da ação de extensão
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
     * Método utilizado para setar a unidade do projeto da Atividade de Exetnsão.
     * @param unidade
     */
    public void setUnidade(Unidade unidade) {
	getProjeto().setUnidade(unidade);	    
    }

    public Unidade getUnidade() {
	return getProjeto().getUnidade();
    }
    
    /** Retorna a sigla da unidade em questão de forma invertida. 
     *  Utilizada na listagem das atividades de extensão.  
     */
    public String getSiglaUnidadeInsertida() {
    	String[] sigla = getProjeto().getUnidade().getSigla().split("/");
    	if ( sigla.length == 1 )
    		return getProjeto().getUnidade().getSigla();
    	else
    		return sigla[1] + "/" + sigla[0];
	}

    /**
     * Método utilizado para setar o ano do projeto da Atividade de Extensão.
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
     * Método utilizado para setar a área de conhecimento CNPQ do projeto da Ativadade de Exetnsão.
     * @param areaCNPQ
     */
    public void setAreaConhecimentoCnpq(AreaConhecimentoCnpq areaCNPQ) {
	getProjeto().setAreaConhecimentoCnpq(areaCNPQ);
    }

    /**
     * Método utilizado para setar o tipo de situação do projeto da Ativiadde de Extensão.
     * @param tipoSituacaoProjeto
     */
    public void setSituacaoProjeto(TipoSituacaoProjeto tipoSituacaoProjeto) {
    	situacaoProjeto = tipoSituacaoProjeto;
    	getProjeto().setSituacaoProjeto(situacaoProjeto);
    }
    
    /** 
     * Situação na qual o projeto se encontra (Ex.: Concluído, Cancelado, Aguardando Avaliação) 
     */
    public TipoSituacaoProjeto getSituacaoProjeto() {
	return situacaoProjeto;	    
    }

    public Collection<ArquivoProjeto> getArquivos() {
	return getProjeto().getArquivos();
    }

    /**
     * Método utilizado para setar o Collection de arquivos do projeto da Atividade de Extensão
     * @param arquivos
     */
    public void setArquivos(Collection<ArquivoProjeto> arquivos) {
	getProjeto().setArquivos(arquivos);
    }

    /**
     * Método utilizado para adicionar um arquivo ao projeto da Atividade de Extensão
     * @param arquivo
     */
    public void addArquivo(ArquivoProjeto arquivo) {
	getProjeto().addArquivo(arquivo);	    
    }

    /**
     * Método utilizado para adicionar uma nova foto ao projeto da Atividade de Extensão.
     * @param novaFoto
     */
    public void addFoto(FotoProjeto novaFoto) {
	getProjeto().addFoto(novaFoto);	    
    }

    public Collection<FotoProjeto> getFotos(){
	return getProjeto().getFotos();
    }

    /**
     * Método utilizado para setar o Collection de fotos do projeto da Atividade de Extensão.
     * @param fotos
     */
    public void setFotos(Collection<FotoProjeto> fotos){
    	getProjeto().setFotos(fotos);
    }

    public String getTitulo() {
    	return getProjeto().getTitulo();
    }

    /**
     * Método utilizado para setar o título do projeto da Atividade de Extensão
     * @param titulo
     */
    public void setTitulo(String titulo) {
    	getProjeto().setTitulo(titulo);
    }

    /**
     * Método utilizado para setar a data de início do projeto da Atividade de Extensão.
     * @param data
     */
    public void setDataInicio(Date data) {
	getProjeto().setDataInicio(data);
    }

    public Date getDataInicio() {
	return getProjeto().getDataInicio();
    }

    /**
     * Método utilizado para setar a data fim do projeto da Atividade de Extensão
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
     * Método utilizado para setar a metodologia
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado para setar a justificativa
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado para setar o histórico
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado apra setar o registro de entrada.
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
     * </ul>
     * @param reg
     */
    public void setRegistroEntrada(RegistroEntrada reg) {
	getProjeto().setRegistroEntrada(reg);
    }

    /**
     * Método utilizado apra setar a data de cadastro
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado apra setar o número institucional
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado apra setar os membros da equipe
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado para realizar validações na entidade AtividadeExtensão
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
						lista.addErro("Não é possível remover esse membro pois há atividade(s) vínculadas para o mesmo.");
						break achou;
					}
				}
			}
		}
    	return lista;
    }
    
    /**
     * Informa se este projeto faz parte de uma 
     * Ação Acadêmica Associada. 
     * 
     * @return
     */
    public boolean isProjetoAssociado() {
	return getProjeto().isProjetoAssociado();
    }

    /**
     * Informa se é um projeto isolado.
     * 
     * @return
     */
    public boolean isProjetoIsolado() {
	return getProjeto().isProjetoIsolado();
    }


    /**
     * Informa se a ação de extensão está sendo renovada.
     * 
     * @return
     */
    public Boolean getRenovacao() {
        return getProjeto().getRenovacao();
    }

    /**
     * Método utilizado apra setar se há ou não renovação
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado apra setar o resumo
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado apra setar as refenrências
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
     * </ul>
     * @param referencias
     */
    public void setReferencias(String referencias) {
	this.getProjeto().setReferencias(referencias);
    }

    /**
     * Método utilizado apra setar os Objetivos
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado apra setar os Resultados
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Método utilizado apra setar os Objetivos Especificos
     * <br>
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>Nao é chamdo por JSP(s)</li>
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
     * Indica se a ação foi finalizada.
     * 
     * @return
     */
    public boolean isFinalizada() {
    	return getProjeto().isFinalizado();
    }

    /**
     * Verifica se o edital desta ação de extensão ainda está em aberto.
     * Utilizado para habilitar o campo de preenchimento de edital no formulário
     * de cadastro da ação.
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
	    return false; //edital desta ação não está mais aberto.
	}
	return true; //ação ainda não selecionou um edital.
    }
    
    /** verifica se é permito enviar email de notificação na falta do relatório final */
    public boolean isPermitidoEnviarEmailNotificacaoFaltaDeRelatorioFinal() {
    	
    	if (isEnviouRelatorioFinal()) {
    		return false;
    	}
    	
    	//Só poderá enviar outra notificação X dias após o envio da última notificação.
    	//Irei substituir pelo método da arquitetura que soma datas, caso exista.
    	if(dataEmailCobrancaRelatorioFinal != null) {
    		
    		Calendar calendar = Calendar.getInstance();  
    		calendar.setTime( dataEmailCobrancaRelatorioFinal );  
    		calendar.add( Calendar.DAY_OF_MONTH , INTERVALO_DIAS_PERMITIR_EMAIL );	
    		
    		if( (new Date()).before(calendar.getTime()) ) {
    			return false;
    		}    	
    	}
    	
    	//Só poderá enviar notificação se coordenador possuir email cadastrado
    	if(getCoordenacao().getPessoa().getEmail() == null ){
    		return false;
    	}
    	
    	return true;    	
    }

    
    /** Verifica se o relatório final desta ação já foi enviado. */
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
	 * Retorna o total de dias restantes para a conclusão da
	 * ação de extensão.
	 * 
	 * @return valores negativos indicam que a ação já passou do prazo de conclusão.
	 */
	public long getTotalDiasConclusao() {		
		return CalendarUtils.calculoDias(
				CalendarUtils.configuraTempoDaData(new Date(), 0, 0, 0, 0),
				CalendarUtils.configuraTempoDaData(getDataFim(), 0, 0, 0, 0) );			
	}
	
	/**
	 * Verifica se a data para a conclusão da ação é posterior ou anterior à atual.
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
	 * Verifica se a ação pode ser distribuída para iniciar a avaliação.
	 * 
	 * @return
	 */
	public boolean isPermitidoIniciarAvaliacao() {
	    return  isProjetoIsolado() 
	    			&& (getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO 
	    			|| getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_SUBMETIDO); 
	}
	
	/** retorna total de vagas remuneradas disponíveis   */
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

	/** Público total da ação de extensão */
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
	 * Determina se um avaliador Ad Hoc pode avaliar esta ação de extensão.
	 * O avaliador Ad Hoc é responsável por avaliar o mérito acadêmico da ação de extensão
	 * definindo uma nota para a proposta baseado no formulário de avaliação disponibilizado
	 * pela Pró-Reitoria de Extensão.
	 * 
	 * @return
	 */
	public boolean isPermitidoAdHocAvaliar() {
	    return  (getSituacaoProjeto().getId() == TipoSituacaoProjeto.EXTENSAO_AGUARDANDO_AVALIACAO); //se a ação ainda está aguardando avaliação 
	}

	/**
	 * Retorna uma String de notas de todas a avaliações realizadas para esta ação. 
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
	 * Determina se esta ação de extensão pode ser Reativada.
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

	/**Retorna apenas os objetivos da ação que estão ativos.*/
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

	/**retorna apenas os locais de realização da ação, que estão ativos.*/
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