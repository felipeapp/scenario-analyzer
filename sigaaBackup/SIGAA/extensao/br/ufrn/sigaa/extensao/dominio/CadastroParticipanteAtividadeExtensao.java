/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>  Representa um cadastro <strong>*** ÚNICO ***</strong> que as pessoas devem fazer para realizar 
 *  a inscrição em um curso ou evento de extensão.</p>
 *
 * <p> <strong>Essa classe é um desmembramento da classe <code>InscricaoAtividadeParticipante</code>, agora lá vai conter 
 * apenas as informações da inscrição em si, as informações do participante ficam centralizadas aqui!!!</strong></p>
 *
 *
 * <p> <i> Cada pessoa vai possuir APENAS 1 CadastroParticipanteAtividadeExtensao durante toda a sua vida, 
 *   com uma única senha para acessar o sistema criada por ela mesma.  
 *   Quando ela for se inscrever em uma nova atividade de extensão vai ser criado uma outra entidade 
 *   InscricaoParticipanteAtividadeExtensao.  Que vai fazer o papel da antiga 
 *   InscricaoAtividadeParticipante, contendo os dados para aquela inscrição exclusiva.
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
	
	
    /** CPF do participante inscrito. Nulo para o caso do usuário ser estrageiro. */
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
    
    /** Número do logradouro participante inscrito. Pode ser nulo caso o participante seja cadastrado por alguns gestor. */
    @Column(name = "numero", nullable = true)
    private String numero;

    /** O complemento do endereço do participante inscrito. */
    @Column(name = "complemento", nullable = true)
    private String complemento;
    
    /** Bairro do participante inscrito. Pode ser nulo caso o participante seja cadastrado por alguns gestor. */ 
    @Column(name = "bairro", nullable = true)
    private String bairro;
    
    /** Município do participante inscrito. Pode ser nulo caso o participante seja cadastrado por alguns gestor. */
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
    
    /** Email do participante. É usado como "login" na página "privada" do usuário na parte 
     * pública, já que a pessoa não tem usuário no sistema. 
     * e também para o coordenador entrar em contato. */
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    
    /** Telefone do participante para o coordenador entrar em contato. Guarda os números com a máscara. */
    @Column(name = "telefone", nullable = true)
    private String telefone;
	
    
	/** Celular do participante para o coordenador entrar em contato. Guarda os números com a máscara.  */
    @Column(name = "celular", nullable = true)
    private String celular;

	
    /**
     * <p>Senha para o usuário ter acesso a parte onde ele pode gerenciar as suas incrições.</p>
     * 
     * <p>Vai ser na parte pública do sigaa mas ele vai ter que se "logar" para ter acesso.</p>
     * 
     * <p>Guarda a senha em MD5</p>
     * 
     * <p><strong>ESSE É O ÚNICO CAMPO USADO PARA SE AUTENTICAR NO SISTEMA.</strong></p>
     * 
     */
    @Column(name = "senha", nullable = true)
    private String senha;

    
    
    /**
     * <p>Guarda a senha gerada pelo sistema em <strong>TEXTO PURO</strong> para ser enviada para o usuário.  
     * Quando o usuário se logar ele pode alterar essa senha gerada.</p>
     *  
     * <p>Utilizado nos caso em que o coordenador cadastra o usuário, ou no caso que o usuário 
     * perder a senha e solicita reenvio. Neste último caso, o sistema gera uma senha automática e envia.</p>
     * 
     * <p><strong>ESSE CAMPO NUNCA É USADO PARA SE AUTENTICAR NO SISTEMA</strong>. Mesmo nos casos nos quais o sistema gera uma senha 
     * automática o usuário precisa confirmar essa alteração para gerar o hash da senha e colocar no campo "senha".</p> 
     *  
     */
    @Column(name = "senha_gerada", nullable = true)
    private String senhaGerada;
    
    
    /** Caso a pessoa seja extranjeira, tem que possuir obrigatoriamente o passaporte informado, caso seja brasileira o CPF.*/
    @Column(name = "estrangeiro", nullable = false)
    private boolean estrangeiro = false;
    

    
    /** <p>Código gerado e salvo para o usuário validar o e-mail informado na inscrição.</p>
     * 
     *  <p>Usado apenas quando o usuário faz seu cadastro no sistema ou um pedido de solicitação de alteração de senha é feito.</p>
     *  
     *  <p>Caso a inscrição do participante seja feita pelo coordenador essse código não vai exisitir, o 
     *  participante já vai ser salvo no banco com seu cadastro confirmado.</p>
     */
    @Column(name = "codigo_acesso_confirmacao", nullable = true)
    private String codigoAcessoConfirmacao;
    
    
    /** 
     * Indica se o cadastro foi removido do sistema. Caso haja a necessidade de remover um cadastro 
     * ele será desativado, não podendo a pessoa acessar mais o sistema nem participar de nenhum curso 
     * ou evento com esse cadastro.
     */
    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;

    
	/** 
	 * Se o cadastro foi confirmado pelo usuário (via email). Só vai se logar no sistema quando confirmar. 
	 * 
	 * O usuário pode realizar várias vezes o cadatro até confirmar. Para caso ele erre o email pode ser 
	 * cadastrar novamente.
	 */
    @Column(name = "confirmado", nullable = false)
    private boolean confirmado = false;
    
    
    
    
	////////////////////////////INFORMAÇÕES DE AUDITORIA  ///////////////////////////////////////
	
	
	/**
	 *  *** IMPORTANTE ***: Se for cadastrado pelo próprio usuário na área pública, essa coluna vai ser nula.
	 * 
	 * informações de quem criou
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
	* registro entrada  do usuário que realizou a última atualização
	*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_ultima_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroUltimaAtualizacao;

    
    /** Data da última atualização do cadastro */
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
     * Gera o hash da senha de autenticação do usuário para guardar no banco.
     *
     * @param novaSenha
     * @throws NegocioException 
     */
    public void geraHashSenha(){
    	this.senha = UFRNUtils.toMD5(senha);
    }
    
    
    /**
	 * Método utilizado para informar o HashCode
	 * @return
	 */
	public void geraCodigoAcessoConfirmacaoInscricao() {
	    if(isEstrangeiro())
	    	this.codigoAcessoConfirmacao = UFRNUtils.toSHA1Digest("IP"+ getId() + getPassaporte() );
	    else
	    	this.codigoAcessoConfirmacao  = UFRNUtils.toSHA1Digest("IP"+ getId() + getCpf() );
	}
    
	
	
    /** Contém a lógica  de gerar uma nova senha automática para acessar participantes de cursos e evento de extenssão */
	public void geraSenhaAutomatica(){
		String senhaTemp = UFRNUtils.toMD5(UFRNUtils.geraSenhaAleatoria() );
		if(senhaTemp.length() > 10)
			this.senhaGerada = senhaTemp.substring(0, 10);
		else
			this.senhaGerada = senhaTemp;
	}
	
	/**
	 * Retorna o endereço completo do participante.
	 * 
	 * logradouro nº complemento bairro, municipio/estado - 59000-000
	 *
	 * @return
	 */
	public String getEnderecoCompleto(){
		return logradouro+" "+numero+" "+complemento+" "+bairro
		+", "+ ( municipio != null ? municipio.getNome() : "" )+"/"+ ( unidadeFederativa != null ? unidadeFederativa.getSigla() : "" )+" - "+cep;
	}
	
	
	
    /**
	 * <p>Método utilizao para validar o cadastro de um novo participante.</p>
	 * 
	 * <p>Valida por ***Completo***, geralmente quando o próprio participante faz o cadastro.</p> 
	 */
	public ListaMensagens validate() {
		
		ListaMensagens lista = new ListaMensagens();
		
		lista.addAll(validateDadosPessoais());
		lista.addAll(validateEndereco());
		lista.addAll(validateSenha());
		
		
		return lista;
	}
	
	
	/**
	 * Método utilizao para validar o cadastro de partricipantes quando algum gestor cadastra. 
	 * Nesse casos alguns dados vão ficar opcionais.
	 * 
	 */
	public ListaMensagens validateCadastroByGestor() {
		
		ListaMensagens lista = new ListaMensagens();
		
		lista.addAll(validateDadosPessoais());
			
		return lista;
	}
	
	
	/**
	 * Método utilizao para validar o cadastro de partricipantes, mas não valida o campo senha. Usado na alteração dos dados pelos gestores.
	 * 
	 * Que podem não informa a senha do usuário
	 */
	private ListaMensagens validateDadosPessoais() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if(estrangeiro){
			ValidatorUtil.validateRequired(passaporte, "Passaporte", lista); // se o usuário é internacional tem que infomar o passaporte obrigatoriamente
		}else{
			if(cpf != null)
				ValidatorUtil.validateCPF_CNPJ(cpf, "CPF", lista); // caso não seja deve informar o CPF
			else
				lista.addMensagem(FORMATO_INVALIDO, "CPF");
		}
		
		ValidatorUtil.validateRequired(nome, "Nome Completo", lista);
		
		ValidatorUtil.validateRequired(dataNascimento, "Data de Nascimento", lista);
		
		if(dataNascimento != null){
			Date dataLimite = CalendarUtils.adicionarAnos(new Date(), -1); // a pessoa tem que ter pelo menos 1 ano para participar de uma ação de extensão.
			ValidatorUtil.validaDataAnteriorIgual(dataNascimento, dataLimite, "Data de Nascimento", lista);
		}
		
		ValidatorUtil.validateRequired(email, "Email", lista);
		
		return lista;
	}
	
	
	/**
	 * Método utilizao para validar o cadastro de partricipantes, mas não valida o campo senha. Usado na alteração dos dados pelos gestores.
	 * 
	 * Que podem não informa a senha do usuário
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
	 * Método utilizao para validar o cadastro de partricipantes, mas não valida o campo senha. Usado na alteração dos dados pelos gestores.
	 * 
	 * Que podem não informa a senha do usuário
	 */
	private ListaMensagens validateEndereco() {
		
		ListaMensagens lista = new ListaMensagens();
		
		ValidatorUtil.validateRequired(logradouro, "Logradouro", lista);
		ValidatorUtil.validateRequired(numero, "Número", lista);
		
		if(StringUtils.isNotEmpty(numero)){
			try{
				Integer.parseInt(numero);
			}catch(NumberFormatException nfe){
				lista.addMensagem(FORMATO_INVALIDO, "Número");
			}
		}
		
		ValidatorUtil.validateRequired(bairro, "Bairro", lista);
		ValidatorUtil.validateRequiredId(municipio.getId(), "Município", lista);
		ValidatorUtil.validateRequiredId(unidadeFederativa.getId(), "UF", lista);
		
		if (ValidatorUtil.validateCEP(cep, "CEP", lista) == 0)
			ValidatorUtil.validateRequired(cep, "CEP", lista);
		
		return lista;
	}
	
	/** Apaga dados que não foram preenchidos*/
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
     * Ver comentários da classe pai.<br/>
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
