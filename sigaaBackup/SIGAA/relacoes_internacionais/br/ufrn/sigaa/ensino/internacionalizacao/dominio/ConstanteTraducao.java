/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 16/08/2012
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
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Objeto responsável pela manutenção das constantes que serão traduzidos no sistema.
 * 
 * @author Rafael Gomes
 *
 */

@Entity
@Table(schema = "internacionalizacao", name = "constante_traducao")
public class ConstanteTraducao implements Validatable{
	
	/** Identificador */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_constante_traducao", unique = true, nullable = false)
	private int id;
	
	/** Nome da constante da classe a ser traduzido. */
	@Column
	private String constante;

	/** Texto com a tradução do atributo.*/
	@Column
	private String valor;

	/** Indica o nome completo da classe responsável pela entidade da constante traduzida. */
	@Column
	private String classe;
	
	/** Idioma para o qual o atributo do objeto em questão foi traduzido.*/
	@Column
	private String idioma;

	/** Entidade do objeto do atributo traduzido.*/
	@ManyToOne(fetch=FetchType.EAGER) 
	@JoinColumn(name="id_entidade_traducao")
	private EntidadeTraducao entidade;
	
	/** Indica se o item da entidade encontra-se ativa ou não, para a tradução. */
	@CampoAtivo(true)
	private Boolean ativo = true;
	
	/** Data de cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/** Atributo transiente utilizando para retorna o idioma por extenso.*/
	@Transient
	private String descricaoIdioma;
	
	/** Atributo transiente que possibilita ou não a inserção de valores para a constante de tradução na view.*/
	@Transient
	private boolean inputDisabled;
	
	
	/** Construtor padrão */
	public ConstanteTraducao() {}

	/**
	 * Construtor mínimo
	 * @param id
	 */
	public ConstanteTraducao(int id) {
		this.id = id;
	}

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public EntidadeTraducao getEntidade() {
		return entidade;
	}

	public void setEntidade(EntidadeTraducao entidade) {
		this.entidade = entidade;
	}

	public String getConstante() {
		return constante;
	}

	public void setConstante(String constante) {
		this.constante = constante;
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
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
		return null;
	}

}
