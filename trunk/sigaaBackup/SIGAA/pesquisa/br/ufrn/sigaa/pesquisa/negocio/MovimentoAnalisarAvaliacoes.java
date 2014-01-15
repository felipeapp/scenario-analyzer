/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Entidade respons�vel pela realiza��o do movimento de an�lise das avalia��es
 * 
 * @author Victor Hugo
 */
public class MovimentoAnalisarAvaliacoes extends AbstractMovimentoAdapter {

	private Collection<ProjetoPesquisa> projetos;

	/**
	 * @return Returns the projetos.
	 */
	public Collection<ProjetoPesquisa> getProjetos() {
		return projetos;
	}

	/**
	 * @param projetos The projetos to set.
	 */
	public void setProjetos(Collection<ProjetoPesquisa> projetos) {
		this.projetos = projetos;
	}
	
}
