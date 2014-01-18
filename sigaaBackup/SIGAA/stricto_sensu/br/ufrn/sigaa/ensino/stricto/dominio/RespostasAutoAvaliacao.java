/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Respostas dadas na Auto Avaliação por um docente de um programa.
 * @author Édipo Elder F. de Melo
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
	
	/** Respostas dadas ao questionário da Auto Avaliação. */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinTable(name="questionario_respostas_auto_avaliacao", schema="stricto_sensu",
			joinColumns=@JoinColumn(name="id_respostas_auto_avaliacao"),  
			inverseJoinColumns=@JoinColumn(name="id_questionario_respostas"))
	private QuestionarioRespostas respostas;
	
	/** Coleção de {@link MetaAcaoFormacaoRH metas e ações de formação de Recursos Humanos} para o programa.*/
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_respostas_auto_avaliacao")
	private Collection<MetaAcaoFormacaoRH> metasAcoesFormacaoRH;
	
	/** Coleção de {@link MetaAcaoFormacaoProducaoAcademica metas de formação acadêmica e de produção científica} para o programa.*/
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_respostas_auto_avaliacao")
	private Collection<MetaAcaoFormacaoProducaoAcademica> metasAcoesFormacaoAcademica;
	
	/** {@link SugestaoMelhoriaPrograma}: sugestões que o coordenador indicada para a melhoria da qualidade do programa.*/
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_sugestao_melhoria_programa")
	private SugestaoMelhoriaPrograma sugestoesMelhoriaPrograma;
	
	/** Data de cadastro. */
	@Column(name="cadastrado_em")
	private Date cadastradoEm;
	
	/** Data da última atualização. */
	@Column(name="atualizado_em")
	private Date atualizadoEm;
	
	/** Registro de entrada do usuário que fez a auto avaliação. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada",nullable=false)
	@CriadoPor
	private RegistroEntrada preenchidoPor;
	
	/** Calendário de auto avaliação a qual estas respsotas foram dadas. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_calendario_aplicacao_auto_avaliacao",nullable=false)
	private CalendarioAplicacaoAutoAvaliacao calendario;
	
	/** Situação atual da Auto Avaliação */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="situacao", nullable=false)
	private SituacaoRespostasAutoAvaliacao situacao;
	
	/** Coleção de {@link ParecerAutoAvaliacao parecer} dado pela Comissão sobre as Auto Avaliação.*/
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_respostas_auto_avaliacao")
	@OrderBy("cadastradoEm")
	private List<ParecerAutoAvaliacao> pareceresAutoAvaliacao;
	
	/** Construtor padrão. */
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
	
	/** Valida os dados da resposta à auto avaliação
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(unidade, "Programa de Pós Graduação", lista);
		validateRequired(calendario, "Calendário de Auto Avaliação", lista);
		validateRequired(situacao, "Situação", lista);
		return lista;
	}
	
	/**
	 * Retorna uma representação textal do objeto, informando o nome
	 * do programa que fez a auto avalição.
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

	/** Adiciona uma {@link MetaAcaoFormacaoRH} à esta Auto Avaliação
	 * @param metaAcaoFormacaoRH
	 */
	public void addMetaAcaoFormacaoRH(MetaAcaoFormacaoRH metaAcaoFormacaoRH) {
		if (metasAcoesFormacaoRH == null) metasAcoesFormacaoRH = new LinkedList<MetaAcaoFormacaoRH>();
		metaAcaoFormacaoRH.setRespostasAutoAvaliacao(this);
		metasAcoesFormacaoRH.add(metaAcaoFormacaoRH);
	}
	
	/** Adiciona uma {@link MetaAcaoFormacaoProducaoAcademica} à esta Auto Avaliação
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
	
	/** Retorna o último parecer dado pela Comissão
	 * @return
	 */
	public ParecerAutoAvaliacao getUltimoParecer() {
		if (pareceresAutoAvaliacao != null && pareceresAutoAvaliacao.size() > 0)
			return pareceresAutoAvaliacao.get(pareceresAutoAvaliacao.size() - 1);
		else 
			return null;
	}
	
	/** Indica que as respostas foram submetidas para a apreciação da Comissão.
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
	
	/** Indica que as respostas foram retornadas da apreciação da Comissão.
	 * @return
	 */
	public boolean isRetornado() {
		return situacao == SituacaoRespostasAutoAvaliacao.RETORNADO;
	}

	/**
	 * Adiciona um parecer à análise da auto avaliação.
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
