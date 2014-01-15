package br.ufrn.sigaa.ensino_rede.resources;

public class DocenteRedeDTO {
	private int id;
	private String cpf;
	private String nome;
	private String email;
	private Long dataNascimento; 
	private String sexo;
	private String municipioNaturalidade;
	private String ufNaturalidade;
	private String numeroIdentidade;
	private String orgaoIdentidade;
	private String ufIdentidade;
	private Long dataExpedicaoIdentidade;
	private String telefoneFixo;
	private String telefoneCelular;
	private String endereco;
	private String ies;
	private String campus;
	private String curso;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Long dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getMunicipioNaturalidade() {
		return municipioNaturalidade;
	}
	public void setMunicipioNaturalidade(String municipioNaturalidade) {
		this.municipioNaturalidade = municipioNaturalidade;
	}
	public String getUfNaturalidade() {
		return ufNaturalidade;
	}
	public void setUfNaturalidade(String ufNaturalidade) {
		this.ufNaturalidade = ufNaturalidade;
	}
	public String getNumeroIdentidade() {
		return numeroIdentidade;
	}
	public void setNumeroIdentidade(String numeroIdentidade) {
		this.numeroIdentidade = numeroIdentidade;
	}
	public String getOrgaoIdentidade() {
		return orgaoIdentidade;
	}
	public void setOrgaoIdentidade(String orgaoIdentidade) {
		this.orgaoIdentidade = orgaoIdentidade;
	}
	public String getUfIdentidade() {
		return ufIdentidade;
	}
	public void setUfIdentidade(String ufIdentidade) {
		this.ufIdentidade = ufIdentidade;
	}
	public Long getDataExpedicaoIdentidade() {
		return dataExpedicaoIdentidade;
	}
	public void setDataExpedicaoIdentidade(Long dataExpedicaoIdentidade) {
		this.dataExpedicaoIdentidade = dataExpedicaoIdentidade;
	}
	public String getTelefoneFixo() {
		return telefoneFixo;
	}
	public void setTelefoneFixo(String telefoneFixo) {
		this.telefoneFixo = telefoneFixo;
	}
	public String getTelefoneCelular() {
		return telefoneCelular;
	}
	public void setTelefoneCelular(String telefoneCelular) {
		this.telefoneCelular = telefoneCelular;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getIes() {
		return ies;
	}
	public void setIes(String ies) {
		this.ies = ies;
	}
	public String getCampus() {
		return campus;
	}
	public void setCampus(String campus) {
		this.campus = campus;
	}
	public String getCurso() {
		return curso;
	}
	public void setCurso(String curso) {
		this.curso = curso;
	}
}
