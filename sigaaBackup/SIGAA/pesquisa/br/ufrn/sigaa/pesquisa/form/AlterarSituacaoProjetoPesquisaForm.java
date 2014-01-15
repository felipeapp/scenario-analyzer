/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/05/2007
 *
 */

package br.ufrn.sigaa.pesquisa.form;

import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Form utilizado para realizar operações de alteração da situação e/ou tipo de um projeto de pesquisa
 *
 * @author Leonardo
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("unchecked")
public class AlterarSituacaoProjetoPesquisaForm extends SigaaForm {

	private ProjetoPesquisa projeto;

	private String dataInicio;
	private String dataFim;

	public AlterarSituacaoProjetoPesquisaForm(){
		projeto = new ProjetoPesquisa();

		projeto.setSituacaoProjeto( new TipoSituacaoProjeto() );
		projeto.setEdital(new EditalPesquisa());
	}

	public ProjetoPesquisa getProjeto() {
		return projeto;
	}

	public void setProjeto(ProjetoPesquisa projeto) {
		this.projeto = projeto;
	}

	public String getDataFim() {
		return dataFim;
	}

	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}

	public String getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}
}
