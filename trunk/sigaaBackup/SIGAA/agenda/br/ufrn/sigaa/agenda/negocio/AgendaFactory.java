package br.ufrn.sigaa.agenda.negocio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.agenda.dominio.Agenda;
import br.ufrn.sigaa.agenda.dominio.Evento;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.HorarioDocente;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

import com.google.ical.values.Frequency;
import com.google.ical.values.Weekday;
import com.google.ical.values.WeekdayNum;

/**
 * Classe responsável pela criação de agendas a partir de outras entidades, tais como turmas.
 * 
 * @author wendell
 *
 */
public class AgendaFactory {
	
	protected static AgendaFactory instance = new AgendaFactory();
	
	private AgendaFactory() {
	}
	
	public static AgendaFactory getInstance() {
		return instance;
	}
	
	/**
	 * Cria uma coleção de agendas para uma coleção de turmas (uma para cada)
	 * 
	 * @param turmas
	 * @return
	 */
	public Collection<Agenda> createFrom(Collection<Turma> turmas) {
		Collection<Agenda> agendas = new ArrayList<Agenda>();
		for (Turma turma : turmas) {
			agendas.add( createFrom(turma) );
		}
		return agendas;
	}

	/**
	 * Cria uma agenda e seus eventos a partir de uma turma e seus horários.
	 * 
	 * @param turma
	 * @return
	 */
	public Agenda createFrom(Turma turma) {
		Agenda agenda = new Agenda();
		agenda.setId(turma.getId());

		Evento evento = null;
		Date horaFimAnterior = null;
		// ordena a lista de horários de acordo com a dataInicio, dia e hora afim de poder agrupar os horários em um único evento
		Collections.sort(turma.getHorarios());
		for (HorarioTurma horarioTurma : turma.getHorarios()) {
			if(horarioTurma.getDataInicio() != null){
				Date horarioFim = CalendarUtils.definirHorario(horarioTurma.getDataInicio(), horarioTurma.getHoraFim());
				// Verificar se os horários são adjacentes, unindo-os se for o caso
				if (horaFimAnterior == null || !horaFimAnterior.equals(horarioTurma.getHoraInicio())) {
					evento = agenda.createEvento();
					evento.setTitulo( turma.getDescricaoResumida());
					evento.setDataInicio(CalendarUtils.definirHorario(horarioTurma.getDataInicio(), horarioTurma.getHoraInicio()));
					evento.setDataFim(horarioFim);
					evento.createRecorrencia()
						.configuraFrequencia(Frequency.WEEKLY)
						.configuraRepetirAte(horarioTurma.getDataFim())
						.setDiasSemana( Arrays.asList(getWeekDay(horarioTurma.getDia())) );
					evento.getRecorrencia().setOcorreTodosOsDias(false);
				} else {
					if (evento == null)
						evento = agenda.createEvento();
					evento.setDataFim(horarioFim);
				}
				horaFimAnterior = horarioTurma.getHoraFim();
			}
		}
		return agenda;
	}
	
	/**
	 * Cria uma coleção de agendas e seus eventos a partir de uma lista de horários do docente de uma turma.
	 * 
	 * @param turmas
	 * @return
	 */
	public Collection<Agenda> createFromHorarioDocente(Collection<Turma> turmas, DocenteTurma docenteTurma) {
		Collection<Agenda> agendas = new ArrayList<Agenda>();
		for (Turma turma : turmas) {
			agendas.add(createFromHorarioDocente(turma, docenteTurma));
		}
		return agendas;
	}
	
	/**
	 * Cria uma agenda e seus eventos a partir de uma lista de horários do docente de uma turma.
	 * 
	 * @param turma
	 * @return
	 */
	public Agenda createFromHorarioDocente(Turma turma, DocenteTurma docenteTurma) {
		Agenda agenda = new Agenda();
		Evento evento = null;
		Date horaFimAnterior = null;
		// ordena a lista de horários de acordo com a dataInicio, dia e hora afim de poder agrupar os horários em um único evento
		List<HorarioDocente> horariosDocente = new ArrayList<HorarioDocente>();
		for (DocenteTurma dt : turma.getDocentesTurmas()) {
			if (dt.getId() == docenteTurma.getId())
				horariosDocente.addAll(dt.getHorarios());
		}
		Collections.sort(horariosDocente);
		for (HorarioDocente horarioDocente : horariosDocente) {
			if(horarioDocente.getDataInicio() != null){
				Date horarioFim = CalendarUtils.definirHorario(horarioDocente.getDataInicio(), horarioDocente.getHoraFim());
				// Verificar se os horários são adjacentes, unindo-os se for o caso
				if (horaFimAnterior == null || !horaFimAnterior.equals(horarioDocente.getHoraInicio())) {
					evento = agenda.createEvento();
					evento.setTitulo( turma.getDescricaoResumida());
					evento.setDescricao("Local: " + turma.getLocal());
					evento.setDataInicio(CalendarUtils.definirHorario(horarioDocente.getDataInicio(), horarioDocente.getHoraInicio()));
					evento.setDataFim(horarioFim);
					evento.createRecorrencia()
						.configuraFrequencia(Frequency.WEEKLY)
						.configuraRepetirAte(horarioDocente.getDataFim())
						.setDiasSemana( Arrays.asList(getWeekDay(horarioDocente.getDia())) );
					evento.getRecorrencia().setOcorreTodosOsDias(false);
				} else {
					if (evento == null)
						evento = agenda.createEvento();
					evento.setDataFim(horarioFim);
				}
				horaFimAnterior = horarioDocente.getHoraFim();
			}
		}
		return agenda;
	}
	
	/**
	 * Transforma um carater representando o dia (2 sendo segunda, 3 sendo terça, etc.) para um objeto WeekdayNum
	 * 
	 * @param dia
	 * @return
	 */
	private static WeekdayNum getWeekDay(char dia) {
		Weekday weekday = null;
		switch (dia) {
			case '1': weekday = Weekday.SU; break;
			case '2': weekday = Weekday.MO; break;
			case '3': weekday = Weekday.TU; break;
			case '4': weekday = Weekday.WE; break;
			case '5': weekday = Weekday.TH; break;
			case '6': weekday = Weekday.FR; break;
			case '7': weekday = Weekday.SA; break;
		}
		return new WeekdayNum(0, weekday);
	}
}
