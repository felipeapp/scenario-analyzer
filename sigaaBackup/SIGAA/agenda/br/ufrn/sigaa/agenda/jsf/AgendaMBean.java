/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * ManagedBean respons�vel pela manipula��o e exibi��o de agendas de eventos.
 * 
 * @author wendell
 *
 */
@Component("agendaBean") 
@Scope("session")
public class AgendaMBean extends SigaaAbstractController<Agenda> {

	/** Modelo de agenda utilizado na visualiza��o dos eventos da turma. */
	private AgendaDataModel model;
	/** Cole��o de turmas abertas para o usu�rio. */
	private Collection<Turma> turmasAbertas;
	/** Turma selecionada pelo usu�rio para visualiza��o na agenda. */
	private Turma turmaSelecionada;
	
	/** Construtor padr�o. */
	public AgendaMBean() {
		clear();
	}

	/** Redireciona o usu�rio para a visualiza��o da agenda da turma.<br/>M�todo n�o invocado por JSP�s.
	 * 
	 * @return
	 */
	public String visualizar() {
		return forward("/agenda/agenda.jsf");
	}
	
	/** Adiciona uma agenda ao modelo de agenda.<br/>M�todo n�o invocado por JSP�s.
	 * @param agenda
	 * @return
	 */
	public AgendaMBean addAgenda(Agenda agenda) {
		//model.addAgenda(agenda);
		return this;
	}
	
	/** Adiciona uma cole��o de  agenda ao modelo de agenda.<br/>M�todo n�o invocado por JSP�s.
	 * @param agendas
	 * @return
	 */
	public AgendaMBean addAgendas(Collection<Agenda> agendas) {
		//model.addAgendas(agendas);
		return this;
	}
	
	/** Reseta os atributos deste controller.<br/>M�todo n�o invocado por JSP�s.
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
	
	/** Retorna o modelo de agenda utilizado na visualiza��o dos eventos da turma. 
	 * @return
	 */
	public AgendaDataModel getModel() {
		return model;
	}

	/** Seta o modelo de agenda utilizado na visualiza��o dos eventos da turma.
	 * @param model
	 */
	public void setModel(AgendaDataModel model) {
		this.model = model;
	}
	
	/** Retorna a turma selecionada pelo usu�rio para visualiza��o na agenda. 
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

	/** Retorna a cole��o de turmas abertas para o usu�rio. 
	 * @return
	 */
	public Collection<Turma> getTurmasAbertas() {
		return turmasAbertas;
	}

	/** Seta a cole��o de turmas abertas para o usu�rio.
	 * @param turmasAbertas
	 */
	public void setTurmasAbertas(Collection<Turma> turmasAbertas) {
		this.turmasAbertas = turmasAbertas;
	}

}
