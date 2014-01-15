/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 *
 * Criado em:  03/12/2008
 */
package br.ufrn.sigaa.mobile.biblioteca.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoRenovaEmprestimo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.GeraPesquisaTextualFactory;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.RegistraEstatisticasBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.EmprestimoByDataEmprestimoComparator;
import br.ufrn.sigaa.biblioteca.util.ExemplarByLocalizacaoBibliotecaComparator;
import br.ufrn.sigaa.biblioteca.util.FasciculoByBibliotecaAnoCronologicoNumeroComparator;
import br.ufrn.sigaa.biblioteca.util.PaginacaoBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;
import br.ufrn.sigaa.mensagens.MensagensBiblioteca;
import br.ufrn.sigaa.mobile.commons.SigaaAbstractMobileController;

/**
 *
 *    Bean que controla as opera��es da parte mobile da biblioteca.
 *
 * @author jadson
 * @since 03/12/2008
 * @version 1.0 criacao da classe
 *
 */
@Component("operacoesBibliotecaMobileMBean")
@Scope("session")
public class OperacoesBibliotecaMobileMBean  extends SigaaAbstractMobileController <UsuarioBiblioteca>{

	/** O menu principal da biblioteca m�bile  */
	public static final String PAGINA_MENU_PRINCIPAL_BIBLIOTECA_MOBILE = "/mobile/biblioteca/menu.jsp";
	/** A p�gina onde o usu�rio visualiza os seus empr�timos renov�veis  */
	public static final String PAGINA_VISUALIZA_MEUS_EMPRESTIMOS_RENOVAVEIS_MOBILE = "/mobile/biblioteca/visualizaMeusEmprestimosRenovaveisMobile.jsp";
	/** A p�gina onde o usu�rio visualiza os seus empr�stimos em aberto  */
	public static final String PAGINA_VISUALIZA_MEUS_EMPRESTIMO_ABERTOS_MOBILE = "/mobile/biblioteca/visualizaMeusEmprestimosAbertosMobile.jsp";
	/** A p�gina de filtros mobile onde o usu�rio visualiza o seu hist�rico de empr�stimos */
	public static final String PAGINA_CONSULTA_MEUS_EMPRESTIMOS_MOBILE = "/mobile/biblioteca/consultaMeusEmprestimosMobile.jsp";
	/** A p�gina que mostra a listagem com o hist�rico de  empr�stimos do usu�rio */
	public static final String PAGINA_RESUTADOS_CONSULTA_MEUS_EMPRESTIMOS_MOBILE = "/mobile/biblioteca/resultadoConsultaMeusEmprestimosMobile.jsp";
	/** A p�gina de filtros para a consulta no acervo m�bile */
	public static final String PAGINA_CONSULTA_TITULO_MOBILE = "/mobile/biblioteca/consultaTituloMobile.jsp";
	/** A p�gina que mostra os resultados na busca do acervo m�bile */
	public static final String PAGINA_RESULTADOS_CONSULTA_TITULO_MOBILE = "/mobile/biblioteca/resultadosConsultaTituloMobile.jsp";
	/** A p�gina que visualiza os materiais do t�tulo selecionado na busca do acervo m�bile  */
	public static final String PAGINA_VISUALIZA_MATERIAS_MOBILE = "/mobile/biblioteca/visualizaMateriasMobile.jsp";
	
	/** As informacoes do titulo dos emprestimos */
	private List <CacheEntidadesMarc> titulosResumidos;
	
	/** Emprestimos que podem ser renovados */
	private List <Emprestimo> emprestimosAtivosRenovaveis;
	
	/** Lista contendo todos os emprestimos ativos */
	private List <Emprestimo> emprestimosAtivos;
		
	/** Consultas dos empr�stimos dos usu�rios at� os que j� foram devolvidos */
	private List<Emprestimo> emprestimos;
	
	/** as datas s�o jogada nas seguintes vari�veis porque n�o tem como validar com java script na p�gina */
	private String diaInicial;
	/** as datas s�o jogada nas seguintes vari�veis porque n�o tem como validar com java script na p�gina */
	private String mesInicial;
	/** as datas s�o jogada nas seguintes vari�veis porque n�o tem como validar com java script na p�gina */
	private String anoInicial;
	/** as datas s�o jogada nas seguintes vari�veis porque n�o tem como validar com java script na p�gina */
	private String diaFinal;
	/** as datas s�o jogada nas seguintes vari�veis porque n�o tem como validar com java script na p�gina */
	private String mesFinal;
	/** as datas s�o jogada nas seguintes vari�veis porque n�o tem como validar com java script na p�gina */
	private String anoFinal;

	/** A data inicial para consulta o hist�rico de empr�stimos de um usu�rio */
	private Date dataInicial;
	
	/** A data final para consulta o hist�rico de empr�stimos de um usu�rio */
	private Date dataFinal;
	
	/** Dados para a consulta de materiais */
	private String titulo, autor, assunto, editora;
	
	/** O t�tulo selecionado para ver os materiais */
	private int idTituloSelecionado;
	
	/** Caso o titulo seja de outra coisa que n�o peri�dicos */
	private List<Exemplar> exemplares = new ArrayList<Exemplar>();
	
	/** Caso o titulo seja de periodico */
	private Assinatura assinatura;
	/** Caso o titulo seja de periodico. contem os fasc�culos dele */
	private List<Fasciculo> fasciculos = new ArrayList<Fasciculo>();
	
	/**
	 * Indica que o t�tulo selecionado � de um peri�dico.
	 */
	private boolean periodico = false;
	
	/** Os ids das bibliotecas que possuem acervo p�blico no sistema. O resultado da busca p�blica s� deve buscar nessas biblioteca. */
	protected List<Integer> idsBibliotecasAcervoPublico;
	
	/** Utilizado para gerar a buscas nativas no banco*/
	private GeraPesquisaTextual geradorPesquisa ;
	
	/** Indica se o usu�rio acessou via /mobile/toush */
	private boolean touch = false;
	
	
	/* Vari�veis necess�rias para a pagina��o da Consulta de Acervo */
	
	/** Guarda a quantidade de resultados por p�gina que o usu�rio deseja ver na consulta*/
	protected int quantideResultadosPorPagina = 25;
	
	/** A quantidade total de resultados que ser�o p�ginados */
	protected int quantidadeTotalResultados = 0;
	
	/** A quantidade total de p�ginas da p�gina��o */
	protected int quantidadePaginas = 1;
	
	/** A p�gina atual que est� sendo mostrada ao usu�rio*/
	protected int paginaAtual;
	
	/**  
	 * Os resultados da consulta, que ser�o p�ginados para visualiza��o do usu�rio.  
	 */
	protected List<CacheEntidadesMarc> resultadosBuscados = new ArrayList<CacheEntidadesMarc>();
	
	/**  Guarda os artigos que ser�o visualizados pelos usu�rios no momento
	 *   Utilizado para diminuir a quantidade de resultados exibidos ao usu�rio.
	 *   A pagina��o ocorre em mem�ria porque as consultas geralmente s�o r�pidas, o que demora mais � redenrizar uma quantidade muito grande para o usu�rio.  
	 */
	protected List<CacheEntidadesMarc> resultadosPaginadosEmMemoria = new ArrayList<CacheEntidadesMarc>();
	
	/**
	 * Redirenciona o usu�rio para a p�gina principal da bibliotecam mobile.
	 *
     * Chamado a partir da p�gina: /sigaa.war/mobile/menu.jsp
     * 
	 * @return
	 * @throws ArqException
	 * @throws DAOException
	 */
	public String entrarModuloBiblioteca() throws ArqException{
		geradorPesquisa = new GeraPesquisaTextualFactory().getGeradorPesquisaTextual();
		return telaMenuPrincipalBibliotecaMobile();
	}
	
	
	
	/**
	 * Redirenciona o usu�rio para o menu principal do m�dulo mobile.
	 *
     * Chamado a partir da p�gina: /sigaa.war/mobile/biblioteca/menu.jsp
     * 
	 * @return
	 * @throws ArqException
	 * @throws DAOException
	 */
	public String voltarMenuPrincipal() throws ArqException{
		return forward("/mobile/menu.jsp");
	}
	
	/**
	 * Inicia o caso de uso para renovar os empr�stimos do usu�rio logado.
	 *
     * Chamado a partir da p�gina: /sigaa.war/mobile/biblioteca/menu.jsp
     * 
	 * @return
	 * @throws ArqException
	 * @throws DAOException
	 */
	public String iniciarRenovacao() throws ArqException{
		touch = getParameterBoolean("touch");
		
		prepareMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
		
		carregaEmprestimosEmAbertosRenovaveis();
		return telaVisualizaMeusEmpretimosRenovaveisMobile();
	}
	
	
	/**
	 *   Caso de uso no qual o usu�rio visualiza todos os empr�stimos dele, mesmo os que j� tenham
	 *   sido renovados
	 *
	 *	Chamado a partir da p�gina: /sigaa.war/mobile/biblioteca/menu.jsp
	 *
	 * @return
	 * @throws ArqException
	 * @throws DAOException
	 */
	public String iniciarVisualizarEmprestimos(){
		touch = getParameterBoolean("touch"); 
		
		carregaEmprestimosEmAberto();
		return telaVisualizaEmprestimosAbertosMobile();
	}
	
	
	
	
	
	
	/**
	 * Consulta os �ltimo empr�stimos do usu�rio. Inclusive os que j� foram devolvidos.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/mobile/biblioteca/menu.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarConsultaUltimoEmpretimos(){
		
		touch = getParameterBoolean("touch");
		
		diaInicial = "";
	 	mesInicial = "";
	 	anoInicial = "";
	 	diaFinal = "";
	 	mesFinal = "";
	 	anoFinal = "";
		
		return telaConsultaMeusEmprestimosMobile();
	}
	
	
	
	/**
	 *   Encaminha para a p�gina onde o usu�rio entra com os crit�rios de busca.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/mobile/biblioteca/menu.jsp
	 * @return
	 * @throws ArqException
	 */
	public String iniciarConsultaTitulo(){
		
		touch = getParameterBoolean("touch");
		
		titulo = null;
		autor = null;
		idTituloSelecionado = 0;
		exemplares = new ArrayList<Exemplar>();
		assinatura = null;
		fasciculos = new ArrayList<Fasciculo>();
		periodico = false;
		paginaAtual = 1;
		
		return telaConsultaTituloMobile();
	}
	

	
	
	/**
	 * 
	 * Chama o processador para renovar os emprestimos selecionados pelo usu�rio
	 *
	 * Chamado a partir da p�gina: /sigaa.war/mobile/biblioteca/mostraEmprestimosRenovaveisMobile.jsp
	 * @return
	 */
	public String renovarEmprestimos(){
		
		
		/* passa as posicoes dos elementos que foram selecionados como parametro na requisicao
		 * dos ckeckbox html comuns (nao quero usar checkbox do jsf porque aqui eh mobile). */
		String[] posicoes = getCurrentRequest().getParameterValues("emprestimosSelecionados");
		
		if(posicoes == null || posicoes.length <= 0 || emprestimosAtivosRenovaveis == null || emprestimosAtivosRenovaveis.size() <= 0 ){
			addMensagemErro("Nenhum empr�stimo foi selecionado para renova��o.");
			return telaVisualizaMeusEmpretimosRenovaveisMobile();
		}
		
		List <Emprestimo> emprestimosSelecionados = new ArrayList <Emprestimo> ();
		
		for (int index = 0; index < posicoes.length && ( Integer.parseInt( posicoes[index] ) < emprestimosAtivosRenovaveis.size() ) ; index++) {
			emprestimosSelecionados.add(emprestimosAtivosRenovaveis.get(Integer.parseInt( posicoes[index] )));
		}
			
		return renovar(emprestimosSelecionados);
	}


	/**
	 * 
	 * Chama o processador para renovar todos os emprestimos
	 *
	 * Chamado a partir da p�gina: /sigaa.war/mobile/biblioteca/mostraEmprestimosRenovaveisMobile.jsp
	 * @return
	 */
	public String renovarTodos(){
		
		List <Emprestimo> emprestimosSelecionados = new ArrayList <Emprestimo> ();
		
		emprestimosSelecionados.addAll(emprestimosAtivosRenovaveis);

		return renovar(emprestimosSelecionados);
	}
	

	/**
	 * Respons�vel por realizar renova��es de empr�stimos de livros.
	 * 
	 * @param emprestimosSelecionados
	 * @return
	 */
	private String renovar(List<Emprestimo> emprestimosSelecionados) {
		UsuarioBibliotecaDao dao = null;
		
		try{
		
			//  monta as informacoes que o processador precisa apezar de algumas nao serem necessarias aqui.
			// soh quando o modulo desktop eh usado.
			
			dao = getDAO(UsuarioBibliotecaDao.class);
			
			
			List <Integer> idsMateriais = new ArrayList <Integer> ();
			
			for (Emprestimo e : emprestimosSelecionados)
				idsMateriais.add(e.getMaterial().getId());

	
			UsuarioBiblioteca usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), dao);
			
			MovimentoRenovaEmprestimo mov = new MovimentoRenovaEmprestimo(idsMateriais, usuarioBiblioteca, usuarioBiblioteca.getSenha());
			
			mov.setCodMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
			
			
			br.ufrn.sigaa.biblioteca.integracao.dtos.RetornoOperacoesCirculacaoDTO retorno = execute(mov, getCurrentRequest());
		
			for (String mensagem : retorno.mensagemAosUsuarios) {
				addMensagemWarning(mensagem);
			}
			
			addMensagem(MensagensBiblioteca.EMPRESTIMOS_RENOVADOS);
			
			prepareMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
			carregaEmprestimosEmAbertosRenovaveis();
			
			return telaVisualizaMeusEmpretimosRenovaveisMobile();		
			
			
		}catch(NegocioException ne){
			ne.printStackTrace();
			addMensagemErro(ne.getMessage());
		}catch(ArqException arqEx){
			// Importante tratar todos os erros senao se redirecionado para a tela de erro,
			// na qual esta tela nao se adequa a visualizacao no modulo mobile.
			// TODO implementar outra pagina de erro para o sistema mobile
			arqEx.printStackTrace();
			addMensagemErroPadrao();
		}
	
		return telaVisualizaMeusEmpretimosRenovaveisMobile(); // se de algum erro volta para a tela mas nao carrega os dados novamente
	}
	
	
	/**
	 *   Consulta todos os empr�stimo que n�o foram estornados do usu�rio no per�odo passado.
	 * 
	 * </br>Chamado a partir da p�gina: /sigaa.war/mobile/biblioteca/consultaMeusEmprestimosMobile.jsp
	 * @return
	 * @throws DAOException
	 */
	public String consultaUltimosEmprestimosUsuario(){
		
		touch = getParameterBoolean("touch");
		
		EmprestimoDao emprestimoDAO = getDAO(EmprestimoDao.class);
		UsuarioBibliotecaDao dao = null;
		
		titulosResumidos = new ArrayList <CacheEntidadesMarc>();
		
		if (!touch){
		
			int diaInicial = 0;
			int mesInicial = 0;
			int anoInicial = 0;
			int diaFinal = 0;
			int mesFinal = 0;
			int anoFinal = 0;
			
			
			if(this.diaInicial.length() != 2 || this.diaFinal.length() != 2 ||
					this.mesInicial.length() != 2 || this.mesFinal.length() != 2 ||
					this.anoInicial.length() != 4 || this.anoFinal.length() != 4 ){
				
				addMensagemErro("Data Inv�lida");
				return null;
			}
			
			try{
				
				diaInicial = Integer.parseInt(this.diaInicial);
				mesInicial = Integer.parseInt(this.mesInicial);
				anoInicial = Integer.parseInt(this.anoInicial);
				diaFinal = Integer.parseInt(this.diaFinal);
				mesFinal = Integer.parseInt(this.mesFinal);
				anoFinal= Integer.parseInt(this.anoFinal);
				
			}catch(NumberFormatException nfe){
				addMensagemErro("Data Inv�lida");
				return null;
			}
			
			
			Calendar ci = Calendar.getInstance();
			ci.set(Calendar.DAY_OF_MONTH, diaInicial);
			ci.set(Calendar.MONTH, mesInicial-1);
			ci.set(Calendar.YEAR, anoInicial);
			
			Calendar cf = Calendar.getInstance();
			cf.set(Calendar.DAY_OF_MONTH, diaFinal);
			cf.set(Calendar.MONTH, mesFinal-1);
			cf.set(Calendar.YEAR, anoFinal);
			
			this.dataInicial = ci.getTime();
			this.dataFinal = cf.getTime();
		
		} else {
			this.dataInicial = null;
			this.dataFinal = null;
		}
		
		try {
			
			dao = getDAO(UsuarioBibliotecaDao.class);
			
			UsuarioBiblioteca usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), dao);
		
			emprestimos = emprestimoDAO.findEmprestimosAtivosByUsuarioMaterialBiblioteca(usuarioBiblioteca, null, null, null, null, null, dataInicial, dataFinal, null);
		
			Collections.sort(emprestimos, new EmprestimoByDataEmprestimoComparator());
			
			for (Emprestimo e : emprestimos) {
				e.setDataRenovacao(BibliotecaUtil.getDataUltimaRenovacao(e));
				titulosResumidos.add(BibliotecaUtil.obtemDadosTituloCache(e.getMaterial().getTituloCatalografico().getId()));
			}
			
			if (emprestimos.size() == 0)
				addMensagemWarning("O usu�rio n�o realizou empr�stimos da data informada.");
		
			
			
		}catch(NegocioException ne){
			ne.printStackTrace();
			addMensagemErro(ne.getMessage());
		}catch (DAOException daoEx) {
			daoEx.printStackTrace();
			addMensagemErroPadrao();
		}finally{
			if(dao != null) dao.close();
			if(emprestimoDAO != null ) emprestimoDAO.close();
		}
		
		return telaResultadosConsultaMeusEmprestimosMobile();
	}
	
	/**
	 * Realiza a consulta de t�tulos do sistema mobile e redireciona para a p�gina de resultados.
	 *
	 * Chamado a partir da p�gina: sigaa.war/biblioteca/mobile/biblioteca/consultaTituloMobile.jsp
	 * @return
	 */
	public String consultarTitulos(){
		
		TituloCatalograficoDao dao = getDAO(TituloCatalograficoDao.class);
		
		if(StringUtils.isEmpty(titulo) && StringUtils.isEmpty(autor) && StringUtils.isEmpty(assunto) && StringUtils.isEmpty(editora)){
			addMensagemErro("Informe um crit�rio de busca.");
			return null;
		}
		
		try {
			geradorPesquisa = new GeraPesquisaTextualFactory().getGeradorPesquisaTextual();
			
			if (touch) {
				resultadosBuscados = dao.buscaMultiCampoPublica(geradorPesquisa, CampoOrdenacaoConsultaAcervo.TITULO, titulo, assunto, autor, null, editora, null, null, null, null, null, false, getIdsBibliotecasAcervoPublico());
				geraResultadosPaginacao();
			} else {
				titulosResumidos = dao.buscaMultiCampoMobile(geradorPesquisa, CampoOrdenacaoConsultaAcervo.TITULO, titulo, autor, getIdsBibliotecasAcervoPublico());
			}
			
			if(!touch && titulosResumidos.size() == 0){
				addMensagemErro("N�o foram encontrados t�tulos com os crit�rios informados.");
			}
			
			if(!touch && TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_MOBILE.compareTo( titulosResumidos.size() ) <= 0){
				addMensagemWarning("A busca resultou em um n�mero muito grande de resultados. Somente os "+TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_MOBILE+" primeiros est�o sendo mostrados.");
			}
			
			if (touch && getQuantidadeTotalResultados() == 0) {
				addMensagemErro("N�o foram encontrados t�tulos com os crit�rios informados.");
			}
			
			if (touch && TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_PUBLICA.compareTo(getQuantidadeTotalResultados()) <= 0){
				addMensagemWarning("A busca resultou em um n�mero muito grande de resultados. Somente os "+TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_PUBLICA+" primeiros est�o sendo mostrados.");
			}
			
		}	catch (DAOException e) {
			e.printStackTrace();
			addMensagemErroPadrao();
		} finally {
			if(dao != null) dao.close();
		}
		
		return telaResultadosConsultaTituloMobile();
	}
	
	/**
	 *   Carrega as informa��es dos materiais do t�tulo, sejam eles exemplares ou fasc�culos e
	 *   mostram para o usu�rio na p�gina espef�cica.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/mobile/biblioteca/resultadosConsultaTituloMobile.jsp
	 * @return
	 */
	public String visualizarMateriais(){
		
		FasciculoDao daoFasciculo = getDAO(FasciculoDao.class);
		
		TituloCatalografico titulo = new TituloCatalografico(idTituloSelecionado);
		try {
			
			titulo = daoFasciculo.refresh(titulo);
		
			if(titulo.getFormatoMaterial().isFormatoPeriodico()){
				
				periodico = true;
				
				List<Assinatura> assinaturas = titulo.getAssinaturas();
				
				if(assinaturas != null){
					
					for (Assinatura assi : assinaturas) {
						
						fasciculos =  daoFasciculo.findTodosFasciculosAtivosDaAssinatura(assi.getId(), null);
						
						Collections.sort(fasciculos, new FasciculoByBibliotecaAnoCronologicoNumeroComparator());
						
						for (Fasciculo fasciculo : fasciculos) {
							carregaDadosFasciculos(daoFasciculo, fasciculo);
						}
					}
					
				}
				
			}else{
				
				periodico = false;
				
				ExemplarDao daoExemplares = getDAO(ExemplarDao.class);
				
				exemplares = daoExemplares.findAllExemplarAtivosDoTitulo(titulo.getId());
				
				for (Exemplar exemplar : exemplares) {
					carregaDadosExemplares(daoExemplares, exemplar);
				}
				
				Collections.sort(exemplares, new ExemplarByLocalizacaoBibliotecaComparator());
				
			}
			
		} catch (DAOException e) {
			e.printStackTrace();
			addMensagemErroPadrao();
		}
			
		return telaVisualizaMateriaisMobile();
	}
	
	
	
	
	/**
	 * Carrega os empr�stimos ativos do usu�rio que podem ser renovados
	 *
	 * @return
	 */
	private void carregaEmprestimosEmAbertosRenovaveis(){
		
		EmprestimoDao emprestimoDAO = getDAO(EmprestimoDao.class);
		emprestimosAtivosRenovaveis = new ArrayList <Emprestimo> ();
		titulosResumidos = new ArrayList <CacheEntidadesMarc>();
		UsuarioBibliotecaDao dao = null;
		
		try {
			dao = getDAO(UsuarioBibliotecaDao.class);
			
			UsuarioBiblioteca usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), dao);
			

			emprestimosAtivosRenovaveis = emprestimoDAO.findEmprestimosAtivosByUsuarioMaterialBiblioteca(usuarioBiblioteca, null, null, false, true, null, null, null, null);
		
			for (Emprestimo e : emprestimosAtivosRenovaveis){
				e.setMaterial(dao.refresh(e.getMaterial()));
				titulosResumidos.add( BibliotecaUtil.obtemDadosTituloCache( e.getMaterial().getTituloCatalografico().getId() )  );
			}
			
			if (emprestimosAtivosRenovaveis.size() == 0)
				addMensagemWarning("Usu�rio n�o possui empr�stimos que podem ser renovados");
		
			
			
		}catch(NegocioException ne){
			ne.printStackTrace();
			addMensagemErro(ne.getMessage());
		}catch(DAOException daoEx){
			// Tem que tratar todos os erros senao vai pra a tela de erro
			// TODO implementar outra pagina de erro padr�o para o sistema mobile
			daoEx.printStackTrace();
			addMensagemErroPadrao();
		}finally{
			if(dao != null) dao.close();
			if(emprestimoDAO != null) emprestimoDAO.close();
		}
	}
	
	
	
	/**
	 * Carrega os empr�stimos ativos do usu�rio que podem ser renovados
	 *
	 * @return
	 */
	private void carregaEmprestimosEmAberto(){
		
		EmprestimoDao emprestimoDAO = getDAO(EmprestimoDao.class);
		emprestimosAtivosRenovaveis = new ArrayList <Emprestimo> ();
		titulosResumidos = new ArrayList <CacheEntidadesMarc> ();
		
		UsuarioBibliotecaDao dao = null;
		
		try {
			dao = getDAO(UsuarioBibliotecaDao.class);
			
			UsuarioBiblioteca usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), dao);
			
			emprestimosAtivos = emprestimoDAO.findEmprestimosAtivosByUsuarioMaterialBiblioteca(usuarioBiblioteca, null, null, false, null, null, null, null, null);
			
			for (Emprestimo e : emprestimosAtivos){
				e.setDataRenovacao(BibliotecaUtil.getDataUltimaRenovacao(e));
				titulosResumidos.add( BibliotecaUtil.obtemDadosTituloCache(e.getMaterial().getTituloCatalografico().getId()) );
			}
			

			if (emprestimosAtivos.size() == 0)
				addMensagemWarning("Usu�rio n�o possui empr�stimos ativos ");
			
			
		}catch(NegocioException ne){
			ne.printStackTrace();
			addMensagemErro(ne.getMessage());
		}catch(DAOException daoEx){
			// Tem que tratar todos os erros sen�o vai pra a tela de erro do sistema web
			// TODO implementar outra p�gina de erro padr�o para o sistema mobile
			daoEx.printStackTrace();
			addMensagemErroPadrao();
		}finally{
			if(dao != null) dao.close();
			if(emprestimoDAO != null) emprestimoDAO.close();
		}
	}
	
	
	
	/**
	 *   Retorna os ids das bibliotecas que possuem acervo p�blico. (liberado para consulta a usu�rios que n�o trabalham nas bibliotecas)
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @return
	 * @throws DAOException
	 */
	public List<Integer>  getIdsBibliotecasAcervoPublico() throws DAOException{
		if(idsBibliotecasAcervoPublico == null){
			BibliotecaDao dao = null;
			try{
				dao =getDAO(BibliotecaDao.class);
				idsBibliotecasAcervoPublico = dao.findIdsBibliotecaAcervoPublico();
			}finally{
				if(dao != null) dao.close();
			}
		}
		return idsBibliotecasAcervoPublico;
	}
	
	
	/**
	 * carrega os relacionamentos lazy dos exemplares
	 */
	private void carregaDadosExemplares(GenericDAO dao, Exemplar exemplar) throws DAOException{
		dao.refresh(exemplar.getSituacao());
		dao.refresh(exemplar.getStatus());
		dao.refresh(exemplar.getColecao());
		dao.refresh(exemplar.getBiblioteca());
		dao.refresh(exemplar.getTipoMaterial());
	}
	
	/**
	 * carega os relacionamentos lazy dos fasc�culos
	 */
	private void carregaDadosFasciculos(GenericDAO dao, Fasciculo fasciculo) throws DAOException{
		dao.refresh(fasciculo.getSituacao());
		dao.refresh(fasciculo.getStatus());
		dao.refresh(fasciculo.getColecao());
		dao.refresh(fasciculo.getBiblioteca());
		dao.refresh(fasciculo.getTipoMaterial());
	}
	
	
	////////p�gina de navega��o /////////
	
	/**
	 * Redireciona para a p�gina onde o usu�rio visualiza os empr�stimos renov�veis
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaVisualizaMeusEmpretimosRenovaveisMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/visualizaMeusEmprestimosRenovaveisMobile.jsp" : PAGINA_VISUALIZA_MEUS_EMPRESTIMOS_RENOVAVEIS_MOBILE);
	}
	
	/**
	 * Redireciona para a p�gina onde o usu�rio visualiza os empr�stimos em aberto da parte m�bile.
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaVisualizaEmprestimosAbertosMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/visualizaMeusEmprestimosAbertosMobile.jsp" : PAGINA_VISUALIZA_MEUS_EMPRESTIMO_ABERTOS_MOBILE);
	}
	
	/**
	 * Redireciona para a p�gina de filtro onde o usu�rio visualiza o seu hist�rico de empr�stimos
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaConsultaMeusEmprestimosMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/consultaMeusEmprestimosMobile.jsp" : PAGINA_CONSULTA_MEUS_EMPRESTIMOS_MOBILE);
	}
	
	/**
	 * Redireciona para a p�gina onde o usu�rio visualiza o seu hist�rico de empr�stimos
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaResultadosConsultaMeusEmprestimosMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/resultadoConsultaMeusEmprestimosMobile.jsp" : PAGINA_RESUTADOS_CONSULTA_MEUS_EMPRESTIMOS_MOBILE);
	}
	
	/**
	 * Redireciona para a p�gina onde o usu�rio consulta o acervo na parte mobile
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaConsultaTituloMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/consultaTituloMobile.jsp" : PAGINA_CONSULTA_TITULO_MOBILE);
	}
	
	/**
	 * Redireciona para a p�gina onde o usu�rio visualiza os t�tulos da busca do acervo mobile
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaResultadosConsultaTituloMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/resultadosConsultaTituloMobile.jsp" : PAGINA_RESULTADOS_CONSULTA_TITULO_MOBILE);
	}
	
	/**
	 * Redireciona para a p�gina onde o usu�rio visualiza os materiais de um t�tulo
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaVisualizaMateriaisMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/visualizaMateriasMobile.jsp" : PAGINA_VISUALIZA_MATERIAS_MOBILE);
	}
	
	/**
	 * Redireciona para a p�gina onde o usu�rio visualiza  o menu principal da biblioteca.
	 * 
	 *  <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaMenuPrincipalBibliotecaMobile(){
		return forward(PAGINA_MENU_PRINCIPAL_BIBLIOTECA_MOBILE);
	}
	
	
	// sets e gets
	
	public List<Emprestimo> getEmprestimosAtivosRenovaveis() {
		return emprestimosAtivosRenovaveis;
	}
	
	public void setEmprestimosAtivosRenovaveis(
			List<Emprestimo> emprestimosAtivosRenovaveis) {
		this.emprestimosAtivosRenovaveis = emprestimosAtivosRenovaveis;
	}

	public List <CacheEntidadesMarc> getTitulosResumidos(){
		return titulosResumidos;
	}

	public List<Emprestimo> getEmprestimosAtivos() {
		return emprestimosAtivos;
	}

	public void setEmprestimosAtivos(List<Emprestimo> emprestimosAtivos) {
		this.emprestimosAtivos = emprestimosAtivos;
	}

	public List<Emprestimo> getEmprestimos() {
		return emprestimos;
	}

	public void setEmprestimos(List<Emprestimo> emprestimos) {
		this.emprestimos = emprestimos;
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

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public int getIdTituloSelecionado() {
		return idTituloSelecionado;
	}

	public void setIdTituloSelecionado(int idTituloSelecionado) {
		this.idTituloSelecionado = idTituloSelecionado;
	}

	public List<Exemplar> getExemplares() {
		return exemplares;
	}

	public void setExemplares(List<Exemplar> exemplares) {
		this.exemplares = exemplares;
	}

	public Assinatura getAssinatura() {
		return assinatura;
	}

	public void setAssinatura(Assinatura assinatura) {
		this.assinatura = assinatura;
	}

	public List<Fasciculo> getFasciculos() {
		return fasciculos;
	}

	public void setFasciculos(List<Fasciculo> fasciculos) {
		this.fasciculos = fasciculos;
	}

	public boolean isPeriodico() {
		return periodico;
	}

	public void setPeriodico(boolean periodico) {
		this.periodico = periodico;
	}

	public String getDiaInicial() {
		return diaInicial;
	}

	public void setDiaInicial(String diaInicial) {
		this.diaInicial = diaInicial;
	}

	public String getMesInicial() {
		return mesInicial;
	}

	public void setMesInicial(String mesInicial) {
		this.mesInicial = mesInicial;
	}

	public String getAnoInicial() {
		return anoInicial;
	}

	public void setAnoInicial(String anoInicial) {
		this.anoInicial = anoInicial;
	}

	public String getDiaFinal() {
		return diaFinal;
	}

	public void setDiaFinal(String diaFinal) {
		this.diaFinal = diaFinal;
	}

	public String getMesFinal() {
		return mesFinal;
	}

	public void setMesFinal(String mesFinal) {
		this.mesFinal = mesFinal;
	}

	public String getAnoFinal() {
		return anoFinal;
	}

	public void setAnoFinal(String anoFinal) {
		this.anoFinal = anoFinal;
	}

	public boolean isTouch() {
		return touch;
	}

	public void setTouch(boolean touch) {
		this.touch = touch;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora;
	}
	
	/** 
	 * M�todo que realiza a pagina��o dos resultados da consulta de t�tulos no acervo 
	 * 
	 * N�o chamado por JSP(s).
	 * 
	 * */
	public final void geraResultadosPaginacao(){
		quantidadeTotalResultados = resultadosBuscados.size();
		quantidadePaginas = PaginacaoBibliotecaUtil.calculaQuantidadePaginas(quantidadeTotalResultados, quantideResultadosPorPagina);
		
		resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
	}
	
	/** 
	 *  M�todo que precisa ser implementado para o usu�rio "andar" entre as p�ginas.
	 *  
	 *  Cont�m o comportamente padr�o utilizado nas buscas p�blicas de registrar as estat�sticas da consulta.
	 *  
	 *  Chamado a partir da p�gina: /sigaa.war/mobile/touch/biblioteca/resultadosConsultaTituloMobile.jsp
	 * 
	 */
	public String atualizaResultadosPaginacao() throws DAOException{
		
		//int numeroPaginaAtual = getParameterInt("numero_pagina_atual");
		
		if(paginaAtual > quantidadePaginas)
			paginaAtual = quantidadePaginas;
		
		if(paginaAtual <= 0)
			paginaAtual = 1;
		
		resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
		
		// Registra a consulta dos T�tulos que est�o na p�gina que o usu�rio est� visualizando no momento ///
		RegistraEstatisticasBiblioteca.getInstance().registrarTitulosConsultados(resultadosPaginadosEmMemoria);
		
		return telaResultadosConsultaTituloMobile();
	}
	
	/** 
	 * Retorna a descri��o da p�gina no formato:  '25 a 50 de 1000 ' 
	 * 
	 * Chamado a partir da p�gina: /sigaa.war/mobile/touch/biblioteca/resultadosConsultaTituloMobile.jsp
	 * 
	 * */
	public final String getDescricaoPaginacao(){
		int resultadosDaPagina = paginaAtual * quantideResultadosPorPagina;
		
		if(resultadosDaPagina > quantidadeTotalResultados)
			resultadosDaPagina = quantidadeTotalResultados;
		return (  (paginaAtual-1) * quantideResultadosPorPagina+1)+" a "+resultadosDaPagina+" de "+quantidadeTotalResultados; 	
	}
	
	/**
	 * Limpa apenas os resultados da pesquisa
	 * 
	 * M�todo n�o chamado por JSP(s).
	 */
	public final void limpaResultadoPesquisa(){
		resultadosBuscados.clear();
		resultadosPaginadosEmMemoria.clear();
		
		paginaAtual = 1;
		quantidadeTotalResultados = 0;
		quantidadePaginas = 1;
	}
	
	/**
	 * Retorna a quantidade de artigos mostradas no momento para o usu�rio
	 * 
	 * M�todo n�o chamado por JSP(s).
	 *
	 * @return
	 */
	public int getQuantidadeResultadosMostrados(){
		if( resultadosPaginadosEmMemoria != null)
			return resultadosPaginadosEmMemoria.size();
		else return 0;
	}
	
	public int getQuantideResultadosPorPagina() {
		return quantideResultadosPorPagina;
	}

	public void setQuantideResultadosPorPagina(int quantideResultadosPorPagina) {
		this.quantideResultadosPorPagina = quantideResultadosPorPagina;
	}

	public int getQuantidadeTotalResultados() {
		return quantidadeTotalResultados;
	}

	public void setQuantidadeTotalResultados(int quantidadeTotalResultados) {
		this.quantidadeTotalResultados = quantidadeTotalResultados;
	}

	public int getQuantidadePaginas() {
		return quantidadePaginas;
	}

	public void setQuantidadePaginas(int quantidadePaginas) {
		this.quantidadePaginas = quantidadePaginas;
	}

	public int getPaginaAtual() {
		return paginaAtual;
	}

	public void setPaginaAtual(int paginaAtual) {
		this.paginaAtual = paginaAtual;
	}

	public List<CacheEntidadesMarc> getResultadosPaginadosEmMemoria() {
		return resultadosPaginadosEmMemoria;
	}

	public void setResultadosPaginadosEmMemoria(List<CacheEntidadesMarc> resultadosPaginadosEmMemoria) {
		this.resultadosPaginadosEmMemoria = resultadosPaginadosEmMemoria;
	}

	public List<CacheEntidadesMarc> getResultadosBuscados() {
		return resultadosBuscados;
	}
	
	public void setResultadosBuscados(List<CacheEntidadesMarc> resultadosBuscados) {
		this.resultadosBuscados = resultadosBuscados;
	}
	
	public Collection<SelectItem> getPaginas(){
		Collection<SelectItem> paginas = new ArrayList<SelectItem>();
		
		for (int i=1; i <= quantidadePaginas; i++){
			SelectItem item = new SelectItem();
			item.setValue(i);
			item.setLabel("Pag. " + i);
			paginas.add(item);
		}
		
		return paginas;
	}
}