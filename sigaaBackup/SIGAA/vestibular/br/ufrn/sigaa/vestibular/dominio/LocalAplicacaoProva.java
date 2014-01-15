/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;

/**
 * Entidade que Define quais os possíveis locais de aplicação de prova
 * 
 * @author Édipo
 * 
 */
@Entity
@Table(name = "local_aplicacao_prova", schema = "vestibular", uniqueConstraints = {})
public class LocalAplicacaoProva implements PersistDB, Validatable,
		Comparable<LocalAplicacaoProva> {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_local_aplicacao_prova", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Nome do local de aplicação de prova. */
	private String nome;
	
	/** Nome do contato no local de aplicação de prova. */
	private String contato;
	
	/** Endereço do local de aplicação de prova. */
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_endereco")
	private Endereco endereco;
	
	/** E-mail do contato no local de aplicação de prova. */
	private String email;
	
	/** Telefone do contato do local de aplicação de prova. */
	private String telefone;
	
	/** Celular do contato do local de aplicação de prova. */
	private String celular;
	
	/** DDD do telefone do contato do local de aplicação de prova. */
	@Column(name = "codigo_area_nacional_telefone_fixo", nullable = true, insertable = true, updatable = true)
	private Short codigoAreaNacionalTelefoneFixo;
	
	/** DDD do celular do contato do local de aplicação de prova. */
	@Column(name = "codigo_area_nacional_telefone_celular", nullable = true, insertable = true, updatable = true)
	private Short codigoAreaNacionalTelefoneCelular;
	
	/** Quantidade de fiscais para trabalhar em salas. */
	@Column(name = "num_fiscais_sala", nullable = true, insertable = true, updatable = true)
	private int numFiscaisSala;
	
	/** Quantidade de fiscais para trabalhar nos corredores. */
	@Column(name = "num_fiscais_corredor", nullable = true, insertable = true, updatable = true)
	private int numFiscaisCorredor;
	
	/** Quantidade de pessoas de apoio. */
	@Column(name = "num_apoio", nullable = true, insertable = true, updatable = true)
	private int numApoio;
	
	/** Quantidade de seguranças de apoio. */
	@Column(name = "num_segurancas", nullable = true, insertable = true, updatable = true)
	private int numSegurancas;
	
	/** Capacidade ideal total de candidatos no local de aplicação de prova. */
	@Column(name = "capacidade_ideal_total", nullable = true, insertable = true, updatable = true)
	private int capacidadeIdealTotal;

	/** Lista de salas do local de aplicação de prova. */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_local_aplicacao_prova", unique = false, nullable = true, insertable = true, updatable = true)
	@OrderBy("numero")
	private List<Sala> salas;

	/** Construtor padrão. */
	public LocalAplicacaoProva() {
		salas = new ArrayList<Sala>();
		inicializaEndereco();
	}

	/** Construtor com ID.
	 *  @param id
	 */
	public LocalAplicacaoProva(int id) {
		this.id = id;
	}

	/** Retorna a capacidade ideal total de candidatos no local de aplicação de prova. 
	 * @return
	 */
	public int getCapacidadeIdealTotal() {
		return capacidadeIdealTotal;
	}

	/** Retorna o celular do contato do local de aplicação de prova. 
	 * @return
	 */
	public String getCelular() {
		return celular;
	}

	/** Retorna o DDD do celular do contato do local de aplicação de prova. 
	 * @return
	 */
	public Short getCodigoAreaNacionalTelefoneCelular() {
		return codigoAreaNacionalTelefoneCelular;
	}

	/** Retorna o DDD do telefone do contato do local de aplicação de prova. 
	 * @return
	 */
	public Short getCodigoAreaNacionalTelefoneFixo() {
		return codigoAreaNacionalTelefoneFixo;
	}

	/** Retorna o nome do contato no local de aplicação de prova. 
	 * @return
	 */
	public String getContato() {
		return contato;
	}

	/** Retorna o e-mail do contato no local de aplicação de prova. 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	/** Retorna o endereço do local de aplicação de prova. 
	 * @return
	 */
	public Endereco getEndereco() {
		return endereco;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna o nome do local de aplicação de prova. 
	 * @return
	 */
	public String getNome() {
		return nome;
	}

	/** Retorna a quantidade de pessoas de apoio. 
	 * @return
	 */
	public int getNumApoio() {
		return numApoio;
	}

	/** Retorna a quantidade de fiscais para trabalhar nos corredores. 
	 * @return
	 */
	public int getNumFiscaisCorredor() {
		return numFiscaisCorredor;
	}

	/** Retorna a quantidade de fiscais para trabalhar em salas. 
	 * @return
	 */
	public int getNumFiscaisSala() {
		return numFiscaisSala;
	}

	/** Retorna a quantidade de seguranças de apoio. 
	 * @return
	 */
	public int getNumSegurancas() {
		return numSegurancas;
	}

	/** retorna a lista de salas do local de aplicação de prova. 
	 * @return
	 */
	public List<Sala> getSalas() {
		return salas;
	}

	/** Retorna o telefone do contato do local de aplicação de prova. 
	 * @return
	 */
	public String getTelefone() {
		return telefone;
	}

	/** Inicializa o endereço de contato, setando valores padrões para pais (Brasil), UF (RN) e município (NATAL).
	 * 
	 */
	private void inicializaEndereco() {
		endereco = new Endereco();
		endereco.setPais(new Pais(Pais.BRASIL));
		endereco.setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		endereco.setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
		endereco.setTipoLogradouro(new TipoLogradouro(TipoLogradouro.RUA));
	}

	/** Seta a capacidade ideal total de candidatos no local de aplicação de prova. 
	 * @param capacidadeIdealTotal
	 */
	public void setCapacidadeIdealTotal(int capacidadeIdealTotal) {
		this.capacidadeIdealTotal = capacidadeIdealTotal;
	}

	/** Seta o celular do contato do local de aplicação de prova. 
	 * @param celular
	 */
	public void setCelular(String celular) {
		this.celular = celular;
	}

	/** Seta o DDD do celular do contato do local de aplicação de prova. 
	 * @param codigoAreaNacionalTelefoneCelular
	 */
	public void setCodigoAreaNacionalTelefoneCelular(
			Short codigoAreaNacionalTelefoneCelular) {
		this.codigoAreaNacionalTelefoneCelular = codigoAreaNacionalTelefoneCelular;
	}

	/** Seta o DDD do telefone do contato do local de aplicação de prova. 
	 * @param codigoAreaNacionalTelefoneFixo
	 */
	public void setCodigoAreaNacionalTelefoneFixo(
			Short codigoAreaNacionalTelefoneFixo) {
		this.codigoAreaNacionalTelefoneFixo = codigoAreaNacionalTelefoneFixo;
	}

	/** Seta o nome do contato no local de aplicação de prova. 
	 * @param contato
	 */
	public void setContato(String contato) {
		this.contato = contato;
	}

	/** Seta o e-mail do contato no local de aplicação de prova. 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/** Seta o endereço do local de aplicação de prova. 
	 * @param endereco
	 */
	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	/**
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta o nome do local de aplicação de prova. 
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Seta a quantidade de pessoas de apoio. 
	 * @param numApoio
	 */
	public void setNumApoio(int numApoio) {
		this.numApoio = numApoio;
	}

	/** Seta a quantidade de fiscais para trabalhar nos corredores. 
	 * @param numFiscaisCorredor
	 */
	public void setNumFiscaisCorredor(int numFiscaisCorredor) {
		this.numFiscaisCorredor = numFiscaisCorredor;
	}

	/** Seta a quantidade de fiscais para trabalhar em salas. 
	 * @param numFiscaisSala
	 */
	public void setNumFiscaisSala(int numFiscaisSala) {
		this.numFiscaisSala = numFiscaisSala;
	}

	/** Seta a quantidade de seguranças de apoio. 
	 * @param numSegurancas
	 */
	public void setNumSegurancas(int numSegurancas) {
		this.numSegurancas = numSegurancas;
	}

	/** Seta a lista de salas do local de aplicação de prova. 
	 * @param salas
	 */
	public void setSalas(List<Sala> salas) {
		this.salas = salas;
	}

	/** Seta o telefone do contato do local de aplicação de prova. 
	 * @param telefone
	 */
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	/** Valida os dados: nome do local de aplicação de prova; CEP, logradouro, número e bairro, do endereço do local de aplicação de prova.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(this.nome, "Nome do local", lista);
		ValidatorUtil.validateRequired(this.endereco.getCep(), "CEP", lista);
		ValidatorUtil.validateRequired(this.endereco.getLogradouro(), "Logradouro", lista);
		ValidatorUtil.validateRequired(this.endereco.getNumero(), "N.°", lista);
		ValidatorUtil.validateRequired(this.endereco.getBairro(), "Bairro",	lista);
		return lista;
	}

	/** Compara este objeto com outro, comparando nomes iguais.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(LocalAplicacaoProva l) {
		if (nome != null && l.getNome() != null) {
			return nome.compareTo(l.getNome());
		} else {
			return 0;
		}
	}
	
	/** Compara se este objeto é igual a outro.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LocalAplicacaoProva) {
			LocalAplicacaoProva other = (LocalAplicacaoProva) obj;
			if (this.id == other.id)
				return true;
		}
		return false;
	}

	/** Retorna uma string com o nome local de aplicação de prova e o bairro do endereço.
	 * @return
	 */
	@Transient
	public String getNomeBairro() {
		StringBuilder str = new StringBuilder(nome);
		if (endereco != null && endereco.getBairro() != null)
			str.append(" - " + endereco.getBairro());
		return str.toString();
	}
	
	/** Retorna uma representação textual do local de aplicação de prova no formato:
	 *  ID, seguido de vírgula, seguido do nome do local.
	 */
	@Override
	public String toString() {
		return getId() + ", " + getNome();
	}

	/** Retorna um código hash associado a este objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, nome);
	}

}
