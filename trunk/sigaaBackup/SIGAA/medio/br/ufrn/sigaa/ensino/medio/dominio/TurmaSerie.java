/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 24/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.Turno;

/**
 * Classe responsável pela entidade de relação entre Turma e série, no qual o aluno será matriculado.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name = "turma_serie", schema = "medio")
public class TurmaSerie implements Validatable{

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_turma_serie", nullable = false)
	private int id;

	/** Atributo responsável pela informação do ano de vigência da turma em série de ensino médio.*/
	@Column(name = "ano")
	private Integer ano;
	
	/** Atributo responsável pelo nome descritivo da turma. */
	@Column(name = "nome" )
	private String nome;
	
	/** Referência a Série responsável pela turma */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_serie", unique = false)
	private Serie serie;
	
	/** Quantidade máxima de alunos. */
	@Column(name = "capacidade_aluno", unique = false, nullable = true, insertable = true, updatable = true)
	private Integer capacidadeAluno;
	
	/** Indica se a turma de serie está ativa, permitindo sua utilização no SIGAA. */
	@Column(name = "ativo", nullable = false)
	private boolean ativo = true;
	
	/** Indica se a turma é de serie ou de dependencia. */
	@Column(name = "dependencia", nullable = false)
	private boolean dependencia = false;
	

	/** Turno no qual ocorrem as aulas da turma. */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_turno", unique = false, nullable = true, insertable = true, updatable = true)
	private Turno turno = new Turno();
	
	/** Data de início do período letivo da turmaSerie. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataInicio;

	/** Data de fim do período letivo da turmaSerie. */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	private Date dataFim;
	
	/** Quantidade de alunos matriculados com status MATRICULADO. */
	@Transient
	private long qtdMatriculados;
	
	/** Local das aulas. */ 
	@Transient
	private String local;
	
	/** Estrutura Curricular, no qual a turma tenha sido criada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curriculo_medio")
	private CurriculoMedio curriculo;
	
	/** Define os dados  de entrada do usuário no sistema para inserção da turma e série. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;
	
	/** Define a data em que a Turma e série foram cadastradas */
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/**
	 * Atributo responsável pela lista das turmas pertencentes a série no ano.
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "turmaSerie")
	private Collection<TurmaSerieAno> disciplinas = new ArrayList<TurmaSerieAno>();
	
	/**
	 * Atributo responsável pela lista de alunos pertencentes a turma série no ano.
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "turmaSerie")
	private Collection<MatriculaDiscenteSerie> alunos = new ArrayList<MatriculaDiscenteSerie>();

	
	//Constructors
	/** default constructor */
	public TurmaSerie() {
	
	}
	/**
	 * default minimal constructor 
	 * @param id
	 */
	public TurmaSerie(int id) {
		this.id = id;
	}
	/**
	 * minimal constructor
	 * @param id
	 * @param ano
	 * @param nome
	 */
	public TurmaSerie(int id, int ano, String nome) {
		this.id = id;
		this.ano = ano;
		this.nome = nome;
	}

	/**
	 * Construtor
	 * @param id
	 * @param ano
	 * @param nome
	 * @param serie
	 * @param capacidadeAluno
	 * @param ativo
	 * @param dataCadastro
	 */
	public TurmaSerie(int id, Integer ano, String nome, Serie serie,
			Integer capacidadeAluno, boolean ativo, Date dataCadastro) {
		this.id = id;
		this.ano = ano;
		this.nome = nome;
		this.serie = serie;
		this.capacidadeAluno = capacidadeAluno;
		this.ativo = ativo;
		this.dataCadastro = dataCadastro;
	}
	
	/** Retorna a descrição completa da Turma de Série, contemplando a descrição da Série e o nome da turma.*/
	@Transient
	public String getDescricaoCompleta() {
		StringBuffer descricao = new StringBuffer();
		descricao.append(this.getSerie().getDescricaoCompleta() +" - "+ (this.nome.equalsIgnoreCase("dep")?"Dependência":this.nome));
		return descricao.toString();
	}	
	
	
	//Getters and Setters
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Serie getSerie() {
		return serie;
	}
	public void setSerie(Serie serie) {
		this.serie = serie;
	}
	public Integer getCapacidadeAluno() {
		return capacidadeAluno;
	}
	public void setCapacidadeAluno(Integer capacidadeAluno) {
		this.capacidadeAluno = capacidadeAluno;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	public boolean isDependencia() {
		return dependencia;
	}
	public void setDependencia(boolean dependencia) {
		this.dependencia = dependencia;
	}
	public Turno getTurno() {
		return turno;
	}
	public void setTurno(Turno turno) {
		this.turno = turno;
	}
	public long getQtdMatriculados() {
		return qtdMatriculados;
	}
	public void setQtdMatriculados(long qtdMatriculados) {
		this.qtdMatriculados = qtdMatriculados;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public CurriculoMedio getCurriculo() {
		return curriculo;
	}
	public void setCurriculo(CurriculoMedio curriculo) {
		this.curriculo = curriculo;
	}
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	public Collection<TurmaSerieAno> getDisciplinas() {
		return disciplinas;
	}
	public void setDisciplinas(Collection<TurmaSerieAno> disciplinas) {
		this.disciplinas = disciplinas;
	}
	
	public Collection<MatriculaDiscenteSerie> getAlunos() {
		return alunos;
	}
	public void setAlunos(Collection<MatriculaDiscenteSerie> alunos) {
		this.alunos = alunos;
	}
	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(ano, "Ano", lista);
		ValidatorUtil.validateRequired(nome, "Nome", lista);
		ValidatorUtil.validateRequired(serie, "Série", lista);
		ValidatorUtil.validaInt(capacidadeAluno, "Capacidade", lista);
		ValidatorUtil.validateRequired(turno, "Turno", lista);
		ValidatorUtil.validateRequired(dataInicio, "Início", lista);
		ValidatorUtil.validateRequired(dataFim, "Fim", lista);
		ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Início", lista);
		return lista;
	}

	/** Compara o ID com o passado por parâmetro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Calcula e retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	/**
	 * Retorna a descrição da série
	 * @return
	 */
	public String getDescricaoSerieTurma(){
		if (serie != null)
			return serie.getDescricaoCompleta() +" '"+ nome+"'";
		return "";
	}
	
}
