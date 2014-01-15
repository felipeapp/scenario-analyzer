/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/04/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;


import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Esse Entidade é responsável pela distribuição dos projetos.
 * 
 * @author Victor Hugo
 */
public class DistribuicaoProjetos {

	private ProjetoPesquisa projeto;
	
	private Collection<Consultor> consultores = new ArrayList<Consultor>();

	public Collection<Consultor> getConsultores() {
		return consultores;
	}

	public void setConsultores(Collection<Consultor> consultores) {
		this.consultores = consultores;
	}

	public ProjetoPesquisa getProjeto() {
		return projeto;
	}

	public void setProjeto(ProjetoPesquisa projeto) {
		this.projeto = projeto;
	}
	
}
