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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Tipos de região geográfica de abrangência de um evento
 *
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_regiao", schema = "prodocente")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class TipoRegiao implements Validatable {

	public static final int LOCAL = 1;

	public static final int REGIONAL = 2;

	public static final int NACIONAL = 3;

	public static final int INTERNACIONAL = 4;

	public static final int NAO_INFORMADO = 5;

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_regiao", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	private boolean ativo;
	
	/** Creates a new instance of TipoRegiao */
	public TipoRegiao(int id) {
		this.id = id;
	}

	public TipoRegiao() {
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

		ValidatorUtil.validateRequired(getDescricao(),"Descrição", lista);

		return lista;
	}
}