/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Movimento para realizar a consolida��o de discentes em s�rie de ensino m�dio.
 * @author Rafael Gomes
 *
 */
public class MovimentoConsolidarSerie extends AbstractMovimentoAdapter{

	/** Lista com as matriculas do discente na s�rie para a consolida��o. */
	private List<MatriculaDiscenteSerie> listMatriculaSerie;
	/** Ano utilizado para a consolida��o dos discentes na s�rie. */
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
