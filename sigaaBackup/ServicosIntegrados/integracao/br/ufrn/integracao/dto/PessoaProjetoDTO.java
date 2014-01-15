package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

public class PessoaProjetoDTO implements Serializable {

	/**Matricula do aluno.*/
	private Long matricula;
	
	/**Siape do servidor.*/
	private Integer siape;
	
	/** Identificador */
	private int id;

	/** Endereço da pessoa/empresa */
	private String endereco;

	/** CEP da pessoa/empresa */
	private String CEP;

	/** Unidade Federativa da pessoa/empresa */
	private String UF;

	/** Telefone da pessoa/empresa */
	private String telefone;

	/** Celular da pessoa */
	private String celular;

	/** FAX da pessoa */
	private String fax;

	/** 
	 * Tipo de pessoa. F - Física; J - Jurídica; P - Pensionistas que compartilham o mesmo número de 
	 * CPF que o Servidor Instituidor. Este caso ocorre para beneficiárias de pensão de servidores 
	 * muito antigos, devido à antigamente existir a possibilidade da esposa ter o mesmo CPF do 
	 * marido em caso de dependência econômica.
	 */
	private char tipo;


	/** CPF ou CNPJ da pessoa */
	private Long cpf_cnpj;
	
	/** Complemento do endereço residencial da pessoa */
	private String enderecoComplemento;

	/** Em caso de pessoa jurídica, nome do representante da empresa */
	private String nomeRepresentante;
	
	/** Nome da pessoa ou razão social em caso de empresa */
	private String nome;

	/** Bairro da pessoa/empresa */
	private String bairro;
	
	/** Cidade da pessoa/empresa */
	private String cidade;

	/** Em caso de pessoa jurídica, nome fantasia da empresa */
	private String nomeFantasia;

	/** Data de nascimento da pessoa física */
	private Date dataNascimento;

	/** Sexo da pessoa. M - Masculino; F - Feminino. */
	private char sexo;

	/** E-Mail da pessoa/empresa */
	private String email;

	/** Se a pessoa é funcionária da universidade */
	private Boolean funcionario;
	
	/** NIT/PIS da pessoa física */
	private Long nitPIS;

	/**
	 * Para pessoa jurídica. Atributo utilizado pelo subsistema de faturas para indicar quando um
	 * fornecedor pode ou não emitir uma fatura.
	 */
	private boolean emiteFatura = false;
	
	/**
	 * Para pessoa jurídica. Alíquota de imposto a ser descontado no valor bruto das faturas.
	 */
	private Double aliquotaImposto = null;

	/**
	 * Atributo utilizado pelo subsistema de convênios para indicar quando um
	 * fornecedor é internacional e não possui cnpj.
	 */
	private boolean internacional = false;

	/** Número do passaporte, para o caso de estrangeiros */
	private String passaporte;

	/** Pais de origem de uma pessoa estrangeira* */
	private String paisOrigem;

	/** Informa se a pessoa tem CPF ou CNPJ válido.. usado para restringir a busca de pessoas migradas erradas */
	private boolean valido;

	/** Em caso de pessoa física, nome da mãe da pessoa. */
	private String nomeMae;

	/** Em caso de pessoa física, nome do pai da pessoa. */
	private String nomePai;
	
	/** Órgão expedidor do RG (identidade) da pessoa*/
	private String rgOrgaoExpedidor;
	
	/** Data da expedição do RG */
	private Date rgDataExpedicao;

	/** Identidade: Registro Geral */
	private String registroGeral;

	private Integer idPerfil;
	
	/** Nome da pessoa ou razão social em caso de empresa em ASCII, para auxiliar consultas */
	private String nomeAscii;
	
	/**
	 * Dados da CNH (Carteira Nacional de Habilitação)
	 */

	/** Número da CNH (Carteira Nacional de Habilitação) da pessoa */
	private String cnhNumero;
	
	/** Registro da CNH (Carteira Nacional de Habilitação) da pessoa */
	private String cnhRegistro;
	
	/** Estado em que a CNH (Carteira Nacional de Habilitação) da pessoa foi emitida */
	private String cnhUf;
	
	/** Data em que a primeira da CNH (Carteira Nacional de Habilitação) da pessoa foi emitida */
	private Date cnhDataPrimeiraHabilitacao;
	
	/** Data de expedição da CNH (Carteira Nacional de Habilitação) da pessoa */
	private Date cnhDataExpedicao;
	
	/** Data de validade da CNH (Carteira Nacional de Habilitação) da pessoa */
	private Date cnhDataValidade;
	
	/** Categoria da CNH (Carteira Nacional de Habilitação) da pessoa */
	private Integer idCnhCategoria;
	
	/** Categoria da CNH (Carteira Nacional de Habilitação) da pessoa */
	private String cnhCategoria;
	
	private Integer idFormacao;
	
	private String formacao;
	
	/** Número da casa/apto associado ao logradouro */
	private String enderecoNumero;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getCEP() {
		return CEP;
	}

	public void setCEP(String cEP) {
		CEP = cEP;
	}

	public String getUF() {
		return UF;
	}

	public void setUF(String uF) {
		UF = uF;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	public Long getCpf_cnpj() {
		return cpf_cnpj;
	}

	public void setCpf_cnpj(Long cpfCnpj) {
		cpf_cnpj = cpfCnpj;
	}

	public String getEnderecoComplemento() {
		return enderecoComplemento;
	}

	public void setEnderecoComplemento(String enderecoComplemento) {
		this.enderecoComplemento = enderecoComplemento;
	}

	public String getNomeRepresentante() {
		return nomeRepresentante;
	}

	public void setNomeRepresentante(String nomeRepresentante) {
		this.nomeRepresentante = nomeRepresentante;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public char getSexo() {
		return sexo;
	}

	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Boolean funcionario) {
		this.funcionario = funcionario;
	}

	public Long getNitPIS() {
		return nitPIS;
	}

	public void setNitPIS(Long nitPIS) {
		this.nitPIS = nitPIS;
	}

	public boolean isEmiteFatura() {
		return emiteFatura;
	}

	public void setEmiteFatura(boolean emiteFatura) {
		this.emiteFatura = emiteFatura;
	}

	public Double getAliquotaImposto() {
		return aliquotaImposto;
	}

	public void setAliquotaImposto(Double aliquotaImposto) {
		this.aliquotaImposto = aliquotaImposto;
	}

	public boolean isInternacional() {
		return internacional;
	}

	public void setInternacional(boolean internacional) {
		this.internacional = internacional;
	}

	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	public String getPaisOrigem() {
		return paisOrigem;
	}

	public void setPaisOrigem(String paisOrigem) {
		this.paisOrigem = paisOrigem;
	}

	public boolean isValido() {
		return valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public String getNomePai() {
		return nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	public String getRgOrgaoExpedidor() {
		return rgOrgaoExpedidor;
	}

	public void setRgOrgaoExpedidor(String rgOrgaoExpedidor) {
		this.rgOrgaoExpedidor = rgOrgaoExpedidor;
	}

	public Date getRgDataExpedicao() {
		return rgDataExpedicao;
	}

	public void setRgDataExpedicao(Date rgDataExpedicao) {
		this.rgDataExpedicao = rgDataExpedicao;
	}

	public String getRegistroGeral() {
		return registroGeral;
	}

	public void setRegistroGeral(String registroGeral) {
		this.registroGeral = registroGeral;
	}

	public Integer getIdPerfil() {
		return idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	public String getNomeAscii() {
		return nomeAscii;
	}

	public void setNomeAscii(String nomeAscii) {
		this.nomeAscii = nomeAscii;
	}

	public String getCnhNumero() {
		return cnhNumero;
	}

	public void setCnhNumero(String cnhNumero) {
		this.cnhNumero = cnhNumero;
	}

	public String getCnhRegistro() {
		return cnhRegistro;
	}

	public void setCnhRegistro(String cnhRegistro) {
		this.cnhRegistro = cnhRegistro;
	}

	public Date getCnhDataPrimeiraHabilitacao() {
		return cnhDataPrimeiraHabilitacao;
	}

	public void setCnhDataPrimeiraHabilitacao(Date cnhDataPrimeiraHabilitacao) {
		this.cnhDataPrimeiraHabilitacao = cnhDataPrimeiraHabilitacao;
	}

	public Date getCnhDataExpedicao() {
		return cnhDataExpedicao;
	}

	public void setCnhDataExpedicao(Date cnhDataExpedicao) {
		this.cnhDataExpedicao = cnhDataExpedicao;
	}

	public Date getCnhDataValidade() {
		return cnhDataValidade;
	}

	public void setCnhDataValidade(Date cnhDataValidade) {
		this.cnhDataValidade = cnhDataValidade;
	}

	public Integer getIdCnhCategoria() {
		return idCnhCategoria;
	}

	public void setIdCnhCategoria(Integer idCnhCategoria) {
		this.idCnhCategoria = idCnhCategoria;
	}

	public String getCnhCategoria() {
		return cnhCategoria;
	}

	public void setCnhCategoria(String cnhCategoria) {
		this.cnhCategoria = cnhCategoria;
	}

	public Integer getIdFormacao() {
		return idFormacao;
	}

	public void setIdFormacao(Integer idFormacao) {
		this.idFormacao = idFormacao;
	}

	public String getFormacao() {
		return formacao;
	}

	public void setFormacao(String formacao) {
		this.formacao = formacao;
	}

	public String getEnderecoNumero() {
		return enderecoNumero;
	}

	public void setEnderecoNumero(String enderecoNumero) {
		this.enderecoNumero = enderecoNumero;
	}

	public void setCnhUf(String cnhUf) {
		this.cnhUf = cnhUf;
	}

	public String getCnhUf() {
		return cnhUf;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setSiape(Integer siape) {
		this.siape = siape;
	}

	public Integer getSiape() {
		return siape;
	}
	
	

}
