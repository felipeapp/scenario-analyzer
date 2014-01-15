/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/03/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *
 *    Movimento para cadastrar um novo exemplar, podem ser passados v�rios exemplares 
 * de uma �nica vez, se o usu�rio decidir que eles s�o iguais so mudando o numero de patrim�nio.
 *
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 criacao da classe
 *
 */

public class MovimentoCadastraExemplar extends AbstractMovimentoAdapter{

	private List<Exemplar> exemplares;
	
	
	public MovimentoCadastraExemplar(List<Exemplar> exemplares) {
		this.exemplares = exemplares;
	}
	
	public List<Exemplar> getExemplares() {
		return exemplares;
	}
	
}
