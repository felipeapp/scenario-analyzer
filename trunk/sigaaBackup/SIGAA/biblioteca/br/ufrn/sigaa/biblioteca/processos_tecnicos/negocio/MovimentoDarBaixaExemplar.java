/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on  26/03/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *
 * Passa os dados para o processador
 *
 * @author jadson
 * @since 26/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoDarBaixaExemplar extends AbstractMovimentoAdapter{
	
	/** O exemplar que vai ser baixado*/
	private Exemplar exemplar;
	
	/** Se o procesador vai permitir realizar a baixa de um material emprestado, utilizado APENAS no caso de uso do devolver materiais perdidos. */
	private boolean permiteBaixarMaterialEmprestado = false;
	
	/** Se o sistema vai bloquear a opera��o caso a baixa n�o possa ser feita no SIPAC ou apenas mostrar uma mesnagem de erro. */
	private boolean baixaPatrimonioObrigatoria = true;
	
	/** Construtor que � para ser utilizada nos casos de uso do sistema. */
	public MovimentoDarBaixaExemplar(Exemplar exemplar){
		this.exemplar = exemplar;
		permiteBaixarMaterialEmprestado = false;
	}
	
	/** Construtor utilizado APENAS no caso de uso de devolver material perdido */
	public MovimentoDarBaixaExemplar(Exemplar exemplar, boolean permiteBaixarMaterialEmprestado, boolean baixaPatrimonioObrigatoria){
		this.exemplar = exemplar;
		this.permiteBaixarMaterialEmprestado = permiteBaixarMaterialEmprestado;
		this.baixaPatrimonioObrigatoria = baixaPatrimonioObrigatoria;
	}

	public Exemplar getExemplar() {
		return exemplar;
	}

	public boolean isPermiteBaixarMaterialEmprestado() {
		return permiteBaixarMaterialEmprestado;
	}

	public boolean isBaixaPatrimonioObrigatoria() {
		return baixaPatrimonioObrigatoria;
	}
	
}
