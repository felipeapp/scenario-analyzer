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
 * Entidade que registra os tipos de participação de docentes na organização de eventos
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "tipo_participacao_organizacao_eventos", schema = "prodocente")
public class TipoParticipacaoOrganizacaoEventos implements Validatable {

	public static final int EDITOR_PERIODICOS_JORNAIS_SIMILARES = 1;

	public static final int REVISOR_PERIODICOS_JORNAIS_SIMILARES = 2;

	public static final int MEMBRO_CONSELHO_EDITORIAL = 3;

	public static final int RESPONSAVEL_ORGANIZACAO_EVENTO = 4;

	public static final int MEMBRO_COMISSAO_ORGANIZADORA = 5;

	public static final int CONSULTOR_AD_HOC_REVISTA_CIENTIFICA = 8;

	public static final int CONFERENCISTA_CONVIDADO = 13;

	public static final int VISITAS_MISSOES_CIENTIFICAS = 14;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_participacao_organizacao_eventos", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	private boolean ativo;

	/** Creates a new instance of TipoAvaliacaoOrganizacaoEvento */
	public TipoParticipacaoOrganizacaoEventos(int id) {
		this.id = id;
	}

	public TipoParticipacaoOrganizacaoEventos() {

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
		if (idObject != null) {
			id = idObject;
		}
	}

	/*
	 * Campo Obrigatorio: Descricao
	 */

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDescricao(), "Descrição", lista);

		return lista;
	}

}
