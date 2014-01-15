/*
 * MovimentoRemoveStatusOuTipoEmprestimo.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 24/02/2010
 * Autor: jadson
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.StatusMaterialInformacional;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoEmprestimo;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.TipoMaterial;

/**
 *
 * Passa os dados para o processador
 *
 * @author jadson
 *
 */
public class MovimentoRemoveStatusTipoMaterialETipoEmprestimo extends AbstractMovimentoAdapter {

	/** O status que vai ser removido (desativado) */
	private StatusMaterialInformacional status; 
	
	/** O novo status que os materias que possuem o status antigo vão sem migrados */
	private StatusMaterialInformacional novoStatusParaOsMateriais; 
	
	 /** O tipo de empretimos que vai ser removido (desativado) */
	private TipoEmprestimo tipoEmprestimo; 
	
	/** O status que vai ser removido (desativado) */
	private TipoMaterial tipoMaterial; 
	
	/** O novo status que os materias que possuem o status antigo vão sem migrados */
	private TipoMaterial novoTipoMaterial; 
	
	
	/** Indica qual dos dois objetos vão ser removidos, porque esse movimento só vai usado por um objeto por vez */
	private boolean removerStatus;

	/** Indica qual dos dois objetos vão ser removidos, porque esse movimento só vai usado por um objeto por vez */
	private boolean removerTipoMaterial;
	
	/**
	 * Para o caso de uso de remoção do status
	 * @param status
	 * @param novoStatusParaOsMateriais
	 */
	public MovimentoRemoveStatusTipoMaterialETipoEmprestimo(StatusMaterialInformacional status, StatusMaterialInformacional novoStatusParaOsMateriais) {
		this.status = status;
		this.novoStatusParaOsMateriais = novoStatusParaOsMateriais;
		removerStatus = true;
		removerTipoMaterial = false;
	}


	/**
	 * Para o caso de uso de remoção de tipo de material
	 * @param tipoMaterial
	 * @param novoTipoMaterial
	 */
	public MovimentoRemoveStatusTipoMaterialETipoEmprestimo(TipoMaterial tipoMaterial, TipoMaterial novoTipoMaterial) {
		this.tipoMaterial = tipoMaterial;
		this.novoTipoMaterial = novoTipoMaterial;
		removerStatus = false;
		removerTipoMaterial = true;
	}
	
	/**
	 * Para o caso de uso de remoção de tipo de emprestimo
	 * @param tipoEmprestimo
	 */
	public MovimentoRemoveStatusTipoMaterialETipoEmprestimo(TipoEmprestimo tipoEmprestimo) {
		this.tipoEmprestimo = tipoEmprestimo;
		removerStatus = false;
		removerTipoMaterial = false;
	}

	public StatusMaterialInformacional getStatus() {	return status;}

	public TipoEmprestimo getTipoEmprestimo() {return tipoEmprestimo;}

	public StatusMaterialInformacional getNovoStatusParaOsMateriais() {return novoStatusParaOsMateriais;}

	public TipoMaterial getTipoMaterial() {return tipoMaterial;}

	public TipoMaterial getNovoTipoMaterial() {return novoTipoMaterial;}

	public boolean isRemoverStatus() {return removerStatus;	}

	public boolean isRemoverTipoMaterial() {return removerTipoMaterial;}
	
	
}
