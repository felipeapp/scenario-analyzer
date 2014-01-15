/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 * 
 * Created on 12/12/2006
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ava.dominio.TopicoAula;

/**
 * Classe que representa um movimento para
 * o cadastro do conteúdo de aulas no portal da turma.
 *
 * @author David Ricardo
 *
 */
public class ConteudoAulaMov extends AbstractMovimentoAdapter {

	private Collection<TopicoAula> aulas;

	/**
	 * @return the aulas
	 */
	public Collection<TopicoAula> getAulas() {
		return aulas;
	}

	/**
	 * @param aulas the aulas to set
	 */
	public void setAulas(Collection<TopicoAula> aulas) {
		this.aulas = aulas;
	}
	
}
