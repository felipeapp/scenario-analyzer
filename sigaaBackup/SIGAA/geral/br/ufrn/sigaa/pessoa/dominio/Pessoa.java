/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '29/02/2008'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateEmailRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;
import java.util.StringTokenizer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.dominio.GrauFormacao;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ead.dominio.PoloCurso;

/**
 * Pessoa do SIGAA, dados específicos de pessoa no sistema acadêmico.
 * @author Gleydson
 */
@Entity
@Table(schema="comum", name = "pessoa")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Pessoa extends PessoaGeral implements PersistDB {

	/** certificado militar da pessoa */
	private CertificadoMilitar certificadoMilitar;

	/** município da pessoa */
	private Municipio municipio = new Municipio();

	/** país da pessoa */
	private Pais pais = new Pais();

	/** etnia da pessoa */
	private TipoEtnia tipoEtnia;
	/** Tipo da Rede de Ensino de origem da Pessoa*/
	private TipoRedeEnsino tipoRedeEnsino = new TipoRedeEnsino();
	/** Estado Civil da Pessoa */
	private EstadoCivil estadoCivil = new EstadoCivil();
	/** Título Eleitor da pessoa */
	private TituloEleitor tituloEleitor;
	/** Identidade da pessoa */
	private Identidade identidade = new Identidade();
	/** Tipo de Raça da Pessoa */
	private TipoRaca tipoRaca = new TipoRaca();
	/** Unidade Federativa do endereço da pessoa. */
	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa();
	/** Endereço de contato da pessoa */
	private Endereco enderecoContato = new Endereco();
	/** Grau de Formação da pessoa */
	private GrauFormacao grauFormacao;
	/** Abreviatura */
	private String abreviatura;
	/** Nome do Pai da pessoa */
	private String nomePai;
    /** Nome da Mãe da pessoa */
	private String nomeMae;
	/** Município de naturalidade fora da base de dados. */
	private String municipioNaturalidadeOutro;
	/** Outra forma de documentação da pessoa. */
	private String outroDocumento;
	/** Atributo responsável por verificar se o domínio é valido. */
	private boolean valido = true;
	/** Data da última atualização dos dados da pessoa. */
	private Date ultimaAtualizacao;
	/** Verificador se a pessoa é funcionária. */
	private boolean funcionario;
	/** Conta bancária da pessoa. */
	private ContaBancaria contaBancaria;
	/** Informa a origem da pessoa com relação a nível de ensino. */
	private Character origem;
	/** Data de Cadastro da pessoa. */
	private Date dataCadastro;
	/** Cidade Polo ligado com um curso de ensino a distância. */
	private PoloCurso polo;

	/** Escola de conclusão do ensino médio. */
	private String escolaConclusaoEnsinoMedio;
	/** Ano de conclusão do ensino médio da pessoa. */
	private String anoConclusaoEnsinoMedio;
	/** Registro do tipo de necessidade especial da pessoa. */
	private TipoNecessidadeEspecial tipoNecessidadeEspecial;
	/** Nome da pessoa em formato ASCII. */
	private String nomeAscii;
	/** Tipo sanguineo da pessoa. */
	private String tipoSanguineo; 
	/** País de nacionalidade da pessoa. */
	private String paisNacionalidade;
	/** Registro de perfil da pessoa. */
	private PerfilPessoa perfil;
	/** Nome oficial da pessoa, usado em documentos oficiais. */
	private String nomeOficial;
	
	public Character getOrigem() {
		return origem;
	}

	public void setOrigem(Character origem) {
		this.origem = origem;
	}

	/** default constructor */
	public Pessoa() {
		setInternacional(false);
	}

	/** default minimal constructor */
	public Pessoa(int id) {
		this();
		this.id = id;
	}

	/** minimal constructor */
	public Pessoa(int id, String nome) {
		this(id);
		this.nome = nome;
	}

	public Pessoa(int idPessoa, String nome, Long num) {
		this(idPessoa, nome);
		setCpf_cnpj(num);
	}

	public Pessoa(String nome, Date dataNascimento, String logradouro, String numero, String complemento, String bairro,
			String cidade, String cep, String telefoneFixo, String telefoneCelular, String fax, String identidade, String email, String nomeMae){
		setNome(nome);
		setDataNascimento(dataNascimento);
		getEnderecoContato().setLogradouro(logradouro);
		getEnderecoContato().setNumero(numero);
		getEnderecoContato().setComplemento(complemento);
		getEnderecoContato().setBairro(bairro);
		getEnderecoContato().getMunicipio().setNome(cidade);
		getEnderecoContato().setCep(cep);
		setTelefone(telefoneFixo);
		setCelular(telefoneCelular);
		setFax(fax);
		getIdentidade().setNumero(identidade);
		setEmail(email);
		setNomeMae(nomeMae);
	}


	// Property accessors
	@Override
	@Id
	@Column(name = "id_pessoa", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int idPessoa) {
		this.id = idPessoa;
	}

	@Override
	@Column(name = "cpf_cnpj", unique = true, nullable = true, insertable = true, updatable = true)
	public Long getCpf_cnpj() {
		return super.getCpf_cnpj();
	}

	@Override
	@Column(name = "telefone_fixo", unique = true, nullable = true, insertable = true, updatable = true)
	public String getTelefone() {
		return super.getTelefone();
	}

	@Override
	@Column(name = "telefone_celular", unique = true, nullable = true, insertable = true, updatable = true)
	public String getCelular() {
		return super.getCelular();
	}

	@Embedded
	public CertificadoMilitar getCertificadoMilitar() {
		return this.certificadoMilitar;
	}

	public void setCertificadoMilitar(CertificadoMilitar certificadoMilitar) {
		this.certificadoMilitar = certificadoMilitar;
	}
	
	/** Método de retorno do nome do município agregado a Unidade Federativa. */
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

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_municipio_naturalidade", unique = false, nullable = true, insertable = true, updatable = true)
	public Municipio getMunicipio() {
		return this.municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
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

	@Embedded
	public TituloEleitor getTituloEleitor() {
		return this.tituloEleitor;
	}

	public void setTituloEleitor(TituloEleitor tituloEleitor) {
		this.tituloEleitor = tituloEleitor;
	}

	@Embedded
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

	@Override
	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getNome() {
		return this.nome;
	}
	
	@Override
	public void setNome(String nome) {
		this.nome = nome;
		setNomeAscii(nome);
	}

	@Override
	@Temporal(TemporalType.DATE)
	@Column(name = "data_nascimento", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataNascimento() {
		return this.dataNascimento;
	}

	@Override
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	@Override
	@Column(name = "sexo", unique = false, nullable = true, insertable = true, updatable = true, length = 1)
	public char getSexo() {
		return this.sexo;
	}

	@Override
	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	@Override
	@Column(name = "passaporte", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	public String getPassaporte() {
		return this.passaporte;
	}

	@Override
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

	@Override
	@Column(name = "nome_pai", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getNomePai() {
		return this.nomePai;
	}

	@Override
	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	@Override
	@Column(name = "nome_mae", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getNomeMae() {
		return this.nomeMae;
	}

	@Override
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	@Override
	@Column(name = "email", unique = false, nullable = true, insertable = true, updatable = true, length = 50)
	public String getEmail() {
		return this.email;
	}

	@Override
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

	@Override
	@Column(name = "endereco", unique = false, nullable = true, insertable = true, updatable = true, length = 150)
	public String getEndereco() {
		return this.endereco;
	}

	@Override
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	@Override
	@Column(name = "tipo", unique = false, nullable = true, insertable = true, updatable = true, length = 1)
	public char getTipo() {
		return super.getTipo();
	}

	@Override
	@Column(name = "valido", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isValido() {
		return this.valido;
	}

	@Override
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

	@Override
	@Column(name = "funcionario", unique = false, nullable = true, insertable = true, updatable = true)
	public Boolean isFuncionario() {
		return this.funcionario;
	}

	public void setFuncionario(boolean funcionario) {
		this.funcionario = funcionario;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_conta_bancaria", unique = false, nullable = true, insertable = true, updatable = true)
	public ContaBancaria getContaBancaria() {
		return contaBancaria;
	}

	public void setContaBancaria(ContaBancaria contaBancaria) {
		this.contaBancaria = contaBancaria;
	}

	@Override
	@Column(name = "codigo_area_nacional_telefone_celular", unique = false, nullable = true, insertable = true, updatable = true)
	public Short getCodigoAreaNacionalTelefoneCelular() {
		return super.getCodigoAreaNacionalTelefoneCelular();
	}

	@Override
	public void setCodigoAreaNacionalTelefoneCelular(Short codigoAreaNacionalTelefoneCelular) {
		super.setCodigoAreaNacionalTelefoneCelular(codigoAreaNacionalTelefoneCelular);
	}

	@Override
	@Column(name = "codigo_area_nacional_telefone_fixo", unique = false, nullable = true, insertable = true, updatable = true)
	public Short getCodigoAreaNacionalTelefoneFixo() {
		return super.getCodigoAreaNacionalTelefoneFixo();
	}

	@Override
	public void setCodigoAreaNacionalTelefoneFixo(Short ocodigoAreaNacionalTelefoneFixodigoAreaNacionalTelefoneFixo) {
		super.setCodigoAreaNacionalTelefoneFixo(ocodigoAreaNacionalTelefoneFixodigoAreaNacionalTelefoneFixo);
	}

	@Override
	@Column(name = "fax", unique = true, nullable = true, insertable = true, updatable = true)
	public String getFax() {
		return fax;
	}

	@Override
	public void setFax(String fax) {
		this.fax = fax;
	}

	@Transient
	public boolean isPF() {
		return this.getTipo() == PESSOA_FISICA;
	}

	@Transient
	public boolean isPJ() {
		return this.getTipo() == PESSOA_JURIDICA;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome", "cpf_cnpj");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, getCpf_cnpj(), nome);
	}
	
	/**
	 * Método responsável por realizar a comparação de objetos Pessoa através do nome da pessoa. 
	 * @param obj
	 * @return
	 */
    public int compareTo(Object obj) {
        Pessoa pessoa = (Pessoa)obj;
        int ultimaComparacao = nomeAscii.compareTo(pessoa.nomeAscii);
        return (ultimaComparacao != 0 ? ultimaComparacao : nomeAscii.compareTo(pessoa.nomeAscii)); 
    }

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
	
	/**
	 * Retorna o nome abreviado da pessoa.
	 * @return
	 */
	@Transient
	public String getNomeAbreviado() {
		if (nome != null) {
			StringTokenizer nomeTok = new StringTokenizer(nome.trim());
			int count = nomeTok.countTokens();
			if (count > 1) {
				int count1 = nomeTok.countTokens() - 1;
				String nomeAbreviado = nomeTok.nextToken() + " " + nomeTok.nextToken() ;
				for (int i = 2; i < count; i++){
					String aux = nomeTok.nextToken();
					if(aux.equals("DE") || aux.equals("DA") || aux.equals("DO") || aux.equals("DOS") )
						nomeAbreviado += " " + aux;
					else
						if(i != count1 ){
							nomeAbreviado += " " + aux.charAt(0);
							nomeAbreviado += ".";
						}
						else
							nomeAbreviado += " " + aux;
				}
				return nomeAbreviado;
			} else {
				return nome;
			}
		}else
			return "";
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_cadastro")
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	@Transient
	public PoloCurso getPolo() {
		return polo;
	}

	public void setPolo(PoloCurso polo) {
		this.polo = polo;
	}

	/**
	 * Método responsável por iniciar atributos iniciais do objeto pessoa.
	 */
	public void prepararDados() {

	
		// País
		if( isEmpty( this.getPais() ) ) {
			if( this.isInternacional() )
				this.setPais( new Pais(Pais.NAO_INFORMADO) );
			else
				this.setPais( new Pais(Pais.BRASIL) );
		}

		// Unidade Federativa
		if( this.getUnidadeFederativa() == null ) {
			this.setUnidadeFederativa( new UnidadeFederativa() );
		} else {
			this.getUnidadeFederativa().getSigla();
		}
			

		// Endereço de Contato
		if( this.getEnderecoContato() == null  ) {
			this.setEnderecoContato( new Endereco() );
		}

		if ( this.getEnderecoContato().getMunicipio() == null  ) {
			this.getEnderecoContato().setMunicipio( new Municipio() );
		} else {
			// evitar a mesma referência à dois objetos Municipios (bug do hibernate)
			Municipio m = new Municipio(getEnderecoContato().getMunicipio().getId(), getEnderecoContato().getMunicipio().getNome());
			m.setUnidadeFederativa(getEnderecoContato().getMunicipio().getUnidadeFederativa());
			this.getEnderecoContato().setMunicipio(m);
		}

		if ( this.getEnderecoContato().getUnidadeFederativa() == null  ) {
			this.getEnderecoContato().setUnidadeFederativa( new UnidadeFederativa() );
		}

		if( this.getEnderecoContato().getTipoLogradouro() == null )
			this.getEnderecoContato().setTipoLogradouro( new TipoLogradouro() );

		if( this.getTipoRaca() == null ) {
			this.setTipoRaca( new TipoRaca() );
		}

		if ( this.getIdentidade() == null) {
			this.setIdentidade(new Identidade());
			this.getIdentidade().setUnidadeFederativa(new UnidadeFederativa());
		} else if (this.getIdentidade().getUnidadeFederativa() == null) {
			this.getIdentidade().setUnidadeFederativa(new UnidadeFederativa());
		} else {
			this.getIdentidade().getUnidadeFederativa().getSigla();
		}

		if ( this.getTituloEleitor() == null) {
			this.setTituloEleitor(new TituloEleitor());
		} else if (this.getTituloEleitor().getUnidadeFederativa() == null) {
			this.getTituloEleitor().setUnidadeFederativa(new UnidadeFederativa());
		} else {
			this.getTituloEleitor().getUnidadeFederativa().getSigla();	
		}

		if( this.getEstadoCivil() == null ) {
			this.setEstadoCivil(new EstadoCivil());
		} else {
			this.getEstadoCivil().getDescricao();
		}

		if (this.getMunicipio() == null ) {
			this.setMunicipio(new Municipio());
		} else {
			this.getMunicipio().getNomeUF();
		}

		if (this.getContaBancaria() == null) {
		    this.setContaBancaria( new ContaBancaria() );
		} else {
			this.contaBancaria.getBanco().getCodigoNome();
		}

		if( this.getTipoNecessidadeEspecial() == null )
			this.setTipoNecessidadeEspecial(new TipoNecessidadeEspecial());
		else
			this.getTipoNecessidadeEspecial().getDescricao();
		
		if (this.getCertificadoMilitar() == null) {
			this.setCertificadoMilitar(new CertificadoMilitar());
		} else {
			this.getCertificadoMilitar().getNumero();
		}
		
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

	}

	@Column(name = "segundograuanoconclusao", unique = false, nullable = true, insertable = true, updatable = true)
	public String getAnoConclusaoEnsinoMedio() {
		return this.anoConclusaoEnsinoMedio;
	}

	public void setAnoConclusaoEnsinoMedio(String anoConclusaoEnsinoMedio) {
		this.anoConclusaoEnsinoMedio = anoConclusaoEnsinoMedio;
	}

	@Column(name = "segundograuescola", unique = false, nullable = true, insertable = true, updatable = true)
	public String getEscolaConclusaoEnsinoMedio() {
		return this.escolaConclusaoEnsinoMedio;
	}

	public void setEscolaConclusaoEnsinoMedio(String escolaConclusaoEnsinoMedio) {
		this.escolaConclusaoEnsinoMedio = escolaConclusaoEnsinoMedio;
	}

	@Override
	@Column(name = "internacional", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isInternacional() {
		return super.isInternacional();
	}

	/** Método responsável por retornar apenas o primeiro nome. */
	@Transient
	public String getPrimeiroNome() {
		String[] nomes = nome.split(" ");
		return nomes[0];
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_tipo_necessidade_especial")
	public TipoNecessidadeEspecial getTipoNecessidadeEspecial() {
		return tipoNecessidadeEspecial;
	}

	public void setTipoNecessidadeEspecial(TipoNecessidadeEspecial tipoNecessidadeEspecial) {
		this.tipoNecessidadeEspecial = tipoNecessidadeEspecial;
	}

	@Override
	@Column(name = "nome_ascii")
	public String getNomeAscii() {
		return nomeAscii;
	}

	@Override
	public void setNomeAscii(String nomeAscii) {
		if ( nomeAscii != null)
			this.nomeAscii = StringUtils.toAscii(nomeAscii.toUpperCase());
	}

	/**
	 * Validar somente os dados de contato
	 * 
	 * @return
	 */
	public ListaMensagens validarDadosContato() {
		ListaMensagens lista = new ListaMensagens();

		validateRequired(enderecoContato.getLogradouro(), "Logradouro", lista);
		validateRequired(enderecoContato.getNumero(), "Número", lista);
		validateRequired(enderecoContato.getBairro(), "Bairro", lista);
		validateRequired(enderecoContato.getUnidadeFederativa(), "Unidade Federativa", lista);
		validateRequired(enderecoContato.getMunicipio(), "Município", lista);
		if (enderecoContato.getCep() != null)
			validateMaxLength(enderecoContato.getCep(), 10, "CEP", lista);
		validateEmailRequired(email, "E-mail", lista);
		
		return lista;
	}

	@Column(name="tipo_sanguineo")
	public String getTipoSanguineo() {
		return tipoSanguineo;
	}

	public void setTipoSanguineo(String tipoSanguineo) {
		this.tipoSanguineo = tipoSanguineo;
	}
	
	@Column(name="pais_nacionalidade")
	public String getPaisNacionalidade() {
		return paisNacionalidade;
	}

	public void setPaisNacionalidade(String paisNacionalidade) {
		this.paisNacionalidade = paisNacionalidade;
	}

	/** 
	 * Retorna nome formatado para o ajax.
	 * @return
	 */
	@Transient
	public String getNomeFormatado() {
		return "<div style='border-bottom: dashed 1px; padding: 2px'><div float: left; text-align: right'>"
		+ getNome() + "</div><div style='margin-left: 25px; font-size: x-small;'>"
		+ "</div> </div>";
	}
	
	/** Retorna cpf da pessoa concatenado com nome formatado para o ajax.
	 * @return
	 */
	@Transient
	public String getCpfNomeFormatado() {
		return "<div style='border-bottom: dashed 1px; padding: 2px'><div float: left; text-align: right'>"
		+ ( !isEmpty(getCpf_cnpj()) ? getCpfCnpjFormatado() + " - " : "" )
		+ ( isEmpty(getCpf_cnpj()) && !isEmpty(getPassaporte()) ? getPassaporte() + " - " : "" )
		+ getNome() + "</div></div>";
	}	

	@Transient
	public PerfilPessoa getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilPessoa perfil) {
		this.perfil = perfil;
	}

	@Column(name="nome_oficial")
	public String getNomeOficial() {
		return nomeOficial;
	}

	public void setNomeOficial(String nomeOficial) {
		this.nomeOficial = nomeOficial;
	}

}