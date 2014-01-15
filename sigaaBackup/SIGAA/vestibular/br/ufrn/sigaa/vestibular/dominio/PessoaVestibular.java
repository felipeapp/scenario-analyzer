/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/08/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateBirthday;
import static br.ufrn.arq.util.ValidatorUtil.validateCPF_CNPJ;
import static br.ufrn.arq.util.ValidatorUtil.validateEmailRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.CertificadoMilitar;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.TipoEtnia;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TipoRedeEnsino;
import br.ufrn.sigaa.pessoa.dominio.TituloEleitor;

/**
 * Dados Pessoais do Candidato ao Vestibular.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "pessoa_vestibular", schema = "vestibular")
public class PessoaVestibular extends PessoaGeral implements PersistDB, Validatable, Serializable {

	/** Certificado Militar do candidato. */
	private CertificadoMilitar certificadoMilitar;

	/** Município de Naturalidade do candidato. */
	private Municipio municipio = new Municipio();

	/** País de naturalidade do candidato. */
	private Pais pais = new Pais();

	/** Etnia do candidato.*/
	private TipoEtnia tipoEtnia;

	/** Tipo da Rede de Ensino do candidato. */
	private TipoRedeEnsino tipoRedeEnsino = new TipoRedeEnsino();

	/** Estado Civil do candidato. */
	private EstadoCivil estadoCivil = new EstadoCivil();

	/** Dados do Título de Eleitor do candidato. */
	private TituloEleitor tituloEleitor;

	/** Dados da Identidade do candidato. */
	private Identidade identidade = new Identidade();

	/** Tipo de raça do candidato. */
	private TipoRaca tipoRaca = new TipoRaca();

	/** Unidade Federativa de naturalidade do candidato.*/
	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO);

	/** Endereço de contato do candidato. */
	private Endereco enderecoContato = new Endereco();

	/** Nome do pai do candidato.*/
	private String nomePai;

	/** Nome da mãe do candidato.*/
	private String nomeMae;

	/** Nome do município de naturalidade do candidato, quando não há município pré-definido de naturalidade. */
	private String municipioNaturalidadeOutro;

	/** Outros dados de identificação do candidato. */
	private String outroDocumento;

	/** Data de última atualização dos dados do candidato. */
	private Date ultimaAtualizacao;

	/** Data de cadastro dos dados do candidato.*/
	private Date dataCadastro;

	/** Unidade Federativa de conclusão do Ensino Médio do candidato.*/
	private UnidadeFederativa ufConclusaoEnsinoMedio = new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO);
	
	/** Nome da escola de conclusão do Ensino Médio do candidato. */
	private String nomeEscolaConclusaoEnsinoMedio;

	/** Escola onde o candidato concluiu o ensino médio. */
	private EscolaInep escolaConclusaoEnsinoMedio;
	
	/** Ano de conclusão do ensino médio.*/
	private String anoConclusaoEnsinoMedio;

	/** Tipo de necessidade especial do candidato.*/
	private TipoNecessidadeEspecial tipoNecessidadeEspecial;

	/** Nome do candidato, sem acentos ou caracteres especiais. */
	private String nomeAscii;
	
	/** ID do arquivo da foto 3x4 do candidto. */
	private Integer idFoto;
	
	/** Status da foto enviada pelo candidato. A foto é avaliada pela Comissão Permanente do Vestibular e validada, isto é, é realmente uma foto 3x4. */
	private StatusFoto statusFoto;
	
	/** Senha do candidato para acesso à área pessoal de acompanhamento do vestibular. */
	private String senha;
	
	/** Data do último acesso do usuário ao sistema. */
	private Date ultimoAcesso;
	
	/** Registro de entrada do usuário. */
	private RegistroEntrada registroEntrada;
	
	/** Data de validade da solicitação de recuperação de senha. */
	private Date validadeRecuperacaoSenha;
	
	/** Chave utilizada para recuperação de senha. */
	private String chaveRecuperacaoSenha;
	
	/** Registro de entrada do usuário que validou a foto do candidato. */ 
	private RegistroEntrada fotoValidadaPor;
	
	/** Novo status da foto do candidato, dado durante a verificação se realmente é uma foto no padrão 3x4. */
	private StatusFoto novoStatusFoto;
	
	/** Serve como totalizador para relatórios */
	private Long total;

	/** Arquivo enviado pelo usuário, a ser utilizado como foto 3x4. */
	private UploadedFile imagem;
	
	/** Dados pessoais anteriores. Mantem os dados que foram migrados. */
	private PessoaVestibular dadosAnteriores;
	
	/** Indica que a inscrição foi importada/migrada de concursos externos. */
	private boolean migrada = false;
	
	/** Construtor padrão. */
	public PessoaVestibular() {
		prepararDados();
	}

	/** Construtor parametrizado. */
	public PessoaVestibular(int id) {
		this();
		this.id = id;
	}

	/** Construtor parametrizado. */
	public PessoaVestibular(int idPessoa, String nome) {
		this(idPessoa);
		this.nome = nome;
	}

	/** Construtor parametrizado. */
	public PessoaVestibular(int idPessoa, String nome, long num) {
		this(idPessoa, nome);
		setCpf_cnpj(num);
	}
	
	/** Retorna a chave primária.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getId()
	 */
	@Override
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="vestibular.pessoa_seq") })
	@Column(name = "id_pessoa", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setId(int)
	 */
	@Override
	public void setId(int idPessoa) {
		this.id = idPessoa;
	}

	/** Retorna o CPF do candidato
	 * @see br.ufrn.comum.dominio.PessoaGeral#getCpf_cnpj()
	 */
	@Override
	@Column(name = "cpf_cnpj", unique = true, nullable = true, insertable = true, updatable = true)
	public Long getCpf_cnpj() {
		return super.getCpf_cnpj();
	}

	/** Retorna o número do telefone fixo do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getTelefone()
	 */
	@Override
	@Column(name = "telefone_fixo", unique = true, nullable = true, insertable = true, updatable = true)
	public String getTelefone() {
		return super.getTelefone();
	}

	/** Retorna o número do telefone do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getCelular()
	 */
	@Override
	@Column(name = "telefone_celular", unique = true, nullable = true, insertable = true, updatable = true)
	public String getCelular() {
		return super.getCelular();
	}

	/** Retorna os dados do certificado de obrigações militares do candidato.
	 * @return
	 */
	@Embedded
	public CertificadoMilitar getCertificadoMilitar() {
		return this.certificadoMilitar;
	}

	/** Seta os dados do certificado de obrigações militares do candidato.
	 * @param certificadoMilitar
	 */
	public void setCertificadoMilitar(CertificadoMilitar certificadoMilitar) {
		this.certificadoMilitar = certificadoMilitar;
	}

	/** Retorna o município e a unidade federativa da naturalidade do candidato.
	 * @return
	 */
	@Transient
	public String getMunicipioUf() {
		StringBuilder sb = new StringBuilder();
		if (municipio != null) sb.append(municipio.getNome());
		if (unidadeFederativa != null) {
			if (sb.length() > 0) sb.append("/");
			sb.append(unidadeFederativa.getSigla());
		}
		return sb.toString();
	}

	/** Retorna o município da naturalidade do candidato.
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_municipio_naturalidade", unique = false, nullable = true, insertable = true, updatable = true)
	public Municipio getMunicipio() {
		return this.municipio;
	}

	/** Seta o município da naturalidade do candidato.
	 * @param municipio
	 */
	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	/** Retorna o país da naturalidade do candidato.
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais_nacionalidade", unique = false, nullable = true, insertable = true, updatable = true)
	public Pais getPais() {
		return this.pais;
	}

	/** Seta o país da naturalidade do candidato.
	 * @param pais
	 */
	public void setPais(Pais pais) {
		this.pais = pais;
	}

	/** Retorna a Etnia do candidato.
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_etnia", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoEtnia getTipoEtnia() {
		return this.tipoEtnia;
	}

	/** Seta a Etnia do candidato.
	 * @param tipoEtnia
	 */
	public void setTipoEtnia(TipoEtnia tipoEtnia) {
		this.tipoEtnia = tipoEtnia;
	}

	/** Retorna o tipo da Rede de Ensino do candidato. 
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_rede_ensino", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoRedeEnsino getTipoRedeEnsino() {
		return this.tipoRedeEnsino;
	}

	/** Seta o tipo da Rede de Ensino do candidato.
	 * @param tipoRedeEnsino
	 */
	public void setTipoRedeEnsino(TipoRedeEnsino tipoRedeEnsino) {
		this.tipoRedeEnsino = tipoRedeEnsino;
	}

	/** Retorna o Estado Civil do candidato. 
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_estado_civil", unique = false, nullable = true, insertable = true, updatable = true)
	public EstadoCivil getEstadoCivil() {
		return this.estadoCivil;
	}

	/** Seta o Estado Civil do candidato. 
	 * @param estadoCivil
	 */
	public void setEstadoCivil(EstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	/** Retorna os dados do Título de Eleitor do candidato. 
	 * @return
	 */
	@Embedded
	public TituloEleitor getTituloEleitor() {
		return this.tituloEleitor;
	}

	/** Seta os dados do Título de Eleitor do candidato. 
	 * @param tituloEleitor
	 */
	public void setTituloEleitor(TituloEleitor tituloEleitor) {
		this.tituloEleitor = tituloEleitor;
	}

	/** Retorna os dados da Identidade do candidato. 
	 * @return
	 */
	@Embedded
	public Identidade getIdentidade() {
		return this.identidade;
	}

	/** Seta os dados da Identidade do candidato.
	 * @param identidade
	 */
	public void setIdentidade(Identidade identidade) {
		this.identidade = identidade;
	}

	/** Retorna o Tipo de raça do candidato. 
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_raca", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoRaca getTipoRaca() {
		return this.tipoRaca;
	}

	/** Seta o Tipo de raça do candidato.
	 * @param tipoRaca
	 */
	public void setTipoRaca(TipoRaca tipoRaca) {
		this.tipoRaca = tipoRaca;
	}

	/** Retorna a Unidade Federativa de naturalidade do candidato.
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_uf_naturalidade", unique = false, nullable = true, insertable = true, updatable = true)
	public UnidadeFederativa getUnidadeFederativa() {
		return this.unidadeFederativa;
	}

	/** Seta a Unidade Federativa de naturalidade do candidato.
	 * @param unidadeFederativa
	 */
	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	/** Retorna o Endereço de contato do candidato. 
	 * @return
	 */
	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_endereco_contato", unique = false, nullable = true, insertable = true, updatable = true)
	public Endereco getEnderecoContato() {
		return this.enderecoContato;
	}

	/** Seta o Endereço de contato do candidato. 
	 * @param enderecoByIdEnderecoContato
	 */
	public void setEnderecoContato(Endereco enderecoByIdEnderecoContato) {
		this.enderecoContato = enderecoByIdEnderecoContato;
	}

	/** Retorna nome do candidato. 
	 * @see br.ufrn.comum.dominio.PessoaGeral#getNome()
	 */
	@Override
	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getNome() {
		return this.nome;
	}

	/** Seta nome do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setNome(java.lang.String)
	 */
	@Override
	public void setNome(String nome) {
		if (nome != null)
			nome = nome.toUpperCase();
		this.nome = nome;
		setNomeAscii(nome);
	}

	/** Retorna a data de nascimento do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getDataNascimento()
	 */
	@Override
	@Temporal(TemporalType.DATE)
	@Column(name = "data_nascimento", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataNascimento() {
		return this.dataNascimento;
	}

	/** Seta a data de nascimento do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setDataNascimento(java.util.Date)
	 */
	@Override
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/** Retorna o gênero do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getSexo()
	 */
	@Override
	@Column(name = "sexo", unique = false, nullable = true, insertable = true, updatable = true, length = 1)
	public char getSexo() {
		return this.sexo;
	}

	/** Seta o gênero do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setSexo(char)
	 */
	@Override
	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	/** Retorna os dados do passaporte do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getPassaporte()
	 */
	@Override
	@Column(name = "passaporte", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	public String getPassaporte() {
		return this.passaporte;
	}

	/** Seta os dados do passaporte do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setPassaporte(java.lang.String)
	 */
	@Override
	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	/** Retorna o nome do pai do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getNomePai()
	 */
	@Override
	@Column(name = "nome_pai", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getNomePai() {
		return this.nomePai;
	}

	/** Seta o nome do pai do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setNomePai(java.lang.String)
	 */
	@Override
	public void setNomePai(String nomePai) {
		if (nomePai != null)
			nomePai = nomePai.toUpperCase();
		this.nomePai = nomePai;
	}

	/** Retorna o nome da mãe do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getNomeMae()
	 */
	@Override
	@Column(name = "nome_mae", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getNomeMae() {
		return this.nomeMae;
	}

	/** Seta o nome da mãe do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setNomeMae(java.lang.String)
	 */
	@Override
	public void setNomeMae(String nomeMae) {
		if (nomeMae != null)
			nomeMae = nomeMae.toUpperCase();
		this.nomeMae = nomeMae;
	}

	/** Retorna o e-mail do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getEmail()
	 */
	@Override
	@Column(name = "email", unique = false, nullable = true, insertable = true, updatable = true, length = 50)
	public String getEmail() {
		return this.email;
	}

	/** Seta o e-mail do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setEmail(java.lang.String)
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
	}

	/** Retorna o nome do município de naturalidade do candidato, quando não há município pré-definido de naturalidade. 
	 * @return
	 */
	@Column(name = "municipio_naturalidade_outro", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getMunicipioNaturalidadeOutro() {
		return this.municipioNaturalidadeOutro;
	}

	/** Seta o nome do município de naturalidade do candidato, quando não há município pré-definido de naturalidade.
	 * @param municipioNaturalidadeOutro
	 */
	public void setMunicipioNaturalidadeOutro(String municipioNaturalidadeOutro) {
		this.municipioNaturalidadeOutro = municipioNaturalidadeOutro;
	}

	/** Retorna outros dados de identificação do candidato. 
	 * @return
	 */
	@Column(name = "outro_documento", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	public String getOutroDocumento() {
		return this.outroDocumento;
	}

	/** Seta outros dados de identificação do candidato.
	 * @param outroDocumento
	 */
	public void setOutroDocumento(String outroDocumento) {
		this.outroDocumento = outroDocumento;
	}

	/** Retorna outro endereço do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getEndereco()
	 */
	@Override
	@Column(name = "endereco", unique = false, nullable = true, insertable = true, updatable = true, length = 150)
	public String getEndereco() {
		return this.endereco;
	}

	/** Seta outro endereço do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setEndereco(java.lang.String)
	 */
	@Override
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	/** Retorna o tipo de pessoa (pessoa física ou pessoa jurídica)
	 * @see br.ufrn.comum.dominio.PessoaGeral#getTipo()
	 */
	@Override
	@Column(name = "tipo", unique = false, nullable = true, insertable = true, updatable = true, length = 1)
	public char getTipo() {
		return super.getTipo();
	}

	/** Informa se a pessoa tem CPF ou CNPJ válido.. usado para restringir a busca de pessoas migradas erradas  
	 * @see br.ufrn.comum.dominio.PessoaGeral#isValido()
	 */
	@Override
	@Column(name = "valido", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isValido() {
		return this.valido;
	}

	/** Seta se a pessoa tem CPF ou CNPJ válido.. usado para restringir a busca de pessoas migradas erradas 
	 * @see br.ufrn.comum.dominio.PessoaGeral#setValido(boolean)
	 */
	@Override
	public void setValido(boolean valido) {
		this.valido = valido;
	}

	/** Retorna a data de última atualização dos dados do candidato. 
	 * @return
	 */
	@Column(name = "ultima_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 8)
	public Date getUltimaAtualizacao() {
		return this.ultimaAtualizacao;
	}

	/** Seta a data de última atualização dos dados do candidato.
	 * @param ultimaAtualizacao
	 */
	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	/** Retorna o código DDD do telefone celular do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getCodigoAreaNacionalTelefoneCelular()
	 */
	@Override
	@Column(name = "codigo_area_nacional_telefone_celular", unique = false, nullable = true, insertable = true, updatable = true)
	public Short getCodigoAreaNacionalTelefoneCelular() {
		return super.getCodigoAreaNacionalTelefoneCelular();
	}

	/** Seta o código DDD do telefone celular do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setCodigoAreaNacionalTelefoneCelular(java.lang.Short)
	 */
	@Override
	public void setCodigoAreaNacionalTelefoneCelular(Short codigoAreaNacionalTelefoneCelular) {
		super.setCodigoAreaNacionalTelefoneCelular(codigoAreaNacionalTelefoneCelular);
	}

	/** Retorna o código DDD do telefone fixo do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getCodigoAreaNacionalTelefoneFixo()
	 */
	@Override
	@Column(name = "codigo_area_nacional_telefone_fixo", unique = false, nullable = true, insertable = true, updatable = true)
	public Short getCodigoAreaNacionalTelefoneFixo() {
		return super.getCodigoAreaNacionalTelefoneFixo();
	}

	/** Seta o código DDD do telefone fixo do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setCodigoAreaNacionalTelefoneFixo(java.lang.Short)
	 */
	@Override
	public void setCodigoAreaNacionalTelefoneFixo(Short ocodigoAreaNacionalTelefoneFixodigoAreaNacionalTelefoneFixo) {
		super.setCodigoAreaNacionalTelefoneFixo(ocodigoAreaNacionalTelefoneFixodigoAreaNacionalTelefoneFixo);
	}

	/** Retorna o número do fax do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#getFax()
	 */
	@Override
	@Column(name = "fax", unique = true, nullable = true, insertable = true, updatable = true)
	public String getFax() {
		return fax;
	}

	/** Seta o número do fax do candidato.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setFax(java.lang.String)
	 */
	@Override
	public void setFax(String fax) {
		this.fax = fax;
	}

	/** Indica se o candidato é pessoa física.
	 * @return
	 */
	@Transient
	public boolean isPF() {
		return this.getTipo() == PESSOA_FISICA;
	}

	/** Indica se o candidato é pessoa jurídica.
	 * @return
	 */
	@Transient
	public boolean isPJ() {
		return this.getTipo() == PESSOA_JURIDICA;
	}

	/** Retorna se este objeto é igual ao o objeto passado no parâmetro, comparando a chave primária, nome e cpf/cnpj.
	 * @see br.ufrn.comum.dominio.PessoaGeral#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome", "cpf_cnpj");
	}

	/** Retorna o código hash deste objeto.
	 * @see br.ufrn.comum.dominio.PessoaGeral#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, getCpf_cnpj(), nome);
	}

	/** Retorna o nome do candidato.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return nome;
	}

	/**
	 * Retorna os dois primeiros nomes da pessoa
	 * @return
	 */
	@Transient
	public String getNomeResumido() {
		if (nome != null) {
			String[] nomes = nome.split(" ");
			if (nomes.length > 1) {
				if (nomes.length > 2 && (nomes[1].equals("DE") || nomes[1].equals("DO")))
					return nomes[0] + " " + nomes[2];
				else
					return nomes[0] + " " + nomes[1];
			} else {
				return nome;
			}
		} else {
			return "";
		}
	}

	/** Retorna a data de cadastro dos dados do candidato.
	 * @return 
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/** Seta a data de cadastro dos dados do candidato.
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/** Inicializa os atributos nulos. */
	public void prepararDados() {
		// Naturalidade
		if (isEmpty(this.pais)) {
			this.setPais(new Pais(Pais.BRASIL));
		}
		if (isEmpty(this.getUnidadeFederativa())) {
			this.setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		}
		if (isEmpty(this.getMunicipio())) {
			this.setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
		}
		// Endereço de Contato
		if (this.getEnderecoContato() == null) {
			this.setEnderecoContato(new Endereco());
		}
		if (isEmpty(this.getEnderecoContato().getMunicipio())) {
			this.getEnderecoContato().setMunicipio(new Municipio(Municipio.ID_MUNICIPIO_PADRAO));
		}
		if (isEmpty(this.getEnderecoContato().getUnidadeFederativa())) {
			this.getEnderecoContato().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		}
		if (isEmpty(this.getEnderecoContato().getTipoLogradouro()))
			this.getEnderecoContato().setTipoLogradouro(new TipoLogradouro());
		// Raça
		if (isEmpty(this.getTipoRaca())) {
			this.setTipoRaca(new TipoRaca());
		}
		// Identidade
		if (isEmpty(this.getIdentidade())) {
			this.setIdentidade(new Identidade());
			this.getIdentidade().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		} else if (isEmpty(this.getIdentidade().getUnidadeFederativa())) {
			this.getIdentidade().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		}
		// Título de Eleitor
		if (isEmpty(this.getTituloEleitor())) {
			this.setTituloEleitor(new TituloEleitor());
		} 
		if (isEmpty(this.getTituloEleitor().getUnidadeFederativa())) {
			this.getTituloEleitor().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		}
		// Estado Civil
		if (isEmpty(this.getEstadoCivil())) {
			this.setEstadoCivil(new EstadoCivil());
		}

		if (isEmpty(this.getTipoNecessidadeEspecial()))
			this.setTipoNecessidadeEspecial(new TipoNecessidadeEspecial());
		
		if (isEmpty(escolaConclusaoEnsinoMedio))
			this.escolaConclusaoEnsinoMedio = new EscolaInep();
		
		if (statusFoto == null)
			this.statusFoto = new StatusFoto(StatusFoto.NAO_ANALISADA);
		
		if (certificadoMilitar == null)
			this.certificadoMilitar = new CertificadoMilitar();
		
		if (isEmpty(this.ufConclusaoEnsinoMedio)) {
			this.ufConclusaoEnsinoMedio = new UnidadeFederativa();
		}
		novoStatusFoto = new StatusFoto(0);
	}

	/**
	 * Define como null atributos que estiverem com valores vazios
	 */
	public void anularAtributosVazios() {

		if ( isInternacional()) {
			setUnidadeFederativa(null);
			setMunicipio(null);

			if (isEmpty(getCpf_cnpj())) {
				setCpf_cnpj(null);
			}
		}

		if (tituloEleitor != null && StringUtils.isEmpty( tituloEleitor.getNumero() ) ){
			tituloEleitor = null;
		}

		if( getEstadoCivil() != null && getEstadoCivil().getId() == 0 )
			setEstadoCivil(null);

		if( getTipoRaca() != null && getTipoRaca().getId() == 0 )
			setTipoRaca(null);
		
		if (getTipoRedeEnsino() != null && getTipoRedeEnsino().getId() == 0) {
			setTipoRedeEnsino(null);
		}
		
		if (getTipoNecessidadeEspecial() != null && getTipoNecessidadeEspecial().getId() == 0){
			setTipoNecessidadeEspecial(null);
		}
		
		if (getEscolaConclusaoEnsinoMedio() != null && getEscolaConclusaoEnsinoMedio().getId() == 0) {
			setEscolaConclusaoEnsinoMedio(null);
		}
		
		if (getCertificadoMilitar() != null && StringUtils.isEmpty( getCertificadoMilitar().getNumero()))
			setCertificadoMilitar(null);

	}

	/** Retorna o ano de conclusão do ensino médio.
	 * @return
	 */
	@Column(name = "segundograuanoconclusao", unique = false, nullable = true, insertable = true, updatable = true)
	public String getAnoConclusaoEnsinoMedio() {
		return this.anoConclusaoEnsinoMedio;
	}

	/** Seta o ano de conclusão do ensino médio.
	 * @param anoConclusaoEnsinoMedio
	 */
	public void setAnoConclusaoEnsinoMedio(String anoConclusaoEnsinoMedio) {
		this.anoConclusaoEnsinoMedio = anoConclusaoEnsinoMedio;
	}

	/** Retorna o nome da escola de conclusão do Ensino Médio do candidato. 
	 * @return
	 */
	@Column(name = "segundograuescola", unique = false, nullable = true, insertable = true, updatable = true)
	public String getNomeEscolaConclusaoEnsinoMedio() {
		if (!ValidatorUtil.isEmpty(escolaConclusaoEnsinoMedio))
			return this.escolaConclusaoEnsinoMedio.getNome();
		else
			return this.nomeEscolaConclusaoEnsinoMedio;
	}

	/** Seta o nome da escola de conclusão do Ensino Médio do candidato.
	 * @param nomeEscolaConclusaoEnsinoMedio
	 */
	public void setNomeEscolaConclusaoEnsinoMedio(String nomeEscolaConclusaoEnsinoMedio) {
		this.nomeEscolaConclusaoEnsinoMedio = nomeEscolaConclusaoEnsinoMedio;
	}

	/** Indica se o candidato é estrangeiro.
	 * @see br.ufrn.comum.dominio.PessoaGeral#isInternacional()
	 */
	@Override
	@Column(name = "internacional", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isInternacional() {
		return super.isInternacional();
	}

	/** Retorna o primeiro nome do candidato.
	 * @return
	 */
	@Transient
	public String getPrimeiroNome() {
		String[] nomes = nome.split(" ");
		return nomes[0];
	}

	/** Retorna o tipo de necessidade especial do candidato.
	 * @return
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_tipo_necessidade_especial")
	public TipoNecessidadeEspecial getTipoNecessidadeEspecial() {
		return tipoNecessidadeEspecial;
	}

	/** Seta o tipo de necessidade especial do candidato.
	 * @param tipoNecessidadeEspecial
	 */
	public void setTipoNecessidadeEspecial(TipoNecessidadeEspecial tipoNecessidadeEspecial) {
		this.tipoNecessidadeEspecial = tipoNecessidadeEspecial;
	}

	/** Retorna o nome do candidato, sem acentos ou caracteres especiais. 
	 * @see br.ufrn.comum.dominio.PessoaGeral#getNomeAscii()
	 */
	@Override
	@Column(name = "nome_ascii")
	public String getNomeAscii() {
		return nomeAscii;
	}

	/** Seta o nome do candidato, sem acentos ou caracteres especiais.
	 * @see br.ufrn.comum.dominio.PessoaGeral#setNomeAscii(java.lang.String)
	 */
	@Override
	public void setNomeAscii(String nomeAscii) {
		if ( nomeAscii != null)
			this.nomeAscii = StringUtils.toAscii(nomeAscii.toUpperCase());
	}

	/**
	 * Valida somente os dados de contato do candidato.
	 * 
	 * @return
	 */
	public ListaMensagens validarDadosContato() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(enderecoContato.getCep(), "CEP", lista);
		validateRequired(enderecoContato.getTipoLogradouro(), "Tipo do Logradouro", lista);
		validateRequired(enderecoContato.getLogradouro(), "Logradouro", lista);
		validateRequired(enderecoContato.getNumero(), "Número", lista);
		validateRequired(enderecoContato.getBairro(), "Bairro", lista);
		validateRequired(enderecoContato.getUnidadeFederativa(), "Unidade Federativa", lista);
		validateRequired(enderecoContato.getMunicipio(), "Município", lista);
		validateEmailRequired(email, "E-mail", lista);
		return lista;
	}

	/** Retorna a Unidade Federativa de conclusão do Ensino Médio do candidato.
	 * @return
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_uf_conclusao_em")
	public UnidadeFederativa getUfConclusaoEnsinoMedio() {
		return ufConclusaoEnsinoMedio;
	}

	/** Seta a Unidade Federativa de conclusão do Ensino Médio do candidato.
	 * @param ufConclusaoEnsinoMedio
	 */
	public void setUfConclusaoEnsinoMedio(UnidadeFederativa ufConclusaoEnsinoMedio) {
		this.ufConclusaoEnsinoMedio = ufConclusaoEnsinoMedio;
	}

	/** 
	 * Retorna o ID do arquivo da foto 3x4 do candidato. 
	 * @return
	 */
	@Column(name="id_foto")
	public Integer getIdFoto() {
		return idFoto;
	}

	/** 
	 * Seta o ID do arquivo da foto 3x4 do candidato. 
	 * @param idFoto
	 */
	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	/** Valida os dados pessoais obrigatórios do candidato. 
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		if (getCpf_cnpj() == 0)
			setCpf_cnpj(null);
		validateRequired(getCpf_cnpj(), "CPF", lista);
		if (getCpf_cnpj() != null)
			validateCPF_CNPJ(getCpf_cnpj(), "CPF", lista);
		validateRequired(getNome(), "Nome", lista);
		validateMaxLength(getNomePai(), 200, "Nome", lista);
		validateBirthday(getDataNascimento(), "Data Nascimento", lista);
		validateRequired(getDataNascimento(), "Data de Nascimento", lista);
		if (getEstadoCivil() == null)
			validateRequired(getEstadoCivil(), "Estado Civil", lista);
		else
			validateRequiredId(getEstadoCivil().getId(), "Estado Civil", lista);
		validateRequired(getNomeMae(), "Nome da mãe", lista);
		validateMaxLength(getNomePai(), 200, "Nome do Pai", lista);
		validateMaxLength(getNomeMae(), 200, "Nome da Mãe", lista);
		if (getIdentidade() == null)
			validateRequired(getIdentidade(), "Documento de Identificação", lista);
		else {
			validateRequired(getIdentidade().getNumero(), "N° do Doc. de Identificação", lista);
			validateRequired(getIdentidade().getOrgaoExpedicao(), "Órgão de Expedição do Doc. de Identificação", lista);
			validateRequired(getIdentidade().getDataExpedicao(), "Data de Expedição do Doc. de Identificação", lista);
			validateMinValue(getIdentidade().getDataExpedicao(), getDataNascimento(), "Data de Expedição do Doc. de Identificação", lista);
			validateMaxValue(getIdentidade().getDataExpedicao(), new Date(), "Data de Expedição do Doc. de Identificação", lista);
		}
		// Conclusão EM
		if (getUfConclusaoEnsinoMedio() == null)
			validateRequired(getUfConclusaoEnsinoMedio(), "UF da Escola de Conclusão do Ensino Médio", lista);
		else
			validateRequiredId(getUfConclusaoEnsinoMedio().getId(), "UF da Escola de Conclusão do Ensino Médio", lista);
		if (ValidatorUtil.isEmpty(getEscolaConclusaoEnsinoMedio()) && ValidatorUtil.isEmpty(getNomeEscolaConclusaoEnsinoMedio()))
			validateRequired(getEscolaConclusaoEnsinoMedio(), "Escola de Conclusão do Ensino Médio", lista);
		Integer ano = StringUtils.extractInteger(getAnoConclusaoEnsinoMedio());
		if (ano == null)
			validateRequired(ano, "Ano de Conclusão do Ensino Médio", lista);
		else {
			validateMaxValue(ano, CalendarUtils.getAnoAtual(), "Ano de Conclusão do Ensino Médio", lista);
			if (getDataNascimento() != null)
				validateMinValue(ano, CalendarUtils.getAno(getDataNascimento()), "Ano de Conclusão do Ensino Médio", lista);
		}
		ValidatorUtil.validateMinLength(getNomeEscolaConclusaoEnsinoMedio(), 5, "Nome da Escola", lista);
		// certificado militar
		if (getCertificadoMilitar() != null && getCertificadoMilitar().getDataExpedicao() != null && getDataNascimento() != null){
			Date dezesseteAnos = CalendarUtils.adicionarAnos(getDataNascimento(), 17);
			validateMinValue(getCertificadoMilitar().getDataExpedicao(), dezesseteAnos, "Data de Expedição do Certificado Militar", lista);
		}
		// dados de contato
		lista.addAll(validarDadosContato().getMensagens());
		return lista;
	}

	/** Retorna a Senha do candidato para acesso à área pessoal de acompanhamento do vestibular. 
	 * @return
	 */
	public String getSenha() {
		return senha;
	}

	/** Seta a Senha do candidato para acesso à área pessoal de acompanhamento do vestibular.
	 * @param senha
	 */
	public void setSenha(String senha) {
		if (senha != null) senha = senha.trim();
		this.senha = senha;
	}

	/** Retorna a data do último acesso do usuário ao sistema. 
	 * @return
	 */
	@Column(name="ultimo_acesso")
	public Date getUltimoAcesso() {
		return ultimoAcesso;
	}

	/** Seta a data do último acesso do usuário ao sistema.
	 * @param ultimoAcesso
	 */
	public void setUltimoAcesso(Date ultimoAcesso) {
		this.ultimoAcesso = ultimoAcesso;
	}

	/** Retorna o registro de entrada do usuário. 
	 * @return
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_registro_entrada")
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	/** Seta o registro de entrada do usuário.
	 * @param registroEntrada
	 */
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	
	/** Retorna a escola onde o candidato concluiu o ensino médio. 
	 * @return
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_escola")
	public EscolaInep getEscolaConclusaoEnsinoMedio() {
		return escolaConclusaoEnsinoMedio;
	}

	/** Seta a escola onde o candidato concluiu o ensino médio.
	 * @param escolaConclusaoEnsinoMedio
	 */
	public void setEscolaConclusaoEnsinoMedio(EscolaInep escolaConclusaoEnsinoMedio) {
		this.escolaConclusaoEnsinoMedio = escolaConclusaoEnsinoMedio;
	}

	/** Retorna a data de validade da solicitação de recuperação de senha. 
	 * @return
	 */
	@Column(name="validade_recuperacao_senha")
	public Date getValidadeRecuperacaoSenha() {
		return validadeRecuperacaoSenha;
	}

	/** Seta a data de validade da solicitação de recuperação de senha.
	 * @param validadeRecuperacaoSenha
	 */
	public void setValidadeRecuperacaoSenha(Date validadeRecuperacaoSenha) {
		this.validadeRecuperacaoSenha = validadeRecuperacaoSenha;
	}

	/** Retorna  a chave utilizada para recuperação de senha. 
	 * @return
	 */
	@Column(name="chave_recuperacao_senha")
	public String getChaveRecuperacaoSenha() {
		return chaveRecuperacaoSenha;
	}

	/** Seta  a chave utilizada para recuperação de senha.
	 * @param chaveRecuperacaoSenha
	 */
	public void setChaveRecuperacaoSenha(String chaveRecuperacaoSenha) {
		this.chaveRecuperacaoSenha = chaveRecuperacaoSenha;
	}

	/** Indica se a foto foi avaliada pela Comissão Permanente do Vestibular e validada, isto é, é realmente uma foto 3x4. 
	 * @return
	 */
	@Transient
	public boolean isFotoValida() {
		return statusFoto != null && statusFoto.isValida();
	}

	/** Retorna o status da foto enviada pelo candidato. A foto é avaliada pela Comissão Permanente do Vestibular e validada, isto é, é realmente uma foto 3x4.
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status_foto")
	public StatusFoto getStatusFoto() {
		return statusFoto;
	}

	/** Seta o status da foto enviada pelo candidato. A foto é avaliada pela Comissão Permanente do Vestibular e validada, isto é, é realmente uma foto 3x4.
	 * @param statusFoto
	 */
	public void setStatusFoto(StatusFoto statusFoto) {
		this.statusFoto = statusFoto;
	}

	/** Retorna o registro de entrada do usuário que validou a foto do candidato. 
	 * @return
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_registro_validacao_foto")
	public RegistroEntrada getFotoValidadaPor() {
		return fotoValidadaPor;
	}

	/** Seta o registro de entrada do usuário que validou a foto do candidato.
	 * @param fotoValiadaPor
	 */
	public void setFotoValidadaPor(RegistroEntrada fotoValidadaPor) {
		this.fotoValidadaPor = fotoValidadaPor;
	}

	/** Retorna o novo status da foto do candidato, dado durante a verificação se realmente é uma foto no padrão 3x4. 
	 * @return
	 */
	@Transient
	public StatusFoto getNovoStatusFoto() {
		return novoStatusFoto;
	}

	/** Seta o novo status da foto do candidato, dado durante a verificação se realmente é uma foto no padrão 3x4.
	 * @param novoStatusFoto
	 */
	public void setNovoStatusFoto(StatusFoto novoStatusFoto) {
		this.novoStatusFoto = novoStatusFoto;
	}

	/** Indica se o status da foto do candidato foi alterado.
	 * @return
	 */
	@Transient
	public boolean isStatusFotoAlterado() {
		return statusFoto.getId() != novoStatusFoto.getId();
	}

	/**
	 * Retorna o total
	 */
	@Transient
	public Long getTotal() {
		return total;
	}

	/**
	 * Seta o total
	 */
	public void setTotal(Long total) {
		this.total = total;
	}
	
	@Transient
	public UploadedFile getImagem() {
		return imagem;
	}

	public void setImagem(UploadedFile imagem) {
		this.imagem = imagem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa_anterior")
	public PessoaVestibular getDadosAnteriores() {
		return dadosAnteriores;
	}

	public void setDadosAnteriores(PessoaVestibular dadosAnteriores) {
		this.dadosAnteriores = dadosAnteriores;
	}

	public boolean isMigrada() {
		return migrada;
	}

	public void setMigrada(boolean migrada) {
		this.migrada = migrada;
	}

}