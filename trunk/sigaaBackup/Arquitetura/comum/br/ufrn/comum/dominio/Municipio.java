/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/09/2009
 */
package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
 
/**
* Entidade que armazena os dados dos municipios.
* 
* @author Jean Guerethes
* @author Gleydson Lima
*/
@Entity
@Table(name = "municipio", schema="comum")
public class Municipio implements Validatable, Comparable<Municipio> {

	public static final int NATAL = 1171;
	
	/**
	 * Id da UF padrão que deve ser utilizado pelos sistemas. 
	 */
	public static final int ID_MUNICIPIO_PADRAO = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.MUNICIPIO_PADRAO);

	// Fields

	private int id;

	/** unidade federativo ao qual o municipio pertence */
	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa();

	/** código do municipio */
	private String codigo;

	/** nome do municipio */
	private String nome;

	/** diz se o municipio está ativo ou não */
	private boolean ativo;
	
	/** código do municipio no SIAFI*/
	private String codSIAFI;
	
	// Constructors

	/**
	 * @return the ativo
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/**
	 * @param ativo the ativo to set
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** default constructor */
	public Municipio() {
	}

	/** default minimal constructor */
	public Municipio(int id) {
		this.id = id;
		unidadeFederativa = null;
	}
	
	/** default minimal constructor */
	public Municipio(String nome) {
		this.nome = nome;
	}

	/** minimal constructor */
	public Municipio(int idMunicipio, UnidadeFederativa unidadeFederativa,
			String codigo, String nome) {
		this.id = idMunicipio;
		this.unidadeFederativa = unidadeFederativa;
		this.codigo = codigo;
		this.nome = nome;
	}

	// Property accessors
	@Id
	@Column(name = "id_municipio", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	public int getId() {
		return this.id;
	}

	public void setId(int idMunicipio) {
		this.id = idMunicipio;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade_federativa", unique = false, nullable = false, insertable = true, updatable = true)
	public UnidadeFederativa getUnidadeFederativa() {
		return this.unidadeFederativa;
	}

	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	@Column(name = "codigo", unique = false, nullable = false, insertable = true, updatable = true, length = 12)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Override
	public String toString() {
		return nome;
	}

	@Transient
	public String getNomeUF() {
		return nome + " - " + unidadeFederativa.getSigla();
	}

	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequiredId(unidadeFederativa.getId(), "Unidade Federativa", erros);
		ValidatorUtil.validateRequired(codigo, "Código", erros);
		ValidatorUtil.validateRequired(nome, "Nome", erros);
		return erros;
	}

	public int compareTo(Municipio m) {
		return new CompareToBuilder()
			.append(this.getNome(), m.getNome())
			.toComparison();
	}

	@Column(name = "codigo_siafi")
	public String getCodSIAFI() {
		return codSIAFI;
	}

	public void setCodSIAFI(String codSIAFI) {
		this.codSIAFI = codSIAFI;
	}

}