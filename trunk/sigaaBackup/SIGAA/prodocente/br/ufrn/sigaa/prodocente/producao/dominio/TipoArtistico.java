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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Tipo de produção artística, literária ou visual
 *
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_artistico", schema = "prodocente")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class TipoArtistico implements Validatable {

	public static final TipoArtistico ARTISTICO = new TipoArtistico(1);

	public static final TipoArtistico APRESENTACAO_EVENTO = new TipoArtistico(9);

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_artistico", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	@JoinColumn(name = "id_tipo_producao", referencedColumnName = "id_tipo_producao")
	@ManyToOne
	private TipoProducao tipoProducao = new TipoProducao();

	/** Creates a new instance of TipoArtistico */
	public TipoArtistico() {
	}

	public TipoArtistico(int id) {
		this.id=id;
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

	public TipoProducao getTipoProducao()
	{
		return this.tipoProducao;
	}

	public void setTipoProducao(TipoProducao tipoProducao)
	{
		this.tipoProducao = tipoProducao;
	}


	public Integer getIdObject() {
		return id;
	}



	public void setIdObject(Integer idObject) {
		if ( idObject != null ) {
			id = idObject;
		}
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
