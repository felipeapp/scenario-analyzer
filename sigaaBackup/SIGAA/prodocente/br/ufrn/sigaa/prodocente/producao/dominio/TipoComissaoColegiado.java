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
 * Entidade que registra os tipos de comissões e colegiados que um docente por participar
 *
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_comissao_colegiado", schema = "prodocente")
public class TipoComissaoColegiado implements Validatable {

	public static final int COLEGIADO_CURSO_CONSEC = 1;

	public static final int COMISSAO_PERMANENTE = 2;

	public static final int COMISSAO_TEMPORARIA = 3;

	public static final int COMISSAO_SINDICANCIA = 4;

	public static final int CRIACAO_CURSO_REFORM_PROJETO = 5;

	public static final int COLEGIADO_SUPERIOR = 6;

	public static final int ORIENTADOR_ACADEMICO = 7;

	public static final int COMITE_CIENTIFICO = 8;

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_comissao_colegiado", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	/** Creates a new instance of TipoComissaoColegiado */
	public TipoComissaoColegiado(int id) {
		this.id = id;
	}

	public TipoComissaoColegiado() {
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


	/*
	 *  Campos Obrigatorios: Descricao, Natureza
	 */

	public ListaMensagens validate(){

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);

		return lista;
	}

}
