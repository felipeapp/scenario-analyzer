/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 24/10/2012
 * 
 */
package br.ufrn.sigaa.extensao.dominio;

import static br.ufrn.arq.mensagens.MensagensArquitetura.FORMATO_INVALIDO;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 *
 * <p>  Representa um cadastro <strong>*** �NICO ***</strong> que as pessoas devem fazer para realizar 
 *  a inscri��o em um curso ou evento de extens�o.</p>
 *
 * <p> <strong>Essa classe � um desmembramento da classe <code>InscricaoAtividadeParticipante</code>, agora l� vai conter 
 * apenas as informa��es da inscri��o em si, as informa��es do participante ficam centralizadas aqui!!!</strong></p>
 *
 *
 * <p> <i> Cada pessoa vai possuir APENAS 1 CadastroParticipanteAtividadeExtensao durante toda a sua vida, 
 *   com uma �nica senha para acessar o sistema criada por ela mesma.  
 *   Quando ela for se inscrever em uma nova atividade de extens�o vai ser criado uma outra entidade 
 *   InscricaoParticipanteAtividadeExtensao.  Que vai fazer o papel da antiga 
 *   InscricaoAtividadeParticipante, contendo os dados para aquela inscri��o exclusiva.
 * </i> </p>
 * 
 * @author jadson
 *
 */
@Entity
@Table(schema = "extensao", name = "cadastro_participante_atividade_extensao")
public class CadastroParticipanteAtividadeExtensao implements Validatable{


	/** O id  */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="extensao.cadastro_participante_atividade_extensao_sequence") })
	@Column(name = "id_cadastro_participante_atividade_extensao", unique = true, nullable = false)
    private int id;
	
	
    /** CPF do participante inscrito. Nulo para o caso do usu�rio ser estrageiro. */
	 @Column(name = "cpf", unique = true, nullable = true)
    private Long cpf;
    
    /** Passaporte do participante caso seja estrangeiro */
    @Column(name = "passaporte", nullable = true)
    private String passaporte;

    /** Nome do participante inscrito. */
    @Column(name = "nome", nullable = false)
    private String nome;

    /** Nome do participante para as buscas */
    @Column(name = "nome_ascii", nullable = false)
    private String nomeAscii;
    
    /** Data de nascimento do participante inscrito. */
    @Temporal(TemporalType.DATE)
    @Column(name = "data_nascimento", nullable = false )
    private Date dataNascimento;

    /** Logradouro do participante inscrito. Pode ser nulo caso o participante seja cadastrado por alguns gestor. */
    @Column(name = "logradouro", nullable = true)
    private String logradouro;
    
    /** N�mero do logradouro participante inscrito. Pode ser nulo caso o participante seja cadastrado por alguns gestor. */
    @Column(name = "numero", nullable = true)
    private String numero;

    /** O complemento do endere�o do participante inscrito. */
    @Column(name = "complemento", nullable = true)
    private String complemento;
    
    /** Bairro do participante inscrito. Pode ser nulo caso o participante seja cadastrado por alguns gestor. */ 
    @Column(name = "bairro", nullable = true)
    private String bairro;
    
    /** Munic�pio do participante inscrito. Pode ser nulo caso o participante seja cadastrado por alguns gestor. */
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_municipio", nullable = true)
    private Municipio municipio = new Municipio();
    
    /** Unidade Federativa onde reside o participante inscrito. Pode ser nulo caso o participante seja cadastrado por alguns gestor. */
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade_federativa", nullable = true)
    private UnidadeFederativa unidadeFederativa;

    /** CEP do local onde reside o participante inscrito.  Pode ser nulo caso o participante seja cadastrado por alguns gestor. */
    @Column(name = "cep", nullable = true)
    private String cep;
    
    /** Email do participante. � usado como "login" na p�gina "privada" do usu�rio na parte 
     * p�blica, j� que a pessoa n�o tem usu�rio no sistema. 
     * e tamb�m para o coordenador entrar em contato. */
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    
    /** Telefone do participante para o coordenador entrar em contato. Guarda os n�meros com a m�scara. */
    @Column(name = "telefone", nullable = true)
    private String telefone;
	
    
	/** Celular do participante para o coordenador entrar em contato. Guarda os n�meros com a m�scara.  */
    @Column(name = "celular", nullable = true)
    private String celular;

	
    /**
     * <p>Senha para o usu�rio ter acesso a parte onde ele pode gerenciar as suas incri��es.</p>
     * 
     * <p>Vai ser na parte p�blica do sigaa mas ele vai ter que se "logar" para ter acesso.</p>
     * 
     * <p>Guarda a senha em MD5</p>
     * 
     * <p><strong>ESSE � O �NICO CAMPO USADO PARA SE AUTENTICAR NO SISTEMA.</strong></p>
     * 
     */
    @Column(name = "senha", nullable = true)
    private String senha;

    
    
    /**
     * <p>Guarda a senha gerada pelo sistema em <strong>TEXTO PURO</strong> para ser enviada para o usu�rio.  
     * Quando o usu�rio se logar ele pode alterar essa senha gerada.</p>
     *  
     * <p>Utilizado nos caso em que o coordenador cadastra o usu�rio, ou no caso que o usu�rio 
     * perder a senha e solicita reenvio. Neste �ltimo caso, o sistema gera uma senha autom�tica e envia.</p>
     * 
     * <p><strong>ESSE CAMPO NUNCA � USADO PARA SE AUTENTICAR NO SISTEMA</strong>. Mesmo nos casos nos quais o sistema gera uma senha 
     * autom�tica o usu�rio precisa confirmar essa altera��o para gerar o hash da senha e colocar no campo "senha".</p> 
     *  
     */
    @Column(name = "senha_gerada", nullable = true)
    private String senhaGerada;
    
    
    /** Caso a pessoa seja extranjeira, tem que possuir obrigatoriamente o passaporte informado, caso seja brasileira o CPF.*/
    @Column(name = "estrangeiro", nullable = false)
    private boolean estrangeiro = false;
    

    
    /** <p>C�digo gerado e salvo para o usu�rio validar o e-mail informado na inscri��o.</p>
     * 
     *  <p>Usado apenas quando o usu�rio faz seu cadastro no sistema ou um pedido de solicita��o de altera��o de senha � feito.</p>
     *  
     *  <p>Caso a inscri��o do participante seja feita pelo coordenador essse c�digo n�o vai exisitir, o 
     *  participante j� vai ser salvo no banco com seu cadastro confirmado.</p>
     */
    @Column(name = "codigo_acesso_confirmacao", nullable = true)
    private String codigoAcessoConfirmacao;
    
    
    /** 
     * Indica se o cadastro foi removido do sistema. Caso haja a necessidade de remover um cadastro 
     * ele ser� desativado, n�o podendo a pessoa acessar mais o sistema nem participar de nenhum curso 
     * ou evento com esse cadastro.
     */
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    
	/** 
	 * Se o cadastro foi confirmado pelo usu�rio (via email). S� vai se logar no sistema quando confirmar. 
	 * 
	 * O usu�rio pode realizar v�rias vezes o cadatro at� confirmar. Para caso ele erre o email pode ser 
	 * cadastrar novamente.
	 */
    @Column(name = "confirmado", nullable = false)
    private boolean confirmado = false;
    
    
    
    
	////////////////////////////INFORMA��ES DE AUDITORIA  ///////////////////////////////////////
	
	
	/**
	 *  *** IMPORTANTE ***: Se for cadastrado pelo pr�prio usu�rio na �rea p�blica, essa coluna vai ser nula.
	 * 
	 * informa��es de quem criou
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/** Data de cadastro no sistema */
    @CriadoEm
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    private Date dataCadastro;
	
	/**
	* registro entrada  do usu�rio que realizou a �ltima atualiza��o
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

    
    /** Data da �ltima atualiza��o do cadastro */
    @AtualizadoEm
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_ultima_atualizacao")
    private Date dataUltimaAtualizacao;
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    public CadastroParticipanteAtividadeExtensao(){}    
    
    public CadastroParticipanteAtividadeExtensao(int id){
    	this.id = id;
    }    
    
    public CadastroParticipanteAtividadeExtensao(int id, Long cpf, String passaporte, String nome, String email){
    	this(id);
    	this.cpf = cpf;
    	this.passaporte = passaporte;
    	setNome(nome);
    	this.email = email;
    }
    
    /**
     * Gera o hash da senha de autentica��o do usu�rio para guardar no banco.
     *
     * @param novaSenha
     * @throws NegocioException 
     */
    public void geraHashSenha(){
    	this.senha = UFRNUtils.toMD5(senha);
    }
    
    
    /**
	 * M�todo utilizado para informar o HashCode
	 * @return
	 */
	public void geraCodigoAcessoConfirmacaoInscricao() {
	    if(isEstrangeiro())
	    	this.codigoAcessoConfirmacao = UFRNUtils.toSHA1Digest("IP"+ getId() + getPassaporte() );
	    else
	    	this.codigoAcessoConfirmacao  = UFRNUtils.toSHA1Digest("IP"+ getId() + getCpf() );
	}
    
	
	
    /** Cont�m a l�gica  de gerar uma nova senha autom�tica para acessar participantes de cursos e evento de extenss�o */
	public void geraSenhaAutomatica(){
		String senhaTemp = UFRNUtils.toMD5(UFRNUtils.geraSenhaAleatoria() );
		if(senhaTemp.length() > 10)
			this.senhaGerada = senhaTemp.substring(0, 10);
		else
			this.senhaGerada = senhaTemp;
	}
	
	/**
	 * Retorna o endere�o completo do participante.
	 * 
	 * logradouro n� complemento bairro, municipio/estado - 59000-000
	 *
	 * @return
	 */
	public String getEnderecoCompleto(){
		return logradouro+" "+numero+" "+complemento+" "+bairro
		+", "+ ( municipio != null ? municipio.getNome() : "" )+"/"+ ( unidadeFederativa != null ? unidadeFederativa.getSigla() : "" )+" - "+cep;
	}
	
	
	
    /**
	 * <p>M�todo utilizao para validar o cadastro de um novo participante.</p>
	 * 
	 * <p>Valida por ***Completo***, geralmente quando o pr�prio participante faz o cadastro.</p> 
	 */
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		
		lista.addAll(validateDadosPessoais());
		lista.addAll(validateEndereco());
		lista.addAll(validateSenha());
		
		
		return lista;
	}
	
	
	/**
	 * M�todo utilizao para validar o cadastro de partricipantes quando algum gestor cadastra. 
	 * Nesse casos alguns dados v�o ficar opcionais.
	 * 
	 */
	public ListaMensagens validateCadastroByGestor() {
		
		ListaMensagens lista = new ListaMensagens();
		
		lista.addAll(validateDadosPessoais());
			
		return lista;
	}
	
	
	/**
	 * M�todo utilizao para validar o cadastro de partricipantes, mas n�o valida o campo senha. Usado na altera��o dos dados pelos gestores.
	 * 
	 * Que podem n�o informa a senha do usu�rio
	 */
	private ListaMensagens validateDadosPessoais() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if(estrangeiro){
			ValidatorUtil.validateRequired(passaporte, "Passaporte", lista); // se o usu�rio � internacional tem que infomar o passaporte obrigatoriamente
		}else{
			if(cpf != null)
				ValidatorUtil.validateCPF_CNPJ(cpf, "CPF", lista); // caso n�o seja deve informar o CPF
			else
				lista.addMensagem(FORMATO_INVALIDO, "CPF");
		}
		
		ValidatorUtil.validateRequired(nome, "Nome Completo", lista);
		
		ValidatorUtil.validateRequired(dataNascimento, "Data de Nascimento", lista);
		
		if(dataNascimento != null){
			Date dataLimite = CalendarUtils.adicionarAnos(new Date(), -1); // a pessoa tem que ter pelo menos 1 ano para participar de uma a��o de extens�o.
			ValidatorUtil.validaDataAnteriorIgual(dataNascimento, dataLimite, "Data de Nascimento", lista);
		}
		
		ValidatorUtil.validateRequired(email, "Email", lista);
		
		return lista;
	}
	
	
	/**
	 * M�todo utilizao para validar o cadastro de partricipantes, mas n�o valida o campo senha. Usado na altera��o dos dados pelos gestores.
	 * 
	 * Que podem n�o informa a senha do usu�rio
	 */
	public ListaMensagens validateSenha() {
		
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(senha, "Senha", lista);
		
		if(senha != null){
			if(senha.length() < 6 || senha.length() > 20){
				lista.addErro("O campo senha deve ser entre 6 a 20 caracteres.");
			}
		}
		
		return lista;
	}
	
	
	/**
	 * M�todo utilizao para validar o cadastro de partricipantes, mas n�o valida o campo senha. Usado na altera��o dos dados pelos gestores.
	 * 
	 * Que podem n�o informa a senha do usu�rio
	 */
	private ListaMensagens validateEndereco() {
		
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(logradouro, "Logradouro", lista);
		ValidatorUtil.validateRequired(numero, "N�mero", lista);
		
		if(StringUtils.isNotEmpty(numero)){
			try{
				Integer.parseInt(numero);
			}catch(NumberFormatException nfe){
				lista.addMensagem(FORMATO_INVALIDO, "N�mero");
			}
		}
		
		ValidatorUtil.validateRequired(bairro, "Bairro", lista);
		ValidatorUtil.validateRequiredId(municipio.getId(), "Munic�pio", lista);
		ValidatorUtil.validateRequiredId(unidadeFederativa.getId(), "UF", lista);
		
		if (ValidatorUtil.validateCEP(cep, "CEP", lista) == 0)
			ValidatorUtil.validateRequired(cep, "CEP", lista);
		
		return lista;
	}
	
	/** Apaga dados que n�o foram preenchidos*/
	public void anulaDadosTransientes(){
		if(cpf == 0) cpf = null;
		if(StringUtils.isEmpty(logradouro)) logradouro = null;
		if(StringUtils.isEmpty(bairro)) bairro = null;
		if(StringUtils.isEmpty(cep)) cep = null;
		if(StringUtils.isEmpty(celular)) celular = null;
		if(StringUtils.isEmpty(telefone)) telefone = null;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CadastroParticipanteAtividadeExtensao other = (CadastroParticipanteAtividadeExtensao) obj;
		if (id != other.id)
			return false;
		return true;
	}
    
	
    /**
     * Ver coment�rios da classe pai.<br/>
     *
     * @see java.lang.Object#toString()
     */
    
    @Override
    public String toString() {
    	return "["+id+"] "+nome+"   ( "+email+" )";
    }
    
    
    
    /////  sets e gets   /////
    
    
    public String getIdentificacaoNome() {
    	String identificacaoNome = "";
    	if(cpf != null )
    		identificacaoNome = cpf.toString();
    	else
    		if(passaporte != null ){
    			identificacaoNome = passaporte;
    		}
    	
		return identificacaoNome + " - " + nome;
	}
    
	public void setId(int id) {
		this.id = id;
	}

	public String getPassaporte() {
	    return passaporte;
	}

	public void setPassaporte(String passaporte) {
	    this.passaporte = passaporte;
	}

	public int getId() {
		return id;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
		this.nomeAscii = StringUtils.toAsciiAndUpperCase(nome);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		
		/* Yes, according to RFC 2821, the local mailbox (the portion before @) is considered case-sensitive. 
		 * However, typically e-mail addresses are not case-sensitive because of the difficulties and confusion 
		 * it would cause between users, the server, and the administrator. Therefore, when sending an e-mail, 
		 * it's safe to assume an e-mail address like "SUPPORT@computerhope.com" is the 
		 * same as "support@computerhope.com." */
		
		if(email != null)
			this.email = email.toLowerCase();
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
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

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public UnidadeFederativa getUnidadeFederativa() {
		return unidadeFederativa;
	}

	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public void setSenha(String senha) {
		if(senha != null)
			this.senha = senha.trim();
	}

	public String getSenha() {
		return senha;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}
	
	public String getDataNascimentoFormatada() {
		return new SimpleDateFormat("dd/MM/yyyy").format(dataNascimento);
	}


	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
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

	public boolean isEstrangeiro() {
		return estrangeiro;
	}

	public void setEstrangeiro(boolean estrangeiro) {
		this.estrangeiro = estrangeiro;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}


	public boolean isConfirmado() {
		return confirmado;
	}

	public void setConfirmado(boolean confirmado) {
		this.confirmado = confirmado;
	}

	public String getSenhaGerada() {
		return senhaGerada;
	}

	public void setSenhaGerada(String senhaGerada) {
		this.senhaGerada = senhaGerada;
	}


	public String getCodigoAcessoConfirmacao() {
		return codigoAcessoConfirmacao;
	}


	public void setCodigoAcessoConfirmacao(String codigoAcessoConfirmacao) {
		this.codigoAcessoConfirmacao = codigoAcessoConfirmacao;
	}

	public Date getDataUltimaAtualizacao() {
		return dataUltimaAtualizacao;
	}

	public void setDataUltimaAtualizacao(Date dataUltimaAtualizacao) {
		this.dataUltimaAtualizacao = dataUltimaAtualizacao;
	}
}
