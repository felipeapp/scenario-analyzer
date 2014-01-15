/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Interface com as operações da aplicação Desktop de circulação
 * 
 * Toda a comunicação é realizada utilizando objetos Dto (Data Transfer Objects), objetos 
 * sem comportamentos.
 * 
 * @author Gleydson
 * @since 22/09/2008
 * @version 1.1 Jadson 20/05/2011 o serviço virou um webservice
 */
@WebService
public interface BibliotecaCirculacaoDesktopRemoteService {

	public final int TIPO_CAMPO_BUSCA_USUARIO_CPF = 1;
	public final int TIPO_CAMPO_BUSCA_USUARIO_MATRICULA = 2;
	public final int TIPO_CAMPO_BUSCA_USUARIO_NOME = 3;
	public final int TIPO_CAMPO_BUSCA_USUARIO_SIAPE = 4;
	public final int TIPO_CAMPO_BUSCA_USUARIO_PASSAPORTE = 5;
	
	/**
	 * Usado para gerar a chave de segurança para acesso aos arquivos de fotos do SIGAA. Como o
	 * arquivo que contém a chave que gera a chave de segurança está no lado do servidor, não dava
	 * para colocar este método do lado do cliente. Tinha que ser uma chamada remota.
	 * 
	 * @param idFoto o id da foto que se está querendo recuperar.
	 * @return
	 */
	public String generateFotoKey (int idFoto);
	
	
	
	/**
     *  <p>Busca os dados de um usuário utilizando a digital </p> 
	 *  
     * @param digital
     * @param idBiblioteca
     * @return
     * @throws java.lang.Exception
     */
	public UsuarioBibliotecaDto populaDadosUsuarioUsandoDigital (byte[] digital) throws NegocioRemotoBibliotecaDesktopException;
	

	
	/**
	 * <p>Método utilizado para cadastrar a digital de um dedo do usuário na aplicação de circulação da biblioteca</p>
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
	 * Obtém as informações de um usuário da biblioteca. 
	 * Usado, por exemplo, no caso de uso: realizar empréstimos.
	 * 
	 * Retorna, além das informações do usuário, as informações sobre a situação do usuário 
	 * na biblioteca chamado o caso de uso "Verificar situação do Usuário", isso para ver se o usuário
	 * pode realizar a operação que está solicitando.
	 * 
	 * 
	 * IMPORTANTE Era bom retornar a senha(ou o hash) também, para realizar a autenticação no cliente e evitar
	 * de realizar várias chamadas caso o usuário digite a senha inválida, isso porque aqui as chamadas
	 * são remotas e isso tem um custo alto.
	 *
	 * @param identifacao a identificação do usuário da biblioteca, no caso de aluno vai ser 
	 * a matrícula, para professor, a matrícula do siape.
	 * 
	 * @param informacaoUsuario as informações do usuário já são passadas quando esse método é
	 * chamado do método identificarPessoaUsandoDigital(), quando o sistema remoto o chama passa nulo.
	 * 
	 * @param idBiblioteca A id da biblioteca onde o operador do programa desktop está.
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
	 * Busca os usuários cujo nome contenha o parâmetro passado.
	 * 
	 * @param nome
	 * @return
	 * @throws NegocioRemotoBibliotecaDesktopException
	 */
	public List <UsuarioBibliotecaDto> buscaUsuariosCirculacaoDesktop(String consulta, int tipoCampo, boolean buscarUsuariosExternos) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	/**
	 * Busca apenas os empréstimos ativos do usuário. 
	 * Esse método é usado para atualizar a lista dos empréstimos ativos do usuário depois
	 * que ele realiza alguma operação no sistema (EMPRESTAR, RENOVAR, DEVOLVER)
	 *
	 * @param idUsuario
	 * @return
	 */
	public List <EmprestimoDto> findEmprestimosAtivosUsuario (int idUsuario) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	
	
	
	
	/**
	 * Retorna as informações de um material informacional para o usuário verificar no sistema desktop.
	 *
	 * Tem que vir as informações do material, do título e informações da previsão de devolução, biblioteca do material 
	 * e, caso esteja empréstado, o usuário para quem está emprestado
	 *
	 * @param codigoBarras
	 * @param valorVinculoUsuario o vinculo do usuário para trazer os tipos de empréstimos e políticas que ele pode realizar. Pode não ser passado, se está buscando o material para devolução.
	 * 
	 * @return
	 */
	public MaterialInformacionalDto findMaterialByCodigoBarras (String codigoBarras, Integer valorVinculoUsuario) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	/**
	 * Consulta o material cujo código de barras é o passado, registra esta consulta e retorna
	 * o último empréstimo deste.
	 *
	 * @return
	 */
	public RetornoOperacoesCirculacaoDTO realizaCheckout (String codigoBarras, int idOperador) throws NegocioRemotoBibliotecaDesktopException;

	
	
	
    /**
     * Realiza um conjunto de empréstimos de materiais ou renovações de empréstimos de acordo com a operação dos EmprestimoDto passados
     * 
     * @param emprestimos     a lista de empréstimos passados
     * @param idUsuarioBiblioteca   o usuário biblioteca que está realizando as operações
     * @param senhaDigitada           A senha digitada pelo usuário em MD5, para atenticação dos empréstimos
     * @param idOperador  O operador que está reailzando os empréstimos
     * @return idRegistroEntrada guarda o registro de entrada do operador que se logou no sistema, já que o desktop não tem sessão.
     * @param idBibliotecaOperacao  o biblioteca que o usuário selecionou para utilizar no momento, para não permitir empréstar ou devolver materiais de outras bibliotecas mesmo ele tendo permissão
     *                                de empréstimos em outras bibliotecas, porque os dados do comprovante seriam impressos errados. 
     */
	public RetornoOperacoesCirculacaoDTO realizarOperacoes (List<EmprestimoDto> emprestimos, int idUsuarioBiblioteca, String senhaDigitada, int idOperador, int idRegistroEntrada, int idBibliotecaOperacao) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	/**
	 * Realiza a devolução de um empréstimo
	 *
	 * @param codigoBarras
	 * @param permitirMulta
	 * @param idUsuario O usuário que está operando o sistema.
	 * @param idBibliotecaOperacao  O biblioteca que o operador está utilizando no momento
	 * @return EmprestimoDto com as informações da devolução para imprimir o comprovante e guardar 
	 * a operação caso o operador queira desfazer. Como aqui é apenas 1 por vez não precisa retornar 
	 * uma lista de operações.
	 */
	public RetornoOperacoesCirculacaoDTO devolverMaterial (MaterialInformacionalDto material, int idUsuario, int idBibliotecaOperacao) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	
	/**
	 * Desfaz uma operação realizada no desktop. É usado nos casos nos quais o operador realizou uma
	 * operação equivocadamente.
	 * 
	 * Ex.: Era para devolver e ele renovou. Só é permitido desfazer as 10 últimas operações.
	 *      
	 * Como essas operações de empréstimos só envolvem duas classes "Emprestimo" e "MaterialInformacional", passando
	 * o id dessas duas classes e o tipo da operação que foi feita, é possível desfazer realizando 
	 * todos os cálculos ao contrário que as operações normalmente fazem.
	 *      
	 * Somente CHEFES da seção de circulação podem desfazer as operações.
	 *      
	 *
	 * @param idItem
	 * @param idEmprestimo
	 * @param tipoOpecacao
	 * @param loginChefe o login do chefe que autorizou a operação
	 * @param senhaChefe a senha do chefe que autorizou a operação
	 * @param idUsuarioOperador operador que estava na hora que a operação foi desfeita.
	 */
	public void desfazOperacao (int idEmprestimo, int tipoOpecacao, String loginChefe, String senhaChefe, int idUsuarioOperador, String operacionalSystem) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
	/**
	 * Realiza o logon do usuário do desktop no sistema. É importante para impedir
	 * que qualquer usuário utilize o sistema e registrar todas as operações realizadas pelo usuário
	 * autorizado caso algum dia necessite de auditoria.
	 * 
	 * É retornado para o cliente um mapa do usuário com seus registros de entrada. 
	 * 
	 * O cliente deve manter essa informação de maneira que sempre que o cliente mandar
	 * essa informação para o servidor significa que ele foi autenticado 
	 * (talvez seja necessário gerar uma senha temporária). Se não tiver, significa que o cliente 
	 * precisa se autenticar novamente. 
	 * 
	 * @param login o login do usuário
	 * @param senha a senha do usuário
	 * @param inetAdd o endereço da estação remota de onde o usuário está se logando.
	 * @return um mapa do usuário com seus registros de entrada.
	 * 
	 * @throws NegocioRemotoBibliotecaDesktopException
	 */
	public ParametrosRetornoLogarCirculacaoDTO logar (String login, String senha, String hostAddress, String hostName, String operacionalSystem) throws NegocioRemotoBibliotecaDesktopException;
	
	
	
}
