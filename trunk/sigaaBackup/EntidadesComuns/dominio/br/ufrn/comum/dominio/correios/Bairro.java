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
 * Entidade que guarda informações de bairros 
 * das diversas localidades do país.
 * 
 * (Dados extraídos da base de dados dos Correios)
 *
 * @author Ricardo Wendell
 *
 */
@Entity
@Table(name = "bairro",schema = "correios")
public class Bairro {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	private String nome;

	@ManyToOne
	@JoinColumn(name="id_localidade")
	private Localidade localidade;

	public Bairro() {

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

}
