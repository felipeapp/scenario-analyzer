/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2010
 */
package br.ufrn.ambientes.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.annotations.Required;
import br.ufrn.comum.dominio.PessoaGeral;

/**
 * Representa um usuário que pode acessar um determinado 
 * tipo de ambiente.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Entity @Table(name="usuario_ambiente", schema="comum")
public class UsuarioAmbiente implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="comum.usuario_ambiente_seq") })
	private int id;
	
	/** Login do usuário */
	@Required
	private String login;
	
	/** Senha do usuário */
	@Required
	private String senha;
	
	/** Instituição à qual pertence o usuário */
	@Required
	private String instituicao;

	/** Se o usuário está ativo ou não */
	@CampoAtivo
	private boolean ativo;
	
	/** Pessoa associada ao usuário */
	@Required @Column(name="id_pessoa")
	private int idPessoa;
	
	@Transient
	private PessoaGeral pessoa;
	
	/** Tipo de ambiente que o usuário tem permissão de acessar */
	@ManyToOne @JoinColumn(name="id_tipo_ambiente")
	@Required
	private TipoAmbiente ambiente;

	@Transient
	private RegistroEntradaAmbiente entradaAmbiente;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(String instituicao) {
		this.instituicao = instituicao;
	}

	public int getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(int idPessoa) {
		this.idPessoa = idPessoa;
	}

	public PessoaGeral getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaGeral pessoa) {
		this.pessoa = pessoa;
		this.idPessoa = pessoa.getId();
	}

	public TipoAmbiente getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(TipoAmbiente ambiente) {
		this.ambiente = ambiente;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RegistroEntradaAmbiente getEntradaAmbiente() {
		return entradaAmbiente;
	}

	public void setEntradaAmbiente(RegistroEntradaAmbiente entradaAmbiente) {
		this.entradaAmbiente = entradaAmbiente;
	}
	
}
