/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/01/2011
 *
 */
package br.ufrn.sigaa.projetos.dominio;

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
 * Detalhamento de recursos financeiros de ações associadas. Compõe o
 * relatório parcial e final. Através desta classe o
 * coordenador do projeto informa a quantidade de recursos utilizados durante a
 * execução do projeto.
 * </p>
 * 
 * @author geyson
 * 
 ******************************************************************************/
@Entity
@Table(name = "detalhamento_recursos_projeto", schema = "projetos")
public class DetalhamentoRecursosProjeto implements Validatable {
	
	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_detalhamento_recursos_projeto", nullable = false)
	private int id;

	/** Elemento de despesa. É como uma categoria de despesas. Ex. MATERIAL DE CONSUMO */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_elemento")
	private ElementoDespesa elemento;

	/** Total do recuso disponibilizado pelo faex */
	private Double interno = 0.0;

	/** Total do recuso disponibilizado pela funpec */
	private Double externo = 0.0;

	/** Total do recuso disponibilizado por fontes terceiras */
	private Double outros = 0.0;

	/** Valor  */
	@Transient
	private Double valor = 0.0;

	/** Relatório projeto do detalhamento */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_relatorio_projeto")
	private RelatorioAcaoAssociada relatorioProjeto;

	public DetalhamentoRecursosProjeto() {
	}
	public ListaMensagens validate() {
		return null;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ElementoDespesa getElemento() {
		return elemento;
	}
	public void setElemento(ElementoDespesa elemento) {
		this.elemento = elemento;
	}
	public Double getOutros() {
		return outros;
	}
	public void setOutros(Double outros) {
		this.outros = outros;
	}
	public Double getValor() {
		valor = interno + externo + outros;
		return valor;
	}
	public void setValor(Double valor) {
		this.valor = valor;
	}
	public Double getInterno() {
		return interno;
	}
	public void setInterno(Double interno) {
		this.interno = interno;
	}
	public Double getExterno() {
		return externo;
	}
	public void setExterno(Double externo) {
		this.externo = externo;
	}
	public RelatorioAcaoAssociada getRelatorioProjeto() {
		return relatorioProjeto;
	}
	public void setRelatorioProjeto(RelatorioAcaoAssociada relatorioProjeto) {
		this.relatorioProjeto = relatorioProjeto;
	}


}
