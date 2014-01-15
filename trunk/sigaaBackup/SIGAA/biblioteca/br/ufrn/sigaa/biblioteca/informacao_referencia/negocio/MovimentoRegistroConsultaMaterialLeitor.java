/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/05/2009
 * 
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *
 *      Movimento que guarda os dados passados ao processador que registra a consulta de materiais. 
 *
 * @author Fred_Castro
 * @since 30/04/2009
 * @version 1.0 criação da classe
 *
 */
public class MovimentoRegistroConsultaMaterialLeitor extends AbstractMovimentoAdapter{

	private List <MaterialInformacional> materiais;
	private int turno;
	private Date data;
	
	public MovimentoRegistroConsultaMaterialLeitor(List <MaterialInformacional> materiais, int turno, Date data) {
		super();
		this.materiais = materiais;
		this.turno = turno;
		this.data = data;
	}

	public List<MaterialInformacional> getMateriais() {
		return materiais;
	}

	public int getTurno() {
		return turno;
	}

	public Date getData() {
		return data;
	}
}
