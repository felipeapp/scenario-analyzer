/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 07/12/2007
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;

/**
 * Movimento que encapsula os dados necessários para efetuar uma mudança de
 * currículo coletiva
 * 
 * @author leonardo
 *
 */
public class MovimentoMudancaCurricularColetiva extends AbstractMovimento {

	
	private int qtdAlunos;
	
	private Curriculo curriculoOrigem;
	
	private Curriculo curriculoDestino;
	
	private Integer anoIngresso, periodoIngresso;
	
	public MovimentoMudancaCurricularColetiva(){
	}
	
	public Curriculo getCurriculoDestino() {
		return curriculoDestino;
	}


	public void setCurriculoDestino(Curriculo curriculoDestino) {
		this.curriculoDestino = curriculoDestino;
	}


	public Curriculo getCurriculoOrigem() {
		return curriculoOrigem;
	}


	public void setCurriculoOrigem(Curriculo curriculoOrigem) {
		this.curriculoOrigem = curriculoOrigem;
	}


	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void setId(int id) {
		// TODO Auto-generated method stub

	}

	public int getQtdAlunos() {
		return qtdAlunos;
	}

	public void setQtdAlunos(int qtdAlunos) {
		this.qtdAlunos = qtdAlunos;
	}

	public Integer getAnoIngresso() {
		return anoIngresso;
	}

	public void setAnoIngresso(Integer anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	public Integer getPeriodoIngresso() {
		return periodoIngresso;
	}

	public void setPeriodoIngresso(Integer periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

}
