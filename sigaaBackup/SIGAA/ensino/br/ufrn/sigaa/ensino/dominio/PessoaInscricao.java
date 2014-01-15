/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
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
 * processo de seleção.
 *
 * @author Ricardo Wendell
 */
@Entity
@Table(name="pessoa_inscricao", schema="ensino")
public class PessoaInscricao implements PersistDB, Validatable{

	/** Chave primária. */
	private int id;

	/** Nome do candidato. */
	private String nome;
	
	/** CPF do candidato. */
	private Long cpf;
	
	/** E-mail do candidato. */
	private String email;
	
	/** Nome da mãe do candidato. */
	private String nomeMae;
	
	/** Nome do pai do candidato. */
	private String nomePai;
	
	/** Gênero do candidato. */
	private char sexo;
	
	/** Data de nascimento do candidato. */
	private Date dataNascimento;
	
	/** Estado civil do candidato. */
	private EstadoCivil estadoCivil = new EstadoCivil();
	
	/** Tipo da raça do candidato.*/
	private TipoRaca tipoRaca;
	
	/** Indica se a pessoa é estrangeira (implica na validação do CPF ou do Passaporte). */
	private boolean estrangeiro;
	
	/** País de naturalidade do candidato.*/
	private Pais pais = new Pais();
	
	/** Unidade Federativa de natalidade do candidato. */
	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa();
	
	/** Município de natalidade do candidato. */
	private Municipio municipio = new Municipio();
	
	/** Município de natalidade do candidato, caso seja outro que não os cadastrados no SIGAA. */
	private String municipioNaturalidadeOutro;
	
	/** Dados referentes ao Documento de Identificação do candidato. */
	private Identidade identidade;
	
	/** Dados referentes ao Título de Eleitor do candidato. */
	private TituloEleitor tituloEleitor = new TituloEleitor();
	
	/** Dados referentes ao Passaporte do candidato. */	
	private String passaporte;
	
	/** Endereço residencial do candidato. */
	private Endereco enderecoResidencial = new Endereco();
	
	/** Código de área nacional do telefone fixo de contato do candidato. */
	protected Short codigoAreaNacionalTelefoneFixo;
	
	/** Código de área nacional do telefone móvel de contato do candidato. */
	protected Short codigoAreaNacionalTelefoneCelular;
	
	/** Número do telefone fixo de contato do candidato. */
	protected String telefone;
	
	/** Número do telefone móvel de contato do candidato. */
	protected String celular;	
	
	/** Data em que o candidato se cadastrou. */
	@CriadoEm
	private Date dataCadastro;
	
	/** Construtor padrão.*/
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
		ValidatorUtil.validateRequired(nomeMae, "Nome da Mãe", erros);
		ValidatorUtil.validateAbreviacao(nomeMae, "Nome da Mãe", erros);
		ValidatorUtil.validateAbreviacao(nomePai, "Nome do Pai", erros);
		ValidatorUtil.validateRequired(sexo, "Sexo", erros);
		ValidatorUtil.validateRequired(dataNascimento, "Data de Nascimento", erros);
		//ValidatorUtil.validateBirthday(dataNascimento, "Data de Nascimento", erros);
		ValidatorUtil.validateRequiredId(estadoCivil.getId(), "Estado Civil", erros);
		//ValidatorUtil.validateRequiredId(tipoRaca.getId(), "Etnia", erros);
		
		//Naturalidade
		ValidatorUtil.validateRequiredId(pais.getId(), "País", erros);
			
		if (pais.getId() == Pais.BRASIL) {
			ValidatorUtil.validateRequiredId(municipio.getId(), "Município de Naturalidade", erros);
			ValidatorUtil.validateRequiredId(unidadeFederativa.getId(), "UF de Naturalidade", erros);
		}else
			ValidatorUtil.validateRequired(municipioNaturalidadeOutro, "Município de Naturalidade", erros);

		//Documentos
		if ( !isEstrangeiro() ){ 
			ValidatorUtil.validateRequired(identidade.getNumero(), "RG", erros);
			ValidatorUtil.validateRequired(identidade.getOrgaoExpedicao(), "Órgão de Expedição", erros);
			ValidatorUtil.validateRequired(identidade.getUnidadeFederativa().getId(), "UF de Expedição do Documento", erros);
			ValidatorUtil.validateRequired(identidade.getDataExpedicao(), "Data de Expedição", erros);
		}
		
		if( !ValidatorUtil.isEmpty(dataNascimento) )
			ValidatorUtil.validateBirthday(dataNascimento, "Data de Nascimento", erros);
		
		//Endereço
		if ( !isEstrangeiro() ){ 
			
			ValidatorUtil.validateRequired(enderecoResidencial.getCep(),"CEP", erros);
			if(enderecoResidencial.getCep() != null && enderecoResidencial.getCep().length()<9)
				erros.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "CEP");
			
			ValidatorUtil.validateRequiredId(enderecoResidencial.getTipoLogradouro().getId(), "Tipo Logradouro", erros);
			ValidatorUtil.validateRequired(enderecoResidencial.getLogradouro(), "Logradouro", erros);
			ValidatorUtil.validateRequired(enderecoResidencial.getNumero(), "Número da Residência", erros);
			ValidatorUtil.validateRequired(enderecoResidencial.getBairro(), "Bairro", erros);
			ValidatorUtil.validateRequired(enderecoResidencial.getUnidadeFederativa().getId(), "UF do Endereço", erros);
			ValidatorUtil.validateRequiredId(enderecoResidencial.getMunicipio().getId(), "Município do Endereço", erros);
		}
		
		if(!ValidatorUtil.isEmpty(getTelefone()))
			ValidatorUtil.validateRequired(codigoAreaNacionalTelefoneFixo, "Codigo de Area", erros);
		
		if(!ValidatorUtil.isEmpty(getCelular()))
			ValidatorUtil.validateRequired(codigoAreaNacionalTelefoneCelular, "Codigo de Area", erros);

			
		return erros;
	}
	
	/**
	 * Esse método consiste na formatação dos dados para que posteriormente seja usado. 
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
	 * Instância objetos necessários e define valores padrão para alguns deles.
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
	
	/** Retorna a chave primária. 
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	public int getId() {
		return this.id;
	}

	/** Seta a chave primária.
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
	 * Foramta o CPF para ser exibido na listagem e relatórios
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

	/** Retorna o nome da mãe do candidato. 
	 * @return
	 */
	@Column(name="nome_mae")
	public String getNomeMae() {
		return this.nomeMae;
	}

	/** Seta o nome da mãe do candidato.
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

	/** Retorna o gênero do candidato.
	 * @return
	 */
	public char getSexo() {
		return this.sexo;
	}

	/** Seta o gênero do candidato. 
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

	/** Retorna o tipo da raça do candidato.
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_raca")	
	public TipoRaca getTipoRaca() {
		return this.tipoRaca;
	}

	/** Seta o tipo da raça do candidato.
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
	
	/** Retorna o endereço residencial do candidato. 
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name="id_endereco_residencial")	
	public Endereco getEnderecoResidencial() {
		return this.enderecoResidencial;
	}

	/** Seta o endereço residencial do candidato.
	 * @param enderecoResidencial
	 */
	public void setEnderecoResidencial(Endereco enderecoResidencial) {
		this.enderecoResidencial = enderecoResidencial;
	}
	
	/** Retorna o código de área nacional do telefone fixo de contato do candidato.
	 * @return
	 */
	@Column(name="codigo_area_nacional_telefone_fixo")
	public Short getCodigoAreaNacionalTelefoneFixo() {
		return this.codigoAreaNacionalTelefoneFixo;
	}

	/** Seta o código de área nacional do telefone fixo de contato do candidato. 
	 * @param codigoAreaNacionalTelefoneFixo
	 */
	public void setCodigoAreaNacionalTelefoneFixo(
			Short codigoAreaNacionalTelefoneFixo) {
		this.codigoAreaNacionalTelefoneFixo = codigoAreaNacionalTelefoneFixo;
	}

	/** Retorna o código de área nacional do telefone móvel de contato do candidato.
	 * @return
	 */
	@Column(name="codigo_area_nacional_telefone_celular")
	public Short getCodigoAreaNacionalTelefoneCelular() {
		return this.codigoAreaNacionalTelefoneCelular;
	}

	/** Seta o código de área nacional do telefone móvel de contato do candidato. 
	 * @param codigoAreaNacionalTelefoneCelular
	 */
	public void setCodigoAreaNacionalTelefoneCelular(
			Short codigoAreaNacionalTelefoneCelular) {
		this.codigoAreaNacionalTelefoneCelular = codigoAreaNacionalTelefoneCelular;
	}

	/** Retorna o número do telefone fixo de contato do candidato.
	 * @return
	 */
	@Column(name = "telefone_fixo", unique = true, nullable = true, insertable = true, updatable = true)
	public String getTelefone() {
		return this.telefone;
	}

	/** Seta o número do telefone fixo de contato do candidato. 
	 * @param telefone
	 */
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	/** Retorna o número do telefone móvel de contato do candidato.
	 * @return
	 */
	@Column(name = "telefone_celular", unique = true, nullable = true, insertable = true, updatable = true)
	public String getCelular() {
		return this.celular;
	}

	/** Seta o número do telefone móvel de contato do candidato. 
	 * @param celular
	 */
	public void setCelular(String celular) {
		this.celular = celular;
	}

	/** Retorna o país de naturalidade do candidato.
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pais_nacionalidade", unique = false, nullable = true, insertable = true, updatable = true)	
	public Pais getPais() {
		return this.pais;
	}

	/** Seta o país de naturalidade do candidato.
	 * @param pais
	 */
	public void setPais(Pais pais) {
		this.pais = pais;
	}

	/** Retorna o município de natalidade do candidato. 
	 * @return
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_municipio_naturalidade", unique = false, nullable = true, insertable = true, updatable = true)	
	public Municipio getMunicipio() {
		return this.municipio;
	}

	/** Seta o município de natalidade do candidato.
	 * @param municipio
	 */
	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	/** Retorna o município de natalidade do candidato, caso seja outro que não os cadastrados no SIGAA.
	 * @return
	 */
	@Column(name = "municipio_naturalidade_outro", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getMunicipioNaturalidadeOutro() {
		return this.municipioNaturalidadeOutro;
	}

	/** Seta o município de natalidade do candidato, caso seja outro que não os cadastrados no SIGAA. 
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

	/** Retorna os dados referentes ao Documento de Identificação do candidato.
	 * @return
	 */
	@Embedded
	public Identidade getIdentidade() {
		return this.identidade;
	}

	/** Seta os dados referentes ao Documento de Identificação do candidato. 
	 * @param identidade
	 */
	public void setIdentidade(Identidade identidade) {
		this.identidade = identidade;
	}

	/** Retorna os dados referentes ao Título de Eleitor do candidato.
	 * @return
	 */
	@Embedded
	public TituloEleitor getTituloEleitor() {
		return this.tituloEleitor;
	}

	/** Seta os dados referentes ao Título de Eleitor do candidato. 
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
	 * Converte os dados pessoais de uma inscrição para Pessoa
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

	/** Indica se a pessoa é estrangeira (implica na validação do CPF ou do Passaporte). 
	 * @return
	 */
	@Transient
	public boolean isEstrangeiro() {
		return estrangeiro;
	}
	
	

	/** Seta se a pessoa é estrangeira (implica na validação do CPF ou do Passaporte).
	 * @param estrangeiro
	 */
	public void setEstrangeiro(boolean estrangeiro) {
		this.estrangeiro = estrangeiro;
	}
	
}
