/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/01/2011
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
@Entity
@Table(schema = "projetos", name = "tipo_relatorio")
public class TipoRelatorioProjeto implements Validatable {
	
	/** Tipo Parcial */
	public static int RELATORIO_PARCIAL = 1;
	
	/** Tipo Final */
	public static int RELATORIO_FINAL = 2;
	
	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_relatorio", nullable = false)
	private int id;

	/** Descricao */
	@Column(name = "descricao")
	private String descricao;

	/** Creates a new instance of TipoProjeto */
	public TipoRelatorioProjeto() {
	}

	/** Creates a new instance of TipoProjeto */
	public TipoRelatorioProjeto(int id) {
		this.id = id;
	}

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

	/** Valida tipo relatório */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

}
