/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/06/2008
 *
 */
package br.ufrn.sigaa.extensao.relatorio.dominio;

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

/**
 * <p>
 * Classe que representa o tipo de relatório de extensão Exemplo: Relatório
 * parcial, Relatório Final, Relatório de justificativa.
 * </p>
 * 
 * 
 * @author Ilueny Santos
 */
@Entity
@Table(schema = "extensao", name = "tipo_relatorio")
public class TipoRelatorioExtensao implements Validatable {

	/** Tipo de relatório parcial. */
	public static int RELATORIO_PARCIAL = 1;

	/** Tipo de relatório final. */
	public static int RELATORIO_FINAL = 2;

	/** Identificador único do tipo de relatório. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_relatorio")
	private int id;

	/** Descricção do relatório. */
	@Column(name = "descricao")
	private String descricao;

	/** Creates a new instance of TipoProjeto */
	public TipoRelatorioExtensao() {
	}

	public TipoRelatorioExtensao(int id) {
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

	/** Descrição do tipo de relatório. */
	public String toString() {
		return descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

}
