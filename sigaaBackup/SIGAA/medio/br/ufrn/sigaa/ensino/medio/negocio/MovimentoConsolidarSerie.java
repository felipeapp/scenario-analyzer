/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 25/10/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;

/**
 * Movimento para realizar a consolidação de discentes em série de ensino médio.
 * @author Rafael Gomes
 *
 */
public class MovimentoConsolidarSerie extends AbstractMovimentoAdapter{

	/** Lista com as matriculas do discente na série para a consolidação. */
	private List<MatriculaDiscenteSerie> listMatriculaSerie;
	/** Ano utilizado para a consolidação dos discentes na série. */
	private Integer ano;

	
	public List<MatriculaDiscenteSerie> getListMatriculaSerie() {
		return listMatriculaSerie;
	}

	public void setListMatriculaSerie(
			List<MatriculaDiscenteSerie> listMatriculaSerie) {
		this.listMatriculaSerie = listMatriculaSerie;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}
	
	
}
