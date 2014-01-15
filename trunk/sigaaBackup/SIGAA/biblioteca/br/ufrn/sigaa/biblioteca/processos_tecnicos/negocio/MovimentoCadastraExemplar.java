/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 *    Movimento para cadastrar um novo exemplar, podem ser passados vários exemplares 
 * de uma única vez, se o usuário decidir que eles são iguais so mudando o numero de patrimônio.
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
