/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/05/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 * <p> Passa os dados para o processador. </p>
 *
 * @author jadson
 *
 */
public class MovimentoReorganizaCodigoBarrasFasciculos extends AbstractMovimentoAdapter{

	/** Os fascículos que terão seus códigos alterados.*/
	private List<Fasciculo> fasciculosAssinatura ;

	/** Assinatura dos fascículos que serão alterados, passado para o processador para validação. */
	private Assinatura assinaturaSelecionada;
	
	/**O número de sequência final que assiantura possirá depois da reorganização*/
	private int numeroSequencialFasciculos;
	
	/** Guardas os fasculos com o mesmo código da assinatura mas em outras assinatura. Pode reorganizar o código dos fascículos desde que não batão com esse.*/
	private List<Fasciculo> fasciculosEmOutrasAssinaturas;

	
	
	public MovimentoReorganizaCodigoBarrasFasciculos(List<Fasciculo> fasciculosAssinatura, Assinatura assinaturaSelecionada, int numeroSequencialFasciculos, List<Fasciculo> fasciculosEmOutrasAssinaturas) {
		this.fasciculosAssinatura = fasciculosAssinatura;
		this.fasciculosEmOutrasAssinaturas = fasciculosEmOutrasAssinaturas;
		this.assinaturaSelecionada = assinaturaSelecionada;
		this.numeroSequencialFasciculos = numeroSequencialFasciculos;
	}

	public List<Fasciculo> getFasciculosAssinatura() {
		return fasciculosAssinatura;
	}

	public List<Fasciculo> getFasciculosEmOutrasAssinaturas() {
		return fasciculosEmOutrasAssinaturas;
	}

	public Assinatura getAssinaturaSelecionada() {
		return assinaturaSelecionada;
	}

	public int getNumeroSequencialFasciculos() {
		return numeroSequencialFasciculos;
	}
	
}
