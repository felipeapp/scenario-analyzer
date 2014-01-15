/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/03/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;


/**
 *   Passa os dados para o processador que vai substituir um exemplar no acervo. 
 *   {@link ProcessadorSubstituirExemplar}.
 *
 * @author jadson
 * @since 26/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoSubstituirExemplar extends AbstractMovimentoAdapter{

	private Exemplar exemplarSubstituidor;
	private Exemplar exemplarQueVaiSerSubstituido; // para salvar no historico
	
	public MovimentoSubstituirExemplar(Exemplar exemplarSubstituidor, Exemplar exemplarQueVaiSerSubstituido){
		this.exemplarSubstituidor = exemplarSubstituidor;
		this.exemplarQueVaiSerSubstituido = exemplarQueVaiSerSubstituido;
	}

	public Exemplar getExemplarSubstituidor() {
		return exemplarSubstituidor;
	}

	public Exemplar getExemplarQueVaiSerSubstituido() {
		return exemplarQueVaiSerSubstituido;
	}


	
}
