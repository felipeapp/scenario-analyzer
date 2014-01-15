/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/10/2008
 *
 */
package br.ufrn.sigaa.biblioteca.dominio;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;

/**
 *  <p>  Classe que guarda de forma temporária TODAS as informações do usuário da biblioteca para visualização o módulo de circulação da biblioteca. 
 *      Utilizada no módulo web, já que o desktop utiliza os DTOs para guardas as mesmas informações.</p>
 *      
 *  <p> 
 *      <i> (Além das informações que identificação o usuário biblioteca como nome, cpf, telefone, essa classe guarda, por exemplo,
 *      as informações do vínculo que o usuário deve usar para realizar os empréstimos.)
 *      </i> 
 *  </p>
 *	
 *	<p><strong>IMPORTANTE:</strong> Os dados dessa classe devem ser montados preferencionalmente utilizando o método:
 * 	{@link CirculacaoUtil.obtemInformacoesDoUsuarioDaBiblioteca}, assim o código fica centralizado e evitam-se erros.</p>
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
	 *  o nome do usuário (nome da pessoa ou descrição da biblioteca)
	 */
	private String nomeUsuario; 

	/**
	 *  guarda a matrícula, número do siape, cpf, ou código da Biblioteca, dependendo se é aluno, sevidor, usuário externo ou bibliotececa repectivamente.
	 *  guarda também o passaporte, caso a pessoa seja entrageira e não possua CPF
	 */
	private String codigoIdentificacaoUsuario; 
	
	/**
	 * Indica se o identificador for CPF ou Passaporte para mostrar corretamente ao usuário, não tem como saber essa informação, ela tem que
	 * ser passado no momento da construção do objeto.
	 */
	private boolean usuarioIdentificadoPeloPassaporte = true;
	
	/**
	 * Utilizando apenas para identifica quando o vínculo está inativo, se é uma pessoa ou biblioteca;
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
	 * A identificação do vínculo
	 */
	private Integer identificacaoVinculo;
	
	
	
	/**
	 *  Útil no envio de mensagem para um usuário
	 */
	private String loginUsuario;
	
	///////////////////////  Informações de contato //////////////////////////
	
	/**
	 *  telefone para contado
	 */
	private String telefone;

	/**
	 * email para contato
	 */
	private String email; 
	
	/**
	 * Nome da avenida, rua, praça, travessa, alameda, beco, etc..
	 */
	private String logradouro;

	/** Número do prédio */
	private String numero;

	/** Complemento (bloco, apartamento, etc.) */
	private String complemento;

	/** cep */
	private String cep;
	
	/**
	 * Nome da avenida, rua, praça, travessa, alameda, beco, etc..
	 */
	private String bairro;
	
	////////////////////////////////////////////////////////////////
	
	/**informações que são preenchidas quando o usuário é servidor (professor e técnico-administrativo) */
	private String cargo= null;
	/**informações que são preenchidas quando o usuário é servidor (professor e técnico-administrativo) */
	private String lotacao= null;
	
	
	/** informações que são preenchidas quando  é aluno (médio, graduação, pós) */ 
	private String curso= null;
	/** informações que são preenchidas quando  é aluno (médio, graduação, pós) */ 
	private String centro= null;
	
	/**
	 *  Para alunos especiais, se forem de mobilidade estudantil, poderão emprestar materiais nas bibliotecas.
	 */
	private boolean mobilidadeEstudantil;
	
	/**
	 * Indica se o discente participa de um projeto de iniciação científica
	 */
	private boolean iniciacaoCientifica;	

	
	/**
	 *  A senha do usuário, em MD5;
	 */
	private String hashSenha; 


	/** O motivo pelo qual o vínculo do usuário externo foi cancelado. */
	private String motivoCancelamentoVinculoUsuarioExterno; 
	
	/**
	 *  <p>Identifica o tipo do usuários para mostrar e validar os empréstimos, já que discentes especiais não fazem empréstimos.</p>
	 *  <p>Só usado caso o usuário seja discente</p>
	 *  <p>1 - Normal, 2 - Especial</p>
	 */
	private Integer tipoDiscente; 
	
	
	/**
	 * Construtor para usuário inativos para pessoa
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
	 * Construtor para usuário inativos para biblioteca
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
	 * Construtor para aluno ( aluno de pós, graduação ou de nível médio )
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
	 * Construtor para servidores (professor e técnicos-administrativos )
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
	 * Construtor para docentes externos (NÃO SÃO CONSIDERADOS SERVIDORES, NÃO TEM SIAPE)
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
	 * Construtor para usuários externos
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
	 * Construtor sem parâmetros
	 */
	public InformacoesUsuarioBiblioteca() {
	}

	
	
	/**
	 * Retorna a indentificação correta do usuário.
	 *
	 * @return
	 */
	public int getIdentificacaoVinculo(){
		
		return identificacaoVinculo;
	}
	
	
	/**
	 *   Método que determina se a variáel <code>matricula_SIAPE_CPF_CODIGO_BIBLIOTECA</code> contem uma matrícula.
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
	 *   Método que determina se a variáel <code>matricula_SIAPE_CPF_CODIGO_BIBLIOTECA</code> contem um cpf ou passaporte
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
	 *   Método que determina se a variáel <code>matricula_SIAPE_CPF_CODIGO_BIBLIOTECA</code> contem um siape.
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
	 *   Método que determina se a variáel <code>matricula_SIAPE_CPF_CODIGO_BIBLIOTECA</code> contem uma matrícula.
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
	 * Verifica se a informação passada é a informação do CPF (Utilizado quando o usuário não possui a informação do vínculo)
	 * @return
	 */
	public boolean isContemCPF() {
		return isContemCPFPassaporte() && ! usuarioIdentificadoPeloPassaporte;
	}	
	
	/**
	 * Verifica se a informação passada é a informação do Passaporte (Utilizado quando o usuário não possui a informação do vínculo)
	 * @return
	 */
	public boolean isContemPassaporte() {
		return isContemCPFPassaporte() &&  usuarioIdentificadoPeloPassaporte;
	}	
	
	/**
	 *  Verifica se o vínculo de  um docentes externo
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
		return telefone != null ? telefone : "NÃO INFORMADO";
	}
	public String getEmail() {
		return email != null ? email : "NÃO INFORMADO";
	}
	public String getCargo() {
		return cargo != null ? cargo : "NÃO INFORMADO";
	}
	public String getLotacao() {
		return lotacao != null ? lotacao : "NÃO INFORMADO";
	}
	public String getCurso() {
		return curso != null ? curso : "NÃO INFORMADO";
	}
	public String getCentro() {
		return centro != null ? centro : "NÃO INFORMADO";
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
	 * Retorna o endereço já formatado para visualização
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
			return "NÃO INFORMADO";
		else
			return endereco.toString();
	}
	
	
	/**
	 * Valido para usuários externos 
	 *
	 * @return
	 */
	public boolean isVinculoCancelado(){
		return StringUtils.notEmpty(motivoCancelamentoVinculoUsuarioExterno);
	}
	
	/**
	 * Valido para usuários externos 
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