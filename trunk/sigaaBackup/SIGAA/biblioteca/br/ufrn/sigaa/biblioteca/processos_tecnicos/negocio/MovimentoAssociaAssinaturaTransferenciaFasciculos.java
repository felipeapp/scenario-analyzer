/*
 * MovimentoAssociaAssinaturaTransferenciaFasciculos.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 03/02/2010
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *
 *   Passo os dados necess�rio para o processador que que associa a assinatura rec�m criada as 
 *   transfer�ncias que estavam sem assinatura. 
 *
 * @author jadson
 * @since 03/02/2010
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoAssociaAssinaturaTransferenciaFasciculos extends AbstractMovimentoAdapter{

	 /** Assinatura que vai ser criada */
	private Assinatura assinatura;  
	
	 /** se na cria��o deve gerar o c�digo da assinatura  */
	private boolean gerarCodigoAssinatuaCompra;
	
	 /** id da biblioteca para a qual a solicita��o de transfer�nicia foi feita e assinatura est� sendo criada */
	private int idBibliotecaDestino;  
	
	 /** id do t�tulo da assinatura criada e dos fasc�culos que foram solicitado a transfer�ncia */
	private int idTituloFasciculos;

	 /** Se vai ser criada uma nova assinatura e associada aos registro de movimenta��o sem assintura ou apenas vai ser realizada a associa��o */
	private boolean criarNovaAssinatura; 
	
	/** caso n�o seja criada a assinatura, deve-se passar o id da assinatura j� existente a qual a associa��o vai ser realizada */
	private int idAssinaturaAssociacao; 
	
	
	/**
	 * Construtor utilizando quando o usu�rio criou uma nova assinatura, ent�o precisa savas os dados 
	 * da assinatura no banco e  associar a assinatura ao registro de transfer�ncia pendente.
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
	 * Construtor utilizando quando o usu�rio selecionou uma assinatura j� existente, ent�o precisa 
	 * apenas associar a assinatura ao registro de transfer�ncia pendente.
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
