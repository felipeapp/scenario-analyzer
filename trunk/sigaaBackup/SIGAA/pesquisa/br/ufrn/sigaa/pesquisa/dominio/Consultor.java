/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe representativa dos consultores responsáveis por avaliar 
 * os projetos de pesquisa e planos de trabalho, quando necessário
 * 
 * @author Ricardo Wendell
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "consultor", schema = "pesquisa", uniqueConstraints = {})
public class Consultor implements PersistDB {

	/** Prefixo utilizado antes do código do consultor */
	public static final char PREFIXO_USUARIO = 'c';
	/** Define o tamanho da senha gerada pelo sistema */
	public static final int TAMANHO_SENHA_AUTOMATICA = 8;
	/** String utilizada para ser realizado o get pela url, para acesso do consultor */
	public static final String CODIGO_CONSULTOR = "codigo";
	/** Endereço de acesso ao portal do consultor quando acesso externamente. */
	public static final String ENDERECO_ACESSO = "/acessoConsultor";
	
	/** Chave primária */
	private int id;

	/** área de conhecimento a qual o consultar está associado */
	private AreaConhecimentoCnpq areaConhecimentoCnpq = new AreaConhecimentoCnpq();

	/** Utilizado para consultores internos */
	private Servidor servidor;

	/** Nome do consultor */
	private String nome;

	/** E-mail do consultor para possibilitar o envio de notificações */
	private String email;

	/** Marca se um consultor é um funcionário da UFRN */
	private boolean interno = true;

	/** Código de acesso único para cada consultor */
	private Integer codigo;
	
	/** Senha de acesso a ser utilizada pelos consultores externos */
	private String senha;

	/** Avaliações do Projeto destinadas aos consultores */
	private Set<AvaliacaoProjeto> avaliacoesProjeto = new HashSet<AvaliacaoProjeto>(0);

	/** Url de acesso ao portal do consultor. */
	private String urlAcesso;
	
	/**
	 * quantidade de avaliações que já foram realizadas (distribuídas) para este consultor.
	 * utilizado na distribuição de projetos.
	 */
	private Integer qtdAvaliacoes = 0;

	/** Serve para indicar se o Consultor é interno ou externo */
	private boolean consultorEspecial;
	
	// Constructors

	/** default constructor */
	public Consultor() {
		servidor = new Servidor();
	}

	/** minimal constructor */
	public Consultor(int idConsultor) {
		this.id = idConsultor;
	}

	/** full constructor */
	public Consultor(int idConsultor,
			AreaConhecimentoCnpq areaConhecimentoCnpq, Servidor servidor,
			String nome, String email, boolean interno, Set<AvaliacaoProjeto> avaliacaoProjetos) {
		this.id = idConsultor;
		this.areaConhecimentoCnpq = areaConhecimentoCnpq;
		this.servidor = servidor;
		this.nome = nome;
		this.email = email;
		this.interno = interno;
		this.avaliacoesProjeto = avaliacaoProjetos;
	}

	public Consultor(int idConsultor, int qtdAvaliacoes) {
		this.id = idConsultor;
		this.qtdAvaliacoes = qtdAvaliacoes;
	}

	/** Geração da chave primária da classe Consultor */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_consultor", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idConsultor) {
		this.id = idConsultor;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_area_conhecimento_cnpq", unique = false, nullable = true, insertable = true, updatable = true)
	public AreaConhecimentoCnpq getAreaConhecimentoCnpq() {
		return this.areaConhecimentoCnpq;
	}

	public void setAreaConhecimentoCnpq(
			AreaConhecimentoCnpq areaConhecimentoCnpq) {
		this.areaConhecimentoCnpq = areaConhecimentoCnpq;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_servidor", unique = false, nullable = true, insertable = true, updatable = true)
	public Servidor getServidor() {
		return this.servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	@Column(name = "nome", unique = false, nullable = true, insertable = true, updatable = true, length = 250)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "email", unique = false, nullable = true, insertable = true, updatable = true, length = 250)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "interno", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isInterno() {
		return this.interno;
	}

	public void setInterno(boolean interno) {
		this.interno = interno;
	}

	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "consultor")
	public Set<AvaliacaoProjeto> getAvaliacoesProjeto() {
		return this.avaliacoesProjeto;
	}

	public void setAvaliacoesProjeto(Set<AvaliacaoProjeto> avaliacaoProjetos) {
		this.avaliacoesProjeto = avaliacaoProjetos;
	}

	@Column(name="qtd_avaliacoes")
	public Integer getQtdAvaliacoes() {
		return qtdAvaliacoes;
	}

	public void setQtdAvaliacoes(Integer qtdAvaliacoes) {
		this.qtdAvaliacoes = qtdAvaliacoes;
	}

	@Column(name="codigo_acesso")
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name="senha_acesso")
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	@Transient
	public String getTipo() {
		return (interno ? "INTERNO" : "EXTERNO");
	}
	
	@Transient
	public String getUrlAcesso() {
		return urlAcesso;
	}

	public void setUrlAcesso(String urlAcesso) {
		this.urlAcesso = urlAcesso;
	}

	@Override
	public boolean equals(Object obj) {
		if (getId() == 0) {
			return false;
		} else {
			return EqualsUtil.testEquals(this, obj, "id");
		}
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	@Transient
	public boolean isConsultorEspecial() {
		return consultorEspecial;
	}

	public void setConsultorEspecial(boolean consultorEspecial) {
		this.consultorEspecial = consultorEspecial;
	}

	/** 
	 * Esse método serve para incrementar a quantidade de avaliações
	 * se a quantidade for nula ele recebe o valor 1, caso contrário e
	 * incrementado a quantidade atual de avaliações.
	 */
	public void incrementarQtdAvaliacoes() {
		if (this.qtdAvaliacoes == null)
			this.qtdAvaliacoes = 1;
		else
			this.qtdAvaliacoes++;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if (interno) {
			ValidatorUtil.validateRequired(servidor, "Servidor", lista);
		} else {
			ValidatorUtil.validateRequired(nome, "Nome", lista);
		}
		
		ValidatorUtil.validateRequired(email, "Email", lista);
		
		ValidatorUtil.validateRequired(areaConhecimentoCnpq, "Área de Conhecimento", lista);

		return lista;
	}

}
