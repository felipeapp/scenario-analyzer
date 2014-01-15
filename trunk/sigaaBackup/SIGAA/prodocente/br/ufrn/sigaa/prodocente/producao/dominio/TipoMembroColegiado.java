/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que registra os tipos de participações que um docente pode ter em um colegiado
 *
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_membro_colegiado", schema = "prodocente")
public class TipoMembroColegiado implements Validatable {

	@Id 
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_membro_colegiado", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	private boolean ativo;
	
	/** Creates a new instance of TipoMembroColegiado */
	public TipoMembroColegiado() {
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

	public void setDescricao(String nome) {
		this.descricao = nome;
	}

	public Integer getIdObject() {
		return id;
	}

	public void setIdObject(Integer idObject) {
		if ( idObject != null ) {
			id = idObject;
		}
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/*
	 * Campo Obrigatorio: Nome
	 */
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(),"Descrição", lista);

		return lista;
	}
}