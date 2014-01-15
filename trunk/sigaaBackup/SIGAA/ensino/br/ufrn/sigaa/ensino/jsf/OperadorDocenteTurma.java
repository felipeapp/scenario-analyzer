/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 27/05/2010
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/**
 * Operador para a classe {@link DocenteTurma}.
 * 
 * @author Édipo
 *
 */
public interface OperadorDocenteTurma {
	
	/**
	 * Método utilizado para definir os docentes da turma.
	 * 
	 * @param docentesTurma
	 * @return
	 * @throws ArqException
	 */
	public String defineDocentesTurma(Collection<DocenteTurma> docentesTurma) throws ArqException;
	
	/**
	 * Direciona o usuário para a tela de definição dos docentes da turma.
	 * 
	 * @return
	 */
	public String definicaoDocenteTurmaVoltar();

	
}
