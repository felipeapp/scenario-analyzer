/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;

/**
 * Recomendação é um conceito atribuído pela CAPES ao programa de pós-graduação.
 * @author Gleydson
 */
@Entity
@Table(name = "recomendacao", schema = "stricto_sensu")
public class Recomendacao implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_recomendacao", nullable = false)
	private int id;

	/** o conceito atribuído */
	private short conceito;

	/** a data da avaliação, o conceito válido do curso é o que tem a data de avaliação mais recente */
	private Date dataAvaliacao;

	/** o curso que está sendo avaliado */
	@ManyToOne()
	@JoinColumn(name = "id_curso")
	private Curso curso;

	private String portaria;

	public short getConceito() {
		return conceito;
	}

	public void setConceito(short conceito) {
		this.conceito = conceito;
	}

	public Date getDataAvaliacao() {
		return dataAvaliacao;
	}

	public void setDataAvaliacao(Date dataAvaliacao) {
		this.dataAvaliacao = dataAvaliacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	
	public String getPortaria() {
		return portaria;
	}

	public void setPortaria(String portaria) {
		this.portaria = portaria;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(curso, "Curso", erros);
		ValidatorUtil.validaInt(conceito, "Conceito", erros);
		return erros;
	}

}
