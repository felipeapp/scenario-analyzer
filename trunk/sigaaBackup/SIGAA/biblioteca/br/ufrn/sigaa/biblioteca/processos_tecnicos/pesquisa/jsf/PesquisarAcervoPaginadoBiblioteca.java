/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> <i> Contém os métodos e campos padrão utilizados na páginação </i> </p>
 * 
 * @author jadson
 *
 */
public abstract class PesquisarAcervoPaginadoBiblioteca extends SigaaAbstractController<Object> {
	
	/** Guarda o valor do campo escolhido pelo usuário para ordenar a consulta */
	protected int valorCampoOrdenacao;
	
	
	/** Guarda a quantidade de resultados por página que o usuário deseja ver na consulta*/
	protected int quantideResultadosPorPagina = 25;
	
	/** A quantidade total de resultados que serão páginados */
	protected int quantidadeTotalResultados = 0;
	
	/** A quantidade total de páginas da páginação */
	protected int quantidadePaginas = 1;
	
	/** A página atual que está sendo mostrada ao usuário*/
	protected int paginaAtual = 1;
	
	/**  
	 * Os resultados da consulta, que serão páginados para visualização do usuário.  
	 */
	protected List<CacheEntidadesMarc> resultadosBuscados = new ArrayList<CacheEntidadesMarc>();
	
	/**  Guarda os artigos que serão visualizados pelos usuários no momento
	 *   Utilizado para diminuir a quantidade de resultados exibidos ao usuário.
	 *   A paginação ocorre em memória porque as consultas geralmente são rápidas, o que demora mais é redenrizar uma quantidade muito grande para o usuário.  
	 */
	protected List<CacheEntidadesMarc> resultadosPaginadosEmMemoria = new ArrayList<CacheEntidadesMarc>();
	
	
	/** Retorna a lista de página que o usuário pode percorrer na consulta */
	public final List<Integer> getListaPaginasVisiveis(){
		return PaginacaoBibliotecaUtil.getListaPaginasVisiveis(paginaAtual, quantidadePaginas);
	}
	
	
	/** método que realiza a paginação dos resultados da consulta de títulos no acervo */
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
	 *  Chamado a partir da página: 
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
		
	
		// Registra a consulta dos Títulos que estão na página que o usuário está visualizando no momento ///
		RegistraEstatisticasBiblioteca.getInstance().registrarTitulosConsultados(resultadosPaginadosEmMemoria);
		
	
	}
	
	/** Retorna a descrição da página no formato:  '25 a 50 de 1000 ' */
	public final String getDescricaoPaginacao(){
		int resultadosDaPagina = paginaAtual * quantideResultadosPorPagina;
		
		if(resultadosDaPagina > quantidadeTotalResultados)
			resultadosDaPagina = quantidadeTotalResultados;
		return (  (paginaAtual-1) * quantideResultadosPorPagina+1)+" a "+resultadosDaPagina+" de "+quantidadeTotalResultados; 	
	}
	

	/**
	 * 
	 * Retorna a quantidade de resultados por página que o usuário pode escolher para serem mostrados na pesquisa de artigos.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	
	/** A classes que extederem essa devem dizer por quais campos as informações poderão ser ordenadas */
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
	 * Retorna a quantidade de artigos mostradas no momento para o usuário
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
