/*
 * MovimentoTransfereFasciculosEntreBibliotecas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 *    Passa os dados para o processador que realiza a transfer�ncia
 *
 * @author jadson
 * @since 23/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoTransfereFasciculosEntreBibliotecas extends AbstractMovimentoAdapter{

	/** os fasc�culos que ser�o movimentados */
	private List<Fasciculo> fasciculos;
	/** Assinatura de Origem da Transfer�ncia de Fasc�culos */
	private Assinatura assinaturaOrigem;
	/** Assinatura de Destino da Transfer�ncia de Fasc�culos (Opcional) */
	private Assinatura assinaturaDestino;

	/** Se a assinatura destino vai ficar pendente para o momento da autoriza��o */
	private boolean solicitarCriacaoAssinatura;
	
	/** Usu�rio pode n�o informar a assinatura destino, mas a biblioteca � obrigrat�rio */
	private Biblioteca bibliotecaDestino;
	
	public MovimentoTransfereFasciculosEntreBibliotecas(List<Fasciculo> fasciculos, Assinatura assinaturaOrigem
			, Assinatura assinaturaDestino, boolean solicitarCriacaoAssinatura, Biblioteca bibliotecaDestino) {
		this.fasciculos = fasciculos;
		this.assinaturaOrigem = assinaturaOrigem;
		this.assinaturaDestino = assinaturaDestino;
		this.solicitarCriacaoAssinatura = solicitarCriacaoAssinatura;
		this.bibliotecaDestino = bibliotecaDestino;
	}

	
	public List<Fasciculo> getFasciculos() {
		return fasciculos;
	}

	public Assinatura getAssinaturaOrigem() {
		return assinaturaOrigem;
	}

	public Assinatura getAssinaturaDestino() {
		return assinaturaDestino;
	}

	public boolean isSolicitarCriacaoAssinatura() {
		return solicitarCriacaoAssinatura;
	}


	public Biblioteca getBibliotecaDestino() {
		return bibliotecaDestino;
	}
	
	
	
}
