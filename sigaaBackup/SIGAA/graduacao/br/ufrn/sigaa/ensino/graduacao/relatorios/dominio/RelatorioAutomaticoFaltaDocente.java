/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.dominio;

import java.util.Date;

import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

public class RelatorioAutomaticoFaltaDocente {

	private Pessoa pessoa = new Pessoa();
	private Unidade centro = new Unidade();
	private Unidade departamento = new Unidade();
	private Date dataAula;
	private long numeroFaltas;

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public Unidade getDepartamento() {
		return departamento;
	}

	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	public Date getDataAula() {
		return dataAula;
	}

	public void setDataAula(Date dataAula) {
		this.dataAula = dataAula;
	}

	public long getNumeroFaltas() {
		return numeroFaltas;
	}

	public void setNumeroFaltas(long numeroFaltas) {
		this.numeroFaltas = numeroFaltas;
	}
}
