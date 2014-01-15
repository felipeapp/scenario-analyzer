/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 22/01/2010
 *
 */

package br.ufrn.sigaa.ead.jsf;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ead.dominio.LoteMatriculasDiscente;
/**
 * Encapsula LoteMatriculas para ser usada no processador
 * 
 * @author Henrique André
 *
 */
@SuppressWarnings("serial")
public class MovimentoLoteMatricula extends AbstractMovimentoAdapter {

	private List<LoteMatriculasDiscente> loteMatriculas;

	public List<LoteMatriculasDiscente> getLoteMatriculas() {
		return loteMatriculas;
	}

	public void setLoteMatriculas(List<LoteMatriculasDiscente> loteMatriculas) {
		this.loteMatriculas = loteMatriculas;
	}

}
