/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 10/01/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Representa o conteúdo programático de um Componente Curricular (Art. 35 No
 * 227/2009-CONSEPE)
 * 
 * @author Gleydson
 * 
 */
@Entity
@Table(name = "componente_curricular_programa", schema = "ensino")
public class ComponenteCurricularPrograma implements Validatable, Cloneable {

	/** Chave primária */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "hibernate_sequence") })
	@Column(name = "id_componente_curricular_programa", nullable = false)
	private int id;

	/** Objetivos */
	private String objetivos;

	/** Conteudo */
	private String conteudo;

	/** Competencias e Habilidades */
	private String competenciasHabilidades;

	/** Metodologia */
	private String metodologia;

	/** Procedimentos Avaliação */
	private String procedimentosAvaliacao;

	/** Referencias */
	private String referencias;

	/** Componente Curricular */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricular componenteCurricular;

	/** Ano */
	private Integer ano;

	/** Período */
	private Integer periodo;

	/** Número de Unidades */
	private Integer numUnidades;

	public Integer getNumUnidades() {
		return numUnidades;
	}

	public void setNumUnidades(Integer numUnidades) {
		this.numUnidades = numUnidades;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}

	public void setComponenteCurricular(
			ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	public String getProcedimentosAvaliacao() {
		return procedimentosAvaliacao;
	}

	public void setProcedimentosAvaliacao(String avaliacao) {
		this.procedimentosAvaliacao = avaliacao;
	}

	public String getCompetenciasHabilidades() {
		return competenciasHabilidades;
	}

	public void setCompetenciasHabilidades(String competencias) {
		this.competenciasHabilidades = competencias;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMetodologia() {
		return metodologia;
	}

	public void setMetodologia(String metodologia) {
		this.metodologia = metodologia;
	}

	public String getObjetivos() {
		return objetivos;
	}

	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}

	public String getReferencias() {
		return referencias;
	}

	public void setReferencias(String referencias) {
		this.referencias = referencias;
	}

	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(objetivos, "Objetivos", lista);
		ValidatorUtil.validateRequired(conteudo, "Conteúdo", lista);
		ValidatorUtil.validateRequired(competenciasHabilidades,
				"Competência e Habilidades", lista);
		ValidatorUtil.validateRequiredId(ano, "Ano", lista);
		ValidatorUtil.validateRequiredId(periodo, "Período", lista);

		/*
		 * ValidatorUtil.validateRequired(metodologia, "Metodologia", lista);
		 * ValidatorUtil.validateRequired(procedimentosAvaliacao, "Avaliação",
		 * lista); ValidatorUtil.validateRequired(referencias,
		 * "Referências Bibliográficas", lista);
		 * ValidatorUtil.validateMaxLength(metodologia, 10000, "Metodologia",
		 * lista); ValidatorUtil.validateMaxLength(procedimentosAvaliacao,
		 * 10000, "Avaliação", lista);
		 * ValidatorUtil.validateMaxLength(referencias, 10000, "Referências",
		 * lista);
		 */

		ValidatorUtil.validateMaxLength(objetivos, 10000, "Objetivos", lista);
		ValidatorUtil.validateMaxLength(conteudo, 10000, "Conteúdo", lista);
		ValidatorUtil.validateMaxLength(competenciasHabilidades, 10000,
				"Competência e Habilidades", lista);

/*		int tamanhoMinimo = 50, minimoCaracteres = 10, minimoPalavras = 10;

		ValidatorUtil.validateLeroLero(objetivos, "Objetivos", tamanhoMinimo,
				minimoCaracteres, minimoPalavras, lista);
		ValidatorUtil.validateLeroLero(conteudo, "Conteúdo", tamanhoMinimo,
				minimoCaracteres, minimoPalavras, lista);
		ValidatorUtil.validateLeroLero(competenciasHabilidades,
				"Competências e Habilidades", tamanhoMinimo, minimoCaracteres,
				minimoPalavras, lista);*/

		// ValidatorUtil.validateLeroLero(referencias,
		// "Referências Bibliográficas", tamanhoMinimo, minimoCaracteres,
		// minimoPalavras, lista);

		return lista;
	}

	@Override
	public ComponenteCurricularPrograma clone()
			throws CloneNotSupportedException {
		return (ComponenteCurricularPrograma) super.clone();
	}

	/**
	 * verifica se algum desses atributos foram modificados. usado para qnd for
	 * atualizar um obj.
	 * 
	 * @param obj
	 * @return
	 */
	public boolean equalsToUpdate(ComponenteCurricularPrograma obj) {
		return EqualsUtil.testEquals(this, obj, "id", "objetivos", "conteudo",
				"competenciasHabilidades");
	}

	public String getAnoPeriodo() {
		return getAno() + "." + getPeriodo();
	}

}
