/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on  29/04/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;

/**
 *
 *     Passa os dados para o processador que cataloga autoridades. 
 *
 * @author jadson
 * @since 29/04/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoCatalogaAutoridade extends AbstractMovimentoAdapter{

	/** A autoridade que vai ser criada. */
	private Autoridade autoridade;
	
	/**
	 *  Utilizado para quando o usuário for finalizar a catalogação. Então atribui o atributo catalogado para true e 
	 * valida se a autoridade possui os campos obrigatorios
	 */
	private boolean finalizandoCatalogacao;
	
	
	public MovimentoCatalogaAutoridade(Autoridade autoridade, boolean finalizandoCatalogacao){
		this.autoridade = autoridade;
		this.finalizandoCatalogacao = finalizandoCatalogacao;
	}

	public Autoridade getAutoridade() {
		return autoridade;
	}


	public boolean isFinalizandoCatalogacao() {
		return finalizandoCatalogacao;
	}
}
