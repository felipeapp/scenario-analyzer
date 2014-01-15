/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 04/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.RegistraEstatisticasBiblioteca;
import br.ufrn.sigaa.biblioteca.util.PaginacaoBibliotecaUtil;

/**
 *
 * <p> Classe que deve ser extendida por quem deseja realizar a pesquisa paginada do acervo para a biblioteca.</p>
 *
 * <p> <i> Cont�m os m�todos e campos padr�o utilizados na p�gina��o </i> </p>
 * 
 * @author jadson
 *
 */
public abstract class PesquisarAcervoPaginadoBiblioteca extends SigaaAbstractController<Object> {
	
	/** Guarda o valor do campo escolhido pelo usu�rio para ordenar a consulta */
	protected int valorCampoOrdenacao;
	
	
	/** Guarda a quantidade de resultados por p�gina que o usu�rio deseja ver na consulta*/
	protected int quantideResultadosPorPagina = 25;
	
	/** A quantidade total de resultados que ser�o p�ginados */
	protected int quantidadeTotalResultados = 0;
	
	/** A quantidade total de p�ginas da p�gina��o */
	protected int quantidadePaginas = 1;
	
	/** A p�gina atual que est� sendo mostrada ao usu�rio*/
	protected int paginaAtual = 1;
	
	/**  
	 * Os resultados da consulta, que ser�o p�ginados para visualiza��o do usu�rio.  
	 */
	protected List<CacheEntidadesMarc> resultadosBuscados = new ArrayList<CacheEntidadesMarc>();
	
	/**  Guarda os artigos que ser�o visualizados pelos usu�rios no momento
	 *   Utilizado para diminuir a quantidade de resultados exibidos ao usu�rio.
	 *   A pagina��o ocorre em mem�ria porque as consultas geralmente s�o r�pidas, o que demora mais � redenrizar uma quantidade muito grande para o usu�rio.  
	 */
	protected List<CacheEntidadesMarc> resultadosPaginadosEmMemoria = new ArrayList<CacheEntidadesMarc>();
	
	
	/** Retorna a lista de p�gina que o usu�rio pode percorrer na consulta */
	public final List<Integer> getListaPaginasVisiveis(){
		return PaginacaoBibliotecaUtil.getListaPaginasVisiveis(paginaAtual, quantidadePaginas);
	}
	
	
	/** m�todo que realiza a pagina��o dos resultados da consulta de t�tulos no acervo */
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
	 *  Chamado a partir da p�gina: 
	 * 	<ul>
	 * 		<li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPaginacaoConsultaAcervo.jsp</li>
	 *  </ul>
	 */
	public void atualizaResultadosPaginacao(ActionEvent event) throws DAOException{
		
		int numeroPaginaAtual = getParameterInt("_numero_pagina_atual");
		
		paginaAtual = numeroPaginaAtual;
		
		if(paginaAtual > quantidadePaginas)
			paginaAtual = quantidadePaginas;
		
		if(paginaAtual <= 0)
			paginaAtual = 1;
		
		resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
		
	
		// Registra a consulta dos T�tulos que est�o na p�gina que o usu�rio est� visualizando no momento ///
		RegistraEstatisticasBiblioteca.getInstance().registrarTitulosConsultados(resultadosPaginadosEmMemoria);
		
	
	}
	
	/** Retorna a descri��o da p�gina no formato:  '25 a 50 de 1000 ' */
	public final String getDescricaoPaginacao(){
		int resultadosDaPagina = paginaAtual * quantideResultadosPorPagina;
		
		if(resultadosDaPagina > quantidadeTotalResultados)
			resultadosDaPagina = quantidadeTotalResultados;
		return (  (paginaAtual-1) * quantideResultadosPorPagina+1)+" a "+resultadosDaPagina+" de "+quantidadeTotalResultados; 	
	}
	

	/**
	 * 
	 * Retorna a quantidade de resultados por p�gina que o usu�rio pode escolher para serem mostrados na pesquisa de artigos.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaArtigo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public Collection<SelectItem> getQtdResultadosPorPaginaComboBox(){
		
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		lista.add(new SelectItem(25, "25"));
		lista.add(new SelectItem(50, "50"));
		lista.add(new SelectItem(100, "100"));
		
		return lista;
	}
	
	/** A classes que extederem essa devem dizer por quais campos as informa��es poder�o ser ordenadas */
	public abstract Collection<SelectItem> getCampoOrdenacaoResultadosComboBox();
	
	
	/**
	 * Limpa apenas os resultados da pesquisa
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
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaArtigo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/paginaPesquisaAutoridade.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public int getQuantidadeResultadosMostrados(){
		if( resultadosPaginadosEmMemoria != null)
			return resultadosPaginadosEmMemoria.size();
		else return 0;
	}
	
	public int getValorCampoOrdenacao() {
		return valorCampoOrdenacao;
	}

	public void setValorCampoOrdenacao(int valorCampoOrdenacao) {
		this.valorCampoOrdenacao = valorCampoOrdenacao;
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
	
	
}
