package br.ufrn.sigaa.projetos.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;

/*******************************************************************************
 * <p>
 * Representa o tipo de autoriza��o dada pelos chefes de departamentos �s a��es.
 * <br/>
 * Exemplo: 1=AD-REFERENDUM, 2=REUNI�O ORDIN�RIA, 3=REUNI�O EXTRA ORDINARIA
 * </p>
 * 
 ******************************************************************************/
@Entity
@Table(name = "tipo_autorizacao_departamento", schema = "projetos")
public class TipoAutorizacaoDepartamento implements Validatable {

	/** Identificador �nico do tipo de autoriza��o. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_autorizacao_departamento")
	private int id;

	/** Descri��o do tipo de autoriza��o. */
	@Column(name = "descricao", nullable = false)
	private String descricao;

	/** Informa que a a��o n�o foi autorizada. */
	public static final int NAO_AUTORIZAR = 0;

	/** A a��o foi autorizada, mas a decis�o pode ser revista. */
	public static final int AD_REFERENDUM = 1;

	/** A a��o foi autorizada em reuni�o ordin�ria do departamento. */
	public static final int REUNIAO_ORDINARIA = 2;

	/** A a��o foi autorizada em reuni�o extra ordin�ria. */
	public static final int REUNIAO_EXTRA_ORDINARIA = 3;

	// Constructors

	/** default constructor */
	public TipoAutorizacaoDepartamento() {
	}

	/** full constructor */
	public TipoAutorizacaoDepartamento(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public ListaMensagens validate() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}