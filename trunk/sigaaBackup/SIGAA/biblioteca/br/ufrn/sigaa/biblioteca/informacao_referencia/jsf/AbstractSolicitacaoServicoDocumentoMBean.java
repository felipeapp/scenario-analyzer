/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean responsável pelos atendimento e solicitação de serviços realizada por discentes ou servidores à biblioteca.
 * 
 * @author Felipe Rivas
 */
public abstract class AbstractSolicitacaoServicoDocumentoMBean<T extends SolicitacaoServicoDocumento> extends SigaaAbstractController<T> 
																					implements ISolicitacaoServicoDocumentoMBean<T> {
	
	
	/** usado no botão no qual o usuário faz a solicitação ou a altera */
	public static final String TEXTO_ALTERAR = "Alterar";
	
	/**
	 * Nome do managed bean que trata as operações que podem ser generalizadas para todos os tipos de solicitações
	 */
	protected static final String NOME_MBEAN_GENERICO = "solicitacaoServicoDocumentoMBean";
	
	/**
	 * Biblioteca para as quais o usuário pode realizar as solicitações.
	 */
	protected Collection<Biblioteca> bibliotecas = new ArrayList<Biblioteca>();
	
	/** Usado na transferência de solicitações */
	private Collection<Biblioteca> bibliotecasServico = new ArrayList<Biblioteca>();
	
	/** Armazena a operação que está sendo executada no momento. */
	private int operacao;
	
	/** Tipo de documento selecionado no filtro de busca */
	private Integer tipoDocumento;
	
	/** Limite inicial da data de cadastro selecionado no filtro de busca */
	private Date dataInicial;
	/** Limite final da data de cadastro selecionado no filtro de busca */
	private Date dataFinal;
	/** Indica se as solicitações canceladas devem ser incluídas no resultado da busca */
	private boolean buscarCanceladas;
	/** Indica se as solicitações atendidas devem ser incluídas no resultado da busca */
	private boolean buscarAtendidas;
	/** Indica se o filtro de tipo de documento está ativado */
	private boolean buscarTipoDocumento;

	/** Biblioteca selecionada no filtro de busca */
	private Biblioteca biblioteca = new Biblioteca(-1);
	
	/** Usado na transferência de solicitações */
	private Biblioteca bibliotecaDestino = new Biblioteca(-1);

	/** Arquivo contendo o trabalho digitalizado */
	private UploadedFile arquivoTrabalho;
	
	/** Operação de atender. */
	public static final int ATENDER = 2;
	/** Operação de cancelar. */
	public static final int CANCELAR = 3;
	
	/** Se o bibliotecário cancelar uma solicitação tem que informar ao usuário que a solicitou */
	private String motivoCancelamento;
	
	public AbstractSolicitacaoServicoDocumentoMBean() throws DAOException {
		initObj();
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////  Realização da solicitação //////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Inicia o caso de uso onde o usuário visualiza as suas solicitações feitas. (tando de normalização quanto de catalogação.)
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
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
	 * Inicia o caso de uso onde o usuário faz uma solicitação de serviço.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul>
	 * 	<li>/biblioteca/informacao_referencia/normalizacao_catalogacao/minhasSolicitacoes.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String realizarNovaSolicitacao() throws ArqException{
		
		setConfirmButton("Cadastrar Solicitação");
		
		initObj();
		
		BibliotecaDao dao = null;
		
		try {
			dao = getDAO(BibliotecaDao.class);
		
			// Verifica se o usuário possuia alguma conta ativida na biblioteca para poder solicitar os serviços da seção de Info. e Ref.
			// Caso não tenha será lançada uma NegocioException
			UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), null);
			
			if( getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE ) ){ // se acessou pelo portal discente é aluno
				obj.setDiscente( getDiscenteUsuario().getDiscente() );
				obj.setPessoa(getDiscenteUsuario().getPessoa());
				obj.setServidor( null );
			} else {                                     // qualquer outra forma de acesso é servidor
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
	 * Cria a solicitação de serviço do usuário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
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
				addMensagemInformation("Solicitação cadastrada com sucesso!");
			} else {
				if(arquivoTrabalho != null){  // substitui o arquivo anterior
					validaFormatoArquivoTrabalho();
					mov.setArquivoTrabalho(arquivoTrabalho);
				}
 				mov.setCodMovimento(getMovimentoAlterar());
				execute(mov);
				addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Solicitação");
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
	 * Exibe as informações completas da solicitação para o usuário.
	 * 
	 * Método não utilizado em JSPs
	 * @throws ArqException 
	 */
	public String visualizarDadosSolicitacao() throws ArqException {
		initObj();          // inicializa o objeto salvo no banco com o id passado a requisição
		
		if (obj == null || obj.getId() == 0 || !obj.isAtiva()) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			
			return verMinhasSolicitacoes();
		}
		
		prepararVisualizar();
		
		return telaVisualizarSolicitacao();
	}

	/**
	 * Método utilizado para efetuar alterações sobre o objeto de solicitação antes da visualização dos dados da mesma.
	 * @throws ArqException 
	 */
	protected abstract void prepararVisualizar() throws ArqException;
	
	/**
	 * Exibe as informações completa da solicitação para o bibliotecário.
	 * 
	 * Possui algumas informações a mais como por quem foi validada, por quem foi atendida.
	 * 
	 * Método não utilizado em JSPs
	 * 
	 * @throws ArqException
	 */
	public String visualizarDadosSolicitacaoAtendimento() throws ArqException {
		
		operacao = -1;
		
		initObj();          // inicializa o objeto salvo no banco com o id passado a requisição
		
		if (obj == null){
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			
			return verSolicitacoes();
		}
		
		return telaVisualizarDadosSolicitacaoAtendimento();
	}

	/**
	 * Método que preenche os dados particulares do objeto, se necessário
	 */
	protected abstract void preencherDados();

	/**
	 * Método que sobrescreve os valores atuais dos seus campos com as informações da base de dados 
	 * 
	 * @param dao
	 * @throws DAOException
	 */
	protected abstract void sincronizarDados(GenericDAO dao) throws DAOException;

	/**
	 * Retorna o comando que representa a operação cadastrar nova solicitação
	 * 
	 * @return
	 */
	protected abstract Comando getMovimentoCadastrar();

	/**
	 * Retorna o comando que representa a operação alterar solicitação
	 * 
	 * @return
	 */
	protected abstract Comando getMovimentoAlterar();


	/**
	 * Retorna o comando que representa a operação atender solicitação
	 * 
	 * @return
	 */
	protected abstract Comando getMovimentoAtender();

	/**
	 * Retorna o comando que representa a operação cancelar solicitação
	 * 
	 * @return
	 */
	protected abstract Comando getMovimentoCancelar();
	
	/////////////////  fim da parte de realizar um solicitação  ////////////////////////////////
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////   Parte onde o usuário pode alterar e visualizar os dados das suas solicitações ////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Altera os dados da solicitação feita.
	 * O usuário pode alterar solicitações que estão com status "Solicitado".
	 * 
	 * Método não utilizado em JSPs
	 * 
	 * @throws ArqException 
	 */
	public String alterarSolicitacao() throws ArqException {
		
		setConfirmButton(TEXTO_ALTERAR);

		BibliotecaDao dao = null;
		initObj(); // inicializa o objeto salvo no banco com o id passado a requisição
		
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
		
		// tela que cria a solicitação sendo que agora com o botão alterar habilitado
		return telaNovaSolicitacaoServico();
	}
	
	/**
	 * Remove a solicitação selecionada.
	 * O usuário pode remover solicitações que estão com status "Solicitado".
	 * 
	 * Método não utilizado em JSPs
	 */
	public String removerSolicitacao() throws DAOException {
		
		initObj();   // inicializa o objeto salvo no banco com o id passado a requisição
		
		GenericDAO dao = null;
		
		if (obj == null || obj.getId() == 0 || ! obj.isAtiva())
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		else{
			try {
				dao = getGenericDAO();
				obj.setAtiva(false);
				dao.update(obj);
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Solicitação");
			} finally {
				if (dao != null) dao.close();
			}
		
		}
		
		return verMinhasSolicitacoes();
	}
	
	/////////////////////  Fim da parte de alteração da solicitação pelo usuário /////////////////////////
	
	
	
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////    Parte do fluxo onde o bibliotecário atende as solicitações de serviço     //////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Atende uma solicitação de serviço.
	 * 
	 * Método não utilizado em JSPs
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
	 * Cancelar uma solicitação de serviço (retorna ao usuário).
	 * 
	 * Método não utilizado em JSPs
	 */
	public String cancelarSolicitacao() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		operacao = CANCELAR;
		setConfirmButton("Cancelar Solicitação");
		prepareMovimento(getMovimentoCancelar());
		
		return verificarOperacoes();
	}

	/**
	 * Exibe a tela com as solicitações a serem atendidas para o bibliotecário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
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
	 * Realiza a operação do botão confirmar da tela que visualiza os dados da solicitação. A operação realizada
	 * vai depender da operação escolhida no passo anterior pelo usuário.<br/><br/>
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
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
				
				msgConfirmacao = "Atendimento realizado com sucesso. Um e-mail foi enviado ao usuário da " +
						"solicitação informando o seu atendimento.";
				
			} else if (isCancelar()){
				
				if(StringUtils.isEmpty(motivoCancelamento)){
					addMensagemErro("Informe o motivo do cancelamento.");
					return null;
				}
				else if (motivoCancelamento.length() > 100) {
					addMensagemErro("O campo 'Motivo' não pode ultrapassar 100 caracteres.");
					return null;
				}
				
				mov.setCodMovimento(getMovimentoCancelar());
				execute(mov);

				enviarEmailsCancelamento();
				
				msgConfirmacao = "Cancelamento realizado com sucesso. Um email foi enviado ao usuário da " +
						"solicitação informando o seu cancelamento.";
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
	 * Envia os e-mails referentes à operação de atendimento de solicitação.
	 * 
	 * @throws DAOException
	 */
	private void enviarEmailsAtendimento() throws DAOException {
		EnvioEmailBiblioteca envioEmail = new EnvioEmailBiblioteca();
		
		String assuntoEmail = null;
		String conteudo = null;
		String destinatario = null;
		
		assuntoEmail = "Aviso de Solicitação de "+ obj.getTipoServico() +" Atendida";
		
		conteudo = "<p>A sua solicitação de " + obj.getTipoServico() + " foi " +
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
	 * Envia os e-mails referentes à operação de cancelamento de solicitação.
	 * 
	 * @throws DAOException
	 */
	private void enviarEmailsCancelamento() throws DAOException {
		EnvioEmailBiblioteca envioEmail = new EnvioEmailBiblioteca();
		
		String assuntoEmail = null;
		String conteudo = null;
		String destinatario = null;
		
		assuntoEmail = "Aviso de Solicitação de "+WordUtils.capitalize(obj.getTipoServico().toLowerCase())+" não atendida";
		
		conteudo = "<p>A sua solicitação de " + obj.getTipoServico().toLowerCase() + " não pôde " +
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
	 * Método utilizado para efetuar alterações sobre o objeto de solicitação durante a confirmação de atendimento da mesma.
	 * 
	 * @param mov
	 * @throws ArqException
	 */
	protected abstract void confirmarAtender(MovimentoCadastro mov) throws NegocioException;

	/**
	 * Retorna a instância de um movimento específico através de polimorfismo.
	 * 
	 * @return
	 */
	protected abstract MovimentoSolicitacaoDocumento instanciarMovimento();

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	////// Ultima parte onde o usuário pode ver o resultado do atendimento da solicitação ///////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////
		
	/**
	 *   Verifica se a operação (Validar, Atender ou Cancelar) escolhida pelo usuário pode
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
				addMensagemErro("Apenas solicitações de " + obj.getTipoServico().toLowerCase() + " solicitadas podem ser validadas.");
				return null;
			}
		}else*/ if( isAtender() ){
			
			if( !obj.isSolicitado() && !obj.isReenviado() ){
				addMensagemErro("Apenas solicitações de " + obj.getTipoServico().toLowerCase() + " solicitadas podem ser atendidas.");
				return null;
			}

			prepararAtender();
		} else if( isCancelar() ){
			if( obj.isAtendido() ){
				addMensagemErro("Esta solicitação de " + obj.getTipoServico().toLowerCase() + " já foi atendida. Portanto, não pode ser cancelada.");
				return null;
			}
			
			if( obj.isCancelado() ){
				addMensagemErro("Esta solicitação de " + obj.getTipoServico().toLowerCase() + " já foi cancelada. Portanto, não pode ser cancelada novamente.");
				return null;
			}
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////
		// A tela que visualiza as informações da solicitação é comum em todos os casos          //
		// Apenas mudando os botões e alguma informação extra que depende da operação escolhida  //
		///////////////////////////////////////////////////////////////////////////////////////////
		return telaVisualizarDadosSolicitacaoAtendimento();

	}

	/**
	 * Método utilizado para efetuar alterações sobre o objeto de solicitação antes da confirmação de atendimento da mesma.
	 */
	protected abstract void prepararAtender();

	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 *   Método que obtém a biblioteca que o usuário pode solicitar o serviço.
	 *   Se for aluno a setorial dele, se for servidor ou aluno sem setorial a biblioteca Central.
	 * @throws ArqException
	 *
	 */
	private void obtemBibliotecaSolicitante() throws ArqException {
		
		bibliotecas = new ArrayList<Biblioteca>();
		
		// Acessou pelo portal discente, então vai fazer a solicitação como discente
		if (getSubSistema().equals( SigaaSubsistemas.PORTAL_DISCENTE)){
			if (!isEmpty(obj.getDiscente().getUnidade())){
				bibliotecas = BibliotecaUtil.getBibliotecasDoDiscenteByServico(obj.getDiscente(), getTipoServico());
			}
		} else { // Solicitação como servidor
			bibliotecas.add( BibliotecaUtil.getBibliotecaCentral() );
		}
		
		// Se o usuário fez uma solicitação para a setorial do centro dele como aluno e depois
		// se loga como servidor, só vai poder fazer novas solicitações para central, mas para as
		// solicitações já feitas tem que aparecer a biblioteca onde ela estava
		
		if(obj.getId() > 0 && obj.getBiblioteca() != null && ! contains(bibliotecas, obj.getBiblioteca())){
			bibliotecas.add(obj.getBiblioteca());
		}
		
		// Não deixa a biblioteca central se houver somente ela e ela não estiver oferecendo
		// os serviço desejado
		if ( bibliotecas.size() == 1 ) {
			Iterator<Biblioteca> it = bibliotecas.iterator();
			if ( ! BibliotecaUtil.bibliotecaRealizaServico(getPropriedadeServico(), it.next().getId()))
				it.remove();
		}
		
		if (bibliotecas.size() == 0) {
			addMensagemErro("No momento não há bibliotecas disponibilizando este serviço.");
		}
	}

	/**
	 * Retorna a propriedade da classe ServicosBiblioteca que representa o tipo de serviço solicitado
	 * 
	 * @return
	 */
	public abstract String getPropriedadeServico();
	
	/**
	 * Verifica se a lista de bibliotecas passa contem o objeto biblioteca pelo id.
	 * Não dava para usar o <code>contains</code> de <code>Collections</code> porque o
	 * Hibernate traz objetos <code>proxy</code> e o <code>equals</code> e
	 * <code>hashcode</code> não funcionam
	 */
	private boolean contains(Collection<Biblioteca> bib, Biblioteca b) {
		
		for (Biblioteca bibl : bib) {
			if(bibl.getId() == b.getId())
				return true;
		}
		
		return false;
	}
	
	/**
	 *    Inicializa a solicitação, se tiver algum idSolicitacao como parâmetro da
	 * solicitação, inicializa o <tt>obj</tt> com os dados da solicitação salva no banco(
	 * alteração e remoção), senão inicializa uma nova solicitação
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
	 * Inicializa a instância do objeto de solicitação
	 */
	protected abstract void inicializarDados();

	/**
	 * Instancia o objeto de solicitação de acordo com seu tipo através de reflexão
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
	
	
	//////////////////////////   telas de navegação  //////////////////////////////////////

	/**
	 * Redireciona o fluxo de navegação para a página de criação de solicitação.
	 * 
	 * @return
	 */
	protected abstract String telaNovaSolicitacaoServico();

	/**
	 * Redireciona o fluxo de navegação para a página de visualização dos dados de uma solicitação do usuário.
	 * 
	 * @return
	 */
	protected abstract String telaVisualizarSolicitacao();
	/**
	 * Redireciona o fluxo de navegação para a página de visualização dos dados de uma solicitação para o bibliotecário.
	 * 
	 * @return
	 */
	protected abstract String telaVisualizarDadosSolicitacaoAtendimento();
		
	/**
	 * Exibe a tela com o comprovante da solicitação.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
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
	 * Valida o arquivo que o usuário informou.
	 *
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
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
				throw new NegocioException("O tamanho máximo permito para o nome de arquivo é de 100 caracteres.");
			}
			
		}
	}

	/**
	 * Método que verifica se o nome está encontre os aceitos para submeter o arquivo
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
	 * Método que verifica se o content type está encontre os aceitos para submeter o arquivo
	 * @param contentType
	 * @return
	 */
	public static boolean isContentTypeAceitoParaSubmissaoArquivos(String contentType){
		if(StringUtils.isEmpty(contentType))
			return false;
		
		if(contentType.equals("application/msword") // .doc
				|| contentType.equals("application/octet-stream") //.docx 
				|| contentType.equals("application/x-zip-compressed")  // por incrível que parece o IE ler o arquivo .docx como um zip 
				|| contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") // em algumas máquinas clientes o docx vem assim
				|| ( 	// Se continer essas 3 palavras também deixa passar..
						    contentType.contains("document")  
						&&  contentType.contains("office") 
						&&  contentType.contains("word") )
				)
				
			return true;
		else
			return false;
	}
	
	
	/**
	 * Método que retorna o número de autenticação gerado para ser exibido no comprovante.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:</p>
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacaoCatalogacao.jsp</li></ul>
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/normalizacao_catalogacao/comprovanteSolicitacaoNormalizacao.jsp</li></ul>
	 */
	public String getNumeroAutenticacao() {
		return BibliotecaUtil.geraNumeroAutenticacaoComprovantes(obj.getId(), obj.getDataCadastro());
	}

	/**
	 * Redireciona o fluxo de navegação para a página de exibição do comprovante de solicitação.
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