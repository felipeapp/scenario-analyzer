/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 12/11/2010
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.LevantamentoInfraDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ArquivoLevantamentoInfra;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.LevantamentoInfraEstrutura;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.LevantamentoInfraEstrutura.Situacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.informacao_referencia.negocio.MovimentoLevantamentoInfra;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * MBean que controla desde as solicita��es at� a realiza��o de levantamentos de
 * infra-estrutura.
 *
 * @author Br�ulio
 * 
 * @see LevantamentoInfraEstrutura
 * @see ArquivoLevantamentoInfra
 */
@Component("levantamentoInfraMBean")
@Scope("request")
public class LevantamentoInfraMBean extends
		SigaaAbstractController<LevantamentoInfraEstrutura> {

	///////// As JSPs desse caso de uso: /////////
	
	/** Caminho base para as funcionalidades.. */
	private static final String PATH = "/biblioteca/informacao_referencia/levantamento_infra/";
	
	/** JSP com lista de levantamentos do usu�rio.  */
	private static final String LISTAR_PARA_USUARIO =                PATH + "listaDoUsuario.jsp";
	/** JSP para solicita��o de levantamento. */
	private static final String SOLICITAR =                          PATH + "formDoUsuario.jsp";
	/** JSP com lista de levantamentos para os bibliotec�rios. */
	private static final String LISTAR_PARA_BIBLIOTECARIO =          PATH + "listaDoBibliotecario.jsp";
	/** JSP para atendimento de um levantamento. */
	private static final String ATENDER =                            PATH + "formDoBibliotecario.jsp";
	/** JSP para visualiza��o de um levantamento j� atendido ou cancelado. */
	private static final String VISUALIZAR =                         PATH + "visualizar.jsp";
	
	/** JSP para cancelamento de um levantamento pelo bibliotec�rio. */
	private static final String CANCELAR_SOLICITACAO_BIBLIOTECARIO = PATH + "motivoCancelamentoBibliotecario.jsp";
	
	//////////////////////////////////////////////
	
	/** Os pap�is que d�o ao usu�rio permiss�o de solicitar levantamentos de infra-estrutura. */
	private static final int[] PAPEIS_USUARIO = SigaaPapeis.BIBLIOTECA_SOLICITAR_LEVANTAMENTO_INFRA;
	
	/** Os pap�is que d�o ao bibliotec�rio acesso a realizar e gerenciar levantamentos de infra-estrutura. */
	private static final int[] PAPEIS_BIBLIOTECARIO = new int[]{
			SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
			SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
			SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO};
	
	/** Indica a n�o filtragem por biblioteca. */
	private static final int TODAS_BIBLIOTECAS = -1;

	/** Indica a n�o filtragem por situa��o. */
	private static final int TODAS_SITUACOES = -1;
	
	/////// Vari�veis usadas pelo MBean //////
	
	/** A lista de levantamentos de infra-estrutura. */
	private List<LevantamentoInfraEstrutura> lista;
	
	/** O filtro de bibliotecas. */
	private int filtroBiblioteca = TODAS_BIBLIOTECAS;
	
	/** O filtro de situa��o do levantamento. */
	private int filtroSituacao = Situacao.SOLICITADO.v;
	
	/** Os arquivos que ser�o inseridos no levantamento.  */
	private List<UploadedFile> arquivosTemp = new ArrayList<UploadedFile>();
	
	/** Cache das bibliotecas onde o bibliotec�rio pode visualizar/atender solicita��es. */
	private Collection<Biblioteca> cacheBibliotecasDoBibliotecario = null;

	/** Cache das bibliotecas para as quais o usu�rio pode fazer solicita��es. */
	private Collection<Biblioteca> cacheBibliotecasDoUsuario;
	
	/** Indica se o usu�rio � bibliotec�rio. */
	private boolean bibliotecario;
	
	//////////////////////////////////////////
	
	/** Inicializa o MBean. */
	public LevantamentoInfraMBean() {
		obj = new LevantamentoInfraEstrutura();
		obj.setBiblioteca( new Biblioteca() );
	}
	
	/////// A��es do MBbean /////
	
	/**
	 * Mostra a lista com as solicita��es de levantamento do usu�rio.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/menus/modulo_biblioteca_servidor.jsp</li></ul>
	 * <ul><li>/sigaa.war/portais/docente/menu_docente.jsp</li></ul>
	 */
	public String listarParaUsuario() throws SegurancaException, DAOException {
		
		checkRole(PAPEIS_USUARIO);
		
		LevantamentoInfraDao dao = getDAO(LevantamentoInfraDao.class);
		
		lista = dao.findLevantamentosDoUsuario( getUsuarioLogado().getPessoa().getId() );
		
		bibliotecario = false;
		
		return forward(LISTAR_PARA_USUARIO);
	}
	
	/**
	 * Lista os levantamentos de infra-estrutura para o bibliotec�rio que vai atend�-las.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/menus/informacao_referencia.jsp</li></ul>
	 */
	public String listarParaBibliotecario() throws SegurancaException, DAOException {
		
		checkRole(PAPEIS_BIBLIOTECARIO);
		
		LevantamentoInfraDao dao = getDAO(LevantamentoInfraDao.class);
		
		if ( filtroBiblioteca == TODAS_BIBLIOTECAS ) {
			List<Integer> bibs = new ArrayList<Integer>();
			for ( Biblioteca b : this.getBibliotecasDoBibliotecario() )
				bibs.add(b.getId() );
			lista = dao.findLevantamentos(bibs , filtroSituacao );
		}
		else if ( filtroBiblioteca == TODAS_BIBLIOTECAS )
			lista = dao.findLevantamentos( null, filtroSituacao );
		else
			lista = dao.findLevantamentos( Arrays.asList( new Integer[]{filtroBiblioteca} ), filtroSituacao );
		
		bibliotecario = true;
		
		return forward(LISTAR_PARA_BIBLIOTECARIO);
	}
	
	/**
	 * Inicia o processo de solicita��o de levantamento de infra-estrutura.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoUsuario.jsp</li></ul>
	 */
	public String iniciarSolicitacao() throws ArqException {
		checkRole(PAPEIS_USUARIO);

		if ( getBibliotecasDoUsuario().isEmpty() ) {
			addMensagemErro("N�o h� nenhuma biblioteca para a qual voc� possa solicitar levantamento de infra-estrutura.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.CADASTRAR_SOLICITACAO_LEVANTAMENTO_INFRA);
		
		obj = new LevantamentoInfraEstrutura();
		obj.setBiblioteca( new Biblioteca() );
		obj.getBiblioteca().setId( getBibliotecasDoUsuario().iterator().next().getId() );
		obj.setTextoSolicitacao("\n\n\n\n---\nAtenciosamente\n"+getUsuarioLogado().getNome()); // Cria uma "assinatura" pre definida para a solicita��o.
		return forward(SOLICITAR);
	}
	
	/**
	 * Cadastra uma nova solicita��o de levantamento de infra-estrutura.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoUsuario.jsp</li></ul>
	 */
	public String solicitar() throws ArqException {
		
		checkRole(PAPEIS_USUARIO);
		
		if ( StringUtils.isEmpty( obj.getTextoSolicitacao() ) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Detalhes");
		if ( obj.getBiblioteca() == null || obj.getBiblioteca().getId() <= 0 )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Biblioteca");
		
		if ( hasErrors() )
			return null;
		
		MovimentoLevantamentoInfra mov = new MovimentoLevantamentoInfra(
				SigaaListaComando.CADASTRAR_SOLICITACAO_LEVANTAMENTO_INFRA, obj);
		
		obj.setSituacao(Situacao.SOLICITADO.v);
		obj.setSolicitante( new Pessoa() );
		obj.getSolicitante().setId( getUsuarioLogado().getPessoa().getId() );
		
		try {
			obj = execute(mov);
		} catch ( NegocioException e ) {
			addMensagens( e.getListaMensagens() );
			return null;
		}
		
		addMensagemInformation("A sua solicita��o de levantamento de infra-estrutura n�mero <i>"+obj.getNumeroLevantamentoInfra()+"</i> foi cadastrada " +
				"e a biblioteca respons�vel foi avisada.");
		
		return cancelar();
	}

	/**
	 * Mostra para o usu�rio os dados de uma solicita��o.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoUsuario.jsp</li>
	 * <li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/motivoCancelamentoUsuario.jsp</li></ul>
	 */
	public String visualizarParaUsuario() throws ArqException {
		
		checkRole( PAPEIS_USUARIO );
		
		prepareMovimento(SigaaListaComando.CANCELAR_SOLICITACAO_LEVANTAMENTO_INFRA);
		
		int idLevantamentoInfra = getParameterInt("idLevantamentoInfra", -1);
		
		if ( idLevantamentoInfra == -1 )
			if ( this.obj != null && this.obj.getId() > 0 )
				idLevantamentoInfra = this.obj.getId();
		
		if ( idLevantamentoInfra == -1 )
			throw new ArqException( new IllegalArgumentException("Faltou id do levantamento de infra-estrutura") );
		
		this.obj = getGenericDAO().findByPrimaryKey( idLevantamentoInfra, LevantamentoInfraEstrutura.class );
		
		if ( obj.getSolicitante().getId() != getUsuarioLogado().getPessoa().getId() ) {
			addMensagemErro("Voc� n�o tem permiss�o para visualizar levantamentos solicitados por outra pessoa.");
			return null;
		}
		
		// preenche nomes dos arquivos
		for ( ArquivoLevantamentoInfra arq : obj.getArquivos() )
			arq.setNome( EnvioArquivoHelper.recuperaNomeArquivo(arq.getIdArquivo()) );
		
		return forward(VISUALIZAR);
	}
	
	/**
	 * Mostra uma solicita��o de levantamento para o bibliotec�rio atend�-la.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoBibliotecario.jsp</li>
	 * <li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public String visualizarParaBibliotecario() throws ArqException {
		
		checkRole(PAPEIS_BIBLIOTECARIO);
		
		prepareMovimento(SigaaListaComando.ATENDER_SOLICITACAO_LEVANTAMENTO_INFRA);
		prepareMovimento(SigaaListaComando.SALVAR_SOLICITACAO_LEVANTAMENTO_INFRA);
		
		int idLevantamentoInfra = getParameterInt("idLevantamentoInfra", -1);
		
		if ( idLevantamentoInfra == -1 )
			if ( this.obj != null && this.obj.getId() > 0 )
				idLevantamentoInfra = this.obj.getId();
		
		if ( idLevantamentoInfra == -1 )
			throw new ArqException( new IllegalArgumentException("Faltou id do levantamento de infra-estrutura") );
		
		this.obj = getGenericDAO().findByPrimaryKey( idLevantamentoInfra, LevantamentoInfraEstrutura.class );
		
		// preenche nomes dos arquivos
		for ( ArquivoLevantamentoInfra arq : obj.getArquivos() )
			arq.setNome( EnvioArquivoHelper.recuperaNomeArquivo(arq.getIdArquivo()) );
		
		if ( obj.getSituacao() == Situacao.SOLICITADO.v )
			return forward(ATENDER);
		else
			return forward(VISUALIZAR);
	}
	
	/**
	 * Somente salva as altera��es do levantamento. N�o envia email nem disponibiliza os dados para o usu�rio.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public String salvar()
			throws ArqException, IOException {
		
		checkRole(PAPEIS_BIBLIOTECARIO);
		
		// seta os arquivos que ser�o adicionados
		
		for ( UploadedFile upFile : arquivosTemp ) {
			ArquivoLevantamentoInfra arq = new ArquivoLevantamentoInfra();
			
			arq.setId(0);
			arq.setIdArquivo(0);
			arq.setConteudo(upFile.getBytes());
			arq.setNome(upFile.getName());
			arq.setTipo(upFile.getContentType());
			
			obj.getArquivos().add(arq);
		}
		
		// executa o movimento
		
		MovimentoLevantamentoInfra mov = new MovimentoLevantamentoInfra(
				SigaaListaComando.SALVAR_SOLICITACAO_LEVANTAMENTO_INFRA, obj);
		
		try {
			obj = execute(mov);
		} catch ( NegocioException e ) {
			addMensagens(e.getListaMensagens());
			
			// remove os arquivos, para o MBean n�o pensar que eles est�o no BD
			Iterator<ArquivoLevantamentoInfra> it = obj.getArquivos().iterator();
			while ( it.hasNext() ) {
				ArquivoLevantamentoInfra arq = it.next();
				
				if ( arq.getConteudo() != null )
					it.remove();
				
			}
			
			return null;
		}
		
		addMensagemInformation("O levantamento de infra-estrutura numero <i>"+obj.getNumeroLevantamentoInfra()+" </i> foi salvo com sucesso.");
		
		obj = null;
		arquivosTemp.clear();
		
		return listarParaBibliotecario();
	}
	
	/**
	 * Salva o levantamento e o disponibiliza ao usu�rio.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public String atender() throws IOException, ArqException {
		
		checkRole(PAPEIS_BIBLIOTECARIO);
		
		// valida��o
		
		if ( StringUtils.isEmpty(obj.getTextoBibliotecario()) ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resposta ao usu�rio");
			return null;
		}
		
		// seta os arquivos que ser�o adicionados
		
		for ( UploadedFile upFile : arquivosTemp ) {
			ArquivoLevantamentoInfra arq = new ArquivoLevantamentoInfra();
			
			arq.setId(0);
			arq.setIdArquivo(0);
			arq.setConteudo(upFile.getBytes());
			arq.setNome(upFile.getName());
			arq.setTipo(upFile.getContentType());
			
			obj.getArquivos().add(arq);
		}
		
		// executa o movimento
		
		MovimentoLevantamentoInfra mov = new MovimentoLevantamentoInfra(
				SigaaListaComando.ATENDER_SOLICITACAO_LEVANTAMENTO_INFRA, obj);
		
		try {
			obj = execute(mov);
		} catch ( NegocioException e ) {
			addMensagens(e.getListaMensagens());
			
			// remove os arquivos, para o MBean n�o pensar que eles est�o no BD
			Iterator<ArquivoLevantamentoInfra> it = obj.getArquivos().iterator();
			while ( it.hasNext() ) {
				ArquivoLevantamentoInfra arq = it.next();
				
				if ( arq.getConteudo() != null )
					it.remove();
				
			}
			
			return null;
		}
		
		addMensagemInformation("O levantamento de infra-estrutura n�mero <i>"+obj.getNumeroLevantamentoInfra()+"</i> foi atendido com sucesso. O usu�rio foi informado por " +
				"e-mail e as informa��es agora est�o dispon�veis para ele.");
		
		obj = null;
		arquivosTemp.clear();
		
		return listarParaBibliotecario();
	}
	
	
	/**
	 * Inicia o processo de cancelamento de um levantamento.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 * @throws ArqException 
	 */
	public String iniciarCancelarSolicitacaoParaBibliotecario() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CANCELAR_SOLICITACAO_LEVANTAMENTO_INFRA);
		
		return forward(CANCELAR_SOLICITACAO_BIBLIOTECARIO);
	}
	
	/**
	 * Cancela a solicita��o de levantamento.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/motivoCancelamentoUsuario.jsp</li></ul>
	 */
	public String cancelarSolicitacaoUsuario() throws ArqException {
		
		obj.setMotivoCancelamento("CANCELADO PELO USU�RIO");
		
		cancelarSolicitacao();
		
		if ( hasErrors() )
			return null;
		
		addMensagemInformation("A sua solicita��o de levantamento de infra-estrutura n�mero <i>"+obj.getNumeroLevantamentoInfra()+"</i> foi cancelada com sucesso.");
		
		return listarParaUsuario();
	}
	
	/**
	 * Cancela a solicita��o de levantamento.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/motivoCancelamentoBibliotecario.jsp</li></ul>
	 */
	public String cancelarSolicitacaoBibliotecario() throws ArqException {
		
		cancelarSolicitacao();
		
		if ( hasErrors() )
			return null;
		
		addMensagemInformation("A solicita��o de levantamento de infra-estrutura n�mero <i>"+obj.getNumeroLevantamentoInfra()+"</i> foi cancelada com sucesso.");
		
		return listarParaBibliotecario();
	}
	
	/**
	 * Cancela uma solicita��o.
	 */
	private void cancelarSolicitacao() throws ArqException {
		
		if ( StringUtils.isEmpty( obj.getMotivoCancelamento() ) ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,
					"Motivo");
			return;
		}
		
		MovimentoLevantamentoInfra mov = new MovimentoLevantamentoInfra(
				SigaaListaComando.CANCELAR_SOLICITACAO_LEVANTAMENTO_INFRA, obj);
		
		try {
			obj = execute(mov);
		} catch ( NegocioException e ) {
			addMensagens(e.getListaMensagens());
		}
	
	}
	
	/**
	 * Filtra a lista do bibliotec�rio de levantamentos de infra-estrutura.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoBibliotecario.jsp</li></ul>
	 */
	public String filtrar() throws SegurancaException, DAOException {
		return listarParaBibliotecario();
	}
	
	/**
	 * Adiciona um arquivo ao levantamento.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public String adicionarArquivo( ValueChangeEvent e ) {
		
		UploadedFile arquivo = (UploadedFile) e.getNewValue();
		
		if ( arquivo == null )
			return null;
		
		// verifica se j� h� um arquivo com o mesmo nome
		
		for ( ArquivoLevantamentoInfra f : obj.getArquivos() ) {
			if ( f.getNome().equals(arquivo.getName()) ) {
				addMensagemErro("Um arquivo de mesmo nome j� foi adicionado. Por favor retire o arquivo de mesmo " +
						"nome ou renomeio o novo arquivo.");
				return null;
			}
		}
		for ( UploadedFile f : arquivosTemp ) {
			if ( f.getName().equals(arquivo.getName()) ) {
				addMensagemErro("Um arquivo de mesmo nome j� foi adicionado. Por favor retire o arquivo de mesmo " +
						"nome ou renomeio o novo arquivo.");
				return null;
			}
		}
		
		// Valida tipo e tamanho do arquivo.
		// Observe que o tipo MIME do arquivo � enviado pelo browser do usu�rio e n�o � seguro.
		
		String extensao = arquivo.getName().replaceAll(".+\\.", "");
		
		if (
				! ArquivoLevantamentoInfra.ARQUIVOS_PERMITIDOS.contains( arquivo.getContentType().toLowerCase() ) &&
				! ArquivoLevantamentoInfra.EXTENSOES_PERMITIDAS.contains( extensao.toLowerCase() ) ) {
			addMensagemErro("Tipo inv�lido de arquivo.");
			return null;
		}
		
		if ( arquivo.getSize() >= ArquivoLevantamentoInfra.TAMANHO_MAXIMO_ARQUIVO * (1 << 20) ) {
			addMensagemErro("O tamanho m�ximo do arquivo � de " +
					ArquivoLevantamentoInfra.TAMANHO_MAXIMO_ARQUIVO + "MB");
			return null;
		}
		
		addMensagemInformation("Arquivo \""+arquivo.getName()+"\" adicionado com sucesso.");
		
		arquivosTemp.add( arquivo );
		
		return null;
	}
	
	
	/**
	 * Marca um arquivo que j� estava na base de arquivos para remo��o.
	 * A remo��o s� � feita quando o usu�rio salva o levantamento.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public String removerArquivoDaBase() {
		
		int id = getParameterInt("id", -1);
		
		ArquivoLevantamentoInfra arq = null;
		for ( ArquivoLevantamentoInfra f : obj.getArquivos() )
			if ( f.getId() == id ) {
				arq = f;
				break;
			}
		
		if ( arq != null ){
			obj.getArquivos().remove(arq);
			addMensagemInformation("Arquivo \""+arq.getNome()+"\" removido com sucesso");
		}
			
		return null;
	}
	
	/**
	 * Remove um arquivo que ainda est� em mem�ria.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public String removerArquivoEmMemoria() {
		
		String arquivo = getParameter("arquivo");
		
		UploadedFile arq = null;
		for ( UploadedFile f : arquivosTemp ) {
			if ( f.getName().equals(arquivo) )
				arq = f;
		}
		
		if ( arq != null ){
			arquivosTemp.remove(arq);
			addMensagemInformation("Arquivo \""+arq.getName()+"\" removido com sucesso");
		}
		return null;
	}
	
	/**
	 * Baixa um arquivo do levantamento.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoUsuario.jsp</li></ul>
	 */
	public void baixarArquivo() throws IOException, ArqException {
		
		int id = getParameterInt("id", -1);
		
		if ( id == -1 )
			throw new ArqException(new IllegalArgumentException("Informe id do arquivo"));
		
		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletResponse resp = (HttpServletResponse) fc.getExternalContext().getResponse();
		
		EnvioArquivoHelper.recuperaArquivo(resp, id, true);
		
		fc.responseComplete();
		
	}
	
	////// M�todos auxiliares //////
	
	/**
	 * Retorna as bibliotecas onde o usu�rio pode solicitar levantamento de infra-estrutura.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoUsuario.jsp</li></ul>
	 */
	public List<SelectItem> getBibliotecasDoUsuarioCombo() throws ArqException {
		return toSelectItems(this.getBibliotecasDoUsuario(), "id", "descricao");
	}
	
	/**
	 * Retorna as bibliotecas onde o bibliotec�rio tem permiss�o para realizar levantamentos.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoBibliotecario.jsp</li></ul>
	 */
	public List<SelectItem> getBibliotecasDoBibliotecarioCombo() throws DAOException {
		return toSelectItems(this.getBibliotecasDoBibliotecario(), "id", "descricao");
	}
	
	/**
	 * Retorna as poss�veis situa��es de um levantamento de infra-estrutura.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoBibliotecario.jsp</li></ul>
	 */
	public List<SelectItem> getSituacoesCombo() {
		return toSelectItems( Arrays.asList(LevantamentoInfraEstrutura.Situacao.values()), "v", "descricao" );
	}
	
	/**
	 * Retorna os arquivos que acabarem de ser adicionados e ainda est�o somente
	 * em mem�ria.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public List<String> getArquivosEmMemoria() {
		List<String> resultado = new ArrayList<String>();
		for ( UploadedFile arq : this.arquivosTemp )
			resultado.add( arq.getName() );
		return resultado;
	}
	
	/** Retorna as bibliotecas para as quais o usu�rio pode solicitar um levantamento de infra-estrutura */
	private Collection<Biblioteca> getBibliotecasDoUsuario() throws ArqException {
		if ( cacheBibliotecasDoUsuario != null )
			return cacheBibliotecasDoUsuario;
		
		cacheBibliotecasDoUsuario = BibliotecaUtil.getBibliotecasDoDiscenteByServico(getUsuarioLogado().getDiscente(), TipoServicoInformacaoReferencia.LEVANTAMENTO_INFRA_ESTRUTURA);
		
		// adiciona a central, se essa fizer o servi�o e se ela n�o tiver sido adicionada no m�todo acima
		boolean temCentral = false;
		for ( Biblioteca b : cacheBibliotecasDoUsuario )
			if ( b.isBibliotecaCentral() ) {
				temCentral = true;
				break;
			}
		if ( ! temCentral ) {
			Biblioteca central = BibliotecaUtil.getBibliotecaCentral();
			if ( BibliotecaUtil.bibliotecaRealizaServico("realizaLevantamentoInfraEstrutura", central.getId()))
				cacheBibliotecasDoUsuario.add( central );
		}
		
		return cacheBibliotecasDoUsuario;
	}
	
	/** Retorna as bibliotecas onde o bibliotec�rio pode atender solicita��es de levantamento de infra-estrutura. */
	private Collection<Biblioteca> getBibliotecasDoBibliotecario() throws DAOException {
		
		if ( cacheBibliotecasDoBibliotecario != null )
			return cacheBibliotecasDoBibliotecario;
		
		Collection<Biblioteca> b = new HashSet<Biblioteca>();
		
		if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			
			if(isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO) ||
					isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF)){
			
				List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
					getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
				b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades) );
				
				idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
						getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF);
				if ( ! idUnidades.isEmpty() )
					b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades) );
			}
		
		} else {
			b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas() );
		}
		
		cacheBibliotecasDoBibliotecario = b;
		
		return b;
	}
	
	/////////// Telas de Navega��o  ///////////
	
	/**
	 * Redireciona o fluxo de navega��o para a tela de listagem.
	 * 
	 * Usado em sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoUsuario.jsp
	 */
	public String telaListaDoUsuario(){
		return forward(LISTAR_PARA_USUARIO);
	}	
	
	////// GETs e SETs //////
	
	public int getTodasBibliotecas() {
		return TODAS_BIBLIOTECAS;
	}
	
	public int getTodasSituacoes() {
		return TODAS_SITUACOES;
	}
	
	public int[] getPapeisUsuario() {
		return PAPEIS_USUARIO;
	}
	
	public int[] getPapeisBibliotecario() {
		return PAPEIS_BIBLIOTECARIO;
	}
	
	public List<LevantamentoInfraEstrutura> getLista() {
		return lista;
	}

	public int getFiltroBiblioteca() {
		return filtroBiblioteca;
	}

	public void setFiltroBiblioteca(int filtroBiblioteca) {
		this.filtroBiblioteca = filtroBiblioteca;
	}

	public int getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(int filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	public boolean isBibliotecario() {
		return bibliotecario;
	}

}
