/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 19/07/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pesquisa.dominio.CotaDocente;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ClassificacaoRelatorio;

public class MovimentoDistribuicaoCotasDocentes extends
		AbstractMovimentoAdapter {

	private EditalPesquisa edital;

	private CotaDocente cotaDocente;

	private ClassificacaoRelatorio classificacao;

	private Collection<CotaDocente> cotasDocentes;

	public ClassificacaoRelatorio getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(ClassificacaoRelatorio classificacao) {
		this.classificacao = classificacao;
	}

	public Collection<CotaDocente> getCotasDocentes() {
		return cotasDocentes;
	}

	public void setCotasDocentes(Collection<CotaDocente> cotasDocentes) {
		this.cotasDocentes = cotasDocentes;
	}

	public EditalPesquisa getEdital() {
		return edital;
	}

	public void setEdital(EditalPesquisa edital) {
		this.edital = edital;
	}

	public CotaDocente getCotaDocente() {
		return cotaDocente;
	}

	public void setCotaDocente(CotaDocente cotaDocente) {
		this.cotaDocente = cotaDocente;
	}



}
