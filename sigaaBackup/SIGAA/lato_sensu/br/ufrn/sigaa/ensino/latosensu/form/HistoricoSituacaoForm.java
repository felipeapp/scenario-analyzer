/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '05/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.form;

import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.HistoricoSituacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.PropostaCursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;

/**
 * Form Bean respons�vel pelas opera��es que envolvem o Hist�rico de modifica��es da Situa��o de uma Proposta
 * de Curso Lato.
 * 
 * @author Eric Moura
 *
 */
public class HistoricoSituacaoForm extends SigaaForm<HistoricoSituacao> {
	
	/** Tipo de busca a ser realizada. */
	private int tipoBusca;
	
	/** Curso Lato utilizado nas opera��es. */
	private CursoLato cursoLato;

	public int getTipoBusca() {
		return tipoBusca;
	}


	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}


	public HistoricoSituacaoForm() {
		obj = new HistoricoSituacao();
		obj.setProposta(new PropostaCursoLato());
		obj.getProposta().setSituacaoProposta(new SituacaoProposta());
		obj.setSituacao(new SituacaoProposta());
		obj.setUsuario(new Usuario());
		cursoLato = new CursoLato();
	}


	/**
	 * @return the cursoLato
	 */
	public CursoLato getCursoLato() {
		return cursoLato;
	}


	/**
	 * @param cursoLato the cursoLato to set
	 */
	public void setCursoLato(CursoLato cursoLato) {
		this.cursoLato = cursoLato;
	}

}
