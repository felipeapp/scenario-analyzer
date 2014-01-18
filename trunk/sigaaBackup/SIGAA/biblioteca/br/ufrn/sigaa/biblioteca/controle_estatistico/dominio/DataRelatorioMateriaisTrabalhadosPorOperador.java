/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 27/04/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import br.ufrn.arq.util.StringUtils;

/**
 * 
 * <p> Classe que armazena temporariamente os dados retornados pelo relatório de materiais trabalhados por operador.</p>
 *
 * <p> <i> (Serve para organizar melhor os dados antes de mostrar para o usuário ) </i> </p>
 * 
 * @author jadson
 *
 */
public class DataRelatorioMateriaisTrabalhadosPorOperador implements Comparable<DataRelatorioMateriaisTrabalhadosPorOperador>{

	/** Nome da pessoa */
	private String nomePessoa;
	/** Código de barras */
	private String codigoBarras;
	/** Classificacação 1 */
	private String classificacao1;
	/** Classificacação 2 */
	private String classificacao2;
	/** Classificacação 3 */
	private String classificacao3;
	/** Número do sistema */
	private Integer numerodoSistema;
	/** Título */
	private String titulo;
	/** Autor */
	private String autor;
	/** Edição */
	private String edicao;
	/** Ano */
	private String ano;
	/** Chamada */
	private String chamada;
	/** Biblioteca */
	private String  descricaoBiblioteca;
	/** Data de criação */
	private Date  dataCriacao;
	
	/**
	 * Construtor padrão.
	 * 
	 * @param nomePessoa
	 * @param codigoBarras
	 * @param classeCDU
	 * @param classeBlack
	 * @param numerodoSistema
	 * @param titulo
	 * @param autor
	 * @param edicao
	 * @param ano
	 * @param chamada
	 * @param descricaoBiblioteca
	 * @param dataCriacao
	 */
	public DataRelatorioMateriaisTrabalhadosPorOperador(String nomePessoa, String codigoBarras,
			String classificacao1, String classificacao2, String classificacao3, 
			Integer numerodoSistema, String titulo, String autor, 
			String edicao, String ano, String chamada, String descricaoBiblioteca,
			Date dataCriacao) {
		this.nomePessoa = nomePessoa;
		this.codigoBarras = codigoBarras;
		this.classificacao1 = classificacao1;
		this.classificacao2 = classificacao2;
		this.classificacao3 = classificacao3;
		this.numerodoSistema = numerodoSistema;
		this.titulo = titulo;
		this.autor = autor;
		this.edicao = edicao;
		this.ano = ano;
		this.chamada = chamada;
		this.descricaoBiblioteca = descricaoBiblioteca;
		this.dataCriacao = dataCriacao;
	}



	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(DataRelatorioMateriaisTrabalhadosPorOperador other) {
		
		int resultado = 0;
		
		// o de títulos não pode ordenar por biblioteca, porque título não tem biblioteca //
		if(StringUtils.notEmpty(this.descricaoBiblioteca) &&  StringUtils.notEmpty(other.descricaoBiblioteca))
			resultado = this.descricaoBiblioteca.compareTo(other.descricaoBiblioteca);
		
			
		if(resultado != 0 ) // de bibliotecas diferêntes
			return resultado;
		
		if(this.dataCriacao != null && other.dataCriacao != null)
			return this.dataCriacao.compareTo(other.dataCriacao);
		else
			return this.codigoBarras.compareTo(other.codigoBarras);
	}

	/// sets e gets //

	public String getCodigoBarras() {
		return codigoBarras;
	}

	public Integer getNumerodoSistema() {
		return numerodoSistema;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getAutor() {
		return autor;
	}

	public String getEdicao() {
		return edicao;
	}

	/**
	 * Retorna o ano formatado.
	 * 
	 * @return
	 */
	public String getAno() {
		
		StringBuilder anoRetorno = new StringBuilder();
		
		List<String> nomesList = new ArrayList<String>();
		
		if(ano != null){
			StringTokenizer tokens = new StringTokenizer(ano, "#$&");
			
			
			while(tokens.hasMoreTokens()){
				
				String ano = tokens.nextToken();
				if(StringUtils.notEmpty(ano) && ! ano.equalsIgnoreCase("null"))
					nomesList.add(ano);
			}
		}
		
		for (String string : nomesList) {
			anoRetorno.append(string+" ");
		}
		
		return anoRetorno.toString();
	}

	public String getChamada() {
		return chamada;
	}

	public String getDescricaoBiblioteca() {
		return descricaoBiblioteca;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public String getClassificacao1() {
		return classificacao1;
	}

	public void setClassificacao1(String classificacao1) {
		this.classificacao1 = classificacao1;
	}
	public String getClassificacao2() {
		return classificacao2;
	}

	public void setClassificacao2(String classificacao2) {
		this.classificacao2 = classificacao2;
	}

	public String getClassificacao3() {
		return classificacao3;
	}

	public void setClassificacao3(String classificacao3) {
		this.classificacao3 = classificacao3;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}	
	
}
