/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
 * Created on 14/12/2007
*/
package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Endereco;
import br.ufrn.sigaa.pessoa.dominio.EstadoCivil;
import br.ufrn.sigaa.pessoa.dominio.Identidade;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.TipoLogradouro;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;
import br.ufrn.sigaa.pessoa.dominio.TituloEleitor;

/**
 * Classe que representa pessoas inscritas em um
 * processo de sele��o.
 *
 * @author Ricardo Wendell
 */
@Entity
@Table(name="pessoa_inscricao", schema="ensino")
public class PessoaInscricao implements PersistDB, Validatable{

	/** Chave prim�ria. */
	private int id;

	/** Nome do candidato. */
	private String nome;
	
	/** CPF do candidato. */
	private Long cpf;
	
	/** E-mail do candidato. */
	private String email;
	
	/** Nome da m�e do candidato. */
	private String nomeMae;
	
	/** Nome do pai do candidato. */
	private String nomePai;
	
	/** G�nero do candidato. */
	private char sexo;
	
	/** Data de nascimento do candidato. */
	private Date dataNascimento;
	
	/** Estado civil do candidato. */
	private EstadoCivil estadoCivil = new EstadoCivil();
	
	/** Tipo da ra�a do candidato.*/
	private TipoRaca tipoRaca;
	
	/** Indica se a pessoa � estrangeira (implica na valida��o do CPF ou do Passaporte). */
	private boolean estrangeiro;
	
	/** Pa�s de naturalidade do candidato.*/
	private Pais pais = new Pais();
	
	/** Unidade Federativa de natalidade do candidato. */
	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa();
	
	/** Munic�pio de natalidade do candidato. */
	private Municipio municipio = new Municipio();
	
	/** Munic�pio de natalidade do candidato, caso seja outro que n�o os cadastrados no SIGAA. */
	private String municipioNaturalidadeOutro;
	
	/** Dados referentes ao Documento de Identifica��o do candidato. */
	private Identidade identidade;
	
	/** Dados referentes ao T�tulo de Eleitor do candidato. */
	private TituloEleitor tituloEleitor = new TituloEleitor();
	
	/** Dados referentes ao Passaporte do candidato. */	
	private String passaporte;
	
	/** Endere�o residencial do candidato. */
	private Endereco enderecoResidencial = new Endereco();
	
	/** C�digo de �rea nacional do telefone fixo de contato do candidato. */
	protected Short codigoAreaNacionalTelefoneFixo;
	
	/** C�digo de �rea nacional do telefone m�vel de contato do candidato. */
	protected Short codigoAreaNacionalTelefoneCelular;
	
	/** N�mero do telefone fixo de contato do candidato. */
	protected String telefone;
	
	/** N�mero do telefone m�vel de contato do candidato. */
	protected String celular;	
	
	/** Data em que o candidato se cadastrou. */
	@CriadoEm
	private Date dataCadastro;
	
	/** Construtor padr�o.*/
	public PessoaInscricao() {
		setEstrangeiro(false);
	}
	
	/** Valida os dados pessoais do candidato.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		
		//Dados Pessoais
		if (isEstrangeiro()) {
			ValidatorUtil.validateRequired(passaporte, "Passaporte", erros);
		} else {
			ValidatorUtil.validateRequired(cpf, "CPF", erros);
			if(!isEmpty(cpf))
				ValidatorUtil.validateCPF_CNPJ(cpf, "CPF", erros);
		}
		ValidatorUtil.validateRequired(nome, "Nome", erros);
		ValidatorUtil.validateAbreviacao(nome, "Nome", erros);
		ValidatorUtil.validateRequired(email, "E-mail", erros);
		ValidatorUtil.validateEmail(email, "E-Mail", erros);		
		ValidatorUtil.validateRequired(nomeMae, "Nome da M�e", erros);
		ValidatorUtil.validateAbreviacao(nomeMae, "Nome da M�e", erros);
		ValidatorUtil.validateAbreviacao(nomePai, "Nome do Pai", erros);
		ValidatorUtil.validateRequired(sexo, "Sexo", erros);
		ValidatorUtil.validateRequired(dataNascimento, "Data de Nascimento", erros);
		//ValidatorUtil.validateBirthday(dataNascimento, "Data de Nascimento", erros);
		ValidatorUtil.validateRequiredId(estadoCivil.getId(), "Estado Civil", erros);
		//ValidatorUtil.validateRequiredId(tipoRaca.getId(), "Etnia", erros);
		
		//Naturalidade
		ValidatorUtil.validateRequiredId(pais.getId(), "Pa�s", erros);
			
		if (pais.getId() == Pais.BRASIL) {
			ValidatorUtil.validateRequiredId(municipio.getId(), "Munic�pio de Naturalidade", erros);
			ValidatorUtil.validateRequiredId(unidadeFederativa.getId(), "UF de Naturalidade", erros);
		}else
			ValidatorUtil.validateRequired(municipioNaturalidadeOutro, "Munic�pio de Naturalidade", erros);

		//Documentos
		if ( !isEstrangeiro() ){ 
			ValidatorUtil.validateRequired(identidade.getNumero(), "RG", erros);
			ValidatorUtil.validateRequired(identidade.getOrgaoExpedicao(), "�rg�o de Expedi��o", erros);
			ValidatorUtil.validateRequired(identidade.getUnidadeFederativa().getId(), "UF de Expedi��o do Documento", erros);
			ValidatorUtil.validateRequired(identidade.getDataExpedicao(), "Data de Expedi��o", erros);
		}
		
		if( !ValidatorUtil.isEmpty(dataNascimento) )
			ValidatorUtil.validateBirthday(dataNascimento, "Data de Nascimento", erros);
		
		//Endere�o
		if ( !isEstrangeiro() ){ 
			
			ValidatorUtil.validateRequired(enderecoResidencial.getCep(),"CEP", erros);
			if(enderecoResidencial.getCep() != null && enderecoResidencial.getCep().length()<9)
				erros.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "CEP");
			
			ValidatorUtil.validateRequiredId(enderecoResidencial.getTipoLogradouro().getId(), "Tipo Logradouro", erros);
			ValidatorUtil.validateRequired(enderecoResidencial.getLogradouro(), "Logradouro", erros);
			ValidatorUtil.validateRequired(enderecoResidencial.getNumero(), "N�mero da Resid�ncia", erros);
			ValidatorUtil.validateRequired(enderecoResidencial.getBairro(), "Bairro", erros);
			ValidatorUtil.validateRequired(enderecoResidencial.getUnidadeFederativa().getId(), "UF do Endere�o", erros);
			ValidatorUtil.validateRequiredId(enderecoResidencial.getMunicipio().getId(), "Munic�pio do Endere�o", erros);
		}
		
		if(!ValidatorUtil.isEmpty(getTelefone()))
			ValidatorUtil.validateRequired(codigoAreaNacionalTelefoneFixo, "Codigo de Area", erros);
		
		if(!ValidatorUtil.isEmpty(getCelular()))
			ValidatorUtil.validateRequired(codigoAreaNacionalTelefoneCelular, "Codigo de Area", erros);

			
		return erros;
	}
	
	/**
	 * Esse m�todo consiste na formata��o dos dados para que posteriormente seja usado. 
	 */
	public void formatarDados() {
		if (getNome() != null) {
			setNome( getNome().toUpperCase().trim() );
		}
		if (getNomeMae() != null) {
			setNomeMae( getNomeMae().toUpperCase().trim() );
		}
		if (getNomePai() != null) {
			setNomePai( getNomePai().toUpperCase().trim() );
		}
		if (getEnderecoResidencial() != null && getEnderecoResidencial().getLogradouro() != null) {
			getEnderecoResidencial().setLogradouro( getEnderecoResidencial().getLogradouro().toUpperCase().trim() );
		}
		if (getEnderecoResidencial() != null && getEnderecoResidencial().getBairro() != null) {
			getEnderecoResidencial().setBairro( getEnderecoResidencial().getBairro().toUpperCase().trim() );
		}
		if (getPais().getId() != Pais.BRASIL) {
			setMunicipio(null);
			setUnidadeFederativa(null);
		}		
	}	
	
	/**
	 * Inst�ncia objetos necess�rios e define valores padr�o para alguns deles.
	 */
	public void clear() {
		
		int cepPadrao =  ParametroHelper.getInstance().getParametroInt( ConstantesParametroGeral.CEP_PADRAO );
		
		if (this.getSexo() != 'M' && this.getSexo() != 'F')
			this.setSexo('M');
		if (this.getUnidadeFederativa() == null || this.getUnidadeFederativa().getId() == 0)
			this.setUnidadeFederativa(new UnidadeFederativa());
		if (this.getMunicipio() == null || this.getMunicipio().getId() == 0)
			this.setMunicipio(new Municipio());
		if (this.getEnderecoResidencial() == null || this.getEnderecoResidencial().getId() == 0) {
			this.setEnderecoResidencial(new Endereco());
			this.getEnderecoResidencial().setUnidadeFederativa(new UnidadeFederativa());
			this.getEnderecoResidencial().setCep(cepPadrao + "");
			this.getEnderecoResidencial().setTipoLogradouro(new TipoLogradouro());
			this.getEnderecoResidencial().setMunicipio(new Municipio());
		}
		
		if (getEnderecoResidencial() != null && getEnderecoResidencial().getUnidadeFederativa() == null) {
			this.getEnderecoResidencial().setUnidadeFederativa(new UnidadeFederativa(UnidadeFederativa.ID_UF_PADRAO));
		}
		
		if (getEnderecoResidencial() != null && getEnderecoResidencial().getMunicipio() == null) {
			this.getEnderecoResidencial().setMunicipio(new Municipio());
		}
		
		if (this.getPais() == null || this.getPais().getId() == 0)
			this.setPais(new Pais(Pais.BRASIL));
		
		
		if (this.getIdentidade() == null ) {
			this.setIdentidade(new Identidade());
			this.getIdentidade().setDataExpedicao(null);
			this.getIdentidade().setUnidadeFederativa(new UnidadeFederativa());
		}
		
		if ( this.getTituloEleitor() == null) {
			this.setTituloEleitor(new TituloEleitor());
		} else if (this.getTituloEleitor().getUnidadeFederativa() == null) {
			this.getTituloEleitor().setUnidadeFederativa(new UnidadeFederativa());
		}		
		
		if (this.getTipoRaca() == null)
			this.setTipoRaca(new TipoRaca());
		if (this.getEstadoCivil() == null)
			this.setEstadoCivil(new EstadoCivil(-1));
	}	
	
	/** Retorna a chave prim�ria. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	public int getId() {
		return this.id;
	}

	/** Seta a chave prim�ria.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna o CPF do candidato. 
	 * @return
	 */
	public Long getCpf() {
		return this.cpf;
	}

	/**
	 * Foramta o CPF para ser exibido na listagem e relat�rios
	 * @return
	 */
	@Transient
	public String getCpfFormatado(){
		if(this.cpf != null)
			return Formatador.getInstance().formatarCPF(this.cpf);	
		return null;
	}
	
	/** Seta o CPF do candidato. 
	 * @param cpf
	 */
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	/** Retorna o nome do candidato.
	 * @return
	 */
	public String getNome() {
		return this.nome;
	}

	/** Seta o nome do candidato. 
	 * @param nome
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	/** Retorna o E-mail do candidato.
	 * @return
	 */
	public String getEmail() {
		return this.email;
	}

	/** Seta o E-mail do candidato. 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/** Retorna o nome da m�e do candidato. 
	 * @return
	 */
	@Column(name="nome_mae")
	public String getNomeMae() {
		return this.nomeMae;
	}

	/** Seta o nome da m�e do candidato.
	 * @param nomeMae
	 */
	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	/** Retorna o nome do pai do candidato.
	 * @return
	 */
	@Column(name="nome_pai")
	public String getNomePai() {
		return this.nomePai;
	}

	/** Seta o nome do pai do candidato. 
	 * @param nomePai
	 */
	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	/** Retorna o g�nero do candidato.
	 * @return
	 */
	public char getSexo() {
		return this.sexo;
	}

	/** Seta o g�nero do candidato. 
	 * @param sexo
	 */
	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	/** Retorna a data de nascimento do candidato. 
	 * @return
	 */
	@Column(name="data_nascimento")
	@Temporal(TemporalType.DATE)
	public Date getDataNascimento() {
		return this.dataNascimento;
	}

	/** Seta a data de nascimento do candidato.
	 * @param dataNascimento
	 */
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	/** Retorna o estado civil do candidato. 
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_estado_civil")
	public EstadoCivil getEstadoCivil() {
		return this.estadoCivil;
	}

	/** Seta o estado civil do candidato.
	 * @param estadoCivil
	 */
	public void setEstadoCivil(EstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	/** Retorna o tipo da ra�a do candidato.
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_raca")	
	public TipoRaca getTipoRaca() {
		return this.tipoRaca;
	}

	/** Seta o tipo da ra�a do candidato.
	 * @param tipoRaca
	 */
	public void setTipoRaca(TipoRaca tipoRaca) {
		this.tipoRaca = tipoRaca;
	}

	/** Retorna a data em que o candidato se cadastrou. 
	 * @return
	 */
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCadastro() {
		return this.dataCadastro;
	}

	/** Seta a data em que o candidato se cadastrou.
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
	/** Retorna o endere�o residencial do candidato. 
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name="id_endereco_residencial")	
	public Endereco getEnderecoResidencial() {
		return this.enderecoResidencial;
	}

	/** Seta o endere�o residencial do candidato.
	 * @param enderecoResidencial
	 */
	public void setEnderecoResidencial(Endereco enderecoResidencial) {
		this.enderecoResidencial = enderecoResidencial;
	}
	
	/** Retorna o c�digo de �rea nacional do telefone fixo de contato do candidato.
	 * @return
	 */
	@Column(name="codigo_area_nacional_telefone_fixo")
	public Short getCodigoAreaNacionalTelefoneFixo() {
		return this.codigoAreaNacionalTelefoneFixo;
	}

	/** Seta o c�digo de �rea nacional do telefone fixo de contato do candidato. 
	 * @param codigoAreaNacionalTelefoneFixo
	 */
	public void setCodigoAreaNacionalTelefoneFixo(
			Short codigoAreaNacionalTelefoneFixo) {
		this.codigoAreaNacionalTelefoneFixo = codigoAreaNacionalTelefoneFixo;
	}

	/** Retorna o c�digo de �rea nacional do telefone m�vel de contato do candidato.
	 * @return
	 */
	@Column(name="codigo_area_nacional_telefone_celular")
	public Short getCodigoAreaNacionalTelefoneCelular() {
		return this.codigoAreaNacionalTelefoneCelular;
	}

	/** Seta o c�digo de �rea nacional do telefone m�vel de contato do candidato. 
	 * @param codigoAreaNacionalTelefoneCelular
	 */
	public void setCodigoAreaNacionalTelefoneCelular(
			Short codigoAreaNacionalTelefoneCelular) {
		this.codigoAreaNacionalTelefoneCelular = codigoAreaNacionalTelefoneCelular;
	}

	/** Retorna o n�mero do telefone fixo de contato do candidato.
	 * @return
	 */
	@Column(name = "telefone_fixo", unique = true, nullable = true, insertable = true, updatable = true)
	public String getTelefone() {
		return this.telefone;
	}

	/** Seta o n�mero do telefone fixo de contato do candidato. 
	 * @param telefone
	 */
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	/** Retorna o n�mero do telefone m�vel de contato do candidato.
	 * @return
	 */
	@Column(name = "telefone_celular", unique = true, nullable = true, insertable = true, updatable = true)
	public String getCelular() {
		return this.celular;
	}

	/** Seta o n�mero do telefone m�vel de contato do candidato. 
	 * @param celular
	 */
	public void setCelular(String celular) {
		this.celular = celular;
	}

	/** Retorna o pa�s de naturalidade do candidato.
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pais_nacionalidade", unique = false, nullable = true, insertable = true, updatable = true)	
	public Pais getPais() {
		return this.pais;
	}

	/** Seta o pa�s de naturalidade do candidato.
	 * @param pais
	 */
	public void setPais(Pais pais) {
		this.pais = pais;
	}

	/** Retorna o munic�pio de natalidade do candidato. 
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_municipio_naturalidade", unique = false, nullable = true, insertable = true, updatable = true)	
	public Municipio getMunicipio() {
		return this.municipio;
	}

	/** Seta o munic�pio de natalidade do candidato.
	 * @param municipio
	 */
	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	/** Retorna o munic�pio de natalidade do candidato, caso seja outro que n�o os cadastrados no SIGAA.
	 * @return
	 */
	@Column(name = "municipio_naturalidade_outro", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getMunicipioNaturalidadeOutro() {
		return this.municipioNaturalidadeOutro;
	}

	/** Seta o munic�pio de natalidade do candidato, caso seja outro que n�o os cadastrados no SIGAA. 
	 * @param municipioNaturalidadeOutro
	 */
	public void setMunicipioNaturalidadeOutro(String municipioNaturalidadeOutro) {
		this.municipioNaturalidadeOutro = municipioNaturalidadeOutro;
	}

	/** Retorna a unidade Federativa de natalidade do candidato.
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_uf_naturalidade", unique = false, nullable = true, insertable = true, updatable = true)
	public UnidadeFederativa getUnidadeFederativa() {
		return this.unidadeFederativa;
	}

	/** Seta a unidade Federativa de natalidade do candidato. 
	 * @param unidadeFederativa
	 */
	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	/** Retorna os dados referentes ao Documento de Identifica��o do candidato.
	 * @return
	 */
	@Embedded
	public Identidade getIdentidade() {
		return this.identidade;
	}

	/** Seta os dados referentes ao Documento de Identifica��o do candidato. 
	 * @param identidade
	 */
	public void setIdentidade(Identidade identidade) {
		this.identidade = identidade;
	}

	/** Retorna os dados referentes ao T�tulo de Eleitor do candidato.
	 * @return
	 */
	@Embedded
	public TituloEleitor getTituloEleitor() {
		return this.tituloEleitor;
	}

	/** Seta os dados referentes ao T�tulo de Eleitor do candidato. 
	 * @param tituloEleitor
	 */
	public void setTituloEleitor(TituloEleitor tituloEleitor) {
		this.tituloEleitor = tituloEleitor;
	}

	/** Retorna os dados referentes ao Passaporte do candidato. 
	 * @return
	 */
	public String getPassaporte() {
		return this.passaporte;
	}

	/** Seta os dados referentes ao Passaporte do candidato.
	 * @param passaporte
	 */
	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	/**
	 * Converte os dados pessoais de uma inscri��o para Pessoa
	 * 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public Pessoa toPessoa() {
		Pessoa pessoa = new Pessoa();
		try {
			BeanUtils.copyProperties(pessoa, this);
			pessoa.setId(0);
			pessoa.setCpf_cnpj(cpf);
			pessoa.setInternacional( !isEmpty(passaporte) && !isEmpty(pais) && !pais.isBrasil() );
			pessoa.setEnderecoContato(enderecoResidencial);
			pessoa.prepararDados();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pessoa;
	}

	/** Indica se a pessoa � estrangeira (implica na valida��o do CPF ou do Passaporte). 
	 * @return
	 */
	@Transient
	public boolean isEstrangeiro() {
		return estrangeiro;
	}
	
	

	/** Seta se a pessoa � estrangeira (implica na valida��o do CPF ou do Passaporte).
	 * @param estrangeiro
	 */
	public void setEstrangeiro(boolean estrangeiro) {
		this.estrangeiro = estrangeiro;
	}
	
}
