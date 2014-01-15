/**
 * 
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;

/**
 * Representa o resultado do desempenho da criança para um determinado objetivo avaliado pelo professor
 * num determinado bimestre
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="objetivo_bimestre", schema="infantil", uniqueConstraints={})
@Deprecated
public class ObjetivoBimestre implements Validatable {

	/** Chave primária do Objetivo do Bimestre */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 
	@Column(name = "id_objetivo_bimestre", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Bimestre que será utilizado o objetivo */
	private int bimestre;
	
	/** Resultado do objetivo do bimestre escolhido */
	private int resultado;
	
	/** Armazena o objetivo do conteúdo do bimestre */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_objetivo")
	private ObjetivoConteudo objetivo;
	
	/** Matricula componente a qual o objetivo está vínculado */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_matricula_componente")
	private MatriculaComponente matricula;
	
	public ObjetivoBimestre() {
	}
	
	public ObjetivoBimestre(int bimestre, ObjetivoConteudo objetivo, MatriculaComponente matricula) {
		this.bimestre = bimestre;
		this.objetivo = objetivo;
		this.matricula = matricula;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBimestre() {
		return bimestre;
	}

	public void setBimestre(int bimestre) {
		this.bimestre = bimestre;
	}

	public int getResultado() {
		return resultado;
	}

	public void setResultado(int resultado) {
		this.resultado = resultado;
	}

	public ObjetivoConteudo getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(ObjetivoConteudo objetivo) {
		this.objetivo = objetivo;
	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}
	
	public ListaMensagens validate() {
		return null;
	}

}
