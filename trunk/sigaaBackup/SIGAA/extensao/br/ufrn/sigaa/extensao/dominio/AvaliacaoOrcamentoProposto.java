/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/03/2008
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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.ElementoDespesa;

/*******************************************************************************
 * <p>
 * Representa a proposta, feita pelos avaliadores, para o orçamento da proposta
 * de ação de extensão.
 * <br>
 * O orçamento proposto é informado durante a avaliação da ação pelo presidente
 * do comitê de extensão.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "avaliacao_orcamento_proposto")
public class AvaliacaoOrcamentoProposto implements Validatable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_avaliacao_orcamento_proposto", nullable = false)
	private int id;

	//Elemento de despesa do orçamento. Um elemento de despesa pode por exemplo
	//ser 'Materia de limpesa', outro ser 'material de consumo'.
	@ManyToOne
	@JoinColumn(name = "id_elemento_despesa")
	private ElementoDespesa elementoDespesa;

	//Avaliação da atividade.
	@ManyToOne
	@JoinColumn(name = "id_avaliacao_atividade")
	private AvaliacaoAtividade avaliacaoAtividade;

	//Valor proposto pelo avaliador para um tal elemento de despesa.
	@Column(name = "valor_proposto")
	private double valorProposto;

	public AvaliacaoOrcamentoProposto() {
	}

	public AvaliacaoOrcamentoProposto(int id) {
		this.id = id;
	}

	public AvaliacaoOrcamentoProposto(ElementoDespesa elementoDispesa,
			AvaliacaoAtividade avaliacaoAtividade) {
		this.elementoDespesa = elementoDispesa;
		this.avaliacaoAtividade = avaliacaoAtividade;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(elementoDespesa, "Elemento de Despesa", lista);
		return lista;
	}

	public ElementoDespesa getElementoDespesa() {
		return elementoDespesa;
	}

	public void setElementoDespesa(ElementoDespesa elementoDespesa) {
		this.elementoDespesa = elementoDespesa;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id,", "avaliacaoAtividade.id",
				"elementoDespesa.id");
	}

	public AvaliacaoAtividade getAvaliacaoAtividade() {
		return avaliacaoAtividade;
	}

	public void setAvaliacaoAtividade(AvaliacaoAtividade avaliacaoAtividade) {
		this.avaliacaoAtividade = avaliacaoAtividade;
	}

	public double getValorProposto() {
		return valorProposto;
	}

	public void setValorProposto(double valorProposto) {
		this.valorProposto = valorProposto;
	}

}
