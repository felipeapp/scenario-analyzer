/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/10/2008
 *
 */
package br.ufrn.sigaa.biblioteca.dominio;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;

/**
 *  <p>  Classe que guarda de forma tempor�ria TODAS as informa��es do usu�rio da biblioteca para visualiza��o o m�dulo de circula��o da biblioteca. 
 *      Utilizada no m�dulo web, j� que o desktop utiliza os DTOs para guardas as mesmas informa��es.</p>
 *      
 *  <p> 
 *      <i> (Al�m das informa��es que identifica��o o usu�rio biblioteca como nome, cpf, telefone, essa classe guarda, por exemplo,
 *      as informa��es do v�nculo que o usu�rio deve usar para realizar os empr�stimos.)
 *      </i> 
 *  </p>
 *	
 *	<p><strong>IMPORTANTE:</strong> Os dados dessa classe devem ser montados preferencionalmente utilizando o m�todo:
 * 	{@link CirculacaoUtil.obtemInformacoesDoUsuarioDaBiblioteca}, assim o c�digo fica centralizado e evitam-se erros.</p>
 *
 * @author jadson
 * @since 15/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class InformacoesUsuarioBiblioteca {

	/**
	 * o id na base de dados se precisar consultar
	 */
	private int idUsuarioBiblioteca; 
	
	/**
	 *  o nome do usu�rio (nome da pessoa ou descri��o da biblioteca)
	 */
	private String nomeUsuario; 

	/**
	 *  guarda a matr�cula, n�mero do siape, cpf, ou c�digo da Biblioteca, dependendo se � aluno, sevidor, usu�rio externo ou bibliotececa repectivamente.
	 *  guarda tamb�m o passaporte, caso a pessoa seja entrageira e n�o possua CPF
	 */
	private String codigoIdentificacaoUsuario; 
	
	/**
	 * Indica se o identificador for CPF ou Passaporte para mostrar corretamente ao usu�rio, n�o tem como saber essa informa��o, ela tem que
	 * ser passado no momento da constru��o do objeto.
	 */
	private boolean usuarioIdentificadoPeloPassaporte = true;
	
	/**
	 * Utilizando apenas para identifica quando o v�nculo est� inativo, se � uma pessoa ou biblioteca;
	 */
	private boolean isBibliotecaInativa = false;
	
	/**
	 *  o id da foto na base de dados
	 */
	private Integer idFoto; 
	
	/**
	 * O vinculo
	 */
	private VinculoUsuarioBiblioteca vinculo;

	/**
	 * A identifica��o do v�nculo
	 */
	private Integer identificacaoVinculo;
	
	
	
	/**
	 *  �til no envio de mensagem para um usu�rio
	 */
	private String loginUsuario;
	
	///////////////////////  Informa��es de contato //////////////////////////
	
	/**
	 *  telefone para contado
	 */
	private String telefone;

	/**
	 * email para contato
	 */
	private String email; 
	
	/**
	 * Nome da avenida, rua, pra�a, travessa, alameda, beco, etc..
	 */
	private String logradouro;

	/** N�mero do pr�dio */
	private String numero;

	/** Complemento (bloco, apartamento, etc.) */
	private String complemento;

	/** cep */
	private String cep;
	
	/**
	 * Nome da avenida, rua, pra�a, travessa, alameda, beco, etc..
	 */
	private String bairro;
	
	////////////////////////////////////////////////////////////////
	
	/**informa��es que s�o preenchidas quando o usu�rio � servidor (professor e t�cnico-administrativo) */
	private String cargo= null;
	/**informa��es que s�o preenchidas quando o usu�rio � servidor (professor e t�cnico-administrativo) */
	private String lotacao= null;
	
	
	/** informa��es que s�o preenchidas quando  � aluno (m�dio, gradua��o, p�s) */ 
	private String curso= null;
	/** informa��es que s�o preenchidas quando  � aluno (m�dio, gradua��o, p�s) */ 
	private String centro= null;
	
	/**
	 *  Para alunos especiais, se forem de mobilidade estudantil, poder�o emprestar materiais nas bibliotecas.
	 */
	private boolean mobilidadeEstudantil;
	
	/**
	 * Indica se o discente participa de um projeto de inicia��o cient�fica
	 */
	private boolean iniciacaoCientifica;	

	
	/**
	 *  A senha do usu�rio, em MD5;
	 */
	private String hashSenha; 


	/** O motivo pelo qual o v�nculo do usu�rio externo foi cancelado. */
	private String motivoCancelamentoVinculoUsuarioExterno; 
	
	/**
	 *  <p>Identifica o tipo do usu�rios para mostrar e validar os empr�stimos, j� que discentes especiais n�o fazem empr�stimos.</p>
	 *  <p>S� usado caso o usu�rio seja discente</p>
	 *  <p>1 - Normal, 2 - Especial</p>
	 */
	private Integer tipoDiscente; 
	
	
	/**
	 * Construtor para usu�rio inativos para pessoa
	 * 
	 * @param centro
	 * @param curso
	 * @param ehAluno
	 * @param email
	 * @param idFoto
	 * @param lotacao
	 * @param matricula
	 * @param nomeUsuario
	 * @param telefone
	 * @param tipoUsuario
	 */
	public InformacoesUsuarioBiblioteca(String nomeUsuario, String cpfPassaporte, boolean passandoCPF, Integer idFoto
			,String email, String telefone, String logradouro, String numero,String complemento, String cep, String bairro) {
		
		this(0, VinculoUsuarioBiblioteca.INATIVO, 0, logradouro, numero, complemento, cep, bairro);
		this.idFoto = idFoto;
		this.email = email;
		this.nomeUsuario = nomeUsuario;
		this.codigoIdentificacaoUsuario = cpfPassaporte != null ? cpfPassaporte.replace(".", "").replace("-", "") : "";
		this.telefone = telefone;
		this.isBibliotecaInativa = false;
		this.usuarioIdentificadoPeloPassaporte = ! passandoCPF;
	}
	
	/**
	 * Construtor para usu�rio inativos para biblioteca
	 * 
	 * @param centro
	 * @param curso
	 * @param ehAluno
	 * @param email
	 * @param idFoto
	 * @param lotacao
	 * @param matricula
	 * @param nomeUsuario
	 * @param telefone
	 * @param tipoUsuario
	 */
	public InformacoesUsuarioBiblioteca(String nomeUsuario, String codigoBiblioteca
			,String email, String telefone, String logradouro, String numero,String complemento, String cep, String bairro) {
		
		this(0, VinculoUsuarioBiblioteca.INATIVO, 0, logradouro, numero, complemento, cep, bairro);
		this.email = email;
		this.nomeUsuario = nomeUsuario;
		this.codigoIdentificacaoUsuario = codigoBiblioteca;
		this.telefone = telefone;
		this.isBibliotecaInativa = true;
		this.usuarioIdentificadoPeloPassaporte = false;
	}
	
	/**
	 * Construtor para bibliotecas
	 * 
	 * @param centro
	 * @param curso
	 * @param ehAluno
	 * @param email
	 * @param idFoto
	 * @param lotacao
	 * @param matricula
	 * @param nomeUsuario
	 * @param telefone
	 * @param tipoUsuario
	 */
	public InformacoesUsuarioBiblioteca(int idUsuarioBiblioteca, String codigoBiblioteca,  String descricaoBiblioteca,
			String email, String telefone, VinculoUsuarioBiblioteca vinculo, int identificacaoVinculo,
			String logradouro, String numero,String complemento, String cep, String bairro) {
		
		this(idUsuarioBiblioteca, vinculo, identificacaoVinculo, logradouro, numero, complemento, cep, bairro);
	
		this.codigoIdentificacaoUsuario = codigoBiblioteca;
		this.email = email;
		this.nomeUsuario = descricaoBiblioteca;
		this.telefone = telefone;
	}
	
	/**
	 * Construtor para aluno ( aluno de p�s, gradua��o ou de n�vel m�dio )
	 * 
	 * @param centro
	 * @param curso
	 * @param ehAluno
	 * @param email
	 * @param idFoto
	 * @param lotacao
	 * @param matricula
	 * @param nomeUsuario
	 * @param telefone
	 * @param tipoUsuario
	 */
	public InformacoesUsuarioBiblioteca(int idUsuarioBiblioteca, VinculoUsuarioBiblioteca vinculo,  int identificacaoVinculo,
			String centro, String curso, int tipoDiscente,
			String email, Integer idFoto, String matricula, String nomeUsuario, String loginUsuario,
			String telefone,  String senha, boolean mobilidadeEstudantil, boolean iniciacaoCientifica,
			String logradouro, String numero,String complemento, String cep, String bairro) {
	
		this(idUsuarioBiblioteca, vinculo, identificacaoVinculo, logradouro, numero, complemento, cep, bairro);
		this.centro = centro;
		this.curso = curso;
		this.tipoDiscente = tipoDiscente;
		this.email = email;
		this.idFoto = idFoto;
		this.codigoIdentificacaoUsuario = matricula;
		this.nomeUsuario = nomeUsuario;
		this.loginUsuario = loginUsuario;
		this.telefone = telefone;
		this.hashSenha = senha;
		this.mobilidadeEstudantil = mobilidadeEstudantil;
		this.iniciacaoCientifica = iniciacaoCientifica;
	}
	
	
	/**
	 * Construtor para servidores (professor e t�cnicos-administrativos )
	 * 
	 * @param cargo
	 * @param ehServidor
	 * @param email
	 * @param idFoto
	 * @param lotacao
	 * @param matricula
	 * @param nomeUsuario
	 * @param telefone
	 * @param tipoUsuario
	 */
	public InformacoesUsuarioBiblioteca(int idUsuarioBiblioteca, String cargo,
			String email, Integer idFoto, String lotacao, String siape, 
			String nomeUsuario, String loginUsuario, String telefone, VinculoUsuarioBiblioteca vinculo,  int identificacaoVinculo, String senha,
			String logradouro, String numero,String complemento, String cep, String bairro) {
		
		this(idUsuarioBiblioteca, vinculo, identificacaoVinculo, logradouro, numero, complemento, cep, bairro);
		
		this.cargo = cargo;
		this.email = email;
		this.idFoto = idFoto;
		this.lotacao = lotacao;
		this.codigoIdentificacaoUsuario = siape;
		this.nomeUsuario = nomeUsuario;
		this.loginUsuario = loginUsuario;
		this.telefone = telefone;
		
		this.hashSenha = senha;
	}
	
	
	
	/**
	 * Construtor para docentes externos (N�O S�O CONSIDERADOS SERVIDORES, N�O TEM SIAPE)
	 * 
	 * @param centro
	 * @param curso
	 * @param ehAluno
	 * @param email
	 * @param idFoto
	 * @param lotacao
	 * @param matricula
	 * @param nomeUsuario
	 * @param telefone
	 * @param tipoUsuario
	 */
	public InformacoesUsuarioBiblioteca(int idUsuarioBiblioteca, VinculoUsuarioBiblioteca vinculo, int identificacaoVinculo,
			String email, String telefone, Integer idFoto, String lotacao, String nomeUsuario, String cpfPassaporte, boolean passandoCPF, String loginUsuario
			, String senha, String logradouro, String numero,String complemento, String cep, String bairro) {
		
		this(idUsuarioBiblioteca, VinculoUsuarioBiblioteca.DOCENTE_EXTERNO, identificacaoVinculo, logradouro, numero, complemento, cep, bairro);
		
		
		this.codigoIdentificacaoUsuario =  cpfPassaporte.replace(".", "").replace("-", "");
		this.email = email;
		this.idFoto = idFoto;
		this.lotacao = lotacao;
		this.nomeUsuario = nomeUsuario;
		this.usuarioIdentificadoPeloPassaporte = ! passandoCPF;
		this.loginUsuario = loginUsuario;
		this.telefone = telefone;
		this.hashSenha = senha;
		
	
	}
	
	
	/**
	 * Construtor para usu�rios externos
	 * 
	 * @param centro
	 * @param curso
	 * @param ehAluno
	 * @param email
	 * @param idFoto
	 * @param lotacao
	 * @param matricula
	 * @param nomeUsuario
	 * @param telefone
	 * @param tipoUsuario
	 */
	public InformacoesUsuarioBiblioteca(int idUsuarioBiblioteca , int identificacaoVinculo,
			String email, String nomeUsuario, String cpfPassaporte, boolean passandoCPF, String telefone, String motivoCancelamentoVinculoUsuarioExterno, String senha,
			String logradouro, String numero,String complemento, String cep, String bairro) {
		
		this(idUsuarioBiblioteca, VinculoUsuarioBiblioteca.USUARIO_EXTERNO, identificacaoVinculo, logradouro, numero, complemento, cep, bairro);
		this.email = email;
		this.nomeUsuario = nomeUsuario;
		this.codigoIdentificacaoUsuario =  cpfPassaporte.replace(".", "").replace("-", "");
		this.usuarioIdentificadoPeloPassaporte = ! passandoCPF;
		this.telefone = telefone;
		this.hashSenha = senha;
	
		this.motivoCancelamentoVinculoUsuarioExterno = motivoCancelamentoVinculoUsuarioExterno;
	
		//this.extrageira = extrageira;
		
		
	}
	
	/**
	 * Construtor de dados comuns
	 * @param logradouro
	 * @param numero
	 * @param complemento
	 * @param cep
	 * @param bairro
	 */
	private InformacoesUsuarioBiblioteca(int idUsuarioBiblioteca, VinculoUsuarioBiblioteca vinculo, int identificacaoVinculo, 
					String logradouro, String numero,String complemento, String cep, String bairro){
		this.idUsuarioBiblioteca = idUsuarioBiblioteca;
		this.vinculo = vinculo;
		this.identificacaoVinculo = identificacaoVinculo;
		this.logradouro = logradouro;
		this.numero = numero;
		this.complemento = complemento;
		this.cep = cep;
		this.bairro = bairro;
	}
	
	
	
	
	
	
	
	

	
	
	
	/** 
	 * Construtor sem par�metros
	 */
	public InformacoesUsuarioBiblioteca() {
	}

	
	
	/**
	 * Retorna a indentifica��o correta do usu�rio.
	 *
	 * @return
	 */
	public int getIdentificacaoVinculo(){
		
		return identificacaoVinculo;
	}
	
	
	/**
	 *   M�todo que determina se a vari�el <code>matricula_SIAPE_CPF_CODIGO_BIBLIOTECA</code> contem uma matr�cula.
	 *
	 * @return
	 */
	public boolean isContemMatricula(){
		if( this.vinculo == VinculoUsuarioBiblioteca.ALUNO_INFANTIL
		|| this.vinculo == VinculoUsuarioBiblioteca.ALUNO_TECNICO_MEDIO
		|| this.vinculo == VinculoUsuarioBiblioteca.ALUNO_GRADUACAO
		|| this.vinculo ==VinculoUsuarioBiblioteca.ALUNO_POS_GRADUCACAO)
			return true;
		else
			return false;
	}
	
	/**
	 *   M�todo que determina se a vari�el <code>matricula_SIAPE_CPF_CODIGO_BIBLIOTECA</code> contem um cpf ou passaporte
	 *
	 * @return
	 */
	public boolean isContemCPFPassaporte(){
		if( this.vinculo == VinculoUsuarioBiblioteca.USUARIO_EXTERNO
		|| this.vinculo == VinculoUsuarioBiblioteca.DOCENTE_EXTERNO
		||  ( this.vinculo == VinculoUsuarioBiblioteca.INATIVO && ! isBibliotecaInativa ) )
			return true;
		else
			return false;
	}
	
	/**
	 *   M�todo que determina se a vari�el <code>matricula_SIAPE_CPF_CODIGO_BIBLIOTECA</code> contem um siape.
	 *
	 * @return
	 */
	public boolean isContemSIAPE(){
		if( this.vinculo == VinculoUsuarioBiblioteca.SERVIDOR_TECNO_ADMINISTRATIVO
		|| this.vinculo == VinculoUsuarioBiblioteca.DOCENTE )
			return true;
		else
			return false;
	}
	
	
	/**
	 *   M�todo que determina se a vari�el <code>matricula_SIAPE_CPF_CODIGO_BIBLIOTECA</code> contem uma matr�cula.
	 *
	 * @return
	 */
	public boolean isContemCodigoBiblioteca(){
		if( this.vinculo == VinculoUsuarioBiblioteca.BIBLIOTECA
		|| this.vinculo == VinculoUsuarioBiblioteca.BIBLIOTECA_EXTERNA 
		|| (this.vinculo == VinculoUsuarioBiblioteca.INATIVO && isBibliotecaInativa ) )
			return true;
		else
			return false;
	}
	
	
	/**
	 * Verifica se a informa��o passada � a informa��o do CPF (Utilizado quando o usu�rio n�o possui a informa��o do v�nculo)
	 * @return
	 */
	public boolean isContemCPF() {
		return isContemCPFPassaporte() && ! usuarioIdentificadoPeloPassaporte;
	}	
	
	/**
	 * Verifica se a informa��o passada � a informa��o do Passaporte (Utilizado quando o usu�rio n�o possui a informa��o do v�nculo)
	 * @return
	 */
	public boolean isContemPassaporte() {
		return isContemCPFPassaporte() &&  usuarioIdentificadoPeloPassaporte;
	}	
	
	/**
	 *  Verifica se o v�nculo de  um docentes externo
	 *
	 * @return
	 */
	public boolean isEhDocenteExterno() {
		return this.vinculo == VinculoUsuarioBiblioteca.DOCENTE_EXTERNO;
	}
	
	
	
	//// sets e gets ///
	
	public String getNomeUsuario() {
		return nomeUsuario;
	}
	
	public String getMatricula() {
		return codigoIdentificacaoUsuario;
	}


	public String getSiape() {
		return codigoIdentificacaoUsuario;
	}
	
	public String getCPF() {
		return codigoIdentificacaoUsuario;
	}
	
	public String getPassaporte() {
		return codigoIdentificacaoUsuario;
	}
	
	public String getCodigoBiblioteca() {
		return codigoIdentificacaoUsuario;
	}
	
	public String getCodigoIdentificacaoUsuario() {
		return codigoIdentificacaoUsuario;
	}
	
	public Integer getIdFoto() {
		return idFoto;
	}
	public VinculoUsuarioBiblioteca getVinculo() {
		return vinculo;
	}
	public String getTelefone() {
		return telefone != null ? telefone : "N�O INFORMADO";
	}
	public String getEmail() {
		return email != null ? email : "N�O INFORMADO";
	}
	public String getCargo() {
		return cargo != null ? cargo : "N�O INFORMADO";
	}
	public String getLotacao() {
		return lotacao != null ? lotacao : "N�O INFORMADO";
	}
	public String getCurso() {
		return curso != null ? curso : "N�O INFORMADO";
	}
	public String getCentro() {
		return centro != null ? centro : "N�O INFORMADO";
	}

	public int getIdUsuarioBiblioteca() {
		return idUsuarioBiblioteca;
	}

	public String getLoginUsuario() {
		return loginUsuario;
	}

	public String getSenha() {
		return hashSenha ;
	}

	public void setIdUsuarioBiblioteca(int idUsuarioBiblioteca) {
		this.idUsuarioBiblioteca = idUsuarioBiblioteca;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public void setLoginUsuario(String loginUsuario) {
		this.loginUsuario = loginUsuario;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public boolean isMobilidadeEstudantil() {
		return mobilidadeEstudantil;
	}

	public void setMobilidadeEstudantil(boolean mobilidadeEstudantil) {
		this.mobilidadeEstudantil = mobilidadeEstudantil;
	}

	public boolean isIniciacaoCientifica() {
		return iniciacaoCientifica;
	}

	public void setIniciacaoCientifica(boolean iniciacaoCientifica) {
		this.iniciacaoCientifica = iniciacaoCientifica;
	}


	/**
	 * Retorna o endere�o j� formatado para visualiza��o
	 *
	 * @return
	 */
	public String getEndereco() {
		
		StringBuilder endereco = new StringBuilder();
		
		if(StringUtils.notEmpty(logradouro))
			endereco.append(logradouro);
		
		if(StringUtils.notEmpty(numero))
			endereco.append(" "+numero);
		
		if(StringUtils.notEmpty(bairro)){
			
			if(StringUtils.isEmpty(endereco.toString()))
				endereco.append(" "+bairro);
			else
				endereco.append(", "+bairro);
		}
		
		if(StringUtils.notEmpty(complemento)){
			
			if(StringUtils.isEmpty(endereco.toString()))
				endereco.append(" "+complemento);
			else
				endereco.append(". "+complemento);
		}
		
		if(StringUtils.notEmpty(cep)){
			
			if(StringUtils.isEmpty(endereco.toString()))
				endereco.append(" "+cep);
			else
				endereco.append(". "+cep);
		}
		
		if(StringUtils.isEmpty(endereco.toString())) 
			return "N�O INFORMADO";
		else
			return endereco.toString();
	}
	
	
	/**
	 * Valido para usu�rios externos 
	 *
	 * @return
	 */
	public boolean isVinculoCancelado(){
		return StringUtils.notEmpty(motivoCancelamentoVinculoUsuarioExterno);
	}
	
	/**
	 * Valido para usu�rios externos 
	 *
	 * @return
	 */
	public String getMotivoCancelamentoVinculo(){
		return motivoCancelamentoVinculoUsuarioExterno;
	}

	public Integer getTipoDiscente() {
		return tipoDiscente;
	}
	
	
}