/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 16/04/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioUsuariosFinalizadosComPedenciasBibliotecaDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.OrdenacaoRelatoriosBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.VinculoUsuarioBiblioteca;

/**
 * <p>Cria um relat�rio para a biblioteca poder visualizar os usu�rios que foram finalizados com ped�ncia de material informacional.</p>
 *
 * <p>Esse usu�rios s�o propr�cios a cobran�a judicial caso n�o devolvam o material. </p>
 *
 * <p>Isso � necess�rio porque o cancelamento de v�nculos n�o ser� mais bloqueado, haja visto que isso seria uma vantagem para o usu�rio.
 * Pegar livros na biblioteca para n�o ter seu v�nculo cancelado na universidade.
 * </p>
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - cria��o da classe.
 * @since 16/04/2013
 *
 */
@Component("relatorioUsuariosFinalizadosComPedenciasBibliotecaMBean")
@Scope("request")
public class RelatorioUsuariosFinalizadosComPedenciasBibliotecaMBean  extends AbstractRelatorioBibliotecaMBean{

	/**
	 * A p�gina do relat�rio propriamente dito, onde os dados s�o mostrados
	 */
	public static final String PAGINA_RELATORIO = "/biblioteca/controle_estatistico/relatorioUsuariosFinalizadosComPendenciasBiblioteca.jsp";
	
	
	/** Resultado da consulta de discentes */
	private List<Object[]> resultadosDiscente;
	
	/** Resultado da consulta de servidores */
	private List<Object[]> resultadosServidor;
	
	/** Resultado da consulta de biblioteca */
	private List<Object[]> resultadosBiblioteca;
	
	/**
	 * Construtor obrigat�rio para que usa o abstract relat�rio da biblioteca.
	 */
	public RelatorioUsuariosFinalizadosComPedenciasBibliotecaMBean(){
		super.configuraMBeanRelatorio(this);
	}
	
	
	/**
	 * Configura os filtros do relat�rio.
	 */
	@Override
	public void configurar() {
		setTitulo("Relat�rio de Usu�rios Finalizados com Pend�ncias na Biblioteca");
		
		String instituicao = RepositorioDadosInstitucionais.getSiglaInstituicao();
		
		setDescricao("<p> Nesta p�gina � poss�vel emitir um relat�rio anal�tico de usu�rios que foram finalizados com pend�ncias na biblioteca. </p>"+
				" <br/>"+
				" <p> O sistema bloqueia a conclus�o de alunos com pend�ncias na biblioteca por�m, o cancelamento do v�nculo n�o pode ser bloqueado, pois isso geraria uma vantagem " +
				"para o aluno inadimplente. Bastaria o discente tomar emprestado um material da biblioteca e n�o devolver para ficar eternamente com um v�nculo ativo na "+instituicao+".</p>"+
				"<br/>"+
				"<p>Nesses casos a responsabilidade ficar a cargo da biblioteca de tomar as provid�ncias legais cab�veis.</p>"+
				"<br/>"
				);
		
		setFiltradoPorVariasBibliotecas(true);
		setFiltradoPorVariasCategoriasDeUsuario(true);
		setCampoBibliotecaObrigatorio(false);
		setFiltradoPorOrdenacao(true);
		setOrdenacao( OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME.getValor() );
	}
	
	

	/**
	 * Realiza a consulta do relat�rio.
	 */
	@Override
	public String gerarRelatorio() throws DAOException, SegurancaException {
		
		RelatorioUsuariosFinalizadosComPedenciasBibliotecaDao dao = getDAO(RelatorioUsuariosFinalizadosComPedenciasBibliotecaDao.class);
		configuraDaoRelatorio(dao);
		
		resultadosDiscente = new ArrayList<Object[]>();
		resultadosServidor = new ArrayList<Object[]>();;
		resultadosBiblioteca = new ArrayList<Object[]>();;
		
		resultadosDiscente = dao.findDiscenteFinalizadosComPendenciaNaBiblioteca( UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasCategoriasDeUsuario), OrdenacaoRelatoriosBiblioteca.getOrdenacao(ordenacao) );
		resultadosServidor = dao.findServidoresFinalizadosComPendenciaNaBiblioteca( UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasCategoriasDeUsuario), OrdenacaoRelatoriosBiblioteca.getOrdenacao(ordenacao));
		resultadosBiblioteca = dao.findBibliotecasFinalizadosComPendenciaNaBiblioteca( UFRNUtils.toInteger(variasBibliotecas), UFRNUtils.toInteger(variasCategoriasDeUsuario), OrdenacaoRelatoriosBiblioteca.getOrdenacao(ordenacao));
		
		if (getTotalResultados() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		
		// troca os valores dos v�nculos dos usu�rios pela descri��o teta de "n"
		for (Object[] resultado : resultadosDiscente) {
			resultado[1] = VinculoUsuarioBiblioteca.getVinculo( (Integer)resultado[1] ).getDescricao();
		}
		for (Object[] resultado : resultadosServidor) {
			resultado[1] = VinculoUsuarioBiblioteca.getVinculo( (Integer)resultado[1] ).getDescricao();
		}
		for (Object[] resultado : resultadosBiblioteca) {
			resultado[1] = VinculoUsuarioBiblioteca.getVinculo( (Integer)resultado[1] ).getDescricao();
		}
		
		return forward(PAGINA_RELATORIO);
	}
	
	
	public int getTotalResultados() {
		return resultadosDiscente.size()+resultadosServidor.size()+resultadosBiblioteca.size();
	}
	

	@Override
	public Collection<SelectItem> getOrdenacaoComboBox() {
		ordenacaoItens = new ArrayList<SelectItem>();
		ordenacaoItens.add( new SelectItem(OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME.getValor(), OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_NOME.getAlias()));
		ordenacaoItens.add( new SelectItem(OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_PRAZO.getValor(), OrdenacaoRelatoriosBiblioteca.ORDENADO_POR_PRAZO.getAlias()));
		return ordenacaoItens;
	}


	
	public List<Object[]> getResultadosDiscente() {	return resultadosDiscente;}
	public void setResultadosDiscente(List<Object[]> resultadosDiscente) { this.resultadosDiscente = resultadosDiscente;}
	public List<Object[]> getResultadosServidor() {return resultadosServidor;}
	public void setResultadosServidor(List<Object[]> resultadosServidor) {this.resultadosServidor = resultadosServidor;}
	public List<Object[]> getResultadosBiblioteca() {return resultadosBiblioteca;}
	public void setResultadosBiblioteca(List<Object[]> resultadosBiblioteca) {this.resultadosBiblioteca = resultadosBiblioteca;}



}
