/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 08/10/2008
 * 
 */
package br.ufrn.sigaa.biblioteca.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.LevantamentoBibliograficoInfraDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.LevantamentoBibliograficoInfra;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.Linguas;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.PeriodoLevantBibliografico;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean respons�vel pelo cadastro das Solicita��es do Levantamento Bibliogr�fico e de Infra-Estrutura.
 * 
 * @author Agostinho
 * @author Fred Castro
 */
@Component("levantamentoBibliograficoInfraMBean")
@Scope("request")
public class LevantamentoBibliograficoInfraMBean extends SigaaAbstractController<LevantamentoBibliograficoInfra> {
	
	/**
	 * TODO
	 */
	private static final String PATH = "/biblioteca/informacao_referencia/levantamento_biblio_infra/";
	/**
	 * TODO
	 */
	public static final String PAGINA_LISTA_SOLICITACOES_DO_USUARIO = PATH + "lista.jsp";
	/**
	 * TODO
	 */
	public static final String PAGINA_LISTA_SOLICITACOES            = PATH + "buscar_solicitacoes_indiv.jsp";
	/**
	 * TODO
	 */
	public static final String PAGINA_FORM_SOLICITACAO              = PATH + "form.jsp";
	/**
	 * TODO
	 */
	public static final String PAGINA_SOLICITACAO_FINALIZADA        = PATH + "form_solicitacoes_finalizadas.jsp";
	/**
	 * TODO
	 */
	public static final String PAGINA_TRANSFERENCIA_SOLICITACAO     = PATH + "formTransferenciaSolicitacao.jsp";
	
	/**
	 * TODO
	 */
	private List<String> linguasSelecionadas;
	/**
	 * TODO
	 */
	private List<Linguas> linguas = new ArrayList<Linguas>();
	
	/**
	 * TODO
	 */
	private String outraLinguaDescricao = "";
	/**
	 * TODO
	 */
	private String outroPeriodoDescricao = "";
	
	/**
	 * TODO
	 */
	private Boolean finalizarEnviarEmail = new Boolean(Boolean.FALSE);
	
	/**
	 * TODO
	 */
	private UploadedFile arquivo;
	
	/**
	 * TODO
	 */
	private final SortedSet<LevantamentoBibliograficoInfra> solicitacoes;

	/**
	 * TODO
	 */
	private boolean visualizarSolicitacaoIndividual;
	/**
	 * TODO
	 */
	private boolean visualizarSolicitacaoInfra;
	
	/** Bibliotecas para as quais o usu�rio pode fazer solicita��o. */
	private final List <Biblioteca> bibliotecasDoUsuario = new LinkedList<Biblioteca>();
	
	/** Biblioteca usadas na busca */
	private final List <Biblioteca> bibliotecasDeBusca = new LinkedList<Biblioteca>();
	
	/** Bibliotecas para as quais o funcion�rio pode transferir uma solicita��o. */
	private final List <Biblioteca> bibliotecasDeTransferencia = new LinkedList<Biblioteca>();
	
	/**
	 * TODO
	 */
	private String fontesPesquisadas;
	/**
	 * TODO
	 */
	private String observacoes;

	/**
	 * TODO
	 */
	private Biblioteca biblioteca = new Biblioteca();
	/**
	 * TODO
	 */
	private int situacao = -1;
	/**
	 * TODO
	 */
	private int infra;

	/**
	 * TODO
	 */
	private boolean alterar;
	
	/**
	 * Construtor padr�o.
	 */
	public LevantamentoBibliograficoInfraMBean() throws DAOException {
		solicitacoes = new TreeSet<LevantamentoBibliograficoInfra>(
				new Comparator<LevantamentoBibliograficoInfra>() {
					@Override
					public int compare(LevantamentoBibliograficoInfra o1,
							LevantamentoBibliograficoInfra o2) {
						return - o1.getDataSolicitacao().compareTo( o2.getDataSolicitacao() );
					}
				});
		clear();
	}
	
	/**
	 * Limpa o MBbean.
	 */
	private void clear() throws HibernateException, DAOException {
		obj = new LevantamentoBibliograficoInfra();
		linguas = getDAO(LevantamentoBibliograficoInfraDao.class).findAllLinguas();
		linguasSelecionadas = new ArrayList<String>();
		outraLinguaDescricao = "";
		biblioteca = new Biblioteca();
		solicitacoes.clear();
		situacao = -1;
	}

	/**
	 * Exibe o formul�rio para a solicita��o.
	 * usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/lista.jsp
	 */
	public String solicitarLevantamentoBibliograficoIndividual() throws HibernateException, ArqException {

		if ( getBibliotecasDoUsuario().isEmpty() ) {
			addMensagemErro("No momento n�o h� nenhuma biblioteca que preste o servi�o de Levantamento Bibliogr�fico.");
			return listarSolicitacoesLevantIndividualPorUsuario();
		}
		
		clear();
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(PAGINA_FORM_SOLICITACAO);
	}

	/**
	 * Exibe a listagem com as solicita��es do usu�rio logado.
	 * Usado em sigaa.war/portais/discente/menu_discente.jsp
	 * Usado em sigaa.war/portais/docente/menu_docente.jsp
	 */
	public String listarSolicitacoesLevantIndividualPorUsuario() throws DAOException {
		
		LevantamentoBibliograficoInfraDao dao = null;
		
		try {
			dao = getDAO(LevantamentoBibliograficoInfraDao.class);
			
			solicitacoes.addAll( dao.findAllSolicitacoesLevantamentoByIDPessoa(getUsuarioLogado().
					getPessoa().getId()) );
			return forward(PAGINA_LISTA_SOLICITACOES_DO_USUARIO);
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Exibe a listagem com todas as solicita��es de acordo com os crit�rio de busca.
	 * Usado em sigaa.war/biblioteca/menus/informacao_referencia.jsp
	 */
	public String listarSolicitacoesLevantFuncBiblioteca() throws DAOException, SegurancaException {
		checkRole (SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		if ( ! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL) )
			if ( biblioteca.getId() == 0 )
				biblioteca = getBibliotecasDeBusca().get(0);
		
		solicitacoes.clear();
		solicitacoes.addAll( getDAO(LevantamentoBibliograficoInfraDao.class).findAllSolicitacoesLevantamento(
				biblioteca, situacao, infra) );
		
		return forward(PAGINA_LISTA_SOLICITACOES);
	}
	
	/**
	 * M�todo que obt�m a biblioteca para o bibliotec�rio atender �s requisi��es.<br/><br/>
	 * Se for administrador geral, retorna todas, sen�o retorna as biblioteca onde o bibliotec�rio tem permiss�o
	 * de cataloga��o e informa��o e refer�ncia.<br/><br/>
	 */
	private Set<Biblioteca> obterBibliotecasDeAtendimento() throws DAOException {
		
		// evita bibliotecas repetidas
		Set<Biblioteca> b = new TreeSet<Biblioteca>(
				new Comparator<Biblioteca>() {
					@Override
					public int compare(Biblioteca c, Biblioteca d) { return c.getId() - d.getId(); }
				});
		
		if(! isUserInRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
			
			if(isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO)){
			
				List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
							getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
				
				b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades) );
			}
			
			if(isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO)){
			
				List<Integer> idUnidades = BibliotecaUtil.encontraUnidadesPermissaoDoUsuario(
					getUsuarioLogado(), SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
				b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivasPorUnidade(idUnidades) );
			}
		
		} else {
			b.addAll( getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas() );
		}
		
		return b;
	}
	
	/**
	 * Valida uma solicita��o.
	 * Usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/buscar_solicitacoes_indiv.jsp
	 */
	public String validarSolicitacao () throws ArqException{
		checkRole (SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);

		populateObj(true);
		
		if ( obj.getSituacao() != LevantamentoBibliograficoInfra.SITUACAO_AGUARDANDO_VALIDACAO ) {
			addMensagemErro( "A solicita��o j� foi validada ou finalizada." );
		} else {
		
			try {
				obj.setSituacao(LevantamentoBibliograficoInfra.SITUACAO_VALIDADA);
				
				obj.setDataValidacao(new Date());
				obj.setRegistroEntradaValidacao(getUsuarioLogado().getRegistroEntrada());
				
				MovimentoCadastro mov = new MovimentoCadastro (obj);
				mov.setCodMovimento(ArqListaComando.ALTERAR);
			
				prepareMovimento(ArqListaComando.ALTERAR);
				execute (mov);
				
				addMensagemInformation("Solicita��o validada com sucesso.");
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			}
			
		}
		
		return listarSolicitacoesLevantFuncBiblioteca();
	}

	/**
	 * Exibe o formul�rio para finalizar a solicita��o.
	 * usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/buscar_solicitacoes_indiv.jsp
	 */
	public String preFinalizar () throws ArqException{
		checkRole (SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		alterar = true;
		populateObj(true);
		
		fontesPesquisadas = obj.getFontesPesquisadas();
		observacoes = obj.getObservacao();
		
		prepareMovimento (ArqListaComando.ALTERAR);
		selecionarIdiomas();
		
		return forward(PAGINA_FORM_SOLICITACAO);
	}

	/**
	 * Exibe o arquivo anexo � solicita��o.
	 * usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/form_solicitacoes_finalizadas.jsp
	 */
	public void baixarArquivoAnexado() throws Exception {
		populateObj(true);
		
		FacesContext faces = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();

		EnvioArquivoHelper.recuperaArquivo(response, obj.getIdArquivo(), false );
		
		faces.responseComplete();
	}
	
	/**
	 * Exibe a solicita��o finalizada ao solicitante.
	 * usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/lista.jsp
	 */
	public String visualizarSolicitacaoPreenchida() throws HibernateException, ArqException {
		clear();
		
		populateObj(true);
		selecionarIdiomas();
		
		return forward(PAGINA_SOLICITACAO_FINALIZADA);
	}
	
	/**
	 * Exibe o formul�rio para que o operador altere as informa��es da solicita��o selecionada.
	 * Usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/buscar_solicitacoes_indiv.jsp
	 */
	public String editarSolicitacao() throws HibernateException, ArqException {
		checkRole (SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		clear();
		prepareMovimento(ArqListaComando.ALTERAR);
		
		int idSolicitacaoLevant = getParameterInt("id", 0);
		obj = getDAO(LevantamentoBibliograficoInfraDao.class).findSolicitacoesLevantamentoByID(idSolicitacaoLevant);
		
		for (Linguas lng : obj.getLinguasSelecionadas())
			linguasSelecionadas.add(String.valueOf(lng.getId()));
		
		return forward(PAGINA_FORM_SOLICITACAO);
	}
	
	/**
	 * Exibe o formul�rio para modificar a biblioteca da solicita��o selecionada.
	 * usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/buscar_solicitacoes_infra.jsp
	 */
	public String preTransferirSolicitacao () throws ArqException {
		checkRole (SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		BibliotecaDao dao = null;
		try {
			dao = getDAO(BibliotecaDao.class);
			populateObj(true);
			
			if ( obj.getBibliotecaResponsavel().isBibliotecaCentral() ) {
				
				bibliotecasDeTransferencia.clear();
				bibliotecasDeTransferencia.addAll( obterBibliotecasDeAtendimento() );
				
				// retira a biblioteca central
				Iterator<Biblioteca> it = bibliotecasDeTransferencia.iterator();
				while ( it.hasNext() ) {
					Biblioteca b = it.next();
					if ( b.isBibliotecaCentral() )
						it.remove();
				}
				
			} else {
				bibliotecasDeTransferencia.clear();
				bibliotecasDeTransferencia.add( BibliotecaUtil.getBibliotecaCentral() );
			}
			
			biblioteca = new Biblioteca();
			
			prepareMovimento(ArqListaComando.ALTERAR);
			
			return forward(PAGINA_TRANSFERENCIA_SOLICITACAO);
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Transfere uma solicita��o, alterando sua biblioteca respons�vel.
	 * usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/formTransferenciaSolicitacao.jsp
	 */
	public String transferirSolicitacao () throws ArqException {
		checkRole (SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		BibliotecaDao dao = null;
		
		if (biblioteca.getId() <= 0)
			addMensagemErro("Selecione uma biblioteca.");
		else
			try {
				dao = getDAO(BibliotecaDao.class);
				populateObj();
				
				obj.setBibliotecaResponsavel(biblioteca);
	
				MovimentoCadastro mov = new MovimentoCadastro(obj);
				mov.setCodMovimento(ArqListaComando.ALTERAR);
				
				execute (mov);
				
				addMensagemInformation("Solicita��o Transferida.");
				
				clear();
				
				return listarSolicitacoesLevantFuncBiblioteca();
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			} finally {
				if (dao != null)
					dao.close();
			}
		
		return null;
	}
	
	/**
	 * Altera uma solicita��o.
	 * Caso o operador tenha optado por finaliz�-la, ser� enviado um email ao solicitante.
	 * 
	 * Usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/form.jsp
	 */
	public String alterar() throws ArqException{
		checkRole (SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO);
		
		GenericDAO dao = null;
		populateObj();
		
		selecionarIdiomas();
		
		obj.setFontesPesquisadas(fontesPesquisadas);
		obj.setObservacao(observacoes);
	
		if (StringUtils.isEmpty(obj.getFontesPesquisadas()))
			addMensagemErro("Informe as fontes pesquisadas.");
		
		if (!hasErrors()){
			try {
				
				dao = getGenericDAO();
				
				MovimentoCadastro movCad = new MovimentoCadastro();
				movCad.setObjMovimentado(obj);
				movCad.setCodMovimento(ArqListaComando.ALTERAR);
				
				if (finalizarEnviarEmail) {
					obj.setSituacao(LevantamentoBibliograficoInfra.SITUACAO_FINALIZADA);
					obj.setDataFinalizacao(new Date());
					obj.setRegistroEntradaFinalizacao(getUsuarioLogado().getRegistroEntrada());
				}
				
				// Se o usu�rio anexou um arquivo.
				if (arquivo != null) {
					Integer idArquivoApagar = obj.getIdArquivo();
					
					// Se a solicita��o j� tinha um arquivo, apaga o arquivo em execu��o.
					if (idArquivoApagar != null && idArquivoApagar > 0)
						EnvioArquivoHelper.removeArquivo(idArquivoApagar);
					
					// Salva o novo arquivo.
		        	obj.setIdArquivo(EnvioArquivoHelper.getNextIdArquivo());
					EnvioArquivoHelper.inserirArquivo(obj.getIdArquivo(), arquivo.getBytes(),
							arquivo.getContentType(), arquivo.getName());
				}

				execute(movCad);
		
				if ( finalizarEnviarEmail ){
					obj.setPessoa(dao.refresh(obj.getPessoa()));
					enviarEmailNotificacao("<p>Sua solicita��o de Levantamento Bibliogr�fico foi finalizada. " +
							"Voc� pode verific�-la atrav�s do SIGAA, acessando o link da Biblioteca.</p><p>" +
							"Atenciosamente,<br/>Setor de Informa��o e Refer�ncia.</p>",
							"[SIGAA - Biblioteca] Pesquisa bibliografica finalizada",
							obj.getBibliotecaResponsavel().getEmail(), obj.getPessoa().getEmail());
				}
				
				addMensagemInformation("Solicita��o atualizada com sucesso!");
				
				return listarSolicitacoesLevantFuncBiblioteca();
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			} catch (IOException e){
				throw new ArqException (e);
			}finally {
				if (dao != null)
					dao.close();
			}
		}
		
		return null;
	}
	
	/**
	 * Cadastra a solicita��o de um usu�rio.
	 * Usado em sigaa.war/biblioteca/informacao_referencia/levantamento_biblio_infra/form.jsp
	 */
	public String cadastrarSolicitacaoIndividual() throws SegurancaException, ArqException {
		
		obj.setLinguasSelecionadas(verificarLinguasSelecionadas());
		
		// se a biblioteca passada n�o foi populada, faz isso
		if ( obj.getBibliotecaResponsavel().getEmail() == null ) {
			obj.setBibliotecaResponsavel( getGenericDAO().findByPrimaryKey(
					obj.getBibliotecaResponsavel().getId(), Biblioteca.class ) );
		}

		addMensagens(obj.validate());

		if (!hasErrors()){
			try {
				
				if (!obj.isInfra())
					obj.setSituacao(LevantamentoBibliograficoInfra.SITUACAO_VALIDADA);
				
				obj.setPessoa(getUsuarioLogado().getPessoa());
				
				cadastrarDados();
				addMensagemInformation("Solicita��o cadastrada com sucesso!<br />O setor respons�vel foi notificado " +
						"da sua solicita��o.");
				
				Biblioteca bib = obj.getBibliotecaResponsavel();
				
				String conteudo =
					"Uma nova <b>Solicita��o de Levantamento Bibliogr�fico</b> foi cadastrada no sistema para a <b>" +
					bib.getDescricao() + "</b>." +
					"<p>Voc� pode verific�-la atrav�s do SIGAA, acessando o link da Biblioteca.";
				
				String assunto = "Nova Solicita��o de Levantamento Bibliogr�fico";
				
				String remetente = getUsuarioLogado().getPessoa().getEmail();
				
				enviarEmailNotificacao(
						conteudo, assunto, remetente,
						bib.getEmail());
				
				// se a biblioteca n�o for a central, tamb�m envia o email para a central:
				if ( ! bib.isBibliotecaCentral() ) {
					enviarEmailNotificacao(
							conteudo, assunto + " : " + bib.getDescricao(), remetente,
							BibliotecaUtil.getBibliotecaCentral().getEmail() );
				}
				
				return redirect(getSubSistema().getLink());
			} catch (NegocioException e){
				addMensagens(e.getListaMensagens());
			}
		}
		return null;
	}

	/**
	 * Cadastra uma solicita��o.
	 */
	private void cadastrarDados() throws NegocioException, ArqException {
		MovimentoCadastro movCad = new MovimentoCadastro();
		movCad.setObjMovimentado(obj);
		movCad.setCodMovimento(ArqListaComando.CADASTRAR);
		execute(movCad);
	}

	/**
	 * Envia e-mails de notifica��o.
	 * 1. Ap�s o Solicitante cadastrar a solicita��o de levantamento Bibliogr�fico, o SIR recebe o e-mail.
	 * 2. Ap�s o SIR finalizar a pesquisa, pode enviar e-mail informando o Solicitante.
	 */
	private void enviarEmailNotificacao(String conteudo, String assunto, String remetente, String destinatario) {
		MailBody mail = new MailBody();
		mail.setAssunto(assunto);
		mail.setFromName(remetente);
		mail.setMensagem(conteudo);
		mail.setEmail(destinatario);
		Mail.send(mail);
	}

	/**
	 * Retorna do banco os idiomas selecionados no formul�rio.
	 */
	private List <Linguas> verificarLinguasSelecionadas() throws DAOException {
		
		List <Linguas> lngs = new ArrayList<Linguas>();

		GenericDAO dao = getDAO(LevantamentoBibliograficoInfraDao.class);
		
		if (linguasSelecionadas != null)
			for (String l : linguasSelecionadas) {
				Linguas ls = dao.findByPrimaryKey(Integer.valueOf(l),
						Linguas.class);
				lngs.add(ls);
			}
		
		return lngs;
	}

	/**
	 * Auxilia na exibi��o dos idiomas selecionados
	 */
	private void selecionarIdiomas (){
		
		linguasSelecionadas = new ArrayList <String> ();
		
		if (obj != null)
			for (Linguas l: obj.getLinguasSelecionadas())
				linguasSelecionadas.add(""+l.getId());
	}
	
	/**
	 * Retorna todos os per�odos.
	 */
	public Collection <SelectItem> getAllPeriodos() {
		return toSelectItems(PeriodoLevantBibliografico.getPeriodosDisponiveis(), "id", "descricao");
	}
	
	/**
	 * Retorna todos os idiomas.
	 */
	public Collection <SelectItem> getAllLinguas() throws HibernateException {
		return toSelectItems(linguas, "id", "descricao");
	}
	
	public List<String> getLinguasSelecionadas() {
		return linguasSelecionadas;
	}

	public void setLinguasSelecionadas(
			List<String> linguasSelecionadas) {
		this.linguasSelecionadas = linguasSelecionadas;
	}

	public String getOutraLinguaDescricao() {
		return outraLinguaDescricao;
	}

	public void setOutraLinguaDescricao(String outraLinguaDescricao) {
		this.outraLinguaDescricao = outraLinguaDescricao;
	}

	public SortedSet<LevantamentoBibliograficoInfra> getSolicitacoes() {
		return solicitacoes;
	}

	public List<Linguas> getLinguas() {
		return linguas;
	}

	public void setLinguas(List<Linguas> linguas) {
		this.linguas = linguas;
	}

	public Boolean getFinalizarEnviarEmail() {
		return finalizarEnviarEmail;
	}

	public void setFinalizarEnviarEmail(Boolean finalizarEnviarEmail) {
		this.finalizarEnviarEmail = finalizarEnviarEmail;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public boolean isVisualizarSolicitacaoIndividual() {
		return visualizarSolicitacaoIndividual;
	}

	public void setVisualizarSolicitacaoIndividual(
			boolean visualizarSolicitacaoIndividual) {
		this.visualizarSolicitacaoIndividual = visualizarSolicitacaoIndividual;
	}

	public boolean isVisualizarSolicitacaoInfra() {
		return visualizarSolicitacaoInfra;
	}

	public void setVisualizarSolicitacaoInfra(boolean visualizarSolicitacaoInfra) {
		this.visualizarSolicitacaoInfra = visualizarSolicitacaoInfra;
	}

	public String getOutroPeriodoDescricao() {
		return outroPeriodoDescricao;
	}

	public void setOutroPeriodoDescricao(String outroPeriodoDescricao) {
		this.outroPeriodoDescricao = outroPeriodoDescricao;
	}
	
	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	/**
	 * Indica se o usu�rio logado � um docente.
	 */
	public boolean isDocente (){
		
		Servidor s = getUsuarioLogado().getServidor();
		
		if (s != null)
			return s.isDocente();
		
		return false;
	}
	
	/**
	 * Busca todas as bibliotecas onde o usu�rio poderia solicitar o levantamento.
	 */
	private List <Biblioteca> getBibliotecasDoUsuario () throws ArqException{
		
		if (bibliotecasDoUsuario.isEmpty()){
			bibliotecasDoUsuario.addAll(BibliotecaUtil.getBibliotecasDoDiscenteByServico(getUsuarioLogado().getDiscente(), TipoServicoInformacaoReferencia.LEVANTAMENTO_BIBLIOGRAFICO) );
		}
		
		return bibliotecasDoUsuario;
	}
	
	/**
	 * Indica se o usu�rio solicitante pode fazer solicita��es de infraestrutura.
	 */
	public boolean isAutorizadoInfra () throws DAOException{
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO();
			dao.refresh(getUsuarioLogado());
			
			return
					getUsuarioLogado().isCoordenadorLato() ||
					getUsuarioLogado().isFuncionario() ||
					getUsuarioLogado().getVinculoAtivo().isVinculoServidor() ||
					getUsuarioLogado().getVinculoAtivo().isVinculoCoordenacaoPolo();
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * TODO jsp
	 * @return
	 * @throws ArqException
	 */
	public List <SelectItem> getBibliotecasDoUsuarioCombo () throws ArqException{
		return toSelectItems(getBibliotecasDoUsuario(), "id", "descricao");
	}

	/**
	 * TODO jsp
	 * @return
	 * @throws ArqException
	 */
	public List <SelectItem> getBibliotecasDeBuscaCombo () throws ArqException{
		return toSelectItems(getBibliotecasDeBusca(), "id", "descricao");
	}
	
	/**
	 * TODO jsp
	 * @return
	 */
	public List <SelectItem> getBibliotecasDeTransferenciaCombo () {
		return toSelectItems(getBibliotecasDeTransferencia(), "id", "descricao");
	}
	
	/**
	 * TODO jsp
	 * @return
	 * @throws DAOException
	 */
	public List<Biblioteca> getBibliotecasDeBusca() throws DAOException {
		if ( bibliotecasDeBusca.isEmpty() )
			bibliotecasDeBusca.addAll( obterBibliotecasDeAtendimento() );

		return bibliotecasDeBusca;
	}
	
	public String getFontesPesquisadas() {
		return fontesPesquisadas;
	}

	public void setFontesPesquisadas(String fontesPesquisadas) {
		this.fontesPesquisadas = fontesPesquisadas;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}

	public int getSituacao() {
		return situacao;
	}

	public void setSituacao(int situacao) {
		this.situacao = situacao;
	}

	public int getInfra() {
		return infra;
	}

	public void setInfra(int infra) {
		this.infra = infra;
	}

	public List<Biblioteca> getBibliotecasDeTransferencia() {
		return bibliotecasDeTransferencia;
	}

}