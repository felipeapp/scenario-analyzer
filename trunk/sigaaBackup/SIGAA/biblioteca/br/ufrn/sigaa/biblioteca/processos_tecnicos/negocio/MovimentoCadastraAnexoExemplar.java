/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/04/2009
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *
 *   Movimento que passa os dados para o processador de cadastrar um anexo 
 *
 * @author jadson
 * @since 15/04/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoCadastraAnexoExemplar extends AbstractMovimentoAdapter{

	/** O anexo que vai ser criado*/
	private Exemplar anexo;
	/** O exemplar a quem o anexo pertence */
	private Exemplar principal;
	
	public MovimentoCadastraAnexoExemplar(Exemplar anexo, Exemplar principal){
		
		this.anexo = anexo;
		this.principal = principal;
	}


	public Exemplar getAnexo() {
		return anexo;
	}


	public Exemplar getPrincipal() {
		return principal;
	}
	
}
