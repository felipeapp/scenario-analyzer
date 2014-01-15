/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.RecursoEspacoFisico;

public class MovimentoEspacoFisico extends AbstractMovimentoAdapter {

	private EspacoFisico espacoFisico;
	private List<RecursoEspacoFisico> recursosRemovidos;

	public EspacoFisico getEspacoFisico() {
		return espacoFisico;
	}

	public void setEspacoFisico(EspacoFisico espacoFisico) {
		this.espacoFisico = espacoFisico;
	}

	public List<RecursoEspacoFisico> getRecursosRemovidos() {
		return recursosRemovidos;
	}

	public void setRecursosRemovidos(List<RecursoEspacoFisico> recursosRemovidos) {
		this.recursosRemovidos = recursosRemovidos;
	}

}
