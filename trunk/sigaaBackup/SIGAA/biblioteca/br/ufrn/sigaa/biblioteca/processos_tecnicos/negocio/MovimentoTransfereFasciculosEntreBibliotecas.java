/*
 * MovimentoTransfereFasciculosEntreBibliotecas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
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
 *    Passa os dados para o processador que realiza a transferência
 *
 * @author jadson
 * @since 23/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoTransfereFasciculosEntreBibliotecas extends AbstractMovimentoAdapter{

	/** os fascículos que serão movimentados */
	private List<Fasciculo> fasciculos;
	/** Assinatura de Origem da Transferência de Fascículos */
	private Assinatura assinaturaOrigem;
	/** Assinatura de Destino da Transferência de Fascículos (Opcional) */
	private Assinatura assinaturaDestino;

	/** Se a assinatura destino vai ficar pendente para o momento da autorização */
	private boolean solicitarCriacaoAssinatura;
	
	/** Usuário pode não informar a assinatura destino, mas a biblioteca é obrigratório */
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
