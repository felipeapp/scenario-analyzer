/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 19/05/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormaDocumento;

/**
 *
 * <p> Passa os dados para o processador que vai remover  a forma do documento</p>
 * 
 * @author jadson
 *
 */
public class MovimentoRemoveFormatoDocumento extends AbstractMovimentoAdapter{

	/** A forma de documento que vai ser removida (desativado) */
	private FormaDocumento formaDocumento; 
	
	/** A nova forma do documento que os materias que possuem a forma antiga vão sem migrados. Opcional, se não passado
	 * Apenas vai retirar a forma dos Documento renovida dos materiais existentes. */
	private FormaDocumento novaFormaDocumento;

	
	public MovimentoRemoveFormatoDocumento(FormaDocumento formaDocumento, FormaDocumento novaFormaDocumento) {
		super();
		this.formaDocumento = formaDocumento;
		this.novaFormaDocumento = novaFormaDocumento;
	}

	public FormaDocumento getFormaDocumento() {
		return formaDocumento;
	}

	public FormaDocumento getNovaFormaDocumento() {
		return novaFormaDocumento;
	} 
	
	
	
}