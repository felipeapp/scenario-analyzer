/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Entidade responsável pelas linhas de pesquisa, que estão relacionadas com a área de concentração do
 * programa. Podenso haver linhas de pesquisa sem áreas de concentração.
 * <br>
 * na qual linha de pesquisa de um projeto de pesquisa é a investigação com início e 
 * final definidos, fundamentada em objetivos específicos, visando a 
 * obtenção de resultados, de causa e efeito ou colocação de fatos 
 * novos em evidência.
 * </p>
 * @author Gleydson Lima
 */
@Entity
@Table(name = "linha_pesquisa_stricto", schema = "stricto_sensu")
public class LinhaPesquisaStricto implements Validatable {

	/** Chave primária. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_linha_pesquisa", nullable = false)
	private int id;

	/** Denominação da linha de pesquisa */
	private String denominacao;

	/** Área a qual a linha pertence */
	@ManyToOne()
	@JoinColumn(name = "id_area")
	private AreaConcentracao area;

	/** Programa ao qual a linha pertence, no caso de não pertencer a uma área e sim diretamente a um programa */
	@ManyToOne()
	@JoinColumn(name = "id_programa")
	private Unidade programa;
	
	/** Indica se a linha é selecionada (utilizado para controle de seleção nos formulários). */
	@Transient
	private boolean selecionado;

	/** Descrição complementar da linha de pesquisa, o usuário pode cadastrar ou não. */
	private String descricao; 
	
	/** Construtor padrão. */
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

	/** Retorna a denominação da linha de pesquisa 
	 * @return
	 */
	public String getDenominacao() {
		return denominacao;
	}

	/** Seta a denominação da linha de pesquisa
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

	/** Valida os dados: programa, denominação
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(programa, "Programa", erros);
		ValidatorUtil.validateRequired(denominacao, "Nome", erros);
		return erros;
	}

	/**
	 * Retorna o programa ao qual a linha pertence, no caso de não pertencer a
	 * uma área e sim diretamente a um programa
	 * 
	 * @return
	 */
	public Unidade getPrograma() {
		return programa;
	}

	/**
	 * Seta o programa ao qual a linha pertence, no caso de não pertencer a uma
	 * área e sim diretamente a um programa
	 * 
	 * @param programa
	 */
	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	/** Indica se a linha é selecionada (utilizado para controle de seleção nos formulários).  
	 * @return
	 */
	public boolean isSelecionado() {
		return selecionado;
	}

	/** Seta se a linha é selecionada (utilizado para controle de seleção nos formulários). 
	 * @param selecionado
	 */
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	/**
	 * Retorna uma representação textual no formato: denominação, seguido de
	 * vírgula, seguido do programa
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

	/** Retorna o código hash para o objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	@Transient
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Retorna a descrição da linha de Pesquisa */
	public String getDescricao() {
		return descricao;
	}

	/** Seta uma descrição para uma linha de pesquisa */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}