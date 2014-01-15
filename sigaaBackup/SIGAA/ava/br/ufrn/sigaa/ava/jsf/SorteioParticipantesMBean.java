/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;

/**
 * Managed bean para sortear discentes da Turma Virtual que est�o
 * "presentes" em sala de aula de forma a estimular a participa��o em 
 * sala de aula. 
 * 
 * O crit�rio de "presen�a" a ser usado ser� os discentes que registraram 
 * a presen�a por biometria.
 * 
 * @author agostinho campos
 */
@Component("sorteioParticipantesMBean") 
@Scope("request")
public class SorteioParticipantesMBean extends ControllerTurmaVirtual {

	private List<MatriculaComponente> discentesTurma;
	private MatriculaComponente discenteSorteado;
	
	public SorteioParticipantesMBean() {
	}
	
	/**
	 * Carrega os alunos da turma com nome e foto que foi registrada pelo
	 * sistema de Cadastramento de Pessoas (desktop). Assim evita-se que 
	 * que sejam sorteadas imagens que n�o representam os alunos de verdade. 
	 *  
	 * @return
	 * @throws DAOException 
	 */
	public String iniciar() throws DAOException {
		TurmaVirtualMBean mbean = getMBean("turmaVirtual");
		discentesTurma = (List<MatriculaComponente>) mbean.getDiscentesTurma();
		
		return forward("/ava/SorteioParticipantes/sortear_participantes.jsp");
	}
	
	/**
	 * Sorteia um aluno entre os participantes da turma
	 * 
	 * @throws DAOException
	 */
	public void sortearDiscentes() throws DAOException {		
		TurmaVirtualMBean mbean = getMBean("turmaVirtual");
		discentesTurma = (List<MatriculaComponente>) mbean.getDiscentesTurma();
		
		int numeroSorteado = (int) (Math.random() * discentesTurma.size());
		discenteSorteado = discentesTurma.get(numeroSorteado);
	}
	
	public MatriculaComponente getDiscenteSorteado() {
		return discenteSorteado;
	}

	public void setDiscenteSorteado(MatriculaComponente discenteSorteado) {
		this.discenteSorteado = discenteSorteado;
	}

	public Collection<MatriculaComponente> getDiscentesTurma() {
		return discentesTurma;
	}

	public void setDiscentesTurma(List<MatriculaComponente> discentesTurma) {
		this.discentesTurma = discentesTurma;
	}
}