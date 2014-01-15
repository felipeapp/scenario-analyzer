/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/01/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

public class MembroProjetoServidorForm extends SigaaForm<MembroProjeto> {

	/** Constantes utilizadas durante as buscas */
	public static final int BUSCA_COORDENADOR = 1;
	public static final int BUSCA_AREA_CONHECIMENTO = 2;
	public static final int BUSCA_STATUS_RELATORIO = 3;
	public static final int BUSCA_ANO = 4;

	/** Constantes das finalidades de busca */
	public static final int DECLARACAO_COORDENACAO = 11;
	public static final int DECLARACAO_ORIENTACOES = 12;
	
	private int finalidadeBusca;
	
	private boolean relatorioAvaliado;

	private int[] filtros = {};

	public MembroProjetoServidorForm() {
		obj = new MembroProjeto();
		obj.setProjeto(new Projeto());
		obj.getProjeto().setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
	}

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public boolean isRelatorioAvaliado() {
		return relatorioAvaliado;
	}

	public void setRelatorioAvaliado(boolean relatorioAvaliado) {
		this.relatorioAvaliado = relatorioAvaliado;
	}

	public int getFinalidadeBusca() {
		return finalidadeBusca;
	}

	public void setFinalidadeBusca(int finalidadeBusca) {
		this.finalidadeBusca = finalidadeBusca;
	}

}
