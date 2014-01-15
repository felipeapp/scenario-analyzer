/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 27/04/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoPaginadoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.GeraPesquisaTextualFactory;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;
import br.ufrn.sigaa.biblioteca.util.PaginacaoBibliotecaUtil;

/**
 *      MBean que controla o fluxo do cadastro de autoridades, pesquisa e edi��o
 *      
 *      Como a forma de preenchimento dos dados � muito parecida com o preenchimento de um t�tulo
 * esse MBean vai delegar boa parte da suas fun��es para o MBean de cataloga��o para aproveitar 
 * a mesma p�gina.
 *      
 * @see Autoridade
 * @author jadson
 * @since 27/04/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("catalogaAutoridadesMBean") 
@Scope("request")
public class CatalogaAutoridadesMBean  extends PesquisarAcervoPaginadoBiblioteca {
	
	/**
	 * A p�gina de pesquisa de autoridades padr�o do sistema.
	 */
	public static final String PAGINA_PESQUISA_AUTORIDADES = "/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaAutoridade.jsp";
	
	
	/**
	 *  Usado para gerar consultas nativas do banco
	 */
	private GeraPesquisaTextual geradorPesquisa;
	
	/** Entrada autorizada do autor para realizar a busca */
	private String entradaAutorizadaAutor;
	/** Entrada autorizada de assunto para realizar a busca */
	private String entradaAutorizadaAssunto;
	/** Nome remissivo de  autor para realizar a busca */
	private String nomeRemissivoAutor;
	/** Nome remissivo de assunto para realizar a busca */
	private String nomeRemissivoAssunto;
	/**n�mero do sistema para realizar a busca */
	private Integer numeroDoSistema;
	
	/** Indica que a busca ser� realizada pelo n�mero do sistema */
	private boolean buscarNumeroSistema;
	/** Indica que a busca ser� realizada pela entrada autorizada do autor */
	private boolean buscarAutorizadoAutor;
	/** Indica que a busca ser� realizada pela entrada autorizada de assunto */
	private boolean buscarAutorizadoAssunto;
	/** Indica que a busca ser� realizada pelo entrada remissiva do autor  */
	private boolean buscarRemissivoAutor;
	/** Indica que a busca ser� realizada pelo entrada remissiva de assunto  */
	private boolean buscarRemissivoAssunto;

	
	////////// Dados da busca simples /////////////
	/** Autor para realizar a busca */
	private String autor;
	/** Assunto para realizar a busca */
	private String assunto;
	
	/** Indica que a busca ser� realizada pelo do autor */
	private boolean buscarAutor;
	
	/** Indica que a busca ser� realizada pelo do assunto */
	private boolean buscarAssunto;
	
	////////////////////////////////
	
	/** Indica que a opera��o est� sendo feita agora � a busca normal no acervo, sem nenhuma opera��o extra */
	private boolean pesquisaNormal;
	/** Indica que a opera��o est� sendo feita agora � a busca para a cataloga��o de uma autoridade */
	private boolean pesquisaCatalogacao;  
	/** Indica que a opera��o est� sendo feita agora � a busca para exportar a autoridade */
	private boolean pesquisaExportacao;
	/** Indica que a opera��o est� sendo feita agora � a busca para selecionar uma autoridade e atribuir a um t�tulo */
	private boolean pesquisaSelecaoAutoridadeAutor; 
	/** Indica que a opera��o est� sendo feita agora � a busca para selecionar uma autoridade e atribuir a um t�tulo */
	private boolean pesquisaSelecaoAutoridadeAssunto; 
	
	
	/** Guarda o valor da aba utilizada na busca de autoridades **/
	private String valorAbaPesquisa = "";
	
	/**
	 * Construtor padr�o
	 */
	public CatalogaAutoridadesMBean() {
		obj = new Autoridade();
		geradorPesquisa = new GeraPesquisaTextualFactory().getGeradorPesquisaTextual();
	}
	
	
	/**
	 * 
	 * <p>Inicia o caso de uso de catalogar uma autoridade.<p/>
	 * <p>O usu�rio escolhe se quer preencher todos os dados, usar uma planilha ou importar.<p/> 
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		pesquisaCatalogacao = true;
		
		return telaPesquisaAutoridades();
		
	}
	
	/**
	 *       <p>Inicia o caso de uso de pesquisa normal. Quando o usu�rio entra diretamente na 
	 * p�gina de pesquisa, s� a op��o de editar deve estar selecionada.<p/> 
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarPesquisaNormal() throws SegurancaException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO);
		pesquisaNormal = true;
		return telaPesquisaAutoridades();
	}
	
	
	
	/**
	 *       Inicia o caso de uso de pesquisa de autoridades pelo autor dentro do caso de uso de cataloga��o. 
	 *
	 * <br/><br/>
	 * Chamado a partir do Mbean: CatalogacaoMBean
	 * M�todo n�o invocado por nenhuma jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarPesquisaSelecaoAutoridadeAutor(String entradaAutorizada) throws DAOException{
		pesquisaSelecaoAutoridadeAutor = true;
		
		getCurrentRequest().setAttribute("naoVerificaCampoBuca", true);
		
		this.autor = entradaAutorizada;
		buscarAutor = true;
		buscarAssunto = false;
		return pesquisarAutoridadeSimples();
	}
	
	
	/**
	 *       Inicia o caso de uso de pesquisa de autoridades pelo assunto dentro do caso de uso de cataloga��o. 
	 *
	 * <br/><br/>
	 * Chamado a partir do Mbean: CatalogacaoMBean
	 * M�todo n�o invocado por nenhuma jsp
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarPesquisaSelecaoAutoridadeAssunto(String entradaAutorizada) throws DAOException{
		pesquisaSelecaoAutoridadeAssunto = true;
		
		getCurrentRequest().setAttribute("naoVerificaCampoBuca", true);
		
		this.assunto = entradaAutorizada;
		buscarAutor = false;
		buscarAssunto = true;
		return pesquisarAutoridadeSimples();
	}
	
	
	
	/**
	 *       Inicia o caso de uso de pesquisa de autoridades para a exporta��o. Depois que o usu�rio
	 * realizar a busca ser� mostrado a op��o de selecionar a autoridade, a autoridade selecionada
	 * ser� adicionada �s j� existentes na p�gina de exporta��o.
	 * 
	 *       Chamado dentro do campo de uso de exportar autoridades.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/cooperacao_tecnica_formExportarTitulo.jsp
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String iniciarPesquisaExportacaoAutoridade(){
		pesquisaExportacao = true;
		return telaPesquisaAutoridades();
	}
	
	
	/**
	 *    M�todo que configura o valor das abas da tela de pesquisa de t�tulos.
	 *
	 */
	private void configuraAbasPesquisa(){
		if ( StringUtils.notEmpty(valorAbaPesquisa))
			getCurrentSession().setAttribute("abaPesquisaAutoridades", valorAbaPesquisa);
	}
	
	
	
	/**
	 *     Chama o m�todo de pesquisa das autoridades cadastradas.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEscolheFormaCadastroAutoridade.jsp
	 * @return
	 * @throws DAOException
	 */
	public String pesquisarAutoridadeSimples() throws DAOException{
		
		configuraAbasPesquisa();
		
		AutoridadeDao dao = getDAO(AutoridadeDao.class);
		
		limpaResultadoPesquisa();
		
		CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
		
		if( buscarNumeroSistema) {

			
			if(numeroDoSistema == null){
				buscarNumeroSistema = false;
			}else{
				
				CacheEntidadesMarc autoridade = dao.findByNumeroSistema(numeroDoSistema);
			
				if(autoridade != null){
					resultadosBuscados.add(autoridade);
					quantidadeTotalResultados = resultadosBuscados.size();
					quantidadePaginas = PaginacaoBibliotecaUtil.calculaQuantidadePaginas(quantidadeTotalResultados, quantideResultadosPorPagina);
					resultadosPaginadosEmMemoria.addAll(PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados));
				}else{
					addMensagemWarning("N�o Foi encontrada nenhuma Autoridade com o N�mero do Sistema Informado.");
				}
			}
			
		}else{
		
			
			String autorBusca = autor;
			String assuntoBusca = assunto;
			
			 //quando a busca vem redirecionada da cataloga��o n�o tem par�metros na tela de pesquisa
			
			if( getCurrentRequest().getAttribute("naoVerificaCampoBuca") == null  
					|| ((Boolean)getCurrentRequest().getAttribute("naoVerificaCampoBuca")) == false ){
			
				if(  "buscarAutor".equalsIgnoreCase(getParameter("radioCampoAutoridades"))  ){
					buscarAutor = true;
				}else{
					buscarAutor = false;
				}
				
				if(  "buscarAssunto".equalsIgnoreCase(getParameter("radioCampoAutoridades"))  ){
					buscarAssunto = true;
				}else{
					buscarAssunto = false;
				}
			
			}
			
			if(StringUtils.isEmpty(autor)) buscarAutor = false;
			if(StringUtils.isEmpty(assunto)) buscarAssunto = false;
			
			if(buscarAutor == false) autor = null;
			if(buscarAssunto == false) assunto = null;
			
			if(autor == null && assunto == null)
				addMensagemWarning("Selecione um crit�rio de busca");
			else{
				
				if(autor != null)
					resultadosBuscados = dao.buscaSimplesAutorAcervoAutoridades(geradorPesquisa, campoOrdenacao, autorBusca);
				else{
					if(assunto != null )
						resultadosBuscados = dao.buscaSimplesAssuntoAcervoAutoridades(geradorPesquisa, campoOrdenacao, assuntoBusca);
				}
					
				quantidadeTotalResultados = resultadosBuscados.size();
				quantidadePaginas = PaginacaoBibliotecaUtil.calculaQuantidadePaginas(quantidadeTotalResultados, quantideResultadosPorPagina);
				
				resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
				
				if(quantidadeTotalResultados == 0){
					addMensagemWarning("Nenhum resultado encontrado de acordo com os crit�rios da busca.");
				}
				
				if( AutoridadeDao.LIMITE_BUSCA_AUTORIDADE.compareTo( quantidadeTotalResultados ) <= 0){
					addMensagemWarning("A busca resultou em um n�mero muito grande de resultados, somente os "+AutoridadeDao.LIMITE_BUSCA_AUTORIDADE+" primeiros est�o sendo mostrados.");
				}
				
			}
			
		}
		
		return telaPesquisaAutoridades();
	}
	
	
	
	/**
	 *     Chama o m�todo de pesquisa das autoridades cadastradas.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaEscolheFormaCadastroAutoridade.jsp
	 * @return
	 * @throws DAOException
	 */
	public String pesquisarAutoridadeMultiCampo() throws DAOException{
		
		configuraAbasPesquisa();
		
		AutoridadeDao dao = getDAO(AutoridadeDao.class);
		
		limpaResultadoPesquisa();
		
		CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
		
		if( buscarNumeroSistema) {

			
			if(numeroDoSistema == null){
				buscarNumeroSistema = false;
			}else{
				
				CacheEntidadesMarc autoridade = dao.findByNumeroSistema(numeroDoSistema);
			
				if(autoridade != null){
					resultadosBuscados.add(autoridade);
					quantidadeTotalResultados = resultadosBuscados.size();
					quantidadePaginas = PaginacaoBibliotecaUtil.calculaQuantidadePaginas(quantidadeTotalResultados, quantideResultadosPorPagina);
					resultadosPaginadosEmMemoria.addAll(PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados));
				}else{
					addMensagemWarning("N�o Foi encontrada nenhuma Autoridade com o N�mero do Sistema Informado.");
				}
			}
			
		}else{
		
			
			String entradaAutorizadaAutorBusca = entradaAutorizadaAutor;
			String entradaAutorizadaAssuntoBusca = entradaAutorizadaAssunto;
			String nomeRemissivoAutorBusca = nomeRemissivoAutor;
			String nomeRemissivoAssuntoBusca = nomeRemissivoAssunto;
			
			if(StringUtils.isEmpty(entradaAutorizadaAutor)) buscarAutorizadoAutor = false;
			if(StringUtils.isEmpty(entradaAutorizadaAssunto)) buscarAutorizadoAssunto = false;
			if(StringUtils.isEmpty(nomeRemissivoAutor)) buscarRemissivoAutor = false;
			if(StringUtils.isEmpty(nomeRemissivoAssunto)) buscarRemissivoAssunto = false;
			
			if(buscarAutorizadoAutor == false) entradaAutorizadaAutorBusca = null;
			if(buscarAutorizadoAssunto == false) entradaAutorizadaAssuntoBusca = null;
			if(buscarRemissivoAutor == false) nomeRemissivoAutorBusca = null;
			if(buscarRemissivoAssunto == false) nomeRemissivoAssuntoBusca = null;
			
			if(entradaAutorizadaAutorBusca == null && entradaAutorizadaAssuntoBusca == null && nomeRemissivoAutorBusca == null && nomeRemissivoAssuntoBusca == null)
				addMensagemWarning("Selecione um crit�rio de busca");
			else{
				
				if(entradaAutorizadaAutorBusca != null || nomeRemissivoAutorBusca != null)
					resultadosBuscados = dao.buscaMultiCampoAutorAcervoAutoridades(geradorPesquisa, campoOrdenacao, entradaAutorizadaAutorBusca, nomeRemissivoAutorBusca);
				else{
					if(entradaAutorizadaAssuntoBusca != null || nomeRemissivoAssuntoBusca != null )
						resultadosBuscados = dao.buscaMultiCampoAssuntoAcervoAutoridades(geradorPesquisa, campoOrdenacao, entradaAutorizadaAssuntoBusca, nomeRemissivoAssuntoBusca);
				}
					
				quantidadeTotalResultados = resultadosBuscados.size();
				quantidadePaginas = PaginacaoBibliotecaUtil.calculaQuantidadePaginas(quantidadeTotalResultados, quantideResultadosPorPagina);
				
				resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
				
				if(quantidadeTotalResultados == 0){
					addMensagemWarning("Nenhum resultado encontrado de acordo com os crit�rios da busca.");
				}
				
				if( AutoridadeDao.LIMITE_BUSCA_AUTORIDADE.compareTo( quantidadeTotalResultados ) <= 0){
					addMensagemWarning("A busca resultou em um n�mero muito grande de resultados, somente os "+AutoridadeDao.LIMITE_BUSCA_AUTORIDADE+" primeiros est�o sendo mostrados.");
				}
				
			}
			
		}
		
		return telaPesquisaAutoridades();
	}
	
	
	
	/**
	 *    Apaga os resultados da pesquisa.
	 * 
	 * <br/><br/>   
	 * Chamado a partir da p�gina:  /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaPesquisaAutoridade.jsp
	 * @return
	 */
	public String apagarDadosPesquisa(){
		
		apagarFormularioPesquisa();
		limpaResultadoPesquisa();
		
		return telaPesquisaAutoridades();
	}
	
	/**
	 * Limpa apenas o formul�rio da pesquisa
	 */
	private void apagarFormularioPesquisa(){
		numeroDoSistema = null;
		buscarNumeroSistema = false;
		entradaAutorizadaAutor = null;
		nomeRemissivoAutor = null;
		buscarAutorizadoAutor = false;
		buscarRemissivoAutor = false;
		
		entradaAutorizadaAssunto = null;
		nomeRemissivoAssunto = null;
		buscarAutorizadoAssunto = false;
		buscarRemissivoAssunto = false;
		valorCampoOrdenacao = CampoOrdenacaoConsultaAcervo.ENTRADA_AUTORIZADA_AUTOR.getValor();
	}

	
	
	
	
	/**
	 * 
	 * Chama o Mbean que controla a p�gina do MARC para edi��o dos dados de uma autoridade.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaPesquisaAutoridade.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String editarAutoridade() throws ArqException {
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		AutoridadeDao dao = null;
		
		try{
			dao =  getDAO(AutoridadeDao.class);
		
			Boolean ativo = dao.findSeAutoridadeEstaAtivo(getParameterInt("idAutoridadeParaEdicao"));
		
			if(! ativo ){
				addMensagemErro("A autoridade foi removida, n�o pode mais ser alterada.");
				return null;
			}
		
			// busca a autoridade para edi��o.
			Autoridade autoridade = dao.findAutoriadeByIdInicializandoDados(getParameterInt("idAutoridadeParaEdicao"));
		
		
			CatalogacaoMBean bean = getMBean("catalogacaoMBean");
	
		
			if(autoridade.isCatalogada())
				return bean.iniciarParaEdicaoAutoridades(CatalogacaoUtil.criaTituloAPartirAutoridade(autoridade));
			else
				return bean.iniciarAutoridadesDuplicacao(CatalogacaoUtil.criaTituloAPartirAutoridade(autoridade)); // N�o vai editar, o usu�rio vai continuar salvando at� finalizar
		
		}finally{
			if(dao != null ) dao.close();
		}
		
	}
	
	
	
	/**
	 *    Se o usu�rio selecionou a autoridade, volta ao Mbean de cataloga��o para setar a entrada 
	 * autorizada na p�gina e colocar uma refer�ncia da autoridade no campo de dados para as buscas.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaAutoridade.jsp
	 * @return
	 * @throws DAOException
	 */
	public String selecionarAutoridade() throws DAOException{
		 
		int idAutoridadeCacheSelecionada = getParameterInt("idAutoridadeCacheSelecionada", 0);
		
		
		CatalogacaoMBean bean = getMBean("catalogacaoMBean");
		
		if(idAutoridadeCacheSelecionada != 0){
		 
			 for (CacheEntidadesMarc autoridadeCache : resultadosBuscados) {
				
				 if(autoridadeCache.getIdAutoridade() == idAutoridadeCacheSelecionada){
					 
					 if(pesquisaSelecaoAutoridadeAutor)
						 return bean.configuraEntradaAutorizadaAutor(autoridadeCache.getIdAutoridade());
					 
					 if(pesquisaSelecaoAutoridadeAssunto)
						 return bean.configuraEntradaAutorizadaAssunto(autoridadeCache.getIdAutoridade());
				 }
				 
			}
			 
		}
		
		if(pesquisaSelecaoAutoridadeAutor)
			return bean.configuraEntradaAutorizadaAutor(null); 
		if(pesquisaSelecaoAutoridadeAssunto)
			return bean.configuraEntradaAutorizadaAssunto(null);
		
		return null; // n�o era para chegar aqui.
		 
	}
	
	
	
	
	/**
	 *    M�todo que obt�m o id da autoridade selecionada na p�gina de busca e adiciona as autoridades que
	 *  j� existem para exporta��o.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaAutoridade.jsp
	 * @return
	 * @throws DAOException
	 */
	public String exportarAutoridade() throws DAOException{
		
		CooperacaoTecnicaExportacaoMBean bean = getMBean("cooperacaoTecnicaExportacaoMBean");
		
		return bean.adicionarAutoridadePesquisada( getParameterInt("idAutoridadeParaExportacao", 0) );
		
	}

	
	
	
	/**
	 * 
	 * Inicia o caso de uso de duplicar as informa��es de uma autoriade copiando de outra.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaAutoridade.jsp
	 * 
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String duplicarAutoridade()throws ArqException{
		
		CatalogacaoMBean bean = getMBean("catalogacaoMBean");
		
		Autoridade a = CatalogacaoUtil.duplicarAutoridade (
				getDAO(AutoridadeDao.class).findAutoriadeByIdInicializandoDados( getParameterInt("idAutoridadeParaDuplicacao")  ));
		
		return bean.iniciarAutoridadesDuplicacao( CatalogacaoUtil.criaTituloAPartirAutoridade(a));
		
	}
	
	
	/**
	 * 
	 * Retorna os campos que o usu�rio vai pode escolher a ordena��o no resultado da pesquisa.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaPesquisaAutoridade.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public Collection<SelectItem> getCampoOrdenacaoResultadosComboBox(){
		
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		
		for(CampoOrdenacaoConsultaAcervo campo : CampoOrdenacaoConsultaAcervo.CAMPOS_ORDENACAO_AUTORIDADES){
			lista.add(new SelectItem(campo.getValor(), campo.getDescricao()));
		}
		
		return lista;
	}
	
	
	
	///////////////    telas de navega��o    //////////////////
	
	
	/**
	 * Redireciona para a tela de pesquisa de autoridades.
	 * <br/><br/>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 */
	public String telaPesquisaAutoridades(){
		return forward(PAGINA_PESQUISA_AUTORIDADES);
	}
	
	
	/// sets e gets
	

	public List<CacheEntidadesMarc> getAutoridades() {
		return resultadosBuscados;
	}


	public String getEntradaAutorizadaAutor() {
		return entradaAutorizadaAutor;
	}


	public void setEntradaAutorizadaAutor(String entradaAutorizadaAutor) {
		this.entradaAutorizadaAutor = entradaAutorizadaAutor;
	}


	public String getEntradaAutorizadaAssunto() {
		return entradaAutorizadaAssunto;
	}


	public void setEntradaAutorizadaAssunto(String entradaAutorizadaAssunto) {
		this.entradaAutorizadaAssunto = entradaAutorizadaAssunto;
	}


	public String getNomeRemissivoAutor() {
		return nomeRemissivoAutor;
	}


	public void setNomeRemissivoAutor(String nomeRemissivoAutor) {
		this.nomeRemissivoAutor = nomeRemissivoAutor;
	}


	public String getNomeRemissivoAssunto() {
		return nomeRemissivoAssunto;
	}


	public void setNomeRemissivoAssunto(String nomeRemissivoAssunto) {
		this.nomeRemissivoAssunto = nomeRemissivoAssunto;
	}


	public boolean isBuscarAutorizadoAutor() {
		return buscarAutorizadoAutor;
	}


	public void setBuscarAutorizadoAutor(boolean buscarAutorizadoAutor) {
		this.buscarAutorizadoAutor = buscarAutorizadoAutor;
	}


	public boolean isBuscarAutorizadoAssunto() {
		return buscarAutorizadoAssunto;
	}


	public void setBuscarAutorizadoAssunto(boolean buscarAutorizadoAssunto) {
		this.buscarAutorizadoAssunto = buscarAutorizadoAssunto;
	}


	public boolean isBuscarRemissivoAutor() {
		return buscarRemissivoAutor;
	}


	public void setBuscarRemissivoAutor(boolean buscarRemissivoAutor) {
		this.buscarRemissivoAutor = buscarRemissivoAutor;
	}


	public boolean isBuscarRemissivoAssunto() {
		return buscarRemissivoAssunto;
	}


	public void setBuscarRemissivoAssunto(boolean buscarRemissivoAssunto) {
		this.buscarRemissivoAssunto = buscarRemissivoAssunto;
	}


	public void setAutoridades(List<CacheEntidadesMarc> autoridades) {
		this.resultadosBuscados = autoridades;
	}


	public boolean isPesquisaNormal() {
		return pesquisaNormal;
	}


	public void setPesquisaNormal(boolean pesquisaNormal) {
		this.pesquisaNormal = pesquisaNormal;
	}


	public boolean isPesquisaExportacao() {
		return pesquisaExportacao;
	}


	public void setPesquisaExportacao(boolean pesquisaExportacao) {
		this.pesquisaExportacao = pesquisaExportacao;
	}


	public boolean isPesquisaCatalogacao() {
		return pesquisaCatalogacao;
	}


	public void setPesquisaCatalogacao(boolean pesquisaCatalogacao) {
		this.pesquisaCatalogacao = pesquisaCatalogacao;
	}


	public boolean isPesquisaSelecaoAutoridadeAutor() {
		return pesquisaSelecaoAutoridadeAutor;
	}


	public void setPesquisaSelecaoAutoridadeAutor(boolean pesquisaSelecaoAutoridadeAutor) {
		this.pesquisaSelecaoAutoridadeAutor = pesquisaSelecaoAutoridadeAutor;
	}


	public boolean isPesquisaSelecaoAutoridadeAssunto() {
		return pesquisaSelecaoAutoridadeAssunto;
	}


	public void setPesquisaSelecaoAutoridadeAssunto(boolean pesquisaSelecaoAutoridadeAssunto) {
		this.pesquisaSelecaoAutoridadeAssunto = pesquisaSelecaoAutoridadeAssunto;
	}


	public Integer getNumeroDoSistema() {
		return numeroDoSistema;
	}


	public void setNumeroDoSistema(Integer numeroDoSistema) {
		this.numeroDoSistema = numeroDoSistema;
	}


	public boolean isBuscarNumeroSistema() {
		return buscarNumeroSistema;
	}


	public void setBuscarNumeroSistema(boolean buscarNumeroSistema) {
		this.buscarNumeroSistema = buscarNumeroSistema;
	}


	public String getValorAbaPesquisa() {
		return valorAbaPesquisa;
	}


	public void setValorAbaPesquisa(String valorAbaPesquisa) {
		this.valorAbaPesquisa = valorAbaPesquisa;
	}


	public String getAutor() {
		return autor;
	}


	public void setAutor(String autor) {
		this.autor = autor;
	}


	public String getAssunto() {
		return assunto;
	}


	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}


	public boolean isBuscarAutor() {
		return buscarAutor;
	}

	public void setBuscarAutor(boolean buscarAutor) {
		this.buscarAutor = buscarAutor;
	}

	public boolean isBuscarAssunto() {
		return buscarAssunto;
	}

	public void setBuscarAssunto(boolean buscarAssunto) {
		this.buscarAssunto = buscarAssunto;
	}

	
	
	
}
