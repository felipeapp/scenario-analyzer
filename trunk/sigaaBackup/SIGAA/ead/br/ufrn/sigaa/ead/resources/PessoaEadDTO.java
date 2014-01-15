package br.ufrn.sigaa.ead.resources;

import java.util.List;


public class PessoaEadDTO {
	
	private int id;
	
	private String nome;
	private long dataNascimento;
	private char sexo;
	private Long cpf;
	
	private String email;
	private String telefone;
	
	private String logradouro;
	private String numero;
	private String complemento;
	private String bairro;
	private String cidade;
	private String estado;
	private String cep;
	
	private UsuarioEadDTO usuario;
	private ServidorEadDTO servidor;
	private DocenteExternoEadDTO docenteExterno;
	private List <DocenteTurmaEadDTO> docenteTurmas;
	
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
	public long getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(long dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public char getSexo() {
		return sexo;
	}
	public void setSexo(char sexo) {
		this.sexo = sexo;
	}
	public Long getCpf() {
		return cpf;
	}
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
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
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public UsuarioEadDTO getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioEadDTO usuario) {
		this.usuario = usuario;
	}
	public ServidorEadDTO getServidor() {
		return servidor;
	}
	public void setServidor(ServidorEadDTO servidor) {
		this.servidor = servidor;
	}
	public DocenteExternoEadDTO getDocenteExterno() {
		return docenteExterno;
	}
	public void setDocenteExterno(DocenteExternoEadDTO docenteExterno) {
		this.docenteExterno = docenteExterno;
	}
	public List<DocenteTurmaEadDTO> getDocenteTurmas() {
		return docenteTurmas;
	}
	public void setDocenteTurmas(List<DocenteTurmaEadDTO> docenteTurmas) {
		this.docenteTurmas = docenteTurmas;
	}
}