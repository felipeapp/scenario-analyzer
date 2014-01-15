/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/26
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.ead.dominio.HorarioTutor;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;

/**
 * Processador para cadastro o horário de atendimento dos tutores
 * de ensino a distancia.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorCadastroHorarioTutor extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		TutorOrientador tutor = (TutorOrientador) cMov.getObjMovimentado();
		TutorOrientadorDao tDao = getDAO(TutorOrientadorDao.class, mov);
		GenericDAO dao = getGenericDAO(mov);
		
		List<HorarioTutor> horarios = tDao.findHorariosByTutor(tutor);
		if (horarios != null && !horarios.isEmpty()) {
			horarios.removeAll(tutor.getHorarios());
			for (HorarioTutor horario : horarios) {
				dao.remove(horario);
			}
		}
		
		for (HorarioTutor horario : tutor.getHorarios()) {
			if (horario.getId() == 0)
				dao.create(horario);
			else
				dao.update(horario);
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
