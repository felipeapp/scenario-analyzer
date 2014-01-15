/*
 * TituloResumidoDTO.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.integracao.dto.biblioteca;

import java.io.Serializable;


/**
 *
 *    Objeto que eh envidado ao sistema remoto (SIPAC) para exibir as informacoes de um titulo. 
 *
 * @author jadson
 * @since 04/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class TituloResumidoDTO implements Serializable, Comparable<TituloResumidoDTO>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5457940031961312151L;

	// guarda uma referencia do titulo completo
	public int idTituloCatalografico;
	
	// O número do sistema gerado que identifica esse título
	private int numeroDoSistema;
	
	// campo 245$a
	public String titulo;
	
	// campos 100$a
	public String autor;
	
	// campo 260$c
	public String ano;

	// campo 250$a
	public String edicao;
	
	// campo 090$a + 090$b + 090$c + 090$d
	public String numeroChamada;
	
	// guarda a quantidade de materiais informacionais que o titulo possui
	private int quantidadeMateriaisInformacionaisAtivos;
	
	/**
	 * 
	 * Primeiro e derradeiro construtor
	 * 
	 * @param id
	 * @param titulo
	 * @param subTitulo
	 * @param autor
	 * @param ano
	 * @param edicao
	 * @param localizacao
	 */
	public TituloResumidoDTO(int idTituloCatalografico, int numeroDoSistema, String titulo, String autor, 
			String ano,  String edicao, String numeroChamada, int quantidadeMateriaisInformacionaisAtivos) {
		
		this.idTituloCatalografico = idTituloCatalografico;
		this.numeroDoSistema = numeroDoSistema;
		
		this.titulo = titulo  != null ? titulo : "";
		this.autor = autor  != null ? autor : "";
		this.ano = ano != null ? ano : "";
		this.edicao = edicao != null ? edicao : "";
		this.numeroChamada = numeroChamada != null ? numeroChamada : "";
		
		this.quantidadeMateriaisInformacionaisAtivos = quantidadeMateriaisInformacionaisAtivos;
		
	}
	
	public int compareTo(TituloResumidoDTO other) {
		return this.numeroChamada.compareTo(other.numeroChamada);
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ano == null) ? 0 : ano.hashCode());
		result = prime * result + ((autor == null) ? 0 : autor.hashCode());
		result = prime * result + ((edicao == null) ? 0 : edicao.hashCode());
		result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TituloResumidoDTO other = (TituloResumidoDTO) obj;
		if (ano == null) {
			if (other.ano != null)
				return false;
		} else if (!ano.equals(other.ano))
			return false;
		if (autor == null) {
			if (other.autor != null)
				return false;
		} else if (!autor.equals(other.autor))
			return false;
		if (edicao == null) {
			if (other.edicao != null)
				return false;
		} else if (!edicao.equals(other.edicao))
			return false;
		if (titulo == null) {
			if (other.titulo != null)
				return false;
		} else if (!titulo.equals(other.titulo))
			return false;
		return true;
	}

	// para uso do JSF
	

	public String getTitulo() {
		return titulo;
	}

	
	public int getIdTituloCatalografico() {
		return idTituloCatalografico;
	}

	public int getNumeroDoSistema() {
		return numeroDoSistema;
	}

	public String getNumeroChamada() {
		return numeroChamada;
	}

	public String getAutor() {
		return autor;
	}

	public String getAno() {
		return ano;
	}

	public String getEdicao() {
		return edicao;
	}

	public int getQuantidadeMateriaisInformacionaisAtivos() {
		return quantidadeMateriaisInformacionaisAtivos;
	}

	
	
	
}
