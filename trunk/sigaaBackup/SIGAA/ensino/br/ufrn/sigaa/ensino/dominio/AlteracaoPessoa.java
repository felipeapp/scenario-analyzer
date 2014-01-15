/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 23/02/2007
*
*/
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.dominio.GrauFormacao;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.CertificadoMilitar;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.TipoEtnia;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TipoRedeEnsino;
import br.ufrn.sigaa.pessoa.dominio.TituloEleitor;

/**
 * Entidade para manter o histórico das informações alteradas da classe Pessoa.
 * Contém a referencia para a pessoa que foi alterada, quem realizou a alteração e 
 * todos os dados que ela possuía antes da alteração
 * 
 * @author Gleydson
 */
public class AlteracaoPessoa {

	private int id;

	private RegistroEntrada alteracao;

	private int codMovimento;

	private char tipo;

	private Long cpf_cnpj;

	private CertificadoMilitar certificadoMilitar;

	private Municipio municipio = new Municipio();

	private Endereco enderecoProfissional = new Endereco();

	private Endereco enderecoResidencial = new Endereco();

	private Pais pais = new Pais();

	private TipoEtnia tipoEtnia;

	private TipoRedeEnsino tipoRedeEnsino = new TipoRedeEnsino();

	private EstadoCivil estadoCivil = new EstadoCivil();

	private TituloEleitor tituloEleitor;

	private Identidade identidade = new Identidade();

	private TipoRaca tipoRaca = new TipoRaca();

	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa();

	private Endereco enderecoContato = new Endereco();

	private String nome;

	private Date dataNascimento;

	private char sexo;

	private String passaporte;

	private GrauFormacao grauFormacao;

	private String abreviatura;

	private String nomePai;

	private String nomeMae;

	private String email;

	private String municipioNaturalidadeOutro;

	private String outroDocumento;

	private String endereco;

	private boolean valido;

	private Date ultimaAtualizacao;

	private boolean funcionario;

	// Constructors

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_pessoa", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idPessoa) {
		this.id = idPessoa;
	}

	@Column(name = "cpf_cnpj", unique = true, nullable = true, insertable = true, updatable = true)
	public Long getCpf_cnpj() {
		return cpf_cnpj;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_certificado_militar", unique = false, nullable = true, insertable = true, updatable = true)
	public CertificadoMilitar getCertificadoMilitar() {
		return this.certificadoMilitar;
	}

	public void setCertificadoMilitar(CertificadoMilitar certificadoMilitar) {
		this.certificadoMilitar = certificadoMilitar;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_municipio_naturalidade", unique = false, nullable = true, insertable = true, updatable = true)
	public Municipio getMunicipio() {
		return this.municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_endereco_profissional", unique = false, nullable = true, insertable = true, updatable = true)
	public Endereco getEnderecoProfissional() {
		return this.enderecoProfissional;
	}

	public void setEnderecoProfissional(
			Endereco enderecoByIdEnderecoProfissional) {
		this.enderecoProfissional = enderecoByIdEnderecoProfissional;
	}

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_endereco_residencial", unique = false, nullable = true, insertable = true, updatable = true)
	public Endereco getEnderecoResidencial() {
		return this.enderecoResidencial;
	}

	public void setEnderecoResidencial(Endereco enderecoByIdEnderecoResidencial) {
		this.enderecoResidencial = enderecoByIdEnderecoResidencial;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais_nacionalidade", unique = false, nullable = true, insertable = true, updatable = true)
	public Pais getPais() {
		return this.pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_etnia", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoEtnia getTipoEtnia() {
		return this.tipoEtnia;
	}

	public void setTipoEtnia(TipoEtnia tipoEtnia) {
		this.tipoEtnia = tipoEtnia;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_rede_ensino", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoRedeEnsino getTipoRedeEnsino() {
		return this.tipoRedeEnsino;
	}

	public void setTipoRedeEnsino(TipoRedeEnsino tipoRedeEnsino) {
		this.tipoRedeEnsino = tipoRedeEnsino;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_civil", unique = false, nullable = true, insertable = true, updatable = true)
	public EstadoCivil getEstadoCivil() {
		return this.estadoCivil;
	}

	public void setEstadoCivil(EstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_titulo_eleitor", unique = false, nullable = true, insertable = true, updatable = true)
	public TituloEleitor getTituloEleitor() {
		return this.tituloEleitor;
	}

	public void setTituloEleitor(TituloEleitor tituloEleitor) {
		this.tituloEleitor = tituloEleitor;
	}

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_identidade", unique = false, nullable = true, insertable = true, updatable = true)
	public Identidade getIdentidade() {
		return this.identidade;
	}

	public void setIdentidade(Identidade identidade) {
		this.identidade = identidade;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_raca", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoRaca getTipoRaca() {
		return this.tipoRaca;
	}

	public void setTipoRaca(TipoRaca tipoRaca) {
		this.tipoRaca = tipoRaca;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_uf_naturalidade", unique = false, nullable = true, insertable = true, updatable = true)
	public UnidadeFederativa getUnidadeFederativa() {
		return this.unidadeFederativa;
	}

	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_endereco_contato", unique = false, nullable = true, insertable = true, updatable = true)
	public Endereco getEnderecoContato() {
		return this.enderecoContato;
	}

	public void setEnderecoContato(Endereco enderecoByIdEnderecoContato) {
		this.enderecoContato = enderecoByIdEnderecoContato;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_nascimento", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataNascimento() {
		return this.dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	@Column(name = "sexo", unique = false, nullable = true, insertable = true, updatable = true, length = 1)
	public char getSexo() {
		return this.sexo;
	}

	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	@Column(name = "passaporte", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	public String getPassaporte() {
		return this.passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_grau_formacao", unique = false, nullable = true, insertable = true, updatable = true)
	public GrauFormacao getGrauFormacao() {
		return this.grauFormacao;
	}

	public void setGrauFormacao(GrauFormacao grauFormacao) {
		this.grauFormacao = grauFormacao;
	}

	@Column(name = "abreviatura", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getAbreviatura() {
		return this.abreviatura;
	}

	public void setAbreviatura(String abreviatura) {
		this.abreviatura = abreviatura;
	}

	@Column(name = "nome_pai", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getNomePai() {
		return this.nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	@Column(name = "nome_mae", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getNomeMae() {
		return this.nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	@Column(name = "email", unique = false, nullable = true, insertable = true, updatable = true, length = 50)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "municipio_naturalidade_outro", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getMunicipioNaturalidadeOutro() {
		return this.municipioNaturalidadeOutro;
	}

	public void setMunicipioNaturalidadeOutro(String municipioNaturalidadeOutro) {
		this.municipioNaturalidadeOutro = municipioNaturalidadeOutro;
	}

	@Column(name = "outro_documento", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	public String getOutroDocumento() {
		return this.outroDocumento;
	}

	public void setOutroDocumento(String outroDocumento) {
		this.outroDocumento = outroDocumento;
	}

	@Column(name = "endereco", unique = false, nullable = true, insertable = true, updatable = true, length = 150)
	public String getEndereco() {
		return this.endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	@Column(name = "tipo", unique = false, nullable = true, insertable = true, updatable = true, length = 1)
	public char getTipo() {
		return tipo;
	}

	@Column(name = "valido", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isValido() {
		return this.valido;
	}

	public void setValido(boolean valido) {
		this.valido = valido;
	}

	@Column(name = "ultima_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 8)
	public Date getUltimaAtualizacao() {
		return this.ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	@Column(name = "funcionario", unique = false, nullable = true, insertable = true, updatable = true)
	public Boolean isFuncionario() {
		return this.funcionario;
	}

	public void setFuncionario(boolean funcionario) {
		this.funcionario = funcionario;
	}


	public RegistroEntrada getAlteracao() {
		return alteracao;
	}

	public void setAlteracao(RegistroEntrada alteracao) {
		this.alteracao = alteracao;
	}

	public int getCodMovimento() {
		return codMovimento;
	}

	public void setCodMovimento(int codMovimento) {
		this.codMovimento = codMovimento;
	}

	public void setCpf_cnpj(Long cpf_cnpj) {
		this.cpf_cnpj = cpf_cnpj;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

}
