/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/02/2010
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;

/**
 * Entidade que representa o período de duração da "Indicação de bolsa REUNI" do um discente de pós-graduação
 * para um plano de trabalho.
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name="periodo_indicacao_reuni", schema="stricto_sensu")
public class PeriodoIndicacaoReuni implements Validatable {
	
	/**
	 * Chave primária da indicação.
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_periodo_indicacao_reuni")
	private int id;
	
	/**
	 * Indicação da Bolsa Reuni.
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_indicacao_bolsista_reuni")
	private IndicacaoBolsistaReuni indicacaoBolsistaReuni;
	
	/** Plano de docência assistida */
	@OneToMany(mappedBy = "periodoIndicacaoBolsa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<PlanoDocenciaAssistida> planoDocenciaAssistida = new ArrayList<PlanoDocenciaAssistida>();	
		
	/**
	 * Ano e Período da duração da indicação da bolsa.
	 */
	@Column(name = "ano_periodo")
	private Integer anoPeriodo;	

	public ListaMensagens validate() {
		return null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public IndicacaoBolsistaReuni getIndicacaoBolsistaReuni() {
		return indicacaoBolsistaReuni;
	}

	public void setIndicacaoBolsistaReuni(
			IndicacaoBolsistaReuni indicacaoBolsistaReuni) {
		this.indicacaoBolsistaReuni = indicacaoBolsistaReuni;
	}

	public Integer getAnoPeriodo() {
		return anoPeriodo;
	}

	public void setAnoPeriodo(Integer anoPeriodo) {
		this.anoPeriodo = anoPeriodo;
	}
	
	@Transient
	public String getAnoPeriodoFormatado(){
		return String.valueOf( Double.valueOf((double) anoPeriodo / 10 ));
	}
	
	@Transient
	public int getAno(){
		return Integer.parseInt( String.valueOf(anoPeriodo).substring(0, 4));
	}
	
	@Transient
	public int getPeriodo(){
		return Integer.parseInt( String.valueOf(anoPeriodo).substring(4, 5));
	}	
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	public List<PlanoDocenciaAssistida> getPlanoDocenciaAssistida() {
		return planoDocenciaAssistida;
	}

	public void setPlanoDocenciaAssistida(
			List<PlanoDocenciaAssistida> planoDocenciaAssistida) {
		this.planoDocenciaAssistida = planoDocenciaAssistida;
	}	
}
