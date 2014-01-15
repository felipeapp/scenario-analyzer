/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;

/**
 *
 * <p>Passa os dados para o processador</p>
 * 
 * @author jadson
 *
 */
public class MovimentoQuitaMultaUsuarioBiblioteca extends MovimentoCadastro{

	private MultaUsuarioBiblioteca multaASerPaga;
	
	/**Diz se o pagamento no sistema foi feito manualmente pelo usu�rio. Futuramente o pagamento vai 
	 * ser autom�tico*/
	private boolean pagamentoManual;

	/** N�mero de refer�ncia para identificar a GRU */
	private String numeroReferencia;
	
	/**
	 * O usu�rio a qual a multa que vai ser paga pertence.
	 */
	private UsuarioBiblioteca usuarioDaMulta;

	
	/** Guarda lista de multas que foram pagamas automaticamente para ser dado baixa. Usado no caso de uso de pagamento autom�tico. */
	private List<MultaUsuarioBiblioteca> multasPagaAutomaticamente;
	
	
	/**
	 * Construor para o caso de uso de pagamento manual.
	 * @param multaASerPaga
	 * @param usuarioDaMulta
	 * @param numeroReferencia
	 */
	public MovimentoQuitaMultaUsuarioBiblioteca(MultaUsuarioBiblioteca multaASerPaga, UsuarioBiblioteca usuarioDaMulta, String numeroReferencia) {
		this.multaASerPaga = multaASerPaga;
		this.pagamentoManual = true;
		this.usuarioDaMulta = usuarioDaMulta;
		this.numeroReferencia = numeroReferencia;
	}
	
	/**
	 * Construor para o caso de uso de pagamento autom�tico.
	 * @param multasPagaAutomaticamente
	 */
	public MovimentoQuitaMultaUsuarioBiblioteca(List<MultaUsuarioBiblioteca> multasPagaAutomaticamente) {
		this.multasPagaAutomaticamente = multasPagaAutomaticamente;
		this.pagamentoManual = false;
	}
	
	
	
	public MultaUsuarioBiblioteca getMultaASerPaga() {
		return multaASerPaga;
	}

	public boolean isPagamentoManual() {
		return pagamentoManual;
	}

	public UsuarioBiblioteca getUsuarioDaMulta() {
		return usuarioDaMulta;
	}

	public String getNumeroReferencia() {
		return numeroReferencia;
	}

	public List<MultaUsuarioBiblioteca> getMultasPagaAutomaticamente() {
		return multasPagaAutomaticamente;
	}

	
}
