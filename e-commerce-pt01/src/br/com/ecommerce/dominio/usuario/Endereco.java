package br.com.ecommerce.dominio.usuario;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.ecommerce.arq.dominio.CadastroDB;

/**
 * Armazena o endere�o do usu�rio.
 * @author Thiago Viana Dantas
 *
 */
@Entity
@Table
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Endereco extends CadastroDB{

	/** 
	 * Endere�o da pessoa.
	 */
	@Column(length=2048)
	private String endereco;
	
	/** 
	 * N�mero da casa da pessoa.
	 */
	@Column(name="numero_casa")
	private int numeroCasa;
	
	/**
	 * Complemento do endere�o.
	 */
	private String complemento;
	
	/** 
	 * Bairro do endere�o da pessoa.
	 */
	@Column(length=64)
	private String bairro;
	
	/** 
	 * Cidade do endere�o da pessoa.
	 */
	@ManyToOne(fetch=FetchType.EAGER, cascade = javax.persistence.CascadeType.ALL)
	@JoinColumn(name="id_cidade", unique=true)
	private Cidade cidade;
	
	/** 
	 * CEP do endere�o da pessoa.
	 */
	@Column(name="cep")
	private long CEP;
	
	/**
	 * Propriet�rio do Endere�o.
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_pessoa")
	private Pessoa pessoa;
	
	public Endereco(){
		
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public int getNumeroCasa() {
		return numeroCasa;
	}

	public void setNumeroCasa(int numeroCasa) {
		this.numeroCasa = numeroCasa;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public Cidade getCidade() {
		return cidade;
	}

	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

	public long getCEP() {
		return CEP;
	}

	public void setCEP(long cEP) {
		CEP = cEP;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}
}
