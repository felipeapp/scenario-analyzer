/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean que gerencia as páginas nas quais pode-se verificar a situação dos usuário da biblioteca, 
 * além de ser responsável por emitir o documento de quitação. </p>
 * 
 * <p> <strong> <i>Esse Mbean é chamado na parte de circulação no módulo da biblioteca e também dos portais servidor , discente e docente, 
 * caso o usuário esteja varificando sua própria situação e emitindo o seu documento de quitação. </i> </strong> </p>
 * 
 * <i>
 * <p>Para emitir o comprante autenticado, é preciso implementar a interface <code>AutValidator</code> <br/>
 *    criar um novo tipo de Documento em <code>TipoDocumentoAutenticado</code> e registrar essa classe 
 *    <span style="text-decoration: line-through;"> no arquivo <code>/br/ufrn/arg/seguranca/autenticacao/validadores.properties</code> </span>
 *    na tabela comum.emissao_documento_autenticado_validadores.
 * </p>
 * 
 * 
 * <p>
 * 	   A interface <code>AutValidator</code> possui 2 métodos: <br/>
 * 	<ul>
 * 		<li>validaDigest:  valida se o documento de quitação é verdadeiro </li>
 * 		<li>exibir: chamado apenas se o usuário desejar visualizar o documento novamente no momento da validação.</li>
 * 	</ul>
 * 
 * </i>
 * 
 * </p>
 * 
 * <p>Esses métodos serão chamado a partir da área pública pelo MBean:  ValidacaoMBean</p>
 * 
 * <p>
 * <strong>REGRAS IMPORTANTES DO DOCUMENTO DE QUITAÇÃO: </strong>
 * 		<ul>
 * 			<li> Se o usuário realizar um novo empréstimo na biblioteca o documento de quitação deve ser invalidado </li>
 * 			<li> Se depois emitir o comprovante de quitação, um usuário que nunca tinha utilizado a biblioteca fizer um cadastro na biblioteca, o documento de quitação deve ser invalidado  </li>
 * 			<li> Pessoas sem CPF ou passaporte não podem emitir o documento de quitação, evitando assim o problema com as pessoas duplicadas no sistema.</li>
 * 		</ul>
 * </p>
 * 
 * @author jadson
 * @since 07/10/2008
 * @version 1.0 criação da classe
 * @version 2.0 30/04/2010 altera a emissão do comprovante de quitação para o usuário escolher para qual vínculo quer emitir o comprovante
 *     e também adicionado a possibilidade de emitir o comprovante de quitação para os usuário que nunca utilizaram a biblioteca.
 * @version 3.0 07/07/2010 altera a emissão do comprovante de quitação para permitir a emissão de documentos parciais para cada vínculo que o usuário possuir. 
 *          Caso o usuário possua empréstimos em outro vínculo essa informação deve sair no comprovante.
 * @version 3.1 22/07/2010 valida se a pessoa do usuário selecionado possui  CPF para poder emitir o comprovante de quitação. Para bloquear os casos de pessoas 
 * duplicadas no sistema. Como as pessoas duplicadas estão sempre sem CPF, bloqueiando pelo CPF deve resolver essa situação.
 * @version 4.0 13/04/2011 Introduzindo o conseito de usuários quitados. Caso o usuário emita um comprovante a sua conta deve ser <strong>quitada</strong>. 
 *                         Sendo quitada não poderá mais realizar emprétimos com o vínculo quitado. Deverá fazer recadastro para, se tiver, pegar o novo vínculo.     
 */
@Component("verificaSituacaoUsuarioBibliotecaMBean")
@Scope("request")
public class VerificaSituacaoUsuarioBibliotecaMBean extends SigaaAbstractController<Object> implements AutValidator, PesquisarUsuarioBiblioteca {
	
	
	/**  Página onde o usuário pode verificar a sua situação, a verificação pode ser feita diversas vezes */
	public static final String PAGINA_VERIFICA_SITUACAO_USUARIO = "/biblioteca/circulacao/verificaSituacaoUsuarioBiblioteca.jsp";

	/**  O documento de quitação em si, o usuário só pode emitir caso o vínculo seja quitado. Precisa está na parte pública porque a conferência da validade do documento é pública. */
	public static final String PAGINA_DOCUMENTO_QUITACAO = "/public/biblioteca/circulacao/documentoQuitacaoBiblioteca.jsp";

	/**  Se der algum erro o usuário é direcionado para essa página */
	public static final String PAGINA_ERRO_DOCUMENTO_QUITACAO = "/public/biblioteca/circulacao/erroDocumentoQuitacaoBiblioteca.jsp";
	
	
	/** Mantém uma cópia do comprovante para evitar gerar mais de uma vez se o usuário ficar atualizando a página. */
	private EmissaoDocumentoAutenticado comprovante;
	
	
	/** Informa se o usuário não possui nenhum pendência na biblioteca,  */
	private boolean situacaoSemPendencias = false; 
	
	/** Informa que a situção do usuário permite a ele emitir o documento de quitação ou não, algumas pendências dão direito a emitir o documento de quitação, como bloqueio e supensão. 
	 * Outras não, como a multa, tem que pagar para quitar o vínculo */
	private boolean podeEmitirDocumentoQuitacao = false; 
	
	
	/** O Vinculo atual que o usuário está utilizando */
	private UsuarioBiblioteca usuarioBibliotecaVinculoAtual;
	
	/** <p>Guarda todas as contas que o usuário já teve na biblioteca para verificar a sua situação em outras contas. 
	 * Por exemplo, a suspensão não impede a emissão a emissão do comprovante de quitação, então o usuário pode 
	 * está suspenso e quitar a conta atual, então como ele não tinha conta atual, estava aparecendo como "SEM PEDENCIAS"
	 * mas estava suspenção.<p>
	 *
	 * <p>Agora está sendo feito um bloqueio na cadastro do usuário para ele não quitar e usar outra conta para se livrar a punição.
	 * e na verificação também vai usar essa informação para mostrar para o usuário. Como ele não pode fazer um novo cadastro enquanto a
	 * punição não for finalizada, não tem perido dele fazer novos empréstimos.
	 * </p>
	 * 
	 */
	private List<UsuarioBiblioteca> contasUsuarioBiblioteca;
	
	
	/** As informações do vinculo do usuário prioritário que ele usar para fazer os emprétimos 
	 *  Apenas para mostra na página, no documento de quitação vai sair o vínculo que o usuário escolher */
	private InformacoesUsuarioBiblioteca informacoesUsuarioBiblioteca;
	
	
	
	/** 
	 * <p>Uma lista que contém as informações do vínculo e dos empréstimos feitos com esses vínculos.</p>
	 * <p>Guarda apenas os vínculo ativos no momento (para motrar melhor ao usuário)</p>
	 */
	private List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> informacoesEmprestimosPorVinculoAtivos;
	
	/** 
	 * <p>Uma lista que contém as informações do vínculo e dos empréstimos feitos com esses vínculos.</p>
	 * <p>Guarda apenas os vínculo não mais ativos no momento (para motrar melhor ao usuário)</p>
	 */
	private List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> informacoesEmprestimosPorVinculoInativos;
	
	/** 
	 *  <p>As informações do vinculo do usuário escolhido para emitir o documento de quitação</p>
	 *  <p> <strong>SÃO AS INFORMAÇÕES QUE VÃO SER IMPRESSAS NO COMPROVANTE <strong> </p>
	 *  */
	private InformacaoEmprestimosPorVinculoUsuarioBiblioteca informacoesEmprestimosPorVinculoEscolhido 
		= new InformacaoEmprestimosPorVinculoUsuarioBiblioteca() ;
	

	/** Guarda o código que vai autenticar o documento do SIGAA. */
	private String codigoSeguranca;

	/** Guarda a possíveis situações que o usuário pode estar na biblioteca. */
	private List<SituacaoUsuarioBiblioteca> situacoes = new ArrayList<SituacaoUsuarioBiblioteca>();
	
	
	
	
	
	/* ******************************************************************************************************
	 * Dados para emitir o comprovante quando o usuário não possui cadastro na biblioteca.	(não tem um <code>UsuárioBiblioteca</code>)	
	 * ******************************************************************************************************/
	
	/**
	 * dados do usuário da biblioteca quando o usuário não possui cadastro na biblioteca.	
	 */
	private Integer idPessoaEmissaoComprovante;
	/**
	 *  dados do usuário  da biblioteca quando o usuário não possui cadastro na biblioteca.	
	 */
	private String nomeUsuarioComprovante;
	/**
	 *  dados do usuário  da biblioteca quando o usuário não possui cadastro na biblioteca.	
	 */
	private String cpfUsuarioComprovante;
	/**
	 *  dados do usuário  da biblioteca quando o usuário não possui cadastro na biblioteca.	
	 */
	private String passaporteUsuarioComprovante;
	/**
	 * dados do usuário  da biblioteca quando o usuário não possui cadastro na biblioteca.	
	 */
	private Date   dataNascimentoUsuarioComprovante;
	
	/* ****************************************************************************************************** */
	
	
	/* ******************************************************************************************************
	 * Dados para emitir o comprovante quando o usuário não possui cadastro na biblioteca.	(não tem um <code>UsuárioBiblioteca</code>)	
	 * ******************************************************************************************************/
	/** Contém o id da biblioteca para a qual vai ser emitido o comprovante.*/
	private Integer idBibliotecaEmissaoComprovante;

	
	/* ****************************************************************************************************** */
	
	/** Guardam as informações do Vínculo escolhido pelo usuário para emitir o documento de quitação */
	private String idUsuarioBibliotecaSelecionado;
	/** Guardam as informações do Vínculo escolhido pelo usuário para emitir o documento de quitação */
	private String vinculoSelecionado;
	/** Guardam as informações do Vínculo escolhido pelo usuário para emitir o documento de quitação */
	private String identificacaoVinculoSelecionado;
	
	
	
	
	
	/**
	 * Indica que o usuário está verificando a sua própria situação, neste caso não deve habilitar o 
	 * botão que permite ao usuário voltar para a tela de busca da circulação, já que o usuário só pode ver a situação dela.<br/>
	 * Por questões de segurança o padrão é verdadeiro. <br/>
	 * Ela vai assumir um valor falso quando este caso de uso é acessado de dentro do módulo da biblioteca<br/>.
	 */
	private boolean verificandoMinhaSituacao = true;
	
	
	
	/**
	 * Informa-se que se está emitindo o documento de quitação parcial do usuário.
	 */
	private boolean usuarioNaoPossuiNenhumVinculo = false;
	
	
	
	/**
	 *  <p>Inicia o caso de uso de verificar a situação de qualquer usuário da biblioteca. </p>
	 *  <p>Utilizado Geralmente pelos bibliotecários ou pessoal do DAE, e DAP </p>
	 *  
	 *  <p><strong>Observação: </strong> <br/> <br/> 
	 *  
	 *  Não esquecer que esse método é chamado dos módulos de <strong>STRITUS SENSO</strong> , de <strong>GRADUAÇÃO</strong> e da parte 
	 *  de <strong>APOSENTADORIAS DO DAP SIGRH</strong>>. <br/> 
	 *  ENTÃO LEMBRE-SE QUALQUER ALTERAÇÃO NESSE MÉTODO É PRECISO TESTAR OS LINKS DE LÁ TAMBÉM.<br/> <br/> 
	 *  
	 *  </p>
	 *  
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		/* PPG, DAE utilizam a busca dos usuários da biblioteca para visualiza a situação do usuário.
		 * Para não ficar criando vários papeis aqui, quem quiser emitir o documento de quitação atribuir o 
		 * papel: BIBLIOTECA_EMITE_DECLARACAO_QUITACAO
		 */
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO
				, SigaaPapeis.BIBLIOTECA_EMITE_DECLARACAO_QUITACAO);
		
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, true, true, true, "Verificando a situação do Usuário", null);
	}
	
	
	//////////////////////////// Métodos da pesquisa de usuários biblioteca padrão /////////////////
	
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		verificandoMinhaSituacao = false;
		return carregaVinculosUsuario();
	}
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		idPessoaEmissaoComprovante = p.getId();
		idBibliotecaEmissaoComprovante = null;
	}
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
		idBibliotecaEmissaoComprovante = biblioteca.getId();
		idPessoaEmissaoComprovante = null;
	}

	/**
	 * Ver comentários da classe pai.<br/>
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
	 * <p>  Usado para o usuário da biblioteca consultar sua própria situação. </p>
	 * 
	 * Método chamado pelas seguintes JSP's:
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
	 *  <p>Inicia o caso de uso verificar os vínculo do usuário quando o usuário tenta se cadastrar na biblioteca e não possui nenhum vínculo ativo. </p>
	 * 
	 * Método chamado pelas seguintes JSP's:
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
	 * <p>Método padrão para montar as informações dos vínculos do usuário.</p>
	 * 
	 * 
	 *
	 * @param idPessoa o usuário logado ou o usuário selecionado na busca padrão de circulação, dependendo de onde o caso de uso foi chamado.
	 * @return
	 * @throws ArqException
	 */
	private String carregaVinculosUsuario() throws ArqException{

		zeraDadosEmissaoNovoComprovante();
		
		prepareMovimento(SigaaListaComando.EMITE_QUITACAO_BIBLIOTECA);
		
		informacoesEmprestimosPorVinculoAtivos = new ArrayList<InformacaoEmprestimosPorVinculoUsuarioBiblioteca>();
		informacoesEmprestimosPorVinculoInativos = new ArrayList<InformacaoEmprestimosPorVinculoUsuarioBiblioteca>();
		
		if(idPessoaEmissaoComprovante == null && idBibliotecaEmissaoComprovante == null){ // caso o usuário acesse a página diretamente pelo navegador sem usar o links do sistema
			addMensagemErro("As informações do usuário não foram selecionadas corretamente, por favor reinicie o processo.");
			return null;
		}
		
		UsuarioBibliotecaDao dao = null;
		EmprestimoDao emprestimoDao = null;
		
		try {
			dao = getDAO(UsuarioBibliotecaDao.class);
			emprestimoDao = getDAO(EmprestimoDao.class);
			
			ObtemVinculoUsuarioBibliotecaStrategy strategy = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo();
			
			/*
			 * as contas que o usuário, para saber quais vinculo ele já utilizou, só esses precisa buscar os empréstimos
			 */
			contasUsuarioBiblioteca = new ArrayList<UsuarioBiblioteca>(); 
			
			/*
			 *  as informações sobre todos os vinculo que o usuário tem no sistema, mesmo aqueles cancelados, se não tem empréstimo pode emitir a quitação para ele
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
			 * Para obter a situação atual do usuário
			 */
			usuarioBibliotecaVinculoAtual = UsuarioBibliotecaUtil.recuperaUsuarioNaoQuitadosAtivos(contasUsuarioBiblioteca);
			
			
			// Caso o usuário já tenha cadastro, mas nunca fez empréstimos, vai está nulo o vínculo, mas precisa para 
			// a quitaçao então redireciona o usuário para ele fazer o recadastro
			if(usuarioBibliotecaVinculoAtual != null && ( usuarioBibliotecaVinculoAtual.getVinculo() == null || usuarioBibliotecaVinculoAtual.getIdentificacaoVinculo() == null)){
					addMensagemErro("Não foi possível recuperar a informação do vínculo do usuário, realize um recadastro para consertar essa informação.");
				if(verificandoMinhaSituacao ){
					return ((CadastroUsuarioBibliotecaMBean)getMBean("cadastroUsuarioBibliotecaMBean")).iniciarAutoCadastro();
				}else{
					return null;
				}
			}
			
			
			
			informacoesUsuarioBiblioteca = strategy.getInformacoesUsuario(null, idPessoaEmissaoComprovante, idBibliotecaEmissaoComprovante);
			
			// Adiciona os vínculos ativos no sistema
			informacoesEmprestimosPorVinculoAtivos.addAll( montaInformacoesEmprestimosVinculosAtivos(vinculos, contasUsuarioBiblioteca, emprestimoDao, strategy) );
			
			// Adiciona os vínculos inativos no
			informacoesEmprestimosPorVinculoInativos.addAll( montaInformacoesEmpresimosVinculosInativos(vinculos, contasUsuarioBiblioteca, emprestimoDao, strategy) );
			
			
			
			
			
			
			/** 
			 * Se o usuário nunca usou a biblioteca, nem possui vínculo ativo vai habilitar um 
			 * botão especial para emitir o comprovante
			 * Nesse caso não tem como bloquear nenhum vínculo 
			 */
			if(informacoesEmprestimosPorVinculoAtivos.size() == 0  && informacoesEmprestimosPorVinculoInativos.size() == 0){
				usuarioNaoPossuiNenhumVinculo = true;
			}else{
				usuarioNaoPossuiNenhumVinculo = false;
			}
			


			/**
			 * Mosta a situação do usuário atualmente
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
	 * Re carrega os dados dos vínculos do usuário caso o usuário volte para a tela utilizando o 
	 * voltar do documento de quitação.
	 *  
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	
	


	/** Monta as informações dos vínculos e seus empréstimos para os vínculos ativos do usuário */
	private List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> montaInformacoesEmprestimosVinculosAtivos
		(List<InfoVinculoUsuarioBiblioteca> vinculos, List<UsuarioBiblioteca> contasUsuarioBiblioteca, 
			EmprestimoDao emprestimoDao, ObtemVinculoUsuarioBibliotecaStrategy strategy) throws DAOException{
		
		List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> lista  = new ArrayList<InformacaoEmprestimosPorVinculoUsuarioBiblioteca>();
		
		
		
		// Para cada vinculo recuperado
		for (InfoVinculoUsuarioBiblioteca vinculo : vinculos) {
			
			if(vinculo.isAtivo()){ // esse método processa só os vínculo ativos do usuário
			
				boolean achouVincululoAtual = false;
				
				UsuarioBiblioteca contaUsuarioBibliotecaDoVinculo = recuperaContaBiblioteca(vinculo, contasUsuarioBiblioteca);
				
				if(contaUsuarioBibliotecaDoVinculo != null){ // Só é o vínculo atual se tiver uma conta na biblioteca
					achouVincululoAtual = isVinculoAtualmenteUtilizado(vinculo);
				}
			
				boolean vinculoNuncaUtilizado = true;
				
				List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();
				int qtdEmprestimosTotalVinculo =0;
				
				if(contaUsuarioBibliotecaDoVinculo != null){ // Se o usuário já utilizou esse vinculo na biblioteca
					emprestimos = emprestimoDao.findEmprestimosAtivosPorVinculoUsuario(contaUsuarioBibliotecaDoVinculo);
					qtdEmprestimosTotalVinculo = emprestimoDao.countEmprestimosTotaisPorVinculoUsuario(contaUsuarioBibliotecaDoVinculo);
					vinculoNuncaUtilizado = false;
				}else{
					
					// Se o usuário nunca utilizou o vínculo, adiciona uma conta na biblioteca, porque se ele quitar vai ter que criar a conta quitada no banco.
					Pessoa p = new Pessoa(idPessoaEmissaoComprovante, nomeUsuarioComprovante);
					p.setDataNascimento(dataNascimentoUsuarioComprovante);
					if(StringUtils.notEmpty(cpfUsuarioComprovante) )
						p.setCpf_cnpjString(cpfUsuarioComprovante);
					
					p.setPassaporte( passaporteUsuarioComprovante);
					
					contaUsuarioBibliotecaDoVinculo = new UsuarioBiblioteca(p , vinculo.getVinculo(), vinculo.getIdentificacaoVinculo());
					
					
				}
				
				/* ****  ATENÇÃO: não pode emitir a quitação para vínculo inativos ****/
				if( vinculo.getVinculo() != VinculoUsuarioBiblioteca.INATIVO ){ 
					
					String informacoesCompletasVinculo = strategy.recuperaInformacoesCompletasVinculos(vinculo.getVinculo(), vinculo.getIdentificacaoVinculo()).toUpperCase();
					
					lista.add( new InformacaoEmprestimosPorVinculoUsuarioBiblioteca(contaUsuarioBibliotecaDoVinculo, emprestimos, qtdEmprestimosTotalVinculo, informacoesCompletasVinculo, vinculoNuncaUtilizado, achouVincululoAtual));
				}
			}
			
		}
		
		return lista;
	}
	
	/** Verifica se o vínculo passado é o que o usuário está utilizando */
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
	
	
	/** Recupera a conta na biblioteca do vínculo, se existir */
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
	
	
	/** Monta as informações dos vínculos e seus empréstimos para os vínculos inativos do usuário */
	private List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> montaInformacoesEmpresimosVinculosInativos(
			List<InfoVinculoUsuarioBiblioteca> vinculos, List<UsuarioBiblioteca> contasUsuarioBiblioteca
			, EmprestimoDao emprestimoDao, ObtemVinculoUsuarioBibliotecaStrategy strategy) throws DAOException{
		
		List<InformacaoEmprestimosPorVinculoUsuarioBiblioteca> lista  = new ArrayList<InformacaoEmprestimosPorVinculoUsuarioBiblioteca>();
		
		// Para cada vinculo
		for (InfoVinculoUsuarioBiblioteca vinculo : vinculos) { 
			
			if(! vinculo.isAtivo()){ // esse método processa só os vínculo não ativos do usuário
				
				UsuarioBiblioteca contaUsuarioBibliotecaDoVinculo = recuperaContaBiblioteca(vinculo, contasUsuarioBiblioteca);
				
				boolean achouVincululoAtual = false;
				
				if(contaUsuarioBibliotecaDoVinculo != null){ // Só é o vínculo atual se tiver uma conta na biblioteca
					achouVincululoAtual = isVinculoAtualmenteUtilizado(vinculo);
				}
				
				List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();
				int qtdEmprestimosTotalVinculo =0;
				
				boolean vinculoNuncaUtilizado = true;
				
				if(contaUsuarioBibliotecaDoVinculo != null){ // Se o usuário já utilizou esse vinculo na biblioteca
					emprestimos = emprestimoDao.findEmprestimosAtivosPorVinculoUsuario(contaUsuarioBibliotecaDoVinculo);
					qtdEmprestimosTotalVinculo = emprestimoDao.countEmprestimosTotaisPorVinculoUsuario(contaUsuarioBibliotecaDoVinculo);
					vinculoNuncaUtilizado = false;
				}else{
					
					// Se o usuário nunca utilizou o vínculo, adiciona uma conta na biblioteca, porque se ele quitar vai ter que criar a conta quitada no banco.
					Pessoa p = new Pessoa(idPessoaEmissaoComprovante, nomeUsuarioComprovante);
					p.setDataNascimento(dataNascimentoUsuarioComprovante);
					if(StringUtils.notEmpty(cpfUsuarioComprovante) )
						p.setCpf_cnpjString(cpfUsuarioComprovante);
					
					p.setPassaporte( passaporteUsuarioComprovante);
					
					contaUsuarioBibliotecaDoVinculo = new UsuarioBiblioteca(p , vinculo.getVinculo(), vinculo.getIdentificacaoVinculo());
					
				}
				
				/* ****  ATENÇÃO: não pode emitir a quitação para vínculo inativos ****/
				if( vinculo.getVinculo() != VinculoUsuarioBiblioteca.INATIVO ){
					
					String informacoesCompletasVinculo = strategy.recuperaInformacoesCompletasVinculos(vinculo.getVinculo(), vinculo.getIdentificacaoVinculo()).toUpperCase();
				
					lista.add( new InformacaoEmprestimosPorVinculoUsuarioBiblioteca(contaUsuarioBibliotecaDoVinculo, emprestimos, qtdEmprestimosTotalVinculo, informacoesCompletasVinculo, vinculoNuncaUtilizado, achouVincululoAtual));
				}
				
			}
			
		}
		
		return lista;
	}
	
	
	
	/**
	 * Zera os dados globais toda vida que se escolher um novo usuário.
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
	 * Verifica a situação do usuário com relação aos emprétimos, se ele está suspenso, 
	 * se possui empréstimos ativos e se esses empréstimos ativos estão atrazados.
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
//		}else{ // Se não tem nenhum vínculo atual ativo, então teoricamente não possui empréstimos ativos
			
			if(contasUsuarioBiblioteca != null && contasUsuarioBiblioteca.size() > 0 ){
				
				String motivoBloqueio = VerificaSituacaoUsuarioBibliotecaUtil.getMotivoBloqueadoUsuario( contasUsuarioBiblioteca.get(0) ); // só precisa verificar a primeira, pois é por pessoa.
				
				if(StringUtils.notEmpty(motivoBloqueio))
					situacoes.add(SituacaoUsuarioBiblioteca.ESTA_BLOQUEADO);
				
				
				for(UsuarioBiblioteca contaBiblioteca: contasUsuarioBiblioteca){
					
					situacoes.addAll( VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiEmprestimosEmAbertoOUAtrasadosBiblioteca(contaBiblioteca.getId()) );
					
				}
				
				// Só precisa de 1 conta, porque o método já verifica para todas //
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
	 * Volta para a busca do usuário
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 *                      Parte para emitir o documento de autenticação
	 * 
	 * ********************************************************************************************/

	
	
	
	/**
	 *  Método que configura o vinculo escolhido do usuário com atualizar do setAtribute na página. 
	 * 
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>  Emite o documento de quitação, só deve ser chamado se o usuário não possuir empréstimos ativos (atrasados ou não) </p>
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		// Se a situção está ok
		if(podeEmitirDocumentoQuitacao ){
			/* *************************************************************************************
			 *  Segurança inserida para caso o usuário tente acessar a página do documento diretamente.
			 * *************************************************************************************/
			getCurrentRequest().setAttribute("liberaEmissao", true); 
		}
	
		/**
		 * Caso especial onde o usuário não possui nenhum vínculo para usar a biblioteca, mas quer emitir o comprovante.	
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
		
			// Como na página do documento o usuário vai utilizar o botão voltar, tem que preparar o próximo movimento
			prepareMovimento( SigaaListaComando.EMITE_QUITACAO_BIBLIOTECA); 
			
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		}
		
		codigoSeguranca = comprovante != null ? comprovante.getCodigoSeguranca() : null;
		
		return telaDocumentoQuitacao();

	}
	
	
	
	/**
	 * Emite o documento de quitação para usuários que nunca tiveram nem poderão ter contas na biblioteca 
	 *
	 * @return
	 * @throws ArqException
	 */
	private String emitirDocumentoQuitacaoSemVinculo() throws ArqException{

		if(informacoesUsuarioBiblioteca.getCodigoIdentificacaoUsuario() == null ){
			addMensagemErro("Usuário não tem informações para emitir o documento de quitação");
			return null;
		}
		
		try{
			
			MovimentoEmiteQuitacaoBiblioteca mov = new MovimentoEmiteQuitacaoBiblioteca(informacoesUsuarioBiblioteca, idPessoaEmissaoComprovante, idBibliotecaEmissaoComprovante);
			mov.setCodMovimento(SigaaListaComando.EMITE_QUITACAO_BIBLIOTECA);
			comprovante = execute(mov);
		
			// Como na página do documento o usuário vai utilizar o botão voltar, tem que preparar o próximo movimento
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
	 * <p> Realizar a validação do comprovante de autenticação do documento.</p>
	 * <p> <strong> IMPORTANTE: </strong> caso o usuário realize algum novo empréstimo, o comprovante DEVE ser invalidado. </p>
	 * 
	 * @see br.ufrn.arq.seguranca.autenticacao.AutValidator#validaDigest(br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado)
	 * 
	 * <br><br>
	 * Método não invocado por JSP´s
	 * é public por causa da arquitetura.
	 */
	public boolean validaDigest(EmissaoDocumentoAutenticado comprovanteLocal) {
		
		EmprestimoDao emprestimoDao = null;
		UsuarioBibliotecaDao usuarioDao = null;
		try {
		
			emprestimoDao = getDAO(EmprestimoDao.class); 
			usuarioDao = getDAO(UsuarioBibliotecaDao.class);
			String dadosAuxiliares = comprovanteLocal.getDadosAuxiliares();
			
			/*
			 * <p>IdUsuariobiblioteca:  Usuários com vínculos</p>
			 * 
			 * <p>IdPessoa:  Usuários sem vínculos</p>
			 */
			int id = Integer.parseInt(   dadosAuxiliares.substring(1, dadosAuxiliares.length() ) );
			
			String semente = "";
			
			if(dadosAuxiliares.startsWith("#")){ // O comprovante foi emitido para um usuário com vínculo
				
				UsuarioBiblioteca usuarioUtilizadoEmissaoComprovante = usuarioDao.findInformacoesUsuarioBibliotecaAtivos(id);
			    int qtdEmprestimosTotalVinculo = emprestimoDao.countEmprestimosTotaisPorVinculoUsuario(usuarioUtilizadoEmissaoComprovante);
			    
				InformacoesUsuarioBiblioteca infoUser;
				
				try {
					infoUser = new ObtemVinculoUsuarioBibliotecaFactory().getEstrategiaVinculo().getInformacoesUsuario(usuarioUtilizadoEmissaoComprovante, null, null);
				} catch (NegocioException e) {
					return false;
				}
				
				semente = infoUser.getNomeUsuario()+qtdEmprestimosTotalVinculo;
				
			}else{ // o comprovante foi emitido para uma pessoa sem vínculo
			
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
				return true;  // Comprovante válido
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
	 * Re emite documento de quitação do usuário da biblioteca no momento da verificação se o usuário que está fazendo a validação do comprovante
	 * desejar visualizar o documento emitido pelo sistema.
	 * 
	 * @see br.ufrn.arq.seguranca.autenticacao.AutValidator#exibir(br.ufrn.arq.seguranca.autenticacao.EmissaoDocumentoAutenticado, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * 
	 * <br>
	 * <br>
	 * Método não invocado por JSP´s é public por causa da arquitetura.
	 * 
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public void exibir(EmissaoDocumentoAutenticado comprovanteLocal, HttpServletRequest req, HttpServletResponse res) {
	
		/*
		 * Informa que o documento está sendo reimpresso na área pública do sistema.  ( Não pode user nada de JSF ) 
		 */
		getCurrentRequest().setAttribute("exibindo_area_publica", true); 
		
		
		/* *************************************************************************************
		 *  Segurança inserida para caso o usuário tente acessar a página do documento diretamente.
		 * *************************************************************************************/
		getCurrentRequest().setAttribute("liberaEmissao", true); 
		
		
		/*  Pega o código de segurança do comprovante para re exibir ao usuário */
		codigoSeguranca = comprovanteLocal != null ? comprovanteLocal.getCodigoSeguranca() : null;
		
		UsuarioBibliotecaDao usuarioDao = null;
		
		
		try {
			
			usuarioDao = getDAO(UsuarioBibliotecaDao.class);
			
			String dadosAuxiliares = comprovanteLocal != null ? comprovanteLocal.getDadosAuxiliares() : "";
			
			/*
			 * <p>IdUsuariobiblioteca:  Usuários com vínculos</p>
			 * 
			 * <p>IdPessoa:  Usuários sem vínculos</p>
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
			 *           IMPORTANTE TEM QUE REDIRECIONAR PARA A PÁGINA DO COMPROVANTE
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

	
	
	
	// Telas de navegação.
	
	/**
	 * Retorna tela que exibe a situação do usuário. 
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/biblioteca/circulacao/documentoQuitacaoBiblioteca.jsp</li>
	 *   </ul>
	 * 
	 */
	private String telaMostraSituacaoUsuarioBiblioteca(){
		return forward(PAGINA_VERIFICA_SITUACAO_USUARIO);
	}
	
	
	/**
	 * Retorna a tela que exibe o documento de quitação.
	 */
	private String telaDocumentoQuitacao(){
		return forward(PAGINA_DOCUMENTO_QUITACAO);
	}
	

	/**
	 * 
	 * Retorna a descrição do sistema de bibliotecas parametrizada.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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