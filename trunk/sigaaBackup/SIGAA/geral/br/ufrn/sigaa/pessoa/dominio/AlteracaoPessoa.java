/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on May 21, 2007
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroAcessoPublico;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Entidade para manter o histórico das informações alteradas da classe Pessoa.
 * Contém a referencia para a pessoa que foi alterada, quem realizou a alteração e todos os dados que ela possuía antes da alteração
 * @see br.ufrn.sigaa.dominio.Pessoa
 * @author Victor Hugo
 */
@Entity
@Table(schema = "comum", name = "alteracao_pessoa", uniqueConstraints = {})
public class AlteracaoPessoa implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_alteracao_pessoa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
	 * pessoa atual, a pessoa que foi alterada
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa", unique = false, nullable = true, insertable = true, updatable = true)
	private Pessoa pessoa;

	/**
	 * registro entrada de quem alterou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroEntrada;

	/**
	 * registro de acesso público de quem alterou
	 */
	@ManyToOne(fetch = FetchType.LAZY)	
	@JoinColumn(name = "id_registro_publico")
	private RegistroAcessoPublico registroPublico;	
	
	/**
	 * data da alteração
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	@Column(name = "nome")
	private String nome;

	@Column(name = "nome_ascii")
	private String nomeAscii;
	
	@Column(name = "cpf_cnpj")
	private Long cpf_cnpj;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_nascimento")
	private Date dataNascimento;

	@Column(name = "sexo")
	private char sexo;

	@Column(name = "passaporte")
	private String passaporte;

	@Column(name = "telefone")
	private String telefone;

	@Column(name = "celular")
	private String celular;

	@Column(name = "codigo_area_nacional_telefone_fixo")
	private Short codigoAreaNacionalTelefoneFixo;

	@Column(name = "codigo_area_nacional_telefone_celular")
	private Short codigoAreaNacionalTelefoneCelular;

	@Column(name = "fax")
	private String fax;

	/**
	 * Atributos de GrauFormacao
	 */
	@Column(name = "id_grau_formacao")
	private int idGrauFormacao;

	@Column(name = "abreviatura")
	private String abreviatura;

	@Column(name = "nome_pai")
	private String nomePai;

	@Column(name = "nome_mae")
	private String nomeMae;

	@Column(name = "email")
	private String email;

	@Column(name = "municipio_naturalidade_outro")
	private String municipioNaturalidadeOutro;

	@Column(name = "outro_documento")
	private String outroDocumento;

	@Column(name = "endereco")
	private String endereco;

	@Column(name = "valido")
	private boolean valido;

	@Temporal(TemporalType.DATE)
	@Column(name = "ultima_atualizacao")
	private Date ultimaAtualizacao;

	@Column(name = "funcionario")
	private boolean funcionario;

	private Character origem;

	/**
	 * Atributos de contaBancaria
	 */
	@Column(name = "conta_bancaria_numero")
	private String contaBancariaNumero;

	@Column(name = "conta_bancaria_agencia")
	private String contaBancariaAgencia;

	@Column(name = "conta_bancaria_id_banco")
	private int contaBancariaIdBanco;

	@Column(name = "conta_bancaria_variacao")
	private String contaBancariaVariacao;
	
	@Column(name = "conta_bancaria_operacao")
	private String contaBancariaOperacao;


	/**
	 * Atributos de CertificadoMilitar
	 */
	@Column(name = "certificado_militar_numero")
	private String certificadoMilitarNumero;

	@Column(name = "certificado_militar_serie")
	private String certificadoMilitarSerie;

	@Column(name = "certificado_militar_categoria")
	private String certificadoMilitarCategoria;

	@Column(name = "certificado_militar_orgao_expedicao")
	private String certificadoMilitarOrgaoExpedicao;

	@Temporal(TemporalType.DATE)
	@Column(name = "certificado_militar_data_expedicao")
	private Date certificadoMilitarDataExpedicao;

	/**
	 * Atributos de Municipio
	 */
	@Column(name = "id_municipio_naturalidade")
	private int idMunicipioNaturalidade;

	/**
	 * Atributos de pais
	 */
	@Column(name = "id_pais_nacionalidade")
	private int idPais;

	/**
	 * Atributos de tipoEtnia
	 */
	@Column(name = "id_tipo_etnia")
	private int idTipoEtnia;

	/**
	 * Atributos de tipoRedeEnsino
	 */
	@Column(name = "id_tipo_rede_ensino")
	private int idTipoRedeEnsino;

	/**
	 * Atributos de estadoCivil
	 */
	@Column(name = "id_estado_civil")
	private int idEstadoCivil;

	/**
	 * Atributos de tituloEleitor
	 */
	@Column(name = "titulo_eleitor_id_uf")
	private int tituloEleitorIdUf;

	@Column(name = "titulo_eleitor_numero")
	private String tituloEleitorNumero;

	@Column(name = "titulo_eleitor_zona")
	private String tituloEleitorZona;

	@Column(name = "titulo_eleitor_secao")
	private String tituloEleitorSecao;

	@Temporal(TemporalType.DATE)
	@Column(name = "titulo_eleitor_data_expedicao")
	private Date tituloEleitorDataExpedicao;

	/**
	 * atributos de identidade
	 */
	@Column(name = "identidade_uf")
	private int identidadeIdUf;

	@Column(name = "identidade_numero")
	private String identidadeNumero;

	@Column(name = "identidade_orgao_expedicao")
	private String identidadeOrgaoExpedicao;

	@Temporal(TemporalType.DATE)
	@Column(name = "identidade_data_expedicao")
	private Date identidadeDataExpedicao;

	/**
	 * Atributos de tipoRaca
	 */
	@Column(name = "id_tipo_raca")
	private int idTipoRaca;

	/**
	 * Atributos de unidadeFederativa
	 */
	@Column(name = "id_uf_naturalidade")
	private int idUfNaturalidade;


	/**
	 * Atributos de enderecoContato
	 */
	@Column(name = "endereco_contato_id_pais")
	private int enderecoContatoIdPais;

	@Column(name = "endereco_contato_id_uf")
	private int enderecoContatoIdUf;

	@Column(name = "endereco_contato_id_municipio")
	private int enderecoContatoIdMunicipio;

	@Column(name = "endereco_contato_logradouro")
	private String enderecoContatoLogradouro;

	@Column(name = "endereco_contato_numero")
	private String enderecoContatoNumero;

	@Column(name = "endereco_contato_complemento")
	private String enderecoContatoComplemento;

	@Column(name = "endereco_contato_cep")
	private String enderecoContatoCep;

	@Column(name = "endereco_contato_municipio_outro")
	private String enderecoContatoMunicipioOutro;

	@Column(name = "endereco_contato_caixa_postal")
	private String enderecoContatoCaixaPostal;

	@Column(name = "endereco_contato_bairro")
	private String enderecoContatoBairro;

	@Column(name = "endereco_contato_nome_pais")
	private String enderecoContatoNomePais;

	@Column(name = "endereco_contato_correspondencia")
	private Boolean enderecoContatoCorrespondencia;

	//telefone Fixo
	@Column(name = "endereco_contato_telefone_fixo_codigo_area_nacional")
	private int enderecoContatoTelefoneFixoCodigoAreaNacional;

	@Column(name = "endereco_contato_telefone_fixo_codigo_area_internacional")
	private Integer enderecoContatoTelefoneFixoCodigoAreaInternacional;

	@Column(name = "endereco_contato_telefone_fixo_ramal")
	private String enderecoContatoTelefoneFixoRamal;

	/** telefone Celular */
	@Column(name = "endereco_contato_telefone_celular_codigo_area_nacional")
	private int enderecoContatoTelefoneCelularCodigoAreaNacional;

	@Column(name = "endereco_contato_telefone_celular_codigo_area_internacional")
	private Integer enderecoContatoTelefoneCelularCodigoAreaInternacional;

	@Column(name = "endereco_contato_telefone_celular_ramal")
	private String enderecoContatoTelefoneCelularRamal;

	@Column(name = "cod_comando")
	private Integer codComando;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getAbreviatura() {
		return abreviatura;
	}

	public void setAbreviatura(String abreviatura) {
		this.abreviatura = abreviatura;
	}

	public String getCertificadoMilitarCategoria() {
		return certificadoMilitarCategoria;
	}

	public void setCertificadoMilitarCategoria(String certificadoMilitarCategoria) {
		this.certificadoMilitarCategoria = certificadoMilitarCategoria;
	}

	public Date getCertificadoMilitarDataExpedicao() {
		return certificadoMilitarDataExpedicao;
	}

	public void setCertificadoMilitarDataExpedicao(
			Date certificadoMilitarDataExpedicao) {
		this.certificadoMilitarDataExpedicao = certificadoMilitarDataExpedicao;
	}

	public String getCertificadoMilitarNumero() {
		return certificadoMilitarNumero;
	}

	public void setCertificadoMilitarNumero(String certificadoMilitarNumero) {
		this.certificadoMilitarNumero = certificadoMilitarNumero;
	}

	public String getCertificadoMilitarOrgaoExpedicao() {
		return certificadoMilitarOrgaoExpedicao;
	}

	public void setCertificadoMilitarOrgaoExpedicao(
			String certificadoMilitarOrgaoExpedicao) {
		this.certificadoMilitarOrgaoExpedicao = certificadoMilitarOrgaoExpedicao;
	}

	public String getCertificadoMilitarSerie() {
		return certificadoMilitarSerie;
	}

	public void setCertificadoMilitarSerie(String certificadoMilitarSerie) {
		this.certificadoMilitarSerie = certificadoMilitarSerie;
	}

	public String getContaBancariaAgencia() {
		return contaBancariaAgencia;
	}

	public void setContaBancariaAgencia(String contaBancariaAgencia) {
		this.contaBancariaAgencia = contaBancariaAgencia;
	}

	public int getContaBancariaIdBanco() {
		return contaBancariaIdBanco;
	}

	public void setContaBancariaIdBanco(int contaBancariaIdBanco) {
		this.contaBancariaIdBanco = contaBancariaIdBanco;
	}

	public String getContaBancariaNumero() {
		return contaBancariaNumero;
	}

	public void setContaBancariaNumero(String contaBancariaNumero) {
		this.contaBancariaNumero = contaBancariaNumero;
	}

	public String getContaBancariaVariacao() {
		return contaBancariaVariacao;
	}

	public void setContaBancariaVariacao(String contaBancariaVariacao) {
		this.contaBancariaVariacao = contaBancariaVariacao;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getEnderecoContatoBairro() {
		return enderecoContatoBairro;
	}

	public void setEnderecoContatoBairro(String enderecoContatoBairro) {
		this.enderecoContatoBairro = enderecoContatoBairro;
	}

	public String getEnderecoContatoCaixaPostal() {
		return enderecoContatoCaixaPostal;
	}

	public void setEnderecoContatoCaixaPostal(String enderecoContatoCaixaPostal) {
		this.enderecoContatoCaixaPostal = enderecoContatoCaixaPostal;
	}

	public String getEnderecoContatoCep() {
		return enderecoContatoCep;
	}

	public void setEnderecoContatoCep(String enderecoContatoCep) {
		this.enderecoContatoCep = enderecoContatoCep;
	}

	public String getEnderecoContatoComplemento() {
		return enderecoContatoComplemento;
	}

	public void setEnderecoContatoComplemento(String enderecoContatoComplemento) {
		this.enderecoContatoComplemento = enderecoContatoComplemento;
	}

	public Boolean getEnderecoContatoCorrespondencia() {
		return enderecoContatoCorrespondencia;
	}

	public void setEnderecoContatoCorrespondencia(
			Boolean enderecoContatoCorrespondencia) {
		this.enderecoContatoCorrespondencia = enderecoContatoCorrespondencia;
	}

	public int getEnderecoContatoIdMunicipio() {
		return enderecoContatoIdMunicipio;
	}

	public void setEnderecoContatoIdMunicipio(int enderecoContatoIdMunicipio) {
		this.enderecoContatoIdMunicipio = enderecoContatoIdMunicipio;
	}

	public int getEnderecoContatoIdPais() {
		return enderecoContatoIdPais;
	}

	public void setEnderecoContatoIdPais(int enderecoContatoIdPais) {
		this.enderecoContatoIdPais = enderecoContatoIdPais;
	}

	public int getEnderecoContatoIdUf() {
		return enderecoContatoIdUf;
	}

	public void setEnderecoContatoIdUf(int enderecoContatoIdUf) {
		this.enderecoContatoIdUf = enderecoContatoIdUf;
	}

	public String getEnderecoContatoLogradouro() {
		return enderecoContatoLogradouro;
	}

	public void setEnderecoContatoLogradouro(String enderecoContatoLogradouro) {
		this.enderecoContatoLogradouro = enderecoContatoLogradouro;
	}

	public String getEnderecoContatoMunicipioOutro() {
		return enderecoContatoMunicipioOutro;
	}

	public void setEnderecoContatoMunicipioOutro(
			String enderecoContatoMunicipioOutro) {
		this.enderecoContatoMunicipioOutro = enderecoContatoMunicipioOutro;
	}

	public String getEnderecoContatoNomePais() {
		return enderecoContatoNomePais;
	}

	public void setEnderecoContatoNomePais(String enderecoContatoNomePais) {
		this.enderecoContatoNomePais = enderecoContatoNomePais;
	}

	public String getEnderecoContatoNumero() {
		return enderecoContatoNumero;
	}

	public void setEnderecoContatoNumero(String enderecoContatoNumero) {
		this.enderecoContatoNumero = enderecoContatoNumero;
	}

	public Integer getEnderecoContatoTelefoneCelularCodigoAreaInternacional() {
		return enderecoContatoTelefoneCelularCodigoAreaInternacional;
	}

	public void setEnderecoContatoTelefoneCelularCodigoAreaInternacional(
			Integer enderecoContatoTelefoneCelularCodigoAreaInternacional) {
		this.enderecoContatoTelefoneCelularCodigoAreaInternacional = enderecoContatoTelefoneCelularCodigoAreaInternacional;
	}

	public int getEnderecoContatoTelefoneCelularCodigoAreaNacional() {
		return enderecoContatoTelefoneCelularCodigoAreaNacional;
	}

	public void setEnderecoContatoTelefoneCelularCodigoAreaNacional(
			int enderecoContatoTelefoneCelularCodigoAreaNacional) {
		this.enderecoContatoTelefoneCelularCodigoAreaNacional = enderecoContatoTelefoneCelularCodigoAreaNacional;
	}

	public String getEnderecoContatoTelefoneCelularRamal() {
		return enderecoContatoTelefoneCelularRamal;
	}

	public void setEnderecoContatoTelefoneCelularRamal(
			String enderecoContatoTelefoneCelularRamal) {
		this.enderecoContatoTelefoneCelularRamal = enderecoContatoTelefoneCelularRamal;
	}

	public Integer getEnderecoContatoTelefoneFixoCodigoAreaInternacional() {
		return enderecoContatoTelefoneFixoCodigoAreaInternacional;
	}

	public void setEnderecoContatoTelefoneFixoCodigoAreaInternacional(
			Integer enderecoContatoTelefoneFixoCodigoAreaInternacional) {
		this.enderecoContatoTelefoneFixoCodigoAreaInternacional = enderecoContatoTelefoneFixoCodigoAreaInternacional;
	}

	public int getEnderecoContatoTelefoneFixoCodigoAreaNacional() {
		return enderecoContatoTelefoneFixoCodigoAreaNacional;
	}

	public void setEnderecoContatoTelefoneFixoCodigoAreaNacional(
			int enderecoContatoTelefoneFixoCodigoAreaNacional) {
		this.enderecoContatoTelefoneFixoCodigoAreaNacional = enderecoContatoTelefoneFixoCodigoAreaNacional;
	}

	public String getEnderecoContatoTelefoneFixoRamal() {
		return enderecoContatoTelefoneFixoRamal;
	}

	public void setEnderecoContatoTelefoneFixoRamal(
			String enderecoContatoTelefoneFixoRamal) {
		this.enderecoContatoTelefoneFixoRamal = enderecoContatoTelefoneFixoRamal;
	}

	public boolean isFuncionario() {
		return funcionario;
	}

	public void setFuncionario(boolean funcionario) {
		this.funcionario = funcionario;
	}

	public Date getIdentidadeDataExpedicao() {
		return identidadeDataExpedicao;
	}

	public void setIdentidadeDataExpedicao(Date identidadeDataExpedicao) {
		this.identidadeDataExpedicao = identidadeDataExpedicao;
	}

	public int getIdentidadeIdUf() {
		return identidadeIdUf;
	}

	public void setIdentidadeIdUf(int identidadeIdUf) {
		this.identidadeIdUf = identidadeIdUf;
	}

	public String getIdentidadeNumero() {
		return identidadeNumero;
	}

	public void setIdentidadeNumero(String identidadeNumero) {
		this.identidadeNumero = identidadeNumero;
	}

	public String getIdentidadeOrgaoExpedicao() {
		return identidadeOrgaoExpedicao;
	}

	public void setIdentidadeOrgaoExpedicao(String identidadeOrgaoExpedicao) {
		this.identidadeOrgaoExpedicao = identidadeOrgaoExpedicao;
	}

	public int getIdEstadoCivil() {
		return idEstadoCivil;
	}

	public void setIdEstadoCivil(int idEstadoCivil) {
		this.idEstadoCivil = idEstadoCivil;
	}

	public int getIdGrauFormacao() {
		return idGrauFormacao;
	}

	public void setIdGrauFormacao(int idGrauFormacao) {
		this.idGrauFormacao = idGrauFormacao;
	}

	public int getIdMunicipioNaturalidade() {
		return idMunicipioNaturalidade;
	}

	public void setIdMunicipioNaturalidade(int idMunicipioNaturalidade) {
		this.idMunicipioNaturalidade = idMunicipioNaturalidade;
	}

	public int getIdTipoRaca() {
		return idTipoRaca;
	}

	public void setIdTipoRaca(int idTipoRaca) {
		this.idTipoRaca = idTipoRaca;
	}

	public int getIdUfNaturalidade() {
		return idUfNaturalidade;
	}

	public void setIdUfNaturalidade(int idUfNaturalidade) {
		this.idUfNaturalidade = idUfNaturalidade;
	}

	public int getIdPais() {
		return idPais;
	}

	public void setIdPais(int idPais) {
		this.idPais = idPais;
	}

	public int getIdTipoEtnia() {
		return idTipoEtnia;
	}

	public void setIdTipoEtnia(int idTipoEtnia) {
		this.idTipoEtnia = idTipoEtnia;
	}

	public int getIdTipoRedeEnsino() {
		return idTipoRedeEnsino;
	}

	public void setIdTipoRedeEnsino(int idTipoRedeEnsino) {
		this.idTipoRedeEnsino = idTipoRedeEnsino;
	}

	public String getMunicipioNaturalidadeOutro() {
		return municipioNaturalidadeOutro;
	}

	public void setMunicipioNaturalidadeOutro(String municipioNaturalidadeOutro) {
		this.municipioNaturalidadeOutro = municipioNaturalidadeOutro;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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

	public String getOutroDocumento() {
		return outroDocumento;
	}

	public void setOutroDocumento(String outroDocumento) {
		this.outroDocumento = outroDocumento;
	}

	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	public char getSexo() {
		return sexo;
	}

	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	public Date getTituloEleitorDataExpedicao() {
		return tituloEleitorDataExpedicao;
	}

	public void setTituloEleitorDataExpedicao(Date tituloEleitorDataExpedicao) {
		this.tituloEleitorDataExpedicao = tituloEleitorDataExpedicao;
	}

	public int getTituloEleitorIdUf() {
		return tituloEleitorIdUf;
	}

	public void setTituloEleitorIdUf(int tituloEleitorIdUf) {
		this.tituloEleitorIdUf = tituloEleitorIdUf;
	}

	public String getTituloEleitorNumero() {
		return tituloEleitorNumero;
	}

	public void setTituloEleitorNumero(String tituloEleitorNumero) {
		this.tituloEleitorNumero = tituloEleitorNumero;
	}

	public String getTituloEleitorSecao() {
		return tituloEleitorSecao;
	}

	public void setTituloEleitorSecao(String tituloEleitorSecao) {
		this.tituloEleitorSecao = tituloEleitorSecao;
	}

	public String getTituloEleitorZona() {
		return tituloEleitorZona;
	}

	public void setTituloEleitorZona(String tituloEleitorZona) {
		this.tituloEleitorZona = tituloEleitorZona;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	public boolean isValido() {
		return valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
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

	public Character getOrigem() {
		return origem;
	}

	public void setOrigem(Character origem) {
		this.origem = origem;
	}

	public Long getCpf_cnpj() {
		return cpf_cnpj;
	}

	public void setCpf_cnpj(Long cpf_cnpj) {
		this.cpf_cnpj = cpf_cnpj;
	}

	public Short getCodigoAreaNacionalTelefoneFixo() {
		return codigoAreaNacionalTelefoneFixo;
	}

	public void setCodigoAreaNacionalTelefoneFixo(
			Short codigoAreaNacionalTelefoneFixo) {
		this.codigoAreaNacionalTelefoneFixo = codigoAreaNacionalTelefoneFixo;
	}

	public Short getCodigoAreaNacionalTelefoneCelular() {
		return codigoAreaNacionalTelefoneCelular;
	}

	public void setCodigoAreaNacionalTelefoneCelular(
			Short codigoAreaNacionalTelefoneCelular) {
		this.codigoAreaNacionalTelefoneCelular = codigoAreaNacionalTelefoneCelular;
	}

	public RegistroAcessoPublico getRegistroPublico() {
		return registroPublico;
	}

	public void setRegistroPublico(RegistroAcessoPublico registroPublico) {
		this.registroPublico = registroPublico;
	}

	public Integer getCodComando() {
		return codComando;
	}

	public void setCodComando(Integer codComando) {
		this.codComando = codComando;
	}

	public String getNomeAscii() {
		return nomeAscii;
	}

	public void setNomeAscii(String nomeAscii) {
		this.nomeAscii = nomeAscii;
	}

	public String getContaBancariaOperacao() {
		return contaBancariaOperacao;
	}

	public void setContaBancariaOperacao(String contaBancariaOperacao) {
		this.contaBancariaOperacao = contaBancariaOperacao;
	}
	
}
