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
import java.util.Date;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;

/**
 * Operador para a classe {@link HorarioTurma}.
 * 
 * @author Édipo
 *
 */
public interface OperadorHorarioTurma {

	/**
	 * Método utilizado para definir os horários da turma.
	 * 
	 * @param horarios
	 * @return
	 * @throws ArqException
	 */
	public String defineHorariosTurma(Collection<HorarioTurma> horarios) throws ArqException;
	
	/**
	 * Método utilizado para definir os períodos de início e fim da turma.
	 * 
	 * @param horarios
	 * @return
	 * @throws ArqException
	 */
	public String definePeriodosTurma(Date inicio, Date fim) throws ArqException;
	
	/**
	 * Direciona o usuário para a tela de definição dos horários da turma.
	 * 
	 * @return
	 */
	public String definicaoHorarioTurmaVoltar();
		
	/**
	 * Indica se o usuário tem permissão para alterar o horário da solicitação da turma.
	 * 
	 * @return
	 */
	public boolean isPodeAlterarHorarios();
	
	// métodos para navegação entre formulários
	
}
