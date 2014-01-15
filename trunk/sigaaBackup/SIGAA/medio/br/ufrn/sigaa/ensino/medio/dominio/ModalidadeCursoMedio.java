/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 26/05/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * Tipo de modalidade de um curso m�dio
 * 
 * @author Rafael Rodrigues
 */
@Entity
@Table(name = "modalidade_curso_medio", schema = "medio", uniqueConstraints = {})
public class ModalidadeCursoMedio implements PersistDB, Validatable {

	/** Indica quais modalidades para cursos de m�dio do tipo COCOMITANTE. */
	public static final int CONCOMITANTE = 1;
	/** Indica quais modalidades para cursos de m�dio que s�o realizados em integra��o a outros curso de n�vel T�cnico. */
	public static final int INTEGRADO = 2;
	/** Indica quais modalidades para cursos de m�dio do tipo SUBSEQUENTE. */
	public static final int SUBSEQUENTE = 3;
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_modalidade_curso_medio", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descri��o da Modalidade. */
	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	private String descricao;

	// Constructors

	/** default constructor */
	public ModalidadeCursoMedio() {
	}

	/** minimal constructor */
	public ModalidadeCursoMedio(int idTipoModalidade) {
		this.id = idTipoModalidade;
	}

	/** minimal constructor */
	public ModalidadeCursoMedio(int idTipoModalidade, String descricao) {
		this.id = idTipoModalidade;
		this.descricao = descricao;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int idTipoModalidade) {
		this.id = idTipoModalidade;
	}

	
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	/** Utilizado para verifica se os campos obrigat�rio foram informados.  */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descri��o", erros);
		return erros;
	}
}
