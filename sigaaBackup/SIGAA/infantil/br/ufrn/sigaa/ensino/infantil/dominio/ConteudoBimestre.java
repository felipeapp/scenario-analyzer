/**
 * 
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
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
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="conteudo_bimestre", schema="infantil", uniqueConstraints={})
public class ConteudoBimestre implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") }) 
	@Column(name = "id_conteudo_bimestre", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	private int bimestre;
	
	private String observacoes;
	
	@ManyToOne
	@JoinColumn(name="id_conteudo")
	private Conteudo conteudo;
	
	@ManyToOne
	@JoinColumn(name="id_matricula_componente")
	private MatriculaComponente matricula;
	
	public ConteudoBimestre() {
	}
	
	public ConteudoBimestre(int bimestre, Conteudo conteudo, MatriculaComponente matricula) {
		this.bimestre = bimestre;
		this.conteudo = conteudo;
		this.matricula = matricula;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public Conteudo getConteudo() {
		return conteudo;
	}

	public void setConteudo(Conteudo conteudo) {
		this.conteudo = conteudo;
	}

	public int getBimestre() {
		return bimestre;
	}

	public void setBimestre(int bimestre) {
		this.bimestre = bimestre;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}
	
}
