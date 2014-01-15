/*
 * MovimentoAssociaAssinaturaTransferenciaFasciculos.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 03/02/2010
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *
 *   Passo os dados necessário para o processador que que associa a assinatura recém criada as 
 *   transferências que estavam sem assinatura. 
 *
 * @author jadson
 * @since 03/02/2010
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoAssociaAssinaturaTransferenciaFasciculos extends AbstractMovimentoAdapter{

	 /** Assinatura que vai ser criada */
	private Assinatura assinatura;  
	
	 /** se na criação deve gerar o código da assinatura  */
	private boolean gerarCodigoAssinatuaCompra;
	
	 /** id da biblioteca para a qual a solicitação de transferênicia foi feita e assinatura está sendo criada */
	private int idBibliotecaDestino;  
	
	 /** id do título da assinatura criada e dos fascículos que foram solicitado a transferência */
	private int idTituloFasciculos;

	 /** Se vai ser criada uma nova assinatura e associada aos registro de movimentação sem assintura ou apenas vai ser realizada a associação */
	private boolean criarNovaAssinatura; 
	
	/** caso não seja criada a assinatura, deve-se passar o id da assinatura já existente a qual a associação vai ser realizada */
	private int idAssinaturaAssociacao; 
	
	
	/**
	 * Construtor utilizando quando o usuário criou uma nova assinatura, então precisa savas os dados 
	 * da assinatura no banco e  associar a assinatura ao registro de transferência pendente.
	 * 
	 * @param assinatura
	 * @param gerarCodigoAssinatuaCompra
	 * @param idBibliotecaDestino
	 * @param idTituloFasciculos
	 */
	public MovimentoAssociaAssinaturaTransferenciaFasciculos(
			Assinatura assinatura, boolean gerarCodigoAssinatuaCompra,
			int idBibliotecaDestino, int idTituloFasciculos) {
		super();
		this.assinatura = assinatura;
		this.gerarCodigoAssinatuaCompra = gerarCodigoAssinatuaCompra;
		this.idBibliotecaDestino = idBibliotecaDestino;
		this.idTituloFasciculos = idTituloFasciculos;
		this.criarNovaAssinatura  = true;
	}
	
	/**
	 * Construtor utilizando quando o usuário selecionou uma assinatura já existente, então precisa 
	 * apenas associar a assinatura ao registro de transferência pendente.
	 * 
	 * @param assinatura
	 * @param idBibliotecaDestino
	 * @param idTituloFasciculos
	 * @param idAssinaturaAssociacao
	 */
	public MovimentoAssociaAssinaturaTransferenciaFasciculos( Assinatura assinatura, int idBibliotecaDestino, int idTituloFasciculos, int idAssinaturaAssociacao) {
		super();
		this.assinatura = assinatura;
		this.idBibliotecaDestino = idBibliotecaDestino;
		this.idTituloFasciculos = idTituloFasciculos;
		this.criarNovaAssinatura  = false;
		this.idAssinaturaAssociacao = idAssinaturaAssociacao;
	}
	

	public Assinatura getAssinatura() {
		return assinatura;
	}

	public boolean isGerarCodigoAssinatuaCompra() {
		return gerarCodigoAssinatuaCompra;
	}

	public int getIdBibliotecaDestino() {
		return idBibliotecaDestino;
	}

	public int getIdTituloFasciculos() {
		return idTituloFasciculos;
	}

	public boolean isCriarNovaAssinatura() {
		return criarNovaAssinatura;
	}

	public int getIdAssinaturaAssociacao() {
		return idAssinaturaAssociacao;
	}
	
	
	
	
}
