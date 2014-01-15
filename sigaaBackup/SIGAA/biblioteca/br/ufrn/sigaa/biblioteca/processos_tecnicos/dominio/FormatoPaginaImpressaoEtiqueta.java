/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/05/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *   Define o formato de impressão de uma etiqueta.
 *
 * Autor: freddcs
 * 
 * @version 1.0 criacao da classe
 * @since 15/05/2009
 */
public class FormatoPaginaImpressaoEtiqueta implements Serializable{
	 
	
	/** Identifica o formato a ser gerado */
	private int id;

	
	/** Texto que mostrada ao usuário a descrição do formato da página utilizado. ex: Número 27 (63,5x31 cm) 3x8 ...*/
	private String label;
	
	
	/** Guarda a quantidade de etiqueta por linha que o usuário vai imprimir */
	private int quantidadeEtiquetasPorLinha;

	 /** Guarda o nome do arquivo jasper que vai ser usado para gerar o pdf. */
	private String arquivoJasperUtilizado;
	
	
	/** Guarda os tipos de etiquestas que esse formato de página suporta. Por exemplo: código de barras, lombada, etc.. */
	private List <TipoEtiquetaImpressaoBiblioteca> tipoEtiquetasSuportadasPagina = new ArrayList <TipoEtiquetaImpressaoBiblioteca> ();
	
	
	
	/**
	 * Cria um formato de etiqueta não valido vazio.
	 */
	public FormatoPaginaImpressaoEtiqueta(){
		
	}
	
	
	/**
	 * Construtor padrão.
	 * 
	 * Ao criar o formato da página você deve informar qual o arquivo que vai gerar esse formato e quais os 
	 * tipos de formatos que esse arquivo jarper suporta.
	 * 
	 * @param id
	 * @param label
	 * @param arquivo
	 * @param quantidadeEtiquetasPorPagina
	 * @param etiquetasPossiveis
	 */
	public FormatoPaginaImpressaoEtiqueta(int id, String label, String arquivoJasperUtilizado, int quantidadeEtiquetasPorLinha,
			List<TipoEtiquetaImpressaoBiblioteca> etiquetasPossiveis) {
		this.id = id;
		this.label = label;
		this.arquivoJasperUtilizado = arquivoJasperUtilizado;
		this.quantidadeEtiquetasPorLinha = quantidadeEtiquetasPorLinha;
		this.tipoEtiquetasSuportadasPagina = etiquetasPossiveis;
	}
	
	
	
	
	// sets e gets
	
	public int getId() {
		return id;
	}


	public String getArquivoJasperUtilizado() {
		return arquivoJasperUtilizado;
	}


	public int getQuantidadeEtiquetasPorLinha() {
		return quantidadeEtiquetasPorLinha;
	}


	public List<TipoEtiquetaImpressaoBiblioteca> getTipoEtiquetasSuportadasPagina() {
		return tipoEtiquetasSuportadasPagina;
	}


	public String getLabel() {
		return label;
	}


	public void setId(int id) {
		this.id = id;
	}


	public void setLabel(String label) {
		this.label = label;
	}


	public void setQuantidadeEtiquetasPorLinha(int quantidadeEtiquetasPorLinha) {
		this.quantidadeEtiquetasPorLinha = quantidadeEtiquetasPorLinha;
	}


	public void setArquivoJasperUtilizado(String arquivoJasperUtilizado) {
		this.arquivoJasperUtilizado = arquivoJasperUtilizado;
	}


	public void setTipoEtiquetasSuportadasPagina(
			List<TipoEtiquetaImpressaoBiblioteca> tipoEtiquetasSuportadasPagina) {
		this.tipoEtiquetasSuportadasPagina = tipoEtiquetasSuportadasPagina;
	}

	
	
}