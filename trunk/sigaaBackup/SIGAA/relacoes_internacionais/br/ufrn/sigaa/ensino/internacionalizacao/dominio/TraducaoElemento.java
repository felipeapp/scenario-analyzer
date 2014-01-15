/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Objeto utilizado para relacionar a tradução de um campo(atributo de um objeto) a um idioma.
 *
 * @author Rafael Gomes
 */

@Entity
@Table(schema = "internacionalizacao", name = "traducao_elemento")
public class TraducaoElemento implements Validatable{

	/** Identificador */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="internacionalizacao.traducao_elemento_sequence") })
	@Column(name = "id_traducao_elemento", unique = true, nullable = false)
	private int id;
	
	/** Atributo do objeto traduzido.*/
	@ManyToOne(fetch=FetchType.EAGER) 
	@JoinColumn(name="id_item_traducao")
	private ItemTraducao itemTraducao;
	
	/** Idioma para o qual o atributo do objeto em questão foi traduzido.*/
	@Column
	private String idioma;
	
	/** Identificador do registro na base de dados, que teve seus campos traduzidos.*/
	@Column(name = "id_elemento")
	private Integer idElemento;
	
	/** Texto com a tradução do atributo.*/
	private String valor;
	
	/** Indica se a tradução do atributo foi revisada ou não.*/
	private Boolean revisado; 
	
	/** Indica se a tradução do elemento para tal idioma encontra-se ativo ou não. */
	@CampoAtivo(true)
	private Boolean ativo = true;
	
	/** Data de cadastro da tradução. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Data da última atualização do componente. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Registro entrada do usuário que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Registro entrada do usuário que realizou a última atualização. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Atributo transiente utilizando para retorna o idioma por extenso.*/
	@Transient
	private String descricaoIdioma;
	
	/** Atributo transiente que possibilita ou não a inserção de valores para a tradução do elemento na view.*/
	@Transient
	private boolean inputDisabled;
	
	/**
	 * Construtor padrão 
	 */
	public TraducaoElemento() {}

	/**
	 * Construtor
	 * @param itemTraducao
	 * @param idioma
	 * @param idElemento
	 */
	public TraducaoElemento(ItemTraducao itemTraducao, String idioma, Integer idElemento) {
		this.itemTraducao = itemTraducao;
		this.idioma = idioma;
		this.idElemento = idElemento;
	}

	/**
	 * Construtor mínimo
	 * @param id
	 */
	public TraducaoElemento(int id) {
		this.id = id;
	}
	
	/**
	 * Construtor
	 * @param itemTraducao
	 * @param idioma
	 * @param idElemento
	 * @param descricaoIdioma
	 */
	public TraducaoElemento(ItemTraducao itemTraducao, String idioma, Integer idElemento, String descricaoIdioma) {
		this.itemTraducao = itemTraducao;
		this.idioma = idioma;
		this.idElemento = idElemento;
		this.descricaoIdioma = descricaoIdioma;
	}
	
	/**
	 * Método responsável por verificar se a tradução pertence ao idioma local, ex.: pt para brasileiro
	 * @return
	 */
	public boolean isIdiomaLocal(){
		return IdiomasEnum.PORTUGUES.getId().equalsIgnoreCase(idioma);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ItemTraducao getItemTraducao() {
		return itemTraducao;
	}

	public void setItemTraducao(ItemTraducao itemTraducao) {
		this.itemTraducao = itemTraducao;
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	public Integer getIdElemento() {
		return idElemento;
	}

	public void setIdElemento(Integer idElemento) {
		this.idElemento = idElemento;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public Boolean getRevisado() {
		return revisado;
	}

	public void setRevisado(Boolean revisado) {
		this.revisado = revisado;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public String getDescricaoIdioma() {
		return descricaoIdioma;
	}

	public void setDescricaoIdioma(String descricaoIdioma) {
		this.descricaoIdioma = descricaoIdioma;
	}

	public boolean isInputDisabled() {
		return inputDisabled;
	}

	public void setInputDisabled(boolean inputDisabled) {
		this.inputDisabled = inputDisabled;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
