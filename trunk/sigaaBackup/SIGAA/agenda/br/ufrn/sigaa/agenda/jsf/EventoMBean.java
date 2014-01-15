package br.ufrn.sigaa.agenda.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.agenda.dominio.Agenda;
import br.ufrn.sigaa.agenda.dominio.Evento;
import br.ufrn.sigaa.agenda.dominio.RecorrenciaEvento;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

import com.google.ical.values.Frequency;
import com.google.ical.values.RRule;

@Component("eventoBean") @Scope("request")
public class EventoMBean extends SigaaAbstractController<Evento> {

	private Date horarioInicio, horarioFim;
	private boolean recorrente;
	
	private Date repetirAte;
	
	public EventoMBean() {
		setObj(new Evento());
		obj.setRecorrencia( new RecorrenciaEvento() );
		obj.getRecorrencia().setRRule( new RRule() );
	}
	
	public String iniciar() {
		return forward("/agenda/evento/form.jsf");
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		popularEvento();
		getAgenda().addEvento(obj);
		return forwardCadastrar();
	}
	
	@Override
	public String forwardCadastrar() {
		return retornarParaAgenda();
	}

	private String retornarParaAgenda() {
		return getAgendaBean().addAgenda(getAgenda()).visualizar();
	}
	
	private void popularEvento() {
		if ( !obj.isDiaTodo() ) {
			Date inicio = CalendarUtils.definirHorario( obj.getDataInicio(), horarioInicio );
			obj.setDataInicio(inicio);
			
			Date fim = CalendarUtils.definirHorario( obj.getDataFim(), horarioFim );
			obj.setDataFim(fim);
		}
		
		if (recorrente && repetirAte != null) {
			obj.getRecorrencia().setRepetirAte(repetirAte);
		} else {
			obj.setRecorrencia(null);
		}
		
	}

	@Override
	public String cancelar() {
		return retornarParaAgenda();
	}
	
	public Collection<SelectItem> getTiposFrequencia() {
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add( new SelectItem( Frequency.DAILY , "DIARIAMENTE") );
		itens.add( new SelectItem( Frequency.WEEKLY , "SEMANALMENTE") );
		itens.add( new SelectItem( Frequency.MONTHLY , "MENSALMENTE") );
		itens.add( new SelectItem( Frequency.YEARLY , "ANUALMENTE") );
		return itens;
	}
	
	public Agenda getAgenda() {
		return getObj().getAgenda();
	}
	
	public void setAgenda(Agenda agenda) {
		getObj().setAgenda(agenda);
	}

	public AgendaMBean getAgendaBean() {
		return getMBean("agendaBean");
	}

	public boolean isRecorrente() {
		return recorrente;
	}

	public void setRecorrente(boolean recorrente) {
		this.recorrente = recorrente;
	}

	public Date getHorarioInicio() {
		return horarioInicio;
	}

	public void setHorarioInicio(Date horarioInicio) {
		this.horarioInicio = horarioInicio;
	}

	public Date getHorarioFim() {
		return horarioFim;
	}

	public void setHorarioFim(Date horarioFim) {
		this.horarioFim = horarioFim;
	}

	public Date getRepetirAte() {
		return repetirAte;
	}

	public void setRepetirAte(Date repetirAte) {
		this.repetirAte = repetirAte;
	}
	
}
