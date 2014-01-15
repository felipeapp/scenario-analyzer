/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Classe que define um atributo mape�vel de uma classe a ser utilizada em uma importa��o de dados como,
 * por exemplo, a importa��o de discentes aprovados em processos seletivos externos.
 * 
 * @author �dipo Elder F. de Melo
 * @author Fred_Castro
 * 
 */
@Entity
@Table(schema = "tecnico", name = "atributo_mapeavel_tecnico")
public class AtributoClasseMapeavelTecnico implements PersistDB, Comparable<AtributoClasseMapeavelTecnico> {

	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_atributo_mapeavel_tecnico")
	private int id;
	/** Atributo em discente a ser mapeado, no formato EL. Exemplo: discente.pessoa.nome */
	private String atributo;
	/** Tipo de dado que o atributo recebe, ou seja, indica se � inteiro, data, etc. */
	@Column(name="tipo_dado")
	private TipoDadoAtributoTecnico tipoDado;
	/** Descri��o do atributo a ser mapeado. Exemplo: Nome do discente. */
	private String descricao;
	/** Indica se o atributo mape�vel deve ser obrigat�rio. */
	private boolean obrigatorio;
	/** Caso o atributo utilize tabela de equival�ncia, deve-se indicar qual classe � utilizada na equival�ncia. */
	@Column(name="classe_equivalente")
	private String classeEquivalente;
	/** Caso o atributo utilize tabela de equival�ncia, deve-se indicar qual m�todo descreve textualmente a classe. Exemplo: getNome, getDenominacao, getDescricaoCompleta. */
	@Column(name="metodo_descricao")
	private String metodoDescricao;
	/** Indica se deve filtrar os atributos mape�veis para apenas os que possuem similaridade (o nome � parecido). � utilizado para, por exemplo, listar apenas alguns munic�pios de uma lista com todos munic�pios cadastrados. */
	@Column(name="apenas_similares")
	private boolean apenasSimilares;
	/** Indica se deve filtrar apenas os atributos ativos. */
	@Column(name="apenas_ativos")
	private boolean apenasAtivos;
	/** Atributo da classe pelo qual buscar no suggestionBox. Exemplo: nome, descricao, etc. */
	@Column(name="atributo_busca_auto_complete")
	private String atributoBuscaAutoComplete;
	
	public AtributoClasseMapeavelTecnico () {
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAtributo() {
		return atributo;
	}
	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public boolean isObrigatorio() {
		return obrigatorio;
	}
	public void setObrigatorio(boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}
	/** Indica que este atributo ser� tratado atrav�s de uma tabela de equival�ncia. � o caso de,
	 * por exemplo, mapear o curso Zootecnia - Bacharelado - Manh� no ID = 5432 da matriz curricular correspondente.*/
	public boolean isUsaTabelaEquivalencia() {
		return !isEmpty(classeEquivalente);
	}
	public String getClasseEquivalente() {
		return classeEquivalente;
	}
	public void setClasseEquivalente(String classeEquivalente) {
		this.classeEquivalente = classeEquivalente;
	}
	public String getMetodoDescricao() {
		return metodoDescricao;
	}
	public void setMetodoDescricao(String metodoDescricao) {
		this.metodoDescricao = metodoDescricao;
	}
	public TipoDadoAtributoTecnico getTipoDado() {
		return tipoDado;
	}
	public void setTipoDado(TipoDadoAtributoTecnico tipoDado) {
		this.tipoDado = tipoDado;
	}

	public boolean isApenasSimilares() {
		return apenasSimilares;
	}

	public void setApenasSimilares(boolean apenasSimilares) {
		this.apenasSimilares = apenasSimilares;
	}

	public boolean isApenasAtivos() {
		return apenasAtivos;
	}

	public void setApenasAtivos(boolean apenasAtivos) {
		this.apenasAtivos = apenasAtivos;
	}
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(atributo);
	}
	@Override
	public boolean equals(Object other) {
		if (other != null && other instanceof AtributoClasseMapeavelTecnico) {
			return ((AtributoClasseMapeavelTecnico) other).getAtributo().equals(this.getAtributo());
		} else
			return false;
	}
	/** Retorna uma descri��o textual do mapeamento.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return descricao + " / " + atributo + (obrigatorio ? " (Obrigat�rio)" : "");
	}
	@Override
	public int compareTo(AtributoClasseMapeavelTecnico other) {
		if (other != null)
			return other.getDescricao().compareTo(this.getDescricao());
		return 1;
	}

	public String getAtributoBuscaAutoComplete() {
		return atributoBuscaAutoComplete;
	}

	public void setAtributoBuscaAutoComplete(String atributoBuscaAutoComplete) {
		this.atributoBuscaAutoComplete = atributoBuscaAutoComplete;
	}
	
	public boolean isUsaSuggestionBox() {
		return !isEmpty(atributoBuscaAutoComplete);
	}
}
