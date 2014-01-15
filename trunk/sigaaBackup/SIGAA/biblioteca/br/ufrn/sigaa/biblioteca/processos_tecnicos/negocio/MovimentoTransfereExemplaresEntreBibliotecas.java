/*
 * MovimentoTransfereExemplaresEntreBibliotecas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
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

	/** os exemplares que vão ser transferidos */
	private List<Exemplar> exemplares;
	
	/** A biblioteca destino dos exemplares */
	private Biblioteca bibliotecaDestino;

	/** Os exemplares que dão erro ao tentar transferir. Preenchido pelo processador.*/
	private List<Exemplar> exemplaresComErroTransferencia;
	
	/** 
	 * Indica se o chamado patrimônial vai ser gerado ou não. <br/> 
	 * 
	 * O chamado patrimonial só não deve ser gerado se por acaso a bibliotecária estiver fazendo 
	 * apenas uma correção de exemplares incluídos em uma biblioteca errada, mas no SIPAC a informação 
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
