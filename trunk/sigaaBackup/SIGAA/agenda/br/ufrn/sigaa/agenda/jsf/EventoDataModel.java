package br.ufrn.sigaa.agenda.jsf;

import org.primefaces.model.DefaultScheduleEvent;

import br.ufrn.sigaa.agenda.dominio.Evento;

/**
 * Modelo de dados utilizado para transformar os dados de um Evento nos dados utilzados para a exibição das informações
 * dentro do componende JSF <p:schedule> para a agenda .
 * 
 * @author wendell
 *
 */
public class EventoDataModel extends DefaultScheduleEvent {

	private Evento evento;
	
	public EventoDataModel(Evento evento) {
		super(evento.getTitulo(), evento.getDataInicio(), evento.getDataFim());
		setAllDay(evento.isDiaTodo());
		
		this.evento = evento;
		
//		if (evento.getAgenda() != null) {
//			//setStyleClass("fc-event-" + (evento.getAgenda().getId() % 3));
//		}
	}
	
	public EventoDataModel(Evento evento, String estilo) {
		this(evento);
		setStyleClass(estilo);
	}

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}
	
	
	
	
}
