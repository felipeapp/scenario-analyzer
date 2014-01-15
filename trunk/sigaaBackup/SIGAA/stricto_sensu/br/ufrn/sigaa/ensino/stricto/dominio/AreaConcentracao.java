/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 26/02/2007
 * 
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Area de concentra��o s�o as �reas que um determinado Programa de P�s-Gradua��o trabalha.
 * 
 * @author Gleydson
 */
@Entity
@Table(name = "area_concentracao", schema = "stricto_sensu")
public class AreaConcentracao implements Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_area_concentracao", nullable = false)
	private int id;

	/**
	 * Denomina��o da �rea de concentra��o
	 */
	private String denominacao;
	
	@Transient
	private Collection<LinhaPesquisaStricto> linhasPesquisaStricto; 

	/**
	 * Determina a que n�vel pertence esta �rea de concentra��o
	 */
	private char nivel;

	/**
	 * Programa de p�s ao qual esta �rea est� vinculada
	 */
	@ManyToOne()
	@JoinColumn(name = "id_programa")
	private Unidade programa;

	/**
	 * �rea de conhecimento CNPQ ao qual esta �rea de concentra��o pertence
	 */
	@ManyToOne()
	@JoinColumn(name = "id_area_conhecimento_cnpq")
	private AreaConhecimentoCnpq areaConhecimentoCnpq;

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(programa, "Programa", erros);
		ValidatorUtil.validateRequired(nivel, "N�vel", erros);
		ValidatorUtil.validateRequired(areaConhecimentoCnpq, "�rea de Conhecimento CNPQ", erros);
		ValidatorUtil.validateRequired(denominacao, "Denomina��o", erros);
		return erros;
	}

	public Unidade getPrograma() {
		return programa;
	}

	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public String getNivelDesc() {
		return NivelEnsino.getDescricao(nivel);
	}

	public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
		return areaConhecimentoCnpq;
	}

	public void setAreaConhecimentoCnpq(AreaConhecimentoCnpq areaConhecimentoCnpq) {
		this.areaConhecimentoCnpq = areaConhecimentoCnpq;
	}

	@Override
	public String toString() {
		return denominacao != null ? denominacao + " (" + getNivelDesc()+ ")" : "";
	}
	
	/**
	 * Retorna a descri��o completa da �rea de concentra��o contendo
	 * {@link #denominacao} e {@link #getNivelDesc()} 
	 * @return
	 */
	public String getDenominacaoCompleta(){
		return toString();
	}
	
	public Collection<LinhaPesquisaStricto> getLinhasPesquisaStricto() {
		return linhasPesquisaStricto;
	}
	
	public void setLinhasPesquisaStricto(
			Collection<LinhaPesquisaStricto> linhasPesquisaStricto) {
		this.linhasPesquisaStricto = linhasPesquisaStricto;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
}
