/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 26/02/2007
 * 
 */
package br.ufrn.sigaa.ensino.stricto.dominio;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * <p>
 * Entidade respons�vel pelas linhas de pesquisa, que est�o relacionadas com a �rea de concentra��o do
 * programa. Podenso haver linhas de pesquisa sem �reas de concentra��o.
 * <br>
 * na qual linha de pesquisa de um projeto de pesquisa � a investiga��o com in�cio e 
 * final definidos, fundamentada em objetivos espec�ficos, visando a 
 * obten��o de resultados, de causa e efeito ou coloca��o de fatos 
 * novos em evid�ncia.
 * </p>
 * @author Gleydson Lima
 */
@Entity
@Table(name = "linha_pesquisa_stricto", schema = "stricto_sensu")
public class LinhaPesquisaStricto implements Validatable {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_linha_pesquisa", nullable = false)
	private int id;

	/** Denomina��o da linha de pesquisa */
	private String denominacao;

	/** �rea a qual a linha pertence */
	@ManyToOne()
	@JoinColumn(name = "id_area")
	private AreaConcentracao area;

	/** Programa ao qual a linha pertence, no caso de n�o pertencer a uma �rea e sim diretamente a um programa */
	@ManyToOne()
	@JoinColumn(name = "id_programa")
	private Unidade programa;
	
	/** Indica se a linha � selecionada (utilizado para controle de sele��o nos formul�rios). */
	@Transient
	private boolean selecionado;

	/** Descri��o complementar da linha de pesquisa, o usu�rio pode cadastrar ou n�o. */
	private String descricao; 
	
	/** Construtor padr�o. */
	public LinhaPesquisaStricto() { }
	
	/** Construtor parametrizado.
	 * @param id
	 */
	public LinhaPesquisaStricto(Integer id) {
		super();
		this.id = id;
	}

	/** Retorna a area a qual a linha pertence 
	 * @return
	 */
	public AreaConcentracao getArea() {
		return area;
	}

	/** Seta a area a qual a linha pertence
	 * @param area
	 */
	public void setArea(AreaConcentracao area) {
		this.area = area;
	}

	/** Retorna a denomina��o da linha de pesquisa 
	 * @return
	 */
	public String getDenominacao() {
		return denominacao;
	}

	/** Seta a denomina��o da linha de pesquisa
	 * @param denominacao
	 */
	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Valida os dados: programa, denomina��o
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(programa, "Programa", erros);
		ValidatorUtil.validateRequired(denominacao, "Nome", erros);
		return erros;
	}

	/**
	 * Retorna o programa ao qual a linha pertence, no caso de n�o pertencer a
	 * uma �rea e sim diretamente a um programa
	 * 
	 * @return
	 */
	public Unidade getPrograma() {
		return programa;
	}

	/**
	 * Seta o programa ao qual a linha pertence, no caso de n�o pertencer a uma
	 * �rea e sim diretamente a um programa
	 * 
	 * @param programa
	 */
	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	/** Indica se a linha � selecionada (utilizado para controle de sele��o nos formul�rios).  
	 * @return
	 */
	public boolean isSelecionado() {
		return selecionado;
	}

	/** Seta se a linha � selecionada (utilizado para controle de sele��o nos formul�rios). 
	 * @param selecionado
	 */
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/**
	 * Retorna uma representa��o textual no formato: denomina��o, seguido de
	 * v�rgula, seguido do programa
	 * @return
	 */
	public String toString() {
		return denominacao +", " + getPrograma().getNome();
	}
	
	/** Indica se esse objeto tem a chave igual ao objeto especificado.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	@Transient
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	/** Retorna o c�digo hash para o objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	@Transient
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Retorna a descri��o da linha de Pesquisa */
	public String getDescricao() {
		return descricao;
	}

	/** Seta uma descri��o para uma linha de pesquisa */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}