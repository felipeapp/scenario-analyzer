/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/11/2006
 *
 */
package br.ufrn.sigaa.extensao.relatorio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.dominio.ElementoDespesa;

/*******************************************************************************
 * <p>
 * Detalhamento de recursos financeiros de projetos de extensão. Compõe o
 * relatório parcial e final da ação de extensão. Através desta classe o
 * coordenador da ação informa a quantidade de recursos utilizados durante a
 * execução da ação.
 * </p>
 * 
 * @author Gleydson
 * 
 ******************************************************************************/
@Entity
@SuppressWarnings("serial")
@Table(name = "detalhamento_recursos", schema = "extensao")
public class DetalhamentoRecursos implements Validatable {

	/*** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_detalhamento_recursos")           
	private int id;

	/** Elemento de despesa. É como uma categoria de despesas. Ex. MATERIAL DE CONSUMO */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_elemento")
	private ElementoDespesa elemento;

	/** Total do recuso disponibilizado pelo faex */
	private Double faex = 0.0;

	/** Total do recuso disponibilizado pela funpec. */
	private Double funpec = 0.0;

	/** Total do recuso disponibilizado por fontes terceiras. */
	private Double outros = 0.0;
	
	/** Valor do unitário do recurso. */
	@Transient
	private Double valor = 0.0;

	/** Relatório ao qual o recurso está vinculado. */
	@ManyToOne
	@JoinColumn(name = "id_relatorio_acao_extensao")
	private RelatorioAcaoExtensao relatorioAcaoExtensao;

	public DetalhamentoRecursos() {
	}

	public ElementoDespesa getElemento() {
		return elemento;
	}

	public void setElemento(ElementoDespesa elemento) {
		this.elemento = elemento;
	}

	public Double getFaex() {
		return faex;
	}

	public void setFaex(Double faex) {
		this.faex = faex;
	}

	public Double getFunpec() {
		return funpec;
	}

	public void setFunpec(Double funpec) {
		this.funpec = funpec;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getOutros() {
		return outros;
	}

	public void setOutros(Double outros) {
		this.outros = outros;
	}

	/** Valor total do elemento de despesa. Soma de todas as fontes. */
	public Double getValor() {
		valor = faex + funpec + outros;
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public ListaMensagens validate() {
		return null;
	}

	public RelatorioAcaoExtensao getRelatorioAcaoExtensao() {
		return relatorioAcaoExtensao;
	}

	public void setRelatorioAcaoExtensao(
			RelatorioAcaoExtensao relatorioAcaoExtensao) {
		this.relatorioAcaoExtensao = relatorioAcaoExtensao;
	}

}
