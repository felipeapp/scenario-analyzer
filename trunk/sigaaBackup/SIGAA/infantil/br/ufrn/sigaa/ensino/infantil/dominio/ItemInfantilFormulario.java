/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/11/2011
 */
package br.ufrn.sigaa.ensino.infantil.dominio;

import javax.persistence.CascadeType;
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

/**
 * Representa a associa��o de um item a um Formul�rio de Evolu��o da Crian�a espec�fico. 
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name="item_infantil_formulario", schema="infantil", uniqueConstraints={})
public class ItemInfantilFormulario implements Validatable {

	/** Constante que define o n�vel do bloco no formul�rio. */
	public static final int BLOCO = 0;
	/** Constante que define o n�vel da �rea no formul�rio. */
	public static final int AREA = 1;
	/** Constante que define o n�vel do conte�do no formul�rio. */
	public static final int CONTEUDO = 2;
	/** Constante que define o n�vel do sub-Conte�do no formul�rio. */
	public static final int SUBCONTEUDO = 3;
	/** Constante que define o n�vel dos objetivos no formul�rio. */
	public static final int OBJETIVOS = 4;
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_item_infantil_formulario", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Profundidade do item na �rvore do formul�rio. */
	private int profundidade;
	
	/** Ordem do item na sequ�ncia da �rvore do formul�rio. */
	private int ordem;
	
	/** Per�odos nos quais o item deve ser exibido. */
	private int periodo;
	
	/** Referencia as informa��es do item utilizado para compor o formul�rio. */
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="id_item_infantil")
	private ItemInfantil item = new ItemInfantil();
	
	/** Formul�rio de Evolu��o da Crian�a do qual o item faz parte. */
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="id_formulario_evolucao_crianca")
	private FormularioEvolucaoCrianca formulario;
	
	/** Indica se o item ser� ou n�o edit�vel */
	@Transient
	private boolean editavel = true;
	
	/** Indica se deve ser exibido o textArea na tela de resumo */
	@Transient
	private boolean exibirTextArea = false;

	/** Indica se deve ser exibido o bimestre na tela de resumo */
	@Transient
	private boolean exibirBimestre = false;
	
	@Transient
	private ItemInfantilPeriodo itemPeriodo;
	
	@Transient
	private boolean selecionado = false;
	
	/** Construtor padr�o. */
	public ItemInfantilFormulario() {
	}
	
	/** Construtor padr�o. */
	public ItemInfantilFormulario(int profundidade) {
		this.profundidade = profundidade;
	}

	/** Serve para tabular a montagem da tela de visualiza��o do formul�rio */
	@Transient
	public String identar(){
		return "border:0; padding-right: "+ 10 * profundidade +"px;";
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProfundidade() {
		return profundidade;
	}

	public void setProfundidade(int profundidade) {
		this.profundidade = profundidade;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public FormularioEvolucaoCrianca getFormulario() {
		return formulario;
	}

	public void setFormulario(FormularioEvolucaoCrianca formulario) {
		this.formulario = formulario;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	public ItemInfantil getItem() {
		return item;
	}

	public void setItem(ItemInfantil item) {
		this.item = item;
	}

	public boolean isEditavel() {
		return editavel;
	}

	public void setEditavel(boolean editavel) {
		this.editavel = editavel;
	}
	
	public boolean isExibirTextArea() {
		return exibirTextArea;
	}

	public void setExibirTextArea(boolean exibirTextArea) {
		this.exibirTextArea = exibirTextArea;
	}

	public boolean isExibirBimestre() {
		return exibirBimestre;
	}

	public void setExibirBimestre(boolean exibirBimestre) {
		this.exibirBimestre = exibirBimestre;
	}

	public boolean isBloco(){
		return profundidade == BLOCO;
	}
	
	public boolean isArea(){
		return profundidade == AREA;
	}

	public boolean isConteudo(){
		return profundidade == CONTEUDO;
	}

	public boolean isSubCont(){
		return profundidade == SUBCONTEUDO;
	}

	public boolean isObjetivo(){
		return profundidade == OBJETIVOS;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public ItemInfantilPeriodo getItemPeriodo() {
		return itemPeriodo;
	}

	public void setItemPeriodo(ItemInfantilPeriodo itemPeriodo) {
		this.itemPeriodo = itemPeriodo;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isSelecionado() {
		return selecionado;
	}
	
}