package br.ufrn.comum.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * No SIAPE, uma necessidade especial é identificada seu código SIAPE
 * e pelo código SIAPE do grupo de necessidades ao qual pertence.
 * Essa entidade persiste os grupos de necessidades existentes no SIAPE. 
 * @author SilviaMonteiro
 *
 */
@Entity
@Table (name="grupo_necessidade_especial", schema="comum")
public class GrupoNecessidadeEspecial implements Validatable{

	/**
	 * Identificador da entidade
	 */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
				  	  parameters = {@Parameter(name = "sequence_name", value = "comum.grupo_necessidade_especial_seq")})
	@Column(name="id_grupo_necessidade_especial")
	private int id;
	
	/** Código do grupo de necessidade especial no sistema do SIAPE */
	@Column(name = "codigo_siape")
	private Integer codigoSiape;
	
	/** Descrição do grupo de necessidade especial no sistema SIAPE, por exemplo, Deficiência Física, Deficiência auditiva, etc. */
	@Column (name = "descricao")
	private String descricao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getCodigoSiape() {
		return codigoSiape;
	}

	public void setCodigoSiape(Integer codigoSiape) {
		this.codigoSiape = codigoSiape;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens ();
		validateRequired(descricao, "Descrição", lista);
		validateRequired(codigoSiape, "Código Siape - Grupo da Necessidade", lista);
		return lista;
	}
	
}
