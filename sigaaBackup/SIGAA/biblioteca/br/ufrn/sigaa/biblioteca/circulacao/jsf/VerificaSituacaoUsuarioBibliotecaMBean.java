/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 07/10/2008
 *
 */

package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.autenticacao.AutValidator;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InfoVinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.InformacaoEmprestimosPorVinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoEmiteQuitacaoBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaFactory;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ObtemVinculoUsuarioBibliotecaStrategy;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.SituacaoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.mensagens.MensagensBiblioteca;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * 
 * <p>MBean que gerencia as p�ginas nas quais pode-se verificar a situa��o dos usu�rio da biblioteca, 
 * al�m de ser respons�vel por emitir o documento de quita��o. </p>
 * 
 * <p> <strong> <i>Esse Mbean � chamado na parte de circula��o no m�dulo da biblioteca e tamb�m dos portais servidor , discente e docente, 
 * caso o usu�rio esteja varificando sua pr�pria situa��o e emitindo o seu documento de quita��o. </i> </strong> </p>
 * 
 * <i>
 * <p>Para emitir o comprante autenticado, � preciso implementar a interface <code>AutValidator</code> <br/>
 *    criar um novo tipo de Documento em <code>TipoDocumentoAutenticado</code> e registrar essa classe 
 *    <span style="text-decoration: line-through;"> no arquivo <code>/br/ufrn/arg/seguranca/autenticacao/validadores.properties</code> </span>
 *    na tabela comum.emissao_documento_autenticado_validadores.
 * </p>
 * 
 * 
 * <p>
 * 	   A interface <code>AutValidator</code> possui 2 m�todos: <br/>
 * 	<ul>
 * 		<li>validaDigest:  valida se o documento de quita��o � verdadeiro </li>
 * 		<li>exibir: chamado apenas se o usu�rio desejar visualizar o documento novamente no momento da valida��o.</li>
 * 	</ul>
 * 
 * </i>
 * 
 * </p>
 * 
 * <p>Esses m�todos ser�o chamado a partir da �rea p�blica pelo MBean:  ValidacaoMBean</p>
 * 
 * <p>
 * <strong>REGRAS IMPORTANTES DO DOCUMENTO DE QUITA��O: </strong>
 * 		<ul>
 * 			<li> Se o usu�rio realizar um novo empr�stimo na biblioteca o documento de quita��o deve ser invalidado </li>
 * 			<li> Se depois emitir o comprovante de quita��o, um usu�rio que nunca tinha utilizado a biblioteca fizer um cadastro na biblioteca, o documento de quita��o deve ser invalidado  </li>
 * 			<li> Pessoas sem CPF ou passaporte n�o podem emitir o documento de quita��o, evitando assim o problema com as pessoas duplicadas no sistema.</li>
 * 		</ul>
 * </p>
 * 
 * @author jadson
 * @since 07/10/2008
 * @version 1.0 cria��o da classe
 * @version 2.0 30/04/2010 altera a emiss�o do comprovante de quita��o para o usu�rio escolher para qual v�nculo quer emitir o comprovante
 *     e tamb�m adicionado a possibilidade de emitir o comprovante de quita��o para os usu�rio que nunca utilizaram a biblioteca.
 * @version 3.0 07/07/2010 altera a emiss�o do comprovante de quita��o para permitir a emiss�o de documentos parciais para cada v�nculo que o usu�rio possuir. 
 *          Caso o usu�rio possua empr�stimos em outro v�nculo essa informa��o deve sair no comprovante.
 * @version 3.1 22/07/2010 valida se a pessoa do usu�rio selecionado possui  CPF para poder emitir o comprovante de quita��o. Para bloquear os casos de pessoas 
 * duplicadas no sistema. Como as pessoas duplicadas est�o sempre sem CPF, bloqueiando pelo CPF deve resolver essa situa��o.
 * @version 4.0 13/04/2011 Introduzindo o conseito de usu�rios quitados. Caso o usu�rio emita um comprovante a sua conta deve ser <strong>quitada</strong>. 
 *                         Sendo quitada n�o poder� mais realizar empr�timos com o v�nculo quitado. Dever� fazer recadastro para, se tiver, pegar o novo v�nculo.     
 */
@Component("verificaSituacaoUsuarioBibliotecaMBean")
@Scope("request")
public class VerificaSituacaoUsuarioBibliotecaMBean extends SigaaAbstractController<Object> implements AutValidator, PesquisarUsuarioBiblioteca {
	
	
	/**  P�gina onde o usu�rio pode verificar a sua situa��o, a verifica��o pode ser feita diversas vezes */
	public static final String PAGINA_VERIFICA_SITUACAO_USUARIO = "/biblioteca/circulacao/verificaSituacaoUsuarioBiblioteca.jsp";

	/**  O documento de quita��o em si, o usu�rio s� pode emitir caso o v�nculo seja quitado. Precisa est� na parte p�blica porque a confer�ncia da validade do documento � p�blica. */
	public static final String PAGINA_DOCUMENTO_QUITACAO = "/public/biblioteca/circulacao/documentoQuitacaoBiblioteca.jsp";

	/**  Se der algum erro o usu�rio � direcionado para essa p�gina */
	public static final String PAGINA_ERRO_DOCUMENTO_QUITACAO = "/public/biblioteca/circulacao/erroDocumentoQuitacaoBiblioteca.jsp";
	
	
	/** Mant�m uma c�pia do comprovante para evitar gerar mais de uma vez se o usu�rio ficar atualizando a p�gina. */
	private EmissaoDocumentoAutenticado comprovante;
	
	
	/** Informa se o usu�rio n�o possui nenhum pend�ncia na biblioteca,  */
	private boolean situacaoSemPendencias = false; 
	
	/** Informa que a situ��o do usu�rio permite a ele emitir o documento de quita��o ou n�o, algumas pend�ncias d�o direito a emitir o documento de quita��o, como bloqueio e supens�o. 
	 * Outras n�o, como a multa, tem que pagar para quitar o v�nculo */
	private boolean podeEmitirDocumentoQuitacao = false; 
	
	
	/** O Vinculo atual que o usu�rio est� utilizando */
	private UsuarioBiblioteca usuarioBibliotecaVinculoAtual;
	
	/** <p>Guarda todas as contas que o usu�rio j� teve na biblioteca para verificar a sua situa��o em outras contas. 
	 * Por exemplo, a suspens�o n�o impede a emiss�o a emiss�o do comprovante de quita��o, ent�o o usu�rio pode 
	 * est� suspenso e quitar a conta atual, ent�o como ele n�o tinha conta atual, estava aparecendo como "SEM PEDENCIAS"
	 * mas estava suspen��o.<p>
	 *
	 * <p>Agora est� sendo feito um bloqueio na cadastro do usu�rio para ele n�o quitar e usar outra conta para se livrar a puni��o.
	 * e na verifica��o tamb�m vai usar essa informa��o para mostrar para o usu�rio. Como ele n�o pode fazer um novo cadastro enquanto a
	 * puni��o n�o for finalizada, n�o tem perido dele fazer novos empr�stimos.
	 * </p>
	 * 
	 */
	private List<UsuarioBiblioteca> contasUsuarioBiblioteca;
	
	
	/** As informa��es do vinculo do usu�rio priorit�rio que ele usar para fazer os empr�timos 
	 *  Apenas para mostra na p�gina, no documento de quita��o vai sair o v�nculo que o usu�rio escolher */
	private InformacoesUsuarioBiblioteca informacoesUsuarioBiblioteca;
	
	
	
	/** 
	 * <p>Uma lista que cont�m as informa��es do v�nculo e dos empr�stimos feitos com esses v�nculos.</p>
	 * <p>Guarda apenas os v�nculo ativos no momento (para motrar melhor ao usu�rio)</p>
	 */
	private List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> informacoesEmprestimosPorVinculoAtivos;
	
	/** 
	 * <p>Uma lista que cont�m as informa��es do v�nculo e dos empr�stimos feitos com esses v�nculos.</p>
	 * <p>Guarda apenas os v�nculo n�o mais ativos no momento (para motrar melhor ao usu�rio)</p>
	 */
	private List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> informacoesEmprestimosPorVinculoInativos;
	
	/** 
	 *  <p>As informa��es do vinculo do usu�rio escolhido para emitir o documento de quita��o</p>
	 *  <p> <strong>S�O AS INFORMA��ES QUE V�O SER IMPRESSAS NO COMPROVANTE <strong> </p>
	 *  */
	private InformacaoEmprestimosPorVinculoUsuarioBiblioteca informacoesEmprestimosPorVinculoEscolhido 
		= new InformacaoEmprestimosPorVinculoUsuarioBiblioteca() ;
	

	/** Guarda o c�digo que vai autenticar o documento do SIGAA. */
	private String codigoSeguranca;

	/** Guarda a poss�veis situa��es que o usu�rio pode estar na biblioteca. */
	private List<SituacaoUsuarioBiblioteca> situacoes = new ArrayList<SituacaoUsuarioBiblioteca>();
	
	
	
	
	
	/* ******************************************************************************************************
	 * Dados para emitir o comprovante quando o usu�rio n�o possui cadastro na biblioteca.	(n�o tem um <code>Usu�rioBiblioteca</code>)	
	 * ******************************************************************************************************/
	
	/**
	 * dados do usu�rio da biblioteca quando o usu�rio n�o possui cadastro na biblioteca.	
	 */
	private Integer idPessoaEmissaoComprovante;
	/**
	 *  dados do usu�rio  da biblioteca quando o usu�rio n�o possui cadastro na biblioteca.	
	 */
	private String nomeUsuarioComprovante;
	/**
	 *  dados do usu�rio  da biblioteca quando o usu�rio n�o possui cadastro na biblioteca.	
	 */
	private String cpfUsuarioComprovante;
	/**
	 *  dados do usu�rio  da biblioteca quando o usu�rio n�o possui cadastro na biblioteca.	
	 */
	private String passaporteUsuarioComprovante;
	/**
	 * dados do usu�rio  da biblioteca quando o usu�rio n�o possui cadastro na biblioteca.	
	 */
	private Date   dataNascimentoUsuarioComprovante;
	
	/* ****************************************************************************************************** */
	
	
	/* ******************************************************************************************************
	 * Dados para emitir o comprovante quando o usu�rio n�o possui cadastro na biblioteca.	(n�o tem um <code>Usu�rioBiblioteca</code>)	
	 * ******************************************************************************************************/
	/** Cont�m o id da biblioteca para a qual vai ser emitido o comprovante.*/
	private Integer idBibliotecaEmissaoComprovante;

	
	/* ****************************************************************************************************** */
	
	/** Guardam as informa��es do V�nculo escolhido pelo usu�rio para emitir o documento de quita��o */
	private String idUsuarioBibliotecaSelecionado;
	/** Guardam as informa��es do V�nculo escolhido pelo usu�rio para emitir o documento de quita��o */
	private String vinculoSelecionado;
	/** Guardam as informa��es do V�nculo escolhido pelo usu�rio para emitir o documento de quita��o */
	private String identificacaoVinculoSelecionado;
	
	
	
	
	
	/**
	 * Indica que o usu�rio est� verificando a sua pr�pria situa��o, neste caso n�o deve habilitar o 
	 * bot�o que permite ao usu�rio voltar para a tela de busca da circula��o, j� que o usu�rio s� pode ver a situa��o dela.<br/>
	 * Por quest�es de seguran�a o padr�o � verdadeiro. <br/>
	 * Ela vai assumir um valor falso quando este caso de uso � acessado de dentro do m�dulo da biblioteca<br/>.
	 */
	private boolean verificandoMinhaSituacao = true;
	
	
	
	/**
	 * Informa-se que se est� emitindo o documento de quita��o parcial do usu�rio.
	 */
	private boolean usuarioNaoPossuiNenhumVinculo = false;
	
	
	
	/**
	 *  <p>Inicia o caso de uso de verificar a situa��o de qualquer usu�rio da biblioteca. </p>
	 *  <p>Utilizado Geralmente pelos bibliotec�rios ou pessoal do DAE, e DAP </p>
	 *  
	 *  <p><strong>Observa��o: </strong> <br/> <br/> 
	 *  
	 *  N�o esquecer que esse m�todo � chamado dos m�dulos de <strong>STRITUS SENSO</strong> , de <strong>GRADUA��O</strong> e da parte 
	 *  de <strong>APOSENTADORIAS DO DAP SIGRH</strong>>. <br/> 
	 *  ENT�O LEMBRE-SE QUALQUER ALTERA��O NESSE M�TODO � PRECISO TESTAR OS LINKS DE L� TAMB�M.<br/> <br/> 
	 *  
	 *  </p>
	 *  
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/menus/circulacao.jsp</li>
	 *   	<li>/sigaa.war/graduacao/menus/relatorios_dae.jsp</li>
	 * 		<li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 *   </ul>
	 *
	 *
	 *
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String iniciarVerificacao() throws DAOException, SegurancaException{
		
		/* PPG, DAE utilizam a busca dos usu�rios da biblioteca para visualiza a situa��o do usu�rio.
		 * Para n�o ficar criando v�rios papeis aqui, quem quiser emitir o documento de quita��o atribuir o 
		 * papel: BIBLIOTECA_EMITE_DECLARACAO_QUITACAO
		 */
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_EMITE_DECLARACAO_QUITACAO);
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, true, true, "Verificando a situa��o do Usu�rio", null);
	}
	
	
	//////////////////////////// M�todos da pesquisa de usu�rios biblioteca padr�o /////////////////
	
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		verificandoMinhaSituacao = false;
		return carregaVinculosUsuario();
	}
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		idPessoaEmissaoComprovante = p.getId();
		idBibliotecaEmissaoComprovante = null;
	}
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		idBibliotecaEmissaoComprovante = biblioteca.getId();
		idPessoaEmissaoComprovante = null;
	}

	/**
	 * Ver coment�rios da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa,String... parametros) {
		
		if(parametroDePessoa){
			cpfUsuarioComprovante = parametros[0];
			passaporteUsuarioComprovante = parametros[1];
			nomeUsuarioComprovante = parametros[2];
			
			if(parametros[4] != null){
				
				try {
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					dataNascimentoUsuarioComprovante = format.parse(parametros[4]);
				} catch (ParseException e1) {
					e1.printStackTrace();
					dataNascimentoUsuarioComprovante = null;
				}
			}
		}
		
	}

	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 * <p>  Usado para o usu�rio da biblioteca consultar sua pr�pria situa��o. </p>
	 * 
	 * M�todo chamado pelas seguintes JSP's:
	 * <ul>
	 * 	<li>  /portais/discente/menu_discente.jsp
	 *  <li> /portais/docente/menu_docente.jsp
	 *  <li> /biblioteca/menus/modulo_biblioteca.jsp
	 * </ul>
	 * @return
	 * @throws DAOException, ArqException 
	 * @throws NegocioException 
	 */
	public String verificaSituacaoUsuarioAtualmenteLogado() throws DAOException, ArqException{
		verificandoMinhaSituacao = true;
		idPessoaEmissaoComprovante = getUsuarioLogado().getPessoa().getId();
		return carregaVinculosUsuario();
	}

	
	/**
	 *  <p>Inicia o caso de uso verificar os v�nculo do usu�rio quando o usu�rio tenta se cadastrar na biblioteca e n�o possui nenhum v�nculo ativo. </p>
	 * 
	 * M�todo chamado pelas seguintes JSP's:
	 * <ul>
	 * 	 <li>/sigaa.war/biblioteca/circulacao/criarSenhaUsuarioBiblioteca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException, ArqException 
	 * @throws NegocioException 
	 */
	public String verificaSituacaoUsuarioPassado() throws DAOException, ArqException{
		verificandoMinhaSituacao = true;
		idPessoaEmissaoComprovante = getParameterInt("idPessoaVerificaSituacao");
		return carregaVinculosUsuario();
	}
	

	/**
	 * <p>M�todo padr�o para montar as informa��es dos v�nculos do usu�rio.</p>
	 * 
	 * 
	 *
	 * @param idPessoa o usu�rio logado ou o usu�rio selecionado na busca padr�o de circula��o, dependendo de onde o caso de uso foi chamado.
	 * @return
	 * @throws ArqException
	 */
	private String carregaVinculosUsuario() throws ArqException{

		zeraDadosEmissaoNovoComprovante();
		
		prepareMovimento(SigaaListaComando.EMITE_QUITACAO_BIBLIOTECA);
		
		informacoesEmprestimosPorVinculoAtivos = new ArrayList<InformacaoEmprestimosPorVinculoUsuarioBiblioteca>();
		informacoesEmprestimosPorVinculoInativos = new ArrayList<InformacaoEmprestimosPorVinculoUsuarioBiblioteca>();
		
		if(idPessoaEmissaoComprovante == null && idBibliotecaEmissaoComprovante == null){ // caso o usu�rio acesse a p�gina diretamente pelo navegador sem usar o links do sistema
			addMensagemErro("As informa��es do usu�rio n�o foram selecionadas corretamente, por favor reinicie o processo.");
			return null;
		}
		
		UsuarioBibliotecaDao dao = null;
		EmprestimoDao emprestimoDao = null;
		
		try {
			dao = getDAO(UsuarioBibliotecaDao.class);
			emprestimoDao = getDAO(EmprestimoDao.class);
			
			ObtemVinculoUsuarioBibliotecaStrategy strategy = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo();
			
			/*
			 * as contas que o usu�rio, para saber quais vinculo ele j� utilizou, s� esses precisa buscar os empr�stimos
			 */
			contasUsuarioBiblioteca = new ArrayList<UsuarioBiblioteca>(); 
			
			/*
			 *  as informa��es sobre todos os vinculo que o usu�rio tem no sistema, mesmo aqueles cancelados, se n�o tem empr�stimo pode emitir a quita��o para ele
			 */
			List<InfoVinculoUsuarioBiblioteca> vinculos = new ArrayList<InfoVinculoUsuarioBiblioteca>();
			
			
			if(idPessoaEmissaoComprovante != null){
				contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByPessoa(idPessoaEmissaoComprovante );
				vinculos = strategy.getVinculos(idPessoaEmissaoComprovante);
				//vinculosNaoUsados = recuperaVinculosNaoUsados(vinculos, contasUsuarioBiblioteca);
			}
			
			if(idBibliotecaEmissaoComprovante != null){
				contasUsuarioBiblioteca = dao.findUsuarioBibliotecaAtivoByBiblioteca(idBibliotecaEmissaoComprovante );
			}
			
			/**
			 * Para obter a situa��o atual do usu�rio
			 */
			usuarioBibliotecaVinculoAtual = UsuarioBibliotecaUtil.recuperaUsuarioNaoQuitadosAtivos(contasUsuarioBiblioteca);
			
			
			// Caso o usu�rio j� tenha cadastro, mas nunca fez empr�stimos, vai est� nulo o v�nculo, mas precisa para 
			// a quita�ao ent�o redireciona o usu�rio para ele fazer o recadastro
			if(usuarioBibliotecaVinculoAtual != null && ( usuarioBibliotecaVinculoAtual.getVinculo() == null || usuarioBibliotecaVinculoAtual.getIdentificacaoVinculo() == null)){
					addMensagemErro("N�o foi poss�vel recuperar a informa��o do v�nculo do usu�rio, realize um recadastro para consertar essa informa��o.");
				if(verificandoMinhaSituacao ){
					return ((CadastroUsuarioBibliotecaMBean)getMBean("cadastroUsuarioBibliotecaMBean")).iniciarAutoCadastro();
				}else{
					return null;
				}
			}
			
			
			
			informacoesUsuarioBiblioteca = strategy.getInformacoesUsuario(null, idPessoaEmissaoComprovante, idBibliotecaEmissaoComprovante);
			
			// Adiciona os v�nculos ativos no sistema
			informacoesEmprestimosPorVinculoAtivos.addAll( montaInformacoesEmprestimosVinculosAtivos(vinculos, contasUsuarioBiblioteca, emprestimoDao, strategy) );
			
			// Adiciona os v�nculos inativos no
			informacoesEmprestimosPorVinculoInativos.addAll( montaInformacoesEmpresimosVinculosInativos(vinculos, contasUsuarioBiblioteca, emprestimoDao, strategy) );
			
			
			
			
			
			
			/** 
			 * Se o usu�rio nunca usou a biblioteca, nem possui v�nculo ativo vai habilitar um 
			 * bot�o especial para emitir o comprovante
			 * Nesse caso n�o tem como bloquear nenhum v�nculo 
			 */
			if(informacoesEmprestimosPorVinculoAtivos.size() == 0  && informacoesEmprestimosPorVinculoInativos.size() == 0){
				usuarioNaoPossuiNenhumVinculo = true;
			}else{
				usuarioNaoPossuiNenhumVinculo = false;
			}
			


			/**
			 * Mosta a situa��o do usu�rio atualmente
			 */
			verificarSituacaoUsuario();
			
			return telaMostraSituacaoUsuarioBiblioteca();
			
		} catch (NegocioException e){
			addMensagem(MensagensBiblioteca.USUARIO_NAO_CADASTRADO);
			redirect(CadastroUsuarioBibliotecaMBean.PAGINA_FORM_SENHA_USUARIO);
			return null;
		} finally {
			if (dao != null)  dao.close();
			if(emprestimoDao != null) emprestimoDao.close();
		}
	}

	/**
	 * 
	 * Re carrega os dados dos v�nculos do usu�rio caso o usu�rio volte para a tela utilizando o 
	 * voltar do documento de quita��o.
	 *  
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/documentoQuitacaoBiblioteca.jsp/</li>
	 *   </ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String atualizaInformacoesVinculosDiretoPagina() throws ArqException{
		carregaVinculosUsuario();
		return telaMostraSituacaoUsuarioBiblioteca();
	}
	
	


	/** Monta as informa��es dos v�nculos e seus empr�stimos para os v�nculos ativos do usu�rio */
	private List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> montaInformacoesEmprestimosVinculosAtivos
		(List<InfoVinculoUsuarioBiblioteca> vinculos, List<UsuarioBiblioteca> contasUsuarioBiblioteca, 
			EmprestimoDao emprestimoDao, ObtemVinculoUsuarioBibliotecaStrategy strategy) throws DAOException{
		
		List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> lista  = new ArrayList<InformacaoEmprestimosPorVinculoUsuarioBiblioteca>();
		
		
		
		// Para cada vinculo recuperado
		for (InfoVinculoUsuarioBiblioteca vinculo : vinculos) {
			
			if(vinculo.isAtivo()){ // esse m�todo processa s� os v�nculo ativos do usu�rio
			
				boolean achouVincululoAtual = false;
				
				UsuarioBiblioteca contaUsuarioBibliotecaDoVinculo = recuperaContaBiblioteca(vinculo, contasUsuarioBiblioteca);
				
				if(contaUsuarioBibliotecaDoVinculo != null){ // S� � o v�nculo atual se tiver uma conta na biblioteca
					achouVincululoAtual = isVinculoAtualmenteUtilizado(vinculo);
				}
			
				boolean vinculoNuncaUtilizado = true;
				
				List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();
				int qtdEmprestimosTotalVinculo =0;
				
				if(contaUsuarioBibliotecaDoVinculo != null){ // Se o usu�rio j� utilizou esse vinculo na biblioteca
					emprestimos = emprestimoDao.findEmprestimosAtivosPorVinculoUsuario(contaUsuarioBibliotecaDoVinculo);
					qtdEmprestimosTotalVinculo = emprestimoDao.countEmprestimosTotaisPorVinculoUsuario(contaUsuarioBibliotecaDoVinculo);
					vinculoNuncaUtilizado = false;
				}else{
					
					// Se o usu�rio nunca utilizou o v�nculo, adiciona uma conta na biblioteca, porque se ele quitar vai ter que criar a conta quitada no banco.
					Pessoa p = new Pessoa(idPessoaEmissaoComprovante, nomeUsuarioComprovante);
					p.setDataNascimento(dataNascimentoUsuarioComprovante);
					if(StringUtils.notEmpty(cpfUsuarioComprovante) )
						p.setCpf_cnpjString(cpfUsuarioComprovante);
					
					p.setPassaporte( passaporteUsuarioComprovante);
					
					contaUsuarioBibliotecaDoVinculo = new UsuarioBiblioteca(p , vinculo.getVinculo(), vinculo.getIdentificacaoVinculo());
					
					
				}
				
				/* ****  ATEN��O: n�o pode emitir a quita��o para v�nculo inativos ****/
				if( vinculo.getVinculo() != VinculoUsuarioBiblioteca.INATIVO ){ 
					
					String informacoesCompletasVinculo = strategy.recuperaInformacoesCompletasVinculos(vinculo.getVinculo(), vinculo.getIdentificacaoVinculo()).toUpperCase();
					
					lista.add( new InformacaoEmprestimosPorVinculoUsuarioBiblioteca(contaUsuarioBibliotecaDoVinculo, emprestimos, qtdEmprestimosTotalVinculo, informacoesCompletasVinculo, vinculoNuncaUtilizado, achouVincululoAtual));
				}
			}
			
		}
		
		return lista;
	}
	
	/** Verifica se o v�nculo passado � o que o usu�rio est� utilizando */
	private boolean isVinculoAtualmenteUtilizado(InfoVinculoUsuarioBiblioteca vinculo){
		
		if(usuarioBibliotecaVinculoAtual != null){ // Se existe 1 vinculo sendo utilizado
			
			if( vinculo.getVinculo() != null && usuarioBibliotecaVinculoAtual.getVinculo() != null
			     && vinculo.getIdentificacaoVinculo() != null && usuarioBibliotecaVinculoAtual.getIdentificacaoVinculo() != null
			     &&	vinculo.getVinculo() == usuarioBibliotecaVinculoAtual.getVinculo() 
			     && vinculo.getIdentificacaoVinculo().equals(usuarioBibliotecaVinculoAtual.getIdentificacaoVinculo()) ){
				return true;
			}
		}
		
		return false;
	}
	
	
	/** Recupera a conta na biblioteca do v�nculo, se existir */
	private UsuarioBiblioteca recuperaContaBiblioteca(InfoVinculoUsuarioBiblioteca vinculo, List<UsuarioBiblioteca> contasUsuarioBiblioteca){
		
		if( contasUsuarioBiblioteca != null)
		for(UsuarioBiblioteca contaUsuarioBiblioteca : contasUsuarioBiblioteca){
			
			if(contaUsuarioBiblioteca.getVinculo() != null && contaUsuarioBiblioteca.getVinculo().equals(vinculo.getVinculo()) 
					&& contaUsuarioBiblioteca.getIdentificacaoVinculo() != null && contaUsuarioBiblioteca.getIdentificacaoVinculo().equals(vinculo.getIdentificacaoVinculo()) ){
				return contaUsuarioBiblioteca;
			}
		}
		
		return null;
	}
	
	
	/** Monta as informa��es dos v�nculos e seus empr�stimos para os v�nculos inativos do usu�rio */
	private List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> montaInformacoesEmpresimosVinculosInativos(
			List<InfoVinculoUsuarioBiblioteca> vinculos, List<UsuarioBiblioteca> contasUsuarioBiblioteca
			, EmprestimoDao emprestimoDao, ObtemVinculoUsuarioBibliotecaStrategy strategy) throws DAOException{
		
		List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> lista  = new ArrayList<InformacaoEmprestimosPorVinculoUsuarioBiblioteca>();
		
		// Para cada vinculo
		for (InfoVinculoUsuarioBiblioteca vinculo : vinculos) { 
			
			if(! vinculo.isAtivo()){ // esse m�todo processa s� os v�nculo n�o ativos do usu�rio
				
				UsuarioBiblioteca contaUsuarioBibliotecaDoVinculo = recuperaContaBiblioteca(vinculo, contasUsuarioBiblioteca);
				
				boolean achouVincululoAtual = false;
				
				if(contaUsuarioBibliotecaDoVinculo != null){ // S� � o v�nculo atual se tiver uma conta na biblioteca
					achouVincululoAtual = isVinculoAtualmenteUtilizado(vinculo);
				}
				
				List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();
				int qtdEmprestimosTotalVinculo =0;
				
				boolean vinculoNuncaUtilizado = true;
				
				if(contaUsuarioBibliotecaDoVinculo != null){ // Se o usu�rio j� utilizou esse vinculo na biblioteca
					emprestimos = emprestimoDao.findEmprestimosAtivosPorVinculoUsuario(contaUsuarioBibliotecaDoVinculo);
					qtdEmprestimosTotalVinculo = emprestimoDao.countEmprestimosTotaisPorVinculoUsuario(contaUsuarioBibliotecaDoVinculo);
					vinculoNuncaUtilizado = false;
				}else{
					
					// Se o usu�rio nunca utilizou o v�nculo, adiciona uma conta na biblioteca, porque se ele quitar vai ter que criar a conta quitada no banco.
					Pessoa p = new Pessoa(idPessoaEmissaoComprovante, nomeUsuarioComprovante);
					p.setDataNascimento(dataNascimentoUsuarioComprovante);
					if(StringUtils.notEmpty(cpfUsuarioComprovante) )
						p.setCpf_cnpjString(cpfUsuarioComprovante);
					
					p.setPassaporte( passaporteUsuarioComprovante);
					
					contaUsuarioBibliotecaDoVinculo = new UsuarioBiblioteca(p , vinculo.getVinculo(), vinculo.getIdentificacaoVinculo());
					
				}
				
				/* ****  ATEN��O: n�o pode emitir a quita��o para v�nculo inativos ****/
				if( vinculo.getVinculo() != VinculoUsuarioBiblioteca.INATIVO ){
					
					String informacoesCompletasVinculo = strategy.recuperaInformacoesCompletasVinculos(vinculo.getVinculo(), vinculo.getIdentificacaoVinculo()).toUpperCase();
				
					lista.add( new InformacaoEmprestimosPorVinculoUsuarioBiblioteca(contaUsuarioBibliotecaDoVinculo, emprestimos, qtdEmprestimosTotalVinculo, informacoesCompletasVinculo, vinculoNuncaUtilizado, achouVincululoAtual));
				}
				
			}
			
		}
		
		return lista;
	}
	
	
	
	/**
	 * Zera os dados globais toda vida que se escolher um novo usu�rio.
	 *
	 */
	private void zeraDadosEmissaoNovoComprovante(){
		comprovante = null;

		podeEmitirDocumentoQuitacao = false; 
		usuarioBibliotecaVinculoAtual = null;
		informacoesUsuarioBiblioteca = null;
		informacoesEmprestimosPorVinculoEscolhido = new InformacaoEmprestimosPorVinculoUsuarioBiblioteca() ;
		informacoesEmprestimosPorVinculoAtivos = null;
		informacoesEmprestimosPorVinculoInativos = null;
		codigoSeguranca = null;
		situacoes = new ArrayList<SituacaoUsuarioBiblioteca>();
		identificacaoVinculoSelecionado = null;
		vinculoSelecionado = null;
		idUsuarioBibliotecaSelecionado = null;
		usuarioNaoPossuiNenhumVinculo = false;
		
	}
	

	
	
	/**
	 * Verifica a situa��o do usu�rio com rela��o aos empr�timos, se ele est� suspenso, 
	 * se possui empr�stimos ativos e se esses empr�stimos ativos est�o atrazados.
	 * 
	 * @throws DAOException, ArqException 
	 * @throws NegocioException 
	 */
	private void verificarSituacaoUsuario() throws DAOException, ArqException, NegocioException {
		
		situacoes = new ArrayList<SituacaoUsuarioBiblioteca>();
		
//		if(usuarioBibliotecaVinculoAtual != null){
//			
//			String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario( usuarioBibliotecaVinculoAtual);
//			
//			if(StringUtils.notEmpty(motivoBloqueio))
//				situacoes.add(SituacaoUsuarioBiblioteca.ESTA_BLOQUEADO);
//		
//			situacoes.addAll( VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(usuarioBibliotecaVinculoAtual.getId()));
//			situacoes.addAll( VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiEmprestimosEmAbertoOUAtrasadosBiblioteca(usuarioBibliotecaVinculoAtual.getId()) );
//			
//			if(situacoes.isEmpty()){
//				situacoes.add(SituacaoUsuarioBiblioteca.SEM_PENDENCIA);
//				situacaoSemPendencias = true;
//			}else{
//				situacaoSemPendencias = false;
//			}
//		}else{ // Se n�o tem nenhum v�nculo atual ativo, ent�o teoricamente n�o possui empr�stimos ativos
			
			if(contasUsuarioBiblioteca != null && contasUsuarioBiblioteca.size() > 0 ){
				
				String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario( contasUsuarioBiblioteca.get(0) ); // s� precisa verificar a primeira, pois � por pessoa.
				
				if(StringUtils.notEmpty(motivoBloqueio))
					situacoes.add(SituacaoUsuarioBiblioteca.ESTA_BLOQUEADO);
				
				
				for(UsuarioBiblioteca contaBiblioteca: contasUsuarioBiblioteca){
					
					situacoes.addAll( VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiEmprestimosEmAbertoOUAtrasadosBiblioteca(contaBiblioteca.getId()) );
					
				}
				
				// S� precisa de 1 conta, porque o m�todo j� verifica para todas //
				UsuarioBiblioteca contaBibliotecaExemplo = contasUsuarioBiblioteca.get(0);
				
				situacoes.addAll( VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiPunicoesBiblioteca(
						contaBibliotecaExemplo.getIdentificadorPessoa(), contaBibliotecaExemplo.getIdentificadorBiblioteca() ));
				
			}
			
			if(situacoes.isEmpty()){
				situacoes.add(SituacaoUsuarioBiblioteca.SEM_PENDENCIA);
				situacaoSemPendencias = true;
			}else{
				situacaoSemPendencias = false;
			}
		//}
		
		podeEmitirDocumentoQuitacao = true;
		
		for (SituacaoUsuarioBiblioteca situacao : situacoes) {
			if( situacao.isSituacaoImpedeEmissaoDocumentoQuitacao() ){
				podeEmitirDocumentoQuitacao = false;
				break;
			}	
		}
		
	}
	
	
	/**
	 * 
	 * Volta para a busca do usu�rio
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/verificaSituacaoUsuario.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String voltaTelaBusca(){
		return ((BuscaUsuarioBibliotecaMBean) getMBean("buscaUsuarioBibliotecaMBean")).telaBuscaUsuarioBiblioteca();
	}
	
	
	
	
	
	/* ********************************************************************************************
	 * 
	 *                      Parte para emitir o documento de autentica��o
	 * 
	 * ********************************************************************************************/

	
	
	
	/**
	 *  M�todo que configura o vinculo escolhido do usu�rio com atualizar do setAtribute na p�gina. 
	 * 
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/verificaSituacaoUsuario.jsp/</li>
	 *   </ul>
	 *
	 * @param evt
	 * @throws ArqException
	 */
	public void configuraVinculoEscolhido(ActionEvent evt) throws ArqException { 
		idUsuarioBibliotecaSelecionado = ((Integer) evt.getComponent().getAttributes().get("idUsuarioBibliotecaSelecionado")).toString();
		vinculoSelecionado = ((VinculoUsuarioBiblioteca) evt.getComponent().getAttributes().get("vinculoSelecionado")).toString();
		identificacaoVinculoSelecionado = ((Integer) evt.getComponent().getAttributes().get("identificacaoVinculoSelecionado")).toString();
	}
	
	
	
	/**
	 * <p>  Emite o documento de quita��o, s� deve ser chamado se o usu�rio n�o possuir empr�stimos ativos (atrasados ou n�o) </p>
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/verificaSituacaoUsuario.jsp/</li>
	 *   </ul> 
	 * 
	 * 
	 * @param e
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String emitirDocumentoQuitacao() throws ArqException {
		
		// Se a situ��o est� ok
		if(podeEmitirDocumentoQuitacao ){
			/* *************************************************************************************
			 *  Seguran�a inserida para caso o usu�rio tente acessar a p�gina do documento diretamente.
			 * *************************************************************************************/
			getCurrentRequest().setAttribute("liberaEmissao", true); 
		}
	
		/**
		 * Caso especial onde o usu�rio n�o possui nenhum v�nculo para usar a biblioteca, mas quer emitir o comprovante.	
		 */
		if(usuarioNaoPossuiNenhumVinculo)
			return emitirDocumentoQuitacaoSemVinculo();
			
		
		
		///// Encontra o vinculo selecionado  ////
		boolean encontrouVinculoEntreOsAtivos = false;
		
		for (InformacaoEmprestimosPorVinculoUsuarioBiblioteca infoEmpVinc : informacoesEmprestimosPorVinculoAtivos) {
			
			if(Integer.parseInt(idUsuarioBibliotecaSelecionado) == infoEmpVinc.getUsuarioBiblioteca().getId() 
					&& Integer.parseInt(identificacaoVinculoSelecionado) == infoEmpVinc.getUsuarioBiblioteca().getIdentificacaoVinculo()
					&& Integer.parseInt( vinculoSelecionado) == infoEmpVinc.getUsuarioBiblioteca().getVinculo().getValor()){
				informacoesEmprestimosPorVinculoEscolhido = infoEmpVinc;
				encontrouVinculoEntreOsAtivos =true;
				break;
			}
		}
		
		if( ! encontrouVinculoEntreOsAtivos){
			for (InformacaoEmprestimosPorVinculoUsuarioBiblioteca infoEmpVinc : informacoesEmprestimosPorVinculoInativos) {
				
				if(Integer.parseInt(idUsuarioBibliotecaSelecionado) == infoEmpVinc.getUsuarioBiblioteca().getId() 
						&& Integer.parseInt(identificacaoVinculoSelecionado) == infoEmpVinc.getUsuarioBiblioteca().getIdentificacaoVinculo()
						&& Integer.parseInt( vinculoSelecionado) == infoEmpVinc.getUsuarioBiblioteca().getVinculo().getValor()){
					informacoesEmprestimosPorVinculoEscolhido = infoEmpVinc;
					break;
				}
			}
		}
		
		
		try{
			
			
			informacoesEmprestimosPorVinculoEscolhido.setInfoUsuarioBiblioteca( new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(informacoesEmprestimosPorVinculoEscolhido.getUsuarioBiblioteca(), null, null));
		
			
			MovimentoEmiteQuitacaoBiblioteca mov = new MovimentoEmiteQuitacaoBiblioteca(informacoesEmprestimosPorVinculoEscolhido);
			mov.setCodMovimento(SigaaListaComando.EMITE_QUITACAO_BIBLIOTECA);
			comprovante = execute(mov);
		
			// Como na p�gina do documento o usu�rio vai utilizar o bot�o voltar, tem que preparar o pr�ximo movimento
			prepareMovimento( SigaaListaComando.EMITE_QUITACAO_BIBLIOTECA); 
			
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		codigoSeguranca = comprovante != null ? comprovante.getCodigoSeguranca() : null;
		
		return telaDocumentoQuitacao();

	}
	
	
	
	/**
	 * Emite o documento de quita��o para usu�rios que nunca tiveram nem poder�o ter contas na biblioteca 
	 *
	 * @return
	 * @throws ArqException
	 */
	private String emitirDocumentoQuitacaoSemVinculo() throws ArqException{

		if(informacoesUsuarioBiblioteca.getCodigoIdentificacaoUsuario() == null ){
			addMensagemErro("Usu�rio n�o tem informa��es para emitir o documento de quita��o");
			return null;
		}
		
		try{
			
			MovimentoEmiteQuitacaoBiblioteca mov = new MovimentoEmiteQuitacaoBiblioteca(informacoesUsuarioBiblioteca, idPessoaEmissaoComprovante, idBibliotecaEmissaoComprovante);
			mov.setCodMovimento(SigaaListaComando.EMITE_QUITACAO_BIBLIOTECA);
			comprovante = execute(mov);
		
			// Como na p�gina do documento o usu�rio vai utilizar o bot�o voltar, tem que preparar o pr�ximo movimento
			prepareMovimento( SigaaListaComando.EMITE_QUITACAO_BIBLIOTECA); 

		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		codigoSeguranca = comprovante != null ? comprovante.getCodigoSeguranca() : null;
		
		informacoesEmprestimosPorVinculoEscolhido.setInfoUsuarioBiblioteca( informacoesUsuarioBiblioteca);
		
		return telaDocumentoQuitacao();
	}
	
	

	/**
	 * <p> Realizar a valida��o do comprovante de autentica��o do documento.</p>
	 * <p> <strong> IMPORTANTE: </strong> caso o usu�rio realize algum novo empr�stimo, o comprovante DEVE ser invalidado. </p>
	 * 
	 * @see br.ufrn.arq.seguranca.autenticacao.AutValidator#validaDigest(br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado)
	 * 
	 * <br><br>
	 * M�todo n�o invocado por JSP�s
	 * � public por causa da arquitetura.
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovanteLocal) {
		
		EmprestimoDao emprestimoDao = null;
		UsuarioBibliotecaDao usuarioDao = null;
		try {
		
			emprestimoDao = getDAO(EmprestimoDao.class); 
			usuarioDao = getDAO(UsuarioBibliotecaDao.class);
			String dadosAuxiliares = comprovanteLocal.getDadosAuxiliares();
			
			/*
			 * <p>IdUsuariobiblioteca:  Usu�rios com v�nculos</p>
			 * 
			 * <p>IdPessoa:  Usu�rios sem v�nculos</p>
			 */
			int id = Integer.parseInt(   dadosAuxiliares.substring(1, dadosAuxiliares.length() ) );
			
			String semente = "";
			
			if(dadosAuxiliares.startsWith("#")){ // O comprovante foi emitido para um usu�rio com v�nculo
				
				UsuarioBiblioteca usuarioUtilizadoEmissaoComprovante = usuarioDao.findInformacoesUsuarioBibliotecaAtivos(id);
			    int qtdEmprestimosTotalVinculo = emprestimoDao.countEmprestimosTotaisPorVinculoUsuario(usuarioUtilizadoEmissaoComprovante);
			    
				InformacoesUsuarioBiblioteca infoUser;
				
				try {
					infoUser = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioUtilizadoEmissaoComprovante, null, null);
				} catch (NegocioException e) {
					return false;
				}
				
				semente = infoUser.getNomeUsuario()+qtdEmprestimosTotalVinculo;
				
			}else{ // o comprovante foi emitido para uma pessoa sem v�nculo
			
				InformacoesUsuarioBiblioteca infoUser;
				
				try {
					infoUser = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, id, null);
				} catch (NegocioException e) {
					return false;
				}
				
				semente = infoUser.getNomeUsuario()+0;
			}
			
			
			
			String codigoVerificacao = AutenticacaoUtil.geraCodigoValidacao(comprovanteLocal, semente);
			
			if (codigoVerificacao.equals(comprovanteLocal.getCodigoSeguranca()))
				return true;  // Comprovante v�lido
			else
				return false;
				
			
		
		} catch (DAOException de) {
			de.printStackTrace();
			return false;
		} catch (ArqException ae) {
			ae.printStackTrace();
			return false;
		}finally{
			if(emprestimoDao != null) emprestimoDao.close();
		}
		
	}
	

	/**
	 * Re emite documento de quita��o do usu�rio da biblioteca no momento da verifica��o se o usu�rio que est� fazendo a valida��o do comprovante
	 * desejar visualizar o documento emitido pelo sistema.
	 * 
	 * @see br.ufrn.arq.seguranca.autenticacao.AutValidator#exibir(br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 
	 * <br>
	 * <br>
	 * M�todo n�o invocado por JSP�s � public por causa da arquitetura.
	 * 
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovanteLocal, HttpServletRequest req, HttpServletResponse res) {
	
		/*
		 * Informa que o documento est� sendo reimpresso na �rea p�blica do sistema.  ( N�o pode user nada de JSF ) 
		 */
		getCurrentRequest().setAttribute("exibindo_area_publica", true); 
		
		
		/* *************************************************************************************
		 *  Seguran�a inserida para caso o usu�rio tente acessar a p�gina do documento diretamente.
		 * *************************************************************************************/
		getCurrentRequest().setAttribute("liberaEmissao", true); 
		
		
		/*  Pega o c�digo de seguran�a do comprovante para re exibir ao usu�rio */
		codigoSeguranca = comprovanteLocal != null ? comprovanteLocal.getCodigoSeguranca() : null;
		
		UsuarioBibliotecaDao usuarioDao = null;
		
		
		try {
			
			usuarioDao = getDAO(UsuarioBibliotecaDao.class);
			
			String dadosAuxiliares = comprovanteLocal != null ? comprovanteLocal.getDadosAuxiliares() : "";
			
			/*
			 * <p>IdUsuariobiblioteca:  Usu�rios com v�nculos</p>
			 * 
			 * <p>IdPessoa:  Usu�rios sem v�nculos</p>
			 */
			int id = Integer.parseInt(   dadosAuxiliares.substring(1, dadosAuxiliares.length() ) );
		
			if(dadosAuxiliares.startsWith("#")){
				UsuarioBiblioteca usuarioUtilizadoEmissaoComprovante = usuarioDao.findInformacoesUsuarioBibliotecaAtivos(id);
				informacoesEmprestimosPorVinculoEscolhido =  new InformacaoEmprestimosPorVinculoUsuarioBiblioteca(usuarioUtilizadoEmissaoComprovante, new ArrayList<Emprestimo>(), 0, "", false, false);
				informacoesEmprestimosPorVinculoEscolhido.setInfoUsuarioBiblioteca( new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioUtilizadoEmissaoComprovante, null, null));
				usuarioNaoPossuiNenhumVinculo = false;
			}else{
				informacoesEmprestimosPorVinculoEscolhido.setInfoUsuarioBiblioteca( new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(null, id, null));
				 usuarioNaoPossuiNenhumVinculo = true;
			}
			
			
			
			/* *************************************************************************************
			 *           IMPORTANTE TEM QUE REDIRECIONAR PARA A P�GINA DO COMPROVANTE
			 * *************************************************************************************/
			getCurrentRequest().setAttribute("verificaSituacaoUsuarioBibliotecaMBean", this);
			getCurrentRequest().getRequestDispatcher(PAGINA_DOCUMENTO_QUITACAO).forward(getCurrentRequest(), getCurrentResponse());
		
		}catch (Exception  e) {
			e.printStackTrace();
			
			try {
				getCurrentRequest().getRequestDispatcher(PAGINA_ERRO_DOCUMENTO_QUITACAO).forward(getCurrentRequest(), getCurrentResponse());
			} catch (ServletException e1) {
				e1.printStackTrace();
				tratamentoErroPadrao(e1);
			} catch (IOException e1) {
				e1.printStackTrace();
				tratamentoErroPadrao(e1);
			}
			
			getCurrentRequest().setAttribute("liberaEmissao", false); 
			
		}finally{
			if( usuarioDao != null ) usuarioDao.close(); 
		}
		
	}

	
	
	
	// Telas de navega��o.
	
	/**
	 * Retorna tela que exibe a situa��o do usu�rio. 
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/biblioteca/circulacao/documentoQuitacaoBiblioteca.jsp</li>
	 *   </ul>
	 * 
	 */
	private String telaMostraSituacaoUsuarioBiblioteca(){
		return forward(PAGINA_VERIFICA_SITUACAO_USUARIO);
	}
	
	
	/**
	 * Retorna a tela que exibe o documento de quita��o.
	 */
	private String telaDocumentoQuitacao(){
		return forward(PAGINA_DOCUMENTO_QUITACAO);
	}
	

	/**
	 * 
	 * Retorna a descri��o do sistema de bibliotecas parametrizada.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/documentoQuitacao.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String getDescricaoSistemaBiblioteca(){
		return ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.DESCRICAO_SUB_SISTEMA_BIBLIOTECA);
	}
	
	
	// sets e gets 

	
	
	public String getCodigoSeguranca() {
		return codigoSeguranca;
	}

	public boolean isSituacaoSemPendencias() {
		return situacaoSemPendencias;
	}


	public void setSituacaoSemPendencias(boolean situacaoSemPendencias) {
		this.situacaoSemPendencias = situacaoSemPendencias;
	}


	public void setCodigoSeguranca(String codigoSeguranca) {
		this.codigoSeguranca = codigoSeguranca;
	}

	public InformacoesUsuarioBiblioteca getInformacoesUsuarioBiblioteca() {
		return informacoesUsuarioBiblioteca;
	}

	public void setInformacoesUsuarioBiblioteca(InformacoesUsuarioBiblioteca informacoesUsuarioBiblioteca) {
		this.informacoesUsuarioBiblioteca = informacoesUsuarioBiblioteca;
	}

	public UsuarioBiblioteca getUsuarioBibliotecaVinculoAtual() {
		return usuarioBibliotecaVinculoAtual;
	}

	public void setUsuarioBibliotecaVinculoAtual(UsuarioBiblioteca usuarioBibliotecaVinculoAtual) {
		this.usuarioBibliotecaVinculoAtual = usuarioBibliotecaVinculoAtual;
	}

	public EmissaoDocumentoAutenticado getComprovante() {
		return comprovante;
	}

	public void setComprovante(EmissaoDocumentoAutenticado comprovante) {
		this.comprovante = comprovante;
	}

	public boolean isVerificandoMinhaSituacao() {
		return verificandoMinhaSituacao;
	}

	public InformacaoEmprestimosPorVinculoUsuarioBiblioteca getInformacoesEmprestimosPorVinculoEscolhido() {
		return informacoesEmprestimosPorVinculoEscolhido;
	}


	public void setInformacoesEmprestimosPorVinculoEscolhido(InformacaoEmprestimosPorVinculoUsuarioBiblioteca informacoesEmprestimosPorVinculoEscolhido) {
		this.informacoesEmprestimosPorVinculoEscolhido = informacoesEmprestimosPorVinculoEscolhido;
	}


	public void setVerificandoMinhaSituacao(boolean verificandoMinhaSituacao) {
		this.verificandoMinhaSituacao = verificandoMinhaSituacao;
	}

	public Integer getIdPessoaEmissaoComprovante() {
		return idPessoaEmissaoComprovante;
	}

	public void setIdPessoaEmissaoComprovante(Integer idPessoaEmissaoComprovante) {
		this.idPessoaEmissaoComprovante = idPessoaEmissaoComprovante;
	}

	public Integer getIdBibliotecaEmissaoComprovante() {
		return idBibliotecaEmissaoComprovante;
	}

	public void setIdBibliotecaEmissaoComprovante(Integer idBibliotecaEmissaoComprovante) {
		this.idBibliotecaEmissaoComprovante = idBibliotecaEmissaoComprovante;
	}

	public String getNomeUsuarioComprovante() {
		return nomeUsuarioComprovante;
	}

	public void setNomeUsuarioComprovante(String nomeUsuarioComprovante) {
		this.nomeUsuarioComprovante = nomeUsuarioComprovante;
	}

	public String getCpfUsuarioComprovante() {
		return cpfUsuarioComprovante;
	}
	
	public String getPassaporteUsuarioComprovante() {
		return passaporteUsuarioComprovante;
	}

	public void setCpfUsuarioComprovante(String cpfUsuarioComprovante) {
		this.cpfUsuarioComprovante = cpfUsuarioComprovante;
	}

	public Date getDataNascimentoUsuarioComprovante() {
		return dataNascimentoUsuarioComprovante;
	}

	public void setDataNascimentoUsuarioComprovante(Date dataNascimentoUsuarioComprovante) {
		this.dataNascimentoUsuarioComprovante = dataNascimentoUsuarioComprovante;
	}


	public List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> getInformacoesEmprestimosPorVinculoAtivos() {
		return informacoesEmprestimosPorVinculoAtivos;
	}

	public void setInformacoesEmprestimosPorVinculoAtivos(List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> informacoesEmprestimosPorVinculoAtivos) {
		this.informacoesEmprestimosPorVinculoAtivos = informacoesEmprestimosPorVinculoAtivos;
	}

	public List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> getInformacoesEmprestimosPorVinculoInativos() {
		return informacoesEmprestimosPorVinculoInativos;
	}

	public void setInformacoesEmprestimosPorVinculoInativos(List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> informacoesEmprestimosPorVinculoInativos) {
		this.informacoesEmprestimosPorVinculoInativos = informacoesEmprestimosPorVinculoInativos;
	}


	public boolean isUsuarioNaoPossuiNenhumVinculo() {
		return usuarioNaoPossuiNenhumVinculo;
	}


	public void setUsuarioNaoPossuiNenhumVinculo(boolean usuarioNaoPossuiNenhumVinculo) {
		this.usuarioNaoPossuiNenhumVinculo = usuarioNaoPossuiNenhumVinculo;
	}


	public List<SituacaoUsuarioBiblioteca> getSituacoes() {
		return situacoes;
	}

	public void setSituacoes(List<SituacaoUsuarioBiblioteca> situacoes) {
		this.situacoes = situacoes;
	}

	public String getIdUsuarioBibliotecaSelecionado() {
		return idUsuarioBibliotecaSelecionado;
	}

	public void setIdUsuarioBibliotecaSelecionado(String idUsuarioBibliotecaSelecionado) {
		this.idUsuarioBibliotecaSelecionado = idUsuarioBibliotecaSelecionado;
	}
	
	public String getVinculoSelecionado() {
		return vinculoSelecionado;
	}

	public void setVinculoSelecionado(String vinculoSelecionado) {
		this.vinculoSelecionado = vinculoSelecionado;
	}

	public String getIdentificacaoVinculoSelecionado() {
		return identificacaoVinculoSelecionado;
	}
	public void setIdentificacaoVinculoSelecionado(String identificacaoVinculoSelecionado) {
		this.identificacaoVinculoSelecionado = identificacaoVinculoSelecionado;
	}
//	public String getDescricaoBibliotecaComprovante() {
//		return descricaoBibliotecaComprovante;
//	}
//
//	public void setDescricaoBibliotecaComprovante(String descricaoBibliotecaComprovante) {
//		this.descricaoBibliotecaComprovante = descricaoBibliotecaComprovante;
//	}
	
	public boolean isPodeEmitirDocumentoQuitacao() {
		return podeEmitirDocumentoQuitacao;
	}

	public void setPodeEmitirDocumentoQuitacao(boolean podeEmitirDocumentoQuitacao) {
		this.podeEmitirDocumentoQuitacao = podeEmitirDocumentoQuitacao;
	}
	
	
	
}