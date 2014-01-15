/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que armazena as informações dos países
 */
@Entity
@Table(schema="comum", name = "pais", uniqueConstraints = {})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Pais implements Validatable {

	/** Constante que define o código quando o país informado for o Brasil.  */
	public static final int BRASIL = 31;
	/** Constante que define o código quando país não for informado. */
	public static final int NAO_INFORMADO = -1;

	/** Atribute que define a unicidade. */
	private int id;

	/** Atributo que define o nome do pais */
	private String nome;

	/** Designação da Nacionalidade:
		001          BRASIL		Brasileiro
		002	AFRICA DO SUL	Sul Africano
		003	ANDORRA	???
		004	AFGANISTÃO	Afegão
		005	ARGÉLIA	                Argelino
		006	ALEMANHA            Alemão
		... 
	  */
	private String nacionalidade;

	/** default constructor */
	public Pais() {
	}

	/** default minimal constructor */
	public Pais(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public Pais(int idPais, String nome) {
		this.id = idPais;
		this.nome = nome;
	}

	/** full constructor */
	public Pais(int idPais, String nome, String nacionalidade) {
		this.id = idPais;
		this.nome = nome;
		this.nacionalidade = nacionalidade;
	}

	// Property accessors
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_pais", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idPais) {
		this.id = idPais;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "nacionalidade", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getNacionalidade() {
		return this.nacionalidade;
	}

	public void setNacionalidade(String nacionalidade) {
		this.nacionalidade = nacionalidade;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, nome);
	}
	
	@Transient
	public boolean isBrasil() {
		return this.id == BRASIL;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(nome, "Nome", lista);
		ValidatorUtil.validateRequired(nacionalidade, "Nacionalidade", lista);
		return lista;
	}
	
	/** Retorna uma representação textual deste objeto no formato: nome.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return nome;
	}
}
