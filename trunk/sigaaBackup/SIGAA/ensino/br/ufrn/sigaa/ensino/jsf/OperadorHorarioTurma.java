/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 27/05/2010
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;

/**
 * Operador para a classe {@link HorarioTurma}.
 * 
 * @author �dipo
 *
 */
public interface OperadorHorarioTurma {

	/**
	 * M�todo utilizado para definir os hor�rios da turma.
	 * 
	 * @param horarios
	 * @return
	 * @throws ArqException
	 */
	public String defineHorariosTurma(Collection<HorarioTurma> horarios) throws ArqException;
	
	/**
	 * M�todo utilizado para definir os per�odos de in�cio e fim da turma.
	 * 
	 * @param horarios
	 * @return
	 * @throws ArqException
	 */
	public String definePeriodosTurma(Date inicio, Date fim) throws ArqException;
	
	/**
	 * Direciona o usu�rio para a tela de defini��o dos hor�rios da turma.
	 * 
	 * @return
	 */
	public String definicaoHorarioTurmaVoltar();
		
	/**
	 * Indica se o usu�rio tem permiss�o para alterar o hor�rio da solicita��o da turma.
	 * 
	 * @return
	 */
	public boolean isPodeAlterarHorarios();
	
	// m�todos para navega��o entre formul�rios
	
}
