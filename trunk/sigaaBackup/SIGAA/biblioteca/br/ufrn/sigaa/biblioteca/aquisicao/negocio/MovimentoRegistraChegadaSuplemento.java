/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/10/2009
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 * Passa os dados para o processador
 *
 * @author jadson
 * @since 09/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoRegistraChegadaSuplemento extends AbstractMovimentoAdapter{

	/** O fascículo a que pertence o suplemento*/
	private Fasciculo fasciculoPrincipal;
	
	/** O suplemento que vai ser criado*/
	private Fasciculo suplemento;
	
	/** A assinatura do suplemento e seu fascículo*/
	private Assinatura assinatura;

	public MovimentoRegistraChegadaSuplemento(Fasciculo fasciculoPrincipal, Fasciculo suplemento, Assinatura assinatura) {
		this.fasciculoPrincipal = fasciculoPrincipal;
		this.suplemento = suplemento;
		this.assinatura = assinatura;
	}

	
	public Fasciculo getFasciculoPrincipal() {
		return fasciculoPrincipal;
	}

	public Fasciculo getSuplemento() {
		return suplemento;
	}

	public Assinatura getAssinatura() {
		return assinatura;
	}
	
}
