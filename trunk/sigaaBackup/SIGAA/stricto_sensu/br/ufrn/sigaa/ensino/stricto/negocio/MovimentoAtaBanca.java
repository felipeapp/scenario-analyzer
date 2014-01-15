/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;

public class MovimentoAtaBanca extends AbstractMovimento {

	private int id;
	
	private Arquivo ata;
	
	private BancaPos banca;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Arquivo getAta() {
		return ata;
	}

	public void setAta(Arquivo ata) {
		this.ata = ata;
	}

	public BancaPos getBanca() {
		return banca;
	}

	public void setBanca(BancaPos banca) {
		this.banca = banca;
	}

}
