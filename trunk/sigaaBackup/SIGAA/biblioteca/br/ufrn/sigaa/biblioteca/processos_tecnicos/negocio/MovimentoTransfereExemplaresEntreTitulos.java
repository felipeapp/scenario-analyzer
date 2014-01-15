/*
 * MovimentoTransfereExemplaresEntreTitulos.java
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *    Passa os dados para o processador {@link ProcessadorTransfereExemplaresEntreTitulos}
 *
 * @author jadson
 * @since 01/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoTransfereExemplaresEntreTitulos extends AbstractMovimentoAdapter{

	/**  se está transferindo apenas um exemplar. */          
	private List<Exemplar> exemplaresTransferidos;        
	
	/** se está transferindo todos os exemplares ativos do título */
	private Integer idTituloOriginalExemplares;   
	
	/** o título para onde os exemplares vão */
	private Integer idTituloDestinoExemplares;

	
	/**
	 *  Construtor padrão.
	 * 
	 * @param idExemplar
	 * @param idTituloOriginalExemplares
	 * @param idTituloDestinoExemplares
	 */
	public MovimentoTransfereExemplaresEntreTitulos(List<Exemplar> exemplaresTransferidos
			, Integer idTituloOriginalExemplares, Integer idTituloDestinoExemplares) {
		this.exemplaresTransferidos = exemplaresTransferidos;
		this.idTituloOriginalExemplares = idTituloOriginalExemplares;
		this.idTituloDestinoExemplares = idTituloDestinoExemplares;
	}

	
	public List<Exemplar> getExemplaresTransferidos() {
		return exemplaresTransferidos;
	}
	
	public Integer getIdTituloOriginalExemplares() {
		return idTituloOriginalExemplares;
	}


	public Integer getIdTituloDestinoExemplares() {
		return idTituloDestinoExemplares;
	}
	
}
