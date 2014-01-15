/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
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
 * Entidade que representa as áreas de conhecimento do Conselho Nacional de
 * Desenvolvimento Científico e Tecnológico - CNPq.<br>
 * A Constituição Federal, ao tratar, em seu Artigo 218, da Ciência e
 * Tecnologia, refere-se a áreas de ciência. Entretanto, as agências públicas e
 * a comunidade científica, consagraram a expressão áreas do conhecimento.<br>
 * O CNPq propõe uma classificação das áreas do conhecimento em níveis
 * hierárquicos, estabelecendo grande área, área e subárea, tendo a área como
 * unidade básica de classificação.<br>
 * Por área do conhecimento entende-se o conjunto de conhecimentos
 * inter-relacionados, coletivamente construído, reunido segundo a natureza do
 * objeto de investigação com finalidades de ensino, pesquisa e aplicações
 * práticas<br>
 * A grande área é a aglomeração de diversas áreas do conhecimento em virtude da
 * afinidade de seus objetos, métodos cognitivos e recursos instrumentais
 * refletindo contextos sóciopolíticos específicos<br>
 * Por sub-área entende-se uma segmentação da área do conhecimento estabelecida
 * em função do objeto de estudo e de procedimentos metodológicos reconhecidos e
 * amplamente utilizados.<br>
 * Por especialidade entende-se a caracterização temática da atividade de
 * pesquisa e ensino. Uma mesma especialidade pode ser enquadrada em diferentes
 * grandes áreas, áreas e sub-áreas.
 */
@Entity
@Table(schema="comum", name = "area_conhecimento_cnpq", uniqueConstraints = {})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class AreaConhecimentoCnpq implements PersistDB {

	/** Variável para produções que não possuem area definidas. */
	public static final AreaConhecimentoCnpq INDEFINIDO = new AreaConhecimentoCnpq(1);

	// Fields

	/** Chave primária. */
	private int id;

	/** Referência para a grande área. */
	private AreaConhecimentoCnpq grandeArea;

	/** Referência para a subárea */
	private AreaConhecimentoCnpq subarea;

	/** Referência para a área */
	private AreaConhecimentoCnpq area;

	/** Referência para a especialidade */
	private AreaConhecimentoCnpq especialidade;
	
	/** Nome da área de conhecimento do CNPq. */
	private String nome;

	/** Indica se esta área está excluída. */
	private boolean excluido;

	/** Código da área. */
	private String codigo;

	/** Sigla da área. */
	private String sigla;
	
	
	//atributos transientes
	/** Quantidade de projetos de pesquisa nesta área. */
	private long qtdProjetos;

	/** Quantidade de consultores nesta área. */
	private long qtdConsultores;

	// Constructors

	/** Construtor padrão. */
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
	
	
	/** Retorna a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_area_conhecimento_cnpq", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int idAreaConhecimentoCnpq) {
		this.id = idAreaConhecimentoCnpq;
	}

	/** Retorna a referência para a grande área. 
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_grande_area", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getGrandeArea() {
		return this.grandeArea;
	}

	/** Seta a referência para a grande área.
	 * @param areaConhecimentoCnpqByIdGrandeArea
	 */
	public void setGrandeArea(
			AreaConhecimentoCnpq areaConhecimentoCnpqByIdGrandeArea) {
		this.grandeArea = areaConhecimentoCnpqByIdGrandeArea;
	}

	/** Retorna a referência para a especialidade.
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_especialidade", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getEspecialidade() {
		return this.especialidade;
	}

	/** Seta a referência para a especialidade.
	 * @param areaConhecimentoCnpqByIdEspecialidade
	 */
	public void setEspecialidade(
			AreaConhecimentoCnpq areaConhecimentoCnpqByIdEspecialidade) {
		this.especialidade = areaConhecimentoCnpqByIdEspecialidade;
	}

	/** Retorna a referência para a área. 
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo", unique = false, nullable = true, insertable = false, updatable = false)
	public AreaConhecimentoCnpq getArea() {
		return this.area;
	}

	/** Seta a referência para a área.
	 * @param area
	 */
	public void setArea(AreaConhecimentoCnpq area) {
		this.area = area;
	}
	
	/** Retorna a referência para a subárea. 
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_sub_area", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getSubarea() {
		return this.subarea;
	}

	/** Seta a referência para a subárea.
	 * @param areaConhecimentoCnpqByIdSubArea
	 */
	public void setSubarea(
			AreaConhecimentoCnpq areaConhecimentoCnpqByIdSubArea) {
		this.subarea = areaConhecimentoCnpqByIdSubArea;
	}

	/** Retorna o nome da área de conhecimento do CNPq. 
	 * @return
	 */
	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 120)
	public String getNome() {
		return this.nome;
	}

	/** Seta o nome da área de conhecimento do CNPq.
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Indica se esta área está excluída. 
	 * @return
	 */
	@Column(name = "excluido", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isExcluido() {
		return this.excluido;
	}

	/** Seta se esta área está excluída. 
	 * @param excluido
	 */
	public void setExcluido(boolean excluido) {
		this.excluido = excluido;
	}

	/** Retorna o código da área. 
	 * @return
	 */
	@Column(name = "codigo", unique = false, nullable = true, insertable = true, updatable = true, length = 12)
	public String getCodigo() {
		return this.codigo;
	}

	/** Seta o código da área.
	 * @param codigo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/** Retorna a sigla da área. 
	 * @return
	 */
	public String getSigla() {
		return sigla;
	}

	/** Seta a sigla da área.
	 * @param sigla
	 */
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}


	/** Retorna a quantidade de consultores nesta área. 
	 * @return
	 */
	@Transient
	public long getQtdConsultores() {
		return qtdConsultores;
	}

	/** Seta a quantidade de consultores nesta área.
	 * @param qtdConsultores
	 */
	public void setQtdConsultores(long qtdConsultores) {
		this.qtdConsultores = qtdConsultores;
	}

	/** Retorna a quantidade de projetos de pesquisa nesta área. 
	 * @return
	 */
	@Transient
	public long getQtdProjetos() {
		return qtdProjetos;
	}

	/** Seta a quantidade de projetos de pesquisa nesta área.
	 * @param qtdProjetos
	 */
	public void setQtdProjetos(long qtdProjetos) {
		this.qtdProjetos = qtdProjetos;
	}

	/**
	 * Compara se este objeto é igual ao passado por parâmetro, comparando as
	 * chaves primárias de ambos.
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

	/** Calcula e retorna o código hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, codigo);
	}
	

	/**
	 * Retorna uma representação textual desta área de conhecimento, no formato:
	 * código, seguido de vírgula, seguido do nome da área.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s, %s", codigo, nome);
	}
}