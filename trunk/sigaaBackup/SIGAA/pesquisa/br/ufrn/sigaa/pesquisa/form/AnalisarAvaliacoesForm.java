/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Form utilizado para a analise das avaliações de projetos de pesquisa.
 * 
 * @author Victor Hugo
 *
 */
@SuppressWarnings("unchecked")
public class AnalisarAvaliacoesForm extends SigaaForm {

	Map<ProjetoPesquisa, Integer> analise = new TreeMap<ProjetoPesquisa, Integer>();

	Collection<ProjetoPesquisa> projetosAprovados = new ArrayList<ProjetoPesquisa>();
	Collection<ProjetoPesquisa> projetosReprovados = new ArrayList<ProjetoPesquisa>();
	Collection<ProjetoPesquisa> projetosIndefinidos = new ArrayList<ProjetoPesquisa>();

	private int minimoAvaliacoes = 0;
	private Unidade unidadeAcademica = new Unidade();
	private EditalPesquisa edital = new EditalPesquisa();
	
	private int[] filtros = {};
	public static final int UNIDADE_ACADEMICA = 1;
	public static final int MINIMO_AVALIACOES = 2;
	public static final int FORMATO_RELATORIO = 3;
	public static final int EDITAL = 4;

	/* (non-Javadoc)
	 * @see br.ufrn.sigaa.arq.struts.SigaaForm#referenceData(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void referenceData(HttpServletRequest req) throws ArqException {
		AreaConhecimentoCnpqDao daoArea = (AreaConhecimentoCnpqDao) getDAO( AreaConhecimentoCnpq.class, req );
		mapa.put( "grandeAreas", daoArea.findGrandeAreasConhecimentoCnpq() );
	}



	public Map<ProjetoPesquisa, Integer> getAnalise() {
		return analise;
	}



	public void setAnalise(Map<ProjetoPesquisa, Integer> analise) {
		this.analise = analise;
	}



	/**
	 * @return Returns the projetosAprovados.
	 */
	public Collection<ProjetoPesquisa> getProjetosAprovados() {
		return projetosAprovados;
	}

	/**
	 * @param projetosAprovados The projetosAprovados to set.
	 */
	public void setProjetosAprovados(Collection<ProjetoPesquisa> projetosAprovados) {
		this.projetosAprovados = projetosAprovados;
	}

	/**
	 * @return Returns the projetosIndefinidos.
	 */
	public Collection<ProjetoPesquisa> getProjetosIndefinidos() {
		return projetosIndefinidos;
	}

	/**
	 * @param projetosIndefinidos The projetosIndefinidos to set.
	 */
	public void setProjetosIndefinidos(
			Collection<ProjetoPesquisa> projetosIndefinidos) {
		this.projetosIndefinidos = projetosIndefinidos;
	}

	/**
	 * @return Returns the projetosReprovados.
	 */
	public Collection<ProjetoPesquisa> getProjetosReprovados() {
		return projetosReprovados;
	}

	/**
	 * @param projetosReprovados The projetosReprovados to set.
	 */
	public void setProjetosReprovados(Collection<ProjetoPesquisa> projetosReprovados) {
		this.projetosReprovados = projetosReprovados;
	}

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public int getMinimoAvaliacoes() {
		return minimoAvaliacoes;
	}

	public void setMinimoAvaliacoes(int minimoAvaliacoes) {
		this.minimoAvaliacoes = minimoAvaliacoes;
	}

	public Unidade getUnidadeAcademica() {
		return unidadeAcademica;
	}

	public void setUnidadeAcademica(Unidade unidadeAcademica) {
		this.unidadeAcademica = unidadeAcademica;
	}



	public EditalPesquisa getEdital() {
		return edital;
	}



	public void setEdital(EditalPesquisa edital) {
		this.edital = edital;
	}
}
