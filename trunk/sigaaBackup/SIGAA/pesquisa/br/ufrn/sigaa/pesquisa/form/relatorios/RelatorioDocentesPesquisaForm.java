/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form.relatorios;

import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Form utilizado no relatório de docentes participantes
 * de projetos de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("unchecked")
public class RelatorioDocentesPesquisaForm extends SigaaForm {

	private int ano;

	private TipoSituacaoProjeto situacao;

	public RelatorioDocentesPesquisaForm() {
		ano = getAnoAtual();
		situacao = new TipoSituacaoProjeto(TipoSituacaoProjeto.EM_ANDAMENTO);
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public TipoSituacaoProjeto getSituacao() {
		return situacao;
	}

	public void setSituacao(TipoSituacaoProjeto situacao) {
		this.situacao = situacao;
	}


}
