/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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

	/** Os fasc�culos que ter�o seus c�digos alterados.*/
	private List<Fasciculo> fasciculosAssinatura ;

	/** Assinatura dos fasc�culos que ser�o alterados, passado para o processador para valida��o. */
	private Assinatura assinaturaSelecionada;
	
	/**O n�mero de sequ�ncia final que assiantura possir� depois da reorganiza��o*/
	private int numeroSequencialFasciculos;
	
	/** Guardas os fasculos com o mesmo c�digo da assinatura mas em outras assinatura. Pode reorganizar o c�digo dos fasc�culos desde que n�o bat�o com esse.*/
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
