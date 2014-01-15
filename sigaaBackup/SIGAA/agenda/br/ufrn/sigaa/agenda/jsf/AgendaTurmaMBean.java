/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/09/2010
 *
 */
package br.ufrn.sigaa.agenda.jsf;

import java.util.Collection;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.Turma;

/** Controller que auxilia a exibição da agenda de horários de uma turma, ou de uma lista de turmas.
 * @author Édipo Elder F. Melo
 *
 */
@Component("agendaTurmaBean") 
@Scope("session")
public class AgendaTurmaMBean extends SigaaAbstractController<Turma> {

	private Collection<Turma> turmasAbertas;
	private Turma turmaSelecionada;
	
	public AgendaTurmaMBean() {
		obj = new Turma();
	}
	
	public Turma getTurmaSelecionada() {
		if (turmasAbertas != null) {
			int id = getParameterInt("idTurma", 0);
			for (Turma turma : turmasAbertas) {
				if (turma.getId() == id) {
					turmaSelecionada = turma;
					break;
				}
			}
		}
		return turmaSelecionada;
	}

	public AgendaDataModel getAgendaModelTurmaSelecionada() {
//		if (turmaSelecionada != null)
//			return turmaSelecionada.getAgendaModel();
//		else 
			return null;
	}

	public Collection<Turma> getTurmasAbertas() {
		return turmasAbertas;
	}

	public void setTurmasAbertas(Collection<Turma> turmasAbertas) {
		this.turmasAbertas = turmasAbertas;
	}

	public void setTurmaSelecionada(ActionEvent evt) throws DAOException {
		int id = getParameterInt("idTurma", 0);
		this.turmaSelecionada = getGenericDAO().refresh(new Turma(id));
	}
	
	public void setTurmaSelecionada() throws DAOException {
		int id = getParameterInt("idTurma", 0);
		this.turmaSelecionada = getGenericDAO().refresh(new Turma(id));
	}
}
