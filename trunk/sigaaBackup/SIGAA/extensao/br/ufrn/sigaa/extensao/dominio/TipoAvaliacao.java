/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/02/2008
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
 * Classe que representa o Tipo de avaliação realizada por membros do comitê de
 * extensão ou avaliadores Ad Hoc.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "tipo_avaliacao")
public class TipoAvaliacao implements Validatable {

	// avaliação feita por convidados membros da UFRN
	public static final int AVALIACAO_ACAO_PARECERISTA = 1;

	// avaliação do comitê
	public static final int AVALIACAO_ACAO_COMITE = 2;

	// avaliação da PROEX para projetos que não solicitaram recursos FAEX
	public static final int AVALIACAO_ACAO_PROEX = 3;

	// relatório da ação pelo departamento
	public static final int AVALIACAO_RELATORIO_DEPARTAMENTO = 4;

	// relatório da ação pela PROEX
	public static final int AVALIACAO_RELATORIO_PROEX = 5;

	// avaliação do comitê
	public static final int AVALIACAO_ACAO_PRESIDENTE_COMITE = 6;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_avaliacao", nullable = false)
	private int id;

	@Column(name = "descricao")
	private String descricao;

	public TipoAvaliacao() {
	}

	public TipoAvaliacao(int id) {
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
