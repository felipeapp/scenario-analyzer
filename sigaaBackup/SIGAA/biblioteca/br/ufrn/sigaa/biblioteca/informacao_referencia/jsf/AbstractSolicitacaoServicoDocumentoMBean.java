/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 11/04/2011
 *
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.WordUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServicoDocumento;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.MovimentoSolicitacaoDocumento;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.ProcessadorSolicitacaoOrientacao;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;

/**
 * MBean respons�vel pelos atendimento e solicita��o de servi�os realizada por discentes ou servidores � biblioteca.
 * 
 * @author Felipe Rivas
 */
public abstract class AbstractSolicitacaoServicoDocumentoMBean<T extends SolicitacaoServicoDocumento> extends SigaaAbstractController<T> 
																					implements ISolicitacaoServicoDocumentoMBean<T> {
	
	
	/** usado no bot�o no qual o usu�rio faz a solicita��o ou a altera */
	public static final String TEXTO_ALTERAR = "Alterar";
	
	/**
	 * Nome do managed bean que trata as opera��es que podem ser generalizadas para todos os tipos de solicita��es
	 */
	protected static final String NOME_MBEAN_GENERICO = "solicitacaoServicoDocumentoMBean";
	
	/**
	 * Biblioteca para as quais o usu�rio pode realizar as solicita��es.
	 */
	protected Collection<Biblioteca> bibliotecas = new ArrayList<Biblioteca>();
	
	/** Usado na transfer�ncia de solicita��es */
	private Collection<Biblioteca> bibliotecasServico = new ArrayList<Biblioteca>();
	
	/** Armazena a opera��o que est� sendo executada no momento. */
	private int operacao;
	
	/** Tipo de documento selecionado no filtro de busca */
	private Integer tipoDocumento;
	
	/** Limite inicial da data de cadastro selecionado no filtro de busca */
	private Date dataInicial;
	/** Limite final da data de cadastro selecionado no filtro de busca */
	private Date dataFinal;
	/** Indica se as solicita��es canceladas devem ser inclu�das no resultado da busca */
	private boolean buscarCanceladas;
	/** Indica se as solicita��es atendidas devem ser inclu�das no resultado da busca */
	private boolean buscarAtendidas;
	/** Indica se o filtro de tipo de documento est� ativado */
	private boolean buscarTipoDocumento;

	/** Biblioteca selecionada no filtro de busca */
	private Biblioteca biblioteca = new Biblioteca(-1);
	
	/** Usado na transfer�ncia de solicita��es */
	private Biblioteca bibliotecaDestino = new Biblioteca(-1);

	/** Arquivo contendo o trabalho digitalizado */
	private UploadedFile arquivoTrabalho;
	
	/** Opera��o de atender. */
	public static final int ATENDER = 2;
	/** Opera��o de cancelar. */
	public static final int CANCELAR = 3;
	
	/** Se o bibliotec�rio cancelar uma solicita��o tem que informar ao usu�rio que a solicitou */
	private String motivoCancelamento;
	
	public AbstractSolicitacaoServicoDocumentoMBean() throws DAOException {
		initObj();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////  Realiza��o da solicita��o //////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Inicia o caso de uso onde o usu�rio visualiza as suas solicita��es feitas. (tando de normaliza��o quanto de cataloga��o.)
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/portais/discente/menu_discente.jsp</li>
	 * 	<li>/portais/docente/menu_docente.jsp</li>
	 * 	<li>/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacao.jsp</li>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoNormalizacao.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException 
	 */
	public String verMinhasSolicitacoes() throws DAOException {
		SolicitacaoServicoDocumentoMBean solicitacaoServicoMbean = getMBean(NOME_MBEAN_GENERICO);
		
		return solicitacaoServicoMbean.verMinhasSolicitacoes();
	}
	
	
	
	/**
	 * Inicia o caso de uso onde o usu�rio faz uma solicita��o de servi�o.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String realizarNovaSolicitacao() throws ArqException{
		
		setConfirmButton("Cadastrar Solicita��o");
		
		initObj();
		
		BibliotecaDao dao = null;
		
		try {
			dao = getDAO(BibliotecaDao.class);
		
			// Verifica se o usu�rio possuia alguma conta ativida na biblioteca para poder solicitar os servi�os da se��o de Info. e Ref.
			// Caso n�o tenha ser� lan�ada uma NegocioException
			UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), null);
			
			if( getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE ) ){ // se acessou pelo portal discente � aluno
				obj.setDiscente( getDiscenteUsuario().getDiscente() );
				obj.setPessoa(getDiscenteUsuario().getPessoa());
				obj.setServidor( null );
			} else {                                     // qualquer outra forma de acesso � servidor
				obj.setServidor( getServidorUsuario() );
				obj.setPessoa(getServidorUsuario().getPessoa());
				obj.setDiscente( null );
			}
			
			obtemBibliotecaSolicitante();
	
			prepareMovimento(getMovimentoCadastrar());
			
			return telaNovaSolicitacaoServico();
		}catch(NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		} finally {
			if (dao != null)  dao.close();
		}
	}

	/**
	 * Cria a solicita��o de servi�o do usu�rio.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoNormalizacao.jsp</li>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoCatalogacao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String cadastrarSolicitacao() throws ArqException{
		
		GenericDAO dao = null;
		try {
			
			dao = getGenericDAO();
			
			obj.setBiblioteca(dao.refresh(new Biblioteca(obj.getBiblioteca().getId())));
			
			preencherDados();
			
			MovimentoSolicitacaoDocumento mov = instanciarMovimento();
			
			mov.setObjMovimentado(obj);
	
			if (!getConfirmButton().equals(TEXTO_ALTERAR)){
				validaFormatoArquivoTrabalho();
				mov.setArquivoTrabalho(arquivoTrabalho);
				mov.setCodMovimento(getMovimentoCadastrar());
				execute(mov);
				addMensagemInformation("Solicita��o cadastrada com sucesso!");
			} else {
				if(arquivoTrabalho != null){  // substitui o arquivo anterior
					validaFormatoArquivoTrabalho();
					mov.setArquivoTrabalho(arquivoTrabalho);
				}
 				mov.setCodMovimento(getMovimentoAlterar());
				execute(mov);
				addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Solicita��o");
			}

			getCurrentRequest().setAttribute("exibirOpcaoComprovante", true);
			getCurrentRequest().setAttribute("idSolicitacao", obj.getId());
			
			return verMinhasSolicitacoes();
		
		} catch (ArqException arqex){
			addMensagemErro(arqex.getMessage());
			return null;
		} catch (NegocioException ne){
			addMensagens(ne.getListaMensagens());
			return null;
		} finally {
			if (dao != null) dao.close();
		}
		
	}

	
	/**
	 * Exibe as informa��es completas da solicita��o para o usu�rio.
	 * 
	 * M�todo n�o utilizado em JSPs
	 * @throws ArqException 
	 */
	public String visualizarDadosSolicitacao() throws ArqException {
		initObj();          // inicializa o objeto salvo no banco com o id passado a requisi��o
		
		if (obj == null || obj.getId() == 0 || !obj.isAtiva()) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			
			return verMinhasSolicitacoes();
		}
		
		prepararVisualizar();
		
		return telaVisualizarSolicitacao();
	}

	/**
	 * M�todo utilizado para efetuar altera��es sobre o objeto de solicita��o antes da visualiza��o dos dados da mesma.
	 * @throws ArqException 
	 */
	protected abstract void prepararVisualizar() throws ArqException;
	
	/**
	 * Exibe as informa��es completa da solicita��o para o bibliotec�rio.
	 * 
	 * Possui algumas informa��es a mais como por quem foi validada, por quem foi atendida.
	 * 
	 * M�todo n�o utilizado em JSPs
	 * 
	 * @throws ArqException
	 */
	public String visualizarDadosSolicitacaoAtendimento() throws ArqException {
		
		operacao = -1;
		
		initObj();          // inicializa o objeto salvo no banco com o id passado a requisi��o
		
		if (obj == null){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			
			return verSolicitacoes();
		}
		
		return telaVisualizarDadosSolicitacaoAtendimento();
	}

	/**
	 * M�todo que preenche os dados particulares do objeto, se necess�rio
	 */
	protected abstract void preencherDados();

	/**
	 * M�todo que sobrescreve os valores atuais dos seus campos com as informa��es da base de dados 
	 * 
	 * @param dao
	 * @throws DAOException
	 */
	protected abstract void sincronizarDados(GenericDAO dao) throws DAOException;

	/**
	 * Retorna o comando que representa a opera��o cadastrar nova solicita��o
	 * 
	 * @return
	 */
	protected abstract Comando getMovimentoCadastrar();

	/**
	 * Retorna o comando que representa a opera��o alterar solicita��o
	 * 
	 * @return
	 */
	protected abstract Comando getMovimentoAlterar();


	/**
	 * Retorna o comando que representa a opera��o atender solicita��o
	 * 
	 * @return
	 */
	protected abstract Comando getMovimentoAtender();

	/**
	 * Retorna o comando que representa a opera��o cancelar solicita��o
	 * 
	 * @return
	 */
	protected abstract Comando getMovimentoCancelar();
	
	/////////////////  fim da parte de realizar um solicita��o  ////////////////////////////////
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////   Parte onde o usu�rio pode alterar e visualizar os dados das suas solicita��es ////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Altera os dados da solicita��o feita.
	 * O usu�rio pode alterar solicita��es que est�o com status "Solicitado".
	 * 
	 * M�todo n�o utilizado em JSPs
	 * 
	 * @throws ArqException 
	 */
	public String alterarSolicitacao() throws ArqException {
		
		setConfirmButton(TEXTO_ALTERAR);

		BibliotecaDao dao = null;
		initObj(); // inicializa o objeto salvo no banco com o id passado a requisi��o
		
		if ( obj == null || obj.getId() == 0 || ! obj.isAtiva() ){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return verMinhasSolicitacoes();
		}
		
		try {
			dao = getDAO(BibliotecaDao.class);
			
			prepareMovimento(getMovimentoAlterar());
			
			obj.setBiblioteca(dao.refresh(obj.getBiblioteca()));
			
			sincronizarDados(dao);

			setConfirmButton("Alterar");

			obtemBibliotecaSolicitante();
			
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		} finally {
			if (dao != null) dao.close();
		}

		prepareMovimento(getMovimentoAlterar());
		
		// tela que cria a solicita��o sendo que agora com o bot�o alterar habilitado
		return telaNovaSolicitacaoServico();
	}
	
	/**
	 * Remove a solicita��o selecionada.
	 * O usu�rio pode remover solicita��es que est�o com status "Solicitado".
	 * 
	 * M�todo n�o utilizado em JSPs
	 */
	public String removerSolicitacao() throws DAOException {
		
		initObj();   // inicializa o objeto salvo no banco com o id passado a requisi��o
		
		GenericDAO dao = null;
		
		if (obj == null || obj.getId() == 0 || ! obj.isAtiva())
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		else{
			try {
				dao = getGenericDAO();
				obj.setAtiva(false);
				dao.update(obj);
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Solicita��o");
			} finally {
				if (dao != null) dao.close();
			}
		
		}
		
		return verMinhasSolicitacoes();
	}
	
	/////////////////////  Fim da parte de altera��o da solicita��o pelo usu�rio /////////////////////////
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////    Parte do fluxo onde o bibliotec�rio atende as solicita��es de servi�o     //////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Atende uma solicita��o de servi�o.
	 * 
	 * M�todo n�o utilizado em JSPs
	 */
	public String atenderSolicitacao() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
		
		operacao = ATENDER;
		setConfirmButton("Atender");
		prepareMovimento(getMovimentoAtender());
		
		return verificarOperacoes();
	}
	
	
	/**
	 * Cancelar uma solicita��o de servi�o (retorna ao usu�rio).
	 * 
	 * M�todo n�o utilizado em JSPs
	 */
	public String cancelarSolicitacao() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		operacao = CANCELAR;
		setConfirmButton("Cancelar Solicita��o");
		prepareMovimento(getMovimentoCancelar());
		
		return verificarOperacoes();
	}

	/**
	 * Exibe a tela com as solicita��es a serem atendidas para o bibliotec�rio.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/portais/discente/menu_discente.jsp</li>
	 * 	<li>/portais/docente/menu_docente.jsp</li>
	 * 	<li>/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoNormalizacaoAtendimento.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException 
	 */
	public String verSolicitacoes() throws ArqException {
		SolicitacaoServicoDocumentoMBean solicitacaoServicoMbean = getMBean(NOME_MBEAN_GENERICO);
		
		return solicitacaoServicoMbean.buscarSolicitacoesSolicitadas();
	}
	
	
	
	/**
	 * Realiza a opera��o do bot�o confirmar da tela que visualiza os dados da solicita��o. A opera��o realizada
	 * vai depender da opera��o escolhida no passo anterior pelo usu�rio.<br/><br/>
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoNormalizacaoAtendimento.jsp</li>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacaoAtendimento.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		
		MovimentoCadastro mov = instanciarMovimento();
		
		mov.setObjMovimentado(obj);
		String msgConfirmacao = null;

		try {
			 if (isAtender()){
				
				confirmarAtender(mov);
				
				mov.setCodMovimento(getMovimentoAtender());
				execute(mov);
				
				enviarEmailsAtendimento();
				
				msgConfirmacao = "Atendimento realizado com sucesso. Um e-mail foi enviado ao usu�rio da " +
						"solicita��o informando o seu atendimento.";
				
			} else if (isCancelar()){
				
				if(StringUtils.isEmpty(motivoCancelamento)){
					addMensagemErro("Informe o motivo do cancelamento.");
					return null;
				}
				else if (motivoCancelamento.length() > 100) {
					addMensagemErro("O campo 'Motivo' n�o pode ultrapassar 100 caracteres.");
					return null;
				}
				
				mov.setCodMovimento(getMovimentoCancelar());
				execute(mov);

				enviarEmailsCancelamento();
				
				msgConfirmacao = "Cancelamento realizado com sucesso. Um email foi enviado ao usu�rio da " +
						"solicita��o informando o seu cancelamento.";
			}
		
			addMensagemInformation(msgConfirmacao);
			
			return verSolicitacoes();
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch (ArqException arqex) {
			addMensagemErro(arqex.getMessage());
			arqex.printStackTrace();
			return null;
		}
		
	}

	
	
	/**
	 * Envia os e-mails referentes � opera��o de atendimento de solicita��o.
	 * 
	 * @throws DAOException
	 */
	private void enviarEmailsAtendimento() throws DAOException {
		EnvioEmailBiblioteca envioEmail = new EnvioEmailBiblioteca();
		
		String assuntoEmail = null;
		String conteudo = null;
		String destinatario = null;
		
		assuntoEmail = "Aviso de Solicita��o de "+ obj.getTipoServico() +" Atendida";
		
		conteudo = "<p>A sua solicita��o de " + obj.getTipoServico() + " foi " +
				"atendida. Acesse o sistema e visualize-a pelo link da biblioteca." +
				ProcessadorSolicitacaoOrientacao.ASSINATURA_SETOR_INFORMACAO_E_REFERENCIA;
		
		destinatario = obj.getPessoa().getEmail();

		envioEmail.enviaEmailSimples(obj.getPessoa().getNome(), 
				destinatario, 
				assuntoEmail, 
				assuntoEmail, 
				EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, 
				conteudo);
	}

	/**
	 * Envia os e-mails referentes � opera��o de cancelamento de solicita��o.
	 * 
	 * @throws DAOException
	 */
	private void enviarEmailsCancelamento() throws DAOException {
		EnvioEmailBiblioteca envioEmail = new EnvioEmailBiblioteca();
		
		String assuntoEmail = null;
		String conteudo = null;
		String destinatario = null;
		
		assuntoEmail = "Aviso de Solicita��o de "+WordUtils.capitalize(obj.getTipoServico().toLowerCase())+" n�o atendida";
		
		conteudo = "<p>A sua solicita��o de " + obj.getTipoServico().toLowerCase() + " n�o p�de " +
				"ser atendida. <br><br> Motivo: " + motivoCancelamento + ". <br><br>" +
				ProcessadorSolicitacaoOrientacao.ASSINATURA_SETOR_INFORMACAO_E_REFERENCIA;
		
		destinatario = obj.getPessoa().getEmail();

		envioEmail.enviaEmailSimples(obj.getPessoa().getNome(), 
				destinatario, 
				assuntoEmail, 
				assuntoEmail, 
				EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, 
				conteudo);
	}
	
	/**
	 * M�todo utilizado para efetuar altera��es sobre o objeto de solicita��o durante a confirma��o de atendimento da mesma.
	 * 
	 * @param mov
	 * @throws ArqException
	 */
	protected abstract void confirmarAtender(MovimentoCadastro mov) throws NegocioException;

	/**
	 * Retorna a inst�ncia de um movimento espec�fico atrav�s de polimorfismo.
	 * 
	 * @return
	 */
	protected abstract MovimentoSolicitacaoDocumento instanciarMovimento();

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	////// Ultima parte onde o usu�rio pode ver o resultado do atendimento da solicita��o ///////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
		
	/**
	 *   Verifica se a opera��o (Validar, Atender ou Cancelar) escolhida pelo usu�rio pode
	 *   ser realizada.
	 */
	protected String verificarOperacoes() throws SegurancaException, DAOException {
		
		checkRole(
				SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
				SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO,
				SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO );
		
		initObj();
		
		/*if( isValidar() ){
			if( !obj.isSolicitado() ){
				addMensagemErro("Apenas solicita��es de " + obj.getTipoServico().toLowerCase() + " solicitadas podem ser validadas.");
				return null;
			}
		}else*/ if( isAtender() ){
			
			if( !obj.isSolicitado() && !obj.isReenviado() ){
				addMensagemErro("Apenas solicita��es de " + obj.getTipoServico().toLowerCase() + " solicitadas podem ser atendidas.");
				return null;
			}

			prepararAtender();
		} else if( isCancelar() ){
			if( obj.isAtendido() ){
				addMensagemErro("Esta solicita��o de " + obj.getTipoServico().toLowerCase() + " j� foi atendida. Portanto, n�o pode ser cancelada.");
				return null;
			}
			
			if( obj.isCancelado() ){
				addMensagemErro("Esta solicita��o de " + obj.getTipoServico().toLowerCase() + " j� foi cancelada. Portanto, n�o pode ser cancelada novamente.");
				return null;
			}
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////
		// A tela que visualiza as informa��es da solicita��o � comum em todos os casos          //
		// Apenas mudando os bot�es e alguma informa��o extra que depende da opera��o escolhida  //
		///////////////////////////////////////////////////////////////////////////////////////////
		return telaVisualizarDadosSolicitacaoAtendimento();

	}

	/**
	 * M�todo utilizado para efetuar altera��es sobre o objeto de solicita��o antes da confirma��o de atendimento da mesma.
	 */
	protected abstract void prepararAtender();

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *   M�todo que obt�m a biblioteca que o usu�rio pode solicitar o servi�o.
	 *   Se for aluno a setorial dele, se for servidor ou aluno sem setorial a biblioteca Central.
	 * @throws ArqException
	 *
	 */
	private void obtemBibliotecaSolicitante() throws ArqException {
		
		bibliotecas = new ArrayList<Biblioteca>();
		
		// Acessou pelo portal discente, ent�o vai fazer a solicita��o como discente
		if (getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE)){
			if (!isEmpty(obj.getDiscente().getUnidade())){
				bibliotecas = BibliotecaUtil.getBibliotecasDoDiscenteByServico(obj.getDiscente(), getTipoServico());
			}
		} else { // Solicita��o como servidor
			bibliotecas.add( BibliotecaUtil.getBibliotecaCentral() );
		}
		
		// Se o usu�rio fez uma solicita��o para a setorial do centro dele como aluno e depois
		// se loga como servidor, s� vai poder fazer novas solicita��es para central, mas para as
		// solicita��es j� feitas tem que aparecer a biblioteca onde ela estava
		
		if(obj.getId() > 0 && obj.getBiblioteca() != null && ! contains(bibliotecas, obj.getBiblioteca())){
			bibliotecas.add(obj.getBiblioteca());
		}
		
		// N�o deixa a biblioteca central se houver somente ela e ela n�o estiver oferecendo
		// os servi�o desejado
		if ( bibliotecas.size() == 1 ) {
			Iterator<Biblioteca> it = bibliotecas.iterator();
			if ( ! BibliotecaUtil.bibliotecaRealizaServico(getPropriedadeServico(), it.next().getId()))
				it.remove();
		}
		
		if (bibliotecas.size() == 0) {
			addMensagemErro("No momento n�o h� bibliotecas disponibilizando este servi�o.");
		}
	}

	/**
	 * Retorna a propriedade da classe ServicosBiblioteca que representa o tipo de servi�o solicitado
	 * 
	 * @return
	 */
	public abstract String getPropriedadeServico();
	
	/**
	 * Verifica se a lista de bibliotecas passa contem o objeto biblioteca pelo id.
	 * N�o dava para usar o <code>contains</code> de <code>Collections</code> porque o
	 * Hibernate traz objetos <code>proxy</code> e o <code>equals</code> e
	 * <code>hashcode</code> n�o funcionam
	 */
	private boolean contains(Collection<Biblioteca> bib, Biblioteca b) {
		
		for (Biblioteca bibl : bib) {
			if(bibl.getId() == b.getId())
				return true;
		}
		
		return false;
	}
	
	/**
	 *    Inicializa a solicita��o, se tiver algum idSolicitacao como par�metro da
	 * solicita��o, inicializa o <tt>obj</tt> com os dados da solicita��o salva no banco(
	 * altera��o e remo��o), sen�o inicializa uma nova solicita��o
	 * @throws DAOException 
	 *  
	 * 
	 */
	protected void initObj() throws DAOException {
		obj = instanciarServico();
		
		int id = getParameterInt("idSolicitacao", 0);
		
		if (id != 0){
			obj.setId(id);
			
			populateObj();
		} 
		else {
			obj.setBiblioteca(new Biblioteca(-1));
			
			inicializarDados();
		}
	}

	/**
	 * Inicializa a inst�ncia do objeto de solicita��o
	 */
	protected abstract void inicializarDados();

	/**
	 * Instancia o objeto de solicita��o de acordo com seu tipo atrav�s de reflex�o
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	private T instanciarServico() {
		try {
			return (T)((Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	//////////////////////////   telas de navega��o  //////////////////////////////////////

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de cria��o de solicita��o.
	 * 
	 * @return
	 */
	protected abstract String telaNovaSolicitacaoServico();

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de visualiza��o dos dados de uma solicita��o do usu�rio.
	 * 
	 * @return
	 */
	protected abstract String telaVisualizarSolicitacao();
	/**
	 * Redireciona o fluxo de navega��o para a p�gina de visualiza��o dos dados de uma solicita��o para o bibliotec�rio.
	 * 
	 * @return
	 */
	protected abstract String telaVisualizarDadosSolicitacaoAtendimento();
		
	/**
	 * Exibe a tela com o comprovante da solicita��o.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoNormalizacao.jsp</li>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/visualizarDadosSolicitacaoCatalogacao.jsp</li>
	 * </ul>
	 */
	public String telaComprovante() throws DAOException {
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			obj = dao.refresh(obj);
		
			return telaVisualizaComprovante();
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/**
	 * Valida o arquivo que o usu�rio informou.
	 *
	 * <p>Este m�todo � chamado pelas seguintes JSPs:</p>
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoCatalogacao.jsp</li></ul>
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/novaSolicitacaoNormalizacao.jsp</li></ul>
	 * 
	 * @throws ArqException 
	 */
	public void validaFormatoArquivoTrabalho() throws NegocioException {
		if( arquivoTrabalho != null ) {
			
			if( ! isNomeArquivoValidoParaSubmissaoArquivos(arquivoTrabalho.getName()) 
					|| ! isContentTypeAceitoParaSubmissaoArquivos(arquivoTrabalho.getContentType()) ){ 
				arquivoTrabalho = null;
				throw new NegocioException("O arquivo enviado precisa ser um documento de texto no formato DOC ou DOCX ");
			}
			
			if(arquivoTrabalho != null && arquivoTrabalho.getName().length() > 100){
				arquivoTrabalho = null;
				throw new NegocioException("O tamanho m�ximo permito para o nome de arquivo � de 100 caracteres.");
			}
			
		}
	}

	/**
	 * M�todo que verifica se o nome est� encontre os aceitos para submeter o arquivo
	 * @param contentType
	 * @return
	 */
	public static boolean isNomeArquivoValidoParaSubmissaoArquivos(String nomeArquivo){
		
		if(StringUtils.isEmpty(nomeArquivo))
			return false;
		
		if( nomeArquivo.endsWith(".doc") || nomeArquivo.endsWith(".docx") )
			return true;
		else
			return false;
	}
	
	/**
	 * M�todo que verifica se o content type est� encontre os aceitos para submeter o arquivo
	 * @param contentType
	 * @return
	 */
	public static boolean isContentTypeAceitoParaSubmissaoArquivos(String contentType){
		if(StringUtils.isEmpty(contentType))
			return false;
		
		if(contentType.equals("application/msword") // .doc
				|| contentType.equals("application/octet-stream") //.docx 
				|| contentType.equals("application/x-zip-compressed")  // por incr�vel que parece o IE ler o arquivo .docx como um zip 
				|| contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") // em algumas m�quinas clientes o docx vem assim
				|| ( 	// Se continer essas 3 palavras tamb�m deixa passar..
						    contentType.contains("document")  
						&&  contentType.contains("office") 
						&&  contentType.contains("word") )
				)
				
			return true;
		else
			return false;
	}
	
	
	/**
	 * M�todo que retorna o n�mero de autentica��o gerado para ser exibido no comprovante.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:</p>
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacaoCatalogacao.jsp</li></ul>
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacaoNormalizacao.jsp</li></ul>
	 */
	public String getNumeroAutenticacao() {
		return BibliotecaUtil.geraNumeroAutenticacaoComprovantes(obj.getId(), obj.getDataCadastro());
	}

	/**
	 * Redireciona o fluxo de navega��o para a p�gina de exibi��o do comprovante de solicita��o.
	 * 
	 * @return
	 */
	protected abstract String telaVisualizaComprovante();
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	//////    sets e gets   ////////
	
	public Collection<Biblioteca> getBibliotecas() {
		return bibliotecas;
	}

	public void setBibliotecas(Collection<Biblioteca> bibliotecas) {
		this.bibliotecas = bibliotecas;
	}

	public Collection<SelectItem> getBibliotecasCombo(){
		return toSelectItems(bibliotecas, "id", "descricao");
	}

	public Collection<SelectItem> getBibliotecasServicoCombo(){
		return toSelectItems(bibliotecasServico, "id", "descricao");
	}
	
	public Integer getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}
	
	public boolean isCancelar(){
		return operacao == CANCELAR;
	}
	
	public boolean isAtender(){
		return operacao == ATENDER;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Collection<Biblioteca> getBibliotecasServico() {
		return bibliotecasServico;
	}

	public void setBibliotecasServico(Collection<Biblioteca> bibliotecasServico) {
		this.bibliotecasServico = bibliotecasServico;
	}

	public Biblioteca getBibliotecaDestino() {
		return bibliotecaDestino;
	}

	public void setBibliotecaDestino(Biblioteca bibliotecaDestino) {
		this.bibliotecaDestino = bibliotecaDestino;
	}

	public boolean isBuscarCanceladas() {
		return buscarCanceladas;
	}

	public void setBuscarCanceladas(boolean buscarCanceladas) {
		this.buscarCanceladas = buscarCanceladas;
	}

	public boolean isBuscarAtendidas() {
		return buscarAtendidas;
	}

	public void setBuscarAtendidas(boolean buscarAtendidas) {
		this.buscarAtendidas = buscarAtendidas;
	}

	public boolean isBuscarTipoDocumento() {
		return buscarTipoDocumento;
	}

	public void setBuscarTipoDocumento(boolean buscarTipoDocumento) {
		this.buscarTipoDocumento = buscarTipoDocumento;
	}

	public UploadedFile getArquivoTrabalho() {
		return arquivoTrabalho;
	}

	public void setArquivoTrabalho(UploadedFile arquivoTrabalho) {
		this.arquivoTrabalho = arquivoTrabalho;
	}
	
}