/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Data de Criação: 19/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object utilizado para a integração da autenticação 
 * dos sistemas com outros mecanismos de autenticação. Devido à necessidade
 * de existência dos usuários no banco de dados, mesmo que a autenticação
 * não seja feita via banco, os usuários são cadastrados sob demanda
 * no banco de dados, ou seja, a cada autenticação, verifica-se
 * se o usuário existe no banco. Se não existir, cadastra um novo. 
 *
 * Essa classe é utilizada para trazer as informações essenciais do
 * usuário do mecanismo de autenticação utilizado para que os usuários
 * possam ser cadastrados no banco de dados.
 * 
 * @author Rafael Moreira, David Pereira
 *
 */
public class UsuarioDTO implements Serializable {

	private static final long serialVersionUID = -1L;
	
	/** Id do usuário */
	private Integer id;
	
	/** Login do usuário */
	private String login;
	
	/** Senha do usuário */
	private String senha;
	
	/** Id da pessoa associada ao usuário */
	private Integer idPessoa;
	
	/** Nome da pessoa associada ao usuário */
	private String nome;
	
	/** Nome Completo da pessoa relacionada ao usuário */
	private String nomeCompleto;
	
	/** Id do registro de entrada associado ao usuário */
	private Integer idRegistroEntrada;
	
	/** Id do tipo de usuário */
	private Integer idTipoUsuario;
	
	/** CPF ou CNPJ da pessoa associada ao usuário */
	private Long cpfCnpj;
	
	/** E-Mail do usuário */
	private String email;
	
	/** Ramal do usuário. */
	private String ramal;
	
	/** Matrícula Siape do servidor associado ao usuário */
	private Integer siape;
	
	/** Id da unidade do usuário */
	private Integer idUnidade;
	
	/** Código SIAPECAD da unidade do usuário */
	private Long codigoUnidade;

	/** Se o usuário está inativo ou não */
	private boolean inativo;
	
	/** Lista de permissões do usuário */
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
