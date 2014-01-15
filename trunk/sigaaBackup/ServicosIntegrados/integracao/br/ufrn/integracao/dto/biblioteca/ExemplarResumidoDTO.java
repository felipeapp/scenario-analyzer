/*
 * ExemplarResumidoDTO.java
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
 *     Transporta os dados de um exemplar ou fasciculoentre o SIGAA e o SIPAC. 
 *     Eh resumido porque nao precisa mostrar todas as informacoes do exemplar no SIPAC, la acho 
 * que so interresa saber a quantidade de exemplares de um titulo e em que biblioteca está.
 *
 * @author jadson
 * @since 04/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class ExemplarResumidoDTO  implements Serializable, Comparable<ExemplarResumidoDTO>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3651773128261108825L;

	public String codigoBarras;
	
	public String localizacao;
	
	public String descricaoBiblioteca;
	
	
	public int compareTo(ExemplarResumidoDTO o) {
		return this.codigoBarras.compareTo(o.codigoBarras);
	}

	// para uso do JSF
	
	public String getCodigoBarras() {
		return codigoBarras;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public String getDescricaoBiblioteca() {
		return descricaoBiblioteca;
	}

	
	
}
