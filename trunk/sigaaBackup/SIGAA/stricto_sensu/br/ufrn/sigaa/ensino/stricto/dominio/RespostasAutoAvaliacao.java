/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 08/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;

/**
 * Respostas dadas na Auto Avalia��o por um docente de um programa.
 * @author �dipo Elder F. de Melo
 *
 */
@Entity
@Table(schema="stricto_sensu", name="respostas_auto_avaliacao")
public class RespostasAutoAvaliacao implements Validatable {

	/** Chave primaria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_respostas_auto_avaliacao", nullable = false)
	private int id;
	
	/** Programa avaliado. */ 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade",nullable=true)
	private Unidade unidade;
	
	/** Curso avaliado. */ 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso",nullable=true)
	private Curso curso;
	
	/** Respostas dadas ao question�rio da Auto Avalia��o. */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinTable(name="questionario_respostas_auto_avaliacao", schema="stricto_sensu",
			joinColumns=@JoinColumn(name="id_respostas_auto_avaliacao"),  
			inverseJoinColumns=@JoinColumn(name="id_questionario_respostas"))
	private QuestionarioRespostas respostas;
	
	/** Cole��o de {@link MetaAcaoFormacaoRH metas e a��es de forma��o de Recursos Humanos} para o programa.*/
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_respostas_auto_avaliacao")
	private Collection<MetaAcaoFormacaoRH> metasAcoesFormacaoRH;
	
	/** Cole��o de {@link MetaAcaoFormacaoProducaoAcademica metas de forma��o acad�mica e de produ��o cient�fica} para o programa.*/
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_respostas_auto_avaliacao")
	private Collection<MetaAcaoFormacaoProducaoAcademica> metasAcoesFormacaoAcademica;
	
	/** {@link SugestaoMelhoriaPrograma}: sugest�es que o coordenador indicada para a melhoria da qualidade do programa.*/
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_sugestao_melhoria_programa")
	private SugestaoMelhoriaPrograma sugestoesMelhoriaPrograma;
	
	/** Data de cadastro. */
	@Column(name="cadastrado_em")
	private Date cadastradoEm;
	
	/** Data da �ltima atualiza��o. */
	@Column(name="atualizado_em")
	private Date atualizadoEm;
	
	/** Registro de entrada do usu�rio que fez a auto avalia��o. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada",nullable=false)
	@CriadoPor
	private RegistroEntrada preenchidoPor;
	
	/** Calend�rio de auto avalia��o a qual estas respsotas foram dadas. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_calendario_aplicacao_auto_avaliacao",nullable=false)
	private CalendarioAplicacaoAutoAvaliacao calendario;
	
	/** Situa��o atual da Auto Avalia��o */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="situacao", nullable=false)
	private SituacaoRespostasAutoAvaliacao situacao;
	
	/** Cole��o de {@link ParecerAutoAvaliacao parecer} dado pela Comiss�o sobre as Auto Avalia��o.*/
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_respostas_auto_avaliacao")
	@OrderBy("cadastradoEm")
	private List<ParecerAutoAvaliacao> pareceresAutoAvaliacao;
	
	/** Construtor padr�o. */
	public RespostasAutoAvaliacao() {
		unidade = new Unidade();
		respostas = new QuestionarioRespostas();
		metasAcoesFormacaoRH = new LinkedList<MetaAcaoFormacaoRH>();
		metasAcoesFormacaoAcademica = new LinkedList<MetaAcaoFormacaoProducaoAcademica>();
		sugestoesMelhoriaPrograma = new SugestaoMelhoriaPrograma();
		pareceresAutoAvaliacao = new LinkedList<ParecerAutoAvaliacao>();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Collection<MetaAcaoFormacaoRH> getMetasAcoesFormacaoRH() {
		return metasAcoesFormacaoRH;
	}

	public void setMetasAcoesFormacaoRH(
			Collection<MetaAcaoFormacaoRH> metasAcoesFormacaoRH) {
		this.metasAcoesFormacaoRH = metasAcoesFormacaoRH;
	}

	public Collection<MetaAcaoFormacaoProducaoAcademica> getMetasAcoesFormacaoAcademica() {
		return metasAcoesFormacaoAcademica;
	}

	public void setMetasAcoesFormacaoAcademica(
			Collection<MetaAcaoFormacaoProducaoAcademica> metasAcoesFormacaoAcademica) {
		this.metasAcoesFormacaoAcademica = metasAcoesFormacaoAcademica;
	}

	public SugestaoMelhoriaPrograma getSugestoesMelhoriaPrograma() {
		return sugestoesMelhoriaPrograma;
	}

	public void setSugestoesMelhoriaPrograma(SugestaoMelhoriaPrograma sugestoesMelhoriaPrograma) {
		this.sugestoesMelhoriaPrograma = sugestoesMelhoriaPrograma;
	}

	public Date getCadastradoEm() {
		return cadastradoEm;
	}

	public void setCadastradoEm(Date cadastradoEm) {
		this.cadastradoEm = cadastradoEm;
	}

	public Date getAtualizadoEm() {
		return atualizadoEm;
	}

	public void setAtualizadoEm(Date atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

	public RegistroEntrada getPreenchidoPor() {
		return preenchidoPor;
	}

	public void setPreenchidoPor(RegistroEntrada preenchidoPor) {
		this.preenchidoPor = preenchidoPor;
	}

	public QuestionarioRespostas getRespostas() {
		return respostas;
	}

	public void setRespostas(QuestionarioRespostas respostas) {
		this.respostas = respostas;
	}

	public CalendarioAplicacaoAutoAvaliacao getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAplicacaoAutoAvaliacao calendario) {
		this.calendario = calendario;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
	
	/** Valida os dados da resposta � auto avalia��o
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(unidade, "Programa de P�s Gradua��o", lista);
		validateRequired(calendario, "Calend�rio de Auto Avalia��o", lista);
		validateRequired(situacao, "Situa��o", lista);
		return lista;
	}
	
	/**
	 * Retorna uma representa��o textal do objeto, informando o nome
	 * do programa que fez a auto avali��o.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (!isEmpty(unidade))
			return unidade.getNome() + " " + situacao;
		else
			return curso.getNome() + " " + situacao;
	}

	/** Adiciona uma {@link MetaAcaoFormacaoRH} � esta Auto Avalia��o
	 * @param metaAcaoFormacaoRH
	 */
	public void addMetaAcaoFormacaoRH(MetaAcaoFormacaoRH metaAcaoFormacaoRH) {
		if (metasAcoesFormacaoRH == null) metasAcoesFormacaoRH = new LinkedList<MetaAcaoFormacaoRH>();
		metaAcaoFormacaoRH.setRespostasAutoAvaliacao(this);
		metasAcoesFormacaoRH.add(metaAcaoFormacaoRH);
	}
	
	/** Adiciona uma {@link MetaAcaoFormacaoProducaoAcademica} � esta Auto Avalia��o
	 * @param metaAcaoFormacaoProducaoAcademica
	 */
	public void addMetaAcaoFormacaoProducaoAcademica(MetaAcaoFormacaoProducaoAcademica metaAcaoFormacaoProducaoAcademica) {
		if (metasAcoesFormacaoAcademica == null) metasAcoesFormacaoAcademica = new LinkedList<MetaAcaoFormacaoProducaoAcademica>();
		metaAcaoFormacaoProducaoAcademica.setRespostasAutoAvaliacao(this);
		metasAcoesFormacaoAcademica.add(metaAcaoFormacaoProducaoAcademica);
	}

	public SituacaoRespostasAutoAvaliacao getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoRespostasAutoAvaliacao situacao) {
		this.situacao = situacao;
	}

	public List<ParecerAutoAvaliacao> getPareceresAutoAvaliacao() {
		return pareceresAutoAvaliacao;
	}

	public void setPareceresAutoAvaliacao(List<ParecerAutoAvaliacao> pareceresAutoAvaliacao) {
		this.pareceresAutoAvaliacao = pareceresAutoAvaliacao;
	}
	
	/** Retorna o �ltimo parecer dado pela Comiss�o
	 * @return
	 */
	public ParecerAutoAvaliacao getUltimoParecer() {
		if (pareceresAutoAvaliacao != null && pareceresAutoAvaliacao.size() > 0)
			return pareceresAutoAvaliacao.get(pareceresAutoAvaliacao.size() - 1);
		else 
			return null;
	}
	
	/** Indica que as respostas foram submetidas para a aprecia��o da Comiss�o.
	 * @return
	 */
	public boolean isSubmetido() {
		return situacao == SituacaoRespostasAutoAvaliacao.SUBMETIDO;
	}
	
	/** Indica que as respostas foram salvas para preenchimento posterior.
	 * @return
	 */
	public boolean isSalvo() {
		return situacao == SituacaoRespostasAutoAvaliacao.SALVO;
	}
	
	/** Indica que as respostas foram retornadas da aprecia��o da Comiss�o.
	 * @return
	 */
	public boolean isRetornado() {
		return situacao == SituacaoRespostasAutoAvaliacao.RETORNADO;
	}

	/**
	 * Adiciona um parecer � an�lise da auto avalia��o.
	 * @param parecerAutoAvaliacao
	 */
	public void addParecerAutoAvaliacao(ParecerAutoAvaliacao parecerAutoAvaliacao) {
		if (pareceresAutoAvaliacao == null) pareceresAutoAvaliacao = new LinkedList<ParecerAutoAvaliacao>();
		pareceresAutoAvaliacao.add(parecerAutoAvaliacao);
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}
}
