/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Area de concentração são as áreas que um determinado Programa de Pós-Graduação trabalha.
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
	 * Denominação da área de concentração
	 */
	private String denominacao;
	
	@Transient
	private Collection<LinhaPesquisaStricto> linhasPesquisaStricto; 

	/**
	 * Determina a que nível pertence esta área de concentração
	 */
	private char nivel;

	/**
	 * Programa de pós ao qual esta área está vinculada
	 */
	@ManyToOne()
	@JoinColumn(name = "id_programa")
	private Unidade programa;

	/**
	 * Área de conhecimento CNPQ ao qual esta área de concentração pertence
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
		ValidatorUtil.validateRequired(nivel, "Nível", erros);
		ValidatorUtil.validateRequired(areaConhecimentoCnpq, "Área de Conhecimento CNPQ", erros);
		ValidatorUtil.validateRequired(denominacao, "Denominação", erros);
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
	 * Retorna a descrição completa da área de concentração contendo
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
