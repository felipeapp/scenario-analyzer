/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/10/2006
 *
 */
package br.ufrn.sigaa.extensao.dominio;

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

/*******************************************************************************
 * Detalhes a prestação de serviços, um tipo de ação de extensão que atualmente
 * não está totalmente definida no sistema.
 * 
 * 
 * @author Victor Hugo
 * @author Gleydson
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "detalhe_prest_servico")
public class DetalhePrestServico implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_detalhe_prest_servico", nullable = false)
	private int id;

	@Column(name = "descricao", nullable = false)
	private String descricao;

	@JoinColumn(name = "id_tipo_grupo_prest_servico")
	@ManyToOne
	private br.ufrn.sigaa.extensao.dominio.TipoGrupoPrestServico tipoGrupoPrestServico;

	/** Creates a new instance of DetalhePrestServico */
	public DetalhePrestServico() {
	}

	public DetalhePrestServico(Integer idDetalhePrestServico) {
		this.id = idDetalhePrestServico;
	}

	public DetalhePrestServico(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
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

	public br.ufrn.sigaa.extensao.dominio.TipoGrupoPrestServico getTipoGrupoPrestServico() {
		return this.tipoGrupoPrestServico;
	}

	public void setTipoGrupoPrestServico(
			br.ufrn.sigaa.extensao.dominio.TipoGrupoPrestServico tipoGrupoPrestServico) {
		this.tipoGrupoPrestServico = tipoGrupoPrestServico;
	}

	public String toString() {
		return "br.ufrn.sigaa.extensao.dominio.DetalhePrestServico[id=" + id
		+ "]";
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		ValidatorUtil.validateRequired(tipoGrupoPrestServico, "Tipo Grupo de Prestação de Serviço", lista);

		return lista;
	}

}
