/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>Classe que representa as informações dos usuários do sistema de bibliotecas para a parte de circulação. 
 *    Sempre os empréstimos, suspensões, tudo que for ligado a parte de circulação tem que está relacionado com um "Usuário Biblioteca".</p>
 *        
 * <p>Um usuário biblioteca SÓ pode ser uma <strong>Pessoa</strong> OU  uma <strong>Biblioteca</strong> </p>
 *
 * <p><strong>Observação: </strong> Nunca era para existir dois "Usuários Biblioteca" ativos para a mesma pessoa <br/>
 * no sistema, nem dois "Usuários Biblioteca" ativos para a mesma biblioteca. Muito menos um "Usuário Biblioteca" <br/> 
 * que seja uma biblioteca ou uma pessoa ao mesmo tempo.</p>.
 * 
 * @author jadson
 * @since 26/09/2008
 * @version 1.0 criação da classe
 * @version 2.0 12/04/2011 a partir de agora um pessoa ou biblioteca vai poder possuir vários usuários biblioteca no sistema  durante a sua vida acadêmica
 *                            , mas somente um ativo e não quitado por vez. Para recuperar o histórico vai ser precisa buscar os empréstmos de todos os 
 *                            usuários bibliotecas ativos, quitados ou não.
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
	 * <i>  Ou é uma pessoa ou é uma biblioteca, numca os dois ao mesmo tempo.   <br/>      *
	 * Também nunca deveria existir dois UsuarioBiblioteca para a mesma pessoa ativos       *
	 * no sistema. <br/>                                                                    *
	 * </i>                                                                                 *
	 * **************************************************************************************/
	
	/** Se o usuário do empréstimo for uma pessoa . */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;

	
	/** Se o usuário do empréstimo for uma biblioteca. */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn (name="id_biblioteca", referencedColumnName="id_biblioteca")
	private Biblioteca biblioteca;	
	
	
	/* **************************************************************************************
	 *                                                                                      *
	 * **************************************************************************************/
	
	
	
	/** A senha para realizar os empréstimos no balção da biblioteca  */
	@Column(name="senha")
	private String senha;
	
	
	
    /**
     * <p>O vínculo que o usuário vai utilizar para realizar os empréstimos.</p>
     * 
     * <p>Esse dado vai ser calculado no momento que o usuário fizer o  cadastro na biblioteca, ao realizar o emprétimos essa informação vai 
     * ser comparada, se tiver mudado, não vai deixar fazer o empréstimos e vai pedir para o usuário fazer o recadastro para obter o vínculo correto.</p>
     * 
     */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="vinculo", nullable=true)
	private VinculoUsuarioBiblioteca vinculo;
	
	
	/**************************************************************************************************
	 *   <p>Guarda informações sobre o vínculo que o usuário utilizou para realizar um empréstimo </p> 
	 *   <p>Dependendo o vínculo, este campo pode contém: </p>                         
	 *   <ul>                                                                                         
	 *   	<li>idDiscente</li>:                                  
	 *   	<li>isServidor</li>
	 *   	<li>idUsuarioExterno</li>
	 *   	<li>idDocenteExterno</li>
	 *   	<li>idBiblioteca</li>
	 *   </ul>
	 *   <p><strong>Observação: </strong><i> O usuário não vai poder reativar um vínculo já quitado, só um bibliotecário
	 *   pode. Para identificação do vínculo é utilizado esse campo, caso o usuário possua 2 vínculos iguais. Por exemplo, era discente 
	 *   em um curso, fez vestibular e entrou com discente em outro curso. O vínculo é o mesmo mas a identificação é diferente.</i> 
	 *   </p>     
	 *************************************************************************************************/
	@Column(name = "identificacao_vinculo", nullable=true)
	private Integer identificacaoVinculo;
	
	
	/**
	 * <p>Caso o usuário biblioteca emita o comprovante de quitação, não vai poder mais realizar 
	 * empréstimos da biblioteca. Vai precisar se recadastrar para criar outro usuário biblioteca para ele com outro vínculo.</p>
	 * 
	 * <p>Caso o usuário não possua outro vínculo, não será criado outro usuário biblioteca para ele. Ficando impossibilitado de fazer empréstimos.</p>
	 * 
	 * <p>Não pode existir 2 ou mais usuários bibliotecas ativos e não quitados para a mesma pessoa ao mesmo tempo no sistema. </p>
	 * 
	 */
	@Column(nullable=false)
	private boolean quitado = false;
	
	
	/** Data em que o usuário foi quitado. */
	@Column(name="data_quitacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataQuitacao;
	
	
	/** Usuários removidos do sistema de biblioteca. <br/>
	 *  UsuarioBiblioteca nunca devem ser desativados para mander o seu histórico de empréstimos, só
	 *  em casos de duplicação de cadastro, ou erros da migração. 
	 */
	@Column(nullable=false)
	private boolean ativo = true;
	
	
	
	/////////////// DADOS PARA AUDITORIA ////////////////////

	/** Data de cadastro. */
	@CriadoEm
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCadastro;

	/** Registro entrada do usuário que cadastrou. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	private RegistroEntrada registroCadastro;

	/** Data da última atualização */
	@AtualizadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	private Date dataAtualizacao;

	/** Registro entrada do usuário que realizou a última atualização */
	@AtualizadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	private RegistroEntrada registroAtualizacao;

	
	/** O usuário que quitou o vínculo  */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_realizou_quitacao", referencedColumnName ="id_usuario")
	private Usuario usuarioRealizouQuitacao;

	/** O usuário que retirou a quitação  */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario_desfez_quitacao", referencedColumnName ="id_usuario")
	private Usuario usuarioDesfezQuitacao;
	
	///////////////////////////////////////////////
	
	
	
	/**
	 * Guarda o servidor do usuário biblioteca, caso o usuário esteja fazendo empréstimos como servidor.<br/>
	 * <strong>Observação:</strong> Não pode guardar essa informação no banco, é preciso sempre calcular no momento do empréstimo. <br/>
	 */
	@Transient
	private Servidor servidor;
	
	
	
	/**
	 * Guarda o discente do usuário biblioteca, caso o usuário esteja fazendo empréstimos como discente.<br/>
	 * <strong>Observação:</strong> Não pode guardar essa informação no banco, é preciso sempre calcular no momento do empréstimo. <br/>
	 */
	@Transient
	private Discente discente;
	
	
	
	/**
	 * Guarda o usuário do sistema utilizado para fazer os empréstimo.<br/>
	 * <strong>Observação:</strong> Não pode guardar essa informação no banco, é preciso sempre calcular no momento do empréstimo. <br/>
	 */
	@Transient
	private Usuario usuario;
	
	
	
	/**
	 * Usado pelo hibbernate. 
	 */
	public UsuarioBiblioteca(){
		
	}

	/**
	 * Cria um usuário biblioteca que já estar persistido
	 * @param id
	 */
	public UsuarioBiblioteca(int id){
		this.id = id;
	}
	
	/**
	 * Constrói um novo UsuarioBiblioteca sem senha. Nesse caso o usuária vai precisar criar uma senha 
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
	 * Constrói um novo UsuarioBiblioteca, associando a uma pessoa e a uma senha.
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
	 * Constrói um novo UsuarioBiblioteca, associando a uma pessoa e a uma senha.
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
	 * Constrói um novo UsuarioBiblioteca, associando a uma pessoa mas sem senha
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
     * Configura a nova senha do usuário.
     *
     * @param novaSenha
     * @throws NegocioException 
     */
    public void atualizaSenha(String novaSenha) throws NegocioException{
    	if (isSenhaBibliotecaValida(novaSenha))
    		this.senha = UFRNUtils.toMD5(novaSenha); // guarda em MD5
    }
	
    
	/***
	 * Valida se a senha da biblioteca é válida de acordo com as restrições desse tipo de senha.
	 *
	 * @return
	 * @throws NegocioException 
	 */
    private boolean isSenhaBibliotecaValida(String novaSenha) throws NegocioException{
    	if( StringUtils.isEmpty(novaSenha) || novaSenha.length() < 6 || novaSenha.length() > 8) // Tem que ter tamanho entre 6 e 8.
    		throw new NegocioException ("A senha da biblioteca  deve ter tamanho entre seis e oito dígitos.");
    	
    	try {
    		Integer.parseInt(novaSenha);
    		return true; // Tudo ok.
    		
    	} catch (NumberFormatException e){
    		throw new NegocioException ("A senha da biblioteca só pode conter números.");
    	}
    }
	
   
    /**
     * Verifica se o usuário pertece a uma biblioteca
     *
     * @return
     */
    public boolean ehBiblioteca(){
    	return biblioteca != null && biblioteca.getId() > 0;
    }
    
    
    /**
     * Verifica se o usuário pertece a uma biblioteca
     *
     * @return
     */
    public boolean ehPessoa(){
    	return pessoa != null && pessoa.getId() > 0;
    }
    
    
    /***
     *  <<p>Retorna a identificação do usuário quando o conta é de uma pessoa </p>
     *  
     * @return
     */
    public Integer getIdentificadorPessoa(){
    	return ehPessoa() ?  getPessoa().getId() : null ;
    }
    
    /***
     *  <p>Retorna a identificação do usuário quando o conta é de uma biblioteca </p>
     *  
     * @return
     */
    public Integer getIdentificadorBiblioteca(){
    	return ehBiblioteca() ? getBiblioteca().getId() : null ;
    }
    
    
    
    /**
     * Retorna o nome do usuário, seja ele usuário ou usuário externo.
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
     * Verificação utilizada no cadastro de usuário biblioteca, onde ele cria ou altera a senha.
     * 
     */
    public ListaMensagens validate() {
    	
    	ListaMensagens mensagens = new ListaMensagens();
    	
    	if (pessoa == null && biblioteca == null)
    		mensagens.addErro(" Não foi possível encontrar as informações do usuário. ");
    	
    	if (StringUtils.isEmpty(senha))
    		mensagens.addErro(" A senha dos emprétimos do usuário da biblioteca deve ser informada. ");
    	
    	if(vinculo == VinculoUsuarioBiblioteca.INATIVO){
    		mensagens.addErro(" O usuário não possui vínculo com a instituição para utilizar a biblioteca. ");
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