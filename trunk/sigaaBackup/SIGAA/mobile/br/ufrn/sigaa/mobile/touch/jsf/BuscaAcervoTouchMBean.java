package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.EmprestimoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ExemplarDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.FormatoMaterialDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.GeraPesquisaTextualFactory;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.RegistraEstatisticasBiblioteca;
import br.ufrn.sigaa.biblioteca.util.AssinaturaByUnidadeDestinoCodigoComparator;
import br.ufrn.sigaa.biblioteca.util.PaginacaoBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;

/**
 * MBean responsável pela consulta de acervo da biblioteca no SIGAA Mobile. 
 * 
 * @author Bernardo Ferreira
 */

@Component("buscaAcervoTouch") @Scope("session")
public class BuscaAcervoTouchMBean extends SigaaTouchAbstractController<CacheEntidadesMarc> {
	
	/** Define o número de registro a ser mostrado por página. */
	private static final int REGISTROS_POR_PAGINA = 25;
	
	/** Campos da busca */
	private String titulo, assunto, autor, editora;
	
	/** Armazena os IDs das bibliotecas a serem pesquisadas */
	private List<Integer> idsBibliotecasAcervoPublico;
	
	/** Número de Páginas de Exemplares */
	private int numPaginasExemplares;

	/** Indica a quantidade total de exemplares */
	private int qtdTotalExemplares;
	
	/** Lista com os exemplares encontrados */
	private List<Exemplar> exemplares;
	
	/** Lista com os fascículos encontrados */
	private List<Fasciculo> fasciculos;

	/** Indica se o material é periódico */
	private boolean periodico;

	/** Indica os IDs de assinaturas */
	private ArrayList<Integer> idsAssinaturaBusca;
	
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
	
	
	public BuscaAcervoTouchMBean(){
		numPaginasExemplares = 1;
		
		obj = new CacheEntidadesMarc();
	}
	
	/** 
	 * Encaminha o usuário para a tela de busca de acervo.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/principal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarBusca() {
		paginaAtual = 1;
		
		return forward("/mobile/touch/public/busca_acervo.jsf");
	}
	
	/** 
	 * 
	 * Realiza a busca de acervo.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/busca_acervo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String buscarAcervo() throws DAOException {
		if (isEmpty(titulo) && isEmpty(autor) && isEmpty(assunto) && isEmpty(editora)) {
			addMensagemErro("Especifique um parâmetro para realizar a busca.");
			return null;
		}
		
		GeraPesquisaTextual geradorPesquisa = new GeraPesquisaTextualFactory().getGeradorPesquisaTextual();
		
		TituloCatalograficoDao daoTitulo = getDAO(TituloCatalograficoDao.class);
		
		try {
			resultadosBuscados = daoTitulo.buscaMultiCampoPublica(geradorPesquisa, CampoOrdenacaoConsultaAcervo.TITULO, titulo, assunto, autor, null, editora, null, null, null, null, null, false, getIdsBibliotecasAcervoPublico());
			geraResultadosPaginacao();
			
			if (getQuantidadeTotalResultados() == 0) {
				addMensagemErro("Nenhum resultado foi encontrado de acordo com os critérios de busca informados.");
				return null;
			}
			
			if (TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_PUBLICA.compareTo(getQuantidadeTotalResultados()) <= 0){
				addMensagemWarning("A busca resultou em um número muito grande de resultados. Somente os "+TituloCatalograficoDao.LIMITE_BUSCA_TITULOS_PUBLICA+" primeiros estão sendo mostrados.");
			}
			
		} finally{
			if(daoTitulo != null) daoTitulo.close();
		}

		return forwardListaAcervo();
	}
	
	/** 
	 * 
	 * Visualização dos materiais de determinado título.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/lista_acervo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String view() throws ArqException {
		setId();
		
		if (obj.getId() > 0) {
			obj = getGenericDAO().findByPrimaryKey(obj.getId(),CacheEntidadesMarc.class);
			
			carregarExemplares();
		} else {
			addMensagemErro("Título não selecionado");
			return null;
		}
		
		return forward("/mobile/touch/public/view_acervo.jsf");
	}
	
	/** 
	 * 
	 * Realiza o carregamento de mais exemplares para ser exibido ao usuário.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/lista_acervo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	private void carregarExemplares() throws DAOException {
		AssinaturaDao daoAssinatura = null;
		FormatoMaterialDao formatoDao = null;
		ExemplarDao daoExemplares = null;
		FasciculoDao daoFasciculo = null;
		
		try {
			daoAssinatura = getDAO(AssinaturaDao.class);
			formatoDao = getDAO(FormatoMaterialDao.class);
			
			// Para títulos não catalogados o formato pode ser nulo, mas esses título não podem pussuir materiais.
			FormatoMaterial formatoMaterial = formatoDao.findFormatoMaterialDoTitulo(obj.getIdTituloCatalografico()); 

			periodico = formatoMaterial != null && formatoMaterial.isFormatoPeriodico();

			if(periodico){
				List<Assinatura> assinaturasTitulo = daoAssinatura.findAssinaturasAtivasByTituloByBibliotecas(obj.getIdTituloCatalografico(), getIdsBibliotecasAcervoPublico());
				
				Collections.sort(assinaturasTitulo, new AssinaturaByUnidadeDestinoCodigoComparator());
				
				idsAssinaturaBusca = new ArrayList<Integer>();
	
				for (Assinatura assi : assinaturasTitulo) {
					idsAssinaturaBusca.add(assi.getId());
				}
				
				daoFasciculo = getDAO(FasciculoDao.class);
				
				qtdTotalExemplares = daoFasciculo.countTodosFasciculosAtivosDasAssinaturas(idsAssinaturaBusca, -1, true);

				buscarFasciculos(true);
			} else {
				daoExemplares = getDAO(ExemplarDao.class);
				
				qtdTotalExemplares = daoExemplares.countTodosExemplaresAtivosDoTituloComDadosByBiblioteca(obj.getIdTituloCatalografico(), getIdsBibliotecasAcervoPublico(), true, null);
				
				buscarExemplares(true);	
			}
			
		} finally{
			if(formatoDao != null)
				formatoDao.close();
			if(daoAssinatura != null)
				daoAssinatura.close();
			if(daoExemplares != null)
				daoExemplares.close();
		}
	}
	
	/** 
	 * 
	 * Realiza a busca por exemplares do material.
	 * 
	 */
	private void buscarExemplares(boolean limparLista) throws DAOException{
		ExemplarDao daoExemplares = null;
		EmprestimoDao dao = null;
		ReservaMaterialBibliotecaDao reservaDao = null;
		
		try{
			daoExemplares = getDAO(ExemplarDao.class);
			dao = getDAO(EmprestimoDao.class);
			reservaDao = getDAO(ReservaMaterialBibliotecaDao.class);
			
			if(exemplares == null || limparLista)
				exemplares = new ArrayList<Exemplar>();
			
			// Busca apenas uma vez os exemplares e mantém eles na memória quando o usuário percorre as páginas //
			exemplares.addAll(daoExemplares.findAllExemplarAtivosDoTituloComDadosByBiblioteca(obj.getIdTituloCatalografico(), getIdsBibliotecasAcervoPublico(), 
					true, null, numPaginasExemplares, REGISTROS_POR_PAGINA));
		
			if(exemplares.size() > 0){
				// Para cada exemplares recuperado, busca as data de devolução e conclusão da reserva //
				for (Exemplar exemplar : exemplares){
					
					if(ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
						if (exemplar.isDisponivel()) {
							exemplar.setPrazoConcluirReserva(reservaDao.findPrazoConcluirReservasEmEsperaTitulo(obj.getId()));
						}
					}
					
					if (exemplar.isEmprestado()) {						
						exemplar.setPrazoEmprestimo(dao.findPrazoDevolucaoMaterial(exemplar.getId()));														
					}
				}
			}
			
		} finally {
			if (dao != null)
				dao.close();
			if (daoExemplares != null)
				daoExemplares.close();
			if (reservaDao != null)
				reservaDao.close();
		}			
	}
	
	/** 
	 * 
	 * Realiza a busca por fascículos.
	 * 
	 */
	private void buscarFasciculos(boolean limparLista) throws DAOException{
		FasciculoDao daoFasciculo = null;
		ReservaMaterialBibliotecaDao reservaDao = null;
		EmprestimoDao dao = null;
		
		try{
			daoFasciculo = getDAO(FasciculoDao.class);
			reservaDao = getDAO(ReservaMaterialBibliotecaDao.class);

			List<Fasciculo> fasciculosPaginados = daoFasciculo.findTodosFasciculosAtivosDasAssinaturas(idsAssinaturaBusca, -1, true, 
					numPaginasExemplares, REGISTROS_POR_PAGINA);
			
			if(fasciculosPaginados.size() > 0){
				dao = getDAO(EmprestimoDao.class);
				// Para cada fasciculos recuperado, busca as data de devolução e conclusão da reserva //	
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
			
			if(fasciculos == null || limparLista)
				fasciculos = new ArrayList<Fasciculo>();
			
			fasciculos.addAll(fasciculosPaginados);
		} finally {
			if (daoFasciculo != null)
				daoFasciculo.close();
			if (dao != null)
				dao.close();
			if (reservaDao != null)
				reservaDao.close();
		}
	}
	
	/** 
	 * 
	 * Realiza o carregamento de mais exemplares para ser exibido ao usuário.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_acervo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String carregarMaisExemplares() throws DAOException {
		numPaginasExemplares++;
		
		if(isPeriodico())
			buscarFasciculos(false);
		else
			buscarExemplares(false);
		
		return null;
	}
	
	/** 
	 * Obtém os IDs das bibliotecas a serem consideradas na busca.
	 */
	private List<Integer> getIdsBibliotecasAcervoPublico() throws DAOException {
		if (idsBibliotecasAcervoPublico == null) {
			BibliotecaDao dao = getDAO(BibliotecaDao.class);
			
			try {
				idsBibliotecasAcervoPublico = dao.findIdsBibliotecaAcervoPublico();
			} finally {
				dao.close();
			}
		}
		
		return idsBibliotecasAcervoPublico;
	}
	
	/** 
	 * 
	 * Encaminha o usuário à tela de listagem de acervo.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/mobile/touch/public/view_acervo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String forwardListaAcervo() {
		return forward("/mobile/touch/public/lista_acervo.jsf");
	}
	
	/** 
	 * 
	 * Obtém os tipos de consulta que podem ser realizadas.
	 * 
	 * Método não chamado por JSP(s).
	 * 
	 * @return
	 */
	public Collection<SelectItem> getTiposConsultaCombo() {
		Collection<SelectItem> tipos = new ArrayList<SelectItem>();
		
		tipos.add(new SelectItem('T', "Títulos"));
		tipos.add(new SelectItem('A', "Artigos"));
		
		return tipos;
	}
	
	/**
	 * Verifica se a lista de exemplares/fascículos ainda tem resultados não mostrados.
	 * 
	 * @return
	 */
	public boolean isMaterialComMaisExemplares() {
		return (numPaginasExemplares * REGISTROS_POR_PAGINA) < qtdTotalExemplares;
	}

	public List<Exemplar> getExemplares() {
		return exemplares;
	}

	public void setExemplares(List<Exemplar> exemplares) {
		this.exemplares = exemplares;
	}

	public List<Fasciculo> getFasciculos() {
		return fasciculos;
	}

	public void setFasciculos(List<Fasciculo> fasciculos) {
		this.fasciculos = fasciculos;
	}

	public int getQtdTotalExemplares() {
		return qtdTotalExemplares;
	}

	public void setQtdTotalExemplares(int qtdTotalExemplares) {
		this.qtdTotalExemplares = qtdTotalExemplares;
	}

	public boolean isPeriodico() {
		return periodico;
	}

	public void setPeriodico(boolean periodico) {
		this.periodico = periodico;
	}

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
		
		return forwardListaAcervo();
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
