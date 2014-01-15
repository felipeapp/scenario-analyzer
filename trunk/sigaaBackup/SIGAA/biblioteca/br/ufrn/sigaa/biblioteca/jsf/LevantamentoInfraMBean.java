/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean que controla desde as solicitações até a realização de levantamentos de
 * infra-estrutura.
 *
 * @author Bráulio
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
	
	/** JSP com lista de levantamentos do usuário.  */
	private static final String LISTAR_PARA_USUARIO =                PATH + "listaDoUsuario.jsp";
	/** JSP para solicitação de levantamento. */
	private static final String SOLICITAR =                          PATH + "formDoUsuario.jsp";
	/** JSP com lista de levantamentos para os bibliotecários. */
	private static final String LISTAR_PARA_BIBLIOTECARIO =          PATH + "listaDoBibliotecario.jsp";
	/** JSP para atendimento de um levantamento. */
	private static final String ATENDER =                            PATH + "formDoBibliotecario.jsp";
	/** JSP para visualização de um levantamento já atendido ou cancelado. */
	private static final String VISUALIZAR =                         PATH + "visualizar.jsp";
	
	/** JSP para cancelamento de um levantamento pelo bibliotecário. */
	private static final String CANCELAR_SOLICITACAO_BIBLIOTECARIO = PATH + "motivoCancelamentoBibliotecario.jsp";
	
	//////////////////////////////////////////////
	
	/** Os papéis que dão ao usuário permissão de solicitar levantamentos de infra-estrutura. */
	private static final int[] PAPEIS_USUARIO = SigaaPapeis.BIBLIOTECA_SOLICITAR_LEVANTAMENTO_INFRA;
	
	/** Os papéis que dão ao bibliotecário acesso a realizar e gerenciar levantamentos de infra-estrutura. */
	private static final int[] PAPEIS_BIBLIOTECARIO = new int[]{
			SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
			SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
			SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO};
	
	/** Indica a não filtragem por biblioteca. */
	private static final int TODAS_BIBLIOTECAS = -1;

	/** Indica a não filtragem por situação. */
	private static final int TODAS_SITUACOES = -1;
	
	/////// Variáveis usadas pelo MBean //////
	
	/** A lista de levantamentos de infra-estrutura. */
	private List<LevantamentoInfraEstrutura> lista;
	
	/** O filtro de bibliotecas. */
	private int filtroBiblioteca = TODAS_BIBLIOTECAS;
	
	/** O filtro de situação do levantamento. */
	private int filtroSituacao = Situacao.SOLICITADO.v;
	
	/** Os arquivos que serão inseridos no levantamento.  */
	private List<UploadedFile> arquivosTemp = new ArrayList<UploadedFile>();
	
	/** Cache das bibliotecas onde o bibliotecário pode visualizar/atender solicitações. */
	private Collection<Biblioteca> cacheBibliotecasDoBibliotecario = null;

	/** Cache das bibliotecas para as quais o usuário pode fazer solicitações. */
	private Collection<Biblioteca> cacheBibliotecasDoUsuario;
	
	/** Indica se o usuário é bibliotecário. */
	private boolean bibliotecario;
	
	//////////////////////////////////////////
	
	/** Inicializa o MBean. */
	public LevantamentoInfraMBean() {
		obj = new LevantamentoInfraEstrutura();
		obj.setBiblioteca( new Biblioteca() );
	}
	
	/////// Ações do MBbean /////
	
	/**
	 * Mostra a lista com as solicitações de levantamento do usuário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
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
	 * Lista os levantamentos de infra-estrutura para o bibliotecário que vai atendê-las.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
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
	 * Inicia o processo de solicitação de levantamento de infra-estrutura.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoUsuario.jsp</li></ul>
	 */
	public String iniciarSolicitacao() throws ArqException {
		checkRole(PAPEIS_USUARIO);

		if ( getBibliotecasDoUsuario().isEmpty() ) {
			addMensagemErro("Não há nenhuma biblioteca para a qual você possa solicitar levantamento de infra-estrutura.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.CADASTRAR_SOLICITACAO_LEVANTAMENTO_INFRA);
		
		obj = new LevantamentoInfraEstrutura();
		obj.setBiblioteca( new Biblioteca() );
		obj.getBiblioteca().setId( getBibliotecasDoUsuario().iterator().next().getId() );
		obj.setTextoSolicitacao("\n\n\n\n---\nAtenciosamente\n"+getUsuarioLogado().getNome()); // Cria uma "assinatura" pre definida para a solicitação.
		return forward(SOLICITAR);
	}
	
	/**
	 * Cadastra uma nova solicitação de levantamento de infra-estrutura.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
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
		
		addMensagemInformation("A sua solicitação de levantamento de infra-estrutura número <i>"+obj.getNumeroLevantamentoInfra()+"</i> foi cadastrada " +
				"e a biblioteca responsável foi avisada.");
		
		return cancelar();
	}

	/**
	 * Mostra para o usuário os dados de uma solicitação.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
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
			addMensagemErro("Você não tem permissão para visualizar levantamentos solicitados por outra pessoa.");
			return null;
		}
		
		// preenche nomes dos arquivos
		for ( ArquivoLevantamentoInfra arq : obj.getArquivos() )
			arq.setNome( EnvioArquivoHelper.recuperaNomeArquivo(arq.getIdArquivo()) );
		
		return forward(VISUALIZAR);
	}
	
	/**
	 * Mostra uma solicitação de levantamento para o bibliotecário atendê-la.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
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
	 * Somente salva as alterações do levantamento. Não envia email nem disponibiliza os dados para o usuário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public String salvar()
			throws ArqException, IOException {
		
		checkRole(PAPEIS_BIBLIOTECARIO);
		
		// seta os arquivos que serão adicionados
		
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
			
			// remove os arquivos, para o MBean não pensar que eles estão no BD
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
	 * Salva o levantamento e o disponibiliza ao usuário.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public String atender() throws IOException, ArqException {
		
		checkRole(PAPEIS_BIBLIOTECARIO);
		
		// validação
		
		if ( StringUtils.isEmpty(obj.getTextoBibliotecario()) ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resposta ao usuário");
			return null;
		}
		
		// seta os arquivos que serão adicionados
		
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
			
			// remove os arquivos, para o MBean não pensar que eles estão no BD
			Iterator<ArquivoLevantamentoInfra> it = obj.getArquivos().iterator();
			while ( it.hasNext() ) {
				ArquivoLevantamentoInfra arq = it.next();
				
				if ( arq.getConteudo() != null )
					it.remove();
				
			}
			
			return null;
		}
		
		addMensagemInformation("O levantamento de infra-estrutura número <i>"+obj.getNumeroLevantamentoInfra()+"</i> foi atendido com sucesso. O usuário foi informado por " +
				"e-mail e as informações agora estão disponíveis para ele.");
		
		obj = null;
		arquivosTemp.clear();
		
		return listarParaBibliotecario();
	}
	
	
	/**
	 * Inicia o processo de cancelamento de um levantamento.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 * @throws ArqException 
	 */
	public String iniciarCancelarSolicitacaoParaBibliotecario() throws ArqException {
		
		prepareMovimento(SigaaListaComando.CANCELAR_SOLICITACAO_LEVANTAMENTO_INFRA);
		
		return forward(CANCELAR_SOLICITACAO_BIBLIOTECARIO);
	}
	
	/**
	 * Cancela a solicitação de levantamento.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/motivoCancelamentoUsuario.jsp</li></ul>
	 */
	public String cancelarSolicitacaoUsuario() throws ArqException {
		
		obj.setMotivoCancelamento("CANCELADO PELO USUÁRIO");
		
		cancelarSolicitacao();
		
		if ( hasErrors() )
			return null;
		
		addMensagemInformation("A sua solicitação de levantamento de infra-estrutura número <i>"+obj.getNumeroLevantamentoInfra()+"</i> foi cancelada com sucesso.");
		
		return listarParaUsuario();
	}
	
	/**
	 * Cancela a solicitação de levantamento.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/motivoCancelamentoBibliotecario.jsp</li></ul>
	 */
	public String cancelarSolicitacaoBibliotecario() throws ArqException {
		
		cancelarSolicitacao();
		
		if ( hasErrors() )
			return null;
		
		addMensagemInformation("A solicitação de levantamento de infra-estrutura número <i>"+obj.getNumeroLevantamentoInfra()+"</i> foi cancelada com sucesso.");
		
		return listarParaBibliotecario();
	}
	
	/**
	 * Cancela uma solicitação.
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
	 * Filtra a lista do bibliotecário de levantamentos de infra-estrutura.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoBibliotecario.jsp</li></ul>
	 */
	public String filtrar() throws SegurancaException, DAOException {
		return listarParaBibliotecario();
	}
	
	/**
	 * Adiciona um arquivo ao levantamento.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public String adicionarArquivo( ValueChangeEvent e ) {
		
		UploadedFile arquivo = (UploadedFile) e.getNewValue();
		
		if ( arquivo == null )
			return null;
		
		// verifica se já há um arquivo com o mesmo nome
		
		for ( ArquivoLevantamentoInfra f : obj.getArquivos() ) {
			if ( f.getNome().equals(arquivo.getName()) ) {
				addMensagemErro("Um arquivo de mesmo nome já foi adicionado. Por favor retire o arquivo de mesmo " +
						"nome ou renomeio o novo arquivo.");
				return null;
			}
		}
		for ( UploadedFile f : arquivosTemp ) {
			if ( f.getName().equals(arquivo.getName()) ) {
				addMensagemErro("Um arquivo de mesmo nome já foi adicionado. Por favor retire o arquivo de mesmo " +
						"nome ou renomeio o novo arquivo.");
				return null;
			}
		}
		
		// Valida tipo e tamanho do arquivo.
		// Observe que o tipo MIME do arquivo é enviado pelo browser do usuário e não é seguro.
		
		String extensao = arquivo.getName().replaceAll(".+\\.", "");
		
		if (
				! ArquivoLevantamentoInfra.ARQUIVOS_PERMITIDOS.contains( arquivo.getContentType().toLowerCase() ) &&
				! ArquivoLevantamentoInfra.EXTENSOES_PERMITIDAS.contains( extensao.toLowerCase() ) ) {
			addMensagemErro("Tipo inválido de arquivo.");
			return null;
		}
		
		if ( arquivo.getSize() >= ArquivoLevantamentoInfra.TAMANHO_MAXIMO_ARQUIVO * (1 << 20) ) {
			addMensagemErro("O tamanho máximo do arquivo é de " +
					ArquivoLevantamentoInfra.TAMANHO_MAXIMO_ARQUIVO + "MB");
			return null;
		}
		
		addMensagemInformation("Arquivo \""+arquivo.getName()+"\" adicionado com sucesso.");
		
		arquivosTemp.add( arquivo );
		
		return null;
	}
	
	
	/**
	 * Marca um arquivo que já estava na base de arquivos para remoção.
	 * A remoção só é feita quando o usuário salva o levantamento.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
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
	 * Remove um arquivo que ainda está em memória.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
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
	 * <p>Este método é chamado pelas seguintes JSPs:
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
	
	////// Métodos auxiliares //////
	
	/**
	 * Retorna as bibliotecas onde o usuário pode solicitar levantamento de infra-estrutura.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoUsuario.jsp</li></ul>
	 */
	public List<SelectItem> getBibliotecasDoUsuarioCombo() throws ArqException {
		return toSelectItems(this.getBibliotecasDoUsuario(), "id", "descricao");
	}
	
	/**
	 * Retorna as bibliotecas onde o bibliotecário tem permissão para realizar levantamentos.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoBibliotecario.jsp</li></ul>
	 */
	public List<SelectItem> getBibliotecasDoBibliotecarioCombo() throws DAOException {
		return toSelectItems(this.getBibliotecasDoBibliotecario(), "id", "descricao");
	}
	
	/**
	 * Retorna as possíveis situações de um levantamento de infra-estrutura.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/listaDoBibliotecario.jsp</li></ul>
	 */
	public List<SelectItem> getSituacoesCombo() {
		return toSelectItems( Arrays.asList(LevantamentoInfraEstrutura.Situacao.values()), "v", "descricao" );
	}
	
	/**
	 * Retorna os arquivos que acabarem de ser adicionados e ainda estão somente
	 * em memória.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/informacao_referencia/levantamento_infra/formDoBibliotecario.jsp</li></ul>
	 */
	public List<String> getArquivosEmMemoria() {
		List<String> resultado = new ArrayList<String>();
		for ( UploadedFile arq : this.arquivosTemp )
			resultado.add( arq.getName() );
		return resultado;
	}
	
	/** Retorna as bibliotecas para as quais o usuário pode solicitar um levantamento de infra-estrutura */
	private Collection<Biblioteca> getBibliotecasDoUsuario() throws ArqException {
		if ( cacheBibliotecasDoUsuario != null )
			return cacheBibliotecasDoUsuario;
		
		cacheBibliotecasDoUsuario = BibliotecaUtil.getBibliotecasDoDiscenteByServico(getUsuarioLogado().getDiscente(), TipoServicoInformacaoReferencia.LEVANTAMENTO_INFRA_ESTRUTURA);
		
		// adiciona a central, se essa fizer o serviço e se ela não tiver sido adicionada no método acima
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
	
	/** Retorna as bibliotecas onde o bibliotecário pode atender solicitações de levantamento de infra-estrutura. */
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
	
	/////////// Telas de Navegação  ///////////
	
	/**
	 * Redireciona o fluxo de navegação para a tela de listagem.
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
