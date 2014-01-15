/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 03/11/2008
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ArtigoDePeriodicoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Colecao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoPesquisaAvancada;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoPesquisaAvancada.TipoCampoBuscaAvancada;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.GeraPesquisaTextualFactory;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.RegistraEstatisticasBiblioteca;

/**
 *
 *     MBean que controla a parte de pesquisas p�blica da biblioteca.
 *     <ul>
 *     		<li>A pesquisa p�blica no acervo  de T�tulos.</li>
 *     		<li>A pesquisa de novas aquisi��es no SIPAC.</li>
 *     		<li>A pesquisa p�blica no acervo de Artigos de Peri�dicos.</li>
 *     </ul>
 *
 * @author jadson
 * @since 03/11/2008
 * @version 1.0 cria��o da classe
 *
 */
@Component(value="pesquisaPublicaBibliotecaMBean")
@Scope(value="request")
public class PesquisaPublicaBibliotecaMBean extends PesquisarAcervoPaginadoBiblioteca {

	/**
	 * A p�gina de busca p�blica no acervo
	 */
	public static final String PAGINA_BUSCA_PUBLICA = "/public/biblioteca/buscaPublicaAcervo.jsp";
	
	
	
	/** Limita da busca de novas aquisi��es.*/
	private static final Integer LIMITE_AQUISICAO_LISTADA = 200;
	
	
	/**
	 *  usado para gerar consultas nativas do banco
	 */
	private GeraPesquisaTextual geradorPesquisa;
	
	/** Os campos da busca simples de um t�tulo no acervo   */
	protected String titulo, assunto, autor, localPublicacao, editora;
	/** Os campos da busca simples de um t�tulo no acervo   */
	protected Integer anoInicial, anoFinal;
	
	/** Filtros sobre os materiais dos T�tulos */
	protected Integer idBiblioteca;
	/** Filtros sobre os materiais dos T�tulos */
	protected Integer idColecao;
	/** Filtros sobre os materiais dos T�tulos */
	protected Integer idTipoMaterial;
	
	
	/** Guarda os dados do combobox para n�o ficar buscando toda vida que renderizar a p�gina */
	protected Collection<Biblioteca> bibliotecasAtivas;
	/** Guarda os dados do combobox para n�o ficar buscando toda vida que renderizar a p�gina */
	protected Collection<Colecao> colecoes;
	/** Guarda os dados do combobox para n�o ficar buscando toda vida que renderizar a p�gina */
	protected Collection<TipoMaterial> tiposMateriais;
	
	
	
	/** INDICA POR QUAIS CAMPOS DEVE-SE BUSCAR  */
	protected boolean buscarTitulo, buscarAssunto, buscarAutor, buscarLocalPublicacao
		, buscarEditora, buscarAno, buscarPalavrasChaves, buscarBiblioteca, buscarColecao, buscarTipoMaterial, buscarAssuntoAutorizado, buscarAutorAutorizado;
	
	
	/**  Resultados   da   consulta  no acervo de artigos */
	protected List<CacheEntidadesMarc> artigos = new ArrayList<CacheEntidadesMarc>();
	
	
	/** Guarda o resultado da pesquisa de novas aquisi��es do sistema */
	protected Collection<Object[]> aquisicaoSIPAC;
	
	/** Guarda se o usu�rio escolheu fazer a busca simples. */
	protected boolean buscaSimples = false;
	
	/** Guarda se o usu�rio escolheu fazer a busca multi campo. � a busca padr�o do sistema. */
	protected boolean buscaMultiCampo = true;
	
	/** Guarda se o usu�rio escolheu fazer a busca avan�ada. */
	protected boolean buscaAvancada = false;
	
	/** Guarda se o usu�rio escolheu fazer a busca de autoridades. */
	protected boolean buscaAutoridadesSimples = false;
	
	
	////////////////////////DADOS DA BUSCA SIMPLES    ////////////////////////
	
	/** Os campos de busca simples */
	protected CampoPesquisaAvancada campoBuscaSimples;
	
	///////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////DADOS DA BUSCA AVANCADA    ////////////////////////
	
	/** Os campos de busca da tela */
	protected List<CampoPesquisaAvancada> campos = new ArrayList<CampoPesquisaAvancada>();
	
	///////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////DADOS DA BUSCA AUTORIDADES    ////////////////////////
	
	/** O autor autorizado digitado pelo usu�rio para ser recuperado */
	protected String autorAutorizado;
	
	/** O assunto autorizado digitado pelo usu�rio para ser recuperado */
	protected String assuntoAutorizado;
	
	///////////////////////////////////////////////////////////////////////////
	
	
	/** Os ids das bibliotecas que possuem acervo p�blico no sistema. O resultado da busca p�blica s� deve buscar nessas biblioteca. */
	protected List<Integer> idsBibliotecasAcervoPublico;
	
	
	/**
	 * Construtor padr�o
	 */
	public PesquisaPublicaBibliotecaMBean(){
		
		geradorPesquisa = new GeraPesquisaTextualFactory().getGeradorPesquisaTextual();
		
		valorCampoOrdenacao = CampoOrdenacaoConsultaAcervo.TITULO.getValor();
		
		// A busca avan�ada sempre come�a com tr�s campos
		campos.add(new CampoPesquisaAvancada(campos.size(), true));
		campos.add(new CampoPesquisaAvancada(campos.size(), true));
		campos.add(new CampoPesquisaAvancada(campos.size(), true));
		
		campoBuscaSimples = new CampoPesquisaAvancada(0, "", CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.TODOS_OS_CAMPOS);
		
	}
	
	
	/**
	 *   <p>Altera a forma do usu�rio digitar os dados na busca avan�ada para o campo 1 quando o usu�rio escolhe
	 *   biblioteca, cole��o ou tipo material.</p>
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp
	 */
	public void verificaExibicaoTipoCampo(ValueChangeEvent evt) {
		
		Integer valorEscolhido = (Integer) evt.getNewValue();

		int posicaoCampoSelecionado = (Integer) evt.getComponent().getAttributes().get("campoSelecionado");
		
		CampoPesquisaAvancada campoSelecionado = campos.get(posicaoCampoSelecionado);
		
		campoSelecionado.setTipoCampo(TipoCampoBuscaAvancada.getTipoCampoBuscaAvancada(valorEscolhido));
		
		campoSelecionado.verificaExibicaoTipoCampo();
		
	}
	
	
	/**
	 * <p>M�todo que realiza a pesquisa simples das informa��es dos t�tulos do sistema.</p>
	 * 
	 * <p>A pesquisa simples o usu�rio digita um termo e o sistema busca por todos os campos, para isso faz usado da pesquisa avan�ada todos os campos.</p>
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/public/biblioteca/ buscaPublicaSimplesAcervo.jsp</li>
	 *   </ul>
	 */
	public String pesquisaSimplesAcervo() throws DAOException{

		String titulo = null;
		String autor = null;
		String assunto = null;
		
		TituloCatalograficoDao daoTitulo = null;
		ArtigoDePeriodicoDao artigoDao = null;
		
		limparResultadosPesquisa();
		
		try {
			
			daoTitulo = getDAO(TituloCatalograficoDao.class);
			artigoDao = getDAO(ArtigoDePeriodicoDao.class);	
				
			if(! campoBuscaSimples.isBuscarCampo()){
				campoBuscaSimples.setValorCampo(null);
			}else{ // se o usu�rio selecionou o campo
				if(! campoBuscaSimples.contemInformacoes()) {
					campoBuscaSimples.setBuscarCampo(false);
				}else {
					titulo = campoBuscaSimples.getValorCampo();
					autor = campoBuscaSimples.getValorCampo();
					assunto = campoBuscaSimples.getValorCampo();
				}
			}
			
			List<CampoPesquisaAvancada> camposPesquisaSimples = new ArrayList<CampoPesquisaAvancada>();
			camposPesquisaSimples.add(campoBuscaSimples);
			
			Integer idBibliotecaBusca = idBiblioteca;
			Integer idColecaoBusca = idColecao;
			Integer idTipoMaterialBusca = idTipoMaterial;
			
			if(! buscarBiblioteca) idBibliotecaBusca = -1;
			if(! buscarColecao) idColecaoBusca = -1;
			if(! buscarTipoMaterial) idTipoMaterialBusca = -1;
			
			if(idBibliotecaBusca > 0){ // Se o usu�rio selecionou filtar por biblioteca 
				CampoPesquisaAvancada campoBuscaSimplesBiblioteca = new CampoPesquisaAvancada(1, String.valueOf(idBibliotecaBusca), CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.BIBLIOTECA);
				camposPesquisaSimples.add(campoBuscaSimplesBiblioteca);
			}
			
			if(idColecaoBusca > 0){ // Se o usu�rio selecionou filtar por cole��o 
				CampoPesquisaAvancada campoBuscaSimplesColecao = new CampoPesquisaAvancada(2, String.valueOf(idColecaoBusca), CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.COLECAO);
				camposPesquisaSimples.add(campoBuscaSimplesColecao);
			}

			if(idTipoMaterialBusca > 0){ // Se o usu�rio selecionou filtar por tipo de material 
				CampoPesquisaAvancada campoBuscaSimplesTipoMaterial = new CampoPesquisaAvancada(3, String.valueOf(idTipoMaterialBusca), CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.TIPO_MATERIAL);
				camposPesquisaSimples.add(campoBuscaSimplesTipoMaterial);
			}
			
			CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
			
			resultadosBuscados = daoTitulo.buscaAvancadaPublica(geradorPesquisa, campoOrdenacao, camposPesquisaSimples, getIdsBibliotecasAcervoPublico());
			
			geraResultadosPaginacao();
			
			///////// REALIZA TAMB�M A BUSCA DE ARTIGOS /////
			if (StringUtils.notEmpty(titulo) || StringUtils.notEmpty(autor) || StringUtils.notEmpty(assunto)) {
				artigos = artigoDao.buscaSimplesArtigo(geradorPesquisa, CampoOrdenacaoConsultaAcervo.isCampoOrdenacaoArtigo(campoOrdenacao) ? campoOrdenacao : null, titulo, autor, assunto, true, null);
			}
			
			verificaLimitesDaBusca();
			
			// Registra a consulta dos T�tulos que est�o na p�gina que o usu�rio est� visualizando no momento ///
			RegistraEstatisticasBiblioteca.getInstance().registrarTitulosConsultados(resultadosPaginadosEmMemoria);
			
		}finally{
			if(daoTitulo != null) daoTitulo.close();
			if(artigoDao != null) artigoDao.close();
		}
		
		return telaBuscaPublicaAcervo();
		
	}
	
	
	
	
	/**
	 *    M�todo que realiza a busca no acervo. � o mesmo m�todo usado pelos bibliotec�rios dentro
	 * do sistema, s� que est� sendo chamado da  p�gina de pesquisa p�blica. Aqui s� T�tulos
	 * que possuem materiais s�o mostrados.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/public/biblioteca/buscaPublicaMultiCampoAcervo.jsp</li>
	 *   </ul>
	 */
	public String pesquisaMultiCampoAcervo() throws DAOException{
		
		limparResultadosPesquisa();
		
		TituloCatalograficoDao daoTitulo = null;
		ArtigoDePeriodicoDao daoArtigo = null;
		
		String tituloBusca = titulo;
		String assuntoBusca = assunto;
		String autorBusca = autor;
		String localPublicacaoBusca = localPublicacao;
		String editoraBusca = editora;
		Integer anoInicialBusca = anoInicial;
		Integer anoFinalBusca = anoFinal;
		
		Integer idBibliotecaBusca = idBiblioteca;
		Integer idColecaoBusca = idColecao;
		Integer idTipoMaterialBusca = idTipoMaterial;
		
		
		if(StringUtils.isEmpty(titulo) || titulo.length() <= 2) buscarTitulo = false;
		if(StringUtils.isEmpty(assunto) || assunto.length() <= 2) buscarAssunto = false;
		if(StringUtils.isEmpty(autor) || autor.length() <= 2) buscarAutor = false;
		if(StringUtils.isEmpty(localPublicacao) || localPublicacao.length() <= 2) buscarLocalPublicacao = false;
		if(StringUtils.isEmpty(editora) || editora.length() <= 2) buscarEditora = false;
		if(anoInicialBusca == null && anoFinalBusca == null) buscarAno = false;
		if(new Integer(-1).equals(idBiblioteca)) buscarBiblioteca = false;
		if(new Integer(-1).equals(idColecao)) buscarColecao = false;
		if(new Integer(-1).equals(idTipoMaterial)) buscarTipoMaterial = false;
		
		
		if(! buscarTitulo) tituloBusca = null;
		if(! buscarAssunto) assuntoBusca = null;
		if(! buscarAutor) autorBusca = null;
		if(! buscarLocalPublicacao) localPublicacaoBusca = null;
		if(! buscarEditora) editoraBusca = null;
		if(! buscarAno) { anoInicialBusca = null; anoFinalBusca = null; }
		
		if(! buscarBiblioteca) idBibliotecaBusca = -1;
		if(! buscarColecao) idColecaoBusca = -1;
		if(! buscarTipoMaterial) idTipoMaterialBusca = -1;
		
		try{
		
			daoTitulo = getDAO(TituloCatalograficoDao.class);
			daoArtigo = getDAO(ArtigoDePeriodicoDao.class);
			
			CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
			
			resultadosBuscados = daoTitulo.buscaMultiCampoPublica(geradorPesquisa, campoOrdenacao, tituloBusca, assuntoBusca, autorBusca,
					localPublicacaoBusca, editoraBusca, anoInicialBusca, anoFinalBusca, idBibliotecaBusca, idColecaoBusca, idTipoMaterialBusca, false, getIdsBibliotecasAcervoPublico());
			
			geraResultadosPaginacao();
			
			//////////////REALIZA TAMB�M A BUSCA DE ARTIGOS //////////////
			if ( StringUtils.notEmpty(tituloBusca) || StringUtils.notEmpty(autorBusca) || StringUtils.notEmpty(assuntoBusca)  ){
				
				artigos = daoArtigo.findAllArtigosResumidosAtivosByTituloAutorPalarvaChave(geradorPesquisa, CampoOrdenacaoConsultaAcervo.isCampoOrdenacaoArtigo(campoOrdenacao) ? campoOrdenacao : null,
						tituloBusca, autorBusca, assuntoBusca, true, getIdsBibliotecasAcervoPublico());
		
			}
			
			
			
			verificaLimitesDaBusca();
		
			// Registra a consulta dos T�tulos que est�o na p�gina que o usu�rio est� visualizando no momento ///
			RegistraEstatisticasBiblioteca.getInstance().registrarTitulosConsultados(resultadosPaginadosEmMemoria);
			
		}finally{
			
			if(daoTitulo != null) daoTitulo.close();
			if(daoArtigo != null) daoArtigo.close();
		}
		
		
		return telaBuscaPublicaAcervo();
		
	}
	
	
	
	/**
	 * 
	 * M�todo que realiza a pesquisa avan�ada das informa��es dos t�tulos do sistema.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/public/biblioteca/ buscaPublicaAvancadaAcervo.jsp</li>
	 *   </ul>
	 */
	public String pesquisaAvancadaAcervo() throws DAOException{

		String titulo = null;
		String autor = null;
		String assunto = null;
		
		TituloCatalograficoDao daoTitulo = null;
		ArtigoDePeriodicoDao artigoDao = null;
		
		limparResultadosPesquisa();
		
		try {
			
			daoTitulo = getDAO(TituloCatalograficoDao.class);
			artigoDao = getDAO(ArtigoDePeriodicoDao.class);	
			
			for (CampoPesquisaAvancada campo : campos) {
				
				if(! campo.isBuscarCampo()){
					if(campo.isCampoBuscaInformacoesMateriais())
						campo.setValorCampo("-1");
					else
						campo.setValorCampo(null);
					
				}else{ // se o usu�rio selecionou o campo
					
					if(! campo.contemInformacoes()) {
						campo.setBuscarCampo(false);
					}
					else {
						if (campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.TODOS_OS_CAMPOS) {
							titulo = campo.getValorCampo();
							autor = campo.getValorCampo();
							assunto = campo.getValorCampo();
						}
						else {
							if (campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.TITULO) {
								titulo = campo.getValorCampo();
							}
							else if (campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.AUTOR) {
								autor = campo.getValorCampo();
							}
							else if (campo.getTipoCampo() == CampoPesquisaAvancada.TipoCampoBuscaAvancada.ASSUNTO) {
								assunto = campo.getValorCampo();
							}
						}
					}
				}
				
				if(campo.fazendoOuLogicoComInformacoesMateriais()){
					addMensagemErro("N�o � permitido realizar a opera��o \"OU\" com informa��es dos materiais do acervo, pois a busca pode trazer uma quantidade muito grande de resultados.");
					return telaBuscaPublicaAcervo();
				}
				 
			}
			
			CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
			
			resultadosBuscados = daoTitulo.buscaAvancadaPublica(geradorPesquisa, campoOrdenacao, campos, getIdsBibliotecasAcervoPublico());
			
			geraResultadosPaginacao();
			
			///////// REALIZA TAMB�M A BUSCA DE ARTIGOS /////
			if (StringUtils.notEmpty(titulo) || StringUtils.notEmpty(autor) || StringUtils.notEmpty(assunto)) {
				artigos = artigoDao.findAllArtigosResumidosAtivosByTituloAutorPalarvaChave(geradorPesquisa, CampoOrdenacaoConsultaAcervo.isCampoOrdenacaoArtigo(campoOrdenacao) ? campoOrdenacao : null, titulo, autor, assunto, true, null);
			}
			
			verificaLimitesDaBusca();
			
			// Registra a consulta dos T�tulos que est�o na p�gina que o usu�rio est� visualizando no momento ///
			RegistraEstatisticasBiblioteca.getInstance().registrarTitulosConsultados(resultadosPaginadosEmMemoria);
			
		}finally{
			if(daoTitulo != null) daoTitulo.close();
			if(artigoDao != null) artigoDao.close();
		}
		
		return telaBuscaPublicaAcervo();
		
	}
	
	
	
	/**
	 *    M�todo que realiza a busca no acervo. � o mesmo m�todo usado pelos bibliotec�rios dentro
	 * do sistema, s� que est� sendo chamado da  p�gina de pesquisa p�blica. Aqui s� T�tulos
	 * que possuem materiais s�o mostrados.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/public/biblioteca/buscaPublicaSimplesAutoridades.jsp</li>
	 *   </ul>
	 */
	public String pesquisaSimplesAcervoAutoridades() throws DAOException{
		
		limparResultadosPesquisa();
		
		AutoridadeDao daoAutoridade = null;
		
		String autorBusca = autorAutorizado;
		String assuntoBusca = assuntoAutorizado;
		
		if(  "buscarAutor".equalsIgnoreCase(getParameter("radioCampoAutoridades"))  ){
			buscarAutorAutorizado = true;
		}else{
			buscarAutorAutorizado = false;
		}
		
		if(  "buscarAssunto".equalsIgnoreCase(getParameter("radioCampoAutoridades"))  ){
			buscarAssuntoAutorizado = true;
		}else{
			buscarAssuntoAutorizado = false;
		}
		
		if(StringUtils.isEmpty(autorAutorizado) || autorAutorizado.length() <= 2) buscarAutorAutorizado = false;
		if(StringUtils.isEmpty(assuntoAutorizado) || assuntoAutorizado.length() <= 2) buscarAssuntoAutorizado = false;
		
				
		if(! buscarAutorAutorizado) autorBusca = null;
		if(! buscarAssuntoAutorizado) assuntoBusca = null;
		
		
		try{
		
			daoAutoridade = getDAO(AutoridadeDao.class);
			
			CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
			
			if(buscarAutorAutorizado)
				resultadosBuscados = daoAutoridade.buscaSimplesAutorAcervoAutoridades(geradorPesquisa, campoOrdenacao, autorBusca);
			else{
				if(buscarAssuntoAutorizado)
					resultadosBuscados = daoAutoridade.buscaSimplesAssuntoAcervoAutoridades(geradorPesquisa, campoOrdenacao, assuntoBusca);
			}
			
			geraResultadosPaginacao();
			
			verificaLimitesDaBusca();
		
			
		}finally{
			
			if(daoAutoridade != null) daoAutoridade.close();
		}
		
		
		return telaBuscaPublicaAcervo();
		
	}
	
	
	

	/** verifica se os limits das buscas foram alcan�ados para mostrar a mensagem ao usu�ria */
	private void verificaLimitesDaBusca(){
		
		if ( quantidadeTotalResultados == 0 && artigos.size() == 0)
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		if( TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_PUBLICA.compareTo( quantidadeTotalResultados ) <= 0){
			addMensagemWarning("A busca resultou em um n�mero muito grande de resultados, somente os "+TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_PUBLICA+" primeiros est�o sendo mostrados.");
		}
		
		if (  ArtigoDePeriodicoDao.LIMITE_BUSCA_ARTIGOS.compareTo( artigos.size() ) <= 0) {
			addMensagemWarning("A busca de artigos resultou em um n�mero muito grande de resultados, somente os "+ArtigoDePeriodicoDao.LIMITE_BUSCA_ARTIGOS+" primeiros est�o sendo mostrados.");
		}
	}
	
	
	
	
	
	/**
	 * 
	 * <p> Gera o resultado da pesquisa em um arquivo de refer�ncia da ABNT.</p>
	 * <p> <i>Esse arquivo nada mais � do que um arquivo de texto com os formato de refer�ncia as obras buscadas
	 * um abaixo do outro. </i>
	 * </p>
	 * <p><strong>Observa��o: M�todo tamb�m chamado das pesquisas p�blicas do sistema.</strong></p>
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublica.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 *
	 */
	public void gerarResultadoPesquisaArquivoReferenciaFormatoABNT( ActionEvent evt) throws IOException, DAOException{
		
		if(resultadosPaginadosEmMemoria.size() > 0){
			new GeradorArquivoDeReferenciaABNT().gerarArquivoContextoAtualFaces(resultadosBuscados);
		}else{
			addMensagemErro("A consulta n�o possui resultados para gerar o arquivo de refer�ncia da ABNT");
		}
		
	}
	
	
	
	
	
	/**
	 *   Limpar os resultados da busca de no acervo.
	 * 
	 * <br/><br/>
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp</li>
	 *     <li>/sigaa.war/public/biblioteca/ buscaPublicaSimplesAcervo.jsp</li>
	 *     <li>/sigaa.war/public/biblioteca/buscaPublicaMultiCampoAcervo.jsp</li>
	 *     <li>/sigaa.war/public/biblioteca/ buscaPublicaAvancadaAcervo.jsp</li>
	 *     <li>/sigaa.war/public/biblioteca/buscaPublicaSimplesAutoridades.jsp</li>
	 *   </ul>
	 */
	public String limparResultadosBuscaAcervo(){
	
		
		limparFiltrosMaterial();
		
		limparDadosFormularioBusca();
		
		limparResultadosPesquisa();
		
		return telaBuscaPublicaAcervo();
		
	}
	
	
	
	/**
	 *  Limpa o dados do formul�riod e busca digitados pelo usu�rio.
	 */
	private void limparDadosFormularioBusca(){
		titulo = null;
		assunto = null;
		autor = null;
		localPublicacao = null;
		editora = null;
		anoInicial = null;
		anoFinal = null;
		autorAutorizado = null;
		assuntoAutorizado = null;
		
		buscarTitulo = false;
		buscarAssunto = false;
		buscarAutor = false;
		buscarLocalPublicacao = false;
		buscarEditora = false;
		buscarAno = false;
		
		buscarBiblioteca = false;
		buscarColecao = false;
		buscarTipoMaterial = false;
		
		buscarAutorAutorizado = true;
		buscarAssuntoAutorizado = false;
		
		
		campos = new ArrayList<CampoPesquisaAvancada>();
		campos.add(new CampoPesquisaAvancada(campos.size(), true));
		campos.add(new CampoPesquisaAvancada(campos.size(), true));
		campos.add(new CampoPesquisaAvancada(campos.size(), true));
	
		campoBuscaSimples = new CampoPesquisaAvancada(0, "", CampoPesquisaAvancada.E, TipoCampoBuscaAvancada.TODOS_OS_CAMPOS);
		
	}
	
	/**
	 *  Limpa dos dados da consulta
	 */
	private void limparFiltrosMaterial(){
		buscarColecao = false;
		buscarBiblioteca = false;
		buscarTipoMaterial = false;
		
		idBiblioteca = -1;
		idColecao = -1;
		idTipoMaterial = -1;
		
	}
	
	
	/**
	 *  Limpa a lista de os resultados da pesquisa
	 */
	private void limparResultadosPesquisa(){
		paginaAtual = 1;
		quantidadeTotalResultados = 0;
		quantidadePaginas = 1;
		
		artigos = new ArrayList<CacheEntidadesMarc>();
		resultadosBuscados = new ArrayList<CacheEntidadesMarc>();
		resultadosPaginadosEmMemoria = new ArrayList<CacheEntidadesMarc>();
	}
	
	
	/**
	 * Realiza a a��o de cancelar a busca p�blica da biblioteca retornando para o menu principal da parte p�blica do sistema.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp</li>
	 *     <li>/sigaa.war/public/biblioteca/ buscaPublicaSimplesAcervo.jsp</li>
	 *     <li>/sigaa.war/public/biblioteca/buscaPublicaMultiCampoAcervo.jsp</li>
	 *     <li>/sigaa.war/public/biblioteca/ buscaPublicaAvancadaAcervo.jsp</li>
	 *     <li>/sigaa.war/public/biblioteca/buscaPublicaSimplesAutoridades.jsp</li>
	 *   </ul>
	 */
	public String cancelarBuscaPublica() {
		resetBean();
		return forward("/public/index.jsp");
	}
	
	
	
	/**
	 *   Configura o sistema para mostra o formul�rio da busca p�blica simples
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp
	 */
	public String configuraBuscaSimples(){
		buscaSimples = true;
		buscaMultiCampo = false;
		buscaAvancada = false;
		buscaAutoridadesSimples = false;
		
		limparResultadosBuscaAcervo();
		
		return telaBuscaPublicaAcervo();
	}
	
	/**
	 *   Configura o sistema para mostra o formul�rio da busca p�blica simples
	 * 
	 *   <br/><br/>
	 *   Chamado a partir da p�gina: /sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp
	 */
	public String configuraBuscaMultiCampo(){
		buscaSimples = false;
		buscaMultiCampo = true;
		buscaAvancada = false;
		buscaAutoridadesSimples = false;
		
		limparResultadosBuscaAcervo();
		
		return telaBuscaPublicaAcervo();
	}
	
	
	
	
	/**
	 *    Configura o sistema para mostrar o formul�rio da busca p�blica avan�ada.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp
	 */
	public String configuraBuscaAvancada(){
		buscaSimples = false;
		buscaMultiCampo = false;
		buscaAvancada = true;
		buscaAutoridadesSimples = false;
		
		limparResultadosBuscaAcervo();
		
		return telaBuscaPublicaAcervo();
	}
	
	/**
	 *    Configura o sistema para mostrar o formul�rio da busca p�blica de autoridades.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp
	 */
	public String configuraBuscaAutoridadesSimples(){
		buscaSimples = false;
		buscaMultiCampo = false;
		buscaAvancada = false;
		buscaAutoridadesSimples = true;
		
		limparResultadosBuscaAcervo();
		
		return telaBuscaPublicaAcervo();
	}
	
	
	
	
	
	/**
	 * Popula uma cole��o de objetos contendo as novas aquisi��es da biblioteca no SIPAC
	 * 
	 * <br/><br/>
	 * /sigaa.war/public/biblioteca/buscaPublicaAquisicaoBiblioteca.jsp
	 */
	public void pesquisarAquisicaoSIPAC(ActionEvent e) throws ArqException{
		
		aquisicaoSIPAC = getDAO(TituloCatalograficoDao.class).
		findAllAquisicaoSIPAC(titulo, autor, anoInicial, LIMITE_AQUISICAO_LISTADA);
		
		if(aquisicaoSIPAC.size() >= LIMITE_AQUISICAO_LISTADA){
			
			addMensagemWarning("Sua busca trouxe mais de " + LIMITE_AQUISICAO_LISTADA +
					" resultados e somente os " + LIMITE_AQUISICAO_LISTADA + " primeiros "
					+" est�o sendo mostrados, por favor restrinja-a");
			
		}else if(aquisicaoSIPAC.size()==0)
			addMensagemWarning("Nenhum resultado foi encontrado para os crit�rios utilizados.");
		
	}
	

	
	/**
	 * Cont�m uma cole��o de objetos contendo as novas aquisi��es da biblioteca no SIPAC
	 * 
	 * <br/><br/>
	 * /sigaa.warpublic/biblioteca/buscaPublicaAquisicaoBiblioteca.jsp
	 */
	public Collection<Object[]> getAquisicaoSIPAC(){
		return aquisicaoSIPAC;
	}

	
	
	/**
	 *   Retorna os ids das bibliotecas que possuem acervo p�blico. (liberado para consulta a usu�rios que n�o trabalham nas bibliotecas)
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
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
	 *   <p>Retorna os ids das bibliotecas que possuem acervo p�blico formatados com v�rgula 1,2,3,4,5.... </p>
	 *   <p>Este valor vai ser passado como par�metro para o MBean que visualiza os detalhes dos materiais,
	 *   para impedir a visualiza de materiais de bibliotecas n�o publicas</p>
	 * 
	 *   <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublica.jsp</li>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaAvancada.jsp</li>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaMultiCampo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 */
	public String getIdsBibliotecasAcervoPublicoFormatados() throws DAOException{
		
		String retorno = "";
		
		List<Integer> lista = getIdsBibliotecasAcervoPublico();
		
		for (int index = 0; index <  lista.size(); index++) {
			
			if(index == lista.size()-1)
				retorno +=lista.get(index);
			else
				retorno +=lista.get(index)+"_";
		}
		
		return retorno;
	}
	
	
	//////////////////////// M�todos dos ComboBox  ////////////////////////////
	
	/**
	 * 
	 * Retorna todas as Biblioteca internas do sistema para mostrar no formul�rio da busca
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublica.jsp</li>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaAvancada.jsp</li>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaMultiCampo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecasInternasAtivas() throws DAOException{
		if ( bibliotecasAtivas == null )
			bibliotecasAtivas = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		return toSelectItems(bibliotecasAtivas, "id", "descricaoCompleta");
	}
	
	/**
	 * 
	 * Retorna todas as cole��es para mostrar no formul�rio da busca
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublica.jsp</li>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaAvancada.jsp</li>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaMultiCampo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getColecoesAtivas() throws DAOException{
		if(colecoes == null)
			colecoes = getGenericDAO().findByExactField(Colecao.class, "ativo", true);
		return toSelectItems(colecoes, "id", "descricaoCompleta");
	}
	

	/**
	 * 
	 * Retorna todas os tipos do item catalogr�fico para mostrar no formul�rio da busca
	 *
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublica.jsp</li>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaAvancada.jsp</li>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaMultiCampo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getTiposMateriaisAtivos() throws DAOException{
		if(tiposMateriais == null)
			tiposMateriais = getGenericDAO().findByExactField(TipoMaterial.class, "ativo", true);
		return toSelectItems(tiposMateriais, "id", "descricao");
	}
	
	
	//////////////////////////////////////////////////////
	
	
	
	
	/////// Tela de navega��o ///////
	
	/**
	 * M�todo n�o chamado por jsp.
	 */
	public String telaBuscaPublicaAcervo(){
		return forward(PAGINA_BUSCA_PUBLICA);
	}
	
	
	
	/**
	 * Retorna para a tela de busca.
	 * 
	 * N�o � chamado por nenhuma JSP.
	 */
	public String voltarTelaBusca() {
		return telaBuscaPublicaAcervo();
	}
	
	
	/**
	 *  Retorna os campos que o usu�rio vai pode escolher a ordena��o no resultado da pesquisa.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaAcervo.jsp</li>
	 *    <li>/sigaa.war/public/biblioteca/buscaPublicaArtigos.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoPaginadoBiblioteca#getCampoOrdenacaoResultadosComboBox()
	 */
	@Override
	public Collection<SelectItem> getCampoOrdenacaoResultadosComboBox() {
		
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		
		if(! buscaAutoridadesSimples){
			for(CampoOrdenacaoConsultaAcervo campo : CampoOrdenacaoConsultaAcervo.CAMPOS_ORDENACAO_TITULO_PUBLICO){
				lista.add(new SelectItem(campo.getValor(), campo.getDescricao()));
			}
		}
		else{
			lista = getCampoOrdenacaoResultadosAutoridadesComboBox(); 
		}
		
		return lista;
	}
	
	
	/**
	 *  Retorna os campos que o usu�rio vai pode escolher a ordena��o no resultado da pesquisa exclusivo para autoridades. Usado na busca interna
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoPaginadoBiblioteca#getCampoOrdenacaoResultadosComboBox()
	 */
	public Collection<SelectItem> getCampoOrdenacaoResultadosAutoridadesComboBox() {
		
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		
		for(CampoOrdenacaoConsultaAcervo campo : CampoOrdenacaoConsultaAcervo.CAMPOS_ORDENACAO_AUTORIDADES_PUBLICO){
			lista.add(new SelectItem(campo.getValor(), campo.getDescricao()));
		}
		
		return lista;
	}
	
	
	
	// Os gets e sets do JSF
	

	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getAssunto() {
		return assunto;
	}
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getLocalPublicacao() {
		return localPublicacao;
	}
	public void setLocalPublicacao(String localPublicacao) {
		this.localPublicacao = localPublicacao;
	}
	public String getEditora() {
		return editora;
	}
	public void setEditora(String editora) {
		this.editora = editora;
	}
	public Integer getAnoInicial() {
		return anoInicial;
	}
	public void setAnoInicial(Integer anoInicial) {
		this.anoInicial = anoInicial;
	}
	public Integer getAnoFinal() {
		return anoFinal;
	}
	public void setAnoFinal(Integer anoFinal) {
		this.anoFinal = anoFinal;
	}

	public boolean isBuscarTitulo() {
		return buscarTitulo;
	}

	public void setBuscarTitulo(boolean buscarTitulo) {
		this.buscarTitulo = buscarTitulo;
	}

	public boolean isBuscarAssunto() {
		return buscarAssunto;
	}

	public void setBuscarAssunto(boolean buscarAssunto) {
		this.buscarAssunto = buscarAssunto;
	}

	public boolean isBuscarAutor() {
		return buscarAutor;
	}

	public void setBuscarAutor(boolean buscarAutor) {
		this.buscarAutor = buscarAutor;
	}

	public boolean isBuscarLocalPublicacao() {
		return buscarLocalPublicacao;
	}

	public void setBuscarLocalPublicacao(boolean buscarLocalPublicacao) {
		this.buscarLocalPublicacao = buscarLocalPublicacao;
	}

	public boolean isBuscarEditora() {
		return buscarEditora;
	}

	public void setBuscarEditora(boolean buscarEditora) {
		this.buscarEditora = buscarEditora;
	}

	public boolean isBuscarAno() {
		return buscarAno;
	}

	public void setBuscarAno(boolean buscarAno) {
		this.buscarAno = buscarAno;
	}

	public boolean isBuscarPalavrasChaves() {
		return buscarPalavrasChaves;
	}

	public void setBuscarPalavrasChaves(boolean buscarPalavrasChaves) {
		this.buscarPalavrasChaves = buscarPalavrasChaves;
	}

	public List<CacheEntidadesMarc> getArtigos() {
		return artigos;
	}

	public void setArtigos(List<CacheEntidadesMarc> artigos) {
		this.artigos = artigos;
	}

	public Integer getIdBiblioteca() {
		return idBiblioteca;
	}

	public void setIdBiblioteca(Integer idBiblioteca) {
		this.idBiblioteca = idBiblioteca;
	}

	public Integer getIdColecao() {
		return idColecao;
	}

	public void setIdColecao(Integer idColecao) {
		this.idColecao = idColecao;
	}

	public Integer getIdTipoMaterial() {
		return idTipoMaterial;
	}

	public void setIdTipoMaterial(Integer idTipoMaterial) {
		this.idTipoMaterial = idTipoMaterial;
	}

	public boolean isBuscarBiblioteca() {
		return buscarBiblioteca;
	}

	public void setBuscarBiblioteca(boolean buscarBiblioteca) {
		this.buscarBiblioteca = buscarBiblioteca;
	}

	public boolean isBuscarColecao() {
		return buscarColecao;
	}

	public void setBuscarColecao(boolean buscarColecao) {
		this.buscarColecao = buscarColecao;
	}

	public boolean isBuscarTipoMaterial() {
		return buscarTipoMaterial;
	}


	public void setBuscarTipoMaterial(boolean buscarTipoMaterial) {
		this.buscarTipoMaterial = buscarTipoMaterial;
	}

	public boolean isBuscaAvancada() {
		return buscaAvancada;
	}

	public void setBuscaAvancada(boolean buscaAvancada) {
		this.buscaAvancada = buscaAvancada;
	}

	public List<CampoPesquisaAvancada> getCampos() {
		return campos;
	}


	public void setCampos(List<CampoPesquisaAvancada> campos) {
		this.campos = campos;
	}

	public CampoPesquisaAvancada getCampoBuscaSimples() {
		return campoBuscaSimples;
	}

	public void setCampoBuscaSimples(CampoPesquisaAvancada campoBuscaSimples) {
		this.campoBuscaSimples = campoBuscaSimples;
	}

	public boolean isBuscaSimples() {
		return buscaSimples;
	}


	public void setBuscaSimples(boolean buscaSimples) {
		this.buscaSimples = buscaSimples;
	}

	public boolean isBuscaMultiCampo() {
		return buscaMultiCampo;
	}

	public void setBuscaMultiCampo(boolean buscaMultiCampo) {
		this.buscaMultiCampo = buscaMultiCampo;
	}

	public boolean isBuscaAutoridadesSimples() {
		return buscaAutoridadesSimples;
	}

	public void setBuscaAutoridadesSimples(boolean buscaAutoridadesSimples) {
		this.buscaAutoridadesSimples = buscaAutoridadesSimples;
	}


	public boolean isBuscarAssuntoAutorizado() {
		return buscarAssuntoAutorizado;
	}


	public void setBuscarAssuntoAutorizado(boolean buscarAssuntoAutorizado) {
		this.buscarAssuntoAutorizado = buscarAssuntoAutorizado;
	}


	public boolean isBuscarAutorAutorizado() {
		return buscarAutorAutorizado;
	}


	public void setBuscarAutorAutorizado(boolean buscarAutorAutorizado) {
		this.buscarAutorAutorizado = buscarAutorAutorizado;
	}


	public String getAutorAutorizado() {
		return autorAutorizado;
	}


	public void setAutorAutorizado(String autorAutorizado) {
		this.autorAutorizado = autorAutorizado;
	}


	public String getAssuntoAutorizado() {
		return assuntoAutorizado;
	}


	public void setAssuntoAutorizado(String assuntoAutorizado) {
		this.assuntoAutorizado = assuntoAutorizado;
	}
	
	
	
}
