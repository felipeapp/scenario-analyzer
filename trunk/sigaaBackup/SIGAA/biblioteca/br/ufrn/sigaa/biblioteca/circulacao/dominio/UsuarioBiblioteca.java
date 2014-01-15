/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Criado em: 26/09/2008
 */
package br.ufrn.sigaa.biblioteca.circulacao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

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
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * <p>Classe que representa as informa��es dos usu�rios do sistema de bibliotecas para a parte de circula��o. 
 *    Sempre os empr�stimos, suspens�es, tudo que for ligado a parte de circula��o tem que est� relacionado com um "Usu�rio Biblioteca".</p>
 *        
 * <p>Um usu�rio biblioteca S� pode ser uma <strong>Pessoa</strong> OU  uma <strong>Biblioteca</strong> </p>
 *
 * <p><strong>Observa��o: </strong> Nunca era para existir dois "Usu�rios Biblioteca" ativos para a mesma pessoa <br/>
 * no sistema, nem dois "Usu�rios Biblioteca" ativos para a mesma biblioteca. Muito menos um "Usu�rio Biblioteca" <br/> 
 * que seja uma biblioteca ou uma pessoa ao mesmo tempo.</p>.
 * 
 * @author jadson
 * @since 26/09/2008
 * @version 1.0 cria��o da classe
 * @version 2.0 12/04/2011 a partir de agora um pessoa ou biblioteca vai poder possuir v�rios usu�rios biblioteca no sistema  durante a sua vida acad�mica
 *                            , mas somente um ativo e n�o quitado por vez. Para recuperar o hist�rico vai ser precisa buscar os empr�stmos de todos os 
 *                            usu�rios bibliotecas ativos, quitados ou n�o.
 */
@Entity
@Table(name = "usuario_biblioteca", schema = "biblioteca")
public class UsuarioBiblioteca implements Validatable{

	
	/**
	 * O id
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="biblioteca.usuario_biblioteca_sequence") })
	@Column(name="id_usuario_biblioteca")
	private int id;
	
	
	/* **************************************************************************************
	 *  <p> <strong>Identifica um UsuarioBiblioteca no sistema <strong>   </p>              *
	 *                                                                                      *
	 * <i>  Ou � uma pessoa ou � uma biblioteca, numca os dois ao mesmo tempo.   <br/>      *
	 * Tamb�m nunca deveria existir dois UsuarioBiblioteca para a mesma pessoa ativos       *
	 * no sistema. <br/>                                                                    *
	 * </i>                                                                                 *
	 * **************************************************************************************/
	
	/** Se o usu�rio do empr�stimo for uma pessoa . */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;

	
	/** Se o usu�rio do empr�stimo for uma biblioteca. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn (name="id_biblioteca", referencedColumnName="id_biblioteca")
	private Biblioteca biblioteca;	
	
	
	/* **************************************************************************************
	 *                                                                                      *
	 * **************************************************************************************/
	
	
	
	/** A senha para realizar os empr�stimos no bal��o da biblioteca  */
	@Column(name="senha")
	private String senha;
	
	
	
    /**
     * <p>O v�nculo que o usu�rio vai utilizar para realizar os empr�stimos.</p>
     * 
     * <p>Esse dado vai ser calculado no momento que o usu�rio fizer o  cadastro na biblioteca, ao realizar o empr�timos essa informa��o vai 
     * ser comparada, se tiver mudado, n�o vai deixar fazer o empr�stimos e vai pedir para o usu�rio fazer o recadastro para obter o v�nculo correto.</p>
     * 
     */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="vinculo", nullable=true)
	private VinculoUsuarioBiblioteca vinculo;
	
	
	/**************************************************************************************************
	 *   <p>Guarda informa��es sobre o v�nculo que o usu�rio utilizou para realizar um empr�stimo </p> 
	 *   <p>Dependendo o v�nculo, este campo pode cont�m: </p>                         
	 *   <ul>                                                                                         
	 *   	<li>idDiscente</li>:                                  
	 *   	<li>isServidor</li>
	 *   	<li>idUsuarioExterno</li>
	 *   	<li>idDocenteExterno</li>
	 *   	<li>idBiblioteca</li>
	 *   </ul>
	 *   <p><strong>Observa��o: </strong><i> O usu�rio n�o vai poder reativar um v�nculo j� quitado, s� um bibliotec�rio
	 *   pode. Para identifica��o do v�nculo � utilizado esse campo, caso o usu�rio possua 2 v�nculos iguais. Por exemplo, era discente 
	 *   em um curso, fez vestibular e entrou com discente em outro curso. O v�nculo � o mesmo mas a identifica��o � diferente.</i> 
	 *   </p>     
	 *************************************************************************************************/
	@Column(name = "identificacao_vinculo", nullable=true)
	private Integer identificacaoVinculo;
	
	
	/**
	 * <p>Caso o usu�rio biblioteca emita o comprovante de quita��o, n�o vai poder mais realizar 
	 * empr�stimos da biblioteca. Vai precisar se recadastrar para criar outro usu�rio biblioteca para ele com outro v�nculo.</p>
	 * 
	 * <p>Caso o usu�rio n�o possua outro v�nculo, n�o ser� criado outro usu�rio biblioteca para ele. Ficando impossibilitado de fazer empr�stimos.</p>
	 * 
	 * <p>N�o pode existir 2 ou mais usu�rios bibliotecas ativos e n�o quitados para a mesma pessoa ao mesmo tempo no sistema. </p>
	 * 
	 */
	@Column(nullable=false)
	private boolean quitado = false;
	
	
	/** Data em que o usu�rio foi quitado. */
	@Column(name="data_quitacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataQuitacao;
	
	
	/** Usu�rios removidos do sistema de biblioteca. <br/>
	 *  UsuarioBiblioteca nunca devem ser desativados para mander o seu hist�rico de empr�stimos, s�
	 *  em casos de duplica��o de cadastro, ou erros da migra��o. 
	 */
	@Column(nullable=false)
	private boolean ativo = true;
	
	
	
	/////////////// DADOS PARA AUDITORIA ////////////////////

	/** Data de cadastro. */
	@CriadoEm
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	/** Registro entrada do usu�rio que cadastrou. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	private RegistroEntrada registroCadastro;

	/** Data da �ltima atualiza��o */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	private Date dataAtualizacao;

	/** Registro entrada do usu�rio que realizou a �ltima atualiza��o */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	private RegistroEntrada registroAtualizacao;

	
	/** O usu�rio que quitou o v�nculo  */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_realizou_quitacao", referencedColumnName ="id_usuario")
	private Usuario usuarioRealizouQuitacao;

	/** O usu�rio que retirou a quita��o  */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_desfez_quitacao", referencedColumnName ="id_usuario")
	private Usuario usuarioDesfezQuitacao;
	
	///////////////////////////////////////////////
	
	
	
	/**
	 * Guarda o servidor do usu�rio biblioteca, caso o usu�rio esteja fazendo empr�stimos como servidor.<br/>
	 * <strong>Observa��o:</strong> N�o pode guardar essa informa��o no banco, � preciso sempre calcular no momento do empr�stimo. <br/>
	 */
	@Transient
	private Servidor servidor;
	
	
	
	/**
	 * Guarda o discente do usu�rio biblioteca, caso o usu�rio esteja fazendo empr�stimos como discente.<br/>
	 * <strong>Observa��o:</strong> N�o pode guardar essa informa��o no banco, � preciso sempre calcular no momento do empr�stimo. <br/>
	 */
	@Transient
	private Discente discente;
	
	
	
	/**
	 * Guarda o usu�rio do sistema utilizado para fazer os empr�stimo.<br/>
	 * <strong>Observa��o:</strong> N�o pode guardar essa informa��o no banco, � preciso sempre calcular no momento do empr�stimo. <br/>
	 */
	@Transient
	private Usuario usuario;
	
	
	
	/**
	 * Usado pelo hibbernate. 
	 */
	public UsuarioBiblioteca(){
		
	}

	/**
	 * Cria um usu�rio biblioteca que j� estar persistido
	 * @param id
	 */
	public UsuarioBiblioteca(int id){
		this.id = id;
	}
	
	/**
	 * Constr�i um novo UsuarioBiblioteca sem senha. Nesse caso o usu�ria vai precisar criar uma senha 
	 * antes de fazer os emprestimos.
	 * 
	 * @param usuario
	 * @param senha1
	 * @throws NegocioException
	 */
	public UsuarioBiblioteca( Pessoa pessoa){
		this.pessoa = pessoa;
		this.ativo = true;
		this.biblioteca = null;
	}
	
	
	/**
	 * Constr�i um novo UsuarioBiblioteca, associando a uma pessoa e a uma senha.
	 * 
	 * @param usuario
	 * @param senha1
	 * @throws NegocioException
	 */
	public UsuarioBiblioteca( Pessoa pessoa, String senha) throws NegocioException{
		this(pessoa);
		atualizaSenha(senha);
	}

	/**
	 * Constr�i um novo UsuarioBiblioteca, associando a uma pessoa e a uma senha.
	 * 
	 * @param usuario
	 * @param senha1
	 * @throws NegocioException
	 */
	public UsuarioBiblioteca( Pessoa pessoa, VinculoUsuarioBiblioteca vinculo, int identificacaoVinculo){
		this(pessoa);
		this.vinculo = vinculo;
		this.identificacaoVinculo = identificacaoVinculo;
	}
	
	/**
	 * Constr�i um novo UsuarioBiblioteca, associando a uma pessoa mas sem senha
	 * 
	 * @param usuario
	 * @param senha1
	 * @throws NegocioException
	 */
	public UsuarioBiblioteca( Pessoa pessoa, String senha, VinculoUsuarioBiblioteca vinculo, int identificacaoVinculo) throws NegocioException{
		this(pessoa, vinculo, identificacaoVinculo);
		atualizaSenha(senha);
	}
	
	
	 /**
     * Configura a nova senha do usu�rio.
     *
     * @param novaSenha
     * @throws NegocioException 
     */
    public void atualizaSenha(String novaSenha) throws NegocioException{
    	if (isSenhaBibliotecaValida(novaSenha))
    		this.senha = UFRNUtils.toMD5(novaSenha); // guarda em MD5
    }
	
    
	/***
	 * Valida se a senha da biblioteca � v�lida de acordo com as restri��es desse tipo de senha.
	 *
	 * @return
	 * @throws NegocioException 
	 */
    private boolean isSenhaBibliotecaValida(String novaSenha) throws NegocioException{
    	if( StringUtils.isEmpty(novaSenha) || novaSenha.length() < 6 || novaSenha.length() > 8) // Tem que ter tamanho entre 6 e 8.
    		throw new NegocioException ("A senha da biblioteca  deve ter tamanho entre seis e oito d�gitos.");
    	
    	try {
    		Integer.parseInt(novaSenha);
    		return true; // Tudo ok.
    		
    	} catch (NumberFormatException e){
    		throw new NegocioException ("A senha da biblioteca s� pode conter n�meros.");
    	}
    }
	
   
    /**
     * Verifica se o usu�rio pertece a uma biblioteca
     *
     * @return
     */
    public boolean ehBiblioteca(){
    	return biblioteca != null && biblioteca.getId() > 0;
    }
    
    
    /**
     * Verifica se o usu�rio pertece a uma biblioteca
     *
     * @return
     */
    public boolean ehPessoa(){
    	return pessoa != null && pessoa.getId() > 0;
    }
    
    
    /***
     *  <<p>Retorna a identifica��o do usu�rio quando o conta � de uma pessoa </p>
     *  
     * @return
     */
    public Integer getIdentificadorPessoa(){
    	return ehPessoa() ?  getPessoa().getId() : null ;
    }
    
    /***
     *  <p>Retorna a identifica��o do usu�rio quando o conta � de uma biblioteca </p>
     *  
     * @return
     */
    public Integer getIdentificadorBiblioteca(){
    	return ehBiblioteca() ? getBiblioteca().getId() : null ;
    }
    
    
    
    /**
     * Retorna o nome do usu�rio, seja ele usu�rio ou usu�rio externo.
     * 
     * @return
     */
    public String getNome(){
    	
    	if (pessoa != null)
    		return pessoa.getNome();
    	
    	if (biblioteca != null)
    		return biblioteca.getDescricao();
    	
    	return null;
    }
    
    /**
     * Verifica��o utilizada no cadastro de usu�rio biblioteca, onde ele cria ou altera a senha.
     * 
     */
    public ListaMensagens validate() {
    	
    	ListaMensagens mensagens = new ListaMensagens();
    	
    	if (pessoa == null && biblioteca == null)
    		mensagens.addErro(" N�o foi poss�vel encontrar as informa��es do usu�rio. ");
    	
    	if (StringUtils.isEmpty(senha))
    		mensagens.addErro(" A senha dos empr�timos do usu�rio da biblioteca deve ser informada. ");
    	
    	if(vinculo == VinculoUsuarioBiblioteca.INATIVO){
    		mensagens.addErro(" O usu�rio n�o possui v�nculo com a institui��o para utilizar a biblioteca. ");
		}
    	
    	return mensagens;
    }

    
    /// set e get ////
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
//	public boolean isBloqueado() {
//		return bloqueado;
//	}
//
//	public void setBloqueado(boolean bloqueado) {
//		this.bloqueado = bloqueado;
//	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public VinculoUsuarioBiblioteca getVinculo() {
		return vinculo;
	}

	public void setVinculo(VinculoUsuarioBiblioteca vinculo) {
		this.vinculo = vinculo;
	}

	public Integer getIdentificacaoVinculo() {
		return identificacaoVinculo;
	}

	public void setIdentificacaoVinculo(Integer identificacaoVinculo) {
		this.identificacaoVinculo = identificacaoVinculo;
	}

	public boolean isQuitado() {
		return quitado;
	}

	public void setQuitado(boolean quitado) {
		this.quitado = quitado;
	}

	public Date getDataQuitacao() {
		return dataQuitacao;
	}

	public void setDataQuitacao(Date dataQuitacao) {
		this.dataQuitacao = dataQuitacao;
	}

	public Usuario getUsuarioRealizouQuitacao() {
		return usuarioRealizouQuitacao;
	}

	public void setUsuarioRealizouQuitacao(Usuario usuarioRealizouQuitacao) {
		this.usuarioRealizouQuitacao = usuarioRealizouQuitacao;
	}

	public Usuario getUsuarioDesfezQuitacao() {
		return usuarioDesfezQuitacao;
	}

	public void setUsuarioDesfezQuitacao(Usuario usuarioDesfezQuitacao) {
		this.usuarioDesfezQuitacao = usuarioDesfezQuitacao;
	}
	
	
	
}