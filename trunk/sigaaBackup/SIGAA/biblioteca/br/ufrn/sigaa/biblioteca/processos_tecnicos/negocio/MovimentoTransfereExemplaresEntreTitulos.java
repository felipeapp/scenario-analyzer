/*
 * MovimentoTransfereExemplaresEntreTitulos.java
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

	/**  se est� transferindo apenas um exemplar. */          
	private List<Exemplar> exemplaresTransferidos;        
	
	/** se est� transferindo todos os exemplares ativos do t�tulo */
	private Integer idTituloOriginalExemplares;   
	
	/** o t�tulo para onde os exemplares v�o */
	private Integer idTituloDestinoExemplares;

	
	/**
	 *  Construtor padr�o.
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
