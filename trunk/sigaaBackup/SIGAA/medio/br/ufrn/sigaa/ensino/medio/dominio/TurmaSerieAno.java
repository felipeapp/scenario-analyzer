/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 24/05/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Entidade respons�vel por armazenar as turmas de disciplinas pertencentes a uma Turma.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name="turma_serie_ano", schema="medio")
public class TurmaSerieAno implements Validatable, Comparable<TurmaSerieAno> {

	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_turma_serie_ano", nullable = false)
	private int id;
	
	/** Turma de S�rie de ensino m�dio ao qual este objeto pertence, referente ao relacionamento NxN de turmas com turmaSerie. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_turma_serie", nullable = false)
	private TurmaSerie turmaSerie = new TurmaSerie();
	
	/** Turma de ensino m�dio ao qual este objeto pertence, referente ao relacionamento NxN de turmas com turmaSerie. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_turma", nullable = false)
	private Turma turma;
	
	/** Indica se a turma est� ativa no s�rie relacionada, permitindo sua utiliza��o no SIGAA. */
	@Column(name = "ativo", nullable = false)
	private boolean ativo = true;
	
	/** Data de cadastro das informa��es da turma. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro de quem realizou o cadastro */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/** Atributo utilizado na listagem de disciplinas por discente, 
	 * para informar se a disciplina est� sendo paga como depend�ncia.*/
	@Transient
	private boolean dependencia;
	
	//Constructors
	/** default constructor */
	public TurmaSerieAno() {
	}
	
	/**
	 * default minimal constructor 
	 * @param id
	 */
	public TurmaSerieAno(int id) {
		this.id = id;
	}
	
	/**
	 * minimal constructor
	  * @param id
	 * @param turmaSerie
	 * @param turma
	 */
	public TurmaSerieAno(int id, TurmaSerie turmaSerie, Turma turma) {
		super();
		this.id = id;
		this.turmaSerie = turmaSerie;
		this.turma = turma;
	}
	
	/** Retorna a descri��o de depend�ncia de matr�cula do discente.*/
	public String getDescricaoDependencia(){
		return isDependencia() ? "(DEPEND�NCIA)" : null;
	}
	
	//Getters and Setters

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public TurmaSerie getTurmaSerie() {
		return turmaSerie;
	}
	public void setTurmaSerie(TurmaSerie turmaSerie) {
		this.turmaSerie = turmaSerie;
	}
	public Turma getTurma() {
		return turma;
	}
	public void setTurma(Turma turma) {
		this.turma = turma;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	public Date getDataCadastro() {
		return dataCadastro;
	}
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public boolean isDependencia() {
		return dependencia;
	}

	public void setDependencia(boolean dependencia) {
		this.dependencia = dependencia;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(turmaSerie, "Turma da S�rie", lista);
		ValidatorUtil.validateRequired(turma, "Turma", lista);
		ValidatorUtil.validateRequired(ativo, "Ativo", lista);
		return lista;
	}

	@Override
	public int compareTo(TurmaSerieAno tsa) {
		return (turma.getDisciplina().getNome()).compareTo(tsa.turma.getDisciplina().getNome());
	}
}
