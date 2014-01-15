/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/09/2010
 *
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe de negócio que auxilia na gerência de grupos de discentes da turma virtual.
 * 
 * @author Fred_Castro
 *
 */
public class MovimentoGrupoDiscentes extends AbstractMovimentoAdapter {

	/** A turma cujos grupos estão sendo gerenciados. */
	private Turma turma;
	
	/** A nova configuração dos grupos da turma. */
	private List <GrupoDiscentes> grupos;
	
	/** Os grupos que devem ser removidos (desativados). */
	private List <GrupoDiscentes> gruposARemover;
	
	public MovimentoGrupoDiscentes (Turma turma, List<GrupoDiscentes> grupos, List<GrupoDiscentes> gruposARemover) {
		super();

		this.turma = turma;
		this.grupos = grupos;
		this.gruposARemover = gruposARemover;
		
		setCodMovimento(SigaaListaComando.GERENCIAR_GRUPOS_DISCENTES);
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public List<GrupoDiscentes> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoDiscentes> grupos) {
		this.grupos = grupos;
	}

	public List<GrupoDiscentes> getGruposARemover() {
		return gruposARemover;
	}

	public void setGruposARemover(List<GrupoDiscentes> gruposARemover) {
		this.gruposARemover = gruposARemover;
	}
}