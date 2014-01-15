/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 31/01/2012
 */
package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Interface que define a validação de uma restrição para matrícula on-line de discentes.
 * 
 * @author Leonardo Campos
 *
 */
public interface RestricaoDiscenteMatriculaValidator {

	public void validate(DiscenteAdapter discente, ListaMensagens lista) throws ArqException;
}
