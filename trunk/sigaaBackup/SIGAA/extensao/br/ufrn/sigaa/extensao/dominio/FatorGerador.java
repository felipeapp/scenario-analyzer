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
 * @author Victor Hugo
 * @author Gleydson
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "fator_gerador")
public class FatorGerador implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_fator_gerador", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	/** Construtor padrão */
	public FatorGerador() {
	}

	public FatorGerador(int id) {
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

	public String toString() {
		return descricao;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		return lista;
	}

}
