/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '01/12/2006'
 *
 */
package br.ufrn.sigaa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Entidade que representa as �reas de conhecimento do Conselho Nacional de
 * Desenvolvimento Cient�fico e Tecnol�gico - CNPq.<br>
 * A Constitui��o Federal, ao tratar, em seu Artigo 218, da Ci�ncia e
 * Tecnologia, refere-se a �reas de ci�ncia. Entretanto, as ag�ncias p�blicas e
 * a comunidade cient�fica, consagraram a express�o �reas do conhecimento.<br>
 * O CNPq prop�e uma classifica��o das �reas do conhecimento em n�veis
 * hier�rquicos, estabelecendo grande �rea, �rea e sub�rea, tendo a �rea como
 * unidade b�sica de classifica��o.<br>
 * Por �rea do conhecimento entende-se o conjunto de conhecimentos
 * inter-relacionados, coletivamente constru�do, reunido segundo a natureza do
 * objeto de investiga��o com finalidades de ensino, pesquisa e aplica��es
 * pr�ticas<br>
 * A grande �rea � a aglomera��o de diversas �reas do conhecimento em virtude da
 * afinidade de seus objetos, m�todos cognitivos e recursos instrumentais
 * refletindo contextos s�ciopol�ticos espec�ficos<br>
 * Por sub-�rea entende-se uma segmenta��o da �rea do conhecimento estabelecida
 * em fun��o do objeto de estudo e de procedimentos metodol�gicos reconhecidos e
 * amplamente utilizados.<br>
 * Por especialidade entende-se a caracteriza��o tem�tica da atividade de
 * pesquisa e ensino. Uma mesma especialidade pode ser enquadrada em diferentes
 * grandes �reas, �reas e sub-�reas.
 */
@Entity
@Table(schema="comum", name = "area_conhecimento_cnpq", uniqueConstraints = {})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class AreaConhecimentoCnpq implements PersistDB {

	/** Vari�vel para produ��es que n�o possuem area definidas. */
	public static final AreaConhecimentoCnpq INDEFINIDO = new AreaConhecimentoCnpq(1);

	// Fields

	/** Chave prim�ria. */
	private int id;

	/** Refer�ncia para a grande �rea. */
	private AreaConhecimentoCnpq grandeArea;

	/** Refer�ncia para a sub�rea */
	private AreaConhecimentoCnpq subarea;

	/** Refer�ncia para a �rea */
	private AreaConhecimentoCnpq area;

	/** Refer�ncia para a especialidade */
	private AreaConhecimentoCnpq especialidade;
	
	/** Nome da �rea de conhecimento do CNPq. */
	private String nome;

	/** Indica se esta �rea est� exclu�da. */
	private boolean excluido;

	/** C�digo da �rea. */
	private String codigo;

	/** Sigla da �rea. */
	private String sigla;
	
	
	//atributos transientes
	/** Quantidade de projetos de pesquisa nesta �rea. */
	private long qtdProjetos;

	/** Quantidade de consultores nesta �rea. */
	private long qtdConsultores;

	// Constructors

	/** Construtor padr�o. */
	public AreaConhecimentoCnpq() {
	}

	/** Construtor parametrizado.
	 * 
	 * @param id
	 */
	public AreaConhecimentoCnpq(int id) {
		this.id = id;
	}

	/** Construtor parametrizado.
	 * 
	 * @param idAreaConhecimentoCnpq
	 * @param nome
	 */
	public AreaConhecimentoCnpq(int idAreaConhecimentoCnpq, String nome) {
		this.id = idAreaConhecimentoCnpq;
		this.nome = nome;
	}
	
	/** Construtor parametrizado.
	 * 
	 * @param idAreaConhecimentoCnpq
	 * @param nome
	 */
	public AreaConhecimentoCnpq(int idAreaConhecimentoCnpq, String sigla, String nome) {
		this(idAreaConhecimentoCnpq, nome);
		this.sigla = sigla;
	}
	

	/** Construtor parametrizado.
	 * 
	 * @param idAreaConhecimentoCnpq
	 * @param areaConhecimentoCnpqByIdGrandeArea
	 * @param areaConhecimentoCnpqByIdEspecialidade
	 * @param areaConhecimentoCnpqByIdSubArea
	 * @param nome
	 * @param excluido
	 * @param codigo
	 */
	public AreaConhecimentoCnpq(int idAreaConhecimentoCnpq,
			AreaConhecimentoCnpq areaConhecimentoCnpqByIdGrandeArea,
			AreaConhecimentoCnpq areaConhecimentoCnpqByIdEspecialidade,
			AreaConhecimentoCnpq areaConhecimentoCnpqByIdSubArea, String nome,
			boolean excluido, String codigo) {
		this.id = idAreaConhecimentoCnpq;
		this.grandeArea = areaConhecimentoCnpqByIdGrandeArea;
		this.especialidade = areaConhecimentoCnpqByIdEspecialidade;
		this.subarea = areaConhecimentoCnpqByIdSubArea;
		this.nome = nome;
		this.excluido = excluido;
		this.codigo = codigo;
	}

	// Property accessors
	
	
	/** Retorna a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_area_conhecimento_cnpq", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int idAreaConhecimentoCnpq) {
		this.id = idAreaConhecimentoCnpq;
	}

	/** Retorna a refer�ncia para a grande �rea. 
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_grande_area", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getGrandeArea() {
		return this.grandeArea;
	}

	/** Seta a refer�ncia para a grande �rea.
	 * @param areaConhecimentoCnpqByIdGrandeArea
	 */
	public void setGrandeArea(
			AreaConhecimentoCnpq areaConhecimentoCnpqByIdGrandeArea) {
		this.grandeArea = areaConhecimentoCnpqByIdGrandeArea;
	}

	/** Retorna a refer�ncia para a especialidade.
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_especialidade", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getEspecialidade() {
		return this.especialidade;
	}

	/** Seta a refer�ncia para a especialidade.
	 * @param areaConhecimentoCnpqByIdEspecialidade
	 */
	public void setEspecialidade(
			AreaConhecimentoCnpq areaConhecimentoCnpqByIdEspecialidade) {
		this.especialidade = areaConhecimentoCnpqByIdEspecialidade;
	}

	/** Retorna a refer�ncia para a �rea. 
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo", unique = false, nullable = true, insertable = false, updatable = false)
	public AreaConhecimentoCnpq getArea() {
		return this.area;
	}

	/** Seta a refer�ncia para a �rea.
	 * @param area
	 */
	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}
	
	/** Retorna a refer�ncia para a sub�rea. 
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_sub_area", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getSubarea() {
		return this.subarea;
	}

	/** Seta a refer�ncia para a sub�rea.
	 * @param areaConhecimentoCnpqByIdSubArea
	 */
	public void setSubarea(
			AreaConhecimentoCnpq areaConhecimentoCnpqByIdSubArea) {
		this.subarea = areaConhecimentoCnpqByIdSubArea;
	}

	/** Retorna o nome da �rea de conhecimento do CNPq. 
	 * @return
	 */
	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 120)
	public String getNome() {
		return this.nome;
	}

	/** Seta o nome da �rea de conhecimento do CNPq.
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Indica se esta �rea est� exclu�da. 
	 * @return
	 */
	@Column(name = "excluido", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isExcluido() {
		return this.excluido;
	}

	/** Seta se esta �rea est� exclu�da. 
	 * @param excluido
	 */
	public void setExcluido(boolean excluido) {
		this.excluido = excluido;
	}

	/** Retorna o c�digo da �rea. 
	 * @return
	 */
	@Column(name = "codigo", unique = false, nullable = true, insertable = true, updatable = true, length = 12)
	public String getCodigo() {
		return this.codigo;
	}

	/** Seta o c�digo da �rea.
	 * @param codigo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/** Retorna a sigla da �rea. 
	 * @return
	 */
	public String getSigla() {
		return sigla;
	}

	/** Seta a sigla da �rea.
	 * @param sigla
	 */
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}


	/** Retorna a quantidade de consultores nesta �rea. 
	 * @return
	 */
	@Transient
	public long getQtdConsultores() {
		return qtdConsultores;
	}

	/** Seta a quantidade de consultores nesta �rea.
	 * @param qtdConsultores
	 */
	public void setQtdConsultores(long qtdConsultores) {
		this.qtdConsultores = qtdConsultores;
	}

	/** Retorna a quantidade de projetos de pesquisa nesta �rea. 
	 * @return
	 */
	@Transient
	public long getQtdProjetos() {
		return qtdProjetos;
	}

	/** Seta a quantidade de projetos de pesquisa nesta �rea.
	 * @param qtdProjetos
	 */
	public void setQtdProjetos(long qtdProjetos) {
		this.qtdProjetos = qtdProjetos;
	}

	/**
	 * Compara se este objeto � igual ao passado por par�metro, comparando as
	 * chaves prim�rias de ambos.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (getId() == 0) {
			return false;
		} else {
			return EqualsUtil.testEquals(this, obj, "id");
		}
	}

	/** Calcula e retorna o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, codigo);
	}
	

	/**
	 * Retorna uma representa��o textual desta �rea de conhecimento, no formato:
	 * c�digo, seguido de v�rgula, seguido do nome da �rea.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s, %s", codigo, nome);
	}
}