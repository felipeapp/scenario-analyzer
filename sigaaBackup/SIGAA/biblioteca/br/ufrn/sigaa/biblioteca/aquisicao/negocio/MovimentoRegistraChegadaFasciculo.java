/*
 * Universidade Federal do Rio Grande no Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2009
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 *    Dados passados para o processador que registra a assinatura.
 *
 * @author jadson
 * @since 07/04/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoRegistraChegadaFasciculo extends AbstractMovimentoAdapter{

	/** A assinatura do fasc�culo que vai ser registrado*/
	private Assinatura assinatura;
	
	/** O fasc�culo que vai ser registrado */
	private Fasciculo fasciculo;
	
	/** O suplemento dos fasc�culo, caso o usu�rio escolha registrar um suplemento junto com o fasc�culo*/
	private Fasciculo suplemento;
	

	public MovimentoRegistraChegadaFasciculo(Assinatura assinatura, Fasciculo fasciculo){
		this.assinatura = assinatura;
		this.fasciculo = fasciculo;	
	}

	
	public MovimentoRegistraChegadaFasciculo(Assinatura assinatura, Fasciculo fasciculo, Fasciculo suplemento ){
		this.assinatura = assinatura;
		this.fasciculo = fasciculo;
		this.suplemento = suplemento;
	}

	public Assinatura getAssinatura() {
		return assinatura;
	}


	public Fasciculo getFasciculo() {
		return fasciculo;
	}

	public Fasciculo getSuplemento() {
		return suplemento;
	}
	
}
