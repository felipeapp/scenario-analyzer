/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 10/11/2011
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;

/**
 * Entidade responsável por armazenar as matrículas de componente por matrícula do discente em série.
 * 
 * @author Rafael Gomes
 *
 */
@Entity
@Table(name="matricula_componente_serie", schema="medio")
public class MatriculaComponenteSerie implements Validatable, Comparable<MatriculaComponenteSerie> {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_matricula_componente_serie", nullable = false)
	private int id;
	
	/** Matricula da Série de ensino médio ao qual este objeto pertence, referente ao relacionamento NxN de MatriculaComponente com matriculaDiscenteSerie. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_matricula_discente_serie", nullable = false)
	private MatriculaDiscenteSerie matriculaDiscenteSerie;
	
	/** Matricula componente de ensino médio ao qual este objeto pertence, referente ao relacionamento NxN de MatriculaComponente com matriculaDiscenteSerie.  */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_matricula_componente", nullable = false)
	private MatriculaComponente matriculaComponente;
	
	/** Atributo utilizado para informar se a disciplina está sendo paga como dependência.*/
	@Column(name = "dependencia", nullable = false)
	private boolean dependencia = false;
	
	/** Indica se a matricula de componente está ativa na matrícula do discente na série relacionada, permitindo sua utilização no SIGAA. */
	@Column(name = "ativo", nullable = false)
	private boolean ativo = true;
	
	/** Data de cadastro das informações da turma. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro de quem realizou o cadastro */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada registroEntrada;

	//Constructors
	/** default constructor */
	public MatriculaComponenteSerie() {
	}
	
	/**
	 * default minimal constructor 
	 * @param id
	 */
	public MatriculaComponenteSerie(int id) {
		this.id = id;
	}
	
	/**
	 * minimal constructor
	  * @param id
	 * @param turmaSerie
	 * @param turma
	 */
	public MatriculaComponenteSerie(int id, MatriculaDiscenteSerie matriculaDiscenteSerie, MatriculaComponente matriculaComponente) {
		super();
		this.id = id;
		this.matriculaDiscenteSerie = matriculaDiscenteSerie;
		this.matriculaComponente = matriculaComponente;
	}
	
	
	//Getters and Setters

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public MatriculaDiscenteSerie getMatriculaDiscenteSerie() {
		return matriculaDiscenteSerie;
	}

	public void setMatriculaDiscenteSerie(
			MatriculaDiscenteSerie matriculaDiscenteSerie) {
		this.matriculaDiscenteSerie = matriculaDiscenteSerie;
	}

	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}

	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}

	public boolean isDependencia() {
		return dependencia;
	}

	public void setDependencia(boolean dependencia) {
		this.dependencia = dependencia;
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

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(matriculaDiscenteSerie, "Matrícula do Discente na Série", lista);
		ValidatorUtil.validateRequired(matriculaComponente, "Matrícula do Discente na disciplina", lista);
		ValidatorUtil.validateRequired(ativo, "Ativo", lista);
		return lista;
	}

	@Override
	public int compareTo(MatriculaComponenteSerie mcs) {
		return (matriculaComponente.getComponente().getNome()).compareTo(mcs.matriculaComponente.getComponente().getNome());
	}

}