/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2007
 *
 */
package br.ufrn.sigaa.extensao.dominio;

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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/*******************************************************************************
 * <p>
 * Representa um resumo do orçamento detalhado informado durante o cadastro de
 * uma ação de extensão.
 * </p>
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@Entity
@Table(schema = "extensao", name = "orcamento_consolidado")
public class OrcamentoConsolidado implements Validatable {

	/** Identificador único do objeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_orcamento_consolidado", nullable = false)
	private int id;

	/** Representa uma categoria de despesas */
	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(name = "id_elemento_despesa")
	private ElementoDespesa elementoDespesa;

	/** Projeto ao qual se refere o orçamento */
	@ManyToOne(fetch= FetchType.EAGER)
	@JoinColumn(name = "id_projeto")
	private Projeto projeto;
	
	/**  Fundação (funpec) */
	@Column(name = "fundacao")
	private Double fundacao = 0.0;

	/**  Fundo de apoio a extensão universitária - Faex. */
	@Column(name = "fundo")
	private Double fundo = 0.0;

	/** Outros. */
	@Column(name = "outros")
	private Double outros = 0.0;

	/** Atributos preenchidos após a avaliação da proposta pelo comitê de extensão */
	@Column(name = "fundacao_concedido")
	private double fundacaoConcedido;

	/** Total concedido após avaliação */
	@Column(name = "fundo_concedido")
	private double fundoConcedido;

	/** Outros recursos concedido após avaliação */
	@Column(name = "outros_concedido")
	private double outrosConcedido;

	/** Atributos preenchidos após a execução da ação de extensão, o preenchimento destes campos é feito durante o relatório de prestação de contas da ação. */
	/** Total de recursos oriundos da fundação da IFES que foram gastos. */
	@Column(name = "fundacao_utilizado")
	private double fundacaoUtilizado;

	/** Total dos recursos utilizados */
	@Column(name = "fundo_utilizado")
	private double fundoUtilizado;

	/** Outros recursos utilizados. */
	@Column(name = "outros_utilizado")
	private double outrosUtilizado;

	/** Total do orçamento. Utilizado somente na view. */
	@Transient
	private double totalOrcamento;

	public OrcamentoConsolidado() {
	}

	public OrcamentoConsolidado(int id) {
		this.id = id;
	}

	public OrcamentoConsolidado(ElementoDespesa elementoDispesa, Projeto projeto) {
		this.elementoDespesa = elementoDispesa;
		this.projeto = projeto;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getFundacao() {
		return fundacao;
	}

	public void setFundacao(Double fundacao) {
		this.fundacao = fundacao;
	}

	public Double getFundo() {
		return fundo;
	}

	public void setFundo(Double fundo) {
		this.fundo = fundo;
	}

	public Double getOutros() {
		return outros;
	}

	public void setOutros(Double outros) {
		this.outros = outros;
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
		return EqualsUtil.testEquals(this, obj, "id,", "elementoDespesa.id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, elementoDespesa.getId());
	}
	
	public Double getTotalConsolidado() {
		return fundo + fundacao + outros;
	}

	public double getTotalConsolidadoConcedido() {
		return fundoConcedido + fundacaoConcedido + outrosConcedido;
	}

	public double getTotalOrcamento() {
		return totalOrcamento;
	}

	public void setTotalOrcamento(double totalOrcamento) {
		this.totalOrcamento = totalOrcamento;
	}

	public double getFundacaoConcedido() {
		return fundacaoConcedido;
	}

	public void setFundacaoConcedido(double fundacaoConcedido) {
		this.fundacaoConcedido = fundacaoConcedido;
	}

	public double getFundoConcedido() {
		return fundoConcedido;
	}

	public void setFundoConcedido(double fundoConcedido) {
		this.fundoConcedido = fundoConcedido;
	}

	public double getOutrosConcedido() {
		return outrosConcedido;
	}

	public void setOutrosConcedido(double outrosConcedido) {
		this.outrosConcedido = outrosConcedido;
	}

	public double getFundacaoUtilizado() {
		return fundacaoUtilizado;
	}

	public void setFundacaoUtilizado(double fundacaoUtilizado) {
		this.fundacaoUtilizado = fundacaoUtilizado;
	}

	public double getFundoUtilizado() {
		return fundoUtilizado;
	}

	public void setFundoUtilizado(double fundoUtilizado) {
		this.fundoUtilizado = fundoUtilizado;
	}

	public double getOutrosUtilizado() {
		return outrosUtilizado;
	}

	public void setOutrosUtilizado(double outrosUtilizado) {
		this.outrosUtilizado = outrosUtilizado;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

}
