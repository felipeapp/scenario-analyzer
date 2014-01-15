/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Cria��o: 19/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object utilizado para a integra��o da autentica��o 
 * dos sistemas com outros mecanismos de autentica��o. Devido � necessidade
 * de exist�ncia dos usu�rios no banco de dados, mesmo que a autentica��o
 * n�o seja feita via banco, os usu�rios s�o cadastrados sob demanda
 * no banco de dados, ou seja, a cada autentica��o, verifica-se
 * se o usu�rio existe no banco. Se n�o existir, cadastra um novo. 
 *
 * Essa classe � utilizada para trazer as informa��es essenciais do
 * usu�rio do mecanismo de autentica��o utilizado para que os usu�rios
 * possam ser cadastrados no banco de dados.
 * 
 * @author Rafael Moreira, David Pereira
 *
 */
public class UsuarioDTO implements Serializable {

	private static final long serialVersionUID = -1L;
	
	/** Id do usu�rio */
	private Integer id;
	
	/** Login do usu�rio */
	private String login;
	
	/** Senha do usu�rio */
	private String senha;
	
	/** Id da pessoa associada ao usu�rio */
	private Integer idPessoa;
	
	/** Nome da pessoa associada ao usu�rio */
	private String nome;
	
	/** Nome Completo da pessoa relacionada ao usu�rio */
	private String nomeCompleto;
	
	/** Id do registro de entrada associado ao usu�rio */
	private Integer idRegistroEntrada;
	
	/** Id do tipo de usu�rio */
	private Integer idTipoUsuario;
	
	/** CPF ou CNPJ da pessoa associada ao usu�rio */
	private Long cpfCnpj;
	
	/** E-Mail do usu�rio */
	private String email;
	
	/** Ramal do usu�rio. */
	private String ramal;
	
	/** Matr�cula Siape do servidor associado ao usu�rio */
	private Integer siape;
	
	/** Id da unidade do usu�rio */
	private Integer idUnidade;
	
	/** C�digo SIAPECAD da unidade do usu�rio */
	private Long codigoUnidade;

	/** Se o usu�rio est� inativo ou n�o */
	private boolean inativo;
	
	/** Lista de permiss�es do usu�rio */
	private List<PermissaoDTO> permissoes;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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
	
	public Integer getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(Long cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getSiape() {
		return siape;
	}

	public void setSiape(Integer siape) {
		this.siape = siape;
	}

	public Long getCodigoUnidade() {
		return codigoUnidade;
	}

	public void setCodigoUnidade(Long codigoUnidade) {
		this.codigoUnidade = codigoUnidade;
	}

	public boolean isInativo() {
		return inativo;
	}

	public void setInativo(boolean inativo) {
		this.inativo = inativo;
	}

	public List<PermissaoDTO> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<PermissaoDTO> permissoes) {
		this.permissoes = permissoes;
	}

	public Integer getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(Integer idUnidade) {
		this.idUnidade = idUnidade;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}

	public String getNomeCompleto() {
		return nomeCompleto;
	}

	public void setNomeCompleto(String nomeCompleto) {
		this.nomeCompleto = nomeCompleto;
	}

	public Integer getIdRegistroEntrada() {
		return idRegistroEntrada;
	}

	public void setIdRegistroEntrada(Integer idRegistroEntrada) {
		this.idRegistroEntrada = idRegistroEntrada;
	}
	
	public Integer getIdTipoUsuario() {
		return idTipoUsuario;
	}
	
	public void setIdTipoUsuario(Integer idTipoUsuario) {
		this.idTipoUsuario = idTipoUsuario;
	}
	
}
