/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 09/08/2010
 *
 */
package br.ufrn.sigaa.agenda.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.agenda.dominio.Agenda;
import br.ufrn.sigaa.agenda.negocio.AgendaFactory;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * ManagedBean responsável pela manipulação e exibição de agendas de eventos.
 * 
 * @author wendell
 *
 */
@Component("agendaBean") 
@Scope("session")
public class AgendaMBean extends SigaaAbstractController<Agenda> {

	/** Modelo de agenda utilizado na visualização dos eventos da turma. */
	private AgendaDataModel model;
	/** Coleção de turmas abertas para o usuário. */
	private Collection<Turma> turmasAbertas;
	/** Turma selecionada pelo usuário para visualização na agenda. */
	private Turma turmaSelecionada;
	
	/** Construtor padrão. */
	public AgendaMBean() {
		clear();
	}

	/** Redireciona o usuário para a visualização da agenda da turma.<br/>Método não invocado por JSP´s.
	 * 
	 * @return
	 */
	public String visualizar() {
		return forward("/agenda/agenda.jsf");
	}
	
	/** Adiciona uma agenda ao modelo de agenda.<br/>Método não invocado por JSP´s.
	 * @param agenda
	 * @return
	 */
	public AgendaMBean addAgenda(Agenda agenda) {
		//model.addAgenda(agenda);
		return this;
	}
	
	/** Adiciona uma coleção de  agenda ao modelo de agenda.<br/>Método não invocado por JSP´s.
	 * @param agendas
	 * @return
	 */
	public AgendaMBean addAgendas(Collection<Agenda> agendas) {
		//model.addAgendas(agendas);
		return this;
	}
	
	/** Reseta os atributos deste controller.<br/>Método não invocado por JSP´s.
	 * @return
	 */
	public AgendaMBean clear() {
		if (model == null) {
			setModel(new AgendaDataModel());
		} else {
			model.clear();
		}
		return this;
	}
	
	/** Retorna o modelo de agenda utilizado na visualização dos eventos da turma. 
	 * @return
	 */
	public AgendaDataModel getModel() {
		return model;
	}

	/** Seta o modelo de agenda utilizado na visualização dos eventos da turma.
	 * @param model
	 */
	public void setModel(AgendaDataModel model) {
		this.model = model;
	}
	
	/** Retorna a turma selecionada pelo usuário para visualização na agenda. 
	 * @return
	 */
	public Turma getTurmaSelecionada() {
		turmaSelecionada = null;
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

	/** Retorna o modelo de agenda da turma selecionada.
	 * @return
	 */
	public AgendaDataModel getAgendaModelTurmaSelecionada() {
		if (turmaSelecionada != null)
			return new AgendaDataModel(AgendaFactory.getInstance().createFrom(turmaSelecionada));
		else return null;
	}

	/** Retorna a coleção de turmas abertas para o usuário. 
	 * @return
	 */
	public Collection<Turma> getTurmasAbertas() {
		return turmasAbertas;
	}

	/** Seta a coleção de turmas abertas para o usuário.
	 * @param turmasAbertas
	 */
	public void setTurmasAbertas(Collection<Turma> turmasAbertas) {
		this.turmasAbertas = turmasAbertas;
	}

}
