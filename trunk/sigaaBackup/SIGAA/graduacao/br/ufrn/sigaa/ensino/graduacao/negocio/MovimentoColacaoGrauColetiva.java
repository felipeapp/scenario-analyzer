/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Agrupa dados para serem processados na conclusão de curso de vários alunos de um curso
 *
 * @author Andre Dantas
 *
 */
public class MovimentoColacaoGrauColetiva extends AbstractMovimentoAdapter {

	private Collection<DiscenteGraduacao> discentes;

	private Date dataColacao;

	private int ano;

	private int periodo;
	
	private int statusRetorno;

	public Collection<DiscenteGraduacao> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(Collection<DiscenteGraduacao> discentes) {
		this.discentes = discentes;
	}

	public Date getDataColacao() {
		return dataColacao;
	}

	public void setDataColacao(Date dataColacao) {
		this.dataColacao = dataColacao;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public int getStatusRetorno() {
		return statusRetorno;
	}

	public void setStatusRetorno(int statusRetorno) {
		this.statusRetorno = statusRetorno;
	}

}
