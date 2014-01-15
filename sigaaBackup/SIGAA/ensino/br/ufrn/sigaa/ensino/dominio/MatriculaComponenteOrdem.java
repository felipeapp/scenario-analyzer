/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 17/01/2012
 */
package br.ufrn.sigaa.ensino.dominio;

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

/**
 * Entidade que registra as matr�culas de forma��o complementar realizadas com sua respectiva ordem.
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "matricula_componente_ordem", schema = "ensino", uniqueConstraints = {})
public class MatriculaComponenteOrdem implements Validatable {
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "hibernate_sequence") })
	@Column(name = "id_matricula_componente_ordem", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Ordem de realiza��o da matr�cula */
	private int ordem;
	
	/** Matr�cula em componente curricular gerada atrav�s da matr�cula */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_matricula_componente")
	private MatriculaComponente matriculaComponente;

	@Override
	public ListaMensagens validate() {
		return null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public MatriculaComponente getMatriculaComponente() {
		return matriculaComponente;
	}

	public void setMatriculaComponente(MatriculaComponente matriculaComponente) {
		this.matriculaComponente = matriculaComponente;
	}
}
