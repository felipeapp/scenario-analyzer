package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Necessidades especiais das pessoas.
 * 
 * podem ser: Deficiência física, auditíva, mental e multipla.
 * 
 * @author Gleydson Lima
 * 
 */
@Entity
@Table(name="comum.tipo_necessidade_especial")
public class TipoNecessidadeEspecial implements PersistDB, Validatable{

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_necessidade_especial", unique = true, nullable = false, insertable = true, updatable = true)
	/** Identificador */
	private int id;

	/** Descrição da deficiência */
	private String descricao;

	/** Código da necessidade especial no sistema do SIAPE */
	@Transient //Column(name = "codigo_siape")
	private Integer codigoSiape;

	
	/**
	 * Constructor default
	 */
	public TipoNecessidadeEspecial() {	}
	
	/**
	 * Constructor minimal
	 */
	public TipoNecessidadeEspecial(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getCodigoSiape() {
		return codigoSiape;
	}

	public void setCodigoSiape(Integer codigoSiape) {
		this.codigoSiape = codigoSiape;
	}
	
	/** Utilizado para verifica se os campos obrigatório foram informados.  */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", erros);
		return erros;
	}
}