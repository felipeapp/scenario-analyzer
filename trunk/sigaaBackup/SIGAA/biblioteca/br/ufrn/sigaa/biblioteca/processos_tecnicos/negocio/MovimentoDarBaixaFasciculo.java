/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/09/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *    Movimento de dar baixa em um fasc�culo. Os dados aqui definidos s�o usados no
 * processador de dar baixa em Fasc�culos.
 *
 * @author jadson
 * @since 11/09/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoDarBaixaFasciculo  extends AbstractMovimentoAdapter{
	
	/** O fasc�culo que ser� baixado do acervo. */
	private Fasciculo fasciculo;
	
	/** Se o procesador vai permitir realizar a baixa de um material emprestado, utilizado APENAS no caso de uso do devolver materiais perdidos. */
	private boolean permiteBaixarMaterialEmprestado = false;
	
	/** Construtor que � para ser utilizada nos casos de uso do sistema. */
	public MovimentoDarBaixaFasciculo(Fasciculo fasciculo){
		this.fasciculo = fasciculo;
		permiteBaixarMaterialEmprestado = false;
	}

	/** Construtor utilizado APENAS no caso de uso de devolver material perdido */
	public MovimentoDarBaixaFasciculo(Fasciculo fasciculo, boolean permiteBaixarMaterialEmprestado){
		this.fasciculo = fasciculo;
		this.permiteBaixarMaterialEmprestado = permiteBaixarMaterialEmprestado;
	}
	
	public Fasciculo getFasciculo() {
		return fasciculo;
	}
	
	public boolean isPermiteBaixarMaterialEmprestado() {
		return permiteBaixarMaterialEmprestado;
	}

}
