/**
 *
 */
package br.ufrn.comum.dominio.correios;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;


/**
 * Entidade que contém as informações dos diversos
 * logradouros (ruas, avenidas, etc.) do país.
 * Cada logradouro possui um CEP associado.
 * 
 * (Dados extraídos da base de dados dos Correios)
 * 
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "logradouro",schema = "correios")
public class Logradouro {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	private String nome;

	@Column(name = "tipo_logradouro")
	private String tipoLogradouro;

	private Long cep;

	@ManyToOne
	@JoinColumn(name="id_bairro")
	private Bairro bairro;

	@ManyToOne
	@JoinColumn(name="id_localidade")
	private Localidade localidade;

	public Bairro getBairro() {
		return this.bairro;
	}

	/**
	 * Retorna a descrição completa do logradouro, incluindo seu tipo
	 * 
	 * @return
	 */
	public String getNomeCompleto() {
		return tipoLogradouro + " " + nome;
	}

	public void setBairro(Bairro bairro) {
		this.bairro = bairro;
	}

	public Long getCep() {
		return this.cep;
	}

	public void setCep(Long cep) {
		this.cep = cep;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Localidade getLocalidade() {
		return this.localidade;
	}

	public void setLocalidade(Localidade localidade) {
		this.localidade = localidade;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipoLogradouro() {
		return this.tipoLogradouro;
	}

	public void setTipoLogradouro(String tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

}
