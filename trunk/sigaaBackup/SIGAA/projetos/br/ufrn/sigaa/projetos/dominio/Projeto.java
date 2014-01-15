/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ArrayUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.OrcamentoConsolidado;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;


/**
 * Essa Entidade representa um projeto pode ser de pesquisa, extensão, monitoria.
 * Informando dentre outras dados o título do projeto, um breve resumo do mesmo, algumas palavras chaves, ano
 * de referência do projeto, situação que o projeto se encontra, o coordenador do projeto, dentre outras
 * informações sobre o projeto.  
 *
 * 
 * @author Gleydson
 * @author ilueny santos
 */
@Entity
@Table(name = "projeto", schema = "projetos")
public class Projeto extends AbstractMovimento implements Comparable<Projeto>{

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="projetos.projeto_sequence") })
	@Column(name="id_projeto")           
	private int id;

	/** Título do projeto */
	private String titulo;
	
	/** Número de bolsas concedidas pelo comitê avaliador. */
	@Column(name = "bolsas_concedidas")
	private Integer bolsasConcedidas = 0;

	/** Número de bolsas solicitadas pelo coordenador. */
	@Column(name = "bolsas_solicitadas")
	private Integer bolsasSolicitadas = 0;

	/** Total estimado de discentes envolvidos ação acadêmica. */
	@Column(name = "total_discentes_envolvidos")
	private Integer totalDiscentesEnvovidos = 0;	

	/** Descrição do Projeto */
	private String descricao;

	/** Resumo do Projeto */
	private String resumo;

	/** Metodologia do projeto */
	private String metodologia;

	/** Objetivo do Projeto */
	private String objetivos;
	
	/** Objetivo do Projeto */
	@Column(name = "objetivos_especificos")
	private String objetivosEspecificos;
	
	/** Indica se o projeto é cadastrado com apoio da Universidade. (concorre a edital interno)*/
	private boolean interno = true;
	
    /** Informa se a ação acadêmica possui algum tipo de convênio com a Fundação da IFES. */
    private boolean convenio = false;

	/** Email referente ao projeto */
	private String email;

	/** Justificativa para o Projeto */
	private String justificativa;

	/** Resultados esperados com a execução do projeto */
	private String resultados;

	/** Referente as referências usadas  */
	private String referencias;
	
	/** Palavras consideradas chave no Projeto */
	@Column(name = "palavras_chave")
	private String palavrasChave;

	/** Ano de referência do Projeto */
	private Integer ano;

	/** Data de cadastro do Projeto*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Data de início do Projeto */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio")
	private Date dataInicio;

	/** Data final do Projeto */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim")
	private Date dataFim;

	/** Situação na qual o projeto se encontra (Ex.: Concluído, Cancelado, Aguardando Avaliação) */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_situacao_projeto")
	private TipoSituacaoProjeto situacaoProjeto = new TipoSituacaoProjeto();

	/** Referente ao município e demais dados referente ao projeto. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade")
	private Unidade unidade = new Unidade();

	/** Histórico das Situações anteriores do Projeto */
	@OneToMany(cascade = {CascadeType.ALL}, mappedBy = "projeto")
	@OrderBy(value="data")
	private Collection<HistoricoSituacaoProjeto> historicoSituacao = new ArrayList<HistoricoSituacaoProjeto>();

	/** Usuário que cadastrou o projeto */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Arquivo que pode ser associado a o projeto */
	@Column(name = "id_arquivo")
	private Integer idArquivo;
	
	/** Serve para informar se o Projeto está ou não em uso. */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo;

	/** Número Institucional */
	@Column(name = "numero_institucional")
	private Integer numeroInstitucional;
	
	/** Data de atualização do Projeto*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;
	
	/** Usuário que atualizou o projeto */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;
	
	/** Foto pode ser colocada. */
	@OneToMany(mappedBy = "projeto")
	private Collection<FotoProjeto> fotos = new ArrayList<FotoProjeto>();

	/** Adição de Arquivos  para divulgação do projeto. */
	@OneToMany(mappedBy = "projeto")
	private Collection<ArquivoProjeto> arquivos = new ArrayList<ArquivoProjeto>();

	/** Representa o orçamento solicitado para execução do projeto*/
	@OneToMany(mappedBy = "projeto")
	@OrderBy("elementoDespesa ASC")
	private Collection<OrcamentoDetalhado> orcamento = new ArrayList<OrcamentoDetalhado>();

	/** Representa o orçamento a totalização do orçamento solicitado e quando o projeto */
	@OneToMany(mappedBy = "projeto")
	@OrderBy("elementoDespesa ASC")
	private Collection<OrcamentoConsolidado> orcamentoConsolidado = new ArrayList<OrcamentoConsolidado>();
	
	/** Representa um membro do projeto */
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "projeto")
	private Collection<MembroProjeto> equipe = new ArrayList<MembroProjeto>();

	/** Cronograma das atividades referentes a projetos */
	@IndexColumn(name = "ordem", base = 1)
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "projeto")
	private List<CronogramaProjeto> cronograma = new ArrayList<CronogramaProjeto>(0);

	/** Serve para representar áreas de conhecimento do Conselho Nacional de Desenvolvimento Científico e 
	 * Tecnológico. 
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCnpq = new AreaConhecimentoCnpq();
	
	/** Informa a área de Abrangência do Projeto. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_regiao")
	private TipoRegiao abrangencia = new TipoRegiao(TipoRegiao.LOCAL);

	/** Membro coordenador do projeto */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_coordenador")
	private MembroProjeto coordenador;

	/** Informar o tipo de Edital no qual o projeto está inserido. (Ex.: Associado, Monitoria...) */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_edital")
	private Edital edital = new Edital();
	
	/** Relatórios Projeto.*/
    @OneToMany(mappedBy = "projeto", fetch = FetchType.LAZY )
    @OrderBy(value = "tipoRelatorio")
    private Collection<RelatorioAcaoAssociada> relatorios = new ArrayList<RelatorioAcaoAssociada>();
    
    /** Lista de discentes (bolsistas e voluntários) que participam do projeto.*/
    @OneToMany(mappedBy = "projeto", fetch = FetchType.LAZY)
    @OrderBy("dataInicio ASC")
    private Collection<DiscenteProjeto> discentesProjeto = new ArrayList<DiscenteProjeto>();

	/** Utilizado no cadastro do projeto base */
	/** Dimensão acadêmica da proposta */	
	private boolean ensino = false;
	
	/** Atributo utilizado a dimensão acadêmica da proposta */
	private boolean pesquisa = false;
	
	/** Atributo utilizado a dimensão acadêmica da proposta */
	private boolean extensao = false;
	
	
	/** Modalidades de projeto integrado */
	@Column(name = "programa_extensao")
	private boolean programaExtensao = false;

	/** Atributo utilizado para representar a modalidade de Projeto de Extensão para o projeto integrado */
	@Column(name = "projeto_extensao")
	private boolean projetoExtensao = false;
	
	/** Atributo utilizado para representar a modalidade de Curso de Extensão para o projeto integrado */
	@Column(name = "curso_extensao")
	private boolean cursoExtensao = false;
	
	/** Atributo utilizado para representar a modalidade de Evento de Extensão para o projeto integrado */
	@Column(name = "evento_extensao")
	private boolean eventoExtensao = false;

	/** Atributo utilizado para representar a modalidade de Programa de Monitoria para o projeto integrado */
	@Column(name = "programa_monitoria")
	private boolean programaMonitoria = false;

	/** Atributo utilizado para representar a modalidade de Melhoria e Qualidade de Ensino para o projeto integrado */
	@Column(name = "melhoria_qualidade_ensino")
	private boolean melhoriaQualidadeEnsino = false;

	/** Atributo utilizado para representar a modalidade de Apoi de Grupo de Pesquisa para o projeto integrado */
	@Column(name = "apoio_grupo_pesquisa")
	private boolean apoioGrupoPesquisa = false;

	/** Atributo utilizado para representar a modalidade de Apoio à novos Pesquisadores para o projeto integrado */
	@Column(name = "apoio_novos_pesquisadores")
	private boolean apoioNovosPesquisadores = false;
	
	/** Atributo utilizado para representar a modalidade de Iniciação Científica para o projeto integrado */
	@Column(name = "iniciacao_cientifica")
	private boolean iniciacaoCientifica = false;
	
	/** Atributo utilizado para representar a modalidade de Finaciamento Interno para o projeto integrado */
	/** Tipos de financiamento do projeto */
	@Column(name = "financiamento_interno")
	private boolean financiamentoInterno;

	/** Atributo utilizado para informar se o projeto, após a avaliação final do gestor, recebeu finaciamento interno*/
	@Column(name = "recebeu_financiamento_interno")
	private boolean recebeuFinanciamentoInterno;
	
	/** Atributo utilizado para representar a modalidade de Finaciamento Externo para o projeto integrado */
	@Column(name = "financiamento_externo")
	private boolean financiamentoExterno;

	/** Atributo utilizado para representar a modalidade de Auto Finaciamento para o projeto integrado */
	@Column(name = "auto_financiado")
	private boolean autoFinanciado;

	/** Atributo utilizado para representar a modalidade de Sem Finaciamento para o projeto integrado */
	@Column(name = "sem_financiamento")
	private boolean semFinanciamento;

	/** Atributo utilizado para representar a modalidade de Renovação para o projeto integrado */
	@Column(name = "renovacao", nullable = false)
	private Boolean renovacao = false;

	/** Atributo utilizado para representar a classificação financiadora */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_classificacao_financiadora")
	private ClassificacaoFinanciadora classificacaoFinanciadora = new ClassificacaoFinanciadora();

	/** Atributo utilizado para representar a descrição do financiador Externo */
	@Column(name = "descricao_financiador_externo")
	private String descricaoFinanciadorExterno;
	
	/** Atributo utilizado para representar as solicitações de Reconsideração */
	@OneToMany(mappedBy = "projeto")
	private Collection<SolicitacaoReconsideracao> solicitacoesReconsideracao = new ArrayList<SolicitacaoReconsideracao>();

	/** Referente a unidade orçamentária a qual o projeto está associado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade_orcamentaria")
	private Unidade unidadeOrcamentaria = new Unidade();
	
	/** Representa a autorização dada pelo chefe de departamento que participa do projeto através de seus docentes/técnicos administrativos. */ 
	@OneToMany(mappedBy = "projeto")
	private Collection<AutorizacaoDepartamento> autorizacoesDepartamentos = new ArrayList<AutorizacaoDepartamento>();

	/** Representa a classificação que o projeto recebeu na avaliação realizada pelos membros do comitê.*/
	@Column(name = "classificacao")
	private Integer classificacao;
	
	/** Representa a nota que o projeto recebeu na avaliação realizada pelos membros do comitê.*/
	@Column(name = "media")
	private Double media;

	/** Representa a lista de avaliações as quais o projeto foi submetido.*/
	@OneToMany(mappedBy = "projeto")
	private List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();
	
	/** Representa uma inscrição de um discente para a seleção de uma ação associada. */
    @OneToMany(mappedBy = "projeto")
    private Collection<InscricaoSelecaoProjeto> inscricoesSelecao = new ArrayList<InscricaoSelecaoProjeto>();
    
	/** Informa o tipo de projeto. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_projeto", nullable = false)    	
	private TipoProjeto tipoProjeto;
	
	/**  Informa que o Gestor autorizou a emissão dos certificados do projeto. Utilizado para projetos que precisam imprimir o certificados antes da conclusão. */
	@Column(name = "autorizar_certificado_gestor")
	private boolean autorizarCertificadoGestor = false;


	
	/** Utilizado na verificação dos cadastros das modalidades do projeto */
	@Transient
	private boolean programaExtensaoSubmetido = false;

	/** Atributo projetoExtensaoSubmetido utilizado na verificação dos cadastros das modalidades do projeto integrado */
	@Transient
	private boolean projetoExtensaoSubmetido = false;
	
	/** Atributo cursoExtensaoSubmetido utilizado na verificação dos cadastros das modalidades do projeto integrado */
	@Transient
	private boolean cursoExtensaoSubmetido = false;
	
	/** Atributo eventoExtensaoSubmetido utilizado na verificação dos cadastros das modalidades do projeto integrado */
	@Transient
	private boolean eventoExtensaoSubmetido = false;

	/** Atributo programaMonitoriaSubmetido utilizado na verificação dos cadastros das modalidades do projeto integrado */
	@Transient
	private boolean programaMonitoriaSubmetido = false;

	/** Atributo melhoriaQualidadeEnsinoSubmetido utilizado na verificação dos cadastros das modalidades do projeto integrado */
	@Transient
	private boolean melhoriaQualidadeEnsinoSubmetido = false;

	/** Atributo apoioGrupoPesquisaSubmetido utilizado na verificação dos cadastros das modalidades do projeto integrado */
	@Transient
	private boolean apoioGrupoPesquisaSubmetido = false;

	/** Atributo apoioNovosPesquisadoresSubmetido utilizado na verificação dos cadastros das modalidades do projeto integrado */
	@Transient
	private boolean apoioNovosPesquisadoresSubmetido = false;
	
	/** Atributo iniciacaoCientificaSubmetido utilizado na verificação dos cadastros das modalidades do projeto integrado */
	@Transient
	private boolean iniciacaoCientificaSubmetido = false;
	
	/** Atributo utilizado para representar a String Código do Projeto */
	@Transient
	private String codigoProjeto;
	
	/** Atributo utilizado para representar se o projeto está ou não selecionado */
	@Transient
	private boolean selecionado;
	
	@Transient
	private String tipoAcaoExtensao;
	
	public Projeto() {		
	}

	/** Construtor simplificado. */
	public Projeto(int id) {
		this.id = id;
	}

	/** Construtor simplificado. */
	public Projeto(TipoProjeto tipo) {
		this.tipoProjeto = tipo;
	}

	/** Retorna a chave primária */
	public int getId() {
		return id;
	}

	/** Seta a chave primária */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a data do cadastro */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data de Cadastro */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Retorna a data Final */
	public Date getDataFim() {
		return dataFim;
	}

	/** Seta a data Final */
	public void setDataFim(Date dataFim) {
		this.dataFim = CalendarUtils.descartarHoras(dataFim);
	}

	/** Retorna a data Inicial */
	public Date getDataInicio() {
		return dataInicio;
	}

	/** Seta a data Inicial */
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = CalendarUtils.descartarHoras(dataInicio);
	}

	/** Retorna o Título */
	public String getTitulo() {
		return titulo;
	}
	
	/** Retorna o Ano - Título do projeto */
	public String getAnoTitulo(){
		return getAno() + " - " + getTitulo();
	}
	
	/** Seta o título do projeto */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Retorna a Descrição do Projeto */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descrição do projeto */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Retorna o email */
	public String getEmail() {
		return email;
	}

	/** Seta o Email */
	public void setEmail(String email) {
		this.email = email;
	}

	/** Retorna a Metodologia do Projeto */
	public String getMetodologia() {
		return metodologia;
	}

	/** Seta a Metodologia do Projeto */
	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	/** Retorna os Objetivos do Projeto */
	public String getObjetivos() {
		return objetivos;
	}

	/** Seta os Objetivos do Projeto */
	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	/** Retorna o Ano do projeto */
	public Integer getAno() {
		return ano;
	}

	/** Seta o Ano do projeto */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/** Retorna as palavras chave   */
	public String getPalavrasChave() {
		return palavrasChave;
	}

	/** Seta as palavras chave */
	public void setPalavrasChave(String palavasChave) {
		this.palavrasChave = palavasChave;
	}

	/** Retorna a situação do Projeto */
	public TipoSituacaoProjeto getSituacaoProjeto() {
		return situacaoProjeto;
	}

	/** Seta a situação do Projeto */
	public void setSituacaoProjeto(TipoSituacaoProjeto situacaoProjeto) {
		this.situacaoProjeto = situacaoProjeto;
	}

	/** Retorna a Unidade do Projeto */
	public Unidade getUnidade() {
		return unidade;
	}

	/** Seta a Unidade do Projeto */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/** Retorna o histórico da Situação do Projeto */
	public Collection<HistoricoSituacaoProjeto> getHistoricoSituacao() {
		return historicoSituacao;
	}

	/** Seta o histórico de Situação do Projeto */
	public void setHistoricoSituacao(
			Collection<HistoricoSituacaoProjeto> historicoSituacao) {
		this.historicoSituacao = historicoSituacao;
	}

	/** Adiciona um histórico ao projeto */
	public void addHistoricoSituacao(HistoricoSituacaoProjeto historicoSituacaoProjeto) {
		historicoSituacaoProjeto.setProjeto(this);
		if (historicoSituacao == null)
			historicoSituacao = new ArrayList<HistoricoSituacaoProjeto>(0);
		historicoSituacao.add(historicoSituacaoProjeto);
	}

	/** Retorna a Justificativa do Projeto */
	public String getJustificativa() {
		return justificativa;
	}

	/** Seta a Justificativa do Projeto */
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	/** Retorna o registro de entrada do Projeto */
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o registro de entrada do projeto */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	/** Retorna a id do arquivo */
	public Integer getIdArquivo() {
		return idArquivo;
	}

	/** Seta o id do arquivo */
	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	/** Retorna o status do projeto se está em uso ou não. */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta os status do projeto */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** Retorna o Número Institucional */
	public Integer getNumeroInstitucional() {
		return numeroInstitucional;
	}

	/** Seta o Número Institucional */
	public void setNumeroInstitucional(Integer numeroInstitucional) {
		this.numeroInstitucional = numeroInstitucional;
	}
	
	/** Adiciona uma foto ao projeto */
	public boolean addFoto(FotoProjeto foto) {
		foto.setProjeto(this);
		return fotos.add(foto);
	}
	
	/** Adiciona um arquivo ao projeto */
	public boolean addArquivo(ArquivoProjeto arquivo) {
		arquivo.setProjeto(this);
		return getArquivos().add(arquivo);
	}
	
	/** Retorna as fotos do projeto */
	public Collection<FotoProjeto> getFotos() {		
		return fotos;
	}

	/** Seta as fotos do projeto */
	public void setFotos(Collection<FotoProjeto> fotos) {
		this.fotos = fotos;
	}

	/** Retorna o orçamento do projeto */
	public Collection<OrcamentoDetalhado> getOrcamento() {
	    return orcamento;
	}

	/** Seta o Orçamento do projeto */
	public void setOrcamento(Collection<OrcamentoDetalhado> orcamento) {
	    this.orcamento = orcamento;
	}

	/** Retorna a equipe do projeto */
	public Collection<MembroProjeto> getEquipe() {
		// removendo os excluídos da lista..
		if (equipe != null) {
		    for (Iterator<MembroProjeto> it = equipe.iterator(); it.hasNext();) {
		    	MembroProjeto mb = it.next(); 
		    	if ( mb != null && !mb.isAtivo()) {
		    		it.remove();
		    	 }
		    }
		}
	    return equipe;
	}

	/** Seta a equipe do Projeto */
	public void setEquipe(Collection<MembroProjeto> equipe) {
	    this.equipe = equipe;
	}

	/** Retorna o cronograma do projeto */
	public List<CronogramaProjeto> getCronograma() {
	    return cronograma;
	}

	/** Seta o cronograma do projeto */
	public void setCronograma(List<CronogramaProjeto> cronograma) {
	    this.cronograma = cronograma;
	}

	/** Retorna a Área de Conhecimento Cnpq */
	public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
	    return areaConhecimentoCnpq;
	}

	/** Seta a Área de Conhecimento Cnpq */
	public void setAreaConhecimentoCnpq(AreaConhecimentoCnpq areaConhecimentoCnpq) {
	    this.areaConhecimentoCnpq = areaConhecimentoCnpq;
	}

	/** Retorna o Resumo */
	public String getResumo() {
	    return resumo;
	}

	/** Seta o resumo */
	public void setResumo(String resumo) {
	    this.resumo = resumo;
	}

	/** Retorna os Resultados */
	public String getResultados() {
	    return resultados;
	}

	/** Seta os resultados */
	public void setResultados(String resultados) {
	    this.resultados = resultados;
	}

	/** Retorna as Referências */
	public String getReferencias() {
	    return referencias;
	}

	/** Seta as Referências */
	public void setReferencias(String referencias) {
	    this.referencias = referencias;
	}

	/** Retorna os arquivos */
	public Collection<ArquivoProjeto> getArquivos() {
	    return arquivos;
	}

	/** Seta os arquivos */
	public void setArquivos(Collection<ArquivoProjeto> arquivos) {
	    this.arquivos = arquivos;
	}

	/** Retorna a Abrangência do Projeto */
	public TipoRegiao getAbrangencia() {
	    return abrangencia;
	}

	/** Seta a Abrangência do Projeto */
	public void setAbrangencia(TipoRegiao abrangencia) {
	    this.abrangencia = abrangencia;
	}

	/** Retorna se o projeto trata-se um projeto de Ensino ou não */
	public boolean isEnsino() {
	    return ensino;
	}
	
	/** Seta um projeto do tipo Ensino */
	public void setEnsino(boolean ensino) {
	    this.ensino = ensino;
	}

	/** Retorna se o projeto trata-se um projeto de Pesquisa ou não */
	public boolean isPesquisa() {
	    return pesquisa;
	}

	/** Seta um projeto do Tipo Pesquisa */
	public void setPesquisa(boolean pesquisa) {
	    this.pesquisa = pesquisa;
	}

	/** Retorna se o projeto trata-se um projeto de Extensão ou não */
	public boolean isExtensao() {
	    return extensao;
	}

	/** Seta um projeto do Tipo Extensão */
	public void setExtensao(boolean extensao) {
	    this.extensao = extensao;
	}

	/** Retorna se o projeto trata-se um projeto de Programa Extensão ou não */
	public boolean isProgramaExtensao() {
	    return programaExtensao;
	}

	/** Seta um projeto do Tipo Programa Extensão */
	public void setProgramaExtensao(boolean programaExtensao) {
	    this.programaExtensao = programaExtensao;
	}

	/** Retorna se o projeto trata-se um projeto de Projeto Extensão ou não */
	public boolean isProjetoExtensao() {
	    return projetoExtensao;
	}

	/** Seta um projeto to tipo Projeto Extensão */
	public void setProjetoExtensao(boolean projetoExtensao) {
	    this.projetoExtensao = projetoExtensao;
	}

	/** Retorna se o projeto trata-se um projeto de Curso Extensão ou não */
	public boolean isCursoExtensao() {
	    return cursoExtensao;
	}

	/** Seta um projeto do tipo Curso Extensão */
	public void setCursoExtensao(boolean cursoExtensao) {
	    this.cursoExtensao = cursoExtensao;
	}

	/** Retorna se o projeto trata-se um projeto de Evento Extensão ou não */
	public boolean isEventoExtensao() {
	    return eventoExtensao;
	}

	/** Seta um projeto do tipo Evento Extensão */
	public void setEventoExtensao(boolean eventoExtensao) {
	    this.eventoExtensao = eventoExtensao;
	}

	/** Retorna se o projeto trata-se um projeto de Programa Monitoria ou não */
	public boolean isProgramaMonitoria() {
	    return programaMonitoria;
	}

	/** Seta um projeto do tipo Programa Monitoria */
	public void setProgramaMonitoria(boolean programaMonitoria) {
	    this.programaMonitoria = programaMonitoria;
	}

	/** Retorna se o projeto trata-se um projeto de Melhoria da Qualidade do Ensino ou não */
	public boolean isMelhoriaQualidadeEnsino() {
	    return melhoriaQualidadeEnsino;
	}

	/** Seta um projeto do tipo Melhoria da Qualidade de Ensino */
	public void setMelhoriaQualidadeEnsino(boolean melhoriaQualidadeEnsino) {
	    this.melhoriaQualidadeEnsino = melhoriaQualidadeEnsino;
	}

	/** Retorna se o projeto trata-se um projeto de Apoio Grupo Pesquisa ou não */
	public boolean isApoioGrupoPesquisa() {
	    return apoioGrupoPesquisa;
	}

	/** Seta um projeto do tipo Apoio Grupo Pesquisa */
	public void setApoioGrupoPesquisa(boolean apoioGrupoPesquisa) {
	    this.apoioGrupoPesquisa = apoioGrupoPesquisa;
	}

	/** Retorna se o projeto trata-se um projeto de Apoio a Novos Pesquisadores ou não */
	public boolean isApoioNovosPesquisadores() {
	    return apoioNovosPesquisadores;
	}

	/** Seta um projeto do tipo Apoio a Novos Pesquisadores */
	public void setApoioNovosPesquisadores(boolean apoioNovosPesquisadores) {
	    this.apoioNovosPesquisadores = apoioNovosPesquisadores;
	}

	/** Retorna se o projeto trata-se um projeto de Iniciação Científica ou não */
	public boolean isIniciacaoCientifica() {
	    return iniciacaoCientifica;
	}

	/** Seta um projeto do Tipo Iniciação Científica */
	public void setIniciacaoCientifica(boolean iniciacaoCientifica) {
	    this.iniciacaoCientifica = iniciacaoCientifica;
	}

	/** Retorna se o projeto trata-se um projeto de Financiamento Interno ou não */
	public boolean isFinanciamentoInterno() {
	    return financiamentoInterno;
	}

	/** Seta um projeto do Tipo Financiamento Interno */
	public void setFinanciamentoInterno(boolean financiamentoInterno) {
	    this.financiamentoInterno = financiamentoInterno;
	}

	/** Retorna se o projeto trata-se um projeto de Financiamento Externo ou não */
	public boolean isFinanciamentoExterno() {
	    return financiamentoExterno;
	}

	/** Seta um projeto do Tipo Financiamento Externo */
	public void setFinanciamentoExterno(boolean financiamentoExterno) {
	    this.financiamentoExterno = financiamentoExterno;
	}

	/** Retorna se o projeto trata-se um projeto de Auto Financiamento ou não */
	public boolean isAutoFinanciado() {
	    return autoFinanciado;
	}

	/** Seta um projeto do Tipo Auto Financiamento */
	public void setAutoFinanciado(boolean autoFinanciado) {
	    this.autoFinanciado = autoFinanciado;
	}

	/** Retorna se o projeto trata-se um projeto sem Financiamento ou não */
	public boolean isSemFinanciamento() {
	    return semFinanciamento;
	}

	/** Seta um projeto sem Financiamento */
	public void setSemFinanciamento(boolean semFinanciamento) {
	    this.semFinanciamento = semFinanciamento;
	}

	/** Retorna a Classificação da Financiadora */
	public ClassificacaoFinanciadora getClassificacaoFinanciadora() {
	    return classificacaoFinanciadora;
	}

	/** Seta uma classificação de uma Financiadora */
	public void setClassificacaoFinanciadora(
		ClassificacaoFinanciadora classificacaoFinanciadora) {
	    this.classificacaoFinanciadora = classificacaoFinanciadora;
	}

	/** Retorna uma Descrição da Financiadora Externa */
	public String getDescricaoFinanciadorExterno() {
	    return descricaoFinanciadorExterno;
	}

	/** Seta um descrição para uma Financiadora Externa */
	public void setDescricaoFinanciadorExterno(String descricaoFinanciadorExterno) {
	    this.descricaoFinanciadorExterno = descricaoFinanciadorExterno;
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
		}

		if (this.isFinanciamentoExterno()) {
			if (result.length() > 0) {
			    result.append(", ");
			}
			result.append("FINANCIAMENTO EXTERNO");
			if (classificacaoFinanciadora != null) {
			    result.append(" (" + classificacaoFinanciadora.getDescricao() + " - " + descricaoFinanciadorExterno + ")");
			}
		}

		if (this.isAutoFinanciado()) {
			if (result.length() > 0) {
				result.append(", ");
			}
			result.append("AÇÃO AUTO-FINANCIADA ");
		}

		if ((!this.isAutoFinanciado()) && (!this.isFinanciamentoExterno()) && (!this.isFinanciamentoInterno())) {
			result.append("SEM FINANCIAMENTO");
		}

		return result.toString();

	}
	
	/**
	 * Adiciona um MembroProjeto a equipe do projeto
	 * 
	 * @param membro
	 * @return
	 */
	public boolean addMembroEquipe(MembroProjeto membro) {
	    membro.setProjeto(this);
	    if (membro.isCoordenador()) {
	    	this.setCoordenador(membro);
	    }	    
	    return this.getEquipe().add(membro);
	}
	
	/**
	 * Retorna a quantidade de semanas do projeto.
	 * com base no período de execução.
	 * 
	 * @return
	 */
	public int getTotalSemanasProjeto() {
	    Date hoje = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
	    
	    if ((this.getDataFim() == null) || (hoje.before(this.getDataFim()))) {
	    	return CalendarUtils.calculaQuantidadeSemanasEntreDatasIntervaloFechado(this.getDataInicio(), new Date());
	    }
	    return CalendarUtils.calculaQuantidadeSemanasEntreDatasIntervaloFechado(this.getDataInicio(), this.getDataFim());
	}
	
	/** Retorna se o projeto trata-se um projeto de Programa Extensão Submetido ou não */
	public boolean isProgramaExtensaoSubmetido() {
	    return programaExtensaoSubmetido;
	}

	/** Seta um programa Extensão Submetido */
	public void setProgramaExtensaoSubmetido(boolean programaExtensaoSubmetido) {
	    this.programaExtensaoSubmetido = programaExtensaoSubmetido;
	}

	/** Retorna se o projeto trata-se um projeto de Extensão Submetido ou não */
	public boolean isProjetoExtensaoSubmetido() {
	    return projetoExtensaoSubmetido;
	}

	/** Seta um projeto de Extensão Submetido */
	public void setProjetoExtensaoSubmetido(boolean projetoExtensaoSubmetido) {
	    this.projetoExtensaoSubmetido = projetoExtensaoSubmetido;
	}

	/** Retorna se o projeto trata-se um projeto de Curso Submetido ou não */
	public boolean isCursoExtensaoSubmetido() {
	    return cursoExtensaoSubmetido;
	}

	/** Seta um projeto de Curso Submetido */
	public void setCursoExtensaoSubmetido(boolean cursoExtensaoSubmetido) {
	    this.cursoExtensaoSubmetido = cursoExtensaoSubmetido;
	}

	/** Retorna se o projeto trata-se um projeto de Evento Extensão Submetido ou não */
	public boolean isEventoExtensaoSubmetido() {
	    return eventoExtensaoSubmetido;
	}

	/** Seta um projeto de Evento Extensão Submetido */
	public void setEventoExtensaoSubmetido(boolean eventoExtensaoSubmetido) {
	    this.eventoExtensaoSubmetido = eventoExtensaoSubmetido;
	}

	/** Retorna se o projeto trata-se um projeto de Programa Monitoria Submetido ou não */
	public boolean isProgramaMonitoriaSubmetido() {
	    return programaMonitoriaSubmetido;
	}

	/** Seta um programa de Monitoria Submetido */
	public void setProgramaMonitoriaSubmetido(boolean programaMonitoriaSubmetido) {
	    this.programaMonitoriaSubmetido = programaMonitoriaSubmetido;
	}

	/** Retorna se o projeto trata-se um projeto de melhoria da Qualidade do Ensino Submetido ou não */
	public boolean isMelhoriaQualidadeEnsinoSubmetido() {
	    return melhoriaQualidadeEnsinoSubmetido;
	}

	/** Seta um programa de Melhoria da qualidade de ensino Submetido */
	public void setMelhoriaQualidadeEnsinoSubmetido(
		boolean melhoriaQualidadeEnsinoSubmetido) {
	    this.melhoriaQualidadeEnsinoSubmetido = melhoriaQualidadeEnsinoSubmetido;
	}
	
	/** Retorna se o projeto trata-se um projeto de Apoio a Grupo de Pesquisa Submetido ou não */
	public boolean isApoioGrupoPesquisaSubmetido() {
	    return apoioGrupoPesquisaSubmetido;
	}

	/** Seta um projeto de Apoio a Grupo de Pesquisa Submetido */
	public void setApoioGrupoPesquisaSubmetido(boolean apoioGrupoPesquisaSubmetido) {
	    this.apoioGrupoPesquisaSubmetido = apoioGrupoPesquisaSubmetido;
	}

	/** Retorna se o projeto trata-se um projeto de Apoio a Novos Pesquisadores Submetido ou não */
	public boolean isApoioNovosPesquisadoresSubmetido() {
	    return apoioNovosPesquisadoresSubmetido;
	}

	/** Seta um Apoio a Pesquisadores Submetidos ou não */
	public void setApoioNovosPesquisadoresSubmetido(
		boolean apoioNovosPesquisadoresSubmetido) {
	    this.apoioNovosPesquisadoresSubmetido = apoioNovosPesquisadoresSubmetido;
	}

	/** Retorna se o projeto trata-se um projeto de Iniciação Científica ou não */
	public boolean isIniciacaoCientificaSubmetido() {
	    return iniciacaoCientificaSubmetido;
	}

	/** Seta um projeto de Iniciação Científica */
	public void setIniciacaoCientificaSubmetido(boolean iniciacaoCientificaSubmetido) {
	    this.iniciacaoCientificaSubmetido = iniciacaoCientificaSubmetido;
	}
	
	/** Retorna o coordenador do Projeto */
	public MembroProjeto getCoordenador() {
	    return coordenador;
	}

	/** Seta o coordenador do projeto */
	public void setCoordenador(MembroProjeto coordenador) {
	    this.coordenador = coordenador;
	}

	/** Retorna se o projeto está em sendo cadastrado ou não */
	public boolean isCadastroEmAndamento(){
		return situacaoProjeto != null ? 
			(situacaoProjeto.getId() == TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO || // projeto base 
				situacaoProjeto.getId() == TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO || //extensão
				situacaoProjeto.getId() == TipoSituacaoProjeto.CADASTRADO) //pesquisa 
				: false;
	}
	
	/**
	 * Informa se o projeto foi aprovado com ou sem recursos financeiros.
	 * Utilizado para permitir a execução de projetos pelo coordenador.
	 * 
	 * @return
	 */
	public boolean isAprovadoComSemRecurso(){
		return situacaoProjeto.getId() == TipoSituacaoProjeto.PROJETO_BASE_APROVADO_COM_RECURSOS || situacaoProjeto.getId() == TipoSituacaoProjeto.PROJETO_BASE_APROVADO_SEM_RECURSOS;
	}

	/**
	 * Informa se o projeto está na situação de concluído.
	 * Utilizado na emissão de certificados.
	 * 
	 * @return
	 */
	public boolean isConcluido(){
		Integer situacaoAtual = getSituacaoProjeto().getId();
		return	Arrays.asList(TipoSituacaoProjeto.PROJETOS_GRUPO_CONCLUIDO).contains(situacaoAtual);
	}

	/**
	 * Informa se o projeto está em uma situação inválida para remoção.
	 * Ex. Reprovado, Cancelado, etc.
	 * Permite a remoção do projeto pela coordenação do mesmo.
	 * 
	 * @return
	 */
	public boolean isPassivelRemocao(){
		Integer situacaoAtual = getSituacaoProjeto().getId();
		return	Arrays.asList(TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO)
					.contains(situacaoAtual) || isCadastroEmAndamento();
	}
	
	/** Retorna o Edital */
	public Edital getEdital() {
	    return edital;
	}
	
	/** Seta o Edital do projeto */
	public void setEdital(Edital edital) {
		this.edital = edital;
	}

	/** Retorna se o projeto e de renovação ou não */
	public Boolean getRenovacao() {
	    return renovacao;
	}

	/** Seta um projeto de Renovação */
	public void setRenovacao(Boolean renovacao) {
	    this.renovacao = renovacao;
	}
	
	/**
	 * Método utilizado para verificar se o Objeto passado por parâmetro é igual ao objeto da classe atual.
	 */
	@Override
	public boolean equals(Object obj) {
	    return EqualsUtil.testEquals(this, obj, "id");
	}

	/**
	 * Método utilizado para informar o hashCode do Objeto atual da classe.
	 */
	@Override
	public int hashCode() {
	    return HashCodeUtil.hashAll(getId());
	}

	public String getCodigoProjeto() {
		return codigoProjeto;
	}

	public void setCodigoProjeto(String codigoProjeto) {
		this.codigoProjeto = codigoProjeto;
	}

	public Collection<SolicitacaoReconsideracao> getSolicitacoesReconsideracao() {
	    return solicitacoesReconsideracao;
	}

	public void setSolicitacoesReconsideracao(
		Collection<SolicitacaoReconsideracao> solicitacoesReconsideracao) {
	    this.solicitacoesReconsideracao = solicitacoesReconsideracao;
	}
	
	/**
	 * Retorna true se o coordenador da ação puder enviar reconsideração.
	 */
	public boolean isPermitidoSolicitarReconsideracao() {
		// Já solicitou reconsideração
		if (((this.getSolicitacoesReconsideracao() != null) && (!this.getSolicitacoesReconsideracao().isEmpty()))
				|| (this.getEdital() != null && this.getEdital().getDataFimReconsideracao() == null)) {
			return false;
			
		// Deve estar no grupo de situações permitidas e, se estiver vinculado a um edital, deve estar dentro do prazo
		}else if ( (ArrayUtils.idContains(this.getSituacaoProjeto().getId(), TipoSituacaoProjeto.PROJETO_BASE_GRUPO_SOLICITAR_RECONSIDERACAO))
				&& (this.getEdital() == null || !CalendarUtils.descartarHoras(new Date()).after(this.getEdital().getDataFimReconsideracao()) )) {
			return true;
			
		}else {
			return false;
		}
	}

	
	/**
	 * Retorna um texto com a dimensão acadêmica desta ação.
	 *  
	 * @return
	 */
	public String getDimensaoAcademica() {
	   StringBuilder result = new StringBuilder();
	   
	   if (this.ensino) {
	       result.append(" ENSINO, ");
	   }

	   if (this.pesquisa) {
	       result.append(" PESQUISA, ");
	   }
	   
	   if (this.extensao) {
	       result.append(" EXTENSÃO, ");
	   }
	   
	   if (!this.ensino && !this.pesquisa && !this.extensao) {
	       result.append("ISOLADO");
	   }else {
		   result.deleteCharAt(result.lastIndexOf(","));
	   }
	   
	   return result.toString();
	   
	}
	
	/**
	 * Indica o número de dimensões acadêmicas que o projeto abrange.
	 * 
	 * Ex.: Ensino = 1, Ensino + Pesquisa = 2, Ensino + Pesquisa + Extensão = 3.
	 * @return
	 */
	public int getDimensaoProjeto() {
	    int dimensao = 0;
	    if (ensino) {
		dimensao++;
	    }
	    if (pesquisa) {
		dimensao++;
	    }
	    if (extensao) {
		dimensao++;
	    }
	    return dimensao;	    
	}
	
	/**
	 * Informa se este projeto faz parte de uma 
	 * Ação Acadêmica Associada. 
	 * 
	 * @return
	 */
	public boolean isProjetoAssociado() {
	    return getDimensaoProjeto() > 1;
	}
	
	/**
	 * Informa se é um projeto Isolado.
	 * @return
	 */
	public boolean isProjetoIsolado() {
	    return !isProjetoAssociado();
	}
	

	public Collection<OrcamentoConsolidado> getOrcamentoConsolidado() {
	    return orcamentoConsolidado;
	}

	public void setOrcamentoConsolidado(
		Collection<OrcamentoConsolidado> orcamentoConsolidado) {
	    this.orcamentoConsolidado = orcamentoConsolidado;
	}

	/**
	 * Verifica se este servidor é coordenador deste projeto
	 * 
	 * @param servidor 
	 * @return
	 */
	public boolean coordenaEsteProjeto(Servidor servidor) {
	    return getCoordenador() != null && getCoordenador().getServidor().equals(servidor);
	}
	
	/**
	 * Informa se o projeto está finalizado com base na data.
	 * 
	 * @return
	 */
	public boolean isFinalizado() {
	    return isAtivo() && isValido() && getDataFim().before(CalendarUtils.descartarHoras(new Date()));
	}
	
	/**
	 * Indica, com base na data início e data fim, se o projeto iniciou.
	 * @return
	 */
	public boolean isValido() {
	    return (getDataInicio() != null) && (getDataFim() != null);
	}

	/**
	 * Verifica, com base na data início e data fim, se a o projeto está atuante.
	 * @return
	 */
	public boolean isVigente() {
	    return isAtivo() && isValido() && CalendarUtils.isDentroPeriodo(getDataInicio(), getDataFim());
	}

	/**
	 * Retorna o status do membro do projeto com base nas datas de início e fim.
	 * 
	 * @return
	 */
	public String getStatus() {
	    return isFinalizado() ? "Finalizado" : isVigente() ? "Ativo" : "Inativo" ;
	}

	public Unidade getUnidadeOrcamentaria() {
	    return unidadeOrcamentaria;
	}

	public void setUnidadeOrcamentaria(Unidade unidadeOrcamentaria) {
	    this.unidadeOrcamentaria = unidadeOrcamentaria;
	}

	public Collection<AutorizacaoDepartamento> getAutorizacoesDepartamentos() {
	    return autorizacoesDepartamentos;
	}

	public void setAutorizacoesDepartamentos(
		Collection<AutorizacaoDepartamento> autorizacoesDepartamentos) {
	    this.autorizacoesDepartamentos = autorizacoesDepartamentos;
	}

	/** Informa se é um projeto externo.*/
	public boolean isExterno() {
	    return !interno;
	}

	public void setExterno(boolean externo) {
	    interno = !externo;
	}

	public boolean isInterno() {
	    return interno;
	}

	public void setInterno(boolean interno) {
	    this.interno = interno;
	}

	public Integer getBolsasConcedidas() {
	    return bolsasConcedidas;
	}

	public void setBolsasConcedidas(Integer bolsasConcedidas) {
	    this.bolsasConcedidas = bolsasConcedidas;
	}

	public Integer getBolsasSolicitadas() {
	    return bolsasSolicitadas;
	}

	public void setBolsasSolicitadas(Integer bolsasSolicitadas) {
	    this.bolsasSolicitadas = bolsasSolicitadas;
	}

	public Integer getTotalDiscentesEnvovidos() {
	    return totalDiscentesEnvovidos;
	}

	public void setTotalDiscentesEnvovidos(Integer totalDiscentesEnvovidos) {
	    this.totalDiscentesEnvovidos = totalDiscentesEnvovidos;
	}

	/**
	 * Retorna o valor total do orçamento solicitado. 
	 */
	public double getTotalOrcamentoSolicitado() {
	    double total = 0.0; 
	    for (OrcamentoDetalhado orc : orcamento) {
		total += orc.getValorTotal();
	    }
	    return total;
	}

	public Double getMedia() {
	    return media;
	}

	public void setMedia(Double media) {
	    this.media = media;
	}
	
	/**
	 * Usado na classificação do projeto
	 * 
	 * @param other
	 * @return
	 */
	@Override
	public int compareTo(Projeto other) {
	    return other.getMedia().compareTo(this.getMedia());
	}

	/**
	 * Propriedade transiente utilizada para melhorar a interação com 
	 * o usuário na camada de visualização.
	 * 
	 * @return
	 */
	public boolean isSelecionado() {
	    return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
	    this.selecionado = selecionado;
	}

	/**
	 * Retorna somente avaliações ativas do projeto.
	 * 
	 * @return
	 */
	public List<Avaliacao> getAvaliacoesAtivas() {
		List<Avaliacao> result = new ArrayList<Avaliacao>();
		for (Avaliacao a : avaliacoes) {
			if (a.isAtivo()) {
				result.add(a);
			}
		} 
		return result;
	}
	
	/**
	 * Retorna somente avaliações ativas e realizadas do projeto.
	 * 
	 * @return
	 */
	public List<Avaliacao> getAvaliacoesAtivasRealizadas() {
		List<Avaliacao> result = new ArrayList<Avaliacao>();
		for (Avaliacao a : avaliacoes) {
			if (a.isAtivo() && a.getDataAvaliacao() != null) {
				result.add(a);
			}
		} 
		return result;
	}

	public void setAvaliacoes(List<Avaliacao> avaliacoes) {
	    this.avaliacoes = avaliacoes;
	}
	
	/**
	 * Retorna todas as avaliações do projeto, inclusive a inativas.
	 * @return
	 */
	public List<Avaliacao> getAvaliacoes() {
	    return avaliacoes;
	}

	/**
	 * Retorna o total de avaliações distribuidas do projeto.
	 * 
	 * @return
	 */
	public int getTotalAvaliacoesAtivas() {
		return getAvaliacoesAtivas().size();
	}

	/**
	 * Retorna o total de avaliações realizada do projeto.
	 * 
	 * @return
	 */
	public int getTotalAvaliacoesAtivasRealizadas() {
		return getAvaliacoesAtivasRealizadas().size();
	}

	
	/**
	 * Retorna a avaliação do projeto com maior nota;
	 * 
	 * @return
	 */
	public Avaliacao getMaxAvaliacao() {
		if (!ValidatorUtil.isEmpty(getAvaliacoesAtivasRealizadas())) {
			Collections.sort(avaliacoes, new Comparator<Avaliacao>() {
	
				@Override
				public int compare(Avaliacao o1, Avaliacao o2) {
					if (o1.getNota() > o2.getNota())
						return 1;
					if (o1.getNota() < o2.getNota())
						return -1;
					
					return 0;
				}
				
			});	
			return getAvaliacoesAtivasRealizadas().get(getAvaliacoesAtivasRealizadas().size() - 1);
		}
		return null;
	}

	/**
	 * Retorna a avaliação do projeto com menor nota;
	 * @return
	 */
	public Avaliacao getMinAvaliacao() {
		if (!ValidatorUtil.isEmpty(getAvaliacoesAtivasRealizadas())) {
			Collections.sort(avaliacoes, new Comparator<Avaliacao>() {
	
				@Override
				public int compare(Avaliacao o1, Avaliacao o2) {
					if (o1.getNota() > o2.getNota())
						return 1;
					if (o1.getNota() < o2.getNota())
						return -1;
					
					return 0;
				}
				
			});
			return getAvaliacoesAtivasRealizadas().get(0);
		}
		return null;
	}

	/**
	 * Retorna a última avaliação realizada pelo comitê interno.
	 * O comitê interno realiza somente uma avaliação do projeto
	 * Esta avaliação é definitiva e determina a nota final do projeto.
	 * 
	 * @return
	 */
	public Avaliacao getAvaliacaoComiteInterno() {
		Avaliacao result = null;
		for (Avaliacao av : avaliacoes) {
			if (av.isAtivo() 
					&& av.getDataAvaliacao() != null 
					&& av.isAvaliacaoComiteInterno() 
					&& av.isAvaliacaoProjeto()
					&& (result == null || av.getDataAvaliacao().after(result.getDataAvaliacao()))
				) {
				result = av;
			}			
		}
		return result;
	}
	
	/**
	 * Retorna a maior discrepância entre as avaliações
	 * realizadas para o projeto.
	 * 
	 * @return
	 */
	public Double getDiscrepanciaAvaliacao() {
		if (getMaxAvaliacao() == null || getMinAvaliacao() == null)
			return null;
		return Math.abs(getMaxAvaliacao().getNota() - getMinAvaliacao().getNota());
	}
	
	
	/**
	 * Verifica se o projeto pode ser distribuído para avaliação.
	 * Caso um membro do comitê interno já tenha avaliado o projeto
	 * o mesmo não pode ser avaliado novamente.
	 * 
	 * @return
	 */
	public boolean isPermitidoAvaliar() {
	    return  (getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO 
	    			|| getSituacaoProjeto().getId() == TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO) &&
	    			 (this.getAvaliacaoComiteInterno() == null || isSolicitouReconsideracao()); 
	}
	
	/** 
	 * Indica se o projeto solicitou reconsideração.
	 * Utilizado na liberação de novas avaliações.
	 *  
	 */
	public boolean isSolicitouReconsideracao() {
		return ValidatorUtil.isNotEmpty(this.getSolicitacoesReconsideracao());
	}


	/**
	 * Verifica se o projeto pode receber recursos.
	 * Somente projetos que foram avaliados por algum membro do comitê interno
	 * pode receber recursos.
	 * 
	 * @return
	 */
	public boolean isPermitidoConcederRecursos() {
	    return ValidatorUtil.isNotEmpty(this.getAvaliacoesAtivas()); 
	}

	
	/**
	 * Informa a classificação do projeto após as avaliações.
	 * 
	 * @return
	 */
	public Integer getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(Integer classificacao) {
		this.classificacao = classificacao;
	}

	public Collection<RelatorioAcaoAssociada> getRelatorios() {
		return relatorios;
	}

	public void setRelatorios(Collection<RelatorioAcaoAssociada> relatorios) {
		this.relatorios = relatorios;
	}
	
	public Collection<DiscenteProjeto> getDiscentes(){
		return discentesProjeto;
	}

	/** Lista de discentes (bolsistas e voluntários) Ativos que participam do projeto.*/
	public Collection<DiscenteProjeto> getDiscentesProjeto() {
		// Retiradas dos discentes excluídos da lista
		if( (discentesProjeto != null) && (!discentesProjeto.isEmpty()) ){
			for( Iterator<DiscenteProjeto> it = discentesProjeto.iterator(); it.hasNext(); ){
				DiscenteProjeto dp = it.next();
				if(!dp.isAtivo()){
					it.remove();
				}
			}
		}
		return discentesProjeto;
	}

	public void setDiscentesProjeto(Collection<DiscenteProjeto> discentesProjeto) {
		this.discentesProjeto = discentesProjeto;
	}

	/** Representa uma inscrição de um discente para a seleção de uma ação associada. */
	public Collection<InscricaoSelecaoProjeto> getInscricoesSelecao() {
		Collections.sort((List<InscricaoSelecaoProjeto>) inscricoesSelecao);
		return inscricoesSelecao;
	}

	public void setInscricoesSelecao(
			Collection<InscricaoSelecaoProjeto> inscricoesSelecao) {
		this.inscricoesSelecao = inscricoesSelecao;
	}

	public boolean isConvenio() {
		return convenio;
	}

	public void setConvenio(boolean convenio) {
		this.convenio = convenio;
	}

    /** Verifica se o relatório final desta ação já foi enviado. */
    public boolean isEnviouRelatorioFinal() {
    	for(RelatorioAcaoAssociada rel : relatorios) {
    		if(rel.isRelatorioFinal() && rel.getDataEnvio() != null) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     *  Verifica se é possível visualizar as avaliações realizadas.
     *  Atualmente as avaliações podem ser visualizadas a partir do dia
     *  de início das reconsiderações.
     */
    public boolean isPermitidoVisualizarAvaliacao() {
    	if (edital == null) {
    		return true;
    	}else {
   			return edital.getDataInicioReconsideracao() != null &&
    			new Date().after(edital.getDataInicioReconsideracao()) ;
    	}
    }

    /**
     * Verifica se a pessoa faz parte da equipe do projeto.
     * 
     * @param idPessoa
     * @return
     */
    public boolean isPertenceEquipe(int idPessoa) {
    	if (getEquipe() != null) {
    		for(MembroProjeto mp: getEquipe()){
    			if(mp.getPessoa().getId() == idPessoa){
    				return true;
    			}
    		}
    	}
    	return false;
    }

	public TipoProjeto getTipoProjeto() {
		return tipoProjeto;
	}

	public void setTipoProjeto(TipoProjeto tipoProjeto) {
		this.tipoProjeto = tipoProjeto;
	}

	public boolean isRecebeuFinanciamentoInterno() {
		return recebeuFinanciamentoInterno;
	}

	public void setRecebeuFinanciamentoInterno(boolean recebeuFinanciamentoInterno) {
		this.recebeuFinanciamentoInterno = recebeuFinanciamentoInterno;
	}

	public boolean isAutorizarCertificadoGestor() {
		return autorizarCertificadoGestor;
	}

	public void setAutorizarCertificadoGestor(boolean autorizarCertificadoGestor) {
		this.autorizarCertificadoGestor = autorizarCertificadoGestor;
	}

	public String getObjetivosEspecificos() {
		return objetivosEspecificos;
	}

	public void setObjetivosEspecificos(String objetivosEspecificos) {
		this.objetivosEspecificos = objetivosEspecificos;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public String getTipoAcaoExtensao() {
		return tipoAcaoExtensao;
	}

	public void setTipoAcaoExtensao(String tipoAcaoExtensao) {
		this.tipoAcaoExtensao = tipoAcaoExtensao;
	}
	
}