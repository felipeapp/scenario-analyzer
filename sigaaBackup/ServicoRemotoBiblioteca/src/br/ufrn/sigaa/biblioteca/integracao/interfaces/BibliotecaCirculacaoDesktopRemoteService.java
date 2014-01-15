/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/09/2008
 *
 */ 

package br.ufrn.sigaa.biblioteca.integracao.interfaces;

import java.util.List;

import javax.jws.WebService;

import br.ufrn.sigaa.biblioteca.integracao.dtos.EmprestimoDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.MaterialInformacionalDto;
import br.ufrn.sigaa.biblioteca.integracao.dtos.ParametrosRetornoLogarCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO;
import br.ufrn.sigaa.biblioteca.integracao.dtos.UsuarioBibliotecaDto;
import br.ufrn.sigaa.biblioteca.integracao.exceptions.NegocioRemotoBibliotecaDesktopException;



/**
 * Interface com as opera��es da aplica��o Desktop de circula��o
 * 
 * Toda a comunica��o � realizada utilizando objetos Dto (Data Transfer Objects), objetos 
 * sem comportamentos.
 * 
 * @author Gleydson
 * @since 22/09/2008
 * @version 1.1 Jadson 20/05/2011 o servi�o virou um webservice
 */
@WebService
public interface BibliotecaCirculacaoDesktopRemoteService {

	public final int TIPO_CAMPO_BUSCA_USUARIO_CPF = 1;
	public final int TIPO_CAMPO_BUSCA_USUARIO_MATRICULA = 2;
	public final int TIPO_CAMPO_BUSCA_USUARIO_NOME = 3;
	public final int TIPO_CAMPO_BUSCA_USUARIO_SIAPE = 4;
	public final int TIPO_CAMPO_BUSCA_USUARIO_PASSAPORTE = 5;
	
	/**
	 * Usado para gerar a chave de seguran�a para acesso aos arquivos de fotos do SIGAA. Como o
	 * arquivo que cont�m a chave que gera a chave de seguran�a est� no lado do servidor, n�o dava
	 * para colocar este m�todo do lado do cliente. Tinha que ser uma chamada remota.
	 * 
	 * @param idFoto o id da foto que se est� querendo recuperar.
	 * @return
	 */
	public String generateFotoKey (int idFoto);
	
	
	
	/**
     *  <p>Busca os dados de um usu�rio utilizando a digital </p> 
	 *  
     * @param digital
     * @param idBiblioteca
     * @return
     * @throws java.lang.Exception
     */
	public UsuarioBibliotecaDto populaDadosUsuarioUsandoDigital (byte[] digital) throws NegocioRemotoBibliotecaDesktopException;
	

	
	/**
	 * <p>M�todo utilizado para cadastrar a digital de um dedo do usu�rio na aplica��o de circula��o da biblioteca</p>
	 *
	 * @param idUsuarioBiblioteca
	 * @param digital
	 * @param tipoDedo
	 * @return
	 * @throws NegocioRemotoBibliotecaDesktopException
	 */
	public boolean cadastraDigitalUsuario (int idUsuarioBiblioteca, byte [] digital, String tipoDedo, int idOperador) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	
	/**
	 * 
	 * Obt�m as informa��es de um usu�rio da biblioteca. 
	 * Usado, por exemplo, no caso de uso: realizar empr�stimos.
	 * 
	 * Retorna, al�m das informa��es do usu�rio, as informa��es sobre a situa��o do usu�rio 
	 * na biblioteca chamado o caso de uso "Verificar situa��o do Usu�rio", isso para ver se o usu�rio
	 * pode realizar a opera��o que est� solicitando.
	 * 
	 * 
	 * IMPORTANTE Era bom retornar a senha(ou o hash) tamb�m, para realizar a autentica��o no cliente e evitar
	 * de realizar v�rias chamadas caso o usu�rio digite a senha inv�lida, isso porque aqui as chamadas
	 * s�o remotas e isso tem um custo alto.
	 *
	 * @param identifacao a identifica��o do usu�rio da biblioteca, no caso de aluno vai ser 
	 * a matr�cula, para professor, a matr�cula do siape.
	 * 
	 * @param informacaoUsuario as informa��es do usu�rio j� s�o passadas quando esse m�todo �
	 * chamado do m�todo identificarPessoaUsandoDigital(), quando o sistema remoto o chama passa nulo.
	 * 
	 * @param idBiblioteca A id da biblioteca onde o operador do programa desktop est�.
	 * 
	 * @return <pre>UsuarioBibliotecaDto 
	 *            |- EmprestimoDto_1 
	 *            |		|- MaterialInformacionalDto
	 *            |
	 *            |- EmprestimoDto_2
	 *            |		|- MaterialInformacionalDto
	 *            |
	 *            |- EmprestimoDto_N
	 *            |		|- MaterialInformacionalDto</pre>
	 *       
	 * @throws java.lang.Exception
	 */
	public UsuarioBibliotecaDto populaDadosUsuario (UsuarioBibliotecaDto dto) throws NegocioRemotoBibliotecaDesktopException;

	
	/**
	 * Busca os usu�rios cujo nome contenha o par�metro passado.
	 * 
	 * @param nome
	 * @return
	 * @throws NegocioRemotoBibliotecaDesktopException
	 */
	public List <UsuarioBibliotecaDto> buscaUsuariosCirculacaoDesktop(String consulta, int tipoCampo, boolean buscarUsuariosExternos) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	/**
	 * Busca apenas os empr�stimos ativos do usu�rio. 
	 * Esse m�todo � usado para atualizar a lista dos empr�stimos ativos do usu�rio depois
	 * que ele realiza alguma opera��o no sistema (EMPRESTAR, RENOVAR, DEVOLVER)
	 *
	 * @param idUsuario
	 * @return
	 */
	public List <EmprestimoDto> findEmprestimosAtivosUsuario (int idUsuario) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	
	
	
	
	/**
	 * Retorna as informa��es de um material informacional para o usu�rio verificar no sistema desktop.
	 *
	 * Tem que vir as informa��es do material, do t�tulo e informa��es da previs�o de devolu��o, biblioteca do material 
	 * e, caso esteja empr�stado, o usu�rio para quem est� emprestado
	 *
	 * @param codigoBarras
	 * @param valorVinculoUsuario o vinculo do usu�rio para trazer os tipos de empr�stimos e pol�ticas que ele pode realizar. Pode n�o ser passado, se est� buscando o material para devolu��o.
	 * 
	 * @return
	 */
	public MaterialInformacionalDto findMaterialByCodigoBarras (String codigoBarras, Integer valorVinculoUsuario) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	/**
	 * Consulta o material cujo c�digo de barras � o passado, registra esta consulta e retorna
	 * o �ltimo empr�stimo deste.
	 *
	 * @return
	 */
	public RetornoOperacoesCirculacaoDTO realizaCheckout (String codigoBarras, int idOperador) throws NegocioRemotoBibliotecaDesktopException;

	
	
	
    /**
     * Realiza um conjunto de empr�stimos de materiais ou renova��es de empr�stimos de acordo com a opera��o dos EmprestimoDto passados
     * 
     * @param emprestimos     a lista de empr�stimos passados
     * @param idUsuarioBiblioteca   o usu�rio biblioteca que est� realizando as opera��es
     * @param senhaDigitada           A senha digitada pelo usu�rio em MD5, para atentica��o dos empr�stimos
     * @param idOperador  O operador que est� reailzando os empr�stimos
     * @return idRegistroEntrada guarda o registro de entrada do operador que se logou no sistema, j� que o desktop n�o tem sess�o.
     * @param idBibliotecaOperacao  o biblioteca que o usu�rio selecionou para utilizar no momento, para n�o permitir empr�star ou devolver materiais de outras bibliotecas mesmo ele tendo permiss�o
     *                                de empr�stimos em outras bibliotecas, porque os dados do comprovante seriam impressos errados. 
     */
	public RetornoOperacoesCirculacaoDTO realizarOperacoes (List<EmprestimoDto> emprestimos, int idUsuarioBiblioteca, String senhaDigitada, int idOperador, int idRegistroEntrada, int idBibliotecaOperacao) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	/**
	 * Realiza a devolu��o de um empr�stimo
	 *
	 * @param codigoBarras
	 * @param permitirMulta
	 * @param idUsuario O usu�rio que est� operando o sistema.
	 * @param idBibliotecaOperacao  O biblioteca que o operador est� utilizando no momento
	 * @return EmprestimoDto com as informa��es da devolu��o para imprimir o comprovante e guardar 
	 * a opera��o caso o operador queira desfazer. Como aqui � apenas 1 por vez n�o precisa retornar 
	 * uma lista de opera��es.
	 */
	public RetornoOperacoesCirculacaoDTO devolverMaterial (MaterialInformacionalDto material, int idUsuario, int idBibliotecaOperacao) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	
	/**
	 * Desfaz uma opera��o realizada no desktop. � usado nos casos nos quais o operador realizou uma
	 * opera��o equivocadamente.
	 * 
	 * Ex.: Era para devolver e ele renovou. S� � permitido desfazer as 10 �ltimas opera��es.
	 *      
	 * Como essas opera��es de empr�stimos s� envolvem duas classes "Emprestimo" e "MaterialInformacional", passando
	 * o id dessas duas classes e o tipo da opera��o que foi feita, � poss�vel desfazer realizando 
	 * todos os c�lculos ao contr�rio que as opera��es normalmente fazem.
	 *      
	 * Somente CHEFES da se��o de circula��o podem desfazer as opera��es.
	 *      
	 *
	 * @param idItem
	 * @param idEmprestimo
	 * @param tipoOpecacao
	 * @param loginChefe o login do chefe que autorizou a opera��o
	 * @param senhaChefe a senha do chefe que autorizou a opera��o
	 * @param idUsuarioOperador operador que estava na hora que a opera��o foi desfeita.
	 */
	public void desfazOperacao (int idEmprestimo, int tipoOpecacao, String loginChefe, String senhaChefe, int idUsuarioOperador, String operacionalSystem) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	/**
	 * Realiza o logon do usu�rio do desktop no sistema. � importante para impedir
	 * que qualquer usu�rio utilize o sistema e registrar todas as opera��es realizadas pelo usu�rio
	 * autorizado caso algum dia necessite de auditoria.
	 * 
	 * � retornado para o cliente um mapa do usu�rio com seus registros de entrada. 
	 * 
	 * O cliente deve manter essa informa��o de maneira que sempre que o cliente mandar
	 * essa informa��o para o servidor significa que ele foi autenticado 
	 * (talvez seja necess�rio gerar uma senha tempor�ria). Se n�o tiver, significa que o cliente 
	 * precisa se autenticar novamente. 
	 * 
	 * @param login o login do usu�rio
	 * @param senha a senha do usu�rio
	 * @param inetAdd o endere�o da esta��o remota de onde o usu�rio est� se logando.
	 * @return um mapa do usu�rio com seus registros de entrada.
	 * 
	 * @throws NegocioRemotoBibliotecaDesktopException
	 */
	public ParametrosRetornoLogarCirculacaoDTO logar (String login, String senha, String hostAddress, String hostName, String operacionalSystem) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
}
