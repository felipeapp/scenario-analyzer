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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Objeto responsável pela manutenção das entidades dos campos traduzidos no sistema.
 * 
 * @author Rafael Gomes
 */

@Entity
@Table(schema = "internacionalizacao", name = "entidade_traducao")
public class EntidadeTraducao implements Validatable {

	/** Identificador */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_entidade_traducao", unique = true, nullable = false)
	private int id;
	
	/** Nome legível da Entidade do atributo traduzido.*/
	@Column
	private String nome;
	
	/** Indica o nome completo da classe responsável pela entidade do atributo traduzido. */
	@Column
	private String classe;
	
	/** Mantém o nome simplificado da classe responsável pela entidade do atributo traduzido. */
	@Column(name="nome_classe")
	private String nomeClasse;
	
	/**
	 * Lista os itens de tradução, ou seja atributos de uma entidade.
	 */
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "entidade")
	@OrderBy("atributo")
	private List<ItemTraducao> itensTraducao;
	
	/** Indica se a entidade do elemento para tradução encontra-se ativa ou não. */
	@CampoAtivo(true)
	private Boolean ativo = true;

	/** Data de cadastro. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	public List<ItemTraducao> getItensTraducao() {
		return itensTraducao;
	}

	public void setItensTraducao(List<ItemTraducao> itensTraducao) {
		this.itensTraducao = itensTraducao;
	}

	/** Registro entrada do usuário que cadastrou. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	
	/** Construtor padrão */
	public EntidadeTraducao() {}

	/**
	 * Construtor mínimo
	 * @param id
	 */
	public EntidadeTraducao(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getNomeClasse() {
		return nomeClasse;
	}

	public void setNomeClasse(String nomeClasse) {
		this.nomeClasse = nomeClasse;
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

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequired(this.nome, "Nome", erros);
		ValidatorUtil.validateRequired(this.classe, "Classe", erros);
		return erros;
	}
	
}