package br.ufrn.sigaa.monitoria.negocio;

import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.monitoria.CalendarioMonitoriaDao;
import br.ufrn.sigaa.monitoria.dominio.CalendarioMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;

public class CalendarioMonitoriaHelper {

	public static boolean isPeriodoSubmissaoSID(ProjetoEnsino projeto) throws DAOException {
		CalendarioMonitoriaDao dao = getDAO(CalendarioMonitoriaDao.class);
		try {
			Collection<CalendarioMonitoria> calendarios = dao.findCalendariosAtivosMonitoria();
			for (CalendarioMonitoria calendarioMonitoria : calendarios) {
				if ( projeto.getAno() == calendarioMonitoria.getAnoProjetoResumoSid() )
					return CalendarUtils.isDentroPeriodoAberto(
							calendarioMonitoria.getInicioEnvioResumoSid(), calendarioMonitoria.getFimEnvioResumoSid());
			}
		} finally {
			dao.close();
		}
		
		return false;
	}

	/**
	 * Retorna o DAO da classe informada
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}
	
}