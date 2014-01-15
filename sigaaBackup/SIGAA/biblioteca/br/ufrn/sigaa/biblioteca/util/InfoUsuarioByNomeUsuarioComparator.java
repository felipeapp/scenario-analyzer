/*
 * InfoUsuarioByNomeUsuarioComparator.java
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
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import br.ufrn.sigaa.biblioteca.dominio.InformacoesUsuarioBiblioteca;

/**
 *
 *     Implementa as regras de ordenacao da classe <code>InformacoesUsuarioBiblioteca</code>
 * pelo nome do usuario da biblioteca.
 *
 *
 * @author jadson
 * @since 16/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class InfoUsuarioByNomeUsuarioComparator implements Comparator<InformacoesUsuarioBiblioteca>{

	public int compare(InformacoesUsuarioBiblioteca o1,
			InformacoesUsuarioBiblioteca o2) {
		return o1.getNomeUsuario().compareTo(o2.getNomeUsuario());
	}

}
