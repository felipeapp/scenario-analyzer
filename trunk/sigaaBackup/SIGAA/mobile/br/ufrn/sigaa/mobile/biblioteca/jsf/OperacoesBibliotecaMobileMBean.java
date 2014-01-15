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
 *    Bean que controla as operações da parte mobile da biblioteca.
 *
 * @author jadson
 * @since 03/12/2008
 * @version 1.0 criacao da classe
 *
 */
@Component("operacoesBibliotecaMobileMBean")
@Scope("session")
public class OperacoesBibliotecaMobileMBean  extends SigaaAbstractMobileController <UsuarioBiblioteca>{

	/** O menu principal da biblioteca móbile  */
	public static final String PAGINA_MENU_PRINCIPAL_BIBLIOTECA_MOBILE = "/mobile/biblioteca/menu.jsp";
	/** A página onde o usuário visualiza os seus emprétimos renováveis  */
	public static final String PAGINA_VISUALIZA_MEUS_EMPRESTIMOS_RENOVAVEIS_MOBILE = "/mobile/biblioteca/visualizaMeusEmprestimosRenovaveisMobile.jsp";
	/** A página onde o usuário visualiza os seus empréstimos em aberto  */
	public static final String PAGINA_VISUALIZA_MEUS_EMPRESTIMO_ABERTOS_MOBILE = "/mobile/biblioteca/visualizaMeusEmprestimosAbertosMobile.jsp";
	/** A página de filtros mobile onde o usuário visualiza o seu histórico de empréstimos */
	public static final String PAGINA_CONSULTA_MEUS_EMPRESTIMOS_MOBILE = "/mobile/biblioteca/consultaMeusEmprestimosMobile.jsp";
	/** A página que mostra a listagem com o histórico de  empréstimos do usuário */
	public static final String PAGINA_RESUTADOS_CONSULTA_MEUS_EMPRESTIMOS_MOBILE = "/mobile/biblioteca/resultadoConsultaMeusEmprestimosMobile.jsp";
	/** A página de filtros para a consulta no acervo móbile */
	public static final String PAGINA_CONSULTA_TITULO_MOBILE = "/mobile/biblioteca/consultaTituloMobile.jsp";
	/** A página que mostra os resultados na busca do acervo móbile */
	public static final String PAGINA_RESULTADOS_CONSULTA_TITULO_MOBILE = "/mobile/biblioteca/resultadosConsultaTituloMobile.jsp";
	/** A página que visualiza os materiais do título selecionado na busca do acervo móbile  */
	public static final String PAGINA_VISUALIZA_MATERIAS_MOBILE = "/mobile/biblioteca/visualizaMateriasMobile.jsp";
	
	/** As informacoes do titulo dos emprestimos */
	private List <CacheEntidadesMarc> titulosResumidos;
	
	/** Emprestimos que podem ser renovados */
	private List <Emprestimo> emprestimosAtivosRenovaveis;
	
	/** Lista contendo todos os emprestimos ativos */
	private List <Emprestimo> emprestimosAtivos;
		
	/** Consultas dos empréstimos dos usuários até os que já foram devolvidos */
	private List<Emprestimo> emprestimos;
	
	/** as datas são jogada nas seguintes variáveis porque não tem como validar com java script na página */
	private String diaInicial;
	/** as datas são jogada nas seguintes variáveis porque não tem como validar com java script na página */
	private String mesInicial;
	/** as datas são jogada nas seguintes variáveis porque não tem como validar com java script na página */
	private String anoInicial;
	/** as datas são jogada nas seguintes variáveis porque não tem como validar com java script na página */
	private String diaFinal;
	/** as datas são jogada nas seguintes variáveis porque não tem como validar com java script na página */
	private String mesFinal;
	/** as datas são jogada nas seguintes variáveis porque não tem como validar com java script na página */
	private String anoFinal;

	/** A data inicial para consulta o histórico de empréstimos de um usuário */
	private Date dataInicial;
	
	/** A data final para consulta o histórico de empréstimos de um usuário */
	private Date dataFinal;
	
	/** Dados para a consulta de materiais */
	private String titulo, autor, assunto, editora;
	
	/** O título selecionado para ver os materiais */
	private int idTituloSelecionado;
	
	/** Caso o titulo seja de outra coisa que não periódicos */
	private List<Exemplar> exemplares = new ArrayList<Exemplar>();
	
	/** Caso o titulo seja de periodico */
	private Assinatura assinatura;
	/** Caso o titulo seja de periodico. contem os fascículos dele */
	private List<Fasciculo> fasciculos = new ArrayList<Fasciculo>();
	
	/**
	 * Indica que o título selecionado é de um periódico.
	 */
	private boolean periodico = false;
	
	/** Os ids das bibliotecas que possuem acervo público no sistema. O resultado da busca pública só deve buscar nessas biblioteca. */
	protected List<Integer> idsBibliotecasAcervoPublico;
	
	/** Utilizado para gerar a buscas nativas no banco*/
	private GeraPesquisaTextual geradorPesquisa ;
	
	/** Indica se o usuário acessou via /mobile/toush */
	private boolean touch = false;
	
	
	/* Variáveis necessárias para a paginação da Consulta de Acervo */
	
	/** Guarda a quantidade de resultados por página que o usuário deseja ver na consulta*/
	protected int quantideResultadosPorPagina = 25;
	
	/** A quantidade total de resultados que serão páginados */
	protected int quantidadeTotalResultados = 0;
	
	/** A quantidade total de páginas da páginação */
	protected int quantidadePaginas = 1;
	
	/** A página atual que está sendo mostrada ao usuário*/
	protected int paginaAtual;
	
	/**  
	 * Os resultados da consulta, que serão páginados para visualização do usuário.  
	 */
	protected List<CacheEntidadesMarc> resultadosBuscados = new ArrayList<CacheEntidadesMarc>();
	
	/**  Guarda os artigos que serão visualizados pelos usuários no momento
	 *   Utilizado para diminuir a quantidade de resultados exibidos ao usuário.
	 *   A paginação ocorre em memória porque as consultas geralmente são rápidas, o que demora mais é redenrizar uma quantidade muito grande para o usuário.  
	 */
	protected List<CacheEntidadesMarc> resultadosPaginadosEmMemoria = new ArrayList<CacheEntidadesMarc>();
	
	/**
	 * Redirenciona o usuário para a página principal da bibliotecam mobile.
	 *
     * Chamado a partir da página: /sigaa.war/mobile/menu.jsp
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
	 * Redirenciona o usuário para o menu principal do módulo mobile.
	 *
     * Chamado a partir da página: /sigaa.war/mobile/biblioteca/menu.jsp
     * 
	 * @return
	 * @throws ArqException
	 * @throws DAOException
	 */
	public String voltarMenuPrincipal() throws ArqException{
		return forward("/mobile/menu.jsp");
	}
	
	/**
	 * Inicia o caso de uso para renovar os empréstimos do usuário logado.
	 *
     * Chamado a partir da página: /sigaa.war/mobile/biblioteca/menu.jsp
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
	 *   Caso de uso no qual o usuário visualiza todos os empréstimos dele, mesmo os que já tenham
	 *   sido renovados
	 *
	 *	Chamado a partir da página: /sigaa.war/mobile/biblioteca/menu.jsp
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
	 * Consulta os último empréstimos do usuário. Inclusive os que já foram devolvidos.
	 *
	 * Chamado a partir da página: /sigaa.war/mobile/biblioteca/menu.jsp
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
	 *   Encaminha para a página onde o usuário entra com os critérios de busca.
	 *
	 * Chamado a partir da página: /sigaa.war/mobile/biblioteca/menu.jsp
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
	 * Chama o processador para renovar os emprestimos selecionados pelo usuário
	 *
	 * Chamado a partir da página: /sigaa.war/mobile/biblioteca/mostraEmprestimosRenovaveisMobile.jsp
	 * @return
	 */
	public String renovarEmprestimos(){
		
		
		/* passa as posicoes dos elementos que foram selecionados como parametro na requisicao
		 * dos ckeckbox html comuns (nao quero usar checkbox do jsf porque aqui eh mobile). */
		String[] posicoes = getCurrentRequest().getParameterValues("emprestimosSelecionados");
		
		if(posicoes == null || posicoes.length <= 0 || emprestimosAtivosRenovaveis == null || emprestimosAtivosRenovaveis.size() <= 0 ){
			addMensagemErro("Nenhum empréstimo foi selecionado para renovação.");
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
	 * Chamado a partir da página: /sigaa.war/mobile/biblioteca/mostraEmprestimosRenovaveisMobile.jsp
	 * @return
	 */
	public String renovarTodos(){
		
		List <Emprestimo> emprestimosSelecionados = new ArrayList <Emprestimo> ();
		
		emprestimosSelecionados.addAll(emprestimosAtivosRenovaveis);

		return renovar(emprestimosSelecionados);
	}
	

	/**
	 * Responsável por realizar renovações de empréstimos de livros.
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
	 *   Consulta todos os empréstimo que não foram estornados do usuário no período passado.
	 * 
	 * </br>Chamado a partir da página: /sigaa.war/mobile/biblioteca/consultaMeusEmprestimosMobile.jsp
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
				
				addMensagemErro("Data Inválida");
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
				addMensagemErro("Data Inválida");
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
				addMensagemWarning("O usuário não realizou empréstimos da data informada.");
		
			
			
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
	 * Realiza a consulta de títulos do sistema mobile e redireciona para a página de resultados.
	 *
	 * Chamado a partir da página: sigaa.war/biblioteca/mobile/biblioteca/consultaTituloMobile.jsp
	 * @return
	 */
	public String consultarTitulos(){
		
		TituloCatalograficoDao dao = getDAO(TituloCatalograficoDao.class);
		
		if(StringUtils.isEmpty(titulo) && StringUtils.isEmpty(autor) && StringUtils.isEmpty(assunto) && StringUtils.isEmpty(editora)){
			addMensagemErro("Informe um critério de busca.");
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
				addMensagemErro("Não foram encontrados títulos com os critérios informados.");
			}
			
			if(!touch && TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_MOBILE.compareTo( titulosResumidos.size() ) <= 0){
				addMensagemWarning("A busca resultou em um número muito grande de resultados. Somente os "+TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_MOBILE+" primeiros estão sendo mostrados.");
			}
			
			if (touch && getQuantidadeTotalResultados() == 0) {
				addMensagemErro("Não foram encontrados títulos com os critérios informados.");
			}
			
			if (touch && TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_PUBLICA.compareTo(getQuantidadeTotalResultados()) <= 0){
				addMensagemWarning("A busca resultou em um número muito grande de resultados. Somente os "+TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_PUBLICA+" primeiros estão sendo mostrados.");
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
	 *   Carrega as informações dos materiais do título, sejam eles exemplares ou fascículos e
	 *   mostram para o usuário na página espefícica.
	 *
	 * Chamado a partir da página: /sigaa.war/mobile/biblioteca/resultadosConsultaTituloMobile.jsp
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
	 * Carrega os empréstimos ativos do usuário que podem ser renovados
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
				addMensagemWarning("Usuário não possui empréstimos que podem ser renovados");
		
			
			
		}catch(NegocioException ne){
			ne.printStackTrace();
			addMensagemErro(ne.getMessage());
		}catch(DAOException daoEx){
			// Tem que tratar todos os erros senao vai pra a tela de erro
			// TODO implementar outra pagina de erro padrão para o sistema mobile
			daoEx.printStackTrace();
			addMensagemErroPadrao();
		}finally{
			if(dao != null) dao.close();
			if(emprestimoDAO != null) emprestimoDAO.close();
		}
	}
	
	
	
	/**
	 * Carrega os empréstimos ativos do usuário que podem ser renovados
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
				addMensagemWarning("Usuário não possui empréstimos ativos ");
			
			
		}catch(NegocioException ne){
			ne.printStackTrace();
			addMensagemErro(ne.getMessage());
		}catch(DAOException daoEx){
			// Tem que tratar todos os erros senão vai pra a tela de erro do sistema web
			// TODO implementar outra página de erro padrão para o sistema mobile
			daoEx.printStackTrace();
			addMensagemErroPadrao();
		}finally{
			if(dao != null) dao.close();
			if(emprestimoDAO != null) emprestimoDAO.close();
		}
	}
	
	
	
	/**
	 *   Retorna os ids das bibliotecas que possuem acervo público. (liberado para consulta a usuários que não trabalham nas bibliotecas)
	 *
	 *   Método não chamado por nenhuma página jsp.
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
	 * carega os relacionamentos lazy dos fascículos
	 */
	private void carregaDadosFasciculos(GenericDAO dao, Fasciculo fasciculo) throws DAOException{
		dao.refresh(fasciculo.getSituacao());
		dao.refresh(fasciculo.getStatus());
		dao.refresh(fasciculo.getColecao());
		dao.refresh(fasciculo.getBiblioteca());
		dao.refresh(fasciculo.getTipoMaterial());
	}
	
	
	////////página de navegação /////////
	
	/**
	 * Redireciona para a página onde o usuário visualiza os empréstimos renováveis
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaVisualizaMeusEmpretimosRenovaveisMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/visualizaMeusEmprestimosRenovaveisMobile.jsp" : PAGINA_VISUALIZA_MEUS_EMPRESTIMOS_RENOVAVEIS_MOBILE);
	}
	
	/**
	 * Redireciona para a página onde o usuário visualiza os empréstimos em aberto da parte móbile.
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaVisualizaEmprestimosAbertosMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/visualizaMeusEmprestimosAbertosMobile.jsp" : PAGINA_VISUALIZA_MEUS_EMPRESTIMO_ABERTOS_MOBILE);
	}
	
	/**
	 * Redireciona para a página de filtro onde o usuário visualiza o seu histórico de empréstimos
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaConsultaMeusEmprestimosMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/consultaMeusEmprestimosMobile.jsp" : PAGINA_CONSULTA_MEUS_EMPRESTIMOS_MOBILE);
	}
	
	/**
	 * Redireciona para a página onde o usuário visualiza o seu histórico de empréstimos
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaResultadosConsultaMeusEmprestimosMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/resultadoConsultaMeusEmprestimosMobile.jsp" : PAGINA_RESUTADOS_CONSULTA_MEUS_EMPRESTIMOS_MOBILE);
	}
	
	/**
	 * Redireciona para a página onde o usuário consulta o acervo na parte mobile
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaConsultaTituloMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/consultaTituloMobile.jsp" : PAGINA_CONSULTA_TITULO_MOBILE);
	}
	
	/**
	 * Redireciona para a página onde o usuário visualiza os títulos da busca do acervo mobile
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaResultadosConsultaTituloMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/resultadosConsultaTituloMobile.jsp" : PAGINA_RESULTADOS_CONSULTA_TITULO_MOBILE);
	}
	
	/**
	 * Redireciona para a página onde o usuário visualiza os materiais de um título
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaVisualizaMateriaisMobile(){
		return forward(touch ? "/mobile/touch/biblioteca/visualizaMateriasMobile.jsp" : PAGINA_VISUALIZA_MATERIAS_MOBILE);
	}
	
	/**
	 * Redireciona para a página onde o usuário visualiza  o menu principal da biblioteca.
	 * 
	 *  <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
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
	 * Método que realiza a paginação dos resultados da consulta de títulos no acervo 
	 * 
	 * Não chamado por JSP(s).
	 * 
	 * */
	public final void geraResultadosPaginacao(){
		quantidadeTotalResultados = resultadosBuscados.size();
		quantidadePaginas = PaginacaoBibliotecaUtil.calculaQuantidadePaginas(quantidadeTotalResultados, quantideResultadosPorPagina);
		
		resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
	}
	
	/** 
	 *  Método que precisa ser implementado para o usuário "andar" entre as páginas.
	 *  
	 *  Contém o comportamente padrão utilizado nas buscas públicas de registrar as estatísticas da consulta.
	 *  
	 *  Chamado a partir da página: /sigaa.war/mobile/touch/biblioteca/resultadosConsultaTituloMobile.jsp
	 * 
	 */
	public String atualizaResultadosPaginacao() throws DAOException{
		
		//int numeroPaginaAtual = getParameterInt("numero_pagina_atual");
		
		if(paginaAtual > quantidadePaginas)
			paginaAtual = quantidadePaginas;
		
		if(paginaAtual <= 0)
			paginaAtual = 1;
		
		resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
		
		// Registra a consulta dos Títulos que estão na página que o usuário está visualizando no momento ///
		RegistraEstatisticasBiblioteca.getInstance().registrarTitulosConsultados(resultadosPaginadosEmMemoria);
		
		return telaResultadosConsultaTituloMobile();
	}
	
	/** 
	 * Retorna a descrição da página no formato:  '25 a 50 de 1000 ' 
	 * 
	 * Chamado a partir da página: /sigaa.war/mobile/touch/biblioteca/resultadosConsultaTituloMobile.jsp
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
	 * Método não chamado por JSP(s).
	 */
	public final void limpaResultadoPesquisa(){
		resultadosBuscados.clear();
		resultadosPaginadosEmMemoria.clear();
		
		paginaAtual = 1;
		quantidadeTotalResultados = 0;
		quantidadePaginas = 1;
	}
	
	/**
	 * Retorna a quantidade de artigos mostradas no momento para o usuário
	 * 
	 * Método não chamado por JSP(s).
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