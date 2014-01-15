/*
 * MovimentoTransfereExemplaresEntreBibliotecas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *     Passa os dados para o processador {@link ProcessadorTransfereExemplaresEntreBibliotecas}.
 *
 * @author jadson
 * @since 23/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoTransfereExemplaresEntreBibliotecas extends AbstractMovimentoAdapter{

	/** os exemplares que v�o ser transferidos */
	private List<Exemplar> exemplares;
	
	/** A biblioteca destino dos exemplares */
	private Biblioteca bibliotecaDestino;

	/** Os exemplares que d�o erro ao tentar transferir. Preenchido pelo processador.*/
	private List<Exemplar> exemplaresComErroTransferencia;
	
	/** 
	 * Indica se o chamado patrim�nial vai ser gerado ou n�o. <br/> 
	 * 
	 * O chamado patrimonial s� n�o deve ser gerado se por acaso a bibliotec�ria estiver fazendo 
	 * apenas uma corre��o de exemplares inclu�dos em uma biblioteca errada, mas no SIPAC a informa��o 
	 * estiver correta.<br/>
	 * 
	 */
	private boolean gerarChamadoPatrimonial = true;
	
	public MovimentoTransfereExemplaresEntreBibliotecas(List<Exemplar> exemplares, Biblioteca bibliotecaDestino, boolean gerarChamadoPatrimonial) {
		this.exemplares = exemplares;
		this.bibliotecaDestino = bibliotecaDestino;
		this.gerarChamadoPatrimonial = gerarChamadoPatrimonial;
	}

	public List<Exemplar> getExemplares() {
		return exemplares;
	}

	public Biblioteca getBibliotecaDestino() {
		return bibliotecaDestino;
	}

	public boolean isGerarChamadoPatrimonial() {
		return gerarChamadoPatrimonial;
	}

	public List<Exemplar> getExemplaresComErroTransferencia() {
		return exemplaresComErroTransferencia;
	}

	/** Adiciona um exemplar com erro*/
	public void adicionaExemplaresComErroTransferencia(Exemplar exemplarComErroTransferencia) {
		if( this.exemplaresComErroTransferencia == null){
			this.exemplaresComErroTransferencia = new ArrayList<Exemplar>();
		}
		this.exemplaresComErroTransferencia.add(exemplarComErroTransferencia);
	}
	
}
