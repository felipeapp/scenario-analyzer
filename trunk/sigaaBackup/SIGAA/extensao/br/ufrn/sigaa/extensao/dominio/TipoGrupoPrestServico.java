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
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/*******************************************************************************
 * <p>
 * Classe utilizada no contexto de PrestacaoServicos. O modulo de prestação de
 * serviços ainda não está totalmente definido no SIGAA.
 * </p>
 * 
 * @author Gleydson
 * @author Victor Hugo
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "tipo_grupo_prest_servico")
public class TipoGrupoPrestServico implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_grupo_prest_servico", nullable = false)
	private int id;

	//Descrição textual do tipo de grupo de prestação de serviço.
	@Column(name = "descricao", nullable = false)
	private String descricao;

	@Column(name = "ajuda")
	private String ajuda;

	/** Creates a new instance of TipoGrupoPrestServico */
	public TipoGrupoPrestServico() {
	}

	public TipoGrupoPrestServico(int id) {
		this.id = id;
	}

	public TipoGrupoPrestServico(int id, String descricao) {
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

	public String getAjuda() {
		return this.ajuda;
	}

	public void setAjuda(String ajuda) {
		this.ajuda = ajuda;
	}

	public String toString() {
		return descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

}
