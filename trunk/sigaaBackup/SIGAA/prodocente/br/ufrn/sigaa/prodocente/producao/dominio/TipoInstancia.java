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
 *
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_instancia", schema = "prodocente")
public class TipoInstancia implements Validatable {

	public static final int CONSELHO_EDITORIAL = 2;

	public static final int OUTROS = 3;
	
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_instancia", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	private boolean ativo;
	
	public TipoInstancia() {
	
	}
	
	public TipoInstancia(int id) {
		this.id = id;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
	 * Campo Obrigatorio: Descricao
	 */
	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(),"Descrição", lista.getMensagens());

		return lista;
	}
}