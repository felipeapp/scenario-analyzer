/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/11/2011
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

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
 * Armazena as informa��es do preenchimento dos itens do Formul�rio de Evolu��o da Crian�a
 * para cada per�odo letivo considerado.
 * 
 * @author Leonardo Campos
 * @author Jean Guerethes
 */
@Entity
@Table(name="item_infantil_periodo", schema="infantil", uniqueConstraints={})
public class ItemInfantilPeriodo implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_item_infantil_periodo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Per�odo referente ao item preenchido. */
	private int periodo;
	
	/** Observa��es referentes ao item preenchido. */
	private String observacoes;
	
	/** Resultado da avalia��o do item preenchido. */
	private Integer resultado;
	
	/** Matr�cula em componente curricular do aluno correspondente ao item preenchido. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_matricula_componente")
	private MatriculaComponente matricula;
	
	/** Matr�cula em componente curricular do aluno correspondente ao item preenchido. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_item_infantil_formulario")
	private ItemInfantilFormulario itemFormulario;
	
	/** Construtor padr�o. */
	public ItemInfantilPeriodo() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Integer getResultado() {
		return resultado;
	}

	public void setResultado(Integer resultado) {
		this.resultado = resultado;
	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public ItemInfantilFormulario getItemFormulario() {
		return itemFormulario;
	}

	public void setItemFormulario(ItemInfantilFormulario itemFormulario) {
		this.itemFormulario = itemFormulario;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
	
}