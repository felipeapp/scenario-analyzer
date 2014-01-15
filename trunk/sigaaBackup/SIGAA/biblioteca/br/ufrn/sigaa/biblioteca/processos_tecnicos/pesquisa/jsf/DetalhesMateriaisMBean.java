/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ArtigoDePeriodicoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FormatoMaterialDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.AssinaturaByUnidadeDestinoCodigoComparator;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 *
 * <p> Classe que cont�m os dado comuns da visualiza��o dos materiais de um t�tulo, tanto para a parte
 * privada como p�blica do sistema. </p>
 * 
 * @author jadson
 *
 */
public abstract class DetalhesMateriaisMBean extends SigaaAbstractController<TituloCatalografico>{

	/** As informa��es do T�tulo que ser�o mostradas ao usu�rio.*/
	protected CacheEntidadesMarc tituloCache;
	
	
	/** Guarda todos os materiais do T�tulo de n�o peri�dico, carregados sobre demanda */ 
	protected List<Exemplar> exemplares = new ArrayList<Exemplar>();
	
	/** Os exemplares do T�tulo  mostrados no momento ao usu�rio */ 
	protected List<Exemplar> exemplaresPaginados = new ArrayList<Exemplar>();
	
	/** Guarda todos os materiais do T�tulo de peri�dico, carregados sobre demanda */ 
	protected List<Fasciculo> fasciculos = new ArrayList<Fasciculo>(); 
	
	/** Os fasc�culos do T�tulo mostrados no momento ao usu�rio */ 
	protected List<Fasciculo> fasciculosPaginados = new ArrayList<Fasciculo>(); 
	
	
	///////////////  Dados para visualiza��o quando o t�tulo � de peri�dico    ///////////////
	
	/** guarda todas as assinaturas do t�tulo selecionado */
	protected List<Assinatura> assinaturasTitulo = new ArrayList<Assinatura>();   
	
	/** guarda os ids das assinaturas do t�tulo selecionado */
	List<Integer> idsAssinaturaBusca = new ArrayList<Integer>();
	
	/**O valor padr�o a ser setado para a biblioteca */
	protected int VALOR_PADRAO_COMBOBOX_BIBLIOTECA = -2;
	
	/**O valor padr�o a ser setado para o ano */
	protected int VALOR_PADRAO_COMBOBOX_ANO = -1;
	
	/** usado para filtrar os materiais por biblioteca, pois em alguns casos,  a quantidade � muito grande */
	protected Integer idBibliotecaMateriais = VALOR_PADRAO_COMBOBOX_BIBLIOTECA;                                 
	
	/** usado para filtrar os fasc�culos pelo ano, pois em alguns casos,  a quantidade � muito grande */
	protected Integer anoPesquisaFasciculos = VALOR_PADRAO_COMBOBOX_ANO;                                
	
	/** Indica que o t�tulo � de peri�dico, ent�o deve-se mostrar os fasc�culos do mesmo, caso contr�rio, deve-se mostrar os exemplares.*/
	protected boolean periodico = false;
	
	/**
	 * Guarda a lista dos ids das bibliotecas que possuem acervo p�blico.  Se a busca for p�blica, somente 
	 * os materiais dessas bibliotecas podem ser exibidos.
	 */
	protected List<Integer> idsBibliotecasPublicas = new ArrayList<Integer>();
	
	/** Indica se deve retornar os apenas os materiais que possuem situa��o vis�vel para o usu�rio final. Esse valor vai ser passado 
	 * como par�metro nas buscas utilizadas pelos usu�riso finais da biblioteca. No caso da busca dos bibliotec�rios vai ser sempre
	 * false para recuperar todos os materiais do T�tulo. */
	protected boolean apenasSituacaoVisivelUsuarioFinal = false;
	
	
	/** Guarda o fasc�culo dos artigos para mostrar suas informa��es na p�gina de visualiza��es. */ 
	protected Fasciculo fasciculoSelecionado; 
	
	/** Caso o t�tulo seja de peri�dico */
	protected Assinatura assinatura;
	
	/** Usado na busca p�blica de artigos */
	protected CacheEntidadesMarc artigo;
	
	/** Para visualizar as informa��es dos artigos do fasc�culo selecionado */
	protected List<CacheEntidadesMarc> listaArtigosDoFasciculo;
	
	/** Armazena a p�gina que iniciou o fluxo do bean. */
	protected String paginaVoltar;
	
	
	/* Informa��es para p�gina��o dos resultados   */
	
	/** A p�gina atual */
	protected int paginaAtual = 1;
	
	/** A quantidade m�xima de materiais mostrados em uma p�gina */
	public static  final Integer QTD_RESULTADOS_POR_PAGINA = 20;
	
	/** A quantidade total de resultados que ser�o p�ginados */
	protected int quantidadeTotalResultados = 0;
	
	/** A quantidade total de p�ginas da p�gina��o */
	protected int quantidadePaginas = 1;
	
	/** Guarda as p�gina j� percorridas pelo usu�rio para n�o precisar ficar buscando os resultados toda vez que o usu�rio passar pela p�gina. */
	public Set<Integer> paginasPercorridas = new HashSet<Integer>();
	
	/** Se � para mostrar as informa��es do T�tulo do material na p�gina padr�o de detalhes do material, s� utilizado para a pesquisa de exemplares 
	 * e fasc�culos, na pesquisa de T�tulos n�o precisa mostrar, porque o usu�rio j� sabe o T�tulo.*/
	boolean mostrarInformacaoTituloDetalhesMaterial = false;
	
	
	
	/* Informa��es utilizadas para o usu�rio visializar as informa��es completas do material selecionado na listagem de materiais exibida na tela.   */
	
	/**
	 * O material selecionado com as informa��es completas
	 */
	protected MaterialInformacional materialSelecionado;
	
	/**
	 * A quantidade de empr�timos do material selecionado
	 */
	protected int qtdEmprestimosMaterialSelecionado = 0;
	
	/**
	 * As reservas ddo material selecionado
	 */
	protected List<ReservaMaterialBiblioteca> reservasDoMaterial;
	
	/**
	 * Os artigos do fasc�culo selecionado para mostrar ao usu�rio caso o fasc�culo possua artigos.
	 */
	protected List<CacheEntidadesMarc> artigosDoFasciculoSelecionado;
	

	/** Indica que a visualiza��o dos detalhes do material vai mostrar a informa��o do T�tulo.*/
	protected boolean mostrarInformacoesTitulo = false;
	
	/** As informa��es do T�tulo mostrado na p�gina de detalhes do Material*/
	private CacheEntidadesMarc tituloDoMaterial;
	
	/** A assinatura do fasc�culo selecionado para visualizar as informa��es */
	private Assinatura asssinaturaDoMaterial;
	
	
	/* ********************************************************************************************** */
	
	/**
	 * Construtor padr�o
	 */
	public DetalhesMateriaisMBean(){
		
		if(obj == null)
			obj = new TituloCatalografico();

		if (StringUtils.isEmpty(getParameter("redirectTo"))) {
			paginaVoltar = getCurrentRequest().getRequestURI().replaceFirst(getContextPath(), "");
		}
		else {
			paginaVoltar = getParameter("redirectTo");
		}
		
	}
	
	
	
	/**
	 * Popula dados dos materiais do t�tulo selecionado para exibi��o
	 * <br/>
	 * Chamado da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pequisas_acervo/paginaDetalhesMateriais.jsp
	 *
	 * @param event
	 * @throws DAOException
	 */
	protected void carregaDadosMateriais() throws DAOException{
		
		fasciculos.clear();
		exemplares.clear();
		

		
		paginaAtual = 1;
		quantidadeTotalResultados = 0;
		quantidadePaginas = 1;
		materialSelecionado = null;
		paginasPercorridas = new HashSet<Integer>();
		
		if(obj.getId() == 0){
			obj.setId(getParameterInt("idTitulo", 0)); // O id do t�tulo � passado como par�metro via Javascript
			String idBibliotecaAcervoPublico = getParameter("idsBibliotecasAcervoPublicoFormatados");
			
			Boolean apenasSituacaoVisivel = getParameterBoolean("apenasSituacaoVisivelUsuarioFinal");
			
			if(apenasSituacaoVisivel != null)
				apenasSituacaoVisivelUsuarioFinal = apenasSituacaoVisivel;
			else
				apenasSituacaoVisivelUsuarioFinal = false;
			
			if(idBibliotecaAcervoPublico != null){ // se est� na parte p�blica esse par�metro vai ser passado.
				
				String[] idsBibliotecasPublicasStr = idBibliotecaAcervoPublico.split("_");
				
				for (String string : idsBibliotecasPublicasStr) {
					idsBibliotecasPublicas.add( Integer.parseInt(string) );
				}
			}
		}
		
		if (obj.getId() > 0) {
			
			AssinaturaDao daoAssinatura = null;
			FormatoMaterialDao formatoDao = null;
			ExemplarDao daoExemplares = null;
			FasciculoDao daoFasciculo = null;
			
			try {
				daoAssinatura = getDAO(AssinaturaDao.class);
				formatoDao = getDAO(FormatoMaterialDao.class);
				
				// Para t�tulos n�o catalogados o formato pode ser nulo, mas esses t�tulo n�o podem pussuir materiais.
				FormatoMaterial formatoMaterial = formatoDao.findFormatoMaterialDoTitulo(obj.getId()); 

				tituloCache = formatoDao.findByExactField(CacheEntidadesMarc.class, "idTituloCatalografico", obj.getId(), true);
				
				
				periodico = formatoMaterial != null && formatoMaterial.isFormatoPeriodico();
				
				
				if(periodico){
					
					assinaturasTitulo = daoAssinatura.findAssinaturasAtivasByTituloByBibliotecas(obj.getId(), idsBibliotecasPublicas);
					
					Collections.sort(assinaturasTitulo, new AssinaturaByUnidadeDestinoCodigoComparator());
					
					idsAssinaturaBusca = new ArrayList<Integer>();
		
					for (Assinatura assi : assinaturasTitulo) {
						// se escolheu todas as bibliotecas ou a assinatura � da biblioteca escolhida.
						if(idBibliotecaMateriais.equals(-2) || assi.getUnidadeDestino().getId() == idBibliotecaMateriais){
							idsAssinaturaBusca.add(assi.getId());
						}
					}
					
					daoFasciculo = getDAO(FasciculoDao.class);
					quantidadeTotalResultados = daoFasciculo.countTodosFasciculosAtivosDasAssinaturas(idsAssinaturaBusca, anoPesquisaFasciculos, apenasSituacaoVisivelUsuarioFinal);
					quantidadePaginas = calculaQuantidadePaginas();
					
					/* Cria a cole��o com a quantidade total de exemplares */
					for (int index = 0; index < quantidadeTotalResultados; index++) {
						fasciculos.add(new Fasciculo());
					}
					
					buscarFasciculos();
					
				}else{
					
					daoExemplares = getDAO(ExemplarDao.class);
					quantidadeTotalResultados = daoExemplares.countTodosExemplaresAtivosDoTituloComDadosByBiblioteca(obj.getId(), idsBibliotecasPublicas, apenasSituacaoVisivelUsuarioFinal, idBibliotecaMateriais);
					quantidadePaginas = calculaQuantidadePaginas();
					
					/* Cria a cole��o com a quantidade total de exemplares */
					for (int index = 0; index < quantidadeTotalResultados; index++) {
						exemplares.add(new Exemplar());
					}
					
					buscarExemplares();	
					
				}
				
			} finally{
				if(formatoDao != null) formatoDao.close();
				if(daoAssinatura != null) daoAssinatura.close();
				if(daoExemplares != null) daoExemplares.close();
			}
		}
		
	}
	
	/**
	 * Realiza a busca p�gina de exemplares
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @throws DAOException
	 */
	private void buscarExemplares() throws DAOException{
		
		ExemplarDao daoExemplares = null;
		EmprestimoDao dao = null;
		ReservaMaterialBibliotecaDao reservaDao = null;
		
		try{
		
			daoExemplares = getDAO(ExemplarDao.class);
			dao = getDAO(EmprestimoDao.class);
			reservaDao = getDAO(ReservaMaterialBibliotecaDao.class);
			
			// Busca apenas uma vez os exemplares e mant�m eles na mem�ria quando o usu�rio percorre as p�ginas //
			if(! paginasPercorridas.contains(paginaAtual)){
				paginasPercorridas.add(paginaAtual);
				
				exemplaresPaginados = daoExemplares.findAllExemplarAtivosDoTituloComDadosByBiblioteca(obj.getId(), idsBibliotecasPublicas, apenasSituacaoVisivelUsuarioFinal, idBibliotecaMateriais, paginaAtual, QTD_RESULTADOS_POR_PAGINA);
			
				if(exemplaresPaginados.size() > 0){
					
					
					// Para cada exemplares recuperado, busca as data de devolu��o e conclus�o da reserva //
					for (Exemplar exemplar : exemplaresPaginados){
						
						if(ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
							if (exemplar.isDisponivel()) {
								exemplar.setPrazoConcluirReserva(reservaDao.findPrazoConcluirReservasEmEsperaTitulo(obj.getId()));
							}
						}
						
						if (exemplar.isEmprestado()) {						
							exemplar.setPrazoEmprestimo(dao.findPrazoDevolucaoMaterial(exemplar.getId()));														
						}
					}
					
					int indexTemp = 0; // o indice do resultado tempor�rio buscado no banco
					int indexPrimeiroResultado = getPrimeiroResultadoPagina()-1;
					int indexUltimoResultado =  getUltimoResultadoPagina()-1;
					
					for (; indexPrimeiroResultado <= indexUltimoResultado && indexTemp < exemplaresPaginados.size() ; indexPrimeiroResultado++) {
						exemplares.set(indexPrimeiroResultado, exemplaresPaginados.get(indexTemp));
						indexTemp++;
					}
				}
			}else{ // Se os materiais j� tinham sido buscados, copia para a lista mostrada ao usu�rio os materiais que j� estavam em mem�ria.
				
				exemplaresPaginados = new ArrayList<Exemplar>();
				
				int indexPrimeiroResultado = getPrimeiroResultadoPagina()-1;
				int indexUltimoResultado =  getUltimoResultadoPagina()-1;
				
				for (; indexPrimeiroResultado <= indexUltimoResultado ; indexPrimeiroResultado++) {
					exemplaresPaginados.add(exemplares.get(indexPrimeiroResultado));
				}
				
			}
			
		} finally {
			if (dao != null) dao.close();
			if (daoExemplares != null) daoExemplares.close();
			if (reservaDao != null) reservaDao.close();
		}						
	}
	
	
	/**
	 * Realiza a busca p�gina de fasc�culos
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @throws DAOException
	 */
	private void buscarFasciculos() throws DAOException{
		
		FasciculoDao daoFasciculo = null;
		ReservaMaterialBibliotecaDao reservaDao = null;
		EmprestimoDao dao = null;
		
		try{
		
			daoFasciculo = getDAO(FasciculoDao.class);
		
			reservaDao = getDAO(ReservaMaterialBibliotecaDao.class);
			
			
			// Busca apenas uma vez os exemplares e mant�m eles na mem�ria quando o usu�rio percorre as p�ginas //
			if(! paginasPercorridas.contains(paginaAtual)){
				paginasPercorridas.add(paginaAtual);
			
		
				if(idBibliotecaMateriais!= null && ! idBibliotecaMateriais.equals(-1)){ // usu�rio escolheu a biblioteca dos fasc�culos	
					
					if(assinaturasTitulo != null){
						
						
						fasciculosPaginados =  daoFasciculo.findTodosFasciculosAtivosDasAssinaturas(idsAssinaturaBusca, anoPesquisaFasciculos, apenasSituacaoVisivelUsuarioFinal, paginaAtual, QTD_RESULTADOS_POR_PAGINA);
						
						if(fasciculosPaginados.size() > 0){
						
							dao = getDAO(EmprestimoDao.class);
							
							
							// Para cada fasciculos recuperado, busca as data de devolu��o e conclus�o da reserva //	
							for (Fasciculo fasciculo : fasciculosPaginados) {							
								
								if(ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
									if (fasciculo.isDisponivel()) {
										fasciculo.setPrazoConcluirReserva(reservaDao.findPrazoConcluirReservasEmEsperaTitulo(obj.getId()));
									}
								}
								
								if (fasciculo.isEmprestado()) {
									fasciculo.setPrazoEmprestimo(dao.findPrazoDevolucaoMaterial(fasciculo.getId()));														
								}
						 	}
							
						}
						
						int indexTemp = 0; // o indice do resultado tempor�rio buscado no banco
						int indexPrimeiroResultado = getPrimeiroResultadoPagina()-1;
						int indexUltimoResultado =  getUltimoResultadoPagina()-1;
						
						for (; indexPrimeiroResultado <= indexUltimoResultado && indexTemp < fasciculosPaginados.size() ; indexPrimeiroResultado++) {
							fasciculos.set(indexPrimeiroResultado, fasciculosPaginados.get(indexTemp));
							indexTemp++;
						}
							
							
					}
				}
				
			}else{ // Se os materiais j� tinham sido buscados, copia para a lista mostrada ao usu�rio os materiais que j� estavam em mem�ria.
				
				fasciculosPaginados = new ArrayList<Fasciculo>();
				
				int indexPrimeiroResultado = getPrimeiroResultadoPagina()-1;
				int indexUltimoResultado =  getUltimoResultadoPagina()-1;
				
				for (; indexPrimeiroResultado <= indexUltimoResultado ; indexPrimeiroResultado++) {
					fasciculosPaginados.add(fasciculos.get(indexPrimeiroResultado));
				}
				
			}
		
		} finally {
			if (daoFasciculo != null) daoFasciculo.close();
			if (dao != null) dao.close();
			if (reservaDao != null) reservaDao.close();
		}
		
	
	}
	
	/**
	 * Calcula a quantidade de p�ginas necess�rias para mostras todos os resultados.
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	private int calculaQuantidadePaginas(){
		return (quantidadeTotalResultados / QTD_RESULTADOS_POR_PAGINA) + (quantidadeTotalResultados % QTD_RESULTADOS_POR_PAGINA == 0  ?  0 : 1 );
	}
	
	
	/**
	 *   <p>Inicia a visualiza��o dos materiais do T�tulo, realizando alguma a��o necess�ria antes da exibir a tela de informa��es dos materiais. </p>
	 *   
	 *   <p> <i>Redefinido em cada bean que extende essa classe </i> </p>
	 *   
	 *   <p>N�o � utilizado por nenhuma JSP.</p>
	 *   
	 * @return
	 */
	public abstract String visualizarMateriaisTitulo();
	
	
	
	/**
	 *   <p>Redireciona para a tela de detalhes dos materiais do t�tulo </p>
	 *   
	 *   <p> <i>Redefinido em cada bean que extende essa classe </i> </p>
	 *   
	 *   <p>N�o � utilizado por nenhuma JSP.</p>
	 *   
	 * @return
	 */
	public abstract String telaInformacoesMateriais();
	

	
	
	
	/**
	 *   Verifica qual de qual biblioteca o usu�rio que visualizar os fasc�culos do T�tulo 
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp;
	 * Chamado a partir da p�gina: /sigaa.war/public/biblioteca/paginaDetalhesMateriaisPublica.jsp;	
	 *
	 * @param evt
	 * @throws DAOException
	 */
	public void verificaAlteracaoFiltroBiblioteca(ValueChangeEvent evt) throws DAOException{
		idBibliotecaMateriais = (Integer) evt.getNewValue();
		 carregaDadosMateriais();
	}
	
	
	/**
	 *   Verifica qual de qual ano o usu�rio que visualizar os fasc�culos do T�tulo 
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp;
	 * Chamado a partir da p�gina: /sigaa.war/public/biblioteca/paginaDetalhesMateriaisPublica.jsp;	
	 *
	 * @param evt
	 * @throws DAOException
	 */
	public void verificaAlteracaoFiltroAno(ValueChangeEvent evt) throws DAOException{
		anoPesquisaFasciculos = (Integer) evt.getNewValue();
		carregaDadosMateriais();
	}
	
	
	/**
	 * 
	 *    Retorna todas as bibliotecas internas para o usu�rio escolher de qual biblioteca quer ver 
	 * os fasc�culos. Como a quantidade de fasc�culos geralmente � muito grande, ficava pesado e dif�cil 
	 * de visualizar trazer sempre todos os fasc�culos.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp;
	 * Chamado a partir da p�gina: /sigaa.war/public/biblioteca/paginaDetalhesMateriaisPublica.jsp;	
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getBibliotecasInternas() throws DAOException{
		
		Collection <Biblioteca> b = getDAO(BibliotecaDao.class).findAllBibliotecasInternasAtivas();
		return toSelectItems(b, "id", "descricaoCompleta");
	}
	
	
	
	
	/**
	 *    Retorna os 10 �ltimos anos para o usu�rio escolher de qual ano quer visualizar os fasc�culos. <br/>
	 *    Tem que filtrar por ano por em alguns casos a quantidade de fasc�culos � muito grande ( > 700 ). <br/>
	 *    
	 * <ul>
	 *
	 * <li>Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp; </li>
	 * <li>Chamado a partir da p�gina: /sigaa.war/public/biblioteca/paginaDetalhesMateriaisPublica.jsp;	 </li>
	 * <li>Chamado a partir da p�gina: /sigaa.war/public/biblioteca/paginaDetalhesMateriaisInterna.jsp; </li>
	 *
	 * </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getAnosPesquisaFasciculos() throws DAOException{
		
		
		Collection <SelectItem> anosPesquisa = new ArrayList<SelectItem>();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		
		anosPesquisa.add(new SelectItem(-1, "-- TODOS --"));
		
		// Gera os 20 �ltimos anos para o usu�rio fazer a pesquisa
		for (int i = 0; i < 20; i++) {
			anosPesquisa.add(new SelectItem( c.get(Calendar.YEAR) , ""+c.get(Calendar.YEAR) ) );
			c.add(Calendar.YEAR, -1); // pega o ano enterior
		}
		
		return anosPesquisa;
	}
	
	
	
	
	/**
	 * 
	 * Atualiza os dados da p�gina, quando usu�rio seleciona uma nova p�gina.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @param event
	 * @throws DAOException 
	 */
	public void atualizaDadosPagina(ActionEvent event) throws DAOException{
		
		FasciculoDao daoFasciculo = null;
		ReservaMaterialBibliotecaDao reservaDao = null;
		
		try {

			daoFasciculo = getDAO(FasciculoDao.class);
			reservaDao = getDAO(ReservaMaterialBibliotecaDao.class);
		
			int numeroPaginaAtual = getParameterInt("_numero_pagina_atual");
			
			paginaAtual = numeroPaginaAtual;
			
			if(paginaAtual > quantidadePaginas)
				paginaAtual = quantidadePaginas;
			
			if(paginaAtual <= 0)
				paginaAtual = 1;
			
			if(periodico){
				buscarFasciculos();
			}else{
				buscarExemplares();
			}
		
		} finally{
			if(daoFasciculo != null) daoFasciculo.close();
			
			if(reservaDao != null) reservaDao.close();
		}
		
	}
	
	
	
	/**
	 * 
	 * <p>Retorna uma lista de no m�ximo 10 p�gina que o usu�rio pode percorrer</p>
	 * 
	 * <p>Caso seja permitido, retorna as 5 p�gina anteriores e 4 p�gina posteriores � p�gina atual, mais a pr�pria p�gina atual.</p> 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnivos/pesquisas_acervo/paginaDetalhesMateriais;jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public List<Integer> getPaginas(){
		
		List<Integer> paginas = new ArrayList<Integer>();
		
		int pagina = paginaAtual;
		
		int contador = 0;
		
		while (pagina > 1 && contador < 5) { // Conta a quantidade de p�ginas anteriores a p�gina atual que podem ser mostradas, no limite m�ximo de 5.
			pagina--;
			contador++;
		}
		
		for (; pagina < paginaAtual+( 10-contador ) && pagina <=  quantidadePaginas && quantidadePaginas > 1; pagina++) {
			paginas.add(pagina);
			
		}
		
		return paginas;
	}
	
	
	/**
	 * 
	 * <p>M�todo que carrega todas as informa��es do material selecionado e mostra para o usu�rio na tela padr�o da aplica��o.</p>
	 * 
	 * <p>Para os materiais carrega todas a suas informa��es, suas reservas se existir e a quantidade de empr�stimos realizadas.</p> 
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *      <li>/sigaa.war/biblioteca/processos_tecncios/pesquisa_acervo/paginaDeatalhesMateriais.jsp</li>
	 *      <li>/sigaa.war/biblioteca/processos_tecncios/pesquisa_acervo/paginaDeatalhesMateriaisInterna.jsp</li>
	 *      <li>/sigaa.war/biblioteca/processos_tecncios/pesquisa_acervo/paginaDeatalhesMateriaisPublica.jsp</li>
	 *      <li>/sigaa.war/biblioteca//processos_tecnicos/pesquisas_acervo/pesquisaFasciculo.jsp</li>
	 *   </ul>
	 *
	 * @param event
	 * @throws DAOException
	 */
	public void carregarDetalhesMaterialSelecionado(ActionEvent event) throws DAOException{
		
		long tempo = System.currentTimeMillis();
		ExemplarDao dao = null;
		FasciculoDao daoFasiculo = null;
		EmprestimoDao emprestimoDao = null;
		ReservaMaterialBibliotecaDao reservaDao = null;
		ArtigoDePeriodicoDao artigoDao = null;
		AssinaturaDao assinaturaDao = null;
		MaterialInformacionalDao materialDao = null;
		
		try{
			dao = getDAO(ExemplarDao.class);
			daoFasiculo = getDAO(FasciculoDao.class);
			emprestimoDao = getDAO(EmprestimoDao.class);
			reservaDao = getDAO(ReservaMaterialBibliotecaDao.class);
			artigoDao = getDAO(ArtigoDePeriodicoDao.class);
			assinaturaDao = getDAO(AssinaturaDao.class);
			materialDao = getDAO(MaterialInformacionalDao.class);
			
			int idMaterial = getParameterInt("idMaterialMostrarDetalhes");
			
			//////////     @see pesquisaFasciculo.jsp  //////////
			
			if(getParameterBoolean("isPeriodicoVisualzarDetalhes") == true)
				periodico = true;
			
			if(getParameterBoolean("isMostrarInformacaoTituloDetalhesMaterial") == true)
				mostrarInformacaoTituloDetalhesMaterial = true;
			
			if(periodico){
				materialSelecionado = daoFasiculo.findTodosDadosFasciculo(idMaterial);
				
				asssinaturaDoMaterial = assinaturaDao.findAssinaturaDoFasciculo(materialSelecionado.getId());
				
				if(! materialSelecionado.isDadoBaixa()){
				
					List<Integer> idArtigos = new ArrayList<Integer>();
					List<ArtigoDePeriodico> artigos = ((Fasciculo)materialSelecionado).getArtigos();
					
					if(artigos != null ){
						for (ArtigoDePeriodico artigo : artigos) {
							idArtigos.add(artigo.getId());
						}
					}
					
					artigosDoFasciculoSelecionado = artigoDao.findInformacoesArtigos( idArtigos );
				}
				
			}else{
				materialSelecionado = dao.findTodosDadosExemplar(idMaterial);
			}
			
			
			if(! materialSelecionado.isDadoBaixa()){
			
				if(mostrarInformacaoTituloDetalhesMaterial)
					tituloDoMaterial = BibliotecaUtil.obtemDadosTituloDoMaterial(idMaterial);
				
				qtdEmprestimosMaterialSelecionado = emprestimoDao.countEmprestimosDoMaterial(idMaterial);
				
				if( ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
					reservasDoMaterial = reservaDao.buscasReservasAtivasDoTituloDoMaterialEmOrdem(idMaterial);
				}
			}else{
				Object[] dadosBaixa = materialDao.findInformacoesBaixaMaterial(materialSelecionado.getId());
 				
				materialSelecionado.setMotivoBaixa( (String) dadosBaixa[0]);
				materialSelecionado.setInformacoesTituloMaterialBaixado( (String) dadosBaixa[1] );
				
				Pessoa p = new Pessoa();
				p.setNome( (String) dadosBaixa[2] );
				Usuario u = new Usuario();
				u.setPessoa(p);
				RegistroEntrada r =  new RegistroEntrada();
				r.setUsuario(u);
				
				materialSelecionado.setRegistroUltimaAtualizacao( r );
				materialSelecionado.setDataUltimaAtualizacao( (Date) dadosBaixa[3] );
			}
			
		} finally{
			if(dao != null) dao.close();
			if(daoFasiculo != null) daoFasiculo.close();
			if(emprestimoDao != null) emprestimoDao.close();
			if(reservaDao != null) reservaDao.close();
			if(artigoDao != null) artigoDao.close();
			if(assinaturaDao != null) assinaturaDao.close();
			if(materialDao != null) materialDao.close();
			
		}
		
		System.out.println(">>>>>>>>>>> Consultar todas informa��es do material demorou: "+ (System.currentTimeMillis()-tempo)+" ms");
		
	}
	
	
	
	/**
	 *  M�todo que permite ao usu�rio navegar nos resultados das pesquisas, indo ao pr�ximo resultado.
	 * 
	 * <ul>Utilizado nas seguintes JSP's:
	 * 	<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 * 	<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/paginaDetalhesMateriaisInterna.jsp</li>
	 * 	<li>/sigaa.war/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 */
	public abstract String irProximoResultado() throws DAOException;
	
	
	
	/**
	 *  M�todo que permite ao usu�rio navegar nos resultados das pesquisas, voltando ao resultado anterior.
	 *
	 * <ul>Utilizado nas seguintes JSP's:
	 * 	<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 * 	<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/paginaDetalhesMateriaisInterna.jsp</li>
	 * 	<li>/sigaa.war/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 */
	public abstract String irResultadoAnterior() throws DAOException;

	
	
	/**
	 *  M�todo que permite ao usu�rio navegar nos resultados das pesquisas, indo ao primeiro resultado.
	 *
	 * <ul>Utilizado nas seguintes JSP's:
	 * 	<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 * 	<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/paginaDetalhesMateriaisInterna.jsp</li>
	 * 	<li>/sigaa.war/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 */
	public abstract String irPrimeiraPosicao() throws DAOException;
	
	
	
	/**
	 *  M�todo que permite ao usu�rio navegar nos resultados das pesquisas, indo ao �ltimo resultado.
	 *
	 * <ul>Utilizado nas seguintes JSP's:
	 * 	<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/paginaDetalhesMateriais.jsp</li>
	 * 	<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/paginaDetalhesMateriaisInterna.jsp</li>
	 * 	<li>/sigaa.war/public/biblioteca/paginaDetalhesMateriaisPublica.jsp</li>
	 * </ul>
	 */
	public abstract String irUtimaPosicao() throws DAOException;
	
	
	/**
	 *  Verifica se o usu�rio n�o chegou no final da lista
	 *
	 */
	public abstract boolean getPodeAvancarResultadosPesquisa();
	
	
	/**
	 *  Verifica se o usu�rio n�o chegou no in�cio da lista
	 *
	 */
	public abstract boolean getPodeVoltarResultadosPesquisa();
	
	/**
	 * Redireciona o fluxo de navega��o para a p�gina que iniciou o bean.
	 * 
	 * <ul>Utilizado nas seguintes JSP's:
	 * 	<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/paginaDetalhesMateriaisInterna.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina que inicialmente chamou o bean.
	 */
	public String voltar() {
		return forward(paginaVoltar);
	}
	
	
	
	
	
	//////////////////  sets e gets padr�es  ///////////
	
	public boolean isPeriodico() {
		return periodico;
	}

	public List<Exemplar> getExemplares() {
		return exemplares;
	}

	public List<Fasciculo> getFasciculos() {
		return fasciculos;
	}

	public void setFasciculos(List<Fasciculo> fasciculos) {
		this.fasciculos = fasciculos;
	}	

	public List<Exemplar> getExemplaresPaginados() {
		return exemplaresPaginados;
	}

	public void setExemplaresPaginados(List<Exemplar> exemplaresPaginados) {
		this.exemplaresPaginados = exemplaresPaginados;
	}

	public List<Fasciculo> getFasciculosPaginados() {
		return fasciculosPaginados;
	}

	public void setFasciculosPaginados(List<Fasciculo> fasciculosPaginados) {
		this.fasciculosPaginados = fasciculosPaginados;
	}

	public CacheEntidadesMarc getTituloCache() {
		return tituloCache;
	}

	public void setTituloCache(CacheEntidadesMarc tituloCache) {
		this.tituloCache = tituloCache;
	}

	public void setPeriodico(boolean periodico) {
		this.periodico = periodico;
	}

	/**
	 * 
	 * Utilizando para configurar a propriedade "peri�dico" para visualizar os dados completos de um material fora da p�gina de detalhes do material.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaFasciculos.jsp</li>
	 *   </ul>
	 *
	 * @param periodico
	 */
	public void setPeriodicoStr(String periodico) {
		if(StringUtils.notEmpty(periodico) && "true".equals(periodico))
			this.periodico = true;
		else
			this.periodico = false;
	}
	
	
	/**
	 * 
	 * Utilizando para configurar a propriedade "mostrarInformacoesTitulo" para visualizar os dados do T�tuto do material
	 * na p�gia padr�o de detalhes de um material.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaFasciculos.jsp</li>
	 *   </ul>
	 *
	 * @param periodico
	 */
	public void setMostrarInformacoesTitulo(String mostrarInformacoesTitulo) {
		if(StringUtils.notEmpty(mostrarInformacoesTitulo) && "true".equals(mostrarInformacoesTitulo))
			this.mostrarInformacoesTitulo = true;
		else
			this.mostrarInformacoesTitulo = false;
	}
	
	
	public void setExemplares(List<Exemplar> exemplares) {
		this.exemplares = exemplares;
	}
	
	public Integer getIdBibliotecaMateriais() {
		return idBibliotecaMateriais;
	}

	public void setIdBibliotecaMateriais(Integer idBibliotecaMateriais) {
		this.idBibliotecaMateriais = idBibliotecaMateriais;
	}
	
	public Integer getAnoPesquisaFasciculos() {
		return anoPesquisaFasciculos;
	}

	public void setAnoPesquisaFasciculos(Integer anoPesquisaFasciculos) {
		this.anoPesquisaFasciculos = anoPesquisaFasciculos;
	}

	public List<Assinatura> getAssinaturasTitulo() {
		return assinaturasTitulo;
	}

	public void setAssinaturasTitulo(List<Assinatura> assinaturasTitulo) {
		this.assinaturasTitulo = assinaturasTitulo;
	}



	public Fasciculo getFasciculoSelecionado() {
		return fasciculoSelecionado;
	}



	public void setFasciculoSelecionado(Fasciculo fasciculoSelecionado) {
		this.fasciculoSelecionado = fasciculoSelecionado;
	}



	public Assinatura getAssinatura() {
		return assinatura;
	}



	public void setAssinatura(Assinatura assinatura) {
		this.assinatura = assinatura;
	}



	public CacheEntidadesMarc getArtigo() {
		return artigo;
	}



	public void setArtigo(CacheEntidadesMarc artigo) {
		this.artigo = artigo;
	}



	public List<CacheEntidadesMarc> getListaArtigosDoFasciculo() {
		return listaArtigosDoFasciculo;
	}



	public void setListaArtigosDoFasciculo(List<CacheEntidadesMarc> listaArtigosDoFasciculo) {
		this.listaArtigosDoFasciculo = listaArtigosDoFasciculo;
	}

	public MaterialInformacional getMaterialSelecionado() {
		return materialSelecionado;
	}

	public void setMaterialSelecionado(MaterialInformacional materialSelecionado) {
		this.materialSelecionado = materialSelecionado;
	}

	public int getQtdEmprestimosMaterialSelecionado() {
		return qtdEmprestimosMaterialSelecionado;
	}

	public void setQtdEmprestimosMaterialSelecionado(int qtdEmprestimosMaterialSelecionado) {
		this.qtdEmprestimosMaterialSelecionado = qtdEmprestimosMaterialSelecionado;
	}

	public List<ReservaMaterialBiblioteca> getReservasDoMaterial() {
		return reservasDoMaterial;
	}

	public void setReservasDoMaterial(List<ReservaMaterialBiblioteca> reservasDoMaterial) {
		this.reservasDoMaterial = reservasDoMaterial;
	}

	public int getPaginaAtual() {
		return paginaAtual;
	}

	public void setPaginaAtual(int paginaAtual) {
		this.paginaAtual = paginaAtual;
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

	public List<CacheEntidadesMarc> getArtigosDoFasciculoSelecionado() {
		return artigosDoFasciculoSelecionado;
	}


	public void setArtigosDoFasciculoSelecionado(List<CacheEntidadesMarc> artigosDoFasciculoSelecionado) {
		this.artigosDoFasciculoSelecionado = artigosDoFasciculoSelecionado;
	}

	/** 
	 * Calcula e retorna a posi��o do primeiro material a ser mostrado ao usu�rio. 
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pequisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *   </ul>
	 */
	public int getPrimeiroResultadoPagina(){
		return ((paginaAtual-1)*getQtdResultadosPorPagina())+1;
	}
	
	
	/**
	 * <p>Retorna o indice do �ltimo resultado da p�gina.</p>
	 * 
	 *
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pequisas_acervo/paginaDetalhesMateriais.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getUltimoResultadoPagina() {
		int ultimo = (getPrimeiroResultadoPagina()-1)+getQtdResultadosPorPagina();
		
		if(ultimo >= quantidadeTotalResultados) 
			return quantidadeTotalResultados;
		else
			return ultimo;
	}
	
	
	public Integer getQtdResultadosPorPagina() {
		return QTD_RESULTADOS_POR_PAGINA;
	}

	public boolean isMostrarInformacoesTitulo() {
		return mostrarInformacoesTitulo;
	}



	public void setMostrarInformacoesTitulo(boolean mostrarInformacoesTitulo) {
		this.mostrarInformacoesTitulo = mostrarInformacoesTitulo;
	}

	public CacheEntidadesMarc getTituloDoMaterial() {
		return tituloDoMaterial;
	}

	public void setTituloDoMaterial(CacheEntidadesMarc tituloDoMaterial) {
		this.tituloDoMaterial = tituloDoMaterial;
	}

	public Assinatura getAsssinaturaDoMaterial() {
		return asssinaturaDoMaterial;
	}

	public void setAsssinaturaDoMaterial(Assinatura asssinaturaDoMaterial) {
		this.asssinaturaDoMaterial = asssinaturaDoMaterial;
	}

	public boolean isMostrarInformacaoTituloDetalhesMaterial() {
		return mostrarInformacaoTituloDetalhesMaterial;
	}

	public void setMostrarInformacaoTituloDetalhesMaterial(boolean mostrarInformacaoTituloDetalhesMaterial) {
		this.mostrarInformacaoTituloDetalhesMaterial = mostrarInformacaoTituloDetalhesMaterial;
	}
	
	
	
}
