/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 09/04/2012
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.DevolucaoMaterialPerdido;

/**
 * Movimento que guarda os dados passados ao processador que devolve empréstimos de materiais perdidos na biblioteca. 
 *
 * @author Jadson
 * @since 09/04/2012
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoDevolveEmprestimoMaterialPerdido extends MovimentoCadastro{

	/** O empréstimos que está em aberto cujo material foi perdido  */
	private int idMaterial;
	
	/** Guarda informação da devolução do material perdido.*/
	private DevolucaoMaterialPerdido devolucaoMaterialPerdido;
	
	/** Indica se será realizada a baixa no material ou não.*/
	private boolean realizarBaixaMaterial = true;
	
	public MovimentoDevolveEmprestimoMaterialPerdido(int idMaterial, DevolucaoMaterialPerdido devolucaoMaterialPerdido, boolean realizarBaixaMaterial) {
		setCodMovimento(SigaaListaComando.DEVOLVE_EMPRESTIMO_MATERIAL_PERDIDO);
		this.idMaterial = idMaterial;
		this.devolucaoMaterialPerdido = devolucaoMaterialPerdido;
		this.realizarBaixaMaterial = realizarBaixaMaterial;
	}

	public int getIdMaterial() {
		return idMaterial;
	}

	public DevolucaoMaterialPerdido getDevolucaoMaterialPerdido() {
		return devolucaoMaterialPerdido;
	}

	public void setDevolucaoMaterialPerdido(DevolucaoMaterialPerdido devolucaoMaterialPerdido) {
		this.devolucaoMaterialPerdido = devolucaoMaterialPerdido;
	}

	public boolean isRealizarBaixaMaterial() {
		return realizarBaixaMaterial;
	}
	
	
	
}
