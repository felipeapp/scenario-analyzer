/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '27/01/2010'
 *
 */

package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Armazena informa��es necess�rias para a consulta de informa��es de aulas de docentes.
 * 
 * @author R�mulo Augusto
 *
 */
public class AulasDocenteDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3515935324010179231L;
	
	private int idServidor;
	
	private Date inicioAfastamento;
	
	private Date fimAfastamento;
	
	public int getIdServidor() {
		return idServidor;
	}
	
	public void setIdServidor(int idServidor) {
		this.idServidor = idServidor;
	}
	
	public Date getInicioAfastamento() {
		return inicioAfastamento;
	}
	
	public void setInicioAfastamento(Date inicioAfastamento) {
		this.inicioAfastamento = inicioAfastamento;
	}
	
	public Date getFimAfastamento() {
		return fimAfastamento;
	}
	
	public void setFimAfastamento(Date fimAfastamento) {
		this.fimAfastamento = fimAfastamento;
	}
}
