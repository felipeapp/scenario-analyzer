/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 31/01/2012
 */
package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Interface que define a valida��o de uma restri��o para matr�cula on-line de discentes.
 * 
 * @author Leonardo Campos
 *
 */
public interface RestricaoDiscenteMatriculaValidator {

	public void validate(DiscenteAdapter discente, ListaMensagens lista) throws ArqException;
}
