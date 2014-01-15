/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
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
 * Recomenda��o � um conceito atribu�do pela CAPES ao programa de p�s-gradua��o.
 * @author Gleydson
 */
@Entity
@Table(name = "recomendacao", schema = "stricto_sensu")
public class Recomendacao implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_recomendacao", nullable = false)
	private int id;

	/** o conceito atribu�do */
	private short conceito;

	/** a data da avalia��o, o conceito v�lido do curso � o que tem a data de avalia��o mais recente */
	private Date dataAvaliacao;

	/** o curso que est� sendo avaliado */
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
