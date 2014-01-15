/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 11/09/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *    Movimento de dar baixa em um fascículo. Os dados aqui definidos são usados no
 * processador de dar baixa em Fascículos.
 *
 * @author jadson
 * @since 11/09/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoDarBaixaFasciculo  extends AbstractMovimentoAdapter{
	
	/** O fascículo que será baixado do acervo. */
	private Fasciculo fasciculo;
	
	/** Se o procesador vai permitir realizar a baixa de um material emprestado, utilizado APENAS no caso de uso do devolver materiais perdidos. */
	private boolean permiteBaixarMaterialEmprestado = false;
	
	/** Construtor que é para ser utilizada nos casos de uso do sistema. */
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
