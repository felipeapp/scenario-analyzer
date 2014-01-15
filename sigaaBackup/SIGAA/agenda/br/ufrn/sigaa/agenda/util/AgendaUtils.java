package br.ufrn.sigaa.agenda.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.agenda.dominio.Evento;
import br.ufrn.sigaa.arq.dao.agenda.EventoDao;

import com.google.ical.values.DateValue;
import com.google.ical.values.DateValueImpl;

public class AgendaUtils {

	
	
	public static DateValue toDateValue(Date data) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		// em java o mês começa com 0
		return new DateValueImpl(c.get(Calendar.YEAR), (c.get(Calendar.MONTH)+1), c.get(Calendar.DAY_OF_MONTH));
	}
	
	
	
	/**
	 *   Carrega do banco os eventos paginados da agenda.
	 *
	 * @param idAgenda
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 * @throws DAOException 
	 * @throws ParseException 
	 */
	public static List<Evento> carregaEventosPersistidos(final int idAgenda, final Date dataInicio, final Date dataFim, final Short statusEventosCarregaveis) throws DAOException, ParseException{
		EventoDao dao = null;
		
		List<Evento> temp = new ArrayList<Evento>();
		
		try {
			
			dao = DAOFactory.getInstance().getDAO(EventoDao.class);
			temp = dao.findEventosDaAgendaNoPeriodo(idAgenda, dataInicio, dataFim, statusEventosCarregaveis);
		
		}finally{
			if (dao != null) dao.close();
		}
		
		return temp;
		
	}
	
	
}

