/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 07/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafael Gomes
 *
 */
public class Vestibular {
	
	private List<Inscrito> inscritos = new ArrayList<Inscrito>();
	 
	public void add(Inscrito inscrito) {
		this.inscritos.add(inscrito);
	}
 
	public List<Inscrito> getContent() {
		return this.inscritos;
	}

}
